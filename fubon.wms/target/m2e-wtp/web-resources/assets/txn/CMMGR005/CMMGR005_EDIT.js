/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMMGR005EditController', 
    function($scope, $controller, socketService, alerts) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR005EditController";  
        
        $scope.init = function(){
        	$scope.row = $scope.row || {};
            
        	$scope.inputVO = {
        			hlbAuditId: $scope.row.AUDITID,
        			scheduleId: $scope.row.SCHEDULEID
            };
	    	$scope.paramList = [];
	      }
        $scope.init();

//      初始分頁資訊
        $scope.inquireInit = function(){
        	$scope.paramList = [];
        }
        $scope.inquireInit();
        
        $scope.inquire = function() {
        	$scope.sendRecv("CMMGR005", "messageInquire", "com.systex.jbranch.app.server.fps.cmmgr005.CMMGR005InputVO", $scope.inputVO,
	             function(tota, isError) {
	                  if (!isError) {
	                	  $scope.initLimit();
	                      $scope.paramList = tota[0].body.messageList;
	                      $scope.outputVO = tota[0].body;
	                  }else{
	                	  $scope.showErrorMsg('ehl_01_common_000');
	                  }
            	});
        }
        $scope.inquire();
        
        $scope.showmsg = function(msg) {
        	$scope.currentDetailRow = msg;
        };
    }
);