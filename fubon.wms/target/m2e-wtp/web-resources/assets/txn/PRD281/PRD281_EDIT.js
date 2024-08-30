/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD281_EDITController',
	function($scope, $controller) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD281_EDITController";
		
		// date picker
		$scope.dateOptions = {};
	    //config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			var today = new Date();
			$scope.dateOptions.minDate = new Date(today.getFullYear(), today.getMonth(), today.getDate() + 1);
		};
		$scope.limitDate();
		
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.inputVO = $scope.row;
			
			$scope.mappingSet['TARGET_CURR_ID'] = [];
		}
		$scope.init();		
		        
		//取得商品資料
		$scope.getPrdData = function() {
			if(!$scope.inputVO.SEQ_NO) return;
						
			$scope.sendRecv("PRD281", "inquire", "com.systex.jbranch.app.server.fps.prd281.PRD281InputVO", {SEQ_NO: $scope.inputVO.SEQ_NO},
				function(tota, isError) {
					if (!isError) {
						$scope.inputVO = tota[0].body.resultList[0];
						$scope.inputVO.EFFECTIVE_DATE = $scope.toJsDate($scope.inputVO.EFFECTIVE_DATE);
						$scope.currIdChange();
					}
			});
		}
		$scope.getPrdData();		
		
		$scope.save = function() {
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			
			$scope.inputVO.saveType = "1";
			$scope.sendRecv("PRD281", "save", "com.systex.jbranch.app.server.fps.prd281.PRD281InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg('ehl_01_common_004');
                   		$scope.closeThisDialog('successful');
					}
			});
		};
		
		$scope.currIdChange = function() {
			if(!$scope.inputVO.CURR_ID) {
				$scope.inputVO.TARGET_CURR_ID = "";
				$scope.mappingSet['TARGET_CURR_ID'] = [];
				return;
			}
			
			$scope.sendRecv("PRD281", "inquireTargetCurrs", "com.systex.jbranch.app.server.fps.prd281.PRD281InputVO", {CURR_ID: $scope.inputVO.CURR_ID},
				function(tota, isError) {
					if (!isError) {
						$scope.mappingSet['TARGET_CURR_ID'] = tota[0].body.resultList;
					}
			});
		}
});