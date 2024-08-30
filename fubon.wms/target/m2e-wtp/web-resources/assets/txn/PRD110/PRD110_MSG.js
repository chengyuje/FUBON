
'use strict';
eSoafApp.controller('PRD110_MSGController', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter, sysInfoService) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PRD110_MSGController";
	
	$scope.message = "";
	$scope.message2 = "";
	
	$scope.init = function() {
		var isFirstTrade = $scope.msgObj.isFirstTrade;
		var specialCust = $scope.msgObj.specialCust;
		var msg = "";
		var msg2 = "";
		var concatStr = "，申購前請記得錄音。";
		if (isFirstTrade == "Y" && specialCust == "Y") {
			msg += "客戶是首購客戶";
			msg2 += "客戶是特定客戶";
		} else if (isFirstTrade == "Y") {
			msg += "客戶是首購客戶";
		} else if (specialCust == "Y") {
			msg += "客戶是特定客戶";
		}

		if (msg.length > 0 && msg2.length > 0) {
			msg += concatStr;
			msg2 += concatStr;
		} else if (msg != "") {
			msg += concatStr;
		}
		
		$scope.message = msg;
		$scope.message2 = msg2;
	};
	
	$scope.init();

});