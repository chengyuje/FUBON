'use strict';
eSoafApp.controller('FPS920Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS920Controller";
		
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
		if(projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["FPS920"])
			$scope.CanConfirm = projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["FPS920"].FUNCTIONID["confirm"];
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
			$scope.sendRecv("FPS920", "inquire", "com.systex.jbranch.app.server.fps.fps920.FPS920InputVO", $scope.inputVO,
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
		$scope.$on("FPS900_Controller.query2", function(event) {
			$scope.inquire();
		});
		
		$scope.upload = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/FPS920/FPS920_UPLOAD.html',
				className: 'FPS920_UPLOAD',
				showClose: false,
                controller: ['$scope', function($scope) {
                	
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful') {
					$scope.inquireInit();
					$scope.inquire();
				}
			});
		};
		
		$scope.detail = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/FPS920/FPS920_DETAIL.html',
				className: 'FPS920_DETAIL',
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
						$scope.sendRecv("FPS920", "delete", "com.systex.jbranch.app.server.fps.fps920.FPS920InputVO", {'param_no': row.PARAM_NO},
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
						$scope.sendRecv("FPS920", "copy", "com.systex.jbranch.app.server.fps.fps920.FPS920InputVO", {'param_no': row.PARAM_NO},
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