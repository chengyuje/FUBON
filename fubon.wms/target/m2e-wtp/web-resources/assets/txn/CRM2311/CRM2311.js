/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM2311Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService , getParameter) {
		$controller('BaseController', {$scope: $scope});
		$controller('CRM230Controller', {$scope: $scope});
		$scope.controllerName = "CRM2311Controller";
		
		// init
		$scope.init = function(){
			$scope.value_sDateOptions = {
				maxDate: $scope.maxDate,
				minDate: $scope.minDate
			};
			$scope.value_eDateOptions = {
				maxDate: $scope.maxDate,
				minDate: $scope.minDate
			};
			$scope.due_sDateOptions = {
				maxDate: $scope.maxDate,
				minDate: $scope.minDate
			};
			$scope.due_eDateOptions = {
				maxDate: $scope.maxDate,
				minDate: $scope.minDate
			};
			
			getParameter.XML(["CRM.DEBIT_TYPE"], function(totas) {
				if (totas) {
					$scope.mappingSet['CRM.DEBIT_TYPE'] = totas.data[totas.key.indexOf('CRM.DEBIT_TYPE')];
				}
			});
		};
		$scope.init();

		$scope.inquire = function() {
			$scope.sendRecv("CRM2311", "inquire", "com.systex.jbranch.app.server.fps.crm2311.CRM2311InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.resultList = [];
								$scope.outputVO = [];
								$scope.custList = [];
								$scope.custOutputVO = [];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							
							//去除沒有投資資訊的客戶
							$scope.resultList = [];
							angular.forEach(tota[0].body.resultList, function(row) {
								if(row.DEBIT_TYPE != null){
									$scope.resultList.push(row);
								}
							});
							$scope.outputVO = {'data':$scope.resultList};
							
							//客戶資訊(一個客戶只呈現一筆)
							$scope.custList = [];
							angular.forEach($scope.resultList, function(row) {
								var temp = $scope.custList.map(function(e) { return e.CUST_ID; }).indexOf(row.CUST_ID) > -1;
								if (!temp)
		            				$scope.custList.push(row);
							});
							$scope.custOutputVO = {'data':$scope.custList};
							return;
						}
			});
	    };

		$scope.limitDate = function() {
			$scope.due_sDateOptions.maxDate = $scope.inputVO.due_eDate || $scope.maxDate;
			$scope.due_eDateOptions.minDate = $scope.inputVO.due_sDate || $scope.minDate;
		};
		
	    $scope.clearAll = function(){
	    	$scope.custList = [];
	    	$scope.custOutputVO = {};
	    	$scope.resultList = [];
	    	$scope.inputVO = {};
	    	$scope.outputVO = {};
	    }
	    
		$scope.initQuery = function(){
			$scope.sendRecv("CRM211", "initQuery", "com.systex.jbranch.app.server.fps.crm211.CRM211InputVO", $scope.inputVO,
				function(tota, isError) {
				if (!isError) {
					if(tota[0].body.showMsg){
						$scope.showErrorMsg(tota[0].body.errorMsg);
					}
					
				}					
			});
		};
		$scope.initQuery();
		
});
