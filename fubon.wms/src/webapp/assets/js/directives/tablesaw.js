/**================================================================================================
@program: tablesaw.js
@description:
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive('ngTablesaw',['$rootScope','projInfoService', function($rootScope, projInfoService) {
    return {
		restrict: 'A',
		link: function(scope, elm, attrs) {
			if (scope.$last == true) {
				$rootScope.etablesaw();
            }
		}
	};
}]);