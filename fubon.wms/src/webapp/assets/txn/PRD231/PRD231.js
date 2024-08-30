/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD231Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD231Controller";
		
		// combobox
		getParameter.XML(["PRD.MKT_TIER2", "PRD.MKT_TIER3"], function(totas) {
			if (totas) {
				$scope.MKT_TIER2 = totas.data[totas.key.indexOf('PRD.MKT_TIER2')];
				$scope.MKT_TIER3 = totas.data[totas.key.indexOf('PRD.MKT_TIER3')];
			}
		});
		
		// init
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.sendRecv("PRD231", "initMktTier1", "com.systex.jbranch.app.server.fps.prd231.PRD231InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.MKT_TIER1 = tota[0].body.mktTier1List;
					}
			});
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.paramList = [];
		}
		$scope.inquireInit();
		
		// inquire
		$scope.inquire = function() {
			$scope.sendRecv("PRD231", "inquire", "com.systex.jbranch.app.server.fps.prd231.PRD231InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
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
				template: 'assets/txn/PRD231/PRD231_UPLOAD.html',
				className: 'PRD231_UPLOAD',
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
						$scope.sendRecv("PRD231", "delete", "com.systex.jbranch.app.server.fps.prd231.PRD231InputVO", {'global_id': row.GLOBAL_ID},
                				function(totas, isError) {
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
			var ptype = $scope.inputVO.ptype;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD231/PRD231_EDIT.html',
				className: 'PRD231',
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
		
		$scope.changeMktTier1 = function () {
			$scope.sendRecv("PRD231", "changeKmtTier1", "com.systex.jbranch.app.server.fps.prd231.PRD231InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.inputVO.mkt_tier2 = '';
						$scope.MKT_TIER2_LIST = tota[0].body.mktTier2List;
						
						$scope.inputVO.mkt_tier3 = '';
						$scope.MKT_TIER3_LIST = tota[0].body.mktTier3List;
					}
			});
		};		
		
		$scope.changeMktTier2 = function () {
			$scope.sendRecv("PRD231", "changeKmtTier2", "com.systex.jbranch.app.server.fps.prd231.PRD231InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.inputVO.mkt_tier3 = '';
						$scope.MKT_TIER3_LIST = tota[0].body.mktTier3List;
					}
			});
		};
		
		$scope.download = function(row) {
			$scope.sendRecv("PRD231", "download", "com.systex.jbranch.app.server.fps.prd231.PRD231InputVO", $scope.inputVO,
				function(totas, isError) {
					if (!isError) {
						
	            	}
				}
			);
		};
		
});