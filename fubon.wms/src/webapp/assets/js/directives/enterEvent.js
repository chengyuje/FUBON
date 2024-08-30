/**================================================================================================
@program: enterEvent.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive('ngEnter', function () {
    return function (scope, element, attrs) {
        element.on("keypress", function (event) {
            if(event.which === 13) {
                scope.$apply(function (){
                    scope.$eval(attrs.ngEnter);
                });

                event.preventDefault();
            }
            console.log('keypress');
        });
    };
});