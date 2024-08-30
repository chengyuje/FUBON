/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT420Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q, validateService, sotService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT420Controller";

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
			$scope.inputVO.prodType='4';  //4：SI
        	$scope.inputVO.tradeType='2'; //2：贖回
        	
			$scope.data = [];
			$scope.custAssetSIList = [];
			
			$scope.inputVO = {
					custId: '', 
					startDate: undefined, 
					endDate: undefined, 
					prodId: '', 
					prodName: '', 
					prodType: '2'
        	};
			
			var custID = $scope.connector('get','ORG110_custID');
			if(custID != undefined){
				$scope.inputVO.custId = custID;
			}	
			
			$scope.limitDate();
		};
        $scope.init();
         
        
      //SI產品庫存資料查詢
        $scope.query = function () {
        	if($scope.inputVO.custId == '' || $scope.inputVO.custId == undefined) {
	    		$scope.showErrorMsg("ehl_01_common_022");
	    		return;
	    	}
        	 $scope.inputVO.custId = $filter('uppercase')($scope.inputVO.custId);
			 $scope.inputVO.prodId = $filter('uppercase')($scope.inputVO.prodId);
			 $scope.inputVO.prodName = $filter('uppercase')($scope.inputVO.prodName);
        	$scope.data = [];
        	$scope.custAssetSIList = [];
        	$scope.inputVO.custId = $filter('uppercase')($scope.inputVO.custId);
        	var validCustID = validateService.checkCustID($scope.inputVO.custId); //自然人和法人檢查
			//			console.log("custID:" + $scope.inputVO.custId + ", checkCustID: "+validCustID);
			if(validCustID==false) {
				$scope.inputVO.custId='';
				return;
			}
        	var SOT708InputVO = {
        		custId:$scope.inputVO.custId,
        		prodId:$scope.inputVO.prodId,
        		prodName:$scope.inputVO.prodName,
        		startDate:$scope.inputVO.startDate,
        		endDate:$scope.inputVO.endDate
     		};
        	$scope.sendRecv("SOT708", "getCustAssetSIData", "com.systex.jbranch.app.server.fps.sot708.SOT708InputVO", SOT708InputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.custAssetSIList.length == 0){
								$scope.showErrorMsg("ehl_01_common_009");
								return;
							}
							if (tota[0] && tota[0].body.custAssetSIList) {
								$scope.custId=$scope.inputVO.custId;
								$scope.custAssetSIList = tota[0].body.custAssetSIList; 
							}else { 
								$scope.showErrorMsg("ehl_01_common_009");
							}
						}
			});
        };
        
        $scope.next = function (row) {
        	$scope.connector('set','SOT421_prodDTL', row);
        	$scope.connector('set','SOT421_custID', $scope.custId);
        	$scope.connector('set','SOTTradeSEQ', null);
			$scope.connector('set','SOTCarSEQ', null);
			if ($scope.fromFPS) {
				// from FPS_SOT.js
				$scope.setSOTurl('assets/txn/SOT421/SOT421.html');
			} else {
				$rootScope.menuItemInfo.url = "assets/txn/SOT421/SOT421.html";
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