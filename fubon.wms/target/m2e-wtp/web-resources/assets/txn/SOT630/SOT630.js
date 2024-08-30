'use strict';
eSoafApp.controller('SOT630Controller', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "SOT630Controller";
	
	getParameter.XML(["SOT.AUTH_TYPE", "SOT.PROD_TYPE", "SOT.TRADE_TYPE", "SOT.BUY_SELL_B", "SOT.TRUST_TRADE_TYPE"], function(totas) {
		if (totas) {
			$scope.mappingSet['SOT.AUTH_TYPE'] = totas.data[totas.key.indexOf('SOT.AUTH_TYPE')];
			$scope.mappingSet['SOT.PROD_TYPE'] = totas.data[totas.key.indexOf('SOT.PROD_TYPE')];
			$scope.mappingSet['SOT.TRADE_TYPE'] = totas.data[totas.key.indexOf('SOT.TRADE_TYPE')];
			$scope.mappingSet['SOT.BUY_SELL_B'] = totas.data[totas.key.indexOf('SOT.BUY_SELL_B')];
			$scope.mappingSet['SOT.TRUST_TRADE_TYPE'] = totas.data[totas.key.indexOf('SOT.TRUST_TRADE_TYPE')];
		}
	});
	
	$scope.getTradeType = function() {
		$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = [];
		switch ($scope.inputVO.prodType) {
			case "1":
				$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = [];
				$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = $scope.mappingSet['SOT.TRADE_TYPE'];
				break;
			case "2":
			case "3":
			case "4":
			case "5":
				$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = [];
				$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = $scope.mappingSet['SOT.BUY_SELL_B'];
				break;
			default:
				$scope.mappingSet['SOT.TRADE_TYPE_COMBOBOX'] = [];
				break;
		}
	}
    
	// time picker
	$scope.startDateOptions = {
		maxDate: $scope.inputVO.eDate || $scope.maxDate,
		minDate: $scope.minDate
	};
    $scope.startDateOptions2 = {
		maxDate: $scope.inputVO.eDate || $scope.maxDate,
		minDate: $scope.inputVO.sDate || $scope.minDate
	};
	$scope.endDateOptions = {
		maxDate: $scope.maxDate,
		minDate: $scope.inputVO.sDate || $scope.minDate
	};
	$scope.endDateOptions2 = {
		maxDate: $scope.maxDate,
		minDate: $scope.inputVO.eDate || $scope.minDate
	};
	$scope.altInputFormats = ['M!/d!/yyyy'];
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	$scope.limitDate = function() {
		$scope.startDateOptions.maxDate = $scope.inputVO.eDate || $scope.inputVO.sDate2 || $scope.maxDate;
		
		$scope.startDateOptions2.minDate = $scope.inputVO.sDate || $scope.minDate;
		
		$scope.endDateOptions.minDate = $scope.inputVO.sDate || $scope.minDate;
		$scope.endDateOptions.maxDate = $scope.inputVO.eDate2;
		
		$scope.endDateOptions2.minDate = $scope.inputVO.eDate || $scope.minDate;
	};
  	
    $scope.init = function(){
		$scope.inputVO = {
			custID: '',
			authType: '',
			prodType: '',
			tradeType: '',
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
		
		$scope.sendRecv("SOT630", "query", "com.systex.jbranch.app.server.fps.sot630.SOT630InputVO", $scope.inputVO, function(tota, isError) {
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
	}
	
	$scope.toUppercase_data = function(value, type){
		switch (type) {
			case 'custid':
				if(value){
					$scope.inputVO.custID = value.toUpperCase();
				}
				break;
			default:
				break;
			}
	}
	
	$scope.initVal = function() {
		switch ($scope.inputVO.authType) {
			case "A" :
				$scope.inputVO.tradeType = '';
				$scope.inputVO.trustTradeType = '';
				break;
		}
	}
	
    $scope.comma_split = function(value) {
    	if (null == value) {
    		return '';
    	} else {
    		return value.split(',');
    	}
    	
    }
});
