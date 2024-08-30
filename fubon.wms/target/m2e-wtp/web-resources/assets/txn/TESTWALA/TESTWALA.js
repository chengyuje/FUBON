/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('TESTWALAController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "TESTWALAController";
		
		//get first date of week
		function dateInWeek(month,year) {
		    return new Date(year + "-" + month + "-01").getDay()
		}
		
		//get days of month
		function daysInMonth(month,year) {
		    return new Date(year, month, 0).getDate();
		}

		$scope.year = '2018';
		$scope.month = '04';
		$scope.dateList = [];
		
		//put front null date
		for (var i = 0 ; i < dateInWeek($scope.month,$scope.year); i++) {
			$scope.dateList.push({date : '', show_up : false, show_down : false});
		}
		//put date
		for (var i = 0 ; i < daysInMonth($scope.month,$scope.year); i++) {
			$scope.dateList.push({date : i + 1, show_up : false, show_down : false});
		}

		//put back null date
		var date_length = 42 - $scope.dateList.length;
		for (var i = 0 ; i < date_length; i++) {
			$scope.dateList.push({date : '', show_up : false, show_down : false});
		}

		$scope.add_data = function() {
			alert(JSON.stringify($scope.dateList));
		}

		
});
		