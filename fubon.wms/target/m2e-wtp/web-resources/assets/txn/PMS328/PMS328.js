
'use strict';
eSoafApp.controller('PMS328Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService,$filter) {
	
	$controller('BaseController', {$scope: $scope});
	
	$scope.controllerName = "PMS328Controller";
	
	// 繼承共用的組織連動選單
	$controller('PMSRegionController', {$scope: $scope});
	
	$scope.mappingSet['AO_TYPE'] = [];
	$scope.mappingSet['AO_TYPE'].push({LABEL:'(計績)', DATA:'1'},{LABEL:'(兼職)', DATA:'2'},{LABEL:'(維護)', DATA:'3'},{LABEL:'(UHRM計績)', DATA:'5'},{LABEL:'(UHRM維護)', DATA:'6'});
		
	$scope.init = function() {
		$scope.inputVO = {
    			eTime            : 0,
    			sTime            : 0,
    			eTimes           : '',    		//結束月
    			sTimes           : '',    		//起始月
    			aocode           : '',
				branch           : '',
				region           : '',
				op               : '',
				aojob            : '',
				region_center_id : '',  	 	//區域中心
				branch_area_id   : '',			//營運區
				branch_nbr       : '',			//分行
				ao_code          : '',			//理專
				funcPage         : 'PMS328', 
				importSDate      : '',
				importEDate      : ''
    	};
		
		$scope.paramList = [];
		$scope.paramList2 = [];
		$scope.totalList = [];
		$scope.curDate = new Date();
	};
    $scope.init();
    
    /*** 可示範圍  JACKY共用版  START ***/
    //選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChange = function(){
    	angular.forEach($scope.mappingSet['timeS'], function(row, index, objs){
			if(row.DATA == $scope.inputVO.sTime)
				$scope.inputVO.sTimes = row.LABEL.substring(0, 4) + row.LABEL.substring(5, 7);
		});
    
    	angular.forEach($scope.mappingSet['timeE'], function(row, index, objs){
			if(row.DATA == $scope.inputVO.eTime)
				$scope.inputVO.eTimes = row.LABEL.substring(0, 4) + row.LABEL.substring(5, 7);
		});
    	
    	//取得轉換年月
    	if($scope.inputVO.sTimes != '請選擇')
    		$scope.inputVO.reportDate = $scope.inputVO.sTimes;    //目前2月1日無資料  須設定為   1970220   否則預設為   +"01"
    	if($scope.inputVO.eTime != ''){
    		if($scope.inputVO.eTimes != '請選擇')
    			$scope.inputVO.reportDate = $scope.inputVO.eTimes;   
    	}
    	
    	//可是範圍  觸發 
    	if($scope.inputVO.sTime != '' || $scope.inputVO.eTime != ''){
    		$scope.RegionController_getORG($scope.inputVO);
    	}
    };
    $scope.dateChange();
    
	$scope.curDate = new Date();
	
	var rp = "RC";
	if(sysInfoService.getRoleName().substring(0, 2) == 'FC')
		rp = "AO";
	if(sysInfoService.getRoleID() == 'A161')
		rp = "BR";
	/*** 可示範圍  JACKY共用版  END***/
    
	/***===年月===***/
   	$scope.yMonth = function() {
		var NowDate = new Date();
		var yr = NowDate.getFullYear();
		var mm = NowDate.getMonth() + 2;
		var strmm = '';
		var xm = '';
		$scope.mappingSet['time'] = [];
		for (var i = 0; i < 37; i++) {
			mm = mm - 1;
			if (mm == 0) {
				mm = 12;
				yr = yr - 1;
			}
			if (mm < 10)
				strmm = '0' + mm;
			else
				strmm = mm;
			$scope.mappingSet['time'].push({
				LABEL : yr + '/' + strmm,
				DATA : i + 1
			});
		} 
		
		$scope.mappingSet['timeS'] = [];
		$scope.mappingSet['timeS'] = angular.copy($scope.mappingSet['time']);
		
		$scope.mappingSet['timeE'] = [];
		$scope.mappingSet['timeE'] = angular.copy($scope.mappingSet['time']);
		
		$scope.mappingSet['finals'] = [];
		$scope.mappingSet['finals'] = angular.copy($scope.mappingSet['time']);
   	};
   	$scope.yMonth();

 	/***===年月起===***/
   	$scope.chtimeS = function(){
		var NowDate = new Date();
		var yr = NowDate.getFullYear();
		var mm = NowDate.getMonth() + 1;
		var strmm = '';
		var xm = '';
		if($scope.inputVO.sTime!=0){
			$scope.mappingSet['timeE'] = [] ;
			for (var i = 0; i < $scope.inputVO.sTime; i++) {
				$scope.mappingSet['timeE'].push(angular.copy($scope.mappingSet['time'][i]));
			} 
		}

	};
	
	/***===年月訖===***/
   	$scope.chtimeE = function() {
		var NowDate = new Date();
		var yr = NowDate.getFullYear();
		var mm = NowDate.getMonth() + 1;
		var strmm = '';
		var xm = '';
		if($scope.inputVO.eTime != 0) {
			$scope.mappingSet['timeS'] = angular.copy($scope.mappingSet['time']) ;
			for (var i = 0; i < $scope.inputVO.eTime - 1; i++) {
				$scope.mappingSet['timeS'].splice(0, 1);	
			} 
		}
	};
	
	/*** 跨欄  START ***/
	$scope.numGroups = function(input){
		if(input == null)
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
	
	$scope.numGroupsA = function(input){
		if(input == null)
			return;
		
		var branch_all = _.groupBy(input, "BRANCH_NAME");
		var branch_cnt = Object.keys(branch_all).length - 2;
		return branch_cnt ;
	}
	/*** 跨欄  END ***/
	
    $scope.inquire = function() {
    	if($scope.parameterTypeEditForm.region_center_id.$invalid) {
    		$scope.showErrorMsg('欄位檢核錯誤:業務處為必填欄位');
    		return;
    	}
    	
    	angular.forEach($scope.mappingSet['timeS'], function(row, index, objs) {
			if(row.DATA == $scope.inputVO.sTime)
				$scope.inputVO.sTimes = row.LABEL.substring(0, 4) + row.LABEL.substring(5, 7);
		});
    
    	angular.forEach($scope.mappingSet['timeE'], function(row, index, objs) {
			if(row.DATA == $scope.inputVO.eTime)
				$scope.inputVO.eTimes = row.LABEL.substring(0, 4) + row.LABEL.substring(5, 7);
		});
    	
    	//年月檢核
    	if ($scope.inputVO.sTime == '' || $scope.inputVO.eTime == '') {
    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位(資料統計月份)起訖!!');
    		return;
    	}

    	$scope.s_time = $scope.mappingSet['time'][$scope.inputVO.sTime-1].LABEL;
    	$scope.e_time = $scope.mappingSet['time'][$scope.inputVO.eTime-1].LABEL;
    	
    	if ($scope.s_time < '2024/08' && $scope.e_time >= '2024/08') {
    		$scope.showErrorMsg('(2024/08起)因新制財管會員分級，故不可以新舊跨月查詢');
    		return;
    	}
    	
    	if($scope.inputVO.sTimes == '請選擇') {
    		$scope.inputVO.sTimes = '';
    	}
    	
    	if($scope.inputVO.eTimes == '請選擇') {
    		$scope.inputVO.eTimes = '';
    	}
		
		$scope.sendRecv("PMS328", "queryData", "com.systex.jbranch.app.server.fps.pms328.PMS328InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				
				if(tota[0].body.resultList.length == 0) {
					$scope.paramList2 = [];
					$scope.param = [];
					$scope.paramList = [];
					$scope.mappingSet['timeS'] = [];
					$scope.mappingSet['timeS'] = angular.copy($scope.mappingSet['time']);
					
					$scope.mappingSet['timeE'] = [];
					$scope.mappingSet['timeE'] = angular.copy($scope.mappingSet['time']);
					$scope.showMsg("ehl_01_common_009");
					
        			return;
        		}
				
				$scope.paramList2 = tota[0].body.resultList;
				$scope.totalList = tota[0].body.totalList;
				$scope.param = tota[0].body.DATA;
				$scope.csvList = tota[0].body.csvList;
				$scope.outputVO = tota[0].body;	
				$scope.cp = tota[0].body.currentPageIndex + 1;
				$scope.tp = tota[0].body.totalPage;
				
				$scope.totalRecord = tota[0].body.totalRecord;
				$scope.rp = $scope.totalRecord % 20;
			}
		});
		
		return;
	};

	$scope.export = function() {
		$scope.sendRecv("PMS328", "export", "com.systex.jbranch.app.server.fps.pms328.PMS328OutputVO", {'list':$scope.csvList, 's_time':$scope.s_time}, 
		function(tota, isError) {
			if (!isError) {
				return;
			}
		});
	};
	
});
