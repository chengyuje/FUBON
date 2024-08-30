'use strict';
eSoafApp.controller('CRM141Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CRM141Controller";
	
	$scope.inputVO.pri_id = '';
	$scope.inputVO.pri_id = String(sysInfoService.getPriID());
	$scope.inputVO.memLoginFlag = String(sysInfoService.getMemLoginFlag());
	
	//CRM141
	$scope.inquire = function() {
		$scope.sendRecv("CRM141", "inquire", "com.systex.jbranch.app.server.fps.crm141.CRM141InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsg(tota[0].body.msgData);
			}
			
			if (tota[0].body.resultList.length == 0) {
        		return;
			} else {
				$scope.resultList = tota[0].body.resultList;
			}
		});
	};
	
	$scope.inquire();
	
	//CRM352
	$scope.inquire2 = function() {
		$scope.sendRecv("CRM141", "inquire2", "com.systex.jbranch.app.server.fps.crm141.CRM141InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsg(totas[0].body.msgData);
			}
			
			if (tota[0].body.resultList2.length == 0) {
        		return;
			} else {
				$scope.resultList2 = tota[0].body.resultList2;
			}
		});
	};
		
	$scope.inquire2();
		
		
	$scope.detail = function() {
		switch ($scope.inputVO.pri_id) {
			case '001':
			case '002':
			case '003':
			case '004':
			case '004AO':
			case '005':
			case '007':
			case '008':
			case 'UHRM002':
				$rootScope.menuItemInfo.url = "assets/txn/CAM190/CAM190.html";
				break;
			case 'UHRM012':
			case 'UHRM013':
				$scope.connector('set', 'comeFromCrm141', 'D');
				$rootScope.menuItemInfo.url = "assets/txn/CAM211/CAM211.html"; 
				break;
			default :
				$scope.connector('set', 'comeFromCrm141', 'D');
				$rootScope.menuItemInfo.url = "assets/txn/CAM210/CAM210.html"; 
				break;
		}
	}
	
	$scope.detail_2 = function(row) {
		switch ($scope.inputVO.pri_id) {
			case '001':
			case '002':
			case '003':
			case '004':
			case '004AO':
			case '005':
			case '007':
			case '008':
			case 'UHRM002':
				$scope.connector('set', 'tab', 'tab5');
				$scope.connector('set', 'campName', row.CAMPAIGN_NAME);
				$rootScope.menuItemInfo.url = "assets/txn/CAM190/CAM190.html";
				break;
			case 'UHRM012':
			case 'UHRM013':
				$scope.connector('set', 'comeFromCrm141', 'Y');
				$scope.connector('set', 'CRM141_ROW', row);
				$rootScope.menuItemInfo.url = "assets/txn/CAM211/CAM211.html";
				break;
			default :
				$scope.connector('set', 'comeFromCrm141', 'Y');
				$scope.connector('set', 'CRM141_ROW', row);
				$rootScope.menuItemInfo.url = "assets/txn/CAM210/CAM210.html";
				break;
		}
	}
		
});
		