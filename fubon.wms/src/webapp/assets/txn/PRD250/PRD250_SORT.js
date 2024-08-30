'use strict';
eSoafApp.controller('PRD250_SORTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD250_SORTController";
		
		// date picker
		var nowDate = new Date();
		nowDate.setDate(nowDate.getDate() + 1);
		nowDate.setHours(0, 0, 0, 0);
		$scope.dateOptions = {
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
		$scope.init = function() {
			$scope.listBase = [{'rank': 1, 'prd_id': null, 'canEdit': true},{'rank': 2, 'prd_id': null, 'canEdit': true},{'rank': 3, 'prd_id': null, 'canEdit': true},{'rank': 4, 'prd_id': null, 'canEdit': true},{'rank': 5, 'prd_id': null, 'canEdit': true},
								{'rank': 6, 'prd_id': null, 'canEdit': true},{'rank': 7, 'prd_id': null, 'canEdit': true},{'rank': 8, 'prd_id': null, 'canEdit': true},{'rank': 9, 'prd_id': null, 'canEdit': true},{'rank': 10, 'prd_id': null, 'canEdit': true}];
			$scope.sendRecv("PRD250", "getRank", "com.systex.jbranch.app.server.fps.prd250.PRD250InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						var tempDate = [];
						angular.forEach(tota[0].body.resultList3, function(row) {
							tempDate.push($filter('date')($scope.toJsDate(row.EFFECT_DATE), "yyyy/MM/dd"));
						});
						$scope.changeDate = tempDate.toString();
						// current
						if(tota[0].body.resultList.length > 0)
							$scope.currentDate = $filter('date')($scope.toJsDate(tota[0].body.resultList[0].PRD_RANK_DATE), "yyyy/MM/dd");
						angular.forEach(tota[0].body.resultList, function(row) {
							var exiIndex = $scope.listBase.map(function(e) { return e.rank; }).indexOf(row.PRD_RANK);
							if(exiIndex != -1)
								$scope.listBase[exiIndex].old_prd_id = row.PRD_ID;
								$scope.listBase[exiIndex].old_bond_name = row.BOND_CNAME_A;
								$scope.listBase[exiIndex].old_risk_id = row.RISKCATE_ID;
						});
						// wait doing
						angular.forEach(tota[0].body.resultList2, function(row) {
							var exiIndex = $scope.listBase.map(function(e) { return e.rank; }).indexOf(row.PRD_RANK);
							if(exiIndex != -1) {
								$scope.listBase[exiIndex].prd_id = row.PRD_ID;
								$scope.listBase[exiIndex].bond_name = row.BOND_CNAME_A;
								$scope.listBase[exiIndex].risk_id = row.RISKCATE_ID;
							}
						});
						// 初始化的時候ng-change會在CALL一次 沒差
						if(tota[0].body.lastDate)
							$scope.inputVO.date = $scope.toJsDate(tota[0].body.lastDate);
					}
			});
		};
		$scope.init();
		
		$scope.checkID = function(dataRow) {
			if(dataRow.prd_id) {
				$scope.sendRecv("PRD250", "checkID", "com.systex.jbranch.app.server.fps.prd250.PRD250InputVO", {'prd_id':dataRow.prd_id, 'status':'S'},
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.errorMsg != undefined) {
								$scope.showErrorMsg(tota[0].body.errorMsg);
								dataRow.prd_id = undefined;
								dataRow.bond_name = "";
								dataRow.risk_id = "";
								dataRow.canEdit = true;
							} else {
								dataRow.bond_name = tota[0].body.bond_name;
								dataRow.risk_id = tota[0].body.rick_id;
								dataRow.canEdit = tota[0].body.canEdit;								
							}
						}
				});
			} else {
				dataRow.bond_name = "";
				dataRow.risk_id = "";
				dataRow.canEdit = true;
			}
		};
		
		$scope.goPRD130_BOND = function (dataRow) {
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT210/SOT210_ROUTE.html',
				className: 'PRD130',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					$scope.isPop = true;
					$scope.is250 = true;
					$scope.txnName = "海外債搜尋";
	        		$scope.routeURL = 'assets/txn/PRD130/PRD130.html';
	            }]
			}).closePromise.then(function (data) {
				if(data.value != "cancel") {
					dataRow.prd_id = data.value.PRD_ID;
					dataRow.risk_id = data.value.RISKCATE_ID;
					$scope.checkID(dataRow);
				}
			});
		};
		
		$scope.save = function() {
			if(!$scope.inputVO.date) {
				$scope.showErrorMsg('請選擇日期');
				return;
			}
			//
			var data = $scope.listBase.filter(function(row) {
				return row.prd_id;
	    	});
			// ----------------------------------------------------------------------
			if(data.length > 0) {
				var check1 = false;
				var check2 = false;
				angular.forEach(data, function(row) {
					if(!row.canEdit)
						check1 = true;
//					if(row.risk_id == "P1")
//						check2 = true;
				});
				if(check1) {
					$scope.showErrorMsg('尚有錯誤的代碼，請重新輸入。');
					return;
				}
//				if(!check2) {
//					$scope.showErrorMsg('商品風險等級至少需有一個"P1"，請重新輸入。');
//					return;
//				}
				// ----------------------------------------------------------------------
				var uniq = data.map((e) => {
	        	  return {'count': 1, 'prd_id': e.prd_id}
	        	})
	        	.reduce((a, b) => {
	        	  a[b.prd_id] = (a[b.prd_id] || 0) + b.count
	        	  return a
	        	}, {});
	        	var duplicates = Object.keys(uniq).filter((a) => uniq[a] > 1);
	        	if(duplicates.length > 0) {
	        		$scope.showErrorMsg('有重複代碼: ' + duplicates + '，請重新選擇。');
	        		return;
	        	}
	        	// ----------------------------------------------------------------------
	        	$scope.reallySave(data);
			}
			else {
				$confirm({text: '若無填寫任何資料視同清空本日!!'}, {size: 'sm'}).then(function() {
					$scope.reallySave(data);
				});
			}
		};
		$scope.reallySave = function(data) {
			$scope.sendRecv("PRD250", "saveSort", "com.systex.jbranch.app.server.fps.prd250.PRD250InputVO", {'review_list': data,'date': $scope.inputVO.date},
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg('ehl_01_common_004');
						$scope.init();
					}
			});
		};
		
		$scope.clear = function() {
			if(!$scope.inputVO.date) {
				$scope.showErrorMsg('請選擇日期');
				return;
			}
			$confirm({text: '確定清空所選擇之日期？？'}, {size: 'sm'}).then(function() {
				$scope.reallySave([]);
			});
		};
		
		
		
});