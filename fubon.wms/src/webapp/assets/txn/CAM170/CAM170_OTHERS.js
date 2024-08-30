/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM170_OTHERSController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM170_OTHERSController";
		
		$scope.init = function(){
			$scope.inputVO = {
				examVersion: $scope.examVersion, 
				questionVersion: $scope.questionVersion, 
				questionDesc: $scope.questionDesc, 
				answerSEQ: $scope.answerSEQ, 
				answerDesc: $scope.answerDesc
	    	};
		};
		$scope.init();
		
		$scope.sendRecv("CAM170", "getOthersAnswer", "com.systex.jbranch.app.server.fps.cam170.CAM170InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.otherAnswerList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.otherAnswerList = tota[0].body.otherAnswerList;
						$scope.outputVO = tota[0].body;
						
						return;
					}
		});
		
});
