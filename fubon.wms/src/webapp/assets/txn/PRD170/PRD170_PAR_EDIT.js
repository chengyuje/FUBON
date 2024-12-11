/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD170_PAR_EDITController',
    function($rootScope, $scope, $controller, sysInfoService, getParameter) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "PRD170_PAR_EDITController";

        // init
        $scope.init = function() {
        	$scope.sendRecv("PRD170", "inquireEditData", "com.systex.jbranch.app.server.fps.prd170.PRD170InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.resultList = [];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;	
							return;
						}						
			});
        };
        $scope.init();
        
        $scope.update = function(row) {
        	
        	$scope.updateVO = {
        			PARAM_TYPE : row.PARAM_TYPE,
        			PARAM_CODE : row.PARAM_CODE,
        			PARAM_NAME : row.PARAM_NAME
        	};
        	
        	$scope.sendRecv("PRD170", "updateParameter", "com.systex.jbranch.app.server.fps.prd170.PRD170InputVO", $scope.updateVO,
					function(tota, isError) {
						if (!isError) {
								$scope.resultList = [];
								$scope.showSuccessMsg("ehl_01_common_025");
								$scope.init();
						} else {
							$scope.showErrorMsg("ehl_01_common_024");
						}						
			});
        	
        	
        	
        	
        }

});
