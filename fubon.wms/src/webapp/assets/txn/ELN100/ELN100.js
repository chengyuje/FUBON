/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ELN100Controller', function($rootScope, $scope, $controller, ngDialog) {
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
	$scope.getBBG = function(nbr){
		$scope.sendRecv("ELN100", "getBBG", "com.systex.jbranch.app.server.fps.eln100.ELN100InputVO", {'bbg_code1': $scope.inputVO['bbg_code' + nbr]},
		function(tota, isError) {
			if (!isError) {
				if (tota[0].body.resultList.length > 0) {
					// 
				} else {
					$scope.inputVO['bbg_code' + nbr] = undefined;
					$scope.showErrorMsg("無此標的");
				}
			}
		});
	}
	
	$scope.init = function(){
		$scope.disRate1 = false;
		$scope.disRate2 = false;
		$scope.inquireInit();
		
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
	
	$scope.countDate = function (Date1, Date2) {
		let milliseconds_Time = Date2.getTime() - Date1.getTime();
		return (milliseconds_Time / (1000 * 3600 * 24)) +1;
	};

	$scope.inquire = function() {
		if ($scope.inputVO.query_date_s == undefined || $scope.inputVO.query_date_e == undefined) {
			$scope.showErrorMsg("查詢條件『*詢價起迄日』為必要輸入欄位。");
			return;
		}
		
		if ($scope.countDate($scope.inputVO.query_date_s, $scope.inputVO.query_date_e) > 3) {
			$scope.showErrorMsg("查詢區間，限過去連續3個營業日。");
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
	
	$scope.chooseBBG = function(nbr) {
		var dialog = ngDialog.open({
			template: 'assets/txn/ELN100/ELN100_BBG.html',
			className: 'ELN100',
			showClose: false,
			scope : $scope,
			controller: ['$scope', function($scope) {
				
            }]
		}).closePromise.then(function (data) {
			if(data.value != "cancel") {
				$scope.inputVO['bbg_code' + nbr] = data.value.BBG_CODE;
			}
		});
	}
	
	$scope.changeKiType = function() {
		for (var i = 1; i <= 2; i++) {
			if ($scope.inputVO['type' + i] == 4) {
				if ($scope.inputVO.ki_type == 'EKI' || $scope.inputVO.ki_type == 'AKI') {
					$scope['disRate' + i] = false;
					$scope['limitVal' + i] = 50;
				} else {
					$scope['disRate' + i] = true;
					$scope['limitVal' + i] = 0;
					
					if (i == 1) {
						$scope.inputVO.rate1 = undefined;
						$scope.inputVO.rate2 = undefined;						
					} else {
						$scope.inputVO.rate3 = undefined;
						$scope.inputVO.rate4 = undefined;
					}
					
				}			
			}
		}
	}
	
	$scope.changeType = function(nbr) {
		$scope['disRate' + nbr] = false;
		$scope['limitVal' + nbr] = 0;
		
		if (nbr == '1') {
			$scope.inputVO.type2 = undefined;
			
			$scope.inputVO.rate1 = undefined;
			$scope.inputVO.rate2 = undefined;
			$scope.inputVO.rate3 = undefined;
			$scope.inputVO.rate4 = undefined;
		} else {
			if ($scope.inputVO.type2 == $scope.inputVO.type1) {
				$scope.inputVO.type2 = undefined;
				$scope.showErrorMsg("兩列不能重複選項。");
			}
			
			$scope.inputVO.rate3 = undefined;
			$scope.inputVO.rate4 = undefined;
		}
		
		/**
		 *  點選單一條件，輸入數值區間(非必要翰入欄位):
		 *  1. 年化收益率	 ：查詢每日詢價明細資料庫中，收益率(年化%)	，限數值5以上。 
		 *  2. 執行價格	 ：查詢每日詢價明細資料庫中，執行價格(%)  	，限數值50以上。
		 *  3. KO 價格	 ：查詢每日詢價明細資料庫中，KO 價格(%)	，限數值80以上。
		 *  4. KI 價格	 ：查詢每日詢價明細資料庫中，KI 價格(%)	，若『KI類型』有選EKI、AKI，開放輸入，限數值50以上; 選None，則不可輸入數值。
		 *  5. 通路服務費率：查詢每日詢價明細資料庫中，UF(%)		，限數值0.5以上。
		 * **/
		switch ($scope.inputVO['type' + nbr]) {
			case 1: {
				$scope['limitVal' + nbr] = 5;
				break;
			}
			case 2: {
				$scope['limitVal' + nbr] = 50;
				break;
			}
			case 3: {
				$scope['limitVal' + nbr] = 80;
				break;
			}
			case 4: {
				if ($scope.inputVO.ki_type == 'EKI' || $scope.inputVO.ki_type == 'AKI') {
					$scope['limitVal' + nbr] = 50;
				} else {
					$scope['limitVal' + nbr] = 0;
					$scope['disRate' + nbr] = true;
				}
				break;
			}
			case 5: {
				$scope['limitVal' + nbr] = 0.5;
				break;
			}
			default: {
				break;
			}
		}
//		alert($scope.limitVal);
	}
	
	$scope.checkRate = function(nbr) {
		var val = $scope.inputVO['rate' + nbr];
		var limitNbr = nbr > '2' ? '2' : '1';
		
		if (val < $scope['limitVal' + limitNbr]) {
			$scope.inputVO['rate' + nbr] = undefined;
			$scope.showErrorMsg("限數值 " + $scope['limitVal' + limitNbr] + " 以上");
			return;
		}
		
		if ($scope.inputVO.rate1 != undefined && $scope.inputVO.rate2 != undefined) {
			if ($scope.inputVO.rate1 > $scope.inputVO.rate2) {
				if (nbr == '1') {
					$scope.inputVO.rate1 = undefined;
					$scope.showErrorMsg("數值需小於 " + $scope.inputVO.rate2);
				} else {
					$scope.inputVO.rate2 = undefined;
					$scope.showErrorMsg("數值需大於 " + $scope.inputVO.rate1);
				}
			}
		}
		if ($scope.inputVO.rate3 != undefined && $scope.inputVO.rate4 != undefined) {
			if ($scope.inputVO.rate3 > $scope.inputVO.rate4) {
				if (nbr == '3') {
					$scope.inputVO.rate3 = undefined;
					$scope.showErrorMsg("數值需小於 " + $scope.inputVO.rate4);
				} else {
					$scope.inputVO.rate4 = undefined;
					$scope.showErrorMsg("數值需大於 " + $scope.inputVO.rate3);
				}
			}
		}
	}
	
	$scope.getDetail = function(row) {
		var row = row;
		
		var dialog = ngDialog.open({
			template: 'assets/txn/ELN100/ELN100_DETAIL.html',
			className: 'ELN100',
			showClose: false,
			scope : $scope,
			controller: ['$scope', function($scope) {
				$scope.row = row;
            }]
		}).closePromise.then(function (data) {
			//
		});
	}
	
	$scope.init();
}); 
