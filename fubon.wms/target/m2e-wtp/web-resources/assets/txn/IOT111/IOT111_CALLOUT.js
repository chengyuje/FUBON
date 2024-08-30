/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('IOT111_CALLOUTController', function($rootScope, $scope, $controller, getParameter) {
    $controller('BaseController', {$scope: $scope});
    $scope.controllerName = "IOT111_CALLOUTController";
    
    //XML參數
  	getParameter.XML(["CALLOUT_STATUS", "CALLOUT.FAIL_REASON", "CALLOUT.FAIL_TYPE"],function(totas){
  		if(totas){
  			$scope.mappingSet['CALLOUT_STATUS'] = totas.data[totas.key.indexOf('CALLOUT_STATUS')];				// 電訪狀態
  			$scope.mappingSet['CALLOUT.FAIL_REASON'] = totas.data[totas.key.indexOf('CALLOUT.FAIL_REASON')];	// 未成功原因
  			$scope.mappingSet['CALLOUT.FAIL_TYPE'] = totas.data[totas.key.indexOf('CALLOUT.FAIL_TYPE')];		// 失敗異常分類
  		}
  	});
    
    $scope.init = function(){
    	$scope.outputVO = $scope.resultList;
	}
    
    $scope.init();
});