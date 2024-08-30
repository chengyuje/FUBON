/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT413Controller',
	function($rootScope, $scope, $controller, $confirm, $filter, socketService, ngDialog, projInfoService, $q, validateService,getParameter, sotService, sysInfoService, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT413Controller";
		
		//xml參數初始化
		getParameter.XML(["PRD.FCI_CURRENCY", "SOT.FCI_PM_PRIVILEGE_ID"], function(totas) {
			if (totas) {
				$scope.mappingSet['PRD.FCI_CURRENCY'] = totas.data[totas.key.indexOf('PRD.FCI_CURRENCY')];
				$scope.mappingSet['SOT.FCI_PM_PRIVILEGE_ID'] = totas.data[totas.key.indexOf('SOT.FCI_PM_PRIVILEGE_ID')];
				
				//權限ID
				$scope.privilegeId = sysInfoService.getPriID();
				var findFCIPMRole = $filter('filter')($scope.mappingSet['SOT.FCI_PM_PRIVILEGE_ID'], {DATA: $scope.privilegeId[0]});
				$scope.inputVO.isFCIPMRole = (findFCIPMRole != null && findFCIPMRole != undefined && findFCIPMRole.length > 0) ? true : false;
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
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST']= []; 		        //扣款帳號
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY']= []; 		//扣款帳號
			$scope.mappingSet['prodAcct']= [];                          //組合式商品帳號
			
			$scope.inputVO.prodType = '7';  //7：FCI
        	$scope.inputVO.tradeType = '1'; //1： 申購
			$scope.inputVO = {	
					tradeSEQ:'',								//下單交易序號
					custID: '', 								//客戶ID
					custName: '', 								//客戶姓名
					kycLv: '', 									//KYC等級
					kycDueDate: undefined, 						//KYC效期
					profInvestorYN: '', 						//是否為專業投資人
					piDueDate: undefined, 						//專業投資人效期
					custRemarks: '', 							//客戶註記
					outFlag:'',									//OBU註記
					isOBU: '', 									//是否為OBU客戶
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
					traderCharge : undefined,					//交易員CHARGE
					narratorID: '',								//解說人員ID
					narratorName: '',							//解說人員姓名
					authID: '',									//授權解說人員ID
//					authName: '',								//授權解說人員姓名
					debitAcct:'',								//扣款帳號
					prodAcct:'',								//組合式商品帳號
					totAmt:'',
					creditAcct: '',								//收益入帳帳號/贖回款入帳帳號
					disableAuthId: false,						//disabled AUTH_ID
					hnwcYN: '',									//是否為高資產客戶 Y/N 
					hnwcServiceYN: ''							//可提供高資產商品或服務 Y/N 
			};
			
			// if data
			$scope.inputVO.tradeSEQ = $scope.connector('get', 'SOTTradeSEQ');
			$scope.connector('set', 'SOTTradeSEQ', null);	
			$scope.inputVO.loginBranchID = sysInfoService.getBranchID();
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
        	$scope.inputVO.prodID = '';
        	$scope.inputVO.prodName = '';								//商品名稱
        	$scope.inputVO.prodCurr = '';								//計價幣別
        	$scope.inputVO.prodRiskLv = '';								//產品風險等級
			$scope.inputVO.prodMinBuyAmt = undefined;					//最低申購面額
			$scope.inputVO.prodMinGrdAmt = undefined;					//累計申購面額
			$scope.inputVO.purchaseAmt = undefined;						//申購面額
			$scope.inputVO.monPeriod = undefined;						//天期
			$scope.inputVO.rmProfe = undefined;							//理專收益率
			$scope.inputVO.targetCurrId = undefined;					//連結標的
			$scope.inputVO.strikePrice = undefined;						//履約價
			$scope.inputVO.ftpRate = undefined;							//FTP RATE
			$scope.inputVO.targetName = undefined;						//連結標的中文
			$scope.inputVO.tradeDate = undefined;						//申購日
			$scope.inputVO.valueDate = undefined; 						//扣款(起息)日
			$scope.inputVO.spotDate = undefined;						//比價日	
			$scope.inputVO.expireDate = undefined;						//到期(入帳)日
			$scope.inputVO.intDates = undefined;						//產品天期
			$scope.inputVO.prdProfeeRate = undefined;					//預估年化收益率(到期比價匯率大於等於履約價)
			$scope.inputVO.lessProfeeRate = undefined; 					//預估年化收益率(到期比價匯率小於履約價)
			$scope.inputVO.prdProfeeAmt = undefined;					//到期比價匯率大於等於履約價
			$scope.inputVO.lessProfeeAmt = undefined; 					//到期比價匯率小於履約價
			$scope.inputVO.traderCharge = undefined; 					//交易員CHARGE
		 
			$scope.setAcctDisplay(["SOT.DEBIT_ACCT_LIST"]);
			
		}
        
        //設定客戶資料
        $scope.setCustInfo=function(body){
        	debugger
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
			$scope.inputVO.pro1500 = body.pro1500;							//專業自然人提供1500萬財力證明(L2,J2)
			$scope.inputVO.pro3000 = body.pro3000;							//專業自然人提供3000萬財力證明(L1,J1)
			$scope.inputVO.proCorpInv = body.proCorpInv;					//專業機構投資人
			$scope.inputVO.highYieldCorp = body.highYieldCorp;				//高淨值法人
			$scope.inputVO.siProCorp = body.siProCorp;						//衍商資格專業法人
			$scope.inputVO.trustProCorp = body.trustProCorp;
			$scope.inputVO.hnwcYN = body.hnwcYN;
			$scope.inputVO.hnwcServiceYN = body.hnwcServiceYN;
			//自然人不須輸入授權交易人員，法人才需要輸入
			$scope.inputVO.disableAuthId = $scope.inputVO.custID.length >= 10 ? true : false;
		};
		
		// SOT701-客戶電文
		$scope.getSOTCustInfo = function(input) {
			debugger;
			
			if($scope.inputVO.loginBranchID == "772") {
				//簡易分行772不能申購 
				$scope.showErrorMsg('簡易分行無法進行FCI申購');
				$scope.inputVO.custID='';
				$scope.clearCust();
				return;
			}
			
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
				$scope.sendRecv("SOT413", "getSOTCustInfoCT", "com.systex.jbranch.app.server.fps.sot413.SOT413InputVO", {'custID':$scope.inputVO.custID, 'prodType':'7', 'tradeType':1},
				function(tota, isError) {
					$scope.clearCust();
					if (!isError) {
						if(tota[0].body.KYCResult == 'N'){
							$scope.showErrorMsg('ehl_02_KYC310_021');
							$scope.inputVO.custID = "";
							return;
						}
						//debugger;
						//客戶為利害關係人，不可申購
						if (tota[0].body.custStakeholder == "Y") {
							$scope.inputVO.custID = "";
							$scope.showErrorMsg("不開放利害關係人進行FCI申購");
							return;
						}
						
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
		$scope.goPRD151 = function() {
			if($scope.inputVO.custID == "") {
				$scope.showErrorMsg("請先輸入客戶ID"); 
				return;
			}
			
			var kyclv = $scope.inputVO.kycLv;
			var isFCIPMRole = $scope.inputVO.isFCIPMRole;
			
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT413/SOT413_ROUTE.html',
				className: 'PRD151',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
	        		$scope.txnName = "報價查詢";
	        		$scope.fromSOT = "Y";
	        		$scope.kycLV = kyclv;
	        		$scope.isFCIPMRole = isFCIPMRole;
	        		$scope.routeURL = 'assets/txn/PRD151/PRD151.html';
	            }]
			}).closePromise.then(function (data) {
				if(data.value && data.value != 'cancel'){
					$scope.data = {};
					$scope.data = data.value;
					if($scope.data.allowPurchaseYN == "N") {
						//若非PM角色需檢核，自行輸入的產品收益率(年化)<最低收益率或理專收益<minUF(X%)，則跳出錯誤視窗"產品條件超出權限，請洽PM"，且無法繼續申購
						$scope.showErrorMsg("產品條件超出權限，請洽PM"); 
						$scope.clearProd();
						return;
					}
					debugger
					$scope.inputVO.monType = $scope.data.monType;
					$scope.inputVO.prodName = "固定配息匯率型組合式商品";
					$scope.inputVO.prodCurr = $scope.data.C_CURR_ID; //幣別
					$scope.inputVO.monPeriod = $scope.data.monType; //天期
					$scope.inputVO.prdProfeeRate = $scope.data.C_PRD_PROFEE; //預估年化收益率(到期比價匯率大於等於履約價)
					$scope.inputVO.lessProfeeRate = (Number($scope.data.C_PRD_PROFEE) + 0.01).toFixed(2); //預估年化收益率(到期比價匯率小於履約價)
					$scope.inputVO.rmProfee = $scope.data.RM_PROFEE; //理專收益率
					$scope.inputVO.prodRiskLv = $scope.data.C_RISKCATE_ID; //風險等級
					$scope.inputVO.targetCurrId = $scope.data.C_TARGET_CURR_ID; //連結標的
					$scope.inputVO.prodMinBuyAmt = $scope.data.C_BASE_AMT; //最低申購金額
					$scope.inputVO.prodMinGrdAmt = $scope.data.C_UNIT_AMT; //累進申購金額
					$scope.inputVO.strikePrice = $scope.data.C_STRIKE_PRICE; //履約價
					$scope.inputVO.targetName = $scope.data.C_TARGET_CNAME; //連結標的中文
					$scope.data = {};
					$scope.getProdDTL();
				}
			});
		};
		
		// 商品查詢
		$scope.getProdDTL = function () {
			var deferred = $q.defer();
			if($scope.inputVO.prodCurr) {				
				$scope.sendRecv("SOT413", "getProdDTL", "com.systex.jbranch.app.server.fps.sot413.SOT413InputVO", {prodCurr: $scope.inputVO.prodCurr, monType: $scope.inputVO.monType},
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.errorMsg != null && tota[0].body.errorMsg != "") {
									$scope.clearProd();
								} else if (tota[0].body.prodDTL && tota[0].body.prodDTL.length > 0) {
									$scope.inputVO.tradeDate = $scope.toJsDate(tota[0].body.prodDTL[0].TRADE_DATE);	//申購日
									$scope.inputVO.valueDate = $scope.toJsDate(tota[0].body.prodDTL[0].VALUE_DATE);		//扣款(起息)日
									$scope.inputVO.spotDate = $scope.toJsDate(tota[0].body.prodDTL[0].SPOT_DATE);			//比價日	
									$scope.inputVO.expireDate = $scope.toJsDate(tota[0].body.prodDTL[0].EXPIRE_DATE);		//到期(入帳)日
									$scope.inputVO.intDates =  tota[0].body.prodDTL[0].INT_DATES;			//產品天期
//									$scope.inputVO.prdProfeeRate =  tota[0].body.prodDTL[0].PRD_PROFEE_RATE;	//預估年化收益率
//									$scope.inputVO.lessProfeeRate =  tota[0].body.prodDTL[0].LESS_PROFEE_RATE; //到期比價匯率小於履約價
									$scope.inputVO.ftpRate =  tota[0].body.prodDTL[0].FTP_RATE; //FTP_RATE
									$scope.inputVO.traderCharge = tota[0].body.prodDTL[0].TRADER_CHARGE; //交易員CHARGE
									
									$scope.setAcctDisplay(["SOT.DEBIT_ACCT_LIST"]);
									$scope.inputVO.debitAcct = ($scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY'].length > 0 ? $scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY'][0].LABEL : "");//扣款帳號
									
									//如果有申購金額需要重算履約價(預估收益金額)
									$scope.checkUnitNum();
								}
								deferred.resolve("success");
								return deferred.promise;
							}
							
				});
			} else {
				$scope.clearProd();
//				$scope.inputVO.prodID = "";
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
			} else if ($scope.inputVO.purchaseAmt > $scope.inputVO.prodMinBuyAmt && 
				($scope.inputVO.purchaseAmt % $scope.inputVO.prodMinGrdAmt) == 0){
			} else {
					//警示並清空purchaseAmt(申購面額)
					$scope.showErrorMsg("ehl_01_sot310_007");
					$scope.inputVO.purchaseAmt=undefined;
					return;
			}

			//取得折台金額
			$scope.sendRecv("SOT413", "getPurchaseAmtTwd", "com.systex.jbranch.app.server.fps.sot413.SOT413InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						debugger
						$scope.inputVO.purchaseAmtTwd = tota[0].body.purchaseAmtTwd;
					}
			});
			
			debugger
			$scope.inputVO.prdProfeeAmt = $scope.inputVO.purchaseAmt * ($scope.inputVO.prdProfeeRate/100) * ($scope.inputVO.intDates/360);//到期比價匯率大於等於履約價
			$scope.inputVO.lessProfeeAmt = $scope.inputVO.purchaseAmt * ($scope.inputVO.lessProfeeRate/100) * ($scope.inputVO.intDates/360);//到期比價匯率小於履約價
			$scope.inputVO.purchaseAmt = $scope.moneyFormat($scope.inputVO.purchaseAmt);
			
			
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
			$scope.inputVO.tradeStatus = "1"; //暫存
			$scope.sendRecv("SOT413", "save", "com.systex.jbranch.app.server.fps.sot413.SOT413InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.showMsg("儲存成功");
						} else {
							$scope.showErrorMsg("ehl_01_common_008");
						}
						$scope.inputVO.purchaseAmt = $scope.moneyFormat($scope.inputVO.purchaseAmt);
			});
		};
		
		
		//查詢帳號幣別		
		$scope.getAcctCurrency = function() { 
			 var prodCurrency = ($scope.inputVO.prodCurr) == "CNH" ? "CNY" : $scope.inputVO.prodCurr;
			 var acctCcy = undefined;
			 if (prodCurrency){ //有傳信託業務別 和 商品幣別 
		 		   acctCcy = prodCurrency; 
			 } 
			 return acctCcy;
		};	
		
		//扣款帳號
		$scope.changeAcct = function (type) {
			debugger
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
			$scope.sendRecv("SOT413", "query", "com.systex.jbranch.app.server.fps.sot413.SOT413InputVO",{'tradeSEQ':$scope.inputVO.tradeSEQ},
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
								
								if($scope.carList && $scope.carList.length !=0 ) {
									debugger
									$scope.inputVO.monType = $scope.carList[0].MON_PERIOD;
									$scope.inputVO.prodName = $scope.carList[0].PROD_NAME;
									$scope.inputVO.prodCurr = $scope.carList[0].PROD_CURR; //幣別
									$scope.inputVO.monPeriod = $scope.carList[0].MON_PERIOD; //天期
									$scope.inputVO.rmProfee = $scope.carList[0].RM_PROFEE; //理專收益率
									$scope.inputVO.prodRiskLv = $scope.carList[0].PROD_RISK_LV; //風險等級
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
									$scope.inputVO.prdProfeeAmt = $scope.carList[0].PRD_PROFEE_AMT;	//到期比價匯率大於等於履約價
									$scope.inputVO.lessProfeeAmt = $scope.carList[0].LESS_PROFEE_AMT; //到期比價匯率小於履約價
									$scope.inputVO.ftpRate = $scope.carList[0].FTP_RATE; //FTP_RATE
									$scope.inputVO.traderCharge = $scope.carList[0].TRADER_CHARGE; //交易員CHARGE
									
									$scope.inputVO.narratorID=$scope.carList[0].NARRATOR_ID;	//解說專員員編
									$scope.inputVO.narratorName=$scope.carList[0].NARRATOR_NAME;//解說專員姓名
									$scope.inputVO.authID=$scope.carList[0].AUTH_ID;			//授權交易人員員編
//									$scope.getTellerName('authID',$scope.inputVO.authID);		//授權交易人員姓名
									$scope.inputVO.bossID=$scope.carList[0].BOSS_ID;			//主管員編
									$scope.getTellerName('bossID',$scope.inputVO.bossID);		//主管姓名
									
									$scope.setAcctDisplay(["SOT.DEBIT_ACCT_LIST"]);
									$scope.inputVO.purchaseAmt = $scope.carList[0].PURCHASE_AMT;	//申購金額
									$scope.inputVO.purchaseAmt = $scope.moneyFormat($scope.inputVO.purchaseAmt);
									$scope.inputVO.purchaseAmtTwd = $scope.carList[0].PURCHASE_AMT_TWD;//申購金額折台
									$scope.inputVO.debitAcct = $scope.carList[0].DEBIT_ACCT + "_" + ($scope.carList[0].PROD_CURR == "CNH" ? "CNY" : $scope.carList[0].PROD_CURR); //扣款帳號
									$scope.inputVO.prodAcct = $scope.carList[0].PROD_ACCT;
									$scope.changeAcct('debit');
								}
							});
						}
					}
			});
		};
		
		$scope.query = function() {
			if($scope.inputVO.tradeSEQ) {
				$scope.noCallCustQuery();
			} else {
				$scope.getTradeSEQ();//取得交易序號
			}
		};
		$scope.query();
		
		$scope.nextCheck = function() {
			//讓扣款金額的e-combobox預設值是上次選的
			 
			$scope.changeAcct();
			
			$scope.inputVO.purchaseAmt=$scope.moneyUnFormat($scope.inputVO.purchaseAmt);
					
			var txtMsg = "";
			if ($scope.showAvbBalance < $scope.inputVO.purchaseAmt) {
				txtMsg = $filter('i18n')('ehl_01_sot310_008');  // sot310_008餘額不足
			}
			$scope.inputVO.purchaseAmt=$scope.moneyFormat($scope.inputVO.purchaseAmt);
						
			return txtMsg;
		}
				
		//下一步
		$scope.goNext = function() {
			$scope.inputVO.purchaseAmt=$scope.moneyUnFormat($scope.inputVO.purchaseAmt);
			$scope.inputVO.tradeStatus = "2";//風控檢核中
			
			$scope.sendRecv("SOT413", "next", "com.systex.jbranch.app.server.fps.sot413.SOT413InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.errorMsg != null) {
								$scope.showErrorMsg(tota[0].body.errorMsg);
								
								return;
							} else {
								//產生報表
								var fitVO = {
									caseCode : 		1, 								//case1 下單
									custId   :		$scope.inputVO.custID,			//客戶ID
									prdType  :      "FCI",							//商品類別 : SI
									tradeSeq : 		$scope.inputVO.tradeSEQ, 		//交易序號
									tradeSubType:	1,								//交易類型 : 申購
									isPrintSot816:	$scope.inputVO.isPrintSot816,	//是否列印結構型商品交易自主聲明書
									isPrintSot817: 	$scope.inputVO.isPrintSot817	//是否列印結構型商品推介終止同意書
								}
								// 商品契約書 		- 套表
								// 自主聲明書 		- 套表
								// 錄音稿 			- PM上傳
								// SI交易檢核表		- PM上傳
								// SI錄音案例分享	- PM上傳	
								$scope.sendRecv("SOT712", "fitToGetPDF", "com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO", fitVO,
										function(tota, isError) {
									if (isError) {
										$scope.showErrorMsg(totas[0].body.msgData);
									} else {
										$scope.connector('set','SOTTradeSEQ', $scope.inputVO.tradeSEQ);
										$scope.connector('set','SOT414_warningMsg', "");
										$rootScope.menuItemInfo.url = "assets/txn/SOT414/SOT414.html";
										return;
									}
								});
							}
						}
			});
		}
		
		//傳送"檢核及列印文件"，避免按鈕被連續點擊造成錯誤，先disable按鈕並$timeout
		$scope.next = function () {
			$scope.inputVO.goNextDisabled = true;
			
			$timeout(function() {
				$scope.do_next(); 
				$scope.inputVO.goNextDisabled = false;}
			, 3000);			
		}
		
		//檢核餘額才進入下一步
		$scope.do_next = function() {
			$scope.inputVO.purchaseAmt=$scope.moneyUnFormat($scope.inputVO.purchaseAmt);
			var txtMsg = $scope.nextCheck();//檢核餘額不足  txtMsg:餘額不足
			if (txtMsg == '') {
		    	if($scope.parameterTypeEditForm.$invalid) { //檢何必輸欄位
		    		$scope.showErrorMsg("ehl_01_common_022");
		    		return;
		    	}
		    	//下一步
		    	//非常規交易檢核
		    	debugger
				$scope.inputVO.isPrintSot816 = "N";	//是否列印結構型商品交易自主聲明書
				$scope.inputVO.isPrintSot817 = "N";	//是否列印結構型商品推介終止同意書
				
				//*** 推介同意簽署機制
				if ($scope.inputVO.proCorpInv == "Y" || $scope.inputVO.highYieldCorp == "Y" || $scope.inputVO.siProCorp == "Y" || $scope.inputVO.pro3000 == "Y" ||
						($scope.inputVO.pro1500 == "Y" && $scope.inputVO.purchaseAmtTwd >= 3000000)) {
					//專業機構投資人//高淨值法人//專業法人//專業自然人提供3000萬財力證明//專業自然人提供1500萬財力證明且申購金額>=3百萬台幣，不檢核也不套印
					//下一步
					$scope.goNext();
				} else {							
					if (($scope.inputVO.profInvestorYN != "Y" && $scope.inputVO.custRemarks == "Y") ||
						($scope.inputVO.pro1500 == "Y" && $scope.inputVO.purchaseAmtTwd < 3000000 && $scope.inputVO.custRemarks == "Y")) {
						//一般客戶為特定客群，需簽署結構型商品交易自主聲明書
						$scope.inputVO.isPrintSot816 = "Y";
						//下一步
						$scope.goNext();
					} else  if (($scope.inputVO.pro1500 == "Y" && $scope.inputVO.purchaseAmtTwd < 3000000 && $scope.inputVO.custRemarks != "Y") ||
								($scope.inputVO.profInvestorYN != "Y" && $scope.inputVO.custRemarks != "Y") ||
								($scope.inputVO.siProCorp != "Y"))
					{ 
						//查詢結構型商品推介同意註記
						$scope.sendRecv("SOT701", "getSDACTQ8Data", "com.systex.jbranch.app.server.fps.sot701.SOT701InputVO", {'custID':$scope.inputVO.custID},
							function(totaCT, isError) {
								if (!isError) {
									$scope.inputVO.isSign = totaCT[0].body.siPromDataVO.isSign;
									$scope.inputVO.signStatus = totaCT[0].body.siPromDataVO.status;
									debugger
									if($scope.inputVO.isSign != "Y" || $scope.inputVO.signStatus != "Y") {
										$scope.inputVO.isPrintSot816 = "Y";	//是否列印結構型商品交易自主聲明書
										$scope.inputVO.isPrintSot817 = "Y";	//是否列印結構型商品推介終止同意書
									}
									//下一步
									$scope.goNext();
								}
						});
					} else {
						//下一步
						$scope.goNext();
					}
				}
		    } else {
		    	$scope.showErrorMsg(txtMsg);
	    		return;
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