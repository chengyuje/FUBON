/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT521Controller',
	function($rootScope, $scope, $controller,getParameter , $confirm, socketService,$filter, ngDialog, projInfoService, $q, sotService, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT521Controller";
		
		//xml參數初始化
		getParameter.XML(["SOT.CUST_TYPE", "SOT.REDEEM_TYPE", "SOT.ENTRUST_TYPE_REDEEM_SN","SOT.TRUST_CURR_TYPE","SOT.MARKET_TYPE","SOT.SPEC_CUSTOMER"], function(totas) {
			if (totas) {
				$scope.mappingSet['SOT.CUST_TYPE'] = totas.data[totas.key.indexOf('SOT.CUST_TYPE')];
				$scope.mappingSet['SOT.REDEEM_TYPE'] = totas.data[totas.key.indexOf('SOT.REDEEM_TYPE')];
				$scope.mappingSet['SOT.ENTRUST_TYPE_REDEEM_SN'] = totas.data[totas.key.indexOf('SOT.ENTRUST_TYPE_REDEEM_SN')];
				$scope.mappingSet['SOT.TRUST_CURR_TYPE'] = totas.data[totas.key.indexOf('SOT.TRUST_CURR_TYPE')];
				$scope.mappingSet['SOT.MARKET_TYPE'] = totas.data[totas.key.indexOf('SOT.MARKET_TYPE')];
				$scope.mappingSet['SOT.SPEC_CUSTOMER'] = totas.data[totas.key.indexOf('SOT.SPEC_CUSTOMER')];//客戶註記
			}
		});
		$scope.init = function(){
			$scope.prodDTL=$scope.connector('get','SOT521_prodDTL');
			$scope.inputVO = {
					tradeSEQ: '',
					custType:'CUST',
					custID:$scope.connector('get','SOT521_custID'),
					prodType:'5',
					tradeType:'2',
					marketType:'1',
					trustCurrType:'',                 //信託業務別  
					//申購面額 (purchaseAmt)=信託本金(trustAmt)=總扣款金額(totAmt)
					purchaseAmt:undefined,
					trustAmt:undefined,//信託本金   
					totAmt:undefined,//總扣款金額
					//客戶資訊
					custName:'',
					kycLV:'',
					kycDueDate:undefined,
					custRemarks:'',
					isOBU:'',
					profInvestorYN:'',
					piRemark: '',								//專業投資人註記
					piDueDate:undefined,
					//商品資訊
					certificateID:'',
					prodID:'',
					prodName:'',
					prodRiskLV:'',
					prodCurr:'',
					prodMinBuyAmt:undefined,
					prodMinGrdAmt:undefined,
					debitAcct:'',
					trustAcct:'',
					creditAcct:'',
					narratorID: projInfoService.getUserID(),//解說人員ID
					narratorName: projInfoService.getUserName(),//解說人員姓名
					entrustType: '', 					//贖回方式 (修改預設為無)
					redeemType:'',
					tradeStatus:'',

					trustTS: 'S',								//S=特金/M=金錢信託
					goOPDisabled: false							//傳送OP按鈕，避免連續點擊
			};
			$scope.connector('set','SOT521_prodDTL',null);
			$scope.connector('set','SOT521_custID',null);
//			$scope.inputVO = {
//					tradeSEQ: '',
//					custType:'CUST',
//					custID:$scope.connector('get','SOT521_custID'),
//					prodType:'5',
//					tradeType:'2',
//					marketType:'1',
//					trustCurrType:'Y',                 //信託業務別  
//					//申購面額 (purchaseAmt)=信託本金(trustAmt)=總扣款金額(totAmt)
//					purchaseAmt:1,
//					trustAmt:1,//信託本金   
//					totAmt:1,//總扣款金額
//					//客戶資訊
//					custName:'1',
//					kycLV:'1',
////					kycDueDate:'2016-10-21 10:18:02',
//					custRemarks:'1',
//					isOBU:'1',
//					profInvestorYN:'1',
//					piDueDate:'1',
//					//商品資訊
//					prodID:'1',
//					prodName:'1',
//					prodRiskLV:'1',
//					prodCurr:'1',
//					prodMinBuyAmt:'1',
//					prodMinGrdAmt:'1',
//					debitAcct:'1',
//					trustAcct:'1',
//					creditAcct:'1',
//					narratorID:'1',
//					narratorName:'1',
//					entrustType: '1', 					//贖回方式
//					redeemType:'1'
//			};
			
		};
		
		$scope.chRedeemType = function(){
			if($scope.inputVO.redeemType =='1'){
				$scope.inputVO.redeemAmt = $scope.moneyFormat($scope.inputVO.purchaseAmt);
				$scope.calculate();
			}
		};
		
		$scope.checkRedeemAmt = function(){
			$scope.inputVO.redeemAmt=$scope.moneyUnFormat($scope.inputVO.redeemAmt);
			if($scope.inputVO.redeemAmt < $scope.inputVO.prodMinBuyAmt){
				$scope.showMsg('ehl_02_sot521_001'); //部份贖回金額不得低於最低贖回金額。
				$scope.inputVO.redeemAmt=undefined;
			}else if($scope.inputVO.redeemAmt % $scope.inputVO.prodMinGrdAmt != 0 ){
				$scope.showMsg('ehl_02_sot521_002'); //剩餘庫存金額不得低於最低贖回金額。
				$scope.inputVO.redeemAmt=undefined;
			}else if(($scope.inputVO.entrustAmt- $scope.inputVO.redeemAmt) > $scope.inputVO.prodMinBuyAmt){
				$scope.showMsg('ehl_02_sot521_003'); //剩餘庫存金額不得低於最低贖回金額。
				$scope.inputVO.redeemAmt=undefined;
			}else if($scope.inputVO.redeemAmt > $scope.inputVO.purchaseAmt){
				$scope.showMsg('ehl_02_sot521_004'); //贖回金額不得高於申購面額。
				$scope.inputVO.redeemAmt=undefined;
			}
			$scope.inputVO.redeemAmt=$scope.moneyFormat($scope.inputVO.redeemAmt);
			$scope.calculate();
		};
		
		//檢查信託帳號、贖回款入帳帳號之判斷
		$scope.checkTrustAcct = function(){
			if ($scope.inputVO.trustTS == 'S') {
				$scope.cmbCreditAcct = false;
				var creditAcctListDisplay = 'credit';

				//信託帳號檢核
				if ($scope.inputVO.trustAcct && sotService.is168($scope.inputVO.trustAcct)) {
					$scope.mappingSet[creditAcctListDisplay] = $filter('filter')($scope.mappingSet[creditAcctListDisplay],
						{DATA: $scope.inputVO.trustAcct},
						function (actual, expected) {
							return angular.equals(actual, expected)
						}
					);
				} else {
					//有傳信託業務別
					if ($scope.inputVO.trustCurrType) {
						if ($scope.inputVO.trustCurrType == 'N' || $scope.inputVO.trustCurrType == 'C') { //N台幣
							$scope.mappingSet[creditAcctListDisplay] = $filter('filter')($scope.mappingSet[creditAcctListDisplay], {CURRENCY: 'TWD'});
						} else if ($scope.inputVO.trustCurrType == 'Y') {  //Y外幣
							$scope.mappingSet[creditAcctListDisplay] = $filter('filter')($scope.mappingSet[creditAcctListDisplay], {CURRENCY: "TWD"}, function (actual, expected) {
								return !angular.equals(actual, expected)
							});
						}
					}
				}

				if ($scope.mappingSet[creditAcctListDisplay].length == 1) { //只有一筆不能勾選
					$scope.inputVO.creditAcct = $scope.mappingSet[creditAcctListDisplay][0].DATA;
					$scope.cmbCreditAcct = true;
				}
			}
	    }
		
		$scope.setCustInfo = function(body){
			if ($scope.inputVO.trustTS == 'S') {
				$scope.mappingSet['credit'] = body.creditAcct;
				$scope.mappingSet['debit'] = body.debitList;
			} else {
				$scope.inputVO.contractID = $scope.connector('get', 'SOTContractID');
				$scope.inputVO.debitAcct = $scope.connector('get', 'SOTDebitAcct');
				$scope.inputVO.creditAcct = $scope.connector('get', 'SOTDebitAcct');
			}

			$scope.inputVO.custName = body.custName;
			$scope.inputVO.kycLV = body.kycLevel;
			$scope.inputVO.kycDueDate =  $scope.toJsDate(body.kycDueDate);
			$scope.inputVO.custRemarks = body.custRemarks;
			$scope.inputVO.isOBU = body.outFlag;
			$scope.inputVO.profInvestorYN = body.profInvestorYN;
			$scope.inputVO.piRemark = body.piRemark;							//專業投資人註記
			$scope.inputVO.piDueDate =  $scope.toJsDate(body.piDueDate);
			$scope.inputVO.fatcaType = body.fatcaType;
			$scope.inputVO.isCustStakeholder = body.isCustStakeholder;
		}
		
		//取得客戶資料
		$scope.getSOTCustInfo = function(){
			var defer = $q.defer();
			if($scope.inputVO.custID) {
				$scope.sendRecv("SOT520", "getSOTCustInfo", "com.systex.jbranch.app.server.fps.sot520.SOT520InputVO", {'custID':$scope.inputVO.custID, 'prodType':5, 'tradeType':2},
						function(tota, isError) {
					if (!isError) {
						$scope.conCancel=false;
						//若非為本行客戶或信託客戶，需有訊息告知非為本行客戶或未開立信託戶，並須擋下單。
						if(  tota[0].body.trustAcct.length == 0){
							$scope.showErrorMsg('ehl_01_sot310_002');//非為本行客戶或未開立信託戶
							$scope.newTrade()
						}
						// 若為禁銷客戶，出現提示訊息禁止下單。
						else if(tota[0].body.noSale == 'Y' ){
							$scope.showErrorMsg('ehl_01_sot310_003'); //(ehl_01_sot310_003：禁銷客戶，禁止下單)
							$scope.setCustInfo(tota[0].body);//修改若為禁銷客戶/死亡戶/禁治產等狀況，可下單。 原邏輯為禁止下單。
							defer.resolve("success");
//							$rootScope.menuItemInfo.url = "assets/txn/SOT520/SOT520.html";
						}
						// 若為死亡戶，顯示警示訊息。
						else if(tota[0].body.deathFlag == 'Y' ){
							$scope.showErrorMsg('ehl_01_SOT_012'); //(死亡戶/禁治產，不可下單)
							$scope.setCustInfo(tota[0].body); //修改若為死亡戶狀況，可下單。   原邏輯為禁止下單。
							defer.resolve("success");
//							$rootScope.menuItemInfo.url = "assets/txn/SOT520/SOT520.html";
						}
						// 若為拒銷客戶，出提示訊息請專員確認是否繼續。
						else if(tota[0].body.rejectProdFlag == 'Y' ){
							$scope.conCancel=true;
							txtMsg = $filter('i18n')('ehl_01_sot310_005');
							$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
								$scope.setCustInfo(tota[0].body);
								defer.resolve("success");
								return defer.promise;
				            });
//							$scope.inputVO.custID = "";
						}
						// 如果專業投資人效期已過期，則出警示訊息，但無須擋下單。
						else if(tota[0].body.piDueDate < new Date()){
							$scope.showErrorMsg('ehl_01_sot310_006'); //(ehl_01_SOT510_006：專業投資人已過期)
							$scope.setCustInfo(tota[0].body);
							defer.resolve("success");
						}else{
							$scope.setCustInfo(tota[0].body);
							defer.resolve("success");
						}
					}else {
						$scope.newTrade()
					}
				});
			}
			return defer.promise;
		};
		
		$scope.getProdDTL = function(){
			var defer = $q.defer();
			if($scope.inputVO.prodID) {
				$scope.sendRecv("SOT520", "getProdDTL", "com.systex.jbranch.app.server.fps.sot520.SOT520InputVO", {"prodID":$scope.inputVO.prodID},
						function(tota, isError) {
							if (!isError) {
//								alert(JSON.stringify(tota));
								$scope.inputVO.bondVal=tota[0].body.prodDTL[0].BOND_VALUE;
								$scope.inputVO.riskcateID=tota[0].body.prodDTL[0].RISKCATE_ID;
								$scope.inputVO.prodCurr=tota[0].body.prodDTL[0].CURRENCY_STD_ID;
								$scope.inputVO.prodMinBuyAmt=tota[0].body.prodDTL[0].BASE_AMT_OF_BUYBACK; //最低贖回
								$scope.inputVO.prodMinGrdAmt=tota[0].body.prodDTL[0].UNIT_AMT_OF_BUYBACK; //累計贖回
								$scope.dateOfFlotation=tota[0].body.prodDTL[0].DATE_OF_FLOTATION; //商品發行日
								defer.resolve("suc");
							}
				});
			}
			return defer.promise;
		};
		
		$scope.setProd = function(){
			if ($scope.prodDTL && $scope.inputVO.custID) {
				$scope.getSOTCustInfo().then(function(data) {
					$scope.inputVO.prodID = $scope.prodDTL.BondNo;
					$scope.getProdDTL().then(function(data) {
						console.log($scope.prodDTL); 	
						$scope.inputVO.trustUnit=$scope.prodDTL.Unit;
						$scope.inputVO.prodCurr=$scope.prodDTL.CurCode;
						$scope.inputVO.trustCurrType = $scope.prodDTL.TrustType;	
//						$scope.inputVO.marketType = $scope.prodDTL.BondType;						//債券市場種類
						$scope.inputVO.prodName = $scope.prodDTL.BondName;							//商品名稱
						$scope.inputVO.purchaseAmt = $scope.prodDTL.TrustVal;						//庫存面額
						$scope.inputVO.redeemAmt =$scope.moneyFormat($scope.inputVO.purchaseAmt);
						$scope.inputVO.refVal = $filter('number')($scope.prodDTL.RefPrice,4);		//參考報價
						$scope.inputVO.refValDate =  $scope.toJsDate($scope.prodDTL.RefPriceDate);	//參考報價(日期)
//						$scope.inputVO.refValDate=new Date();//報價日期資料有問題
						$scope.inputVO.feeRate = $scope.prodDTL.TrustFeeRate;
						$scope.inputVO.trustAmt = $scope.prodDTL.TrustAmt;							//信託本金
						$scope.inputVO.certificateID = $scope.prodDTL.TrustNo;
						$scope.chRedeemType();
						$scope.calculate();
						$scope.inputVO.trustAcct = $scope.prodDTL.TrustAcct.trim();
						$scope.checkTrustAcct();
//						$scope.inputVO.creditAcct=$scope.inputVO.trustAcct;//test

					});
				});
			}
		}
		
		$scope.newTrade = function () {
			if ($scope.inputVO.trustTS == 'S') {
				if ($scope.fromFPS) {
					// from FPS_SOT.js
					$scope.setSOTurl('assets/txn/SOT520/SOT520.html');
				} else {
					$rootScope.menuItemInfo.url = "assets/txn/SOT520/SOT520.html";
				}
			} else {
				if ($scope.fromFPS) {
					// from FPS_SOT.js
					$scope.setSOTurl('assets/txn/SOT525/SOT525.html');
				} else {
					$rootScope.menuItemInfo.url = "assets/txn/SOT525/SOT525.html";
				}
			}
		}
		
		$scope.getTradeSEQ = function(){
			$scope.sendRecv("SOT712", "getTradeSEQ", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {},
					function(tota, isError) {
						if (!isError) {
							$scope.inputVO.tradeSEQ = tota[0].body.tradeSEQ;
							return;
						}
			});
		}
		
		$scope.noCallCustQuery = function () {
			var deferred = $q.defer();
			$scope.sendRecv("SOT520", "query", "com.systex.jbranch.app.server.fps.sot520.SOT520InputVO",{'tradeSEQ':$scope.inputVO.tradeSEQ},
					function(tota, isError) {
						if (!isError) {
							$scope.carList = tota[0].body.carList;
							$scope.mainList = tota[0].body.mainList;
							if($scope.mainList && $scope.mainList.length !=0){
								$scope.inputVO.custID= $scope.mainList[0].CUST_ID;
								$scope.getSOTCustInfo().then(function(data){
									$scope.inputVO.prodType= $scope.mainList[0].PROD_TYPE;
									$scope.inputVO.custName= $scope.mainList[0].CUST_NAME;
									$scope.inputVO.kycLV= $scope.mainList[0].KYC_LV;
									$scope.inputVO.kycDueDate= $scope.toJsDate($scope.mainList[0].KYC_DUE_DATE);
									$scope.inputVO.custRemarks= $scope.mainList[0].CUST_REMARKS;
									$scope.inputVO.isOBU= $scope.mainList[0].IS_OBU;
									$scope.inputVO.profInvestorYN= $scope.mainList[0].PROF_INVESTOR_YN;
									$scope.inputVO.piRemark = $scope.mainList[0].PI_REMARK;							//專業投資人註記
									$scope.inputVO.piDueDate=$scope.toJsDate($scope.mainList[0].PI_DUE_DATE);
									$scope.inputVO.tradeStatus = tota[0].body.mainList[0].TRADE_STATUS;					//交易狀態
									if($scope.carList && $scope.carList.length !=0){
										$scope.inputVO.refValDate = $scope.toJsDate($scope.carList[0].REF_VAL_DATE);
										$scope.inputVO.certificateID = $scope.carList[0].CERTIFICATE_ID;
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
										$scope.checkTrustAcct();
									}
								});
							}
						}
			});
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
			$scope.inputVO.redeemAmt=Number($scope.moneyUnFormat($scope.inputVO.redeemAmt));
			$scope.sendRecv("SOT520", "verifyTradeBond", "com.systex.jbranch.app.server.fps.sot520.SOT520InputVO", $scope.inputVO,
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
//												$scope.custInfo(tota[0].body);
//												deferred.resolve("success"); 
//												return deferred.promise;
											} else {
//												$scope.prodClear(true);
												deferred.resolve("");
											}
										});
									}
								}
								$scope.printReport();
								
								$scope.showMsg("ehl_02_SOT_002");
								$scope.query();
								return;
							}
						}
					});
		};
		
		//非常規交易
		$scope.getSOTCustInfoCT = function () {
			$scope.sendRecv("SOT520", 
							"getSOTCustInfoCT", 
							"com.systex.jbranch.app.server.fps.sot520.SOT520InputVO", 
							{'custID':$scope.inputVO.custID},
					function(totaCT, isError) {
						if (!isError) {				
							if($scope.inputVO.custID.length == 10)
								$scope.custAge = (totaCT[0].body.custAge < 18 ? $filter('i18n')('ehl_01_sot510_001') : ""); //委託人未成年，請查詢印鑑備註訊息或法代同意書內容
							
							return;
						}
			});
		};
		
		$scope.query = function() {
			if($scope.inputVO.tradeSEQ) {
				$scope.noCallCustQuery();
			} else {
				$scope.newTrade()
			}
		};
		
		$scope.calculate = function () {
			$scope.refVal = undefined;
			if ($scope.inputVO.entrustType == "4") {
				$scope.refVal = $scope.inputVO.refVal -1;
			} else if ($scope.inputVO.entrustType == "5") {
				$scope.refVal = $scope.inputVO.refVal -3;
			} else if ($scope.inputVO.entrustType == "6") {
				$scope.refVal = $scope.inputVO.refVal -5;
			} else {
				$scope.refVal = $scope.inputVO.refVal;
			}
			$filter('number')($scope.refVal,2);
			$scope.inputVO.entrustAmt = $scope.refVal;
			var endDate = new Date();
			var year = endDate.getFullYear();
			var month = endDate.getMonth();
			var day = endDate.getDate();			
			endDate = new Date(year,month,day);
			$scope.inputVO.fee = ((Math.round((endDate - $scope.toJsDate($scope.dateOfFlotation)) / (1000 * 60 * 60 * 24)) + 1) / 365) * ($scope.inputVO.feeRate) * $scope.moneyUnFormat($scope.inputVO.redeemAmt);//改為商品發行日
//			$scope.inputVO.fee = ((Math.round((endDate - $scope.toJsDate($scope.prodDTL.ApplyDate)) / (1000 * 60 * 60 * 24)) + 1) / 365) * ($scope.inputVO.feeRate) * $scope.moneyUnFormat($scope.inputVO.redeemAmt);
//			$scope.inputVO.totAmt = Math.round(($scope.inputVO.entrustAmt * ($scope.inputVO.feeRate / 100)) + $scope.inputVO.payableFee - $scope.inputVO.fee);
//			$scope.inputVO.totAmt=0;
		}
		
		$scope.init();
		$scope.getTradeSEQ();
		$scope.setProd();
		$scope.getSOTCustInfoCT();
		
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
		
		$scope.printReport = function(){
			//產生報表
			var fitVO = {
				caseCode : 		1, 							//case1 下單
				custId   :		$scope.inputVO.custID,		//客戶ID
				prdType  :      5,							//商品類別 : SN
				tradeSeq : 		$scope.inputVO.tradeSEQ, 	//交易序號
				tradeSubType:	2							//交易類型 : 贖回
			}
					
			$scope.sendRecv("SOT712", "fitToGetPDF", "com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO", fitVO,
					function(tota, isError) {
				if (isError) {
					$scope.showErrorMsg(totas[0].body.msgData);
				}
			});
		}
});
