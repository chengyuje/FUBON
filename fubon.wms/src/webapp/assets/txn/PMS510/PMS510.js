'use strict';
eSoafApp.controller('PMS510Controller', function ($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
        
	$controller('BaseController', {$scope: $scope});
	
	$scope.controllerName = "PMS510Controller";

	//組織連動繼承
	$controller('RegionController', {$scope: $scope});

	var NowDate = new Date();
	var yr = NowDate.getFullYear();
    var mm = NowDate.getMonth() + 1;
    var strmm = '';
    $scope.mappingSet['timeE'] = [];
    for (var i = 0; i < 13; i++) {
    	if(mm == 0){
    		mm = 12;
    		yr = yr - 1;
    	}
    	
    	if(mm < 10)
    		strmm = '0' + mm;
    	else
    		strmm = mm; 
    	
    	mm = mm - 1;
    	
    	       		
    	$scope.mappingSet['timeE'].push({
    		LABEL: yr + '/' + strmm,
    		DATA: yr + '' + strmm
    	});        
    }

    //選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChange = function(){
	   if($scope.inputVO.sCreDate != ''){
	   		$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
	   } else {   
		   $scope.inputVO.sCreDate = '201701';
		   $scope.inputVO.reportDate = $scope.inputVO.sCreDate;    
		   $scope.inputVO.sCreDate = '';
	   } 
    	$scope.inputVO.dataMonth = $scope.inputVO.sCreDate; 
    }; 
	
    $scope.init = function() {
		$scope.priID = String(sysInfoService.getPriID());
		
		$scope.inputVO = {					
			sCreDate        : undefined,
			reportHierarchy : '',
			deptID          : ''
    	};
		
		$scope.titleBrh = [];
		$scope.titleColName = [];
		$scope.titleCol = [];
		$scope.resultList = [];
		$scope.outputVO = [];
		
		$scope.deptList = [];
		
		$scope.REPORT_HIERARCHY = [];
		switch ($scope.priID) {
			case "061":
			case "046":
			case "045":
				$scope.REPORT_HIERARCHY.push({LABEL: '全行', DATA: 'ALL'});
			case "033":
			case "013":
				$scope.REPORT_HIERARCHY.push({LABEL: '業務處', DATA: 'REGION'});
			case "012":
				$scope.REPORT_HIERARCHY.push({LABEL: '營運區', DATA: 'AREA'});
			case "011":
			case "009":
				$scope.REPORT_HIERARCHY.push({LABEL: '分行', DATA: 'BRH'});
				break;
		}
		
		$scope.dateChange();
	};
	
	$scope.init();
	
	$scope.getDeptList = function() {
		switch ($scope.inputVO.reportHierarchy) {
			case "BRH":
			case "AREA":
			case "REGION":
				$scope.sendRecv("PMS510", "getDeptList", "com.systex.jbranch.app.server.fps.pms510.PMS510InputVO", $scope.inputVO, function(tota, isError) {
					if (!isError) {
						if(tota[0].body.deptList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							
							$scope.deptList = [];
							
							return;
		        		}
						
						$scope.deptList = tota[0].body.deptList;
					}						
				});
				break;
		}
	}
	
	$scope.query = function() {
		if ($scope.inputVO.sCreDate == '' || $scope.inputVO.sCreDate == undefined || 
			$scope.inputVO.reportHierarchy == '' || $scope.inputVO.reportHierarchy == undefined) {
			$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位(年月、報表層級)');
    		return;
    	} else if (($scope.inputVO.reportHierarchy != 'ALL') && ($scope.inputVO.deptID == null || $scope.inputVO.deptID == '' || $scope.inputVO.deptID == undefined)){
    		$scope.showErrorMsg('欄位檢核錯誤:若報表層級非「全行」，組織必要輸入欄位');
    		return;
    	}

		$scope.sendRecv("PMS510", "query", "com.systex.jbranch.app.server.fps.pms510.PMS510InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
					
					$scope.titleBrh = [];
					$scope.titleColName = [];
					$scope.titleCol = [];
					$scope.resultList = [];
					$scope.outputVO = [];
					
					return;
        		}
				
				$scope.titleBrh = tota[0].body.titleBrh;
				$scope.titleColName = tota[0].body.titleColName;
				$scope.titleCol = tota[0].body.titleCol;
				
				$scope.resultList = tota[0].body.resultList;
				
				$scope.outputVO = tota[0].body;
			}						
		});
	};
	
	$scope.exportRPT = function(){
		$scope.inputVO.titleBrh = $scope.titleBrh;
		$scope.inputVO.titleColName = $scope.titleColName;
		$scope.inputVO.titleCol = $scope.titleCol;
		$scope.inputVO.exportList = $scope.resultList;
		
		$scope.sendRecv("PMS510", "exportRPT", "com.systex.jbranch.app.server.fps.pms510.PMS510InputVO", $scope.inputVO, function(tota, isError) {						
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
        	}
		});
	};
	
	$scope.isTypeOF = function (value) {
		if (value == '-') {
			return 'string';
		} else if (isNaN(parseFloat(value))) {
			return 'string';
		} else {
			return 'number';
		}
	};
});
