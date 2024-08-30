/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('KYC999Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, $q, getParameter, $filter, validateService, $timeout) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "KYC999Controller";

	// init
	$scope.init = function() {
		
		var date = new Date(), y = date.getFullYear(), m = date.getMonth();
		$scope.inputVO.dateStart = new Date(y, m, 1);
		$scope.inputVO.dateEnd = new Date(y, m + 1, 0);
		$scope.paramList = [];
		$scope.resultList = [];
		$scope.outputVO = {};
	}
	$scope.init();

	// 查詢
	$scope.inquire = function() {
		
		$scope.sendRecv("KYC999", "inquire", "com.systex.jbranch.app.server.fps.kyc999.KYC999InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				
				if (tota[0].body.resultList.length > 0) {
					$scope.resultList = tota[0].body.resultList;
					$scope.outputVO = tota[0].body;
				} else {
					$scope.showMsg("ehl_01_common_009");
				}
			} else {
				$scope.paramList = [];
				$scope.resultList = [];
				$scope.outputVO = {};
				$scope.showMsg("ehl_01_common_024");
			}
		});
	}
	
	//輸出CSV檔
	$scope.exportCSV = function() {
		
		$scope.sendRecv("KYC999", "export", "com.systex.jbranch.app.server.fps.kyc999.KYC999InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				
			} else {
				$scope.showMsg("ehl_01_common_024");
			}
		});
	}
	
	
	// date picker
	// 日期起訖
	$scope.sDateOptions = {};
	$scope.eDateOptions = {};
	var date = new Date(), y = date.getFullYear(), m = date.getMonth();
	
	
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	$scope.limitDate = function() {
		$scope.sDateOptions.maxDate = $scope.inputVO.dateEnd;
		$scope.eDateOptions.minDate = $scope.inputVO.dateStart;
	};
});