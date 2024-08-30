/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS218Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) 
	{
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS218Controller";	
		//繼承共用的組繼連動選單
		$controller('PMSRegionController', {$scope: $scope});
		
		$scope.init = function()
		{
			$scope.inputVO = 
			{
					rptVersion       : '',
					region           : '',
					sTime            : '',
					PROD_SOURCE      : '',
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
			$scope.paramList = [];
            $scope.sumFlag = false;
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
			var FCvo = {'param_type': 'FUBONSYS.FC_ROLE', 'desc': false};
        	$scope.requestComboBox(FCvo, function(totas) {      	
        		if (totas[totas.length - 1].body.result === 'success') {        		
        			projInfoService.mappingSet['FUBONSYS.FC_ROLE'] = totas[0].body.result;
        			for(var key in projInfoService.mappingSet['FUBONSYS.FC_ROLE']){
        	    		if(projInfoService.mappingSet['FUBONSYS.FC_ROLE'][key].DATA == projInfoService.getRoleID()){
        	    			$scope.FCFlag = 'Y';
        	    		}
        	    	}
        		}
	    	})
	    	
			var FCHvo = {'param_type': 'FUBONSYS.FCH_ROLE', 'desc': false};
        	$scope.requestComboBox(FCHvo, function(totas) {      	
        		if (totas[totas.length - 1].body.result === 'success') {        		
        			projInfoService.mappingSet['FUBONSYS.FCH_ROLE'] = totas[0].body.result;
        			for(var key in projInfoService.mappingSet['FUBONSYS.FCH_ROLE']){
        	    		if(projInfoService.mappingSet['FUBONSYS.FCH_ROLE'][key].DATA == projInfoService.getRoleID()){
        	    			$scope.FCHFlag = 'Y';
        	    		}
        	    	}
        		}
	    	})
	    	
        	if($scope.FCFlag=='Y'||$scope.FCHFlag=='Y'){
        		$scope.inputVO.empHistFlag = 'Y'
        	};
		};
		$scope.init();
		var vo = {'param_type': 'PMS.CNR_PROD_SOURCE', 'desc': false};
    	$scope.requestComboBox(vo, function(totas) {      	
    		if (totas[totas.length - 1].body.result === 'success') {        		
    			projInfoService.mappingSet['PMS.CNR_PROD_SOURCE'] = totas[0].body.result;
    			$scope.mappingSet['PMS.CNR_PROD_SOURCE'] = projInfoService.mappingSet['PMS.CNR_PROD_SOURCE'];
    		}
    	});
		$scope.inquireInit = function()
		{
			$scope.AREA_LIST = [];
			$scope.REGION_LIST = [];
			$scope.BRANCH_LIST = [];
			$scope.AO_LIST = [];
		}
		$scope.inquireInit();
		$scope.rptVersionChange = function() {
			$scope.inputVO.role = $scope.role; 
			$scope.sendRecv("PMS218", "version", "com.systex.jbranch.app.server.fps.pms218.PMS218InputVO", $scope.inputVO,
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
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
    	$scope.exportRPT = function(){
    		if($scope.parameterTypeEditForm.$invalid)
			{
				$scope.showMsg('欄位檢核錯誤:請選擇必填欄位');
        		return;
        	}
			$scope.sendRecv("PMS218", "export",	"com.systex.jbranch.app.server.fps.pms218.PMS218InputVO",$scope.inputVO
					, function(tota, isError) {
						if (!isError) {
							if(tota[0].body.errorMessage =='匯出筆數過多, 請增加查詢條件'){
								$scope.showErrorMsg("匯出筆數過多, 請增加查詢條件");
								return;
							}
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
				$scope.showMsg('欄位檢核錯誤:請選擇必填欄位');
        		return;
        	}
			$scope.sendRecv("PMS218", "inquire", "com.systex.jbranch.app.server.fps.pms218.PMS218InputVO", $scope.inputVO,
					function(tota, isError)
					{
						if (!isError) 
						{
							if(tota[0].body.errorMessage =='查詢筆數過多, 請增加查詢條件' ){
								$scope.showErrorMsg(tota[0].body.errorMessage);
								return;
							}
							$scope.paramList = [];
							$scope.csvList = [];
							$scope.originalList=[];
							if(tota[0].body.resultList.length == 0) 
							{
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.originalList = angular.copy(tota[0].body.resultList);
							$scope.paramList = tota[0].body.resultList;
							$scope.csvList=tota[0].body.csvList;
							$scope.roleList = tota[0].body.roleList;
							$scope.confirmFlag = true;
							$scope.outputVO = tota[0].body;
							return;
						}else
						{
							$scope.paramList = [];
							$scope.csvList = [];
							$scope.originalList=[];
							$scope.showBtn = 'none';
						}	
			});
		};
});