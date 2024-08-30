/**================================================================================================
@program: eMakePositive.js
@description: 
@version: 1.0.20200214
=================================================================================================*/
eSoafApp.filter('eMakePositive', function() {
    return function (item) {
    	return Math.abs(item);
    };
});