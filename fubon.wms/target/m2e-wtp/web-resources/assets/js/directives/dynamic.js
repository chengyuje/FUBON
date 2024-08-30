/**================================================================================================
@program: dynamic.js
@author Eli
@description: dynamic inject angular element to html
@version: 1.0.20180831
=================================================================================================*/
eSoafApp.directive('dynamic', ['$compile', function($compile) {
    return {
        scope: true,
        restrict: 'E',
        link: function($scope, $element, $attr) {
            var el;
            $attr.$observe('template', function (tpl) {
                el = $compile(tpl || '')($scope);
                $element.html("");
                $element.append(el);
            });
        }
    };
}]);