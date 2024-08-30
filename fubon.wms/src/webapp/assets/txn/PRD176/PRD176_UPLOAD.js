'use strict';
eSoafApp.controller('PRD176_UPLOADController',
    function($scope, $controller) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "PRD176_UPLOADController";
        $scope.inputVO = {};

        $scope.uploadFinshed = function(name, rname) {
            $scope.inputVO.fileName = name;
            $scope.inputVO.fileRealName = rname;
        };

        $scope.save = function() {
            $scope.sendRecv("PRD176", "upload", "com.systex.jbranch.app.server.fps.prd176.PRD176InputVO", $scope.inputVO,
                function(tota, isError) {
                    if (isError) {
                        $scope.showErrorMsg(tota[0].body.msgData);
                    } else {
                        const { requiredError, typeError } = tota[0].body;
                        if (!(typeError.length || requiredError.length)) {
                            $scope.showSuccessMsg('ehl_01_common_004');
                            $scope.closeThisDialog('successful');
                        } else {
                            $scope.showSuccessMsg('部分處理完畢，尚有錯誤的資料將無法儲存！');
                            if (requiredError.length)
                                $scope.showErrorMsg(`下列表格位置為必要欄位：${requiredError.join('、')}`);
                            if (typeError.length)
                                $scope.showErrorMsg(`下列表格位置型別錯誤：${typeError.join('、')}`);
                        }
                    }
                });
        };
    });
