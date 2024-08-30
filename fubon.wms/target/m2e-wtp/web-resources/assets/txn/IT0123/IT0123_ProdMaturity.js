/**================================================================================================
 @program: IT0123_ProdMaturity.js
 @author Eli
 @version: 20181220 初版
 =================================================================================================*/
'use strict';
eSoafApp.controller('IT0123_ProdMaturity_Controller', ($scope, $controller) => {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "IT0123_ProdMaturity_Controller";

        /** 初始化 **/
        $scope.inputVO = {};
        $scope.init = () => {
            $scope.result = [];
        }
        $scope.init();

        /** 如果商品ID輸入欄為空，則初始化 **/
        $scope.listenProdInput = () => {
            if (!$scope.inputVO.prdId) $scope.init();
        }

        /** 執行商品募集期展延 **/
        $scope.execute = () => {
            $scope.result.map((sub) =>
                $scope.sendRecv('IT0123', 'changeMaturity', 'com.systex.jbranch.app.server.fps.it0123.IT0123InputVO',
                    {prdId: sub.prdId}, (tota, isError) => {
                        if (!isError) {
                            sub.msg = tota[0].body;
                        } else {
                            $scope.showErrorMsg('ehl_01_common_024');
                        }
                    })
            );
        }

        /** 滿足偵測條件後 執行商品募集期展延**/
        $scope.keyUpExe = ($event) => {
            if (!$scope.inputVO.prdId) return;

            $scope.init()
            /** 分割商品ID，並填入結果陣列 **/
            $scope.inputVO.prdId.split(',')
                .filter((subId) => subId)
                .map((subId) => $scope.result.push({prdId: subId, msg: '等待執行...'}));

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
