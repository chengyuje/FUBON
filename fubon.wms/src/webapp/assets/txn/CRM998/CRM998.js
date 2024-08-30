/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM998Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = 'CRM998Controller';
	
	//判斷使用者是否具有權限，有權限者直接進入覆核頁面
	$scope.sendRecv('CRM998', 'getPriId', 'com.systex.jbranch.app.server.fps.crm998.CRM998InputVO', {}, function(tota, isError) {
		if (!isError) {
			if (tota[0].body.resultList.length) {
				var priArr = tota[0].body.resultList.map((e)=>e.PRIVILEGEID);
				if (priArr.indexOf(String(sysInfoService.getPriID())) > -1) {
					$scope.activeJustified = 1;
				} else {
					$scope.activeJustified = 0;
				}
			}
		} else {
			$scope.showMsg('ehl_01_common_024');
		}
	});
	
	$scope.choose = function(page) {
		if (page == '0') {	 //當點選申請頁時，執行初始
			$scope.doInit = !$scope.doInit;
		} else {             //當點選覆核頁時，執行查詢
			$scope.doQry = !$scope.doQry;
		}
	}
});