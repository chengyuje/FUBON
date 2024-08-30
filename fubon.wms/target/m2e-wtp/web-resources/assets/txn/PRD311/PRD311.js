/**================================================================================================
 @program PRD311 E 學院課程商品查詢
 @author Eli
 @Description
 @version 1.0.20190903
 =================================================================================================*/
'use strict';
eSoafApp.controller('PRD311Controller', ($rootScope, $scope, $controller, $confirm, $filter, socketService, ngDialog) => {
    $controller('BaseController', {$scope: $scope});
    $scope.controllerName = "PRD311Controller";
    $scope.modelName = 'com.systex.jbranch.app.server.fps.prd311.PRD311InputVO';

    /**  clear button action  **/
    $scope.clear = () => {
        $scope.initValue();
    }

    /**  查詢動作 **/
    $scope.query = () => {
        $scope.sendRecv('PRD311', 'query', $scope.modelName, $scope.inputVO,
            (tota, isError) => {
                if (!isError) {
                    $scope.data = tota[0].body.result;
                    $scope.outputVO = tota[0].body;

                    if (!$scope.data.length)
                        $scope.showMsg('ehl_01_common_009');
                }
            });
    }

    /**  新增動作  **/
    $scope.add = () => {
        if ($scope.dataInvalid()) return;

        $scope.sendRecv('PRD311', 'add', $scope.modelName, $scope.inputVO,
            (tota, isError) => {
                $scope.clear();
                if (!isError) {
                    let errMsg = tota[0].body;
                    if (errMsg) $scope.showErrorMsg(errMsg);
                    else {
                        $scope.showSuccessMsg('ehl_01_common_023');
                        $scope.query();
                    }
                }
            });
    }

    /** 確認資料是否備齊 **/
    $scope.dataInvalid = () => {
        if ($scope.inputVO.classId && $scope.inputVO.prodId && $scope.inputVO.empId) return false;
        else {
            $scope.showErrorMsg('課程代碼、商品代碼、員工編碼均為必填！');
            return true;
        }
    }

    /**  upload button action  **/
    $scope.upload = () => {
        let dialog = ngDialog.open({
            template: 'assets/txn/PRD311/PRD311_UPLOAD.html',
            className: 'PRD311_UPLOAD',
            showClose: false,
            controller: ['$scope', function ($scope) {

            }]
        });
        dialog.closePromise.then(function (data) {
            if (data.value != 'cancel') {
                $scope.uploadData(data.value);
            }
        });
    }

    /**  將上傳的檔案資料匯入資料庫 **/
    $scope.uploadData = fileName => {
        $scope.sendRecv('PRD311', 'upload', $scope.modelName, {fileName: fileName}, (tota, isError) => {
            $scope.clear();
            if (!isError) {
                let errMsg = tota[0].body;
                if (errMsg.length) $scope.showErrorMsg(errMsg.join(''));
                else $scope.showSuccessMsg('ehl_01_common_010');
                $scope.query();
            }
        });
    }

    /**  刪除動作 **/
    $scope.delete = row => {
        $scope.sendRecv('PRD311', 'delete', $scope.modelName,
            {
                classId: row.CLASS_ID,
                prodId: row.PRD_ID,
                empId: row.EMP_ID
            }, (tota, isError) => {
                $scope.clear();
                if (!isError) {
                    let errMsg = tota[0].body;
                    if (errMsg) $scope.showErrorMsg(errMsg);
                    else {
                        $scope.showSuccessMsg('ehl_01_common_003');
                        $scope.query();
                    }
                }
            });
    }

    /** 配置參數 **/
    $scope.configureParameters = () => {
        $scope.mappingSet = {
            STATUS: [{LABEL: '未通過', DATA: '0'}, {LABEL: '通過', DATA: '1'}]
        }
    }

    /** 匯出資料成 Excel **/
    $scope.exportExcel = () => $scope.sendRecv("PRD311", "export", $scope.modelName, $scope.inputVO, () => {});

    /**  初始變數 **/
    $scope.initValue = () => {
        $scope.data = [];
        $scope.subData = [];
        $scope.outputVO = {};
        $scope.inputVO = {};
    }

    $scope.init = () => {
        $scope.initValue();
        $scope.configureParameters();
    }
    $scope.init();
});