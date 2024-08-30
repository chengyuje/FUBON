/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM200_FILESController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM200_FILESController";
		
		var campName = $scope.campName;
		$scope.init = function(){
			$scope.inputVO = {
				campID: $scope.campID,
				stepID: $scope.stepID
        	};
		};
        $scope.init();
                
        $scope.sendRecv("CAM200", "getFileList", "com.systex.jbranch.app.server.fps.cam200.CAM200InputVO", $scope.inputVO,
        		function(tota, isError) {
					if (!isError) {
						if(tota[0].body.fileList.length == 0) {
//							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						$scope.fileList = tota[0].body.fileList;
						$scope.outputVO = tota[0].body;

						return;
					}
		});
        
        $scope.downloadByteFile = function(row){
			$scope.sendRecv("CAM200", "downloadFile", "com.systex.jbranch.app.server.fps.cam200.CAM200InputVO", {onefileName:row.DOC_NAME, attach:row.DOC_FILE},
					function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		};
});
