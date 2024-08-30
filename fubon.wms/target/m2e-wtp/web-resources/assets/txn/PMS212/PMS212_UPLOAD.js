/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS212_UPLOADController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $timeout, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS212_UPLOADController";

		$scope.init = function() {
			$scope.inputVO = {
				userId  :projInfoService.getUserID()
			};
			$scope.largeAgrList = [];
		};
		$scope.init();
	    
	  //import xls
		$scope.importfile = function() {
			$scope.inputVO.custID   = $scope.custID;
			if($scope.inputVO.realfileName==null||$scope.inputVO.realfileName==undefined){
				$scope.showErrorMsg('上傳資料不能為空');
				return;
			}
			$scope.sendRecv("PMS212", "importData", "com.systex.jbranch.app.server.fps.pms212.PMS212InputVO", $scope.inputVO,
					function(tota, isError) {
						if(!isError){
							$scope.showMsg("ehl_01_common_010");
							$scope.closeThisDialog('cancel');
						}else{
							$scope.showErrorMsg(tota[0].body.errorMessage);
						}
					}
			);
	    };
    
	  //文件名
	    $scope.uploadFinshed = function(name,rname) {
        		$scope.inputVO.fileName = name;
            	$scope.inputVO.realfileName = rname;
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