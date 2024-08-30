/**================================================================================================
@program: ePercent.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.filter('ePercent', function() {
    return function (input) {
    	//check param
    	if (!input) {return 0.00+"%"};
    	if (input == null) {return 0.00+"%"};
    	if (typeof input != "number") {return 0.00+"%"};
    	//setting
    	input = input * 100;
    	input = input.toString().trim();
    	//conditions
    	var chk = input.indexOf(".");
    	if(chk == -1){
    		return input+=".00%";
    	}else{
    		var counts = input.substring(chk+1,input.length);
    		if(counts.length == 1){
	    		return input+="0%";
    		}else if(counts.length == 2){
    			return input+="%";
			}else{
    			return input = input.substring(0,input.indexOf(".")+3)+"%";
    		}
    	}
        return input;
    };
});