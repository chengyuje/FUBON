/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM421_updateSingleController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM421_updateSingleController";

		// filter
		getParameter.XML(["CRM.CON_DEGREE", "CRM.VIP_DEGREE", "CRM.NF_TRUST_CURR_TYPE", "CRM.ETF_TRUST_CURR_TYPE"], function(totas) {
			if (totas) {
				$scope.mappingSet['CRM.CON_DEGREE'] = totas.data[totas.key.indexOf('CRM.CON_DEGREE')];
				$scope.mappingSet['CRM.VIP_DEGREE'] = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
				$scope.mappingSet['CRM.NF_TRUST_CURR_TYPE'] = totas.data[totas.key.indexOf('CRM.NF_TRUST_CURR_TYPE')];
				$scope.mappingSet['CRM.ETF_TRUST_CURR_TYPE'] = totas.data[totas.key.indexOf('CRM.ETF_TRUST_CURR_TYPE')];
			}
		});
	    //
		
		$scope.mappingSet['APPLY_TYPE']=[];
		$scope.mappingSet['APPLY_TYPE'].push({LABEL:"基金單筆申購" , DATA: '1'}, {LABEL:"基金定期(不)定額申購" , DATA: '2'}, {LABEL:"基金動態鎖利" , DATA: '6'}, {LABEL:"海外ETF/股票申購" , DATA: '4'});
		
		// init
		$scope.init = function(){
			$scope.inputVO = {
					cust_id : $scope.cust_id,
					seq : $scope.seq
			};
		};
		$scope.init();

		// date picker
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		/*
		 * 本月主推/商品搜尋
		 * 
		 * 2016-10-11 add by walala
		 * 2016-12-01 modify by ocean : 傳值、導頁 修正
		 * 
		 */
		$scope.searchProd = function(main_prd) {
			var cust_id = $scope.inputVO.cust_id;
			var apply_type = $scope.inputVO.apply_type;
			var mainYN = main_prd;
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM421/CRM421_searchProd.html',
				className: (apply_type == '1' || apply_type == '2' || apply_type == '6' ? 'PRD110' : 'PRD120'),
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					if(apply_type == "6") {
						$scope.txnName = "搜尋母基金";
						$scope.dynamicType = "M"; //動態鎖利類別 M:母基金 C:子基金
					} else {
						if(mainYN){
							$scope.txnName = "本月主推";
						}else{
							$scope.txnName = "搜尋基金";
						}
					}
					$scope.isPop = true;
	        		$scope.routeURL = (apply_type == '1' || apply_type == '2' || apply_type == '6' ? 'assets/txn/PRD110/PRD110.html' : 'assets/txn/PRD120/PRD120.html');
	        		$scope.tradeType = ((apply_type == '1' || apply_type == '6') ? '1' : (apply_type == '2' ? '5' : '1'));
	        		$scope.cust_id = cust_id
	        		$scope.main_prd = (mainYN == 'Y' ? mainYN : '');
	            }]
			}).closePromise.then(function (data) {
				if (apply_type == '1' || apply_type == '6') {
					$scope.prod_id_1 = data.value.PRD_ID;
				} else if (apply_type == '2') {
					$scope.prod_id_2 = data.value.PRD_ID;
				} else {
					$scope.prod_id_4 = data.value.PRD_ID;
				}
				$scope.prod_inquire();
			});
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
							
							$scope.cust_id = $scope.custList[0].CUST_ID;
							$scope.cust_name = $scope.custList[0].CUST_NAME;
							$scope.con_degree = $scope.custList[0].CON_DEGREE;
							$scope.vip_degree = $scope.custList[0].VIP_DEGREE;
							$scope.cust_aum = $scope.custList[0].AUM_AMT;
							$scope.y_profee = $scope.y_profeeList[0].Y_PROFEE;
							return;
						}
			});
	    };
	    $scope.initial();
	    
	    $scope.update_initial = function() {
	    	$scope.sendRecv("CRM421", "update_initial", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.updateList.length == 0) {
//							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						$scope.updateList = tota[0].body.updateList;
						
						if ($scope.updateList[0].APPLY_TYPE == '1' || $scope.updateList[0].APPLY_TYPE == '6') {
				    		
				    		$scope.prod_id_1 = $scope.updateList[0].PROD_ID;
				    		$scope.prod_name_1 = $scope.updateList[0].PROD_NAME;
				    		$scope.trust_curr_1 = $scope.updateList[0].TRUST_CURR;
				    		$scope.purchase_amt_1 = $scope.updateList[0].PURCHASE_AMT;
				    		$scope.discount_type_1 = $scope.updateList[0].DISCOUNT_TYPE;
				    		$scope.fee_rate_1 = $scope.updateList[0].FEE_RATE;
				    		$scope.fee_discount_1 = $scope.updateList[0].FEE_RATE;
				    		$scope.fee_1 = $scope.updateList[0].FEE;
				    		
				    	} else if ($scope.updateList[0].APPLY_TYPE == '2') {
				    		
				    		$scope.prod_id_2 = $scope.updateList[0].PROD_ID;
				    		$scope.prod_name_2 = $scope.updateList[0].PROD_NAME;
				    		$scope.trust_curr_2 = $scope.updateList[0].TRUST_CURR;
				    		$scope.purchase_amt_2 = $scope.updateList[0].PURCHASE_AMT;
				    		$scope.discount_type_2 = $scope.updateList[0].DISCOUNT_TYPE;
				    		$scope.fee_discount_2 = $scope.updateList[0].FEE_RATE;
				    		$scope.fee_2 = $scope.updateList[0].FEE;
				    		
				    	} else if ($scope.updateList[0].APPLY_TYPE == '4') {
				    		
				    		$scope.prod_id_4 = $scope.updateList[0].PROD_ID;
				    		$scope.prod_name_4 = $scope.updateList[0].PROD_NAME;
				    		$scope.entrust_unit_4 = $scope.updateList[0].ENTRUST_UNIT
				    		$scope.entrust_amt_4 = $scope.updateList[0].ENTRUST_AMT
				    		$scope.purchase_amt_4 = $scope.updateList[0].PURCHASE_AMT;
				    		$scope.discount_type_4 = $scope.updateList[0].DISCOUNT_TYPE;
				    		$scope.fee_rate_4 = $scope.updateList[0].FEE_RATE;
				    		$scope.fee_discount_4 = $scope.updateList[0].FEE_DISCOUNT;
				    		$scope.fee_4 = $scope.updateList[0].FEE;
				    	}	
						$scope.highest_auth_lv = $scope.updateList[0].HIGHEST_AUTH_LV;
						$scope.trade_date = $scope.toJsDate($scope.updateList[0].APPLY_DATE);
						return;
					}
			});
	    }
	    
	    //僅單次議價且完成覆核方可進行修改，並僅能修改商品和金額
	    $scope.updateApplyFlow = function() {
	    	
	    	if ($scope.inputVO.apply_type == '1' || $scope.inputVO.apply_type == '6') {
	    		
	    		$scope.inputVO.prod_id = $scope.prod_id_1;
	    		$scope.inputVO.prod_name = $scope.prod_name_1;
	    		$scope.inputVO.purchase_amt = $scope.purchase_amt_1;
	    		
	    	} else if ($scope.inputVO.apply_type == '2') {
	    		
	    		$scope.inputVO.prod_id = $scope.prod_id_2;
	    		$scope.inputVO.prod_name = $scope.prod_name_2;
	    		$scope.inputVO.purchase_amt = $scope.purchase_amt_2;
	    		
	    	} else if ($scope.inputVO.apply_type == '4') {
	    		
	    		$scope.inputVO.prod_id = $scope.prod_id_4;
	    		$scope.inputVO.prod_name = $scope.prod_name_4;
	    		$scope.inputVO.purchase_amt = $scope.purchase_amt_4;
	    		
	    	}
	    	$scope.inputVO.seq = $scope.updateList[0].APPLY_SEQ;
	    	
	    	$scope.sendRecv("CRM421", "updateApplyFlow", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", $scope.inputVO,
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    	}
                    	if (totas.length > 0) {
                    		$scope.closeThisDialog('successful');
                    	};
    				}
    		);

	    }
	    
	    
	    
});
