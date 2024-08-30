/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS810Controller',
  function ($rootScope, $scope, $controller, sysInfoService, ngDialog, getParameter, $confirm, $q) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPS810Controller';

    /* parameter */
    $scope.total = {
      Y: {
        empCnt: 0,
        custCnt: 0,
        amountCnt: 0,
      },
      M: {
        empCnt: 0,
        custCnt: 0,
        amountCnt: 0,
      }
    };
    // from 800
    // $scope.priID = sysInfoService.getPriID();
    // $scope.regionID = sysInfoService.getRegionID();
    // $scope.areaID = sysInfoService.getAreaID();
    // $scope.branchID = sysInfoService.getBranchID();
    // SPP, INV
    // $scope.planType; from 800
    // -1: 空, 1: 理專,2: 主管,3: 總行
    // $scope.roleType; from 800

    // $scope.regions; from800

    var mapping = {
      office: {
        regionCnt: '人數',
        regionPct: '比例',
        wholePct: '比例'
      },
      custByRoleType: {
        1: {
          regionCnt: '轄下已規劃客戶數',
          regionPct: '各級客戶已規劃比例',
          wholePct: '同級理專各級客戶規劃比例'
        },
        2: {
          regionCnt: '轄下有使用的客戶人數',
          regionPct: '轄下各級客戶使用比例',
          wholePct: '全行各級客戶使用比例'
        }
      },
      cust: {
        CNT_V: '私人',
        CNT_M: '白金',
        CNT_B: '個人',
      },
      emp: {
        regionCnt: '轄下有使用的理專人數',
        regionPct: '轄下各級理專使用比例',
        wholePct: '全行各級理專使用比例'
      },
      // from 800
      // roleType: {
      //   1: ['002'],
      //   2: ['009', '010', '011', '012', '013'],
      //   3: ['000']
      // },
      // path: {
      //   // detail path
      //   empCnt: 'assets/txn/FPS813/FPS813.html',
      //   custCnt: 'assets/txn/FPS814/FPS814.html',
      //   amountCnt: 'assets/txn/FPS815/FPS815.html',
      //   roleType: {
      //     1: 'assets/txn/FPS816/FPS816.html',
      //     2: 'assets/txn/FPS817/FPS817.html',
      //     3: 'assets/txn/FPS818/FPS818.html',
      //   }
      // }
    };
    /* init */
    $scope.init = function () {
      $scope.total = {
        Y: {
          empCnt: 0,
          custCnt: 0,
          amountCnt: 0,
        },
        M: {
          empCnt: 0,
          custCnt: 0,
          amountCnt: 0,
        }
      };
    };

    /* param */

    /* main function */
    var inquire = function (roleType) {
      $scope.sendRecv('FPS810', 'inquire', 'com.systex.jbranch.app.server.fps.fps810.FPS810InputVO', {
          regionID: roleType === 2 ? $scope.regionID : undefined,
          branchID: roleType === 1 ? $scope.branchID : undefined,
          planType: roleType === 3 ? $scope.planType : undefined
        },
        function (tota, isError) {
          if (!isError) {
            $scope.init();
            $scope.paramList = tota[0].body;
            console.log($scope.paramList);
            var isOffice = $scope.roleType === 3;
            /**
             * func:
             * empCount: array
             * wholeEmpCount: array
             * RegionEmpCount: array
             * titleMapping: object
             * totalCount: object
             * isOffice: boolean
             */

            // ~月
            $scope.empCntM = calEmp(
              $scope.paramList.empCntM,
              $scope.paramList.wholeEmpTotalM,
              $scope.paramList.regionEmpTotalM,
              isOffice ? mapping.office : mapping.emp,
              $scope.total.M,
              isOffice
            );

            $scope.custCntM = calCust(
              $scope.paramList.custCntM,
              $scope.paramList.wholeCustTotalM,
              $scope.paramList.regionCustTotalM,
              isOffice ? mapping.office : Object.assign(mapping.cust, mapping.custByRoleType[$scope.roleType]),
              $scope.total.M,
              isOffice
            );
            $scope.amountCntM = calAmount(
              $scope.paramList.amountCntM,
              $scope.total.M,
              isOffice
            );
            // ~年
            $scope.empCntY = calEmp(
              $scope.paramList.empCntY,
              $scope.paramList.wholeEmpTotalY,
              $scope.paramList.regionEmpTotalY,
              isOffice ? mapping.office : mapping.emp,
              $scope.total.Y,
              isOffice
            );
            $scope.custCntY = calCust(
              $scope.paramList.custCntY,
              $scope.paramList.wholeCustTotalY,
              $scope.paramList.regionCustTotalY,
              isOffice ? mapping.office : Object.assign(mapping.cust, mapping.custByRoleType[$scope.roleType]),
              $scope.total.Y,
              isOffice
            );
            $scope.amountCntY = calAmount(
              $scope.paramList.amountCntY,
              $scope.total.Y,
              isOffice
            );
            // console.log($scope.custCntY);
            return true;
          }
          $scope.showErrorMsg(tota);
        }
      );
    };

    $scope.go = function (where, data) {
      var len = where.length;
      var type = where.slice(len - 1, len);
      var key = where.slice(0, len - 1);
      if (key === 'amountCnt' && $scope.roleType === 1) {
        var today = new Date();
        var before = new Date();
        switch (type) {
          case 'M':
            before.setDate(1);
            break;
          case 'Y':
            before.setFullYear(before.getFullYear() - 1);
            break;
        }

        $scope.connector('set', 'amountCntMore', {
          sDate: before,
          eDate: today
        });
        $scope.tab('4');
        return true;
      }
      openDialog($scope.mapping.path[key], type, data, $scope.roleType);
    };

    var openDialog = function (path, type, data, roleType) {
      var dialog = ngDialog.open({
        template: path,
        className: path.slice(11, 17),
        controller: ['$scope', function ($scope) {
          $scope.type = type;
          $scope.data = data;
          $scope.roleType = roleType;
        }]
      });

      dialog.closePromise.then(function (data) {
        if (typeof (data.value) == 'object') {
          if (data.value.length > 0) {
            console.log('this is callback');
          }
        }
      });
    };

    /* helping function */
    var calEmp = function (cnt, whole, region, paramMap, total, isOffice, isZero) {
      // var total = {
      //   cnt: 0,
      //   region: 0,
      //   whole: 0
      // };

      // debugger;
      var initCnt = [{
        FC1: 0,
        FC2: 0,
        FC3: 0,
        FC4: 0,
        FC5: 0,
        REGION_ID: '-1',
        REGION_NAME: '-1',
      }];

      var result = [];
      (cnt && cnt.length > 0 ? cnt : initCnt).forEach(function (row, i) {
        // 過濾空值 多退
        if (!row.REGION_NAME) {
          return false;
        }

        // 處
        var newList = [];

        // 有使用理專人數
        // regionCnt as rc
        var rcObj = {};
        var rcTotal = 0;
        rcObj.title = paramMap.regionCnt;
        rcObj.region = row.REGION_NAME;
        for (var j = 1; j <= 5; j++) {
          rcObj['FC' + j] = row['FC' + j] || 0;
          rcTotal += row['FC' + j] || 0;
        }
        rcObj.cntTotal = rcTotal;
        rcObj.total = rcTotal;
        newList.push(rcObj);

        // 有使用理專比例
        // regionPct as rp
        var rpObj = {};
        var rpTotal = 0;
        rpObj.title = paramMap.regionPct;
        rpObj.region = row.REGION_NAME;
        rpObj.unit = '%';
        for (var k = 1; k <= 5; k++) {
          rpObj['FC' + k] = Math.round((row['FC' + k] / region[i]['FC' + k]) * 100) || 0;
          rpTotal += region[i]['FC' + k] || 0;
        }
        rpObj.cntTotal = rpTotal;
        rpObj.total = Math.round((rcTotal / rpTotal) * 100) || 0;
        newList.push(rpObj);

        if (!isOffice) {
          // wholePct as wp
          var wpObj = {};
          var wpTotal = 0;
          wpObj.title = paramMap.wholePct;
          wpObj.region = row.REGION_NAME;
          wpObj.unit = '%';
          for (var m = 1; m <= 5; m++) {
            wpObj['FC' + m] = Math.round((row['FC' + m] / whole[0]['FC' + m]) * 100) || 0;
            wpTotal += whole[0]['FC' + m] || 0;
          }
          wpObj.cntTotal = wpTotal;
          wpObj.total = Math.round((rcTotal / wpTotal) * 100) || 0;
          newList.push(wpObj);
        }
        // total
        total.empCnt += rcTotal || 0;
        // result
        result.push(newList);
      });

      if (isZero) {
        return result[0];
      }

      // 少補
      if ($scope.roleType === 3 && $scope.regions.length !== result.length) {
        var zeroIndex = 0;
        var reFormList = [];
        $scope.regions.forEach(function (item, m) {
          if (!cnt[zeroIndex] || cnt[zeroIndex].REGION_ID !== item.REGION_ID) {
            var zeroObj = {};
            zeroObj.REGION_ID = $scope.regions[m].REGION_ID;
            zeroObj.REGION_NAME = $scope.regions[m].REGION_NAME;
            for (var n = 1; n <= 5; n++) {
              zeroObj['FC' + n] = 0;
            }
            var zeroList = calEmp([zeroObj], whole, region, paramMap, total, isOffice, true);
            reFormList.push(zeroList);
          } else {
            reFormList.push(result[zeroIndex]);
            zeroIndex += 1;
          }
        });
        result = reFormList;
      }

      // isOffice 全行
      if (isOffice) {
        var newList = [];
        // total cnt as tc
        var tcObj = {};
        var tcTotal = 0;
        tcObj.title = paramMap.regionCnt;
        tcObj.region = '全行';
        result.forEach(function (row, index) {
          var cntRow = row[0];
          for (var k = 1; k <= 5; k++) {
            if (!tcObj['FC' + k]) {
              tcObj['FC' + k] = 0;
            }
            tcObj['FC' + k] += cntRow['FC' + k] || 0;
            tcTotal += cntRow['FC' + k] || 0;
          }
        });
        tcObj.cntTotal = tcTotal;
        tcObj.total = tcTotal;
        newList.push(tcObj);

        // total pct as tp
        var tpObj = {};
        var tpTotal = 0;
        tpObj.title = paramMap.wholePct;
        tpObj.region = '全行';
        tpObj.unit = '%';
        result.forEach(function (row, index) {
          var cntRow = row[0];
          for (var k = 1; k <= 5; k++) {
            tpObj['FC' + k] = Math.round((tpObj['FC' + k] / whole[0]['FC' + k]) * 100) || 0;
            tpTotal += whole[0]['FC' + k] || 0;
          }
        });
        tpObj.cntTotal = tcTotal;
        tpObj.total = Math.round((tcTotal / tpTotal) * 100) || 0;
        newList.push(tpObj);
        result.push(newList);
      }
      // console.log(result);
      return result;
    };

    var calCust = function (cnt, whole, region, paramMap, total, isOffice) {
      // var total = {
      //   cnt: 0,
      //   region: 0,
      //   whole: 0
      // };
      var result = [];
      cnt.forEach(function (row, i) {
        if (!row.REGION_NAME) {
          return false;
        }
        // 處
        var newList = [];
        // regionCnt as rc
        var rcObj = {};
        var rcTotal = 0;
        rcObj.title = paramMap.regionCnt;
        rcObj.region = row.REGION_NAME;
        rcObj.CNT_V = row.CNT_V || 0;
        rcObj.CNT_A = row.CNT_A || 0;
        rcObj.CNT_B = row.CNT_B || 0;
        rcTotal = (row.CNT_V + row.CNT_A + row.CNT_B) || 0;
        rcObj.cntTotal = rcTotal;
        rcObj.total = rcTotal;
        newList.push(rcObj);

        // regionPct as rp
        var rpObj = {};
        var rpTotal = 0;
        rpObj.title = paramMap.regionPct;
        rpObj.region = row.REGION_NAME;
        rpObj.unit = '%';
        rpObj.CNT_V = Math.round((row.CNT_V / (region[i].CNT_V || 1)) * 100) || 0;
        rpObj.CNT_A = Math.round((row.CNT_A / (region[i].CNT_A || 1)) * 100) || 0;
        rpObj.CNT_B = Math.round((row.CNT_B / (region[i].CNT_B || 1)) * 100) || 0;
        rpTotal = region[i].CNT_V + region[i].CNT_A + region[i].CNT_B;
        rpObj.cntTotal = rpTotal;
        rpObj.total = Math.round((rcTotal / (rpTotal || 1)) * 100) || 0;
        newList.push(rpObj);

        if (!isOffice) {
          // wholePct as wp
          var wpObj = {};
          var wpTotal = 0;
          wpObj.title = paramMap.wholePct;
          wpObj.region = row.REGION_NAME;
          wpObj.unit = '%';
          wpObj.CNT_V = Math.round((row.CNT_V / whole[0].CNT_V) * 100) || 0;
          wpObj.CNT_A = Math.round((row.CNT_A / whole[0].CNT_A) * 100) || 0;
          wpObj.CNT_B = Math.round((row.CNT_B / whole[0].CNT_B) * 100) || 0;
          wpTotal = whole[i].CNT_V + whole[i].CNT_A + whole[i].CNT_B;
          wpObj.cntTotal = wpTotal;
          wpObj.total = Math.round((rcTotal / wpTotal) * 100) || 0;
          newList.push(wpObj);
        }
        // total
        total.custCnt += rcTotal || 0;
        // result
        result.push(newList);
      });

      // isOffice 全行
      if (isOffice) {
        var newList = [];
        // total cnt as tc
        var tcObj = {
          CNT_V: 0,
          CNT_A: 0,
          CNT_B: 0,
        };
        var tcTotal = 0;
        tcObj.title = paramMap.regionCnt;
        tcObj.region = '全行';
        result.forEach(function (row, index) {
          var cntRow = row[0];
          tcObj.CNT_V += cntRow.CNT_V || 0;
          tcObj.CNT_A += cntRow.CNT_A || 0;
          tcObj.CNT_B += cntRow.CNT_B || 0;
        });
        tcObj.cntTotal = tcTotal;
        tcObj.total = tcTotal;
        newList.push(tcObj);

        // total pct as tp
        var tpObj = {
          CNT_V: 0,
          CNT_A: 0,
          CNT_B: 0,
        };
        var tpTotal = 0;
        tpObj.title = paramMap.wholePct;
        tpObj.region = '全行';
        tpObj.unit = '%';
        result.forEach(function (row, index) {
          var cntRow = row[0];
          tpObj.CNT_V += Math.round((cntRow.CNT_V / whole[0].CNT_V) * 100) || 0;
          tpObj.CNT_A += Math.round((cntRow.CNT_A / whole[0].CNT_A) * 100) || 0;
          tpObj.CNT_B += Math.round((cntRow.CNT_B / whole[0].CNT_B) * 100) || 0;
        });
        tpObj.cntTotal = tcTotal;
        tpObj.total = Math.round((tcTotal / tpTotal) * 100) || 0;
        newList.push(tpObj);
        result.push(newList);
      }

      return result;
    };

    var calAmount = function (cnt, total, isOffice) {
      var result = [];
      cnt.forEach(function (row, i) {
        if (!row.REGION_NAME) {
          return false;
        }
        var obj = {};
        obj.region = row.REGION_NAME;
        obj.rPCH_AMOUNT = Math.round(row.PCH_AMOUNT / 1000);
        obj.rN_PCH_AMOUNT = Math.round(row.N_PCH_AMOUNT / 1000);
        obj.rPCH_FEE = Math.round(row.PCH_FEE / 1000);
        obj.total = Math.round((row.PCH_AMOUNT + row.N_PCH_AMOUNT) / 1000);

        // total
        total.amountCnt += obj.total || 0;
        // result
        result.push(obj);
      });

      if (isOffice) {
        var obj = {};
        obj.region = '全行';
        obj.rPCH_AMOUNT = Math.round(cnt.reduce(function (a, b) {
          return a + b.PCH_AMOUNT;
        }, 0) / 1000);
        obj.rN_PCH_AMOUNT = Math.round(cnt.reduce(function (a, b) {
          return a + b.N_PCH_AMOUNT;
        }, 0) / 1000);
        obj.rPCH_FEE = Math.round(cnt.reduce(function (a, b) {
          return a + b.PCH_FEE;
        }, 0) / 1000);
        obj.total = Math.round(cnt.reduce(function (a, b) {
          return a + b.PCH_AMOUNT + b.N_PCH_AMOUNT;
        }, 0) / 1000);
        result.push(obj);
      }

      return result;
    };

    /* watch function */
    // $scope.$watch(function () {
    //   return $('section:first-of-type .section-body', cntMEl).height();
    // }, function (val) {
    //   $('section:not(:first-of-type) .section-body', cntMEl).height(val);
    // });
    $scope.$watch(function () {
      return $scope.planType;
    }, function (val) {
      inquire($scope.roleType);
    });

    /* main progress */
    $scope.init();
    inquire($scope.roleType);
  }
);
