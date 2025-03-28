eSoafApp.controller("BannerController", ['$rootScope', '$scope', '$controller', '$http', '$cookies', 'socketService', 'sysInfoService', 'projInfoService', '$confirm', 'ngDialog', '$window', 'getParameter' ,
	function($rootScope, $scope, $controller, $http, $cookies, socketService, sysInfoService, projInfoService, $confirm, ngDialog , $window, getParameter){	
			$controller('BaseController', {$scope: $scope});
			
			$scope.IsMobile = projInfoService.getLoginSourceToken() == 'mobile';
			$scope.role = sysInfoService.getPriID()[0];
			$scope.canQuick = true;
			$scope.sendRecv("CRM911", "checkQuick", "com.systex.jbranch.app.server.fps.crm911.CRM911InputVO", {},
				function(tota, isError) {
					if (!isError) {
						angular.forEach(tota[0].body.resultList, function(row) {
							if(row.PARAM_CODE == $scope.role)
								$scope.canQuick = false
	        			});
					}
					
					// 讀取參數檔
					getParameter.XML(['SYS.AAS_SSO_ACC_PW', 'SYS.FMA_SSO_ACC_PW'],function(tota){
						if(tota){
							var AAS_SSO_ACC_PW = tota.data[tota.key.indexOf('SYS.AAS_SSO_ACC_PW')];
							$scope.AAS_SSO_ACC_PW_ENABLED = false;
							angular.forEach(AAS_SSO_ACC_PW, function(row, index) {
								if(row.DATA == 'ENABLED') 
									$scope.AAS_SSO_ACC_PW_ENABLED = row.LABEL == 'Y' 
							});
							
							//#2237 財管行銷助理SSO
							$scope.FMA_SSO_ACC_PW = tota.data[tota.key.indexOf('SYS.FMA_SSO_ACC_PW')];
							debugger;
							$scope.FMA_SSO_ACC_PW_ENABLED = false;
							angular.forEach($scope.FMA_SSO_ACC_PW, function(row, index) {
								if(row.DATA == 'ENABLED') 
									$scope.FMA_SSO_ACC_PW_ENABLED = row.LABEL == 'Y' 
							});
						}
					});
					
			});
			
			$scope.reload = function() {
				//send alert with unload event.
				$scope.$emit("e-unload-alert", 'MobilePlatform');
			}
			
			$scope.switchRole = function() {
				var list = $scope.userList;
				var dialog = ngDialog.open({
	        		template: 'assets/txn/CMFPG000/CMFPG000_AGENT.html',
	        		className: 'CMFPG000',
	        		showClose: false,
	        		controller: ['$scope', function($scope) {
	        			$scope.userList = list;
	        		}]
	        	});
				dialog.closePromise.then(function (data) {
	        		if(data.value === 'successful'){
	        			$scope.setSysInfoService(projInfoService.LoginInfo);
	        		}
	        	});
			};
			
			//下單
			$scope.custExist = 'N';
			$scope.order = function() {
				if ($scope.inputVO.crm110Type == 'ID') {
					$scope.inputVO.cust_id = $scope.inputVO.cust.toUpperCase();			
				}
				$scope.inputVO.orderOption = undefined;							
				$scope.custExist = 'Y';
			}
			
			$scope.change = function(){
				$scope.custExist = 'N';
			}
			
			$scope.selectOption = function(){
				//下單交易整合 TODO
				var pageArray = ['SOT110','SOT111','SOT130','SOT140','SOT150','SOT120','SOT121','SOT210','SOT211','SOT220','SOT221','SOT310','SOT320','SOT410','SOT420','SOT510','SOT520'];
				var isMod=false;
				for(var i=0;i<pageArray.length;i++){
					if($scope.inputVO.orderOption==pageArray[i]){
						isMod=true;
						break;
					}
				}
				if(isMod){
					$scope.connector('set','SOTCustID', $scope.inputVO.cust);
				}
				else{
					$scope.connector('set','ORG110_custID', $scope.inputVO.cust);
				}
				$rootScope.menuItemInfo.url = "assets/txn/" + $scope.inputVO.orderOption + "/" + $scope.inputVO.orderOption + ".html";					
			}
			
			$scope.loginOut = function() {
				$confirm({text: '確定 個金分行業務管理系統?'}, {size: 'sm'}).then(function() {
					$scope.sendRecv("CMFPG000", "tlroff", "com.systex.jbranch.app.server.fps.cmfpg000.LoginPageVO", {},
						function(tota, isError) {
							if (isError)
								$scope.msgData = tota.body.msgData;
							else {
								projInfoService.setApServerName(null);
								projInfoService.setUserList(null);
								projInfoService.setUserID(null);
								projInfoService.setUserName(null);
								projInfoService.setApplicationID(null);
								projInfoService.setBranchID(null);
								projInfoService.setBranchName(null);
								projInfoService.setWsID(null);
								projInfoService.setRoleID(null);
								projInfoService.setRoleName(null);
								projInfoService.setAvailBranch(null);
								projInfoService.setAvailRegion(null);
								projInfoService.setAvailArea(null);
								projInfoService.setUser(null);
								projInfoService.setAoCode(null);
								
								//send alert with unload event.
								$scope.$emit("e-unload-alert", 'Login');
							}
						}
					);
				});
			}
			
			$scope.chkToken = function($event){
				$event.preventDefault();
				
				//行動投保
				if($window.navigator.userAgent.match(/iPad;\s.*\sMac\s.*/i) != null || sysInfoService.getLoginSourceToken() == 'mobile'){
					$scope.sendRecv("SSO001" , "takeToken" , "java.util.HashMap" , {id : sysInfoService.getUserID()},
						function(tota, isError) {
							if (!isError) {
								if(window.webkit.messageHandlers.jumpMappMethod){
									window.webkit.messageHandlers.jumpMappMethod.postMessage(tota[0].body.MAPP_URL);
								} else {
									window.location = tota[0].body.MAPP_URL;
								}
								
								
								if(tota[0].body.errMsg != undefined){
									$scope.showErrorMsg(tota[0].body.errMsg);
								}
							}
						}
					);		
				}
				//保經代
				else{
					$scope.sendRecv("SSO001" , "doGeESoafWebUrl" , "java.util.HashMap" , {id : sysInfoService.getUserID()},
						function(tota, isError) {
							if (!isError) {
								window.open(tota[0].body.url);

								if(tota[0].body.errMsg != undefined){
									$scope.showErrorMsg(tota[0].body.errMsg);
								}
							}
						}
					);
				}			
				
			}
			
			//透過FORM表單進行SSO傳輸
			$scope.goMFA = function() {
				$scope.sendRecv("SSO003" , "takeToken" , "java.util.HashMap" , {id : sysInfoService.getUserID()} ,
		        		function(tota,isError){
			        		if (!isError) {
			        			if(tota[0].body.errMsg) {
			        				$scope.showErrorMsg(tota[0].body.errCode + ":" + tota[0].body.errMsg);
			        			} else {
			        				
			        				$scope.sendData = {
			        						encryptedEmp : tota[0].body.encryptData
			        				};

			        	            
			        	            var form = document.createElement("form");
			        	            form.method = "POST";
			        	            form.action = $scope.FMA_SSO_ACC_PW[0].LABEL;  //網址

			        	            var input1 = document.createElement("input"); //夾帶參數
			        	            input1.type = "hidden";
			        	            input1.name = "encryptedEmp";
			        	            input1.value = tota[0].body.encryptData;
			        	            form.appendChild(input1);

			        	           
			        	            form.target = "_blank";  // 新頁籤

			        	            document.body.appendChild(form);
                                    debugger;
			        	            form.submit();

			        	            document.body.removeChild(form);
			        			}
			        			return;
			        		}
		        	});
	        };
}]);