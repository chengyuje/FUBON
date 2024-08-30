/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CRM110_MultiDataController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM110_MultiDataController";
		$scope.userList = $scope.row;
		$scope.totaBody = $scope.totaBody;
		
		/** 抓出所有分行 **/
		$scope.mappingSet['branchsDesc_all'] = [];		
    	$scope.sendRecv("CRM210", "getAllBranch", "com.systex.jbranch.app.server.fps.crm210.CRM210InputVO", {}, 
    			function(totas, isError) {
	                if (totas.length > 0) {
	                	angular.forEach(totas[0].body.resultList, function(row, index, objs){
	            			$scope.mappingSet['branchsDesc_all'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
	            		});
	                };
	    });
		
		$scope.foucsButton = function() {
			$('#savebut').focus();
		};
		debugger
		$scope.inputVO = {
			select: {},
			cust_name: $scope.cust_name,
			role: $scope.role,
			ao_code: $scope.ao_code,
			priID: sysInfoService.getPriID()[0]
	    };
		
//		欄位排序用
		$scope.inquire = function(){
			$scope.sendRecv("CRM110", "inquire", "com.systex.jbranch.app.server.fps.crm110.CRM110InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.userList = tota[0].body.resultList;
							$scope.totaBody = tota[0].body;
						}
					}
			);
		};
		
	    $scope.goCRM610 = function(row){
	    	$scope.CRM_CUSTVO = {
					CUST_ID :  row.CUST_ID,
					CUST_NAME :row.CUST_NAME
			}
			$scope.connector('set','CRM_CUSTVO',$scope.CRM_CUSTVO);
	    	var path = "assets/txn/CRM610/CRM610_MAIN.html";
			$scope.connector("set","CRM610URL",path);
			
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM610/CRM610.html',
				className: 'CRM610',
				showClose: false
			});
	    };
		
		$scope.save = function() {			
			if($scope.inputVO.select.CUST_ID == undefined){
				$scope.showErrorMsgInDialog('請選擇角色');
			} else {
				var path = '';
				
				if ($scope.role == '004') {
					path = "assets/txn/CRM711/CRM711.html";
				}else {
					path = "assets/txn/CRM610/CRM610_MAIN.html";
				}

				$scope.connector('set','CRM110_CUST_ID', $scope.inputVO.select.CUST_ID);
				$scope.connector('set','CRM110_CUST_NAME', $scope.inputVO.select.CUST_NAME);
				$scope.connector('set','CRM110_AOCODE', $scope.inputVO.select.AO_CODE);
				
				
				$scope.CRM_CUSTVO = {
						CUST_ID :  $scope.inputVO.select.CUST_ID,
						CUST_NAME :$scope.inputVO.select.CUST_NAME						
					}
				$scope.connector('set','CRM_CUSTVO',$scope.CRM_CUSTVO);
				$scope.connector("set","CRM610URL",path);
//				$rootScope.menuItemInfo.url = "assets/txn/CRM610/CRM610.html";
				var dialog = ngDialog.open({
					template: 'assets/txn/CRM610/CRM610.html',
					className: 'CRM610',
					showClose: false
				});
				
				$scope.closeThisDialog('success');
				return;
			}
		}
		
		$scope.selectOption = function(row) {
			//下單交易整合 TODO
			var pageArray = ['SOT110','SOT111','SOT130','SOT140','SOT150','SOT120','SOT121','SOT210','SOT211','SOT220','SOT221','SOT310','SOT320','SOT410','SOT420','SOT510','SOT520'];
			var isMod=false;
			for(var i=0;i<pageArray.length;i++){
				if(row.OPTION==pageArray[i]){
					isMod=true;
					break;
				}
			}
			if(isMod){
				$scope.connector('set','SOTCustID',row.CUST_ID);
			}
			else{
				$scope.connector('set','ORG110_custID', row.CUST_ID );
			}

			$rootScope.menuItemInfo.url = "assets/txn/" + row.OPTION + "/" + row.OPTION + ".html";
			$scope.closeThisDialog('success');
		}
		
});
