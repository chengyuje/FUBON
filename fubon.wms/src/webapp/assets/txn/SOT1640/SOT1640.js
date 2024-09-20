/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT1640Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q, validateService, sotService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT1640Controller";
		//繼承PRD100高齡檢核
		$controller('PRD100Controller', {$scope: $scope});
		
		debugger
        getParameter.XML(["SOT.CUST_TYPE", "SOT.TRADE_DATE_TYPE", "COMMON.YES_NO", "SOT.RESERVE_DATE_TIMESTAMP", "SOT.SPEC_CUSTOMER",'SOT.FUND_DECIMAL_POINT', "SOT.RESERVE_TRADE_DAYS","SOT.TRANSFER_TYPE_DYNA", "SOT.PROSPECTUS_TYPE"], function(totas) {
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
				//轉換方式
				$scope.mappingSet['SOT.TRANSFER_TYPE_DYNA']  = totas.data[totas.key.indexOf('SOT.TRANSFER_TYPE_DYNA')];
				//公開說明書選項
				$scope.mappingSet['SOT.PROSPECTUS_TYPE'] = totas.data[totas.key.indexOf('SOT.PROSPECTUS_TYPE')];
				
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
			$scope.inputVO.debitAcct = '';								//扣款帳號
			$scope.inputVO.trustAcct = '';								//信託帳號
			$scope.inputVO.creditAcct = '';								//收益入帳帳號
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
			$scope.inputVO.trustCurrType = $scope.setTrustCurrType($scope.inputVO.prodFus20, $scope.inputVO.trustCurr);
			$scope.inputVO.fundInfoSelling = data.fundInfoSelling; //建議售出Y/N 要出提示 ehl_01_SOT702_016
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
			$scope.inputVO.transferType = ""; 	//轉換方式
			$scope.inputVO.numUnits = undefined; 	//單位數
			$scope.inputVO.presentVal = undefined; 	//參考現值
			$scope.presentVal = undefined; 	//參考現值
			$scope.inputVO.prospectusType = undefined;    	//公開說明書
			$scope.inputVO.inProdId = '';     //轉入基金代碼
			$scope.inputVO.inProdName = '';     //轉入基金名稱
			$scope.inputVO.inProdCurr = '';     //轉入計價幣別
			$scope.inputVO.inProdRiskLv = '';   //轉入產品風險等級
			$scope.inputVO.inProdC1YN = '';
			$scope.inputVO.inProdC2YN = '';
			$scope.inputVO.inProdC3YN = '';
			$scope.inputVO.inProdC4YN = '';
			$scope.inputVO.inProdC5YN = '';
			$("#tradeDateType1").attr("disabled", false);
			
			//清空子基金
			$scope.prodClearC1(true);
			$scope.prodClearC2(true);
			$scope.prodClearC3(true);
			$scope.prodClearC4(true);
			$scope.prodClearC5(true);
		}
		
		//清空子轉入母基金商品資料
		$scope.prodClearIn = function() {
			$scope.inputVO.inProdId = '';     //轉入基金代碼
			$scope.inputVO.inProdName = '';     //轉入基金名稱
			$scope.inputVO.inProdCurr = '';     //轉入計價幣別
			$scope.inputVO.inProdRiskLv = '';   //轉入產品風險等級
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
			$scope.inputVO.numUnitsC1 = undefined; 	//單位數
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
			$scope.inputVO.numUnitsC2 = undefined; 	//單位數
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
			$scope.inputVO.numUnitsC3 = undefined; 	//單位數
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
			$scope.inputVO.numUnitsC4 = undefined; 	//單位數
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
			$scope.inputVO.numUnitsC5 = undefined; 	//單位數
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
									
									$scope.inputVO.custProType = tota[0].body.custProType;                  // 專業投資人類型
									$scope.inputVO.isFirstTrade = tota[0].body.isFirstTrade;                //是否首購
									$scope.inputVO.isBanker = tota[0].body.isBanker; 						//是否為行員
									$scope.inputVO.hnwcYN = tota[0].body.hnwcYN;
									$scope.inputVO.hnwcServiceYN = tota[0].body.hnwcServiceYN;
									$scope.inputVO.flagNumber = tota[0].body.flagNumber; 					//90天內是否有貸款紀錄 Y/N
									
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
		
		$scope.notEmptyValue = function(value) {
			if(value == undefined || value == null || value == "")
				return false;
			else
				return true;
		}
		
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
			$scope.inputVO.trustCurrType = row.TRUST_CURR_TYPE;     //信託幣別類別
			if(row.TRUST_CURR_TYPE=='C') { 
				$scope.inputVO.prodFus20 = row.TRUST_CURR_TYPE;     //C:國內基金 
//				$scope.currencyType();                              //選擇信託幣別 查最低申購金額
		  	}
			$scope.inputVO.numUnits = row.UNIT_NUM;
			$scope.inputVO.presentVal = row.PRESENT_VAL.toFixed(4);
			$scope.inputVO.transferType = row.TRANSFER_TYPE;
			$scope.inputVO.prodFus07 = row.FUS07;	//2017-09-11 取基金-小數點位數
			$scope.inputVO.trustCurr = row.TRUST_CURR;     //信託幣別
			$scope.inputVO.trustAcct = row.TRUST_ACCT;  //信託帳號
			$scope.inputVO.creditAcct = row.CREDIT_ACCT; //收益入帳帳號
			$scope.inputVO.purchaseAmt = row.PURCHASE_AMT;     //申購金額 
			$scope.inputVO.certificateID = row.CERTIFICATE_ID;	//庫存憑證編號
			$scope.inputVO.tradeDateType = row.TRADE_DATE_TYPE;     //交易日期類別
			$scope.inputVO.tradeDate = row.TRADE_DATE;     //交易日期
			$scope.inputVO.narratorID = row.NARRATOR_ID;     //解說專員員編
			$scope.inputVO.narratorName = row.NARRATOR_NAME;     //解說專員姓名
			$scope.inputVO.prospectusType = row.PROSPECTUS_TYPE; //取得公開說明書方式
			
		    $scope.inputVO.prodIdC1 = row.PROD_ID_C1;
			$scope.inputVO.prodNameC1 = row.PROD_NAME_C1;			//商品名稱
			$scope.inputVO.prodRiskLvC1 = row.PROD_RISK_LV_C1;
			$scope.inputVO.purchaseAmtC1 = row.PURCHASE_AMT_C1;     //申購金額 
			$scope.inputVO.numUnitsC1 = row.UNIT_NUM_C1;     //單位數
			
			$scope.inputVO.prodIdC2 = row.PROD_ID_C2;
			$scope.inputVO.prodNameC2 = row.PROD_NAME_C2;			//商品名稱
			$scope.inputVO.prodRiskLvC2 = row.PROD_RISK_LV_C2;
			$scope.inputVO.purchaseAmtC2 = row.PURCHASE_AMT_C2;     //申購金額 
			$scope.inputVO.numUnitsC2 = row.UNIT_NUM_C2;     //單位數
			
			$scope.inputVO.prodIdC3 = row.PROD_ID_C3;
			$scope.inputVO.prodNameC3 = row.PROD_NAME_C3;			//商品名稱
			$scope.inputVO.prodRiskLvC3 = row.PROD_RISK_LV_C3;
			$scope.inputVO.purchaseAmtC3 = row.PURCHASE_AMT_C3;     //申購金額 
			$scope.inputVO.numUnitsC3 = row.UNIT_NUM_C3;     //單位數
			
			$scope.inputVO.prodIdC4 = row.PROD_ID_C4;
			$scope.inputVO.prodNameC4 = row.PROD_NAME_C4;			//商品名稱
			$scope.inputVO.prodRiskLvC4 = row.PROD_RISK_LV_C4;
			$scope.inputVO.purchaseAmtC4 = row.PURCHASE_AMT_C4;     //申購金額 
			$scope.inputVO.numUnitsC4 = row.UNIT_NUM_C4;     //單位數
			
			$scope.inputVO.prodIdC5 = row.PROD_ID_C5;
			$scope.inputVO.prodNameC5 = row.PROD_NAME_C5;			//商品名稱
			$scope.inputVO.prodRiskLvC5 = row.PROD_RISK_LV_C5;
			$scope.inputVO.purchaseAmtC5 = row.PURCHASE_AMT_C5;     //申購金額 
			$scope.inputVO.numUnitsC5 = row.UNIT_NUM_C5;     //單位數
			
			$scope.inputVO.inProdId = row.IN_PROD_ID;     //轉入基金代碼
			$scope.inputVO.inProdName = row.IN_PROD_NAME;     //轉入基金名稱
			$scope.inputVO.inProdCurr = row.IN_PROD_CURR;     //轉入計價幣別
			$scope.inputVO.inProdRiskLv = row.IN_PROD_RISK_LV;   //轉入產品風險等級
			
			$scope.inputVO.inProdC1YN = row.IN_PROD_C1_YN;
			$scope.inputVO.inProdC2YN = row.IN_PROD_C2_YN;
			$scope.inputVO.inProdC3YN = row.IN_PROD_C3_YN;
			$scope.inputVO.inProdC4YN = row.IN_PROD_C4_YN;
			$scope.inputVO.inProdC5YN = row.IN_PROD_C5_YN;
		};
		
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
			debugger
			var obj = eval("$scope.inputVO.prodId" + (type == "M" ? "" : type) + idx);
			//將要搜尋的母基金或子基金放到PROD_ID
			var prodId = $filter('uppercase')(obj);
		    
			if(prodId) {
				$scope.sendRecv("SOT1640", "getProdDTL", "com.systex.jbranch.app.server.fps.sot1640.SOT1640InputVO", {"prodId": prodId},
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
		
		//取得可轉換母基金商品資料
		$scope.getInProdDTL = function() {
			var deferred = $q.defer();
			
			if($scope.inputVO.inProdId) {
				$scope.sendRecv("SOT1640", "getInProdDTL", "com.systex.jbranch.app.server.fps.sot1640.SOT1640InputVO", {"prodId": $scope.inputVO.prodId, "inProdId": $scope.inputVO.inProdId, "custID": $scope.inputVO.custID, "dynamicProdCurrM":$scope.inputVO.prodCurr},
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.errorMsg != null && tota[0].body.errorMsg != undefined && tota[0].body.errorMsg != "") {
									$scope.showErrorMsg(tota[0].body.errorMsg);					
									$scope.prodClearIn();
									return;
								}
								if (tota[0].body.prodDTL != null) {
									debugger
									if (tota[0].body.prodDTL[0]) {
										$scope.inputVO.inProdId = tota[0].body.prodDTL[0].PRD_ID;	//母基金只有商品代碼不一樣，其他帶一般基金商品變數名稱
										$scope.inputVO.inProdName = tota[0].body.prodDTL[0].FUND_CNAME;						//商品名稱
										$scope.inputVO.inProdCurr = tota[0].body.prodDTL[0].CURRENCY_STD_ID;					//計價幣別
										$scope.inputVO.inProdRiskLv = tota[0].body.prodDTL[0].RISKCATE_ID;
									}
									return;
								} else {
									$scope.showErrorMsg("ehl_01_common_009");					
									$scope.prodClearIn();
								}
							}
							//查無商品 需清除轉入商品資料
							$scope.prodClearIn();
					});
			}
			
			return deferred.promise;
		};
		
		//查詢購物車
		$scope.noCallCustQuery = function () {
			debugger
			var deferred = $q.defer();
			$scope.sendRecv("SOT1640", "query", "com.systex.jbranch.app.server.fps.sot1640.SOT1640InputVO", {"tradeSEQ": $scope.inputVO.tradeSEQ},
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
				
				if($scope.inputVO.transferType == "1" && !$scope.notEmptyValue($scope.inputVO.inProdId)) {
					//母基金轉換
					$scope.showErrorMsg("請選擇轉入母基金標的");
		    		return;
				} else if($scope.inputVO.transferType == "2") {
					//子基金轉入母基金
					if(!$scope.notEmptyValue($scope.inputVO.inProdC1YN) && !$scope.notEmptyValue($scope.inputVO.inProdC2YN) && !$scope.notEmptyValue($scope.inputVO.inProdC3YN) &&
							!$scope.notEmptyValue($scope.inputVO.inProdC4YN) && !$scope.notEmptyValue($scope.inputVO.inProdC5YN)) {
						$scope.showErrorMsg("請選擇子基金轉入母基金標的");
			    		return;
					}
				}
				
//				$scope.setTrustCurr(); //設定信託幣別 CALL  $scope.checkTrustACcct()
//				$scope.inputVO.purchaseAmt = $scope.cleanCurrency($scope.inputVO.purchaseAmt);
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
		    $scope.sendRecv("SOT1640", "save", "com.systex.jbranch.app.server.fps.sot1640.SOT1640InputVO", $scope.inputVO,
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
			$scope.sendRecv("SOT1640", "save", "com.systex.jbranch.app.server.fps.sot1640.SOT1640InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if (tota[0].body.errorMsg != null) {
							$scope.showErrorMsg(tota[0].body.errorMsg);								
							return;
						} else {
							$scope.sendRecv("SOT1640", "next", "com.systex.jbranch.app.server.fps.sot1640.SOT1640InputVO", {tradeSEQ : $scope.inputVO.tradeSEQ, 'prodType':$scope.inputVO.prodType},
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
											$rootScope.menuItemInfo.url = "assets/txn/SOT1641/SOT1641.html";							
											return;
										}
							});	
						}
					}
			});
		}
		
		$scope.goSOT132=function(){
			$scope.prodClearM(true);
			var custId = $scope.inputVO.custID;
			var trustTS = $scope.inputVO.trustTS;
			var debitAcct = $scope.inputVO.debitAcct;
			var cartList = $scope.cartList;
			var contractID = $scope.inputVO.contractID;
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT132/SOT132.html',
				className: 'SOT132',
				controller:['$scope',function($scope){
					$scope.txnName = "SOT1640";
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
			$scope.inputVO.numUnits           =   Number(data.prodM.CurUntNum).toFixed(4); //原單位數
			$scope.inputVO.trustAcct          =   data.prodM.AcctId02.trim();  //信託帳號
			$scope.inputVO.creditAcct         =   data.prodM.PayAcctId; //收益入帳帳號
			$scope.inputVO.branchNbr 		  =   $scope.inputVO.trustAcct ? $scope.inputVO.trustAcct.substring(2,5) : "";
			$scope.inputVO.unitValue 		  =   $scope.inputVO.presentVal/$scope.inputVO.numUnits //單位(計算贖回方式之值) presentVal= numUnits * unitValue
			$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.inputVO.trustCurr});
			//取得母基金商品資料
			$scope.getProdDTL("M", "");
			//子基金
			if(data.prodC1) {
				$scope.inputVO.prodIdC1 = data.prodC1.FundNO; 
				$scope.inputVO.purchaseAmtC1 = data.prodC1.CurAmt; //信託金額
				$scope.inputVO.numUnitsC1 = Number(data.prodC1.CurUntNum).toFixed(4); //原單位數
				//取得子基金商品資料
				$scope.getProdDTL("C", "1");
			}
			if(data.prodC2) {
				$scope.inputVO.prodIdC2 = data.prodC2.FundNO;
				$scope.inputVO.purchaseAmtC2 = data.prodC2.CurAmt; //信託金額
				$scope.inputVO.numUnitsC2 = Number(data.prodC2.CurUntNum).toFixed(4); //原單位數
				//取得子基金商品資料
				$scope.getProdDTL("C", "2");
			}
			if(data.prodC3) {
				$scope.inputVO.prodIdC3 = data.prodC3.FundNO; 
				$scope.inputVO.purchaseAmtC3 = data.prodC3.CurAmt; //信託金額
				$scope.inputVO.numUnitsC3 = Number(data.prodC3.CurUntNum).toFixed(4); //原單位數
				//取得子基金商品資料
				$scope.getProdDTL("C", "3");
			}
			if(data.prodC4) {
				$scope.inputVO.prodIdC4 = data.prodC4.FundNO; 
				$scope.inputVO.purchaseAmtC4 = data.prodC4.CurAmt; //信託金額
				$scope.inputVO.numUnitsC4 = Number(data.prodC4.CurUntNum).toFixed(4); //原單位數
				//取得子基金商品資料
				$scope.getProdDTL("C", "4");
			}
			if(data.prodC5) {
				$scope.inputVO.prodIdC5 = data.prodC5.FundNO; 
				$scope.inputVO.purchaseAmtC5 = data.prodC5.CurAmt; //信託金額
				$scope.inputVO.numUnitsC5 = Number(data.prodC5.CurUntNum).toFixed(4); //原單位數
				//取得子基金商品資料
				$scope.getProdDTL("C", "5");
			}
		}
		
		//可轉換母基金查詢
		$scope.goPRD110In = function() {
			var custID = $scope.inputVO.custID;
			var trustTS = $scope.inputVO.trustTS;
			var prodIdM = $scope.inputVO.prodId;
			var dynamicProdCurrM = $scope.inputVO.prodCurr;
			
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT1640/SOT1640_ROUTE.html',
				className: 'PRD110',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					$scope.txnName = "搜尋可轉換母基金";
					$scope.isPop = true;
	        		$scope.routeURL = 'assets/txn/PRD110/PRD110.html';
	        		$scope.tradeType = "1";
	        		$scope.cust_id = custID;
	        		$scope.trustTS = trustTS;
	        		$scope.dynamicType = "M"; //動態鎖利類別 M:母基金 C:子基金
	        		//可轉換母基金需要與原庫存母基金是同系列基金
        			$scope.sameSerialYN = "Y"; 
        			$scope.sameSerialProdId = prodIdM;
        			$scope.dynamicProdCurrM = dynamicProdCurrM;
	        		$scope.fromSOTProdYN = "Y"; //下單商品搜尋//下單只需要輸入客戶ID時檢核，商品搜尋時不需再次檢核
	        		$scope.fromSOT110 = true; //搭配貸款風險預告書判斷
	            }]
			}).closePromise.then(function (data) {
				if(data.value != "cancel") {
					debugger
					$scope.inputVO.inProdId = data.value.PRD_ID; //轉入母基金
					$scope.getInProdDTL();
				}
			});
		};	
		
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
		
		//轉換方式變更時
		$scope.setTransferType = function() {
			if($scope.inputVO.transferType == '1') { //母基金轉換
				if($scope.inputVO.numUnits == 0) {
					$scope.showErrorMsg("母基金無庫存，不得選擇母基金轉換");
					$scope.inputVO.transferType = "";
        			return;
				}
				$scope.inputVO.inProdC1YN = "N";
				$scope.inputVO.inProdC2YN = "N";
				$scope.inputVO.inProdC3YN = "N";
				$scope.inputVO.inProdC4YN = "N";
				$scope.inputVO.inProdC5YN = "N";
			} else if($scope.inputVO.transferType == '2') {	//子基金轉回母基金
				if(!$scope.inputVO.prodIdC1) {
					$scope.showErrorMsg("此庫存無子基金");
					$scope.inputVO.transferType = "";
        			return;
				}
				$scope.inputVO.inProdId = "";
				$scope.inputVO.inProdName = "";
				$scope.inputVO.inProdCurr = "";
				$scope.inputVO.inProdRiskLv = "";
			}
		};
		
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
		
		//選擇轉出子基金時，檢核單位數
		$scope.checkUnitC = function(idx) {
			var chkYN = eval("$scope.inputVO.inProdC" + idx + "YN");
			var nUnit = eval("$scope.inputVO.numUnitsC" + idx);
			if(chkYN == "Y" && (!nUnit || nUnit <= 0)) {
				$scope.showMsg("子基金單位數為0，不可轉入母基金");
				if(idx == "1") $scope.inputVO.inProdC1YN = "N";
				if(idx == "2") $scope.inputVO.inProdC2YN = "N";
				if(idx == "3") $scope.inputVO.inProdC3YN = "N";
				if(idx == "4") $scope.inputVO.inProdC4YN = "N";
				if(idx == "5") $scope.inputVO.inProdC5YN = "N";
    			return;
			}
		}
		
		//確認基金註記 => 此交易檢查是否停止申購
		$scope.checkFundStatusIn = () => $scope.sendRecv("SOT703", "qryFundMemo",
			"com.systex.jbranch.app.server.fps.sot703.SOT703InputVO", {prodId: $scope.inputVO.inProdId},
			(tota) => tota[0].body[0] && tota[0].body[0].NO_E_PURCHASE ? $scope.showErrorMsg(tota[0].body[0].NO_E_PURCHASE) : $scope.getInProdDTL());
		
		// init
        $scope.init = function() {
        	$scope.custDTLbody = {};
        	$scope.custDTL = [];
        	$scope.carList = [];
        	$scope.inputVO.prodType='8';  //8：基金動態鎖利
        	$scope.inputVO.tradeType='3'; //3：基金動態鎖利轉換
        	$scope.inputVO.seniorAuthType='S'; //高齡評估表授權種類(S:下單、A：適配)
        	$scope.inputVO.contractID = ''; //契約編號
    		$scope.inputVO.trustTS = 'S';  //M:金錢信託  S:特金交易--
			$scope.inputVO.tradeSEQ = ''; 
			$scope.inputVO.custID = '';  //客戶ID
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
			$scope.inputVO.flagNumber = ''; //90天內是否有貸款紀錄 Y/N
			$scope.inputVO.transferType = ""; //轉換方式
			$scope.inputVO.otherWithCustId = false; //是否從資產總覽CRM821交易過來，帶CUSTID及庫存資料
		};
		$scope.init();
		
		$scope.inbyCRM821 = function() {
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
		
		$scope.validateSeniorCust = function() {
			if(!$scope.inputVO.custID) return;
			
			$scope.inputVO.prodType='8';  //8：基金動態鎖利
        	$scope.inputVO.tradeType='3'; //3：轉換
        	$scope.inputVO.seniorAuthType='S'; //高齡評估表授權種類(S:下單、A：適配)
        	$scope.inputVO.trustTS = 'S';  //S:特金交易
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
			if($scope.inputVO.otherWithCustId) { //從資產總覽CRM821交易過來，帶CUSTID及庫存資料
				$scope.inbyCRM821();
			} else {
				$scope.getSOTCustInfo(false,true);
			}
			$scope.connector('set','SOTCustID',null);
		};
		
		if(!$scope.connector('get','SOTCustID')) {
			//"不是"從資產總覽交易過來，維持原來
			$scope.query();
		} else {
			//從資產總覽CRM821交易過來，帶CUSTID及庫存資料
			$scope.inputVO.otherWithCustId = true;
			$scope.inputVO.custID = $scope.connector('get','SOTCustID');
			$scope.validateSeniorCust();
		}
		
		
});