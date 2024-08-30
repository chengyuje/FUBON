/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT225Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT225Controller";
		
		$scope.init = function(){
			$scope.resultList = [];
			$scope.outputVO = null;
			
			$scope.inputVO.custID = $scope.custID;
		};
        $scope.init();
        
        $scope.sendRecv("SOT225", "getCustAssetETFMonData", "com.systex.jbranch.app.server.fps.sot225.SOT225InputVO", $scope.inputVO, 
        	function(tota, isError) {
				if (!isError) {
					if (tota[0].body.errorMsg != "" && tota[0].body.errorMsg != null) {
						$scope.showErrorMsg(tota[0].body.errorMsg);
					} else {
						debugger
						$scope.resultList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
					}

					return;
				}
			}
        );
        
        $scope.choose = function(row){
			$scope.returnData = row;
			$scope.connector('set', 'SOTProd', $scope.returnData);
			$scope.closeThisDialog('successful');
		}
        
		
});