/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM813Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "CRM813Controller";

	$scope.getPara = function() {
		getParameter.XML([ 'CRM.INT_TYPE', 'CRM.CBS.AUTO_TR_TYPE', 'CRM.CBS.INT_PAY_TYPE' ], function(totas) {
			if (len(totas) > 0) {
				$scope.mappingSet['CRM.INT_TYPE'] = totas.data[totas.key.indexOf('CRM.INT_TYPE')];
				$scope.mappingSet['CRM.CBS.AUTO_TR_TYPE'] = totas.data[totas.key.indexOf('CRM.CBS.AUTO_TR_TYPE')];
				$scope.mappingSet['CRM.CBS.INT_PAY_TYPE'] = totas.data[totas.key.indexOf('CRM.CBS.INT_PAY_TYPE')];
			}

		});
	};
	$scope.getPara();

	// 初始化
	$scope.init = function() {
		$scope.inputVO.cust_id = $scope.custVO.CUST_ID;
	};
	$scope.init();

	// 初始化
	$scope.inquireInit = function() {
		$scope.resultList = [];
		$scope.resultList2 = [];
	}
	$scope.inquireInit();

	// 查詢台幣定存
	$scope.inquire = function() {
		$scope.sendRecv("CRM813", "inquire", "com.systex.jbranch.app.server.fps.crm813.CRM813InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsg(totas[0].body.msgData);
			}
			if (tota[0].body.resultList == null || tota[0].body.resultList.length == 0) {
				// $scope.showMsg("ehl_01_common_009");
				return;
			}
			$scope.resultList = tota[0].body.resultList;
			$scope.outputVO = tota[0].body;

			return;
		})
	};
	$scope.inquire();

	// 查詢外幣定存
	$scope.inquire2 = function() {
		$scope.sendRecv("CRM813", "inquire2", "com.systex.jbranch.app.server.fps.crm813.CRM813InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsg(totas[0].body.msgData);
			}
			if (tota[0].body.resultList2 == null || tota[0].body.resultList2.length == 0) {
				// $scope.showMsg("ehl_01_common_009");
				return;
			}

			$scope.resultList2 = tota[0].body.resultList2;
			$scope.outputVO2 = tota[0].body;
			// $scope.ex_map = tota[0].body.ex_map;

			return;
		})
	};
	$scope.inquire2();

	// 檢查是否為125帳號
	$scope.checkINT_PAY_TYP = function(row) {
		if (row.INT_PAY_TYP == '1' || row.INT_PAY_TYP == '2') {
			if (row.ACNO.substr(3, 3) == '125') {
				return row.INT_PAY_TYP + '125';
			} else {
				return row.INT_PAY_TYP;
			}
		}
		return row.INT_PAY_TYP;
	}

	// 取前3碼
	$scope.getChar = function(row) {
		return row.substr(0, 3);
	}

	// 轉STR YYYYMMDD TO DATE
	$scope.transDate = function(row) {
		var pattern = /(\d{4})(\d{2})(\d{2})/;
		return new Date(row.replace(pattern, '$1-$2-$3'));
	}

	// 轉台幣
	$scope.exchange = function(row) {
		return Number(row.OPN_DPR_AMT) * $scope.ex_map[row.CUR];
	}

});
