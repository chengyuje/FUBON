/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM236Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$controller('CRM230Controller', {$scope: $scope});
		
		$scope.controllerName = "CRM236Controller";
		
		// date picker
		$scope.yomDateOptions = {
			minMode: "year"
		};
		/****** date picker end	******/
		
		$scope.inquire = function() {
//			$scope.inputVO.aolist = $scope.aolist;
//			if($scope.ao_code != undefined){
//				$scope.inputVO.ao_code = $scope.ao_code[0];				
//			}
			$scope.sendRecv("CRM236", "inquire", "com.systex.jbranch.app.server.fps.crm236.CRM236InputVO", $scope.inputVO,
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
	    
	    $scope.clearAll = function(){
	    	$scope.custList = [];
	    	$scope.custOutputVO = {};
	    	$scope.resultList = [];
	    	$scope.inputVO = {};
	    	$scope.outputVO = {};
	    }
		

//		$scope.sendRecv("CRM236", "getProd_id", "com.systex.jbranch.app.server.fps.crm236.CRM236InputVO", {},
//				function(totas, isError) {
//                	if (isError) {
//                		$scope.showErrorMsg(totas[0].body.msgData);
//                	}
//                	if (totas.length > 0) {
//                		$scope.mappingSet['prod_id'] = [];
//                		angular.forEach(totas[0].body.resultList, function(row, index, objs){
//            				$scope.mappingSet['prod_id'].push({LABEL: row.PROD_ID, DATA: row.PROD_ID});
//            			});
//                	};
//				}
//		);
		
//		$scope.sendRecv("CRM236", "getProd_name", "com.systex.jbranch.app.server.fps.crm236.CRM236InputVO", {},
//				function(totas, isError) {
//                	if (isError) {
//                		$scope.showErrorMsg(totas[0].body.msgData);
//                	}
//                	if (totas.length > 0) {
//                		$scope.mappingSet['prod_name'] = [];
//                		angular.forEach(totas[0].body.resultList, function(row, index, objs){
//            				$scope.mappingSet['prod_name'].push({LABEL: row.PROD_NAME, DATA: row.PROD_NAME});
//            			});
//                	};
//				}
//		);
	    
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
