'use strict';
eSoafApp.controller('REF900Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$controller('RegionController', {$scope: $scope});
		$scope.controllerName = "REF900Controller";
		
		// combobox
		getParameter.XML(["CAM.REF_PROD", "CAM.REF_STATUS"], function(totas) {
			if (totas) {
				$scope.REF_PROD = totas.data[totas.key.indexOf('CAM.REF_PROD')];
				$scope.REF_STATUS = totas.data[totas.key.indexOf('CAM.REF_STATUS')];
			}
		});
		
		// date picker
		$scope.optionsInit = function() {
			$scope.startDateOptions = {};
			$scope.endDateOptions = {};
		}
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.startDateOptions.maxDate = $scope.inputVO.endDate;
			if ($scope.inputVO.endDate) {
				let y = $scope.inputVO.endDate.getFullYear();
				let m = $scope.inputVO.endDate.getMonth() - 3;
				let d = $scope.inputVO.endDate.getDate();
				$scope.startDateOptions.minDate = new Date(y, m, d);
			}
			$scope.endDateOptions.minDate = $scope.inputVO.startDate;
			if ($scope.inputVO.startDate) {
				let y = $scope.inputVO.startDate.getFullYear();
				let m = $scope.inputVO.startDate.getMonth() + 3;
				let d = $scope.inputVO.startDate.getDate();
				$scope.endDateOptions.maxDate = new Date(y, m, d);
			}
		};
		// date picker end
		
		// init
		$scope.pri_id = projInfoService.getPriID()[0];
		$scope.init = function() {
			$scope.inputVO = {};
			var min_mon = new Date();
			min_mon.setMonth(min_mon.getMonth() - 2);
			min_mon.setHours(0, 0, 0, 0);
			$scope.inputVO.startDate = min_mon;
			var min_mon2 = new Date();
			min_mon2.setHours(0, 0, 0, 0);
			$scope.inputVO.endDate = min_mon2;
			$scope.optionsInit();
			$scope.limitDate();
			// 連動
			$scope.regionOBJ = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
	        $scope.RegionController_setName($scope.regionOBJ);
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.totalList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		// inquire
		$scope.inquire = function() {
			$scope.sendRecv("REF900", "inquire", "com.systex.jbranch.app.server.fps.ref900.REF900InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						// from crm181 first time need
						$scope.inputVO.seq = "";
						//
						$scope.totalList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
						$scope.loginSysRole = tota[0].body.loginSysRole;
						
						angular.forEach($scope.totalList, function(row) {
							var boss_temp = row.SALES_BOSS_EMP_LIST.split(",") || [];
							row.enableCombo = true;
							row.set = [];
							// 判斷USERID是受轉介人且為待接受案件的話就用受轉介人的下拉選單
							if (row.USERID == projInfoService.getUserID() && row.STATUS == 'W') {
								row.set.push({LABEL: "接受", DATA: "Y"});
								row.set.push({LABEL: "不接受", DATA: "A"});
							}
							// 若非受轉人，但權限群組是011/012/013/046 且是待仲裁案件， 用主管的下拉選單
							else if(row.STATUS == 'A' && row.SALES_BOSS == $scope.pri_id && boss_temp.indexOf(projInfoService.getUserID()) > -1) {
								row.set.push({LABEL: "轉介成立", DATA: "Y"});
								row.set.push({LABEL: "轉介不成立", DATA: "C"});
							}
							else
								row.enableCombo = false;
						});
					}
			});
		};
		
		if($scope.connector('get','passParams') != undefined) {
			$scope.inputVO.seq = $scope.connector('get','passParams');
			$scope.connector('set','passParams', null);
			$scope.inquire();
		};
		
		$scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "Y" && row.STATUS == 'W') {
					$confirm({text: '是否接受轉介!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("REF900", "goEffect", "com.systex.jbranch.app.server.fps.ref900.REF900InputVO", {'seq': row.SEQ},
            				function(totas, isError) {
                            	if (!isError) {
                            		$scope.showSuccessMsg('ehl_01_common_004');
                            		$scope.inquireInit();
                            		$scope.inquire();
                            	}
            				}
                		);
					});
				}
				else if(row.cmbAction == "Y" && row.STATUS == 'A') {
					$confirm({text: '是否發回轉介!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("REF900", "goEffect", "com.systex.jbranch.app.server.fps.ref900.REF900InputVO", {'seq': row.SEQ},
            				function(totas, isError) {
                            	if (!isError) {
                            		$scope.showSuccessMsg('ehl_01_common_004');
                            		$scope.inquireInit();
                            		$scope.inquire();
                            	}
            				}
                		);
					});
				}
				else if(row.cmbAction == "A") {
					var dialog = ngDialog.open({
						template: 'assets/txn/REF900/REF900_REASON.html',
						className: 'REF900_REASON',
						showClose: false,
		                controller: ['$scope', function($scope) {
		                	
		                }]
					});
					dialog.closePromise.then(function (data) {
						if(data.value === 'cancel')
							return;
						else {
							$scope.sendRecv("REF900", "backEffect", "com.systex.jbranch.app.server.fps.ref900.REF900InputVO", {'seq': row.SEQ, 'reason': data.value},
	            				function(totas, isError) {
	                            	if (!isError) {
	                            		if(totas[0].body.errorMsg.length > 0)
	                            			$scope.showWarningMsg("該人員無E-mail:"+ totas[0].body.errorMsg);
	                            		if(totas[0].body.errorMsg2.length > 0)
	                            			$scope.showWarningMsg("該人員Email格式錯誤:"+ totas[0].body.errorMsg2);
	                            		$scope.showSuccessMsg('ehl_01_common_004');
	                            		$scope.inquireInit();
	                            		$scope.inquire();
	                            	}
	            				}
	                		);
						}
					});
				} else {
					$confirm({text: '是否刪除!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("REF900", "delEffect", "com.systex.jbranch.app.server.fps.ref900.REF900InputVO", {'seq': row.SEQ},
            				function(totas, isError) {
                            	if (!isError) {
                            		$scope.showSuccessMsg('ehl_01_common_004');
                            		$scope.inquireInit();
                            		$scope.inquire();
                            	}
            				}
                		);
					});
				}
				row.cmbAction = "";
			}
		};
		
		
		
		
});