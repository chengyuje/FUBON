/**================================================================================================
@program: eNumberRange.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive('eNumberRange', function() {
	return {
		restrict: 'A',
		require: 'ngModel',
        scope:{},
		link: function(scope, element, attrs, ngModel) {

			//check out
			if(attrs.type !== 'number')return;
			if(!attrs.min && !attrs.max)return;
			
			//setting
			var min,max;
			attrs.$observe('min', function(value){min=Number(value);});
			attrs.$observe('max', function(value){max=Number(value);});
			
			//dynamic checking
			$(element).change(function() {
				if(Number(element.val())>max){
					ngModel.$setViewValue(max);
			        ngModel.$render();
				}
				if(Number(element.val())<min){
					ngModel.$setViewValue(min);
			        ngModel.$render();
				}
			});
			
		}
	};
});