/**================================================================================================
@program: eNegative.js
@description: Negative value style control.
@version: 1.0.20170822
=================================================================================================*/
eSoafApp.directive('eNeg', ["$timeout", function($timeout) {
	return {
		restrict: 'EAC',
		scope: {
			type: "@?eNeg"
		},
		link: function(scope, element, attrs) {			
			
			/** API **/
			//processing
			scope.process = function(e){
				var $txt = (e?e.text():false) || element.text();
				if(angular.isDefined($txt) && $txt.indexOf("-")>-1){
            		(e?e:element).addClass("e-str-negative");
            	}else{
            		if((e?e:element).hasClass("e-str-negative"))(e?e:element).removeClass("e-str-negative");
            	};					
			}
			//listening
			scope.listen = function(e){
				(e?e:element).bind('DOMSubtreeModified', function(event) {
					scope.process(e?e:element);
	            });
			}
			
			/** type: [single] **/
			if(angular.isUndefined(scope.type) || scope.type.indexOf("single")>-1){				
				//initialize
				scope.init = function(){
					$timeout(function(){
						if(angular.isDefined(element.text()) && element.text().indexOf("{{")>-1 && element.text().indexOf("}}")>-1)return scope.init();
						scope.process();
					});
				}
				scope.init();				
				//new listen
				var singleListen = angular.copy(scope.listen());
			}
			
			/** type: [table-data] **/
			if(angular.isDefined(scope.type) && scope.type.indexOf("data")>-1 && element.is("table")){
				$timeout(function(){
					element.find("tbody tr td").each(function(){
						scope.process($(this));
						//new listen
						var dataListen = angular.copy(scope.listen($(this)));
	                });
				});
			}
			
			/** type: [table-panel] **/
			if(angular.isDefined(scope.type) && scope.type.indexOf("panel")>-1 && element.is("table")){
				
			}
			
		}
	};
}]);