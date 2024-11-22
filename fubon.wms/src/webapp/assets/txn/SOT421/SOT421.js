/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT421Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT421Controller";
		
		// filter
		getParameter.XML(["SOT.CUST_TYPE", "SOT.TRUST_CURR_TYPE", "SOT.MARKET_TYPE", "SOT.ENTRUST_TYPE_REDEEM_SI","SOT.SPEC_CUSTOMER"], function(totas) {
			if (totas) {
				$scope.mappingSet['SOT.CUST_TYPE'] = totas.data[totas.key.indexOf('SOT.CUST_TYPE')];
				$scope.mappingSet['SOT.TRUST_CURR_TYPE'] = totas.data[totas.key.indexOf('SOT.TRUST_CURR_TYPE')];
				$scope.mappingSet['SOT.MARKET_TYPE'] = totas.data[totas.key.indexOf('SOT.MARKET_TYPE')];
				$scope.mappingSet['SOT.ENTRUST_TYPE_REDEEM_SI'] = totas.data[totas.key.indexOf('SOT.ENTRUST_TYPE_REDEEM_SI')];
				$scope.mappingSet['SOT.SPEC_CUSTOMER'] = totas.data[totas.key.indexOf('SOT.SPEC_CUSTOMER')];//客戶註記
			}         
		});
        //
		
		$scope.init = function(){
			//			console.log("init");
			$scope.refVal = undefined;
			$scope.DEBIT_ACCT=undefined;                                //扣款帳號餘額
			$scope.inputVO = {
					tradeSEQ: '',
					
					custID: '', 								//客戶ID
					custName: '', 
					
					kycLV: '', 									//KYC等級
					kycDueDate: undefined, 						//KYC效期
					profInvestorYN: '', 						//是否為專業投資人
					piRemark: '',								//專業投資人註記
					piDueDate: undefined, 						//專業投資人效期
					custRemarks: '', 							//客戶註記
					isOBU: '', 									//是否為OBU客戶
					isAgreeProdAdv: '', 						//同意投資商品諮詢服務
					bargainDueDate: undefined,					//期間議價效期
					plNotifyWays: '',							//停損停利通知方式
					takeProfitPerc: undefined,					//停利點
					stopLossPerc: undefined,					//停損點 
					prodAcct: '', 								//組合式商品帳號
					debitAcct: '',								//贖回款入帳帳號
					w8benEffDate: undefined,					//W8ben有效日期
					w8BenEffYN: '',
					fatcaType: '',
					
					
					custType: 'CUST', 							//來行人員
					agentID: '', 								//代理人ID
					agentName: '',
					
					narratorID: projInfoService.getUserID(),	//解說人員ID
					narratorName: projInfoService.getUserName(),//解說人員姓名
					batchSeq: '',								//批號
					
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
					prodAcct: '',								//組合式商品帳號
					debitAcct: '',								//贖回款入帳帳號
					ivBrh: '',                                  //分行別(收件行)
					tradeDate: undefined,						//交易日期
					
					entrustType: '4', 					//贖回方式 4:發行機構或券商回覆之實際成交價格
					certificateID: '',							//憑證編號
					goOPDisabled: false							//傳送OP按鈕，避免連續點擊
			};
		};
        
        // SOT701-客戶電文
		$scope.getSOTCustInfo = function() {
			//			console.log("getSOTCustInfo");
			var deferred = $q.defer();
			
			if($scope.inputVO.custID) {
				$scope.sendRecv("SOT420", "getSOTCustInfo", "com.systex.jbranch.app.server.fps.sot420.SOT420InputVO", {'custID': $scope.inputVO.custID, 'prodType': 4, 'tradeType': 2},
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.prodAcct.length == 0) { //prodAcct組合式商品帳號不為空
									$scope.showErrorMsg("ehl_01_sot310_002");
									if ($scope.fromFPS) {
										// from FPS_SOT.js
										$scope.setSOTurl('assets/txn/SOT420/SOT420.html');
									} else {
										$rootScope.menuItemInfo.url = "assets/txn/SOT420/SOT420.html";
									}
								} else if (tota[0].body.noSale == "Y") { //若為禁銷客戶，出現提示訊息禁止下單。
									$scope.showErrorMsg("ehl_01_sot310_003");
									if ($scope.fromFPS) {
										// from FPS_SOT.js
										$scope.setSOTurl('assets/txn/SOT420/SOT420.html');
									} else {
										$rootScope.menuItemInfo.url = "assets/txn/SOT420/SOT420.html";
									}
								} else if (tota[0].body.deathFlag == "Y" ) { //若為死亡戶/禁治產等狀況，不可下單。 //修改若為死亡戶狀況，可下單。   原邏輯為禁止下單。
									$scope.showErrorMsg("ehl_01_SOT_012");
									if ($scope.fromFPS) {
										// from FPS_SOT.js
										$scope.setSOTurl('assets/txn/SOT420/SOT420.html');
									} else {
										$rootScope.menuItemInfo.url = "assets/txn/SOT420/SOT420.html";
									}
								} else {
									var custDtl = tota[0].body;
									if (tota[0].body.rejectProdFlag == "Y") {
										var txtMsg = $filter('i18n')('ehl_01_sot310_005');
										
										$confirm({text: txtMsg + "是否繼續"}, {size: 'sm'}).then(function() {
											$scope.setDTL(custDtl); //設定客戶資料
											deferred.resolve("success");
											return deferred.promise;
							            }); 
									} else {
										$scope.setDTL(custDtl);
									}
								}
							}
				});
			}
			
			deferred.resolve("");
			return deferred.promise;
		};
		
		//非常規交易
		$scope.getSOTCustInfoCT = function () {
			$scope.sendRecv("SOT420", "getSOTCustInfoCT", "com.systex.jbranch.app.server.fps.sot420.SOT420InputVO", {'tradeSEQ':$scope.inputVO.tradeSEQ, 'custID':$scope.inputVO.custID,'prodType': 4,'tradeType':2, 'prodID': $scope.inputVO.prodID},
					function(totaCT, isError) {
						if (!isError) {
							$scope.ageUnder70Flag = (totaCT[0].body.ageUnder70Flag == "N" ? $filter('i18n')('ehl_02_SOT_004') : "");
							$scope.eduJrFlag = (totaCT[0].body.eduJrFlag == "N" ? $filter('i18n')('ehl_02_SOT_005') : ""); ;
							$scope.healthFlag = (totaCT[0].body.healthFlag == "N" ? $filter('i18n')('ehl_02_SOT_006') : "");
							if($scope.inputVO.custID.length == 10)
								$scope.custAge = (totaCT[0].body.custAge < 18 ? $filter('i18n')('ehl_01_sot510_001') : ""); //委託人未成年，請查詢印鑑備註訊息或法代同意書內容
							return;
						}
			});
		};
		
		//設定客戶資料
		$scope.setDTL = function (custDtl) {
			$scope.inputVO.custName = custDtl.custName;
			$scope.inputVO.kycLV = custDtl.kycLV;									//KYC等級
			$scope.inputVO.kycDueDate = $scope.toJsDate(custDtl.kycDueDate);		//KYC效期
			$scope.inputVO.profInvestorYN = custDtl.profInvestorYN;				//是否為專業投資人
			$scope.inputVO.piRemark = custDtl.piRemark;							//專業投資人註記
			$scope.inputVO.piDueDate = $scope.toJsDate(custDtl.piDueDate);			//專業投資人效期
			$scope.inputVO.custRemarks = custDtl.custRemarks;						//客戶註記
			$scope.inputVO.isOBU = custDtl.isOBU;									//是否為OBU客戶
			$scope.inputVO.isAgreeProdAdv = custDtl.isAgreeProdAdv;				//同意投資商品諮詢服務
			$scope.inputVO.bargainDueDate =  custDtl.bargainDueDate;				//期間議價效期
			$scope.inputVO.plNotifyWays = custDtl.plNotifyWays;					//停損停利通知方式
			$scope.inputVO.takeProfitPerc = custDtl.takeProfitPerc;				//停利點
			$scope.inputVO.stopLossPerc = custDtl.stopLossPerc;					//停損點
			$scope.inputVO.w8benEffDate = $scope.toJsDate(custDtl.w8benEffDate);	//W8ben有效日期
			$scope.inputVO.w8BenEffYN = custDtl.w8BenEffYN;
			$scope.inputVO.fatcaType = custDtl.fatcaType;											
														 
			 
			
		};
		
		$scope.noCallCustQuery = function () {
			//			console.log("noCallCustQuery");
			var deferred = $q.defer();
			
			
			//TODO call SOT410 查購物車
			//			console.log("SOT410 query()　carList");
			$scope.sendRecv("SOT410", "query", "com.systex.jbranch.app.server.fps.sot410.SOT410InputVO", $scope.inputVO,
					function(tota, isError) {
				        //			console.log("SOT410 query");
						if (!isError) {
							$scope.cartList = tota[0].body.carList;
							$scope.outputVO = tota[0].body;
							
							$scope.inputVO.narratorID = tota[0].body.carList[0].NARRATOR_ID;					//解說人員ID
							$scope.inputVO.narratorName = tota[0].body.carList[0].NARRATOR_NAME;				//解說人員姓名
							$scope.narratorDisabled = true;
							$scope.inputVO.batchSeq = tota[0].body.carList[0].BATCH_SEQ;						//批號
							
							$scope.inputVO.custID = tota[0].body.mainList[0].CUST_ID;							//客戶ID
							$scope.inputVO.custName = tota[0].body.mainList[0].CUST_NAME;	 

							$scope.inputVO.kycLV = tota[0].body.mainList[0].KYC_LV;	 							//KYC等級
							$scope.inputVO.kycDueDate = $scope.toJsDate(tota[0].body.mainList[0].KYC_DUE_DATE);	//KYC效期
							$scope.inputVO.profInvestorYN = tota[0].body.mainList[0].PROF_INVESTOR_YN;	 		//是否為專業投資人
							$scope.inputVO.piRemark = tota[0].body.mainList[0].PI_REMARK;						//專業投資人註記
							$scope.inputVO.piDueDate = $scope.toJsDate(tota[0].body.mainList[0].PI_DUE_DATE);	//專業投資人效期
							$scope.inputVO.custRemarks = tota[0].body.mainList[0].CUST_REMARKS;	 				//客戶註記
							$scope.inputVO.isOBU = tota[0].body.mainList[0].IS_OBU;	 							//是否為OBU客戶
							$scope.inputVO.isAgreeProdAdv = tota[0].body.mainList[0].IS_AGREE_PROD_ADV;	 		//同意投資商品諮詢服務
							$scope.inputVO.bargainDueDate = tota[0].body.mainList[0].BARGAIN_DUE_DATE;			//期間議價效期
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
							
							$scope.inputVO.prodAcct = tota[0].body.carList[0].PROD_ACCT;		                //組合式商品帳號		 
							$scope.inputVO.debitAcct = tota[0].body.carList[0].DEBIT_ACCT;	                    //贖回款入帳帳號
							$scope.ivBrh = tota[0].body.mainList[0].BRANCH_NBR;  //TODO BRANCH_NBR=ivBrh                                        //分行別(收件行)
							deferred.resolve("success");
						}
			});
			
			return deferred.promise;
		};
		
		$scope.getProdDTL = function () {
			//			console.log("scope.getProdDTL()");
			var deferred = $q.defer();
			
			if($scope.inputVO.prodID) {
				$scope.sendRecv("SOT420", "getProdDTL", "com.systex.jbranch.app.server.fps.sot420.SOT420InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								//			console.log('getProdDTL()'+JSON.stringify(tota)); 
								if (tota[0].body.prodDTL.length && tota[0].body.prodDTL.length > 0) {
						        	$scope.inputVO.prodCurr = tota[0].body.prodDTL[0].CURRENCY_STD_ID;	//計價幣別
									$scope.inputVO.prodRiskLV = tota[0].body.prodDTL[0].RISKCATE_ID;	//產品風險等級
									 
									//預設
									$scope.inputVO.entrustType = ""; //4：發行機構或券商回覆之實際成交價格 (修改預設為無)
									if ($scope.inputVO.entrustType == "4") { 
										$scope.refVal = $scope.inputVO.refVal;        //發行機構或券商回覆之實際成交價格
										$scope.inputVO.entrustAmt = $scope.inputVO.refVal; 
									}
									
									deferred.resolve("success");
									return deferred.promise;
								} else {
									var txtMsg = $scope.inputVO.prodID + $filter('i18n')('ehl_01_common_009');
									$scope.showErrorMsg(txtMsg);
									if ($scope.fromFPS) {
										// from FPS_SOT.js
										$scope.setSOTurl('assets/txn/SOT420/SOT420.html');
									} else {
										$rootScope.menuItemInfo.url = "assets/txn/SOT420/SOT420.html";
									}
									//			console.log("getProdDTL 查無資料:"+$scope.inputVO.prodID);
								}
							} 
				});
			} else {
				$scope.showErrorMsg("ehl_01_common_009");
				if ($scope.fromFPS) {
					// from FPS_SOT.js
					$scope.setSOTurl('assets/txn/SOT420/SOT420.html');
				} else {
					$rootScope.menuItemInfo.url = "assets/txn/SOT420/SOT420.html";
				}
				//			console.log("getProdDTL empty[009]");
			}
			
			deferred.resolve("");
			return deferred.promise;
		};
		
		//計算限價 entrustAmt
		$scope.calculate = function () {
			//			console.log("calculate()");
			$scope.refVal = undefined;
			//贖回方式：
			if ($scope.inputVO.entrustType == "1") {          //限價1%
				$scope.refVal = $scope.inputVO.refVal - 1;
				$scope.inputVO.entrustAmt = $scope.inputVO.refVal - 1;
			} else if ($scope.inputVO.entrustType == "2") {   //限價3%
				$scope.refVal = $scope.inputVO.refVal - 3;
				$scope.inputVO.entrustAmt = $scope.inputVO.refVal - 3;
			} else if ($scope.inputVO.entrustType == "3") {   //限價5%
				$scope.refVal = $scope.inputVO.refVal - 5;
				$scope.inputVO.entrustAmt = $scope.inputVO.refVal - 5;
			} else if ($scope.inputVO.entrustType == "4") {
				$scope.refVal = $scope.inputVO.refVal;        //發行機構或券商回覆之實際成交價格
				$scope.inputVO.entrustAmt = $scope.inputVO.refVal;
			}
			
		}
        
        // if data
        $scope.prodDTL = $scope.connector('get','SOT421_prodDTL');
        $scope.custID = $scope.connector('get','SOT421_custID');
		$scope.inputVO.tradeSEQ = $scope.connector('get','SOTTradeSEQ');
		$scope.inputVO.carSEQ = $scope.connector('get','SOTCarSEQ');
		
		$scope.sendRecv("SOT712", "getTradeSEQ", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.inputVO.tradeSEQ = tota[0].body.tradeSEQ;
						return;
					}
		});
		
		
		//從庫存頁面導入到下單, 要將庫存資料放到下單頁
		$scope.setProd = function () {
			//			console.log("scope.setProd()");
			//			console.log('prodDTL'+JSON.stringify($scope.prodDTL)); 
			//			console.log('custID'+JSON.stringify($scope.custID)); 
			//			console.log('tradeSEQ'+JSON.stringify($scope.inputVO.tradeSEQ)); 
			//			console.log('carSEQ'+JSON.stringify($scope.inputVO.carSEQ)); 
			$scope.init();
			
			if ($scope.prodDTL && $scope.custID) {
				$scope.inputVO.custID = $scope.custID;
				 
				$scope.inputVO.ivBrh = $scope.prodDTL.IVBRH;  //分行別(收件行)
				$scope.inputVO.debitAcct = $scope.prodDTL.IVCUAC;  //活存帳號
				$scope.inputVO.prodAcct = $scope.prodDTL.IVTDAC;   //定存帳號  IVTDAC= PROD_ACCT()       
				 
				$scope.getSOTCustInfo().then(function(data) {
					$scope.inputVO.prodID = $scope.prodDTL.SDPRD;
					//$scope.prodDTL 為庫存頁面電文導入的資料
					$scope.inputVO.prodName = $scope.prodDTL.PRDCNM;					  //商品名稱
					$scope.inputVO.purchaseAmt = $scope.prodDTL.IVAMT2;					  //庫存面額
					$scope.inputVO.prodCurr = $scope.prodDTL.IVCUCY;                      //商品幣別
					$scope.inputVO.receivedNo = $scope.prodDTL.IVRNO;                     //收件編號　
					//贖回要取庫存的最新報價和最新報價日
					$scope.inputVO.refVal = $scope.prodDTL.SDAMT3;                        //最新報價
					$scope.inputVO.refValDate = $scope.toJsDate($scope.prodDTL.SDAMT3DATE);		          //最新報價日
					 
					//查詢商品
					$scope.getProdDTL().then(function(data) {
						//			console.log('getProdDTL().success data'+JSON.stringify(data)); 
						//$scope.inputVO.trustCurrType = $scope.prodDTL.TrustType;			  //信託業務別
						//$scope.inputVO.marketType = $scope.prodDTL.BondType;				  //債券市場種類 
					});
				}); 
			}
			
		}
		$scope.setProd();
		$scope.calculate();//計算限價 entrustAmt
		
		$scope.query = function() {
			//			console.log("scope.query()");
			if($scope.inputVO.tradeSEQ) {
				$scope.noCallCustQuery().then(function(data) {
					$scope.getSOTCustInfoCT();
				});
			} else {
				if ($scope.fromFPS) {
					// from FPS_SOT.js
					$scope.setSOTurl('assets/txn/SOT420/SOT420.html');
				} else {
					$rootScope.menuItemInfo.url = "assets/txn/SOT420/SOT420.html";
				}
				//			console.log("query error");

			}
		};
		
		//傳送OP交易，避免按鈕被連續點擊造成錯誤，先disable按鈕並$timeout
		$scope.goOP = function () {
			$scope.inputVO.goOPDisabled = true;
			
			$timeout(function() {
				$scope.do_goOP(); 
				$scope.inputVO.goOPDisabled = false;}
			, 1000);			
		}
		
		$scope.do_goOP = function () {
			
			if($scope.inputVO.refVal==0 || !$scope.inputVO.refValDate){
				$scope.showErrorMsg("參考報價為0；報價日期為空");
				return;
			}
			
			
			//			console.log("scope.goOP()");
			$scope.sendRecv("SOT420", "goOP", "com.systex.jbranch.app.server.fps.sot420.SOT420InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.errorMsg != '' && tota[0].body.errorMsg != null) {
								$scope.showErrorMsg(tota[0].body.errorMsg);
							} else {
								$scope.printReport();  //產生報表
								$scope.showMsg("ehl_02_SOT_002");
								$scope.query();
								return;
							}
						}
			});
			 
			
			 		
		};
		
		$scope.newTrade = function () {
			//			console.log("newTrade");
			if ($scope.fromFPS) {
				// from FPS_SOT.js
				$scope.setSOTurl('assets/txn/SOT420/SOT420.html');
			} else {
				$rootScope.menuItemInfo.url = "assets/txn/SOT420/SOT420.html";
			} 
		}
		
		//產生報表
		$scope.printReport = function(){
			var fitVO = {
				caseCode : 		1, 							//case1 下單
				custId   :		$scope.inputVO.custID,		//客戶ID
				prdType  :      4,							//商品類別 : SI
				tradeSeq : 		$scope.inputVO.tradeSEQ, 	//交易序號
				tradeSubType:	2,							//交易類型 : 贖回
				ivBrh :         $scope.inputVO.ivBrh    
			}
				
			$scope.sendRecv("SOT712", "fitToGetPDF", "com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO", fitVO,
					function(tota, isError) {
				if (isError) {
					$scope.showErrorMsg(totas[0].body.msgData);
				}
			});
		};
		
});