/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS240Controller',
  function ($scope, $controller, ngDialog, $timeout, $q, fps200Service, FPSUtilsService) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPS240Controller';

    /* parameter */
    $scope.kycInvalid = $scope.connector('get', 'kycInvalid') !== false ? true : false;
    var fps200 = fps200Service;
    var modelPortfolioList = [];
    $scope.recoVolatility = 0;

    var minPriceAmt = 0;
    var price = 0; // 存款部位 A1 + B - C -D

    var sortOrder = {
      deposit: {
        1: 2,
        3: 1,
        2: 3
      },
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
      transaction: {
        MFD: 1,
        ETF: 2,
        INS: 3,
        BND: 4,
        SN: 5,
        SI: 6
      },
      fundName: {
        股票型: 1,
        債券型: 2,
        平衡型: 3,
        其他: 4
      }
    };

    var pathMap = {
      printPreview: './assets/txn/FPS240/FPS240PrintPreview.html',
      product: './assets/txn/FPS/FPSProd.html',
      stepConfirm: './assets/txn/FPS240/FPS240Confirm.html',
      warnings: './assets/txn/FPS240/FPS240Warning.html',
      SOT: './assets/txn/FPS/FPS_SOT.html',
      next: '',
      prev: 'STEP3',
      SOTtype: {
        MFD: {
          1: { // 申購
            1: 'SOT110', 	// 單筆
            2: 'SOT120' 	// 定額
          },
          F1: { // 申購
            1: 'SOT110', 	// 單筆
            2: 'SOT120'		// 定額
          },
          F2: 'SOT130', 	// 部分贖回
          F3: 'SOT130', 	// 贖回
          F4: 'SOT140', 	// 轉出
          F5: 'SOT140' 		// 轉入 無連結(同轉出)
        },
        ETF: {
          1: 'SOT210', 		// 申購,
          E1: 'SOT210', 	// 買進
          E2: 'SOT220' 		// 賣出
        },
        INS: {
          1: 'IOT110', 		// 申購,
          I1: 'IOT110', 	// 申購,
          // I2: '' 		// 贖回 無連結
        },
        BND: {
          1: 'SOT310', 		// 申購
          2: 'SOT320', 		// 贖回
        },
        SI: {
          1: 'SOT410', 		// 申購
          2: 'SOT420', 		// 贖回
        },
        SN: {
          1: 'SOT510', 		// 申購
          2: 'SOT520', 		// 贖回
          3: 'SOT520' 		// 部分贖回
        }
      },
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

    // 贖回 最低贖回金額 轉出 賣出
    var RDMtxnType = ['F2', 'F3', 'F4', 'E2', 'I2', '2', '3'];

    $scope.recommend = {
      deposit: 0,
      fixed: 0,
      stock: 0
    };
    
    $scope.stockRiskLevel = {};
    
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
      statusMap: {
        '-1': 'PRINT_REJECT',
        '1': 'PRINT_THINK', //PRINT_ORDER
        '0': 'PRINT_THINK'
      },

      trendPath: {
        H: 'assets/images/ic-unbiased.svg',
        B: 'assets/images/ic-rise.svg',
        S: 'assets/images/ic-decline.svg'
      },

      keys: [
        'fixed',
        'stock'
      ],

      keysZhTw: [
         '類債券商品',
         '類股票商品',
         '尚未規劃金額'
      ],

      code: {
        'fixed': '2',
        'stock': '3'
      },

      fullProdType: {
        FUND: '基金投資注意事項',
        ETF: '海外ETF投資注意事項',
        BND: '海外債商品風險宣告聲明文字',
        SN: '境外結構型商品(SN)風險宣告聲明文字',
        SI: '組合式商品(SI)風險宣告聲明文字',
        'INS-S': '儲蓄型保險風險宣告聲明文字',
        INSI: '連結全委帳戶之投資型保險投資注意事項',
        NANO: '奈米投風險宣告聲明文字',
      },
  	  NANOTxnType : [
  	    {DATA:1,LABEL:'申購'},
  	    {DATA:2,LABEL:'贖回'}
  	  ],
  	  NANOInvType : [
  	    {DATA:1,LABEL:'單筆'},
  	    {DATA:2,LABEL:'定額'},
  	    {DATA:3,LABEL:'單筆+定額'}
  	  ]
    };

    // FPSChartUtils
    var fpsChart = FPSChartUtils($scope.mapping, canvasJsConfig);

    var rateMap = {};
    var charts = {
      amountChart: null,
      stockChart: null,
      beforeChart: null,
      afterChart: null
    };

    $scope.chartTables = {
      amountChart: [],
      stockChart: [],
      beforeChart: [],
      afterChart: []
    };
    $scope.today = (new Date()).yyyyMMdd('.');
    $scope.todayPrint = (new Date()).yyyyMMdd('/');
    

    // print seq once per time
    $scope.connector('set', 'FPS240PrintSEQ', -1);

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

      fps200.getXmlParam([
        'FPS.CUST_RISK_ATR_2',
        'FPS.BUY_CURRENCY',
        'FPS.TXN_TYPE',
        'FPS.INV_TYPE',
        'FPS.PROD_TYPE',
        'FPS.INV_PRD_TYPE',
        'FPS.INV_PRD_TYPE_2',
        'FPS.CURRENCY'
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
              row.type = row.PRD_TYPE.replace(/\d/g, '') || row.PRD_TYPE;
              var tmp = (row.FONT || '').split('#');
              row.COLOR = tmp.length > 1 ? '#' + tmp[1] : undefined;
              row.FONT = (row.FONT).split('#')[0];

              // 篩選注意組合
              if (noticeType.indexOf(row.type) < 0) {
            	noticeType.push(row.type);            		
              }
              // 組注意資料列
              if (notice[row.PRD_TYPE] === undefined) {
                notice[row.PRD_TYPE] = [row];
                noticeFlag[row.type] = false;
              } else {
                notice[row.PRD_TYPE].push(row);
                noticeFlag[row.type] = false;
              }
            });
            
            var noticeTypeAll = [];
            var noticeFlagAll = {};
        	if($scope.STEP1VO.SIGN_AGMT_YN === 'N') { //沒有推介同意則全部顯示
        		noticeTypeAll.push('FUND');
        		noticeTypeAll.push('ETF');
        		noticeTypeAll.push('BND');
        		noticeTypeAll.push('SI');
        		noticeTypeAll.push('SN');
        		noticeTypeAll.push('NANO'); //新增奈米投
        		noticeFlagAll = {
    				'FUND' : true,
    				'ETF' : true,
    				'BND' : true,
    				'SI' : true,
    				'SN' : true,
    				'NANO' : true
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
    $scope.STEP2VO = $scope.connector('get', 'STEP2VO');
    $scope.custInfo = $scope.connector('get', 'custInfo');
    var planID = $scope.connector('get', 'planID') || null;
    var custID = $scope.connector('get', 'custID') || null;

    /* init */
    var init = function () {

    };

    /* main function */
    var inquire = function () {
      var defer = $q.defer();
      $scope.sendRecv('FPS240', 'inquire', 'com.systex.jbranch.app.server.fps.fps240.FPS240InputVO', {
          planID: planID,
          custID: custID,
          riskType: $scope.STEP1VO.CUST_RISK,
          OBU: $scope.custInfo.OBU_FLAG,
          isPro: $scope.custInfo.CUST_PRO_FLAG,
          commRsYn: $scope.STEP1VO.SIGN_AGMT_YN
        },
        function (tota, isError) {
          if (!isError) {
            $scope.headList = {};
            $scope.paramList = tota[0].body.outputList;

            $scope.recoVolatility = tota[0].body.recoVolatility || 0;
            // 匯率
            $scope.fxRateList = tota[0].body.fxRateList;
            // 年度報酬
            $scope.yearRateList = tota[0].body.yearRateList;
            // 歷史年度平均報酬率
            $scope.historyYRate = tota[0].body.historyYRate === null ? '--' : ((Number(tota[0].body.historyYRate) * 100) || 0).toFixed(2);
            // 波波率
            $scope.volatility = tota[0].body.volatility === null ? '--' : ((Number(tota[0].body.volatility) * 100) || 0).toFixed(2);
            // 初始模組
            modelPortfolioList = tota[0].body.initModelPortfolioList || [];
            // 滿一年波波率
            $scope.fullyearVolatility = tota[0].body.fullYearVolatility === null ? '--' : ((Number(tota[0].body.fullYearVolatility) * 100) || 0).toFixed(2);

            // 建議上限
            $scope.stockRiskLevel = tota[0].body.stockRiskLevel;
            
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
            // 有無投保
            $scope.hasIns = tota[0].body.hasInvest;
            //有無推介
            $scope.commRsYn = $scope.STEP1VO.SIGN_AGMT_YN;
            // 商品
            $scope.manualList = tota[0].body.manualList;
            // 圖片
            $scope.rptPicture = tota[0].body.rptPicture;

            // plan date
            $scope.planDate = (new Date(tota[0].body.planDate)).yyyyMMdd('/');
            console.log($scope.planDate);
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
            
	         // 債券績效
	        $scope.DEBTPerformanceList = [];
	        tota[0].body.debtPerformanceList.map(function (row) {
	            row.DATE_OF_MATURITY = row.DATE_OF_MATURITY ? Math.round(row.DATE_OF_MATURITY * 100) / 100 : '--';
	            row.FREQUENCY_OF_INTEST_PAY = row.FREQUENCY_OF_INTEST_PAY ? row.FREQUENCY_OF_INTEST_PAY : '--';
	            row.BUY_PRICE = row.BUY_PRICE ? Math.round(row.BUY_PRICE * 100) / 100 : '--';
	            row.LASTRATE = row.LASTRATE ? Math.round(row.LASTRATE * 100) / 100 : '--';
//	            debugger
	            if($scope.hasIns) {
	            	$scope.DEBTPerformanceList.push(row);
	            } else {
	            	if(row.PRD_TYPE !== 'SN' && row.PRD_TYPE !== 'SI') {
	            		$scope.DEBTPerformanceList.push(row);
	            	}
	            }
	        });

//	        debugger
            // 前言
            var briefList = tota[0].body.briefList || [];
            $scope.brief = briefList.length > 0 ? briefList[0].CONTENT : '';

            // cust head data
            if (tota[0].body.headList && tota[0].body.headList.length > 0) {
              $scope.headList = tota[0].body.headList[0];
              // fps210
              $scope.headList.deposit = $scope.headList.DEPOSIT_AMT || 0;
              // hasIns
              $scope.headList.annuityProd = $scope.headList.ANNUITY_INS_AMT || 0;
              $scope.headList.fixedProd = $scope.headList.FIXED_INCOME_AMT || 0;
              $scope.headList.fundProd = $scope.headList.FUND_AMT || 0;
              $scope.headList.liquidAmt = $scope.headList.annuityProd + $scope.headList.fixedProd + $scope.headList.fundProd + $scope.headList.deposit;

              $scope.headList.sowAmt = $scope.headList.SOW_AMT || 0;
              // INS_YEAR_AMT_1(new) INS_POLICY_AMT(inv)
              $scope.headList.insPolicyAmt = ($scope.headList.INS_POLICY_AMT || $scope.headList.INS_YEAR_AMT_1) || 0;
              // INS_YEAR_AMT_2(new) INS_SAV_AMT(inv)
              $scope.headList.insSavAmt = ($scope.headList.INS_SAV_AMT || $scope.headList.INS_YEAR_AMT_2) || 0;
              $scope.headList.ins = $scope.headList.INS_AMT || 0;
              $scope.headList.cash = $scope.headList.CASH_YEAR_AMT || 0;
              $scope.headList.price = (Number($scope.headList.liquidAmt) + Number($scope.headList.sowAmt)) || 0;
              
              // 前一個營業日
              $scope.connector('set', 'fpsPrevBusiDay', tota[0].body.prevBusiDay);

              // fps220
              $scope.headList.planAmt = $scope.headList.PLAN_AMT;
              $scope.headList.depositAmt = $scope.headList.ANNUITY_INS_AMT + $scope.headList.DEPOSIT_AMT + $scope.headList.SOW_AMT -
              $scope.headList.CASH_YEAR_AMT - $scope.headList.INS_SAV_AMT - $scope.headList.INS_POLICY_AMT;
              $scope.headList.fixedAmt = $scope.STEP2VO.PORTFOLIO2_AMT_AFTER_ORIGIN;
              $scope.headList.stockAmt = $scope.STEP2VO.PORTFOLIO3_AMT_AFTER_ORIGIN;
              $scope.headList.depositPct = Math.round($scope.headList.depositAmt / $scope.headList.planAmt * 100);
              $scope.headList.fixedPct = $scope.STEP2VO.PORTFOLIO2_RATIO_AFTER_ORIGIN
              $scope.headList.stockPct = $scope.STEP2VO.PORTFOLIO3_RATIO_AFTER_ORIGIN
              
              // 調整的項目
              // 額外拆的
              $scope.headList.deposit = $scope.headList.DEPOSIT_AMT || 0;
              $scope.headList.mfdProd = $scope.headList.MFD_PROD_AMT || 0;
              $scope.headList.etfProd = $scope.headList.ETF_PROD_AMT || 0;
              $scope.headList.insProd = $scope.headList.INS_PROD_AMT || 0;
              $scope.headList.bondProd = $scope.headList.BOND_PROD_AMT || 0;
              $scope.headList.snProd = $scope.headList.SN_PROD_AMT || 0;
              $scope.headList.siProd = $scope.headList.SI_PROD_AMT || 0;
              //2020-2-3 by Jacky新增奈米投
              $scope.headList.nanoProd = $scope.headList.NANO_PROD_AMT || 0;
              
              $scope.headList.liquidAmt = $scope.headList.deposit + $scope.headList.mfdProd + $scope.headList.etfProd +
              		$scope.headList.insProd + $scope.headList.bondProd + $scope.headList.snProd + $scope.headList.siProd + $scope.headList.nanoProd;
              $scope.headList.sowAmt = $scope.headList.SOW_AMT || 0;
              $scope.headList.price = (Number($scope.headList.liquidAmt) + Number($scope.headList.sowAmt)) || 0;
              $scope.headList.cash = $scope.headList.CASH_YEAR_AMT || 0;
              $scope.headList.needExpenses = $scope.headList.LIVE_YEAR_AMT || 0;
              $scope.headList.loanExpenses = $scope.headList.LOAN_EXPENSES || 0;
              $scope.headList.otherExpenses = $scope.headList.OTHER_EXPENSES || 0;
              $scope.headList.ins = $scope.headList.INS_AMT || 0;
              $scope.headList.insPolicyAmt = ($scope.headList.INS_POLICY_AMT || $scope.headList.INS_YEAR_AMT_1) || 0;
              $scope.headList.insSavAmt = ($scope.headList.INS_SAV_AMT || $scope.headList.INS_YEAR_AMT_2) || 0;
              $scope.headList.cashPrepare = $scope.headList.CASH_PREPARE || 0;

              // hasIns
              $scope.stockVolatility = tota[0].body.stockVolatility === null ? '--' : ((Number(tota[0].body.stockVolatility) * 100) || 0).toFixed(2);

              price = $scope.headList.DEPOSIT_AMT + $scope.headList.SOW_AMT -
                $scope.headList.CASH_YEAR_AMT - $scope.headList.INS_SAV_AMT - $scope.headList.INS_POLICY_AMT;
            }

            // recommend model
            modelPortfolioList.forEach(function (row) {
             if ('B' === row.STOCK_BOND_TYPE) {
                $scope.recommend.fixed += Number(row.INV_PERCENT);
              } else if ('S' === row.STOCK_BOND_TYPE) {
                $scope.recommend.stock += Number(row.INV_PERCENT);
              }
            });

            // filter list
            var transaction = [];
            $scope.mapping.keys.forEach(function (key) {
              $scope[key + 'List'] = sortListOf(key, formatList(key, filterList($scope.paramList, 'INV_PRD_TYPE', $scope.mapping.code[key])));
              transaction = transaction.concat($scope[key + 'List']);
              
              $scope[key + 'List'].forEach(function(row) {
            	  row.PURCHASE_ORG_AMT = Math.round(row.PURCHASE_ORG_AMT);
            	  row.PURCHASE_TWD_AMT = Math.round(row.PURCHASE_TWD_AMT);
              });
              
            });
            // transaction list
            
            $scope.transactionList = transaction.filter(function (row) {
              //奈米投借用INV_PRD_TYPE_2存放投資策略欄位
//              var isTransaction = (row.INV_PRD_TYPE_2 !== '2' && row.INV_PRD_TYPE_2 !== '1') &&
//                (row.TRUST_CURR === 'TWD' ? (row.NOW_AMT_TWD - row.PURCHASE_TWD_AMT !== 0) : (row.NOW_AMT - row.PURCHASE_ORG_AMT !== 0));
              //改寫表示方式, 原判斷式INV_PRD_TYPE_2 不可為1,2....且依幣別判斷購買金額要>0
            	debugger;
              var isTransaction = false;
              if(row.PTYPE == 'NANO'){
            	  if(row.TRUST_CURR === 'TWD')
            		  isTransaction = (row.NOW_AMT_TWD - row.PURCHASE_TWD_AMT !== 0)? true : false;
            	  else
            		  isTransaction = (row.NOW_AMT - row.PURCHASE_ORG_AMT !== 0)? true : false;
              }else{
            	  if(row.INV_PRD_TYPE_2 !== '2' && row.INV_PRD_TYPE_2 !== '1'){
            		  if(row.TRUST_CURR === 'TWD')
            			  isTransaction = (row.NOW_AMT_TWD - row.PURCHASE_TWD_AMT !== 0)? true : false;
                	  else
                		  isTransaction = (row.NOW_AMT - row.PURCHASE_ORG_AMT !== 0)? true : false;
            	  }
              }
              
              if (isTransaction) {
                var orgAmt = Number(row.PURCHASE_ORG_AMT) || 0;
                var orgInvAmt = (row.TRUST_CURR === 'TWD' ? Number(row.INVENTORY_TWD_AMT || row.NOW_AMT_TWD) : Number(row.INVENTORY_ORG_AMT || row.NOW_AMT)) || 0;

                if (row.NOW_AMT) {
                  row.transaction = RDMtxnType.indexOf(row.TXN_TYPE) >= 0 ?
                    (orgInvAmt - orgAmt) > (Number(row.RDM_ORG_AMT_ORDER || 0) || 0) :
                    (orgAmt - orgInvAmt) > (Number(row.PURCHASE_ORG_AMT_ORDER || 0) || 0);
                } else {
                  row.transaction = orgAmt > (Number(row.PURCHASE_ORG_AMT_ORDER || 0) || 0);
                }
                // count twd
                var cur = '';
//                if (row.INV_PRD_TYPE === '3') {
                  switch (row.buyCur) {
                    case '1':
                      cur = row.TRUST_CURR || row.CURRENCY_TYPE;
                      break;
                    case '2':
                      cur = 'TWD';
                      break;
                    default:
                      cur = row.TRUST_CURR || row.CURRENCY_TYPE;
                      row.buyCur = (!row.buyCur && (row.TRUST_CURR === 'TWD' || row.CURRENCY_TYPE === 'TWD')) ? '2':'1';
                  }
//                } else {
//                  cur = row.TRUST_CURR || row.CURRENCY_TYPE || '';
//                }
                var rate = rateMap[cur] && rateMap[cur].buy ? rateMap[cur].buy : 1; // with no wrong
                // 顯示申購贖回金額
                row.TRANSACT_ORG_AMT = row.NOW_AMT ? orgInvAmt - orgAmt : orgAmt;
                row.TRANSACT_TWD_AMT = Math.floor((Number(row.TRANSACT_ORG_AMT) || 0) * rate);
                row.tooltipInfo = certificateTitle[row.PTYPE] !== undefined ?
                  (certificateTitle[row.PTYPE] ? (certificateTitle[row.PTYPE] + ': ' + row.CERT_NBR) : '') :
                  '';
              }
              return isTransaction;
            }).sort(function (a, b) {
              return sortOrder.transaction[a.PTYPE] - sortOrder.transaction[b.PTYPE];
            });
            console.log($scope.transactionList);

            // set notice flag
            var pTypeMap = {
              MFD: 'FUND',
              ETF: 'ETF',
              BND: 'BND',
              SI: 'SI',
              SN: 'SN',
              S: 'INS-S',
              I: 'INSI',
              NANO: 'NANO'
            };
            $scope.transactionList.forEach(function (row) {
              var tmp = pTypeMap[row.PTYPE];
              if (row.PTYPE === 'INS') {
                tmp = row.INV_PRD_TYPE === '3' ? pTypeMap.I : pTypeMap.S;
              }
              $scope.mapping.noticeFlags[tmp] = true;
            });

            console.log($scope.mapping.noticeTypes);

            $scope.reTotal();

            // 小財神
            $scope.warning = [];
//            if ($scope.recommend.deposit < $scope.depositPct)
//              $scope.warning.push('存款+儲蓄型保險部位' + $scope.depositPct + '%，超過建議上限' + Math.round($scope.recommend.deposit) + '%');
            var tempStockPct = Math.round($scope.stockAmt/($scope.fixedAmt + $scope.stockAmt)*100);
            if ($scope.stockRiskLevel[$scope.custInfo.KYC_LEVEL]< tempStockPct) {
            	$scope.warning.push('類股票商品部位' + tempStockPct + '%，超過建議上限' + Math.round($scope.stockRiskLevel[$scope.custInfo.KYC_LEVEL]) + '%，提醒您，有可能於次月落入類股票商品超標警示名單中');            	
            }

            if ($scope.warning.length > 0) {
              $scope.go('warning');
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

    // 去那兒
    $scope.go = function (where) {
    	debugger
      switch (where) {
        case 'prev':
          chgStep(pathMap[where]);
          break;
        case 'print':
          openDialog(pathMap.printPreview, {
            planDate: $scope.planDate,
            brief: $scope.brief,
            custInfo: $scope.custInfo,
            rptPicture: $scope.rptPicture,
            headList: $scope.headList,
            depositAmt: $scope.depositAmt,
            depositList: $scope.depositList,
            fixedNowPct: $scope.fixedList.reduce(function (a, b) {
              return a + (Number(b.NOW_INV_PERCENT) || 0);
            }, 0),
            fixedPct: Math.round($scope.fixedAmt/($scope.fixedAmt + $scope.stockAmt)*100),
            fixedAmt: $scope.fixedAmt,
            fixedList: $scope.fixedList,
            stockNowPct: $scope.stockList.reduce(function (a, b) {
              return a + (Number(b.NOW_INV_PERCENT) || 0);
            }, 0),
            stockPct: Math.round($scope.stockAmt/($scope.fixedAmt + $scope.stockAmt)*100),
            stockAmt: $scope.stockAmt,
            stockList: $scope.stockList,
            historyYRate: $scope.historyYRate,
            yearRateList: $scope.yearRateList,
            stockVolatility: $scope.stockVolatility,
            volatility: $scope.fullyearVolatility,
            DEBTPerformanceList: $scope.DEBTPerformanceList,
            MFDPerformanceList: $scope.MFDPerformanceList,
            transactionList: $scope.transactionList,
            manualList: $scope.manualList,
            mapping: $scope.mapping,
            fpsChart: fpsChart,
            hasIns: $scope.hasIns,
          }, 'print');
          break;
        case 'next':
          openDialog(pathMap.stepConfirm, $scope.headList.PLAN_STATUS, 'confirm');
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
          $scope.custID = custID;
          $scope.planID = planID;
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

    var popCbFn = {
      confirm: function (value) {
        var action = value.action;
        $scope.sendRecv('FPS240', 'save', 'com.systex.jbranch.app.server.fps.fps240.FPS240InputVO', {
            planID: planID,
            action: 'update',
            planStatus: $scope.mapping.statusMap[action]
          },
          function (tota, isError) {
            if (!isError && action === '1') {
              $scope.headList.PLAN_STATUS = $scope.mapping.statusMap[action];
              return true;
            }
            $scope.connector('set', 'STEP', 'history');
            return false;
          });
      }
    };

    // 跳轉到SOT
    $scope.chgUrl = function (row) {
      openDialog(pathMap.SOT, {
        where: row.PTYPE === 'MFD' && (row.TXN_TYPE === '1' || row.TXN_TYPE === 'F1') ?
          pathMap.SOTtype[row.PTYPE][row.TXN_TYPE][row.INV_TYPE] : pathMap.SOTtype[row.PTYPE][row.TXN_TYPE],
        transact: row
      }, 'SOT');
    };

    var chgStep = function (step) {
      $scope.connector('set', 'STEP', step);
    };

    /* sub function */
    // draw chart
    var drawDoughnut = function () {
      $timeout(function () {
    	  $scope.before = {};
          $scope.before.fixedPct = $scope.STEP2VO.PORTFOLIO2_RATIO_AFTER_ORIGIN;
          $scope.before.stockPct = $scope.STEP2VO.PORTFOLIO3_RATIO_AFTER_ORIGIN;
          
          $scope.after = {};
          $scope.after.fixedPct = Math.round($scope.fixedAmt/($scope.fixedAmt + $scope.stockAmt)*100);
          $scope.after.stockPct = Math.round($scope.stockAmt/($scope.fixedAmt + $scope.stockAmt)*100);
          
    	  beforeDoughut('beforeChart', fpsChart.amountDataPoints($scope.before), $scope.chartTables);
    	  afterDoughut('afterChart', fpsChart.amountDataPoints($scope.after), $scope.chartTables);
    	  
    	  $scope.chartTables.afterChart.push({label:'尚未規劃金額', amt:(($scope.STEP1VO.PLAN_AMT-($scope.fixedAmt + $scope.stockAmt)))});
//        if ($scope.hasIns) {
//          beforeDoughut('beforeChart', fpsChart.amountDataPoints($scope.headList), $scope.chartTables);
//          afterDoughut('afterChart', fpsChart.amountDataPoints($scope), $scope.chartTables);
//        } else {
//          amountDought('amountChart', fpsChart.amountDataPoints($scope), $scope.chartTables);
//          stockDought('stockChart', fpsChart.stockDataPoints($scope.stockList, 'PURCHASE_TWD_AMT'), $scope.chartTables);
//        }
      });
    };

    var amountDought = function (chartName, dataPoints, tableRoot) {
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
      tableRoot[chartName] = fpsChart.setChartTable.call(this, charts[chartName]);
    };

    var stockDought = function (chartName, dataPoints, tableRoot) {
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

      charts[chartName] = null;
      charts[chartName] = new CanvasJS.Chart(chartName, chartData);
      charts[chartName].render();
      tableRoot[chartName] = fpsChart.setChartTable.call(this, charts[chartName]);
    };

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
        item.amt = $scope.headList[$scope.mapping.keys[index] + 'Amt'];
        // 建議
        switch (index) {
          case 0: // deposit
            if (item.value > $scope.recommend[$scope.mapping.keys[index]]) {
              item.down = true;
              if (price > minPriceAmt * 10000) {
                item.advice = '降低存款部位';
              }
            }
            break;
          case 1: // fixed
            if (item.value > $scope.recommend[$scope.mapping.keys[index]]) {
              item.down = true;
              item.advice = '有獲利可降低部位';
            }
            break;
          case 2: // stock
            if (item.value < $scope.recommend[$scope.mapping.keys[index]]) {
              item.down = true;
              item.advice = '增加部位';
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

    // 重新計算總額和tab比例
    $scope.reTotal = function (type) {
      // console.log(type);
      $scope.depositAmt = Math.round(FPSUtilsService.sumList($scope.depositList, 'PURCHASE_TWD_AMT'));
      $scope.fixedAmt = Math.round(FPSUtilsService.sumList($scope.fixedList, 'PURCHASE_TWD_AMT'));
      $scope.stockAmt = Math.round(FPSUtilsService.sumList($scope.stockList, 'PURCHASE_TWD_AMT'));
      $scope.depositPct = Math.round(($scope.depositAmt / $scope.STEP1VO.PLAN_AMT) * 100) || 0;
      $scope.fixedPct = Math.round(($scope.fixedAmt / $scope.STEP1VO.PLAN_AMT) * 100) || 0;
      $scope.stockPct = Math.round(($scope.stockAmt / $scope.STEP1VO.PLAN_AMT) * 100) || 0;

      //調整後金額
      $scope.AdjDepositAmt = Math.round(FPSUtilsService.sumList($scope.depositList, 'NOW_AMT_TWD'));
      $scope.AdjFixedAmt = Math.round(FPSUtilsService.sumList($scope.fixedList, 'NOW_AMT_TWD'));
      $scope.AdjStockAmt = Math.round(FPSUtilsService.sumList($scope.stockList, 'NOW_AMT_TWD'));
      $scope.AdjDepositPct = Math.round(($scope.AdjDepositAmt / $scope.STEP1VO.PLAN_AMT) * 100) || 0;
      $scope.AdjFixedPct = Math.round(($scope.AdjFixedAmt / $scope.STEP1VO.PLAN_AMT) * 100) || 0;
      $scope.AdjSockPct = Math.round(($scope.AdjStockAmt / $scope.STEP1VO.PLAN_AMT) * 100) || 0;

      // count sum
      $scope.totalAmt = $scope.depositAmt + $scope.fixedAmt + $scope.stockAmt;
      // count pct
      $scope.leftPct = Math.round((($scope.STEP1VO.PLAN_AMT - $scope.totalAmt) / $scope.STEP1VO.PLAN_AMT) * 100) || 0;
    };

    /* helping function */
    var filterList = function (list, key, val) {
      return angular.copy(list).filter(function (row) {
        return (key ? row[key] : row) === val;
      });
    };

    var formatDeposit = function (row) {
      row.PRD_ID = row.INV_PRD_TYPE_2 !== '3' ? (row.CURRENCY_TYPE || row.TRUST_CURR) : row.PRD_ID;
      //交易指示
      // 新商品
      // row.TXN_TYPE = '1';
      row.txnTypeList = $scope.mapping.TxnType;
    };

    var formatFixed = function (row) {
      //交易指示
      if (!$scope.hasIns || row.NOW_AMT === 0 || (row.NOW_AMT < row.PURCHASE_ORG_AMT)) {
        // 申購
        // row.TXN_TYPE = '1';
        row.txnTypeList = $scope.mapping.TxnType;
      } else if (row.PURCHASE_ORG_AMT === 0) {
        // 贖回
        // row.TXN_TYPE = '2';
        row.txnTypeList = $scope.mapping.fixedTxnType;
      } else if (row.NOW_AMT > row.PURCHASE_ORG_AMT) {
        // 部分贖回
        // row.TXN_TYPE = '3';
        row.txnTypeList = $scope.mapping.fixedTxnType;
      }
    	
      if(row.PTYPE === 'MFD' || row.PTYPE === 'ETF') {
    	  formatStock(row);
      }
      
    };

    var formatStock = function (row) {
      // 交易指示
      debugger;
      if (!$scope.hasIns || !row.isStock) {
        // row.TXN_TYPE = '1';
    	  row.txnTypeList = $scope.mapping[row.PTYPE + 'TxnType'];
//        row.txnTypeList = $scope.mapping.TxnType;
      } else if (row.isStock) {
        // 庫存
        row.txnTypeList = $scope.mapping[row.PTYPE + 'TxnType'];
      }
    };

    var formatList = (function () {
      // reformat list function mapping
      var formatMap = {
        deposit: formatDeposit,
        fixed: formatFixed,
        stock: formatStock,
      };
      return function (type, list) {
        return angular.copy(list).map(function (row) {
          row.action = !row.isIns ? row.SEQNO ? 'update' : 'create' : row.action;
          row.PRD_CNAME = row.PRD_CNAME || row.PRD_NAME;
          row.PURCHASE_ORG_AMT = row.PURCHASE_ORG_AMT || 0;
          row.PURCHASE_TWD_AMT = row.PURCHASE_TWD_AMT || 0;
          row.CURRENCY_TYPE = row.CURRENCY_TYPE || row.TRUST_CURR;
          // init buyCur ins always 1
          if (row.PTYPE === 'INS')
            row.buyCur = '1';
          else
            row.buyCur = row.TRUST_CURR === 'TWD' || row.CURRENCY_TYPE === 'TWD' ? '2' : '1';
          row.INV_PERCENT = Math.round(parseFloat(row.INV_PERCENT));

          if ($scope.hasIns) {
            row.NOW_INV_PERCENT = Math.round(parseFloat((((row.NOW_AMT_TWD || 0) / $scope.headList.planAmt) || 0) * 100));
          }
          if (!!row.NOW_AMT) {
            row.isStock = true;
          }

          formatMap[type](row);
          return row;
        });
      };
    })();

    // 排序順序
    var sortListOf = function (type, list) {
      var sortkey = {
        deposit: 'INV_PRD_TYPE_2',
        fixed: 'PTYPE',
        stock: 'PTYPE'
      };
      var key = sortkey[type];
      return list.sort(function (a, b) {
        return sortOrder[type][a[key]] - sortOrder[type][b[key]];
      });
    };

    /* main progress */
    param()
      .then(function () {
        return inquire();
      })
      .then(function () {
        console.log('draw');
        drawDoughnut();
      });

  });
