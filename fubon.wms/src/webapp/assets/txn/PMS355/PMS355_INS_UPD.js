'use strict';
eSoafApp.controller('PMS355_INS_UPDController', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $timeout, $filter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS355_INS_UPDController";
	
	$scope.init = function() {
		$scope.inputVO = {
			userId : projInfoService.getUserID()
		};
		$scope.largeAgrList = [];
	};
	
	$scope.init();

	$scope.importfile = function() {
		$scope.inputVO.sTime = $scope.yearMon;
		
		if($scope.inputVO.realfileName == null || $scope.inputVO.realfileName == undefined){
			$scope.showErrorMsg('上傳資料不能為空');
			return;
		}
		
		$scope.sendRecv("PMS355", "importCustData", "com.systex.jbranch.app.server.fps.pms355.PMS355InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				return;
			}else{
				$scope.showMsg("上傳成功");							
				$scope.closeThisDialog('cancel');
			}
		});
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