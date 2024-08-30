'use strict';
eSoafApp.controller('IOT150_notFbSigReviewController',
    function($scope, $controller, getParameter, ngDialog) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "IOT150_notFbSigReviewController";
        $controller('CommonUtil', {$scope: $scope}); // 載入 CommonUtil

        $scope.inquireInit = function () {
            $scope.inputVO = {};
            $scope.totalList = [];
            $scope.outputVO = {};
        }

        $scope.inquire = function () {
            if ($scope.IOT150NotFbSigReviewForm.$invalid) {
                $scope.showErrorMsg('欄位檢核錯誤：請輸入必填欄位！');
                return;
            }

            $scope.sendRecv("IOT150", "inquireNotFubonData", "com.systex.jbranch.app.server.fps.iot150.IOT150NotFbInputVO",
                $scope.inputVO, function (tota, isError) {
                    if (!isError) {
                        $scope.totalList = tota[0].body.data;
                        $scope.outputVO = tota[0].body
                        if ($scope.totalList.length === 0) {
                            $scope.showMsg("ehl_01_common_009");
                        }
                    }
                });
        };

        // 查詢條件「（起）總行點收送件(簽署後)日期」預設值
        const getNotFbBatchDateStartDefault = () => {
            $scope.sendRecv("IOT150", "getBusDate", "com.systex.jbranch.app.server.fps.iot150.IOT150NotFbInputVO", {},
                function (tota, isError) {
                    if (!isError) {
                        $scope.inputVO.notFbBatchDateStart = $scope.toJsDate(tota[0].body.busDate);
                    }
                });
        }

        const inquireCompany = () => {
            $scope.sendRecv("PRD176", "inquire", "com.systex.jbranch.app.server.fps.prd176.PRD176InputVO", {},
                function (tota, isError) {
                    if (!isError) {
                        let data = tota[0].body.resultList;
                        $scope.mappingSet['COMPANY'] = _.chain(data)
                            .map(each => ({LABEL: each.CNAME, DATA: each.SERIALNUM + ''}))
                            .filter(com => com.DATA !== '82') // 非富壽排除「富邦」
                            .value();

                        $scope.mappingSet['COMPANY_CONTACT'] = {}; // 保險公司-行政聯絡人
                        data.forEach(each => { $scope.mappingSet['COMPANY_CONTACT'][each.SERIALNUM] = each.ADMCONTACTPERSONNAME })
                    }
                });
        };

        $scope.checkRow = function() {
            $scope.totalList.forEach(each => each.SELECTED = $scope.checkVO.clickAll);
        }

        function validateSelected(selected) {
            if (!selected.length) {
                $scope.showErrorMsg(`沒有勾選任何項目！`)
                return false;
            }

            const invalid = selected.filter(each => !each.NOT_FB_BATCH_DATE || each.NOT_FB_SIGN_DATE);
            if (invalid.length > 0) {
                $scope.showErrorMsg(`勾選項目必須已有「送保險公司日期」且「保險公司回饋簽收日期」為空白才可以進行簽收！`)
                return false;
            }
            return true;
        }

        $scope.review = function() {
            $scope.lockPrintButton = true;

            const selected = $scope.totalList.filter(each => each.SELECTED);
            if(!validateSelected(selected)) {
                $scope.lockPrintButton = false;
                return;
            }

            // 保險公司簽收窗口預設是行政聯絡人
            const defaultContact = $scope.mappingSet['COMPANY_CONTACT'][selected[0].COMPANY_NUM];
            const dialog = ngDialog.open({
                template: 'assets/txn/IOT150/IOT150_notFbReviewForm.html',
                className: 'IOT150_notFbReviewForm',
                controller:['$scope',function($scope){
                    $scope.selected = selected;
                    $scope.defaultContact = defaultContact;
                }]
            });
            dialog.closePromise.then(function(data){
                $scope.lockPrintButton = false;
                $scope.inquire();
            });
        }

        $scope.init = function () {
            $scope.inquireInit();

            // 初始日曆
            $scope.notFbBatchDateObj = {};
            $scope.initDateOptions($scope.notFbBatchDateObj);

            // InputVO 初始
            $scope.inputVO = {
                isNewReg: null,
                notFbBatchDateStart: null,
                notFbBatchDateEnd: new Date(),
                aftSignOprId: null,
                companyNum: null
            };
            getNotFbBatchDateStartDefault();
            inquireCompany();

            $scope.checkVO = {}; // 勾選
            $scope.lockPrintButton = false;
        };
        $scope.init();
    });
