'use strict';
eSoafApp.controller('FPS960Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS960Controller";
		
		// combobox
		getParameter.XML(["FUBONSYS.FAIA_ROLE", "FPS.PARAM_STATUS"], function(totas) {
			if (totas) {
				$scope.PARAM_STATUS = totas.data[totas.key.indexOf('FPS.PARAM_STATUS')];
			}
		});
		// date picker
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		// date picker end
		
		// 判斷主管直接根據有無覆核權限
		if(projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["FPS920"])
			$scope.CanConfirm = projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["FPS920"].FUNCTIONID["confirm"];
		else
			$scope.CanConfirm = false;
		// init
		$scope.init = function() {
			$scope.obj = {};
			$scope.inputVO = {
				setType: '2',
				chkRole: [],
				realfileName: ''
			};
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.obj.clickList = false;
			$scope.totalList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		$scope.inquire = function() {
			$scope.sendRecv("FPS960", "getList", "com.systex.jbranch.app.server.fps.fps960.FPS960InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.totalList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
						angular.forEach($scope.totalList, function(row) {
							row.set = [];
							row.set.push({LABEL: "傳送主管覆核", DATA: "P"});
							row.set.push({LABEL: "刪除", DATA: "D"});
						});
					}
			});
		};
		$scope.inquire();
		
		$scope.typeChange = function() {
			if ($scope.inputVO.setType == "1")
				$scope.inputVO.chkRole = [];
			else {
				$scope.inputVO.fileName = null;
	        	$scope.inputVO.realfileName = null;
			}
		};
		
		$scope.setReport = function () {
			var dialog = ngDialog.open({
				template: 'assets/txn/FPS960/FPS960_SET.html',
				className: 'FPS960_SET',
				showClose: false,
				closeByEscape: false,
                controller: ['$scope', function($scope) {
                	
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'cancel')
					return;
				else
					$scope.inputVO.chkRole = angular.copy(data.value);
			});
		};
		
		$scope.$watchCollection('dataList', function(newNames, oldNames) {
			$scope.obj.clickList = false;
        });
		$scope.checklist = function() {
        	if ($scope.obj.clickList) {
        		angular.forEach($scope.dataList, function(row) {
        			if(row.STATUS == 'P' || row.STATUS == 'W')
        				row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.dataList, function(row) {
        			if(row.STATUS == 'P' || row.STATUS == 'W')
        				row.SELECTED = false;
    			});
        	}
        };
        
        $scope.downloadSimple = function() {
        	$scope.sendRecv("FPS960", "downloadSimple", "com.systex.jbranch.app.server.fps.fps960.FPS960InputVO", {},
				function(tota, isError) {
					if (!isError) {
						
					}
			});
		};
        $scope.uploadFinshed = function(name, rname) {
        	$scope.inputVO.fileName = name;
        	$scope.inputVO.realfileName = rname;
        };
        
        $scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "P") {
					$confirm({text: '是否傳送主管覆核!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("FPS960", "goConfirm", "com.systex.jbranch.app.server.fps.fps960.FPS960InputVO", {'param_no': row.PARAM_NO},
		    				function(totas, isError) {
		    	        		if (!isError) {
		    	        			$scope.showSuccessMsg('ehl_01_common_004');
		    	        			$scope.init();
		    	        			$scope.inquireInit();
		    	        			$scope.inquire();
		    					}
		    				}
		    			);
					});
				} else if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("FPS960", "goDelete", "com.systex.jbranch.app.server.fps.fps960.FPS960InputVO", {'param_no': row.PARAM_NO},
							function(tota, isError) {
								if (!isError) {
									$scope.showSuccessMsg('ehl_01_common_004');
									$scope.init();
		    	        			$scope.inquireInit();
		    	        			$scope.inquire();
								}
						});
					});
				}
				row.cmbAction = "";
			}
		};
        
        $scope.download = function(row) {
			$scope.sendRecv("FPS960", "download", "com.systex.jbranch.app.server.fps.fps960.FPS960InputVO", {'param_no': row.PARAM_NO},
				function(totas, isError) {
	        		if (!isError) {
						
					}
				}
			);
		};
        
		$scope.goAdd = function() {
			if(!$scope.inputVO.date) {
				$scope.showErrorMsg('請輸入生效日');
    			return;
			}
			var init_date = new Date();
			init_date.setHours(0, 0, 0, 0);
			if($scope.inputVO.date <= init_date) {
				$scope.showErrorMsg('生效日不可包含當天及更早的日期');
    			return;
			}
			// 自訂人員
        	if($scope.inputVO.setType == '2') {
        		if($scope.inputVO.chkRole.length == 0) {
        			$scope.showErrorMsg('請至少選擇一個');
        			return;
        		}
        	}
        	// 依上傳名單
        	else {
        		if(!$scope.inputVO.realfileName) {
        			$scope.showErrorMsg('上傳檔案為空');
        			return;
        		}
        	}
        	$scope.sendRecv("FPS960", "goAdd", "com.systex.jbranch.app.server.fps.fps960.FPS960InputVO", $scope.inputVO,
				function(totas, isError) {
	        		if (!isError) {
	        			if(totas[0].body.errorList && totas[0].body.errorList.length > 0)
	            			$scope.showErrorMsg('ehl_01_fps960_001',[totas[0].body.errorList.toString()]);
                		if(totas[0].body.errorList2 && totas[0].body.errorList2.length > 0)
	            			$scope.showErrorMsg('ehl_01_fps960_002',[totas[0].body.errorList2.toString()]);
	        			$scope.showSuccessMsg('ehl_01_common_004');
	        			$scope.init();
	        			$scope.inquireInit();
	        			$scope.inquire();
					}
				}
			);
        };
		
        $scope.review = function(status) {
        	// get select
			var ans = $scope.totalList.filter(function(obj) {
	    		return (obj.SELECTED == true);
	    	});
        	if(ans.length == 0) {
        		$scope.showErrorMsg('至少選擇一筆');
        		return;
        	}
        	if(status == 'W' && ans.length > 1) {
        		$scope.showErrorMsg('覆核一次只需要核可一筆');
        		return;
        	}
        	if(status == 'W') {
				var init_date = new Date();
				init_date.setHours(0, 0, 0, 0);
				if($scope.toJsDate(ans[0].EFFECT_START_DATE) < init_date) {
					$scope.showErrorMsg('主管覆核日不可晚於生效日');
        			return;
				}	
			}
        	//
        	var check = false; var check2 = false;
    		for(var k = 0; k < ans.length; k++) {
    			if((status == 'W' || status == 'R') && ans[k].STATUS != 'P') {
    				check = true;
		            break;
    			}
    			if(status == 'F' && ans[k].STATUS != 'W') {
					check2 = true;
		            break;
		        }
		    }
    		if(check) {
    			$scope.showErrorMsg('已經覆核過的無需再覆核');
    			return;
    		}
    		if(check2) {
    			$scope.showErrorMsg('只有待生效的資料才能失效');
    			return;
    		}
        	//
        	$confirm({text: '是否' + (status == 'R' ? '退回' : status == 'W' ? '核可' : '失效')}, {size: 'sm'}).then(function() {
        		$scope.sendRecv("FPS960", "goReview", "com.systex.jbranch.app.server.fps.fps960.FPS960InputVO", {'review_list': ans,'status': status},
    				function(totas, isError) {
    	        		if (!isError) {
    	        			$scope.showSuccessMsg((status == 'R') ? "ehl_01_common_020" : "ehl_01_common_021");
    	        			$scope.init();
    	        			$scope.inquireInit();
    	        			$scope.inquire();
    					}
    				}
    			);
			});
        };
        
		
		
});