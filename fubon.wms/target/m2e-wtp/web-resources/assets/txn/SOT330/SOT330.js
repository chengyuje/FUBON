/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT330Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT330Controller";
		
		$scope.mappingSet['SOT.BUY_SELL'] = [];
		$scope.mappingSet['SOT.BUY_SELL'].push({LABEL: '申購' , DATA: 'B'}, {LABEL: '贖回' , DATA: 'S'});
		$scope.mappingSet['EntrustStatus'] = [];
		$scope.mappingSet['EntrustStatus'].push({LABEL: '已成交' , DATA: '01'}, {LABEL: '未成交' , DATA: '02'}, {LABEL: '部分成交' , DATA: '03'});
        		
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
        	$scope.GtcNo = undefined;
			$scope.inputVO = {
					custID: '',
					tradeType: '',
					prodID: '', 
					sDate: undefined, 
					eDate: undefined
        	};
			$scope.limitDate();
		};
		$scope.init();
        
		$scope.inquireInit = function() {
			$scope.data = [];
			$scope.resultList = [];
			$scope.outputVO = [];
		};
		$scope.inquireInit();	
		
		$scope.query = function() {
			$scope.resultList = [];
			$scope.outputVO = [];
			
			if ($scope.checkColumn()) {
				$scope.sendRecv("SOT330", "query", "com.systex.jbranch.app.server.fps.sot330.SOT330InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								debugger
								$scope.resultList = tota[0].body.resultList;
								console.log($scope.resultList);
								$scope.outputVO = tota[0].body;
								
								return;
							}
				});
			}
		}
		
		$scope.checkColumn = function() {					
			if ($scope.inputVO.custID == "" || 
				$scope.inputVO.tradeType == "" || 
				typeof($scope.inputVO.sDate) === 'undefined' ||
				typeof($scope.inputVO.eDate) === 'undefined') {
				
				$scope.showErrorMsgInDialog("ehl_01_common_022");
				return false;
			} else {
				return true;
			}
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
		
		//長效單明細資料
	    $scope.goDetail = function (row){
	    	debugger
	    	var dialog = ngDialog.open({
	    		template: 'assets/txn/SOT312/SOT312_DETAIL.html',
				className: 'SOT312_DETAIL',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.GtcNo = row.GtcNo;
					$scope.CustId = row.CustId;
				}]						    	        
	    	}).closePromise.then(function (data) {
	    		//$scope.GtcNo = undefined;
	    	});	        	 
	    };
});
