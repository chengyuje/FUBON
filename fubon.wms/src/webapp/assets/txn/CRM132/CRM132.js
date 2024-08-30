'use strict';
eSoafApp.controller('CRM132Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService) {
	
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CRM132Controller";
	
	$scope.campNameTemp = $scope.connector('get', 'campName');
	$scope.leadTypeTemp = $scope.connector('get', 'leadType');
	$scope.connector('set', 'campName', '');
	$scope.connector('set', 'leadType', '');
	
	$scope.init = function () {
		$scope.inputVO = {
			mroleid     : '',
			pri_id      : String(sysInfoService.getPriID()), 
			memLoginFlag: String(sysInfoService.getMemLoginFlag())
		}
		
		$scope.roleId = sysInfoService.getRoleID();
	};
	
	$scope.init();

	$scope.loginRole = function (){
		$scope.sendRecv("CRM131", "login", "com.systex.jbranch.app.server.fps.crm131.CRM131InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if (tota[0].body.privilege.length == 0) {
        			return;
        		} else {
					$scope.privilege = tota[0].body.privilege;
					$scope.inputVO.mroleid = $scope.privilege[0].COUNTS;
					console.log('$scope.inputVO.mroleid:'+$scope.inputVO.mroleid);
					$scope.data();
        		}
			}
		})
	};
	
	$scope.loginRole();
	
	$scope.data = function () { 
		$scope.sendRecv("CRM132", "query", "com.systex.jbranch.app.server.fps.crm132.CRM132InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if (tota[0].body.resultList.length == 0) {
					return;
        		} else {
        			$scope.resultList = tota[0].body.resultList;
        		}
			}
		});
	};

	
	$scope.detail = function(row) {
		if ($scope.inputVO.pri_id == '001' ||
			$scope.inputVO.pri_id == '002' ||
		    $scope.inputVO.pri_id == '003' || 
		    $scope.inputVO.pri_id == '004' || 
		    $scope.inputVO.pri_id == '004AO' || 
		    $scope.inputVO.pri_id == '005' || 
		    $scope.inputVO.pri_id == '007' || 
		    $scope.inputVO.pri_id == '008' || 
		    $scope.inputVO.pri_id == 'UHRM002') {
			$scope.connector('set', 'tab', 'tab5');
			$scope.connector('set', 'campName', row.CAMPAIGN_NAME);
			$rootScope.menuItemInfo.url = "assets/txn/CAM190/CAM190.html";
		} else {
			$scope.connector('set','comeFromCrm132','Y');
			$scope.connector('set','CRM132_ROW', row);
			$rootScope.menuItemInfo.url = "assets/txn/CAM210/CAM210.html";
		} 
	};
});