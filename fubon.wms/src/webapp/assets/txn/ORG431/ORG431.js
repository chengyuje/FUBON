'use strict';
eSoafApp.controller('ORG431Controller', function(sysInfoService, $scope, $controller, $filter, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "ORG431Controller";
	
	$scope.query = function() {
		$scope.aoCntLst = [];
		$scope.reportLst = [];
		
		$scope.sendRecv("ORG431", "query", "com.systex.jbranch.app.server.fps.org431.ORG431InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
				$scope.aoCntLst = tota[0].body.aoCntLst;
				$scope.reportLst = tota[0].body.reportLst;
				
				$scope.inputVO.year = tota[0].body.year;
				$scope.inputVO.toDay = tota[0].body.toDay;
				$scope.inputVO.nowMonth = tota[0].body.nowMonth;
				$scope.inputVO.nextMonth = tota[0].body.nextMonth;
				$scope.inputVO.next2Month = tota[0].body.next2Month;
				
				$scope.outputVO = tota[0].body;
			}
		});
	};
	
	$scope.init = function() {
		$scope.aoCntLst = [];
		$scope.reportLst = [];
		
		$scope.inputVO = {
			year: '',
			toDay: '', 
			nowMonth: '',
			nextMonth:  '',
			next2Month: '', 
			EXPORT_LST: []
    	};
		
		$scope.query();
	};
	$scope.init();
	
	$scope.export = function() {
		angular.copy($scope.reportLst, $scope.inputVO.EXPORT_LST);
		$scope.inputVO.EXPORT_LST.push($scope.totals);
		
		console.log(JSON.stringify($scope.inputVO.EXPORT_LST));
		
		$scope.sendRecv("ORG431", "export", "com.systex.jbranch.app.server.fps.org431.ORG431InputVO", $scope.inputVO,
				function(totas, isError) {
                	if (isError) {
                		$scope.showErrorMsg(totas[0].body.msgData);
                	}
                	if (totas.length > 0) {
                		if(totas[0].body.aoCntLst && totas[0].body.aoCntLst.length == 0) {
                			$scope.showMsg("ehl_01_common_009");
                			return;
                		}
                	};
				}
		);
	};
	
});
