/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS714Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS714Controller";	
		//繼承共用的組繼連動選單
		$controller('PMSRegionController', {$scope: $scope});
		$scope.init = function(){
			$scope.inputVO = {
					sTime            : ''
			};
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
            $scope.resultList = [];
            
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
		//console.log("log===714");
		$scope.inquireInit = function(){
			$scope.AREA_LIST = [];
			$scope.REGION_LIST = [];
			$scope.BRANCH_LIST = [];
			$scope.AO_LIST = [];
		}
		$scope.inquireInit();
	    	
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
    	/**
		 * 匯出
		 */
    	$scope.exportRPT = function(){
			$scope.sendRecv("PMS714", "export",	"com.systex.jbranch.app.server.fps.pms714.PMS714OutputVO",{
					 'csvList': $scope.csvList}, function(tota, isError) {
						if (!isError) {
							return;
						}
					});
			};
		/**
		 * 查詢
		 */
		$scope.inquire = function(){
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:請選擇必填欄位');
        		return;
        	}
			$scope.sendRecv("PMS714", "inquire", "com.systex.jbranch.app.server.fps.pms714.PMS714InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.resultList = [];
							$scope.csvList = [];
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.originalList = angular.copy(tota[0].body.resultList);
							$scope.resultList = tota[0].body.resultList;
							$scope.csvList = tota[0].body.csvList;
							$scope.confirmFlag = true;
							for(var i = 0; i < $scope.resultList.length; i++){								
								if($scope.resultList[i].A_AO_CODE == undefined){
									$scope.confirmFlag = false;
								}
							}
							$scope.outputVO = tota[0].body;
							return;
						}else{
							$scope.showBtn = 'none';
						}	
			});
		};
});
