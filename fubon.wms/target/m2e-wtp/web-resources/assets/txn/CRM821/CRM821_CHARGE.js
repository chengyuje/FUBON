/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM821_CHARGEController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM821_CHARGEController";
		
		$scope.mappingSet['AssetType'] = [];
		$scope.mappingSet['AssetType'].push({LABEL: '單筆', DATA: '0001'},
											{LABEL: '定期定額', DATA: '0002'},
											{LABEL: '定期不定額', DATA: '0003'},
											{LABEL: '定存轉基金', DATA: '0004'},
											{LABEL: '基金套餐', DATA: '0005'});

		$scope.resultList = $scope.row;
		$scope.resultList = _.filter($scope.resultList, function(o) { return o.Status == '0'; });
		$scope.inputVO.cust_id = $scope.cust_id;
		//
		var monobj = {};
		angular.forEach($scope.resultList, function(row) {
			// date
			row.all_day = [];
			row.all_day.push(row.TransferDate01);row.all_day.push(row.TransferDate02);row.all_day.push(row.TransferDate03);
			row.all_day.push(row.TransferDate04);row.all_day.push(row.TransferDate05);row.all_day.push(row.TransferDate06);
			row.all_day = _.compact(row.all_day);
			// 每次申購金額
			var everyAmt = row.AssetType == '0002' ? row.TransferAmt : row.TransferAmt_L;
			angular.forEach(row.all_day , function(row2) {
				monobj[row2] ? monobj[row2] = monobj[row2] + everyAmt : monobj[row2] = everyAmt; 
			});
		});
		$scope.MON_DATA = Object.keys(monobj).sort();
		
		var groupByCurCode = _.chain($scope.resultList).groupBy('CurCode')
    	.toPairs().map(function (pair) {
    		return _.zipObject(['CurCode', 'DATA'], pair); 
    	}).value();
		// every CurCode is obj
		$scope.COUNT_DATA = [];
		angular.forEach(groupByCurCode, function(group) {
			var obj2 = {};
			angular.forEach(group.DATA, function(row) {
				// date
				var all_day = [];
				all_day.push(row.TransferDate01);all_day.push(row.TransferDate02);all_day.push(row.TransferDate03);
				all_day.push(row.TransferDate04);all_day.push(row.TransferDate05);all_day.push(row.TransferDate06);
				all_day = _.compact(all_day);
				// 每次申購金額
				var everyAmt = row.AssetType == '0002' ? row.TransferAmt : row.TransferAmt_L;
				angular.forEach(all_day , function(row2) {
					obj2[row2] ? obj2[row2] = obj2[row2] + everyAmt : obj2[row2] = everyAmt; 
				});
			});
			$scope.COUNT_DATA.push({'CURCODE': group.CurCode, 'TOTAL': _.values(obj2).reduce(function(a, b) { return a + b; }, 0), 'MONOBJ': obj2});
		});
		//
		
		$scope.getSum = function(list, key) {
			if(list == null) 
				return;
			
			var sum = 0;
			for (var i = 0; i < list.length; i++ ) {
				sum += Number(list[i][key]);
			}
			return Number(sum);
		}

});
