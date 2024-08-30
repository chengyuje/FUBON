'use strict';
eSoafApp.controller('ORG121_EDITController', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $q, $filter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "ORG121_EDITController";
	
	// filter
	getParameter.XML(["ORG.ATCH_REASON"], function(totas) {
		if (totas) {
			$scope.mappingSet['ORG.ATCH_REASON'] = totas.data[totas.key.indexOf('ORG.ATCH_REASON')];
		}
	});
	
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
    $scope.limitDate2 = function(){
    	$scope.dateTemp = true;
    }
    //
	
	$scope.initial = function(){
		$scope.aoSize = 0;
		$scope.inputVO = {
				regionCenterID	: '',				//區域中心
				opAreaID		: '',				//營運區
				branchNbr		: '',				//分行
				empID			: $scope.row.EMP_ID,//員工編號
				aoPerfEffDate	: undefined,		//業績生效日
				aoType			: '',				//FC/FCH
				typeOne			: '',				
				aoList			: []
		};
	}
	
	$scope.initial();
	
    $scope.inquire = function(){
		$scope.sendRecv("ORG121", "showORG121MOD", "com.systex.jbranch.app.server.fps.org121.ORG121InputVO", {'empID' :$scope.inputVO.empID},
			function(tota, isError) {
				if (!isError) {
					$scope.paramList = tota[0].body.modList;

					$scope.inputVO.regionCenterID = $scope.paramList[0].REGION_CENTER_ID;
					$scope.inputVO.opAreaID = $scope.paramList[0].BRANCH_AREA_ID;
					$scope.inputVO.branchNbr = $scope.paramList[0].BRANCH_NBR;
					$scope.inputVO.empID = $scope.paramList[0].EMP_ID;
					$scope.inputVO.aoPerfEffDate = $scope.toJsDate($scope.paramList[0].PERF_EFF_DATE); //業績生效日
				}
		}); 	
    };

    $scope.inquire();
    
    $scope.addReview = function () {
    	var deferred = $q.defer();
    	
    	console.log("addReview");
		$scope.sendRecv("ORG121", "addAoCodeSetting", "com.systex.jbranch.app.server.fps.org121.ORG121InputVO", $scope.inputVO, function(tota, isError) {		                        	
			if (isError) {
				$scope.showErrorMsg(tota[0].body.msgData);
				$scope.showMsg('ehl_01_common_005');
				
				return;
			}
			
			if(tota.length > 0) {
				deferred.resolve("success");
			}
		});
		
		return deferred.promise;
    }
    
    $scope.addAoCodeSetting = function(){
    	if ($filter('date')($scope.inputVO.aoPerfEffDate, "yyyy-MM-dd") !== $filter('date')($scope.toJsDate($scope.paramList[0].PERF_EFF_DATE), "yyyy-MM-dd")) {
			$scope.inputVO.typeOne = '1';
			$scope.addReview().then(function(data) {
				$scope.showSuccessMsg('ehl_01_common_015');
				$scope.closeThisDialog('successful');
			});
		}
    };
});