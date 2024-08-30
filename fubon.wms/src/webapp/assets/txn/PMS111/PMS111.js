/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS111Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS111Controller";
	
	$scope.cleanList = function() {
		$scope.totals = {};
		$scope.execStatisticsList = [];
		$scope.noContentDtlList = [];
		$scope.reportLst = [];
	}
	
	$scope.getEDateList = function() {
		$scope.EDateList = [];
		
		if ($scope.inputVO.sDate != undefined && $scope.inputVO.sDate != null && $scope.inputVO.sDate != '') {
			$scope.sendRecv("PMS111", "getEDateList", "com.systex.jbranch.app.server.fps.pms111.PMS111InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
				if (tota.length > 0) {
					$scope.EDateList = tota[0].body.eDateList;
				}
			});
		}
	};
	
	$scope.getSDateList = function() {
		$scope.SDateList = [];
		$scope.EDateList = [];
		
		$scope.sendRecv("PMS111", "getSDateList", "com.systex.jbranch.app.server.fps.pms111.PMS111InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
				$scope.SDateList = tota[0].body.sDateList;
			}
		});
	};
	
	$scope.init = function() {
		$scope.totals = {};
		$scope.execStatisticsList = [];
		$scope.noContentDtlList = [];
		$scope.reportLst = [];
		
		$scope.inputVO = {
    	};
		
		$scope.getSDateList();
	};
	$scope.init();
	
	$scope.query = function(type) {
		// 欄位檢核錯誤：*為必要輸入欄位，請輸入後重試
		if ($scope.inputVO.sDate == undefined || $scope.inputVO.sDate == null || $scope.inputVO.sDate == '' || 
			$scope.inputVO.eDate == undefined || $scope.inputVO.eDate == null || $scope.inputVO.eDate == '' ) {
			$scope.showErrorMsg("ehl_01_common_022");
			return;
		}
		
		switch(type) {
			case "execStatistics":
				$scope.totals = {};
				$scope.execStatisticsList = [];
				$scope.sendRecv("PMS111", "getExecStatistics", "com.systex.jbranch.app.server.fps.pms111.PMS111InputVO", $scope.inputVO, function(tota, isError) {
					if (isError) {
						$scope.showErrorMsgInDialog(tota.body.msgData);
						return;
					}
					if (tota.length > 0) {
						$scope.execStatisticsList = tota[0].body.execStatisticsList;
						$scope.rep_execStatisticsList = tota[0].body.rep_execStatisticsList;
						$scope.outputVO_T1 = tota[0].body;
					}
				});
				
				break;
			case "noContentDtl":				
				$scope.totals = {};
				$scope.noContentDtlList = [];
				$scope.sendRecv("PMS111", "getNoContentDtl", "com.systex.jbranch.app.server.fps.pms111.PMS111InputVO", $scope.inputVO, function(tota, isError) {
					if (isError) {
						$scope.showErrorMsgInDialog(tota.body.msgData);
						return;
					}
					if (tota.length > 0) {
						$scope.noContentDtlList = tota[0].body.noContentDtlList;
						$scope.rep_noContentDtlList = tota[0].body.noContentDtlList;
						$scope.outputVO_T2 = tota[0].body;
					}
				});
				
				break;
		}
	};
	
	$scope.export = function(type) {
		switch(type) {
			case "execStatistics":
				$scope.inputVO.rep_execStatisticsList = $scope.rep_execStatisticsList;

				$scope.sendRecv("PMS111", "expExecStatistics", "com.systex.jbranch.app.server.fps.pms111.PMS111InputVO", $scope.inputVO,
						function(totas, isError) {
		                	if (isError) {
		                		$scope.showErrorMsg(totas[0].body.msgData);
		                	}
						}
				);
				break;
			case "noContentDtl":
				$scope.inputVO.rep_noContentDtlList = $scope.rep_noContentDtlList;
				
				$scope.sendRecv("PMS111", "expNoContentDtl", "com.systex.jbranch.app.server.fps.pms111.PMS111InputVO", $scope.inputVO,
						function(totas, isError) {
		                	if (isError) {
		                		$scope.showErrorMsg(totas[0].body.msgData);
		                	}
						}
				);
				break;
		}
	};
});
