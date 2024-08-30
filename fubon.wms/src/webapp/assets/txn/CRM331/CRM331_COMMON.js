/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM331_COMMONController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$scope.controllerName = "CRM331_COMMONController";
		

		// bra
		$scope.mappingSet['branchsDesc'] = [];
		angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
			$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		});
		// gender
		$scope.mappingSet['GENDER'] = [];
		$scope.mappingSet['GENDER'].push({LABEL : '男',DATA : '1'},{LABEL : '女',DATA : '2'});
		// ins_yn
		$scope.mappingSet['INS_YN'] = [];
		$scope.mappingSet['INS_YN'].push({LABEL : '有',DATA : 'Y'},{LABEL : '無',DATA : 'N'});
		// OP_FREQ
		$scope.mappingSet['OP_FREQ'] = [];
		$scope.mappingSet['OP_FREQ'].push({LABEL : '符合',DATA : 'Y'},{LABEL : '未符合',DATA : 'N'});
});
