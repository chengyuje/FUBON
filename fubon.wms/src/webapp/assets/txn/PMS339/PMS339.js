/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS339Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS339Controller";	
		//繼承共用的組繼連動選單
		$controller('PMSRegionController', {$scope: $scope});
		$scope.init = function(){
			$scope.inputVO = {
					aoFlag           :'N',
					psFlag           :'Y',
					sTime            : '',
					psFlagType       : ''
			};
			if(sysInfoService.getPriID()=='004')
				$scope.inputVO.psFlagType='7';
            $scope.paramList = [];
            $scope.sumFlag = false;
            //清空共用
            $scope.REGION_LIST   =[];
            $scope.AREA_LIST     =[];
            $scope.BRANCH_LIST   =[];
            $scope.EMP_LIST      =[];
            $scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
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
		
		
	    $scope.initRole = function()
	    {
	    	$scope.sendRecv("PMS339", "getRole", "com.systex.jbranch.app.server.fps.pms339.PMS339InputVO", {},
					function(tota, isError) 
					{
						if (!isError) {							
							$scope.roleflag = tota[0].body.role[0].CNT;
							//增加權限控管
							if($scope.roleflag!='0')
							{
								$scope.psflag=false;   //DISABLE 下拉選單
								$scope.con_page=12;
							}
							else
							{
								$scope.psflag=true;    //DISABLE 下拉選單
								$scope.inputVO.idCat=1;
								$scope.con_page=11;
								
							}	
							return;
						}
			});
	    }
	    $scope.initRole();
		
		
		
		$scope.inquireInit = function(){
			$scope.AREA_LIST = [];
			$scope.REGION_LIST = [];
			$scope.BRANCH_LIST = [];
			$scope.EMP_LIST = [];
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
		
		//$scope.curDate = new Date();
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
    		$scope.outputVO.flag=$scope.roleflag;
			$scope.sendRecv("PMS339", "export",	"com.systex.jbranch.app.server.fps.pms339.PMS339OutputVO",$scope.outputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.resultList = tota[0].body.resultList;
							$scope.csvList = tota[0].body.csvList;
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
	    		$scope.showMsg('欄位檢核錯誤:請選擇必填欄位');
        		return;
        	}
			$scope.sendRecv("PMS339", "inquire", "com.systex.jbranch.app.server.fps.pms339.PMS339InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.paramList = [];
							$scope.csvList = [];
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
								$scope.curDate = '';
	                			return;
	                		}
							console.log("339");
							$scope.paramList = tota[0].body.resultList;
							$scope.csvList = tota[0].body.csvList;
							$scope.curDate = new Date($scope.paramList[0].CREATETIME);
							$scope.confirmFlag = true;
							for(var i = 0; i < $scope.paramList.length; i++){								
								if($scope.paramList[i].A_AO_CODE == undefined){
									$scope.confirmFlag = false;
								}
							}
							$scope.outputVO = tota[0].body;
							$scope.year = $scope.paramList[0].WORK_YY;
							$scope.month = $scope.paramList[0].WORK_MM;
							angular.forEach($scope.paramList, function(row, index, objs){
								$scope.state = $scope.paramList[index].CONTRACT_STATE;
								
							});
							for(var i = 0; i < $scope.csvList.length; i++){								
								if($scope.csvList[i].CONTRACT_STATE == "原始總保費／佣金收入合計"||$scope.csvList[i].CONTRACT_STATE == "扣除契約撤銷保費／佣金收入合計"){
									tota[0].body.totalRecord--; 
								}
							}
							return;
						}else{
							$scope.showBtn = 'none';
						}	
			});
		};
});