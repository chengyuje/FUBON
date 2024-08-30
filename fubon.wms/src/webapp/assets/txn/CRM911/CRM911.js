'use strict';
eSoafApp.controller('CRM911Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM911Controller";
		
		$scope.init = function(){
			$scope.sendRecv("CRM911", "initial", "com.systex.jbranch.app.server.fps.crm911.CRM911InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.listBase = tota[0].body.resultList;
					}
			});
		};
		$scope.init();
		
		// 2017/8/9 add function type
		if(projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["CRM911"])
			$scope.CanEdit = projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["CRM911"].FUNCTIONID["maintenance"];
		else
			$scope.CanEdit = false;
		
		// 排序用
		$scope.sortableOptions = {
			stop: function(e, ui) {
				angular.forEach(ui.item.sortable.droptargetModel, function(row, index){
					row.ORDER_NO = index + 1;
				});
			},
			disabled: !$scope.CanEdit
		};
		
		$scope.add = function(i) {
			$scope.listBase.splice((i+1), 0, {'ORDER_NO':(i+1),'LINK_NAME':'','LINK_URL':''});
			angular.forEach($scope.listBase, function(row, index) {
				row.ORDER_NO = index + 1;
			});
	    };
	    
		$scope.del = function(index) {
	    	$confirm({text: '是否刪除此筆資料？ '}, {size: 'sm'}).then(function() {
	    		$scope.listBase.splice(index,1);
	    		angular.forEach($scope.listBase, function(row, index) {
					row.ORDER_NO = index + 1;
				});
            });
	    };
	    
	    $scope.save = function() {
	    	if($scope.listBase.length == 0) {
	    		$scope.showErrorMsg('ehl_01_crm911_001');
	    		return;
	    	}
	    	// check null
	    	var check = false;
	    	angular.forEach($scope.listBase, function(row, index){
				if(!row.LINK_NAME || !row.LINK_URL)
					check = true;
			});
	    	if(check) {
	    		$scope.showErrorMsg('ehl_01_crm911_002');
	    		return;
	    	}
	    	//
	    	$scope.sendRecv("CRM911", "save", "com.systex.jbranch.app.server.fps.crm911.CRM911InputVO", {'list':$scope.listBase},
					function(totas, isError) {
	                	if (!isError) {
	                		$scope.init();
	                		$rootScope.GENERATE_MENU();
	                		$scope.showSuccessMsg('ehl_01_common_004');
	                	};
					}
			);
		};
		
		
});