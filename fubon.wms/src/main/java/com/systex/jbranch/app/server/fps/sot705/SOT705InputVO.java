package com.systex.jbranch.app.server.fps.sot705;

/**
 * Created by SebastianWu on 2016/9/21.
 */
public class SOT705InputVO {
    private String custId;      //客戶ID
    private String tradeSeq;    //下單交易序號
    private String seqNo;       //明細檔交易流水號
    private String checkType;   //電文確認碼 1:檢核 2:確認
    private String stockCode;   //交易所代號
    private Boolean isOBU;		//是否為OBU客戶
    private Boolean isInTran;    //是否含在途  true:含在途
    private Boolean isOnlyInTran;//是否只含在途 true:只回傳在途 下行RANGE=0002, 0003, 0004
    private String queryType;    //取得委託交易的類型 SOT230:HFMTID=0001,0002 ELSE: HFMTID=0003
    private String obuFlag;      // OBU 註記
    private String trustTS;		 //S=特金/M=金錢信託

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getTradeSeq() {
        return tradeSeq;
    }

    public void setTradeSeq(String tradeSeq) {
        this.tradeSeq = tradeSeq;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public Boolean getIsOBU() {
		return isOBU;
	}

	public void setIsOBU(Boolean isOBU) {
		this.isOBU = isOBU;
	}

	public Boolean getIsOnlyInTran() {
		return isOnlyInTran;
	}

	public void setIsOnlyInTran(Boolean isOnlyInTran) {
		this.isOnlyInTran = isOnlyInTran;
	}

	public Boolean getIsInTran() {
		return isInTran;
	}

	public void setIsInTran(Boolean isInTran) {
		this.isInTran = isInTran;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getTrustTS() {
		return trustTS;
	}

	public void setTrustTS(String trustTS) {
		this.trustTS = trustTS;
	}

	@Override
	public String toString() {
		return "SOT705InputVO [custId=" + custId + ", tradeSeq=" + tradeSeq
				+ ", seqNo=" + seqNo + ", checkType=" + checkType
				+ ", stockCode=" + stockCode + ", isOBU=" + isOBU
				+ ", isInTran=" + isInTran + ", isOnlyInTran=" + isOnlyInTran
				+ ", queryType=" + queryType + "]";
	}


    public String getObuFlag() {
        return obuFlag;
    }

    public void setObuFlag(String obuFlag) {
        this.obuFlag = obuFlag;
    }
}
