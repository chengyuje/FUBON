/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS220Controller',
  function ($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $timeout, $confirm, getParameter, $q, sysInfoService) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPS220Controller';
    $scope.dragOptions = {
      label: {
        front: '0%',
        end: '100%',
        unit: '%',
      },
      drag: {
        interval: 1
      },
      showTip: true,
      showLabel: true,
      showBtn: true,
    };
    /** parameter **/
    $scope.connector('set', 'STEP2VO', undefined);
    $scope.connector('set', 'STEP3VO', undefined);
    $scope.kycInvalid = $scope.connector('get', 'kycInvalid') !== false ? true : false;
    $scope.modelFlag = '1';
    $scope.inputVO = {};
    $scope.recoVolatility = 0;
    $scope.stockVolatility = 0;
    var minPriceAmt = 0;
    var price = 0; // 存款部位 A1 + B - C -D
    var cntSISN = 0;

    var modelPortfolioList = [];
    var pathMap = {
      next: 'STEP3',
      prev: 'STEP1'
    };
    $scope.recommend = {
      deposit: 0,
      fixed: 0,
      stock: 0
    };
    $scope.headList = {
      planAmt: 0,
      depositAmt: 0,
      fixedAmt: 0,
      stockAmt: 0,
      depositPct: 0,
      fixedPct: 0,
      stockPct: 0
    };
    var isModel = false;
    var charts = {
      stockChart: null,
      beforeChart: null,
      afterChart: null
    };

    var mapping = {
      keys: [
        'deposit',
        'fixed',
        'stock'
      ],

      keysZhTw: [
        '儲蓄型保險+存款',
        '固定收益商品',
        '基金+ETF+投資型保險'
      ],
    };
    $scope.chartTables = {
      stockChart: [],
      beforeChart: [],
      afterChart: []
    };
    $scope.arrowUp = true;
    $scope.hasIns = false; // 有無投保

    /**
     * STEP1VO
     * CUST_RISK: custInfo.KYC_LEVEL, $scope.connector('get', 'custInfo').KYC_LEVEL;
     * PLAN_ID: $scope.inputVO.planID,
     * PLAN_AMT: $scope.inputVO.planAmt,
     * CUST_ID: $scope.inputVO.custID, $scope.connector('get', 'custID');
     * COMM_RS_YN: custInfo.COMM_RS_YN
     */
    $scope.STEP1VO = $scope.connector('get', 'STEP1VO');
    var riskType = $scope.STEP1VO.CUST_RISK;
    var planID = $scope.STEP1VO.PLAN_ID;
    var custID = $scope.STEP1VO.CUST_ID;
    $scope.inputVO.cust_id = custID;

    // getXML
    var param = function () {
      var deferred = $q.defer();
      $scope.sendRecv('FPS220', 'getDepositParam', 'com.systex.jbranch.app.server.fps.fps220.FPS220InputVO', {},
        function (tota, isError) {
          if (!isError) {
            minPriceAmt = tota[0].body || -1;
            console.log(minPriceAmt);
            deferred.resolve('success');
          }
          deferred.reject(tota);
        });
      return deferred.promise;
    };

    /* init */
    $scope.init = function () {
      var defer = $q.defer();
      if ($scope.STEP1VO !== undefined) {
        inquire(defer);
      } else {
        defer.resolve(true);
      }
      return defer.promise;
    };

    /* main function */
    var inquire = function (defer) {
      $scope.sendRecv('FPS220', 'inquire', 'com.systex.jbranch.app.server.fps.fps220.FPS220InputVO', {
          riskType: riskType,
          planID: planID,
          custID: custID
        },
        function (tota, isError) {
          if (!isError) {
            $scope.inputVO.planAmt = $scope.STEP1VO.PLAN_AMT;
            $scope.marketOverview = tota[0].body.market;
            var paramList = tota[0].body.outputList || [];
            modelPortfolioList = tota[0].body.initModelPortfolioList || [];
            $scope.recoVolatility = tota[0].body.recoVolatility ? Number(tota[0].body.recoVolatility).toFixed(1) : null;

            $scope.hasIns = tota[0].body.hasInvest;

            if (paramList.length > 0 && $scope.hasIns) {
              $scope.headList.planAmt = paramList[0].PLAN_AMT;
              $scope.headList.depositAmt = paramList[0].ANNUITY_INS_AMT + paramList[0].DEPOSIT_AMT + paramList[0].SOW_AMT -
                paramList[0].CASH_YEAR_AMT - paramList[0].INS_SAV_AMT - paramList[0].INS_POLICY_AMT;
              $scope.headList.fixedAmt = paramList[0].FIXED_INCOME_AMT;
              $scope.headList.stockAmt = paramList[0].FUND_AMT;
              $scope.headList.depositPct = Math.round($scope.headList.depositAmt / $scope.headList.planAmt * 100);
              $scope.headList.fixedPct = Math.round($scope.headList.fixedAmt / $scope.headList.planAmt * 100);
              $scope.headList.stockPct = Math.round((100 - $scope.headList.depositPct - $scope.headList.fixedPct));

              $scope.stockVolatility = tota[0].body.stockVolatility ? (Number(tota[0].body.stockVolatility) * 100 || 0).toFixed(2) : null;
              if ($scope.stockVolatility === null) {
                // $scope.showMsg('計算波動率無共同區間');
              }
              price = paramList[0].DEPOSIT_AMT + paramList[0].SOW_AMT -
                paramList[0].CASH_YEAR_AMT - paramList[0].INS_SAV_AMT - paramList[0].INS_POLICY_AMT;

              // get if has SI SN
              cntSISN = tota[0].body.cntFixed;
            }

            $scope.recommend = {
              deposit: 0,
              fixed: 0,
              stock: 0
            };

            // 是模組比例
            isModel = (paramList[0].PORTFOLIO1_RATIO + paramList[0].PORTFOLIO2_RATIO + paramList[0].PORTFOLIO3_RATIO) <= 0;

            modelPortfolioList.forEach(function (row) {
              if ('1' === row.INV_PRD_TYPE) {
                $scope.recommend.deposit += Number(row.INV_PERCENT);
              } else if ('2' === row.INV_PRD_TYPE) {
                $scope.recommend.fixed += Number(row.INV_PERCENT);
              } else if ('3' === row.INV_PRD_TYPE) {
                $scope.recommend.stock += Number(row.INV_PERCENT);
              }
            });

            if (isModel) {
              if ($scope.hasIns) {
                $scope.inputVO.depositAmt = $scope.headList.depositAmt;
                $scope.inputVO.fixedAmt = $scope.headList.fixedAmt;
                $scope.inputVO.stockAmt = $scope.headList.stockAmt;
                $scope.inputVO.depositPct = $scope.headList.depositPct;
                $scope.inputVO.fixedPct = $scope.headList.fixedPct;
                $scope.inputVO.stockPct = $scope.headList.stockPct;
              } else {
                $scope.recommendModel();
                calAmtByPct();
              }
            } else {
              $scope.inputVO.depositPct = paramList[0].PORTFOLIO1_RATIO;
              $scope.inputVO.fixedPct = paramList[0].PORTFOLIO2_RATIO;
              $scope.inputVO.stockPct = paramList[0].PORTFOLIO3_RATIO;
              $scope.inputVO.depositAmt = paramList[0].PORTFOLIO1_AMT;
              $scope.inputVO.fixedAmt = paramList[0].PORTFOLIO2_AMT;
              $scope.inputVO.stockAmt = paramList[0].PORTFOLIO3_AMT;
              // calAmtByPct();
            }

            defer.resolve(true);
          } else {
            defer.reject(tota);
            $scope.showErrorMsg(tota);
          }
        });
    };

    $scope.go = function (where) {
      switch (where) {
        case 'save':
          save('2')
            .then(function () {
              $scope.showSuccessMsg('ehl_01_common_025');
            });
          break;
        case 'next':
          var flag = true;
          mapping.keys.forEach(function (key) {
            if ($scope.inputVO[key + 'Amt'] < 0 || $scope.inputVO[key + 'Amt'] > $scope.inputVO.planAmt) {
              flag = false;
            }
          });
          if (!flag) return $scope.showErrorMsg('金額不能為負值或超出可供理財規劃金額');

          checkFixed()
            .then(function () {
              chgStep(pathMap[where]);
            }, function () {

            });
          break;
        case 'prev':
          chgStep(pathMap[where]);
          break;
      }
    };

    var save = function (step) {
      var defer = $q.defer();
      if ($scope.amtForm.$dirty || isModel || step) {
        var depositPct = (Math.round($scope.inputVO.depositAmt / $scope.inputVO.planAmt * 10000)) / 100;
        var fixedPct = (Math.round($scope.inputVO.fixedAmt / $scope.inputVO.planAmt * 10000)) / 100;
        var stockPct = 100 - depositPct - fixedPct;

        $scope.sendRecv('FPS220', 'save', 'com.systex.jbranch.app.server.fps.fps220.FPS220InputVO', {
          depositPct: depositPct,
          fixedPct: fixedPct,
          stockPct: stockPct,
          depositAmt: $scope.inputVO.depositAmt,
          fixedAmt: $scope.inputVO.fixedAmt,
          stockAmt: $scope.inputVO.stockAmt,
          planID: $scope.STEP1VO.PLAN_ID,
          action: 'update',
          step: step
        }, function (tota, isError) {
          if (!isError) {
            isModel = false;
            $scope.amtForm.$setPristine();
            defer.resolve(true);
            return true;
          }
          $scope.showErrorMsg(tota);
          defer.reject(false);
          return false;
        });
      } else {
        $timeout(function () {
          defer.resolve(true);
        });
      }

      return defer.promise;
    };

    // check for fixed
    var checkFixed = function () {
      var defer = $q.defer();
      if (Math.round($scope.inputVO.fixedAmt) <= 0 || cntSISN > 0) {
        $timeout(function () {
          defer.resolve(true);
        });
        return defer.promise;
      }

      $scope.sendRecv('FPS220', 'checkFixed', 'com.systex.jbranch.app.server.fps.fps220.FPS220InputVO', {
        fixedAmt: Math.round($scope.inputVO.fixedAmt) || 0,
        custID: custID,
        riskType: riskType
      }, function (tota, isError) {
        if (!isError) {
          var checker = tota[0].body;
          if (checker === 0) {
            $confirm({
              text: '不符固定收益商品最低申購門檻或無適配的固定收益商品，原固定收益部位配置金額將全數轉到「基金+ETF+投資型保險」'
            }).then(function () {
              $scope.inputVO.fixedAmt = 0;
              $scope.balancePercent('fixedAmt');
              $scope.changePie();
              defer.resolve(true);
            }, function () {
              defer.reject(true);
            });
            return true;
          }
          defer.resolve(checker);
          return true;
        }
        $scope.showErrorMsg(tota);
        defer.reject(false);
        return false;
      });
      return defer.promise;
    };

    $scope.changePie = function () {
      $scope.inputVO.stockAmt = $scope.inputVO.planAmt - $scope.inputVO.depositAmt - $scope.inputVO.fixedAmt;
      //現分配金額
      //inputVO.depositAmt
      //inputVO.fixedAmt
      //inputVO.stockAmt
      //現分配比例
      $scope.inputVO.depositPct = Math.round($scope.inputVO.depositAmt / $scope.inputVO.planAmt * 100);
      $scope.inputVO.fixedPct = Math.round($scope.inputVO.fixedAmt / $scope.inputVO.planAmt * 100);
      $scope.inputVO.stockPct = Math.round((100 - $scope.inputVO.depositPct - $scope.inputVO.fixedPct));
      drawDoughnut();
    };

    var chgStep = function (type) {
      //投組ID
      $scope.inputVO.planID = $scope.STEP1VO.PLAN_ID;
      $scope.inputVO.modelPortfolioID = $scope.MODEL_PORTFOLIO_ID;
      $scope.inputVO.modelFlag = $scope.modelFlag;

      var step = type.match(/\d{1}/);
      save(step ? step[0] : undefined)
        .then(function () {
          var vo = {
            MODEL_FLAG: $scope.inputVO.modelFlag,
            PORTFOLIO1_AMT_AFTER: $scope.inputVO.depositAmt,
            PORTFOLIO2_AMT_AFTER: $scope.inputVO.fixedAmt,
            PORTFOLIO3_AMT_AFTER: $scope.inputVO.stockAmt,
            PORTFOLIO1_RATIO_AFTER: $scope.inputVO.depositPct,
            PORTFOLIO2_RATIO_AFTER: $scope.inputVO.fixedPct,
            PORTFOLIO3_RATIO_AFTER: $scope.inputVO.stockPct
          };
          console.info(vo);
          $scope.connector('set', 'STEP2VO', vo);
          $scope.connector('set', 'STEP', type);
        });
    };

    /* sub function */
    $scope.recommendModel = function () {
      $scope.inputVO.depositPct = $scope.recommend.deposit;
      $scope.inputVO.fixedPct = $scope.recommend.fixed;
      $scope.inputVO.stockPct = $scope.recommend.stock;
    };

    // 拖拉Event
    $scope.dragBarOnDrag = function (newPct, target) {
      var pct = Number(newPct);
      if (!pct && pct !== 0) return false;
      $scope.inputVO[target] = Math.round(pct * $scope.inputVO.planAmt / 100);
      $scope.balancePercent(target);
      $scope.changePie();
    };

    // 平衡數值
    $scope.balancePercent = function (type) {
      console.log($scope.inputVO[type]);
      var tmp = parseInt($scope.inputVO[type], 10);
      if (tmp < 0) {
        $scope.inputVO[type] = $scope.inputVO[type] * (-1);
      }

      var max = $scope.inputVO.planAmt;
      if ($scope.inputVO[type] > max) {
        $scope.showErrorMsg('輸入的金額需小於「客戶可供理財規劃金額」，請重新輸入。');
        $scope.inputVO[type] = 0;
      }

      var paramMap = {
        depositAmt: 'fixedAmt',
        fixedAmt: 'depositAmt'
      };

      var total = mapping.keys.reduce(function (cnt, key) {
        return cnt + parseInt($scope.inputVO[key + 'Amt'], 10);
      }, 0);
      var divide = total - max;
      var lastDivide = $scope.inputVO.stockAmt - divide;
      $scope.inputVO.stockAmt = lastDivide < 0 ? 0 : lastDivide;
      $scope.inputVO[paramMap[type]] = lastDivide < 0 ? parseInt($scope.inputVO[paramMap[type]], 10) + lastDivide : $scope.inputVO[paramMap[type]];

      return true;
    };

    // draw chart
    var drawDoughnut = function () {
      $timeout(function () {
        if ($scope.hasIns) {
          beforeDoughut('beforeChart');
          afterDoughut('afterChart');
        } else {
          stockDoughut('stockChart');
        }
      });
    };

    var stockDoughut = function (chartName) {
      var dataPoints = angular.copy(mapping.keys).map(function (item, index) {
        return {
          y: $scope.inputVO[item + 'Pct'],
          legendText: mapping.keysZhTw[index],
          indexLabel: $scope.inputVO[item + 'Pct'] + '%',
          indexLabelLineColor: 'transparent',
        };
      });

      var chartData = {
        colorSet: 'tri',
        backgroundColor: 'transparent',
        data: [{
          radius: '100%',
          innerRadius: '40%',
          type: 'doughnut',
          showInLegend: false,
          dataPoints: dataPoints
        }]
      };

      charts[chartName] = null;
      charts[chartName] = new CanvasJS.Chart(chartName, chartData);
      charts[chartName].render();
      setChartTable(chartName);
    };

    var beforeDoughut = function (chartName) {
      var dataPoints = angular.copy(mapping.keys).map(function (item, index) {
        return {
          y: $scope.headList[item + 'Pct'],
          legendText: mapping.keysZhTw[index],
          indexLabel: $scope.headList[item + 'Pct'] + '%',
          indexLabelLineColor: 'transparent',
        };
      });

      var chartData = {
        colorSet: 'tri',
        backgroundColor: 'transparent',
        data: [{
          innerRadius: '40%',
          radius: '100%',
          type: 'doughnut',
          showInLegend: false,
          dataPoints: dataPoints
        }]
      };

      charts[chartName] = null;
      charts[chartName] = new CanvasJS.Chart(chartName, chartData);
      charts[chartName].render();
      setChartTable(chartName);
      $scope.chartTables[chartName].forEach(function (item, index) {
        item.advice = '--';
        item.amt = $scope.headList[mapping.keys[index] + 'Amt'];
        // 建議
        switch (index) {
          case 0: // deposit
            if (item.value > $scope.recommend[mapping.keys[index]]) {
              item.down = true;
              if (price > minPriceAmt * 10000) {
                item.advice = '降低存款部位';
              }
            }
            break;
          case 1: // fixed
            if (item.value > $scope.recommend[mapping.keys[index]]) {
              item.down = true;
              item.advice = '有獲利可降低部位';
            }
            break;
          case 2: // stock
            if (item.value < $scope.recommend[mapping.keys[index]]) {
              item.down = true;
              item.advice = '增加部位';
            }
            break;
        }
      });
    };

    var afterDoughut = function (chartName) {
      var dataPoints = angular.copy(mapping.keys).map(function (item, index) {
        return {
          y: $scope.inputVO[item + 'Pct'],
          legendText: mapping.keysZhTw[index],
          indexLabel: $scope.inputVO[item + 'Pct'] + '%',
          indexLabelLineColor: 'transparent',
        };
      });

      var chartData = {
        colorSet: 'tri',
        backgroundColor: 'transparent',
        data: [{
          radius: '100%',
          innerRadius: '40%',
          type: 'doughnut',
          showInLegend: false,
          dataPoints: dataPoints
        }]
      };

      charts[chartName] = null;
      charts[chartName] = new CanvasJS.Chart(chartName, chartData);
      charts[chartName].render();
      setChartTable(chartName);
      $scope.chartTables[chartName].forEach(function (item, index) {
        item.amt = $scope.inputVO[mapping.keys[index] + 'Amt'];
        item.advice = (index < 2 ? '≦' : '') + $scope.recommend[mapping.keys[index]] + '%';
      });
    };

    /* helping function */
    // set chart table datas with colorsets and datapoints
    var setChartTable = function (chartName) {
      var chartColor = canvasJsConfig.bgcSets[charts[chartName].options.colorSet];
      var chartData = charts[chartName].options.data[0].dataPoints;
      var colorCnt = chartColor.length;

      $scope.chartTables[chartName] = chartData.map(function (data, index) {
        return {
          color: chartColor[index % colorCnt],
          label: data.legendText,
          value: data.y
        };
      });
    };

    var calAmtByPct = function () {
      $scope.inputVO.depositAmt = Math.round(Number($scope.inputVO.planAmt) * Number($scope.inputVO.depositPct) / Number(100));
      $scope.inputVO.fixedAmt = Math.round(Number($scope.inputVO.planAmt) * Number($scope.inputVO.fixedPct) / Number(100));
      $scope.inputVO.stockAmt = Math.round(Number($scope.inputVO.planAmt) * Number($scope.inputVO.stockPct) / Number(100));
    };

    var maxAmount = function (type) {
      var max = 0;
      switch (type) {
        case 'depositAmt':
          max = $scope.inputVO.planAmt - $scope.inputVO.fixedAmt || 0;
          break;
        case 'fixedAmt':
          max = $scope.inputVO.planAmt - $scope.inputVO.depositAmt || 0;
          break;
        case 'stockAmt':
          max = $scope.inputVO.planAmt;
          break;
        default:
          max = $scope.inputVO.planAmt;
          break;
      }
      return max;
    };

    /* main progress */
    param()
      .then(() => {
        return $scope.init();
      }).then(() => {
        $scope.changePie();
        drawDoughnut();
      });

    console.log('step 2');
  }
);
