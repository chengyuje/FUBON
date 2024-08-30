/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('TESTSOAPController',function($scope, $rootScope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, getParameter , $http) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = 'TESTSOAPController';
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	$scope.init = function(queryType){
		$scope.inputVO = {};
		$scope.outputVO = {};
		
		$scope.inputVO.contentType='application/soap+xml;charset=UTF-8';
		$scope.inputVO.url = 'http://10.42.70.194/FBMAPPService/GetMappCase.asmx';
		$scope.inputVO.soapAction = 'http://www.fubon.com/sso/webservices/GetCase';
	};
    $scope.init();

	//查詢
    $scope.query = function(){
		$scope.sendRecv('TESTSOAP', 'query', 'com.systex.jbranch.app.server.fps.testsoap.TESTSOAPInputVO',  $scope.inputVO , function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultSoapData) {
					$scope.outputVO = tota[0].body;
					$scope.inputVO.soapRequestData = tota[0].body.resultSoapData;
					return;
        		}
				$scope.showMsg('ehl_01_common_009');
				return;
			}
		});
	};
});