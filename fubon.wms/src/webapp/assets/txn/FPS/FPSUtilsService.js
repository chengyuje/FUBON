'use strict';
eSoafApp.factory('FPSUtilsService', ['getParameter', '$q',
  function (getParameter, $q) {

    // 'FPS.PROD_TYPE': 'pType',
    // 'PRD.PRD_STATUS': 'prdStatus',
    // 'PRD.CURRENCY': 'cur',
    // 'FPS.BUY_CURRENCY': 'cmbCur',
    // 'FPS.TXN_TYPE': 'TxnType',

    // Mapping Map
    var paramMapping = {
      'KYC.EDUCATION': 'kycEducation',
      'CRM.CUST_EDUCATIONAL_BACKGROUND': 'educationCif',
      'FPS.PLANNING': 'planning',
      'FPS.PARAM_STATUS': 'paramStatus',
      'PRD.PRD_RR': 'pRiskType',
      'FPS.PARAM_PTYPE': 'paramPtype',
      'FPS.RISK_ATTR_MAPPING': 'riskAttrMapping',
      'FPS.REPORT_ALERT': 'reportAlert',
      'CUST.RISK_ATTR': 'riskAttr',
      'SYS.COMMON_YN': 'commonYN',
      'FPS.TXN_TYPE': 'txnType', //
      'FPS.INV_TYPE': 'invType', // 單筆 定額
      'FPS.BUY_CURRENCY': 'buyCur', // 原幣 台幣
      'FPS.CUST_RISK_ATR_2': 'riskAttrInv', // C1 C2 C3 C4
      'FPS.PROD_TYPE': 'prodType', // 基金 ETF 海外債 海外股票 SN DCI SI 保險
      'FPS.INV_PRD_TYPE_2': 'invProdType', // 台幣商品 外幣商品 儲蓄型保險商品
      'FPS.INV_PRD_TYPE': 'invPrdType', // 儲蓄型保險+存款 固定收益商品 基股類商品
      'FPS.CURRENCY': 'currency',
      'SOT.NF_MIN_BUY_AMT_1': 'invType1Limit',
      'SOT.NF_MIN_BUY_AMT_2': 'invType2Limit',
    };

    /**
     * get xml parameter if is not exist,
     * those parameters will save in FPSUtilsService.mapping
     * @param {array} arr -parameter keys
     */
    var getXmlParam = function (arr) {
      var that = this;
      var req = [];
      var deferred = $q.defer();
      arr.forEach(function (item) {
        if (!that.mapping[paramMapping[item]]) {
          req.push(item);
        }
      });
      getParameter.XML(req, function (totas) {
        if (totas) {
          req.forEach(function (item) {
            that.mapping[paramMapping[item]] = totas.data[totas.key.indexOf(item)] || [];
          });
          deferred.resolve(that.mapping);
        } else {
          deferred.reject(false);
        }
      });

      return deferred.promise;
    };

    /**
     * maerge ls and output jlb data format
     * @param {array} ls
     * @param {object} mapping map for key
     */
    var formatJLBRow = function (ls, mapping) {
      mapping = mapping || {
        AMT: 'PURCHASE_TWD_AMT',
        TYPE: 'PRD_TYPE'
      };
      var total = ls.reduce(function (a, b) {
        return a + b[mapping.AMT];
      }, 0);
      // return mergeBy(angular.copy(ls), ['PRD_ID', mapping.TYPE], function (a, b) {
      //   a[mapping.AMT] += b[mapping.AMT];
      //   if (a.TARGETS || b.TARGETS)
      //     a.TARGETS = (a.TARGETS || []).concat((b.TARGETS || []));
      // })
      return ls.map(function (row) {
        return {
          PRD_ID: row.PRD_ID,
          PRD_CNAME: row.PRD_CNAME,
          WEIGHT: (row[mapping.AMT] / total) || 0,
          PRD_TYPE: row[mapping.TYPE],
          TARGETS: row.TARGETS,
          ORDER_STATUS: !!row.STORE_RAW ? 'Y' : null,
          INV_TYPE: row.InvType || row.INV_TYPE,
          PURCHASE_TWD_AMT: row[mapping.AMT]
        };
      });
    };


    /* ======= REALLY HELPING FUNCTIONS ======= */
    /**
     * 合併
     * @param {array} list -main list
     * @param {array} keys -merge keys
     * @param {fn} mergeFn -do merge
     * @param {fn} filterFn -if true do filter
     */
    var mergeBy = function (list, keys, mergeFn, filterFn) {
      var newList = [];
      var mergeTable = {};
      var cnt = 0;
      // find same key
      list.forEach(function (row, index) {
        if (filterFn && !filterFn(row, index)) return cnt += 1;
        var id = keys.reduce(function (a, b) {
          return a += (row[b] || '');
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

    /**
     * 加comma by js
     * @param {number} x
     */
    var numberWithCommas = function (x) {
      if (!x) return x;
      var parts = x.toString().split('.');
      parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ',');
      return parts.join('.');
    };

    /**
     * 加總 list 欄位
     * @param {array} list
     * @param {string} key
     */
    var sumList = function (list, key) {
      if(list) {
    	  return angular.copy(list).reduce(function (a, b) {
    		  return a += key ? b[key] : b;
    	  }, 0);    	  
      } else {
    	  return 0;
      }
    };

    return {
      // FPS
      mapping: {},
      getXmlParam: getXmlParam,
      JLB: {
        formatJLBRow: formatJLBRow
      },
      // really utils
      mergeBy: mergeBy,
      numberWithCommas: numberWithCommas,
      sumList: sumList
    };
  }
]);
