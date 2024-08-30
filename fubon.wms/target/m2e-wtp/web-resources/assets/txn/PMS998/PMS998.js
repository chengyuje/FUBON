'use strict';
eSoafApp.controller('PMS998Controller', function(sysInfoService, $scope, $controller, $confirm, $compile, socketService, ngDialog, projInfoService, getParameter) {
	$controller('BaseController', { $scope : $scope });
	$scope.controllerName = "PMS998Controller";
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	getParameter.XML(["PMS.C2E_RELATION", "PMS.PROVE", "PMS.PMS998_STATUS"], function(totas) {
		if (totas) {
			$scope.mappingSet['PMS.C2E_RELATION'] = totas.data[totas.key.indexOf('PMS.C2E_RELATION')];
			$scope.mappingSet['PMS.PROVE'] = totas.data[totas.key.indexOf('PMS.PROVE')];
			$scope.mappingSet['PMS.PMS998_STATUS'] = totas.data[totas.key.indexOf('PMS.PMS998_STATUS')];
		}
	});
	
	$scope.altInputFormats = ['M!/d!/yyyy'];
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
    $scope.importStartDateOptions = {
		maxDate: $scope.inputVO.importEDate || $scope.maxDate,
		minDate: $scope.minDate
	};
	$scope.importEndDateOptions = {
		maxDate: $scope.maxDate,
		minDate: $scope.inputVO.importSDate || $scope.minDate
	};
	$scope.limitDate = function() {
		$scope.importStartDateOptions.maxDate = $scope.inputVO.importEDate || $scope.maxDate;
		$scope.importEndDateOptions.minDate = $scope.inputVO.importSDate || $scope.minDate;
	};
	
	// filter
	getParameter.XML(["COMMON.REVIEW_STATUS"], function(totas) {
		if (totas) {
			$scope.mappingSet['COMMON.REVIEW_STATUS'] = totas.data[totas.key.indexOf('COMMON.REVIEW_STATUS')];
		}
	});
    //
	
	$scope.memLoginFlag = String(sysInfoService.getMemLoginFlag());
	
	/*
	 * 取得UHRM人員清單(由員工檔+角色檔)
	 */
	$scope.sendRecv("ORG260", "get031EmpList", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, function(tota, isError) {
		if (isError) {
			return;
		}
		if (tota.length > 0) {
			$scope.mappingSet['UHRM_LIST'] = tota[0].body.uhrmList;
			$scope.inputVO.uEmpID = sysInfoService.getUserID();
		}
	});
	
	$scope.init = function() {
		$scope.inputVO = {
			importSDate      : undefined,
			importEDate      : undefined,
			region_center_id : '',
			branch_area_id   : '',
			branch_nbr       : '',
			EMP_ID           : ''
		};
		
		//組織連動
        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
        $scope.RegionController_setName($scope.region);
        
        $scope.hideFlag = "N";
        if ((sysInfoService.getMemLoginFlag()).toLowerCase().startsWith("uhrm")) {
        	$scope.hideFlag = "Y";
        	$scope.inputVO.branchNbr = "715";
        }
	};
	
	$scope.query = function() {
		$scope.sendRecv("PMS998", "query", "com.systex.jbranch.app.server.fps.pms998.PMS998InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
				$scope.resultList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;
			}
			
		});
	};
	
	$scope.actionGO = function(actionType, seqNO) {
		$scope.inputVO.actionType = actionType;
		$scope.inputVO.seq = seqNO;
		
		$scope.sendRecv("PMS998", "action", "com.systex.jbranch.app.server.fps.pms998.PMS998InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				switch (actionType) {
					case "RT":
						$scope.showSuccessMsg('ehl_01_common_021');
						break;
					case "RF":
						$scope.showSuccessMsg('ehl_01_common_020');
						break;
					case "D":
						$scope.showSuccessMsg('ehl_01_common_003');
						break;
				}
				$scope.query();
			}
		});
	}
	
	$scope.add = function () {
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS998/PMS998_INSERT.html',
			className: 'PMS998',
			showClose: false,
			scope : $scope
		});
		dialog.closePromise.then(function (data) {
			if(data.value === 'successful'){
				$scope.init();
				$scope.query();
			}
		});
	};
	
	//匯出
	$scope.export = function() {
		if ($scope.resultList != undefined) {
			$scope.inputVO.list = $scope.resultList;
			$scope.sendRecv("PMS998", "export", "com.systex.jbranch.app.server.fps.pms998.PMS998InputVO", {list: $scope.resultList},
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg("資料匯出成功!");
					}
				});
		} else {
			$scope.showMsg('目前沒有資料，請先查詢!');
		}
	};
	
	$scope.init();
});
