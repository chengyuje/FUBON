/**3
 * 
 */
'use strict';
eSoafApp.config(['$locationProvider', function($locationProvider) {  
	$locationProvider.html5Mode(true);  
}]);

eSoafApp.controller("CRM453Controller",['$rootScope', '$scope', '$controller', '$location', '$interpolate', '$timeout', 'projInfoService', '$confirm', 'sysInfoService', 'socketService', 'ngDialog', 'getParameter',
    function($rootScope, $scope, $controller, $location, $interpolate, $timeout, projInfoService, $confirm, sysInfoService, socketService, ngDialog, getParameter) {
		$controller('BaseController', {$scope : $scope});
		
		$scope.mappingSet['CRM.APPLY.CAT'] =[];
    	$scope.mappingSet['CRM.APPLY.CAT'].push({LABEL: '期間', DATA: '1'},
    											{LABEL: '單次', DATA: '2'});
		
		$scope.init = function() {
//			alert(JSON.stringify($location.search()));
//			$scope.inputVO = {
//				auserid : $location.search().auserid, 
//				empID : $location.search().userid, 
//				empID : '003037'
//			};
			
			$scope.sendRecv("CRM453", "getEmpIdByAuserId", "com.systex.jbranch.app.server.fps.crm453.CRM453InputVO", {"auserid": $location.search().auserid},
				function(totas, isError) {
					if (totas[0].body.resultList.length == 0) {
						$scope.showMsg("未取得登入者員編。");
            			return;
            		}
					if (totas[0].body.resultList.length > 0) {
						$scope.inputVO.empID = totas[0].body.resultList[0].EMP_NUMBER;
					}		
					$scope.connector("set", "CRM453_inputVO", $scope.inputVO);
					$scope.connector("set", "CRM453_routeURL", 'assets/txn/CRM453/CRM453.html');
			});
			
			$scope.$watch('connector("get","CRM453_routeURL")', function(newValue, oldValue) {
				$scope.routeURL = $scope.connector("get","CRM453_routeURL");
			});
		};
		$scope.init();
		
		
}]);