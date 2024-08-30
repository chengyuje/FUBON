/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT930Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "IOT930Controller";
		
		$scope.getMappVideoList = function() {
			$scope.sendRecv("IOT930","GetMAPPEVideo","com.systex.jbranch.app.server.fps.iot930.IOT930InputVO", {'CASE_ID': $scope.inputVO.CASE_ID},
					function(tota,isError){
						if(!isError){
							$scope.MAPPEVideoList = tota[0].body.MAPPEVideoList;
						}
				});
		}
        
		$scope.init = function() {
			$scope.MAPPEVideoList = null;
			$scope.inputVO.CASE_ID = $scope.CASE_ID;
			
			$scope.getMappVideoList();
		}
		$scope.init();
	}
);