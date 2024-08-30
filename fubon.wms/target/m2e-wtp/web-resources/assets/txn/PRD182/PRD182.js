'use strict';
eSoafApp.controller('PRD182Controller', function($rootScope, $scope, $controller, getParameter, sysInfoService) {
    $controller('BaseController', {$scope: $scope});
    $scope.controllerName = "PRD182Controller";

    const initialJsbEntrySite = function () {
    	$scope.empId = sysInfoService.getUserID();

        getParameter.XML(['JSB.SITE_URL'], function(totas) {
            if(len(totas)>0){
                const sites = totas.data[totas.key.indexOf('JSB.SITE_URL')];
                const map = {};
                sites.forEach(s => map[s.DATA] = s.LABEL);

                const jsbForms = document.getElementsByTagName('form');
                for (let each of jsbForms)
                    each.action = map[each.id];

            } else {
                $scope.showErrorMsg(`入口網站參數載入有誤，請重新整理。`);
            }
        });
    }
    initialJsbEntrySite();

});
