/**================================================================================================
 @program: CMMGR023.js
 @author SamTu
 @Description 排程監控_確認未來排程預計要Get的檔案是否存在
 @version: 1.0.20190904
 =================================================================================================*/
'use strict';
eSoafApp.controller('CMMGR023_Controller',
    ($scope, $controller, socketService, ngDialog) => {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR023_Controller";
        
        $scope.init = function(){
        	$scope.isDateChoose = false;
        	$scope.inputVO.searchDate = null;
        	$scope.inputVO.SCHDID = "";
        	$scope.inputVO.JOBID = "";
        }
        $scope.init();
        
        $scope.getLatestMoniterResult = function(){
	    	  $scope.sendRecv("CMMGR023", "getLatestMoniterResult", "com.systex.jbranch.app.server.fps.cmmgr023.CMMGR023InputVO", $scope.inputVO,
	                  function(tota, isError) {
	                      if (!isError) {
	                    	  $scope.monitorTime = tota[0].body.monitorTime;
	                    	  $scope.paramList = tota[0].body.recentSCHDMonitorList;
	                    	  $scope.outputVO = tota[0].body;
	                          return;
	                      }
	                  });
	      }
        $scope.getLatestMoniterResult();
        
        $scope.inquire = function(){
	    	  $scope.sendRecv("CMMGR023", "getHistoryMoniterResult", "com.systex.jbranch.app.server.fps.cmmgr023.CMMGR023InputVO", $scope.inputVO,
	                  function(tota, isError) {
	                      if (!isError) {
	                    	  $scope.paramList2 = tota[0].body.hisSCHDMonitorList;
	                    	  $scope.outputVO2 = tota[0].body;
	                          return;
	                      }
	                  });
	      }
        
        $scope.chooseDate = function(){
        	$scope.isDateChoose = true;
	      }
        
        //日期相關
    	$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};       
    }
);
