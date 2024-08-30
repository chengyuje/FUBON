/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT640Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT640Controller";

		// combobox
		$scope.getParameter = function() {
			getParameter.XML(["PRD.FCI_CURRENCY", "SOT.FCI_INV_STATUS", "SOT.FCI_INV_STATUS_TODAY", "SOT.FCI_PM_PRIVILEGE_ID", "FUBONSYS.FC_ROLE"], function(totas) {
				if (totas) {
					$scope.mappingSet['PRD.FCI_CURRENCY'] = totas.data[totas.key.indexOf('PRD.FCI_CURRENCY')];
					$scope.mappingSet['SOT.FCI_INV_STATUS'] = totas.data[totas.key.indexOf('SOT.FCI_INV_STATUS')];
					$scope.mappingSet['SOT.FCI_INV_STATUS_TODAY'] = totas.data[totas.key.indexOf('SOT.FCI_INV_STATUS_TODAY')];
					$scope.mappingSet['SOT.FCI_PM_PRIVILEGE_ID'] = totas.data[totas.key.indexOf('SOT.FCI_PM_PRIVILEGE_ID')];
					
					$scope.mappingSet['MON_PERIOD'] = [];
					for (var i = 1; i <= 12; i++) {
						$scope.mappingSet['MON_PERIOD'].push({LABEL: i+"個月", DATA: i});
					}
					
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
		}
        
		//Initialization
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.getParameter();
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
			
			$scope.sendRecv("SOT640", "inquire", "com.systex.jbranch.app.server.fps.sot640.SOT640InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							debugger
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
						}
				});
		};	
		
		$scope.goPage = function(row) {
			$scope.connector('set',"SOTTradeSEQ", row.TRADE_SEQ);
			$scope.connector('set',"SOTCarSEQ", null);
			if(row.CONT_YN == "Y") {
				debugger
				//暫存、風控檢核中，回到編輯頁面
				if(row.TRADE_STATUS == "1") { //暫存
					$rootScope.menuItemInfo.url = "assets/txn/SOT413/SOT413.html";
				} else if(row.TRADE_STATUS == "2") { //風控檢核中，
					$rootScope.menuItemInfo.url = "assets/txn/SOT414/SOT414.html";
				}
				
			} else if(row.REPRINT_YN == "Y") {
				//補印表單
				$scope.sendRecv("SOT610", "printReport", "com.systex.jbranch.app.server.fps.sot610.SOT610InputVO", {tradeSeq: row.TRADE_SEQ},
						function(tota, isError) {
				});
			}
		}
		
		//匯出
		$scope.exportData = function(){
			$scope.sendRecv("SOT640","export","com.systex.jbranch.app.server.fps.sot640.SOT640InputVO", {'printList': $scope.resultList}, 
				function(tota, isError) {
					if (isError) {							
						return;            		
		           	}
				});			
		};
		
});