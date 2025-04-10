'use strict';
eSoafApp.controller('PMS711_tgGoalController', function($rootScope, $scope,
		$controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS711_tgGoalController";
	$scope.init = function() {
		$scope.showList = [];
	};
	$scope.init();
	/** 查詢 * */
	$scope.query = function() {
		$scope.inputVO.date_year = $scope.date_year;
		$scope.inputVO.personType = $scope.personType;
		$scope.inputVO.subProjectSeqId = $scope.subId;
		$scope.sendRecv("PMS711", "queryTgGoal","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",
			$scope.inputVO, function(tota, isError) {
				if (!isError) {
					if (tota[0].body.showList == null
							|| tota[0].body.showList.length == 0) {
						$scope.showList = [];
						return;
					}
					$scope.showList = tota[0].body.showList;
				}else{
					$scope.showMsg("ehl_01_common_009");
				}
			});
	}
	$scope.query();
	
	$scope.save = function() {
		$scope.inputVO.date_year = $scope.date_year;
		$scope.inputVO.personType = $scope.personType;
		$scope.inputVO.subProjectSeqId = $scope.subId;
		$scope.inputVO.showList = 	$scope.showList;
		$scope.inputVO.isSpecial = $scope.isSpecial;
		
		if(null==String($scope.showList[0].TAKING_PROP) || ''==String($scope.showList[0].TAKING_PROP)
				|| null==String($scope.showList[0].NEW_PROP) || ''==String($scope.showList[0].NEW_PROP)){
			$scope.showMsg("ehl_01_common_022");
			return;
		}
		for(var i=0; i<$scope.showList.length; i++){
			for(var key in $scope.showList[i]){
				if(null==String($scope.showList[i][key]) || ''==String($scope.showList[i][key])){
					$scope.showMsg("ehl_01_common_022");
					return;
				}
			}
		}
		$scope.sendRecv("PMS711", "saveTgGoal","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",
			$scope.inputVO, function(tota, isError) {
				if (!isError) {
					$scope.showMsg("ehl_01_common_002"); //更新成功
					$scope.query();
					return;
				}else{
					$scope.showErrorMsg("ehl_01_common_007");//更新失敗
				}
			});
	}
});