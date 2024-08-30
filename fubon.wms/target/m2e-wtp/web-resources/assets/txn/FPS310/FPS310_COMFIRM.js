/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('FPS310_COMFIRM_Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope : $scope});
		$scope.controllerName = "FPS310_COMFIRM_Controller";	
		var signText = {
	      valid: '提醒您：此客戶尚未填寫推介同意書。建議請客戶填寫完成後，本行即可提供完整之商品投資標的規劃。若客戶未簽署推介同意書，則本行所提供之規畫文件將不包含個別商品標的。是否列印推介同意書?',
	      notValid: '提醒您，此客戶無法簽署推介同意書，本行所提供之規畫文件將不包含個別商品標的。是否繼續規劃?'
	    };
		//REC_YN 判斷是否可簽署推介同意書
		$scope.textContent = $scope.custData.REC_YN === 'N' ? signText.notValid : signText.valid
	}
);
