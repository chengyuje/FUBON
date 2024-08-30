/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM238Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$controller('CRM230Controller', {$scope: $scope});
		
		$scope.controllerName = "CRM238Controller";
		
		getParameter.XML(["CRM.DCI_CRCY_CHGE_STATUS"], function(totas) {
			if (totas) {
				$scope.mappingSet['CRM.DCI_CRCY_CHGE_STATUS']  = totas.data[totas.key.indexOf('CRM.DCI_CRCY_CHGE_STATUS')];
			}
		});
		
		// init
		$scope.init = function(){
			$scope.expiry_date_bgn = {
					maxDate: $scope.maxDate,
					minDate: $scope.minDate
			};
			$scope.expiry_date_end = {
					maxDate: $scope.maxDate,
					minDate: $scope.minDate
			};
			$scope.due_date_bgn = {
					maxDate: $scope.maxDate,
					minDate: $scope.minDate
			};
			$scope.due_date_end = {
					maxDate: $scope.maxDate,
					minDate: $scope.minDate
			};
		};
		$scope.init();
		
		
		$scope.inquire = function() {
//			$scope.inputVO.aolist = $scope.aolist;
//			if($scope.ao_code != undefined){
//				$scope.inputVO.ao_code = $scope.ao_code[0];				
//			}
			$scope.sendRecv("CRM238", "inquire", "com.systex.jbranch.app.server.fps.crm238.CRM238InputVO", $scope.inputVO,
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
								if(row.PROD_ID != null){
									$scope.resultList.push(row);
								}
							});
							$scope.outputVO = {'data':$scope.resultList};
							/**計算加總**/
							$scope.txtList = {};
							$scope.delList = {};
							$scope.crcyList = [];
							
							//交易幣別
							var groupByCrcy1 = _.chain($scope.resultList).groupBy('VALU_CRCY_TYPE')
					    	.toPairs().map(function (pair) {
					    		return _.zipObject(['VALU_CRCY_TYPE', 'DATA'], pair); 
					    	}).value();
							angular.forEach(groupByCrcy1, function(group) {
								//加總								
								var TXN_AMT = 0;
								angular.forEach(group.DATA, function(row) {
									TXN_AMT += row.TXN_AMT;
								});
								//各幣別資料
								$scope.txtList[group.VALU_CRCY_TYPE] = TXN_AMT;
								$scope.crcyList.push(group.VALU_CRCY_TYPE);
							});
							
							//到期幣別
							var groupByCrcy2 = _.chain($scope.resultList).groupBy('CRCY')
					    	.toPairs().map(function (pair) {
					    		return _.zipObject(['CRCY', 'DATA'], pair); 
					    	}).value();
							angular.forEach(groupByCrcy2, function(group) {
								//加總								
								var DEL_AMT = 0;
								angular.forEach(group.DATA, function(row) {
									DEL_AMT += row.DEL_AMT;
								});
								//各幣別資料
								$scope.delList[group.CRCY] = DEL_AMT;
								$scope.crcyList.push(group.CRCY);
							});
							//去除重複幣別
							$scope.crcyList = _.uniq($scope.crcyList);
							
							$scope.outputVO = {'data_sum':$scope.crcyList};							
							
							//客戶資訊(一個客戶只呈現一筆)
							$scope.custList = [];
							angular.forEach(tota[0].body.resultList, function(row) {
								var temp = $scope.custList.map(function(e) { return e.CUST_ID; }).indexOf(row.CUST_ID) > -1;
								if (!temp)
		            				$scope.custList.push(row);
							});
							$scope.custOutputVO = {'data':$scope.custList};
							return;
						}
			});
	    };
	    
		$scope.limitDate2 = function() {
			$scope.expiry_date_bgn.maxDate = $scope.inputVO.expiry_date_end || $scope.maxDate;
			$scope.expiry_date_end.minDate = $scope.inputVO.expiry_date_bgn || $scope.minDate;
		};
		$scope.limitDate3 = function() {
			$scope.due_date_bgn.maxDate = $scope.inputVO.due_date_end || $scope.maxDate;
			$scope.due_date_end.minDate = $scope.inputVO.due_date_bgn || $scope.minDate;
		};
		
	    $scope.clearAll = function(){
	    	$scope.custList = [];
	    	$scope.custOutputVO = {};
	    	$scope.resultList = [];
	    	$scope.inputVO = {};
	    	$scope.outputVO = {};
	    	
	    	$scope.crcyList = {};
	    	$scope.resultCrcyList = [];
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