/**================================================================================================
 @program: CMMGR017_Query.js
 @author Eli
 @version: 1.0.20181105 add package query panel
 =================================================================================================*/
'use strict';
eSoafApp.controller('CMMGR017_Query_Controller',
    function ($scope, $controller) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR017_Query_Controller";

        /** 初始資訊 **/
        $scope.init = function () {
            $scope.inputVO = {
                packageName : ''
            };
            $scope.pckInfo = undefined;
        }
        $scope.init();

        /** 查詢Package資訊 **/
        $scope.qryPck = function () {
            $scope.sendRecv('CMMGR017', 'queryPackageInfo', 'com.systex.jbranch.app.server.fps.cmmgr017.CMMGR017InputVO',
                {pckName: $scope.inputVO.packageName}, (tota, isError) => {
                    if (!isError) {
                        if (tota[0].body.pckInfo.length) {
                            $scope.pckInfo = tota[0].body.pckInfo;
                            $scope.pckInfo.forEach(e => $scope.getBasicInfo(e));
                            $scope.pckInfo.forEach(e => $scope.addDDL(e));
                        } else {
                            $scope.pckInfo = undefined;
                            $scope.showMsg('ehl_01_common_009');
                        }
                    } else {
                        $scope.showErrorMsg('ehl_01_common_024');
                    }
                });
        }

        /**
         * 根據傳入Package物件取得基本資訊
         * @param pck
         * @returns {string}
         */
        $scope.getBasicInfo = pck => {
           pck.basic = [
                {ATTR : 'CREATED', VAL : pck.CREATED},
                {ATTR : 'LAST_DDL_TIME', VAL : pck.LAST_DDL_TIME},
                {ATTR : 'TYPE', VAL : pck.TYPE},
                {ATTR : 'STATUS', VAL : pck.STATUS}];
        }


        /**
         * 將 TEXT 前面加上 DDL 語法
         * @param pck
         */
        $scope.addDDL = pck => {
            pck.TEXT = `CREATE OR REPLACE ${pck.TEXT}`;
        }

        /**
         * 分割錯誤訊息並顯示
         * @param pck
         */
        $scope.showPckErrMsg = function (sub) {
            $scope.errList = sub.ERR_MSG.split("\\n");

            if (!sub.errShow) {
                $scope.pckInfo.forEach((e)=>e.errShow = false);
            }
            sub.errShow = !sub.errShow;
        }

        /**
         * 顯示基本資訊
         */
        $scope.showBasicInfo = function () {
            $scope.basicShow = !$scope.basicShow;
        }


        /** 一鍵複製 **/
        $scope.copyText = copyClassIndex => {
            document.querySelector(`.CMMGR017_QUERY .copyArea${copyClassIndex}`).select();
            try {
                $scope.showMsg(document.execCommand('copy')? '資料已複製到剪貼簿！': '複製失敗！');
            } catch (err) {
                $scope.showErrorMsg('Oops! 複製功能出了點問題，目前無法使用！');
            }
        }
    }
);
