var appObj2jsObj = function (obj) {
	if (!obj.product) obj.product = {};
	return {
		NAME: obj.product.marketName,
		CERT_NBR: null,
		CURRENCY_TYPE: obj.product.currCd,
		FUND_TYPE_NAME: obj.product.categoryName,
		INV_PERCENT: Math.round(obj.product.ratio),
		INV_PRD_TYPE: obj.product.invProductType,
		INV_PRD_TYPE_2: obj.product.invProductType === '1' ? getInvProdType2(obj.product.productType, obj.product.currCd) : null,
		INV_TYPE: obj.product.investType,
		KEY_NO: obj.product.keyNo,
		NOW_AMT: obj.product.stockAmount,
		NOW_AMT_TWD: obj.product.stockAmountNTD,
		NOW_INV_PERCENT: obj.product.stockRatio,
		PRD_CNAME: obj.product.productName,
		PRD_ID: obj.product.productId,
		PTYPE: obj.product.productType,
		PRD_TYPE: obj.product.productType,
		PURCHASE_ORG_AMT: obj.product.amount,
		PURCHASE_TWD_AMT: obj.product.amountNTD,
		NTD_PERCENT: obj.product.amountNTD,
		RISK_TYPE: obj.product.riskLevel,
		SEQNO: obj.product.SEQNO,
		TARGETS: obj.product.targets,
		TRUST_CURR: obj.product.pCurrency,
		TXN_TYPE: null,
		buyCur: obj.currency,
		CHG_PERCENT : obj.product.amount,
	    NTD_PERCENT : obj.product.amountNTD,
	    STORE_NTD : obj.product.stockAmountNTD
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
    fullProdType: {
      FUND: '基金投資注意事項',
      ETF: '海外ETF投資注意事項',
      BND: '海外債商品風險宣告聲明文字',
      SN: '境外結構型商品(SN)風險宣告聲明文字',
      SI: '組合式商品(SI)風險宣告聲明文字',
      'INS-S': '儲蓄型保險風險宣告聲明文字',
      INSI: '連結全委帳戶之投資型保險投資注意事項',
    }
  };

  ['transaction', 'param'].forEach(function (key) {
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
