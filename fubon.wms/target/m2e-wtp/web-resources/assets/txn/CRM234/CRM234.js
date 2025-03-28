/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM234Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService , getParameter, crmService) {
		$controller('BaseController', {$scope: $scope});
		$controller('CRM230Controller', {$scope: $scope});
		
		$scope.controllerName = "CRM234Controller";
		
		crmService.getForbiddenList();
		
		getParameter.XML(["CRM.CRM233_PRD_TYPE" , "FPS.CURRENCY"], function(totas) {
			if (totas) {
				//海外股票篩選條件之投資標的類型 僅放入 一般股票、認股權證、ADR，其他項目不要放入
				$scope.mappingSet['CRM.CRM233_PRD_TYPE_RE'] = [];
				angular.forEach(totas.data[totas.key.indexOf('CRM.CRM233_PRD_TYPE')] , function(row, index, objs){
					if(1 == row.DATA || 3 == row.DATA || 5 == row.DATA){
						$scope.mappingSet['CRM.CRM233_PRD_TYPE_RE'].push({LABEL: row.LABEL, DATA: row.DATA});
					}
				});
				
				//海外股票篩選條件之商品幣別 僅放入資料庫有的商品幣別-美元、港幣、日圓及歐元，其他幣別不要放入
				$scope.mappingSet['FPS.CURRENCY_RE'] = [];
				angular.forEach(totas.data[totas.key.indexOf('FPS.CURRENCY')] , function(row, index, objs){
					if('USD' == row.DATA || 'HKD' == row.DATA || 'JPY' == row.DATA || 'EUR' == row.DATA){
						$scope.mappingSet['FPS.CURRENCY_RE'].push({LABEL: row.LABEL, DATA: row.DATA});
					}
				});
				
			}
		});
		
		// pro_ivs_product
		$scope.mappingSet['pro_ivs_product'] = [];
		$scope.mappingSet['pro_ivs_product'].push({LABEL : '專投商品',DATA : '1'},{LABEL : '非專投商品',DATA : '2'});
		
		// trust_type
		$scope.mappingSet['trust_type'] = [];
		$scope.mappingSet['trust_type'].push({LABEL : '台幣信託',DATA : '1'},{LABEL : '外幣信託',DATA : '2'});
		
		// 交易所		
		$scope.sendRecv("CRM234", "getStockCode", "com.systex.jbranch.app.server.fps.crm234.CRM234InputVO", {},
				function(tota, isError) {
			if (!isError) {
				$scope.mappingSet['STOCK_CODE'] = [];
				angular.forEach(tota[0].body.resultList, function(row, index, objs){
    				$scope.mappingSet['STOCK_CODE'].push({LABEL: row.STOCK_NAME, DATA: row.STOCK_CODE});
    			});
			}
		});
		
		
		// init
		$scope.init = function(){
			$scope.txn_sDateOptions = {
					maxDate: $scope.maxDate,
					minDate: $scope.minDate
			};
			$scope.txn_eDateOptions = {
					maxDate: $scope.maxDate,
					minDate: $scope.minDate
			};
		};
		$scope.init();

		// Date Limit
		$scope.limitDate1 = function() {
			$scope.txn_sDateOptions.maxDate = $scope.inputVO.txn_date_end || $scope.maxDate;
			$scope.txn_eDateOptions.minDate = $scope.inputVO.txn_date_bgn || $scope.minDate;
		};
		
		// inquire
		$scope.inquire = function() {
//			$scope.inputVO.aolist = $scope.aolist;
			$scope.sendRecv("CRM234", "inquire", "com.systex.jbranch.app.server.fps.crm234.CRM234InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
//							$scope.resultList = tota[0].body.resultList;
//							$scope.outputVO = tota[0].body;
							
							//去除沒有投資資訊的客戶
							$scope.resultList = [];
							angular.forEach(tota[0].body.resultList, function(row) {
								if(row.PROD_NAME != null){
									$scope.resultList.push(row);
								}
							});
							$scope.resultList = crmService.filterList($rootScope.forbiddenData,$scope.resultList);
							$scope.outputVO = {'data':$scope.resultList};
							
							//客戶資訊(一個客戶只呈現一筆)
							$scope.custList = [];
							angular.forEach(tota[0].body.resultList, function(row) {
								var temp = $scope.custList.map(function(e) { return e.CUST_ID; }).indexOf(row.CUST_ID) > -1;
								if (!temp)
		            				$scope.custList.push(row);
							});
							$scope.custList = crmService.filterList($rootScope.forbiddenData,$scope.custList);
							$scope.custOutputVO = {'data':$scope.custList};
							return;
						}
			});
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
