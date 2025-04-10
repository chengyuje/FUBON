/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT1641Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, $interval, getParameter, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT1641Controller";
		
		getParameter.XML(["SOT.CUST_TYPE", "SOT.CHANGE_TRADE_SUB_TYPE", "SOT.TRADE_DATE_TYPE", "COMMON.YES_NO", "SOT.TRUST_CURR_TYPE", "SOT.SPEC_CUSTOMER", "SOT.TRANSFER_TYPE_DYNA", "SOT.PROSPECTUS_TYPE"], function(totas) {
			if (totas) {
				// mapping 來行人員  參數設定SOT.CUST_TYPE
				$scope.mappingSet['SOT.CUST_TYPE'] = totas.data[totas.key.indexOf('SOT.CUST_TYPE')];
				//信託型態 
		        $scope.mappingSet['SOT.CHANGE_TRADE_SUB_TYPE'] = totas.data[totas.key.indexOf('SOT.CHANGE_TRADE_SUB_TYPE')];
		        //交易日期
		        $scope.mappingSet['SOT.TRADE_DATE_TYPE'] = totas.data[totas.key.indexOf('SOT.TRADE_DATE_TYPE')];
		        //YES;NO
		        $scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
				//信託幣別
		        $scope.mappingSet['SOT.TRUST_CURR_TYPE'] = totas.data[totas.key.indexOf('SOT.TRUST_CURR_TYPE')];
		        //客戶註記
		        $scope.mappingSet['SOT.SPEC_CUSTOMER'] = totas.data[totas.key.indexOf('SOT.SPEC_CUSTOMER')];
		        //轉換方式
				$scope.mappingSet['SOT.TRANSFER_TYPE_DYNA']  = totas.data[totas.key.indexOf('SOT.TRANSFER_TYPE_DYNA')];
				//公開說明書選項
				$scope.mappingSet['SOT.PROSPECTUS_TYPE'] = totas.data[totas.key.indexOf('SOT.PROSPECTUS_TYPE')];
			}
		});


        $scope.init = function() {
        	$scope.fromApp = false;
        	$scope.carList = [];
        	
			$scope.inputVO = {
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
				piRemark: '',                               //專業投資人註記
				debitAcct: '', 								//扣款帳號
				trustAcct: '', 								//信託帳號
				creditAcct: '',								//收益入帳帳號
				custProType: '',                            //專業投資人類型1：大專投 2：小專投
				custType: 'CUST', 							//來行人員
				goOPDisabled: false,						//傳送OP按鈕，避免連續點擊
				prodType:"8",								//8:動態鎖利
				trustTS: 'S',                               //M:金錢信託  S:特金交易--預設特金如果契約編號有值則為SOT112交易為金錢信託
				isWeb: 'N',									//臨櫃交易N, 快速申購Y
				ovsPrivateYN: '',      						//是否為境外私募基金
				loanFlag: 'N'                               //90天貸款註記(Y/N)
			};
		};
		$scope.init();
		        
        $scope.inputVO.tradeSEQ = $scope.connector('get', 'SOTTradeSEQ');
        $scope.connector('set', 'SOTTradeSEQ', null);//避免跨交易返回本頁殘值
        
        $scope.noCallCustQuery = function () {
        	var deferred = $q.defer();
        	$scope.sendRecv("SOT1640", "query", "com.systex.jbranch.app.server.fps.sot1640.SOT1640InputVO", $scope.inputVO,
					function(tota, isError) {
        				debugger
						if (!isError) {
							if (tota[0].body.mainList.length == 0) {
								$scope.connector('set', "SOTTradeSEQ", null);
								$scope.connector('set', "SOTCarSEQ", null);
								$rootScope.menuItemInfo.url = "assets/txn/SOT1640/SOT1640.html";	
								return;
							}
							$scope.inputVO.custID = tota[0].body.mainList[0].CUST_ID;						//客戶ID
							$scope.inputVO.custName = tota[0].body.mainList[0].CUST_NAME;	 

							$scope.inputVO.kycLV = tota[0].body.mainList[0].KYC_LV;	 						//KYC等級
							$scope.inputVO.kycDueDate = $scope.toJsDate(tota[0].body.mainList[0].KYC_DUE_DATE);	 			//KYC效期
							$scope.inputVO.profInvestorYN = tota[0].body.mainList[0].PROF_INVESTOR_YN;	 	//是否為專業投資人
							$scope.inputVO.piDueDate = $scope.toJsDate(tota[0].body.mainList[0].PI_DUE_DATE);	 			//專業投資人效期
							$scope.inputVO.custRemarks = tota[0].body.mainList[0].CUST_REMARKS;	 			//客戶註記
							$scope.inputVO.isOBU = tota[0].body.mainList[0].IS_OBU;	 						//是否為OBU客戶
							$scope.inputVO.isAgreeProdAdv = tota[0].body.mainList[0].IS_AGREE_PROD_ADV;	 	//同意投資商品諮詢服務
							$scope.inputVO.piRemark = tota[0].body.mainList[0].PI_REMARK;	 	            //專業投資人註記
							$scope.inputVO.bargainDueDate = $scope.toJsDate(tota[0].body.mainList[0].BARGAIN_DUE_DATE);		//期間議價效期
							$scope.inputVO.bargainFeeFlag = tota[0].body.mainList[0].BARGAIN_FEE_FLAG;		//議價狀態
							$scope.inputVO.tradeStatus = tota[0].body.mainList[0].TRADE_STATUS;				//交易狀態
							$scope.inputVO.isWeb = tota[0].body.mainList[0].IS_WEB;							//是否為網銀快速申購(Y/N)
							$scope.inputVO.loanFlag = tota[0].body.mainList[0].FLAG_NUMBER;				    //90天貸款註記(Y/N)
							debugger
							$scope.mainList = tota[0].body.mainList;
							$scope.carList = tota[0].body.carList;
							
							if ($scope.mainList[0].IS_REC_NEEDED == 'N') {
								$scope.recSeqFlagOrg = 'N';
							} else {
								$scope.recSeqFlagOrg = 'Y';
							}							
							$scope.recSeqFlag = $scope.recSeqFlagOrg;
							
							$scope.outputVO = tota[0].body;
							
							deferred.resolve("success");
						}
			});
			
			return deferred.promise;
		};
		
		$scope.getSOTCustInfoCT = function () {
			$scope.sendRecv("SOT110", "getSOTCustInfoCT", "com.systex.jbranch.app.server.fps.sot110.SOT110InputVO", {'tradeSEQ':$scope.inputVO.tradeSEQ,'custID':$scope.inputVO.custID, 'prodType':1, 'tradeType':1},
					function(totaCT, isError) {
						if (!isError) {
							$scope.isFirstTrade = (totaCT[0].body.isFirstTrade == "Y" ? $filter('i18n')('ehl_02_SOT_003') : "");     //是否首購
							$scope.ageUnder70Flag = (totaCT[0].body.ageUnder70Flag == "N" ? $filter('i18n')('ehl_02_SOT_004') : ""); //年齡小於70
							$scope.eduJrFlag = (totaCT[0].body.eduJrFlag == "N" ? $filter('i18n')('ehl_02_SOT_005') : "");           //教育程度國中以上(不含國中)
							$scope.healthFlag = (totaCT[0].body.healthFlag == "N" ? $filter('i18n')('ehl_02_SOT_006') : "");         //未領有全民健保重大傷病證明
							
							return;
						}
			});
		};
		
		$scope.enterPage = function () {
			$scope.noCallCustQuery().then(function(data) {
				$scope.getSOTCustInfoCT();
			});
		};
		$scope.enterPage();
		
		$scope.delCar = function(row) {
			var txtMsg = "";
			if ($scope.carList.length == 1) {
				txtMsg = $filter('i18n')('ehl_02_common_005');
			} else {
				txtMsg = $filter('i18n')('ehl_02_common_006');
			}
			$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
				$scope.sendRecv("SOT1640", "delProd", "com.systex.jbranch.app.server.fps.sot1640.SOT1640InputVO", {tradeSEQ : $scope.inputVO.tradeSEQ},
						function(tota, isError) {
							if (!isError) {
								$scope.showMsg("ehl_01_common_003"); //刪除成功
								$scope.newTrade();
								return;
							}
				});
            });
		};
		
		$scope.goPage = function(row) {
			$scope.connector('set', "SOTTradeSEQ", row.TRADE_SEQ);
			$scope.connector('set', "SOTCarSEQ", row.SEQ_NO);
			$rootScope.menuItemInfo.url = "assets/txn/SOT1640/SOT1640.html";	
		};
		
		$scope.query = function() {
			if($scope.inputVO.tradeSEQ) {
				$scope.noCallCustQuery().then(function(data) {
					$scope.getSOTCustInfoCT();
				});
			} else {
				$scope.connector('set', "SOTTradeSEQ", null);
				$rootScope.menuItemInfo.url = "assets/txn/SOT1640/SOT1640.html";	
			}
		};
		$scope.query();
		
		//傳送OP交易，避免按鈕被連續點擊造成錯誤，先disable按鈕並$timeout
		$scope.goOP = function () {
			$scope.inputVO.goOPDisabled = true;
			
			$timeout(function() {
				$scope.do_goOP(); 
				$scope.inputVO.goOPDisabled = false;}
			, 1000);			
		}
		
		//傳送OP交易  ; 產生動態鎖利轉換申請書表單
		$scope.do_goOP = function () {
			//檢核錄音序號
			if(!$scope.inputVO.recSEQ && $scope.mainList[0].IS_REC_NEEDED == 'Y') {
				$scope.showErrorMsg("ehl_01_common_022");
				return;
			}
			//進行傳送OP交易及列印表單鍵  (批號產生)
			$scope.sendRecv("SOT712", "updateBatchSeq", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {'prodType': '8', 'tradeType':3 ,
																												   'tradeSeq': $scope.inputVO.tradeSEQ}, 
			function(tota, isError) {
				if (!isError) {
					$scope.sendRecv("SOT1640", "goOP", "com.systex.jbranch.app.server.fps.sot1640.SOT1640InputVO", $scope.inputVO,
							function(tota, isError) {
								if (!isError) {
									if (tota[0].body.errorMsg != '' && tota[0].body.errorMsg != null) {
										$scope.showErrorMsg(tota[0].body.errorMsg);
									} else {
										$scope.showMsg("ehl_02_SOT_002");
										$scope.query();
										$scope.printReport();//產生贖回申請書表單
										
										return;
									}
								}
					});
				}
			});			
		}; 
				
		//列印申請書表單
		$scope.printReport = function() {
			var fitVO = {
				caseCode : 		1, 							//case1 下單
				custId   :		$scope.inputVO.custID,		//客戶ID
				prdType  :      8,							//商品類別 : 8:基金動態鎖利
				tradeSeq : 		$scope.inputVO.tradeSEQ, 	//交易序號
				tradeType:		3,							//基金交易類別 : 轉換
				isbBackend:		"N",						//是否為後收型基金(Y/N)
				isPrintSOT819:	$scope.inputVO.loanFlag 	//是否列印貸款風險預告書
			}
				
			$scope.sendRecv("SOT712", "fitToGetPDF", "com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO", fitVO,
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsg(totas[0].body.msgData);
						}
			});
		}
		
		// new
		$scope.newTrade = function() {
			$scope.connector('set','SOTTradeSEQ', null);
			$scope.connector('set','SOTCarSEQ', null);
			$rootScope.menuItemInfo.url = "assets/txn/SOT1640/SOT1640.html";	
			
		};
		
		// for App WebView
		if (typeof(webViewParamObj) != 'undefined') {
			$scope.fromApp = true;
		}

		//檢核錄音序號 
		$scope.validateRecSeq = function() { 
			if($scope.inputVO.recSEQ == ''){
				$scope.recSeqFlag = $scope.recSeqFlagOrg;
				return;
			}
			//錄音檢核除了 SI/SN 一定要給商品 PROD_ID 以外；其他 3 種，不需要給 PROD_ID~
			var sot712InputVO ={'custID':$scope.inputVO.custID,
					'prodType': 'FUND',
					'recSeq':$scope.inputVO.recSEQ,
					'ovsPrivateYN': 'N'
					};
			$scope.sendRecv("SOT712", "validateRecseq", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO",sot712InputVO ,
			function(tota, isError) {
				if (!isError) {
					if(!tota[0].body.Recseq){
						$scope.showErrorMsg("ehl_02_common_007");
						$scope.inputVO.recSEQ='';
						$scope.recSeqFlag = $scope.recSeqFlagOrg;
					} else {
						$scope.recSeqFlag = 'N';
					}
					return;
				}
			});
		}; 
});