/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMMGR007Controller',
    function ($scope, $controller, socketService, ngDialog, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR007Controller";

        $scope.init = function () {
            $scope.inputVO = {
                jobid: '',
                jobname: '',
                description: '',
                beanid: '',
                classname: '',
                parameters: '',
                precondition: '',
                postcondition: ''
            };
        }
        $scope.init();

//      初始分頁資訊
        $scope.inquireInit = function () {
            $scope.resultList = [];
        }
        $scope.inquireInit();


        $scope.inquire = function () {
            $scope.sendRecv("CMMGR007", "inquire", "com.systex.jbranch.app.server.fps.cmmgr007.CMMGR007InputVO", $scope.inputVO,
                function (tota, isError) {
                    if (!isError) {
                        $scope.resultList = tota[0].body.resultList;
                        $scope.outputVO = tota[0].body;
                        if (!$scope.resultList.length)
                            $scope.showMsg('ehl_01_common_009');
                    }
                });
        };

        $scope.edit = function (row) {
            var dialog = ngDialog.open({
                template: 'assets/txn/CMMGR007/CMMGR007_EDIT.html',
                className: 'CMMGR007',
                showClose: false,
                controller: ['$scope', function ($scope) {
                    $scope.row = row;
                }]
            });
            dialog.closePromise.then(function (data) {
                if (data.value === 'successful') {
                    $scope.inquireInit();
                    $scope.inquire();
                }
            });
        };


    });