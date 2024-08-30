/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS424UController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS424UController";
		// 繼承
		$controller('PMS424Controller', {$scope: $scope});
		
		$scope.initPMS424U = function() {
			$scope.inputVO.uhrmRC = '031';
			$scope.inputVO.uhrmOP = '031A';
			$scope.inputVO.person_role = 'UHRM';
			
			$scope.isMainten = false;
			$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS424U'},
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList[0].MAIN_YN == 'Y') {
							$scope.isMainten = true;
						}
					}
			});
			
		};
		
		$scope.initPMS424U();
		
//		$scope.passParams = $scope.connector('get','passParams');
////		alert($scope.passParams);
//		$scope.fromCRM181 = function() {
//			if($scope.passParams != undefined) {
//				var dataParams = $scope.passParams.replace(/=/g, ',').split(',');
////				alert(JSON.stringify(dataParams));
//				//理專送出時間
//				var dateParam = [];
//				for(var i=0; dataParams.length > i ; i++){
//					if(dataParams[i] == "MON"){						
//						dateParam = dataParams[i+1];
//					}
//				}
//				//上一個月
//				if(dateParam == 'LASTMON') {
//					$scope.inputVO.yyyymm = $scope.mappingSet['time'][0].DATA;
//					$scope.dateChange();
//				}
//			}
//		}
//		$scope.fromCRM181();
});