'use strict';
eSoafApp.controller('PMS716_insureYeController', function($rootScope, $scope,
		$controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS716_insureYeController";
	//初始化
	$scope.init = function(){
		$scope.flag = 'detail';
	}
	$scope.init();
	
	/** 查詢 * */
	$scope.query = function() {
		$scope.inputVO.date_year = $scope.date_year;
		$scope.inputVO.flag = $scope.flag;
		$scope.sendRecv("PMS716","queryInsureYe","com.systex.jbranch.app.server.fps.pms716.PMS716InputVO",$scope.inputVO,
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
	$scope.uploadInsureYe = function() {
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS716/PMS716_UPLOAD.html',
			className: 'PMS716_UPLOAD',
			showClose : true,
    		controller: ['$scope', function($scope) {
				$scope.workFlow = 'addInsureYe';
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
    	$scope.sendRecv("PMS716", "downloadSample", "com.systex.jbranch.app.server.fps.pms716.PMS716InputVO", {"downloadSample":"insureYe"},
				function(tota, isError) {
					if (!isError) {
						return;
					}
		});
	};
	
	
});