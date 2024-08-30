'use strict';
eSoafApp.controller('FPS930Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS930Controller";
		
		// combobox
		getParameter.XML(["FPS.PARAM_STATUS"], function(totas) {
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
		if(projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["FPS930"])
			$scope.CanConfirm = projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["FPS930"].FUNCTIONID["confirm"];
		else
			$scope.CanConfirm = false;
		// init
		$scope.init = function() {
			$scope.inputVO = {};
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.totalList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		$scope.inquire = function() {
			$scope.sendRecv("FPS930", "inquire", "com.systex.jbranch.app.server.fps.fps930.FPS930InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						$scope.totalList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
						angular.forEach($scope.totalList, function(row) {
							row.set = [];
							row.set.push({LABEL: "複製", DATA: "C"});
							if(row.STATUS == 'S' || row.STATUS == 'R')
								row.set.push({LABEL: "刪除", DATA: "D"});
						});
					}
			});
		};
//		$scope.$on("FPS900_Controller.query2", function(event) {
//			$scope.inquire();
//		});
		$scope.inquire();
		
		$scope.create = function() {
			$confirm({text: '確認建立一個空白主檔'}, {size: 'sm'}).then(function() {
				$scope.sendRecv("FPS930", "create", "com.systex.jbranch.app.server.fps.fps930.FPS930InputVO", $scope.inputVO,
    				function(totas, isError) {
                    	if (!isError) {
                    		$scope.showSuccessMsg("ehl_01_common_004");
                    		$scope.inquireInit();
                    		$scope.inquire();
                    	};
    				}
        		);
			});
		};
		
		$scope.detail = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/FPS930/FPS930_DETAIL.html',
				className: 'FPS930_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.Datarow = row;
                }]
			});
			dialog.closePromise.then(function (data) {
				$scope.inquireInit();
				$scope.inquire();
			});
		};
		
		$scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("FPS930", "delete", "com.systex.jbranch.app.server.fps.fps930.FPS930InputVO", {'param_no': row.PARAM_NO},
            				function(totas, isError) {
                            	if (!isError) {
                            		$scope.showSuccessMsg("ehl_01_common_003");
                            		$scope.inquireInit();
                            		$scope.inquire();
                            	};
            				}
                		);
					});
				} else if(row.cmbAction == "C") {
					$confirm({text: '是否複製此筆資料!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("FPS930", "copy", "com.systex.jbranch.app.server.fps.fps930.FPS930InputVO", {'param_no': row.PARAM_NO},
            				function(totas, isError) {
                            	if (!isError) {
                            		$scope.showSuccessMsg("ehl_01_common_004");
                            		$scope.inquireInit();
                            		$scope.inquire();
                            	};
            				}
                		);
					});
				}
				row.cmbAction = "";
			}
		};
		
		
		
		
		
		
});