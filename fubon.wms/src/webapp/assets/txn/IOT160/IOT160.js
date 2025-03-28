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
	
	$scope.getChkEmpList = function() {
		$scope.chkEmpList = [];
		$scope.sendRecv("IOT160", "getChkEmpList", "com.systex.jbranch.app.server.fps.iot160.IOT160InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.chkEmpList = tota[0].body.list;
					}
			});
	}
	$scope.getChkEmpList();
	
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
			prevShipMethod_1 : '1', //上一次的送達方式
			batchSeq_1 : $scope.getBatchSeq(),
			date_1 : now,
			empId : sysInfoService.getUserID(),
			chkEmpId : ''
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
		
	//儲存前檢核
	$scope.checkData = function() {
		debugger
		var rtnValue = true;
		if($scope.inputVO.shipMethod_1 == '' || $scope.inputVO.batchSeq_1 == '' || $scope.inputVO.date_1 == '') {
			//送達方式、批次、送達日，為必填欄位
			rtnValue = false;
		}
		var count = 0;
		for(var i = 1; i <=10; i++) {
			debugger
			var thisElement = document.getElementById("batchNbr_" + i);
			if(!thisElement || (thisElement.value.length > 0 && thisElement.value.length < 11)) {
				//分行批號須為11碼
				rtnValue = false;
			} else {
				count++;
			}
		}
		if(count == 0) rtnValue = false;  //沒有填入任何分行批號
		
		return rtnValue;
	}	
	
	$scope.saveData = function() {
		debugger
		var rtnValue = true;
		if($scope.inputVO.shipMethod_1 == '' || $scope.inputVO.batchSeq_1 == '' || $scope.inputVO.date_1 == '') {
			//送達方式、批次、送達日，為必填欄位
			$scope.showErrorMsg("送達方式、批次、送達日，為必填欄位");
			return;
		}
		var count = 0;
		for(var i = 1; i <=10; i++) {
			debugger
			var thisElement = document.getElementById("batchNbr_" + i);
			if(!thisElement || (thisElement.value.length > 0 && thisElement.value.length < 11)) {
				//分行批號須為11碼
				$scope.showErrorMsg("分行批號須為11碼");
				return;
			}
			if(thisElement.value.length == 0) {
				count++;
			}
		}
		if(count == 10) {
			//沒有填入任何分行批號
			$scope.showErrorMsg("請填入批號");
			return;
		}
		
		$scope.sendRecv("IOT160", "saveData", "com.systex.jbranch.app.server.fps.iot160.IOT160InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showMsg(tota[0].body.message);
						$scope.closeThisDialog('successful');
					}
			});
	}
	
	//批號滿12碼，自動跳到下一個tabindex
	$scope.toNextIndex = function(thisIdx, nextIdx) {
		debugger
		var batchNo = eval("$scope.inputVO.batchNbr_" + thisIdx);
		if(batchNo && batchNo.length >= 11) {
			//批次為大寫
			var thisElement = document.getElementById("batchNbr_" + thisIdx);
			thisElement.value = batchNo.toUpperCase();
			//下一個批號onFocus
			var nextElement = document.getElementById("batchNbr_" + nextIdx);
			if(nextElement) nextElement.focus();
		}
	}
	
	$scope.setBatchNo = function(idx, value) {
		if(idx == 1) {
			$scope.inputVO.batchNbr_1 = value;
		} else if(idx == 2) {
			$scope.inputVO.batchNbr_2 = value;
		} else if(idx == 3) {
			$scope.inputVO.batchNbr_3 = value;
		} else if(idx == 4) {
			$scope.inputVO.batchNbr_4 = value;
		} else if(idx == 5) {
			$scope.inputVO.batchNbr_5 = value;
		} else if(idx == 6) {
			$scope.inputVO.batchNbr_6 = value;
		} else if(idx == 7) {
			$scope.inputVO.batchNbr_7 = value;
		} else if(idx == 8) {
			$scope.inputVO.batchNbr_8 = value;
		} else if(idx == 9) {
			$scope.inputVO.batchNbr_9 = value;
		} else if(idx == 10) {
			$scope.inputVO.batchNbr_10 = value;
		}
	}
	
	//將批號都清掉
	$scope.clearOPBatchNo = function() {
		$scope.inputVO.batchNbr_1 = "";
		$scope.inputVO.batchNbr_2 = "";
		$scope.inputVO.batchNbr_3 = "";
		$scope.inputVO.batchNbr_4 = "";
		$scope.inputVO.batchNbr_5 = "";
		$scope.inputVO.batchNbr_6 = "";
		$scope.inputVO.batchNbr_7 = "";
		$scope.inputVO.batchNbr_8 = "";
		$scope.inputVO.batchNbr_9 = "";
		$scope.inputVO.batchNbr_10 = "";
	}
	
	//無紙化查詢
	$scope.inquireNoPaper = function() {
		//取得
		$scope.sendRecv("IOT160", "inquireNoPaper", "com.systex.jbranch.app.server.fps.iot160.IOT160InputVO", {"chkEmpId":$scope.inputVO.chkEmpId},
				function(tota, isError) {
					if (!isError) {
						$scope.clearOPBatchNo(); //將批號都先清掉
						if(tota[0].body.list && tota[0].body.list.length > 0) {
							var count = 1;
							angular.forEach(tota[0].body.list, function(row) {
								if(count <= 10 && row.OP_BATCH_NO) { //只有10個欄位，多的就不放了
									debugger
									$scope.setBatchNo(count, row.OP_BATCH_NO);
									$scope.toNextIndex(count, count+1)
								}
								count++;
							});
							//送達方式
							$scope.inputVO.prevShipMethod_1 = "5"; //避免再觸發$scope.checkData_1();，上面已經清過批號
							$scope.inputVO.shipMethod_1 = "5"; //無紙化
						} else {
							$scope.showErrorMsg("查無無紙化案件");
						}
					}
			});
	}
	
	$scope.checkData_1 = function() {
		debugger
		//送達方式由"無紙化"改為其它，或由其他改為"無紙化"，則需清空前一次批號
		if($scope.inputVO.shipMethod_1 == "5" && $scope.inputVO.prevShipMethod_1 != "5") {
			$scope.clearOPBatchNo(); //將批號先清掉
		} else if($scope.inputVO.shipMethod_1 != "5" && $scope.inputVO.prevShipMethod_1 == "5") {
			$scope.clearOPBatchNo(); //將批號先清掉
		}
		//
		$scope.inputVO.prevShipMethod_1 = $scope.inputVO.shipMethod_1;
	}
	
});