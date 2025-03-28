/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM232Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $timeout, crmService) {
		$controller('BaseController', {$scope: $scope});
		$controller('CRM230Controller', {$scope: $scope});
		
		$scope.controllerName = "CRM232Controller";
		
		crmService.getForbiddenList();
		
		// init
		$scope.init = function(){
			$scope.startDateOptions = {
					maxDate: $scope.maxDate,
					minDate: $scope.minDate
			};
			$scope.endDateOptions = {
					maxDate: $scope.maxDate,
					minDate: $scope.minDate
			};
		};
		$scope.init();

		//申購區間
		$scope.limitSignDate = function() {
			$scope.startDateOptions.maxDate = $scope.inputVO.sign_date_end || $scope.maxDate;
			$scope.endDateOptions.minDate = $scope.inputVO.sign_date_bgn || $scope.minDate;
		};
		
		//查詢，避免按鈕被連續點擊造成錯誤，先disable按鈕並$timeout
		$scope.inquire = function () {
			$scope.inputVO.inquireDisabled = true;
			
			$timeout(function() {
				$scope.do_inquire(); 
				$scope.inputVO.inquireDisabled = false;}
			, 1000);			
		}
		
		//查詢
		$scope.do_inquire = function() {
//			$scope.inputVO.aolist = $scope.aolist;
			$scope.sendRecv("CRM232", "inquire", "com.systex.jbranch.app.server.fps.crm232.CRM232InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
//							$scope.resultList = tota[0].body.resultList;
//							$scope.outputVO = tota[0].body;
							debugger;
							//去除沒有投資資訊的客戶
							$scope.resultList = [];
							angular.forEach(tota[0].body.resultList, function(row) {
								if(row.PRD_ID != null){
									$scope.resultList.push(row);
								}
							});
							$scope.resultList = crmService.filterList($rootScope.forbiddenData,$scope.resultList);
							$scope.outputVO = {'data':$scope.resultList};
							debugger;
							//客戶資訊(一個客戶只呈現一筆)
							$scope.custList = [];
							angular.forEach(tota[0].body.resultList, function(row) {
								var temp = $scope.custList.map(function(e) { return e.CUST_ID; }).indexOf(row.CUST_ID) > -1;
								if (!temp)
		            				$scope.custList.push(row);
							});
							$scope.custList = crmService.filterList($rootScope.forbiddenData,$scope.custList);
							$scope.custOutputVO = {'data':$scope.custList};
							debugger;
							return;
						}
			});
	    };
	    
	    //投資市場別
		$scope.getTier2 = function() {
			if($scope.inputVO.tier1) {
				$scope.inputVO.tier2 = "";
				$scope.sendRecv("PRD110", "getArea", "com.systex.jbranch.app.server.fps.prd110.PRD110InputVO", {'fund_type':$scope.inputVO.tier1},
						function(tota, isError) {
							if (!isError) {
								$scope.mappingSet['TIER2'] = [];
								angular.forEach(tota[0].body.resultList, function(row) {
									$scope.mappingSet['TIER2'].push({LABEL: row.PARAM_NAME, DATA: row.NEXT_TIER_VALUE});
			        			});
								return;
							}
				});
			} else {
				$scope.inputVO.tier1 = "";
				$scope.inputVO.tier2 = "";
				$scope.mappingSet['TIER2'] = [];
				$scope.mappingSet['TIER3'] = [];
			}
		};
		
		$scope.getTier3 = function() {
			if($scope.inputVO.tier2) {
				$scope.sendRecv("PRD110", "getTarget", "com.systex.jbranch.app.server.fps.prd110.PRD110InputVO", {'inv_area':$scope.inputVO.tier2},
						function(tota, isError) {
							if (!isError) {
								$scope.mappingSet['TIER3'] = [];
								angular.forEach(tota[0].body.resultList, function(row) {
									$scope.mappingSet['TIER3'].push({LABEL: row.PARAM_NAME, DATA: row.NEXT_TIER_VALUE});
			        			});
								return;
							}
				});
			} else {
				$scope.inputVO.tier2 = "";
				$scope.mappingSet['TIER3'] = [];
			}
		};
		
		$scope.changeWay = function() {
			if($scope.inputVO.inv_way_nbr == '' || $scope.inputVO.inv_way_nbr == '1')
				$scope.inputVO.deduct_yn = '';
		}
		
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
