/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS410Controller',
  function ($scope, $controller, ngDialog, socketService, alerts, projInfoService, $q, getParameter, $filter, $timeout) {
    $controller('BaseController', {
      $scope: $scope
    });

    $scope.controllerName = 'FPS410Controller';

    /* parameter */
    var fps300 = $scope.fps300;
    $scope.currentProgress = fps300.getCurrentProgress() || {};
    $scope.recommendations = fps300.getRecommendations() || {};
    //    console.log("0131xxx----="+JSON.stringify($scope.recommendations));
    //取得客戶風險屬性
    var custRiskAttr = fps300.getCustData().KYC_LEVEL;
    var custRiskAttrNumber = fps300.getCustData().KYC_LEVEL.substring(1, 2);
    $scope.rate = []; // currency rate
    $scope.mapping = {};
    var totalNTD = 0;
    var tempHisValue = 0;
    $scope.isEditing = false; // if isediting listen change and update calNTD or calOri
    //不顯示筆圖案
    $scope.isPencil = "N";
    $scope.isReb = true; //true:顯示 單筆投入總額(NT$)、定期投入總額(NT$)
    $scope.connector('set', 'FPS410PrintSEQ', -1);


    $scope.inputVO = {
      SD: undefined,
      ED: undefined
    };

    var pathMap = {
      'product': './assets/txn/FPS/FPSProd.html',
      detail: {
        MFD: './assets/txn/PRD110/PRD110_DETAIL.html',
        INS: './assets/txn/PRD160/PRD160_DETAIL.html'
      },
    };

    $scope.titleMap = [{
      title: '基金',
      path: 'assets/txn/FPS/FPSProd_MFD.html',
      order: 0,
      active: true
    }, {
      title: '投資型保險',
      path: 'assets/txn/FPS/FPSProd_InvestIns.html',
      order: 1,
      active: false
    }];

    // ugly guy for status
    $scope.hitRateGuy = {
      '1': './assets/images/money_SVG/money_happy.svg',
      '0': './assets/images/money_SVG/money_shoulder.svg',
      '-1': './assets/images/money_SVG/money_sweat.svg'
    };

    // charts init value
    var charts = {
      classChart: null,
      stockChart: null
    };

    $scope.chartTables = {
      classChart: [],
      stockChart: []
    };

    var sortOrder = {
      fundName: {
        股票型: 1,
        債券型: 2,
        平衡型: 3,
        其他: 4
      }
    };

    // planID || custID
    var planID = fps300.getPlanID() || null;
    var custID = fps300.inputVO.custID;

    // getXML
    var param = function () {
      var deferred = $q.defer();
      var flag = false;

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
        'FPS.TXN_TYPE'
      ]).then(function (mapping) {
        $scope.mapping = mapping;
        $scope.mapping.sTxnType = $scope.mapping.TxnType.filter(function (item) {
          return item.DATA.charAt(0, 1) === 'S';
        });
        $scope.mapping.nTxnType = $scope.mapping.TxnType.filter(function (item) {
          return item.DATA.charAt(0, 1) === 'N';
        });
        $scope.mapping.fullProdType = {
          MFD: '基金投資注意事項',
          INSI: '連結全委帳戶之投資型保險投資注意事項'
        };
        $scope.mapping.noticeTypes = {
          0: 'MFD',
          1: 'INSI'
        };
        $scope.sendRecv('FPS240', 'getParameter', 'com.systex.jbranch.app.server.fps.fps240.FPS240InputVO', {
            planID: planID
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
          });
      }, function (err) {
        deferred.reject(false);
      });
      return deferred.promise;
    };

    /* init */
    $scope.init = function () {
    	$scope.mapping.noticeTypes = {
    			0: 'MFD',
  	          	1: 'INSI'
    	};
    	//上一頁返回
    	$scope.inquire();
    };

    //基股商品(基金+投資型保險) (判斷Model Portfolio是否有值，若有值則進入主畫面；反之跳出FPS320畫面)
    $scope.inquire = function () {
      var deferred = $q.defer();
      var inputVO = {
        planID: planID,
        custID: custID,
        riskType: $scope.recommendations.CUST_RISK_ATTR_NAME || fps300.getRISK_ATTR(),
        action: 'step1',
        commRsYn: fps300.getCustData().SIGN_AGMT_YN == "Y"
      };

      $scope.sendRecv('FPS400', 'inquire', 'com.systex.jbranch.app.server.fps.fps400.FPS400InputVO', inputVO,
        function (tota, isError) {
          if (!isError) {
            $scope.planDate = (tota[0].body.planDate || '').substring(0, 10).replace(/[-]+/g, '/');
            $scope.inputVO.SD = new Date($scope.planDate || undefined);
            $scope.limitDate();
            $scope.connector('set', 'preBusiDay', tota[0].body.preBusiDay);
            
            $scope.achivedParamList = tota[0].body.outputList;
            angular.forEach($scope.achivedParamList, function (value, key) {
              $scope.HIT_RATE_FLAG = value.HIT_RATE_FLAG;
              $scope.HIT_RATE_DESC = value.HIT_RATE_DESC;
              $scope.HIT_RATE = Math.round(value.HIT_RATE);
              $scope.MARKET_VALUE = Math.round(value.MARKET_VALUE);
              $scope.AMT_TARGET = Math.round(value.AMT_TARGET);
              //              $scope.RETURN_RATE = value.RETURN_RATE;
              $scope.INV_AMT_CURRENT = Math.round(value.INV_AMT_CURRENT);
            });
          }
        });

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
              $scope.HIT_RATE = value.HIT_RATE;
              $scope.MARKET_VALUE = value.MARKET_VALUE;
              $scope.AMT_TARGET = value.AMT_TARGET;
              $scope.RETURN_RATE = value.RETURN_RATE;
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
              fps300.setRecommendations($scope.recommendations);
            }

            if (fps300.custData.kycDisable) {
              $scope.kycDisable = true;
            } else if (parseInt(custRiskAttrNumber) < parseInt($scope.recommendations.CUST_RISK_ATTR_NAME.substring(1, 2))) {
              $scope.kycDisable = true;
            }

            fps300.rate = tota[0].body.rate;
            $scope.mapping.noticeFlags = {
              MFD: false,
              INSI: false
            };
            // calculate
            $scope.paramList.forEach(function (row) {
              reformatList(row);
              row.action = 'update';
              totalNTD += row.NTD_PERCENT;
              if (row.PTYPE === 'INS') {
                $scope.mapping.noticeFlags['INSI'] = true;
                if ($scope.mapping.noticeTypes.indexOf('INSI') < 0) {
            		$scope.mapping.noticeTypes.push('INSI');            		
                }
              } else {
                $scope.mapping.noticeFlags[row.PTYPE] = true;
              }
            });
            
            if(fps300.custData.SIGN_AGMT_YN === 'Y') {
            	$scope.mapping.noticeFlagAlls = $scope.mapping.noticeFlags;
            }
            
            var invPercent = 0;
            $scope.paramList.forEach(function (row) {
              row.INV_PERCENT = parseInt(row.NTD_PERCENT * 100 / totalNTD);
              invPercent += row.INV_PERCENT;
            });
            if ($scope.paramList.length > 0 && invPercent < 100) {
              $scope.paramList[$scope.paramList.length - 1].INV_PERCENT = $scope.paramList[$scope.paramList.length - 1].INV_PERCENT + (100 - invPercent);
            }
            // fps300.setParamList($scope.paramList);
            drawDoughnut();
            deferred.resolve('success');
            return true;
          }
          $scope.showErrorMsg(tota);
          return false;
        });
      return deferred.promise;
    };
    var isFirst = "Y";
    // 歷史交易明細
    $scope.inquireHis = function () {
      //如查詢區間無資料，可以有訊息文字：查詢區間無商品贖回資料
      //之後記得加訊息文字
      $scope.sendRecv('FPS400', 'inquireHis', 'com.systex.jbranch.app.server.fps.fps400.FPS400InputVO', {
          planID: fps300.getPlanID(),
          custID: fps300.inputVO.custID,
          sDate: $scope.inputVO.SD,
          eDate: $scope.inputVO.ED,
          flagYN: isFirst
        },
        function (tota, isError) {
          if (!isError) {
            isFirst = "N";
            if(tota[0].body.outputList.length > 0){
            	$scope.transDetailList = tota[0].body.outputList.map(function (row) {
            		row.DATA_DATE = (row.DATA_DATE || '').split(' ')[0];
            		return row;
            	});            	
            } else {
            	$scope.showMsg("查無贖回交易資料");
    			return;
            }
          }
        });
    };

    /**
     * 去那兒
     */
    $scope.go = function (where, type, data) {
      switch (where) {
        case 'detail':
          openDialog(pathMap.detail[type], data, type);
          break;
        case 'print':
          openDialog('./assets/txn/FPS410/FPS410PrintPreview.html', {
            planDate: $scope.planDate,
            MFDPerformanceList: $scope.MFDPerformanceList,
            brief: $scope.brief,
            custInfo: $scope.custInfo,
            rptPicture: $scope.rptPicture,
            volatility: $scope.volatility,
            manualList: $scope.manualList,
            mapping: $scope.mapping,
            fpsPrint: $scope.p_charts,
            fps300: fps300,
            recommendations: $scope.recommendations,
            paramList: $scope.paramList
          }, 'print');
          break;
        default:
          break;
      }
    };

    var openDialog = function (path, data, type) {
      if (!path) return false;
      // type: detail
      var scope = $scope;
      var dialog = ngDialog.open({
        template: path,
        className: 'FPS200',
        controller: ['$scope', function ($scope) {
          $scope.data = data;
          $scope.trendPath = scope.mapping.trendPath;

          $scope.row = angular.copy(data);
          switch (type) {
            case 'MFD':
              $scope.row.FUND_CNAME = data.PRD_CNAME || data.PROD_NAME;
              $scope.row.PRD_ID = data.PRD_ID || data.PROD_ID;
              break;
            case 'INS':
              $scope.row.INSPRD_NAME = data.PRD_CNAME;
              break;
          }
          $scope.planID = planID;
          $scope.custID = custID;
          $scope.cust_id = custID;
          $scope.hasIns = scope.hasIns;
        }]
      });
    };

    // record old value of ntdpercent or chgpercent
    $scope.setHisValue = function (val) {
      tempHisValue = parseFloat(val) || 0;
      $scope.isEditing = true;
    };

    // draw chart
    var drawDoughnut = function () {
      $timeout(function () {
        classDought('classChart', $scope.chartTables);
        stockDought('stockChart', $scope.chartTables);
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

      var chartData = {
        colorSet: 'yellow',
        backgroundColor: 'transparent',
        data: [{
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
    	row.FUND_TYPE_NAME = row.STOCK_BOND_TYPE === 'B' ? '債券型' : '股票型';
//        if (!row.FUND_TYPE_NAME) row.FUND_TYPE_NAME = '平衡型';
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

      var chartData = {
        colorSet: 'blue',
        backgroundColor: 'transparent',
        data: [{
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

    // date picker
    // 活動起迄日
    $scope.sDateOptions = {};
    $scope.eDateOptions = {};
    $scope.open = function ($event, dataPicker, parent) {
      $event.preventDefault();
      $event.stopPropagation();
      if (parent === undefined) {
        $scope[dataPicker] = true;
      } else {
        parent[dataPicker] = true;
      }
    };

    $scope.limitDate = function () {
      $scope.sDateOptions.maxDate = $scope.inputVO.ED;
      $scope.sDateOptions.minDate = new Date($scope.planDate || undefined);
      $scope.eDateOptions.minDate = $scope.inputVO.SD;
    };

    /* main process */
    $scope.init();
//    $scope.inquireHis();
    param()
      .then(function (success) {
        $scope.inquire();
      }, function (err) {
        console.log(err);
      });
  }
);
