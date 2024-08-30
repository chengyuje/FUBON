'use strict';
eSoafApp.controller('CRM513Controller', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CRM513Controller";
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	// time picker
	$scope.startDateOptions = {
		maxDate: $scope.inputVO.eDate || $scope.maxDate,
		minDate: $scope.minDate
	};

	$scope.endDateOptions = {
		maxDate: $scope.maxDate,
		minDate: $scope.inputVO.sDate || $scope.minDate
	};
	
    $scope.startDateOptions2 = {
		maxDate: $scope.inputVO.eDate || $scope.maxDate,
		minDate: $scope.inputVO.sDate || $scope.minDate
	};
    
	$scope.endDateOptions2 = {
		maxDate: $scope.maxDate,
		minDate: $scope.inputVO.eDate || $scope.minDate
	};
	
	$scope.altInputFormats = ['M!/d!/yyyy'];
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
	$scope.limitDate = function() {
		$scope.startDateOptions.maxDate = $scope.inputVO.eDate || $scope.inputVO.sDate2 || $scope.maxDate;
		
		$scope.startDateOptions2.minDate = $scope.inputVO.sDate || $scope.minDate;
		
		$scope.endDateOptions.minDate = $scope.inputVO.sDate || $scope.minDate;
		$scope.endDateOptions.maxDate = $scope.inputVO.eDate2;
		
		$scope.endDateOptions2.minDate = $scope.inputVO.eDate || $scope.minDate;
	};
  	
    $scope.init = function(){
		$scope.inputVO = {
			custID: '',
			branchNbr:'',
			sDate: undefined, 
			eDate: undefined
    	};
		
		$scope.limitDate();
		
		//組織連動
        $scope.region = ['N', $scope.inputVO, "region", "REGION_LIST", "area_id", "AREA_LIST", "branchNbr", "BRANCH_LIST", "aoCode", "AO_LIST", "emp_id", "EMP_LIST"];
        $scope.RegionController_setName($scope.region);
	};
	$scope.init();
    
	$scope.inquireInit = function() {
		$scope.data = [];
		$scope.tradeList = [];
		$scope.outputVO = [];
	};
	$scope.inquireInit();	
	
	$scope.query = function() {
		$scope.tradeList = [];
		$scope.outputVO = [];
		
		$scope.sendRecv("CRM513", "query", "com.systex.jbranch.app.server.fps.crm513.CRM513InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.tradeList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				$scope.tradeList = tota[0].body.tradeList;
				$scope.outputVO = tota[0].body;
				
				return;
			}
		});
	}
	
	$scope.toUppercase_data = function(value, type){
		switch (type) {
			case 'custid':
				if(value){
					$scope.inputVO.custID = value.toUpperCase();
				}
				break;
			default:
				break;
			}
	}
	
    $scope.comma_split = function(value) {
    	if (null == value) {
    		return '';
    	} else {
    		return value.split(';');
    	}
    }
    
	$scope.export = function(){
		$scope.inputVO.tradeList = $scope.tradeList;
		$scope.sendRecv("CRM513", "export", "com.systex.jbranch.app.server.fps.crm513.CRM513InputVO", $scope.inputVO, function(tota, isError) {						
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
        	}
		});
	};
});
