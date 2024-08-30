function formatWebViewParam(){
//	debugger
	this.getScope().data = {};
	this.getScope().paramMap = this.getParam();
	
	var sotTypeMap = {
		MFD: {
          1: { 				// 申購
            1: 'SOT110', 	// 單筆
            2: 'SOT120' 	// 定額
          },
          F1: { 			// 申購
            1: 'SOT110', 	// 單筆
            2: 'SOT120' 	// 定額
          },
          F2: 'SOT130', 	// 部分贖回
          F3: 'SOT130', 	// 贖回
          F4: 'SOT140', 	// 轉出
//        F5: 'SOT140' 		// 轉入 無連結
        },
        ETF: {
          1: 'SOT210', 		// 申購
          E1: 'SOT210', 	// 買進
          E2: 'SOT220' 		// 賣出
        },
        INS: {
          1: 'IOT110', 		// 申購
          I1: 'IOT110', 	// 申購
//        I2: '' 			// 贖回 無連結
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
	}
	
//	this.getScope().data.where = this.getScope().paramMap.where;
	
	var prd_type = this.getScope().paramMap.prdType;
	var txn_type = this.getScope().paramMap.txnType;
	var inv_type = this.getScope().paramMap.invType;
	
	this.getScope().data.where = (prd_type === 'MFD' && (txn_type === '1' || txn_type === 'F1') ? 
								 sotTypeMap[prd_type][txn_type][inv_type] : sotTypeMap[prd_type][txn_type])
	          
	this.getScope().data.transact = {
	    PRD_ID :this.getScope().paramMap.prdID,
	    INV_TYPE :this.getScope().paramMap.invType,
	    CmbCur :this.getScope().paramMap.cmbCur,
	    TRUST_CURR :this.getScope().paramMap.trustCurr,
	    CHG_PERCENT :this.getScope().paramMap.chgPercent,
	    PURCHASE_ORG_AMT :this.getScope().paramMap.purhaseOrgAmt,
	    PURCHASE_TWD_AMT :this.getScope().paramMap.purhaseTwdAmt
	};
	this.getScope().custID = this.getScope().paramMap.custID;
	this.getScope().planID = this.getScope().paramMap.planID;
}	

function formatWebViewResParam(data){
	this.setResult(data);
}
