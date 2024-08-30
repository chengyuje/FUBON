/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM822_AccDetailsController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM822_AccDetailsController";
		
		//日曆初始設定
		$scope.bgn_sDateOptions = {
			maxDate: $scope.inputVO.EndDt
		};
		$scope.bgn_eDateOptions = {
			minDate: $scope.inputVO.StartDt
		};
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.EndDt;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.StartDt;
		};
		
		//初始化
		$scope.init = function() {
			$scope.inputVO.cust_id = $scope.cust_id;
			$scope.inputVO.time = '';

			//查詢入帳帳號
			$scope.sendRecv("CRM822", "getAcctNbr", "com.systex.jbranch.app.server.fps.crm822.CRM822InputVO", $scope.inputVO,
					function(tota, isError) {
				if (isError) {
					$scope.showErrorMsg(tota[0].body.msgData);
				}
				
				$scope.mappingSet['account'] = [];
				
				angular.forEach(tota[0].body.resultList, function(row){
					$scope.mappingSet['account'].push({LABEL: row.ACCT_NBR, DATA: row.ACCT_NBR});
				});
			});
		}
		
		$scope.init();
				
		//快速查詢
		$scope.getPeriod = function(days) {
			var nowDate = new Date();
			
			if (days == 7) {			//近一個星期
				var nowDate2 = new Date();
				var preDate1 = nowDate.setDate(nowDate.getDate() - 7);
				$scope.inputVO.StartDt = preDate1;
				$scope.inputVO.EndDt = $scope.nowDate;
			} else if(days == 30) {		//近一個月
				var preDate = nowDate.setMonth(nowDate.getMonth() - 1);
				$scope.inputVO.StartDt = preDate;
				$scope.inputVO.EndDt = $scope.nowDate;
			} else if(days == 90){		//近三個
				var preDate = nowDate.setDate(nowDate.getDate() - 90);
				$scope.inputVO.StartDt = preDate;
				$scope.inputVO.EndDt = $scope.nowDate;
			}
		}
		
		//查詢
		$scope.inquireAccDetails = function() {
			$scope.resultList = [];
			$scope.outputVO = [];
			$scope.sendRecv("CRM822", "inquireAccDetails", "com.systex.jbranch.app.server.fps.crm822.CRM822InputVO", $scope.inputVO,
					function(tota, isError) {
						if(tota[0].body.resultList.length > 0){
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;								
						}else{
							$scope.showMsg("ehl_01_common_009");
							return;
						}				
			});
		}
		
		//清空日期
		$scope.cancel = function(){
			$scope.inputVO.StartDt = '';
			$scope.inputVO.EndDt = '';
			$scope.bgn_sDateOptions.maxDate = '';
			$scope.bgn_eDateOptions.minDate = '';
			$scope.inputVO.time = '';
			$scope.inputVO.account = '';
		}
        
});
