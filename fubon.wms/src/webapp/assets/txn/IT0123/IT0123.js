/**================================================================================================
 @program: IT0123.js
 @author Eli
 @version: 20181213 初版
 =================================================================================================*/
'use strict';
eSoafApp.controller('IT0123_Controller', ($scope, $controller) => {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "IT0123_Controller";
    }
);
