/**================================================================================================
 @program: CMMGR017_Upload.js
 @author Eli
 @version: 1.0.20181105
 =================================================================================================*/
'use strict';
eSoafApp.controller('CMMGR017_Upload_Controller',
    function ($scope, $controller) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR017_Upload_Controller";

        /** 初始分頁資訊 **/
        $scope.inquireInit = function () {
            $scope.inputVO = {
                sql: ''
            };

            $scope.msgList = '';
        }
        $scope.inquireInit();


        /** 執行上傳的sql **/
        $scope.execute = function () {
            $scope.sendRecv('CMMGR017', 'executeSql', 'com.systex.jbranch.app.server.fps.cmmgr017.CMMGR017InputVO',
                {sql: $scope.inputVO.sql}, function (tota, isError) {
                    if (!isError) {
                        $scope.msgList = tota[0].body.msg;
                    } else {
                        $scope.showMsg('ehl_01_common_024');
                    }
                });
        }

        /** 滿足偵測條件後 執行SQL**/
        $scope.keyUpExe = function ($event) {
            if ($event.keyCode == 17) {
                $scope.timeCtrl = $event.timeStamp;
            } else if ($event.keyCode == 13
                && $scope.timeCtrl
                && ($event.timeStamp - $scope.timeCtrl) < 50) {
                $scope.execute();
            }
        }
    }
);
