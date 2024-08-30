
/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM170Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM170Controller";
		
		$scope.priID = sysInfoService.getPriID();
		
        $scope.altInputFormats = ['M!/d!/yyyy'];
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
        $scope.importStartDateOptions = {
    		maxDate: $scope.inputVO.importEDate || $scope.maxDate,
    		minDate: $scope.minDate
		};
		$scope.importEndDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.importSDate || $scope.minDate
		};
		$scope.limitDate = function() {
			$scope.importStartDateOptions.maxDate = $scope.inputVO.importEDate || $scope.maxDate;
			$scope.importEndDateOptions.minDate = $scope.inputVO.importSDate || $scope.minDate;
		};
		
		$scope.startDateOptions = {
			maxDate: $scope.inputVO.eDate || $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.endDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.sDate || $scope.minDate
		};
		$scope.endDateOptions2 = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.eDate || $scope.minDate
		};
		$scope.limitDate2 = function() {
			$scope.startDateOptions.maxDate = $scope.inputVO.eDate || $scope.maxDate;
			
			$scope.endDateOptions.minDate = $scope.inputVO.sDate || $scope.minDate;
			$scope.endDateOptions.maxDate = $scope.inputVO.eDate2;
			
			$scope.endDateOptions2.minDate = $scope.inputVO.eDate || $scope.minDate;
		};
		
		$scope.init = function(){
			$scope.inputVO = {
					campID: '',
					importSDate: undefined,
					importEDate: undefined,
					campName: '',
					sDate: undefined,
					eDate: undefined,
					eDate2: undefined
			};
			
			$scope.limitDate();
			$scope.limitDate2();
			$scope.campaignList = null;
		};
		$scope.init();
		
		$scope.query = function() {
			$scope.sendRecv("CAM170", "query", "com.systex.jbranch.app.server.fps.cam170.CAM170InputVO", $scope.inputVO,
					function(tota, isError) {
						$scope.campaignList = [];
							if (!isError) {
								if(tota[0].body.campaignList.length == 0) {
									$scope.showMsg("ehl_01_common_009");
			            			return;
			            		}
								$scope.campaignList = tota[0].body.campaignList;
								$scope.outputVO = tota[0].body;
								return;
							}
					}
			);
		}
		
		$scope.statistics = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CAM170/CAM170_STATISTICS.html',
				className: 'CAM170_STATISTICS',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.examVersion = row.EXAM_ID;
                }]
			})
		}
		
        $scope.export = function(row) {
			$scope.sendRecv("CAM170", "export", "com.systex.jbranch.app.server.fps.cam170.CAM170InputVO", {examVersion: row.EXAM_ID},
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    	}
                    	if (totas.length > 0) {
                    		if(totas[0].body.reportList && totas[0].body.reportList.length == 0) {
                    			$scope.showMsg("ehl_01_common_009");
                    			return;
                    		}
                    	};
    				}
    		);
            };
		
});
