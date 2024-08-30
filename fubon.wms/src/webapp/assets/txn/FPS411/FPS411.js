/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS411Controller',
  function ($scope, $controller, ngDialog, $timeout, $q, $confirm, $filter, FPSUtilsService) {
    $controller('BaseController', {
      $scope: $scope
    });

    $scope.controllerName = 'FPS411Controller';
    $scope.checkFps320 = false;
    /* parameter */
    var fps300 = $scope.fps300;
    $scope.calInvestmentText = '';
    $scope.calModelPorfolioText = '';
    $scope.showFPS323Error = false;
    $scope.showFPS330Error = false;
    $scope.isOld = fps300.getIsOld() || false;
    $scope.currentProgress = fps300.getCurrentProgress() || {};
    $scope.recommendations = fps300.getRecommendations() || {};
    $scope.paramListStock = fps300.getParamListOrg();
    //取得客戶風險屬性
    var custRiskAttr = fps300.getCustData().KYC_LEVEL;
    var custRiskAttrNumber = fps300.getCustData().KYC_LEVEL.substring(1, 2);
    //選擇投資組合用
    var riskType = $scope.recommendations.CUST_RISK_ATTR_NAME;
    var INV_AMT_TYPE = $scope.recommendations.ONETIME ? '1' : '2';
    var stockAmt = fps300.floatFormat(((parseFloat($scope.recommendations.ONETIME) + parseFloat($scope.recommendations.PERMONTH))), 0);

    $scope.isCreate = fps300.getPlanID() ? false : true;
    $scope.mapping = {
      trendPath: {
        H: 'assets/images/ic-unbiased.svg',
        B: 'assets/images/ic-rise.svg',
        S: 'assets/images/ic-decline.svg'
      },

      stockMap: [{
        title: '基金',
        path: 'assets/txn/FPS/FPSProd_MFD.html',
        order: 0,
        active: true
      }, {
        title: '投資型保險',
        path: 'assets/txn/FPS/FPSProd_InvestIns.html',
        order: 1,
        active: false
      }],
    };

    // certificate title
    var certificateTitle = {
      MFD: '憑證編號',
      INS: '保單號碼',
    };

    var totalNTD = 0;
    var tempHisValue = 0;
    $scope.isEditing = false; // if isediting listen change and update calNTD or calOri
    $scope.isReb = true; //true:顯示 單筆投入總額(NT$)、定期投入總額(NT$)

    var rateMap = {};
    var pathMap = {
      product: './assets/txn/FPS/FPSProd.html',
      models: './assets/txn/FPS230/FPS230Models.html',
      detail: {
        MFD: './assets/txn/PRD110/PRD110_DETAIL.html',
        INS: './assets/txn/PRD160/PRD160_DETAIL.html'
      }
    };

    var mergeKey = {
      stock: ['PRD_ID', 'CURRENCY_TYPE', 'CmbCur']
    };
    var mergeCb = function (standard, obj) {
      standard.isStock = standard.isStock || obj.isStock;
      standard.PURCHASE_TWD_AMT = Number(standard.PURCHASE_TWD_AMT || 0) + Number(obj.PURCHASE_TWD_AMT || 0);
      standard.PURCHASE_ORG_AMT = Number(standard.PURCHASE_ORG_AMT || 0) + Number(obj.PURCHASE_ORG_AMT || 0);
      standard.STORE_NTD = Number(standard.STORE_NTD || 0) + Number(obj.STORE_NTD || 0);
      standard.STORE_RAW = Number(standard.STORE_RAW || 0) + Number(obj.STORE_RAW || 0);
      standard.INV_PERCENT = Number(standard.INV_PERCENT || 0) + Number(obj.INV_PERCENT || 0);
    };

    // custID | planID
    var planID = fps300.getPlanID() || null;
    var custID = fps300.inputVO.custID;

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

    var featureDescription = [];

    // ugly guy for status
    $scope.hitRateGuy = {
      '1': './assets/images/money_SVG/money_happy.svg',
      '0': './assets/images/money_SVG/money_shoulder.svg',
      '-1': './assets/images/money_SVG/money_sweat.svg'
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
        'FPS.INV_TYPE',
        'SOT.NF_MIN_BUY_AMT_1',
        'SOT.NF_MIN_BUY_AMT_2'
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
        deferred.resolve('success');
      }, function (err) {
        deferred.reject(false);
      });

      return deferred.promise;
    };

    /* init */
    $scope.init = function () {
      $scope.inputVO = {};
      var fps300 = $scope.fps300;
      //上一頁返回
      if ($scope.currentProgress === 'ReturnFPS411' || $scope.isOld) {
        if ($scope.currentProgress === 'ReturnFPS411') {
          $scope.marketOverview = fps300.getMarketOverview();
          $scope.paramListStock = fps300.getParamListOrg();
        }
        $scope.recommendations = fps300.getRecommendations();
        $scope.mixedParamList = fps300.getParamList();
        totalNTD = 0;
        $scope.mixedParamList.forEach(function (row) {
          totalNTD += row.NTD_PERCENT;
        });
        drawDoughnut();
        checkTrial();
      } else {
        $scope.inquire();
      }

      // 進度狀態
      getStatusBar();
    };

    /* main function */
    var getStatusBar = function () {
      $scope.sendRecv('FPS400', 'inquire', 'com.systex.jbranch.app.server.fps.fps400.FPS400InputVO', {
          planID: fps300.getPlanID() || null,
          custID: fps300.inputVO.custID,
          riskType: $scope.recommendations.CUST_RISK_ATTR_NAME || fps300.getRISK_ATTR()
        },
        function (tota, isError) {
          if (!isError) {
            //成立日期
            fps300.setPlanDate(tota[0].body.planDate.substring(0, 10).replace(/[-]+/g, '/'));
            //預計到期日期
            $scope.overDateTmp = $scope.getPreDay((parseInt(fps300.getPlanDate().substring(0, 4)) + parseInt($scope.recommendations.PLANHEAD)) + fps300.getPlanDate().substring(4, 10));
            fps300.setOverDate($scope.overDateTmp);
            $scope.achivedParamList = tota[0].body.outputList;
            angular.forEach($scope.achivedParamList, function (value, key) {
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

    // sppolist and stocklist
    $scope.inquire = function () {
      var deferred = $q.defer();

      $scope.sendRecv('FPS410', 'inquire', 'com.systex.jbranch.app.server.fps.fps410.FPS410InputVO', {
          planID: fps300.getPlanID() || null,
          custID: fps300.inputVO.custID,
          riskType: $scope.recommendations.CUST_RISK_ATTR_NAME || fps300.getRISK_ATTR(),
          action: 'step2'
        },
        function (tota, isError) {
          console.log(tota);
          if (!isError) {
            totalNTD = 0;
            $scope.marketOverview = tota[0].body.marketOverview;
            fps300.setMarketOverview($scope.marketOverview);
            $scope.paramList = tota[0].body.sppList;
            $scope.paramListStock = tota[0].body.outputList;
            $scope.recommendSettings = tota[0].body.settingList;
            featureDescription = tota[0].body.featureDescription;

            $scope.achivedParamList = tota[0].body.achivedParamList;
            angular.forEach($scope.achivedParamList, function (value, key) {
              $scope.HIT_RATE_DESC = value.HIT_RATE_DESC;
              $scope.HIT_RATE = value.HIT_RATE;
              $scope.MARKET_VALUE = value.MARKET_VALUE;
              $scope.AMT_TARGET = value.AMT_TARGET;
              $scope.RETURN_RATE = value.RETURN_RATE;
              $scope.INV_AMT_CURRENT = value.INV_AMT_CURRENT;
            });

            // about settings
            if ($scope.recommendSettings && $scope.recommendSettings.length > 0) {
              $scope.recommendSettings = $scope.recommendSettings[0];
              $scope.recommendations.PLANNAME = $scope.recommendSettings.INV_PLAN_NAME;
              $scope.recommendations.PLANHEAD = $scope.recommendSettings.INV_PERIOD;
              $scope.recommendations.ONETIME = $scope.recommendSettings.INV_AMT_ONETIME;
              $scope.recommendations.PERMONTH = $scope.recommendSettings.INV_AMT_PER_MONTH;
              $scope.recommendations.TARGET = $scope.recommendSettings.INV_AMT_TARGET;
              $scope.recommendations.CUST_RISK_ATTR_NAME = $scope.recommendSettings.VOL_RISK_ATTR;
              // 儲存特定目的投資資料
              $scope.recommendations.STORE_PLANNAME = $scope.recommendSettings.INV_PLAN_NAME;
              $scope.recommendations.STORE_PLANHEAD = $scope.recommendSettings.INV_PERIOD;
              $scope.recommendations.STORE_ONETIME = $scope.recommendSettings.INV_AMT_ONETIME;
              $scope.recommendations.STORE_PERMONTH = $scope.recommendSettings.INV_AMT_PER_MONTH;
              $scope.recommendations.STORE_TARGET = $scope.recommendSettings.INV_AMT_TARGET;
              $scope.recommendations.STORE_CUST_RISK_ATTR_NAME = $scope.recommendSettings.VOL_RISK_ATTR;
              fps300.setRecommendations($scope.recommendations);
            }

            fps300.rate = tota[0].body.rate;
            if (fps300.rate.length > 0) {
              fps300.rate.forEach(function (row) {
                rateMap[row.CUR_COD] = {
                  buy: row.BUY_RATE
                };
              });
              rateMap.TWD = {
                buy: 1
              };
            }

            totalNTD = $scope.paramList.reduce(function (a, b) {
              return a + b.NTD_PERCENT;
            }, 0);

            // calculate
            $scope.paramList.forEach(function (row) {
              row.isStock = false;
              reformatList(row);
            });

            // 以下單百分比重算
            var totalNtdStock = $scope.paramListStock.reduce(function (a, b) {
              return a + b.STORE_NTD;
            }, 0);
            var invPercent = $scope.paramListStock.reduce(function (a, b) {
              b.PORTFOLIO_RATIO = Math.round(b.STORE_NTD * 100 / totalNtdStock);
              return a + b.PORTFOLIO_RATIO;
            }, 0);

            if (invPercent < 100 && $scope.paramListStock && $scope.paramListStock.length > 0) {
              $scope.paramListStock[$scope.paramListStock.length - 1].PORTFOLIO_RATIO = $scope.paramListStock[$scope.paramListStock.length - 1].PORTFOLIO_RATIO + (100 - invPercent);
            }
            $scope.paramListStock.forEach(function (row) {
              row.isStock = true;
              reformatList(row);
              row.tooltipInfo = certificateTitle[row.PTYPE] !== undefined ? (
                  (certificateTitle[row.PTYPE] ? (certificateTitle[row.PTYPE] + ': ' + row.CERT_NBR + ' / ') : '') +
                  ((row.TRUST_CURR && row.TRUST_CURR === 'TWD') ? ('台幣投入金額: ' + FPSUtilsService.numberWithCommas(row.INV_AMT_TWD)) : ('投入金額: ' + FPSUtilsService.numberWithCommas(row.INV_AMT))) + ' / ' +
                  ((row.TRUST_CURR && row.TRUST_CURR === 'TWD') ? ('台幣含息報酬率: ' + row.RTN_RATE_WD_TWD) : ('含息報酬率: ' + row.RTN_RATE_WD)) + '% / ' +
                  '申請日期: ' + new Date(row.DATA_DATE).yyyyMMdd('/')) :
                '';
            });

            fps300.setParamListOrg($scope.paramListStock);
            // todo: merge then
            $scope.mixedParamList = angular.copy($scope.paramListStock.concat($scope.paramList));
            fps300.setParamList($scope.mixedParamList);
            $scope.mixedParamList.forEach(function (row) {
              getTxnType(row, true);
              $scope.chgStore(row, true);
            });
            
            checkTrial();
            drawDoughnut();
            deferred.resolve('success');
            return true;
          }
          $scope.showErrorMsg(tota);
          return false;
        });
      return deferred.promise;
    };

    // 改變幣別
    $scope.chgCur = function (row) {
      if (!row.CmbCur) {
        row.CmbCur = '1';
      }
      if (row.CmbCur === '2') {
        row.CHG_PERCENT = Math.round(parseFloat(row.NTD_PERCENT));
      } else {
        row.CHG_PERCENT = Math.round(parseFloat(row.NTD_PERCENT) / curRate(row.CURRENCY_TYPE)) || 0;
      }
      if (row.CmbCur === '1') {
        if (row.INV_TYPE === '2') {
          row.LowMoney = row.SML_SUBS_MINI_AMT_FOR;
        } else if (row.INV_TYPE === '1') {
          row.LowMoney = row.GEN_SUBS_MINI_AMT_FOR;
        } else {
          row.LowMoney = 0;
        }
      } else {
        if (row.INV_TYPE === '2') {
          row.LowMoney = $filter('filter')($scope.mapping.invType2Limit, {
            DATA: 'TWD'
          });
          row.LowMoney = row.LowMoney[0].LABEL;
        } else {
          row.LowMoney = $filter('filter')($scope.mapping.invType1Limit, {
            DATA: 'TWD_FOREIGN'
          });
          row.LowMoney = row.LowMoney[0].LABEL;
        }
      }
      
      if(row.ORDER_STATUS == 'Y') {
    	  row.LowMoney = row.SML_SUBS_MINI_AMT_FOR; 
      }
    };

    // 改變金額
    $scope.chgStore = function (row, stopTrial) {
      row.lowerEdge = false;
      row.CHG_PERCENT = row.CHG_PERCENT ? Math.floor(row.CHG_PERCENT) : '0';
      if (row.ORDER_STATUS === 'Y') {
        if (row.TRUST_CURR === 'TWD' ? row.CHG_PERCENT > row.STORE_NTD : row.CHG_PERCENT > row.STORE_RAW) {
          row.lowerEdge = true;
        }
      } else {
        if (parseFloat(row.CHG_PERCENT) < parseFloat(row.LowMoney)) {
          row.lowerEdge = true;
        }
      }

      if (row.PTYPE === 'INS') {
        row.CmbCur = '1';
      }
      row.NTD_PERCENT = row.CmbCur === '2' ? parseFloat(row.CHG_PERCENT) : fps300.floatFormat((parseFloat(row.CHG_PERCENT) * curRate(row.CURRENCY_TYPE)), 2) || 0;
      getTxnType(row);

      totalNTD = 0;
      $scope.mixedParamList.forEach(function (row) {
        totalNTD += row.NTD_PERCENT;
      });
      calPercent();
      if (!stopTrial)
        checkTrial();
    };

    // 改變投資方式
    $scope.changeInvType = function (row) {
      if (!row.INV_TYPE) {
        row.INV_TYPE = '1';
      }
      //若交易指示下拉選單為'定期定額'，則對應原幣最低申購金額 = TBPRD_FUND.SML_SUBS_MINI_AMT_FOR
      if (row.CmbCur === '1') {
        if (row.INV_TYPE === '2') {
          row.LowMoney = row.SML_SUBS_MINI_AMT_FOR;
        } else if (row.INV_TYPE === '1') {
          row.LowMoney = row.GEN_SUBS_MINI_AMT_FOR;
        } else {
          row.LowMoney = 0;
        }
      } else {
        if (row.INV_TYPE === '2') {
          row.LowMoney = $filter('filter')($scope.mapping.invType2Limit, {
            DATA: 'TWD'
          });
          row.LowMoney = row.LowMoney[0].LABEL;
        } else {
          row.LowMoney = $filter('filter')($scope.mapping.invType1Limit, {
            DATA: 'TWD_FOREIGN'
          });
          row.LowMoney = row.LowMoney[0].LABEL;
        }
      }
      if(row.ORDER_STATUS == 'Y') {
    	  row.LowMoney = row.SML_SUBS_MINI_AMT_FOR; 
      }
      $scope.chgStore(row);
    };

    // 儲存btn
    $scope.save = function () {
      save();
    };

    var save = function (isGoFps412) {
      var inputVO = {
        custID: fps300.inputVO.custID,
        sppType: $scope.recommendations.planningCode,
        planName: $scope.recommendations.PLANNAME,
        planID: fps300.getPlanID(),
        RISK_ATTR: custRiskAttr,
        VOL_RISK_ATTR: $scope.recommendations.CUST_RISK_ATTR_NAME,
        INV_PERIOD: $scope.recommendations.PLANHEAD === '' ? '0' : $scope.recommendations.PLANHEAD,
        INV_AMT_ONETIME: $scope.recommendations.ONETIME === '' ? '0' : $scope.recommendations.ONETIME,
        INV_AMT_PER_MONTH: $scope.recommendations.PERMONTH === '' ? '0' : $scope.recommendations.PERMONTH,
        INV_AMT_TARGET: $scope.recommendations.TARGET === '' ? '0' : $scope.recommendations.TARGET,
      };
      inputVO.prdList = [];
      $scope.mixedParamList.forEach(function (row) {
        if (row.CHG_PERCENT !== row.STORE_RAW) {
          var temp = {
            PLAN_ID: row.PLAN_ID || null,
            SEQNO: row.SEQNO || null,
            PRD_ID: row.PRD_ID,
            PTYPE: row.PTYPE,
            RISK_TYPE: row.RISK_TYPE,
            CURRENCY_TYPE: row.CURRENCY_TYPE,
            TRUST_CURR: row.CmbCur === '1' ? row.CURRENCY_TYPE : 'TWD',
            MARKET_CIS: row.CIS_3M,
            PURCHASE_ORG_AMT: row.CHG_PERCENT,
            PURCHASE_TWD_AMT: row.NTD_PERCENT,
            PORTFOLIO_RATIO: row.INV_PERCENT,
            LIMIT_ORG_AMT: row.LowMoney,
            TXN_TYPE: row.TxnType,
            INV_TYPE: row.INV_TYPE,
            STATUS: 'S',
            EX_RATE: row.CURRENCY_TYPE === 'TWD' ? 1 : curRate(row.CURRENCY_TYPE),
            PURCHASE_ORG_AMT_ORDER: (row.CURRENCY_TYPE === 'TWD' ? row.STORE_NTD : row.STORE_RAW),
            PURCHASE_TWD_AMT_ORDER: row.STORE_NTD,
            ORDER_STATUS: row.ORDER_STATUS,
            PRD_SOURCE_FLAG: row.status,
            RETURN_RATE: row.RETURN_RATE,
            TARGETS: row.TARGETS,
            // CERTIFICATE_ID: row.CERTIFICATE_ID,
          };
          inputVO.prdList.push(temp);
        }
      });
      $scope.sendRecv('FPS410', 'save', 'com.systex.jbranch.app.server.fps.fps410.FPS410InputVO', inputVO,
        function (tota, isError) {
          if (!isError) {
            if (!isGoFps412) {
              $scope.showSuccessMsg('ehl_01_common_025'); //儲存成功
            }
            fps300.setPlanID(tota[0].body[0]);

            $scope.achivedParamList = tota[0].body[1];
            angular.forEach($scope.achivedParamList, function (value, key) {
              $scope.HIT_RATE_DESC = value.HIT_RATE_DESC;
              $scope.HIT_RATE = value.HIT_RATE;
              $scope.MARKET_VALUE = value.MARKET_VALUE;
              $scope.AMT_TARGET = value.AMT_TARGET;
              $scope.RETURN_RATE = value.RETURN_RATE;
              $scope.INV_AMT_CURRENT = value.INV_AMT_CURRENT;
            });
            $scope.isCreate = false;
            fps300.setParamList($scope.mixedParamList);
          }
        });
    };

    //儲存FPS320的改變
    $scope.saveForFPS320 = function () {
      var deferred = $q.defer();
    	
      var inputVO = {
        custID: fps300.inputVO.custID,
        sppType: $scope.recommendations.planningCode,
        planName: $scope.recommendations.PLANNAME,
        planID: fps300.getPlanID(),
        RISK_ATTR: custRiskAttr,
        VOL_RISK_ATTR: $scope.recommendations.CUST_RISK_ATTR_NAME,
        INV_PERIOD: $scope.recommendations.PLANHEAD === '' ? '0' : $scope.recommendations.PLANHEAD,
        INV_AMT_ONETIME: $scope.recommendations.ONETIME === '' ? '0' : $scope.recommendations.ONETIME,
        INV_AMT_PER_MONTH: $scope.recommendations.PERMONTH === '' ? '0' : $scope.recommendations.PERMONTH,
        INV_AMT_TARGET: $scope.recommendations.TARGET === '' ? '0' : $scope.recommendations.TARGET,
      };
      inputVO.prdList = [];
      $scope.sendRecv('FPS410', 'save', 'com.systex.jbranch.app.server.fps.fps410.FPS410InputVO', inputVO,
        function (tota, isError) {
          if (!isError) {
//            $scope.showSuccessMsg('ehl_01_common_025'); //儲存成功
            riskType = $scope.recommendations.CUST_RISK_ATTR_NAME;
            // 儲存特定目的投資資料
//            $scope.recommendations.STORE_PLANHEAD = $scope.recommendSettings.INV_PERIOD;
//            $scope.recommendations.STORE_TARGET = $scope.recommendSettings.INV_AMT_TARGET;
//            $scope.recommendations.STORE_CUST_RISK_ATTR_NAME = $scope.recommendSettings.VOL_RISK_ATTR;
            fps300.setRecommendations($scope.recommendations);
            checkTrial();
            
            deferred.resolve('success');
          } else {
        	  deferred.reject(false);
          }
        });
      
      return deferred.promise;
    };
    
    //checkValue(row.LowMoney, row.CHG_PERCENT, row.STORE_RAW, row.ORDER_STATUS)
    $scope.checkValue = function (row) {
    	if (row.ORDER_STATUS == 'Y' && (row.TRUST_CURR === 'TWD' ? row.CHG_PERCENT > row.STORE_NTD : row.CHG_PERCENT > row.STORE_RAW)) {
    		$scope.showErrorMsg('庫存商品僅限(部分)贖回/賣出；申購請點選「新增商品」');
    		row.CHG_PERCENT = row.STORE_RAW;
    		return;
    	}
    	
    	if (row.ORDER_STATUS == 'Y' && parseInt(row.CHG_PERCENT) != 0 && parseInt(row.LowMoney) > 0 && 
    			parseInt(row.CHG_PERCENT) < parseInt(row.STORE_RAW) &&
    			(parseInt(row.STORE_RAW) - parseInt(row.CHG_PERCENT)) < parseInt(row.LowMoney)) {
    		var suggestVal = (parseInt(row.STORE_RAW) - parseInt(row.LowMoney)).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    		$scope.showErrorMsg('輸入的金額低於贖回門檻，建議您可以調整為' + suggestVal + '，請重新填寫。');
    		row.CHG_PERCENT = row.STORE_RAW;
    		return
    	}
    		
    	if (row.ORDER_STATUS != 'Y' && parseInt(row.LowMoney) > parseInt(row.CHG_PERCENT)) {
    		$scope.showErrorMsg('調整後原幣金額低於原幣申購門檻/累進金額，請重新填寫。');  
    		row.CHG_PERCENT = undefined;
            return;
        }
    }

    /**
     * 去那兒
     */
    $scope.go = function (where, type, data) {
      switch (where) {
        case 'nextFPS412':
          var checkMoney = false;
          var checkBackMoney = false;
          $scope.mixedParamList.forEach(function (row) {
            if (row.TxnType === 'F1' || row.TxnType === 'I1' || row.TxnType === 'F5') {
              if (parseFloat(row.CHG_PERCENT) < parseFloat(row.LowMoney)) {
                checkMoney = true;
              }
            }
            if (row.ORDER_STATUS === 'Y') {
              if (parseFloat(row.CHG_PERCENT) > parseFloat(row.STORE_RAW)) {
                checkBackMoney = true;
              }
            }
          });
          if (checkBackMoney) {
            $scope.showErrorMsg('庫存商品僅限(部分)贖回/賣出；申購請點選「新增商品」');
            return;
          }
          if (checkMoney) {
            $scope.showErrorMsg('調整後原幣金額低於原幣申購門檻/累進金額，請重新填寫。');
            return;
          }
          var ONETIME_TEMP = 0;
          var PERMONTH_TEMP = 0;
          $scope.mixedParamList.forEach(function (row) {
            //保險算入單筆投資
            if (row.INV_TYPE === '2' && row.PTYPE === 'MFD') {
              PERMONTH_TEMP += row.NTD_PERCENT;
            } else {
              ONETIME_TEMP += row.NTD_PERCENT;
            }
          });
          //目標金額<投入金額時，需控管
          if (parseInt($scope.recommendations.TARGET) < (ONETIME_TEMP + (PERMONTH_TEMP * parseInt($scope.recommendations.PLANHEAD) * 12))) {
            $scope.showErrorMsg('設定之目標金額需大於投入金額，請重新輸入。');
            return;
          }
          save(true);
          $scope.mixedParamList.forEach(function (row) {
            row.TRUST_CURR = row.CmbCur === '1' ? row.CURRENCY_TYPE : 'TWD'
            for (var i = 0; i < fps300.getParamListOrg().length; i++) {
              if (row.PRD_ID === fps300.getParamListOrg()[i].PRD_ID) {
                row.INV_PERCENT_OLD = fps300.getParamListOrg()[i].PORTFOLIO_RATIO;
              }
            }
          });
          fps300.setParamList($scope.mixedParamList);
          fps300.chgTab('FPS412');
          break;
        case 'stock':
          openDialog(pathMap.product, $scope.mapping[where + 'Map'], where);
          break;
        case 'product':
          var productMap = angular.copy($scope.mapping.stockMap).map(function (row, index) {
            row.active = data.PTYPE === 'MFD' && index === 0;
            if (row.active) row.data = {
              prdID: data.PRD_ID
            };
            return row;
          });
          openDialog(pathMap.product, productMap, 'product');
          break;
        case 'detail':
          openDialog(pathMap.detail[type], data, type);
          break;
        case 'models':
          openDialog(pathMap.models, {
            riskType: riskType,
            stockAmt: stockAmt,
            featureDescription: featureDescription,
            mapping: $scope.mapping,
            rateMap: rateMap,
            returnAll: false,
            hasIns: true,
            txnID: 'FPS324',
            txnVO: 'com.systex.jbranch.app.server.fps.fps324.FPS324InputVO',
            inputVO: {
              riskType: riskType,
              INV_AMT_TYPE: INV_AMT_TYPE
            },
            sppType: 'SPP'
          }, where);
          break;
        default:
          break;
      }
    };

    var openDialog = function (path, data, type) {
      if (!path) return false;
      // type: deposit|fixed|stock|detail
      var scope = $scope;
      var dialog = ngDialog.open({
        template: path,
        className: 'FPS200',
        controller: ['$scope', function ($scope) {
          $scope.data = data;
          $scope.trendPath = scope.mapping.trendPath;
          $scope.riskType = type === 'models' ? riskType : custRiskAttr;
          $scope.row = angular.copy(data);
          switch (type) {
            case 'product':
              $scope.isMore = false;
              $scope.showInfo = true;
              break;
            case 'MFD':
              $scope.row.FUND_CNAME = data.PRD_CNAME;
              break;
            case 'INS':
              $scope.row.INSPRD_NAME = data.PRD_CNAME;
              break;
          }
          $scope.cust_id = custID;
          $scope.hasIns = scope.hasIns;
        }]
      });

      dialog.closePromise.then(function (data) {
        if (typeof (data.value) === 'object') {
          if (data.value.length > 0) {
            popCbFn[type](data.value);
          }
          totalNTD = 0;
          $scope.mixedParamList.forEach(function (row) {
            totalNTD += row.NTD_PERCENT;
          });
          drawDoughnut();
        }
      });
    };

    var popCbFn = {
      models: function (data) {
        var checkPrdID = true;
        $scope.modelsParamList = formatParam(data);
        if (checkPrdID) {
          if ($scope.modelsParamList.length > 0) {
            $scope.mixedParamList = $scope.mixedParamList.concat($scope.modelsParamList);
          }
        }
      },
      stock: function (data) {
        var checkPrdID = true;
        $scope.mfdModelsParamList = [];
        $scope.insModelsParamList = [];
        data.forEach(function (row) {
          if (row.title === '基金') {
            if (row.list && row.list.length > 0) {
              $scope.mfdModelsParamList = formatParam(row.list);
            }
          } else {
            if (row.list && row.list.length > 0) {
              $scope.insModelsParamList = formatParam(row.list);
            }
          }
        });

        if (checkPrdID) {
          if ($scope.mfdModelsParamList.length > 0) {
            $scope.mixedParamList = $scope.mixedParamList.concat($scope.mfdModelsParamList);
          }
          if ($scope.insModelsParamList.length > 0) {
            $scope.mixedParamList = $scope.mixedParamList.concat($scope.insModelsParamList);
          }
        }
      },
      product: function (data) {
        var checkPrdID = true;
        $scope.mfdModelsParamList = [];
        $scope.insModelsParamList = [];
        data.forEach(function (row) {
          if (row.title === '基金') {
            if (row.list && row.list.length > 0) {
              $scope.mfdModelsParamList = formatParam(row.list);
            }
          } else {
            if (row.list && row.list.length > 0) {
              $scope.insModelsParamList = formatParam(row.list);
            }
          }
        });

        if (checkPrdID) {
          if ($scope.mfdModelsParamList.length > 0) {
            $scope.mixedParamList = $scope.mixedParamList.concat($scope.mfdModelsParamList);
          }
          if ($scope.insModelsParamList.length > 0) {
            $scope.mixedParamList = $scope.mixedParamList.concat($scope.insModelsParamList);
          }
        }
      }
    };

    var formatParam = function (list) {
      list.forEach(function (row) {
        var txnList = angular.copy($scope.mapping[row.PTYPE + 'TxnType']);
        switch (row.PTYPE) {
          case 'MFD':
            row.txnTypeList = [txnList[0], txnList[4]];
            row.TxnType = 'F1';
            break;
          case 'INS':
            row.txnTypeList = [txnList[0]];
            row.TxnType = 'I1';
            break;
          default:
            break;
        }
        row.CHG_PERCENT = 0;
        row.PLAN_ID = fps300.getPlanID();
        row.action = 'create';
        row.CmbCur = row.CURRENCY_TYPE === 'TWD' ? '2' : '1';
        if ($scope.recommendations.ONETIME) {
          row.INV_TYPE = '1';
        } else {
          row.INV_TYPE = '2';
        }
        //若投資方式下拉選單為'定期定額'，則對應原幣最低申購金額 = TBPRD_FUND.SML_SUBS_MINI_AMT_FOR
        if (row.INV_TYPE === '2') {
          row.LowMoney = row.SML_SUBS_MINI_AMT_FOR;
        } else if (row.INV_TYPE === '1') {
          row.LowMoney = row.GEN_SUBS_MINI_AMT_FOR;
        } else {
          row.LowMoney = 0;
        }
        row.INV_PERCENT = 0;
        if (row.PTYPE === 'INS') {
          row.P_TYPE = '2';
          row.INV_TYPE = '1';
          row.LowMoney = row.GEN_SUBS_MINI_AMT_FOR;
          row.TARGETS = row.targetIDs || row.TARGETS;
        } else {
          row.P_TYPE = '1';
        }
        row.isStock = false;
        if(row.ORDER_STATUS == 'Y') {
      	  row.LowMoney = row.SML_SUBS_MINI_AMT_FOR; 
        }
        $scope.chgStore(row);
        console.log(row);
      });
      return list;
    };


    // 輸入規劃資料
    $scope.openFps320 = function (isCreate) {
      if ($scope.checkFps320) {
        return;
      }
      $scope.checkFps320 = true;
      var scope = $scope;
      console.log($scope.recommendations);

      var dialog = ngDialog.open({
        template: 'assets/txn/FPS320/FPS320.html',
        className: 'FPS411',
        showClose: false,
        controller: ['$scope', function ($scope) {
          $scope.isCreate = true; //isCreate;
          $scope.recommendations = scope.recommendations;
          $scope.fps300 = fps300;
          $scope.isData = scope.isData;
          $scope.planDate = fps300.getPlanDate();
          $scope.overDate = fps300.getOverDate();
          $scope.custRiskAttr = custRiskAttr;
          $scope.custRiskAttrNumber = custRiskAttrNumber;
          //用來判斷是否鎖住投組名稱
          $scope.beacon = 1;
          $scope.isReb = true;
        }]
      });

      dialog.closePromise.then(function (data) {
        $scope.checkFps320 = false;
        if (data.value !== 'cancel') {
          $scope.recommendations = data.value;
          fps300.setRecommendations($scope.recommendations);
          // no use in fps324
          if ($scope.recommendations.CUST_RISK_ATTR !== undefined) {
            if ($scope.recommendations.CUST_RISK_ATTR === '1' ||
              $scope.recommendations.CUST_RISK_ATTR === '2') {
              $scope.recommendations.CUST_RISK_ATTR_NAME = 'C1-' +
                $scope.recommendations.CUST_RISK_ATTR;
            } else if ($scope.recommendations.CUST_RISK_ATTR === '3' ||
              $scope.recommendations.CUST_RISK_ATTR === '4') {
              $scope.recommendations.CUST_RISK_ATTR_NAME = 'C2-' +
                $scope.recommendations.CUST_RISK_ATTR;
            } else if ($scope.recommendations.CUST_RISK_ATTR === '5' ||
              $scope.recommendations.CUST_RISK_ATTR === '6') {
              $scope.recommendations.CUST_RISK_ATTR_NAME = 'C3-' +
                $scope.recommendations.CUST_RISK_ATTR;
            } else if ($scope.recommendations.CUST_RISK_ATTR === '7' ||
              $scope.recommendations.CUST_RISK_ATTR === '8') {
              $scope.recommendations.CUST_RISK_ATTR_NAME = 'C4-' +
                $scope.recommendations.CUST_RISK_ATTR;
            }
          }
          
//          if ($scope.recommendations.PLANNAME !== $scope.recommendations.STORE_PLANNAME ||
//            $scope.recommendations.PLANHEAD !== $scope.recommendations.STORE_PLANHEAD ||
//            Number($scope.recommendations.ONETIME || 0) !== Number($scope.recommendations.STORE_ONETIME || 0) ||
//            Number($scope.recommendations.PERMONTH || 0) !== Number($scope.recommendations.STORE_PERMONTH || 0) ||
//            $scope.recommendations.TARGET !== $scope.recommendations.STORE_TARGET ||
//            $scope.recommendations.CUST_RISK_ATTR_NAME !== $scope.recommendations.STORE_CUST_RISK_ATTR_NAME
//          ) {
//            $confirm({
//              text: '特定目的規劃之〝投資天期〞或〝期初投資〞或〝每月投資〞或〝目標金額〞或〝風險承受度級數〞已變更，同意本次調整內容?'
//            }, {
//              size: 'sm'
//            }).then(function () {
//              // 4
//              $scope.saveForFPS320();
//            }, 
            
            $scope.saveForFPS320().then(function () {
              // four
//              $scope.recommendations.PLANNAME = $scope.recommendations.STORE_PLANNAME;
//              $scope.recommendations.PLANHEAD = $scope.recommendations.STORE_PLANHEAD;
//              $scope.recommendations.ONETIME = Number($scope.recommendations.STORE_ONETIME || 0);
//              $scope.recommendations.PERMONTH = Number($scope.recommendations.STORE_PERMONTH || 0);
//              $scope.recommendations.TARGET = $scope.recommendations.STORE_TARGET;
//              $scope.recommendations.CUST_RISK_ATTR_NAM = $scope.recommendations.STORE_CUST_RISK_ATTR_NAME;
              
              // 進度狀態
              getStatusBar();
            });
//          }
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
      var length = $scope.mixedParamList.length;
      var count = 0;
      totalNTD = 0;
      $scope.mixedParamList.filter(function (row) {
        return row.action !== 'delete';
      }).forEach(function (row, index) {
        totalNTD += fps300.floatFormat(row.NTD_PERCENT, 0);
      });
      $scope.mixedParamList.filter(function (row) {
        return row.action !== 'delete';
      }).forEach(function (row, index) {
        if (index + 1 !== length) {
          row.INV_PERCENT = fps300.floatFormat((row.NTD_PERCENT / totalNTD * 100), 0) || 0;
          count += row.INV_PERCENT;
        } else {
          row.INV_PERCENT = fps300.floatFormat((100 - (count > 100 ? 100 : count)), 0) || 0;
        }
      });
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
        //          temp[key].y = Math.round((temp[key].amt / (cntTotal || 1)) * 100);
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
        //        temp[key].y = Math.round((temp[key].amt / (cntTotal || 1)) * 100);
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

    //刪除row
    $scope.deleteRow = function (index) {
      $confirm({
        title: '警告',
        text: '您確定要刪除此筆資料?'
      }, {
        size: 'sm'
      }).then(function () {
        $scope.mixedParamList.splice(index, 1);
        totalNTD = 0;
        $scope.mixedParamList.forEach(function (row) {
          totalNTD += row.NTD_PERCENT;
        });
        calPercent();
        checkTrial();
      }, function () {});
    };

    //投組試算檢查
    var calAll = function () {
      $scope.calInvestmentText = '';
      $scope.calModelPorfolioText = '';
      var delCount = 0;
      for (var i = 0; i < $scope.mixedParamList.length; i++) {
        var row = $scope.mixedParamList[i];
        if (row.action !== 'delete') {
          if (row.ORDER_STATUS === 'N' && !row.CHG_PERCENT && row.CHG_PERCENT !== 0) {
            return '請於紅框處輸入金額';
          }
          //row.PTYPE=='INS' 保險算入單筆投資
          if (row.PTYPE !== 'INS') {
            if (!row.INV_TYPE) {
              return '請選擇投資方式';
            }
          }
          delCount++;
        }
      }
      if (delCount < 1) {
        return '執行績效模擬投資標的需大於一檔';
      }
      return '';
    }

    $scope.$on('FPS323_error', function (event, data) {
      $scope.showFPS323Error = true;
      $scope.calInvestmentText = data;
    });
    $scope.$on('FPS330_error', function (event, data) {
      $scope.showFPS330Error = true;
      $scope.calModelPorfolioText = data;
    });

    // back FPS320
    $scope.backFPS320 = function () {
      $scope.closeThisDialog($scope.inputVO.total);
    };

    /**
     * 投組試算: 績效模擬(FPS323) + 歷史績效表現(FPS330)
     * @param {bool} isButton
     */
    var checkTrial = function (isButton) {
      $scope.calInvestmentText = '';
      $scope.calModelPorfolioText = '';
      var checkCalAll = calAll();
      if (checkCalAll) {
        $scope.calInvestmentText = checkCalAll;
        $scope.calModelPorfolioText = checkCalAll;
        if (isButton) {
          $scope.showErrorMsg(checkCalAll);
        }
        return;
      }

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

      $scope.showFPS323Error = false;
      $scope.calInvestmentText = '';
      $scope.calModelPorfolioText = '';
      $scope.showFPS330Error = false;
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

    $scope.openFps325 = function (row) {
      var scope = $scope;
      var tempList = $scope.mixedParamList.filter(function (row) {
        return row.action !== 'delete' && row.CHG_PERCENT > 0;
      });
      var listMap = tempList.reduce(function (a, b) {
        a[b.PRD_ID] = b.PRD_ID + ' ' + b.PRD_CNAME;
        return a;
      }, {});
      if (tempList.length === 0) {
        $scope.showErrorMsg('請先新增商品');
      } else if (tempList.length > 10) {
        $scope.showErrorMsg('最佳配置試算僅限配置十檔商品以內');
      } else {
        var dialog = ngDialog.open({
          // SWITCHPRD
          template: 'assets/txn/FPS325/FPS325.html',
          className: '',
          showClose: true,
          controller: ['$scope', function ($scope) {
            $scope.fps300 = fps300;
            $scope.inputVO = {};
            $scope.inputVO.TARGET = scope.recommendations.TARGET;
            $scope.inputVO.PLANHEAD = scope.recommendations.PLANHEAD;
            $scope.inputVO.ONETIME = parseInt(scope.recommendations.ONETIME) + parseInt(scope.recommendations.PERMONTH);
            $scope.inputVO.prdList = tempList;
            $scope.stockAmt = parseInt(scope.recommendations.ONETIME) + parseInt(scope.recommendations.PERMONTH);
            $scope.stockPct = '100';
            $scope.sppType = 'SPP';
            $scope.hasIns = true;
          }]
        });

        dialog.closePromise.then(function (data) {
          if (data.value.status === 'success') {
            for (var i = 0; i < data.value.addList.length; i++) {
              for (var j = 0; j < $scope.mixedParamList.length; j++) {
                if (data.value.addList[i].PRD_ID === $scope.mixedParamList[j].PRD_ID) {
                  $scope.mixedParamList[j].CHG_PERCENT = data.value.addList[i].PURCHASE_ORG_AMT;
                  $scope.mixedParamList[j].NTD_PERCENT = data.value.addList[i].PURCHASE_TWD_AMT;
                  $scope.mixedParamList[j].INV_PERCENT = data.value.addList[i].INV_PERCENT;
                }
              }
            }
          } else if (data.value.status === 'error') {
            data.value.errorList.forEach(function (row) {
              $scope.showMsg('「' + listMap[row] + '」成立期間未滿一年，無法帶入〔最佳配置試算〕');
            });
          }
        });
      }
    };

    /* helping function */
    // reformat 商品 type: add | stock
    var reformatList = function (row) {
      row.CmbCur = row.TRUST_CURR === 'TWD' ? '2' : '1';
      row.STORE_RAW = toIntNumber(row.STORE_RAW);
      row.STORE_NTD = toIntNumber(row.STORE_NTD);
      row.CHG_PERCENT = row.isStock && row.TRUST_CURR === 'TWD' ? toIntNumber(row.PURCHASE_TWD_AMT) : toIntNumber(row.PURCHASE_ORG_AMT);
      row.NTD_PERCENT = toIntNumber(row.PURCHASE_TWD_AMT);
      row.INV_PERCENT = toIntNumber(row.PORTFOLIO_RATIO);
      // 新增庫存的投資比例
      row.STORE_INV_PERCENT = row.PORTFOLIO_RATIO;
      // 投資方式
      row.txnTypeList = $scope.mapping.nTxnType;
      row.txnTypeList2 = $scope.mapping.sTxnType;
      // 若投資方式下拉選單為'定期定額'，則對應原幣最低申購金額 = TBPRD_FUND.SML_SUBS_MINI_AMT_FOR
      if (row.INV_TYPE === '2') {
        row.LowMoney = row.SML_SUBS_MINI_AMT_FOR;
      } else if (row.INV_TYPE === '1') {
        row.LowMoney = row.GEN_SUBS_MINI_AMT_FOR;
      } else {
        row.LowMoney = 0;
      }
      //贖回門檻
      if(row.ORDER_STATUS == 'Y') {
    	  row.LowMoney = row.SML_SUBS_MINI_AMT_FOR; 
      }
      row.status = row.PRD_SOURCE_FLAG || '4'; // 1 新增商品  2 理財標的建議  3加入庫存 4Return
      if (row.PTYPE === 'INS') {
        row.CmbCur = '1';
        row.TARGETS = row.targetIDs || row.TARGETS;
      }
      row.txnTypeList = $scope.mapping[row.PTYPE + 'TxnType'];
    };

    var curRate = function (type) {
      if (type === 'TWD') {
        return 1;
      }
      var temp = fps300.rate.filter(function (row) {
        return row.CUR_COD === type;
      }) || [];
      return temp.length > 0 ? parseFloat(temp[0].BUY_RATE) : 0;
    };

    var toIntNumber = function (num) {
      return Math.round(parseFloat(num)) || 0;
    };
    // 設定row TxnType
    var getTxnType = function (row, isInit) {
      var txnList = angular.copy($scope.mapping[row.PTYPE + 'TxnType']);
      var orgAmt = Number(row.CHG_PERCENT) || 0;
      var orgInvAmt = (row.TRUST_CURR === 'TWD' ? Number(row.STORE_NTD) : Number(row.STORE_RAW)) || 0;

      if (row.ORDER_STATUS === 'Y') {
        //庫存
        switch (row.PTYPE) {
          case 'MFD':
            if (orgAmt >= orgInvAmt) {
              // 不動作
              row.txnTypeList = [txnList[0]];
              row.TxnType = '';
            } else if (orgAmt <= 0) {
              // 贖回、轉出
              row.txnTypeList = [txnList[1], txnList[3]];
              row.TxnType = 'F2';
            } else if (orgAmt < orgInvAmt && orgInvAmt !== 0) {
              // 部分贖回、轉出
              row.txnTypeList = [txnList[2], txnList[3]];
              row.TxnType = 'F3';
            }
            break;
          case 'INS':
            if (orgAmt >= orgInvAmt) {
              // 不動作
              row.txnTypeList = [txnList[0]];
              row.TxnType = '';
            } else if (orgAmt < orgInvAmt) {
              // 贖回
              row.txnTypeList = [txnList[1]];
              row.TxnType = 'I2';
            }
            break;
          default:
            break;
        }
        row.txnTypeListLength = (row.txnTypeList || []).length;
        return;
      }
      //新增
      switch (row.PTYPE) {
        case 'MFD':
          // 申購、贖回、部分贖回、轉出、轉入
          if (!row.STORE_RAW) {
            // 申購、轉入
            row.txnTypeList = [txnList[0], txnList[4]];
            if (isInit) {
              row.TxnType = row.TXN_TYPE;
//              row.TxnType = 'F1';
            }
          } else if (orgAmt === orgInvAmt) {
            // 不動作
            row.txnTypeList = [txnList[0]];
            row.TxnType = '';
          } else if (orgAmt > orgInvAmt) {
            // 申購
            row.txnTypeList = [txnList[0]];
            row.TxnType = 'F1';
          } else if (orgAmt <= 0) {
            // 贖回、轉出
            row.txnTypeList = [txnList[1], txnList[3]];
            row.TxnType = 'F2';
          } else if (orgAmt < orgInvAmt && orgInvAmt !== 0) {
            // 部分贖回、轉出
            row.txnTypeList = [txnList[2], txnList[3]];
            row.TxnType = 'F3';
          } else {
            // 申購、轉入
            row.txnTypeList = [txnList[0], txnList[1], txnList[2], txnList[3]];
            row.TxnType = '';
          }
          break;
        case 'INS':
          // 申購、贖回
          if (!orgInvAmt) {
            // 申購、轉入
            row.txnTypeList = [txnList[0]];
            if (!isInit) {
              row.TxnType = 'I1';
            }
          } else if (orgAmt === orgInvAmt) {
            //不動作
            row.txnTypeList = [txnList[0]];
          } else if (orgAmt > orgInvAmt) {
            // 申購
            row.txnTypeList = [txnList[0]];
            row.TxnType = 'I1';
          } else if (orgAmt < orgInvAmt) {
            // 贖回
            row.txnTypeList = [txnList[1]];
            row.TxnType = 'I2';
          } else {
            row.txnTypeList = txnList;
            row.TxnType = '';
          }
          break;
      }
      row.txnTypeListLength = (row.txnTypeList || []).length;
    };

    $scope.chgTxnType = function (row) {
      if (!row.TxnType && (row.STORE_RAW === '0' || row.STORE_RAW !== row.CHG_PERCENT)) {
        row.TxnType = row.txnTypeList[1].DATA;
      }
    };

    /* main process */
    param()
      .then(function (success) {
        $scope.init();
      }, function (err) {
        console.log(err);
      });
    console.log('FPS411');
  }
);
