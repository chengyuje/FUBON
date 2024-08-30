/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM511Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM511Controller";
		
		// combobox
		$scope.mappingSet['Type'] = [];
		$scope.mappingSet['Type'].push({LABEL : '客戶經營-KYC',DATA : '1'},{LABEL : 'Advisory-KYC',DATA : '2'});
		
		// date picker
		// 有效起始日期
		$scope.bgn_sDateOptions = {};
		$scope.end_sDateOptions = {};
		// 有效截止日期
		$scope.bgn_eDateOptions = {};
		$scope.end_eDateOptions = {};
		// config
		$scope.altInputFormats = ['M!/d!/yyyy'];
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.bgn_eDate;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.bgn_sDate;
		};
		$scope.limitDate2 = function() {
			$scope.end_sDateOptions.maxDate = $scope.inputVO.end_eDate;
			$scope.end_eDateOptions.minDate = $scope.inputVO.end_sDate;
		};
		// date picker end
		
		// init
		$scope.init = function(){
			$scope.inputVO = {};
			$scope.limitDate();
			$scope.limitDate2();
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.paramList = [];
		}
		$scope.inquireInit();
		
		// inquire
		$scope.inquire = function(){
			$scope.sendRecv("CRM511", "inquire", "com.systex.jbranch.app.server.fps.crm511.CRM511InputVO", $scope.inputVO,
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
							});
							return;
						}
			});
		};
		
		$scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("CRM511", "deleteKYC", "com.systex.jbranch.app.server.fps.crm511.CRM511InputVO", {'qstn_id': row.QSTN_ID},
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
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM511/CRM511_EDIT.html',
				className: 'CRM511',
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
		
		$scope.detail = function (row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM511/CRM511_DETAIL.html',
				className: 'CRM511',
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
		
		$scope.exportData = function() {
			if($scope.paramList.length == 0)
				return;
			$scope.sendRecv("CRM511", "download", "com.systex.jbranch.app.server.fps.crm511.CRM511InputVO", $scope.inputVO,
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
		
		
});