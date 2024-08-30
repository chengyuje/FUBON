///======================
/// 通訊模組
///======================
eSoafApp.factory('projInfoService', ['sysInfoService',
	function(sysInfoService) {
		///======================
		/// singleton 用法
		///======================
		if ( arguments.callee._singletonInstance ) {
	    	return arguments.callee._singletonInstance;
		}
		//extends sysInfoService
		var projInfoService = sysInfoService;
		
		//START
		projInfoService.mappingSet = {};
		//eComboPlaceholder by proj
		projInfoService.eComboPlaceholder = '請選擇';
		
		//singleton 用法
		arguments.callee._singletonInstance = projInfoService;
		
	    return projInfoService;
 	}]
);
