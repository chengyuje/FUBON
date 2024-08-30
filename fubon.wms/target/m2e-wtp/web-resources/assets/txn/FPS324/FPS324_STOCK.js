/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS324_STOCKController', function ($scope, $controller, socketService, alerts, projInfoService, $q, getParameter) {
    $controller('BaseController', {
        $scope: $scope
    });
    $scope.controllerName = "FPS324_STOCKController";

    // init 
    $scope.init = function () {
        $scope.inputVO = {
            custId: $scope.custId
        };
    }

    //帳號下拉選單
    $scope.getHeadRoleList = function () {
        $scope.sendRecv("FPS324", "getAccount", "com.systex.jbranch.app.server.fps.fps324.FPS324InputVO",
            $scope.inputVO,
            function (tota, isError) {
                if (!isError) {
                    $scope.accountList = [];
                    angular.forEach(tota[0].body.outputList, function (row, index, objs) {
                        $scope.accountList.push({
                            LABEL: row.ACC_NO,
                            DATA: row.ACC_NO
                        });
                    });
                    return;
                }
            });
    };

    //查詢btn
    $scope.inquire = function () {
        $scope.inputVO.custId = $scope.inputVO.account;
        $scope.sendRecv("FPS324", "inquireStock", "com.systex.jbranch.app.server.fps.fps324.FPS324InputVO",
            $scope.inputVO,
            function (tota, isError) {
                if (!isError) {
                    //分頁改寫
                    $scope.outputVO = tota[0].body;
                    console.log(tota[0].body);
                    //TABLE內容
                    $scope.paramList = tota[0].body.outputList;
                    $scope.paramList.forEach(function (row) {
                        row.BUY_DATE = trunc(row.BUY_DATE);
                        row.NAV_DATE = trunc(row.NAV_DATE);
                    });
                    if (!$scope.paramList) {
                        $scope.showMsg('ehl_01_common_009');
                    }
                } else {
                    $scope.showErrorMsg('ehl_01_common_022');
                }
            });
    };

    //加入btn
    $scope.add = function () {
        var checkList = $scope.paramList.filter(function (row) {
            return row.isChecked;
        });
        if (checkList.length <= 0) {
            $scope.showMsg('請至少選取一筆資料');
        } else {
            $scope.closePop(checkList);
        }
    };

    //關掉Dialog
    $scope.closePop = function (list) {
        $scope.closeThisDialog({
            status: 'success',
            addList: list
        });
    };

    var trunc = function (a) {
        return a.split(' ')[0];
    };

    //back FPS324
    $scope.backFPS324 = function () {
        $scope.closeThisDialog($scope.inputVO.total);
    };

    //載入controller時所要執行動作
    $scope.init();
    $scope.getHeadRoleList();
});
