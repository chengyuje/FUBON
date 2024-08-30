package com.systex.jbranch.fubon.commons.esb.vo.nfbrn1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;

/**
 * Created by SebastianWu on 2016/10/6.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRN1InputVO {
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
	private String TRUST_CURR_TYPE;             //信託業務別
    @XmlElement
	private String TRADE_SUB_TYPE;              //交易類別
    @XmlElement
	private String TRADE_DATE;                    //交易日期
    @XmlElement
	private String EFF_DATE;                      //生效日期
    @XmlElement
	private String TRUST_ACCT;                  //信託帳號
    @XmlElement
	private String CREDIT_ACCT;                 //收益入帳帳號
    @XmlElement
	private String DEBIT_ID;                    //扣款ID
    @XmlElement
	private String DEBIT_ACCT;                  //扣款帳號
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
	private String FEE_RATE_1;				//手續費率
	@XmlElement
	private String FEE_RATE_2;				//手續費率2
	@XmlElement
	private String FEE_RATE_3;				//手續費率3
	
	
	@XmlElement
	private String FEE_1;				//手續費1
	@XmlElement
	private String FEE_2;				//手續費2
	@XmlElement
	private String FEE_3;				//手續費3
	@XmlElement
	private String FEE_M_1; 			// C74 手續費1(中)
	@XmlElement
	private String FEE_RATE_M_1; 		// C75 手續費率1(中)
	@XmlElement
	private String FEE_H_1; 			// C76 手續費1(高)
	@XmlElement
	private String FEE_RATE_H_1; 		// C77 手續費率1(高)
	@XmlElement
	private String FEE_M_2; 			// C78 手續費2(中)
	@XmlElement
	private String FEE_RATE_M_2; 		// C79 手續費率2(中)
	@XmlElement
	private String FEE_H_2; 			// C80 手續費2(高)
	@XmlElement
	private String FEE_RATE_H_2; 		// C81 手續費率2(高)
	@XmlElement
	private String FEE_M_3; 			// C82 手續費3(中)
	@XmlElement
	private String FEE_RATE_M_3; 		// C83 手續費率3(中)
	@XmlElement
	private String FEE_H_3; 			// C84 手續費3(高)
	@XmlElement
	private String FEE_RATE_H_3; 		// C85 手續費率3(高)
    @XmlElement
	private String PROSPECTUS_TYPE;             //公開說明書交付方式
    @XmlElement
	private String PROD_ID_1;                   //1基金代號/基金套餐
    @XmlElement
	private String GROUP_IFA_1;                 //1優惠團體代碼
    @XmlElement
	private String PURCHASE_AMT_L_1;        	//1扣款金額低
    @XmlElement
	private String PURCHASE_AMT_M_1;        	//1扣款金額中
    @XmlElement
	private String PURCHASE_AMT_H_1;        	//1扣款金額高
    @XmlElement
	private String CHARGE_DATE_1_1;             //1約定扣款日1
    @XmlElement
	private String CHARGE_DATE_2_1;             //1約定扣款日2
    @XmlElement
	private String CHARGE_DATE_3_1;             //1約定扣款日3
    @XmlElement
	private String CHARGE_DATE_4_1;             //1約定扣款日4
    @XmlElement
	private String CHARGE_DATE_5_1;             //1約定扣款日5
    @XmlElement
	private String CHARGE_DATE_6_1;             //1約定扣款日6
    @XmlElement
	private String PROD_ID_2;                   //2基金代號/基金套餐
    @XmlElement
	private String GROUP_IFA_2;                 //2優惠團體代碼
    @XmlElement
	private String PURCHASE_AMT_L_2;        	//2扣款金額低
    @XmlElement
	private String PURCHASE_AMT_M_2;        	//2扣款金額中
    @XmlElement
	private String PURCHASE_AMT_H_2;        	//2扣款金額高
    @XmlElement
	private String CHARGE_DATE_1_2;             //2約定扣款日1
    @XmlElement
	private String CHARGE_DATE_2_2;             //2約定扣款日2
    @XmlElement
	private String CHARGE_DATE_3_2;             //2約定扣款日3
    @XmlElement
	private String CHARGE_DATE_4_2;             //2約定扣款日4
    @XmlElement
	private String CHARGE_DATE_5_2;             //2約定扣款日5
    @XmlElement
	private String CHARGE_DATE_6_2;             //2約定扣款日6
    @XmlElement
	private String PROD_ID_3;                   //3基金代號/基金套餐
    @XmlElement
	private String GROUP_IFA_3;                 //3優惠團體代碼
    @XmlElement
	private String PURCHASE_AMT_L_3;        	//3扣款金額低
    @XmlElement
	private String PURCHASE_AMT_M_3;        	//3扣款金額中
    @XmlElement
	private String PURCHASE_AMT_H_3;        	//3扣款金額高
    @XmlElement
	private String CHARGE_DATE_1_3;             //3約定扣款日1
    @XmlElement
	private String CHARGE_DATE_2_3;             //3約定扣款日2
    @XmlElement
	private String CHARGE_DATE_3_3;             //3約定扣款日3
    @XmlElement
	private String CHARGE_DATE_4_3;             //3約定扣款日4
    @XmlElement
	private String CHARGE_DATE_5_3;             //3約定扣款日5
    @XmlElement
	private String CHARGE_DATE_6_3;             //3約定扣款日6
    @XmlElement
	private String BTH_COUPON_1;                //生日券1
    @XmlElement
	private String BTH_COUPON_2;                //生日券2
    @XmlElement
	private String BTH_COUPON_3;                //生日券3
    @XmlElement
	private String AUTO_CX_1;                   //自動換匯1
    @XmlElement
	private String AUTO_CX_2;                   //自動換匯2
    @XmlElement
	private String AUTO_CX_3;                   //自動換匯3
    @XmlElement
	private String REC_SEQ;                     //錄音序號
    @XmlElement
	private String IS_PL_1;                     //是否停損停利1
    @XmlElement
	private String TAKE_PROFIT_PERC_1;      	//停利1
    @XmlElement
	private String TAKE_PROFIT_SYM_1;           //停利符號1
    @XmlElement
	private String STOP_LOSS_PERC_1;        	//停損1
    @XmlElement
	private String STOP_LOSS_SYM_1;             //停損符號1
    @XmlElement
	private String IS_PL_2;                     //是否停損停利2
    @XmlElement
	private String TAKE_PROFIT_PERC_2;      	//停利2
    @XmlElement
	private String TAKE_PROFIT_SYM_2;           //停利符號2
    @XmlElement
	private String STOP_LOSS_PERC_2;        	//停損2
    @XmlElement
	private String STOP_LOSS_SYM_2;             //停損符號2
    @XmlElement
	private String IS_PL_3;                     //是否停損停利3
    @XmlElement
	private String TAKE_PROFIT_PERC_3;      	//停利3
    @XmlElement
	private String TAKE_PROFIT_SYM_3;           //停利符號3
    @XmlElement
	private String STOP_LOSS_PERC_3;        	//停損3
    @XmlElement
	private String STOP_LOSS_SYM_3;             //停損符號3
    @XmlElement
	private String BARGAIN_APPLY_SEQ_1;         //議價編號1
    @XmlElement
	private String BARGAIN_APPLY_SEQ_2;         //議價編號2
    @XmlElement
	private String BARGAIN_APPLY_SEQ_3;         //議價編號3
    @XmlElement
	private String FLAG_NUMBER;         //客戶申貸紀錄 系統發查90天內的貸款紀錄，若為Y，則客戶申貸紀錄自動顯示Y，其餘為N
    
    
	public String getFEE_RATE_2() {
		return FEE_RATE_2;
	}
	public void setFEE_RATE_2(String fEE_RATE_2) {
		FEE_RATE_2 = fEE_RATE_2;
	}
	public String getFEE_RATE_3() {
		return FEE_RATE_3;
	}
	public void setFEE_RATE_3(String fEE_RATE_3) {
		FEE_RATE_3 = fEE_RATE_3;
	}
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
	public String getTRUST_CURR_TYPE() {
		return TRUST_CURR_TYPE;
	}
	public void setTRUST_CURR_TYPE(String tRUST_CURR_TYPE) {
		TRUST_CURR_TYPE = tRUST_CURR_TYPE;
	}
	public String getTRADE_SUB_TYPE() {
		return TRADE_SUB_TYPE;
	}
	public void setTRADE_SUB_TYPE(String tRADE_SUB_TYPE) {
		TRADE_SUB_TYPE = tRADE_SUB_TYPE;
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
	public String getCREDIT_ACCT() {
		return CREDIT_ACCT;
	}
	public void setCREDIT_ACCT(String cREDIT_ACCT) {
		CREDIT_ACCT = cREDIT_ACCT;
	}
	public String getDEBIT_ID() {
		return DEBIT_ID;
	}
	public void setDEBIT_ID(String dEBIT_ID) {
		DEBIT_ID = dEBIT_ID;
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

	public String getFEE_RATE_1() {
		return FEE_RATE_1;
	}

	public void setFEE_RATE_1(String FEE_RATE_1) {
		this.FEE_RATE_1 = FEE_RATE_1;
	}

	public String getPROSPECTUS_TYPE() {
		return PROSPECTUS_TYPE;
	}
	public void setPROSPECTUS_TYPE(String pROSPECTUS_TYPE) {
		PROSPECTUS_TYPE = pROSPECTUS_TYPE;
	}
	public String getPROD_ID_1() {
		return PROD_ID_1;
	}
	public void setPROD_ID_1(String pROD_ID_1) {
		PROD_ID_1 = pROD_ID_1;
	}
	public String getGROUP_IFA_1() {
		return GROUP_IFA_1;
	}
	public void setGROUP_IFA_1(String gROUP_IFA_1) {
		GROUP_IFA_1 = gROUP_IFA_1;
	}
	public String getPURCHASE_AMT_L_1() {
		return PURCHASE_AMT_L_1;
	}
	public void setPURCHASE_AMT_L_1(String pURCHASE_AMT_L_1) {
		PURCHASE_AMT_L_1 = pURCHASE_AMT_L_1;
	}
	public String getPURCHASE_AMT_M_1() {
		return PURCHASE_AMT_M_1;
	}
	public void setPURCHASE_AMT_M_1(String pURCHASE_AMT_M_1) {
		PURCHASE_AMT_M_1 = pURCHASE_AMT_M_1;
	}
	public String getPURCHASE_AMT_H_1() {
		return PURCHASE_AMT_H_1;
	}
	public void setPURCHASE_AMT_H_1(String pURCHASE_AMT_H_1) {
		PURCHASE_AMT_H_1 = pURCHASE_AMT_H_1;
	}
	public String getCHARGE_DATE_1_1() {
		return CHARGE_DATE_1_1;
	}
	public void setCHARGE_DATE_1_1(String cHARGE_DATE_1_1) {
		CHARGE_DATE_1_1 = cHARGE_DATE_1_1;
	}
	public String getCHARGE_DATE_2_1() {
		return CHARGE_DATE_2_1;
	}
	public void setCHARGE_DATE_2_1(String cHARGE_DATE_2_1) {
		CHARGE_DATE_2_1 = cHARGE_DATE_2_1;
	}
	public String getCHARGE_DATE_3_1() {
		return CHARGE_DATE_3_1;
	}
	public void setCHARGE_DATE_3_1(String cHARGE_DATE_3_1) {
		CHARGE_DATE_3_1 = cHARGE_DATE_3_1;
	}
	public String getCHARGE_DATE_4_1() {
		return CHARGE_DATE_4_1;
	}
	public void setCHARGE_DATE_4_1(String cHARGE_DATE_4_1) {
		CHARGE_DATE_4_1 = cHARGE_DATE_4_1;
	}
	public String getCHARGE_DATE_5_1() {
		return CHARGE_DATE_5_1;
	}
	public void setCHARGE_DATE_5_1(String cHARGE_DATE_5_1) {
		CHARGE_DATE_5_1 = cHARGE_DATE_5_1;
	}
	public String getCHARGE_DATE_6_1() {
		return CHARGE_DATE_6_1;
	}
	public void setCHARGE_DATE_6_1(String cHARGE_DATE_6_1) {
		CHARGE_DATE_6_1 = cHARGE_DATE_6_1;
	}
	public String getPROD_ID_2() {
		return PROD_ID_2;
	}
	public void setPROD_ID_2(String pROD_ID_2) {
		PROD_ID_2 = pROD_ID_2;
	}
	public String getGROUP_IFA_2() {
		return GROUP_IFA_2;
	}
	public void setGROUP_IFA_2(String gROUP_IFA_2) {
		GROUP_IFA_2 = gROUP_IFA_2;
	}
	public String getPURCHASE_AMT_L_2() {
		return PURCHASE_AMT_L_2;
	}
	public void setPURCHASE_AMT_L_2(String pURCHASE_AMT_L_2) {
		PURCHASE_AMT_L_2 = pURCHASE_AMT_L_2;
	}
	public String getPURCHASE_AMT_M_2() {
		return PURCHASE_AMT_M_2;
	}
	public void setPURCHASE_AMT_M_2(String pURCHASE_AMT_M_2) {
		PURCHASE_AMT_M_2 = pURCHASE_AMT_M_2;
	}
	public String getPURCHASE_AMT_H_2() {
		return PURCHASE_AMT_H_2;
	}
	public void setPURCHASE_AMT_H_2(String pURCHASE_AMT_H_2) {
		PURCHASE_AMT_H_2 = pURCHASE_AMT_H_2;
	}
	public String getCHARGE_DATE_1_2() {
		return CHARGE_DATE_1_2;
	}
	public void setCHARGE_DATE_1_2(String cHARGE_DATE_1_2) {
		CHARGE_DATE_1_2 = cHARGE_DATE_1_2;
	}
	public String getCHARGE_DATE_2_2() {
		return CHARGE_DATE_2_2;
	}
	public void setCHARGE_DATE_2_2(String cHARGE_DATE_2_2) {
		CHARGE_DATE_2_2 = cHARGE_DATE_2_2;
	}
	public String getCHARGE_DATE_3_2() {
		return CHARGE_DATE_3_2;
	}
	public void setCHARGE_DATE_3_2(String cHARGE_DATE_3_2) {
		CHARGE_DATE_3_2 = cHARGE_DATE_3_2;
	}
	public String getCHARGE_DATE_4_2() {
		return CHARGE_DATE_4_2;
	}
	public void setCHARGE_DATE_4_2(String cHARGE_DATE_4_2) {
		CHARGE_DATE_4_2 = cHARGE_DATE_4_2;
	}
	public String getCHARGE_DATE_5_2() {
		return CHARGE_DATE_5_2;
	}
	public void setCHARGE_DATE_5_2(String cHARGE_DATE_5_2) {
		CHARGE_DATE_5_2 = cHARGE_DATE_5_2;
	}
	public String getCHARGE_DATE_6_2() {
		return CHARGE_DATE_6_2;
	}
	public void setCHARGE_DATE_6_2(String cHARGE_DATE_6_2) {
		CHARGE_DATE_6_2 = cHARGE_DATE_6_2;
	}
	public String getPROD_ID_3() {
		return PROD_ID_3;
	}
	public void setPROD_ID_3(String pROD_ID_3) {
		PROD_ID_3 = pROD_ID_3;
	}
	public String getGROUP_IFA_3() {
		return GROUP_IFA_3;
	}
	public void setGROUP_IFA_3(String gROUP_IFA_3) {
		GROUP_IFA_3 = gROUP_IFA_3;
	}
	public String getPURCHASE_AMT_L_3() {
		return PURCHASE_AMT_L_3;
	}
	public void setPURCHASE_AMT_L_3(String pURCHASE_AMT_L_3) {
		PURCHASE_AMT_L_3 = pURCHASE_AMT_L_3;
	}
	public String getPURCHASE_AMT_M_3() {
		return PURCHASE_AMT_M_3;
	}
	public void setPURCHASE_AMT_M_3(String pURCHASE_AMT_M_3) {
		PURCHASE_AMT_M_3 = pURCHASE_AMT_M_3;
	}
	public String getPURCHASE_AMT_H_3() {
		return PURCHASE_AMT_H_3;
	}
	public void setPURCHASE_AMT_H_3(String pURCHASE_AMT_H_3) {
		PURCHASE_AMT_H_3 = pURCHASE_AMT_H_3;
	}
	public String getCHARGE_DATE_1_3() {
		return CHARGE_DATE_1_3;
	}
	public void setCHARGE_DATE_1_3(String cHARGE_DATE_1_3) {
		CHARGE_DATE_1_3 = cHARGE_DATE_1_3;
	}
	public String getCHARGE_DATE_2_3() {
		return CHARGE_DATE_2_3;
	}
	public void setCHARGE_DATE_2_3(String cHARGE_DATE_2_3) {
		CHARGE_DATE_2_3 = cHARGE_DATE_2_3;
	}
	public String getCHARGE_DATE_3_3() {
		return CHARGE_DATE_3_3;
	}
	public void setCHARGE_DATE_3_3(String cHARGE_DATE_3_3) {
		CHARGE_DATE_3_3 = cHARGE_DATE_3_3;
	}
	public String getCHARGE_DATE_4_3() {
		return CHARGE_DATE_4_3;
	}
	public void setCHARGE_DATE_4_3(String cHARGE_DATE_4_3) {
		CHARGE_DATE_4_3 = cHARGE_DATE_4_3;
	}
	public String getCHARGE_DATE_5_3() {
		return CHARGE_DATE_5_3;
	}
	public void setCHARGE_DATE_5_3(String cHARGE_DATE_5_3) {
		CHARGE_DATE_5_3 = cHARGE_DATE_5_3;
	}
	public String getCHARGE_DATE_6_3() {
		return CHARGE_DATE_6_3;
	}
	public void setCHARGE_DATE_6_3(String cHARGE_DATE_6_3) {
		CHARGE_DATE_6_3 = cHARGE_DATE_6_3;
	}
	public String getBTH_COUPON_1() {
		return BTH_COUPON_1;
	}
	public void setBTH_COUPON_1(String bTH_COUPON_1) {
		BTH_COUPON_1 = bTH_COUPON_1;
	}
	public String getBTH_COUPON_2() {
		return BTH_COUPON_2;
	}
	public void setBTH_COUPON_2(String bTH_COUPON_2) {
		BTH_COUPON_2 = bTH_COUPON_2;
	}
	public String getBTH_COUPON_3() {
		return BTH_COUPON_3;
	}
	public void setBTH_COUPON_3(String bTH_COUPON_3) {
		BTH_COUPON_3 = bTH_COUPON_3;
	}
	public String getAUTO_CX_1() {
		return AUTO_CX_1;
	}
	public void setAUTO_CX_1(String aUTO_CX_1) {
		AUTO_CX_1 = aUTO_CX_1;
	}
	public String getAUTO_CX_2() {
		return AUTO_CX_2;
	}
	public void setAUTO_CX_2(String aUTO_CX_2) {
		AUTO_CX_2 = aUTO_CX_2;
	}
	public String getAUTO_CX_3() {
		return AUTO_CX_3;
	}
	public void setAUTO_CX_3(String aUTO_CX_3) {
		AUTO_CX_3 = aUTO_CX_3;
	}
	public String getREC_SEQ() {
		return REC_SEQ;
	}
	public void setREC_SEQ(String rEC_SEQ) {
		REC_SEQ = rEC_SEQ;
	}
	public String getIS_PL_1() {
		return IS_PL_1;
	}
	public void setIS_PL_1(String iS_PL_1) {
		IS_PL_1 = iS_PL_1;
	}
	public String getTAKE_PROFIT_PERC_1() {
		return TAKE_PROFIT_PERC_1;
	}
	public void setTAKE_PROFIT_PERC_1(String tAKE_PROFIT_PERC_1) {
		TAKE_PROFIT_PERC_1 = tAKE_PROFIT_PERC_1;
	}
	public String getTAKE_PROFIT_SYM_1() {
		return TAKE_PROFIT_SYM_1;
	}
	public void setTAKE_PROFIT_SYM_1(String tAKE_PROFIT_SYM_1) {
		TAKE_PROFIT_SYM_1 = tAKE_PROFIT_SYM_1;
	}
	public String getSTOP_LOSS_PERC_1() {
		return STOP_LOSS_PERC_1;
	}
	public void setSTOP_LOSS_PERC_1(String sTOP_LOSS_PERC_1) {
		STOP_LOSS_PERC_1 = sTOP_LOSS_PERC_1;
	}
	public String getSTOP_LOSS_SYM_1() {
		return STOP_LOSS_SYM_1;
	}
	public void setSTOP_LOSS_SYM_1(String sTOP_LOSS_SYM_1) {
		STOP_LOSS_SYM_1 = sTOP_LOSS_SYM_1;
	}
	public String getIS_PL_2() {
		return IS_PL_2;
	}
	public void setIS_PL_2(String iS_PL_2) {
		IS_PL_2 = iS_PL_2;
	}
	public String getTAKE_PROFIT_PERC_2() {
		return TAKE_PROFIT_PERC_2;
	}
	public void setTAKE_PROFIT_PERC_2(String tAKE_PROFIT_PERC_2) {
		TAKE_PROFIT_PERC_2 = tAKE_PROFIT_PERC_2;
	}
	public String getTAKE_PROFIT_SYM_2() {
		return TAKE_PROFIT_SYM_2;
	}
	public void setTAKE_PROFIT_SYM_2(String tAKE_PROFIT_SYM_2) {
		TAKE_PROFIT_SYM_2 = tAKE_PROFIT_SYM_2;
	}
	public String getSTOP_LOSS_PERC_2() {
		return STOP_LOSS_PERC_2;
	}
	public void setSTOP_LOSS_PERC_2(String sTOP_LOSS_PERC_2) {
		STOP_LOSS_PERC_2 = sTOP_LOSS_PERC_2;
	}
	public String getSTOP_LOSS_SYM_2() {
		return STOP_LOSS_SYM_2;
	}
	public void setSTOP_LOSS_SYM_2(String sTOP_LOSS_SYM_2) {
		STOP_LOSS_SYM_2 = sTOP_LOSS_SYM_2;
	}
	public String getIS_PL_3() {
		return IS_PL_3;
	}
	public void setIS_PL_3(String iS_PL_3) {
		IS_PL_3 = iS_PL_3;
	}
	public String getTAKE_PROFIT_PERC_3() {
		return TAKE_PROFIT_PERC_3;
	}
	public void setTAKE_PROFIT_PERC_3(String tAKE_PROFIT_PERC_3) {
		TAKE_PROFIT_PERC_3 = tAKE_PROFIT_PERC_3;
	}
	public String getTAKE_PROFIT_SYM_3() {
		return TAKE_PROFIT_SYM_3;
	}
	public void setTAKE_PROFIT_SYM_3(String tAKE_PROFIT_SYM_3) {
		TAKE_PROFIT_SYM_3 = tAKE_PROFIT_SYM_3;
	}
	public String getSTOP_LOSS_PERC_3() {
		return STOP_LOSS_PERC_3;
	}
	public void setSTOP_LOSS_PERC_3(String sTOP_LOSS_PERC_3) {
		STOP_LOSS_PERC_3 = sTOP_LOSS_PERC_3;
	}
	public String getSTOP_LOSS_SYM_3() {
		return STOP_LOSS_SYM_3;
	}
	public void setSTOP_LOSS_SYM_3(String sTOP_LOSS_SYM_3) {
		STOP_LOSS_SYM_3 = sTOP_LOSS_SYM_3;
	}
	public String getBARGAIN_APPLY_SEQ_1() {
		return BARGAIN_APPLY_SEQ_1;
	}
	public void setBARGAIN_APPLY_SEQ_1(String bARGAIN_APPLY_SEQ_1) {
		BARGAIN_APPLY_SEQ_1 = bARGAIN_APPLY_SEQ_1;
	}
	public String getBARGAIN_APPLY_SEQ_2() {
		return BARGAIN_APPLY_SEQ_2;
	}
	public void setBARGAIN_APPLY_SEQ_2(String bARGAIN_APPLY_SEQ_2) {
		BARGAIN_APPLY_SEQ_2 = bARGAIN_APPLY_SEQ_2;
	}
	public String getBARGAIN_APPLY_SEQ_3() {
		return BARGAIN_APPLY_SEQ_3;
	}
	public void setBARGAIN_APPLY_SEQ_3(String bARGAIN_APPLY_SEQ_3) {
		BARGAIN_APPLY_SEQ_3 = bARGAIN_APPLY_SEQ_3;
	}
	public String getFEE_1() {
		return FEE_1;
	}
	public void setFEE_1(String fEE_1) {
		FEE_1 = fEE_1;
	}
	public String getFEE_2() {
		return FEE_2;
	}
	public void setFEE_2(String fEE_2) {
		FEE_2 = fEE_2;
	}
	public String getFEE_3() {
		return FEE_3;
	}
	public void setFEE_3(String fEE_3) {
		FEE_3 = fEE_3;
	}
	public String getFEE_M_1() {
		return FEE_M_1;
	}
	public void setFEE_M_1(String fEE_M_1) {
		FEE_M_1 = fEE_M_1;
	}
	public String getFEE_RATE_M_1() {
		return FEE_RATE_M_1;
	}
	public void setFEE_RATE_M_1(String fEE_RATE_M_1) {
		FEE_RATE_M_1 = fEE_RATE_M_1;
	}
	public String getFEE_H_1() {
		return FEE_H_1;
	}
	public void setFEE_H_1(String fEE_H_1) {
		FEE_H_1 = fEE_H_1;
	}
	public String getFEE_RATE_H_1() {
		return FEE_RATE_H_1;
	}
	public void setFEE_RATE_H_1(String fEE_RATE_H_1) {
		FEE_RATE_H_1 = fEE_RATE_H_1;
	}
	public String getFEE_M_2() {
		return FEE_M_2;
	}
	public void setFEE_M_2(String fEE_M_2) {
		FEE_M_2 = fEE_M_2;
	}
	public String getFEE_RATE_M_2() {
		return FEE_RATE_M_2;
	}
	public void setFEE_RATE_M_2(String fEE_RATE_M_2) {
		FEE_RATE_M_2 = fEE_RATE_M_2;
	}
	public String getFEE_H_2() {
		return FEE_H_2;
	}
	public void setFEE_H_2(String fEE_H_2) {
		FEE_H_2 = fEE_H_2;
	}
	public String getFEE_RATE_H_2() {
		return FEE_RATE_H_2;
	}
	public void setFEE_RATE_H_2(String fEE_RATE_H_2) {
		FEE_RATE_H_2 = fEE_RATE_H_2;
	}
	public String getFEE_M_3() {
		return FEE_M_3;
	}
	public void setFEE_M_3(String fEE_M_3) {
		FEE_M_3 = fEE_M_3;
	}
	public String getFEE_RATE_M_3() {
		return FEE_RATE_M_3;
	}
	public void setFEE_RATE_M_3(String fEE_RATE_M_3) {
		FEE_RATE_M_3 = fEE_RATE_M_3;
	}
	public String getFEE_H_3() {
		return FEE_H_3;
	}
	public void setFEE_H_3(String fEE_H_3) {
		FEE_H_3 = fEE_H_3;
	}
	public String getFEE_RATE_H_3() {
		return FEE_RATE_H_3;
	}
	public void setFEE_RATE_H_3(String fEE_RATE_H_3) {
		FEE_RATE_H_3 = fEE_RATE_H_3;
	}
	public String getFLAG_NUMBER() {
		return FLAG_NUMBER;
	}
	public void setFLAG_NUMBER(String fLAG_NUMBER) {
		FLAG_NUMBER = fLAG_NUMBER;
	}
	
	
	
}
