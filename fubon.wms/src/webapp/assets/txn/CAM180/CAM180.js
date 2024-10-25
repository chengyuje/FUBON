'use strict';
eSoafApp.controller('CAM180Controller', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, getParameter, sysInfoService) {
	$controller('BaseController', {$scope: $scope});
	$controller('RegionController', {$scope: $scope});
	$scope.controllerName = "CAM180Controller";
	
	$scope.tabType = 0;
	// combobox
	getParameter.XML(["CAM.CHANNEL_CODE", "FUBONSYS.HEADMGR_ROLE", "CAM.VST_REC_CMU_TYPE"], function(totas) {
		if (totas) {
			$scope.mappingSet['CAM.VST_REC_CMU_TYPE'] = totas.data[totas.key.indexOf('CAM.VST_REC_CMU_TYPE')];
			$scope.CHANNEL_CODE = totas.data[totas.key.indexOf('CAM.CHANNEL_CODE')];
			$scope.CHANNEL_CODE = _.filter($scope.CHANNEL_CODE, function(o) { return !(o.DATA == 'FC1' || o.DATA == 'FC2' || o.DATA == 'FC3' || o.DATA == 'FC4' || o.DATA == 'FC5'); });
			var temp_role = totas.data[totas.key.indexOf('FUBONSYS.HEADMGR_ROLE')].map(function(e) { return e.DATA;});
			$scope.IS_HEADMGR_ROLE = temp_role.indexOf(projInfoService.getRoleID()) > -1;
		}
	});
	
	// date picker
	// 名單建立日期
	$scope.sDateOptions = {};
	$scope.eDateOptions = {};
	// 名單維護日期
	$scope.sEDateOptions = {};
	$scope.eEDateOptions = {};
	// config
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
	$scope.limitDate = function() {
		$scope.sDateOptions.maxDate = $scope.inputVO.eDate;
		$scope.eDateOptions.minDate = $scope.inputVO.sDate;
//		if($scope.inputVO.sDate) {
//			var compare1 = new Date(($scope.inputVO.sDate.getFullYear()+1),$scope.inputVO.sDate.getMonth(),$scope.inputVO.sDate.getDate()-1);
//			$scope.eDateOptions.maxDate = compare1;
//			if(compare1 < $scope.inputVO.eDate)
//				$scope.inputVO.eDate = "";
//		}
//		if($scope.inputVO.sDate || $scope.inputVO.eDate || $scope.inputVO.sEDate || $scope.inputVO.eEDate)
			$scope.date = false;
//		else
//			$scope.date = true;
	};
	
	$scope.limitEDate = function() {
		$scope.sEDateOptions.maxDate = $scope.inputVO.eEDate;
		$scope.eEDateOptions.minDate = $scope.inputVO.sEDate;
		if($scope.inputVO.sEDate) {
			var compare2 = new Date(($scope.inputVO.sEDate.getFullYear()+1),$scope.inputVO.sEDate.getMonth(),$scope.inputVO.sEDate.getDate()-1);
			$scope.eEDateOptions.maxDate = compare2;
			if(compare2 < $scope.inputVO.eEDate)
				$scope.inputVO.eEDate = "";
		}	 
		if($scope.inputVO.sDate || $scope.inputVO.eDate || $scope.inputVO.sEDate || $scope.inputVO.eEDate)
			$scope.date = false;
		else
			$scope.date = true;
	};
	// date picker end
	
	/*
	 * 取得UHRM人員清單(由員工檔+角色檔)
	 */
	$scope.getUHRMList = function() {
//		$scope.inputVO.regionCenterID = $scope.inputVO.ao_03;
		$scope.inputVO.branchAreaID = $scope.inputVO.branch_area_id;
		
		$scope.sendRecv("ORG260", "getUHRMListByType", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO,  function(tota, isError) {
			if (isError) {
				return;
			}
			if (tota.length > 0) {
				$scope.mappingSet['UHRM_LIST'] = tota[0].body.uhrmList;
				if ($scope.mappingSet['UHRM_LIST'].length >= 1 && $scope.login == 'UHRM002') {
					$scope.inputVO.uEmpID = $scope.mappingSet['UHRM_LIST'][0].DATA;
				} else {
					$scope.inputVO.uEmpID = '';
				}
			}
		});
	};
	
	$scope.init = function() {
		$scope.inputVO = {};
		$scope.limitDate();
		$scope.limitEDate();
		$scope.date = true;
		// 理專使用訪談記錄下載功能時,角色欄位不可再選擇,預設為"全部FC"並反灰
		var temp = ['001', '002', '003'];
		if(temp.indexOf(projInfoService.getPriID()[0]) > -1) {
			$scope.cam180IsFC = true;
		}
		//業務處長
		$scope.inputVO.isRegionMgr = (projInfoService.getPriID()[0] == "013");
		// 組織連動
        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "bCode", "BRANCH_LIST", "ao_code", "AO_LIST", "pCode", "EMP_LIST"];
        $scope.RegionController_setName($scope.region).then(function(data) {
        	if($scope.EMP_LIST.length > 2) {
        		$scope.inputVO.pCode = "";
        	}
        	//處長登入，取得私銀人員資料
        	if($scope.inputVO.isRegionMgr) {
        		$scope.getUHRMList();
        	}
        });
	};
	
	$scope.init();
	
	$scope.inquireInit = function() {
		$scope.totalList = [];
		$scope.outputVO = {};
	}
	
	$scope.inquireInit();
	
	$scope.inquire = function(tabType, isUHRM) {
		debugger
		if ($scope.date) {
			$scope.showErrorMsg("ehl_01_common_022");
    		return;
    	}
		
//		if ($scope.parameterTypeEditForm.$invalid) {
//    		$scope.showErrorMsg("ehl_01_common_022");
//    		return;
//    	}
		
		if ($scope.inputVO.id) {
			$scope.inputVO.id = $scope.inputVO.id.toUpperCase();
		}
		
		$scope.inputVO.branch_list = [];
		angular.forEach($scope.BRANCH_LIST, function(row, index, objs){
			if(row.DATA != "" && row.DATA != "0"){
				$scope.inputVO.branch_list.push({LABEL: row.LABEL, DATA: row.DATA});					
			}
		});
		
		$scope.inputVO.regionMrgUHRMAreaYN = "N";
		//非私銀非總行一定要選分行
		//業務處長有選營運區
		if(!isUHRM && $scope.inputVO.isRegionMgr && $scope.inputVO.branch_area_id != undefined
				&& $scope.inputVO.branch_area_id != null && $scope.inputVO.branch_area_id != "") {
			if($scope.inputVO.branch_list.length > 0 && $scope.inputVO.branch_list[0].DATA.length > 3) {
				//若選到私銀區，或沒有分行下拉選單資料
				//不檢核
				$scope.inputVO.regionMrgUHRMAreaYN = "Y"; //業務處長選私銀區
			} else if(!$scope.inputVO.bCode) {
				//業務處長選非私銀區，一定要選分行
				$scope.showErrorMsg("ehl_01_common_022");
				return;
			}
		} else if(!isUHRM && !$scope.IS_HEADMGR_ROLE && !$scope.inputVO.bCode) {
			//非私銀非總行一定要選分行
			$scope.showErrorMsg("ehl_01_common_022");
        	return;
		}
		
		$scope.inputVO.tabType = tabType;
		
		$scope.sendRecv("CAM180", "inquire", "com.systex.jbranch.app.server.fps.cam180.CAM180InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				$scope.totalList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;
			}
		});
		
	};
	
	$scope.setTabType = function (tabType) {
		$scope.tabType = tabType;
	}
    		
	$scope.download = function() {
		if($scope.date) {
			$scope.showErrorMsg("ehl_01_common_022");
			console.log("1");
    		return;
    	}
		
		if($scope.parameterTypeEditForm.$invalid && !(sysInfoService.getMemLoginFlag()).toLowerCase().startsWith("uhrm")) {
    		$scope.showErrorMsg("ehl_01_common_022");
    		return;
    	}
		// 2018/5/18 mantis 4933
		if(!$scope.IS_HEADMGR_ROLE) {
			if(!$scope.inputVO.bCode && !$scope.inputVO.id && !(sysInfoService.getMemLoginFlag()).toLowerCase().startsWith("uhrm")) {
				$scope.showErrorMsg("ehl_01_common_022");
        		return;
			}
		}
		
		// toUpperCase
		if($scope.inputVO.id)
			$scope.inputVO.id = $scope.inputVO.id.toUpperCase();
		$scope.sendRecv("CAM180", "download", "com.systex.jbranch.app.server.fps.cam180.CAM180InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					if(tota[0].body.resultList && tota[0].body.resultList.length == 0)
						$scope.showMsg("ehl_01_common_009");
				}
		});
	};
		
	
		
});