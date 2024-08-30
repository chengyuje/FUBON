/**================================================================================================
 @program PRD311 E 學院課程商品查詢 上船檔案頁面
 @author Eli
 @Description
 @version 1.0.20190903
 =================================================================================================*/
'use strict';
eSoafApp.controller('PRD311_UPLOADController',
    function ($scope, $controller) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = 'PRD311_UPLOADController';

        $scope.uploadFinshed = (name, rname) => {
            $scope.inputVO.fileName = name;
            $scope.inputVO.realfileName = rname;
        }

        /**  下載範例檔案 **/
        $scope.downloadExample = () => $scope.sendRecv('PRD311', 'uploadExample', 'com.systex.jbranch.app.server.fps.prd311.PRD311InputVO', {}, () => {
        });

        /**  送出 **/
        $scope.send = () => $scope.closeThisDialog($scope.inputVO.fileName);
    });