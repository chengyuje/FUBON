var getInvProdType2 = function (type, cur) {
  if (type === 'DOP') {
    if (cur === 'TWD') return '1'
    return '2'
  } else {
    return '3';
  }
};

var mockDBDate = function (str) {
  if (!str || str == 'N') return '';
  return new Date(str.replace(/\./g, '-')).yyyyMMdd('-') + ' 00:00:00';
}

var appObj2jsObj = function (obj) {
  if (!obj.product) obj.product = {};
  return {
    CERT_NBR: null,
    CURRENCY_TYPE: obj.product.currCd,
    FUND_TYPE_NAME: obj.product.categoryName,
    INV_PERCENT: Math.round(obj.product.ratio || 0),
    INV_PRD_TYPE: obj.product.invProductType,
    INV_PRD_TYPE_2: obj.product.invProductType === '1' ? getInvProdType2(obj.product.productType, obj.product.currCd) : null,
    INV_TYPE: obj.product.invType,
    KEY_NO: obj.product.keyNo,
    NOW_AMT: Math.round(obj.product.stockAmount || 0),
    NOW_AMT_TWD: Math.round(obj.product.stockAmountNTD || 0),
    NOW_INV_PERCENT: Math.round(obj.product.stockRatio || 0),
    PRD_CNAME: obj.product.productName,
    PRD_ID: obj.product.productId,
    PTYPE: obj.product.productType,
    PURCHASE_ORG_AMT: Math.round(obj.product.amount || 0),
    PURCHASE_TWD_AMT: Math.round(obj.product.amountNTD || 0),
    RISK_TYPE: obj.product.riskLevel,
    SEQNO: obj.product.SEQNO,
    TARGETS: obj.product.targets,
    TRUST_CURR: obj.product.pCurrency,
    TXN_TYPE: obj.product.txnType || null,
    buyCur: obj.currency,
    TRANSACT_ORG_AMT: Math.round(obj.transactAmount || 0),
    TRANSACT_TWD_AMT: Math.round(obj.transactAmountNTD || 0)
  };
};

function formatWebViewParam() {
  var param = this.getParam();
  var that = this;
  for (var key in param) {
    if (param.hasOwnProperty(key)) {
      this.getScope()[key] = param[key];
    }
  }

  this.getScope().mapping = {
    keys: [
      'fixed',
      'stock'
    ],

    keysZhTw: [
        '類債券商品',
        '類股票商品',
        '尚未規劃金額'
     ],

    fullProdType: {
      FUND: '基金投資注意事項',
      ETF: '海外ETF投資注意事項',
      BND: '海外債商品風險宣告聲明文字',
      SN: '境外結構型商品(SN)風險宣告聲明文字',
      SI: '組合式商品(SI)風險宣告聲明文字',
      'INS-S': '儲蓄型保險風險宣告聲明文字',
      INSI: '連結全委帳戶之投資型保險投資注意事項',
      NANO: '奈米投風險宣告聲明文字',
    }
  };

  // 專業投資人
  var custProTmp = this.getParam().custInfo.CUST_PRO.split('/');
  this.getScope().custInfo.CUST_PRO_FLAG = custProTmp[0] || '';
  this.getScope().custInfo.CUST_PRO_DATE = mockDBDate(custProTmp[1]);
  // KYC
  var kycTmp = this.getParam().custInfo.KYC.split('/');
  this.getScope().custInfo.KYC_LEVEL = kycTmp[0];
  // mock format from db
  this.getScope().custInfo.KYC_DUE_DATE = mockDBDate(kycTmp[1]);

  ['transaction', ...this.getScope().mapping.keys].forEach(function (key) {
    that.getScope()[key + 'List'] = that.getScope()[key + 'List'].map(function (item) {
      return appObj2jsObj(item);
    });
  });

  setTimeout(function () {
    console.log('getParam: ', that.getParam());
    console.log('getScope: ', that.getScope());
  }, 5000);
}

function formatWebViewResParam(data) {
  this.setResult(data);
}
