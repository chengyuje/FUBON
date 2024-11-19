/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT610Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT610Controller";
		
		getParameter.XML(["SOT.TRADE_TYPE", "SOT.BUY_SELL", "SOT.TRUST_TRADE_TYPE", "SOT.TRADE_TYPE_DYNA"], function(totas) {
			   if (totas) {
				//交易種類-基金
			    $scope.mappingSet['SOT.TRADE_TYPE'] = totas.data[totas.key.indexOf('SOT.TRADE_TYPE')];
				//交易種類-其他
			    $scope.mappingSet['SOT.BUY_SELL'] = totas.data[totas.key.indexOf('SOT.BUY_SELL')];
			    //信託交易類別
			    $scope.mappingSet['SOT.TRUST_TRADE_TYPE'] = totas.data[totas.key.indexOf('SOT.TRUST_TRADE_TYPE')];
			    //交易種類-動態鎖利
			    $scope.mappingSet['SOT.TRADE_TYPE_DYNA'] = totas.data[totas.key.indexOf('SOT.TRADE_TYPE_DYNA')];
			   }
			  });
		
		//=== filter
		var vo = {'param_type': 'SOT.TRADE_TYPE', 'desc': false};
		if(!projInfoService.mappingSet['SOT.TRADE_TYPE']) {
	    	$scope.requestComboBox(vo, function(totas) {
	    		if (totas[totas.length - 1].body.result === 'success') {
	    			projInfoService.mappingSet['SOT.TRADE_TYPE'] = totas[0].body.result;
	    			$scope.mappingSet['SOT.TRADE_TYPE'] = projInfoService.mappingSet['SOT.TRADE_TYPE'];
	    		}
	    	});
	    } else {
	    	$scope.mappingSet['SOT.TRADE_TYPE'] = projInfoService.mappingSet['SOT.TRADE_TYPE'];
	    }
		
		var vo = {'param_type': 'SOT.BARGAIN_FEE_FLAG', 'desc': false};
		if(!projInfoService.mappingSet['SOT.BARGAIN_FEE_FLAG']) {
	    	$scope.requestComboBox(vo, function(totas) {
	    		if (totas[totas.length - 1].body.result === 'success') {
	    			projInfoService.mappingSet['SOT.BARGAIN_FEE_FLAG'] = totas[0].body.result;
	    			$scope.mappingSet['SOT.BARGAIN_FEE_FLAG'] = projInfoService.mappingSet['SOT.BARGAIN_FEE_FLAG'];
	    		}
	    	});
	    } else {
	    	$scope.mappingSet['SOT.BARGAIN_FEE_FLAG'] = projInfoService.mappingSet['SOT.BARGAIN_FEE_FLAG'];
	    }
		
		var vo = {'param_type': 'SOT.PROD_TYPE', 'desc': false};
		if(!projInfoService.mappingSet['SOT.PROD_TYPE']) {
	    	$scope.requestComboBox(vo, function(totas) {
	    		if (totas[totas.length - 1].body.result === 'success') {
	    			projInfoService.mappingSet['SOT.PROD_TYPE'] = totas[0].body.result;
	    			$scope.mappingSet['SOT.PROD_TYPE'] = projInfoService.mappingSet['SOT.PROD_TYPE'];
	    		}
	    	});
	    } else {
	    	$scope.mappingSet['SOT.PROD_TYPE'] = projInfoService.mappingSet['SOT.PROD_TYPE'];
	    }
		
		var vo = {'param_type': 'SOT.TRADE_STATUS', 'desc': false};
		if(!projInfoService.mappingSet['SOT.TRADE_STATUS']) {
	    	$scope.requestComboBox(vo, function(totas) {
	    		if (totas[totas.length - 1].body.result === 'success') {
	    			projInfoService.mappingSet['SOT.TRADE_STATUS'] = totas[0].body.result;
	    			$scope.mappingSet['SOT.TRADE_STATUS'] = projInfoService.mappingSet['SOT.TRADE_STATUS'];
	    		}
	    	});
	    } else {
	    	$scope.mappingSet['SOT.TRADE_STATUS'] = projInfoService.mappingSet['SOT.TRADE_STATUS'];
	    }
		
		var vo = {'param_type': 'SOT.TRADE_STATUS', 'desc': false};
		if(!projInfoService.mappingSet['SOT.TRADE_STATUS']) {
	    	$scope.requestComboBox(vo, function(totas) {
	    		if (totas[totas.length - 1].body.result === 'success') {
	    			projInfoService.mappingSet['SOT.TRADE_STATUS'] = totas[0].body.result;
	    			$scope.mappingSet['TRADE_STATUS'] = projInfoService.mappingSet['SOT.TRADE_STATUS'];
	    		}
	    	});
	    } else {
	    	$scope.mappingSet['SOT.TRADE_STATUS'] = projInfoService.mappingSet['SOT.TRADE_STATUS'];
	    }
        //===
		
		//=== filter combobox
		$scope.getTradeType = function() {
			//modify by jimmy 2017/02/22
			$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = [];
			//若商品種類為1，則交易種類SOT.TRADE_TYPE的XML；若商品種類為2~5，則交易種類SOT.BUY_SELL的XML
			if ($scope.inputVO.prodType == "1") {
				$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = [];
				$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = $scope.mappingSet['SOT.TRADE_TYPE'];
				//只有基金與ETF有是否議價
				$scope.YN_Baragin = false;
				//是否議價預設值為Y
				$scope.inputVO.isBaraginNeeded = 'Y'
			} else if ($scope.inputVO.prodType == "8") { //動態鎖利 
				$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = [];
				$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = $scope.mappingSet['SOT.TRADE_TYPE_DYNA'];
				//只有基金與ETF有是否議價
				$scope.YN_Baragin = false;
				//是否議價預設值為Y
				$scope.inputVO.isBaraginNeeded = 'Y'
			}else if($scope.inputVO.prodType == ""){
				$scope.YN_Baragin = true;
				$scope.inputVO.isBaraginNeeded = '';
				$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = [];
			}else{
				if($scope.inputVO.prodType == "2"){
					$scope.YN_Baragin = false;
					$scope.inputVO.isBaraginNeeded = 'Y';
				}else{
					$scope.inputVO.isBaraginNeeded = '';
					$scope.YN_Baragin = true;
				}
				$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = [];
				$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = $scope.mappingSet['SOT.BUY_SELL'];
			}

		}
		
		$scope.getBargainFeeFlag = function() {
			$scope.mappingSet['SOT.BARGAIN_FEE_FLAG_COMBOBOX'] = [];
			//若商品種類為1，則交易種類1~4；若商品種類為2~5，則交易種類1~2
			angular.forEach(projInfoService.mappingSet['SOT.BARGAIN_FEE_FLAG'], function(row){
				if ($scope.inputVO.isBaraginNeeded == "Y") {
					$scope.mappingSet['SOT.BARGAIN_FEE_FLAG_COMBOBOX'].push({LABEL: row.LABEL, DATA: row.DATA});
				} else {
					$scope.mappingSet['SOT.BARGAIN_FEE_FLAG_COMBOBOX'] = [];
					$scope.inputVO.bargainFeeFlag = "";
				}
			});
		}
		//===
        
		// time picker
		$scope.startDateOptions = {
			maxDate: $scope.inputVO.eDate || $scope.maxDate,
			minDate: $scope.minDate
		};
        $scope.endDateOptions = {
			maxDate: $scope.inputVO.eDate || $scope.maxDate,
			minDate: $scope.inputVO.sDate || $scope.minDate
		};

		$scope.altInputFormats = ['M!/d!/yyyy'];
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.startDateOptions.maxDate = $scope.inputVO.eDate || $scope.maxDate;
			if ($scope.inputVO.eDate) {
				let y = $scope.inputVO.eDate.getFullYear();
				let m = $scope.inputVO.eDate.getMonth() - 6;
				let d = $scope.inputVO.eDate.getDate();
				$scope.startDateOptions.minDate = new Date(y, m, d);
			}
			
			$scope.endDateOptions.minDate = $scope.inputVO.sDate || $scope.minDate;
			if ($scope.inputVO.sDate) {
				let y = $scope.inputVO.sDate.getFullYear();
				let m = $scope.inputVO.sDate.getMonth() + 6;
				let d = $scope.inputVO.sDate.getDate();
				$scope.endDateOptions.maxDate = new Date(y, m, d);
			}

		};
      	
        $scope.init = function(){
        	$scope.YN_Baragin = true;
			$scope.inputVO = {
					custID: '',
					prodType: '',
					tradeType: '',
					tradeStatus: '',
					isBaraginNeeded: '',
					bargainFeeFlag: '', 
					prodID: '', 
					sDate: undefined, 
					eDate: undefined
        	};
			$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = [];
			$scope.limitDate();
		};
		$scope.init();
        
		$scope.inquireInit = function() {
			$scope.data = [];
			$scope.tradeList = [];
			$scope.outputVO = [];
		};
		$scope.inquireInit();	
		
		$scope.query = function() {
			$scope.tradeList = [];
			$scope.outputVO = [];
			if ($scope.checkColumn()) {
				$scope.sendRecv("SOT610", "query", "com.systex.jbranch.app.server.fps.sot610.SOT610InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.tradeList.length == 0) {
									$scope.showMsg("ehl_01_common_009");
		                			return;
		                		}
								$scope.tradeList = tota[0].body.tradeList;
								$scope.outputVO = tota[0].body;
								
								return;
							}
				});
			} else {
				$scope.showErrorMsg("ehl_02_sot610_001");
			}
		}
		
		$scope.checkColumn = function() {
			if ($scope.inputVO.custID == "" && 
				$scope.inputVO.prodType == "" && 
				$scope.inputVO.tradeType == "" && 
				$scope.inputVO.tradeStatus == "" && 
				$scope.inputVO.isBaraginNeeded == "" && 
				$scope.inputVO.bargainFeeFlag == "" && 
				$scope.inputVO.prodID == "" && 
				typeof($scope.inputVO.sDate) === 'undefined' &&
				typeof($scope.inputVO.eDate) === 'undefined') {
				
				return false;
			} else {
				return true;
			}
		}
		
		$scope.goPage = function(row) {
			$scope.connector('set',"SOTTradeSEQ", row.TRADE_SEQ);
			$scope.connector('set',"SOTCarSEQ", null);
			$rootScope.menuItemInfo.url = "assets/txn/" + row.GO_PAGE + "/" + row.GO_PAGE + ".html";
		}
		
		$scope.goWebBank = function(row) {
			$scope.connector('set',"SOTTradeSEQ", row.TRADE_SEQ);
			$scope.connector('set',"SOTCarSEQ", null);
			$rootScope.menuItemInfo.url = "assets/txn/" + row.GO_WEBBANK + "/" + row.GO_WEBBANK + ".html";
		}
		
		$scope.goSOT411 = function(row) {
			$scope.connector('set',"SOTTradeSEQ", row.TRADE_SEQ);
			$scope.connector('set',"SOTCarSEQ", null);
			$rootScope.menuItemInfo.url = "assets/txn/SOT411/SOT411.html";
		}
		
		$scope.printReport = function(row) {
			$scope.sendRecv("SOT610", "printReport", "com.systex.jbranch.app.server.fps.sot610.SOT610InputVO", {tradeSeq: row.TRADE_SEQ},
					function(tota, isError) {
			});
			
		}
		
		$scope.toUppercase_data = function(value,type){
			switch (type) {
			case 'custid':
				if(value){
					$scope.inputVO.custID = value.toUpperCase();
				}
				break;
			case 'prodid':
				if(value){
					$scope.inputVO.prodID = value.toUpperCase();
				}
				break;
			default:
				break;
			}
		}
});
