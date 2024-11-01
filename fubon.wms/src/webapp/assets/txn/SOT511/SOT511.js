/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT511Controller',
	function($rootScope, $scope, $controller, $filter, $confirm, sysInfoService, $q, getParameter, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT511Controller";
		
		//xml參數初始化
		getParameter.XML(["SOT.CUST_TYPE", "SOT.TRUST_CURR_TYPE", "SOT.MARKET_TYPE","SOT.SPEC_CUSTOMER"], function(totas) {
			if (totas) {
				$scope.mappingSet['SOT.CUST_TYPE'] = totas.data[totas.key.indexOf('SOT.CUST_TYPE')];
				$scope.mappingSet['SOT.TRUST_CURR_TYPE'] = totas.data[totas.key.indexOf('SOT.TRUST_CURR_TYPE')];
				$scope.mappingSet['SOT.MARKET_TYPE'] = totas.data[totas.key.indexOf('SOT.MARKET_TYPE')];
				$scope.mappingSet['SOT.SPEC_CUSTOMER'] = totas.data[totas.key.indexOf('SOT.SPEC_CUSTOMER')];//客戶註記
			}
		});
		
		$scope.init = function(){
			$scope.inputVO = {
					tradeSEQ: $scope.connector('get', "SOTTradeSEQ"),
//					tradeSEQ:'20161012000060',
					custType:'CUST',
					custID:'',
					prodType:'5',
					tradeType:'1',
					marketType:'1',
					trustCurrType:'',                 //信託業務別  
					//申購面額 (purchaseAmt)=信託本金(trustAmt)=總扣款金額(totAmt)
					purchaseAmt:undefined,
					trustAmt:undefined,//信託本金   
					totAmt:undefined,//總扣款金額
					//客戶資訊
					custName:'',
					kycLV:'',
					kycDueDate:'',
					custRemarks:'',
					isOBU:'',
					profInvestorYN:'',
					piDueDate:'',
					//商品資訊
					prodID:'',
					prodName:'',
					prodRiskLV:'',
					prodCurr:'',
					prodMinBuyAmt:'',
					prodMinGrdAmt:'',
					debitAcct:'',
					trustAcct:'',
					creditAcct:'',
					narratorID:'',
					narratorName:'',
					authID: '', //授權交易人員ID
//					authName: '', //授權交易人員姓名
					isBargainNeeded:'', //是否需要議價
					isRecNeeded:'',		//是否需錄音序號
					tradeStatus:'',	    //交易狀態
					recSEQ:'',		    //錄音序號
					goOPDisabled: false,//傳送OP按鈕，避免連續點擊
					overCentRateYN: '',							//超過集中度上限
					warningMsg: $scope.connector('get', 'SOT511_warningMsg'),
					loanFlag: ''                               //90天貸款註記(Y/N)
			};
			$scope.connector('set','SOTTradeSEQ', null);
			$scope.connector('set','SOT511_warningMsg', null);
		};
		//取消
		$scope.delTrade = function () {
			var txtMsg = $filter('i18n')('ehl_02_common_005');
			$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
				$scope.sendRecv("SOT510", "delTrade", "com.systex.jbranch.app.server.fps.sot510.SOT510InputVO",{"tradeSEQ":$scope.inputVO.tradeSEQ},
						function(tota, isError) {
							if (!isError) {
								$scope.newTrade();
							}
				});
            });
		};

		$scope.runToWhere = function(tradeSEQ) {
			$scope.connector('set', "SOTTradeSEQ", tradeSEQ);

			if ($scope.inputVO.trustTS == 'S') {
				if ($scope.fromFPS) {
					// from FPS_SOT.js
					$scope.setSOTurl('assets/txn/SOT510/SOT510.html');
				} else {
					$rootScope.menuItemInfo.url = "assets/txn/SOT510/SOT510.html";
				}
			} else {
				if ($scope.fromFPS) {
					$scope.setSOTurl('assets/txn/SOT515/SOT515.html');
				} else {
					$rootScope.menuItemInfo.url = "assets/txn/SOT515/SOT515.html";
				}
			}
		}

		//修改
		$scope.goPage = function() {
			$scope.runToWhere($scope.inputVO.tradeSEQ)
		};

		//傳送OP交易，避免按鈕被連續點擊造成錯誤，先disable按鈕並$timeout
		$scope.verifyTradeBond = function () {
			$scope.inputVO.goOPDisabled = true;
			
			$timeout(function() {
				$scope.do_verifyTradeBond(); 
				$scope.inputVO.goOPDisabled = false;}
			, 1000);			
		}
		
		//傳送OP交易及列印表單btn
		$scope.do_verifyTradeBond= function(){
			if($scope.inputVO.isRecNeeded == 'Y' && !$scope.inputVO.recSEQ){
				$scope.showErrorMsg("ehl_02_common_007");
				return;
			}
			$scope.sendRecv("SOT712", "updateBatchSeq", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", 
			{'prodType': 'SN', 'tradeSeq': $scope.inputVO.tradeSEQ},
			function(tota, isError) {
				if (!isError) {
					$scope.sendRecv("SOT510", "verifyTradeBond", "com.systex.jbranch.app.server.fps.sot510.SOT510InputVO", 
					{'tradeSEQ':$scope.inputVO.tradeSEQ,'prodType':1,'checkType':2,'recSEQ':$scope.inputVO.recSEQ,
					 'purchaseAmt':$scope.inputVO.purchaseAmt, 'isOBU': $scope.inputVO.isOBU},
					function(totaCT, isError) {
						if (!isError) {
							if (totaCT[0].body.warningMsg != '' && totaCT[0].body.warningMsg != null) {
								$scope.showErrorMsg(totaCT[0].body.warningMsg);
							}
							if (totaCT[0].body.errorMsg != '' && totaCT[0].body.errorMsg != null) {
								$scope.showErrorMsg(totaCT[0].body.errorMsg);
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
		
		//繼續交易btn
		$scope.newTrade = function() {
			$scope.runToWhere(null);
		};
		
		//結束
		$scope.goPageSOT610 = function () {
			$rootScope.menuItemInfo.url = "assets/txn/SOT610/SOT610.html";
		};
		
		//錄音序號檢核
		$scope.validateRecSeq = function(){
			if($scope.inputVO.recSEQ == ''){
				$scope.recSeqFlag = $scope.recSeqFlagOrg;
				return;
			}
			$scope.sendRecv("SOT712", 
					"validateRecseq", 
					"com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", 
					{'custID':$scope.inputVO.custID, 
				 	 'prodType': 'SN', 
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
				}
	});
			
		}
		
		$scope.noCallCustQuery = function () {
			var defer = $q.defer();
			$scope.sendRecv("SOT510", "query", "com.systex.jbranch.app.server.fps.sot510.SOT510InputVO",{'tradeSEQ':$scope.inputVO.tradeSEQ},
					function(tota, isError) {
						if (!isError) {
							console.log(tota[0].body);
							$scope.carList = tota[0].body.carList;
							$scope.mainList = tota[0].body.mainList;
							if($scope.mainList && $scope.mainList.length !=0){
								$scope.inputVO.isRecNeeded = $scope.mainList[0].IS_REC_NEEDED;  //是否需錄音序號
								$scope.inputVO.custProType = $scope.mainList[0].PROF_INVESTOR_TYPE;//專投種類
								$scope.inputVO.custID= $scope.mainList[0].CUST_ID;
								$scope.inputVO.prodType= $scope.mainList[0].PROD_TYPE;
								$scope.inputVO.custName= $scope.mainList[0].CUST_NAME;
								$scope.inputVO.kycLV= $scope.mainList[0].KYC_LV;
								$scope.inputVO.kycDueDate= $scope.mainList[0].KYC_DUE_DATE;
								$scope.inputVO.custRemarks= $scope.mainList[0].CUST_REMARKS;
								$scope.inputVO.isOBU= $scope.mainList[0].IS_OBU;
								$scope.inputVO.profInvestorYN= $scope.mainList[0].PROF_INVESTOR_YN;
								$scope.inputVO.piDueDate= $scope.mainList[0].PI_DUE_DATE;
								$scope.inputVO.tradeStatus = tota[0].body.mainList[0].TRADE_STATUS;					//交易狀態
								$scope.inputVO.loanFlag = tota[0].body.mainList[0].FLAG_NUMBER;				    //90天貸款註記(Y/N)

								// 金錢信託
								$scope.inputVO.trustTS = tota[0].body.mainList[0].TRUST_TRADE_TYPE;					//信託交易類別

								if($scope.carList && $scope.carList.length !=0){
									$scope.inputVO.marketType=$scope.carList[0].MARKET_TYPE;
									$scope.inputVO.trustCurrType=$scope.carList[0].TRUST_CURR_TYPE;
									$scope.inputVO.purchaseAmt=$scope.carList[0].PURCHASE_AMT;
									$scope.inputVO.trustAmt=$scope.carList[0].TRUST_AMT;
									$scope.inputVO.totAmt=$scope.carList[0].TOT_AMT;
									$scope.inputVO.prodID=$scope.carList[0].PROD_ID;
									$scope.inputVO.prodName=$scope.carList[0].PROD_NAME;
									$scope.inputVO.prodRiskLV=$scope.carList[0].PROD_RISK_LV;
									$scope.inputVO.prodCurr=$scope.carList[0].PROD_CURR;
									$scope.inputVO.prodMinBuyAmt=$scope.carList[0].PROD_MIN_BUY_AMT;
									$scope.inputVO.prodMinGrdAmt=$scope.carList[0].PROD_MIN_GRD_AMT;
									$scope.inputVO.debitAcct=$scope.carList[0].DEBIT_ACCT;
									$scope.inputVO.trustAcct=$scope.carList[0].TRUST_ACCT;
									$scope.inputVO.creditAcct=$scope.carList[0].CREDIT_ACCT;
									$scope.inputVO.narratorID=$scope.carList[0].NARRATOR_ID;
									$scope.inputVO.narratorName=$scope.carList[0].NARRATOR_NAME;
									$scope.inputVO.authID=$scope.carList[0].AUTH_ID;
//									$scope.getTellerName('authID',$scope.inputVO.authName);
									$scope.inputVO.bossID=$scope.carList[0].BOSS_ID;			//主管員編
									$scope.getTellerName('bossID',$scope.inputVO.bossID);		//主管姓名
									$scope.inputVO.overCentRateYN = $scope.carList[0].OVER_CENTRATE_YN;			//超過集中度上限

									// 金錢信託
									$scope.inputVO.contractID = tota[0].body.carList[0].CONTRACT_ID;					//契約編號
									$scope.inputVO.trustPeopNum = tota[0].body.carList[0].TRUST_PEOP_NUM;				//是否為多委託人契約 Y=是/N=否
								}
							}
							defer.resolve("success");
						}
			});
			return defer.promise;
		};
		
		//非常規交易
		$scope.getSOTCustInfoCT = function () {
			$scope.sendRecv("SOT510", "getSOTCustInfoCT", "com.systex.jbranch.app.server.fps.sot510.SOT510InputVO", 
			{'custID':$scope.inputVO.custID, 'prodType': $scope.inputVO.prodType, 'tradeType': $scope.inputVO.tradeType, 
			 'prodID': $scope.inputVO.prodID, 'isOBU': $scope.inputVO.isOBU},
			function(totaCT, isError) {
				if (!isError) {
//					$scope.isFirstTrade = (totaCT[0].body.isFirstTrade == "Y" ? $filter('i18n')('ehl_02_SOT_003') : ""); 
					$scope.isFirstTrade = ""; // SI，SN不判斷是否為首購 
					$scope.ageUnder70Flag = (totaCT[0].body.ageUnder70Flag == "N" ? $filter('i18n')('ehl_02_SOT_004') : "");
					$scope.eduJrFlag = (totaCT[0].body.eduJrFlag == "N" ? $filter('i18n')('ehl_02_SOT_005') : ""); ;
					$scope.healthFlag = (totaCT[0].body.healthFlag == "N" ? $filter('i18n')('ehl_02_SOT_006') : "");

					if ($scope.mainList[0].IS_REC_NEEDED == 'N') {
						$scope.recSeqFlagOrg = 'N';
					} else {
//						$scope.recSeqFlagOrg = (totaCT[0].body.isFirstTrade == "Y" || totaCT[0].body.custRemarks == "Y" ? "Y" : "N");     //是否弱勢或首購客戶 控制<傳送OP交易及列印表單>與<網銀快速下單>按鈕
						$scope.recSeqFlagOrg = 'Y';
					}	
					
					$scope.recSeqFlag = $scope.recSeqFlagOrg;
					if($scope.inputVO.custID.length == 10)
						$scope.custAge = (totaCT[0].body.custAge < 18 ? $filter('i18n')('ehl_01_sot510_001') : ""); //委託人未成年，請查詢印鑑備註訊息或法代同意書內容
					
//					if ($scope.isFirstTrade == "" && $scope.ageUnder70Flag == "" && $scope.eduJrFlag == "" && $scope.healthFlag == ""){
//						 $scope.recNeeded=false;
//					}
					return;
				}
			});
		};
		
		//初始查詢
		$scope.query = function() {
			if($scope.inputVO.tradeSEQ) {
				$scope.noCallCustQuery().then(function(data) {
					$scope.getSOTCustInfoCT();
				});
			} else {
				$scope.runToWhere(null);
			}
		};
		
		$scope.init();
		$scope.query();
		
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
				prdType  :      5,								//商品類別 : SN
				tradeSeq : 		$scope.inputVO.tradeSEQ, 		//交易序號
				tradeSubType:	1,								//交易類型 : 申購
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