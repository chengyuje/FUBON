/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT410Controller',
	function($rootScope, $scope, $controller, $confirm, $filter, socketService, ngDialog, projInfoService, $q, validateService,getParameter, sotService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT410Controller";
		//繼承PRD100高齡檢核
		$controller('PRD100Controller', {$scope: $scope});
		
		//xml參數初始化
		getParameter.XML(["SOT.SPEC_CUSTOMER"], function(totas) {
			if (totas) {
				$scope.mappingSet['SOT.SPEC_CUSTOMER'] = totas.data[totas.key.indexOf('SOT.SPEC_CUSTOMER')];//客戶註記
			}
		});
		
		//按[清除]鍵   清除序號
		$scope.clearTradeSEQ = function(){
        	$scope.inputVO.tradeSEQ='';
        	$scope.query();
        }
		
		// 取得新交易序號
		$scope.getTradeSEQ = function() { 
			 
				$scope.sendRecv("SOT712", "getTradeSEQ", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.tradeSEQ = tota[0].body.tradeSEQ;//回傳新的交易序號
								return;
							}
				});
			 
		};
		
		$scope.init = function() {
			$scope.sumBDS =''; //此單已申購金額
			$scope.nvlAMT =''; //前一日投資AUM
			$scope.sumITEM='';//SI已申購金額+SI庫存金額+SN已申購金額+SN在途申購金額+SN庫存金額
			
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST']= []; 		        //扣款帳號
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY']= []; 		//扣款帳號
			$scope.mappingSet['prodAcct']= [];                          //組合式商品帳號
			
			$scope.inputVO = {	
					tradeSEQ:'',								//下單交易序號
					custID: '', 								//客戶ID
					custName: '', 								//客戶姓名
					prodType: '4',  							//4：SI
		        	tradeType: '1', 							//1： 申購
		        	seniorAuthType: 'S', 						//高齡評估表授權種類(S:下單、A：適配)
		        	trustTS: "S", 								//高齡評估表主管確認表使用
					kycLv: '', 									//KYC等級
					kycDueDate: undefined, 						//KYC效期
					profInvestorYN: '', 						//是否為專業投資人
					piDueDate: undefined, 						//專業投資人效期
					custRemarks: '', 							//客戶註記
					outFlag:'',									//OBU註記
					isOBU: '', 									//是否為OBU客戶
					prodID: '', 								//商品ID
					prodName: '',								//商品名稱
					purchaseAmt: undefined,							//申購面額
					prodCurr: '', 								//計價幣別
					prodRiskLv: '', 							//產品風險等級
					prodMinBuyAmt: undefined, 					//最低申購金額
					prodMinGrdAmt: undefined,					//累進申購面額
					narratorID: '',								//解說人員ID
					narratorName: '',							//解說人員姓名
					authID: '',									//授權解說人員ID
//					authName: '',								//授權解說人員姓名
					debitAcct:'',								//扣款帳號
					prodAcct:'',								//組合式商品帳號
					totAmt:'',
					creditAcct: '',								//收益入帳帳號/贖回款入帳帳號
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
			
			// if data
			$scope.inputVO.tradeSEQ=$scope.connector('get', 'SOTTradeSEQ');
			$scope.connector('set', 'SOTTradeSEQ', null);
			
//			商品搜尋
			if($scope.connector('get','PR140_ID')){
				$scope.disProdID=true;
				$scope.inputVO.prodID=$scope.connector('get','PR140_ID');
				$scope.connector('set','PR140_ID',null);
				$scope.getProdDTL();
			}
			 
		};
		$scope.init();
		
		//查詢帳號幣別		
		$scope.getAcctCurrency = function() { 
			var prodCurrency = $scope.inputVO.prodCurr;
			var acctCcy = undefined;
			if (prodCurrency) { 
				acctCcy = prodCurrency;
		    }  
			return acctCcy;
		 };
	 
	     //將帳號轉成display暫存陣列
		 $scope.setAcctDisplay = function(acctNameList){
			 /*SI
			    帳號來源 = SOT701
			     組合式商品帳號不會回傳幣別和餘額，僅顯示帳號
			     扣款帳號，只顯示該商品幣別的帳號
			     扣款帳號幣別餘額需顯示於帳號旁邊
			     扣款帳號下拉選單中加上幣別資料。如：300168097213_USD
			  */
			 
			   //查詢帳號幣別		
		    var acctCurrency = $scope.getAcctCurrency();
			for(var i=0;i<acctNameList.length;i++){
				if(acctCurrency){
//					angular.forEach($scope.mappingSet['SOT.DEBIT_ACCT_LIST'], function(row){
//						if (sotService.is168(row.DATA)) {
//							var checkFlag = false;
//							var debitAcct = row.DATA.substr(0,14);
//							debugger;
//							angular.forEach($scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY'], function(row2){
//								var checkAcct = debitAcct+'_'+acctCurrency;//檢核是否有此幣別帳號
//								if (checkAcct == row2.DATA) {
//									checkFlag = true;
//								}
//							});							
//							if (!checkFlag) {
//								debugger;
//								$scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY'].push({LABEL: debitAcct+'_'+acctCurrency, 
//																			  DATA: debitAcct+'_'+acctCurrency,
//																			  AVBBALANCE:'0',
//																			  CURRENCY: acctCurrency,
//																			  label: debitAcct+'_'+acctCurrency,
//																			  value: debitAcct+'_'+acctCurrency});
//							}
//						}
//					});
					$scope.mappingSet[acctNameList[i]+'#DSIPLAY'] = $filter('filter')($scope.mappingSet[acctNameList[i]], acctCurrency);
				}else{
					$scope.mappingSet[acctNameList[i]+'#DSIPLAY'] = $scope.mappingSet[acctNameList[i]];
				}
			}
		}
		
		//清除客戶資料
		$scope.clearCust = function(){ 
			$scope.inputVO.custName = '';
			$scope.inputVO.kycLv = '';
			$scope.inputVO.kycDueDate = undefined;
			$scope.inputVO.custRemarks = '';
			$scope.inputVO.outFlag = '';
			$scope.inputVO.profInvestorYN = '';
			$scope.inputVO.piRemark = '';                               //專業投資人註記
			$scope.inputVO.piDueDate = undefined;
			$scope.inputVO.debitAcct='';								//扣款帳號
			$scope.inputVO.trustAcct='';								//信託帳號
			$scope.inputVO.prodAcct='';									//組合式商品帳號
			$scope.inputVO.creditAcct = '';								//收益入帳帳號
			$scope.showAvbBalance=undefined;                            //扣款帳號餘額
			$scope.showCurrency=undefined;                              //扣款帳號餘額幣別
			$scope.inputVO.hnwcYN = '';
			$scope.inputVO.hnwcServiceYN = '';
			$scope.inputVO.disableAuthId = false;
			$scope.inputVO.authID = '';
//			$scope.inputVO.authName = '';
			$scope.clearProd();
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST'] = []; 		        //扣款帳號
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY']= []; 		//扣款帳號
			$scope.mappingSet['prodAcct']= [];                          //組合式商品帳號			  
		};
		
		//商品清除
        $scope.clearProd = function() {
        	$scope.sumBDS =''; //此單已申購金額
			$scope.nvlAMT =''; //前一日投資AUM
			$scope.sumITEM=''; //SI已申購金額+SI庫存金額+SN已申購金額+SN在途申購金額+SN庫存金額
			
        	$scope.inputVO.prodName = '';								//商品名稱
        	$scope.inputVO.prodCurr = '';								//計價幣別
        	$scope.inputVO.prodRiskLv = '';								//產品風險等級
			$scope.inputVO.prodMinBuyAmt = undefined;					//最低申購面額
			$scope.inputVO.prodMinGrdAmt = undefined;					//累計申購面額
			$scope.inputVO.trustCurr = '';								//信託幣別
			$scope.inputVO.refVal = undefined;							//參考報價
			$scope.inputVO.refValDate = undefined;						//參考報價日期
			$scope.inputVO.tradeDate = undefined;						//交易日期
			$scope.inputVO.purchaseAmt = undefined;						//申購面額
			$scope.inputVO.hnwcBuy = '';
		 
			$scope.setAcctDisplay(["SOT.DEBIT_ACCT_LIST"]);
			
		}
        
        //設定客戶資料
        $scope.setCustInfo=function(body){
			$scope.inputVO.fatcaType = body.fatcaType;
//			$scope.inputVO.isCustStakeholder = body.isCustStakeholder;
//			$scope.inputVO.custQValue = body.custQValue; 
			$scope.inputVO.custName = body.custName;						//客戶姓名
			$scope.inputVO.kycLv = body.kycLv;								//KYC等級
			$scope.inputVO.kycDueDate = $scope.toJsDate(body.kycDueDate);	//KYC效期
			$scope.inputVO.custRemarks = body.custRemarks;					//客戶註記
			$scope.inputVO.isOBU = body.isOBU;								//是否為OBU客戶
			$scope.inputVO.profInvestorYN = body.profInvestorYN;			//是否為專業投資人
			$scope.inputVO.piRemark = body.piRemark;			            //專業投資人註記
			$scope.inputVO.piDueDate = $scope.toJsDate(body.piDueDate);		//專業投資人效期
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST']= body.debitList; 	    //扣款帳號
			$scope.setAcctDisplay(["SOT.DEBIT_ACCT_LIST"]);
			$scope.inputVO.prodAcct = body.prodAcctList[0].LABEL; 			//組合式商品帳號
			$scope.inputVO.narratorID= body.narratorID;                     //解說專員原編
			$scope.inputVO.narratorName= body.narratorName;                 //解說專員姓名
			$scope.inputVO.prodID = ""; 									//重查客戶也要清產品
			$scope.inputVO.pro1500 = body.pro1500;							//專業自然人註記
			$scope.inputVO.trustProCorp = body.trustProCorp;
			$scope.inputVO.hnwcYN = body.hnwcYN;
			$scope.inputVO.hnwcServiceYN = body.hnwcServiceYN;
			//自然人不須輸入授權交易人員，法人才需要輸入
			$scope.inputVO.disableAuthId = $scope.inputVO.custID.length >= 10 ? true : false;
			$scope.inputVO.flagNumber = body.flagNumber;					//90天內是否有貸款紀錄 Y/N
		};
		
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
			if($scope.inputVO.otherWithCustId) { //有帶客戶ID(快查)
				$scope.queryChkSenior();
			} else {
				$scope.getSOTCustInfo(true);
			}
			$scope.connector('set','SOTCustID',null);
		};
		
		// SOT701-客戶電文
		$scope.getSOTCustInfo = function(input) {
			debugger;
			var defer = $q.defer();
			$scope.inputVO.custID = $filter('uppercase')($scope.inputVO.custID);
			var validCustID = validateService.checkCustID($scope.inputVO.custID); //自然人和法人檢查
//			console.log("custID:" + $scope.inputVO.custID + ", checkCustID: "+validCustID);
			if(validCustID==false){ 
				$scope.inputVO.custID='';
			}
			if(validCustID==false || input) { 
				$scope.clearCust();
				$scope.clearProd();
				$scope.inputVO.prodID = "";
			}
			
			if(validCustID) {	
				$scope.clearCust();//清除客戶資料
				$scope.clearProd();//清除產品資料
				$scope.sendRecv("SOT410", "getSOTCustInfo", "com.systex.jbranch.app.server.fps.sot410.SOT410InputVO", {'custID':$scope.inputVO.custID, 'prodType':4, 'tradeType':1},
				function(tota, isError) {
					$scope.clearCust();
					if (!isError) {
						if(tota[0].body.KYCResult == 'N'){
							$scope.showErrorMsg('ehl_02_KYC310_021');
							$scope.inputVO.custID = "";
							return;
						}
						//debugger;
						$scope.toDay = $filter('date')(new Date(),'yyyy-MM-dd 00:00:00');//取當日日期
						//若非為本行客戶或未開立組合式商品帳戶，需有訊息告知，並須擋下單。
						if(tota[0].body.debitList.length == 0 || tota[0].body.prodAcctList.length == 0){
							$scope.inputVO.custID = "";
							$scope.showMsg('ehl_02_sot410_001');
							return;
						}
						// 若為禁銷客戶，出現提示訊息禁止下單。
						if(tota[0].body.noSale == 'Y' ){
							$scope.inputVO.custID = "";
							$scope.showMsg('ehl_01_sot310_003');
						}
						// 若為死亡戶/禁治產等狀況，不可下單。
						else if(tota[0].body.deathFlag == 'Y' || tota[0].body.isInterdict == 'Y' ){
							$scope.inputVO.custID = "";
							$scope.showMsg('ehl_01_sot310_004');
						}
						// 若為拒銷客戶，出提示訊息請專員確認是否繼續。
						else if(tota[0].body.rejectProdFlag == 'Y' ){
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
//						else if($scope.toJsDate(tota[0].body.kycDueDate) < $scope.toJsDate($scope.toDay)){
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
						//FOR CBS TEST日期
						else if(tota[0].body.profInvestorYN == "Y" && tota[0].body.isPiDueDateUseful ){ //$scope.toJsDate(tota[0].body.piDueDate) < $scope.toJsDate($scope.toDay)
							$scope.showMsg('ehl_01_sot310_006');
							$scope.setCustInfo(tota[0].body); //設定客戶資料
							defer.resolve("success"); 
					    } else {
					    	$scope.setCustInfo(tota[0].body); //設定客戶資料
					    	defer.resolve("success");
					    }
					}
				});
			}
			return defer.promise;
		};
		
		//商品搜尋
		$scope.goPRD150 = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT410/SOT410_ROUTE.html',
				className: 'PRD150',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					$scope.isPop=true;
	        		$scope.txnName = "商品搜尋";
	        		$scope.fromSOTProdYN = "Y"; //下單商品搜尋//下單只需要輸入客戶ID時檢核，商品搜尋時不需再次檢核
	        		$scope.cust_id=$scope.$parent.inputVO.custID;
	        		$scope.routeURL = 'assets/txn/PRD150/PRD150.html';
	            }]
			}).closePromise.then(function (data) {
				if(data.value && data.value != 'cancel'){
					$scope.inputVO.prodID = data.value;
					$scope.getProdDTL();
				}
			});
		};
		
		//適配結果代碼以逗號分隔
		$scope.showFitnessMessage = function (fitnessMessage) {
			if(fitnessMessage && fitnessMessage!=''){
				$scope.connector('set','fitnessMessageList', fitnessMessage);
				ngDialog.closeAll();
	        	var dialog = ngDialog.open({
	        		template: 'assets/txn/SOT712/FitnessMessageDialog.html',
	        		className: '',
	        		controller: ['$scope',function($scope){ 
	        		}]
	        	});
	        	dialog.closePromise.then(function(data){  
	        		 
	        	});
			}
		};
		// 商品查詢
		$scope.getProdDTL = function () {
			var deferred = $q.defer();
			$scope.clearProd();
			if($scope.inputVO.prodID) {
				$scope.inputVO.prodID = $scope.inputVO.prodID.toUpperCase();
				$scope.sendRecv("SOT410", "getProdDTL", "com.systex.jbranch.app.server.fps.sot410.SOT410InputVO", $scope.inputVO,
						function(tota, isError) {
					debugger;
							if (!isError) {
								if (tota[0].body.errorMsg != null && tota[0].body.errorMsg != "") {
									//適配有錯誤訊息
									$scope.showErrorMsg(tota[0].body.errorMsg);
									$scope.inputVO.prodID = "";
									$scope.clearProd();
								} else if (tota[0].body.prodDTL && tota[0].body.prodDTL.length > 0) {
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
												$scope.clearProd();
												deferred.resolve("");
											}
										});
									}		
									
									$scope.inputVO.prodName = tota[0].body.prodDTL[0].SI_CNAME;							    //商品名稱
									$scope.inputVO.prodRiskLv = tota[0].body.prodDTL[0].RISKCATE_ID;						//產品風險等級
									$scope.inputVO.prodMinBuyAmt = tota[0].body.prodDTL[0].BASE_AMT_OF_PURCHASE;			//最低申購金額
									$scope.inputVO.prodMinGrdAmt = tota[0].body.prodDTL[0].UNIT_AMT_OF_PURCHASE;			//累進申購金額	
									$scope.inputVO.prodCurr = tota[0].body.prodDTL[0].CURRENCY_STD_ID;						//計價幣別
									$scope.inputVO.hnwcBuy =  tota[0].body.prodDTL[0].HNWC_BUY;								//商品限高資產客戶申購註記
//									$scope.setAcctDisplay(["SOT.DEBIT_ACCT_LIST"]);
//									$scope.inputVO.debitAcct = ($scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY'].length > 0 ? $scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY'][0].LABEL : "");				//扣款帳號
									$scope.sumBDS =tota[0].body.sumBDS; //此單已申購金額
									$scope.nvlAMT =tota[0].body.nvlAMT; //前一日投資AUM
									$scope.sumITEM=tota[0].body.sumITEM;//SI已申購金額+SI庫存金額+SN已申購金額+SN在途申購金額+SN庫存金額
									
									$scope.setAcctDisplay(["SOT.DEBIT_ACCT_LIST"]);
									$scope.inputVO.debitAcct = ($scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY'].length > 0 ? $scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY'][0].LABEL : "");				//扣款帳號
									
									$scope.showFitnessMessage(tota[0].body.fitnessMessage);//顯示適配警示Dialog
								}else{
									$scope.inputVO.prodID='';
									$scope.showMsg("ehl_01_common_009");
								}
								deferred.resolve("success");
								return deferred.promise;
								
							} else {  
								if (tota.body.msgData) {
									//$scope.showErrorMsg(tota.body.msgData);
								} else {	
									$scope.showErrorMsg("ehl_01_common_009");
								} 
								$scope.inputVO.prodID = "";
								$scope.clearProd();
							}
							
				});
			} else {
				$scope.clearProd();
				$scope.inputVO.prodID = "";
			}

			deferred.resolve("");
			return deferred.promise;
		};
		
		//申購面額檢核
		$scope.checkUnitNum = function(){
			
			if(!$scope.inputVO.purchaseAmt){
				return;
			}
			
			$scope.inputVO.purchaseAmt=$scope.moneyUnFormat($scope.inputVO.purchaseAmt);
			if($scope.inputVO.prodMinGrdAmt==0 && $scope.inputVO.purchaseAmt > $scope.inputVO.prodMinBuyAmt){
				//警示並清空purchaseAmt(申購面額)
				$scope.showErrorMsg("ehl_01_sot310_009"); //申購面額只能是最低申購面額，不能超過最低申購面額。
				$scope.inputVO.purchaseAmt=undefined;
				return;
			}
			
			if($scope.inputVO.purchaseAmt == $scope.inputVO.prodMinBuyAmt) {
			}else if ($scope.inputVO.purchaseAmt > $scope.inputVO.prodMinBuyAmt && 
				($scope.inputVO.purchaseAmt % $scope.inputVO.prodMinGrdAmt) == 0){
			} else {
					//警示並清空purchaseAmt(申購面額)
					$scope.showErrorMsg("ehl_01_sot310_007");
					$scope.inputVO.purchaseAmt=undefined;
			}
			

			$scope.inputVO.purchaseAmt=$scope.moneyFormat($scope.inputVO.purchaseAmt);
			 
			//alert($scope.inputVO.purchaseAmt);
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
		
		//暫存
		$scope.save = function () {
			$scope.inputVO.purchaseAmt=$scope.moneyUnFormat($scope.inputVO.purchaseAmt);
			if($scope.inputVO.purchaseAmt==''){
				$scope.inputVO.purchaseAmt = undefined;
			}
			$scope.sendRecv("SOT410", "save", "com.systex.jbranch.app.server.fps.sot410.SOT410InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
						} else {
							$scope.showErrorMsg("ehl_01_common_008");
						}
						$scope.inputVO.purchaseAmt=$scope.moneyFormat($scope.inputVO.purchaseAmt);
							
			});
		};
		
		
		//查詢帳號幣別		
		$scope.getAcctCurrency = function() { 
			 var prodCurrency = $scope.inputVO.prodCurr;
			 var acctCcy = undefined;
			 if (prodCurrency){ //有傳信託業務別 和 商品幣別 
		 		   acctCcy = prodCurrency; 
			 } 
			 return acctCcy;
		};	
		
		//扣款帳號
		$scope.changeAcct = function (type) {
		

			/*餘額不足扣總扣款金額，警示提醒
			angular.forEach($scope.mappingSet['SOT.DEBIT_ACCT_LIST'], function(row){
				$scope.debitAcct = row.DEBIT_ACCT;
			});
			*/
			$scope.showAvbBalance=undefined;                            //扣款帳號餘額
			$scope.showCurrency=undefined;                              //扣款帳號餘額幣別
			for(var i=0;i<$scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY'].length;i++){
				if($scope.inputVO.debitAcct == $scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY'][i].DATA){
					$scope.showAvbBalance = $scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY'][i].AVBBALANCE;
					$scope.showCurrency  =$scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY'][i].CURRENCY;
				}
			}
		};
		
		$scope.noCallCustQuery = function () {
			$scope.sendRecv("SOT410", "query", "com.systex.jbranch.app.server.fps.sot410.SOT410InputVO",{'tradeSEQ':$scope.inputVO.tradeSEQ},
					function(tota, isError) {
						if (!isError) {
							
							$scope.carList = tota[0].body.carList;
							$scope.mainList = tota[0].body.mainList;
							if($scope.mainList && $scope.mainList.length !=0){
								$scope.inputVO.custID= $scope.mainList[0].CUST_ID;
								$scope.getSOTCustInfo().then(function(data){ 				//
									$scope.inputVO.prodType= $scope.mainList[0].PROD_TYPE;			//商品類別
									$scope.inputVO.custName= $scope.mainList[0].CUST_NAME;			//客戶姓名
									$scope.inputVO.kycLv= $scope.mainList[0].KYC_LV;				//KYC等級
									$scope.inputVO.kycDueDate= $scope.toJsDate($scope.mainList[0].KYC_DUE_DATE);		//KYC效期
									$scope.inputVO.custRemarks= $scope.mainList[0].CUST_REMARKS;	//客戶註記
									$scope.inputVO.isOBU= $scope.mainList[0].IS_OBU;				//是否為OBU客戶
									$scope.inputVO.profInvestorYN= $scope.mainList[0].PROF_INVESTOR_YN;//是否為專業投資人
									$scope.inputVO.piRemark = tota[0].body.mainList[0].PI_REMARK;	//專業投資人註記
									$scope.inputVO.piDueDate= $scope.toJsDate($scope.mainList[0].PI_DUE_DATE);		//專業投資人效期
									
									if($scope.carList && $scope.carList.length !=0){
//										$scope.inputVO.seqNo=$scope.carList[0].SEQ_NO;				//流水號
//										$scope.inputVO.batchSeq=$scope.carList[0].BATCH_SEQ;		//下單批號
//										$scope.inputVO.tradeSubType=$scope.carList[0].TRADE_SUB_TYPE;//交易類型
										$scope.inputVO.prodID=$scope.carList[0].PROD_ID;
										$scope.inputVO.prodName=$scope.carList[0].PROD_NAME;		//商品名稱
										$scope.inputVO.prodCurr=$scope.carList[0].PROD_CURR;		//計價幣別
										$scope.inputVO.prodRiskLv=$scope.carList[0].PROD_RISK_LV;	//產品風險等級
										$scope.inputVO.prodMinBuyAmt=$scope.carList[0].PROD_MIN_BUY_AMT;//最低申購面額
										$scope.inputVO.prodMinGrdAmt=$scope.carList[0].PROD_MIN_GRD_AMT;//累計申購面額
										$scope.setAcctDisplay(["SOT.DEBIT_ACCT_LIST"]);
										$scope.inputVO.purchaseAmt=$scope.carList[0].PURCHASE_AMT;	//申購金額
										$scope.inputVO.purchaseAmt= $scope.moneyFormat($scope.inputVO.purchaseAmt);
										$scope.inputVO.refVal=$scope.carList[0].REF_VAL;			//參考報價
//										$scope.inputVO.refValDate=$scope.carList[0].REF_VAL_DATE;	//參考報價日期
										$scope.inputVO.debitAcct=$scope.carList[0].DEBIT_ACCT;		//扣款帳號
										 
										$scope.inputVO.debitAcct =$scope.inputVO.debitAcct + "_" + $scope.getAcctCurrency();
										
										$scope.inputVO.prodAcct=$scope.carList[0].PROD_ACCT;		//組合式商品帳號W
										$scope.inputVO.narratorID=$scope.carList[0].NARRATOR_ID;	//解說專員員編
										$scope.inputVO.narratorName=$scope.carList[0].NARRATOR_NAME;//解說專員姓名
										$scope.inputVO.authID=$scope.carList[0].AUTH_ID;			//授權交易人員員編
//										$scope.getTellerName('authID',$scope.inputVO.authID);		//授權交易人員姓名
										$scope.inputVO.bossID=$scope.carList[0].BOSS_ID;			//主管員編
										$scope.getTellerName('bossID',$scope.inputVO.bossID);		//主管姓名
									}
								});
							}
						}
			});
		};
		
		//從快查或別的交易過來，帶CUSTID
		$scope.queryChkSenior = function() {
			if($scope.connector('get','SOTCustID')){
				$scope.inputVO.custID=$scope.connector('get','SOTCustID');
				$scope.connector('set','SOTCustID',null);
				$scope.getSOTCustInfo(true).then(function(data){
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
				$scope.getTradeSEQ();//取得交易序號
			}
		}
		
		$scope.query = function() {
			//從其他地方進入下單
			if($scope.connector('get', 'SOTTradeSEQ')){
				$scope.inputVO.tradeSEQ = $scope.connector('get', 'SOTTradeSEQ');
				$scope.inputVO.carSEQ = $scope.connector('get', 'SOTCarSEQ');
				$scope.connector('set', 'SOTTradeSEQ', null);
				$scope.connector('set', 'SOTCarSEQ', null);
			} else if ($scope.fromFPS){
				console.log($scope.FPSData);				
				$scope.inputVO.custID = $scope.FPSData.custID;//客戶ID					
				$scope.getSOTCustInfo(true).then(function(data){
					$scope.inputVO.prodID = $scope.FPSData.prdID; //商品代號	
					$scope.getProdDTL().then(function(data){
						$scope.inputVO.purchaseAmt = $scope.FPSData.PURCHASE_ORG_AMT; //原幣金額
					});						
				});				
			}
			
			if($scope.inputVO.tradeSEQ) {
				$scope.noCallCustQuery();
			} else {
				$scope.getTradeSEQ();//取得交易序號
			}
		};
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
		
		$scope.nextCheck = function() {
			//讓扣款金額的e-combobox預設值是上次選的
			 
			$scope.changeAcct();
			
			$scope.inputVO.purchaseAmt=$scope.moneyUnFormat($scope.inputVO.purchaseAmt);
			
			//			console.log("nvlAMT"+$scope.nvlAMT);
			//			console.log("sumBDS:"+$scope.sumBDS);
			//			console.log("sumITEM:"+$scope.sumITEM);
			//			console.log("purchaseAmt:"+$scope.inputVO.purchaseAmt);
			//			console.log("check AUM20%: " + Number(($scope.sumBDS+$scope.inputVO.purchaseAmt)/$scope.nvlAMT) );
			//			console.log("check AUM50%: " + Number(($scope.sumITEM+$scope.inputVO.purchaseAmt)/$scope.nvlAMT) );
			
			//(此單申購金額+此單已申購金額(1))/ 前一日投資AUM(4) >=20%，跳出警示「單一商品申購金額超過投資AUM之20%。」ehl_01_sot410_008
//			if(Number(($scope.sumBDS+$scope.inputVO.purchaseAmt)/$scope.nvlAMT) >= 0.2){
//				$scope.showErrorMsg("ehl_01_sot410_008");
//			}
			
			// (SI已申購金額+SI庫存金額+SN已申購金額+SN在途申購金額+SN庫存金額(2)+此單申購金額)/前一日投資AUM(4) >=50%，跳出警示「衍生性商品餘額超過投資AUM的50%。」ehl_01_sot410_009
//			if(Number(($scope.sumITEM+$scope.inputVO.purchaseAmt)/$scope.nvlAMT) >= 0.5){
//				$scope.showErrorMsg("ehl_01_sot410_009");
//			}
			
			var txtMsg ='';
			if ($scope.showAvbBalance < $scope.inputVO.purchaseAmt) {
				var txtMsg = $filter('i18n')('ehl_01_sot310_008');  // sot310_008餘額不足
			}
			$scope.inputVO.purchaseAmt=$scope.moneyFormat($scope.inputVO.purchaseAmt);
			
			return txtMsg;
		}
		
		
		//下一步
		$scope.goNext = function() {
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg("ehl_01_common_022");
	    		return;
	    	}
			
//			if(!$scope.inputVO.disableAuthId && $scope.inputVO.authID == '') {
//				$scope.showErrorMsg("請輸入授權交易人員");
//				return;
//			}
			
			$scope.inputVO.purchaseAmt=$scope.moneyUnFormat($scope.inputVO.purchaseAmt);
			
			$scope.sendRecv("SOT410", "next", "com.systex.jbranch.app.server.fps.sot410.SOT410InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.errorMsg != null) {
								$scope.showErrorMsg(tota[0].body.errorMsg);
								
								return;
							} else {
								if (tota[0].body.warningMsg != null && tota[0].body.warningMsg != "") {
									$scope.inputVO.warningMsg = tota[0].body.warningMsg;
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
											$scope.connector('set','SOT411_warningMsg', $scope.inputVO.warningMsg);
											if ($scope.fromFPS) {
												// from FPS_SOT.js
												$scope.setSOTurl('assets/txn/SOT411/SOT411.html');
											} else {
												$rootScope.menuItemInfo.url = "assets/txn/SOT411/SOT411.html";
											}								
											return;
										}
									});
								} else {
									$scope.connector('set','SOTTradeSEQ', $scope.inputVO.tradeSEQ);
									$scope.connector('set','SOT411_warningMsg', "");
									if ($scope.fromFPS) {
										// from FPS_SOT.js
										$scope.setSOTurl('assets/txn/SOT411/SOT411.html');
									} else {
										$rootScope.menuItemInfo.url = "assets/txn/SOT411/SOT411.html";
									}								
									return;
								}
							}
						}
						$scope.inputVO.purchaseAmt=$scope.moneyFormat($scope.inputVO.purchaseAmt);
							
			});
		}
		
		//檢核餘額才進入下一步
		$scope.next = function() {
			    $scope.inputVO.purchaseAmt=$scope.moneyUnFormat($scope.inputVO.purchaseAmt);
			    var txtMsg = $scope.nextCheck();//檢核餘額不足  txtMsg:餘額不足
			    if (txtMsg == '') {
			    	$scope.goNext();//下一步
			    } else {
				    var deferred = $q.defer();
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
							deferred.resolve("success");
							
							$scope.goNext();//下一步
							
							return deferred.promise;
						} else {
							
							deferred.resolve("");
						}
					});
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
		
});