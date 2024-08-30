'use strict';
eSoafApp.controller('PMS705Controller', function($rootScope, $scope,
		$controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS705Controller";
	//繼承共用的組繼連動選單
	$controller('PMSRegionController', {$scope: $scope});
	$scope.init = function() 
	{
		$scope.inputVO = {
			rptVersion       : '',
			sTime            : '',
			role             : ''
		};
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
		$scope.largeAgrList = [];
		$scope.sumFlag = false;
		$scope.disableRegionCombo = false;
		$scope.disableAreaCombo = false;
		$scope.disableBranchCombo = false;
		$scope.disableAoCombo = false;
		
		var voFch = {'param_type': 'FUBONSYS.FCH_ROLE', 'desc': false};
    	$scope.requestComboBox(voFch, function(totas) {      	
    		if (totas[totas.length - 1].body.result === 'success') {        		
    			projInfoService.mappingSet['FUBONSYS.FCH_ROLE'] = totas[0].body.result;
    			for(var key in projInfoService.mappingSet['FUBONSYS.FCH_ROLE']){
    	    		if(projInfoService.mappingSet['FUBONSYS.FCH_ROLE'][key].DATA == projInfoService.getRoleID()){
    	    			$scope.inputVO.empHistFlag = 'Y';
    	    		}
    	    	}
    		}
    	});
	};
	$scope.init();
	$scope.inquireInit = function(){
		$scope.AREA_LIST = [];
		$scope.REGION_LIST = [];
		$scope.BRANCH_LIST = [];
		$scope.AO_LIST = [];
	}
	$scope.inquireInit();
	$scope.rptVersionChange = function() 
	{
		$scope.inputVO.role = $scope.role; 
		$scope.inputVO.yearMon = $scope.inputVO.sTime;
		$scope.sendRecv("PMS705", "version", "com.systex.jbranch.app.server.fps.pms705.PMS705InputVO", $scope.inputVO,
				function(totas, isError) 
				{
                	if (isError) 
                	{
                		$scope.showErrorMsg(totas[0].body.msgData);
                	}
                	if (totas.length > 0) 
                	{
                		$scope.mappingSet['rversion'] = [];
                		angular.forEach(totas[0].body.verList, function(row, index, objs){
	                		if(!!$scope.inputVO.sTime)	
        					$scope.mappingSet['rversion'].push({LABEL: row.COLUMN_VALUE,DATA: row.COLUMN_VALUE});
                		});
                	};
				}
		);
    };	
	
  //選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChange = function() {
    	$scope.inquireInit();
    	if(''==$scope.inputVO.sTime){
    		$scope.init();
    		return;
    	}
    	//設定回傳時間
    	$scope.inputVO.reportDate = $scope.inputVO.sTime;  
    	//可是範圍  觸發 
    	$scope.RegionController_getORG($scope.inputVO);
    }
	// 报表年月初始化方法
    $scope.isq = function()
    {
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
	/** 查詢 * */
	$scope.query = function() 
	{
		if($scope.parameterTypeEditForm.$invalid)
		{
    		$scope.showErrorMsg('欄位檢核錯誤:請選擇必填欄位');
    		return;
    	}
		$scope.sendRecv("PMS705", "queryData",
				"com.systex.jbranch.app.server.fps.pms705.PMS705InputVO",
				$scope.inputVO, function(tota, isError) 
				{
					if (!isError) 
					{
						$scope.largeAgrList = [];
						$scope.csvList = [];
						if (tota[0].body.largeAgrList == null
								|| tota[0].body.largeAgrList.length == 0) 
						{
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.largeAgrList = tota[0].body.largeAgrList;
						$scope.csvList = tota[0].body.csvList;
						$scope.roleList = tota[0].body.roleList;
						$scope.confirmFlag = true;
						for(var i = 0; i < $scope.largeAgrList.length; i++){								
							if($scope.largeAgrList[i].A_AO_CODE == undefined){
								$scope.confirmFlag = false;
							}
						}
						$scope.outputVO = tota[0].body;
						return;
					}else
					{
						$scope.showBtn = 'none';
					}
				});
	}
	/** 匯出 * */
	$scope.export = function() 
	{
		$scope.sendRecv("PMS705", "export",
				"com.systex.jbranch.app.server.fps.pms705.PMS705OutputVO", {
					'csvList' : $scope.csvList,
					'roleList' : $scope.roleList
				}, function(tota, isError) 
				{
					if (!isError) 
					{
						return;
					}
				});
	}
});