/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM3104Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$controller('PMSRegionController', {$scope: $scope});
		$scope.controllerName = "CRM3104Controller";
		
		//權限: 043, 044 總行分行管理科人員
		$scope.privilegeId = projInfoService.getPriID()[0];
		$scope.isHeadMgr = ($scope.privilegeId == '043' || $scope.privilegeId == '044');
		
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		//選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	//設定回傳時間
        	if($scope.inputVO.sCreDate != '' && $scope.inputVO.sCreDate != null && $scope.inputVO.sCreDate != undefined){
        		$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
        		$scope.RegionController_getORG($scope.inputVO);
        	} else {
        		let defaultDate = new Date().setDate(new Date().getDate() - 14);
		        let defaultEndDate = new Date();
        		$scope.inputVO.reportDate = defaultDate;
        		$scope.RegionController_getORG($scope.inputVO);
        	}
        };
        
		// combobox
    	getParameter.XML(["FUBONSYS.FC_ROLE", "FUBONSYS.BMMGR_ROLE", "FUBONSYS.ARMGR_ROLE", "CRM.TRS_PRJ_ROT_STEP_STATUS", "CRM.TRS_PRJ_ROT_STEP1_BMMGR", "CRM.TRS_TYPE"], function(totas) {
    		if (totas) {
    			$scope.mappingSet['FUBONSYS.BMMGR_ROLE'] = totas.data[totas.key.indexOf('FUBONSYS.BMMGR_ROLE')];
    			$scope.mappingSet['FUBONSYS.FC_ROLE'] = totas.data[totas.key.indexOf('FUBONSYS.FC_ROLE')];
    			$scope.mappingSet['FUBONSYS.ARMGR_ROLE'] = totas.data[totas.key.indexOf('FUBONSYS.ARMGR_ROLE')];
    			$scope.mappingSet['CRM.TRS_PRJ_ROT_STEP_STATUS'] = totas.data[totas.key.indexOf('CRM.TRS_PRJ_ROT_STEP_STATUS')];
    			$scope.mappingSet['CRM.TRS_PRJ_ROT_STEP1_BMMGR'] = totas.data[totas.key.indexOf('CRM.TRS_PRJ_ROT_STEP1_BMMGR')]; //第一階段可覆核主管角色
    			$scope.mappingSet['CRM.TRS_TYPE'] = totas.data[totas.key.indexOf('CRM.TRS_TYPE')];
    			
    			debugger
    			//是否為理專角色
    			var findfc = $filter('filter')($scope.mappingSet['FUBONSYS.FC_ROLE'], {DATA: sysInfoService.getRoleID()});
				$scope.isFC = (findfc != null && findfc.length > 0) ? true : false;
				
				//是否為第一階段分行主管角色
				var findbmmS1 = $filter('filter')($scope.mappingSet['CRM.TRS_PRJ_ROT_STEP1_BMMGR'], {DATA: sysInfoService.getRoleID()});
				$scope.isBMMGRStep1 = (findbmmS1 != null && findbmmS1.length > 0) ? true : false;
				
				//是否為分行主管角色
				var findbmm = $filter('filter')($scope.mappingSet['FUBONSYS.BMMGR_ROLE'], {DATA: sysInfoService.getRoleID()});
				$scope.isBMMGR = (findbmm != null && findbmm.length > 0) ? true : false;
    			
				//是否為處主管角色
				var findarm = $filter('filter')($scope.mappingSet['FUBONSYS.ARMGR_ROLE'], {DATA: sysInfoService.getRoleID()});
				$scope.isARMGR = (findarm != null && findarm.length > 0) ? true : false;
    		}
    	});
    	
		//Project combobox
		$scope.getAllPRJ = function() {
			$scope.sendRecv("CRM3103", "getAllPRJ", "com.systex.jbranch.app.server.fps.crm3103.CRM3103InputVO",{},
					function(tota, isError) {
					   if(!isError){
						   	$scope.PROJNAME = [];
		                	angular.forEach(tota[0].body.allPRJ, function(row) {
		                		$scope.PROJNAME.push({LABEL: row.PRJ_NAME, DATA: row.PRJ_ID});
		                	});
					   }
				});
		};
		$scope.getAllPRJ();
		
		$scope.inquireInit = function() {
			$scope.resultList = [];
			$scope.data = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		$scope.init = function() {
			$scope.checkVO = {};
			
			/** InputVO 初始，預設日期為系統日 **/
            let defaultDate = new Date().setDate(new Date().getDate() - 14);
            let defaultEndDate = new Date();
            $scope.inputVO = {
            	/** 連動組織會用到的參數 **/
	            region_center_id	: undefined,
	            branch_area_id		: undefined,
	            branch_nbr			: undefined,
	            reportDate			: $filter('date')(defaultDate, 'yyyyMM'),
	            custId      : ''
            };
			$scope.dateChange();
			$scope.inquireInit();
		};
		$scope.init();
		
		//查詢
		$scope.inquire = function() {
			$scope.resultList = [];
			$scope.inputVO.bra_nbr = $scope.inputVO.branch_nbr;
			$scope.sendRecv("CRM3104", "inquire", "com.systex.jbranch.app.server.fps.crm3104.CRM3104InputVO",$scope.inputVO,
				function(tota, isError) {
					if(!isError) {
						if (tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
						}
						else {
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
		               	}
				   }
			});
		};
		
		//匯出
		$scope.exportData = function() {
			$scope.sendRecv("CRM3104","export","com.systex.jbranch.app.server.fps.crm3104.CRM3104InputVO", 
					{'printList': $scope.resultList}, function(tota, isError) {
						if (isError) {							
							return;            		
		            	}
					});			
		};
});