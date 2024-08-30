/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS412Controller',
  function ($scope, $controller, ngDialog, $q, $timeout, FPSUtilsService) {
    $controller('BaseController', {
      $scope: $scope
    });

    $scope.controllerName = 'FPS412Controller';

    $scope.today = (new Date()).yyyyMMdd("/");
    
    //目標-下單購物車先反灰，配合APP上線再打開 (#6291)
    $scope.btnDisable = true;
    
    /* parameter */
    var fps300 = $scope.fps300;
    $scope.paramList = fps300.getParamList();
    $scope.paramListOrg = fps300.getParamListOrg();
    $scope.fps350Check = false;

    $scope.currentProgress = fps300.getCurrentProgress() || {};
    $scope.recommendations = fps300.getRecommendations() || {};
    $scope.connector('set', 'FPS412PrintSEQ', -1);
    //取得客戶風險屬性
    $scope.riskType = fps300.custData.KYC_LEVEL;
    var custRiskAttr = fps300.getCustData().KYC_LEVEL;
    var custRiskAttrNumber = fps300.getCustData().KYC_LEVEL.substring(1, 2);
    $scope.isCreate = fps300.getPlanID() ? false : true;
//    $scope.rate = []; // currency rate
    $scope.mapping = {
      trendPath: {
        H: 'assets/images/ic-unbiased.svg',
        B: 'assets/images/ic-rise.svg',
        S: 'assets/images/ic-decline.svg'
      },
    };
    var totalNTD = 0;
    var tempHisValue = 0;
    $scope.isEditing = false; // if isediting listen change and update calNTD or calOri
    $scope.isReb = true; //true:顯示 單筆投入總額(NT$)、定期投入總額(NT$)
    $scope.isPencil = "N"; //不顯示筆圖案
    $scope.checkTransaction = false;

    var pathMap = {
      warnings: './assets/txn/FPS240/FPS240Warning.html',
      SOT: './assets/txn/FPS/FPS_SOT.html',
      1: 'SOT110', //基金單筆申購
      2: 'SOT120', //基金定額申購
      3: 'SOT130', //贖回
      4: 'SOT140', //轉出
      5: 'IOT110' //保險申購
    };

    // charts init value
    var charts = {
      a_classChart: null,
      a_stockChart: null,
      b_classChart: null,
      b_stockChart: null
    };

    $scope.chartTables = {
      a_classChart: [],
      a_stockChart: [],
      b_classChart: [],
      b_stockChart: []
    };

    var sortOrder = {
      fundName: {
        股票型: 1,
        債券型: 2,
        平衡型: 3,
        其他: 4
      }
    };

    // 贖回 最低贖回金額 轉出 賣出
    var RDMtxnType = ['F2', 'F3', 'F4', 'E2', 'I2', '2', '3'];

    // certificate title
    var certificateTitle = {
      MFD: '憑證編號',
      INS: '保單號碼',
    };

    // ugly guy for status
    $scope.hitRateGuy = {
      '1': './assets/images/money_SVG/money_happy.svg',
      '0': './assets/images/money_SVG/money_shoulder.svg',
      '-1': './assets/images/money_SVG/money_sweat.svg'
    };

    // custID | planID
    var planID = fps300.getPlanID() || null;
    var custID = fps300.inputVO.custID;

    // getXML
    var param = function () {
      var deferred = $q.defer();
      var count = 0;
      var done = 2;
      var checkDone = function () {
        count += 1;
        if (count === done) {
          deferred.resolve('success');
        }
      };

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
        'FPS.INV_TYPE',
      ]).then(function (mapping) {
        $scope.mapping = Object.assign($scope.mapping, mapping);
        // 庫存用下拉選單
        $scope.mapping.INSTxnType = $scope.mapping.TxnType.filter(function (row) {
          return row.DATA.charAt(0) === 'I';
        });
        $scope.mapping.MFDTxnType = $scope.mapping.TxnType.filter(function (row) {
          return row.DATA.charAt(0) === 'F';
        });
        $scope.mapping.nTxnType = $scope.mapping.TxnType.filter(function (row) {
          return row.DATA.charAt(0) === 'N';
        });
        $scope.mapping.sTxnType = $scope.mapping.TxnType.filter(function (row) {
          return row.DATA.charAt(0) === 'S';
        });
        $scope.mapping.TxnType = [{
          DATA: 'F',
          LABEL: '申購'
        }];
        $scope.mapping.fullProdType = {
          MFD: '基金投資注意事項',
          INSI: '連結全委帳戶之投資型保險投資注意事項'
        };
        $scope.mapping.noticeFlags = {
          MFD: false,
          INSI: false
        };
        $scope.mapping.noticeTypes = {
          0: 'MFD',
          1: 'INSI'
        };
        checkDone();
      }, function (err) {
        deferred.reject(false);
      });
      
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
            checkDone();
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

    //查詢現況投資組合檢視的資料
    $scope.inquire = function () {
      var deferred = $q.defer();
      var inputVO = {
        planID: fps300.getPlanID() || null,
        custID: fps300.inputVO.custID,
        riskType: $scope.recommendations.CUST_RISK_ATTR_NAME || fps300.getRISK_ATTR()
      };

      $scope.sendRecv('FPS400', 'inquire', 'com.systex.jbranch.app.server.fps.fps400.FPS400InputVO', inputVO,
        function (tota, isError) {
          if (!isError) {
            $scope.planDate = tota[0].body.planDate;
            $scope.inputVO.SD = new Date($scope.planDate || undefined);
            $scope.limitDate();

            angular.forEach(tota[0].body.outputList, function (value, key) {
              $scope.INV_PLAN_NAME = value.INV_PLAN_NAME;
              $scope.INV_AMT_TARGET = Math.round(value.INV_AMT_TARGET);
              $scope.HIT_RATE_FLAG = value.HIT_RATE_FLAG;
              $scope.HIT_RATE_DESC = value.HIT_RATE_DESC;
              $scope.HIT_RATE = Math.round(value.HIT_RATE);
              $scope.MARKET_VALUE = Math.round(value.MARKET_VALUE);
              $scope.AMT_TARGET = Math.round(value.AMT_TARGET);
              $scope.RETURN_RATE = value.RETURN_RATE;
              $scope.INV_AMT_CURRENT = Math.round(value.INV_AMT_CURRENT);
            });
          }
        });
      $scope.paramListStock = fps300.getParamListOrg();
      $scope.mixedParamList = fps300.getParamList();

      $scope.transactionList = $scope.paramList.filter(function (row) {
        var isTransaction = (row.STORE_RAW - row.CHG_PERCENT !== 0);
        if (isTransaction) {
          if (row.STORE_RAW) {
            row.isStock = true;
            row.transaction = RDMtxnType.indexOf(row.TXN_TYPE) >= 0 ?
              (row.STORE_RAW - row.CHG_PERCENT) > (Number(row.RDM_ORG_AMT_ORDER || 0) || 0) :
              (row.CHG_PERCENT - row.STORE_RAW) > (Number(row.CHG_PERCENT || 0) || 0);
          } else {
            row.transaction = row.CHG_PERCENT > (Number(row.CHG_PERCENT || 0) || 0);
          }
          // count twd
          // 顯示申購贖回金額
          row.TRANSACT_ORG_AMT = row.STORE_RAW ? row.STORE_RAW - row.CHG_PERCENT : row.CHG_PERCENT;
          row.TRANSACT_TWD_AMT = Math.round((Number(row.TRANSACT_ORG_AMT) || 0) * curRate(row.TRUST_CURR));
          row.tooltipInfo = certificateTitle[row.PTYPE] !== undefined ?
            (certificateTitle[row.PTYPE] ? (certificateTitle[row.PTYPE] + ': ' + row.CERT_NBR) : '') : '';
        }
        return isTransaction;
      });

      $scope.transactionList.forEach(function (row) {
    	  if (row.PTYPE === 'INS') {
    		  $scope.mapping.noticeFlags['INSI'] = true;
    	  } else {
    		  $scope.mapping.noticeFlags[row.PTYPE] = true;
    	  }

    	  if(fps300.custData.SIGN_AGMT_YN === 'Y'){
    		  $scope.mapping.noticeFlagAlls = $scope.mapping.noticeFlags
    	  } else {
    		  $scope.mapping.noticeFlagAlls = {
      				'MFD' : true,
      				'INSI' : true
      		  }
    	  }

    	  if ($scope.transactionList.length > 0)
    		  $scope.checkTransaction = true;
      });

      callFps340();
      drawDoughnut();
      checkTrial();
      return deferred.promise;
    };
    var callFps340 = function () {
      $scope.sendRecv('FPS340', 'inquire', 'com.systex.jbranch.app.server.fps.fps340.FPS340InputVO', {
          planId: fps300.getPlanID(),
          custId: fps300.inputVO.custID,
          commRsYn: false,
          riskType: $scope.riskType,
          prdList: $scope.mixedParamList
        },
        function (tota, isError) {
          if (!isError) {
            // 有無投保
            $scope.hasIns = tota[0].body.hasInvest;

            //波動度DB來
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

            //取得查詢結果_績效追蹤與投組調整Tab
            $scope.achivedParamList = tota[0].body.achivedParamList;
            angular.forEach($scope.achivedParamList, function (value, key) {
              $scope.HIT_RATE_DESC = value.HIT_RATE_DESC;
              $scope.HIT_RATE = value.HIT_RATE;
              $scope.MARKET_VALUE = value.MARKET_VALUE;
              $scope.AMT_TARGET = value.AMT_TARGET;
              $scope.RETURN_RATE = value.RETURN_RATE;
              $scope.INV_AMT_CURRENT = value.INV_AMT_CURRENT;
            });
            //            $scope.reTotal();
            return true;
          }
          $scope.showErrorMsg(tota);
          //          defer.reject(false);
          return false;
        });
    };

    $scope.chgCur = function (row) {
      if (row.CmbCur == '2') {
        row.CHG_PERCENT = row.NTD_PERCENT;
      } else {
        row.CHG_PERCENT = fps300.floatFormat((parseFloat(row.NTD_PERCENT) / curRate(row.CURRENCY_TYPE)), 4) || 0;
      }
    };

    $scope.changeTxnType = function (row) {
      //若交易指示下拉選單為'定期定額'，則對應原幣最低申購金額 = TBPRD_FUND.SML_SUBS_MINI_AMT_FOR
      if (row.INV_TYPE === '2') {
        row.LowMoney = row.SML_SUBS_MINI_AMT_FOR;
      } else if (row.INV_TYPE === '1') {
        row.LowMoney = row.GEN_SUBS_MINI_AMT_FOR;
      } else {
        row.LowMoney = 0;
      }
      $scope.chgStore(row);
    };

    //儲存btn
    $scope.save = function () {
      if ($scope.fps324Form.$invalid) {
        $scope.showErrorMsgInDialog('ehl_01_common_022');
        return false;
      }

      var inputVO = {
        custID: fps300.inputVO.custID,
        sppType: $scope.recommendations.planningCode,
        planName: $scope.recommendations.PLANNAME,
        planID: fps300.getPlanID(),
        VOL_RISK_ATTR: $scope.recommendations.CUST_RISK_ATTR_NAME,
        INV_PERIOD: $scope.recommendations.PLANHEAD,
        INV_AMT_ONETIME: $scope.recommendations.ONETIME,
        INV_AMT_PER_MONTH: $scope.recommendations.PERMONTH,
        INV_AMT_TARGET: $scope.recommendations.TARGET,
      };
      inputVO.prdList = [];

      $scope.sendRecv('FPS324', 'save', 'com.systex.jbranch.app.server.fps.fps324.FPS324InputVO', inputVO,
        function (tota, isError) {
          if (!isError) {
            $scope.showSuccessMsg('ehl_01_common_025'); //儲存成功
            fps300.setPlanID(tota[0].body);
            $scope.isCreate = false;
            $scope.inquire();
          }
        });
    };

    /**
     * 投組試算: 績效模擬(FPS323) + 歷史績效表現(FPS330)
     */
    var checkTrial = function () {
      // cal total NTD
      totalNTD = $scope.mixedParamList.reduce(function (a, b) {
        return a + b.NTD_PERCENT;
      }, 0) || 0;
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
        custID: custID,
        planID: planID,
        ret: formatedData,
        beginAmount: parseInt(oneTime, 10),
        eachYear: parseInt(perMonth, 10) * 12,
        year: parseInt($scope.recommendations.PLANHEAD, 10),
        target: parseInt($scope.recommendations.TARGET, 10)
      });
      // FPS330 歷史績效表現
      $scope.$broadcast('FPS330_drawHistoryChart', formatedData);
    };

    //跳轉到SOT
    $scope.chgUrl = function (row) {
      var otPage = "";
      switch (row.TxnType) {
        case "F1":
          if (row.INV_TYPE == "2") {
            //基金定額申購
            otPage = "SOT120";
          } else {
            //基金單筆申購
            otPage = "SOT110";
          }
          break;
        case "F2":
          //贖回
          otPage = "SOT130";
          break;
        case "F4":
        case "F5":
          //轉出、轉入
          otPage = "SOT140";
          break;
        case "I1":
          //贖回
          otPage = "IOT110";
          break;
        default:
          break;
      }
      openDialog(pathMap.SOT, {
        where: otPage,
        transact: row
      }, 'SOT');
    };

    // 歷史交易明細
    $scope.inquireHis = function () {
      //如查詢區間無資料，可以有訊息文字：查詢區間無商品贖回資料
      //之後記得加訊息文字
      $scope.sendRecv('FPS400', 'inquireHis', 'com.systex.jbranch.app.server.fps.fps400.FPS400InputVO', {
          planID: fps300.getPlanID(),
          custID: fps300.inputVO.custID,
          sDate: $scope.inputVO.SD,
          eDate: $scope.inputVO.ED
        },
        function (tota, isError) {
          if (!isError) {
            $scope.transDetailList = tota[0].body.outputList;
          }
        });

    };

    /**
     * 去那兒
     */
    $scope.go = function (where, data) {
      switch (where) {
        case 'print':
          openDialog('./assets/txn/FPS412/FPS412PrintPreview.html', {
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
            recommendations: $scope.recommendations,
            paramListStock: $scope.paramListStock,
            mixedParamList: $scope.mixedParamList
          }, 'print');
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
          popCbFn[type](data.value);
        }
      });
    };

    // record old value of ntdpercent or chgpercent
    $scope.setHisValue = function (val) {
      tempHisValue = parseFloat(val) || 0;
      $scope.isEditing = true;
    };

    //計算調整後約當台幣
    $scope.calNTD = function (row) {
      totalNTD = totalNTD || 0;
      totalNTD -= row.NTD_PERCENT || 0;
      row.CHG_PERCENT = row.CHG_PERCENT || 0;
      // 調整後約當台幣
      row.NTD_PERCENT = fps300.floatFormat((parseFloat(row.CHG_PERCENT || 0) * (row.CURRENCY_TYPE === 'TWD' ? 1 : curRate(row.CURRENCY_TYPE))), 4) || 0;
      totalNTD += row.NTD_PERCENT || 0;
      $scope.isEditing = false;
      calPercent();
    };

    $scope.calOri = function (row) {
      totalNTD = totalNTD || 0;
      totalNTD -= tempHisValue;
      row.NTD_PERCENT = row.NTD_PERCENT || 0;
      var ntdP = fps300.floatFormat((parseFloat(row.NTD_PERCENT || 0)), 2);
      // 調整後原幣
      row.CHG_PERCENT = fps300.floatFormat((parseFloat(row.NTD_PERCENT || 0) / (row.CURRENCY_TYPE === 'TWD' ? 1 : curRate(row.CURRENCY_TYPE))), 4) || 0;
      totalNTD += ntdP;
      $scope.isEditing = false;
      tempHisValue = ntdP;
      calPercent();
    };

    var calPercent = function () {
      var invPercent = 0;
      $scope.mixedParamList.forEach(function (row) {
        row.INV_PERCENT = parseInt(row.NTD_PERCENT * 100 / totalNTD);
        invPercent += row.INV_PERCENT;
      });
      if ($scope.mixedParamList.length > 0 && invPercent < 100) {
        $scope.mixedParamList[$scope.mixedParamList.length - 1].INV_PERCENT = $scope.mixedParamList[$scope.mixedParamList.length - 1].INV_PERCENT + (100 - invPercent);
      }
      drawDoughnut();
    };

    // draw chart
    var drawDoughnut = function () {
      $timeout(function () {
        // before
        classDought('b_classChart', $scope.paramListStock, $scope.chartTables, 'STORE_NTD');
        stockDought('b_stockChart', $scope.paramListStock, $scope.chartTables, 'STORE_NTD');
        // after
        classDought('a_classChart', $scope.mixedParamList, $scope.chartTables, 'NTD_PERCENT');
        stockDought('a_stockChart', $scope.mixedParamList, $scope.chartTables, 'NTD_PERCENT');
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
            indexLabelLineColor: '#ffffff',
            INV_PERCENT: 0
          };
          textIndex++;
        } else {
          temp[row.NAME].amt += row[key] || 0;
        }
        temp[row.NAME].INV_PERCENT += row.INV_PERCENT;
        cntTotal += row[key] || 0;
      });

      var dataPoints = Object.keys(temp).map(function (key, mapIndex) {
        //            temp[key].y = Math.round((temp[key].amt / (cntTotal || 1)) * 100);
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
        backgroundColor: '#ffffff',
        data: [{
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
//        if (!row.FUND_TYPE_NAME) row.FUND_TYPE_NAME = '平衡型';
        if (temp[row.FUND_TYPE_NAME] === undefined) {
          temp[row.FUND_TYPE_NAME] = {
            amt: row[key] || 0,
            legendText: row.FUND_TYPE_NAME,
            indexLabelLineColor: '#ffffff',
            INV_PERCENT: 0
            // indexLabel: row.INV_PERCENT + '%'
          };
          textIndex++;
        } else {
          temp[row.FUND_TYPE_NAME].amt += row[key] || 0;
        }
        temp[row.FUND_TYPE_NAME].INV_PERCENT += row.INV_PERCENT;
        cntTotal += row[key] || 0;
      });

      var dataPoints = Object.keys(temp).map(function (key, mapIndex) {
        //          temp[key].y = Math.round((temp[key].amt / (cntTotal || 1)) * 100);
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
        backgroundColor: '#ffffff',
        data: [{
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

    /* helping function */
    var curRate = function (type) {
        if (type === 'TWD') {
          return 1;
        }
        var temp = fps300.rate.filter(function (row) {
          return row.CUR_COD === type;
        }) || [];
        return temp.length > 0 ? parseFloat(temp[0].BUY_RATE) : 0;
      };

    // 合併
    var mergeBy = function (list, keys, mergeFn) {
      var newList = [];
      var mergeTable = {};
      var cnt = 0;
      // find same key
      list.forEach(function (row, index) {
        var id = keys.reduce(function (a, b) {
          return a += row[b];
        }, '');
        if (mergeTable[id] === undefined) {
          mergeTable[id] = cnt;
          cnt += 1;
        } else {
          row.mergeIndex = mergeTable[id];
        }
      });
      // push by index
      list.forEach(function (row) {
        if (row.mergeIndex === undefined) {
          newList.push(row);
        } else {
          mergeFn(newList[row.mergeIndex], row);
        }
      });

      return newList;
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
    param()
      .then(function (success) {
        $scope.init();
        $scope.inquireHis();
        $scope.inquire();
      }, function (err) {
        console.log(err);
      });
  }
);
