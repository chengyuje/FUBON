/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD235_FEEController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD235_FEEController";
        
		$scope.init = function() {
			var today = new Date();			
			$scope.inputVO = {};
			$scope.inputVO.year1 = today.getFullYear() - 1; //近一年
			$scope.inputVO.year5 = today.getFullYear() - 5; //近五年
		}
		$scope.init();
		
		//取得費用率報酬率資料
		$scope.getFeeRates = function() {
        	$scope.sendRecv("PRD235", "getFeeRates", "com.systex.jbranch.app.server.fps.prd235.PRD235InputVO", $scope.inputVO,
                function(tota, isError) {
                    if (isError) {
                        $scope.showErrorMsg(tota[0].body.msgData);
                    } else {
                    	$scope.resultList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
                    }
			});
        };
        $scope.getFeeRates();
		
        //下載範例檔案
		$scope.downloadSimple = function() {
        	$scope.sendRecv("PRD235", "downloadSimpleFee", "com.systex.jbranch.app.server.fps.prd235.PRD235InputVO", {},
					function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		};
				
		$scope.uploadFinshed = function(name, rname) {
        	$scope.inputVO.fileName = name;
        	$scope.inputVO.fileRealName = rname;
        };
		
      //上傳費用率報酬率資料
        $scope.uploadFee = function() {
        	$scope.sendRecv("PRD235", "uploadFee", "com.systex.jbranch.app.server.fps.prd235.PRD235InputVO", $scope.inputVO,
                function(tota, isError) {
                    if (isError) {
                        $scope.showErrorMsg(tota[0].body.msgData);
                    } else {
                    	$scope.init();
                    	$scope.getFeeRates();
                    	$scope.showSuccessMsg("上傳成功"); 
                    }
			});
        };
});