/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD170Controller',
    function($rootScope, $scope, $controller, sysInfoService, getParameter) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "PRD170Controller";

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

        // init
        $scope.init = function() {
            $scope.type = null;
            $scope.page = null;
            $scope.InsType = null;
            $scope.includePage = null;
            $scope.includePage2= null;

            initialJsbEntrySite();
        };
        $scope.init();

        $scope.changeTag = function(type) {
            $scope.type = type;
            $scope.page = null;
            $scope.includePage = null;
            $scope.includePage2= null;
        }

        $scope.changePage = function(page, page2) {
            // 不想另開資料夾
            if(!page2)
                page2 = page;
            $scope.page = page;
            $scope.includePage = "assets/txn/" + page2 + "/" + page + ".html?forIncludeTime=" + Date.now();
        };

        $scope.changeINSPage = function(page , InsType) {
            $scope.page = page;
            $scope.InsType = InsType;
            $scope.connector('set' , 'INS910_INSTYPE' , InsType);
            $scope.includePage = "assets/txn/" + page + "/" + page + ".html?forIncludeTime=" + Date.now();
        }

        // 2017/6/15 follow prd230
        if($scope.connector('get','MAO151_PARAMS')!=undefined ) {
            switch($scope.connector('get','MAO151_PARAMS').PAGE) {
                case 'HOME':
                    $scope.changeTag('5');
                    $scope.changePage('PRD210');
                    break;
            }
        }



});
