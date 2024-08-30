/**================================================================================================
@program: eLengthLimit.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive('eLengthLimit', function() {
	return {
		restrict: 'A',
		require: 'ngModel',
		link: function(scope, elm, attrs, ngModelCtrl) {
			if (!ngModelCtrl) {
				return;
			}
			ngModelCtrl.$parsers.unshift(function(val) {
				var check = false;
				var limit = attrs.eLengthLimit || 0;
				var type = attrs.eLengthType || 'UTF8';
				if (val != undefined) {
					var length = 0;
					if ("UTF8" == type) {
						for (var i = 0;i < val.length;i++) {
							length += encodeURIComponent(val.charAt(i)).replace(/%[A-F\d]{2}/g, 'U').length;
						}
					}
					else if ("HF" == type) {
						var s;
						for (var i = 0;i < val.length;i++) {
							s = val.charCodeAt(i);
							while( s > 0 ){
								length++;
								s = s >> 8;
							}
						}
					}
					else if ("ND" == type) {
						if(val.indexOf('.') != -1)
							length = val.substring(0, val.indexOf('.')).length + val.substring(val.indexOf('.') + 1, val.length).length;
						else
							length = val.length;
					}
					if(length > limit)
						check = true;
				}
				if(check) {
					var currentValue = ngModelCtrl.$modelValue;
					ngModelCtrl.$setViewValue(currentValue);
					ngModelCtrl.$render();
					return currentValue;
				}
				else
					return val;
			});
		}
	};
});