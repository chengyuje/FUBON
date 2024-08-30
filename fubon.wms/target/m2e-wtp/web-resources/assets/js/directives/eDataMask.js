/**================================================================================================
@program: eDataMask.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive('eDataMask', function() {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function(scope, element, attrs, modelCtrl) {
            ///======================
            /// 只在 Binding 時使用
            ///======================
            modelCtrl.$render = function() {
                console.log("$render");
                element.val(dataMaskFormat(modelCtrl.$modelValue));
            };            
        }
    };
});
