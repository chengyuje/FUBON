/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM1242_CONFLICTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM1242_CONFLICTController";
		
		
		//分行代號轉名稱
		$scope.mappingSet['branchsDesc'] = [];
		angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
			$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		});
				
		//查詢
		$scope.inquire = function() {
			$scope.inputVO = {
					seq : $scope.row.SEQ,
			}
			$scope.sendRecv("CRM1242", "check", "com.systex.jbranch.app.server.fps.crm1242.CRM1242InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.resultList = tota[0].body.resultList;
							if($scope.resultList.length == 0) {	
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                			
	                		}else{	
	                			//判斷FA/IA
								for(var i = 0; i < $scope.resultList.length; i++) {						
									if($scope.resultList[i].AS_TYPE == "F"){
										$scope.resultList[i].AS_FAIA ="FA";
									}else if($scope.resultList[i].AS_TYPE == "I"){
										$scope.resultList[i].AS_FAIA ="IA";
									}
								}
	                			$scope.resultList_conflict = tota[0].body.resultList;	
	                			return;
	                			
	                		}																
						}						
			});
	    }
		$scope.inquire();
		
        
        
});
		