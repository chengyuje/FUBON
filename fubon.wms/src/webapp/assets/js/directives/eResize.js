/**================================================================================================
@program: eResize.js
@description:
@version: 1.0.20170414
=================================================================================================*/
eSoafApp.directive('resize',['$window', function ($window) {
    return function (scope, element, attr) {
        var w = angular.element($window);
        scope.$watch(function () {
            return {
                'h': window.innerHeight, 
                'w': window.innerWidth
            };
        }, function (newValue, oldValue) {
            console.log(newValue, oldValue);
            scope.windowHeight = newValue.h;
            scope.windowWidth = newValue.w;

            scope.resizeWithOffset = function(offsetH) {
                //通知
                scope.$eval(attr.notifier);
                return { 
                    'height': (newValue.h - offsetH) + 'px'
                };
            };
        }, true);

        w.bind('resize', function () {
            scope.$apply();
        });
    }
}]);