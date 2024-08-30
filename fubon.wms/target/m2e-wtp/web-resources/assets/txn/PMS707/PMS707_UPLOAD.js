/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS707_UPLOADController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $timeout, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS707_UPLOADController";

		$scope.init = function() {
			$scope.inputVO = {
				userId  :projInfoService.getUserID()
			};
		};
		$scope.init();
	    
	  //import xls
		$scope.importfile = function() {
			$scope.inputVO.yearMon = $scope.yearMon;
			$scope.sendRecv("PMS707", $scope.workFlow,"com.systex.jbranch.app.server.fps.pms707.PMS707InputVO",
				$scope.inputVO, function(tota, isError) {
					if (isError) {
						return;
					} else {
						//調用存儲過程
						$scope.inputVO.storedFlag = $scope.workFlow;
						$scope.callStored();
					};
				});
	    };
	    
	    //調用存儲過程存excel表
	   $scope.callStored = function() {
			$scope.sendRecv("PMS707", "callStored", "com.systex.jbranch.app.server.fps.pms707.PMS707InputVO", $scope.inputVO,
					function(tota, isError) {
						if(tota[0].body.errorMessage==null||tota[0].body.errorMessage==""){
							$scope.showMsg("ehl_01_common_010");
							$scope.closeThisDialog('cancel');
						}else{
							$scope.showErrorMsg(tota[0].body.errorMessage);
						}
			});
	    };
	    
	  //文件名
	    $scope.uploadFinshed = function(name,rname) {
        		$scope.inputVO.fileName = name;
        };
        
        
        //有數據傳入按鈕才生效
        $scope.checkName = function(){
        	var check = false;
        	if($scope.inputVO.fileName != undefined && $scope.inputVO.fileName != ''){
        		check = true;
        	}
        	return check;
        }
	    
	    
});