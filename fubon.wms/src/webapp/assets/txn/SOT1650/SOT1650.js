/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT1650Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q, validateService, sotService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT1650Controller";
		//繼承PRD100高齡檢核
		$controller('PRD100Controller', {$scope: $scope});
		
		debugger
        getParameter.XML(["SOT.CUST_TYPE", "SOT.TRADE_DATE_TYPE", "COMMON.YES_NO", "SOT.RESERVE_DATE_TIMESTAMP", "SOT.SPEC_CUSTOMER",'SOT.FUND_DECIMAL_POINT', "SOT.RESERVE_TRADE_DAYS","SOT.REDEEM_TYPE","SOT.DEDUCTION_STATUS_DYNA", "SOT.NEW_CONTRACT_DATE_DYNA"], function(totas) {
			if (totas) {
		    	//預約時間限制參數
		    	$scope.mappingSet['SOT.RESERVE_DATE_TIMESTAMP'] = totas.data[totas.key.indexOf('SOT.RESERVE_DATE_TIMESTAMP')];
				// mapping 來行人員  參數設定SOT.CUST_TYPE
				$scope.mappingSet['SOT.CUST_TYPE'] = totas.data[totas.key.indexOf('SOT.CUST_TYPE')];
				//交易日期
		        $scope.mappingSet['SOT.TRADE_DATE_TYPE'] = totas.data[totas.key.indexOf('SOT.TRADE_DATE_TYPE')];
		        //YES;NO
		        $scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
				//Nday 預約 營業日參數
				$scope.mappingSet['SOT.RESERVE_TRADE_DAYS'] = totas.data[totas.key.indexOf('SOT.RESERVE_TRADE_DAYS')]; 
				// 基金幣別小數位
				$scope.mappingSet['SOT.FUND_DECIMAL_POINT'] = totas.data[totas.key.indexOf('SOT.FUND_DECIMAL_POINT')]; 
	        	//客戶註記
				$scope.mappingSet['SOT.SPEC_CUSTOMER'] = totas.data[totas.key.indexOf('SOT.SPEC_CUSTOMER')];
				//贖回方式
				$scope.mappingSet['SOT.REDEEM_TYPE']  = totas.data[totas.key.indexOf('SOT.REDEEM_TYPE')];
				//扣款狀態
				$scope.mappingSet['SOT.DEDUCTION_STATUS_DYNA']  = totas.data[totas.key.indexOf('SOT.DEDUCTION_STATUS_DYNA')];
				//動態鎖利新契約起日_此日期之後子基金最多3筆
				$scope.mappingSet['SOT.NEW_CONTRACT_DATE_DYNA']  = totas.data[totas.key.indexOf('SOT.NEW_CONTRACT_DATE_DYNA')];
				
				angular.forEach($scope.mappingSet['SOT.RESERVE_TRADE_DAYS'], function(row) {
	        		if($scope.inputVO.trustTS == 'M' && row.DATA=='MNF'){
	        			$scope.reserveTradeDay = row.LABEL;
	        		}
	        		if($scope.inputVO.trustTS == 'S' && row.DATA=='NF') { 
	        			$scope.reserveTradeDay = row.LABEL;
	        		}
				});
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
        } else {
        	$scope.mappingSet['SOT.CHANGE_TRADE_SUB_TYPE'] = projInfoService.mappingSet['SOT.CHANGE_TRADE_SUB_TYPE'];
		}
        
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
//			$scope.inputVO.debitAcct = '';								//扣款帳號
//			$scope.inputVO.trustAcct = '';								//信託帳號
//			$scope.inputVO.creditAcct = '';								//收益入帳帳號
			$scope.inputVO.w8benEffDate = undefined;					//W8ben有效日期
			$scope.inputVO.custType = 'CUST';							//來行人員
			$scope.inputVO.agentID = '';								//代理人ID
			$scope.inputVO.agentName = '';
			$scope.inputVO.custProType = '';                            //專業投資人類型1：大專投 2：小專投
			$scope.inputVO.isFirstTrade = '';                            //是否首購 
			$scope.inputVO.debitAvbBalance = undefined;                 //扣款帳號餘額
			$scope.avlCurrency = undefined;                             //扣款帳號餘額幣別
			$scope.inputVO.hnwcYN = '';
			$scope.inputVO.hnwcServiceYN = '';
			
			$scope.mappingSet['SOT.CREDIT_ACCT_LIST'] = [];
			$scope.mappingSet['SOT.CREDIT_ACCT_LIST#DISPLAY'] = [];
			$scope.err = {}; //錯誤訊息組	
			
			$scope.prodClearM(true);
        };
        
      //母基金資料
		$scope.setProdDataM = function(data) {
			$scope.inputVO.prodId = data.prodDTL[0].PRD_ID;	//母基金只有商品代碼不一樣，其他帶一般基金商品變數名稱
			$scope.inputVO.prodName = data.prodDTL[0].FUND_CNAME;						//商品名稱
			$scope.inputVO.prodCurr = data.prodDTL[0].CURRENCY_STD_ID;					//計價幣別
			$scope.inputVO.prodRiskLv = data.prodDTL[0].RISKCATE_ID;
			$scope.inputVO.NotVertify = data.prodDTL[0].FUS40;                         //未核備欄位FUS40
			$scope.inputVO.prodFus20 = data.prodDTL[0].FUS20;                          //C:國內基金
			$scope.inputVO.prodFus07 = data.prodDTL[0].FUS07;							//2017-09-11 取基金-小數點位數
			$scope.inputVO.buyTwd = data.prodDTL[0].BUY_TWD;                           //N:不可申購台幣信託註記
//			$scope.inputVO.trustCurrType = $scope.setTrustCurrType($scope.inputVO.prodFus20, $scope.inputVO.trustCurr);
			$scope.inputVO.fundInfoSelling = data.fundInfoSelling; //建議售出Y/N 要出提示 ehl_01_SOT702_016
			$scope.showFitnessMessage(data.fitnessMessage);
		}
		
		//子基金資料1
		$scope.setProdDataC1 = function(data) {
			$scope.inputVO.prodIdC1 = data.prodDTL[0].PRD_ID;
			$scope.inputVO.prodNameC1 = data.prodDTL[0].FUND_CNAME;						//商品名稱
			$scope.inputVO.prodCurrC1 = data.prodDTL[0].CURRENCY_STD_ID;				//計價幣別
			$scope.inputVO.prodRiskLvC1 = data.prodDTL[0].RISKCATE_ID;
		}
		
		//子基金資料2
		$scope.setProdDataC2 = function(data) {
			$scope.inputVO.prodIdC2 = data.prodDTL[0].PRD_ID;
			$scope.inputVO.prodNameC2 = data.prodDTL[0].FUND_CNAME;						//商品名稱
			$scope.inputVO.prodCurrC2 = data.prodDTL[0].CURRENCY_STD_ID;				//計價幣別
			$scope.inputVO.prodRiskLvC2 = data.prodDTL[0].RISKCATE_ID;
		}
		
		//子基金資料3
		$scope.setProdDataC3 = function(data) {
			$scope.inputVO.prodIdC3 = data.prodDTL[0].PRD_ID;
			$scope.inputVO.prodNameC3 = data.prodDTL[0].FUND_CNAME;						//商品名稱
			$scope.inputVO.prodCurrC3 = data.prodDTL[0].CURRENCY_STD_ID;				//計價幣別
			$scope.inputVO.prodRiskLvC3 = data.prodDTL[0].RISKCATE_ID;
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
			$scope.inputVO.purchaseAmt = undefined;       //信託金額 
			$scope.inputVO.BaseAmtOfSPurchaseCurr ='';    //最低申購金額幣別
			$scope.inputVO.certificateID = ""; //憑證編號
			$scope.inputVO.tradeDateType = undefined;     	//交易日期類別	
			$scope.inputVO.fundInfoSelling = undefined; 	//商品管理有設建議售出
			$scope.inputVO.rdmUnit = undefined; 	//原單位數
			$scope.inputVO.redeemType = ""; 	//贖回方式
			$scope.inputVO.numUnits = undefined; 	//單位數
			$scope.inputVO.presentVal = undefined; 	//參考現值
			$scope.presentVal = undefined; 	//參考現值
			$scope.err = {}; //錯誤訊息組
			$("#tradeDateType1").attr("disabled", false);
			$scope.inputVO.creditAcct = "";
			$scope.inputVO.trustAcct = '';	//信託帳號
			$scope.inputVO.chgStatusYN = "N";
			$scope.inputVO.chgTransDateYN = "N";
			$scope.inputVO.chgAmountYN = "N";
			$scope.inputVO.chgAddProdYN = "N";
			$scope.inputVO.transDate = "";
			$scope.onChgStatusYN();
			$scope.onChgTransDateYN();
			$scope.onChgAmountYN();
			$scope.onChgAddProdYN();
			
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
								    debugger
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
									//贖回入帳帳號
									$scope.mappingSet['credit'] = [];
									angular.forEach(tota[0].body.creditAcct, function(row){
										$scope.mappingSet['credit'].push({LABEL: row.DATA, DATA: row.DATA});
									});
									
									//從FPS來的不清
									if (!$scope.fromFPS) {
										if(loadEdit!=true) {
											$scope.prodClearM(true);//清除產品
										}
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
		
		$scope.isEmptyValue = function(value) {
			if(value == undefined || value == null || value == "")
				return true;
			else
				return false;
		}
		
		//將購物車該筆相同seqNo編號 顯示在編輯區
		$scope.edit = function(row) {
			debugger
//			console.log('edit(row)'+JSON.stringify(row)); 
			$scope.inputVO.seqNo = row.SEQ_NO;
			$scope.inputVO.tradeSubType = row.TRADE_SUB_TYPE;     //信託型態
			$scope.inputVO.prodId = row.PROD_ID;     		//基金代號
			$scope.inputVO.prodName = row.PROD_NAME;     	//基金名稱
			$scope.inputVO.prodCurr = row.PROD_CURR;     	//計價幣別
			$scope.inputVO.prodRiskLv = row.PROD_RISK_LV;     //產品風險等級
			$scope.inputVO.notVertify = row.NOT_VERTIFY;      //未核備欄位
			$scope.inputVO.prodFus07 = row.FUS07;	//2017-09-11 取基金-小數點位數
			$scope.inputVO.trustCurr = row.TRUST_CURR;     //信託幣別
			$scope.inputVO.purchaseAmt = row.PURCHASE_AMT;     //申購金額 
			$scope.inputVO.certificateID = row.CERTIFICATE_ID;	//庫存憑證編號
			$scope.inputVO.tradeDateType = row.TRADE_DATE_TYPE;     //交易日期類別
			$scope.inputVO.tradeDate = row.TRADE_DATE;     //交易日期
			$scope.inputVO.narratorID = row.NARRATOR_ID;     //解說專員員編
			$scope.inputVO.narratorName = row.NARRATOR_NAME;     //解說專員姓名
			$scope.inputVO.trustAcct = row.TRUST_ACCT;     //信託帳號
			$scope.inputVO.creditAcct = row.CREDIT_ACCT;     //收益入帳帳號
			$scope.inputVO.transDate = row.TRANSFER_DATE;
			
		    $scope.inputVO.prodIdC1 = row.PROD_ID_C1;
			$scope.inputVO.prodNameC1 = row.PROD_NAME_C1;			//商品名稱
			$scope.inputVO.prodRiskLvC1 = row.PROD_RISK_LV_C1;
			$scope.inputVO.purchaseAmtC1 = row.PURCHASE_AMT_C1;     //申購金額 
			$scope.inputVO.eviNumTypeC1 = row.PROD_STATUS_C1;		//子基金扣款狀態
			
			$scope.inputVO.prodIdC2 = row.PROD_ID_C2;
			$scope.inputVO.prodNameC2 = row.PROD_NAME_C2;			//商品名稱
			$scope.inputVO.prodRiskLvC2 = row.PROD_RISK_LV_C2;
			$scope.inputVO.purchaseAmtC2 = row.PURCHASE_AMT_C2;     //申購金額 
			$scope.inputVO.eviNumTypeC2 = row.PROD_STATUS_C2;		//子基金扣款狀態
			
			$scope.inputVO.prodIdC3 = row.PROD_ID_C3;
			$scope.inputVO.prodNameC3 = row.PROD_NAME_C3;			//商品名稱
			$scope.inputVO.prodRiskLvC3 = row.PROD_RISK_LV_C3;
			$scope.inputVO.purchaseAmtC3 = row.PURCHASE_AMT_C3;     //申購金額 
			$scope.inputVO.eviNumTypeC3 = row.PROD_STATUS_C3;		//子基金扣款狀態
			
			$scope.inputVO.prodIdC4 = row.PROD_ID_C4;
			$scope.inputVO.prodNameC4 = row.PROD_NAME_C4;			//商品名稱
			$scope.inputVO.prodRiskLvC4 = row.PROD_RISK_LV_C4;
			$scope.inputVO.purchaseAmtC4 = row.PURCHASE_AMT_C4;     //申購金額 
			$scope.inputVO.eviNumTypeC4 = row.PROD_STATUS_C4;		//子基金扣款狀態
			
			$scope.inputVO.prodIdC5 = row.PROD_ID_C5;
			$scope.inputVO.prodNameC5 = row.PROD_NAME_C5;			//商品名稱
			$scope.inputVO.prodRiskLvC5 = row.PROD_RISK_LV_C5;
			$scope.inputVO.purchaseAmtC5 = row.PURCHASE_AMT_C5;     //申購金額 
			$scope.inputVO.eviNumTypeC5 = row.PROD_STATUS_C5;		//子基金扣款狀態
			
			$scope.inputVO.chgStatusYN = row.CHG_STATUS_YN;
			$scope.inputVO.chgAmountYN = row.CHG_AMOUNT_YN;
			$scope.inputVO.chgTransDateYN = row.CHG_TRANSDATE_YN;
			$scope.inputVO.chgAddProdYN = row.CHG_ADDPROD_YN;
			
			$scope.inputVO.fProdStatusC1 = row.F_PROD_STATUS_C1;
			$scope.inputVO.fProdStatusC2 = row.F_PROD_STATUS_C2;
			$scope.inputVO.fProdStatusC3 = row.F_PROD_STATUS_C3;
			$scope.inputVO.fProdStatusC4 = row.F_PROD_STATUS_C4;
			$scope.inputVO.fProdStatusC5 = row.F_PROD_STATUS_C5;
			
			$scope.inputVO.fPurchaseAmtC1 = row.F_PURCHASE_AMT_C1;
			$scope.inputVO.fPurchaseAmtC2 = row.F_PURCHASE_AMT_C2;
			$scope.inputVO.fPurchaseAmtC3 = row.F_PURCHASE_AMT_C3;
			$scope.inputVO.fPurchaseAmtC4 = row.F_PURCHASE_AMT_C4;
			$scope.inputVO.fPurchaseAmtC5 = row.F_PURCHASE_AMT_C5;
			$scope.findNfMinBuyAmt('C', "1");  //最低申購金額
			$scope.findNfMinBuyAmt('C', "2");  //最低申購金額
			$scope.findNfMinBuyAmt('C', "3");  //最低申購金額
			$scope.findNfMinBuyAmt('C', "4");  //最低申購金額
			$scope.findNfMinBuyAmt('C', "5");  //最低申購金額
			
			$scope.inputVO.fTransDate1 = row.F_TRANSFER_DATE_1;
			$scope.inputVO.fTransDate2 = row.F_TRANSFER_DATE_2;
			$scope.inputVO.fTransDate3 = row.F_TRANSFER_DATE_3;
			$scope.inputVO.fTransDate4 = row.F_TRANSFER_DATE_4;
			$scope.inputVO.fTransDate5 = row.F_TRANSFER_DATE_5;
			$scope.inputVO.fTransDate6 = row.F_TRANSFER_DATE_6;
			
			$scope.inputVO.fProdIdC1 = row.F_PROD_ID_C1;
			$scope.inputVO.fProdNameC1 = row.F_PROD_NAME_C1;
			$scope.inputVO.fProdCurrC1 = row.PROD_CURR;
			$scope.inputVO.fProdRiskLvC1 = row.F_PROD_RISK_LV_C1;
			$scope.inputVO.fAddprodAmtC1 = row.F_ADDPROD_AMT_C1;
			$scope.inputVO.fProdMinBuyAmtC1 = row.F_PROD_MIN_BUY_AMT_C1;
			$scope.inputVO.fProdMinGrdAmtC1 = row.F_PROD_MIN_GRD_AMT_C1;
			
			$scope.inputVO.fProdIdC2 = row.F_PROD_ID_C2;
			$scope.inputVO.fProdNameC2 = row.F_PROD_NAME_C2;
			$scope.inputVO.fProdCurrC2 = row.PROD_CURR;
			$scope.inputVO.fProdRiskLvC2 = row.F_PROD_RISK_LV_C2;
			$scope.inputVO.fAddprodAmtC2 = row.F_ADDPROD_AMT_C2;
			$scope.inputVO.fProdMinBuyAmtC2 = row.F_PROD_MIN_BUY_AMT_C2;
			$scope.inputVO.fProdMinGrdAmtC2 = row.F_PROD_MIN_GRD_AMT_C2;
			
			$scope.inputVO.fProdIdC3 = row.F_PROD_ID_C3;
			$scope.inputVO.fProdNameC3 = row.F_PROD_NAME_C3;
			$scope.inputVO.fProdCurrC3 = row.PROD_CURR;
			$scope.inputVO.fProdRiskLvC3 = row.F_PROD_RISK_LV_C3;
			$scope.inputVO.fAddprodAmtC3 = row.F_ADDPROD_AMT_C3;
			$scope.inputVO.fProdMinBuyAmtC3 = row.F_PROD_MIN_BUY_AMT_C3;
			$scope.inputVO.fProdMinGrdAmtC3 = row.F_PROD_MIN_GRD_AMT_C3;
			
			$scope.inputVO.fProdIdC4 = row.F_PROD_ID_C4;
			$scope.inputVO.fProdNameC4 = row.F_PROD_NAME_C4;
			$scope.inputVO.fProdCurrC4 = row.PROD_CURR;
			$scope.inputVO.fProdRiskLvC4 = row.F_PROD_RISK_LV_C4;
			$scope.inputVO.fAddprodAmtC4 = row.F_ADDPROD_AMT_C4;
			$scope.inputVO.fProdMinBuyAmtC4 = row.F_PROD_MIN_BUY_AMT_C4;
			$scope.inputVO.fProdMinGrdAmtC4 = row.F_PROD_MIN_GRD_AMT_C4;
			
			$scope.inputVO.fProdIdC5 = row.F_PROD_ID_C5;
			$scope.inputVO.fProdNameC5 = row.F_PROD_NAME_C5;
			$scope.inputVO.fProdCurrC5 = row.PROD_CURR;
			$scope.inputVO.fProdRiskLvC5 = row.F_PROD_RISK_LV_C5;
			$scope.inputVO.fAddprodAmtC5 = row.F_ADDPROD_AMT_C5;
			$scope.inputVO.fProdMinBuyAmtC5 = row.F_PROD_MIN_BUY_AMT_C5;
			$scope.inputVO.fProdMinGrdAmtC5 = row.F_PROD_MIN_GRD_AMT_C5;
		};
		
		//選擇信託類型
		$scope.tradeType = function(){
			/*1.選擇信託型態定期定額，僅秀出與信託幣別相同的最低申購金額限制。
			  2. 選擇信託型態選擇定期不定額，需秀出與信託幣別相同的最低申購金額限制。
			 */
			// 參考 SOT.CHANGE_TRADE_SUB_TYPE
			var type = $scope.inputVO.tradeSubType;
			//			console.log("tradeSubType:"+type);
			if(type == '1') {
				//			console.log('1：單筆');
			}
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
//			$scope.setTrustCurr(); //設定信託幣別  CALL  $scope.checkTrustAcct()
//			$scope.findNfMinBuyAmt("M", "");
			
			$scope.inputVO.purchaseAmt = undefined; //清空申購金額
			//2017-7-5 設定幣別小數位數
			$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.avlCurrency});
			$scope.num = $scope.mod[0].LABEL;
			
			//控管國內貨幣型基金交易時間，超過10:30 轉為預約交易狀態	
			//境外私募基金不用判斷，都是預約交易
			if($scope.inputVO.prodId != null && $scope.inputVO.prodId != ''){ 
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
		
		//取得動態鎖利商品資料(庫存)
		$scope.getProdDTL = function(type, idx) {
			var deferred = $q.defer();
			
			var obj = eval("$scope.inputVO.prodId" + (type == "M" ? "" : type) + idx);
			//將要搜尋的母基金或子基金放到PROD_ID
			var prodId = $filter('uppercase')(obj);
		    
			if(prodId) {
				$scope.sendRecv("SOT1650", "getProdDTL", "com.systex.jbranch.app.server.fps.sot1650.SOT1650InputVO", {"prodId": prodId},
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.prodDTL != null) {
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
										$scope.findNfMinBuyAmt(type, idx);  //最低申購金額
									}
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
			$scope.sendRecv("SOT1650", "query", "com.systex.jbranch.app.server.fps.sot1650.SOT1650InputVO", {"tradeSEQ": $scope.inputVO.tradeSEQ},
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
		
	    $scope.initOverTradeTime = function() {
	    	$scope.overTradeTime = false;
	    };
	    
	    //儲存前欄位檢核
	    $scope.validBeforeSave = function() {
	    	if($scope.inputVO.prodId == undefined || $scope.inputVO.prodId == null || $scope.inputVO.prodId == "") {
	    		$scope.showErrorMsg("請選擇庫存");
	    		return false;
	    	}
	    	//檢查變更狀態
		    if($scope.inputVO.chgStatusYN == "Y") {
		    	if($scope.isEmpty($scope.inputVO.fProdStatusC1) && $scope.isEmpty($scope.inputVO.fProdStatusC2) && $scope.isEmpty($scope.inputVO.fProdStatusC3) &&
		    			$scope.isEmpty($scope.inputVO.fProdStatusC4) && $scope.isEmpty($scope.inputVO.fProdStatusC5)) {
		    		$scope.inputVO.chgStatusYN = "N";
		    	}
		    }
		    //檢查變更扣款日期
			if($scope.inputVO.chgTransDateYN == "Y") {
				if($scope.inputVO.fTransDate1 == "N" && $scope.inputVO.fTransDate2 == "N" && $scope.inputVO.fTransDate3 == "N" &&
						$scope.inputVO.fTransDate4 == "N" && $scope.inputVO.fTransDate5 == "N") {
					$scope.inputVO.chgTransDateYN = "N";
				}
			}
			//檢查變更轉換金額
			if($scope.inputVO.chgAmountYN == "Y") {
				if($scope.isEmpty($scope.inputVO.fPurchaseAmtC1) && $scope.isEmpty($scope.inputVO.fPurchaseAmtC2) && $scope.isEmpty($scope.inputVO.fPurchaseAmtC3) &&
						$scope.isEmpty($scope.inputVO.fPurchaseAmtC4) && $scope.isEmpty($scope.inputVO.fPurchaseAmtC5)) {
					$scope.inputVO.chgAmountYN = "N";
				}
			}
			//檢查增加子基金
			if($scope.inputVO.chgAddProdYN == "Y") {
				if($scope.isEmpty($scope.inputVO.fProdIdC1) && $scope.isEmpty($scope.inputVO.fProdIdC2) && $scope.isEmpty($scope.inputVO.fProdIdC3) &&
						$scope.isEmpty($scope.inputVO.fProdIdC4) && $scope.isEmpty($scope.inputVO.fProdIdC5)) {
					$scope.inputVO.chgAddProdYN = "N";
				}
				if((!$scope.isEmpty($scope.inputVO.fProdIdC1) && $scope.isEmpty($scope.inputVO.fAddprodAmtC1)) ||
						(!$scope.isEmpty($scope.inputVO.fProdIdC2) && $scope.isEmpty($scope.inputVO.fAddprodAmtC2)) ||
						(!$scope.isEmpty($scope.inputVO.fProdIdC3) && $scope.isEmpty($scope.inputVO.fAddprodAmtC3)) ||
						(!$scope.isEmpty($scope.inputVO.fProdIdC4) && $scope.isEmpty($scope.inputVO.fAddprodAmtC4)) ||
						(!$scope.isEmpty($scope.inputVO.fProdIdC5) && $scope.isEmpty($scope.inputVO.fAddprodAmtC5))) {
					$scope.showErrorMsg("請輸入增加子基金轉換金額");
		    		return false;
				}
			}
		    
			if($scope.inputVO.chgStatusYN == "N" && $scope.inputVO.chgTransDateYN == "N" && $scope.inputVO.chgAmountYN == "N" && $scope.inputVO.chgAddProdYN == "N") {
				$scope.showErrorMsg("請選擇變更項目及其變更內容");
	    		return false;
			}
			
			return true;
	    }
	    
	    //高齡評估量表檢核
	    $scope.validateSeniorCust = function() {
	    	if(!$scope.validBeforeSave()) { //儲存前欄位檢核
	    		//欄位檢核有誤
	    		return;
	    	}
	    	
			//是否有變更扣款日期且次數增加
			var chargeDateChanged = false;
			if($scope.inputVO.chgTransDateYN == "Y") { //有變更扣款日期
				var fcount = 0;
				for(var idx=1; idx<=6; idx++) {
					var fdate = eval("$scope.inputVO.fTransDate" + idx)//變更後每月扣款日期
					if(fdate && fdate == "Y") fcount++;
				}
				if(fcount > $scope.inputVO.transDateLength) chargeDateChanged = true;
			}
			
			//高資產客戶且非特定客戶檢查是否需要做越級適配檢核，C4也不須檢核
			$scope.inputVO.needHnwcRiskValueYN = "N";
			
			if(chargeDateChanged || //有變更扣款日期且有增加次數
					$scope.inputVO.chgAddProdYN == "Y" || //有增加子基金
					($scope.inputVO.chgStatusYN == "Y" && 
						 (($scope.inputVO.prodIdC1 && $scope.inputVO.fProdStatusC1 == "N") ||
						  ($scope.inputVO.prodIdC2 && $scope.inputVO.fProdStatusC2 == "N") ||
						  ($scope.inputVO.prodIdC3 && $scope.inputVO.fProdStatusC3 == "N") ||
						  ($scope.inputVO.prodIdC4 && $scope.inputVO.fProdStatusC4 == "N") ||
						  ($scope.inputVO.prodIdC5 && $scope.inputVO.fProdStatusC5 == "N"))) || //有恢復扣款
					($scope.inputVO.chgAmountYN == "Y" && 
						 (($scope.inputVO.prodIdC1 && $scope.inputVO.fPurchaseAmtC1 && $scope.inputVO.fPurchaseAmtC1 > $scope.inputVO.purchaseAmtC1) ||
						  ($scope.inputVO.prodIdC2 && $scope.inputVO.fPurchaseAmtC2 && $scope.inputVO.fPurchaseAmtC2 > $scope.inputVO.purchaseAmtC2) ||
						  ($scope.inputVO.prodIdC3 && $scope.inputVO.fPurchaseAmtC3 && $scope.inputVO.fPurchaseAmtC3 > $scope.inputVO.purchaseAmtC3) ||
						  ($scope.inputVO.prodIdC4 && $scope.inputVO.fPurchaseAmtC4 && $scope.inputVO.fPurchaseAmtC4 > $scope.inputVO.purchaseAmtC4) ||
						  ($scope.inputVO.prodIdC5 && $scope.inputVO.fPurchaseAmtC5 && $scope.inputVO.fPurchaseAmtC5 > $scope.inputVO.purchaseAmtC5)))) { //有變更金額且增加扣款金額
				//PRD100.validSeniorCustEval高齡檢核
				$scope.inputVO.prodType='8';  //8：基金動態鎖利
	        	$scope.inputVO.tradeType='4'; //4：基金動態鎖利事件變更
				$scope.inputVO.seniorAuthType = 'S';	//高齡評估表授權種類(S:下單、A：適配)
				$scope.inputVO.trustTS = 'S';  			//S:特金交易 M:金錢信託
				$scope.inputVO.type = "1";
				$scope.inputVO.cust_id = $scope.inputVO.custID;
				$scope.inputVO.needHnwcRiskValueYN = "Y"; //有這些邊更需檢核是否需要越級適配 //有變更扣款日期且有增加次數//有增加子基金//有恢復扣款//有變更金額且增加扣款金額
				$scope.validSeniorCustEval(); 
			} else {
				$scope.save('2'); //下一步
			}
		}
		
		//PRD100.validSeniorCustEval高齡檢核不通過清空變更資料
		$scope.clearCustInfo = function() {
			$scope.prodClearM(true);
		};
		
		//PRD100.validSeniorCustEval高齡檢核通過後做下一步
		$scope.reallyInquire = function() {
			$scope.save('2'); //下一步
		};
		
		//1:暫存 / 2:下一步按鈕
		$scope.save = function(type) {
			if($scope.inputVO.tradeDateType == '' || $scope.inputVO.tradeDateType == undefined){
				$scope.showErrorMsg("ehl_01_common_022");
	    		return;
			}
			
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
			$scope.sendRecv("SOT1650", "save", "com.systex.jbranch.app.server.fps.sot1650.SOT1650InputVO", $scope.inputVO,
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
			$scope.sendRecv("SOT1650", "save", "com.systex.jbranch.app.server.fps.sot1650.SOT1650InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if (tota[0].body.errorMsg != null) {
							$scope.showErrorMsg(tota[0].body.errorMsg);								
							return;
						} else {
							$scope.sendRecv("SOT1650", "next", "com.systex.jbranch.app.server.fps.sot1650.SOT1650InputVO", {tradeSEQ : $scope.inputVO.tradeSEQ, 'prodType':$scope.inputVO.prodType},
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
											$rootScope.menuItemInfo.url = "assets/txn/SOT1651/SOT1651.html";							
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
					$scope.txnName = "SOT1650";
					$scope.custID=custId;
					$scope.trustTS=trustTS;
					$scope.debitAcct=debitAcct;
					$scope.dynamicYN = "Y"; //動態鎖利註記
				}]
			});
			dialog.closePromise.then(function(data){
				console.log(data);
				if(data.value && data.value != "cancel") {
					debugger
					//先清除原庫存資料
					$scope.prodClearM(true);
					//將庫存資料放入欄位中
					$scope.putInStockData(data.value);
				}
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
			$scope.presentVal				  =   Number(data.prodM.CurBal).toFixed(4);
			$scope.inputVO.presentVal		  =   Number(data.prodM.CurBal).toFixed(4); //參考現值
			$scope.inputVO.certificateID  	  =   data.prodM.EviNum; //憑證編號
			$scope.inputVO.prodCurr        	  =   data.prodM.CurFund;//計價幣別
			$scope.inputVO.trustCurr          =   data.prodM.CurCode;//信託幣別
			$scope.inputVO.purchaseAmt		  =   data.prodM.CurAmt; //信託金額
			$scope.inputVO.rdmUnit            =   Number(data.prodM.CurUntNum).toFixed(4); //原單位數
			$scope.inputVO.numUnits           =   Number(data.prodM.CurUntNum).toFixed(4); //單位數
			$scope.inputVO.trustAcct          =   data.prodM.AcctId02.trim();  //信託帳號
			$scope.inputVO.creditAcct         =   data.prodM.PayAcctId; //收益入帳帳號
//			$scope.inputVO.creditAcctDisplay  =   data.prodM.PayAcctId; //收益入帳帳號
			$scope.inputVO.branchNbr 		  =   $scope.inputVO.trustAcct ? $scope.inputVO.trustAcct.substring(2,5) : "";
			$scope.inputVO.unitValue 		  =   $scope.inputVO.presentVal/$scope.inputVO.rdmUnit //單位(計算贖回方式之值) presentVal= numUnits * unitValue
			$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.inputVO.trustCurr});
			//是否為動態鎖利新契約 是:子基金最多3筆 否:舊契約子基金最多5筆
			$scope.inputVO.isNewContract = true; 
			try {
				//投資起日
				var strdate = data.prodM.Strdate; 
				var invStartDate  = (parseInt(strdate.substr(0, 3)) + 1911) + strdate.substr(3, 2) + strdate.substr(5, 2);
				//動態鎖利新契約起日_此日期之後子基金最多3筆
				var invDate = $filter('filter')($scope.mappingSet['SOT.NEW_CONTRACT_DATE_DYNA'], {DATA: "1"}); 
				if(invStartDate < invDate[0].LABEL) {
					//投資起日 < 動態鎖利新契約起日 ==> 為舊契約
					$scope.inputVO.isNewContract = false; 
				}
			} catch(e) {}
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
			$scope.inputVO.transDateLength = dateArray.length; //轉換日期有幾個，若有增加須做高齡檢核
			
			//取得母基金商品資料
			$scope.getProdDTL("M", "");
			//子基金
			if(data.prodC1) {
				$scope.inputVO.prodIdC1 = data.prodC1.FundNO; 
				$scope.inputVO.purchaseAmtC1 = data.prodC1.TRANSFERAmt; //轉換金額
				$scope.inputVO.eviNumTypeC1 = (data.prodC1.EviNumType && data.prodC1.EviNumType == "Y" ? "Y" : "N"); //扣款狀態
				//取得子基金商品資料
				$scope.getProdDTL("C", "1");
			}
			if(data.prodC2) {
				$scope.inputVO.prodIdC2 = data.prodC2.FundNO;
				$scope.inputVO.purchaseAmtC2 = data.prodC2.TRANSFERAmt; //轉換金額
				$scope.inputVO.eviNumTypeC2 = (data.prodC2.EviNumType && data.prodC2.EviNumType == "Y" ? "Y" : "N"); //扣款狀態
				//取得子基金商品資料
				$scope.getProdDTL("C", "2");
			}
			if(data.prodC3) {
				$scope.inputVO.prodIdC3 = data.prodC3.FundNO; 
				$scope.inputVO.purchaseAmtC3 = data.prodC3.TRANSFERAmt; //轉換金額
				$scope.inputVO.eviNumTypeC3 = (data.prodC3.EviNumType && data.prodC3.EviNumType == "Y" ? "Y" : "N"); //扣款狀態
				//取得子基金商品資料
				$scope.getProdDTL("C", "3");
			}
			if(data.prodC4) {
				$scope.inputVO.prodIdC4 = data.prodC4.FundNO; 
				$scope.inputVO.purchaseAmtC4 = data.prodC4.TRANSFERAmt; //轉換金額
				$scope.inputVO.eviNumTypeC4 = (data.prodC4.EviNumType && data.prodC4.EviNumType == "Y" ? "Y" : "N"); //扣款狀態
				//取得子基金商品資料
				$scope.getProdDTL("C", "4");
			}
			if(data.prodC5) {
				$scope.inputVO.prodIdC5 = data.prodC5.FundNO; 
				$scope.inputVO.purchaseAmtC5 = data.prodC5.TRANSFERAmt; //轉換金額
				$scope.inputVO.eviNumTypeC5 = (data.prodC5.EviNumType && data.prodC5.EviNumType == "Y" ? "Y" : "N"); //扣款狀態
				//取得子基金商品資料
				$scope.getProdDTL("C", "5");
			}
		}
		
//		$scope.setTrustCurrType= function(FUS20,curcode){
//			if(FUS20 && FUS20=='C'){
//				return "C";
//			}else{
//				if(curcode=='TWD'){
//					return"N";
//				}else{
//					return"Y";
//				}
//			}
//		}
		
		//取得小數點位數
		$scope.getCurrency = function(){
			if($scope.mod != undefined)
				$scope.num = $scope.mod[0].LABEL;
			//信託金額
			$scope.inputVO.purchaseAmt = !isNaN($scope.inputVO.purchaseAmt) ? Number($scope.inputVO.purchaseAmt).toFixed($scope.num) : $scope.inputVO.purchaseAmt;
			$scope.inputVO.purchaseAmtC1 = !isNaN($scope.inputVO.purchaseAmtC1) ? Number($scope.inputVO.purchaseAmtC1).toFixed($scope.num) : $scope.inputVO.purchaseAmtC1;
			$scope.inputVO.purchaseAmtC2 = !isNaN($scope.inputVO.purchaseAmtC2) ? Number($scope.inputVO.purchaseAmtC2).toFixed($scope.num) : $scope.inputVO.purchaseAmtC2;
			$scope.inputVO.purchaseAmtC3 = !isNaN($scope.inputVO.purchaseAmtC3) ? Number($scope.inputVO.purchaseAmtC3).toFixed($scope.num) : $scope.inputVO.purchaseAmtC3;
			$scope.inputVO.purchaseAmtC4 = !isNaN($scope.inputVO.purchaseAmtC4) ? Number($scope.inputVO.purchaseAmtC4).toFixed($scope.num) : $scope.inputVO.purchaseAmtC4;
			$scope.inputVO.purchaseAmtC5 = !isNaN($scope.inputVO.purchaseAmtC5) ? Number($scope.inputVO.purchaseAmtC5).toFixed($scope.num) : $scope.inputVO.purchaseAmtC5;
			//參考現值
			$scope.inputVO.presentVal= !isNaN($scope.inputVO.presentVal) ? Number($scope.inputVO.presentVal).toFixed($scope.num) : $scope.inputVO.presentVal;
		};
		
		//變更子基金扣款狀態
		$scope.onChgStatusYN = function() {
			if($scope.inputVO.chgStatusYN != "Y") {
				$scope.inputVO.fProdStatusC1 = "";
				$scope.inputVO.fProdStatusC2 = "";
				$scope.inputVO.fProdStatusC3 = "";
				$scope.inputVO.fProdStatusC4 = "";
				$scope.inputVO.fProdStatusC5 = "";
			}
		}
		
		//變更每月扣款日期
		$scope.onChgTransDateYN = function() {
			if($scope.inputVO.chgTransDateYN != "Y") {
				$scope.inputVO.fTransDate1 = "N";
				$scope.inputVO.fTransDate2 = "N";
				$scope.inputVO.fTransDate3 = "N";
				$scope.inputVO.fTransDate4 = "N";
				$scope.inputVO.fTransDate5 = "N";
				$scope.inputVO.fTransDate6 = "N";
			}
		}
		
		//變更子基金轉換金額
		$scope.onChgAmountYN = function() {
			if($scope.inputVO.chgAmountYN != "Y") {
				$scope.inputVO.fPurchaseAmtC1 = undefined;
				$scope.inputVO.fPurchaseAmtC2 = undefined;
				$scope.inputVO.fPurchaseAmtC3 = undefined;
				$scope.inputVO.fPurchaseAmtC4 = undefined;
				$scope.inputVO.fPurchaseAmtC5 = undefined;
			}
		}
		
		//變更新增子基金
		$scope.onChgAddProdYN = function() {
			if($scope.inputVO.chgAddProdYN != "Y") {
				$scope.inputVO.fProdIdC1 = "";
				$scope.inputVO.fProdNameC1 = "";
				$scope.inputVO.fProdCurrC1 = "";
				$scope.inputVO.fProdRiskLvC1 = "";
				$scope.inputVO.fAddprodAmtC1 = undefined;
				$scope.inputVO.fProdMinBuyAmtC1 = undefined;
				$scope.inputVO.fProdMinGrdAmtC1 = undefined;
				
				$scope.inputVO.fProdIdC2 = "";
				$scope.inputVO.fProdNameC2 = "";
				$scope.inputVO.fProdCurrC2 = "";
				$scope.inputVO.fProdRiskLvC2 = "";
				$scope.inputVO.fAddprodAmtC2 = undefined;
				$scope.inputVO.fProdMinBuyAmtC2 = undefined;
				$scope.inputVO.fProdMinGrdAmtC2 = undefined;
				
				$scope.inputVO.fProdIdC3 = "";
				$scope.inputVO.fProdNameC3 = "";
				$scope.inputVO.fProdCurrC3 = "";
				$scope.inputVO.fProdRiskLvC3 = "";
				$scope.inputVO.fAddprodAmtC3 = undefined;
				$scope.inputVO.fProdMinBuyAmtC3 = undefined;
				$scope.inputVO.fProdMinGrdAmtC3 = undefined;
				
				$scope.inputVO.fProdIdC4 = "";
				$scope.inputVO.fProdNameC4 = "";
				$scope.inputVO.fProdCurrC4 = "";
				$scope.inputVO.fProdRiskLvC4 = "";
				$scope.inputVO.fAddprodAmtC4 = undefined;
				$scope.inputVO.fProdMinBuyAmtC4 = undefined;
				$scope.inputVO.fProdMinGrdAmtC4 = undefined;
				
				$scope.inputVO.fProdIdC5 = "";
				$scope.inputVO.fProdNameC5 = "";
				$scope.inputVO.fProdCurrC5 = "";
				$scope.inputVO.fProdRiskLvC5 = "";
				$scope.inputVO.fAddprodAmtC5 = undefined;
				$scope.inputVO.fProdMinBuyAmtC5 = undefined;
				$scope.inputVO.fProdMinGrdAmtC5 = undefined;
			}
		}
		
		//取得庫存中子基金最低申購金額&累進申購面額
		$scope.findNfMinBuyAmt = function(type, idx){
			var MIN_BUY_AMT = type == "M" ? "SOT.NF_MIN_BUY_AMT_DM" : "SOT.NF_MIN_BUY_AMT_DC";
			var PARAM_CODE = $scope.inputVO.trustCurr; //信託幣別
			
			//最低申購金額
	        var vo = {'param_type': MIN_BUY_AMT, 'desc': false};
	        
	        $scope.requestComboBox(vo, function(totas) {
	        	if (totas[totas.length - 1].body.result === 'success') {
        			angular.forEach(totas[0].body.result, function(row){
        	       		if(row.DATA == PARAM_CODE){
        	       			if(idx == "1") $scope.inputVO.prodMinBuyAmtC1 = row.LABEL;
    						if(idx == "2") $scope.inputVO.prodMinBuyAmtC2 = row.LABEL;
    						if(idx == "3") $scope.inputVO.prodMinBuyAmtC3 = row.LABEL;
    						if(idx == "4") $scope.inputVO.prodMinBuyAmtC4 = row.LABEL;
    						if(idx == "5") $scope.inputVO.prodMinBuyAmtC5 = row.LABEL;
        	       		}
        	       	});
	        	}
	        });
	        	
	        //累進申購面額
	        var MIN_GRD_AMT = type == "M" ? "SOT.NF_MIN_GRD_AMT_DM" : "SOT.NF_MIN_GRD_AMT_DC";			
			var vog = {'param_type': MIN_GRD_AMT, 'desc': false};
	        	$scope.requestComboBox(vog, function(totas) {
	        		if (totas[totas.length - 1].body.result === 'success') {
        				angular.forEach(totas[0].body.result, function(row){
        	        		if(row.DATA == PARAM_CODE){
        	        			if(idx == "1") $scope.inputVO.prodMinGrdAmtC1 = row.LABEL;
    							if(idx == "2") $scope.inputVO.prodMinGrdAmtC2 = row.LABEL;
    							if(idx == "3") $scope.inputVO.prodMinGrdAmtC3 = row.LABEL;
    							if(idx == "4") $scope.inputVO.prodMinGrdAmtC4 = row.LABEL;
    							if(idx == "5") $scope.inputVO.prodMinGrdAmtC5 = row.LABEL;
        	        		}
        	        	});
	        		}
	        });
		}
		
		//取得新增子基金最低申購金額&累進申購面額
		$scope.findAddProdNfMinBuyAmt = function(type, idx){
			var MIN_BUY_AMT = type == "M" ? "SOT.NF_MIN_BUY_AMT_DM" : "SOT.NF_MIN_BUY_AMT_DC";
			var PARAM_CODE = $scope.inputVO.trustCurr; //信託幣別
			
			//最低申購金額
	        var vo = {'param_type': MIN_BUY_AMT, 'desc': false};
	        
	        $scope.requestComboBox(vo, function(totas) {
	        	if (totas[totas.length - 1].body.result === 'success') {
        			angular.forEach(totas[0].body.result, function(row){
        	       		if(row.DATA == PARAM_CODE){
        	       			if(idx == "1") $scope.inputVO.fProdMinBuyAmtC1 = row.LABEL;
    						if(idx == "2") $scope.inputVO.fProdMinBuyAmtC2 = row.LABEL;
    						if(idx == "3") $scope.inputVO.fProdMinBuyAmtC3 = row.LABEL;
    						if(idx == "4") $scope.inputVO.fProdMinBuyAmtC4 = row.LABEL;
    						if(idx == "5") $scope.inputVO.fProdMinBuyAmtC5 = row.LABEL;
        	       		}
        	       	});
	        	}
	        });
	        	
	        //累進申購面額
	        var MIN_GRD_AMT = type == "M" ? "SOT.NF_MIN_GRD_AMT_DM" : "SOT.NF_MIN_GRD_AMT_DC";			
			var vog = {'param_type': MIN_GRD_AMT, 'desc': false};
	        	$scope.requestComboBox(vog, function(totas) {
	        		if (totas[totas.length - 1].body.result === 'success') {
        				angular.forEach(totas[0].body.result, function(row){
        	        		if(row.DATA == PARAM_CODE){
        	        			if(idx == "1") $scope.inputVO.fProdMinGrdAmtC1 = row.LABEL;
    							if(idx == "2") $scope.inputVO.fProdMinGrdAmtC2 = row.LABEL;
    							if(idx == "3") $scope.inputVO.fProdMinGrdAmtC3 = row.LABEL;
    							if(idx == "4") $scope.inputVO.fProdMinGrdAmtC4 = row.LABEL;
    							if(idx == "5") $scope.inputVO.fProdMinGrdAmtC5 = row.LABEL;
        	        		}
        	        	});
	        		}
	        });
		}
		
		//檢查子基金轉換金額的最低申購金額&累進申購面額
		$scope.checkPurchaseAmtLimit = function(type, idx) {
			var prodMinBuyAmt = undefined;
			var prodMinGrdAmt = undefined;
			var purchaseAmt = undefined;
			
			if(idx == "1") {
				prodMinBuyAmt = Number($scope.inputVO.prodMinBuyAmtC1);
				prodMinGrdAmt = Number($scope.inputVO.prodMinGrdAmtC1);
				purchaseAmt = Number($scope.inputVO.fPurchaseAmtC1);
			}
			if(idx == "2") {
				prodMinBuyAmt = Number($scope.inputVO.prodMinBuyAmtC2);
				prodMinGrdAmt = Number($scope.inputVO.prodMinGrdAmtC2);
				purchaseAmt = Number($scope.inputVO.fPurchaseAmtC2);
			}
			if(idx == "3") {
				prodMinBuyAmt = Number($scope.inputVO.prodMinBuyAmtC3);
				prodMinGrdAmt = Number($scope.inputVO.prodMinGrdAmtC3);
				purchaseAmt = Number($scope.inputVO.fPurchaseAmtC3);
			}
			if(idx == "4") {
				prodMinBuyAmt = Number($scope.inputVO.prodMinBuyAmtC4);
				prodMinGrdAmt = Number($scope.inputVO.prodMinGrdAmtC4);
				purchaseAmt = Number($scope.inputVO.fPurchaseAmtC4);
			}
			if(idx == "5") {
				prodMinBuyAmt = Number($scope.inputVO.prodMinBuyAmtC5);
				prodMinGrdAmt = Number($scope.inputVO.prodMinGrdAmtC5);
				purchaseAmt = Number($scope.inputVO.fPurchaseAmtC5);
			}
			
			if (purchaseAmt && prodMinBuyAmt && prodMinGrdAmt && (purchaseAmt < prodMinBuyAmt || purchaseAmt % prodMinGrdAmt != 0)) {
				$scope.showErrorMsg("ehl_01_sot310_007");  //申購面額須高於最低申購面額，且須為累進申購面額之倍數。
				if(idx == "1") $scope.inputVO.fPurchaseAmtC1 = undefined;
				if(idx == "2") $scope.inputVO.fPurchaseAmtC2 = undefined;
				if(idx == "3") $scope.inputVO.fPurchaseAmtC3 = undefined;
				if(idx == "4") $scope.inputVO.fPurchaseAmtC4 = undefined;
				if(idx == "5") $scope.inputVO.fPurchaseAmtC5 = undefined;
			}
		}
		
		//檢查新增子基金最低申購金額&累進申購面額
		$scope.checkAddProdAmtLimit = function(type, idx) {
			var prodMinBuyAmt = undefined;
			var prodMinGrdAmt = undefined;
			var purchaseAmt = undefined;
			
			if(idx == "1") {
				if($scope.inputVO.fProdIdC1 == "") {
					$scope.showErrorMsg("請先選擇子基金1");
					$scope.addProdClearD(type, idx, true);
					return;
				}
				prodMinBuyAmt = Number($scope.inputVO.fProdMinBuyAmtC1);
				prodMinGrdAmt = Number($scope.inputVO.fProdMinGrdAmtC1);
				purchaseAmt = Number($scope.inputVO.fAddprodAmtC1);
			}
			if(idx == "2") {
				if($scope.inputVO.fProdIdC2 == "") {
					$scope.showErrorMsg("請先選擇子基金2");
					$scope.addProdClearD(type, idx, true);
					return;
				}
				prodMinBuyAmt = Number($scope.inputVO.fProdMinBuyAmtC2);
				prodMinGrdAmt = Number($scope.inputVO.fProdMinGrdAmtC2);
				purchaseAmt = Number($scope.inputVO.fAddprodAmtC2);
			}
			if(idx == "3") {
				if($scope.inputVO.fProdIdC3 == "") {
					$scope.showErrorMsg("請先選擇子基金3");
					$scope.addProdClearD(type, idx, true);
					return;
				}
				prodMinBuyAmt = Number($scope.inputVO.fProdMinBuyAmtC3);
				prodMinGrdAmt = Number($scope.inputVO.fProdMinGrdAmtC3);
				purchaseAmt = Number($scope.inputVO.fAddprodAmtC3);
			}
			if(idx == "4") {
				if($scope.inputVO.fProdIdC4 == "") {
					$scope.showErrorMsg("請先選擇子基金4");
					$scope.addProdClearD(type, idx, true);
					return;
				}
				prodMinBuyAmt = Number($scope.inputVO.fProdMinBuyAmtC4);
				prodMinGrdAmt = Number($scope.inputVO.fProdMinGrdAmtC4);
				purchaseAmt = Number($scope.inputVO.fAddprodAmtC4);
			}
			if(idx == "5") {
				if($scope.inputVO.fProdIdC5 == "") {
					$scope.showErrorMsg("請先選擇子基金5");
					$scope.addProdClearD(type, idx, true);
					return;
				}
				prodMinBuyAmt = Number($scope.inputVO.fProdMinBuyAmtC5);
				prodMinGrdAmt = Number($scope.inputVO.fProdMinGrdAmtC5);
				purchaseAmt = Number($scope.inputVO.fAddprodAmtC5);
			}
			
			if (purchaseAmt && prodMinBuyAmt && prodMinGrdAmt && (purchaseAmt < prodMinBuyAmt || purchaseAmt % prodMinGrdAmt != 0)) {
				$scope.showErrorMsg("ehl_01_sot310_007");  //申購面額須高於最低申購面額，且須為累進申購面額之倍數。
				if(idx == "1") $scope.inputVO.fAddprodAmtC1 = undefined;
				if(idx == "2") $scope.inputVO.fAddprodAmtC2 = undefined;
				if(idx == "3") $scope.inputVO.fAddprodAmtC3 = undefined;
				if(idx == "4") $scope.inputVO.fAddprodAmtC4 = undefined;
				if(idx == "5") $scope.inputVO.fAddprodAmtC5 = undefined;
			}
		}
		
		//新增子基金資料1
		$scope.setAddProdData = function(data, idx) {
			if(idx == 1) {
				$scope.inputVO.fProdIdC1 = data.prodDTL[0].PRD_ID;
				$scope.inputVO.fProdNameC1 = data.prodDTL[0].FUND_CNAME;		//商品名稱
				$scope.inputVO.fProdCurrC1 = data.prodDTL[0].CURRENCY_STD_ID;	//計價幣別
				$scope.inputVO.fProdRiskLvC1 = data.prodDTL[0].RISKCATE_ID;
			} else if(idx == 2) {
				$scope.inputVO.fProdIdC2 = data.prodDTL[0].PRD_ID;
				$scope.inputVO.fProdNameC2 = data.prodDTL[0].FUND_CNAME;		//商品名稱
				$scope.inputVO.fProdCurrC2 = data.prodDTL[0].CURRENCY_STD_ID;	//計價幣別
				$scope.inputVO.fProdRiskLvC2 = data.prodDTL[0].RISKCATE_ID;
			} else if(idx == 3) {
				$scope.inputVO.fProdIdC3 = data.prodDTL[0].PRD_ID;
				$scope.inputVO.fProdNameC3 = data.prodDTL[0].FUND_CNAME;		//商品名稱
				$scope.inputVO.fProdCurrC3 = data.prodDTL[0].CURRENCY_STD_ID;	//計價幣別
				$scope.inputVO.fProdRiskLvC3 = data.prodDTL[0].RISKCATE_ID;
			} else if(idx == 4) {
				$scope.inputVO.fProdIdC4 = data.prodDTL[0].PRD_ID;
				$scope.inputVO.fProdNameC4 = data.prodDTL[0].FUND_CNAME;		//商品名稱
				$scope.inputVO.fProdCurrC4 = data.prodDTL[0].CURRENCY_STD_ID;	//計價幣別
				$scope.inputVO.fProdRiskLvC4 = data.prodDTL[0].RISKCATE_ID;
			} else if(idx == 5) {
				$scope.inputVO.fProdIdC5 = data.prodDTL[0].PRD_ID;
				$scope.inputVO.fProdNameC5 = data.prodDTL[0].FUND_CNAME;		//商品名稱
				$scope.inputVO.fProdCurrC5 = data.prodDTL[0].CURRENCY_STD_ID;	//計價幣別
				$scope.inputVO.fProdRiskLvC5 = data.prodDTL[0].RISKCATE_ID;
			}
			
		}
		
		//清空新增子基金1商品資料
		$scope.addProdClearC1 = function(isClearProdId) {
			if (isClearProdId) {
				$scope.inputVO.fProdIdC1 = ''; //也要清空產品編號
			}
			$scope.inputVO.fProdNameC1 = '';     //基金名稱
			$scope.inputVO.fProdCurrC1 = '';     //計價幣別
			$scope.inputVO.fProdRiskLvC1 = '';   //產品風險等級
			$scope.inputVO.fAddprodAmtC1 = undefined;       //申購金額 
			$scope.inputVO.fProdMinBuyAmtC1 = undefined;    //最低申購金額
			$scope.inputVO.fProdMinGrdAmtC1 = undefined;    //累進申購面額
		}
		
		//清空新增子基金2商品資料
		$scope.addProdClearC2 = function(isClearProdId) {
			if (isClearProdId) {
				$scope.inputVO.fProdIdC2 = ''; //也要清空產品編號
			}
			$scope.inputVO.fProdNameC2 = '';     //基金名稱
			$scope.inputVO.fProdCurrC2 = '';     //計價幣別
			$scope.inputVO.fProdRiskLvC2 = '';   //產品風險等級
			$scope.inputVO.fAddprodAmtC2 = undefined;       //申購金額 
			$scope.inputVO.fProdMinBuyAmtC2 = undefined;    //最低申購金額
			$scope.inputVO.fProdMinGrdAmtC2 = undefined;    //累進申購面額
		}
		
		//清空新增子基金3商品資料
		$scope.addProdClearC3 = function(isClearProdId) {
			if (isClearProdId) {
				$scope.inputVO.fProdIdC3 = ''; //也要清空產品編號
			}
			$scope.inputVO.fProdNameC3 = '';     //基金名稱
			$scope.inputVO.fProdCurrC3 = '';     //計價幣別
			$scope.inputVO.fProdRiskLvC3 = '';   //產品風險等級
			$scope.inputVO.fAddprodAmtC3 = undefined;       //申購金額 
			$scope.inputVO.fProdMinBuyAmtC3 = undefined;    //最低申購金額
			$scope.inputVO.fProdMinGrdAmtC3 = undefined;    //累進申購面額
		}
		
		//清空新增子基金4商品資料
		$scope.addProdClearC4 = function(isClearProdId) {
			if (isClearProdId) {
				$scope.inputVO.fProdIdC4 = ''; //也要清空產品編號
			}
			$scope.inputVO.fProdNameC4 = '';     //基金名稱
			$scope.inputVO.fProdCurrC4 = '';     //計價幣別
			$scope.inputVO.fProdRiskLvC4 = '';   //產品風險等級
			$scope.inputVO.fAddprodAmtC4 = undefined;       //申購金額 
			$scope.inputVO.fProdMinBuyAmtC4 = undefined;    //最低申購金額
			$scope.inputVO.fProdMinGrdAmtC4 = undefined;    //累進申購面額
		}
		
		//清空新增子基金5商品資料
		$scope.addProdClearC5 = function(isClearProdId) {
			if (isClearProdId) {
				$scope.inputVO.fProdIdC5 = ''; //也要清空產品編號
			}
			$scope.inputVO.fProdNameC5 = '';     //基金名稱
			$scope.inputVO.fProdCurrC5 = '';     //計價幣別
			$scope.inputVO.fProdRiskLvC5 = '';   //產品風險等級
			$scope.inputVO.fAddprodAmtC5 = undefined;       //申購金額 
			$scope.inputVO.fProdMinBuyAmtC5 = undefined;    //最低申購金額
			$scope.inputVO.fProdMinGrdAmtC5 = undefined;    //累進申購面額
		}
		
		//清空商品資料
		$scope.addProdClearD = function(type, idx, isClearPID) {
			if(idx == "1") $scope.addProdClearC1(isClearPID);
			if(idx == "2") $scope.addProdClearC2(isClearPID);
			if(idx == "3") $scope.addProdClearC3(isClearPID);
			if(idx == "4") $scope.addProdClearC4(isClearPID);
			if(idx == "5") $scope.addProdClearC5(isClearPID);
		}
		
		//新增子基金商品資料
		$scope.getAddProdDTL = function(type, idx) {
			var deferred = $q.defer();
			
			//新增子基金檢核
			if(idx == "2") {
				if($scope.inputVO.fProdIdC1 == "") {
					$scope.showErrorMsg("請先選擇子基金1");
					$scope.addProdClearD(type, idx, true);
					return;
				}
			} else if(idx == "3") {
				if($scope.inputVO.fProdIdC1 == "" || $scope.inputVO.fProdIdC2 == "") {
					$scope.showErrorMsg("請先選擇子基金1以及子基金2");
					$scope.addProdClearD(type, idx, true);
					return;
				}
			} else if(idx == "4") {
				if($scope.inputVO.fProdIdC1 == "" || $scope.inputVO.fProdIdC2 == "" || $scope.inputVO.fProdIdC3 == "") {
					$scope.showErrorMsg("請先選擇子基金1、2、3");
					$scope.addProdClearD(type, idx, true);
					return;
				}
			} else if(idx == "5") {
				if($scope.inputVO.fProdIdC1 == "" || $scope.inputVO.fProdIdC2 == "" || $scope.inputVO.fProdIdC3 == "" || $scope.inputVO.fProdIdC4 == "") {
					$scope.showErrorMsg("請先選擇子基金1、2、3、4");
					$scope.addProdClearD(type, idx, true);
					return;
				}
			}			
			//清空選擇的商品資料
			$scope.addProdClearD(type, idx, false);
			
			var obj = eval("$scope.inputVO.fProdId" + type + idx);
			//將要搜尋的新增子基金放到PROD_ID
			var prodIdC = $filter('uppercase')(obj);
		    
			if(prodIdC) {
				$scope.sendRecv("SOT1610", "getProdDTL", "com.systex.jbranch.app.server.fps.sot1610.SOT1610InputVO", 
						{"dynamicType":"C", "prodId":prodIdC, "custID":$scope.inputVO.custID, "trustTS":$scope.inputVO.trustTS, "prodIdM":$scope.inputVO.prodId, "dynamicProdCurrM":$scope.inputVO.prodCurr},
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.errorMsg != null && tota[0].body.errorMsg != "") {
								//適配有錯誤訊息
								$scope.showErrorMsg(tota[0].body.errorMsg);
								$scope.addProdClearD(type, idx, true);
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
									//子基金商品資料
									$scope.setAddProdData(tota[0].body, idx);
								}
								
								$scope.findAddProdNfMinBuyAmt(type, idx);  //最低申購金額								
								return;
							} else {
								$scope.showErrorMsg("ehl_01_common_009");					
								$scope.addProdClearD(type, idx, true);
							}
						}
						//查無商品 需清除商品ID
						$scope.addProdClearD(type, idx, true);
				});
			}
			return deferred.promise;
		};
		
		//動態鎖利新增子基金商品查詢
		$scope.goPRD110D = function(type, idx) {
			var custID = $scope.inputVO.custID;
			var trustTS = $scope.inputVO.trustTS;
			var prodIdM = $scope.inputVO.prodId;
			var dynamicProdCurrM = $scope.inputVO.prodCurr;
			
			//新增子基金檢核
			if(idx == "2") {
				if($scope.inputVO.fProdIdC1 == "") {
					$scope.showErrorMsg("請先選擇子基金1");
					$scope.addProdClearD(type, idx, true);
					return;
				}
			} else if(idx == "3") {
				if($scope.inputVO.fProdIdC1 == "" || $scope.inputVO.fProdIdC2 == "") {
					$scope.showErrorMsg("請先選擇子基金1以及子基金2");
					$scope.addProdClearD(type, idx, true);
					return;
				}
			} else if(idx == "4") {
				if($scope.inputVO.fProdIdC1 == "" || $scope.inputVO.fProdIdC2 == "" || $scope.inputVO.fProdIdC3 == "") {
					$scope.showErrorMsg("請先選擇子基金1、2、3");
					$scope.addProdClearD(type, idx, true);
					return;
				}
			} else if(idx == "5") {
				if($scope.inputVO.fProdIdC1 == "" || $scope.inputVO.fProdIdC2 == "" || $scope.inputVO.fProdIdC3 == "" || $scope.inputVO.fProdIdC4 == "") {
					$scope.showErrorMsg("請先選擇子基金1、2、3、4");
					$scope.addProdClearD(type, idx, true);
					return;
				}
			}
			
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT110/SOT110_ROUTE.html',
				className: 'PRD110',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					if(type == "M") {
						$scope.txnName = "搜尋母基金";
					}else{
						$scope.txnName = "搜尋子基金";
					}
					$scope.isPop = true;
	        		$scope.routeURL = 'assets/txn/PRD110/PRD110.html';
	        		$scope.tradeType = "1";
	        		$scope.cust_id = custID;
	        		$scope.trustTS = trustTS;
	        		$scope.dynamicType = type; //動態鎖利類別 M:母基金 C:子基金
	        		if(type == "C") {
	        			//子基金需要與母基金是同系列基金且相同幣別
	        			$scope.sameSerialYN = "Y"; 
	        			$scope.sameSerialProdId = prodIdM;
	        			$scope.dynamicProdCurrM = dynamicProdCurrM;
	        		}
	        		$scope.fromSOTProdYN = "Y"; //下單商品搜尋//下單只需要輸入客戶ID時檢核，商品搜尋時不需再次檢核
	        		$scope.fromSOT110 = true; //搭配貸款風險預告書判斷
	            }]
			}).closePromise.then(function (data) {
				if(data.value != "cancel") {
					if(idx == "1") $scope.inputVO.fProdIdC1 = data.value.PRD_ID;
					if(idx == "2") $scope.inputVO.fProdIdC2 = data.value.PRD_ID;
					if(idx == "3") $scope.inputVO.fProdIdC3 = data.value.PRD_ID;
					if(idx == "4") $scope.inputVO.fProdIdC4 = data.value.PRD_ID;
					if(idx == "5") $scope.inputVO.fProdIdC5 = data.value.PRD_ID;
					$scope.getAddProdDTL(type, idx);
				}
			});
		};	
		
		//變更子基金狀態
		$scope.checkfProdStatus = function(data, idx) {
			if(idx == "1") {
				if(data == $scope.inputVO.eviNumTypeC1) {
					$scope.showErrorMsg("子基金扣款狀態與之前相同");
					$scope.inputVO.fProdStatusC1 = "";
					return;
				}
			} else if(idx == "2") {
				if(data == $scope.inputVO.eviNumTypeC2) {
					$scope.showErrorMsg("子基金扣款狀態與之前相同");
					$scope.inputVO.fProdStatusC2 = "";
					return;
				}
			} else if(idx == "3") {
				if(data == $scope.inputVO.eviNumTypeC3) {
					$scope.showErrorMsg("子基金扣款狀態與之前相同");
					$scope.inputVO.fProdStatusC3 = "";
					return;
				}
			} else if(idx == "4") {
				if(data == $scope.inputVO.eviNumTypeC4) {
					$scope.showErrorMsg("子基金扣款狀態與之前相同");
					$scope.inputVO.fProdStatusC4 = "";
					return;
				}
			} else if(idx == "5") {
				if(data == $scope.inputVO.eviNumTypeC5) {
					$scope.showErrorMsg("子基金扣款狀態與之前相同");
					$scope.inputVO.fProdStatusC5 = "";
					return;
				}
			}
		}
		
		$scope.isEmpty = function(str) {
			if(str == undefined || str == null || str == "")
				return true;
			else
				return false;
		}
		
		//確認基金註記 => 此交易檢查是否停止申購
		$scope.checkFundStatusC1 = () => $scope.sendRecv("SOT703", "qryFundMemo",
				"com.systex.jbranch.app.server.fps.sot703.SOT703InputVO", {prodId: $scope.inputVO.fProdIdC1},
				(tota) => tota[0].body[0] && tota[0].body[0].NO_E_PURCHASE ? $scope.showErrorMsg(tota[0].body[0].NO_E_PURCHASE) : $scope.getAddProdDTL("C", "1"));
		$scope.checkFundStatusC2 = () => $scope.sendRecv("SOT703", "qryFundMemo",
				"com.systex.jbranch.app.server.fps.sot703.SOT703InputVO", {prodId: $scope.inputVO.fProdIdC2},
				(tota) => tota[0].body[0] && tota[0].body[0].NO_E_PURCHASE ? $scope.showErrorMsg(tota[0].body[0].NO_E_PURCHASE) : $scope.getAddProdDTL("C", "2"));
		$scope.checkFundStatusC3 = () => $scope.sendRecv("SOT703", "qryFundMemo",
				"com.systex.jbranch.app.server.fps.sot703.SOT703InputVO", {prodId: $scope.inputVO.fProdIdC3},
				(tota) => tota[0].body[0] && tota[0].body[0].NO_E_PURCHASE ? $scope.showErrorMsg(tota[0].body[0].NO_E_PURCHASE) : $scope.getAddProdDTL("C", "3"));
		$scope.checkFundStatusC4 = () => $scope.sendRecv("SOT703", "qryFundMemo",
				"com.systex.jbranch.app.server.fps.sot703.SOT703InputVO", {prodId: $scope.inputVO.fProdIdC4},
				(tota) => tota[0].body[0] && tota[0].body[0].NO_E_PURCHASE ? $scope.showErrorMsg(tota[0].body[0].NO_E_PURCHASE) : $scope.getAddProdDTL("C", "4"));
		$scope.checkFundStatusC5 = () => $scope.sendRecv("SOT703", "qryFundMemo",
				"com.systex.jbranch.app.server.fps.sot703.SOT703InputVO", {prodId: $scope.inputVO.fProdIdC5},
				(tota) => tota[0].body[0] && tota[0].body[0].NO_E_PURCHASE ? $scope.showErrorMsg(tota[0].body[0].NO_E_PURCHASE) : $scope.getAddProdDTL("C", "5"));
		
		// init
        $scope.init = function() {
        	$scope.custDTLbody = {};
        	$scope.custDTL = [];
        	$scope.carList = [];
        	$scope.inputVO.prodType='8';  //8：基金動態鎖利
        	$scope.inputVO.tradeType='4'; //4：基金動態鎖利事件變更
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
			$scope.checkTradeDateType();//預設即時或預約
			$scope.overTradeTime = false;
			$scope.inputVO.isWeb = 'N';		//臨櫃交易N, 快速申購Y
			$scope.inputVO.prospTypeDisabled = false;
			$scope.inputVO.hnwcYN = ''; //是否為高資產客戶 Y/N 
			$scope.inputVO.hnwcServiceYN = ''; //可提供高資產商品或服務 Y/N 
			$scope.inputVO.flagNumber = '' //90天內是否有貸款紀錄 Y/N
			$scope.inputVO.chgStatusYN = "N";
			$scope.inputVO.chgTransDateYN = "N";
			$scope.inputVO.chgAmountYN = "N";
			$scope.inputVO.chgAddProdYN = "N";
			$scope.inputVO.transDate = "";
			$scope.inputVO.tradeDateType = "";
			$scope.onChgStatusYN();
			$scope.onChgTransDateYN();
			$scope.onChgAmountYN();
			$scope.onChgAddProdYN();
		};
		$scope.init();
		
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
			$scope.query();
		} else {
			//從資產總覽CRM821交易過來，帶CUSTID及庫存資料
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
		
});