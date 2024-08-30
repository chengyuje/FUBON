/**================================================================================================
@program: mapping.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.filter('mapping', ['socketService', '$q', 'projInfoService', function(socketService, $q, projInfoService) {
	
	
	return function(input, mappingObj, style, type = 'single', splitCode = ";" ) {
		
		if(mappingObj === undefined){
//			console.log('mappingObj is undefined!');
			return input;
		}
//		var mappingObj = projInfoService.mappingSet[mapping];
//		console.log('mappingObj=' + JSON.stringify(mappingObj));
//		if(typeof mapping === 'string' && mappingObj === undefined && isLock == false){
//			isLock = true;
//			var inputVO = {'param_type': mapping, 'desc': false};
//			var deferred = $q.defer();
//			socketService.sendRecv("COMBOBOX", "query", "com.systex.jbranch.commons.ap.combobox.QueryInputVO", inputVO)
//			.then(
//			function(response) {
//				console.log('response result='+JSON.stringify(response[0].body.result));
//				mappingObj = response[0].body.result;
//				projInfoService.mappingSet[mapping] = mappingObj;
//				console.log('add mappingSet=' + JSON.stringify(mappingSet));
//				deferred.resolve();
//				isLock = false;
//			},
//			function(err) {
//				deferred.reject(err);
//				isLock = false;
//			});
//			deferred.promise;
//		}
		var newString = input;
		var multipleString = '';
		var nowStr = '';
		if(type != 'single') {		
			if(null === input || '' === input || undefined === input) {
				return newString;
			}
			var multipleItem = input.split(splitCode);
			
			angular.forEach(multipleItem, function(item) {
				nowStr = '';
				angular.forEach(mappingObj, function(obj) {
			    	if (obj.DATA == item) {
			        	newString = obj.LABEL;
			        	switch(style) {
							case "F1":
								nowStr = obj.DATA + '-' + obj.LABEL;
								break;
							case "F2":
								nowStr = obj.DATA;
								break;
							case "F3":
							default:
								nowStr = obj.LABEL;
								break;
						}
			        }
			    });
		    	if(nowStr === '') {
		    		nowStr = item;
		    	}
		    	multipleString = multipleString + nowStr + splitCode;
			});
			return multipleString.substring(0,multipleString.length -1);
		} else {
			angular.forEach(mappingObj, function(obj) {
		    	if (obj.DATA == input) {
		        	newString = obj.LABEL;
		        	switch(style) {
						case "F1":
							newString = obj.DATA + '-' + obj.LABEL;
							break;
						case "F2":
							newString = obj.DATA;
							break;
						case "F3":
						default:
							newString = obj.LABEL;
							break;
					}
		        }
			    return input;
		    });
		}		  
	 return newString;
	};
}]);