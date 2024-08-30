/**================================================================================================
 @program: CMMGR006.js
 @author: Eli
 @date: 20190416 參數種類與編碼設定（TBSYSPARAMTYPE & TBSYSPARAMETER）
 =================================================================================================**/
'use strict';
eSoafApp.controller('CMMGR006_Controller', ($scope, $controller, socketService, ngDialog, projInfoService, getParameter) => {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR006_Controller";

        /** 載入參數 **/
        $scope.loadParameter = () => {
            getParameter.XML(['TBSYSPARAMTYPE.PTYPE_BUSS'], (totas) => {
                if (totas)
                    $scope.mappingSet['TBSYSPARAMTYPE_PTYPE_BUSS'] = totas.data[totas.key.indexOf('TBSYSPARAMTYPE.PTYPE_BUSS')];
            });
        }

        /** 查詢參數種類資料 **/
        $scope.inquire = () => {
            $scope.sendRecv("CMMGR006", "inquireType", "com.systex.jbranch.app.server.fps.cmmgr006.CMMGR006InputVO", $scope.inputVO,
                (tota, isError) => {
                    if (!isError) {
                        $scope.paramData = tota[0].body.paramData;
                        $scope.outputVO = tota[0].body;
                        if (!$scope.paramData.length)
                            $scope.showMsg('ehl_01_common_009');
                    }
                });
        }

        /** 新增/編輯彈跳視窗 **/
        $scope.edit = row => {
            let self = $scope;
            let dialog = ngDialog.open({
                template: 'assets/txn/CMMGR006/CMMGR006_EDIT.html',
                className: 'CMMGR006_EDIT',
                showClose: false,
                controller: ['$scope', function ($scope) {
                    $scope.row = row;
                    $scope.mappingSet = self.mappingSet;
                }]
            });

            dialog.closePromise.then((data) => {
                if (data.value === 'successful')
                    $scope.inputVO = {};
                $scope.inquire();
            });
        }

        /** 匯出參數資料成 Excel **/
        $scope.exportExcel = () => $scope.sendRecv("CMMGR006", "exportExcel", "com.systex.jbranch.app.server.fps.cmmgr006.CMMGR006InputVO", $scope.inputVO);

        /** 初始化 **/
        $scope.init = () => {
            $scope.paramData = [];
            $scope.subData = [];
            $scope.outputVO = {};
            $scope.inputVO = {};

            $scope.loadParameter();
        }
        $scope.init();
    }
);
