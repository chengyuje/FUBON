/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS225Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,$filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS225Controller";	
		//繼承共用的組繼連動選單
		$controller('PMSRegionController', {$scope: $scope});
		$scope.mappingSet['rtype'] = [];
		$scope.mappingSet['rtype'].push(
				{LABEL: '總表', DATA: 1},
				{LABEL: '房信貸', DATA: 2},
				{LABEL: '信用卡', DATA: 3}
				);
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};
		$scope.init = function(){
			$scope.inputVO = {
					sTime            : '',
					emp_id           : '',
					branch_nbr       : '',
					region_center_id : '',
					branch_area_id   : '',
					psFlag           : 'Y',
					aoFlag			 : 'N',
					rtype            : ''
			};
		
            $scope.sumFlag = false;
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
			$scope.disableEmpCombo = false;
			
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
		$scope.inquireInit = function(){
			$scope.AREA_LIST = [];
			$scope.REGION_LIST = [];
			$scope.BRANCH_LIST = [];
			$scope.AO_LIST = [];
			$scope.EMP_LIST = [];
			$scope.paramList = [];
			$scope.csvList = [];
		}
		
		$scope.rptTypeChange = function(){
			$scope.paramList = [];
			$scope.csvList = [];
		};
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
    	/**
		 * 匯出
		 */
    	$scope.exportRPT = function(){
			$scope.sendRecv("PMS225", "export",	"com.systex.jbranch.app.server.fps.pms225.PMS225OutputVO",
					{'list':$scope.csvList,
					'rptType' : $scope.inputVO.rptType},
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
			$scope.sendRecv("PMS225", "inquire", "com.systex.jbranch.app.server.fps.pms225.PMS225InputVO", $scope.inputVO,
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
							$scope.confirmFlag = true;
							for(var i = 0; i < $scope.paramList.length; i++){								
								if($scope.paramList[i].A_AO_CODE == undefined){
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
