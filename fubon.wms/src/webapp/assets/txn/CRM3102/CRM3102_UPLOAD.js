/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM3102_UPLOADController',
	function($scope, $controller) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM3102_UPLOADController";
		
//		$scope.inputVO = {};
//		$scope.inputVO.PRJ_ID = $scope.PRJ_ID;
		
		// date picker
		$scope.sDateOptions = {};
        $scope.eDateOptions = {};
        $scope.exeDateOptions = {};
	    //config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.sDateOptions.maxDate = $scope.inputVO.edate;
			$scope.eDateOptions.minDate = $scope.inputVO.sdate;
			$scope.exeDateOptions.minDate = $scope.inputVO.edate;
//			$scope.staDateOptions.minDate = $scope.inputVO.edate;
		};
		
		$scope.inputVO = {};
		$scope.inputVO.PRJ_TYPE = '1';	// 專案類型預設為『必輪調專案』
		$scope.inputVO.PRJ_ID = ($scope.PRJ_ID == undefined || $scope.PRJ_ID == null || $scope.PRJ_ID == '') ? "" : $scope.PRJ_ID;
		
		$scope.uploadFinshed = function(name, rname) {
			$scope.inputVO.fileName = name;
        	$scope.inputVO.realfileName = rname;
		};
        
		//取得專案資料
		$scope.getPrjData = function() {
			debugger;
			if($scope.inputVO.PRJ_ID == "") 
				return;
				
			$scope.sendRecv("CRM3102", "getPrjData", "com.systex.jbranch.app.server.fps.crm3102.CRM3102InputVO", {PRJ_ID: $scope.inputVO.PRJ_ID},
				function(tota, isError) {
					if (!isError) {
						debugger
						$scope.inputVO = tota[0].body.PRJList[0];
						$scope.inputVO.sdate = $scope.toJsDate($scope.inputVO.PRJ_DATE_BGN);
						$scope.inputVO.edate = $scope.toJsDate($scope.inputVO.PRJ_DATE_END);
						$scope.inputVO.PRJ_EXE_DATE = $scope.toJsDate($scope.inputVO.PRJ_EXE_DATE);
						$scope.inputVO.STATEMENT_DATE = $scope.toJsDate($scope.inputVO.STATEMENT_DATE);
					}
			});
		}
		$scope.getPrjData();
		
		
		$scope.editPrj = function() {
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			//加強管控需檢核：對帳單基準日以及加強管控類型
			if($scope.inputVO.PRJ_TYPE == '2' && 
					(($scope.inputVO.STATEMENT_DATE == undefined || $scope.inputVO.STATEMENT_DATE == null || $scope.inputVO.STATEMENT_DATE == '') ||
					 ($scope.inputVO.PRJ_SUB_TYPE == undefined || $scope.inputVO.PRJ_SUB_TYPE == null || $scope.inputVO.PRJ_SUB_TYPE == ''))) {
				$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
			}

			$scope.sendRecv("CRM3102", "editPrj", "com.systex.jbranch.app.server.fps.crm3102.CRM3102InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.showSuccessMsg('ehl_01_common_004');
                    		$scope.closeThisDialog('successful');
                    		
                    		if (tota[0].body.error == 'ERR1') {
								$scope.showErrorMsg("理專:" + tota[0].body.resultList + ", 有重複資料");
                    		}
						}
			});
		};
		
		$scope.downloadSample = function() {
        	$scope.sendRecv("CRM3102", "downloadSample", "com.systex.jbranch.app.server.fps.crm3102.CRM3102InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		};
		
});