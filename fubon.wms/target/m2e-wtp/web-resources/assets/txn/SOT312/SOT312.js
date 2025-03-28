/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT312Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT312Controller";
		
		getParameter.XML(["SOT.BUY_SELL"], function(totas) {
			if (totas) {
			    $scope.mappingSet['SOT.BUY_SELL'] = totas.data[totas.key.indexOf('SOT.BUY_SELL')];			    
			}
		});
        		
		// time picker
		$scope.optionsInit = function() {
			$scope.startDateOptions = {
				maxDate: $scope.inputVO.eDate || $scope.maxDate,
				minDate: $scope.minDate
			};
			$scope.endDateOptions = {
				maxDate: $scope.inputVO.eDate || $scope.maxDate,
				minDate: $scope.inputVO.sDate || $scope.minDate
			}
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
        	$scope.GtcNo = undefined;
			$scope.inputVO = {
					custID: '',
					tradeType: '',
					prodID: '', 
					sDate: undefined, 
					eDate: undefined
        	};
        	$scope.optionsInit();
			$scope.limitDate();
		};
		$scope.init();
        
		$scope.inquireInit = function() {
			$scope.data = [];
			$scope.gtcList = [];
			$scope.outputVO = [];
		};
		$scope.inquireInit();	
		
		$scope.query = function() {
			$scope.gtcList = [];
			$scope.outputVO = [];
			
			if ($scope.checkColumn()) {
				$scope.sendRecv("SOT312", "query", "com.systex.jbranch.app.server.fps.sot312.SOT312InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								debugger
								$scope.gtcList = tota[0].body.gtcList;
								console.log($scope.gtcList);
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
