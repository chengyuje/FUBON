/**================================================================================================
@program: autofocus.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive('autofocus', ['$timeout', function($timeout) {
  return {
    restrict: 'A',
    link : function($scope, $element) {
      $timeout(function() {
        $element[0].focus();
      });
    }
  }
}]);