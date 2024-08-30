
'use strict';
eSoafApp.controller('PMS328DTController', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService,$filter) {
	
	$controller('BaseController', {$scope: $scope});
	
	$scope.controllerName = "PMS328DTController";
	
	// 繼承共用的組織連動選單
	$controller('PMSRegionController', {$scope: $scope});
	
	// 繼承
	$controller('PMS328Controller', {$scope: $scope});
	
	$scope.initPMS328DT = function(){
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
				funcPage         : 'PMS328DT'
    	};
		
		$scope.paramList = [];
		$scope.paramList2 = [];
		$scope.totalList = [];
		$scope.curDate = new Date();
	};
	
    $scope.initPMS328DT();
});
