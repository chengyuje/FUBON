/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS350PreviewController',
    function ($rootScope, $scope, $controller) {
        $controller('BaseController', {
            $scope: $scope
        });
        $scope.controllerName = "FPS350PreviewController";

        var log = console.log;
        $scope.init = function () {
        	$scope.base64 = 'data:application/pdf;base64,';
        	$scope.inputVO = {
        		planID: $scope.data.PLAN_ID,
        		SEQNO: $scope.data.SEQ_NO,
        		custID: $scope.data.CUST_ID
            };
        };

        $scope.preview = function () {
        	$scope.sendRecv('FPS350', 'preview', 'com.systex.jbranch.app.server.fps.fps350.FPS350InputVO', $scope.inputVO,
            function (tota, isError) {
        		if(!isError) {
        			$scope.base64 += tota[0].body.base64;
        		}
        	});
        };
        
        // sendMail
        $scope.sendMail = function() {
            $scope.sendRecv('FPS350', 'sendMail', 'com.systex.jbranch.app.server.fps.fps350.FPS350InputVO', $scope.inputVO,
            function (tota, isError) {
            	if (!isError) {
            		$scope.showMsg(tota[0].body.message);
            	}
            });
        };
        
        // main process
        $scope.init();
        $scope.preview();
    });
