/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM421_terminateAndApplyController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM421_terminateAndApplyController";
        
		// filter
		getParameter.XML(["CRM.CON_DEGREE", "CRM.VIP_DEGREE"], function(totas) {
			if (totas) {
				$scope.mappingSet['CRM.CON_DEGREE'] = totas.data[totas.key.indexOf('CRM.CON_DEGREE')];
				$scope.mappingSet['CRM.VIP_DEGREE'] = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
			}
		});
	    //
		
        $scope.brg_sdateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.brg_edateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		// date picker
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.brg_sdateOptions.maxDate = $scope.inputVO.brg_edate || $scope.maxDate;
			$scope.brg_edateOptions.minDate = $scope.inputVO.brg_sdate || $scope.minDate;
		};
		
		$scope.init = function(){
			$scope.inputVO = {
					seq: $scope.row.APPLY_SEQ,
					cust_id: $scope.row.CUST_ID, 
					apply_cat: $scope.row.APPLY_CAT, 
					apply_type: $scope.row.APPLY_TYPE,
					terminateReason: '',
					brg_sdate: undefined,
					brg_edate: undefined
        	};
		};
        $scope.init();
        
        $scope.initial = function() {
			$scope.sendRecv("CRM421", "initial", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.custList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.custList = tota[0].body.custList;
							$scope.y_profeeList = tota[0].body.y_profeeList;
							$scope.highest_lvList = tota[0].body.highest_lvList;
							
							$scope.cust_id = $scope.custList[0].CUST_ID;
							$scope.cust_name = $scope.custList[0].CUST_NAME;
							$scope.con_degree = $scope.custList[0].CON_DEGREE;
							$scope.vip_degree = $scope.custList[0].VIP_DEGREE;
							$scope.cust_aum = $scope.custList[0].AUM_AMT;
							$scope.y_profee = $scope.custList[0].Y_PROFEE;
							
							return;
						}
			});
	    };
	    $scope.initial();
		
	    /*
	     * 判斷當前最低折數
	     * 
	     * 2016-12-01 modify by ocean : 若其中一欄未填，則先不查找授權等級
	     * 
	     */
	    $scope.getHighest_auth_lv = function(type) {
	    	$scope.inputVO.con_degree = $scope.con_degree;
	    	$scope.inputVO.prod_type = (type == 'NF' ? '1' : '2');
	    	
	    	if ($scope.inputVO.prod_type == "1" &&
	    		(($scope.inputVO.dmt_stock != "" && $scope.inputVO.dmt_stock != undefined) ||
				($scope.inputVO.frn_stock != "" && $scope.inputVO.frn_stock != undefined) ||
				($scope.inputVO.dmt_bond != "" && $scope.inputVO.dmt_bond != undefined) ||
			    ($scope.inputVO.frn_bond != "" && $scope.inputVO.frn_bond != undefined) ||
			    ($scope.inputVO.dmt_balanced != "" && $scope.inputVO.dmt_balanced != undefined) ||
			    ($scope.inputVO.frn_balanced != "" && $scope.inputVO.frn_balanced != undefined))) {
	    		
	    		var array1 = [];
		    	var temp1 = 0;
		    	array1.push($scope.inputVO.dmt_stock, 
		    				$scope.inputVO.frn_stock, 
		    				$scope.inputVO.dmt_bond,
		    			    $scope.inputVO.frn_bond, 
		    			    $scope.inputVO.dmt_balanced, 
		    			    $scope.inputVO.frn_balanced)
		    			  
		    	$scope.inputVO.highest_auth_lv = _.min(array1);

		    	if ($scope.inputVO.highest_auth_lv != undefined && $scope.inputVO.highest_auth_lv != temp1) {
			    	$scope.sendRecv("CRM421", "getHighest_auth_lv", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.highest_lvList == null) {
									$scope.showMsg("ehl_01_common_009");
		                			return;
		                		}
								$scope.highest_lvList = tota[0].body.highest_lvList;

								$scope.highest_auth_lv_1 = $scope.highest_lvList[0].HIGHEST_LV;
								$scope.highest_auth_lv_name_1 = $scope.highest_lvList[0].HIGHEST_LV_NAME;
								temp1 = $scope.inputVO.highest_auth_lv;
								return;
							}
					});
		    	}
	    	}
	    	
	    	if ($scope.inputVO.prod_type == "2" &&
	    		($scope.inputVO.buy_hk_mrk != "" && $scope.inputVO.buy_hk_mrk != undefined) || 
				($scope.inputVO.sell_hk_mrk != "" && $scope.inputVO.sell_hk_mrk != undefined) || 
				($scope.inputVO.buy_us_mrk != "" && $scope.inputVO.buy_us_mrk != undefined) ||
				($scope.inputVO.sell_us_mrk != "" && $scope.inputVO.sell_us_mrk != undefined)) {
	    		var array2 = [];
		    	var temp2 = 0;
		    	array2.push($scope.inputVO.buy_hk_mrk, 
		    				$scope.inputVO.sell_hk_mrk, 
		    				$scope.inputVO.buy_us_mrk ,
		    				$scope.inputVO.sell_us_mrk)
		    			  
		    	$scope.inputVO.highest_auth_lv = _.min(array2);
		    	
		    	if ($scope.inputVO.highest_auth_lv != undefined && $scope.inputVO.highest_auth_lv != temp2 ) {
			    	$scope.sendRecv("CRM421", "getHighest_auth_lv", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.highest_lvList == null) {
//									$scope.showMsg("ehl_01_common_009");
		                			return;
		                		}
								$scope.highest_lvList = tota[0].body.highest_lvList;

								$scope.highest_auth_lv_2 = $scope.highest_lvList[0].HIGHEST_LV;
								temp2 = $scope.inputVO.highest_auth_lv;
								return;
							}
					});
		    	}
	    	}
	    };
	    
	    /*
	     * 送交覆核
	     * 
	     * 2016-10-11 add by walal
	     * 2016-12-01 modify by ocean : 若電文檢核/鍵機回傳error訊息，顯示於畫面
	     * 
	     */
	    $scope.terminateAndApply = function() {
	    	$scope.inputVO.cust_id = $scope.cust_id;
	    	$scope.inputVO.con_degree = $scope.con_degree;
	    	
	    	if (($scope.inputVO.dmt_stock != "" && $scope.inputVO.dmt_stock != undefined) ||
				($scope.inputVO.frn_stock != "" && $scope.inputVO.frn_stock != undefined) ||
				($scope.inputVO.dmt_bond != "" && $scope.inputVO.dmt_bond != undefined) ||
			    ($scope.inputVO.frn_bond != "" && $scope.inputVO.frn_bond != undefined) ||
			    ($scope.inputVO.dmt_balanced != "" && $scope.inputVO.dmt_balanced != undefined) ||
			    ($scope.inputVO.frn_balanced != "" && $scope.inputVO.frn_balanced != undefined)) {
	    		//do 基金
	    		$scope.inputVO.applyPeriod_1 = 'Y';
	    		$scope.inputVO.highest_auth_lv_1 = $scope.highest_auth_lv_1;
	    	} else {
	    		$scope.inputVO.applyPeriod_1 = 'N';
	    	}

	    	if (($scope.inputVO.buy_hk_mrk != "" && $scope.inputVO.buy_hk_mrk != undefined) || 
				($scope.inputVO.sell_hk_mrk != "" && $scope.inputVO.sell_hk_mrk != undefined) || 
				($scope.inputVO.buy_us_mrk != "" && $scope.inputVO.buy_us_mrk != undefined) ||
				($scope.inputVO.sell_us_mrk != "" && $scope.inputVO.sell_us_mrk != undefined)) {
	    		//do 海外ETF/股票
	    		$scope.inputVO.applyPeriod_2 = 'Y';
	    		$scope.inputVO.highest_auth_lv_2 = $scope.highest_auth_lv_2;
	    	} else {
	    		$scope.inputVO.applyPeriod_2 = 'N';
	    	}
	    	
	    	if ($scope.inputVO.applyPeriod_1 == 'Y' || $scope.inputVO.applyPeriod_2 == 'Y') {
	    		$confirm({text: '是否終止後再申請此筆資料!!'}, {size: 'sm'}).then(function() {
		    		$scope.sendRecv("CRM421", "terminateAndApply", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", $scope.inputVO,
		        			function(totas, isError) {
		            			if (isError) {
//		            				$scope.showErrorMsgInDialog(totas.body.msgData);
		            				return;
		    	                }
		    	                if (totas.length > 0) {
		    	                	if (totas[0].body.errorMsg != null) {
		    	                		$scope.showErrorMsg(totas[0].body.errorMsg);
		    	                	} else {
		    	                		$scope.showSuccessMsg("ehl_01_CRM421_007");
			                    		$scope.closeThisDialog('successful');
		    	                	}
		    	                };
		    	            }
		    		);
		    	});
	    	}
	    }
});
