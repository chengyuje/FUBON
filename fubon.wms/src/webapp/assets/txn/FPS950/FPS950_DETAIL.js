'use strict';
eSoafApp.controller('FPS950_DETAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS950_DETAILController";
		
		// combobox
		getParameter.XML(["FPS.TREND_TYPE", "PRD.MKT_TIER3"], function(totas) {
			if (totas) {
				$scope.TREND_TYPE = totas.data[totas.key.indexOf('FPS.TREND_TYPE')];
				$scope.MKT_TIER3 = totas.data[totas.key.indexOf('PRD.MKT_TIER3')];
			}
		});
		// date picker
		$scope.dateOptions = {
			minDate: $scope.nowDate
		};
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		// date picker end
		
		// init
		$scope.currUser = projInfoService.getUserID();
		$scope.init = function() {
			$scope.inputVO = {
				param_no: $scope.Datarow.PARAM_NO,
				date: $scope.toJsDate($scope.Datarow.EFFECT_START_DATE),
				market_overview: $scope.Datarow.MARKET_OVERVIEW,
				totalList: []
			};
			//
			$scope.sendRecv("FPS950", "init_detail", "com.systex.jbranch.app.server.fps.fps950.FPS950InputVO", {'param_no': $scope.inputVO.param_no},
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
	            			return;
	            		}
						$scope.inputVO.totalList = tota[0].body.resultList;
						angular.forEach($scope.inputVO.totalList, function(row, index) {
    						row.set = [];
    						row.set.push({LABEL: "修改", DATA: "U"});
    						row.set.push({LABEL: "刪除", DATA: "D"});
    						// 讓之後編輯能快速找到那筆, 定一個ID
							row.ROW_ID = row.TYPE + ":" + (index + 1);
    					});
					}
			});
		};
		$scope.init();
		
		$scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
						var delIndex = $scope.inputVO.totalList.map(function(e) { return e.ROW_ID; }).indexOf(row.ROW_ID);
						$scope.inputVO.totalList.splice(delIndex, 1);
					});
				} else
					$scope.edit(row);
				row.cmbAction = "";
			}
		};
		
		$scope.add = function () {
			var dialog = ngDialog.open({
				template: 'assets/txn/FPS950/FPS950_EDIT.html',
				className: 'FPS950_EDIT',
				showClose: false,
                controller: ['$scope', function($scope) {
                	
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value != "cancel") {
					var temp = angular.copy(data.value);
					$scope.inputVO.totalList.push({'TYPE': temp.type, 'TREND': temp.trend, 'OVERVIEW': temp.overview, 'set': [{LABEL: "修改", DATA: "U"},{LABEL: "刪除", DATA: "D"}]});
					angular.forEach($scope.inputVO.totalList, function(row, index) {
						// 讓之後編輯能快速找到那筆, 定一個ID
						row.ROW_ID = row.TYPE + ":" + (index + 1);
					});
					$scope.inputVO.totalList = _.orderBy($scope.inputVO.totalList, ['TYPE']);
				}
			});
		};
		
		$scope.edit = function (row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/FPS950/FPS950_EDIT.html',
				className: 'FPS950_EDIT',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.Datarow = row;
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value != "cancel") {
					var temp = angular.copy(data.value);
					row.TYPE = temp.type;
					row.TREND = temp.trend;
					row.OVERVIEW = temp.overview;
					$scope.inputVO.totalList = _.orderBy($scope.inputVO.totalList, ['TYPE']);
				}
			});
		};
		
		$scope.save = function () {
			if($scope.inputVO.totalList.length == 0) {
				$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
			}
			//
			var uniq = $scope.inputVO.totalList.map((e) => {
        	  return {'count': 1, 'TYPE': e.TYPE}
        	})
        	.reduce((a, b) => {
        	  a[b.TYPE] = (a[b.TYPE] || 0) + b.count
        	  return a
        	}, {});
        	var duplicates = Object.keys(uniq).filter((a) => uniq[a] > 1);
        	if(duplicates.length > 0) {
        		$scope.showErrorMsg('有重複代碼: ' + duplicates + '，請重新選擇。');
        		return;
        	}
			//
			$scope.sendRecv("FPS950", "add", "com.systex.jbranch.app.server.fps.fps950.FPS950InputVO", $scope.inputVO,
				function(totas, isError) {
                	if (!isError) {
                		$scope.showSuccessMsg('ehl_01_common_004');
                		$scope.Datarow.STATUS = 'S';
                	};
				}
    		);
		};
		
		$scope.goReview = function () {
			if($scope.inputVO.totalList.length == 0) {
				$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
			}
			//
			var init_date = new Date();
			init_date.setHours(0, 0, 0, 0);
			if($scope.inputVO.date <= init_date) {
				$scope.showErrorMsg('生效日不可包含當天及更早的日期');
    			return;
			}
			//
			var uniq = $scope.inputVO.totalList.map((e) => {
        	  return {'count': 1, 'TYPE': e.TYPE}
        	})
        	.reduce((a, b) => {
        	  a[b.TYPE] = (a[b.TYPE] || 0) + b.count
        	  return a
        	}, {});
        	var duplicates = Object.keys(uniq).filter((a) => uniq[a] > 1);
        	if(duplicates.length > 0) {
        		$scope.showErrorMsg('有重複代碼: ' + duplicates + '，請重新選擇。');
        		return;
        	}
			//
			$confirm({text: '確定傳送主管覆核?'}, {size: 'sm'}).then(function() {
				$scope.sendRecv("FPS950", "goReview", "com.systex.jbranch.app.server.fps.fps950.FPS950InputVO", $scope.inputVO,
					function(totas, isError) {
	                	if (!isError) {
	                		$scope.showSuccessMsg('ehl_01_common_004');
	                		$scope.closeThisDialog('successful');
	                	};
					}
	    		);
			});
		};
		
		$scope.review = function (status) {
			if(status == 'W') {
				var init_date = new Date();
				init_date.setHours(0, 0, 0, 0);
				if($scope.toJsDate($scope.Datarow.EFFECT_START_DATE) < init_date) {
					$scope.showErrorMsg('主管覆核日不可晚於生效日');
        			return;
				}	
			}
			$confirm({text: '是否' + (status == 'R' ? '退回' : status == 'W' ? '核可' : '失效')}, {size: 'sm'}).then(function() {
				$scope.sendRecv("FPS950", "review", "com.systex.jbranch.app.server.fps.fps950.FPS950InputVO", {'param_no': $scope.inputVO.param_no, 'status': status},
					function(totas, isError) {
	                	if (!isError) {
	                		$scope.showSuccessMsg((status == 'R') ? "ehl_01_common_020" : "ehl_01_common_021");
	                		$scope.closeThisDialog('successful');
	                	};
					}
	    		);
			});
		};
		
		
});