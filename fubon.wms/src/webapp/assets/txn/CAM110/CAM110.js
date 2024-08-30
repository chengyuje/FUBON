/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM110Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM110Controller";
		
		$scope.init = function(){
			$scope.sendRecv("CAM110", "initial", "com.systex.jbranch.app.server.fps.cam110.CAM110InputVO", {},
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		if(totas[0].body.resultList.length == 0) {
	                			$scope.showMsg("ehl_01_common_001");
	                			return;
	                		}
	                		$scope.List = totas[0].body.resultList;
	                	};
					}
			);
		};
        $scope.init();
		
        $scope.save = function(){
        	$scope.sendRecv("CAM110", "save", "com.systex.jbranch.app.server.fps.cam110.CAM110InputVO", {'list':$scope.List},
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsgInDialog(totas[0].body.msgData);
                    	}
                    	if (totas.length > 0) {
                    		$scope.showMsg('儲存成功');
                    		$scope.init();
                    	};
    				}
    		);
        	
        };
		
});
