/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT132Controller', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sotService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "SOT132Controller";

	// xml參數初始化
	var vo = {
		'param_type' : 'SOT.ASSET_TRADE_SUB_TYPE',
		'desc' : false
	};
	if (!projInfoService.mappingSet['SOT.ASSET_TRADE_SUB_TYPE']) {
		$scope.requestComboBox(vo, function(totas) {
			if (totas[totas.length - 1].body.result === 'success') {
				projInfoService.mappingSet['SOT.ASSET_TRADE_SUB_TYPE'] = totas[0].body.result;
				$scope.mappingSet['SOT.ASSET_TRADE_SUB_TYPE'] = projInfoService.mappingSet['SOT.ASSET_TRADE_SUB_TYPE'];
			}
		});
	} else
		$scope.mappingSet['SOT.ASSET_TRADE_SUB_TYPE'] = projInfoService.mappingSet['SOT.ASSET_TRADE_SUB_TYPE'];

	$scope.init = function() {
		// 判斷導入的交易是否為SOT140,SOT150
		$scope.txnName = $filter('uppercase')($scope.txnName);
		if ($scope.txnName && ($scope.txnName == 'SOT140' || $scope.txnName == 'SOT150')) {
			$scope.disButton = true;
		}
	};

	$scope.query = function() {
		if($scope.dynamicYN && $scope.dynamicYN == "Y") {
			//動態鎖利庫存
			$scope.queryDyna();
		} else {
			//一般基金庫存
			$scope.queryNormal();
		}
	}
	
	//非動態鎖利
	$scope.queryNormal = function() {
		$scope.sendRecv("SOT703", "getCustAssetNFData", "com.systex.jbranch.app.server.fps.sot703.SOT703InputVO", {
			"custId" : $scope.custID,
			"trustTS" : $scope.trustTS,
			"debitAcct" : $scope.debitAcct
		}, function(tota, isError) {
			if (!isError) {
				if (tota[0].body.custAssetFundList) {
					$scope.astList = tota[0].body.custAssetFundList;
					$scope.outputVO = tota[0].body;

					for (var i = 0; i < $scope.astList.length; i++) {
						if ($scope.astList[i].AssetType) {
							switch ($scope.astList[i].AssetType) {
							case "0001":
								$scope.astList[i].assetTradeSubType = '1';
								$scope.astList[i].assetTradeSubTypeD = '1';
								if(!$scope.astList[i].Dynamic || $scope.astList[i].Dynamic == '') {
									$scope.astList[i].FundName = $scope.astList[i].FundName + "(一般)";
								} else if($scope.astList[i].Dynamic == '1') {
									$scope.astList[i].FundName = $scope.astList[i].FundName + "(動態鎖利_母)";
								} else if($scope.astList[i].Dynamic == '2') {
									$scope.astList[i].FundName = $scope.astList[i].FundName + "(動態鎖利_子)";
								}
								break;
							case "0002":
								$scope.astList[i].FundName = $scope.astList[i].FundName + "(一般)";

								if (sotService.isFundProjectByTxType($scope.astList[i].TxType)) {
									$scope.astList[i].assetTradeSubType = '4';
									$scope.astList[i].assetTradeSubTypeD = $scope.astList[i].TxType === 'Y'? '4': '8';
								} else {
									$scope.astList[i].assetTradeSubType = '2';
									$scope.astList[i].assetTradeSubTypeD = '2';
								}
								break;
							case "0003":
								$scope.astList[i].FundName = $scope.astList[i].FundName + "(一般)";
								if (sotService.isFundProjectByTxType($scope.astList[i].TxType)) {
									$scope.astList[i].assetTradeSubType = '5';
									$scope.astList[i].assetTradeSubTypeD = $scope.astList[i].TxType === 'Y'? '5': '9';
								} else {
									$scope.astList[i].assetTradeSubType = '3';
									$scope.astList[i].assetTradeSubTypeD = '3';
								}
								break;
							case "0004":
								$scope.astList[i].FundName = $scope.astList[i].FundName + "(一般)";
								$scope.astList[i].assetTradeSubType = '6';
								$scope.astList[i].assetTradeSubTypeD = '6';
								break;
							case "0005":
								$scope.astList[i].FundName = $scope.astList[i].FundName + "(一般)";
								$scope.astList[i].assetTradeSubType = '7';
								$scope.astList[i].assetTradeSubTypeD = '7';
								break;
							}
						}

					}
				}
				return;
			}
		});
	};
	
	//動態鎖利
	$scope.queryDyna = function() {
		$scope.sendRecv("SOT703", "getCustAssetNFData", "com.systex.jbranch.app.server.fps.sot703.SOT703InputVO", {
			"custId" : $scope.custID,
			"trustTS" : $scope.trustTS,
			"debitAcct" : $scope.debitAcct,
			"returnNumZeroYN" : "Y" //母基金庫存為0仍要顯示
		}, function(tota, isError) {
			if (!isError) {
				if (tota[0].body.custAssetFundList) {
					$scope.tempList = tota[0].body.custAssetFundList;
					$scope.astList = [];
					$scope.outputVO = tota[0].body;

					for (var i = 0; i < $scope.tempList.length; i++) {
						//只取得動態鎖利的庫存
						if ($scope.tempList[i].AssetType && $scope.tempList[i].AssetType == "0001" && 
								($scope.tempList[i].Dynamic == '1' || $scope.tempList[i].Dynamic == '2')) {
							$scope.tempList[i].assetTradeSubType = '1';
							$scope.tempList[i].assetTradeSubTypeD = '1';
							if($scope.tempList[i].Dynamic == '1') {
								$scope.tempList[i].FundName = $scope.tempList[i].FundName + "(動態鎖利_母)";
							} else if($scope.tempList[i].Dynamic == '2') {
								$scope.tempList[i].FundName = $scope.tempList[i].FundName + "(動態鎖利_子)";
							}
							$scope.astList.push($scope.tempList[i]);
						}
					}
					//排序：憑證編號、母/子基金(母基金在前)
					$scope.astList.sort(function (a, b) {
						if(a.EviNum == b.EviNum) {
							return (a.Dynamic < b.Dynamic) ? -1 : 1;
						} else {
							return (a.EviNum < b.EviNum) ? -1 : 1;
						}
					});
				}
				return;
			}
		});
	};
	

	// #0687 金錢信託_基金贖回_後收型基金新判斷
	$scope.checkEviNumAndCloseDialog = function(row) {
		debugger;
		if (!($scope.trustTS == 'M')) {
			$scope.closeThisDialog(row);
			return;
		}
		$scope.sendRecv("SOT130", "getBackendConstraintParameter", "com.systex.jbranch.app.server.fps.sot130.SOT130InputVO", {
			'outProdID' : row.FundNO
		}, function(tota, isError) {
			if (!isError) {			
				if (!(tota[0].body.prodDTL[0].IS_BACKEND === "Y")) {
					debugger;
					$scope.closeThisDialog(row);
					return;
				}
				if (!(tota[0].body.prodDTL[0].FUSM10 === "Y")) {
					debugger;
					$scope.closeThisDialog(row);
					return;
				}
				debugger;
				var chosenFundNumberList = $scope.astList.filter(function(prod) {
					return prod.FundNO == row.FundNO;
				});
				chosenFundNumberList = chosenFundNumberList.sort();
				var cartChosenFundNumberFromList = $scope.cartList.filter(function(cart) {
					return cart.RDM_PROD_ID == row.FundNO && cart.CONTRACT_ID == $scope.contractID;
				});
				// console.log('chosenFundNumberList');
				// console.log(chosenFundNumberList);
				// console.log('cartChosenFundNumberFromList');
				// console.log(cartChosenFundNumberFromList);

				if (cartChosenFundNumberFromList.length < 1) {
					if (row.EviNum == chosenFundNumberList[0].EviNum) {
						debugger;
						$scope.closeThisDialog(row);
						return;
					}
					else {
						debugger;
						$scope.showErrorMsg("ehl_01_SOT130_001");
						return;
					}
				}
				if (cartChosenFundNumberFromList.length >= 1) {
					debugger;
					if (chosenFundNumberList.length == cartChosenFundNumberFromList.length) {
						$scope.closeThisDialog(row);
						return;
					}
					if (row.EviNum == chosenFundNumberList[cartChosenFundNumberFromList.length].EviNum) {
						$scope.closeThisDialog(row);
						return;
					} else {
						var i = 0;
						for (i = 0; i < cartChosenFundNumberFromList.length; i++) {
							if (row.EviNum == cartChosenFundNumberFromList[i].RDM_CERTIFICATE_ID) {
								$scope.closeThisDialog(row);
								return;
							}
						}
						debugger;
						$scope.showErrorMsg("ehl_01_SOT130_001");
						return;
					}
				}
			}
		});
	};

	//動態鎖利：回傳該憑證編號的母子基金資料
	$scope.getDynaData = function(row) {
		var rtnData = [];
		var cIndex = 1;
		
		for (var i = 0; i < $scope.astList.length; i++) {
			//相同憑證編號
			if($scope.astList[i].EviNum == row.EviNum) {
				if($scope.astList[i].Dynamic == "1") rtnData.prodM = $scope.astList[i]; //母基金
				if($scope.astList[i].Dynamic == "2") { //子基金(舊憑證最多5筆，新憑證最多3筆)
					if(cIndex == 1) rtnData.prodC1 = $scope.astList[i];
					if(cIndex == 2) rtnData.prodC2 = $scope.astList[i];
					if(cIndex == 3) rtnData.prodC3 = $scope.astList[i];
					if(cIndex == 4) rtnData.prodC4 = $scope.astList[i];
					if(cIndex == 5) rtnData.prodC5 = $scope.astList[i];
					cIndex++;
				}
			}
		}
		//回傳該憑證編號的母子基金資料
		$scope.closeThisDialog(rtnData); 
	}
	
	$scope.init();
	$scope.query();

});