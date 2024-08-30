/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict'; 
eSoafApp.controller('SOT220Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q, validateService, sotService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT220Controller";
		
		var roundDecimal = function (val) {
			  return Math.round(Math.round(val * Math.pow(10, (4 || 0) + 1)) / 10) / Math.pow(10, (4 || 0));
			}
		
		// filter
		getParameter.XML(["SOT.CUST_TYPE", "SOT.EBANK_PRTDOC_URL", "SOT.QUOTE", "SOT.EBANK_PRTDOC_URL", "SOT.CHG_PRTDOC_URL", "SOT.ENTRUST_TYPE", "SOT.BARGAIN_STATUS","SOT.SPEC_CUSTOMER","SOT.ETF_DECIMAL_POINT"], function(totas) {
			if (totas) {
				$scope.mappingSet['SOT.CUST_TYPE'] = totas.data[totas.key.indexOf('SOT.CUST_TYPE')];
				$scope.mappingSet['SOT.EBANK_PRTDOC_URL'] = totas.data[totas.key.indexOf('SOT.EBANK_PRTDOC_URL')];
				$scope.quote = totas.data[totas.key.indexOf('SOT.QUOTE')][0].LABEL;
				$scope.eBankPredocURL = totas.data[totas.key.indexOf('SOT.EBANK_PRTDOC_URL')][0].LABEL;
				$scope.chgPrtdocURL = totas.data[totas.key.indexOf('SOT.CHG_PRTDOC_URL')][0].LABEL;
				$scope.mappingSet['SOT.ENTRUST_TYPE'] = totas.data[totas.key.indexOf('SOT.ENTRUST_TYPE')];
				$scope.mappingSet['SOT.BARGAIN_STATUS'] = totas.data[totas.key.indexOf('SOT.BARGAIN_STATUS')];
				$scope.mappingSet['SOT.SPEC_CUSTOMER'] = totas.data[totas.key.indexOf('SOT.SPEC_CUSTOMER')];//客戶註記
				$scope.mappingSet['SOT.ETF_DECIMAL_POINT'] = totas.data[totas.key.indexOf('SOT.ETF_DECIMAL_POINT')];//幣別取小數
				
			}
		});
		
		$scope.getCurrency = function(prodCurr){
			$scope.mod = $filter('filter')($scope.mappingSet['SOT.ETF_DECIMAL_POINT'], {DATA: prodCurr});
			if($scope.mod.length == 1){
				var num = $scope.mod[0].LABEL;
				var pow = Math.pow(10 , num);
//				$scope.inputVO.entrustAmt = !isNaN($scope.inputVO.entrustAmt)  ? Number($scope.inputVO.entrustAmt).toFixed($scope.mod[0].LABEL)  : $scope.inputVO.entrustAmt;
//				$scope.inputVO.entrustAmt1= !isNaN($scope.inputVO.entrustAmt1) ? Number($scope.inputVO.entrustAmt1).toFixed($scope.mod[0].LABEL) : $scope.inputVO.entrustAmt1;
//				$scope.inputVO.entrustAmt2= !isNaN($scope.inputVO.entrustAmt2) ? Number($scope.inputVO.entrustAmt2).toFixed($scope.mod[0].LABEL) : $scope.inputVO.entrustAmt2;
//				$scope.inputVO.fee = !isNaN($scope.inputVO.fee) ? Number($scope.inputVO.fee).toFixed($scope.mod[0].LABEL) : $scope.inputVO.fee;
				$scope.inputVO.entrustAmt = !isNaN($scope.inputVO.entrustAmt)  ? Math.round( $scope.inputVO.entrustAmt  * pow ) / pow  : $scope.inputVO.entrustAmt;
				$scope.inputVO.entrustAmt1= !isNaN($scope.inputVO.entrustAmt1) ? Math.round( $scope.inputVO.entrustAmt1 * pow ) / pow  : $scope.inputVO.entrustAmt1;
				$scope.inputVO.entrustAmt2= !isNaN($scope.inputVO.entrustAmt2) ? Math.round( $scope.inputVO.entrustAmt2 * pow ) / pow  : $scope.inputVO.entrustAmt2;
				$scope.inputVO.fee = !isNaN($scope.inputVO.fee) ? Math.round( $scope.inputVO.fee * pow ) / pow  : $scope.inputVO.fee;
			}
		};
		
		$scope.clearTradeSEQ = function(){
        	$scope.inputVO.tradeSEQ='';
        	$scope.query();
        }
		
		$scope.init = function() {
	    	$scope.cartList = [];
	    	$scope.creditAcctList = [];
	    	$scope.unitNumType='';
			$scope.inputVO = {
					tradeSEQ: '',
					
					custID: '', 								//客戶ID
					custName: '', 
					
					kycLV: '', 									//KYC等級
					kycDueDate: undefined, 						//KYC效期
					profInvestorYN: '', 						//是否為專業投資人
					piDueDate: undefined, 						//專業投資人效期
					custRemarks: '', 							//客戶註記
					piRemark:'',								//專投註記
					isOBU: '', 									//是否為OBU客戶
					isAgreeProdAdv: '', 						//同意投資商品諮詢服務
					bargainDueDate: undefined,					//期間議價效期
					plNotifyWays: '',							//停損停利通知方式
					takeProfitPerc: undefined,					//停利點
					stopLossPerc: undefined,					//停損點
					debitAcct: '', 								//扣款帳號
					trustAcct: '', 								//信託帳號
					creditAcct: '',								//收益入帳帳號
					w8benEffDate: undefined,					//W8ben有效日期
					w8BenEffYN: '',
					fatcaType: '',
					custProType: '',
					
					custType: 'CUST', 							//來行人員
					agentID: '', 								//代理人ID
					agentName: '',
					
					prodID: '', 								//商品ID
					prodName: '',								//商品名稱
					prodCurr: '', 								//計價幣別
					prodRiskLV: '', 							//產品風險等級
					marketPrice: undefined, 					//市價
					unitNum: undefined,							//股數
					unitNum1: undefined,						//畸零股
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
					bargainApplySeq: undefined,					//議價編號
					
					countryCode: '',							//交易所
					stockCode: '',								//交易所代號
					number: undefined,							//庫存股數
					isNewProd: '',								//是否為新商品
					entrustCur: '', 							//交易幣別
					refPrice: undefined,						//外幣參考市值
					trustCurrType: '',							//信託業務別
						
					narratorID : projInfoService.getUserID(),    //解說人員ID
					narratorName : projInfoService.getUserName(), //解說人員姓名	
					trustTS : 'S'								 //M:金錢信託  S:特金交易
						
			};
		};
		
        $scope.cartList = [];
        
		$scope.custClear = function() {
			$scope.inputVO.custName = '';
			
			$scope.inputVO.kycLV = '';									//KYC等級
			$scope.inputVO.kycDueDate = undefined;						//KYC效期
			$scope.inputVO.profInvestorYN = '';							//是否為專業投資人
			$scope.inputVO.piDueDate = undefined;						//專業投資人效期
			$scope.inputVO.custRemarks = '';							//客戶註記
			$scope.inputVO.piRemark = '';								//專投註記
			$scope.inputVO.isOBU = '';									//是否為OBU客戶
			$scope.inputVO.isAgreeProdAdv = '';							//同意投資商品諮詢服務
			$scope.inputVO.bargainDueDate =  undefined;					//期間議價效期
			$scope.inputVO.plNotifyWays = '';							//停損停利通知方式
			$scope.inputVO.takeProfitPerc = undefined;					//停利點
			$scope.inputVO.stopLossPerc = undefined;					//停損點
			$scope.inputVO.debitAcct = '';								//扣款帳號
			$scope.inputVO.trustAcct = '';								//信託帳號
			$scope.inputVO.creditAcct = '';								//收益入帳帳號
			$scope.inputVO.w8benEffDate = undefined;					//W8ben有效日期
			$scope.inputVO.w8BenEffYN = '';
			$scope.inputVO.fatcaType = '';
			$scope.inputVO.custProType = '';
			
			$scope.inputVO.custType = 'CUST';							//來行人員
			$scope.inputVO.agentID = '';								//代理人ID
			$scope.inputVO.agentName = '';
        };
        

        $scope.unitNum1 = undefined;
        
        $scope.prodClear = function() {
        	$scope.inputVO.prodID = ''; 
        	$scope.inputVO.prodName = '';								//商品名稱
        	$scope.inputVO.prodCurr = ''; 								//計價幣別
        	$scope.inputVO.prodRiskLV = ''; 							//產品風險等級
        	$scope.inputVO.marketPrice = undefined; 					//市價
        	$scope.inputVO.unitNum = undefined;							//股數
        	$scope.inputVO.unitNum1 = undefined;						//畸零股
        	$scope.inputVO.prodMinBuyUnit = undefined;					//最低賣出單位
        	$scope.inputVO.prodMinGrdUnit = undefined;					//最低累進單位
        	$scope.inputVO.dueDateShow = '';
			$scope.inputVO.dueDate = undefined; 	
			$scope.inputVO.entrustType = '';							//賣出價格指示
			$scope.inputVO.entrustAmt = undefined;						//約定限價
			$scope.inputVO.entrustAmt1 = undefined; 					//限價-元
			$scope.inputVO.closingPriceDate = undefined;				//限價-日期
			$scope.inputVO.closingPrice = undefined;					//限價-收盤價
			$scope.inputVO.entrustDiscount = 5; 						//限價-yy%
			$scope.inputVO.entrustAmt2 = undefined;						//限價-XXXX.XX元以下買進
			$scope.inputVO.defaultFeeRate = undefined; 					//表定手續費率
			$scope.inputVO.feeRateType = ''; 							//手續費優惠方式
			$scope.inputVO.feeRate = undefined; 						//手續費率
			$scope.inputVO.fee = undefined; 							//手續費金額
			$scope.inputVO.feeDiscount = undefined; 					//折數
			$scope.inputVO.brgReason = '下單議價';						//議價原因
			$scope.inputVO.bargainApplySeq = undefined; 				//議價編號
			$scope.inputVO.countryCode = '';							//交易所
			$scope.inputVO.stockCode = '';								//交易所代號
			$scope.inputVO.number = undefined;							//庫存股數
			$scope.inputVO.isNewProd = '';								//是否為新商品
			$scope.inputVO.entrustCur = '';								//交易幣別
			$scope.inputVO.refPrice = undefined;						//外幣參考市值
			$scope.inputVO.trustCurrType = '';							//信託業務別
			$scope.inputVO.sellType = '1';								//賣出股數類型：1整股 2零股
        }
		
		// getFee
		$scope.getFee = function(type) {
			$scope.inputVO.fee=$scope.moneyUnFormat($scope.inputVO.fee);
			if (type == 'rate') {
				$scope.inputVO.feeDiscount = Number($filter('number')($scope.inputVO.feeRate / $scope.inputVO.defaultFeeRate * 10,3));
			} else if (type == 'feeDiscount') {
				$scope.inputVO.feeRate = ($scope.inputVO.feeDiscount * $scope.inputVO.defaultFeeRate) / 10;
			}
			
			var feeRate = $scope.inputVO.feeRate;
			var unitNum = ($scope.inputVO.sellType == "1" ? $scope.inputVO.unitNum : $scope.inputVO.unitNum1);
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
					$scope.inputVO.fee = roundDecimal(unitNum * $scope.moneyUnFormat($scope.inputVO.closingPrice) * feeRate / 100);
					break;
				case "2":	
					$scope.inputVO.fee = roundDecimal(unitNum * $scope.moneyUnFormat($scope.inputVO.entrustAmt) * feeRate / 100);
					break;
				case "3":
					$scope.inputVO.fee = roundDecimal(unitNum * $scope.moneyUnFormat($scope.inputVO.entrustAmt1) * feeRate / 100);
					break;
				case "4":
					$scope.inputVO.fee = roundDecimal(unitNum * $scope.moneyUnFormat($scope.inputVO.entrustAmt2) * feeRate / 100);
					break;
			}
			$scope.getCurrency($scope.inputVO.prodCurr); // 幣別取小數轉換
		};
		
		$scope.getFeeTypeData = function () {
			
			$scope.sendRecv("SOT710", "getFeeTypeData", "com.systex.jbranch.app.server.fps.sot710.SOT710InputVO", {'custID': $scope.inputVO.custID},
					function(tota1, isError) {
						if (!isError) {
							$scope.feeTypeData = tota1[0].body.feeTypeList;

							$scope.mappingSet['FEE_RATE_TYPE'] = [];
							$scope.mappingSet['FEE_RATE_TYPE'].push({LABEL: "申請議價", DATA: "A"});
							angular.forEach($scope.feeTypeData, function(row){
								if ($scope.inputVO.prodID == (row.prodId).trim()) {
									$scope.mappingSet['FEE_RATE_TYPE'].push({LABEL: ("事先議價" + row.feeDiscount+ "折"), 
																			 //DATA: "D",
																			 DATA: row.applySeq,//事先議價(多筆)
																			 BARGAIN_APPLY_SEQ: row.applySeq,
																			 FEE_RATE: row.feeRate}
									);
								}
			    			});
						}
			});
			
		};
		
		// SOT701-客戶電文
		$scope.getSOTCustInfo = function(isInit) {
			var deferred = $q.defer();
			$scope.inputVO.custID = $filter('uppercase')($scope.inputVO.custID);
			var validCustID = validateService.checkCustID($scope.inputVO.custID); //自然人和法人檢查
			if(validCustID==false){ 
				$scope.inputVO.custID='';
			}
			if(!isInit){
				$scope.custClear();
				$scope.prodClear();
				$scope.inputVO.prodID = "";
			}
			if($scope.inputVO.custID) {				
				$scope.sendRecv("SOT220", "getSOTCustInfo", "com.systex.jbranch.app.server.fps.sot220.SOT220InputVO", {'custID':$scope.inputVO.custID, 'prodType':2, 'tradeType':2, 'trustTS':$scope.inputVO.trustTS},
						function(tota, isError) {
							if (!isError) {
									$scope.inputVO.custName = tota[0].body.custName;
									$scope.inputVO.kycLV = tota[0].body.kycLV;									//KYC等級
									$scope.inputVO.kycDueDate = $scope.toJsDate(tota[0].body.kycDueDate);		//KYC效期
									$scope.inputVO.profInvestorYN = tota[0].body.profInvestorYN;				//是否為專業投資人
									$scope.inputVO.piDueDate = $scope.toJsDate(tota[0].body.piDueDate);			//專業投資人效期
									$scope.inputVO.custRemarks = tota[0].body.custRemarks;						//客戶註記
									$scope.inputVO.piRemark = tota[0].body.piRemark;							//專投註記
									$scope.inputVO.isOBU = tota[0].body.isOBU;									//是否為OBU客戶
									$scope.inputVO.isAgreeProdAdv = tota[0].body.isAgreeProdAdv;				//同意投資商品諮詢服務
									if(tota[0].body.bargainDueDate){
										$scope.inputVO.bargainDueDate =  $scope.toJsDate(tota[0].body.bargainDueDate);					//期間議價效期
									} //期間議價效期
									$scope.inputVO.plNotifyWays = tota[0].body.plNotifyWays;					//停損停利通知方式
									$scope.inputVO.takeProfitPerc = tota[0].body.takeProfitPerc;				//停利點
									$scope.inputVO.stopLossPerc = tota[0].body.stopLossPerc;					//停損點
									$scope.inputVO.w8benEffDate = $scope.toJsDate(tota[0].body.w8benEffDate);	//W8ben有效日期
									$scope.inputVO.w8BenEffYN = tota[0].body.w8BenEffYN;
									$scope.inputVO.fatcaType = tota[0].body.fatcaType;
									$scope.inputVO.custProType = tota[0].body.custProType;						//專投種類
										
									if($scope.inputVO.trustTS == 'M' && tota[0].body.deathFlag == 'Y') {
										//金錢信託若為死亡戶，不可下單。
										$scope.showErrorMsg('死亡戶，不可下單'); 
										$scope.inputVO.custID = "";
										$scope.custClear();
									}
									
									if(!isInit){
										$scope.mappingSet['SOT.DEBIT_ACCT_LIST'] = tota[0].body.debitAcct;
										$scope.mappingSet['SOT.INCLUDING_DIGIT_DEBIT_ACCT_LIST'] = tota[0].body.includingDigitdebitAcct;
//										$scope.mappingSet['SOT.TRUST_ACCT_LIST'] = tota[0].body.trustAcct;
										//$scope.inputVO.debitAcct = (tota[0].body.debitAcct.length > 0 ? tota[0].body.debitAcct[0].LABEL : "");				//扣款帳號
										//$scope.inputVO.trustAcct = (tota[0].body.trustAcct.length > 0 ? tota[0].body.trustAcct[0].LABEL : "");				//信託帳號
										if ($scope.inputVO.trustTS != 'M') {
											$scope.creditAcctList = tota[0].body.creditAcct;
											$scope.mappingSet['SOT.CREDIT_ACCT_LIST'] = tota[0].body.creditAcct;
											$scope.inputVO.creditAcct = (tota[0].body.creditAcct.length > 0 ? tota[0].body.creditAcct[0].LABEL : "");			//收益入帳帳號
											$scope.creditAcctDisabled = false;
										}
									}
												
									deferred.resolve("success");
							} else {
								$scope.inputVO.custID = "";
								$scope.custClear();
							}
				});
			}else{
				$scope.custClear();
			}
			
			return deferred.promise;
		};
		
		$scope.goSOT222 = function(isBtn) {
			$scope.custID = $scope.inputVO.custID;
			$scope.isOBU = $scope.inputVO.isOBU;
			
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT222/SOT222.html',
				className: 'SOT222',
				showClose: false,
				scope : $scope
			}).closePromise.then(function (data) {
				$scope.prodClear();
//				$scope.row = $scope.connector('get', 'SOT222'); 
				if($scope.connector('get', 'SOTProd')){
					//#0695 排除數存戶
					debugger;
					var tempTrustAcct = $scope.connector('get', 'SOTProd').TrustAcct.trim();
					if(sotService.is168(tempTrustAcct) && sotService.isDigitAcct(tempTrustAcct,$scope.mappingSet['SOT.DEBIT_ACCT_LIST'])){
						$scope.showErrorMsg("ehl_02_SOT_996");
						$scope.prodClear();
						return;
					} 
					
					$scope.inputVO.prodID = $scope.connector('get', 'SOTProd').InsuranceNo?$scope.connector('get', 'SOTProd').InsuranceNo.trim():null;
					$scope.getProdDTL();
					}
				});
		};
		
		$scope.getTradeDueDate = function () {
			$scope.dueDate = [];
			$scope.inputVO.dueDateShow=[];
			var deferred = $q.defer();
			$scope.sendRecv("SOT210", "getTradeDueDate", "com.systex.jbranch.app.server.fps.sot210.SOT210InputVO", {custID: $scope.inputVO.custID, stockCode: $scope.inputVO.stockCode},
					function(totaDate, isError) {
						if (!isError) {
							$scope.dueDate = totaDate[0].body.dueDate;
//							$scope.showDueDate=[];
							$scope.outputVO = totaDate[0].body;
							
							$scope.inputVO.dueDate = $scope.dueDate[0].DATA;
							angular.forEach($scope.dueDate, function(row, index){
								$scope.inputVO.dueDateShow = $scope.inputVO.dueDateShow + $filter('date')($scope.toJsDate(row.DATA), "yyyy-MM-dd") + ",";
//								$scope.showDueDate[index] = false;
			    			});
							$scope.checkShowDueDate();
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
		
		//inputVO.prodCurr
		// 商品查詢
		$scope.getProdDTL = function (isInit) {
			var deferred = $q.defer();
			$scope.quoteProd="";
			if($scope.inputVO.prodID) {
				$scope.sendRecv("SOT220", "getProdDTL", "com.systex.jbranch.app.server.fps.sot220.SOT220InputVO", {custID: $scope.inputVO.custID, prodID: $scope.inputVO.prodID}, 
		        	function(tota, isError) {
						if (!isError) {
							//無商品資料
							if(!tota[0].body.prodDTL) {
								$scope.inputVO.prodID = "";
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							if (tota[0].body.errorMsg != "" && tota[0].body.errorMsg != null) {
								$scope.showErrorMsg(tota[0].body.errorMsg);

								$scope.inputVO.prodID = "";
								$scope.prodClear();
								
								return;
							} else {
								$scope.prodList = tota[0].body.prodDTL[0];
								$scope.showentrustType=[true,true,true,true];
								for(var i=0;i<tota[0].body.entrustTypeList.length;i++){
									//tota[0].body.entrustTypeList-1=index   $scope.showentrustType[index]=false
									$scope.showentrustType[tota[0].body.entrustTypeList[i].PARAM_CODE-1]=false;
								}
								debugger;
								if ($scope.inputVO.trustTS != 'M') {
									$scope.inputProdDTL($scope.prodList,isInit);
								} else {
									//金錢信託
									$scope.inputProdDTL_Mon($scope.prodList);
								}
								
								$scope.getTradeDueDate().then(function(data) {
									$scope.getBestFeeRate();
								});
							}
							$scope.getCurrency($scope.inputVO.prodCurr); //幣別取小數轉換
							deferred.resolve("success");
						}
					}
		        );
			} else {
				$scope.prodClear();
				$scope.inputVO.prodID = "";
			}

			return deferred.promise;
		};
		
		$scope.inputProdDTL = function (row,isInit) {
			$scope.inputVO.countryCode = row.COUNTRY_CODE;
			$scope.quoteProd="a="+$scope.inputVO.prodID+"&Market="+$scope.inputVO.countryCode;
			//a=商品代碼&market=商品國籍
			$scope.inputVO.pType = row.PTYPE;					//交易市場別
			$scope.inputVO.stockCode = row.STOCK_CODE;			//交易所代號
			$scope.inputVO.marketPrice = row.MARKET_PRICE;		//市價
			
			if(!isInit){
				//由電文or DB 傳送
				if($scope.connector('get', 'SOTProd')){
					$scope.inputVO.closingPrice = $scope.connector('get', 'SOTProd').CurAmt;			//收盤價
					$scope.inputVO.prodName = $scope.connector('get', 'SOTProd').ProductName;			//商品名稱
					$scope.inputVO.prodCurr = $scope.connector('get', 'SOTProd').CurCode;		//計價幣別
					$scope.inputVO.trustAcct = $scope.connector('get', 'SOTProd').TrustAcct;			//信託帳號
					$scope.inputVO.number = $scope.connector('get', 'SOTProd').Number;					//庫存股數  存入db欄位 TRUST_NUM
					$scope.inputVO.entrustCur = $scope.connector('get', 'SOTProd').EntrustCur;		//交易幣別
					$scope.inputVO.refPrice = $scope.connector('get', 'SOTProd').ForCurBal;			//外幣參考市值
					$scope.inputVO.isNewProd = $scope.connector('get', 'SOTProd').ProductType2=="Y"?"N":"Y";			//是否為新商品
					$scope.inputVO.trustCurrType = $scope.connector('get', 'SOTProd').TrustBusinessType;	//信託業務別
					$scope.connector('set', 'SOTProd',undefined);
				}else{
					$scope.inputVO.closingPrice = row.CUR_AMT;			//收盤價
					$scope.inputVO.prodName = row.PROD_NAME;			//商品名稱
					$scope.inputVO.prodCurr = row.CURRENCY_STD_ID;		//計價幣別
					$scope.inputVO.trustAcct = row.TRUST_ACCT;			//信託帳號
					$scope.inputVO.number = row.NUMBER;					//庫存股數  存入db欄位 TRUST_NUM
					$scope.inputVO.entrustCur = row.ENTRUST_CUR;		//交易幣別
					$scope.inputVO.refPrice = row.FOR_CUR_BAL;			//外幣參考市值
					$scope.inputVO.isNewProd = row.IS_NEW_PROD;			//是否為新商品
					$scope.inputVO.trustCurrType = row.TRUST_CURR_TYPE;	//信託業務別
				}
				
				$scope.inputVO.prodRiskLV = row.RISKCATE_ID;		//產品風險等級
				$scope.inputVO.prodMinBuyUnit = row.TXN_UNIT;		//最低賣出單位
				$scope.inputVO.prodMinGrdUnit = row.TRADING_UNIT;	//最低累進單位
				
//				if($scope.inputVO.stockCode && $scope.inputVO.stockCode=='HKSE'){
//					if($scope.inputVO.number % $scope.inputVO.prodMinBuyUnit != 0)
//						$scope.showentrustType=[true,true,false,true];
//					else
//						$scope.showentrustType=[false,true,true,true];
//				}
//				if ($scope.inputVO.trustAcct && AcctsotService.is168($scope.inputVO.trust))  //隱蔽掉&& AcctsotService.is168($scope.inputVO.trust)
				if ($scope.inputVO.trustTS != 'M') {
					if ($scope.inputVO.trustAcct && sotService.is168($scope.inputVO.trustAcct) ){
						$scope.mappingSet['SOT.CREDIT_ACCT_LIST'] = [];
						debugger;
						$scope.mappingSet['SOT.CREDIT_ACCT_LIST'].push({LABEL:$scope.inputVO.trustAcct ,DATA:$scope.inputVO.trustAcct});
						$scope.inputVO.creditAcct = $scope.inputVO.trustAcct;
						$scope.creditAcctDisabled = true;
					} else {
						$scope.mappingSet['SOT.CREDIT_ACCT_LIST'] = $scope.creditAcctList;
	//					$scope.inputVO.creditAcct = (tota[0].body.creditAcct.length > 0 ? tota[0].body.creditAcct[0].LABEL : "");
						//有傳信託業務別
						if ($scope.inputVO.trustCurrType) {
				 		   if($scope.inputVO.trustCurrType=='N' || $scope.inputVO.trustCurrType=='C'){ //N台幣
				 			   $scope.mappingSet['SOT.CREDIT_ACCT_LIST'] = $scope.getCreditAcctList($scope.inputVO.trustCurrType);//$filter('filter')($scope.mappingSet['SOT.CREDIT_ACCT_LIST'], {CURRENCY: 'TWD'});
				 		   } else if ($scope.inputVO.trustCurrType=='Y'){  //Y外幣
				 			   $scope.mappingSet['SOT.CREDIT_ACCT_LIST'] = $scope.getCreditAcctList($scope.inputVO.trustCurrType);//$filter('filter')($scope.mappingSet['SOT.CREDIT_ACCT_LIST'],{CURRENCY: "TWD"},function(actual, expected) { return !angular.equals(actual, expected)});
				 		   }
						}
						$scope.creditAcctDisabled = false;
					}				
					
					if($scope.mappingSet['SOT.CREDIT_ACCT_LIST'].length==1){ //只有一筆不能勾選
						$scope.inputVO.creditAcct=$scope.mappingSet['SOT.CREDIT_ACCT_LIST'][0].DATA;
						$scope.creditAcctDisabled = true;
					}
				}
				
				$scope.inputVO.unitNum1 = $scope.inputVO.number % $scope.inputVO.prodMinBuyUnit;
															
				$scope.inputVO.entrustAmt = $filter('number')($scope.inputVO.closingPrice * 0.9,6);	//約定限價
				$scope.inputVO.closingPriceDate = $scope.toJsDate(row.SOU_DATE);					//收盤價日期
				$scope.inputVO.entrustAmt2 = $filter('number')($scope.inputVO.closingPrice+($scope.inputVO.closingPrice * $scope.inputVO.entrustDiscount / 100),6);
												
				$scope.getFeeTypeData();
				
				$scope.unitNum1Disable = false;
				//賣出價格指示
				if ($scope.inputVO.countryCode == "USA") { // 美股：約定限價/限價；有效市場日期大於市場交易日期時僅限價
					
				} else if ($scope.inputVO.countryCode == "HKG") { // 港股：限價
					var nowTime = $filter("date")(new Date(), "HHmm");
					if (nowTime >= "0900" && nowTime <= "1230") {
//						$scope.unitNum1Disable = true;
					} else {
						$scope.unitNum1Disable = true;
						$scope.inputVO.unitNum1 = undefined;
					}
				} else if ($scope.inputVO.countryCode == "JPN" || $scope.inputVO.countryCode == "LUX") { // 日股/盧森堡：市價
					
				} else if ($scope.inputVO.countryCode == "GRC") { // 希臘：限價/市價
					
				} 
			}
		}
		
		//金錢信託取得庫存
		$scope.inputProdDTL_Mon = function (row) {
			$scope.inputVO.countryCode = row.COUNTRY_CODE;
			$scope.quoteProd="a="+$scope.inputVO.prodID+"&Market="+$scope.inputVO.countryCode;
			//a=商品代碼&market=商品國籍
			$scope.inputVO.pType = row.PTYPE;					//交易市場別
			$scope.inputVO.stockCode = row.STOCK_CODE;			//交易所代號
			$scope.inputVO.marketPrice = row.MARKET_PRICE;		//市價
			debugger
			//由電文or DB 傳送
			if($scope.connector('get', 'SOTProd')){
//				$scope.inputVO.closingPrice = $scope.connector('get', 'SOTProd').CurAmt;		//收盤價
				$scope.inputVO.prodName = $scope.connector('get', 'SOTProd').ProdName;			//商品名稱
				$scope.inputVO.prodCurr = $scope.connector('get', 'SOTProd').Currency;			//計價幣別
//				$scope.inputVO.trustAcct = $scope.connector('get', 'SOTProd').TrustAcct;		//信託帳號
				$scope.inputVO.number = $scope.connector('get', 'SOTProd').StockNum;			//庫存股數  存入db欄位 TRUST_NUM
				$scope.inputVO.avlStockNum = $scope.connector('get', 'SOTProd').AvlStockNum;	//可贖股數 
				$scope.inputVO.entrustCur = $scope.connector('get', 'SOTProd').Currency;		//交易幣別
//				$scope.inputVO.refPrice = $scope.connector('get', 'SOTProd').Currency;			//外幣參考市值
//				$scope.inputVO.isNewProd = $scope.connector('get', 'SOTProd').ProductType2=="Y"?"N":"Y";			//是否為新商品
//				$scope.inputVO.trustCurrType = $scope.connector('get', 'SOTProd').TrustBusinessType;	//信託業務別
				$scope.connector('set', 'SOTProd',undefined);
			}else{
//				$scope.inputVO.closingPrice = row.CUR_AMT;			//收盤價
//				$scope.inputVO.prodName = row.PROD_NAME;			//商品名稱
//				$scope.inputVO.prodCurr = row.CURRENCY_STD_ID;		//計價幣別
//				$scope.inputVO.trustAcct = row.TRUST_ACCT;			//信託帳號
//				$scope.inputVO.number = row.NUMBER;					//庫存股數  存入db欄位 TRUST_NUM
//				$scope.inputVO.entrustCur = row.ENTRUST_CUR;		//交易幣別
//				$scope.inputVO.refPrice = row.FOR_CUR_BAL;			//外幣參考市值
//				$scope.inputVO.isNewProd = row.IS_NEW_PROD;			//是否為新商品
//				$scope.inputVO.trustCurrType = row.TRUST_CURR_TYPE;	//信託業務別
			}
			$scope.inputVO.closingPrice = row.CUR_AMT;			//收盤價
			$scope.inputVO.refPrice = row.FOR_CUR_BAL;			//外幣參考市值
			$scope.inputVO.trustCurrType = "Y";					//信託業務別
			$scope.inputVO.refPrice = ($scope.inputVO.closingPrice * $scope.inputVO.avlStockNum);//外幣參考市值
			
			$scope.inputVO.prodRiskLV = row.RISKCATE_ID;		//產品風險等級
			$scope.inputVO.prodMinBuyUnit = row.TXN_UNIT;		//最低賣出單位
			$scope.inputVO.prodMinGrdUnit = row.TRADING_UNIT;	//最低累進單位
			
			$scope.inputVO.unitNum1 = $scope.inputVO.number % $scope.inputVO.prodMinBuyUnit;
			$scope.inputVO.entrustAmt = $filter('number')($scope.inputVO.closingPrice * 0.9,6);	//約定限價
			$scope.inputVO.closingPriceDate = $scope.toJsDate(row.SOU_DATE);					//收盤價日期
			$scope.inputVO.entrustAmt2 = $filter('number')($scope.inputVO.closingPrice+($scope.inputVO.closingPrice * $scope.inputVO.entrustDiscount / 100),6);
											
			$scope.getFeeTypeData();
			
			$scope.unitNum1Disable = false;
			//賣出價格指示
			if ($scope.inputVO.countryCode == "USA") { // 美股：約定限價/限價；有效市場日期大於市場交易日期時僅限價
				
			} else if ($scope.inputVO.countryCode == "HKG") { // 港股：限價
				var nowTime = $filter("date")(new Date(), "HHmm");
				if (nowTime >= "0900" && nowTime <= "1230") {
//					$scope.unitNum1Disable = true;
				} else {
					$scope.unitNum1Disable = true;
					$scope.inputVO.unitNum1 = undefined;
				}
			} else if ($scope.inputVO.countryCode == "JPN" || $scope.inputVO.countryCode == "LUX") { // 日股/盧森堡：市價
				
			} else if ($scope.inputVO.countryCode == "GRC") { // 希臘：限價/市價
				
			}
		}
		
		//取得贖回入帳帳號；用扣款帳號取得台幣或外幣的帳號
		$scope.getCreditAcctList = function(trustCurrType) {
			var creditList = [];
			var debitAcctList = [];
//			debugger
			if(trustCurrType == 'Y') {	//外幣信託
				debitAcctList = $filter('filter')($scope.mappingSet['SOT.INCLUDING_DIGIT_DEBIT_ACCT_LIST'],{CURRENCY: "TWD"},function(actual, expected) { return !angular.equals(actual, expected)});
			} else {	//台幣信託 OR 國內信託
				debitAcctList = $filter('filter')($scope.mappingSet['SOT.INCLUDING_DIGIT_DEBIT_ACCT_LIST'], {CURRENCY: 'TWD'});
			}
			
			angular.forEach(debitAcctList, function(row) {
//				debugger
				var rowData = $filter('filter')(creditList, {DATA: row.DATA});
				if(rowData.length == 0 && row.DATA != "") {
					creditList.push({LABEL:row.DATA, DATA:row.DATA});
				}
			});
			
			return creditList;
		}
		
		//檢核股數
		$scope.checkUnitNum = function() {
			debugger
			$scope.inputVO.entrustAmt =$scope.moneyUnFormat($scope.inputVO.entrustAmt);
			$scope.inputVO.entrustAmt1=$scope.moneyUnFormat($scope.inputVO.entrustAmt1);
			if ($scope.inputVO.sellType == "1") {
				if ($scope.inputVO.unitNum != "" && $scope.inputVO.unitNum > 0 ) {
					var list = [$scope.inputVO.prodMinBuyUnit];
					$scope.inputVO.unitNum1 = undefined;
					$scope.priceTemp = 0;
					
					if (($scope.inputVO.unitNum >= $scope.inputVO.prodMinBuyUnit) && 
						(($scope.inputVO.unitNum % $scope.inputVO.prodMinGrdUnit) == 0)) {
						
						switch ($scope.inputVO.entrustType){
							case "1":
								$scope.priceTemp = $filter('number')($scope.inputVO.closingPrice * 0.9,6);
								break;
							case "2":
								$scope.priceTemp = $scope.inputVO.entrustAmt;
								break;
							case "3":
								$scope.priceTemp = $scope.inputVO.entrustAmt1;
								break;
							case "4":
								if($scope.inputVO.entrustDiscount<0) $scope.inputVO.entrustDiscount=0;
								else if($scope.inputVO.entrustDiscount > 5) $scope.inputVO.entrustDiscount=5;
								$scope.inputVO.entrustAmt2 = $filter('number')($scope.inputVO.closingPrice+($scope.inputVO.closingPrice * $scope.inputVO.entrustDiscount / 100),6);
								$scope.inputVO.entrustAmt2=$scope.moneyUnFormat($scope.inputVO.entrustAmt2);
								$scope.priceTemp = $scope.inputVO.entrustAmt2;
								break;
						}
	
						if ($scope.priceTemp < $scope.inputVO.prodMinBuyAmt) {//TODO $scope.inputVO.prodMinBuyAmt
							$scope.inputVO.entrustAmt = $filter('number')($scope.inputVO.closingPrice * 0.9,6);
							$scope.inputVO.entrustAmt1 = undefined;
							$scope.inputVO.entrustDiscount = 5;
							$scope.inputVO.entrustAmt2 = $filter('number')($scope.inputVO.closingPrice+($scope.inputVO.closingPrice * $scope.inputVO.entrustDiscount / 100),6);
							
							$scope.showErrorMsg("ehl_01_sot220_001", list);
							$scope.inputVO.entrustAmt =$scope.moneyFormat($scope.inputVO.entrustAmt);
							$scope.inputVO.entrustAmt1=$scope.moneyFormat($scope.inputVO.entrustAmt1);
							return;
						}
						
						$scope.getBestFeeRate();
					} else {
						$scope.inputVO.entrustAmt = $filter('number')($scope.inputVO.closingPrice * 0.9,6);
						$scope.inputVO.entrustAmt1 = undefined;
						$scope.inputVO.entrustDiscount = 5;
						$scope.inputVO.entrustAmt2 = $filter('number')($scope.inputVO.closingPrice+($scope.inputVO.closingPrice * $scope.inputVO.entrustDiscount / 100),6);
						
						$scope.showErrorMsg("ehl_01_sot220_002", list);
						$scope.inputVO.entrustAmt =$scope.moneyFormat($scope.inputVO.entrustAmt);
						$scope.inputVO.entrustAmt1=$scope.moneyFormat($scope.inputVO.entrustAmt1);
						return;
					}
				} else {
					if($scope.inputVO.unitNum != undefined && $scope.inputVO.unitNum != "") {
						$scope.showErrorMsg("ehl_01_sot220_002");
						if($scope.inputVO.unitNum <= 0)
							$scope.inputVO.unitNum = "";
					}
				}
			} else {
				if ($scope.inputVO.unitNum1 != "" && $scope.inputVO.unitNum1 > 0 ) {
					var list = [($scope.inputVO.number % $scope.inputVO.prodMinBuyUnit)];
					$scope.inputVO.unitNum = undefined;
					var number = ($scope.inputVO.number % $scope.inputVO.prodMinBuyUnit);
					if($scope.inputVO.unitNum1 > ($scope.inputVO.number % $scope.inputVO.prodMinBuyUnit)){
						$scope.showErrorMsg("不可填寫大於畸零股數的數字");
						$scope.inputVO.unitNum1=undefined;
						return;
					}
					if ($scope.inputVO.unitNum1 > number) {
						$scope.inputVO.entrustAmt = $filter('number')($scope.inputVO.closingPrice * 0.9,6);
						$scope.inputVO.entrustAmt1 = undefined;
						$scope.inputVO.entrustDiscount = 5;
						$scope.inputVO.entrustAmt2 = $filter('number')($scope.inputVO.closingPrice+($scope.inputVO.closingPrice * $scope.inputVO.entrustDiscount / 100),6);
						
						$scope.showErrorMsg("ehl_01_sot220_001", list);
						$scope.inputVO.unitNum1 = $scope.inputVO.number % $scope.inputVO.prodMinBuyUnit;
						$scope.inputVO.entrustAmt =$scope.moneyFormat($scope.inputVO.entrustAmt);
						$scope.inputVO.entrustAmt1=$scope.moneyFormat($scope.inputVO.entrustAmt1);
						return;
					} 
					
					if($scope.inputVO.entrustType == "4") {
						if($scope.inputVO.entrustDiscount<0) $scope.inputVO.entrustDiscount=0;
						else if($scope.inputVO.entrustDiscount > 5) $scope.inputVO.entrustDiscount=5;
						$scope.inputVO.entrustAmt2 = $filter('number')($scope.inputVO.closingPrice+($scope.inputVO.closingPrice * $scope.inputVO.entrustDiscount / 100),6);
					}
					
					$scope.getBestFeeRate();
				}
			}
			$scope.inputVO.entrustAmt = $filter('number')($scope.inputVO.entrustAmt,6);
			$scope.inputVO.entrustAmt1 = $filter('number')($scope.inputVO.entrustAmt1,6);
			$scope.getCurrency($scope.inputVO.prodCurr); //幣別取小數轉換
		};
		
		$scope.defalutUnitNum = function () {
			if (typeof($scope.inputVO.unitNum1) === 'undefined') {
				$scope.inputVO.unitNum1 = $scope.inputVO.number % $scope.inputVO.prodMinBuyUnit;
			}
						
			$scope.getFee('rate');
		}
		
		$scope.checkEntrustType = function () {
			$scope.inputVO.entrustType = '';
			if($scope.inputVO.stockCode && ($scope.inputVO.stockCode=='HKSE' || $scope.inputVO.stockCode=='TSE')){
				if($scope.inputVO.sellType == "1")
					$scope.showentrustType=[true,true,false,true];
				else if($scope.inputVO.sellType == "2")
					$scope.showentrustType=[false,true,true,true];
			}
		}
		
		$scope.noCallCustQuery = function () {
			var deferred = $q.defer();
			$scope.sendRecv("SOT220", "query", "com.systex.jbranch.app.server.fps.sot220.SOT220InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
//							$scope.getTradeDueDate();
							if ($scope.inputVO.trustTS != 'M') {
								$scope.mappingSet['SOT.CREDIT_ACCT_LIST']=[];
							}
							$scope.cartList = tota[0].body.carList;
							$scope.outputVO = tota[0].body;
							if (tota[0].body.mainList.length > 0) {
								$scope.inputVO.custID = tota[0].body.mainList[0].CUST_ID;				//客戶ID
								$scope.inputVO.trustTS = tota[0].body.mainList[0].TRUST_TRADE_TYPE;		//信託交易類別 S:特金 M:金錢信託
							}
							
							if ($scope.inputVO.carSEQ) {
								angular.forEach($scope.cartList, function(row){
									if ($scope.inputVO.carSEQ == row.SEQ_NO) {
										$scope.inputVO.creditAcct=row.CREDIT_ACCT;
										$scope.inputVO.trustAcct =row.TRUST_ACCT;
										if ($scope.inputVO.trustTS != 'M') {
											$scope.mappingSet['SOT.CREDIT_ACCT_LIST'].push({LABEL:$scope.inputVO.creditAcct ,DATA:$scope.inputVO.creditAcct});
											if ($scope.inputVO.trustAcct && sotService.is168($scope.inputVO.trustAcct)){
												$scope.creditAcctDisabled = true;
											}else{
												$scope.creditAcctDisabled = false;
											}	
										}
										$scope.inputVO.prodID = row.PROD_ID;
							        	$scope.inputVO.prodName = row.PROD_NAME;									//商品名稱
							        	$scope.inputVO.prodCurr = row.PROD_CURR; 									//計價幣別
							        	$scope.inputVO.prodRiskLV = row.PROD_RISK_LV; 								//產品風險等級
							        	$scope.inputVO.sellType = row.SELL_TYPE;
							        	if (row.SELL_TYPE == "1") {
							        		$scope.inputVO.unitNum = row.UNIT_NUM;									//股數
							        	} else {
							        		$scope.inputVO.unitNum1 = row.UNIT_NUM;									//股數
							        	}
							        	$scope.inputVO.number = row.TRUST_NUM;
							        	$scope.inputVO.countryCode = row.TRADE_MARKET;
							        	$scope.inputVO.prodMinBuyUnit = row.PROD_MIN_BUY_UNIT;						//最低買進單位
							        	$scope.inputVO.prodMinGrdUnit = row.PROD_MIN_GRD_UNIT;						//最低累進單位
							        	$scope.inputVO.dueDateShow = row.DUE_DATE_SHOW;
							        	var array = ($scope.inputVO.dueDateShow).split(',');
							        	$scope.dueDate = [];
							        	angular.forEach(array, function(value, key) {
							        		if(value){
							        			$scope.dueDate.push({LABEL: value + " 00:00:00", DATA: value + " 00:00:00"});
							        		}
							        		
						    			});

							        	$scope.inputVO.dueDate = row.DUE_DATE;
										$scope.inputVO.entrustType = row.ENTRUST_TYPE;								//買進價格指示
//										$scope.checkShowDueDate();
										
										$scope.inputVO.refPrice = row.REF_PRICE;
										$scope.inputVO.isNewProd = row.IS_NEW_PROD;
										$scope.inputVO.closingPrice = row.CLOSING_PRICE;							//限價-收盤價
										$scope.inputVO.entrustAmt = $filter('number')($scope.inputVO.closingPrice * 0.9,6);
										$scope.inputVO.entrustAmt1 = undefined;
										$scope.inputVO.entrustDiscount = 5;
										$scope.inputVO.entrustAmt2 = $filter('number')($scope.inputVO.closingPrice+($scope.inputVO.closingPrice * $scope.inputVO.entrustDiscount / 100),6);
										switch ($scope.inputVO.entrustType){
											case "1":
												break;
											case "2":
												$scope.inputVO.entrustAmt = $filter('number')(row.ENTRUST_AMT,6);
												break;
											case "3":
												$scope.inputVO.entrustAmt1 = $filter('number')(row.ENTRUST_AMT,6);
												break;
											case "4":
												$scope.inputVO.entrustAmt2 = row.ENTRUST_AMT;
												$scope.inputVO.entrustDiscount = row.ENTRUST_DISCOUNT;
												break;
										}
										$scope.inputVO.trustCurrType = row.TRUST_CURR_TYPE
										$scope.inputVO.closingPriceDate = $scope.toJsDate(row.CLOSING_PRICE_DATE);	//限價-日期
										
										$scope.inputVO.defaultFeeRate = row.DEFAULT_FEE_RATE; 						//表定手續費率
										$scope.inputVO.feeRateType = row.FEE_TYPE; 									//手續費優惠方式
										
										if (row.FEE_TYPE == 'D') {
											$scope.feeTypeIndex = row.BARGAIN_APPLY_SEQ; 							//手續費優惠方式(下拉選單用)
										} else {
											$scope.feeTypeIndex = row.FEE_TYPE;										//手續費優惠方式(下拉選單用)
										}										
										
										$scope.inputVO.feeRate = row.FEE_RATE; 										//手續費率
										$scope.inputVO.fee =$scope.moneyFormat(row.FEE); 							//手續費金額
										$scope.inputVO.feeDiscount = row.FEE_DISCOUNT; 								//折數
										
										$scope.inputVO.narratorID=$scope.cartList[0].NARRATOR_ID;                    //解說人員ID
										$scope.inputVO.narratorName=$scope.cartList[0].NARRATOR_NAME;				//解說人員姓名
									}
				    			});
							}
							if($scope.inputVO.entrustAmt) $scope.inputVO.entrustAmt = $filter('number')($scope.inputVO.entrustAmt,6);

							if($scope.inputVO.entrustAmt1) $scope.inputVO.entrustAmt1 = $filter('number')($scope.inputVO.entrustAmt1,6);

							deferred.resolve("success");
						}
			});
			
			return deferred.promise;
		};
		
		$scope.getBestFeeRate = function() {
			debugger
			if ($scope.inputVO.prodID && $scope.inputVO.trustAcct) {
				$scope.sendRecv("SOT710", "getDefaultFeeRate", "com.systex.jbranch.app.server.fps.sot710.SOT710InputVO", {custID: $scope.inputVO.custID, prodId : $scope.inputVO.prodID, 
																														  trustAcct: $scope.inputVO.trustAcct, 
																														  trustCurrType: $scope.inputVO.trustCurrType},
						  function(tota1, isError) {
							  if (!isError) {
								  if(tota1[0].body.errorCode != null && tota1[0].body.errorCode != "") {
									  $scope.showErrorMsg(tota1[0].body.errorCode + ":" + tota1[0].body.errorTxt);
									  
									  $scope.inputVO.prodID = "";
									  $scope.prodClear();
								  } else {
									  $scope.inputVO.defaultFeeRate = tota1[0].body.defaultFeeRate;
								  }
								  return;
							  }
				});
			}
			
			var entrustAmt = undefined;
			if ($scope.inputVO.entrustType == '1') {
				entrustAmt = $filter('number')($scope.inputVO.closingPrice * 0.9,6);
			} else if ($scope.inputVO.entrustType == '2') {
				entrustAmt = $scope.inputVO.entrustAmt;
			} else if ($scope.inputVO.entrustType == '3') {
				entrustAmt = $scope.inputVO.entrustAmt1;
			} else if ($scope.inputVO.entrustType == '4') {
				entrustAmt = $scope.inputVO.entrustAmt2;
			}
						
			var unitNum = ($scope.inputVO.sellType == "1" ? $scope.inputVO.unitNum : $scope.inputVO.unitNum1);
			if (entrustAmt != '' && typeof(entrustAmt) !== 'undefined' && typeof(unitNum) !== 'undefined' && typeof($scope.inputVO.defaultFeeRate) !== 'undefined') {
				$scope.sendRecv("SOT710", "getBestFeeRate", "com.systex.jbranch.app.server.fps.sot710.SOT710InputVO", {custID: $scope.inputVO.custID,
																													   trustAcct: $scope.inputVO.trustAcct,
																													   buySell: 'S',
																													   prodId: $scope.inputVO.prodID,
																													   unitNum: unitNum,
																													   entrustAmt: $scope.moneyUnFormat(entrustAmt),
																													   dueDate: $scope.toJsDate($scope.dueDate[0].DATA),
																													   defaultFeeRate: $scope.inputVO.defaultFeeRate,
																													   trustCurrType: $scope.inputVO.trustCurrType,
																													   trustTS: $scope.inputVO.trustTS},
					   function(tota, isError) {
						   if (!isError) {
							   var map = $filter('filter')($scope.mappingSet['FEE_RATE_TYPE'], {DATA: ""});
							   if(map != undefined) {
								   map[0].FEE_RATE = tota[0].body.bestFeeRate;
								   if($scope.inputVO.feeRateType == '' || $scope.inputVO.feeRateType == null || $scope.inputVO.feeRateType == "C") {
									   $scope.inputVO.feeRate = tota[0].body.bestFeeRate;
								   }
							   }
							   $scope.getFee('rate');
							   return;
						   }
				});
			}
			
			$scope.getFee('rate');
		};
			
		$scope.getFeeType = function () {
			//先清空
			$scope.inputVO.bargainApplySEQ = "";
			
			var feeTypeIndex = $scope.feeTypeIndex;
			$scope.inputVO.feeRateType = $scope.feeTypeIndex;
			if(feeTypeIndex != 'A' && $scope.mappingSet['FEE_RATE_TYPE'] != undefined){
				if(feeTypeIndex == 'C')
					$scope.inputVO.feeRate = Number(($filter('filter')($scope.mappingSet['FEE_RATE_TYPE'], {DATA: ""}))[0].FEE_RATE);
				else
					$scope.inputVO.feeRate = Number(($filter('filter')($scope.mappingSet['FEE_RATE_TYPE'], {DATA: feeTypeIndex}))[0].FEE_RATE);
				$scope.getFee('rate');
				
				if(feeTypeIndex != 'C' && feeTypeIndex != '' && feeTypeIndex != null) {//事先議價
					$scope.inputVO.feeRateType = "D";
					$scope.inputVO.bargainApplySEQ = feeTypeIndex;
				}
			} else {
				//申請議價
				$scope.inputVO.feeRate = null;
				$scope.inputVO.feeDiscount = null;
				$scope.inputVO.fee = null;
			}	
		}
				
		$scope.query = function(row) {
			$scope.cartList = [];
			// if data
			//判斷若有tradeSEQ則呼叫query method
			//沒有則判斷是否有custID and prodID
			if($scope.connector('get','SOTCustID')){
				$scope.inputVO.custID=$scope.connector('get','SOTCustID');
				$scope.connector('set','SOTCustID',null);
				$scope.getSOTCustInfo().then(function(data){
					if($scope.connector('get', 'SOTProd')){
						$scope.inputVO.prodID = $scope.connector('get', 'SOTProd').InsuranceNo?$scope.connector('get', 'SOTProd').InsuranceNo.trim():null;
						$scope.getProdDTL();
					}
				});
			}else if($scope.connector('get', 'SOTTradeSEQ')){
				$scope.inputVO.tradeSEQ = $scope.connector('get', 'SOTTradeSEQ');
				$scope.inputVO.carSEQ = $scope.connector('get', 'SOTCarSEQ');
				$scope.connector('set', 'SOTTradeSEQ', null);
				$scope.connector('set', 'SOTCarSEQ', null);
			}
			else if (row) {
				$scope.inputVO.carSEQ = row.SEQ_NO;
			} else if ($scope.fromFPS){
				console.log($scope.FPSData);
				$scope.inputVO.custID = $scope.FPSData.custID;//客戶ID
				$scope.getSOTCustInfo().then(function(data) {
				});
			}
			
			if($scope.inputVO.tradeSEQ) {
				$scope.noCallCustQuery().then(function(data){
					$scope.getSOTCustInfo(true).then(function(data) {
						if ($scope.inputVO.carSEQ) {
							$scope.getProdDTL(true);
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
			
//			if($scope.connector('get','ORG110_prod')){
//			var prod =$scope.connector('get','ORG110_prod');
//			if(prod && $scope.connector('get','ORG110_custID')){
//				$scope.inputVO.custID = $scope.connector('get','ORG110_custID');
//				$scope.connector('set', 'SOT222',prod);
//				$scope.inputVO.prodID = prod.InsuranceNo.trim();
//				$scope.getSOTCustInfo().then(function(data){
//					$scope.getProdDTL();
//				});
//			}
//			$scope.connector('set','ORG110_custID','');
//			$scope.connector('set','ORG110_prod','')
//		}
		//贖回有custid時
//		else if($scope.connector('get','ORG110_custID')){
//			$scope.inputVO.custID=$scope.connector('get','ORG110_custID');
//			$scope.connector('set','ORG110_custID','');
//			$scope.getSOTCustInfo();
//		}
//		else{
//			//風控等等頁面導入
//			$scope.inputVO.custID=$scope.connector('get','SOT220_custID');
//			$scope.inputVO.tradeSEQ = $scope.connector('get', 'SOT220_tradeSEQ');
//			$scope.inputVO.carSEQ = $scope.connector('get', 'SOT220_carSEQ');
//			$scope.inputVO.prodID = $scope.connector('get', 'SOT220_prodID');
//			$scope.connector('set', 'SOT220_tradeSEQ', null);
//			$scope.connector('set', 'SOT220_carSEQ', null);
//			$scope.connector('set', 'SOT220_prodID', null);
//			$scope.connector('set', 'SOT220_custID', null);
//		}
		
		};
		
		$scope.refresh = function (row) {
			if (row) {
				$scope.inputVO.carSEQ = row.SEQ_NO;
			}
			$scope.noCallCustQuery();
		};
		
		$scope.addCar = function() {
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg("ehl_01_common_022");
	    		return;
	    	}
			if ($scope.cartList.length == 6) {
				$scope.showErrorMsg('ehl_01_SOT_007');
				return;
			}
			
			var entrustType = $scope.inputVO.entrustType;
			if (entrustType == "") {
				$scope.showErrorMsg("ehl_01_common_022");
				return;
			} else {
				if (entrustType == "2") {
					if ($scope.inputVO.entrustAmt == "") {
						$scope.showErrorMsg("ehl_01_common_022");
						return;
					}
				} else if (entrustType == "3") {
					if ($scope.inputVO.entrustAmt1 == "") {
						$scope.showErrorMsg("ehl_01_common_022");
						return;
					}
				}
			}
			$scope.inputVO.entrustAmt =Number($scope.moneyUnFormat($scope.inputVO.entrustAmt));
			$scope.inputVO.entrustAmt1=Number($scope.moneyUnFormat($scope.inputVO.entrustAmt1));
			$scope.inputVO.entrustAmt2=Number($scope.moneyUnFormat($scope.inputVO.entrustAmt2));
			$scope.inputVO.fee=Number($scope.moneyUnFormat($scope.inputVO.fee));
			
			
//			alert(JSON.stringify($scope.inputVO));
			//檢核賣出股數與畸零股數必須擇一輸入
			var checkUnit = $scope.inputVO.unitNum || $scope.inputVO.unitNum1;
			if (!checkUnit) {
				$scope.showErrorMsg("ehl_01_common_022");
				return;
			}
			
			$scope.sendRecv("SOT220", "save", "com.systex.jbranch.app.server.fps.sot220.SOT220InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.errorMsg != null) {
								$scope.showErrorMsg(tota[0].body.errorMsg);
								
								return;
							} else {
								$scope.inputVO.prodID = "";
								$scope.prodClear();
								$scope.refresh();
								
								return;
							}
						}else{
							$scope.inputVO.entrustAmt =$scope.moneyFormat($scope.inputVO.entrustAmt);
							$scope.inputVO.entrustAmt1=$scope.moneyFormat($scope.inputVO.entrustAmt1);
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
				$scope.sendRecv("SOT220", "delProd", "com.systex.jbranch.app.server.fps.sot220.SOT220InputVO", {tradeSEQ : $scope.inputVO.tradeSEQ, carSEQ: row.SEQ_NO},
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
			$scope.sendRecv("SOT220", "next", "com.systex.jbranch.app.server.fps.sot220.SOT220InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.connector('set','SOTTradeSEQ', $scope.inputVO.tradeSEQ);
							$scope.connector('set','SOTCarSEQ', null);
							if ($scope.inputVO.trustTS != 'M') {
								if ($scope.fromFPS) {
									// from FPS_SOT.js
									$scope.setSOTurl('assets/txn/SOT221/SOT221.html');
								} else {
									$rootScope.menuItemInfo.url = "assets/txn/SOT221/SOT221.html";
								}
							} else {
								$rootScope.menuItemInfo.url = "assets/txn/SOT224/SOT224.html";
							}
							
							return;
						}
			});
		};
		
		$scope.init();
		$scope.query(); 
		
		
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
});