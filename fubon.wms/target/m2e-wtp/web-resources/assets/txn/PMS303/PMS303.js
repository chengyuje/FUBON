/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS303Controller',
	function($scope, $controller, socketService, sysInfoService, ngDialog, projInfoService, $q, $confirm, $filter,$timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS303Controller";
		$controller('PMSRegionController', {$scope: $scope});
		$scope.pri = '';
		$scope.pri = String(sysInfoService.getPriID());
		
		//待覆核權限
		if ($scope.pri == '045' || $scope.pri == '046' ) {
			$scope.pri_type = 'show';
		}else{			
			$scope.pri_type = 'hide';
		}
		
		$scope.inputVO.pri_type = $scope.pri_type;
		var Today=new Date();
			
		/***TEST ORG COMBOBOX START***/
        var org = [];
        //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	if($scope.inputVO.sCreDate!=''){
           		$scope.inputVO.reportDate =$filter('date')($scope.inputVO.sCreDate,'yyyyMMdd');
           		$scope.RegionController_getORG($scope.inputVO);
        		$scope.inputVO.sTime = $scope.inputVO.sCreDate;
           	} 	
        }; 
        /***ORG COMBOBOX END***/
        
        $scope.init = function(){
			$scope.inputVO = {
					aoFlag           	: 'Y',
					psFlag           	: 'N',
					sTime				: '',
					region_center_id  	: '',   			//區域中心
					branch_area_id  	: '',			//營運區
					branch_nbr			: '',			//分行
					ao_code  			: '',			//理專
					sCreDate      		: undefined,
					pri_type 			: $scope.pri_type
        	};
		  	
			$scope.inputVO2 = {
					NOTE:'',
					pk: '1'
	        };
			
			$scope.curDate = new Date();
			$scope.showBtn = 'none';
			$scope.checked3='0';
			
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
        	
        	$scope.sendRecv("PMS306", "noteText", "com.systex.jbranch.app.server.fps.pms306.PMS306InputVO", $scope.inputVO,
 				function(tota, isError) {
 					if (!isError) {							
 						if(tota[0].body.notelist == 0) {
 							$scope.inputVO2.NOTE="";
 							$scope.showMsg("ehl_01_common_009");								
                 			return;
                 		}
 						$scope.inputVO2.NOTE = tota[0].body.notelist[0].REMARK+"";
 								
 						return;
 					}
     		});
        	 
        	$scope.sendRecv("PMS303", "queryDiscount", "com.systex.jbranch.app.server.fps.pms303.PMS303InputVO", $scope.inputVO,
  				function(tota, isError) {
  					if (!isError) {							
  						if(tota[0].body.discountList == 0) {
  							$scope.inputVO2.PD_RATE="";
  							$scope.showMsg("ehl_01_common_009");								
                  			return;
                  		}
  						$scope.inputVO2.PD_RATE = tota[0].body.discountList[0].PARAM_NAME_EDIT+"";
  						console.log($scope.inputVO2.PD_RATE);
  								
  						return;
  					}
      		});
		};
		$scope.init();
		
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
        
        //config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};  
		
		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.maxDate =  Today;
		};
		
		$scope.btnport = function(){
			if($scope.inputVO.sCreDate==undefined){
				$scope.showMsg("資料統計日期為必填欄位");	 
				return;
			}
			$scope.sendRecv("PMS303", "querydetail", "com.systex.jbranch.app.server.fps.pms303.PMS303InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if( tota[0].body.resultList.length == 0 ) {
							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						return;
					}
			});
		}
		
		$scope.inquireInit = function(){
			$scope.paramList = [];
			$scope.paramList2 = [];
			$scope.originalList = [];
		}
		$scope.inquireInit();
		
		$scope.numGroups = function(input){      
			var i = 0;    
            for(var key in input) i++;      
			return i;
        }
         
       $scope.getSum = function(group, key){
	        var sum = 0;
	        for (var i = 0; i < group.length; i++){
	            {
	        	 if(group[i][key] != null)
	        	 sum += group[i][key];
	            }
	         }  
	        return sum;
	   }
       
       $scope.getRegionTotal = function(region, key){
    	   
	       	if($scope.inputVO.region_center_id == ""){
	       		switch(region){
	       		case "個金分行業務一處":
	       			return $scope.regionCenterList[0][key];
	       		case "個金分行業務二處":
	       			return $scope.regionCenterList[1][key];
	       		case "個金分行業務三處":
	       			return $scope.regionCenterList[2][key];
	       		}
	       	}else{  return $scope.regionCenterList[0][key];}
      }
		
		$scope.getBranchTotal = function(region, key){
			
			if($scope.inputVO.branch_area_id != ""){
				return $scope.branchAreaList[0][key];
			}else{
				for(var i = 0; i < $scope.branchAreaList.length ; i++){
					if(region[0]['BRANCH_AREA_ID'] == $scope.branchAreaList[i]['BRANCH_AREA_ID']){
						return $scope.branchAreaList[i][key];
					}
				}
			}
		}
       
       
   		$scope.as = function(str){			
   			var s=JSON.stringify(str)+"";
   			return s.substring(1,5)+'/'+s.substring(5,7)+'/'+s.substring(7,9);	   			
   		}
           
		$scope.query = function(){

			if($scope.inputVO.sCreDate==undefined){
				
				$scope.showMsg("日期為必填欄位");	 
				return;
			}
			
			$scope.sendRecv("PMS303", "inquire", "com.systex.jbranch.app.server.fps.pms303.PMS303InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								
								$scope.originalList = [];
								$scope.paramList2=[];
								$scope.paramList = [];
								$scope.csvList=[];
								$scope.totalList = [];
								$scope.outputVO = tota[0].body;
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
								$scope.originalList = angular.copy(tota[0].body.resultList);
								$scope.paramList2 = tota[0].body.resultList;
								$scope.checked3='1';
								$scope.rate=tota[0].body.rate;

								for(var i=0;i<$scope.paramList2.length;++i){
									$scope.paramList2[i].DATA_DATE=$scope.paramList2[i].DATA_DATE.substring(0,4)+'/'+$scope.paramList2[i].DATA_DATE.substring(4,6)+'/'+$scope.paramList2[i].DATA_DATE.substring(6,$scope.paramList2[i].DATA_DATE.length);
								}									
							
					
								$scope.regionCenterList = tota[0].body.regionCenterList; //處
								$scope.branchAreaList = tota[0].body.branchAreaList;	 //區
								$scope.totalList = tota[0].body.totalList;
								$scope.csvList=tota[0].body.csvList;
								$scope.outputVO = tota[0].body;
								$scope.showBtn = 'block';
								return;
						}else{
								$scope.showBtn = 'none';
						}						
			});
		};
		
		function percentage(s)	
		{
		      s+='';
		      var len=s.length-2;	
		      var count=0,c=1;	
			  while(count++<len)	
			  c*=10;	
			  return c;
		}	
		
		$scope.save = function () {
			$scope.sendRecv("PMS303", "save", "com.systex.jbranch.app.server.fps.pms303.PMS303InputVO", {'List':$scope.paramList, 'List2':$scope.originalList},
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
    		if($scope.inputVO.sCreDate==undefined){
    			$scope.showMsg("日期為必填欄位");	 
				return;
    		}
			$scope.sendRecv("PMS303", "export", "com.systex.jbranch.app.server.fps.pms303.PMS303InputVO", 
				{'list':$scope.csvList, 
				 'rate': $scope.rate, 
				 'totalList': $scope.totalList,
				 'centerList': $scope.regionCenterList,
				 'areaList': $scope.branchAreaList}, function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		};
});
