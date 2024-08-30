/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT620_ADDPRODController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT620_ADDPRODController";
        		
		//主畫面過來的資料
		$scope.inputVO.custID = $scope.custID;
		
		//參數
		getParameter.XML(["SOT.CENTRATE_PROD_TYPE", "FPS.CURRENCY"], function(totas) {
			if (totas) {
				//商品種類
			    $scope.mappingSet['SOT.CENTRATE_PROD_TYPE'] = totas.data[totas.key.indexOf('SOT.CENTRATE_PROD_TYPE')];
			    //幣別
			    $scope.mappingSet['FPS.CURRENCY'] = totas.data[totas.key.indexOf('FPS.CURRENCY')];
			}
		});
		
		$scope.init = function() {
			$scope.inputVO.prodType = "";					//商品類別
			$scope.inputVO.prodID = "";						//商品ID
			$scope.inputVO.prodName = '';					//商品名稱
        	$scope.inputVO.prodCurr = '';					//計價幣別
			$scope.inputVO.prodMinBuyAmt = undefined;		//最低申購面額
			$scope.inputVO.prodMinGrdAmt = undefined;		//累計申購面額
			$scope.inputVO.purchaseAmt = undefined;			//申購金額/面額
			$scope.inputVO.trustAmt = undefined;			//海外債信託本金原幣
			$scope.inputVO.trustAmtTWD = undefined;			//海外債信託本金台幣
		};
		$scope.init();
		
		$scope.prodClear = function() {
        	$scope.inputVO.prodName = '';					//商品名稱
        	$scope.inputVO.prodCurr = '';					//計價幣別
			$scope.inputVO.prodMinBuyAmt = undefined;		//最低申購面額
			$scope.inputVO.prodMinGrdAmt = undefined;		//累計申購面額
			$scope.inputVO.purchaseAmt = undefined;				//申購金額/面額	
			$scope.inputVO.trustAmt = undefined;			//海外債信託本金
			$scope.inputVO.trustAmtTWD = undefined;			//海外債信託本金台幣
        }
				
		//商品種類ON_CHANGE
        $scope.prodTypeOnChange = function() {
        	$scope.inputVO.prodID = "";
			$scope.inputVO.purchaseAmt = undefined;
        };
        		
        //取得商品資訊
        $scope.getProdData = function() {
        	if (!$scope.checkNull($scope.inputVO.prodType) && !$scope.checkNull($scope.inputVO.prodID)) {
        		//先檢核是否為高風險商品
        		$scope.sendRecv("SOT620", "checkHNWCBuy", "com.systex.jbranch.app.server.fps.sot620.SOT620InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							//商品適配及取得商品資訊
							if($scope.inputVO.prodType == "4") { //海外債
			        			$scope.getBondProdDTL();
			        		} else if($scope.inputVO.prodType == "2") { //SN
			        			$scope.getSNProdDTL();
			        		} else if($scope.inputVO.prodType == "1") { //SI
			        			$scope.getSIProdDTL();
			        		}  else if($scope.inputVO.prodType == "6") { //境外私募基金
			        			$scope.getOvePriProdDTL();
			        		}
						} else {
							$scope.inputVO.prodID = "";
						}
				});
			} else {
//				$scope.showErrorMsg("請輸入商品種類/商品代碼");
			}
        	
        }
        
        //取得海外債商品資訊
        $scope.getBondProdDTL = function() {
        	$scope.inputVO.prodID = $filter('uppercase')($scope.inputVO.prodID);
        	if($scope.inputVO.prodID) {
	        	$scope.sendRecv("SOT310", "getProdDTL", "com.systex.jbranch.app.server.fps.sot310.SOT310InputVO",{'custID':$scope.inputVO.custID,'prodID':$scope.inputVO.prodID},
						function(tota, isError) {
	        				$scope.prodClear();
							if (!isError) {
								if (tota[0].body.errorMsg != null && tota[0].body.errorMsg != "") {
									//適配有錯誤訊息
									$scope.showErrorMsg(tota[0].body.errorMsg);
									$scope.inputVO.prodID = "";
								} else if (tota[0].body.prodDTL != null && tota[0].body.prodDTL.length > 0) {
									$scope.inputVO.bondVal =  tota[0].body.bondVal;
						        	$scope.inputVO.prodName = tota[0].body.prodDTL[0].BOND_CNAME;						//商品名稱
						        	$scope.inputVO.prodCurr = tota[0].body.prodDTL[0].CURRENCY_STD_ID;					//計價幣別
									$scope.inputVO.prodRiskLV = tota[0].body.prodDTL[0].RISKCATE_ID;					//產品風險等級
									$scope.inputVO.prodMinBuyAmt = tota[0].body.prodDTL[0].BASE_AMT_OF_PURCHASE;		//最低申購面額
									$scope.inputVO.prodMinGrdAmt = tota[0].body.prodDTL[0].UNIT_OF_VALUE;				//累計申購面額
									$scope.inputVO.refVal = tota[0].body.prodDTL[0].BUY_PRICE;							//參考報價
									$scope.inputVO.refValDate = $scope.toJsDate(tota[0].body.prodDTL[0].BARGAIN_DATE);	//參考報價日期
									$scope.inputVO.limitedPrice = tota[0].body.prodDTL[0].LIMITED_PRICE;
									$scope.inputVO.buyRate = tota[0].body.buyRate;										//取得商品計價幣別最新買匯匯率
								} else {
									$scope.showErrorMsg("查無商品報價資料");
									$scope.inputVO.prodID = "";
								}
							} else {
								$scope.inputVO.prodID = "";
							}
	
				});
        	} else {
				$scope.prodClear();
			}
        }
        
        //取得SN商品資訊
		$scope.getSNProdDTL = function(){
			$scope.inputVO.prodID = $filter('uppercase')($scope.inputVO.prodID);
			
			if($scope.inputVO.prodID) {
				$scope.sendRecv("SOT510", "getProdDTL", "com.systex.jbranch.app.server.fps.sot510.SOT510InputVO", {"prodID":$scope.inputVO.prodID, "custID":$scope.inputVO.custID},
						function(tota, isError) {
							$scope.prodClear();
							if (!isError) {
								if (tota[0].body.errorMsg != null && tota[0].body.errorMsg != "") {
									//適配有錯誤訊息
									$scope.showErrorMsg(tota[0].body.errorMsg);
									$scope.inputVO.prodID = "";
								} else if(tota[0].body.prodRefVal && tota[0].body.prodRefVal.length > 0) {
									$scope.prodDTL=tota[0].body.prodRefVal[0];
									$scope.inputVO.prodName=$scope.prodDTL.BondName;
									$scope.inputVO.bondVal =tota[0].body.bondVal;
									$scope.inputVO.prodRiskLV=$scope.prodDTL.ProdRiskLv;
									$scope.inputVO.prodCurr=$scope.prodDTL.ProdCurr;
									$scope.inputVO.prodMinBuyAmt =$scope.prodDTL.ProdMinBuyAmt;
									$scope.inputVO.prodMinGrdAmt=$scope.prodDTL.ProdMinGrdAmt;
									$scope.inputVO.refVal = $scope.prodDTL.RefVal;	//參考報價
								} else {
									$scope.inputVO.prodID = '';
									$scope.showMsg("ehl_01_common_009");
								}
								return;
							} else {
								$scope.inputVO.prodID = '';
							}
				});
			} else {
				$scope.prodClear();
			}
		};
		
		//取得SI商品資訊
		$scope.getSIProdDTL = function () {
			$scope.inputVO.prodID = $filter('uppercase')($scope.inputVO.prodID);
			
			if($scope.inputVO.prodID) {
				$scope.sendRecv("SOT410", "getProdDTL", "com.systex.jbranch.app.server.fps.sot410.SOT410InputVO", $scope.inputVO,
						function(tota, isError) {
							$scope.prodClear();
							if (!isError) {
								if (tota[0].body.errorMsg != null && tota[0].body.errorMsg != "") {
									//適配有錯誤訊息
									$scope.showErrorMsg(tota[0].body.errorMsg);
									$scope.inputVO.prodID = "";
								} else if (tota[0].body.prodDTL && tota[0].body.prodDTL.length > 0) {
									$scope.inputVO.prodName = tota[0].body.prodDTL[0].SI_CNAME;							    //商品名稱
									$scope.inputVO.prodRiskLv = tota[0].body.prodDTL[0].RISKCATE_ID;						//產品風險等級
									$scope.inputVO.prodMinBuyAmt = tota[0].body.prodDTL[0].BASE_AMT_OF_PURCHASE;			//最低申購金額
									$scope.inputVO.prodMinGrdAmt = tota[0].body.prodDTL[0].UNIT_AMT_OF_PURCHASE;			//累進申購金額	
									$scope.inputVO.prodCurr = tota[0].body.prodDTL[0].CURRENCY_STD_ID;						//計價幣別
								}else{
									$scope.inputVO.prodID='';
									$scope.showMsg("ehl_01_common_009");
								}
							} else {  
								if (tota.body.msgData) {
									//$scope.showErrorMsg(tota.body.msgData);
								} else {	
									$scope.showErrorMsg("ehl_01_common_009");
								} 
								$scope.inputVO.prodID = "";
							}
				});
			} else {
				$scope.inputVO.prodID = "";
			}
		};
		
		//取得境外私募基金商品資訊
        $scope.getOvePriProdDTL = function() {
        	$scope.inputVO.prodID = $filter('uppercase')($scope.inputVO.prodID);
        	if($scope.inputVO.prodID) {
	        	$scope.sendRecv("SOT110", "getProdDTL", "com.systex.jbranch.app.server.fps.sot110.SOT110InputVO",{'custID':$scope.inputVO.custID,'prodId':$scope.inputVO.prodID},
						function(tota, isError) {
	        				$scope.prodClear();
							if (!isError) {
								if (tota[0].body.errorMsg != null && tota[0].body.errorMsg != "") {
									//適配有錯誤訊息
									$scope.showErrorMsg(tota[0].body.errorMsg);
									$scope.inputVO.prodID = "";
								} else if (tota[0].body.prodDTL != null && tota[0].body.prodDTL.length > 0) {
									$scope.inputVO.prodName = tota[0].body.prodDTL[0].FUND_CNAME;						//商品名稱
									$scope.inputVO.prodCurr = tota[0].body.prodDTL[0].CURRENCY_STD_ID;					//計價幣別
									$scope.inputVO.prodRiskLv = tota[0].body.prodDTL[0].RISKCATE_ID;
									$scope.inputVO.prodMinBuyAmt = tota[0].body.prodDTL[0].MIN_BUYAMT_OVSPRI;			//境外私募基金最低申購金額
									$scope.inputVO.prodMinGrdAmt = 1;
								} else {
									$scope.inputVO.prodID = "";
								}
							} else {
								$scope.inputVO.prodID = "";
							}
	
				});
        	} else {
				$scope.prodClear();
			}
        }
        
        //檢核申購金額/面額
		//申購面額須高於最低申購面額，且須為累進申購面額之倍數
        $scope.validPurchaseAmt = function () {
        	if($scope.inputVO.purchaseAmt == undefined) {
        		$scope.showErrorMsg("申購面額/金額需大於0");
        		return false;
        	}
        	
        	var roundPlace = $scope.inputVO.prodCurr == "TWD" ? 0 : 2;
        	$scope.inputVO.purchaseAmt = parseFloat($scope.inputVO.purchaseAmt).toFixed(roundPlace);
        	
        	if(RegExp('[1246]').test($scope.inputVO.prodType)) {
        		//SI/SN/海外債/境外私募基金
				if (($scope.inputVO.purchaseAmt >= $scope.inputVO.prodMinBuyAmt) && ($scope.inputVO.purchaseAmt % $scope.inputVO.prodMinGrdAmt == 0)) {
					$scope.inputVO.purchaseAmt = $scope.moneyUnFormat($scope.inputVO.purchaseAmt);
					
					if($scope.inputVO.prodType == "4") {
						//海外債：申購金額 = 申購面額 x 參考報價
						$scope.inputVO.trustAmt = $scope.inputVO.purchaseAmt * $scope.inputVO.refVal / 100;
					} else {
						//SI/SN/境外私募基金 申購金額
						$scope.inputVO.trustAmt = $scope.inputVO.purchaseAmt;
					}
					
					//取得折台金額
					$scope.sendRecv("SOT620", "getCurrencyRate", "com.systex.jbranch.app.server.fps.sot620.SOT620InputVO", {"currCode":$scope.inputVO.prodCurr},
						function(tota, isError) {
							if (!isError) {
								var currRate = tota[0].body.currencyRate;
								$scope.inputVO.trustAmtTWD = parseFloat($scope.inputVO.trustAmt * currRate).toFixed(0);
							}
					});
				} else {
					$scope.inputVO.purchaseAmt = undefined;
					$scope.inputVO.trustAmt = undefined;
					$scope.inputVO.trustAmtTWD = undefined;
	
					if($scope.inputVO.prodMinBuyAmt && $scope.inputVO.prodMinGrdAmt){
						if ($scope.inputVO.custID !='' && $scope.inputVO.trustAmt != undefined) {
							$scope.showErrorMsg("ehl_01_sot310_007");
							return false;
						} else if ($scope.inputVO.custID !='' && $scope.inputVO.purchaseAmt == undefined) {
							$scope.showErrorMsg("ehl_01_sot310_007");  //申購面額須高於最低申購面額，且須為累進申購面額之倍數。
							return false;
						}
					}
				}
				
				return true;
        	} else {
        		if($scope.checkNull($scope.inputVO.prodCurr)) {
        			$scope.showErrorMsg("請輸入計價幣別");
    				return false;
        		} else {
        			$scope.inputVO.trustAmt = $scope.inputVO.purchaseAmt;
        			//取得折台金額
					$scope.sendRecv("SOT620", "getCurrencyRate", "com.systex.jbranch.app.server.fps.sot620.SOT620InputVO", {"currCode":$scope.inputVO.prodCurr},
						function(tota, isError) {
							if (!isError) {
								debugger
								var currRate = tota[0].body.currencyRate;
								$scope.inputVO.trustAmtTWD = parseFloat($scope.inputVO.trustAmt * currRate).toFixed(0);
							}
					});
					return true;
        		}
        	}
        	
        }
        
        //回傳新增商品資訊
        $scope.add = function() {
        	if($scope.checkNull($scope.inputVO.prodType)) {
        		$scope.showErrorMsg("請輸入商品種類");
				return false;
        	} else {
        		if(RegExp('[1246]').test($scope.inputVO.prodType)) {
        			//SI/SN/海外債/境外私募基金
        			if($scope.checkNull($scope.inputVO.prodID) || $scope.checkNull($scope.inputVO.purchaseAmt)) {
        				$scope.showErrorMsg("請輸入商品代碼及申購金額/面額");
        				return false;
        			}
        		} else if($scope.checkNull($scope.inputVO.purchaseAmt)) {
    				$scope.showErrorMsg("請輸入申購金額/面額");
    				return false;
        		}
        	}
        	
        	if($scope.validPurchaseAmt()) {
        		$scope.closeThisDialog($scope.inputVO);
        	}
        }
        
        $scope.checkNull = function(data) {
			if (data == undefined || data == null || data.trim() == '') {
				return true;
			}
			return false;
		}
		
		
});