package com.systex.jbranch.fubon.commons.esb.vo.nrbrva9;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by SebastianWu on 2016/9/21.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NRBRVA9InputVO {
    @XmlElement
	private String CUST_ID;                 //客戶ID
    @XmlElement
	private String BRANCH;                  //分行別
    @XmlElement
	private String KIND;                 	//信託業務別 Y:外幣信託 N:台幣信託
    @XmlElement
	private String BUYSELL;              	//買賣別 B:買 S:賣
    @XmlElement
	private String DAYTYPE;                 //委託期間
    @XmlElement
	private String TX_DATE;                 //委託日期
    @XmlElement
	private String START_DATE_1;            //交易市場日期
	@XmlElement
	private String END_DATE_1;              //交易市場日期長效單迄日
    @XmlElement
	private String ACCT_NO1;                //信託帳號
    @XmlElement
	private String DBUOBU;                  //DBU/OBU
    @XmlElement
	private String ACCT_ID;                 //扣款ID
    @XmlElement
	private String ACCT_NO3;                //扣款/入帳帳號
    @XmlElement
	private String PAY_TYPE;                //繳款方式
    @XmlElement
	private String CONFIRM;                 //確認碼
    @XmlElement
	private String BATCH_NO;                //批號
    @XmlElement
	private String EMP_ID;                  //建機人員
    @XmlElement
	private String REFEREE_ID;              //推薦人
    @XmlElement
	private String TYPE;                 	//交易類型 1:整股單(含即時手工) 2:零股單(含即時手工)
    @XmlElement
	private String FEE_RATE_1;              //手續費率_1
    @XmlElement
	private String EXG_COD_1;               //交易所代號_1
    @XmlElement
	private String STOCK_COD_1;             //商品代號_1
    @XmlElement
	private String GROUP_IFA_1;             //優惠團體代碼_1
    @XmlElement
	private String AMOUNT_1;           		//下單數量_1
    @XmlElement
	private String TYPE_1;              	//委託方式_1
    @XmlElement
	private String PRICE_1;            	 	//委託價格_1
    @XmlElement
	private String FEE_RATE_2;              //手續費率_2
    @XmlElement
	private String EXG_COD_2;               //交易所代號_2
    @XmlElement
	private String STOCK_COD_2;             //商品代號_2
    @XmlElement
	private String GROUP_IFA_2;             //優惠團體代碼_2
    @XmlElement
	private String AMOUNT_2;            	//下單數量_2
    @XmlElement
	private String TYPE_2;               	//委託方式_2
    @XmlElement
	private String PRICE_2;             	//委託價格_2
    @XmlElement
	private String EarnAcct;                //收益帳號
    @XmlElement
	private String REC_SEQ;                 //錄音序號
    @XmlElement
	private String IS_PL_1;                 //是否停損停利_1
    @XmlElement
	private String TAKE_PROFIT_PERC_1;  	//停利點_1
    @XmlElement
	private String TAKE_PROFIT_SYM_1;       //停利符號_1
    @XmlElement
	private String STOP_LOSS_PERC_1;        //停損點_1
    @XmlElement
	private String STOP_LOSS_SYM_1;         //停損符號_1
    @XmlElement
	private String BARGAIN_APPLY_SEQ_1;     //議價編號_1
    @XmlElement
	private String NARRATOR_ID_2;           //解說專員編號_2
    @XmlElement
	private String IS_PL_2;                 //是否停損停利_2
    @XmlElement
	private String TAKE_PROFIT_PERC_2;      //停利點_2
    @XmlElement
	private String TAKE_PROFIT_SYM_2;       //停利符號_2
    @XmlElement
	private String STOP_LOSS_PERC_2;        //停損點_2
    @XmlElement
	private String STOP_LOSS_SYM_2;         //停損符號_2
    @XmlElement
	private String BARGAIN_APPLY_SEQ_2;     //議價編號_2
    @XmlElement
	private String LoanCode;     //系統發查90天內的貸款紀錄，若為Y，則客戶申貸紀錄自動顯示Y，其餘為N
    

    public String getCUST_ID() {
        return CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getBRANCH() {
        return BRANCH;
    }

    public void setBRANCH(String BRANCH) {
        this.BRANCH = BRANCH;
    }

    public String getKIND() {
        return KIND;
    }

    public void setKIND(String KIND) {
        this.KIND = KIND;
    }

    public String getBUYSELL() {
        return BUYSELL;
    }

    public void setBUYSELL(String BUYSELL) {
        this.BUYSELL = BUYSELL;
    }

    public String getDAYTYPE() {
        return DAYTYPE;
    }

    public void setDAYTYPE(String DAYTYPE) {
        this.DAYTYPE = DAYTYPE;
    }

    public String getTX_DATE() {
        return TX_DATE;
    }

    public void setTX_DATE(String TX_DATE) {
        this.TX_DATE = TX_DATE;
    }

    public String getSTART_DATE_1() {
		return START_DATE_1;
	}

	public void setSTART_DATE_1(String sTART_DATE_1) {
		START_DATE_1 = sTART_DATE_1;
	}

	public String getEND_DATE_1() {
		return END_DATE_1;
	}

	public void setEND_DATE_1(String eND_DATE_1) {
		END_DATE_1 = eND_DATE_1;
	}

	public String getACCT_NO1() {
        return ACCT_NO1;
    }

    public void setACCT_NO1(String ACCT_NO1) {
        this.ACCT_NO1 = ACCT_NO1;
    }

    public String getDBUOBU() {
        return DBUOBU;
    }

    public void setDBUOBU(String DBUOBU) {
        this.DBUOBU = DBUOBU;
    }

    public String getACCT_ID() {
        return ACCT_ID;
    }

    public void setACCT_ID(String ACCT_ID) {
        this.ACCT_ID = ACCT_ID;
    }

    public String getACCT_NO3() {
        return ACCT_NO3;
    }

    public void setACCT_NO3(String ACCT_NO3) {
        this.ACCT_NO3 = ACCT_NO3;
    }

    public String getPAY_TYPE() {
        return PAY_TYPE;
    }

    public void setPAY_TYPE(String PAY_TYPE) {
        this.PAY_TYPE = PAY_TYPE;
    }

    public String getCONFIRM() {
        return CONFIRM;
    }

    public void setCONFIRM(String CONFIRM) {
        this.CONFIRM = CONFIRM;
    }

    public String getBATCH_NO() {
        return BATCH_NO;
    }

    public void setBATCH_NO(String BATCH_NO) {
        this.BATCH_NO = BATCH_NO;
    }

    public String getEMP_ID() {
        return EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getREFEREE_ID() {
        return REFEREE_ID;
    }

    public void setREFEREE_ID(String REFEREE_ID) {
        this.REFEREE_ID = REFEREE_ID;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public String getFEE_RATE_1() {
        return FEE_RATE_1;
    }

    public void setFEE_RATE_1(String FEE_RATE_1) {
        this.FEE_RATE_1 = FEE_RATE_1;
    }

    public String getEXG_COD_1() {
        return EXG_COD_1;
    }

    public void setEXG_COD_1(String EXG_COD_1) {
        this.EXG_COD_1 = EXG_COD_1;
    }

    public String getSTOCK_COD_1() {
        return STOCK_COD_1;
    }

    public void setSTOCK_COD_1(String STOCK_COD_1) {
        this.STOCK_COD_1 = STOCK_COD_1;
    }

    public String getGROUP_IFA_1() {
        return GROUP_IFA_1;
    }

    public void setGROUP_IFA_1(String GROUP_IFA_1) {
        this.GROUP_IFA_1 = GROUP_IFA_1;
    }

    public String getAMOUNT_1() {
        return AMOUNT_1;
    }

    public void setAMOUNT_1(String AMOUNT_1) {
        this.AMOUNT_1 = AMOUNT_1;
    }

    public String getTYPE_1() {
        return TYPE_1;
    }

    public void setTYPE_1(String TYPE_1) {
        this.TYPE_1 = TYPE_1;
    }

    public String getPRICE_1() {
        return PRICE_1;
    }

    public void setPRICE_1(String PRICE_1) {
        this.PRICE_1 = PRICE_1;
    }

    public String getFEE_RATE_2() {
        return FEE_RATE_2;
    }

    public void setFEE_RATE_2(String FEE_RATE_2) {
        this.FEE_RATE_2 = FEE_RATE_2;
    }

    public String getEXG_COD_2() {
        return EXG_COD_2;
    }

    public void setEXG_COD_2(String EXG_COD_2) {
        this.EXG_COD_2 = EXG_COD_2;
    }

    public String getSTOCK_COD_2() {
        return STOCK_COD_2;
    }

    public void setSTOCK_COD_2(String STOCK_COD_2) {
        this.STOCK_COD_2 = STOCK_COD_2;
    }

    public String getGROUP_IFA_2() {
        return GROUP_IFA_2;
    }

    public void setGROUP_IFA_2(String GROUP_IFA_2) {
        this.GROUP_IFA_2 = GROUP_IFA_2;
    }

    public String getAMOUNT_2() {
        return AMOUNT_2;
    }

    public void setAMOUNT_2(String AMOUNT_2) {
        this.AMOUNT_2 = AMOUNT_2;
    }

    public String getTYPE_2() {
        return TYPE_2;
    }

    public void setTYPE_2(String TYPE_2) {
        this.TYPE_2 = TYPE_2;
    }

    public String getPRICE_2() {
        return PRICE_2;
    }

    public void setPRICE_2(String PRICE_2) {
        this.PRICE_2 = PRICE_2;
    }

    public String getEarnAcct() {
        return EarnAcct;
    }

    public void setEarnAcct(String earnAcct) {
        EarnAcct = earnAcct;
    }

    public String getREC_SEQ() {
        return REC_SEQ;
    }

    public void setREC_SEQ(String REC_SEQ) {
        this.REC_SEQ = REC_SEQ;
    }

    public String getIS_PL_1() {
        return IS_PL_1;
    }

    public void setIS_PL_1(String IS_PL_1) {
        this.IS_PL_1 = IS_PL_1;
    }

    public String getTAKE_PROFIT_PERC_1() {
        return TAKE_PROFIT_PERC_1;
    }

    public void setTAKE_PROFIT_PERC_1(String TAKE_PROFIT_PERC_1) {
        this.TAKE_PROFIT_PERC_1 = TAKE_PROFIT_PERC_1;
    }

    public String getTAKE_PROFIT_SYM_1() {
        return TAKE_PROFIT_SYM_1;
    }

    public void setTAKE_PROFIT_SYM_1(String TAKE_PROFIT_SYM_1) {
        this.TAKE_PROFIT_SYM_1 = TAKE_PROFIT_SYM_1;
    }

    public String getSTOP_LOSS_PERC_1() {
        return STOP_LOSS_PERC_1;
    }

    public void setSTOP_LOSS_PERC_1(String STOP_LOSS_PERC_1) {
        this.STOP_LOSS_PERC_1 = STOP_LOSS_PERC_1;
    }

    public String getSTOP_LOSS_SYM_1() {
        return STOP_LOSS_SYM_1;
    }

    public void setSTOP_LOSS_SYM_1(String STOP_LOSS_SYM_1) {
        this.STOP_LOSS_SYM_1 = STOP_LOSS_SYM_1;
    }

    public String getBARGAIN_APPLY_SEQ_1() {
        return BARGAIN_APPLY_SEQ_1;
    }

    public void setBARGAIN_APPLY_SEQ_1(String BARGAIN_APPLY_SEQ_1) {
        this.BARGAIN_APPLY_SEQ_1 = BARGAIN_APPLY_SEQ_1;
    }

    public String getNARRATOR_ID_2() {
        return NARRATOR_ID_2;
    }

    public void setNARRATOR_ID_2(String NARRATOR_ID_2) {
        this.NARRATOR_ID_2 = NARRATOR_ID_2;
    }

    public String getIS_PL_2() {
        return IS_PL_2;
    }

    public void setIS_PL_2(String IS_PL_2) {
        this.IS_PL_2 = IS_PL_2;
    }

    public String getTAKE_PROFIT_PERC_2() {
        return TAKE_PROFIT_PERC_2;
    }

    public void setTAKE_PROFIT_PERC_2(String TAKE_PROFIT_PERC_2) {
        this.TAKE_PROFIT_PERC_2 = TAKE_PROFIT_PERC_2;
    }

    public String getTAKE_PROFIT_SYM_2() {
        return TAKE_PROFIT_SYM_2;
    }

    public void setTAKE_PROFIT_SYM_2(String TAKE_PROFIT_SYM_2) {
        this.TAKE_PROFIT_SYM_2 = TAKE_PROFIT_SYM_2;
    }

    public String getSTOP_LOSS_PERC_2() {
        return STOP_LOSS_PERC_2;
    }

    public void setSTOP_LOSS_PERC_2(String STOP_LOSS_PERC_2) {
        this.STOP_LOSS_PERC_2 = STOP_LOSS_PERC_2;
    }

    public String getSTOP_LOSS_SYM_2() {
        return STOP_LOSS_SYM_2;
    }

    public void setSTOP_LOSS_SYM_2(String STOP_LOSS_SYM_2) {
        this.STOP_LOSS_SYM_2 = STOP_LOSS_SYM_2;
    }

    public String getBARGAIN_APPLY_SEQ_2() {
        return BARGAIN_APPLY_SEQ_2;
    }

    public void setBARGAIN_APPLY_SEQ_2(String BARGAIN_APPLY_SEQ_2) {
        this.BARGAIN_APPLY_SEQ_2 = BARGAIN_APPLY_SEQ_2;
    }

    public String getLoanCode() {
		return LoanCode;
	}

	public void setLoanCode(String loanCode) {
		LoanCode = loanCode;
	}

	@Override
    public String toString() {
        return "NRBRVA9InputVO{" +
                "CUST_ID='" + CUST_ID + '\'' +
                ", BRANCH='" + BRANCH + '\'' +
                ", KIND='" + KIND + '\'' +
                ", BUYSELL='" + BUYSELL + '\'' +
                ", DAYTYPE='" + DAYTYPE + '\'' +
                ", TX_DATE='" + TX_DATE + '\'' +
                ", ACCT_NO1='" + ACCT_NO1 + '\'' +
                ", DBUOBU='" + DBUOBU + '\'' +
                ", ACCT_ID='" + ACCT_ID + '\'' +
                ", ACCT_NO3='" + ACCT_NO3 + '\'' +
                ", PAY_TYPE='" + PAY_TYPE + '\'' +
                ", CONFIRM='" + CONFIRM + '\'' +
                ", BATCH_NO='" + BATCH_NO + '\'' +
                ", EMP_ID='" + EMP_ID + '\'' +
                ", REFEREE_ID='" + REFEREE_ID + '\'' +
                ", TYPE='" + TYPE + '\'' +
                ", FEE_RATE_1='" + FEE_RATE_1 + '\'' +
                ", EXG_COD_1='" + EXG_COD_1 + '\'' +
                ", STOCK_COD_1='" + STOCK_COD_1 + '\'' +
                ", GROUP_IFA_1='" + GROUP_IFA_1 + '\'' +
                ", AMOUNT_1='" + AMOUNT_1 + '\'' +
                ", TYPE_1='" + TYPE_1 + '\'' +
                ", PRICE_1='" + PRICE_1 + '\'' +
                ", FEE_RATE_2='" + FEE_RATE_2 + '\'' +
                ", EXG_COD_2='" + EXG_COD_2 + '\'' +
                ", STOCK_COD_2='" + STOCK_COD_2 + '\'' +
                ", GROUP_IFA_2='" + GROUP_IFA_2 + '\'' +
                ", AMOUNT_2='" + AMOUNT_2 + '\'' +
                ", TYPE_2='" + TYPE_2 + '\'' +
                ", PRICE_2='" + PRICE_2 + '\'' +
                ", EarnAcct='" + EarnAcct + '\'' +
                ", REC_SEQ='" + REC_SEQ + '\'' +
                ", IS_PL_1='" + IS_PL_1 + '\'' +
                ", TAKE_PROFIT_PERC_1='" + TAKE_PROFIT_PERC_1 + '\'' +
                ", TAKE_PROFIT_SYM_1='" + TAKE_PROFIT_SYM_1 + '\'' +
                ", STOP_LOSS_PERC_1='" + STOP_LOSS_PERC_1 + '\'' +
                ", STOP_LOSS_SYM_1='" + STOP_LOSS_SYM_1 + '\'' +
                ", BARGAIN_APPLY_SEQ_1='" + BARGAIN_APPLY_SEQ_1 + '\'' +
                ", NARRATOR_ID_2='" + NARRATOR_ID_2 + '\'' +
                ", IS_PL_2='" + IS_PL_2 + '\'' +
                ", TAKE_PROFIT_PERC_2='" + TAKE_PROFIT_PERC_2 + '\'' +
                ", TAKE_PROFIT_SYM_2='" + TAKE_PROFIT_SYM_2 + '\'' +
                ", STOP_LOSS_PERC_2='" + STOP_LOSS_PERC_2 + '\'' +
                ", STOP_LOSS_SYM_2='" + STOP_LOSS_SYM_2 + '\'' +
                ", BARGAIN_APPLY_SEQ_2='" + BARGAIN_APPLY_SEQ_2 + '\'' +
                ", LoanCode='" + LoanCode + '\'' +
                '}';
    }
}
