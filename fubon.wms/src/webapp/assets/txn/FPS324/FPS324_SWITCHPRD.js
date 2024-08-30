/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS324SWITCHPRDController',
    function ($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $confirm, sysInfoService, getParameter, $filter) {
        $controller('BaseController', {
            $scope: $scope
        });
        $scope.controllerName = "FPS324SWITCHPRDController";

        var prd = {};

        //init
        $scope.init = function () {
            $scope.inputVO.isSelected = 0;
        };

        $scope.inquireInit = function () {
            $scope.paramList = [];
        };

        //query btn
        $scope.query = function () {
            console.log($scope.inputVO);
            $scope.paramList = [];

            $scope.sendRecv("FPS324", "switchPRD", "com.systex.jbranch.app.server.fps.fps324.FPS324InputVO",
                $scope.inputVO,
                function (totas, isError) {
                    if (!isError) {
                        if (totas[0].body.outputList.length > 0) {
                            $scope.paramList = totas[0].body.outputList;
                            $scope.outputVO = totas[0].body;
                            $scope.inputVO.isSelected = 0;
                        }
                        if ($scope.paramList.length == 0) {
                            $scope.showMsg('ehl_01_common_009'); // 查無資料
                            $scope.paramList = [];
                            $scope.outputVO = [];
                            $scope.inputVO.isSelected = -1;
                        }
                    }
                });
        };


        //關掉Dialog
        $scope.closePop = function (status) {
            var list = null;
            if (status == 'success' && $scope.inputVO.isSelected >= 0) {
                list = $scope.paramList[$scope.inputVO.isSelected];

                if (!list) {
                    console.log('所選為空');
                    return false;
                }
            }

            $scope.closeThisDialog({
                status: status,
                addList: list || false
            });
        };

        $scope.query();
        $scope.init();
    }
);
