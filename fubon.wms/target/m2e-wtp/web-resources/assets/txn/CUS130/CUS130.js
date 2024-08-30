/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CUS130Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CUS130Controller";
		
		// combobox
		getParameter.XML(["CUS.IVG_TYPE", "CUS.IVG_PLAN_TYPE"], function(totas) {
			if (totas) {
				$scope.IVG_TYPE = totas.data[totas.key.indexOf('CUS.IVG_TYPE')];
				$scope.IVG_PLAN_TYPE = totas.data[totas.key.indexOf('CUS.IVG_PLAN_TYPE')];
			}
		});
		//
		
		// date picker
		$scope.ivgStartDateOptions = {};
		$scope.ivgEndDateOptions = {};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.ivgStartDateOptions.maxDate = $scope.inputVO.ivgEndDate;
			$scope.ivgEndDateOptions.minDate = $scope.inputVO.ivgStartDate;
		};
		// date picker end
		
		// init
		$scope.init = function(){
			$scope.inputVO = {};
			$scope.limitDate();
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.paramList = [];
		}
		$scope.inquireInit();
		
		// inquire
		$scope.inquire = function(){
			$scope.sendRecv("CUS130", "inquire", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							angular.forEach($scope.paramList, function(row, index, objs){
								row.set = [];
								row.set.push({LABEL: "修改", DATA: "U"});
								row.set.push({LABEL: "刪除", DATA: "D"});
								row.set.push({LABEL: "複製計畫", DATA: "C"});
							});
							return;
						}
			});
		};
		
		$scope.detail = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CUS130/CUS130_DETAIL.html',
				className: 'CUS130_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		};
		
		$scope.action = function(row) {
			if(row.cmbAction) {
				if (row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("CUS130", "delete", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", {'seq': row.IVG_PLAN_SEQ},
                				function(totas, isError) {
                                	if (!isError) {
                                		$scope.showSuccessMsg("ehl_01_common_003");
                                		$scope.inquireInit();
                                		$scope.inquire();
                                	}
                				}
                		);
					});
				}
				else if (row.cmbAction == "U") {
					$scope.edit(row,"U");
				}
				else if (row.cmbAction == "C")
					$scope.edit(row,"C");
				row.cmbAction = "";
			}
		};
		
		$scope.edit = function (row,type) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CUS130/CUS130_EDIT.html',
				className: 'CUS130_EDIT',
				showClose: false,
				closeByEscape: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                	$scope.EditType = type;
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
