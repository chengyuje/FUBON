'use strict';
eSoafApp.controller('FPS910Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "FPS910Controller";
		
		// combobox
		getParameter.XML(["FPS.PARAM_STATUS", "FPS.PORTFOLIO"], function(totas) {
			if (totas) {
				$scope.PARAM_STATUS = totas.data[totas.key.indexOf('FPS.PARAM_STATUS')];
				$scope.PORTFOLIO = totas.data[totas.key.indexOf('FPS.PORTFOLIO')];
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
		if(projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["FPS910"])
			$scope.CanConfirm = projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["FPS910"].FUNCTIONID["confirm"];
		else
			$scope.CanConfirm = false;
		// init
		$scope.inputVO = {};
        $scope.inputVO.setting_type = '2';
		$scope.init = function() {
			var oritype = $scope.inputVO ? $scope.inputVO.setting_type : ''; 
			$scope.inputVO = {};
			$scope.inputVO.setting_type = oritype;
			$scope.inputVO.inv_amt_type = '3';
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.totalList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		$scope.inquire = function() {
			$scope.sendRecv("FPS910", "inquire", "com.systex.jbranch.app.server.fps.fps910.FPS910InputVO", $scope.inputVO,
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
//		$scope.inquire();
		$scope.$on("FPS900_Controller.query1", function(event) {
			$scope.inquire();
		});
		
		$scope.download_main = function() {
			$scope.sendRecv("FPS910", "download_main", "com.systex.jbranch.app.server.fps.fps910.FPS910InputVO", {},
				function(totas, isError) {
                	if (!isError) {
                		
                	};
				}
    		);
		};
		
		$scope.create = function() {
			$confirm({text: '確認建立一個空白主檔'}, {size: 'sm'}).then(function() {
				$scope.sendRecv("FPS910", "create", "com.systex.jbranch.app.server.fps.fps910.FPS910InputVO", $scope.inputVO,
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
		
		$scope.upload = function() {
			var temp = $scope.inputVO.setting_type;
			var dialog = ngDialog.open({
				template: 'assets/txn/FPS910/FPS910_UPLOAD.html',
				className: 'FPS910_UPLOAD',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.home_type = temp;
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
			if($scope.inputVO.setting_type == 1) {
				var dialog = ngDialog.open({
					template: 'assets/txn/FPS910/FPS910_DETAIL_1.html',
					className: 'FPS910_DETAIL_1',
					showClose: false,
	                controller: ['$scope', function($scope) {
	                	$scope.Datarow = row;
	                }]
				});
				dialog.closePromise.then(function (data) {
					$scope.inquireInit();
					$scope.inquire();
				});
			}
			else {
				var dialog = ngDialog.open({
					template: 'assets/txn/FPS910/FPS910_DETAIL_2.html',
					className: 'FPS910_DETAIL_2',
					showClose: false,
	                controller: ['$scope', function($scope) {
	                	$scope.Datarow = row;
	                }]
				});
				dialog.closePromise.then(function (data) {
					$scope.inquireInit();
					$scope.inquire();
				});
			}
		};
		
		$scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("FPS910", "delete", "com.systex.jbranch.app.server.fps.fps910.FPS910InputVO", {'param_no': row.PARAM_NO},
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
						$scope.sendRecv("FPS910", "copy", "com.systex.jbranch.app.server.fps.fps910.FPS910InputVO", {'param_no': row.PARAM_NO},
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