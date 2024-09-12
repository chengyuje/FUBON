/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT321Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q, sotService, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT321Controller";

		// filter
		getParameter.XML(["SOT.CUST_TYPE", "SOT.TRUST_CURR_TYPE", "SOT.MARKET_TYPE", "SOT.ENTRUST_TYPE_REDEEM_SN","SOT.SPEC_CUSTOMER","SOT.BN_CUR_LIMIT_GTC", "SOT.BN_GTC_LIMITPRICE_RANGE", "OTH001"], function(totas) {
			if (totas) {
				$scope.mappingSet['SOT.CUST_TYPE'] = totas.data[totas.key.indexOf('SOT.CUST_TYPE')];
				$scope.mappingSet['SOT.TRUST_CURR_TYPE'] = totas.data[totas.key.indexOf('SOT.TRUST_CURR_TYPE')];
				$scope.mappingSet['SOT.MARKET_TYPE'] = totas.data[totas.key.indexOf('SOT.MARKET_TYPE')];
				$scope.mappingSet['SOT.ENTRUST_TYPE_REDEEM_SN'] = totas.data[totas.key.indexOf('SOT.ENTRUST_TYPE_REDEEM_SN')];
		        $scope.mappingSet['SOT.SPEC_CUSTOMER'] = totas.data[totas.key.indexOf('SOT.SPEC_CUSTOMER')];//客戶註記
		        $scope.mappingSet['SOT.BN_CUR_LIMIT_GTC'] = totas.data[totas.key.indexOf('SOT.BN_CUR_LIMIT_GTC')];
				$scope.mappingSet['SOT.BN_GTC_LIMITPRICE_RANGE'] = totas.data[totas.key.indexOf('SOT.BN_GTC_LIMITPRICE_RANGE')];
				$scope.mappingSet['CBS.ORDERDATE'] = totas.data[totas.key.indexOf('OTH001')].filter(e => e.DATA === 'ORDER_DATE');
			}
		});
        
		$scope.getMaxGtcEndDate = function() {
			$scope.sendRecv("SOT310", "getMaxGtcEndDate", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO", {},
			function(tota, isError) {
				if (!isError) {
					// 指定交易起日限定為『次營業日』起的20個營業日內，AS400仍要檢核是否有遇到臺灣、美國或香港假日。
					$scope.inputVO.MIN_GTC_START_DATE = $scope.toJsDate(tota[0].body.minGtcStartDate);
					$scope.inputVO.MAX_GTC_START_DATE = $scope.toJsDate(tota[0].body.maxGtcStartDate);
					$scope.inputVO.MIN_GTC_END_DATE   = $scope.toJsDate(tota[0].body.minGtcEndDate);
					$scope.inputVO.MAX_GTC_END_DATE   = $scope.toJsDate(tota[0].body.maxGtcEndDate);
					
//					$scope.inputVO.MIN_GTC_START_DATE = tota[0].body.minGtcStartDate;
//					$scope.inputVO.MAX_GTC_DATE = tota[0].body.maxGtcEndDate;
//					alert($scope.inputVO.MIN_GTC_START_DATE);
//					alert($scope.inputVO.MAX_GTC_DATE);
					
					$scope.limitDate();
				}
			});
		};
		$scope.getMaxGtcEndDate();

		// date picker
//		$scope.apply_gtcEndDateOptions = {
//				maxDate: $scope.maxDate,
//				minDate: $scope.minDate
//		};

		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			var minStartDate = $scope.inputVO.MIN_GTC_START_DATE;
			var maxStartDate = $scope.inputVO.MAX_GTC_START_DATE;
			var minEndDate   = $scope.inputVO.MIN_GTC_END_DATE;
			var maxEndDate   = $scope.inputVO.MAX_GTC_END_DATE;
			
//			var endDate = undefined;
//			if ($scope.inputVO.gtcYN != 'P' && $scope.inputVO.gtcEndDate != undefined) {
//				endDate = angular.copy($scope.inputVO.gtcEndDate);
//				endDate = endDate.setDate(endDate.getDate()-1);					
//				endDate = new Date(endDate);
//				if (endDate > maxEndDate) {
//					endDate = maxEndDate;
//				}
//			}
			$scope.apply_gtcStartDateOptions.minDate = minStartDate;
			$scope.apply_gtcStartDateOptions.maxDate = maxStartDate;
			
			// 長效單起迄日邏輯：最短2日最長5日,要連續的日期區間
			if ($scope.inputVO.gtcYN == 'Y') {
				var startDate = undefined;
				if ($scope.inputVO.gtcStartDate != undefined) {
					startDate = angular.copy($scope.inputVO.gtcStartDate);
					startDate = startDate.setDate(startDate.getDate()+1);					
					startDate = new Date(startDate);
					if (startDate < minEndDate) {
						startDate = minEndDate;
					}
				}
				$scope.apply_gtcEndDateOptions.minDate = startDate || minEndDate;
				$scope.apply_gtcEndDateOptions.maxDate = maxEndDate;
				
			} else if ($scope.inputVO.gtcYN == 'P') {
				$scope.apply_gtcEndDateOptions.minDate = undefined;
				$scope.apply_gtcEndDateOptions.maxDate = undefined;
				// 選擇『預約單』則 enable 指定交易起日 及 disable 委託迄日，但委託迄日須等於指定交易起日。
				$scope.inputVO.gtcEndDate = $scope.inputVO.gtcStartDate;
				
			} else {
				$scope.apply_gtcEndDateOptions.minDate = minEndDate;
				$scope.apply_gtcEndDateOptions.maxDate = maxEndDate;
			}
			
			if ($scope.inputVO.gtcStartDate != undefined && $scope.inputVO.gtcEndDate != undefined) {
				if ($scope.inputVO.gtcStartDate > $scope.inputVO.gtcEndDate) {
					// 若起日大於迄日，則清空迄日
					$scope.inputVO.gtcEndDate = undefined;
				}
			}
		};
		
		/**
		 * 進入申購頁面，先檢核交易時間，若時間超過15:00 (含)，則提示訊息「已過當日交易截止時間，請改採長效單或預約單委託」
		 * **/
		$scope.checkTradeDateType = function(){
			var defer = $q.defer();
			$scope.sendRecv("SOT310", "checkTime", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO", {},
			function(tota, isError) {
				if (!isError) {
					$scope.sotYN = tota[0].body.sotYN;
//					alert($scope.sotYN);
					
					if ($scope.sotYN == 'N') {
						$("#gtcN").attr("disabled", true);
						$scope.inputVO.gtcYN = 'Y';
						$scope.showMsg("已過當日交易截止時間，僅可採長效單或預約單委託。");
					}
				}
				defer.resolve("success");					
			});
			return defer.promise;
		};

		$scope.init = function(){
			$scope.disGtcRefVal = false;
			$scope.checkTradeDateType();
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST']=[];
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY']=[];
			$scope.inputVO = {
					tradeSEQ: '',

					custID: '', 								//客戶ID
					custName: '',

					kycLV: '', 									//KYC等級
					kycDueDate: undefined, 						//KYC效期
					profInvestorYN: '', 						//是否為專業投資人
					piDueDate: undefined, 						//專業投資人效期
					piRemark: '',								//專業投資人註記
					custRemarks: '', 							//客戶註記
					isOBU: '', 									//是否為OBU客戶
					isAgreeProdAdv: '', 						//同意投資商品諮詢服務
					bargainDueDate: undefined,					//期間議價效期
					plNotifyWays: '',							//停損停利通知方式
					takeProfitPerc: undefined,					//停利點
					stopLossPerc: undefined,					//停損點
					debitAcct: '', 								//扣款帳號
					trustAcct: '', 								//信託帳號
					creditAcct: '',								//收益入帳帳號
					w8benEffDate: undefined,					//W8ben有效日期
					w8BenEffYN: '',
					fatcaType: '',
					custProType: '',


					custType: 'CUST', 							//來行人員
					agentID: '', 								//代理人ID
					agentName: '',

					narratorID: projInfoService.getUserID(),	//解說人員ID
					narratorName: projInfoService.getUserName(),//解說人員姓名

					prodID: '', 								//商品ID
					prodName: '',								//商品名稱
					prodCurr: '',								//計價幣別
					prodRiskLV: '',								//產品風險等級
					prodMinBuyAmt: undefined,					//最低申購面額
					prodMinGrdAmt: undefined,					//累計申購面額
					trustCurrType: 'Y',							//信託幣別類型
					trustCurr: '',								//信託幣別
					trustUnit: undefined,						//庫存張數
					marketType: '2',							//債券市場種類
					refVal: undefined,							//參考報價
					refValDate: undefined,						//參考報價日期
					purchaseAmt: undefined,						//申購金額/庫存金額
					entrustType: '',							//委託價格類型/贖回方式
					entrustAmt: undefined,						//委託價格/贖回價格
					trustAmt: undefined,						//信託本金
					defaultFeeRate: undefined,					//表定手續費率
					advFeeRate: undefined,						//事先申請手續費率
					bargainApplySEQ: '',						//議價編號
					feeRate: undefined,							//手續費率/信託管理費率
					fee: undefined,								//手續費金額/預估信託管理費
					feeDiscount: undefined,						//手續費折數
					payableFee: undefined,						//應付前手息/應收前手息
					totAmt: undefined,							//總扣款金額/預估贖回入帳金額
					debitAcct: '',								//扣款帳號
					creditAcct: '',								//收益入帳帳號/贖回款入帳帳號
					tradeDate: undefined,						//交易日期

					certificateID: '',							//憑證編號
					gtcYN: 'N',									//當日單N, 長效單Y
					gtcEndDate: undefined,						//長效單迄日
					gtcRefVal: undefined,						//長效單委託價格

					trustTS: 'S',								//S=特金/M=金錢信託
					goOPDisabled: false							//傳送OP按鈕，避免連續點擊
			};
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
		 $scope.setAcctDisplay = function(acctNameList){

			//查詢帳號幣別
		    //var acctCurrency = $scope.getAcctCurrency();
			 var acctCurrency = undefined;

			/** mimi
 			各交易 (除了SI) 贖回入帳帳號邏輯跟申購時的收益帳號邏輯相同 但無需考慮幣別
 			那就先顯示多筆, 沒關係, 也不用把幣別帶在後面
			**/
			for(var i=0;i<acctNameList.length;i++){
				if(acctCurrency){
					$scope.mappingSet[acctNameList[i]+'#DISPLAY'] = $filter('filter')($scope.mappingSet[acctNameList[i]], acctCurrency);
				}else{
					$scope.mappingSet[acctNameList[i]+'#DISPLAY'] = $scope.mappingSet[acctNameList[i]];
				}
			}
		}

        // SOT701-客戶電文
		$scope.getSOTCustInfo = function() {
			var deferred = $q.defer();

			if($scope.inputVO.custID) {
				$scope.sendRecv("SOT320", "getSOTCustInfo", "com.systex.jbranch.app.server.fps.sot320.SOT320InputVO", {'custID': $scope.inputVO.custID, 'prodType': 3, 'tradeType': 2},
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.custName == null || tota[0].body.custName == "" || tota[0].body.trustAcct.length == 0) { //若非為本行客戶或信託客戶，需有訊息告知非為本行客戶或未開立信託戶，並須擋下單。
									$scope.showErrorMsg("ehl_01_sot310_002");
									if ($scope.fromFPS) {
										// from FPS_SOT.js
										$scope.setSOTurl('assets/txn/SOT320/SOT320.html');
									} else {
										$rootScope.menuItemInfo.url = "assets/txn/SOT320/SOT320.html";
									}
								} else if (tota[0].body.noSale == "Y") { //若為禁銷客戶，出現提示訊息禁止下單。
									$scope.showErrorMsg("ehl_01_sot310_003");
									$scope.dtl = tota[0].body;//修改若為禁銷客戶狀況，可下單。 原邏輯為禁止下單。
									$scope.setDTL();
									deferred.resolve("success");
//									$rootScope.menuItemInfo.url = "assets/txn/SOT320/SOT320.html";
								} else if (tota[0].body.deathFlag == "Y" ) { //若為死亡戶/禁治產等狀況，不可下單。
									$scope.showErrorMsg("ehl_01_SOT_012");
									$scope.dtl = tota[0].body;//修改若為死亡戶狀況，可下單。   原邏輯為禁止下單。
									$scope.setDTL();
									deferred.resolve("success");
//									$rootScope.menuItemInfo.url = "assets/txn/SOT320/SOT320.html";
								} else {
									$scope.dtl = tota[0].body;
									if (tota[0].body.rejectProdFlag == "Y") {
										var txtMsg = $filter('i18n')('ehl_01_sot310_005');

										$confirm({text: txtMsg + "是否繼續"}, {size: 'sm'}).then(function() {
											$scope.setDTL();
											deferred.resolve("success");
											return deferred.promise;
							            });
									} else {
										$scope.setDTL();
									}
								}
								if($scope.inputVO.custID.length == 10)
									$scope.custAge = (tota[0].body.custAge <20 ? $filter('i18n')('ehl_01_sot510_001') : ""); //委託人未成年，請查詢印鑑備註訊息或法代同意書內容

							}
				});
			}

			deferred.resolve("");
			return deferred.promise;
		};

		//設定客戶資料
		$scope.setDTL = function () {
			$scope.inputVO.custName = $scope.dtl.custName;
			$scope.inputVO.kycLV = $scope.dtl.kycLV;									//KYC等級
			$scope.inputVO.kycDueDate = $scope.toJsDate($scope.dtl.kycDueDate);			//KYC效期
			$scope.inputVO.profInvestorYN = $scope.dtl.profInvestorYN;					//是否為專業投資人
			$scope.inputVO.piDueDate = $scope.toJsDate($scope.dtl.piDueDate);			//專業投資人效期
			$scope.inputVO.piRemark = $scope.dtl.piRemark;								//專業投資人註記
			$scope.inputVO.custRemarks = $scope.dtl.custRemarks;						//客戶註記
			$scope.inputVO.isOBU = $scope.dtl.isOBU;									//是否為OBU客戶
			$scope.inputVO.isAgreeProdAdv = $scope.dtl.isAgreeProdAdv;					//同意投資商品諮詢服務
			$scope.inputVO.bargainDueDate =  $scope.dtl.bargainDueDate;					//期間議價效期
			$scope.inputVO.plNotifyWays = $scope.dtl.plNotifyWays;						//停損停利通知方式
			$scope.inputVO.takeProfitPerc = $scope.dtl.takeProfitPerc;					//停利點
			$scope.inputVO.stopLossPerc = $scope.dtl.stopLossPerc;						//停損點
			$scope.inputVO.w8benEffDate = $scope.toJsDate($scope.dtl.w8benEffDate);		//W8ben有效日期
			$scope.inputVO.w8BenEffYN = $scope.dtl.w8BenEffYN;
			$scope.inputVO.fatcaType = $scope.dtl.fatcaType;
			$scope.inputVO.custProType = $scope.dtl.custProType;						//專投種類

			if ($scope.inputVO.trustTS == 'S') {
	//			$scope.mappingSet['SOT.TRUST_ACCT_LIST'] = $scope.dtl.trustAcct;
				$scope.mappingSet['SOT.DEBIT_ACCT_LIST'] = $scope.dtl.debitAcct;
				$scope.mappingSet['SOT.CREDIT_ACCT_LIST'] = $scope.dtl.creditAcct;
				$scope.inputVO.debitAcct = ($scope.dtl.debitAcct.length > 0 ? $scope.dtl.debitAcct[0].LABEL : "");				//扣款帳號
	//			$scope.inputVO.trustAcct = ($scope.dtl.trustAcct.length > 0 ? $scope.dtl.trustAcct[0].LABEL : "");				//信託帳號
				$scope.inputVO.creditAcct = ($scope.dtl.creditAcct.length > 0 ? $scope.dtl.creditAcct[0].LABEL : "");			//收益入帳帳號
			} else {
				$scope.inputVO.contractID = $scope.connector('get', 'SOTContractID');
				$scope.inputVO.debitAcct = $scope.connector('get', 'SOTDebitAcct');
				$scope.inputVO.creditAcct = $scope.connector('get', 'SOTDebitAcct');
			}

			$scope.setAcctDisplay(["SOT.CREDIT_ACCT_LIST"]);
			$scope.checkTrustAcct();
		};

		//檢查信託帳號、贖回款入帳帳號之判斷
		$scope.checkTrustAcct = function(){
			if ($scope.inputVO.trustTS == 'S') {
			$scope.cmbCreditAcct=false;
			var creditAcctListDisplay='SOT.CREDIT_ACCT_LIST#DISPLAY';

			//信託帳號檢核
			if ($scope.inputVO.trustAcct && sotService.is168($scope.inputVO.trustAcct)) {
				$scope.mappingSet[creditAcctListDisplay] = $filter('filter')($scope.mappingSet[creditAcctListDisplay],
																		{DATA: $scope.inputVO.trustAcct},
																		function(actual, expected) { return angular.equals(actual, expected)}
																	   );
			} else {
				//有傳信託業務別
				if ($scope.inputVO.trustCurrType) {
		 		   if($scope.inputVO.trustCurrType=='N' || $scope.inputVO.trustCurrType=='C'){ //N台幣
		 			 $scope.mappingSet[creditAcctListDisplay] = $filter('filter')($scope.mappingSet[creditAcctListDisplay], {CURRENCY: 'TWD'});
		 		   } else if ($scope.inputVO.trustCurrType=='Y'){  //Y外幣
		 			  $scope.mappingSet[creditAcctListDisplay] = $filter('filter')($scope.mappingSet[creditAcctListDisplay],{CURRENCY: "TWD"},function(actual, expected) {  return !angular.equals(actual, expected)});
		 		   }
				}
			}

			if($scope.mappingSet[creditAcctListDisplay].length==1){ //只有一筆不能勾選
				$scope.inputVO.creditAcct=$scope.mappingSet[creditAcctListDisplay][0].DATA;
				$scope.cmbCreditAcct=true;
				}
			}
	    }


		//查詢購物車資料
		$scope.noCallCustQuery = function () {
			var deferred = $q.defer();

			$scope.sendRecv("SOT310", "query", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.cartList = tota[0].body.carList;
							$scope.outputVO = tota[0].body;

							$scope.inputVO.narratorID = tota[0].body.carList[0].NARRATOR_ID;					//解說人員ID
							$scope.inputVO.narratorName = tota[0].body.carList[0].NARRATOR_NAME;				//解說人員姓名
							$scope.narratorDisabled = true;

							$scope.inputVO.custID = tota[0].body.mainList[0].CUST_ID;							//客戶ID
							$scope.inputVO.custName = tota[0].body.mainList[0].CUST_NAME;

							$scope.inputVO.kycLV = tota[0].body.mainList[0].KYC_LV;	 							//KYC等級
							$scope.inputVO.kycDueDate = $scope.toJsDate(tota[0].body.mainList[0].KYC_DUE_DATE);	//KYC效期
							$scope.inputVO.profInvestorYN = tota[0].body.mainList[0].PROF_INVESTOR_YN;	 		//是否為專業投資人
							$scope.inputVO.piDueDate = $scope.toJsDate(tota[0].body.mainList[0].PI_DUE_DATE);	//專業投資人效期
							$scope.inputVO.piRemark = tota[0].body.mainList[0].PI_REMARK;						//專業投資人效期
							$scope.inputVO.custRemarks = tota[0].body.mainList[0].CUST_REMARKS;	 				//客戶註記
							$scope.inputVO.isOBU = tota[0].body.mainList[0].IS_OBU;	 							//是否為OBU客戶
							$scope.inputVO.isAgreeProdAdv = tota[0].body.mainList[0].IS_AGREE_PROD_ADV;	 		//同意投資商品諮詢服務
							$scope.inputVO.bargainDueDate = $scope.toJsDate(tota[0].body.mainList[0].BARGAIN_DUE_DATE);	//期間議價效期
							$scope.inputVO.bargainFeeFlag = tota[0].body.mainList[0].BARGAIN_FEE_FLAG;			//議價狀態

							$scope.inputVO.isBargainNeeded = tota[0].body.mainList[0].IS_BARGAIN_NEEDED;		//是否需要議價
							$scope.inputVO.tradeStatus = tota[0].body.mainList[0].TRADE_STATUS;					//交易狀態

							$scope.inputVO.prodID = tota[0].body.carList[0].PROD_ID;
							$scope.inputVO.prodName = tota[0].body.carList[0].PROD_NAME;						//商品名稱
				        	$scope.inputVO.prodCurr = tota[0].body.carList[0].PROD_CURR; 						//計價幣別
				        	$scope.inputVO.prodRiskLV = tota[0].body.carList[0].PROD_RISK_LV; 					//產品風險等級

							$scope.inputVO.trustCurrType = tota[0].body.carList[0].TRUST_CURR_TYPE;				//信託幣別類型
							$scope.inputVO.trustCurr = tota[0].body.carList[0].TRUST_CURR;						//信託幣別
							$scope.inputVO.trustUnit = tota[0].body.carList[0].TRUST_UNIT;						//庫存張數
							$scope.inputVO.marketType = tota[0].body.carList[0].MARKET_TYPE;					//債券市場種類
							$scope.inputVO.refVal = tota[0].body.carList[0].REF_VAL;							//參考報價
							$scope.inputVO.refValDate = $scope.toJsDate(tota[0].body.carList[0].REF_VAL_DATE);	//參考報價日期
							$scope.inputVO.purchaseAmt = tota[0].body.carList[0].PURCHASE_AMT;					//申購金額/庫存金額
							$scope.inputVO.entrustType = tota[0].body.carList[0].ENTRUST_TYPE;					//委託價格類型/贖回方式
							$scope.inputVO.entrustAmt = tota[0].body.carList[0].ENTRUST_AMT;					//委託價格/贖回價格
							$scope.inputVO.trustAmt = tota[0].body.carList[0].TRUST_AMT;						//信託本金
							$scope.inputVO.bargainApplySEQ = tota[0].body.carList[0].BARGAIN_APPLY_SEQ;			//議價編號
							$scope.inputVO.feeRate = tota[0].body.carList[0].FEE_RATE;							//手續費率/信託管理費率
							$scope.inputVO.fee = tota[0].body.carList[0].FEE;									//手續費金額/預估信託管理費
							$scope.inputVO.payableFee = tota[0].body.carList[0].PAYABLE_FEE;					//應付前手息/應收前手息
							$scope.inputVO.totAmt = tota[0].body.carList[0].TOT_AMT;							//總扣款金額/預估贖回入帳金額
							$scope.inputVO.tradeDate = $scope.toJsDate(tota[0].body.carList[0].TRADE_DATE);		//交易日期

							$scope.inputVO.trustAcct = tota[0].body.carList[0].TRUST_ACCT;						//信託帳號
							$scope.inputVO.creditAcct = tota[0].body.carList[0].CREDIT_ACCT;					//收益入帳帳號

							$scope.inputVO.gtcYN = tota[0].body.carList[0].GTC_YN;
							$scope.inputVO.gtcStartDate = $scope.toJsDate(tota[0].body.carList[0].GTC_START_DATE);
							$scope.inputVO.gtcEndDate = $scope.toJsDate(tota[0].body.carList[0].GTC_END_DATE);

							if($scope.inputVO.gtcYN == "Y") {
								$scope.inputVO.gtcRefVal = tota[0].body.carList[0].ENTRUST_AMT;					//長效單委託價格/贖回價格
							}

							//$scope.inputVO.creditAcct = $scope.inputVO.creditAcct+"_"+$scope.getAcctCurrency();
//							$scope.setAcctDisplay(["SOT.CREDIT_ACCT_LIST"]);
							deferred.resolve("success");
						}
			});

			return deferred.promise;
		};

		$scope.getProdDTL = function () {
			var deferred = $q.defer();

			if($scope.inputVO.prodID) {
				$scope.sendRecv("SOT320", "getProdDTL", "com.systex.jbranch.app.server.fps.sot320.SOT320InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.prodDTL.length > 0) {
						        	//$scope.inputVO.prodCurr = tota[0].body.prodDTL[0].CURRENCY_STD_ID;	//計價幣別
									$scope.inputVO.prodRiskLV = tota[0].body.prodDTL[0].RISKCATE_ID;	//產品風險等級

									deferred.resolve("success");
									return deferred.promise;
								} else {
									var txtMsg = $scope.inputVO.prodID + $filter('i18n')('ehl_01_common_009');
									$scope.showErrorMsg(txtMsg);
									//$rootScope.menuItemInfo.url = "assets/txn/SOT320/SOT320.html";
								}
							}
				});
			} else {
				$scope.showErrorMsg("ehl_01_common_009");
				if ($scope.fromFPS) {
					// from FPS_SOT.js
					$scope.setSOTurl('assets/txn/SOT320/SOT320.html');
				} else {
					$rootScope.menuItemInfo.url = "assets/txn/SOT320/SOT320.html";
				}
			}

			deferred.resolve("");
			return deferred.promise;
		};

		$scope.calculate = function () {
			// 選限價1%、3%、5%時，下面的『委託贖回限價價格』欄位就要反灰不能填寫
			if ($scope.inputVO.entrustType == "4" || $scope.inputVO.entrustType == "5" || $scope.inputVO.entrustType == "6") {
				$scope.disGtcRefVal = true;
				$scope.inputVO.gtcRefVal = undefined;
			} else {
				$scope.disGtcRefVal = false;
			}
			
			var refVal = undefined;
			if ($scope.inputVO.entrustType == "4") {
				refVal = $scope.inputVO.refVal - 1;
			} else if ($scope.inputVO.entrustType == "5") {
				refVal = $scope.inputVO.refVal - 3;
			} else if ($scope.inputVO.entrustType == "6") {
				refVal = $scope.inputVO.refVal - 5;
			} else {
				refVal = $scope.inputVO.refVal;
			}

			$scope.inputVO.entrustAmt = refVal;
			$scope.inputVO.payableFee = $scope.prodDTL.PayableFee;//應收前手息
			var endDate = new Date();
			var year = endDate.getFullYear();
			var month = endDate.getMonth();
			var day = endDate.getDate();
			endDate = new Date(year,month,day);
			$scope.inputVO.fee = ((Math.round((endDate - $scope.toJsDate($scope.prodDTL.ApplyDate)) / (1000 * 60 * 60 * 24)) + 1) / 365) * ($scope.inputVO.feeRate / 100) * $scope.inputVO.trustAmt;
			$scope.inputVO.totAmt = ($scope.inputVO.purchaseAmt * ($scope.inputVO.refVal/ 100)) + $scope.inputVO.payableFee - $scope.inputVO.fee;

//			$scope.inputVO.totAmtCal ="(" + $scope.inputVO.purchaseAmt +" * (" + $scope.inputVO.refVal +"/ 100 ) ) + " + $scope.inputVO.payableFee +" - " + $scope.inputVO.fee;

		}

		$scope.gtcYN_Changed = function() {
			if($scope.inputVO.gtcYN == "N") {
				$scope.inputVO.gtcRefVal = undefined;
			} else {
				$scope.inputVO.gtcRefVal = $scope.inputVO.refVal;
			}
			
			if ($scope.inputVO.gtcYN == "N") {
				// 當日單
				$scope.inputVO.gtcStartDate = undefined;
				$scope.inputVO.gtcEndDate = undefined;	
				
			} else if ($scope.inputVO.gtcYN == "Y") {
				// 長效單
				$scope.inputVO.gtcEndDate = undefined;
				
			} else if ($scope.inputVO.gtcYN == "P") {
				// 預約單
				$scope.inputVO.gtcEndDate = $scope.inputVO.gtcStartDate;
			}
			$scope.limitDate();

			$scope.inputVO.entrustType = "2";
			$scope.calculate();
		}

		$scope.chkGtcRefVal = function() {
			if($scope.inputVO.gtcYN == "Y") {
				//參數取得"限價超過參考報價的正負N"
				var rangelist = $filter('filter')($scope.mappingSet["SOT.BN_GTC_LIMITPRICE_RANGE"], "1");
				var range = 3;
				//若無參數預設為3
				if(range != undefined && range != null) range = rangelist[0].LABEL;

				if(Math.abs($scope.inputVO.refVal - $scope.inputVO.gtcRefVal) > range ) {
					var txtMsg = $filter('i18n')('ehl_02_SOT_013');	//限價超過參考報價的正負3，請確認是否繼續下單。
					$scope.showWarningMsg(txtMsg, range);
				}
			}
		}

        // if data
        $scope.prodDTL = $scope.connector('get', 'SOT321_prodDTL');
        $scope.custID = $scope.connector('get', 'SOT321_custID');
		$scope.inputVO.tradeSEQ = $scope.connector('get', 'SOTTradeSEQ');
		$scope.inputVO.carSEQ = $scope.connector('get', 'SOTCarSEQ');

		$scope.sendRecv("SOT712", "getTradeSEQ", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.inputVO.tradeSEQ = tota[0].body.tradeSEQ;
						return;
					}
		});

		//設定商品資料
		$scope.setProd = function () {
			$scope.init();

			if ($scope.prodDTL && $scope.custID) {
				$scope.inputVO.custID = $scope.custID;
				$scope.getSOTCustInfo().then(function(data) {
					$scope.inputVO.prodID = $scope.prodDTL.BondNo;
					$scope.getProdDTL().then(function(data) {
						$scope.inputVO.prodCurr = $scope.prodDTL.CurCode;
						$scope.inputVO.trustCurrType = $scope.prodDTL.TrustType;					//信託業務別
						$scope.inputVO.marketType = $scope.prodDTL.BondType;						//債券市場種類
						$scope.inputVO.prodName = $scope.prodDTL.BondName;							//商品名稱
						$scope.inputVO.purchaseAmt = $scope.prodDTL.TrustVal;						//庫存面額
						$scope.inputVO.refVal = $scope.prodDTL.RefPrice;							//參考報價
						$scope.inputVO.refValDate = $scope.toJsDate($scope.prodDTL.RefPriceDate);	//參考報價(日期)
						$scope.inputVO.feeRate = $scope.prodDTL.TrustFeeRate * 100;
						$scope.inputVO.bondVal = $scope.prodDTL.BondVal;							//票面價值
						$scope.inputVO.trustAmt = $scope.prodDTL.TrustAmt;							//信託本金
						$scope.inputVO.certificateID = $scope.prodDTL.TrustNo;
						$scope.inputVO.trustUnit = $scope.prodDTL.Unit;
						$scope.inputVO.trustAcct = $scope.prodDTL.TrustAcct.trim();
//						$scope.setAcctDisplay(["SOT.CREDIT_ACCT_LIST"]);
						$scope.calculate();
					});
				});
			}
		}
		$scope.setProd();

		$scope.query = function() {
			if($scope.inputVO.tradeSEQ) {
				$scope.noCallCustQuery();
			} else {
				if ($scope.fromFPS) {
					// from FPS_SOT.js
					$scope.setSOTurl('assets/txn/SOT320/SOT320.html');
				} else {
					$rootScope.menuItemInfo.url = "assets/txn/SOT320/SOT320.html";
				}
			}
		};

		//傳送OP交易，避免按鈕被連續點擊造成錯誤，先disable按鈕並$timeout
		$scope.goOP = function () {
			$scope.inputVO.goOPDisabled = true;

			$timeout(function() {
				$scope.check_goOP();
				$scope.inputVO.goOPDisabled = false;}
			, 1000);
		}

		$scope.check_goOP = function () {
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg("ehl_01_common_022");
	    		return;
	    	}

			//若海外債申購及贖回交易頁面上報價日期不等於today，按"傳送OP及列印表單"時，出訊息踢退交易 (#5442)
			//報價日期
			var refValYear = $scope.inputVO.refValDate.getFullYear();
			var refValMonth = $scope.inputVO.refValDate.getMonth();
			var refValDate = $scope.inputVO.refValDate.getDate();
			//系統日期
			var CBSTESTDATE = $scope.mappingSet['CBS.ORDERDATE'][0]["LABEL"];
			debugger;
			if(CBSTESTDATE.length == 8){
				debugger;
				var year = CBSTESTDATE.substring(0,4);
				var month = CBSTESTDATE.substring(4,6);
				if(month.substr(0,1) == "0"){
					month = month.substr(1,2);
					month = parseInt(month) - 1;
				}
				var date = CBSTESTDATE.substring(6,8);
				if(date.substr(0,1) == "0"){
					date = date.substr(1,2);
				}
			} else {
				debugger;
				var today = new Date;
				var year = today.getFullYear();
				var month = today.getMonth();
				var date = today.getDate();
			}
			
			//若海外債申購及贖回交易頁面上報價日期不等於today，按"下一步"時，清空商品代號，切請出訊息: 報價日期須為今日。
//			if($scope.inputVO.gtcYN != "Y" && (refValYear != year || refValMonth != month || refValDate != date)){
//				$scope.showErrorMsg("報價日期須為今日。");
//	    		return;
//			}

			if($scope.inputVO.gtcYN == "" || $scope.inputVO.gtcYN == null || $scope.inputVO.gtcYN == undefined) {
				$scope.showErrorMsg("必須輸入委託方式");
				return;
			}
			
			if ($scope.inputVO.gtcYN == "Y") {
				// 長效單必須輸入委託起日＆委託迄日
				if ($scope.inputVO.gtcStartDate == "" || $scope.inputVO.gtcStartDate == undefined || $scope.inputVO.gtcStartDate == null ||
					$scope.inputVO.gtcEndDate == "" || $scope.inputVO.gtcEndDate == undefined || $scope.inputVO.gtcEndDate == null) {
					$scope.showErrorMsg("請選擇長效單日期");
					return;
				}
			} else if ($scope.inputVO.gtcYN == "P") {
				// 預約單必須輸入委託起日
				if ($scope.inputVO.gtcStartDate == "" || $scope.inputVO.gtcStartDate == undefined || $scope.inputVO.gtcStartDate == null) {
					$scope.showErrorMsg("請選擇預約單日期");
					return;
				}
			}
			
			// 長效單：未輸入限價價格，顯示錯誤訊息「請輸入參考委託贖回價格」(長效單只有限價，無市價選項) 
			if ($scope.inputVO.gtcYN == "Y" && ($scope.inputVO.gtcRefVal == undefined || $scope.inputVO.gtcRefVal == '') ) {
				$scope.showErrorMsg("請輸入參考委託贖回價格");
				return;
			}
			
			/**
			 * 檢查：
			 * 1. 長效單、預約單：委託日期不可為 臺灣、香港、美國之假日(電文_NJWEEA70)
			 * 2. 長效單：最短2個營業日、最長5個營業日（需排除美國、香港、台灣休假日）
			 * **/
			if ($scope.inputVO.gtcYN == "Y" || $scope.inputVO.gtcYN == "P") {
				$scope.sendRecv("SOT310", "checkGtcDate", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO", 
				{'gtcYN':$scope.inputVO.gtcYN, 'gtcStartDate':$scope.inputVO.gtcStartDate, 'gtcEndDate':$scope.inputVO.gtcEndDate},
				function(tota, isError) {
					if (!isError) {
						var sflag = tota[0].body.resultList[0].START_DATE_YN;
						var eflag = tota[0].body.resultList[0].END_DATE_YN;
						var dates = tota[0].body.resultList[0].COUNT_DATES;
						if ($scope.inputVO.gtcYN == "Y") {
							if (sflag == "N") {
								$scope.showErrorMsg("長效單『指定交易起日』不可為臺灣、香港、美國之假日，請重先選擇。");
								return;
							}
							if (eflag == "N") {
								$scope.showErrorMsg("長效單『指定交易迄日』不可為臺灣、香港、美國之假日，請重先選擇。");
								return;
							}
							if (dates < 2 || dates > 5) {
								$scope.showErrorMsg("長效單『指定交易起迄日』最短需2個營業日、最長為5個營業日（排除美國、香港、台灣休假日）");
								return;
							}
						} else if ($scope.inputVO.gtcYN == "P") {
							if (sflag == "N") {
								$scope.showErrorMsg("預約單『指定交易起日』不可為臺灣、香港、美國之假日，請重先選擇。");
								return;
							}
						}
						$scope.do_goOP();
					}
				});
			} else {
				// 當日單
				$scope.sendRecv("SOT310", "checkTime", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.sotYN = tota[0].body.sotYN;
//						alert($scope.sotYN);
						if ($scope.sotYN == 'N' && $scope.inputVO.gtcYN == 'N') {
							$scope.showMsg("已過當日交易截止時間，請改採長效單或預約單委託。");
							return;
						}
						$scope.do_goOP();
					}				
				});
			}
		};
		
		$scope.do_goOP = function () {
			$scope.sendRecv("SOT320", "goOP", "com.systex.jbranch.app.server.fps.sot320.SOT320InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					if (tota[0].body.errorMsg != '' && tota[0].body.errorMsg != null) {
						$scope.showErrorMsg(tota[0].body.errorMsg);
					} else {
						if(tota[0].body.warningCode){
							for(var i=0;i<tota[0].body.warningCode.length;i++){
								var warningCode = tota[0].body.warningCode[i];
								var dialog = ngDialog.open({
									template: 'assets/txn/CONFIRM/CONFIRM.html',
									className: 'CONFIRM',
									showClose: false,
									scope : $scope,
									controller: ['$scope', function($scope) {
										$scope.dialogLabel = $filter('i18n')(warningCode).replace("{0}", $scope.inputVO.trustAcct);
						            }]
								}).closePromise.then(function (data) {
									if (data.value === 'successful') {
										
									} else {
										deferred.resolve("");
									}
								});
							}
						}
						$scope.printReport();  //產生報表
						$scope.showMsg("ehl_02_SOT_002");
						$scope.query();
						return;
					}
				}
			});
		}

		$scope.newTrade = function () {
			if ($scope.inputVO.trustTS == 'S') {
				if ($scope.fromFPS) {
					// from FPS_SOT.js
					$scope.setSOTurl('assets/txn/SOT320/SOT320.html');
				} else {
					$rootScope.menuItemInfo.url = "assets/txn/SOT320/SOT320.html";
				}
			} else {
				if ($scope.fromFPS) {
					// from FPS_SOT.js
					$scope.setSOTurl('assets/txn/SOT325/SOT325.html');
				} else {
					$rootScope.menuItemInfo.url = "assets/txn/SOT325/SOT325.html";
				}
			}

		}

		//取得解說專員姓名
		$scope.getTellerName = function(type,name){
			if(name) {
				$scope.sendRecv("SOT712", "getTellerName", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {"tellerID":name},
						function(tota, isError) {
							if (!isError) {
								if(type=='narratorID'){
									$scope.inputVO.narratorName=tota[0].body.EMP_NAME;
								}else if(type=='bossID'){
									$scope.inputVO.bossName=tota[0].body.EMP_NAME;
								}
							}else{
								if(type=='narratorID'){
									$scope.inputVO.narratorName='';
									$scope.inputVO.narratorID='';
								}else if(type=='bossID'){
									$scope.inputVO.bossName='';
									$scope.inputVO.bossID='';
								}
							}
							return;
				});
			}else{
				if(type=='narratorID'){
					$scope.inputVO.narratorName='';
					$scope.inputVO.narratorID='';
				}else if(type=='bossID'){
					$scope.inputVO.bossName='';
					$scope.inputVO.bossID='';
				}
			}
		};

		//產生報表
		$scope.printReport = function(){
			var fitVO = {
				caseCode : 		1, 							//case1 下單
				custId   :		$scope.inputVO.custID,		//客戶ID
				prdType  :      3,							//商品類別 : 海外債
				tradeSeq : 		$scope.inputVO.tradeSEQ, 	//交易序號
				tradeSubType:	2							//交易類型 : 贖回
			}

			$scope.sendRecv("SOT712", "fitToGetPDF", "com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO", fitVO,
					function(tota, isError) {
				if (isError) {
					$scope.showErrorMsg(totas[0].body.msgData);
				}
			});
		};
});
