/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM421_PeriodPageController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM421_PeriodPageController";

		// filter
		getParameter.XML(["CRM.CON_DEGREE", "CRM.VIP_DEGREE"], function(totas) {
			if (totas) {
				$scope.mappingSet['CRM.CON_DEGREE'] = totas.data[totas.key.indexOf('CRM.CON_DEGREE')];
				$scope.mappingSet['CRM.VIP_DEGREE'] = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
			}
		});
	    //
		
		// init
		$scope.init = function(){
			$scope.actionFlag = false;  //避免發生因為連選太快或是系統忙碌導致發送相同電文至400主機(#4211)
			
			$scope.inputVO = {
//					terminateAndApply : $scope.terminateAndApply || '',
					cust_id : $scope.cust_id,
					seq : $scope.seq || '' ,
					highest_auth_lv : 0
			};
			
			//查詢客戶是否有家庭戶
//			$scope.hasPrv = false;
//			$scope.sendRecv("CRM421", "checkCustPrv", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", 
//				{'cust_id': $scope.cust_id},
//					function(tota, isError) {
//						if (!isError) {
////							alert(JSON.stringify(tota[0].body.resultList));
//							if(tota[0].body.resultList != undefined && tota[0].body.resultList.length > 0){
//								if(tota[0].body.resultList.length == 1){
//									var cust_id_m = tota[0].body.resultList[0].CUST_ID_M;
//									var cust_id_s = tota[0].body.resultList[0].CUST_ID_S;
//									if(cust_id_m != cust_id_s){
//										$scope.hasPrv = true;
//									}
//								} else {
//									$scope.hasPrv = true;
//								}
//							}
//						}
//			});
			
			$scope.brg_sdateOptions = {
					maxDate: $scope.maxDate,
					minDate: $scope.minDate
					};
			$scope.brg_edateOptions = {
					maxDate: $scope.maxDate,
					minDate: $scope.minDate
					};
			
	    	$confirm({text: '【金錢信託不適用「事先」議價授權申請及覆核】'}, {size: 'sm'}).then(function() {});
		};
		$scope.init();
		

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

		/*
		 * 查詢
		 * 
		 * 2016-10-11 add by walala
		 * 2016-12-06 modify by ocean : 手收於JAVA端修正，JS端同步修正
		 * 
		 */
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
	     * 送交覆核
	     * 
	     * 2016-10-11 add by walala
	     * 2016-12-01 modify by ocean : 若電文檢核/鍵機回傳error訊息，顯示於畫面
	     * 
	     */
	    $scope.applyPeriod = function() {
	    	$scope.actionFlag = true;
	    	$scope.inputVO.cust_id = $scope.cust_id;
	    	$scope.inputVO.con_degree = $scope.con_degree;
	    	
	    	if(($scope.inputVO.dmt_stock == "" || $scope.inputVO.dmt_stock == undefined) &&
	    		($scope.inputVO.frn_stock == "" || $scope.inputVO.frn_stock == undefined) &&
    			($scope.inputVO.buy_hk_mrk == "" || $scope.inputVO.buy_hk_mrk == undefined) &&
    			($scope.inputVO.sell_hk_mrk == "" || $scope.inputVO.sell_hk_mrk == undefined) &&
    			($scope.inputVO.dmt_bond == "" || $scope.inputVO.dmt_bond == undefined) &&
    			($scope.inputVO.frn_bond == "" || $scope.inputVO.frn_bond == undefined) &&
    			($scope.inputVO.buy_us_mrk == "" || $scope.inputVO.buy_us_mrk == undefined) &&
    			($scope.inputVO.sell_us_mrk == "" || $scope.inputVO.sell_us_mrk == undefined) &&
    			($scope.inputVO.buy_uk_mrk == "" || $scope.inputVO.buy_uk_mrk == undefined) &&
    			($scope.inputVO.sell_uk_mrk == "" || $scope.inputVO.sell_uk_mrk == undefined) &&
    			($scope.inputVO.buy_jp_mrk == "" || $scope.inputVO.buy_jp_mrk == undefined) &&
    			($scope.inputVO.sell_jp_mrk == "" || $scope.inputVO.sell_jp_mrk == undefined) &&
    			($scope.inputVO.dmt_balanced == "" || $scope.inputVO.dmt_balanced == undefined) && 
    			($scope.inputVO.frn_balanced == "" || $scope.inputVO.frn_balanced == undefined) || 
    			$scope.inputVO.brg_sdate == undefined || $scope.inputVO.brg_edate == undefined){
	    			$scope.actionFlag = false;
	    			$scope.showErrorMsg("ehl_01_common_022");		//欄位檢核錯誤：*為必要輸入欄位,請輸入後重試
	    			return;
	    	}
	    	
	    	if(($scope.inputVO.dmt_stock != "" && $scope.inputVO.dmt_stock != undefined) ||
				($scope.inputVO.frn_stock != "" && $scope.inputVO.frn_stock != undefined) ||
				($scope.inputVO.dmt_bond != "" && $scope.inputVO.dmt_bond != undefined) ||
			    ($scope.inputVO.frn_bond != "" && $scope.inputVO.frn_bond != undefined) ||
			    ($scope.inputVO.dmt_balanced != "" && $scope.inputVO.dmt_balanced != undefined) ||
			    ($scope.inputVO.frn_balanced != "" && $scope.inputVO.frn_balanced != undefined)){
	    		
	    		if(($scope.inputVO.dmt_stock == "" || $scope.inputVO.dmt_stock == undefined) ||
    				($scope.inputVO.frn_stock == "" || $scope.inputVO.frn_stock == undefined) ||
    				($scope.inputVO.dmt_bond == "" || $scope.inputVO.dmt_bond == undefined) ||
    			    ($scope.inputVO.frn_bond == "" || $scope.inputVO.frn_bond == undefined) ||
    			    ($scope.inputVO.dmt_balanced == "" || $scope.inputVO.dmt_balanced == undefined) ||
    			    ($scope.inputVO.frn_balanced == "" || $scope.inputVO.frn_balanced == undefined)){
		    			$scope.actionFlag = false;
		    			$scope.showErrorMsg("申請基金適用折數需皆有值");
		    			return;
	    		}
	    			
	    	}
	    	
	    	if (($scope.inputVO.buy_hk_mrk != "" && $scope.inputVO.buy_hk_mrk != undefined) || 
				($scope.inputVO.sell_hk_mrk != "" && $scope.inputVO.sell_hk_mrk != undefined) || 
				($scope.inputVO.buy_us_mrk != "" && $scope.inputVO.buy_us_mrk != undefined) ||
				($scope.inputVO.sell_us_mrk != "" && $scope.inputVO.sell_us_mrk != undefined) ||
				($scope.inputVO.buy_uk_mrk != "" && $scope.inputVO.buy_uk_mrk != undefined) ||
				($scope.inputVO.sell_uk_mrk != "" && $scope.inputVO.sell_uk_mrk != undefined) ||
				($scope.inputVO.buy_jp_mrk != "" && $scope.inputVO.buy_jp_mrk != undefined) ||
				($scope.inputVO.sell_jp_mrk != "" && $scope.inputVO.sell_jp_mrk != undefined)) {
	    		
	    		if (($scope.inputVO.buy_hk_mrk == "" || $scope.inputVO.buy_hk_mrk == undefined) || 
    				($scope.inputVO.sell_hk_mrk == "" || $scope.inputVO.sell_hk_mrk == undefined) || 
    				($scope.inputVO.buy_us_mrk == "" || $scope.inputVO.buy_us_mrk == undefined) ||
    				($scope.inputVO.sell_us_mrk == "" || $scope.inputVO.sell_us_mrk == undefined) ||
    				($scope.inputVO.buy_uk_mrk == "" || $scope.inputVO.buy_uk_mrk == undefined) ||
    				($scope.inputVO.sell_uk_mrk == "" || $scope.inputVO.sell_uk_mrk == undefined) ||
    				($scope.inputVO.buy_jp_mrk == "" || $scope.inputVO.buy_jp_mrk == undefined) ||
    				($scope.inputVO.sell_jp_mrk == "" || $scope.inputVO.sell_jp_mrk == undefined)) {
		    			$scope.actionFlag = false;
		    			$scope.showErrorMsg("申請海外ETF/股票適用折數需皆有值");
		    			return;
	    		}
	    	}
	    	
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
				($scope.inputVO.sell_us_mrk != "" && $scope.inputVO.sell_us_mrk != undefined) ||
				($scope.inputVO.buy_uk_mrk != "" && $scope.inputVO.buy_uk_mrk != undefined) ||
				($scope.inputVO.sell_uk_mrk != "" && $scope.inputVO.sell_uk_mrk != undefined) ||
				($scope.inputVO.buy_jp_mrk != "" && $scope.inputVO.buy_jp_mrk != undefined) ||
				($scope.inputVO.sell_jp_mrk != "" && $scope.inputVO.sell_jp_mrk != undefined)) {
	    		//do 海外ETF/股票
	    		$scope.inputVO.applyPeriod_2 = 'Y';
	    		$scope.inputVO.highest_auth_lv_2 = $scope.highest_auth_lv_2;
	    	} else {
	    		$scope.inputVO.applyPeriod_2 = 'N';
	    	}
	    	
	    	if ($scope.inputVO.applyPeriod_1 == 'Y' || $scope.inputVO.applyPeriod_2 == 'Y') {
	    		$scope.sendRecv("CRM421", "applyPeriod", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", $scope.inputVO,
	        			function(totas, isError) {
	            			if (isError) {
	            				$scope.actionFlag = false;
//	            				$scope.showErrorMsgInDialog(totas.body.msgData);
	            				return;
	    	                }
	    	                if (totas.length > 0) {
	    	                	if (totas[0].body.errorMsg != null) {
	    	                		$scope.showErrorMsg(totas[0].body.errorMsg);
	    	                		$scope.actionFlag = false;
	    	                	} else {
	    		                	$scope.closeThisDialog('successful');
	    	                	}
	    	                };
	    	            }
	    		);
	    	}
	    }
	    
	    /*
	     * 判斷當前最低折數
	     * 
	     * 2016-10-11 add by walala
	     * 2016-12-01 modify by ocean : 若其中一欄未填，則先不查找授權等級
	     * 
	     */
	    $scope.getHighest_auth_lv = function(type) {
	    	if($scope.con_degree == null){
	    		$scope.inputVO.con_degree = "OTH";
	    	}else{
	    		$scope.inputVO.con_degree = $scope.con_degree;	    		
	    	}
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
		    	array1.push( parseFloat($scope.inputVO.dmt_stock), 
		    			     parseFloat($scope.inputVO.frn_stock), 
		    			     parseFloat($scope.inputVO.dmt_bond), 
		    			     parseFloat($scope.inputVO.frn_bond), 
		    			     parseFloat($scope.inputVO.dmt_balanced), 
		    			     parseFloat($scope.inputVO.frn_balanced));
		    			  
		    	$scope.inputVO.highest_auth_lv = _.min(array1);		//$scope.inputVO.highest_auth_lv ==> 此為最低折數
//		    	alert($scope.inputVO.highest_auth_lv);
		    	
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
				($scope.inputVO.sell_us_mrk != "" && $scope.inputVO.sell_us_mrk != undefined) ||
				($scope.inputVO.buy_uk_mrk != "" && $scope.inputVO.buy_uk_mrk != undefined) ||
				($scope.inputVO.sell_uk_mrk != "" && $scope.inputVO.sell_uk_mrk != undefined) ||
				($scope.inputVO.buy_jp_mrk != "" && $scope.inputVO.buy_jp_mrk != undefined) ||
				($scope.inputVO.sell_jp_mrk != "" && $scope.inputVO.sell_jp_mrk != undefined)) {
	    		var array2 = [];
		    	var temp2 = 0;
		    	array2.push(parseFloat($scope.inputVO.buy_hk_mrk), 
		    			    parseFloat($scope.inputVO.sell_hk_mrk), 
		    			    parseFloat($scope.inputVO.buy_us_mrk),
		    			    parseFloat($scope.inputVO.sell_us_mrk),
		    			    parseFloat($scope.inputVO.buy_uk_mrk),
		    			    parseFloat($scope.inputVO.sell_uk_mrk),
		    			    parseFloat($scope.inputVO.buy_jp_mrk),
		    			    parseFloat($scope.inputVO.sell_jp_mrk));
		    			  
		    	$scope.inputVO.highest_auth_lv = _.min(array2);		//$scope.inputVO.highest_auth_lv ==> 此為最低折數
		    	
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
								$scope.highest_auth_lv_name_2 = $scope.highest_lvList[0].HIGHEST_LV_NAME;
								temp2 = $scope.inputVO.highest_auth_lv;
								return;
							}
					});
		    	}
	    	}
	    };
	    
//	    $scope.goFamily = function() {
//	    	var cust_id = $scope.cust_id;
//	    	var dialog = ngDialog.open({
//				template: 'assets/txn/CRM421/CRM421_FamilyPeriod.html',
//				className: 'CRM421_FamilyPeriod',
//				showClose: false,
//                controller: ['$scope', function($scope) {
//                	$scope.cust_id = cust_id;
//                }]
//			});
//			dialog.closePromise.then(function (data) {
//				if(data.value === 'successful'){
//					$scope.inquireInit();
//            		$scope.inquire();
//				}
//			});
//	    }
});
