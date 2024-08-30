///======================
/// 原ObjUtil.as
///======================
eSoafApp.factory('objService', function() {

	return {
		/**
		 * source 來源
		 * destination 目地
		 */
		copy: function(source, destination) {

			for(var p in destination){
				
				var sourceProperty = source[p];
				if(sourceProperty != null){
					destination[p] = sourceProperty;
				}
			}
		}
	};
});