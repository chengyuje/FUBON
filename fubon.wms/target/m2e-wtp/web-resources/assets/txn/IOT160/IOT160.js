/*
 */
'use strict';
eSoafApp.controller('IOT160Controller', function($scope, $rootScope,$controller, socketService, ngDialog, projInfoService, sysInfoService, $q, $confirm, $filter) {	
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "IOT160Controller";
	
	//批次
	$scope.getBatchSeq = function() {
		var today = new Date();
		var today_year = today.getFullYear();  //西元年份 
		var today_month = today.getMonth()+1;  //一年中的第幾月 
		var today_date = today.getDate();      //一月份中的第幾天
		var now  = new Date(); //系統時間
		
		var time1 = new Date(today_year, today_month-1, today_date, "11", "00", "00");
		if (now.valueOf() <= time1.valueOf()) return "1"; //00:00~11:00 第一批
		
		var time2 = new Date(today_year, today_month-1, today_date, "16", "00", "00");
		if (now.valueOf() <= time2.valueOf()) return "2"; //11:01~16:00 第二批
		
		return "3"; //16:01~23:59 第三批
	}
	
	 // init
	$scope.init = function() {
		var now = new Date();
		debugger
		$scope.inputVO = {
			batchNbr_1 : '',
			batchNbr_2 : '',
			batchNbr_3 : '',
			batchNbr_4 : '',
			batchNbr_5 : '',
			batchNbr_6 : '',
			batchNbr_7 : '',
			batchNbr_8 : '',
			batchNbr_9 : '',
			batchNbr_10 : '',
			shipMethod_1 : '1',
			batchSeq_1 : $scope.getBatchSeq(),
			date_1 : now,
			empId : sysInfoService.getUserID()
		};
	};
	$scope.init();
	
	//判斷是否通過檢核
	$scope.check = function() {
		$scope.check = {
			pass : 'true'
		};
	};
	$scope.check();
	
	// *****日曆的使用*******//
	$scope.altInputFormats = ['M!/d!/yyyy'];
	// 時間
    $scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
	// 日期不可大於今天
	$scope.timesys = new Date();
	$scope.endDateOptions = {				
		maxDate : $scope.timesys
	};
		
	$scope.checkData = function() {
		if($scope.inputVO.shipMethod_1 == '' || $scope.inputVO.batchSeq_1 == '' || $scope.inputVO.date_1 == '') {
			return false;
		} else {
			return true;
		}
	}
	
	$scope.checkData_1 = function() {
		if($scope.inputVO.batchNbr_1 != ''){
			var temp=$scope.inputVO.batchNbr_1.toUpperCase();
			$scope.inputVO.batchNbr_1 = temp;
		}
		
		if ($scope.inputVO.batchNbr_1 != '' && $scope.checkData()) {
			$scope.check.pass = 'true'
		}else {
			$scope.check.pass = 'false'
		}
	}
	
	$scope.checkData_2 = function() {
		if($scope.inputVO.batchNbr_2 != ''){
			var temp=$scope.inputVO.batchNbr_2.toUpperCase();
			$scope.inputVO.batchNbr_2 = temp;
		}

		if ($scope.inputVO.batchNbr_2 != '' && $scope.checkData()) {
			$scope.check.pass = 'true'
		}else {
			$scope.check.pass = 'false'
		}
	}
	
	$scope.checkData_3 = function() {
		if($scope.inputVO.batchNbr_3 != ''){
			var temp=$scope.inputVO.batchNbr_3.toUpperCase();
			$scope.inputVO.batchNbr_3 = temp;
		}

		if ($scope.inputVO.batchNbr_3 != '' && $scope.checkData()) {
			$scope.check.pass = 'true'
		}else {
			$scope.check.pass = 'false'
		}
	}
	
	$scope.checkData_4 = function() {
		if($scope.inputVO.batchNbr_4 != ''){
			var temp=$scope.inputVO.batchNbr_4.toUpperCase();
			$scope.inputVO.batchNbr_4 = temp;
		}

		if ($scope.inputVO.batchNbr_4 != '' && $scope.checkData()) {
			$scope.check.pass = 'true'
		}else {
			$scope.check.pass = 'false'
		}
	}
	
	$scope.checkData_5 = function() {
		if($scope.inputVO.batchNbr_5 != ''){
			var temp=$scope.inputVO.batchNbr_5.toUpperCase();
			$scope.inputVO.batchNbr_5 = temp;
		}

		if ($scope.inputVO.batchNbr_5 != '' && $scope.checkData()) {
			$scope.check.pass = 'true'
		}else {
			$scope.check.pass = 'false'
		}
	}
	
	$scope.checkData_6 = function() {
		if($scope.inputVO.batchNbr_6 != ''){
			var temp=$scope.inputVO.batchNbr_6.toUpperCase();
			$scope.inputVO.batchNbr_6 = temp;
		}

		if ($scope.inputVO.batchNbr_6 != '' && $scope.checkData()) {
			$scope.check.pass = 'true'
		}else {
			$scope.check.pass = 'false'
		}
	}
	
	$scope.checkData_7 = function() {
		if($scope.inputVO.batchNbr_7 != ''){
			var temp=$scope.inputVO.batchNbr_7.toUpperCase();
			$scope.inputVO.batchNbr_7 = temp;
		}

		if ($scope.inputVO.batchNbr_7 != '' && $scope.checkData()) {
			$scope.check.pass = 'true'
		}else {
			$scope.check.pass = 'false'
		}
	}
	
	$scope.checkData_8 = function() {
		if($scope.inputVO.batchNbr_8 != ''){
			var temp=$scope.inputVO.batchNbr_8.toUpperCase();
			$scope.inputVO.batchNbr_8 = temp;
		}

		if ($scope.inputVO.batchNbr_8 != '' && $scope.checkData()) {
			$scope.check.pass = 'true'
		}else {
			$scope.check.pass = 'false'
		}
	}
	
	$scope.checkData_9 = function() {
		if($scope.inputVO.batchNbr_9 != ''){
			var temp=$scope.inputVO.batchNbr_9.toUpperCase();
			$scope.inputVO.batchNbr_9 = temp;
		}

		if ($scope.inputVO.batchNbr_9 != '' && $scope.checkData()) {
			$scope.check.pass = 'true'
		}else {
			$scope.check.pass = 'false'
		}
	}
	
	$scope.checkData_10 = function() {
		if($scope.inputVO.batchNbr_10 != ''){
			var temp=$scope.inputVO.batchNbr_10.toUpperCase();
			$scope.inputVO.batchNbr_10 = temp;
		}

		if ($scope.inputVO.batchNbr_10 != '' && $scope.checkData()) {
			$scope.check.pass = 'true'
		}else {
			$scope.check.pass = 'false'
		}
	}
	
	$scope.saveData = function() {
		$scope.sendRecv("IOT160", "saveData", "com.systex.jbranch.app.server.fps.iot160.IOT160InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showMsg(tota[0].body.message);
						$scope.closeThisDialog('successful');
					}
			})
	}
	
	//批號滿12碼，自動跳到下一個tabindex
	$scope.toNextIndex = function(thisIdx, nextIdx) {
		var batchNo = eval("$scope.inputVO.batchNbr_" + thisIdx);
		if(batchNo && batchNo.length >= 11) {
			var element = document.getElementById("batchNbr_" + nextIdx);
			if(element) element.focus();
			
			switch (thisIdx) {
			case "1":
				checkData_1();
				break;
			case "2":
				checkData_2();
				break;
			case "3":
				checkData_3();
				break;
			case "4":
				checkData_4();
				break;
			case "5":
				checkData_5();
				break;
			case "6":
				checkData_6();
				break;
			case "7":
				checkData_7();
				break;
			case "8":
				checkData_8();
				break;
			case "9":
				checkData_9();
				break;
			}
		}
	}
	
});