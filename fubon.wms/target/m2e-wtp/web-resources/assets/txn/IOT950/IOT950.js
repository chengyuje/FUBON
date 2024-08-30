/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT950Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "IOT950Controller";
		
		//檢查是否配置比例相加為100
		$scope.checkPercentage = function() {
			if($scope.inputVO.INVESTList.length > 0) {
				var sum = 0;
				for(var a = 0; a<$scope.inputVO.INVESTList.length; a++){
					if($scope.inputVO.INVESTList[a].ALLOCATION_RATIO != undefined 
							&& $scope.inputVO.INVESTList[a].ALLOCATION_RATIO != null
							&& $scope.inputVO.INVESTList[a].ALLOCATION_RATIO != '') {
						if($scope.inputVO.INVESTList[a].ALLOCATION_RATIO != '') {
							if($scope.inputVO.INVESTList[a].ALLOCATION_RATIO < 5) {
								return false
								break;
							} else {
								sum += Number($scope.inputVO.INVESTList[a].ALLOCATION_RATIO);
							}
						}
					}
				}
				if(sum != 100) {
					return false;
				} else {
					return true;
				}
			} else {
				return false;
			}
		}

		//確定，回傳配置比例資料
		$scope.returnInvListData = function() {
			debugger
			//檢查是否配置比例相加為100
			if(!$scope.checkPercentage()) {
				$scope.showErrorMsg("ehl_01_iot120_008");
				return;
			}			
			
			//回傳配置比例資料
			$scope.closeThisDialog($scope.inputVO.INVESTList);
		}
       		
		$scope.init = function() {
			debugger
			$scope.inputVO = $scope.inputVO;	
		}
		$scope.init();
				
	}
);