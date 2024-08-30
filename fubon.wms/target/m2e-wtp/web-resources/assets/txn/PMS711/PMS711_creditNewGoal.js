'use strict';
eSoafApp.controller('PMS711_creditNewGoalController', function($rootScope, $scope,
		$controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS711_creditNewGoalController";

	$scope.select = function() {
		$scope.inputVO.date_year = $scope.date_year;
		$scope.inputVO.personType = $scope.personType;
		$scope.sendRecv("PMS711", "queryCreditNewGoal",
				"com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",
				$scope.inputVO, function(tota, isError) {
					if (!isError) {
						$scope.showList = tota[0].body.showList;
						return;
					}
				});
	}
	$scope.select();
	
	//校驗輸入數據不能為空
	$scope.checkNull = function(){
		$scope.inputVO.showList = $scope.showList;
		for (var i = 0; i < $scope.inputVO.showList.length; i++) {
			if (String($scope.inputVO.showList[i].CEN_LONG_BUY) == "" || String($scope.inputVO.showList[i].CEN_LONG_BUY)==null) {
				$scope.showMsg("ehl_01_common_022");
				return;
			}
		}
		$scope.save();
	}
	
	//储存页面输入数据
	$scope.save = function() {
		$scope.inputVO.date_year = $scope.date_year;
		$scope.inputVO.personType = $scope.personType;
		$scope.inputVO.subProjectSeqId = $scope.subId;
		$scope.inputVO.isSpecial = $scope.isSpecial;
		
		$scope.sendRecv("PMS711", "saveCreditNewGoal",
				"com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",
				$scope.inputVO, function(tota, isError) {
					if (!isError) {
						$scope.showMsg("ehl_01_common_002");
					}
					else
					{
						$scope.showMsg("ehl_01_common_007");
					}
				});
	}
	
	//TS設定
	$scope.setTS = function() {
		var date_year = $scope.date_year;
		var personType = $scope.personType;
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS711/PMS711_creditNewGoalSetTS.html',
			className: 'PMS711_creditNewGoalSetTS',
			showClose: true,
			controller: ['$scope', function($scope) {
				$scope.date_year = date_year,
				$scope.personType = personType;
            }]
		});
	}
});