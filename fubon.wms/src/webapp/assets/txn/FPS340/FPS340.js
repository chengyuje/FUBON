/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS340Controller',
  function ($scope, $controller, ngDialog, $q, $confirm, FPSUtilsService) {
    $controller('BaseController', {
      $scope: $scope
    });

    $scope.controllerName = 'FPS340Controller';

    //目標-下單購物車先反灰，配合APP上線再打開 (#6291)
    $scope.btnDisable = true;
    
    $scope.today = (new Date()).yyyyMMdd("/");
    $scope.todayPrint = (new Date()).yyyyMMdd('/');
    /* parameter */
    var fps300 = $scope.fps300;
    $scope.recommendations = fps300.getRecommendations();
    $scope.marketOverview = fps300.getMarketOverview();
    $scope.paramList = fps300.getParamList();
    //基金->保險排序
    $scope.paramList = $scope.paramList.sort(function (a, b) {
      return a.P_TYPE - b.P_TYPE;
    });
    $scope.riskType = fps300.custData.KYC_LEVEL;
    if (fps300.custData.kycDisable) {
      $scope.kycDisable = true;
    } else if (parseInt($scope.riskType.substring(1, 2)) < parseInt($scope.recommendations.CUST_RISK_ATTR_NAME.substring(1, 2))) {
      $scope.kycDisable = true;
    }
    $scope.mapping = {};
    $scope.isCreate = fps300.getPlanID() ? false : true;
    //不顯示筆圖案
    $scope.isPencil = "N";
    // print seq once per time
    $scope.connector('set', 'FPS340PrintSEQ', -1);

    var pathMap = {
      printPreview: './assets/txn/FPS340/FPS340PrintPreview.html',
      stepConfirm: './assets/txn/FPS240/FPS240Confirm.html',
      warnings: './assets/txn/FPS240/FPS240Warning.html',
      SOT: './assets/txn/FPS/FPS_SOT.html',
      next: '',
      1: 'SOT110',
      2: 'SOT120'
    };

    var charts = {
      stockChart: null,
      classChart: null
    };

    $scope.chartTables = {
      stockChart: [],
      classChart: []
    };

    $scope.p_charts = {
      stockChart: [],
      classChart: []
    }

    var sortOrder = {
      fundName: {
        股票型: 1,
        債券型: 2,
        平衡型: 3,
        其他: 4
      }
    };

    // getXML
    var param = function () {
      var deferred = $q.defer();

      fps300.getXmlParam([
        'CUST.RISK_ATTR',
        'FPS.PROD_TYPE',
        'FPS.PARAM_STATUS',
        'SYS.COMMON_YN',
        'PRD.PRD_RR',
        'FPS.PARAM_PTYPE',
        'PRD.PRD_STATUS',
        'CUST.RISK_ATTR',
        'FPS.INV_PRD_TYPE',
        'PRD.CURRENCY',
        'FPS.BUY_CURRENCY',
        'FPS.TXN_TYPE',
        'FPS.INV_TYPE'
      ]).then(function (mapping) {
        $scope.mapping = Object.assign($scope.mapping, mapping);
        $scope.mapping.statusMap = {
//          '1': 'PRINT_ORDER', //客戶有下單意願
          '1': 'PRINT_THINK', //客戶有下單意願
          '0': 'PRINT_THINK', //暫存，客戶需考慮
          '-1': 'PRINT_REJECT' //結束，客戶拒絕
        };
        $scope.mapping.fullProdType = {
          MFD: '基金投資注意事項',
          INSI: '連結全委帳戶之投資型保險投資注意事項'
        };
        $scope.mapping.noticeFlags = {
          MFD: false,
          INSI: false,
        };
        $scope.mapping.noticeTypes = {
          0: 'MFD',
          1: 'INSI',
        };

        deferred.resolve('success');
      }, function (err) {
        deferred.reject(false);
      });

      $scope.sendRecv('FPS240', 'getParameter', 'com.systex.jbranch.app.server.fps.fps240.FPS240InputVO', {
          planId: fps300.getPlanID()
        },
        function (tota, isError) {
          if (!isError) {
            var notice = {};
            var noticeType = [];
            var noticeFlag = {};
            
            tota[0].body.outputList.forEach(function (row) {
              if (row.PRD_TYPE === 'FUND') row.PRD_TYPE = 'MFD';
              else if (row.PRD_TYPE === 'BOND') row.PRD_TYPE = 'BND';
              else if (row.PRD_TYPE === 'INSI1' || row.PRD_TYPE === 'INSI2') row.PRD_TYPE = 'INSI';

              var tmp = (row.FONT || '').split('#');
              row.COLOR = tmp.length > 1 ? '#' + tmp[1] : undefined;
              row.FONT = (row.FONT).split('#')[0];

              // 篩選注意組合
              if (noticeType.indexOf(row.PRD_TYPE) < 0) {
            	noticeType.push(row.PRD_TYPE);            		
              }
              // 組注意資料列
              if (notice[row.PRD_TYPE] === undefined) {
                notice[row.PRD_TYPE] = [row];
                noticeFlag[row.PRD_TYPE] = false;
              } else {
                notice[row.PRD_TYPE].push(row);
                noticeFlag[row.PRD_TYPE] = false;
              }
            });
            
            var noticeTypeAll = [];
            var noticeFlagAll = {};
            
            if(fps300.custData.SIGN_AGMT_YN === 'N') {
        		noticeTypeAll.push('MFD');
        		noticeTypeAll.push('INSI');
        		noticeFlagAll = {
    				'MFD' : true,
    				'INSI' : true
        		}
        	} else {
        		noticeTypeAll = noticeType;
                noticeFlagAll = noticeFlag;
        	}
            $scope.mapping.noticeTypeAlls = noticeTypeAll;
            $scope.mapping.noticeFlagAlls = noticeFlagAll;

            $scope.mapping.noticeList = notice;
            $scope.mapping.noticeTypes = noticeType;
            $scope.mapping.noticeFlags = noticeFlag;
            $scope.mapping.noticeToggles = false;
            return true;
          }
          deferred.reject(false);
        });

      return deferred.promise;
    };

    /* init */
    $scope.init = function () {
      $scope.inputVO = {};
    };

    $scope.inquire = function () {
      drawDoughnut();
      $scope.sendRecv('FPS340', 'inquire', 'com.systex.jbranch.app.server.fps.fps340.FPS340InputVO', {
          planId: fps300.getPlanID(),
          custId: fps300.inputVO.custID,
          commRsYn: $scope.isCreate,
          riskType: $scope.riskType,
          prdList: $scope.paramList
        },
        function (tota, isError) {
          if (!isError) {
            // 有無投保
            $scope.hasIns = tota[0].body.hasInvest;
            $scope.planDate = tota[0].body.planDate;

            //波動度DB來
            $scope.volatilityDB = parseInt(tota[0].body.recoVolaility);

            // 商品
            $scope.manualList = tota[0].body.manualList;

            // 前言
            var briefList = tota[0].body.briefList || [];
            $scope.brief = briefList.length > 0 ? briefList[0].CONTENT : '';
            // 年度報酬
            $scope.yearRateList = tota[0].body.yearRateList;
            // 歷史年度平均報酬率
            $scope.historyYRate = tota[0].body.historyYRate === null ? '--' : ((Number(tota[0].body.historyYRate) * 100) || 0).toFixed(2);
            // 年化波動率
            $scope.volatility = tota[0].body.volatility === null ? '--' : ((Number(tota[0].body.volatility) * 100) || 0).toFixed(2);
            // 滿一年波波率
            $scope.fullyearVolatility = tota[0].body.fullYearVolatility === null ? '--' : ((Number(tota[0].body.fullYearVolatility) * 100) || 0).toFixed(2);

            $scope.connector('set', 'fpsPrevBusiDay', tota[0].body.preBusiDay);
            
            // 小財神
            $scope.warning = [];
            if (($scope.volatility === '--' ? 0 : $scope.volatility) > $scope.volatilityDB)
              $scope.warning.push('基金+投資型保險年化波動率超過投組警示值' + $scope.volatilityDB +
                '%，提醒您，有可能將於次月月初落入投組波動度警示值名單中。');

            if ($scope.warning.length > 0) {
              $scope.go('warning');
            }

            // 圖片
            $scope.rptPicture = tota[0].body.rptPicture;

            // 基金績效
            $scope.MFDPerformanceList = tota[0].body.MFDPerformanceList.map(function (row) {
              row.RETURN_3M = row.RETURN_3M ? Math.round(row.RETURN_3M * 100) / 100 : '--';
              row.RETURN_6M = row.RETURN_6M ? Math.round(row.RETURN_6M * 100) / 100 : '--';
              row.RETURN_1Y = row.RETURN_1Y ? Math.round(row.RETURN_1Y * 100) / 100 : '--';
              row.RETURN_2Y = row.RETURN_2Y ? Math.round(row.RETURN_2Y * 100) / 100 : '--';
              row.RETURN_3Y = row.RETURN_3Y ? Math.round(row.RETURN_3Y * 100) / 100 : '--';
              row.VOLATILITY = row.VOLATILITY ? Math.round(row.VOLATILITY * 100) / 100 : '--';
              return row;
            });
            // set notice flag
            $scope.invPct = 0;
            $scope.invAmt = 0;
            $scope.paramList.forEach(function (row) {
              if (row.PTYPE === 'INS') {
            	$scope.mapping.noticeFlags['INSI'] = true;
            	if ($scope.mapping.noticeTypeAlls.indexOf('INSI') < 0) {
            		$scope.mapping.noticeTypeAlls.push('INSI');            		
                }
              } else {
                $scope.mapping.noticeFlags[row.PTYPE] = true;
              }
              
              //商品明細-配置比例、約當金額
              $scope.invPct += row.INV_PERCENT;
              $scope.invAmt += row.PURCHASE_TWD_AMT;
            });
            
            if(fps300.custData.SIGN_AGMT_YN === 'Y') {
          	  $scope.mapping.noticeFlagAlls = $scope.mapping.noticeFlags;
            }
            
            // 取得查詢結果_績效追蹤與投組調整Tab
            $scope.achivedParamList = tota[0].body.achivedParamList;
            angular.forEach($scope.achivedParamList, function (value, key) {
              $scope.HIT_RATE_DESC = value.HIT_RATE_DESC;
              $scope.HIT_RATE = value.HIT_RATE;
              $scope.MARKET_VALUE = value.MARKET_VALUE;
              $scope.AMT_TARGET = value.AMT_TARGET;
              $scope.RETURN_RATE = value.RETURN_RATE;
              $scope.INV_AMT_CURRENT = value.INV_AMT_CURRENT;
            });
            return true;
          }
          $scope.showErrorMsg(tota);
          return false;
        });
    };

    var drawDoughnut = function () {
      var temp = {};
      var temp2 = {};
      var index1 = 0;
      var index2 = 0;

      $scope.paramList.forEach(function (row) {
        // 股價配置
        if (temp[row.FUND_TYPE_NAME] === undefined) {
          temp[row.FUND_TYPE_NAME] = {
            y: row.INV_PERCENT,
            legendText: row.FUND_TYPE_NAME,
            indexLabelLineColor: 'transparent',
            indexLabel: row.INV_PERCENT + '%'
          };
          index1++;
        } else {
          temp[row.FUND_TYPE_NAME].y += row.INV_PERCENT;
          temp[row.FUND_TYPE_NAME].indexLabel = temp[row.FUND_TYPE_NAME].y + '%';
        }
        // 類別配置
        if (temp2[row.NAME] === undefined) {
          temp2[row.NAME] = {
            y: row.INV_PERCENT,
            legendText: row.NAME,
            indexLabelLineColor: 'transparent',
            indexLabel: row.INV_PERCENT + '%'
          };
          index2++;
        } else {
          temp2[row.NAME].y += row.INV_PERCENT;
          temp2[row.NAME].indexLabel = temp2[row.NAME].y + '%';
        }
      });

      var dataPoints = Object.keys(temp).map(function (key) {
        return temp[key];
      });
      // 股票/債券及平衡排序固定
      dataPoints = dataPoints.sort(function (a, b) {
        return sortOrder.fundName[a.legendText] > sortOrder.fundName[b.legendText];
      });

      // 依佔比高到低排序
      var otherIndex = 3;
      var dataPoints_2 = Object.keys(temp2).map(function (key) {
        return temp2[key];
      }).sort(function (a, b) {
        return b.y - a.y;
      }).reduce(function (a, b, i) {
        if (i < 4) {
          if (b.legendText === '其他') {
            otherIndex = i;
          }
          a.push(b);
        } else {
          a[otherIndex].y += b.y;
          a[otherIndex].indexLabel = a[otherIndex].y + '%';
          a[otherIndex].legendText = '其他';
        }
        return a;
      }, []).sort(function (a, b) {
        return b.y - a.y;
      });

      var chartData = {
        colorSet: 'blue',
        backgroundColor: 'transparent',
        data: [{
          radius: '100%',
          innerRadius: '40%',
          type: 'doughnut',
          showInLegend: false,
          dataPoints: dataPoints
        }]
      };

      var chartData_2 = {
        colorSet: 'yellow',
        backgroundColor: 'transparent',
        data: [{
          radius: '100%',
          innerRadius: '40%',
          type: 'doughnut',
          showInLegend: false,
          dataPoints: dataPoints_2
        }]
      };

      $scope.p_charts.stockChart = angular.copy(chartData);
      $scope.p_charts.classChart = angular.copy(chartData_2);
      for (var i = 0; i < $scope.p_charts.stockChart.data["0"].dataPoints.length; i++) {
        $scope.p_charts.stockChart.data["0"].dataPoints[i].legendText = "(" + (i + 1) + ") " +
          $scope.p_charts.stockChart.data["0"].dataPoints[i].legendText;
        $scope.p_charts.stockChart.data["0"].dataPoints[i].indexLabel = "(" + (i + 1) + ") " +
          $scope.p_charts.stockChart.data["0"].dataPoints[i].indexLabel;
      }
      for (var i = 0; i < $scope.p_charts.classChart.data["0"].dataPoints.length; i++) {
        $scope.p_charts.classChart.data["0"].dataPoints[i].legendText = "(" + (i + 1) + ") " +
          $scope.p_charts.classChart.data["0"].dataPoints[i].legendText;
        $scope.p_charts.classChart.data["0"].dataPoints[i].indexLabel = "(" + (i + 1) + ") " +
          $scope.p_charts.classChart.data["0"].dataPoints[i].indexLabel;
      }

      charts.stockChart = null;
      charts.stockChart = new CanvasJS.Chart('stockChart', chartData);
      charts.stockChart.render();
      setChartTable('stockChart');

      charts.classChart = null;
      charts.classChart = new CanvasJS.Chart('classChart', chartData_2);
      charts.classChart.render();
      setChartTable('classChart');

      $scope.calInvestment();
      $scope.calModelPorfolio();
    };

    //投組試算 FPS323 start
    $scope.calInvestment = function () {
      var formatedData = FPSUtilsService.JLB.formatJLBRow($scope.paramList, {
        AMT: 'NTD_PERCENT',
        TYPE: 'PTYPE'
      });
      // 計算單筆金額 計算定額金額
      var oneTime = 0;
      var perMonth = 0;
      $scope.paramList.forEach(function (row) {
        if (row.PTYPE === 'INS')
          oneTime += parseInt(row.NTD_PERCENT, 10);
        else if (row.INV_TYPE === '1')
          oneTime += parseInt(row.NTD_PERCENT, 10);
        else if (row.INV_TYPE === '2')
          perMonth += parseInt(row.NTD_PERCENT, 10);
      });

      // FPS323 績效模擬
      $scope.$broadcast('FPS323_drawMonteCarlo', {
        custID: fps300.inputVO.custID,
        planID: fps300.getPlanID(),
        ret: formatedData,
        beginAmount: parseInt(oneTime, 10),
        eachYear: parseInt(perMonth, 10) * 12,
        year: parseInt($scope.recommendations.PLANHEAD, 10),
        target: parseInt($scope.recommendations.TARGET, 10)
      });
    };
    //投組試算 FPS323 end

    $scope.calModelPorfolio = function () {
      if (!$scope.isCreate) {
    	  var formatedData = FPSUtilsService.JLB.formatJLBRow($scope.paramList, {
    		  AMT: 'NTD_PERCENT',
   	       	  TYPE: 'PTYPE'
    	  });
    	  $scope.$broadcast('FPS330_drawHistoryChart', formatedData);
      }
    };

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

    // 跳轉到SOT、IOT
    $scope.chgUrl = function (row) {
      openDialog(pathMap.SOT, {
        where: row.PTYPE === 'MFD' ? pathMap[row.InvType] : 'IOT110',
        transact: row
      }, 'SOT');
    };

    // 去那兒
    $scope.go = function (where) {
      switch (where) {
        case 'print':
          $scope.sendRecv('FPS340', 'updateHead', 'com.systex.jbranch.app.server.fps.fps340.FPS340InputVO', {
              planId: fps300.getPlanID(),
              planStatus: "PRINT_THINK"
            },
            function (tota, isError) {
              if (!isError) {
                $scope.recommendations.PLAN_STATUS = "PRINT_THINK";
                openDialog(pathMap.printPreview, {
                  fromFps: '340', 
                  planDate: $scope.planDate,
                  MFDPerformanceList: $scope.MFDPerformanceList,
                  brief: $scope.brief,
                  custInfo: $scope.custInfo,
                  rptPicture: $scope.rptPicture,
                  volatility: $scope.fullyearVolatility,
                  manualList: $scope.manualList,
                  mapping: $scope.mapping,
                  fpsPrint: $scope.p_charts,
                  hasIns: $scope.hasIns,
                  fps300: fps300,
                  paramList: $scope.paramList,
                  recommendations: $scope.recommendations
                }, 'print');
                return true;
              }
              return false;
            });
          break;
        case 'next':
          var checkPrint = false;
          $scope.sendRecv('FPS340', 'checker', 'com.systex.jbranch.app.server.fps.fps340.FPS340InputVO', {
              planId: fps300.getPlanID()
            },
            function (tota, isError) {
              if (!isError) {
                checkPrint = (parseInt(tota[0].body) || 0) > 0;
//                if (tota[0].body == "PLAN_STEP") {
//                  $confirm({
//                    text: '尚未產出規劃書，若執行下一步則無法再轉寄/列印規劃書是否繼續?'
//                  }, {
//                    size: 'sm'
//                  }).then(function () {
//                    $scope.sendRecv('FPS340', 'updateHead', 'com.systex.jbranch.app.server.fps.fps340.FPS340InputVO', {
//                        planId: fps300.getPlanID(),
//                        planStatus: "PRINT_THINK"
//                      },
//                      function (tota, isError) {
//                        if (!isError) {
//                          $scope.recommendations.PLAN_STATUS = "PRINT_THINK";
//                          openDialog(pathMap.stepConfirm, null, 'confirm');
//                          return true;
//                        }
//                        return false;
//                      });
//                  }, function () {
//                    // four
//                  });
//                } else {
                  openDialog(pathMap.stepConfirm, $scope.recommendations.PLAN_STATUS, 'confirm');
//                }
              }
            });
          break;
        case 'warning':
          openDialog(pathMap.warnings, $scope.warning, where);
          break;
        default:
          break;
      }
    };

    var openDialog = function (path, data, type) {
      var scope = $scope;
      var dialog = ngDialog.open({
        template: path,
        className: 'FPS200',
        controller: ['$scope', function ($scope) {
          $scope.custID = fps300.inputVO.custID;
          $scope.planID = fps300.getPlanID();
          $scope.data = data;
          $scope.trendPath = scope.mapping.trendPath;
        }]
      });

      dialog.closePromise.then(function (data) {
        if (typeof (data.value) === 'object') {
          if (type == "confirm") {
            popCbFn[type](data.value);
          }
        }
      });
    };

    var popCbFn = {
      confirm: function (value) {
        var action = value.action;
        $scope.sendRecv('FPS340', 'updateHead', 'com.systex.jbranch.app.server.fps.fps340.FPS340InputVO', {
            planId: fps300.getPlanID(),
            planStatus: $scope.mapping.statusMap[action]
          },
          function (tota, isError) {
            if (!isError) {
              $scope.recommendations.PLAN_STATUS = $scope.mapping.statusMap[action];
              return true;
            }
            return false;
          });
      }
    };

    /* main process */
    $scope.init();
    param().then(function (success) {
      $scope.inquire();
    }, function (err) {
      console.log(err);
    });
  }
);
