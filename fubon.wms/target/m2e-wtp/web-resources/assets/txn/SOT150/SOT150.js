/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT150Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q, validateService, sotService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT150Controller";
		//繼承PRD100高齡檢核
		$controller('PRD100Controller', {$scope: $scope});
		
		// filter
		$scope.getXML = function(){
			var deferrd = $q.defer();
			getParameter.XML(["SOT.CUST_TYPE", "SOT.CERTIFICATE_STATUS", "SOT.RT_TRADE_SUB_TYPE", "SOT.NF_CHANGE_TYPE"
			                  ,'SOT.ASSET_TRADE_SUB_TYPE', 'SOT.PROSPECTUS_TYPE', 'SOT.RESERVE_DATE_TIMESTAMP','SOT.RESERVE_TRADE_DAYS','SOT.TRADE_DATE_TYPE','SOT.TRUST_CURR_TYPE','SOT.SPEC_CUSTOMER','SOT.FUND_ASSET_STATUS','SOT.FUND_DECIMAL_POINT'], function(totas) {
				if (totas) {
					$scope.mappingSet['SOT.CUST_TYPE'] = totas.data[totas.key.indexOf('SOT.CUST_TYPE')];
					$scope.mappingSet['SOT.CERTIFICATE_STATUS'] = totas.data[totas.key.indexOf('SOT.CERTIFICATE_STATUS')];
					$scope.mappingSet['SOT.RT_TRADE_SUB_TYPE'] = totas.data[totas.key.indexOf('SOT.RT_TRADE_SUB_TYPE')];
					$scope.mappingSet['SOT.NF_CHANGE_TYPE'] = totas.data[totas.key.indexOf('SOT.NF_CHANGE_TYPE')];
					$scope.mappingSet['SOT.ASSET_TRADE_SUB_TYPE']  =  totas.data[totas.key.indexOf('SOT.ASSET_TRADE_SUB_TYPE')];
					
					//信託幣別
			        $scope.mappingSet['SOT.TRUST_CURR_TYPE'] = totas.data[totas.key.indexOf('SOT.TRUST_CURR_TYPE')];
			        
					
					//公開說明書選項                                                                           
					$scope.mappingSet['SOT.PROSPECTUS_TYPE'] = totas.data[totas.key.indexOf('SOT.PROSPECTUS_TYPE')];
					
					//交易日期 選項  即時或預約
			        $scope.mappingSet['SOT.TRADE_DATE_TYPE'] = totas.data[totas.key.indexOf('SOT.TRADE_DATE_TYPE')];
			       
					
					//預約時間限制參數
			    	$scope.mappingSet['SOT.RESERVE_DATE_TIMESTAMP'] = totas.data[totas.key.indexOf('SOT.RESERVE_DATE_TIMESTAMP')]; 
			    	
					$scope.mappingSet['SOT.SPEC_CUSTOMER'] = totas.data[totas.key.indexOf('SOT.SPEC_CUSTOMER')];//客戶註記
	
					$scope.mappingSet['SOT.FUND_ASSET_STATUS'] = totas.data[totas.key.indexOf('SOT.FUND_ASSET_STATUS')];//憑證狀態
					
					// 基金幣別小數位
					$scope.mappingSet['SOT.FUND_DECIMAL_POINT'] = totas.data[totas.key.indexOf('SOT.FUND_DECIMAL_POINT')]; 
					
			    	//Nday 預約 營業日參數
					$scope.mappingSet['SOT.RESERVE_TRADE_DAYS'] = totas.data[totas.key.indexOf('SOT.RESERVE_TRADE_DAYS')]; 
		        	angular.forEach($scope.mappingSet['SOT.RESERVE_TRADE_DAYS'], function(row) {
		        		if(row.DATA=='NF') { 
		        			$scope.reserveTradeDay = row.LABEL;
		        		}
					});
		        	deferrd.resolve();
				}
			});
			return deferrd.promise;
		}
		
		$scope.getReserveTradeDate = function() {  
			/* 取N day getTradeDate 營業日 */ 
			$scope.sendRecv("SOT712", "getReserveTradeDate", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {prodType:'NF'},
					function(tota, isError) {
						if (!isError) { 
							$scope.inputVO.reservationTradeDate = tota[0].body.TradeDate;
							return;
						}
			});
		};
		
		//開啟28天月曆
		$scope.calendar28Days_picker = function(){
			//			console.log("calendar28Days_picker:");
			$scope.connector('set','chargeDateList',$scope.inputVO.chargeDateList);
        	var dialog = ngDialog.open({
        		template: 'assets/txn/SOT712/28Days_picker.html',
        		className: '',
        		controller: ['$scope',function($scope){
        		}]
        	});
        	dialog.closePromise.then(function(data){  
        		$scope.inputVO.chargeDateList = $scope.connector('get','chargeDateList');
        	});
        }
		
		var NowDate = new Date(); //現在時間
		
		$scope.fResumeDateOptions = {
			maxDate: $scope.maxDate,
			minDate: NowDate
		}
		$scope.fHoldStartDateOptions = {
			maxDate: $scope.maxDate,
			minDate: NowDate
		};
		$scope.fHoldEndDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.fHoldStartDate || $scope.minDate
		};
		$scope.altInputFormats = ['M!/d!/yyyy'];
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			
			$scope.fHoldStartDateOptions.maxDate = $scope.inputVO.fHoldEndDate;
			$scope.fHoldEndDateOptions.minDate = $scope.inputVO.fHoldStartDate || $scope.minDate;
		};
		$scope.limitDate1 = function() {          //預定之暫停扣款日不能等於當日
			
			$scope.fHoldStartDateOptions.maxDate = $scope.inputVO.fHoldEndDate;
			$scope.fHoldEndDateOptions.minDate = $scope.inputVO.fHoldStartDate || $scope.minDate;
			
			if($scope.inputVO.tradeDateType == 2){              
				var a = NowDate.getFullYear().toString() + NowDate.getMonth().toString() + NowDate.getDate().toString();
				var b = $scope.inputVO.fHoldStartDate.getFullYear().toString() + $scope.inputVO.fHoldStartDate.getMonth().toString() + $scope.inputVO.fHoldStartDate.getDate().toString();
				if(a==b){
					$scope.showErrorMsg("預約交易時，暫停扣款起日不得為鍵機當日");
				}
			}
		};
        //
        
        $scope.cartList = [];
        
        
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
								$scope.inputVO.tradeSEQ = tota[0].body.tradeSEQ;
								return;
							}
				});
			 
		};
		
		//預約限制時間參數化
		$scope.getReserveDateTimestamp = function(){  
	        if ($scope.mappingSet['SOT.RESERVE_DATE_TIMESTAMP']) {
	        	angular.forEach($scope.mappingSet['SOT.RESERVE_DATE_TIMESTAMP'], function(row) {
	        		if (row) { 
	       				var rdTimestamp = row.LABEL.split("|");
	       				$scope.rdTimestamp1 = rdTimestamp[0].substring(0, 2)+":"+rdTimestamp[0].substring(2, 4)+":00";
	       				$scope.rdTimestamp2 = rdTimestamp[1].substring(0, 2)+":"+rdTimestamp[1].substring(2, 4)+":00";
	       			}
	       		});
	       	}else{
	       		$scope.rdTimestamp1 = "15:20:00";
   				$scope.rdTimestamp2 = "15:30:00"; 
        	} 
	        //alert("預約時間限制:"+$scope.rdTimestamp1 + "~" +$scope.rdTimestamp2);
		}
		
		
 
		/**
		 * 基金交易日期：即時與預約判斷
前端先取得XML參數SOT.RESERVE_DATE_TIMESTAMP
目前設定為：2200|2210

前端判斷
1. 若時間在2200之前，則USER可選擇"即時"與"預約"
2. 若時間在2200與2210之前，則出現錯誤訊息ehl_01_SOT_009   
  {0}~{1}之間不可執行交易    
  {0}為傳入參數22:00；
  {1}為傳入參數21:00
3. 若時間在2210之後，則USER只能選擇"預約"
4.選擇"即時"交易，則交易日期為系統日期
5.選擇"預約"交易，則交易日期由SOT712.getReserveTradeDate取得
		 * 
		 * **/
		//交易日期類別是否可選擇即時/預約
		$scope.checkTradeDateType = function(){
			$scope.getReserveDateTimestamp();//預約限制時間參數化
			/*
			比對系統時間
			AM00:00~PM3:20，預設為即時
			PM3:20~PM3:30不能執行
			PM3:30後，預設為預約
           */
			//			console.log("checkTradeDateType 交易日期類別是否可選擇即時/預約");
			////			console.log($scope.inputVO.tradeDateType);
			var today = new Date();
			var today_year = today.getFullYear(); //西元年份 
			var today_month = today.getMonth()+1; //一年中的第幾月 
			var today_date = today.getDate(); //一月份中的第幾天
			var AM0000 = Date.parse(today_year+"-"+today_month+"-"+today_date+" 00:00:00");
			var PM0320 = Date.parse(today_year+"-"+today_month+"-"+today_date+" "+$scope.rdTimestamp1);
			var PM0330 = Date.parse(today_year+"-"+today_month+"-"+today_date+" "+$scope.rdTimestamp2);
			var now  = new Date();
			
			if ( now.valueOf() > AM0000.valueOf() && now.valueOf() < PM0320.valueOf()) {
				//AM00:00~PM3:20，預設為即時
                if($scope.inputVO.tradeDateType==''){
                  $scope.inputVO.tradeDateType=1;
				  //			console.log("00:00~" + $scope.rdTimestamp1 + "，預設為即時");
                }
			} else if( now.valueOf() > PM0320.valueOf() && now.valueOf() < PM0330.valueOf()) {
				//PM3:20~PM3:30不能執行
				$scope.inputVO.tradeDateType="";
				$scope.showErrorMsg($scope.rdTimestamp1 + "~" + $scope.rdTimestamp2 + "不能執行");
				//			console.log("tradeDateType:"+$scope.inputVO.tradeDateType);
				return false;
			} else if(now.valueOf() > PM0330.valueOf()) {
				//PM3:30後，預設為預約
				$("#tradeDateType1").attr("disabled", true);
				$scope.inputVO.tradeDateType=2;
			}			
		}
        $scope.init = function() {
        	$scope.cartList = []; 
			$scope.inputVO = {
					prodType:'1',                               //1：基金
		        	tradeType:'4',                              //4：變更
		        	seniorAuthType:'S',							//高齡評估表授權種類(S:下單、A：適配)
		        	trustTS: 'S',  								//S:特金交易 M:金錢信託
					tradeSEQ: '',					
					custID: '', 								//客戶ID
					custName: '', 					
					kycLV: '', 									//KYC等級
					kycDueDate: undefined, 						//KYC效期
					profInvestorYN: '', 						//是否為專業投資人
					piDueDate: undefined, 						//專業投資人效期
					custAgeUnder65YN: '',						//客戶年齡為65歲以下
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
					custProType: '',
					noSale: '',
					piRemark:'',
					hnwcYN:'',
					hnwcServiceYN:'',
					
					custType: 'CUST', 							//來行人員
					agentID: '', 								//代理人ID
					agentName: '',
					
					narratorID: projInfoService.getUserID(),	//解說人員ID
					narratorName: projInfoService.getUserName(),//解說人員姓名
					
					tradeSubType: '', 							//信託型態
					certificateID: '', 							//憑證編號
					bProdID: '', 								//變更前基金代號
					bProdName: '', 								//變更前基金名稱
					bProdCurr: '', 								//變更前產品計價幣別
					bProdRiskLV: '', 							//變更前產品風險等級
					bTrustCurr: '', 							//變更前產品信託幣別
					bTrustCurrType: '', 						//轉出標的信託幣別類別
					bStatus: '',								//變更前憑證狀態
					bTrustAmt: undefined, 						//變更前信託金額
					bPurchaseAmtL: undefined, 					//變更前每月扣款金額_低(定期不定額)
					bPurchaseAmtM: undefined,					//變更前每月扣款金額_中(定期不定額)；變更前扣款金額(定期定額)
					bPurchaseAmtH: undefined, 					//變更前每月扣款金額_高(定期不定額)
					bChargeDate1: '', 							//變更前每月扣款日期1
					bChargeDate2: '', 							//變更前每月扣款日期2
					bChargeDate3: '', 							//變更前每月扣款日期3
					bChargeDate4: '', 							//變更前每月扣款日期4
					bChargeDate5: '', 							//變更前每月扣款日期5
					bChargeDate6: '', 							//變更前每月扣款日期6
					bDebitAcct: '', 							//變更前扣款帳號
					bCreditAcct: '', 							//變更前收益入帳帳號
					bTrustAcct: '', 							//變更前信託帳號
					bCertificateStatus: '',					//變更前憑證狀態    NFBRN9 (客戶庫存查詢) 定期定額C30狀態	Status  0:正常 1:暫停 2:非主標的 3:終止
					bNotVertify: '',                            //變更前標的未核備
					
					bEquivalentAmtL: undefined,
					bEquivalentAmtM: undefined,
					bEquivalentAmtH: undefined,
					bChargeDateList: '', 
					chargeDateList: '',
					
					fProdID: '', 								//變更後基金代號
					fProdName: '', 								//變更後基金名稱
					fProdCurr: '', 								//變更後產品計價幣別
					fProdRiskLV: '', 							//變更後產品風險等級
					fTrustCurr: '', 							//變更後產品信託幣別
					fPurchaseAmtL: undefined, 					//變更後每月扣款金額_低(定期不定額)
					fPurchaseAmtM: undefined, 					//變更後每月扣款金額_中(定期不定額)；變更後扣款金額(定期定額)
					fPurchaseAmtH: undefined, 					//變更後每月扣款金額_高(定期不定額)
					fChargeDate1: '',							//變更後每月扣款日期1
					fChargeDate2: '',							//變更後每月扣款日期2
					fChargeDate3: '',							//變更後每月扣款日期3
					fChargeDate4: '',							//變更後每月扣款日期4
					fChargeDate5: '',							//變更後每月扣款日期5
					fChargeDate6: '',							//變更後每月扣款日期6
					fDebitAcct: '', 							//變更後扣款帳號
					fCreditAcct: '', 							//變更後收益入帳帳號
					fCertificateStatus: '', 					//變更後憑證狀態   NFBRN9 (客戶庫存查詢) 定期定額C30狀態	Status  0:正常 1:暫停 2:非主標的 3:終止
					fHoldStartDate: undefined, 					//變更後暫停扣款起日
					fHoldEndDate: new Date(2099, 11 , 31), 		//變更後暫停扣款迄日
					fResumeDate: undefined, 					//變更後恢復正常扣款起日
					fFeeL: undefined,							//變更後每次扣款手續費_低
					fFeeM: undefined,							//變更後每次扣款手續費_中
					fFeeH: undefined,							//變更後每次扣款手續費_高
					doValidSeniorCustEval: true					//第一次加入購物車才需要檢核
			};
			$scope.isShowAcctList = false;                     //是否顯示帳號清單
			$scope.inputVO.debitAvbBalance = undefined;                 //扣款帳號餘額
			$scope.avlCurrency = undefined;                             //扣款帳號餘額幣別
			$scope.isGetSOTCustInfo = undefined; //查詢客戶帳號  (執行前 undefined ,執行後 true)
			var custID = $scope.connector('get','ORG110_custID');
			console.log(custID);
			if(custID != undefined){
				$scope.inputVO.custID = custID;
			}
			 
			$scope.getReserveTradeDate();  // 取N day getTradeDate 營業日
			$scope.checkTradeDateType(); //預設即時或預約
		};
		$scope.init();
		
		$scope.custClear = function() {
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST#DISPLAY'] = [];
			$scope.mappingSet['SOT.CREDIT_ACCT_LIST#DISPLAY'] = [];
			
			$scope.inputVO.custName = '';
			
			$scope.inputVO.kycLV = '';									//KYC等級
			$scope.inputVO.kycDueDate = undefined;						//KYC效期
			$scope.inputVO.profInvestorYN = '';							//是否為專業投資人
			$scope.inputVO.piDueDate = undefined;						//專業投資人效期
			$scope.inputVO.custAgeUnder65YN = '';						//客戶年齡65歲以下
			$scope.inputVO.custRemarks = '';							//客戶註記
			$scope.inputVO.isOBU = '';									//是否為OBU客戶
			$scope.inputVO.isAgreeProdAdv = '';							//同意投資商品諮詢服務
			$scope.inputVO.bargainDueDate =  undefined;					//期間議價效期
			$scope.inputVO.plNotifyWays = '';							//停損停利通知方式
			$scope.inputVO.takeProfitPerc = undefined;					//停利點
			$scope.inputVO.stopLossPerc = undefined;					//停損點
			//$scope.inputVO.debitAcct = '';								//扣款帳號
			$scope.inputVO.trustAcct = '';								//信託帳號
			//$scope.inputVO.creditAcct = '';								//收益入帳帳號
			$scope.inputVO.w8benEffDate = undefined;					//W8ben有效日期
			$scope.inputVO.w8BenEffYN = '';
			$scope.inputVO.fatcaType = '';
			$scope.inputVO.custProType = '';
			$scope.inputVO.noSale = '';
			$scope.inputVO.piRemark = '';
			$scope.inputVO.hnwcYN = '';
			$scope.inputVO.hnwcServiceYN = '';
			
			$scope.inputVO.custType = 'CUST';							//來行人員
			$scope.inputVO.agentID = '';								//代理人ID
			$scope.inputVO.agentName = '';
			$scope.isGetSOTCustInfo = undefined; //查詢客戶帳號  (執行前 undefined ,執行後 true)
			$scope.inputVO.debitAvbBalance = undefined;                 //扣款帳號餘額
			$scope.avlCurrency = undefined;                             //扣款帳號餘額幣別
			$scope.isShowAcctList = false;                     //是否顯示帳號清單
			$scope.inputVO.isFirstTrade ='';
		};
        
        $scope.prodClear = function() {
                                            //庫存信託帳號
        	$scope.inputVO.tradeSubType = '';							//信託型態
        	$scope.inputVO.tradeSubTypeD = '';							//詳細信託型態
        	$scope.txType ='';                                          //庫存 交易類別  空白 : 一般 , ‘Y’: FUND99
        	$scope.assetType ='';                                       //庫存assetType
        	$scope.inputVO.certificateID = '';							//憑證編號
			$scope.inputVO.bProdID = '';								//變更前基金代號
			$scope.inputVO.bProdName = '';								//變更前基金名稱
			$scope.inputVO.bProdCurr = '';								//變更前產品計價幣別
			$scope.inputVO.bProdRiskLV = '';							//變更前產品風險等級
			$scope.inputVO.bTrustCurr = '';								//變更前產品信託幣別
			$scope.inputVO.bTrustCurrType = '';							//轉出標的信託幣別類別 C,N,Y
			$scope.inputVO.bStatus = '';								//變更前憑證狀態
			$scope.inputVO.bTrustAmt = undefined; 						//變更前信託金額
			$scope.inputVO.bPurchaseAmtL = undefined; 					//變更前每月扣款金額_低(定期不定額)；變更前扣款金額(定期定額)
			$scope.inputVO.bPurchaseAmtM = undefined;					//變更前每月扣款金額_中(定期不定額)
			$scope.inputVO.bPurchaseAmtH = undefined; 					//變更前每月扣款金額_高(定期不定額)
			$scope.inputVO.bPurchaseAmtExchL = undefined; 		        //變更前每月扣款金額換匯_低(定期不定額)
			$scope.inputVO.bPurchaseAmtExchM = undefined;		        //變更前每月扣款金額換匯_中(定期不定額)；變更前扣款金額(定期定額)
			$scope.inputVO.bPurchaseAmtExchH = undefined;		        //變更前每月扣款金額換匯_高(定期不定額)
			
			$scope.inputVO.bChargeDate1 = '';							//變更前每月扣款日期1
			$scope.inputVO.bChargeDate2 = '';							//變更前每月扣款日期2
			$scope.inputVO.bChargeDate3 = '';							//變更前每月扣款日期3
			$scope.inputVO.bChargeDate4 = '';							//變更前每月扣款日期4
			$scope.inputVO.bChargeDate5 = '';							//變更前每月扣款日期5
			$scope.inputVO.bChargeDate6 = '';							//變更前每月扣款日期6
			$scope.inputVO.bDebitAcct = '';								//變更前扣款帳號
			$scope.inputVO.bCreditAcct = '';							//變更前收益入帳帳號
			$scope.inputVO.bTrustAcct = '';								//變更前信託帳號
			$scope.inputVO.bCertificateStatus = '',					    //變更前憑證狀態  NFBRN9 (客戶庫存查詢) 定期定額C30狀態	Status  0:正常 1:暫停 2:非主標的 3:終止
			$scope.inputVO.bNotVertify = '';                            //變更前未核備欄位FUS40
			
			$scope.inputVO.bEquivalentAmtL = undefined;                 //用來判斷每月扣款金額金額 (換匯後或非換匯)是否相等
			$scope.inputVO.bEquivalentAmtM = undefined;
			$scope.inputVO.bEquivalentAmtH = undefined;
			
			$scope.inputVO.bChargeDateList = '';
			$scope.inputVO.chargeDateList = '';		
			
			$scope.inputVO.fProdID = '';								//變更後基金代號
			$scope.inputVO.fProdName = '';								//變更後基金名稱
			$scope.inputVO.fProdCurr = '';								//變更後產品計價幣別
			$scope.inputVO.fProdRiskLV = '';							//變更後產品風險等級
			$scope.inputVO.fTrustCurr = '';								//變更後產品信託幣別
			$scope.inputVO.fPurchaseAmtL = undefined; 					//變更後每月扣款金額_低(定期不定額)；變更後扣款金額(定期定額)
			$scope.inputVO.fPurchaseAmtM = undefined; 					//變更後每月扣款金額_中(定期不定額)
			$scope.inputVO.fPurchaseAmtH = undefined; 					//變更後每月扣款金額_高(定期不定額)
			$scope.inputVO.fChargeDate1 = '';							//變更後每月扣款日期1
			$scope.inputVO.fChargeDate2 = '';							//變更後每月扣款日期2
			$scope.inputVO.fChargeDate3 = '';							//變更後每月扣款日期3
			$scope.inputVO.fChargeDate4 = '';							//變更後每月扣款日期4
			$scope.inputVO.fChargeDate5 = '';							//變更後每月扣款日期5
			$scope.inputVO.fChargeDate6 = '';							//變更後每月扣款日期6
			$scope.inputVO.fDebitAcct = '';								//變更後扣款帳號
			$scope.inputVO.fCreditAcct = '';							//變更後收益入帳帳號
			$scope.inputVO.fCertificateStatus = ''; 					//變更後憑證狀態    NFBRN9 (客戶庫存查詢) 定期定額C30狀態	Status  0:正常 1:暫停 2:非主標的 3:終止
			$scope.inputVO.fHoldStartDate = undefined; 					//變更後暫停扣款起日
			$scope.inputVO.fHoldEndDate = new Date(2099, 11 , 31); 		//變更後暫停扣款迄日
			$scope.inputVO.fResumeDate = undefined; 					//變更後恢復正常扣款起日
			$scope.inputVO.fFeeL = undefined;							//變更後每次扣款手續費_低
			$scope.inputVO.fFeeM = undefined;							//變更後每次扣款手續費_中
			$scope.inputVO.fFeeH = undefined;							//變更後每次扣款手續費_高
			
			$scope.inputVO.prodMinBuyAmt = undefined;                   //申購金額
			$scope.inputVO.BaseAmtOfSPurchaseCurr ='';                  //最低申購金額幣別
			$scope.inputVO.tradeDateType = undefined;                   //交易日期類別
			//$scope.inputVO.tradeDate = undefined;                       //交易日期類別
        }
		
		// if data
		$scope.inputVO.tradeSEQ = $scope.connector('get', 'SOTTradeSEQ');
		$scope.inputVO.carSEQ = $scope.connector('get', 'SOTCarSEQ');
		$scope.connector('set', 'SOTTradeSEQ', null);
		$scope.connector('set', 'SOTCarSEQ', null);
		
		// SOT701-客戶電文
		$scope.getSOTCustInfo = function(loadEdit,input) {
			$scope.inputVO.custID = $filter('uppercase')($scope.inputVO.custID);
			var validCustID = validateService.checkCustID($scope.inputVO.custID); //自然人和法人檢查
			//			console.log("custID:" + $scope.inputVO.custID + ", checkCustID: "+validCustID);
			
			if(validCustID==false){ 
				$scope.inputVO.custID='';
			}
			
			if(validCustID==false || input) { 
				$scope.custClear();
				$scope.prodClear(true);
			}
			
			if(validCustID) {
				 
				$scope.isGetSOTCustInfo = undefined; //查詢客戶帳號  (執行前 undefined ,執行後 true)
				$scope.sendRecv("SOT150", "getSOTCustInfo", "com.systex.jbranch.app.server.fps.sot150.SOT150InputVO", {'custID':$scope.inputVO.custID, 'prodType':1, 'tradeType':4},
						function(tota, isError) {
							if (!isError) {
									$scope.inputVO.custName = tota[0].body.custName;
									$scope.inputVO.kycLV = tota[0].body.kycLV;										//KYC等級
									if(tota[0].body.kycDueDate){ 
										$scope.inputVO.kycDueDate = $scope.toJsDate(tota[0].body.kycDueDate);			//KYC效期
									}
									$scope.inputVO.profInvestorYN = tota[0].body.profInvestorYN;					//是否為專業投資人
									if(tota[0].body.piDueDate){
										$scope.inputVO.piDueDate = $scope.toJsDate(tota[0].body.piDueDate);				//專業投資人效期
									}
									$scope.inputVO.custAgeUnder65YN = tota[0].body.ageUnder70Flag;					//客戶年齡65歲以下
									$scope.inputVO.custRemarks = tota[0].body.custRemarks;							//客戶註記
									$scope.inputVO.isOBU = tota[0].body.isOBU;										//是否為OBU客戶
									$scope.inputVO.isAgreeProdAdv = tota[0].body.isAgreeProdAdv;					//同意投資商品諮詢服務
									if(tota[0].body.bargainDueDate){
										$scope.inputVO.bargainDueDate =  $scope.toJsDate(tota[0].body.bargainDueDate);					//期間議價效期
									}									
									$scope.inputVO.plNotifyWays = tota[0].body.plNotifyWays;						//停損停利通知方式
									$scope.inputVO.takeProfitPerc = tota[0].body.takeProfitPerc;					//停利點
									$scope.inputVO.stopLossPerc = tota[0].body.stopLossPerc;						//停損點
									if(tota[0].body.w8benEffDate){
										$scope.inputVO.w8benEffDate = $scope.toJsDate(tota[0].body.w8benEffDate);		//W8ben有效日期
									}
									$scope.inputVO.w8BenEffYN = tota[0].body.w8BenEffYN;
									$scope.inputVO.fatcaType = tota[0].body.fatcaType;
									$scope.inputVO.custProType = tota[0].body.custProType;							//專業投資人類型
									$scope.inputVO.piRemark = tota[0].body.piRemark;
									$scope.inputVO.hnwcYN = tota[0].body.hnwcYN;
									$scope.inputVO.hnwcServiceYN = tota[0].body.hnwcServiceYN;
									
									// add by ocean 非常規交易
									$scope.inputVO.isFirstTrade = tota[0].body.isFirstTrade; 
									$scope.inputVO.ageUnder70Flag = tota[0].body.ageUnder70Flag;
									$scope.inputVO.eduJrFlag = tota[0].body.eduJrFlag;
									$scope.inputVO.healthFlag = tota[0].body.healthFlag; 

									$scope.mappingSet['SOT.DEBIT_ACCT_LIST'] = tota[0].body.debitAcct;
									$scope.mappingSet['SOT.TRUST_ACCT_LIST'] = tota[0].body.trustAcct;
									$scope.mappingSet['SOT.CREDIT_ACCT_LIST'] = tota[0].body.creditAcct;
									//不指定 $scope.inputVO.fDebitAcct = (tota[0].body.debitAcct.length > 0 ? tota[0].body.debitAcct[0].LABEL : "");				//扣款帳號
									//不指定 $scope.inputVO.fCreditAcct = (tota[0].body.creditAcct.length > 0 ? tota[0].body.creditAcct[0].LABEL : "");			//收益入帳帳號
									$scope.isGetSOTCustInfo = true;
									return;
							} else {
								$scope.inputVO.custID = "";
								$scope.custClear();
							}
				});
			}
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
			$scope.inputVO.fProdName = "";									//變更後基金名稱
			$scope.inputVO.fProdCurr = "";									//變更後產品計價幣別
			$scope.inputVO.fProdRiskLV = "";								//變更後產品風險等級
			$scope.inputVO.fTrustCurr = "";			                        //變更後產品信託幣別 
			$scope.inputVO.fPurchaseAmtL = undefined; 					    //變更後每月扣款金額_低(定期不定額)；變更後扣款金額(定期定額)
			$scope.inputVO.fPurchaseAmtM = undefined; 					    //變更後每月扣款金額_中(定期不定額)
			$scope.inputVO.fPurchaseAmtH = undefined; 					    //變更後每月扣款金額_高(定期不定額)
			$scope.inputVO.fProdID = $filter('uppercase')($scope.inputVO.fProdID);
			
			if($scope.inputVO.fProdID) {
				
				
				$scope.sendRecv("SOT150", "getProdDTL", "com.systex.jbranch.app.server.fps.sot150.SOT150InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.prodDTL != null && tota[0].body.prodDTL.length > 0) {
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
												$scope.inputVO.fProdID = "";
											}
										});
									}		
									$scope.inputVO.fProdName = tota[0].body.prodDTL[0].FUND_CNAME;			//變更後基金名稱
									$scope.inputVO.fProdCurr = tota[0].body.prodDTL[0].CURRENCY_STD_ID;		//變更後產品計價幣別
									$scope.inputVO.fProdRiskLV = tota[0].body.prodDTL[0].RISKCATE_ID;		//變更後產品風險等級

                                    
									if ($scope.inputVO.bTrustCurrType=='C' || $scope.inputVO.bTrustCurrType=='N') {  //變更前信託幣別  C國內 N台幣信託
										$scope.inputVO.fTrustCurr = 'TWD';  //變更後產品信託幣別
									} else if ($scope.inputVO.bTrustCurrType=='Y') {  //變更前信託幣別Y外幣
										$scope.inputVO.fTrustCurr= tota[0].body.prodDTL[0].CURRENCY_STD_ID;	//變更後產品信託幣別
									}
									  
									$scope.getAmtByProdCurr();//外幣信託 要  查詢庫存金額換匯
									
									$scope.findNfMinBuyAmt();//查詢最低/最高申購金額 (BY tradeSubType 信託幣別currencyType)
									$scope.showFitnessMessage(tota[0].body.fitnessMessage); 
									return;
								} else {
									if(tota[0].body.errorMsg){
										$scope.showErrorMsg(tota[0].body.errorMsg);
									}
									$scope.inputVO.fProdID = "";
								}
							} else { 
								if (tota.body.msgData) {
									//$scope.showErrorMsg(tota.body.msgData);
								} else {	
									$scope.showErrorMsg("ehl_01_common_009");
								}
							
								$scope.inputVO.fProdID = "";
								$scope.inputVO.fProdName = "";									//變更後基金名稱
								$scope.inputVO.fProdCurr = "";									//變更後產品計價幣別
								$scope.inputVO.fProdRiskLV = "";								//變更後產品風險等級
								$scope.inputVO.fTrustCurr = "";			//變更後產品信託幣別
							}
				});
			}
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
		
		$scope.noCallCustQuery = function () {
			var deferred = $q.defer();
			
			$scope.sendRecv("SOT150", "query", "com.systex.jbranch.app.server.fps.sot150.SOT150InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.cartList = tota[0].body.carList;
							$scope.outputVO = tota[0].body;

							if (tota[0].body.carList.length > 0 && tota[0].body.mainList.length > 0) {
								$scope.inputVO.narratorID = tota[0].body.carList[0].NARRATOR_ID;		//解說人員ID
								$scope.inputVO.narratorName = tota[0].body.carList[0].NARRATOR_NAME;	//解說人員姓名
								$scope.narratorDisabled = true;
								
								$scope.inputVO.custID = tota[0].body.mainList[0].CUST_ID;				//客戶ID
								
								if ($scope.inputVO.carSEQ) {
									angular.forEach($scope.cartList, function(row){
										if ($scope.inputVO.carSEQ == row.SEQ_NO) {
											$scope.inputVO.tradeSubType = row.TRADE_SUB_TYPE;						//信託型態
								        	$scope.inputVO.certificateID = row.CERTIFICATE_ID;						//憑證編號
											$scope.inputVO.bProdID = row.B_PROD_ID;									//變更前基金代號
											$scope.inputVO.bProdName = row.B_PROD_NAME;								//變更前基金名稱
											$scope.inputVO.bProdCurr = row.B_PROD_CURR;								//變更前產品計價幣別
											$scope.inputVO.bProdRiskLV = row.B_PROD_RISK_LV;						//變更前產品風險等級
											$scope.inputVO.bTrustCurr = row.B_TRUST_CURR;							//變更前產品信託幣別
											$scope.inputVO.bTrustCurrType = row.B_TRUST_CURR_TYPE;					//轉出標的信託幣別類別
											$scope.inputVO.bTrustAmt = row.B_TRUST_AMT; 							//變更前信託金額
											$scope.inputVO.bPurchaseAmtL = row.B_PURCHASE_AMT_L; 					//變更前每月扣款金額_低(定期不定額)；變更前扣款金額(定期定額)
											$scope.inputVO.bPurchaseAmtM = row.B_PURCHASE_AMT_M;					//變更前每月扣款金額_中(定期不定額)
											$scope.inputVO.bPurchaseAmtH = row.B_PURCHASE_AMT_H; 					//變更前每月扣款金額_高(定期不定額)
											$scope.inputVO.bPurchaseAmtExchL= row.B_PURCHASE_AMT_EXCH_L;
											$scope.inputVO.bPurchaseAmtExchM= row.B_PURCHASE_AMT_EXCH_M;
											$scope.inputVO.bPurchaseAmtExchH= row.B_PURCHASE_AMT_EXCH_H;
											
											if($scope.inputVO.bPurchaseAmtExchL)
												$scope.inputVO.bEquivalentAmtL = $scope.inputVO.bPurchaseAmtExchL;
											else
												$scope.inputVO.bEquivalentAmtL = $scope.inputVO.bPurchaseAmtL;
											
											if($scope.inputVO.bPurchaseAmtExchM)
												$scope.inputVO.bEquivalentAmtM = $scope.inputVO.bPurchaseAmtExchM;
											else
												$scope.inputVO.bEquivalentAmtM = $scope.inputVO.bPurchaseAmtM;
											
											if($scope.inputVO.bPurchaseAmtExchH)
												$scope.inputVO.bEquivalentAmtH = $scope.inputVO.bPurchaseAmtExchH;
											else
												$scope.inputVO.bEquivalentAmtH = $scope.inputVO.bPurchaseAmtH;
											
											
											$scope.inputVO.bChargeDate1 = row.B_CHARGE_DATE_1;						//變更前每月扣款日期1
											$scope.inputVO.bChargeDate2 = row.B_CHARGE_DATE_2;						//變更前每月扣款日期2
											$scope.inputVO.bChargeDate3 = row.B_CHARGE_DATE_3;						//變更前每月扣款日期3
											$scope.inputVO.bChargeDate4 = row.B_CHARGE_DATE_4;						//變更前每月扣款日期4
											$scope.inputVO.bChargeDate5 = row.B_CHARGE_DATE_5;						//變更前每月扣款日期5
											$scope.inputVO.bChargeDate6 = row.B_CHARGE_DATE_6;						//變更前每月扣款日期6
											//每月扣款日期1~6 (小額用)
											$scope.inputVO.bChargeDateList = $scope.inputVO.bChargeDate1 + "、" +  $scope.inputVO.bChargeDate2 + "、" +  $scope.inputVO.bChargeDate3 + "、" +  $scope.inputVO.bChargeDate4 + "、" +  $scope.inputVO.bChargeDate5 + "、" +  $scope.inputVO.bChargeDate6;
											$scope.inputVO.bChargeDateList = $scope.inputVO.bChargeDateList.replace(/\、null/g,"");
											$scope.inputVO.bChargeDateList = $scope.inputVO.bChargeDateList.replace(/null/g,"");
											
											 
											$scope.inputVO.bDebitAcct = row.B_DEBIT_ACCT;							//變更前扣款帳號
											$scope.inputVO.bCreditAcct = row.B_CREDIT_ACCT;							//變更前收益入帳帳號
											$scope.inputVO.bTrustAcct = row.B_TRUST_ACCT;							//變更前信託帳號
											$scope.inputVO.bCertificateStatus = row.B_CERTIFICATE_STATUS;			//變更前憑證狀態
											
											$scope.inputVO.fProdID = ('' != row.F_PROD_ID ? row.F_PROD_ID : '');									//變更後基金代號
											$scope.inputVO.fProdName = ('' != row.F_PROD_NAME ? row.F_PROD_NAME : '');								//變更後基金名稱
											$scope.inputVO.fProdCurr = ('' != row.F_PROD_CURR ? row.F_PROD_CURR : '');								//變更後產品計價幣別
											$scope.inputVO.fProdRiskLV = ('' != row.F_PROD_RISK_LV ? row.F_PROD_RISK_LV : '');						//變更後產品風險等級
											$scope.inputVO.fTrustCurr = ('' != row.F_TRUST_CURR ? row.F_TRUST_CURR : '');							//變更後產品信託幣別
											
											/* 不要取變更前的扣款金額*/
											$scope.inputVO.fPurchaseAmtL = row.F_PURCHASE_AMT_L;	                //變更後每月扣款金額_低(定期不定額)
											$scope.inputVO.fPurchaseAmtM = row.F_PURCHASE_AMT_M;	                //變更後每月扣款金額_中(定期不定額)；變更後扣款金額(定期定額)
											$scope.inputVO.fPurchaseAmtH = row.F_PURCHASE_AMT_H;	                //變更後每月扣款金額_高(定期不定額)
											
											
											/* 不要取變更前扣款日期*/ 
											$scope.inputVO.fChargeDate1 = row.F_CHARGE_DATE_1;						//變更後每月扣款日期1
											$scope.inputVO.fChargeDate2 = row.F_CHARGE_DATE_2;						//變更後每月扣款日期2
											$scope.inputVO.fChargeDate3 = row.F_CHARGE_DATE_3;						//變更後每月扣款日期3
											$scope.inputVO.fChargeDate4 = row.F_CHARGE_DATE_4;						//變更後每月扣款日期4
											$scope.inputVO.fChargeDate5 = row.F_CHARGE_DATE_5;						//變更後每月扣款日期5
											$scope.inputVO.fChargeDate6 = row.F_CHARGE_DATE_6;						//變更後每月扣款日期6
											
											//每月扣款日期1~6 (小額用)
											$scope.inputVO.chargeDateList = $scope.inputVO.fChargeDate1 + "、" +  $scope.inputVO.fChargeDate2 + "、" +  $scope.inputVO.fChargeDate3 + "、" +  $scope.inputVO.fChargeDate4 + "、" +  $scope.inputVO.fChargeDate5 + "、" +  $scope.inputVO.fChargeDate6;
											$scope.inputVO.chargeDateList = $scope.inputVO.chargeDateList.replace(/\、null/g,"");
											$scope.inputVO.chargeDateList = $scope.inputVO.chargeDateList.replace(/null/g,"");
											
											$scope.inputVO.fDebitAcct = row.F_DEBIT_ACCT+ '_' +$scope.inputVO.fTrustCurr;	 //變更後扣款帳號
											//alert('1_'+$scope.inputVO.fDebitAcct);
											$scope.inputVO.fCreditAcct = row.F_CREDIT_ACCT;							         //變更後收益入帳帳號
											$scope.inputVO.fCertificateStatus = row.F_CERTIFICATE_STATUS;			         //變更後憑證狀態
											if(row.F_HOLD_START_DATE){
												$scope.inputVO.fHoldStartDate = $scope.toJsDate(row.F_HOLD_START_DATE); 		 //變更後暫停扣款起日
											}
											if(row.F_HOLD_END_DATE){
												$scope.inputVO.fHoldEndDate = $scope.toJsDate(row.F_HOLD_END_DATE); 			 //變更後暫停扣款迄日
											}
											if(row.F_RESUME_DATE){
												$scope.inputVO.fResumeDate = $scope.toJsDate(row.F_RESUME_DATE); 				 //變更後恢復正常扣款起日
											}
											$scope.inputVO.tradeDateType = row.TRADE_DATE_TYPE;                              //交易日期類別
											if(row.TRADE_DATE){
												$scope.inputVO.tradeDate = $scope.toJsDate(row.TRADE_DATE);                      //交易日期
											}
											 
											$scope.inputVO.prospectusType=row.PROSPECTUS_TYPE;//公開說明書交付方式
											$scope.findNfMinBuyAmt();//查詢最低/最高申購金額 (BY tradeSubType 信託幣別currencyType)
										}
					    			});
								}
							}
							
							deferred.resolve("success");
						}
			});
			
			return deferred.promise;
		};
		
		$scope.query = function() {
			$scope.cartList = [];
			
			if($scope.inputVO.tradeSEQ) {
				$scope.noCallCustQuery().then(function(data) {
					var loadEdit = true;
					$scope.getSOTCustInfo(loadEdit,false);
				});
			} else {
				$scope.getTradeSEQ();//取得交易序號
			}
		};
		$scope.query();
		
		$scope.refresh = function (seqNO) {
			if (seqNO) {
				$scope.inputVO.carSEQ = seqNO;
			}
			
			$scope.noCallCustQuery();
		};
		
		//將kyc、產品風險等級擷取最後的數字做比對
		function getLV(val){
			if(val){
				return parseInt(val.substring(val.length-1));
			}
			return 0;
		}
		//擷取每月扣款日期之長度
		function getChargeDateListLeng(str){
			if(str){
				var list = str.split('、');
				var leng=0;
				for(var i=0;i<list.length;i++){
					if(list[i]){
						leng++;
					}
				}
				return leng;
			}
			return 0;
		}
		
		//是否為空值
		$scope.checkIsNull = function(str) {
			if(str == null || str == undefined || str == "") {
				return true;
			} else {
				return false;
			}
		}
		
		$scope.validateSeniorCust = function() {
			//是否有變更扣款日期且次數增加
			var chargeDateChanged = false;
			if(!$scope.checkIsNull($scope.inputVO.chargeDateList)) {
				var bcount = 0;
				for(var idx=1; idx<=6; idx++) {
					var bdate = eval("$scope.inputVO.bChargeDate" + idx)//變更前每月扣款日期
					if(bdate) bcount++;
				}
				var fcount = $scope.inputVO.chargeDateList.split("、");
				if(fcount.length > bcount) chargeDateChanged = true;
			}
			
			if($scope.inputVO.doValidSeniorCustEval && //第一次加入購物車才需檢核
					((!$scope.checkIsNull($scope.inputVO.fProdID) && $scope.inputVO.fProdID != $scope.inputVO.bProdID) || //有變更標的
					 $scope.inputVO.fCertificateStatus == "0" || //有恢復扣款
					 (!$scope.checkIsNull($scope.inputVO.fPurchaseAmtL) && $scope.inputVO.fPurchaseAmtL > $scope.inputVO.bPurchaseAmtL) || //有變更金額且增加扣款金額
					 (!$scope.checkIsNull($scope.inputVO.fPurchaseAmtM) && $scope.inputVO.fPurchaseAmtM > $scope.inputVO.bPurchaseAmtM) || //有變更金額且增加扣款金額
					 (!$scope.checkIsNull($scope.inputVO.fPurchaseAmtH) && $scope.inputVO.fPurchaseAmtH > $scope.inputVO.bPurchaseAmtH) || //有變更金額且增加扣款金額
					 chargeDateChanged)) { //有變更扣款日期且有增加次數
				//PRD100.validSeniorCustEval高齡檢核
				$scope.inputVO.prodType = '1';			//1：基金
				$scope.inputVO.tradeType = '4';     	//4：變更
				$scope.inputVO.seniorAuthType = 'S';	//高齡評估表授權種類(S:下單、A：適配)
				$scope.inputVO.trustTS = 'S';  			//S:特金交易 M:金錢信託
				$scope.inputVO.type = "1";
				$scope.inputVO.cust_id = $scope.inputVO.custID;
				$scope.validSeniorCustEval(); 
				$scope.inputVO.doValidSeniorCustEval = false;
			} else {
				$scope.addCar();
			}
		}
		
		//PRD100.validSeniorCustEval高齡檢核不通過清空變更資料
		$scope.clearCustInfo = function() {
			$scope.inputVO.prodID = "";
			$scope.prodClear();
			$scope.inputVO.doValidSeniorCustEval = true; //不通過之後還是要做高齡檢核
		};
		
		//PRD100.validSeniorCustEval高齡檢核通過後加入購物車
		$scope.reallyInquire = function() {
			$scope.addCar();
		};
		
		$scope.addCar = function() {
			if($scope.inputVO.fPurchaseAmtL==''){
				$scope.inputVO.fPurchaseAmtL = undefined;//變更後每月扣款金額_低(定期不定額)；變更後扣款金額(定期定額)
			}
			if($scope.inputVO.fPurchaseAmtM==''){
				$scope.inputVO.fPurchaseAmtM = undefined;//變更後每月扣款金額_中(定期不定額)
			}
			if($scope.inputVO.fPurchaseAmtH==''){
				$scope.inputVO.fPurchaseAmtH = undefined;//變更後每月扣款金額_高(定期不定額)
			}
			
			if($scope.checkTradeDateType() == false) { //檢查預約和即時
				return;
			}
			
			if($scope.checkX7Change()==false){ //檢查標的和金額
				return;
			}
			debugger
			//檢查當投資標的相同且產品風險等級大於KYC等級
			if(!$scope.inputVO.fProdID || $scope.inputVO.bProdID == $scope.inputVO.fProdID){//判斷prodID是否相同
				var custKycLV = getLV($scope.inputVO.kycLV);
				if($scope.inputVO.custRemarks != "Y" && $scope.inputVO.hnwcYN == "Y" && $scope.inputVO.hnwcServiceYN == "Y" && $scope.inputVO.kycLV != "C4") {
					custKycLV = custKycLV + 1; //高資產客戶且非特定客戶檢查且非C4，客戶可越一級
				}
				if(getLV($scope.inputVO.bProdRiskLV) > custKycLV){//判斷產品風險等級是否大於KYC等級
					if((getChargeDateListLeng($scope.inputVO.bChargeDateList) < getChargeDateListLeng($scope.inputVO.chargeDateList)) || //判斷變更後之每月扣款日期長度是否大於變更前之每月扣款日期長度
						($scope.inputVO.bPurchaseAmtL < $scope.inputVO.fPurchaseAmtL)){//判斷變更後之每月扣款金額是否大於變更前之每月扣款金額
						$scope.inputVO.fPurchaseAmtL  =$scope.inputVO.bPurchaseAmtL;
						$scope.showErrorMsg("ehl_02_sot150_006");
						return;
					}
				}
			}
			
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg("ehl_01_common_022");
	    		return;
	    	}
			
			if ($scope.countList() == 6) {
				$scope.showErrorMsg('ehl_01_SOT_007');
				return;
			}
			
			$scope.isShowAcctList = false; //不顯示帳號清單
			
			//高資產客戶且非特定客戶檢查是否需要做越級適配檢核，C4也不須檢核
			$scope.inputVO.needHnwcRiskValueYN = "N";
			if($scope.inputVO.custRemarks != "Y" && $scope.inputVO.hnwcYN == "Y" && $scope.inputVO.hnwcServiceYN == "Y" && $scope.inputVO.kycLV != "C4") {
				//是否有變更扣款日期且次數增加
				var chargeDateChanged = false;
				if(!$scope.checkIsNull($scope.inputVO.chargeDateList)) {
					var bcount = 0;
					for(var idx=1; idx<=6; idx++) {
						var bdate = eval("$scope.inputVO.bChargeDate" + idx)//變更前每月扣款日期
						if(bdate) bcount++;
					}
					var fcount = $scope.inputVO.chargeDateList.split("、");
					if(fcount.length > bcount) chargeDateChanged = true;
				}
				
				if(((!$scope.checkIsNull($scope.inputVO.fProdID) && $scope.inputVO.fProdID != $scope.inputVO.bProdID) || //有變更標的
						 $scope.inputVO.fCertificateStatus == "0" || //有恢復扣款
						 (!$scope.checkIsNull($scope.inputVO.fPurchaseAmtL) && $scope.inputVO.fPurchaseAmtL > $scope.inputVO.bPurchaseAmtL) || //有變更金額且增加扣款金額
						 (!$scope.checkIsNull($scope.inputVO.fPurchaseAmtM) && $scope.inputVO.fPurchaseAmtM > $scope.inputVO.bPurchaseAmtM) || //有變更金額且增加扣款金額
						 (!$scope.checkIsNull($scope.inputVO.fPurchaseAmtH) && $scope.inputVO.fPurchaseAmtH > $scope.inputVO.bPurchaseAmtH) || //有變更金額且增加扣款金額
						 chargeDateChanged)) { //有變更扣款日期且有增加次數
					$scope.inputVO.needHnwcRiskValueYN = "Y";
				}
			}
			
			$scope.sendRecv("SOT150", "save", "com.systex.jbranch.app.server.fps.sot150.SOT150InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.errorMsg != null) {
								$scope.showErrorMsg(tota[0].body.errorMsg);
								$scope.refresh();
								
								return;
							} else {
								$scope.inputVO.prodID = "";
								$scope.prodClear();
								$scope.refresh();
								
								return;
							}
						}
			});
		};
		
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
		
		$scope.delCar = function(seqNO) {
			var txtMsg = "";
			if ($scope.countList() == 1) {
				txtMsg = $filter('i18n')('ehl_02_common_005');
			} else {
				txtMsg = $filter('i18n')('ehl_02_common_006');
			}
			
			$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
				$scope.sendRecv("SOT150", "delProd", "com.systex.jbranch.app.server.fps.sot150.SOT150InputVO", {tradeSEQ : $scope.inputVO.tradeSEQ, carSEQ: seqNO},
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
		
		//暫存button
		$scope.save = function() { 
			if($scope.checkTradeDateType() == false) { //交易日期類別是否可選擇即時/預約
				return;
			} 
			$scope.sendRecv("SOT150", "save", "com.systex.jbranch.app.server.fps.sot150.SOT150InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.errorMsg != null) {
								$scope.showErrorMsg(tota[0].body.errorMsg);
								$scope.refresh(); 
								return;
							} else {
								$scope.connector('set','SOTTradeSEQ', $scope.inputVO.tradeSEQ);
								$scope.connector('set','SOTCarSEQ', null);  //TODO WHY
								return;
							}
						}
			});
		};
		
		$scope.next = function() {
			if($scope.checkTradeDateType() == false) { //交易日期類別是否可選擇即時/預約
				return;
			}
			
			$scope.sendRecv("SOT150", "next", "com.systex.jbranch.app.server.fps.sot150.SOT150InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.connector('set','SOTTradeSEQ', $scope.inputVO.tradeSEQ);
							$scope.connector('set','SOTCarSEQ', null);
							$rootScope.menuItemInfo.url = "assets/txn/SOT151/SOT151.html";
							
							return;
						}
			});
		};
		
		$scope.goPRD110 = function (mainProd) {
			var custID = $scope.inputVO.custID;
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT150/SOT150_ROUTE.html',
				className: 'PRD110',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					if(mainProd){
						$scope.txnName = "本月主推";
					}else{
						$scope.txnName = "搜尋基金";
					}
					$scope.isPop = true;
	        		$scope.routeURL = 'assets/txn/PRD110/PRD110.html';
	        		$scope.tradeType = "4";
	        		$scope.cust_id = custID;
	        		$scope.main_prd = mainProd;
					$scope.fromSOTProdYN = "Y"; //下單商品搜尋//下單只需要輸入客戶ID時檢核，商品搜尋時不需再次檢核
	            }]
			}).closePromise.then(function (data) {
				if(data.value != "cancel") {
					$scope.inputVO.fProdID = data.value.PRD_ID;
					$scope.getProdDTL();
				}
			});
		};
		//查詢基金庫存基本資料
		$scope.getOutProdDTL = function (outProdID) { 
			if($scope.inputVO.outProdID=='' ) {  
				return;
			}

			var prodDTLInputVo = {'bProdID':outProdID, 'custID':$scope.inputVO.custID,'tradeDateType':$scope.inputVO.tradeDateType ,'tradeDate': $scope.inputVO.tradeDate};
			 		
					$scope.sendRecv("SOT150", "getProdDTL", "com.systex.jbranch.app.server.fps.sot150.SOT150InputVO", prodDTLInputVo ,
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.prodDTL != null && tota[0].body.prodDTL.length > 0) { 		//計價幣別
									$scope.inputVO.bProdRiskLV = tota[0].body.prodDTL[0].RISKCATE_ID;	    //單位現值
									$scope.inputVO.bNotVertify = tota[0].body.prodDTL[0].FUS40;             //未核備欄位FUS40
									var prodFus20 = tota[0].body.prodDTL[0].FUS20;                          //基金註記FUS20 C,N,Y
									if (prodFus20 == 'C') { //國內基金
										$scope.inputVO.bTrustCurrType='C';
									} else if (prodFus20 != 'C') {
										if($scope.inputVO.bTrustCurr=='TWD'){  //基金商品檔國內基金註記 != C，商品庫存幣別為TWD  =>商品為台幣信託
											$scope.inputVO.bTrustCurrType = 'N';//台幣
										}else{                                 //基金商品檔國內基金註記 != C，商品庫存幣別非TWD =>商品為外幣信託 
											$scope.inputVO.bTrustCurrType = 'Y';//外幣  
										}
									}	
									
									return;
								}
							} else {
								$scope.showErrorMsg("ehl_01_common_009");	
							}
				});
		};
		
		
		//選轉入商品 ，查詢 庫存金額換匯
		$scope.getAmtByProdCurr = function () {
			if($scope.inputVO.bProdName && 
					($scope.inputVO.bProdID==$scope.inputVO.fProdID 
							|| $scope.inputVO.bProdCurr==$scope.inputVO.fProdCurr)){
				//同標的、同計價幣別不須查換匯
				
				if ($scope.inputVO.bTrustCurrType=='C' || $scope.inputVO.bTrustCurrType=='N') {  //變更前信託幣別  C國內 N台幣信託
					$scope.inputVO.fTrustCurr = 'TWD';  //變更後產品信託幣別
				} else if ($scope.inputVO.bTrustCurrType=='Y') {  //變更前信託幣別Y外幣
					$scope.inputVO.fTrustCurr = $scope.inputVO.fProdCurr;  //換匯幣別	//變更後產品信託幣別
				} 
				
				//同標的、同計價幣別不須換匯
				$scope.inputVO.bExchCurr = '';
				$scope.inputVO.bPurchaseAmtExchL = undefined;
				$scope.inputVO.bPurchaseAmtExchM = undefined;
				$scope.inputVO.bPurchaseAmtExchH = undefined;
				
				$scope.inputVO.bEquivalentAmtL = $scope.inputVO.bPurchaseAmtL;
				$scope.inputVO.bEquivalentAmtM = $scope.inputVO.bPurchaseAmtM;
				$scope.inputVO.bEquivalentAmtH = $scope.inputVO.bPurchaseAmtH;
				$scope.inputVO.fPurchaseAmtL = $scope.inputVO.bPurchaseAmtL;
				$scope.inputVO.fPurchaseAmtM = $scope.inputVO.bPurchaseAmtM;
				$scope.inputVO.fPurchaseAmtH = $scope.inputVO.bPurchaseAmtH;
				
				$scope.inputVO.fDebitAcct = $scope.inputVO.fDebitAcct.split("_")[0]+ '_' +$scope.inputVO.fTrustCurr;
			}else if($scope.inputVO.bProdName && $scope.inputVO.bProdID!=$scope.inputVO.fProdID){ //要先選庫存
				$scope.sendRecv("SOT703", "getAmtByProdCurr", "com.systex.jbranch.app.server.fps.sot150.SOT150InputVO", $scope.inputVO ,
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.errorMsg != null) {
									$scope.inputVO.fProdID = '';								//變更後基金代號
									$scope.inputVO.fProdName = '';								//變更後基金名稱
									$scope.inputVO.fProdCurr = '';								//變更後產品計價幣別
									$scope.inputVO.fProdRiskLV = '';							//變更後產品風險等級
									$scope.inputVO.fTrustCurr = $scope.inputVO.bTrustCurr;		//變更後產品信託幣別
									$scope.showErrorMsg('查詢庫存金額換匯:'+tota[0].body.errorMsg);
								} else {
									if ($scope.inputVO.bTrustCurrType=='C' || $scope.inputVO.bTrustCurrType=='N') {  //變更前信託幣別  C國內 N台幣信託
										$scope.inputVO.fTrustCurr = "TWD";  //變更後產品信託幣別 
									} else if ($scope.inputVO.bTrustCurrType=='Y') {            //變更前信託幣別Y外幣
										$scope.inputVO.fTrustCurr = $scope.inputVO.fProdCurr;  //換匯幣別	//變更後產品信託幣別
									}
									$scope.inputVO.bExchCurr = tota[0].body.ExchangeCurr;    //exchangeCurr fTrustCurr直接用電文 換匯後幣別
									
									if (tota[0].body.ExchangeAmt1 != null && tota[0].body.ExchangeAmt1 != '0') {
										$scope.inputVO.bPurchaseAmtExchL = tota[0].body.ExchangeAmt1;
										$scope.inputVO.bEquivalentAmtL = tota[0].body.ExchangeAmt1;
										$scope.inputVO.fPurchaseAmtL = tota[0].body.ExchangeAmt1;
									} else {
										$scope.inputVO.bPurchaseAmtExchL = undefined;
										$scope.inputVO.bEquivalentAmtL = $scope.inputVO.bPurchaseAmtL;
										$scope.inputVO.fPurchaseAmtL = $scope.inputVO.bPurchaseAmtL;
									}
									
									if (tota[0].body.ExchangeAmt2 != null && tota[0].body.ExchangeAmt2 != '0') {
										$scope.inputVO.bPurchaseAmtExchM = tota[0].body.ExchangeAmt2;
										$scope.inputVO.bEquivalentAmtM = tota[0].body.ExchangeAmt2;
										$scope.inputVO.fPurchaseAmtM = tota[0].body.ExchangeAmt2;
									} else {
										$scope.inputVO.bPurchaseAmtExchM = undefined;
										$scope.inputVO.bEquivalentAmtM = $scope.inputVO.bPurchaseAmtM;
										$scope.inputVO.fPurchaseAmtM = $scope.inputVO.bPurchaseAmtM;
									}
									
									if (tota[0].body.ExchangeAmt3 != null && tota[0].body.ExchangeAmt3 != '0') {
										$scope.inputVO.bPurchaseAmtExchH = tota[0].body.ExchangeAmt3;								
										$scope.inputVO.bEquivalentAmtH = tota[0].body.ExchangeAmt3;							
										$scope.inputVO.fPurchaseAmtH = tota[0].body.ExchangeAmt3;
									} else {
										$scope.inputVO.bPurchaseAmtExchH = undefined;								
										$scope.inputVO.bEquivalentAmtH = $scope.inputVO.bPurchaseAmtH;							
										$scope.inputVO.fPurchaseAmtH = $scope.inputVO.bPurchaseAmtH;
									}									
									
									$scope.inputVO.fDebitAcct = $scope.inputVO.fDebitAcct.split("_")[0]+ '_' +$scope.inputVO.fTrustCurr;
									//alert('2_'+$scope.inputVO.fDebitAcct);
									$scope.checkTrustAcct();
//									$scope.changeAcct();//1.換扣款帳號和2.換商品都要查詢 該帳號幣別餘額
								}
							} else {
								$scope.showErrorMsg("ehl_01_common_009");
							}
				});
			}

		}

		//庫存查詢
		$scope.goSOT132 = function () {
			var custId = $scope.inputVO.custID;

			var dialog = ngDialog.open({
				template: 'assets/txn/SOT132/SOT132.html',
				className: 'SOT132',
				controller:['$scope',function($scope){
					$scope.txnName = "sot150";
					$scope.custID=custId;
				}],
				showClose: false,
				scope : $scope
			}).closePromise.then(function (data) {
				$scope.prodClear();//清除 庫存/變更標的
				
				//動態鎖利不可變更
				if (data.value.Dynamic && (data.value.Dynamic == '1' || data.value.Dynamic == '2')) {
					$scope.showErrorMsg("請至動態鎖利專區進行交易");
					return;
				}
				
 				$scope.assetType = data.value.AssetType.substring(3,4);  //信託型態
 				$scope.txType = data.value.TxType;
				
 				$scope.sendRecv("SOT140", "isBackend", "com.systex.jbranch.app.server.fps.sot140.SOT140InputVO", {'outProdID': data.value.FundNO},
						function(tota, isError) {
 							if (!isError) {
								if(tota[0].body.prodDTL[0].IS_BACKEND == 'Y'){
									$scope.showErrorMsg('基金變更：不提供後收型基金變更功能');
									return;
								} else if(tota[0].body.prodDTL[0].OVS_PRIVATE_YN == 'Y') {
									$scope.showErrorMsg('基金變更：不提供境外私募基金變更功能');
									return;
								} else {
									// 單筆申購
									if($scope.assetType=="1"){ 
										$scope.inputVO.tradeSubType ="1";
										$scope.inputVO.tradeSubTypeD ="1";
									}else if($scope.assetType=="2"){
										// 定期定額(Fund久久 or Fund心投)
										if(sotService.isFundProjectByTxType($scope.txType)) {
											$scope.inputVO.tradeSubType ="4";
											$scope.inputVO.tradeSubTypeD =$scope.txType=='Y'? "4": "8";
										} else {
											// 定期定額
											$scope.inputVO.tradeSubType ="2";
											$scope.inputVO.tradeSubTypeD ="2";
										}
									}else if($scope.assetType=="3"){
										// 定期不定額(Fund久久 or Fund心投)
										if(sotService.isFundProjectByTxType($scope.txType)) {
											$scope.inputVO.tradeSubType ="5";
											$scope.inputVO.tradeSubTypeD =$scope.txType=='Y'? "5": "9";
										} else {
											// 定期不定額
											$scope.inputVO.tradeSubType ="3";
											$scope.inputVO.tradeSubTypeD ="3";
										}
									}else if($scope.assetType=="4"){          //0004定存轉基金 
											$scope.inputVO.tradeSubType ="6"; //6.定存轉基金
											$scope.inputVO.tradeSubTypeD ="6"; //6.定存轉基金
									}else if($scope.assetType=="5"){          //0005基金套餐
										$scope.inputVO.tradeSubType ="7";     //7.基金套餐
										$scope.inputVO.tradeSubTypeD ="7";     //7.基金套餐
									}
									
									if($scope.txType=='Y')
										$scope.txType='(FUND久久)';//show web
									if($scope.txType=='A')
										$scope.txType='(FUND心投)';//show web
									 
									$scope.inputVO.certificateID = data.value.EviNum;                 	//憑證編號 
									$scope.inputVO.bProdID = data.value.FundNO;		                    //變更前基金代號
									$scope.inputVO.bProdName = data.value.FundName;	                    //變更前基金名稱
									$scope.inputVO.bProdCurr = data.value.CurFund;	                    //變更前產品計價幣別   基金幣別
									$scope.inputVO.bStatus = data.value.Status;							//變更前憑證狀態
									$scope.inputVO.bTrustCurr = data.value.CurCode;			            //變更前產品信託幣別
					 				$scope.inputVO.fTrustCurr = data.value.CurCode;                     //要指定變更後幣別 這樣才能扣款帳號幣別餘額

					 				$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.inputVO.bTrustCurr});
					 				$scope.filterNumber = $scope.mod[0].LABEL;
					 				
					 				//變更前信託金額format小數點
					 				$scope.inputVO.fPurchaseAmtL = $filter('number')($scope.moneyUnFormat($scope.inputVO.fPurchaseAmtL), $scope.filterNumber.toString());
					 				$scope.inputVO.fPurchaseAmtM = $filter('number')($scope.moneyUnFormat($scope.inputVO.fPurchaseAmtM), $scope.filterNumber.toString());
					 				$scope.inputVO.fPurchaseAmtH = $filter('number')($scope.moneyUnFormat($scope.inputVO.fPurchaseAmtH), $scope.filterNumber.toString());
					 				 				
					 				if('TW'==data.value.CurCode){                        
					 					$scope.inputVO.bTrustCurrType = 'N';                             //轉出標的信託幣別類別
									}else{
										$scope.inputVO.bTrustCurrType = 'Y';
									}
					 				 
					 				$scope.inputVO.bTrustAmt = data.value.CurAmt;                         //變更前信託金額    投資金額
					 				if($scope.inputVO.tradeSubType!='1'){ 
										if($scope.inputVO.tradeSubType=='2' || $scope.inputVO.tradeSubType=='4'){
											$scope.inputVO.bPurchaseAmtL = data.value.TransferAmt;                //定期定額每月扣款金額
											$scope.inputVO.bEquivalentAmtL = $scope.inputVO.bPurchaseAmtL;        //定期定額每月扣款金額 
										}else if($scope.inputVO.tradeSubType=='3' || $scope.inputVO.tradeSubType=='5'){
											$scope.inputVO.bPurchaseAmtL = data.value.TransferAmt_L;              //BEFORE定期不定額 每月扣款金額L
											$scope.inputVO.bPurchaseAmtM = data.value.TransferAmt_M;              //BEFORE每月扣款金額M
											$scope.inputVO.bPurchaseAmtH = data.value.TransferAmt_H;              //BEFORE每月扣款金額H 
											$scope.inputVO.bEquivalentAmtL = data.value.TransferAmt_L;            //BEFORE每月扣款金額L (換匯或不換匯) 
											$scope.inputVO.bEquivalentAmtM = data.value.TransferAmt_M;            //BEFORE每月扣款金額M
											$scope.inputVO.bEquivalentAmtH = data.value.TransferAmt_H;            //BEFORE每月扣款金額H
										}
										
										$scope.inputVO.bChargeDate1 = data.value.TransferDate01;						  //變更前每月扣款日期1
										$scope.inputVO.bChargeDate2 = data.value.TransferDate02;						  //變更前每月扣款日期2
										$scope.inputVO.bChargeDate3 = data.value.TransferDate03;						  //變更前每月扣款日期3
										$scope.inputVO.bChargeDate4 = data.value.TransferDate04;						  //變更前每月扣款日期4
										$scope.inputVO.bChargeDate5 = data.value.TransferDate05;						  //變更前每月扣款日期5
										$scope.inputVO.bChargeDate6 = data.value.TransferDate06;						  //變更前每月扣款日期6
										//每月扣款日期1~6 (小額用)
					 				 
									
										$scope.inputVO.bChargeDateList = $scope.inputVO.bChargeDate1 + "、" +  $scope.inputVO.bChargeDate2 + "、" +  $scope.inputVO.bChargeDate3 + "、" +  $scope.inputVO.bChargeDate4 + "、" +  $scope.inputVO.bChargeDate5 + "、" +  $scope.inputVO.bChargeDate6;
										$scope.inputVO.bChargeDateList = $scope.inputVO.bChargeDateList.replace(/\、null/g,"");
										$scope.inputVO.bChargeDateList = $scope.inputVO.bChargeDateList.replace(/null/g,"");
										$scope.inputVO.bDebitAcct = data.value.PayAccountNo;							   		//變更前扣款帳號
										$scope.inputVO.fDebitAcct = $scope.inputVO.bDebitAcct+ '_' +$scope.inputVO.bTrustCurr;  //變更後扣款帳號  =變更前扣款帳號
						 			}
									$scope.inputVO.bCreditAcct = data.value.PayAcctId;								       //變更前收益入帳帳號
					 				$scope.inputVO.fCreditAcct = $scope.inputVO.bCreditAcct;                               //變更後收益入帳帳號=變更前收益入帳帳號
					 				$scope.inputVO.bTrustAcct = data.value.AcctId02;								       //變更前信託帳號
					 				$scope.inputVO.trustAcct = $scope.inputVO.bTrustAcct;  //因帳號變更checkTrustAcct()需要trustAcct
					 				if($scope.inputVO.tradeSubType!='1'){
					 					$scope.inputVO.bCertificateStatus = data.value.Status;							   //變更前憑證狀態
					 				}
					 				
									$scope.getOutProdDTL($scope.inputVO.bProdID);//查轉出標的產品風險等級
									
									
									$scope.inputVO.debitAvbBalance = undefined;    //扣款帳號餘額
									$scope.avlCurrency = undefined;                //扣款帳號餘額幣別
									$scope.checkTrustAcct();
//									$scope.changeAcct();//1.換扣款帳號和2.換商品都要查詢 該帳號幣別餘額
									
									
									$scope.canDoChange();//控制可以做那些變更的檢核
									
//									$scope.inputProdDTL($scope.row);
//									
//									$scope.connector('set', 'SOT222', '');
									//#0695 排除數存戶
									if(sotService.is168($scope.inputVO.bTrustAcct) && sotService.isDigitAcct($scope.inputVO.bTrustAcct,$scope.mappingSet['SOT.DEBIT_ACCT_LIST'])){
										$scope.showErrorMsg("ehl_02_SOT_995");
										$scope.prodClear();
									} 
								}
 							}	
 						}
 				);
				
			});
 
		};
		
		$scope.changeAcct = function (type) { 
			if($scope.isGetSOTCustInfo){ //查詢完客戶後才能changeAcct()
				//			console.log('changeAcct()type:' + type +' , isGetSOTCustInfo:' + $scope.isGetSOTCustInfo);
				
				/*
				if (type == 'debit') {
					$scope.inputVO.fCreditAcct = $scope.inputVO.fDebitAcct.split("_")[0];
				} else {
					$scope.inputVO.fDebitAcct = $scope.inputVO.fCreditAcct + '_' + $scope.inputVO.fTrustCurr;
				}
				*/
				
				//			console.log("set2 inputVO.debitAcct:" + $scope.inputVO.debitAcct);
				//			console.log("set2 inputVO.trustAcct:" + $scope.inputVO.trustAcct);
				//			console.log("set2 inputVO.creditAcct:" + $scope.inputVO.creditAcct);
				//取得 相同商品別的 扣款帳後餘額
				 
				$scope.avlCurrency=undefined;
				$scope.inputVO.debitAvbBalance=undefined; 
				//有幣別和餘額
				angular.forEach($scope.mappingSet['SOT.DEBIT_ACCT_LIST#DISPLAY'], function(acctCcyRow){
					if ($scope.inputVO.fDebitAcct == acctCcyRow.DATA) {  
						$scope.inputVO.debitAvbBalance = acctCcyRow.ACCT_BALACNE;
//						$scope.inputVO.debitAvbBalance = acctCcyRow.AVB_BALANCE;
							$scope.avlCurrency = acctCcyRow.CURRENCY;
							//			console.log('debitAcct acct:'+acctCcyRow.DATA+' currency:'+$scope.avlCurrency +', debitAvbBalance:'+$scope.inputVO.debitAvbBalance);
					}
				});
				
				//alert('3_'+$scope.inputVO.fDebitAcct);
			
			} 
			
		};
		 
		//將帳號轉成display暫存陣列
		$scope.setAcctDisplay = function(acctNameList,prodCurr) {
			for (var i=0;i<acctNameList.length;i++) {
				if (acctNameList[i]=='SOT.CREDIT_ACCT_LIST') {
					$scope.mappingSet[acctNameList[i]+'#DISPLAY'] = $scope.mappingSet[acctNameList[i]];
				} else {
					if (prodCurr) {
						$scope.mappingSet[acctNameList[i]+'#DISPLAY'] = $filter('filter')($scope.mappingSet[acctNameList[i]], {CURRENCY: prodCurr});
					} else {
						$scope.mappingSet[acctNameList[i]+'#DISPLAY'] = $scope.mappingSet[acctNameList[i]];
					}
				}
			}
		};
		
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
				//信託帳號檢核
//				var trustStr = $scope.inputVO.trustAcct.substr(3,3);
//				if ($scope.inputVO.trustAcct && trustStr == '168') {
//					$scope.mappingSet[debitAcctListDisplay] = $filter('filter')($scope.mappingSet[debitAcctListDisplay],
//																			{DATA: $scope.inputVO.trustAcct},
//																			function(actual, expected) { return angular.equals(actual.split("_")[0], expected)}
//																		  );
//					$scope.mappingSet[creditAcctListDisplay] = $filter('filter')($scope.mappingSet[creditAcctListDisplay],
//																			{DATA: $scope.inputVO.trustAcct},
//																			function(actual, expected) { return angular.equals(actual, expected)}
//																		   );
//				}
				//有傳信託業務別 和 商品幣別 
				if ($scope.inputVO.bTrustCurrType) {
		 		   if($scope.inputVO.bTrustCurrType=='N' || $scope.inputVO.bTrustCurrType=='C'){ //N台幣
		 			 $scope.mappingSet[debitAcctListDisplay] = $filter('filter')($scope.mappingSet[debitAcctListDisplay], {CURRENCY: 'TWD'});
		 			 //$scope.mappingSet[creditAcctListDisplay] = $filter('filter')($scope.mappingSet[creditAcctListDisplay], {CURRENCY: 'TWD'});
		 		   }else if($scope.inputVO.bTrustCurrType=='Y' && $scope.inputVO.fTrustCurr){	  //外幣信託  & 變更後商品信託幣別(未選變更後，則 變更前商品信託幣別)
		 			  $scope.mappingSet[debitAcctListDisplay] = $filter('filter')($scope.mappingSet[debitAcctListDisplay], {CURRENCY: $scope.inputVO.fTrustCurr});
			 		  //$scope.mappingSet[creditAcctListDisplay] = $filter('filter')($scope.mappingSet[creditAcctListDisplay], {CURRENCY: $scope.inputVO.prodCurr}); 
		 		   }else if ($scope.inputVO.bTrustCurrType=='Y'){  //Y外幣
		 			  $scope.mappingSet[debitAcctListDisplay] = $filter('filter')($scope.mappingSet[debitAcctListDisplay],{CURRENCY: "TWD"},function(actual, expected) {  return !angular.equals(actual, expected)});
			 		  //$scope.mappingSet[creditAcctListDisplay] = $filter('filter')($scope.mappingSet[creditAcctListDisplay],{CURRENCY: "TWD"},function(actual, expected) {  return !angular.equals(actual, expected)});
		 		   }
				}

//				if($scope.mappingSet[debitAcctListDisplay].length==1){ //只有一筆不能勾選
//					$scope.inputVO.fDebitAcct=$scope.mappingSet[debitAcctListDisplay][0].DATA;
//					$scope.cmbDebitAcct=true;
//				}
//				if($scope.mappingSet[creditAcctListDisplay].length==1){ //只有一筆不能勾選
//					$scope.inputVO.fCreditAcct=$scope.mappingSet[creditAcctListDisplay][0].DATA;
//					$scope.cmbCreditAcct=true;
//				}
				//alert('4_'+$scope.inputVO.fDebitAcct);
				$scope.changeAcct();//查詢餘額
		};
		
		/**儲存格合併、加總**/
		//計算合併列數
		$scope.numGroups = function(input){
			if(input == null)
				return;
            return Object.keys(input).length;
        }        
		//橫向加總
        $scope.getSum = function(group, key) {
        	var sum = 0;
            for (var i = 0; i < group.length; i++){            	
            	sum += _.sum(group[i][key].split(',').map(Number));
            }  
            return sum;
        }        
        //直向加總
        $scope.getSumya = function(group, key) {
        	var sum = [];
        	var ha = 0;        	
        	for(var i = 0; i < group[0][key].split(',').map(Number).length; i++){
        		for (var j = 0; j < group.length; j++){
        			ha += group[j][key].split(',').map(Number)[i];
        		}          		
        		sum.push(ha);
        		ha = 0;
        	}        	
            return sum; 
        }
        
        
        //查詢最低/最高申購金額 (BY tradeSubType 信託幣別currencyType,比照SOT120)
		$scope.findNfMinBuyAmt = function(){
			var PARAM_CODE='';
			var MIN_BUY_AMT='';
			/*
			if('N'==$scope.inputVO.bTrustCurrType){
				PARAM_CODE='TWD';
			}else{
				PARAM_CODE=$scope.inputVO.prodCurr; //產品計價幣別
			}
			*/
			PARAM_CODE=$scope.inputVO.fTrustCurr; //變更後產品信託幣別
			//PARAM_CODE=$scope.inputVO.bExchCurr;//改由換匯電文後取得
			var tradeSubType = $scope.inputVO.tradeSubType; 
			if(tradeSubType=='2' || tradeSubType=='4')
				MIN_BUY_AMT='SOT.NF_MIN_BUY_AMT_2';
			else if (tradeSubType=='3' || tradeSubType=='5')
				MIN_BUY_AMT='SOT.NF_MIN_BUY_AMT_3';
			
			//信託型態
	        var vo = {'param_type': MIN_BUY_AMT, 'desc': false};
	        $scope.inputVO.prodMinBuyAmt = 0;
	        $scope.inputVO.MaxAmtOfSPurchase = 0;
	        	$scope.requestComboBox(vo, function(totas) {
	        		if (totas[totas.length - 1].body.result === 'success') {
        				angular.forEach(totas[0].body.result, function(row){
        	        		if(row.DATA == PARAM_CODE){
        	        			$scope.inputVO.prodMinBuyAmt= row.LABEL;
        	        			$scope.inputVO.BaseAmtOfSPurchaseCurr= row.DATA;
        	        			//			console.log("NF_MIN_BUY_AMT:" + MIN_BUY_AMT + "," + row.DATA + "," + row.LABEL);
        	        		}
        	        	});
	        		}
	        });
	        	 	
	        if(tradeSubType=='4' || tradeSubType=='5') {
	        	//信託型態 ; FUND99申購最高金額：SOT.NF_MAX_BUY_AMT

		        var vo = {'param_type': 'SOT.NF_MAX_BUY_AMT', 'desc': false};
		        $scope.inputVO.prodMinBuyAmt="";
		        	$scope.requestComboBox(vo, function(totas) {
		        		if (totas[totas.length - 1].body.result === 'success') {
	        				angular.forEach(totas[0].body.result, function(row) {
	        	        		if(row.DATA == PARAM_CODE) {
	        	        			$scope.inputVO.MaxAmtOfSPurchase = row.LABEL;
	        	        			$scope.inputVO.MaxAmtOfSPurchaseCurr = row.DATA;
	        	        			//			console.log("MaxAmtOfSPurchase:" + MIN_BUY_AMT + "," + row.DATA + "," + row.LABEL);
	        	        		}
	        	        	});
		        		}
		        });
	        }	
	        
		}
        //檢查 申購金額 要在 合理範圍內 (比照SOT120)
		$scope.checkPurchaseAmtLimit = function() {
				var prodMinBuyAmt = Number($scope.inputVO.prodMinBuyAmt);
				var MaxAmtOfSPurchase = Number($scope.inputVO.MaxAmtOfSPurchase);
//				console.log("checkPurchaseAmtLimit:"+$scope.inputVO.tradeSubType  +" : " + prodMinBuyAmt + " : "+ Number($scope.inputVO.fPurchaseAmtL));
				if (($scope.inputVO.tradeSubType == '2') || ($scope.inputVO.tradeSubType == '4')) {
					var purchaseAmt = Number($scope.inputVO.fPurchaseAmtL);
					if($scope.inputVO.tradeSubType == '2') {
						if(purchaseAmt > 0 && purchaseAmt < prodMinBuyAmt) {
							$scope.showErrorMsg("申購金額"+purchaseAmt+" 不可小於最低申購金額" + prodMinBuyAmt);
							$scope.inputVO.purchaseAmt = undefined;
						}
						
					} else {
						if(purchaseAmt > 0 && (purchaseAmt < prodMinBuyAmt ||
								(purchaseAmt > MaxAmtOfSPurchase && MaxAmtOfSPurchase!=0))) {
							$scope.showErrorMsg("申購金額"+purchaseAmt+" 需介於最低申購金額" + prodMinBuyAmt + " 與最高申購金額 " + MaxAmtOfSPurchase + " 間");
							$scope.inputVO.purchaseAmt = undefined;
						}
					}
				}
				if (($scope.inputVO.tradeSubType == '3') || ($scope.inputVO.tradeSubType == '5')) {
					var purchaseAmtL = Number($scope.inputVO.fPurchaseAmtL);
					var purchaseAmtM = Number($scope.inputVO.fPurchaseAmtM);
					var purchaseAmtH = Number($scope.inputVO.fPurchaseAmtH);
					
					if($scope.inputVO.tradeSubType == '3') {			
						if(purchaseAmtL > 0 && purchaseAmtL < prodMinBuyAmt) {
							$scope.showErrorMsg("申購金額低"+purchaseAmtL+"  不可小於最低申購金額" + prodMinBuyAmt);
							$scope.inputVO.purchaseAmtL = undefined;
						}
						if(purchaseAmtM > 0 && purchaseAmtM < prodMinBuyAmt) {
							$scope.showErrorMsg("申購金額中"+purchaseAmtM+"  不可小於最低申購金額" + prodMinBuyAmt);
							$scope.inputVO.purchaseAmtM = undefined;
						}
						if(purchaseAmtH > 0 && purchaseAmtH < prodMinBuyAmt) {
							$scope.showErrorMsg("申購金額高"+purchaseAmtH+"  不可小於最低申購金額" + prodMinBuyAmt);
							$scope.inputVO.purchaseAmtH = undefined;
						} 	
					} else {	
						if(purchaseAmtL > 0 && (purchaseAmtL < prodMinBuyAmt ||
								(purchaseAmtL > MaxAmtOfSPurchase && MaxAmtOfSPurchase!=0))) {
							$scope.showErrorMsg("申購金額低 "+purchaseAmtL+" 需介於最低申購金額" + prodMinBuyAmt + " 與最高申購金額 " + MaxAmtOfSPurchase + " 間");
							$scope.inputVO.purchaseAmtL = undefined;
						}
						if(purchaseAmtM > 0 && (purchaseAmtM < prodMinBuyAmt ||
								purchaseAmtM < purchaseAmtL ||
								(purchaseAmtM > MaxAmtOfSPurchase && MaxAmtOfSPurchase!=0))) {
							$scope.showErrorMsg("申購金額中 "+purchaseAmtM+" 需介於最低申購金額" + prodMinBuyAmt + " 與最高申購金額 " + MaxAmtOfSPurchase + " 間");
							$scope.inputVO.purchaseAmtM = undefined;
						}
						if(purchaseAmtH > 0 && (purchaseAmtH < prodMinBuyAmt ||
								purchaseAmtH < purchaseAmtL ||
								purchaseAmtH < purchaseAmtM ||
								(purchaseAmtH > MaxAmtOfSPurchase && MaxAmtOfSPurchase!=0))) {
							$scope.showErrorMsg("申購金額高 "+purchaseAmtH+" 需介於最低申購金額" + prodMinBuyAmt + " 與最高申購金額 " + MaxAmtOfSPurchase + " 間");
							$scope.inputVO.purchaseAmtH = undefined;
						} 
					}
				}			
		}
		
		$scope.checkX7Change = function(){
			//無輸入標的 但有金額
			if(!$scope.inputVO.fProdName &&	($scope.inputVO.fPurchaseAmtL || $scope.inputVO.fPurchaseAmtM || $scope.inputVO.fPurchaseAmtH)){
				$scope.showErrorMsg("請同時輸入變更標的和金額");
				return false;
			}
			//有標的 但無金額L
			if(($scope.inputVO.tradeSubType=='2' || $scope.inputVO.tradeSubType=='4') && $scope.inputVO.fProdName && (!$scope.inputVO.fPurchaseAmtL)){
				$scope.showErrorMsg("請同時輸入變更標的和金額");
				return false;
			}
			//有標的 但無金額LMH
			if(($scope.inputVO.tradeSubType=='3' || $scope.inputVO.tradeSubType=='5') && $scope.inputVO.fProdName && (!$scope.inputVO.fPurchaseAmtL || !$scope.inputVO.fPurchaseAmtM || !$scope.inputVO.fPurchaseAmtH)){
				$scope.showErrorMsg("請同時輸入變更標的和金額");
				return false;
			}
			return true;
		}
		
		
		
		//控制可以變更
		$scope.canDoChange = function () {
			
			$scope.canCmd = {};
			
			/*
			瑞國2/21 MAIL提供的檢核
			1.扣款日期變更/扣款帳號變更/基金暫停扣款/基金契約終止
			  庫存不可為單筆申購,定轉基,基金套餐
			2.扣款標的/金額變更
			  庫存不可為單筆申購 ,基金套餐
			3.基金恢復扣款
			  庫存不可為單筆申購 ,基金套餐
			4.暫停扣款起日<=今日
			  暫停扣款迄日>=今日
			5.收益入帳帳號變更
			  收益入帳與信託帳號相同且為168帳號時不可變更
			  */
			
			
			//檢核
			if($scope.assetType!="1" && $scope.assetType!="2" && $scope.assetType!="3") {
				$scope.showErrorMsg('只有 單筆/定期定額/定期不定額，才能做基金變更');
				$scope.prodClear();
				return;
			}
			if($scope.inputVO.bCertificateStatus=="2" ) {  //2:非主標的  (不可做任何變更)
				$scope.showErrorMsg("ehl_02_sot150_001");//該庫存非主標的不可變更
				$scope.prodClear();
				return;
			}
			
			//是否為新興市場之非投資等級債券型基金
			$scope.inputVO.nfs100 = false;
			$scope.sendRecv("SOT150", "getNFS100YN", "com.systex.jbranch.app.server.fps.sot150.SOT150InputVO", {'bProdID': $scope.inputVO.bProdID},
				function(tota, isError) {
					if (!isError) {
						//客戶如庫存新興市場之非投資等級債券基金，65歲以上非專投客戶不得增加扣款日期、金額及辦理暫停扣款後，不受理恢復扣款之申請
						$scope.inputVO.nfs100 = (tota[0].body.nfs100YN == 'Y' && $scope.inputVO.custAgeUnder65YN != 'Y' && $scope.inputVO.profInvestorYN !='Y');
						
						// A9：扣款日期變更
						 $scope.canDoA9();
						 //A8：扣款帳號變更
						 $scope.canDoA8();
						 // AX：標的變更
						 $scope.canDoAX();
						 // A7：金額變更
						 $scope.canDoA7();
						 // X7：標的與金額變更
						 $scope.canDoX7();
						 // B2：正常扣款變更
						 $scope.canDoB2();
						 // B1：暫停扣款變更
						 $scope.canDoB1();
						 // B8：終止憑證變更
						 $scope.canDoB8();
						 // A0：新收益入帳帳號變更
						 $scope.canDoA0();
					}
			});
		};
		
		//檢查憑證狀態變更
		$scope.checkAfterStatus = function () {
			
			 var beforeCertificateStatusLabel = '';
			 var afterCertificateStatusLabel = '';
			 angular.forEach($scope.mappingSet['SOT.CERTIFICATE_STATUS'], function(row, index, objs){	
        		if (row.DATA == $scope.inputVO.fCertificateStatus) {
        			afterCertificateStatusLabel = row.LABEL; 
				}
			 });
			 angular.forEach($scope.mappingSet['SOT.CERTIFICATE_STATUS'], function(row, index, objs){	
        		if (row.DATA == $scope.inputVO.bCertificateStatus) {
        			beforeCertificateStatusLabel = row.LABEL; 
				}
			 });
		     //0:正常   B2：正常扣款變更
			 //1:暫停   B1：暫停扣款變更
			 //3:終止   B8：終止憑證變更
			 if($scope.inputVO.bProdID && $scope.inputVO.fCertificateStatus=='0' && $scope.canCmd.B2==false){
				 $scope.inputVO.fCertificateStatus='';
				 if($scope.inputVO.nfs100) {
					 $scope.showErrorMsg('65歲以上非專業投資人不可恢復扣款新興市場非投資等級債券基金');
				 } else {
					 $scope.showErrorMsg('ehl_02_sot150_002',[beforeCertificateStatusLabel,afterCertificateStatusLabel]);
				 }
			 }
			 if($scope.inputVO.bProdID && $scope.inputVO.fCertificateStatus=='1' && $scope.canCmd.B1==false){
				 $scope.inputVO.fCertificateStatus='';
				 $scope.showErrorMsg('ehl_02_sot150_002',[beforeCertificateStatusLabel,afterCertificateStatusLabel]);
			 }
			 if($scope.inputVO.bProdID && $scope.inputVO.fCertificateStatus=='3' && $scope.canCmd.B8==false){
				 $scope.inputVO.fCertificateStatus='';
				 $scope.showErrorMsg('ehl_02_sot150_004',[$scope.inputVO.bTrustAmt]);
			 }
			 
			 if ($scope.inputVO.fCertificateStatus == '0') {
				 $scope.prodRiskLV = "";//取產品風險等級
				 if ($scope.inputVO.fProdRiskLV) {
					 $scope.prodRiskLV = $scope.inputVO.fProdRiskLV;
				 } else {
					 $scope.prodRiskLV = $scope.inputVO.bProdRiskLV;
				 }
				 debugger
				 var custKycLV = getLV($scope.inputVO.kycLV);
					if($scope.inputVO.custRemarks != "Y" && $scope.inputVO.hnwcYN == "Y" && $scope.inputVO.hnwcServiceYN == "Y" && $scope.inputVO.kycLV != "C4") {
						custKycLV = custKycLV + 1; //高資產客戶且非特定客戶檢查且非C4，客戶可越一級
					}
				 if (getLV($scope.prodRiskLV) > custKycLV) { //判斷產品風險等級是否大於KYC等級
					 $scope.inputVO.fCertificateStatus='';
					 $scope.showErrorMsg("客戶欲變更扣款條件之基金風險等級("+$scope.prodRiskLV+")已超過客戶的投資風險承受度("+$scope.inputVO.kycLV+")，無法執行本交易。");
				 }
				 
				 $scope.toDay = $filter('date')(new Date(),'yyyy-MM-dd 00:00:00');//取當日日期
				 if ($scope.inputVO.kycDueDate < $scope.toJsDate($scope.toDay)) { //若KYC過期，需有訊息提示客戶。
					 $scope.inputVO.fCertificateStatus='';
					 $scope.showErrorMsg("客戶欲變更扣款條件之基金風險等級("+$scope.prodRiskLV+")已超過客戶的投資風險承受度(已失效)，無法執行本交易。");
				 }				 
			 }
			 
		 };
		
		 // A9：扣款日期變更
		 $scope.canDoA9 = function () {
			 /*
			  * 庫存不可為1.單筆申購,6.定轉基,7.基金套餐, 新興市場之非投資等級債券基金
			  * */
			 if($scope.inputVO.tradeSubType=='1' || $scope.inputVO.tradeSubType=='6' || $scope.inputVO.tradeSubType=='7' || $scope.inputVO.nfs100){
				 $scope.canCmd.A9=false;	 
			 }else{
				 $scope.canCmd.A9=true;
			 }
		 };
		 
		 // AX：標的變更
		 $scope.canDoAX = function () {
			 /*
			  * 庫存不可為1.單筆申購 ,7.基金套餐 
			  * */
			 if($scope.inputVO.tradeSubType=='1' || $scope.inputVO.tradeSubType=='7'){
				 $scope.canCmd.AX=false;	 
			 }else{
				 $scope.canCmd.AX=true;
			 }
		 };
		 
		 // A7：金額變更
		 $scope.canDoA7 = function () {
			 /*
			  * 庫存不可為1.單筆申購 ,7.基金套餐, 新興市場之非投資等級債券基金
			  * */
			 if($scope.inputVO.tradeSubType=='1' || $scope.inputVO.tradeSubType=='7' || $scope.inputVO.nfs100){
				 $scope.canCmd.A7=false;	 
			 }else{
				 $scope.canCmd.A7=true;
			 }
		 };
		 
		 // X7：標的與金額變更
		 $scope.canDoX7 = function () {
			 /*
			  * 庫存不可為1.單筆申購 ,7.基金套餐 
			  * */
			 if($scope.inputVO.tradeSubType=='1' || $scope.inputVO.tradeSubType=='7'){
				 $scope.canCmd.X7=false;	 
			 }else{
				 $scope.canCmd.X7=true;
			 }
		 };
		 
		 // B2：正常扣款變更
		 $scope.canDoB2 = function () {
			 /*
			  * 恢復
				庫存狀態C30=1暫停 
                                        庫存不可為1.單筆申購 ,7.基金套餐, 新興市場之非投資等級債券基金 
			  * 
			  * */
			 if($scope.inputVO.tradeSubType=='1' || $scope.inputVO.tradeSubType=='7' || $scope.inputVO.nfs100){
				 $scope.canCmd.B2=false;
			 }else if($scope.inputVO.bCertificateStatus=='0'){
				 $scope.canCmd.B2=false;
			 }else if($scope.inputVO.bCertificateStatus=='1'){ 
				 $scope.canCmd.B2=true;
			 } 
		 };
		 
		 // B1：暫停扣款變更
		 $scope.canDoB1 = function () {
			 /*
			  * 庫存狀態C30=0正常 
			  * */
			  
			 if($scope.inputVO.tradeSubType=='1' || $scope.inputVO.tradeSubType=='6' || $scope.inputVO.tradeSubType=='7'){
				 $scope.canCmd.B1=false;	 
			 }else if($scope.inputVO.bCertificateStatus=='1'){ 
				 $scope.canCmd.B1=false;
			 }else if($scope.inputVO.bCertificateStatus=='0'){ 
				 $scope.canCmd.B1=true;
			 }
		 };
		 
		 //B1：暫停      暫停扣款起日<=今日      暫停扣款迄日>=今日
		 $scope.checkfHoldStartEndDate = function (){
			 if($scope.inputVO.fHoldStartDate && $scope.toJsDate($scope.inputVO.fHoldStartDate) > new Date()){
				 $scope.showErrorMsg('ehl_02_sot150_003'); //暫停扣款起日<=今日
				 $scope.inputVO.fHoldStartDate='';
				 return false;
			 }
             if($scope.inputVO.fHoldEndDate && $scope.toJsDate($scope.inputVO.fHoldEndDate) < new Date()){
            	 $scope.showErrorMsg('ehl_02_sot150_003');//暫停扣款迄日>=今日
            	 $scope.inputVO.fHoldEndDate='';
            	 return false;
			 } 
			 return true;
		 }
		 
		 // B8：終止憑證變更
		 $scope.canDoB8 = function () {
			 /* 庫存餘額需為為0
			  * 庫存不可為1.單筆申購,6.定轉基,7.基金套餐
			  * */
			 if($scope.inputVO.tradeSubType=='1' || $scope.inputVO.tradeSubType=='6' || $scope.inputVO.tradeSubType=='7'){
				// $scope.showErrorMsg('終止憑證變更  庫存不可為1.單筆申購,6.定轉基,7.基金套餐');
				 $scope.canCmd.B8=false;
			 }else if($scope.inputVO.bTrustAmt==0){  //庫存餘額需為為0
				 $scope.canCmd.B8=true;
			 }else if($scope.inputVO.bTrustAmt!=0){
				// $scope.showWarningMsg('ehl_02_sot150_004',[$scope.inputVO.bTrustAmt]);//庫存餘額為0不能  終止變更
				 $scope.canCmd.B8=false;
			 }
			   
		 };
		 
		 // A0：新收益入帳帳號變更
		 $scope.canDoA0 = function () {
			 /* 
			  * AcctId02庫存信託帳號
			  * 收益入帳與信託帳號相同且為168帳號時不可變更
			  * */
			 if($scope.inputVO.bTrustAcct==$scope.inputVO.bCreditAcct && sotService.is168($scope.inputVO.bCreditAcct)){
				// $scope.showWarningMsg('ehl_02_sot150_005');//收益入帳號與信託帳號相同且為168帳號時不可變更
				 $scope.canCmd.A0=false;
			 }else{
				 $scope.canCmd.A0=true;
			 }
		 };
		 
		 //A8：扣款帳號變更
		 $scope.canDoA8 = function () {
			 /*  
			  * 庫存不可為1.單筆申購,6.定轉基,7.基金套餐 
			  * */
			 if($scope.inputVO.tradeSubType=='1' || $scope.inputVO.tradeSubType=='6' || $scope.inputVO.tradeSubType=='7'){
				 $scope.canCmd.A8=false;	 
			 }else{
				 //扣款帳號與信託帳號相同且為168帳號時，可變更為其他扣款帳號 (94年開放可以改變)
				 $scope.canCmd.A8=true;
			 }
		 };
		
		 
		 $scope.showAcctList = function () {
			 if($scope.isShowAcctList){
				 $scope.isShowAcctList = false;
			 }else{
				 $scope.isShowAcctList = true;
			 }
		 }
		 
		 
			//由客管導頁進入
			$scope.inbyCRM821 = function(){
				if($scope.connector('get','SOTCustID')){
					$scope.inputVO.custID = $scope.connector('get','SOTCustID');
					var data = $scope.connector('get','SOTProd');
					$scope.getSOTCustInfo(true,true);
					if(data){
						$scope.prodClear();//清除 庫存/變更標的
		 				$scope.assetType = data.AssetType.substring(3,4);  //信託型態
		 				$scope.txType = data.TxType;


						// 單筆申購
						if($scope.assetType=="1"){
							$scope.inputVO.tradeSubType ="1";
							$scope.inputVO.tradeSubTypeD ="1";
						}else if($scope.assetType=="2"){
							// 定期定額(Fund久久 or Fund心投)
							if(sotService.isFundProjectByTxType($scope.txType)) {
								$scope.inputVO.tradeSubType ="4";
								$scope.inputVO.tradeSubTypeD =$scope.txType=='Y'? "4": "8";
							} else {
								// 定期定額
								$scope.inputVO.tradeSubType ="2";
								$scope.inputVO.tradeSubTypeD ="2";
							}
						}else if($scope.assetType=="3"){
							// 定期不定額(Fund久久 or Fund心投)
							if(sotService.isFundProjectByTxType($scope.txType)) {
								$scope.inputVO.tradeSubType ="5";
								$scope.inputVO.tradeSubTypeD =$scope.txType=='Y'? "5": "9";
							} else {
								// 定期不定額
								$scope.inputVO.tradeSubType ="3";
								$scope.inputVO.tradeSubTypeD ="3";
							}
						}else if($scope.assetType=="4"){          //0004定存轉基金
							$scope.inputVO.tradeSubType ="6"; //6.定存轉基金
							$scope.inputVO.tradeSubTypeD ="6"; //6.定存轉基金
						}else if($scope.assetType=="5"){          //0005基金套餐
							$scope.inputVO.tradeSubType ="7";     //7.基金套餐
							$scope.inputVO.tradeSubTypeD ="7";     //7.基金套餐
						}
						
						if($scope.txType=='Y')
							$scope.txType='(FUND久久)';//show web
						if($scope.txType=='A')
							$scope.txType='(FUND心投)';//show web
						 
						$scope.inputVO.certificateID = data.EviNum;                 //憑證編號 
						$scope.inputVO.bProdID = data.FundNO;		                     //變更前基金代號
						$scope.inputVO.bProdName = data.FundName;	                     //變更前基金名稱
						$scope.inputVO.bProdCurr = data.CurFund;	                     //變更前產品計價幣別   基金幣別
						$scope.inputVO.bStatus = data.Status;							 //變更前憑證狀態
						$scope.inputVO.bTrustCurr = data.CurCode;			             //變更前產品信託幣別
		 				$scope.inputVO.fTrustCurr = data.CurCode;                      //要指定變更後幣別 這樣才能扣款帳號幣別餘額
		 				if('TW'==data.CurCode){                        
		 					$scope.inputVO.bTrustCurrType = 'N';                             //轉出標的信託幣別類別
						}else{
							$scope.inputVO.bTrustCurrType = 'Y';
						}
		 				 
		 				$scope.inputVO.bTrustAmt = data.CurAmt;                         //變更前信託金額    投資金額
		 				if($scope.inputVO.tradeSubType!='1'){ 
							if($scope.inputVO.tradeSubType=='2' || $scope.inputVO.tradeSubType=='4'){
								$scope.inputVO.bPurchaseAmtL = data.TransferAmt;                //定期定額每月扣款金額
								$scope.inputVO.bEquivalentAmtL = $scope.inputVO.bPurchaseAmtL;        //定期定額每月扣款金額 
							}else if($scope.inputVO.tradeSubType=='3' || $scope.inputVO.tradeSubType=='5'){
								$scope.inputVO.bPurchaseAmtL = data.TransferAmt_L;              //BEFORE定期不定額 每月扣款金額L
								$scope.inputVO.bPurchaseAmtM = data.TransferAmt_M;              //BEFORE每月扣款金額M
								$scope.inputVO.bPurchaseAmtH = data.TransferAmt_H;              //BEFORE每月扣款金額H 
								$scope.inputVO.bEquivalentAmtL = data.TransferAmt_L;            //BEFORE每月扣款金額L (換匯或不換匯) 
								$scope.inputVO.bEquivalentAmtM = data.TransferAmt_M;            //BEFORE每月扣款金額M
								$scope.inputVO.bEquivalentAmtH = data.TransferAmt_H;            //BEFORE每月扣款金額H
							}
							
							$scope.inputVO.bChargeDate1 = data.TransferDate01;						  //變更前每月扣款日期1
							$scope.inputVO.bChargeDate2 = data.TransferDate02;						  //變更前每月扣款日期2
							$scope.inputVO.bChargeDate3 = data.TransferDate03;						  //變更前每月扣款日期3
							$scope.inputVO.bChargeDate4 = data.TransferDate04;						  //變更前每月扣款日期4
							$scope.inputVO.bChargeDate5 = data.TransferDate05;						  //變更前每月扣款日期5
							$scope.inputVO.bChargeDate6 = data.TransferDate06;						  //變更前每月扣款日期6
							//每月扣款日期1~6 (小額用)
		 				 
						
							$scope.inputVO.bChargeDateList = $scope.inputVO.bChargeDate1 + "、" +  $scope.inputVO.bChargeDate2 + "、" +  $scope.inputVO.bChargeDate3 + "、" +  $scope.inputVO.bChargeDate4 + "、" +  $scope.inputVO.bChargeDate5 + "、" +  $scope.inputVO.bChargeDate6;
							$scope.inputVO.bChargeDateList = $scope.inputVO.bChargeDateList.replace(/\、null/g,"");
							$scope.inputVO.bChargeDateList = $scope.inputVO.bChargeDateList.replace(/null/g,"");
		 				 
		 					$scope.inputVO.bDebitAcct = data.PayAccountNo;							   //變更前扣款帳號
		 					//不指定 ，因為幣別 $scope.inputVO.fDebitAcct = $scope.inputVO.bDebitAcct;                             //變更後扣款帳號  =變更前扣款帳號
		 				}
		 				$scope.inputVO.bCreditAcct = data.PayAcctId;								       //變更前收益入帳帳號
		 				//不指定 $scope.inputVO.fCreditAcct = $scope.inputVO.bCreditAcct;                               //變更後收益入帳帳號=變更前收益入帳帳號
		 				$scope.inputVO.bTrustAcct = data.AcctId02;								       //變更前信託帳號
		 				$scope.inputVO.trustAcct = $scope.inputVO.bTrustAcct;  //因帳號變更checkTrustAcct()需要trustAcct
		 				if($scope.inputVO.tradeSubType!='1'){
		 					$scope.inputVO.bCertificateStatus = data.Status;							   //變更前憑證狀態
		 				}
		 				
						$scope.getOutProdDTL($scope.inputVO.bProdID);//查轉出標的產品風險等級
						
						
						$scope.inputVO.debitAvbBalance = undefined;    //扣款帳號餘額
						$scope.avlCurrency = undefined;                //扣款帳號餘額幣別
//						$scope.checkTrustAcct();
						$scope.changeAcct();//1.換扣款帳號和2.換商品都要查詢 該帳號幣別餘額
					
						$scope.canDoChange();//控制可以做那些變更的檢核
					}
					
				}
				$scope.connector('set','SOTCustID',undefined);
				$scope.connector('set','SOTProd',undefined);
			}
			//等參數load完才能執行導頁程式
			$scope.getXML().then(function(){
				$scope.inbyCRM821();
			});

		/**
		 * 確認基金註記 => 此交易檢查是否停止申購
		 */
		$scope.checkFundStatus = () => $scope.sendRecv("SOT703", "qryFundMemo",
			"com.systex.jbranch.app.server.fps.sot703.SOT703InputVO", {prodId: $scope.inputVO.fProdID},
			(tota) => tota[0].body[0] && tota[0].body[0].NO_E_PURCHASE ? $scope.showErrorMsg(tota[0].body[0].NO_E_PURCHASE) : $scope.getProdDTL());
});