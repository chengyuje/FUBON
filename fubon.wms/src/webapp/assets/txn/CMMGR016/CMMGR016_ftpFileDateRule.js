/**================================================================================================
 @program: CMMGR016_ftpFileDateRule.js
 @author Eli
 @description Ftp 檔名日期格式化規則
 @date 20190701
 =================================================================================================**/
'use strict';
eSoafApp.controller('CMMGR016_ftpFileDateRuleController', ($rootScope, $scope, $controller) => {
    $controller('BaseController', {$scope: $scope});
    $scope.controllerName = "CMMGR016_ftpFileDateRuleController";

    /** 取得格式化的檔案名稱 **/
    $scope.getRealName = () => {
        $scope.sendRecv('CMMGR016', 'getRealName', 'com.systex.jbranch.app.server.fps.cmmgr016.CMMGR016InputVO',
            {name: $scope.testName}, (tota, isError) => {
                if (!isError) {
                    let msg = tota[0].body;
                    if (msg == 'error') {
                        $scope.error = '日期格式有誤！';
                        $scope.realName = '';
                    }
                    else {
                        $scope.error = '';
                        $scope.realName = msg;
                    }
                }
            });
    }
});
