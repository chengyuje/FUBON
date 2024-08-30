/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SQM110_flowDetailController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "SQM110_flowDetailController";
	
	$scope.STATUS_LIST = [{'LABEL':'未處理', 'DATA': '0'},{'LABEL':'結案扣分', 'DATA': '1'},{'LABEL':'結案不扣分', 'DATA': '2'},{'LABEL':'處理中', 'DATA': '3'},{'LABEL':'退件', 'DATA': '4'},{'LABEL':'', 'DATA': '5'}];
	
	//資料查詢
	$scope.totalData = [];
	$scope.outputVO = {};
	$scope.query = function() {	
		$scope.sendRecv("SQM110", "queryFlowDetail", "com.systex.jbranch.app.server.fps.sqm110.SQM110DetailInputVO", {'case_no' : $scope.case_no}, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.totalList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
					return;
				}
				$scope.totalData = tota[0].body.totalList;
				$scope.outputVO = tota[0].body;
			}						
		});
		
	};
	
	$scope.query();

});