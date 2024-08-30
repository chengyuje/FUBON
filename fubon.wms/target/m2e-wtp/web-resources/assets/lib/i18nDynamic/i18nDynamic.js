 "use strict";
  	
var i18nModule = angular.module('i18nDynamic', ['ngLocale']);

i18nModule.factory("i18nLanguage", ['$rootScope','$locale', '$log', 'i18nDictionary', '$window', function($rootScope, $locale, $log, i18nDictionary, $window){

	var that = this;
	that.language = $window.navigator.language || $window.navigator.userLanguage;
	that.dictionary = {};

	// Come from angularJS localization files
	// Add language that you need for your application
	that.angularLocale = {
						  'en' : {"DATETIME_FORMATS":{"MONTH":["January","February","March","April","May","June","July","August","September","October","November","December"],"SHORTMONTH":["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"],"DAY":["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"],"SHORTDAY":["Sun","Mon","Tue","Wed","Thu","Fri","Sat"],"AMPMS":["AM","PM"],"medium":"MMM d, y h:mm:ss a","short":"M/d/yy h:mm a","fullDate":"EEEE, MMMM d, y","longDate":"MMMM d, y","mediumDate":"MMM d, y","shortDate":"M/d/yy","mediumTime":"h:mm:ss a","shortTime":"h:mm a"},"NUMBER_FORMATS":{"DECIMAL_SEP":".","GROUP_SEP":",","PATTERNS":[{"minInt":1,"minFrac":0,"macFrac":0,"posPre":"","posSuf":"","negPre":"-","negSuf":"","gSize":3,"lgSize":3,"maxFrac":3},{"minInt":1,"minFrac":2,"macFrac":0,"posPre":"\u00A4","posSuf":"","negPre":"(\u00A4","negSuf":")","gSize":3,"lgSize":3,"maxFrac":2}],"CURRENCY_SYM":"$"},"pluralCat":function (n) {  if (n == 1) {    return PLURAL_CATEGORY.ONE;  }  return PLURAL_CATEGORY.OTHER;},"id":"en"},
						  'zh-tw' : {
							  "DATETIME_FORMATS": {
								    "AMPMS": [
								      "\u4e0a\u5348",
								      "\u4e0b\u5348"
								    ],
								    "DAY": [
								      "\u661f\u671f\u65e5",
								      "\u661f\u671f\u4e00",
								      "\u661f\u671f\u4e8c",
								      "\u661f\u671f\u4e09",
								      "\u661f\u671f\u56db",
								      "\u661f\u671f\u4e94",
								      "\u661f\u671f\u516d"
								    ],
								    "ERANAMES": [
								      "\u897f\u5143\u524d",
								      "\u897f\u5143"
								    ],
								    "ERAS": [
								      "\u897f\u5143\u524d",
								      "\u897f\u5143"
								    ],
								    "FIRSTDAYOFWEEK": 6,
								    "MONTH": [
								      "1\u6708",
								      "2\u6708",
								      "3\u6708",
								      "4\u6708",
								      "5\u6708",
								      "6\u6708",
								      "7\u6708",
								      "8\u6708",
								      "9\u6708",
								      "10\u6708",
								      "11\u6708",
								      "12\u6708"
								    ],
								    "SHORTDAY": [
								      "\u9031\u65e5",
								      "\u9031\u4e00",
								      "\u9031\u4e8c",
								      "\u9031\u4e09",
								      "\u9031\u56db",
								      "\u9031\u4e94",
								      "\u9031\u516d"
								    ],
								    "SHORTMONTH": [
								      "1\u6708",
								      "2\u6708",
								      "3\u6708",
								      "4\u6708",
								      "5\u6708",
								      "6\u6708",
								      "7\u6708",
								      "8\u6708",
								      "9\u6708",
								      "10\u6708",
								      "11\u6708",
								      "12\u6708"
								    ],
								    "WEEKENDRANGE": [
								      5,
								      6
								    ],
								    "fullDate": "y\u5e74M\u6708d\u65e5 EEEE",
								    "longDate": "y\u5e74M\u6708d\u65e5",
								    "medium": "y\u5e74M\u6708d\u65e5 ah:mm:ss",
								    "mediumDate": "y\u5e74M\u6708d\u65e5",
								    "mediumTime": "ah:mm:ss",
								    "short": "y/M/d ah:mm",
								    "shortDate": "y/M/d",
								    "shortTime": "ah:mm"
								  },
								  "NUMBER_FORMATS": {
								    "CURRENCY_SYM": "NT$",
								    "DECIMAL_SEP": ".",
								    "GROUP_SEP": ",",
								    "PATTERNS": [
								      {
								        "gSize": 3,
								        "lgSize": 3,
								        "maxFrac": 3,
								        "minFrac": 0,
								        "minInt": 1,
								        "negPre": "-",
								        "negSuf": "",
								        "posPre": "",
								        "posSuf": ""
								      },
								      {
								        "gSize": 3,
								        "lgSize": 3,
								        "maxFrac": 2,
								        "minFrac": 2,
								        "minInt": 1,
								        "negPre": "-\u00a4",
								        "negSuf": "",
								        "posPre": "\u00a4",
								        "posSuf": ""
								      }
								    ]
								  },
								  "id": "zh-tw",
								  "pluralCat": function(n, opt_precision) {  return PLURAL_CATEGORY.OTHER;}
								}
						};

	/**
	 * Constructor
	 */
	function i18nLanguageService () {
		this.setLocale(that.language.toLocaleLowerCase());
	}


	// Replace the format content with the new local selected
	i18nLanguageService.prototype.loadAngularLocale = function(language){
        $locale.DATETIME_FORMATS = that.angularLocale[language].DATETIME_FORMATS;
        $locale.NUMBER_FORMATS = that.angularLocale[language].NUMBER_FORMATS;
        $log.info('language='+language + ', $locale='+JSON.stringify($locale));
        $locale.id = language;
	}

	i18nLanguageService.prototype.getTranslation = function(msgKey, locale){
			return that.dictionary[msgKey];
	};

	i18nLanguageService.prototype.setLocale = function(newLocale){

		var parent_that = this;
		if(newLocale){
            $log.info('Language modification');
            
            var promise = i18nDictionary.loadDictionary(newLocale);
            promise.then(function(data) {
            	loadLocale(parent_that, that, data, newLocale);
    		},
    		function(data){
    			loadLocale(parent_that, that, data, newLocale);
    		});
		}	
	};
	
	function loadLocale(parent_that, that, data, newLocale){
		that.dictionary = data;
    	
    	if(that.dictionary){
    		parent_that.loadAngularLocale(newLocale);	
        }else{
        	$log.warn('No dictionary for locale ' + newLocale);	

        	if(newLocale.lastIndexOf("-") > 0){
				newLocale = newLocale.slice(0, newLocale.lastIndexOf("-"));
				if(newLocale){
					parent_that.setLocale(newLocale);
				}
			}
        }
	}

	return new i18nLanguageService();	
}]);



i18nModule.filter("i18n",  ['$locale', 'i18nLanguage', '$log', function($locale, i18nLanguage, $log){

    var filter = function (msgKey, parameterArray, defaultValue) {
        var content = i18nLanguage.getTranslation(msgKey, $locale);
        if(content &&　parameterArray){
        	for(var i = 0; i < parameterArray.length; i++){
        		if(parameterArray[i]){
        			content = content.replace('{' + i + '}', parameterArray[i]);
        		}
        	}
        	return content;
        }
        
        if(content){
        	return content;
        }
        
        if(defaultValue){
        	return defaultValue;
        }
        
        return msgKey;
    };
    
    filter.$stateful = true; // <-- the magic line
    
    return filter;
}]);





