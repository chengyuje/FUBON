'use strict';
eSoafApp.controller('IT0123_Other_Controller', ($scope, $controller) => {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "IT0123_Other_Controller";

        $scope.init = () => {
            $scope.workstationInfo = '';
            $scope.inputVO = {};
        }
        $scope.init();

        /** 修改客戶C值 **/
        $scope.printWorkstationInfo = () => {
            $scope.sendRecv('IT0123', 'printWorkstationInfo', 'com.systex.jbranch.app.server.fps.it0123.IT0123InputVO',
                $scope.inputVO, (tota, isError) => {
                    if (!isError) {
                        $scope.workstationInfo = tota[0].body;
                    } else {
                        $scope.showErrorMsg('ehl_01_common_024');
                    }
                });
        }

    }
);
