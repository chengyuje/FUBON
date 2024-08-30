/**================================================================================================
 @program PRD310 E 學院課程商品維護
 @author Eli
 @Description
 @version 1.0.20190903
 =================================================================================================*/
'use strict';
eSoafApp.controller('PRD310Controller', ($rootScope, $scope, $controller) => {
    $controller('BaseController', {$scope: $scope});
    $scope.controllerName = 'PRD310Controller';
    $scope.modelName = 'com.systex.jbranch.app.server.fps.prd310.PRD310InputVO';

    /**  查詢課程資料 **/
    $scope.query = () => {
        $scope.sendRecv('PRD310', 'query', $scope.modelName, $scope.inputVO,
            (tota, isError) => {
                if (!isError) {
                    $scope.data = tota[0].body.result;
                    $scope.outputVO = tota[0].body;

                    if (!$scope.data.length)
                        $scope.showMsg('ehl_01_common_009');
                }
            });
    }

    /** 新增課程 **/
    $scope.add = () => {
        if ($scope.dataInvalid()) return;

        $scope.sendRecv('PRD310', 'add', $scope.modelName, $scope.inputVO,
            (tota, isError) => {
                $scope.clear();
                if (!isError) {
                    let errMsg = tota[0].body;
                    if (errMsg) $scope.showErrorMsg(errMsg);
                    else {
                        $scope.showSuccessMsg('ehl_01_common_025');
                        $scope.query();
                    }
                }
            });
    }

    /** 確認資料是否備齊 **/
    $scope.dataInvalid = () => {
        if ($scope.inputVO.classId && $scope.inputVO.prodId) return false;
        else {
            $scope.showErrorMsg('課程代碼或商品代碼為空，無法新增！');
            return true;
        }
    }

    /** 刪除課程 **/
    $scope.delete = classId => {
        $scope.sendRecv('PRD310', 'delete', $scope.modelName, {classId: classId},
            (tota, isError) => {
                $scope.clear();
                if (!isError) {
                    $scope.showSuccessMsg('ehl_01_common_003');
                    $scope.query();
                }
            });
    }

    /** 匯出資料成 Excel **/
    $scope.exportExcel = () => $scope.sendRecv("PRD310", "export", $scope.modelName, $scope.inputVO, () => {});

    /**  清除數據 **/
    $scope.clear = () => {
        $scope.init();
    }

    $scope.init = () => {
        $scope.data = [];
        $scope.subData = [];
        $scope.outputVO = {};
        $scope.inputVO = {};
    }
    $scope.init();
});