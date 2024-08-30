/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MPL110ListController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MPL110ListController";
		
		$scope.init = function() {
			$scope.inputVO = $scope.connector("get", "MPL110_inputVO");
			$scope.connector("set", "MPL110_inputVO", null);
			// 測試用
//			$scope.inputVO = { 
//				empID : '199850'
//			};
		}
		$scope.init();
		
		$scope.viewDetail = function() {
//			alert(JSON.stringify($scope.inputVO));
			$scope.sendRecv("MPL110", "inquire", "com.systex.jbranch.app.server.fps.mpl110.MPL110InputVO", $scope.inputVO,
				function(totas, isError) {
					if (totas[0].body.resultList.length == 0) {
						$scope.showMsg("ehl_01_common_009");
            			return;
            		}
					if (totas[0].body.resultList.length > 0) {
						$scope.resultList = _.sortBy(totas[0].body.resultList, ['BRANCH_NBR']);
					}	
					
					$scope.inputVO.isHEADMGR = totas[0].body.isHEADMGR; //是否有總行權限(Y/N)
					$scope.inputVO.isARMGR = totas[0].body.isARMGR;		//是否有業務處長權限(Y/N)
					$scope.inputVO.isMBRMGR = totas[0].body.isMBRMGR;	//是否有營運督導權限(Y/N)
			});
		};
		$scope.viewDetail();
		
		//以分行合計
		$scope.detail = function(row) {
//			alert(JSON.stringify(row));
			$scope.inputVO.regionID = ''; 
			$scope.inputVO.areaID = ''; 
			$scope.inputVO.branchID = row.BRANCH_NBR; 
			$scope.inputVO.showName = row.BRANCH_NBR + row.BRANCH_NAME;
			
			$scope.connector("set", "MPL110_inputVO", $scope.inputVO);
			$scope.connector("set", "MPL110_routeURL", 'assets/txn/MPL110/MPL110_MENU.html');
		}
		
		//以營運區合計
		$scope.detail_MBR = function(row) {
//			alert(JSON.stringify(row));
			$scope.inputVO.regionID = ''; 
			$scope.inputVO.areaID = row.BRANCH_AREA_ID; 
			$scope.inputVO.branchID = ''; 
			$scope.inputVO.showName = row.BRANCH_AREA_ID + row.BRANCH_AREA_NAME; 
			
			$scope.connector("set", "MPL110_inputVO", $scope.inputVO);
			$scope.connector("set", "MPL110_routeURL", 'assets/txn/MPL110/MPL110_MENU.html');
		}
		
		//以業務處合計
		$scope.detail_AR = function(row) {
//			alert(JSON.stringify(row));
			$scope.inputVO.regionID = row.REGION_CENTER_ID; 
			$scope.inputVO.areaID = ''; 
			$scope.inputVO.branchID = ''; 
			$scope.inputVO.showName = row.REGION_CENTER_ID + row.REGION_CENTER_NAME; 
			
			$scope.connector("set", "MPL110_inputVO", $scope.inputVO);
			$scope.connector("set", "MPL110_routeURL", 'assets/txn/MPL110/MPL110_MENU.html');
		}
		
		//以全行合計
		$scope.detail_ALL = function(row) {
//			alert(JSON.stringify(row));
			$scope.inputVO.regionID = ''; 
			$scope.inputVO.areaID = ''; 
			$scope.inputVO.branchID = ''; 
			$scope.inputVO.showName = '全行'; 
			
			$scope.connector("set", "MPL110_inputVO", $scope.inputVO);
			$scope.connector("set", "MPL110_routeURL", 'assets/txn/MPL110/MPL110_MENU.html');
		}
});