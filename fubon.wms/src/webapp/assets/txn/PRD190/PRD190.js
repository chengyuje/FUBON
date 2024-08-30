/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD190Controller',
	function($rootScope, $scope, $controller, $confirm,$filter, socketService, ngDialog, projInfoService, getParameter, $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD190Controller";
		
		// combobox
		getParameter.XML(["FPS.PROD_RISK_LEVEL", "FPS.CURRENCY","FPS.STOCK_BOND_TYPE","FPS.PROD_INV_LEVEL","PRD.CORE_TYPE"], function(totas) {
			if (totas) {
				$scope.PROD_RISK_LEVEL = totas.data[totas.key.indexOf('FPS.PROD_RISK_LEVEL')];
				$scope.CURRENCY = totas.data[totas.key.indexOf('FPS.CURRENCY')];
				$scope.STOCK_BOND_TYPE = totas.data[totas.key.indexOf('FPS.STOCK_BOND_TYPE')];
				$scope.PROD_INV_LEVEL = totas.data[totas.key.indexOf('FPS.PROD_INV_LEVEL')];
				$scope.CORE_TYPE = totas.data[totas.key.indexOf('PRD.CORE_TYPE')];
			}
		});
        		
		// init
        $scope.IsMobile = projInfoService.getLoginSourceToken() == 'mobile';
        $scope.inputVO = {};
        $scope.inputVO.type = '1';
		$scope.init = function() {
			debugger;
			var oritype = $scope.inputVO.type; 
			$scope.inputVO = {};
			$scope.inputVO.type = oritype;
			// 2017/2/18 add
			if($scope.cust_id) {
				// for ocean
				$scope.inputVO.cust_id = $scope.cust_id;
			}else if($scope.is910){
				$scope.inputVO.type = '2';
				$scope.inputVO.stock_bond_type = $scope.stock_bond_type;				
			}
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.paramList = [];
			$scope.totalList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
				
		
		$scope.getName = function() {
			var deferred = $q.defer();
			if($scope.inputVO.prd_id) {
				$scope.inputVO.prd_id = $scope.inputVO.prd_id.toUpperCase();
				$scope.sendRecv("PRD300", "checkID", "com.systex.jbranch.app.server.fps.prd300.PRD300InputVO", {'prd_id':$scope.inputVO.prd_id},
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.prd_name) {
									$scope.inputVO.prd_name = tota[0].body.prd_name;
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
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			// toUpperCase
			if($scope.inputVO.cust_id)
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
			if($scope.inputVO.prd_id) {
				$scope.inputVO.prd_id = $scope.inputVO.prd_id.toUpperCase();
				$scope.getName().then(function(data) {
					$scope.reallyInquire();
				});
			} else
				$scope.reallyInquire();
		};
		$scope.reallyInquire = function() {
			$scope.sendRecv("PRD190", "inquire", "com.systex.jbranch.app.server.fps.prd190.PRD190InputVO", $scope.inputVO,
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