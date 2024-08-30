/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MKT110Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MKT110Controller";
		
		// combobox
		getParameter.XML(["CAM.BULLETIN_TYPE", "MKT.CHANNEL_CODE"], function(totas) {
			if (totas) {
				$scope.BULLETIN_TYPE = totas.data[totas.key.indexOf('CAM.BULLETIN_TYPE')];
				$scope.CHANNEL_CODE = totas.data[totas.key.indexOf('MKT.CHANNEL_CODE')];
			}
		});
		//
		
     	// date picker
        // 活動起迄日
        $scope.sDateOptions = {};
		$scope.eDateOptions = {};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.sDateOptions.maxDate = $scope.inputVO.eDate;
			$scope.eDateOptions.minDate = $scope.inputVO.sDate;
		};
		// date picker end
		
		$scope.nowDate = new Date();
		$scope.nowDate.setHours(0, 0, 0, 0);
		$scope.init = function() {
			$scope.obj = {};
			$scope.inputVO = {};
			$scope.inputVO.pri_id = projInfoService.getPriID()[0];
			$scope.conDis = false;
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.paramList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		$scope.btnExcute = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/MKT111/MKT111.html',
				className: 'MKT111',
				showClose: false,
                controller: ['$scope', function($scope) {
              	  
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.inquireInit();
					$scope.inquire();
				}
			});
		};
		
		$scope.currUser = projInfoService.getUserID();
		$scope.inquire = function() {
			$scope.obj.clickAll = false;
			$scope.sendRecv("MKT110", "inquire", "com.systex.jbranch.app.server.fps.mkt110.MKT110InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							$scope.conDis = false;
							// follow mao151
							$scope.inputVO.seq = null;
							$scope.connector('set','MAO151_PARAMS',undefined);
							//
							angular.forEach($scope.paramList, function(row) {
								if(row.REVIEW_STATUS == 'W' && row.CREATOR != $scope.currUser)
									$scope.conDis = true;
								//
								row.set = [];
								if($scope.nowDate <= $scope.toJsDate(row.E_DATE))
									row.set.push({LABEL: "修改", DATA: "U"});
								row.set.push({LABEL: "刪除", DATA: "D"});
								// 適用人員
								var role = [];
								$scope.chkCode = row.ROLE.split(",");
    							var idx = $scope.chkCode.indexOf('ALL');
    							if (idx > -1) {
    				        		role.push('全部');
    				        	} else {
    				        		angular.forEach($scope.chkCode, function(row2){
    									role.push($filter('mapping')(row2,$scope.CHANNEL_CODE));
    								});
    				        	}
								row.ROLE = role.toString();
							});
						}
			});
		};
		
		// follow mao151
		if($scope.connector('get','MAO151_PARAMS')!=undefined) {
			if($scope.connector('get','MAO151_PARAMS').PAGE == 'HOME') {
				$scope.inputVO.seq = $scope.connector('get','MAO151_PARAMS').SEQ;
				$scope.inquire();
			}
		}
		
		$scope.detail = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/MKT110/MKT110_DETAIL.html',
				className: 'MKT110',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		};
		
		$scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("MKT110", "deleteData", "com.systex.jbranch.app.server.fps.mkt110.MKT110InputVO", {'seq': row.SEQ},
								function(tota, isError) {
									if (!isError) {
										$scope.showSuccessMsg('ehl_01_common_004');
										$scope.inquireInit();
										$scope.inquire();
									}
						});
					});
				} else
					$scope.edit(row);
				row.cmbAction = "";
			}
		};
		
		$scope.edit = function (row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/MKT111/MKT111.html',
				className: 'MKT111',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.inquireInit();
					$scope.inquire();
				}
			});
		};
		
		$scope.checkrow = function() {
        	if ($scope.obj.clickAll) {
        		angular.forEach($scope.data, function(row) {
        			if(row.REVIEW_STATUS == 'W' && row.CREATOR != $scope.currUser)
        				row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.data, function(row) {
        			if(row.REVIEW_STATUS == 'W' && row.CREATOR != $scope.currUser)
        				row.SELECTED = false;
    			});
        	}
        };
        
        $scope.review = function (status) {
			// get select
			var ans = $scope.paramList.filter(function(obj) {
	    		return (obj.SELECTED == true);
	    	});
        	if(ans.length == 0){
        		return;
        	}
			$confirm({text: '是否' + ((status == 'N') ? '退回' : '核可')}, {size: 'sm'}).then(function() {
				$scope.sendRecv("MKT110", "review", "com.systex.jbranch.app.server.fps.mkt110.MKT110InputVO", {'review_list': ans,'status': status},
						function(tota, isError) {
							if (!isError) {
								$scope.showMsg((status == 'N') ? "ehl_01_common_020" : "ehl_01_common_021");
								$scope.inquireInit();
								$scope.inquire();
							}
				});
			});
		};
		
		
});