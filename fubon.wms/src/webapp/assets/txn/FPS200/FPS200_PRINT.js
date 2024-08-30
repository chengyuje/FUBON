/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS200PrintController', 
	    function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, getParameter) {
	        $controller('BaseController', {$scope: $scope});
	        $scope.controllerName = "FPS200PrintController"; 
	        
	        /** initialize **/
	        $scope.init = function(){
	        	$scope.inputVO.planID = $scope.row.PLAN_ID;
	        	$scope.sendRecv("FPS200", "queryPrint", "com.systex.jbranch.app.server.fps.fps200.FPS200InputVO", $scope.inputVO,
			              function(tota, isError) {
			                  if (!isError) {
			                	  $scope.printList = tota[0].body.printList;
			                  }
			                  else{
			                	  $scope.showErrorMsg(tota);
			                  }
			                
			      });
	        	
	        }
	        $scope.init();
	        
	        $scope.printPlan = function(){
	        	$scope.inputVO.planID = $scope.row.PLAN_ID;
	        	$scope.inputVO.custID = $scope.row.CUST_ID;
	        	$scope.sendRecv("FPS200", "printPlan", "com.systex.jbranch.app.server.fps.fps200.FPS200InputVO", $scope.inputVO,
			              function(tota, isError) {
			                  if (!isError) {
			                	  $scope.init();
			                  }
			                  else{
			                	  $scope.showErrorMsg(tota);
			                  }
			                
			      });
	        }
	        
}
);