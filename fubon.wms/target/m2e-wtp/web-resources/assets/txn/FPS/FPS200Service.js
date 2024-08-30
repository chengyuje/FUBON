eSoafApp.factory('fps200Service', ['ngDialog', 'getParameter', '$q',
  function (ngDialog, getParameter, $q) {
    var fps200 = {
      custData: null, // 客戶資料
      planID: null, // 計畫ID
      mapping: {}, // xml參數
      tab: null,
      stored: {}, // for mapping or static data

      //get & set 客戶基本資料
      getCustData: function () {
        return this.custData;
      },
      setCustData: function (val) {
        this.custData = val;
      },

      //get & set 計畫ID
      getPlanID: function () {
        return this.planID;
      },
      setPlanID: function (val) {
        this.planID = val;
      },

      //PAGE路徑
      path: {
        TAB2: 'assets/txn/FPS300/FPS300_TAB2.html',
      },

      // Mapping Map
      mappingMap: {
        // 'CRM.CUST_EDUCATIONAL_BACKGROUND': 'educationCif',
        'CUST.RISK_ATTR': 'riskAttr',
        // 'FPS.PLANNING': 'planning',
        // 'PRD.PTYPE': 'pType',
        // 'FPS.PARAM_STATUS': 'paramStatus',
        'SYS.COMMON_YN': 'commonYN',
        // 'PRD.PRD_RR': 'pRiskType',
        // 'FPS.PARAM_PTYPE': 'paramPtype',
        // 'PRD.PRD_STATUS': 'prdStatus',
        // 'FPS.INV_PRD_TYPE': 'invPrdType',
        // 'FPS.RISK_ATTR_MAPPING': 'riskAttrMapping',
        // 'PRD.CURRENCY': 'cur',
        // 'FPS.CUR_LIST': 'cmbCur',
        'FPS.TXN_TYPE': 'txnType', // 
        'FPS.INV_TYPE': 'invType', // 單筆 定額
        // 'FPS.REPORT_ALERT': 'reportAlert',
        'FPS.BUY_CURRENCY': 'buyCur', // 原幣 台幣
        'FPS.CUST_RISK_ATR_2': 'riskAttrInv', // C1 C2 C3 C4
        'FPS.PROD_TYPE': 'prodType', // 基金 ETF 海外債 海外股票 SN DCI SI 保險
        'FPS.INV_PRD_TYPE_2': 'invProdType', // 台幣商品 外幣商品 儲蓄型保險商品
        'FPS.INV_PRD_TYPE': 'invPrdType', // 儲蓄型保險+存款 固定收益商品 基股類商品
        'FPS.CURRENCY': 'currency',
        'SOT.NF_MIN_BUY_AMT_1': 'invType1Limit',
        'SOT.NF_MIN_BUY_AMT_2': 'invType2Limit',
      },

      getXmlParam: function (arr) {
        var fps300 = this;
        var paramList = [];
        var req = [];
        var deferred = $q.defer();
        arr.forEach(function (item) {
          if (!fps300.mapping[fps300.mappingMap[item]]) {
            req.push(item);
          }
        });
        getParameter.XML(req, function (totas) {
          if (totas) {
            req.forEach(function (item) {
              fps300.mapping[fps300.mappingMap[item]] = totas.data[totas.key.indexOf(item)] || [];
            });
            deferred.resolve(fps300.mapping);
          } else {
            deferred.reject(false);
          }
        });

        return deferred.promise;
      },

      // set & get once(default)
      _get: function (id, flag) {
        var data = this.stored[id];
        if (!flag) this.stored[id] = {};
        return data;
      },

      _set: function (id, val) {
        this.stored[id] = val;
      },

      // format float => float
      formatFloat: function formatFloat(num, pos) {
        var size = Math.pow(10, pos);
        return Math.round(num * size) / size;
      },

      // 是否為空 => boolean
      isEmpty: function (value, type) {
        var _temp = value === undefined || value === null;
        if (type === 'string') {
          return _temp || value.trim() === '';
        } else if (type === 'int') {
          return _temp && value !== 0;
        } else {
          return _temp;
        }
      },

      // 刪除 comma => int
      decomma: function (number) {
        return parseInt(number.toString().replace(/\,/, ''), 10);
      },

      // 集合 object keys => object
      groupBy: function (arr, key, fn) {
        var _arr = angular.copy(arr);
        var temp = {};
        _arr.forEach(function (row, index) {
          if (temp[row[key]] === undefined) {
            temp[row[key]] = fn(row);
          } else {
            temp[row[key]] = fn(row, temp[row[key]]);
          }
        });
        return temp;
      },

      // Math
      //由於JS的浮點數運算有誤差，所以編寫相關函數來得到精確的結果
      //加法
      accAdd: function (arg1, arg2) {
        var r1, r2, m;
        try {
          r1 = arg1.toString().split('.')[1].length;
        } catch (e) {
          r1 = 0;
        }
        try {
          r2 = arg2.toString().split('.')[1].length;
        } catch (e) {
          r2 = 0;
        }
        m = Math.pow(10, Math.max(r1, r2));
        return (arg1 * m + arg2 * m) / m;
      },

      //減法
      accSub: function (arg1, arg2) {
        var r1, r2, m, n;
        try {
          r1 = arg1.toString().split('.')[1].length;
        } catch (e) {
          r1 = 0;
        }
        try {
          r2 = arg2.toString().split('.')[1].length;
        } catch (e) {
          r2 = 0;
        }
        m = Math.pow(10, Math.max(r1, r2));
        //last modify by deeka
        //动态控制精度长度
        n = (r1 >= r2) ? r1 : r2;
        return parseFloat(((arg1 * m - arg2 * m) / m).toFixed(n));
      },

      //除法
      accDiv: function (arg1, arg2) {
        var t1 = 0,
          t2 = 0,
          r1, r2;
        try {
          t1 = arg1.toString().split('.')[1].length;
        } catch (e) {}
        try {
          t2 = arg2.toString().split('.')[1].length;
        } catch (e) {}
        r1 = Number(arg1.toString().replace('.', ''));
        r2 = Number(arg2.toString().replace('.', ''));
        return (r1 / r2) * Math.pow(10, t2 - t1);
      },

      //乘法
      accMul: function (arg1, arg2) {
        var m = 0,
          s1 = arg1.toString(),
          s2 = arg2.toString();
        try {
          m += s1.split('.')[1].length;
        } catch (e) {}
        try {
          m += s2.split('.')[1].length;
        } catch (e) {}
        return Number(s1.replace('.', '')) * Number(s2.replace('.', '')) / Math.pow(10, m);
      },

      // window top 
      setGoTop: function (el, margin) {
        var rect = el.getBoundingClientRect();
        var scrollTop = window.pageYOffset || document.documentElement.scrollTop;
        var top = rect.top + scrollTop;

        return function () {
          document.body.scrollTop = top - margin;
          document.documentElement.scrollTop = top - margin;
        };
      }
    };
    return fps200;
  }
]);
