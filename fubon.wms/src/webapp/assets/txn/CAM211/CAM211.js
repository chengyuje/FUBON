/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM211Controller', function($scope, $rootScope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter,sysInfoService) {
	$controller('BaseController', {$scope: $scope});
	
	$scope.controllerName = "CAM211Controller";

	// 繼承
	$controller('CAM210Controller', {$scope: $scope});
	
	$scope.initCAM211 = function() {
//		$scope.inputVO = {};
		var min_mon = new Date();
		min_mon.setMonth(min_mon.getMonth() - 2, 1);
		min_mon.setHours(0, 0, 0, 0);
		$scope.inputVO.camp_sDate = min_mon;
		$scope.limitDate();
		
		$scope.getUHRMList();
		
        //登入者權限
		$scope.loginRole = function (){
			$scope.sendRecv("CRM131", "login", "com.systex.jbranch.app.server.fps.crm131.CRM131InputVO", {},
			function(tota, isError) {
				if (!isError) {
					if(tota[0].body.privilege.length == 0) {
            			return;
            		}else{
						$scope.privilege = tota[0].body.privilege;
						$scope.inputVO.mroleid = $scope.privilege[0].COUNTS;
            		}
				}
			})
		};
		
		$scope.loginRole();
		
		$scope.fromOTHER();
		
        $scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'CAM211'}, function(tota, isError) {
			if (!isError) {
				$scope.chkMaintenance = tota[0].body.isMaintenancePRI == 'Y' ? true : false;

				$scope.uhrmRCList = [];
				$scope.uhrmOPList = [];

				if (null != tota[0].body.uhrmORGList) {
					angular.forEach(tota[0].body.uhrmORGList, function(row) {
						$scope.uhrmRCList.push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});
					});	
					
					$scope.inputVO.uhrmRC = tota[0].body.uhrmORGList[0].REGION_CENTER_ID;
					
					angular.forEach(tota[0].body.uhrmORGList, function(row) {
						$scope.uhrmOPList.push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
					});
					
					$scope.inputVO.uhrmOP = tota[0].body.uhrmORGList[0].BRANCH_AREA_ID;
		        }
			}						
		});
	};
	$scope.initCAM211();
	
	// 20170524 ADD BY OCEAN
	$scope.downloadCAM211 = function() {
		if ($scope.tabSheet == '3' || $scope.tabSheet == '4') {
			if (!$scope.inputVO.campaignName) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
		}
		
		var temp = $scope.inputVO;
		var dialog = ngDialog.open({
			template: 'assets/txn/CAM211/CAM211_DOWNLOAD.html',
			className: 'CAM211_DOWNLOAD',
			showClose: false,
            controller: ['$scope', function($scope) {
            	$scope.inputVO = temp;
            }]
		});
	};
	
});