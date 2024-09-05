/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT1620Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q , validateService, sotService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT1620Controller";
		//繼承PRD100高齡檢核
		$controller('PRD100Controller', {$scope: $scope});
		
		debugger
        getParameter.XML(["SOT.CUST_TYPE", "SOT.TRADE_DATE_TYPE", "COMMON.YES_NO", "SOT.BARGAIN_STATUS", "SOT.RESERVE_TRADE_DAYS", "SOT.FITNESS_YN", "SOT.RESERVE_DATE_TIMESTAMP", "SOT.SPEC_CUSTOMER",'SOT.FUND_DECIMAL_POINT', "SOT.PROSPECTUS_TYPE"], function(totas) {
			if (totas) {
		    	//預約時間限制參數
		    	$scope.mappingSet['SOT.RESERVE_DATE_TIMESTAMP'] = totas.data[totas.key.indexOf('SOT.RESERVE_DATE_TIMESTAMP')];
				// mapping 來行人員  參數設定SOT.CUST_TYPE
				$scope.mappingSet['SOT.CUST_TYPE'] = totas.data[totas.key.indexOf('SOT.CUST_TYPE')];
				//交易日期
		        $scope.mappingSet['SOT.TRADE_DATE_TYPE'] = totas.data[totas.key.indexOf('SOT.TRADE_DATE_TYPE')];
		        //YES;NO
		        $scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
				$scope.mappingSet['SOT.EBANK_PRTDOC_URL'] = totas.data[totas.key.indexOf('SOT.EBANK_PRTDOC_URL')];
				//公開說明書選項
				$scope.mappingSet['SOT.PROSPECTUS_TYPE'] = totas.data[totas.key.indexOf('SOT.PROSPECTUS_TYPE')];	
				//議價狀態 (若為申請議價) 
				$scope.mappingSet['SOT.BARGAIN_STATUS'] = totas.data[totas.key.indexOf('SOT.BARGAIN_STATUS')];
				
				//Nday 預約 營業日參數
				$scope.mappingSet['SOT.RESERVE_TRADE_DAYS'] = totas.data[totas.key.indexOf('SOT.RESERVE_TRADE_DAYS')]; 
				// 基金幣別小數位
				$scope.mappingSet['SOT.FUND_DECIMAL_POINT'] = totas.data[totas.key.indexOf('SOT.FUND_DECIMAL_POINT')]; 
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
    
        // 隨幣別控制小數位數
        $scope.getCurrency = function(prodCurr){
        	if($scope.avlCurrency){
        		$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.avlCurrency});
        	}else{//default 計價幣別
        		$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: prodCurr});
        	}			
			$scope.num = $scope.mod[0].LABEL;
			
			//輸入框也要控制小數位
			$scope.inputVO.raiseAmt = $filter('number')($scope.moneyUnFormat($scope.inputVO.raiseAmt), $scope.num.toString());
						
			// 最低申購金額：
			$scope.inputVO.fee = $scope.cleanCurrency($scope.inputVO.fee); // 修改格式，去掉逗點
			$scope.inputVO.prodMinBuyAmt = !isNaN($scope.inputVO.prodMinBuyAmt)  ? Number($scope.inputVO.prodMinBuyAmt).toFixed($scope.num)  : $scope.inputVO.prodMinBuyAmt;  
			// 手續費金額：
			$scope.inputVO.fee = !isNaN($scope.inputVO.fee) ? Number($scope.inputVO.fee).toFixed($scope.num) : $scope.inputVO.fee; 
			// 扣款帳號：
			$scope.inputVO.debitAvbBalance = !isNaN($scope.inputVO.debitAvbBalance) ? Number($scope.inputVO.debitAvbBalance).toFixed($scope.num) : $scope.inputVO.debitAvbBalance;
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
			if(money){
				try {
					money = money.replace(/[,]+/g,"");
				} catch(e){}
			}
			return money;
		}
		
		$scope.custClear = function() {
//			console.log("custClear:");
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
			$scope.inputVO.custProType = '';                            //專業投資人類型1：大專投 2：小專投
			$scope.inputVO.isFirstTrade = '';                            //是否首購 
			$scope.isGetSOTCustInfo = undefined; //查詢客戶帳號  (執行前 undefined ,執行後 true)
			$scope.inputVO.debitAvbBalance = undefined;                 //扣款帳號餘額
			$scope.avlCurrency = undefined;                             //扣款帳號餘額幣別
			$scope.inputVO.hnwcYN = '';
			$scope.inputVO.hnwcServiceYN = '';
			
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST'] = [];
			$scope.mappingSet['SOT.TRUST_ACCT_LIST'] = [];
			$scope.mappingSet['SOT.CREDIT_ACCT_LIST'] = [];
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST#DISPLAY'] = [];
			$scope.mappingSet['SOT.CREDIT_ACCT_LIST#DISPLAY'] = [];
			$scope.err = {}; //錯誤訊息組	
        };
        
      //母基金資料
		$scope.setProdDataM = function(data) {
			$scope.inputVO.prodId = data.prodDTL[0].PRD_ID;	//母基金只有商品代碼不一樣，其他帶一般基金商品變數名稱
			$scope.inputVO.prodName = data.prodDTL[0].FUND_CNAME;						//商品名稱
			$scope.inputVO.prodCurr = data.prodDTL[0].CURRENCY_STD_ID;					//計價幣別
			$scope.inputVO.prodRiskLv = data.prodDTL[0].RISKCATE_ID;
			$scope.inputVO.NotVertify = data.prodDTL[0].FUS40;                         //未核備欄位FUS40
			$scope.inputVO.prodFus20 = data.prodDTL[0].FUS20;                          //C:國內基金
			$scope.inputVO.buyTwd = data.prodDTL[0].BUY_TWD;                           //N:不可申購台幣信託註記
			
//			if($scope.inputVO.prodFus20=='C'){
//				$scope.inputVO.trustCurrType = $scope.inputVO.prodFus20;
//				$scope.currencyType();                                      //選擇信託幣別 查最低申購金額
//			}
			$scope.inputVO.trustCurrType = $scope.setTrustCurrType($scope.inputVO.prodFus20, $scope.inputVO.trustCurr);
			$scope.inputVO.fundInfoSelling = data.fundInfoSelling; //建議售出Y/N 要出提示 ehl_01_SOT702_016
			$scope.showFitnessMessage(data.fitnessMessage);
			
			$scope.inputVO.debitAvbBalance = undefined;                 //扣款帳號餘額
			$scope.avlCurrency = undefined;                             //扣款帳號餘額幣別
//			$scope.checkTrustAcct();
			//$scope.changeAcct();//1.換扣款帳號和2.換商品都要查詢 該帳號幣別餘額
		}
		
		//子基金資料1
		$scope.setProdDataC1 = function(data) {
			$scope.inputVO.prodIdC1 = data.prodDTL[0].PRD_ID;
			$scope.inputVO.prodNameC1 = data.prodDTL[0].FUND_CNAME;						//商品名稱
			$scope.inputVO.prodCurrC1 = data.prodDTL[0].CURRENCY_STD_ID;				//計價幣別
			$scope.inputVO.prodRiskLvC1 = data.prodDTL[0].RISKCATE_ID;
//			$scope.inputVO.NotVertifyC1 = data.prodDTL[0].FUS40;                         //未核備欄位FUS40
//			$scope.inputVO.prodFus20C1 = data.prodDTL[0].FUS20;                          //C:國內基金
//			$scope.inputVO.buyTwdC1 = data.prodDTL[0].BUY_TWD;                           //N:不可申購台幣信託註記			
		}
		
		//子基金資料2
		$scope.setProdDataC2 = function(data) {
			$scope.inputVO.prodIdC2 = data.prodDTL[0].PRD_ID;
			$scope.inputVO.prodNameC2 = data.prodDTL[0].FUND_CNAME;						//商品名稱
			$scope.inputVO.prodCurrC2 = data.prodDTL[0].CURRENCY_STD_ID;				//計價幣別
			$scope.inputVO.prodRiskLvC2 = data.prodDTL[0].RISKCATE_ID;
//			$scope.inputVO.NotVertifyC2 = data.prodDTL[0].FUS40;                         //未核備欄位FUS40
//			$scope.inputVO.prodFus20C2 = data.prodDTL[0].FUS20;                          //C:國內基金
//			$scope.inputVO.buyTwdC2 = data.prodDTL[0].BUY_TWD;                           //N:不可申購台幣信託註記			
		}
		
		//子基金資料3
		$scope.setProdDataC3 = function(data) {
			$scope.inputVO.prodIdC3 = data.prodDTL[0].PRD_ID;
			$scope.inputVO.prodNameC3 = data.prodDTL[0].FUND_CNAME;						//商品名稱
			$scope.inputVO.prodCurrC3 = data.prodDTL[0].CURRENCY_STD_ID;				//計價幣別
			$scope.inputVO.prodRiskLvC3 = data.prodDTL[0].RISKCATE_ID;
//			$scope.inputVO.NotVertifyC3 = data.prodDTL[0].FUS40;                         //未核備欄位FUS40
//			$scope.inputVO.prodFus20C3 = data.prodDTL[0].FUS20;                          //C:國內基金
//			$scope.inputVO.buyTwdC3 = data.prodDTL[0].BUY_TWD;                           //N:不可申購台幣信託註記			
		}
		
		//子基金資料4
		$scope.setProdDataC4 = function(data) {
			$scope.inputVO.prodIdC4 = data.prodDTL[0].PRD_ID;
			$scope.inputVO.prodNameC4 = data.prodDTL[0].FUND_CNAME;						//商品名稱
			$scope.inputVO.prodCurrC4 = data.prodDTL[0].CURRENCY_STD_ID;				//計價幣別
			$scope.inputVO.prodRiskLvC4 = data.prodDTL[0].RISKCATE_ID;
		}
		
		//子基金資料5
		$scope.setProdDataC5 = function(data) {
			$scope.inputVO.prodIdC5 = data.prodDTL[0].PRD_ID;
			$scope.inputVO.prodNameC5 = data.prodDTL[0].FUND_CNAME;						//商品名稱
			$scope.inputVO.prodCurrC5 = data.prodDTL[0].CURRENCY_STD_ID;				//計價幣別
			$scope.inputVO.prodRiskLvC5 = data.prodDTL[0].RISKCATE_ID;
		}
		
		//清空商品資料
		$scope.prodClearD = function(type, idx, isClearPID) {
			if(type == "M") {
				$scope.prodClearM(isClearPID);
			} else {
				if(idx == "1") $scope.prodClearC1(isClearPID);
				if(idx == "2") $scope.prodClearC2(isClearPID);
				if(idx == "3") $scope.prodClearC3(isClearPID);
				if(idx == "4") $scope.prodClearC4(isClearPID);
				if(idx == "5") $scope.prodClearC5(isClearPID);
			}
		}
		
		//清空母基金商品資料
		$scope.prodClearM = function(isClearProdId) {
			if (isClearProdId) {
				$scope.inputVO.prodId = ''; //也要清空產品編號
			}
			$scope.mappingSet['FEE_RATE_TYPE'] = [];//手續費優惠方式
	       	$scope.feeTypeListData = undefined;
	       	$scope.inputVO.prodName = '';     //基金名稱
			$scope.inputVO.prodCurr = '';     //計價幣別
			$scope.inputVO.prodRiskLv = '';   //產品風險等級
			$scope.inputVO.notVertify = '';   //未核備欄位FUS40
			$scope.inputVO.prodFus20 = '';    //C:國內基金 
			$scope.inputVO.buyTwd = '';       //N:不可申購台幣信託註記
			$scope.inputVO.isBackend = '';       //判斷是否為後收
			$scope.inputVO.ovsPrivateYN = '';       //是否為境外私募基金
			$scope.inputVO.ovsPriMinBuyAmt = undefined;   //境外私募基金最低申購金額
			$scope.inputVO.prodMinBuyAmt = undefined;     //最低申購金額
			$scope.inputVO.trustCurrType = '';            //信託幣別類別
			$scope.inputVO.trustCurr = '';                //信託幣別
			$scope.inputVO.purchaseAmt = undefined;       //申購金額
			$scope.inputVO.raiseAmt = undefined;       	  //加碼金額 
			$scope.inputVO.BaseAmtOfSPurchaseCurr ='';    //最低申購金額幣別
			$scope.inputVO.certificateID = ""; //憑證編號
				 
			$scope.inputVO.feeType = '';                  	//手續費優惠方式
			$scope.inputVO.brgReason = '下單議價';			//議價原因
			$scope.inputVO.bargainStatus = undefined;     	//議價狀態 (若為申請議價)
			$scope.inputVO.bargainApplySEQ = undefined;    //議價編號
			$scope.inputVO.tradeSubType = '5';            	//信託型態:5.母基金加碼
			$scope.inputVO.defaultFeeRate = undefined;    	//表定手續費 
			
			$scope.inputVO.feeRate = undefined;           	//手續費率 
			 
			$scope.inputVO.fee = undefined;               	//手續費金額 
			$scope.inputVO.feeDiscount = undefined;       	//手續費折數 
			$scope.inputVO.groupOfa = undefined;          	//團體優惠代碼 (ESB DefaultFeeRateVO)
			  
			$scope.inputVO.tradeDateType = undefined;     	//交易日期類別			 
			$scope.feeRateDisabled = false;
			$scope.inputVO.fundInfoSelling = undefined; 	//商品管理有設建議售出
			$scope.fromCRM421bargainApplySeq = undefined; 	//從CRM421傳來下單
			$scope.inputVO.trustAcct = "";
			$scope.err = {}; //錯誤訊息組
			$("#tradeDateType1").attr("disabled", false);
			
			$scope.inputVO.prospectusType = undefined;    	//公開說明書
			$scope.inputVO.engagedROI1 = undefined;
			$scope.inputVO.transDate = "";
			$scope.inputVO.BenefitReturnRate1 = undefined; //約定報酬率
			$scope.inputVO.BenefitReturnRate2 = undefined; //約定報酬率
			$scope.inputVO.BenefitReturnRate3 = undefined; //約定報酬率
			$scope.inputVO.SatelliteBuyDates = ""; //每月轉換日期
			
			//清空子基金
			$scope.prodClearC1(true);
			$scope.prodClearC2(true);
			$scope.prodClearC3(true);
			$scope.prodClearC4(true);
			$scope.prodClearC5(true);
		}
		
		//清空子基金1商品資料
		$scope.prodClearC1 = function(isClearProdId) {
			if (isClearProdId) {
				$scope.inputVO.prodIdC1 = ''; //也要清空產品編號
			}
			$scope.inputVO.prodNameC1 = '';     //基金名稱
			$scope.inputVO.prodCurrC1 = '';     //計價幣別
			$scope.inputVO.prodRiskLvC1 = '';   //產品風險等級
			$scope.inputVO.notVertifyC1 = '';   //未核備欄位FUS40
			$scope.inputVO.prodFus20C1 = '';    //C:國內基金 
			$scope.inputVO.buyTwdC1 = '';       //N:不可申購台幣信託註記
			$scope.inputVO.isBackendC1 = '';       //判斷是否為後收
			$scope.inputVO.ovsPrivateYNC1 = '';       //是否為境外私募基金
			$scope.inputVO.ovsPriMinBuyAmtC1 = undefined;   //境外私募基金最低申購金額
			$scope.inputVO.prodMinBuyAmtC1 = undefined;     //最低申購金額
			$scope.inputVO.trustCurrTypeC1 = '';            //信託幣別類別
			$scope.inputVO.trustCurrC1 = '';                //信託幣別
			$scope.inputVO.purchaseAmtC1 = undefined;       //信託金額 
			$scope.inputVO.BaseAmtOfSPurchaseCurrC1 ='';    //最低申購金額幣別
		}
		
		//清空子基金2商品資料
		$scope.prodClearC2 = function(isClearProdId) {
			if (isClearProdId) {
				$scope.inputVO.prodIdC2 = ''; //也要清空產品編號
			}
			$scope.inputVO.prodNameC2 = '';     //基金名稱
			$scope.inputVO.prodCurrC2 = '';     //計價幣別
			$scope.inputVO.prodRiskLvC2 = '';   //產品風險等級
			$scope.inputVO.notVertifyC2 = '';   //未核備欄位FUS40
			$scope.inputVO.prodFus20C2 = '';    //C:國內基金 
			$scope.inputVO.buyTwdC2 = '';       //N:不可申購台幣信託註記
			$scope.inputVO.isBackendC2 = '';       //判斷是否為後收
			$scope.inputVO.ovsPrivateYNC2 = '';       //是否為境外私募基金
			$scope.inputVO.ovsPriMinBuyAmtC2 = undefined;   //境外私募基金最低申購金額
			$scope.inputVO.prodMinBuyAmtC2 = undefined;     //最低申購金額
			$scope.inputVO.trustCurrTypeC2 = '';            //信託幣別類別
			$scope.inputVO.trustCurrC2 = '';                //信託幣別
			$scope.inputVO.purchaseAmtC2 = undefined;       //信託金額 
			$scope.inputVO.BaseAmtOfSPurchaseCurrC2 ='';    //最低申購金額幣別
		}
		
		//清空子基金3商品資料
		$scope.prodClearC3 = function(isClearProdId) {
			if (isClearProdId) {
				$scope.inputVO.prodIdC3 = ''; //也要清空產品編號
			}
			$scope.inputVO.prodNameC3 = '';     //基金名稱
			$scope.inputVO.prodCurrC3 = '';     //計價幣別
			$scope.inputVO.prodRiskLvC3 = '';   //產品風險等級
			$scope.inputVO.notVertifyC3 = '';   //未核備欄位FUS40
			$scope.inputVO.prodFus20C3 = '';    //C:國內基金 
			$scope.inputVO.buyTwdC3 = '';       //N:不可申購台幣信託註記
			$scope.inputVO.isBackendC3 = '';       //判斷是否為後收
			$scope.inputVO.ovsPrivateYNC3 = '';       //是否為境外私募基金
			$scope.inputVO.ovsPriMinBuyAmtC3 = undefined;   //境外私募基金最低申購金額
			$scope.inputVO.prodMinBuyAmtC3 = undefined;     //最低申購金額
			$scope.inputVO.trustCurrTypeC3 = '';            //信託幣別類別
			$scope.inputVO.trustCurrC3 = '';                //信託幣別
			$scope.inputVO.purchaseAmtC3 = undefined;       //信託金額 
			$scope.inputVO.BaseAmtOfSPurchaseCurrC3 ='';    //最低申購金額幣別
		}
		
		//清空子基金4商品資料
		$scope.prodClearC4 = function(isClearProdId) {
			if (isClearProdId) {
				$scope.inputVO.prodIdC4 = ''; //也要清空產品編號
			}
			$scope.inputVO.prodNameC4 = '';     //基金名稱
			$scope.inputVO.prodCurrC4 = '';     //計價幣別
			$scope.inputVO.prodRiskLvC4 = '';   //產品風險等級
			$scope.inputVO.notVertifyC4 = '';   //未核備欄位FUS40
			$scope.inputVO.prodFus20C4 = '';    //C:國內基金 
			$scope.inputVO.buyTwdC4 = '';       //N:不可申購台幣信託註記
			$scope.inputVO.isBackendC4 = '';       //判斷是否為後收
			$scope.inputVO.ovsPrivateYNC4 = '';       //是否為境外私募基金
			$scope.inputVO.ovsPriMinBuyAmtC4 = undefined;   //境外私募基金最低申購金額
			$scope.inputVO.prodMinBuyAmtC4 = undefined;     //最低申購金額
			$scope.inputVO.trustCurrTypeC4 = '';            //信託幣別類別
			$scope.inputVO.trustCurrC4 = '';                //信託幣別
			$scope.inputVO.purchaseAmtC4 = undefined;       //信託金額 
			$scope.inputVO.BaseAmtOfSPurchaseCurrC4 ='';    //最低申購金額幣別
		}
		
		//清空子基金5商品資料
		$scope.prodClearC5 = function(isClearProdId) {
			if (isClearProdId) {
				$scope.inputVO.prodIdC5 = ''; //也要清空產品編號
			}
			$scope.inputVO.prodNameC5 = '';     //基金名稱
			$scope.inputVO.prodCurrC5 = '';     //計價幣別
			$scope.inputVO.prodRiskLvC5 = '';   //產品風險等級
			$scope.inputVO.notVertifyC5 = '';   //未核備欄位FUS40
			$scope.inputVO.prodFus20C5 = '';    //C:國內基金 
			$scope.inputVO.buyTwdC5 = '';       //N:不可申購台幣信託註記
			$scope.inputVO.isBackendC5 = '';       //判斷是否為後收
			$scope.inputVO.ovsPrivateYNC5 = '';       //是否為境外私募基金
			$scope.inputVO.ovsPriMinBuyAmtC5 = undefined;   //境外私募基金最低申購金額
			$scope.inputVO.prodMinBuyAmtC5 = undefined;     //最低申購金額
			$scope.inputVO.trustCurrTypeC5 = '';            //信託幣別類別
			$scope.inputVO.trustCurrC5 = '';                //信託幣別
			$scope.inputVO.purchaseAmtC5 = undefined;       //信託金額 
			$scope.inputVO.BaseAmtOfSPurchaseCurrC5 ='';    //最低申購金額幣別
		}
		
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
		
		//交易日期類別是否可選擇即時/預約
		$scope.checkTradeDateType = function(){
			var defer = $q.defer();
//			$scope.getReserveDateTimestamp();//預約限制時間參數化
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
        	$scope.inputVO.dynamicYN = "Y";
        	$scope.inputVO.prodType='8';  //8：基金動態鎖利
        	$scope.inputVO.tradeType='5'; //5：基金動態鎖利母基金加碼
        	$scope.inputVO.seniorAuthType='S'; //高齡評估表授權種類(S:下單、A：適配)
        	$scope.inputVO.contractID = ''; //契約編號
    		$scope.inputVO.trustTS = 'S';  //M:金錢信託  S:特金交易--
			$scope.inputVO.tradeSEQ = ''; 
			$scope.inputVO.custID = ''; 								//客戶ID
			$scope.inputVO.custName = '';
			$scope.custClear(); 
			$scope.prodClearM(true);
			$scope.inputVO.custType = 'CUST';
			$scope.inputVO.narratorID = projInfoService.getUserID();  //解說專員員編
			$scope.inputVO.narratorName = projInfoService.getUserName();  //解說專員姓名
			$scope.getReserveTradeDate();  // 取N day getTradeDate 營業日
			$scope.inputVO.contractID = ''; //契約編號
			$scope.checkTradeDateType();//預設即時或預約
			$scope.overTradeTime = false;
			$scope.inputVO.isWeb = 'N';		//臨櫃交易N, 快速申購Y
			$scope.inputVO.prospTypeDisabled = false;
			$scope.inputVO.hnwcYN = ''; //是否為高資產客戶 Y/N 
			$scope.inputVO.hnwcServiceYN = ''; //可提供高資產商品或服務 Y/N 
			$scope.inputVO.flagNumber = '' //90天內是否有貸款紀錄 Y/N
			$scope.inputVO.otherWithCustId = false; //是否帶客戶ID進來(快查)
			$scope.inputVO.transDate1 = "N"; //轉換日期
			$scope.inputVO.transDate2 = "N";
			$scope.inputVO.transDate3 = "N";
			$scope.inputVO.transDate4 = "N";
			$scope.inputVO.transDate5 = "N";
			$scope.inputVO.transDate6 = "N";
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
			if($scope.inputVO.otherWithCustId) { //有帶客戶ID(快查)
				$scope.queryChkSenior();
			} else {
				$scope.getSOTCustInfo(false,true);
			}
			$scope.connector('set','SOTCustID',null);
		};
		
		// SOT701-客戶電文
		$scope.getSOTCustInfo = function(loadEdit,input) {
			debugger
			//			console.log("SOTCustInfo"+loadEdit);
			var deferred = $q.defer();
			$scope.inputVO.custID = $filter('uppercase')($scope.inputVO.custID);
			var validCustID = validateService.checkCustID($scope.inputVO.custID); //自然人和法人檢查
			//			console.log("custID:" + $scope.inputVO.custID + ", checkCustID: "+validCustID);
			if(validCustID==false){ 
				$scope.inputVO.custID='';
			}
			
			if(validCustID==false || input) { 
				$scope.custClear();
				$scope.prodClearM(true);
			}
			if(validCustID) {
				//動態鎖利與一般基金的取得客戶資料方式相同，prodType先不變
				$scope.sendRecv("SOT110", "getSOTCustInfo", "com.systex.jbranch.app.server.fps.sot110.SOT110InputVO", {'custID':$scope.inputVO.custID, 'prodType':1, 'tradeType':1, 'trustTS':$scope.inputVO.trustTS},
						function(tota, isError) {
							if (!isError) { 
									$scope.toDay = $filter('date')(new Date(),'yyyy-MM-dd 00:00:00');//取當日日期
									//FOR CBS TEST日期
//								    if ($scope.toJsDate(tota[0].body.kycDueDate) < $scope.toJsDate($scope.toDay)) { //若KYC過期，需有訊息提示客戶，並清空客戶ID。
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
								    
								    $scope.isGetSOTCustInfo = undefined;
								    //			console.log("SOTCustInfo:"+JSON.stringify(tota[0].body));
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
									$scope.inputVO.custProType = tota[0].body.custProType;                  // 專業投資人類型
									$scope.inputVO.isFirstTrade = tota[0].body.isFirstTrade;                //是否首購
									$scope.inputVO.isBanker = tota[0].body.isBanker; 						//是否為行員
									$scope.inputVO.hnwcYN = tota[0].body.hnwcYN;
									$scope.inputVO.hnwcServiceYN = tota[0].body.hnwcServiceYN;
									$scope.inputVO.flagNumber = tota[0].body.flagNumber; 					//90天內是否有貸款紀錄 Y/N
									
									var car_debitAcct = $scope.inputVO.debitAcct;     //扣款帳號
									var car_feeType = $scope.inputVO.feeType;  //手續費優惠方式
									var car_bargainApplySEQ = $scope.inputVO.bargainApplySEQ; //議價編號
									var car_groupOfa = $scope.inputVO.groupOfa; //優惠團體代碼  (從手續費優惠方式)
									var car_raiseAmt = $scope.inputVO.raiseAmt;     //申購金額 
									debugger
									
									//			console.log("set SOT.DEBIT_ACCT_LIST 會呼叫 changeAcct()");
									$scope.mappingSet['SOT.DEBIT_ACCT_LIST'] = tota[0].body.debitAcct;
									
									//			console.log("SOTCustInfo !$scope.inputVO.seqNo:"+(!$scope.inputVO.seqNo) + "!loadEdit:" + (!loadEdit));
//									if (!loadEdit || !$scope.inputVO.seqNo) {//沒有設loadEdit  從編輯按鈕(風控)過來不寫預設  因為信託帳號跟手續費有關 !$scope.inputVO.seqNo
									if (!loadEdit) {
//										$scope.inputVO.debitAcct = (tota[0].body.debitAcct.length > 0 ? tota[0].body.debitAcct[0].LABEL : "");				//扣款帳號
										$scope.isGetSOTCustInfo = true;
										$scope.checkTrustAcct();  //1.檢查信託帳號 設定扣款帳號  2.顯示debitAcct餘額 
									} else if(loadEdit==true) {
										//			console.log("car_debitAcct:"+car_debitAcct);
										//			console.log("car_trustAcct:"+car_trustAcct);
										//			console.log("car_creditAcct:"+car_creditAcct);
										$scope.inputVO.debitAcct = car_debitAcct;     //扣款帳號
										$scope.inputVO.raiseAmt = car_raiseAmt;     //申購金額 
										
										//扣款帳號
										$scope.isGetSOTCustInfo=true; //for changeAcct()
										$scope.checkTrustAcct();  //1.檢查信託帳號  2.顯示debitAcct餘額 
										
										$scope.inputVO.feeType = car_feeType;  //手續費優惠方式
										$scope.inputVO.bargainApplySEQ = car_bargainApplySEQ; //議價編號
										$scope.inputVO.groupOfa = car_groupOfa; //優惠團體代碼  (從手續費優惠方式)
										//			console.log("car_getFeeTypeListData");
										$scope.getFeeTypeListData(true,$scope.inputVO.bargainApplySEQ,$scope.inputVO.feeType);
									}
									
									//從FPS來的不清
									if (!$scope.fromFPS) {
										if(loadEdit!=true) {
											$scope.prodClearM(true);//清除產品
										}
									}									

									if ($scope.brgApplySingle) {//事先議價
										$scope.inputVO.prodId = $scope.brgApplySingle.PROD_ID;
										//再找產品
										//			console.log('fromCRM421bargain 2 getProdDTL');
										$scope.getProdDTL("M", "");
									} 
									deferred.resolve("success");
									return deferred.promise;
							} else {
								//查無客戶 需清空資料
								$scope.inputVO.custID='';
								$scope.custClear();
								$scope.prodClearM(true);//清除產品
							}
				});
			}
			return deferred.promise;
		};
		
		//手續費優惠方式清單 (含最優)
		//目前先輸入申購金額才查手續費列表和最優手續費
		$scope.getFeeTypeListData = function (isLoadEdit,carBargainApplySEQ,carFeeType) { 
			debugger
			var deferred = $q.defer();
			var canGetFeeType = false;
			var raiseAmt = $scope.moneyUnFormat($scope.inputVO.raiseAmt);//千分位問題
			if(raiseAmt>0 && $scope.inputVO.trustCurrType) {
				canGetFeeType = true;
			}
			
			if(canGetFeeType){
				var getFeeTypeListInputVO = {"custID": $scope.inputVO.custID, 
						            "trustAcct": $scope.inputVO.trustAcct, 
						            "trustCurrType": $scope.inputVO.trustCurrType, 
						            "tradeSubType": "1", //固定送申購 
						            "prodId":  $scope.inputVO.prodId, //母基金
						            "purchaseAmt": $scope.moneyUnFormat($scope.inputVO.raiseAmt),
						            "feeTypeIndex": $scope.inputVO.feeTypeIndex,
						            "feeRate": $scope.inputVO.feeRate,
						            "fee": $scope.moneyUnFormat($scope.inputVO.fee),
						            "feeDiscount": $scope.moneyUnFormat($scope.inputVO.feeDiscount),
						            "prodName":$scope.inputVO.prodName,
						            "trustTS" : "S",
						            "isOBU" : $scope.inputVO.isOBU,
						            "dynamicYN" : "Y"
						            };
				$scope.sendRecv("SOT110", "getFeeTypeListData", "com.systex.jbranch.app.server.fps.sot110.SOT110InputVO", getFeeTypeListInputVO,
					function(tota1, isError) {
						debugger
					    $scope.err = {};
						if (!isError) {
							var feeTypeIndex_temp = '';
							$scope.feeTypeListData = tota1[0].body.feeTypeList;
							$scope.mappingSet['FEE_RATE_TYPE'] = []; 
							angular.forEach($scope.feeTypeListData, function(row) {
								if (row.feeTypeIndex != 'idx99') {  //[99最優]不放進去 
									$scope.mappingSet['FEE_RATE_TYPE'].push({LABEL:row.LABEL, DATA:row.feeTypeIndex});
									if(carBargainApplySEQ){
										if(carBargainApplySEQ == row.BeneCode){
											feeTypeIndex_temp = row.feeTypeIndex;
										}
									}
								
								} else {
									$scope.inputVO.defaultFeeRate = row.DefaultFeeRateL;
								} 
			    			});
							if (carBargainApplySEQ) {
								$scope.inputVO.feeTypeIndex=tota1[0].body.feeTypeIndex;//保留上次選的手續費方式 
							} else {
								$scope.inputVO.feeTypeIndex=''; //下拉重設'請選擇'
								$scope.getFeeType();
							}
							//議價選擇
							if(feeTypeIndex_temp != ''){
								$scope.inputVO.feeTypeIndex = feeTypeIndex_temp;
								$scope.getFeeType(isLoadEdit,carBargainApplySEQ,carFeeType);//先跑最優收續費計算 (優惠方式'空白'即為最優)
							}
							$scope.showSuccessMsg("已經重新計算手續費優惠方式列表"); 
							
						} else {
							$scope.err.fee_isFeeRateTypeError = true;
							if (tota1.body.msgData) {
								
							}
						}
						
				});
			}
			return deferred.promise;
		};
		
//		//取幣別小數點位數
//        $scope.getCurrScale = function(curr){
//        	$scope.currList = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: curr});		
//			return $scope.currList[0].LABEL;
//        }
		
		$scope.notEmptyValue = function(value) {
			if(value == undefined || value == null || value == "")
				return false;
			else
				return true;
		}
		
		//基金表定、最優手續費
		//重選信託帳號要 再查最優手續費
		$scope.getDefaultFeeRate = function () {
			$scope.inputVO.raiseAmt = $scope.moneyUnFormat($scope.inputVO.raiseAmt);
			$scope.inputVO.fee = $scope.moneyUnFormat($scope.inputVO.fee);
			$scope.inputVO.feeDiscount = $scope.moneyUnFormat($scope.inputVO.feeDiscount);
			debugger
			if ($scope.notEmptyValue($scope.inputVO.prodId) && $scope.isGetSOTCustInfo && 
					$scope.notEmptyValue($scope.inputVO.trustCurrType) && $scope.notEmptyValue($scope.inputVO.trustAcct)) { //查詢完客戶後才能 查 基金表定、最優手續費
				$scope.sendRecv("SOT110", "getDefaultFeeData", "com.systex.jbranch.app.server.fps.sot110.SOT110InputVO", $scope.inputVO ,
						function(tota1, isError) {
					debugger
							if (!isError) { 
								if(!$scope.feeTypeListData)
									$scope.feeTypeListData=[];
								$scope.feeTypeListData[0] = tota1[0].body.feeTypeList[0]; //置換最優的優惠方式[請選擇] 
								//不要預設最優$scope.inputVO.feeType="C";
								$scope.inputVO.feeTypeIndex=tota1[0].body.feeTypeIndex;//保留上次選的手續費方式 
								 
								for (var i = 0; i < $scope.feeTypeListData.length; i++){	
									var row = $scope.feeTypeListData[i];
									if (row.feeTypeIndex == 'idxAPPLY') {  
										$scope.feeTypeListData[i] = tota1[0].body.feeTypeList[1]; //置換目前[申請議價]
									} 
								}
								$scope.showSuccessMsg("已經重新計算最優手續費");
								$scope.getFeeType();
								return;
							}
				});
			}
		};
		
		//切換手續費方式 ,取得手續費率(高中低  或 申請的手續費)
		$scope.getFeeType = function (isLoadEdit,carBargainApplySEQ,carFeeType) {
			debugger
			/*FeeType A：申請議價  B：生日券使用  C：最優手續費 D：單次議價*/
			$scope.inputVO.bargainApplySEQ = undefined;
			$scope.inputVO.groupOfa = undefined;
			$scope.inputVO.feeType = undefined;
			//不要清空$scope.inputVO.trustCurr = undefined;
			//就算重算手續費，不要清空$scope.inputVO.feeRate = undefined;      //手續費率 
			//就算重算手續費，不要清空$scope.inputVO.fee = undefined;     //手續費金額 
			//就算重算手續費，不要清空$scope.inputVO.feeDiscount = undefined;     //手續費折數 
			
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
						$scope.inputVO.feeRate = row.FeeRate;
						$scope.getFee('rate');//計算折數
						$scope.inputVO.feeRateData = JSON.stringify(row);
						$scope.feeRateDisabled = true;
					}
				}
				
				//B生日券使用
				if ($scope.inputVO.feeType == 'B') {
					$scope.feeRateDisabled = true;
				}
					
				//A申請議價
				if ($scope.inputVO.feeType == 'A' && 'idxAPPLY'== row.feeTypeIndex) {
					if(isLoadEdit!=true){	//若不是編輯資料過來  要清空
						
							/*cathy說要保留原申請議價，即使重算手續費方式 
							   $scope.inputVO.feeRate = '';
							   $scope.getFee('rate');//計算折數
				             */
							
							//取得[保留原申請議價]
							$scope.inputVO.feeRate = row.FeeRate; //from java or HTML
							$scope.getFee('rate');//計算折數
							$scope.inputVO.feeRateData = JSON.stringify(row);
						 
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
					$scope.inputVO.feeRate = row.FeeRateL;
				    $scope.inputVO.fee = row.FeeL;
				    $scope.inputVO.defaultFeeRate = row.DefaultFeeRateL; 
				    //$scope.inputVO.trustCurr = row.TrustCurr;
				    $scope.getFee('rate');//計算折數

					$scope.inputVO.groupOfa = row.GroupOfa;   //團體優惠代碼 (最優手續費_要記錄團體優惠代碼)
					$scope.inputVO.trustCurr = row.TrustCurr; //投資幣別
					$scope.feeRateDisabled = true;
					$scope.inputVO.feeRateData = JSON.stringify(row); 
				}
			}); 
				
			$scope.checkBestFee();//檢查手續費是否最優
		};
		
		//檢查手續費是否最優(比較DefaultFeeRateVO.fee L/M/H)
		$scope.checkBestFee = function () {
			var bestFeeType = $scope.feeTypeListData[0];
			/*若此時選擇的優惠劣於既有的身分別或期間議價優惠，
			則會跳出提醒訊息「客戶已享有手續費率XX%，
			請選擇其他手續費優惠方式」。跳出警語後，
			還原到預設優惠折扣如手續費率、手續費金額、折數
			(「身分別」、「期間議價」其中最優)，手續費優惠方式變成'請選擇'
			*/
			
			var errMsgParam = ''; 
			if (Number(bestFeeType.FeeRateL) < Number($scope.inputVO.feeRate)) {
		    	errMsgParam = bestFeeType.FeeRateL + '%'; 
		    } 
			if (errMsgParam!='') {
				$scope.showErrorMsg('「客戶已享有手續費率' + errMsgParam + ' ，請選擇其他手續費優惠方式」');
				//20210329_#0394_金錢信託_選擇折數>預設優惠折數擋下單
				if($scope.inputVO.trustTS == 'M'){
					 $scope.inputVO.feeTypeIndex = ''; //下拉重設'請選擇'(最優)
					 $scope.getFeeType();
				}
				//因為會有重算最優， 不重新選最優  $scope.inputVO.feeTypeIndex = ''; //下拉重設'請選擇'(最優)
				//因為會有重算最優， 不重新選最優  $scope.getFeeType();
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
			//			console.log("getCustAcctData"); 
			$scope.setCustAcctData();  //setCustAcctData CALL  $scope.checkTrustAcct()
			//TODO 改變信託帳號  則要變換 手續費
							
			if ($scope.brgApplySingle) {//事先議價 
				//			console.log('fromCRM421bargain 5 FeeTypeListData()');
				$scope.getFeeTypeListData(true,$scope.brgApplySingle.bargainApplySEQ ,'D'); //查詢表定手續和其他事先議價
				$scope.checkPurchaseAmtLimit();//因查BaseFee花時間不能馬上查到
			} 
		} 
		
		// getFee 手續費率和折數計算
		$scope.getFee = function(type) {
			if($scope.inputVO.feeType != "E"){
				$scope.inputVO.raiseAmt = $scope.moneyUnFormat($scope.inputVO.raiseAmt);
				if(type == 'rate') {					
					$scope.inputVO.fee = $filter('number')($scope.inputVO.raiseAmt * $scope.inputVO.feeRate / 100 , '2');
					$scope.inputVO.feeDiscount = $filter('number')($scope.inputVO.feeRate / $scope.inputVO.defaultFeeRate * 10 , '2');
					if($scope.inputVO.fee=="" || $scope.inputVO.fee==0.0000){
						$scope.inputVO.fee=0;
						$scope.inputVO.feeDiscount=0;
					}
				} else {
					if($scope.inputVO.feeDiscount) {
						$scope.inputVO.feeRate = $filter('number')($scope.inputVO.feeDiscount * $scope.inputVO.defaultFeeRate / 10 , '2');
						$scope.inputVO.fee = $filter('number')($scope.inputVO.raiseAmt * $scope.inputVO.feeRate / 100 , '2');
					}
				}
			}
			
			//[保留原申請議價],讓可以切換後手續費後還能拿到原申請議價
			if ($scope.inputVO.feeTypeIndex=="idxAPPLY") {
				var idxApplyData = {
					LABEL:"申請議價",
				    feeType: "A", //A：申請議價
				    feeTypeIndex:"idxAPPLY",
				    FeeRate:$scope.inputVO.feeRate,
					Fee: $scope.inputVO.fee,
					FeeDiscount: $scope.inputVO.feeDiscount
				}
				for (var i = 0; i < $scope.feeTypeListData.length; i++){	
					var row = $scope.feeTypeListData[i];
					if (row.feeTypeIndex == 'idxAPPLY') {  
						$scope.feeTypeListData[i] = idxApplyData; //保留WEB申請議價
					} 
				}
			}

			$scope.getCurrency($scope.inputVO.prodCurr);
		};
		
		//將購物車該筆相同seqNo編號 顯示在編輯區
		$scope.edit = function(row) {
			debugger
//			console.log('edit(row)'+JSON.stringify(row)); 
			$scope.inputVO.seqNo = row.SEQ_NO;
			$scope.inputVO.batchSeq = row.BATCH_SEQ;     //下單批號
			$scope.inputVO.batchNo = row.BATCH_NO;     //下單批號流水號
			$scope.inputVO.tradeSubType = row.TRADE_SUB_TYPE;     //信託型態
			$scope.inputVO.prodId = row.PROD_ID;     		//基金代號
			$scope.inputVO.prodName = row.PROD_NAME;     	//基金名稱
			$scope.inputVO.prodCurr = row.PROD_CURR;     	//計價幣別
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
			$scope.inputVO.raiseAmt = row.RAISE_AMT;     //申購金額 
			$scope.inputVO.defaultFeeRate = row.DEFAULT_FEE_RATE;     //表定手續費率 
			$scope.inputVO.bargainStatus = row.BARGAIN_STATUS;     //議價狀態 (若為申請議價)
			$scope.inputVO.feeRate = row.FEE_RATE;     //手續費率
			$scope.inputVO.fee = row.FEE;     //手續費金額
			$scope.inputVO.feeDiscount = row.FEE_DISCOUNT;     //手續費折數
			$scope.inputVO.certificateID = row.CERTIFICATE_ID;	//庫存憑證編號
			 
			$scope.inputVO.tradeDateType = row.TRADE_DATE_TYPE;     //交易日期類別
			$scope.inputVO.tradeDate = row.TRADE_DATE;     //交易日期
			$scope.inputVO.narratorID = row.NARRATOR_ID;     //解說專員員編
			$scope.inputVO.narratorName = row.NARRATOR_NAME;     //解說專員姓名
			 
			var acctCurrency = $scope.getAcctCurrency();
			$scope.inputVO.debitAcct = row.DEBIT_ACCT + '_'+ acctCurrency;     //扣款帳號
			$scope.inputVO.trustAcct = row.TRUST_ACCT;     //信託帳號
			$scope.inputVO.creditAcct = row.CREDIT_ACCT;     //收益入帳帳號
			$scope.changeAcct('debit'); //顯示debitAcct餘額
			$scope.inputVO.prospectusType = row.PROSPECTUS_TYPE; //取得公開說明書方式
			$scope.inputVO.feeType = row.FEE_TYPE;     //手續費優惠方式
			$scope.inputVO.bargainApplySEQ = row.BARGAIN_APPLY_SEQ;     //議價編號 
			//重新查手續費優惠方式
			$scope.getFeeTypeListData(true,$scope.inputVO.bargainApplySEQ,$scope.inputVO.feeType) ;
		    $scope.inputVO.groupOfa = row.GROUP_OFA;//優惠團體代碼  (從手續費優惠方式)
			
		    $scope.inputVO.prodId = row.PROD_ID;		//母基金只有商品代碼不一樣，其他帶一般基金商品變數名稱
		    $scope.inputVO.prodMinGrdAmt = row.PROD_MIN_GRD_AMT;
		    $scope.inputVO.prodIdC1 = row.PROD_ID_C1;
			$scope.inputVO.prodNameC1 = row.PROD_NAME_C1;			//商品名稱
			$scope.inputVO.prodRiskLvC1 = row.PROD_RISK_LV_C1;
			$scope.inputVO.purchaseAmtC1 = row.PURCHASE_AMT_C1;     //申購金額 
			
			$scope.inputVO.prodIdC2 = row.PROD_ID_C2;
			$scope.inputVO.prodNameC2 = row.PROD_NAME_C2;			//商品名稱
			$scope.inputVO.prodRiskLvC2 = row.PROD_RISK_LV_C2;
			$scope.inputVO.purchaseAmtC2 = row.PURCHASE_AMT_C2;     //申購金額 
			
			$scope.inputVO.prodIdC3 = row.PROD_ID_C3;
			$scope.inputVO.prodNameC3 = row.PROD_NAME_C3;			//商品名稱
			$scope.inputVO.prodRiskLvC3 = row.PROD_RISK_LV_C3;
			$scope.inputVO.purchaseAmtC3 = row.PURCHASE_AMT_C3;     //申購金額 
			
			$scope.inputVO.prodIdC4 = row.PROD_ID_C4;
			$scope.inputVO.prodNameC4 = row.PROD_NAME_C4;			//商品名稱
			$scope.inputVO.prodRiskLvC4 = row.PROD_RISK_LV_C4;
			$scope.inputVO.purchaseAmtC4 = row.PURCHASE_AMT_C4;     //申購金額 
			
			$scope.inputVO.prodIdC5 = row.PROD_ID_C5;
			$scope.inputVO.prodNameC5 = row.PROD_NAME_C5;			//商品名稱
			$scope.inputVO.prodRiskLvC5 = row.PROD_RISK_LV_C5;
			$scope.inputVO.purchaseAmtC5 = row.PURCHASE_AMT_C5;     //申購金額 
			
			$scope.inputVO.transDate = row.TRANSFER_DATE;
			$scope.inputVO.engagedROI1 = row.ENGAGED_ROI1;
			$scope.inputVO.engagedROI2 = row.ENGAGED_ROI2;
			$scope.inputVO.engagedROI3 = row.ENGAGED_ROI3;
		};
		
		//取得最低申購金額&累進申購面額
		//只有母基金
		$scope.findNfMinBuyAmt = function(type, idx){
			var MIN_BUY_AMT = "SOT.NF_MIN_BUY_AMT_DM_2";
			var PARAM_CODE = $scope.inputVO.trustCurr; //產品信託幣別
			
			//最低申購金額
	        var vo = {'param_type': MIN_BUY_AMT, 'desc': false};
	        
	        $scope.requestComboBox(vo, function(totas) {
	        	if (totas[totas.length - 1].body.result === 'success') {
        			angular.forEach(totas[0].body.result, function(row){
        	       		if(row.DATA == PARAM_CODE){
        	       			if(type == "M") {
        	       				$scope.inputVO.prodMinBuyAmt= row.LABEL;
        	       				$scope.inputVO.BaseAmtOfSPurchaseCurr = row.DATA.split("_")[0];
        	       			}
        	       		}
        	       	});
	        	}
	        });
	        	
	        //累進申購面額
	        var MIN_GRD_AMT = "SOT.NF_MIN_GRD_AMT_DM_2";			
			var vog = {'param_type': MIN_GRD_AMT, 'desc': false};
	        	$scope.requestComboBox(vog, function(totas) {
	        		if (totas[totas.length - 1].body.result === 'success') {
        				angular.forEach(totas[0].body.result, function(row){
        	        		if(row.DATA == PARAM_CODE){
        	        			if(type == "M") {
        	        				$scope.inputVO.prodMinGrdAmt= row.LABEL;
        	        			}
        	        		}
        	        	});
	        		}
	        });
		}
		
		//檢查最低申購金額&累進申購面額
		$scope.checkPurchaseAmtLimit = function() {
			var prodMinBuyAmt = Number($scope.inputVO.prodMinBuyAmt);
			var prodMinGrdAmt = Number($scope.inputVO.prodMinGrdAmt);
			var raiseAmt = Number($scope.inputVO.raiseAmt);
			
			if (raiseAmt && prodMinBuyAmt && prodMinGrdAmt && (raiseAmt < prodMinBuyAmt || raiseAmt % prodMinGrdAmt != 0)) {
				$scope.showErrorMsg("ehl_01_sot310_007");  //申購面額須高於最低申購面額，且須為累進申購面額之倍數。
				$scope.inputVO.raiseAmt = undefined;
			}
		}
		
		//約定報酬率
		$scope.engagedROIDisplayChanged = function() {
			if($scope.inputVO.engagedROIDisplay == "0") {
				$scope.inputVO.engagedROI = "";
			} else {
				$scope.inputVO.engagedROI = $scope.inputVO.engagedROIDisplay;
			}
		}
		
		//約定報酬率
		$scope.engagedROIChanged = function() {
			if($scope.inputVO.engagedROI == 5 || $scope.inputVO.engagedROI == 10 || $scope.inputVO.engagedROI == 15 ||  
					$scope.inputVO.engagedROI == 20 || $scope.inputVO.engagedROI == 25 || $scope.inputVO.engagedROI == 30) {
				$scope.inputVO.engagedROIDisplay = $scope.inputVO.engagedROI;
			} else {
				$scope.inputVO.engagedROIDisplay = "0";
			}
		}
		
		//選擇信託類型
		$scope.tradeType = function(){
			$scope.findNfMinBuyAmt("M", "");
		}
		
		//依台幣信託外幣信託:  設定信託幣別  CALL  $scope.checkTrustAcct()
		$scope.setTrustCurr = function() {
			//檢查信託帳號
			$scope.checkTrustAcct(); 
		}
		
		//選擇信託幣別
		$scope.currencyType = function() {
			//			console.log("currencyType()選擇信託幣別");
			/*1.選擇台幣顯示台幣最低申購金額。
			2.選擇外幣顯示外幣最低申購金額。
			3. 當信託幣別改變時，表定手續費率、手續費優惠費率也將 改變。
   			呼叫SOT 709 .js –fetchEsbNfDefaultFeeRate(基金代號、台外幣別、申購金額、信託帳號)
			 */
			//20201124_#0397_OBU信託幣別不可選台幣
			if($scope.inputVO.isOBU == 'Y' && ($scope.inputVO.trustCurrType == 'C' || $scope.inputVO.trustCurrType == 'N')){
				$scope.showErrorMsg("ehl_01_SOT110_001"); 
				$scope.inputVO.trustCurrType = undefined;
				return;
			}
			$scope.setTrustCurr(); //設定信託幣別  CALL  $scope.checkTrustAcct()
			$scope.findNfMinBuyAmt("M", "");
			
			if ($scope.brgApplySingle) {//事先議價 
				//			console.log('fromCRM421bargain 4 CustAcctData()');
				$scope.getCustAcctData();    //1.設定客戶帳號  CALL  $scope.checkTrustAcct()； 2.事先議價 
			}
			$scope.inputVO.raiseAmt = undefined; //清空申購金額
			
			$scope.getCustAcctData();        //1.設定客戶帳號  CALL  $scope.checkTrustAcct()； 2.事先議價   
			//$scope.checkFitness();                  //檢查適配
			$scope.getFeeTypeListData();            //手續費優惠方式
			
			//2017-7-5 設定幣別小數位數
			$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.avlCurrency});
			$scope.num = $scope.mod[0].LABEL;
			
			//控管國內貨幣型基金交易時間，超過10:30 轉為預約交易狀態	
			//境外私募基金不用判斷，都是預約交易
			if($scope.inputVO.prodId != null && $scope.inputVO.prodId != '' && $scope.inputVO.ovsPrivateYN != "Y"){ 
				$scope.sendRecv("SOT110", "checkReserve", "com.systex.jbranch.app.server.fps.sot110.SOT110InputVO", $scope.inputVO,
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
			//境外私募基金不用判斷，都是預約交易
			if($scope.inputVO.ovsPrivateYN == "Y"){ 
				$("#tradeDateType1").attr("disabled", true);
				$("#tradeDateType2").attr("checked", true);
				$scope.inputVO.tradeDateType = '2';
			}
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
		
		//取得動態鎖利商品資料
		$scope.getProdDTL = function(type, idx) {
			var deferred = $q.defer();
			
			var obj = eval("$scope.inputVO.prodId" + (type == "M" ? "" : type) + idx);
			//將要搜尋的母基金或子基金放到PROD_ID
			var prodId = $filter('uppercase')(obj);
		    
			if(prodId) {
				$scope.sendRecv("SOT1620", "getProdDTL", "com.systex.jbranch.app.server.fps.sot1620.SOT1620InputVO", {"prodId": prodId},
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.errorMsg != null && tota[0].body.errorMsg != "") {
								//適配有錯誤訊息
								$scope.showErrorMsg(tota[0].body.errorMsg);
								$scope.prodClearD(type, idx, true);
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
										} else {
											$scope.prodClearD(type, idx, true);
											deferred.resolve("");
										}
									});
								}		
								
								if (tota[0].body.prodDTL[0]) {
									if(type == "M") {
										//母基金商品資料
										$scope.setProdDataM(tota[0].body);
									} else {
										//子基金商品資料
										if(idx == "1") $scope.setProdDataC1(tota[0].body);
										if(idx == "2") $scope.setProdDataC2(tota[0].body);
										if(idx == "3") $scope.setProdDataC3(tota[0].body);
										if(idx == "4") $scope.setProdDataC4(tota[0].body);
										if(idx == "5") $scope.setProdDataC5(tota[0].body);
									}
								}
								
//								$scope.findNfMinBuyAmt(type, idx);  //最低申購金額
								
								if(type == "M") { //母基金
									if ($scope.brgApplySingle) {//事先議價
//										$scope.inputVO.tradeSubType = $scope.brgApplySingle.tradeSubType;//1.單筆申購(預設)
										 
										//信託幣別
										$scope.inputVO.trustCurrType = $scope.brgApplySingle.trustCurrType;
//										$scope.currencyType();       //選擇信託幣別 查最低申購金額
										$scope.inputVO.raiseAmt = $scope.brgApplySingle.PURCHASE_AMT; //設定申購金額
										$scope.findNfMinBuyAmt(type, idx);
										$scope.getCustAcctData();        //1.設定客戶帳號  CALL  $scope.checkTrustAcct()； 2.事先議價 
										$scope.getFeeTypeListData(false, $scope.brgApplySingle.bargainApplySEQ, 'D');

									}
									$scope.currencyType();
									$scope.getCurrency($scope.inputVO.prodCurr);
								}
								return;
							} else {
								$scope.showErrorMsg("ehl_01_common_009");					
								$scope.prodClearD(type, idx, true);
							}
						}
						//查無商品 需清除商品ID
						$scope.prodClearD(type, idx, true);
				});
			}
			return deferred.promise;
		};
		
		//查詢購物車
		$scope.noCallCustQuery = function () {
			debugger
			var deferred = $q.defer();
			$scope.sendRecv("SOT1620", "query", "com.systex.jbranch.app.server.fps.sot1620.SOT1620InputVO", {"tradeSEQ": $scope.inputVO.tradeSEQ},
					function(tota, isError) {
						debugger
						if (!isError) {
							$scope.carList = tota[0].body.carList;
							$scope.outputVO = tota[0].body;
							
							if (tota[0].body.carList[0]) { //avoid carList is empty
								$scope.inputVO.narratorID = tota[0].body.carList[0].NARRATOR_ID;		//解說人員ID
								$scope.inputVO.narratorName = tota[0].body.carList[0].NARRATOR_NAME;	//解說人員姓名
							}
							$scope.narratorDisabled = true;
							if (tota[0].body.mainList[0] && tota[0].body.mainList[0].CUST_ID) { //avoid mainList is empty when del all
								$scope.inputVO.custID = tota[0].body.mainList[0].CUST_ID;			//客戶ID
								$scope.inputVO.isWeb = tota[0].body.mainList[0].IS_WEB;				//臨櫃交易N, 快速申購Y
							}
							//風控頁按編輯回到下單頁 ,從購物車中取要編輯商品
							angular.forEach($scope.carList, function(row){
								$scope.edit(row);//將該筆相同seqNo,顯示在編輯區
			    			});
							deferred.resolve("success");
						}
			});
			
			return deferred.promise;
		};
		
		$scope.changeAcct = function (type) {
			debugger
			var trustCurrType = $scope.inputVO.trustCurrType; //C國內 , N台幣 , Y外幣
			
			if($scope.isGetSOTCustInfo){ //查詢完客戶後才能changeAcct()
				//取得 相同商品別的 扣款帳後餘額
				$scope.avlCurrency=undefined;
				$scope.inputVO.debitAvbBalance=undefined; 
				//有幣別和餘額
				angular.forEach($scope.mappingSet['SOT.DEBIT_ACCT_LIST#DISPLAY'], function(acctCcyRow){
					debugger
					if ($scope.inputVO.debitAcct == acctCcyRow.DATA) {  
							//$scope.inputVO.debitAvbBalance = row.ACCT_BALACNE;
							$scope.inputVO.debitAvbBalance = acctCcyRow.AVB_BALANCE;
							$scope.avlCurrency = acctCcyRow.CURRENCY;
							//			console.log('debitAcct acct:'+acctCcyRow.DATA+' currency:'+$scope.avlCurrency +', debitAvbBalance:'+$scope.inputVO.debitAvbBalance);
					}
				});
				
				if($scope.mappingSet['SOT.DEBIT_ACCT_LIST#DISPLAY'].length==0 && $scope.raiseAmtDisable != 'Y' && $scope.inputVO.trustTS != 'M'){ //0筆
					$scope.cmbDebitAcct=true;
					$scope.showErrorMsg("無扣款帳號");
					$scope.raiseAmtDisable = 'Y';
				}else if ($scope.mappingSet['SOT.DEBIT_ACCT_LIST#DISPLAY'].length > 0 && $scope.inputVO.trustTS != 'M'){
					$scope.raiseAmtDisable = 'N';
				}				
			} 
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
		$scope.setAcctDisplay = function(acctNameList,prodCurr) {
			debugger
			for (var i=0;i<acctNameList.length;i++) {
				if (prodCurr) {
					$scope.mappingSet[acctNameList[i]+'#DISPLAY'] = $filter('filter')($scope.mappingSet[acctNameList[i]], {CURRENCY: prodCurr});
				} else {
					$scope.mappingSet[acctNameList[i]+'#DISPLAY'] = $scope.mappingSet[acctNameList[i]];
				}
			}
		};
		
		//檢查信託帳號
		$scope.checkTrustAcct = function(){
			debugger
			$scope.cmbDebitAcct=false;
			$scope.avlBalance = undefined;                              //扣款帳號餘額
			$scope.avlCurrency = undefined;                             //扣款帳號餘額幣別
			var debitAcctListDisplay='SOT.DEBIT_ACCT_LIST#DISPLAY';
			var debitAcctList='SOT.DEBIT_ACCT_LIST';
			
			$scope.setAcctDisplay([debitAcctList]); //初始化
			
			//商品幣別 無此幣扣款帳號要增加
			if ($scope.inputVO.prodCurr) {
				if (sotService.is168($scope.inputVO.trustAcct)) {
					var checkFlag = false;
					angular.forEach($scope.mappingSet[debitAcctListDisplay], function(row2){
						var checkAcct = $scope.inputVO.trustAcct+'_'+$scope.inputVO.prodCurr;//檢核是否有此幣別帳號
						if (checkAcct == row2.DATA) {
							checkFlag = true;
						}
					});
					if (!checkFlag) {
						$scope.mappingSet[debitAcctListDisplay].push({LABEL: $scope.inputVO.trustAcct+'_'+$scope.inputVO.prodCurr, 
																	  DATA: $scope.inputVO.trustAcct+'_'+$scope.inputVO.prodCurr,
																	  AVB_BALANCE:'0',
																	  CURRENCY:$scope.inputVO.prodCurr,
																	  label:$scope.inputVO.trustAcct+'_'+$scope.inputVO.prodCurr,
																	  value:$scope.inputVO.trustAcct+'_'+$scope.inputVO.prodCurr});
					}
				}
			}				
				
			//信託帳號檢核
			if (sotService.is168($scope.inputVO.trustAcct)) {
				$scope.mappingSet[debitAcctListDisplay] = $filter('filter')($scope.mappingSet[debitAcctListDisplay],
																		{DATA: $scope.inputVO.trustAcct},
																		function(actual, expected) { return angular.equals(actual.split("_")[0], expected)}
																	  );
			}
				
			//有傳信託業務別 和 商品幣別 
			if ($scope.inputVO.trustCurrType) {
				if($scope.inputVO.trustCurrType=='N' || $scope.inputVO.trustCurrType=='C'){ //N台幣
		 			 $scope.mappingSet[debitAcctListDisplay] = $filter('filter')($scope.mappingSet[debitAcctListDisplay], {CURRENCY: 'TWD'});
		 			 //$scope.mappingSet[creditAcctListDisplay] = $filter('filter')($scope.mappingSet[creditAcctListDisplay], {CURRENCY: 'TWD'});
		 		  }else if($scope.inputVO.prodCurr){	  //計價
		 			  $scope.mappingSet[debitAcctListDisplay] = $filter('filter')($scope.mappingSet[debitAcctListDisplay], {CURRENCY: $scope.inputVO.prodCurr});
		 			  //$scope.mappingSet[creditAcctListDisplay] = $filter('filter')($scope.mappingSet[creditAcctListDisplay], {CURRENCY: $scope.inputVO.prodCurr}); 
		 		  }else if ($scope.inputVO.trustCurrType=='Y'){  //Y外幣
		 			  $scope.mappingSet[debitAcctListDisplay] = $filter('filter')($scope.mappingSet[debitAcctListDisplay],{CURRENCY: "TWD"},function(actual, expected) {  return !angular.equals(actual, expected)});
			 		  //$scope.mappingSet[creditAcctListDisplay] = $filter('filter')($scope.mappingSet[creditAcctListDisplay],{CURRENCY: "TWD"},function(actual, expected) {  return !angular.equals(actual, expected)});
		 		  }
			}
				
			if($scope.mappingSet[debitAcctListDisplay].length==1 && $scope.inputVO.trustTS == 'S'){ //只有一筆不能勾選
				$scope.inputVO.debitAcct=$scope.mappingSet[debitAcctListDisplay][0].DATA;
				$scope.cmbDebitAcct=true;
			}

			$scope.changeAcct('debit');//查詢餘額
		};
		
		//applyseq query
		$scope.queryApplyseq = function(prod){
			var deferred = $q.defer();
			
			//取得事先議價設定
			$scope.sendRecv("SOT110", "getCrmBrgApplySingle", "com.systex.jbranch.app.server.fps.sot110.SOT110InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.brgApplySingle = tota[0].body.brgApplySingle; //事先議價
							//避免議價編號被清掉
							$scope.brgApplySingle.bargainApplySEQ = $scope.inputVO.bargainApplySEQ;
							//資料庫查無信託幣別
							if(!$scope.brgApplySingle.TRUST_CURR){
								$scope.brgApplySingle.TRUST_CURR = prod.INVEST_CUR;
								
								if($scope.brgApplySingle.trustCurrType != 'C'){
									//假如信託業務別不為C(境內)時，TRUST_CURR(投資幣別)為TWD
									if($scope.brgApplySingle.TRUST_CURR == 'TWD'){
										//信託業務別(trustCurrType)=N(台幣)
										$scope.brgApplySingle.trustCurrType = 'N';
									}else{
										//信託業務別(trustCurrType)=Y(外幣)
										$scope.brgApplySingle.trustCurrType = 'Y';
									}
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
			$scope.carList = [];
			//預設值
			if($scope.connector('get','SOTCustID')){
				$scope.getTradeSEQ();
			    $scope.inputVO.custID = $scope.connector('get','SOTCustID');
			    $scope.connector('set','SOTCustID',null);
			    $scope.getSOTCustInfo(false,true).then(function(data) {
			    	debugger
			    	var dynaData = [];
			    	dynaData.prodM = $scope.connector('get', 'SOTProdM');
			    	dynaData.prodC1 = $scope.connector('get', 'SOTProdC1');
			    	dynaData.prodC2 = $scope.connector('get', 'SOTProdC2');
			    	dynaData.prodC3 = $scope.connector('get', 'SOTProdC3');
			    	dynaData.prodC4 = $scope.connector('get', 'SOTProdC4');
			    	dynaData.prodC5 = $scope.connector('get', 'SOTProdC5');
			    	//寫入庫存資料
			    	$scope.putInStockData(dynaData);
			    	//清空帶入資料
			    	$scope.connector('set','SOTProdM', null);
					$scope.connector('set','SOTProdC1', null);
					$scope.connector('set','SOTProdC2', null);
					$scope.connector('set','SOTProdC3', null);
					$scope.connector('set','SOTProdC4', null);
					$scope.connector('set','SOTProdC5', null);
				});
			}
		}
		
		$scope.query = function() {
			debugger
			$scope.carList = [];
			//預設值
			if ($scope.connector('get','SOTTradeSEQ')){
				$scope.inputVO.tradeSEQ = $scope.connector('get','SOTTradeSEQ');
				$scope.inputVO.seqNo = 1; //只有一筆，固定為1
				$scope.connector('set', 'SOTTradeSEQ', null); //避免跨交易返回本頁殘值
				$scope.connector('set', 'SOTCarSEQ', null);
				$scope.noCallCustQuery().then(function(data) {
					var loadEdit = true;
					$scope.getSOTCustInfo(loadEdit,false);
				});
			} else {
				// 取得新交易序號 
				$scope.getTradeSEQ();
			}
		};
		
		if(!$scope.connector('get','SOTCustID')) {
			//"不是"從資產總覽交易過來，維持原來
			$scope.inputVO.otherWithCustId = false;
			$scope.query();
		} else {
			//從資產總覽CRM821交易過來，帶CUSTID及庫存資料，先做高齡檢核
			$scope.inputVO.otherWithCustId = true;
			$scope.inputVO.custID = $scope.connector('get','SOTCustID');
			$scope.validateSeniorCust();
		}
		
		/**
		 * 確認基金註記 => 此交易檢查是否停止申購
		 */
		//確認基金註記 => 此交易檢查是否停止申購
		$scope.checkFundStatusM = () => $scope.sendRecv("SOT703", "qryFundMemo",
			"com.systex.jbranch.app.server.fps.sot703.SOT703InputVO", {prodId: $scope.inputVO.prodId},
			(tota) => tota[0].body[0] && tota[0].body[0].NO_E_PURCHASE ? $scope.showErrorMsg(tota[0].body[0].NO_E_PURCHASE) : $scope.getProdDTL("M", ""));		
		
	    $scope.initOverTradeTime = function() {
	    	$scope.overTradeTime = false;
	    };
	    
	  //暫存/下一步按鈕
		$scope.save = function(type) {
			$scope.checkTradeDateType().then(function(){ //交易日期類別是否可選擇即時/預約
				//必輸欄位檢核
				if($scope.parameterTypeEditForm.$invalid) {
		    		$scope.showErrorMsg("ehl_01_common_022");
		    		return;
		    	}
				debugger
				if($scope.overTradeTime){
					$scope.initOverTradeTime(); //判斷完再回到初始狀態
					return;
				}
								
				$scope.checkBestFee();//檢查手續費是否最優
				
				$scope.setTrustCurr(); //設定信託幣別 CALL  $scope.checkTrustAcct()
				$scope.inputVO.raiseAmt = $scope.cleanCurrency($scope.inputVO.raiseAmt);
				
			    $scope.inputVO.fee = $scope.cleanCurrency($scope.inputVO.fee);
			    //feeDiscount為""可能會導致inputVO抓不到值，因為inpuVO的feeDiscount是BigDecimal
			    if(!$scope.inputVO.feeDiscount){
			    	$scope.inputVO.feeDiscount = 0;
			    }
			    
			    $scope.carList = [];
			    
			    if(type == "1") { //暫存
			    	$scope.doSave();
			    } else { //下一步
			    	$scope.doNext();
			    }
				
			});
		}
		
		//暫存
		$scope.doSave = function() {
		    $scope.sendRecv("SOT1620", "save", "com.systex.jbranch.app.server.fps.sot1620.SOT1620InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.errorMsg != null) {
								$scope.showErrorMsg(tota[0].body.errorMsg);								
								return;
							} else {
								$scope.showMsg("ehl_01_common_025");	
								return;
							}
						}
			});
		}
		
		//下一步
		$scope.doNext = function() {
			debugger
			$scope.sendRecv("SOT1620", "save", "com.systex.jbranch.app.server.fps.sot1620.SOT1620InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if (tota[0].body.errorMsg != null) {
							$scope.showErrorMsg(tota[0].body.errorMsg);								
							return;
						} else {
							$scope.sendRecv("SOT1620", "next", "com.systex.jbranch.app.server.fps.sot1620.SOT1620InputVO", {tradeSEQ : $scope.inputVO.tradeSEQ, 'prodType':$scope.inputVO.prodType},
									function(tota, isError) {
										if (!isError) {
											debugger
											//有錯誤訊息
											if (tota[0].body.errorMsg != "") {
												$scope.showErrorMsg(tota[0].body.errorMsg);								
												return;
											}
											
											$scope.connector('set','SOTTradeSEQ', $scope.inputVO.tradeSEQ);
											$scope.connector('set','SOTCarSEQ', null);
											$rootScope.menuItemInfo.url = "assets/txn/SOT1621/SOT1621.html";							
											return;
										}
							});	
						}
					}
			});
		}
		
		$scope.goSOT132=function(){
			var custId = $scope.inputVO.custID;
			var trustTS = $scope.inputVO.trustTS;
			var debitAcct = $scope.inputVO.debitAcct;
			var cartList = $scope.cartList;
			var contractID = $scope.inputVO.contractID;
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT132/SOT132.html',
				className: 'SOT132',
				controller:['$scope',function($scope){
					$scope.txnName = "sot1620";
					$scope.custID=custId;
					$scope.trustTS=trustTS;
					$scope.debitAcct=debitAcct;
					$scope.dynamicYN = "Y"; //動態鎖利註記
				}]
			});
			dialog.closePromise.then(function(data){
				console.log(data);
				if(data.value && data.value != "cancel"){
					//先清除原庫存資料
					$scope.prodClearM(true);
					//將庫存資料放入欄位中
					$scope.putInStockData(data.value);
				}
				
				//#0695 排除數存戶
//				if($scope.inputVO.trustTS != 'M' && sotService.is168($scope.inputVO.trustAcct)
//						&& sotService.isDigitAcct($scope.inputVO.trustAcct,$scope.mappingSet['debit'])){
//					$scope.showErrorMsg("ehl_02_SOT_996");
//					$scope.prodClearM(true);
//				} 
			});
		};
		
		//將庫存資料放入欄位中
		$scope.putInStockData = function(data) {
			debugger
			//母基金
//			$scope.inputVO.is_backend = '';		//是否為後收型基金
			$scope.inputVO.prodId = data.prodM.FundNO; //母基金代號
			//電文PROD NAME 為簡寫，改取DB資料
			//$scope.inputVO.rdmProdName = data.FundName; //基金名稱
//			$scope.inputVO.tradeType       =   data.assetTradeSubType; //交易型態
//			$scope.inputVO.tradeTypeD 	  =   data.assetTradeSubTypeD; //詳細交易型態
			$scope.present_val				  =   Number(data.prodM.CurBal).toFixed(4);
			$scope.inputVO.present_val		  =   Number(data.prodM.CurBal).toFixed(4); //參考現值
			$scope.inputVO.certificateID  	  =   data.prodM.EviNum; //憑證編號
			$scope.inputVO.prodCurr        	  =   data.prodM.CurFund;//計價幣別
			$scope.inputVO.trustCurr          =   data.prodM.CurCode;//信託幣別
			$scope.inputVO.purchaseAmt		  =   data.prodM.CurAmt; //信託金額
//			$scope.inputVO.rdmUnit            =   data.CurUntNum; //原單位數
//			$scope.inputVO.numUnits           =   data.CurUntNum.toFixed(4); //單位數
			$scope.inputVO.trustAcct          =   data.prodM.AcctId02.trim();  //信託帳號
			$scope.inputVO.creditAcct         =   data.prodM.PayAcctId; //收益入帳帳號
			$scope.inputVO.branchNbr 		  =   $scope.inputVO.trustAcct ? $scope.inputVO.trustAcct.substring(2,5) : "";
			//約定申購子基金日
			var dateArray = [];
			if(data.prodM.SatelliteBuyDate1) dateArray.push(data.prodM.SatelliteBuyDate1);
			if(data.prodM.SatelliteBuyDate2) dateArray.push(data.prodM.SatelliteBuyDate2);
			if(data.prodM.SatelliteBuyDate3) dateArray.push(data.prodM.SatelliteBuyDate3);
			if(data.prodM.SatelliteBuyDate4) dateArray.push(data.prodM.SatelliteBuyDate4);
			if(data.prodM.SatelliteBuyDate5) dateArray.push(data.prodM.SatelliteBuyDate5);
			if(data.prodM.SatelliteBuyDate6) dateArray.push(data.prodM.SatelliteBuyDate6);
			dateArray.sort();
			$scope.inputVO.transDate = dateArray.join(", ");
			//約定報酬率
			$scope.inputVO.engagedROI1 =   data.prodM.BenefitReturnRate1;
			$scope.inputVO.engagedROI2 =   data.prodM.BenefitReturnRate2;
			$scope.inputVO.engagedROI3 =   data.prodM.BenefitReturnRate3;
//			$scope.unitValue 				  =   $scope.inputVO.present_val/$scope.inputVO.rdmUnit //單位(計算贖回方式之值) present_val= numUnits * unitValue
			$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.inputVO.trustCurr});
			//取得母基金商品資料
			$scope.getProdDTL("M", "");
			//子基金
			if(data.prodC1) {
				$scope.inputVO.prodIdC1 = data.prodC1.FundNO; 
				$scope.inputVO.purchaseAmtC1 = data.prodC1.CurAmt; //信託金額
				//取得子基金商品資料
				$scope.getProdDTL("C", "1");
			}
			if(data.prodC2) {
				$scope.inputVO.prodIdC2 = data.prodC2.FundNO;
				$scope.inputVO.purchaseAmtC2 = data.prodC2.CurAmt; //信託金額
				//取得子基金商品資料
				$scope.getProdDTL("C", "2");
			}
			if(data.prodC3) {
				$scope.inputVO.prodIdC3 = data.prodC3.FundNO; 
				$scope.inputVO.purchaseAmtC3 = data.prodC3.CurAmt; //信託金額
				//取得子基金商品資料
				$scope.getProdDTL("C", "3");
			}
			if(data.prodC4) {
				$scope.inputVO.prodIdC4 = data.prodC4.FundNO; 
				$scope.inputVO.purchaseAmtC4 = data.prodC4.CurAmt; //信託金額
				//取得子基金商品資料
				$scope.getProdDTL("C", "4");
			}
			if(data.prodC5) {
				$scope.inputVO.prodIdC5 = data.prodC5.FundNO; 
				$scope.inputVO.purchaseAmtC5 = data.prodC5.CurAmt; //信託金額
				//取得子基金商品資料
				$scope.getProdDTL("C", "5");
			}
			
			$scope.checkTrustAcct();
		}
		
		$scope.setTrustCurrType= function(FUS20,curcode){
			if(FUS20 && FUS20=='C'){
				return "C";
			}else{
				if(curcode=='TWD'){
					return"N";
				}else{
					return"Y";
				}
			}
		}
		
		
});