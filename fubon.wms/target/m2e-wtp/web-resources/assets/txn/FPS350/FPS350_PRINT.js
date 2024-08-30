/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS350PrintController',
    function ($rootScope, $scope, $controller) {
        $controller('BaseController', {
            $scope: $scope
        });
        $scope.controllerName = "FPS350PrintController";

        var log = console.log;
        var init = function () {
            $scope.paramList = [];
        };

        log($scope.mapping);
        log($scope.plan);

        var inquire = function () {
            $scope.sendRecv('FPS350', 'inquirePrint', 'com.systex.jbranch.app.server.fps.fps350.FPS350InputVO', $scope.inputVO,
                function (tota, isError) {
                    if (!isError) {
                        log(tota[0].body.outputList);
                        $scope.paramList = tota[0].body.outputList;
                        $scope.checker = tota[0].body.checker;
                        return true;
                    }
                    $scope.showErrorMsg(tota);
                    return false;
                });
        };

        $scope.print = function (row) {
            var inputVO = {
                SEQNO: row.SEQNO
            };
            $scope.sendRecv('FPS350', 'print', 'com.systex.jbranch.app.server.fps.fps350.FPS350InputVO', inputVO,
                function (tota, isError) {
                    if (!isError) {}
                    $scope.showErrorMsg(tota);
                    return false;
                });
        };

        $scope.addShelf = function (row) {
            var inputVO = {
                SEQNO: row.SEQNO
            };
            $scope.sendRecv('FPS350', 'addShelf', 'com.systex.jbranch.app.server.fps.fps350.FPS350InputVO', inputVO,
                function (tota, isError) {
                    if (!isError) {
                        $scope.showSuccessMsg('ehl_01_common_023');//執行成功
                        return true;
                    }
                    $scope.showErrorMsg(tota);
                    return false;
                });
        };

        // main process
        init();
        inquire();
    });
