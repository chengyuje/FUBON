/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT414Controller',
	function($rootScope, $scope, $controller, $confirm, $filter, socketService, ngDialog, projInfoService, $q, getParameter, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT414Controller";
		
		getParameter.XML(["PRD.FCI_CURRENCY", "PRD.FCI_CURRENCY_CODE"], function(totas) {
			if (totas) {
				$scope.mappingSet['PRD.FCI_CURRENCY'] = totas.data[totas.key.indexOf('PRD.FCI_CURRENCY')];
				$scope.mappingSet['PRD.FCI_CURRENCY_CODE'] = totas.data[totas.key.indexOf('PRD.FCI_CURRENCY_CODE')];
			}
		});
		
		$scope.init = function() {
			$scope.inputVO = {
					prodType:'7', //FCI
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
					prodAcct:'',								//組合式商品帳號 	
					custProFlag: '',
					authID: '',									//授權解說人員ID
//					authName: '',								//授權解說人員姓名
					narratorID: projInfoService.getUserID(),	//解說人員ID
					narratorName: projInfoService.getUserName(),//解說人員姓名
					batchSeq: '',								//批號
					
					prodID: '', 								//商品ID
					prodName: '',								//商品名稱
					purchaseAmt: undefined,						//申購面額
					prodCurr: '', 								//計價幣別
					prodRiskLv: '', 							//產品風險等級
					prodMinBuyAmt: undefined, 					//最低申購金額
					prodMinGrdAmt: undefined,					//累進申購面額
					monPeriod: undefined,						//天期
					rmProfee: undefined,						//理專收益率
					targetCurrId: undefined,					//連結標的
					strikePrice: undefined,						//履約價
					ftpRate: undefined,							//FTP RATE
					targetName: undefined,						//連結標的中文
					tradeDate : undefined,						//申購日
					valueDate : undefined, 						//扣款(起息)日
					spotDate : undefined,						//比價日	
					expireDate : undefined,						//到期(入帳)日
					intDates : undefined,						//產品天期
					prdProfeeRate : undefined,					//預估年化收益率(到期比價匯率大於等於履約價)
					lessProfeeRate : undefined, 				//預估年化收益率(到期比價匯率小於履約價)
					prdProfeeAmt : undefined,					//到期比價匯率大於等於履約價
					lessProfeeAmt : undefined, 					//到期比價匯率小於履約價
					
					tradeStatus: '', 							//交易狀態
					recSEQ:'',		  							//錄音序號
					recCode:'',									//錄音代碼
					
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
					goOPDisabled: false						//傳送OP按鈕，避免連續點擊
			};
		};
		$scope.init();
		
		$scope.inputVO.tradeSEQ = $scope.connector('get','SOTTradeSEQ');
		$scope.inputVO.warningMsg = $scope.connector('get', 'SOT414_warningMsg');
//		$scope.inputVO.carSEQ = $scope.connector('get', 'SOTCarSEQ);
		$scope.connector('set', 'SOTTradeSEQ', null);
		$scope.connector('set', 'SOT414_warningMsg', null);
//		$scope.connector('set', 'SOTCarSEQ', null);
		
		//取消
		$scope.delTrade = function () {
			var txtMsg = $filter('i18n')('ehl_02_common_005');
			$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
				$scope.sendRecv("SOT413", "delTrade", "com.systex.jbranch.app.server.fps.sot413.SOT413InputVO",{"tradeSEQ":$scope.inputVO.tradeSEQ},
						function(tota, isError) {
							if (!isError) {
								$scope.newTrade();
							}
				});
            });
		};
		
		//非常規交易
		$scope.getSOTCustInfoCT = function () {
			$scope.sendRecv("SOT410", "getSOTCustInfoCT", "com.systex.jbranch.app.server.fps.sot410.SOT410InputVO", {'tradeSEQ':$scope.inputVO.tradeSEQ, 'custID':$scope.inputVO.custID,'prodType': '7','tradeType':1, 'prodID': $scope.inputVO.prodID},
				function(totaCT, isError) {
					if (!isError) {
						debugger
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
										debugger
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
			$scope.sendRecv("SOT413", "query", "com.systex.jbranch.app.server.fps.sot413.SOT413InputVO",{'tradeSEQ':$scope.inputVO.tradeSEQ},
					function(tota, isError) {
						if (!isError) {
							$scope.carList = tota[0].body.carList;
							$scope.mainList = tota[0].body.mainList;
							if($scope.mainList && $scope.mainList.length != 0) {
								$scope.inputVO.isRecNeeded = $scope.mainList[0].IS_REC_NEEDED;  //是否需錄音序號
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
								if($scope.carList && $scope.carList.length != 0) {
									$scope.inputVO.monType = $scope.carList[0].MON_PERIOD;
									$scope.inputVO.prodName = $scope.carList[0].PROD_NAME;
									$scope.inputVO.prodCurr = $scope.carList[0].PROD_CURR; //幣別
									$scope.inputVO.monPeriod = $scope.carList[0].MON_PERIOD; //天期
									$scope.inputVO.rmProfee = $scope.carList[0].RM_PROFEE; //理專收益率
									$scope.inputVO.prodRiskLv = $scope.carList[0].PROD_RISK_LV; //風險等級
									$scope.inputVO.purchaseAmt = $scope.carList[0].PURCHASE_AMT;	//申購金額
									$scope.inputVO.purchaseAmtTwd = $scope.carList[0].PURCHASE_AMT_TWD;	//申購金額折台
									$scope.inputVO.targetCurrId = $scope.carList[0].TARGET_CURR_ID; //連結標的
									$scope.inputVO.prodMinBuyAmt = $scope.carList[0].PROD_MIN_BUY_AMT; //最低申購金額
									$scope.inputVO.prodMinGrdAmt = $scope.carList[0].PROD_MIN_GRD_AMT; //累進申購金額
									$scope.inputVO.strikePrice = $scope.carList[0].STRIKE_PRICE; //履約價
									$scope.inputVO.targetName = $scope.carList[0].TARGET_NAME; //連結標的中文
									$scope.inputVO.tradeDate = $scope.toJsDate($scope.carList[0].TRADE_DATE);	//申購日
									$scope.inputVO.valueDate = $scope.toJsDate($scope.carList[0].VALUE_DATE);		//扣款(起息)日
									$scope.inputVO.spotDate = $scope.toJsDate($scope.carList[0].SPOT_DATE);			//比價日	
									$scope.inputVO.expireDate = $scope.toJsDate($scope.carList[0].EXPIRE_DATE);		//到期(入帳)日
									$scope.inputVO.intDates = $scope.carList[0].INT_DATES;			//產品天期
									$scope.inputVO.prdProfeeRate = $scope.carList[0].PRD_PROFEE_RATE;	//預估年化收益率(到期比價匯率大於等於履約價)
									$scope.inputVO.lessProfeeRate = $scope.carList[0].LESS_PROFEE_RATE; //預估年化收益率(到期比價匯率小於履約價)
									$scope.inputVO.ftpRate = $scope.carList[0].FTP_RATE; //FTP_RATE
									$scope.inputVO.recCode = $scope.carList[0].REC_CODE; //錄音代碼
									$scope.inputVO.prodAcct = $scope.carList[0].PROD_ACCT;
									$scope.inputVO.debitAcct = $scope.carList[0].DEBIT_ACCT;
									$scope.inputVO.prdProfeeAmt = $scope.carList[0].PRD_PROFEE_AMT;//到期比價匯率大於等於履約價
									$scope.inputVO.lessProfeeAmt = $scope.carList[0].LESS_PROFEE_AMT;//到期比價匯率小於履約價
								 
									$scope.inputVO.authID = $scope.carList[0].AUTH_ID;
//									$scope.getTellerName('authID',$scope.inputVO.authID);		//主管姓名
									$scope.inputVO.narratorID = $scope.carList[0].NARRATOR_ID;
									$scope.inputVO.narratorName = $scope.carList[0].NARRATOR_NAME;
									$scope.inputVO.batchSeq = $scope.carList[0].BATCH_SEQ;
									$scope.inputVO.bossID = $scope.carList[0].BOSS_ID;			//主管員編
									$scope.getTellerName('bossID',$scope.inputVO.bossID);		//主管姓名
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
				$rootScope.menuItemInfo.url = "assets/txn/SOT413/SOT413.html";
			}
		};
 		$scope.query();
		
		//修改
		$scope.goPage = function() {
			$scope.connector('set', "SOTTradeSEQ", $scope.inputVO.tradeSEQ);
			$rootScope.menuItemInfo.url = "assets/txn/SOT413/SOT413.html";
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
			if($scope.inputVO.isRecNeeded == 'Y') {
				if($scope.inputVO.recSEQ == undefined || $scope.inputVO.recSEQ == '' || $scope.inputVO.recSEQ == null) {
					$scope.showErrorMsg("ehl_02_common_007");
					return;
				}
				//檢核錄音序號
				$scope.sendRecv("SOT413", "validateRecseq", "com.systex.jbranch.app.server.fps.sot413.SOT413InputVO", $scope.inputVO,
					function(tota, isError) {
						debugger
						if (!isError) {
							if(!tota[0].body.Recseq){
								$scope.showErrorMsg("ehl_02_common_007");
								$scope.inputVO.recSEQ='';
								return;
							}

							$scope.sendRecv("SOT413", "goOP", "com.systex.jbranch.app.server.fps.sot413.SOT413InputVO", $scope.inputVO,
									function(tota, isError) {
										if (!isError) {
											if (tota[0].body.errorMsg != '' && tota[0].body.errorMsg != null) {
												$scope.showErrorMsg(tota[0].body.errorMsg);
											} else {
												$scope.showMsg("傳送OP成功");
												$scope.query();
												return;
											}
										}
								});
						}
				});
			} else {
				$scope.sendRecv("SOT413", "goOP", "com.systex.jbranch.app.server.fps.sot413.SOT413InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.errorMsg != '' && tota[0].body.errorMsg != null) {
									$scope.showErrorMsg(tota[0].body.errorMsg);
								} else {
									$scope.showMsg("傳送OP成功");
									$scope.query();
									return;
								}
							}
					});
			}
		};
		
		//繼續交易
		$scope.newTrade = function() {
			$scope.connector('set', "SOTTradeSEQ", null);
			$rootScope.menuItemInfo.url = "assets/txn/SOT413/SOT413.html";
		};
		
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
		
		//SI錄音範例分享文件
		$scope.printRecCaseSHR = function() {
			$scope.sendRecv("SOT413", "printRecCaseSHR", "com.systex.jbranch.app.server.fps.sot413.SOT413InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							
						}
				});
		}
});