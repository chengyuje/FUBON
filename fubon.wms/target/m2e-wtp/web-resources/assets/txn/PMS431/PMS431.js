'use strict';
eSoafApp.controller('PMS431Controller', function ($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
        
	$controller('BaseController', {$scope: $scope});
    $controller('PMSRegionController', {$scope: $scope});
    
	$scope.controllerName = "PMS431Controller";

	//組織連動繼承
	$controller('RegionController', {$scope: $scope});

	getParameter.XML(["PMS.HIGH_RISK_INV_TYPE"], function(totas) {
		if (totas) {
			$scope.HIGH_RISK_INV_TYPE = totas.data[totas.key.indexOf('PMS.HIGH_RISK_INV_TYPE')];
		}
	});
	
	var NowDate = new Date();
	var yr = NowDate.getFullYear();
    var mm = NowDate.getMonth()+1;
    var strmm = '';
    $scope.mappingSet['timeE'] = [];
    for(var i = 0; i < 13; i++){
    	mm = mm -1;
    	if(mm == 0){
    		mm = 12;
    		yr = yr-1;
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

    //選取月份下拉選單 --> 重新設定可視範圍
    $scope.dateChange = function(){
	   if ($scope.inputVO.sCreDate != '') {
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
			sCreDate         : undefined,				
			region_center_id : '',
			branch_area_id   : '' ,	
			branch_nbr       : '',
			empID            : '' ,
			highRiskInvType  : '',
			noteStatus       : '',
			memLoginFlag     : String(sysInfoService.getMemLoginFlag())
    	};
		
		$scope.resultList = [];
		$scope.outputVO = [];
		$scope.curDate = new Date();
		$scope.dateChange();
	    
		$scope.disableRegionCombo = false;
		$scope.disableAreaCombo = false;
		$scope.disableBranchCombo = false;
		$scope.disableAoCombo = false;

        $scope.test = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "aoCode", "AO_LIST", "emp_id", "EMP_LIST"];
        $scope.RegionController_setName($scope.test);
        
	};
	
	$scope.init();
	
	$scope.query = function(){
		if ($scope.inputVO.sCreDate == '' || $scope.inputVO.sCreDate == undefined) {
			$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位(統計月份)');
    		return;
    	}

		$scope.sendRecv("PMS431", "queryData", "com.systex.jbranch.app.server.fps.pms431.PMS431InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
					$scope.data = [];
					return;
        		}
				$scope.data = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;
				return;
			}						
		});
	};
	
	$scope.openReview = function(type, row, logPri) {
		var page = '';
		switch (logPri) {
			case 'BRH':
				page = 'PMS431';
				
				break;
			case 'UHRM':
				page = 'PMS431U';
				
				break;
		}
		
		var dialog = ngDialog.open({
			template: 'assets/txn/' + page + '/' + page + '_REVIEW.html',
			className: page,
			showClose: false,
			controller: ['$scope', function($scope) {
				$scope.row = row
			}]
		}).closePromise.then(function (data) {
			$scope.query();
		});
	};		
	
    // 匯出 Csv
	$scope.exportRPT = function(){
		$scope.sendRecv("PMS431", "export", "com.systex.jbranch.app.server.fps.pms431.PMS431InputVO", $scope.inputVO, function(tota, isError) {						
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
        	}
		});
	};
});
