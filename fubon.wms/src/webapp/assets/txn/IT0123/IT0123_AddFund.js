/**================================================================================================
 @program: IT0123_AddFund.js
 @author Eli
 @version: 20190806 初版
 =================================================================================================*/
'use strict';
eSoafApp.controller('IT0123_AddFund_Controller', ($scope, $controller) => {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "IT0123_AddFund_Controller";

        /** 初始化 **/
        $scope.init = () => {
            $scope.inputVO = {};
        }
        $scope.init();

        /** 清除匯入結果 **/
        $scope.clear = () => {
            $scope.valid = undefined;
            $scope.invalid = undefined;
        }

        /** 選擇檔案完畢 **/
        $scope.uploadFinshed = name => $scope.inputVO.fileName = name;

        /** 上傳檔案以新增基金資訊 **/
        $scope.uploadFundInfo = () => {
            $scope.clear();
            $scope.sendRecv('IT0123', 'uploadFundInfo', 'com.systex.jbranch.app.server.fps.it0123.IT0123InputVO',
                $scope.inputVO, (tota, isError) => {
                    if (!isError) {
                        $scope.valid = tota[0].body.VALID;
                        $scope.invalid = tota[0].body.INVALID;
                    }
                });
        }

        /** 下載【上傳檔案已新增基金資訊】範例檔 **/
        $scope.uploadFundInfoExample = () => {
            $scope.sendRecv('IT0123', 'uploadFundInfoExample', 'com.systex.jbranch.app.server.fps.it0123.IT0123InputVO',
                {}, () => {
                });
        }
    }
);
