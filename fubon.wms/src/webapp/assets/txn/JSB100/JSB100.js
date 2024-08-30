/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('JSB100Controller',
	function($rootScope, $scope, $controller, ngDialog, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "JSB100Controller";
		
		getParameter.XML(["CRM.CRM239_CONTRACT_STATUS"], function(totas) {
			if (totas) {
				$scope.mappingSet['CRM.CONTRACT_ST'] = totas.data[totas.key.indexOf('CRM.CRM239_CONTRACT_STATUS')];
				
			}
		});
		
		// 修改日期
		$scope.sDateOptions = {
        		maxDate: $scope.maxDate,
        		minDate: $scope.minDate
        };
        $scope.eDateOptions = {
        		maxDate: $scope.maxDate,
        		minDate: $scope.minDate
        };
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.sDateOptions.maxDate = $scope.inputVO.user_update_date_e || $scope.maxDate;
			$scope.eDateOptions.minDate = $scope.inputVO.user_update_date_s || $scope.minDate;
		};
		// 修改日期 END
		
		$scope.init = function(){
			$scope.resultList = [];
			$scope.outputVO = [];
			$scope.inputVO = {};
			
			$scope.mappingSet['JSB.UPDATE_STATUS'] = [];
			$scope.mappingSet['JSB.UPDATE_STATUS'].push({LABEL:"修改中" , DATA: 'E'}, 
												 	  	{LABEL:"覆核中" , DATA: 'P'});
		}
	
		$scope.inquire = function() {
			// 保單檔修改功能，查詢條件四擇一，非全部都必輸
			if ($scope.inputVO.cust_id == undefined &&
				$scope.inputVO.ins_id == undefined &&
				$scope.inputVO.policy_nbr == undefined &&
				$scope.inputVO.acceptid == undefined && 
				$scope.inputVO.user_update_date_s == undefined &&
				$scope.inputVO.user_update_date_e == undefined) {
				$scope.showErrorMsg("查詢條件『*』需至少填寫一個。");
				return;
			}
			
			$scope.sendRecv("JSB100", "inquire", "com.systex.jbranch.app.server.fps.jsb100.JSB100InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					$scope.resultList = [];
					$scope.outputVO = [];
					if (tota[0].body.resultList.length > 0) {
						$scope.resultList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
						
					} else {
						$scope.showMsg("ehl_01_common_009");	//查無資料
						return;
					}
				}
			});
		}
		
		$scope.showDTL = function (row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/JSB100/JSB100_DETAIL.html',
				className: 'JSB100',
				showClose: false,
				 controller: ['$scope', function($scope) {
					 	$scope.row = row;
	             }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful' || data.value === 'cancel'){
					$scope.inquire();
				}
			});
	    }
		
		$scope.showLOG = function (row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/JSB100/JSB100_LOG.html',
				className: 'JSB100',
				showClose: false,
				 controller: ['$scope', function($scope) {
					 	$scope.row = row;
	             }]
			});
//			dialog.closePromise.then(function (data) {
//				if(data.value === 'successful' || data.value === 'cancel'){
//					
//				}
//			});
	    }
		
		$scope.init();
});
