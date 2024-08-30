/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS230Controller',
  function ($scope, $controller, ngDialog, $timeout, $confirm, $q, fps200Service, $filter, FPSUtilsService) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPS230Controller';

    /* parameter */
    $scope.kycInvalid = $scope.connector('get', 'kycInvalid') !== false ? true : false;
    var fps200 = fps200Service;
    var pathMap = {
      bestRatio: './assets/txn/FPS325/FPS325.html',
      review: './assets/txn/FPS230/FPS230Review.html',
      models: './assets/txn/FPS230/FPS230Models.html',
      detail: {
        BND: './assets/txn/PRD130/PRD130_DETAIL.html',
        MFD: './assets/txn/PRD110/PRD110_DETAIL.html',
        SN: './assets/txn/PRD140/PRD140_DETAIL.html',
        SI: './assets/txn/PRD150/PRD150_DETAIL.html',
        INS: './assets/txn/PRD160/PRD160_DETAIL.html'
      },
      product: './assets/txn/FPS/FPSProd.html',
      next: 'STEP4',
      prev: 'STEP1'
    };
    var sortOrder = {
//      deposit: {
//        1: 2,
//        3: 1,
//        2: 3
//      },
      fixed: {
        SN: 2,
        SI: 3,
        BND: 1
      },
      stock: {
        MFD: 1,
        ETF: 2,
        INS: 3
      },
      isModel: {
        true: 1,
        false: 4
      },
      isStock: {
        true: 1,
        false: 4
      }
    };
    
    var charts = {
      beforeChart: null,
      afterChart: null
    };

    $scope.chartTables = {
      beforeChart: [],
      afterChart: []
    };

    var mergeKey = {
//      deposit: ['PRD_ID', 'CURRENCY_TYPE', 'CERT_NBR'],
      fixed: ['PRD_ID', 'CURRENCY_TYPE', 'CERT_NBR'],
      stock: ['PRD_ID', 'CURRENCY_TYPE', 'buyCur', 'TARGETS', 'CERT_NBR']
    };
    
    $scope.recommend = {
      fixed: 0,
      stock: 0
    };

    var errorMap = {
      redAlert: '紅框欄位為必要填寫欄位，請填寫。',
      limitAlertHasIns: '調整後原幣金額小於原幣申購門檻/累進金額，請重新填寫。',
      limitAlert: '投入金額小於申購門檻/累進金額，請重新填寫。',
      noPartBack: 'SI及海外債不可部分贖回，請重新填寫。',
      snLimitAlert: '輸入的金額低於贖回門檻，建議您可以調整為{0}，請重新填寫。',
      // noMoreBuy: '商品已經過了募集期間，不可再申購。',
      noMoreBuy: '庫存商品僅限(部分)贖回/賣出；申購請點選「新增商品」',
      notMultiNum: '申購金額需為累進金額之倍數',
      snBackAlert: 'SN贖回後庫存金額不足贖回門檻，請全部贖回。',
      snPrdUnitBackAlert: '輸入的金額低於贖回門檻或累進金額，建議您可以調整為{0}，請重新調整。',
      backAlert:'不符合 申購 之累進金額，請重新調整。',
      nanoRedeemAlert:'奈米投商品不可以部分贖回'
    };

    // certificate title
    var certificateTitle = {
      BND: '憑證編號',
      MFD: '憑證編號',
      ETF: null,
      SN: '憑證編號',
      SI: '帳號',
      INS: '保單號碼',
      DOP: null,
      NANO: '憑證編號'
    };

    // model bias
    $scope.modelBias = {};
    
    $scope.mapping0 = {
    	keys: [
    	  'fixed',
    	   'stock',
    	],
	    keysZhTw: [
	       '類債券商品',
	       '類股票商品',
	    ]	
    }

    $scope.mapping = {
      BNDTxnType : [
        {DATA:1,LABEL:'申購'},
  	    {DATA:2,LABEL:'贖回'},
  	    {DATA:3,LABEL:'贖回'}
  	  ],
  	  SITxnType : [
  	    {DATA:1,LABEL:'申購'},
  	    {DATA:2,LABEL:'贖回'},
  	    {DATA:3,LABEL:'贖回'}
  	  ],
  	  SNTxnType : [
  	    {DATA:1,LABEL:'申購'},
  	    {DATA:2,LABEL:'贖回'},
  	    {DATA:3,LABEL:'部分贖回'}
  	  ],
  	  NANOTxnType : [
  	    {DATA:1,LABEL:'申購'},
  	    {DATA:2,LABEL:'贖回'}
  	  ],
  	  NANOInvType : [
  	    {DATA:1,LABEL:'單筆'},
  	    {DATA:2,LABEL:'定額'},
  	    {DATA:3,LABEL:'單筆+定額'}
  	  ],
      astCode: {
//        deposit: ['01', '02', '04', '05', '14'],
        fixed: ['10', '15'],
        stock: ['07', '08', '09', '14']
      },

      trendPath: {
        H: 'assets/images/ic-unbiased.svg',
        B: 'assets/images/ic-rise.svg',
        S: 'assets/images/ic-decline.svg'
      },

      keys: [
//        'deposit',
        'fixed',
        'stock'
      ],
      
      stockBondType: [
        'B',
        'S'              
      ],

      keysZhTw: [
//        '儲蓄型保險+存款',
        '類債券商品',
        '類股票商品'
      ],

      code: {
//        'deposit': '1',
        'fixed': '2',
        'stock': '3',
//        '1': 'deposit',
        '2': 'fixed',
        '3': 'stock'
      },

      index: {
//        'deposit': 0,
        'fixed': 1,
        'stock': 2
      },

      // tab1 deposit pop out config
//      depositMap: [{
//        title: '儲蓄型保險',
//        path: 'assets/txn/FPS/FPSProd_SavingIns.html',
//        order: 0,
//        active: true,
//      }, {
//        title: '存款',
//        path: 'assets/txn/FPS/FPSProd_Currency.html',
//        order: 1,
//        active: false
//      }],

      fixedMap: [{
	    title: '基金',
	    path: 'assets/txn/FPS/FPSProd_MFD.html',
	    order: 0,
	    active: true
	  }, {
	    title: 'ETF',
	    path: 'assets/txn/FPS/FPSProd_ETF.html',
	    order: 1,
	    active: false
	  }, {
        title: '海外債',
        path: 'assets/txn/FPS/FPSProd_Bond.html',
        order: 2,
        active: false
      }, {
        title: 'SN',
        path: 'assets/txn/FPS/FPSProd_SN.html',
        order: 3,
        active: false
      }, {
        title: 'SI',
        path: 'assets/txn/FPS/FPSProd_SI.html',
        order: 4,
        active: false
      }, {
          title: '奈米投',
          path: 'assets/txn/FPS/FPSProd_NANO.html',
          order: 5,
          active: false
      }],

      stockMap: [{
        title: '基金',
        path: 'assets/txn/FPS/FPSProd_MFD.html',
        order: 0,
        active: true
      }, {
        title: 'ETF',
        path: 'assets/txn/FPS/FPSProd_ETF.html',
        order: 1,
        active: false
      }, {
          title: '奈米投',
          path: 'assets/txn/FPS/FPSProd_NANO.html',
          order: 2,
          active: false
      }
//      , {
//        title: '投資型保險',
//        path: 'assets/txn/FPS/FPSProd_InvestIns.html',
//        order: 2,
//        active: false
//      }
      ],
    };

    $scope.dragOptions = {
      label: {
        front: '0%',
        end: '100%',
        unit: '%',
      },
      showTip: false,
      showLabel: true,
      showBtn: false,
    };

    var listMap = function (type) {
      return $scope[type + 'List'] || [];
    };
    var rateMap = {};
    var modelPortfolioList = [];
    var featureDescription = [];
    var blockHasStock = {};
        
    // getXML
    var param = function () {
      var deferred = $q.defer();
      FPSUtilsService.getXmlParam([
        'FPS.CUST_RISK_ATR_2',
        'FPS.BUY_CURRENCY',
        'FPS.TXN_TYPE',
        'FPS.INV_TYPE',
        'FPS.PROD_TYPE',
        'FPS.INV_PRD_TYPE',
        'FPS.INV_PRD_TYPE_2',
        'SOT.NF_MIN_BUY_AMT_1',
        'SOT.NF_MIN_BUY_AMT_2',
      ]).then(function (mapping) {
        $scope.mapping = Object.assign($scope.mapping, mapping);
        $scope.mapping.ETFTxnType = $scope.mapping.txnType.filter(function (row) {
          return row.DATA.charAt(0) === 'E';
        });
        $scope.mapping.INSTxnType = $scope.mapping.txnType.filter(function (row) {
          return row.DATA.charAt(0) === 'I';
        });
        $scope.mapping.MFDTxnType = $scope.mapping.txnType.filter(function (row) {
          return row.DATA.charAt(0) === 'F';
        });
        
        $scope.mapping.TxnType = $scope.mapping.txnType.filter(function (row) {
          return row.DATA.charAt(0) === '1';
        });
        $scope.mapping.fixedTxnType = $scope.mapping.txnType.filter(function (row) {
          return !!Number(row.DATA.charAt(0));
        });

        var tmp;
        tmp = $scope.mapping.invType1Limit.filter(function (row) {
          return row.DATA === 'TWD';
        }) || [];
        $scope.invType1LimitTWD = tmp.length > 0 && tmp[0].LABEL;
        tmp = $scope.mapping.invType2Limit.filter(function (row) {
          return row.DATA === 'TWD';
        }) || [];
        $scope.invType2LimitTWD = tmp.length > 0 && tmp[0].LABEL;

        deferred.resolve('success');
      }, function (err) {
        deferred.reject(false);
      });
      return deferred.promise;
    };

    /**
     * STEP1VO
     * CUST_RISK: custInfo.KYC_LEVEL,
     * PLAN_ID: $scope.inputVO.planID,
     * PLAN_AMT: $scope.inputVO.planAmt,
     * CUST_ID: $scope.inputVO.custID,
     */
    $scope.STEP1VO = $scope.connector('get', 'STEP1VO');
    /**
     * STEP2VO
     * MODEL_FLAG: $scope.inputVO.modelFlag,
     * PORTFOLIO1_AMT_AFTER: $scope.inputVO.depositAmt,
     * PORTFOLIO2_AMT_AFTER: $scope.inputVO.fixedAmt,
     * PORTFOLIO3_AMT_AFTER: $scope.inputVO.stockAmt,
     * PORTFOLIO1_RATIO_AFTER: $scope.inputVO.depositPct,
     * PORTFOLIO2_RATIO_AFTER: $scope.inputVO.otherPct,
     * PORTFOLIO3_RATIO_AFTER: $scope.inputVO.invPct
     */
//    // make step2 pct parseInt
    
    $scope.STEP2VO = $scope.connector('get', 'STEP2VO');
    $scope.STEP2VO.PORTFOLIO2_AMT_AFTER = parseInt($scope.STEP2VO.PORTFOLIO2_AMT_AFTER, 10) || 0;
    $scope.STEP2VO.PORTFOLIO3_AMT_AFTER = parseInt($scope.STEP2VO.PORTFOLIO3_AMT_AFTER, 10) || 0;
    $scope.STEP2VO.PORTFOLIO2_RATIO_AFTER_ORIGIN = Number($scope.STEP2VO.PORTFOLIO2_RATIO_AFTER_ORIGIN) || 0;
    $scope.STEP2VO.PORTFOLIO3_RATIO_AFTER_ORIGIN = Number($scope.STEP2VO.PORTFOLIO3_RATIO_AFTER_ORIGIN) || 0;
    $scope.STEP2VO.PORTFOLIO2_AMT_AFTER_ORIGIN = parseInt($scope.STEP2VO.PORTFOLIO2_AMT_AFTER_ORIGIN, 10) || 0;
    $scope.STEP2VO.PORTFOLIO3_AMT_AFTER_ORIGIN = parseInt($scope.STEP2VO.PORTFOLIO3_AMT_AFTER_ORIGIN, 10) || 0;
    $scope.STEP2VO.PORTFOLIO2_RATIO_AFTER = Number($scope.STEP2VO.PORTFOLIO2_RATIO_AFTER) || 0;
    $scope.STEP2VO.PORTFOLIO3_RATIO_AFTER = Number($scope.STEP2VO.PORTFOLIO3_RATIO_AFTER) || 0;

    $scope.custInfo = $scope.connector('get', 'custInfo');
    var planID = $scope.connector('get', 'planID') || null;
    var custID = $scope.connector('get', 'custID') || null;
    var riskType = $scope.STEP1VO.CUST_RISK;
    var isFromStep1 = $scope.fps200.prevStep === 'STEP1';
    var limitVolatility = 0;
    if (!isFromStep1) {
      $scope.connector('set', 'FPS230_keep_old_amt', undefined);
    }

    /**
     * prod item
     * PLAN_ID
     * SEQNO
     * PRD_ID
     * PRD_CNAME
     * CURRENCY_TYPE
     * PTYPE
     * INV_PERCENT
     * INV_PRD_TYPE
     * INV_PRD_TYPE_2
     * RISK_TYPE
     * PURCHASE_ORG_AMT
     * PURCHASE_TWD_AMT
     * NOW_AMT
     * NOW_AMT_TWD
     * LIMIT_ORG_AMT
     * CIS_3M
     * FUND_TYPE_NAME
     * PORTFOLIO_TYPE
     * TRUST_CURR
     * buyCur
     * hasError
     * errorCode
     */

    /* main function */
    var inquire = function () {
      var defer = $q.defer();
      $scope.sendRecv('FPS230', 'inquire', 'com.systex.jbranch.app.server.fps.fps230.FPS230InputVO', {
          planID: planID,
          custID: custID,
          riskType: riskType,
          OBU: $scope.custInfo.OBU_FLAG,
          isPro: $scope.custInfo.CUST_PRO_FLAG,
        },
        function (tota, isError) {
          if (!isError) {
        	debugger;
        	$scope.paramList = [];
        	$scope.paramList = tota[0].body.outputList;
            $scope.fxRateList = tota[0].body.fxRateList;
//            console.log("405 $scope.paramList");
//            console.log($scope.paramList);
            
            // 市場概況
            $scope.marketOverview = tota[0].body.market;
            
            $scope.stockRiskLevel = tota[0].body.stockRiskLevel;
            // 建議配置
            $scope.suggestPct = tota[0].body.suggestPct;
            modelPortfolioList = mpClearNull(tota[0].body.initModelPortfolioList);
            calModelBias(tota[0].body.initModelPortfolioList);
            
            featureDescription = tota[0].body.featureDescription;
            $scope.hasIns = tota[0].body.hasInvest;
            limitVolatility = tota[0].body.volatility;
            
            if($scope.hasIns) {
            	$scope.orignFixedPct = 100 - $scope.stockRiskLevel[$scope.custInfo.KYC_LEVEL];
            	$scope.orignStockPct = $scope.stockRiskLevel[$scope.custInfo.KYC_LEVEL];
            	$scope.orignFixedAmt = Math.round($scope.STEP1VO.PLAN_AMT * $scope.orignFixedPct/100);
            	$scope.orignStockAmt = Math.round($scope.STEP1VO.PLAN_AMT * $scope.orignStockPct/100);
            } else {
            	$scope.orignFixedPct = $scope.STEP2VO.PORTFOLIO2_RATIO_AFTER_ORIGIN;
            	$scope.orignStockPct = $scope.STEP2VO.PORTFOLIO3_RATIO_AFTER_ORIGIN;
            	$scope.orignFixedAmt = $scope.STEP2VO.PORTFOLIO2_AMT_AFTER_ORIGIN;
            	$scope.orignStockAmt = $scope.STEP2VO.PORTFOLIO3_AMT_AFTER_ORIGIN;
            }
            
            // error codes
            tota[0].body.errorList.forEach(function (e) {
              $scope.showErrorMsg(e);
            });

            // 有歷史資料
            var isHis = tota[0].body.isHis;
            $scope.isHisPlan = tota[0].body.isHis;
            
            // 匯率表
            if ($scope.fxRateList.length > 0) {
              $scope.fxRateList.forEach(function (row) {
                rateMap[row.CUR_COD] = {
                  buy: row.BUY_RATE,
                  sell: row.SEL_RATE
                };
              });
              rateMap.TWD = {
                buy: 1,
                sell: 1
              };
            }
//            console.log("453 $scope.paramList");
//            console.log($scope.paramList);
            // 初始 inv table 的庫存金額
            $scope.paramList.forEach(function (row) {
              row.NOW_AMT = 0;
              row.NOW_AMT_TWD = 0;
            });
            
            // 是首推
            if (!$scope.hasIns && !isHis) { // 是推薦(model)
              $scope.paramList = mpClearNull($scope.paramList);
              calAmtFromPct($scope.paramList);
            } 

            var FPS230_keep_old_amt = $scope.connector('get', 'FPS230_keep_old_amt');
//            debugger;
            // 是從step2 計算更改的bias
            if (!$scope.hasIns && isHis && isFromStep1 && FPS230_keep_old_amt) {
//              debugger;
              var orginBias = {};
              $scope.mapping.keys.forEach(function (key) {
                orginBias[key] = $scope.STEP2VO['PORTFOLIO' + $scope.mapping.code[key] + '_AMT_AFTER'] / FPS230_keep_old_amt[key] || 1;
              });
              $scope.paramList.forEach(function (row) {
                if (orginBias[$scope.mapping.code[row.INV_PRD_TYPE]] === 1) return false;
//                row.PURCHASE_TWD_AMT = Math.floor(row.PURCHASE_TWD_AMT * orginBias[$scope.mapping.code[row.INV_PRD_TYPE]]);
//                chgTWD(row);
                  row.INV_PERCENT = Math.round((row.PURCHASE_TWD_AMT/$scope.STEP2VO['PORTFOLIO' + row.INV_PRD_TYPE + '_AMT_AFTER'])*10000000000)/10000000000;
              });
              $scope.paramList = validLimitList($scope.paramList);
            }
//            debugger;
            if ($scope.hasIns) {
              // 庫存
              $scope.custStockList = tota[0].body.hisStocksList || [];
              var certNbrLs = $scope.paramList.filter(function (row) {
                return !!row.CERT_NBR || (row.INV_PRD_TYPE_2 === '1' || row.INV_PRD_TYPE_2 === '2');
              }).map(function (row) {
                return row.CERT_NBR || row.TRUST_CURR;
              });
//              debugger;
//              console.log("491 $scope.custStockList");
//              console.log($scope.custStockList);
              
              var saving = {};
              var savingMerge = function (row, hasSaved) {
                if (!saving[row.TRUST_CURR]) {
                  row.isStock = true;
                  row.PLAN_ID = planID;
                  row.PRD_NAME = row.PROD_NAME;
                  row.PURCHASE_ORG_AMT = hasSaved ? 0 : row.NOW_AMT;
                  row.PURCHASE_TWD_AMT = hasSaved ? 0 : row.NOW_AMT_TWD;
                  row.NOW_AMT = Math.round(row.NOW_AMT);
                  row.NOW_AMT_TWD = Math.round(row.NOW_AMT_TWD);
                  row.PORTFOLIO_RATIO = 0;
                  saving[row.TRUST_CURR] = row;
                  saving[row.TRUST_CURR].CERT_NBR = undefined;
                } else {
                  saving[row.TRUST_CURR].PURCHASE_ORG_AMT += hasSaved ? 0 : row.NOW_AMT;
                  saving[row.TRUST_CURR].PURCHASE_TWD_AMT += hasSaved ? 0 : row.NOW_AMT_TWD;
                  saving[row.TRUST_CURR].NOW_AMT += Math.round(row.NOW_AMT);
                  saving[row.TRUST_CURR].NOW_AMT_TWD += Math.round(row.NOW_AMT_TWD);
                }
              };
             
              var snMap = {};
              
              // if saving ins, use stock value
              $scope.custStockList.forEach(function (row) {
                // 有無存在資料庫
                var hasSaved = (row.INV_PRD_TYPE_2 === '1' || row.INV_PRD_TYPE_2 === '2') ?
                  certNbrLs.indexOf(row.TRUST_CURR) >= 0 :
                  certNbrLs.indexOf(row.CERT_NBR) >= 0;
                // 紀錄block有無庫存
                blockHasStock[$scope.mapping.code[row.INV_PRD_TYPE]] = true;
                if (row.INV_PRD_TYPE_2 === '1' || row.INV_PRD_TYPE_2 === '2') {
                  savingMerge(row, hasSaved);
                  return true;
                }

                // NOW_AMT_TWD || INV_AMT_TWD
                row.isStock = true;
                row.PLAN_ID = planID;
                row.PRD_NAME = row.PROD_NAME;
                row.PURCHASE_ORG_AMT = hasSaved && row.INV_PRD_TYPE_2 !== '3' ? 0 : (row.TRUST_CURR === 'TWD' ? row.NOW_AMT_TWD : row.NOW_AMT);
                row.PURCHASE_TWD_AMT = hasSaved && row.INV_PRD_TYPE_2 !== '3' ? 0 : row.NOW_AMT_TWD;
                row.NOW_AMT = Math.round(row.NOW_AMT);
                row.NOW_AMT_TWD = Math.round(row.NOW_AMT_TWD);
                row.PORTFOLIO_RATIO = 0;

                row.tooltipInfo = certificateTitle[row.PTYPE] !== undefined ? (
                    (certificateTitle[row.PTYPE] ? (certificateTitle[row.PTYPE] + ': ' + row.CERT_NBR + ' / ') : '') +
                    ((row.TRUST_CURR && row.TRUST_CURR === 'TWD') ? ('台幣投入金額: ' + FPSUtilsService.numberWithCommas(row.INV_AMT_TWD)) : ('投入金額: ' + FPSUtilsService.numberWithCommas(row.INV_AMT))) + ' / ' +
                    ((row.TRUST_CURR && row.TRUST_CURR === 'TWD') ? ('台幣含息報酬率: ' + row.RTN_RATE_WD_TWD) : ('含息報酬率: ' + row.RTN_RATE_WD)) + '% / ' +
                    '申請日期: ' + new Date(row.DATA_DATE).yyyyMMdd('/')) :
                  '';
                    
              	  
          	    if(row.PTYPE === 'SN') {
          		  var value = (row.TRUST_CURR && row.TRUST_CURR === 'TWD') ? row.INV_AMT_TWD : row.INV_AMT;
          		  row.SML_SUBS_MINI_AMT_FOR = Math.ceil((row.SML_SUBS_MINI_AMT_FOR * row.NOW_AMT / value)/100)*100;
//          		  row.SML_UNIT_SUBS_MINI_AMT_FOR = Math.ceil((row.PRD_UNIT * row.NOW_AMT / value)/100)*100;
          		  row.PRD_UNIT = Math.ceil((row.PRD_UNIT * row.NOW_AMT / value)/100)*100;
          	    }    
                $scope.paramList.push(row);
              });
             
              if (Object.keys(saving).length > 0) {
                // todo: sum all foreign currency
                Object.keys(saving).forEach(function (currency) {
                  $scope.paramList.push(saving[currency]);
                });
              }
            }
             
            // filter format merge sort
            // if saving ins, use stock value
            // Jacky 註解 : 這段程式是用來將custStockList(客戶庫存)拆分出類債券及類股票List
            $scope.mapping.keys.forEach(function (key) {
              // format list
//              debugger;
              var keyList = filterList($scope.paramList, 'INV_PRD_TYPE', $scope.mapping.code[key]);
              if ((!$scope.paramList || $scope.paramList.length === 0) && ((isFromStep1 && !$scope.hasIns)) && $scope['orign' + key + 'Amt'] > 0) {
                $scope[key + 'List'] =
                  sortListOf(key,
                    formatList(key,
                      calAmtFromPct(
                        filterList(modelPortfolioList, 'INV_PRD_TYPE', $scope.mapping.code[key])), true));
              } else {
            	$scope[key + 'List'] = 
                 !$scope.hasIns && (!isHis || isFromStep1) && $scope.STEP2VO['PORTFOLIO' + $scope.mapping.code[key] + '_AMT_AFTER'] === 0 ? [] :
                  sortListOf(key,
                    FPSUtilsService.mergeBy(
                      formatList(key, keyList, !isHis), mergeKey[key], mergeCb,
                      function (row, i) {
                        return row.INV_PRD_TYPE_2 === '1' || row.INV_PRD_TYPE_2 === '2' || row.CERT_NBR;
                      }));
              }
              //FPS240是每一檔四捨五入再加相
              $scope[key + 'List'].forEach(function(row) {
            	  row.PURCHASE_ORG_AMT = Math.round(row.PURCHASE_ORG_AMT);
              });
             
              if (!$scope.hasIns && !isHis) { //2020-5-4 by Jacky 無投保也沒有歷史規劃資料, 是第一次進入配置全新規劃
            	  validLimitList($scope[key + 'List']);
            	  
              }
                            
              if ($scope.hasIns) {
                // if 建議 = 0
                if ((!isHis || isFromStep1) && $scope['orign' + key + 'Amt'] === 0) {
                  $scope[key + 'List'].forEach(function (row) {
                    if (row.INV_PRD_TYPE_2 !== '3') {
                      row.PURCHASE_ORG_AMT = 0;
                      row.PURCHASE_TWD_AMT = 0;
                      row.INV_PERCENT = 0;
                    }
                  });
                }
              }
              
              // 設定txnType
              if (key === 'fixed') {
                $scope.fixedList.forEach(function (row) {
                  // 交易指示
                  getTxnType(row.PTYPE, row, row.isStock);
                  updateLimitAmt(row);
                });
            	}
              if (key === 'stock') {
                $scope.stockList.forEach(function (row) {
                  // 交易指示
                  getTxnType(row.PTYPE, row, row.isStock);
                  updateLimitAmt(row);
                });
              }
            });
            
//            console.log("628 $scope.stockList");
//            console.log($scope.stockList);
//            console.log("628 $scope.fixedList");
//            console.log($scope.fixedList);
            reTotal();
            if (!isHis && !$scope.hasIns) {
              // 偏移
              // putExtra2End();
//              putExtra2TWD();
            }

            defer.resolve(true);
            return true;
          }
          $scope.showErrorMsg(tota);
          defer.reject(false);
          return false;
        });
      return defer.promise;
    };

    var save = function (list, step) {
//      debugger;
//      console.log(list);
      var defer = $q.defer();
      if (list && planID) {

        $scope.sendRecv('FPS230', 'save', 'com.systex.jbranch.app.server.fps.fps230.FPS230InputVO', {
            planID: planID,
            prodList: list,
            action: 'update',
            step: step
          },
          function (tota, isError) {
            if (!isError) {
              isFromStep1 = false;
              defer.resolve(tota);
              return true;
            }
            defer.reject(tota);
            return false;
          });
      } else {
        $timeout(function () {
          defer.reject('no list to save');
        });
      }
      return defer.promise;
    };

    /**
     * 去那兒
     */
    $scope.go = function (where, type, data) {
//      debugger;
      switch (where) {
        case 'deposit':
        case 'fixed':
        case 'stock':
          openDialog(pathMap.product, $scope.mapping[where + 'Map'], where);
          break;
        case 'save':
        case 'prev':
        case 'next':
          var isNext = where === 'next';
          var result = checkB4Save(isNext);

          if (result) {
        	$scope.STEP2VO.PORTFOLIO2_AMT_AFTER = $scope.fixedAmt;
          	$scope.STEP2VO.PORTFOLIO3_AMT_AFTER = $scope.stockAmt;
        	$scope.STEP2VO.PORTFOLIO2_RATIO_AFTER = Math.round($scope.fixedAmt/($scope.fixedAmt + $scope.stockAmt)*100);
        	$scope.STEP2VO.PORTFOLIO3_RATIO_AFTER = Math.round($scope.stockAmt/($scope.fixedAmt + $scope.stockAmt)*100);
            if (isNext && $scope.leftPct < 0) {
              // get TWD
              var i = -1;
              listMap('deposit').forEach(function (row, index) {
                var tmp = row.INV_PRD_TYPE_2 === '1' && row.CURRENCY_TYPE === 'TWD' ? index : -1;
                i = tmp > -1 ? tmp : i;
              });
              var twdAmt = i > -1 ? listMap('deposit')[i].PURCHASE_TWD_AMT : 0;
              if (twdAmt > ($scope.totalAmt - $scope.STEP1VO.PLAN_AMT)) {
                return $confirm({
                  text: '總配置比例高於100%，超出的部分是否從現金準備中扣除?'
                }, {
                  size: 'sm'
                }).then(function () {
                  // 少的全部丟到台幣存款
                  listMap('deposit')[i].PURCHASE_TWD_AMT += $scope.STEP1VO.PLAN_AMT - $scope.totalAmt;
                  chgTWD(listMap('deposit')[i]);
                  reTotal('deposit');
                  listMap('deposit')[i].INV_PERCENT = rePercent(listMap('deposit')[i]);
                  $scope.go(where, type);
                });
              } else {
                var classes = $scope.mapping.keys.filter(function (key, i) {
                  return $scope.STEP2VO['PORTFOLIO' + (i + 2) + '_AMT_AFTER_ORIGIN'] < $scope[key + 'Amt'];
                });
                return $confirm({
                  text: '總配置比例超過100%，請調降「' + classes.map(function (c) {
                    return $scope.mapping.keysZhTw[parseInt($scope.mapping.code[c]) - 2];
                  }).join('、') + '」配置比例'
                }, {
                  size: 'sm'
                });
              }
            } else if (isNext && $scope.leftPct > 0) {
//              return $confirm({
//                text: '總配置比例低於100%，是否轉置到台幣存款項目'
//              }, {
//                size: 'sm'
//              }).then(function () {
//                // 多的全部丟到台幣存款
////                putExtra2TWD();
//                $scope.go(where, type);
//              });
            }

            var step = pathMap[where] ? pathMap[where].match(/\d{1}/) : undefined;
            save(result, step ? step[0] : undefined)
              .then(function () {
                if (where === 'save') {
                  $scope.showSuccessMsg('ehl_01_common_025');
                  inquire();
                } else {
                  chgStep(pathMap[where]);
                }
              }, function (error) {
                $scope.showErrorMsg(error);
              });
          }

          break;
        case 'figureAmt':
          var stockList = listMap('stock').filter(function (row) {
            return $scope.hasIns ? row.PURCHASE_TWD_AMT > 0 : true;
          });
          if (stockList.length === 0) {
            $scope.showErrorMsg('請先新增商品');
            return false;
          } else if (stockList.length > 10) {
            $scope.showErrorMsg('最佳配置試算僅限配置十檔商品以內');
            return false;
          } else {
            figureAmt(stockList);
          }
          break;
        case 'revert':
          revertModelPortfolio(type);
          break;
        case 'review':
          openDialog(pathMap.review, reviewModelPortfolio(modelPortfolioList), type);
          break;
        case 'models':
          openDialog(pathMap.models, {
            riskType: riskType,
            totalAmt: $scope.STEP1VO.PLAN_AMT,
            stockAmt: type === 'fixed' ? $scope.STEP2VO.PORTFOLIO2_AMT_AFTER : $scope.STEP2VO.PORTFOLIO3_AMT_AFTER,
            stockPct: type === 'fixed' ? $scope.STEP2VO.PORTFOLIO2_RATIO_AFTER_ORIGIN : $scope.STEP2VO.PORTFOLIO3_RATIO_AFTER_ORIGIN,
            featureDescription: featureDescription,
            mapping: $scope.mapping,
            rateMap: rateMap,
            hasIns: $scope.hasIns,
            type: type,
            txnID: 'FPS230',
            txnVO: 'com.systex.jbranch.app.server.fps.fps230.FPS230InputVO',
            inputVO: {
              custID: custID,
              planID: planID,
              riskType: riskType,
              OBU: $scope.custInfo.OBU_FLAG,
              isPro: $scope.custInfo.CUST_PRO_FLAG,
            },
            returnAll: !$scope.hasIns,
            sppType: 'INV'
          }, where);
          break;
        case 'detail':
          openDialog(pathMap.detail[type], data, type);
          break;
        case 'product':
          var productMap = angular.copy($scope.mapping[type + 'Map']).map(function (row, index) {
        	if(type === 'fixed') {
        		row.active = (data.PTYPE === 'MFD' && index === 0) || (data.PTYPE === 'ETF' && index === 1) || (data.PTYPE === 'BND' && index === 2) || (data.PTYPE === 'SN' && index === 3) || (data.PTYPE === 'SI' && index === 4);
        	} else {
        		row.active = (data.PTYPE === 'MFD' && index === 0) || (data.PTYPE === 'ETF' && index === 1);
        	}
            if (row.active) row.data = {
              prdID: data.PRD_ID,
            };
            return row;
          });
          openDialog(pathMap.product, productMap, type);
          break;
        default:
          break;
      }
    };

    var openDialog = function (path, data, type) {
//      debugger;
      if (!path) return false;
      // type: deposit|fixed|stock|detail
      var scope = $scope;
      var dialog = ngDialog.open({
        template: path,
        className: 'FPS200',
        controller: ['$scope', function ($scope) {
          $scope.data = data;
          $scope.trendPath = scope.mapping.trendPath;
          $scope.cust_id = custID;
          $scope.hasIns = scope.hasIns;
          $scope.riskType = riskType;
          $scope.OBU = scope.custInfo.OBU_FLAG;
          $scope.isPro = scope.custInfo.CUST_PRO_FLAG;
          $scope.row = angular.copy(data);
          $scope.type = type;
          switch (type) {
            case 'product':
              $scope.showInfo = true;
              break;
            case 'BND':
              $scope.row.BOND_NAME = data.PRD_CNAME;
              break;
            case 'MFD':
              $scope.row.FUND_CNAME = data.PRD_CNAME;
              break;
            case 'SN':
              $scope.row.SN_CNAME = data.PRD_CNAME;
              break;
            case 'SI':
              $scope.row.SI_CNAME = data.PRD_CNAME;
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
          }
        }
      });
    };

    var popCbFn = {
      checkDupProd: function (type, data) {
        if (!data) return false;
        var prdList = listMap(type).map(function (a) {
          return a.PRD_ID;
        });
        var hasDuplicate = false;
        data.reduce(function (a, b) {
          return a.concat(b.list);
        }, []).forEach(function (c) {
          if (prdList.indexOf(c.PRD_ID) >= 0)
            hasDuplicate = true;
        });
        if (hasDuplicate) $scope.showErrorMsg('有重複的商品代號不可選擇');
      },
      product: function (data) {
        this.stock(data);
      },
      deposit: function (data) {
        $scope.depositList = sortListOf('deposit', FPSUtilsService.mergeBy(data.reduce(function (a, b) {
          return a.concat(formatList('deposit', b.list, true));
        }, $scope.depositList), mergeKey.deposit, mergeCb, function (row, i) {
          return row.INV_PRD_TYPE_2 === '1' || row.INV_PRD_TYPE_2 === '2';
        }));
        checkError($scope.depositList, true);
      },
      fixed: function (data) {
        $scope.fixedList = sortListOf('fixed', FPSUtilsService.mergeBy(data.reduce(function (a, b) {
          return a.concat(formatList('fixed', b.list, true));
        }, $scope.fixedList), mergeKey.fixed, mergeCb, function (row, i) {
          return row.CERT_NBR;
        }).map(function (newFixed) {
//          if ($scope.hasIns) {
//            getTxnType(newFixed.PTYPE, newFixed, newFixed.isStock);
//          }
          getTxnType(newFixed.PTYPE, newFixed, newFixed.isStock);
          return newFixed;
        }));
        checkError($scope.fixedList, true);
        $scope.fixedList.forEach(function(row){
        	row.INV_PRD_TYPE = '2';
        	row.STOCK_BOND_TYPE = 'B';
        });
      },
      stock: function (data) {
        $scope.stockList = sortListOf('stock', FPSUtilsService.mergeBy(data.reduce(function (a, b) {
          return a.concat(formatList('stock', b.list, true));
        }, $scope.stockList), mergeKey.stock, mergeCb, function (row, i) {
          return row.CERT_NBR;
        }).map(function (newStock) {
//          if ($scope.hasIns) {
//            getTxnType(newStock.PTYPE, newStock, newStock.isStock);
//          }
          getTxnType(newStock.PTYPE, newStock, newStock.isStock);
          return newStock;
        }));
        checkError($scope.stockList, true);
        $scope.stockList.forEach(function(row){
        	row.INV_PRD_TYPE = '3';
        	row.STOCK_BOND_TYPE = 'S';
        });
      },
      models: function (data) {
    	if(!(data && data.length >0)) {
    		return;
    	}
    	
    	for(var i=0;i<data.length;i++){
	       if(data[i].PTYPE === 'NANO'){ //奈米投都是主推
	 		  data[i].MAIN_PRD = 'Y';
	 	    }
	    	if($scope.hasIns){ //有庫存不做配置
	    	  data[i].PURCHASE_ORG_AMT = 0;
		 	  data[i].PURCHASE_TWD_AMT = 0;
		 	     data[i].INV_PERCENT = 0;
	    	}	    	
	    }
    	var item = data[0];
	    if(item.STOCK_BOND_TYPE === 'B') {
		  $scope.fixedList = $scope.hasIns ?
            $scope.fixedList.concat(formatList('fixed', data, true).map(function (newStock) {
              getTxnType(newStock.PTYPE, newStock, false);
              return newStock;
            })) :
            formatList_for_model('fixed', data, true);
          $scope.fixedList.forEach(function (fixed) {
            if(fixed.isStock || !$scope.hasIns) {
        	    chgORG(fixed);
        	    rePercent(fixed);	        	  
            }	else {
            	//不可以清成0, 這樣會造成畫面上已新增商品都會變成0
//        	    fixed.PURCHASE_ORG_AMT = 0;
//	            fixed.PURCHASE_TWD_AMT = 0;
//	            fixed.INV_PERCENT = 0;
//	            fixed.hasError = true;
            }
          });
          if (!$scope.hasIns) {
        	  validLimitList($scope.fixedList);
          }
          checkError($scope.fixedList, true);
          reTotal('fixed');  
	    } else {
//	      debugger;           
		  $scope.stockList = $scope.hasIns ?
            $scope.stockList.concat(formatList('stock', data, true).map(function (newStock) {
              getTxnType(newStock.PTYPE, newStock, false);
              return newStock;
            })) :
            formatList_for_model('stock', data, true);
          $scope.stockList.forEach(function (stock) {
//        	  debugger;
        	  if(stock.isStock || !$scope.hasIns) {
        	    chgORG(stock);
        	    rePercent(stock);	        	  
              } else {
            	  //不可以清成0, 這樣會造成畫面上已新增商品都會變成0
//                stock.PURCHASE_ORG_AMT = 0;
//                stock.PURCHASE_TWD_AMT = 0;
//                stock.INV_PERCENT = 0;
//                stock.hasError = true;
              }
          });
          if (!$scope.hasIns) {
        	  validLimitList($scope.stockList);
          } 
          checkError($scope.stockList, true);
          reTotal('stock');
	    }  
        // 偏移不理他
        // if (!$scope.hasIns) {
        //   // 偏移
        //   putExtra2End();
        // }
      },
      review: function (data) {
        popCbFn.deposit([{
          list: data.filter(function (row) {
            return row.INV_PRD_TYPE === '1';
          })
        }]);
        popCbFn.fixed([{
          list: data.filter(function (row) {
            return row.INV_PRD_TYPE === '2';
          })
        }]);
        popCbFn.stock([{
          list: data.filter(function (row) {
            return row.INV_PRD_TYPE === '3';
          })
        }]);
      }
    };

    // fps325 最佳配置
    var figureAmt = function (list) {
      var listMap = list.reduce(function (a, b) {
        a[b.PRD_ID] = b.PRD_ID + ' ' + b.PRD_CNAME;
        if (b.TARGETS) {
          (b.TARGETS.split('/') || []).forEach(function (t) {
            a[t] = b.PRD_ID + ' ' + b.PRD_CNAME + ': ' + t;
          });
        }
        return a;
      }, {});
      var scope = $scope;
      var dialog = ngDialog.open({
        className: 'FPS200',
        template: pathMap.bestRatio,
        controller: ['$scope', function ($scope) {
          // 6.25 建議配置金額 -> 已規劃金額
          $scope.inputVO = {};
          $scope.inputVO.ONETIME = scope.stockAmt;
          $scope.inputVO.prdList = angular.copy(list);
          $scope.riskType = riskType;
          $scope.stockAmt = scope.stockAmt;
          $scope.stockPct = scope.STEP2VO.PORTFOLIO3_RATIO_AFTER;
          $scope.sppType = 'INV';
          $scope.hasIns = scope.hasIns;
        }]
      });

      dialog.closePromise.then(function (data) {
        if (data.value.status === 'success') {
          data.value.addList.forEach(function (row) {
            $scope.stockList.forEach(function (item) {
              if (row.PRD_ID === item.PRD_ID) {
                item.PURCHASE_ORG_AMT = item.buyCur === '2' ? row.PURCHASE_TWD_AMT : row.PURCHASE_ORG_AMT;
                item.PURCHASE_TWD_AMT = row.PURCHASE_TWD_AMT;
                item.INV_PERCENT = Math.round(row.INV_PERCENT * Number($scope.STEP2VO.PORTFOLIO3_RATIO_AFTER_ORIGIN) / 100*10000000000)/10000000000;
                $scope.chgORGWrapper(item, 'stock');
              }
            });
          });
        } else if (data.value.status === 'error') {
          data.value.errorList.forEach(function (row) {
            var errTxt = listMap[row];
            if (!errTxt) return false;
            $scope.showMsg('ehl_01_FPS325_002', [errTxt]);
          });
        }
      });
    };

    var chgStep = function (step) {
      if (step === 'STEP1')
        $scope.connector('set', 'FPS230_keep_old_amt', {
          fixed: $scope.STEP2VO.PORTFOLIO2_AMT_AFTER,
          stock: $scope.STEP2VO.PORTFOLIO3_AMT_AFTER
        });
      $scope.connector('set', 'STEP', step);
    };

    // 改變Org套裝
    $scope.chgORGWrapper = function (row, type) {
      if(row) {
    	  if ($scope.hasIns && row.INV_PRD_TYPE !== '1') getTxnType(row.PTYPE, row, row.isStock);
    	  var errorSets = new Set();
    	  var type = $scope.mapping.code[row.INV_PRD_TYPE];
		  row.errorCode = [];
		  row.hasError = strictCheckError[type](row);
		  row.errorCode.forEach(function (code) {
			  errorSets.add(code);
		  });
		  		  
          if (row.hasError) {
        	row.PURCHASE_ORG_AMT = row.isStock ? row.NOW_AMT : null;
        	row.PURCHASE_TWD_AMT = row.isStock ? row.NOW_AMT_TWD : null;
        	getTxnType(row.PTYPE, row, row.isStock);
        	row.hasError = !row.isStock;
	        errorSets.forEach(function (key) {
	          var tmp = key.split('||');
	          if (tmp[0] === 'snLimitAlert' || tmp[0] === 'snPrdUnitBackAlert') {
                $scope.showErrorMsg(errorMap[tmp[0]].replace('{0}', tmp[1]));
              } else {
                $scope.showErrorMsg(errorMap[key]);
              }
              return false;
	        });
	        return false;
	      }
    	  
    	  chgORG(row);
    	  reTotal(type);
    	  rePercent(row);
    	  // 改變txnTypeList
    	  // check if error
      }	
    };

    // 如為hasIns 檢核數字
    $scope.checkB4ChgORG = function (type, row) {
      // ng - blur="!row.isStock||checkB4ChgORG(row.PTYPE,row)?chgORGWrapper(row,'fixed'):''"
      var purchase = fps200.formatFloat(parseFloat(row.PURCHASE_ORG_AMT || 0), 1);
      var checksum = false;
      switch (type) {
        case 'BND':
        case 'SI':
          checksum = purchase >= row.NOW_AMT || purchase === 0;
          break;
        case 'SN':
          checksum = true;
          break;
        case 'MFD':
        case 'ETF':
        case 'INS':
          checksum = true;
          break;
      }
      return checksum;
    };

    /* sub function */

    // 改變Org
    var chgORG = function (row) {
      // console.log(row);
      if(row) {
    	  var cur = '';
//    	  if (row.INV_PRD_TYPE === '3') {
		  switch (row.buyCur) {
		  case '1':
			  cur = row.TRUST_CURR || row.CURRENCY_TYPE;
			  break;
		  case '2':
			  cur = 'TWD';
			  break;
		  default:
			  cur = row.TRUST_CURR || row.CURRENCY_TYPE;
		  }
//    	  } else {
//    		  cur = row.TRUST_CURR || row.CURRENCY_TYPE || '';
//    	  }
    	  if (!rateMap[cur] || !rateMap[cur].buy) {
    		  console.log('no ' + cur + ' currency rate');
    		  console.log(row);
    	  }
    	  var rate = rateMap[cur] && rateMap[cur].buy ? rateMap[cur].buy : 1; // with no wrong
    	  // row.PURCHASE_ORG_AMT = !cur && row.INV_PRD_TYPE === '3' ? 0 : fps200.formatFloat(parseFloat(row.PURCHASE_ORG_AMT || 0), 1);
//    	  debugger;
    	  row.PURCHASE_ORG_AMT = !cur && row.INV_PRD_TYPE === '3' ? 0 : Math.round((Number(row.PURCHASE_ORG_AMT) || 0)*10000000000)/10000000000;
    	  row.PURCHASE_TWD_AMT = !cur && row.INV_PRD_TYPE === '3' ? 0 : (Math.round(((Number(row.PURCHASE_ORG_AMT) || 0))*10000000000)/10000000000)* rate;
      }	
    };

    var chgTWD = function (row) {
      // console.log(row);
      var cur = '';
//      if (row.INV_PRD_TYPE === '3') {
        switch (row.buyCur) {
          case '1':
            cur = row.TRUST_CURR || row.CURRENCY_TYPE;
            break;
          case '2':
            cur = 'TWD';
            break;
          default:
            cur = row.TRUST_CURR || row.CURRENCY_TYPE;
        }
//      } else {
//        cur = row.TRUST_CURR || row.CURRENCY_TYPE || '';
//      }
      //
      if (!rateMap[cur] || !rateMap[cur].buy) {
        console.log('no ' + cur + ' currency rate');
        console.log(row);
      }
      var rate = rateMap[cur] && rateMap[cur].buy ? rateMap[cur].buy : 1; // with no wrong
      row.PURCHASE_TWD_AMT = !cur && row.INV_PRD_TYPE === '3' ? 0 : Math.round((Number(row.PURCHASE_TWD_AMT) || 0)*10000000000)/10000000000;
      row.PURCHASE_ORG_AMT = !cur && row.INV_PRD_TYPE === '3' ? 0 : (Math.round(((Number(row.PURCHASE_TWD_AMT) || 0))*10000000000)/10000000000)/ rate;
    };

    // 重新計算總額和tab比例 順便檢查error
    var reTotal = function (type) {
//      debugger;
      switch (type) {
        case 'deposit':
        case 'fixed':
        case 'stock':
          $scope[type + 'List'].forEach(function(row){
//           	  row.TEMP_PURCHASE_TWD_AMT = row.PURCHASE_TWD_AMT;
        	  row.TEMP_PURCHASE_TWD_AMT = exTwdAmt(row,row.PURCHASE_ORG_AMT);
        	  row.TEMP_PURCHASE_ORG_AMT = row.PURCHASE_ORG_AMT;
//           	  row.PURCHASE_TWD_AMT = Math.round(row.PURCHASE_TWD_AMT);
        	  row.PURCHASE_TWD_AMT = Math.round(exTwdAmt(row,row.PURCHASE_ORG_AMT));
        	  row.PURCHASE_ORG_AMT = Math.round(row.PURCHASE_ORG_AMT);
          });       
          $scope[type + 'TempAmt'] = FPSUtilsService.sumList($scope[type + 'List'], 'PURCHASE_TWD_AMT');
          $scope[type + 'Amt'] = Math.round(FPSUtilsService.sumList($scope[type + 'List'], 'PURCHASE_TWD_AMT'));
          $scope[type + 'Pct'] = Math.round(($scope[type + 'Amt'] / $scope.STEP1VO.PLAN_AMT) * 100) || 0;
         
          drawDoughnut();
          break;
        default:
          $scope.mapping.keys.forEach(function (key) {
            reTotal(key);
          });
          break;
      }

      // count sum
      $scope.totalAmt = Math.round($scope.fixedTempAmt + $scope.stockTempAmt);
      
      //2020-5-4 增加檢查是否超過可規劃金額,自動調整
//      debugger;
      if($scope.STEP1VO.PLAN_AMT - $scope.totalAmt < 0){ //可規劃金額超過配置金額
    	  if (!$scope.hasIns && !$scope.isHisPlan && !type){ //2020-5-4 無投保之全新規劃  , type空值為initial進入畫面時
    		  if($scope.stockList.length > 0){
    			  var diffAmt = exAmt($scope.stockList[0] , $scope.totalAmt - $scope.STEP1VO.PLAN_AMT);
    			  $scope.stockList[0].PURCHASE_ORG_AMT = $scope.stockList[0].PURCHASE_ORG_AMT - diffAmt;
    			  chgORG($scope.stockList[0]);
    			  reTotal("stock");
    		  }else{
    			  if($scope.fixedList.length > 0){
    				  var diffAmt = exAmt($scope.fixedList[0] , $scope.totalAmt - $scope.STEP1VO.PLAN_AMT);
    				  $scope.fixedList[0].PURCHASE_ORG_AMT = $scope.fixedList[0].PURCHASE_ORG_AMT - diffAmt;
    				  chgORG($scope.fixedList[0]);
        			  reTotal("fixed");
    			  }
    		  }
    	  }
      }
      
      // count pct
      $scope.leftPct = Math.round((($scope.STEP1VO.PLAN_AMT - $scope.totalAmt) / $scope.STEP1VO.PLAN_AMT) * 100) || 0;
      
      
      // 金額超過但比例無超過則無條件進位 & vice versa
      if ($scope.STEP1VO.PLAN_AMT - $scope.totalAmt < 0 && $scope.leftPct === 0) {
        // over
        $scope.leftPct -= 1;
      } else if ($scope.STEP1VO.PLAN_AMT - $scope.totalAmt > 0 && $scope.leftPct === 0) {
        // less
        $scope.leftPct += 1;
      }
    };
    
    //計算台幣金額
    var exTwdAmt = function(row , orgAmt){
    	var cur = row.TRUST_CURR || row.CURRENCY_TYPE;
		var rate = rateMap[cur] && rateMap[cur].buy ? rateMap[cur].buy : 1; // with no wrong
		var twdAmt = orgAmt * rate;
		return twdAmt;
    }
    
    //計算台幣金額換算原幣
    var exAmt = function(row , diffAmt){
    	var cur = row.TRUST_CURR || row.CURRENCY_TYPE;
		var rate = rateMap[cur] && rateMap[cur].buy ? rateMap[cur].buy : 1; // with no wrong
		var exAmt = diffAmt/rate;
		return exAmt < 1 ? 1 : Math.round(exAmt);
    }
    
    // 重新計算item比例
    var rePercent = function (row, idDecimal) {
      if(row) {
    	  row.INV_PERCENT = Math.round((row.PURCHASE_TWD_AMT / $scope.STEP1VO.PLAN_AMT) * 100);
    	  return Math.round((row.PURCHASE_TWD_AMT / $scope.STEP1VO.PLAN_AMT) * 100 * (idDecimal ? 10000000000 : 1)) / (idDecimal ? 10000000000 : 1);    	  
      } else {
    	  return 0;
      }
    };

    // 刪除
    $scope.deleteRow = function (type, row, index) {
      $confirm({
        text: '您確定要刪除此筆資料?',
      }, {
        size: 'sm'
      }).then(function () {
        if (row.action) {
          listMap('delete').push(row);
        }
        listMap(type).splice(index, 1);
        reTotal(type);
      });
    };

    // 更動原幣金額
    $scope.chgBuyCur = function (row) {
      if (!row.buyCur) {
        row.buyCur = '1';
        return $scope.chgBuyCur(row);
      } else if (row.buyCur === '1') {
        chgTWD(row);
      } else if (row.buyCur === '2') {
        row.PURCHASE_ORG_AMT = row.PURCHASE_TWD_AMT || 0;
      }
      updateLimitAmt(row);
      reTotal($scope.mapping.code[row.INV_PRD_TYPE]);
      row.INV_PERCENT = rePercent(row);
    };

    // 更動投資方式
    $scope.chgInvType = function (row) {
      if (!row.INV_TYPE) {
        row.INV_TYPE = '1';
      }
      updateLimitAmt(row);
      
      if(row.STOCK_BOND_TYPE === 'S')
    	  row.hasError = strictCheckError.stock(row);
    };

    var updateLimitAmt = function (row) {
      // 如果是類股票
      if(row.isStock) {
    	  if(row.PTYPE === 'MFD' || row.PTYPE === 'ETF') {
    		  row.limitAmt = Number(row.SML_SUBS_MINI_AMT_FOR);
    	  }
    	  //增加奈米投申購門檻
    	  if(row.PTYPE === 'NANO'){
    		  row.limitAmt = 100;
    	  }
      } 
      // 如果不是類股票
      else {
    	  if(row.PTYPE === 'MFD' || row.PTYPE === 'ETF') {
    		  row.limitAmt = row.INV_TYPE !== '2' ?
		        (row.buyCur !== '2' ? Math.ceil(Number(row.GEN_SUBS_MINI_AMT_FOR)) : $scope.invType1LimitTWD) :
		        (row.buyCur !== '2' ? Math.ceil(Number(row.SML_SUBS_MINI_AMT_FOR)) : $scope.invType2LimitTWD);
    	  }
    	  //增加奈米投申購門檻
    	  if(row.PTYPE === 'NANO'){
    		  row.limitAmt = 100;
    	  }
      }
      
//        (row.buyCur !== '2' ? Math.ceil(Number(row.GEN_SUBS_MINI_AMT_FOR)) : $scope.invType1LimitTWD) :
//        (row.buyCur !== '2' ? Math.ceil(Number(row.SML_SUBS_MINI_AMT_FOR)) : $scope.invType2LimitTWD);
    };

    // 匯入原始
    var revertModelPortfolio = function (type) {
      $confirm({
        text: '匯入原始建議將取代原規劃，確認是否匯入?'
      }, {
        size: 'sm'
      }).then(function () {
        $scope[type + 'List'] =
          sortListOf(type,
            formatList(type,
              calAmtFromPct(
                filterList(modelPortfolioList, 'INV_PRD_TYPE', $scope.mapping.code[type])), true));
        reTotal(type);
        // putExtra2End(type);
      });
    };

    // 總行配置
    var reviewModelPortfolio = function (list) {
      // console.log(list);
      var newList = angular.copy(list).map(function (row) {
        row.textPTYPE = row.INV_PRD_TYPE === '1' && row.INV_PRD_TYPE_2 !== '3' ? '存款' : $filter('mapping')(row.PTYPE, $scope.mapping.prodType);
        row.textPRD_ID = row.INV_PRD_TYPE === '1' && row.INV_PRD_TYPE_2 !== '3' ? '' : row.PRD_ID;
        row.textPRD_CNAME = row.INV_PRD_TYPE === '1' && row.INV_PRD_TYPE_2 !== '3' ? $filter('mapping')(row.INV_PRD_TYPE_2, $scope.mapping.invProdType) : row.PRD_CNAME;
        return row;
      });
      return newList;
    };
    
    var calAmtFromPctRow = function(row) {
//    	var bias = $scope.modelBias[$scope.mapping.code[row.INV_PRD_TYPE]] || 0;
        // plan A
        // row.PURCHASE_TWD_AMT = Math.round((row.INV_PERCENT * bias * $scope.STEP1VO.PLAN_AMT) / 100);
        // plan B
    	if(!$scope.hasIns) {    		
    		
    		row.PURCHASE_TWD_AMT = Math.round(((row.PORTFOLIO_RATIO / 100) * $scope.STEP1VO.PLAN_AMT)*10000000000)/10000000000;
            chgTWD(row);
//        
    		if(row.PURCHASE_ORG_AMT < row.GEN_SUBS_MINI_AMT_FOR ) {
    			row.PURCHASE_TWD_AMT = Math.round(row.PURCHASE_TWD_AMT);
    			chgTWD(row);
    		}
    	}
        chgORG(row);
    }

    // 儲存前檢核
    var checkB4Save = function (isStrict) {
      // result deposit|fixed|stock|delete
      // 0314 delete all then create
      var listMapCb = function (row) {
    	var newRow = {
          isStock: row.isStock,
          PLAN_ID: planID,
          SEQNO: row.SEQNO || undefined,
          // 台外幣用currency
          PRD_ID: (row.INV_PRD_TYPE === '1' && row.INV_PRD_TYPE_2 !== '3') ? row.CURRENCY_TYPE : row.PRD_ID,
          PRD_TYPE: row.INV_PRD_TYPE === '1' ? (row.INV_PRD_TYPE_2 === '3' ? 'INS' : undefined) : row.PTYPE,
          PRD_NAME: row.PRD_CNAME,
          RISKCATE_ID: row.RISK_TYPE || undefined,
          PROD_CURR: row.CURRENCY_TYPE,
          TRUST_CURR: row.INV_PRD_TYPE === '1' ? row.CURRENCY_TYPE : (row.buyCur === '1' ? row.CURRENCY_TYPE : 'TWD'),
          MARKET_CIS: row.CIS_3M || undefined,
          PURCHASE_ORG_AMT: row.TEMP_PURCHASE_ORG_AMT,
          PURCHASE_TWD_AMT: row.TEMP_PURCHASE_TWD_AMT,
          INVENTORY_ORG_AMT: isStrict ? row.NOW_AMT : undefined,
          INVENTORY_TWD_AMT: isStrict ? row.NOW_AMT_TWD : undefined,
          PORTFOLIO_RATIO: rePercent(row, true),
          GEN_SUBS_MINI_AMT_FOR: row.GEN_SUBS_MINI_AMT_FOR,
          SML_SUBS_MINI_AMT_FOR: row.SML_SUBS_MINI_AMT_FOR,
          PRD_UNIT: row.PRD_UNIT,
          PORTFOLIO_TYPE: undefined,
          INV_PRD_TYPE_2: (row.PTYPE == 'NANO' && !row.INV_PRD_TYPE_2) ? row.INS_TYPE : row.INV_PRD_TYPE_2, //奈米投的投資策略欄位放在INV_PRD_TYPE_2欄位,重抓庫存時才可以串到產品主檔
          INV_PRD_TYPE: row.STOCK_BOND_TYPE === 'B' ? '2' : '3',
          INV_TYPE: row.INV_TYPE || undefined,
          // TXN_TYPE: row.isStock ? (row.INV_PRD_TYPE === '3' ? row.TXN_TYPE : undefined) : (!row.PTYPE ? undefined : '1'),
          TXN_TYPE: row.isStock ? (!row.PTYPE ? undefined : row.TXN_TYPE) : (!row.PTYPE ? undefined : row.TXN_TYPE),
          targets: row.INV_PRD_TYPE === '3' ? row.targetIDs || row.TARGETS : undefined,
          limitAmt: row.limitAmt,
          CERT_NBR: row.CERT_NBR,
          buyCur: row.buyCur,
        } 
//    	calAmtFromPctRow(newRow);
        return newRow;
      };

      var rd = listMap('deposit').map(function (row) {
        return listMapCb(row);
      });
      var fd = listMap('fixed').map(function (row) {
        return listMapCb(row);
      });
      var rs = listMap('stock').map(function (row) {
        row.targets = row.targets || [];
        return listMapCb(row);
      });

      
      var totalList = rd.concat(rs, fd);
      console.log(totalList);
      console.log(checkError(totalList, isStrict));
      // check error

      var errorSets = checkError(totalList, isStrict);
      if (errorSets.size > 0) {
        errorSets.forEach(function (key) {
          var tmp = key.split(',');
          if (tmp[0] === 'snLimitAlert') {
            $scope.showErrorMsg(errorMap.F2.replace('{0}', tmp[1]));
          } else {
            $scope.showErrorMsg(errorMap[key]);
          }
        });
        return false;
      }

      return totalList;
    };

    /**
     * Check Save Logic
     */

    // 檢核邏輯 => Set
    var checkError = function (checkList, isStrict) {
      var errorSet = new Set();
      if(checkList) {
    	  checkList.forEach(function (row) {
    		  var type = $scope.mapping.code[row.INV_PRD_TYPE];
    		  row.errorCode = [];
    		  row.hasError = defaultCheckError[type](row) || (isStrict && strictCheckError[type](row));
    		  row.errorCode.forEach(function (code) {
    			  errorSet.add(code);
    		  });
    	  });
      }
      return errorSet;
    };

    // 基本檢核 (資料檢核)
    var defaultCheckError = {
      deposit: function (row) {
        var flag = (row.INV_PRD_TYPE_2 === '3' ? !row.PRD_ID : !(row.CURRENCY_TYPE || row.TRUST_CURR));
        if (flag) row.errorCode.push('redAlert');
        return flag;
      },
      fixed: function (row) {
        var flag = !row.PRD_ID;
        if (flag) row.errorCode.push('redAlert');
        return flag;
      },
      stock: function (row) {
        var flag = !row.PRD_ID;
        if (flag) row.errorCode.push('redAlert');
        return flag;
      }
    };

    // 嚴格檢核 (數字檢核)
    var strictCheckError = {
      deposit: function (row) {
        var orgAmt = Number(row.PURCHASE_ORG_AMT);
        var orgInvAmt = Number((row.INVENTORY_ORG_AMT || row.NOW_AMT)) || 0;

        // 輸入不為0
        // 輸入>最低申購金額
        var notZero = (!row.isStock && orgAmt <= 0);
        var minLimit = (row.INV_PRD_TYPE_2 === '3' && orgAmt < (Number(row.GEN_SUBS_MINI_AMT_FOR) || 0));
        if (notZero) row.errorCode.push('redAlert');
        if (minLimit) row.errorCode.push('limitAlert' + ($scope.hasIns ? 'HasIns' : ''));

        return notZero || minLimit;
      },
      fixed: function (row) {
    	  
        // 調整金額
        var orgAmt = Math.round(Number(row.PURCHASE_ORG_AMT));
        // 庫存金額
        var orgInvAmt = Number((row.INVENTORY_ORG_AMT || row.NOW_AMT)) || 0;
        // 輸入不為0
        // 輸入>最低申購金額 (!isStock) 調整後>調整前 (isStock && divide > 0)
        // 贖回金額 > 最低贖回金額 (isStock && SN)
        // 贖回金額 = 庫存金額 (isStock && SI BND)
        // (SI SN)庫存不可再申購
        // SN BND(有PRD_UNIT) 要為其倍數
        var notZero = (!row.isStock && orgAmt <= 0);
        // var minLimit = ((!row.isStock || (orgAmt - orgInvAmt > 0)) &&
        //   ((!row.isStock ? orgAmt : (orgAmt - orgInvAmt)) < (Number(row.GEN_SUBS_MINI_AMT_FOR) || 0)));
        var minLimit = !row.isStock && (orgAmt < (Number(row.GEN_SUBS_MINI_AMT_FOR) || 0));
        
        var siBndBackLimit = false;
        siBndBackLimit = ((row.isStock && (orgAmt - orgInvAmt < 0) && ((row.PTYPE || row.PRD_TYPE) === 'BND' || (row.PTYPE || row.PRD_TYPE) === 'SI')) && (orgAmt !== 0));
        // var sisnStockPurchase = (row.isStock && ((row.PTYPE || row.PRD_TYPE) === 'SI' || (row.PTYPE || row.PRD_TYPE) === 'SN') && (orgAmt > orgInvAmt));
        var noReBuy = (row.isStock && (orgAmt > orgInvAmt));
        var snBndMultiNum = !row.isStock && (orgAmt - orgInvAmt) > 0 && row.PRD_UNIT && ((orgAmt - row.GEN_SUBS_MINI_AMT_FOR - orgInvAmt) % Number(row.PRD_UNIT) !== 0);
//        row.isStock ? ((orgAmt - orgInvAmt < 0) && orgAmt !== 0) && (orgInvAmt - orgAmt) > 0 && row.PRD_UNIT && ((orgInvAmt - orgAmt) % Number(row.PRD_UNIT) !== 0) :

        var snBackAlert = row.isStock && (orgAmt !== 0) && orgAmt !== orgInvAmt && orgAmt < (Number(row.SML_SUBS_MINI_AMT_FOR) || 0);
        
        if (notZero) row.errorCode.push('redAlert');
        if (minLimit) row.errorCode.push('limitAlert' + ($scope.hasIns ? 'HasIns' : ''));
        if (siBndBackLimit) row.errorCode.push('noPartBack');
        if (noReBuy) row.errorCode.push('noMoreBuy');
        if (snBndMultiNum) row.errorCode.push('notMultiNum');
        if (snBackAlert) row.errorCode.push('snBackAlert');
        
        var snCheckPrdUnit = 0;
        var backAlert = false;
        if((row.PTYPE || row.PRD_TYPE) === 'MFD' ||(row.PTYPE || row.PRD_TYPE) === 'ETF' ||(row.PTYPE || row.PRD_TYPE) === 'NANO') {
        	return strictCheckError.stock(row);
        } else {
            // 庫存只檢查 SN
            if(row.isStock) {
            	if(!snBackAlert) {
	            	if(orgAmt !== 0 && (orgAmt < orgInvAmt) && (row.PTYPE || row.PRD_TYPE) === 'SN' && row.PRD_UNIT) {
	            		// 先計算門檻值
	            		var arrayGap = [];
	            		var suggestValue = null;
	            		
	            		for(var i = 0; i <= Math.floor((orgInvAmt-row.SML_SUBS_MINI_AMT_FOR)/row.PRD_UNIT) ; i++) {
	            			suggestValue = orgInvAmt - (row.SML_SUBS_MINI_AMT_FOR + row.PRD_UNIT*i);
	            			if(orgInvAmt - suggestValue >= row.SML_SUBS_MINI_AMT_FOR && suggestValue > row.SML_SUBS_MINI_AMT_FOR) {
	            				arrayGap[i] = suggestValue;
	            			} 
	            		}
	            		
	            		suggestValue = null;
	            		var isSame = false
	            		for(var i = 0; i<arrayGap.length; i++) {
	            			
	            			if(i == 0 && orgInvAmt > orgAmt && orgAmt !== 0 && orgAmt > arrayGap[0] ) {
	            				suggestValue = (arrayGap[0]).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	            				break;
	            			}
	            			
	            			if(arrayGap[i] > orgAmt && orgAmt > arrayGap[i+1]) {
	            				suggestValue = (arrayGap[i]).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	            			} else if(arrayGap[i] == orgAmt || orgAmt == arrayGap[i+1]) {
	            				isSame = true;
	            			}
	            		}
	            		
	            		if(suggestValue == null) {
	            			suggestValue= '0，全部贖回'
	            		}
//	            		alert(JSON.stringify(arrayGap));
	            		snCheckPrdUnit = true && !isSame;
	            	}
            	}
            } 
            // 非庫存檢查 BND/SI
            else {
            	backAlert = row.PRD_UNIT && ((orgAmt) % Number(row.PRD_UNIT) !== 0);
            }
        }
        if (backAlert) row.errorCode.push('backAlert');
        if (snCheckPrdUnit) row.errorCode.push('snPrdUnitBackAlert||' + suggestValue);
        
        var snBackLimit = false;
        if(!snCheckPrdUnit && !snBackAlert) {
        	snBackLimit = (row.isStock && (orgAmt - orgInvAmt < 0) && orgAmt !== 0 && (row.PTYPE || row.PRD_TYPE) === 'SN') &&
            ((orgInvAmt - orgAmt) < (Number(row.SML_SUBS_MINI_AMT_FOR) || 0) ||
              orgInvAmt < (Number(row.SML_SUBS_MINI_AMT_FOR) || 0));
        	
        	var x = (row.NOW_AMT-row.SML_SUBS_MINI_AMT_FOR);
        	if (snBackLimit) row.errorCode.push('snLimitAlert||' + x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ","));
        }

        return notZero || minLimit || siBndBackLimit || snBackLimit || noReBuy || snBndMultiNum || snBackAlert || backAlert || snCheckPrdUnit;
      },
      stock: function (row) {
    	  
    	// 調整金額
        var orgAmt = Math.round(Number(row.PURCHASE_ORG_AMT));
    	// 庫存金額
    	var orgInvAmt = (row.TRUST_CURR === 'TWD' ? Number((row.INVENTORY_TWD_AMT || row.NOW_AMT_TWD)) : Number((row.INVENTORY_ORG_AMT || row.NOW_AMT))) || 0;

        // 輸入不為0
        // 輸入>最低申購金額 (!isStock) 皆為申購 (isStock && txnType = 'F1', 'I1')
        // 輸入有改變但無交易指示 (isStock)
        // 庫存不可再申購
        var notZero = (!row.isStock && orgAmt <= 0);
        // var minLimit = ((!row.isStock || (row.TXN_TYPE === 'F1' || row.TXN_TYPE === 'I1')) &&
        //   ((!row.isStock ? orgAmt : (orgAmt - orgInvAmt)) < (Number(row.limitAmt) || 0)));
        var minLimit = !row.isStock && (orgAmt < (Number(row.limitAmt) || 0));
        var noTxnType = (row.isStock && (orgAmt - orgInvAmt !== 0) && !row.TXN_TYPE);
        var noReBuy = (row.isStock && (orgAmt > orgInvAmt));
        
        // 如果是庫存 且輸入不是空 那麼 贖回的金額要大於 贖回門檻
        var backAlert = row.isStock && (orgAmt !== 0) && (orgAmt !== orgInvAmt) && (orgAmt<orgInvAmt) && (orgInvAmt - orgAmt) < (Number(row.limitAmt) || 0);
        
        if (notZero || noTxnType) row.errorCode.push('redAlert');
        if (minLimit) row.errorCode.push('limitAlert' + ($scope.hasIns ? 'HasIns' : ''));
        if (noReBuy) row.errorCode.push('noMoreBuy');
        var x = (row.NOW_AMT-row.limitAmt);
        if (backAlert) row.errorCode.push('snLimitAlert||' + x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ","));
        
        //加上奈米投輸入檢查-不可以部分贖回
        var nanoRedeemAlert = false;
        if(row.PTYPE == 'NANO'){
		    if(row.NOW_AMT > 0 && row.PURCHASE_ORG_AMT < row.NOW_AMT && row.PURCHASE_ORG_AMT != 0){
		    	 nanoRedeemAlert = true;
		    	 row.errorCode.push('nanoRedeemAlert');
				 row.PURCHASE_ORG_AMT = row.NOW_AMT;
				 row.PURCHASE_TWD_AMT = row.NOW_AMT_TWD;
			}
		 }
        return notZero || minLimit || noTxnType || noReBuy || backAlert || nanoRedeemAlert;
      }
    };

    /* helping function */
    var filterList = function (list, key, val) {
      return angular.copy(list).filter(function (row) {
        return (key ? row[key] : row) === val;
      });
    };

    // model 計算新比例偏差
    var calModelBias = function (modelList) {
      $scope.modelBias = {};
      // filter list
      $scope.mapping.keys.forEach(function (key) {
        // step2 pct / model pct
        var step2Pct = $scope.STEP2VO['PORTFOLIO' + $scope.mapping.code[key] + '_RATIO_AFTER_ORIGIN'] || 0;
        var modelPct = FPSUtilsService.sumList(filterList(modelList, 'INV_PRD_TYPE', $scope.mapping.code[key]), 'INV_PERCENT') || 0;
        $scope.modelBias[key] = (step2Pct / (modelPct || step2Pct)) || 0;
      });
    };

    // model 以比例計算Amt
    var calAmtFromPct = function (list) {
      list.forEach(function (row, i) {
    	debugger;
        var bias = $scope.modelBias[$scope.mapping.code[row.INV_PRD_TYPE]] || 0;
        // plan A
        // row.PURCHASE_TWD_AMT = Math.round((row.INV_PERCENT * bias * $scope.STEP1VO.PLAN_AMT) / 100);
        // plan B
        row.PURCHASE_TWD_AMT = Math.round(((row.INV_PERCENT / 100) * (($scope.STEP1VO.PLAN_AMT) * $scope.STEP2VO['PORTFOLIO' + row.INV_PRD_TYPE + '_RATIO_AFTER_ORIGIN']/100)
          )*10000000000)/10000000000;
        chgTWD(row);
        
        if(row.PURCHASE_ORG_AMT < row.GEN_SUBS_MINI_AMT_FOR ) {
        	row.PURCHASE_TWD_AMT = Math.round(row.PURCHASE_TWD_AMT);
        	chgTWD(row);
        }

        // 要累進金額
        if (row.INV_PRD_TYPE === '2' && row.PRD_UNIT) {
          row.PURCHASE_ORG_AMT = row.PURCHASE_ORG_AMT - (row.PURCHASE_ORG_AMT % row.PRD_UNIT);
          chgORG(row);
        }
      });

      return validLimitList(list);
    };
    
    // 檢核更改list後更改邏輯
    var validLimitList = function (list) {
      var rmSaving = false;
      var rmIndex = [];
      var rmAmt = 0;
      var twdIndex = -1;

      // 1115 如果其中一項不足申購門檻，全部改為0
      var nowFlag = undefined;
      var fixedZeroFlag = false;
      var stockZeroFlag = false;
      list.forEach(function (row, i) {
//    	debugger;
    	if(nowFlag === undefined) {
    		if(row.INV_PRD_TYPE === '2') {
    			nowFlag = 'fixed';
    		} else if(row.INV_PRD_TYPE === '3') {
    			nowFlag = 'stock';
    		}
    	}  
    	  
        // 要累進金額
        if (row.INV_PRD_TYPE === '2' && row.PRD_UNIT) {
          row.PURCHASE_ORG_AMT = row.PURCHASE_ORG_AMT - (row.PURCHASE_ORG_AMT % row.PRD_UNIT);
          chgORG(row);
        }
        // 儲蓄型保險+存款
//        else if (row.INV_PRD_TYPE_2 === '1') twdIndex = i;
        else if (row.INV_PRD_TYPE_2 === '2' && row.PURCHASE_ORG_AMT === 0) rmIndex.unshift(i);
        
        // 類債券
        else if (row.INV_PRD_TYPE === '2') {
        	if(row.PTYPE === 'MFD' || row.PTYPE === 'ETF') {
        		if (row.PURCHASE_ORG_AMT < Number(row.limitAmt)) {
        			fixedZeroFlag = true;        			
        		}
        	} else {
        		if (row.PURCHASE_ORG_AMT < Number(row.GEN_SUBS_MINI_AMT_FOR)) {
        			fixedZeroFlag = true;        			
        		}
        	}
        }
        
        else if (row.INV_PRD_TYPE_2 === '3' && row.PURCHASE_ORG_AMT < Number(row.GEN_SUBS_MINI_AMT_FOR)) {
          rmIndex.unshift(i);
          rmSaving = true;
          rmAmt = row.PURCHASE_TWD_AMT;
        }
        // 類股票
        else if (row.INV_PRD_TYPE === '3' && row.PURCHASE_ORG_AMT < Number(row.GEN_SUBS_MINI_AMT_FOR)) {
          stockZeroFlag = true;
        }
      });

      // 將stock轉為0
      if (fixedZeroFlag && nowFlag === 'fixed') {
          $scope.showMsg('部份商品未達該檔商品最低申購金額，無法適用總行建議比例，請理專自行輸入金額。');
          var zeroAmt = 0;
          list.filter(function (row) {
            return row.INV_PRD_TYPE === '2';
          }).forEach(function (row) {
            zeroAmt += row.PURCHASE_TWD_AMT;
            row.PURCHASE_ORG_AMT = 0;
            row.PURCHASE_TWD_AMT = 0;
            row.hasError = true;
            chgORG(list[twdIndex]);
          });

          if (twdIndex > -1) {
            list[twdIndex].PURCHASE_TWD_AMT += zeroAmt;
            chgTWD(list[twdIndex]);
          }
      }
      
      if (stockZeroFlag && nowFlag === 'stock') {
        $scope.showMsg('部份商品未達該檔商品最低申購金額，無法適用總行建議比例，請理專自行輸入金額。');
        var zeroAmt = 0;
        list.filter(function (row) {
          return row.INV_PRD_TYPE === '3';
        }).forEach(function (row) {
          zeroAmt += row.PURCHASE_TWD_AMT;
          row.PURCHASE_ORG_AMT = 0;
          row.PURCHASE_TWD_AMT = 0;
          row.hasError = true;
          chgORG(list[twdIndex]);
        });

        if (twdIndex > -1) {
          list[twdIndex].PURCHASE_TWD_AMT += zeroAmt;
          chgTWD(list[twdIndex]);
        }
      }

      // 如果其 saving ins amt < limit 補至台幣, 因為已經排序過 下一筆即是台幣
//      if (rmSaving && $scope.STEP2VO.PORTFOLIO1_AMT_AFTER !== 0) {
//        $scope.showMsg('儲蓄型保險商品配置金額未達該檔商品最低申購金額，原配置金額全部轉入台幣存款。');
//        if (twdIndex > -1) {
//          list[twdIndex].PURCHASE_TWD_AMT += rmAmt;
//          chgTWD(list[twdIndex]);
//        }
//      }

      // rm
      if (rmIndex.length > 0 && $scope.STEP2VO.PORTFOLIO1_AMT_AFTER !== 0) {
        rmIndex.forEach(function (i) {
          list.splice(i, 1);
        });
      }
      return list;
    };

    // model 偏差值存數最後商品
    var putExtra2End = function (type) {
      var defaultTypes = ['fixed', 'stock'];
      (type ? [type] : defaultTypes).forEach(function (key) {
        // 留給putExtra2TWD
//        if (key === 'deposit') return putExtra2TWD(key);
        if ($scope.STEP2VO['PORTFOLIO' + $scope.mapping.code[key] + '_AMT_AFTER'] !== $scope[key + 'Amt'] && $scope[key + 'List'].length > 0) {
          var divide = $scope.STEP2VO['PORTFOLIO' + $scope.mapping.code[key] + '_AMT_AFTER'] - $scope[key + 'Amt'];
          $scope[key + 'List'][$scope[key + 'List'].length - 1].PURCHASE_TWD_AMT += divide;
          chgTWD($scope[key + 'List'][$scope[key + 'List'].length - 1]);
          reTotal(key);
        }
      });
    };

    // model 偏差值存數台幣 totalAmt || depositAmt
    var putExtra2TWD = function (type) {
      var minus = type ? ($scope.STEP2VO.PORTFOLIO1_AMT_AFTER - $scope.depositAmt) : ($scope.STEP1VO.PLAN_AMT - $scope.totalAmt);
      if (minus !== 0) {
        // 多的全部丟到台幣存款
        var i = -1;
        listMap('deposit').forEach(function (row, index) {
          var tmp = row.INV_PRD_TYPE_2 === '1' && row.CURRENCY_TYPE === 'TWD' ? index : -1;
          i = tmp > -1 ? tmp : i;
        });

        if (i > -1) {
          listMap('deposit')[i].PURCHASE_TWD_AMT += $scope.STEP1VO.PLAN_AMT - $scope.totalAmt;
          chgTWD(listMap('deposit')[i]);
        } else {
          i = listMap('deposit').length;
          listMap('deposit').push({
            CIS_3M: null,
            CURRENCY_TYPE: 'TWD',
            FUND_TYPE_NAME: null,
            INV_PERCENT: 0,
            INV_PRD_TYPE: '1',
            INV_PRD_TYPE_2: '1',
            LIMIT_ORG_AMT: null,
            PLAN_ID: planID,
            PORTFOLIO_TYPE: null,
            PRD_CNAME: undefined,
            PRD_ID: 'TWD',
            PTYPE: null,
            PURCHASE_ORG_AMT: minus,
            PURCHASE_TWD_AMT: minus,
            RISK_TYPE: null,
            SEQNO: undefined,
            TRUST_CURR: 'TWD',
            buyCur: '2'
          });
        }
        reTotal('deposit');
        listMap('deposit')[i].INV_PERCENT = rePercent(listMap('deposit')[i]);
      }
    };

    // 設定row TxnType
    var getTxnType = function (type, row, isStock) {
//      debugger;
      var txnList = angular.copy($scope.mapping[row.PTYPE + 'TxnType']);
      var errorType = function () {
        row.txnTypeList = [];
        row.TXN_TYPE = '';
      };
      var orgAmt = Math.round(Number(row.PURCHASE_ORG_AMT) || 0);
      var orgInvAmt = Math.round((row.TRUST_CURR === 'TWD' ? Number(row.INVENTORY_TWD_AMT || row.NOW_AMT_TWD) : Number(row.INVENTORY_ORG_AMT || row.NOW_AMT)) || 0);

      switch (type) {
        case 'SN':
        case 'SI':
        case 'BND':
        	
          if (!isStock && (!$scope.hasIns || Math.round(row.NOW_AMT || 0) === 0 || (Math.round(row.NOW_AMT || 0) < Math.round(row.PURCHASE_ORG_AMT || 0)))) {
            // 申購
    	    row.txnTypeList = [txnList[0]];
            row.TXN_TYPE = '1';
          } else if (Number(row.PURCHASE_ORG_AMT) === 0) {
            // 贖回
    	    row.txnTypeList = [txnList[1]];  
            row.TXN_TYPE = '2';
          } else if (row.NOW_AMT > Math.round(row.PURCHASE_ORG_AMT)) {
            // 部分贖回
        	row.txnTypeList = [txnList[2]];
            row.TXN_TYPE = '3';
          } else {
        	  row.txnTypeList = [txnList[0], txnList[1], txnList[2]];
        	  row.TXN_TYPE = '';
          }
          break;
        case 'MFD':
        	
          // 申購、贖回、部分贖回、轉出、轉入
          if (!isStock && blockHasStock.stock) {
            // 申購、轉入
            row.txnTypeList = [txnList[0], txnList[4]];
            row.TXN_TYPE = ['F1', 'F5'].indexOf(row.TXN_TYPE) >= 0 ? row.TXN_TYPE : 'F1';
          } else if (!isStock && !blockHasStock.stock) {
            // 申購
//            row.txnTypeList = [txnList[0]];
//            row.TXN_TYPE = 'F1';
        	  row.txnTypeList = [txnList[0], txnList[4]];
              row.TXN_TYPE = ['F1', 'F5'].indexOf(row.TXN_TYPE) >= 0 ? row.TXN_TYPE : 'F1';
          } else if (orgAmt > orgInvAmt) {
            // 申購
//            row.txnTypeList = [txnList[0]];
//            row.TXN_TYPE = 'F1';
        	  row.txnTypeList = [txnList[0], txnList[4]];
              row.TXN_TYPE = ['F1', 'F5'].indexOf(row.TXN_TYPE) >= 0 ? row.TXN_TYPE : 'F1';
            // errorType();
          } else if (orgAmt === 0) {
            // 贖回、轉出
            row.txnTypeList = [txnList[1], txnList[3]];
            row.TXN_TYPE = ['F2', 'F4'].indexOf(row.TXN_TYPE) >= 0 ? row.TXN_TYPE : 'F2';
          } else if (orgAmt < orgInvAmt && orgInvAmt !== 0) {
            // 部分贖回、轉出
            row.txnTypeList = [txnList[2], txnList[3]];
            row.TXN_TYPE = ['F3', 'F4'].indexOf(row.TXN_TYPE) >= 0 ? row.TXN_TYPE : 'F3';
          } else {
            // 申購、轉入
            row.txnTypeList = [txnList[0], txnList[1], txnList[2], txnList[3]];
            row.TXN_TYPE = '';
          }
          break;
        case 'NANO':
          // 買進、賣出
           if (!isStock) {
             // 買進
             row.txnTypeList = [txnList[0]];
             row.TXN_TYPE = '1';
           } else if (orgAmt > orgInvAmt) {
             // 買進
             row.txnTypeList = [txnList[0]];
             row.TXN_TYPE = '1';
             // errorType();
           } else if (orgAmt < orgInvAmt) {
             // 賣出
             row.txnTypeList = [txnList[1]];
              row.TXN_TYPE = '2';
           } else {
             row.txnTypeList = txnList;
              row.TXN_TYPE = '';
           }
           break;
        case 'ETF':
          // 買進、賣出
          if (!isStock) {
            // 買進
            row.txnTypeList = [txnList[0]];
            row.TXN_TYPE = 'E1';
          } else if (orgAmt > orgInvAmt) {
            // 買進
            row.txnTypeList = [txnList[0]];
            row.TXN_TYPE = 'E1';
            // errorType();
          } else if (orgAmt < orgInvAmt) {
            // 賣出
            row.txnTypeList = [txnList[1]];
            row.TXN_TYPE = 'E2';
          } else {
            row.txnTypeList = txnList;
            row.TXN_TYPE = '';
          }
          break;
        case 'INS':
          // 申購、贖回
          if (!isStock) {
            // 申購
            row.txnTypeList = [txnList[0]];
            row.TXN_TYPE = 'I1';
          } else if (orgAmt > orgInvAmt) {
            // 申購
            row.txnTypeList = [txnList[0]];
            row.TXN_TYPE = 'I1';
            // errorType();
          } else if (orgAmt < orgInvAmt) {
            // 贖回
            row.txnTypeList = [txnList[1]];
            row.TXN_TYPE = 'I2';
          } else {
            row.txnTypeList = txnList;
            row.TXN_TYPE = '';
          }
          break;
      }
      row.txnTypeListLength = (row.txnTypeList || []).length;
    };

    // format List
    var formatDeposit = function (row) {
      row.PRD_ID = row.INV_PRD_TYPE_2 !== '3' ? (row.CURRENCY_TYPE || row.TRUST_CURR) : row.PRD_ID;
    };

    var formatFixed = function (row) {
      row.limitAmt = row.GEN_SUBS_MINI_AMT_FOR || 0;
      
      if(row.PTYPE == 'MFD' || row.PTYPE == 'NANO') {
    	  updateLimitAmt(row);
      }
    };

    var formatStock = function (row, isNew) {
      // 投資方式
      if ((row.PTYPE === 'MFD' || row.PTYPE === 'NANO') && (isNew || !row.INV_TYPE)) row.INV_TYPE = '1';
      updateLimitAmt(row);
      if (row.PTYPE === 'INS') {
        row.targetIDs = row.targetIDs || row.TARGETS;
      }
    };

    var formatList = (function () {
      // reformat list function mapping
      var formatMap = {
        deposit: formatDeposit,
        fixed: formatFixed,
        stock: formatStock,
      };
      return function (type, list, isNew) {
        return angular.copy(list).map(function (row) {
          row.SEQNO = row.SEQNO || undefined;
          row.PRD_CNAME = row.PRD_CNAME || row.PRD_NAME;
          row.PURCHASE_ORG_AMT = row.PURCHASE_ORG_AMT || 0;
          row.PURCHASE_TWD_AMT = row.PURCHASE_TWD_AMT || 0;
          row.CURRENCY_TYPE = row.CURRENCY_TYPE || row.TRUST_CURR;
          // init buyCur ins always 1
          if (row.PTYPE === 'INS')
            row.buyCur = '1';
          else
            row.buyCur = row.TRUST_CURR === 'TWD' || row.CURRENCY_TYPE === 'TWD' ? '2' : '1';
          
          ////奈米投的投資策略欄位放在INV_PRD_TYPE_2欄位,重抓庫存時才可以串到產品主檔
          if (row.PTYPE == 'NANO' && !row.INV_PRD_TYPE_2){
        	  row.INV_PRD_TYPE_2 = row.INS_TYPE;
          }
          // 要累進金額
          if (row.INV_PRD_TYPE === '2' && row.PRD_UNIT && (!row.isStock && !row.CERT_NBR)) {
//        	var tempPurchaseOrgAmt = row.PURCHASE_ORG_AMT;
//        	if(row.PURCHASE_ORG_AMT < row.GEN_SUBS_MINI_AMT_FOR) {
//        		tempPurchaseOrgAmt = Math.round(row.PURCHASE_ORG_AMT);
//        	}
            row.PURCHASE_ORG_AMT = row.PURCHASE_ORG_AMT - (row.PURCHASE_ORG_AMT % row.PRD_UNIT);
          }
          chgORG(row);
          // row.INV_PERCENT = row.PORTFOLIO_RATIO || row.INV_PERCENT || 0;
          row.INV_PERCENT = rePercent(row);

          formatMap[type](row, isNew);

          // set errCode
          row.errorCode = [];
          return row;
        });
      };
    })();
    
    var formatList_for_model = (function () {
        // reformat list function mapping
        var formatMap = {
          deposit: formatDeposit,
          fixed: formatFixed,
          stock: formatStock,
        };
        return function (type, list, isNew) {
          return angular.copy(list).map(function (row) {
            row.SEQNO = row.SEQNO || undefined;
            row.PRD_CNAME = row.PRD_CNAME || row.PRD_NAME;
            row.PURCHASE_ORG_AMT = row.TEMP_PURCHASE_ORG_AMT || 0;
            row.PURCHASE_TWD_AMT = row.TEMP_PURCHASE_TWD_AMT || 0;
            row.INV_PERCENT = row.TEMP_INV_PERCENT;
            row.CURRENCY_TYPE = row.CURRENCY_TYPE || row.TRUST_CURR;
            // init buyCur ins always 1
            if (row.PTYPE === 'INS') row.buyCur = '1';
            else row.buyCur = row.TRUST_CURR === 'TWD' || row.CURRENCY_TYPE === 'TWD' ? '2' : '1';

            // 要累進金額
            if (row.INV_PRD_TYPE === '2' && row.PRD_UNIT && (!row.isStock && !row.CERT_NBR)) {
              if(row.PURCHASE_ORG_AMT < row.GEN_SUBS_MINI_AMT_FOR) row.PURCHASE_ORG_AMT = Math.round(row.PURCHASE_ORG_AMT);	
              row.PURCHASE_ORG_AMT = row.PURCHASE_ORG_AMT - (row.PURCHASE_ORG_AMT % row.PRD_UNIT);
            }
            chgORG(row);
            row.INV_PERCENT = rePercent(row);

            formatMap[type](row, isNew);

            row.errorCode = [];
            return row;
          });
        };
      })();

    // merge callback
    var mergeCb = function (standard, obj) {
      debugger;
      standard.isStock = standard.isStock || obj.isStock;
      standard.SEQNO = standard.SEQNO || obj.SEQNO;
      if (standard.INV_PRD_TYPE_2 === '3' && standard.isStock) {
        standard.PURCHASE_TWD_AMT = Number((standard.isStock ? standard.PURCHASE_TWD_AMT : obj.PURCHASE_TWD_AMT) || 0);
        standard.PURCHASE_ORG_AMT = Number((standard.isStock ? standard.PURCHASE_ORG_AMT : obj.PURCHASE_ORG_AMT) || 0);
      } else {
        standard.PURCHASE_TWD_AMT = Number(standard.PURCHASE_TWD_AMT || 0) + Number(obj.PURCHASE_TWD_AMT || 0);
        standard.PURCHASE_ORG_AMT = Number(standard.PURCHASE_ORG_AMT || 0) + Number(obj.PURCHASE_ORG_AMT || 0);
      }
      standard.NOW_AMT_TWD = Number(standard.NOW_AMT_TWD || 0) + Number(obj.NOW_AMT_TWD || 0);
      standard.NOW_AMT = Number(standard.NOW_AMT || 0) + Number(obj.NOW_AMT || 0);
      standard.MAIN_PRD = (standard.MAIN_PRD === 'Y' || obj.MAIN_PRD === 'Y') ? 'Y' : 'N';
      standard.PRD_UNIT = standard.PRD_UNIT || obj.PRD_UNIT;
      standard.tooltipInfo = standard.tooltipInfo || obj.tooltipInfo;
      
      standard.SML_SUBS_MINI_AMT_FOR = obj.SML_SUBS_MINI_AMT_FOR || standard.SML_SUBS_MINI_AMT_FOR;
      standard.PRD_UNIT = obj.PRD_UNIT || standard.PRD_UNIT;
      // standard.INV_PERCENT = Number(standard.INV_PERCENT || 0) + Number(obj.INV_PERCENT || 0);
      chgORG(standard);
      standard.INV_PERCENT = rePercent(standard);
    };

    // 清除modelPortfolioList中空欄位
    var mpClearNull = function (list) {
      return angular.copy(list).filter(function (row) {
        row.isModel = true;
        return row.INV_PRD_TYPE === '1' && row.INV_PRD_TYPE_2 !== '3' ? row.CURRENCY_TYPE : row.PRD_ID;
      });
    };

    // 排序順序
    var sortListOf = function (type, list) {
      var sortkey = {
        deposit: 'INV_PRD_TYPE_2',
        fixed: 'PTYPE',
        stock: 'PTYPE'
      };
      var key = sortkey[type];
      list.forEach(function(row){
    	  if(!row.INV_TYPE) {
    		  row.INV_TYPE = '1';
    	  }
      });
      return list.sort(function (a, b) {
        return sortOrder[type][a[key]] * ($scope.hasIns ? sortOrder.isStock[Boolean(a.isStock)] : sortOrder.isModel[Boolean(a.isModel)]) -
          sortOrder[type][b[key]] * ($scope.hasIns ? sortOrder.isStock[Boolean(b.isStock)] : sortOrder.isModel[Boolean(b.isModel)]);
      });
    };
    
    var fpsChart0 = FPSChartUtils($scope.mapping0, canvasJsConfig);
    
    var fpsChart = FPSChartUtils($scope.mapping, canvasJsConfig);
    
	var beforeDoughut = function (chartName, dataPoints, tableRoot) {
	    var chartData = {
	      colorSet: 'tri',
	      backgroundColor: 'transparent',
	      height: '240',
	      width: '320',
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
	    tableRoot[chartName] = fpsChart.setChartTable.call(this, charts[chartName]);
	    tableRoot[chartName].forEach(function (item, index) {
	      item.advice = '--';
	      // 建議
	      switch (index) {
	        case 0: // fixed
	          item.adviceMsg = '--';
	          break;
	        case 1: // stock
	          if (item.value < $scope.recommend[$scope.mapping.keys[index]]) {
	            item.down = true;
	            item.adviceMsg = '可維持或適度增加';
	          } else {
	        	item.adviceMsg = '--';
	          }
	          break;
	      }
	    });
	  };

	 var afterDoughut = function (chartName, dataPoints, tableRoot) {
	    var chartData = {
	      colorSet: 'tri',
	      backgroundColor: 'transparent',
	      height: '240',
	      width: '320',
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
	    tableRoot[chartName] = fpsChart.setChartTable.call(this, charts[chartName]);
	    
	    tableRoot[chartName].forEach(function (item, index) {
	      item.amt = $scope[$scope.mapping.keys[index] + 'Amt'];
	      
	      switch (index) {
	        case 0: // fixed
	        	item.advice = '--';
	        	item.beforeValue = $scope.STEP2VO.PORTFOLIO2_AMT_AFTER_ORIGIN;
	          	item.beforePct = $scope.STEP2VO.PORTFOLIO2_RATIO_AFTER_ORIGIN;
	          	item.adviceMsg = '--';
	          	break;
	        case 1: // stock
	        	item.advice = (index < 2 ? '≦' : '') + $scope.stockRiskLevel[$scope.custInfo.KYC_LEVEL] + '%';
	        	item.beforeValue = $scope.STEP2VO.PORTFOLIO3_AMT_AFTER_ORIGIN;
	        	item.beforePct = $scope.STEP2VO.PORTFOLIO3_RATIO_AFTER_ORIGIN;
	        	if ($scope.STEP2VO.PORTFOLIO3_RATIO_AFTER < $scope.stockRiskLevel[$scope.custInfo.KYC_LEVEL]) {
		           item.down = true;
		           item.adviceMsg = '可維持或適度增加';
		        } else if ($scope.STEP2VO.PORTFOLIO3_RATIO_AFTER > $scope.stockRiskLevel[$scope.custInfo.KYC_LEVEL]){
		           item.adviceMsg = '降低部位';
		        } else {
		        	item.advicMsg = '--';
		        }
	        	break;
	      }
	    });
	 };
	 
	 var drawDoughnut = function () {
	      $timeout(function () {
//	        if ($scope.hasIns) {
	          $scope.before = {};
	          $scope.before.fixedPct = $scope.STEP2VO.PORTFOLIO2_RATIO_AFTER_ORIGIN;
	          $scope.before.stockPct = $scope.STEP2VO.PORTFOLIO3_RATIO_AFTER_ORIGIN;
//	          $scope.before.otherPct = 0;
	          
	          $scope.after = {};
	          $scope.STEP2VO.PORTFOLIO2_RATIO_AFTER = Math.round($scope.fixedAmt/($scope.fixedAmt + $scope.stockAmt)*100);
	          $scope.STEP2VO.PORTFOLIO3_RATIO_AFTER = Math.round($scope.stockAmt/($scope.fixedAmt + $scope.stockAmt)*100);
	          $scope.after.fixedPct = $scope.STEP2VO.PORTFOLIO2_RATIO_AFTER;
	          $scope.after.stockPct = $scope.STEP2VO.PORTFOLIO3_RATIO_AFTER;
	        	  
	          beforeDoughut('beforeChart', fpsChart0.amountDataPoints($scope.before), $scope.chartTables);
	          afterDoughut('afterChart', fpsChart.amountDataPoints($scope.after), $scope.chartTables);
	          
	          var t = Math.round($scope.fixedTempAmt + $scope.stockTempAmt);
	          $scope.chartTables.afterChart.push({label:'尚未規劃金額', amt:(($scope.STEP1VO.PLAN_AMT - t))});
//	        } 
	      });
	 };

    /* main progress */
    param()
      .then(function () {
        inquire().then(function () {
            // chgStep(pathMap.next);
    	    drawDoughnut();	
          });
      });
  }
);
