/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD210Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD210Controller";
		
		// combobox
		getParameter.XML(["PRD.INS_TYPE", "COMMON.YES_NO"], function(totas) {
			if (totas) {
				$scope.INS_TYPE = totas.data[totas.key.indexOf('PRD.INS_TYPE')];
				$scope.COM_YN = totas.data[totas.key.indexOf('COMMON.YES_NO')];
			}
		});
        //
		
		if(projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["PRD210"])
			$scope.CanConfirm = projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["PRD210"].FUNCTIONID["confirm"];
		else
			$scope.CanConfirm = false;
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
			$scope.sendRecv("PRD210", "inquire", "com.systex.jbranch.app.server.fps.prd210.PRD210InputVO", $scope.inputVO,
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
						angular.forEach($scope.roleList, function(row, index, objs){
							if(row.REVIEW_STATUS == 'W' && row.CREATOR != $scope.currUser)
								$scope.conDis = true;
							row.set = [];
							row.set.push({LABEL: "修改", DATA: "U"});
							row.set.push({LABEL: "刪除", DATA: "D"});
						});
					}
			});
		};
		
		// follow prd230
		if($scope.connector('get','MAO151_PARAMS')!=undefined){
			//擷取HOME參數
			$scope.inputVO.passParams=$scope.connector('get','MAO151_PARAMS').PAGE;
			//查詢首頁近來覆核人員  為Home才會查詢
			if($scope.inputVO.passParams=='HOME')
				$scope.inquire();
		}
		
		$scope.download = function(row) {
			$scope.sendRecv("PRD210", "download", "com.systex.jbranch.app.server.fps.prd210.PRD210InputVO", $scope.inputVO,
				function(totas, isError) {
                	if (!isError) {
                		if(totas[0].body.resultList && totas[0].body.resultList.length == 0)
                			$scope.showErrorMsg("ehl_01_common_009");
                	};
				}
			);
		};
		
		$scope.upload = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD210/PRD210_UPLOAD.html',
				className: 'PRD210',
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
			$confirm({text: '是否' + ((status == 'N') ? '退回' : '核可'),title:'覆核確認'}, {size: 'sm'}).then(function() {
				$scope.sendRecv("PRD210", "review", "com.systex.jbranch.app.server.fps.prd210.PRD210InputVO", {'review_list': ans,'status': status},
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
						$scope.sendRecv("PRD210", "deleteData", "com.systex.jbranch.app.server.fps.prd210.PRD210InputVO", {'prd_id': row.PRD_ID},
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
				template: 'assets/txn/PRD210/PRD210_EDIT.html',
				className: 'PRD210',
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