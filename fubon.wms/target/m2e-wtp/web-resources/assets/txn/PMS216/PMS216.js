'use strict';
eSoafApp.controller('PMS216Controller', function($rootScope, $scope,
		$controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService, $filter) {
	$controller('BaseController', {
		$scope : $scope
	});
	$controller('PMSRegionController', {$scope: $scope});
	$scope.controllerName = "PMS216Controller";
	// 报表年月初始化方法
    $scope.isq = function(){
		var NowDate = new Date();
		var yr = NowDate.getFullYear();
		var mm = NowDate.getMonth() + 1;
		var strmm = '';
		var xm = '';
		$scope.mappingSet['timeE'] = [];
		for (var i = 0; i < 12; i++) {
			mm = mm - 1;
			if (mm == 0) {
				mm = 12;
				yr = yr - 1;
			}
			if (mm < 10)
				strmm = '0' + mm;
			else
				strmm = mm;
			$scope.mappingSet['timeE'].push({
				LABEL : yr + '/' + strmm,
				DATA : yr + '' + strmm
			});
		} 
		
        
	};
	$scope.isq();
	
	$scope.inquireInit = function(){
		$scope.AREA_LIST = [];
		$scope.REGION_LIST = [];
		$scope.BRANCH_LIST = [];
		$scope.AO_LIST = [];
	}
	 /*** 可示範圍  JACKY共用版  START ***/
    //選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChange = function(){
    	$scope.inquireInit();
    	if(''==$scope.inputVO.sCreDate){
    		$scope.init();
    		return;
    	}
    	//設定回傳時間
    	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
    	//可是範圍  觸發 
    	$scope.RegionController_getORG($scope.inputVO);
    };
    
	$scope.curDate = new Date();
	
	var rp = "RC";
	if(sysInfoService.getRoleName().substring(0, 2) == 'FC')
		rp = "AO";
	if(sysInfoService.getRoleID() == 'A161' || sysInfoService.getRoleID()=='ABRU')
		rp = "BR";
	
	/*** 可示範圍  JACKY共用版  END***/
	
	$scope.init = function(){
		$scope.inputVO = {
				sCreDate:'',    //初使年月   inputVO必要加  因可視範圍
				branch_area_id:'',
				dataMonth : '',
				assignType: '1',
				role    : ''		
		};
		$scope.disableRegionCombo = false;
		$scope.disableAreaCombo = false;
		$scope.disableBranchCombo = false;
		$scope.disableAoCombo = false;
		$scope.inquireInit();
        $scope.largeAgrList = []; 
        $scope.csvList = []; 
        var voHead = {'param_type': 'FUBONSYS.HEADMGR_ROLE', 'desc': false};
    	$scope.requestComboBox(voHead, function(totas) {      	
    		if (totas[totas.length - 1].body.result === 'success') {        		
    			projInfoService.mappingSet['FUBONSYS.HEADMGR_ROLE'] = totas[0].body.result;
    			$scope.role = 0;
    			for(var key in projInfoService.mappingSet['FUBONSYS.HEADMGR_ROLE']){
    	    		if(projInfoService.mappingSet['FUBONSYS.HEADMGR_ROLE'][key].DATA == projInfoService.getRoleID()){
    	    			$scope.role = 1;
    	    		}
    	    	}
    		}
    	});
		
		var vo = {'param_type': 'FUBONSYS.FC_ROLE', 'desc': false};
    	$scope.requestComboBox(vo, function(totas) {      	
    		if (totas[totas.length - 1].body.result === 'success') {        		
    			projInfoService.mappingSet['FUBONSYS.FC_ROLE'] = totas[0].body.result;
    			for(var key in projInfoService.mappingSet['FUBONSYS.FC_ROLE']){
    				if(projInfoService.mappingSet['FUBONSYS.FC_ROLE'][key].DATA == projInfoService.getRoleID()){
    					$scope.inputVO.empHistFlag = 'Y';
    				}
    			}
    		}
    	});
	};
	$scope.init();
	
	$scope.rptVersionChange = function() {
		$scope.inputVO.role = $scope.role; 
		$scope.inputVO.yearMon = $scope.inputVO.sCreDate;
		$scope.inputVO.rptVersion = '';
		$scope.disableVersionCombo = false;
		$scope.mappingSet['rversion'] = [];
		if($scope.inputVO.sCreDate!=null && $scope.inputVO.sCreDate!=''){
			$scope.sendRecv("PMS216", "version", "com.systex.jbranch.app.server.fps.pms216.PMS216InputVO", $scope.inputVO,
					function(totas, isError) {
				if (isError) {
					$scope.showErrorMsg(totas[0].body.msgData);
				}
				if (totas.length > 0) {
					$scope.mappingSet['rversion'] = [];
					angular.forEach(totas[0].body.verList, function(row, index, objs){
						
						$scope.mappingSet['rversion'].push({LABEL: row.COLUMN_VALUE,DATA: row.COLUMN_VALUE});
					});
				};
			}
			);
		}
		
    };

	/** 查詢 * */
	$scope.query = function() {
		if($scope.parameterTypeEditForm.$invalid){
    		$scope.showErrorMsg('欄位檢核錯誤:請選擇必填欄位');
    		return;
    	}
		$scope.inputVO.yearMon = $scope.inputVO.sCreDate;
		$scope.inputVO.regionCenterId = $scope.inputVO.region_center_id;
		$scope.inputVO.branchAreaId = $scope.inputVO.branch_area_id;
		$scope.inputVO.branchNbr = $scope.inputVO.branch_nbr;
		$scope.inputVO.aoCode = $scope.inputVO.ao_code;
		$scope.sendRecv("PMS216", "queryData","com.systex.jbranch.app.server.fps.pms216.PMS216InputVO",
				$scope.inputVO, function(tota, isError) {
					if (!isError) {
						$scope.largeAgrList = [];
						$scope.csvList = [];
						if (tota[0].body.largeAgrList == null
								|| tota[0].body.largeAgrList.length == 0) {
							
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.largeAgrList = tota[0].body.largeAgrList;
						$scope.csvList = tota[0].body.csvList;
						$scope.roleList = tota[0].body.roleList;
						$scope.outputVO = tota[0].body;
						return;
					}else
					{
						$scope.showBtn = 'none';
					}
				});
	}
	
	/** 匯出 * */
	$scope.export = function() {
		$scope.sendRecv("PMS216", "export","com.systex.jbranch.app.server.fps.pms216.PMS216OutputVO", {
					'csvList' : $scope.csvList,
					'roleList' : $scope.roleList
				}, function(tota, isError) {
					if (!isError) {
						return;
					}	
				});
	}
});