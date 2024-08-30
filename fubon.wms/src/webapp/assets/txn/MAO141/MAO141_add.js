/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MAO141_addController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MAO141_addController";
		
		//組織連動繼承
		$controller('RegionController', {$scope: $scope});
		
		// filter
		getParameter.XML(["MAO.DEV_SITE_TYPE"], function(totas) {
			if (totas) {
				$scope.mappingSet['MAO.DEV_SITE_TYPE'] = totas.data[totas.key.indexOf('MAO.DEV_SITE_TYPE')];
			}
		});
		
		$scope.init = function() {
			$scope.inputVO = {
					regionCenterId : '',
					bra_areaID : '',
					branchNbr : '',
					dev_nbr : '',
					dev_site_type : ''
			}
			
			//組織連動
	        $scope.region = ['N', $scope.inputVO, "regionCenterId", "REGION_LIST", "bra_areaID", "AREA_LIST", "branchNbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
	        $scope.RegionController_setName($scope.region);
		}
		$scope.init();

		$scope.add = function(){
        	$scope.sendRecv("MAO141", "add", "com.systex.jbranch.app.server.fps.mao141.MAO141InputVO", $scope.inputVO,
    			function(totas, isError) {
        			if (isError) {
        				$scope.showErrorMsgInDialog(totas.body.msgData);
        				return;
	                }
	                if (totas.length > 0) {
	                	$scope.showMsg('儲存成功');
	                	$scope.closeThisDialog('successful');
	                };
	            }
        	);
        }
		
		
		
});