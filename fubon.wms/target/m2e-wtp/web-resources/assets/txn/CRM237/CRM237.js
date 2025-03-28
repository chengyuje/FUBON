/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM237Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, crmService) {
		$controller('BaseController', {$scope: $scope});
		$controller('CRM230Controller', {$scope: $scope});

		$scope.controllerName = "CRM237Controller";
		
		crmService.getForbiddenList();
		
		// date picker
		$scope.yomDateOptions = {
			minMode: "year"
		};
		// date picker end		
		
		$scope.inquire = function() {
//			$scope.inputVO.aolist = $scope.aolist;
			$scope.sendRecv("CRM237", "inquire", "com.systex.jbranch.app.server.fps.crm237.CRM237InputVO", $scope.inputVO,
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
								if(row.BOND_NBR != null){
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
