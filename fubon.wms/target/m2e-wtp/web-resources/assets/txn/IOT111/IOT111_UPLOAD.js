/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('IOT111_UPLOADController', function($rootScope, $scope, $controller) {
    $controller('BaseController', {$scope: $scope});
    $scope.controllerName = "IOT111_UPLOADController";
    
    $scope.init = function(){
    	$scope.resultList = [];
    	$scope.outputVO = [];
	}
    
 	$scope.downLoad = function() {
		$scope.sendRecv("IOT111", "downLoad", "com.systex.jbranch.app.server.fps.iot111.IOT111InputVO", {},
		function(tota, isError) {
			
		});
	}
 	
 	$scope.uploadFinshed = function(name, rname) {
		$scope.inputVO.fileName = name;
		$scope.inputVO.fileRealName = rname;
	}
 	
 	$scope.upload = function() {
 		if ($scope.inputVO.fileName == undefined) {
 			$scope.showErrorMsg("ehl_01_common_022");
 			return;
 		}
 		
 		$scope.sendRecv("IOT111", "upload", "com.systex.jbranch.app.server.fps.iot111.IOT111InputVO", $scope.inputVO,
 		function(tota, isError) {
 			if (!isError) {
 				$scope.resultList = tota[0].body.resultList;
 				$scope.outputVO = tota[0].body; 				
 			}
 		});
 	}
 	
 	$scope.confirm = function() {
 		if ($scope.resultList.length == 0) {
 			$scope.showErrorMsg("請先上傳資料檔案");
			return;
 		}
 		
 		var errFlag = false;
 		angular.forEach($scope.resultList, function(row){
 			if (!errFlag) {
 				if (row.CHECK_RESULT_1 == 'N' || row.CHECK_RESULT_2 == 'N' || row.CHECK_RESULT_3 == 'N') {
 					$scope.showErrorMsg("適合度編碼檢核編碼錄音序號不符合規則，無法上傳。");
 					errFlag = true;
 					return;
 				} 				
 			}
 		});
 		if (!errFlag) {
 			$scope.sendRecv("IOT111", "confirm", "com.systex.jbranch.app.server.fps.iot111.IOT111InputVO", {'resultList': $scope.resultList},
 					function(tota, isError) {
 				if (!isError) {
 					$scope.showSuccessMsg('ehl_01_common_023'); // 執行成功
 					$scope.closeThisDialog('successful');				
 				}
 			}); 			
 		}
 	}
    
    $scope.init();
});