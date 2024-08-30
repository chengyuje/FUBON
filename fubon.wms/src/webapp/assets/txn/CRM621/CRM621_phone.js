/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM621_phoneController', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "CRM621_phoneController";


	$scope.mappingSet['YN'] = [ {
		LABEL : "是",
		DATA : "Y"
	}, {
		LABEL : "否",
		DATA : "N"
	} ];

	$scope.inquire_phone = function() {
		$scope.phoneList = [];
		$scope.sendRecv("CRM621", "inquire_phone", "com.systex.jbranch.app.server.fps.crm621.CRM621InputVO", {
			'cust_id' : $scope.custVO.CUST_ID
		}, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsg(tota[0].body.msgData);
			}
			if (tota[0].body.phoneList.length > 0) {
				$scope.phoneList = tota[0].body.phoneList;
				$scope.output_phoneList = tota[0].body;
			}
		});
	}
	$scope.inquire_phone();
});
