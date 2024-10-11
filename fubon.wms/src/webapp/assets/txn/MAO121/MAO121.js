'use strict';
eSoafApp.controller('MAO121Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, $filter, $timeout, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "MAO121Controller";
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	$scope.priID = String(sysInfoService.getPriID());
    $scope.memLoginFlag = String(sysInfoService.getMemLoginFlag());
    
	// filter
	getParameter.XML(["MAO.DEV_STATUS_AO", "MAO.USE_PERIOD"], function(totas) {
		if (totas) {
			$scope.mappingSet['MAO.DEV_STATUS_AO'] = totas.data[totas.key.indexOf('MAO.DEV_STATUS_AO')];
			$scope.mappingSet['MAO.USE_PERIOD'] = totas.data[totas.key.indexOf('MAO.USE_PERIOD')];
		}
	});
    //
	
	/*
	 * 取得UHRM人員清單(由員工檔+角色檔)
	 */
	$scope.getUHRMList = function() {
		$scope.sendRecv("ORG260", "getUHRMList", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) { 
				if (null != tota[0].body.uhrmList) {
					$scope.mappingSet['UHRM_LIST'] = tota[0].body.uhrmList;
				} else {
					$scope.mappingSet['UHRM_LIST'] = [];
				}
			} 
		});
	};
	$scope.getUHRMList();
	
	// datepicker
	$scope.use_date_bgnOptions = {
		maxDate: $scope.inputVO.use_date_end || $scope.maxDate,
		minDate: $scope.minDate
	};
	$scope.use_date_endOptions = {
		maxDate: $scope.maxDate,
		minDate: $scope.inputVO.use_date_bgn || $scope.minDate
	};
	$scope.altInputFormats = ['M!/d!/yyyy'];
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	$scope.limitDate = function() {
		$scope.use_date_bgnOptions.maxDate = $scope.inputVO.use_date_end || $scope.maxDate;
		$scope.use_date_endOptions.minDate = $scope.inputVO.use_date_bgn || $scope.minDate;
	};
	
	$scope.init = function() {
		$scope.data = [];
		$scope.resultList = [];
		
		$scope.inputVO = {
				region_center_id : '',
				branch_area_id : '',
				branch_nbr : '',
				use_date_bgn : new Date(),
				use_date_end : new Date(),
				emp_id : ''
		}
		
		$scope.limitDate();
		
		//組織連動
        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
        $scope.RegionController_setName($scope.region);
        
        $scope.inputVO.emp_id = '';
        
        $scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'MAO121'}, function(tota, isError) {
			if (!isError) {
				$scope.chkMaintenance = tota[0].body.isMaintenancePRI == 'Y' ? true : false;

				$scope.uhrmRCList = [];
				$scope.uhrmOPList = [];

				if (null != tota[0].body.uhrmORGList) {
					angular.forEach(tota[0].body.uhrmORGList, function(row) {
						$scope.uhrmRCList.push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});
					});	
					
					$scope.inputVO.region_center_id = tota[0].body.uhrmORGList[0].REGION_CENTER_ID;
					
					angular.forEach(tota[0].body.uhrmORGList, function(row) {
						$scope.uhrmOPList.push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
					});
					
					$scope.inputVO.branch_area_id = tota[0].body.uhrmORGList[0].BRANCH_AREA_ID;
		        }
			}						
		});
	}
	
	$timeout(function(){$scope.init();},500);
	
	//輸出欄初始化
	$scope.inquireInit = function(){
		$scope.resultList = [];
		$scope.outputVO = {};
	}
	$scope.inquireInit();
	
	//查詢
	$scope.inquire = function() {
		$scope.inquireInit();
		$scope.sendRecv("MAO121", "inquire", "com.systex.jbranch.app.server.fps.mao121.MAO121InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.inquireInit();
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				$scope.resultList = _.sortBy(tota[0].body.resultList, ['USE_DATE']);
				$scope.outputVO = tota[0].body;
				return;
			}
		});
    };
    

    $scope.comma_split = function(value) {
    	return value.split(',');
    }
    
    // 連至客戶首頁
    $scope.custDTL = function(row) {
    	$scope.custVO = {
				CUST_ID   : row.substring(0, row.indexOf(":")),
				CUST_NAME : row.substring(row.indexOf(":") + 1, row.length)	
		}
		$scope.connector('set','CRM_CUSTVO',$scope.custVO);
    	
    	var path = "assets/txn/CRM610/CRM610_MAIN.html";
		var set = $scope.connector("set","CRM610URL",path);
		var dialog = ngDialog.open({
			template: 'assets/txn/CRM610/CRM610.html',
			className: 'CRM610',
			showClose: false
		});
	}
});