'use strict';
eSoafApp.controller('PRD177Controller',
    function ($rootScope, $scope, $controller, $confirm, socketService, ngDialog) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "PRD177Controller";
        $controller('CommonUtil', {$scope: $scope}); // 載入 CommonUtil

        $scope.inquireInit = function (keepRequired) {
            // 保留 inputVO 必填欄位，使呼叫此方法後再呼叫 inquire 可以順利查詢
            if (keepRequired) {
                const {insuranceCoSerialNum} = $scope.inputVO;
                $scope.inputVO = {insuranceCoSerialNum}
            } else {
                $scope.inputVO = {};
            }
            $scope.totalList = [];
            $scope.outputVO = {};
        }

        $scope.inquire = function () {
            if ($scope.prd177InquireForm.$invalid) {
                $scope.showErrorMsg('欄位檢核錯誤:請輸入必要欄位');
                return;
            }

            $scope.sendRecv("PRD177", "inquire", "com.systex.jbranch.app.server.fps.prd177.PRD177InputVO", $scope.inputVO,
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
            $scope.sendRecv("PRD177", "exportCSV",
                "com.systex.jbranch.app.server.fps.prd177.PRD177InputVO",
                isSample ? {isSampleExport: isSample} : $scope.inputVO,
                () => {
                });
        }

        const inquireCompany = () => {
            $scope.sendRecv("PRD176", "inquire", "com.systex.jbranch.app.server.fps.prd176.PRD176InputVO", {},
                function (tota, isError) {
                    if (!isError) {
                        $scope.mappingSet['COMPANY'] = _.chain(tota[0].body.resultList)
                            .map(each => ({LABEL: each.CNAME, DATA: each.SERIALNUM + ''}))
                            .value();
                    }
                });
        };

        $scope.upload = function () {
            const exportFn = $scope.exportCSV;
            const insuranceCoSerialNum = $scope.inputVO.insuranceCoSerialNum;
            const dialog = ngDialog.open({
                template: 'assets/txn/PRD177/PRD177_UPLOAD.html',
                className: 'PRD177_UPLOAD',
                showClose: false,
                controller: ['$scope', function ($scope) {
                    $scope.exportCSV = exportFn;
                    $scope.insuranceCoSerialNum = insuranceCoSerialNum;
                }]
            });
            dialog.closePromise.then(function (data) {
                if (data.value === 'successful') {
                    $scope.inquireInit(true);
                    $scope.inquire();
                }
            });
        };

        $scope.action = function (row) {
            if (!$scope.firstCallInFreeze())
                return;

            if (row.cmbAction) {
                if (row.cmbAction === 'D') {
                    $confirm({text: '您確定刪除此筆記錄？'}, {size: 'sm'}).then(function () {
                        $scope.sendRecv("PRD177", "delete", "com.systex.jbranch.app.server.fps.prd177.PRD177InputVO",
                            {'productSerialNum': row.PRODUCTSERIALNUM, 'itemSerialNum': row.ITEMSERIALNUM},
                            function (totas, isError) {
                                if (isError) {
                                    $scope.showErrorMsg(totas[0].body.msgData);
                                }
                                if (totas.length > 0) {
                                    $scope.showSuccessMsg('ehl_01_common_003'); // 刪除成功
                                    $scope.inquireInit(true);
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

        // 續期佣金率點選檢視，一樣開啟 Edit Dialog 並唯讀即可。
        $scope.viewBound = function (row) {
            openEditDialog(row, true)
        }

        const openEditDialog = (row, readOnly) => {
            const companies = $scope.mappingSet['COMPANY'];
            return ngDialog.open({
                template: 'assets/txn/PRD177/PRD177_EDIT.html',
                className: 'PRD177_EDIT',
                showClose: false,
                controller: ['$scope', function ($scope) {
                    $scope.row = row;
                    $scope.companies = companies;
                    $scope.readOnly = readOnly;
                }]
            });
        }


        $scope.edit = function (row) {
            openEditDialog(row, false).closePromise.then(function (data) {
                if (data.value === 'successful') {
                    $scope.inquireInit(true);
                    $scope.inquire();
                }
            });
        };

        // init
        function init() {
            $scope.inquireInit();

            /** 初始日曆 **/
            $scope.productValidFrom = {};
            $scope.initDateOptions($scope.productValidFrom);

            /** InputVO 初始 **/
            $scope.inputVO = {
                insuranceCoSerialNum: null,
                prdId: null,
                shortName: null,
                currency: null,
                productValidFromSDate: null,
                productValidFromEDate: null
            };

            $scope.mappingSet['ACTION'] = [
                {LABEL: "修改", DATA: "U"},
                {LABEL: "刪除", DATA: "D"}
            ];
            inquireCompany()
        }

        init();

    });
