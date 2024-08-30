'use strict';
eSoafApp.controller('PMS355Controller', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS355Controller";;
	
	// 繼承共用的組織連動選單
	$controller('RegionController', {$scope: $scope});
	
	var NowDate = new Date();
	var yr = NowDate.getFullYear();
    var mm = NowDate.getMonth() + 1;
    var strmm = '';
    $scope.mappingSet['timeE'] = [];
    for(var i = 0; i < 13; i++){
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
    	
    	mm = mm - 1;
    }

    //選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChange = function() {
	   if($scope.inputVO.sCreDate != ''){
	   		$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
	   } else {   
		   $scope.inputVO.sCreDate = '201701';
		   $scope.inputVO.reportDate = $scope.inputVO.sCreDate;    
		   $scope.inputVO.sCreDate = '';
	   } 
    	$scope.inputVO.dataMon = $scope.inputVO.sCreDate; 
    }; 
    
	// 初始化
	$scope.init = function() {
		$scope.priID = String(sysInfoService.getPriID());

		$scope.inputVO = {
			sCreDate			: '',
			region_center_id	: '',   // 區域中心
			branch_area_id		: '', 	// 營運區
			branch_nbr			: '',	// 分行	
			exportList          : [], 
			memLoginFlag        : String(sysInfoService.getMemLoginFlag())
    	};
		
		$scope.resultList = [];
		$scope.outputVO = [];
		
		$scope.dateChange();
		
		$scope.test = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "aoCode", "AO_LIST", "emp_id", "EMP_LIST"];
		$scope.RegionController_setName($scope.test);
	};
	
	$scope.init();
	
	//資料查詢
	$scope.query = function() {	
		if ($scope.inputVO.sCreDate == '') {
    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位(資料月份)');
    		return;
    	}
		
		$scope.resultList = [];
		$scope.outputVO = [];
		
		$scope.sendRecv("PMS355", "query", "com.systex.jbranch.app.server.fps.pms355.PMS355InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				
				$scope.resultList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;		
			}				
		});
	};
	
	$scope.exportRPT = function(){
		$scope.inputVO.exportList = $scope.resultList;
		
		$scope.sendRecv("PMS355", "export", "com.systex.jbranch.app.server.fps.pms355.PMS355InputVO", $scope.inputVO, function(tota, isError) {						
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
        	}
		});
	};
	
	$scope.updINSCust = function(yearmon){
		$scope.inputVO.sTime = $scope.inputVO.sCreDate;
		
		/*判斷資料月份是否為空*/
		if('' == $scope.inputVO.sTime || null == $scope.inputVO.sTime){
			$scope.showErrorMsg("尚未選擇資料月份");
			return;
		}
		
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS355/PMS355_INS.html',
			className: 'PMS355_INS',
			showClose: false,
			controller: ['$scope', function($scope) {
				$scope.yearMon = yearmon;
				$scope.uploadName = 'importCustData';
            }]
		});
		
		dialog.closePromise.then(function(data) {
    		if(data.value == 'cancel'){
			}
    	});
		
	}
});