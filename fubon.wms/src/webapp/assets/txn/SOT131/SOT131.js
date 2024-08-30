/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT131Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, $interval, getParameter, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT131Controller";
		
		// mapping
		$scope.getXML = ['SOT.CUST_TYPE','SOT.REDEEM_TYPE','SOT.ASSET_TRADE_SUB_TYPE','SOT.SPEC_CUSTOMER', 'SOT.BARGAIN_STATUS'];
		 getParameter.XML($scope.getXML, function(totas) {
			 if(len(totas)>0){
				 var tmp = totas;
				 $scope.mappingSet['SOT.CUST_TYPE'] = tmp.data[tmp.key.indexOf('SOT.CUST_TYPE')];  
				 $scope.mappingSet['SOT.REDEEM_TYPE']  = tmp.data[tmp.key.indexOf('SOT.REDEEM_TYPE')];
				 $scope.mappingSet['SOT.ASSET_TRADE_SUB_TYPE']  = tmp.data[tmp.key.indexOf('SOT.ASSET_TRADE_SUB_TYPE')];
				 $scope.mappingSet['SOT.SPEC_CUSTOMER'] = tmp.data[tmp.key.indexOf('SOT.SPEC_CUSTOMER')];//客戶註記
				//議價狀態 (若為申請議價) 
				$scope.mappingSet['SOT.BARGAIN_STATUS'] = totas.data[totas.key.indexOf('SOT.BARGAIN_STATUS')];
			 }
			 
		 });
		// init
		$scope.init = function() {
			$scope.goOPDisabled = false;	//傳送OP按鈕，避免連續點擊
			$scope.nextDis = true;
        	$scope.custDTL = [];
        	$scope.cartList = [];
			$scope.inputVO = {};
			$scope.tradeSEQ = $scope.connector('get', 'SOTTradeSEQ');
			$scope.inputVO.tradeSEQ = $scope.tradeSEQ;
			$scope.connector('set', 'SOTTradeSEQ', null);//避免跨交易返回本頁殘值
			$scope.inputVO.trustTS = 'S'; //M:金錢信託  S:特金交易--預設特金如果契約編號有值則為SOT112交易為金錢信託
		};
		$scope.init();
		
		$scope.noCallCustQuery = function () {
        	var deferred = $q.defer();
        	
        	$scope.sendRecv("SOT130", "query", "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO", {'tradeSEQ': $scope.tradeSEQ,'rdmProdID': $scope.inputVO.rdmProdID},
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.custDTL.length == 0) {
								$scope.connector('set', "SOTTradeSEQ", null);
								$scope.connector('set', "SOTCarSEQ", null);
								if ($scope.fromFPS) {
									// from FPS_SOT.js
									if($scope.inputVO.trustTS == 'S'){
										$scope.setSOTurl('assets/txn/SOT130/SOT130.html');
									}else{
										$scope.setSOTurl('assets/txn/SOT133/SOT133.html');
									}									
								} else {
									if($scope.inputVO.trustTS == 'S'){
										$rootScope.menuItemInfo.url = "assets/txn/SOT130/SOT130.html";
									}else{
										$rootScope.menuItemInfo.url = "assets/txn/SOT133/SOT133.html";
									}
									
								}								
								return deferred.promise;
							}
							$scope.custDTL = tota[0].body.custDTL;
							$scope.inputVO.custType = 'CUST';
							$scope.inputVO.custID = $scope.custDTL[0].CUST_ID;
							$scope.inputVO.custName = $scope.custDTL[0].CUST_NAME;
							$scope.inputVO.proxyCustID = $scope.custDTL[0].AGENT_ID;
							$scope.inputVO.proxyCustName = $scope.custDTL[0].AGENT_NAME;
							$scope.inputVO.kycLevel = $scope.custDTL[0].KYC_LV;
							$scope.inputVO.kycDueDate = $scope.custDTL[0].KYC_DUE_DATE;
							$scope.inputVO.custRemarks = $scope.custDTL[0].CUST_REMARKS;
							$scope.inputVO.outFlag = $scope.custDTL[0].IS_OBU;
							$scope.inputVO.custTxFlag = $scope.custDTL[0].IS_AGREE_PROD_ADV;
							$scope.inputVO.custFeePrdDueDate = $scope.custDTL[0].BARGAIN_DUE_DATE;
							$scope.inputVO.profInvestorYN= $scope.custDTL[0].PROF_INVESTOR_YN;
							$scope.inputVO.piRemark = $scope.custDTL[0].PI_REMARK;	 	            	//專業投資人註記
							$scope.inputVO.bargainFeeFlag = $scope.custDTL[0].BARGAIN_FEE_FLAG;			//議價狀態
							$scope.inputVO.tradeStatus = $scope.custDTL[0].TRADE_STATUS;				//交易狀態
							$scope.inputVO.isAgreeProdAdv = $scope.custDTL[0].IS_AGREE_PROD_ADV;	 	//同意投資商品諮詢服務
							$scope.cartList = tota[0].body.cartList;
							
							$scope.isRePurchase = 'N';
							$scope.feeTypeDisabled = false;
							angular.forEach($scope.cartList, function(row){								
								if (row.IS_RE_PURCHASE == 'Y') {
									$scope.isRePurchase = row.IS_RE_PURCHASE;									
								}
								if (row.FEE_TYPE == 'A') { //A申請議價
									$scope.feeTypeDisabled = true;
								}
							});
							$scope.setApplyBargainList(tota[0].body.cartList);//設定議價狀態清單
							deferred.resolve("success");
							
//							// 錄音序號
//							if ($scope.custDTL[0].REC_SEQ) {
//								$scope.sendRecv("SOT712", "validateRecSeq", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {'recSeq': $scope.custDTL[0].REC_SEQ,'custID': $scope.custDTL[0].CUST_ID,'prodType':'NF'},
//										function(tota, isError) {
//											if (!isError) {
//												alert('xd');
//												return;
//											}
//								});
//							}
//							return;
						}
			});
        	return deferred.promise;
		};
		
		//設定議價狀態清單,SOT131這裡用cartList而非carList注意
		$scope.setApplyBargainList = function (cartList) {
			$scope.applyBargainList = [];
//			$scope.applyBargainListTime = undefined;
			angular.forEach(cartList, function(row){
				if (row.FEE_TYPE == 'A') { //A申請議價
					var applyStatus={
						PCH_PROD_ID:row.PCH_PROD_ID,
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
        	 
        	if($scope.custDTL[0].BARGAIN_FEE_FLAG=='1'){
				$scope.sendRecv("SOT130", "query", "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								//$scope.showMsg("已查詢手續費優惠申請");
								$scope.custDTL = tota[0].body.custDTL;//SOT131這裡用custDTL而非mainList注意
								$scope.inputVO.bargainFeeFlag = $scope.custDTL[0].BARGAIN_FEE_FLAG;		//議價狀態
								$scope.cartList = tota[0].body.cartList;//SOT131這裡用cartList而非carList注意
								$scope.setApplyBargainList(tota[0].body.cartList);//設定議價狀態清單
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
					
		$scope.editCart = function(index) {
			$scope.connector('set', "SOTTradeSEQ", $scope.tradeSEQ);
			$scope.connector('set','SOTCarSEQ', $scope.cartList[index].SEQ_NO);
			if ($scope.fromFPS) {
				// from FPS_SOT.js
				if($scope.inputVO.trustTS == 'S'){
					$scope.setSOTurl('assets/txn/SOT130/SOT130.html');
				}else{
					$scope.setSOTurl('assets/txn/SOT133/SOT133.html');
				}				
			} else {
				if($scope.inputVO.trustTS == 'S'){
					$rootScope.menuItemInfo.url = "assets/txn/SOT130/SOT130.html";
				}else{
					$rootScope.menuItemInfo.url = "assets/txn/SOT133/SOT133.html";
				}				
			}
		};
		
		$scope.delCar =function (seqNo) {
			debugger;
			var txtMsg = "";
			if ($scope.cartList.length == 1) {
				txtMsg = $filter('i18n')('ehl_02_common_005');
			} else {
				txtMsg = $filter('i18n')('ehl_02_common_006');
			}
			$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
				$scope.sendRecv("SOT130", "delProd", "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO", {'type': $scope.type,'tradeSEQ': $scope.tradeSEQ,'rdmProdID': $scope.inputVO.rdmProdID,'seqNo':seqNo},
						function(tota, isError) {
							if (!isError) {
									$scope.noCallCustQuery();
								return;
							}
				});
			});
		};
		
//		$scope.goPage = function(seqNO) {
//			alert('in');
//			$scope.connector('set', "SOTTradeSEQ", $scope.tradeSEQ);
//			$scope.connector('set', "SOTCarSEQ", seqNO);
//			alert($scope.tradeSEQ+seqNO);
////			$rootScope.menuItemInfo.url = "assets/txn/SOT130/SOT130.html";
//		};
		
		$scope.getSOTCustInfoCT = function () {
			$scope.sendRecv("SOT130", "getSOTCustInfoCT", "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO", {'tradeSEQ':$scope.tradeSEQ, 'custID':$scope.inputVO.custID, 'prodType':1, 'tradeType':2},
					function(totaCT, isError) {
						if (!isError) {debugger;
							$scope.isFirstTrade = (totaCT[0].body.isFirstTrade == "Y" ? $filter('i18n')('ehl_02_SOT_003') : ""); 
							$scope.ageUnder70Flag = (totaCT[0].body.ageUnder70Flag == "N" ? $filter('i18n')('ehl_02_SOT_004') : "");
							$scope.eduJrFlag = (totaCT[0].body.eduJrFlag == "N" ? $filter('i18n')('ehl_02_SOT_005') : ""); ;
							$scope.healthFlag = (totaCT[0].body.healthFlag == "N" ? $filter('i18n')('ehl_02_SOT_006') : ""); 
							
							if (!totaCT[0].body.recNeeded) {
								$scope.recSeqFlagOrg = 'N';
							} else {
//								$scope.recSeqFlagOrg = (totaCT[0].body.isFirstTrade == "Y" || totaCT[0].body.custRemarks == "Y" ? "Y" : "N");     //是否弱勢或首購客戶 控制<傳送OP交易及列印表單>與<網銀快速下單>按鈕
								$scope.recSeqFlagOrg = 'Y';
							}	
							$scope.recSeqFlag = $scope.recSeqFlagOrg;
							if ($scope.isRePurchase == 'N') {
								$scope.isRecNeeded = false;
							} else {
								$scope.isRecNeeded =totaCT[0].body.recNeeded;
							}							
//							if($scope.isFirstTrade =="" || $scope.ageUnder70Flag =="" || $scope.eduJrFlag =="" || $scope.healthFlag ==""){
//								$scope.IS_REC_NEEDED="N";
//							}else{
//								$scope.IS_REC_NEEDED="Y";
//							}
							
							return;
						}
			});
		};
		
		//列印申請書表單
		$scope.printReport = function() {
			//查詢是否為"後收型基金"
			$scope.isBackend = '';
			$scope.sendRecv("SOT130", "getBackendConstraintParameter", "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO", {'outProdID': $scope.cartList[0].RDM_PROD_ID},
					function(tota1, isError) {
						if (!isError) {
							$scope.isBackend = tota1[0].body.prodDTL[0].IS_BACKEND;
							
							var fitVO = {
								caseCode : 		1, 							//case1 下單
								custId   :		$scope.inputVO.custID,		//客戶ID
								prdType  :      1,							//商品類別 : 基金
								tradeSeq : 		$scope.inputVO.tradeSEQ, 	//交易序號
								tradeType:		2,							//基金交易類別 : 贖回/贖回再申購
								isbBackend:		$scope.isBackend			//是否為後收型基金(Y/N)
							}
							
							$scope.sendRecv("SOT712", "fitToGetPDF", "com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO", fitVO,
									function(tota, isError) {
										if (isError) {
//											$scope.showErrorMsg(totas[0].body.msgData);
//											$scope.showErrorMsg(tota[0].body.msgData);
											$scope.showErrorMsg(tota.body.msgData);
										}
							});
						}
			});
		}
		
		//TODO   檢核錄音序號 
		$scope.validateRecSeq = function() {
//			if($scope.inputVO.recSEQ && $scope.inputVO.recSEQ.length<10){
//				$scope.showErrorMsg("錄音序號格式不符");
//				return;
//			}
			if($scope.inputVO.recSEQ == ''){
				$scope.recSeqFlag = $scope.recSeqFlagOrg;
				return;
			}
			
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
				$scope.connector('set', "SOT130_tradeSEQ", null);
				if ($scope.fromFPS) {
					// from FPS_SOT.js
					if($scope.inputVO.trustTS == 'S'){
						$scope.setSOTurl('assets/txn/SOT130/SOT130.html');
					}else{
						$scope.setSOTurl('assets/txn/SOT133/SOT133.html');
					}					
				} else {
					if($scope.inputVO.trustTS == 'S'){
						$rootScope.menuItemInfo.url = "assets/txn/SOT130/SOT130.html";
					}else{
						$rootScope.menuItemInfo.url = "assets/txn/SOT133/SOT133.html";
					}					
				}
			}
		};
		$scope.query();
		
		//傳送OP交易
		$scope.do_goOP = function () {
			$scope.noCallCustQuery();
			if ($scope.inputVO.bargainFeeFlag == null || $scope.inputVO.bargainFeeFlag == '2') {
				$scope.inputVO.tradeSeq =  $scope.tradeSEQ;
				$scope.sendRecv("SOT712", "updateBatchSeq", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", 
						{'prodType': 'NF', 
					 	 'tradeSeq':$scope.inputVO.tradeSeq,
			   			 'tradeType': '2'},
						function(tota, isError) {
						if (!isError) {
							$scope.sendRecv("SOT130", "goOP", "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO", $scope.inputVO,
								function(tota, isError) {
									if (!isError) {
										if (tota[0].body.errorMsg != '' && tota[0].body.errorMsg != null) {
											$scope.showErrorMsg(tota[0].body.errorMsg);
										} else {
											$scope.showMsg("ehl_02_SOT_002");
											$scope.query();
//												$scope.reportService();
											$scope.printReport();
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
		
		// 網銀快速下單
		$scope.goBANK = function() {
			// wait
		};
		// new
		$scope.newTrade = function() {
			$scope.connector('set','SOTTradeSEQ', null);
			$scope.connector('set','SOTCarSEQ', null);
			if ($scope.fromFPS) {
				// from FPS_SOT.js
				if($scope.inputVO.trustTS == 'S'){
					$scope.setSOTurl('assets/txn/SOT130/SOT130.html');
				}else{
					$scope.setSOTurl('assets/txn/SOT133/SOT133.html');
				}				
			} else {
				if($scope.inputVO.trustTS == 'S'){
					$rootScope.menuItemInfo.url = "assets/txn/SOT130/SOT130.html";
				}else{
					$rootScope.menuItemInfo.url = "assets/txn/SOT133/SOT133.html";
				}				
			}
		};
		
		$scope.enterPage = function () {
//			$scope.getBatchSeq().then(function(d){
				$scope.noCallCustQuery().then(function(data) {
					$scope.getSOTCustInfoCT();
				});
//			});
		};
		$scope.enterPage();
		
		$scope.checkBackEndRule = function (row) {
			debugger;
			var deferred = $q.defer();
			$scope.sendRecv("SOT130", "getBackendConstraintParameter", "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO", {'outProdID': row.RDM_PROD_ID},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.prodDTL[0].IS_BACKEND == 'Y' && tota[0].body.prodDTL[0].FUSM10 == 'Y') {
								debugger;
								var chosenCartList = $scope.cartList.filter(function(cart) {
									return cart.RDM_PROD_ID == row.RDM_PROD_ID && cart.CONTRACT_ID == row.CONTRACT_ID;
								});
								debugger;
								if(chosenCartList.length >1) {
									if(row.RDM_CERTIFICATE_ID == chosenCartList[chosenCartList.length-1].RDM_CERTIFICATE_ID) {
										debugger;
										deferred.resolve("success");
									} else {
										debugger;
										deferred.resolve("fail");
									}
										
								} else {
									debugger;
									deferred.resolve("success");
								}
							} else {
								debugger;
								deferred.resolve("success");
							}
						}
				});
			return deferred.promise;
		};
		
		$scope.delCarFromM = function(row) {
			debugger;
			$scope.checkBackEndRule(row).then(function(data) {
			if(data != 'success') {
				debugger;
				$scope.showErrorMsg("ehl_01_SOT130_001");
				return;
			}
			var txtMsg = "";
			if ($scope.cartList.length == 1) {
				txtMsg = $filter('i18n')('ehl_02_common_005');
			} else {
				txtMsg = $filter('i18n')('ehl_02_common_006');
			}
			$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
				$scope.sendRecv("SOT130", "delProd", "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO", {'type': $scope.type,'tradeSEQ': $scope.tradeSEQ,'rdmProdID': $scope.inputVO.rdmProdID,'seqNo':row.SEQ_NO},
						function(tota, isError) {
							if (!isError) {
									$scope.noCallCustQuery();
								return;
							}
				});
			});	
			});
		};
		
});