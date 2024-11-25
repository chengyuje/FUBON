
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
		
		$scope.optionsInit = function() {
			$scope.importStartDateOptions = {
				maxDate: $scope.inputVO.importEDate || $scope.maxDate,
				minDate: $scope.minDate
			};
			$scope.importEndDateOptions = {
				maxDate: $scope.maxDate,
				minDate: $scope.inputVO.importSDate || $scope.minDate
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
		}
       
		$scope.limitDate = function() {
			$scope.importStartDateOptions.maxDate = $scope.inputVO.importEDate || $scope.maxDate;
			if ($scope.inputVO.importEDate) {
				let y = $scope.inputVO.importEDate.getFullYear() - 1;
				let m = $scope.inputVO.importEDate.getMonth();
				let d = $scope.inputVO.importEDate.getDate();
				$scope.importStartDateOptions.minDate = new Date(y, m, d);
			}
			$scope.importEndDateOptions.minDate = $scope.inputVO.importSDate || $scope.minDate;
			if ($scope.inputVO.importSDate) {
				let y = $scope.inputVO.importSDate.getFullYear() + 1;
				let m = $scope.inputVO.importSDate.getMonth();
				let d = $scope.inputVO.importSDate.getDate();
				$scope.importEndDateOptions.maxDate = new Date(y, m, d);
			}
		};
		
		$scope.limitDate2 = function() {
			$scope.startDateOptions.maxDate = $scope.inputVO.eDate || $scope.maxDate;
			
			$scope.endDateOptions.maxDate = $scope.inputVO.eDate2;
			if ($scope.inputVO.eDate2) {
				let y = $scope.inputVO.eDate2.getFullYear() - 1;
				let m = $scope.inputVO.eDate2.getMonth();
				let d = $scope.inputVO.eDate2.getDate();
				$scope.endDateOptions.minDate = new Date(y, m, d);
			}
			$scope.endDateOptions2.minDate = $scope.inputVO.eDate || $scope.minDate;
			if ($scope.inputVO.eDate) {
				let y = $scope.inputVO.eDate.getFullYear() + 1;
				let m = $scope.inputVO.eDate.getMonth();
				let d = $scope.inputVO.eDate.getDate();
				$scope.endDateOptions2.maxDate = new Date(y, m, d);
			}
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
			$scope.optionsInit();
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
