/**================================================================================================
@program: eChangeBraByte.js
@description: 
@version: 1.0.20200214
=================================================================================================*/
eSoafApp.filter('eChangeBraByte', function() {
    return function (item) {
    	return item? '0' + item: '';
    };
});