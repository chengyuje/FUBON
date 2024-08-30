'use strict';
eSoafApp.controller('PMS364Controller', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
	
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS364Controller";
	
	// 繼承共用的組織連動選單
	$controller('PMSRegionController', {$scope: $scope});
	
	var NowDate = new Date();
    var yr = NowDate.getFullYear();
    var mm = NowDate.getMonth() + 2;

    var strmm = '';
    $scope.mappingSet['timeE'] = [];
    for(var i = 0; i < 3; i++){
    	mm = mm - 1;
    	if(mm == 0){
    		mm = 12;
    		yr = yr - 1;
    	}
    	
    	if(mm<10)
    		strmm = '0' + mm;
    	else
    		strmm = mm;        	
    	
    	$scope.mappingSet['timeE'].push({
    		LABEL: yr + '/' + strmm,
    		DATA : yr + '' + strmm
    	});        
    }
        
	/*** 可視範圍  JACKY共用版  START ***/
    //選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChange = function(){
    
    	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;    //目前2月1日無資料  須設定為   1970220   否則預設為   +"01"
    	
    	//可視範圍  觸發 
    	if($scope.inputVO.sCreDate!=''){
    		$scope.RegionController_getORG($scope.inputVO);
    	}
    };
    	
	var rp = "RC";
	if(sysInfoService.getRoleName().substring(0, 2) == 'FC')
		rp = "AO";
	if(sysInfoService.getRoleID() == 'A161')
		rp = "BR";
	
	/*** 可視範圍  JACKY共用版  END***/
	
	$scope.init = function(){
		$scope.inputVO = {					
				sCreDate: '',			// 資料月份		
				region_center_id: '',   // 業務處
				branch_area_id: '' ,    // 營運區
				branch_nbr: '',         // 分行
				ao_code: '',            // 理專
				NOT_EXIST_UHRM: 'N',    // 排除條件
				NOT_EXIST_BS: 'N',      // 排除條件
    	};			

		$scope.outputVO={totalList:[]};
	};
	
	$scope.init();
	
	$scope.inquireInit = function(){
		$scope.paramList = [];
		$scope.outputVO={totalList:[]};
	}
	
	$scope.inquireInit();
	
	/*** 跨欄  START ***/
	$scope.numGroups = function(input){
		var i = 0;    
		for(var key in input) i++;      
		return i;
	}
	
	$scope.filterByKey = function(input, type, Key) {
		return($filter('filter')(input, Key, true));
	}
	
	$scope.numGroups = function(input, type) {
		if(input == null)
			return;
		
		var ans = $filter('groupBy')(input, type);
		
		return Object.keys(ans).length;
	}
	/*** 跨欄  END ***/
	
	$scope.query = function() {
		if ($scope.parameterTypeEditForm.$invalid) {
    		$scope.showErrorMsg('欄位檢核錯誤:請選擇「資料月份」');
    		return;
    	}

		$scope.sendRecv("PMS364", "queryData", "com.systex.jbranch.app.server.fps.pms364.PMS364InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if (tota[0].body.resultList.length == 0) {
					$scope.paramList = [];
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				
				$scope.paramList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;	
			};
		});
	};
	
	$scope.openDialog = function (row, type) {
		var yyyymm = row.YYYYMM;
		var branchNbr = row.BRANCH_NBR;
		var custAO = row.CUST_AO;
		var type = type;
		
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS364/PMS364_DETAIL.html',
			className: 'PMS364',
			showClose: false,
            controller: ['$scope', function($scope) {
            	$scope.yyyymm = yyyymm;
            	$scope.branchNbr = branchNbr;
            	$scope.custAO = custAO;
            	$scope.type = type;
            }]
		});
	};
	
	$scope.exportRPT = function(){
		$scope.inputVO.exportList = $scope.paramList;
		$scope.inputVO.yyyymm = $scope.inputVO.sCreDate;
		
		$scope.sendRecv("PMS364", "export", "com.systex.jbranch.app.server.fps.pms364.PMS364InputVO", $scope.inputVO, function(tota, isError) {						
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
        	}
		});
	};
});
