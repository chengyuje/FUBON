'use strict';
eSoafApp.controller('PRD210_SORTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD210_SORTController";
		
		// date picker
		var nowDate = new Date();
		nowDate.setDate(nowDate.getDate() + 1);
		nowDate.setHours(0, 0, 0, 0);
//		$scope.dateOptions = {
//			minDate: nowDate
//		};
		$scope.date2Options = {
			minDate: nowDate
		};
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		// date picker end
		
		$scope.inputVO = {};
		// 儲蓄型保險
//		$scope.init = function() {
//			$scope.listBase = [{'rank': 1, 'prd_id': null, 'canEdit': true},{'rank': 2, 'prd_id': null, 'canEdit': true},{'rank': 3, 'prd_id': null, 'canEdit': true},{'rank': 4, 'prd_id': null, 'canEdit': true},{'rank': 5, 'prd_id': null, 'canEdit': true},
//								{'rank': 6, 'prd_id': null, 'canEdit': true},{'rank': 7, 'prd_id': null, 'canEdit': true},{'rank': 8, 'prd_id': null, 'canEdit': true},{'rank': 9, 'prd_id': null, 'canEdit': true},{'rank': 10, 'prd_id': null, 'canEdit': true}];
//			$scope.sendRecv("PRD210", "getRank", "com.systex.jbranch.app.server.fps.prd210.PRD210InputVO", $scope.inputVO,
//				function(tota, isError) {
//					if (!isError) {
//						var tempDate = [];
//						angular.forEach(tota[0].body.resultList3, function(row) {
//							tempDate.push($filter('date')($scope.toJsDate(row.EFFECT_DATE), "yyyy/MM/dd"));
//						});
//						$scope.changeDate = tempDate.toString();
//						// current
//						if(tota[0].body.resultList.length > 0)
//							$scope.currentDate = $filter('date')($scope.toJsDate(tota[0].body.resultList[0].PRD_RANK_DATE), "yyyy/MM/dd");
//						angular.forEach(tota[0].body.resultList, function(row) {
//							var exiIndex = $scope.listBase.map(function(e) { return e.rank; }).indexOf(row.PRD_RANK);
//							if(exiIndex != -1) {
//								$scope.listBase[exiIndex].old_prd_id = row.PRD_ID;
//								$scope.listBase[exiIndex].old_ins_name = row.INSPRD_NAME;
//							}
//						});
//						// wait doing
//						angular.forEach(tota[0].body.resultList2, function(row) {
//							var exiIndex = $scope.listBase.map(function(e) { return e.rank; }).indexOf(row.PRD_RANK);
//							if(exiIndex != -1) {
//								$scope.listBase[exiIndex].prd_id = row.PRD_ID;
//								$scope.listBase[exiIndex].ins_name = row.INSPRD_NAME;
//							}
//						});
//						// 初始化的時候ng-change會在CALL一次 沒差
//						if(tota[0].body.lastDate)
//							$scope.inputVO.date = $scope.toJsDate(tota[0].body.lastDate);
//					}
//			});
//		};
		// 投資型保險
		$scope.init2 = function() {
			$scope.listBase2 = [{'rank': 1, 'prd_id': null, 'canEdit': true},{'rank': 2, 'prd_id': null, 'canEdit': true},{'rank': 3, 'prd_id': null, 'canEdit': true},{'rank': 4, 'prd_id': null, 'canEdit': true},{'rank': 5, 'prd_id': null, 'canEdit': true},
								{'rank': 6, 'prd_id': null, 'canEdit': true},{'rank': 7, 'prd_id': null, 'canEdit': true},{'rank': 8, 'prd_id': null, 'canEdit': true},{'rank': 9, 'prd_id': null, 'canEdit': true},{'rank': 10, 'prd_id': null, 'canEdit': true}];
			$scope.sendRecv("PRD210", "getRank2", "com.systex.jbranch.app.server.fps.prd210.PRD210InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						var tempDate = [];
						angular.forEach(tota[0].body.resultList3, function(row) {
							tempDate.push($filter('date')($scope.toJsDate(row.EFFECT_DATE), "yyyy/MM/dd"));
						});
						$scope.changeDate2 = tempDate.toString();
						// current
						if(tota[0].body.resultList.length > 0)
							$scope.currentDate2 = $filter('date')($scope.toJsDate(tota[0].body.resultList[0].PRD_RANK_DATE), "yyyy/MM/dd");
						angular.forEach(tota[0].body.resultList, function(row) {
							var exiIndex = $scope.listBase2.map(function(e) { return e.rank; }).indexOf(row.PRD_RANK);
							if(exiIndex != -1) {
								$scope.listBase2[exiIndex].old_prd_id = row.PRD_ID;
								$scope.listBase2[exiIndex].old_ins_name = row.INSPRD_NAME;
								// TBPRD_RANK_INS_TARGET
								$scope.listBase2[exiIndex].old_fund_list = row.fund_list;
							}
						});
						// wait doing
						angular.forEach(tota[0].body.resultList2, function(row) {
							var exiIndex = $scope.listBase2.map(function(e) { return e.rank; }).indexOf(row.PRD_RANK);
							if(exiIndex != -1) {
								$scope.listBase2[exiIndex].prd_id = row.PRD_ID;
								$scope.listBase2[exiIndex].ins_name = row.INSPRD_NAME;
								// TBPRD_RANK_INS_TARGET
								$scope.listBase2[exiIndex].fund_list = row.fund_list;
							}
						});
						// 初始化的時候ng-change會在CALL一次 沒差
						if(tota[0].body.lastDate)
							$scope.inputVO.date2 = $scope.toJsDate(tota[0].body.lastDate);
					}
			});
		};
//		$scope.init();
		$scope.init2();
		
		$scope.checkID = function(dataRow, ins_type) {
			if(dataRow.prd_id) {
				$scope.sendRecv("PRD210", "checkID", "com.systex.jbranch.app.server.fps.prd210.PRD210InputVO", {'prd_id':dataRow.prd_id, 'status':'S', 'ins_type': ins_type},
					function(tota, isError) {
						if (!isError) {
							dataRow.ins_name = tota[0].body.ins_name;
							dataRow.fund_list = [];
							dataRow.canEdit = tota[0].body.canEdit;
						}
				});
			} else {
				dataRow.ins_name = "";
				dataRow.fund_list = [];
				dataRow.canEdit = true;
			}
		};
		
		$scope.goPRD160 = function (dataRow, ins_type) {
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT210/SOT210_ROUTE.html',
				className: 'PRD160',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					$scope.isPop = true;
					$scope.is910 = true;
					$scope.is910_ins_type = ins_type;
					$scope.txnName = "保險搜尋";
					$scope.routeURL = 'assets/txn/PRD160/PRD160.html';
	            }]
			}).closePromise.then(function (data) {
				if(data.value != "cancel") {
					dataRow.prd_id = data.value.PRD_ID;
					$scope.checkID(dataRow, ins_type);
				}
			});
		};
		
		$scope.openFUND = function (dataRow) {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD210/PRD210_FUND.html',
				className: 'PRD210_FUND',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					$scope.dataRow = dataRow;
	            }]
			}).closePromise.then(function (data) {
				if(data.value != "cancel") {
					dataRow.fund_list = angular.copy(data.value);
				}
			});
		};
		
		function check_list(data, msgType) {
			var msg = null;
			angular.forEach(data, function(row) {
				if(!row.canEdit)
					msg = '尚有錯誤的代碼，請重新輸入。';
				if(msgType == '2' && (!row.fund_list || row.fund_list.length == 0))
					msg = '請選擇標的代號。';
			});
			// ----------------------------------------------------------------------
			var uniq = data.map((e) => {
        	  return {'count': 1, 'prd_id': e.prd_id}
        	})
        	.reduce((a, b) => {
        	  a[b.prd_id] = (a[b.prd_id] || 0) + b.count
        	  return a
        	}, {});
        	var duplicates = Object.keys(uniq).filter((a) => uniq[a] > 1);
        	if(duplicates.length > 0)
        		msg = '有重複代碼: ' + duplicates + '，請重新選擇。';
			
			return msg;
		};
		$scope.save = function() {
//			var msg = null;
//			var data = $scope.listBase.filter(function(row) {
//				return row.prd_id;
//	    	});
//			if(data.length > 0)
//				msg = check_list(data, '1');
			//
			var msg2 = null;
			var data2 = $scope.listBase2.filter(function(row) {
				return row.prd_id;
	    	});
			if(data2.length > 0)
				msg2 = check_list(data2, '2');
			//
//			if((data.length > 0 && !$scope.inputVO.date) || (data2.length > 0 && !$scope.inputVO.date2)) {
			if(data2.length > 0 && !$scope.inputVO.date2) {
				$scope.showErrorMsg('請選擇日期');
				return;
			}
			//
//			if(msg || msg2) {
			if(msg2) {
//				if(msg) $scope.showErrorMsg(msg);
				if(msg2) $scope.showErrorMsg(msg2);
				return;
			}
			//
//			if(($scope.inputVO.date && data.length == 0) || ($scope.inputVO.date2 && data2.length == 0)) {
			if($scope.inputVO.date2 && data2.length == 0) {
				$confirm({text: '若無填寫任何資料視同清空本日!!'}, {size: 'sm'}).then(function() {
					$scope.reallySave(data2);
//					$scope.reallySave(data, data2);
				});
			} else
				$scope.reallySave(data2);
//				$scope.reallySave(data, data2);
		};
		
//		$scope.reallySave = function(data, data2) {
		$scope.reallySave = function(data2) {
//			$scope.sendRecv("PRD210", "saveSort", "com.systex.jbranch.app.server.fps.prd210.PRD210InputVO", {'review_list': data, 'review_list2': data2, 'date': $scope.inputVO.date, 'date2': $scope.inputVO.date2},
			$scope.sendRecv("PRD210", "saveSort", "com.systex.jbranch.app.server.fps.prd210.PRD210InputVO", 
				{'review_list2': data2, 'date2': $scope.inputVO.date2},
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg('ehl_01_common_004');
//						$scope.init();
						$scope.init2();
					}
			});
		};
		
		$scope.clear = function() {
//			if(!$scope.inputVO.date || !$scope.inputVO.date2) {
			if(!$scope.inputVO.date2) {
				$scope.showErrorMsg('請選擇日期');
				return;
			}
			$confirm({text: '確定清空所選擇之日期？'}, {size: 'sm'}).then(function() {
				$scope.reallySave([]);
//				$scope.reallySave([], []);
			});
		};
		
		
		
});