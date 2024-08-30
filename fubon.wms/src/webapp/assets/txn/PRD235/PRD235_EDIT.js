/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD235_EDITController',
	function($scope, $controller) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD235_EDITController";
		
//		$scope.inputVO = {};
//		$scope.inputVO.PRJ_ID = $scope.PRJ_ID;
		
		// date picker
		$scope.sDateOptions = {};
        $scope.eDateOptions = {};
        $scope.deadlineOptions = {};
        $scope.tradeDateOptions = {};
	    //config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.sDateOptions.maxDate = $scope.inputVO.END_DATE;
			$scope.eDateOptions.minDate = $scope.inputVO.START_DATE;
			$scope.deadlineOptions.minDate = new Date($scope.inputVO.END_DATE.getFullYear(), $scope.inputVO.END_DATE.getMonth(), $scope.inputVO.END_DATE.getDate() + 1);
			$scope.tradeDateOptions.minDate = new Date($scope.inputVO.END_DATE.getFullYear(), $scope.inputVO.END_DATE.getMonth(), $scope.inputVO.END_DATE.getDate() + 1);
		};
		$scope.limitEndDate = function() {
			$scope.inputVO.DEADLINE_DATE = null;
			$scope.inputVO.TRADE_DATE = null;
			$scope.limitDate();
		};
		
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.inputVO = $scope.row;
			if($scope.inputVO.PRD_ID == undefined || $scope.inputVO.PRD_ID == null) {
				$scope.inputVO.PRD_ID = "";
			}
		}
		$scope.init();		
		        
		//取得商品資料
		$scope.getPrdData = function() {
			if($scope.inputVO.PRD_ID == "") 
				return;
			
			$scope.inputVO.PRD_ID = $scope.inputVO.PRD_ID.toUpperCase();
			
			$scope.sendRecv("PRD235", "inquire", "com.systex.jbranch.app.server.fps.prd235.PRD235InputVO", {SEQ_NO: $scope.inputVO.SEQ_NO},
				function(tota, isError) {
					if (!isError) {
						$scope.inputVO = tota[0].body.resultList[0];
						$scope.inputVO.START_DATE = $scope.toJsDate($scope.inputVO.START_DATE);
						$scope.inputVO.END_DATE = $scope.toJsDate($scope.inputVO.END_DATE);
						$scope.inputVO.DEADLINE_DATE = $scope.toJsDate($scope.inputVO.DEADLINE_DATE);
						$scope.inputVO.TRADE_DATE = $scope.toJsDate($scope.inputVO.TRADE_DATE);
					}
			});
		}
		$scope.getPrdData();		
		
		$scope.save = function() {
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			
			$scope.inputVO.saveType = "1";
			$scope.sendRecv("PRD235", "save", "com.systex.jbranch.app.server.fps.prd235.PRD235InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg('ehl_01_common_004');
                   		$scope.closeThisDialog('successful');
					}
			});
		};
		
		$scope.getPrdName = function() {
			if($scope.inputVO.PRD_ID == "") 
				return;
			
        	$scope.sendRecv("PRD235", "getPrdName", "com.systex.jbranch.app.server.fps.prd235.PRD235InputVO", {PRD_ID: $scope.inputVO.PRD_ID},
					function(tota, isError) {
						if (!isError) {
							$scope.inputVO.PRD_NAME = tota[0].body.prdName;
							$scope.inputVO.MIN_PURCHASE_AMT = tota[0].body.MIN_PURCHASE_AMT;
						} else {
							$scope.inputVO.PRD_ID = "";
						}
			});
		};
		
		$scope.chkPrdYear = function() {
			if($scope.inputVO.PRD_YEAR != undefined || $scope.inputVO.PRD_YEAR != null || $scope.inputVO.PRD_YEAR != "") {
				var dDate = new Date();
				if($scope.inputVO.PRD_YEAR < dDate.getFullYear()) {
					$scope.inputVO.PRD_YEAR = "";
					$scope.showErrorMsg('年份需大於等於系統年');
				}
			}
		}
});