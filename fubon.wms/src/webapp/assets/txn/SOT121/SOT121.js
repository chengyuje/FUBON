/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT121Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, $interval, getParameter, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT121Controller";
		
		getParameter.XML(["SOT.CUST_TYPE", "SOT.CHANGE_TRADE_SUB_TYPE", "SOT.TRADE_DATE_TYPE", "COMMON.YES_NO", "SOT.EBANK_PRTDOC_URL", "SOT.CHG_PRTDOC_URL", "SOT.BARGAIN_STATUS", "SOT.TRUST_CURR_TYPE", "SOT.PROSPECTUS_TYPE","SOT.SPEC_CUSTOMER","SOT.NEW_CHANGE_TRADE_SUB_TYPE"], function(totas) {
			if (totas) {
				// mapping 來行人員  參數設定SOT.CUST_TYPE
				$scope.mappingSet['SOT.CUST_TYPE'] = totas.data[totas.key.indexOf('SOT.CUST_TYPE')];
				//信託型態 
		        $scope.mappingSet['SOT.NEW_CHANGE_TRADE_SUB_TYPE'] = totas.data[totas.key.indexOf('SOT.NEW_CHANGE_TRADE_SUB_TYPE')];
		        //交易日期
		        $scope.mappingSet['SOT.TRADE_DATE_TYPE'] = totas.data[totas.key.indexOf('SOT.TRADE_DATE_TYPE')];
		        //YES;NO
		        $scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
				$scope.mappingSet['SOT.EBANK_PRTDOC_URL'] = totas.data[totas.key.indexOf('SOT.EBANK_PRTDOC_URL')];
				
				//公開說明書選項
				$scope.mappingSet['SOT.PROSPECTUS_TYPE'] = totas.data[totas.key.indexOf('SOT.PROSPECTUS_TYPE')];
				
				//網行銀服務申請書列印
				$scope.eBankPredocURL = totas.data[totas.key.indexOf('SOT.EBANK_PRTDOC_URL')][0].LABEL;
				//變更申請書列印
				$scope.chgPrtdocURL = totas.data[totas.key.indexOf('SOT.CHG_PRTDOC_URL')][0].LABEL;
				//議價狀態 (若為申請議價) 
				$scope.mappingSet['SOT.BARGAIN_STATUS'] = totas.data[totas.key.indexOf('SOT.BARGAIN_STATUS')];
				
				//自動換匯
		        $scope.mappingSet['SOT_isAutoCx']=[{"DATA":"Y","LABEL":"是"}, {"DATA":"N","LABEL":"否"}];
		        //信託幣別
		        $scope.mappingSet['SOT.TRUST_CURR_TYPE'] = totas.data[totas.key.indexOf('SOT.TRUST_CURR_TYPE')];
		        $scope.mappingSet['SOT.SPEC_CUSTOMER'] = totas.data[totas.key.indexOf('SOT.SPEC_CUSTOMER')];//客戶註記

			}
		});


        $scope.init = function() {
        	$scope.carList = [];
        	
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
					piRemark: '',                               //專業投資人註記
					plNotifyWays: '',							//停損停利通知方式
					takeProfitPerc: undefined,					//停利點
					stopLossPerc: undefined,					//停損點
					debitAcct: '', 								//扣款帳號
					trustAcct: '', 								//信託帳號
					creditAcct: '',								//收益入帳帳號
					custProType: '',                            //專業投資人類型1：大專投 2：小專投
					custType: 'CUST', 							//來行人員
					agentID: '', 								//代理人ID
					agentName: '',
					
					narratorID: projInfoService.getUserID(),	//解說人員ID
					narratorName: projInfoService.getUserName(),//解說人員姓名
					
					bargainFeeFlag: '',							//議價狀態
					goOPDisabled: false,						//傳送OP按鈕，避免連續點擊
					goBankDisabled: false,						//網銀快速下單按鈕，避免連續點擊
					trustTS: 'S',                               //M:金錢信託  S:特金交易--預設特金如果契約編號有值則為SOT112交易為金錢信託
					isWeb: 'N'									//臨櫃交易N, 快速申購Y
			};
		};
		$scope.init();

        $scope.inputVO.tradeSEQ = $scope.connector('get', 'SOTTradeSEQ');
        $scope.connector('set', 'SOTTradeSEQ', null);//避免跨交易返回本頁殘值
        
        $scope.noCallCustQuery = function () {
        	var deferred = $q.defer();
        	
			$scope.sendRecv("SOT120", "query", "com.systex.jbranch.app.server.fps.sot120.SOT120InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.mainList.length == 0) {
								$scope.connector('set', "SOTTradeSEQ", null);
								$scope.connector('set', "SOTCarSEQ", null);
								if ($scope.fromFPS) {
									// from FPS_SOT.js
									if($scope.inputVO.trustTS == 'S'){
										$scope.setSOTurl('assets/txn/SOT120/SOT120.html');
									}else{
										$scope.setSOTurl('assets/txn/SOT122/SOT122.html');
									}									
								} else {
									if($scope.inputVO.trustTS == 'S'){
										$rootScope.menuItemInfo.url = "assets/txn/SOT120/SOT120.html";
									}else{
										$rootScope.menuItemInfo.url = "assets/txn/SOT122/SOT122.html";
									}									
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
							$scope.inputVO.piRemark = tota[0].body.mainList[0].PI_REMARK;	 	            //專業投資人註記
							$scope.inputVO.bargainDueDate = $scope.toJsDate(tota[0].body.mainList[0].BARGAIN_DUE_DATE);		//期間議價效期
							$scope.inputVO.bargainFeeFlag = tota[0].body.mainList[0].BARGAIN_FEE_FLAG;		//議價狀態
							$scope.inputVO.tradeStatus = tota[0].body.mainList[0].TRADE_STATUS;				//交易狀態
							$scope.inputVO.isWeb = tota[0].body.mainList[0].IS_WEB;							//是否為網銀快速申購(Y/N)
							
							$scope.mainList = tota[0].body.mainList;
							$scope.carList = tota[0].body.carList;
							$scope.inputVO.debitAcct = $scope.carList[0].DEBIT_ACCT;  // 供網銀取得戶名電文需求欄位
							$scope.outputVO = tota[0].body;
							
							$scope.feeTypeDisabled = false;
							angular.forEach($scope.carList, function(row){
								if (row.FEE_TYPE == 'A') { //A申請議價
									$scope.feeTypeDisabled = true;
								}
							});
							
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
				if (row.FEE_TYPE == 'A' & !row.falseBargin) { //A申請議價
					var applyStatus={
						PCH_PROD_ID:row.PROD_ID,
						PROD_NAME:row.PROD_NAME,
						TRADE_SUB_TYPE:row.TRADE_SUB_TYPE,
					    FEE_RATE:row.FEE_RATE,
					    FEE_RATE_L:row.FEE_RATE_L,
					    FEE_RATE_M:row.FEE_RATE_M,
					    FEE_RATE_H:row.FEE_RATE_H,
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
        	 de
        	if($scope.mainList[0].BARGAIN_FEE_FLAG=='1'){
				$scope.sendRecv("SOT120", "query", "com.systex.jbranch.app.server.fps.sot120.SOT120InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								//$scope.showMsg("已查詢手續費優惠申請");
								$scope.mainList = tota[0].body.mainList;
								$scope.inputVO.bargainFeeFlag = $scope.mainList[0].BARGAIN_FEE_FLAG;		//議價狀態
								$scope.carList = tota[0].body.carList;
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
			$scope.sendRecv("SOT120", "getSOTCustInfoCT", "com.systex.jbranch.app.server.fps.sot120.SOT120InputVO", {'tradeSEQ':$scope.inputVO.tradeSEQ, 'custID':$scope.inputVO.custID, 'prodType':1, 'tradeType':5},
					function(totaCT, isError) {
						if (!isError) {
							$scope.isFirstTrade = (totaCT[0].body.isFirstTrade == "Y" ? $filter('i18n')('ehl_02_SOT_003') : "");     //是否首購
							$scope.ageUnder70Flag = (totaCT[0].body.ageUnder70Flag == "N" ? $filter('i18n')('ehl_02_SOT_004') : ""); //年齡小於70
							$scope.eduJrFlag = (totaCT[0].body.eduJrFlag == "N" ? $filter('i18n')('ehl_02_SOT_005') : "");           //教育程度國中以上(不含國中)
							$scope.healthFlag = (totaCT[0].body.healthFlag == "N" ? $filter('i18n')('ehl_02_SOT_006') : "");         //重大傷病等級
							
							if ($scope.mainList[0].IS_REC_NEEDED == 'N') {
								$scope.recSeqFlagOrg = 'N';
							} else {
//								$scope.recSeqFlagOrg = (totaCT[0].body.isFirstTrade == "Y" || totaCT[0].body.custRemarks == "Y" ? "Y" : "N");     //是否弱勢或首購客戶 控制<傳送OP交易及列印表單>與<網銀快速下單>按鈕
								$scope.recSeqFlagOrg = 'Y';
							}	
							$scope.recSeqFlag = $scope.recSeqFlagOrg;
							
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
			if ($scope.carList.length == 1) {
				txtMsg = $filter('i18n')('ehl_02_common_005');
			} else {
				txtMsg = $filter('i18n')('ehl_02_common_006');
			}
			$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
				$scope.sendRecv("SOT120", "delProd", "com.systex.jbranch.app.server.fps.sot120.SOT120InputVO", {tradeSEQ : $scope.inputVO.tradeSEQ, seqNo: row.SEQ_NO},
						function(tota, isError) {
							if (!isError) {
								$scope.noCallCustQuery();
								
								return;
							}
				});
            });
		};
		
		$scope.goPage = function(row) {
			$scope.connector('set', "SOTTradeSEQ", row.TRADE_SEQ);
			$scope.connector('set', "SOTCarSEQ", row.SEQ_NO);
			if ($scope.fromFPS) {
				// from FPS_SOT.js
				if($scope.inputVO.trustTS == 'S'){
					$scope.setSOTurl('assets/txn/SOT120/SOT120.html');
				}else{
					$scope.setSOTurl('assets/txn/SOT122/SOT122.html');
				}				
			} else {
				if($scope.inputVO.trustTS == 'S'){
					$rootScope.menuItemInfo.url = "assets/txn/SOT120/SOT120.html";
				}else{
					$rootScope.menuItemInfo.url = "assets/txn/SOT122/SOT122.html";
				}				
			}
		};
		
		//TODO   檢核錄音序號 
		$scope.validateRecSeq = function() {
			if($scope.inputVO.recSEQ && $scope.inputVO.recSEQ.length<10){
				$scope.showErrorMsg("錄音序號格式不符");
				return;
			}
			if($scope.inputVO.recSEQ == ''){
				$scope.recSeqFlag = $scope.recSeqFlagOrg;
				return;
			}
			
			/* var SOT712InputVO = {'tellerID': $scope.carList[0].NARRATOR_ID ,'recSeq': $scope.inputVO.recSeq ,'custID': $scope.inputVO.custID ,'branchNbr': 'TODO' ,'prodType': 'NF' ,'prodID': null ,'tradeType': 5 };
			 */
			//錄音檢核除了 SI/SN 一定要給商品 PROD_ID 以外；其他 3 種，不需要給 PROD_ID~
			var sot712InputVO ={'custID':$scope.inputVO.custID,
					'prodType': 'FUND',
					'recSeq':$scope.inputVO.recSEQ
					};
			$scope.sendRecv("SOT712", "validateRecseq", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO",sot712InputVO ,
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
				}
			});
		}; 
		 
		
		$scope.query = function() {
			if($scope.inputVO.tradeSEQ) {
				$scope.noCallCustQuery().then(function(data) {
					$scope.getSOTCustInfoCT();
				});
			} else {
				$scope.connector('set', "SOTTradeSEQ", null);
				if ($scope.fromFPS) {
					// from FPS_SOT.js
					if($scope.inputVO.trustTS == 'S'){
						$scope.setSOTurl('assets/txn/SOT120/SOT120.html');
					}else{
						$scope.setSOTurl('assets/txn/SOT122/SOT122.html');
					}
					
				} else {
					if($scope.inputVO.trustTS == 'S'){
						$rootScope.menuItemInfo.url = "assets/txn/SOT120/SOT120.html";
					}else{
						$rootScope.menuItemInfo.url = "assets/txn/SOT122/SOT122.html";
					}					
				}
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
		
		//傳送OP交易  ; 產生基金定期(不)定額申購申請書表單
		$scope.do_goOP = function () {
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg("ehl_01_common_022");
	    		return;
	    	}
			if(!$scope.inputVO.recSEQ && $scope.mainList[0].IS_REC_NEEDED == 'Y') {
				$scope.showErrorMsg("ehl_01_common_022");
				return;
			}
			
			$scope.noCallCustQuery();
			
			if ($scope.inputVO.bargainFeeFlag == null || $scope.inputVO.bargainFeeFlag == '2') {
				
						// 檢核手續費優惠申請皆簽核完畢後，才可進行傳送OP交易及列印表單鍵   (批號產生)
						$scope.sendRecv("SOT712", "updateBatchSeq", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {'prodType': 'NF', 'tradeType':5 ,
																															   'tradeSeq': $scope.inputVO.tradeSEQ}, 
						function(tota, isError) {
							if (!isError) {
								$scope.sendRecv("SOT120", "goOP", "com.systex.jbranch.app.server.fps.sot120.SOT120InputVO", $scope.inputVO,
										function(tota, isError) {
											if (!isError) {
												if (tota[0].body.errorMsg != '' && tota[0].body.errorMsg != null) {
													$scope.showErrorMsg(tota[0].body.errorMsg);
												} else {
													$scope.showMsg("ehl_02_SOT_002");
													$scope.query();
													$scope.printReport();//產生基金定期(不)定額申購申請書表單
												 
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
			
			$scope.isforeignCurrencyInteger().then(function(data) {			
				if(!data){
					return;
				}
				
				$timeout(function() {
					$scope.do_goBANK(); 
					$scope.inputVO.goBankDisabled = false;}
				, 1000);
				
			});						
		}
		
		//網銀快速下單 
		$scope.do_goBANK = function() {
			$scope.sendRecv("SOT703", "verifyESBWebPurchaseNF", "com.systex.jbranch.app.server.fps.sot703.SOT703InputVO", {
				   'tradeSeq': $scope.inputVO.tradeSEQ,
				   'debitAcct' : $scope.inputVO.debitAcct }, 
			function(tota, isError) { 
					if (!isError) {
						if (tota[0].body.errorMsg != '' && tota[0].body.errorMsg != null) {
							$scope.showErrorMsg(tota[0].body.errorMsg);
						} else { 
							// 檢核手續費優惠申請皆簽核完畢後，才可進行傳送OP交易及列印表單鍵   (批號產生)
							$scope.sendRecv("SOT712", "updateBatchSeq", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {'prodType': 'NF', 'tradeType':5 ,
																																   'tradeSeq': $scope.inputVO.tradeSEQ}, 
								function(tota, isError) {
									if (!isError) {
										  $scope.sendRecv("SOT120", "goBANK", "com.systex.jbranch.app.server.fps.sot120.SOT120InputVO", {
												   'tradeSEQ': $scope.inputVO.tradeSEQ},
										  function(tota, isError) {
											if (!isError) {
												if (tota[0].body.errorMsg != '' && tota[0].body.errorMsg != null) {
												   $scope.showErrorMsg(tota[0].body.errorMsg);
												} else { 
													   $scope.showMsg("ehl_01_SOT_011");//網銀/行銀快速下單成功
													   $scope.query();
													   return;
												}
											 }
										  }); 
									      return;
									}
							}); 
						} 
					}
			});
		}
		
		//列印申請書表單
		$scope.printReport = function() {
			var fitVO = {
				caseCode : 		1, 							//case1 下單
				custId   :		$scope.inputVO.custID,		//客戶ID
				prdType  :      1,							//商品類別 : 基金
				tradeSeq : 		$scope.inputVO.tradeSEQ, 	//交易序號
				tradeType:		5							//基金交易類別 : 定期(不)定額申購
			}
			
			$scope.sendRecv("SOT712", "fitToGetPDF", "com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO", fitVO,
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsg(totas[0].body.msgData);
						}
			});
		}
		 
		
		// new
		$scope.newTrade = function() {
			$scope.connector('set','SOTTradeSEQ', null);
			$scope.connector('set','SOTCarSEQ', null);
			if ($scope.fromFPS) {
				// from FPS_SOT.js
				if($scope.inputVO.trustTS == 'S'){
					$scope.setSOTurl('assets/txn/SOT120/SOT120.html');
				}else{
					$scope.setSOTurl('assets/txn/SOT122/SOT122.html');
				}				
			} else {
				if($scope.inputVO.trustTS == 'S'){
					$rootScope.menuItemInfo.url = "assets/txn/SOT120/SOT120.html";
				}else{
					$rootScope.menuItemInfo.url = "assets/txn/SOT122/SOT122.html";
				}				
			}
		};
		
		$scope.isforeignCurrencyInteger = function() {
			var defered = $q.defer();
			$scope.foreignCurrencyIsInteger = true;
			angular.forEach($scope.carList, function(row){
				if ((row.TRADE_SUB_TYPE == '2') || (row.TRADE_SUB_TYPE == '4')) {
					if (row.TRUST_CURR_TYPE != 'TWD' && !(row.PURCHASE_AMT%1 === 0)) { 
						$scope.showMsg("ehl_01_SOT_017");
						$scope.foreignCurrencyIsInteger = false;
						$scope.inputVO.goBankDisabled = false;
					}		
				}
				
                if ((row.TRADE_SUB_TYPE == '3') || (row.TRADE_SUB_TYPE == '5')) {
                	if (row.TRUST_CURR_TYPE != 'TWD' && !(row.PURCHASE_AMT_L%1 === 0 && row.PURCHASE_AMT_M%1 === 0 && row.PURCHASE_AMT_H%1 === 0)) { 
						$scope.showMsg("ehl_01_SOT_017");
						$scope.foreignCurrencyIsInteger = false;
						$scope.inputVO.goBankDisabled = false;
					}				
				}
			});
			defered.resolve($scope.foreignCurrencyIsInteger);
			return defered.promise;
			
		};
		
});