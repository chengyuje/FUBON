/* 
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT140Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q, validateService, sotService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT140Controller";
		//繼承PRD100高齡檢核
		$controller('PRD100Controller', {$scope: $scope});
		
		$scope.getXML = function(){
			var deferrd = $q.defer();
			getParameter.XML(["SOT.CUST_TYPE", "SOT.TRANSFER_TYPE", "SOT.TRADE_DATE_TYPE","SOT.CHANGE_TRADE_SUB_TYPE", "SOT.PROSPECTUS_TYPE","SOT.RESERVE_TRADE_DAYS", "SOT.FITNESS_YN", "SOT.RESERVE_DATE_TIMESTAMP", "SOT.TRUST_CURR_TYPE","SOT.SPEC_CUSTOMER","SOT.FUND_DECIMAL_POINT","SOT.ASSET_TRADE_SUB_TYPE"], function(totas) {
			if (totas) {
				//來行人員
				$scope.mappingSet['SOT.CUST_TYPE'] = totas.data[totas.key.indexOf('SOT.CUST_TYPE')];
				//轉出方式
		        $scope.mappingSet['SOT.TRANSFER_TYPE'] = totas.data[totas.key.indexOf('SOT.TRANSFER_TYPE')];
		        //交易日期
		        $scope.mappingSet['SOT.TRADE_DATE_TYPE'] = totas.data[totas.key.indexOf('SOT.TRADE_DATE_TYPE')];
		        //信託型態 
		        $scope.mappingSet['SOT.CHANGE_TRADE_SUB_TYPE'] = totas.data[totas.key.indexOf('SOT.CHANGE_TRADE_SUB_TYPE')];
		        //公開說明書選項
				$scope.mappingSet['SOT.PROSPECTUS_TYPE'] = totas.data[totas.key.indexOf('SOT.PROSPECTUS_TYPE')];
				
				// 基金幣別小數位
				$scope.mappingSet['SOT.FUND_DECIMAL_POINT'] = totas.data[totas.key.indexOf('SOT.FUND_DECIMAL_POINT')]; 
				
		        //Nday 預約 營業日參數
				$scope.mappingSet['SOT.RESERVE_TRADE_DAYS'] = totas.data[totas.key.indexOf('SOT.RESERVE_TRADE_DAYS')]; 
		    	angular.forEach($scope.mappingSet['SOT.RESERVE_TRADE_DAYS'], function(row) {
		    		if(row.DATA=='NF') { 
		    			$scope.reserveTradeDay = row.LABEL;
		    		}
				});
		    	//是否適配
		    	$scope.mappingSet['SOT.FITNESS_YN'] = totas.data[totas.key.indexOf('SOT.FITNESS_YN')];
		    	angular.forEach($scope.mappingSet['SOT.FITNESS_YN'], function(row) {
		    		if(row.DATA=='NF' && row.LABEL=='N') { 
		    			$scope.showErrorMsg("參數SOT.FITNESS_YN NF停止商品適配");
		    		}
				});
		    	//信託幣別
		        $scope.mappingSet['SOT.TRUST_CURR_TYPE'] = totas.data[totas.key.indexOf('SOT.TRUST_CURR_TYPE')];
		        $scope.mappingSet['SOT_AssetType'] = totas.data[totas.key.indexOf('SOT.ASSET_TRADE_SUB_TYPE')];
		        //預約時間限制參數
		    	$scope.mappingSet['SOT.RESERVE_DATE_TIMESTAMP'] = totas.data[totas.key.indexOf('SOT.RESERVE_DATE_TIMESTAMP')]; 
				$scope.mappingSet['SOT.SPEC_CUSTOMER'] = totas.data[totas.key.indexOf('SOT.SPEC_CUSTOMER')];//客戶註記
				deferrd.resolve();
			}
		});
			return deferrd.promise;
		}        
        
        $scope.getTradeDate = function(){
			$scope.sendRecv("SOT712", "getReserveTradeDate", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {prodType:'NF'},
					function(tota, isError) {
						if (!isError) {
							$scope.tradeDate =tota[0].body.TradeDate;
							if($scope.inputVO.tradeDateType == '2'){
								$scope.inputVO.tradeDate = $scope.tradeDate;
							}else if($scope.inputVO.tradeDateType == '1'
									|| $scope.inputVO.tradeDateType == ''
									|| $scope.inputVO.tradeDateType == undefined){
								$scope.inputVO.tradeDate = new Date();
							} 
							
							return;
						}
			});
		}

        $scope.getTradeDate();
        $scope.getReserveTradeDate = function() {  
        	if($scope.inputVO.tradeDateType == '2'){
				$scope.inputVO.tradeDate = $scope.tradeDate;
			 }else if($scope.inputVO.tradeDateType == '1'){
				$scope.inputVO.tradeDate = new Date();
			 }
		};
		
		//清除客戶資料
		$scope.custClear = function(cleanCustID) {
			$scope.mappingSet['feeDebitAcct']= undefined; 		//手續費扣款帳號
			if(cleanCustID){
			   $scope.inputVO.custID ='';						//客戶ID
			}
			$scope.inputVO.custName = '';						//客戶姓名
			$scope.inputVO.kycLV = '';							//KYC等級	
			$scope.inputVO.kycDueDate = undefined;				//KYC效期
			$scope.inputVO.custRemarks = '';					//客戶註記
			$scope.inputVO.outFlag = '';						//是否為OBU客戶
			$scope.inputVO.custTxFlag = '';						//同意投資商品諮詢服務
			$scope.inputVO.feeDebitAcct ='';					//手續費扣款帳號
			$scope.inputVO.proxyCustID = '';					//代理人ID
			$scope.inputVO.proxyCustName = '';					//代理人姓名		
			$scope.inputVO.piDueDate = undefined;				//專業投資人效期
			$scope.inputVO.is_backend = '';						//是否為後收型基金
			$scope.inputVO.hnwcYN = '';
			$scope.inputVO.hnwcServiceYN = '';
			
			$scope.inputVO = {
					tradeSEQ:$scope.inputVO.tradeSEQ,			//下單交易序號
					custID:$scope.inputVO.custID,
					custName: '', 								//客戶姓名
					kycLV: '', 									//KYC等級
					kycDueDate: undefined, 						//KYC效期
					profInvestorYN: '', 						//是否為專業投資人
					piDueDate: undefined, 						//專業投資人效期
					custRemarks: '', 							//客戶註記
					outFlag:'',									//OBU註記
					isOBU: '', 									//是否為OBU客戶
					feeDebitAcct:'',							//手續費扣款帳號
					narratorID: $scope.inputVO.narratorID,		//解說人員ID
					narratorName: $scope.inputVO.narratorName,	//解說人員姓名
					isAgreeProdAdv:'',							//同意投資商品諮詢服務 
					piRemark : '',                              //專業投資人註記
					//適配
				    noSale:'',									//商品禁銷註記
					fatcaType:'',								//判斷身分fatca: 不合作、美國人、未簽署
					ageUnder70Flag:'',							//年齡小於70
					isFirstTrade:'',                            //是否首購
					is_backend:''								//是否為後收型基金

			};
        	
//			$scope.inputVO.bargainDueDate =  undefined;			//期間議價效期
//			$scope.inputVO.w8benEffDate = undefined;			//W8ben有效日期
//			$scope.inputVO.custType = 'CUST';					//來行人員

			
//			$scope.isGetSOTCustInfo = undefined; //查詢客戶帳號  (執行前 undefined ,執行後 true)
			
        };
        
        $scope.prodClear = function() {
        	 //			console.log("prodClear:"); 
        	 $scope.inProdList=[{}];
        	 $scope.inputVO.carSEQ = undefined;
        	 $scope.inputVO.outProdID = '';             // 轉出標的
			 $scope.inputVO.outProdName = '';           // 轉出標的
			 $scope.inputVO.outProdCurr = undefined;    // 計價幣別
			 $scope.inputVO.outProdRiskLV = '';         // 產品風險等級
			 $scope.inputVO.outTrustCurr = '';          // 信託金額
			 $scope.inputVO.outTrustCurrType = '';      // 信託幣別
			 $scope.inputVO.outTrustAmt = undefined;    // 信託金額
			 $scope.inputVO.outTradeType = '';          // 信託型態 
			 $scope.inputVO.outTradeTypeD = '';         // 詳細信託型態
			 $scope.inputVO.outCertificateID = '';      // 憑證編號
			 $scope.inputVO.outNotVertify = '';         // 未核備欄位
			 
			 $scope.inputVO.outUnit = undefined;        // 原單位數
			 $scope.inputVO.transferType = '';          // 轉出方式
			 $scope.inputVO.prospectusType    ='';		//公開說明書
//			 $scope.inputVO.feeDebitAcct = '';          //手續費扣款帳號
//			 $scope.inputVO.tradeDateType = '';         //交易日期類別
//			 $scope.inputVO.tradeDate = undefined;      //交易日期
			 $scope.inputVO.is_backend = '';			//是否為後收型基金
			 $scope.inputVO.queryProdIDindex = undefined;
			 $scope.clearInProd();
		};
		
		$scope.clearInProd = function(){
			 $scope.inputVO.inProdID1 = '';             // 轉入標的ID 1
			 $scope.inputVO.inProdName1 = '';           // 轉入標的NAME 1
			 $scope.inputVO.inProdCurr1 = '';           // 轉入計價幣別 1
			 $scope.inputVO.inProdRiskLV1 = '';         // 轉入產品風險等級 1
			 $scope.inputVO.inUnit1 = undefined;        // 轉入單位數 1
			 $scope.inputVO.inPresentVal1 = undefined;  // 轉入參考現值 1
			 $scope.inputVO.inUnitPrice1 = undefined;   // 轉入單位現值 1
			 $scope.inputVO.inProdID2 = '';             // 轉入標的ID 2
			 $scope.inputVO.inProdName2 = '';           // 轉入標的NAME 2
			 $scope.inputVO.inProdCurr2 = '';           // 轉入計價幣別 2
			 $scope.inputVO.inProdRiskLV2 = '';         // 轉入產品風險等級 2
			 $scope.inputVO.inUnit2 = undefined;        // 轉入單位數 2
			 $scope.inputVO.inPresentVal2 = undefined;  // 轉入參考現值 2
			 $scope.inputVO.inUnitPrice2 = undefined;   // 轉入單位現值2
			 $scope.inputVO.inProdID3 = '';             // 轉入標的ID 3
			 $scope.inputVO.inProdName3 = '';           // 轉入標的NAME 3
			 $scope.inputVO.inProdCurr3 = '';           // 轉入計價幣別 3
			 $scope.inputVO.inProdRiskLV3 = '';         // 轉入產品風險等級 3
			 $scope.inputVO.inUnit3 = undefined;        // 轉入單位數 3
			 $scope.inputVO.inPresentVal3 = undefined;  // 轉入參考現值 3 
			 $scope.inputVO.inUnitPrice3 = undefined;   // 轉入單位現值 3
		}
		
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
		
		$scope.init = function() {	
			$scope.carList=[];
			$scope.custClear(true);
			$scope.prodClear(); // 清除 IN/OUT產品
			$scope.inputVO.narratorID = projInfoService.getUserID();  //解說專員員編
			$scope.inputVO.narratorName = projInfoService.getUserName();  //解說專員姓名
			$scope.getReserveTradeDate();  // 取N day getTradeDate 營業日
			$scope.inputVO.otherWithCustId = false; //是否帶客戶ID進來(快查)
//			$scope.inputVO.tradeSEQ=$scope.connector('get', "SOTTradeSEQ");
			$scope.inputVO.prodType='1';  //1：基金
        	$scope.inputVO.tradeType='3'; //3：轉換
        	$scope.inputVO.seniorAuthType='S'; //高齡評估表授權種類(S:下單、A：適配)
        	$scope.inputVO.trustTS = 'S';  //S:特金交易
        	
			$scope.fromHome();
		};
		
		$scope.fromHome = function() {
			var custID = $scope.connector('get','ORG110_custID');
			if(custID != undefined){
				$scope.inputVO.custID = custID;
			}
		};	
		$scope.init();
		
		
		//下一步 按鈕
		$scope.next = function() {
			if($scope.checkTradeDateType() == false) { //交易日期類別是否可選擇即時/預約
				return;
			}
			 
			$scope.sendRecv("SOT140", "next", "com.systex.jbranch.app.server.fps.sot140.SOT140InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.prodClear();
							$scope.connector('set','SOTTradeSEQ', $scope.inputVO.tradeSEQ);
							$scope.connector('set','SOTCarSEQ', null);
							if ($scope.fromFPS) {
								// from FPS_SOT.js
								$scope.setSOTurl('assets/txn/SOT141/SOT141.html');
							} else {
								$rootScope.menuItemInfo.url = "assets/txn/SOT141/SOT141.html";
							}							
							return;
						}
			});
		};
		
 
		
		//將購物車該筆相同seqNo編號 顯示在編輯區
		$scope.edit = function(row) {
			$scope.inProdList =[{}];
		  try{
			 //			console.log('edit(row)'+JSON.stringify(row)); 
		 
			 $scope.inputVO.carSEQ = row.SEQ_NO;                        //流水號  
			 $scope.inputVO.batchSeq = row.BATCH_SEQ;                  //下單批號
			 $scope.inputVO.batchNo = row.BATCH_NO;                    //下單批號流水號 
			 
			 $scope.inputVO.outProdID = row.OUT_PROD_ID;               // 轉出標的
			 $scope.inputVO.outProdName = row.OUT_PROD_NAME;           // 轉出標的
			 $scope.inputVO.outProdCurr = row.OUT_PROD_CURR;           // 計價幣別
			 $scope.inputVO.outProdRiskLV = row.OUT_PROD_RISK_LV;      // 產品風險等級
			 $scope.inputVO.outTrustCurr = row.OUT_TRUST_CURR;         // 信託金額
			 $scope.inputVO.outTrustCurrType = row.OUT_TRUST_CURR_TYPE;// 信託幣別
			 $scope.inputVO.outTrustAmt = row.OUT_TRUST_AMT;           // 信託金額
			 $scope.inputVO.outTradeType = row.OUT_TRADE_TYPE;         // 信託型態 
			 $scope.inputVO.outTradeTypeD = row.OUT_TRADE_TYPE_D;      // 詳細信託型態
			 $scope.inputVO.outCertificateID = row.OUT_CERTIFICATE_ID; // 憑證編號
			 $scope.inputVO.prospectusType = row.PROSPECTUS_TYPE; 	   //取得公開說明書方式
			 $scope.inputVO.outUnit = row.OUT_UNIT;                    // 原單位數
			 
			 $scope.inputVO.outTrustCurrType = row.OUT_TRUST_CURR_TYPE;//轉出標的信託幣別類別 
			 $scope.inputVO.outPresentVal = row.OUT_PRESENT_VAL;       //轉出標的參考現值
			 $scope.inputVO.outTrustAcct = row.OUT_TRUST_ACCT;	       //轉出標的信託帳號
			 
			 $scope.inputVO.transferType = row.TRANSFER_TYPE;          // 轉出方式
			 
			 $scope.inProdList[0].inProdID1 = row.IN_PROD_ID_1;              	// 轉入標的ID 1
			 $scope.inProdList[0].inProdName1   = row.IN_PROD_NAME_1;          	// 轉入標的NAME 1
			 $scope.inProdList[0].inProdCurr1   = row.IN_PROD_CURR_1;          	// 轉入計價幣別 1
			 $scope.inProdList[0].inProdRiskLV1 = row.IN_PROD_RISK_LV_1;    	// 轉入產品風險等級 1
			 $scope.inProdList[0].inUnit1 	  = row.IN_UNIT_1;                  // 轉入單位數 1
			 $scope.inProdList[0].inPresentVal1 = row.IN_PRESENT_VAL_1;     	// 轉入參考現值 1
			 if($scope.inputVO.transferType==2){
				 $scope.inProdList.push({
					 'inProdID1'     : row.IN_PROD_ID_2,             	// 轉入標的ID 2
					 'inProdName1'   : row.IN_PROD_NAME_2,          	// 轉入標的NAME 2
					 'inProdCurr1'   : row.IN_PROD_CURR_2,          	// 轉入計價幣別 2
					 'inProdRiskLV1' : row.IN_PROD_RISK_LV_2,     		// 轉入產品風險等級 2
					 'inUnit1' 	     : row.IN_UNIT_2,                   // 轉入單位數 2
					 'inPresentVal1' : row.IN_PRESENT_VAL_2      		// 轉入參考現值 2
				 });

				 $scope.inProdList.push({
					 'inProdID1'     : row.IN_PROD_ID_3,              	// 轉入標的ID 3
					 'inProdName1'   : row.IN_PROD_NAME_3,          	// 轉入標的NAME 3
					 'inProdCurr1'   : row.IN_PROD_CURR_3,          	// 轉入計價幣別 3
					 'inProdRiskLV1' : row.IN_PROD_RISK_LV_3,     		// 轉入產品風險等級 3
					 'inUnit1' 	     : row.IN_UNIT_3,                   // 轉入單位數 3
					 'inPresentVal1' : row.IN_PRESENT_VAL_3      		// 轉入參考現值 3 
				 });
			 }
			 			 
//			 $scope.inputVO.inProdName1 = row.IN_PROD_NAME_1;          // 轉入標的NAME 1
//			 $scope.inputVO.inProdCurr1 = row.IN_PROD_CURR_1;          // 轉入計價幣別 1
//			 $scope.inputVO.inProdRiskLV1 = row.IN_PROD_RISK_LV_1;     // 轉入產品風險等級 1
//			 $scope.inputVO.inUnit1 = row.IN_UNIT_1;                   // 轉入單位數 1
//			 $scope.inputVO.inPresentVal1 = row.IN_PRESENT_VAL_1;      // 轉入參考現值 1
//			 
//			 
//			 $scope.inputVO.inProdName2 = row.IN_PROD_NAME_2;          // 轉入標的NAME 2
//			 $scope.inputVO.inProdCurr2 = row.IN_PROD_CURR_2;          // 轉入計價幣別 2
//			 $scope.inputVO.inProdRiskLV2 = row.IN_PROD_RISK_LV_2;     // 轉入產品風險等級 2
//			 $scope.inputVO.inUnit2 = row.IN_UNIT_2;                   // 轉入單位數 2
//			 $scope.inputVO.inPresentVal2 = row.IN_PRESENT_VAL_2;      // 轉入參考現值 2
//			 
//			 
//			 $scope.inputVO.inProdName3 = row.IN_PROD_NAME_3;          // 轉入標的NAME 3
//			 $scope.inputVO.inProdCurr3 = row.IN_PROD_CURR_3;          // 轉入計價幣別 3
//			 $scope.inputVO.inProdRiskLV3 = row.IN_PROD_RISK_LV_3;     // 轉入產品風險等級 3
//			 $scope.inputVO.inUnit3 = row.IN_UNIT_3;                   // 轉入單位數 3
//			 $scope.inputVO.inPresentVal3 = row.IN_PRESENT_VAL_3;      // 轉入參考現值 3 
			 
			 $scope.inputVO.feeDebitAcct = row.FEE_DEBIT_ACCT;     		//手續費扣款帳號
			 $scope.inputVO.tradeDateType = row.TRADE_DATE_TYPE;     	//交易日期類別
			 $scope.inputVO.tradeDate = row.TRADE_DATE;     			//交易日期
			 
			 $scope.inputVO.narratorID = row.NARRATOR_ID;     			//解說專員員編
			 $scope.inputVO.narratorName = row.NARRATOR_NAME;     		//解說專員姓名 
		
			 //設定傳入單位現值(原轉入和轉出 不同幣別要換匯，所以改以轉出為主)
			 $scope.inputVO.inUnitPrice1 = $scope.inputVO.outPresentVal/$scope.inputVO.outUnit;
			 $scope.inputVO.inUnitPrice2 = $scope.inputVO.inUnitPrice1;
			 $scope.inputVO.inUnitPrice3 = $scope.inputVO.inUnitPrice1;
			 
		  }catch(e){ 
		    	//			console.log("edit err:"+e);
		  }

		};

		$scope.countList = function () {
			var tradeSize = 0;
			if ($scope.carList.length > 0) {
				tradeSize = 1;
				var tradePordTemp = $scope.carList[0].SEQ_NO;
				
				angular.forEach($scope.carList, function(row){
					if (row.SEQ_NO != tradePordTemp) {
						tradeSize++;
						tradePordTemp = row.SEQ_NO;
					}
				});
			}
			
			return tradeSize;
		}
		
		$scope.delCar = function(row) {
			var txtMsg = "";
			if ($scope.countList() == 1) {
				txtMsg = $filter('i18n')('ehl_02_common_005');
			} else {
				txtMsg = $filter('i18n')('ehl_02_common_006');
			}
			
			$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
				$scope.sendRecv("SOT140", "delProd", "com.systex.jbranch.app.server.fps.sot140.SOT140InputVO", {tradeSEQ : $scope.inputVO.tradeSEQ, carSEQ: row.SEQ_NO},
						function(tota, isError) {
							if (!isError) {
								$scope.prodClear();
								$scope.refresh();
								
								return;
							}
				});
            });
		};      
		
		// if data
		$scope.inputVO.tradeSEQ = $scope.connector('get', 'SOTTradeSEQ'); 
		$scope.inputVO.carSEQ = $scope.connector('get','SOTCarSEQ');
		$scope.connector('set', 'SOTTradeSEQ', null);
		$scope.connector('set', 'SOTCarSEQ', null);

		$scope.validateSeniorCust = function() {
			if(!$scope.inputVO.custID) return;
			
			$scope.inputVO.prodType='1';  //1：基金
        	$scope.inputVO.tradeType='3'; //3：轉換
        	$scope.inputVO.seniorAuthType='S'; //高齡評估表授權種類(S:下單、A：適配)
        	$scope.inputVO.trustTS = 'S';  //S:特金交易
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
			if($scope.inputVO.otherWithCustId) { //有帶客戶ID(快查)
				$scope.inbyCRM821();
			} else {
				$scope.getSOTCustInfo(false,true);
			}
			$scope.connector('set','SOTCustID',null);
		};
		
		// SOT701-客戶電文
		$scope.getSOTCustInfo = function(loadEdit,input) {
			//			console.log("SOTCustInfo"+loadEdit);
			$scope.inputVO.custID = $filter('uppercase')($scope.inputVO.custID);
			var validCustID = validateService.checkCustID($scope.inputVO.custID); //自然人和法人檢查
			//			console.log("custID:" + $scope.inputVO.custID + ", checkCustID: "+validCustID);
			if(validCustID==false){ 
				$scope.inputVO.custID='';
			}
			if(validCustID==false || input) {
				 
				$scope.custClear(false);
				$scope.prodClear();
				
				//若CUST_ID有變，交易序號應更新且購物車應清空，否則資料會錯亂
				if($scope.carList && $scope.carList.length > 0) {
					$scope.clearTradeSEQ();
				}
			}
			if(validCustID) {			
				$scope.sendRecv("SOT140", "getSOTCustInfo", "com.systex.jbranch.app.server.fps.sot140.SOT140InputVO", {'custID':$scope.inputVO.custID, 'prodType':1, 'tradeType':3},
						function(tota, isError) {
							if (!isError) {
									$scope.toDay = $filter('date')(new Date(),'yyyy-MM-dd 00:00:00');//取當日日期
									//FOR CBS TEST日期
//									if ($scope.toJsDate(tota[0].body.kycDueDate) < $scope.toJsDate($scope.toDay)) { //若KYC過期，需有訊息提示客戶，並清空客戶ID。
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
										 $scope.custClear(true);
										 return;
									}
//								    $scope.isGetSOTCustInfo = undefined;s
//								    //			console.log("SOTCustInfo:"+JSON.stringify(tota[0].body));
									$scope.inputVO.custName = tota[0].body.custName;
									$scope.inputVO.kycLV = tota[0].body.kycLevel;						//KYC等級
									$scope.inputVO.kycDueDate = $scope.toJsDate(tota[0].body.kycDueDate);	//KYC效期
									$scope.inputVO.custRemarks = tota[0].body.custRemarks;					//客戶註記
									$scope.inputVO.isOBU = tota[0].body.isOBU;								//是否為OBU客戶
									$scope.inputVO.isAgreeProdAdv = tota[0].body.isAgreeProdAdv;			//同意投資商品諮詢服務
									$scope.inputVO.piRemark = tota[0].body.piRemark;			            //專業投資人註記
									$scope.inputVO.piDueDate = $scope.toJsDate(tota[0].body.piDueDate);		//專業投資人效期
									$scope.inputVO.profInvestorYN = tota[0].body.profInvestorYN;
									$scope.inputVO.custTxFlag     = tota[0].body.custTxFlag;
									console.log(tota[0].body);
									$scope.inputVO.custProType = tota[0].body.custProType;				
									// add by ocean 非常規交易
									$scope.inputVO.isFirstTrade = tota[0].body.isFirstTrade;                //是否首購   
									$scope.inputVO.ageUnder70Flag = tota[0].body.ageUnder70Flag;
									$scope.inputVO.eduJrFlag = tota[0].body.eduJrFlag;
									$scope.inputVO.healthFlag = tota[0].body.healthFlag; 
									$scope.inputVO.hnwcYN = tota[0].body.hnwcYN;
									$scope.inputVO.hnwcServiceYN = tota[0].body.hnwcServiceYN;
									
									var car_feeDebitAcct = $scope.inputVO.feeDebitAcct;     //扣款帳號
									
									$scope.mappingSet['feeDebitAcct'] = tota[0].body.debitAcct; //扣款帳號LIST
									
									if (!loadEdit) {//沒有設loadEdit  從編輯按鈕(風控)過來不寫預設  因為信託帳號跟手續費有關 !$scope.inputVO.seqNo
										$scope.inputVO.feeDebitAcct = (tota[0].body.debitAcct.length > 0 ? tota[0].body.debitAcct[0].LABEL : "");
									} else if(loadEdit==true) {  
										$scope.inputVO.feeDebitAcct = car_feeDebitAcct;     //扣款帳號  
									} 
									
									if(loadEdit!=true) {
										$scope.prodClear();//清除產品
									}
										
									return;
							} else {
								$scope.custClear(true);
							}
				});
			}
		};
		
		//解說專員
		$scope.getTellerName = function() {
			$scope.sendRecv("SOT712", "getTellerName", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {'tellerID': $scope.inputVO.narratorID},
					function(tota, isError) {
						if (!isError) {
							$scope.inputVO.narratorName = tota[0].body.EMP_NAME;
							return;
						}
			});
		};
		
		
		//交易日期
		/*
		$scope.checkTradeDateType = function(){
		     var dat = (+new Date()) ;
		     if(( parseInt(dat) % (86400000) + 28800000 ) > 55800000 ){
		    	 $scope.inputVO.tradeDateType='2';     //PM3:30後為預約
		     }
		     if(( parseInt(dat) % (86400000) + 28800000 ) < 55200000 ){
		    	 $scope.inputVO.tradeDateType='1';     //AM00:00~PM3:20為即時
		     }
		      
		     if((parseInt(dat) % (86400000) + 28800000 ) >= 55200000 && (parseInt(dat) % (3600000 * 24) + 28800000) <= 55800000) {
		    	 $scope.inputVO.tradeDateType='3';		//PM3:20~PM3:30不能執行
		     }
		      
		}
		*/
		
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
		
		$scope.checkTradeDateType();
		
		 
		
		//偵測每秒當前時間
//		$scope.showTime=function(){
//			$scope.checkTradeDateType();
//			setTimeout($scope.showTime(),1000);
//		}
//		$scope.showTime();
		
		//查詢基金庫存基本資料
		$scope.getOutProdDTL = function (outProdID) { 
			if($scope.inputVO.outProdID=='' ) {  
				return;
			}
			$scope.inProdList=[{}];
//			$scope.inputVO.inProdID1 = '';
//			$scope.inputVO.inProdID2 = '';
//			$scope.inputVO.inProdID3 = '';

			var prodDTLInputVo = {'queryProdID':outProdID, 'queryOutProd':'Y', 'custID':$scope.inputVO.custID,'tradeDateType':$scope.inputVO.tradeDateType ,'tradeDate': $scope.inputVO.tradeDate};
					$scope.sendRecv("SOT140", "getProdDTL", "com.systex.jbranch.app.server.fps.sot140.SOT140InputVO", prodDTLInputVo ,
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.prodDTL != null && tota[0].body.prodDTL.length > 0) { 		//計價幣別
									$scope.inputVO.outProdRiskLV = tota[0].body.prodDTL[0].RISKCATE_ID;	    //單位現值
									$scope.inputVO.outNotVertify = tota[0].body.prodDTL[0].FUS40;           //商品未核備欄位FUS40
									$scope.inputVO.outOvsPrivateYN = tota[0].body.prodDTL[0].OVS_PRIVATE_YN; //境外私募基金
									
									var FUS20 = tota[0].body.prodDTL[0].FUS20;   
									if(FUS20 && FUS20=='C'){
										$scope.inputVO.outTrustCurrType = "C";
									}else{
										if($scope.inputVO.outTrustCurr =='TWD'){
											$scope.inputVO.outTrustCurrType = "N";
										}else{
											$scope.inputVO.outTrustCurrType = "Y";
										}
									}
									return;
								}
							}  
							
							$scope.showErrorMsg("ehl_01_common_009");
				});
		};

		//基金代碼
		$scope.getProdDTL1 = function (index,queryOutProd) {
			var inProd = $scope.inProdList[index];
			inProd.inProdID1_show = '';           // 轉入標的顯示ID
			inProd.inProdName1 = '';           // 轉入標的NAME 
			inProd.inProdCurr1 = '';           // 轉入計價幣別 
			inProd.inProdRiskLV1 = '';         // 轉入產品風險等級 
			$("#tradeDateType1").attr("disabled", false);
			if($scope.inputVO.transferType != '1'){
				inProd.inUnit1 = undefined;        // 轉入單位數 
				inProd.inPresentVal1 = undefined;  // 轉入參考現值 
			}
			
			//已從庫存計算inProd.inUnitPrice1 = undefined;   // 轉入單位現值 
			//$scope.checkFitness(1);
			if(!inProd.inProdID1){
				return;
			}
			
			inProd.inProdID1 = $filter('uppercase')(inProd.inProdID1);
			
			$scope.sendRecv("SOT140", "getProdDTL", "com.systex.jbranch.app.server.fps.sot140.SOT140InputVO", {"outProdID":$scope.inputVO.outProdID,"queryProdID":inProd.inProdID1,'queryOutProd':queryOutProd, 'custID':$scope.inputVO.custID,'tradeDateType':$scope.inputVO.tradeDateType ,'tradeDate': $scope.inputVO.tradeDate} ,
				function(tota, isError) {
					if (!isError) {
						if (tota[0].body.errorMsg != null && tota[0].body.errorMsg != "") {
							//適配有錯誤訊息
							
							$scope.showErrorMsg(tota[0].body.errorMsg);
							inProd.inProdID1='';
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
		//									$scope.custInfo(tota[0].body);
		//									deferred.resolve("success"); 
		//									return deferred.promise;
									} else {
										deferred.resolve("");
									}
								});
							}		
							inProd.inProdID1_show = inProd.inProdID1 ;
							inProd.inProdName1 = tota[0].body.prodDTL[0].FUND_CNAME;			//基金名稱
							inProd.inProdCurr1 = tota[0].body.prodDTL[0].CURRENCY_STD_ID;		//計價幣別
							inProd.inProdRiskLV1 = tota[0].body.prodDTL[0].RISKCATE_ID;			//風險等級
//							if($scope.inProdList.length < 3 && $scope.inputVO.transferType=='2'){
//								$scope.inProdList.push({});
//							}
							//	$scope.inputVO.inUnitPrice1 = tota[0].body.prodDTL[0].PRICE;                //單位現值
							
							//控管國內貨幣型基金交易時間，超過10:30 轉為預約交易狀態	
							$scope.sendRecv("SOT110", "checkReserve", "com.systex.jbranch.app.server.fps.sot110.SOT110InputVO", {"prodId" : inProd.inProdID1},
									function(tota, isError) {
										if (!isError) {
											$scope.rev = tota[0].body.reserve;
											if(!$scope.rev){  
													$("#tradeDateType1").attr("disabled", true);
													$scope.inputVO.tradeDateType = '2';
													$scope.inputVO.tradeDate = $scope.tradeDate;
													$scope.showMsg("此筆交易已超過國內貨幣型基金交易時間，將轉為預約交易");
											}
										}
								});
							
							return;
						} else {
							$scope.showErrorMsg("ehl_01_common_009");
							inProd.inProdID1='';
						}
					}else{
						inProd.inProdID1='';
					}
						
			});
		};
				
		//查詢購物車
		$scope.noCallCustQuery = function () {
			//			console.log('noCallCustQuery');
			var deferred = $q.defer();
			$scope.sendRecv("SOT140", "query", "com.systex.jbranch.app.server.fps.sot140.SOT140InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.carList = tota[0].body.carList;
							$scope.outputVO = tota[0].body;
							
							if (tota[0].body.carList[0]) { //avoid carList is empty
								$scope.inputVO.narratorID = tota[0].body.carList[0].NARRATOR_ID;		//解說人員ID
								$scope.inputVO.narratorName = tota[0].body.carList[0].NARRATOR_NAME;	//解說人員姓名
							}
							$scope.narratorDisabled = true;
							if (tota[0].body.mainList[0] && tota[0].body.mainList[0].CUST_ID) { //avoid mainList is empty when del all
								$scope.inputVO.custID = tota[0].body.mainList[0].CUST_ID;				//客戶ID
							}
							//風控頁按編輯回到下單頁 ,從購物車中取要編輯商品
							if ($scope.inputVO.carSEQ) {
								angular.forEach($scope.carList, function(row){
									if ($scope.inputVO.carSEQ == row.SEQ_NO) {
										$scope.edit(row);//將該筆相同seqNo,顯示在編輯區
									}
				    			});
							}
							
							deferred.resolve("success");
						}
			});
			
			return deferred.promise;
		};
		
		$scope.query = function() {
			$scope.carList = [];
			
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
	
		//setINProd return inProd validate if true showErrorMsg
		$scope.setInProd = function(){
			var validFail=true;
			$scope.clearInProd();
			for(var index=0;index<$scope.inProdList.length;index++){
				if($scope.inProdList[index].inProdID1){
					var inProd = $scope.inProdList[index];
					inProd.inUnit1=$scope.moneyUnFormat(inProd.inUnit1);
					inProd.inPresentVal1=$scope.moneyUnFormat(inProd.inPresentVal1);
					if(!$scope.inputVO.inProdID1){
						 validFail=false;
						 $scope.inputVO.inProdID1 = inProd.inProdID1;              // 轉入標的ID 1
						 $scope.inputVO.inProdName1 = inProd.inProdName1;          // 轉入標的NAME 1
						 $scope.inputVO.inProdCurr1 = inProd.inProdCurr1;          // 轉入計價幣別 1
						 $scope.inputVO.inProdRiskLV1 = inProd.inProdRiskLV1;     // 轉入產品風險等級 1
						 $scope.inputVO.inUnit1 = inProd.inUnit1;                   // 轉入單位數 1
						 $scope.inputVO.inPresentVal1 = inProd.inPresentVal1;      // 轉入參考現值 1
						 if(!$scope.inputVO.inUnit1 || !$scope.inputVO.inPresentVal1){
							 validFail=true;
						 }
					}else if(!$scope.inputVO.inProdID2){
						 $scope.inputVO.inProdID2 = inProd.inProdID1;              // 轉入標的ID 2
						 $scope.inputVO.inProdName2 = inProd.inProdName1;          // 轉入標的NAME 2
						 $scope.inputVO.inProdCurr2 = inProd.inProdCurr1;          // 轉入計價幣別 2
						 $scope.inputVO.inProdRiskLV2 = inProd.inProdRiskLV1;     // 轉入產品風險等級 2
						 $scope.inputVO.inUnit2 = inProd.inUnit1;                   // 轉入單位數 2
						 $scope.inputVO.inPresentVal2 = inProd.inPresentVal1;      // 轉入參考現值 2
						 if(!$scope.inputVO.inUnit2 || !$scope.inputVO.inPresentVal2){
							 validFail=true;
						 }
					}else if(!$scope.inputVO.inProdID3){
						 $scope.inputVO.inProdID3 = inProd.inProdID1;              // 轉入標的ID 3
						 $scope.inputVO.inProdName3 = inProd.inProdName1;          // 轉入標的NAME 3
						 $scope.inputVO.inProdCurr3 = inProd.inProdCurr1;          // 轉入計價幣別 3
						 $scope.inputVO.inProdRiskLV3 = inProd.inProdRiskLV1;     // 轉入產品風險等級 3
						 $scope.inputVO.inUnit3 = inProd.inUnit1;                   // 轉入單位數 3
						 $scope.inputVO.inPresentVal3 = inProd.inPresentVal1;      // 轉入參考現值 3 
						 if(!$scope.inputVO.inUnit3 || !$scope.inputVO.inPresentVal3){
							 validFail=true;
						 }
					}
				}
			}
			return validFail;
		}
		
		//加入購物車
		$scope.addCar = function(){
			var inProdID=$scope.setInProd();
			
			if($scope.parameterTypeEditForm.$invalid || inProdID) {
	    		$scope.showErrorMsg("ehl_01_common_022");
	    		return;
	    	}
			if ($scope.carList && $scope.carList.length == 6) {
				$scope.showErrorMsg('ehl_01_SOT_007');
				return;
			}
			if($scope.checkTradeDateType() == false) { //交易日期類別是否可選擇即時/預約
				return;
			}

			$scope.inputVO.prodType=1;
			$scope.inputVO.tradeType=3;
			$scope.sendRecv("SOT140", "save", "com.systex.jbranch.app.server.fps.sot140.SOT140InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.errorMsg != null) {
								$scope.showErrorMsg(tota[0].body.errorMsg);
								$scope.refresh();
								$scope.prodClear();
								return;
							} else {
								if(tota[0].body.SHORT_1!=''){
									if(tota[0].body.SHORT_1==1){
										$scope.showErrorMsg('ehl_01_SOT_015');
									}
									if(tota[0].body.SHORT_1==2){
										$scope.showErrorMsg('ehl_01_SOT_016');
									}
								}
								$scope.prodClear();
								$scope.refresh();
								$scope.prodClear();
								return;
							}
						}
					}); 
		};
		
		
		$scope.refresh = function (seqNO) {
			if (seqNO) {
				$scope.inputVO.carSEQ = seqNO;
			}
			
			$scope.noCallCustQuery();
		};

		//選擇庫存
		$scope.goSOT132 = function(){
			/*
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT132/SOT132.html',
			    className: 'SOT132',
			    controller:['$scope',function($scope){
			    	$scope.txnName = "SOT140";
			    	$scope.custID=$scope.inputVO.custID;
			    }]
			 });
			*/
			var custId = $scope.inputVO.custID;//SOT132需要$scope.custID
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT132/SOT132.html',
			    className: 'SOT132',
			    controller:['$scope',function($scope){
			    	$scope.custID=custId;
					$scope.txnName = "sot140";
				}],
				showClose: false,
				scope : $scope
			 });
			dialog.closePromise.then(function(data){
				if(data.value && data.value != "cancel"){
					$scope.prodClear();//清除轉入轉出
					
					//動態鎖利不可轉換
					if (data.value.Dynamic && (data.value.Dynamic == '1' || data.value.Dynamic == '2')) {
						$scope.showErrorMsg("請至動態鎖利專區進行交易");
						return;
					}
					
					//console.log("goSOT132 data:" + JSON.stringify(data));
					$scope.inputVO.outProdID = data.value.FundNO;		 //基金代號
					
					//查詢是否為"後收型基金" ===> 基金轉換：僅提供後收型基金"全部轉換"同系列"後收型基金"功能
					$scope.sendRecv("SOT140", "isBackend", "com.systex.jbranch.app.server.fps.sot140.SOT140InputVO", {'outProdID': data.value.FundNO},
							function(tota, isError) {
								if (!isError) {
									$scope.inputVO.is_backend = tota[0].body.prodDTL[0].IS_BACKEND;
									
									if($scope.inputVO.is_backend == 'Y'){
										//若為"後收型基金" ===> 只能"全部轉換"
										getParameter.XML(["SOT.TRANSFER_TYPE"], function(totas) {
											if (totas) {
												$scope.mappingSet['SOT.TRANSFER_TYPE'] = [];
												var transfer_type = totas.data[totas.key.indexOf('SOT.TRANSFER_TYPE')];
												angular.forEach(transfer_type, function(row) {
													if(row.DATA == '1'){	//全部轉換
														$scope.mappingSet['SOT.TRANSFER_TYPE'].push({LABEL:row.LABEL, DATA:row.DATA});														
													}
												});
											}
										});
										
									}else{
										getParameter.XML(["SOT.TRANSFER_TYPE"], function(totas) {
											if (totas) {
												$scope.mappingSet['SOT.TRANSFER_TYPE'] = totas.data[totas.key.indexOf('SOT.TRANSFER_TYPE')];												
											}
										});
									}
									
								}
							}
					);
					
					$scope.inputVO.outProdName = data.value.FundName;	 //基金名稱
					$scope.inputVO.outProdCurr = data.value.CurFund;	 //計價幣別   CurFund基金幣別
					
 					$scope.inputVO.outTrustAmt = data.value.CurAmt;        // 轉出標的信託金額-  CurAmt投資金額
 					$scope.inputVO.outTradeType = data.value.AssetType;    //轉出標的信託型態-  AssetType庫存類別
					$scope.inputVO.outTradeTypeD= data.value.assetTradeSubTypeD; //詳細交易型態
 					$scope.inputVO.outUnit = data.value.CurUntNum;       //單位數   CurUntNum單位數
					$scope.inputVO.outCertificateID = data.value.EviNum; //憑證編號 
//					if('TW'==data.value.CurCode){                        //轉出標的信託幣別    CurCode交易幣別
//						$scope.inputVO.outTrustCurrType = 'N';
//					}else{
//						$scope.inputVO.outTrustCurrType = 'Y';
//					}
					$scope.inputVO.outTrustCurr = data.value.CurCode;     // CurCode交易幣別
					$scope.inputVO.outPresentVal = data.value.CurBal;     //轉出標的參考現值
					$scope.inputVO.outTrustAcct = data.value.AcctId02;	  //轉出標的信託帳號
					$scope.getOutProdDTL($scope.inputVO.outProdID);//查轉出標的產品風險等級
					
					$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.inputVO.outTrustCurr});					
	 				$scope.filterNumber = $scope.mod[0].LABEL;
					
					//設定傳入單位現值(原轉入和轉出 不同幣別要換匯，所以改以轉出為主)
					$scope.inputVO.inUnitPrice1 = $scope.inputVO.outPresentVal/$scope.inputVO.outUnit;
					$scope.inputVO.inUnitPrice2 = $scope.inputVO.inUnitPrice1;
					$scope.inputVO.inUnitPrice3 = $scope.inputVO.inUnitPrice1;
					 
					if(!$scope.inputVO.outPresentVal){
						$scope.showErrorMsg("庫存 轉出標的參考現值:"+$scope.inputVO.outPresentVal);
					}
					
					if(!$scope.inputVO.outUnit){
						$scope.showErrorMsg("庫存 單位數:"+$scope.inputVO.outUnit);
					}
					
			    } 
				
				
				//#0695 排除數存戶
				if(sotService.is168($scope.inputVO.outTrustAcct) &&
						sotService.isDigitAcct($scope.inputVO.outTrustAcct,$scope.mappingSet['feeDebitAcct'])){
					$scope.showErrorMsg("ehl_02_SOT_995");
					$scope.prodClear();
				} 
			});
		}
		
		//轉出方式
		$scope.changeTransferType = function(){
			if($scope.inputVO.transferType=='1'){ //全部轉出 轉入要同庫存單位
				$scope.inProdList=[{}];
				$scope.inProdList[0].inUnit1=$scope.inputVO.outUnit;
				$scope.queryUnitAndPresentVal(true,0);
			}else{
				$scope.inProdList=[{},{},{}];
			}
		}
		
		//搜尋同系列基金
//		$scope.goPRD110=function()
//		{
//			var dialog = ngDialog.open({
//				template: 'assets/txn/PRD110/PRD110.html',
//				className: 'PRD140',
//				showClose: false,
//				scope : $scope,
//				controller: ['$scope', function($scope) {
//					$scope.isPop=true;
//	        		$scope.routeURL = 'assets/txn/PRD110/PRD110.html';
//	        		$scope.txnName = "搜尋同系列基金";
//	            }]
//			}).closePromise.then(function (data) {
//				if(data.value){
//					var dataValue = data.value;
//					$scope.inputVO.inProdID1 = dataValue.fund_id;				//基金代號
//					$scope.inputVO.inProdName1 = dataValue.fund_name;			//基金名稱
//					$scope.inputVO.inProdCurr1 = dataValue.currency;			//計價幣別
//					$scope.inputVO.inProdRiskLV1 = dataValue.risk_level;		//產品風險等級
//				}
//			});
//		};		
		
	
		//搜尋同系列基金 ,規則富邦尚未提供//TODO FIXME
		$scope.goPRD110series = function () {
			if($scope.inputVO.outOvsPrivateYN && $scope.inputVO.outOvsPrivateYN =="Y") {
				$scope.showErrorMsg("境外私募基金不可做轉換交易");
				$scope.prodClear();
				return;
			}
			
			if(!$scope.inputVO.outProdID){
				$scope.showErrorMsg("請先選擇轉出標的!");
				return;
			}
			var isBackend = $scope.inputVO.is_backend;
			var custID = $scope.inputVO.custID;
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT140/SOT140_ROUTE.html',
				className: 'PRD110',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					$scope.isPop = true;
	        		$scope.routeURL = 'assets/txn/PRD110/PRD110.html';
	        		$scope.tradeType = "3";
	        		$scope.cust_id = custID;
	        		$scope.fromSOTProdYN = "Y"; //下單商品搜尋//下單只需要輸入客戶ID時檢核，商品搜尋時不需再次檢核
	        		$scope.sameSerialYN = 'Y';
	        		$scope.sameSerialProdId = $scope.inputVO.outProdID;
	        		$scope.isBackend = isBackend;
	            }]
			}).closePromise.then(function (data) {
				if(data.value != "cancel") {
					//原邏輯
//					$scope.inputVO.inProdID1 = data.value.PRD_ID;//TODO
//					$scope.getProdDTL1(data.value.PRD_ID,'Y');
					if($scope.inputVO.transferType==1){
						$scope.inProdList[0].inProdID1=data.value.PRD_ID;
						$scope.getProdDTL1(0,'Y');
					}else{
						for(var i=0;i<$scope.inProdList.length;i++){
							if(!$scope.inProdList[i].inProdID1){
								$scope.inProdList[i].inProdID1=data.value.PRD_ID;
								$scope.getProdDTL1(i,'Y');
								break;
							}
						}
					}
					$scope.compareCDSCMonth();
					
					
				}
			});
		};
		
		//計算傳入單位或參考現值
		$scope.queryUnitAndPresentVal = function (isQVal, index) { 
			var inUnitPrice =  $scope.inputVO.outPresentVal/$scope.inputVO.outUnit;
			$scope.inProdList[index].inUnit1 = $scope.moneyUnFormat($scope.inProdList[index].inUnit1);
			$scope.inProdList[index].inPresentVal1 = $scope.moneyUnFormat($scope.inProdList[index].inPresentVal1);
				if(isQVal){
					$scope.inProdList[index].inPresentVal1 = $filter('number')($scope.inProdList[index].inUnit1 * inUnitPrice , $scope.filterNumber.toString());
					$scope.inProdList[index].inUnit1=$filter('number')($scope.inProdList[index].inUnit1, '4');
				}
				else{
					$scope.inProdList[index].inUnit1 = $filter('number')($scope.inProdList[index].inPresentVal1 / inUnitPrice , '4');
					$scope.inProdList[index].inPresentVal1=$filter('number')($scope.inProdList[index].inPresentVal1, $scope.filterNumber.toString());
				}
			
			};
		
		 
			$scope.inbyCRM821 = function(){
				if($scope.connector('get','SOTCustID')){
					$scope.inputVO.custID = $scope.connector('get','SOTCustID');
					$scope.getSOTCustInfo(true,false);
					var data = $scope.connector('get','SOTProd');
					
					if(data){
						$scope.prodClear();//清除轉入轉出
						$scope.inputVO.outProdID = data.FundNO;		 //基金代號
						$scope.inputVO.outProdName = data.FundName;	 //基金名稱
						$scope.inputVO.outProdCurr = data.CurFund;	 //計價幣別   CurFund基金幣別
						
	 					$scope.inputVO.outTrustAmt = data.CurAmt;        // 轉出標的信託金額-  CurAmt投資金額
	 					$scope.inputVO.outTradeType = data.AssetType;    //轉出標的信託型態-  AssetType庫存類別
	 					$scope.inputVO.outTradeTypeD = data.assetTradeSubTypeD;    //轉出標的詳細信託型態-  AssetType庫存類別
	 					$scope.inputVO.outUnit = data.CurUntNum;       //單位數   CurUntNum單位數
						$scope.inputVO.outCertificateID = data.EviNum; //憑證編號 

						$scope.inputVO.outTrustCurr = data.CurCode;     // CurCode交易幣別
						$scope.inputVO.outPresentVal = data.CurBal;     //轉出標的參考現值
						$scope.inputVO.outTrustAcct = data.AcctId02;	  //轉出標的信託帳號
						$scope.getOutProdDTL($scope.inputVO.outProdID);//查轉出標的產品風險等級
						
						$scope.mod = $filter('filter')($scope.mappingSet['SOT.FUND_DECIMAL_POINT'], {DATA: $scope.inputVO.outTrustCurr});					
		 				$scope.filterNumber = $scope.mod[0].LABEL;
		 				
						//設定傳入單位現值(原轉入和轉出 不同幣別要換匯，所以改以轉出為主)
						$scope.inputVO.inUnitPrice1 = $scope.inputVO.outPresentVal/$scope.inputVO.outUnit;
						$scope.inputVO.inUnitPrice2 = $scope.inputVO.inUnitPrice1;
						$scope.inputVO.inUnitPrice3 = $scope.inputVO.inUnitPrice1;
						 
						if(!$scope.inputVO.outPresentVal){
							$scope.showErrorMsg("庫存 轉出標的參考現值:"+$scope.inputVO.outPresentVal);
						}
						
						if(!$scope.inputVO.outUnit){
							$scope.showErrorMsg("庫存 單位數:"+$scope.inputVO.outUnit);
						}
					}
					
				}
				$scope.connector('set','SOTCustID',undefined);
				$scope.connector('set','SOTProd',undefined);
			}
			
			//FPS導頁進入
			$scope.inbyFPS = function(){
				if ($scope.fromFPS){
					console.log($scope.FPSData);
					$scope.inputVO.custID = $scope.FPSData.custID;
					$scope.getSOTCustInfo();
				}		
			}
			$scope.inbyFPS();
			
			$scope.getXML().then(function(){
				if($scope.connector('get','SOTCustID')) {
					//從快查或別的交易過來，帶CUSTID，先做高齡檢核
					$scope.inputVO.otherWithCustId = true;
					$scope.inputVO.custID = $scope.connector('get','SOTCustID');
					$scope.validateSeniorCust();
				}
				
			});

		/**
		 * 確認基金註記 => 此交易檢查是否停止轉入
		 */
		$scope.checkFundStatus = (index, queryOutProd) => $scope.sendRecv("SOT703", "qryFundMemo",
			"com.systex.jbranch.app.server.fps.sot703.SOT703InputVO", {prodId: $scope.inProdList[index].inProdID1},
			(tota) => tota[0].body[0] && tota[0].body[0].NO_E_IN ? $scope.showErrorMsg(tota[0].body[0].NO_E_IN) : $scope.getProdDTL1(index, queryOutProd));
		
		/**
		 * 比較後收型基金 轉入以及轉出標的的CDSC年期是否相符
		 */
		$scope.compareCDSCMonth = function(){			
			debugger;
			if($scope.inputVO.is_backend == 'Y'){
				$scope.inputVO.inProdID1 = $scope.inProdList[0].inProdID1;
				$scope.sendRecv("SOT140", "compareCDSCMonth", "com.systex.jbranch.app.server.fps.sot140.SOT140InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if(!tota[0].body.sameCDSCMonth){
									$scope.prodClear();
									$scope.showErrorMsg("後收型基金年期控管，基金CDSC年期不同，不可互轉。");
								}						
							}
						});
			}
		}

});