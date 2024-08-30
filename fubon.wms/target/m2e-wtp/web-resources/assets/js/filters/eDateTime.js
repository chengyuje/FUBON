///
/// 日期格式化 filters
///
eSoafApp.filter('eDateTime', function() {
    return function (input) {
    	var cData = "/";
    	if (input == null) return "";
    	if (input.length >= 6 && input.length <= 8) {
			var oRegExp = new RegExp("([0-9]{2,4})([0-9]{2})([0-9]{2})","g");
			if (oRegExp.test(input)) {
				var strTemp = input.replace(oRegExp, "$1" + cData + "$2" + cData + "$3");	
				input = strTemp;
			}
    	}
        return input;
    };
});

eSoafApp.filter('eMoney', function() {
    return function (input) {
        return S2M(input);
    };    
});