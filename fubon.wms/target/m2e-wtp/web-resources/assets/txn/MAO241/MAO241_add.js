/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MAO241_addController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MAO241_addController";
		
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
					dev_nbr : '',
					dev_site_type : ''
			}
		}
		$scope.init();

		$scope.add = function(){
        	$scope.sendRecv("MAO241", "add", "com.systex.jbranch.app.server.fps.mao241.MAO241InputVO", $scope.inputVO,
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