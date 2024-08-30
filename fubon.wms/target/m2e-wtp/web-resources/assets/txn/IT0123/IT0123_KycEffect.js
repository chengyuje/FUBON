/**================================================================================================
 @program: IT0123_KycEffect.js
 @author Eli
 @version: 20181213 初版
 =================================================================================================*/
'use strict';
eSoafApp.controller('IT0123_KycEffect_Controller', ($scope, $controller) => {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "IT0123_KycEffect_Controller";

        /** 初始化 **/
        $scope.inputVO = {};
        $scope.init = () => {
            $scope.result = [];
        }
        $scope.init();

        /** 如果客戶ID輸入欄為空，則初始化 **/
        $scope.listenKycInput = () => {
            if (!$scope.inputVO.custId) $scope.init();
        }

        /** 執行生效KYC **/
        $scope.execute = () => {
            $scope.result.map((sub) =>
                $scope.sendRecv('IT0123', 'execute', 'com.systex.jbranch.app.server.fps.it0123.IT0123InputVO',
                    {custId: sub.custId}, (tota, isError) => {
                        if (!isError) {
                            sub.msg = tota[0].body;
                        } else {
                            $scope.showErrorMsg('ehl_01_common_024');
                        }
                    })
            );
        }

        /** 滿足偵測條件後 執行生效KYC**/
        $scope.keyUpExe = ($event) => {
            if (!$scope.inputVO.custId) return;

            $scope.init();
            /** 分割客戶ID，並填入結果陣列 **/
            $scope.inputVO.custId.split(',')
                .filter((subId) => subId)
                .map((subId) => $scope.result.push({custId: subId.trim(), msg: '等待執行...'}));

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
