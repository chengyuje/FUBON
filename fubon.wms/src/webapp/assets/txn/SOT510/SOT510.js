/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT510Controller',
	function($rootScope, $scope, $controller, $confirm, $filter, socketService, ngDialog, projInfoService,getParameter , $q, validateService, sotService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT510Controller";
		//繼承PRD100高齡檢核
		$controller('PRD100Controller', {$scope: $scope});
		
		//xml參數初始化
		getParameter.XML(["SOT.CUST_TYPE", "SOT.TRUST_CURR_TYPE", "SOT.MARKET_TYPE","SOT.SPEC_CUSTOMER"], function(totas) {
			if (totas) {
				$scope.mappingSet['SOT.CUST_TYPE'] = totas.data[totas.key.indexOf('SOT.CUST_TYPE')];
				$scope.mappingSet['SOT.TRUST_CURR_TYPE'] = totas.data[totas.key.indexOf('SOT.TRUST_CURR_TYPE')];
				for(var i=0;i<$scope.mappingSet['SOT.TRUST_CURR_TYPE'].length;i++){
					if($scope.mappingSet['SOT.TRUST_CURR_TYPE'][i].DATA =='C'){
						$scope.mappingSet['SOT.TRUST_CURR_TYPE'].splice(i,1);
						break;
					}
				}
				$scope.mappingSet['SOT.MARKET_TYPE'] = totas.data[totas.key.indexOf('SOT.MARKET_TYPE')];
				$scope.mappingSet['SOT.SPEC_CUSTOMER'] = totas.data[totas.key.indexOf('SOT.SPEC_CUSTOMER')];//客戶註記
			}
		});
		
		
		//參數初始化
		$scope.init = function(){
			$scope.sumBDS =''; //此單已申購金額
			$scope.nvlAMT =''; //前一日投資AUM
			$scope.sumITEM='';//SI已申購金額+SI庫存金額+SN已申購金額+SN在途申購金額+SN庫存金額
			$scope.disBtn = true;
			$scope.cmbDebitAcct=true;
			$scope.cmbCreditAcct=true;
			$scope.purchaseAmt='';
			$scope.inputVO = {
					custProType:'',
					tradeSEQ: '',
					custType:'CUST',
					custID:'',
					prodType:'5', //SN
					tradeType:'1', //申購
					seniorAuthType:'S',	//高齡評估表授權種類(S:下單、A：適配)
					marketType:'1',
					trustCurrType:'Y',                 //信託業務別  
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
					fatcaType: '',
//					isCustStakeholder: undefined,
//					custQValue: '',
					//商品資訊
					entrustType:'2',
					feeType:'1',
					bondVal:'',
					prodID:'',
					prodName:'',
					prodRiskLV:'',
					prodCurr:'',
					prodMinBuyAmt:'',
					prodMinGrdAmt:'',
					debitAcct:'',
					trustAcct:'',
					creditAcct:'',
					narratorID: projInfoService.getUserID(),	//解說人員ID
					narratorName: projInfoService.getUserName(),//解說人員姓名
					authID: '', //授權交易人員ID
//					authName: '', //授權交易人員姓名
					warningMsg: '',								//警告訊息
					trustTS: 'S',								//交易類別:特金S或金錢信託M
					trustPeopNum: 'N',							//是否為多委託人契約
					hnwcBuy: '',								//商品:限高資產客戶申購
					hnwcYN: '',									//是否為高資產客戶 Y/N 
					hnwcServiceYN: '',							//可提供高資產商品或服務 Y/N 
					disableAuthId: false,						//disabled AUTH_ID
					flagNumber: '',								//90天內是否有貸款紀錄 Y/N
					otherWithCustId: false						//是否帶客戶ID進來(快查)
			};
			
			var custID = $scope.connector('get','ORG110_custID');
			if(custID != undefined){
				$scope.inputVO.custID = custID;
			}
//			$scope.inputVO = {
//					tradeSEQ: '',
//					custType:'CUST',
//					custID:'1',
//					prodType:'5',
//					tradeType:'1',
//					marketType:'1',
//					trustCurrType:'1',                 //信託業務別  
//					//申購面額 (purchaseAmt)=信託本金(trustAmt)=總扣款金額(totAmt)
//					purchaseAmt:'1',
//					trustAmt:'1',//信託本金   
//					totAmt:'1',//總扣款金額
//					//客戶資訊
//					custName:'1',
//					kycLV:'1',
////					kycDueDate:'2016-01-01',
//					custRemarks:'1',
//					isOBU:'1',
//					profInvestorYN:'1',
////					piDueDate:'2016-01-01',
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
//					narratorName:'1'
//			};
			
			$scope.inputVO.tradeSEQ=$scope.connector('get', "SOTTradeSEQ");
			$scope.connector('set', "SOTTradeSEQ",null);
			if($scope.connector('get','PR140_ID')){
				$scope.disProdID=true;
				$scope.inputVO.prodID=$scope.connector('get','PR140_ID');
				$scope.connector('set','PR140_ID',null);
				$scope.getProdDTL();
			}
			
		};
		
		$scope.clearProd = function(){
			$scope.sumBDS =''; //此單已申購金額
			$scope.nvlAMT =''; //前一日投資AUM
			$scope.sumITEM=''; //SI已申購金額+SI庫存金額+SN已申購金額+SN在途申購金額+SN庫存金額
			$scope.purchaseAmt='';
			$scope.inputVO.bondVal='';
			$scope.inputVO.prodName='';
			$scope.inputVO.prodRiskLV='';
			$scope.inputVO.prodCurr='';
			$scope.inputVO.prodMinBuyAmt='';
			$scope.inputVO.prodMinGrdAmt='';
			$scope.inputVO.purchaseAmt='';
			$scope.inputVO.trustAmt='';
			$scope.inputVO.totAmt='';
			$scope.inputVO.hnwcBuy = '';
		};
		
		//清除客戶資料
		$scope.clearCust = function(){
			$scope.mappingSet['trustAcct']= []; 
			$scope.mappingSet['credit']= []; 
			$scope.mappingSet['debit']= []; 
			$scope.mappingSet['credit#DISPLAY']= []; 
			$scope.mappingSet['debit#DISPLAY']= []; 
			$scope.inputVO.custProType ='';
			$scope.inputVO.piRemark = '';  
			$scope.inputVO.custName = '';
			$scope.inputVO.kycLV = '';
			$scope.inputVO.kycDueDate = undefined;
			$scope.inputVO.custRemarks = '';
			$scope.inputVO.isOBU = '';
			$scope.inputVO.profInvestorYN = '';
			$scope.inputVO.piDueDate = undefined;
			$scope.inputVO.debitAcct='';
			$scope.inputVO.trustAcct='';
			$scope.inputVO.creditAcct='';
			$scope.inputVO.fatcaType = '';
			$scope.inputVO.hnwcYN = '';
			$scope.inputVO.hnwcServiceYN = '';
			$scope.inputVO.disableAuthId = false;
			$scope.inputVO.authID = '';
//			$scope.inputVO.authName = '';
//			$scope.inputVO.isCustStakeholder = undefined;
//			$scope.inputVO.custQValue = '';
//			$scope.inputVO.narratorID= '';
//			$scope.inputVO.narratorName= '';
		};
		
		$scope.clearTradeSEQ = function(){
        	$scope.inputVO.tradeSEQ='';
        	$scope.query();
        }
		
		//從快查或別的交易過來，帶CUSTID
		$scope.queryChkSenior = function() {
			if($scope.connector('get','SOTCustID')){
				$scope.inputVO.custID=$scope.connector('get','SOTCustID');
				$scope.connector('set','SOTCustID',null);
				$scope.getSOTCustInfo().then(function(data){
					if($scope.connector('get', 'SOTProd')){
						$scope.inputVO.prodID = $scope.connector('get', 'SOTProd').InsuranceNo?$scope.connector('get', 'SOTProd').InsuranceNo.trim():null;
						$scope.connector('set','SOTProd',null);
						$scope.getProdDTL();
					}
				});
			}
			
			if($scope.inputVO.tradeSEQ) {
				$scope.noCallCustQuery();
			} else {
				// 取得新交易序號 
				$scope.sendRecv("SOT712", "getTradeSEQ", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.tradeSEQ = tota[0].body.tradeSEQ;
								return;
							}
				});
			}
		}
		
		//目前先做取得新交易序號(後續會有已有舊交易處理邏輯)
		$scope.query = function() {
			//從其他頁面進入下單
			if($scope.connector('get', 'SOTTradeSEQ')){
				$scope.inputVO.tradeSEQ = $scope.connector('get', 'SOTTradeSEQ');
				$scope.inputVO.carSEQ = $scope.connector('get', 'SOTCarSEQ');
				$scope.connector('set', 'SOTTradeSEQ', null);
				$scope.connector('set', 'SOTCarSEQ', null);
			} else if ($scope.fromFPS){
				console.log($scope.FPSData);
				$scope.inputVO.custID = $scope.FPSData.custID;//客戶ID					
				$scope.getSOTCustInfo().then(function(data){
					$scope.inputVO.prodID = $scope.FPSData.prdID; //商品代號	
					$scope.getProdDTL();
					$scope.inputVO.purchaseAmt = $scope.FPSData.PURCHASE_ORG_AMT; //原幣金額
				});
			}

			if($scope.inputVO.tradeSEQ) {
				$scope.noCallCustQuery();
			} else {
				// 取得新交易序號 
				$scope.sendRecv("SOT712", "getTradeSEQ", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.tradeSEQ = tota[0].body.tradeSEQ;
								return;
							}
				});
			}
		};
		
		$scope.noCallCustQuery = function () {
			var deferred = $q.defer();
			$scope.sendRecv("SOT510", "query", "com.systex.jbranch.app.server.fps.sot510.SOT510InputVO",{'tradeSEQ':$scope.inputVO.tradeSEQ},
					function(tota, isError) {
						if (!isError) {
							$scope.carList = tota[0].body.carList;
							$scope.mainList = tota[0].body.mainList;
							if($scope.mainList && $scope.mainList.length !=0){
								$scope.inputVO.custID= $scope.mainList[0].CUST_ID;
								$scope.getSOTCustInfo().then(function(data){
									$scope.inputVO.piRemark = $scope.mainList[0].PI_REMARK;	//專業投資人註記
									$scope.inputVO.prodType= $scope.mainList[0].PROD_TYPE;
									$scope.inputVO.custName= $scope.mainList[0].CUST_NAME;
									$scope.inputVO.custProType = $scope.mainList[0].PROF_INVESTOR_TYPE;//專投種類
									$scope.inputVO.kycLV= $scope.mainList[0].KYC_LV;
									$scope.inputVO.kycDueDate= $scope.mainList[0].KYC_DUE_DATE;
									$scope.inputVO.custRemarks= $scope.mainList[0].CUST_REMARKS;
									$scope.inputVO.isOBU= $scope.mainList[0].IS_OBU;
									$scope.inputVO.profInvestorYN= $scope.mainList[0].PROF_INVESTOR_YN;
									$scope.inputVO.piDueDate= $scope.mainList[0].PI_DUE_DATE;
									if($scope.carList && $scope.carList.length !=0){
										$scope.inputVO.marketType=$scope.carList[0].MARKET_TYPE;
										$scope.inputVO.trustCurrType=$scope.carList[0].TRUST_CURR_TYPE;
										$scope.inputVO.purchaseAmt=$scope.carList[0].PURCHASE_AMT;
										if($scope.inputVO.purchaseAmt)	$scope.purchaseAmt=$filter('number')($scope.inputVO.purchaseAmt);
										$scope.inputVO.trustAmt=$scope.carList[0].TRUST_AMT;
										$scope.inputVO.totAmt=$scope.carList[0].TOT_AMT;
										$scope.inputVO.prodID=$scope.carList[0].PROD_ID;
										$scope.inputVO.prodName=$scope.carList[0].PROD_NAME;
										$scope.inputVO.bondVal=$scope.carList[0].BOND_VALUE;  //todo
										$scope.inputVO.prodRiskLV=$scope.carList[0].PROD_RISK_LV;
										$scope.inputVO.prodCurr=$scope.carList[0].PROD_CURR;
										$scope.inputVO.prodMinBuyAmt=$scope.carList[0].PROD_MIN_BUY_AMT;
										$scope.inputVO.prodMinGrdAmt=$scope.carList[0].PROD_MIN_GRD_AMT;
										$scope.inputVO.debitAcct=$scope.carList[0].DEBIT_ACCT+"_"+$scope.inputVO.prodCurr;
										$scope.inputVO.trustAcct=$scope.carList[0].TRUST_ACCT;
										$scope.inputVO.creditAcct=$scope.carList[0].CREDIT_ACCT;
										$scope.inputVO.narratorID=$scope.carList[0].NARRATOR_ID;
										$scope.inputVO.narratorName=$scope.carList[0].NARRATOR_NAME;
										$scope.inputVO.authID=$scope.carList[0].AUTH_ID;
//										$scope.getTellerName('authID',$scope.inputVO.authName);
										$scope.inputVO.bossID=$scope.carList[0].BOSS_ID;
										$scope.getTellerName('bossID',$scope.inputVO.bossID);
									}
									$scope.sumBDS =tota[0].body.sumBDS  == null?0:tota[0].body.sumBDS; //此單已申購金額
									$scope.nvlAMT =tota[0].body.nvlAMT  == null?0:tota[0].body.nvlAMT; //前一日投資AUM
									$scope.sumITEM=tota[0].body.sumITEM == null?0:tota[0].body.sumITEM; //SI已申購金額+SI庫存金額+SN已申購金額+SN在途申購金額+SN庫存金額
								});
							}
							deferred.resolve('success')
						}
			});
			return deferred.promise;
		};
		
		//將帳號轉成display暫存陣列
		$scope.setAcctDisplay = function(acctNameList,prodCurr){
			for(var i=0;i<acctNameList.length;i++){
				if(prodCurr){
					$scope.mappingSet[acctNameList[i]+'#DISPLAY'] = $filter('filter')($scope.mappingSet[acctNameList[i]], {CURRENCY: prodCurr});
				}else{
					$scope.mappingSet[acctNameList[i]+'#DISPLAY'] = $scope.mappingSet[acctNameList[i]];
				}
			}
		}
		
		$scope.validateSeniorCust = function() {
			if(!$scope.inputVO.custID) return;
			
			$scope.inputVO.type = "1";
			$scope.inputVO.cust_id = $scope.inputVO.custID;
			$scope.validSeniorCustEval(); //PRD100.validSeniorCustEval高齡檢核
		}
		
		//PRD100.validSeniorCustEval高齡檢核不通過清空客戶ID
		$scope.clearCustInfo = function() {
			$scope.inputVO.custID = "";
			$scope.connector('set','SOTCustID',null);
			$scope.clearCust();
		};
		
		//PRD100.validSeniorCustEval高齡檢核通過後查詢
		$scope.reallyInquire = function() {
			if($scope.inputVO.trustTS == 'M') {
				//金錢信託
				$scope.getCustANDContractList();
			} else {
				if($scope.inputVO.otherWithCustId) { //有帶客戶ID(快查)
					$scope.queryChkSenior();
				} else {
					$scope.getSOTCustInfo();
				}
			}
			$scope.connector('set','SOTCustID',null);
		};
		
		//取得客戶資料
		$scope.getSOTCustInfo = function(){
			$scope.inputVO.custID = $filter('uppercase')($scope.inputVO.custID);
			var defer = $q.defer();
			$scope.clearCust();
			$scope.inputVO.prodID="";
			$scope.clearProd();
			var validCustID = validateService.checkCustID($scope.inputVO.custID); //自然人和法人檢查
			if(validCustID==false) {
				$scope.inputVO.custID='';
			}else if(validCustID) {
				$scope.sendRecv("SOT510", "getSOTCustInfo", "com.systex.jbranch.app.server.fps.sot510.SOT510InputVO", {'custID':$scope.inputVO.custID, 'prodType':5, 'tradeType':1, 'trustTS':$scope.inputVO.trustTS},
						function(tota, isError) {
							if (!isError) {
								$scope.conCancel=false;
								$scope.toDay = $filter('date')(new Date(),'yyyy-MM-dd 00:00:00');//取當日日期
								//若非為本行客戶或信託客戶，需有訊息告知非為本行客戶或未開立信託戶，並須擋下單。
								if($scope.inputVO.trustTS == 'S' && (tota[0].body.custName == null || tota[0].body.custName == "" || tota[0].body.trustAcct.length == 0)){
									$scope.showErrorMsg('ehl_01_sot310_002');//非為本行客戶或未開立信託戶
									$scope.inputVO.custID = "";
								}
								// 若為禁銷客戶，出現提示訊息禁止下單。
								else if(tota[0].body.noSale == 'Y' ){
									$scope.showErrorMsg('ehl_01_sot310_003'); //(ehl_01_sot310_003：禁銷客戶，禁止下單)
									$scope.inputVO.custID = "";
								}
								// 若為死亡戶/禁治產等狀況，不可下單。
								else if(tota[0].body.deathFlag == 'Y' || tota[0].body.isInterdict == 'Y' ){
									$scope.showErrorMsg('ehl_01_sot310_004'); //(ehl_01_SOT510_004：死亡戶/禁治產，不可下單)
									$scope.inputVO.custID = "";
								}
								// 若為拒銷客戶，出提示訊息請專員確認是否繼續。
								else if(tota[0].body.rejectProdFlag == 'Y' ){
									$scope.conCancel=true;
									txtMsg = $filter('i18n')('ehl_01_sot310_005');
									var dialog = ngDialog.open({
										template: 'assets/txn/CONFIRM/CONFIRM.html',
										className: 'CONFIRM',
										showClose: false,
										scope : $scope,
										controller: ['$scope', function($scope) {
											$scope.dialogLabel = txtMsg;
							            }]
									}).closePromise.then(function (data) {
										if (data.value === 'successful') {
											$scope.setCustInfo(tota[0].body); //設定客戶資料
											deferred.resolve("success");  //若為拒銷客戶，出現警告訊息問是否繼續，選"確定"，但客戶ID不能被清掉。
											return deferred.promise;
										} else {
											$scope.inputVO.custID = "";
											deferred.resolve("");
										}
									});
								}
								//FOR CBS TEST日期
								// 另若KYC過期，需有訊息提示客戶，並清空客戶ID。
//								else if(!tota[0].body.kycDueDate || $scope.toJsDate(tota[0].body.kycDueDate) < $scope.toJsDate($scope.toDay)){
								else if(tota[0].body.isKycDueDateUseful){
									var kycDueDate = $scope.toJsDate(tota[0].body.kycDueDate);
									var msgParam = "";
									if(kycDueDate==null) {
										msgParam='未承作';
									}else{
										msgParam = kycDueDate.getFullYear() + "/" +(kycDueDate.getMonth() + 1) + "/" + kycDueDate.getDate();
									}
									var txtMsg = $filter('i18n')('ehl_01_sot310_001') + "(" + msgParam + ")";
									$scope.inputVO.custID = "";
									$scope.clearCust();
									$scope.showErrorMsg(txtMsg);
								}
								// 如果專業投資人效期已過期，則出警示訊息，但無須擋下單。
								//2023.05.31不須顯示錯誤訊息
								//FOR CBS TEST日期
								else if(!tota[0].body.piDueDate || tota[0].body.isPiDueDateUseful ){ //$scope.toJsDate(tota[0].body.piDueDate) < $scope.toJsDate($scope.toDay)
//									$scope.showErrorMsg('ehl_01_sot310_006'); //(ehl_01_SOT510_006：專業投資人已過期)
									$scope.setCustInfo(tota[0].body);
									defer.resolve("success");
								}else{
									$scope.setCustInfo(tota[0].body);
									defer.resolve("success");
								}
							}else {
								$scope.inputVO.custID = "";
							}
				});
			}
			if(!$scope.inputVO.custID){
				$scope.inputVO.prodID="";
				$scope.clearProd();
			}
				
			return defer.promise;
		};
		
		$scope.setCustInfo=function(body){
			$scope.inputVO.piRemark = body.piRemark;	
			$scope.inputVO.fatcaType = body.fatcaType;
//			$scope.inputVO.isCustStakeholder = body.isCustStakeholder;
//			$scope.inputVO.custQValue = body.custQValue;
			$scope.mappingSet['trustAcct']= body.trustAcct; 
			$scope.mappingSet['credit']= body.creditAcct; 
			$scope.mappingSet['debit']= body.debitAcct;


			$scope.setAcctDisplay(['credit','debit']);	//將帳號轉成display暫存陣列
			$scope.inputVO.custName = body.custName;
			$scope.inputVO.kycLV = body.kycLevel;
			$scope.inputVO.kycDueDate =body.kycDueDate;
			$scope.inputVO.custRemarks = body.custRemarks;
			$scope.inputVO.isOBU = body.outFlag;
			$scope.inputVO.profInvestorYN = body.profInvestorYN;
			$scope.inputVO.piDueDate = body.piDueDate;
			$scope.inputVO.debitAcct = (body.debitAcct.length > 0 ? body.debitAcct[0].LABEL : "");				//扣款帳號
			$scope.inputVO.trustAcct = (body.trustAcct.length > 0 ? body.trustAcct[0].LABEL : "");				//信託帳號
			$scope.inputVO.creditAcct = (body.creditAcct.length > 0 ? body.creditAcct[0].LABEL : "");			//收益入帳帳號
			$scope.inputVO.custProType = body.custProType;							//專投種類
//			$scope.inputVO.debitAcct = (body.debitAcct.length > 0 ? body.debitAcct[0].LABEL : "");				//扣款帳號
//			$scope.inputVO.trustAcct = $scope.inputVO.debitAcct;
//			$scope.inputVO.creditAcct = $scope.inputVO.debitAcct;
			$scope.inputVO.hnwcYN = body.hnwcYN;
			$scope.inputVO.hnwcServiceYN = body.hnwcServiceYN;
			//自然人不須輸入授權交易人員，法人才需要輸入
			$scope.inputVO.disableAuthId = $scope.inputVO.custID.length >= 10 ? true : false;
			$scope.inputVO.flagNumber = body.flagNumber;														//90天內是否有貸款紀錄 Y/N
			
			if($scope.inputVO.hnwcYN === "Y") {
				$scope.showMsg("ehl_01_sot510_002");
			}
		}
		
		// 取得商品資訊
		$scope.getProdDTL = function(){
			$scope.inputVO.prodID = $filter('uppercase')($scope.inputVO.prodID);
			if($scope.inputVO.prodID) {
				$scope.sendRecv("SOT510", "getProdDTL", "com.systex.jbranch.app.server.fps.sot510.SOT510InputVO", {"prodID":$scope.inputVO.prodID,
																												   "custID":$scope.inputVO.custID,
																												   "trustTS":$scope.inputVO.trustTS},
						function(tota, isError) {
							$scope.clearProd();
							if (!isError) {
								if (tota[0].body.errorMsg != null && tota[0].body.errorMsg != "") {
									//適配有錯誤訊息
									$scope.showErrorMsg(tota[0].body.errorMsg);
									$scope.inputVO.prodID = "";
								} else if(tota[0].body.prodRefVal && tota[0].body.prodRefVal.length > 0){
									if (tota[0].body.warningMsg != null && tota[0].body.warningMsg != "") {
										//適配有警告訊息
										var dialog = ngDialog.open({
											template: 'assets/txn/CONFIRM/CONFIRM.html',
											className: 'CONFIRM',
											showClose: false,
											scope : $scope,
											controller: ['$scope', function($scope) {
												$scope.dialogLabel = tota[0].body.warningMsg;
								            }]
										}).closePromise.then(function (data) {
											if (data.value === 'successful') {
	//											$scope.custInfo(tota[0].body);
	//											deferred.resolve("success"); 
	//											return deferred.promise;
											} else {
												$scope.inputVO.prodID = "";
												deferred.resolve("");
											}
										});
									}		
									$scope.prodDTL=tota[0].body.prodRefVal[0];
									$scope.inputVO.prodName=$scope.prodDTL.BondName;
									$scope.inputVO.bondVal =tota[0].body.bondVal;
									$scope.inputVO.prodRiskLV=$scope.prodDTL.ProdRiskLv;
									$scope.inputVO.prodCurr=$scope.prodDTL.ProdCurr;
									$scope.inputVO.prodMinBuyAmt =$scope.prodDTL.ProdMinBuyAmt;
									$scope.inputVO.prodMinGrdAmt=$scope.prodDTL.ProdMinGrdAmt;
									$scope.inputVO.refVal = $scope.prodDTL.RefVal;	//參考報價
									$scope.inputVO.hnwcBuy = tota[0].body.hnwcBuy;										//商品限高資產客戶申購註記
									$scope.sumBDS =tota[0].body.sumBDS==null?0:tota[0].body.sumBDS; //此單已申購金額
									$scope.nvlAMT =tota[0].body.nvlAMT==null?0:tota[0].body.sumBDS; //前一日投資AUM
									$scope.sumITEM=tota[0].body.sumITEM==null?0:tota[0].body.sumBDS;//SI已申購金額+SI庫存金額+SN已申購金額+SN在途申購金額+SN庫存金額
									$scope.checkTrustAcct();
									
									if ($scope.inputVO.trustTS == 'M') {
										if (!$scope.checkAccCurrency($scope.acctCurrencyByM)) {
											$scope.inputVO.contractID = '';
										}
									}
								}else{
									$scope.inputVO.prodID='';
									$scope.showMsg("ehl_01_common_009");
								}
								return;
							} else {
								$scope.inputVO.prodID='';
							}
				});
			}else{
				$scope.clearProd();
			}
		};
		
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
		
		//檢核授權交易人員ID(身分證字號)
		$scope.validateAuthID = function() {
			debugger
			$scope.inputVO.authID = $filter('uppercase')($scope.inputVO.authID);
			var validAuthID = validateService.checkCustID($scope.inputVO.authID); //自然人和法人檢查
			if(!validAuthID){ 
				$scope.inputVO.authID = '';
			}
		}
		
		//申購面額檢核
		$scope.checkUnitNum = function(){
			if(!$scope.purchaseAmt){
				$scope.inputVO.trustAmt=undefined;
				$scope.inputVO.totAmt=undefined;
				return;
			}
			$scope.purchaseAmt=$scope.purchaseAmt.replace(/,/g, '');
			$scope.inputVO.purchaseAmt=$scope.purchaseAmt;
			if ($scope.inputVO.purchaseAmt >= $scope.inputVO.prodMinBuyAmt && 
				($scope.inputVO.purchaseAmt % $scope.inputVO.prodMinGrdAmt) == 0) 
			{
					$scope.inputVO.trustAmt=$scope.inputVO.purchaseAmt;
					$scope.inputVO.totAmt=$scope.inputVO.purchaseAmt;
			} else {
					//警示並清空purchaseAmt(申購面額)
					$scope.showErrorMsg("ehl_01_sot310_007");
					$scope.inputVO.purchaseAmt=undefined;
					$scope.inputVO.trustAmt=undefined;
					$scope.inputVO.totAmt=undefined;
					$scope.purchaseAmt=undefined;
			}
			if($scope.purchaseAmt){
				$scope.purchaseAmt = $filter('number')($scope.purchaseAmt);
			}
		};
		
		//下一步
		$scope.next = function() {
			$scope.inputVO.warningMsg = "";
			
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg("ehl_01_common_022");
	    		return;
	    	}
			
//			if(!$scope.inputVO.disableAuthId && $scope.inputVO.authID == '') {
//				$scope.showErrorMsg("請輸入授權交易人員");
//				return;
//			}
			
			//(此單申購金額+此單已申購金額(1))/ 前一日投資AUM(4) >=20%，跳出警示「單一商品申購金額超過投資AUM之20%。」ehl_01_sot410_008
//			if(Number(($scope.sumBDS+$scope.inputVO.purchaseAmt)/$scope.nvlAMT) >= 0.2){
//				$scope.inputVO.warningMsg = $filter('i18n')('ehl_01_sot410_008');
//			}
			// (SI已申購金額+SI庫存金額+SN已申購金額+SN在途申購金額+SN庫存金額(2)+此單申購金額)/前一日投資AUM(4) >=50%，跳出警示「衍生性商品餘額超過投資AUM的50%。」ehl_01_sot410_009
//			if(Number(($scope.sumITEM+$scope.inputVO.purchaseAmt)/$scope.nvlAMT) >= 0.5){
//				if($scope.inputVO.warningMsg != "") $scope.inputVO.warningMsg += "\n";
//				$scope.inputVO.warningMsg += $filter('i18n')('ehl_01_sot410_009');
//			}
			$scope.DEBIT_ACCT=undefined;
			for(var i=0;i<$scope.mappingSet['debit'].length;i++){
				if($scope.inputVO.debitAcct == $scope.mappingSet['debit'][i].DATA){
					$scope.DEBIT_ACCT = $scope.mappingSet['debit'][i].DEBIT_ACCT;
				}
			}
			
			if ($scope.DEBIT_ACCT < $scope.inputVO.totAmt) {
				if($scope.inputVO.warningMsg != "") $scope.inputVO.warningMsg += "\n";
				$scope.inputVO.warningMsg = $filter('i18n')('ehl_01_sot310_008');
			}
			
			$scope.sendRecv("SOT510", "next", "com.systex.jbranch.app.server.fps.sot510.SOT510InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.errorMsg != null) {
								$scope.showErrorMsg(tota[0].body.errorMsg);
								return;
							} else {
								if ($scope.inputVO.warningMsg != "" || (tota[0].body.warningMsg != null && tota[0].body.warningMsg != "")) {
									if(tota[0].body.warningMsg != null && tota[0].body.warningMsg != "")
										$scope.inputVO.warningMsg = $scope.inputVO.warningMsg + "\n" + tota[0].body.warningMsg;
									//適配有警告訊息
									var dialog = ngDialog.open({
										template: 'assets/txn/CONFIRM/CONFIRM.html',
										className: 'CONFIRM',
										showClose: false,
										scope : $scope,
										controller: ['$scope', function($scope) {
											$scope.dialogLabel = $scope.inputVO.warningMsg + "\n是否繼續";
							            }]
									}).closePromise.then(function (data) {
										if (data.value === 'successful') {
											$scope.connector('set','SOTTradeSEQ', $scope.inputVO.tradeSEQ);

											if ($scope.inputVO.trustTS == 'S') {
												$scope.connector('set','SOT511_warningMsg', $scope.inputVO.warningMsg);
												if ($scope.fromFPS) {
													// from FPS_SOT.js
													$scope.setSOTurl('assets/txn/SOT511/SOT511.html');
												} else {
													$rootScope.menuItemInfo.url = "assets/txn/SOT511/SOT511.html";
												}
											} else {
												$scope.connector('set','SOT516_warningMsg', $scope.inputVO.warningMsg);
												if ($scope.fromFPS) {
													// from FPS_SOT.js
													$scope.setSOTurl('assets/txn/SOT516/SOT516.html');
												} else {
													$rootScope.menuItemInfo.url = "assets/txn/SOT516/SOT516.html";
												}
											}
										}
									});
								} else {
									$scope.connector('set','SOTTradeSEQ', $scope.inputVO.tradeSEQ);

									if ($scope.inputVO.trustTS == 'S') {
										$scope.connector('set','SOT511_warningMsg', "");

										if ($scope.fromFPS) {
											// from FPS_SOT.js
											$scope.setSOTurl('assets/txn/SOT511/SOT511.html');
										} else {
											$rootScope.menuItemInfo.url = "assets/txn/SOT511/SOT511.html";
										}
									} else {
										$scope.connector('set','SOT516_warningMsg', "");

										if ($scope.fromFPS) {
											// from FPS_SOT.js
											$scope.setSOTurl('assets/txn/SOT516/SOT516.html');
										} else {
											$rootScope.menuItemInfo.url = "assets/txn/SOT516/SOT516.html";
										}
									}

								}	
								
								return;
							}
						}
			});
		};
		
		$scope.save = function(){
//			alert(JSON.stringify($scope.inputVO));
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg("ehl_01_common_022");
	    		return;
	    	}
			$scope.sendRecv("SOT510", "save", "com.systex.jbranch.app.server.fps.sot510.SOT510InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
						return;
						}
			});
		};
		
		$scope.goPRD140=function() {
			var trustTS = $scope.inputVO.trustTS;
			
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT510/SOT510_ROUTE.html',
				className: 'PRD140',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					$scope.isPop=true;
	        		$scope.routeURL = 'assets/txn/PRD140/PRD140.html';
	        		$scope.txnName = "商品搜尋";
	        		$scope.trustTS = trustTS;
	        		$scope.fromSOTProdYN = "Y"; //下單商品搜尋//下單只需要輸入客戶ID時檢核，商品搜尋時不需再次檢核
	        		$scope.cust_id=$scope.$parent.inputVO.custID;
	            }]
			}).closePromise.then(function (data) {
				if(data.value && data.value != 'cancel'){
						$scope.inputVO.prodID = data.value;
						$scope.getProdDTL();
				}
			});
		};
		
		
		$scope.checkTrustAcct = function(){//debugger;
//			$scope.inputVO.debitAcct='';
//			$scope.inputVO.creditAcct='';
			$scope.cmbDebitAcct=false;
			$scope.cmbCreditAcct=false;
			$scope.setAcctDisplay(['debit','credit']); //初始化
			
			//商品幣別 無此幣扣款帳號要增加
			if ($scope.inputVO.prodCurr) {
				angular.forEach($scope.mappingSet['trustAcct'], function(row){
					if (sotService.is168(row.DATA)) {
						var checkFlag = false;
						angular.forEach($scope.mappingSet['debit#DISPLAY'], function(row2){
							var checkAcct = row.DATA+'_'+$scope.inputVO.prodCurr;//檢核是否有此幣別帳號
							if (checkAcct == row2.DATA) {
								checkFlag = true;
							}
						});
						if (!checkFlag) {
							$scope.mappingSet['debit#DISPLAY'].push({LABEL: row.DATA+'_'+$scope.inputVO.prodCurr, 
																		  DATA: row.DATA+'_'+$scope.inputVO.prodCurr,
																		  DEBIT_ACCT:'0',
																		  CURRENCY:$scope.inputVO.prodCurr,
																		  label:row.DATA+'_'+$scope.inputVO.prodCurr,
																		  value:row.DATA+'_'+$scope.inputVO.prodCurr});
						}
					}
				});
			}
			
			//信託帳號檢核
			if ($scope.inputVO.trustAcct && sotService.is168($scope.inputVO.trustAcct)) {
				$scope.mappingSet['debit#DISPLAY'] = $filter('filter')($scope.mappingSet['debit#DISPLAY'],
																		{DATA: $scope.inputVO.trustAcct},
																		function(actual, expected) { return angular.equals(actual.split("_")[0], expected)}
																	  );
				$scope.mappingSet['credit#DISPLAY'] = $filter('filter')($scope.mappingSet['credit#DISPLAY'],
																		{DATA: $scope.inputVO.trustAcct},
																		function(actual, expected) { return angular.equals(actual, expected)}
																	   );
			}
			//有傳信託業務別 和 商品幣別 
			if ($scope.inputVO.trustCurrType){
	 		   if($scope.inputVO.trustCurrType=='N'){ //N台幣
	 			 $scope.mappingSet['debit#DISPLAY'] = $filter('filter')($scope.mappingSet['debit#DISPLAY'], {CURRENCY: 'TWD'});
//	 			 $scope.mappingSet['credit#DISPLAY'] = $filter('filter')($scope.mappingSet['credit#DISPLAY'], {CURRENCY: 'TWD'});
	 		   }else if($scope.inputVO.prodCurr){	  //計價
	 			  $scope.mappingSet['debit#DISPLAY'] = $filter('filter')($scope.mappingSet['debit#DISPLAY'], {CURRENCY: $scope.inputVO.prodCurr});
//		 		  $scope.mappingSet['credit#DISPLAY'] = $filter('filter')($scope.mappingSet['credit#DISPLAY'], {CURRENCY: $scope.inputVO.prodCurr});
	 		   }else if ($scope.inputVO.trustCurrType=='Y'){  //Y外幣
	 			  $scope.mappingSet['debit#DISPLAY'] = $filter('filter')($scope.mappingSet['debit#DISPLAY'],{CURRENCY: "TWD"},function(actual, expected) { console.log(actual); return !angular.equals(actual, expected)});
//		 		  $scope.mappingSet['credit#DISPLAY'] = $filter('filter')($scope.mappingSet['credit#DISPLAY'],{CURRENCY: "TWD"},function(actual, expected) { console.log(actual); return !angular.equals(actual, expected)});
	 		   }
			} 
			if($scope.mappingSet['debit#DISPLAY'].length==1 && $scope.inputVO.trustTS == 'S'){
				$scope.inputVO.debitAcct=$scope.mappingSet['debit#DISPLAY'][0].DATA;
				$scope.cmbDebitAcct=true;
			}
			if($scope.mappingSet['credit#DISPLAY'].length==1 && $scope.inputVO.trustTS == 'S'){
				$scope.inputVO.creditAcct=$scope.mappingSet['credit#DISPLAY'][0].DATA;
				$scope.cmbCreditAcct=true;
			}
			if ($scope.inputVO.trustTS == 'S') {
				if($filter('filter')($scope.mappingSet['debit#DISPLAY'], {DATA: $scope.inputVO.debitAcct}).length==0){
					$scope.inputVO.debitAcct='';
				}
				if($filter('filter')($scope.mappingSet['credit#DISPLAY'], {DATA: $scope.inputVO.creditAcct}).length==0){
					$scope.inputVO.creditAcct='';
				}
			}
			$scope.changeAcct();
		};
		
		$scope.changeAcct = function () {
			var debitAcct = $scope.inputVO.debitAcct.split("_");
			$scope.inputVO.creditAcct = debitAcct[0];
			
			$scope.showAvbBalance='';
			$scope.showCurrency  ='';
			for(var i=0;i<$scope.mappingSet['debit#DISPLAY'].length;i++){
				if($scope.mappingSet['debit#DISPLAY'][i].LABEL==$scope.inputVO.debitAcct){
					$scope.showAvbBalance=$scope.mappingSet['debit#DISPLAY'][i].DEBIT_ACCT;
					$scope.showCurrency  =$scope.mappingSet['debit#DISPLAY'][i].CURRENCY;
					break;
				}
				
			}
		};
		
//		//判斷條件若成立，則顯示暫存，下一步
		$scope.$watch('parameterTypeEditForm.$invalid', function(newValue, oldValue) {
			if(newValue){
				$scope.disBtn = true;
			}else{
				$scope.disBtn = false;
			}
		});
		
        $scope.checkAccCurrency = function (currency) {
            console.log("SOT510.js 走進checkAccCurrency")
        	var acctList = currency.split(",");

            if ($scope.inputVO.prodCurr != undefined && $scope.inputVO.prodCurr != '' && $scope.inputVO.prodCurr != null) {
//				console.log('prodCurr:' + $scope.inputVO.prodCurr);
//				console.log('acctList:' + acctList);
//				console.log('split:' + acctList.indexOf($scope.inputVO.prodCurr));
            	console.log("SOT510.js 商品幣別:  " + $scope.inputVO.prodCurr);
                if (acctList.indexOf($scope.inputVO.prodCurr) == -1) {
                    $scope.showErrorMsg("該契約編號之帳號尚未建立該幣別。");
                    return false;
                }
            }

            return true;
        }
		
		$scope.init();

		if(!$scope.connector('get','SOTCustID')) {
			//"不是"從快查或別的交易過來，帶CUSTID，維持原來
			$scope.inputVO.otherWithCustId = false;
			$scope.query();
		} else {
			//從快查或別的交易過來，帶CUSTID，先做高齡檢核
			$scope.inputVO.otherWithCustId = true;
			$scope.inputVO.custID = $scope.connector('get','SOTCustID');
			$scope.validateSeniorCust();
		}
});