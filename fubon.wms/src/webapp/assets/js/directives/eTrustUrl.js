/**================================================================================================
@program: eTrustUrl.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.filter('trustAsResourceUrl', ['$sce', function($sce) {
    return function(val) {
        return $sce.trustAsResourceUrl(val);
    };
}]);