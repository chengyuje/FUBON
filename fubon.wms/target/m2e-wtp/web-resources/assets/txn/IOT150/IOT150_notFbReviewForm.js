'use strict';
eSoafApp.controller('IOT150_notFbReviewFormController',
    function($scope, $controller) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "IOT150_notFbReviewFormController";
        $controller('CommonUtil', {$scope: $scope}); // 載入 CommonUtil

        $scope.save = function () {
            if ($scope.IOT150NotFbReviewForm.$invalid) {
                $scope.showErrorMsg('欄位檢核錯誤：請輸入必填欄位！');
                return;
            }

            $scope.sendRecv("IOT150", "reviewNotFubonData", "com.systex.jbranch.app.server.fps.iot150.IOT150NotFbReviewFormVO",
                $scope.inputVO, function (tota, isError) {
                    if (!isError) {
                        $scope.showSuccessMsg('ehl_01_common_006'); // 更新成功
                        $scope.closeThisDialog('cancel');
                    }
                });
        }

        $scope.init = function () {
            // 初始日曆
            $scope.notFbSignDateObj = {};
            $scope.initDateOptions($scope.notFbSignDateObj);

            // InputVO 初始
            $scope.inputVO = {
                notFbSignDate: new Date(),
                notFbOpName: $scope.defaultContact,
                insKeyNo: $scope.selected.map(each => each.INS_KEYNO)
            };
        };
        $scope.init();
    });
