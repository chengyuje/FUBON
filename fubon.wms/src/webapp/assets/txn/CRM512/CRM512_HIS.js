
'use strict';
eSoafApp.controller('CRM512_HISController', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CRM512_HISController";
	
    $scope.inputVO.custID = $scope.custID;
    $scope.inputVO.examVersion = $scope.row.EXAM_VERSION;
    $scope.inputVO.questionVersion = $scope.row.QUESTION_VERSION;

    $scope.sendRecv("CRM512", "getCustHisByQus", "com.systex.jbranch.app.server.fps.crm512.CRM512InputVO", $scope.inputVO, function(tota, isError) {
		if (!isError) {
			$scope.custHisByQusList = tota[0].body.custHisByQusList;
			$scope.outputVO = tota[0].body;
			return;
		}
	});
});
