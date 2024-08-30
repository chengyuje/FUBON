/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS304Controller',
	function($scope, $rootScope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS304Controller";
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
        
        
        $scope.mappingSet['seture']=[];
        $scope.mappingSet['seture'].push({
			DATA : '2',
			LABEL : '理專統計'
		},{
			DATA : '1',
			LABEL : '分行統計'
		});
        
        
        
        // date picker
		$scope.bgn_sDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.bgn_eDateOptions = {
				maxDate: $scope.maxDate,
				minDate: $scope.minDate
			};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate;
		};
		// date picker end
		
		/***TEST ORG COMBOBOX START***/
        var org = [];

        
       
      //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){	
        	if($scope.inputVO.sCreDate!='') { 
        		$scope.inputVO.reportDate = $filter('date')($scope.inputVO.sCreDate,'yyyyMMdd');
        		$scope.RegionController_getORG($scope.inputVO);
        		$scope.inputVO.sTime = $scope.inputVO.sCreDate;
        	}
        };  
       
        
        /***ORG COMBOBOX END***/
		
		
		
		$scope.init = function(){
			
			$scope.sendRecv("PMS304", "queryDiscount", "com.systex.jbranch.app.server.fps.pms304.PMS304InputVO", $scope.inputVO,
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
			
			$scope.inputVO = {		
					
					aoFlag           :'Y',
					psFlag           :'N',
					sTime            : '',
					
					
					region_center_id  :'',   //區域中心
					branch_area_id  :'',	//營運區
					branch_nbr:'',			//分行
					ao_code  :'',			//理專
					sCreDate      :'',					
					
                    check:true //true為分行統計
        	};
			
			
		
			$scope.checked='1';
			$scope.checked3='0';
			$scope.curDate = new Date();
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
			$scope.sumFlag = false;
			
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
		
		$scope.Reports=function(){
			
			if($scope.checked=='1'){
				$scope.inputVO.check=true;
			}else{
				$scope.inputVO.check=false;
			}
			$scope.paramList =[];
	
			$scope.regionCenterList = []; //處
			$scope.branchAreaList = [];	  //區
			$scope.branchNbrList = [];	  //分行
			$scope.totalList = [];        //全行
			
		}     
		
        $scope.altInputFormats = ['M!/d!/yyyy'];
    	//時間
        $scope.model = {};
    	$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
	
		$scope.inquireInit = function(){
			$scope.paramList = [];
			$scope.originalList = [];
		}
		$scope.inquireInit();
		
	/***new***/	
		$scope.numGroups = function(input){
		  var i=0;    
            for(var key in input) i++;      
			return i;
		}
		
		//計算合併列數
		$scope.numGroupsA = function(input){
			if(input == null)
				return;
			var sum = 0;
			var ans = $filter('groupBy')(input, 'BRANCH_AREA_NAME');
			sum += Object.keys(ans).length * 2
			angular.forEach(Object.values(ans), function(row) {
    			var child = $filter('groupBy')(row, 'BRANCH_NBR');
    			sum += Object.keys(child).length;
			});
            return sum;
        }
		
		$scope.numGroupsB = function(input){
			if(input == null)
				return;
			var ans = $filter('groupBy')(input, 'BRANCH_NBR');
            return Object.keys(ans).length;
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
        	}else{
        		return $scope.regionCenterList[0][key];
        	}
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
        
        $scope.getBranchNbrTotal = function(region, key){
			if($scope.inputVO.branch_nbr != ""){
				return $scope.branchNbrList[0][key];
			}else{
				for(var i = 0; i < $scope.branchNbrList.length ; i++){
					if(region[0]['BRANCH_NBR'] == $scope.branchNbrList[i]['BRANCH_NBR']){
						return $scope.branchNbrList[i][key];
					}
				}
			}
		}
        
		$scope.as = function(str){
			var s=JSON.stringify(str)+"";
			return s.substring(1,5)+'/'+s.substring(5,7)+'/'+s.substring(7,9);		
		}
		
		//查詢
		$scope.query = function(){
			//檢核
			if($scope.inputVO.sCreDate=='') {
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:日期必要輸入欄位');
        		return;
        	}		
			if($scope.parameterTypeEditForm.$invalid) {
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:報表類型必要輸入欄位');
        		return;
        	}
		  
			
			$scope.sendRecv("PMS304", "inquire", "com.systex.jbranch.app.server.fps.pms304.PMS304InputVO", $scope.inputVO,
					
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList =[];
								$scope.checked3='0';
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$(".sticky-wrap").scrollLeft(0).scrollTop(0); // reset scroll bar position
							$scope.paramList = tota[0].body.resultList;
							$scope.csvList=tota[0].body.csvList;
							$scope.outputVO = tota[0].body
							$scope.checked3='1';
							$scope.regionCenterList = tota[0].body.regionCenterList; //處
							$scope.branchAreaList = tota[0].body.branchAreaList;	 //區
							$scope.totalList = tota[0].body.totalList;               //全行
						    $scope.branchNbrList = tota[0].body.branchNbrList; //分行
						    // clear out range columns: 如果在這裡執行時間點不對就要用$q把查詢結果包起來.then(()=>{...})再執行以下程式碼, 形成異步處理.
//						    $timeout(()=>{$('.sticky-col td[align=right]').css('display','none');});
//							$timeout(()=>{$('.sticky-col td[align=right]').css('display','none');},1000);
						    $timeout(function(){$('.sticky-col td[align=right]').css('display','none');});
							$timeout(function(){$('.sticky-col td[align=right]').css('display','none');},1000);
							return;
						}					
			});
			
			
		};
		
		//匯出exc
//    	$scope.export = function() {
//			$scope.sendRecv("PMS304", "export","com.systex.jbranch.app.server.fps.pms304.PMS304OutputVO",
//					{'list':$scope.csvList}, function(tota, isError) {
//				if (!isError) {
//					return;
//				}
//			});
//		};
//		
		$scope.export = function() {
    		if($scope.inputVO.sCreDate==undefined){
    			$scope.showMsg("日期為必填欄位");	 
				return;
    		}
    	
    			$scope.sendRecv("PMS304", "export", "com.systex.jbranch.app.server.fps.pms304.PMS304InputVO", 
    					{'list'      : $scope.csvList, 
    					 'totalList' : $scope.totalList,
    					 'centerList': $scope.regionCenterList,
    					 'areaList'  : $scope.branchAreaList,
    					 'branchList': $scope.branchNbrList ,
    					 'check'     : $scope.inputVO.check    }, function(tota, isError) {
    							if (!isError) {
    								return;
    							}
    				});
			
		};
		
});
