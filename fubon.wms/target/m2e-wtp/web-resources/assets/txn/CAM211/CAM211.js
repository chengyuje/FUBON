/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM211Controller', function($scope, $rootScope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter,sysInfoService) {
	$controller('BaseController', {$scope: $scope});
	
	$scope.controllerName = "CAM211Controller";
	
	$scope.UHRM_REGION_CENTER_LIST = [{'LABEL':'個人高端客群處', 'DATA': 'TEMP_RC'}];
	$scope.UHRM_BRANCH_AREA_LIST = [{'LABEL':'台灣業務中心', 'DATA': 'TEMP_BA'}];

	/*
	 * 取得UHRM人員清單(由員工檔+角色檔)
	 */
	$scope.getUHRMList = function() {
		$scope.sendRecv("ORG260", "getUHRMList", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, 
				function(tota, isError) {
					if (isError) {
						return;
					}
					if (tota.length > 0) {
						$scope.mappingSet['UHRM_LIST'] = tota[0].body.uhrmList;
						$scope.inputVO.pCode = tota[0].body.uEmpID;
					}
		});
	};
	
	// 繼承
	$controller('CAM210Controller', {$scope: $scope});
	
	$scope.initCAM211 = function() {
		$scope.inputVO = {};
		var min_mon = new Date();
		min_mon.setMonth(min_mon.getMonth() - 2, 1);
		min_mon.setHours(0, 0, 0, 0);
		$scope.inputVO.camp_sDate = min_mon;
		$scope.limitDate();
		
		$scope.getUHRMList();
		$scope.inputVO.uhrmRegion = 'TEMP_RC';
		$scope.inputVO.uhrmOp = 'TEMP_BA';
		
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
	};
	$scope.initCAM211();
	
	// 20170524 ADD BY OCEAN
	$scope.downloadCAM211 = function() {
		console.log($scope.tabSheet);
		if($scope.tabSheet == '3' || $scope.tabSheet == '4') {
			if(!$scope.inputVO.campaignName) {
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