/**================================================================================================
@program: smallTitle.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.filter('smallTitle', function() {
    return function (input) {
    	//check param
    	if (!input) {return " - "}
    	if (typeof input != "string") { input.toString().trim(); }
    	//setting
    	if(input.length>6) {
    		input = input.substring(0,6) + "...";
    	}
        return input;
    };
});