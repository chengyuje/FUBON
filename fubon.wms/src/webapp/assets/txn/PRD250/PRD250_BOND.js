/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD250_BONDController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD250_BONDController";

		// combobox
		getParameter.XML(["COMMON.YES_NO", "FPS.STOCK_BOND_TYPE", "PRD.BOND_CUSTOMER_LEVEL", "PRD.BOND_PROJECT"], function(totas) {
			if (totas) {
				$scope.COM_YN = totas.data[totas.key.indexOf('COMMON.YES_NO')];
				$scope.mappingSet['FPS.STOCK_BOND_TYPE'] = totas.data[totas.key.indexOf('FPS.STOCK_BOND_TYPE')];
				$scope.mappingSet['PRD.BOND_CUSTOMER_LEVEL'] = totas.data[totas.key.indexOf('PRD.BOND_CUSTOMER_LEVEL')];
				$scope.mappingSet['PRD.BOND_PROJECT'] = totas.data[totas.key.indexOf('PRD.BOND_PROJECT')];
			}
		});

		// init
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.checkVO = {};
			$scope.conDis = false;
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.roleList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();

		// inquire
		$scope.currUser = projInfoService.getUserID();
		$scope.inquire = function() {
			// toUpperCase
			if($scope.inputVO.prd_id)
				$scope.inputVO.prd_id = $scope.inputVO.prd_id.toUpperCase();
			$scope.sendRecv("PRD250", "inquire", "com.systex.jbranch.app.server.fps.prd250.PRD250InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.roleList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							$scope.conDis = false;
							// follow mao151
							$scope.connector('set','MAO151_PARAMS',undefined);
							angular.forEach($scope.roleList, function(row, index, objs){
								if(row.REVIEW_STATUS == 'W' && row.CREATOR != $scope.currUser)
									$scope.conDis = true;
								row.set = [];
								row.set.push({LABEL: "修改", DATA: "U"});
								row.set.push({LABEL: "刪除", DATA: "D"});
							});
							return;
						}
			});
		};

		// follow prd230
		if($scope.connector('get','MAO151_PARAMS')!=undefined) {
			if($scope.connector('get','MAO151_PARAMS').PAGE == 'HOME') {
				$scope.inputVO.prd_id = $scope.connector('get','MAO151_PARAMS').PRD_ID;
				$scope.inquire();
			}
		}

		$scope.download = function() {
			$scope.sendRecv("PRD250", "download", "com.systex.jbranch.app.server.fps.prd250.PRD250InputVO", $scope.inputVO,
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		if(totas[0].body.resultList && totas[0].body.resultList.length == 0) {
	                			$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
	                	};
					}
			);
		};

		$scope.upload = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD250/PRD250_BOND_UPLOAD.html',
				className: 'PRD250',
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

		$scope.checkrow = function() {
			if ($scope.checkVO.clickAll) {
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
			var ans = $scope.roleList.filter(function(obj) {
	    		return (obj.SELECTED == true);
	    	});
        	if(ans.length == 0){
        		return;
        	}
			$confirm({text: '是否' + ((status == 'N') ? '退回' : '核可')}, {size: 'sm'}).then(function() {
				$scope.sendRecv("PRD250", "review", "com.systex.jbranch.app.server.fps.prd250.PRD250InputVO", {'review_list': ans,'status': status},
						function(tota, isError) {
							if (isError) {
		                		$scope.showErrorMsg(tota[0].body.msgData);
		                	}
		                	if (tota.length > 0) {
		                		$scope.showMsg((status == 'N') ? "ehl_01_common_020" : "ehl_01_common_021");
		                		$scope.inquireInit();
		    					$scope.inquire();
		                	};
				});
			});
		};

		$scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("PRD250", "deleteData", "com.systex.jbranch.app.server.fps.prd250.PRD250InputVO", {'prd_id': row.PRD_ID},
                				function(totas, isError) {
                                	if (isError) {
                                		$scope.showErrorMsg(totas[0].body.msgData);
                                	}
                                	if (totas.length > 0) {
                                		$scope.showSuccessMsg('刪除成功');
                                		$scope.inquireInit();
                    					$scope.inquire();
                                	};
                				}
                		);
					});
				} else
					$scope.edit(row);
				row.cmbAction = "";
			}
		};

		$scope.edit = function (row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD250/PRD250_BOND_EDIT.html',
				className: 'PRD250',
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

		$scope.uploadTemp = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD250/PRD250_BOND_UPLOAD_TEMP.html',
				className: 'PRD250',
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

		$scope.downloadProject = function(row) {
			$scope.inputVO.downloadParamType = 'PRD.BOND_PROJECT';
			$scope.sendRecv("PRD230", "downloadProject", "com.systex.jbranch.app.server.fps.prd230.PRD230InputVO", $scope.inputVO,
				function(totas, isError) {
					if (isError) {
						$scope.showErrorMsg(totas[0].body.msgData);
					}
					if (totas.length > 0) {
						if(totas[0].body.resultList && totas[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							return;
						}
					};
				}
			);
		};
		$scope.downloadCustomerLevel = function(row) {
			$scope.inputVO.downloadParamType = 'PRD.BOND_CUSTOMER_LEVEL';
			$scope.sendRecv("PRD230", "downloadCustomerLevel", "com.systex.jbranch.app.server.fps.prd230.PRD230InputVO", $scope.inputVO,
				function(totas, isError) {
					if (isError) {
						$scope.showErrorMsg(totas[0].body.msgData);
					}
					if (totas.length > 0) {
						if(totas[0].body.resultList && totas[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							return;
						}
					};
				}
			);
		};

		$scope.uploadProject = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD250/PRD250_BOND_UPLOAD_PROJECT.html',
				className: 'PRD250',
				showClose: false,
				controller: ['$scope', function($scope) {

				}]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful') {
				}
			});
		};
		$scope.uploadCustomerLevel = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD250/PRD250_BOND_UPLOAD_CUSTOMER_LEVEL.html',
				className: 'PRD250',
				showClose: false,
				controller: ['$scope', function($scope) {

				}]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful') {
				}
			});
		};
	});
