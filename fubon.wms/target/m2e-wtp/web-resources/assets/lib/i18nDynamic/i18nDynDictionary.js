"use strict";
  	
var i18nModule = angular.module('i18nDynamic');

i18nModule.factory("i18nDictionary", ['$rootScope', '$log', 'socketService', 'projInfoService', '$q', function($rootScope, $log, socketService, projInfoService, $q){

	/**
	 * Constructor
	 */
	function i18nDictionaryService () {
	};


	// This is the method to override to load/fetch your own dictionary
	// The only requirement your language JSON files in your approot/i18n/, named as the language you are targeting, ie
	i18nDictionaryService.prototype.loadDictionary = function(language){
		var keys = {}; // default value
		return loadRemoteQuery(language, keys);
	};
	
	function loadRemoteQuery(language, keys){
		var deferred = $q.defer();
		
// 若更換語系必須重新載入
//		if (!projInfoService.getI18N()) {
			socketService.sendRecv("I18N", "inquireAll", "com.systex.jbranch.app.server.fps.i18n.I18NInputVO", {'locale': language})
			.then(
			function(data) {
				angular.forEach(data[0].body.resultList, function(row, index, objs){
					keys[row.CODE] = row.TEXT;
				});
				projInfoService.setI18N(keys);
				deferred.resolve(keys);
			},
			function(err) {
				$rootScope.showErrorMsg(err);
				deferred.reject(keys);
			});
			return deferred.promise;
//		}
//		deferred.resolve(projInfoService.getI18N());
//		return deferred.promise;
	}

	return new i18nDictionaryService();
}]);