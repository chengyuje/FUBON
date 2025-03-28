/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT120Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q, validateService, sotService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT120Controller";
		//繼承PRD100高齡檢核
		$controller('PRD100Controller', {$scope: $scope});
		  
        getParameter.XML(["SOT.CUST_TYPE", "SOT.TRADE_DATE_TYPE", "COMMON.YES_NO", "SOT.EBANK_PRTDOC_URL", "SOT.CHG_PRTDOC_URL", "SOT.BARGAIN_STATUS", "SOT.RESERVE_TRADE_DAYS", "SOT.FITNESS_YN", "SOT.RESERVE_DATE_TIMESTAMP", "SOT.PROSPECTUS_TYPE", "CRM.SINGLE_REG_DISCOUNT","SOT.SPEC_CUSTOMER",'SOT.FUND_DECIMAL_POINT'], function(totas) {
			if (totas) {
				// mapping 來行人員  參數設定SOT.CUST_TYPE
				$scope.mappingSet['SOT.CUST_TYPE'] = totas.data[totas.key.indexOf('SOT.CUST_TYPE')];
				  //交易日期
		        $scope.mappingSet['SOT.TRADE_DATE_TYPE'] = totas.data[totas.key.indexOf('SOT.TRADE_DATE_TYPE')];
		        //YES;NO
		        $scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
				$scope.mappingSet['SOT.EBANK_PRTDOC_URL'] = totas.data[totas.key.indexOf('SOT.EBANK_PRTDOC_URL')];
				
				//公開說明書選項
				$scope.mappingSet['SOT.PROSPECTUS_TYPE'] = totas.data[totas.key.indexOf('SOT.PROSPECTUS_TYPE')];
				
//				//網行銀服務申請書列印
//				$scope.eBankPredocURL = totas.data[totas.key.indexOf('SOT.EBANK_PRTDOC_URL')][0].LABEL;
//				//變更申請書列印
//				$scope.chgPrtdocURL = totas.data[totas.key.indexOf('SOT.CHG_PRTDOC_URL')][0].LABEL;
				
				if (typeof (webViewParamObj) !== 'undefined') {
					$scope.urlDisable = true;
				} else {
					$scope.urlDisable = false;
					//網行銀服務申請書列印
					$scope.eBankPredocURL = totas.data[totas.key.indexOf('SOT.EBANK_PRTDOC_URL')][0].LABEL;
					//變更申請書列印
					$scope.chgPrtdocURL = totas.data[totas.key.indexOf('SOT.CHG_PRTDOC_URL')][0].LABEL;					
				}
				
				//議價狀態 (若為申請議價) 
				$scope.mappingSet['SOT.BARGAIN_STATUS'] = totas.data[totas.key.indexOf('SOT.BARGAIN_STATUS')];
				
				// 幣別位數轉換
				$scope.mappingSet['SOT.FUND_DECIMAL_POINT'] = totas.data[totas.key.indexOf('SOT.FUND_DECIMAL_POINT')];
				//Nday 預約 營業日參數
				$scope.mappingSet['SOT.RESERVE_TRADE_DAYS'] = totas.data[totas.key.indexOf('SOT.RESERVE_TRADE_DAYS')]; 
	        	angular.forEach($scope.mappingSet['SOT.RESERVE_TRADE_DAYS'], function(row) {
	        		if($scope.inputVO.trustTS == 'M' && row.DATA=='MNF'){
	        			$scope.reserveTradeDay = row.LABEL;
	        		}
	        		if($scope.inputVO.trustTS == 'S' && row.DATA=='NF') { 
	        			$scope.reserveTradeDay = row.LABEL;
	        		}
				});
	        	
	        	//是否適配
		    	$scope.mappingSet['SOT.FITNESS_YN'] = totas.data[totas.key.indexOf('SOT.FITNESS_YN')];
		    	angular.forEach($scope.mappingSet['SOT.FITNESS_YN'], function(row) {
		    		if(row.DATA=='NF' && row.LABEL=='N') { 
		    			$scope.showErrorMsg("參數SOT.FITNESS_YN NF停止商品適配");
		    		}
				});
		    	
		    	//預約時間限制參數
		    	$scope.mappingSet['SOT.RESERVE_DATE_TIMESTAMP'] = totas.data[totas.key.indexOf('SOT.RESERVE_DATE_TIMESTAMP')]; 
				
		    	//小額優惠碼
		    	$scope.singleRegDiscount = totas.data[totas.key.indexOf('CRM.SINGLE_REG_DISCOUNT')];
				$scope.mappingSet['SOT.SPEC_CUSTOMER'] = totas.data[totas.key.indexOf('SOT.SPEC_CUSTOMER')];//客戶註記

			}
		});
        
        //信託型態
        var vo = {'param_type': 'SOT.CHANGE_TRADE_SUB_TYPE', 'desc': false};
        if(!projInfoService.mappingSet['SOT.CHANGE_TRADE_SUB_TYPE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['SOT.CHANGE_TRADE_SUB_TYPE'] = [];
        			for (var i = 0; i < totas[0].body.result.length; i++){	
        				var element = totas[0].body.result[i];
        				if(element.DATA>1){
        					projInfoService.mappingSet['SOT.CHANGE_TRADE_SUB_TYPE'].push({LABEL: element.LABEL, DATA: element.DATA});
        				}
        			}
        			$scope.mappingSet['SOT.CHANGE_TRADE_SUB_TYPE'] = projInfoService.mappingSet['SOT.CHANGE_TRADE_SUB_TYPE'];
        		}
        	});
        } else
        	$scope.mappingSet['SOT.CHANGE_TRADE_SUB_TYPE'] = projInfoService.mappingSet['SOT.CHANGE_TRADE_SUB_TYPE'];
        
        
        //小數點轉換
        $scope.getCurrency = function(){
        	
        	if($scope.avlCurrency_2){
        		$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.avlCurrency_2});
        	}else{//default 計價幣別
        		$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.inputVO.prodCurr});
        	}			
			$scope.num = $scope.mod[0].LABEL;
        	
						
			//最低申購金額
			$scope.inputVO.prodMinBuyAmt = !isNaN($scope.inputVO.prodMinBuyAmt)  ? Number($scope.inputVO.prodMinBuyAmt).toFixed($scope.num)  : $scope.inputVO.prodMinBuyAmt; ;
			//手續費金額
			if(sotService.isMultiple($scope.inputVO.tradeSubType)){
				$scope.inputVO.fee = !isNaN($scope.inputVO.fee) ? Number($scope.inputVO.fee).toFixed($scope.num) : $scope.inputVO.fee;
				//輸入框也要控制小數位
				$scope.inputVO.purchaseAmt = $filter('number')($scope.moneyUnFormat($scope.inputVO.purchaseAmt), $scope.num.toString());
				
			}else{
				$scope.inputVO.feeL = !isNaN($scope.inputVO.feeL) ? Number($scope.inputVO.feeL).toFixed($scope.num) : $scope.inputVO.feeL;
				$scope.inputVO.feeM = !isNaN($scope.inputVO.feeM) ? Number($scope.inputVO.feeM).toFixed($scope.num) : $scope.inputVO.feeM;
				$scope.inputVO.feeH = !isNaN($scope.inputVO.feeH) ? Number($scope.inputVO.feeH).toFixed($scope.num) : $scope.inputVO.feeH;
				
				//輸入框也要控制小數位
				$scope.inputVO.purchaseAmtL = $filter('number')($scope.moneyUnFormat($scope.inputVO.purchaseAmtL), $scope.num.toString());
				$scope.inputVO.purchaseAmtM = $filter('number')($scope.moneyUnFormat($scope.inputVO.purchaseAmtM), $scope.num.toString());
				$scope.inputVO.purchaseAmtH = $filter('number')($scope.moneyUnFormat($scope.inputVO.purchaseAmtH), $scope.num.toString());
				
			}
			
			//扣款帳號
			$scope.inputVO.debitAvbBalance = !isNaN($scope.inputVO.debitAvbBalance) ? Number($scope.inputVO.debitAvbBalance).toFixed($scope.num) : $scope.inputVO.debitAvbBalance; ;
		};
        
		
        $scope.getReserveTradeDate = function() {  
        	var tempProdType = '';
        	if($scope.inputVO.trustTS == 'M') {
        		tempProdType = 'MNF';
        	} else {
        		tempProdType = 'NF';
        	}
			/* 取N day getTradeDate 營業日 */ 
			$scope.sendRecv("SOT712", "getReserveTradeDate", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {prodType:tempProdType},
					function(tota, isError) {
						if (!isError) { 
							$scope.inputVO.reservationTradeDate = tota[0].body.TradeDate;
							return;
						}
			});
		};
        
         

        //去千分位
		$scope.cleanCurrency = function(money){
			//money = money.replace(/[,]+/g,"");
			return money;
		}

		$scope.custClear = function() {
			$scope.inputVO.custName = '';
			$scope.inputVO.kycLV = '';									//KYC等級
			
			$scope.inputVO.kycDueDate = undefined;						//KYC效期
			$scope.inputVO.profInvestorYN = '';							//是否為專業投資人
			$scope.inputVO.piDueDate = undefined;						//專業投資人效期
			$scope.inputVO.custRemarks = '';							//客戶註記
			$scope.inputVO.isOBU = '';									//是否為OBU客戶
			$scope.inputVO.isAgreeProdAdv = '';							//同意投資商品諮詢服務
			$scope.inputVO.piRemark = '';                               //專業投資人註記
			$scope.inputVO.bargainDueDate =  undefined;					//期間議價效期
			$scope.inputVO.plNotifyWays = '';							//停損停利通知方式
			$scope.inputVO.takeProfitPerc = undefined;					//停利點
			$scope.inputVO.stopLossPerc = undefined;					//停損點
			$scope.inputVO.debitAcct = '';								//扣款帳號
			$scope.inputVO.trustAcct = '';								//信託帳號
			$scope.inputVO.creditAcct = '';								//收益入帳帳號
			$scope.inputVO.w8benEffDate = undefined;					//W8ben有效日期
			$scope.inputVO.custType = 'CUST';							//來行人員
			$scope.inputVO.agentID = '';								//代理人ID
			$scope.inputVO.agentName = '';
			$scope.inputVO.custProType = '';                               //專業投資人類型1：大專投 2：小專投
			$scope.inputVO.isFirstTrade = '';                            //是否首購
			$scope.isGetSOTCustInfo = undefined; //查詢客戶帳號  (執行前 undefined ,執行後 true)
			//$scope.inputVO.debitAcctBalance = undefined;                //扣款帳號餘額
			$scope.inputVO.debitAvbBalance = undefined;                 //扣款帳號餘額
			$scope.avlCurrency = undefined;                             //扣款帳號餘額幣別
			$scope.inputVO.hnwcYN = '';
			$scope.inputVO.hnwcServiceYN = '';
			$scope.inputVO.kycDueDateLessOneMonth = false;
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST'] = [];
			$scope.mappingSet['SOT.TRUST_ACCT_LIST'] = [];
			$scope.mappingSet['SOT.CREDIT_ACCT_LIST'] = [];
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST#DISPLAY'] = [];
			$scope.mappingSet['SOT.CREDIT_ACCT_LIST#DISPLAY'] = [];
			$scope.err = {}; //錯誤訊息組
        };
        
        //WMS-CR-20191009-01_金錢信託套表需求申請單 2020-2-10 add by Jacky
		$scope.inputVO.contractID = ''; //契約編號
		$scope.inputVO.trustTS = 'S';  //M:金錢信託  S:特金交易--預設特金如果契約編號有值則為SOT112交易為金錢信託
		
        //清除手續費相關
        $scope.prodFeeClear = function() {
        	$scope.inputVO.defaultFeeRate = undefined;     //表定手續費率
			 $scope.inputVO.feeType = '';     //手續費優惠方式
			 $scope.inputVO.brgReason = '下單議價';			//議價原因
			 $scope.inputVO.bargainStatus = undefined;     //議價狀態 (若為申請議價)
			 $scope.inputVO.bargainApplySEQ = undefined;     //議價編號
			 
			 $scope.inputVO.defaultFeeRate = undefined;   //表定手續費
			 $scope.inputVO.defaultFeeRateL = undefined;  //表定手續費L
			 $scope.inputVO.defaultFeeRateM = undefined;  //表定手續費M
			 $scope.inputVO.defaultFeeRateH = undefined;  //表定手續費H
			 
			 $scope.inputVO.feeRate = undefined;      //手續費率
			 $scope.inputVO.feeRateL = undefined;     //手續費率L
			 $scope.inputVO.feeRateM = undefined;     //手續費率M
			 $scope.inputVO.feeRateH = undefined;     //手續費率H
			 
			 $scope.inputVO.fee = undefined;     //手續費金額
			 $scope.inputVO.feeL = undefined;     //手續費金額_低 (小額用)
			 $scope.inputVO.feeM = undefined;     //手續費金額_中 (小額用)
			 $scope.inputVO.feeH = undefined;     //手續費金額_高 (小額用)
			 $scope.inputVO.feeDiscount = undefined;     //手續費折數
			 $scope.inputVO.feeDiscountL = undefined;     //手續費折數
			 $scope.inputVO.feeDiscountM = undefined;     //手續費折數
			 $scope.inputVO.feeDiscountH = undefined;     //手續費折數
			 $scope.inputVO.groupOfa = undefined;   //團體優惠代碼 (ESB DefaultFeeRateVO)
			 $scope.inputVO.trustCurr = undefined;  //投資幣別(ESB DefaultFeeRateVO)
			 $scope.feeRateDisabled = false;
        }
        $scope.prodClear = function(isClearProdId) {
        	 if (isClearProdId) {
        		 $scope.inputVO.prodId = ''; //也要清空產品編號
        	 }
        	 $scope.mappingSet['FEE_RATE_TYPE'] = [];//手續費優惠方式
        	 $scope.feeTypeListData = undefined;
			 $scope.inputVO.prodName = '';     //基金名稱
			 $scope.inputVO.prodCurr = '';     //計價幣別
			 $scope.inputVO.prodRiskLv = '';     //產品風險等級
			 $scope.inputVO.notVertify = '';      //未核備欄位FUS40
			 $scope.inputVO.prodFus20 = '';      //C:國內基金
			 $scope.inputVO.buyTwd = '';         //N:不可申購台幣信託註記
			 $scope.inputVO.prodMinBuyAmt = undefined;     //最低申購金額
			 $scope.inputVO.BaseAmtOfSPurchaseCurr =''; //最低申購金額幣別
			 
			 if($scope.inputVO.trustTS == 'S'){ //特金交易才清除
				 $scope.inputVO.tradeSubType = undefined;      //信託型態:4項
				 $scope.inputVO.tradeDateType = undefined;     //交易日期類別
			 }
			 
			 $scope.inputVO.prospectusType = undefined;    //公開說明書
			 $scope.inputVO.trustCurrType = '';     //信託幣別類別
			 $scope.inputVO.trustCurr = '';     //信託幣別
			 $scope.inputVO.purchaseAmt = undefined;     //申購金額
			 $scope.inputVO.purchaseAmtL = undefined;     //申購金額_低 (小額用)
			 $scope.inputVO.purchaseAmtM = undefined;     //申購金額_中 (小額用)
			 $scope.inputVO.purchaseAmtH = undefined;     //申購金額_高 (小額用)
			 $scope.inputVO.isAutoCx = undefined;     //是否自動換匯
			 $scope.inputVO.chargeDateList = undefined;     //每月扣款日期1 ~6(小額用)
			 
			 
			 $scope.prodFeeClear(); //清除手續費相關
			 
			
			 //$scope.inputVO.tradeDate = undefined;     //交易日期
			 
			 
			 
			 $scope.inputVO.fundInfoSelling = undefined; //商品管理有設建議售出
			 $scope.fromCRM421bargainApplySeq = undefined; //從CRM421傳來下單
			 $scope.isAutoCxYes = undefined;               //是否可以自動換匯
			 $scope.err = {}; //錯誤訊息組
			 $("#tradeDateType1").attr("disabled", false);
        }
        
        /*信託帳號168, 則 扣款和收益要168
		$scope.changeTrustAcct = function() {
			var trustAcct = $scope.inputVO.trustAcct;
			if (trustAcct.indexOf("168")==3) { 
				$scope.inputVO.debitAcct = trustAcct;
				$scope.inputVO.creditAcct = trustAcct;
			}
		};
		*/
		
        //從CRM421事先議價過來做下單
		$scope.fromCRM421bargain = function () { 
			$scope.fromCRM421bargainApplySeq = $scope.connector('get', "SOTNF_bargainApplySeq");
			if($scope.fromCRM421bargainApplySeq && $scope.fromCRM421bargainApplySeq!=null) { 
				$scope.inputVO.bargainApplySEQ = $scope.fromCRM421bargainApplySeq;
				$scope.fromCRM421bargainApplySeq = undefined; 
				$scope.connector('set', "SOTNF_bargainApplySeq", null);
				
				var SOT120InputVO = {  
					bargainApplySEQ :$scope.inputVO.bargainApplySEQ
				};
				//取得事先議價設定
				$scope.sendRecv("SOT120", "getCrmBrgApplySingle", "com.systex.jbranch.app.server.fps.sot120.SOT120InputVO", SOT120InputVO,
						function(tota, isError) {
							if (!isError) {
								$scope.custClear();
								$scope.prodClear();
								$scope.brgApplySingle = tota[0].body.brgApplySingle; //事先議價
								if ($scope.brgApplySingle) {//事先議價
								    $scope.inputVO.custID = $scope.brgApplySingle.CUST_ID;
								    $scope.getSOTCustInfo(false,false);
								} 
								
								return;
							}else{
								$scope.showErrorMsg("ehl_01_common_009");//查無資料
								return;
							}
				});  
			} 
		};
        
		
		//按[清除]鍵   清除序號
        $scope.clearTradeSEQ = function(){
        	$scope.inputVO.tradeSEQ='';
        	$scope.query();
        }
        
		// 取得新交易序號
		$scope.getTradeSEQ = function() { 
				$scope.sendRecv("SOT712", "getTradeSEQ", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.tradeSEQ = tota[0].body.tradeSEQ;
								return;
							}
				});
			 
		};
//		
//		getParameter.XML(["CRM.FC032671_PHS_CATG"], function(totas) {
//			   if (totas) {
//			    $scope.FC032671_PHS_CATG = totas.data[totas.key.indexOf('CRM.FC032671_PHS_CATG')];
//			   }
//			  });
//		
//		 var str = $scope.inputVO.account;
//		    var res = str.split("-");    
//		    var index = $scope.FC032671_PHS_CATG.map(function(e) { return e.LABEL; }).indexOf(res[1]);
////		             alert($scope.FC032671_PHS_CATG[index].DATA);
//		    var param_code = $scope.FC032671_PHS_CATG[index].DATA;
//		    
//		    var index = $scope.singleRegDiscount.map(function(e) { return e.LABEL; }).indexOf(res[1]);
//		    
		    
		    
		//預約限制時間參數化
//		$scope.getReserveDateTimestamp = function(){  
//	        if ($scope.mappingSet['SOT.RESERVE_DATE_TIMESTAMP']) {
//	        	alert(JSON.stringify($scope.mappingSet['SOT.RESERVE_DATE_TIMESTAMP']));
//	        	angular.forEach($scope.mappingSet['SOT.RESERVE_DATE_TIMESTAMP'], function(row) {
//	        		if (row) { 
//	       				var rdTimestamp = row.LABEL.split("|");
//	       				$scope.rdTimestamp1 = rdTimestamp[0].substring(0, 2)+":"+rdTimestamp[0].substring(2, 4)+":00";
//	       				$scope.rdTimestamp2 = rdTimestamp[1].substring(0, 2)+":"+rdTimestamp[1].substring(2, 4)+":00";
//	       			}
//	       		});
//	       	}else{
//	       		$scope.rdTimestamp1 = "15:20:00";
//   				$scope.rdTimestamp2 = "15:30:00"; 
//        	} 
//	        //alert("預約時間限制:"+$scope.rdTimestamp1 + "~" +$scope.rdTimestamp2);
//		}
		
		//交易日期類別是否可選擇即時/預約
		$scope.checkTradeDateType = function(){
			var defer = $q.defer();
			$scope.sendRecv("SOT110", "getReserveDateTimestamp", "com.systex.jbranch.app.server.fps.sot110.SOT110InputVO", {},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length > 0) {
								var rdTimestamp = tota[0].body.resultList[0].PARAM_NAME.split("|");
								$scope.rdTimestamp1 = rdTimestamp[0].substring(0, 2)+":"+rdTimestamp[0].substring(2, 4)+":00";
								$scope.rdTimestamp2 = rdTimestamp[1].substring(0, 2)+":"+rdTimestamp[1].substring(2, 4)+":00";
							} else {
								$scope.rdTimestamp1 = "15:20:00";
								$scope.rdTimestamp2 = "15:30:00"; 
							}
						}
//						alert("預約時間限制:"+$scope.rdTimestamp1 + "~" +$scope.rdTimestamp2);
						/*
						比對系統時間
						AM00:00~PM3:20，預設為即時
						PM3:20~PM3:30不能執行
						PM3:30後，預設為預約
			           */
						var today = new Date();
						var today_year = today.getFullYear();  //西元年份 
						var today_month = today.getMonth()+1;  //一年中的第幾月 
						var today_date = today.getDate();      //一月份中的第幾天
						var timeArray1 = $scope.rdTimestamp1.split(":");
						var timeArray2 = $scope.rdTimestamp2.split(":");
						
						var AM0000 = new Date(today_year, today_month-1, today_date);
						var PM0320 = new Date(today_year, today_month-1, today_date, timeArray1[0], timeArray1[1], timeArray1[2]);
						var PM0330 = new Date(today_year, today_month-1, today_date, timeArray2[0], timeArray2[1], timeArray2[2]);
						var now  = new Date();
						
						
						if ( now.valueOf() > AM0000.valueOf() && now.valueOf() < PM0320.valueOf()) {
							//AM00:00~PM3:20，預設為即時
			                if($scope.inputVO.tradeDateType == '' || $scope.inputVO.tradeDateType == undefined){
			                	$scope.inputVO.tradeDateType = '1';
			                }
						} else if((now.valueOf() > PM0320.valueOf() || now.valueOf() == PM0320.valueOf()) && 
								  (now.valueOf() < PM0330.valueOf() || now.valueOf() == PM0330.valueOf())) {
							//PM3:20~PM3:30不能執行
							$scope.inputVO.tradeDateType = "";
							$scope.showErrorMsg($scope.rdTimestamp1 + "~" + $scope.rdTimestamp2 + "不能執行");
							$scope.overTradeTime = true;
						} else if(now.valueOf() > PM0330.valueOf()) {
							//PM3:30後，只能為預約
							$("#tradeDateType1").attr("disabled", true);
							$scope.inputVO.tradeDateType = '2';
						}	
						defer.resolve("success");

			});	
//			$scope.getReserveDateTimestamp();//預約限制時間參數化
//			/*
//			比對系統時間
//			AM00:00~PM3:20，預設為即時
//			PM3:20~PM3:30不能執行
//			PM3:30後，預設為預約
//           */
//			var today = new Date();
//			var today_year = today.getFullYear(); //西元年份 
//			var today_month = today.getMonth()+1; //一年中的第幾月 
//			var today_date = today.getDate(); //一月份中的第幾天
//			var AM0000 = Date.parse(today_year+"-"+today_month+"-"+today_date+" 00:00:00");
//			var PM0320 = Date.parse(today_year+"-"+today_month+"-"+today_date+" "+$scope.rdTimestamp1);
//			var PM0330 = Date.parse(today_year+"-"+today_month+"-"+today_date+" "+$scope.rdTimestamp2);
//			var now  = new Date();
//			
//			if ( now.valueOf() > AM0000.valueOf() && now.valueOf() < PM0320.valueOf()) {
//				//AM00:00~PM3:20，預設為即時
//                if($scope.inputVO.tradeDateType==''){
//                  $scope.inputVO.tradeDateType=1;
//                }
//			} else if( now.valueOf() > PM0320.valueOf() && now.valueOf() < PM0330.valueOf()) {
//				//PM3:20~PM3:30不能執行
//				$scope.inputVO.tradeDateType="";
//				$scope.showErrorMsg($scope.rdTimestamp1 + "~" + $scope.rdTimestamp2 + "不能執行");
//				return false;
//			} else if(now.valueOf() > PM0330.valueOf()) {
//				//PM3:30後，預設為預約
//				$("#tradeDateType1").attr("disabled", true);
//				$scope.inputVO.tradeDateType=2;
//			}		
			return defer.promise;
		};
		
		$scope.checkTradeDate = function(flag){
			$scope.sendRecv("SOT110", "getReserveDateTimestamp", "com.systex.jbranch.app.server.fps.sot110.SOT110InputVO", {},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length > 0) {
								$scope.rdTimestamp = tota[0].body.resultList[0].PARAM_NAME.split("|");
								$scope.rdTimestamp1 = $scope.rdTimestamp[0].substring(0, 2)+":" + $scope.rdTimestamp[0].substring(2, 4)+":00";
								$scope.rdTimestamp2 = $scope.rdTimestamp[1].substring(0, 2)+":" + $scope.rdTimestamp[1].substring(2, 4)+":00";
							} else {
								$scope.rdTimestamp1 = "15:20:00";
								$scope.rdTimestamp2 = "15:30:00"; 
							}
						}
						
						var today = new Date();
						var today_year = today.getFullYear();  //西元年份 
						var today_month = today.getMonth()+1;  //一年中的第幾月 
						var today_date = today.getDate();      //一月份中的第幾天
						
						var timeArray1 = $scope.rdTimestamp1.split(":");
						var timeArray2 = $scope.rdTimestamp2.split(":");
						
						var AM0000 = new Date(today_year, today_month-1, today_date);
						var PM0320 = new Date(today_year, today_month-1, today_date, timeArray1[0], timeArray1[1], timeArray1[2]);
						var PM0330 = new Date(today_year, today_month-1, today_date, timeArray2[0], timeArray2[1], timeArray2[2]);
						var now  = new Date();
						
						if((now > PM0320 || now == PM0320) && 
						   (now < PM0330 || now == PM0330)) {
							//PM3:20~PM3:30不能執行
							$scope.inputVO.tradeDateType = "";
							$scope.showErrorMsg($scope.rdTimestamp1 + "~" + $scope.rdTimestamp2 + "不能執行");
							return;
						} else if(now > PM0330) {
							//PM3:30後，只能為預約
							$("#tradeDateType1").attr("disabled", true);
							$scope.inputVO.tradeDateType = '2';
							if(flag == '1'){
								$scope.showErrorMsg($scope.rdTimestamp2 + "後僅可預約。");	
							}
							return
						}
						
						// for app 要這樣寫否則無法切換
						if(flag == '1'){
							$scope.inputVO.tradeDateType = '1';
						} else {
							$scope.inputVO.tradeDateType = '2';
						}
			});	
		}
		
     // init
        $scope.init = function() {
        	$scope.cmbDebitAcct=true;  //控制Account disabled
			$scope.cmbCreditAcct=true; //控制Account disabled
        	$scope.custDTLbody = {};
        	$scope.custDTL = [];
        	$scope.carList = [];
        	$scope.inputVO.prodType='1';  //1：基金
        	$scope.inputVO.tradeType='5'; //5：定期(不)定額申購
        	$scope.inputVO.seniorAuthType='S'; //高齡評估表授權種類(S:下單、A：適配)
			$scope.inputVO.tradeSEQ = ''; 
			$scope.inputVO.custID = ''; 								//客戶ID
			$scope.inputVO.custName = '';
			$scope.custClear();
			$scope.prodClear(true);
			$scope.inputVO.custType = 'CUST';
			$scope.inputVO.isAutoCx = 'N'; //自動換匯：
			$scope.inputVO.narratorID = projInfoService.getUserID();  //解說專員員編
			$scope.inputVO.narratorName = projInfoService.getUserName();  //解說專員姓名
			$scope.getReserveTradeDate();  // 取N day getTradeDate 營業日 
			$scope.fromCRM421bargain(); //從CRM421過來的下單
			$scope.inputVO.contractID = ''; //契約編號
			$scope.checkTradeDateType(); //預設即時或預約
			$scope.inputVO.buyType = 'C'; //申購方式
			$scope.overTradeTime = false;
			$scope.inputVO.isWeb = 'N';		//臨櫃交易N, 快速申購Y
			$scope.inputVO.otherWithCustId = false; //是否帶客戶ID進來(快查)
			$scope.inputVO.hnwcYN = ''; //是否為高資產客戶 Y/N 
			$scope.inputVO.hnwcServiceYN = ''; //可提供高資產商品或服務 Y/N 
			$scope.isAutoCxYes = true;
			$scope.fundType = "";
		};
		$scope.init(); 
		
		$scope.validateSeniorCust = function() {
			if(!$scope.inputVO.custID) return;
			
			$scope.inputVO.type = "1";
			$scope.inputVO.cust_id = $scope.inputVO.custID;
			$scope.validSeniorCustEval(); //PRD100.validSeniorCustEval高齡檢核
		}
		
		//PRD100.validSeniorCustEval高齡檢核不通過清空客戶ID
		$scope.clearCustInfo = function() {
			$scope.inputVO.custID = "";
			$scope.connector('set','SOTCustID',null);
			$scope.custClear();
		};
		
		//PRD100.validSeniorCustEval高齡檢核通過後查詢
		$scope.reallyInquire = function() {
			if($scope.inputVO.trustTS == 'M') {
				//金錢信託
				$scope.getCustANDContractList(true);
			} else {
				if($scope.inputVO.otherWithCustId) { //有帶客戶ID(快查)
					$scope.queryChkSenior();
				} else {
					$scope.getSOTCustInfo(false,true);
				}
			}
			$scope.connector('set','SOTCustID',null);
		};
		
		// SOT701-客戶電文
		$scope.getSOTCustInfo = function(loadEdit,input) {
			var deferred = $q.defer();
			$scope.inputVO.custID = $filter('uppercase')($scope.inputVO.custID);
			var validCustID = validateService.checkCustID($scope.inputVO.custID); //自然人和法人檢查
			
			if(validCustID==false){ 
				$scope.inputVO.custID='';
			}
			if(validCustID==false || input) {
				$scope.custClear();
				$scope.prodClear(true);
			}
			
			if(validCustID) {
				$scope.sendRecv("SOT120", "getSOTCustInfo", "com.systex.jbranch.app.server.fps.sot120.SOT120InputVO", {'custID':$scope.inputVO.custID, 'prodType':1, 'tradeType':5},
						function(tota, isError) {
							if (!isError) {
									$scope.toDay = $filter('date')(new Date(),'yyyy-MM-dd 00:00:00');//取當日日期
									//FOR CBS TEST日期
//									if ($scope.toJsDate(tota[0].body.kycDueDate) < $scope.toJsDate($scope.toDay)) { //若KYC過期，需有訊息提示客戶，並清空客戶ID。
									if(tota[0].body.isKycDueDateUseful){
										var kycDueDate = $scope.toJsDate(tota[0].body.kycDueDate);
										 var msgParam = "";
										 if(kycDueDate==null) {
											msgParam='未承作';
										 }else{
											msgParam = kycDueDate.getFullYear() + "/" +(kycDueDate.getMonth() + 1) + "/" + kycDueDate.getDate();
										 }
										 var txtMsg = $filter('i18n')('ehl_01_sot310_001') + "(" + msgParam + ")";
										 $scope.showErrorMsg(txtMsg);
										 $scope.custClear();
										 $scope.inputVO.custID = "";
										 return;
									}
									if (tota[0].body.noSale == "Y") { //若為禁銷客戶，出現提示訊息禁止下單
										$scope.showErrorMsg("ehl_01_sot310_003");
										$scope.custClear();
										$scope.inputVO.custID = "";
										return;
									}
								    if (tota[0].body.rejectProdFlag == "Y") {	//拒銷(RS)註記Y 得拒絕申請人臨櫃進行非存款類之理財商品下單(轉換、變更投資標的不在此限)
								    	$scope.showErrorMsg("ehl_01_SOT702_002");
										$scope.custClear();
										$scope.inputVO.custID = "";
										return;
								    }
								    
								    if ((tota[0].body.deathFlag == "Y" || tota[0].body.isInterdict == "Y") && $scope.inputVO.trustTS == 'M') { //FOR金錢信託 若為死亡戶/禁治產等狀況，不可下單。
										$scope.showErrorMsg("ehl_01_sot310_004");
										$scope.custClear();
										$scope.inputVO.custID = "";
										return;
									}
								    if (tota[0].body.isOBU == "Y"){
								    	$scope.showErrorMsg("ehl_01_SOT120_001"); //#0397
										$scope.custClear();
										$scope.inputVO.custID = "";
										return;
								    }
								    
								    $scope.isGetSOTCustInfo = undefined;
									$scope.inputVO.custName = tota[0].body.custName;
									$scope.inputVO.kycLV = tota[0].body.kycLV;								//KYC等級
									$scope.inputVO.kycDueDate = $scope.toJsDate(tota[0].body.kycDueDate);	//KYC效期
									$scope.inputVO.profInvestorYN = tota[0].body.profInvestorYN;			//是否為專業投資人
									$scope.inputVO.piDueDate = $scope.toJsDate(tota[0].body.piDueDate);		//專業投資人效期
									$scope.inputVO.custRemarks = tota[0].body.custRemarks;					//客戶註記
									$scope.inputVO.isOBU = tota[0].body.isOBU;								//是否為OBU客戶
									$scope.inputVO.isAgreeProdAdv = tota[0].body.isAgreeProdAdv;			//同意投資商品諮詢服務
									$scope.inputVO.piRemark = tota[0].body.piRemark;			            //專業投資人註記
									$scope.inputVO.bargainDueDate =  $scope.toJsDate(tota[0].body.bargainDueDate);			//期間議價效期
									$scope.inputVO.plNotifyWays = tota[0].body.plNotifyWays;				//停損停利通知方式
									
									//$scope.inputVO.w8benEffDate = $scope.toJsDate(tota[0].body.w8benEffDate);		//W8ben有效日期
									$scope.inputVO.custProType = tota[0].body.custProType;                   // 專業投資人類型
									$scope.inputVO.isFirstTrade = tota[0].body.isFirstTrade;                 //是否首購
									$scope.inputVO.isBanker = tota[0].body.isBanker; 						 //是否為行員
									$scope.inputVO.hnwcYN = tota[0].body.hnwcYN;
									$scope.inputVO.hnwcServiceYN = tota[0].body.hnwcServiceYN;
									
									$scope.inputVO.kycDueDateLessOneMonth = tota[0].body.kycDueDateLessOneMonth;
									
									var car_debitAcct = $scope.inputVO.debitAcct;     //扣款帳號
									var car_trustAcct = $scope.inputVO.trustAcct;     //信託帳號
									var car_creditAcct = $scope.inputVO.creditAcct;     //收益入帳帳號
									var car_feeType = $scope.inputVO.feeType;  //手續費優惠方式
									var car_bargainApplySEQ = $scope.inputVO.bargainApplySEQ; //議價編號
									var car_groupOfa = $scope.inputVO.groupOfa; //優惠團體代碼  (從手續費優惠方式)
									var car_purchaseAmt = $scope.inputVO.purchaseAmt;     //申購金額
									var car_purchaseAmtL = $scope.inputVO.purchaseAmtL;     //申購金額_低 (小額用)
									var car_purchaseAmtM = $scope.inputVO.purchaseAmtM;     //申購金額_中 (小額用)
									var car_purchaseAmtH = $scope.inputVO.purchaseAmtH;     //申購金額_高 (小額用)
									
									
									$scope.mappingSet['SOT.DEBIT_ACCT_LIST'] = tota[0].body.debitAcct;
									$scope.mappingSet['SOT.TRUST_ACCT_LIST'] = tota[0].body.trustAcct;
									$scope.mappingSet['SOT.CREDIT_ACCT_LIST'] = tota[0].body.creditAcct;
									
									if (!loadEdit || !$scope.inputVO.seqNo) {//沒有設loadEdit  從編輯按鈕(風控)過來不寫預設  因為信託帳號跟手續費有關 !$scope.inputVO.seqNo
										$scope.inputVO.takeProfitPerc = tota[0].body.takeProfitPerc;			//停利點
										$scope.inputVO.stopLossPerc = tota[0].body.stopLossPerc;				//停損點
										$scope.inputVO.debitAcct = (tota[0].body.debitAcct.length > 0 ? tota[0].body.debitAcct[0].LABEL : "");				//扣款帳號
										$scope.inputVO.trustAcct = (tota[0].body.trustAcct.length > 0 ? tota[0].body.trustAcct[0].LABEL : "");				//信託帳號
										$scope.inputVO.creditAcct = (tota[0].body.creditAcct.length > 0 ? tota[0].body.creditAcct[0].LABEL : "");			//收益入帳帳號
										$scope.isGetSOTCustInfo = true;
										$scope.checkTrustAcct();  //1.檢查信託帳號 設定扣款帳號  2.顯示debitAcct餘額
									} else if(loadEdit==true) {
										$scope.inputVO.debitAcct = car_debitAcct;     //扣款帳號
										$scope.inputVO.trustAcct = car_trustAcct;     //信託帳號
										$scope.inputVO.creditAcct = car_creditAcct;     //收益入帳帳號
										$scope.inputVO.purchaseAmt = car_purchaseAmt;     //申購金額
										$scope.inputVO.purchaseAmtL = car_purchaseAmtL;     //申購金額_低 (小額用)
										$scope.inputVO.purchaseAmtM = car_purchaseAmtM;     //申購金額_中 (小額用)
										$scope.inputVO.purchaseAmtH = car_purchaseAmtH;     //申購金額_高 (小額用)
										 
										//扣款帳號
										$scope.isGetSOTCustInfo=true; //for changeAcct()
										$scope.checkTrustAcct();  //1.檢查信託帳號  2.顯示debitAcct餘額 
										
										$scope.inputVO.feeType = car_feeType;  //手續費優惠方式
										$scope.inputVO.bargainApplySEQ = car_bargainApplySEQ; //議價編號
										$scope.inputVO.groupOfa = car_groupOfa; //優惠團體代碼  (從手續費優惠方式)
//										$scope.getFeeTypeListData(true,$scope.inputVO.bargainApplySEQ,$scope.inputVO.feeType);
									}
									
									/*  02/18
									$scope.changeTrustAcct();//信託帳號168, 則 扣款和收益要168
									
									//debitAcct顯示餘額
									angular.forEach($scope.mappingSet['SOT.DEBIT_ACCT_LIST'], function(row) { //扣款帳號餘額
						        		if($scope.inputVO.debitAcct==row.LABEL) { 
						        			$scope.inputVO.debitAcctBalance = row.ACCT_BALACNE;
						        		}
									});
									
									
									$scope.isGetSOTCustInfo = true;
									*/
									
									//從FPS來的不清
									if(!$scope.fromFPS) {
										if(loadEdit!=true) {
											$scope.prodClear(true);//清除產品
										}
									}
									
									if ($scope.brgApplySingle) {//事先議價
										$scope.inputVO.prodId = $scope.brgApplySingle.PROD_ID;
										//再找產品
										$scope.getProdDTL();
									}
									
									deferred.resolve("success");
							} else {
								//查無客戶 需清空資料
								$scope.inputVO.custID='';
								$scope.custClear();
								$scope.prodClear(true);
							}
				});
			}
			return deferred.promise;
		};
		
		//手續費優惠方式清單 (含最優)
		//目前先輸入申購金額才查手續費列表和最優手續費
		$scope.getFeeTypeListData = function (isLoadEdit,carBargainApplySEQ,carFeeType) { 
			var deferred = $q.defer();
			
			var canGetFeeType = false;
			if(sotService.isMultiple($scope.inputVO.tradeSubType)){
				var purchaseAmt = Number($scope.moneyUnFormat($scope.inputVO.purchaseAmt));
				if(purchaseAmt>0 && ($scope.inputVO.isAutoCx=='Y' || $scope.inputVO.isAutoCx=='N') && $scope.inputVO.trustCurrType) {
					canGetFeeType = true;
				}
			}
			if(sotService.isMultipleN($scope.inputVO.tradeSubType)){
				var purchaseAmtL = Number($scope.moneyUnFormat($scope.inputVO.purchaseAmtL));
				var purchaseAmtM = Number($scope.moneyUnFormat($scope.inputVO.purchaseAmtM));
				var purchaseAmtH = Number($scope.moneyUnFormat($scope.inputVO.purchaseAmtH));
				if(purchaseAmtL>0 && purchaseAmtM>0 && purchaseAmtH>0 && $scope.inputVO.isAutoCx && $scope.inputVO.trustCurrType) {
					canGetFeeType = true;
				}
			}
			if(canGetFeeType){
				var getFeeTypeListInputVO = {"custID": $scope.inputVO.custID, 
						            "trustAcct": $scope.inputVO.trustAcct, 
						            "trustCurrType": $scope.inputVO.trustCurrType, 
						            "tradeSubType": $scope.inputVO.tradeSubType, 
						            "prodId":  $scope.inputVO.prodId, 
						            "prodName": $scope.inputVO.prodName,
						            "purchaseAmt": $scope.moneyUnFormat($scope.inputVO.purchaseAmt), 
						            "purchaseAmtL": $scope.moneyUnFormat($scope.inputVO.purchaseAmtL), 
						            "purchaseAmtM": $scope.moneyUnFormat($scope.inputVO.purchaseAmtM), 
						            "purchaseAmtH": $scope.moneyUnFormat($scope.inputVO.purchaseAmtH), 
						            "isAutoCx": $scope.inputVO.isAutoCx,
						            "trustTS" :$scope.inputVO.trustTS
						            };
				$scope.sendRecv("SOT120", "getFeeTypeListData", "com.systex.jbranch.app.server.fps.sot120.SOT120InputVO", getFeeTypeListInputVO,
					function(tota1, isError) {
						$scope.err = {};
						if (!isError) {
							var feeTypeIndex_temp = '';
							var tradeSubType_temp = $scope.inputVO.tradeSubType;
							$scope.feeTypeListData = tota1[0].body.feeTypeList;
							$scope.mappingSet['FEE_RATE_TYPE'] = []; 
							angular.forEach($scope.feeTypeListData, function(row) {
								if (row.feeTypeIndex != 'idx99' && !sotService.isFundProject(tradeSubType_temp)) {  //[99最優]不放進去 ,[FUND心投定期定額及FUND心投定期不定額] 也不加
									$scope.mappingSet['FEE_RATE_TYPE'].push({LABEL:row.LABEL, DATA:row.feeTypeIndex});
									if(carBargainApplySEQ){
										if(carBargainApplySEQ == row.BeneCode){
											feeTypeIndex_temp = row.feeTypeIndex;
										}
									}									
								} 
			    			});

							$scope.inputVO.feeTypeIndex=''; //下拉重設'請選擇'
							if(!carBargainApplySEQ){
								$scope.getFeeType();
							}
							if(feeTypeIndex_temp != ''){
								$scope.inputVO.feeTypeIndex = feeTypeIndex_temp;
								$scope.getFeeType(isLoadEdit,carBargainApplySEQ,carFeeType);//先跑最優收續費計算 (優惠方式'空白'即為最優)
							}
//							$scope.getFeeType(isLoadEdit,carBargainApplySEQ,carFeeType);//先跑最優收續費計算 (優惠方式'空白'即為最優)
						} else {
							$scope.err.fee_isFeeRateTypeError = true;
							if (tota1.body.msgData) {
								//$scope.showErrorMsg(tota1.body.msgData);
								$scope.err.fee_feeRateTypeErrorMsg = tota1.body.msgData;
							}
						}
						
				});
			}
			return deferred.promise;
		};
		
		//基金表定、最優手續費
		//重選信託帳號要 再查最優手續費
		$scope.getDefaultFeeRate = function () {
			$scope.inputVO.purchaseAmt = $scope.moneyUnFormat($scope.inputVO.purchaseAmt);
			$scope.inputVO.purchaseAmtH = $scope.moneyUnFormat($scope.inputVO.purchaseAmtH);
			$scope.inputVO.purchaseAmtM = $scope.moneyUnFormat($scope.inputVO.purchaseAmtM);
			$scope.inputVO.purchaseAmtL = $scope.moneyUnFormat($scope.inputVO.purchaseAmtL);
			if ($scope.inputVO.prodId && $scope.isGetSOTCustInfo && $scope.inputVO.trustCurrType!='') { //查詢完客戶後才能 查 基金表定、最優手續費
				$scope.sendRecv("SOT120", "getDefaultFeeData", "com.systex.jbranch.app.server.fps.sot120.SOT120InputVO", $scope.inputVO ,
						function(tota1, isError) {
							if (!isError) {
								if(!$scope.feeTypeListData)
									$scope.feeTypeListData=[];
								$scope.feeTypeListData[0] = tota1[0].body.feeTypeList[0]; //置換最優的優惠方式[請選擇] 
								//$scope.inputVO.feeType="C";
								return;
							}else{
								$scope.prodFeeClear(); //清除手續費相關
							}
				});
			}
		};

		 
		
		//切換手續費方式 ,取得手續費率(高中低  或 申請的手續費)
		$scope.getFeeType = function (isLoadEdit,carBargainApplySEQ,carFeeType) {
			/*FeeType A：申請議價  B：生日券使用  C：最優手續費 D：單次議價*/
			debugger;
			$scope.inputVO.bargainApplySEQ = undefined;
			$scope.inputVO.groupOfa = undefined;
			$scope.inputVO.feeType = undefined;
			//$scope.inputVO.trustCurr = undefined;
			$scope.inputVO.feeRate = undefined;      //手續費率
			 $scope.inputVO.feeRateL = undefined;     //手續費率L
			 $scope.inputVO.feeRateM = undefined;     //手續費率M
			 $scope.inputVO.feeRateH = undefined;     //手續費率H 
			 $scope.inputVO.fee = undefined;     //手續費金額
			 $scope.inputVO.feeL = undefined;     //手續費金額_低 (小額用)
			 $scope.inputVO.feeM = undefined;     //手續費金額_中 (小額用)
			 $scope.inputVO.feeH = undefined;     //手續費金額_高 (小額用)
			 $scope.inputVO.feeDiscount = undefined;     //手續費折數
			 $scope.inputVO.feeDiscountL = undefined;     //手續費折數
			 $scope.inputVO.feeDiscountM = undefined;     //手續費折數
			 $scope.inputVO.feeDiscountH = undefined;     //手續費折數
			
			if(isLoadEdit==true){ //若是從編輯過來 要指定feeTypeIndex
				angular.forEach($scope.feeTypeListData, function(row) {
					if(carFeeType=='C'){
						$scope.inputVO.feeTypeIndex = '';
					}else if(carFeeType=='D' && carBargainApplySEQ==row.BeneCode){
						$scope.inputVO.feeTypeIndex = row.feeTypeIndex;
					}else if(carFeeType=='B'){
						$scope.inputVO.feeTypeIndex='idxBTH';
					}else if(carFeeType=='A'){
						$scope.inputVO.feeTypeIndex='idxAPPLY'; 
					} 
				});
			}
					
				angular.forEach($scope.feeTypeListData, function(row) {
					if($scope.inputVO.feeTypeIndex =='' || $scope.inputVO.feeTypeIndex =='idx99') {
						 $scope.inputVO.feeType ='C';
					}
					
					if ($scope.inputVO.feeTypeIndex == row.feeTypeIndex) {
						
						$scope.inputVO.feeType = row.feeType; //A：申請議價  B：生日券使用  C：最優手續費  D：單次議價
						 
						//D事先議價
						if($scope.inputVO.feeType=='D'){
							$scope.inputVO.bargainApplySEQ = row.BeneCode;//BeneCode優惠碼  EviNum成交憑證號碼
							$scope.inputVO.groupOfa = row.groupCode;
							$scope.getFee('rate');//計算折數
							$scope.inputVO.feeRateData = JSON.stringify(row);
							
//							if ($scope.inputVO.tradeSubType == '2' || $scope.inputVO.tradeSubType == '4') { 
//								$scope.inputVO.feeRate = row.FeeRate;
//								$scope.inputVO.feeRate = row.FeeRate;
//								$scope.getFee('rate');//計算折數
//								$scope.inputVO.feeRateData = JSON.stringify(row);
//							}else if ($scope.inputVO.tradeSubType == '3' || $scope.inputVO.tradeSubType == '5') {
//							//請選擇(最優) 有高中低
//								$scope.inputVO.feeRateL = row.FeeRate;
//								$scope.inputVO.feeRateM = row.FeeRate;
//								$scope.inputVO.feeRateH = row.FeeRate;
//								$scope.getFee('rate');//計算折數
//								$scope.inputVO.feeRateData = JSON.stringify(row);
//							}
							$scope.feeRateDisabled = true;
						}
					}
					
					//B生日券使用
					if ($scope.inputVO.feeType == 'B') {
						$scope.feeRateDisabled = true;
					}
					
					//A申請議價
					if ($scope.inputVO.feeType == 'A') {
						if(isLoadEdit!=true){//若不是編輯資料過來  要清空
//							if ($scope.inputVO.tradeSubType == '2' || $scope.inputVO.tradeSubType == '4') {
								$scope.inputVO.feeRate = '';
								$scope.inputVO.fee = '';
								//$scope.getFee('rate');//計算折數
								$scope.inputVO.feeRateData = JSON.stringify(row);
//							}else if ($scope.inputVO.tradeSubType == '3' || $scope.inputVO.tradeSubType == '5') {
//								//請選擇(最優) 有高中低
//								$scope.inputVO.feeRateL = '';
//								$scope.inputVO.feeRateM = '';
//								$scope.inputVO.feeRateH = '';
//								//$scope.getFee('rate');//計算折數
//								$scope.inputVO.feeRateData = JSON.stringify(row);
//							}
						}
						$scope.feeRateDisabled = false;
					}
					
					//E次數型團體優惠
					if ($scope.inputVO.feeType == 'E' && $scope.inputVO.feeTypeIndex == row.feeTypeIndex) {
						$scope.inputVO.groupOfa = row.groupCode;
					}
					
					//C最優(請選擇)
					if (($scope.inputVO.feeTypeIndex =='idx99' || $scope.inputVO.feeTypeIndex =='') 
						 && 'idx99'== row.feeTypeIndex && 'C'== $scope.inputVO.feeType) { 
						if (sotService.isMultiple($scope.inputVO.tradeSubType)) {
						    $scope.inputVO.feeRate = row.FeeRateL;
						    $scope.inputVO.fee = row.FeeL;
						    $scope.inputVO.defaultFeeRate = row.DefaultFeeRateL;
						    //$scope.inputVO.trustCurr = row.TrustCurr;
						    $scope.getFee('rate');//計算折數
						} else if (sotService.isMultipleN($scope.inputVO.tradeSubType)) {
							//請選擇(最優) 有高中低
							$scope.inputVO.feeRateL = row.FeeRateL;
							$scope.inputVO.feeRateM = row.FeeRateM;
							$scope.inputVO.feeRateH = row.FeeRateH;
							$scope.inputVO.feeL = row.FeeL;
							$scope.inputVO.feeM = row.FeeM;
							$scope.inputVO.feeH = row.FeeH; 
							$scope.inputVO.defaultFeeRateL = row.DefaultFeeRateL;
							$scope.inputVO.defaultFeeRateM = row.DefaultFeeRateM;
							$scope.inputVO.defaultFeeRateH = row.DefaultFeeRateH; 
							$scope.getFee('rate');//計算折數
						}

						$scope.inputVO.groupOfa = row.GroupOfa;   //團體優惠代碼 (最優手續費_要記錄團體優惠代碼)
						$scope.inputVO.trustCurr = row.TrustCurr; //投資幣別
						$scope.feeRateDisabled = true;
						$scope.inputVO.feeRateData = JSON.stringify(row); 
					}
//					alert($scope.inputVO.feeRateData);
				}); 
				
				$scope.checkBestFee();//檢查手續費是否最優
				$scope.getCurrency();
		};
		
		//檢查手續費是否最優(比較DefaultFeeRateVO.fee L/M/H)
		//#1808 小額快速申購_申請議價費率判斷邏輯開啟 只讓特金修改 金錢不動
		$scope.checkBestFee = function (isLoadEdit,carBargainApplySEQ,carFeeType) {
			if($scope.inputVO.trustTS == 'S') {
			var bestFeeType = $scope.feeTypeListData[0];
			/*若此時選擇的優惠劣於既有的身分別或期間議價優惠，
			則會跳出提醒訊息「客戶已享有手續費率XX%，
			請選擇其他手續費優惠方式」。跳出警語後，
			還原到預設優惠折扣如手續費率、手續費金額、折數
			(「身分別」、「期間議價」其中最優)，手續費優惠方式變成'請選擇'
			*/
			var errMsgParam = ''; 
			if (sotService.isMultiple($scope.inputVO.tradeSubType)) {
			    if (Number(bestFeeType.FeeRateL) < Number($scope.inputVO.feeRate)) {
			    	errMsgParam = bestFeeType.FeeRateL + '%'; 
			    }
			} else if (sotService.isMultipleN($scope.inputVO.tradeSubType)) {
				//請選擇(最優) 有高中低
				if (Number(bestFeeType.FeeRateL) < Number($scope.inputVO.feeRateL) || 
					Number(bestFeeType.FeeRateM) < Number($scope.inputVO.feeRateM) || 
					Number(bestFeeType.FeeRateH) < Number($scope.inputVO.feeRateH)) {
					errMsgParam = bestFeeType.FeeRateL + '%,' + bestFeeType.FeeRateM + '%,' + bestFeeType.FeeRateH + '%'; 
				} 
			}
			if (errMsgParam!='') {
				$scope.showErrorMsg('「客戶已享有手續費率' + errMsgParam + ' ，請選擇其他手續費優惠方式」');
//				$scope.inputVO.feeTypeIndex = ''; //下拉重設'請選擇'(最優)
//				$scope.getFeeType();
			}
		 }	
		};
		
		//setCustAcctData CALL  $scope.checkTrustAcct()
		 $scope.setCustAcctData = function(){
			 
			//checkTrustAcct 應改已處理 SOT.DEBIT_ACCT_LIST#DISPLAY
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST#DISPLAY'] = $scope.mappingSet['SOT.DEBIT_ACCT_LIST'];
			$scope.mappingSet['SOT.CREDIT_ACCT_LIST#DISPLAY'] = $scope.mappingSet['SOT.CREDIT_ACCT_LIST'];
		    
			$scope.checkTrustAcct();//信託帳號168, 則 扣款和收益要168 			
		    
	   }
		
		//1.設定客戶帳號 ； 2.事先議價   CALL  $scope.checkTrustAcct()
		$scope.getCustAcctData = function() {
			$scope.setCustAcctData();  //setCustAcctData CALL  $scope.checkTrustAcct()
			//TODO 改變信託帳號  則要變換 手續費
			if ($scope.brgApplySingle) {//事先議價 
				$scope.getFeeTypeListData(true,$scope.brgApplySingle.bargainApplySEQ ,'D'); //查詢表定手續和其他事先議價
				$scope.checkPurchaseAmtLimit();//因查BaseFee花時間不能馬上查到
//				$scope.brgApplySingle = undefined;
			} 
		} 
		 
		
		// getFee 手續費率和折數計算
		$scope.getFee = function(type) {
			$scope.inputVO.purchaseAmt = $scope.moneyUnFormat($scope.inputVO.purchaseAmt);
			$scope.inputVO.purchaseAmtL = $scope.moneyUnFormat($scope.inputVO.purchaseAmtL);
			$scope.inputVO.purchaseAmtM = $scope.moneyUnFormat($scope.inputVO.purchaseAmtM);
			$scope.inputVO.purchaseAmtH = $scope.moneyUnFormat($scope.inputVO.purchaseAmtH);
			
			if($scope.inputVO.feeType == 'A' || $scope.inputVO.feeType == 'D') {
				//申請議價 or 事先議價
				var map = $filter('filter')($scope.singleRegDiscount, {DATA: $scope.inputVO.groupOfa});
				$scope.inputVO.feeDiscount = map[0].LABEL;
				
				if(sotService.isMultipleN($scope.inputVO.tradeSubType)){
					//定期不定額
					$scope.inputVO.feeRate = 0;
					$scope.inputVO.fee = 0;
					$scope.inputVO.feeRateL = $scope.inputVO.feeDiscount * $scope.inputVO.defaultFeeRateL / 10;
					$scope.inputVO.feeL = $scope.inputVO.purchaseAmtL * $scope.inputVO.feeRateL / 100;
					$scope.inputVO.feeRateM = $scope.inputVO.feeDiscount * $scope.inputVO.defaultFeeRateM / 10;
					$scope.inputVO.feeM = $scope.inputVO.purchaseAmtM * $scope.inputVO.feeRateM / 100;
					$scope.inputVO.feeRateH = $scope.inputVO.feeDiscount * $scope.inputVO.defaultFeeRateH / 10;
					$scope.inputVO.feeH = $scope.inputVO.purchaseAmtH * $scope.inputVO.feeRateH / 100;
				} else {
					//定期定額
					$scope.inputVO.feeRate = $scope.inputVO.feeDiscount * $scope.inputVO.defaultFeeRate / 10;
					$scope.inputVO.fee = $scope.inputVO.purchaseAmt * $scope.inputVO.feeRate / 100;
				}
			} else if($scope.inputVO.feeType == 'E') {
				//次數型團體優惠不用算費率與折數
			} else if(sotService.isMultiple($scope.inputVO.tradeSubType)){
				if(type == 'rate') {
					$scope.inputVO.fee = $scope.inputVO.purchaseAmt * $scope.inputVO.feeRate / 100;
					$scope.inputVO.feeDiscount = $filter('number')($scope.inputVO.feeRate / $scope.inputVO.defaultFeeRate * 10 , '2');
					if($scope.inputVO.fee=="" || $scope.inputVO.fee==0.0000){
						$scope.inputVO.fee=0;
						$scope.inputVO.feeDiscount=0;
					}
				}
				else {
					if($scope.inputVO.feeDiscount) {
						$scope.inputVO.feeRate = $scope.inputVO.feeDiscount * $scope.inputVO.defaultFeeRate / 10;
						$scope.inputVO.fee = $scope.inputVO.purchaseAmt * $scope.inputVO.feeRate / 100;
					}
				}
			}else if(sotService.isMultipleN($scope.inputVO.tradeSubType)){
				if(type == 'rate') {
//					$scope.inputVO.feeL = $scope.inputVO.purchaseAmtL * $scope.inputVO.feeRateL / 100;
//					$scope.inputVO.feeM = $scope.inputVO.purchaseAmtM * $scope.inputVO.feeRateM / 100;
//					$scope.inputVO.feeH = $scope.inputVO.purchaseAmtH * $scope.inputVO.feeRateH / 100;
					$scope.inputVO.feeDiscountL = $filter('number')( $scope.inputVO.feeRateL / $scope.inputVO.defaultFeeRateL * 10 , '2');
					$scope.inputVO.feeDiscountM = $filter('number')($scope.inputVO.feeRateM / $scope.inputVO.defaultFeeRateM * 10 , '2');
					$scope.inputVO.feeDiscountH = $filter('number')($scope.inputVO.feeRateH / $scope.inputVO.defaultFeeRateH * 10 , '2');
				}
				else {
					if($scope.inputVO.feeDiscount) {
						$scope.inputVO.feeRate = $scope.inputVO.feeDiscount * $scope.inputVO.defaultFeeRate / 10;
//						$scope.inputVO.feeL = $scope.inputVO.purchaseAmtL * $scope.inputVO.feeRateL / 100;
//						$scope.inputVO.feeM = $scope.inputVO.purchaseAmtM * $scope.inputVO.feeRateM / 100;
//						$scope.inputVO.feeH = $scope.inputVO.purchaseAmtH * $scope.inputVO.feeRateH / 100;
					}
				}
			}
			$scope.checkBestFee();//檢查手續費是否最優
		};
		
		
		
		
		
		/** 
        檢核時：
加入購物車 -> 存檔 -> 檢核電文
若成功，電文共用模組回傳error為空白給交易
若失敗，電文共用模組回傳ERROR給交易
交易接收電文模組：
1. 成功
   a. 清空商品資訊欄位
  b. refresh 購物車
2. 失敗
  a. 刪除該筆資料
  b. 顯示錯誤訊息
  c. refresh 購物車
沙巴：SOT705我會修改TDS，檢核時若有錯誤，不會由共用丟exception，會回ERROR MES給交易
其他TDS再麻煩Ocean改，先由ETF試試看 
      **/
		

//		//CRM421檢核 申請議價(手續費)
//		$scope.applyFeeESBverify = function() {
//			var crm421InputVO ={};
//			if (($scope.inputVO.tradeSubType == '2') || ($scope.inputVO.tradeSubType == '4')) {
//				crm421InputVO = {
//					cust_id: $scope.inputVO.custID,
//					apply_type: '1', //  商品類別 1：國內外基金 2：海外有價證券 3：信託商品 4：國外ETF 5：境外結構型商品
//					prod_id: $scope.inputVO.prodId, // 商品代碼
//					prod_name: $scope.inputVO.prodName, // 
//					trust_curr: $scope.inputVO.trustCurrType, // 信託幣別 
//					discount_type: '1' , // 申請折扣類別  1 = 手續費率   2 = 手續費折數
//					purchase_amt: $scope.inputVO.purchaseAmt, // 申購金額
//					fee_rate: $scope.inputVO.feeRate, // 折扣費率
//					fee_discount: $scope.inputVO.feeDiscount , // 折扣數
//					fee: $scope.inputVO.fee, // 手續費金額 
//					con_degree:'',                                  //貢獻度等級 ?
//					highest_auth_lv:'',                             //最高授權等級 ?
//					//entrust_unit:'',                                委託數量 (基金應該不用)
//					//entrust_amt:'',                                 委託價格 (基金應該不用)
//					brg_reason:'',                                  //議價原因?
//					defaultFeeRate:$scope.inputVO.defaultFeeRate,   //表定手續費 
//					prod_type:''                                    //基金要設多少?
//				};
//			}else if (($scope.inputVO.tradeSubType == '3') || ($scope.inputVO.tradeSubType == '5')) {
//				crm421InputVO = {
//						cust_id: $scope.inputVO.custID,
//						apply_type: '1', //  商品類別 1：國內外基金 2：海外有價證券 3：信託商品 4：國外ETF 5：境外結構型商品
//						prod_id: $scope.inputVO.prodId, // 商品代碼
//						prod_name: $scope.inputVO.prodName, // 
//						trust_curr: $scope.inputVO.trustCurrType, // 信託幣別 
//						discount_type: '1' , // 申請折扣類別  1 = 手續費率   2 = 手續費折數
//						purchase_amt: $scope.inputVO.purchaseAmtL, // 申購金額
//						fee_rate: $scope.inputVO.feeRateL, // 折扣費率
//						fee_discount: $scope.inputVO.feeDiscountL , // 折扣數
//						fee: $scope.inputVO.feeL, // 手續費金額 
//					};
//			}
//			
//			if ($scope.inputVO.feeTypeIndex=='idxAPPLY') {
//				//手續費檢核成功addToList , 才能SAVE
//				$scope.sendRecv("CRM421", "addToList", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", crm421InputVO,
//						function(tota, isError) {
//							if (tota.length > 0 && tota[0].body.applySeqList && tota[0].body.applySeqList.length > 0) {
//		                		$scope.showSuccessMsg("ehl_01_common_001");
//		                		$scope.inputVO.bargainApplySEQ = tota[0].body.applySeqList[0]; //CRM421回傳議價編號 TODO待補欄位 
//								return true;
//		                	}
//		                	if (isError) {
//		                		$scope.showErrorMsg(tota[0].body.msgData);
//		                		return false;
//		                	} 
//				});
//			} else {
//				return true; 
//			}	
//			
//		};
		
		//加入購物車
		$scope.addCar = function() {
			$scope.checkTradeDateType().then(function(){
				if($scope.inputVO.kycDueDateLessOneMonth && $scope.inputVO.tradeDateType === '2') {
					var showYear = $scope.inputVO.kycDueDate.getFullYear();  //西元年份 
					var showMonth = $scope.inputVO.kycDueDate.getMonth()+1;  //一年中的第幾月 
					var showDate = $scope.inputVO.kycDueDate.getDate();      //一月份中的第幾天
					
					$scope.showMsg('ehl_01_SOT_018',[showYear,showMonth,showDate]);
				}
				
				if(!$scope.inputVO.prodName){ //查無商品
					$scope.showErrorMsg("ehl_01_common_009"); 
					return;
				}
				if(!$scope.inputVO.tradeSubType || $scope.inputVO.tradeSubType=='') {
					$scope.showErrorMsg('請輸入信託型態');
					return;
				}
				if($scope.overTradeTime){
					$scope.initOverTradeTime(); //判斷完再回到初始狀態
					return;
				}
				if($scope.parameterTypeEditForm.$invalid) {
		    		$scope.showErrorMsg("ehl_01_common_022");
		    		return;
		    	}
				if($scope.inputVO.trustTS == 'S'){ //特金交易才需要檢查
					if(Number($scope.inputVO.takeProfitPerc)<=0 || Number($scope.inputVO.stopLossPerc)>=0){
						$scope.showErrorMsg('滿足點/停損點設定錯誤,不能為0。EX:1%,-1%');
						return;
					}
				}
				if($scope.parameterTypeEditForm.$invalid) {
		    		$scope.showErrorMsg("ehl_01_common_022");
		    		return;
		    	}
				if ($scope.carList.length == 6) {
					$scope.showErrorMsg('ehl_01_SOT_007');
					return;
				}
				$scope.setTrustCurr(); //設定信託幣別 CALL  $scope.checkTrustAcct()
				 
				
//				if ($scope.applyFeeESBverify()==false) { //CRM421檢核 申請議價(手續費)
//					return;
//				}
				
				//網銀快速申購檢核//申購方式：臨櫃/網銀快速交易
				if(!$scope.isWebValid()) return;
				
				//alert(JSON.stringify($scope.inputVO));
				$scope.inputVO.purchaseAmt = $scope.moneyUnFormat($scope.inputVO.purchaseAmt);
				$scope.inputVO.purchaseAmtH = $scope.moneyUnFormat($scope.inputVO.purchaseAmtH);
				$scope.inputVO.purchaseAmtM = $scope.moneyUnFormat($scope.inputVO.purchaseAmtM);
				$scope.inputVO.purchaseAmtL = $scope.moneyUnFormat($scope.inputVO.purchaseAmtL);
				$scope.inputVO.fee = $scope.cleanCurrency($scope.inputVO.fee);
				$scope.inputVO.feeL = $scope.cleanCurrency($scope.inputVO.feeL);
				$scope.inputVO.feeM = $scope.cleanCurrency($scope.inputVO.feeM);
				$scope.inputVO.feeH = $scope.cleanCurrency($scope.inputVO.feeH);
				
				  $scope.sendRecv("SOT120", "save", "com.systex.jbranch.app.server.fps.sot120.SOT120InputVO", $scope.inputVO,
						  function(tota, isError) {
							if (!isError) {
								if (tota[0].body.errorMsg != null) {
									$scope.showErrorMsg(tota[0].body.errorMsg);
									$scope.refresh();
											
									return;
								} else {
									$scope.prodClear(true);
									$scope.refresh();
												
									return;
								}
							}
						  }); 
			});
		};	
		
		//刪除購物車該筆
		$scope.delCar = function(row) {
			var txtMsg = "";
			if ($scope.carList.length == 1) {
				txtMsg = $filter('i18n')('ehl_02_common_005');
			} else {
				txtMsg = $filter('i18n')('ehl_02_common_006');
			}
			$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
				$scope.sendRecv("SOT120", "delProd", "com.systex.jbranch.app.server.fps.sot120.SOT120InputVO", {tradeSEQ : $scope.inputVO.tradeSEQ, seqNo: row.SEQ_NO},
						function(tota, isError) {
							if (!isError) {
								$scope.prodClear(true);
								$scope.refresh();
								
								return;
							}
				});
            });
		};
		
//		//CRM421確認 申請議價
//		$scope.applyFeeESBconfirm = function() {
//			//CRM421電文確認 申請議價(手續費)
//			var crm421InputVO = {
//				seq: $scope.inputVO.bargainApplySEQ 
//			};
//			if ($scope.inputVO.feeTypeIndex=='idxAPPLY') {
//				//手續費檢核成功addToList , 才能SAVE
//				$scope.sendRecv("CRM421", "applySingle", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", crm421InputVO,
//						function(tota, isError) {
//			                if (tota && tota.length > 0) {
//			                	$scope.showSuccessMsg("ehl_01_common_006"); 
//			                	return true;
//			                } 
//			                if (isError) {
//		        				$scope.showErrorMsgInDialog(tota.body.msgData);
//		        				return false;
//			                }
//				});
//			} else {
//				return true;
//			}
//		};
//		
		
		//下一步 按鈕
		$scope.next = function() {
			$scope.checkTradeDateType().then(function(){ //交易日期類別是否可選擇即時/預約
				if($scope.overTradeTime){
					$scope.initOverTradeTime(); //判斷完再回到初始狀態
					return;
				}	
//				if($scope.applyFeeESBconfirm() == false) { // CRM421確認 申請議價
//				return;
//			} 
			
			$scope.sendRecv("SOT120", "next", "com.systex.jbranch.app.server.fps.sot120.SOT120InputVO", {tradeSEQ : $scope.inputVO.tradeSEQ},
					function(tota, isError) {
						if (!isError) {
							$scope.connector('set','SOTTradeSEQ', $scope.inputVO.tradeSEQ);
							$scope.connector('set','SOTCarSEQ', null);
							if ($scope.fromFPS) {
								// from FPS_SOT.js								
								if($scope.inputVO.trustTS == 'S'){
									$scope.setSOTurl('assets/txn/SOT121/SOT121.html'); //特金交易
								}else{
									$scope.setSOTurl('assets/txn/SOT123/SOT123.html'); //金錢信託
								}
							} else {								
								if($scope.inputVO.trustTS == 'S'){
									$rootScope.menuItemInfo.url = "assets/txn/SOT121/SOT121.html"; //特金交易
								}else{
									$rootScope.menuItemInfo.url = "assets/txn/SOT123/SOT123.html"; //金錢信託
								}
							}							
							return;
						}
			});		
		  });
		};
		
		
		//將購物車該筆相同seqNo編號 顯示在編輯區
		$scope.edit = function(row) {
		  try{ 
			 $scope.inputVO.seqNo = row.SEQ_NO;
			 $scope.inputVO.batchSeq = row.BATCH_SEQ;     //下單批號
			 $scope.inputVO.batchNo = row.BATCH_NO;     //下單批號流水號
			 $scope.inputVO.tradeSubType = row.TRADE_SUB_TYPE;     //信託型態
			 $scope.inputVO.prodId = row.PROD_ID;     //基金代號
			 $scope.inputVO.prodName = row.PROD_NAME;     //基金名稱
			 $scope.inputVO.prodCurr = row.PROD_CURR;     //計價幣別
			 $scope.inputVO.prodRiskLv = row.PROD_RISK_LV;     //產品風險等級
			 $scope.inputVO.notVertify = row.NOT_VERTIFY;      //未核備欄位
			 $scope.inputVO.prodMinBuyAmt = row.PROD_MIN_BUY_AMT;     //最低申購金額
			 $scope.inputVO.trustCurrType = row.TRUST_CURR_TYPE;     //信託幣別類別
			 if(row.TRUST_CURR_TYPE=='C') { 
				 $scope.inputVO.prodFus20 = row.TRUST_CURR_TYPE;     //C:國內基金
				 $scope.currencyType();                              //選擇信託幣別 查最低申購金額
		  	 }
			 $scope.inputVO.trustCurr = row.TRUST_CURR;     //信託幣別
			 $scope.inputVO.purchaseAmt = row.PURCHASE_AMT;     //申購金額
			 $scope.inputVO.purchaseAmtL = row.PURCHASE_AMT_L;     //申購金額_低 (小額用)
			 $scope.inputVO.purchaseAmtM = row.PURCHASE_AMT_M;     //申購金額_中 (小額用)
			 $scope.inputVO.purchaseAmtH = row.PURCHASE_AMT_H;     //申購金額_高 (小額用)
			 $scope.inputVO.isAutoCx = row.IS_AUTO_CX;     //是否自動換匯
			 
			 //每月扣款日期1~6 (小額用)
			 $scope.inputVO.chargeDateList = row.CHARGE_DATE_1 +"、" +row.CHARGE_DATE_2 +"、"+row.CHARGE_DATE_3 +"、"+row.CHARGE_DATE_4 +"、"+row.CHARGE_DATE_5 +"、"+row.CHARGE_DATE_6;
			 $scope.inputVO.chargeDateList = $scope.inputVO.chargeDateList.replace(/\、null/g,"");
			 
			 $scope.inputVO.defaultFeeRate = row.DEFAULT_FEE_RATE;     //表定手續費率
			 $scope.inputVO.defaultFeeRateL = row.DEFAULT_FEE_RATE_L;     //表定手續費率
			 $scope.inputVO.defaultFeeRateM = row.DEFAULT_FEE_RATE_M;     //表定手續費率
			 $scope.inputVO.defaultFeeRateH = row.DEFAULT_FEE_RATE_H;     //表定手續費率
			 
			 
			 $scope.inputVO.bargainStatus = row.BARGAIN_STATUS;     //議價狀態 (若為申請議價)
			 
			 $scope.inputVO.feeRate = row.FEE_RATE;     //手續費率
			 $scope.inputVO.feeRateL = row.FEE_RATE_L;     //手續費率
			 $scope.inputVO.feeRateM = row.FEE_RATE_M;     //手續費率
			 $scope.inputVO.feeRateH = row.FEE_RATE_H;     //手續費率
			 $scope.inputVO.fee = row.FEE;     //手續費金額
			 $scope.inputVO.feeL = row.FEE_L;     //手續費金額_低 (小額用)
			 $scope.inputVO.feeM = row.FEE_M;     //手續費金額_中 (小額用)
			 $scope.inputVO.feeH = row.FEE_H;     //手續費金額_高 (小額用)
			 $scope.inputVO.feeDiscount = row.FEE_DISCOUNT;     //手續費折數
			 $scope.inputVO.feeDiscountL = row.FEE_DISCOUNT_L;     //手續費折數_低
			 $scope.inputVO.feeDiscountM = row.FEE_DISCOUNT_M;     //手續費折數_中
			 $scope.inputVO.feeDiscountH = row.FEE_DISCOUNT_H;     //手續費折數_高
			 
			 
			 $scope.inputVO.tradeDateType = row.TRADE_DATE_TYPE;     //交易日期類別
			 $scope.inputVO.tradeDate = row.TRADE_DATE;     //交易日期
			 $scope.inputVO.stopLossPerc = row.STOP_LOSS_PERC;     //停損點(%)
			 $scope.inputVO.takeProfitPerc = row.TAKE_PROFIT_PERC;     //停利點(%)
			 $scope.inputVO.plNotifWays = row.PL_NOTIFY_WAYS;     //停損停利通知方式
			 $scope.inputVO.narratorID = row.NARRATOR_ID;     //解說專員員編
			 $scope.inputVO.narratorName = row.NARRATOR_NAME;     //解說專員姓名
			 
			 var acctCurrency = $scope.getAcctCurrency();
			 $scope.inputVO.debitAcct = row.DEBIT_ACCT + '_'+ acctCurrency;     //扣款帳號
			 $scope.inputVO.trustAcct = row.TRUST_ACCT;     //信託帳號
			 $scope.inputVO.creditAcct = row.CREDIT_ACCT + '_'+ acctCurrency;     //收益入帳帳號
			 $scope.changeAcct('debit'); //顯示debitAcct餘額 
			 $scope.inputVO.prospectusType = row.PROSPECTUS_TYPE; //取得公開說明書方式
			 $scope.inputVO.feeType = row.FEE_TYPE;     //手續費優惠方式
			 $scope.inputVO.bargainApplySEQ = row.BARGAIN_APPLY_SEQ;     //議價編號 
			 //重新查手續費優惠方式
			 $scope.getFeeTypeListData(true,$scope.inputVO.bargainApplySEQ,$scope.inputVO.feeType) ;
		     $scope.inputVO.groupOfa = row.GROUP_OFA;//優惠團體代碼  (從手續費優惠方式)
			   
			 
		  }catch(e){ 
		  }

		};
		
		//暫存button
		$scope.save = function() { 
			$scope.checkTradeDateType().then(function(){ //交易日期類別是否可選擇即時/預約
				if($scope.overTradeTime){
					$scope.initOverTradeTime(); //判斷完再回到初始狀態
					return;
				}
				$scope.inputVO.purchaseAmt = $scope.moneyUnFormat($scope.inputVO.purchaseAmt);
				$scope.inputVO.purchaseAmtH = $scope.moneyUnFormat($scope.inputVO.purchaseAmtH);
				$scope.inputVO.purchaseAmtM = $scope.moneyUnFormat($scope.inputVO.purchaseAmtM);
				$scope.inputVO.purchaseAmtL = $scope.moneyUnFormat($scope.inputVO.purchaseAmtL);
				$scope.inputVO.fee = $scope.cleanCurrency($scope.inputVO.fee);
				$scope.inputVO.feeL = $scope.cleanCurrency($scope.inputVO.feeL);
				$scope.inputVO.feeM = $scope.cleanCurrency($scope.inputVO.feeM);
				$scope.inputVO.feeH = $scope.cleanCurrency($scope.inputVO.feeH);
				
				$scope.sendRecv("SOT120", "save", "com.systex.jbranch.app.server.fps.sot120.SOT120InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								$scope.connector('set','SOTTradeSEQ', $scope.inputVO.tradeSEQ);
								$scope.connector('set','SOTCarSEQ', null);  //TODO WHY
								return;
							}
				});			
			});		
		};
	
	    //搜尋基金按鈕  基金查詢
		$scope.searchFund = function(row){
			$scope.findNfMinBuyAmt();  //找最低申購金額
        }
        
        //#2320:網行銀申購排除後收型/貨幣型/ETF基金 
		function checkFundType ()  {
			var check = false;
			if ($scope.fundType != "04" && $scope.inputVO.prodId.substring(0, 2) != "05") {
				check = true;
			}
			return check;
		}
		
		//查詢最低/最高申購金額 (BY tradeSubType 信託幣別currencyType)
		$scope.findNfMinBuyAmt = function() {
			debugger;
			var PARAM_CODE = '';
			var MIN_BUY_AMT = '';
			if ('N' == $scope.inputVO.trustCurrType || 'C' == $scope.inputVO.trustCurrType) {
				PARAM_CODE = 'TWD';
			} else {
				PARAM_CODE = $scope.inputVO.prodCurr; //產品計價幣別
			}
			var tradeSubType = $scope.inputVO.tradeSubType;
			
			if ($scope.inputVO.isWeb == 'Y' && checkFundType()) {
				MIN_BUY_AMT = 'SOT.NF_MIN_BUY_AMT_IS_WEB';
			} else if (sotService.isMultiple(tradeSubType)) {
				MIN_BUY_AMT = 'SOT.NF_MIN_BUY_AMT_2';
			} else if (sotService.isMultipleN(tradeSubType)) {
				MIN_BUY_AMT = 'SOT.NF_MIN_BUY_AMT_3';
			}

			if (MIN_BUY_AMT != '') {
				//信託型態
				var vo = { 'param_type': MIN_BUY_AMT, 'desc': false };
				$scope.inputVO.prodMinBuyAmt = 0;
				$scope.inputVO.MaxAmtOfSPurchase = 0;
				$scope.requestComboBox(vo, function(totas) {
					if (totas[totas.length - 1].body.result === 'success') {
						angular.forEach(totas[0].body.result, function(row) {
							if (row.DATA == PARAM_CODE) {
								$scope.inputVO.prodMinBuyAmt = row.LABEL;
								$scope.inputVO.BaseAmtOfSPurchaseCurr = row.DATA;
								$scope.avlCurrency_2 = $scope.inputVO.BaseAmtOfSPurchaseCurr;
							}
						});
					}
				});
			}

			if (sotService.isFundProject(tradeSubType)) {
				//信託型態
				var vo = { 'param_type': 'SOT.NF_MAX_BUY_AMT', 'desc': false };
				$scope.inputVO.prodMinBuyAmt = 0;
				$scope.requestComboBox(vo, function(totas) {
					if (totas[totas.length - 1].body.result === 'success') {
						angular.forEach(totas[0].body.result, function(row) {
							if (row.DATA == PARAM_CODE) {
								$scope.inputVO.MaxAmtOfSPurchase = row.LABEL;
								$scope.inputVO.MaxAmtOfSPurchaseCurr = row.DATA;
							}
						});
					}
				});
			}

		}
		
		
		//檢查 申購金額 要在 合理範圍內
		$scope.checkPurchaseAmtLimit = function(data) {
			try{
				var prodMinBuyAmt = Number($scope.inputVO.prodMinBuyAmt);
				var MaxAmtOfSPurchase = Number($scope.inputVO.MaxAmtOfSPurchase);
				if (sotService.isMultiple($scope.inputVO.tradeSubType)) {
					var purchaseAmt = Number($scope.moneyUnFormat($scope.inputVO.purchaseAmt));
					if($scope.inputVO.tradeSubType == '2') {
						if (purchaseAmt > 0 && purchaseAmt < prodMinBuyAmt) {
							$scope.showErrorMsg("ehl_01_SOT_019", [purchaseAmt, prodMinBuyAmt]);
							$scope.inputVO.purchaseAmt = undefined;
						}
						//#2320:網行銀以台幣購買須以千為單位外幣不用。
						if ($scope.inputVO.isWeb == 'Y'
							&& $scope.inputVO.trustCurrType != "Y" && $scope.inputVO.trustCurrType != ""
							&& !Number.isNaN(purchaseAmt) && purchaseAmt % 1000 != 0 && checkFundType()) {
							$scope.showErrorMsg("ehl_01_SOT_027");
							$scope.inputVO.purchaseAmt = undefined;
						}
					} else {
						if(purchaseAmt > 0 && (purchaseAmt < prodMinBuyAmt ||
								(purchaseAmt > MaxAmtOfSPurchase && MaxAmtOfSPurchase!=0))) {
							$scope.showErrorMsg("ehl_01_SOT_020", [purchaseAmt, prodMinBuyAmt, MaxAmtOfSPurchase]);
							$scope.inputVO.purchaseAmt = undefined;
						}
					}
				}
				if (sotService.isMultipleN($scope.inputVO.tradeSubType)) {
					debugger;
					var purchaseAmtL = Number($scope.moneyUnFormat($scope.inputVO.purchaseAmtL));
					var purchaseAmtM = Number($scope.moneyUnFormat($scope.inputVO.purchaseAmtM));
					var purchaseAmtH = Number($scope.moneyUnFormat($scope.inputVO.purchaseAmtH));
					
					if($scope.inputVO.tradeSubType == '3') {			
						if(purchaseAmtL > 0 && purchaseAmtL < prodMinBuyAmt) {
							$scope.showErrorMsg("ehl_01_SOT_021", [purchaseAmtL, prodMinBuyAmt]);
							$scope.inputVO.purchaseAmtL = undefined;
						}
						if(purchaseAmtM > 0 && purchaseAmtM < prodMinBuyAmt) {
							$scope.showErrorMsg("ehl_01_SOT_022", [purchaseAmtM, prodMinBuyAmt]);
							$scope.inputVO.purchaseAmtM = undefined;
						}
						if(purchaseAmtH > 0 && purchaseAmtH < prodMinBuyAmt) {
							$scope.showErrorMsg("ehl_01_SOT_023", [purchaseAmtH, prodMinBuyAmt]);
							$scope.inputVO.purchaseAmtH = undefined;
						}
						//#2320:網行銀購買須以千為單位
						if ($scope.inputVO.isWeb == 'Y'
							&& $scope.inputVO.trustCurrType != "Y" && $scope.inputVO.trustCurrType != ""
							&& (
								(!Number.isNaN(purchaseAmtL) && purchaseAmtL % 1000 != 0)
								|| (!Number.isNaN(purchaseAmtM) && purchaseAmtM % 1000 != 0)
								|| (!Number.isNaN(purchaseAmtH) && purchaseAmtH % 1000 !== 0)
							   )
							&& checkFundType()) {
							$scope.showErrorMsg("ehl_01_SOT_027");
							switch (data) {
								case "L":
									$scope.inputVO.purchaseAmtL = undefined;
									break;
								case "M":
									$scope.inputVO.purchaseAmtM = undefined;
									break;
								case "H":
									$scope.inputVO.purchaseAmtH = undefined;
									break;
								default:
									$scope.inputVO.purchaseAmtL = undefined;
									$scope.inputVO.purchaseAmtM = undefined;
									$scope.inputVO.purchaseAmtH = undefined;
							}
						}	
					} else {	
						if(purchaseAmtL > 0 && (purchaseAmtL < prodMinBuyAmt ||
								(purchaseAmtL > MaxAmtOfSPurchase && MaxAmtOfSPurchase!=0))) {
							$scope.showErrorMsg("ehl_01_SOT_024", [purchaseAmtL, prodMinBuyAmt, MaxAmtOfSPurchase]);
							$scope.inputVO.purchaseAmtL = undefined;
						}
						if(purchaseAmtM > 0 && (purchaseAmtM < prodMinBuyAmt ||
								purchaseAmtM < purchaseAmtL ||
								(purchaseAmtM > MaxAmtOfSPurchase && MaxAmtOfSPurchase!=0))) {
							$scope.showErrorMsg("ehl_01_SOT_025", [purchaseAmtM, prodMinBuyAmt, MaxAmtOfSPurchase]);
							$scope.inputVO.purchaseAmtM = undefined;
						}
						if(purchaseAmtH > 0 && (purchaseAmtH < prodMinBuyAmt ||
								purchaseAmtH < purchaseAmtL ||
								purchaseAmtH < purchaseAmtM ||
								(purchaseAmtH > MaxAmtOfSPurchase && MaxAmtOfSPurchase!=0))) {
							$scope.showErrorMsg("ehl_01_SOT_026", [purchaseAmtH, prodMinBuyAmt, MaxAmtOfSPurchase]);
							$scope.inputVO.purchaseAmtH = undefined;
						} 
					}
				}
				
			}catch(e){
				alert(e);
			}	
		}
		
		//選擇信託類型
		$scope.tradeType = function(){
			/*1.選擇信託型態定期定額，僅秀出與信託幣別相同的最低申購金額限制。
			  2. 選擇信託型態選擇定期不定額，需秀出與信託幣別相同的最低申購金額限制。
			 */
			
			// 參考 SOT.CHANGE_TRADE_SUB_TYPE
			var type = $scope.inputVO.tradeSubType;
			if(type == '2') {
			}
			else if(type == '3') {
			}
			else if(type == '4') {
			}
			else if(type == '5') {
			}
			$scope.findNfMinBuyAmt();
		}
		
		
		//依台幣信託外幣信託:  設定信託幣別  CALL  $scope.checkTrustAcct()
		$scope.setTrustCurr = function() {
			//檢查信託帳號
			$scope.checkTrustAcct(); 
		}
		
		//選擇信託幣別
		$scope.currencyType = function() {
	        /* 自動換匯		
			  1. 選外幣信託，自動換匯才可以選擇，預設"否"。 台幣信託預設否不可修改
			  2. 只有扣款帳號168才可以選擇換匯. 其餘預設否不可修改
            */ 

			if ($scope.inputVO.trustCurrType=='N' || $scope.inputVO.trustCurrType=='C') {
				$scope.isAutoCxYes = undefined;
				$scope.inputVO.isAutoCx='N';
			} else if ($scope.inputVO.trustCurrType=='Y' && sotService.is168($scope.inputVO.debitAcct) && $scope.inputVO.isWeb =='N') {
				$scope.isAutoCxYes = true;
				//$scope.inputVO.isAutoCx='Y';
				$scope.inputVO.isAutoCx='N';
			} else {
				$scope.isAutoCxYes = undefined;
				$scope.inputVO.isAutoCx='N';
			}
						
			/*
			  1. 選擇台幣顯示台幣最低申購金額。
			  2. 選擇外幣顯示外幣最低申購金額。
			  3. 當信託幣別改變時，表定手續費率、手續費優惠費率也將 改變。
   			            呼叫SOT 709 .js –fetchEsbNfDefaultFeeRate(基金代號、台外幣別、申購金額、信託帳號)
			*/
			
			$scope.setTrustCurr();         //設定信託幣別   CALL  $scope.checkTrustAcct()
			
			//切換幣別要重新設定小數位數
			if($scope.inputVO.trustCurrType == "N"){
        		$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: "TWD"});
        	}else{//default 計價幣別
        		$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.inputVO.prodCurr});
        	}			
			$scope.num = $scope.mod[0].LABEL;
			
			$scope.findNfMinBuyAmt();
			
//			if ($scope.brgApplySingle) {//事先議價 
//				$scope.getCustAcctData();    //1.設定客戶帳號  CALL  $scope.checkTrustAcct()； 2.事先議價 
//			} 		
			
			if (!$scope.brgApplySingle) {
				//從FPS來的不清
				if(!$scope.fromFPS) {
					$scope.inputVO.purchaseAmt = undefined;  //清空申購金額
					$scope.inputVO.purchaseAmtL = undefined; //清空申購金額L
					$scope.inputVO.purchaseAmtM = undefined; //清空申購金額M
					$scope.inputVO.purchaseAmtH = undefined; //清空申購金額H
				}				
				$scope.getCustAcctData();                //1.設定客戶帳號  CALL  $scope.checkTrustAcct()； 2.事先議價 
				$scope.getFeeTypeListData();             //手續費優惠方式
			}
			
			//控管國內貨幣型基金交易時間，超過10:30 轉為預約交易狀態	
			if($scope.inputVO.prodId != null  && $scope.inputVO.prodId != ''){
				//修改#0000204 基金定期(不)定額申購>FUND久久，錯誤訊息(NumberFormatException),"prodMinBuyAmt":""造成
				//2020-04-22 by Jacky 修改
				$scope.sendRecv("SOT110", "checkReserve", "com.systex.jbranch.app.server.fps.sot110.SOT110InputVO", {'prodId':$scope.inputVO.prodId},
						function(tota, isError) {
							if (!isError) {
								$scope.rev = tota[0].body.reserve;
								if(!$scope.rev){  
										$("#tradeDateType1").attr("disabled", true);
										$("#tradeDateType2").attr("checked", true);
										$scope.inputVO.tradeDateType = '2';
										$scope.showMsg("此筆交易已超過國內貨幣型基金交易時間，將轉為預約交易");
								}
							}
					});	
			}
			
			if($scope.inputVO.trustTS == 'M'){
				$scope.checkCurrM();
			}
			
		}

		//開啟28天月曆
		$scope.calendar28Days_picker = function(){
			$scope.connector('set','chargeDateList',$scope.inputVO.chargeDateList);
        	var dialog = ngDialog.open({
        		template: 'assets/txn/SOT712/28Days_picker.html',
        		className: '',
        		controller: ['$scope',function($scope){
        			//$scope.showErrorMsg($scope);
        			//$scope.showErrorMsg(cust_id); 
        			
        		}]
        	});
        	dialog.closePromise.then(function(data){  
        		$scope.inputVO.chargeDateList = $scope.connector('get','chargeDateList');
        	});
        }
		
		//解說專員
		$scope.getSOTEmpInfo = function() {
			$scope.sendRecv("SOT712", "getTellerName", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {'tellerID': $scope.inputVO.narratorID},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.EMP_NAME == null || tota[0].body.EMP_NAME == "") {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							
							$scope.inputVO.narratorName = tota[0].body.EMP_NAME;
							return;
						}
			});
		};
		
		//適配結果代碼以逗號分隔
		$scope.showFitnessMessage = function (fitnessMessage) {

			if(fitnessMessage && fitnessMessage!=''){
				$scope.connector('set','fitnessMessageList', fitnessMessage);
				ngDialog.closeAll();
	        	var dialog = ngDialog.open({
	        		template: 'assets/txn/SOT712/FitnessMessageDialog.html',
	        		className: '',
	        		controller: ['$scope',function($scope){ 
	        		}]
	        	});
	        	dialog.closePromise.then(function(data){  
	        		 
	        	});
			}
		};
		
		// 商品查詢
		$scope.getProdDTL = function () {
			var deferred = $q.defer();
			//從FPS來的第一次不清，進入SOT後使用到getProdDTL()就會清了
			if ($scope.clearPurchaseAmt) {
				$scope.inputVO.purchaseAmt = undefined;
			}
			//從FPS來的不清
			if(!$scope.fromFPS) {
				$scope.prodClear();
			} else {
				$scope.clearPurchaseAmt = true;
			}
			
			$scope.inputVO.prodId = $filter('uppercase')($scope.inputVO.prodId);
			
			if($scope.inputVO.prodId) {
				
				$scope.sendRecv("SOT120", "getProdDTL", "com.systex.jbranch.app.server.fps.sot120.SOT120InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.errorMsg != null && tota[0].body.errorMsg != "") {
									//適配有錯誤訊息
									$scope.showErrorMsg(tota[0].body.errorMsg);
									$scope.prodClear(true);
								} else if (tota[0].body.prodDTL != null) {
									if (tota[0].body.warningMsg != null && tota[0].body.warningMsg != "") {
											//適配有警告訊息
											var dialog = ngDialog.open({
											template: 'assets/txn/CONFIRM/CONFIRM.html',
											className: 'CONFIRM',
											showClose: false,
											scope : $scope,
											controller: ['$scope', function($scope) {
												$scope.dialogLabel = tota[0].body.warningMsg;
								            }]
										}).closePromise.then(function (data) {
											if (data.value === 'successful') {
//												$scope.custInfo(tota[0].body);
//												deferred.resolve("success"); 
//												return deferred.promise;
											} else {
												$scope.prodClear(true);
												deferred.resolve("");
											}
										});
									}		
								 
									$scope.findNfMinBuyAmt();  //找最低申購金額
									if (tota[0].body.prodDTL[0]) {
										$scope.inputVO.prodId = tota[0].body.prodDTL[0].PRD_ID;
										$scope.inputVO.prodName = tota[0].body.prodDTL[0].FUND_CNAME;							//商品名稱
										$scope.inputVO.prodCurr = tota[0].body.prodDTL[0].CURRENCY_STD_ID;						//計價幣別
										$scope.inputVO.prodRiskLv = tota[0].body.prodDTL[0].RISKCATE_ID;
										$scope.inputVO.NotVertify = tota[0].body.prodDTL[0].FUS40;                              //未核備欄位FUS40
										$scope.inputVO.prodFus20 = tota[0].body.prodDTL[0].FUS20;                               //C:國內基金
										$scope.inputVO.buyTwd    = tota[0].body.prodDTL[0].BUY_TWD;                             //N:不可申購台幣信託註記
										if($scope.inputVO.prodFus20 == 'C'){
											$scope.inputVO.trustCurrType = $scope.inputVO.prodFus20;
											$scope.inputVO.isAutoCx = "N";
											$scope.currencyType();       //選擇信託幣別 查最低申購金額
										}
										if($scope.inputVO.buyTwd == 'N'){
											$scope.inputVO.trustCurrType = "Y";
											$scope.inputVO.isAutoCx = "N";
											$scope.currencyType();       //選擇信託幣別 查最低申購金額
										}										
										$scope.inputVO.fundInfoSelling = tota[0].body.fundInfoSelling; //建議售出Y/N 要出提示 ehl_01_SOT702_016
										$scope.showFitnessMessage(tota[0].body.fitnessMessage);
										
										$scope.inputVO.debitAvbBalance = undefined;                    //扣款帳號餘額
										$scope.avlCurrency = undefined;                                //扣款帳號餘額幣別
										$scope.checkTrustAcct();
//										$scope.changeAcct();                                           //1.換扣款帳號和2.換商品都要查詢 該帳號幣別餘額
										$scope.fundType = tota[0].body.prodDTL[0].FUND_TYPE;
									}
									
									if ($scope.brgApplySingle) {//事先議價
										$scope.inputVO.tradeSubType = $scope.brgApplySingle.tradeSubType;//2定期定額申購(預設)
										$scope.inputVO.isAutoCx = $scope.brgApplySingle.isAutoCx;        //自動換匯(預設)
										 
										if ($scope.inputVO.prodFus20  == 'C') {
											//信託幣別
											$scope.inputVO.trustCurrType = 'C';
											$scope.inputVO.isAutoCx = "N";
										} else {
											//信託幣別
											$scope.inputVO.trustCurrType = $scope.brgApplySingle.trustCurrType;
										}
										
										$scope.inputVO.purchaseAmt = $scope.brgApplySingle.PURCHASE_AMT; //設定申購金額
										$scope.getCustAcctData();        //1.設定客戶帳號  CALL  $scope.checkTrustAcct()； 2.事先議價
//										$scope.getFeeTypeListData(false, $scope.brgApplySingle.bargainApplySEQ, 'D');
//										$scope.currencyType();       //選擇信託幣別 查最低申購金額

									}
									
									
								return;
							} else {
								$scope.showErrorMsg("ehl_01_common_009");
								$scope.prodClear(true);
							}
						}
							//查無商品 需清除商品ID
							$scope.prodClear(true);	
				});
			}
			return deferred.promise;
		};
		
		
		// 商品適配   給切換交易日期要檢查
//		$scope.checkFitness = function () {
//			var deferred = $q.defer();
//			if($scope.inputVO.prodId) { 
//				$scope.sendRecv("SOT120", "checkFitness", "com.systex.jbranch.app.server.fps.sot120.SOT120InputVO", $scope.inputVO,
//						function(tota, isError) {
//							if (!isError) {  
//								$scope.showFitnessMessage(tota[0].body.fitnessMessage); 
//								deferred.resolve("success"); 
//								return;
//							} else {
//								//適配失敗
//								$scope.showErrorMsg("適配失敗"); 
//								$scope.inputVO.tradeDateType = undefined;     //交易日期類別
//								deferred.resolve("faile"); 
//								return;
//							}
//				});
//			}
//			return deferred.promise;
//		};
		
		
		//查詢購物車
		$scope.noCallCustQuery = function () {
			var deferred = $q.defer();
			$scope.sendRecv("SOT120", "query", "com.systex.jbranch.app.server.fps.sot120.SOT120InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.carList = tota[0].body.carList;
							$scope.outputVO = tota[0].body;
							
							if (tota[0].body.carList[0]) { //avoid carList is empty
								$scope.inputVO.narratorID = tota[0].body.carList[0].NARRATOR_ID;		//解說人員ID
								$scope.inputVO.narratorName = tota[0].body.carList[0].NARRATOR_NAME;	//解說人員姓名
								//WMS-CR-20191009-01_金錢信託套表需求申請單 2020-2-10 add by Jacky
								$scope.inputVO.contractID = tota[0].body.carList[0].CONTRACT_ID;
								$scope.inputVO.trustTS = tota[0].body.mainList[0].TRUST_TRADE_TYPE;
							}
							$scope.narratorDisabled = true;
							if (tota[0].body.mainList[0] && tota[0].body.mainList[0].CUST_ID) { //avoid mainList is empty when del all
								$scope.inputVO.custID = tota[0].body.mainList[0].CUST_ID;				//客戶ID
								$scope.inputVO.isWeb = tota[0].body.mainList[0].IS_WEB;				//臨櫃交易N, 快速申購Y
							}
							//風控頁按編輯回到下單頁 ,從購物車中取要編輯商品
							if ($scope.inputVO.seqNo) {
								angular.forEach($scope.carList, function(row){
									if ($scope.inputVO.seqNo == row.SEQ_NO) {
										$scope.edit(row);//將該筆相同seqNo,顯示在編輯區
									}
				    			});
							}
							
							deferred.resolve("success");
						}
			});
			
			return deferred.promise;
		};
		
		$scope.changeAcct = function (type) {
			var trustCurrType = $scope.inputVO.trustCurrType; //C國內 , N台幣 , Y外幣
			
			if($scope.isGetSOTCustInfo){ //查詢完客戶後才能changeAcct()
				if (type == 'debit') {
					$scope.inputVO.creditAcct = $scope.inputVO.debitAcct.split("_")[0];
				} 
//				else {
//					$scope.inputVO.debitAcct = $scope.inputVO.creditAcct;
//				}
				//取得 相同商品別的 扣款帳後餘額
				 
				$scope.avlCurrency=undefined;
				$scope.inputVO.debitAvbBalance=undefined; 
				//有幣別和餘額
				angular.forEach($scope.mappingSet['SOT.DEBIT_ACCT_LIST#DISPLAY'], function(acctCcyRow){
					if ($scope.inputVO.debitAcct == acctCcyRow.DATA) {  
						$scope.inputVO.debitAvbBalance = acctCcyRow.ACCT_BALACNE;
//						$scope.inputVO.debitAvbBalance = acctCcyRow.AVB_BALANCE;
							$scope.avlCurrency = acctCcyRow.CURRENCY;
							$scope.avlCurrency_2 = $scope.inputVO.BaseAmtOfSPurchaseCurr;
					}
				});
			
			} 
			
			/* TODO bug?
			扣款帳號 和 收益入帳帳號 的選單 各不相同 , 但 changeAcct() 要互相指定creditAcct OR debitAcct
			, 造成下拉<e-combobox>畫面沒法呈現設定結果_________
			SOT701查詢後:只有各一個帳號
			扣款帳號mappingSet['SOT.DEBIT_ACCT_LIST'] ：{213}
			信託帳號mappingSet['SOT.TRUST_ACCT_LIST']：{068}
			收益入帳帳號mappingSet['SOT.CREDIT_ACCT_LIST']：{068}

			 */
			
		};
		
		//查詢帳號幣別		
		$scope.getAcctCurrency = function() {	
			 var trustCurrType = $scope.inputVO.trustCurrType;
			 var prodCurrency = $scope.inputVO.prodCurr;
			 var acctCcy = undefined;
				if (trustCurrType && prodCurrency){ //有傳信託業務別 和 商品幣別 
		 		   if(trustCurrType=='C' || trustCurrType=='N'){
		 			   acctCcy = 'TWD';
		 		   }else if (trustCurrType=='Y'){  //Y外幣
		 			   acctCcy = prodCurrency;
		 		   }
				} 
				return acctCcy;
		};
		
		//將帳號轉成display暫存陣列
		$scope.setAcctDisplay = function(acctNameList,prodCurr){
				for(var i=0;i<acctNameList.length;i++){
					if(prodCurr){
						$scope.mappingSet[acctNameList[i]+'#DISPLAY'] = $filter('filter')($scope.mappingSet[acctNameList[i]], {CURRENCY: prodCurr});
					}else{
						if(acctNameList[i]=='SOT.TRUST_ACCT_LIST' && ($scope.inputVO.isAutoCx && $scope.inputVO.isAutoCx=='Y')){//判斷為信託帳號且自動換會為是時只顯示168帳號
							$scope.mappingSet[acctNameList[i]+'#DISPLAY']=[];
							for(var idx=0;idx<$scope.mappingSet[acctNameList[i]].length;idx++){
								if($scope.mappingSet[acctNameList[i]][idx].DATA && sotService.is168($scope.mappingSet[acctNameList[i]][idx].DATA)){
									$scope.mappingSet[acctNameList[i]+'#DISPLAY'].push($scope.mappingSet[acctNameList[i]][idx]);
								}
							}
						}else{
							$scope.mappingSet[acctNameList[i]+'#DISPLAY'] = $scope.mappingSet[acctNameList[i]];
						}
						
					}
				}
		};
		
		//檢查信託帳號
		$scope.checkTrustAcct = function(){
				$scope.cmbDebitAcct=false;
				$scope.cmbCreditAcct=false;
				$scope.avlBalance = undefined;                              //扣款帳號餘額
				$scope.avlCurrency = undefined;                             //扣款帳號餘額幣別
				var debitAcctListDisplay='SOT.DEBIT_ACCT_LIST#DISPLAY';
				var trustAcctListDisplay = 'SOT.TRUST_ACCT_LIST#DISPLAY';
				var creditAcctListDisplay='SOT.CREDIT_ACCT_LIST#DISPLAY';
				
				var debitAcctList='SOT.DEBIT_ACCT_LIST';
				var trustAcctList = 'SOT.TRUST_ACCT_LIST';
				var creditAcctList='SOT.CREDIT_ACCT_LIST';
				
				$scope.setAcctDisplay([creditAcctList,debitAcctList,trustAcctList]); //初始化
				var prodCurr=$scope.inputVO.prodCurr;
				//商品幣別 無此幣扣款帳號要增加
				if ($scope.inputVO.prodCurr) {
					if(($scope.inputVO.isAutoCx && $scope.inputVO.isAutoCx=='Y') || $scope.inputVO.trustCurrType=='N'){
						//當自動換匯為是或是信託業務別為台幣 設定幣別為TWD
						prodCurr = 'TWD'
					}
					angular.forEach($scope.mappingSet['SOT.TRUST_ACCT_LIST'], function(row){
						if (sotService.is168(row.DATA)) {
							var checkFlag = false;
							var checkTWDFlag = false;
							angular.forEach($scope.mappingSet[debitAcctListDisplay], function(row2){
								var checkAcct = row.DATA+'_'+prodCurr;//檢核是否有此幣別帳號
								if (checkAcct == row2.DATA) {
									checkFlag = true;
								}
							});
							if (!checkFlag) {
								$scope.mappingSet[debitAcctListDisplay].push({LABEL: row.DATA+'_'+prodCurr, 
																			  DATA: row.DATA+'_'+prodCurr,
																			  AVB_BALANCE:'0',
																			  CURRENCY:prodCurr,
																			  label:row.DATA+'_'+prodCurr,
																			  value:row.DATA+'_'+prodCurr});
							}
						}
					});
				}
				
				//信託帳號檢核
				if ($scope.inputVO.trustAcct && sotService.is168($scope.inputVO.trustAcct)) {
					//Levi 2020/04/17 CR 鍵機規則調整	修改為:信託、收益需相同	扣款、收益可不同
//					$scope.mappingSet[debitAcctListDisplay] = $filter('filter')($scope.mappingSet[debitAcctListDisplay],
//																			{DATA: $scope.inputVO.trustAcct},
//																			function(actual, expected) { return angular.equals(actual.split("_")[0], expected)}
//																		  );
					$scope.mappingSet[creditAcctListDisplay] = $filter('filter')($scope.mappingSet[creditAcctListDisplay],
																			{DATA: $scope.inputVO.trustAcct},
																			function(actual, expected) { return angular.equals(actual.split("_")[0], expected)}
																		   );
				}
				//有傳信託業務別 和 商品幣別 
				if ($scope.inputVO.trustCurrType){
		 		  if(prodCurr){	  //計價
		 			  $scope.mappingSet[debitAcctListDisplay] = $filter('filter')($scope.mappingSet[debitAcctListDisplay], {CURRENCY:prodCurr});
//			 		  $scope.mappingSet[creditAcctListDisplay] = $filter('filter')($scope.mappingSet[creditAcctListDisplay], {CURRENCY: $scope.inputVO.prodCurr});
		 			   
		 		   }else if ($scope.inputVO.trustCurrType=='Y'){  //Y外幣
		 			  $scope.mappingSet[debitAcctListDisplay] = $filter('filter')($scope.mappingSet[debitAcctListDisplay],{CURRENCY: "TWD"},function(actual, expected) {  return !angular.equals(actual, expected)});
//			 		  $scope.mappingSet[creditAcctListDisplay] = $filter('filter')($scope.mappingSet[creditAcctListDisplay],{CURRENCY: "TWD"},function(actual, expected) {  return !angular.equals(actual, expected)});
		 		   }
				} 
				if($scope.mappingSet[debitAcctListDisplay].length==1 && $scope.inputVO.trustTS == 'S'){ //只有一筆不能勾選
					$scope.inputVO.debitAcct=$scope.mappingSet[debitAcctListDisplay][0].DATA;
					$scope.cmbDebitAcct=true;
				}
				if($scope.mappingSet[creditAcctListDisplay].length==1 && $scope.inputVO.trustTS == 'S'){ //只有一筆不能勾選
					$scope.inputVO.creditAcct=$scope.mappingSet[creditAcctListDisplay][0].DATA;
					$scope.cmbCreditAcct=true;
				}
				
				$scope.changeAcct('debit');//查詢餘額
		};
				
		//applyseq query
		$scope.queryApplyseq = function(prod){
			var deferred = $q.defer();
			//取得事先議價設定
			$scope.sendRecv("SOT120", "getCrmBrgApplySingle", "com.systex.jbranch.app.server.fps.sot120.SOT120InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.brgApplySingle = tota[0].body.brgApplySingle; //事先議價
							//避免議價編號被清掉
							$scope.brgApplySingle.bargainApplySEQ = $scope.inputVO.bargainApplySEQ;
							//資料庫查無信託幣別
							if(!$scope.brgApplySingle.TRUST_CURR){
								$scope.brgApplySingle.TRUST_CURR = prod.INVEST_CUR;
								if($scope.brgApplySingle.TRUST_CURR == 'TWD'){
									$scope.brgApplySingle.trustCurrType = 'N';
								}else{
									$scope.brgApplySingle.trustCurrType = 'Y';
								}
							}
							//資料庫查無申購金額
							if(!$scope.brgApplySingle.PURCHASE_AMT){
								$scope.brgApplySingle.PURCHASE_AMT = prod.INVEST_AMT;
							}
							
							
							deferred.resolve("success");
						}else{
							$scope.showErrorMsg("ehl_01_common_009");//查無資料
						}
			}); 
			return deferred.promise;
		}
		
		//從快查或別的交易過來，帶CUSTID
		$scope.queryChkSenior = function() {
			if($scope.connector('get','SOTCustID')){
				$scope.getTradeSEQ();
			    $scope.inputVO.custID = $scope.connector('get','SOTCustID');
			    $scope.connector('set','SOTCustID',null);
			    $scope.getSOTCustInfo(false,true).then(function(data) {
			    	var prod=$scope.connector('get', 'SOTProd');
			    	if(prod){
			    		if(prod.FundNO){
			    			$scope.inputVO.prodId = prod.FundNO.trim();
			    		}else if(prod.PROD_ID){
			    			$scope.inputVO.prodId = prod.PROD_ID.trim();
			    		}else{
			    			$scope.inputVO.prodId = null
			    		}
						$scope.connector('set','SOTProd',null);
						if(prod.APPLY_SEQ){
							$scope.inputVO.bargainApplySEQ = prod.APPLY_SEQ;
							$scope.queryApplyseq(prod).then(function(){
								$scope.getProdDTL();
							});
						}else{
							$scope.getProdDTL();
						}
						
					}
				});
			}
		}
				
		$scope.query = function() {
			$scope.carList = [];
			//預設值
			if ($scope.connector('get','SOTTradeSEQ')){
				$scope.inputVO.tradeSEQ = $scope.connector('get','SOTTradeSEQ');
				$scope.inputVO.seqNo = $scope.connector('get','SOTCarSEQ');
				$scope.connector('set', 'SOTTradeSEQ', null); //避免跨交易返回本頁殘值
				$scope.connector('set', 'SOTCarSEQ', null);
				$scope.noCallCustQuery().then(function(data) {
					var loadEdit = true;
					$scope.getSOTCustInfo(loadEdit,false);
				});
			} else if ($scope.fromFPS){
				$scope.getTradeSEQ();
				$scope.inputVO.planID = $scope.FPSData.planID;
				$scope.inputVO.custID = $scope.FPSData.custID;
				$scope.inputVO.prodId = $scope.FPSData.prdID; //商品代號
				$scope.inputVO.tradeSubType = $scope.FPSData.invType; // 信託型態
				$scope.inputVO.trustCurrType = $scope.FPSData.cmbCur == '2' ? 'N' : 'Y'; //幣別
				$scope.inputVO.prodCurr = $scope.FPSData.trustCurr; //產品計價幣別
				$scope.inputVO.purchaseAmt = $scope.FPSData.PURCHASE_ORG_AMT; //原幣金額
				$scope.inputVO.purchaseAmtL = $scope.FPSData.PURCHASE_ORG_AMT; //原幣金額
				$scope.getSOTCustInfo(false, false).then(function (data) {
				  $scope.getProdDTL();
				  $scope.getFeeTypeListData();
				});
			} else {
				// 取得新交易序號 
				$scope.getTradeSEQ();
			}
		};
		if(!$scope.connector('get','SOTCustID')) {
			//"不是"從快查或別的交易過來，帶CUSTID，維持原來
			$scope.inputVO.otherWithCustId = false;
			$scope.query();
		} else {
			//從快查或別的交易過來，帶CUSTID，先做高齡檢核
			$scope.inputVO.otherWithCustId = true;
			$scope.inputVO.custID = $scope.connector('get','SOTCustID');
			$scope.validateSeniorCust();
		}
		
		//更新購物車清單
		$scope.refresh = function (row) { 
			if (row) {
				$scope.inputVO.seqNo = row.SEQ_NO;
			}
			
			$scope.noCallCustQuery(); 
		};
		 
		//test 事先議價FromCRM421
		$scope.testFromCRM421 = function () { 
			$scope.connector('set', "SOTNF_bargainApplySeq", "7"); // 6單筆  7 小額
			$rootScope.menuItemInfo.url = "assets/txn/SOT110/SOT110.html";
		};
		
		$scope.goPRD110 = function (mainProd) {
			var custID = $scope.inputVO.custID;
			var trustTS = $scope.inputVO.trustTS;
			
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT120/SOT120_ROUTE.html',
				className: 'PRD110',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					if(mainProd){
						$scope.txnName = "本月主推";
					}else{
						$scope.txnName = "搜尋基金";
					}
					$scope.isPop = true;
	        		$scope.routeURL = 'assets/txn/PRD110/PRD110.html';
	        		$scope.tradeType = $scope.$parent.inputVO.tradeType;
	        		$scope.cust_id = custID;
	        		$scope.main_prd = mainProd;
	        		$scope.trustTS = trustTS;
	        		$scope.fromSOTProdYN = "Y"; //下單商品搜尋//下單只需要輸入客戶ID時檢核，商品搜尋時不需再次檢核
	            }]
			}).closePromise.then(function (data) {
				if(data.value != "cancel") {
					$scope.inputVO.prodId = data.value.PRD_ID;
					$scope.getProdDTL();
				}
			});
		};

		/**
		 * 確認基金註記 => 此交易檢查是否停止申購
		 */
		$scope.checkFundStatus = () => $scope.sendRecv("SOT703", "qryFundMemo",
			"com.systex.jbranch.app.server.fps.sot703.SOT703InputVO", {prodId: $scope.inputVO.prodId},
			(tota) => tota[0].body[0] && tota[0].body[0].NO_E_PURCHASE ? $scope.showErrorMsg(tota[0].body[0].NO_E_PURCHASE) : $scope.getProdDTL());
		
		$scope.checkCurrM = function() {
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST#DISPLAY'] = [];
			var sameCur = false;
			angular.forEach($scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'], function(contractRow){
				if ($scope.inputVO.debitAcct == contractRow.DATA) {
					var arrayCur = contractRow.CUR.split(',');
					angular.forEach(arrayCur, function(cur){
						if($scope.inputVO.trustCurrType =='C' || $scope.inputVO.trustCurrType == 'N'){
							if('TWD' == cur){
								$scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST#DISPLAY'].push({LABEL: contractRow.DATA, DATA: contractRow.DATA, CUR: contractRow.CUR});
								sameCur = true;
							}
						}else if ($scope.inputVO.trustCurrType =='Y'){
							if($scope.inputVO.prodCurr == cur){
								$scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST#DISPLAY'].push({LABEL: contractRow.DATA, DATA: contractRow.DATA, CUR: contractRow.CUR});
								sameCur = true;
							}
						}

					});
					if(sameCur == false){
						$scope.showErrorMsg("ehl_02_SOT_997");
						$scope.prodClear(true);
//						$scope.inputVO.custID = "";
					}
				}
			
		    })
	    };
	    
	    $scope.checkBuyType = function() {
	    	if($scope.inputVO.buyType == 'Q'){
	    		if($scope.inputVO.piRemark != 'Y'){
	    			$scope.inputVO.buyType = undefined;
	    			$scope.showErrorMsg("未簽署推介同意書不可進行快速下單。");	
	    		}
	    		if(sotService.isFundProject($scope.inputVO.tradeSubType)){
	    			$scope.inputVO.buyType = undefined;
	    			$scope.showErrorMsg("定期(不)定額以及定期(不)定額FUND心投尚未開放網銀行銀快速申購功能");
	    		}
	    	}
	    }
	    $scope.initOverTradeTime = function() {
	    	$scope.overTradeTime = false;
	    };
	    
	    //申購方式：臨櫃/網銀快速交易
		$scope.isWebValid = function() {
			if($scope.inputVO.isWeb == 'Y') {
				if($scope.inputVO.isAgreeProdAdv != 'Y' && $scope.inputVO.profInvestorYN != 'Y' ) {
					$scope.showErrorMsg("需簽過推介同意書才可使用網銀行銀快速申購功能。");	//檢核客戶是否可使用快速申購功能
					return false;
				}
				if(sotService.isFundProject($scope.inputVO.tradeSubType)) {
					$scope.inputVO.tradeSubType = undefined;
					$scope.showErrorMsg("定期(不)定額FUND心投尚未開放網銀行銀快速申購功能。");	//檢核信託型態是否可使用快速申購功能
					return false;
				}
				
				//快速申購不可自動換匯
				$scope.inputVO.isAutoCx = "N";
				$scope.isAutoCxYes = undefined;
//				$scope.showErrorMsg("快速申購不開放自動換匯功能。");	//檢核客戶是否可使用快速申購功能
			} else {
				if ($scope.inputVO.trustCurrType=='Y' && sotService.is168($scope.inputVO.debitAcct)) {
					$scope.isAutoCxYes = true;
				} else {
					$scope.inputVO.isAutoCx = "N";
					$scope.isAutoCxYes = undefined;
				}
			}
			
			return true;
		}
		
		$scope.onWebChange = function() {
			//#2320:因臨櫃和網行銀基金購買價格不一樣，這邊多加判斷。
			$scope.findNfMinBuyAmt();
			if($scope.isWebValid() && $scope.inputVO.isWeb == 'Y') {
				$scope.checkPurchaseAmtLimit();
				$scope.inputVO.feeTypeIndex=''; //手續費優惠方式下拉重設'請選擇'
				$scope.getFeeType();
			}
		}
		
});