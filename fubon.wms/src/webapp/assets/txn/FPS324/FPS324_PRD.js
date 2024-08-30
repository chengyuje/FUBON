/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
// reference: PRD110
'use strict';
eSoafApp.controller('FPS324PRDController',
    function ($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $confirm, sysInfoService, getParameter, $filter) {
        $controller('BaseController', {
            $scope: $scope
        });
        $scope.controllerName = "FPS324PRDController";

        var prd = {};

        //init
        $scope.init = function () {
            $scope.inputVO = {
                priID: '', // 商品代碼
                prdCName: '', // 商品名稱
                pType: '', // 商品分類
                isPrimarySale: '', // 主推
                isBestChoice: '', // 精選
                isAvailable: 'Y', // 可銷售
                isCamProd: '', // 行銷活動商品
                isRetired: '', // 退休規劃
                isEducation: '', // 子女教育
                isBuyHouse: '', // 購屋計劃
                isSavings: '', // 穩定儲蓄
                isDividend: '', // 固定配息
                status: 'NO_REVIEW', // 狀態
                currencyType: '', // 幣別
                riskType: '', // 風險等級
                mfMktCat: '' // 市場分類
            };

        };

        $scope.inquireInit = function () {
            $scope.paramList = [];
        };

        //query btn
        $scope.query = function () {
            console.log($scope.inputVO);
            $scope.paramList = [];
            $scope.inputVO.priID = $scope.inputVO.priID.toUpperCase();
            $scope.sendRecv("FPS324", "queryPrd", "com.systex.jbranch.app.server.fps.prd110.PRD110InputVO", $scope.inputVO,
                function (totas, isError) {
                    if (!isError) {
                        if (totas[0].body.resultList.length > 0) {
                            $scope.paramList = totas[0].body.resultList;
                            $scope.outputVO = totas[0].body;
                        }
                        if ($scope.paramList.length == 0) {
                            $scope.showMsg('ehl_01_common_009'); // 查無資料
                            $scope.paramList = [];
                            $scope.outputVO = [];
                        }
                    }
                });
        };


        //關掉Dialog
        $scope.closePop = function (status) {
            var list = [];
            if (status === 'success') {
                list = $scope.paramList.filter(function (item) {
                    return item.isSelected;
                });

                if (!list) {
                    console.log('所選為空');
                    return false;
                }
            }

            $scope.closeThisDialog({
                status: status,
                addList: list || []
            });

        };
        /* main progress */
        $scope.init();
        $scope.inquireInit();

    }
);
