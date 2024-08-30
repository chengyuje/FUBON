function formatWebViewParam() {
  var oneTime = this.getParam().oneTime;
  var prdList = this.getParam().prdList;
  var riskType = this.getParam().riskType;
  var stockAmt = this.getParam().stockAmt;
  var stockPct = this.getParam().stockPct;
  var sppType = this.getParam().sppType;
  var hasIns = this.getParam().hasIns;

  this.getScope().inputVO = {};
  this.getScope().inputVO.ONETIME = parseInt(oneTime, 10);
  this.getScope().inputVO.prdList = prdList.map(function (row) {
    return {
      PRD_CNAME: row.product.productName,
      PRD_ID: row.product.productId,
      INV_PERCENT: row.product.ratio,
			PTYPE: row.product.productType,
			CURRENCY_TYPE: row.product.currCd,
      lowMoney: row.investType === '0' ? row.product.onceLimit : row.product.monthlyLimit,
      TARGETS: row.product.targets
    }
  });
  this.getScope().riskType = riskType;
  this.getScope().stockAmt = parseInt(stockAmt, 10);
  this.getScope().stockPct = parseInt(stockPct, 10);
  this.getScope().sppType = sppType;
  this.getScope().hasIns = Boolean(hasIns);
  this.getScope().listMap = prdList.reduce(function (a, b) {
    a[b.product.productId] = b.product.productId + ' ' + b.product.productName;
    if (b.product.targets) {
      (b.TARGETS.split('/') || []).forEach(function (t) {
        a[t] = b.product.productId + ' ' + b.b.product.productName + ': ' + t;
      });
    }
    return a;
  }, {});
}

function formatWebViewResParam(isError, data) {
  this.setResult({
    isError: isError ? "1" : "0",
    errorStr: isError ? data : [],
    optimalFundList: isError ? [] : data
  });
}
