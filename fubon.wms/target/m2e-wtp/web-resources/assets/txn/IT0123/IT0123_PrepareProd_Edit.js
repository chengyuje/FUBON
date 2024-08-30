/**================================================================================================
 @program: IT0123_PrepareProd_Edit.js
 @author: Eli
 @date: 上線必要更新資料編輯頁（新增/更新）
 =================================================================================================**/
'use strict';
eSoafApp.controller("IT0123_PrepareProd_EditController", ($scope, $controller, $filter) => {
    $controller('BaseController', {$scope: $scope});
    $scope.controllerName = "IT0123_PrepareProd_EditController";

    /** 上傳檔案完成時 **/
    $scope.uploadFinshed = (name, rname) => {
        $scope.inputVO.FILE_NAME = name;
        $scope.inputVO.REAL_NAME = `${$filter('date')(new Date(), 'yyyyMMddHHmmss_sss')}_${rname}`;
    };

    /** 點選儲存鍵 **/
    $scope.save = () => {
        if ($scope.recordEditForm.$invalid) {
            $scope.showErrorMsg('欄位檢核錯誤:請輸入必填欄位！');
            return;
        }

        // 檢核是否有選擇類型
        if (!$scope.inputVO.TYPE) {
            $scope.showErrorMsg('請選擇類型！');
            return;
        }

        // 新增紀錄需檢查 FILE_NAME、REAL_NAME 是否有值。更新紀錄因為 FILE_NAME 已存在，不需更新
        if (!$scope.isUpdate && !$scope.inputVO.FILE_NAME && !$scope.inputVO.REAL_NAME) {
            $scope.showErrorMsg('請上傳檔案！');
            return;
        }

        if ($scope.isUpdate) $scope.saveLogic('update', 'ehl_01_common_002');
        else $scope.saveLogic('insert', 'ehl_01_common_001');
    };

    /** 儲存邏輯 **/
    $scope.saveLogic = (action, msg) => {
        $scope.sendRecv("IT0123", action + "ProdMustDo", $scope.modelName,
            {mustUpdateRecord: $scope.inputVO}, (_, isError) => {
                if (!isError) {
                    $scope.showSuccessMsg(msg);
                    $scope.closeThisDialog("successful");
                } else $scope.showErrorMsg('ehl_01_common_024')
            });
    };

    /** 關閉 Dialog **/
    $scope.cancel = () => {
        $scope.closeThisDialog("cancel");
    };
});