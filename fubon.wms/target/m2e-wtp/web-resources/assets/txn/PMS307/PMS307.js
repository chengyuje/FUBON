/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS307Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS307Controller";
		$controller('PMSRegionController', {$scope: $scope});
		/******===初始化===******/
		$scope.init = function(){
			var Noed=undefined    ;//new Date("2016/07/25");
			$scope.inputVO = {					
					region_center_id  :'',   //區域中心
					branch_area_id  :'',	//營運區
					branch_nbr:'',			//分行
					ao_code  :'',			//理專
					sCreDate      :undefined,
					seture1 :'1'
        	};
			$scope.curDate = new Date();
			$scope.checked='1';
			$scope.mappingSet['seture1']=[];
	        $scope.mappingSet['seture1'].push(
	        		{DATA : '1',LABEL : '保險核實報表'},	
	        		{DATA : '2',LABEL : '保險月報'},
	        		{DATA : '3',LABEL : '保險追蹤報表'}
	        		);
		};
		$scope.init();
		
		
		/******===年月字串start===******/
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
        /******===年月字串end===******/
        
        
        
        /******===報表類型下拉選單start===******/
       
       $scope.Reports=function(){
    	 
    	 $scope.inputVO.seture1=$scope.checked; 
    	 
     }  
        
        
        /******===報表類型下拉選單end===******/
        
        
        
 
    	 /***===以下連動區域中心.營運區.分行別end===***/
	        
      //選取日期下拉選單 --> 重新設定可視範圍
       $scope.dateChange = function(){
    	   
        	 if($scope.inputVO.sCreDate!=undefined){
        		 $scope.inputVO.reportDate = $filter('date')($scope.inputVO.sCreDate,'yyyyMMdd');
       	       	 $scope.RegionController_getORG($scope.inputVO);
       	       	 
        	 }
        };
        
      //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateMonthChange = function(){
        	
       	 if($scope.inputVO.sCreDate!=''){
       		 $scope.inputVO.reportDate = $scope.inputVO.sCreDate;
      	       	 $scope.RegionController_getORG($scope.inputVO);
      	       	 
       	 }
       };
       
      //週月報轉換
        $scope.ChangeReport = function(){
        	
        	$scope.inputVO.sCreDate = '';
          	$scope.inputVO.reportType = '';
          	
        }
        	
       	 
        
        
        
	        
    	 /***===時間選單控制===***/    
    	$scope.altInputFormats = ['M!/d!/yyyy'];
    	//時間
        $scope.model = {};
    	$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		 /***===時間選單控制end===***/   
	
	
		
		 /***===總和function===***/
		$scope.numGroups = function(input){
			if(input==null)
				return;
			
            return Object.keys(input).length;
           }
         
           $scope.getSum = function(group, key) {
            var sum = 0;
            for (var i = 0; i < group.length; i++){
             sum += group[i][key];
            }  
            return sum;
           }
         /***===總和function_end===***/
		
		
           
         /***==查詢==***/
		$scope.query = function(){
			if($scope.inputVO.sCreDate == undefined || $scope.inputVO.sCreDate == '') {
				$scope.showMsg("資料統計日期/月份為必填欄位");	 
				return; 
			}
			
			if($scope.inputVO.seture1 == '') {
				$scope.showMsg("報表類型為必填欄位");	 
				return; 
			}
			
			$scope.sendRecv("PMS307", "inquire", "com.systex.jbranch.app.server.fps.pms307.PMS307InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList=[];
								$scope.csvList=[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
//							alert(JSON.stringify(tota[0].body));
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO=tota[0].body;
							$scope.csvList=tota[0].body.csvList;
	
							return;
						}					
			});
		};
		 /***==查詢end==***/
		
		
	//匯出exceil
    	$scope.export = function() {
			$scope.sendRecv("PMS307", "export",
					"com.systex.jbranch.app.server.fps.pms307.PMS307OutputVO",
					{'list':$scope.csvList,'checked':$scope.checked}, function(tota, isError) {
						if (!isError) {
							return;
						}
					});
		};
});
