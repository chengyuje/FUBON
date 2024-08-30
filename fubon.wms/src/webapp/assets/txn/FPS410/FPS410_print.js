/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPSPrintController',
  function ($scope, $controller, $timeout, $q) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPSPrintController';
    var fps300 = $scope.fps300;
    var totalNTD = 0;
    var tempHisValue = 0;
    //不顯示筆圖案
    $scope.isPencil = "N";
    $scope.isReb = true; //true:顯示 單筆投入總額(NT$)、定期投入總額(NT$)
    if (typeof (webViewParamObj) != 'undefined') {
    	$scope.fromFps = '340';
    	fps300 = {};
    	fps300.custData = $scope.custInfo;
    }
    $scope.hitRateGuyText = {
      '1': '目前目標進度狀態為綠燈，達成率80%，達目標。',
      '0': '目前目標進度狀態為黃燈，達成率80%，進度微幅落後應達目標。',
      '-1': '目前目標進度狀態為紅燈，達成率80%，低於應達目標。'
    };
    $scope.hitRateGuy = {
      '1': './assets/images/money_SVG/money_happy.svg',
      '0': './assets/images/money_SVG/money_shoulder_2.png',
      '-1': './assets/images/money_SVG/money_sweat.svg'
    };
    var sortOrder = {
      fundName: {
        股票型: 1,
        債券型: 2,
        平衡型: 3,
        其他: 4
      }
    };
    //    $scope.paramList = fps300.getParamList();
    /* parameter */
    $scope.path = {
      header: './assets/images/FPS_PDF_Header_3.png',
      brief: './assets/txn/FPS_PDF_Template/FPSutils_component/FPS_print_brief.template.html',
      custInfo: './assets/txn/FPS/FPS_CUST.template.html',
      requirement: 'assets/txn/FPS340/FPS340.template.html',

      achievedState: 'assets/txn/FPS400/FPS400_print.template.html',
      achievedState_ps: './assets/txn/FPS_PDF_Template/FPS340_component/achievedState_ps.html',

      firstPlanChart: './assets/txn/FPS_PDF_Template/FPS340_component/firstPlanChart.html',

      manual: './assets/txn/FPS_PDF_Template/FPSutils_component/manual.html',
      notice: './assets/txn/FPS_PDF_Template/FPSutils_component/notice.html',
    };

    // charts init value
    var charts = {
      p_classChart: null,
      p_stockChart: null
    };

    $scope.chartTables = {
      p_classChart: [],
      p_stockChart: []
    };
    
    var getFPS410Data = function() {
    	var inputVO = {
    			planID: $scope.planID,
    			custID: $scope.custID,
    			riskType: $scope.riskLevel,
    			action: 'step1',
    			commRsYn: fps300.custData.SIGN_AGMT_YN == "Y"
    	};
    	$scope.sendRecv('FPS410', 'inquire', 'com.systex.jbranch.app.server.fps.fps410.FPS410InputVO', inputVO,
        function (tota, isError) {
    		if (!isError) {
            // 商品
            $scope.manualList = tota[0].body.outputMap.manualList;
            // 前言
            var briefList = tota[0].body.outputMap.briefList || [];
            $scope.brief = briefList.length > 0 ? briefList[0].CONTENT : '';
            // 圖片
            $scope.rptPicture = tota[0].body.outputMap.rptPicture;

            //取得查詢結果_績效追蹤與投組調整Tab
            $scope.achivedParamList = tota[0].body.outputMap.achivedParamList;
            angular.forEach($scope.achivedParamList, function (value, key) {
              $scope.HIT_RATE_DESC = value.HIT_RATE_DESC;
              $scope.HIT_RATE = Math.round(value.HIT_RATE);
              $scope.MARKET_VALUE = Math.round(value.MARKET_VALUE);
              $scope.AMT_TARGET = Math.round(value.AMT_TARGET);
              $scope.RETURN_RATE = value.RETURN_RATE;
              $scope.INV_AMT_CURRENT = Math.round(value.INV_AMT_CURRENT);
            });
            totalNTD = 0;
            $scope.paramList = tota[0].body.outputList;

            // about settings 
            if (tota[0].body.settingList && tota[0].body.settingList.length > 0) {
              $scope.recommendSettings = tota[0].body.settingList[0];
              $scope.recommendations.PLANNAME = $scope.recommendSettings.INV_PLAN_NAME;
              $scope.recommendations.PLANHEAD = $scope.recommendSettings.INV_PERIOD;
              $scope.recommendations.ONETIME = $scope.recommendSettings.INV_AMT_ONETIME;
              $scope.recommendations.PERMONTH = $scope.recommendSettings.INV_AMT_PER_MONTH;
              $scope.recommendations.TARGET = $scope.recommendSettings.INV_AMT_TARGET;
              $scope.recommendations.CUST_RISK_ATTR_NAME = $scope.recommendSettings.VOL_RISK_ATTR;
              $scope.recommendations.ONETIME_R = $scope.recommendSettings.ONETIME_R;
              $scope.recommendations.PERMONTH_R = $scope.recommendSettings.PERMONTH_R;
            }

            fps300.rate = tota[0].body.rate;
            $scope.mapping.noticeFlags = {
              MFD: false,
              INSI: false
            };
            // calculate
            $scope.paramList.forEach(function (row) {
            	reformatList(row);
            	totalNTD += row.NTD_PERCENT;
            });
            
            var invPercent = 0;
            $scope.paramList.forEach(function (row) {
                row.INV_PERCENT = parseInt(row.NTD_PERCENT * 100 / totalNTD);
                invPercent += row.INV_PERCENT;
            });
            if ($scope.paramList.length > 0 && invPercent < 100) {
             $scope.paramList[$scope.paramList.length - 1].INV_PERCENT = $scope.paramList[$scope.paramList.length - 1].INV_PERCENT + (100 - invPercent);
            }
            return true;
          }
          $scope.showErrorMsg(tota);
          return false;
        });
    }
    
    if(typeof (webViewParamObj) != 'undefined') {
    	getFPS410Data();
    }

    /* init */
    var init = function () {
      $scope.sendRecv('FPS400', 'inquire', 'com.systex.jbranch.app.server.fps.fps400.FPS400InputVO', {
    	   planID: typeof (webViewParamObj) != 'undefined' ? $scope.planID : fps300.getPlanID(),
    	   custID: typeof (webViewParamObj) != 'undefined' ? $scope.custID: fps300.inputVO.custID
        },
        function (tota, isError) {
          if (!isError) {
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
      var yearRateNums = $scope.yearRateList ? $scope.yearRateList.map(function (row) {
        return row.RETURN_ANN_M;
      }) : [];
      $scope.yearRate = {
        best: $scope.yearRateList ? Math.min.apply(Math, yearRateNums) : '--',
        worst: $scope.yearRateList ? Math.max.apply(Math, yearRateNums) : '--',
        avg: $scope.yRate
      };
      
      // chart
      drawDoughnut();
      
      if (typeof (webViewParamObj) != 'undefined') {
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
    		  $scope.rptUrl = "data:image/png;base64," + btoa(result);
    		  $scope.rptUrleName = rpt.RPT_PIC_NAME;
    	  } 
      }
    };
    
    /* helping function */
    var reformatList = function (row) {
	    row.PLAN_ID = row.PLAN_ID;
	    row.SEQNO = row.SEQNO;
	    row.PRD_ID = row.PRD_ID;
	    row.PTYPE = row.PTYPE;
	
	    row.RISK_TYPE = row.RISK_TYPE;
	    row.CURRENCY_TYPE = row.CURRENCY_TYPE;
	    row.CmbCur = row.TRUST_CURR === 'TWD' ? '2' : '1';
	    row.CIS_3M = row.CIS_3M;
	    row.STORE_RAW = toIntNumber(row.STORE_RAW);
	    row.STORE_NTD = toIntNumber(row.STORE_NTD);
	    row.CHG_PERCENT = toIntNumber(row.STORE_RAW);
	    row.NTD_PERCENT = toIntNumber(row.STORE_NTD);
	    row.LowMoney = row.LIMIT_ORG_AMT;
	    row.TxnType = row.TXN_TYPE;
	    row.EX_RATE = row.EX_RATE;
	
	    //投資方式
	    row.txnTypeList = $scope.mapping[row.PTYPE + 'TxnType'];
	    //判斷單筆還是定額
	    if ($scope.recommendations.ONETIME) {
	    	row.TxnType = 'N1';
	    } else {
	    	row.TxnType = 'N2';
	    }
    };

    var toIntNumber = function (num) {
    	return Math.round(parseFloat(num)) || 0;
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
        classDought('p_classChart', $scope.chartTables);
        stockDought('p_stockChart', $scope.chartTables);
      });
    };

    // 類別配置
    var classDought = function (chartName, root) {
      var temp = {};
      var textIndex = 0;
      $scope.paramList.forEach(function (row) {
        if (!row.NAME) row.NAME = '其他';
        if (temp[row.NAME] === undefined) {
          temp[row.NAME] = {
            y: row.INV_PERCENT,
            legendText: row.NAME,
            indexLabelLineColor: 'transparent',
            // indexLabel: row.INV_PERCENT + '%'
          };
          textIndex++;
        } else {
          temp[row.NAME].y += row.INV_PERCENT;
        }
      });

      var dataPoints = Object.keys(temp).map(function (key, mapIndex) {
        temp[key].y = Math.round(temp[key].y);
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

      var textIndex = 1;
      dataPoints.forEach(function (row) {
        row.legendText = "(" + textIndex + ") " + row.legendText;
        row.indexLabel = "(" + textIndex + ") " + row.indexLabel;
        textIndex++;
      });
      var chartData = {
        colorSet: 'yellow',
        backgroundColor: 'transparent',
        data: [{
          indexLabelFontFamily: 'Microsoft JhengHei',
          innerRadius: '40%',
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
    var stockDought = function (chartName, root) {
      var temp = {};
      var textIndex = 0;
      $scope.paramList.forEach(function (row) {
        if (!row.FUND_TYPE_NAME) row.FUND_TYPE_NAME = '平衡型';
        if (temp[row.FUND_TYPE_NAME] === undefined) {
          temp[row.FUND_TYPE_NAME] = {
            y: row.INV_PERCENT,
            legendText: row.FUND_TYPE_NAME,
            indexLabelLineColor: 'transparent',
            // indexLabel: row.INV_PERCENT + '%'
          };
          textIndex++;
        } else {
          temp[row.FUND_TYPE_NAME].y += row.INV_PERCENT;
          // temp[row.FUND_TYPE_NAME].indexLabel = temp[row.FUND_TYPE_NAME].y + '%';
        }
      });

      var dataPoints = Object.keys(temp).map(function (key, mapIndex) {
        temp[key].y = Math.round(temp[key].y);
        temp[key].indexLabel = temp[key].y + '%';
        return temp[key];
      });

      //股票/債券及平衡排序固定
      dataPoints = dataPoints.sort(function (a, b) {
        return sortOrder.fundName[a.legendText] > sortOrder.fundName[b.legendText];
      });

      var textIndex = 1;
      dataPoints.forEach(function (row) {
        row.legendText = "(" + textIndex + ") " + row.legendText;
        row.indexLabel = "(" + textIndex + ") " + row.indexLabel;
        textIndex++;
      });
      var chartData = {
        colorSet: 'blue',
        backgroundColor: 'transparent',
        data: [{
          indexLabelFontFamily: 'Microsoft JhengHei',
          innerRadius: '40%',
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
