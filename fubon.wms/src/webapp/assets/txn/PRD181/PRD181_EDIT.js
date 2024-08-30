/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD181_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD181_EDITController";
		$scope.btnSubmit = function(){	
			if($scope.inputVO.INSPRD_ID==null || $scope.inputVO.INSPRD_NAME==null){
				$scope.showErrorMsg('ehl_01_common_022');
				return;
			}
			
			if($scope.inputVO.INSPRD_ID.trim().length==0|| $scope.inputVO.INSPRD_NAME.trim().length==0){
				$scope.showErrorMsg('ehl_01_common_022');
				return;
			}
					$scope.sendRecv("PRD181","addData","com.systex.jbranch.app.server.fps.prd181.PRD181InputVO",
		        			$scope.inputVO,function(tota,isError){
	                	if (tota.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_001');
	                		$scope.closeThisDialog('successful');
	                	};
					});
		}
		
		
		
	}
);