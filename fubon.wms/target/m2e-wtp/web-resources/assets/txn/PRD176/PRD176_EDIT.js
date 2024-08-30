'use strict';
eSoafApp.controller('PRD176_EDITController',
    function ($scope, $controller) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "PRD176_EDITController";
        $controller('CommonUtil', {$scope: $scope}); // 載入 CommonUtil

        function init() {
            /** 初始日曆 **/
            $scope.contractDateObj = {};
            $scope.initDateOptions($scope.contractDateObj);

            $scope.renewDateObj = {};
            $scope.initDateOptions($scope.renewDateObj);

            $scope.termDateObj = {};
            $scope.initDateOptions($scope.termDateObj);

            $scope.row = $scope.row || {};
            $scope.inputVO = {
                serialNum: $scope.row.SERIALNUM,
                cname: $scope.row.CNAME,
                shortname: $scope.row.SHORTNAME,
                ename: $scope.row.ENAME,
                zipcode: $scope.row.ZIPCODE,
                address: $scope.row.ADDRESS,
                contactPersonName: $scope.row.CONTACTPERSONNAME,
                contactPersonPhone: $scope.row.CONTACTPERSONPHONE,
                idNum: $scope.row.IDNUM,
                contactPersonEmail: $scope.row.CONTACTPERSONEMAIL,
                admContactPersonName: $scope.row.ADMCONTACTPERSONNAME,
                admContactPersonPhone: $scope.row.ADMCONTACTPERSONPHONE,
                admContactPersonEmail: $scope.row.ADMCONTACTPERSONEMAIL,
                indContactPersonName: $scope.row.INDCONTACTPERSONNAME,
                indContactPersonPhone: $scope.row.INDCONTACTPERSONPHONE,
                indContactPersonEmail: $scope.row.INDCONTACTPERSONEMAIL,
                contractDate: $scope.toJsDate($scope.row.CONTRACTDATE),
                renewDate: $scope.toJsDate($scope.row.RENEWDATE),
                termDate: $scope.toJsDate($scope.row.TERMDATE)
            };
        }

        init();

        $scope.save = () => {
            if ($scope.prd176EditForm.$invalid) {
                $scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
                return;
            }
            $scope.sendRecv("PRD176", "save", "com.systex.jbranch.app.server.fps.prd176.PRD176DataVO", $scope.inputVO,
                function (tota, isError) {
                    if (isError) {
                        $scope.showErrorMsg(tota[0].body.msgData);
                    }
                    if (tota.length > 0) {
                        $scope.showSuccessMsg('ehl_01_common_004');
                        $scope.closeThisDialog('successful');
                    }
                });
        };
    });
