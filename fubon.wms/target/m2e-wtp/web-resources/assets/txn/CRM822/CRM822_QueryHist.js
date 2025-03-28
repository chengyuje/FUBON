/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM822_QueryHistController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM822_QueryHistController";
		
		//日曆初始設定
//		$scope.bgn_sDateOptions = {
//			maxDate: $scope.inputVO.EndDt
//		};
//		$scope.bgn_eDateOptions = {
//			minDate: $scope.inputVO.StartDt
//		};
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.EndDt;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.StartDt;
			
			if($scope.inputVO.EndDt) {
				var tempDate = new Date($scope.inputVO.EndDt.getFullYear(), $scope.inputVO.EndDt.getMonth(), $scope.inputVO.EndDt.getDate() - 365);				
				$scope.bgn_sDateOptions.minDate = tempDate;

			}
			
			if($scope.inputVO.StartDt) {
				var tempDate = new Date($scope.inputVO.StartDt.getFullYear(), $scope.inputVO.StartDt.getMonth(), $scope.inputVO.StartDt.getDate() + 365)
				$scope.bgn_eDateOptions.maxDate = tempDate;		
			}
		};
		
		//初始化
		$scope.init = function() {
			$scope.inputVO.cust_id = $scope.cust_id;
			$scope.inputVO.time = '';
			var Today = new Date();
			$scope.year1 = Today.getFullYear() + "年度";
			$scope.year2 = Today.getFullYear() - 1 + "年度";
			$scope.year3 = Today.getFullYear() - 2 + "年度";

			//查詢
			$scope.sendRecv("CRM822", "getCustAcct", "com.systex.jbranch.app.server.fps.crm822.CRM822InputVO", $scope.inputVO,
					function(tota, isError) {
				if (isError) {
					$scope.showErrorMsg(tota[0].body.msgData);
				}

				$scope.mappingSet['account'] = [];
				
				angular.forEach(tota[0].body, function(row){
					$scope.mappingSet['account'].push({LABEL: row.TrustAcct.trim(), DATA: row.TrustAcct});
				});
			});
		}
		
		$scope.init();
				
		//設定日期by月
		$scope.getMonth = function(month) {
			var nowDate = new Date();
			if (month == 12) {
				var nowDate2 = new Date();
				var preDate1 = nowDate.setMonth(nowDate.getMonth() - 12);
				var preDate2 = nowDate2.setMonth(nowDate2.getMonth() - 6);
				$scope.inputVO.StartDt = preDate1;
				$scope.inputVO.EndDt = preDate2;
			} else {
				var preDate = nowDate.setMonth(nowDate.getMonth() - month);
				$scope.inputVO.StartDt = preDate;
				$scope.inputVO.EndDt = $scope.nowDate;
			}
		}
		
		//設定日期by年
		$scope.getYear = function(year) {
			var firstDay = new Date();
			firstDay.setMonth(0);
			firstDay.setDate(1);
			var lastDay = new Date();
			lastDay.setMonth(11);
			lastDay.setDate(31);
			
			if(year == 'thisYear') {
				
			} else if(year == 'lastYear') {				
				firstDay.setFullYear(firstDay.getFullYear() - 1);
				lastDay.setFullYear(lastDay.getFullYear() - 1);
			} else if(year == 'beforeLastYear') {				
				firstDay.setFullYear(firstDay.getFullYear() - 2);
				lastDay.setFullYear(lastDay.getFullYear() - 2);
			}
			$scope.inputVO.StartDt = firstDay;
			$scope.inputVO.EndDt = lastDay;
		}
		
		//查詢
		$scope.inquire = function() {
			$scope.resultList = [];
			$scope.outputVO = [];
			$scope.sendRecv("CRM822", "inquire", "com.systex.jbranch.app.server.fps.crm822.CRM822InputVO", $scope.inputVO,
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
			$scope.bgn_sDateOptions.minDate = '';
			$scope.bgn_eDateOptions.maxDate = '';
			$scope.bgn_eDateOptions.minDate = '';
			$scope.inputVO.time = '';
			$scope.inputVO.account = '';
		}
		
		//明細
		$scope.detail = function (row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM822/CRM822_HIST_DETAIL.html',
				className: 'CRM822_HIST_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		};
        
});
