'use strict';
eSoafApp.controller('PMS711_pdGoalController', function($rootScope, $scope,
		$controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS711_pdGoalController";
	/** 查詢 * */
	$scope.query = function() {
		$scope.inputVO.date_year = $scope.date_year;
		$scope.inputVO.personType = $scope.personType;
		$scope.sendRecv("PMS711", "queryPdGoal","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",
		$scope.inputVO, function(tota, isError) {
			if (!isError) {
				if (tota[0].body.showList == null
						|| tota[0].body.showList.length == 0) {
					$scope.showList = [];
					$scope.showMsg("ehl_01_common_009");
					return;
				}
				$scope.showList = tota[0].body.showList;
			}else{
				$scope.showMsg("错误ehl_01_common_009");
			}
		});
	}
	$scope.query();
	
	$scope.save = function() {
		$scope.inputVO.showList = $scope.showList;
		$scope.inputVO.columnMap = $scope.showRowList;
		$scope.inputVO.date_year = $scope.date_year;
		$scope.inputVO.personType = $scope.personType;
		$scope.inputVO.subProjectSeqId = $scope.subId;
		$scope.inputVO.isSpecial = $scope.isSpecial;
		
		for(var i=0; i<$scope.showList.length; i++){
			for(var key in $scope.showList[i]){
				if(null==String($scope.showList[i][key]) || ''==String($scope.showList[i][key])){
					$scope.showMsg("ehl_01_common_022");  ////欄位檢核錯誤：必要輸入欄位
					return;
				}
			}
		}
		$scope.sendRecv("PMS711", "savePdGoal","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",
			$scope.inputVO, function(tota, isError) {
				if (!isError) {
					$scope.showMsg("ehl_01_common_002"); //更新成功
				}else{
					$scope.showErrorMsg("ehl_01_common_007");//更新失敗
				}
			});
	}
});