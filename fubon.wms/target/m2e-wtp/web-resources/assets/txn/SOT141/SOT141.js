/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT141Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, $interval, getParameter, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT141Controller";
		
		
		
		getParameter.XML(["SOT.CUST_TYPE", "SOT.ASSET_TRADE_SUB_TYPE", "SOT.TRADE_DATE_TYPE", "COMMON.YES_NO", "SOT.EBANK_PRTDOC_URL", "SOT.CHG_PRTDOC_URL", "SOT.BARGAIN_STATUS","SOT.TRANSFER_TYPE","SOT.SPEC_CUSTOMER"], function(totas) {
			if (totas) {
				// mapping 來行人員  參數設定SOT.CUST_TYPE
				$scope.mappingSet['SOT.CUST_TYPE'] = totas.data[totas.key.indexOf('SOT.CUST_TYPE')];
				//信託型態 
		        $scope.mappingSet['SOT.ASSET_TRADE_SUB_TYPE'] = totas.data[totas.key.indexOf('SOT.ASSET_TRADE_SUB_TYPE')];
		        //交易日期
		        $scope.mappingSet['SOT.TRADE_DATE_TYPE'] = totas.data[totas.key.indexOf('SOT.TRADE_DATE_TYPE')];
		        //YES;NO
		        $scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
                //轉換方式
		        $scope.mappingSet['SOT.TRANSFER_TYPE'] = totas.data[totas.key.indexOf('SOT.TRANSFER_TYPE')]; 
		        $scope.mappingSet['SOT.SPEC_CUSTOMER'] = totas.data[totas.key.indexOf('SOT.SPEC_CUSTOMER')];//客戶註記
			}
		});
		
		$scope.init = function() {
			$scope.goOPDisabled = false;	//傳送OP按鈕，避免連續點擊
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
					debitAcct: '', 								//扣款帳號
					custProType: '',                            //專業投資人類型1：大專投 2：小專投
					custType: 'CUST', 							//來行人員
					agentID: '', 								//代理人ID
					agentName: '',
					
					narratorID: projInfoService.getUserID(),	//解說人員ID
					narratorName: projInfoService.getUserName(),//解說人員姓名
					
					bargainFeeFlag: ''							//議價狀態
			};
		};
		$scope.init();
		
		$scope.inputVO.tradeSEQ = $scope.connector('get', 'SOTTradeSEQ');
		$scope.connector('set', 'SOTTradeSEQ', null);//避免跨交易返回本頁殘值
		
		
		$scope.noCallCustQuery = function () {
        	var deferred = $q.defer();
        	
			$scope.sendRecv("SOT140", "query", "com.systex.jbranch.app.server.fps.sot140.SOT140InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.mainList.length == 0) {
								$scope.connector('set', "SOTTradeSEQ", null);
								$scope.connector('set', "SOTCarSEQ", null);
								if ($scope.fromFPS) {
									// from FPS_SOT.js
									$scope.setSOTurl('assets/txn/SOT140/SOT140.html');
								} else {
									$rootScope.menuItemInfo.url = "assets/txn/SOT140/SOT140.html";
								}								
								return;
							}
							console.log(tota[0].body);
							$scope.inputVO.custID = tota[0].body.mainList[0].CUST_ID;						//客戶ID
							$scope.inputVO.custName = tota[0].body.mainList[0].CUST_NAME;	 
							$scope.IS_REC_NEEDED =  tota[0].body.mainList[0].IS_REC_NEEDED;
							$scope.inputVO.kycLevel = tota[0].body.mainList[0].KYC_LV; 						//KYC等級
							$scope.inputVO.kycDueDate = $scope.toJsDate(tota[0].body.mainList[0].KYC_DUE_DATE);	 			//KYC效期
							$scope.inputVO.profInvestorYN = tota[0].body.mainList[0].PROF_INVESTOR_YN;	 	//是否為專業投資人
							$scope.inputVO.piDueDate = $scope.toJsDate(tota[0].body.mainList[0].PI_DUE_DATE);	 			//專業投資人效期
							$scope.inputVO.custRemarks = tota[0].body.mainList[0].CUST_REMARKS;	 			//客戶註記
							$scope.inputVO.isOBU = tota[0].body.mainList[0].IS_OBU;	 						//是否為OBU客戶
							$scope.inputVO.isAgreeProdAdv = tota[0].body.mainList[0].IS_AGREE_PROD_ADV;	 	//同意投資商品諮詢服務
							$scope.inputVO.piRemark = tota[0].body.mainList[0].PI_REMARK;	 	            //專業投資人註記
							$scope.inputVO.bargainDueDate = tota[0].body.mainList[0].BARGAIN_DUE_DATE;		//期間議價效期
							$scope.inputVO.bargainFeeFlag = tota[0].body.mainList[0].BARGAIN_FEE_FLAG;		//議價狀態
							$scope.inputVO.tradeStatus = tota[0].body.mainList[0].TRADE_STATUS;				//交易狀態
														
							$scope.inputVO.branchNbr = tota[0].body.mainList[0].BRANCH_NBR;		//交易分行代碼
							$scope.inputVO.outProdID = tota[0].body.carList[0].OUT_PROD_ID;		//轉出標的代碼
							
							$scope.mainList = tota[0].body.mainList;
							$scope.carList = tota[0].body.carList;
							$scope.outputVO = tota[0].body;
							
							$scope.inputVO.narratorID = tota[0].body.carList[0].NARRATOR_ID;		//解說人員ID
							$scope.inputVO.narratorName = tota[0].body.carList[0].NARRATOR_NAME;	//解說人員姓名
							
							deferred.resolve("success");
						}
			});
			
			return deferred.promise;
		};
		
		
		
		$scope.getSOTCustInfoCT = function () {
			$scope.sendRecv("SOT140", "getSOTCustInfoCT", "com.systex.jbranch.app.server.fps.sot140.SOT140InputVO", {'tradeSEQ':$scope.inputVO.tradeSEQ,'custID':$scope.inputVO.custID, 'prodType':1, 'tradeType':3},
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
			if (row && row.length == 1) {
				txtMsg = $filter('i18n')('ehl_02_common_005');
			} else {
				txtMsg = $filter('i18n')('ehl_02_common_006');
			}
			$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
				$scope.sendRecv("SOT140", "delProd", "com.systex.jbranch.app.server.fps.sot140.SOT140InputVO", {tradeSEQ : $scope.inputVO.tradeSEQ, carSEQ: row.SEQ_NO},
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
				$scope.setSOTurl('assets/txn/SOT140/SOT140.html');
			} else {
				$rootScope.menuItemInfo.url = "assets/txn/SOT140/SOT140.html";
			}
		};
		
		//TODO   檢核錄音序號 
		$scope.validateRecSeq = function() {			
			if($scope.inputVO.recSEQ == ''){
				$scope.recSeqFlag = $scope.recSeqFlagOrg;
				return;
			}
			/* var SOT712InputVO = {'tellerID': $scope.carList[0].NARRATOR_ID ,'recSeq': $scope.inputVO.recSeq ,'custID': $scope.inputVO.custID ,'branchNbr': 'TODO' ,'prodType': 'NF' ,'prodID': null ,'tradeType': 3 };
			 */
			//錄音檢核除了 SI/SN 一定要給商品 PROD_ID 以外；其他 3 種，不需要給 PROD_ID~
			var sot712InputVO ={'custID':$scope.inputVO.custID,
					'branchNbr' : $scope.inputVO.branchNbr,
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
					$scope.setSOTurl('assets/txn/SOT140/SOT140.html');
				} else {
					$rootScope.menuItemInfo.url = "assets/txn/SOT140/SOT140.html";
				}
			}
		};
		$scope.query();
		
		//傳送OP交易  ; 產生基金定期(不)定額申購申請書表單
		$scope.do_goOP = function () {
			$scope.noCallCustQuery();
			if ($scope.inputVO.bargainFeeFlag == null || $scope.inputVO.bargainFeeFlag == '2') {
				// 檢核手續費優惠申請皆簽核完畢後，才可進行傳送OP交易及列印表單鍵   (批號產生)
				$scope.sendRecv("SOT712", "updateBatchSeq", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", 
					{'prodType': 'NF', 
					 'tradeType':3 ,
					 'tradeSeq': $scope.inputVO.tradeSEQ}, 
					function(tota, isError) {
						 if (!isError) {
							 $scope.sendRecv("SOT140", "goOP", "com.systex.jbranch.app.server.fps.sot140.SOT140InputVO", $scope.inputVO,
								function(tota, isError) {
									if (!isError) {
										if (tota[0].body.errorMsg != '' && tota[0].body.errorMsg != null) {
											$scope.showErrorMsg(tota[0].body.errorMsg);
										} else {
											$scope.showMsg("ehl_02_SOT_002"); 
											$scope.printReport();//基金_轉換資料輸入及適配 申請書表單 
											 
											$scope.enterPage();													
//											var txtMsg = $filter('i18n')('ehl_01_SOT_008');
//											$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
//												$scope.connector('set', "SOTTradeSEQ", null);
//												$scope.connector('set', "SOTCarSEQ", null);
//												$rootScope.menuItemInfo.url = "assets/txn/SOT140/SOT140.html";
//								            });												
											return;
										}
									}
							 });
						 }
				});
			}
		};
		
		$scope.goOP = function () {
			$scope.goOPDisabled = true;
			$timeout(function() {
				$scope.do_goOP(); 
				$scope.goOPDisabled = false;
			}, 1000);
		}
		
		//列印申請書表單
		$scope.printReport = function() {
			//查詢是否為"後收型基金"
			$scope.isBackend = '';
			$scope.sendRecv("SOT140", "isBackend", "com.systex.jbranch.app.server.fps.sot140.SOT140InputVO", {'outProdID': $scope.carList[0].OUT_PROD_ID},
					function(tota1, isError) {
						if (!isError) {
							$scope.isBackend = tota1[0].body.prodDTL[0].IS_BACKEND;
							
							var fitVO = {
									caseCode  : 	1, 							//case1 下單
									custId    :		$scope.inputVO.custID,		//客戶ID
									prdType   :     1,							//商品類別 : 基金
									tradeSeq  : 	$scope.inputVO.tradeSEQ, 	//交易序號
									tradeType :		3,							//基金交易類別 : 轉換
									isbBackend:		$scope.isBackend			//是否為後收型基金(Y/N)
								}
								
								$scope.sendRecv("SOT712", "fitToGetPDF", "com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO", fitVO,
										function(tota, isError) {
											if (isError) {
												$scope.showErrorMsg(totas[0].body.msgData);
											}
								});
						}
			});
		}
		
		 
		
		// new
		$scope.newTrade = function() {
			$scope.connector('set','SOTTradeSEQ', null);
			$scope.connector('set','SOTCarSEQ', null);
			if ($scope.fromFPS) {
				// from FPS_SOT.js
				$scope.setSOTurl('assets/txn/SOT140/SOT140.html');
			} else {
				$rootScope.menuItemInfo.url = "assets/txn/SOT140/SOT140.html";
			}
		};
});