/**================================================================================================
@program: slimScrollInit.js
@description: JQuery
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive('slimScrollInit', function() {
	return function(scope, element, attrs) {
		$('.slim-scroll').each(function () {
	        var $this = $(this);
	        $this.slimScroll({
	            height: $this.data('height') ||  100,
	            railVisible: true
	        });
	    });
	};
});