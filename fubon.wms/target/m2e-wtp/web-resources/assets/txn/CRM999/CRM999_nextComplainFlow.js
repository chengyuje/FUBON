/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM999nextController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM999nextController";

		/*--------------------------------------------------------------------------------------------
		 * 初始化
		 * --------------------------------------------------------------------------------------------
		 */
		
		/*
		 * 載入初始資訊
		 */
		$scope.start = function() {
			$scope.inputVO = angular.copy($scope.preInputVO);
			
			//轉時間
			$scope.inputVO.fmt_create_date = $scope.toJsDate($scope.inputVO.fmt_create_date);
			$scope.inputVO.fmt_end_date = $scope.toJsDate($scope.inputVO.fmt_end_date);
			$scope.inputVO.birthday = $scope.toJsDate($scope.inputVO.birthday);
			$scope.inputVO.openAccountDate = $scope.toJsDate($scope.inputVO.openAccountDate);
			$scope.inputVO.buyDate = $scope.toJsDate($scope.inputVO.buyDate);
			$scope.inputVO.startDate = $scope.toJsDate($scope.inputVO.startDate);
			$scope.inputVO.endDate = $scope.toJsDate($scope.inputVO.endDate);
			
			//有下一流程才加入，否則顯示無相關資料
			if ($scope.inputVO.allUsr.length) {
				//設定allUserList combo source
				$scope.mappingSet['allUserList'] = [];
				$scope.inputVO.allUsr.map((e)=>{
					$scope.mappingSet['allUserList'].push(
				        {LABEL: e.NAME, DATA: e.PERSON_ID}
					)
				})
			}
		}

		$scope.init = function() {
			$scope.start(); //載入初始資訊
		}
		$scope.init();
		
		/**
		* 儲存此次記錄並更新狀態
		*/	
		$scope.saveSubmit = function() {
			if(!$scope.inputVO.nextEmp) {
				$scope.showErrorMsg('請選擇下一流程處理人員');
				return;
			}
			
			$scope.inputVO.status = '1'; //0表示只儲存  1表示儲存更新handleStep欄位

			$scope.sendRecv("CRM999", "save", "com.systex.jbranch.app.server.fps.crm999.CRM999InputVO", $scope.inputVO,
					function(tota, isError) {
				if (!isError) {
					if(tota[0].body.msg == 'Y') {
						$scope.showMsg('執行成功！');
						$scope.closeThisDialog('successful');
					} else {
						//其他錯誤訊息
						if(tota[0].body.errorMsg) {
							$scope.showErrorMsg(tota[0].body.errorMsg);
						}
						
						if(tota[0].body.msg == 'N') {
							$scope.showErrorMsg('執行失敗！');
						}
					}
				}
			});
		}
});