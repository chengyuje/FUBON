/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS348Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS348Controller";
		$controller('PMSRegionController', {$scope: $scope});
		
		$scope.init = function(){
			$scope.inputVO = {					
					aoFlag           :'Y',
					psFlag           :'N',
					sTime            : '',
					
					
					region_center_id  :'',   //區域中心
					branch_area_id  :'',	//營運區
					branch_nbr:'',			//分行
					ao_code  :'',			//理專
					sCreDate      :'',
					sCreDate_OK   :''   //畫面的
           };
			$scope.paramList=[];
			$scope.csvList=[];
			$scope.outputVO={};
			$scope.curDate = new Date();
			$scope.showBtn = 'none';
		 
			
			
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
		
		var NowDate = new Date();
		
		var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth()+2;
        var strmm='';
        $scope.mappingSet['timeE'] = [];
        for(var i=0; i<36; i++){
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
        
        
        
        /***TEST ORG COMBOBOX START***/
        var org = [];

        
        //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	
        if($scope.inputVO.sCreDate!='')
      	 { 	
        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
        	$scope.RegionController_getORG($scope.inputVO);
      	  }
        };
        
     
        /***ORG COMBOBOX END***/
        
        
       
	        
	        
	        
	        
	        
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};				
		
	
		$scope.inquireInit = function(){
		
			$scope.paramList = [];
			$scope.originalList = [];
		}
		$scope.inquireInit();
		
		$scope.query = function(){
			
			if($scope.inputVO.sCreDate==undefined)
			{
				 $scope.showMsg("資料統計日期為必填欄位");	 
				return; 
			}
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:月份必要輸入欄位');
        		return;
        	}
			$scope.sendRecv("PMS348", "queryCDate", "com.systex.jbranch.app.server.fps.pms348.PMS348InputVO", $scope.inputVO,
					function(tota, isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.inputVO.sCreDate_OK = '';
				}else{
					
					//日期組字串
					if(tota[0].body.resultList[0].CREATDATE !=null){
						$scope.CREATE_DATE = tota[0].body.resultList[0].CREATDATE;				
						$scope.CREATE_DATE_YEAR = $scope.CREATE_DATE.substr(0,4);
						$scope.CREATE_DATE_MONTH = $scope.CREATE_DATE.substr(5,2);
						$scope.CREATE_DATE_DAY = $scope	.CREATE_DATE.substr(8,2);
						$scope.CREATE_DATE = $scope.CREATE_DATE_YEAR + "/" + $scope.CREATE_DATE_MONTH + "/" + $scope.CREATE_DATE_DAY;
					//該月沒建表才有可能發生的例外狀況
					}else{			
						$scope.CREATE_DATE ='';
					}	
					
				}
			});
			
			$scope.sendRecv("PMS348", "queryData", "com.systex.jbranch.app.server.fps.pms348.PMS348InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList=[];
								$scope.csvList=[];
								$scope.outputVO={}
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							for(var i = 0; i < tota[0].body.resultList.length; i++) {
								
							}
							$scope.originalList = angular.copy(tota[0].body.resultList);
							$scope.paramList = tota[0].body.resultList;
						
							$scope.csvList=tota[0].body.csvList;
							$scope.outputVO = tota[0].body;
							$scope.showBtn = 'block';
							return;
						}else{
							$scope.showBtn = 'none';
						}						
			});
		};
		$scope.save = function () {
			
			
			
			$scope.sendRecv("PMS348", "save", "com.systex.jbranch.app.server.fps.pms348.PMS348InputVO", {'List':$scope.paramList, 'List2':$scope.originalList},
					function(tota, isError) {
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);
		            	}
		            	if (tota.length > 0) {
		            		$scope.showMsg('儲存成功');
		            		$scope.query();
		            	};		
			});
		};
		

    	$scope.export = function() {
			
			$scope.sendRecv("PMS348", "export",
					"com.systex.jbranch.app.server.fps.pms348.PMS348OutputVO",
					{'list':$scope.csvList}, function(tota, isError) {
						if (!isError) {
						
				
							return;
						}
					});
		};
});
