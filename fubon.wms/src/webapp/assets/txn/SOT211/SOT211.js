/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT211Controller',
	function($rootScope, $scope, $controller, socketService,sysInfoService, ngDialog, projInfoService, $q, $confirm, $filter, $interval, getParameter, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT211Controller";
		
		// filter
		getParameter.XML(["SOT.CUST_TYPE", "SOT.EBANK_PRTDOC_URL", "SOT.QUOTE", "SOT.EBANK_PRTDOC_URL", "SOT.CHG_PRTDOC_URL", "SOT.ENTRUST_TYPE", "SOT.BARGAIN_STATUS","SOT.SPEC_CUSTOMER"], function(totas) {
			if (totas) {
				$scope.mappingSet['SOT.CUST_TYPE'] = totas.data[totas.key.indexOf('SOT.CUST_TYPE')];
				$scope.mappingSet['SOT.EBANK_PRTDOC_URL'] = totas.data[totas.key.indexOf('SOT.EBANK_PRTDOC_URL')];
				$scope.quote = totas.data[totas.key.indexOf('SOT.QUOTE')][0].LABEL;
				$scope.eBankPredocURL = totas.data[totas.key.indexOf('SOT.EBANK_PRTDOC_URL')][0].LABEL;
				$scope.chgPrtdocURL = totas.data[totas.key.indexOf('SOT.CHG_PRTDOC_URL')][0].LABEL;
				$scope.mappingSet['SOT.ENTRUST_TYPE'] = totas.data[totas.key.indexOf('SOT.ENTRUST_TYPE')];
				$scope.mappingSet['SOT.BARGAIN_STATUS'] = totas.data[totas.key.indexOf('SOT.BARGAIN_STATUS')];
				$scope.mappingSet['SOT.SPEC_CUSTOMER'] = totas.data[totas.key.indexOf('SOT.SPEC_CUSTOMER')];//客戶註記
			}
		});

        
        $scope.init = function() {
        	$scope.cartList = [];
        	
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
					plNotifyWays: '',							//停損停利通知方式
					takeProfitPerc: undefined,					//停利點
					stopLossPerc: undefined,					//停損點
					debitAcct: '', 								//扣款帳號
					trustAcct: '', 								//信託帳號
					creditAcct: '',								//收益入帳帳號
					
					custType: 'CUST', 							//來行人員
					agentID: '', 								//代理人ID
					agentName: '',
					
					narratorID: projInfoService.getUserID(),	//解說人員ID
					narratorName: projInfoService.getUserName(),//解說人員姓名
					
					bargainFeeFlag: '',							//議價狀態
					goOPDisabled: false,					    //傳送OP按鈕，避免連續點擊
					loanFlag: 'N'                               //90天貸款註記(Y/N)
			};
			$scope.inputVO.tradeSEQ = $scope.connector('get', 'SOTTradeSEQ');
	        $scope.connector('set', 'SOTTradeSEQ', null);
		};
		$scope.init();
		
        
        
        $scope.noCallCustQuery = function () {
        	var deferred = $q.defer();
        	
			$scope.sendRecv("SOT210", "query", "com.systex.jbranch.app.server.fps.sot210.SOT210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.mainList.length == 0) {
								$scope.connector('set', "SOTTradeSEQ", null);
								$scope.connector('set', "SOTCarSEQ", null);
								if ($scope.fromFPS) {
									// from FPS_SOT.js
									$scope.setSOTurl('assets/txn/SOT210/SOT210.html');
								} else if($scope.inputVO.trustTS == "M") {
									$rootScope.menuItemInfo.url = "assets/txn/SOT212/SOT212.html";
								} else {
									$rootScope.menuItemInfo.url = "assets/txn/SOT210/SOT210.html";
								}								
								return;
							}
							$scope.inputVO.custID = tota[0].body.mainList[0].CUST_ID;						//客戶ID
							$scope.inputVO.custName = tota[0].body.mainList[0].CUST_NAME;	 

							$scope.inputVO.kycLV = tota[0].body.mainList[0].KYC_LV;	 						//KYC等級
							$scope.inputVO.kycDueDate = $scope.toJsDate(tota[0].body.mainList[0].KYC_DUE_DATE);	 			//KYC效期
							$scope.inputVO.profInvestorYN = tota[0].body.mainList[0].PROF_INVESTOR_YN;	 	//是否為專業投資人
							$scope.inputVO.piDueDate = $scope.toJsDate(tota[0].body.mainList[0].PI_DUE_DATE);	 			//專業投資人效期
							$scope.inputVO.custRemarks = tota[0].body.mainList[0].CUST_REMARKS;	 			//客戶註記
							$scope.inputVO.isOBU = tota[0].body.mainList[0].IS_OBU;	 						//是否為OBU客戶
							$scope.inputVO.isAgreeProdAdv = tota[0].body.mainList[0].IS_AGREE_PROD_ADV;	 	//同意投資商品諮詢服務
							$scope.inputVO.bargainDueDate = $scope.toJsDate(tota[0].body.mainList[0].BARGAIN_DUE_DATE);		//期間議價效期
							$scope.inputVO.bargainFeeFlag = tota[0].body.mainList[0].BARGAIN_FEE_FLAG;		//議價狀態
							$scope.inputVO.tradeStatus = tota[0].body.mainList[0].TRADE_STATUS;				//交易狀態
							$scope.isRecNeeded =tota[0].body.mainList[0].IS_REC_NEEDED;  //是否需錄音序號
							$scope.inputVO.loanFlag = tota[0].body.mainList[0].FLAG_NUMBER;				        //90天貸款註記(Y/N)
							
							// 金錢信託
							$scope.inputVO.trustTS = tota[0].body.mainList[0].TRUST_TRADE_TYPE;					//信託交易類別
							$scope.inputVO.contractID = tota[0].body.carList[0].CONTRACT_ID;					//契約編號
							$scope.inputVO.trustPeopNum = tota[0].body.carList[0].TRUST_PEOP_NUM;				//是否為多委託人契約 Y=是/N=否
							
							$scope.mainList = tota[0].body.mainList;
							$scope.cartList = tota[0].body.carList;
							$scope.outputVO = tota[0].body;
							
							$scope.inputVO.narratorID = tota[0].body.carList[0].NARRATOR_ID;		//解說人員ID
							$scope.inputVO.narratorName = tota[0].body.carList[0].NARRATOR_NAME;	//解說人員姓名
							$scope.setApplyBargainList(tota[0].body.carList);//設定議價狀態清單
							deferred.resolve("success");
						}
			});
			
			return deferred.promise;
		};
		
		//設定議價狀態清單
		$scope.setApplyBargainList = function (carList) {
			$scope.applyBargainList = [];
//			$scope.applyBargainListTime = undefined;
			
			angular.forEach(carList, function(row){
				if (row.FEE_TYPE == 'A') { //A申請議價
					var applyStatus={
						PCH_PROD_ID:row.PROD_ID,
						PROD_NAME:row.PROD_NAME,
					    FEE_RATE:row.FEE_RATE,
					    BARGAIN_STATUS:row.BARGAIN_STATUS,
					    BARGAIN_APPLY_SEQ:row.BARGAIN_APPLY_SEQ
					};
					$scope.applyBargainList.push(applyStatus);
//					$scope.applyBargainListTime = $filter('date')(new Date(),'yyyy-MM-dd hh:mm:ss');
				}
			});
		}
		//查詢議價狀態
		$scope.queryApplyBargainStatus = function () {
        	var deferred = $q.defer(); 
        	 
        	if($scope.mainList[0].BARGAIN_FEE_FLAG=='1'){
				$scope.sendRecv("SOT210", "query", "com.systex.jbranch.app.server.fps.sot210.SOT210InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								//$scope.showMsg("已查詢手續費優惠申請");
								$scope.mainList = tota[0].body.mainList;
								$scope.inputVO.bargainFeeFlag = $scope.mainList[0].BARGAIN_FEE_FLAG;		//議價狀態
								$scope.cartList = tota[0].body.carList;//SOT211.html用cartList而非carList注意
								$scope.setApplyBargainList(tota[0].body.carList);//設定議價狀態清單
								deferred.resolve("success");
							}
				});
        	}
			return deferred.promise;
		};

		//定時查詢議價狀態
//		setInterval(function() {
//			$scope.queryApplyBargainStatus();
//	    }, 15000); //15000 =15秒
		
		$scope.getSOTCustInfoCT = function () {
			$scope.sendRecv("SOT210", "getSOTCustInfoCT", "com.systex.jbranch.app.server.fps.sot210.SOT210InputVO", {'custID':$scope.inputVO.custID, 'prodType':2, 'tradeType':1, 'trustTS':$scope.inputVO.trustTS},
					function(totaCT, isError) {
						if (!isError) {
							console.log(totaCT[0].body);
							$scope.isFirstTrade = (totaCT[0].body.isFirstTrade == "Y" && $scope.inputVO.custID.length ==10 ? $filter('i18n')('ehl_02_SOT_003') : ""); 
							$scope.ageUnder70Flag = (totaCT[0].body.ageUnder70Flag == "N" ? $filter('i18n')('ehl_02_SOT_004') : "");
							$scope.eduJrFlag = (totaCT[0].body.eduJrFlag == "N" ? $filter('i18n')('ehl_02_SOT_005') : ""); ;
							$scope.healthFlag = (totaCT[0].body.healthFlag == "N" ? $filter('i18n')('ehl_02_SOT_006') : ""); 
//							$scope.recSeqFlagOrg = (totaCT[0].body.isFirstTrade == "Y" || totaCT[0].body.custRemarks == "Y" ? "Y" : "N");     //是否弱勢或首購客戶 控制<傳送OP交易及列印表單>與<網銀快速下單>按鈕
							$scope.recSeqFlagOrg = $scope.mainList[0].IS_REC_NEEDED;
							$scope.recSeqFlag = $scope.recSeqFlagOrg;
							if($scope.inputVO.custID.length == 10)
								$scope.custAge = (totaCT[0].body.custAge < 18 ? $filter('i18n')('ehl_01_sot510_001') : ""); //委託人未成年，請查詢印鑑備註訊息或法代同意書內容
							
							return;
						}
			});
		};
		
		$scope.enterPage = function () {
			$scope.noCallCustQuery().then(function(data) {
				$scope.getSOTCustInfoCT();
			});
		};
		$scope.enterPage();
		
		$scope.delCar = function(row) {
			var txtMsg = "";
			if ($scope.cartList.length == 1) {
				txtMsg = $filter('i18n')('ehl_02_common_005');
			} else {
				txtMsg = $filter('i18n')('ehl_02_common_006');
			}
			
			$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
				$scope.sendRecv("SOT210", "delProd", "com.systex.jbranch.app.server.fps.sot210.SOT210InputVO", {tradeSEQ : $scope.inputVO.tradeSEQ, carSEQ: row.SEQ_NO},
						function(tota, isError) {
							if (!isError) {
								$scope.noCallCustQuery();
								
								return;
							}
				});
            });
		};
		
		//產生報表
		$scope.printReport = function(){
			var fitVO = {
				caseCode : 		1, 							//case1 下單
				custId   :		$scope.inputVO.custID,		//客戶ID
				prdType  :      2,							//商品類別 : 海外ETF/股票
				tradeSeq : 		$scope.inputVO.tradeSEQ, 	//交易序號
				tradeSubType:	1,							//交易類型 : 申購
				tradeTS  :      $scope.inputVO.trustTS,			//S=特金,M=金錢信託
				isPrintSOT819:		$scope.inputVO.loanFlag //是否列印貸款風險預告書
			}
				
			$scope.sendRecv("SOT712", "fitToGetPDF", "com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO", fitVO,
					function(tota, isError) {
				if (isError) {
					$scope.showErrorMsg(totas[0].body.msgData);
				}
			});
		};
		
		$scope.goPage = function(row) {
			$scope.connector('set', "SOTTradeSEQ", row.TRADE_SEQ);
			$scope.connector('set', "SOTCarSEQ", row.SEQ_NO);
			if ($scope.fromFPS) {
				// from FPS_SOT.js
				$scope.setSOTurl('assets/txn/SOT210/SOT210.html');
			} else if($scope.inputVO.trustTS == "M") {
				$rootScope.menuItemInfo.url = "assets/txn/SOT212/SOT212.html";
			} else {
				$rootScope.menuItemInfo.url = "assets/txn/SOT210/SOT210.html";
			}
		};
		
		//錄音序號檢核
		//prodType傳送STOCK，因為錄音主機會將海外ETF與STOCK的產品類別皆歸類到"STOCK"
		$scope.validateRecSeq = function(){
			if($scope.inputVO.recSEQ == ''){
				$scope.recSeqFlag = $scope.recSeqFlagOrg;
				return;
			}
			if($scope.inputVO.recSEQ){
				$scope.sendRecv("SOT712", 
						"validateRecseq", 
						"com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", 
						{'custID':$scope.inputVO.custID, 
					 	 'prodType': 'STOCK', 
					 	 'recSeq':$scope.inputVO.recSEQ,
					 	 'branchNbr':sysInfoService.getBranchID()},
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
					}else{
						$scope.showErrorMsg("ehl_02_common_007");
						$scope.inputVO.recSEQ='';
						$scope.recSeqFlag = $scope.recSeqFlagOrg;
					}
				});
			}
		}
		
		//傳送OP交易，避免按鈕被連續點擊造成錯誤，先disable按鈕並$timeout
		$scope.goOP = function () {
			$scope.inputVO.goOPDisabled = true;
			
			$timeout(function() {
				$scope.do_goOP(); 
				$scope.inputVO.goOPDisabled = false;}
			, 1000);			
		}
		
		//傳送OP交易  ; 產生ETF申購申請書表單
		$scope.do_goOP = function () {
			//$scope.noCallCustQuery();
			if($scope.isRecNeeded == 'Y' && !$scope.inputVO.recSEQ){
				$scope.showErrorMsg("ehl_02_common_007");
				return;
			}
			if ($scope.inputVO.bargainFeeFlag == null || $scope.inputVO.bargainFeeFlag == '2') {
				// 檢核手續費優惠申請皆簽核完畢後，才可進行傳送OP交易及列印表單鍵
				$scope.sendRecv("SOT712", "updateBatchSeq", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {'prodType': 'ETF', 
					   																								   'tradeSeq': $scope.inputVO.tradeSEQ},
					function(tota, isError) {
						if (!isError) {
							$scope.sendRecv("SOT210", "goOP", "com.systex.jbranch.app.server.fps.sot210.SOT210InputVO", {'custID': $scope.inputVO.custID, 
									   																					 'tradeSEQ': $scope.inputVO.tradeSEQ,
									   																					 'recSEQ':$scope.inputVO.recSEQ,
									   																					 'trustTS':$scope.inputVO.trustTS
									   																					 },
									function(tota, isError) {
										if (!isError) {
											if (tota[0].body.errorMsg != '' && tota[0].body.errorMsg != null) {
												$scope.showErrorMsg(tota[0].body.errorMsg);
											} else {
												$scope.showMsg("ehl_02_SOT_002");
												$scope.enterPage();
												$scope.printReport();//產生報表
//												$scope.printReport().then(function(data) {
//														var txtMsg = $filter('i18n')('ehl_01_SOT_008');
//														$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
//														$scope.connector('set', "SOT210_tradeSEQ", null);
//														$scope.connector('set', "SOT210_carSEQ", null);
//														$rootScope.menuItemInfo.url = "assets/txn/SOT210/SOT210.html";
//										            });
//												});
												return;
											}
										}
							});
						}
				});
			}
		};
		
		// new
		$scope.newTrade = function() {
			if ($scope.fromFPS) {
				// from FPS_SOT.js
				$scope.setSOTurl('assets/txn/SOT210/SOT210.html');
			} else if($scope.inputVO.trustTS == "M") {
				$rootScope.menuItemInfo.url = "assets/txn/SOT212/SOT212.html";
			} else {
				$rootScope.menuItemInfo.url = "assets/txn/SOT210/SOT210.html";
			}			
		};
});