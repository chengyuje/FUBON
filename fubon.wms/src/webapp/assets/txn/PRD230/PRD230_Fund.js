/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD230_FundController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD230_FundController";
		
		// combobox
		getParameter.XML(["PRD.FUND_VIGILANT","COMMON.YES_NO", "FPS.STOCK_BOND_TYPE", "PRD.FUND_ALERT", "PRD.FUND_SUBJECT", "PRD.FUND_PROJECT", "PRD.FUND_CUSTOMER_LEVEL"], function(totas) {
			if (totas) {
				$scope.mappingSet['PRD.FUND_VIGILANT'] = totas.data[totas.key.indexOf('PRD.FUND_VIGILANT')];
				$scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
				$scope.mappingSet['FPS.STOCK_BOND_TYPE'] = totas.data[totas.key.indexOf('FPS.STOCK_BOND_TYPE')];
				$scope.mappingSet['PRD.FUND_ALERT'] = totas.data[totas.key.indexOf('PRD.FUND_ALERT')];
				$scope.mappingSet['PRD.FUND_SUBJECT'] = totas.data[totas.key.indexOf('PRD.FUND_SUBJECT')];
				$scope.mappingSet['PRD.FUND_PROJECT'] = totas.data[totas.key.indexOf('PRD.FUND_PROJECT')];
				$scope.mappingSet['PRD.FUND_CUSTOMER_LEVEL'] = totas.data[totas.key.indexOf('PRD.FUND_CUSTOMER_LEVEL')];
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
		$scope.roleListConfirm = 0;
		$scope.inquire = function() {
			// toUpperCase
			if($scope.inputVO.prd_id)
				$scope.inputVO.prd_id = $scope.inputVO.prd_id.toUpperCase();
			$scope.roleListConfirm = 0;
			$scope.sendRecv("PRD230", "inquire", "com.systex.jbranch.app.server.fps.prd230.PRD230InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.conDis = false;
							$scope.checkVO.clickAll = false;
							$scope.checkVO.clickAll2 = false;
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.roleList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							// follow mao151
							$scope.inputVO.passParams='';
							$scope.connector('set','MAO151_PARAMS',undefined);
							//
							angular.forEach($scope.roleList, function(row) {
								if(row.REVIEW_STATUS == 'W' && row.CREATOR != $scope.currUser) {
									$scope.conDis = true;
									$scope.roleListConfirm++;
								}	
								row.set = [];
								row.set.push({LABEL: "修改", DATA: "U"});
								row.set.push({LABEL: "刪除", DATA: "D"});
							});
						}
			});
		};
		
		/*****首頁登入判斷覆核查詢*****/   
		if($scope.connector('get','MAO151_PARAMS')!=undefined){
			//擷取HOME參數
			$scope.inputVO.passParams=$scope.connector('get','MAO151_PARAMS').PAGE;
			//查詢首頁近來覆核人員  為Home才會查詢
			if($scope.inputVO.passParams=='HOME')
				$scope.inquire();
		}
		
		$scope.download = function(row) {
			$scope.sendRecv("PRD230", "download", "com.systex.jbranch.app.server.fps.prd230.PRD230InputVO", $scope.inputVO,
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
		
		$scope.downloadWarning = function(row) {
			$scope.sendRecv("PRD230", "downloadWarning", "com.systex.jbranch.app.server.fps.prd230.PRD230InputVO", $scope.inputVO,
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
		$scope.downloadSubject = function(row) {
			$scope.sendRecv("PRD230", "downloadSubject", "com.systex.jbranch.app.server.fps.prd230.PRD230InputVO", $scope.inputVO,
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
		$scope.downloadProject = function(row) {
			$scope.inputVO.downloadParamType = 'PRD.FUND_PROJECT';
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
			$scope.inputVO.downloadParamType = 'PRD.FUND_CUSTOMER_LEVEL';
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
		
		$scope.upload = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD230/PRD230_Fund_UPLOAD.html',
				className: 'PRD230',
				showClose: false,
                controller: ['$scope', function($scope) {
                	
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful') {
					if($scope.inputVO.prd_id && $scope.inputVO.prd_id.length > 1) {
						$scope.inquireInit();
						$scope.inquire();
					}	
				}
			});
		};
		
		$scope.uploadWarning = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD230/PRD230_Fund_UPLOADWARNING.html',
				className: 'PRD230',
				showClose: false,
                controller: ['$scope', function($scope) {
                	
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful') {
				}
			});
		};
		$scope.uploadSubject = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD230/PRD230_Fund_UPLOAD_SUBJECT.html',
				className: 'PRD230',
				showClose: false,
                controller: ['$scope', function($scope) {
                	
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful') {
				}
			});
		};
		$scope.uploadProject = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD230/PRD230_Fund_UPLOAD_PROJECT.html',
				className: 'PRD230',
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
				template: 'assets/txn/PRD230/PRD230_Fund_UPLOAD_CUSTOMER_LEVEL.html',
				className: 'PRD230',
				showClose: false,
                controller: ['$scope', function($scope) {
                	
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful') {
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
        $scope.checkrow2 = function() {
        	if ($scope.checkVO.clickAll2) {
        		angular.forEach($scope.roleList, function(row) {
        			if(row.REVIEW_STATUS == 'W' && row.CREATOR != $scope.currUser)
        				row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.roleList, function(row) {
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
				$scope.sendRecv("PRD230", "review", "com.systex.jbranch.app.server.fps.prd230.PRD230InputVO", {'review_list': ans,'status': status},
						function(tota, isError) {
							if (isError) {
		                		$scope.showErrorMsg(tota[0].body.msgData);
		                	}
		                	if (tota.length > 0) {
		                		$scope.showMsg((status == 'N') ? "ehl_01_common_020" : "ehl_01_common_021");
		                	};
		                	$scope.inquireInit();
							$scope.inquire();
				});
			});
		};
		
		$scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("PRD230", "deleteData", "com.systex.jbranch.app.server.fps.prd230.PRD230InputVO", {'prd_id': row.PRD_ID},
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
				template: 'assets/txn/PRD230/PRD230_Fund_EDIT.html',
				className: 'PRD230',
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
});