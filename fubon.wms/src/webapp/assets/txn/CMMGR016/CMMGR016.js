/**================================================================================================
 @program: CMMGR016.js
 @author Eli
 @description 報表內容查詢頁面，提供檢索粗略資訊
 @date 20190502 增加驗證檔資訊
 @date 20190704 增加報表 FTP、JOB、MAIL 動作的資訊
 =================================================================================================**/
'use strict';
eSoafApp.controller('CMMGR016_Controller', ($rootScope, $scope, $controller, $confirm, socketService, ngDialog) => {
    $controller('BaseController', {
        $scope: $scope
    });
    $scope.controllerName = "CMMGR016_Controller";
    /** controller mapping model name **/
    const voName = 'com.systex.jbranch.app.server.fps.cmmgr016.CMMGR016InputVO';

    /** 將變數設定成預設值 **/
    $scope.clear = () => {
        $scope.inputVO = {}
        $scope.outputVO = [];
        $scope.data = [];
        $scope.result = [];
    }

    /** 配置參數 **/
    $scope.configureParameter = () => {
        $scope.mappingSet = {
            /** 報表種類 **/
            type: [{LABEL: '無', DATA: 'N'}, {LABEL: 'CSV', DATA: 'C'}, {LABEL: 'TXT', DATA: 'T'}],
            /** 功能 **/
            func: [{LABEL: '查看', DATA: 'R'}, {LABEL: '修改', DATA: 'U'}, {LABEL: '刪除', DATA: 'D'}, {
                LABEL: '下載',
                DATA: 'L'
            }],
            /** 檔案編碼 **/
            coding: [{LABEL: 'UTF-8', DATA: 'U'}, {LABEL: 'MS950', DATA: 'M'}]
        }
    }

    /** 查詢報表內容設定資料 **/
    $scope.inquire = () => {
        $scope.sendRecv('CMMGR016', 'inquire', voName, $scope.inputVO, (tota, isError) => {
            if (!isError) {
                $scope.outputVO = tota[0].body;
                $scope.result = tota[0].body.resultList;
                if (!$scope.result.length)
                    $scope.showMsg('ehl_01_common_009');
            } else $scope.showMsg('ehl_01_common_024');
        });
    }

    /**
     * 執行動作
     * @param  row 資料列
     *
     * funcType
     * R：查詢
     * U：修改
     * D：刪除
     * L：下載
     *
     */
    $scope.exec = row => {
        switch (row.func) {
            case 'R':
            case 'U':
                $scope.cruAction(row.func, row.RPT_CODE);
                break;
            case 'D':
                $scope.delAction(row.RPT_CODE);
                break;
            case 'L':
                $scope.download(row.RPT_CODE);
        }
        row.func = '';
    }

    /** 新增 C / 查詢 R / 修改 U **/
    $scope.cruAction = (type, code) => {
        let dialog = ngDialog.open({
            template: 'assets/txn/CMMGR016/CMMGR016_detail.html',
            className: 'CMMGR016Detail',
            showClose: false,
            controller: ['$scope', function ($scope) {
                $scope.funcType = type;
                $scope.code = code;
            }]
        });

        dialog.closePromise.then((data) => {
            if (data.value === 'successful')
                setTimeout(() => $scope.inquire(), 1000);
        });
    }


    /** 刪除報表 **/
    $scope.delAction = code => {
        $confirm({text: `確定刪除 ${code} 報表?`}, {size: 'sm'}).then(() => {
            $scope.sendRecv('CMMGR016', 'delete', voName, {code: code}, (_, isError) => {
                if (!isError) {
                    $scope.showMsg('ehl_01_common_003');
                    $scope.inquire();
                } else $scope.showMsg('ehl_01_common_024');
            });
        });
    }

    /** 下載報表 **/
    $scope.download = code => {
        $scope.sendRecv('CMMGR016', 'download', voName, {code: code}, (_, isError) => {
            if (!isError) $scope.showMsg('ehl_01_common_023');
            else $scope.showMsg('ehl_01_common_024');
        });
    }

    /** 初始化 **/
    $scope.init = () => {
        $scope.clear();
        $scope.configureParameter();
    }
    $scope.init();
});
