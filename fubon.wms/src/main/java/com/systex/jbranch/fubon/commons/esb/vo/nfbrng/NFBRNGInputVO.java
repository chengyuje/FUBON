package com.systex.jbranch.fubon.commons.esb.vo.nfbrng;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 動態鎖利母基金加碼
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRNGInputVO {
    @XmlElement
	private String ERR_COD;                     //錯誤碼
    @XmlElement
	private String ERR_TXT;                     //錯誤訊息
    @XmlElement
	private String TX_FLG;                      //是否可承作交易
    @XmlElement
	private String CUST_ID;                     //客戶ID
    @XmlElement
	private String BRANCH_NBR;                  //分行別
    @XmlElement
	private String TRADE_DATE;                    //交易日期
    @XmlElement
	private String EFF_DATE;                      //生效日期
    @XmlElement
	private String TRUST_ACCT;                  //信託帳號
    @XmlElement
	private String DEBIT_ACCT;                  //扣款帳號
    @XmlElement
	private String CERTIFICATE_ID;
    @XmlElement
	private String PAY_TYPE;                    //繳款方式
    @XmlElement
	private String CONFIRM;                     //確認碼
    @XmlElement
	private String BATCH_SEQ;                   //批號
    @XmlElement
	private String EMP_ID;                      //建機人員
    @XmlElement
	private String NARRATOR_ID;                 //解說人員
    @XmlElement
	private String PROSPECTUS_TYPE;             //公開說明書交付方式
    @XmlElement
   	private String TRUST_CURR;                  //投資幣別
    @XmlElement
	private String FEE;							//手續費
	@XmlElement
	private String FEE_RATE;					//手續費率    
    @XmlElement
	private String PROD_ID;                   	//母基金代號
    @XmlElement
	private String PURCHASE_AMT;        		//母基金扣款金額
    @XmlElement
	private String REC_SEQ;                     //錄音序號
    @XmlElement
	private String BARGAIN_APPLY_SEQ;         	//議價編號
    @XmlElement
	private String FLAG_NUMBER;         		//客戶申貸紀錄 系統發查90天內的貸款紀錄，若為Y，則客戶申貸紀錄自動顯示Y，其餘為N
    @XmlElement
	private String RESERVE_COL_1;                //保留欄位1
    @XmlElement
	private String RESERVE_COL_2;                //保留欄位2
    
    
	public String getERR_COD() {
		return ERR_COD;
	}
	public void setERR_COD(String eRR_COD) {
		ERR_COD = eRR_COD;
	}
	public String getERR_TXT() {
		return ERR_TXT;
	}
	public void setERR_TXT(String eRR_TXT) {
		ERR_TXT = eRR_TXT;
	}
	public String getTX_FLG() {
		return TX_FLG;
	}
	public void setTX_FLG(String tX_FLG) {
		TX_FLG = tX_FLG;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public String getBRANCH_NBR() {
		return BRANCH_NBR;
	}
	public void setBRANCH_NBR(String bRANCH_NBR) {
		BRANCH_NBR = bRANCH_NBR;
	}
	public String getTRADE_DATE() {
		return TRADE_DATE;
	}
	public void setTRADE_DATE(String tRADE_DATE) {
		TRADE_DATE = tRADE_DATE;
	}
	public String getEFF_DATE() {
		return EFF_DATE;
	}
	public void setEFF_DATE(String eFF_DATE) {
		EFF_DATE = eFF_DATE;
	}
	public String getTRUST_ACCT() {
		return TRUST_ACCT;
	}
	public void setTRUST_ACCT(String tRUST_ACCT) {
		TRUST_ACCT = tRUST_ACCT;
	}
	public String getCERTIFICATE_ID() {
		return CERTIFICATE_ID;
	}
	public void setCERTIFICATE_ID(String cERTIFICATE_ID) {
		CERTIFICATE_ID = cERTIFICATE_ID;
	}
	public String getDEBIT_ACCT() {
		return DEBIT_ACCT;
	}
	public void setDEBIT_ACCT(String dEBIT_ACCT) {
		DEBIT_ACCT = dEBIT_ACCT;
	}
	public String getPAY_TYPE() {
		return PAY_TYPE;
	}
	public void setPAY_TYPE(String pAY_TYPE) {
		PAY_TYPE = pAY_TYPE;
	}
	public String getCONFIRM() {
		return CONFIRM;
	}
	public void setCONFIRM(String cONFIRM) {
		CONFIRM = cONFIRM;
	}
	public String getBATCH_SEQ() {
		return BATCH_SEQ;
	}
	public void setBATCH_SEQ(String bATCH_SEQ) {
		BATCH_SEQ = bATCH_SEQ;
	}
	public String getEMP_ID() {
		return EMP_ID;
	}
	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}
	public String getNARRATOR_ID() {
		return NARRATOR_ID;
	}
	public void setNARRATOR_ID(String nARRATOR_ID) {
		NARRATOR_ID = nARRATOR_ID;
	}
	public String getPROSPECTUS_TYPE() {
		return PROSPECTUS_TYPE;
	}
	public void setPROSPECTUS_TYPE(String pROSPECTUS_TYPE) {
		PROSPECTUS_TYPE = pROSPECTUS_TYPE;
	}
	public String getTRUST_CURR() {
		return TRUST_CURR;
	}
	public void setTRUST_CURR(String tRUST_CURR) {
		TRUST_CURR = tRUST_CURR;
	}
	public String getFEE() {
		return FEE;
	}
	public void setFEE(String fEE) {
		FEE = fEE;
	}
	public String getFEE_RATE() {
		return FEE_RATE;
	}
	public void setFEE_RATE(String fEE_RATE) {
		FEE_RATE = fEE_RATE;
	}
	public String getPROD_ID() {
		return PROD_ID;
	}
	public void setPROD_ID(String pROD_ID) {
		PROD_ID = pROD_ID;
	}
	public String getPURCHASE_AMT() {
		return PURCHASE_AMT;
	}
	public void setPURCHASE_AMT(String pURCHASE_AMT) {
		PURCHASE_AMT = pURCHASE_AMT;
	}
	public String getREC_SEQ() {
		return REC_SEQ;
	}
	public void setREC_SEQ(String rEC_SEQ) {
		REC_SEQ = rEC_SEQ;
	}
	public String getBARGAIN_APPLY_SEQ() {
		return BARGAIN_APPLY_SEQ;
	}
	public void setBARGAIN_APPLY_SEQ(String bARGAIN_APPLY_SEQ) {
		BARGAIN_APPLY_SEQ = bARGAIN_APPLY_SEQ;
	}
	public String getFLAG_NUMBER() {
		return FLAG_NUMBER;
	}
	public void setFLAG_NUMBER(String fLAG_NUMBER) {
		FLAG_NUMBER = fLAG_NUMBER;
	}
	public String getRESERVE_COL_1() {
		return RESERVE_COL_1;
	}
	public void setRESERVE_COL_1(String rESERVE_COL_1) {
		RESERVE_COL_1 = rESERVE_COL_1;
	}
	public String getRESERVE_COL_2() {
		return RESERVE_COL_2;
	}
	public void setRESERVE_COL_2(String rESERVE_COL_2) {
		RESERVE_COL_2 = rESERVE_COL_2;
	}
	
}
