'use strict';
eSoafApp.controller('SOT212Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q, validateService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT212Controller";
		
		// 繼承特金ETF原來所有function
		$controller('SOT210Controller', {$scope: $scope});
		
		// 參數取得
		$scope.getParam = function () {
			var deferred = $q.defer();
			
			getParameter.XML(['SOT.TRUST_ACCT', 'SOT.CREDIT_ACCT_ETF'], function(totas) {
				if(len(totas) > 0) {
					debugger
					$scope.mappingSet['SOT.TRUST_ACCT_LIST#TRUST'] = totas.data[totas.key.indexOf('SOT.TRUST_ACCT')];
					$scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'] = totas.data[totas.key.indexOf('SOT.CREDIT_ACCT_ETF')];
					
					deferred.resolve("success");
				} else {
					deferred.resolve("");
				}
			});
			
			return deferred.promise;
		};
		
		// 信託帳號
		$scope.setTrustAcct = function () {
			debugger;
			$scope.inputVO.trustAcct = $scope.mappingSet['SOT.TRUST_ACCT_LIST#TRUST'][1].DATA;
		}
		
		// 收益入帳帳號
		$scope.setCreditAcct = function () {
			debugger;
			$scope.inputVO.creditAcct = $scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'][1].DATA;
		}
		
		// 扣款帳號
		$scope.getACC = function(enterTime) {
			debugger
			if (enterTime == 'default' || (enterTime == 'change' && $scope.interFlag)) {
				$scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'] = [];

				if ($scope.inputVO.contractID != '') {
					angular.forEach($scope.mappingSet['SOT.CONTRACT_LIST'], function(contractRow){ 
						if ($scope.inputVO.contractID == contractRow.DATA) {
							
							if(contractRow.CONTRACT_SPE_FLAG.substring(0,2) == '01' || contractRow.LABEL.substring(0,9) == '250100000'){
								$scope.showMsg("ehl_02_SOT_998");
							}
							
							if(enterTime == 'change' || ($scope.lockFlagByContractID == true && enterTime == 'default')) {
								$scope.needclear = false;
								//0000275: 金錢信託受監護受輔助宣告交易控管調整
								if(contractRow.GUARDIANSHIP_FLAG == '1'){
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
							
							$scope.acctCurrencyByM = contractRow.CUR;
							if ($scope.checkAccCurrency($scope.acctCurrencyByM)) {
								$scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'].push({LABEL: contractRow.ACC, DATA: contractRow.ACC});
								
								$scope.inputVO.trustPeopNum = contractRow.FLAG;
								
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
							} else {
								$scope.inputVO.contractID = '';
							}
						}
					});
					
					if($scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'].length == 1){ // 只有一筆不能勾選
						$scope.inputVO.debitAcct = $scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'][0].DATA;
						$scope.cmbDebitAcctByTrust = true;
					}
				}
			}
			
			// 只有一筆不能勾選，協助設定預設值時，會觸發ng-change導致confirm跳兩次，以interFlag判斷是否執行
			if (enterTime == 'change' && !$scope.interFlag) {
				$scope.interFlag = true;
			}
		};
		
		$scope.noCallCustQueryByM = function() {
			var deferred = $q.defer();
			
			// 取得契約
			$scope.inputVO.trustPeopNum = 'N';								// 是否為多委託人契約 Y=是/N=否
			$scope.mappingSet['SOT.CONTRACT_LIST'] = [];
			$scope.sendRecv("SOT315", "getCustANDContractList", "com.systex.jbranch.app.server.fps.sot315.SOT315InputVO", {'custID':$scope.inputVO.custID}, function(tota, isError) {
				if (!isError) {
					debugger
					$scope.mappingSet['SOT.CONTRACT_LIST'] = tota[0].body.contractList;
					$scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'] = [];
//					$scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'] = [];
					
					if($scope.mappingSet['SOT.CONTRACT_LIST'].length == 1){ //只有一筆不能勾選
						$scope.inputVO.contractID = $scope.mappingSet['SOT.CONTRACT_LIST'][0].DATA;
						$scope.lockFlagByContractID = true;
					} else if ($scope.mappingSet['SOT.CONTRACT_LIST'].length < 1){ //沒有契約資料不可以繼續 
//						$scope.lockFlagByContractID = true;
						$scope.showErrorMsg("此客戶查無金錢信託契約資料");
						$scope.custClear();
						$scope.inputVO.custID = "";
					} else {
						$scope.interFlag = true;
						$scope.lockFlagByContractID = false;
					}
					
					deferred.resolve("success");
				}
			});
			
			return deferred.promise;
		}
		
    	// 新的交易
		$scope.getCustANDContractList = function(input) {				// 若為新交易，要取得客戶契約編號時... 
			$scope.getParam().then(function() {							// 取得參數
				$scope.getSOTCustInfo(input).then(function(data) {		// STEP 1. 取得客戶基本資料
					$scope.interFlag = false;
					$scope.noCallCustQueryByM().then(function() { 		// STEP 2. 取得契約編號
						$scope.getACC('default');						// STEP 3. 設定扣款帳號
						$scope.setCreditAcct();							// STEP 4. 設定收益入帳帳號
						$scope.setTrustAcct();							// STEP 5. 設定信託帳號
					});
				});
			});
		};
		
		// 進入畫面時...
		$scope.SOT212init = function() {
			$scope.mappingSet['SOT.CONTRACT_LIST'] = [];				// 契約編號列表
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'] = [];		// 扣款帳號
			$scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'] = [];		// 收益入帳帳號
			$scope.mappingSet['SOT.TRUST_ACCT_LIST#TRUST'] = [];		// 信託帳號
			
			$scope.cmbDebitAcctByTrust = true;
			$scope.inputVO.trustTS = 'M';								// S=特金/M=金錢信託
			$scope.inputVO.trustPeopNum = 'N';							// 是否為多委託人契約 Y=是/N=否
			$scope.lockFlagByContractID = true;
			
			if ($scope.inputVO.tradeSEQ) { 								// 若由SOT610整合交易進入畫面時...
				$scope.getParam().then(function() {						// 取得參數
					$scope.noCallCustQuery().then(function() { 			// STEP 1. 取得客戶基本資料
						$scope.interFlag = false;
						$scope.noCallCustQueryByM().then(function() { 	// STEP 2. 取得契約編號 
							$scope.getACC('default');					// STEP 3. 設定扣款帳號
							$scope.setCreditAcct();						// STEP 4. 設定收益入帳帳號
							$scope.setTrustAcct();						// STEP 5. 設定信託帳號
						});
					});
				});
			}
		}
		
		$scope.SOT212init();		
});