/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS305Controller',
	function($rootScope,$scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS305Controller";
		$controller('PMSRegionController', {$scope: $scope});
		
		$scope.init = function(){
			
			$scope.sendRecv("PMS305", "queryDiscount", "com.systex.jbranch.app.server.fps.pms305.PMS305InputVO", $scope.inputVO,
      				function(tota, isError) {
      					if (!isError) {							
      						if(tota[0].body.discountList == 0) {
      							$scope.inputVO.PD_RATE="";
      							$scope.showMsg("ehl_01_common_009");								
                      			return;
                      		}
      						$scope.inputVO.SALE_RATE = tota[0].body.discountList[0].PARAM_NAME_EDIT+"";
      						$scope.inputVO.PD_RATE = tota[0].body.discountList[1].PARAM_NAME_EDIT+"";
      								
      						return;
      					}
      		});
			
			var Noed=undefined;//new Date("2016/07/29");
			$scope.inputVO = {					
					
					aoFlag           :'Y',
					psFlag           :'N',
					sTime            : '',
					
					
					branch_name:'',
					region_name:'',					
					INS_ID:'',
					INS_NAME:'',
					
					region_center_id  :'',   //區域中心
					branch_area_id  :'',	//營運區
					branch_nbr:'',			//分行
					ao_code  :'',			//理專
					sCreDate      :undefined				
        	};
			$scope.curDate = new Date();
			$scope.showBtn = 'none';
			$scope.checked3='0';
			$scope.paramList=[];
		    $scope.d=[]; 
		
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
		$rootScope.$on("rootEvent", function(e,data){
			 if(data==-1)
				 $scope.inputVO.INS_NAME='';
			 else
				 $scope.inputVO.INS_NAME=data;
		
		});
		$scope.altInputFormats = ['M!/d!/yyyy'];
    	//時間
        $scope.model = {};
    	$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
	
		
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
        
        //選取月份下拉選單 --> 重新設定可視範圍
        
        $scope.dateChange = function(){
        	
        	
    	   	
    		  if($scope.inputVO.sCreDate!=undefined) 
    		  {
    			  
    			  $scope.inputVO.reportDate = $filter('date')($scope.inputVO.sCreDate,'yyyyMMdd');
    			  $scope.RegionController_getORG($scope.inputVO);
    		  
    		  }
    	 
 
    }; 
        
        
     
        $scope.qu=function(INS_ID){
        	var dialog = ngDialog.open({			      
		        template: 'assets/txn/PMS305/PMS305_QUERY.html',
		        className: 'PMS305_QUERY',
		        controller: ['$scope', function($scope) {
		        	$scope.INS_ID=INS_ID;
		        	//$scope=$scope.inputVO.INS_ID;
		        }]
            });     
         }  
        
		$scope.inquireInit = function(){
			$scope.paramList = [];
			$scope.originalList = [];
			$scope.totalList = [];
		}
		$scope.inquireInit();
		
		$scope.as=function(str){
			var s=JSON.stringify(str)+"";
			return s.substring(1,5)+'/'+s.substring(5,7)+'/'+s.substring(7,9);				
		}
		
		$scope.query = function(){
			//alert($scope.inputVO.region_center_id)
			if($scope.inputVO.sCreDate==undefined) {
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:日期必要輸入欄位');
        		return;
        	}
			
			
			$scope.sendRecv("PMS305", "queryData", "com.systex.jbranch.app.server.fps.pms305.PMS305InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList =[];
								$scope.csvList=[];
								$scope.outputVO=[];
								$scope.checked3='0';
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.originalList = angular.copy(tota[0].body.resultList);
							$scope.paramList = tota[0].body.resultList;
							$scope.csvList=tota[0].body.csvList;
							$scope.outputVO = tota[0].body;
							$scope.totalList = tota[0].body.totalList;               //全行合計
							
							return;
						}				
			});
		};
		
		$scope.save = function () {
			$scope.sendRecv("PMS305", "save", "com.systex.jbranch.app.server.fps.pms305.PMS305InputVO", {'List':$scope.paramList, 'List2':$scope.originalList},
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
			$scope.sendRecv("PMS305", "export",
					"com.systex.jbranch.app.server.fps.pms305.PMS305OutputVO",
					{ 'list'      : $scope.csvList,
					  'totalList' : $scope.totalList }, function(tota, isError) {
						if (!isError) {
							return;
						}
					});
		};
});
