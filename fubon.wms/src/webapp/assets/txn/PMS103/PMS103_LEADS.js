/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS103_LEADSController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS103_LEADSController";
		
		/***初始化***/
		$scope.init = function(){
			$scope.inputVO = {
					CDATE:'',
					ADATE:'',
					MDATE:''
        	};
		};
        $scope.init();
          
        /***queryMod查詢明細***/
        $scope.sendRecv("PMS103", "queryMod", "com.systex.jbranch.app.server.fps.pms103.PMS103InputVO", {his_stepID: $scope.stepID, his_campID: $scope.campID},
        		function(tota, isError) {
					if (!isError) {
						$scope.resultList2 = tota[0].body.resultList2;
						$scope.outputVO = tota[0].body;
						return;
					}
		});
        
        /***getLeadsList查詢明細***/
        $scope.sendRecv("PMS103", "getLeadsList", "com.systex.jbranch.app.server.fps.pms103.PMS103InputVO", {his_stepID: $scope.stepID, his_campID: $scope.campID},
        		function(tota, isError) {
					if (!isError) {
						$scope.resultList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
						return;
					}
		});
});
