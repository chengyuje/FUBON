/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM239Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter) {
		$controller('BaseController', {$scope: $scope});
		$controller('CRM230Controller', {$scope: $scope});
		
		$scope.controllerName = "CRM239Controller";
		
		$scope.inquire = function() {
//			$scope.inputVO.aolist = $scope.aolist;
			
			$scope.sendRecv("CRM239", "inquire", "com.systex.jbranch.app.server.fps.crm239.CRM239InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
//							$scope.resultList = tota[0].body.resultList;
//							$scope.totalCntRecord = tota[0].body.totalCntRecord;
//							$scope.outputVO = tota[0].body;
							
							//去除沒有投資資訊的客戶
							$scope.resultList = [];
							angular.forEach(tota[0].body.resultList, function(row) {
								if(row.POLICY_NBR != null){
									$scope.resultList.push(row);
								}								
							});
							$scope.outputVO = {'data':$scope.resultList};
							
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
	
		// init
		$scope.init = function(){
			 $scope.policy_sDateOptions = {
	    		maxDate: $scope.maxDate,
				minDate: $scope.minDate
			 };
		    $scope.policy_eDateOptions= {
	    		maxDate: $scope.maxDate,
				minDate: $scope.minDate
		    };
		};
		$scope.init();
		
		$scope.limitDate1 = function() {
			$scope.policy_sDateOptions.maxDate = $scope.inputVO.policy_active_date_e || $scope.maxDate;
			$scope.policy_eDateOptions.minDate = $scope.inputVO.policy_active_date_s || $scope.minDate;
		};
		
	    $scope.clearAll = function(){
	    	$scope.custList = [];
	    	$scope.insCompanyList = [];
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

		$scope.getInsCompany = function(){
			$scope.insCompanyList = [];
			
			$scope.sendRecv("CRM239", "getInsCompany", "com.systex.jbranch.app.server.fps.crm239.CRM239InputVO", $scope.inputVO,
				function(tota, isError) {
				if (!isError) {
					$scope.insCompanyList = tota[0].body.resultList;
				}					
			});
		};
		$scope.getInsCompany();
		
});
