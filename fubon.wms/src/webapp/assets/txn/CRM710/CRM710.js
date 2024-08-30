'use strict';
eSoafApp.controller('CRM710Controller', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm,getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CRM710Controller";
	
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
    $scope.importStartDateOptions = {
    	minMode: 'month', 
		maxDate: $scope.inputVO.importEDate || $scope.maxDate,
		minDate: $scope.minDate
	};
    
	$scope.importEndDateOptions = {
		minMode: 'month',
		maxDate: $scope.maxDate,
		minDate: $scope.inputVO.importSDate || $scope.minDate
	};
	
	$scope.limitDate = function() {
		$scope.importStartDateOptions.maxDate = $scope.inputVO.importEDate || $scope.maxDate;
		$scope.importEndDateOptions.minDate = $scope.inputVO.importSDate || $scope.minDate;
	};
	
	getParameter.XML(["SOT.TRADE_TYPE", "SOT.BUY_SELL", "IOT.TRADE_TYPE", "CRM.PROD_LIST"], function(totas) {
		if (totas) {
			//交易種類-基金
			$scope.mappingSet['SOT.TRADE_TYPE'] = totas.data[totas.key.indexOf('SOT.TRADE_TYPE')];
			//交易種類-保險
			$scope.mappingSet['IOT.TRADE_TYPE'] = totas.data[totas.key.indexOf('IOT.TRADE_TYPE')];
			//交易種類-其他
		    $scope.mappingSet['SOT.BUY_SELL'] = totas.data[totas.key.indexOf('SOT.BUY_SELL')];
		    
		    $scope.mappingSet['CRM.PROD_LIST'] = totas.data[totas.key.indexOf('CRM.PROD_LIST')]
		}
	});
	
	//=== filter combobox
	$scope.getTradeType = function() {
		$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = [];
		//若商品種類為1，則交易種類SOT.TRADE_TYPE的XML；若商品種類為2~5，則交易種類SOT.BUY_SELL的XML
		if ($scope.inputVO.prodType == "1") {
			$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = [];
			$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = $scope.mappingSet['SOT.TRADE_TYPE'];
		} else if ($scope.inputVO.prodType == "") {
			$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = [];
		} else if ($scope.inputVO.prodType == "7") {
			$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = [];
			$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = $scope.mappingSet['IOT.TRADE_TYPE'];
		} else {
			$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = [];
			$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = $scope.mappingSet['SOT.BUY_SELL'];
		}
	}
	//===
  	
    $scope.init = function(){
		$scope.inputVO = {
			empID      : '',
			custID     : '',
			custName   : '',
			importSDate: undefined, 
			importEDate: undefined,
			prodType   : '',
			tradeType  : ''
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
			$scope.sendRecv("CRM710", "query", "com.systex.jbranch.app.server.fps.crm710.CRM710InputVO", $scope.inputVO, function(tota, isError) {
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
			$scope.showErrorMsg("ehl_02_CRM710_001");
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
	
	$scope.toUppercase_data = function(value,type){
		switch (type) {
			case 'custid':
				if (value) {
					$scope.inputVO.custID = value.toUpperCase();
				}
				break;
			default:
				break;
		}
	}
});
