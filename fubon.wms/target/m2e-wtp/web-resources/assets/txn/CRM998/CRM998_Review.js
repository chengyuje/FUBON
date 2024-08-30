/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM998_ReviewController', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, $q, getParameter, $filter, validateService, $timeout) {
	$controller('BaseController', {
		$scope : $scope
	});
	$controller('RegionController', {
		$scope : $scope
	});

	$scope.controllerName = 'CRM998_ReviewController';

	// 初始化
	$scope.init = function() {
		$scope.inputVO.custId = '';
		$scope.inputVO.centerId = '';
		$scope.inputVO.areaId = '';
		$scope.inputVO.braNbr = '';
		$scope.inputVO.aoCode = '';
		$scope.inputVO.apprStatus = 'W';
		$scope.inputVO.applDateStart = null;
		$scope.inputVO.applDateEnd = null;
	};
	$scope.init();
	
	//組織初始化
	$scope.orgInit = function() {
		$scope.regionParams = [ 'Y', $scope.inputVO, 'centerId', 'REGION_LIST', 'areaId', 'AREA_LIST', 'braNbr', 'BRANCH_LIST', 'aoCode', 'AO_LIST', 'emp_id', 'EMP_LIST'];
		$scope.RegionController_setName($scope.regionParams);		
	}
	$scope.orgInit();

	$scope.clear = function() {
		$scope.apprData = '';
		$scope.paramList = '';
		$scope.outputVO = [];
	}
	
	// e-combobox
	getParameter.XML(['CRM.APPROVAL_STATUS','CRM.VIP_DEGREE'], function(totas) {
		if (totas) {
			$scope.APPR_STATUS = totas.data[totas.key.indexOf('CRM.APPROVAL_STATUS')];
			$scope.VIP_DEGREE = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
		}
	});

	//查詢覆核資料
	$scope.getApprData = function() {
		$scope.sendRecv('CRM998', 'inquireApprovalData', 'com.systex.jbranch.app.server.fps.crm998.CRM998InputVO', $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if (tota[0].body.resultList.length) {
					$scope.apprData = tota[0].body.resultList;
				} else {
					$scope.showMsg('ehl_01_common_009');
				}
				$scope.outputVO = tota[0].body;
			} else {
				$scope.showMsg('ehl_01_common_024');
			}
		});
	}
	
	//切換頁面的時候進行查詢
	$scope.$watch('doQry', function(newValue, oldValue) {
		if ((newValue != oldValue) ||
			(newValue && oldValue)	
		) {
			$scope.init();
			$scope.getApprData();
		}  
    });
	
	//放行覆核資料
	$scope.execute = function(row, status) {
		$confirm({
			text : '確定要' + (status == 'A' ? '同意' : '退回')  + '?'
		}, {
			size : 'sm'
		}).then(function() {
			var data = angular.copy(row);
			data.APPR_STATUS = status;
			data.ORG_DEGREE_NAME = $scope.getDegName(row.ORG_DEGREE);
			data.NEW_DEGREE_NAME = $scope.getDegName(row.NEW_DEGREE);

			$scope.sendRecv('CRM998', 'approval', 'com.systex.jbranch.app.server.fps.crm998.CRM998InputVO',
					[data], function(tota, isError) {
						if (!isError) {
							$scope.showMsg('ehl_01_common_023');
						} else {
							$scope.showMsg('ehl_01_common_024');
						}
						
						$scope.afterExecute();
			});
		});
	}
	
	//取得對應等級名稱
	$scope.getDegName = function(deg) {
		var deg = $scope.VIP_DEGREE.filter((e)=>e.DATA == deg)[0];
		if (deg) return deg.LABEL;
		else return null;
	}
	
	//執行完後動作
	$scope.afterExecute = function() {
		if ($scope.apprData.length -1) {
			$scope.getApprData();
		} else {
			$scope.clear();
		}
	}
	
	// date picker
	// 理專送出時間
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
		$scope.sDateOptions.maxDate = $scope.inputVO.applDateEnd;
		$scope.eDateOptions.minDate = $scope.inputVO.applDateStart;
	};
	
	//審核狀態決定頁面的呈現
	$scope.statusChange = function() {
		if ($scope.inputVO.apprStatus == 'W') {
			$scope.isApprMode  = true;
			$scope.otherMode = false;
		} else {
			$scope.isApprMode  = false;
			$scope.otherMode = true;
		}
		
		//清空data
		$scope.clear();
	}
	$scope.statusChange();
});