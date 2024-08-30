/**================================================================================================
 @Description 電文查詢功能
 =================================================================================================*/
'use strict';
eSoafApp.controller('CMMGR022_Controller',
    ($scope, $controller, socketService, ngDialog, getParameter) => {
        $controller('BaseController', {$scope: $scope});
        /** 載入 CommonUtil **/
        $controller('CommonUtil', {$scope: $scope});
        $scope.controllerName = "CMMGR022_Controller";
        $scope.modelName = 'com.systex.jbranch.app.server.fps.cmmgr022.CMMGR022InputVO';

        /** 查詢電文資訊（上下文除外) **/
        $scope.query = () => {
            $scope.timeFormat();

            $scope.sendRecv("CMMGR022", "query", $scope.modelName, $scope.inputVO,
                (tota, isError) => {
                    if (!isError) {
                        $scope.data = tota[0].body.txResult;
                        $scope.outputVO = tota[0].body;

                        if (!$scope.data.length)
                            $scope.showMsg('ehl_01_common_009');
                    }
                });
        }

        /** 時間格式化 （起始時間,結束時間）**/
        $scope.timeFormat = (sDay = $scope.inputVO.start,
                             eDay = $scope.inputVO.end,
                             sTime = $scope.inputVO.startTime,
                             eTime = $scope.inputVO.endTime) => {
            if (sDay)
                if (sTime) sDay.setHours(sTime.getHours(), sTime.getMinutes());
                /** 沒有設置起始時間，起始日期視為當日的 00:00:00:000 開始 **/
                else sDay.setHours(0, 0);
            if (eDay) {
                if (eTime) eDay.setHours(eTime.getHours(), eTime.getMinutes());
                /** 沒有設置結束時間，結束日期視為到當日的 23:59:59:999 **/
                else eDay.setHours(23, 59, 59, 999);
            }
        }

        /** 查詢電文內容 **/
        $scope.openDetail = row => {
            ngDialog.open({
                template: 'assets/txn/CMMGR022/CMMGR022_DETAIL.html',
                className: 'CMMGR022_DETAIL',
                controller: ['$scope', function ($scope) {
                    $scope.vo = angular.copy(row);
                }]
            });
        }

        /** 配置電文物件 **/
        $scope.configureTx = () => {
            // 電文物件相關配置
            $scope.txList = [{
                type: 'CBS',
                data: angular.copy($scope.mappingSet['CBS'])
            }, {
                type: 'ESB',
                data: angular.copy($scope.mappingSet['ESB'])
            }];
        }

        /** 電文一覽 Filter **/
        $scope.filterTx = () => {
            if ($scope.inputVO.id)
                $scope.txList
                    .forEach(tx => tx.data = $scope.mappingSet[tx.type]
                            .filter(each => each.DATA.contains($scope.inputVO.id) ||
                                each.LABEL.contains($scope.inputVO.id)));
            else $scope.configureTx();
        }

        /** 載入參數 **/
        $scope.loadParameter = () => {
            /** XML參數 **/
            getParameter.XML(['TX.CBS_LIST', 'TX.ESB_LIST'], (totas) => {
                if (totas) {
                    $scope.mappingSet['CBS'] = totas.data[totas.key.indexOf('TX.CBS_LIST')];
                    $scope.mappingSet['ESB'] = totas.data[totas.key.indexOf('TX.ESB_LIST')];
                    $scope.configureTx();
                }
            });
        }

        /** 初始化 **/
        $scope.init = () => {
            // 初始日曆
            $scope.tx = {};
            $scope.initDateOptions($scope.tx);

            $scope.loadParameter();

            $scope.data = [];
            $scope.subData = [];
            $scope.outputVO = {};
            $scope.inputVO = {};
        }
        $scope.init();
    }
);
