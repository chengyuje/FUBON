/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG420Controller', function(sysInfoService, $scope, $controller, $filter, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "ORG420Controller";
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
    
	$scope.init = function(repType) {
		$scope.empLeftJobLst = [];
		$scope.totals = {};
		$scope.cMonth = ''
		$scope.today = ''; 
		
		$scope.inputVO = {
				toDay			 : '', 
				RPT_TYPE         : (repType == undefined ? 'JOB' : repType),
				region_center_id : '',
				branch_area_id   : '',
				branch_nbr       : '',
				EXPORT_LST		 : []
		};
		
		//組織連動
        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
        $scope.RegionController_setName($scope.region);
	};
	
	$scope.query = function() {
		$scope.totals = {};
		$scope.sendRecv("ORG420", "query", "com.systex.jbranch.app.server.fps.org420.ORG420InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
				$scope.empLeftJobLst = tota[0].body.empLeftJobLst;
				$scope.reportLst = tota[0].body.reportLst;
				$scope.inputVO.toDay = tota[0].body.toDay;
				$scope.outputVO = tota[0].body;
			}
		});
	};
	
	$scope.export = function() {
		angular.copy($scope.reportLst, $scope.inputVO.EXPORT_LST);
		$scope.inputVO.EXPORT_LST.push($scope.totals);

		$scope.sendRecv("ORG420", "export", "com.systex.jbranch.app.server.fps.org420.ORG420InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
			}
		});
	};
	
	$scope.init(undefined);
});
