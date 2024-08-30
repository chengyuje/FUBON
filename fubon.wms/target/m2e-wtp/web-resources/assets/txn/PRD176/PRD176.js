'use strict';
eSoafApp.controller('PRD176Controller',
    function ($rootScope, $scope, $controller, $confirm, socketService, ngDialog) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "PRD176Controller";
        $controller('CommonUtil', {$scope: $scope}); // 載入 CommonUtil

        $scope.inquireInit = function () {
            $scope.inputVO = {};
            $scope.totalList = [];
            $scope.outputVO = {};
        }

        $scope.inquire = function () {
            $scope.sendRecv("PRD176", "inquire", "com.systex.jbranch.app.server.fps.prd176.PRD176InputVO", $scope.inputVO,
                function (tota, isError) {
                    if (!isError) {
                        $scope.totalList = tota[0].body.resultList;
                        $scope.outputVO = tota[0].body;

                        if ($scope.totalList.length <= 0)
                            $scope.showMsg('ehl_01_common_009'); // 查無資料
                    }
                });
        };

        /** 匯出參數資料成 CSV **/
        $scope.exportCSV = isSample => {
            $scope.sendRecv("PRD176", "exportCSV",
                "com.systex.jbranch.app.server.fps.prd176.PRD176InputVO",
                isSample ? {isSampleExport: isSample} : $scope.inputVO,
                () => {
                });
        }

        $scope.upload = function () {
            const exportFn = $scope.exportCSV;
            const dialog = ngDialog.open({
                template: 'assets/txn/PRD176/PRD176_UPLOAD.html',
                className: 'PRD176_UPLOAD',
                showClose: false,
                controller: ['$scope', function ($scope) {
                    $scope.exportCSV = exportFn;
                }]
            });
            dialog.closePromise.then(function (data) {
                if (data.value === 'successful') {
                    $scope.inquireInit();
                    $scope.inquire();
                }
            });
        };

        $scope.action = function (row) {
            if (row.cmbAction) {
                if (row.cmbAction === 'D') {
                    $confirm({text: '您確定刪除此筆記錄？'}, {size: 'sm'}).then(function () {
                        $scope.sendRecv("PRD176", "delete", "com.systex.jbranch.app.server.fps.prd176.PRD176InputVO",
                            {'serialNum': row.SERIALNUM}, function (totas, isError) {
                                if (isError) {
                                    $scope.showErrorMsg(totas[0].body.msgData);
                                }
                                if (totas.length > 0) {
                                    $scope.showSuccessMsg('ehl_01_common_003'); // 刪除成功
                                    $scope.inquireInit();
                                    $scope.inquire();
                                }
                            }
                        );
                    });
                } else
                    $scope.edit(row);
                row.cmbAction = '';
            }
        };

        $scope.edit = function (row) {
            const dialog = ngDialog.open({
                template: 'assets/txn/PRD176/PRD176_EDIT.html',
                className: 'PRD176_EDIT',
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

        // init
        $scope.init = function () {
            $scope.inquireInit();

            /** 初始日曆 **/
            $scope.contradateObj = {};
            $scope.initDateOptions($scope.contradateObj);

            $scope.renewdateObj = {};
            $scope.initDateOptions($scope.renewdateObj);


            /** InputVO 初始 **/
            $scope.inputVO = {
                name: null,
                contractSDate: null,
                contractEDate: null,
                renewSDate: null,
                renewEDate: null
            };

            $scope.mappingSet['ACTION'] = [
                {LABEL: "修改", DATA: "U"},
                {LABEL: "刪除", DATA: "D"}
            ];
        };
        $scope.init();
    });
