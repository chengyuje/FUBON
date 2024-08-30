'use strict';
eSoafApp.controller('PMS362_EXPORTController',
	function($rootScope, $scope, $controller, $confirm, $filter, socketService, ngDialog, projInfoService,sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS362_EXPORTController";
		
		$scope.mappingSet['srchType'] = [];
        $scope.mappingSet['srchType'].push({LABEL:'Step1:分行收益核實版', DATA:'BR_PROFIT'}, {LABEL:'Step2:分行銷量核實版', DATA:'BR_SALES'}, {LABEL:'Step3:專員業績戰報資料', DATA:'AO_PROFIT'});
		/*
        $scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		*/	
        
		$scope.inputVO = {
				srchType: ''
		};
		
		$scope.uploadFinshed = function(name, rname) {
	    	$scope.inputVO.fileName = name;
        	$scope.inputVO.realfileName = rname;
        };
		
		$scope.insertExpt = function(set_name) {
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			
			var data = {
				srchType : $scope.inputVO.srchType,
//				time : $scope.inputVO.time,
				success : 'success'
			}
			
			$scope.sendRecv("PMS362", "uploadFile", "com.systex.jbranch.app.server.fps.pms362.PMS362InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg(tota[0].body.successMsg);
                		$scope.closeThisDialog(data);
					}
			});
		};
		

		
		
});