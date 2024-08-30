/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS341Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS341Controller";	
		//繼承共用的組繼連動選單
		$controller('PMSRegionController', {$scope: $scope});
		$scope.mappingSet['idCat'] = [];
		$scope.mappingSet['idCat'].push(
				{LABEL: 'PS-消金業務專員', DATA: 'PS'},
				{LABEL: 'RF-轉介人', DATA: 'RF'}
				);
		$scope.init = function()
		{
			//$scope.curDate = new Date();
			$scope.inputVO = {
					sTime            : '',
					aoFlag           :'N',
					psFlag           :'Y',
					idCat            : '',
					psFlagType       :''               //psflag用途
			};
            $scope.paramList = [];
        	if(sysInfoService.getPriID()=='004')
				$scope.inputVO.psFlagType='7';
            $scope.sumFlag = false;
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
									$scope.inputVO.idCat='PS';
								
								}	
								return;
							}
				});
		    }
		    $scope.initRole();
		$scope.isq = function()
        {
    		var NowDate = new Date();
    		var yr = NowDate.getFullYear();
    		var mm = NowDate.getMonth() + 1;
    		var strmm = '';
    		var xm = '';
    		$scope.mappingSet['timeE'] = [];
    		for (var i = 0; i < 12; i++)
    		{
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
    	$scope.inquireInit = function()
		{
    		$scope.AREA_LIST = [];
			$scope.REGION_LIST = [];
			$scope.BRANCH_LIST = [];
			$scope.EMP_LIST = [];
		}
    	
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
		$scope.inquireInit();	
    	/** 上傳窗口彈出 **/
    	$scope.upload = function(sTime)
    	{
    		if($scope.parameterTypeEditForm.$invalid)
    		{
        		$scope.showErrorMsg('請選擇正確上傳檔案的年月');
        		return;
        	}
    		var dialog = ngDialog.open({
    			template: 'assets/txn/PMS341/PMS341_DETAIL.html',
    			className: 'PMS341_DETAIL',
    			showClose: false,
    			controller: ['$scope', function($scope) {
    				$scope.yearMon = sTime;
                }]
    		});
    		/**
    		 * 關閉子界面時，刷新主界面
    		 */
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel1'){
        			 $scope.inquire();
    				}
        	});
    	}
    	
    	
    	/**
		 * 匯出
		 */
    	$scope.exportRPT = function()
    	{
    		$scope.outputVO.type = $scope.flag;
			$scope.sendRecv("PMS341", "export", "com.systex.jbranch.app.server.fps.pms341.PMS341OutputVO", $scope.outputVO,
					function(tota, isError) 
					{						
						if (isError) 
						{
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
		            	}
						if (tota.length > 0) 
						{
	                		if(tota[0].body.resultList && tota[0].body.resultList.length == 0) 
	                		{
	                			$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
	                	};
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
			$scope.sendRecv("PMS341", "inquire", "com.systex.jbranch.app.server.fps.pms341.PMS341InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) 
						{
							$scope.paramList = [];
							$scope.csvList = [];
							if(tota[0].body.resultList.length == 0) 
							{
								$scope.showMsg("ehl_01_common_009");
								$scope.curDate = '';
								$scope.bonusNum = '';
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.curDate = new Date($scope.paramList[0].CREATETIME);							
							$scope.bonusNum = new Date($scope.paramList[0].BONUS_NUM);							
							
							$scope.csvList = tota[0].body.csvList;
							$scope.confirmFlag = true;
							for(var i = 0; i < $scope.paramList.length; i++){								
								if($scope.paramList[i].A_AO_CODE == undefined){
									$scope.confirmFlag = false;
								}
							}
							$scope.outputVO = tota[0].body;
							$scope.rowspan_num1 = 0;
							$scope.rowspan_num2 = 0;
							$scope.rowspan_num3 = 0;
							for (var int = 0; int < $scope.paramList.length; int++) 
							{
								$scope.row = $scope.paramList[int];
								if($scope.row.ID_CAT == "1")
								{
									$scope.rowspan_num1 += 1;
								}
								else if($scope.row.ID_CAT == "2")
								{
									$scope.rowspan_num2 += 1;
								}
								else if($scope.row.ID_CAT == "3")
								{
									$scope.rowspan_num3 += 1;
								}
							}
							return;
						}else
						{
							$scope.showBtn = 'none';
						}	
			});
		};
		
		/**
		 * 下載
		 */
		$scope.downLoad = function() {
			$scope.sendRecv("PMS341", "downLoad", "com.systex.jbranch.app.server.fps.pms341.PMS341OutputVO", {
				 'csvList': $scope.csvList}, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
				if (tota.length > 0) {
				}
			});
		}
});