///======================
/// 原DateUtil.as
///======================
eSoafApp.factory('dateService', 
	function() {
	
	 	return {
			parseRocDate: function(valueString) {
				console.log('value length=' + valueString.length);
		 		if(valueString.length == 7){
		 			
					var year = parseInt(valueString.substring(0,3), 10)+1911;
					var month = valueString.substring(3,5);
					var day = valueString.substring(5,7);
					console.log('year='+year+', month='+month+', day='+day);
		 			return new Date(year, month - 1, day);
		 		}
		 		return null;
			}
	 	};
	}
);