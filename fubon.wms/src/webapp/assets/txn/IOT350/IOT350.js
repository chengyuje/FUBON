/**
 * 
 */
'use strict';
eSoafApp.controller('IOT350Controller', 
    function($rootScope,$scope,$controller, $confirm, sysInfoService,socketService, ngDialog, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "IOT350Controller";
        
        $scope.date = new Date();
        $scope.hideTable='';
        // init
		$scope.init = function() {
			$scope.inputVO = {
				insId : '',
				custId : '',
				insuredId : '',
				applyDateFrom : undefined,
				applyDateTo : undefined
			};
		};
		$scope.init();
		
		// date picker
		$scope.sDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.eDateOptions = {
				maxDate: $scope.maxDate,
				minDate: $scope.minDate
			};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.sDateOptions.maxDate = $scope.inputVO.applyDateTo || $scope.maxDate;
			$scope.eDateOptions.minDate = $scope.inputVO.applyDateFrom || $scope.minDate;
		};
		// date picker end
		
		$scope.clear = function(){
			$scope.inputVO = {
					insId : '',
					custId : '',
					insuredId : '',
					applyDateFrom : undefined,
					applyDateTo : undefined
				};
			$scope.limitDate();
		}
		
		$scope.queryData = function() {
			$scope.paramList = [];
			$scope.outputVO = [];
			$scope.inputVO.custId = $scope.inputVO.custId.toUpperCase();
			$scope.inputVO.insuredId = $scope.inputVO.insuredId.toUpperCase();
			$scope.sendRecv("IOT350", "queryData", "com.systex.jbranch.app.server.fps.iot350.IOT350InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							
							for(var i=0; i<$scope.paramList.length; i++){
								if($scope.paramList[i].PPT_TYPE == 'A') {
									$scope.paramList[i].PPT_TYPE = '住宅火險(地震險、家庭綜合險)';
								} else if($scope.paramList[i].PPT_TYPE == 'B') {
									$scope.paramList[i].PPT_TYPE = '汽(機)車險(任意及強制險)';
								} else if($scope.paramList[i].PPT_TYPE == 'C') {
									$scope.paramList[i].PPT_TYPE = '傷害險';
								} else if($scope.paramList[i].PPT_TYPE == 'D') {
									$scope.paramList[i].PPT_TYPE = '傷害險(附加健康險)';
								} else if($scope.paramList[i].PPT_TYPE == 'E') {
									$scope.paramList[i].PPT_TYPE = '健康險';
								} else if($scope.paramList[i].PPT_TYPE == 'F') {
									$scope.paramList[i].PPT_TYPE = '旅平險';
								} else if($scope.paramList[i].PPT_TYPE == 'G') {
									$scope.paramList[i].PPT_TYPE = '旅平險(附加健康險)';
								} else if($scope.paramList[i].PPT_TYPE == 'H') {
									$scope.paramList[i].PPT_TYPE = '個人責任險';
								} else if($scope.paramList[i].PPT_TYPE == 'I') {
									$scope.paramList[i].PPT_TYPE = '商業火險';
								} else if($scope.paramList[i].PPT_TYPE == 'J') {
									$scope.paramList[i].PPT_TYPE = '水險';
								} else if($scope.paramList[i].PPT_TYPE == 'K') {
									$scope.paramList[i].PPT_TYPE = '新種險';
								} else if($scope.paramList[i].PPT_TYPE == 'L') {
									$scope.paramList[i].PPT_TYPE = '工程險';
								} 
							}
							
							return;
						}
			});
		 };
		
	});