/**================================================================================================
 @program: IT0123_Recording.js
 @author Eli
 @version: 20181220 初版
 @version: 20190122 商品類別改為ComboBox
 =================================================================================================*/
'use strict';
eSoafApp.controller('IT0123_Recording_Controller', ($scope, $controller) => {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "IT0123_Recording_Controller";

        /** 初始化 **/
        $scope.inputVO = {};
        $scope.PRD_TYPE = [
            {LABEL: 'FUND', DATA: 'FUND'},
            {LABEL: 'STOCK', DATA: 'STOCK'},
            {LABEL: 'BOND', DATA: 'BOND'},
            {LABEL: 'SI', DATA: 'SI'},
            {LABEL: 'SN', DATA: 'SN'},
            {LABEL: 'SD', DATA: 'SD'},
            {LABEL: 'DCI', DATA: 'DCI'}
        ];

        $scope.init = () => {
            $scope.msg = '';
        }
        $scope.init();

        /** 執行新增錄音序號 **/
        $scope.execute = () => {
            $scope.sendRecv('IT0123', 'insertRecording', 'com.systex.jbranch.app.server.fps.it0123.IT0123InputVO',
                $scope.inputVO, (tota, isError) => {
                    if (!isError) {
                        $scope.msg += `${tota[0].body}\n`;
                    } else {
                        $scope.showErrorMsg('ehl_01_common_024');
                    }
                });
        }

        /** 當商品種類滿足SI、SD、SN時，必須在輸入商品ID **/
        $scope.whetherShowPrdIdCol = () => {
            if ($scope.inputVO.prdType.match(/^SI$|^SD$|^SN$/)) $scope.inputVO.showPrdIdCol = true;
            else {
                $scope.inputVO.showPrdIdCol = false;
                $scope.inputVO.prdId = '';
            }
        }

        /** 滿足偵測條件後 執行商品募集期展延**/
        $scope.keyUpExe = ($event) => {
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
