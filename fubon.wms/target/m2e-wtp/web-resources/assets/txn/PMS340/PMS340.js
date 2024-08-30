/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS340Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService) 
	{		
		$controller('PMSRegionController', {$scope: $scope});		
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS340Controller";	
		$scope.mappingSet['idCat'] = [];
		$scope.mappingSet['idCat'].push(
				{LABEL: 'PS', DATA: 1},
//				{LABEL: '業務主管', DATA: 2},    //20170616 註解掉業務主管
				{LABEL: '轉介人', DATA: 3}
				);
		//新增權限控管
		
		//使用共用可視範圍		
		$scope.dateChange = function(){
	        if($scope.inputVO.sTime!=''){ 	
	        	$scope.inputVO.reportDate = $scope.inputVO.sTime;
	        	$scope.RegionController_getORG($scope.inputVO);
	        }
	    };
	    $scope.init = function()
		{
			$scope.inputVO = {
					sTime : '', 
					idCat : '',
					aoFlag: 'N',
		            psFlag: 'Y',
		            psFlagType:''
			};
			if(sysInfoService.getPriID()=='004')
				$scope.inputVO.psFlagType='7';
            $scope.paramList = [];
            $scope.csvList = [];
            //清空共用
            $scope.REGION_LIST   =[];
            $scope.AREA_LIST     =[];
            $scope.BRANCH_LIST   =[];
            $scope.EMP_LIST      =[];
            $scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableEmpCombo = false;
			
            $scope.dateChange();
            $scope.counts=0;  //總筆數
            
		};
		$scope.init();
		
	    $scope.initRole = function()
	    {
	    	$scope.sendRecv("PMS340", "getRole", "com.systex.jbranch.app.server.fps.pms340.PMS340InputVO",{},
					function(tota, isError) 
					{
						if (!isError) {							
							$scope.roleflag = tota[0].body.role[0].CNT;
							//增加權限控管
							if($scope.roleflag=='1')
							{
								$scope.psflag=false;   //DISABLE 下拉選單
							}
							else
							{
								$scope.psflag=true;    //DISABLE 下拉選單
								$scope.inputVO.idCat=1;
							
							}	
							return;
						}
			});
	    }
	    $scope.initRole();
		
		
		
		$scope.idCatChange = function()
		{
			$scope.paramList = [];
			$scope.csvList = [];
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
		 * 整批上傳功能
		 * 
		 */
		$scope.upload = function(yearmon){
//			$scope.inputVO.sTime  = $scope.inputVO.sCreDate;
			/*判斷計積年月是否為空*/
			if(''==$scope.inputVO.sTime || null==$scope.inputVO.sTime){
				$scope.showErrorMsg("尚未選擇資料年月");
				return;
			}
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS340/PMS340_UPLOAD.html',
				className: 'PMS340_UPLOAD',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.yearMon = yearmon;
					$scope.uploadName = 'importData';
	            }]
			});
			dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        		//	 $scope.query();
 				}
        	});
			
		}  	
        
    	/**
		 * 匯出
		 */
    	$scope.exportRPT = function()
    	{
			$scope.sendRecv("PMS340", "export",	
					"com.systex.jbranch.app.server.fps.pms340.PMS340OutputVO",{
					'csvList':$scope.csvList,
					'idCat' : $scope.inputVO.idCat,
					'flag':$scope.roleflag},
					function(tota, isError) {
						if (!isError) {
							return;
						}
					});
			};		
			
		/**
		 * 匯出轉介人
		 */
	    $scope.exportRefRPT = function()
	    {
	    	if(''==$scope.inputVO.sTime || null==$scope.inputVO.sTime){
				$scope.showErrorMsg("尚未選擇資料年月");
				return;
			}
			$scope.sendRecv("PMS340", "exportRef",	
					"com.systex.jbranch.app.server.fps.pms340.PMS340OutputVO",{
					'sTime' : $scope.inputVO.sTime,
					'flag':$scope.roleflag},
					function(tota, isError) {
						if (!isError) {
							return;
						}
					});
		};			
    			
		/**
		 * 計算獎勵金
		 */
	    $scope.callStored = function()
	    {
	    	if(''==$scope.inputVO.sTime || null==$scope.inputVO.sTime){
				$scope.showErrorMsg("尚未選擇資料年月");
				return;
			}
	    	
	    	$confirm({text: '是否要計算房貸壽險獎勵金?'}, {size: 'sm'}).then(function(){
				$scope.sendRecv("PMS340", "callCalcProc",	
						"com.systex.jbranch.app.server.fps.pms340.PMS340OutputVO",{
					    'sTime' : $scope.inputVO.sTime,
						'flag':$scope.roleflag},
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.errorMessage=='') {
									$scope.showMsg("計算房貸壽險獎勵金成功!");  
			            		}else{
			            			$scope.showMsg("計算房貸壽險獎勵金失敗!");  
			            		}
							}
						});
	    	})
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
			$scope.sendRecv("PMS340", "inquire", "com.systex.jbranch.app.server.fps.pms340.PMS340InputVO", $scope.inputVO,
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
							//2017066
							$scope.counts=tota[0].body.resultList.length;  //總筆數
							$scope.counts--; //有資料先減一
							angular.forEach($scope.paramList, function(row, index, objs){
								if(row.POLICY_NO=='-'){ //篩選
									$scope.counts--;
								}
							});
							$scope.csvList=tota[0].body.csvList;
							$scope.outputVO = tota[0].body;
							
							return;
						}else
						{
							$scope.showBtn = 'none';
						}	
			});
		};
});