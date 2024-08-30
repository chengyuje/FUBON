/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT210Controller',
	function($rootScope, $scope, $controller, $confirm,$confirmModalDefaults, socketService, ngDialog, projInfoService, $filter, getParameter, $q, validateService, sotService ) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT210Controller";
		//繼承PRD100高齡檢核
		$controller('PRD100Controller', {$scope: $scope});
		
		var roundDecimal = function (val) {
			  return Math.round(Math.round(val * Math.pow(10, (4 || 0) + 1)) / 10) / Math.pow(10, (4 || 0));
		}
		// filter
		getParameter.XML(["SOT.COUNTRY_CODE", "SOT.CUST_TYPE", "SOT.EBANK_PRTDOC_URL", "SOT.QUOTE", "SOT.EBANK_PRTDOC_URL", "SOT.CHG_PRTDOC_URL", "SOT.ENTRUST_TYPE", "SOT.BARGAIN_STATUS","SOT.SPEC_CUSTOMER","SOT.ETF_DECIMAL_POINT"], function(totas) {
			if (totas) {
				$scope.mappingSet['SOT.COUNTRY_CODE'] = totas.data[totas.key.indexOf('SOT.COUNTRY_CODE')];
				$scope.mappingSet['SOT.CUST_TYPE'] = totas.data[totas.key.indexOf('SOT.CUST_TYPE')];
				$scope.mappingSet['SOT.EBANK_PRTDOC_URL'] = totas.data[totas.key.indexOf('SOT.EBANK_PRTDOC_URL')];
				$scope.quote = totas.data[totas.key.indexOf('SOT.QUOTE')][0].LABEL;
//				$scope.eBankPredocURL = totas.data[totas.key.indexOf('SOT.EBANK_PRTDOC_URL')][0].LABEL;
//				$scope.chgPrtdocURL = totas.data[totas.key.indexOf('SOT.CHG_PRTDOC_URL')][0].LABEL;
				if (typeof (webViewParamObj) !== 'undefined') {
					$scope.urlDisable = true;
				} else {
					$scope.urlDisable = false;
					//網行銀服務申請書列印
					$scope.eBankPredocURL = totas.data[totas.key.indexOf('SOT.EBANK_PRTDOC_URL')][0].LABEL;
					//變更申請書列印
					$scope.chgPrtdocURL = totas.data[totas.key.indexOf('SOT.CHG_PRTDOC_URL')][0].LABEL;					
				}
				$scope.mappingSet['SOT.ENTRUST_TYPE'] = totas.data[totas.key.indexOf('SOT.ENTRUST_TYPE')];
				$scope.mappingSet['SOT.BARGAIN_STATUS'] = totas.data[totas.key.indexOf('SOT.BARGAIN_STATUS')];
				$scope.mappingSet['SOT.SPEC_CUSTOMER'] = totas.data[totas.key.indexOf('SOT.SPEC_CUSTOMER')];//客戶註記
				$scope.mappingSet['SOT.ETF_DECIMAL_POINT'] = totas.data[totas.key.indexOf('SOT.ETF_DECIMAL_POINT')];//幣別取小數
			}
		});
        $scope.cartList = [];
        
        $scope.getCurrency = function(prodCurr){
			$scope.mod = $filter('filter')($scope.mappingSet['SOT.ETF_DECIMAL_POINT'], {DATA: prodCurr});
			if($scope.mod.length == 1){
				var num = $scope.mod[0].LABEL ;
				var pow = Math.pow(10 , num);
				$scope.inputVO.entrustAmt = !isNaN($scope.inputVO.entrustAmt)  ? Number($scope.inputVO.entrustAmt.toFixed(num))  : $scope.inputVO.entrustAmt;  // 約定限價
				$scope.inputVO.entrustAmt1= !isNaN($scope.inputVO.entrustAmt1) && $scope.inputVO.entrustAmt1 != "" ? Number(parseFloat($scope.inputVO.entrustAmt1).toFixed(num)) : $scope.inputVO.entrustAmt1; // 限價
				$scope.inputVO.entrustAmt2= !isNaN($scope.inputVO.entrustAmt2) ? Number($scope.inputVO.entrustAmt2).toFixed(num) : $scope.inputVO.entrustAmt2; // 限價%數
				$scope.inputVO.fee = !isNaN($scope.inputVO.fee) ? Math.round( $scope.inputVO.fee * pow ) / pow  : $scope.inputVO.fee; // 手續費
			}
		};
        
        $scope.clearTradeSEQ = function(){
        	$scope.inputVO.tradeSEQ='';
        	$scope.query();
        }
        
        $scope.init = function() {
        	$scope.mappingSet['FEE_RATE_TYPE']=[];
        	$scope.debitAcctLabel='';
        	$scope.showentrustType=[true,true,true,true];
        	$scope.cartList = [];
        	$scope.dueDate = [];
        	
        	//手續費優惠方式預設
        	$scope.feeRateType = '';
			$scope.inputVO = {
					prodType:'2',                               //2：ETF
		        	tradeType:'1',                              //1：申購
		        	seniorAuthType:'S',							//高齡評估表授權種類(S:下單、A：適配)
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
					bargainDueDate: undefined,					//期間議價效期
					plNotifyWays: '',							//停損停利通知方式
					takeProfitPerc: undefined,					//停利點
					stopLossPerc: undefined,					//停損點
					debitAcct: '', 								//扣款帳號
					trustAcct: '', 								//信託帳號
					creditAcct: '',								//收益入帳帳號
					w8benStartDate: undefined,					//W8ben簽署起日
					w8benEndDate: undefined,					//W8ben簽署迄日
					w8BenEffYN: '',
					fatcaType: '',
					custProType: '',							//專投種類
					investType: '',								//特定客戶
					investDue: '',								//特定客戶申請日距今是否滿兩週
					piRemark:'',								//專投註記
					custType: 'CUST', 							//來行人員
					agentID: '', 								//代理人ID
					agentName: '',
					narratorID: projInfoService.getUserID(),	//解說人員ID
					narratorName: projInfoService.getUserName(),//解說人員姓名
					prodID: '', 								//商品ID
					prodName: '',								//商品名稱
					prodCurr: '', 								//計價幣別
					prodRiskLV: '', 							//產品風險等級
					marketPrice: undefined, 					//市價
					unitNum: undefined,							//股數
					prodMinBuyAmt: undefined, 					//最低申購金額
					prodMinBuyUnit: undefined,					//最低買進單位
					prodMinGrdUnit: undefined,					//最低累進單位
					dueDateShow: '',
					dueDate: undefined, 	
					entrustType: '2',							//買進價格指示
					entrustAmt: undefined,						//約定限價
					entrustAmt1: undefined, 					//限價-元
					closingPriceDate: undefined,				//限價-日期
					closingPrice: undefined,					//限價-收盤價
					entrustDiscount: 5, 						//限價-yy%
					entrustAmt2: undefined,						//限價-XXXX.XX元以下買進
					defaultFeeRate: undefined, 					//表定手續費率
					feeRateType: '', 							//手續費優惠方式
					feeRate: undefined, 						//手續費率
					fee: undefined, 							//手續費金額
					feeDiscount: undefined, 					//折數
					countryCode: '',							//交易國家別
					stockCode: '',								//交易所代號	
					trustTS: 'S',								//交易類別:特金S或金錢信託M
					trustPeopNum: 'N',							//是否為多委託人契約
					acctCurrency: '',							//金錢信託-扣款帳號幣別
					flagNumber: '',								//90天內是否有貸款紀錄 Y/N
					otherWithCustId: false,						//是否帶客戶ID進來(快查)
					hnwcYN: '',									//是否為高資產客戶 Y/N 
					hnwcServiceYN: ''							//可提供高資產商品或服務 Y/N 
			};
		};
		
		$scope.custClear = function() {
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST'] = [];
			$scope.mappingSet['SOT.TRUST_ACCT_LIST'] = [];
			$scope.mappingSet['SOT.CREDIT_ACCT_LIST'] = [];
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY'] = [];
			$scope.mappingSet['SOT.CREDIT_ACCT_LIST#DSIPLAY'] = [];
			
			$scope.inputVO.custName = '';
			
			$scope.inputVO.kycLV = '';									//KYC等級
			$scope.inputVO.kycDueDate = undefined;						//KYC效期
			$scope.inputVO.profInvestorYN = '';							//是否為專業投資人
			$scope.inputVO.piDueDate = undefined;						//專業投資人效期
			$scope.inputVO.custRemarks = '';							//客戶註記
			$scope.inputVO.isOBU = '';									//是否為OBU客戶
			$scope.inputVO.isAgreeProdAdv = '';							//同意投資商品諮詢服務
			$scope.inputVO.bargainDueDate =  undefined;					//期間議價效期
			$scope.inputVO.plNotifyWays = '';							//停損停利通知方式
			$scope.inputVO.takeProfitPerc = undefined;					//停利點
			$scope.inputVO.stopLossPerc = undefined;					//停損點
			$scope.inputVO.debitAcct = '';								//扣款帳號
			$scope.inputVO.trustAcct = '';								//信託帳號
			$scope.inputVO.creditAcct = '';								//收益入帳帳號
			$scope.inputVO.w8benStartDate = undefined;					//W8ben簽署起日
			$scope.inputVO.w8benEndDate = undefined;					//W8ben簽署迄日
			$scope.inputVO.w8BenEffYN = '';
			$scope.inputVO.fatcaType = '';
			$scope.inputVO.custProType = '';
			$scope.inputVO.investType = '';								//特定客戶
			$scope.inputVO.investDue = '';								//特定客戶申請日距今是否滿兩週
			$scope.inputVO.piRemark = '';								//專投註記
			
			$scope.inputVO.custType = 'CUST';							//來行人員
			$scope.inputVO.agentID = '';								//代理人ID
			$scope.inputVO.agentName = '';
			$scope.inputVO.hnwcYN = '';
			$scope.inputVO.hnwcServiceYN = '';
			
			//金錢信託
			$scope.mappingSet['SOT.CONTRACT_LIST'] = [];			// 契約編號列表
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'] = [];	// 扣款帳號
//			$scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'] = [];	// 收益入帳帳號
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
			$scope.custClear();
		};
		
		//PRD100.validSeniorCustEval高齡檢核通過後查詢
		$scope.reallyInquire = function() {
			if($scope.inputVO.trustTS == 'M') {
				//金錢信託
				$scope.getCustANDContractList(true);
			} else {
				if($scope.inputVO.otherWithCustId) { //有帶客戶ID(快查)
					$scope.queryChkSenior();
				} else {
					$scope.getSOTCustInfo(true);
				}
			}
			$scope.connector('set','SOTCustID',null);
		};
		
		// SOT701-客戶電文
		$scope.getSOTCustInfo = function(input) {
			var deferred = $q.defer();
			$scope.inputVO.custID = $filter('uppercase')($scope.inputVO.custID);
			var validCustID = validateService.checkCustID($scope.inputVO.custID); //自然人和法人檢查
			if(validCustID==false){ 
				$scope.inputVO.custID='';
			}
			if(input){
				$scope.custClear();
				$scope.prodClear();
				$scope.inputVO.prodID = "";
			}
			if($scope.inputVO.custID) {
				$scope.sendRecv("SOT210", "getSOTCustInfo", "com.systex.jbranch.app.server.fps.sot210.SOT210InputVO", {'custID':$scope.inputVO.custID, 'prodType':2, 'tradeType':1, 'trustTS':$scope.inputVO.trustTS},
						function(tota, isError) {
							if (!isError) {
								//客戶仍未簽署海外股票與海外ETF商品說明暨風險預告書！
								if (tota[0].body.signRiskYn != "Y") {
									$scope.showMsg("ehl_01_sot210_005");
								}
								
								//客戶為利害關係人，請參照本行組合式商品利害關係人規範申購
								if (tota[0].body.custStakeholder == "Y") {
									$scope.showErrorMsg("ehl_01_sot210_007");
								}
								
								$scope.toDay = $filter('date')(new Date(),'yyyy-MM-dd 00:00:00');//取當日日期
								//FOR CBS TEST日期
//								if ($scope.toJsDate(tota[0].body.kycDueDate) < $scope.toJsDate($scope.toDay)) { //若KYC過期，需有訊息提示客戶，並清空客戶ID。
								if(tota[0].body.isKycDueDateUseful){
									var kycDueDate = $scope.toJsDate(tota[0].body.kycDueDate);
									var msgParam = "";
									if(kycDueDate==null) {
										msgParam='未承作';
									}else{
										msgParam = kycDueDate.getFullYear() + "/" +(kycDueDate.getMonth() + 1) + "/" + kycDueDate.getDate();
									}
									var txtMsg = $filter('i18n')('ehl_01_sot310_001') + "(" + msgParam + ")";
									$scope.showErrorMsg(txtMsg);
									$scope.custClear();
									$scope.inputVO.custID = "";
								} else if (tota[0].body.noSale == "Y") { //若為禁銷客戶，出現提示訊息禁止下單。
									$scope.showErrorMsg("ehl_01_sot310_003");
									$scope.custClear();
									$scope.inputVO.custID = "";
								} else if($scope.inputVO.trustTS == 'M' && (tota[0].body.deathFlag == 'Y' || tota[0].body.isInterdict == 'Y')) {
									//金錢信託若為死亡戶/禁治產等狀況，不可下單。
									$scope.showErrorMsg('ehl_01_sot310_004'); //(ehl_01_SOT510_004：死亡戶/禁治產，不可下單)
									$scope.custClear();
									$scope.inputVO.custID = "";
								} else if (tota[0].body.rejectProdFlag == "Y") {	//拒銷(RS)註記Y 得拒絕申請人臨櫃進行非存款類之理財商品下單(轉換、變更投資標的不在此限)
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
											$scope.custInfo(tota[0].body, input);
											deferred.resolve("success");  //若為拒銷客戶，出現警告訊息問是否繼續，選"確定"，但客戶ID不能被清掉。
											return deferred.promise;
										} else {
											$scope.custClear();
											$scope.inputVO.custID = "";
											deferred.resolve("");
										}
									});
									
								} else {
									if ($scope.fromFPS) {
										input = true;
									}
									$scope.custInfo(tota[0].body, input);									
									deferred.resolve("success");									
									return deferred.promise;
								}
							} else {
								$scope.inputVO.custID = "";
								$scope.custClear();
							}
				});
			}
			return deferred.promise;
		};
		
		$scope.custInfo = function (row, input) {
			$scope.inputVO.custName = row.custName;
			$scope.inputVO.kycLV = row.kycLV;										//KYC等級
			$scope.inputVO.kycDueDate = $scope.toJsDate(row.kycDueDate);			//KYC效期
			$scope.inputVO.profInvestorYN = row.profInvestorYN;						//是否為專業投資人
			$scope.inputVO.piDueDate = $scope.toJsDate(row.piDueDate);				//專業投資人效期
			$scope.inputVO.custRemarks = row.custRemarks;							//客戶註記
			$scope.inputVO.piRemark = row.piRemark;
			$scope.inputVO.isOBU = row.isOBU;										//是否為OBU客戶
			$scope.inputVO.isAgreeProdAdv = row.isAgreeProdAdv;						//同意投資商品諮詢服務
			if(row.bargainDueDate){
				$scope.inputVO.bargainDueDate =  $scope.toJsDate(row.bargainDueDate);					//期間議價效期
			}
			
			$scope.inputVO.plNotifyWays = row.plNotifyWays;							//停損停利通知方式
			
			$scope.inputVO.w8benStartDate = $scope.toJsDate(row.w8benStartDate);	//W8ben簽署起日
			$scope.inputVO.w8benEndDate = $scope.toJsDate(row.w8benEndDate);		//W8ben簽署迄日
			$scope.inputVO.w8BenEffYN = row.w8BenEffYN;
			$scope.inputVO.fatcaType = row.fatcaType;
			$scope.inputVO.custProType = row.custProType;							//專投種類
			$scope.inputVO.investType = row.investType;								//特定客戶
			$scope.inputVO.investDue = row.investDue;								//特定客戶申請日距今是否滿兩週
			$scope.inputVO.flagNumber = row.flagNumber;								//90天內是否有貸款紀錄 Y/N
			$scope.inputVO.hnwcYN = row.hnwcYN;
			$scope.inputVO.hnwcServiceYN = row.hnwcServiceYN;
			
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST'] = row.debitAcct;
			$scope.mappingSet['SOT.TRUST_ACCT_LIST'] = row.trustAcct;
			$scope.mappingSet['SOT.CREDIT_ACCT_LIST'] = row.creditAcct;
			if ($scope.inputVO.trustTS != 'M') {
				$scope.setAcctDisplay(["SOT.DEBIT_ACCT_LIST","SOT.CREDIT_ACCT_LIST"],$scope.inputVO.prodCurr);
			}
			if(input){
				$scope.inputVO.takeProfitPerc = row.takeProfitPerc;					//停利點
				$scope.inputVO.stopLossPerc = row.stopLossPerc;						//停損點
				$scope.inputVO.debitAcct = (row.debitAcct.length > 0 ? row.debitAcct[0].LABEL : "");				//扣款帳號
				$scope.inputVO.trustAcct = (row.trustAcct.length > 0 ? row.trustAcct[0].LABEL : "");				//信託帳號
				if ($scope.inputVO.trustTS != 'M') {
					$scope.inputVO.creditAcct = ($scope.mappingSet['SOT.CREDIT_ACCT_LIST#DSIPLAY'].length > 0 ? $scope.mappingSet['SOT.CREDIT_ACCT_LIST#DSIPLAY'][0].LABEL : "");			//收益入帳帳號
				}
			}
			$scope.checkTrustAcct();  //1.檢查信託帳號 設定扣款帳號  2.顯示debitAcct餘額 
		};
		
        $scope.prodClear = function() {
        	$scope.inputVO.prodName = '';								//商品名稱
        	$scope.inputVO.prodCurr = ''; 								//計價幣別
        	$scope.inputVO.prodRiskLV = ''; 							//產品風險等級
        	$scope.inputVO.marketPrice = undefined; 					//市價
        	$scope.inputVO.unitNum = undefined;							//股數
        	$scope.inputVO.prodMinBuyAmt = undefined; 					//最低申購金額
        	$scope.inputVO.prodMinBuyUnit = undefined;					//最低買進單位
        	$scope.inputVO.prodMinGrdUnit = undefined;					//最低累進單位
        	$scope.inputVO.dueDateShow = '';
			$scope.inputVO.dueDate = undefined; 	
			$scope.inputVO.entrustType = '';							//買進價格指示
			$scope.inputVO.entrustAmt = undefined;						//約定限價
			$scope.inputVO.entrustAmt1 = undefined; 					//限價-元
			$scope.inputVO.closingPriceDate = undefined;				//限價-日期
			$scope.inputVO.closingPrice = undefined;					//限價-收盤價
			$scope.inputVO.entrustDiscount = 5; 						//限價-yy%
			$scope.inputVO.entrustAmt2 = undefined;						//限價-XXXX.XX元以下買進
			$scope.inputVO.defaultFeeRate = undefined; 					//表定手續費率
			$scope.inputVO.feeRateType = ''; 							//手續費優惠方式
			$scope.feeRateType = '';									//手續費優惠方式下拉選單
			$scope.inputVO.feeRate = undefined; 						//手續費率
			$scope.inputVO.fee = undefined; 							//手續費金額
			$scope.inputVO.feeDiscount = undefined; 					//折數
			$scope.inputVO.brgReason = '下單議價';						//議價原因
			$scope.inputVO.countryCode = '';							//交易國家別
			$scope.inputVO.stockCode = '';								//交易所代號
			if ($scope.inputVO.trustTS != 'M') {
				$scope.setAcctDisplay(["SOT.DEBIT_ACCT_LIST","SOT.CREDIT_ACCT_LIST"],$scope.inputVO.prodCurr);
			}
        }
		
		// getFee
		$scope.getFee = function(type) {
			var feeRate;
			if (type == 'rate') {
				feeRate = $scope.inputVO.feeRate;
				if($scope.inputVO.feeRate == $scope.inputVO.defaultFeeRate){
					$scope.inputVO.feeDiscount = null;
				}else{
					if(Number($filter('number')($scope.inputVO.feeRate / $scope.inputVO.defaultFeeRate * 10,3)) > 0){
						$scope.inputVO.feeDiscount = Number($filter('number')($scope.inputVO.feeRate / $scope.inputVO.defaultFeeRate * 10,3));
					}
					
				}
			} else if (type == 'feeDiscount') {
				$scope.inputVO.feeRate = roundDecimal(($scope.inputVO.feeDiscount * $scope.inputVO.defaultFeeRate) / 10);
				feeRate = roundDecimal(($scope.inputVO.feeDiscount * $scope.inputVO.defaultFeeRate) / 10);
			}
			if (feeRate <= 0) {
				$scope.inputVO.feeRate = null;
				$scope.inputVO.fee = null;
				$scope.inputVO.feeDiscount = null;				
				return;
			}
			if (feeRate > $scope.inputVO.defaultFeeRate) {
				$scope.showErrorMsg("ehl_02_SOT_010");
				$scope.inputVO.feeRate = null;
				$scope.inputVO.fee = null;
				$scope.inputVO.feeDiscount = null;				
				return;
			}
			switch ($scope.inputVO.entrustType){
			case "1":
				$scope.inputVO.fee = roundDecimal($scope.inputVO.unitNum * $scope.inputVO.closingPrice * feeRate / 100);
				break;
			case "2":					
				$scope.inputVO.fee = roundDecimal($scope.inputVO.unitNum * $scope.inputVO.entrustAmt * feeRate / 100);
				break;
			case "3":
				$scope.inputVO.fee = roundDecimal($scope.inputVO.unitNum * $scope.inputVO.entrustAmt1 * feeRate / 100);
				break;
			case "4":
				$scope.inputVO.fee = roundDecimal($scope.inputVO.unitNum * $scope.inputVO.entrustAmt2 * feeRate / 100); 
				break;
			}
			$scope.getCurrency($scope.inputVO.prodCurr); // 幣別取小數轉換
			//$scope.checkBestFee();//檢查手續費是否最優
		};
		
		$scope.getFeeTypeData = function () {
			$scope.sendRecv("SOT710", "getFeeTypeData", "com.systex.jbranch.app.server.fps.sot710.SOT710InputVO", {'custID': $scope.inputVO.custID},
					function(tota1, isError) {
						if (!isError) {
							$scope.feeTypeData = tota1[0].body.feeTypeList;
//							console.log(JSON.stringify($scope.feeTypeData));
							$scope.mappingSet['FEE_RATE_TYPE'] = [];
							//$scope.mappingSet['FEE_RATE_TYPE'].push({LABEL: "請選擇", DATA: "C"});
							$scope.mappingSet['FEE_RATE_TYPE'].push({LABEL: "申請議價", DATA: "A"});
							angular.forEach($scope.feeTypeData, function(row){
								if ($scope.inputVO.prodID == (row.prodId).trim()) {
									$scope.mappingSet['FEE_RATE_TYPE'].push({LABEL: ("事先議價" + row.feeDiscount+ "折"), 
																			 DATA: row.applySeq,
																			 BARGAIN_APPLY_SEQ: row.applySeq,
																			 FEE_RATE: row.feeRate});
								}
			    			});
							//從CRM421進入
							if($scope.apply_seq){
								$scope.getFeeType($scope.apply_seq);
							}
						}
			});
			
		};
		
//		$scope.$watch("inputVO.creditAcct",function(n,o){
//			alert(n+'watch');
//		});
		
		//將帳號轉成display暫存陣列
		$scope.setAcctDisplay = function(acctNameList,prodCurr){
//			for(var i=0;i<acctNameList.length;i++){
//				if(prodCurr && i==0){
//					$scope.mappingSet[acctNameList[0]+'#DSIPLAY'] = $filter('filter')($scope.mappingSet[acctNameList[0]], prodCurr);
//				}else if(prodCurr && i==1){
//					var debitAcct = $filter('filter')($scope.mappingSet[acctNameList[0]], prodCurr);
//					if (debitAcct != undefined && debitAcct.length > 0) {
//						var label =debitAcct[0].LABEL.split("_");
//						var creditAcct = $filter('filter')($scope.mappingSet[acctNameList[1]], label[0]);
//						$scope.mappingSet[acctNameList[1]+'#DSIPLAY'] = creditAcct;	
//					} else {
//						$scope.mappingSet[acctNameList[1]+'#DSIPLAY'] = [];
//					}
//				}else{					
//					$scope.mappingSet[acctNameList[i]+'#DSIPLAY'] = $scope.mappingSet[acctNameList[i]];
//				}
//			}
			for (var i=0;i<acctNameList.length;i++) {
				if (acctNameList[i]=='SOT.CREDIT_ACCT_LIST') {
					$scope.mappingSet[acctNameList[i]+'#DSIPLAY'] = $scope.mappingSet[acctNameList[i]];
				} else {
					if (prodCurr) {
						$scope.mappingSet[acctNameList[i]+'#DSIPLAY'] = $filter('filter')($scope.mappingSet[acctNameList[i]], {CURRENCY: prodCurr});
					} else {
						$scope.mappingSet[acctNameList[i]+'#DSIPLAY'] = $scope.mappingSet[acctNameList[i]];
					}
				}
			}
		}
		
		$scope.getTradeDueDate = function () {
			$scope.dueDate = [];
			var deferred = $q.defer();
			
			$scope.sendRecv("SOT210", "getTradeDueDate", "com.systex.jbranch.app.server.fps.sot210.SOT210InputVO", {custID: $scope.inputVO.custID, stockCode: $scope.inputVO.stockCode},
					function(totaDate, isError) {
						if (!isError) {
							$scope.dueDate = totaDate[0].body.dueDate;
							$scope.showDueDate=[];
							$scope.outputVO = totaDate[0].body;
							
							$scope.inputVO.dueDate = $scope.dueDate[0].DATA;
							angular.forEach($scope.dueDate, function(row, index){
								$scope.inputVO.dueDateShow = $scope.inputVO.dueDateShow + $filter('date')($scope.toJsDate(row.DATA), "yyyy-MM-dd") + ",";
								$scope.showDueDate[index] = false;
							});
							$scope.checkDueDate();
							deferred.resolve("success");
						}
			});
			
			return deferred.promise;
		}
		
		$scope.checkShowDueDate = function () {
			$scope.showDueDate=[];			
			angular.forEach($scope.dueDate, function(row, index){
				if ($scope.inputVO.entrustType == '1' || $scope.inputVO.entrustType == '2') {
					if (index == 0) {
						$scope.showDueDate[index] = false;
					} else {
						$scope.showDueDate[index] = true;
					}
				} else {
					$scope.showDueDate[index] = false;
				}				
			});
			if ($scope.inputVO.entrustType == '1' || $scope.inputVO.entrustType == '2') {
				$scope.inputVO.dueDate = $scope.dueDate[0].DATA;
			}
		}
		
		$scope.checkAccCurrency = function (currency) {
			var acctList = currency.split(",");

			if ($scope.inputVO.prodCurr != undefined && $scope.inputVO.prodCurr != '' && $scope.inputVO.prodCurr != null) {
//				console.log('prodCurr:' + $scope.inputVO.prodCurr);
//				console.log('acctList:' + acctList);
//				console.log('split:' + acctList.indexOf($scope.inputVO.prodCurr));

				if (acctList.indexOf($scope.inputVO.prodCurr) == -1) {
					$scope.showErrorMsg("該契約編號之帳號尚未建立該幣別。");
					return false;
				}
			}

			return true;
		}
		
		// 商品查詢
		$scope.getProdDTL = function () {
			var deferred = $q.defer();
			if (!$scope.fromFPS) {
				$scope.prodClear();
			}
			$scope.quoteProd="";
			if($scope.inputVO.prodID) {
				$scope.inputVO.prodID = $filter('uppercase')($scope.inputVO.prodID);
				$scope.sendRecv("SOT210", "getProdDTL", "com.systex.jbranch.app.server.fps.sot210.SOT210InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.errorMsg != null && tota[0].body.errorMsg != "") {
									//適配有錯誤訊息
									$scope.showErrorMsg(tota[0].body.errorMsg);
									$scope.inputVO.prodID = "";
									$scope.prodClear();
								} else if (tota[0].body.prodDTL != null && tota[0].body.prodDTL.length > 0) {
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
												
											} else {
												$scope.inputVO.prodID = "";
												$scope.prodClear();
												deferred.resolve("");
											}
										});
									}
																	
									//只要是購買交易所為美國的商品，任何客戶都要簽署 W8Ben
									$scope.inputVO.countryCode = tota[0].body.prodDTL[0].COUNTRY_CODE;                      //交易國家別
									debugger;
									if ($scope.inputVO.countryCode == "USA" && $scope.inputVO.w8BenEffYN != "Y") { 
										//客戶為美國人且未簽署W-8BEN顯示訊息
										$scope.showErrorMsg("ehl_01_sot210_006");
									} else if (tota[0].body.errorMsg != null && tota[0].body.errorMsg.length > 0) {
										$scope.showErrorMsg(tota[0].body.errorMsg);
									}
									$scope.quoteProd="a="+$scope.inputVO.prodID+"&Market="+$scope.inputVO.countryCode;
									$scope.inputVO.pType = tota[0].body.pType;												//交易市場別
									$scope.inputVO.stockCode = tota[0].body.prodDTL[0].STOCK_CODE;							//交易所代號
									$scope.inputVO.prodName = tota[0].body.prodDTL[0].PROD_NAME;							//商品名稱
									$scope.inputVO.prodCurr = tota[0].body.prodDTL[0].CURRENCY_STD_ID;						//計價幣別
									$scope.inputVO.prodRiskLV = tota[0].body.prodDTL[0].RISKCATE_ID;						//產品風險等級
									$scope.inputVO.prodMinBuyAmt = parseFloat(tota[0].body.prodDTL[0].TXN_AMOUNT);			//最低申購金額
									$scope.inputVO.prodMinBuyUnit = parseInt(tota[0].body.prodDTL[0].TXN_UNIT);				//最低買進單位
									$scope.inputVO.prodMinGrdUnit = parseInt(tota[0].body.prodDTL[0].TRADING_UNIT);			//最低累進單位
									$scope.inputVO.marketPrice = tota[0].body.prodDTL[0].MARKET_PRICE;
									if ($scope.inputVO.trustTS != 'M') {
										$scope.setAcctDisplay(["SOT.DEBIT_ACCT_LIST","SOT.CREDIT_ACCT_LIST"],$scope.inputVO.prodCurr);
										$scope.inputVO.debitAcct = ($scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY'].length > 0 ? $scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY'][0].LABEL : "");				//扣款帳號
										$scope.inputVO.creditAcct = ($scope.mappingSet['SOT.CREDIT_ACCT_LIST#DSIPLAY'].length > 0 ? $scope.mappingSet['SOT.CREDIT_ACCT_LIST#DSIPLAY'][0].LABEL : "");			//收益入帳帳號
									} else {
										if (!$scope.checkAccCurrency($scope.acctCurrencyByM)) {
											$scope.inputVO.contractID = '';
										}
									}
									$scope.inputVO.closingPrice = tota[0].body.prodDTL[0].CUR_AMT;							//收盤價
									$scope.inputVO.entrustAmt = $scope.inputVO.closingPrice * 1.1;		                    //約定限價
									$scope.inputVO.closingPriceDate = $scope.toJsDate(tota[0].body.prodDTL[0].SOU_DATE);	//收盤價日期
									$scope.inputVO.entrustAmt2 = $filter('number')($scope.inputVO.closingPrice - ($scope.inputVO.closingPrice * $scope.inputVO.entrustDiscount / 100),2);
									$scope.showentrustType=[true,true,true,true];
									for(var i=0;i<tota[0].body.entrustTypeList.length;i++){
										//tota[0].body.entrustTypeList-1=index   $scope.showentrustType[index]=false
										$scope.showentrustType[tota[0].body.entrustTypeList[i].PARAM_CODE-1]=false;
									}
									$scope.getTradeDueDate().then(function(data) {
										$scope.getBestFeeRate();
									});
									
									$scope.getFeeTypeData();
									$scope.checkTrustAcct();  //1.檢查信託帳號 設定扣款帳號  2.顯示debitAcct餘額 
									deferred.resolve("success");
//									return;
								}else{
									$scope.showErrorMsg("ehl_01_common_009");
									$scope.inputVO.prodID = "";
									$scope.prodClear();
								}
							}else{
								$scope.inputVO.prodID = "";
								$scope.prodClear();
							}		
				});
			}
//			alert("checkTrustAcct-----4");
			$scope.checkTrustAcct();  //1.檢查信託帳號 設定扣款帳號  2.顯示debitAcct餘額
			return deferred.promise;
		};
		
		//解說專員
		$scope.getSOTEmpInfo = function() {
			$scope.sendRecv("SOT712", "getTellerName", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {'tellerID': $scope.inputVO.narratorID},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.EMP_NAME == null || tota[0].body.EMP_NAME == "") {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							
							$scope.inputVO.narratorName = tota[0].body.EMP_NAME;
							return;
						}
			});
		};
		
		//檢核股數
		$scope.checkUnitNum = function() {
//			debugger
//			$scope.inputVO.entrustAmt = $scope.moneyUnFormat($scope.inputVO.entrustAmt);
//			$scope.inputVO.entrustAmt1 = $scope.moneyUnFormat($scope.inputVO.entrustAmt1);
			$scope.inputVO.priceTemp = 0;
			if(!$scope.inputVO.unitNum || $scope.inputVO.unitNum==0){
				$scope.getBestFeeRate();
			}
			if (($scope.inputVO.unitNum >= $scope.inputVO.prodMinBuyUnit) && 
				(($scope.inputVO.unitNum % $scope.inputVO.prodMinGrdUnit) == 0)) {
				switch ($scope.inputVO.entrustType){
					case "1":
						$scope.inputVO.entrustAmt=undefined;
						break;
					case "2":
						$scope.inputVO.priceTemp = Math.round($scope.inputVO.entrustAmt * $scope.inputVO.unitNum * 100) / 100;		//四捨五入至小數第二位（#5840）
						break;
					case "3":
						$scope.inputVO.priceTemp = Math.round($scope.inputVO.entrustAmt1 * $scope.inputVO.unitNum * 100) / 100;		//四捨五入至小數第二位（#5840）
						break;
					case "4":
						if($scope.inputVO.entrustDiscount < 0)
							$scope.inputVO.entrustDiscount = 0;
						else if($scope.inputVO.entrustDiscount > 5)
							$scope.inputVO.entrustDiscount = 5;
						$scope.inputVO.entrustAmt2 = $filter('number')($scope.inputVO.closingPrice - ($scope.inputVO.closingPrice * $scope.inputVO.entrustDiscount / 100),2);
						$scope.inputVO.priceTemp = Math.round($scope.inputVO.entrustAmt2 * $scope.inputVO.unitNum * 100) / 100;		//四捨五入至小數第二位（#5840）
						break;
				}

				if ($scope.inputVO.priceTemp > 0 && $scope.inputVO.priceTemp < $scope.inputVO.prodMinBuyAmt) {
					$scope.inputVO.entrustAmt = $scope.inputVO.closingPrice * 1.1;
					$scope.inputVO.unitNum = undefined;
					$scope.inputVO.entrustDiscount = 5;
					$scope.inputVO.entrustAmt2 =  $filter('number')($scope.inputVO.closingPrice - ($scope.inputVO.closingPrice * $scope.inputVO.entrustDiscount / 100),2);
					
					$scope.showErrorMsg("ehl_01_sot210_001");
					return;
				}
				
				$scope.getBestFeeRate();
			} else {
				$scope.inputVO.entrustAmt = $scope.inputVO.closingPrice * 1.1;
//				$scope.inputVO.entrustDiscount = 5;
				$scope.inputVO.entrustAmt2 = $filter('number')($scope.inputVO.closingPrice - ($scope.inputVO.closingPrice * $scope.inputVO.entrustDiscount / 100),2);

				if($scope.inputVO.unitNum != undefined && $scope.inputVO.unitNum != "") {
					$scope.showErrorMsg("ehl_01_sot210_001");
					if($scope.inputVO.unitNum <= 0)
						$scope.inputVO.unitNum = "";
				}
				return;
			}
			$scope.getFee('rate');
		};
		
		
		$scope.noCallCustQuery = function () {
			var deferred = $q.defer();
			$scope.sendRecv("SOT210", "query", "com.systex.jbranch.app.server.fps.sot210.SOT210InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.cartList = tota[0].body.carList;
							$scope.outputVO = tota[0].body;
							
							if (tota[0].body.carList && tota[0].body.carList.length > 0){
								$scope.inputVO.narratorID = tota[0].body.carList[0].NARRATOR_ID;		//解說人員ID
								$scope.inputVO.narratorName = tota[0].body.carList[0].NARRATOR_NAME;	//解說人員姓名

								$scope.narratorDisabled = true;
							}
							if (tota[0].body.mainList && tota[0].body.mainList.length > 0) {
								$scope.inputVO.custID = tota[0].body.mainList[0].CUST_ID;				//客戶ID
								$scope.inputVO.trustTS = tota[0].body.mainList[0].TRUST_TRADE_TYPE;		//信託交易類別 S:特金 M:金錢信託
							}
						
							if ($scope.inputVO.carSEQ) {
								angular.forEach($scope.cartList, function(row){
									if ($scope.inputVO.carSEQ == row.SEQ_NO) {
										$scope.inputVO.prodID = row.PROD_ID;
							        	$scope.inputVO.prodName = row.PROD_NAME;									//商品名稱
							        	$scope.inputVO.prodCurr = row.PROD_CURR; 									//計價幣別
							        	$scope.inputVO.prodRiskLV = row.PROD_RISK_LV; 								//產品風險等級
							        								        	
							        	$scope.inputVO.unitNum = row.UNIT_NUM;										//股數
							        	$scope.inputVO.prodMinBuyAmt = parseFloat(row.PROD_MIN_BUY_AMT); 			//最低申購金額
							        	$scope.inputVO.prodMinBuyUnit = parseInt(row.PROD_MIN_BUY_UNIT);			//最低買進單位
							        	$scope.inputVO.prodMinGrdUnit = parseInt(row.PROD_MIN_GRD_UNIT);			//最低累進單位
							        	$scope.inputVO.countryCode = row.TRADE_MARKET;                              //交易國家別
							        	$scope.quoteProd="a="+$scope.inputVO.prodID+"&Market="+$scope.inputVO.countryCode;
							        	$scope.inputVO.dueDateShow = row.DUE_DATE_SHOW;
							        	$scope.inputVO.debitAcct = row.DEBIT_ACCT+"_"+$scope.inputVO.prodCurr;
							        	$scope.inputVO.trustAcct = row.TRUST_ACCT;
							        	$scope.inputVO.creditAcct = row.CREDIT_ACCT+"_"+$scope.inputVO.prodCurr;
							        	if ($scope.inputVO.trustTS != 'M') {
							        		$scope.setAcctDisplay(["SOT.DEBIT_ACCT_LIST","SOT.CREDIT_ACCT_LIST"],$scope.inputVO.prodCurr);
							        	}
							        	var array = ($scope.inputVO.dueDateShow).split(',');
							        	$scope.dueDate = [];
							        	angular.forEach(array, function(value, key) {
							        		if(value){
							        			$scope.dueDate.push({LABEL: value + " 00:00:00", DATA: value + " 00:00:00"});
							        		}
						    			});
							        	$scope.inputVO.dueDate = row.DUE_DATE;
							        	$scope.inputVO.entrustType = row.ENTRUST_TYPE;								//買進價格指示
							        	$scope.checkShowDueDate();
							        	
										switch ($scope.inputVO.entrustType){
											case "1":
												break;
											case "2":
												$scope.inputVO.entrustAmt = row.ENTRUST_AMT;
												break;
											case "3":
												$scope.inputVO.entrustAmt1 = row.ENTRUST_AMT;
												break;
											case "4":
												$scope.inputVO.entrustAmt2 = row.ENTRUST_AMT;
												$scope.inputVO.entrustDiscount = row.ENTRUST_DISCOUNT;
												break;
										}
										
										$scope.getFeeTypeData();
										$scope.inputVO.closingPriceDate = $scope.toJsDate(row.CLOSING_PRICE_DATE);	//限價-日期
										$scope.inputVO.closingPrice = row.CLOSING_PRICE;							//限價-收盤價
										$scope.inputVO.defaultFeeRate = row.DEFAULT_FEE_RATE; 						//表定手續費率
										$scope.inputVO.entrustAmt = $scope.inputVO.closingPrice * 1.1;              //約定限價
										$scope.inputVO.entrustAmt2 = $scope.inputVO.closingPrice * (100- row.ENTRUST_DISCOUNT) / 100 ;            //限價-5%
										
										$scope.inputVO.feeRateType = row.FEE_TYPE; 									//手續費優惠方式
										if($scope.inputVO.feeRateType == 'D'){
											$scope.feeRateType = row.BARGAIN_APPLY_SEQ;
										}else{
											$scope.feeRateType = $scope.inputVO.feeRateType;
										}
										$scope.inputVO.feeRate = row.FEE_RATE; 										//手續費率
										$scope.inputVO.fee = row.FEE; 												//手續費金額
										$scope.inputVO.feeDiscount = row.FEE_DISCOUNT; 								//折數
										$scope.inputVO.stopLossPerc = row.STOP_LOSS_PERC;
										$scope.inputVO.takeProfitPerc = row.TAKE_PROFIT_PERC;
										$scope.inputVO.contractID = row.CONTRACT_ID;
									}
				    			});
							}
							if(tota[0].body.prodDTL && tota[0].body.prodDTL.length > 0){
								$scope.inputVO.marketPrice = tota[0].body.prodDTL[0].MARKET_PRICE;
							}
//							if($scope.inputVO.entrustAmt) $scope.inputVO.entrustAmt = $filter('number')($scope.inputVO.entrustAmt,6);
//
//							if($scope.inputVO.entrustAmt1) $scope.inputVO.entrustAmt1 = $filter('number')($scope.inputVO.entrustAmt1,6);

							deferred.resolve("success");
						}
			});
			return deferred.promise;
		};
		
		$scope.callGetDefaultFeeRate = function(){
			var deferred = $q.defer();
			if ($scope.inputVO.prodID && $scope.inputVO.trustAcct) {
				$scope.sendRecv("SOT710", "getDefaultFeeRate", "com.systex.jbranch.app.server.fps.sot710.SOT710InputVO", {custID: $scope.inputVO.custID, prodId : $scope.inputVO.prodID, 
																														  trustAcct: $scope.inputVO.trustAcct, 
																														  trustCurrType: 'Y'},
						  function(tota1, isError) {
							  if (!isError) {
								  if(tota1[0].body.errorCode != null && tota1[0].body.errorCode != "") {
									  $scope.showErrorMsg(tota1[0].body.errorCode + ":" + tota1[0].body.errorTxt);
									  
									  $scope.inputVO.prodID = "";
									  $scope.prodClear();
								  } else {
									  $scope.inputVO.defaultFeeRate = tota1[0].body.defaultFeeRate;
									  deferred.resolve("success");
								  }
							  }
				});
			}else{
				 $scope.inputVO.defaultFeeRate=undefined;
				 deferred.resolve("success");
			}
			return deferred.promise;
		}
		
		$scope.getBestFeeRate = function() {
			$scope.callGetDefaultFeeRate().then(function(data) {
				var entrustAmt = undefined;
				if ($scope.inputVO.entrustType == '1') {
					entrustAmt = $scope.inputVO.closingPrice * 0.9;
				} else if ($scope.inputVO.entrustType == '2') {
					entrustAmt = $scope.inputVO.entrustAmt;
				} else if ($scope.inputVO.entrustType == '3') {
					entrustAmt = $scope.inputVO.entrustAmt1;
				} else if ($scope.inputVO.entrustType == '4') {
					entrustAmt = $scope.inputVO.entrustAmt2;
				}
				if ((entrustAmt != '' && typeof(entrustAmt) !== 'undefined' && typeof($scope.inputVO.unitNum) !== 'undefined' && typeof($scope.inputVO.defaultFeeRate) !== 'undefined') && $scope.inputVO.trustAcct) {
					$scope.sendRecv("SOT710", "getBestFeeRate", "com.systex.jbranch.app.server.fps.sot710.SOT710InputVO", {custID: $scope.inputVO.custID,
																														   trustAcct: $scope.inputVO.trustAcct,
																														   buySell: 'B',
																														   prodId: $scope.inputVO.prodID,
																														   unitNum: $scope.inputVO.unitNum,
																														   entrustAmt: $scope.moneyUnFormat(entrustAmt),
																														   dueDate: $scope.toJsDate($scope.dueDate[0].DATA),
																														   defaultFeeRate: $scope.inputVO.defaultFeeRate,
																														   trustCurrType: 'Y',
																														   trustTS: $scope.inputVO.trustTS},
						   function(tota, isError) {
							   if (!isError) {
								   //var map = $filter('filter')($scope.mappingSet['FEE_RATE_TYPE'], {DATA: "C"});
								   var map = $filter('filter')($scope.mappingSet['FEE_RATE_TYPE'], {DATA: ""});
								   if(map != undefined) {
									   map[0].FEE_RATE = tota[0].body.bestFeeRate;
									   if($scope.inputVO.feeRateType == '' || $scope.inputVO.feeRateType == null || $scope.inputVO.feeRateType == "C") {
										   $scope.inputVO.feeRate = tota[0].body.bestFeeRate;
										   $scope.getFee('rate');
									   }
								   }
								   return;
							   }
					});
				}else{
//					$scope.inputVO.feeRate=undefined;
					$scope.getFee('rate');
				}
			});
		};
		
		$scope.getFeeType = function (applySEQ) {
			//先清空
			$scope.inputVO.bargainApplySEQ = "";
			var feeRateType = $scope.feeRateType;
			if(feeRateType != 'A' && $scope.mappingSet['FEE_RATE_TYPE'].length>1){
				if(applySEQ){
					$scope.inputVO.feeRate = Number(($filter('filter')($scope.mappingSet['FEE_RATE_TYPE'], {BARGAIN_APPLY_SEQ: applySEQ,DATA: applySEQ}))[0].FEE_RATE);
				}else{
					if(feeRateType=='C'){//C為最優 cmb預設""為最優
						$scope.inputVO.feeRate = Number(($filter('filter')($scope.mappingSet['FEE_RATE_TYPE'], {DATA:""}))[0].FEE_RATE);
					}else{
						$scope.inputVO.feeRate = Number(($filter('filter')($scope.mappingSet['FEE_RATE_TYPE'], {DATA: feeRateType}))[0].FEE_RATE);
					}
				}
				
				$scope.getFee('rate');
				
				switch (feeRateType) {
				case 'A'://申請議價
					break;
				default://事先議價
					$scope.inputVO.feeRateType = 'D';
					if(applySEQ){
						$scope.inputVO.bargainApplySEQ = ($filter('filter')($scope.mappingSet['FEE_RATE_TYPE'], {BARGAIN_APPLY_SEQ: applySEQ,DATA: feeRateType}))[0].BARGAIN_APPLY_SEQ;
					}else{
						if(feeRateType=='C'){//C為最優 cmb預設""為最優
							$scope.inputVO.bargainApplySEQ = ($filter('filter')($scope.mappingSet['FEE_RATE_TYPE'], {DATA:""}))[0].BARGAIN_APPLY_SEQ;
						}else{
							$scope.inputVO.bargainApplySEQ = ($filter('filter')($scope.mappingSet['FEE_RATE_TYPE'], {DATA: feeRateType}))[0].BARGAIN_APPLY_SEQ;
						}
					}
					break;
				}
			} else {
				//申請議價
				if(feeRateType  == 'A'){
					$scope.inputVO.feeRateType = 'A';
				}
				
				$scope.inputVO.feeRate = null;
				$scope.inputVO.feeDiscount = null;
				$scope.inputVO.fee = null;
			}			
				
			//$scope.checkBestFee();//檢查手續費是否最優	
		}

		//檢查手續費是否最優(比較DefaultFeeRateVO.fee L/M/H)
		$scope.checkBestFee = function () {
			var bestFeeRate = Number(($filter('filter')($scope.mappingSet['FEE_RATE_TYPE'], {DATA: "C"}))[0].FEE_RATE);
			/*若此時選擇的優惠劣於既有的身分別或期間議價優惠，
			則會跳出提醒訊息「客戶已享有手續費率XX%，
			請選擇其他手續費優惠方式」。跳出警語後，
			還原到預設優惠折扣如手續費率、手續費金額、折數
			(「身分別」、「期間議價」其中最優)，手續費優惠方式變成'請選擇'
			*/
			
			var errMsgParam = ''; 
			if (Number(bestFeeRate) < Number($scope.inputVO.feeRate)) {
			   	errMsgParam = bestFeeRate + '%'; 
			} 
			if (errMsgParam!='') {
				$scope.showErrorMsg('「客戶已享有手續費率' + errMsgParam + ' ，請選擇其他手續費優惠方式」');
				//因為會有重算最優， 不重新選最優  $scope.inputVO.feeTypeIndex = ''; //下拉重設'請選擇'(最優)
				//因為會有重算最優， 不重新選最優  $scope.getFeeType();
			}
			
		};
		
		$scope.changeAcct = function (type) {
			debugger
			$scope.avbBalanceAmt='';
			$scope.showCurrency  ='';
			var debitAcct = $scope.inputVO.debitAcct.split("_");
			if (type == 'debit' && $scope.inputVO.trustTS != 'M') {				
				$scope.inputVO.creditAcct = debitAcct[0];
			} 
//			else {
//				var creditAcct= $scope.inputVO.creditAcct;
//				var prodCurr = $scope.inputVO.prodCurr;				
//				if (creditAcct) {
//					if (prodCurr) {
//						$scope.inputVO.debitAcct = creditAcct + "_" + prodCurr;
//					} else {
//						if (debitAcct[0] == creditAcct) {
//							$scope.inputVO.debitAcct = creditAcct + "_" + debitAcct[1];
//						}  else {
//							var debitAcctArr = $filter('filter')($scope.mappingSet['SOT.DEBIT_ACCT_LIST'], creditAcct);
//							$scope.inputVO.debitAcct = debitAcctArr[0].LABEL;
//						}
//					}					
//				} else {
//					$scope.inputVO.debitAcct = "";
//				}
//			}
			if($scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY'] != undefined) {
				for(var i=0;i<$scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY'].length;i++){
					if($scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY'][i].LABEL==$scope.inputVO.debitAcct){
						$scope.avbBalanceAmt=$scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY'][i].AVBBALANCE;
						$scope.showCurrency  =$scope.mappingSet['SOT.DEBIT_ACCT_LIST#DSIPLAY'][i].CURRENCY;
						break;
					}				
				}
			}
			
			$scope.sendRecv("SOT710", "getAvailBalance", "com.systex.jbranch.app.server.fps.sot710.SOT710InputVO", {custID: $scope.inputVO.custID, debitAcct: debitAcct[0]},
					function(tota, isError) {
					if (!isError) {
						$scope.availBalanceList = tota[0].body.availBalanceList;
//						console.log(JSON.stringify($scope.availBalanceList));
						$scope.showAvbBalance = 0;//A+B+C-D
						$scope.sellAmt = 0;
						$scope.tDaySAmt = 0;
						$scope.sellUseAmt = 0;
						$scope.bankUseAmt = 0;
						
						angular.forEach($scope.availBalanceList, function(row){
							if(row.TrxMarketCode.trim() == $scope.inputVO.countryCode){
								$scope.sellAmt = row.SellAmt;				//在途款總額C
								$scope.tDaySAmt = row.TDaySAmt;				//今日賣出金額B
								$scope.sellUseAmt = row.SellUseAmt;			//在途款圈存金額D
								$scope.bankUseAmt = row.BankUseAmt;			//ETF銀行圈存金額	
							}						
						});
						$scope.showAvbBalance = Number($scope.avbBalanceAmt) + Number($scope.tDaySAmt) + Number($scope.sellAmt) - Number($scope.sellUseAmt);
					}
			});
		};
		
		//檢查信託帳號
		$scope.checkTrustAcct = function(){
			debugger
				$scope.cmbDebitAcct=false;
				$scope.cmbCreditAcct=false;				
				var debitAcctListDisplay='SOT.DEBIT_ACCT_LIST#DSIPLAY';
				var creditAcctListDisplay='SOT.CREDIT_ACCT_LIST#DSIPLAY';
				var debitAcctList='SOT.DEBIT_ACCT_LIST';
				var creditAcctList='SOT.CREDIT_ACCT_LIST';
				
				$scope.setAcctDisplay([creditAcctList,debitAcctList]); //初始化
				//商品幣別 無此幣扣款帳號要增加
				if ($scope.inputVO.prodCurr) {
					angular.forEach($scope.mappingSet['SOT.TRUST_ACCT_LIST'], function(row){
						if (sotService.is168(row.DATA)) {
							var checkFlag = false;
							angular.forEach($scope.mappingSet[debitAcctListDisplay], function(row2){
								var checkAcct = row.DATA+'_'+$scope.inputVO.prodCurr;//檢核是否有此幣別帳號
								if (checkAcct == row2.DATA) {
									checkFlag = true;
								}
							});
							if (!checkFlag) {
								$scope.mappingSet[debitAcctListDisplay].push({LABEL: row.DATA+'_'+$scope.inputVO.prodCurr, 
																			  DATA: row.DATA+'_'+$scope.inputVO.prodCurr,
																			  AVBBALANCE:'0',
																			  CURRENCY:$scope.inputVO.prodCurr,
																			  label:row.DATA+'_'+$scope.inputVO.prodCurr,
																			  value:row.DATA+'_'+$scope.inputVO.prodCurr});
							}
						}
					});
				}				
				debugger
				//信託帳號檢核
				if ($scope.inputVO.trustAcct && sotService.is168($scope.inputVO.trustAcct)) {
					$scope.mappingSet[debitAcctListDisplay] = $filter('filter')($scope.mappingSet[debitAcctListDisplay],
																			{DATA: $scope.inputVO.trustAcct},
																			function(actual, expected) { return angular.equals(actual.split("_")[0], expected)}
																		  );
					$scope.mappingSet[creditAcctListDisplay] = $filter('filter')($scope.mappingSet[creditAcctListDisplay],
																			{DATA: $scope.inputVO.trustAcct},
																			function(actual, expected) { return angular.equals(actual, expected)}
																		   );
				}
				
				//商品幣別 				
	 		    if($scope.inputVO.prodCurr){	  //計價
	 			   $scope.mappingSet[debitAcctListDisplay] = $filter('filter')($scope.mappingSet[debitAcctListDisplay], {CURRENCY: $scope.inputVO.prodCurr});
	 		    }
	 		   
				if($scope.mappingSet[debitAcctListDisplay].length == 1 && $scope.inputVO.trustTS == 'S'){ //只有一筆不能勾選
					$scope.inputVO.debitAcct = $scope.mappingSet[debitAcctListDisplay][0].DATA;
					$scope.cmbDebitAcct=true;
				}
				if($scope.mappingSet[creditAcctListDisplay].length == 1 && $scope.inputVO.trustTS == 'S'){ //只有一筆不能勾選
					$scope.inputVO.creditAcct = $scope.mappingSet[creditAcctListDisplay][0].DATA;
					$scope.cmbCreditAcct=true;
				}
				
				$scope.changeAcct('debit');//查詢餘額
		};
		
		$scope.getAvailBalance = function () {
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT210/SOT210_AVAIL_BALANCE.html',
			    className: 'SOT210_AVAIL_BALANCE',
			    controller:['$scope',function($scope){
			    	
				}],
				showClose: false,
				scope : $scope
			 });
			
			dialog.closePromise.then(function(data){
				if(data.value && data.value != "cancel"){
					
				}
			});
		};
		
		$scope.checkDueDate = function () {
			//只要是購買交易所為美國的商品，任何客戶都要簽署 W8Ben
			if ($scope.inputVO.countryCode == "USA") {
				if ($scope.toJsDate($scope.inputVO.dueDate) < $scope.inputVO.w8benStartDate ||
					$scope.toJsDate($scope.inputVO.dueDate) > $scope.inputVO.w8benEedDate) {
					$scope.showErrorMsg('ehl_01_sot210_002');
					return;
				}
			}
			
			if ($scope.inputVO.profInvestorYN == "Y") {
				if ($scope.toJsDate($scope.inputVO.dueDate) > $scope.inputVO.piDueDate) {
//					$scope.showErrorMsg('ehl_01_sot210_003');
					$scope.showMsg('ehl_01_sot210_003');
					return;
				}
			}
			
			if ($scope.toJsDate($scope.inputVO.dueDate) > $scope.inputVO.kycDueDate) {
				$scope.showErrorMsg('ehl_01_sot210_004');
				return;
			}
		}
				// 取得新交易序號
		$scope.getTradeSEQ = function() {
			var deferred = $q.defer();
			$scope.sendRecv("SOT712", "getTradeSEQ", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {},
					function(tota, isError) {
						if (!isError) {
							$scope.inputVO.tradeSEQ = tota[0].body.tradeSEQ; 
							deferred.resolve("success");
						}
			});
			return deferred.promise;
		};
		
		// 取得新交易序號
		$scope.getTradeSEQ = function() {
			var deferred = $q.defer();
			$scope.sendRecv("SOT712", "getTradeSEQ", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {},
					function(tota, isError) {
						if (!isError) {
							$scope.inputVO.tradeSEQ = tota[0].body.tradeSEQ; 
							deferred.resolve("success");
						}
			});
			return deferred.promise;
		};
		
		//從快查或別的交易過來，帶CUSTID
		$scope.queryChkSenior = function() {
			debugger
			if($scope.connector('get','SOTCustID')){
				$scope.inputVO.custID=$scope.connector('get','SOTCustID');
				$scope.connector('set','SOTCustID',null);
				$scope.getSOTCustInfo(true).then(function(data){
					var prod = $scope.connector('get', 'SOTProd');
					if(prod){
						if(prod.APPLY_SEQ){
							if(prod.InsuranceNo){
								$scope.inputVO.prodID = prod.InsuranceNo.trim();
							}else if(prod.PROD_ID){
								$scope.inputVO.prodID = prod.PROD_ID.trim();
							}else{
								$scope.inputVO.prodID = null;
							}
							$scope.apply_seq = prod.APPLY_SEQ;
							$scope.getProdDTL().then(function(){
								$scope.inputVO.unitNum = prod.ENTRUST_UNIT;
								$scope.inputVO.entrustType = '3';
								$scope.inputVO.entrustAmt1 = prod.ENTRUST_AMT;
//								$scope.inputVO.feeRateType = 'D';
								$scope.inputVO.feeRateType = '';
								$scope.feeRateType = $scope.apply_seq;
								$scope.checkUnitNum();
							});
//							$scope.getFeeType($scope.apply_seq);
						}else{
							$scope.apply_seq = undefined;
							if(prod.InsuranceNo){
								$scope.inputVO.prodID = prod.InsuranceNo.trim();
							}else if(prod.PROD_ID){
								$scope.inputVO.prodID = prod.PROD_ID.trim();
							}else{
								$scope.inputVO.prodID = null;
							}
							$scope.getProdDTL();
						}
						$scope.connector('set','SOTPord',null);
					}
				});
			}
			
			$scope.cartList = [];
			if($scope.inputVO.tradeSEQ) {     
				$scope.noCallCustQuery().then(function(data) {
					$scope.getSOTCustInfo().then(function(data) {
							if($scope.inputVO.prodID) {
								$scope.sendRecv("SOT210", "getProdDTL", "com.systex.jbranch.app.server.fps.sot210.SOT210InputVO", $scope.inputVO,
										function(tota, isError) {
											if (!isError) {
												if (tota[0].body.prodDTL != null && tota[0].body.prodDTL.length > 0) {
//													$scope.inputVO.countryCode = tota[0].body.prodDTL[0].COUNTRY_CODE;
													$scope.inputVO.pType = tota[0].body.pType;												//交易市場別
													$scope.inputVO.stockCode = tota[0].body.prodDTL[0].STOCK_CODE;							//交易所代號
													$scope.showentrustType=[true,true,true,true];
													for(var i=0;i<tota[0].body.entrustTypeList.length;i++){
														//tota[0].body.entrustTypeList-1=index   $scope.showentrustType[index]=false
														$scope.showentrustType[tota[0].body.entrustTypeList[i].PARAM_CODE-1]=false;
													}
													return;
												}else{
													$scope.showErrorMsg("ehl_01_common_009");
												}
											}
											$scope.inputVO.prodID = "";
											$scope.prodClear();
								});
							}
					});
				});
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
		
		$scope.query = function() {
			//從其他頁面進入下單
			if($scope.connector('get', 'SOTTradeSEQ')){
				$scope.inputVO.tradeSEQ = $scope.connector('get', 'SOTTradeSEQ');
				$scope.inputVO.carSEQ = $scope.connector('get', 'SOTCarSEQ');
				$scope.connector('set', 'SOTTradeSEQ', null);
				$scope.connector('set', 'SOTCarSEQ', null);
			} else if ($scope.fromFPS){
				console.log($scope.FPSData);
				$scope.getTradeSEQ().then(function(data) {
					$scope.inputVO.custID = $scope.FPSData.custID;//客戶ID
					$scope.inputVO.prodID = $scope.FPSData.prdID; //商品代號	
				
					$scope.noCallCustQuery().then(function(data) {
						$scope.getSOTCustInfo().then(function(data) {
							if($scope.inputVO.prodID) {
								$scope.getProdDTL();
							}
						});
					});
				});
			}

			$scope.cartList = [];
			if($scope.inputVO.tradeSEQ) {     
				$scope.noCallCustQuery().then(function(data) {
					$scope.getSOTCustInfo().then(function(data) {
							if($scope.inputVO.prodID) {
								$scope.sendRecv("SOT210", "getProdDTL", "com.systex.jbranch.app.server.fps.sot210.SOT210InputVO", $scope.inputVO,
										function(tota, isError) {
											if (!isError) {
												if (tota[0].body.prodDTL != null && tota[0].body.prodDTL.length > 0) {
//													$scope.inputVO.countryCode = tota[0].body.prodDTL[0].COUNTRY_CODE;
													$scope.inputVO.pType = tota[0].body.pType;												//交易市場別
													$scope.inputVO.stockCode = tota[0].body.prodDTL[0].STOCK_CODE;							//交易所代號
													$scope.showentrustType=[true,true,true,true];
													for(var i=0;i<tota[0].body.entrustTypeList.length;i++){
														//tota[0].body.entrustTypeList-1=index   $scope.showentrustType[index]=false
														$scope.showentrustType[tota[0].body.entrustTypeList[i].PARAM_CODE-1]=false;
													}
													return;
												}else{
													$scope.showErrorMsg("ehl_01_common_009");
												}
											}
											$scope.inputVO.prodID = "";
											$scope.prodClear();
								});
							}
					});
				});
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
		
		$scope.refresh = function (row) {
			if (row) {
				$scope.inputVO.carSEQ = row.SEQ_NO;
			}
			$scope.noCallCustQuery();
		};
		
		$scope.addCar = function() {
			debugger;
			console.log("$scope.inputVO.carSEQ ", $scope.inputVO.carSEQ);
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg("ehl_01_common_022");
	    		return;
	    	}
			if(!$scope.inputVO.prodID && !($scope.inputVO.unitNum || $scope.inputVO.unitNum)){
				$scope.showErrorMsg("ehl_01_common_022");
				return;
			}
			if(!$scope.inputVO.entrustType){
				$scope.showErrorMsg("ehl_01_common_022");
	    		return;
			}
			//滿足、停損點接不等於0，且滿足點須大於停損點，金錢信託不用檢查
			if($scope.inputVO.trustTS != 'M' && ((($scope.inputVO.takeProfitPerc==0) || ($scope.inputVO.stopLossPerc==0)) ||
				($scope.inputVO.takeProfitPerc <= $scope.inputVO.stopLossPerc))) 
			{
				$scope.showErrorMsg("滿足、停損點皆不等於0，且滿足點須大於停損點");
	    		return;
			}
			if ($scope.cartList.length == 6) {
				$scope.showErrorMsg('ehl_01_SOT_007');
				return;
			}
			//$scope.inputVO.dueDate = $scope.toJsDate($scope.inputVO.dueDate);
			$scope.inputVO.entrustAmt  = Number($scope.inputVO.entrustAmt);
			$scope.inputVO.entrustAmt1 = Number($scope.inputVO.entrustAmt1);
			$scope.inputVO.entrustAmt2 = Number($scope.moneyUnFormat($scope.inputVO.entrustAmt2));
			debugger
			$scope.sendRecv("SOT210", "save", "com.systex.jbranch.app.server.fps.sot210.SOT210InputVO", $scope.inputVO,
					function(tota, isError) {
						debugger
						if (!isError) {
							if (tota[0].body.errorMsg != null) {
								$scope.showErrorMsg(tota[0].body.errorMsg);
								$scope.refresh();
								return;
							} else if (tota[0].body.earnAcctError) {
								$scope.inputVO.creditAcct = tota[0].body.transEarnAcct;
								$scope.showErrorMsg('ehl_01_sot210_008');
								$scope.refresh();
								return;
							} else {
								//add by Brian
								if(tota[0].body.warningMsg != null){
									$scope.showWarningMsg(tota[0].body.warningMsg);
								}
								$scope.inputVO.prodID = "";
								$scope.prodClear();
								$scope.refresh();
								
								return;
							}
						}else{
//							$scope.inputVO.entrustAmt =$filter('number')($scope.inputVO.entrustAmt,6);
//							$scope.inputVO.entrustAmt1=$filter('number')($scope.inputVO.entrustAmt1,6);
						}
			});
		};
		
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
								$scope.inputVO.prodID = "";
								$scope.prodClear();
								$scope.refresh();
								
								return;
							}
				});
            });
		};
		
		$scope.next = function() {
			$scope.sendRecv("SOT210", "next", "com.systex.jbranch.app.server.fps.sot210.SOT210InputVO", {tradeSEQ : $scope.inputVO.tradeSEQ},
					function(tota, isError) {
						if (!isError) {
							$scope.connector('set','SOTTradeSEQ', $scope.inputVO.tradeSEQ);
							$scope.connector('set','SOTCarSEQ', null);
							if ($scope.fromFPS) {
								// from FPS_SOT.js
								$scope.setSOTurl('assets/txn/SOT211/SOT211.html');
							} else {
								if ($scope.inputVO.trustTS == 'M') {
									$rootScope.menuItemInfo.url = "assets/txn/SOT213/SOT213.html";
								} else {
									$rootScope.menuItemInfo.url = "assets/txn/SOT211/SOT211.html";
								}
							}							
							
							return;
						}
			});
		};
		
		$scope.goPRD120_ETF = function () {
			var trustTS = $scope.inputVO.trustTS;
			
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT210/SOT210_ROUTE.html',
				className: 'PRD120',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					$scope.isPop = true;
					$scope.txnName = "ETF搜尋";
	        		$scope.routeURL = 'assets/txn/PRD120/PRD120_ETF.html';
	        		$scope.tradeType = "1";
	        		$scope.cust_id = $scope.$parent.inputVO.custID;
	        		$scope.trustTS = trustTS;
	        		$scope.fromSOTProdYN = "Y"; //下單商品搜尋//下單只需要輸入客戶ID時檢核，商品搜尋時不需再次檢核
	        		$scope.pType = "1";
	            }]
			}).closePromise.then(function (data) {
				if(data.value != "cancel") {
					$scope.inputVO.prodID = data.value.PRD_ID;
					$scope.getProdDTL();
				}
			});
		};
		
		$scope.goPRD120_STOCK = function () {
			var trustTS = $scope.inputVO.trustTS;
			
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT210/SOT210_ROUTE.html',
				className: 'PRD120',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					$scope.isPop = true;
					$scope.txnName = "股票搜尋";
	        		$scope.routeURL = 'assets/txn/PRD120/PRD120_STOCK.html';
	        		$scope.tradeType = "1";
	        		$scope.cust_id = $scope.$parent.inputVO.custID;
	        		$scope.trustTS = trustTS;
	        		$scope.fromSOTProdYN = "Y"; //下單商品搜尋//下單只需要輸入客戶ID時檢核，商品搜尋時不需再次檢核
	        		$scope.pType = "2";
	            }]
			}).closePromise.then(function (data) {
				if(data.value != "cancel") {
					$scope.inputVO.prodID = data.value.PRD_ID;
					$scope.getProdDTL();
				}
			});
		};
		
		$scope.init();
		//query，先做高齡檢核
		if(!$scope.connector('get','SOTCustID')) {
			//"不是"從快查或別的交易過來，帶CUSTID，維持原來
			$scope.inputVO.otherWithCustId = false;
			$scope.query();
		} else {
			//從快查或別的交易過來，帶CUSTID，先做高齡檢核
			debugger
			$scope.inputVO.otherWithCustId = true;
			$scope.inputVO.custID = $scope.connector('get','SOTCustID');
			$scope.validateSeniorCust();
		}
});