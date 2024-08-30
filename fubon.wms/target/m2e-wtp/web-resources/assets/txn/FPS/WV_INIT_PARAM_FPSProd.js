function formatWebViewParam() {
  var detailUrl = {
    BND: 'assets/txn/PRD130/PRD130_DETAIL.js',
    MFD: 'assets/txn/PRD110/PRD110_DETAIL.js',
    SN: 'assets/txn/PRD140/PRD140_DETAIL.js',
    SI: 'assets/txn/PRD150/PRD150_DETAIL.js',
    INS: 'assets/txn/PRD160/PRD160_DETAIL.js'
  };

  var productId = this.getParam().productId;
  var productType = this.getParam().productType;
  var invProductType = this.getParam().invProductType;
  var sppType = this.getParam().sppType;

  this.getScope().riskType = this.getParam().riskType;
  this.getScope().OBU = this.getParam().OBU;
  this.getScope().isPro = this.getParam().isPro;
  this.getScope().type = invProductType == '' ? null : (invProductType == '2' ? 'fixed' : 'stock');
  this.getScope().trendPath = {
    H: 'assets/images/ic-unbiased.svg',
    B: 'assets/images/ic-rise.svg',
    S: 'assets/images/ic-decline.svg'
  };

  if (sppType === 'SPP') {
    doGetScript([detailUrl.MFD, detailUrl.INS]);
    this.getScope().data = [{
      title: '基金',
      path: 'assets/txn/FPS/FPSProd_MFD.html',
      order: 0,
      active: productType === 'MFD' || !productType,
      data: productType === 'MFD' && !!productId ? {
        'prdID': productId
      } : undefined
    }, {
      title: '投資型保險',
      path: 'assets/txn/FPS/FPSProd_InvestIns.html',
      order: 1,
      active: productType === 'INS',
      data: productType === 'INS' && !!productId ? {
        'prdID': productId
      } : undefined
    }];
  } else if ("1" == invProductType) {
    doGetScript([detailUrl.INS]);
    this.getScope().data = [{
      title: '儲蓄型保險',
      path: 'assets/txn/FPS/FPSProd_SavingIns.html',
      order: 0,
      active: productType === 'INS' || !productType,
      data: productType === 'INS' && !!productId ? {
        'prdID': productId
      } : undefined
    }, {
      title: '存款',
      path: 'assets/txn/FPS/FPSProd_Currency.html',
      order: 1,
      active: productType === 'DOP',
      data: productType === 'DOP' && !!productId ? {
        'prdID': productId
      } : undefined
    }];
  } else if ("2" == invProductType) {
    doGetScript([detailUrl.BND, detailUrl.SI, detailUrl.SN]);
    this.getScope().data = [{
        title: '基金',
        path: 'assets/txn/FPS/FPSProd_MFD.html',
        order: 0,
        active: productType === 'MFD' || !productType,
        data: productType === 'MFD' && !!productId ? {
          'prdID': productId
        } : undefined
      }, {
        title: 'ETF',
        path: 'assets/txn/FPS/FPSProd_ETF.html',
        order: 1,
        active: productType === 'ETF',
        data: productType === 'ETF' && !!productId ? {
          'prdID': productId
        } : undefined
      }, {
      title: '海外債',
      path: 'assets/txn/FPS/FPSProd_Bond.html',
      order: 2,
      active: productType === 'BND',
      data: productType === 'BND' && !!productId ? {
        'prdID': productId
      } : undefined
    }, {
      title: 'SN',
      path: 'assets/txn/FPS/FPSProd_SN.html',
      order: 3,
      active: productType === 'SN',
      data: productType === 'SN' && !!productId ? {
        'prdID': productId
      } : undefined
    }, {
      title: 'SI',
      path: 'assets/txn/FPS/FPSProd_SI.html',
      order: 4,
      active: productType === 'SI',
      data: productType === 'SI' && !!productId ? {
        'prdID': productId
      } : undefined
    }, {
        title: '奈米投',
        path: 'assets/txn/FPS/FPSProd_NANO.html',
        order: 5,
        active: productType === 'NANO',
        data: productType === 'NANO' && !!productId ? {
          'prdID': productId
        } : undefined
     }];
  } else if ("3" == invProductType) {
    doGetScript([detailUrl.MFD, detailUrl.INS]);

    this.getScope().data = [{
      title: '基金',
      path: 'assets/txn/FPS/FPSProd_MFD.html',
      order: 0,
      active: productType === 'MFD' || !productType,
      data: productType === 'MFD' && !!productId ? {
        'prdID': productId
      } : undefined
    }, {
      title: 'ETF',
      path: 'assets/txn/FPS/FPSProd_ETF.html',
      order: 1,
      active: productType === 'ETF',
      data: productType === 'ETF' && !!productId ? {
        'prdID': productId
      } : undefined
    }, {
        title: '奈米投',
        path: 'assets/txn/FPS/FPSProd_NANO.html',
        order: 2,
        active: productType === 'NANO',
        data: productType === 'NANO' && !!productId ? {
          'prdID': productId
        } : undefined
     }];
  }
}

// 轉為App decode 格式
var productFormat4App = function (row) {
//  var outlookMap = {
//    'B': 0,
//    'H': 1,
//    'S': 2,
//  };
  
  var cis3m = -1;
  if (row.CIS_3M === 'B') {
	  cis3m = 0;
  } else if (row.CIS_3M === 'H') {
	  cis3m = 1;
  } else if (row.CIS_3M === 'S') {
	  cis3m = 2;
  }
  
  return {
    productId: row.INV_PRD_TYPE === '1' && row.INV_PRD_TYPE_2 !== '3' ? row.CURRENCY_TYPE : row.PRD_ID,
    productName: row.PRD_CNAME,
    invProductType: row.INV_PRD_TYPE.toString(),
    productType: row.PTYPE || 'DOP',
    categoryId: row.FUND_TYPE || '',
    marketId: '',
    riskLevel: row.RISK_TYPE || '',
    isRecommand: row.MAIN_PRD,
    currCd: row.CURRENCY_TYPE,
    pCurrency: '',
    outlook: cis3m,
    amount: 0,
    amountNTD: 0,
    stockAmount: 0,
    stockAmountNTD: 0,
    onceLimit: row.PTYPE == 'NANO' ? 100 : (parseInt(row.GEN_SUBS_MINI_AMT_FOR, 10) || 0),
    monthlyLimit: parseInt(row.SML_SUBS_MINI_AMT_FOR, 10) || 0,
    onceProgressive: parseInt(row.PRD_UNIT, 10) || 0,
    monthLyProgressive: 0,
    rate: 0,
    targets: row.TARGETS || '',
    keyNo: row.KEY_NO ? row.KEY_NO.toString() : '',
    ratio: 0,
    categoryName: row.FUND_TYPE_NAME || '',
    SEQNO: '',
    marketName: row.NAME ? row.NAME.toString() : '',
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
    data.forEach(function (block) {
      block.list = block.list.map(function (row) {
        return productFormat4App(row);
      });
    });
  }
  this.setResult(data);
}
