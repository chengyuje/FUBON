/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS331Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS331Controller";
	
		var NowDate = new Date();
        var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth()+1;        
        var strmm='';
        $scope.mappingSet['timeE'] = [];
        for(var i=0; i<6; i++){
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
        
        $scope.mappingSet['assignType'] = [];
        $scope.mappingSet['assignType'].push({LABEL:'全部', DATA:'1'},{LABEL:'已分派', DATA:'2'},{LABEL:'未分派', DATA:'3'});
        
		$scope.init = function(){
			$scope.inputVO = {
					dataMonth : '',
					rc_id  : '',
					op_id  : '',
					br_id  : '',
					aoEmp  : '',
					assignType: '1'				
			};
			$scope.curDate = new Date();
            $scope.paramList = []; 
            $scope.confirmFlag = false;
		};
		$scope.init();
	
		 /***以下連動區域中心.營運區.分行別***/
		//區域中心資訊
		$scope.genRegion = function() {
			$scope.mappingSet['region'] = [];
			angular.forEach(projInfoService.getAvailRegion(), function(row, index, objs){						
				$scope.mappingSet['region'].push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});										
			});
        };
        $scope.genRegion();
		
      //營運區資訊
		$scope.regionChange = function() {
			$scope.inputVO.op_id = '';
			$scope.mappingSet['op'] = [];
			angular.forEach(projInfoService.getAvailArea(), function(row, index, objs){				
				if(row.REGION_CENTER_ID == $scope.inputVO.rc_id)	
					$scope.mappingSet['op'].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});				
			});
        };
        
      //分行資訊
		$scope.areaChange = function() {
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
        		$scope.inputVO.aoEmp = '';
        		$scope.mappingSet['aoemp'] = [];
    			angular.forEach(totas[0].body.totalList, function(row, index, objs){
    				if(row.BRANCH_NBR == $scope.inputVO.br_id)
    					$scope.mappingSet['aoemp'].push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});	                 					                 					                 					                 			    					    				
    			});    			
    		}); 
        };
        /**** 下拉連動END ****/
			
        $scope.getAOEMP = function(row){          	
        	$scope.sendRecv("PMS203", "getEmpInfo", "com.systex.jbranch.app.server.fps.pms203.PMS203InputVO", {'br_id':row},
    				function(totas, isError) {        		
        		$scope.mappingSet[row] = [];        		
    			angular.forEach(totas[0].body.totalList, function(rows, index, objs){
//    				if(row.BRANCH_NBR == rows.BRANCH_NBR)
    					$scope.mappingSet[row].push({LABEL: rows.EMP_NAME, DATA: rows.AO_CODE});	                 					                 					                 					                 			    					    				
    			});    			
    		}); 
        };
        $scope.getAOEMP();
        
		$scope.inquire = function(){
			$scope.sendRecv("PMS331", "inquire", "com.systex.jbranch.app.server.fps.pms331.PMS331InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.originalList = angular.copy(tota[0].body.resultList);
							$scope.paramList = tota[0].body.resultList;													
							$scope.confirmFlag = true;
							for(var i = 0; i < $scope.paramList.length; i++){								
								if($scope.paramList[i].A_AO_CODE == undefined){
									$scope.confirmFlag = false;
//									alert("ff="+$scope.confirmFlag);
								}
							}
							$scope.outputVO = tota[0].body;	
							return;
						}
			});
		};
		
		/*** 儲存功能 ***/
		$scope.save = function () {			
			$scope.sendRecv("PMS331", "save", "com.systex.jbranch.app.server.fps.pms331.PMS331InputVO", {'list':$scope.paramList, 'list2':$scope.originalList},
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
			
			$scope.sendRecv("PMS331", "confirm", "com.systex.jbranch.app.server.fps.pms331.PMS331InputVO",  {'list':$scope.paramList},
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
