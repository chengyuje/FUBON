/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM821_COUPONController',
	function($rootScope, $scope, $controller, $confirm, $filter, socketService, ngDialog, projInfoService,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM821_COUPONController";
		
		$scope.mappingSet['AssetType'] = [];
		$scope.mappingSet['AssetType'].push({LABEL: '單筆', DATA: '0001'},
											{LABEL: '定期定額', DATA: '0002'},
											{LABEL: '定期不定額', DATA: '0003'},
											{LABEL: '定存轉基金', DATA: '0004'},
											{LABEL: '基金套餐', DATA: '0005'});
		getParameter.XML(['SOT.CHANGE_TRADE_SUB_TYPE'], function(totas) {
			if (totas) {
				$scope.mappingSet['SOT.CHANGE_TRADE_SUB_TYPE'] = totas.data[totas.key.indexOf('SOT.CHANGE_TRADE_SUB_TYPE')];
			}
		});
		
		// 最近三年
		$scope.minDate = new Date(new Date().getFullYear()-2,0,1);
		// date picker
		// 有效起始日期-最近三年
		$scope.sDateOptions = {
			minDate: $scope.minDate,
			maxDate: $scope.inputVO.eDate || $scope.nowDate
		};
		//有效結束日期-範圍一年
		$scope.eDateOptions = {
			minDate: $scope.inputVO.sDate || $scope.minDate,
			maxDate: $scope.nowDate
		};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			// 一年
			$scope.sDateOptions.maxDate = $scope.inputVO.eDate || $scope.nowDate;
			if ($scope.inputVO.eDate) {
				var min = new Date($scope.inputVO.eDate.getTime());
				min.setFullYear(min.getFullYear()-1);
				if(min >= $scope.minDate)
					$scope.sDateOptions.minDate = min;
			} else
				$scope.sDateOptions.minDate = $scope.minDate;
			$scope.eDateOptions.minDate = $scope.inputVO.sDate || $scope.minDate;
			if ($scope.inputVO.sDate) {
				var max = new Date($scope.inputVO.sDate.getTime());
				max.setFullYear(max.getFullYear()+1);
				if(max <= $scope.nowDate)
					$scope.eDateOptions.maxDate = max;
			} else
				$scope.eDateOptions.maxDate = $scope.nowDate;
		};
		// date picker end

		$scope.inputVO.cust_id = $scope.cust_id;
		
		$scope.init = function() {
			$scope.inputVO.sDate = undefined;
			$scope.inputVO.eDate = undefined;
		}
		$scope.init();

		$scope.inquireInit = function(){
			$scope.resultList = [];
		}

		$scope.inquire = function() {
//			//查詢期間
//			var sDate = $filter('date')($scope.inputVO.sDate, 'yyyyMMdd');
//			var eDate = $filter('date')($scope.inputVO.eDate, 'yyyyMMdd');
//			//轉成民國年
//			sDate = sDate-19110000;
//			eDate = eDate-19110000;
////			alert(sDate+"~"+eDate);
//			angular.forEach($scope.dataList,function(row,index){				
//				if(row.Strdate >= sDate && row.Strdate <= eDate) {
//					$scope.resultList.push(row);
//				}
//			});
//			
//			if($scope.resultList == null || $scope.resultList.length == 0) {
//				$scope.showMsg("ehl_01_common_009");
//    			return;
//    		}
			
			$scope.sendRecv("CRM821", "inquire_divid", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO",$scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList == null || tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
	            			return;
	            		}
						$scope.resultList = tota[0].body.resultList;
						return;
					}
			});
		}
});
