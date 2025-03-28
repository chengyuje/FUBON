'use strict';
eSoafApp.controller('CRM860Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM860Controller";
		
		// combobox
		getParameter.XML(["CRM.PRD_TYPE", "CRM.PRD_TYPE_SHOW"], function(totas) {
			if (totas) {
				$scope.PRD_TYPE = totas.data[totas.key.indexOf('CRM.PRD_TYPE')];
				$scope.PRD_TYPE_SHOW = totas.data[totas.key.indexOf('CRM.PRD_TYPE_SHOW')];
			}
		});
		
		// date picker
		$scope.startDateOptions = {};
		$scope.endDateOptions = {};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.startDateOptions.maxDate = $scope.inputVO.endDate;
			$scope.endDateOptions.minDate = $scope.inputVO.startDate;
			
			if($scope.inputVO.endDate) {
				var tempDate = new Date($scope.inputVO.endDate.getFullYear(), $scope.inputVO.endDate.getMonth(), $scope.inputVO.endDate.getDate() - 365);				
				$scope.startDateOptions.minDate = tempDate;

			}
			
			if($scope.inputVO.startDate) {
				var tempDate = new Date($scope.inputVO.startDate.getFullYear(), $scope.inputVO.startDate.getMonth(), $scope.inputVO.startDate.getDate() + 365)
				$scope.endDateOptions.maxDate = tempDate;		
			}
		};
		// date picker end
		
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.inputVO.custID = $scope.custVO.CUST_ID;
			var min_mon = new Date();
			min_mon.setMonth(min_mon.getMonth() - 3);
			min_mon.setHours(0, 0, 0, 0);
			$scope.inputVO.startDate = min_mon;
			var min_mon2 = new Date();
			min_mon2.setDate(min_mon2.getDate() - 1);
			min_mon2.setHours(0, 0, 0, 0);
			$scope.inputVO.endDate = min_mon2;
			$scope.limitDate();
			$scope.firstTime = true;
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.prdList = [];
			$scope.curList = [];
			$scope.divList = [];
			$scope.outputVO1 = [];
			$scope.outputVO2 = [];
			$scope.outputVO3 = [];
			
			$scope.TOTAL_RTN_TWD = 0;
			$scope.TOTAL_AMT_TWD = 0;
			$scope.TOTAL_COST_TWD = 0;
			$scope.TOTAL_DIVID_TWD = 0;
			
			$scope.TOTAL_BOND_PL_TWD = 0;//海外債總損益
			$scope.TOTAL_BOND_COST_TWD = 0;//海外債總成本
		}
		$scope.inquireInit();
		
		$scope.inquire = function() {
			$scope.sendRecv("CRM860", "inquire", "com.systex.jbranch.app.server.fps.crm860.CRM860InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {						
						if(tota[0].body.prdList.length == 0) {
							$scope.showMsg("ehl_01_common_009");						
			         		return;
                		}
						$scope.prdList = tota[0].body.prdList;
						$scope.curList = tota[0].body.curList;
						$scope.outputVO1 = {'prdList': $scope.prdList};
						$scope.outputVO2 = {'curList': $scope.curList};
						
						$scope.TOTAL_RTN_TWD = 0;
						$scope.TOTAL_AMT_TWD = 0;
						$scope.TOTAL_COST_TWD = 0;
						angular.forEach($scope.curList, function(row) {
							$scope.TOTAL_RTN_TWD += row.REF_AMT_TWD - row.INV_COST_TWD;
							$scope.TOTAL_AMT_TWD += row.REF_AMT_TWD;
							$scope.TOTAL_COST_TWD += row.INV_COST_TWD;
						});
						
						$scope.TOTAL_BOND_PL_TWD = 0;
						$scope.TOTAL_BOND_COST_TWD = 0;
						if($scope.inputVO.prdType == '03') {	//海外債
							angular.forEach($scope.prdList, function(row) {
								$scope.TOTAL_BOND_COST_TWD += row.INV_COST_TWD;
								if(row.PROD_TYPE == 'PD22') {	
									//海外債(到期):損益 = 還本原幣金額 + 累積配息金額 - 申購成交金額 -申購前手息
									$scope.TOTAL_BOND_PL_TWD += (row.REF_AMT_TWD + row.TXN_DIVID_TWD - row.INV_COST_TWD - row.PUR_PRE_DIVID_TW);
								} else if(row.PROD_TYPE == 'PD23') {	
									//海外債(贖回):損益 = 還本原幣金額 + 贖回前手息 + 累積配息金額 - 申購成交金額 -申購前手息
									$scope.TOTAL_BOND_PL_TWD += (row.REF_AMT_TWD + row.RDM_PRE_DIVID_TW + row.TXN_DIVID_TWD - row.INV_COST_TWD - row.PUR_PRE_DIVID_TW);
								}
							});
						}
					}
			});
		};
		$scope.inquire2 = function() {
			$scope.sendRecv("CRM860", "inquire2", "com.systex.jbranch.app.server.fps.crm860.CRM860InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.divList.length == 0)
							return;
						$scope.divList = tota[0].body.divList;
						$scope.outputVO3 = tota[0].body;
						$scope.TOTAL_DIVID_TWD = 0;
						angular.forEach($scope.divList, function(row) {
							$scope.TOTAL_DIVID_TWD += row.TXN_DIVID_TWD;
						});
					}
			});
		};
		
		$scope.setPtype = function(type) {
			$scope.inputVO.prdType = type;
			if(!$scope.firstTime) {
				$scope.inquireInit();
				$scope.inquire();
				$scope.inquire2();
			}
			// 進入畫面會先叫一次, 不查詢
			$scope.firstTime = false;
		};
		
		$scope.clearDate = function() {
			$scope.startDateOptions = {};
			$scope.endDateOptions = {};
			$scope.inputVO.endDate = undefined;
			$scope.inputVO.startDate = undefined;
		};
		
		
});