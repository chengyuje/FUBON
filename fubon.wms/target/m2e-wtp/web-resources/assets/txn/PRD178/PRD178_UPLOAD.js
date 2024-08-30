/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD178_UPLOADController', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PRD178_UPLOADController";

	$scope.inputVO = {};

	$scope.downloadSimple = function() {
		$scope.sendRecv("PRD178", "downloadSimple", "com.systex.jbranch.app.server.fps.prd178.PRD178InputVO", {}, function(tota, isError) {
			if (!isError) {
				return;
			}
		});
	};

	$scope.uploadFinshed = function(name, rname) {
		$scope.inputVO.fileName = name;
		$scope.inputVO.fileRealName = rname;
	};

	$scope.save = function() {
		$scope.sendRecv("PRD178", "upload", "com.systex.jbranch.app.server.fps.prd178.PRD178InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsg(tota[0].body.msgData);
			}
			if (tota.length > 0) {
				debugger;
				const { PROD_MAIN_notExist, PROD_LIFEITEM_notExist, requiredError, typeError } = tota[0].body;
				if (!(requiredError.length || typeError.length || PROD_LIFEITEM_notExist.length || PROD_MAIN_notExist.length )) {
                    $scope.showSuccessMsg('ehl_01_common_004');
                    $scope.closeThisDialog('successful');
                } else {
                	$scope.showSuccessMsg('部分處理完畢，尚有錯誤的資料將無法儲存！');
                	if (PROD_MAIN_notExist.length)
    					$scope.showErrorMsg('ehl_01_prd178_001', [ PROD_MAIN_notExist ]);
    				if (PROD_LIFEITEM_notExist.length)
    					$scope.showErrorMsg('ehl_01_prd178_002', [ PROD_LIFEITEM_notExist ]);
    				if (requiredError.length)
    					 $scope.showErrorMsg(`下列表格位置為必要欄位：${requiredError.join('、')}`);
    				if (typeError.length)
    					$scope.showErrorMsg(`下列表格位置型別錯誤：${typeError.join('、')}`);
                }			
			}
		});
	};

});