'use strict';
eSoafApp.controller('PMS366Controller', function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter, $timeout) {
	
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS366Controller";
	
	// 繼承共用的組織連動選單
	$controller('PMSRegionController', {$scope: $scope});
	
	// filter
	getParameter.XML(["PMS.DEP_TYPE"], function(totas) {
		if (totas) {
			$scope.mappingSet['PMS.DEP_TYPE'] = totas.data[totas.key.indexOf('PMS.DEP_TYPE')];
		}
	});
    //
	
	$scope.mappingSet['AO_TYPE'] = [];
	$scope.mappingSet['AO_TYPE'].push({LABEL:'(計績)', DATA:'1'},{LABEL:'(兼職)', DATA:'2'},{LABEL:'(維護)', DATA:'3'},{LABEL:'(UHRM計績)', DATA:'5'},{LABEL:'(UHRM維護)', DATA:'6'});
	
	var NowDate = new Date();
    var yr = NowDate.getFullYear();
    var mm = NowDate.getMonth() + 2;

    var strmm = '';
    $scope.mappingSet['timeE'] = [];
    mm = mm ;
    for(var i = 0; i < 12; i++){
    	mm = mm - 1;
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
    		DATA : yr + '' + strmm
    	});        
    }
        
	/*** 可視範圍  JACKY共用版  START ***/
    // 選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChange = function(){
    	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;    //目前2月1日無資料  須設定為   1970220   否則預設為   +"01"
    	
    	//可視範圍  觸發 
    	if($scope.inputVO.sCreDate != ''){
    		$scope.RegionController_getORG($scope.inputVO);
    	}
    };
	/*** 可視範圍  JACKY共用版  END***/
	
	$scope.init = function(){
		$scope.inputVO = {					
			sCreDate        : '',	// 資料月份		
			region_center_id: '',   // 業務處
			branch_area_id  : '' ,  // 營運區
			branch_nbr      : '',   // 分行
			ao_code         : '',   // 理專
			memLoginFlag    : String(sysInfoService.getMemLoginFlag())
    	};			

		$scope.paramList = [];
		$scope.outputVO = [];
		$scope.totalList =[];
	};
	
	$scope.init();
	
	$scope.inquireInit = function(){
		$scope.paramList = [];
		$scope.outputVO={totalList:[]};
	}
	
	$scope.inquireInit();
	
	$scope.query = function() {
		if ($scope.inputVO.sCreDate == null || 
			$scope.inputVO.sCreDate == undefined || 
			$scope.inputVO.sCreDate == '') {
			$scope.showErrorMsg('欄位檢核錯誤:請選擇「資料月份」');
    		return;
		}

		$scope.sendRecv("PMS366", "queryData", "com.systex.jbranch.app.server.fps.pms366.PMS366InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.param = [];
					$scope.paramList = [];
					$scope.showMsg("ehl_01_common_009");
					
        			return;
        		}
				
				$scope.paramList = tota[0].body.resultList;
				$scope.totalList = tota[0].body.totalList;
				$scope.csvList = tota[0].body.csvList;
				$scope.outputVO = tota[0].body;
			};
		});
	};
	
	$scope.openDialog = function (row, depType) {
		var yyyymm = row.YYYYMM;
		var aoCode = row.AO_CODE;
		var depType = row.DEP_TYPE;
		
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS366/PMS366_DETAIL.html',
			className: 'PMS366',
			showClose: false,
            controller: ['$scope', function($scope) {
            	$scope.yyyymm = yyyymm;
            	$scope.aoCode = aoCode;
            	$scope.depType = depType;
            }]
		});
	};
	
	$scope.exportRPT = function(){
		$scope.inputVO.exportList = $scope.paramList;
		
		$scope.sendRecv("PMS366", "export", "com.systex.jbranch.app.server.fps.pms366.PMS366InputVO", $scope.inputVO, function(tota, isError) {						
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
        	}
		});
	};
});
