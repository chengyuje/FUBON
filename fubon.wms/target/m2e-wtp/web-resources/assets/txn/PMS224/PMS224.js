/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS224Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS224Controller";
		
		var NowDate = new Date();
        var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth()+1;        
        var strmm='';
        $scope.mappingSet['timeE'] = [];
        for(var i=0; i<13; i++){
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
      	
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};
		
		$scope.curDate = new Date();
		
		$scope.init = function(){
			$scope.inputVO = {										
					dataMonth: '',										
					op_id: '' ,
					br_id: '',
					emp_id: ''
        	};			
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];			
		}
		$scope.inquireInit();	
		
		/***以下連動區域中心.營運區.分行別***/
		//區域中心資訊
//		$scope.genRegion = function() {
//			$scope.mappingSet['region'] = [];
//			angular.forEach(projInfoService.getAvailRegion(), function(row, index, objs){						
//				$scope.mappingSet['region'].push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});										
//			});
//        };
//        $scope.genRegion();
		
      //營運區資訊
		$scope.genArea = function() {
			$scope.inputVO.op_id = '';
			$scope.mappingSet['op'] = [];
			angular.forEach(projInfoService.getAvailArea(), function(row, index, objs){				
//				if(row.REGION_CENTER_ID == $scope.inputVO.rc_id)	
					$scope.mappingSet['op'].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});				
			});
        };
        $scope.genArea();
        
      //分行資訊
		$scope.genBranch = function() {
			$scope.inputVO.br_id = '';
			$scope.mappingSet['branch'] = [];
			angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){				
				if(row.BRANCH_AREA_ID == $scope.inputVO.op_id)			
					$scope.mappingSet['branch'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
			});
        }; 
        
      //理專資訊
        $scope.branchChange = function(){        	
        	$scope.sendRecv("PMS203", "getEmpInfo", "com.systex.jbranch.app.server.fps.pms203.PMS203InputVO", $scope.inputVO,
    				function(totas, isError) {  
        		$scope.inputVO.emp_id = '';
        		$scope.mappingSet['aoemp'] = [];
    			angular.forEach(totas[0].body.totalList, function(row, index, objs){
    				if(row.BRANCH_NBR == $scope.inputVO.br_id)
    					$scope.mappingSet['aoemp'].push({LABEL: row.AO_CODE +'-'+row.EMP_NAME, DATA: row.EMP_ID});	                 					                 					                 					                 			    					    				
    			});    			
    		}); 
        };
        
		$scope.query = function(){
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:請選擇必填欄位');
        		return;
        	}
			$scope.sendRecv("PMS224", "queryData", "com.systex.jbranch.app.server.fps.pms224.PMS224InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList=[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}														
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;							
							return;
						}						
			});
		};
		
		$scope.exportRPT = function(){
			$scope.sendRecv("PMS224", "export", "com.systex.jbranch.app.server.fps.pms224.PMS224OutputVO", $scope.outputVO,
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
