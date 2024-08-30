'use strict';
eSoafApp.controller('CRM371_CUSTController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM371_CUSTController";
		
		$scope.inputVO = {};
		$scope.inputVO.seq = $scope.row.TRS_SEQ;
		$scope.inputVO.temp_yn = $scope.row.CALL_REVIEW_NOTE;
		$scope.inputVO.call_review_type = $scope.row.CALL_REVIEW_STATUS;
		
		$scope.save = function() {
			$scope.sendRecv("CRM371", "save_cust", "com.systex.jbranch.app.server.fps.crm371.CRM371InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.showSuccessMsg('ehl_01_common_004');
                    		$scope.closeThisDialog('successful');
						}
			});
		};
		
		
		
});