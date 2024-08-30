/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT311Controller',
	function($rootScope, $scope, $controller, $confirm, sysInfoService, socketService, ngDialog, projInfoService, $filter, getParameter, $q, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT311Controller";
		
		// filter
		getParameter.XML(["SOT.CUST_TYPE", "SOT.TRUST_CURR_TYPE", "SOT.MARKET_TYPE", "SOT.ENTRUST_TYPE_PURCHASE","SOT.SPEC_CUSTOMER"], function(totas) {
			if (totas) {
				$scope.mappingSet['SOT.CUST_TYPE'] = totas.data[totas.key.indexOf('SOT.CUST_TYPE')];
				$scope.mappingSet['SOT.TRUST_CURR_TYPE'] = totas.data[totas.key.indexOf('SOT.TRUST_CURR_TYPE')];
				$scope.mappingSet['SOT.MARKET_TYPE'] = totas.data[totas.key.indexOf('SOT.MARKET_TYPE')];
				$scope.mappingSet['SOT.ENTRUST_TYPE_PURCHASE'] = totas.data[totas.key.indexOf('SOT.ENTRUST_TYPE_PURCHASE')];
		        $scope.mappingSet['SOT.SPEC_CUSTOMER'] = totas.data[totas.key.indexOf('SOT.SPEC_CUSTOMER')];//客戶註記
				
			}
		});
        //
		
		$scope.init = function() {
			$scope.inputVO = {
					tradeSEQ: '',
					
					custID: '', 								//客戶ID
					custName: '', 
					
					kycLV: '', 									//KYC等級
					kycDueDate: undefined, 						//KYC效期
					profInvestorYN: '', 						//是否為專業投資人
					piDueDate: undefined, 						//專業投資人效期
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
					trustCurrType: '',							//信託幣別類型
					trustCurr: '',								//信託幣別
					trustUnit: undefined,						//庫存張數
					marketType: '',								//債券市場種類
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
					trustAcct: '',								//信託帳號
					creditAcct: '',								//收益入帳帳號/贖回款入帳帳號
					tradeDate: undefined,						//交易日期
					limitedPrice: undefined, 
					bestFeeRate: undefined,						//最優手續費率
					
					tradeStatus: '', 
					isBargainNeeded: '',
					
					warningMsg: '',
					
					gtcYN: 'N',									//當日單N, 長效單Y
					gtcEndDate: undefined,						//長效單迄日
					goOPDisabled: false,						//傳送OP按鈕，避免連續點擊
					goBankDisabled: false,						//網銀快速下單按鈕，避免連續點擊
					isWeb: 'N',									//臨櫃交易N, 快速申購Y
					batchSeq: '',								//批號
					webWarningMsg:'',							//快速申購成功後警語
					overCentRateYN: '',							//超過集中度上限
					loanFlag: 'N'                               //90天貸款註記(Y/N)
			};
		};
		$scope.init();
		
		// if data
		$scope.inputVO.tradeSEQ = $scope.connector('get', 'SOTTradeSEQ');
		$scope.inputVO.carSEQ = $scope.connector('get', 'SOTCarSEQ');
		$scope.inputVO.warningMsg = $scope.connector('get', 'SOT311_warningMsg');
		$scope.connector('set', 'SOTTradeSEQ', null);
		$scope.connector('set', 'SOTCarSEQ', null);
		$scope.connector('set', 'SOT311_warningMsg', null);
		
		$scope.noCallCustQuery = function () {
			var deferred = $q.defer();
			
			$scope.sendRecv("SOT310", "query", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.cartList = tota[0].body.carList;
							$scope.outputVO = tota[0].body;
							
							$scope.inputVO.narratorID = tota[0].body.carList[0].NARRATOR_ID;							//解說人員ID
							$scope.inputVO.narratorName = tota[0].body.carList[0].NARRATOR_NAME;						//解說人員姓名
							$scope.narratorDisabled = true;
							
							$scope.inputVO.custID = tota[0].body.mainList[0].CUST_ID;									//客戶ID
							$scope.inputVO.custName = tota[0].body.mainList[0].CUST_NAME;	 

							$scope.inputVO.kycLV = tota[0].body.mainList[0].KYC_LV;	 									//KYC等級
							$scope.inputVO.kycDueDate = $scope.toJsDate(tota[0].body.mainList[0].KYC_DUE_DATE);	 		//KYC效期
							$scope.inputVO.profInvestorYN = tota[0].body.mainList[0].PROF_INVESTOR_YN;	 				//是否為專業投資人
							$scope.inputVO.piDueDate = $scope.toJsDate(tota[0].body.mainList[0].PI_DUE_DATE);	 		//專業投資人效期
							$scope.inputVO.piRemark = tota[0].body.mainList[0].PI_REMARK;	 							//專業投資人註記
							$scope.inputVO.custRemarks = tota[0].body.mainList[0].CUST_REMARKS;	 						//客戶註記
							$scope.inputVO.isOBU = tota[0].body.mainList[0].IS_OBU;	 									//是否為OBU客戶
							$scope.inputVO.isAgreeProdAdv = tota[0].body.mainList[0].IS_AGREE_PROD_ADV;	 				//同意投資商品諮詢服務
							$scope.inputVO.bargainDueDate = $scope.toJsDate(tota[0].body.mainList[0].BARGAIN_DUE_DATE);	//期間議價效期
							$scope.inputVO.bargainFeeFlag = tota[0].body.mainList[0].BARGAIN_FEE_FLAG;					//議價狀態
							
							$scope.inputVO.isBargainNeeded = tota[0].body.mainList[0].IS_BARGAIN_NEEDED;				//是否需要議價
							$scope.inputVO.tradeStatus = tota[0].body.mainList[0].TRADE_STATUS;							//交易狀態
							$scope.isRecNeeded = tota[0].body.mainList[0].IS_REC_NEEDED;								//是否需要錄音
							$scope.inputVO.isWeb = tota[0].body.mainList[0].IS_WEB;										//是否為網銀快速申購(Y/N)

							$scope.inputVO.prodID = tota[0].body.carList[0].PROD_ID;
							$scope.inputVO.prodName = tota[0].body.carList[0].PROD_NAME;						//商品名稱
				        	$scope.inputVO.prodCurr = tota[0].body.carList[0].PROD_CURR; 						//計價幣別
				        	$scope.inputVO.prodRiskLV = tota[0].body.carList[0].PROD_RISK_LV; 					//產品風險等級
				        	
				        	$scope.inputVO.prodMinBuyAmt = tota[0].body.carList[0].PROD_MIN_BUY_AMT;			//最低申購面額
							$scope.inputVO.prodMinGrdAmt = tota[0].body.carList[0].PROD_MIN_GRD_AMT;			//累計申購面額
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
							$scope.inputVO.defaultFeeRate = tota[0].body.carList[0].DEFAULT_FEE_RATE;			//表定手續費率
							$scope.inputVO.advFeeRate = tota[0].body.carList[0].ADV_FEE_RATE;					//事先申請手續費率
							$scope.inputVO.bargainApplySEQ = tota[0].body.carList[0].BARGAIN_APPLY_SEQ;			//議價編號
							$scope.inputVO.feeRate = tota[0].body.carList[0].FEE_RATE;							//手續費率/信託管理費率
							$scope.inputVO.fee = tota[0].body.carList[0].FEE;									//手續費金額/預估信託管理費
							$scope.inputVO.feeDiscount = tota[0].body.carList[0].FEE_DISCOUNT;					//手續費折數
							$scope.inputVO.payableFee = tota[0].body.carList[0].PAYABLE_FEE;					//應付前手息/應收前手息
							$scope.inputVO.totAmt = tota[0].body.carList[0].TOT_AMT;							//總扣款金額/預估贖回入帳金額
							$scope.inputVO.tradeDate = $scope.toJsDate(tota[0].body.carList[0].TRADE_DATE);		//交易日期
							
							$scope.inputVO.debitAcct = tota[0].body.carList[0].DEBIT_ACCT;						//扣款帳號
							$scope.inputVO.trustAcct = tota[0].body.carList[0].TRUST_ACCT;						//信託帳號
							$scope.inputVO.creditAcct = tota[0].body.carList[0].CREDIT_ACCT;					//收益入帳帳號
							
							$scope.inputVO.gtcYN = tota[0].body.carList[0].GTC_YN;								//當日單N, 長效單Y
							$scope.inputVO.gtcEndDate = $scope.toJsDate(tota[0].body.carList[0].GTC_END_DATE);	//長效單迄日
							
							$scope.inputVO.batchSeq = tota[0].body.carList[0].BATCH_SEQ; 						//批號
							
							// 金錢信託
							$scope.inputVO.trustTS = tota[0].body.mainList[0].TRUST_TRADE_TYPE;					//信託交易類別
							$scope.inputVO.contractID = tota[0].body.carList[0].CONTRACT_ID;					//契約編號
							$scope.inputVO.trustPeopNum = tota[0].body.carList[0].TRUST_PEOP_NUM;				//是否為多委託人契約 Y=是/N=否
							
							$scope.inputVO.overCentRateYN = tota[0].body.carList[0].OVER_CENTRATE_YN;			//超過集中度上限
							
							$scope.inputVO.loanFlag = tota[0].body.mainList[0].FLAG_NUMBER;				        //90天貸款註記(Y/N)
							
							$scope.mainList = tota[0].body.mainList;
							
							deferred.resolve("success");
						}
			});
			
			return deferred.promise;
		};
		
		$scope.getSOTCustInfoCT = function () {
			$scope.sendRecv("SOT310", "getSOTCustInfoCT", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO", {'custID':$scope.inputVO.custID, 
																													 'prodType': 3, 
																													 'tradeType': 1, 
																													 'prodID': $scope.inputVO.prodID, 
																													 'custRemarks': $scope.inputVO.custRemarks},
					function(totaCT, isError) {
						if (!isError) {
							$scope.isFirstTrade = (totaCT[0].body.isFirstTrade == "Y" ? $filter('i18n')('ehl_02_SOT_003') : ""); 
							$scope.ageUnder70Flag = (totaCT[0].body.ageUnder70Flag == "N" ? $filter('i18n')('ehl_02_SOT_004') : "");
							$scope.eduJrFlag = (totaCT[0].body.eduJrFlag == "N" ? $filter('i18n')('ehl_02_SOT_005') : ""); ;
							$scope.healthFlag = (totaCT[0].body.healthFlag == "N" ? $filter('i18n')('ehl_02_SOT_006') : ""); 
							
							if ($scope.mainList[0].IS_REC_NEEDED == 'N') {
								$scope.recSeqFlagOrg = 'N';
							} else {
//								$scope.recSeqFlagOrg = (totaCT[0].body.isFirstTrade == "Y" || totaCT[0].body.custRemarks == "Y" ? "Y" : "N");     //是否弱勢或首購客戶 控制<傳送OP交易及列印表單>與<網銀快速下單>按鈕
								$scope.recSeqFlagOrg = 'Y';
							}
							
							$scope.recSeqFlag = $scope.recSeqFlagOrg;
							if($scope.inputVO.custID.length == 10)
								$scope.custAge = (totaCT[0].body.custAge < 18 ? $filter('i18n')('ehl_01_sot510_001') : ""); //委託人未成年，請查詢印鑑備註訊息或法代同意書內容
							
							return;
						}
			});
		};
		
		// 2020-02-14 add by ocean
		$scope.runToWhere = function(tradeSEQT) {
			$scope.connector('set', "SOTTradeSEQ", tradeSEQT);

			if ($scope.inputVO.trustTS == 'S') {
				if ($scope.fromFPS) {
					// from FPS_SOT.js
					$scope.setSOTurl('assets/txn/SOT310/SOT310.html');
				} else {
					$rootScope.menuItemInfo.url = "assets/txn/SOT310/SOT310.html";
				}	
			} else {
				if ($scope.fromFPS) {
					$scope.setSOTurl('assets/txn/SOT315/SOT315.html');
				} else {
					$rootScope.menuItemInfo.url = "assets/txn/SOT315/SOT315.html";
				}	
			}
		}
		
		$scope.goPage = function() {
			$scope.runToWhere($scope.inputVO.tradeSEQ);
		};
		
		$scope.query = function() {
			if($scope.inputVO.tradeSEQ) {
				$scope.noCallCustQuery().then(function(data) {
					$scope.getSOTCustInfoCT();
				});
			} else {
				$scope.runToWhere(null);
			}
		};
		$scope.query();
		
		//傳送OP交易，避免按鈕被連續點擊造成錯誤，先disable按鈕並$timeout
		$scope.goOP = function () {
			$scope.inputVO.goOPDisabled = true;
			
			$timeout(function() {
				$scope.do_goOP(); 
				$scope.inputVO.goOPDisabled = false;}
			, 1000);			
		}
		
		$scope.do_goOP = function () {
			 
			//			console.log("goOP isRecNeeded:" + $scope.isRecNeeded);
			//			console.log("goOP recSEQ:" + $scope.inputVO.recSEQ);
			if($scope.isRecNeeded=='Y' && (!$scope.inputVO.recSEQ || $scope.inputVO.recSEQ=='')){
				$scope.showErrorMsg("ehl_02_common_007");
				return;
			}
			
			if ($scope.inputVO.bargainFeeFlag == null || $scope.inputVO.bargainFeeFlag == '2') {
				// 檢核手續費優惠申請皆簽核完畢後，才可進行傳送OP交易及列印表單鍵
				$scope.sendRecv("SOT712", "updateBatchSeq", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {'prodType': 'BN', 
																													   'tradeSeq': $scope.inputVO.tradeSEQ},
						function(tota, isError) {
							if (!isError) {
								$scope.sendRecv("SOT310", "goOP", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO", $scope.inputVO,
										function(tota, isError) {
											if (!isError) {
												if (tota[0].body.warningMsg != '' && tota[0].body.warningMsg != null) {
													$scope.showErrorMsg(tota[0].body.warningMsg);
												}
												if (tota[0].body.errorMsg != '' && tota[0].body.errorMsg != null) {
													$scope.showErrorMsg(tota[0].body.errorMsg);
												} else {
													$scope.showMsg("ehl_02_SOT_002");
													$scope.query();
													
													$scope.printReport();  //產生報表
//													var txtMsg = $filter('i18n')('ehl_01_SOT_008');
//													$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
//														$scope.connector('set', "SOTTradeSEQ", null);
//														$scope.connector('set', "SOTCarSEQ", null);
//														$rootScope.menuItemInfo.url = "assets/txn/SOT310/SOT310.html";
//										            });
													
													return;
												}
											}
								});
							}
				});
			}
		};
		
		//網銀快速下單，避免按鈕被連續點擊造成錯誤，先disable按鈕並$timeout
		$scope.goBANK = function () {
			$scope.inputVO.goBankDisabled = true;
			
			$timeout(function() {
				$scope.do_goBANK(); 
				$scope.inputVO.goBankDisabled = false;}
			, 1000);			
		}
		
		//網銀快速下單 
		$scope.do_goBANK = function() {
			// 檢核手續費優惠申請皆簽核完畢後，才可進行傳送OP交易及列印表單鍵   (批號產生)
			$scope.sendRecv("SOT712", "updateBatchSeq", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {'prodType': 'BN', 'tradeSeq': $scope.inputVO.tradeSEQ},
				function(tota, isError) {
					if (!isError) {
						  $scope.sendRecv("SOT310", "goBANK", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO", {'tradeSEQ': $scope.inputVO.tradeSEQ},
						  function(tota, isError) {
							 if (!isError) {
								if (tota[0].body.errorMsg != '' && tota[0].body.errorMsg != null) {
								   $scope.showErrorMsg(tota[0].body.errorMsg);
								} else { 
								       $scope.showMsg("ehl_01_SOT_011");//網銀/行銀快速下單成功
								       $scope.inputVO.webWarningMsg = "客戶需於鍵機當日15:00前，透過網銀/行銀確認完成，逾時此筆快速申購將失效。";//警語									   
								       $scope.query();									   
									   return;
								}
							 }
						  }); 
					      return;
					}
			}); 
		}
		
		$scope.delTrade = function () {
			var txtMsg = $filter('i18n')('ehl_02_common_005');
			
			$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
				$scope.sendRecv("SOT310", "delTrade", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								$scope.newTrade();
							}
				});
            });
		};
		
		
		//錄音序號檢核
		$scope.validateRecSeq = function(){
//			if($scope.inputVO.recSEQ.length>0 && $scope.inputVO.recSEQ.length<6){
//				$scope.showErrorMsg("ehl_02_common_007");
//				$scope.inputVO.recSEQ='';
//				return;
//			}
			if($scope.inputVO.recSEQ == ''){
				$scope.recSeqFlag = $scope.recSeqFlagOrg;
				return;
			}
			$scope.sendRecv("SOT712", 
					"validateRecseq", 
					"com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", 
					{'custID':$scope.inputVO.custID, 
				 	 'prodType': 'BOND', 
				 	 'recSeq':$scope.inputVO.recSEQ,
				 	 'branchNbr':sysInfoService.getBranchID(),
					 'prodID': $scope.inputVO.prodID},
			function(tota, isError) {
				if (!isError) {
					if(!tota[0].body.Recseq){
						$scope.showErrorMsg("ehl_02_common_007");
						$scope.inputVO.recSEQ='';
						$scope.recSeqFlag = $scope.recSeqFlagOrg;
					} else {
						$scope.recSeqFlag = 'N';
					}
					return;
				} else {
					//			console.log('validateRecSeq:'+tota[0].body.msgData);
					$scope.showErrorMsg("ehl_02_common_007");
					$scope.inputVO.recSEQ='';
					$scope.recSeqFlag = $scope.recSeqFlagOrg;
					return;
				}
			});
			
		}
		
		$scope.newTrade = function() {
			$scope.runToWhere(null);
		};
		
		$scope.goPageSOT610 = function () {
			$rootScope.menuItemInfo.url = "assets/txn/SOT610/SOT610.html";
		}
		
		//產生報表
		$scope.printReport = function(){
			var fitVO = {
				caseCode : 		1, 								//case1 下單
				custId   :		$scope.inputVO.custID,			//客戶ID
				prdType  :      3,								//商品類別 : 海外債
				tradeSeq : 		$scope.inputVO.tradeSEQ, 		//交易序號
				tradeSubType:	1,								//交易類型 : 申購
				tradeTS  :      $scope.inputVO.trustTS,			//S=特金,M=金錢信託
				overCentRateYN: $scope.inputVO.overCentRateYN,	//超過集中度上限
				isPrintSOT819:		$scope.inputVO.loanFlag //是否列印貸款風險預告書
			}
				
			$scope.sendRecv("SOT712", "fitToGetPDF", "com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO", fitVO,
					function(tota, isError) {
				if (isError) {
					$scope.showErrorMsg(totas[0].body.msgData);
				}
			});
		};
});