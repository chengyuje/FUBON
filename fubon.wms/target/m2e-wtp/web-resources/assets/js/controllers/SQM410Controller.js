/**3
 * 
 */
'use strict';
eSoafApp.config(['$locationProvider', function($locationProvider) {  
	$locationProvider.html5Mode(true);  
}]);

eSoafApp.controller("SQM410Controller",['$rootScope', '$scope', '$controller', '$location', '$interpolate', '$timeout', 'projInfoService', '$confirm', 'sysInfoService', 'socketService', 'ngDialog', 'getParameter',
    function($rootScope, $scope, $controller, $location, $interpolate, $timeout, projInfoService, $confirm, sysInfoService, socketService, ngDialog, getParameter) {
		$controller('BaseController', {$scope : $scope});
		
		$scope.init = function() {
//			alert(JSON.stringify($location.search()));
			//從批次推播進來的情境
			$scope.inputVO = {
				auserid : $location.search().auserid,
				loginEmpID : $location.search().loginEmpID, 
				privilegeID : $location.search().privilegeID
			};
			
			
			if($scope.inputVO.auserid){ //從功能選單進入情境
				$scope.sendRecv("SQM410", "getEmpIdByAuserId", "com.systex.jbranch.app.server.fps.crm453.CRM453InputVO", {"auserid": $location.search().auserid},
					    function(totas, isError) {
								if (totas[0].body.resultList == null || totas[0].body.resultList.length == 0) {
									$scope.showMsg("未取得登入者員編。");
			            			return;
			            		}
								if (totas[0].body.resultList.length > 0) {
									$scope.inputVO.loginEmpID = totas[0].body.resultList[0].EMP_NUMBER;
									$scope.inputVO.privilegeID = totas[0].body.resultList[0].PRIVILEGEID;
//									alert($scope.inputVO.loginEmpID);
									$scope.connector("set", "SQM410_inputVO", $scope.inputVO);
									$scope.connector("set", "SQM410_routeURL", 'assets/txn/SQM410/SQM410.html');
								}
				});
			}else{
				//不可以拉到外層, 後端回應執行不到
				$scope.connector("set", "SQM410_inputVO", $scope.inputVO);
				$scope.connector("set", "SQM410_routeURL", 'assets/txn/SQM410/SQM410.html');
			}
				
			
			$scope.$watch('connector("get","SQM410_routeURL")', function(newValue, oldValue) {
				$scope.routeURL = $scope.connector("get","SQM410_routeURL");
			});
		};
		$scope.init();
}]);