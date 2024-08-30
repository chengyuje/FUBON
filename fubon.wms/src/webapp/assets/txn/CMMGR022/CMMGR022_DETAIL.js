/**================================================================================================
 @program: CMMGR022_DETAIL.js
 @author Eli
 @Description 提供電文的詳細資料
 @version: 1.0.20190613
 =================================================================================================**/
'use strict';
eSoafApp.controller("CMMGR022_DETAILController", ($rootScope, $scope, $controller) => {
    $controller('BaseController', {$scope: $scope});
    $scope.controllerName = "CMMGR022_DETAILController";
    $scope.modelName = 'com.systex.jbranch.app.server.fps.cmmgr022.CMMGR022InputVO';

    /** 載入 CommonUtil **/
    $controller('CommonUtil', {$scope: $scope});

    /** 撈取電文內容 **/
    $scope.qryContent = () => {
        let vo = $scope.vo;
        $scope.sendRecv("CMMGR022", "qryContent", $scope.modelName , {number: vo.HSTANO, id: vo.HTXTID, empId: vo.CREATOR},
            (tota, isError) => {
                if (!isError) {
                    vo.onMsg = $scope.formattedContent(tota[0].body.onMsg);
                    vo.offMsg = $scope.formattedContent($scope.tidy(tota[0].body.offMsg));
                }
            });
    }
    $scope.qryContent();

    /** 下行電文整理一下 **/
    $scope.tidy = msg => msg.replace(/(><)/gi, '>#<').split('#').join('\n');

    /** 格式化電文 **/
    $scope.formattedContent = msg => {
        return msg.split('\n\n')
                  .filter(e => e)
                  .map((e, i) => `--------------------第${i+1}次--------------------\n${e}`)
                  .join('\n\n');
    }
});