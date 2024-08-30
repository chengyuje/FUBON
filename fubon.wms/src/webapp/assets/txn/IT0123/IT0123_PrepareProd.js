/**================================================================================================
 @program: IT0123_PrepareProd.js
 @author Eli
 @version: 20200311 初版
 =================================================================================================*/
'use strict';
eSoafApp.controller('IT0123_PrepareProd_Controller', ($scope, $controller, $filter, ngDialog, $confirm) => {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "IT0123_PrepareProd_Controller";
        $scope.modelName = 'com.systex.jbranch.app.server.fps.it0123.IT0123InputVO';
        /** 載入 CommonUtil **/
        $controller('CommonUtil', {$scope: $scope});

        /** 查詢上線必要更新紀錄 **/
        $scope.query = () => {
            $scope.sendRecv("IT0123", "queryProdMustDo", $scope.modelName,
                {mustUpdateRecord: $scope.queryVO}, (tota, isError) => {
                    if (!isError) {
                        $scope.recordList = tota[0].body.recordList;
                        $scope.outputVO = tota[0].body;

                        if (!$scope.recordList.length)
                            $scope.showMsg('ehl_01_common_009');
                    }
                });
        };

        /** 新增/修改資料 **/
        $scope.edit = row => {
            let self = $scope;
            let dialog = ngDialog.open({
                template: 'assets/txn/IT0123/IT0123_PrepareProd_Edit.html',
                className: 'IT0123_PrepareProd_Edit',
                showClose: false,
                controller: ['$scope', function ($scope) {
                    $scope.modelName = self.modelName;
                    $scope.mappingSet = self.mappingSet;

                    $scope.isUpdate = Boolean(row);
                    if ($scope.isUpdate) {
                        $scope.inputVO = angular.copy(row);
                        // 先保存紀錄之附檔名，更新成功可用這個附檔名來刪除原始附件檔案。
                        $scope.inputVO.PREV_FILE_NAME = row.FILE_NAME;
                    } else $scope.inputVO = {}
                }]
            });
            dialog.closePromise.then(function (data) {
                if (data.value === 'successful') {
                    $scope.query();
                }
            });
        };

        /** 讀取參數 **/
        function initParameter() {
            $scope.mappingSet['TYPE'] = [
                {LABEL: 'TABLE', DATA: 'TABLE'},
                {LABEL: 'DATA', DATA: 'DATA'},
                {LABEL: 'PACKAGE', DATA: 'PACKAGE'},
                {LABEL: 'INDEX', DATA: 'INDEX'},
                {LABEL: 'FUNCTION', DATA: 'FUNCTION'}];
            /** 篩選開關**/
            $scope.filterMode = false;
        }

        /** 重置結果 **/
        $scope.resetResult = () => $scope.recordList = [];

        /** 重製查詢條件組 **/
        $scope.resetQueryVO = () => $scope.queryVO = {};

        /** 下載檔案 **/
        $scope.downloadProdMustDoFile = fileName => {
            $scope.sendRecv("IT0123", "downloadProdMustDoFile", $scope.modelName,
                {mustUpdateRecord: {FILE_NAME: fileName}}, () => {
                });
        };

        /** 下載說明文件 **/
        $scope.downloadPrepareProdIntroDoc = () => {
            $scope.sendRecv("IT0123", "downloadPrepareProdIntroDoc", $scope.modelName, {}, () => {
            });
        };

        /** 篩選開關 **/
        $scope.togglefilter = () => {
            $scope.filterMode = !$scope.filterMode;
            $('.IT0123_PrepareProd .queryConditionRow').slideToggle();
        };

        /** 初始日曆 **/
        function initCalendar() {
            $scope.qryCalendar = {};
            $scope.initDateOptions($scope.qryCalendar);
        }

        /** 刪除指定紀錄 **/
        $scope.deleteProdMustDoRecord = each => {
            $confirm({text: `確定要刪除 ${each.FILE_NAME} 該筆紀錄？`}, {size: 'sm'}).then(() => {
                $scope.sendRecv("IT0123", "deleteProdMustDoRecord", $scope.modelName,
                    {mustUpdateRecord: each}, (_, isError) => {
                        if (!isError) {
                            $scope.showSuccessMsg('ehl_01_common_003');
                            $scope.query();
                        }
                    });
            });
        };

        /** 初始化 **/
        function init() {
            initCalendar();
            $scope.resetQueryVO();
            $scope.resetResult();
            initParameter();
            $scope.query();
        }

        init();
    }
);
