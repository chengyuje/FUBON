/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM411Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM411Controller";
		
		// combobox
		getParameter.XML(["SOT.PROD_TYPE", "CRM.DISCOUNT_RNG_TYPE", 
		                  "CRM.BRG_ROLEID_LV1", "CRM.BRG_ROLEID_LV2", "CRM.BRG_ROLEID_LV3", "CRM.BRG_ROLEID_LV4", 
		                  "CRM.BRG_ROLEID_UHRM_LV1", "CRM.BRG_ROLEID_UHRM_LV2", "CRM.BRG_ROLEID_UHRM_LV3", "CRM.BRG_ROLEID_UHRM_LV4"], 
			function(totas) {
				if (totas) {
					$scope.mappingSet['SOT.PROD_TYPE'] = totas.data[totas.key.indexOf('SOT.PROD_TYPE')];
					$scope.mappingSet['CRM.BRG_ROLEID_LV1'] = totas.data[totas.key.indexOf('CRM.BRG_ROLEID_LV1')];
					$scope.mappingSet['CRM.BRG_ROLEID_LV2'] = totas.data[totas.key.indexOf('CRM.BRG_ROLEID_LV2')];
					$scope.mappingSet['CRM.BRG_ROLEID_LV3'] = totas.data[totas.key.indexOf('CRM.BRG_ROLEID_LV3')];
					$scope.mappingSet['CRM.BRG_ROLEID_LV4'] = totas.data[totas.key.indexOf('CRM.BRG_ROLEID_LV4')];
					$scope.mappingSet['CRM.BRG_ROLEID_UHRM_LV1'] = totas.data[totas.key.indexOf('CRM.BRG_ROLEID_UHRM_LV1')];
					$scope.mappingSet['CRM.BRG_ROLEID_UHRM_LV2'] = totas.data[totas.key.indexOf('CRM.BRG_ROLEID_UHRM_LV2')];
					$scope.mappingSet['CRM.BRG_ROLEID_UHRM_LV3'] = totas.data[totas.key.indexOf('CRM.BRG_ROLEID_UHRM_LV3')];
					$scope.mappingSet['CRM.BRG_ROLEID_UHRM_LV4'] = totas.data[totas.key.indexOf('CRM.BRG_ROLEID_UHRM_LV4')];
				}
			}
		);
		
		$scope.init = function() {
			$scope.ans = [];
			$scope.data = [];
			$scope.outputVO = [];
			$scope.resultList = [];
			angular.forEach($scope.mappingSet['SOT.PROD_TYPE'], function(req){
				req.SELECTED = false;
			});
			$scope.inputVO.clickAll = false;
		}
		
		$scope.init();
		
		//全選
    	$scope.checkrow = function() {
			angular.forEach($scope.mappingSet['SOT.PROD_TYPE'], function(obj){
				obj.SELECTED = $scope.inputVO.clickAll;
			});
			
    		$scope.ans = $scope.mappingSet['SOT.PROD_TYPE'];
        };
		
    	$scope.insert = function (setupType) {
    		var dialog = ngDialog.open({
    			template: 'assets/txn/CRM411/CRM411_INSERT.html',
    			className: 'CRM411_INSERT',
    			showClose: false, 
    			controller: ['$scope', function($scope) {
                	$scope.setupType = setupType;
    			}]
    		});
    		dialog.closePromise.then(function(data) {
	    		  if(data.value === 'successful'){
	    			  $scope.init();
	    		  }
	    	});
    	};
    	
    	$scope.chk = [];
    	//複選
        $scope.toggleSelection = function() {
        	var ans = $scope.mappingSet['SOT.PROD_TYPE'].filter(function(obj){
          		return (obj.SELECTED == true);
          	});
	       	$scope.ans = ans;
        };
    	
    	$scope.query = function(setupType) {
    		$scope.inputVO.role_List = [];
    		$scope.resultList = [];
 			$scope.data = [];
 			
    		$scope.sendRecv("CRM411", "query", "com.systex.jbranch.app.server.fps.crm411.CRM411InputVO", {'role_List':$scope.ans, 'setupType':setupType},
 					function(totas, isError) {
 						if (isError) {
 						}
 						console.log('setupType:' +setupType);
 						if (totas[0].body.resultList != null && totas[0].body.resultList.length > 0) {
							$scope.resultList = totas[0].body.resultList;
	                		$scope.outputVO = totas[0].body;
	                		
	                		$scope.mappingSet['CRM.ROLE'] = [];
	                		if (setupType == '9') {
	                			angular.forEach($scope.mappingSet['CRM.BRG_ROLEID_UHRM_LV1'], function(row) {
		                			$scope.mappingSet['CRM.ROLE'].push({LABEL : row.LABEL , DATA: row.DATA });
		                		});
		                		angular.forEach($scope.mappingSet['CRM.BRG_ROLEID_UHRM_LV2'], function(row) {
		                			$scope.mappingSet['CRM.ROLE'].push({LABEL : row.LABEL , DATA: row.DATA });
		                		});
		                		angular.forEach($scope.mappingSet['CRM.BRG_ROLEID_UHRM_LV3'], function(row) {
		                			$scope.mappingSet['CRM.ROLE'].push({LABEL : row.LABEL , DATA: row.DATA });
		                		});
		                		angular.forEach($scope.mappingSet['CRM.BRG_ROLEID_UHRM_LV4'], function(row) {
		                			$scope.mappingSet['CRM.ROLE'].push({LABEL : row.LABEL , DATA: row.DATA });
		                		});
	                		} else {
	                			angular.forEach($scope.mappingSet['CRM.BRG_ROLEID_LV1'], function(row) {
		                			$scope.mappingSet['CRM.ROLE'].push({LABEL : row.LABEL , DATA: row.DATA });
		                		});
		                		angular.forEach($scope.mappingSet['CRM.BRG_ROLEID_LV2'], function(row) {
		                			$scope.mappingSet['CRM.ROLE'].push({LABEL : row.LABEL , DATA: row.DATA });
		                		});
		                		angular.forEach($scope.mappingSet['CRM.BRG_ROLEID_LV3'], function(row) {
		                			$scope.mappingSet['CRM.ROLE'].push({LABEL : row.LABEL , DATA: row.DATA });
		                		});
		                		angular.forEach($scope.mappingSet['CRM.BRG_ROLEID_LV4'], function(row) {
		                			$scope.mappingSet['CRM.ROLE'].push({LABEL : row.LABEL , DATA: row.DATA });
		                		});
	                		}
                			
	                		// moron 2016/11/18 add
	                		// Stella 2016/12/01 modify
	                		angular.forEach($scope.resultList, function(row) {
	                			if(row.ROLE_LIST) {
	                				var temp = [];
	                				angular.forEach(row.ROLE_LIST.split(","), function(row2) {
	                					temp.push($filter('mapping')(row2,$scope.mappingSet['CRM.ROLE']));
									});
	                				row.ROLE_LIST = temp.toString();
	                			
	                			}
							})
						}
			})
    	 };
    	 
});