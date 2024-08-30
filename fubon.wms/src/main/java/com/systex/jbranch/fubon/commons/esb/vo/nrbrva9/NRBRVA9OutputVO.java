package com.systex.jbranch.fubon.commons.esb.vo.nrbrva9;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/9/21.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NRBRVA9OutputVO {
    @XmlElement
	private String ERR_COD;         //錯誤碼
    @XmlElement
	private String ERR_TXT;         //錯誤訊息
    @XmlElement
	private String TX_FLG;          //是否可承作交易
    @XmlElement
	private String START_DATE_1;    //交易市場日期
    @XmlElement
	private String END_DATE_1;      //交易市場日期長效單迄日
    @XmlElement
	private String ERR_CODE_1;      //錯誤碼_1
    @XmlElement
	private String ERR_MSG_1;       //錯誤訊息_1
    @XmlElement
	private String STOCK_NAME_1;    //商品名稱_1
    @XmlElement
	private String EXG_NAME_1;      //交易所國別_1
    @XmlElement
	private String CURR_1;          //商品幣別_1
    @XmlElement
	private String FEE_1;           //手續費_1
    @XmlElement
	private String FEE_RATE_1;      //手續費率_1
    @XmlElement
	private String GROUP_OFA_1;     //優惠團體代碼(O)_1
    @XmlElement
	private String TAXFEE_1;        //交易稅_1
    @XmlElement
	private String OTHERFEE_1;      //其他費用_1
    @XmlElement
	private String AMT_1;           //圈存金額_1
    @XmlElement
	private String MTNFEE_1;        //保管費用_1
    @XmlElement
	private String ERR_CODE_2;      //錯誤碼_2
    @XmlElement
	private String ERR_MSG_2;       //錯誤訊息_2
    @XmlElement
	private String STOCK_NAME_2;    //商品名稱_2
    @XmlElement
	private String EXG_NAME_2;      //交易所國別_2
    @XmlElement
	private String CURR_2;          //商品幣別_2
    @XmlElement
	private String FEE_2;           //手續費_2
    @XmlElement
	private String FEE_RATE_2;      //手續費率_2
    @XmlElement
	private String GROUP_OFA_2;     //優惠團體代碼(O)_2
    @XmlElement
	private String TAXFEE_2;        //交易稅_2
    @XmlElement
	private String OTHERFEE_2;      //其他費用_2
    @XmlElement
	private String AMT_2;           //圈存金額_2
    @XmlElement
	private String MTNFEE_2;        //保管費用_2
    @XmlElement
	private String PROCUST_1;       //專業投資人警語_1
    @XmlElement
	private String PROCUST_2;       //專業投資人警語_2
    @XmlElement
	private String PRICE_1;       	//1委託價格
    @XmlElement
	private String PRICE_2;       	//2委託價格
    @XmlElement
	private String TYPE_1;       	//1委託方式
    @XmlElement
	private String TYPE_2;       	//2委託方式
    @XmlElement
	private String EarnAcct;                //收益帳號

    public String getERR_COD() {
        return ERR_COD;
    }

    public void setERR_COD(String ERR_COD) {
        this.ERR_COD = ERR_COD;
    }

    public String getERR_TXT() {
        return ERR_TXT;
    }

    public void setERR_TXT(String ERR_TXT) {
        this.ERR_TXT = ERR_TXT;
    }

    public String getTX_FLG() {
        return TX_FLG;
    }

    public void setTX_FLG(String TX_FLG) {
        this.TX_FLG = TX_FLG;
    }

    public String getSTART_DATE_1() {
        return START_DATE_1;
    }

    public void setSTART_DATE_1(String START_DATE_1) {
        this.START_DATE_1 = START_DATE_1;
    }

    public String getEND_DATE_1() {
        return END_DATE_1;
    }

    public void setEND_DATE_1(String END_DATE_1) {
        this.END_DATE_1 = END_DATE_1;
    }

    public String getERR_CODE_1() {
        return ERR_CODE_1;
    }

    public void setERR_CODE_1(String ERR_CODE_1) {
        this.ERR_CODE_1 = ERR_CODE_1;
    }

    public String getERR_MSG_1() {
        return ERR_MSG_1;
    }

    public void setERR_MSG_1(String ERR_MSG_1) {
        this.ERR_MSG_1 = ERR_MSG_1;
    }

    public String getSTOCK_NAME_1() {
        return STOCK_NAME_1;
    }

    public void setSTOCK_NAME_1(String STOCK_NAME_1) {
        this.STOCK_NAME_1 = STOCK_NAME_1;
    }

    public String getEXG_NAME_1() {
        return EXG_NAME_1;
    }

    public void setEXG_NAME_1(String EXG_NAME_1) {
        this.EXG_NAME_1 = EXG_NAME_1;
    }

    public String getCURR_1() {
        return CURR_1;
    }

    public void setCURR_1(String CURR_1) {
        this.CURR_1 = CURR_1;
    }

    public String getFEE_1() {
        return FEE_1;
    }

    public void setFEE_1(String FEE_1) {
        this.FEE_1 = FEE_1;
    }

    public String getFEE_RATE_1() {
        return FEE_RATE_1;
    }

    public void setFEE_RATE_1(String FEE_RATE_1) {
        this.FEE_RATE_1 = FEE_RATE_1;
    }

    public String getGROUP_OFA_1() {
        return GROUP_OFA_1;
    }

    public void setGROUP_OFA_1(String GROUP_OFA_1) {
        this.GROUP_OFA_1 = GROUP_OFA_1;
    }

    public String getTAXFEE_1() {
        return TAXFEE_1;
    }

    public void setTAXFEE_1(String TAXFEE_1) {
        this.TAXFEE_1 = TAXFEE_1;
    }

    public String getOTHERFEE_1() {
        return OTHERFEE_1;
    }

    public void setOTHERFEE_1(String OTHERFEE_1) {
        this.OTHERFEE_1 = OTHERFEE_1;
    }

    public String getAMT_1() {
        return AMT_1;
    }

    public void setAMT_1(String AMT_1) {
        this.AMT_1 = AMT_1;
    }

    public String getMTNFEE_1() {
        return MTNFEE_1;
    }

    public void setMTNFEE_1(String MTNFEE_1) {
        this.MTNFEE_1 = MTNFEE_1;
    }

    public String getERR_CODE_2() {
        return ERR_CODE_2;
    }

    public void setERR_CODE_2(String ERR_CODE_2) {
        this.ERR_CODE_2 = ERR_CODE_2;
    }

    public String getERR_MSG_2() {
        return ERR_MSG_2;
    }

    public void setERR_MSG_2(String ERR_MSG_2) {
        this.ERR_MSG_2 = ERR_MSG_2;
    }

    public String getSTOCK_NAME_2() {
        return STOCK_NAME_2;
    }

    public void setSTOCK_NAME_2(String STOCK_NAME_2) {
        this.STOCK_NAME_2 = STOCK_NAME_2;
    }

    public String getEXG_NAME_2() {
        return EXG_NAME_2;
    }

    public void setEXG_NAME_2(String EXG_NAME_2) {
        this.EXG_NAME_2 = EXG_NAME_2;
    }

    public String getCURR_2() {
        return CURR_2;
    }

    public void setCURR_2(String CURR_2) {
        this.CURR_2 = CURR_2;
    }

    public String getFEE_2() {
        return FEE_2;
    }

    public void setFEE_2(String FEE_2) {
        this.FEE_2 = FEE_2;
    }

    public String getFEE_RATE_2() {
        return FEE_RATE_2;
    }

    public void setFEE_RATE_2(String FEE_RATE_2) {
        this.FEE_RATE_2 = FEE_RATE_2;
    }

    public String getGROUP_OFA_2() {
        return GROUP_OFA_2;
    }

    public void setGROUP_OFA_2(String GROUP_OFA_2) {
        this.GROUP_OFA_2 = GROUP_OFA_2;
    }

    public String getTAXFEE_2() {
        return TAXFEE_2;
    }

    public void setTAXFEE_2(String TAXFEE_2) {
        this.TAXFEE_2 = TAXFEE_2;
    }

    public String getOTHERFEE_2() {
        return OTHERFEE_2;
    }

    public void setOTHERFEE_2(String OTHERFEE_2) {
        this.OTHERFEE_2 = OTHERFEE_2;
    }

    public String getAMT_2() {
        return AMT_2;
    }

    public void setAMT_2(String AMT_2) {
        this.AMT_2 = AMT_2;
    }

    public String getMTNFEE_2() {
        return MTNFEE_2;
    }

    public void setMTNFEE_2(String MTNFEE_2) {
        this.MTNFEE_2 = MTNFEE_2;
    }

    public String getPROCUST_1() {
        return PROCUST_1;
    }

    public void setPROCUST_1(String PROCUST_1) {
        this.PROCUST_1 = PROCUST_1;
    }

    public String getPROCUST_2() {
        return PROCUST_2;
    }

    public void setPROCUST_2(String PROCUST_2) {
        this.PROCUST_2 = PROCUST_2;
    }    

    public String getPRICE_1() {
		return PRICE_1;
	}

	public void setPRICE_1(String pRICE_1) {
		PRICE_1 = pRICE_1;
	}

	public String getPRICE_2() {
		return PRICE_2;
	}

	public void setPRICE_2(String pRICE_2) {
		PRICE_2 = pRICE_2;
	}
	
	public String getTYPE_1() {
		return TYPE_1;
	}

	public void setTYPE_1(String tYPE_1) {
		TYPE_1 = tYPE_1;
	}

	public String getTYPE_2() {
		return TYPE_2;
	}

	public void setTYPE_2(String tYPE_2) {
		TYPE_2 = tYPE_2;
	}
	

	public String getEarnAcct() {
		return EarnAcct;
	}

	public void setEarnAcct(String earnAcct) {
		EarnAcct = earnAcct;
	}

	@Override
    public String toString() {
        return "NRBRVA9OutputVO{" +
                "ERR_COD='" + ERR_COD + '\'' +
                ", ERR_TXT='" + ERR_TXT + '\'' +
                ", TX_FLG='" + TX_FLG + '\'' +
                ", START_DATE_1='" + START_DATE_1 + '\'' +
                ", END_DATE_1='" + END_DATE_1 + '\'' +
                ", ERR_CODE_1='" + ERR_CODE_1 + '\'' +
                ", ERR_MSG_1='" + ERR_MSG_1 + '\'' +
                ", STOCK_NAME_1='" + STOCK_NAME_1 + '\'' +
                ", EXG_NAME_1='" + EXG_NAME_1 + '\'' +
                ", CURR_1='" + CURR_1 + '\'' +
                ", FEE_1='" + FEE_1 + '\'' +
                ", FEE_RATE_1='" + FEE_RATE_1 + '\'' +
                ", GROUP_OFA_1='" + GROUP_OFA_1 + '\'' +
                ", TAXFEE_1='" + TAXFEE_1 + '\'' +
                ", OTHERFEE_1='" + OTHERFEE_1 + '\'' +
                ", AMT_1='" + AMT_1 + '\'' +
                ", MTNFEE_1='" + MTNFEE_1 + '\'' +
                ", ERR_CODE_2='" + ERR_CODE_2 + '\'' +
                ", ERR_MSG_2='" + ERR_MSG_2 + '\'' +
                ", STOCK_NAME_2='" + STOCK_NAME_2 + '\'' +
                ", EXG_NAME_2='" + EXG_NAME_2 + '\'' +
                ", CURR_2='" + CURR_2 + '\'' +
                ", FEE_2='" + FEE_2 + '\'' +
                ", FEE_RATE_2='" + FEE_RATE_2 + '\'' +
                ", GROUP_OFA_2='" + GROUP_OFA_2 + '\'' +
                ", TAXFEE_2='" + TAXFEE_2 + '\'' +
                ", OTHERFEE_2='" + OTHERFEE_2 + '\'' +
                ", AMT_2='" + AMT_2 + '\'' +
                ", MTNFEE_2='" + MTNFEE_2 + '\'' +
                ", PROCUST_1='" + PROCUST_1 + '\'' +
                ", PROCUST_2='" + PROCUST_2 + '\'' +
                ", PRICE_1='" + PRICE_1 + '\'' +
                ", PRICE_2='" + PRICE_2 + '\'' +
                ", TYPE_1='" + TYPE_1 + '\'' +
                ", TYPE_2='" + TYPE_2 + '\'' +
                '}';
    }
}
