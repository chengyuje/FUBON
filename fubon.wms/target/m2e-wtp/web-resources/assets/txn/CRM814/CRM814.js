/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */

'use strict';
eSoafApp.controller('CRM814Controller', function($rootScope, $scope, $controller, $confirm, $filter, socketService, ngDialog, projInfoService, getParameter) {
	$controller('BaseController', {
		$scope : $scope
	});
	


	$scope.controllerName = "CRM814Controller";

	// combobox
	getParameter.XML([ "CRM.FC032671_PHS_CATG" ], function(totas) {
		if (totas) {
			$scope.FC032671_PHS_CATG = totas.data[totas.key.indexOf('CRM.FC032671_PHS_CATG')];
		}
	});

	// 初始化
	$scope.init = function() {

		$scope.inputVO.custID = $scope.custVO.CUST_ID;
		
		var myDate = new Date();
		$scope.date1 = new Date(myDate);
		$scope.date2 = (new Date(myDate)).setMonth(myDate.getMonth() - 1);
		$scope.date3 = (new Date(myDate)).setMonth(myDate.getMonth() - 2);
		$scope.date_sDateOptions = {
			maxDate : $scope.maxDate,
			minDate : $scope.minDate
		};
		$scope.date_eDateOptions = {
			maxDate : $scope.maxDate,
			minDate : $scope.minDate
		};
	};
	$scope.init();

	// 初始化
	$scope.inquireInit = function() {
		$scope.resultList = [];
	}
	$scope.inquireInit();

	// Joe-日曆初始設定-start
	$scope.bgn_sDateOptions = {
		maxDate: $scope.inputVO.eCreDate
	};
	$scope.bgn_eDateOptions = {
		minDate: $scope.inputVO.sCreDate
	};
	// config
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	$scope.limitDate = function() {
		$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate;
		$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate;
	};
	// Joe-日曆初始設定-end

	// Joe-日期設定(radioButton)-start
	$scope.getDate_Day = function(day) {
		var nowDate = new Date();
		var preDate = nowDate.setDate(nowDate.getDate() - day);
		$scope.inputVO.sCreDate = preDate;
		$scope.inputVO.eCreDate = $scope.nowDate;
	}

	$scope.getDate_Month = function(month) {
		var nowDate = new Date();
		if (month == 0) {
			var nowDate2 = new Date();
			var preDate1 = nowDate.setMonth(nowDate.getMonth() - 12);
			var preDate2 = nowDate2.setMonth(nowDate2.getMonth() - 6);
			$scope.inputVO.sCreDate = preDate1;
			$scope.inputVO.eCreDate = preDate2;
		} else {
			var preDate = nowDate.setMonth(nowDate.getMonth() - month);
			$scope.inputVO.sCreDate = preDate;
			$scope.inputVO.eCreDate = $scope.nowDate;
		}
	}
	// Joe-日期設定(radioButton)-end

	// Joe-日期設定(button)-start
	$scope.getDate = function(month) {
		var firstDay = new Date();
		var lastDay = new Date();

		if (month == 'thisMonth') {
			// 設定日期為第一天
			firstDay.setDate(1);
			// 將月份移至下個月份
			lastDay.setMonth(lastDay.getMonth() + 1);
			// 設定為下個月份的第一天
			lastDay.setDate(1);
			// 將日期-1為當月的最後一天
			lastDay.setDate(lastDay.getDate() - 1);
		} else if (month == 'lastMonth') {
			// 將月份移至上個月份
			firstDay.setMonth(firstDay.getMonth() - 1);
			// 設定為上個月份的第一天
			firstDay.setDate(1);
			// 取得當月的第一天
			lastDay.setDate(1);
			// 將當月的第一天-1為上個月的最後一天
			lastDay.setDate(lastDay.getDate() - 1);
		} else if (month == 'lastMonths') {
			// 將月份移至上上個月份
			firstDay.setMonth(firstDay.getMonth() - 2);
			// 設定為上上個月份的第一天
			firstDay.setDate(1);
			// 將月份移至上個月份
			lastDay.setMonth(lastDay.getMonth() - 1);
			// 設定為上個月份的第一天
			lastDay.setDate(1);
			// 將上個月份的第一天-1為上上個月的最後一天
			lastDay.setDate(lastDay.getDate() - 1);
		}
		$scope.inputVO.sCreDate = firstDay;
		$scope.inputVO.eCreDate = lastDay;
	}
	// Joe-日期設定(button)-end

	// Joe-清空日期-start
	$scope.cancel = function() {
		$scope.inputVO.sCreDate = '';
		$scope.inputVO.eCreDate = '';
		$scope.inputVO.time = '';
		$scope.inputVO.account = '';
	}
	// Joe-清空日期-end

	// 找分行
	$scope.mappingSet['branchName'] = [];
	$scope.sendRecv("CRM811", "getBranchName", "com.systex.jbranch.app.server.fps.crm811.CRM811InputVO", {}, function(tota, isError) {
		angular.forEach(tota[0].body.resultList, function(row) {
			$scope.mappingSet['branchName'].push({
				LABEL : row.BRANCH_NAME,
				DATA : row.BRANCH_NBR
			});
		});
	});
    //先判斷是否為本行員工
	$scope.sendRecv("CRM814", "checkOnJob", "com.systex.jbranch.app.server.fps.crm814.CRM814InputVO", $scope.inputVO, function(totas, isError) {
		debugger;
		if (isError) {
			$scope.showErrorMsg(totas[0].body.msgData);
		} else {
			debugger;
			if(totas[0].body){
				$scope.showErrorMsg("該客戶為本行員工，不提供交易明細查詢");
			} else {
				debugger;
				// 查詢
				$scope.sendRecv("SOT701", "getCustAcct12", "com.systex.jbranch.app.server.fps.sot701.SOT701InputVO", $scope.inputVO, function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
					}
					$scope.mappingSet['account'] = [];
					angular.forEach(tota[0].body.acct12List, function(row) {
						var branchNum = $filter('filter')($scope.mappingSet['branchName'], {
							DATA : row.branch.substring(0, 3)
						});
						if (branchNum.length > 0) { // 可能會有被消滅的分行
							$scope.mappingSet['account'].push({
								LABEL : branchNum[0].LABEL,
								DATA : row.acctNo
							});
						} else {
							$scope.mappingSet['account'].push({
								LABEL : "",
								DATA : row.acctNo
							});
						}
					});
				});
				
			}
			
		}
	});


	$scope.inquireInit = function() {
		$scope.tsList = [];
		$scope.tcdList = [];
		$scope.fsList = [];
		$scope.fcdList = [];
		$scope.lnList = [];
		$scope.nfList = [];
		$scope.gdList = [];
		$scope.tsListOutputVO = {};
		$scope.tcdListOutputVO = {};
		$scope.fsListOutputVO = {};
		$scope.fcdListOutputVO = {};
		$scope.lnListOutputVO = {};
		$scope.nfListOutputVO = {};
		$scope.gdListOutputVO = {};
	}

	$scope.inquire = function() {
		$scope.inquireInit();

		// 以EB202674電文回傳的TX_TYPE區分產品類別
		// 1- 台幣活存ts
		// 2- 台幣定存tcd
		// 3- 外幣活存fs
		// 4- 外幣定存fcd
		// 5- 放款ln
		// 9,B,F,S- 基金nf
		// G- 黃金存摺gd

		$scope.sendRecv("CRM814", "inquire", "com.systex.jbranch.app.server.fps.crm814.CRM814InputVO", $scope.inputVO, function(totas, isError) {
			if (isError) {
				$scope.showErrorMsg(totas[0].body.msgData);
			} else {
				if (totas[0].body.length == 0) {
					$scope.showMsg("ehl_01_common_009");
					return;
				}
				if (totas[0].body.length > 0) {
					angular.forEach(totas[0].body, function(row) {
						if (row.ACT_DATE != null && row.ACT_DATE != '') {
							if (row.TX_TYPE == 'L' && row.CUR == "TWD") { // 台幣活存ts
								$scope.tsList.push(row);
							}
							if (row.TX_TYPE == 'T' && row.CUR == "TWD") { // 台幣定存tcd
								$scope.tcdList.push(row);
							}
							if (row.TX_TYPE == 'L' && row.CUR != "TWD" ) { // 外幣活存fs
								$scope.fsList.push(row);
							}
							if (row.TX_TYPE == 'T' && row.CUR != "TWD") { // 外幣定存fcd
								$scope.fcdList.push(row);
							}
							if (row.TX_TYPE == '5') { // 放款ln
								$scope.lnList.push(row);
							}
							if (row.TX_TYPE == '9' || row.TX_TYPE == 'B' || row.TX_TYPE == 'F' || row.TX_TYPE == 'S') { // 基金nf
								$scope.nfList.push(row);
							}
							if (row.TX_TYPE == 'G') { // 黃金存摺gd
								$scope.gdList.push(row);
							}
						}
					});

					// 分頁用
					$scope.tsListOutputVO = {
						'data' : $scope.tsList
					};
					$scope.tcdListOutputVO = {
						'data' : $scope.tcdList
					};
					$scope.fsListOutputVO = {
						'data' : $scope.fsList
					};
					$scope.fcdListOutputVO = {
						'data' : $scope.fcdList
					};
					$scope.lnListOutputVO = {
						'data' : $scope.lnList
					};
					$scope.nfListOutputVO = {
						'data' : $scope.nfList
					};
					$scope.gdListOutputVO = {
						'data' : $scope.gdList
					};
				}
			}
		});

	};

});



