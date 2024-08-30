// 該頁面已廢除未使用
// 該頁面已廢除未使用
// 該頁面已廢除未使用
// 該頁面已廢除未使用
// 該頁面已廢除未使用
// 該頁面已廢除未使用
// 該頁面已廢除未使用
// 該頁面已廢除未使用
// 該頁面已廢除未使用
// 該頁面已廢除未使用
'use strict';
eSoafApp.controller('PMS227_UPLOADController', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $timeout, $filter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS227_UPLOADController";
	
	$scope.init = function() {
		$scope.inputVO = {
			userId  :projInfoService.getUserID(),
			
		};
		$scope.largeAgrList = [];
	};
	$scope.init();
	    
	//import xls
	$scope.importfile = function() {
		$scope.inputVO.sTime = $scope.yearMon;
		if ($scope.inputVO.realfileName == null || $scope.inputVO.realfileName == undefined) {
			$scope.showErrorMsg('上傳資料不能為空');
			return;
		}
		
		$scope.sendRecv("PMS227", $scope.uploadName, "com.systex.jbranch.app.server.fps.pms227.PMS227InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				return;
			}else{
				//調用存儲過程
				$scope.callStored();
			}
		});
	};
	
	//調用存儲過程存excel表
	$scope.callStored = function() {
		$scope.inputVO.uploadName = $scope.uploadName;
		$scope.sendRecv("PMS227", "callStored", "com.systex.jbranch.app.server.fps.pms227.PMS227InputVO", $scope.inputVO, function(tota, isError) {
			if(tota[0].body.errorMessage == null || tota[0].body.errorMessage == ""){
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
	
	// 下載 
	$scope.downLoad = function() {
		$scope.sendRecv("PMS227", "downLoad2", "com.systex.jbranch.app.server.fps.pms227.PMS227OutputVO", {'csvList': $scope.csvList}, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
			}
		});
	}
});