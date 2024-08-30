/**================================================================================================
@program: eMask.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.filter('eMask', function() {
    return function (input, num, way) {
    	//check param
    	if (!input) {return input}
    	input.toString().trim();
    	//setting
    	if(!num){
    		input='';
    		for(var i=0; i<input.length; i+=1){
    			input+='*';
    		}
    	}
    	if(num && input.length>=num) {
    		var str='';
    		for(var i=0; i<num; i+=1){
    			str+='*';
    		}
    		if(way=='left'){
    			input = str + input.substring(num, input.length-num);
    		}else{
    			input = input.substring(0, input.length-num) + str;
    		}
    	}
        return input;
    };
});