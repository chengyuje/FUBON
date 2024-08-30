'use strict';
eSoafApp.controller('PMS716_insureDetailController', function($rootScope, $scope,
		$controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS716_insureDetailController";
	/** 查詢 * */
	$scope.query = function() {
		$scope.inputVO.date_year = $scope.date_year;
			$scope.sendRecv("PMS716","queryInsureDetail","com.systex.jbranch.app.server.fps.pms716.PMS716InputVO",$scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							return;
						}else{
							$scope.showMsg("ehl_01_common_024");		//執行失敗
						}
					});
		
		
	}
	$scope.query();
	$scope.uploadInsureDetail = function() {
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS716/PMS716_UPLOAD.html',
			className: 'PMS716_UPLOAD',
			showClose : true,
    		controller: ['$scope', function($scope) {
				$scope.workFlow = 'addInsureDetail';
            }]
		});
		dialog.closePromise.then(function(data) {
    		if(data.value == 'cancel'){
    			 $scope.query();
				}
    	});
	}
	
	/**download sample files**/
	$scope.downloadSample = function() {
    	$scope.sendRecv("PMS716", "downloadSample", "com.systex.jbranch.app.server.fps.pms716.PMS716InputVO", {"downloadSample":"insureDetail"},
				function(tota, isError) {
					if (!isError) {
						return;
					}
		});
	};
	
	
});