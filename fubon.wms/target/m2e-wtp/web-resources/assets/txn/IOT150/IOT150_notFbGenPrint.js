'use strict';
eSoafApp.controller('IOT150_notFbGenPrintController',
    function ($scope, $controller) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "IOT150_notFbGenPrintController";
        $controller('CommonUtil', {$scope: $scope}); // 載入 CommonUtil

        $scope.inquireInit = function () {
            $scope.inputVO = {};
            $scope.totalList = [];
            $scope.outputVO = {};
        }

        $scope.inquire = function () {
            if ($scope.IOT150NotFbGenPrintForm.$invalid) {
                $scope.showErrorMsg('欄位檢核錯誤：請輸入必填欄位！');
                return;
            }

            $scope.sendRecv("IOT150", "inquireNotFubonData", "com.systex.jbranch.app.server.fps.iot150.IOT150NotFbInputVO", $scope.inputVO,
                function (tota, isError) {
                    if (!isError) {
                        $scope.totalList = tota[0].body.data;
                        $scope.outputVO = tota[0].body
                        if ($scope.totalList.length === 0) {
                            $scope.showMsg("ehl_01_common_009");
                        }
                    }
                });
        }

        // 查詢條件「（起）總行點收送件(簽署後)日期」預設值
        const getAftSignDateStartDefault = () => {
            $scope.sendRecv("IOT150", "getBusDate", "com.systex.jbranch.app.server.fps.iot150.IOT150NotFbInputVO", {},
                function (tota, isError) {
                    if (!isError) {
                        $scope.inputVO.aftSignDateStart = $scope.toJsDate(tota[0].body.busDate);
                    }
                });
        }

        const inquireCompany = () => {
            $scope.sendRecv("PRD176", "inquire", "com.systex.jbranch.app.server.fps.prd176.PRD176InputVO", {},
                function (tota, isError) {
                    if (!isError) {
                        $scope.mappingSet['COMPANY'] = _.chain(tota[0].body.resultList)
                            .map(each => ({LABEL: each.CNAME, DATA: each.SERIALNUM + ''}))
                            .filter(com => com.DATA !== '82') // 非富壽排除「富邦」
                            .value();
                    }
                });
        }

        function validateSelected(selected, isRePrint) {
            if (!selected.length) {
                $scope.showErrorMsg(`沒有勾選任何項目！`)
                return false;
            }

            const invalid = selected.filter(each => each.NOT_FB_OP_NAME && each.NOT_FB_SIGN_DATE);
            if (!isRePrint && invalid.length > 0) {
                $scope.showErrorMsg(`勾選項目必須是「保險公司回饋簽收日期」、「保險公司簽收窗口」皆空白才可以產生送件明細表！`)
                return false;
            }

            const hasPrinted = selected.filter(each => each.NOT_FB_BATCH_SEQ && each.NOT_FB_BATCH_DATE);
            const canRePrint = selected.length === hasPrinted.length;
            const canGenPrint = hasPrinted.length === 0;
            if (isRePrint) { // 選擇補印
                if (!canRePrint) { // 判斷是否能補印
                    $scope.showErrorMsg('所選項目必須全部皆有「送保險公司批號」、「送保險公司日期」才能進行補印');
                    return false;
                }
            } else { // 選擇產生送件明細表
                if (!canGenPrint) { // 判斷是否能產生
                    $scope.showErrorMsg('所選項目必須全部皆無「送保險公司批號」、「送保險公司日期」才能產生送件明細表');
                    return false;
                }
            }
            return true;
        }

        $scope.printNotFubonReport = function (isRePrint) {
            $scope.lockPrintButton = true;

            const selected = $scope.totalList.filter(each => each.SELECTED);
            if(!validateSelected(selected, isRePrint)) {
                $scope.lockPrintButton = false;
                return;
            }

            const printMap = {}
            selected.forEach(eSelected => printMap[eSelected.INS_KEYNO] = eSelected.REMARK_BANK);

            $scope.sendRecv("IOT150", "printNotFubonReport",
                "com.systex.jbranch.app.server.fps.iot150.IOT150NotFbInputVO",
                {
                    insKeyNoMap: printMap,
                    isRePrint: isRePrint,
                    companyNum: selected[0].COMPANY_NUM
                }, function (tota, isError) {
                    $scope.lockPrintButton = false;
                    $scope.inquire()
                });
        }

        $scope.checkRow = function() {
            $scope.totalList.forEach(each => each.SELECTED = $scope.checkVO.clickAll);
        }

        $scope.init = function () {
            $scope.inquireInit();

            // 初始日曆
            $scope.aftSignDateObj = {};
            $scope.initDateOptions($scope.aftSignDateObj);

            // InputVO 初始
            $scope.inputVO = {
                isRePrint: null,
                isNewReg: null,
                aftSignDateStart: null,
                aftSignDateEnd: new Date(),
                aftSignOprId: null,
                companyNum: null
            };
            getAftSignDateStartDefault();
            inquireCompany();

            $scope.checkVO = {}; // 勾選
            $scope.lockPrintButton = false;
        }

        $scope.init();
    });
