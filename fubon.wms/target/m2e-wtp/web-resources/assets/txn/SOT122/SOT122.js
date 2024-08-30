/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT122Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q, validateService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT122Controller";
	
		  
		// 特金基金單筆申購繼承原來所有function
		$controller('SOT120Controller', {$scope: $scope});
		
		// 取得契約列表
		$scope.getCustANDContractList = function(input) {
			$scope.getParam().then(function() {							// 取得參數
				$scope.getSOTCustInfo(input).then(function(data) {		// STEP 1. 取得客戶基本資料
					$scope.noCallCustQueryByM().then(function() { 		// STEP 2. 取得契約編號
						$scope.getACC('default'); 						// STEP 3. 設定扣款帳號/收益入帳帳號
						$scope.setTrustAcct();							// STEP 4. 設定信託帳號
					});
				});
			});
		};
		
		// getXML
		$scope.getParam = function () {
			var deferred = $q.defer();
			
			getParameter.XML(['SOT.TRUST_ACCT'], function(totas) {
				if(len(totas) > 0) {
					$scope.mappingSet['SOT.TRUST_ACCT_LIST#TRUST'] = totas.data[totas.key.indexOf('SOT.TRUST_ACCT')];
					
					deferred.resolve("success");
				} else {
					deferred.resolve("");
				}
			});
			
			return deferred.promise;
		};
		
		$scope.noCallCustQueryByM = function() {
			var deferred = $q.defer();
			
			// 取得契約
			$scope.mappingSet['SOT.CONTRACT_LIST'] = [];
			$scope.sendRecv("SOT315", "getCustANDContractList", "com.systex.jbranch.app.server.fps.sot315.SOT315InputVO", {'custID':$scope.inputVO.custID}, function(tota, isError) {
				if (!isError) {
					$scope.mappingSet['SOT.CONTRACT_LIST'] = tota[0].body.contractList;
					$scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'] = [];
					$scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'] = [];
					
					deferred.resolve("success");
				}
			});
			
			return deferred.promise;
		}
		
		// 信託帳號
		$scope.setTrustAcct = function () {
			$scope.inputVO.trustAcct = $scope.mappingSet['SOT.TRUST_ACCT_LIST#TRUST'][1].DATA;
		}
		
		// 扣款帳號 + 收益入帳帳號
		$scope.getACC = function(enterTime) {
			$scope.prodClear();
			$scope.inputVO.prodId = '';
			$scope.inputVO.trustTS = 'M'; //有時候init抓不到值,只好在選契約編號時再指定一次
			
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'] = [];
			$scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'] = [];
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST#DISPLAY'] = [];
			angular.forEach($scope.mappingSet['SOT.CONTRACT_LIST'], function(contractRow){ 
				if ($scope.inputVO.contractID == contractRow.DATA) {
					$scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'].push({LABEL: contractRow.ACC, DATA: contractRow.ACC, CUR: contractRow.CUR});
					$scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'].push({LABEL: contractRow.ACC, DATA: contractRow.ACC, CUR: contractRow.CUR});
					$scope.inputVO.trustPeopNum = contractRow.FLAG; //是否為多委託人契約
					if ($scope.inputVO.trustPeopNum == 'Y') {
						var txtMsg = $filter('i18n')('ehl_02_SOT_999');
						
						$confirm({text: txtMsg, ok: '繼續', cancel: '結束'})
						.then(function (e) {
						}).catch(function (e) {
							$scope.inputVO.custID = '';
							$scope.custClear();
							
							$scope.inputVO.prodID = '';
							$scope.prodClear();
							return;
						});
					}
					
					if(contractRow.CONTRACT_SPE_FLAG.substring(0,2) == '01' || contractRow.LABEL.substring(0,9) == '250100000'){
						$scope.showMsg("ehl_02_SOT_998");
					}
					
					if(enterTime == 'change' || (enterTime == 'default' && $scope.mappingSet['SOT.CONTRACT_LIST'].length == 1)) {
						$scope.needclear = false;
						//0000275: 金錢信託受監護受輔助宣告交易控管調整
						if(contractRow.GUARDIANSHIP_FLAG === '1'){
							$scope.showErrorMsg("ehl_02_SOT_014");
							$scope.needclear = true;
						}
						// #1324: 金錢信託高齡套表規則新增
						if(contractRow.CONTRACT_SPE_FLAG === '03' && contractRow.CONTRACT_P_TYPE === '58'){
							$scope.showErrorMsg("ehl_01_SOT_M_001");
							$scope.needclear = true;
						}
						if($scope.needclear) {
							$scope.custClear();
							$scope.inputVO.custID = "";
							$scope.inputVO.contractID = "";
						}			
					}
					$scope.inputVO.GUARDIANSHIP_FLAG = contractRow.GUARDIANSHIP_FLAG;
				}
			});
			
			if($scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'].length == 1){ //只有一筆不能勾選
				$scope.inputVO.debitAcct = $scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'][0].DATA;
				$scope.cmbDebitAcct = true;
			}
			
			if($scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'].length == 1){ //只有一筆不能勾選
				$scope.inputVO.creditAcct = $scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'][0].DATA;
				$scope.cmbCreditAcct = true;
			}
			
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST#DISPLAY'] = $scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'];
		};
		
		
		// 進入畫面時...
		$scope.SOT122init = function() {
			$scope.mappingSet['SOT.CONTRACT_LIST'] = [];				// 契約編號列表
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'] = [];		// 扣款帳號
			$scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'] = [];		// 收益入帳帳號
			$scope.inputVO.trustTS = 'M';
			$scope.getReserveTradeDate();
			
			$scope.mappingSet['SOT.TRADE_DATE_TYPE#TRUST'] = [];		// 交易類別-只提供即時交易
			$scope.mappingSet['SOT.TRADE_DATE_TYPE#TRUST'].push({LABEL: "即時", DATA: "1"});
			$scope.inputVO.tradeDateType = "1";
			
			$scope.mappingSet['SOT.CHANGE_TRADE_SUB_TYPE#TRUST'] = [];	// 信託型態-只提供定期定額
			$scope.mappingSet['SOT.CHANGE_TRADE_SUB_TYPE#TRUST'].push({LABEL: "定期定額", DATA: "2"});
			$scope.inputVO.tradeSubType = '2';
			
			if ($scope.inputVO.tradeSEQ) { 								// 若由SOT610整合交易進入畫面時...
				$scope.getParam().then(function() {						// 取得參數
					$scope.noCallCustQuery().then(function() { 			// STEP 1. 取得客戶基本資料
						$scope.noCallCustQueryByM().then(function() { 	// STEP 2. 取得契約編號 
							$scope.getACC('default'); 					// STEP 3. 設定扣款帳號/收益入帳帳號
							$scope.setTrustAcct();						// STEP 4. 設定信託帳號
						});
					});
				});
			}
			
			
			
		}
		$scope.SOT122init();
		
});