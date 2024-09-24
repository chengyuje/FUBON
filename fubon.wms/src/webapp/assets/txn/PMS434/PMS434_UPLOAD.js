
'use strict';
eSoafApp.controller('PMS434_UPLOADController', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter, sysInfoService) {
	$controller('BaseController', { $scope: $scope });
	$scope.controllerName = "PMS434_UPLOADController";

	$scope.inputVO = {
		fileName: "", fileRealName: "", compareType: "1"
	};

	$scope.uploadFinshed = function(name, rname) {
		$scope.inputVO.fileName = name;
		$scope.inputVO.fileRealName = rname;
	};

	$scope.downloadSimple = function() {
		$scope.sendRecv("PMS434", "downloadSimple", "com.systex.jbranch.app.server.fps.pms434.PMS434InputVO", {compareType: $scope.inputVO.compareType},
			function(tota, isError) {
				if (!isError) {
					return;
				}
			});
	};

	$scope.save = function() {
		$scope.sendRecv("PMS434", "upload", "com.systex.jbranch.app.server.fps.pms434.PMS434InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					var obj = { resultList: tota[0].body.resultList, compareType: $scope.inputVO.compareType };
					$scope.closeThisDialog(obj);
				}
			});
	};
});