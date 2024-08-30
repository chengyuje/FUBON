/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM235Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$controller('CRM230Controller', {$scope: $scope});
		$scope.controllerName = "CRM235Controller";
		
		// date picker
		$scope.yomDateOptions = {
			minMode: "year"
		};
		// date picker end		
		
		$scope.inquire = function() {
//			$scope.inputVO.aolist = $scope.aolist;
//			if($scope.ao_code != undefined){
//				$scope.inputVO.ao_code = $scope.ao_code[0];				
//			}
			$scope.sendRecv("CRM235", "inquire", "com.systex.jbranch.app.server.fps.crm235.CRM235InputVO", $scope.inputVO,
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
		
//		$scope.sendRecv("CRM235", "getBondnbr", "com.systex.jbranch.app.server.fps.crm235.CRM235InputVO", {},
//				function(totas, isError) {
//                	if (isError) {
//                		$scope.showErrorMsg(totas[0].body.msgData);
//                	}
//                	if (totas.length > 0) {
//                		$scope.mappingSet['BOND_NBR'] = [];
//                		angular.forEach(totas[0].body.resultList, function(row, index, objs){
//            				$scope.mappingSet['BOND_NBR'].push({LABEL: row.BOND_NBR, DATA: row.BOND_NBR});
//            			});
//                	};
//				}
//		);
//		
//		$scope.sendRecv("CRM235", "getProdname", "com.systex.jbranch.app.server.fps.crm235.CRM235InputVO", {},
//				function(totas, isError) {
//                	if (isError) {
//                		$scope.showErrorMsg(totas[0].body.msgData);
//                	}
//                	if (totas.length > 0) {
//                		$scope.mappingSet['PROD_NAME'] = [];
//                		angular.forEach(totas[0].body.resultList, function(row, index, objs){
//            				$scope.mappingSet['PROD_NAME'].push({LABEL: row.PROD_NAME, DATA: row.PROD_NAME});
//            			});
//                	};
//				}
//		);
//	    
//		$scope.sendRecv("CRM235", "getInstition", "com.systex.jbranch.app.server.fps.crm235.CRM235InputVO", {},
//				function(totas, isError) {			
//		        	if (isError) {
//			    		$scope.showErrorMsg(totas[0].body.msgData);
//			    	}
//			    	if (totas.length > 0) {
//			    		$scope.mappingSet['INSTITION_OF_FLOTATION'] = [];
//			    		angular.forEach(totas[0].body.resultList, function(row, index, objs){
//							$scope.mappingSet['INSTITION_OF_FLOTATION'].push({LABEL: row.INSTITION_OF_FLOTATION, DATA: row.INSTITION_OF_FLOTATION});
//						});
//			    	};			
//		});
	    
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
