/**================================================================================================
 @program: IT0123_ChangeCValue.js
 @author SamTu
 @version: 20200629 初版
 
 =================================================================================================*/
'use strict';
eSoafApp.controller('IT0123_ChangeCValue_Controller', ($scope, $controller) => {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "IT0123_ChangeCValue_Controller";

        /** 初始化 **/
        $scope.inputVO = {};
        $scope.CValue = [
            {LABEL: 'C1', DATA: 'C1'},
            {LABEL: 'C2', DATA: 'C2'},
            {LABEL: 'C3', DATA: 'C3'},
            {LABEL: 'C4', DATA: 'C4'}
        ];

        $scope.init = () => {
            $scope.msg = '';
        }
        $scope.init();

        /** 修改客戶C值 **/
        $scope.execute = () => {
            $scope.sendRecv('IT0123', 'changeCValue', 'com.systex.jbranch.app.server.fps.it0123.IT0123InputVO',
                $scope.inputVO, (tota, isError) => {
                    if (!isError) {
                        $scope.msg = tota[0].body;
                    } else {
                        $scope.showErrorMsg('ehl_01_common_024');
                    }
                });
        }

    }
);
