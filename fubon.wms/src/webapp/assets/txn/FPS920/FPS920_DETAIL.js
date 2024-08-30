'use strict';
eSoafApp.controller('FPS920_DETAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS920_DETAILController";
		
		// combobox
		getParameter.XML(["COMMON.YES_NO"], function(totas) {
			if (totas) {
				$scope.COM_YN = totas.data[totas.key.indexOf('COMMON.YES_NO')];
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
				date: $scope.toJsDate($scope.Datarow.EFFECT_START_DATE)
			};
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.inputVO.totalList = [];
		}
		$scope.inquireInit();
		
		$scope.init_detail = function() {
			$scope.sendRecv("FPS920", "init_detail", "com.systex.jbranch.app.server.fps.fps920.FPS920InputVO", {'param_no': $scope.inputVO.param_no},
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
    						// 財神推薦特定目的項目
    						var temp = [];
    						if(row.FP_EDUCATION_YN == 'Y') temp.push('子女教育規劃');
    						if(row.FP_RETIRE_YN == 'Y') temp.push('退休規劃');
    						if(row.FP_BUYHOUSE_YN == 'Y') temp.push('購屋規劃');
    						if(row.FP_BUYCAR_YN == 'Y') temp.push('購車規劃');
    						if(row.FP_MARRY_YN == 'Y') temp.push('結婚規劃');
    						if(row.FP_OVERSEA_EDUCATION_YN == 'Y') temp.push('留遊學規劃');
    						if(row.FP_TRAVEL_YN == 'Y') temp.push('旅遊規劃');
    						if(row.FP_OTHER_YN == 'Y') temp.push('其他規劃');
    						row.MONEY_GOD = temp.toString();
    						// 讓之後編輯能快速找到那筆, 定一個ID
    						row.ROW_ID = "ROW_ID" + (index + 1);
    					});
					}
			});
		};
		$scope.init_detail();
		
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
				template: 'assets/txn/FPS920/FPS920_EDIT.html',
				className: 'FPS920_EDIT',
				showClose: false,
                controller: ['$scope', function($scope) {
                	
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value != "cancel") {
					var temp = angular.copy(data.value);
					$scope.inputVO.totalList.push({
						'AGE_START': temp.age_start,
						'AGE_END': temp.age_end,
						'CHILD_YN': temp.child_yn,
						'LN_HOUSE_YN': temp.ln_house_yn,
						'FP_EDUCATION_YN': temp.education,
						'FP_RETIRE_YN': temp.retire,
						'FP_BUYHOUSE_YN': temp.buyhouse,
						'FP_BUYCAR_YN': temp.buycar,
						'FP_MARRY_YN': temp.marry,
						'FP_OVERSEA_EDUCATION_YN': temp.ov_education,
						'FP_TRAVEL_YN': temp.travel,
						'FP_OTHER_YN': temp.other,
						'MONEY_GOD': temp.money_god,
						'set': [{LABEL: "修改", DATA: "U"},{LABEL: "刪除", DATA: "D"}]
					});
					// 讓之後編輯能快速找到那筆, 定一個ID
					angular.forEach($scope.inputVO.totalList, function(row, index) {
						row.ROW_ID = "ROW_ID" + (index + 1);
					});
				}
			});
		};
		
		$scope.edit = function (row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/FPS920/FPS920_EDIT.html',
				className: 'FPS920_EDIT',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.Datarow = row;
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value != "cancel") {
					var temp = angular.copy(data.value);
					row.AGE_START = temp.age_start;
					row.AGE_END = temp.age_end;
					row.CHILD_YN = temp.child_yn;
					row.LN_HOUSE_YN = temp.ln_house_yn;
					row.FP_EDUCATION_YN = temp.education;
					row.FP_RETIRE_YN = temp.retire;
					row.FP_BUYHOUSE_YN = temp.buyhouse;
					row.FP_BUYCAR_YN = temp.buycar;
					row.FP_MARRY_YN = temp.marry;
					row.FP_OVERSEA_EDUCATION_YN = temp.ov_education;
					row.FP_TRAVEL_YN = temp.travel;
					row.FP_OTHER_YN = temp.other;
					row.MONEY_GOD = temp.money_god;
				}
			});
		};
		
		$scope.save = function () {
			var check = false; var errorName = '';
			var groups = _.chain($scope.inputVO.totalList).groupBy(function(value) {
				return '未成年子女:' + (value.CHILD_YN == 'Y' ? '有' : '無') + ', 房貸:' + (value.LN_HOUSE_YN == 'Y' ? '有' : '無');
		    }).toPairs().map(function (pair) {
	    		return _.zipObject(['KEY', 'DATA'], pair); 
	    	}).value();
			angular.forEach(groups, function(row) {
				var temps = []; var tempe = [];
				angular.forEach(row.DATA, function(row2) {
					temps.push(parseFloat(row2.AGE_START));
					tempe.push(parseFloat(row2.AGE_END) - 1);
				});
				temps = temps.sort((a, b) => a - b); tempe = tempe.sort((a, b) => a - b);
				for(var k = 1; k < temps.length; k++) {
					if (temps[k] <= tempe[k-1]) {
						check = true;
						errorName = row.KEY;
			            break;
			        }
			    }
			});
			if(check) {
				$scope.showErrorMsg(errorName + '-年齡區間重複');
				return;
			}
			//
			$scope.sendRecv("FPS920", "add", "com.systex.jbranch.app.server.fps.fps920.FPS920InputVO", $scope.inputVO,
				function(totas, isError) {
                	if (!isError) {
                		$scope.showSuccessMsg('ehl_01_common_004');
                		$scope.Datarow.STATUS = 'S';
                	};
				}
    		);
		};
		
		$scope.goReview = function () {
			var init_date = new Date();
			init_date.setHours(0, 0, 0, 0);
			if($scope.inputVO.date <= init_date) {
				$scope.showErrorMsg('生效日不可包含當天及更早的日期');
    			return;
			}
			//
			var check = false; var errorName = '';
			var groups = _.chain($scope.inputVO.totalList).groupBy(function(value) {
				return '未成年子女:' + (value.CHILD_YN == 'Y' ? '有' : '無') + ', 房貸:' + (value.LN_HOUSE_YN == 'Y' ? '有' : '無');
		    }).toPairs().map(function (pair) {
	    		return _.zipObject(['KEY', 'DATA'], pair); 
	    	}).value();
			angular.forEach(groups, function(row) {
				var temps = []; var tempe = [];
				angular.forEach(row.DATA, function(row2) {
					temps.push(parseFloat(row2.AGE_START));
					tempe.push(parseFloat(row2.AGE_END) - 1);
				});
				temps = temps.sort((a, b) => a - b); tempe = tempe.sort((a, b) => a - b);
				for(var k = 1; k < temps.length; k++) {
					if (temps[k] <= tempe[k-1]) {
						check = true;
						errorName = row.KEY;
			            break;
			        }
			    }
			});
			if(check) {
				$scope.showErrorMsg(errorName + '-年齡區間重複');
				return;
			}
			//
			$confirm({text: '確定傳送主管覆核?'}, {size: 'sm'}).then(function() {
				$scope.sendRecv("FPS920", "goReview", "com.systex.jbranch.app.server.fps.fps920.FPS920InputVO", $scope.inputVO,
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
				$scope.sendRecv("FPS920", "review", "com.systex.jbranch.app.server.fps.fps920.FPS920InputVO", {'param_no': $scope.inputVO.param_no, 'status': status},
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