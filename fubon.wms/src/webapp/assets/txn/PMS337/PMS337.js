/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS337Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService, $filter,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS337Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		//filter
		getParameter.XML(["PMS.PAY_YQD","PRD.INS_TYPE"], function(totas) {
			if (totas) {				
				$scope.mappingSet['PMS.PAY_YQD'] = totas.data[totas.key.indexOf('PMS.PAY_YQD')];
				$scope.mappingSet['PRD.INS_TYPE'] = totas.data[totas.key.indexOf('PRD.INS_TYPE')];
			}
		});
		
		var NowDate = new Date();
        var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth()+2;        
        var strmm='';
        $scope.mappingSet['timeE'] = [];
        for(var i=0; i<17; i++){
        	mm = mm -1;
        	if(mm == 0){
        		mm = 12;
        		yr = yr-1;
        	}
        	if(mm<10)
        		strmm = '0' + mm;
        	else
        		strmm = mm;        		
        	$scope.mappingSet['timeE'].push({
        		LABEL: yr+'/'+strmm,
        		DATA: yr +''+ strmm
        	});        
        }
        

        
        /*** 可示範圍  JACKY共用版  START ***/
        //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        
        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;    //目前2月1日無資料  須設定為   1970220   否則預設為   +"01"
        	//可是範圍  觸發 
        	if($scope.inputVO.sCreDate!=''){
        		$scope.RegionController_getORG($scope.inputVO);
        	}
        };
        
		$scope.curDate = new Date();
		
		var rp = "RC";
		if(sysInfoService.getRoleName().substring(0, 2) == 'FC')
			rp = "AO";
		if(sysInfoService.getRoleID() == 'A161')
			rp = "BR";
		
		/*** 可示範圍  JACKY共用版  END***/
           
       
        $scope.mappingSet['assignType'] = [];
        $scope.mappingSet['assignType'].push({LABEL:'每月底提存-已結案佣金檔', DATA:'1'},{LABEL:'每月底提存-未結案佣金檔', DATA:'2'});
        
        //***初始化****//
		$scope.init = function(){
			$scope.inputVO = {
					aoFlag           :'Y',
					psFlag           :'N',
					sTime            : '',
					
					
					sCreDate:'',    //初使年月   inputVO必要加  因可視範圍
					dataMonth : '',
					previewType: '1'				
			};
            $scope.paramList = []; 
            $scope.confirmFlag = false;
		
		
            $scope.sumFlag = false;
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
			
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
	
    
		
        /***匯出EXCEL檔***/
		$scope.exportRPT = function(){
			angular.forEach($scope.originalList, function(row, index, objs){
					angular.forEach($scope.mappingSet['PMS.PAY_YQD'], function(rows, index, objs){
						if(row.PAY_TYPE==rows.DATA)
							row.PAY_TYPE=rows.LABEL;
					});
					angular.forEach($scope.mappingSet['PRD.INS_TYPE'], function(rows, index, objs){
						if(row.INS_TYPE==rows.DATA)
							row.INS_TYPE=rows.LABEL;
					});
					
			});
			$scope.sendRecv("PMS337", "export", "com.systex.jbranch.app.server.fps.pms337.PMS337OutputVO",$scope.outputVO, {'list':$scope.originalList},
					function(tota, isError) {						
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
		            	}
						if (tota.length > 0) {
	                		if(tota[0].body.resultList && tota[0].body.resultList.length == 0) {
	                			$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
	                	};
			});
		};
        
        /****主查詢****/
		$scope.inquire = function(){
			if($scope.inputVO.sCreDate=='')
			{
				 $scope.showMsg("資料統計日期為必填欄位");	 
				return; 
			}
			
			if($scope.inputVO.previewType=='')
			{
				 $scope.showMsg("報表名稱為必填欄位");	 
				return; 
			}
			$scope.sendRecv("PMS337", "inquire", "com.systex.jbranch.app.server.fps.pms337.PMS337InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.originalList =[];
								$scope.paramList = [];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.originalList = tota[0].body.list;	
							$scope.paramList = tota[0].body.resultList;
							$scope.confirmFlag = true;
							for(var i = 0; i < $scope.paramList.length; i++){								
								if($scope.paramList[i].A_AO_CODE == undefined){
									$scope.confirmFlag = false;
								}
								if($scope.paramList[i].BONUS_YN.trim() == 'Y'){
									$scope.paramList[i].BONUS_YN = '有';
								}else{
									$scope.paramList[i].BONUS_YN = '無';
								}
							}
							$scope.outputVO = tota[0].body;
							$scope.outputVO.previewType = angular.copy($scope.inputVO.previewType);
							
//							alert(JSON.stringify($scope.paramList));
							
//							if($scope.paramList.length>=2000) {
//			            		$scope.showErrorMsgInDialog("筆數超過兩千筆，");
//							}
									
							return;
						}
			});
		};
		
		/*** 儲存功能 ***/
		$scope.save = function () {			
			$scope.sendRecv("PMS337", "save", "com.systex.jbranch.app.server.fps.pms337.PMS337InputVO", {'list':$scope.paramList, 'list2':$scope.originalList},
					function(tota, isError) {
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);
		            	}
		            	if (tota.length > 0) {
		            		$scope.showMsg('儲存成功');
		            		$scope.inquire();
		            	};		
			});
		};
			
		/*** 確定移轉功能 ***/
		$scope.confirm = function(){
			if($scope.parameterTypeEditForm.$invalid){				
	    		$scope.showErrorMsg('欄位檢核錯誤:尚有客戶未被指派新AO!');
        		return;
        	}
			
			$scope.sendRecv("PMS337", "confirm", "com.systex.jbranch.app.server.fps.pms337.SSSInputVO",  {'list':$scope.paramList},
					function(tota, isError) {
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);
		            	}
		            	if (tota.length > 0) {
		            		$scope.showMsg('確定移轉完成');
		            		$scope.inquire();
		            	};		
			});
			
		}
		
		/** 理財會員等級代碼 --> 理財會員等級名稱 **/
		var vo = {'param_type': 'CRM.VIP_DEGREE', 'desc': false};
        if(!projInfoService.mappingSet['CRM.VIP_DEGREE']) {
        	$scope.requestComboBox(vo, function(totas) {      	
        		if (totas[totas.length - 1].body.result === 'success') {        		
        			projInfoService.mappingSet['CRM.VIP_DEGREE'] = totas[0].body.result;
        			$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];        		
        		}
        	});
        } else
        	$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];
		
});
