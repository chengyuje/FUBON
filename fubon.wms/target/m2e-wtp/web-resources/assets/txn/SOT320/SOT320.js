/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT320Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q,validateService, sotService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT320Controller";

		// filter
		getParameter.XML(["SOT.BOND_STORAGE_STATUS"], function(totas) {
			if (totas) {
				$scope.mappingSet['SOT.BOND_STORAGE_STATUS'] = totas.data[totas.key.indexOf('SOT.BOND_STORAGE_STATUS')];
			}
		});
        //
		
		$scope.startDateOptions = {
			maxDate: $scope.inputVO.endDate || $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.endDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.startDate || $scope.minDate
		};
		
		$scope.altInputFormats = ['M!/d!/yyyy'];
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		$scope.limitDate = function() {
			$scope.startDateOptions.maxDate = $scope.inputVO.endDate || $scope.maxDate;
			$scope.endDateOptions.minDate = $scope.inputVO.startDate || $scope.minDate;
		};
		
		$scope.init = function(){
			$scope.data = [];
			$scope.custAssetBondList = [];
			$scope.outputVO = [];
			$scope.mappingSet['SOT.DEBIT_ACCT_LIST'] = [];
			$scope.inputVO = {
					custId: '', 
					startDate: undefined, 
					endDate: undefined, 
					prodId: '', 
					prodName: '', 
					prodType: '2',
					trustTS: 'S'
        	};
			
			var custID = $scope.connector('get','ORG110_custID');
			if(custID != undefined){
				$scope.inputVO.custId = custID;
			}	
			
			$scope.limitDate();
		};
        $scope.init();
        
        $scope.query = function () {
        	if($scope.inputVO.custId == '' || $scope.inputVO.custId == undefined) {
	    		$scope.showErrorMsg("ehl_01_common_022");
	    		return;
	    	}
        	$scope.custAssetBondList = [];
        	$scope.data = [];
        	$scope.outputVO = [];
        	
        	$scope.inputVO.custId = $filter('uppercase')($scope.inputVO.custId);
			$scope.inputVO.prodId = $filter('uppercase')($scope.inputVO.prodId);
			$scope.inputVO.prodName = $filter('uppercase')($scope.inputVO.prodName);
        	var validCustID = validateService.checkCustID($scope.inputVO.custId); //自然人和法人檢查
			//			console.log("custID:" + $scope.inputVO.custId + ", checkCustID: "+validCustID);
			if(validCustID == false) {
				$scope.inputVO.custId = '';
				return;
			}
        	//報價日期
        	var refPriceDate = null;
        	debugger;
			if ($scope.inputVO.trustTS == 'S') { 		// 特金贖回
				$scope.sendRecv("SOT707", "getCustAssetBondData", "com.systex.jbranch.app.server.fps.sot707.SOT707InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.custAssetBondList.length > 0) {
									$scope.custId = $scope.inputVO.custId;
									$scope.custAssetBondList = tota[0].body.custAssetBondList;
									$scope.outputVO = tota[0].body;
									refPriceDate = tota[0].body.custAssetBondList[0].refPriceDate
								} else {
									$scope.outputVO = [];
									$scope.showErrorMsg("ehl_01_common_009");
								}
							}
				});
			} else if ($scope.inputVO.trustTS == 'M') {	// 金錢信託贖回
				$scope.sendRecv("SOT707", "getCustAssetBondData_TRUST", "com.systex.jbranch.app.server.fps.sot707.SOT707InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.custAssetBondList.length > 0) {
									$scope.custId = $scope.inputVO.custId;
									$scope.custAssetBondList = tota[0].body.custAssetBondList;
									$scope.outputVO = tota[0].body;
									refPriceDate = tota[0].body.custAssetBondList[0].refPriceDate
								} else {
									$scope.outputVO = [];
									$scope.showErrorMsg("ehl_01_common_009");
								}
							}
				});
			}
	
			$scope.sendRecv("SOT320", "getSOTCustInfo", "com.systex.jbranch.app.server.fps.sot320.SOT320InputVO", {'custID': $scope.inputVO.custId, 'prodType': 3, 'tradeType': 2},
					function(tota, isError) {
						if (!isError) {
							$scope.mappingSet['SOT.DEBIT_ACCT_LIST'] = tota[0].body.debitAcct;
						}
			});
        	
        };
        
        $scope.next = function (row) {
			//#0695 排除數存戶
        	if($scope.inputVO.trustTS != 'M' && sotService.is168(row.TrustAcct.trim())
        			&& sotService.isDigitAcct(row.TrustAcct,$scope.mappingSet['SOT.DEBIT_ACCT_LIST']) ){
				$scope.showErrorMsg("ehl_02_SOT_996");
				return;
        	}
        	
        	$scope.connector('set', 'SOT321_prodDTL', row);
        	$scope.connector('set', 'SOT321_custID', $scope.custId);
        	$scope.connector('set', 'SOTTradeSEQ', null);
			$scope.connector('set', 'SOTCarSEQ', null);
			$scope.connector('set', 'SOTContractID', $scope.inputVO.contractID);
			$scope.connector('set', 'SOTDebitAcct', $scope.inputVO.debitAcct);
			$scope.connector('set', 'SOTTrustPeopNum', $scope.inputVO.trustPeopNum);
			$scope.connector('set', 'SOT321GUARDIANSHIP_FLAG', $scope.inputVO.GUARDIANSHIP_FLAG);

			if (row.trustTS == 'S') {
				if ($scope.fromFPS) {
					// from FPS_SOT.js
					$scope.setSOTurl('assets/txn/SOT321/SOT321.html');
				} else {
					$rootScope.menuItemInfo.url = "assets/txn/SOT321/SOT321.html";
				}	
			} else {
				if ($scope.fromFPS) {
					// from FPS_SOT.js
					$scope.setSOTurl('assets/txn/SOT326/SOT326.html');
				} else {
					$rootScope.menuItemInfo.url = "assets/txn/SOT326/SOT326.html";
				}	
			}
					
        }
        
        //從其他地方進入下單
        $scope.FromElse = function(){
    		if($scope.connector('get','SOTCustID')){
    			$scope.inputVO.custId=$scope.connector('get','SOTCustID');
    			$scope.connector('set','SOTCustID',null);
    			$scope.query();
    		} else if ($scope.fromFPS){
				console.log($scope.FPSData);
				$scope.inputVO.custId = $scope.FPSData.custID;//客戶ID
//				$scope.inputVO.prodId = $scope.FPSData.prdID; //商品代號	 
				$scope.query();
			}

        }
       
        $scope.FromElse();
        
});