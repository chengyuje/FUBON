/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM130Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM130Controller";
		
		// filter
		var vo = {'param_type': 'CAM.LEAD_TYPE', 'desc': false};
        if(!projInfoService.mappingSet['CAM.LEAD_TYPE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['CAM.LEAD_TYPE'] = totas[0].body.result;
        			$scope.mappingSet['CAM.LEAD_TYPE'] = projInfoService.mappingSet['CAM.LEAD_TYPE'];
        		}
        	});
        } else
        	$scope.mappingSet['CAM.LEAD_TYPE'] = projInfoService.mappingSet['CAM.LEAD_TYPE'];
        var vo = {'param_type': 'CAM.CHANNEL_CODE', 'desc': false};
        if(!projInfoService.mappingSet['CAM.CHANNEL_CODE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['CAM.CHANNEL_CODE'] = totas[0].body.result;
        			$scope.mappingSet['CAM.CHANNEL_CODE'] = projInfoService.mappingSet['CAM.CHANNEL_CODE'];
        		}
        	});
        } else
        	$scope.mappingSet['CAM.CHANNEL_CODE'] = projInfoService.mappingSet['CAM.CHANNEL_CODE'];
        var vo = {'param_type': 'CAM.IMPORT_STATUS', 'desc': false};
        if(!projInfoService.mappingSet['CAM.IMPORT_STATUS']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['CAM.IMPORT_STATUS'] = totas[0].body.result;
        			$scope.mappingSet['CAM.IMPORT_STATUS'] = projInfoService.mappingSet['CAM.IMPORT_STATUS'];
        		}
        	});
        } else
        	$scope.mappingSet['CAM.IMPORT_STATUS'] = projInfoService.mappingSet['CAM.IMPORT_STATUS'];
        //
		
     	// date picker
        // 匯入日期
        $scope.sCreDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.eCreDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		// 活動起始日
        $scope.sStaDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.eStaDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		// 活動截止日
        $scope.sEndDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.eEndDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.sCreDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate;
			$scope.eCreDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate;
		};
		$scope.limitDate2 = function() {
			$scope.sStaDateOptions.maxDate = $scope.inputVO.eStaDate || $scope.maxDate;
			$scope.eStaDateOptions.minDate = $scope.inputVO.sStaDate || $scope.minDate;
		};
		$scope.limitDate3 = function() {
			$scope.sEndDateOptions.maxDate = $scope.inputVO.eEndDate || $scope.maxDate;
			$scope.eEndDateOptions.minDate = $scope.inputVO.sEndDate || $scope.minDate;
		};
		// date picker end
		
		$scope.init = function(){
			$scope.inputVO = {};
			$scope.limitDate();
			$scope.limitDate2();
			$scope.limitDate3();
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.paramList = [];
		}
		$scope.inquireInit();
		
		$scope.inquire = function(){
			// 使用部隊-全部FC
			if ($scope.inputVO.channel == "FCALL")
				$scope.inputVO.channel = "FC%";
			
			$scope.sendRecv("CAM130", "queryData", "com.systex.jbranch.app.server.fps.cam130.CAM130InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							return;
						}
			});
		};
		
		$scope.btnAdd = function () {
			$scope.connector('set','CAM140PAGE', "CAM130");
			$scope.connector('set','CAM140', "");
			$rootScope.menuItemInfo.url = "assets/txn/CAM140/CAM140.html";
		};
		
		$scope.fail = function (row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CAM130/CAM130_FAIL.html',
				className: 'CAM130',
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
		
		$scope.openDtl = function (row, expType) {
			$scope.sendRecv("CAM130", "export", "com.systex.jbranch.app.server.fps.cam130.CAM130InputVO", {'seq': row.SEQNO, 'expType': expType},
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                	};
					}
			);
		};
		
		
});
