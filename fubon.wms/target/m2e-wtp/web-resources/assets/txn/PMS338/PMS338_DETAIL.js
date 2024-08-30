/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS338_DETAILController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $timeout, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS338_DETAILController";
		$scope.init = function(){
			$scope.inputVO.yearMon = $scope.yearMon;
			$scope.inputVO.loanType = $scope.loanType;
	    	$scope.sendRecv("PMS338", "detail", "com.systex.jbranch.app.server.fps.pms338.PMS338InputVO", $scope.inputVO,
	    			function(tota, isError) 
	    			{
						if (!isError) 
						{
							if(tota[0].body.detailList.length == 0) 
							{
								$scope.showMsg("ehl_01_common_009");
		            			return;
		            		}
							$scope.datList = tota[0].body.detailList;
							$scope.outputVO = tota[0].body;
							return;
						}else
						{
							$scope.showBtn = 'none';
						}	
			    	});
		}
		$scope.init(); 
});