/**================================================================================================
@program: whenReady.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
/*
 * The whenReady directive allows you to execute the content of a when-ready
 * attribute after the element is ready (i.e. done loading all sub directives and DOM
 * content except for things that load asynchronously like partials and images).
 *
 * Execute multiple expressions by delimiting them with a semi-colon. If there
 * is more than one expression, and the last expression evaluates to true, then
 * all expressions prior will be evaluated after all text nodes in the element
 * have been interpolated (i.e. {{placeholders}} replaced with actual values). 
 *
 * Caveats: if other directives exists on the same element as this directive
 * and destroy the element thus preventing other directives from loading, using
 * this directive won't work. The optimal way to use this is to put this
 * directive on an outer element.
 */
eSoafApp.directive('ngElementReady', [function() {
        return {
            priority: Number.MIN_SAFE_INTEGER, // execute last, after all other directives if any.
            restrict: "A",
            link: function($scope, $element, $attributes) {
              $('.slim-scroll').each(function () {
                var $this = $(this);
                console.log('element='+$this);
                $this.slimScroll({
                    height: $this.data('height') ||  100,
                    railVisible: true
                });
            });
              console.log('ngElementReady');
                $scope.$eval($attributes.ngElementReady); // execute the expression in the attribute.
            }
        };
    }]);