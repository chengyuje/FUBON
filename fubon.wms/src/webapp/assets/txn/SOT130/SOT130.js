/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT130Controller',
	function($rootScope, $scope, $controller, $confirm, sysInfoService ,socketService, ngDialog, projInfoService,$filter, $q, getParameter, validateService, sotService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT130Controller";
		//繼承PRD100高齡檢核
		$controller('PRD100Controller', {$scope: $scope});
		
		// mapping
		$scope.getXML = ['SOT.TRUST_CURR_TYPE','SOT.CUST_TYPE','FPS.CURRENCY','FPS.PROD_RISK_LEVEL','SOT.REDEEM_TYPE','COMMON.YES_NO',
		                 'SOT.TRADE_DATE_TYPE','SOT.EBANK_PRTDOC_URL','SOT.CHG_PRTDOC_URL','SOT.ASSET_TRADE_SUB_TYPE',
		                 'SOT.RESERVE_TRADE_DAYS','SOT.PROSPECTUS_TYPE',"SOT.SPEC_CUSTOMER","SOT.FUND_DECIMAL_POINT"];
		 getParameter.XML($scope.getXML, function(totas) {
			 if(len(totas)>0){
				 var tmp = totas;
				 $scope.mappingSet['SOT.TRUST_CURR_TYPE'] = tmp.data[tmp.key.indexOf('SOT.TRUST_CURR_TYPE')];
				 $scope.mappingSet['SOT.CUST_TYPE'] = tmp.data[tmp.key.indexOf('SOT.CUST_TYPE')];
				 $scope.mappingSet['FPS.CURRENCY']  = tmp.data[tmp.key.indexOf('FPS.CURRENCY')];
				 $scope.mappingSet['FPS.PROD_RISK_LEVEL']  = tmp.data[tmp.key.indexOf('FPS.PROD_RISK_LEVEL')];
				 $scope.mappingSet['SOT.REDEEM_TYPE']  = tmp.data[tmp.key.indexOf('SOT.REDEEM_TYPE')];
				 $scope.mappingSet['COMMON.YES_NO']  = tmp.data[tmp.key.indexOf('COMMON.YES_NO')];
				 $scope.mappingSet['SOT.TRADE_DATE_TYPE']  = tmp.data[tmp.key.indexOf('SOT.TRADE_DATE_TYPE')];
				 $scope.mappingSet['SOT.EBANK_PRTDOC_URL']  =  tmp.data[tmp.key.indexOf('SOT.EBANK_PRTDOC_URL')];
				 $scope.mappingSet['SOT.CHG_PRTDOC_URL']  = tmp.data[tmp.key.indexOf('SOT.CHG_PRTDOC_URL')];
				 $scope.mappingSet['SOT.ASSET_TRADE_SUB_TYPE']  = tmp.data[tmp.key.indexOf('SOT.ASSET_TRADE_SUB_TYPE')];
				//公開說明書選項
				$scope.mappingSet['SOT.PROSPECTUS_TYPE'] = totas.data[totas.key.indexOf('SOT.PROSPECTUS_TYPE')];
				$scope.mappingSet['SOT.SPEC_CUSTOMER'] = totas.data[totas.key.indexOf('SOT.SPEC_CUSTOMER')];//客戶註記
				$scope.mappingSet['SOT.FUND_DECIMAL_POINT'] = totas.data[totas.key.indexOf('SOT.FUND_DECIMAL_POINT')]; // 基金幣別小數位
				 //Nday 預約 營業日參數
				 $scope.mappingSet['SOT.RESERVE_TRADE_DAYS'] = tmp.data[tmp.key.indexOf('SOT.RESERVE_TRADE_DAYS')];
				 angular.forEach($scope.mappingSet['SOT.RESERVE_TRADE_DAYS'], function(row) {
			    		if(row.DATA=='NF') {
			    			$scope.reserveTradeDay = row.LABEL;
			    		}
				 });
			 }
		 });

		//取得小數點位數
			$scope.getCurrency = function(){
				if($scope.mod != undefined)
					$scope.num = $scope.mod[0].LABEL;
				// 信託金額
				$scope.inputVO.rdmProdAmt = !isNaN($scope.inputVO.rdmProdAmt)  ? Number($scope.inputVO.rdmProdAmt).toFixed($scope.num)  : $scope.inputVO.rdmProdAmt;
				// 參考現值
				$scope.inputVO.present_val= !isNaN($scope.inputVO.present_val) ? Number($scope.inputVO.present_val).toFixed($scope.num) : $scope.inputVO.present_val;
				// 手續費金額
				$scope.inputVO.feePrice= !isNaN($scope.inputVO.feePrice) ? Number($scope.inputVO.feePrice).toFixed($scope.num) : $scope.inputVO.feePrice;
			};

        $scope.clearProd = function(){
        	$scope.inputVO.rdmProdID         ='';
        	$scope.inputVO.rdmProdName       ='';
        	$scope.inputVO.rdmProdCurr       ='';
        	$scope.inputVO.rdmProdRiskLV     ='';
        	$scope.inputVO.rdmTradeType      ='';
			$scope.inputVO.rdmTradeTypeD='';
        	$scope.inputVO.rdmCertificateID  ='';
        	$scope.inputVO.rdmCurCode		 ='';
        	$scope.inputVO.rdmUnit           = undefined;
        	$scope.inputVO.redeemType        ='';
        	$scope.inputVO.numUnits          ='';
        	$scope.inputVO.rCreditAcctList   ='';
        	$scope.inputVO.isEndCertificate  =null;
        	$scope.inputVO.isRePurchase      ='';
        	$scope.inputVO.tradeDateType     ='';
        	$scope.inputVO.tradeDate         ='';
        	$scope.inputVO.trustCurrType  	 ='';
        	$scope.inputVO.pchProdID         ='';
        	$scope.inputVO.pchProdName       ='';
        	$scope.inputVO.pchProdCurr       ='';
        	$scope.inputVO.pchProdRiskLV     ='';
        	$scope.inputVO.defaultFeeRate    ='';
        	$scope.inputVO.feeRateType       ='';
        	$scope.inputVO.feeRate           ='';
        	$scope.inputVO.feePrice          ='';
        	$scope.inputVO.feeDiscount       ='';
			$scope.inputVO.present_val 	 	 ='';
			$scope.inputVO.rdmProdAmt 	  	 = undefined;
			$scope.inputVO.prodFus40	  	 ='';	 //是否未核備
			$scope.inputVO.prodFus07 		 ='';	 //2017-09-11 取基金-小數點位數
			$scope.inputVO.prospectusType    ='';    //公開說明書
			$scope.inputVO.ovsPrivateYN		 ='N';
			$scope.inputVO.ovsPrivateSeq	 = undefined;
			$scope.isEndCertificate_required = false;
		    $("#tradeDateType1").attr("disabled", false);
		};

        $scope.clearPchProd = function(){
        	if ($scope.inputVO.isRePurchase == "Y" && $scope.inputVO.rejectProdFlag == "Y") {	//拒銷(RS)註記Y 得拒絕申請人臨櫃進行非存款類之理財商品下單(轉換、變更投資標的不在此限)
		    	$scope.showErrorMsg("ehl_01_SOT702_002");
				$scope.inputVO.isRePurchase = "N";
				return;
		    }

			$scope.inputVO.pchProdID      ='';
			$scope.inputVO.pchProdName    ='';
			$scope.inputVO.pchProdRiskLV  ='';
			$scope.inputVO.pchProdCurr    ='';
			$scope.inputVO.pchTrustCurr   ='';
			$scope.inputVO.defaultFeeRate ='';
			$scope.inputVO.feeRateType    ='';
			$scope.inputVO.feeRate		  ='';
			$scope.inputVO.feePrice	  	  ='';
			$scope.inputVO.feeDiscount	  ='';
			$scope.inputVO.brgReason 	  ='下單議價';	//議價原因
//			$scope.inputVO.rdmProdAmt 	  ='';  // 清空	信託金額
			if($scope.inputVO.ovsPrivateYN != "Y") { //非境外私募基金
				$("#tradeDateType1").attr("disabled", false);
			}
		};

        $scope.clearTradeSEQ = function(){
        	$scope.tradeSEQ='';
        	$scope.query();
        }
		// init
        $scope.init = function() {
        	$scope.custDTL = [];
        	$scope.cartList = [];
        	$scope.inputVO = {};
			var custID = $scope.connector('get','ORG110_custID');
			if(custID != undefined){
				$scope.inputVO.custID = custID;
			}
			$scope.inputVO.prodType='1';  //1：基金
        	$scope.inputVO.tradeType = '2'; //2：贖回/贖回再申購
        	$scope.inputVO.seniorAuthType='S'; //高齡評估表授權種類(S:下單、A：適配)
			$scope.inputVO.custType = 'CUST';
			$scope.inputVO.isRePurchase = 'N';
			$scope.inputVO.isEndCertificate = null;
//			$scope.inputVO.main_prd = 'Y';
			$scope.inputVO.narratorID = projInfoService.getUserID();
			$scope.inputVO.narratorName = projInfoService.getUserName();
			$scope.cmbCreditAcct=false;
			$scope.checkKYC = '';
			$scope.isEndCertificate_required = false;
			$scope.inputVO.is_backend = '';
			$scope.inputVO.trustTS = 'S'; //M:金錢信託  S:特金交易--預設特金如果契約編號有值則為SOT112交易為金錢信託
			$scope.inputVO.isOBU = '';									//是否為OBU客戶
			$scope.isDigitAcct = false;
			$scope.inputVO.ovsPrivateYN = "N"; //是否為境外私募基金
			$scope.inputVO.ovsPrivateSeq = undefined;
			$scope.inputVO.hnwcYN = '';
			$scope.inputVO.hnwcServiceYN = '';
		};
		$scope.init();


		// if data
		$scope.tradeSEQ = $scope.connector('get','SOTTradeSEQ');
		$scope.carSEQ = $scope.connector('get','SOTCarSEQ');
		$scope.connector('set', 'SOTTradeSEQ', null); //避免跨交易返回本頁殘值
		$scope.connector('set', 'SOTCarSEQ', null);

		$scope.noCallCustQuery = function () {
			var deferred = $q.defer();
				$scope.sendRecv("SOT130", "query", "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO", {'tradeSEQ': $scope.tradeSEQ,'rdmProdID': $scope.inputVO.rdmProdID},
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.custDTL && tota[0].body.custDTL.length > 0){
									$scope.custDTL = tota[0].body.custDTL;
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
									$scope.inputVO.piRemark = $scope.custDTL[0].PI_REMARK;
									$scope.inputVO.hnwcYN = $scope.custDTL[0].HNWC_YN;
									$scope.inputVO.hnwcServiceYN = $scope.custDTL[0].HNWC_SERVICE_YN;
								}
								$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.inputVO.rdmProdCurr});
								$scope.getCurrency();
								$scope.cartList = tota[0].body.cartList;

								if ($scope.carSEQ) {
									var index = $scope.cartList.map(function(e) { return e.SEQ_NO; }).indexOf($scope.carSEQ);
									if(index != -1){
										$scope.edit($scope.cartList[index]);
									}
								}
								deferred.resolve("success");
							}
				});
				return deferred.promise;
		}

		// 取得新交易序號
		$scope.getTradeSEQ = function() {
			$scope.sendRecv("SOT712", "getTradeSEQ", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {},
					function(tota, isError) {
						if (!isError) {
							$scope.tradeSEQ = tota[0].body.tradeSEQ;
							return;
						}
			});
		};

		$scope.query = function(){
			$scope.custDTL = [];
			if($scope.tradeSEQ) {
				$scope.noCallCustQuery().then(function(data) {
					$scope.getSOTCustCredit();
				});
			} else {
				// 取得新交易序號
				$scope.getTradeSEQ();
			}
		};
		$scope.query();

		// getSOTCustInfo
		$scope.getSOTCustCredit = function() {
			if($scope.inputVO.custID) {
				$scope.sendRecv("SOT701", "getSOTCustInfo", "com.systex.jbranch.app.server.fps.sot701.SOT701InputVO", {'custID':$scope.inputVO.custID, 'prodType':1, 'tradeType':2},
						function(tota, isError) {
							if (!isError) {
								$scope.mappingSet['credit'] = [];
								$scope.mappingSet['debit'] = [];
								var custAcctData = tota[0].body.custAcctDataVO;
								angular.forEach(custAcctData.creditAcctList, function(row){
										$scope.mappingSet['credit'].push({LABEL: row.acctNo,
																		  DATA: row.acctNo,
																		});
				    			});
								angular.forEach(custAcctData.debitAcctList, function(row){
									$scope.mappingSet['debit'].push({LABEL: row.acctNo,
																	  DATA: row.acctNo,
																	});
								});
								$scope.getFeeTypeData();
								return;
							}
				});
				// clear car
//				$scope.custDTL = [];
//	        	$scope.cartList = [];
			}
		};

		// getSOTCustInfo
		$scope.getSOTCustInfo = function() {
			var deferred = $q.defer();
			$scope.inputVO.custID = $filter('uppercase')($scope.inputVO.custID);
			var validCustID = validateService.checkCustID($scope.inputVO.custID); //自然人和法人檢查
			if(validCustID==false){
				$scope.inputVO.custID='';
			}
			var custId=$scope.inputVO.custID;
			if(!$scope.fromFPS){
				$scope.init();
			}
			if(custId) {
				$scope.sendRecv("SOT701", "getSOTCustInfo", "com.systex.jbranch.app.server.fps.sot701.SOT701InputVO", {'custID':custId, 'prodType':1, 'tradeType':2},
						function(tota, isError) {
							if (!isError) {
								$scope.toDay = $filter('date')(new Date(),'yyyy-MM-dd 00:00:00');//取當日日期
								var kycDueDate = $scope.toJsDate(tota[0].body.custKYCDataVO.kycDueDate);
								//FOR CBS TEST日期
//								if ($scope.toJsDate(tota[0].body.custKYCDataVO.kycDueDate) < $scope.toJsDate($scope.toDay)) { //若KYC過期，需有訊息提示客戶。
								if(tota[0].body.custKYCDataVO.isKycDueDateUseful){
									var msgParam = "";
									if (!kycDueDate) {
										msgParam='未承作';
									}else{
										msgParam = kycDueDate.getFullYear() + "/" +(kycDueDate.getMonth() + 1) + "/" + kycDueDate.getDate();
									}
									$scope.checkKYC = $filter('i18n')('ehl_01_sot310_001') + "(" + msgParam + ")";
								}

								//增加到期14天前的警告訊息 add by Brian
								if(kycDueDate){
									$scope.kycDueDateBeforeTwoWeeks = new Date(kycDueDate.setDate(kycDueDate.getDate() - 14)) //kyc到期日前兩周日期
									if($scope.toJsDate($scope.toDay)< $scope.toJsDate(tota[0].body.custKYCDataVO.kycDueDate)){
										if($scope.kycDueDateBeforeTwoWeeks < $scope.toJsDate($scope.toDay)){
											$scope.checkKYC = $filter('i18n')('ehl_01_SOT_014')
										}
									}
								}

								$scope.mappingSet['credit'] = [];
								$scope.mappingSet['debit'] = [];
								var fp032675Data = tota[0].body.fp032675DataVO;
								var custPLData   = tota[0].body.custPLDataVO;
								var custKYCData  = tota[0].body.custKYCDataVO;
								var custAcctData = tota[0].body.custAcctDataVO;
								var w8BenDataVO = tota[0].body.w8BenDataVO;
								$scope.inputVO.custFeePrdDueDate  = tota[0].body.dueDate;
								$scope.inputVO.fatcaType	  = w8BenDataVO.fatcaType;
								$scope.inputVO.custName       = fp032675Data.custName;
								$scope.inputVO.kycLevel       = custKYCData.kycLevel;
								$scope.inputVO.kycDueDate     = custKYCData.kycDueDate;
								$scope.inputVO.custProFlag	  = fp032675Data.custProFlag;
								$scope.inputVO.custRemarks    = fp032675Data.custRemarks;
								$scope.inputVO.outFlag        = fp032675Data.obuFlag;
								$scope.inputVO.noSale         = fp032675Data.noSale;
								$scope.inputVO.custTxFlag     = fp032675Data.custTxFlag;
								$scope.inputVO.custProType    = fp032675Data.custProType;
								$scope.inputVO.profInvestorYN = fp032675Data.custProFlag; 	//2017/10/23 判斷 專業投資人
								$scope.inputVO.piRemark 	  = fp032675Data.custProRemark;	//2017/10/23 專業投資人註記
								$scope.inputVO.rejectProdFlag = fp032675Data.rejectProdFlag;
								$scope.inputVO.isOBU = fp032675Data.obuFlag;								//是否為OBU客戶
								angular.forEach(custAcctData.creditAcctList, function(row){
										$scope.mappingSet['credit'].push({LABEL: row.acctNo,
																		  DATA: row.acctNo,
																		});
				    			});
								angular.forEach(custAcctData.debitAcctList, function(row){
									$scope.mappingSet['debit'].push({LABEL: row.acctNo,
																	  DATA: row.acctNo,
																	});
			    			    });
								$scope.inputVO.takeProfitPerc = custPLData.takeProfitPerc;
								$scope.inputVO.stopLossPerc   = custPLData.stopLossPerc;
								$scope.inputVO.plNotifWays    = custPLData.plMsg;

								$scope.getFeeTypeData();
								deferred.resolve("success");
								return;

							}else{
								$scope.inputVO.custID='';
							}
				});

				$scope.sendRecv("SOT130", "checkBanker", "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO", {'custID':custId},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.isBanker = tota[0].body.isBanker; 						 //是否為行員
								deferred.resolve("success");
								return;
							}
				});
			}
			$scope.inputVO.custID=custId;
			return deferred.promise;
		};

		//取得高資產客戶註記
		$scope.getHNWCData = function() {
			if($scope.inputVO.custID) {
				$scope.sendRecv("SOT714", "getHNWCData", "com.systex.jbranch.app.server.fps.sot714.SOT714InputVO", {'custID':$scope.inputVO.custID},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.hnwcYN = tota[0].body.hnwcDataVO.validHnwcYN;
								$scope.inputVO.hnwcServiceYN = tota[0].body.hnwcDataVO.hnwcService;
							}
				});
			}
		}
		
		// getCheckKYC
		$scope.getCheckKYC = function() {
			if ($scope.inputVO.isRePurchase == 'Y') {
				if($scope.inputVO.is_backend == 'Y'){
					$scope.showErrorMsg('後收型基金不提供再申購功能。');
					$scope.inputVO.isRePurchase = '';
	        		return;
				} else if($scope.inputVO.ovsPrivateYN == "Y") {
					$scope.showErrorMsg('境外私募基金不提供再申購功能。');
					$scope.inputVO.isRePurchase = 'N';
	        		return;
				}
				if ($scope.checkKYC != '') {
					$scope.showErrorMsg($scope.checkKYC);
					$scope.inputVO.isRePurchase = '';
					return;
				}
				if ($scope.inputVO.isOBU == 'Y'){
					$scope.showErrorMsg("ehl_02_sot130_004");
					$scope.inputVO.isRePurchase = '';
					return;
				}
			}
		};

		// setRedeemType
		$scope.setRedeemType = function() {
//			alert($scope.inputVO.is_backend);
			if($scope.inputVO.redeemType == '1') {		//全部贖回
				$scope.inputVO.numUnits = $scope.inputVO.rdmUnit.toFixed(4);
				$scope.inputVO.present_val=$scope.present_val;
				if($scope.redeemStatus){
					$scope.isShowRedeem=true;
					$scope.inputVO.isEndCertificate='';
					return;
				}
			} else {									//部份贖回
				//#0000200: 開放後收型基金部份贖回功能-併入金錢信託需求上線
//				if($scope.inputVO.is_backend == 'Y'){
//					$scope.showErrorMsg('後收型基金僅可全部贖回。');
//					$scope.inputVO.redeemType = '';
//	        		return;
//				}
				$scope.inputVO.numUnits = $scope.inputVO.rdmUnit.toFixed($scope.inputVO.prodFus07);	//2017-09-11 取基金-小數點位數
			}
			$scope.isShowRedeem=false;
			$scope.isEndCertificate_required = false;
			$scope.inputVO.isEndCertificate=null;
			// 切換贖回方式時，計算再申購標的手續費金額
			if($scope.inputVO.isRePurchase == 'Y'){
				$scope.setRedeemValue( 'ref' ,$scope.inputVO.numUnits);
			}
			$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.inputVO.rdmCurCode});
			$scope.getCurrency(); // 調整幣別小數位數
		};

		var roundDecimal = function (val) {
//			  return Math.pow(10, 4);
//			  return Math.round(Math.round(val * Math.pow(10, (4 || 0) + 1)) / 10) / Math.pow(10, (4 || 0));
			}

		$scope.setRedeemValue = function(type,value){
			if(!value || value == 0){
				$scope.inputVO.numUnits='';
				$scope.inputVO.present_val='';
				return;
			}
			switch(type){

				case "unit":
					$scope.inputVO.numUnits = $filter('number')($scope.inputVO.numUnits, $scope.inputVO.prodFus07);//部份贖回,2017-09-11 取基金-小數點位數
					$scope.inputVO.numUnits = $scope.moneyUnFormat($scope.inputVO.numUnits);
					$scope.inputVO.present_val =($scope.inputVO.numUnits * $scope.unitValue).toFixed(4);
					break;
				case "ref":
					if ($scope.inputVO.redeemType == '1') {//全部贖回
						$scope.inputVO.numUnits = ($scope.inputVO.present_val / $scope.unitValue).toFixed(4);
					} else {//部份贖回
						$scope.inputVO.numUnits = ($scope.inputVO.present_val / $scope.unitValue).toFixed($scope.inputVO.prodFus07);	//2017-09-11 取基金-小數點位數
					}
					break;
			}
//			$scope.getDefaultFee($scope.inputVO.trustCurrType);
			$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.inputVO.rdmCurCode});
			$scope.getCurrency();
		};

		$scope.checkTrustAcct = function(){
			 $scope.cmbDebitAcct=false;
			 $scope.cmbCreditAcct=false;
			if ($scope.inputVO.trustAcct && sotService.is168($scope.inputVO.trustAcct)) {
				  $scope.inputVO.rCreditAcctList =  $scope.inputVO.trustAcct;
				  $scope.cmbCreditAcct=true;
			}
		};

		$scope.getTradeDate = function(){
			var tempProdType = '';
        	if($scope.inputVO.trustTS == 'M') {
        		tempProdType = 'MNF';
        	} else {
        		tempProdType = 'NF';
        	}
			$scope.sendRecv("SOT712", "getReserveTradeDate", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {prodType:tempProdType},
					function(tota, isError) {
						if (!isError) {
							$scope.tradeDate =tota[0].body.TradeDate;
							return;
						}
			});
		}
		 $scope.getTradeDate();
		 $scope.getReserveTradeDate = function() {
			 if($scope.inputVO.tradeDateType == '2'){
				$scope.inputVO.tradeDate = $scope.tradeDate;
			 }else if($scope.inputVO.tradeDateType == '1'){
				$scope.inputVO.tradeDate = undefined;
			 }
			//控管國內貨幣型基金交易時間，超過10:30 轉為預約交易狀態
			if($scope.inputVO.pchProdID != null && $scope.inputVO.pchProdID != ''){
				$scope.sendRecv("SOT110", "checkReserve", "com.systex.jbranch.app.server.fps.sot110.SOT110InputVO", {"prodId" : $scope.inputVO.pchProdID},
						function(tota, isError) {
							if (!isError) {
								$scope.rev = tota[0].body.reserve;
								if(!$scope.rev){
										$("#tradeDateType1").attr("disabled", true);
										$("#tradeDateType2").attr("checked", true);
										$scope.inputVO.tradeDateType = '2';
										$scope.inputVO.tradeDate = $scope.tradeDate;
										$scope.showMsg("此筆交易已超過國內貨幣型基金交易時間，將轉為預約交易");
								}
							}
				});

			}
		};

		// getFee
		$scope.getFee = function(type) {
			if(type == 'rate') {
				$scope.inputVO.feePrice = ($scope.inputVO.present_val * $scope.inputVO.feeRate) / 100;
				$scope.inputVO.feeDiscount = $scope.inputVO.feeRate / $scope.inputVO.defaultFeeRate * 10;
			}
			else {
				if($scope.inputVO.feeDiscount) {

					$scope.inputVO.feeRate = $scope.inputVO.feeDiscount * $scope.inputVO.defaultFeeRate / 10;
					$scope.inputVO.feePrice = ($scope.inputVO.present_val * $scope.inputVO.feeRate) / 100;
				}
			}
			$scope.mod_R = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.inputVO.rdmCurCode});
			$scope.num_R = $scope.mod_R[0].LABEL;
		};
		$scope.validFeeRate = function(){
			if($scope.inputVO.feeRate>=$scope.feeTypeData.feeL){
				$scope.inputVO.feePrice =$scope.feeTypeData.feeL;
				$scope.inputVO.defaultFeeRate = $scope.feeTypeData.defaultFeeRateL;
				$scope.inputVO.feeRate = $scope.feeTypeData.feeRateL;
			}
		}

		$scope.countList = function () {
			var tradeSize = 0;
			if ($scope.cartList.length > 0) {
				tradeSize = 1;
				var tradePordTemp = $scope.cartList[0].SEQ_NO;

				angular.forEach($scope.cartList, function(row){
					if (row.SEQ_NO != tradePordTemp) {
						tradeSize++;
						tradePordTemp = row.SEQ_NO;
					}
				});
			}

			return tradeSize;
		}

		$scope.addCar = function() {
//			debugger;
			console.log($scope.parameterTypeEditForm);
			$scope.cartListSave=[];
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			if ($scope.countList() == 6) {
				$scope.showErrorMsg('ehl_01_SOT_007');
				return;
			}
			//#0000201: 業管系統-部分贖回 同一交易序號、同一憑證，僅能執行部分贖回一次，若要執行第二筆放入購物車時，系統應該要出錯誤訊息
			for(var i=0;i<$scope.cartList.length;i++){
				if($scope.cartList[i].RDM_CERTIFICATE_ID == $scope.inputVO.rdmCertificateID){
					$scope.showErrorMsg('ehl_01_SOT703_001');
	    			return;
				}
			}

			//再申購時檢查，拒銷(RS)註記Y 得拒絕申請人臨櫃進行非存款類之理財商品下單(轉換、變更投資標的不在此限)
			if ($scope.inputVO.noSale == "Y" && $scope.inputVO.isRePurchase == 'Y') {
				$scope.showErrorMsg("ehl_01_sot310_003");
				return;
			}

			$scope.narrDis = true;
			// $scope.custDTL
			if($scope.custDTL.length == 0) {
				$scope.custDTL.push({
					'CUST_ID': $scope.inputVO.custID,
					'CUST_NAME': $scope.inputVO.custName,
					'AGENT_ID': $scope.inputVO.proxyCustID,
					'AGENT_NAME': $scope.inputVO.proxyCustName,
					'KYC_LV': $scope.inputVO.kycLevel,
					'KYC_DUE_DATE': $scope.inputVO.kycDueDate,
					'CUST_REMARKS': $scope.inputVO.custRemarks,
					'IS_OBU': $scope.inputVO.outFlag,
					'TX_FLAG':$scope.inputVO.custProFlag,
					'IS_AGREE_PROD_ADV': $scope.inputVO.custTxFlag,//DESC
					'BARGAIN_DUE_DATE': $scope.inputVO.custFeePrdDueDate,
					'PI_REMARK': $scope.inputVO.piRemark,
					'PROF_INVESTOR_YN':$scope.inputVO.profInvestorYN,
					'HNWC_YN':$scope.inputVO.hnwcYN,
					'HNWC_SERVICE_YN':$scope.inputVO.hnwcServiceYN
				});
			}
//			if($scope.cartListSave && $scope.cartListSave.length!=0){
//				$scope.cartListSave=[];
//			}
			$scope.cartListSave.push({
				'SEQ_NO':$scope.inputVO.seq_no,
				'RDM_PROD_ID': $scope.inputVO.rdmProdID,
				'RDM_PROD_NAME': $scope.inputVO.rdmProdName,
				'RDM_PROD_CURR': $scope.inputVO.rdmProdCurr,
				'RDM_PROD_RISK_LV': $scope.inputVO.rdmProdRiskLV,
				'RDM_TRADE_TYPE': $scope.inputVO.rdmTradeType,
				'RDM_TRADE_TYPE_D': $scope.inputVO.rdmTradeTypeD,
				'RDM_CERTIFICATE_ID': $scope.inputVO.rdmCertificateID,
				'RDM_TRUST_CURR_TYPE': $scope.inputVO.trustCurrType,
				'RDM_TRUST_CURR': $scope.inputVO.rdmCurCode,
				'RDM_UNIT': $scope.inputVO.rdmUnit,
				'RDM_TRUST_AMT': $scope.inputVO.rdmProdAmt,
				'REDEEM_TYPE': $scope.inputVO.redeemType.toString(),
				'UNIT_NUM': $scope.inputVO.numUnits,
				'CREDIT_ACCT': $scope.inputVO.trustTS == 'S' ? $scope.inputVO.rCreditAcctList : $scope.inputVO.debitAcct, //金錢信託以扣款帳號為贖回入帳帳號
				'TRUST_ACCT': $scope.inputVO.trustAcct,
				'IS_END_CERTIFICATE': $scope.inputVO.isEndCertificate,
				'IS_RE_PURCHASE': $scope.inputVO.isRePurchase,
				'TRADE_DATE_TYPE': $scope.inputVO.tradeDateType,
				'TRADE_DATE': $scope.inputVO.tradeDate,
				'RDM_FEE': '',
				'MGM_FEE': '',
				'PCH_PROD_ID': $scope.inputVO.pchProdID,
				'PCH_PROD_NAME': $scope.inputVO.pchProdName,
				'PCH_PROD_CURR': $scope.inputVO.pchProdCurr,
				'PCH_PROD_RISK_LV': $scope.inputVO.pchProdRiskLV,
				'PROSPECTUS_TYPE' : $scope.inputVO.prospectusType,
				'NOT_VERTIFY':$scope.inputVO.prodFus40,
				'DEFAULT_FEE_RATE': $scope.inputVO.defaultFeeRate,
				'FEE_TYPE': $scope.inputVO.feeRateType?$scope.inputVO.feeRateType:"C",
				'BRG_REASON': $scope.inputVO.brgReason,
				'BARGAIN_STATUS': $scope.inputVO.bargainStatus,
				'BARGAIN_APPLY_SEQ': $scope.inputVO.bargainApplySeq,
				'FEE_RATE': $scope.inputVO.feeRate,
				'FEE': $scope.inputVO.feePrice,
				'FEE_DISCOUNT': $scope.inputVO.feeDiscount,
				'STOP_LOSS_PERC': $scope.inputVO.stopLossPerc,
				'TAKE_PROFIT_PERC': $scope.inputVO.takeProfitPerc,
				'PL_NOTIFY_WAYS': $scope.inputVO.plNotifWays,
				'NARRATOR_ID': $scope.inputVO.narratorID,
				'NARRATOR_NAME': $scope.inputVO.narratorName,
				'PRESENT_VAL': $scope.inputVO.present_val,
				'CONTRACT_ID': $scope.inputVO.contractID,      //WMS-CR-20191009-01_金錢信託套表需求申請單 2020-2-10 add by Jacky
				'TRUST_PEOP_NUM': $scope.inputVO.trustPeopNum,
				'OVS_PRIVATE_SEQ': $scope.inputVO.ovsPrivateSeq
			});
			$scope.inputVO.seq_no=undefined;
			 $scope.savecart();
			// clean
//			$scope.inputVO = {
//				custID : $scope.inputVO.custID,
//				custName : $scope.inputVO.custName,
//				proxyCustID : $scope.inputVO.proxyCustID,
//				proxyCustName : $scope.inputVO.proxyCustName,
//				kycLevel : $scope.inputVO.kycLevel,
//				kycDueDate : $scope.inputVO.kycDueDate,
//				custRemarks : $scope.inputVO.custRemarks,
//				outFlag : $scope.inputVO.outFlag,
//				custTxFlag : $scope.inputVO.custTxFlag,
//				custFeePrdDueDate : $scope.inputVO.custFeePrdDueDate,
//				custType : 'CUST',
//				isRePurchase : 'N',
//				narratorID :projInfoService.getUserID(),
//				narratorName :projInfoService.getUserName()
//			};
		};

		$scope.edit = function(row) {
//			$scope.inputVO.profInvestorYN=row.PROF_INVESTOR_YN;
			$scope.inputVO.seq_no = row.SEQ_NO;
			$scope.inputVO.rdmProdID = row.RDM_PROD_ID;
			$scope.inputVO.rdmProdName = row.RDM_PROD_NAME;
			$scope.inputVO.rdmProdCurr = row.RDM_PROD_CURR;
			$scope.inputVO.rdmProdRiskLV = row.RDM_PROD_RISK_LV;
			$scope.inputVO.rdmTradeType = row.RDM_TRADE_TYPE;
			$scope.inputVO.rdmTradeTypeD = row.RDM_TRADE_TYPE_D;
			$scope.inputVO.rdmCertificateID = row.RDM_CERTIFICATE_ID;
			$scope.inputVO.trustCurrType = row.RDM_TRUST_CURR_TYPE;
			$scope.inputVO.rdmCurCode = row.RDM_TRUST_CURR;
			$scope.inputVO.rdmUnit = row.RDM_UNIT;
			$scope.inputVO.present_val = row.PRESENT_VAL.toFixed(4);
			$scope.inputVO.rdmProdAmt = row.RDM_TRUST_AMT;
			$scope.inputVO.redeemType = row.REDEEM_TYPE;
			$scope.inputVO.prodFus07 = row.FUS07;	//2017-09-11 取基金-小數點位數
			if ($scope.inputVO.redeemType == '1') {//全部贖回
				$scope.inputVO.numUnits = row.UNIT_NUM.toFixed(4);
			} else {//部份贖回
				$scope.inputVO.numUnits = row.UNIT_NUM.toFixed($scope.inputVO.prodFus07); //2017-09-11 取基金-小數點位數
			}
			$scope.inputVO.rCreditAcctList = row.CREDIT_ACCT;
			$scope.inputVO.isEndCertificate = row.IS_END_CERTIFICATE;
			$scope.inputVO.isRePurchase = row.IS_RE_PURCHASE;
			$scope.inputVO.tradeDateType = row.TRADE_DATE_TYPE;
			$scope.inputVO.tradeDate = row.TRADE_DATE;
//			$scope.inputVO. = row.RDM_FEE;
//			$scope.inputVO. = row.MGM_FEE;
			$scope.inputVO.prospectusType = row.PROSPECTUS_TYPE; //取得公開說明書方式
			$scope.inputVO.prodFus40 =row.NOT_VERTIFY,
			$scope.inputVO.pchProdID = row.PCH_PROD_ID;
			$scope.inputVO.pchProdName = row.PCH_PROD_NAME;
			$scope.inputVO.pchProdCurr = row.PCH_PROD_CURR;
			$scope.inputVO.pchProdRiskLV = row.PCH_PROD_RISK_LV;
			$scope.inputVO.defaultFeeRate = row.DEFAULT_FEE_RATE;
			$scope.inputVO.feeRateType = row.FEE_TYPE=='C'?'':row.FEE_TYPE;
			$scope.inputVO.bargainStatus = row.BARGAIN_STATUS;
			$scope.inputVO.bargainApplySeq = row.BARGAIN_APPLY_SEQ;
			$scope.inputVO.feeRate = row.FEE_RATE;
			$scope.inputVO.feePrice = row.FEE;
			$scope.inputVO.feeDiscount = row.FEE_DISCOUNT;
			$scope.inputVO.stopLossPerc = row.STOP_LOSS_PERC;
			$scope.inputVO.takeProfitPerc = row.TAKE_PROFIT_PERC;
			$scope.inputVO.plNotifWays = row.PL_NOTIFY_WAYS;
			$scope.inputVO.narratorID = row.NARRATOR_ID;
			$scope.inputVO.narratorName = row.NARRATOR_NAME;
			$scope.inputVO.ovsPrivateYN = row.OVS_PRIVATE_YN;
			$scope.inputVO.ovsPrivateSeq = row.OVS_PRIVATE_SEQ;

			$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.inputVO.rdmCurCode});
			$scope.getCurrency();
		};

		$scope.goSOT132=function(){
			var custId = $scope.inputVO.custID;
			var trustTS = $scope.inputVO.trustTS;
			var debitAcct = $scope.inputVO.debitAcct;
			var cartList = $scope.cartList;
			var contractID = $scope.inputVO.contractID;
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT132/SOT132.html',
				className: 'SOT132',
				controller:['$scope',function($scope){
					$scope.txnName = "sot130";
					$scope.custID=custId;
					$scope.trustTS=trustTS;
					$scope.debitAcct=debitAcct;
					$scope.cartList=cartList;
					$scope.contractID=contractID;
				}]
			});
			dialog.closePromise.then(function(data){
				console.log(data);
				if(data.value && data.value != "cancel"){
					
					//動態鎖利不可贖回
					if (data.value.Dynamic && (data.value.Dynamic == '1' || data.value.Dynamic == '2')) {
						$scope.showErrorMsg("動態鎖利母/子基金不可贖回");
						$scope.clearProd();
						return;
					}
					
					$scope.inputVO.is_backend = '';		//是否為後收型基金
					$scope.clearPchProd();
					$scope.inputVO.rdmProdID = data.value.FundNO; //基金代號
					//電文PROD NAME 為簡寫，改取DB資料
					//$scope.inputVO.rdmProdName = data.value.FundName; //基金名稱
					$scope.redeemStatus = false;
					//信託型態
//					data.value.TxType='N';	//test
//					data.value.AssetType='0002';//test
					var assetType = data.value.AssetType.slice(data.value.AssetType.length-1, data.value.AssetType.length);
					switch (assetType){
						case "1"://單筆申購

							break;
						case "2"://定期定額
							if(data.value.Status && data.value.Status != '2'){	//判斷憑證是否中止是否可選擇
								$scope.redeemStatus = true;
								$scope.isEndCertificate_required = true;
							}
							break;
						case "3"://定期不定額
							if(data.value.Status && data.value.Status != '2'){	//判斷憑證是否中止是否可選擇
								$scope.redeemStatus = true;
								$scope.isEndCertificate_required = true;
							}
							break;
					}
					$scope.inputVO.isRePurchase='N';
					$scope.inputVO.redeemType=1;
					$scope.inputVO.rdmTradeType       =   data.value.assetTradeSubType;  	   //交易型態
					$scope.inputVO.rdmTradeTypeD 	  =   data.value.assetTradeSubTypeD; //詳細交易型態
					$scope.present_val				  =   data.value.CurBal.toFixed(4);
					$scope.inputVO.present_val		  =   data.value.CurBal.toFixed(4); //參考現值
					$scope.inputVO.rdmCertificateID   =   data.value.EviNum; //憑證編號
					$scope.inputVO.rdmProdCurr        =   data.value.CurFund;//計價幣別
					$scope.inputVO.rdmCurCode         =   data.value.CurCode;//信託幣別
					$scope.inputVO.rdmProdAmt		  =   data.value.CurAmt; //信託金額
					$scope.inputVO.rdmUnit            =   data.value.CurUntNum; //原單位數
					if ($scope.inputVO.rdmUnit == '0') {
						$scope.showErrorMsg("ehl_02_sot130_002");	//庫存單位數為0，不可贖回
						$scope.clearProd();
						return;
					}
					$scope.inputVO.numUnits           =   data.value.CurUntNum.toFixed(4); //單位數
					$scope.inputVO.trustAcct          =   data.value.AcctId02.trim();  //信託帳號
					$scope.inputVO.branchNbr 		  =   $scope.inputVO.trustAcct?$scope.inputVO.trustAcct.substring(2,5):"";
					$scope.unitValue 				  =   $scope.inputVO.present_val/$scope.inputVO.rdmUnit //單位(計算贖回方式之值) present_val= numUnits * unitValue
//					data.value.TxType //交易類別
					$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.inputVO.rdmCurCode});
					$scope.checkTrustAcct();
					$scope.setRedeemType();
					                       //信託金額
					$scope.sendRecv("SOT130", "getProdInfo", "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO", {'rdmProdID':$scope.inputVO.rdmProdID},
							function(tota, isError) {
								if (!isError) {
									debugger
									if(tota[0].body && tota[0].body.length !=0){
										$scope.inputVO.rdmProdName		  =   tota[0].body[0].FUND_CNAME; //產品名稱
										$scope.inputVO.rdmProdRiskLV      =   tota[0].body[0].RISKCATE_ID; //產品風險等級
										$scope.inputVO.prodFus20 		  =	  tota[0].body[0].FUS20;//業務別
										$scope.inputVO.trustCurrType 	  =   $scope.setTrustCurrType($scope.inputVO.prodFus20,$scope.inputVO.rdmCurCode);
										$scope.inputVO.prodFus40 		  =	  tota[0].body[0].FUS40;//是否未核備
										$scope.inputVO.prodFus07 		  =	  tota[0].body[0].FUS07;//2017-09-11 取基金-小數點位數
										$scope.inputVO.ovsPrivateYN		  =	  tota[0].body[0].OVS_PRIVATE_YN;//是否是境外私募基金
										$scope.inputVO.ovsPrivateSeq	  =   tota[0].body[0].OVS_PRIVATE_SEQ;//境外私募基金序號
										// 幣別小數點轉換
										$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.inputVO.rdmCurCode});
										$scope.getCurrency();
										
										//境外私募基金交易日
										if($scope.inputVO.ovsPrivateYN == "Y") {
											if($scope.cartList && $scope.cartList.length >= 1) {
												$scope.showErrorMsg("境外私募基金請單獨套表");	
												$scope.clearProd();
												return;
											}
											
											$scope.tradeDate = tota[0].body[0].TRADE_DATE_OVSPRI;
											$scope.inputVO.tradeDate = tota[0].body[0].TRADE_DATE_OVSPRI;
											$scope.inputVO.tradeDateType = '2';
										} else {
											if($scope.cartList && $scope.cartList.length >= 1 && $scope.cartList[0].OVS_PRIVATE_YN == "Y") {
												$scope.showErrorMsg("購物車中已有境外私募基金，請單獨套表");	
												$scope.clearProd();
												return;
											}
											
											$scope.getTradeDate();
										}
									}
									return;
								} else {
									$scope.clearProd();
									return;
								}
					});

					//查詢是否為"後收型基金" ===> 基金轉換：1. 僅提供全部贖回。	 2. 不提供再申購功能。
					$scope.sendRecv("SOT140", "isBackend", "com.systex.jbranch.app.server.fps.sot140.SOT140InputVO", {'outProdID': data.value.FundNO},
							function(tota, isError) {
								if (!isError) {
									$scope.inputVO.is_backend = tota[0].body.prodDTL[0].IS_BACKEND;
								}
					});
				}
				
				//#0695 排除數存戶
				if($scope.inputVO.trustTS != 'M' && sotService.is168($scope.inputVO.trustAcct)
						&& sotService.isDigitAcct($scope.inputVO.trustAcct,$scope.mappingSet['debit'])){
					$scope.showErrorMsg("ehl_02_SOT_996");
					$scope.clearProd();
				} 
			});
		};

		$scope.goPRD110=function(main_prd){
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD110/PRD110_ROUTE.html',
				className: 'PRD110',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					$scope.isPop=true;
	        		$scope.routeURL = 'assets/txn/PRD110/PRD110.html';
	        		$scope.txnName = "查詢條件";
	        		$scope.cust_id=$scope.$parent.inputVO.custID;
	        		$scope.tradeType=$scope.$parent.inputVO.tradeType;
	        		$scope.main_prd=main_prd;
	        		$scope.fromSOTProdYN = "Y"; //下單商品搜尋//下單只需要再申購時檢核，商品搜尋時不需再次檢核
	            }]
			}).closePromise.then(function (data) {
				if(data.value && data.value != 'cancel'){
						$scope.inputVO.pchProdID     = data.value.PRD_ID;
						$scope.inputVO.pchProdName   = data.value.FUND_CNAME;
						$scope.inputVO.pchProdRiskLV = data.value.RISKCATE_ID;
						$scope.inputVO.pchProdCurr   = data.value.CURRENCY_STD_ID;
						$scope.inputVO.pchTrustCurr  = data.value.CURRENCY_STD_ID;
						if(data.value.OVS_PRIVATE_YN == "Y") {  //境外私募基金
							$scope.inputVO.pchProdID = '';
							$scope.inputVO.pchProdName= '';
							$scope.inputVO.pchProdRiskLV= '';
							$scope.inputVO.pchProdCurr= '';
							$scope.inputVO.pchTrustCur='';
							$scope.showErrorMsg("境外私募基金不可做為再申購標的");
						}
						$scope.sendRecv("SOT130", "getProdInfo", "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO", {'rdmProdID':$scope.inputVO.pchProdID},
								function(tota, isError) {
									if (!isError) {
										if(tota[0].body && tota[0].body.length !=0){
											var pchTrustCurrType = $scope.setTrustCurrType(tota[0].body[0].FUS20,$scope.inputVO.pchTrustCurr);
											$scope.PROD_pchTrustCurrType = pchTrustCurrType;
											var clearPchProd = false;
											if(pchTrustCurrType=='C'){//若在申購商品信託幣別為國內，則幣別須相同
												if($scope.inputVO.pchTrustCurr !=  $scope.inputVO.rdmCurCode){
													clearPchProd=true;
												}
											}else if(pchTrustCurrType=='N' || pchTrustCurrType=='Y'){//若在申購商品信託幣別為國外
												if($scope.inputVO.rdmCurCode != 'TWD'){//若贖回幣別為台幣皆可購買，若為外幣則幣別需相同
													if($scope.inputVO.pchTrustCurr !=  $scope.inputVO.rdmCurCode){
														clearPchProd=true;
													}
												}
											}
											
											if(clearPchProd){
												$scope.inputVO.pchProdID = '';
												$scope.inputVO.pchProdName= '';
												$scope.inputVO.pchProdRiskLV= '';
												$scope.inputVO.pchProdCurr= '';
												$scope.inputVO.pchTrustCur='';
												$scope.showErrorMsg("ehl_02_sot130_001");
											}else{
												$scope.getDefaultFee(pchTrustCurrType);
												$scope.validFeeTypeData();
												// 幣別小數點轉換
												$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.inputVO.pchProdCurr});
												$scope.getCurrency();
											}
										}
										return;
									}
						});
						//信託業務別 trustCurrType
//						if($scope.inputVO.pchProdCurr == 'TWD'){
//							$scope.inputVO.trustCurrType='Y';
//						}else{
//							$scope.inputVO.trustCurrType='N';
//						}
//						$scope.inputVO.custID='A123456789';//test
//						$scope.getDefaultFee();
				}
			});
		};



		$scope.delCar = function(seqNO) {
			debugger;
			var txtMsg = "";
			if ($scope.countList() == 1) {
				txtMsg = $filter('i18n')('ehl_02_common_005');
			} else {
				txtMsg = $filter('i18n')('ehl_02_common_006');
			}

			$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
				$scope.sendRecv("SOT130", "delProd", "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO", {tradeSEQ : $scope.tradeSEQ, seqNo: seqNO},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.prodID = "";
//								$scope.prodClear();
								$scope.refresh();
								return;
							}
				});
            });
		};

		$scope.getDefaultFee = function (pchTrustCurrType) {
			if(pchTrustCurrType == undefined){
				pchTrustCurrType = $scope.PROD_pchTrustCurrType;
			}
			if($scope.inputVO.tradeDateType=='2')
				var tradeDate =$scope.toJsDate( $scope.tradeDate);
			$scope.sendRecv("SOT709", "getDefaultFeeRate", "com.systex.jbranch.app.server.fps.sot709.SOT709InputVO", {"custId":$scope.inputVO.custID,
																													  "branchNbr":$scope.inputVO.branchNbr,
																													  "tradeSubType":'1',
																												      "trustCurrType":pchTrustCurrType,
																												      "tradeDate":tradeDate,
																												      "prodId":$scope.inputVO.pchProdID,
																												      "purchaseAmtL":$scope.inputVO.present_val,
																												      "trustAcct":$scope.inputVO.trustAcct
																													},
					function(tota1, isError) {
						if (!isError) {
							 if(tota1[0].body.errorCode != null && tota1[0].body.errorCode != "") {
								  $scope.showErrorMsg(tota1[0].body.errorCode + ":" + tota1[0].body.errorMsg);
								  $scope.inputVO.feePrice = undefined;
								  $scope.inputVO.feeDiscount = undefined;
								  $scope.inputVO.feePrice =undefined;
								  $scope.inputVO.defaultFeeRate = undefined;
								  $scope.inputVO.feeRate = undefined;
							  } else {
								  $scope.feeTypeData = tota1[0].body.defaultFeeRates;
								  $scope.getFeeType();
								  $scope.getFee('rate');
							  }
							  return;
						}
			});
		};

		$scope.validFeeTypeData = function(){
			$scope.mappingSet['FEE_RATE_TYPE']=[];
			for(var i=0;i<$scope.feeRateTypeLst.length;i++){
				if($scope.feeRateTypeLst[i].DATA=='D'){
					if($scope.inputVO.pchProdID != $scope.feeRateTypeLst[i].FundNo){
						continue;
					}
				}
				$scope.mappingSet['FEE_RATE_TYPE'].push($scope.feeRateTypeLst[i]);
			}
		}

		$scope.getFeeTypeData = function () {
			$scope.feeRateTypeLst = [];
			var deferred = $q.defer();
			$scope.mappingSet['FEE_RATE_TYPE'] = [];
			$scope.sendRecv("SOT130","getFeeTypeData" /*"getSingleRegFeeRate"*/, "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO", {'custID':$scope.inputVO.custID},
					function(tota1, isError) {
						if (!isError) {
							$scope.feeRateTypeLst=tota1[0].body.singleFeeRateList;
							$scope.validFeeTypeData();
							deferred.resolve("success");
						}
			});
			return deferred.promise;
		};

		$scope.setTrustCurrType= function(FUS20,curcode){
			if(FUS20 && FUS20=='C'){
				return "C";
			}else{
				if(curcode=='TWD'){
					return"N";
				}else{
					return"Y";
				}
			}
		}

		$scope.getFeeType = function () {
			if(!$scope.inputVO.feeRateType || $scope.inputVO.feeRateType =='A'){
				$scope.inputVO.feePrice =$scope.feeTypeData.feeL;
				$scope.inputVO.defaultFeeRate = $scope.feeTypeData.defaultFeeRateL;
				$scope.inputVO.feeRate = $scope.feeTypeData.feeRateL;
			}else if($scope.inputVO.feeRateType =='B'){
				$scope.inputVO.feePrice ='';
				$scope.inputVO.feeRate = '';
			}
			else{
				for(var i=0;i<$scope.mappingSet['FEE_RATE_TYPE'].length;i++){
					if($scope.inputVO.feeRateType==$scope.mappingSet['FEE_RATE_TYPE'][i].DATA){
						$scope.inputVO.feeRate = $scope.mappingSet['FEE_RATE_TYPE'][i].FeeRate;
					}
				}
			}
		}

		// 商品查詢
		$scope.getProdDTL = function () {
			$scope.inputVO.pchProdName= '';
			$scope.inputVO.pchProdRiskLV= '';
			$scope.inputVO.pchProdCurr= '';
			$scope.inputVO.pchTrustCur='';
			$("#tradeDateType1").attr("disabled", false);
			if($scope.inputVO.pchProdID) {
				var ProdIDTouppercase = $scope.inputVO.pchProdID.toUpperCase();
				$scope.inputVO.pchProdID = ProdIDTouppercase;
//				$scope.inputVO.pchProdID = '';
				$scope.sendRecv("SOT130", "getProdDTL", "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.errorMsg != null && tota[0].body.errorMsg != "") {
									//適配有錯誤訊息
									$scope.showErrorMsg(tota[0].body.errorMsg);
									$scope.inputVO.pchProdID = '';
									$scope.inputVO.pchProdName= '';
									$scope.inputVO.pchProdRiskLV= '';
									$scope.inputVO.pchProdCurr= '';
									$scope.inputVO.pchTrustCur='';
								} else if (tota[0].body.prodDTL != null && tota[0].body.prodDTL.length > 0) {
									if(tota[0].body.prodDTL[0].OVS_PRIVATE_YN == "Y") { //境外私募基金
										$scope.inputVO.pchProdID = '';
										$scope.inputVO.pchProdName= '';
										$scope.inputVO.pchProdRiskLV= '';
										$scope.inputVO.pchProdCurr= '';
										$scope.inputVO.pchTrustCur='';
										$scope.showErrorMsg("境外私募基金不可做為再申購標的");
										return;
									}
									if (tota[0].body.warningMsg != null && tota[0].body.warningMsg != "") {
										//適配有警告訊息
										var dialog = ngDialog.open({
											template: 'assets/txn/CONFIRM/CONFIRM',
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
												$scope.inputVO.pchProdID = '';
												$scope.inputVO.pchProdName= '';
												$scope.inputVO.pchProdRiskLV= '';
												$scope.inputVO.pchProdCurr= '';
												$scope.inputVO.pchTrustCur='';
											}
										});
									}

									$scope.inputVO.pchProdName = tota[0].body.prodDTL[0].FUND_CNAME;			//變更後基金名稱
									$scope.inputVO.pchProdCurr = tota[0].body.prodDTL[0].CURRENCY_STD_ID;		//變更後產品計價幣別
									$scope.inputVO.pchProdRiskLV = tota[0].body.prodDTL[0].RISKCATE_ID;		//變更後產品風險等級
									$scope.inputVO.pchTrustCurr = tota[0].body.prodDTL[0].CURRENCY_STD_ID;	//變更後產品信託幣別
									var pchTrustCurrType = $scope.setTrustCurrType(tota[0].body.prodDTL[0].FUS20,$scope.inputVO.pchTrustCurr);
									$scope.PROD_pchTrustCurrType = pchTrustCurrType;
									$scope.validFeeTypeData();

									var clearPchProd = false;
									if(pchTrustCurrType=='C'){//若在申購商品信託幣別為國內，則幣別須相同
										if($scope.inputVO.pchTrustCurr !=  $scope.inputVO.rdmCurCode){
											clearPchProd=true;
										}
									}else if(pchTrustCurrType=='N' || pchTrustCurrType=='Y'){//若在申購商品信託幣別為國外
										if($scope.inputVO.rdmCurCode != 'TWD'){//若贖回幣別為台幣皆可購買，若為外幣則幣別需相同
											if($scope.inputVO.pchTrustCurr !=  $scope.inputVO.rdmCurCode){
												clearPchProd=true;
											}
										}
									}
									//if(pchTrustCurrType!=$scope.inputVO.trustCurrType){
									if(clearPchProd){
										$scope.inputVO.pchProdID = '';
										$scope.inputVO.pchProdName= '';
										$scope.inputVO.pchProdRiskLV= '';
										$scope.inputVO.pchProdCurr= '';
										$scope.inputVO.pchTrustCur='';
										$scope.showErrorMsg("ehl_02_sot130_001");
									}else{
										$scope.getDefaultFee(pchTrustCurrType);
									}
									// 幣別小數點轉換
//									$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.inputVO.pchProdCurr});
//									$scope.getCurrency();


									//控管國內貨幣型基金交易時間，超過10:30 轉為預約交易狀態
									debugger;
									console.log($scope.inputVO.pchProdID);
									if($scope.inputVO.pchProdID != null && $scope.inputVO.pchProdID != ''){
										$scope.sendRecv("SOT110", "checkReserve", "com.systex.jbranch.app.server.fps.sot110.SOT110InputVO", {"prodId" : $scope.inputVO.pchProdID},
												function(tota, isError) {
													if (!isError) {
														$scope.rev = tota[0].body.reserve;
														if(!$scope.rev){
																$("#tradeDateType1").attr("disabled", true);
																$("#tradeDateType2").attr("checked", true);
																$scope.inputVO.tradeDateType = '2';
																$scope.inputVO.tradeDate = $scope.tradeDate;
																$scope.showMsg("此筆交易已超過國內貨幣型基金交易時間，將轉為預約交易");
														}
													}
										});
									}
									return;
								}
							}

							$scope.showErrorMsg("ehl_01_common_009");

							$scope.inputVO.pchProdID = '';
							$scope.inputVO.pchProdName= '';
							$scope.inputVO.pchProdRiskLV= '';
							$scope.inputVO.pchProdCurr= '';
							$scope.inputVO.pchTrustCur='';
				});
			}
		};



		//取得解說專員姓名
		$scope.getTellerName = function(){
			$scope.inputVO.narratorName='';
			if($scope.inputVO.narratorID) {
				$scope.sendRecv("SOT712", "getTellerName", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {"tellerID":$scope.inputVO.narratorID},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.narratorName=tota[0].body.EMP_NAME;
							return;
							}
				});
			}
		};

		$scope.refresh = function (seqNO) {
			if (seqNO) {
				$scope.inputVO.carSEQ = seqNO;
			}

			$scope.noCallCustQuery();
			$scope.clearProd();

		};

		$scope.next = function() {
			console.log($scope.inputVO.tradeDate);
			$scope.sendRecv("SOT130", "next", "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO",{ "tradeSEQ":$scope.tradeSEQ},
					function(tota, isError) {
						if (!isError) {
							$scope.connector('set','SOTTradeSEQ', $scope.tradeSEQ);
							$scope.connector('set','SOTCarSEQ', null);
							debugger;
							if ($scope.fromFPS) {
								// from FPS_SOT.js
								if($scope.inputVO.trustTS == 'S'){
									$scope.setSOTurl('assets/txn/SOT131/SOT131.html');
								}else{
									$scope.setSOTurl('assets/txn/SOT134/SOT134.html');
								}

							} else {
								if($scope.inputVO.trustTS == 'S'){
									$rootScope.menuItemInfo.url = "assets/txn/SOT131/SOT131.html";
								}else{
									$rootScope.menuItemInfo.url = "assets/txn/SOT134/SOT134.html";
								}
							}
							return;
						}
			});
		};
		 $scope.savecart = function()  {
			 if($scope.parameterTypeEditForm.$invalid) {
	               $scope.showErrorMsg("ehl_01_common_022");
	               return;
			 }

			 	getGoalDueFund($scope.inputVO.rdmProdID)
                    .then(function(goalDueFund) {
                        // 是否為目標到期型基金
                        if ('Y' === goalDueFund) {
                            $scope.showErrorMsg("ehl_02_sot130_005");
                        } else {
							if($scope.inputVO.is_backend == 'Y') {
								$scope.showErrorMsg("ehl_02_sot130_003");
							}
                        }
                    });

				console.log($scope.inputVO.tradeDate);
				 $scope.sendRecv("SOT130", "save", "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO",
						 {'tradeSEQ': $scope.tradeSEQ,'status': '1','custDTL': $scope.custDTL,'cartList': $scope.cartListSave,'trustTS' : $scope.inputVO.trustTS,'GUARDIANSHIP_FLAG' : $scope.inputVO.GUARDIANSHIP_FLAG },
							function(tota, isError) {
								if (!isError) {
									if(tota[0].body.errorMsg){
										$scope.showErrorMsg(tota[0].body.errorMsg);
										return;
									}
									if(tota[0].body.Short_1!=''){
										if(tota[0].body.Short_1==1){
											$scope.showErrorMsg('ehl_01_SOT_015');
										}
										if(tota[0].body.Short_1==2){
											$scope.showErrorMsg('ehl_01_SOT_016');
										}
									}
									$scope.refresh();
									return;
								}
					});
		};


		 function getGoalDueFund(prdId) {
		 	let defer = $q.defer();
		 	$scope.sendRecv("SOT130", "getGoalDueFund", "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO",
				 { rdmProdID: prdId },
				 function(tota, isError) {
						if (!isError)
			 		        defer.resolve(tota[0].body)
                        else
                            defer.reject();
				 });
		 	return defer.promise;
		 };

		//由客管導頁進入
		$scope.inbyCRM821 = function(){
			if($scope.connector('get','SOTCustID')){
				$scope.inputVO.custID = $scope.connector('get','SOTCustID');
				$scope.connector('get','SOTCustID',null);
				var data = $scope.connector('get','SOTProd');
				$scope.connector('set','SOTProd',null);
				$scope.getSOTCustInfo();

					if(data){
						$scope.clearPchProd();
						$scope.inputVO.rdmProdID          =   data.FundNO; //基金代號
						$scope.inputVO.rdmProdName        =   data.FundName; //基金名稱
						$scope.redeemStatus = false;
						//信託型態

						var assetType=data.AssetType.slice(data.AssetType.length-1,data.AssetType.length);
						var assetTypeD = assetType;
						switch (assetType){
							case "1"://單筆申購

								break;
							case "2"://定期定額
								if(sotService.isFundProjectByTxType(data.TxType)) {
									assetType='4';
									assetTypeD = data.TxType === 'Y'? '4': '8';
								}
								if(data.Status && data.Status != '2'){	//判斷憑證是否中止是否可選擇
									$scope.redeemStatus = true;
									$scope.isEndCertificate_required = true;
								}
								break;
							case "3"://定期不定額
								if(sotService.isFundProjectByTxType(data.TxType)){
									assetType='5';
									assetTypeD = data.TxType === 'Y'? '5': '9';
								}
								if(data.Status && data.Status != '2'){	//判斷憑證是否中止是否可選擇
									$scope.redeemStatus = true;
									$scope.isEndCertificate_required = true;
								}
								break;
						}
						$scope.inputVO.isRePurchase='N';
						$scope.inputVO.redeemType=1;
						$scope.inputVO.rdmTradeType       =   assetType;  	//交易型態
						$scope.inputVO.rdmTradeTypeD      =   assetTypeD; //詳細交易型態
						$scope.present_val				  =   data.CurBal.toFixed(4);
						$scope.inputVO.present_val		  =   data.CurBal.toFixed(4); //參考現值
						$scope.inputVO.rdmCertificateID   =   data.EviNum; //憑證編號
						$scope.inputVO.rdmProdCurr        =   data.CurFund;//計價幣別
						$scope.inputVO.rdmCurCode         =   data.CurCode;//信託幣別
						$scope.inputVO.rdmProdAmt		  =   data.CurAmt; //信託金額
						$scope.inputVO.rdmUnit            =   data.CurUntNum; //原單位數
						if ($scope.inputVO.rdmUnit == '0') {
							$scope.showErrorMsg("ehl_02_sot130_002");
							$scope.clearProd();
							return;
						}
						$scope.inputVO.numUnits           =   data.CurUntNum.toFixed(4); //單位數
						$scope.inputVO.trustAcct          =   data.AcctId02.trim();  //信託帳號
						$scope.inputVO.branchNbr 		  =   $scope.inputVO.trustAcct?$scope.inputVO.trustAcct.substring(2,5):"";
						$scope.unitValue 				  =   $scope.inputVO.present_val/$scope.inputVO.rdmUnit //單位(計算贖回方式之值) present_val= numUnits * unitValue
						$scope.checkTrustAcct();
						$scope.setRedeemType();

						//信託金額
						$scope.sendRecv("SOT130", "getProdInfo", "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO", {'rdmProdID':$scope.inputVO.rdmProdID},
								function(tota, isError) {
									if (!isError) {
										if(tota[0].body && tota[0].body.length !=0){

											$scope.inputVO.rdmProdRiskLV      =   tota[0].body[0].RISKCATE_ID; //產品風險等級
											$scope.inputVO.prodFus20 		  =	  tota[0].body[0].FUS20;//業務別
											$scope.inputVO.trustCurrType 	  =   $scope.setTrustCurrType($scope.inputVO.prodFus20,$scope.inputVO.rdmCurCode);
											$scope.inputVO.prodFus40 		  =	  tota[0].body[0].FUS40;//是否未核備
											$scope.inputVO.prodFus07 		  =	  tota[0].body[0].FUS07;//2017-09-11 取基金-小數點位數

										}
										return;
									}
						});
					}

			}
			$scope.connector('set','SOTCustID',undefined);
			$scope.connector('set','SOTProd',undefined);
		}
		$scope.inbyCRM821();

		//FPS導頁進入
		$scope.inbyFPS = function(){
			if ($scope.fromFPS){
				console.log($scope.FPSData);
				$scope.inputVO.custID = $scope.FPSData.custID;
				$scope.getSOTCustInfo();
			}
		}
		$scope.inbyFPS();

		/**
		 * 確認基金註記 => 此交易檢查是否停止申購
		 */
		$scope.checkFundStatus = () => $scope.sendRecv("SOT703", "qryFundMemo",
			"com.systex.jbranch.app.server.fps.sot703.SOT703InputVO", {prodId: $scope.inputVO.pchProdID},
			(tota) => tota[0].body[0] && tota[0].body[0].NO_E_PURCHASE ? $scope.showErrorMsg(tota[0].body[0].NO_E_PURCHASE) : $scope.getProdDTL());

		$scope.checkBackEndRule = function (row) {
			debugger;
			var deferred = $q.defer();
			$scope.sendRecv("SOT130", "getBackendConstraintParameter", "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO", {'outProdID': row.RDM_PROD_ID},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.prodDTL[0].IS_BACKEND == 'Y' && tota[0].body.prodDTL[0].FUSM10 == 'Y' ) {
								var chosenCartList = $scope.cartList.filter(function(cart) {
									return cart.RDM_PROD_ID == row.RDM_PROD_ID && cart.CONTRACT_ID == row.CONTRACT_ID;
								});
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
			if ($scope.countList() == 1) {
				txtMsg = $filter('i18n')('ehl_02_common_005');
			} else {
				txtMsg = $filter('i18n')('ehl_02_common_006');
			}

			$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
				$scope.sendRecv("SOT130", "delProd", "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO", {tradeSEQ : $scope.tradeSEQ, seqNo: row.SEQ_NO},
						function(tota, isError) {
							if (!isError) {
								$scope.inputVO.prodID = "";
//								$scope.prodClear();
								$scope.refresh();
								return;
							}
				});
	        });
			});
		};
		$scope.checkDigitAcct = function() {
			$scope.isDigitAcct = true;			
			if($scope.inputVO.rCreditAcctList == undefined || $scope.inputVO.rCreditAcctList == ''){
				$scope.isDigitAcct = false;
				return;
			}
			if($scope.inputVO.trustTS == 'M'){
				$scope.isDigitAcct = false;
				return;
			}
				
			if(sotService.isDigitAcct($scope.inputVO.rCreditAcctList, $scope.mappingSet['debit'])){
				$scope.isDigitAcct = true;
				$scope.inputVO.isRePurchase = 'N';
			} else {
				$scope.isDigitAcct = false;
			}
		};

		$scope.validateSeniorCust = function() {
			if($scope.inputVO.isRePurchase == "Y") {
				//再申購才做高齡檢核
				$scope.inputVO.type = "1";
				$scope.inputVO.cust_id = $scope.inputVO.custID;
				$scope.validSeniorCustEval(); //PRD100.validSeniorCustEval高齡檢核
			}
		}
		
		//PRD100.validSeniorCustEval高齡檢核不通過不可再申購
		$scope.clearCustInfo = function() {
			$scope.inputVO.isRePurchase = "N";
		};
		
		//PRD100.validSeniorCustEval高齡檢核通過後查詢
		$scope.reallyInquire = function() {
			//檢核通過可做再申購不需做其他
		};
});