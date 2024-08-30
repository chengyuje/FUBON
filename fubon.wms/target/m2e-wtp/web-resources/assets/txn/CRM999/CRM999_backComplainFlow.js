/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM999backController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM999backController";

		/**
		 * 字數長度計算
		 */
		$scope.checkLenght = function(type, cloum, limit) {

			var text = $scope.inputVO[type];
			if (!text) {
				$scope[cloum] = limit;
			} else {
				var len = text.length;
				$scope[cloum] = parseInt(limit)-parseInt(len);
			}
		}
		
		
		/*--------------------------------------------------------------------------------------------
		 * 初始化
		 * --------------------------------------------------------------------------------------------
		 */
		
		/*
		 * 載入初始資訊
		 */
		$scope.start = function() {
			$scope.inputVO = angular.copy($scope.preInputVO);
			$scope.checkLenght('backReason', "lenght1", "60");
		}

		$scope.init = function() {
			$scope.start(); //載入初始資訊
		}
		$scope.init();
		
		
		/**
		 * 確認 【退件】按鈕動作
		 */
		$scope.backSubmit = function() {
			/* 將客訴表退回至上一流程 */
			$scope.inputVO.status = '0'; //0表示只儲存  1表示儲存更新handleStep欄位
			
			$scope.sendRecv("CRM999", "back", "com.systex.jbranch.app.server.fps.crm999.CRM999InputVO", $scope.inputVO,
					function(tota, isError) {
				if (!isError) {
					if(tota[0].body.msg == 'Y') {
						$scope.showMsg('客訴資料表退件成功！');
						$scope.closeThisDialog('successful');
					} else {
						//其他錯誤訊息
						if(tota[0].body.errorMsg) {
							$scope.showErrorMsg(tota[0].body.errorMsg);
						}
						
						if(tota[0].body.msg == 'N') {
							$scope.showErrorMsg('客訴資料表退件失敗！');
						}
					}
				}
			});
		}
	    
});