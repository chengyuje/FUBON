/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ELN100Controller', function($rootScope, $scope, $controller) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "ELN100Controller";
	
	// 修改日期
	$scope.maxDate = new Date();
	$scope.sDateOptions = {
    		maxDate: $scope.maxDate,
    		minDate: $scope.minDate
    };
    $scope.eDateOptions = {
    		maxDate: $scope.maxDate,
    		minDate: $scope.minDate
    };
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	$scope.limitDate = function() {
		$scope.sDateOptions.maxDate = $scope.inputVO.query_date_e || $scope.maxDate;
		$scope.eDateOptions.minDate = $scope.inputVO.query_date_s || $scope.minDate;
	};
	// 修改日期 END
	
	$scope.inquireInit = function(){
		$scope.resultList = [];
		$scope.outputVO = [];
		$scope.inputVO = {};
		
		// 詢價起迄日：預設為當日
		$scope.inputVO.query_date_s = new Date();
		$scope.inputVO.query_date_e = new Date();
		$scope.limitDate();
	}
	
	// 取得標的下拉
	$scope.getBBG = function(){
		$scope.sendRecv("ELN100", "getBBG", "com.systex.jbranch.app.server.fps.eln100.ELN100InputVO", {},
		function(tota, isError) {
			if (!isError) {
				$scope.mappingSet['ELN.BBG_CODE1'] = [];
				$scope.mappingSet['ELN.BBG_CODE2'] = [];
				$scope.mappingSet['ELN.BBG_CODE3'] = [];
				
				if (tota[0].body.resultList.length > 0) {
					angular.forEach(tota[0].body.resultList, function(row){
						$scope.mappingSet['ELN.BBG_CODE1'].push({LABEL: row.BBG_CODE, DATA: row.BBG_CODE});
						$scope.mappingSet['ELN.BBG_CODE2'].push({LABEL: row.BBG_CODE, DATA: row.BBG_CODE});
						$scope.mappingSet['ELN.BBG_CODE3'].push({LABEL: row.BBG_CODE, DATA: row.BBG_CODE});
					});
				}
			}
		});
	}
	
	$scope.init = function(){
		$scope.inquireInit();
		$scope.getBBG();
		
		// 計價幣別 :USD、AUD、JPY、EUR
		$scope.mappingSet['ELN.CURRENCY'] = [];
		$scope.mappingSet['ELN.CURRENCY'].push({LABEL:"USD", DATA: 'USD'},
											   {LABEL:"AUD", DATA: 'AUD'},
											   {LABEL:"JPY", DATA: 'JPY'},
									 	  	   {LABEL:"EUR", DATA: 'EUR'});
		
		// 承作天期 :1~12
		$scope.mappingSet['ELN.TENOR'] = [];
		for (var i = 1; i <= 12; i++) {
			$scope.mappingSet['ELN.TENOR'].push({LABEL: i+"" , DATA: i});
		}
		
		// KO 類型：Daily、Daily Memory、Period End、Period End Memory
		$scope.mappingSet['ELN.KO_TYPE'] = [];
		$scope.mappingSet['ELN.KO_TYPE'].push({LABEL:"Daily" 			, DATA: 'Daily'},
											  {LABEL:"Daily Memory" 	, DATA: 'Daily Memory'},
											  {LABEL:"Period End" 		, DATA: 'Period End'},
									 	  	  {LABEL:"Period End Memory", DATA: 'Period End Memory'});
		
		// KI 類型：None、EKI、AKI
		$scope.mappingSet['ELN.KI_TYPE'] = [];
		$scope.mappingSet['ELN.KI_TYPE'].push({LABEL:"None", DATA: 'None'},
											  {LABEL:"EKI" , DATA: 'EKI'},
											  {LABEL:"AKI" , DATA: 'AKI'});
	}

	$scope.inquire = function() {
		if ($scope.inputVO.query_date_s == undefined || $scope.inputVO.query_date_e == undefined) {
			$scope.showErrorMsg("查詢條件『*詢價起迄日』為必要輸入欄位。");
			return;
		}
		
		$scope.sendRecv("ELN100", "inquire", "com.systex.jbranch.app.server.fps.eln100.ELN100InputVO", $scope.inputVO,
		function(tota, isError) {
			if (!isError) {
				$scope.resultList = [];
				$scope.outputVO = [];
				if (tota[0].body.resultList.length > 0) {
					$scope.resultList = tota[0].body.resultList;
					$scope.outputVO = tota[0].body;
					
				} else {
					$scope.showMsg("ehl_01_common_009");	//查無資料
					return;
				}
			}
		});
	}
	
	$scope.init();
});
