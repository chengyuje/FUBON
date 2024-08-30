/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS313Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$controller('PMSRegionController', {$scope: $scope});
		$scope.controllerName = "PMS313Controller";
		$scope.dateChange = function(){
	        if($scope.inputVO.sCreDate!=''){ 	
	        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
	        	$scope.RegionController_getORG($scope.inputVO);
	        }
	    };
		/**報表類型下拉選單內容**/
        $scope.mappingSet['sType'] = [];
        $scope.mappingSet['sType'].push(
        		{LABEL: '房信貸分行業績戰報', DATA:'RPT01'},        		
        		{LABEL: '好運貸業績戰報', DATA:'RPT02'}
//        		,{LABEL: '房貸法金+個金報表', DATA:'RPT03'}
        );
		
        /**統計類型下拉選單內容**/
        $scope.mappingSet['pType'] = [];
        $scope.mappingSet['pType'].push(
        		{LABEL: 'MTD', DATA:'MTD'},
        		{LABEL: 'YTD', DATA:'YTD'}
        );
        
        /** 報表月份 **/
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
      	
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};
		
		$scope.curDate = new Date();
		$scope.sType = 'RPT01';
		
		$scope.init = function(){
			$scope.inputVO = {
					sCreDate: '',
					sType: '',
					pType: '',
					region_center_id: '',
					branch_area_id: '',
					branch_nbr: '',
					emp_id: ''
        	};	
			$scope.paramList = [];	
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];			
		}
		$scope.inquireInit();	
		
        $scope.numGroups = function(input){
        	if (input == null) {
        		return;
        	}
        		
        	return Object.keys(input).length;
        }
        
        //總和
        $scope.getSum = function(group, key) {
        	var sum = 0;
        	for (var i = 0; i < group.length; i++){
        		sum += group[i][key];
        	}  
        	return sum;
        }
        
        //平均
        $scope.getAve = function(group, key1, key2) {
        	var sum1 = 0;
        	var sum2 = 0;
        	var ave = 0;
        	for (var i = 0; i < group.length; i++){
        		sum1 += group[i][key1];
        		sum2 += group[i][key2];
        	}
        	if(sum1 * sum2 != 0){
        		ave = (sum1 / sum2) * 100;
        	}
        	return ave;
        } 
        
		$scope.query = function(){
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:請選擇「＊」欄位');
        		return;
        	}		
			$scope.sType = $scope.inputVO.sType;
			$scope.sendRecv("PMS313", "queryData", "com.systex.jbranch.app.server.fps.pms313.PMS313InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList = [];			
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}														
							$scope.paramList = tota[0].body.resultList;							
							$scope.outputVO = tota[0].body;
							$scope.outputVO.rptType = angular.copy($scope.inputVO.sType);
							return;																				
						}						
			});			
		};
		
		/** 匯出EXCEL檔 **/
		$scope.exportRPT = function(){
			$scope.sendRecv("PMS313", "export", "com.systex.jbranch.app.server.fps.pms313.PMS313OutputVO", $scope.outputVO,
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
