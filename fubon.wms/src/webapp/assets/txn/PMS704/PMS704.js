/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS704Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$controller('PMSRegionController', {$scope: $scope});
		$scope.controllerName = "PMS704Controller";	
		$scope.init = function(){
			$scope.inputVO = {
					sCreDate:'',    //初使年月   inputVO必要加  因可視範圍
					assignType: '1',
					role    : ''			
			};
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
            $scope.paramList = [];
            
            //判斷理專
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
		
		$scope.inquireInit = function(){
			$scope.AREA_LIST = [];
			$scope.REGION_LIST = [];
			$scope.BRANCH_LIST = [];
			$scope.AO_LIST = [];
		}
		$scope.inquireInit();
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
	    	
	    	$scope.dateChange = function(){
	    		$scope.inquireInit();
	    		if($scope.inputVO.sCreDate==''){
	        		$scope.init();
	        		return;
	        	}
	        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
	   	    	$scope.RegionController_getORG($scope.inputVO);
	   	    };
    	/**
		 * 匯出
		 */
    	$scope.exportRPT = function(){
			$scope.sendRecv("PMS704", "export",	"com.systex.jbranch.app.server.fps.pms704.PMS704OutputVO",{'list':$scope.csvList},
					function(tota, isError) {
						if (!isError) {
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
			$scope.inputVO.sTime = $scope.inputVO.sCreDate;
			$scope.inputVO.region = $scope.inputVO.region_center_id;
			$scope.inputVO.op = $scope.inputVO.branch_area_id;
			$scope.inputVO.branch = $scope.inputVO.branch_nbr;
			$scope.sendRecv("PMS704", "inquire", "com.systex.jbranch.app.server.fps.pms704.PMS704InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.paramList = [];
							$scope.csvList = [];
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.csvList=tota[0].body.csvList;
							$scope.outputVO = tota[0].body;
							return;
						}else{
							//如果用拋出錯誤  "查無資料"   清空結果LIST
							$scope.paramList = [];
							$scope.csvList = [];
							$scope.showBtn = 'none';
						}	
			});
		};
});