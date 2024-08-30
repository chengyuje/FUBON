/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS324SUGController',
    function ($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $confirm, sysInfoService, getParameter, $filter) {
        $controller('BaseController', {
            $scope: $scope
        });
        $scope.controllerName = "FPS324SUGController";

        //init
        $scope.init = function () {
            $scope.inputVO = {

            };
        };

        //query btn
        $scope.query = function () {
            console.log($scope.inputVO);
            $scope.paramList = [];

            $scope.sendRecv("FPS324", "sugQuery", "com.systex.jbranch.app.server.fps.fps324.FPS324InputVO", {
                    riskType: $scope.riskAttr
                },
                function (tota, isError) {
                    if (!isError) {
                        $scope.paramList = tota[0].body.outputList;
                        //                    var firstINV = fps324.recommendations.ONETIME;
                        $scope.isDate = tota[0].body.isData;
                        $scope.rate = tota[0].body.rate;
                        // calculate
                        $scope.paramList.forEach(function (row) {
                            console.log(row);
                            row.SUGG_PERCENT = row.INV_PERCENT;
                            //調整後約當台幣
                            row.RAW_NTD = $scope.firstINV * 1000 * row.INV_PERCENT / 100;
                            //原幣金額
                            if (row.CURRENCY_TYPE === 'NTD') {
                                row.STORE_RAW = row.RAW_NTD;
                                row.cmbCur = '2';
                            } else {
                                row.STORE_RAW = row.RAW_NTD * $scope.rate;
                                row.cmbCur = '1';
                            }
                        });
                        return true;
                    }
                });
        };

        //刪除row
        $scope.deleteRow = function (index) {
            //splice刪除該筆從第0個位子開始1個位子結束
            $scope.paramList.splice(index, 1);
        };


        //關掉Dialog
        $scope.closePop = function (status) {
            $scope.closeThisDialog({
                status: status,
                addList: $scope.paramList || []
            });
        };

        $scope.query();

    });
