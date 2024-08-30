/**================================================================================================
@program: elemReady.js
@description:
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive( 'elemReady',['$parse', function( $parse ) {
   return {
       restrict: 'A',
       link: function( $scope, elem, attrs ) {    
         var readyFunc = $parse( attrs.onReady );

         elem.ready( $scope.$apply( readyFunc( $scope )));
       }
    }
}]);