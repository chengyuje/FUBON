/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM1243Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM1243Controller";
		$scope.homtype = '';
		// bra 已query 多寫的
//		$scope.mappingSet['branchsDesc'] = [];
//		angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
//			$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
//		});

		// ONSITE_PERIOD
		$scope.mappingSet['ONSITE_PERIOD'] = [];
		$scope.mappingSet['ONSITE_PERIOD'].push(
												{LABEL: '上午', DATA: '1'},
												{LABEL: '下午', DATA: '2'}
												);

		//CHG_REASON
		$scope.mappingSet['CHG_REASON'] = [];
		$scope.mappingSet['CHG_REASON'].push(
											{LABEL: '教育訓練', DATA: '1'},
											{LABEL: '客戶陪訪', DATA: '2'},
											{LABEL: '業務檢討', DATA: '3'},
											{LABEL: '其他', DATA: '4'}
											);
		
		$scope.inquireInit = function(){
			$scope.resultList = [];
		};
		$scope.inquireInit();
		
        // query
        $scope.inquire = function(){
			$scope.sendRecv("CRM1243", "inquire", "com.systex.jbranch.app.server.fps.crm1243.CRM1243InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
		            			return;
		            		}
							$scope.resultList = tota[0].body.resultList;
							return;
						}
					}
        )};
        $scope.inquire();
		
        $scope.reply = function(type) {
        	var list = $scope.resultList.filter(function(obj){
	    		return (obj.SELECTED == true);
	    	});
        	if(list.length == 0){
        		return;
        	}
        	
        	if (type == '1') {
        		$scope.sendRecv("CRM1243", "confirm", "com.systex.jbranch.app.server.fps.crm1243.CRM1243InputVO", {'list' : list},
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
        		$scope.sendRecv("CRM1243", "reject", "com.systex.jbranch.app.server.fps.crm1243.CRM1243InputVO", {'list' : list},
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
        
        $scope.$on("CRM1243.init", function(event) {
        	$scope.homtype = 'HOME';
		});
        
        
});
		