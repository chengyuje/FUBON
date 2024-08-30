/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS113Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS113Controller";

	$scope.cleanList = function() {
		$scope.totals = {};
		$scope.qryList = [];
		$scope.rep_qryList = [];
	}
	
	/***XML參數***/
    getParameter.XML(['PMS.PIPELINE_PRD'
    ], (totas) => {
        if (totas) {
            $scope.mappingSet['PMS.PIPELINE_PRD'] = totas.data[totas.key.indexOf('PMS.PIPELINE_PRD')];					// 預計承作商品(大項)
        }
    });
    
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
		$scope.qryList = [];
		$scope.rep_qryList = [];
		
		$scope.inputVO = {
    	};
		
		$scope.getSDateList();
	};
	$scope.init();
	
	$scope.query = function(type) {
		// 欄位檢核錯誤：*為必要輸入欄位，請輸入後重試
		if ($scope.inputVO.sDate == undefined || $scope.inputVO.sDate == null || $scope.inputVO.sDate == '' || 
			$scope.inputVO.estPrd == undefined || $scope.inputVO.estPrd == null || $scope.inputVO.estPrd == '') {
			$scope.showErrorMsg("ehl_01_common_022");
			return;
		}
		
		$scope.totals = {};
		$scope.execStatisticsList = [];
		$scope.sendRecv("PMS113", "getList", "com.systex.jbranch.app.server.fps.pms113.PMS113InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
				$scope.qryList = tota[0].body.qryList;
				$scope.rep_qryList = tota[0].body.rep_qryList;

				$scope.outputVO_T1 = tota[0].body;
			}
		});
				
	};
	
	$scope.export = function(type) {
		$scope.inputVO.rep_qryList = $scope.rep_qryList;

		$scope.sendRecv("PMS113", "export", "com.systex.jbranch.app.server.fps.pms113.PMS113InputVO", $scope.inputVO,
				function(totas, isError) {
                	if (isError) {
                		$scope.showErrorMsg(totas[0].body.msgData);
                	}
				}
		);
	};
});
