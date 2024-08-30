/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM230Controller',
	function(sysInfoService, $rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM230Controller";
		
		$scope.sendRecv("CAM230", "getCampList", "com.systex.jbranch.app.server.fps.cam230.CAM230InputVO", $scope.inputVO,
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(totas[0].body.msgData);
					} else {
						$scope.fileList = tota[0].body.fileList;
						$scope.outputVO = tota[0].body;
						
						$scope.mappingSet['fileList'] = [];
						angular.forEach($scope.fileList, function(row, index, objs){				
								$scope.mappingSet['fileList'].push({LABEL: row.FILENAME, DATA: row.FILENAME});				
						});
						
						$scope.inputVO.assignRow = tota[0].body.assignRow;
					}
				}
		);
		
		$scope.init = function(){
			$scope.inputVO = {
					campID: '',
					assignRow: '',
					totalNum: '',
					successNum: '', 
					failureNum: ''
        	};
		};
        $scope.init();
        
        // edit data
        $scope.getAssignDtl = function() {
        	$scope.inputVO.totalNum = '';
        	$scope.inputVO.successNum = '';
        	$scope.inputVO.failureNum = '';
			$scope.sendRecv("CAM230", "getAssignDtl", "com.systex.jbranch.app.server.fps.cam230.CAM230InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsg(totas[0].body.msgData);
						} else {
							$scope.inputVO.totalNum = tota[0].body.totalNum;
							$scope.inputVO.successNum = tota[0].body.successNum;
							$scope.inputVO.failureNum = tota[0].body.failureNum;
						}
					}
			);
			

        }
		

        
});