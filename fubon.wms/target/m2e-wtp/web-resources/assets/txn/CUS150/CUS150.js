/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CUS150Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CUS150Controller";
		
		// combobox
		getParameter.XML(["CUS.IVG_TYPE", "CUS.IVG_PLAN_TYPE"], function(totas) {
			if (totas) {
				$scope.IVG_TYPE = totas.data[totas.key.indexOf('CUS.IVG_TYPE')];
				$scope.IVG_PLAN_TYPE = totas.data[totas.key.indexOf('CUS.IVG_PLAN_TYPE')];
			}
		});
		//
		
		// date picker
		$scope.ivgStartDateOptions = {};
		$scope.ivgEndDateOptions = {};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.ivgStartDateOptions.maxDate = $scope.inputVO.ivgEndDate;
			$scope.ivgEndDateOptions.minDate = $scope.inputVO.ivgStartDate;
		};
		// date picker end
		
		// init
		$scope.init = function(){
			$scope.inputVO = {};
			$scope.limitDate();
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.paramList = [];
		}
		$scope.inquireInit();
		
		// inquire
		$scope.inquire = function() {
			$scope.sendRecv("CUS150", "inquire", "com.systex.jbranch.app.server.fps.cus150.CUS150InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							return;
						}
			});
		};
				
		$scope.detail = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CUS150/CUS150_DETAIL.html',
				className: 'CUS150_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;                
                }]
			});
		};
});
