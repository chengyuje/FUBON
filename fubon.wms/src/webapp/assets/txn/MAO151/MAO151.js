/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MAO151Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MAO151Controller";
		
		$scope.init = function() {
			$scope.inputVO = {
				emp_id : sysInfoService.getUserID(),
				role : sysInfoService.getRoleID()
			}
		}
		$scope.init();
		
		$scope.inquireInit = function() {
			$scope.resultList = [];
		}
		$scope.inquireInit();
		
		$scope.inquire = function() {
			var mgm113Count = $scope.connector('get','mgm113_flag');
//			var crm990Count = $scope.connector('get','crm990_flag');
			
			$scope.sendRecv("MAO151", "inquire", "com.systex.jbranch.app.server.fps.mao151.MAO151InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0 && mgm113Count == 0) {
							$scope.showMsg("ehl_01_common_009");
	                		return;
						}
						$scope.resultList = tota[0].body.resultList;
						
						if(mgm113Count > 0){
							$scope.resultList.push({'REVIEW_CATEGORY': 'MGM活動待覆核', 
													'REMIND_MSG': 'MGM活動待覆核案件數(' + mgm113Count + ')', 
													'NEXT_PROC_TYPE': 'MGM110'});
						}
						
//						alert(crm990Count);
//						if(crm990Count > 0){
//							$scope.resultList.push({'REVIEW_CATEGORY': '客訴管理待覆核', 
//													'REMIND_MSG': '客訴管理待覆核案件數(' + crm990Count + ')', 
//													'NEXT_PROC_TYPE': 'CRM990'});
//						}
						
						$scope.outputVO = tota[0].body;
						return;
					}
			});
		}
		$scope.inquire();
		
		//傳參數
        $scope.detail = function (row) {
        	switch(row.NEXT_PROC_TYPE) {
        		case 'QRY':
        			var properties = row.PASS_PARAMS.split(', ');
				    var obj = {};
				    properties.forEach(function(property) {
				    	var tup = property.split('=');
				    	obj[tup[0]] = tup[1];
				    });
				    
			    	
		        	$scope.connector('set','MAO151_PARAMS', obj);
		        	$scope.connector('set','MAO151_APPLIED_TIME', row.APPLIED_TIME);
		    		$rootScope.menuItemInfo.url = "assets/txn/" + row.NEXT_PG_URL;
			        break;
        		case 'DTL':
        			var properties = row.PASS_PARAMS.split(', ');
				    var obj = {};
				    properties.forEach(function(property) {
				    	var tup = property.split('=');
				    	obj[tup[0]] = tup[1];
				    });
				    
				    $scope.connector('set','MAO151_PARAMS', obj);
		    		$rootScope.menuItemInfo.url = "assets/txn/" + row.NEXT_PG_URL;
	
			        break;
        		case 'EJA':
        			break;
        		case 'EPR':
        			break;
        		case 'POP':
        			// 2017/4/28 add
        			var dialog = ngDialog.open({
        				template: 'assets/txn/CUS110/CUS110.html',
        				className: 'CUS110',
        				showClose: false,
                        controller: ['$scope', function($scope) {
                        	$scope.isConfirm = true;
                        	$scope.confirmSEQ = row.REVIEW_SEQ;
                        	$scope.confirmNAME = row.APPLIER_NAME;
                        	$scope.recipientType = "CUST";
                        }]
        			});
        			dialog.closePromise.then(function (data) {
        				if(data.value === 'successful') {
        					$scope.inquireInit();
        					$scope.inquire();			
        				}
        			});
        			break;
        		case 'MGM110':
        			$scope.GeneratePage({'txnName':'MGM110','txnId':'MGM110',
        				'txnPath':[{'MENU_ID':'HOME','MENU_NAME':'首頁'},
        				           {'MENU_ID':'MAO151','MENU_NAME':'待覆核清單'},
        				           {'MENU_ID':'MGM110','MENU_NAME':'MGM活動新增/查詢'}]});
        			break;
			}
        }
});
		