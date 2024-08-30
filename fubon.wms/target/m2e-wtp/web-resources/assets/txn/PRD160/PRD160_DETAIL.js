'use strict';
eSoafApp.controller('PRD160_DETAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD160_DETAILController";
		
		// combobox
		getParameter.XML(["PRD.INS_TYPE", "PRD.INS_CURRENCY", "PRD.GUARANTEE_ANNUAL", "PRD.INS_MAIN_RIDER", "COMMON.YES_NO"], function(totas) {
			if (totas) {
				$scope.INS_TYPE = totas.data[totas.key.indexOf('PRD.INS_TYPE')];
				$scope.INS_CURRENCY = totas.data[totas.key.indexOf('PRD.INS_CURRENCY')];
				$scope.GUARANTEE_ANNUAL = totas.data[totas.key.indexOf('PRD.GUARANTEE_ANNUAL')];
				$scope.MAIN_RIDER = totas.data[totas.key.indexOf('PRD.INS_MAIN_RIDER')];
				$scope.COM_YN = totas.data[totas.key.indexOf('COMMON.YES_NO')];
			}
		});
        //
		
		// INFO
		$scope.sendRecv("PRD160", "getInsInfo", "com.systex.jbranch.app.server.fps.prd160.PRD160InputVO", {'key_no':$scope.row.KEY_NO},
			function(tota, isError) {
				if (!isError) {
					$scope.insInfo = tota[0].body.resultList[0];
					// old code 先註解以後搞不好要
//					// 繳別
//					if($scope.insInfo.TOTAL_PAY_TYPE) {
//						var role = [];
//						var temp = $scope.insInfo.TOTAL_PAY_TYPE.split(",");
//						angular.forEach(temp, function(row) {
//							role.push($filter('mapping')(row,$scope.PAY_TYPE));
//						});
//						$scope.insInfo.TOTAL_PAY_TYPE = role.toString();
//					}
			}
		});
		$scope.sendRecv("PRD160", "getInsFeature", "com.systex.jbranch.app.server.fps.prd160.PRD160InputVO", {'key_no':$scope.row.KEY_NO},
			function(tota, isError) {
				if (!isError) {
					$scope.feature = tota[0].body.resultList[0];
				}
		});
		$scope.goRisk_level = function() {
			var id = $scope.row.PRD_ID;
			var name = $scope.row.INSPRD_NAME;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD160/PRD160_RISK.html',
				className: 'PRD160',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.INSPRD_ID = id;
                	$scope.INSPRD_NAME = name;
                }]
			});
		};
		$scope.goDownload = function() {
			var id = $scope.row.PRD_ID;
			var name = $scope.row.INSPRD_NAME;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD120/PRDDocument.html',
				className: 'PRDDocument',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.PRD_ID = id;
                	$scope.PRD_NAME = name;
                	$scope.PTYPE = "INS";
                	$scope.SUBSYSTEM_TYPE = "PRD";
                }]
			});
		};
		
		//for App
		if (typeof(webViewParamObj) != 'undefined') {
			$scope.fromApp = true;
		}
});