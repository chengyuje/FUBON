/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS102_LEADSController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS102_LEADSController";
		$scope.init = function(){
//			$scope.inputVO = {
//					CUST_ID:$scope.inputVO.CUST_ID,
//					CDATE:'',   //C日期
//					ADATE:'',
//					MDATE:''
//        	};
//			alert(JSON.stringify($scope.inputVO));
		};
		$scope.inputVO.sCreDate=$scope.inputVO.PLAN_YEARMON;
        $scope.init();
        
        getParameter.XML(["FPS.PROD_TYPE"], function(totas) {
 			if (totas) {
 				$scope.PRD_TYPE = totas.data[totas.key.indexOf('FPS.PROD_TYPE')];
			}
 		});
        //日自
    	$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
  
		$scope.queryMod = function() {
        //queryMod
	        $scope.sendRecv("PMS102", "queryMod", "com.systex.jbranch.app.server.fps.pms102.PMS102InputVO", $scope.inputVO ,
	        		function(tota, isError) {
						if (!isError) {
							$scope.resultList2 = tota[0].body.resultList2;
							$scope.outputVO2 = tota[0].body;
							return;
						}
			});
		}
		$scope.queryMod();
		$scope.getLeadsList = function() {
	        $scope.sendRecv("PMS102", "getLeadsList", "com.systex.jbranch.app.server.fps.pms102.PMS102InputVO", $scope.inputVO,
	        		function(tota, isError) {
						if (!isError) {
							$scope.resultList = tota[0].body.resultList;
							console.log( tota[0].body.resultList);
							$scope.outputVO1 = tota[0].body;
							return;
						}
			});
		}
		$scope.getLeadsList();
});
