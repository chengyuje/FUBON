/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM615Controller',
    function ($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CRM615Controller";

        $scope.cust_id = $scope.custVO.CUST_ID;
        $scope.aoCode = $scope.custVO.AO_CODE;

        // 貢獻度
        $scope.now = new Date();
        //#0003466 : 每月20號才會產出上個月的
        $scope.nowD = $filter('date')($scope.now, 'dd');
        if ($scope.nowD >= 20) {
            $scope.now = $scope.now.setMonth($scope.now.getMonth() - 1);
        } else {
            $scope.now = $scope.now.setMonth($scope.now.getMonth() - 2);
        }
        // 前二年
        $scope.twoYAgo = new Date();
        $scope.twoYAgo = $scope.twoYAgo.setYear($scope.twoYAgo.getFullYear() - 2);
        $scope.twoYAgo = $filter('date')($scope.twoYAgo, 'yyyy');
        // 前一年
        $scope.oneYAgo = new Date();
        $scope.oneYAgo = $scope.oneYAgo.setYear($scope.oneYAgo.getFullYear() - 1);
        $scope.oneYAgo = $filter('date')($scope.oneYAgo, 'yyyy');
        // 最近一年
        $scope.oneRTime = new Date();
        if ($scope.nowD >= 20) {
            $scope.oneRTime = $scope.oneRTime.setMonth($scope.oneRTime.getMonth() - 12);
        } else {
            $scope.oneRTime = $scope.oneRTime.setMonth($scope.oneRTime.getMonth() - 13);
        }
//		$scope.oneRTime = $scope.oneRTime.setMonth($scope.oneRTime.getMonth()-13);
        $scope.oneRTimeY = $filter('date')($scope.oneRTime, 'yyyy');
        $scope.oneRTimeM = $filter('date')($scope.oneRTime, 'MM');
        // 今年以來
        $scope.thisYTime = new Date();
        $scope.thisYTimeY = $filter('date')($scope.thisYTime, 'yyyy');
        //#0003466 : 每月20號才會產出上個月的
        $scope.thisYTimeD = $filter('date')($scope.thisYTime, 'dd');
        if ($scope.thisYTimeD >= 20) {
            $scope.thisYTime = $scope.thisYTime.setMonth($scope.thisYTime.getMonth() - 1);
        } else {
            $scope.thisYTime = $scope.thisYTime.setMonth($scope.thisYTime.getMonth() - 2);
        }
        $scope.thisYTimeM = $filter('date')($scope.thisYTime, 'MM');

        // data
        function getDate() {
            let deferred = $q.defer();

            $scope.sendRecv("CRM615", "getDate", "com.systex.jbranch.app.server.fps.crm615.CRM615InputVO", {
                    'cust_id': $scope.cust_id,
                    'ao_code': $scope.aoCode
                },
                function (tota, isError) {
                    if (!isError) {

                        if (tota[0].body != null) {

                            //$scope.oneYAgoTEXT = tota[0].body.oneYAgoFee; //  過去12個月
                            //$scope.oneRTimeTEXT = tota[0].body.lastYearFee; // 最近一年
                            //$scope.thisYTimeTEXT = tota[0].body.thisYearFee; // 今年以來(系統自己算)
                            //前二年(系統自己算)
                            $scope.twoYAgoTEXT = tota[0].body.twoYAgoFee;
                            //今年以來(系統自己算)
                            $scope.oneYAgoTEXT = tota[0].body.oneYAgoFee;
                            // 最近一年投保
                            $scope.thisYInsure = tota[0].body.thisYInsure;

                            //#0113
                            $scope.DATA_PERIOD_LAST_YEAR = tota[0].body.DATA_PERIOD_LAST_YEAR;
                            $scope.DATA_PERIOD_NEWEST_YEAR = tota[0].body.DATA_PERIOD_NEWEST_YEAR;
                            //#0504
                            $scope.prftLastCON = tota[0].body.PRFT_LAST_YEAR_NOTE;
                            $scope.prftNewestCON = tota[0].body.PRFT_NEWEST_YEAR_NOTE;
                            deferred.resolve();
                        }

                    }
                });
            return deferred.promise;
        }

        // 貢獻度
//		function getConDegree(data) {
//			if (data >= 300000)
//				return "E級客戶";
//			else if (data >= 100000)
//				return "I級客戶";
//			else if (data >= 30000)
//				return "P級客戶";
//			else if (data >= 0)
//				return "O級客戶";
//			else
//				return "S級客戶";
//		}

        // CBS 需求：前一年與過去 12 個月改為從明細表資料統計 (原本從 Java 計算)
        function extraCalculate(data) {
            // 「過去 12 個月」計算區間從原本的「以上個月為基準再往前12個月」改為取「明細表的前 12 個月」（需確認資料日期為降冪排列）
            // 以避免每月排程 BTCRM6103 執行時間（20號）前後出現區間落差。
            let [start, end] = $scope.DATA_PERIOD_NEWEST_YEAR.split('-');

            let prftNewestYearList = data
                .map(each => each.DATE_TIME)
                .slice(0, 12)
                // 並非每個月都有貢獻度資料，因此取得 TBCRM_CUST_CON_NOTE 求得的區間值「DATA_PERIOD_NEWEST_YEAR」排除不相干的月份
                .filter(date => start <= date && date <= end);

            // 前一年列表
            let prevYearBase = new Date(new Date().getFullYear() - 1, 0);
            let prevYearList = Array.from(Array(12), (_, i) => new Date(prevYearBase.setMonth(i)))
                .map(d => $filter('date')(d, 'yyyyMM'));
            let sum = (data, predicate) => data.filter(each => predicate(each.DATE_TIME))
                .map(each => each.INVAPRU + each.DEPAPRU) // 合計貢獻 = 投保貢獻 + 存款貢獻
                .reduce((prev, next) => prev + next, 0);

            //前一年
            $scope.prftLastYear = sum(data, dataTime => prevYearList.contains(dataTime));
            // 前一年貢獻度
            //$scope.prftLastCON = getConDegree($scope.prftLastYear);
            //過去12個月
            $scope.prftNewestYear = sum(data, dataTime => prftNewestYearList.contains(dataTime));
            // 過去12個月貢獻度
            //$scope.prftNewestCON = getConDegree($scope.prftNewestYear);
        }

        function inquire() {
            let deferred = $q.defer();

            // 貢獻度明細表
            $scope.sendRecv("CRM615", "inquire", "com.systex.jbranch.app.server.fps.crm615.CRM615InputVO", {
                    'cust_id': $scope.cust_id,
                    'ao_code': $scope.aoCode
                },
                function (tota, isError) {
                    if (!isError) {
                        if (tota[0].body.resultList == null) {
                            return;
                        } else {
                            $scope.resultList = tota[0].body.resultList;
                            $scope.outputVO = tota[0].body;

                            deferred.resolve();
                        }
                    }
                });
            return deferred.promise;
        }

        function init() {
            $q.all([getDate(), inquire()])
                .then(() => {
                    extraCalculate($scope.resultList);
                })
        }
        init();

        $scope.getSum = function (list, key) {
            if (list == null)
                return;

            var sum = 0;
            for (var i = 0; i < list.length; i++) {
                sum += Number(list[i][key]);
            }
            return Number(sum);
        }

    });
