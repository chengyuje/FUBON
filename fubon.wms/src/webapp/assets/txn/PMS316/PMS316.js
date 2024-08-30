/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS316Controller',
    function ($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "PMS316Controller";
        // 繼承共用的組織連動選單
        $controller('PMSRegionController', {$scope: $scope});

        /***TEST ORG COMBOBOX START***/
        var org = [];

        $scope.initLoad = function () {
            $scope.sendRecv("PMS000", "getLastYMlist", "com.systex.jbranch.app.server.fps.pms000.PMS000InputVO", {},
                function (totas, isError) {
                    if (totas.length > 0) {
                        $scope.ymList = [];
                        var month = new Date().getMonth() ;
                        var year  = new Date().getFullYear();
                        var ymListLength = totas[0].body.ymList.length;

                        $scope.ymList = totas[0].body.ymList;
                    };
                }
            );
        }
        $scope.initLoad();


        //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dataMonthChange = function () {

            if ($scope.inputVO.dataMonth_E != undefined && $scope.inputVO.dataMonth_S != undefined
                && $scope.inputVO.dataMonth_E != '' && $scope.inputVO.dataMonth_S != '') {

                if ($scope.inputVO.dataMonth_S > $scope.inputVO.dataMonth_E){
                    $scope.showErrorMsg('起月不可大於迄月');
                    $scope.inputVO.dataMonth_S = '';
                    $scope.inputVO.dataMonth_E = '';
                    return;
                }
                
                var s = $scope.inputVO.dataMonth_S.substring(0,4);
                var e = $scope.inputVO.dataMonth_E.substring(0,4);
                if(s != e){
                	$scope.showErrorMsg('起迄年份需相同');
                	$scope.inputVO.dataMonth_S = '';
                	$scope.inputVO.dataMonth_E = '';
                	return;
                }

                $scope.inputVO.reportDate = $scope.inputVO.dataMonth_E;
//                $scope.RegionController_getORG($scope.inputVO);
            }
        };

        //選取日期下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function () {
            $scope.inputVO.reportDate = $filter('date')($scope.inputVO.sCreDate, 'yyyyMMdd');
//            $scope.RegionController_getORG($scope.inputVO);
        };
        /***ORG COMBOBOX END***/

//		$scope.open = function($event, index) {
//			$event.preventDefault();
//			$event.stopPropagation();
//			$scope['opened'+index] = true;
//		};

        // date picker
        var date    = new Date();
        var preDate = new Date(date.getTime() - 24*60*60*1000);
//        var monthFirstDate = new Date(preDate.getFullYear(), preDate.getMonth(), 1);

        $scope.bgn_sDateOptions = {
            maxDate: preDate,
//            minDate: monthFirstDate
        };

        
        // config
        $scope.model = {};
        $scope.open = function ($event, elementOpened) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.model[elementOpened] = !$scope.model[elementOpened];
        };

        $scope.limitDate = function () {
            $scope.bgn_sDateOptions.maxDate = preDate;
//            $scope.bgn_sDateOptions.minDate = monthFirstDate;
        };

        // date picker end


        $scope.init = function () {
            $scope.inputVO = {
                income: '1',
                srchDate: '1',
                sCreDate: preDate,
                dataMonth_S: '',
                dataMonth_E: '',
                reportDate: '',
                branch_nbr: '',          //分行
                region_center_id: '',    //區域中心
                branch_area_id: '',      //營運區
                time_type: '0'
            };
            $scope.limitDate();
            $scope.dateChange();
            $scope.paramList = [];		//清空查詢
            $scope.mappingSet['income'] = [];
            $scope.mappingSet['income'].push(
                {
                    LABEL: 'MTD',
                    DATA: 1
                },
                // {
                //     LABEL: 'QTD',
                //     DATA: 2
                // },
                {
                    LABEL: 'YTD',
                    DATA: 3
                }
            );
            $scope.RegionController_getAllORG($scope.inputVO);
        };

        $scope.init();

        $scope.inquireInit = function () {
            $scope.initLimit();
            $scope.paramList = [];
        }

        $scope.inquireInit();

        $scope.sdType = function () {
            //依報表日期查詢
            if ($scope.inputVO.srchDate == '1') {
                $scope.inputVO.dataMonth_S = '';
                $scope.inputVO.dataMonth_E = '';
                $scope.inputVO.sCreDate = preDate;
                $scope.inputVO.income = '1';
            }
            //依計績月份累計
            else {
                $scope.inputVO.sCreDate = undefined;
                $scope.inputVO.income = 1;
                $scope.inputVO.dataMonth_S = $scope.mappingSet['timeE'][1].DATA;
                $scope.inputVO.dataMonth_E = $scope.mappingSet['timeE'][1].DATA;
            }
            $scope.paramList =[];
        }

        $scope.lock = function () {
            if ($scope.inputVO.income == '' && $scope.inputVO.srchDate == '1'){
            	$scope.inputVO.income = 1;
            }else if ($scope.inputVO.srchDate == '2'){
            	$scope.inputVO.income = '';
            }
        }


        $scope.query = function () {
            //依報表日期查詢檢核
            if ($scope.inputVO.srchDate == '1') {
                if ($scope.inputVO.sCreDate == '' || $scope.inputVO.sCreDate == undefined) {
                    $scope.showErrorMsg('欄位檢核錯誤:請選擇報表日期');
                    return;
                }
            }

            //依計績月份累計檢核
            else {
                if ($scope.parameterTypeEditForm.$invalid) {
                    $scope.showErrorMsg('欄位檢核錯誤:請選擇計績月份');
                    return;
                }

                else if ($scope.inputVO.dataMonth_S > $scope.inputVO.dataMonth_E){
                    $scope.showErrorMsg('起月不可大於迄月');
                    return;
                }
            }

            $scope.sendRecv("PMS316", "queryData", "com.systex.jbranch.app.server.fps.pms316.PMS316InputVO", $scope.inputVO,
                function (tota, isError) {
                    if (!isError) {
                        if (tota[0].body.resultList.length == 0) {
                            $scope.paramList = [];
                            $scope.showMsg("ehl_01_common_009");
                            return;
                        }
                        
                        $scope.paramList = tota[0].body.resultList;
                        $scope.outputVO = tota[0].body;
                        $scope.inputVO.time_type = "1";
                        return;

                    } else {
                        $scope.showBtn = 'none';
                    }
                });
        };


        $scope.numGroups = function (input) {
            if (input == null) {
                return;
            }

            return Object.keys(input).length;
        }

        $scope.exportRPT = function () {
            $scope.sendRecv("PMS316", "export", "com.systex.jbranch.app.server.fps.pms316.PMS316OutputVO", $scope.outputVO,
                function (tota, isError) {
                    if (!isError) {
                        return;
                    }
                });
        }

    });