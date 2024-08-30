/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPSPrintController',
  function ($scope, $controller, $timeout, FPSUtilsService, $q, getParameter) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPSPrintController';
    var fps300 = $scope.fps300;
    if (typeof (webViewParamObj) != 'undefined') {
    	fps300 = {};
    	fps300.custData = $scope.custInfo;
    	$scope.isReb = true; //true:顯示 單筆投入總額(NT$)、定期投入總額(NT$)
    	$scope.fromApp = true;
    	// 讀取參數檔
    	getParameter.XML(['FPS.INV_TYPE','FPS.TXN_TYPE'],function(tota){
    		if(tota){
    			$scope.mapping.invType = tota.data[tota.key.indexOf('FPS.INV_TYPE')];
        	    $scope.txnTypeList_param = tota.data[tota.key.indexOf('FPS.TXN_TYPE')];
        	    angular.forEach($scope.paramList, function (row, index) {
        	    	row.txnTypeList = $scope.txnTypeList_param;
        	    });
    		}
    	});
    }
    $scope.hitRateGuyText = {
      '1': '目前目標進度狀態為綠燈，達成率80%，達目標。',
      '0': '目前目標進度狀態為黃燈，達成率80%，進度微幅落後應達目標。',
      '-1': '目前目標進度狀態為紅燈，達成率80%，低於應達目標。'
    };
    
    $scope.hitRateGuy = {
      '1': './assets/images/money_SVG/money_happy.png',
      '0': './assets/images/money_SVG/money_shoulder_2.png',
      '-1': './assets/images/money_SVG/money_sweat.png'
    };
    var sortOrder = {
      fundName: {
        股票型: 1,
        債券型: 2,
        平衡型: 3,
        其他: 4
      }
    };
    /* parameter */
    $scope.labelIndex = fps300.custData.SIGN_AGMT_YN === 'Y' ? 8 : 5;
    $scope.path = {
      header: './assets/images/FPS_PDF_Header_3.png',
      noSignAlert: './assets/txn/FPS_PDF_Template/FPSutils_component/noSignAlert.html',
      brief: './assets/txn/FPS_PDF_Template/FPSutils_component/FPS_print_brief.template.html',
      custInfo: './assets/txn/FPS/FPS_CUST.template.html',
      requirement: 'assets/txn/FPS340/FPS340.template.html',

      achievedState: 'assets/txn/FPS400/FPS400_print.template.html',
      achievedState_ps: './assets/txn/FPS_PDF_Template/FPS340_component/achievedState_ps.html',

      rebalanceChart: './assets/txn/FPS_PDF_Template/FPS340_component/rebalanceChart.html',

      portRtnSim: './assets/txn/FPS323/FPS323_P.html',
      portRtnSim_ps: './assets/txn/FPS_PDF_Template/FPS340_component/portRtnSim_ps.html',

      rebalanceProduct: './assets/txn/FPS_PDF_Template/FPS340_component/rebalanceProduct.html',

      historyPerformance: './assets/txn/FPS_PDF_Template/FPS340_component/historyPerformance.html',
      historyPerformance_ps: './assets/txn/FPS_PDF_Template/FPSutils_component/historyPerformance_ps.html',

      productPerformance: './assets/txn/FPS_PDF_Template/FPSutils_component/productPerformance.html',
      productPerformance_ps: './assets/txn/FPS_PDF_Template/FPSutils_component/productPerformance_ps.html',

      historyTransaction: './assets/txn/FPS_PDF_Template/FPS340_component/historyTransaction.html',
      transaction: './assets/txn/FPS_PDF_Template/FPS340_component/transaction.html',

      manual: './assets/txn/FPS_PDF_Template/FPSutils_component/manual.html',
      notice: './assets/txn/FPS_PDF_Template/FPSutils_component/notice.html',
    };

    var YRcolor = canvasJsConfig.YRSet;
    // charts init value
    var charts = {
      a_classChart_p: null,
      a_stockChart_p: null,
      b_classChart_p: null,
      b_stockChart_p: null
    };

    $scope.chartTables = {
      a_classChart_p: [],
      a_stockChart_p: [],
      b_classChart_p: [],
      b_stockChart_p: []
    };
    
    var getDrawHistoryChart = function() {
    	var deferred = $q.defer();
    	var formatedData = FPSUtilsService.JLB.formatJLBRow($scope.mixedParamList, {
            AMT: typeof (webViewParamObj) != 'undefined' ? 'PURCHASE_TWD_AMT' : 'NTD_PERCENT',
            TYPE: 'PTYPE'
        });
    	
    	var total = 0;
        angular.forEach(formatedData, function (obj, index) {
      	  total += obj.PURCHASE_TWD_AMT;
        });
        
        if(total == 0) {
      	  $scope.errorText = '投資標的調整後金額為 0 ，無法進行歷史績效表現試算';
            $scope.connector('set', 'FPS330_error', true);
            $scope.$emit('FPS330_error', '投資標的調整後金額為 0 ，無法進行歷史績效表現試算');
            return false;
        }

        $scope.sendRecv('FPS330', 'inquire', 'com.systex.jbranch.app.server.fps.fps330.FPS330InputVO', {
            planID: $scope.planID,
            paramList: formatedData ? formatedData : undefined
          },
          function (tota, isError) {
            $scope.errorText = '';
            if (!isError) {
            	if(!tota[0].body.isError && tota[0].body.yearRateList) {
            		$scope.volatile = 0;
                    $scope.avgVolatile = 0;
                    $scope.minVolatile = 0;
                    $scope.maxVolatile = 0;
                    $scope.yearRateSize = tota[0].body.yearRateList.length;
                    if (tota[0].body.yearRateList.length > 0) {
                      var count = 0;
                      tota[0].body.yearRateList.forEach(function (row) {
                        count += row.RETURN_ANN_M;
                      });
                      var _temp = tota[0].body.yearRateList.map(function (row) {
                        return row.RETURN_ANN_M;
                      });
                      _temp.sort(function (a, b) {
                        return a - b;
                      });
                      $scope.volatile = tota[0].body.volatility ? parseFloat(tota[0].body.volatility).toFixed(2) : '--';
                      $scope.avgVolatile = parseFloat(count / _temp.length).toFixed(2);
                      $scope.minVolatile = parseFloat(_temp[0]).toFixed(2);
                      $scope.maxVolatile = parseFloat(_temp[_temp.length - 1]).toFixed(2);
                      $timeout(function () {
                    	var yearList = tota[0].body.yearRateList;
//                    	if(yearList.length === 1) {
//                    		yearList.push({RETURN_ANN_M: null, YEAR: '0'});
//                    	}
//                        drawChart(yearList);
        				$scope.connector('set', 'FPS330_error', false);
                      });
                      $scope.connector('set', 'FPS330.volatile', $scope.volatile);
                      $scope.connector('set', 'FPS330.avgVolatile', $scope.avgVolatile);
                      $scope.connector('set', 'FPS330.minVolatile', $scope.minVolatile);
                      $scope.connector('set', 'FPS330.maxVolatile', $scope.maxVolatile);
                      $scope.connector('set', 'FPS330.yearRateList', tota[0].body.yearRateList);
                    }
            	} else {
            		$scope.errorText = '投資標的共同區間小於1年，無法進行歷史績效表現試算';
                    $scope.$emit('FPS330_error', '投資標的共同區間小於1年，無法進行歷史績效表現試算');
        			$scope.connector('set', 'FPS330_error', true);
            	}
              
              deferred.resolve('success');
              
            } else {
              $scope.errorText = '投資標的共同區間小於1年，無法進行歷史績效表現試算';
              $scope.$emit('FPS330_error', '投資標的共同區間小於1年，無法進行歷史績效表現試算');
  			  $scope.connector('set', 'FPS330_error', true);
  			  deferred.reject(false);
            }
            $scope.connector('set', 'FPS330.errorText', $scope.errorText);
          });
        return deferred.promise;
    }
    
    var rptFunc = function() {
    	// rpt image
        var rpt = $scope.rptPicture[0];
        $scope.rptUrl = '';
        if (rpt && rpt.RPT_PIC) {
        	var bufferArr = new Uint8Array(rpt.RPT_PIC);
        	var length = bufferArr.length;
        	var result = '';
        	var addition = Math.pow(2, 16) - 1;
        	for (var i = 0; i < length; i += addition) {
        		if (i + addition > length)
        			addition = length - i;
        		result += String.fromCharCode.apply(null, bufferArr.subarray(i, i + addition));
        	}
        	$scope.rptUrl = 'data:image/png;base64,' + btoa(result);
        	$scope.rptUrleName = rpt.RPT_PIC_NAME;
        }
    }

    var callFps340 = function () {
        $scope.sendRecv('FPS340', 'inquire', 'com.systex.jbranch.app.server.fps.fps340.FPS340InputVO', {
            planId: $scope.planID,
            custId: $scope.custID,
            commRsYn: $scope.commYn,
            riskType: $scope.riskLevel,
            prdList: $scope.mixedParamList
          },
          function (tota, isError) {
            if (!isError) {
            	$scope.mixedParamList.forEach(function (row) {
//                    if (row.PTYPE === 'INS') {
//        	        	$scope.mapping.noticeFlags['INSI'] = true;
//                    } else {
//                    	$scope.mapping.noticeFlags[row.PTYPE] = true;
//                    }
    	          //商品明細-配置比例、約當金額
    	          $scope.invPct += row.INV_PERCENT;
    	          $scope.invAmt += row.PURCHASE_TWD_AMT;
    	        });
            	
              // 有無投保
              $scope.hasIns = tota[0].body.hasInvest;

              // 波動度DB來
              $scope.volatilityDB = parseInt(tota[0].body.recoVolaility);

              // 使用指南
              $scope.manualList = tota[0].body.manualList;

              // 前言
              var briefList = tota[0].body.briefList || [];
              $scope.brief = briefList.length > 0 ? briefList[0].CONTENT : '';
              // 歷史績效
              $scope.hisPerformanceList = tota[0].body.hisPerformanceList != null ? tota[0].body.hisPerformanceList.resultArray || [] : null;

              // 歷史年度平均報酬率
              $scope.historyYRate = tota[0].body.historyYRate === null ? '--' : ((Number(tota[0].body.historyYRate) * 100) || 0).toFixed(2);
              // 年化波動率
              $scope.volatility = tota[0].body.volatility === null ? '--' : ((Number(tota[0].body.volatility) * 100) || 0).toFixed(2);
              // 滿一年波波率
              $scope.fullyearVolatility = tota[0].body.fullYearVolatility === null ? '--' : ((Number(tota[0].body.fullYearVolatility) * 100) || 0).toFixed(2);

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
              rptFunc();
              
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

              // 取得查詢結果_績效追蹤與投組調整Tab
              $scope.achivedParamList = tota[0].body.achivedParamList;
              angular.forEach($scope.achivedParamList, function (value, key) {
            	  $scope.HIT_RATE_DESC = value.HIT_RATE_DESC;
                  $scope.HIT_RATE = Math.round(value.HIT_RATE);
                  $scope.MARKET_VALUE = Math.round(value.MARKET_VALUE);
                  $scope.AMT_TARGET = Math.round(value.AMT_TARGET);
                  $scope.RETURN_RATE = value.RETURN_RATE;
                  $scope.INV_AMT_CURRENT = Math.round(value.INV_AMT_CURRENT);
              });
              //            $scope.reTotal();
              return true;
            }
            $scope.showErrorMsg(tota);
            //          defer.reject(false);
            return false;
          });
      };
    
    /* init */
    var init = function () {
      $scope.sendRecv('FPS400', 'inquire', 'com.systex.jbranch.app.server.fps.fps400.FPS400InputVO', {
  		  planID: typeof (webViewParamObj) != 'undefined' ? $scope.planID : fps300.getPlanID(),
		  custID: typeof (webViewParamObj) != 'undefined' ? $scope.custID: fps300.inputVO.custID,
        },
        function (tota, isError) {
          if (!isError) {
            $scope.fpsPrevBusiDay = tota[0].body.preBusiDay;
            $scope.achivedParamList = tota[0].body.outputList;
            angular.forEach($scope.achivedParamList, function (value, key) {
              $scope.HIT_RATE_FLAG = value.HIT_RATE_FLAG;
              $scope.HIT_RATE_DESC = value.HIT_RATE_DESC;
              $scope.HIT_RATE = Math.round(value.HIT_RATE);
              $scope.MARKET_VALUE = Math.round(value.MARKET_VALUE);
              $scope.AMT_TARGET = Math.round(value.AMT_TARGET);
              $scope.RETURN_RATE = value.RETURN_RATE;
              $scope.INV_AMT_CURRENT = Math.round(value.INV_AMT_CURRENT);
              
              $scope.planDate = tota[0].body.planDate;
              $scope.targetYear = tota[0].body.targetYear;
              $scope.overDate = $scope.getPreDay((parseInt($scope.planDate.substring(0, 4)) + parseInt($scope.targetYear)) + $scope.planDate.substring(4, 10)); 
              var planAndOverDate = '目標理財規劃成立日期' + $scope.planDate + '，預計到期日期' + $scope.overDate;
              $scope.hitRateGuyText = {
	              '1': planAndOverDate + '，目前目標進度狀態為綠燈，達成率' + $scope.HIT_RATE + '%。',
	              '0': planAndOverDate + '，目前目標進度狀態微幅落後，達成率' + $scope.HIT_RATE + '%。',
	              '-1': planAndOverDate + '，目前目標進度狀態落後，達成率' + $scope.HIT_RATE + '%。'
	          };
            });
          }
        });
      $scope.sendRecv('FPS410', 'inquirePrint', 'com.systex.jbranch.app.server.fps.fps410.FPS410InputVO', {
    	  planID: typeof (webViewParamObj) != 'undefined' ? $scope.planID : fps300.getPlanID(),
    	  custID: typeof (webViewParamObj) != 'undefined' ? $scope.custID: fps300.inputVO.custID
        },
        function (tota, isError) {
          if (!isError) {
            $scope.returnMList = tota[0].body.outputMap.returnM;
            $scope.hisList = tota[0].body.outputMap.his;
          }
        });
      
      // count yearRate
      var doDraw = function() {
    	  $scope.yearRate = {
              best: $scope.connector('get', 'FPS330.maxVolatile') ? $scope.connector('get', 'FPS330.maxVolatile') : '--',
              worst: $scope.connector('get', 'FPS330.minVolatile') ? $scope.connector('get', 'FPS330.minVolatile') : '--',
              avg: $scope.connector('get', 'FPS330.avgVolatile') ? $scope.connector('get', 'FPS330.avgVolatile') : '--'
          };
          drawDoughnut();
          $scope.checkTrial();
      }
      
      if(typeof (webViewParamObj) != 'undefined') {
    	  callFps340();
    	  if(fps300.custData.SIGN_AGMT_YN === 'Y') {
    		  getDrawHistoryChart().then(function() {
    			  doDraw();
    		  });
    	  } else {
    		  drawDoughnut();
    		  $scope.checkTrial();
    	  }
      } else {
    	  doDraw();
      }
      
      // rpt image
      if(typeof (webViewParamObj) == 'undefined') {
    	  rptFunc();
      }
    };
    
    //取前一日
    $scope.getPreDay = function (s) {
      var y = parseInt(s.substring(0, 4), 10);
      var m = parseInt(s.substring(5, 7), 10) - 1;
      var d = parseInt(s.substring(8, 10), 10);
      var dt = new Date(y, m, d - 1);
      y = dt.getFullYear();
      m = dt.getMonth() + 1;
      d = dt.getDate();
      m = m >= 10 ? m : '0' + m;
      d = d >= 10 ? d : '0' + d;
      return y + '/' + m + '/' + d;
    };

    /* main function */
    var drawDoughnut = function () {
      $timeout(function () {
        // before
        classDought('b_classChart_p', $scope.paramListStock, $scope.chartTables, 'STORE_NTD');
        stockDought('b_stockChart_p', $scope.paramListStock, $scope.chartTables, 'STORE_NTD');
        // after
        classDought('a_classChart_p', $scope.paramList, $scope.chartTables, 'NTD_PERCENT');
        stockDought('a_stockChart_p', $scope.paramList, $scope.chartTables, 'NTD_PERCENT');
      });
    };

    // 類別配置
    var classDought = function (chartName, list, root, key) {
      var temp = {};
      var cntTotal = 0;
      var textIndex = 1;
      angular.copy(list).forEach(function (row) {
        if (!row.NAME) row.NAME = '其他';
        if (temp[row.NAME] === undefined) {
          temp[row.NAME] = {
            amt: row[key] || 0,
            legendText: row.NAME,
            indexLabelLineColor: 'transparent',
            INV_PERCENT: 0
          };
          textIndex++;
        } else {
          temp[row.NAME].amt += row[key] || 0;
        }
        temp[row.NAME].INV_PERCENT += row.INV_PERCENT;
        cntTotal += row[key] || 0;
      });

      var dataPoints = Object.keys(temp).map(function (key) {
        temp[key].y = temp[key].INV_PERCENT;
        temp[key].indexLabel = temp[key].y + '%';
        return temp[key];
      });

      //依佔比高到低排序
      var otherIndex = 3;
      dataPoints = dataPoints.sort(function (a, b) {
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
        colorSet: 'yellow',
        backgroundColor: 'transparent',
        data: [{
          indexLabelFontFamily: 'Microsoft JhengHei',
          innerRadius: '40%',
          radius: '75%',
          type: 'doughnut',
          showInLegend: false,
          dataPoints: dataPoints
        }]
      };

      charts[chartName] = null;
      charts[chartName] = new CanvasJS.Chart(chartName, chartData);
      charts[chartName].render();
      root[chartName] = setChartTable.call(this, charts[chartName]);
    };

    // 股價配置
    var stockDought = function (chartName, list, root, key) {
      var temp = {};
      var cntTotal = 0;
      var textIndex = 1;
      angular.copy(list).forEach(function (row) {
    	row.FUND_TYPE_NAME = row.STOCK_BOND_TYPE === 'B' ? '債券型' : '股票型';
//      if (!row.FUND_TYPE_NAME) row.FUND_TYPE_NAME = '平衡型';
        if (temp[row.FUND_TYPE_NAME] === undefined) {
          temp[row.FUND_TYPE_NAME] = {
            amt: row[key] || 0,
            legendText: row.FUND_TYPE_NAME,
            indexLabelLineColor: 'transparent',
            INV_PERCENT: 0
          };
          textIndex++;
        } else {
          temp[row.FUND_TYPE_NAME].amt += row[key] || 0;
        }
        temp[row.FUND_TYPE_NAME].INV_PERCENT += row.INV_PERCENT;
        cntTotal += row[key] || 0;
      });

      var dataPoints = Object.keys(temp).map(function (key) {
        temp[key].y = temp[key].INV_PERCENT;
        temp[key].indexLabel = temp[key].y + '%';
        return temp[key];
      });
      //股票/債券及平衡排序固定
      dataPoints = dataPoints.sort(function (a, b) {
        return sortOrder.fundName[a.legendText] > sortOrder.fundName[b.legendText];
      });

      var chartData = {
        colorSet: 'blue',
        backgroundColor: 'transparent',
        data: [{
          indexLabelFontFamily: 'Microsoft JhengHei',
          innerRadius: '40%',
          radius: '75%',
          type: 'doughnut',
          showInLegend: false,
          dataPoints: dataPoints
        }]
      };

      charts[chartName] = null;
      charts[chartName] = new CanvasJS.Chart(chartName, chartData);
      charts[chartName].render();
      root[chartName] = setChartTable.call(this, charts[chartName]);
    };

    // set chart table datas with colorsets and datapoints
    var setChartTable = function (chart) {
      var chartColor = canvasJsConfig.bgcSets[chart.options.colorSet];
      var chartData = chart.options.data[0].dataPoints;
      var colorCnt = chartColor.length;

      return chartData.map(function (data, index) {
        return {
          color: chartColor[index % colorCnt],
          label: data.legendText,
          value: data.y
        };
      });
    };

    $scope.checkTrial = function (isButton) {
      $scope.showFPS323Error = false;
      $scope.calInvestmentText = '';
      $scope.calModelPorfolioText = '';
      $scope.showFPS330Error = false;
      $timeout(function () {
        calInvestment();
        if (fps300.custData.SIGN_AGMT_YN === 'Y') {
          yearRateBar('yearRateChart', $scope.connector('get', 'FPS330.yearRateList'));
          $scope.yearRateSize = $scope.connector('get', 'FPS330.yearRateList').length;
          $scope.errorText = $scope.connector('get', 'FPS330.errorText');
        }
      });
      $scope.showFPS323Error = $scope.connector('get', 'FPS323_error');
      $scope.showFPS330Error = $scope.connector('get', 'FPS330_error');
    }
    
    $scope.$on('FPS323_error', function (event, data) {
    	if (typeof (webViewParamObj) == 'undefined') {
      	  $scope.labelIndex = $scope.labelIndex - 1;
        }
    	$scope.showFPS323Error = true;
    	$scope.calInvestmentText = data;
    });
    
    $scope.$on('FPS330_error', function (event, data) {
      $scope.labelIndex = $scope.labelIndex - 1;
      $scope.showFPS330Error = true;
      $scope.calModelPorfolioText = data;
    });

    //投組試算 FPS323 start
    var calInvestment = function () {
      var formatedData = FPSUtilsService.JLB.formatJLBRow($scope.mixedParamList, {
        AMT: 'NTD_PERCENT',
        TYPE: 'PTYPE'
      });
      // 計算單筆金額 計算定額金額
      var oneTime = 0;
      var perMonth = 0;
      $scope.mixedParamList.forEach(function (row) {
        if (row.PTYPE === 'INS')
          oneTime += parseInt(row.NTD_PERCENT, 10);
        else if (row.INV_TYPE === '1')
          oneTime += parseInt(row.NTD_PERCENT, 10);
        else if (row.INV_TYPE === '2')
          perMonth += parseInt(row.NTD_PERCENT, 10);
      });

      // FPS323 績效模擬
      $scope.$broadcast('FPS323_drawMonteCarlo', {
        custID: $scope.custID,
        planID: $scope.planID,
        ret: formatedData,
        beginAmount: parseInt(oneTime, 10),
        eachYear: parseInt(perMonth, 10) * 12,
        year: parseInt($scope.recommendations.PLANHEAD, 10),
        target: parseInt($scope.recommendations.TARGET, 10)
      });
    };

    // yearRate chart
    var yearRateBar = function (chartName, data) {
      if (!data || data.length <= 0) return false;
      var temp = {};
      var len = data.length;
      var chartData = {
        axisY: {
          valueFormatString: '#,##0\'%\'',
          labelFontSize: 14,
          gridThickness: 0.5,
          gridColor: '#fff',
          stripLines: [{
            value: 0,
            color: '#bbb',
            thickness: 2
          }]
        },
        axisX: {
          labelFontSize: 14,
        },
        dataPointWidth: 20,
      };
      var limit = [];
      data.forEach(function (row) {
        temp[row.YEAR] = {
          y: row.RETURN_ANN_M,
          label: row.YEAR,
          color: row.RETURN_ANN_M >= 0 ? YRcolor.raise : YRcolor.fall
        };
        limit.push(row.RETURN_ANN_M);
      });

//      var max = limit.reduce(function (a, b) {
//        return Math.max(a, b);
//      });
//      var min = limit.reduce(function (a, b) {
//        return Math.min(a, b);
//      });
//
//      // y軸最大值上限 多3%放數字
//      chartData.axisY.maximum = max + 5;
//      // y軸最小值下限 多3%放數字
//      chartData.axisY.minimum = min - 5;
//      
//      if((max + 5)<0 && (min - 5)<0) {
//    	  chartData.axisY.maximum = -(min - 5);
//      }
//      
//      if((max + 5)>0 && (min - 5)>0) {
//    	  chartData.axisY.minimum = -5;
//      }
//      
//      chartData.axisY.interval = Math.round((max - (min > 0 ? 0 : min)) / 3);
      
      if(limit.length == 1) {
    	  limit.push(0);
      }
      
      var max = limit.reduce(function (a, b) {
          return Math.max(Math.abs(a), Math.abs(b));
      });

      chartData.axisY.interval = Math.round((max + 10) / 3);
      // y軸最大值上限 多3%放數字
      chartData.axisY.maximum = max + Math.floor(chartData.axisY.interval / 2);
      // y軸最小值下限 多3%放數字
      chartData.axisY.minimum = (max * -1) - Math.floor(chartData.axisY.interval / 2);
      
      var dataPoints = Object.keys(temp).map(function (key) {
        return temp[key];
      });

      if (len < 5) {
        chartData.data = [{
          indexLabelFontFamily: 'Microsoft JhengHei',
          type: 'bar',
          yValueFormatString: '#0.00\'%\'',
          indexLabel: '{y}',
          indexLabelFontSize: 14,
          indexLabelPlacement: 'outside',
          indexLabelOrientation: 'horizontal',
          dataPoints: dataPoints
        }];
      } else {
        chartData.data = [{
          indexLabelFontFamily: 'Microsoft JhengHei',
          type: 'column',
          yValueFormatString: '#0.00\'%\'',
          indexLabel: '{y}',
          indexLabelFontSize: 14,
          indexLabelPlacement: 'outside',
          indexLabelOrientation: 'horizontal',
          dataPoints: dataPoints
        }];
      }
      var chart = new CanvasJS.Chart(chartName, chartData);
      chart.render();
    };

    /* main progress */
    console.log('FPSPrintController');
    var watchCharts = $scope.$watch(function () {
      return document.getElementById('charts');
    }, function (a, b) {
      if (a != null) {
        if (typeof (webViewParamObj) != 'undefined') document.querySelector('.FPS .print.A4').classList.add('iPad');
        init();
        watchCharts();
      }
    });
  }
);
