/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT325Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q, validateService) {
	
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT325Controller";
		
		// 特金海外債贖回繼承
		$controller('SOT320Controller', {$scope: $scope});

		// 參數取得
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
		
		// 信託帳號
		$scope.setTrustAcct = function () {
			$scope.inputVO.trustAcct = $scope.mappingSet['SOT.TRUST_ACCT_LIST#TRUST'][1].DATA;
		}
				
		// 扣款帳號 + 收益入帳帳號
		$scope.getACC = function(enterTime) {
			if (enterTime == 'default' || (enterTime == 'change' && $scope.interFlag)) {
				$scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'] = [];
				$scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'] = [];

				if ($scope.inputVO.contractID != '') {
					angular.forEach($scope.mappingSet['SOT.CONTRACT_LIST'], function(contractRow){ 
						if ($scope.inputVO.contractID == contractRow.DATA) { 
							$scope.acctCurrencyByM = contractRow.CUR;
							$scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'].push({LABEL: contractRow.ACC + "_" + contractRow.CUR, DATA: contractRow.ACC});
							$scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'].push({LABEL: contractRow.ACC + "_" + contractRow.CUR, DATA: contractRow.ACC});
							
							$scope.inputVO.trustPeopNum = contractRow.FLAG;
							$scope.inputVO.debitAcct = contractRow.ACC;
							$scope.inputVO.creditAcct = contractRow.ACC;
							$scope.inputVO.cmbCreditAcct = contractRow.ACC;
							
							if($scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'].length == 1){ // 只有一筆不能勾選
								$scope.inputVO.debitAcct = $scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'][0].DATA;
								$scope.cmbDebitAcctByTrust = true;
							}
							
							if($scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'].length == 1){ // 只有一筆不能勾選
								$scope.inputVO.creditAcct = $scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'][0].DATA;
								$scope.cmbCreditAcctByTrust = true;
							}
							
							//0000275: 金錢信託受監護受輔助宣告交易控管調整
							// #1324: 金錢信託高齡套表規則新增
							if(contractRow.CONTRACT_SPE_FLAG === '03' && contractRow.CONTRACT_P_TYPE === '58'){
								$scope.showErrorMsg("ehl_01_SOT_M_001");
								$scope.inputVO.custId = "";
								$scope.inputVO.contractID = "";
								$scope.inputVO.debitAcct ="";
							}
	
							$scope.inputVO.GUARDIANSHIP_FLAG = contractRow.GUARDIANSHIP_FLAG;
						}
					});
					

				}
			}
			
			// 只有一筆不能勾選，協助設定預設值時，會觸發ng-change導致confirm跳兩次，以interFlag判斷是否執行
			if (enterTime == 'change' && !$scope.interFlag) {
				$scope.interFlag = true;
			}
		};
		
		$scope.noCallCustQueryByM = function() {
			var deferred = $q.defer();
			
			if (($scope.inputVO.custId).length > 0) {
				// 取得契約
				$scope.inputVO.trustPeopNum = 'N';								// 是否為多委託人契約 Y=是/N=否
				$scope.mappingSet['SOT.CONTRACT_LIST'] = [];
				$scope.sendRecv("SOT315", "getCustANDContractList", "com.systex.jbranch.app.server.fps.sot315.SOT315InputVO", {'custID':$scope.inputVO.custId}, function(tota, isError) {
					if (!isError) {
						$scope.mappingSet['SOT.CONTRACT_LIST'] = tota[0].body.contractList;
						$scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'] = [];
						$scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'] = [];
						
						if($scope.mappingSet['SOT.CONTRACT_LIST'].length == 1){ //只有一筆不能勾選
							$scope.inputVO.contractID = $scope.mappingSet['SOT.CONTRACT_LIST'][0].DATA;
							$scope.lockFlagByContractID = true;
						} else if ($scope.mappingSet['SOT.CONTRACT_LIST'].length < 1){ //只有一筆不能勾選 
							$scope.lockFlagByContractID = true;
						} else {
							$scope.interFlag = true;
							$scope.lockFlagByContractID = false;
						}
						
						$scope.getACC('default');
						
						deferred.resolve("success");
					}
				});
			} else {
				deferred.resolve("");
			}
			
			return deferred.promise;
		}
		
		// 進入畫面時...
		$scope.SOT325init = function() {
			$scope.mappingSet['SOT.CONTRACT_LIST'] = [];				// 契約編號列表
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'] = [];		// 扣款帳號
			$scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'] = [];		// 收益入帳帳號
			$scope.inputVO.trustTS = 'M';

			$scope.inputVO = {
				custId: '', 
				trustTS: 'M', 
				contractID: '', 
				debitAcct: '', 
				cmbCreditAcct: ''
        	};
			
			$scope.lockFlagByContractID = true;
		}
		$scope.SOT325init();
});