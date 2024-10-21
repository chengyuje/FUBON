/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT310Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q, validateService, sotService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT310Controller";
		//繼承PRD100高齡檢核
		$controller('PRD100Controller', {$scope: $scope});
		var pageID = $scope.connector('get','SOT310PAGE'); // 從哪前來SOT315
		$scope.connector('set','SOT310PAGE', null);
//		alert(pageID);

		// filter
		getParameter.XML(["SOT.CUST_TYPE", "SOT.MARKET_TYPE","SOT.SPEC_CUSTOMER","SOT.BN_CUR_LIMIT","SOT.BN_CUR_LIMIT_GTC", "SOT.BN_GTC_LIMITPRICE_RANGE", "OTH001", "SOT.BOND_WEB_TIMESTAMP"], function(totas) {
			if (totas) {
				$scope.mappingSet['SOT.CUST_TYPE'] = totas.data[totas.key.indexOf('SOT.CUST_TYPE')];
				$scope.mappingSet['SOT.MARKET_TYPE'] = totas.data[totas.key.indexOf('SOT.MARKET_TYPE')];
				$scope.mappingSet['SOT.ENTRUST_TYPE_PURCHASE'] = totas.data[totas.key.indexOf('SOT.ENTRUST_TYPE_PURCHASE')];
				$scope.mappingSet['SOT.SPEC_CUSTOMER'] = totas.data[totas.key.indexOf('SOT.SPEC_CUSTOMER')];//客戶註記
				$scope.mappingSet['SOT.BN_CUR_LIMIT'] = totas.data[totas.key.indexOf('SOT.BN_CUR_LIMIT')];
				$scope.mappingSet['SOT.BN_CUR_LIMIT_GTC'] = totas.data[totas.key.indexOf('SOT.BN_CUR_LIMIT_GTC')];
				$scope.mappingSet['SOT.BN_GTC_LIMITPRICE_RANGE'] = totas.data[totas.key.indexOf('SOT.BN_GTC_LIMITPRICE_RANGE')]
				$scope.mappingSet['CBS.ORDERDATE'] = totas.data[totas.key.indexOf('OTH001')].filter(e => e.DATA === 'ORDER_DATE');
				$scope.mappingSet['SOT.BOND_WEB_TIMESTAMP'] = totas.data[totas.key.indexOf('SOT.BOND_WEB_TIMESTAMP')];//當日14:50(以參數設定)之後不可使用網銀快速申購
				debugger;
			}
		});

		$scope.getMaxGtcEndDate = function() {
			$scope.sendRecv("SOT310", "getMaxGtcEndDate", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO", {},
			function(tota, isError) {
				if (!isError) {
					// 指定交易起日限定為『次營業日』起的20個營業日內，AS400仍要檢核是否有遇到臺灣、美國或香港假日。
					$scope.inputVO.MIN_GTC_START_DATE = $scope.toJsDate(tota[0].body.minGtcStartDate);
					$scope.inputVO.MAX_GTC_START_DATE = $scope.toJsDate(tota[0].body.maxGtcStartDate);
					$scope.inputVO.MIN_GTC_END_DATE   = $scope.toJsDate(tota[0].body.minGtcEndDate);
					$scope.inputVO.MAX_GTC_END_DATE   = $scope.toJsDate(tota[0].body.maxGtcEndDate);
					
//					$scope.inputVO.MIN_GTC_START_DATE = tota[0].body.minGtcStartDate;
//					$scope.inputVO.MAX_GTC_DATE = tota[0].body.maxGtcEndDate;
//					alert($scope.inputVO.MIN_GTC_START_DATE);
//					alert($scope.inputVO.MAX_GTC_DATE);
					
					$scope.limitDate();
				}
			});
		};
		
		if (pageID != 'SOT315') {
			$scope.getMaxGtcEndDate();
		}
		
		// date picker
//		$scope.apply_gtcEndDateOptions = {
//			maxDate: $scope.maxDate,
//			minDate: $scope.minDate
//		};

		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			var minStartDate = $scope.inputVO.MIN_GTC_START_DATE;
			var maxStartDate = $scope.inputVO.MAX_GTC_START_DATE;
			var minEndDate   = $scope.inputVO.MIN_GTC_END_DATE;
			var maxEndDate   = $scope.inputVO.MAX_GTC_END_DATE;
			
//			var endDate = undefined;
//			if ($scope.inputVO.gtcYN != 'P' && $scope.inputVO.gtcEndDate != undefined) {
//				endDate = angular.copy($scope.inputVO.gtcEndDate);
//				endDate = endDate.setDate(endDate.getDate()-1);					
//				endDate = new Date(endDate);
//				if (endDate > maxEndDate) {
//					endDate = maxEndDate;
//				}
//			}
			$scope.apply_gtcStartDateOptions.minDate = minStartDate;
			$scope.apply_gtcStartDateOptions.maxDate = maxStartDate;
			
			// 長效單起迄日邏輯：最短2日最長5日,要連續的日期區間
			if ($scope.inputVO.gtcYN == 'Y') {
				var startDate = undefined;
				if ($scope.inputVO.gtcStartDate != undefined) {
					startDate = angular.copy($scope.inputVO.gtcStartDate);
					startDate = startDate.setDate(startDate.getDate()+1);					
					startDate = new Date(startDate);
					if (startDate < minEndDate) {
						startDate = minEndDate;
					}
				}
				$scope.apply_gtcEndDateOptions.minDate = startDate || minEndDate;
				$scope.apply_gtcEndDateOptions.maxDate = maxEndDate;
				
			} else if ($scope.inputVO.gtcYN == 'P') {
				$scope.apply_gtcEndDateOptions.minDate = undefined;
				$scope.apply_gtcEndDateOptions.maxDate = undefined;
				// 選擇『預約單』則 enable 指定交易起日 及 disable 委託迄日，但委託迄日須等於指定交易起日。
				$scope.inputVO.gtcEndDate = $scope.inputVO.gtcStartDate;
				
			} else {
				$scope.apply_gtcEndDateOptions.minDate = minEndDate;
				$scope.apply_gtcEndDateOptions.maxDate = maxEndDate;
			}
			
			if ($scope.inputVO.gtcStartDate != undefined && $scope.inputVO.gtcEndDate != undefined) {
				if ($scope.inputVO.gtcStartDate > $scope.inputVO.gtcEndDate) {
					// 若起日大於迄日，則清空迄日
					$scope.inputVO.gtcEndDate = undefined;
				}
			}
		};

		//信託業務別
        var vo = {'param_type': 'SOT.TRUST_CURR_TYPE', 'desc': false};
        if(!projInfoService.mappingSet['SOT.TRUST_CURR_TYPE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['SOT.TRUST_CURR_TYPE'] = [];
        			for (var i = 0; i < totas[0].body.result.length; i++){
        				var element = totas[0].body.result[i];
        				if(element.DATA=='Y' || element.DATA=='N'){  //沒有C:國內(C是給基金)
        					projInfoService.mappingSet['SOT.TRUST_CURR_TYPE'].push({LABEL: element.LABEL, DATA: element.DATA});
        				}
        			}
        			$scope.mappingSet['SOT.TRUST_CURR_TYPE'] = projInfoService.mappingSet['SOT.TRUST_CURR_TYPE'];
        		}
        	});
        } else {
        	$scope.mappingSet['SOT.TRUST_CURR_TYPE'] = projInfoService.mappingSet['SOT.TRUST_CURR_TYPE'];
        }

        /**
		 * 進入申購頁面，先檢核交易時間，若時間超過15:00 (含)，則提示訊息「已過當日交易截止時間，請改採長效單或預約單委託」
		 * **/
		$scope.checkTradeDateType = function(){
			var defer = $q.defer();
			$scope.sendRecv("SOT310", "checkTime", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO", {},
			function(tota, isError) {
				if (!isError) {
					$scope.sotYN = tota[0].body.sotYN;
//					alert($scope.sotYN);
					
					if ($scope.sotYN == 'N') {
						$("#gtcN").attr("disabled", true);
						$scope.inputVO.gtcYN = 'Y';
						$scope.showMsg("已過當日交易截止時間，僅可採長效單或預約單委託。");
					}
				}
				defer.resolve("success");					
			});
			return defer.promise;
		};
        
		$scope.init = function() {
			var deferred = $q.defer();
			$scope.checkTradeDateType();
			$scope.cmbDebitAcct = true;  //控制Account disabled
			$scope.cmbCreditAcct = true; //控制Account disabled
			$scope.inputVO = {
					tradeSEQ: '',
					prodType:'3',                               //3：海外債
		        	tradeType:'1',                              //1：申購
		        	seniorAuthType:'S',							//高齡評估表授權種類(S:下單、A：適配)
					custID: '', 								//客戶ID
					custName: '',
					kycLV: '', 									//KYC等級
					kycDueDate: undefined, 						//KYC效期
					profInvestorYN: '', 						//是否為專業投資人
					piDueDate: undefined, 						//專業投資人效期
					piRemark: '',								//專業投資人註記
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
					w8benEffDate: undefined,					//W8ben有效日期
					w8BenEffYN: '',
					fatcaType: '',
//					isCustStakeholder: undefined,
//					custQValue: '',
					custProType: '',

					custType: 'CUST', 							//來行人員
					agentID: '', 								//代理人ID
					agentName: '',

					narratorID: projInfoService.getUserID(),	//解說人員ID
					narratorName: projInfoService.getUserName(),//解說人員姓名
					bondVal:'',									//票面價值
					prodID: '', 								//商品ID
					prodName: '',								//商品名稱
					prodCurr: '',								//計價幣別
					prodRiskLV: '',								//產品風險等級
					prodMinBuyAmt: undefined,					//最低申購面額
					prodMinGrdAmt: undefined,					//累計申購面額
					trustCurrType: 'Y',							//信託幣別類型
					trustCurr: '',								//信託幣別
					trustUnit: undefined,						//庫存張數
					marketType: '2',							//債券市場種類
					refVal: undefined,							//參考報價
					refValDate: undefined,						//參考報價日期
					purchaseAmt: undefined,						//申購金額/庫存金額
					entrustType: '',							//委託價格類型/贖回方式
					entrustAmt: undefined,						//委託價格/贖回價格
					trustAmt: undefined,						//信託本金
					defaultFeeRate: undefined,					//表定手續費率
					advFeeRate: undefined,						//事先申請手續費率
					bargainApplySEQ: '',						//議價編號
					feeRate: undefined,							//手續費率/信託管理費率
					fee: undefined,								//手續費金額/預估信託管理費
					feeDiscount: undefined,						//手續費折數
					payableFee: undefined,						//應付前手息/應收前手息
					payableFeeRate: undefined,					//應付前手息率
					totAmt: undefined,							//總扣款金額/預估贖回入帳金額
//					debitAcct: '',								//扣款帳號
//					trustAcct: '',								//信託帳號
//					creditAcct: '',								//收益入帳帳號/贖回款入帳帳號
					tradeDate: undefined,						//交易日期
					limitedPrice: undefined,
					bestFeeRate: undefined,						//最優手續費率

					warningMsg: '',								//警告訊息
					defaultFeeDiscount: undefined,				//預設折數 (($scope.inputVO.bestFeeRate/$scope.inputVO.defaultFeeRate) * 10).toFixed(3)
					gtcYN: 'N',									//當日單N, 長效單Y
					gtcEndDate: undefined,						//長效單迄日
					trustTS: 'S',								//交易類別:特金S或金錢信託M
					trustPeopNum: 'N',							//是否為多委託人契約
					acctCurrency: '',							//金錢信託-扣款帳號幣別
					isWeb: 'N',									//臨櫃交易N, 快速申購Y
					prodIsWebSale: undefined,					//商品是否開放網銀申購(Y/N)
					buyRate: undefined,							//商品計價幣別最新買匯匯率
					hnwcBuy: '',								//商品:限高資產客戶申購
					hnwcYN: '',									//是否為高資產客戶 Y/N 
					hnwcServiceYN: '',							//可提供高資產商品或服務 Y/N 
					flagNumber: '',								//90天內是否有貸款紀錄 Y/N
					otherWithCustId: false						//是否帶客戶ID進來(快查)
			};
			var custID = $scope.connector('get','ORG110_custID');

			if(custID != undefined){
				$scope.inputVO.custID = custID;
			}

			$scope.avlBalance = undefined;
			$scope.cartList = undefined;
		};
		$scope.init();

		$scope.custClear = function() {
			$scope.inputVO.custName = '';

			$scope.inputVO.kycLV = '';									//KYC等級
			$scope.inputVO.kycDueDate = undefined;						//KYC效期
			$scope.inputVO.profInvestorYN = '';							//是否為專業投資人
			$scope.inputVO.piDueDate = undefined;						//專業投資人效期
			$scope.inputVO.piRemark = '';								//專業投資人註記
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
			$scope.inputVO.w8benEffDate = undefined;					//W8ben有效日期
			$scope.inputVO.w8BenEffYN = '';
			$scope.inputVO.fatcaType = '';
//			$scope.inputVO.isCustStakeholder = undefined;
//			$scope.inputVO.custQValue = '';
			$scope.inputVO.custProType = '';
//			$scope.inputVO.isWeb = 'N';									//臨櫃交易N, 快速申購Y
			$scope.inputVO.hnwcYN = '';
			$scope.inputVO.hnwcServiceYN = '';
			
			$scope.inputVO.custType = 'CUST';							//來行人員
			$scope.inputVO.agentID = '';								//代理人ID
			$scope.inputVO.agentName = '';
			$scope.avlBalance = undefined;                              //扣款帳號餘額
			$scope.avlCurrency = undefined;                             //扣款帳號餘額幣別
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST'] = [];
			$scope.mappingSet['SOT.TRUST_ACCT_LIST'] = [];
			$scope.mappingSet['SOT.CREDIT_ACCT_LIST'] = [];
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST#DISPLAY'] = [];
			$scope.mappingSet['SOT.CREDIT_ACCT_LIST#DISPLAY'] = [];

			// 2020-01-15 ADD BY OCEAN : 金錢信託
			$scope.mappingSet['SOT.CONTRACT_LIST'] = [];			// 契約編號列表
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'] = [];	// 扣款帳號
			$scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'] = [];	// 收益入帳帳號
//			$scope.mappingSet['SOT.TRUST_ACCT_LIST#TRUST'] = [];	// 信託帳號
        };

        $scope.prodClear = function() {
        	$scope.inputVO.bondVal =  '';								//票面價值
        	$scope.inputVO.prodName = '';								//商品名稱
        	$scope.inputVO.prodCurr = '';								//計價幣別
        	$scope.inputVO.prodRiskLV = '';								//產品風險等級
			$scope.inputVO.prodMinBuyAmt = undefined;					//最低申購面額
			$scope.inputVO.prodMinGrdAmt = undefined;					//累計申購面額
			$scope.inputVO.trustCurrType = 'Y';							//信託幣別類型
			$scope.inputVO.trustCurr = '';								//信託幣別
			$scope.inputVO.trustUnit = undefined;						//庫存張數
			$scope.inputVO.marketType = '2';							//債券市場種類
			$scope.inputVO.refVal = undefined;							//參考報價
			$scope.inputVO.refValDate = undefined;						//參考報價日期
			$scope.inputVO.purchaseAmt = undefined;						//申購金額/庫存金額
			$scope.inputVO.entrustType = '';							//委託價格類型/贖回方式
			$scope.inputVO.entrustAmt = undefined;						//委託價格/贖回價格
			$scope.inputVO.trustAmt = undefined;						//信託本金
			$scope.inputVO.defaultFeeRate = undefined;					//表定手續費率
			$scope.inputVO.advFeeRate = undefined;						//事先申請手續費率
			$scope.inputVO.bargainApplySEQ = '';						//議價編號
			$scope.inputVO.feeRate = undefined;							//手續費率/信託管理費率
			$scope.inputVO.fee = undefined;								//手續費金額/預估信託管理費
			$scope.inputVO.feeDiscount = undefined;						//手續費折數
			$scope.inputVO.payableFee = undefined;						//應付前手息/應收前手息
			$scope.inputVO.payableFeeRate = undefined;					//應付前手息率
			$scope.inputVO.totAmt = undefined;							//總扣款金額/預估贖回入帳金額
			$scope.inputVO.tradeDate = undefined;						//交易日期
			$scope.inputVO.limitedPrice = undefined;
			$scope.inputVO.bestFeeRate = undefined;						//最優手續費率
			$scope.inputVO.gtcYN = 'N';									//當日單N, 長效單Y
			$scope.inputVO.gtcEndDate = undefined;						//長效單迄日
			$scope.nvlAMT = undefined;
			$scope.sumITEM = undefined;
			$scope.inputVO.hnwcBuy = '';

			$scope.cartList = undefined;//購物車
        }

        //按[清除]鍵   清除序號
        $scope.clearTradeSEQ = function(){
        	$scope.inputVO.tradeSEQ='';
        	$scope.query();
        }

		// if data
		$scope.inputVO.tradeSEQ = $scope.connector('get','SOTTradeSEQ');
		$scope.inputVO.carSEQ = $scope.connector('get','SOTCarSEQ');
		$scope.connector('set', 'SOTTradeSEQ', null);
		$scope.connector('set', 'SOTCarSEQ', null);

		// getFee
		$scope.getFee = function(type) {
			if($scope.inputVO.purchaseAmt != undefined){
			    $scope.inputVO.purchaseAmt=$scope.moneyUnFormat($scope.inputVO.purchaseAmt);
			}

//			if(typeof($scope.inputVO.feeRate) == 'undefined' || $scope.inputVO.feeRate<=0 || $scope.inputVO.feeRate==''|| $scope.inputVO.feeRate==null || $scope.inputVO.feeRate>$scope.inputVO.defaultFeeRate || $scope.inputVO.feeDiscount >10){
				//判斷若優惠後手續費率大於表定手續費率則顯示警示訊息
			if($scope.inputVO.defaultFeeRate == $scope.inputVO.bestFeeRate ){
				if($scope.inputVO.feeRate > $scope.inputVO.bestFeeRate ||( $scope.inputVO.feeDiscount != null && $scope.inputVO.feeDiscount != undefined && $scope.inputVO.feeDiscount > 10 ) ){
					$scope.showErrorMsg("ehl_02_SOT_011");
					$scope.inputVO.feeRate = $scope.inputVO.bestFeeRate;  //輸入手續費後，再清掉，手續費應該要以最優手續費計算
					$scope.inputVO.feeDiscount =_.round(($scope.inputVO.bestFeeRate/$scope.inputVO.defaultFeeRate) * 10, 3) ; //折數 = 手續費率(number) ÷ 表定手續費率 × 10
				}
			}else {
				if($scope.inputVO.feeRate > $scope.inputVO.bestFeeRate || $scope.inputVO.feeDiscount > 10 || $scope.inputVO.feeDiscount > $scope.inputVO.defaultFeeDiscount){
					$scope.showErrorMsg("ehl_02_SOT_011");
					$scope.inputVO.feeRate = $scope.inputVO.bestFeeRate;  //輸入手續費後，再清掉，手續費應該要以最優手續費計算
					$scope.inputVO.feeDiscount =_.round(($scope.inputVO.bestFeeRate/$scope.inputVO.defaultFeeRate) * 10, 3); //折數 = 手續費率(number) ÷ 表定手續費率 × 10
				}
			}

//			}

			if (type == 'rate') {
//				debugger;

				var feeRateDecimalPlaces = ($scope.inputVO.feeRate.toString()).split(".")[1];

				if(feeRateDecimalPlaces && feeRateDecimalPlaces.length>5){
					$scope.showErrorMsg("手續費率僅可輸入至小數5位");
					$scope.inputVO.feeRate=$scope.inputVO.bestFeeRate;
				}

				$scope.rate = (typeof($scope.inputVO.feeRate) !== 'undefined' ?  $scope.inputVO.feeRate : (typeof($scope.inputVO.bestFeeRate) !== 'undefined' ? $scope.inputVO.bestFeeRate : $scope.inputVO.defaultFeeRate));

				const fee = (($scope.inputVO.trustAmt * $scope.rate) / 100);
				$scope.inputVO.fee = isNaN(fee)? null: _.round(fee, 2); //手續費金額= 信託本金 × 手續費率
				if($scope.inputVO.feeRate == $scope.inputVO.defaultFeeRate){
					$scope.inputVO.feeDiscount = null; //cathy:如果優惠後手續費=表定手續費，不需要顯示折數，因為沒有10折這種折數。
				}else if (typeof($scope.inputVO.feeRate) !== 'undefined') {
					$scope.inputVO.feeDiscount = _.round(($scope.rate/$scope.inputVO.defaultFeeRate) * 10, 3); //折數 = 手續費率(number) ÷ 表定手續費率 × 10
				}
			} else if (type == 'feeDiscount') {
				if(typeof($scope.inputVO.feeDiscount) == 'undefined' || $scope.inputVO.feeDiscount<=0 || $scope.inputVO.feeDiscount==''|| $scope.inputVO.feeDiscount==null ||$scope.inputVO.feeDiscount >= 10){
					$scope.inputVO.feeRate=$scope.inputVO.bestFeeRate;  //cathy:清掉折數欄位資料，優惠後手續費率應帶出"最優手續費率"並算出手續費

					const feeDiscount = _.round(($scope.rate/$scope.inputVO.defaultFeeRate) * 10, 3);
					$scope.inputVO.feeDiscount = isNaN(feeDiscount) ? null : feeDiscount;
				}else if (typeof($scope.inputVO.feeDiscount) !== 'undefined') {
					$scope.inputVO.feeRate = _.round(($scope.inputVO.feeDiscount * $scope.inputVO.defaultFeeRate) / 10, 5) ; //手續費率 = (折數(number) × 表定手續費) ÷ 10
				}
				const fee = ($scope.inputVO.trustAmt * $scope.inputVO.feeRate) / 100;
				$scope.inputVO.fee =isNaN(fee) ? null : _.round(fee, 2) ;//手續費金額= 信託本金 × 手續費率
			}

			if($scope.inputVO.feeDiscount ==10){//cathy:如果優惠後手續費=表定手續費，不需要顯示折數，因為沒有10折這種折數。
				$scope.inputVO.feeDiscount = null;
		    }

			$scope.inputVO.payableFee = isNaN(($scope.inputVO.payableFeeRate * $scope.inputVO.purchaseAmt) / 100)?null:($scope.inputVO.payableFeeRate * $scope.inputVO.purchaseAmt) / 100;


			$scope.inputVO.totAmt = Number($scope.inputVO.trustAmt) + Number($scope.inputVO.payableFee) + Number($scope.inputVO.fee);
			if($scope.inputVO.purchaseAmt != undefined){
			    $scope.inputVO.purchaseAmt=$scope.moneyFormat($scope.inputVO.purchaseAmt);
			}
		};


		//1.換扣款帳號和2.換商品都要查詢 該帳號幣別餘額
		$scope.changeAcct = function (type) {
//			debugger;
			var debitAcct = $scope.inputVO.debitAcct.split("_");
			if (type == 'debit') {
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

			//取得 相同商品別的 扣款帳後餘額
			$scope.avlBalance = undefined;                              //扣款帳號餘額
			$scope.avlCurrency = undefined;                             //扣款帳號餘額幣別
			//有幣別和餘額
			angular.forEach($scope.mappingSet['SOT.DEBIT_ACCT_LIST#DISPLAY'], function(acctCcyRow){
					if ($scope.inputVO.debitAcct == acctCcyRow.DATA) {
						$scope.avlBalance = acctCcyRow.AVL_BALANCE;
						$scope.avlCurrency = acctCcyRow.CURRENCY;
						//			console.log('debitAcct acct:'+acctCcyRow.DATA+' currency:'+$scope.avlCurrency +', avlBalance:'+$scope.avlBalance);
					}
			});
		};

	  //查詢帳號幣別
	  $scope.getAcctCurrency = function() {
		 var trustCurrType = $scope.inputVO.trustCurrType;
		 var prodCurrency = $scope.inputVO.prodCurr;
		 var acctCcy = undefined;
			if (trustCurrType && prodCurrency){ //有傳信託業務別 和 商品幣別
	 		   if(trustCurrType=='C' || trustCurrType=='N'){
	 			   acctCcy = 'TWD';
	 		   }else if (trustCurrType=='Y'){  //Y外幣
	 			   acctCcy = prodCurrency;
	 		   }
			}
			return acctCcy;
	   };



	   /*
	   $scope.acctDisabledStatus = false;

	   $scope.checkTrustAcct168 = function () {
			if ($scope.inputVO.trustAcct.substr(3, 5) == "168") {
				debitAcct = trustAcct;
				creditAcct = trustAcct;
				　
				$scope.acctDisabledStatus = true;
			}
	   }*/

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

	   //檢查信託帳號
	   $scope.checkTrustAcct = function(){

			$scope.cmbDebitAcct=false;
			$scope.cmbCreditAcct=false;
			$scope.avlBalance = undefined;                              //扣款帳號餘額
			$scope.avlCurrency = undefined;                             //扣款帳號餘額幣別
			var debitAcctListDisplay='SOT.DEBIT_ACCT_LIST#DISPLAY';
			var creditAcctListDisplay='SOT.CREDIT_ACCT_LIST#DISPLAY';
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
																		  AVL_BALANCE:'0',
																		  CURRENCY:$scope.inputVO.prodCurr,
																		  label:row.DATA+'_'+$scope.inputVO.prodCurr,
																		  value:row.DATA+'_'+$scope.inputVO.prodCurr});
						}
					}
				});
			}

			//信託帳號檢核

			if ($scope.inputVO.trustAcct && sotService.is168($scope.inputVO.trustAcct)) {
				$scope.mappingSet[debitAcctListDisplay] = $filter('filter')($scope.mappingSet[debitAcctListDisplay],
																		{DATA: $scope.inputVO.trustAcct},
																		function(actual, expected) { return angular.equals(actual.split("_")[0], expected)}
																	  );
				$scope.mappingSet[creditAcctListDisplay] = $filter('filter')($scope.mappingSet[creditAcctListDisplay],
																		{DATA: $scope.inputVO.trustAcct},
																		function(actual, expected) { return angular.equals(actual.split("_")[0], expected)}
																	   );
			}
			//有傳信託業務別 和 商品幣別
			if ($scope.inputVO.trustCurrType){
	 		   if($scope.inputVO.trustCurrType=='N'){ //N台幣
	 			 $scope.mappingSet[debitAcctListDisplay] = $filter('filter')($scope.mappingSet[debitAcctListDisplay], {CURRENCY: 'TWD'});
//	 			 $scope.mappingSet[creditAcctListDisplay] = $filter('filter')($scope.mappingSet[creditAcctListDisplay], {DATA: $scope.inputVO.trustAcct});
	 		   }else if($scope.inputVO.prodCurr){	  //計價
	 			  $scope.mappingSet[debitAcctListDisplay] = $filter('filter')($scope.mappingSet[debitAcctListDisplay], {CURRENCY: $scope.inputVO.prodCurr});
//		 		  $scope.mappingSet[creditAcctListDisplay] = $filter('filter')($scope.mappingSet[creditAcctListDisplay], {DATA: $scope.inputVO.trustAcct});

	 		   }else if ($scope.inputVO.trustCurrType=='Y'){  //Y外幣
	 			  $scope.mappingSet[debitAcctListDisplay] = $filter('filter')($scope.mappingSet[debitAcctListDisplay],{CURRENCY: "TWD"},function(actual, expected) {  return !angular.equals(actual, expected)});
//	 			  $scope.mappingSet[creditAcctListDisplay] = $filter('filter')($scope.mappingSet[creditAcctListDisplay],{DATA: $scope.inputVO.trustAcct});
				}
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

		$scope.custInfo = function (row) {
			$scope.inputVO.custName = row.custName;
			$scope.inputVO.kycLV = row.kycLV;									//KYC等級
			$scope.inputVO.kycDueDate = $scope.toJsDate(row.kycDueDate);		//KYC效期
			$scope.inputVO.profInvestorYN = row.profInvestorYN;					//是否為專業投資人
			$scope.inputVO.piDueDate = $scope.toJsDate(row.piDueDate);			//專業投資人效期
			$scope.inputVO.piRemark = row.piRemark;								//專業投資人註記
			$scope.inputVO.custRemarks = row.custRemarks;						//客戶註記
			$scope.inputVO.isOBU = row.isOBU;									//是否為OBU客戶
			$scope.inputVO.isAgreeProdAdv = row.isAgreeProdAdv;					//同意投資商品諮詢服務
			$scope.inputVO.bargainDueDate =  row.bargainDueDate;				//期間議價效期
			$scope.inputVO.plNotifyWays = row.plNotifyWays;						//停損停利通知方式
			$scope.inputVO.takeProfitPerc = row.takeProfitPerc;					//停利點
			$scope.inputVO.stopLossPerc = row.stopLossPerc;						//停損點
			$scope.inputVO.w8benEffDate = $scope.toJsDate(row.w8benEffDate);	//W8ben有效日期
			$scope.inputVO.w8BenEffYN = row.w8BenEffYN;
			$scope.inputVO.fatcaType = row.fatcaType;
//			$scope.inputVO.isCustStakeholder = row.isCustStakeholder;
//			$scope.inputVO.custQValue = row.custQValue;
			$scope.inputVO.custProType = row.custProType;						//專投種類
			$scope.inputVO.hnwcYN = row.hnwcYN;
			$scope.inputVO.hnwcServiceYN = row.hnwcServiceYN;
			$scope.inputVO.flagNumber = row.flagNumber;							//90天內是否有貸款紀錄 Y/N
			
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST'] = row.debitAcct;
			$scope.mappingSet['SOT.TRUST_ACCT_LIST'] = row.trustAcct;
			$scope.mappingSet['SOT.CREDIT_ACCT_LIST'] = row.creditAcct;

			$scope.mappingSet['SOT.DEBIT_ACCT_LIST#DISPLAY'] = $scope.mappingSet['SOT.DEBIT_ACCT_LIST'];
			$scope.mappingSet['SOT.CREDIT_ACCT_LIST#DISPLAY'] = $scope.mappingSet['SOT.CREDIT_ACCT_LIST'];



			$scope.inputVO.debitAcct = (row.debitAcct.length > 0 ? row.debitAcct[0].LABEL : "");				//扣款帳號
			$scope.avlBalance = (row.debitAcct.length > 0 ? row.debitAcct[0].AVL_BALANCE : "");					//扣款帳號餘額
			$scope.avlCurrency = (row.debitAcct.length > 0 ? row.debitAcct[0].CURRENCY : "");	                //扣款帳號餘額幣別
			$scope.inputVO.trustAcct = (row.trustAcct.length > 0 ? row.trustAcct[0].LABEL : "");				//信託帳號
			$scope.inputVO.creditAcct = (row.creditAcct.length > 0 ? row.creditAcct[0].LABEL : "");			    //收益入帳帳號
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
			if(input){
				$scope.custClear();
				$scope.prodClear();
				$scope.inputVO.prodID = "";
				//TODO先不清除 $scope.inputVO.tradeSEQ = undefined;
			}
			var validCustID = validateService.checkCustID($scope.inputVO.custID); //自然人和法人檢查
			//			console.log("custID:" + $scope.inputVO.custID + ", checkCustID: "+validCustID);

			if(validCustID==false) {
				$scope.inputVO.custID='';
				$scope.custClear();
				$scope.prodClear();
				$scope.inputVO.prodID = "";
			}else if(validCustID) {
				$scope.custClear();
				$scope.prodClear();
				$scope.sendRecv("SOT310", "getSOTCustInfo", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO", {'custID':$scope.inputVO.custID, 'prodType':3, 'tradeType':1, 'trustTS':$scope.inputVO.trustTS},
						function(tota, isError) {
							if (!isError) {
								$scope.toDay = $filter('date')(new Date(),'yyyy-MM-dd 00:00:00');//取當日日期
								if ($scope.inputVO.trustTS == 'S' && (tota[0].body.custName == null || tota[0].body.custName == "" || tota[0].body.trustAcct.length == 0)) { //若非為本行客戶或信託客戶，需有訊息告知非為本行客戶或未開立信託戶，並須擋下單。
									$scope.showErrorMsg("ehl_01_sot310_002");
									$scope.custClear();
									$scope.inputVO.custID = "";
								} else if (tota[0].body.noSale == "Y") { //若為禁銷客戶，出現提示訊息禁止下單。
									$scope.showErrorMsg("ehl_01_sot310_003");
									$scope.custClear();
									$scope.inputVO.custID = "";
								} else if (tota[0].body.deathFlag == "Y" || tota[0].body.isInterdict == "Y") { //若為死亡戶/禁治產等狀況，不可下單。
									$scope.showErrorMsg("ehl_01_sot310_004");
									$scope.custClear();
									$scope.inputVO.custID = "";
								}
								//FOR CBS TEST日期
//								else if (!tota[0].body.kycDueDate || $scope.toJsDate(tota[0].body.kycDueDate) < $scope.toJsDate($scope.toDay)) { //若KYC過期，需有訊息提示客戶，並清空客戶ID。
								else if(tota[0].body.isKycDueDateUseful){
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
								} else {
									//FOR CBS TEST日期
									if (tota[0].body.rejectProdFlag == "Y" || tota[0].body.isPiDueDateUseful ) {  //$scope.toJsDate(tota[0].body.piDueDate) < $scope.toJsDate($scope.toDay)
										var txtMsg = "";
										if (tota[0].body.rejectProdFlag == "Y") {
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
													$scope.custInfo(tota[0].body);
													deferred.resolve("success");  //若為拒銷客戶，出現警告訊息問是否繼續，選"確定"，但客戶ID不能被清掉。
													return deferred.promise;
												} else {
													$scope.custClear();
													$scope.inputVO.custID = "";
													deferred.resolve("");
												}
											});
											//FOR CBS TEST日期
										} else if (!tota[0].body.piDueDate || tota[0].body.isPiDueDateUseful ) {  //$scope.toJsDate(tota[0].body.piDueDate) < $scope.toJsDate($scope.toDay)
//											txtMsg = $filter('i18n')('ehl_01_sot310_006');
//											$scope.showErrorMsg(txtMsg);
											$scope.custInfo(tota[0].body);  //警告:專業投資人效期已過期，但不擋下單
											deferred.resolve("success");
											return deferred.promise;
										} else if (tota[0].body.profInvestorYN=="N"){  //非專投:也要可以
											$scope.custInfo(tota[0].body);
											deferred.resolve("success");
											return deferred.promise;
										}
									} else {
										$scope.custInfo(tota[0].body);

										deferred.resolve("success");

										return deferred.promise;
									}
								}
							} else {
								$scope.inputVO.custID = "";
								$scope.custClear();
							}
				});
			}

			return deferred.promise;
		};


		/*選擇信託幣別
		$scope.currencyType = function() {
			$scope.avlCurrency = undefined;
			$scope.avlBalance = undefined;
		};
		*/

		$scope.setCustAcctData = function(custAcct){

			$scope.mappingSet['SOT.DEBIT_ACCT_LIST#DISPLAY'] = $scope.mappingSet['SOT.DEBIT_ACCT_LIST'];
			$scope.mappingSet['SOT.CREDIT_ACCT_LIST#DISPLAY'] = $scope.mappingSet['SOT.CREDIT_ACCT_LIST'];

			$scope.checkTrustAcct();//信託帳號168, 則 扣款和收益要168
		}

		$scope.noCallCustQuery = function () {
			var deferred = $q.defer();

			$scope.sendRecv("SOT310", "query", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO",{'tradeSEQ':$scope.inputVO.tradeSEQ} ,
					function(tota, isError) {
						if (!isError) {
							debugger
							$scope.cartList = tota[0].body.carList;
							$scope.outputVO = tota[0].body;

							$scope.inputVO.narratorID = tota[0].body.carList[0].NARRATOR_ID;		//解說人員ID
							$scope.inputVO.narratorName = tota[0].body.carList[0].NARRATOR_NAME;	//解說人員姓名

							$scope.inputVO.custID = tota[0].body.mainList[0].CUST_ID;				//客戶ID
							$scope.inputVO.trustTS = tota[0].body.mainList[0].TRUST_TRADE_TYPE;		//信託交易類別
							$scope.inputVO.isWeb = tota[0].body.mainList[0].IS_WEB;					//是否為網銀快速申購(Y/N)

							$scope.getSOTCustInfo().then(function(data) {
								$scope.inputVO.prodID = tota[0].body.carList[0].PROD_ID;

								$scope.getProdDTL().then(function(data) {
									$scope.inputVO.bondVal =  tota[0].body.carList[0].BOND_VALUE;
									$scope.inputVO.prodName = tota[0].body.carList[0].PROD_NAME;						//商品名稱
						        	$scope.inputVO.prodCurr = tota[0].body.carList[0].PROD_CURR; 						//計價幣別
						        	$scope.inputVO.prodRiskLV = tota[0].body.carList[0].PROD_RISK_LV; 					//產品風險等級

						        	$scope.inputVO.prodMinBuyAmt = tota[0].body.carList[0].PROD_MIN_BUY_AMT;			//最低申購面額
									$scope.inputVO.prodMinGrdAmt = tota[0].body.carList[0].PROD_MIN_GRD_AMT;			//累計申購面額
									$scope.inputVO.trustCurrType = tota[0].body.carList[0].TRUST_CURR_TYPE;				//信託幣別類型
									$scope.inputVO.trustCurr = tota[0].body.carList[0].TRUST_CURR;						//信託幣別
									$scope.inputVO.trustUnit = tota[0].body.carList[0].TRUST_UNIT;						//庫存張數
									$scope.inputVO.marketType = tota[0].body.carList[0].MARKET_TYPE;					//債券市場種類
									$scope.inputVO.refVal = tota[0].body.carList[0].REF_VAL;							//參考報價
									$scope.inputVO.refValDate = $scope.toJsDate(tota[0].body.carList[0].REF_VAL_DATE);	//參考報價日期
									$scope.inputVO.purchaseAmt = tota[0].body.carList[0].PURCHASE_AMT;					//申購金額/庫存金額
									if($scope.inputVO.purchaseAmt != undefined){
										$scope.inputVO.purchaseAmt=$scope.moneyFormat($scope.inputVO.purchaseAmt);
									}
									$scope.inputVO.entrustType = tota[0].body.carList[0].ENTRUST_TYPE;					//委託價格類型/贖回方式
									$scope.initQuery_entrustType=true;
									$scope.inputVO.entrustAmt = tota[0].body.carList[0].ENTRUST_AMT;					//委託價格/贖回價格
									$scope.inputVO.trustAmt = tota[0].body.carList[0].TRUST_AMT;						//信託本金
									$scope.inputVO.defaultFeeRate = tota[0].body.carList[0].DEFAULT_FEE_RATE;			//表定手續費率
									$scope.inputVO.advFeeRate = tota[0].body.carList[0].ADV_FEE_RATE;					//事先申請手續費率
									$scope.inputVO.bestFeeRate = tota[0].body.carList[0].BEST_FEE_RATE;					//最優手續費率
									$scope.inputVO.bargainApplySEQ = tota[0].body.carList[0].BARGAIN_APPLY_SEQ;			//議價編號
									$scope.inputVO.feeRate = tota[0].body.carList[0].FEE_RATE;							//手續費率/信託管理費率
									$scope.inputVO.fee = tota[0].body.carList[0].FEE;									//手續費金額/預估信託管理費
									$scope.inputVO.feeDiscount = tota[0].body.carList[0].FEE_DISCOUNT;					//手續費折數
									$scope.inputVO.payableFee = tota[0].body.carList[0].PAYABLE_FEE;					//應付前手息/應收前手息
									$scope.inputVO.totAmt = tota[0].body.carList[0].TOT_AMT;							//總扣款金額/預估贖回入帳金額
									$scope.inputVO.tradeDate = $scope.toJsDate(tota[0].body.carList[0].TRADE_DATE);		//交易日期
									var acctCurrency = $scope.getAcctCurrency();
									$scope.inputVO.debitAcct = tota[0].body.carList[0].DEBIT_ACCT + '_' + acctCurrency;	//扣款帳號
									$scope.inputVO.trustAcct = tota[0].body.carList[0].TRUST_ACCT;						//信託帳號
									$scope.initQuery_trustAcct = true;
									$scope.inputVO.creditAcct = tota[0].body.carList[0].CREDIT_ACCT + '_' + acctCurrency;//收益入帳帳號

									$scope.inputVO.gtcYN = tota[0].body.carList[0].GTC_YN;
									$scope.inputVO.gtcStartDate = $scope.toJsDate(tota[0].body.carList[0].GTC_START_DATE);
									$scope.inputVO.gtcEndDate = $scope.toJsDate(tota[0].body.carList[0].GTC_END_DATE);

									// 金錢信託
									$scope.inputVO.contractID = tota[0].body.carList[0].CONTRACT_ID;					//契約編號
									$scope.inputVO.trustPeopNum = tota[0].body.carList[0].TRUST_PEOP_NUM;				//是否為多委託人契約 Y=是/N=否
								});
							});

							deferred.resolve("success");
						}
			});

			return deferred.promise;
		};

		$scope.go711FunctionName = function (type, functionName) {
			var deferred = $q.defer();

			var custIdTemp = "99331241";
			if (type == 'M' && functionName != 'getDefaultFeeRateData') {
				custIdTemp = $scope.inputVO.custID;
				if($scope.inputVO.purchaseAmt != undefined){
				    $scope.inputVO.purchaseAmt=$scope.moneyUnFormat($scope.inputVO.purchaseAmt);
				}
			} else if (type == 'S') {
				custIdTemp = $scope.inputVO.custID;
			}

			var debitAcctAry = $scope.inputVO.debitAcct.split("_");
			var debitAccount = "";
			if(debitAcctAry && debitAcctAry.length > 0) {
				debitAccount = debitAcctAry[0];
			}

			$scope.sendRecv("SOT711", $scope.sot711functionName, "com.systex.jbranch.app.server.fps.sot711.SOT711InputVO", {'custId': custIdTemp,
																															'bondNo': $scope.inputVO.prodID,
																															'priceType': $scope.inputVO.entrustType,
																															'entrustAmt': $scope.inputVO.entrustAmt,
																															'purchaseAmt': $scope.inputVO.purchaseAmt,
																															'txFeeType': '1',
																															'trustAcct': $scope.inputVO.trustAcct,
																															'debitAcct': debitAccount,
																															'isOBU':$scope.inputVO.isOBU},
				function(tota1, isError) {
					if (!isError) {
						$scope.inputVO.payableFeeRate = tota1[0].body.payableFeeRate;

						if($scope.initQuery_trustAcct){//html ng-change會引響資料，判斷若為query function給值則return;
							$scope.initQuery_trustAcct = false;
							return;
						}

						if (functionName == 'getDefaultFeeRateData' || functionName == 'getDefaultFeeRateDataWeb') { // 特金的路
							$scope.inputVO.defaultFeeRate = tota1[0].body.defaultFeeRate;
							$scope.inputVO.bestFeeRate = tota1[0].body.bestFeeRate;
						}

						$scope.inputVO.defaultFeeDiscount = _.round(($scope.inputVO.bestFeeRate/$scope.inputVO.defaultFeeRate) * 10, 3);

						//手續費預設為最優手續費
						$scope.inputVO.feeRate = tota1[0].body.bestFeeRate;

						//取得折數
						$scope.getFee('rate');

						if (type = 'S') {
							$scope.checkTrustAcct();
						} else {

						}

						deferred.resolve("success");
					}
				}
			);

			return deferred.promise;
		}

		$scope.getDefaultFeeRateData = function () {
			var deferred = $q.defer();
			if($scope.inputVO.purchaseAmt != undefined){
			    $scope.inputVO.purchaseAmt=$scope.moneyUnFormat($scope.inputVO.purchaseAmt);
			}

			if ($scope.inputVO.custID && $scope.inputVO.prodID && $scope.inputVO.entrustType && $scope.inputVO.entrustAmt && $scope.inputVO.purchaseAmt && $scope.inputVO.trustAcct) {
				if($scope.inputVO.isWeb == 'Y') {	//快速申購手續費電文
					$scope.sot711functionName = "getDefaultFeeRateDataWeb";
				} else {
					$scope.sot711functionName = "getDefaultFeeRateData";//預設手續費電文
				}
				$scope.go711FunctionName($scope.inputVO.trustTS, $scope.sot711functionName).then(function() {
					if ($scope.inputVO.trustTS == 'M') {
						$scope.sot711functionName = "getDefaultFeeRateDataByTrust";//金錢信託手續費電文

						$scope.go711FunctionName($scope.inputVO.trustTS, $scope.sot711functionName).then(function() {
							deferred.resolve("success");
						});
					} else {
						deferred.resolve("success");
					}
				});
			}

			if($scope.inputVO.purchaseAmt != undefined){
				$scope.inputVO.purchaseAmt=$scope.moneyFormat($scope.inputVO.purchaseAmt);
			}

			return deferred.promise;
		}

		$scope.getEntrustAmt = function (flag) {
//			$scope.limitedPrice = $scope.inputVO.limitedPrice;

			if($scope.initQuery_entrustType){	//html ng-change會引響資料，判斷若為query function給值則return;
				$scope.initQuery_entrustType=false;
				return;
			}
			if($scope.inputVO.purchaseAmt != undefined){
				$scope.inputVO.purchaseAmt = $scope.moneyUnFormat($scope.inputVO.purchaseAmt);
			}
			if (true) {
				
				if (flag) {
					$scope.entrustAmtDisabled = false;
					$scope.entrustDisabled = false;

					if ($scope.inputVO.gtcYN == "Y" || $scope.inputVO.gtcYN == "P") {
//						angular.forEach($scope.mappingSet['SOT.BN_CUR_LIMIT'], function(row){
//							if ($scope.inputVO.prodCurr == row.DATA && $scope.inputVO.purchaseAmt < Number(row.LABEL)) {
//								//長效單申購面額須達{0}{1}
//								$scope.showErrorMsg('ehl_01_sot310_010',[row.DATA,$scope.moneyFormat(row.LABEL)]);
//
//								$scope.inputVO.gtcYN = "N";
//								$scope.inputVO.gtcEndDate = undefined;
//								$scope.getEntrustAmt(true);
//								return;
//							}
//						});

						/**
						 * 202406_美芳提供之新規則如下
						 * 
						 * 長效單：『委託價格』disable及default為"限價"，無限制最低申購面額
						 * 預約單：『委託價格』enable 及default為"市價"，價格可約定指定交易日當日市價或買進限價
						 * 
						 * (選擇預約交易或長效單時，須傳送『市價』flag至AS400)
						 * **/
						if ($scope.inputVO.gtcYN == "Y") {
							// 長效單
							$scope.entrustDisabled = true;
							$scope.inputVO.entrustType = "1";	// 限價
							
						} else if($scope.inputVO.gtcYN == "P") {
							// 預約單
							$scope.entrustDisabled = false;
						}
						
						$scope.changeFeeDisabled = false;
						$scope.getDefaultFeeRateData().then(function(data) {
							$scope.getFee('rate');
						});
//						//不可議價
//						$scope.changeFee = undefined;
//						$scope.changeFeeDisabled = true;
//						$scope.getDefaultFeeRateData().then(function(data) {
//							$scope.getFee('rate');
//						});
					} else { //當日單
						$scope.changeFeeDisabled = false;

						angular.forEach($scope.mappingSet['SOT.BN_CUR_LIMIT'], function(row){
							if ($scope.inputVO.prodCurr == row.DATA && $scope.inputVO.purchaseAmt < Number(row.LABEL)) {
								$scope.sendRecv("SOT310", "limitPrice", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO",
								{'custID':$scope.inputVO.custID, 'prodID':$scope.inputVO.prodID} ,
								function(tota, isError) {
									if (!isError) {
										if(tota[0].body.limitPrice){
											if($scope.inputVO.entrustType == '1'){
												$scope.entrustAmtDisabled = true;
												$scope.inputVO.entrustAmt = $scope.inputVO.limitedPrice;
												$scope.inputVO.purchaseAmt = $scope.moneyUnFormat($scope.inputVO.purchaseAmt);

												$scope.inputVO.trustAmt = parseInt($scope.inputVO.purchaseAmt) * $scope.inputVO.entrustAmt / 100;

												if ($scope.inputVO.entrustType != "") {
													$scope.getDefaultFeeRateData().then(function(data) {
														$scope.getFee('rate');
													});
												}
											}
										} else {
											$scope.inputVO.entrustType = "2";
											$scope.entrustDisabled = true;
										}
									}
								});
							} else if ($scope.inputVO.prodCurr == row.DATA && $scope.inputVO.purchaseAmt >= Number(row.LABEL)) {
								$scope.sendRecv("SOT310", "limitPrice", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO",
								{'custID':$scope.inputVO.custID, 'prodID':$scope.inputVO.prodID} ,
								function(tota, isError) {
									if (!isError) {
										if(tota[0].body.limitPrice){
											if ($scope.inputVO.entrustType == '1') {
												$scope.inputVO.entrustAmt = $scope.inputVO.limitedPrice;
												$scope.inputVO.purchaseAmt = $scope.moneyUnFormat($scope.inputVO.purchaseAmt);
												$scope.inputVO.trustAmt = $scope.inputVO.purchaseAmt * $scope.inputVO.entrustAmt / 100;

												if ($scope.inputVO.entrustType != "") {
													$scope.getDefaultFeeRateData().then(function(data) {
														$scope.getFee('rate');
													});
												}
											}
										} else {
											if ($scope.inputVO.entrustType == '1') {
												$scope.inputVO.entrustAmt = undefined;
												$scope.inputVO.purchaseAmt = $scope.moneyUnFormat($scope.inputVO.purchaseAmt);
												$scope.inputVO.trustAmt = $scope.inputVO.purchaseAmt * $scope.inputVO.entrustAmt / 100;

												if ($scope.inputVO.entrustType != "") {
													$scope.getDefaultFeeRateData().then(function(data) {
														$scope.getFee('rate');
													});
												}
											}
										}
									}
								});
							}
						});
					}
				} else if(!flag && $scope.inputVO.gtcYN != "N") { 
					// 長效單/預約單，修改限價價格
					if ($scope.inputVO.entrustAmt == 0) {
						$scope.inputVO.entrustAmt = undefined;
						$scope.showErrorMsg("限價價格不可為0");
						return;
					}
					
					var rangelist = $filter('filter')($scope.mappingSet["SOT.BN_GTC_LIMITPRICE_RANGE"], "1");	//參數取得"限價超過參考報價的正負N"
					var range = 3;
					//若無參數預設為3
					if(range != undefined && range != null) range = rangelist[0].LABEL;

					if(Math.abs($scope.inputVO.entrustAmt - $scope.inputVO.refVal) > range ) {
						var txtMsg = $filter('i18n')('ehl_02_SOT_013');		//限價超過參考報價的正負3，請確認是否繼續下單。
						$scope.showWarningMsg(txtMsg, range);
					}
				}

				if ($scope.inputVO.entrustType == "1" && !flag){
					$scope.inputVO.trustAmt = $scope.inputVO.purchaseAmt * $scope.inputVO.entrustAmt / 100;

					if ($scope.inputVO.entrustType != "") {
						$scope.getDefaultFeeRateData().then(function(data) {
							$scope.getFee('rate');
						});
					}

				} else if ($scope.inputVO.entrustType == "2"){
					//長效單市價可修改委託價格
					if($scope.inputVO.gtcYN != "Y") {
						$scope.inputVO.entrustAmt = $scope.inputVO.refVal;
					} else {
						if($scope.inputVO.entrustAmt == undefined || $scope.inputVO.entrustAmt == null || $scope.inputVO.entrustAmt == "")
							$scope.inputVO.entrustAmt = $scope.inputVO.refVal;
					}

					$scope.inputVO.purchaseAmt = $scope.moneyUnFormat($scope.inputVO.purchaseAmt);
					$scope.inputVO.trustAmt = $scope.inputVO.purchaseAmt * $scope.inputVO.entrustAmt / 100;

					if ($scope.inputVO.entrustType != "") {
						$scope.getDefaultFeeRateData().then(function(data) {
							$scope.getFee('rate');
						});
					}
				}

				//快速申購固定為"市價"
				if($scope.inputVO.isWeb == "Y") {
					$scope.inputVO.entrustType = "2";
					$scope.inputVO.entrustAmt = $scope.inputVO.refVal;
					$scope.entrustAmtDisabled = true;
					$scope.entrustDisabled = true;
				}

//				if ($scope.inputVO.entrustType != "") {
//					$scope.getDefaultFeeRateData().then(function(data) {
//						$scope.getFee('rate');
//					});
//				}
			} else {
				$scope.inputVO.purchaseAmt = undefined;
				$scope.inputVO.entrustType = "";
				$scope.inputVO.entrustAmt = undefined;
				$scope.inputVO.trustAmt = undefined;

				if($scope.inputVO.prodMinBuyAmt && $scope.inputVO.prodMinGrdAmt){
					if ($scope.inputVO.custID !='' && $scope.inputVO.trustAmt != undefined) {  //TODO ?
						$scope.showErrorMsg("ehl_01_sot310_007");
					} else if ($scope.inputVO.custID !='' && $scope.inputVO.purchaseAmt == undefined  && flag) {
						$scope.showErrorMsg("ehl_01_sot310_007");  //申購面額須高於最低申購面額，且須為累進申購面額之倍數。
					}
				}
			}
			if($scope.inputVO.purchaseAmt != undefined){
				$scope.inputVO.purchaseAmt=$scope.moneyFormat($scope.inputVO.purchaseAmt);
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
			$scope.inputVO.prodID = $filter('uppercase')($scope.inputVO.prodID);
			if($scope.inputVO.prodID) {
				if (!$scope.fromFPS) {
					$scope.prodClear();
				}
				$scope.sendRecv("SOT310", "getProdDTL", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO",{'custID':$scope.inputVO.custID,'prodID':$scope.inputVO.prodID, 'trustTS': $scope.inputVO.trustTS},
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
	//											$scope.custInfo(tota[0].body);
	//											deferred.resolve("success");
	//											return deferred.promise;
											} else {
												$scope.inputVO.prodID = "";
												$scope.prodClear();
												deferred.resolve("");
											}
										});
									}

									if($scope.inputVO.isWeb == 'Y' && tota[0].body.isWebSale == "N") {
										$scope.showErrorMsg("此商品不開放快速申購。");//商品註記為不開放網銀快速申購
										$scope.inputVO.prodID = "";
										$scope.prodClear();
										deferred.resolve("");
										return;
									}

									$scope.inputVO.bondVal =  tota[0].body.bondVal;
									$scope.inputVO.prodIsWebSale =  tota[0].body.isWebSale;								//商品是否可網銀快速申購
						        	$scope.inputVO.prodName = tota[0].body.prodDTL[0].BOND_CNAME;						//商品名稱
						        	$scope.inputVO.prodCurr = tota[0].body.prodDTL[0].CURRENCY_STD_ID;					//計價幣別
									$scope.inputVO.prodRiskLV = tota[0].body.prodDTL[0].RISKCATE_ID;					//產品風險等級
									$scope.inputVO.prodMinBuyAmt = tota[0].body.prodDTL[0].BASE_AMT_OF_PURCHASE;		//最低申購面額
									$scope.inputVO.prodMinGrdAmt = tota[0].body.prodDTL[0].UNIT_OF_VALUE;				//累計申購面額
									$scope.inputVO.refVal = tota[0].body.prodDTL[0].BUY_PRICE;							//參考報價
									$scope.inputVO.refValDate = $scope.toJsDate(tota[0].body.prodDTL[0].BARGAIN_DATE);	//參考報價日期
									$scope.inputVO.limitedPrice = tota[0].body.prodDTL[0].LIMITED_PRICE;
									$scope.inputVO.buyRate = tota[0].body.buyRate;										//取得商品計價幣別最新買匯匯率
									$scope.inputVO.hnwcBuy =  tota[0].body.hnwcBuy;										//商品限高資產客戶申購註記
									
									$scope.nvlAMT = tota[0].body.nvlAMT;
									$scope.sumITEM = tota[0].body.sumITEM;

									if ($scope.inputVO.trustTS == 'S') {
										var acctCurrency = $scope.getAcctCurrency();
										$scope.mappingSet['SOT.DEBIT_ACCT_LIST#DISPLAY'] = $filter('filter')($scope.mappingSet["SOT.DEBIT_ACCT_LIST"], acctCurrency);
										$scope.mappingSet['SOT.CREDIT_ACCT_LIST#DISPLAY'] = $filter('filter')($scope.mappingSet["SOT.CREDIT_ACCT_LIST"], acctCurrency);
										$scope.avlBalance = undefined;                              //扣款帳號餘額
										$scope.avlCurrency = undefined;                             //扣款帳號餘額幣別
										$scope.checkTrustAcct();
										//checkTrustAcct 已處理$scope.changeAcct();//1.換扣款帳號和2.換商品都要查詢 該帳號幣別餘額

									} else if ($scope.inputVO.trustTS == 'M') {
										if (!$scope.checkAccCurrency($scope.acctCurrencyByM)) {
											$scope.inputVO.contractID = '';
										}
									}

									deferred.resolve("success");
									return deferred.promise;
								} else {
									$scope.showErrorMsg("查無商品報價資料");

									$scope.inputVO.prodID = "";
									$scope.prodClear();
								}
							}

				});
			} else {
				$scope.prodClear();
				$scope.inputVO.prodID = "";
			}

			//非fromFPS才會有
			if (!$scope.fromFPS) {
				deferred.resolve("");
			}
			return deferred.promise;
		};

		//解說專員
		$scope.getSOTEmpInfo = function() {
			$scope.sendRecv("SOT712", "getTellerName", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {'tellerID': $scope.inputVO.narratorID},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.EMP_NAME){
								$scope.inputVO.narratorName = tota[0].body.EMP_NAME;
							}else{
								$scope.inputVO.narratorName='';
								$scope.inputVO.narratorID='';
							}
							return;
						}else{
							$scope.inputVO.narratorName='';
							$scope.inputVO.narratorID='';
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
			//從其他頁面進入須帶入的值
			if($scope.connector('get', 'SOTTradeSEQ')){
				$scope.inputVO.tradeSEQ = $scope.connector('get', 'SOTTradeSEQ');
				$scope.inputVO.carSEQ = $scope.connector('get', 'SOTCarSEQ');
				$scope.connector('set', 'SOTTradeSEQ', null);
				$scope.connector('set', 'SOTCarSEQ', null);
			} else if ($scope.fromFPS){
				$scope.inputVO.custID = $scope.FPSData.custID;//客戶ID
				$scope.getSOTCustInfo(true).then(function(data){
					$scope.inputVO.prodID = $scope.FPSData.prdID; //商品代號
					$scope.getProdDTL().then(function(data){
						// $scope.inputVO.purchaseAmt = $scope.FPSData.PURCHASE_ORG_AMT; //原幣金額
						// $scope.getEntrustAmt(true);
					});
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

		$scope.save = function () {

			if($scope.inputVO.purchaseAmt != undefined){
			    $scope.inputVO.purchaseAmt=$scope.moneyUnFormat($scope.inputVO.purchaseAmt);
			}
			$scope.sendRecv("SOT310", "save", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
						} else {
							$scope.showErrorMsg("ehl_01_common_008");
						}
			});

			if($scope.inputVO.purchaseAmt != undefined){
				$scope.inputVO.purchaseAmt=$scope.moneyFormat($scope.inputVO.purchaseAmt);
			}
		};

		$scope.next = function() {
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg("ehl_01_common_022");
	    		return;
	    	}

			//報價日期
			var refValYear = $scope.inputVO.refValDate.getFullYear();
			var refValMonth = $scope.inputVO.refValDate.getMonth();
			var refValDate = $scope.inputVO.refValDate.getDate();
			//系統日期
			var CBSTESTDATE = $scope.mappingSet['CBS.ORDERDATE'][0]["LABEL"];
			debugger;
			if(CBSTESTDATE.length == 8){
				debugger;
				var year = CBSTESTDATE.substring(0,4);
				var month = CBSTESTDATE.substring(4,6);
				if(month.substr(0,1) == "0"){
					month = month.substr(1,2);
					month = parseInt(month) - 1;
				} else {
					month = parseInt(month) - 1;
				}
				var date = CBSTESTDATE.substring(6,8);
				if(date.substr(0,1) == "0"){
					date = date.substr(1,2);
				}
			} else {
				debugger;
				var today = new Date;
				var year = today.getFullYear();
				var month = today.getMonth();
				var date = today.getDate();
			}
			
			//非長效單須檢查：若海外債申購及贖回交易頁面上報價日期不等於today，按"下一步"時，清空商品代號，切請出訊息: 報價日期須為今日。
//			if($scope.inputVO.gtcYN != "Y" && (refValYear != year || refValMonth != month || refValDate != date)) {
//				$scope.inputVO.prodID = '';
//				$scope.prodClear();
//				$scope.showErrorMsg("報價日期須為今日。");
//	    		return;
//			}

			if($scope.inputVO.gtcYN == "" || $scope.inputVO.gtcYN == null || $scope.inputVO.gtcYN == undefined) {
				$scope.showErrorMsg("必須輸入委託方式");
				return;
			}
			
			if ($scope.inputVO.gtcYN == "Y") {
				// 長效單必須輸入委託起日＆委託迄日
				if ($scope.inputVO.gtcStartDate == "" || $scope.inputVO.gtcStartDate == undefined || $scope.inputVO.gtcStartDate == null ||
					$scope.inputVO.gtcEndDate == "" || $scope.inputVO.gtcEndDate == undefined || $scope.inputVO.gtcEndDate == null) {
					$scope.showErrorMsg("請選擇長效單日期");
					return;
				}
			} else if ($scope.inputVO.gtcYN == "P") {
				// 預約單必須輸入委託起日
				if ($scope.inputVO.gtcStartDate == "" || $scope.inputVO.gtcStartDate == undefined || $scope.inputVO.gtcStartDate == null) {
					$scope.showErrorMsg("請選擇預約單日期");
					return;
				}
			}
			
			/**
			 * 檢查：
			 * 1. 長效單、預約單：委託日期不可為 臺灣、香港、美國之假日(電文_NJWEEA70)
			 * 2. 長效單：最短2個營業日、最長5個營業日（需排除美國、香港、台灣休假日）
			 * **/
			if ($scope.inputVO.gtcYN == "Y" || $scope.inputVO.gtcYN == "P") {
				$scope.sendRecv("SOT310", "checkGtcDate", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO", 
				{'gtcYN':$scope.inputVO.gtcYN, 'gtcStartDate':$scope.inputVO.gtcStartDate, 'gtcEndDate':$scope.inputVO.gtcEndDate},
				function(tota, isError) {
					if (!isError) {
						var sflag = tota[0].body.resultList[0].START_DATE_YN;
						var eflag = tota[0].body.resultList[0].END_DATE_YN;
						var dates = tota[0].body.resultList[0].COUNT_DATES;
						if ($scope.inputVO.gtcYN == "Y") {
							if (sflag == "N") {
								$scope.showErrorMsg("長效單『指定交易起日』不可為臺灣、香港、美國之假日，請重先選擇。");
								return;
							}
							if (eflag == "N") {
								$scope.showErrorMsg("長效單『指定交易迄日』不可為臺灣、香港、美國之假日，請重先選擇。");
								return;
							}
							if (dates < 2 || dates > 5) {
								$scope.showErrorMsg("長效單『指定交易起迄日』最短需2個營業日、最長為5個營業日（排除美國、香港、台灣休假日）");
								return;
							}
						} else if ($scope.inputVO.gtcYN == "P") {
							if (sflag == "N") {
								$scope.showErrorMsg("預約單『指定交易起日』不可為臺灣、香港、美國之假日，請重先選擇。");
								return;
							}
						}
						
						/********************************************************************************************************/
						// 快速申購檢核
						if ($scope.inputVO.isWeb == 'Y' && !$scope.chkWebSale()) {
							return;
						}
						var txtMsg = "";
						// 長效單不須檢查餘額是否足夠
						if ($scope.inputVO.gtcYN == "N" && $scope.avlBalance < $scope.inputVO.totAmt) {
							txtMsg = $filter('i18n')('ehl_01_sot310_008');
						}
						if (txtMsg != "") { // && trustPeopNumMsg != ""
							$confirm({text: txtMsg + "是否繼續"}, {size: 'sm'}).then(function() {
								$scope.nextQuery();
							});
						} else {
							$scope.nextQuery();
						}
					}
				});
			} else {
				// 快速申購檢核
				if ($scope.inputVO.isWeb == 'Y' && !$scope.chkWebSale()) {
					return;
				}
				var txtMsg = "";
				// 長效單不須檢查餘額是否足夠
				if ($scope.inputVO.gtcYN == "N" && $scope.avlBalance < $scope.inputVO.totAmt) {
					txtMsg = $filter('i18n')('ehl_01_sot310_008');
				}
				if (txtMsg != "") { // && trustPeopNumMsg != ""
					$confirm({text: txtMsg + "是否繼續"}, {size: 'sm'}).then(function() {
						$scope.nextQuery();
					});
				} else {
					$scope.nextQuery();
				}
			}
		};
		
		$scope.nextQuery = function () {
			$scope.sendRecv("SOT310", "checkTime", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO", {},
			function(tota, isError) {
				if (!isError) {
					$scope.sotYN = tota[0].body.sotYN;
//					alert($scope.sotYN);
					if ($scope.sotYN == 'N' && $scope.inputVO.gtcYN == 'N') {
						$scope.showMsg("已過當日交易截止時間，請改採長效單或預約單委託。");
						return;
					} else {
						$scope.inputVO.warningMsg = "";

						if($scope.inputVO.purchaseAmt != undefined){
						    $scope.inputVO.purchaseAmt=$scope.moneyUnFormat($scope.inputVO.purchaseAmt);
						}

						if($scope.nextCheckAUM()==false){ //風控集中度檢查AUM
							return;
						}

						if($scope.inputVO.purchaseAmt != undefined){
						    $scope.inputVO.purchaseAmt=$scope.moneyUnFormat($scope.inputVO.purchaseAmt);
						}

						$scope.sendRecv("SOT310", "next", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.overCentRateResult == "N") {
									//集中度超過上限
									$scope.showErrorMsg("客戶高風險商品集中度比例已超過上限");
									$scope.custClear();
									$scope.inputVO.custID = "";
									$scope.prodClear();
									$scope.inputVO.prodID = "";
									return;
								}
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
												$scope.connector('set','SOTCarSEQ', null);

												if ($scope.inputVO.trustTS == 'S') {
													$scope.connector('set','SOT311_warningMsg', $scope.inputVO.warningMsg);
													if ($scope.fromFPS) {
														// from FPS_SOT.js
														$scope.setSOTurl('assets/txn/SOT311/SOT311.html');
													} else {
														$rootScope.menuItemInfo.url = "assets/txn/SOT311/SOT311.html";
													}
												} else {
													$scope.connector('set','SOT316_warningMsg', $scope.inputVO.warningMsg);
													if ($scope.fromFPS) {
														// from FPS_SOT.js
														$scope.setSOTurl('assets/txn/SOT316/SOT316.html');
													} else {
														$rootScope.menuItemInfo.url = "assets/txn/SOT316/SOT316.html";
													}
												}
											}
										});
									} else {
										$scope.connector('set','SOTTradeSEQ', $scope.inputVO.tradeSEQ);
										$scope.connector('set','SOTCarSEQ', null);
										if ($scope.inputVO.trustTS == 'S') {
											$scope.connector('set','SOT311_warningMsg', "");
											if ($scope.fromFPS) {
												// from FPS_SOT.js
												$scope.setSOTurl('assets/txn/SOT311/SOT311.html');
											} else {
												$rootScope.menuItemInfo.url = "assets/txn/SOT311/SOT311.html";
											}
										} else {
											$scope.connector('set','SOT316_warningMsg', "");
											if ($scope.fromFPS) {
												// from FPS_SOT.js
												$scope.setSOTurl('assets/txn/SOT316/SOT316.html');
											} else {
												$rootScope.menuItemInfo.url = "assets/txn/SOT316/SOT316.html";
											}
										}
									}
									return;
								}
							} else {
								//高資產投組風險權值檢核錯誤
//								$scope.inputVO.prodID = '';
//								$scope.prodClear();
							}
						});
						if($scope.inputVO.purchaseAmt != undefined){
							$scope.inputVO.purchaseAmt = $scope.moneyFormat($scope.inputVO.purchaseAmt);
						}
					}
				}				
			});
		}

		//風險集中度檢查AUM
		$scope.nextCheckAUM = function () {
			if($scope.inputVO.purchaseAmt != undefined){
			    $scope.inputVO.purchaseAmt=$scope.moneyUnFormat($scope.inputVO.purchaseAmt);
			}
			var success = true;
			//(此單申購金額+在途申購金額+庫存)/前一日投資AUM>=20%，跳出警示「單一商品申購金額超過投資AUM之20%。」ehl_01_sot410_008
			//			console.log('nvlAMT前一日投資AUM:'+$scope.nvlAMT);
			//			console.log('sumITEM在途申購金額+庫存:'+$scope.sumITEM);
			//			console.log('purchaseAmt此單申購金額:'+$scope.inputVO.purchaseAmt);
			var checkAum = 0;
			if ($scope.nvlAMT!=0) {
				checkAum = Number(($scope.inputVO.purchaseAmt+$scope.sumITEM)/$scope.nvlAMT);
			}

			if (checkAum >= 0.2) {
				//$scope.showErrorMsg("ehl_01_sot410_008");
				//			console.log("ehl_01_sot410_008:"+checkAum);
				//跳出AUM警示但不擋下單 success = false;
//				$scope.inputVO.warningMsg = $filter('i18n')('ehl_01_sot410_008');
			}
			if($scope.inputVO.purchaseAmt != undefined){
				$scope.inputVO.purchaseAmt=$scope.moneyFormat($scope.inputVO.purchaseAmt);
			}
			return success;
		}

		$scope.goPRD130 = function (mainProd) {
			var trustTS = $scope.inputVO.trustTS;
			
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT310/SOT310_ROUTE.html',
				className: 'PRD130',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
	        		$scope.txnName = "商品搜尋";
	        		$scope.cust_id=$scope.$parent.inputVO.custID;
					$scope.isPop = true;
					$scope.trustTS = trustTS;
					$scope.fromSOTProdYN = "Y"; //下單商品搜尋//下單只需要輸入客戶ID時檢核，商品搜尋時不需再次檢核
	        		$scope.routeURL = 'assets/txn/PRD130/PRD130.html';
	        		$scope.tradeType = "1";
	            }]
			}).closePromise.then(function (data) {
				if(data.value != "cancel") {
					$scope.inputVO.prodID = data.value.PRD_ID;
					$scope.getProdDTL();
				}
			});
		};

		//是否為長效單
		$scope.gtcYN_Changed = function() {
			if($scope.inputVO.isWeb == "Y" && $scope.inputVO.gtcYN == "Y") {
				$scope.inputVO.gtcYN = "N";
				$scope.inputVO.gtcEndDate = undefined;
				$scope.showErrorMsg("長效單及限價交易未開放快速申購。");
				return;
			}
			
			if ($scope.inputVO.gtcYN == "N") {
				// 當日單
				$scope.inputVO.gtcStartDate = undefined;
				$scope.inputVO.gtcEndDate = undefined;	
				
			} else if ($scope.inputVO.gtcYN == "Y") {
				// 長效單
				$scope.inputVO.gtcEndDate = undefined;
				
			} else if ($scope.inputVO.gtcYN == "P") {
				// 預約單
				$scope.inputVO.gtcEndDate = $scope.inputVO.gtcStartDate;
				
				// 預約單：『委託價格』enable 及default為"市價"，價格可約定指定交易日當日市價或買進限價
				$scope.inputVO.entrustType = "2";	// 市價
			}
			$scope.limitDate();

			if ($scope.inputVO.purchaseAmt == undefined || $scope.inputVO.purchaseAmt == null || $scope.inputVO.purchaseAmt == '')
				$scope.getEntrustAmt();
			else
				$scope.getEntrustAmt(true);
		}

		//申購方式：臨櫃/網銀快速交易
		$scope.isWeb_Changed = function() {
			//網銀快速申購檢核
			if($scope.inputVO.isWeb == 'Y') {
				//快速申購不開放長效單
				$scope.inputVO.gtcYN = "N";
				$scope.inputVO.gtcEndDate = undefined;

				//快速申購不可議價
				$scope.changeFee = undefined;

				//快速申購固定為"市價"
				$scope.getEntrustAmt(true);
				if($scope.chkWebSale()) {
					//快速申購與臨櫃的手續費電文不同，須重打
					$scope.getDefaultFeeRateData().then(function(data) {
						$scope.getFee('rate');
					});
				}
			} else {
				//快速申購與臨櫃的手續費電文不同，須重打
				$scope.getDefaultFeeRateData().then(function(data) {
					$scope.getFee('rate');
				});

				$scope.entrustDisabled = false;
			}
		}

		//網銀快速申購檢核
		$scope.chkWebSale = function() {
			if($scope.inputVO.isAgreeProdAdv != 'Y' && $scope.inputVO.profInvestorYN != 'Y' ) {
				$scope.showErrorMsg("需簽過推介同意書才可使用快速申購功能。");//檢核客戶是否可使用快速申購功能
				return false;
			}

			if($scope.inputVO.totAmt && ($scope.inputVO.totAmt * $scope.inputVO.buyRate) > 10000000) {
				$scope.showErrorMsg("交易金額檢核超過上限，不可使用快速申購。");//交易金額上限折台1,000萬
				return false;
			}

			if($scope.inputVO.custID && $scope.inputVO.prodID && $scope.inputVO.prodIsWebSale && $scope.inputVO.prodIsWebSale == 'N') {
				$scope.showErrorMsg("此商品不開放快速申購。");//商品註記為不開放網銀快速申購
				$scope.prodClear();
				return false;
			}

			if($scope.checkWebOverTime()) {
				$scope.showErrorMsg("為讓客戶於網銀行銀確認交易，快速申購鍵機受理至14:50。");//超過14:50(參數)
				return false;
			}

			//快速申購不開放長效單
			if($scope.inputVO.gtcYN == "Y") {
				$scope.showErrorMsg("長效單及限價交易未開放快速申購。");
				return false;
			}

			//快速申購不開放長效單
			if($scope.inputVO.entrustType == "1") {
				$scope.showErrorMsg("長效單及限價交易未開放快速申購。");
				return false;
			}

			return true;
		}

		//檢核是否已超過可網銀快速申購時間(14:50)
		$scope.checkWebOverTime = function() {
			var findParam = $filter('filter')($scope.mappingSet['SOT.BOND_WEB_TIMESTAMP'], {DATA: "1"});
			var webTimestamp = (findParam != null && findParam.length > 0) ? findParam[0].LABEL+":00" : "14:50:00";

			var today = new Date();
			var today_year = today.getFullYear();  //西元年份
			var today_month = today.getMonth()+1;  //一年中的第幾月
			var today_date = today.getDate();      //一月份中的第幾天
			var timeArray1 = webTimestamp.split(":");

			var PM1450 = new Date(today_year, today_month-1, today_date, timeArray1[0], timeArray1[1], timeArray1[2]);
			var now  = new Date();

			if (now.valueOf() > PM1450.valueOf()) {
				return true;//已超過可網銀快速申購時間
			}

			return false;
		};
		
		
		//#1442 信託帳號調整警示訊息
		$scope.showTrustAcctChangeAlarn = function() {
			if($scope.inputVO.purchaseAmt && $scope.inputVO.purchaseAmt != "" && $scope.inputVO.purchaseAmt != null  ) {
				$scope.showMsg("ehl_01_sot310_011");
			}		
		};
});

