/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM1242Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM1242Controller";
		

		$scope.inquireInit = function(){
			$scope.resultList = [];
			$scope.checkList = [];
		};
		$scope.inquireInit();
		
		// query
        $scope.inquire = function(){
			$scope.sendRecv("CRM1242", "inquire", "com.systex.jbranch.app.server.fps.crm1242.CRM1242InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.resultList = tota[0].body.resultList;
							//判斷FA/IA
							for(var i = 0; i < $scope.resultList.length; i++) {						
								if($scope.resultList[i].AS_TYPE == "F"){
									$scope.resultList[i].AS_FAIA ="FA";
								}else if($scope.resultList[i].AS_TYPE == "I"){
									$scope.resultList[i].AS_FAIA ="IA";
								}
							}
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
		            			return;
		            		}else{
		            			
		            			$scope.sendRecv("CRM1242", "check", "com.systex.jbranch.app.server.fps.crm1242.CRM1242InputVO", $scope.inputVO,
		            					function(totas, isError) {
		            				if(totas[0].body.resultList.length != 0) {
		            					$scope.checkList = totas[0].body.resultList;
		            					//檢查搜尋到的資料序號是否跟衝突的資料序號相同，以此檢查資料是否衝突
										for(var i = 0; i < $scope.resultList.length; i++) {														
											for(var j = 0; j < $scope.checkList.length; j++) {		
												if($scope.checkList[j].SEQ == $scope.resultList[i].SEQ){
													$scope.resultList[i].CHECK = "有衝突";
												}				
											}
										}
		            	    		}		
		            			});
								
		            		}
						}
					}
        )};
        $scope.inquire();
         
        //覆核
        $scope.reply = function(type) {
        	var list = $scope.resultList.filter(function(obj){
	    		return (obj.SELECTED == true);
	    	});
        	if(list.length == 0){
        		return;
        	}
        	
        	if (type == '1') {
        		$scope.sendRecv("CRM1242", "confirm", "com.systex.jbranch.app.server.fps.crm1242.CRM1242InputVO", {'list' : list},
    					function(tota, isError) {
    						if (isError) {
    							$scope.showErrorMsgInDialog(tota.body.msgData);
    							return;
    						}
    						
    						if (tota.length > 0) {
    							$scope.showMsg('覆核成功');
    							$scope.inquireInit();
    							$scope.inquire();
    						}
        				}
    			);

        	}else {
        		$scope.sendRecv("CRM1242", "reject", "com.systex.jbranch.app.server.fps.crm1242.CRM1242InputVO", {'list' : list},
    					function(tota, isError) {
    						if (isError) {
    							$scope.showErrorMsgInDialog(tota.body.msgData);
    							return;
    						}
    						if (tota.length > 0) {
    							$scope.showMsg('覆核退回');
    							$scope.inquireInit();
    							$scope.inquire();
    						}
        				}
    			);
        	}
        }
        
        $scope.conflict = function(row) {
        	var dialog = ngDialog.open({
				template: 'assets/txn/CRM1242/CRM1242_CONFLICT.html',
				className: 'CRM1242_CONFLICT',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
//        	dialog.closePromise.then(function (data) {
//				if(data.value === 'successful'){
//					$scope.inquire();
//				}
//			});		
		};        
});
		