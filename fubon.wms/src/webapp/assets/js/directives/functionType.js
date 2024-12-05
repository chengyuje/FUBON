/**================================================================================================
@program: functionType.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive('functionType',['$timeout','projInfoService', function($timeout,projInfoService) {
    return {
		restrict: 'A',
		scope: {
			show: '@functionShow',
			type: '@functionType'
		},
		link: function(scope, elm, attrs) {
			/** Initialize **/
			_functionType_process(scope,projInfoService,elm,$timeout);
		}
	};
}]);
function _functionType_process(scope,projInfoService,elm,$timeout) {
	$timeout(function(){
		debugger;
		scope.show = scope.show || 'hide';
		var enable = false;
		//version 1: only current name , currentTxn extend by menu.
//		if (projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule] && projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID[projInfoService.$currentTxn])
//			enable = projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID[projInfoService.$currentTxn].FUNCTIONID[scope.type];
		//version 2: one by one authority settle.
		if (projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule] && projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID[scope.$parent.authority_txnName])
			enable = projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID[scope.$parent.authority_txnName].FUNCTIONID[scope.type];
		if ("disable" == scope.show) {
			if(!enable) {
				elm.attr('disabled', true);
			}
		} else {
			if(!enable) {
				elm.hide();
			}
		}
	});	
}