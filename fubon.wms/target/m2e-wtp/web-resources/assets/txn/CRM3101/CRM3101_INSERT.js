'use strict';
eSoafApp.controller('CRM3101_INSERTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM3101_INSERTController";
		
		// date picker
		$scope.sDateOptions = {};
        $scope.eDateOptions = {};
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
		};
		
		/**==============================================初始化========================================================**/
		$scope.init = function() {
			$scope.inputVO = {};
		};
		$scope.init();
		
		//下拉式選單：所有專案名稱
		$scope.sendRecv("CRM3101", "getAllPRJ", "com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO", {},
			function(tota, isError) {
			   if(!isError){
				   	$scope.PROJNAME = [];
                	angular.forEach(tota[0].body.allPRJ, function(row) {
                		$scope.PROJNAME.push({LABEL: row.PRJ_NAME, DATA: row.PRJ_ID});
                	});
			   }
		});
		
		//既有專案
		$scope.PRJDTL = function() {
			if($scope.inputVO.PRJ_ID) {
				$scope.sendRecv("CRM3101", "getPRJMast", "com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO", $scope.inputVO,
						function(tota, isError) {
						   if(!isError){
							   	var tempList = tota[0].body.PRJList[0];
			                	$scope.inputVO.PRJ_ID = tempList.PRJ_ID;
			                	$scope.inputVO.PRJ_NAME = tempList.PRJ_NAME;
			                	$scope.inputVO.PRJ_NOTE = tempList.PRJ_NOTE;
		                		$scope.inputVO.sdate = $scope.toJsDate(tempList.PRJ_DATE_BGN);
		                		$scope.inputVO.edate = $scope.toJsDate(tempList.PRJ_DATE_END);
		                		$scope.inputVO.DESC_01 = tempList.DESC_01;
		                		$scope.inputVO.DESC_02 = tempList.DESC_02;
		                		$scope.inputVO.DESC_03 = tempList.DESC_03;
		                		$scope.inputVO.DESC_04 = tempList.DESC_04;
		                		$scope.inputVO.DESC_05 = tempList.DESC_05;
		                		$scope.inputVO.DESC_06 = tempList.DESC_06;
		                		$scope.inputVO.DESC_07 = tempList.DESC_07;
		                		$scope.inputVO.DESC_08 = tempList.DESC_08;
		                		$scope.inputVO.DESC_09 = tempList.DESC_09;
		                		$scope.inputVO.DESC_10 = tempList.DESC_10;
		                		$scope.inputVO.DESC_11 = tempList.DESC_11;
		                		$scope.inputVO.DESC_12 = tempList.DESC_12;
		                		$scope.inputVO.DESC_13 = tempList.DESC_13;
		                		$scope.inputVO.DESC_14 = tempList.DESC_14;
		                		$scope.inputVO.DESC_15 = tempList.DESC_15;
						   }
				});
			}
		};
		
		//新增
		$scope.add = function() {
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			$scope.sendRecv("CRM3101", "edit", "com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg('ehl_01_common_004');
						$scope.closeThisDialog('successful');
					}
			});
		};
			
			
});