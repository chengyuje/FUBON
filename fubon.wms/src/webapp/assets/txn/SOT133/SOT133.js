/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT133Controller',
	function($rootScope, $scope, $controller, $confirm, sysInfoService ,socketService, ngDialog, projInfoService,$filter, $q, getParameter, validateService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT133Controller";
		
		$controller('SOT130Controller', {$scope: $scope});
		
		$scope.getContractList = function() {
			$scope.inputVO.trustTS = 'M'; //有時候init抓不到值,只好在選契約編號時再指定一次
			var deferred = $q.defer();
			// 取得契約
			$scope.mappingSet['SOT.CONTRACT_LIST'] = [];
			$scope.sendRecv("SOT315", "getCustANDContractList", "com.systex.jbranch.app.server.fps.sot315.SOT315InputVO", {'custID':$scope.inputVO.custID}, function(tota, isError) {
				if (!isError) {
					$scope.mappingSet['SOT.CONTRACT_LIST'] = tota[0].body.contractList;
					deferred.resolve("success");
				}
			});
			return deferred.promise;
		}
		
		// 取出扣款帳號
		$scope.getACC = function() {
			angular.forEach($scope.mappingSet['SOT.CONTRACT_LIST'], function(contractRow){ 
				if ($scope.inputVO.contractID == contractRow.DATA) { 
					$scope.inputVO.debitAcct = contractRow.ACC; //扣款帳號
					$scope.inputVO.trustPeopNum = contractRow.FLAG; //是否為多委託人契約
					//0000275: 金錢信託受監護受輔助宣告交易控管調整
					// #1324: 金錢信託高齡套表規則新增
					if(contractRow.CONTRACT_SPE_FLAG === '03' && contractRow.CONTRACT_P_TYPE === '58'){
						$scope.showErrorMsg("ehl_01_SOT_M_001");
						$scope.inputVO.custID = "";
						$scope.inputVO.contractID = "";
					}
					$scope.inputVO.GUARDIANSHIP_FLAG = contractRow.GUARDIANSHIP_FLAG;
				}
			});
		};
		
		// 進入畫面時...
		$scope.SOT133init = function() {
			debugger;
			$scope.mappingSet['SOT.CONTRACT_LIST'] = [];				// 契約編號列表
			$scope.inputVO.trustTS = 'M';
			$scope.getTradeDate();
			console.log("trustTS = "+$scope.inputVO.trustTS);
			$scope.mappingSet['SOT.TRADE_DATE_TYPE#TRUST'] = [];		// 交易類別-只提供即時交易
			$scope.mappingSet['SOT.TRADE_DATE_TYPE#TRUST'].push({LABEL: "即時", DATA: "1"});
			$scope.inputVO.tradeDateType = "1";
			
			if ($scope.inputVO.tradeSEQ) { 							// 若由SOT610整合交易進入畫面時...
				$scope.noCallCustQuery().then(function() { 			// STEP 1. 取得客戶基本資料
					$scope.getContractList().then(function() { 		// STEP 2. 取得契約編號 
						$scope.getACC(); 							// STEP 3. 設定扣款帳號/收益入帳帳號						
					});
				});
			}	
		}
		$scope.SOT133init();
});