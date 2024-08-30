/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS221Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) 
	{
		$controller('BaseController', {$scope: $scope});
		$controller('PMSRegionController', {$scope: $scope});
		$scope.controllerName = "PMS221Controller";	
		$scope.mappingSet['personType'] = [];
		$scope.mappingSet['personType'].push(
				{LABEL: '分行主管', DATA: 'BM'},
				{LABEL: '業務主管', DATA: 'SH'},
				{LABEL: '營運督導', DATA: 'RD'}
				);
		
		$scope.init = function()
		{
			$scope.inputVO = {
					sTime:'',    //初使年月   inputVO必要加  因可視範圍
					dataMonth : '',
					assignType: '1',
					role    : '',
					rptVersion : ''
			};
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
			$scope.disableEmpCombo = false;
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
            $scope.paramList = [];
            $scope.csvList = [];
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.AREA_LIST = [];
			$scope.REGION_LIST = [];
			$scope.BRANCH_LIST = [];
			$scope.AO_LIST = [];
			$scope.EMP_LIST = [];
		}
		
		$scope.rptTypeChange = function()
		{
			$scope.inquireInit();
			if($scope.inputVO.personType == BM)
				$scope.typeName = '分行主管';
			else if($scope.inputVO.personType == SH)
				$scope.typeName = '業務主管';
			else if($scope.inputVO.personType == RD)
				$scope.typeName = '營運督導';
			else
				$scope.typeName = '';
		};
		
		$scope.inquireInit = function()
		{
			$scope.paramList = [];
			$scope.csvList = [];
		}
		$scope.inquireInit();
		$scope.rptVersionChange = function() 
		{
			$scope.inputVO.role = $scope.role; 
			$scope.sendRecv("PMS221", "version", "com.systex.jbranch.app.server.fps.pms221.PMS221InputVO", $scope.inputVO,
					function(totas, isError) 
					{
	                	if (isError) {
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
        
        $scope.dateChange = function(){
        	$scope.inquireInit();
        	if(''==$scope.inputVO.sTime){
        		$scope.init();
        		return;
        	}
        	$scope.inputVO.empHistFlag = $("input[name='empHistRadio']:checked").val();
        	//設定回傳時間
        	$scope.inputVO.reportDate = $scope.inputVO.sTime;
        	//可是範圍  觸發 
        	$scope.RegionController_getORG($scope.inputVO);
	    };
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
        
    	/**
		 * 匯出
		 */
    	$scope.exportRPT = function()
    	{
			$scope.sendRecv("PMS221", "export",	
					"com.systex.jbranch.app.server.fps.pms221.PMS221OutputVO",{
					'csvList':$scope.csvList,
					'roleList' : $scope.roleList},
					function(tota, isError) {
						if (!isError) {
							return;
						}
					});
			};		
    	
		/**
		 * 查詢
	    */
		$scope.inquire = function()
		{
			if($scope.parameterTypeEditForm.$invalid)
			{
	    		$scope.showErrorMsg('欄位檢核錯誤:請選擇必填欄位');
        		return;
        	}
			$scope.sendRecv("PMS221", "inquire", "com.systex.jbranch.app.server.fps.pms221.PMS221InputVO", $scope.inputVO,
					function(tota, isError) 
					{
						if (!isError) {
							$scope.paramList = [];
							$scope.csvList = [];
							if(tota[0].body.resultList.length == 0) 
							{
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.csvList=tota[0].body.csvList;
							$scope.roleList = tota[0].body.roleList;
							$scope.outputVO = tota[0].body;
							return;
						}else
						{
							$scope.showBtn = 'none';
						}	
			});
		};
});