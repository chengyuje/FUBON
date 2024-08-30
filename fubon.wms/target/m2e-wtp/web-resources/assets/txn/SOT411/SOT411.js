/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT411Controller',
	function($rootScope, $scope, $controller, $confirm, $filter, socketService, ngDialog, projInfoService, $q, getParameter, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT411Controller";
		
		getParameter.XML(["SOT.SPEC_CUSTOMER"], function(totas) {
			if (totas) {
		        $scope.mappingSet['SOT.SPEC_CUSTOMER'] = totas.data[totas.key.indexOf('SOT.SPEC_CUSTOMER')];//客戶註記
		        
			}
		});
		
		$scope.init = function() {
			$scope.inputVO = {
					tradeSEQ: '',
					
					custID: '', 								//客戶ID
					custName: '', 
					
					kycLv: '', 									//KYC等級
					kycDueDate: undefined, 						//KYC效期
					profInvestorYN: '', 						//是否為專業投資人
					piRemark: '',                               //專業投資人註記
					piDueDate: undefined, 						//專業投資人效期
					custRemarks: '', 							//客戶註記
					isOBU: '', 									//是否為OBU客戶
					debitAcct: '', 								//扣款帳號
					creditAcct: '',								//收益入帳帳號
					custProFlag: '',
					
					authID: '',									//授權解說人員ID
//					authName: '',								//授權解說人員姓名
					narratorID: projInfoService.getUserID(),	//解說人員ID
					narratorName: projInfoService.getUserName(),//解說人員姓名
					batchSeq: '',								//批號
					
					prodID: '', 								//商品ID
					prodName: '',								//商品名稱
					prodCurr: '',								//計價幣別
					prodRiskLV: '',								//產品風險等級
					prodMinBuyAmt: undefined,					//最低申購面額
					prodMinGrdAmt: undefined,					//累計申購面額
					refVal: undefined,							//參考報價
					refValDate: undefined,						//參考報價日期
					purchaseAmt: undefined,						//申購金額/庫存金額
					debitAcct: '',								//扣款帳號
					trustAcct: '',								//信託帳號
					creditAcct: '',								//收益入帳帳號/贖回款入帳帳號
					tradeDate: undefined,						//交易日期
					prodAcct:'',								//組合式商品帳號 					
					
					tradeStatus: '', 							//交易狀態
					isBargainNeeded: '',						//是否需要議價
					recSEQ:'',		  							//錄音序號
					
					proCorpInv: '',								//專業機構投資人
					highYieldCorp: '',							//高淨值法人
					siProCorp: '',								//衍商資格專業法人
					pro3000: '',								//專業自然人提供3000萬財力證明
					pro1500: '',								//專業自然人提供1500萬財力證明
					isPrintSot816: '',							//是否列印結構型商品交易自主聲明書
					isPrintSot817: '',							//是否列印結構型商品推介終止同意書
					
					signAgmMsg1:'',
					signAgmMsg2:'',
					signAgmMsg3:'',
					signAgmMsg4:'',
					goOPDisabled: false,							//傳送OP按鈕，避免連續點擊
					overCentRateYN: '',							//超過集中度上限
					
					checkRec: false,								//檢查是否需要錄音序號
					loanFlag: ''                               //90天貸款註記(Y/N)
			};
		};
		$scope.init();
		
		$scope.inputVO.tradeSEQ = $scope.connector('get','SOTTradeSEQ');
		$scope.inputVO.warningMsg = $scope.connector('get', 'SOT411_warningMsg')
//		$scope.inputVO.carSEQ = $scope.connector('get', 'SOTCarSEQ);
		$scope.connector('set', 'SOTTradeSEQ', null);
		$scope.connector('set', 'SOT411_warningMsg', null);
//		$scope.connector('set', 'SOTCarSEQ', null);
		
		//取消
		$scope.delTrade = function () {
			var txtMsg = $filter('i18n')('ehl_02_common_005');
			$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
				$scope.sendRecv("SOT410", "delTrade", "com.systex.jbranch.app.server.fps.sot410.SOT410InputVO",{"tradeSEQ":$scope.inputVO.tradeSEQ},
						function(tota, isError) {
							if (!isError) {
								$scope.newTrade();
							}
				});
            });
		};
		
		
		
		
		//非常規交易
		$scope.getSOTCustInfoCT = function () {
			$scope.sendRecv("SOT410", "getSOTCustInfoCT", "com.systex.jbranch.app.server.fps.sot410.SOT410InputVO", {'tradeSEQ':$scope.inputVO.tradeSEQ, 'custID':$scope.inputVO.custID,'prodType': 4,'tradeType':1, 'prodID': $scope.inputVO.prodID},
				function(totaCT, isError) {
					if (!isError) {
						$scope.ageUnder70Flag = (totaCT[0].body.ageUnder70Flag == "N" ? $filter('i18n')('ehl_02_SOT_004') : ""); //特定客戶已改為65歲
						$scope.eduJrFlag = (totaCT[0].body.eduJrFlag == "N" ? $filter('i18n')('ehl_02_SOT_005') : ""); ;
						$scope.healthFlag = (totaCT[0].body.healthFlag == "N" ? $filter('i18n')('ehl_02_SOT_006') : "");
						if($scope.inputVO.custID.length == 10)
							$scope.custAge = (totaCT[0].body.custAge < 18 ? $filter('i18n')('ehl_01_sot510_001') : ""); //委託人未成年，請查詢印鑑備註訊息或法代同意書內容
							
						$scope.inputVO.custProFlag = totaCT[0].body.custProFlag;		//專業投資人
						$scope.inputVO.custRemarks = totaCT[0].body.custRemarks;		//弱勢客戶
						$scope.inputVO.proCorpInv = totaCT[0].body.proCorpInv;			//專業機構投資人
						$scope.inputVO.highYieldCorp = totaCT[0].body.highYieldCorp;	//高淨值法人
						$scope.inputVO.siProCorp = totaCT[0].body.siProCorp;			//衍商資格專業法人
						$scope.inputVO.pro3000 = totaCT[0].body.pro3000;				//專業自然人提供3000萬財力證明(L1,J1)
						$scope.inputVO.pro1500 = totaCT[0].body.pro1500;				//專業自然人提供1500萬財力證明(L2,J2)
							
						$scope.inputVO.isPrintSot816 = "N";	//是否列印結構型商品交易自主聲明書
						$scope.inputVO.isPrintSot817 = "N";	//是否列印結構型商品推介終止同意書
						
						//*** 推介同意簽署機制
						if ($scope.inputVO.proCorpInv == "Y" || $scope.inputVO.highYieldCorp == "Y" || $scope.inputVO.siProCorp == "Y" || $scope.inputVO.pro3000 == "Y" ||
								($scope.inputVO.pro1500 == "Y" && $scope.inputVO.purchaseAmtTwd >= 3000000)) {
							//專業機構投資人//高淨值法人//專業法人//專業自然人提供3000萬財力證明//專業自然人提供1500萬財力證明且申購金額>=3百萬台幣，不檢核也不套印
						} else {							
							if (($scope.inputVO.custProFlag != "Y" && $scope.inputVO.custRemarks == "Y") ||
								($scope.inputVO.pro1500 == "Y" && $scope.inputVO.purchaseAmtTwd < 3000000 && $scope.inputVO.custRemarks == "Y")) {
								//一般客戶為特定客群，需簽署結構型商品交易自主聲明書
								$scope.inputVO.signAgmMsg1 = $filter('i18n')('ehl_02_sot410_002');
								$scope.inputVO.isPrintSot816 = "Y";
							} else  if (($scope.inputVO.pro1500 == "Y" && $scope.inputVO.purchaseAmtTwd < 3000000 && $scope.inputVO.custRemarks != "Y") ||
										($scope.inputVO.custProFlag != "Y" && $scope.inputVO.custRemarks != "Y") ||
										($scope.inputVO.siProCorp != "Y"))
							{ 
								//查詢結構型商品推介同意註記
								$scope.sendRecv("SOT701", "getSDACTQ8Data", "com.systex.jbranch.app.server.fps.sot701.SOT701InputVO", {'custID':$scope.inputVO.custID},
									function(totaCT, isError) {
										if (!isError) {
											$scope.inputVO.isSign = totaCT[0].body.siPromDataVO.isSign;
											$scope.inputVO.signStatus = totaCT[0].body.siPromDataVO.status;
											
											if($scope.inputVO.isSign != "Y" || $scope.inputVO.signStatus != "Y") {
												$scope.inputVO.isPrintSot816 = "Y";	//是否列印結構型商品交易自主聲明書
												$scope.inputVO.isPrintSot817 = "Y";	//是否列印結構型商品推介終止同意書
											}
											
											if($scope.inputVO.isSign == "N") {												
												$scope.inputVO.signAgmMsg2 = $filter('i18n')('ehl_02_sot410_003');		//一般客戶未簽署結構型商品推介同意書，需簽署結構型商品交易自主聲明書
											} else if($scope.inputVO.isSign == "Y") {
												if($scope.inputVO.signStatus == "C") {
													$scope.inputVO.signAgmMsg3 = $filter('i18n')('ehl_02_sot410_004');	//一般客戶簽署結構型商品推介同意書已終止，需簽署結構型商品交易自主聲明書
												} else if($scope.inputVO.signStatus == "E") {
													$scope.inputVO.signAgmMsg4 = $filter('i18n')('ehl_02_sot410_005');	//一般客戶簽署結構型商品推介同意書已失效，需簽署結構型商品交易自主聲明書
												}
											}
											
										}
								});
							}
						}
							
						return;
					}
			});
		};
		
		$scope.noCallCustQuery = function () {
			var defer = $q.defer();
			$scope.sendRecv("SOT410", "query", "com.systex.jbranch.app.server.fps.sot410.SOT410InputVO",{'tradeSEQ':$scope.inputVO.tradeSEQ},
					function(tota, isError) {
						if (!isError) {
							$scope.carList = tota[0].body.carList;
							$scope.mainList = tota[0].body.mainList;
							if($scope.mainList && $scope.mainList.length !=0){
								$scope.isRecNeeded = $scope.mainList[0].IS_REC_NEEDED;  //是否需錄音序號
								if($scope.isRecNeeded == "Y") {
									$scope.inputVO.checkRec = true;
								}
								$scope.inputVO.custID= $scope.mainList[0].CUST_ID;
								$scope.inputVO.prodType= $scope.mainList[0].PROD_TYPE;
								$scope.inputVO.custName= $scope.mainList[0].CUST_NAME;
								$scope.inputVO.kycLv= $scope.mainList[0].KYC_LV;
								$scope.inputVO.kycDueDate= $scope.toJsDate($scope.mainList[0].KYC_DUE_DATE);
								$scope.inputVO.custRemarks= $scope.mainList[0].CUST_REMARKS;
								$scope.inputVO.isOBU= $scope.mainList[0].IS_OBU;
								$scope.inputVO.profInvestorYN= $scope.mainList[0].PROF_INVESTOR_YN;
								$scope.inputVO.piRemark = tota[0].body.mainList[0].PI_REMARK;	 	                        //專業投資人註記
								$scope.inputVO.piDueDate= $scope.toJsDate($scope.mainList[0].PI_DUE_DATE);
								$scope.inputVO.tradeStatus = $scope.mainList[0].TRADE_STATUS;								//交易狀態
//								$scope.inputVO.bargainDueDate = tota[0].body.mainList[0].BARGAIN_DUE_DATE;					//期間議價效期
//								$scope.inputVO.bargainFeeFlag = tota[0].body.mainList[0].BARGAIN_FEE_FLAG;					//議價狀態
								$scope.inputVO.loanFlag = tota[0].body.mainList[0].FLAG_NUMBER;				    //90天貸款註記(Y/N)
								if($scope.carList && $scope.carList.length !=0){
									$scope.inputVO.prodID=$scope.carList[0].PROD_ID;
									$scope.inputVO.prodName=$scope.carList[0].PROD_NAME;
									$scope.inputVO.prodRiskLV=$scope.carList[0].PROD_RISK_LV;
									$scope.inputVO.prodCurr=$scope.carList[0].PROD_CURR;
									$scope.inputVO.prodMinBuyAmt=$scope.carList[0].PROD_MIN_BUY_AMT;
									$scope.inputVO.prodMinGrdAmt=$scope.carList[0].PROD_MIN_GRD_AMT;
									$scope.inputVO.purchaseAmt=$scope.carList[0].PURCHASE_AMT;
									$scope.inputVO.purchaseAmtTwd=$scope.carList[0].PURCHASE_AMT_TWD;
									$scope.inputVO.prodAcct=$scope.carList[0].PROD_ACCT;
									$scope.inputVO.debitAcct=$scope.carList[0].DEBIT_ACCT;
								 
									$scope.inputVO.authID=$scope.carList[0].AUTH_ID;
//									$scope.getTellerName('authID',$scope.inputVO.authID);		//主管姓名
									$scope.inputVO.narratorID=$scope.carList[0].NARRATOR_ID;
									$scope.inputVO.narratorName=$scope.carList[0].NARRATOR_NAME;
									$scope.inputVO.batchSeq=$scope.carList[0].BATCH_SEQ;
									$scope.inputVO.bossID=$scope.carList[0].BOSS_ID;			//主管員編
									$scope.getTellerName('bossID',$scope.inputVO.bossID);		//主管姓名
									
									$scope.inputVO.overCentRateYN = $scope.carList[0].OVER_CENTRATE_YN;			//超過集中度上限
								}
							}
							defer.resolve("success");
						}
			});
			return defer.promise;
		};
		
		//初始頁面
		$scope.query = function() {
			if($scope.inputVO.tradeSEQ) {
				$scope.noCallCustQuery().then(function(data) {
					$scope.getSOTCustInfoCT();
				});
			} else {
				if ($scope.fromFPS) {
					// from FPS_SOT.js
					$scope.setSOTurl('assets/txn/SOT410/SOT410.html');
				} else {
					$rootScope.menuItemInfo.url = "assets/txn/SOT410/SOT410.html";
				}				
			}
		};
 		$scope.query();
 		
		//錄音序號檢核
		$scope.validateRecSeq = function(){
			if($scope.isRecNeeded == 'Y'){
				if($scope.inputVO.recSEQ == undefined || $scope.inputVO.recSEQ == '' || $scope.inputVO.recSEQ == null){
					$scope.showErrorMsg("ehl_02_common_007");
					return false;
				}
			}
//			return true;
			
			// 電文自己驗證錄音序號
			var sot712InputVO ={'custID':$scope.inputVO.custID,
					'prodType': 'SI',
					'recSeq':$scope.inputVO.recSEQ,
					'prodID': $scope.inputVO.prodID
					};
			$scope.sendRecv("SOT712", "validateRecseq", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO",sot712InputVO ,
			function(tota, isError) {
				if (!isError) {
					if(!tota[0].body.Recseq){
						$scope.showErrorMsg("ehl_02_common_007");
						$scope.inputVO.recSEQ='';
					}else {
						$scope.inputVO.checkRec = false;
					}
					return;
				}
			});
			
		}
		
		//修改
		$scope.goPage = function() {
			$scope.connector('set', "SOTTradeSEQ", $scope.inputVO.tradeSEQ);
			if ($scope.fromFPS) {
				// from FPS_SOT.js
				$scope.setSOTurl('assets/txn/SOT410/SOT410.html');
			} else {
				$rootScope.menuItemInfo.url = "assets/txn/SOT410/SOT410.html";
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
		
		//傳送OP
		$scope.do_goOP = function () {
//			if($scope.validateRecSeq()==false){
//				return;
//			}
			//才可進行傳送OP交易及列印表單鍵  (SI不需要 檢核手續費優惠申請皆簽核完畢後)
			$scope.sendRecv("SOT712", "updateBatchSeq", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", 
				{'prodType': 'SI', 'tradeSeq': $scope.inputVO.tradeSEQ}, function(tota, isError) {
					if (!isError) {
						$scope.sendRecv("SOT410", "goOP", "com.systex.jbranch.app.server.fps.sot410.SOT410InputVO", $scope.inputVO,
							function(tota, isError) {
								if (!isError) {
									 
									if (tota[0].body.errorMsg != '' && tota[0].body.errorMsg != null) {
										$scope.showErrorMsg(tota[0].body.errorMsg);
									} else {
										//產生報表
										$scope.printReport();

										$scope.showMsg("ehl_02_SOT_002");
										$scope.query();
										return;
									}
								}
						});
					}
			});
		};
		
		//繼續交易
		$scope.newTrade = function() {
			$scope.connector('set', "SOTTradeSEQ", null);
			if ($scope.fromFPS) {
				// from FPS_SOT.js
				$scope.setSOTurl('assets/txn/SOT410/SOT410.html');
			} else {
				$rootScope.menuItemInfo.url = "assets/txn/SOT410/SOT410.html";
			}
		};
		
		//結束
		$scope.goPageSOT610 = function () {
			$rootScope.menuItemInfo.url = "assets/txn/SOT610/SOT610.html";
		}
		
		//取得姓名
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
		
		$scope.printReport = function(){
			//產生報表
			var fitVO = {
				caseCode : 		1, 								//case1 下單
				custId   :		$scope.inputVO.custID,			//客戶ID
				prdType  :      4,								//商品類別 : SI
				tradeSeq : 		$scope.inputVO.tradeSEQ, 		//交易序號
				tradeSubType:	1,								//交易類型 : 申購
				isPrintSot816:	$scope.inputVO.isPrintSot816,	//是否列印結構型商品交易自主聲明書
				isPrintSot817: 	$scope.inputVO.isPrintSot817,	//是否列印結構型商品推介終止同意書
				overCentRateYN: $scope.inputVO.overCentRateYN,	//超過集中度上限
				isPrintSOT819:		$scope.inputVO.loanFlag //是否列印貸款風險預告書
			}
					
			$scope.sendRecv("SOT712", "fitToGetPDF", "com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO", fitVO,
					function(tota, isError) {
				if (isError) {
					$scope.showErrorMsg(totas[0].body.msgData);
				}
			});
		}
});