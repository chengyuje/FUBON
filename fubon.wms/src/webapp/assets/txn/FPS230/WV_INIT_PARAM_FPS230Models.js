function formatWebViewParam() {
  var custID = this.getParam().custID;
  var data = this.getParam().data;

  //	{"prodType":
  //		{"SN": "SN",
  //		"ETF": "ETF",
  //		"SI": "SI", "": "",
  //		"INS": "保險",
  //		"BND": "海外債",
  //		"DOP": "存款",
  //		"MFD": "基金"}
  //	}
  var prodType = Object.keys(data.mapping.prodType).map(function (key) {
    return {
      DATA: key,
      LABEL: data.mapping.prodType[key]
    };
  });
  data.mapping.prodType = prodType;
  data.mapping.trendPath = {
    H: 'assets/images/ic-unbiased.svg',
    B: 'assets/images/ic-rise.svg',
    S: 'assets/images/ic-decline.svg'
  };

  this.getScope().cust_id = custID;
  this.getScope().data = data;
}

//轉為App decode 格式
var productFormat4App = function (row) {
  var outlookMap = {
    "B": 0,
    "H": 1,
    "S": 2,
  };
  return {
    productId: row.INV_PRD_TYPE === '1' && row.INV_PRD_TYPE_2 !== '3' ? row.CURRENCY_TYPE : row.PRD_ID,
    productName: row.PRD_CNAME,
    invProductType: row.INV_PRD_TYPE.toString(),
    productType: row.PTYPE || 'DOP',
    categoryId: '',
    marketId: '',
    riskLevel: row.RISK_TYPE || '',
    isRecommand: row.MAIN_PRD,
    currCd: row.CURRENCY_TYPE,
    pCurrency: '',
    outlook: outlookMap[row.CIS_3M] || -1,
    amount: parseInt(row.PURCHASE_ORG_AMT, 10) || 0,
    amountNTD: parseInt(row.PURCHASE_TWD_AMT, 10) || 0,
    stockAmount: 0,
    stockAmountNTD: 0,
    onceLimit: parseInt(row.GEN_SUBS_MINI_AMT_FOR, 10) || 0,
    monthlyLimit: parseInt(row.SML_SUBS_MINI_AMT_FOR, 10) || 0,
    onceProgressive: parseInt(row.PRD_UNIT, 10) || 0,
    monthLyProgressive: 0,
    rate: 0,
    targets: row.TARGETS || '',
    keyNo: row.KEY_NO ? row.KEY_NO.toString() : '',
    ratio: parseInt(row.INV_PERCENT, 10) || 0,
    categoryName: '',
    SEQNO: '',
    marketName: '',
    invType: '',
    txnType: '1',
    invAmt: 0,
    invAmtTwd: 0,
    rtnRateWd: 0,
    rtnRateWdTwd: 0,
    dataDate:''
  };
}

function formatWebViewResParam(data) {
  if (!!data) {
    data = data.map(function (row) {
      return productFormat4App(row);
    });
  }
  this.setResult(data);
}
