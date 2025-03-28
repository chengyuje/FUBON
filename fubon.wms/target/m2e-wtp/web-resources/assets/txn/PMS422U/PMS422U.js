/**================================================================================================
 @Description 分行人員與客戶資金往來異常報表
 =================================================================================================**/
'use strict';
eSoafApp.controller('PMS422UController', function ($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS422UController";
    
    // 繼承
	$controller('PMS422Controller', {$scope: $scope});
});
