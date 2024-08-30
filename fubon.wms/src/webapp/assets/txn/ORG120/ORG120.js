/*
 * Modify LOG
 * 
 * 2017-02-02 組織連動(專案同步修正) modify by ocean
 * 
 */
'use strict';
eSoafApp.controller('ORG120Controller',
	function(sysInfoService, $rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $timeout, $filter, $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "ORG120Controller";
		
		$scope.memLoginFlag = sysInfoService.getMemLoginFlag();
		
		//組織連動繼承
		$controller('RegionController', {$scope: $scope});
		
		$scope.getUHRMListByType = function () {
        	var deferred = $q.defer();
        	
        	$scope.sendRecv("ORG260", "getUHRMListByType", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, 
    				function(tota, isError) {
    					if (isError) {
    						return;
    					}
    					if (tota.length > 0) {
    						$scope.mappingSet['UHRM_LIST'] = tota[0].body.uhrmList;
    						
    						deferred.resolve("success");
    					}
    		});
    		
    		return deferred.promise;
        }
		
		// filter
		getParameter.XML(["COMMON.REVIEW_STATUS"], function(totas) {
			if (totas) {
				$scope.mappingSet['COMMON.REVIEW_STATUS'] = totas.data[totas.key.indexOf('COMMON.REVIEW_STATUS')];
			}
		});
        //
        
        /**查詢**/
        $scope.getBranchMbrQuotaLst = function(){
        	
          	$scope.sendRecv("ORG120", "getBranchMbrQuotaLst", "com.systex.jbranch.app.server.fps.org120.ORG120InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.dataList.length == 0) {
							$scope.showMsg('ehl_01_common_009');
                			return;
                		}

						$scope.paramList = tota[0].body.dataList;
						$scope.outputVO = tota[0].body;

						return;
						
					}
			});
        };
        
        $scope.giveValue = function (source) {
        	if (source == 'ec') {
        		$scope.inputVO.aoCodeInput = $scope.inputVO.ao_code;
        	} else if (source == 'ip') {
        		if ($scope.inputVO.aoCodeInput.length == 3) {
            		$scope.inputVO.ao_code = '';
            		angular.forEach($scope.AO_LIST, function(item, index) {
            		    var tindex = ($filter('uppercase')($scope.inputVO.aoCodeInput)).indexOf(item.DATA);
            		    if (tindex > -1 && item.DATA != '') {
            		    	$scope.inputVO.ao_code = $filter('uppercase')($scope.inputVO.aoCodeInput);
            		    }
            		});
            		
            		if ($scope.inputVO.ao_code == '') {
        		    	$scope.inputVO.aoCodeInput = '';
        		    	$scope.inputVO.ao_code = '';
        		    	
        		    	$scope.showMsg('該CODE無法查詢，可能原因：無人持有/權限不足(非轄下)/非AO CODE');
                	}
            	}
        	}
        }
        
		$scope.init = function() {
			$scope.data = [];
			$scope.paramList = [];
			
			$scope.inputVO = {     		
				region_center_id: '',
				branch_area_id  : '',
				branch_nbr		: '',
				ao_code			: '',
				atchCode		: '',
				seqno			: '',
				aoType			: '',
				empID			: '',
				typeOne			: '', 
				uhrm_code       : '',
				aoCodeInput     : ''
        	}
			
        	if ($scope.memLoginFlag.startsWith('uhrm')) {
        		$scope.mappingSet['UHRM_LIST'] = [];
        		// 執行順序有問題-->add $q
        		/*
        		 * 取得UHRM人員清單(由員工檔+角色檔)
        		 */
        		$scope.getUHRMListByType().then(function(data) {            		
            		$scope.mappingSet['connect'] = [];
    	        	if ($scope.connector('get','ORG110_tnsfData') != undefined) {
    	        		$scope.mappingSet['connect'].push($scope.connector('get','ORG110_tnsfData'));
    	        		$scope.inputVO.aoCodeInput = $scope.mappingSet['connect'][0].AO_CODE;
    	        		
    	        		angular.forEach($scope.mappingSet['UHRM_LIST'], function(row){
                		    var tindex = ($filter('uppercase')($scope.inputVO.aoCodeInput)).indexOf(row.UHRM_CODE);
                		    if (tindex > -1 && row.DATA != '') {
                		    	$scope.inputVO.uhrm_code = row.DATA;
                		    }
                		});
    	        		
    	        		$scope.getBranchMbrQuotaLst();
    	        		$scope.connector('set','ORG110_tnsfData', undefined); 
    	        	}
				});
        		
        		
        	} else {
        		//組織連動
    	        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
    	        $scope.RegionController_setName($scope.region).then(function(data) {
    	        	$scope.mappingSet['connect'] = [];
    	        	if ($scope.connector('get','ORG110_tnsfData') != undefined) {
    	        		$scope.mappingSet['connect'].push($scope.connector('get','ORG110_tnsfData'));
    	        		
    	        	    $scope.inputVO.region_center_id = $scope.mappingSet['connect'][0].REGION_CENTER_ID;
    	        	    $scope.inputVO.branch_area_id = $scope.mappingSet['connect'][0].BRANCH_AREA_ID;
    	        		$scope.inputVO.branch_nbr = $scope.mappingSet['connect'][0].BRANCH_NBR;
    	        		$scope.inputVO.ao_code = $scope.mappingSet['connect'][0].AO_CODE;
    	        		$scope.getBranchMbrQuotaLst();
    	        		
    	        		$scope.connector('set','ORG110_tnsfData', undefined); 
    	        	} else if($scope.connector('get','MAO151_PARAMS') != undefined) {
    	        		$scope.inputVO.region_center_id = $scope.connector('get','MAO151_PARAMS').REGION_CENTER_ID;
    	        		$scope.inputVO.branch_area_id = $scope.connector('get','MAO151_PARAMS').BRANCH_AREA_ID;
    	        		$scope.inputVO.branch_nbr = $scope.connector('get','MAO151_PARAMS').BRANCH_ID;
    	        		$scope.inputVO.ao_code = $scope.connector('get','MAO151_PARAMS').AO_CODE;
    	        		$scope.getBranchMbrQuotaLst();
    					
    					$scope.connector('set','MAO151_PARAMS', undefined);
    	        	} else if ($scope.connector('get','passParams') != undefined) {
    	        		$scope.inputVO.ao_code = $scope.connector('get','passParams').replace("ao_code=", "");
    	        		
    	        		$scope.getBranchMbrQuotaLst();
    	        		
    	        		$scope.connector('set','passParams', undefined);
    	        	}
    			});
        	}
		};
        $scope.init();
        
        $scope.getHistory = function (row) {
        	$scope.inputVO.seqno = row.SEQNO;
        	var dialog = ngDialog.open({
				template: 'assets/txn/ORG120/ORG120_REVIEW.html',
				className: 'ORG120',
				showClose: false,
				 controller: ['$scope', function($scope) {
					 $scope.row = row;
                }]
			});
        }
        
        $scope.review = function(row, status){
        	$confirm({text: '是否' + ((status == 'N') ? '退回' : '核可')}, {size: 'sm'}).then(function() {
    			$scope.sendRecv("ORG120", "review", "com.systex.jbranch.app.server.fps.org120.ORG120InputVO", {empID: row.EMP_ID, 
    																										   REVIEW_STATUS: status}, 
    					function(totas, isError) {
    						if (isError) {
    			            	$scope.showErrorMsgInDialog(totas.body.msgData);
    			            	return;
    			            }
    						if (totas.length > 0) {
    							$scope.showMsg((status == 'N') ? "ehl_01_common_020" : "ehl_01_common_021");
    							$scope.getBranchMbrQuotaLst();
    			       		}
    					}
    			);
    		});
        }
       
        $scope.doAction = function(row, action){
        	$scope.inputVO.seqno = row.SEQNO;
			if (action == "U") {
				var dialog = ngDialog.open({
					template: 'assets/txn/ORG120/ORG120_EDIT.html',
					className: 'ORG120_EDIT',
					showClose: false,
					 controller: ['$scope', function($scope) {
	                	$scope.row = row;
	                }]
				}).closePromise.then(function (data) {
					if(data.value === 'successful'){
						$scope.data = [];
						$scope.paramList = [];
						
						$scope.getBranchMbrQuotaLst();
					}
				});
			} else if (action == "D") {
				$confirm({text: '是否刪除該人員持有AO CODE(含主CODE、維護CODE、副CODE)'}, {size: 'sm'}).then(function() {
	    			$scope.sendRecv("ORG120", "addAoCodeReviewByDel", "com.systex.jbranch.app.server.fps.org120.ORG120InputVO", {empID: row.EMP_ID}, 
	    					function(totas, isError) {
	    						if (isError) {
	    			            	$scope.showErrorMsgInDialog(totas.body.msgData);
	    			            	return;
	    			            }
	    						if (totas.length > 0) {
//	    							$scope.showMsg((status == 'N') ? "ehl_01_common_020" : "ehl_01_common_021");
	    							$scope.getBranchMbrQuotaLst();
	    			       		}
	    					}
	    			);
	    		});
			}
        };
        
        $scope.reBack = function() {
    		$scope.connector('set', 'ORG110_queryCondition', $scope.connector('get', 'ORG110_queryCondition'));
    		$rootScope.menuItemInfo.url = "assets/txn/ORG110/ORG110.html";
    	}
});
