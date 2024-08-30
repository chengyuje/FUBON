/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS301_QUERYController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "PMS301_QUERYController";

	$scope.paramList = [];
	$scope.totalList = [];
	$scope.inputVO = {};
	$scope.inputVO.sCreDate = $scope.sCreDate;
	$scope.inputVO.branch_nbr = $scope.branch_nbr;
	$scope.inputVO.srchType = $scope.srchType;
	$scope.inputVO.tx_ym = $scope.tx_ym;
	$scope.sendRecv("PMS301", "queryDetail", "com.systex.jbranch.app.server.fps.pms301.PMS301InputdtatilVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					if (tota[0].body.resultList.length == 0) {
						$scope.showMsg("ehl_01_common_009");
						return;
					}
					$scope.totalList = tota[0].body.resultList;
					$scope.outputVO = tota[0].body;
					console.log($scope.totalList);
					return;
				}
	});

});