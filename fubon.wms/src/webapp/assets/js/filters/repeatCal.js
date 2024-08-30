/**================================================================================================
@program: repeatCal.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.filter('sumOfValue', function() {
	return function(data, key) {
		if (angular.isUndefined(data) || angular.isUndefined(key))
			return 0;
		var sum = 0;

		angular.forEach(data, function(v, k) {
			if (!angular.isUndefined(v[key]) && v[key]) {
				sum = sum + parseInt(v[key]);
			}
		});
		
		return sum;
	}
});