/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS230ModelsController',
  function ($scope, $controller, ngDialog, FPSUtilsService) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPS230ModelsController';

    /* parameter */
    $scope.itemList = [];
    $scope.path = '';
    $scope.model = {};
    $scope.titleMap = [{
      title: '總行推薦',
      path: '',
      order: 0,
      data: {
        recommend: '',
        target: [],
        hisPerformance: []
      },
      active: true
    }];
    var sortOrder = {
      stock: {
        'MFD': 1,
        'ETF': 2,
        'INS': 3
      }
    };
    $scope.selection = [];
    $scope.arrowPath = {
      up: 'assets/images/ic-up.svg',
      down: 'assets/images/ic-down.svg'
    };
    $scope.arrowUp = false;
    var pathMap = {
      detail: {
        BND: './assets/txn/PRD130/PRD130_DETAIL.html',
        MFD: './assets/txn/PRD110/PRD110_DETAIL.html',
        SN: './assets/txn/PRD140/PRD140_DETAIL.html',
        SI: './assets/txn/PRD150/PRD150_DETAIL.html',
        INS: './assets/txn/PRD160/PRD160_DETAIL.html'
      },
      product: './assets/txn/FPS/FPSProd.html',
      model: './assets/txn/FPS230/FPS230ModelDetail.html'
    };
    
    //for App
    $scope.modelMapping = {
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
	      }
	    ],
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
	      }
	    ]
    }
    var custID = $scope.cust_id;
    var rateMap = $scope.data.rateMap;
    $scope.mapping = $scope.data.mapping;
    $scope.prodTypeMap = $scope.mapping.prodType || $scope.mapping.pType;
    $scope.totalAmt = $scope.data.totalAmt || 0;
    $scope.stockAmt = $scope.data.stockAmt || 0;
    var stockPctOrigin = $scope.data.stockPct;
    $scope.stockPct = Math.round($scope.data.stockPct) || 100;
    $scope.riskType = $scope.data.riskType;
    $scope.hasIns = $scope.data.hasIns;
    $scope.preferTitle = '總行推薦';
    $scope.sppType = $scope.data.sppType;
    $scope.returnAll = $scope.data.returnAll; // true: 拿回全部的list, false:拿回selected
    $scope.featureDescription = $scope.data.featureDescription;
    $scope.type = $scope.data.type ? ($scope.data.type == 'fixed' ? '類債券' : '類股票') : '';
    $scope.stockBondType = $scope.data.type ? ($scope.data.type == 'fixed' ?　'B' : 'S') : 'ALL';
    $scope.title = $scope.hasIns ? '投資組合配置' : null;
    var txnID = $scope.data.txnID;
    var txnVO = $scope.data.txnVO;
    var inputVO = $scope.data.inputVO;
    
    $scope.modelBias = {};

    /* init */
    $scope.init = function () {
    	// 2018/11/19 WV
		$scope.sendRecv('FPS230', 'getFeatureDescription', 'com.systex.jbranch.app.server.fps.fps230.FPS230InputVO', {},
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.featureDescription.length > 0) {
							$scope.featureDescription = tota[0].body.featureDescription;
                		}
					}
		});
    	
    	getModels();
    	getHistory();
    };

    /* main function */

    var getModels = function () {
      $scope.sendRecv(txnID, 'getModels', txnVO, inputVO,
        function (tota, isError) {
          if (!isError) {
        	
        	var filterResult = tota[0].body.outputList.filter(function(item, index, array){
        		return $scope.sppType === 'SPP' ? true : item.STOCK_BOND_TYPE === $scope.stockBondType; 
        	});  
        	  
            calPctBias(filterResult);
            var total = $scope.sppType === 'SPP' ? $scope.stockAmt : $scope.totalAmt;
            debugger
            $scope.recommendList = filterResult.map(function (row) {
              var rate = rateMap[row.CURRENCY_TYPE];
              row.PURCHASE_TWD_AMT = Math.round(total * ($scope.hasIns ? 1 : ($scope.modelBias.stock || 1)) * row.INV_PERCENT / 100*10000000000)/10000000000;
              row.PURCHASE_ORG_AMT = Math.round(row.PURCHASE_TWD_AMT / (rate ? rate.buy : 1)*10000000000)/10000000000;
              if ($scope.sppType === 'INV') {
                // 模擬FPS230 由台幣轉原幣再轉回台幣
            	row.TEMP_PURCHASE_TWD_AMT = Math.round(row.PURCHASE_ORG_AMT * (rate ? rate.buy : 1)*10000000000)/10000000000;
            	row.TEMP_PURCHASE_ORG_AMT = row.PURCHASE_ORG_AMT;
                row.PURCHASE_TWD_AMT = Math.round(row.PURCHASE_ORG_AMT * (rate ? rate.buy : 1));
                row.PURCHASE_ORG_AMT = Math.round(row.PURCHASE_ORG_AMT);
              }
              row.TEMP_INV_PERCENT = Math.round(row.INV_PERCENT * (($scope.hasIns || ($scope.sppType === 'SPP')) ? 1 : ($scope.modelBias.stock || 1))*10000000000)/10000000000;
              row.INV_PERCENT = Math.round(row.INV_PERCENT * (($scope.hasIns || ($scope.sppType === 'SPP')) ? 1 : ($scope.modelBias.stock || 1)));
              row.PURCHASE_TWD_AMT = Math.round(row.PURCHASE_TWD_AMT);
              row.PURCHASE_ORG_AMT = Math.round(row.PURCHASE_ORG_AMT);
              return row;
            }).sort(function (a, b) {
              return sortOrder.stock[a.PTYPE] - sortOrder.stock[b.PTYPE];
            });
          }
        });
    };

    var getHistory = function () {
      $scope.sendRecv('FPS230', 'historyModels', 'com.systex.jbranch.app.server.fps.fps230.FPS230InputVO', {
          riskType: $scope.riskType,
          sppType: $scope.sppType,
          stockBondType: $scope.stockBondType // B/S
        },
        function (tota, isError) {
          if (!isError) {
            var ymReg = /(\d{6})/;
            var tmp = (tota[0].body.historyModelList || []).reduce(function (a, b) {
              if (a[b.DATA_YEARMONTH] === undefined) {
                a[b.DATA_YEARMONTH] = {
                  items: [b],
                  RETURN_ANN: b.RETURN_ANN ? b.RETURN_ANN.toFixed(2) : '--',
                  VOLATILITY: b.VOLATILITY ? b.VOLATILITY.toFixed(2) : '--',
                  DATA_YEARMONTH: ymReg.test(b.DATA_YEARMONTH) ?
                    b.DATA_YEARMONTH : b.DATA_YEARMONTH.substring(0, 4) + '/' + b.DATA_YEARMONTH.substring(4, 6),
                  CUST_RISK_ATR: b.CUST_RISK_ATR,
                };
              } else {
                a[b.DATA_YEARMONTH].items.push(b);
              }
              return a;
            }, {});

            $scope.modelList = Object.keys(tmp).map(function (key) {
              return tmp[key];
            }).sort(function (a, b) {
              return b.DATA_YEARMONTH.localeCompare(a.DATA_YEARMONTH);
            });
          }
          return false;
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
        case 'product':
        	var productMap = [];
        	if ($scope.data.type == 'fixed'){
        		var fixedMap = $scope.mapping.fixedMap || $scope.modelMapping.fixedMap;
        		var productMap = angular.copy(fixedMap).map(function (row, index) {
        			row.active = (data.PTYPE === 'MFD' && index === 0) || 
        				(data.PTYPE === 'ETF' && index === 1) || 
        				(data.PTYPE === 'BND' && index === 2) || 
        				(data.PTYPE === 'SN' && index === 3) || 
        				(data.PTYPE === 'SI' && index === 4);
        			if (row.active) row.data = {
        					prdID: data.PRD_ID
        			};
        			return row;
        		});        		
        	} else {
        		var stockMap = $scope.mapping.stockMap || $scope.modelMapping.stockMap;
        		var productMap = angular.copy(stockMap).map(function (row, index) {
        			row.active = (data.PTYPE === 'MFD' && index === 0) ||
	        			(data.PTYPE === 'ETF' && index === 1) ||
	        			(data.PTYPE === 'INS' && index === 2);
        			if (row.active) row.data = {
        					prdID: data.PRD_ID
        			};
        			return row;
        		});
        	}
          openDialog(pathMap.product, productMap, 'product');
          break;
        case 'model':
          openDialog(pathMap.model, data, type);
          break;
        default:
          break;
      }
    };

    var openDialog = function (path, data, type) {
      if (!path) return false;
      var scope = $scope;
      var dialog = ngDialog.open({
        template: path,
        className: 'FPS200',
        controller: ['$scope', function ($scope) {
          $scope.data = data;
          $scope.trendPath = scope.mapping.trendPath;
          $scope.riskType = scope.riskType;
          $scope.row = angular.copy(data);
          switch (type) {
            case 'product':
              $scope.isMore = true;
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
          // }
          $scope.cust_id = custID;
        }]
      });

    };

    $scope.back2Main = function (type) {
      var itemList = [];
      if (type)
        itemList = $scope.recommendList;
      else {
        itemList = $scope.recommendList.filter(function (row) {
          return row.selected;
        });
      }
      // 2018/6/13 WV
      if(typeof(webViewParamObj) != 'undefined'){
      	webViewParamObj.formatWebViewResParam(itemList);
      	window.webkit.messageHandlers.resultCompleted.postMessage(webViewParamObj.getResult());
      } else
    	  $scope.closeThisDialog(itemList);
    };

    /* helping function */
    var mockSet = function (arr, key) {
      var resultSet = [];
      var keySet = [];

      arr.forEach(function (row) {
        var rowKey = key ? row[key] : row;
        if (keySet.length > 0 && keySet.indexOf(rowKey) >= 0) {
          return false;
        }
        resultSet.push(row);
        keySet.push(rowKey);
      });

      return resultSet;
    };

    // model 計算新比例偏差
    var calPctBias = function (modelList) {
      $scope.modelBias = {};
      // step2 pct / model pct
      var step2Pct = stockPctOrigin || 0;
      var modelPct = FPSUtilsService.sumList(modelList, 'INV_PERCENT') || 0;
      $scope.modelBias.stock = Math.round((step2Pct / (modelPct || step2Pct)) * 100) / 100 || 0;
    };

    /* watch */

    /* main progress */
    console.log('FPSModelsController');
    $scope.init();
  }
);
