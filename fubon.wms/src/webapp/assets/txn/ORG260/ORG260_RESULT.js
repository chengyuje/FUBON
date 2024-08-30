/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG260_RESULTController', function($scope, $controller, $confirm, $compile, socketService, ngDialog, projInfoService, getParameter) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "ORG260_RESULTController";
	
	getParameter.XML(["ORG.UHRM_IMP_TYPE", "ORG.UHRM_IMP_STATUS", "ORG.UHRM_IMP_STS_RES"], function(totas) {
		if (totas) {
			$scope.impType = totas.data[totas.key.indexOf('ORG.UHRM_IMP_TYPE')];
			$scope.impStatus = totas.data[totas.key.indexOf('ORG.UHRM_IMP_STATUS')];
			$scope.impStsRes = totas.data[totas.key.indexOf('ORG.UHRM_IMP_STS_RES')];
		}
	});
	
	$scope.getUpdResultList = function () {
		$scope.sendRecv("ORG260", "getUpdResultList", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", {prjID : $scope.prjID}, 
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsgInDialog(tota.body.msgData);
						return;
					}
					if (tota.length > 0) {
						$scope.resultList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
					}
		});
	};
	
	$scope.getUpdResultList();
	
	$scope.closeDialog = function () {
		$scope.closeThisDialog("cancel");
	};
});
