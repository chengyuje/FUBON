/**================================================================================================
 @Description 分行人員與客戶資金往來異常報表_明細資料
 =================================================================================================**/
'use strict';
eSoafApp.controller('PMS422_DetailController', function($rootScope, $scope, $controller) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "PMS422_DetailController";

	/** 查詢明細 **/
	$scope.queryDetail = () => {
		$scope.sendRecv("PMS422", "queryDetail", 'com.systex.jbranch.app.server.fps.pms422.PMS422InputVO',
			$scope.inputVO, (tota, isError) => {
					if (!isError) {
						$scope.details = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;

						if (!$scope.details.length)
							$scope.showMsg("ehl_01_common_009");
					}
		});
	};
	$scope.queryDetail();

});