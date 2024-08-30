/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS346Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter) {
	$scope.controllerName = "PMS346Controller";	
	
	$controller('BaseController', {$scope: $scope});
	$controller('PMSRegionController', {$scope: $scope});
	
	$scope.init = function() {
		var Today = new Date("2016/06/01");
		
		$scope.inputVO = {
				aoFlag    : 'Y',
				psFlag    : 'N',
				sTime     : '',
				
				sCreDate  : undefined,
				eCreDate  : undefined,
				sCreDate2 : undefined,
				eCreDate2 : undefined,
				num       : '',
				id        : '',
				type      : '',
				clas      : '',
				ao_code   : '',
				branch    : '',
				region    : '',
				op        : '', 
				
				goQuery   : false
		};
		
		$scope.startMaxDate = $scope.maxDate;
        $scope.endMinDate = $scope.minDate;
        $scope.paramList2 = [];
        $scope.paramList = [];
        
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
	
	$scope.inquireInit = function(){
		$scope.initLimit();
		$scope.paramList = [];
		$scope.originalList = [];
	}
	
	$scope.inquireInit();	
	
	var vo = {'param_type': 'PMS.NOTE_TYPE', 'desc': false};
	if(!projInfoService.mappingSet['PMS.NOTE_TYPE']) {
    	$scope.requestComboBox(vo, function(totas) {
    		if (totas[totas.length - 1].body.result === 'success') {
    			projInfoService.mappingSet['PMS.NOTE_TYPE'] = totas[0].body.result;
    			$scope.mappingSet['PMS.NOTE_TYPE'] = projInfoService.mappingSet['PMS.NOTE_TYPE'];
    		}
    	});
    } else {
    	$scope.mappingSet['PMS.NOTE_TYPE'] = projInfoService.mappingSet['PMS.NOTE_TYPE'];
    }
	
	var vo = {'param_type': 'PMS.NOTE_STATUS', 'desc': false};
	if(!projInfoService.mappingSet['PMS.NOTE_STATUS']) {
    	$scope.requestComboBox(vo, function(totas) {
    		if (totas[totas.length - 1].body.result === 'success') {
    			projInfoService.mappingSet['PMS.NOTE_STATUS'] = totas[0].body.result;
    			projInfoService.mappingSet['PMS.NOTE_STATUS'];
    			$scope.mappingSet['PMS.NOTE_STATUS'] = projInfoService.mappingSet['PMS.NOTE_STATUS'];
    		}
    	});
    } else {
    	$scope.mappingSet['PMS.NOTE_STATUS'] = projInfoService.mappingSet['PMS.NOTE_STATUS'];
    }
	
	$scope.bgn_sDateOptions2 = {
		maxDate: $scope.maxDate2,
		minDate: $scope.minDate2
	};
	
	$scope.bgn_eDateOptions2 = {
		maxDate: $scope.maxDate2,
		minDate: $scope.minDate2
	};
	
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
	
	$scope.open2 = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
	$scope.limitDate = function() {
		$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate;
		$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate;
	};

	$scope.limitDate2 = function() {
		$scope.bgn_sDateOptions2.maxDate = $scope.inputVO.eCreDate2 || $scope.maxDate2;
		$scope.bgn_eDateOptions2.minDate = $scope.inputVO.sCreDate2 || $scope.minDate2;
	};
	
	$scope.model = {};
	
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
    
    $scope.mappingSet['type'] = [];
    $scope.mappingSet['type'].push({LABEL : '完成', DATA :'1'},{LABEL : '未完成', DATA :'0'});
    
    $scope.mappingSet['class']=[];
    $scope.mappingSet['class'].push({LABEL : '完成', DATA :'1'},{LABEL : '未完成', DATA :'0'});
  
    //保單號碼下跳
    $scope.sizelimit1 = function() {
         
    	var c = $scope.inputVO.POLICY_NO;
    	var e = c.match(/.*[A-Za-z0-9]/);
    	
    	if (e != c && c != '') {
    		$scope.showMsg("只能輸入英文或數字");
        	$scope.inputVO.POLICY_NO = '';
    	}
    		
    		
    	if ($scope.inputVO.POLICY_NO.length == 10) {
    		document.getElementById('P1').focus();
    		document.getElementById('P1').select();             
    	}
    } 
    
    $scope.sizelimit2 = function() {
    	var c = $scope.inputVO.num;
    	var e = c.match(/.*[A-Za-z0-9]/);
    	
     	if (e != c && c != '') {
     		$scope.showMsg("只能輸入英文或數字");
         	$scope.inputVO.num='';
        }
    	 
     	if ($scope.inputVO.num.length == 2) {
     		document.getElementById('P2').focus();
     		document.getElementById('P2').select();
        }
     }
  
     $scope.sizelimit3 = function(){
    	 var c = $scope.inputVO.ID_DUP;
    	 var e = c.match(/.*[A-Za-z0-9]/);
    	 
    	 if (e != c && c != '') {
     		$scope.showMsg("只能輸入英文或數字");
         	$scope.inputVO.ID_DUP = '';
    	 }
     }
   
     //選取月份下拉選單 --> 重新設定可視範圍
     $scope.dateChange = function() {
    	 if ($scope.inputVO.sCreDate != '' && $scope.inputVO.sCreDate != undefined){ 
    		 $scope.inputVO.reportDate = $filter('date')($scope.inputVO.sCreDate,'yyyyMMdd');
    		 $scope.RegionController_getORG($scope.inputVO);
    	 } else {   
    		 $scope.inputVO.sCreDate='201701';
       		  
    		 $scope.inputVO.reportDate = $scope.inputVO.sCreDate;
    		 $scope.RegionController_getORG($scope.inputVO);        
    		 $scope.inputVO.sCreDate = undefined;
    	 } 
    	 
    	 $scope.inputVO.region = '';
    	 $scope.inputVO.op = '';
    	 $scope.inputVO.branch = '';
    	 $scope.inputVO.ao_code = '';
    	 //$scope.genRegion();
     }; 
     
     $scope.dateChange();
     /***以下連動區域中心.營運區.分行別***/
     
     //區域中心資訊
     $scope.Region = function(){
    	 $scope.inputVO.region = $scope.inputVO.region_center_id;
     }
     
     //營運區資訊
     $scope.Area = function() {
    	 $scope.inputVO.op = $scope.inputVO.branch_area_id; 
     } 
       
     //分行資訊
     $scope.Branch = function() {
    	 $scope.inputVO.branch = $scope.inputVO.branch_nbr;
     }
     
     $scope.export = function() {
    	 $scope.sendRecv("PMS346", "export", "com.systex.jbranch.app.server.fps.pms346.PMS346InputVO", $scope.inputVO, function(tota, isError) {
    		 if (!isError) {	
    			 return;
    		 }
    	 });
     };
		
     $scope.inquire = function() {
    	 if ($scope.inputVO.sCreDate == undefined) {
    		 $scope.showMsg("照會日期為必填欄位");
    		 return;
    	 }
    	 
    	 $scope.inputVO.goQuery = true;
			
    	 $scope.sendRecv("PMS346", "inquire", "com.systex.jbranch.app.server.fps.pms346.PMS346InputVO", $scope.inputVO, function(tota, isError) {
    		 if (!isError) {
    			 if (tota[0].body.list.length == 0) {
    				 $scope.paramList = [];
    				 $scope.showMsg("ehl_01_common_009");
    				 
    				 $scope.inputVO.goQuery = false;
    				 return;
    			 } else {
    				 $scope.paramList = tota[0].body.list; // 分頁
        			 $scope.totalList = tota[0].body.resultList;
        			 $scope.outputVO = tota[0].body;
    						
        			 $scope.paramList[0].aa=1;
        			 
        			 if ($scope.paramList.length>1) {
        				 /*表格合併處理*/
        				 var old = -1, bool = false, count = 1;
        				 var index = 0,tab = 0 ;
        				 for(index = 0;index < $scope.paramList.length - 1; index++) {	
        					 $scope.paramList[index].aa = 1;	
        					 
        					 /*需要修改區*/     
        					 if(($scope.paramList[index].POLICY_NO_2==$scope.paramList[index+1].POLICY_NO_2) &&
        						($scope.paramList[index].NOTE_TYPE==$scope.paramList[index+1].NOTE_TYPE) &&
        						($scope.paramList[index].REPLY_DATE==$scope.paramList[index+1].REPLY_DATE)){
    						  
        						 if (bool == true) {
        							 $scope.paramList[index].aa=-1; 
        							 count++;	 
        						 } else {
        							 old = index; 
        							 count++; 	 
        							 bool = true;
        						 }	  
    						    	   
        					 } else {
        						 if (old!=-1) {
        							 $scope.paramList[old].aa = count;
        							 $scope.paramList[index].aa=-1; 
        							 old = -1;
        							 count = 0;
        							 bool = false;
        						 } 								           
        					 }	  
        					 
        					 /*分頁處理*/
        					 if (tab == 9)    
        						 tab = 0;   
        					 else		  
        						 tab++;
        				 }
        				 
        				 /*最後一筆處理*/
        				 if (old != -1)
        					 $scope.paramList[old].aa = count;
    	
        				 if (($scope.paramList[index-1].POLICY_NO_2 == $scope.paramList[index].POLICY_NO_2) && ($scope.paramList[index-1].NOTE_TYPE == $scope.paramList[index].NOTE_TYPE) && ($scope.paramList[index-1].REPLY_DATE == $scope.paramList[index].REPLY_DATE))
        					 $scope.paramList[index].aa = -1;    
        				 else
        					 $scope.paramList[index].aa = 1
        			 }     
    						
        			 angular.forEach($scope.paramList, function(row,index,objs){
        				 if( row.NOTE_URL.substr(0,1) == '無' || row.NOTE_URL == null ){
        					 row.FF = '1';
        				 }
        			 });
    					   
        			 $scope.inputVO.goQuery = false;
    			 }
    		 }
    	 });
     };
	

	$scope.chnum = function (ind) {
	
		$scope.mappingSet[ind.leng] = 0;
	
		$scope.mappingSet[ind.leng] = ind.leng;
	
	};
	
	//加密URL並開啟新畫面下載
	$scope.doc = function (row) {
		if (row.NOTE_URL == null || row.NOTE_URL.substr(0,1) == '無') {
			$scope.showMsg("無照會單附件");
			return;
		}
		
		if (row.CASE_NO == null || row.CASE_NO == undefined) {
			$scope.showMsg("無案件編號");
			return;
		}
		
		$scope.sendRecv("PMS346", "encoding", "com.systex.jbranch.app.server.fps.pms346.PMS346InputVO", {'NOTE_URL': row.NOTE_URL,'CASE_NO':row.CASE_NO}, function(totas, isError) {
			if(!isError){
				if (totas[0].body.encodedUrl.length == 0) {
					$scope.showMsg("ehl_01_common_009");
            		return;
            	}
				
				$scope.encodedUrl = totas[0].body.encodedUrl;
				window.open($scope.encodedUrl);
			}
		});
	}
});
