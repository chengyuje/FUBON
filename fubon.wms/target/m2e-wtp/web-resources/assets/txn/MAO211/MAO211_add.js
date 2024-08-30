/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MAO211_addController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MAO211_addController";
		
		//===filter
        getParameter.XML(["CRM.REL_TYPE"], function(totas) {
			if (totas) {
				$scope.mappingSet['CRM.REL_TYPE'] = totas.data[totas.key.indexOf('CRM.REL_TYPE')];
			}
		});
        //===
        
		//get分行別all
		$scope.mappingSet['branch_nbr_all'] = [];
		angular.forEach(_.sortBy(projInfoService.getAvailBranch(), ['BRANCH_NBR']), function(row, index, objs){
			$scope.mappingSet['branch_nbr_all'].push({LABEL: row.BRANCH_NBR + '-' + row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		});
		
		$scope.add = function() {
			var ans = $scope.addList.filter(function(obj){
	    		return (obj.SELECTED == true);
	    	});
        	if(ans.length == 0){
        		return;
        	}
			$scope.closeThisDialog(ans);
		}
});