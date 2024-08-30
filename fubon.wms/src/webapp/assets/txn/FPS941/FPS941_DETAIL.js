'use strict';
eSoafApp.controller('FPS941_DETAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS941_DETAILController";
		
		// combobox
		getParameter.XML(["FPS.PLAN_TYPE", "FPS.PLAN_PRD_TYPE"], function(totas) {
			if (totas) {
				$scope.PLAN_TYPE = totas.data[totas.key.indexOf('FPS.PLAN_TYPE')];
				$scope.PRD_TYPE = totas.data[totas.key.indexOf('FPS.PLAN_PRD_TYPE')];
			}
		});
		$scope.FONT = [{"LABEL":"正常", "DATA":"1"}, {"LABEL":"粗體", "DATA":"2"}, {"LABEL":"斜體", "DATA":"3"}];
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
				manualfList: [],
				manualmList: [],
				warningList: [],
				realpictureName: ''
			};
		};
		$scope.init();
		
		$scope.init_detail = function() {
			$scope.sendRecv("FPS941", "init_detail", "com.systex.jbranch.app.server.fps.fps941.FPS941InputVO", {'param_no': $scope.inputVO.param_no},
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0)
							return;
						// TBFPS_RPT_PARA_HEAD
						var para_data = tota[0].body.resultList[0];
						if(para_data.RPT_PIC) {
							var bufView = new Uint8Array(para_data.RPT_PIC);
							var length = bufView.length;
						    var result = '';
						    var addition = Math.pow(2,16)-1;
						    for(var i = 0; i<length; i += addition) {
						    	if(i + addition > length)
						    		addition = length - i;
						    	result += String.fromCharCode.apply(null, bufView.subarray(i,i+addition));
						    }
						    $scope.pictureSrc = "data:image/png;base64,"+btoa(result);
							$scope.inputVO.exipicture = "Y";
							$scope.inputVO.realpictureName = para_data.RPT_PIC_NAME;
						}
						// TBFPS_OTHER_PARA_MANUAL 前言
						$scope.inputVO.manualfList = tota[0].body.resultList2;
						angular.forEach($scope.inputVO.manualfList, function(row) {
							row.set = [];
							row.set.push({LABEL: "修改", DATA: "U"});
							row.set.push({LABEL: "刪除", DATA: "D"});
						});
						// TBFPS_OTHER_PARA_MANUAL 使用指南
						$scope.inputVO.manualmList = tota[0].body.resultList3;
						angular.forEach($scope.inputVO.manualmList, function(row, index) {
							var temp = row.FONT;
							row.FONT = temp.split("#")[0];
							row.FONT_COLOR = "#" + temp.split("#")[1];
							
							row.set = [];
							row.set.push({LABEL: "修改", DATA: "U"});
							row.set.push({LABEL: "刪除", DATA: "D"});
							// 讓之後編輯能快速找到那筆, 定一個ID
							row.ROW_ID = row.FPS_TYPE + (index + 1);
						});
						$scope.refreshList_m();
						// TBFPS_OTHER_PARA_WARNING
						$scope.inputVO.warningList = tota[0].body.resultList4;
						angular.forEach($scope.inputVO.warningList, function(row, index) {
							var temp = row.FONT;
							row.FONT = temp.split("#")[0];
							row.FONT_COLOR = "#" + temp.split("#")[1];
							
							row.set = [];
							row.set.push({LABEL: "修改", DATA: "U"});
							row.set.push({LABEL: "刪除", DATA: "D"});
							// 讓之後編輯能快速找到那筆, 定一個ID
							row.ROW_ID = row.PRD_TYPE + (index + 1);
						});
						$scope.refreshList_w();
					}
			});
		};
		$scope.init_detail();
		
		// ---------------------------客戶理財規劃書-前言-------------------------------
		$scope.action_f = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料？ '}, {size: 'sm'}).then(function() {
						var delIndex = $scope.inputVO.manualfList.indexOf(row);
						$scope.inputVO.manualfList.splice(delIndex, 1);
						$scope.EditIndex_f = null;
						$scope.inputVO.plan_type_f = "";
						$scope.inputVO.content_f = "";
//						$scope.showSuccessMsg('ehl_01_common_004');
		            });
				} else {
					$scope.EditIndex_f = $scope.inputVO.manualfList.indexOf(row);
					$scope.inputVO.plan_type_f = row.FPS_TYPE;
					$scope.inputVO.content_f = row.CONTENT;
				}
				row.cmbAction = "";
			}
		};
		
		$scope.add_f = function() {
			if(!$scope.inputVO.plan_type_f) {
				$scope.showErrorMsg('請選擇理財規劃書類別');
        		return;
			}
			var checkIndex = $scope.inputVO.manualfList.map(function(e) { return e.FPS_TYPE; }).indexOf($scope.inputVO.plan_type_f);
        	if(checkIndex > -1) {
        		$scope.showErrorMsg('該財規劃書類別已存在，請選擇編輯或刪除');
        		return;
        	}
        	if(!$scope.inputVO.content_f) {
        		$scope.showErrorMsg('內容不得為空');
        		return;
        	}
			$scope.inputVO.manualfList.push({'FPS_TYPE': $scope.inputVO.plan_type_f, 'DESC_TYPE': 'F', 'CONTENT': $scope.inputVO.content_f, 'set': [{LABEL: "修改", DATA: "U"}, {LABEL: "刪除", DATA: "D"}]});
			$scope.inputVO.plan_type_f = "";
			$scope.inputVO.content_f = "";
//			$scope.showSuccessMsg('ehl_01_common_004');
		};
		
		$scope.edit_f = function() {
			$scope.inputVO.manualfList[$scope.EditIndex_f].FPS_TYPE = angular.copy($scope.inputVO.plan_type_f);
			$scope.inputVO.manualfList[$scope.EditIndex_f].CONTENT = angular.copy($scope.inputVO.content_f);
			$scope.EditIndex_f = null;
			$scope.inputVO.plan_type_f = "";
			$scope.inputVO.content_f = "";
//			$scope.showSuccessMsg('ehl_01_common_004');
		};
		
		$scope.clear_f = function() {
			$scope.EditIndex_f = null;
			$scope.inputVO.plan_type_f = "";
			$scope.inputVO.content_f = "";
		};
		// ---------------------------客戶理財規劃書-前言-------------------------------
		// ui-sortable原生內建BUG, 官方無解這裡爆破
		$scope.refreshList_m = function() {
			if(!$scope.inputVO.plan_type_m) {
				$scope.filterDataList_m = angular.copy($scope.inputVO.manualmList);
				return;
			}
			if($scope.inputVO.plan_type_m != $scope.EditFPS_TYPE_m) {
				$scope.EditIndex_m = null;
				$scope.inputVO.content_m = "";
			}
			$scope.filterDataList_m = [];
			var groupByFPS_TYPE = _.chain($scope.inputVO.manualmList).groupBy('FPS_TYPE')
	    	.toPairs().map(function (pair) {
	    		return _.zipObject(['FPS_TYPE', 'DATA'], pair); 
	    	}).value();
			// every FPS_TYPE is obj
			angular.forEach(groupByFPS_TYPE, function(group) {
				if(group.FPS_TYPE == $scope.inputVO.plan_type_m)
					$scope.filterDataList_m = group.DATA;
			});
		};
		$scope.generateList_m = function(FINANS) {
			// 清空
			if(FINANS.length == 0) {
				// 非全部
				if($scope.inputVO.plan_type_m)
					$scope.inputVO.manualmList = _.filter($scope.inputVO.manualmList, function(o) { return !(o.FPS_TYPE == $scope.inputVO.plan_type_m); });
				else
					$scope.inputVO.manualmList = [];
			} else {
				var groupByFPS_TYPE = _.chain(FINANS).groupBy('FPS_TYPE')
		    	.toPairs().map(function (pair) {
		    		return _.zipObject(['FPS_TYPE', 'DATA'], pair); 
		    	}).value();
				// every FPS_TYPE is obj
				angular.forEach(groupByFPS_TYPE, function(group) {
					// add back
					$scope.inputVO.manualmList = _.filter($scope.inputVO.manualmList, function(o) { return !(o.FPS_TYPE == group.FPS_TYPE); });
					angular.forEach(group.DATA, function(row, index) {
						row.RANK = index + 1;
						// 讓之後編輯能快速找到那筆, 定一個ID
						row.ROW_ID = row.FPS_TYPE + (index + 1);
						$scope.inputVO.manualmList.push(row);
					});
				});
			}
		};
		
		// 排序用
		$scope.sortableOptions_m = {
			stop: function(e, ui) {
				$scope.generateList_m(ui.item.sortable.droptargetModel);
			},
			disabled: !($scope.Datarow.STATUS == 'S' || $scope.Datarow.STATUS == 'R')
		};
		$scope.action_m = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料？ '}, {size: 'sm'}).then(function() {
						var delIndex = $scope.filterDataList_m.indexOf(row);
						$scope.filterDataList_m.splice(delIndex, 1);
						$scope.EditFPS_TYPE_m = null;
						$scope.EditIndex_m = null;
						$scope.generateList_m($scope.filterDataList_m);
//						$scope.showSuccessMsg('ehl_01_common_004');
		            });
				} else {
					$scope.EditFPS_TYPE_m = angular.copy(row.FPS_TYPE);
					$scope.EditIndex_m = angular.copy(row.ROW_ID);
					$scope.inputVO.plan_type_m = row.FPS_TYPE;
					$scope.inputVO.font_m = row.FONT;
					$scope.inputVO.font_color_m = row.FONT_COLOR;
					$scope.inputVO.content_m = row.CONTENT;
				}
				row.cmbAction = "";
			}
		};
		
		$scope.add_m = function() {
			if(!$scope.inputVO.plan_type_m) {
				$scope.showErrorMsg('請選擇理財規劃書類別');
        		return;
			}
			if(!$scope.inputVO.content_m) {
        		$scope.showErrorMsg('內容不得為空');
        		return;
        	}
			$scope.filterDataList_m.push({'FPS_TYPE': $scope.inputVO.plan_type_m, 'DESC_TYPE': 'M', 'FONT': $scope.inputVO.font_m ? $scope.inputVO.font_m : "1", 'FONT_COLOR': $scope.inputVO.font_color_m ? $scope.inputVO.font_color_m : "#000000", 'RANK': 999, 'CONTENT': $scope.inputVO.content_m, 'set': [{LABEL: "修改", DATA: "U"}, {LABEL: "刪除", DATA: "D"}]});
			$scope.inputVO.content_m = "";
			$scope.generateList_m($scope.filterDataList_m);
//			$scope.showSuccessMsg('ehl_01_common_004');
		};
		
		$scope.edit_m = function() {
			var newIndex = $scope.filterDataList_m.map(function(e) { return e.ROW_ID; }).indexOf($scope.EditIndex_m);
			$scope.filterDataList_m[newIndex].FPS_TYPE = angular.copy($scope.inputVO.plan_type_m);
			$scope.filterDataList_m[newIndex].FONT = $scope.inputVO.font_m ? angular.copy($scope.inputVO.font_m) : "1";
			$scope.filterDataList_m[newIndex].FONT_COLOR = $scope.inputVO.font_color_m ? angular.copy($scope.inputVO.font_color_m) : "#000000";
			$scope.filterDataList_m[newIndex].CONTENT = angular.copy($scope.inputVO.content_m);
			$scope.EditFPS_TYPE_m = null;
			$scope.EditIndex_m = null;
			$scope.inputVO.content_m = "";
			$scope.generateList_m($scope.filterDataList_m);
//			$scope.showSuccessMsg('ehl_01_common_004');
		};
		
		$scope.clear_m = function() {
			$scope.EditFPS_TYPE_m = null;
			$scope.EditIndex_m = null;
			$scope.inputVO.font_m = "1";
			$scope.inputVO.font_color_m = "#000000";
			$scope.inputVO.content_m = "";
		};
		// ---------------------------客戶理財規劃書-前言-------------------------------
		// ui-sortable原生內建BUG, 官方無解這裡爆破
		$scope.refreshList_w = function() {
			if(!$scope.inputVO.prd_type_w) {
				$scope.filterDataList_w = angular.copy($scope.inputVO.warningList);
				return;
			}
			if($scope.inputVO.prd_type_w != $scope.EditPRD_TYPE_w) {
				$scope.EditIndex_w = null;
				$scope.inputVO.content_w = "";
			}
			$scope.filterDataList_w = [];
			var groupByPRD_TYPE = _.chain($scope.inputVO.warningList).groupBy('PRD_TYPE')
	    	.toPairs().map(function (pair) {
	    		return _.zipObject(['PRD_TYPE', 'DATA'], pair); 
	    	}).value();
			// every PRD_TYPE is obj
			angular.forEach(groupByPRD_TYPE, function(group) {
				if(group.PRD_TYPE == $scope.inputVO.prd_type_w)
					$scope.filterDataList_w = group.DATA;
			});
		};
		$scope.generateList_w = function(FINANS) {
			// 清空
			if(FINANS.length == 0) {
				// 非全部
				if($scope.inputVO.prd_type_w)
					$scope.inputVO.warningList = _.filter($scope.inputVO.warningList, function(o) { return !(o.PRD_TYPE == $scope.inputVO.prd_type_w); });
				else
					$scope.inputVO.warningList = [];
			} else {
				var groupByPRD_TYPE = _.chain(FINANS).groupBy('PRD_TYPE')
		    	.toPairs().map(function (pair) {
		    		return _.zipObject(['PRD_TYPE', 'DATA'], pair); 
		    	}).value();
				// every PRD_TYPE is obj
				angular.forEach(groupByPRD_TYPE, function(group) {
					// add back
					$scope.inputVO.warningList = _.filter($scope.inputVO.warningList, function(o) { return !(o.PRD_TYPE == group.PRD_TYPE); });
					angular.forEach(group.DATA, function(row, index) {
						row.RANK = index + 1;
						// 讓之後編輯能快速找到那筆, 定一個ID
						row.ROW_ID = row.PRD_TYPE + (index + 1);
						$scope.inputVO.warningList.push(row);
					});
				});
			}
		};
		
		// 排序用
		$scope.sortableOptions_w = {
			stop: function(e, ui) {
				$scope.generateList_w(ui.item.sortable.droptargetModel);
			},
			disabled: !($scope.Datarow.STATUS == 'S' || $scope.Datarow.STATUS == 'R')
		};
		$scope.action_w = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料？ '}, {size: 'sm'}).then(function() {
						var delIndex = $scope.filterDataList_w.indexOf(row);
						$scope.filterDataList_w.splice(delIndex, 1);
						$scope.EditPRD_TYPE_w = null;
						$scope.EditIndex_w = null;
						$scope.generateList_w($scope.filterDataList_w);
//						$scope.showSuccessMsg('ehl_01_common_004');
		            });
				} else {
					$scope.EditPRD_TYPE_w = angular.copy(row.PRD_TYPE);
					$scope.EditIndex_w = angular.copy(row.ROW_ID);
					$scope.inputVO.prd_type_w = row.PRD_TYPE;
					$scope.inputVO.font_w = row.FONT;
					$scope.inputVO.font_color_w = row.FONT_COLOR;
					$scope.inputVO.content_w = row.WARNING;
				}
				row.cmbAction = "";
			}
		};
		
		$scope.add_w = function() {
			if(!$scope.inputVO.prd_type_w) {
				$scope.showErrorMsg('請選擇商品類別');
        		return;
			}
			if(!$scope.inputVO.content_w) {
        		$scope.showErrorMsg('內容不得為空');
        		return;
        	}
			$scope.filterDataList_w.push({'PRD_TYPE': $scope.inputVO.prd_type_w, 'FONT': $scope.inputVO.font_w ? $scope.inputVO.font_w : '1', 'FONT_COLOR': $scope.inputVO.font_color_w ? $scope.inputVO.font_color_w : "#000000", 'RANK': 999, 'WARNING': $scope.inputVO.content_w, 'set': [{LABEL: "修改", DATA: "U"}, {LABEL: "刪除", DATA: "D"}]});
			$scope.inputVO.content_w = "";
			$scope.generateList_w($scope.filterDataList_w);
//			$scope.showSuccessMsg('ehl_01_common_004');
		};
		
		$scope.edit_w = function() {
			var newIndex = $scope.filterDataList_w.map(function(e) { return e.ROW_ID; }).indexOf($scope.EditIndex_w);
			$scope.filterDataList_w[newIndex].PRD_TYPE = angular.copy($scope.inputVO.prd_type_w);
			$scope.filterDataList_w[newIndex].FONT = $scope.inputVO.font_w ? angular.copy($scope.inputVO.font_w) : '1';
			$scope.filterDataList_w[newIndex].FONT_COLOR = $scope.inputVO.font_color_w ? angular.copy($scope.inputVO.font_color_w) : "#000000";
			$scope.filterDataList_w[newIndex].WARNING = angular.copy($scope.inputVO.content_w);
			$scope.EditPRD_TYPE_w = null;
			$scope.EditIndex_w = null;
			$scope.inputVO.content_w = "";
			$scope.generateList_w($scope.filterDataList_w);
//			$scope.showSuccessMsg('ehl_01_common_004');
		};
		
		$scope.clear_w = function() {
			$scope.EditPRD_TYPE_w = null;
			$scope.EditIndex_w = null;
			$scope.inputVO.font_w = "1";
			$scope.inputVO.font_color_w = "#000000";
			$scope.inputVO.content_w = "";
		};
		// ---------------------------END-------------------------------
        $scope.uploadFinshed = function(name, rname) {
        	$scope.pictureSrc = "";
        	$scope.inputVO.exipicture = "Y";
    		$scope.inputVO.pictureName = name;
        	$scope.inputVO.realpictureName = rname;
        };
        $scope.removePicture = function() {
			$scope.pictureSrc = "";
			$scope.inputVO.exipicture = "";
			$scope.inputVO.pictureName = "";
        	$scope.inputVO.realpictureName = "";
		};
		$scope.is_resizePicture = false;
		$scope.resizePicture = function() {
			if(!$scope.is_resizePicture) {
				$('#fps941_pictureSrc').css({'width' : '100%' , 'height' : '100%'});
				$scope.is_resizePicture = !$scope.is_resizePicture;
			}
			else {
				$('#fps941_pictureSrc').css({'width' : '200px' , 'height' : '200px'});
				$scope.is_resizePicture = !$scope.is_resizePicture;
			}
		};
		
		$scope.save = function () {
			$scope.sendRecv("FPS941", "add", "com.systex.jbranch.app.server.fps.fps941.FPS941InputVO", $scope.inputVO,
				function(totas, isError) {
                	if (!isError) {
                		$scope.showSuccessMsg('ehl_01_common_004');
                		$scope.Datarow.STATUS = 'S';
                	};
				}
    		);
		};
		
		$scope.goReview = function () {
			if(!$scope.inputVO.realpictureName || $scope.inputVO.manualfList.length == 0
					|| $scope.inputVO.manualmList.length == 0 || $scope.inputVO.warningList.length == 0) {
				$scope.showErrorMsg('有漏填或未上傳的區塊');
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
			$confirm({text: '確定傳送主管覆核?'}, {size: 'sm'}).then(function() {
				$scope.sendRecv("FPS941", "goReview", "com.systex.jbranch.app.server.fps.fps941.FPS941InputVO", $scope.inputVO,
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
				$scope.sendRecv("FPS941", "review", "com.systex.jbranch.app.server.fps.fps941.FPS941InputVO", {'param_no': $scope.inputVO.param_no, 'status': status},
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