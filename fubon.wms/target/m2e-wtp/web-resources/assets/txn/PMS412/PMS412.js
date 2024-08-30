/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS412Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS412Controller";
		$controller('PMSRegionController', {$scope: $scope});
		
		
		var NowDate = new Date();
        var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth()+1;
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
        
      
 
   
		        
        /***ORG COMBOBOX END***/
        
        
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};				
		
		$scope.init = function(){
			$scope.inputVO = {	
					aoFlag           :'Y',
					psFlag           :'N',
					sTime            : '',
					
					
					region_center_id  :'',   //區域中心
					branch_area_id  :'',	//營運區
					branch_nbr:'',			//分行
					emp_id  : '',			//員編
					emp_id1 : '',			//員編(input)
					sCreDate      :undefined
        	};
			$scope.curDate = new Date();
	
			$scope.paramList = [];
			$scope.outputVO={totalList:[]};
			$scope.inputVO.sCreDate=new Date();
			
			$scope.sumFlag = false;
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			//$scope.disableEmpCombo = false;
			
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
		
		
		//選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	 if($scope.inputVO.sCreDate!='') { 	
        		$scope.inputVO.reportDate = $filter('date')($scope.inputVO.sCreDate,'yyyyMMdd');
      	       	$scope.RegionController_getORG($scope.inputVO);
      	   }
        };  
		
        $scope.dateChange();
		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];
		}
		$scope.inquireInit();
		
		$scope.query = function(){
			
			if($scope.inputVO.emp_id1 != ''){
				$scope.inputVO.emp_id = $scope.inputVO.emp_id1;
			}
			
			$scope.sendRecv("PMS412", "queryData", "com.systex.jbranch.app.server.fps.pms412.PMS412InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList = [];
								$scope.totalData =[];
								$scope.showMsg("查無此人");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.totalData = tota[0].body.totalList;
							$scope.outputVO = tota[0].body;
							return;
						}						
			});
		};
		
		$scope.exportRPT = function(){
			$scope.sendRecv("PMS412", "export", "com.systex.jbranch.app.server.fps.pms412.PMS412OutputVO", $scope.outputVO,
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
});
