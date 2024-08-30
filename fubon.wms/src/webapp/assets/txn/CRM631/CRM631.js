/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM631Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, sysInfoService,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM631Controller";
		
		getParameter.XML(["CAM.LEAD_TYPE"], function(totas) {
			if (totas) {
				$scope.LEAD_TYPE = totas.data[totas.key.indexOf('CAM.LEAD_TYPE')];
			}
		});
		$scope.login = String(sysInfoService.getPriID());	
//		$scope.cust_id = $scope.connector('get','CRM110_CUST_ID');
		$scope.cust_id =  $scope.custVO.CUST_ID;
		
		$scope.sendRecv("CAM200", "getBeContaceListByCRM631", "com.systex.jbranch.app.server.fps.cam200.CAM200InputVO", {'custID':$scope.cust_id},
    			function(tota, isError) {
    				if (!isError) {
    					if(tota[0].body.beContactList && tota[0].body.beContactList.length == 0) {
    						$scope.beContactList = [];
    						return;
    					}
    					$scope.data = tota[0].body.beContactList;
    					
						return;
    				}
    			}
		);
		
		
		$scope.jump = function() {
			
//			$scope.connector('set','CRM110_CUST_ID', $scope.cust_id);
			var path = "assets/txn/CAM200/CAM200.html";
			 $scope.connector('set','tab','tab1');
			$scope.connector("set","CRM610URL",path);
			 $scope.$emit("CRM610VO", {action:"set", type:"URL", data:path});
		};
        
		
});
