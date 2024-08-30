/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS106_GIController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS106_GIController";
	
		$scope.init = function(){
			$scope.inputVO = {
				dataMonth: '',
				br_id: '',
				ao_code: ''
        	};	
			$scope.strYrMn = ($scope.row.YEARMON).substring(0,4)+'/'+($scope.row.YEARMON).substring(4,6);
        };
        $scope.init();
	    
        /*** 查詢資料 ***/
		$scope.queryGI = function(){
			$scope.inputVO.dataMonth = $scope.row.YEARMON;
			$scope.inputVO.branch_nbr = $scope.row.BRANCH_NBR;
			$scope.inputVO.ao_code = $scope.row.AO_CODE;
			if($scope.row.BRANCH_NBR==null){
				$scope.inputVO.ao_code='000';
			}
			$scope.sendRecv("PMS106", "queryGI", "com.systex.jbranch.app.server.fps.pms106.PMS106InputVO", $scope.inputVO,
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
		$scope.queryGI();
                	
});
