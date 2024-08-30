	/*
 */
'use strict';
eSoafApp.controller('CUS140Controller',
	function($scope, $rootScope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CUS140Controller";

		// combobox
		getParameter.XML(["COMMON.YES_NO", "CUS.IVG_TYPE", "CUS.IVG_PLAN_TYPE"], function(totas) {
			if (totas) {
				$scope.YES_NO = totas.data[totas.key.indexOf('COMMON.YES_NO')];
				$scope.IVG_TYPE = totas.data[totas.key.indexOf('CUS.IVG_TYPE')];
				$scope.IVG_PLAN_TYPE = totas.data[totas.key.indexOf('CUS.IVG_PLAN_TYPE')];
			}
		});
		//

		// date picker
		$scope.sIvgStartDateOptions = {};
		$scope.eIvgEndDateOptions = {};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		//放入起訖日期
		$scope.limitDate = function() {
			$scope.sIvgStartDateOptions.maxDate = $scope.inputVO.ivgEndDate;
			$scope.eIvgEndDateOptions.minDate = $scope.inputVO.ivgStartDate;
		};
		//date picker end

		// init
		$scope.nowDate = new Date();
		$scope.nowDate.setHours(0,0,0,0);
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.limitDate();
            $scope.inputVO.memLoginFlag = String(sysInfoService.getMemLoginFlag());
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.paramList = [];
		}
		$scope.inquireInit();

		$scope.inquire = function() {
			$scope.sendRecv("CUS140", "queryData", "com.systex.jbranch.app.server.fps.cus140.CUS140InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.paramList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
					}
				});
		};
		$scope.inquire();
		
		$scope.showDetail = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CUS140/CUS140_2.html',
				className: 'CUS140',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.inquireInit();
					$scope.inquire();
				}
			});
		};
		
		
});