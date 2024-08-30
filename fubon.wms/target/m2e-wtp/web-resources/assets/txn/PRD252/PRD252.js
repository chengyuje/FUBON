/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD252Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD252Controller";
		
		// init
		$scope.init = function() {
			$scope.inputVO = {};
		};
		$scope.init();
		
		$scope.inquireInit = function(){
			$scope.paramList = [];
		}
		$scope.inquireInit();
		
		$scope.inquire = function() {
			$scope.sendRecv("PRD252", "inquire", "com.systex.jbranch.app.server.fps.prd252.PRD252InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						$scope.paramList = tota[0].body.resultList;
						angular.forEach($scope.paramList, function(row, index, objs){
							row.set = [];
							row.set.push({LABEL: "修改", DATA: "U"});
							row.set.push({LABEL: "刪除", DATA: "D"});
						});
					}
			});
		};
		
		$scope.upload = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD252/PRD252_UPLOAD.html',
				className: 'PRD252_UPLOAD',
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
		
		$scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("PRD252", "delete", "com.systex.jbranch.app.server.fps.prd252.PRD252InputVO", {'prdId': row.PRD_ID},
                				function(totas, isError) {
									debugger;
                                	if (isError) {
                                		$scope.showErrorMsg(totas[0].body.msgData);
                                	}
                                	if (totas.length > 0) {
                                		$scope.showSuccessMsg("ehl_01_common_003");
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
//			var ptype = $scope.inputVO.ptype;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD252/PRD252_EDIT.html',
				className: 'PRD252',
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
		

		$scope.download = function(row) {
			$scope.sendRecv("PRD252", "download", "com.systex.jbranch.app.server.fps.prd252.PRD252InputVO", {'prdId': row.PRD_ID},
				function(totas, isError) {
					if (!isError) {
						return;
	            	}
				}
			);
		};
		
});