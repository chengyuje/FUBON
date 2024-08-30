/**================================================================================================
 @program: CMMGR018.js
 @author Eli
 @version: 1.0.20181015
 @version: 1.0.20181130 一進UI直接查詢該主機指定的LOG路徑下的LOG列表
 @version: 1.0.20181205 下載完成再另行呼叫去刪除壓縮檔
 =================================================================================================*/
'use strict';
eSoafApp.controller('CMMGR018_Controller', ($scope, $controller) => {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR018_Controller";

        /**
         * 根據所選Log目錄路徑，查詢底下的Log檔案
         */
        $scope.sendRecv('CMMGR018', 'inquireLogs', 'com.systex.jbranch.app.server.fps.cmmgr018.CMMGR018InputVO',
            {}, (tota, isError) => {
                if (!isError) {
                    $scope.outputVO = tota[0].body;
                    $scope.resultList = tota[0].body.resultList;
                } else {
                    $scope.showErrorMsg('ehl_01_common_024');
                }
            });

        /**
         * 下載指定的Log檔案
         */
        $scope.downloadLog = (row) => {
            $scope.sendRecv('CMMGR018', 'downloadLog', 'com.systex.jbranch.app.server.fps.cmmgr018.CMMGR018InputVO',
                {logDirPath: row.PARENT_PATH, logName: row.LOG_NAME}, (tota, isError) => {
                    if (!isError) {
                        delZip(tota[1].body);
                    }
                });
        }

        /** delete Log Zip in temp folder **/
        function delZip(zipPath) {
            $scope.sendRecv('CMMGR018', 'deleteZip', 'com.systex.jbranch.app.server.fps.cmmgr018.CMMGR018InputVO',
                {zipPath: zipPath});
        }
    }
);
