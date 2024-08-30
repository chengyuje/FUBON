/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD110_OVSPRIController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD110_OVSPRIController";

		// initialize
        $scope.init = function() {
        	$scope.inputVO.PRD_ID = "";
        	$scope.inputVO.FUND_NAME = "";
        		
			$scope.mappingSet['OVSPRI_PRD_CATEGORY'] = [];
			$scope.mappingSet['OVSPRI_PRD_CATEGORY'].push({LABEL: "申購", DATA: "1"});
			$scope.mappingSet['OVSPRI_PRD_CATEGORY'].push({LABEL: "贖回", DATA: "2"});
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.paramList = [];
			$scope.totalList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();	
		
		//取得商品名稱
		$scope.getName = function() {
			var deferred = $q.defer();
			if($scope.inputVO.PRD_ID) {
				$scope.inputVO.PRD_ID = $scope.inputVO.PRD_ID.toUpperCase();
				$scope.sendRecv("PRD110", "getFundName", "com.systex.jbranch.app.server.fps.prd110.PRD110InputVO", {'fund_id':$scope.inputVO.PRD_ID},
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.fund_name) {
									$scope.inputVO.FUND_NAME = tota[0].body.fund_name;
								}
								deferred.resolve();
							}
				});
			} else
				deferred.resolve();
			return deferred.promise;
		};
		
		// inquire
		$scope.inquire = function() {
			if($scope.inputVO.FUND_NAME)
				$scope.inputVO.FUND_NAME = $scope.inputVO.FUND_NAME.toUpperCase();
			if($scope.inputVO.PRD_ID) {
				$scope.inputVO.PRD_ID = $scope.inputVO.PRD_ID.toUpperCase();
				$scope.getName().then(function(data) {
					$scope.reallyInquire();
				});
			} else
				$scope.reallyInquire();
		};
		$scope.reallyInquire = function() {
			$scope.inputVO.validDateYN = "Y";
			$scope.sendRecv("PRD235", "inquire", "com.systex.jbranch.app.server.fps.prd235.PRD235InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.totalList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							return;
						}
			});
		};
});
	