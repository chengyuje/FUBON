/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT223Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q, validateService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT223Controller";		
		//特金ETF贖回繼承
		$controller('SOT220Controller', {$scope: $scope});

		// 參數取得
		$scope.getParam = function () {
			var deferred = $q.defer();
			
			getParameter.XML(['SOT.TRUST_ACCT', 'SOT.CREDIT_ACCT_ETF'], function(totas) {
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
			debugger
			$scope.inputVO.trustAcct = $scope.mappingSet['SOT.TRUST_ACCT_LIST#TRUST'][0].DATA;
		}
		
		$scope.noCallCustQueryByM = function() {
			var deferred = $q.defer();
			
			// 取得契約
			$scope.inputVO.trustPeopNum = 'N';								// 是否為多委託人契約 Y=是/N=否
			$scope.mappingSet['SOT.CONTRACT_LIST'] = [];
			$scope.sendRecv("SOT315", "getCustANDContractList", "com.systex.jbranch.app.server.fps.sot315.SOT315InputVO", {'custID':$scope.inputVO.custID}, function(tota, isError) {
				if (!isError) {
					debugger
					$scope.mappingSet['SOT.CONTRACT_LIST'] = tota[0].body.contractList;					
					deferred.resolve("success");
				}
			});
			
			return deferred.promise;
		}
		
		//取得庫存
		$scope.goSOT225 = function() {
			$scope.custID = $scope.inputVO.custID;
			
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT225/SOT225.html',
				className: 'SOT225',
				showClose: false,
				scope : $scope
			}).closePromise.then(function (data) {
				$scope.prodClear();
				if($scope.connector('get', 'SOTProd')){
					debugger;
					$scope.inputVO.prodID = $scope.connector('get', 'SOTProd').ExtProdId ? $scope.connector('get', 'SOTProd').ExtProdId : null;
					$scope.inputVO.creditAcct = $scope.connector('get', 'SOTProd').AccountNo ? $scope.connector('get', 'SOTProd').AccountNo : null;
					$scope.inputVO.contractID = $scope.connector('get', 'SOTProd').ContractId ? $scope.connector('get', 'SOTProd').ContractId : null;
					var findContract = $filter('filter')($scope.mappingSet['SOT.CONTRACT_LIST'], {DATA: $scope.inputVO.contractID});
					if(findContract != null && findContract.length > 0) {
						$scope.inputVO.GUARDIANSHIP_FLAG = findContract[0].GUARDIANSHIP_FLAG;
						$scope.inputVO.trustPeopNum = findContract[0].FLAG;
					}
					$scope.getProdDTL();
				}
			});
		};
		
		// 新的交易
		$scope.getCustAccData = function(input) {				// 若為新交易，要取得客戶契約編號時... 
			$scope.getParam().then(function() {							// 取得參數
				$scope.getSOTCustInfo(input).then(function(data) {		// STEP 1. 取得客戶基本資料
					$scope.interFlag = false;
					$scope.noCallCustQueryByM().then(function() { 		// STEP 2. 取得契約編號
						$scope.setTrustAcct();							// STEP 5. 設定信託帳號
					});
				});
			});
		};
		
		// 進入畫面時...
		$scope.SOT223init = function() {
			$scope.mappingSet['SOT.CONTRACT_LIST'] = [];				// 契約編號列表
			
			$scope.inputVO.trustTS = 'M';								// S=特金/M=金錢信託
			$scope.inputVO.trustPeopNum = 'N';							// 是否為多委託人契約 Y=是/N=否
			$scope.inputVO.GUARDIANSHIP_FLAG = '';						// 受監護輔助 空白：無監護輔助 1.監護宣告 2輔助宣告
			
			if ($scope.inputVO.tradeSEQ) { 								// 若由SOT610整合交易進入畫面時...
				$scope.getParam().then(function() {						// 取得參數
					$scope.noCallCustQuery().then(function() { 			// STEP 1. 取得客戶基本資料
						$scope.interFlag = false;
						$scope.noCallCustQueryByM().then(function() { 	// STEP 2. 取得契約編號 
							$scope.setTrustAcct();						// STEP 5. 設定信託帳號
						});
					});
				});
			}
		}
		$scope.SOT223init();
});