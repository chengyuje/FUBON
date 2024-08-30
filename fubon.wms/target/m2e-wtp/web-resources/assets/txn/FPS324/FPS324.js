/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS324Controller',
  function ($scope, $controller, ngDialog, $q, $filter, $confirm, FPSUtilsService) {
    $controller('BaseController', {
      $scope: $scope
    });

    $scope.controllerName = 'FPS324Controller';
    $scope.checkFps320 = false;
    $scope.isFps320 = false;
    $scope.calInvestmentText = '';
    $scope.calModelPorfolioText = '';
    var checkPrdID = true;
    var isChange = 'N';
    $scope.showFPS323Error = false;
    $scope.showFPS330Error = false;

    /* parameter */
    var fps300 = $scope.fps300;
    $scope.currentProgress = fps300.getCurrentProgress() || {};
    $scope.recommendations = fps300.getRecommendations() || {};
    //取得客戶風險屬性
    var custRiskAttr = fps300.getCustData().KYC_LEVEL;
    var custRiskAttrNumber = fps300.getCustData().KYC_LEVEL.substring(1, 2);
    //選擇投資組合用
    var riskType = $scope.recommendations.CUST_RISK_ATTR_NAME;
    var INV_AMT_TYPE = $scope.recommendations.ONETIME ? '1' : '2';

    $scope.isCreate = fps300.getPlanID() ? false : true;
    $scope.mapping = {};
    var totalNTD = 0;
    $scope.isEditing = false; // if isediting listen change and update calNTD or calOri
    var rateMap = {};
    var pathMap = {
      stock: './assets/txn/FPS/FPSProd.html',
      product: './assets/txn/FPS/FPSProd.html',
      models: './assets/txn/FPS230/FPS230Models.html',
      detail: {
        MFD: './assets/txn/PRD110/PRD110_DETAIL.html',
        INS: './assets/txn/PRD160/PRD160_DETAIL.html'
      }
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

    var charts = {
      stockChart: null,
      classChart: null
    };

    $scope.chartTables = {
      stockChart: [],
      classChart: []
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

    // getXML
    var param = function () {
      var deferred = $q.defer();

      fps300.getXmlParam([
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
        // console.log(JSON.stringify($scope.mapping));
        // 選擇投資組合用
        $scope.mapping.prodType = $scope.mapping.pType;
        // 選擇投資組合用
        $scope.mapping.trendPath = {
          H: 'assets/images/ic-unbiased.svg',
          B: 'assets/images/ic-rise.svg',
          S: 'assets/images/ic-decline.svg'
        };
        // 選擇投資組合用
        $scope.mapping.stockMap = $scope.titleMap;
        deferred.resolve('success');
      }, function () {
        deferred.reject(false);
      });

      return deferred.promise;
    };

    /* init */
    $scope.init = function () {
      $scope.inputVO = {};
      // 1/10 add
      // 1/31上一頁返回
      if ($scope.currentProgress === 'ReturnFPS324') {
        $scope.recommendations = fps300.getRecommendations();
        riskType = $scope.recommendations.CUST_RISK_ATTR_NAME;
        INV_AMT_TYPE = $scope.recommendations.ONETIME ? '1' : '2';
        $scope.marketOverview = fps300.getMarketOverview();
        $scope.paramList = fps300.getParamList();
        $scope.paramList.forEach(function (row) {
          totalNTD += row.NTD_PERCENT;
        });
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
      } else {
        if ($scope.isCreate) {
          fps300.setDisBeacon(false);
          $scope.openFps320();
        } else {
          fps300.setDisBeacon(true);
          $scope.inquire();
        }
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

    //基股商品(基金+投資型保險) (判斷Model Portfolio是否有值，若有值則進入主畫面；反之跳出FPS320畫面)
    $scope.inquire = function () {
      var deferred = $q.defer();
      var inputVO = {
        planID: fps300.getPlanID() || null,
        custId: fps300.inputVO.custID,
        riskType: $scope.recommendations.CUST_RISK_ATTR_NAME || fps300.getVOL_RISK_ATTR(),
        INV_AMT_TYPE: $scope.recommendations.ONETIME ? '1' : '2',
        isChange: isChange
      };

      $scope.sendRecv('FPS324', 'inquire', 'com.systex.jbranch.app.server.fps.fps324.FPS324InputVO', inputVO,
        function (tota, isError) {
          console.log(tota);
          if (!isError) {
            totalNTD = 0;
            $scope.marketOverview = tota[0].body.marketOverview;
            fps300.setMarketOverview($scope.marketOverview);
            featureDescription = tota[0].body.featureDescription;
            $scope.paramList = tota[0].body.outputList;
            $scope.recommendList = tota[0].body.recommendList;
            if ($scope.paramList.length === 0 && $scope.recommendList.length === 0) {
              $scope.showErrorMsg('無有效的理財規劃參數。');
              return;
            }

            if ($scope.paramList.length === 0) {
              $scope.paramList = angular.copy($scope.recommendList);
              $scope.isCreate = true;
            }

            // about settings
            if (tota[0].body.settingList && tota[0].body.settingList.length > 0) {
              $scope.recommendSettings = tota[0].body.settingList[0];
              $scope.recommendations.PLANNAME = $scope.recommendSettings.INV_PLAN_NAME;
              $scope.recommendations.PLANHEAD = $scope.recommendSettings.INV_PERIOD;
              $scope.recommendations.ONETIME = $scope.recommendSettings.INV_AMT_ONETIME;
              $scope.recommendations.PERMONTH = $scope.recommendSettings.INV_AMT_PER_MONTH;
              $scope.recommendations.TARGET = $scope.recommendSettings.INV_AMT_TARGET;
              $scope.recommendations.CUST_RISK_ATTR_NAME = $scope.recommendSettings.VOL_RISK_ATTR;
              $scope.recommendations.PLAN_STATUS = $scope.recommendSettings.PLAN_STATUS;
              fps300.setRecommendations($scope.recommendations);
              riskType = $scope.recommendations.CUST_RISK_ATTR_NAME;
              INV_AMT_TYPE = $scope.recommendations.ONETIME ? '1' : '2';
            }

            if (tota[0].body.planDate) {
              //規劃日期
              fps300.setPlanDate(tota[0].body.planDate.substring(0, 10).replace(/[-]+/g, '/'));
              //預計到期日期
              $scope.overDateTmp = $scope.getPreDay((parseInt(fps300.getPlanDate().substring(0, 4)) + parseInt($scope.recommendations.PLANHEAD)) + fps300.getPlanDate().substring(4, 10));
              fps300.setOverDate($scope.overDateTmp);
            }

            if (fps300.custData.kycDisable) {
              $scope.kycDisable = true;
              //不顯示筆圖案
              $scope.isPencil = 'N';
            } else if (parseInt(custRiskAttrNumber) < parseInt($scope.recommendations.CUST_RISK_ATTR_NAME.substring(1, 2))) {
              $scope.kycDisable = true;
              //不顯示筆圖案
              $scope.isPencil = 'N';
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

            // calculate
            $scope.paramList.forEach(function (row) {
              if ($scope.isCreate) {
                reformatSug(row);
              } else {
                if (isChange === 'Y') {
                  reformatSug(row);
                  row.action = 'create';
                } else {
                  reformatList(row);
                  row.action = 'update';
                }
              }
              totalNTD += row.NTD_PERCENT;
              
              row.TxnType = row.PTYPE === 'MFD' ? 'F1' : 'I1';
            });
            
            fps300.setParamList($scope.paramList);

            // draw chart
            drawDoughnut();
            checkTrial();

            deferred.resolve('success');
            isChange = 'N';
            if ($scope.connector('get', 'FPS324TOFPS340') === 'Y') {
              $scope.connector('set', 'FPS324TOFPS340', '');
              fps300.chgTab('FPS340');
            }
            return true;
          }
          isChange = 'N';
          $scope.showErrorMsg(tota);
          return false;
        });
      return deferred.promise;
    };

    $scope.chgCur = function (row) {
      if (!row.CmbCur) {
        row.CmbCur = '1';
      }
      if (row.CmbCur === '2') {
        row.CHG_PERCENT = row.NTD_PERCENT;
      } else {
        row.CHG_PERCENT = fps300.floatFormat((parseFloat(row.NTD_PERCENT) / curRate(row.CURRENCY_TYPE)), 0) || 0;
      }
      if (row.CmbCur === '1') {
        if (row.InvType === '2') {
          row.LowMoney = row.SML_SUBS_MINI_AMT_FOR;
        } else if (row.InvType === '1') {
          row.LowMoney = row.GEN_SUBS_MINI_AMT_FOR;
        } else {
          row.LowMoney = 0;
        }
      } else {
        if (row.InvType === '2') {
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
    };

    $scope.chgStore = function (row) {
      row.CHG_PERCENT = row.CHG_PERCENT ? Math.floor(row.CHG_PERCENT) : '0';
      row.NTD_PERCENT = row.CmbCur === '2' ? parseFloat(row.CHG_PERCENT) : fps300.floatFormat((parseFloat(row.CHG_PERCENT) * curRate(row.CURRENCY_TYPE)), 0) || 0;
      calPercent();
      checkTrial();
    };

    $scope.changeInvType = function (row) {
      if (!row.InvType) {
        row.InvType = '1';
      }
      //若投資方式下拉選單為'定期定額'，則對應原幣最低申購金額 = TBPRD_FUND.SML_SUBS_MINI_AMT_FOR
      if (row.CmbCur === '1') {
        if (row.InvType === '2') {
          row.LowMoney = row.SML_SUBS_MINI_AMT_FOR;
        } else if (row.InvType === '1') {
          row.LowMoney = row.GEN_SUBS_MINI_AMT_FOR;
        } else {
          row.LowMoney = 0;
        }
      } else {
        if (row.InvType === '2') {
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
      
      $scope.chgStore(row);
      
      var ONETIME_TEMP = 0;
      var PERMONTH_TEMP = 0;
      $scope.paramList.forEach(function (row) {
        //保險算入單筆投資
        if (row.InvType === '2' && row.PTYPE === 'MFD') {
          PERMONTH_TEMP += row.NTD_PERCENT;
        } else {
          ONETIME_TEMP += row.NTD_PERCENT;
        }
      });
      if (parseInt($scope.recommendations.TARGET) < (ONETIME_TEMP + (PERMONTH_TEMP * parseInt($scope.recommendations.PLANHEAD) * 12))) {
        $scope.showErrorMsg('設定之目標金額需大於期初投資加每月投資金額，請重新調整 投資方式 或是 投入金額。');
        return false;
      } 
    };
    
    $scope.checkValue = function (lowMoney, invMoney) {
    	if (parseInt(lowMoney) > parseInt(invMoney)) {
            $scope.showErrorMsg('投入金額低於申購門檻，請重新填寫。');
            return false;
          }
    }
    
    //儲存btn
    $scope.save = function (type, toFPS340) {
      var ONETIME_TEMP = 0;
      var PERMONTH_TEMP = 0;
      $scope.paramList.forEach(function (row) {
        //保險算入單筆投資
        if (row.InvType === '2' && row.PTYPE === 'MFD') {
          PERMONTH_TEMP += row.NTD_PERCENT;
        } else {
          ONETIME_TEMP += row.NTD_PERCENT;
        }
      });

      if (type === 'Y') {
    	if(!$scope.paramList || $scope.paramList.length === 0) {
    		$scope.showErrorMsg('請新增商品，投資組合建議不能為空。');
            return false;
    	}  
        for (var i = 0; i < $scope.paramList.length; i++) {
          if (parseInt($scope.paramList[i].LowMoney) > parseInt($scope.paramList[i].CHG_PERCENT)) {
            $scope.showErrorMsg('投入金額低於申購門檻，請重新填寫。');
            return false;
          }
          $scope.checkValue($scope.paramList[i].LowMoney, $scope.paramList[i].CHG_PERCENT);
        	
          if (!$scope.paramList[i].InvType && $scope.paramList[i].PTYPE === 'MFD') {
            $scope.showErrorMsg('請選擇投資方式。');
            return false;
          }
        }

        var resultNormal = $scope.connector('get', 'FPS323.resultNormal');
        $scope.connector('set', 'FPS323.resultNormal', undefined);
        //預期期末金額<目標所需金額，提醒客戶：1. 增加投資天期或 2. 提高投資金額或 3.調降目標金額 4. 提高風險承受度。
        //如客戶選擇自身最高的風險承受度級數時，4. 提高風險承受度 不會顯示
        if (resultNormal < $scope.recommendations.TARGET) {
          var CUST_RISK_ATTR = $scope.recommendations.CUST_RISK_ATTR_NAME.substring(3, 4);
          if (parseInt(CUST_RISK_ATTR) === parseInt(custRiskAttrNumber) * 2) {
            $scope.showMsg('預期期末金額<目標所需金額，提醒客戶：1. 增加投資天期或 2. 提高投資金額或 3.調降目標金額。');
          } else {
            $scope.showMsg('預期期末金額<目標所需金額，提醒客戶：1. 增加投資天期或 2. 提高投資金額或 3.調降目標金額 4. 提高風險承受度。');
          }
        }

        //目標金額<投入金額時，需控管
        if (parseInt($scope.recommendations.TARGET) < (ONETIME_TEMP + (PERMONTH_TEMP * parseInt($scope.recommendations.PLANHEAD) * 12))) {
          $scope.showErrorMsg('設定之目標金額需大於期初投資加每月投資金額，請重新輸入。');
          return false;
        }

        //欲申購的期初投資金額約當台幣XXXX元，每月投資金額約當台幣XXXX元，將更新目標設定資料。
        if (!$scope.isCreate && (Number(ONETIME_TEMP) !== Number($scope.recommendations.ONETIME) ||
            Number(PERMONTH_TEMP) !== Number($scope.recommendations.PERMONTH))) {
          $scope.showMsg('欲申購的期初投資金額約當台幣' + $scope.moneyFormat(ONETIME_TEMP) +
            '元，每月投資金額約當台幣' + $scope.moneyFormat(PERMONTH_TEMP) + '元，將更新目標設定資料。');
          $scope.recommendations.ONETIME = ONETIME_TEMP;
          $scope.recommendations.PERMONTH = PERMONTH_TEMP;
        }
      }
      
      var inputVO = {
        custId: fps300.inputVO.custID,
        sppType: $scope.recommendations.planningCode,
        planName: $scope.recommendations.PLANNAME,
        planID: fps300.getPlanID(),
        RISK_ATTR: custRiskAttr,
        VOL_RISK_ATTR: $scope.recommendations.CUST_RISK_ATTR_NAME,
        INV_PERIOD: $scope.recommendations.PLANHEAD === '' ? '0' : $scope.recommendations.PLANHEAD,
        INV_AMT_ONETIME: $scope.recommendations.ONETIME === '' ? '0' : $scope.recommendations.ONETIME,
        INV_AMT_PER_MONTH: $scope.recommendations.PERMONTH === '' ? '0' : $scope.recommendations.PERMONTH,
        INV_AMT_TARGET: $scope.recommendations.TARGET === '' ? '0' : $scope.recommendations.TARGET,
        STEP_STAUS: '2'
      };

      //儲存FPS321
      $scope.fromFPS321 = $scope.recommendations.fromFPS321;
      if ($scope.fromFPS321) {
        inputVO.UNIVERSITY = $scope.fromFPS321.UNIVERSITY;
        inputVO.UNIVERSITY_FEE_EDU = $scope.fromFPS321.UNIVERSITY_FEE_EDU;
        inputVO.UNIVERSITY_FEE_LIFE = $scope.fromFPS321.UNIVERSITY_FEE_LIFE;
        inputVO.UNIVERSITY_YEAR = $scope.fromFPS321.UNIVERSITY_YEAR;
        inputVO.MASTER = $scope.fromFPS321.MASTER;
        inputVO.MASTER_FEE_EDU = $scope.fromFPS321.MASTER_FEE_EDU;
        inputVO.MASTER_FEE_LIFE = $scope.fromFPS321.MASTER_FEE_LIFE;
        inputVO.MASTER_YEAR = $scope.fromFPS321.MASTER_YEAR;
        inputVO.PHD = $scope.fromFPS321.PHD;
        inputVO.PHD_FEE_EDU = $scope.fromFPS321.PHD_FEE_EDU;
        inputVO.PHD_FEE_LIFE = $scope.fromFPS321.PHD_FEE_LIFE;
        inputVO.PHD_YEAR = $scope.fromFPS321.PHD_YEAR;
        inputVO.RETIREMENT_AGE = $scope.fromFPS321.RETIREMENT_AGE;
        inputVO.RETIRE_FEE = $scope.fromFPS321.RETIRE_FEE;
        inputVO.PREPARE_FEE = $scope.fromFPS321.PREPARE_FEE;
        inputVO.SOCIAL_INS_FEE_1 = $scope.fromFPS321.SOCIAL_INS_FEE_1;
        inputVO.SOCIAL_INS_FEE_2 = $scope.fromFPS321.SOCIAL_INS_FEE_2;
        inputVO.SOCIAL_WELFARE_FEE_1 = $scope.fromFPS321.SOCIAL_WELFARE_FEE_1;
        inputVO.SOCIAL_WELFARE_FEE_2 = $scope.fromFPS321.SOCIAL_WELFARE_FEE_2;
        inputVO.COMM_INS_FEE_1 = $scope.fromFPS321.COMM_INS_FEE_1;
        inputVO.COMM_INS_FEE_2 = $scope.fromFPS321.COMM_INS_FEE_2;
        inputVO.OTHER_FEE_1 = $scope.fromFPS321.OTHER_FEE_1;
        inputVO.OTHER_FEE_2 = $scope.fromFPS321.OTHER_FEE_2;
        inputVO.HERITAGE = $scope.fromFPS321.HERITAGE;
        inputVO.sppTypeDelete = $scope.fromFPS321.sppTypeDelete;
      }
      
      inputVO.prdList = [];
      $scope.paramList.forEach(function (row) {
        var temp = {
          PLAN_ID: row.PLAN_ID || null,
          SEQNO: row.SEQNO || null,
          PRD_ID: row.PRD_ID,
          PTYPE: row.PTYPE,
          RISK_TYPE: row.RISK_TYPE,
          CURRENCY_TYPE: row.CURRENCY_TYPE,
          TRUST_CURR: row.CmbCur === '2' ? 'TWD' : row.CURRENCY_TYPE,
          MARKET_CIS: row.CIS_3M,
          PURCHASE_ORG_AMT: row.CHG_PERCENT,
          PURCHASE_TWD_AMT: row.NTD_PERCENT,
          PORTFOLIO_RATIO: row.INV_PERCENT,
          LIMIT_ORG_AMT: row.LowMoney,
          TXN_TYPE: row.PTYPE === 'MFD' ? 'F1' : 'I1',
          INV_TYPE: row.InvType,
          STATUS: 'S',
          EX_RATE: row.CURRENCY_TYPE === 'TWD' ? 1 : curRate(row.CURRENCY_TYPE),
          PURCHASE_ORG_AMT_ORDER: 0,
          PURCHASE_TWD_AMT_ORDER: 0,
          ORDER_STATUS: null,
          PRD_SOURCE_FLAG: row.status,
          TARGETS: row.TARGETS,
          action: row.action || undefined
        };

        inputVO.prdList.push(temp);
      });

      $scope.sendRecv('FPS324', 'save', 'com.systex.jbranch.app.server.fps.fps324.FPS324InputVO', inputVO,
        function (tota, isError) {
          if (!isError) {
            fps300.setPlanID(tota[0].body);
            $scope.isCreate = false;
            if (type === 'Y') {
              $scope.isFps320 = false;
            }
            $scope.inquire();
            $scope.getStep();
            if (toFPS340) {
              fps300.chgTab('FPS340');
            }
          }
        });
    };

    $scope.toFPS340 = function () {
      $scope.save('Y', true);
    };

    //績效追蹤與投組調整，歷史規劃 按鈕顯示
    $scope.getStep = function () {
      var deferred = $q.defer();
      $scope.sendRecv('FPS300', 'getStep', 'com.systex.jbranch.app.server.fps.fps300.FPS300InputVO', {
          custId: fps300.inputVO.custID
        },
        function (tota, isError) {
          if (!isError) {
            fps300.step = tota[0].body.step;
            deferred.resolve('successful');
          }
        });
      return deferred.promise;
    };

    /**
     * 去那兒
     */
    $scope.go = function (where, data) {
      var type;
      switch (where) {
        case 'detail':
          type = data.PTYPE;
          if (type === 'MFD') {
            openDialog('./assets/txn/PRD110/PRD110_DETAIL.html', data, type);
          } else {
            openDialog('./assets/txn/PRD160/PRD160_DETAIL.html', data, type);
          }
          break;
        case 'stock':
          openDialog(pathMap[where], data, 'stock');
          break;
        case 'product':
          var productMap = angular.copy($scope.mapping.stockMap).map(function (row, index) {
            row.active = data.PTYPE === 'MFD' && index === 0;
            if (row.active) row.data = {
              prdID: data.PRD_ID,
            };
            return row;
          });
          openDialog(pathMap[where], productMap, 'product');
          break;
        case 'models':
          var total = $scope.recommendations.ONETIME && $scope.recommendations.PERMONTH ? 0 : ($scope.recommendations.ONETIME ? parseFloat($scope.recommendations.ONETIME) :parseFloat($scope.recommendations.PERMONTH));
          openDialog(pathMap.models, {
            riskType: riskType,
            stockAmt: fps300.floatFormat(total, 0),
            featureDescription: featureDescription,
            mapping: $scope.mapping,
            rateMap: rateMap,
            returnAll: true,
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
      var scope = $scope;
      var dialog = ngDialog.open({
        template: path,
        className: 'FPS200',
        controller: ['$scope', function ($scope) {
          $scope.data = data;
          $scope.trendPath = scope.mapping.trendPath;
          $scope.riskType = type === 'models' ? riskType : custRiskAttr;
          $scope.OBU = scope.custInfo.OBU_FLAG ? scope.custInfo.OBU_FLAG : 'N';
          $scope.isPro = scope.custInfo.CUST_PRO_FLAG ? scope.custInfo.CUST_PRO_FLAG : 'N';
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
              $scope.row.KEY_NO = (data.KEY_NO).toString().split(',')[0];
              break;
          }
        }]
      });

      dialog.closePromise.then(function (data) {
        if (typeof (data.value) === 'object') {
          if (data.value.length > 0) {
            popCbFn[type](data.value);
            fps300.setParamList($scope.paramList);
            calPercent();
            checkTrial();
          }
        }
      });
    };

    var popCbFn = {
      models: function (data) {
        $scope.modelsParamList = formatParamModels(data);
        $scope.paramList = angular.copy($scope.modelsParamList);
        drawDoughnut();
      },
      stock: function (data) {
        checkPrdID = true;
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
            $scope.paramList = $scope.paramList.concat($scope.mfdModelsParamList);
          }
          if ($scope.insModelsParamList.length > 0) {
            $scope.paramList = $scope.paramList.concat($scope.insModelsParamList);
          }
        }
      },
      product: function (data) {
        checkPrdID = true;
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
            $scope.paramList = $scope.paramList.concat($scope.mfdModelsParamList);
          }
          if ($scope.insModelsParamList.length > 0) {
            $scope.paramList = $scope.paramList.concat($scope.insModelsParamList);
          }
        }
      }
    };

    var formatParamModels = function (list) {
      list.forEach(function (row) {
        row.PLAN_ID = fps300.getPlanID();
        row.action = 'create';
        row.CmbCur = row.CURRENCY_TYPE === 'TWD' ? '2' : '1';
        if ($scope.recommendations.ONETIME && $scope.recommendations.PERMONTH) {
          row.isSelectDis = false;
        } else if ($scope.recommendations.ONETIME) {
          row.InvType = '1';
          row.isSelectDis = true;
        } else {
          row.InvType = '2';
          row.isSelectDis = true;
        }
        //若投資方式下拉選單為'定期定額'，則對應原幣最低申購金額 = TBPRD_FUND.SML_SUBS_MINI_AMT_FOR
        if (row.InvType === '2') {
          row.LowMoney = row.SML_SUBS_MINI_AMT_FOR;
        } else if (row.InvType === '1') {
          row.LowMoney = row.GEN_SUBS_MINI_AMT_FOR;
        } else {
          row.LowMoney = 0;
        }
        if (row.PTYPE === 'INS') {
          row.LowMoney = row.GEN_SUBS_MINI_AMT_FOR;
          row.targetIDs = row.targetIDs || row.TARGETS;
        }
        row.TxnType = row.PTYPE === 'MFD' ? 'F1' : 'I1';
        row.CHG_PERCENT = row.PURCHASE_ORG_AMT;
        row.NTD_PERCENT = row.PURCHASE_TWD_AMT;
      });
      return list;
    };

    var formatParam = function (list) {
      list.forEach(function (row) {
        row.PLAN_ID = fps300.getPlanID();
        row.action = 'create';
        row.CmbCur = row.CURRENCY_TYPE === 'TWD' ? '2' : '1';
        if ($scope.recommendations.ONETIME && $scope.recommendations.PERMONTH) {
          row.isSelectDis = false;
        } else if ($scope.recommendations.ONETIME) {
          row.InvType = '1';
          row.isSelectDis = true;
        } else {
          row.InvType = '2';
          row.isSelectDis = true;
        }
        //若投資方式下拉選單為'定期定額'，則對應原幣最低申購金額 = TBPRD_FUND.SML_SUBS_MINI_AMT_FOR
        if (row.InvType === '2') {
          row.LowMoney = row.SML_SUBS_MINI_AMT_FOR;
        } else if (row.InvType === '1') {
          row.LowMoney = row.GEN_SUBS_MINI_AMT_FOR;
        } else {
          row.LowMoney = 0;
        }
        row.INV_PERCENT = 0;
        row.TxnType = row.PTYPE === 'MFD' ? 'F1' : 'I1';

        if (row.PTYPE === 'INS') {
          row.CmbCur = '1';
          row.P_TYPE = '2';
          row.LowMoney = row.GEN_SUBS_MINI_AMT_FOR;
          row.targetIDs = row.targetIDs || row.TARGETS;
        } else {
          row.P_TYPE = '1';
        }
      });
      return list;
    };

    // 輸入規劃資料
    $scope.openFps320 = function () {
      if ($scope.checkFps320) {
        return;
      }
      $scope.checkFps320 = true;
      var scope = $scope;
      var openTemp = angular.copy(scope.recommendations);

      var dialog = ngDialog.open({
        template: 'assets/txn/FPS320/FPS320.html',
        className: 'FPS324',
        showClose: false,
        controller: ['$scope', function ($scope) {
          $scope.isCreate = true; //isCreate;
          $scope.recommendations = scope.recommendations;
          $scope.fps300 = fps300;
          $scope.isData = scope.isData;
          if (fps300.getPlanDate()) {
            $scope.planDate = fps300.getPlanDate();
            $scope.overDate = fps300.getOverDate();
          } else {
            $scope.planDate = $filter('date')(new Date(), 'yyyy/MM/dd');
            $scope.overDate = $filter('date')(new Date(), 'yyyy/MM/dd');
          }

          $scope.custRiskAttr = custRiskAttr;
          $scope.custRiskAttrNumber = custRiskAttrNumber;
          //用來判斷是否鎖住投組名稱
          if (fps300.getDisBeacon()) {
            $scope.beacon = 1;
          }

        }]
      });

      dialog.closePromise.then(function (data) {
        $scope.checkFps320 = false;
        $scope.isFps320 = true;
        if (data.value !== 'cancel' && data.value) {
          $scope.recommendations = data.value;
          fps300.setRecommendations($scope.recommendations);

          if ((openTemp.ONETIME || 0) === ($scope.recommendations.ONETIME || 0) &&
            (openTemp.PERMONTH || 0) === ($scope.recommendations.PERMONTH || 0) &&
            openTemp.CUST_RISK_ATTR_NAME.substring(3, 4) === $scope.recommendations.CUST_RISK_ATTR &&
            (openTemp.PLANHEAD || 0) === ($scope.recommendations.PLANHEAD || 0) &&
            (openTemp.TARGET || 0) === ($scope.recommendations.TARGET || 0)) {
            return;
          }

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
          riskType = $scope.recommendations.CUST_RISK_ATTR_NAME;
          INV_AMT_TYPE = $scope.recommendations.ONETIME ? '1' : '2';
          if (data.value.isChange === 'Y') {
            $scope.paramList = [];
            isChange = data.value.isChange;
            // $scope.inquire().then(function (data) {
            $scope.save('N');
            // });
          } else if (fps300.planID) {
            // 避免金額被用比例重新計算 setPercent()
            $scope.isFps320 = false;
            $scope.save('N');
          } else {
            $scope.paramList = [];
            // $scope.inquire().then(function (data) {
            $scope.save('N');
            // });
          }
          fps300.setDisBeacon(true);
        }
      });
    };

    var calPercent = function () {
      var length = $scope.paramList.length;
      var count = 0;
      totalNTD = 0;
      $scope.paramList.forEach(function (row, index) {
        totalNTD += parseInt(row.NTD_PERCENT);
      });
      $scope.paramList.forEach(function (row, index) {
        if (index + 1 !== length) {
          row.INV_PERCENT = fps300.floatFormat((row.NTD_PERCENT / totalNTD * 100), 0) || 0;
          count += row.INV_PERCENT;
        } else {
        	if (count == 0){
        		row.INV_PERCENT = 0;
        	} else {
        		row.INV_PERCENT = fps300.floatFormat((100 - (count > 100 ? 100 : count)), 0) || 0;
        	}
        }
      });
      drawDoughnut();
    };

    //投資組合建議
    var drawDoughnut = function () {
      var temp = {};
      var temp2 = {};
      var index1 = 0;
      var index2 = 0;
      
//      console.log($scope.paramList);
      $scope.paramList.forEach(function (row) {
        //投資型保險預設就是平衡型部位
//        if (!row.FUND_TYPE_NAME) {
//          row.FUND_TYPE_NAME = '平衡型';
//        }
//    	  alert(row.STOCK_BOND_TYPE);
    	row.FUND_TYPE_NAME = row.STOCK_BOND_TYPE === 'B' ? '債券型' : '股票型';
    	row.INV_PERCENT = Math.round(row.INV_PERCENT);

    	//MF_MKT_CAT > 30 目前沒有NAME
        if (!row.NAME) {
          row.NAME = '其他';
        }
    	
        //股價配置
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
        //類別配置
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
      //股票/債券及平衡排序固定
      dataPoints = dataPoints.sort(function (a, b) {
        return sortOrder.fundName[a.legendText] > sortOrder.fundName[b.legendText];
      });

      //依佔比高到低排序
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

      charts.stockChart = null;
      charts.stockChart = new CanvasJS.Chart('stockChart', chartData);
      charts.stockChart.render();
      setChartTable('stockChart');

      charts.classChart = null;
      charts.classChart = new CanvasJS.Chart('classChart', chartData_2);
      charts.classChart.render();
      setChartTable('classChart');
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

    //刪除row
    $scope.deleteRow = function (index) {
      $confirm({
        title: '警告',
        text: '您確定要刪除此筆資料?'
      }, {
        size: 'sm'
      }).then(function () {
        $scope.paramList.splice(index, 1);
        fps300.setParamList($scope.paramList);
        calPercent();
        checkTrial();
      }, function () {});
    };

    //投組試算檢查
    var calAll = function () {
      $scope.calInvestmentText = '';
      $scope.calModelPorfolioText = '';
      var delCount = 0;
      for (var i = 0; i < $scope.paramList.length; i++) {
        var row = $scope.paramList[i];
        if (!row.CHG_PERCENT) {
          return '請於紅框處輸入金額';
        }
        // row.PTYPE=='INS' 保險算入單筆投資
        if (row.PTYPE !== 'INS') {
          if (!row.InvType) {
            return '請選擇投資方式';
          }
        }
        delCount++;

      }
      if (delCount < 1) {
        return '執行績效模擬投資標的需大於一檔';
      }
      return '';
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
      totalNTD = $scope.paramList.reduce(function (a, b) {
        return a + b.NTD_PERCENT;
      }, 0) || 0;
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
        else if (row.InvType === '1')
          oneTime += parseInt(row.NTD_PERCENT, 10);
        else if (row.InvType === '2')
          perMonth += parseInt(row.NTD_PERCENT, 10);
      });

      $scope.showFPS323Error = false;
      $scope.calInvestmentText = '';
      $scope.calModelPorfolioText = '';
      $scope.showFPS330Error = false;
      // FPS323 績效模擬
      $scope.$broadcast('FPS323_drawMonteCarlo', {
        custID: fps300.inputVO.custID,
        planID: fps300.getPlanID() || null,
        ret: formatedData,
        beginAmount: parseInt(oneTime, 10),
        eachYear: parseInt(perMonth, 10) * 12,
        year: parseInt($scope.recommendations.PLANHEAD, 10),
        target: parseInt($scope.recommendations.TARGET, 10)
      });
      // FPS330 歷史績效表現
      $scope.$broadcast('FPS330_drawHistoryChart', formatedData);
    };

    $scope.$on('FPS323_error', function (event, data) {
      $scope.showFPS323Error = true;
      $scope.calInvestmentText = data;
    });
    $scope.$on('FPS330_error', function (event, data) {
      $scope.showFPS330Error = true;
      $scope.calModelPorfolioText = data;
    });
    /**
     * FPS325 效率前緣
     * @param {*} data
     */
    $scope.openFps325 = function (data) {
      var scope = $scope;
      var tempList = $scope.paramList;
      var listMap = tempList.reduce(function (a, b) {
        a[b.PRD_ID] = b.PRD_ID + ' ' + b.PRD_CNAME;
        if (b.TARGETS) {
          (b.TARGETS.split('/') || []).forEach(function (t) {
            a[t] = b.PRD_ID + ' ' + b.PRD_CNAME + ': ' + t;
          });
        }
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
          }]
        });

        dialog.closePromise.then(function (data) {
          if (data.value.status === 'success') {
            for (var i = 0; i < data.value.addList.length; i++) {
              for (var j = 0; j < $scope.paramList.length; j++) {
                if (data.value.addList[i].PRD_ID === $scope.paramList[j].PRD_ID) {
                  $scope.paramList[j].CHG_PERCENT = data.value.addList[i].PURCHASE_ORG_AMT;
                  $scope.paramList[j].NTD_PERCENT = data.value.addList[i].PURCHASE_TWD_AMT;
                  $scope.paramList[j].INV_PERCENT = data.value.addList[i].INV_PERCENT;
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
    // FPS325 end

    /* helping function */
    var reformatList = function (row) {
      row.PLAN_ID = row.PLAN_ID;
      row.SEQNO = row.SEQNO;
      row.PRD_ID = row.PRD_ID;
      row.PTYPE = row.PTYPE;

      row.RISK_TYPE = row.RISK_TYPE;
      row.CURRENCY_TYPE = row.CURRENCY_TYPE;
      row.CIS_3M = row.CIS_3M;
      row.INV_PERCENT = row.PORTFOLIO_RATIO;
      // update 年期 isFPs320 = true
      if (!$scope.isFps320 && isChange === 'N') {
        //歷史規劃來的
        row.CmbCur = row.TRUST_CURR === 'TWD' ? '2' : '1';
        row.InvType = row.INV_TYPE;
        row.CHG_PERCENT = row.PURCHASE_ORG_AMT;
        row.NTD_PERCENT = row.PURCHASE_TWD_AMT;
      } else {
        //set 原幣金額與約當台幣
        setPERCENT(row);
      }
      //set 投資方式與最低申購金額
      setInvType(row);

      row.TxnType = row.TXN_TYPE;
      row.EX_RATE = row.EX_RATE;
      row.status = row.PRD_SOURCE_FLAG || '4'; // 1 新增商品  2 理財標的建議  3加入庫存 4Return
      if (row.PTYPE === 'INS') {
        row.CmbCur = '1';
        row.targetIDs = row.targetIDs || row.TARGETS;
      }
    };

    var reformatSug = function (row) {
      row.action = 'create';
      row.SUGG_PERCENT = row.INV_PERCENT || null;
      if (row.PTYPE === 'INS') {
        row.targetIDs = row.targetIDs || row.TARGETS;
      }

      //set 原幣金額與約當台幣
      setPERCENT(row);
      //set 投資方式與最低申購金額
      setInvType(row);
    };

    var setPERCENT = function (row) {
      if ($scope.recommendations.ONETIME && $scope.recommendations.PERMONTH) {
        //調整後約當台幣
        row.NTD_PERCENT = $scope.isCreate ? 0 :fps300.floatFormat(((parseFloat($scope.recommendations.ONETIME) + parseFloat($scope.recommendations.PERMONTH)) * row.INV_PERCENT / 100), 0) || 0;
        //庫存約當台幣
        // row.STORE_NTD = fps300.floatFormat(((parseFloat($scope.recommendations.ONETIME) + parseFloat($scope.recommendations.PERMONTH)) * row.INV_PERCENT / 100), 0) || 0;
      } else if ($scope.recommendations.ONETIME) {
        //調整後約當台幣
        row.NTD_PERCENT = fps300.floatFormat(($scope.recommendations.ONETIME * row.INV_PERCENT / 100), 0) || 0;
        //庫存約當台幣
        // row.STORE_NTD = fps300.floatFormat(($scope.recommendations.ONETIME * row.INV_PERCENT / 100), 0) || 0;
      } else {
        //調整後約當台幣
        row.NTD_PERCENT = fps300.floatFormat(($scope.recommendations.PERMONTH * row.INV_PERCENT / 100), 0) || 0;
        //庫存約當台幣
        // row.STORE_NTD = fps300.floatFormat(($scope.recommendations.PERMONTH * row.INV_PERCENT / 100), 0) || 0;
      }

      //原幣庫存金額      row.STORE_RAW
      //調整後原幣金額
      if (row.CURRENCY_TYPE === 'TWD') {
        row.CHG_PERCENT = row.NTD_PERCENT || 0;
        // row.STORE_RAW = row.STORE_NTD || 0;
        row.CmbCur = '2';
      } else {
        row.CHG_PERCENT = fps300.floatFormat((parseFloat(row.NTD_PERCENT) / curRate(row.CURRENCY_TYPE)), 0) || 0;
        // row.STORE_RAW = fps300.floatFormat((parseFloat(row.STORE_NTD) / curRate(row.CURRENCY_TYPE)), 0) || 0;
        row.CmbCur = '1';
      }
    };

    var setInvType = function (row) {
      //投資方式
      if ($scope.recommendations.ONETIME && $scope.recommendations.PERMONTH) {
    	  //如果有期初投資金額與每月投資金額，則可以選擇投資方式
    	  row.isSelectDis = false;
    	  if (row.NTD_PERCENT == 0) {
    		  //若約當台幣為0，比例就為0
    		  row.INV_PERCENT = 0;
    	  }
      } else if ($scope.recommendations.ONETIME) {
        row.InvType = '1';
        row.isSelectDis = true;
      } else {
        row.InvType = '2';
        row.isSelectDis = true;
      }
      //若投資方式下拉選單為'定期定額'，則對應原幣最低申購金額 = TBPRD_FUND.SML_SUBS_MINI_AMT_FOR
      if (row.InvType === '2') {
        row.LowMoney = row.SML_SUBS_MINI_AMT_FOR;
      } else if (row.InvType === '1') {
        row.LowMoney = row.GEN_SUBS_MINI_AMT_FOR;
      } else {
        row.LowMoney = 0;
      }
      if (row.PTYPE === 'INS') {
        row.LowMoney = row.GEN_SUBS_MINI_AMT_FOR;
      }
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

    $scope.checkMoney = function (big, small) {
      if (big && parseInt(big) > 0) {
        if (parseInt(big) <= parseInt(small)) {
          return true;
        }
        return false;
      } else {
        return true;
      }
    };

    /* main process */
    param().then(function () {
      $scope.init();
      if ($scope.currentProgress === 'ReturnFPS324') {
        drawDoughnut();
        checkTrial();
      }
    }, function (err) {
      console.log(err);
    });
  }
);
