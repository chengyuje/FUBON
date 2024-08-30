'use strict';
eSoafApp.controller('PRD210_MANTAINController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD210_MANTAINController";
		
		$scope.checkTragetID = function() {
			if($scope.inputVO.target_id) {
				$scope.sendRecv("PRD210", "checkTragetID", "com.systex.jbranch.app.server.fps.prd210.PRD210InputVO", {'target_id':$scope.inputVO.target_id},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.inputVO.target_id = '';
								$scope.showErrorMsg("標的代號輸入錯誤或已存在。");
	                			return;
	                		} else {
	                			$scope.inputVO.linked_name = tota[0].body.resultList[0].LINKED_NAME;
	                		}
						}
				});
			}
		};
		
		$scope.init = function() {
			if($scope.row){
				$scope.row = $scope.row || {};
				$scope.inputVO = {
						target_id: $scope.row.TARGET_ID,
						linked_name: $scope.row.LINKED_NAME,
						stock_bond_type: $scope.row.STOCK_BOND_TYPE
				};				
			}
		};
		$scope.init();
		
		$scope.save = function() {
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			$scope.sendRecv("PRD210", "saveTraget", "com.systex.jbranch.app.server.fps.prd210.PRD210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.showSuccessMsg('ehl_01_common_004');		//處理完成
	                		$scope.closeThisDialog('successful');
						}
				});
		};
		
		
		
});