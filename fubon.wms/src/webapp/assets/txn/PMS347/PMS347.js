/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS347Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,$filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS347Controller";
		$controller('PMSRegionController', {$scope: $scope});
		
		
		$scope.init = function(){
			var Today= undefined;//new Date("2016/06/01");
			$scope.inputVO = {
					aoFlag           :'Y',
					psFlag           :'N',
					sTime            : '',
					
					
					region_center_id  :'',   //區域中心
					branch_area_id  :'',	//營運區
					branch_nbr:'',			//分行
					ao_code  :'',			//理專
					sCreDate      :undefined,
					sCreDate_OK :undefined,
					sc: ''
			       		
			};
			$scope.startMaxDate = $scope.maxDate;
            $scope.endMinDate = $scope.minDate;
            $scope.paramList = [];
            $scope.curDate = new Date();	
           
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
		 $scope.initquery=function(){
			 $scope.paramList = [];
			 
		 }	
		
		
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
					$event.preventDefault();
					$event.stopPropagation();
					$scope.model[elementOpened] = !$scope.model[elementOpened];
	     };
		        
	
	
		
//		$scope.isq = function(){
//	    	    
//	            var NowDate=new Date();               
//	            var t=NowDate.setMonth(NowDate.getMonth()-6);       
//	            var susdate=new Date(t);
//	            var y=susdate.getFullYear();
//	            var m=susdate.getMonth()+1;
//	            var xm='';
//	            $scope.mappingSet['timeE'] = [];
//	            for(var i=0;i<7;i++){
//	            	if(m<=9){xm='0'+m;}
//	            	if(m>=10){xm=m;}            	
//	            		$scope.mappingSet['timeE'].push({
//	    					LABEL : y+'/'+xm,
//	    					DATA : y+''+xm
//	            		});            		
//	            		if(m<=11)
//	            			{	
//	            			m=m+1;               		
//	            			}
//	            		if(m==12)
//	            			{
//	            			m=1;
//	            			y=y+1;            		
//	            			}
//	            }
//		 };
//	    $scope.isq();
		
		
        /***以下連動區域中心.營運區.分行別***/


        
        
      //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	 if($scope.inputVO.sCreDate!='') { 	
        		$scope.inputVO.reportDate = $filter('date')($scope.inputVO.sCreDate,'yyyyMMdd');
      	       	$scope.RegionController_getORG($scope.inputVO);
      	   }
        };  
        
   	
		
	    $scope.export = function() {
			$scope.sendRecv("PMS347", "export",
					"com.systex.jbranch.app.server.fps.pms347.PMS347OutputVO",
					{'list':$scope.csvList}, function(tota, isError) {
						if (!isError) {
							return;
						}
					});
			};
			
			
			
		$scope.inquire = function(){
			if($scope.inputVO.sCreDate==undefined)
			{
				$scope.showMsg("資料統計日期為必填欄位");	 
				return; 
			}
				$scope.sendRecv("PMS347", "inquire", "com.systex.jbranch.app.server.fps.pms347.PMS347InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.resultList.length == 0) {
									$scope.paramList=[];
									$scope.csvList=[];
									$scope.inputVO.sCreDate_OK=undefined;
									$scope.showMsg("ehl_01_common_009");
		                			return;
		                		}
								$scope.paramList = tota[0].body.resultList;
								angular.forEach($scope.paramList, function(row, index, objs){
									row.CUST_IDS=row.CUST_ID.substring(0, 4)+"****"+row.CUST_ID.substring(8, 10);
								});	
								$scope.csvList=tota[0].body.csvList;
								$scope.inputVO.sCreDate_OK=$scope.inputVO.sCreDate;
								$scope.outputVO = tota[0].body;
								return;
							}
				});
			};
		$scope.chnum = function (ind) {
			$scope.mappingSet[ind.leng]=0;
			$scope.mappingSet[ind.leng]=ind.leng;
		};
		
});
