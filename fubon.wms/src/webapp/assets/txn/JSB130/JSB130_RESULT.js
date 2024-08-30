/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('JSB130_RESULTController', function($rootScope, $scope, $controller, getParameter) {
    $controller('BaseController', {$scope: $scope});
    $scope.controllerName = "JSB130_RESULTController";
    
    getParameter.XML(["JSB.INS_COM_NAME_03"], function(totas) {
		if (totas) {
			$scope.mappingSet['JSB.INS_COM_NAME_03'] = totas.data[totas.key.indexOf('JSB.INS_COM_NAME_03')];
		}
	});
    
    $scope.init = function(){
    	$scope.inputVO.export_type = undefined;
	}
    
    $scope.goMerge = function(){
    	$scope.sendRecv("JSB130", "goMerge", "com.systex.jbranch.app.server.fps.jsb130.JSB130InputVO", $scope.inputVO, 
		function(tota, isError) {
    		if (!isError) {
    			$scope.showSuccessMsg('ehl_01_common_023'); // 執行成功
				$scope.closeThisDialog('successful');
			}
		});
    }
    
    $scope.export = function(type){
    	$scope.inputVO.export_type = type;
    	$scope.sendRecv("JSB130", "export", "com.systex.jbranch.app.server.fps.jsb130.JSB130InputVO", $scope.inputVO, 
		function(tota, isError) {
    		if (isError) {
    			$scope.showErrorMsg(tota.body.msgData);
				return;
			}
		});
    }
    
    // 上傳後結果檢視，按取消，資料不會彙入匯整檔中，如當月有上傳過，將清空重新上傳
    $scope.cancel = function(){
    	$scope.sendRecv("JSB130", "cancel", "com.systex.jbranch.app.server.fps.jsb130.JSB130InputVO", $scope.inputVO, 
		function(tota, isError) {
    		if (isError) {
    			$scope.showErrorMsg(tota.body.msgData);
				return;
			} else {
				$scope.closeThisDialog('cancel');
			}
		});
    }
    
    $scope.init();
});