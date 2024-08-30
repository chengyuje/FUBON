/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM421_SinglePageController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $q, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM421_SinglePageController";

		// filter
		getParameter.XML(["CRM.CON_DEGREE", "CRM.VIP_DEGREE", "CRM.NF_TRUST_CURR_TYPE", "CRM.ETF_TRUST_CURR_TYPE", "CRM.SINGLE_REG_DISCOUNT", "CRM.SINGLE_TYPE", 'SOT.FUND_DECIMAL_POINT', 'SOT.ETF_DECIMAL_POINT'], function(totas) {
			if (totas) {
				$scope.mappingSet['CRM.CON_DEGREE'] = totas.data[totas.key.indexOf('CRM.CON_DEGREE')];
				$scope.mappingSet['CRM.VIP_DEGREE'] = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
				$scope.mappingSet['CRM.NF_TRUST_CURR_TYPE'] = totas.data[totas.key.indexOf('CRM.NF_TRUST_CURR_TYPE')];
				$scope.mappingSet['CRM.ETF_TRUST_CURR_TYPE'] = totas.data[totas.key.indexOf('CRM.ETF_TRUST_CURR_TYPE')];
				$scope.mappingSet['CRM.SINGLE_REG_DISCOUNT'] = totas.data[totas.key.indexOf('CRM.SINGLE_REG_DISCOUNT')];
				$scope.mappingSet['CRM.SINGLE_TYPE'] = totas.data[totas.key.indexOf('CRM.SINGLE_TYPE')];
				// 基金幣別小數位
				$scope.mappingSet['SOT.FUND_DECIMAL_POINT'] = totas.data[totas.key.indexOf('SOT.FUND_DECIMAL_POINT')];
				// ETF幣別小數位
				$scope.mappingSet['SOT.ETF_DECIMAL_POINT'] = totas.data[totas.key.indexOf('SOT.ETF_DECIMAL_POINT')]; 
			}
		});
	    //

//		$scope.mappingSet['APPLY_TYPE']=[];
//		$scope.mappingSet['APPLY_TYPE'].push({LABEL:"基金單筆申購" , DATA: '1'}, 
//											 {LABEL:"基金定期(不)定額申購" , DATA: '2'}, 
//											 {LABEL:"海外ETF/股票申購" , DATA: '4'});
	
		// init
		$scope.init = function(){
			$scope.inputVO = {
					cust_id : $scope.cust_id,
					prd_id : ''
			};
			$scope.isOBU = false;
			$confirm({text: '【金錢信託不適用「事先」議價授權申請及覆核】'}, {size: 'sm'}).then(function() {});
		};
		$scope.init();
		
		$scope.getOBUFlag = function(){
			$scope.sendRecv("SOT701", "getFP032675Data", "com.systex.jbranch.app.server.fps.sot701.SOT701InputVO", {custID : $scope.inputVO.cust_id },
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.fp032675DataVO) {
								$scope.isOBU = tota[0].body.fp032675DataVO.obuFlag == "Y" ? true : false;
	                		}
						}
			});	
		}
		$scope.getOBUFlag();
		
		$scope.initquery = function(){
			$scope.prod_id_1 = undefined;
			$scope.prod_name_1 = undefined;
			$scope.trustCurrType_1 = undefined;
			$scope.purchase_amt_1 = undefined;
			$scope.discount_type_1 = undefined;
			$scope.fee_rate_1 = undefined;
			$scope.discount_type_1 = undefined;
			$scope.fee_discount_1 = undefined;
			$scope.brg_reason_1 = undefined;
			
			$scope.prod_id_2 = undefined;
			$scope.prod_name_2 = undefined;
			$scope.trustCurrType_2 = undefined;
			$scope.purchase_amt_2 = undefined;
			$scope.fee_discount_2 = undefined;
			$scope.fee_2 = undefined;
			$scope.brg_reason_2 = undefined;
			
			$scope.prod_id_4 = undefined;
			$scope.prod_name_4 = undefined;
			$scope.trustCurrType_4 = undefined;
			$scope.entrust_unit_4 = undefined;
			$scope.entrust_amt_4 = undefined;
			$scope.purchase_amt_4 = undefined;
			$scope.discount_type_4 = undefined;
			$scope.fee_rate_4 = undefined;
			$scope.discount_type_4 = undefined;
			$scope.fee_discount_4 = undefined;
			$scope.brg_reason_4 = undefined;
			if($scope.isOBU && $scope.inputVO.apply_type == '2'){
				$scope.showErrorMsg("ehl_02_CRM421_009");
				$scope.inputVO.apply_type = undefined;
			}
			
		};

		// date picker
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		/*
		 * 查詢客戶資訊(進入頁面時觸發)
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

							$scope.inputVO.bra_nbr = $scope.custList[0].BRA_NBR;
							$scope.cust_id = $scope.custList[0].CUST_ID;
							$scope.cust_name = $scope.custList[0].CUST_NAME;
							$scope.con_degree = ($scope.custList[0].CON_DEGREE == null ? 'OTH' : $scope.custList[0].CON_DEGREE);
							$scope.vip_degree = $scope.custList[0].VIP_DEGREE;
							$scope.cust_aum = $scope.custList[0].AUM_AMT;
							$scope.y_profee = $scope.custList[0].Y_PROFEE;

							return;
						}
			});
	    };
	    $scope.initial();
		
	    $scope.inquirelist = function() {
	    	$scope.tempList = undefined;

	    	$scope.sendRecv("CRM421", "inquirelist", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(null == tota[0].body.tempList) {
                			return;
                		}
						$scope.tempList = tota[0].body.tempList;
						angular.forEach($scope.tempList, function(row){
							if ($scope.inputVO.apply_type == '4') {
								// ETF幣別小數位
								$scope.mod = $filter('filter')($scope.mappingSet['SOT.ETF_DECIMAL_POINT'], {DATA: row.TRUST_CURR});
							} else {
								// 基金幣別小數位
								$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: row.TRUST_CURR});
							}							
							row.num = $scope.mod[0].LABEL;
						});
						return;
					}
			});
	    }
	    $scope.inquirelist();

	    /*
	     * set inputVO 
	     * 
	     * 2016-12-05 modify by ocean : else if → switch
	     * 
	     */
	    $scope.setInputVO = function () {
	    	$scope.inputVO.cust_id = $scope.cust_id;
	    	
	    	switch ($scope.inputVO.apply_type) {
		    	case '1':
		    		if ($scope.prod_id_1) {
		    			$scope.inputVO.prod_id = $scope.prod_id_1.toUpperCase();
		    		}
		    		$scope.inputVO.prod_name = $scope.prod_name_1;
		    		$scope.inputVO.trust_curr = $scope.trust_curr_1;
		    		$scope.inputVO.purchase_amt = $scope.purchase_amt_1;
		    		$scope.inputVO.brg_reason = $scope.brg_reason_1;
		    		$scope.inputVO.trustCurrType = $scope.trustCurrType_1;
		    		
		    		if ($scope.discount_type_1 == '1') {
		    			$scope.inputVO.discount_type = '1';
		    		} else if ($scope.discount_type_1 == '2') {
		    			$scope.inputVO.discount_type = '2';
		    		}
		    		
		    		$scope.inputVO.fee_rate = $scope.fee_rate_1;
		    		$scope.inputVO.fee_discount = $scope.fee_discount_1;
		    		debugger;
		    		$scope.inputVO.fee = $scope.fee_1;
		    		$scope.inputVO.defaultFeeRate = $scope.defaultFeeRate_1;
		    		
		    		break;
		    	case '2':
		    		if ($scope.prod_id_2) {
		    			$scope.inputVO.prod_id = $scope.prod_id_2.toUpperCase();
		    		}
		    		$scope.inputVO.prod_name = $scope.prod_name_2;
		    		$scope.inputVO.trust_curr = $scope.trust_curr_2;
		    		$scope.inputVO.purchase_amt = $scope.purchase_amt_2;
		    		$scope.inputVO.brg_reason = $scope.brg_reason_2;
		    		$scope.inputVO.trustCurrType = $scope.trustCurrType_2;
		    		
		    		$scope.inputVO.discount_type = '2';
		    		
		    		$scope.inputVO.fee_rate = $scope.fee_rate_2;
		    		$scope.inputVO.fee_discount = parseFloat($scope.fee_discount_2);
		    		debugger;
		    		$scope.inputVO.fee = $scope.fee_2;
		    		$scope.inputVO.defaultFeeRate = $scope.defaultFeeRate_2;
		    		
		    		break;
		    	case '4':
		    		if ($scope.prod_id_4) {
		    			$scope.inputVO.prod_id = $scope.prod_id_4.toUpperCase();
		    		}
		    		$scope.inputVO.prod_name = $scope.prod_name_4;
		    		$scope.inputVO.entrust_unit = $scope.entrust_unit_4;
		    		$scope.inputVO.entrust_amt = $scope.entrust_amt_4;

		    		if ($scope.entrust_unit_4 != undefined && $scope.entrust_amt_4 != undefined) {
			    		$scope.purchase_amt_4 = $scope.entrust_unit_4 * $scope.entrust_amt_4;
			    		$scope.inputVO.purchase_amt = $scope.purchase_amt_4;
		    		} else {
		    			$scope.inputVO.purchase_amt = $scope.purchase_amt_4;
		    		}
		    		
		    		$scope.inputVO.brg_reason = $scope.brg_reason_4;
		    		$scope.inputVO.trustCurrType = $scope.trustCurrType_4;
		    		// 2017/6/26
		    		$scope.inputVO.trust_curr = $scope.trust_curr_4;
		    		
		    		if ($scope.discount_type_4 == '1') {
		    			$scope.inputVO.discount_type = '1';
		    		} else if ($scope.discount_type_4 == '2') {
		    			$scope.inputVO.discount_type = '2';
		    		}
		    		
		    		$scope.inputVO.fee_rate = $scope.fee_rate_4;
		    		$scope.inputVO.fee_discount = $scope.fee_discount_4;
		    		debugger;
		    		$scope.inputVO.fee = $scope.fee_4;
		    		$scope.inputVO.defaultFeeRate = $scope.defaultFeeRate_4;
		    		
		    		break;
	    	}
	    	
	    	$scope.inputVO.highest_auth_lv = $scope.highest_auth_lv;
	    };
	    
	    $scope.clearFee = function () {
	    	$scope.highest_auth_lv = undefined;
	    	$scope.highest_auth_lv_name_1 = undefined;
	    	
	    	switch ($scope.inputVO.apply_type) {
				case '1':
					$scope.discount_type_1 = undefined;
					$scope.defaultFeeRate_1 = undefined;
					$scope.fee_rate_1 = undefined;
		    		$scope.fee_discount_1 = undefined;
		    		$scope.fee_1 = undefined;
		    		
					break;
				case '2':
					$scope.defaultFeeRate_2 = undefined;
					$scope.fee_rate_2 = undefined;
		    		$scope.fee_discount_2 = parseFloat('');
		    		$scope.fee_2 = undefined;
		    		
					break;
				case '4':
					$scope.defaultFeeRate_4 = undefined;
					$scope.fee_rate_4 = undefined;
		    		$scope.fee_discount_4 = undefined;
		    		$scope.fee_4 = undefined;
		    		
					break;
			}
	    };
	    
	    /*
	     * 取得表定手續費率
	     * 
	     * 2016-12-01 add by ocean
	     * 
	     */
	    $scope.getDefaultFeeRate = function () {
	    	if($scope.inputVO.apply_type == '' || $scope.inputVO.apply_type == undefined){
	    		return;
	    	}
	    	
	    	if($scope.isOBU && $scope.trustCurrType_1 == 'N'){
	    		$scope.showErrorMsg("ehl_01_SOT110_001");
	    		$scope.trustCurrType_1 = undefined;
	    		return;
	    	}

	    	var deferred = $q.defer();
	    	
	    	$scope.setInputVO();
	    	if (!$scope.modifyDisabled) {
				$scope.clearFee();
	    	}
	    	
			if ($scope.inputVO.purchase_amt != undefined && $scope.inputVO.purchase_amt != '' && $scope.inputVO.purchase_amt != null &&
				$scope.inputVO.trustCurrType != undefined && $scope.inputVO.trustCurrType != '' && $scope.inputVO.trustCurrType != null){
				$scope.sendRecv("CRM421", "getDefaultFeeRate", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if (tota.length > 0) {
				                	if (tota[0].body.errorMsg != null && tota[0].body.errorMsg != "") {
				                		$scope.showErrorMsg(tota[0].body.errorMsg);
				                	} else {
				                		switch ($scope.inputVO.apply_type) {
											case '1':
												$scope.defaultFeeRate_1 = tota[0].body.defaultFeeRate;
												break;
											case '2':
												$scope.defaultFeeRate_2 = tota[0].body.defaultFeeRate;
												break;
											case '4':
												$scope.defaultFeeRate_4 = tota[0].body.defaultFeeRate;
												break;
										}
				                	}
				                	deferred.resolve("success");
									return deferred.promise;
				                };
				                
				                deferred.resolve("");
								return deferred.promise;
							}
				});
			}
			
			return deferred.promise;
		};
		
		/*
		 * 商品查詢
		 * 
		 * 2016-10-11 add by walala
		 * 2016-12-05 modify by ocean : else if → switch, add 表定手續費率電文
		 * 
		 */
		$scope.prod_inquire = function() {
			var deferred = $q.defer();
			
			if ($scope.singleDTL == undefined) { 
				$scope.clearFee();
				$scope.purchase_amt_1 = undefined;
			}
			
			if ($scope.inputVO.apply_type == '' || $scope.inputVO.apply_type == undefined) {
				deferred.resolve("");
				return deferred.promise;
			}

			switch ($scope.inputVO.apply_type) {
				case '1':
					if($scope.prod_id_1 == "" || $scope.prod_id_1 == undefined){
						$scope.clearNow();
						return;
					}
					if ($scope.prod_id_1) {
						$scope.inputVO.prod_id = $scope.prod_id_1.toUpperCase();
					}
					$scope.prod_name_1 = '';
					$scope.trust_curr_1 = '';
					break;
				case '2':
					if($scope.prod_id_2 == "" || $scope.prod_id_2 == undefined){
						$scope.clearNow();
						return;
					}
					if ($scope.prod_id_2) {
						$scope.inputVO.prod_id = $scope.prod_id_2.toUpperCase();
					}
					$scope.prod_name_2 = '';
					$scope.trust_curr_2 = '';
					break;
				case '4':
					if($scope.prod_id_4 == "" || $scope.prod_id_4 == undefined){
						$scope.clearNow();
						return;
					}
					if ($scope.prod_id_4) {
						$scope.inputVO.prod_id = $scope.prod_id_4.toUpperCase();
					}
					$scope.prod_name_4 = '';
					$scope.trust_curr_4 = '';
					break;
			}
			
			if ($scope.inputVO.prod_id == '' || $scope.inputVO.prod_id == undefined) {
				deferred.resolve("");
				return deferred.promise;
			}
			
			$scope.highest_auth_lv = undefined;
			$scope.highest_auth_lv_name_1 = undefined;
		    	
			$scope.sendRecv("CRM421", "prod_inquire", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.errorMsg != null && tota[0].body.errorMsg != "") {
								//適配有錯誤訊息
								$scope.showErrorMsg(tota[0].body.errorMsg);
								
								if ($scope.singleDTL != undefined) { 
									$scope.getUpdateDTL();
								}else{
									$scope.clearNow();									
								}
								
							} else if(tota[0].body.prodList == null) {
								$scope.showMsg("ehl_01_common_009");
								
								deferred.resolve("");
								return deferred.promise;
							} else if (tota[0].body.prodList != null && tota[0].body.prodList.length > 0) {
								$scope.prodList = tota[0].body.prodList;
								
								if ($scope.prodList.length > 0) {
									switch ($scope.inputVO.apply_type) {
										case '1':
											$scope.prod_name_1 = $scope.prodList[0].FUND_CNAME;
											$scope.trust_curr_1 = $scope.prodList[0].CURRENCY_STD_ID;
											$scope.trustCurrType_1 = ($scope.prodList[0].TRUST_CURR_TYPE == "C") ? "C" : "N";
											$scope.mappingSet['TRUST_CURR_TYPE'] = [];
											if ($scope.trustCurrType_1 == "C") {
												$scope.mappingSet['TRUST_CURR_TYPE'] = $scope.mappingSet['CRM.NF_TRUST_CURR_TYPE'];
											} else {
												$scope.mappingSet['TRUST_CURR_TYPE'] = $scope.mappingSet['CRM.ETF_TRUST_CURR_TYPE'];
											}
											break;
										case '2':
											$scope.prod_name_2 = $scope.prodList[0].FUND_CNAME;
											$scope.trust_curr_2 = $scope.prodList[0].CURRENCY_STD_ID;
											$scope.trustCurrType_2 = ($scope.prodList[0].TRUST_CURR_TYPE == "C") ? "C" : "N";
											if ($scope.trustCurrType_2 == "C") {
												$scope.mappingSet['TRUST_CURR_TYPE'] = $scope.mappingSet['CRM.NF_TRUST_CURR_TYPE'];
											} else {
												$scope.mappingSet['TRUST_CURR_TYPE'] = $scope.mappingSet['CRM.ETF_TRUST_CURR_TYPE'];
											}
											break;
										case '4':
											$scope.prod_name_4 = $scope.prodList[0].PROD_NAME;
											$scope.trust_curr_4 = $scope.prodList[0].CURRENCY_STD_ID;
											$scope.trustCurrType_4 = "N";
											break;
									}
									
									deferred.resolve("success");
									return deferred.promise;
								} else {
									$scope.clearNow();
									$scope.showMsg("ehl_01_common_009");
								}

								deferred.resolve("");
								return deferred.promise;
							}
						}
			});
			
			return deferred.promise;
		};
		
		/*
		 * 本月主推/商品搜尋
		 * 
		 * 2016-10-11 add by walala
		 * 2016-12-01 modify by ocean : 傳值、導頁 修正
		 * 
		 */
		$scope.searchProd = function(main_prd, prd_type) {
			var cust_id = $scope.inputVO.cust_id;
			var apply_type = $scope.inputVO.apply_type;
			var mainYN = main_prd;
			var pt = prd_type;
			var dialog = ngDialog.open({
				template: (apply_type == '1' || apply_type == '2' ? 'assets/txn/CRM421/CRM421_PRD110_ROUTE.html' : 'assets/txn/CRM421/CRM421_PRD120_ROUTE.html'),
				className: (apply_type == '1' || apply_type == '2' ? 'PRD110' : 'PRD120'),
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					if(mainYN == 'Y'){
						$scope.txnName = "本月主推";
						$scope.main_prd = (mainYN == 'Y' ? mainYN : '');
					}else{
						$scope.txnName = (apply_type == '1' || apply_type == '2' ? "基金搜尋" : (pt == 'ETF') ? "ETF搜尋" : '股票搜尋');
					}
					$scope.txnId = "CRM";
					$scope.isPop = true;
	        		$scope.routeURL = (apply_type == '1' || apply_type == '2' ? 'assets/txn/PRD110/PRD110.html' : (pt == 'ETF') ? 'assets/txn/PRD120/PRD120_ETF.html' : 'assets/txn/PRD120/PRD120_STOCK.html');
	        		$scope.tradeType = (apply_type == '1' ? '1' : (apply_type == '2' ? '5' : '1'));
	        		$scope.cust_id = cust_id;
	            }]
			}).closePromise.then(function (data) {
				switch ($scope.inputVO.apply_type) {
					case '1':
						$scope.prod_id_1 = data.value.PRD_ID;
						
						break;
					case '2': 
						$scope.prod_id_2 = data.value.PRD_ID;
						
						break;
					case '4':
						$scope.prod_id_4 = data.value.PRD_ID;
						
						break;
				}
				
				$scope.prod_inquire();
			});
		};
		
		/*
		 * 折扣數/費率/手續費 連動
		 * 
		 * 2016-12-05 add by ocean
		 * 
		 */
		$scope.getFee = function(type) {
			$scope.highest_auth_lv = undefined;
			$scope.highest_auth_lv_name_1 = undefined;
			
			switch ($scope.inputVO.apply_type) {
				case '1':
					if (type == 'rate' && ($scope.defaultFeeRate_1 != undefined && $scope.fee_rate_1 != undefined)) {
						$scope.fee_discount_1 = (($scope.fee_rate_1 / $scope.defaultFeeRate_1) * 10).toFixed(2); //折數 = 手續費率(number) ÷ 表定手續費率 × 10
					} else if ($scope.defaultFeeRate_1 != undefined && $scope.fee_discount_1 != undefined) {
						$scope.fee_rate_1 = (($scope.fee_discount_1 * $scope.defaultFeeRate_1) / 10).toFixed(2); //手續費率 = (折數(number) × 表定手續費) ÷ 10
					}
					
					if ($scope.trustCurrType_1 == 'C' || $scope.trustCurrType_1 == 'N') {
						// 台幣信託業務
						$scope.num_1 = 0;
					} else if ($scope.trustCurrType_1 == 'Y') {
						// 外幣信託業務
						$scope.mod_1 = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.trust_curr_1});
						// 隨幣別控制小數位數
						$scope.num_1 = $scope.mod_1[0].LABEL;
					}					
    				
					if ($scope.purchase_amt_1 != undefined && $scope.fee_rate_1 != undefined) {
						$scope.fee_1 = (($scope.purchase_amt_1 * $scope.fee_rate_1) / 100).toFixed($scope.num_1); //手續費金額= 信託本金 × 手續費率
					}
					
					break;
				case '2': 
					if (type == 'rate' && ($scope.defaultFeeRate_2 != undefined && $scope.fee_rate_2 != undefined)) {
						$scope.fee_discount_2 = (($scope.fee_rate_2 / $scope.defaultFeeRate_2) * 10).toFixed(2); //折數 = 手續費率(number) ÷ 表定手續費率 × 10
						debugger;
					} else if ($scope.defaultFeeRate_2 != undefined && $scope.fee_discount_2 != undefined && $scope.fee_discount_2 != '') {
						$scope.fee_rate_2 = ((parseFloat($scope.fee_discount_2) * $scope.defaultFeeRate_2) / 100).toFixed(2); //手續費率 = (折數(number) × 表定手續費) ÷ 100
						debugger;
					}
					
					if ($scope.trustCurrType_2 == 'C' || $scope.trustCurrType_2 == 'N') {
						// 台幣信託業務
						$scope.num_2 = 0;						
					} else if ($scope.trustCurrType_2 == 'Y') {
						// 外幣信託業務
						$scope.mod_2 = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.trust_curr_2});
						// 隨幣別控制小數位數
						$scope.num_2 = $scope.mod_2[0].LABEL;
					}					
    				
					if ($scope.purchase_amt_2 != undefined && $scope.fee_rate_2 != undefined) {
						$scope.fee_2 = (($scope.purchase_amt_2 * $scope.fee_rate_2) / 100).toFixed($scope.num_2); //手續費金額= 信託本金 × 手續費率
					}
					 					
					break;
				case '4':
					if (type == 'rate' && ($scope.defaultFeeRate_4 != undefined && $scope.fee_rate_4 != undefined)) {
						$scope.fee_discount_4 = (($scope.fee_rate_4 / $scope.defaultFeeRate_4) * 10).toFixed(2); //折數 = 手續費率(number) ÷ 表定手續費率 × 10
					} else if ($scope.defaultFeeRate_4 != undefined && $scope.fee_discount_4 != undefined) {
						$scope.fee_rate_4 = (($scope.fee_discount_4 * $scope.defaultFeeRate_4) / 10).toFixed(2); //手續費率 = (折數(number) × 表定手續費) ÷ 10
					}
					
					if ($scope.trustCurrType_4 == 'C' || $scope.trustCurrType_4 == 'N') {
						// 台幣信託業務
						$scope.num_4 = 0;
					} else if ($scope.trustCurrType_4 == 'Y') {
						// 外幣信託業務
						$scope.mod_4 = $filter('filter')($scope.mappingSet['SOT.ETF_DECIMAL_POINT'], {DATA: $scope.trust_curr_4});
						// 隨幣別控制小數位數
						$scope.num_4 = $scope.mod_4[0].LABEL;
					}
					
					if ($scope.purchase_amt_4 != undefined && $scope.fee_rate_4 != undefined) {
						$scope.fee_4 = (($scope.purchase_amt_4 * $scope.fee_rate_4) / 100).toFixed($scope.num_4); //手續費金額= 信託本金 × 手續費率
					}
					
					break;
			}
			
			$scope.inputVO.prod_type = ($scope.inputVO.apply_type == '1' || $scope.inputVO.apply_type == '2' ? "1" : "2");
			$scope.inputVO.con_degree = $scope.con_degree;
			
			//$scope.inputVO.highest_auth_lv ==> 此為"折扣數"
			if ($scope.inputVO.apply_type == '1') {
				$scope.inputVO.highest_auth_lv = $scope.fee_discount_1;
			} else if ($scope.inputVO.apply_type == '2') {
				if (isNaN($scope.fee_discount_2) ||  $scope.fee_discount_2 == '') {
					$scope.inputVO.highest_auth_lv = undefined;
				} else {
					$scope.inputVO.highest_auth_lv = parseFloat($scope.fee_discount_2 / 10);
				}
			} else if ($scope.inputVO.apply_type == '4') {
				$scope.inputVO.highest_auth_lv = $scope.fee_discount_4;
			} else {
				$scope.inputVO.highest_auth_lv = undefined;
			}
									
			// 取得最高授權等級
			if ($scope.inputVO.prod_type != undefined && $scope.inputVO.con_degree != undefined && $scope.inputVO.highest_auth_lv != undefined) {
				if($scope.inputVO.apply_type == '4'){
					$scope.inputVO.prod_id = $scope.prod_id_4.toUpperCase();
				}
		    	$scope.sendRecv("CRM421", "getHighest_auth_lv", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.highest_lvList == null) {
//										$scope.showMsg("ehl_01_common_009");
		                			return;
		                		}
								$scope.highest_lvList = tota[0].body.highest_lvList;
								$scope.highest_auth_lv = $scope.highest_lvList[0].HIGHEST_LV;
								$scope.highest_auth_lv_name_1 = $scope.highest_lvList[0].HIGHEST_LV_NAME;

								return;
							}
					}
		    	);
			}
		};
		
		/*
		 * 清除
		 * 
		 * 2016-12-01 add by ocean
		 * 
		 */
		$scope.clearNow = function() { 
			switch ($scope.inputVO.apply_type) {
				case '1':
					$scope.prod_id_1 = undefined;
					$scope.prod_name_1 = undefined;
					$scope.trust_curr_1 = undefined;
					$scope.purchase_amt_1 = undefined;
					$scope.brg_reason_1 = undefined;
					$scope.trustCurrType_1 = undefined;
					$scope.clearFee();
					
					break;
				case '2': 
					$scope.prod_id_2 = undefined;
					$scope.prod_name_2 = undefined;
					$scope.trust_curr_2 = undefined;
					$scope.purchase_amt_2 = undefined;
					$scope.brg_reason_2 = undefined;
					$scope.trustCurrType_2 = undefined;
					$scope.clearFee();
					
					break;
				case '4':
					$scope.prod_id_4 = undefined;
					$scope.prod_name_4 = undefined;
					$scope.trustCurrType_4 = undefined;
		    		$scope.entrust_unit_4 = undefined;
		    		$scope.entrust_amt_4 = undefined;
		    		$scope.purchase_amt_4 = undefined;
		    		$scope.brg_reason_4 = undefined;
					$scope.clearFee();
					
					break;
			}
		};
		
		/*
		 * 加入清單
		 * 
		 * 2016-10-11 add by walala
		 * 2016-12-05 modify by ocean
		 */
	    $scope.addToList = function() {
	    	$scope.setInputVO();
	    	
	    	$scope.sendRecv("CRM421", "addToList", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", $scope.inputVO,
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    	}
                    	if (totas.length > 0) {
                    		if (totas[0].body.errorMsg != null) {
    	                		$scope.showErrorMsg(totas[0].body.errorMsg);
    	                	} else {
                        		$scope.showSuccessMsg("ehl_01_common_001");
                        		$scope.inputVO.applySeqList = totas[0].body.applySeqList;
//                        		$scope.clearNow();
	                        	switch ($scope.inputVO.apply_type) {
	                				case '1':
	                					$scope.prod_id_1 = undefined;
	                					$scope.prod_name_1 = undefined;
	                					$scope.trust_curr_1 = undefined;
	                					$scope.purchase_amt_1 = undefined;
	                					$scope.brg_reason_1 = undefined;
	                					$scope.trustCurrType_1 = undefined;
	                					$scope.clearFee();
	                					
	                					break;
	                				case '2': 
	                					$scope.prod_id_2 = undefined;
	                					$scope.prod_name_2 = undefined;
	                					$scope.trust_curr_2 = undefined;
	                					$scope.purchase_amt_2 = undefined;
	                					$scope.brg_reason_2 = undefined;
	                					$scope.trustCurrType_2 = undefined;
	                					$scope.clearFee();
	                					
	                					break;
	                				case '4':
	                					$scope.prod_id_4 = undefined;
	                					$scope.prod_name_4 = undefined;
	                					$scope.trustCurrType_4 = undefined;
	                		    		$scope.entrust_unit_4 = undefined;
	                		    		$scope.entrust_amt_4 = undefined;
	                		    		$scope.purchase_amt_4 = undefined;
	                		    		$scope.brg_reason_4 = undefined;
	                					$scope.clearFee();
	                					
	                					break;
	                			}
                        		$scope.inquirelist();
    	                	}
                    	};
    				}
    		);
	    };
	    
	    /*
		 * 刪除購物車某一筆
		 * 
		 * 2016-10-11 add by walala
		 * 2016-12-05 modify by ocean
		 */
	    $scope.deletelist = function(row) {
	    	$scope.inputVO.seq = row.APPLY_SEQ;
	    	$scope.sendRecv("CRM421", "deletelist", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", $scope.inputVO,
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    	}
                    	if (totas.length > 0) {
                    		$scope.showSuccessMsg("ehl_01_common_003");
                    		$scope.inputVO.applySeqList = totas[0].body.applySeqList;
                    		$scope.inquirelist();
                    	};
    				}
    		);
	    };
	    
	    /*
	     * 送出審核
	     * 
	     * 2016-10-11 add by walala
		 * 2016-12-05 modify by ocean
	     * 
	     */
	    $scope.applySingle = function() {
	    	$scope.sendRecv("CRM421", "applySingle", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", {'list': $scope.tempList},
    			function(totas, isError) {
        			if (isError) {
        				$scope.showErrorMsgInDialog(totas.body.msgData);
        				return;
	                }
	                if (totas.length > 0) {
	                	$scope.showSuccessMsg("ehl_01_common_006");
	                	$scope.closeThisDialog('successful');
	                };
	            }
	    	);
	    }
	    
	    /*
	     * 修改
	     * 
	     * 2016-12-27 add by ocean
	     */
	    $scope.getUpdateDTL = function () {
	    	if ($scope.singleDTL != undefined) { 
		    	$scope.modifyDisabled = true;
		    	$scope.inputVO.seq = $scope.singleDTL.APPLY_SEQ;
		    	$scope.inputVO.apply_type = $scope.singleDTL.APPLY_TYPE;
		    	
		    	//商品資訊
		    	switch ($scope.inputVO.apply_type) {
			    	case '1':
						$scope.prod_id_1 = $scope.singleDTL.PROD_ID;
						
						break;
					case '2': 
						$scope.prod_id_2 = $scope.singleDTL.PROD_ID;
						
						
						break;
					case '4':
						$scope.prod_id_4 = $scope.singleDTL.PROD_ID;
						
						break;
		    	}
		    	
		    	$scope.prod_inquire().then(function(data) {
		    		//申請資訊
			    	switch ($scope.inputVO.apply_type) {
				    	case '1':
				    		//基金單筆申購
				    		$scope.trustCurrType_1 = $scope.singleDTL.TRUST_CURR_TYPE;
				    		$scope.purchase_amt_1 = ($scope.singleDTL.INVEST_AMT == undefined ? parseFloat($scope.singleDTL.PURCHASE_AMT) : parseFloat($scope.singleDTL.INVEST_AMT));
				    		$scope.discount_type_1 = $scope.singleDTL.DISCOUNT_TYPE;
//				    		$scope.fee_rate_1 = parseFloat($scope.singleDTL.DISCOUNT);
				    		$scope.fee_rate_1 = ($scope.singleDTL.DISCOUNT == undefined ? parseFloat($scope.singleDTL.FEE_RATE) : parseFloat($scope.singleDTL.DISCOUNT));
				    		$scope.fee_discount_1 = parseFloat($scope.singleDTL.DISCOUNT);
							$scope.brg_reason_1 = $scope.singleDTL.BRG_REASON;
							break;
						case '2': 
							//基金定期(不)定額申購
							$scope.trustCurrType_2 = $scope.singleDTL.TRUST_CURR_TYPE;
//							$scope.purchase_amt_2 = parseFloat($scope.singleDTL.INVEST_AMT);
							$scope.purchase_amt_2 = ($scope.singleDTL.INVEST_AMT == undefined ? parseFloat($scope.singleDTL.PURCHASE_AMT) : parseFloat($scope.singleDTL.INVEST_AMT));
//							$scope.fee_discount_2 = parseFloat($scope.singleDTL.DISCOUNT);	//因為CRM.SINGLE_REG_DISCOUNT的PARAM_CODE為10倍，故*10
//							$scope.fee_discount_2 = String ($scope.singleDTL.DISCOUNT).includes(".") ? parseFloat($scope.singleDTL.DISCOUNT)*10 : parseFloat($scope.singleDTL.DISCOUNT);
							$scope.fee_discount_2 = parseFloat($scope.singleDTL.DISCOUNT) > 10 ? parseFloat($scope.singleDTL.DISCOUNT) : parseFloat($scope.singleDTL.DISCOUNT)*10;
							debugger;
							break;
						case '4':
							//海外ETF/股票
							$scope.trustCurrType_4 = $scope.singleDTL.TRUST_CURR_TYPE;
							$scope.entrust_unit_4 = $scope.singleDTL.ENTRUST_UNIT;
							$scope.entrust_amt_4 = $scope.singleDTL.ENTRUST_AMT;
							$scope.discount_type_4 = $scope.singleDTL.DISCOUNT_TYPE;
							$scope.fee_rate_4 = $scope.singleDTL.FEE_RATE;
							$scope.fee_discount_4 = $scope.singleDTL.FEE_DISCOUNT;
							$scope.brg_reason_4 = $scope.singleDTL.BRG_REASON;
							
							break;
			    	}
			    	
			    	$scope.getDefaultFeeRate().then(function(data) {
			    		$scope.getFee(($scope.singleDTL.DISCOUNT_TYPE == '1' ? 'rate' : 'discount'));
			    	});
		    	});
		    	
		    } else {
		    	$scope.modifyDisabled = false;
		    }
	    };
	    $scope.getUpdateDTL();
	    
	    /*
	     * 送出修改
	     * 
	     * 2016-12-27 add by ocean
	     * 
	     */
	    $scope.updateApplyFlow = function () {
	    	$scope.setInputVO();
	    	
	    	$scope.sendRecv("CRM421", "updateApplyFlow", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", $scope.inputVO,
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    	}
                    	if (totas.length > 0) {
                    		if (totas[0].body.errorMsg != null) {
    	                		$scope.showErrorMsg(totas[0].body.errorMsg);
    	                	} else {
    	                		$scope.showSuccessMsg("ehl_01_common_006");
    		                	$scope.closeThisDialog('successful');
    	                	}
                    	};
    				}
    		);
	    };

});
