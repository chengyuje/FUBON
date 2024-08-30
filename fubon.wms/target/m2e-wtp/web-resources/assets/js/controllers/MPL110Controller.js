/**3
 * 
 */
'use strict';
eSoafApp.config(['$locationProvider', function($locationProvider) {  
	$locationProvider.html5Mode(true);  
}]);

eSoafApp.controller("MPL110Controller",['$rootScope', '$scope', '$controller', '$location', '$interpolate', '$timeout', 'projInfoService', '$confirm', 'sysInfoService', 'socketService', 'ngDialog', 'getParameter',
    function($rootScope, $scope, $controller, $location, $interpolate, $timeout, projInfoService, $confirm, sysInfoService, socketService, ngDialog, getParameter) {
		$controller('BaseController', {$scope : $scope});
		
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
//						alert($scope.inputVO.empID);
					}		
					$scope.connector("set", "MPL110_inputVO", $scope.inputVO);
					$scope.connector("set", "MPL110_routeURL", 'assets/txn/MPL110/MPL110.html');
			});
			
			$scope.$watch('connector("get","MPL110_routeURL")', function(newValue, oldValue) {
				$scope.routeURL = $scope.connector("get","MPL110_routeURL");
			});
		};
		$scope.init();
}]);