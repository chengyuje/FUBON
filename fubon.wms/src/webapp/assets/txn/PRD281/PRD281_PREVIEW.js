/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD281_PREVIEWController',
	function($scope, $controller, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD281_PREVIEWController";
		
		$scope.getParams = function() {
			// combobox
			getParameter.XML(["PRD.FCI_CURRENCY"], function(totas) {
				if (totas) {
					$scope.FCI_CURRENCY = totas.data[totas.key.indexOf('PRD.FCI_CURRENCY')];
				}
			});
		}
		$scope.getParams();
		
		// init
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.inputVO = $scope.row;
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.paramList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		        
		//取得預覽資料
		$scope.getPreviewData = function() {
			if(!$scope.inputVO.SEQ_NO) return;
						
			$scope.sendRecv("PRD281", "inquirePreview", "com.systex.jbranch.app.server.fps.prd281.PRD281InputVO", {SEQ_NO: $scope.inputVO.SEQ_NO},
				function(tota, isError) {
					if (!isError) {
						$scope.paramList = tota[0].body.resultList;
            			$scope.outputVO = tota[0].body;
					}
			});
		}
		$scope.getPreviewData();		
		
});