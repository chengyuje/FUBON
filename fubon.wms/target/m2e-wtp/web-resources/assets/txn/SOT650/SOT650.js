/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT650Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT650Controller";

		// combobox
		getParameter.XML(["PRD.FCI_CURRENCY", "SOT.FCI_INV_STATUS", "SOT.FCI_INV_STATUS_RM", "SOT.FCI_PM_PRIVILEGE_ID"], function(totas) {
			if (totas) {
				$scope.mappingSet['PRD.FCI_CURRENCY'] = totas.data[totas.key.indexOf('PRD.FCI_CURRENCY')];
				$scope.mappingSet['SOT.FCI_INV_STATUS'] = totas.data[totas.key.indexOf('SOT.FCI_INV_STATUS')];
				$scope.mappingSet['SOT.FCI_INV_STATUS_RM'] = totas.data[totas.key.indexOf('SOT.FCI_INV_STATUS_RM')];
				$scope.mappingSet['SOT.FCI_PM_PRIVILEGE_ID'] = totas.data[totas.key.indexOf('SOT.FCI_PM_PRIVILEGE_ID')];
				$scope.mappingSet['MON_PERIOD'] = [];
				for (var i = 1; i <= 12; i++) {
					$scope.mappingSet['MON_PERIOD'].push({LABEL: i+"個月", DATA: i});
				}
				debugger
				//權限ID//是否為FCI PM角色
				$scope.privilegeId = sysInfoService.getPriID();
				var findFCIPMRole = $filter('filter')($scope.mappingSet['SOT.FCI_PM_PRIVILEGE_ID'], {DATA: $scope.privilegeId[0]});
				$scope.inputVO.isFCIPMRole = (findFCIPMRole != null && findFCIPMRole != undefined && findFCIPMRole.length > 0) ? true : false;
				//理專角色，自動帶入理專員編
				if($scope.privilegeId == "002" || $scope.privilegeId == "003") {
					$scope.inputVO.EMP_ID = sysInfoService.getUserID();
				}
			}
		});		
        
		// time picker
		$scope.startDateOptions = {
			maxDate: $scope.inputVO.eDate || $scope.maxDate,
			minDate: new Date(new Date().getFullYear()-2, new Date().getMonth(), new Date().getDate())
		};
        $scope.endDateOptions = {
			maxDate: $scope.inputVO.eDate || $scope.maxDate,
			minDate: $scope.inputVO.sDate || $scope.minDate
		};
		$scope.altInputFormats = ['M!/d!/yyyy'];
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.startDateOptions.maxDate = $scope.inputVO.eDate || $scope.maxDate;
			
			$scope.endDateOptions.minDate = $scope.inputVO.sDate || $scope.minDate;
			$scope.endDateOptions.maxDate = $scope.inputVO.eDate;
		};
		
		//Initialization
		$scope.init = function() {
			$scope.inputVO = {};
		};
		$scope.init();
		
		$scope.inquireInit = function() {
			$scope.resultList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
				
		$scope.inquire = function(){
			// toUpperCase
			if($scope.inputVO.CUST_ID) $scope.inputVO.CUST_ID = $scope.inputVO.CUST_ID.toUpperCase();
			
			$scope.sendRecv("SOT650", "inquire", "com.systex.jbranch.app.server.fps.sot650.SOT650InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						debugger
						$scope.resultList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
					}
				});
		};	
		
		//匯出
		$scope.exportData = function(){
			$scope.sendRecv("SOT650","export","com.systex.jbranch.app.server.fps.sot650.SOT650InputVO", {'printList': $scope.resultList}, 
				function(tota, isError) {
					if (isError) {							
						return;            		
		           	}
				});			
		};
		
		$scope.reprintDoc = function(row) {
			if(row.REPRINT_YN == "Y") {
				//補印表單
				$scope.sendRecv("SOT610", "printReport", "com.systex.jbranch.app.server.fps.sot610.SOT610InputVO", {tradeSeq: row.TRADE_SEQ},
						function(tota, isError) {
				});
			}
		}
});