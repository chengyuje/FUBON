/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS325Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS325Controller";	
		//繼承共用的組繼連動選單
		$controller('PMSRegionController', {$scope: $scope});
		$scope.init = function()
		{
			$scope.inputVO = {
					ao_code          : '',
					branch_nbr       : '',
					region_center_id : '',
					branch_area_id   : '',
					role             : ''
			};
			$scope.s1Time = '';
			$scope.s2Time = '';
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
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
        	    	$scope.inputVO.role = $scope.role; 
        		}
	    	});
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
		$scope.isq = function(){
			 $scope.sendRecv("PMS325", "getDurExam", "com.systex.jbranch.app.server.fps.pms325.PMS325InputVO", $scope.inputVO,
						function(totas, isError) {
		                	if (isError) {
		                		$scope.showErrorMsg(totas[0].body.msgData);
		                	}
		                	$scope.mappingSet['timeE'] = [];
		                	angular.forEach(totas[0].body.durExamList, function(row, index, objs){
		                		$scope.mappingSet['timeE'].push({LABEL: row.START_MONTH + '~' + row.END_MONTH,DATA: row.START_MONTH + '~' + row.END_MONTH + '~' + row.EXEC_MONTH});
		                	});
						}
				);
	    	};
	    //選取月份下拉選單 --> 重新設定可視範圍
	    $scope.dateChange = function() {
        	$scope.inquireInit();
        	if(''==$scope.s1Time){
        		$scope.init();
        		return;
        	}
        	$scope.inputVO.startTime = $scope.s1Time.substr(0,6);
			$scope.inputVO.endTime = $scope.s1Time.substr(7,6);
			$scope.inputVO.execTime = $scope.s1Time.substr(14);
			$scope.sendRecv("PMS325", "getexeDurExam", "com.systex.jbranch.app.server.fps.pms325.PMS325InputVO", $scope.inputVO,
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	$scope.mappingSet['timeEx'] = [];
	                	angular.forEach(totas[0].body.durExeExamList, function(row, index, objs){
        					$scope.mappingSet['timeEx'].push({LABEL: row.START_MONTH + '~' + row.EXEC_MONTH,DATA: row.START_MONTH + '~' + row.END_MONTH + '~' + row.EXEC_MONTH});
                		});
					}
			);
			if($scope.inputVO.execTime == "null" || $scope.inputVO.execTime == null || $scope.inputVO.execTime== undefined){
				//設定回傳時間
				var endTime = $scope.s1Time.substr(7,6);
				var date = new Date();
				var nowYear = date.getFullYear();
				var month = date.getMonth() + 1;
				if(month > 2) {
					nowYear = nowYear;
					month = month -2;
				}else {
					nowYear = nowYear - 1;
					month = month + 12 -2;
				}			
				if (month >= 1 && month <= 9) {
			        month = "0" + month;
			    }			
				if(endTime > nowYear + month){				
					$scope.inputVO.reportDate = nowYear + month ;				
				}else{				
					$scope.inputVO.reportDate = endTime;
				}
			}else{
				$scope.inputVO.reportDate = $scope.inputVO.execTime;
			}
        	//可是範圍  觸發 
        	$scope.RegionController_getORG($scope.inputVO);
        }
	    
	    $scope.isq();
	    
	  //選取計值月份
        $scope.dateChange1 = function() {
        	$scope.inquireInit();
			if(''==$scope.s2Time){
        		$scope.init();
        		return;
        	}
			$scope.inputVO.startTime = $scope.s2Time.substr(0,6);
			$scope.inputVO.endTime = $scope.s2Time.substr(7,6);
			$scope.inputVO.execTime = $scope.s2Time.substr(14);
        	//可是範圍  觸發 
        	$scope.RegionController_getORG($scope.inputVO);
		}
        
        /**
		 * 匯出
		 */
    	$scope.exportRPT = function()
    	{
			$scope.sendRecv("PMS325", "export",	"com.systex.jbranch.app.server.fps.pms325.PMS325OutputVO",{
					'csvList':$scope.csvList,
					'roleList' : $scope.roleList},
					function(tota, isError) 
					{
						if (!isError) 
						{
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
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
			$scope.sendRecv("PMS325", "inquire", "com.systex.jbranch.app.server.fps.pms325.PMS325InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.paramList = [];
							$scope.csvList = [];
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.originalList = angular.copy(tota[0].body.resultList);
							$scope.paramList = tota[0].body.resultList;
							$scope.confirmFlag = true;
							for(var i = 0; i < $scope.paramList.length; i++){								
								if($scope.paramList[i].A_AO_CODE == undefined){
									$scope.confirmFlag = false;
								}
							}
							$scope.outputVO = tota[0].body;
							$scope.roleList = tota[0].body.roleList;
							$scope.csvList = tota[0].body.csvList;
							return;
						}else{
							$scope.showBtn = 'none';
						}	
			});
		};
});