'use strict';
eSoafApp.controller('PMS350_DETAILController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS350_DETAILController";
	// 繼承共用的組織連動選單
	$controller('PMSRegionController', {$scope: $scope});
	
	//可否匯出
	$scope.EXPORT_YN = ($scope.DataRow.EXPORT_YN === 'Y');
	
	$scope.memLoginFlag = String(sysInfoService.getMemLoginFlag()).toUpperCase();
	
	var rptMastSeq = $scope.DataRow.SEQ;
	$scope.MARQUEE_TXT = $scope.DataRow.MARQUEE_TXT;
	$scope.MARQUEE_FLAG = $scope.DataRow.MARQUEE_FLAG;

	var NowDate = new Date();
    var yr = NowDate.getFullYear();
    var mm = NowDate.getMonth()+2;
    var strmm = '';
    $scope.mappingSet['timeE'] = [];
    for(var i = 0; i < 24; i++){
    	mm = mm-1 ;
    	if(mm == 0){
    		mm = 12;
    		yr = yr - 1;
    	}
    	
    	if(mm < 10)
    		strmm = '0' + mm;
    	else
    		strmm = mm;     
    	
    	$scope.mappingSet['timeE'].push({
    		LABEL: yr + '/' + strmm,
    		DATA: yr + '' + strmm
    	});        
    }
    
    /*** 可視範圍  JACKY共用版  START ***/
    //選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChange = function(){
    	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;    //目前2月1日無資料  須設定為   1970220   否則預設為   +"01"
    	//可視範圍  觸發 
    	if ($scope.inputVO.sCreDate != '' && !$scope.memLoginFlag.startsWith('UHRM')) {
    		$scope.RegionController_getORG($scope.inputVO).then(function(data) {
    			switch (sysInfoService.getPriID()) {
	    			case "002":
	    			case "003":
	    			case "009":
	    			case "010":
	    			case "011":
	    			case "UHRM002":
	    				$scope.inquireInit();
	    	        	$scope.query();
	    				break;
	    		}
	    			
    			$scope.disableEmp_id();	
			});
    	}
    };
    
	$scope.curDate = new Date();
	
	var rp = "RC";
	if(sysInfoService.getRoleName().substring(0, 2) == 'FC')
		rp = "AO";
	if(sysInfoService.getRoleID() == 'A161')
		rp = "BR";
	
	/*** 可示範圍  JACKY共用版  END***/
    
    $scope.getRPTCol = function(){
		$scope.colList = [];
		$scope.sendRecv("PMS350", "queryRPTCol", "com.systex.jbranch.app.server.fps.pms350.PMS350DetailInputVO", {'seq':rptMastSeq}, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.totalList.length == 0) {
					$scope.colList=[];
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				$scope.colList = tota[0].body.totalList;
				$scope.outputVO = tota[0].body;
				return;
			}						
		});
	}
    
    $scope.getAuthority = function(){
		$scope.authorityList = [];
		$scope.sendRecv("PMS350", "queryAuthority", "com.systex.jbranch.app.server.fps.pms350.PMS350DetailInputVO", {'seq':rptMastSeq}, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.totalList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				
				$scope.authorityList = tota[0].body.totalList;
				$scope.outputVO = tota[0].body;
				
				angular.forEach($scope.authorityList, function(row) {
					$scope.inputVO.default5 = false;
					
					if(row.ROLES){
						var role = [];
						var temp = row.ROLES.split("、");
						row.use_roles_temp = [];
						for (var i = 0; i < temp.length; ++i) {
							if (sysInfoService.getPriID() == temp[i]) {
								$scope.inputVO.default5 = true;
							}
						}
					}
				});
			}						
		});
	}
    
	$scope.open = function($event, index) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope['opened'+index] = true;
	};					
	
	$scope.init = function() {
		$scope.hideExport = false;
		if (!$scope.EXPORT_YN) {
			$scope.hideExport = true;
		}
		
		var NowDate = new Date();
        var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth() + 1;
        var strmm = '';
        
        if (mm == 13) {
    		mm = mm - 1;
    	}
        
    	if (mm<10) {
    		strmm = '0' + mm;
    	} else {
    		strmm = mm;
    	}
    	
        var nowYearMon = yr + '' + strmm;
        
		$scope.inputVO = {
			seq: rptMastSeq,
			sCreDate: nowYearMon,										
			region_center_id: '',   //區域中心
			branch_area_id: '',	//營運區
			branch_nbr: '',			//分行
			ao_code: '',			//理專
			emp_id: '',
			searchType: 'aocode'
    	};
		
		$scope.getAuthority();
		$scope.getRPTCol();
		$scope.paramList = [];	
		$scope.csvList = [];
	    $scope.tempcsv = [];	
	    console.log("1-1:"+sysInfoService.getPriID());
	    
	    if (sysInfoService.getPriID() == "002" || 
			sysInfoService.getPriID() == "003" || 
			sysInfoService.getPriID() == "004" || 
			sysInfoService.getPriID() == "JRM" || 
			sysInfoService.getPriID() == "UHRM002") {
	    	$scope.empIdDisabled = true;
	    } else {
	    	$scope.empIdDisabled = false;
	    }
	};
	
	$scope.inquireInit = function() {
		$scope.paramList = [];
		$scope.outputVO = {};
	};
	
	$scope.initQuery = function() {
		var NowDate = new Date();
        var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth() + 1;
        var strmm = '';
        if(mm == 13){
    		mm = mm-1;
    	}
        
    	if (mm<10) {
    		strmm = '0' + mm;
    	} else {
    		strmm = mm;
    	}
    	
        var nowYearMon = yr + '' + strmm;
		$scope.inputVO = {
			seq: rptMastSeq,
			sCreDate: nowYearMon,										
			searchType: 'aocode'
        };
		
		$scope.getAuthority();
		$scope.getRPTCol();
		$scope.paramList = [];	
		$scope.csvList = [];
   
	    $scope.tempcsv = [];
	    $scope.dateChange();
		
	};
			
	$scope.backToMain = function(){		
		$rootScope.menuItemInfo.url = "assets/txn/PMS350/PMS350.html";
	};

	$scope.radioChange = function(){
		$scope.paramList = [];			
		$scope.dateChange();
			
	}
		
	$scope.query = function(){
		//此報表是否只查所屬的資料
		if ($scope.inputVO.default5) {
			$scope.inputVO.isSelf = "1";
		} else {
			$scope.inputVO.isSelf = "0";
		}

		$scope.sendRecv("PMS350", "queryRPTData", "com.systex.jbranch.app.server.fps.pms350.PMS350DetailInputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.totalList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				
				$scope.paramList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;
				$scope.csvList = tota[0].body.csvList;
			}						
		});			
	};
	

	/**「，」分割資料**/ 
	$scope.getListByComma = function(value){
		if (value != "" && value != undefined) {
			return value.split(';');
		}
	}
	
	$scope.export = function() {
		if (!$scope.EXPORT_YN) {
			$scope.showErrorMsg('此報表含有個資，無法提供匯出功能。');
			return;
		}
		
		$scope.csvList2 = [];
		var len = $scope.csvList.length;
		
		for(var i = 0; i < len; ++i) {
			$scope.csvList[i].aa = 'COL';	  
		}
		
		angular.forEach($scope.csvList, function(row, index, objs) {
			for (var i = 0; i < len; ++i) {
				if ($scope.csvList[i].ROW_SEQ == row.ROW_SEQ){
					$scope.csvList[i].aa = $scope.csvList[i].aa.split(',');
				}	
			}	
		});		
		
		$scope.sendRecv("PMS350", "export","com.systex.jbranch.app.server.fps.pms350.PMS350OutputVO",{'reportId': $scope.DataRow.RPT_NAME + "：" + $scope.DataRow.RPT_EXPLAIN, 'list':$scope.csvList, 'totalList':$scope.colList}, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
			}
			
			if (tota.length > 0) {
				if (tota[0].body.resultList && tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
					return;
				}
			};
		});
	};	

	$scope.getValue = function(rr){					
		$scope.sendRecv("PMS350", "queryDTLREC", "com.systex.jbranch.app.server.fps.pms350.PMS350DetailInputVO", $scope.inputVO, function(tota, isError) {
			$scope.mappingSet[rr.ROW_SEQ] = [];
					
			angular.forEach(tota[0].body.totalList, function(row, index, objs){
						
				if (row.ROW_SEQ == rr.ROW_SEQ ) {	
					$scope.mappingSet[rr.ROW_SEQ].push({DATA: row.CONTENT});	                 					                 					                 					                 			    					    																												
					$scope.tempcsv.push({
						ROW_SEQ:rr.ROW_SEQ,		
						DATA: row.CONTENT
					});
				}
			});													
		});			
	};
	
	$scope.disableEmp_id = function(){					
        //#1868 ps消金 disabled emp_id 欄位 並帶入自身員編
		//#2011 jrm  disabled emp_id 欄位 並帶入自身員編
		
		if (sysInfoService.getPriID() == "002" || 
			sysInfoService.getPriID() == "003" || 
			sysInfoService.getPriID() == "004" || 
			sysInfoService.getPriID() == "JRM" || 
			sysInfoService.getPriID() == "UHRM002") {
			$scope.inputVO.emp_id = sysInfoService.getUserID();
	    } 
	};
	
	$scope.init();
	$scope.dateChange();
});
