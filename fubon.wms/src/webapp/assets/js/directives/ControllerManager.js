/**================================================================================================
@program: ControllerManager.js
@description:
@version: 1.0.20160624
=================================================================================================*/
eSoafApp.directive('ngController', ["sysInfoService", "$timeout", function(sysInfoService, $timeout) {
	return {
		restrict: 'A',
//		require: '^functionType', //share controller by anther directives
		link: function(scope, element, attrs) {
			
			/** Initialize **/
			var ALL = attrs.ngController.replace('Controller','');
			var ID = ALL.split("_")[0];
			
			/** Setting TxnOwn Authority Code **/
			scope.$parent["authority_txnName"] = ID;
			
			/** function **/
			_ControllerManager_process(ID, scope, sysInfoService, $timeout);
			
			//[water mark]
//			document.getElementById('watermark').className = "hidden";
//			$("#watermark").addClass("hidden");
//			$("#watermark").removeClass("watermark");
//			$timeout(function(){
//				if(sysInfoService.getAuthorities() && sysInfoService.getAuthorities().MODULEID[sysInfoService.$currentModule].ITEMID[sysInfoService.$currentTxn] && sysInfoService.getAuthorities().MODULEID[sysInfoService.$currentModule].ITEMID[sysInfoService.$currentTxn].FUNCTIONID['watermark']){
//			    	document.getElementById('watermark').className = "watermark";
//					$("#watermark").removeClass("hidden");
//					$("#watermark").addClass("watermark");
//			    }
//			});
			
		}
	};
}]);
function _ControllerManager_process(ID, scope, sysInfoService, $timeout) {
	//get currentTxn code
	sysInfoService.$currentTxn = ID;
	//[function type] call a public function with no param
	_functionType_process;
}