package com.systex.jbranch.fubon.commons.esb.vo.fc032151;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by SebastianWu on 2016/09/09.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class FC032151OutputVO {
	@XmlElement
	private String ADV_COD;	//債清註記
	@XmlElement
	private String REF_IDNO;//新舊ID
	@XmlElement
	private String CUST_NAME;//中文戶名
	@XmlElement
	private String BDAY; //生日
	@XmlElement
	private Date BDAY_D; //生日
	@XmlElement
	private String ORG_TYPE; //組織型態
	@XmlElement
	private String ORG_CHECK; //法人戶實際受益人
	@XmlElement
	private String STOCK_TYPE; //上市上櫃
	@XmlElement
	private String IDU_COD; //行業別
	@XmlElement
	private String ENTRP_TYPE; //企業規模
	@XmlElement
	private String SEX; //性別
	@XmlElement
	private String CUST_TYPE; //個人分類
	@XmlElement
	private String RESID_IDNO; //大陸居留證號碼
	@XmlElement
	private String RESID_IDN01; //外國人居留證號碼
	@XmlElement
	private String RESID_DATE; //外國人居留證核發日期
	@XmlElement
	private String RESID_EFF_DATE; //外國人居留證核發日期
	@XmlElement
	private String RISK_UNIT; //信評單位
	@XmlElement
	private String RISK_RANK                           ;//信用評等             
	@XmlElement
	private String RANK_DATE                           ;//評等日期            
	@XmlElement
	private String RISK_RATE                           ;//信用評分            
	@XmlElement
	private String IDU_COD2                            ;//授信行業別          
	@XmlElement
	private String RISK_DATE_01                        ;//內部信評評等日      
	@XmlElement
	private Date RISK_DATE_01_D                        ;//內部信評評等日      
	@XmlElement
	private String RISK_RATE_01                        ;//內部信評違約機率    
	@XmlElement
	private String PD_DATE                             ;//消金房貸信評評等日  
	@XmlElement
	private Date PD_DATE_D                             ;//消金房貸信評評等日  
	@XmlElement
	private String PD_RATE                             ;//消金房貸信評違約機率
	@XmlElement
	private String PD_RISK                             ;//消金房貸違約評等    
	@XmlElement
	private String CTRY_COD                            ;//國家代號            
	@XmlElement
	private String RESUME                              ;//學經歷              
	@XmlElement
	private String CUST_STS                            ;//企金戶況            
	@XmlElement
	private String COMPANY                             ;//服務單位            
	@XmlElement
	private String TITLE                               ;//職稱                
	@XmlElement
	private String BUS_ZIP                             ;//戶籍區號            
	@XmlElement
	private String BUS_ADDR_1                          ;//戶籍地址-1          
	@XmlElement
	private String BUS_ADDR_2                          ;//戶籍地址-2          
	@XmlElement
	private String CTT_ZIP                             ;//通訊區號            
	@XmlElement
	private String CTT_ADDR_1                          ;//通訊地址-1          
	@XmlElement
	private String CTT_ADDR_2                          ;//通訊地址-2          
	@XmlElement
	private String PAY_SLIP                            ;//扣繳憑單_功能       
	@XmlElement
	private String PAY_CODE                            ;//扣繳憑單代號        
	@XmlElement
	private String PAY_ZIP                             ;//扣繳憑單_區號       
	@XmlElement
	private String PAY_ADDR_1                          ;//扣繳憑單_地址-1     
	@XmlElement
	private String PAY_ADDR_2                          ;//扣繳憑單_地址-2     
	@XmlElement
	private String TEL_COD1                            ;//電話代碼1           
	@XmlElement
	private String TEL_NO1                             ;//電話號碼1           
	@XmlElement
	private String TEL_EXT1                            ;//電話號碼-轉接1      
	@XmlElement
	private String TEL_TYP1                            ;//電話號碼-類別1      
	@XmlElement
	private String TEL_DAY1                            ;//電話號碼-性質_日間1 
	@XmlElement
	private String TEL_NIGHT1                          ;//電話號碼-性質_晚間1 
	@XmlElement
	private String TEL_VOICE1                          ;//電話號碼-性質_語音1 
	@XmlElement
	private String TEL_FAX1                            ;//電話號碼-性質_傳真1 
	@XmlElement
	private String TEL_COD2                            ;//電話代碼1           
	@XmlElement
	private String TEL_NO2                             ;//電話號碼1           
	@XmlElement
	private String TEL_EXT2                            ;//電話號碼-轉接1      
	@XmlElement
	private String TEL_TYP2                            ;//電話號碼-類別1      
	@XmlElement
	private String TEL_DAY2                            ;//電話號碼-性質_日間1 
	@XmlElement
	private String TEL_NIGHT2                          ;//電話號碼-性質_晚間1 
	@XmlElement
	private String TEL_VOICE2                          ;//電話號碼-性質_語音1 
	@XmlElement
	private String TEL_FAX2                            ;//電話號碼-性質_傳真1 
	@XmlElement
	private String EMAIL_ADDR                          ;//E-MAIL地址          
	@XmlElement
	private String NO_EMAIL_FLG                        ;//不使用EMAIL註記     
	@XmlElement
	private String ENG_NAME                            ;//英文戶名            
	@XmlElement
	private String ENG_ZIP                             ;//英文區號            
	@XmlElement
	private String ENG_ADDR_1                          ;//英文地址_1          
	@XmlElement
	private String ENG_ADDR_2                          ;//英文地址_2          
	@XmlElement
	private String REG_CAP_VAL                         ;//登記資本額仟元      
	@XmlElement
	private String CAP_VAL_YY                          ;//實收資本額年        
	@XmlElement
	private String CAP_VAL_MM                          ;//實收資本額月        
	@XmlElement
	private String CAP_VAL                             ;//實收資本額仟元      
	@XmlElement
	private String EMP_CNT                             ;//員工人數            
	@XmlElement
	private String APROV_DATE                          ;//核准日期            
	@XmlElement
	private String BUS_ITEM                            ;//營業項目            
	@XmlElement
	private String CONTACT_PSON                        ;//聯絡人              
	@XmlElement
	private String RESP_ID                             ;//負責人身份證號      
	@XmlElement
	private String RESP_NAME                           ;//負責人戶名          
	@XmlElement
	private String RESP_BDAY                           ;//負責人生日          
	@XmlElement
	private String PRT_SLIP                            ;//是否列印扣繳憑單    
	@XmlElement
	private String FR_CMPID                            ;//在台母公司統編      
	@XmlElement
	private String FR_INTNO                            ;//報送單位內部編號    
	@XmlElement
	private String FR_REGNO                            ;//所在地註冊編號      
	@XmlElement
	private String BILLS_CHECK                         ;//綜合電子帳單註記    
	@XmlElement
	private String SRC_TYP                             ;//申請單位            
	@XmlElement
	private String LOST_FLG                            ;//客戶特殊註記        
	@XmlElement
	private String BILLS_UPD_DATE                      ;//電子帳單異動日      
	@XmlElement
	private Date BILLS_UPD_DATE_D                    ;//電子帳單異動日      
	@XmlElement
	private String BILLS_STR_DATE                      ;//電子帳單申請日      
	@XmlElement
	private Date BILLS_STR_DATE_D                    ;//電子帳單申請日      
	@XmlElement
	private String BILLS_END_DATE                      ;//電子帳單終止日      
	@XmlElement
	private Date BILLS_END_DATE_D                    ;//電子帳單終止日      
	@XmlElement
	private String LST_TX_BRH1                         ;//最近往來分行        
	@XmlElement
	private String LST_TX_DATE1                        ;//最近往來日期        
	@XmlElement
	private Date LST_TX_DATE1_D                        ;//最近往來日期        
	@XmlElement
	private String LST_TX_BRH2                         ;//最近往來分行        
	@XmlElement
	private String LST_TX_DATE2                        ;//最近往來日期        
	@XmlElement
	private Date LST_TX_DATE2_D                        ;//最近往來日期        
	@XmlElement
	private String LST_TX_BRH3                         ;//最近往來分行        
	@XmlElement
	private String LST_TX_DATE3                        ;//最近往來日期        
	@XmlElement
	private Date LST_TX_DATE3_D                        ;//最近往來日期        
	@XmlElement
	private String LST_TX_BRH4                         ;//最近往來分行        
	@XmlElement
	private String LST_TX_DATE4                        ;//最近往來日期        
	@XmlElement
	private String LST_TX_BRH5                         ;//最近往來分行        
	@XmlElement
	private String LST_TX_DATE5                        ;//最近往來日期        
	@XmlElement
	private Date LST_TX_DATE5_D                       ;//最近往來日期        
	@XmlElement
	private String LST_TX_BRH6                         ;//最近往來分行        
	@XmlElement
	private String LST_TX_DATE6                        ;//最近往來日期        
	@XmlElement
	private Date LST_TX_DATE6_D                       ;//最近往來日期        
	@XmlElement
	private String MARRAGE                             ;//婚姻                
	@XmlElement
	private String CHILD_NO                            ;//子女人數            
	@XmlElement
	private String EDUCATION                           ;//學歷                
	@XmlElement
	private String CAREER                              ;//職業                
	@XmlElement
	private String OLD_CUST_NO                         ;//舊客戶統一編號      
	@XmlElement
	private String EDM_APPV                            ;//ＥＤＭ註記          
	@XmlElement
	private String DM_APPV                             ;//ＤＭ註記            
	@XmlElement
	private String TM_APPV                             ;//ＴＭ註記            
	@XmlElement
	private String SMS_APPV                            ;//ＳＭＳ註記          
	@XmlElement
	private String CUST_STS_01                         ;//特殊戶況說明        
	@XmlElement
	private String TD_NOTIFY                           ;//定存到期EMAIL通知   
	@XmlElement
	private String PROT_NAME                           ;//禁治產之監護人      
	@XmlElement
	private String RATE_TYP                            ;//稅率別              
	@XmlElement
	private String MTN_BRH_RAT                         ;//異動行              
	@XmlElement
	private String MTN_DATE_RAT                        ;//異動日              
	@XmlElement
	private Date MTN_DATE_RAT_D                        ;//異動日              
	@XmlElement
	private String RATE                                ;//扣繳稅率％          
	@XmlElement
	private String RATE_YYY                            ;//稅率年度   
	
	
	
	public String getADV_COD() {
		return ADV_COD;
	}
	public void setADV_COD(String aDV_COD) {
		ADV_COD = aDV_COD;
	}
	public String getREF_IDNO() {
		return REF_IDNO;
	}
	public void setREF_IDNO(String rEF_IDNO) {
		REF_IDNO = rEF_IDNO;
	}
	public String getCUST_NAME() {
		return CUST_NAME;
	}
	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}
	public String getBDAY() {
		return BDAY;
	}
	public void setBDAY(String bDAY) {
		BDAY = bDAY;
	}
	public String getORG_TYPE() {
		return ORG_TYPE;
	}
	public void setORG_TYPE(String oRG_TYPE) {
		ORG_TYPE = oRG_TYPE;
	}
	public String getORG_CHECK() {
		return ORG_CHECK;
	}
	public void setORG_CHECK(String oRG_CHECK) {
		ORG_CHECK = oRG_CHECK;
	}
	public String getSTOCK_TYPE() {
		return STOCK_TYPE;
	}
	public void setSTOCK_TYPE(String sTOCK_TYPE) {
		STOCK_TYPE = sTOCK_TYPE;
	}
	public String getIDU_COD() {
		return IDU_COD;
	}
	public void setIDU_COD(String iDU_COD) {
		IDU_COD = iDU_COD;
	}
	public String getENTRP_TYPE() {
		return ENTRP_TYPE;
	}
	public void setENTRP_TYPE(String eNTRP_TYPE) {
		ENTRP_TYPE = eNTRP_TYPE;
	}
	public String getSEX() {
		return SEX;
	}
	public void setSEX(String sEX) {
		SEX = sEX;
	}
	public String getCUST_TYPE() {
		return CUST_TYPE;
	}
	public void setCUST_TYPE(String cUST_TYPE) {
		CUST_TYPE = cUST_TYPE;
	}
	public String getRESID_IDNO() {
		return RESID_IDNO;
	}
	public void setRESID_IDNO(String rESID_IDNO) {
		RESID_IDNO = rESID_IDNO;
	}
	public String getRESID_IDN01() {
		return RESID_IDN01;
	}
	public void setRESID_IDN01(String rESID_IDN01) {
		RESID_IDN01 = rESID_IDN01;
	}
	public String getRESID_DATE() {
		return RESID_DATE;
	}
	public void setRESID_DATE(String rESID_DATE) {
		RESID_DATE = rESID_DATE;
	}
	public String getRESID_EFF_DATE() {
		return RESID_EFF_DATE;
	}
	public void setRESID_EFF_DATE(String rESID_EFF_DATE) {
		RESID_EFF_DATE = rESID_EFF_DATE;
	}
	public String getRISK_UNIT() {
		return RISK_UNIT;
	}
	public void setRISK_UNIT(String rISK_UNIT) {
		RISK_UNIT = rISK_UNIT;
	}
	public String getRISK_RANK() {
		return RISK_RANK;
	}
	public void setRISK_RANK(String rISK_RANK) {
		RISK_RANK = rISK_RANK;
	}
	public String getRANK_DATE() {
		return RANK_DATE;
	}
	public void setRANK_DATE(String rANK_DATE) {
		RANK_DATE = rANK_DATE;
	}
	public String getRISK_RATE() {
		return RISK_RATE;
	}
	public void setRISK_RATE(String rISK_RATE) {
		RISK_RATE = rISK_RATE;
	}
	public String getIDU_COD2() {
		return IDU_COD2;
	}
	public void setIDU_COD2(String iDU_COD2) {
		IDU_COD2 = iDU_COD2;
	}
	public String getRISK_DATE_01() {
		return RISK_DATE_01;
	}
	public void setRISK_DATE_01(String rISK_DATE_01) {
		RISK_DATE_01 = rISK_DATE_01;
	}
	public String getRISK_RATE_01() {
		return RISK_RATE_01;
	}
	public void setRISK_RATE_01(String rISK_RATE_01) {
		RISK_RATE_01 = rISK_RATE_01;
	}
	public String getPD_DATE() {
		return PD_DATE;
	}
	public void setPD_DATE(String pD_DATE) {
		PD_DATE = pD_DATE;
	}
	public String getPD_RATE() {
		return PD_RATE;
	}
	public void setPD_RATE(String pD_RATE) {
		PD_RATE = pD_RATE;
	}
	public String getPD_RISK() {
		return PD_RISK;
	}
	public void setPD_RISK(String pD_RISK) {
		PD_RISK = pD_RISK;
	}
	public String getCTRY_COD() {
		return CTRY_COD;
	}
	public void setCTRY_COD(String cTRY_COD) {
		CTRY_COD = cTRY_COD;
	}
	public String getRESUME() {
		return RESUME;
	}
	public void setRESUME(String rESUME) {
		RESUME = rESUME;
	}
	public String getCUST_STS() {
		return CUST_STS;
	}
	public void setCUST_STS(String cUST_STS) {
		CUST_STS = cUST_STS;
	}
	public String getCOMPANY() {
		return COMPANY;
	}
	public void setCOMPANY(String cOMPANY) {
		COMPANY = cOMPANY;
	}
	public String getTITLE() {
		return TITLE;
	}
	public void setTITLE(String tITLE) {
		TITLE = tITLE;
	}
	public String getBUS_ZIP() {
		return BUS_ZIP;
	}
	public void setBUS_ZIP(String bUS_ZIP) {
		BUS_ZIP = bUS_ZIP;
	}
	public String getBUS_ADDR_1() {
		return BUS_ADDR_1;
	}
	public void setBUS_ADDR_1(String bUS_ADDR_1) {
		BUS_ADDR_1 = bUS_ADDR_1;
	}
	public String getBUS_ADDR_2() {
		return BUS_ADDR_2;
	}
	public void setBUS_ADDR_2(String bUS_ADDR_2) {
		BUS_ADDR_2 = bUS_ADDR_2;
	}
	public String getCTT_ZIP() {
		return CTT_ZIP;
	}
	public void setCTT_ZIP(String cTT_ZIP) {
		CTT_ZIP = cTT_ZIP;
	}
	public String getCTT_ADDR_1() {
		return CTT_ADDR_1;
	}
	public void setCTT_ADDR_1(String cTT_ADDR_1) {
		CTT_ADDR_1 = cTT_ADDR_1;
	}
	public String getCTT_ADDR_2() {
		return CTT_ADDR_2;
	}
	public void setCTT_ADDR_2(String cTT_ADDR_2) {
		CTT_ADDR_2 = cTT_ADDR_2;
	}
	public String getPAY_SLIP() {
		return PAY_SLIP;
	}
	public void setPAY_SLIP(String pAY_SLIP) {
		PAY_SLIP = pAY_SLIP;
	}
	public String getPAY_CODE() {
		return PAY_CODE;
	}
	public void setPAY_CODE(String pAY_CODE) {
		PAY_CODE = pAY_CODE;
	}
	public String getPAY_ZIP() {
		return PAY_ZIP;
	}
	public void setPAY_ZIP(String pAY_ZIP) {
		PAY_ZIP = pAY_ZIP;
	}
	public String getPAY_ADDR_1() {
		return PAY_ADDR_1;
	}
	public void setPAY_ADDR_1(String pAY_ADDR_1) {
		PAY_ADDR_1 = pAY_ADDR_1;
	}
	public String getPAY_ADDR_2() {
		return PAY_ADDR_2;
	}
	public void setPAY_ADDR_2(String pAY_ADDR_2) {
		PAY_ADDR_2 = pAY_ADDR_2;
	}
	public String getTEL_COD1() {
		return TEL_COD1;
	}
	public void setTEL_COD1(String tEL_COD1) {
		TEL_COD1 = tEL_COD1;
	}
	public String getTEL_NO1() {
		return TEL_NO1;
	}
	public void setTEL_NO1(String tEL_NO1) {
		TEL_NO1 = tEL_NO1;
	}
	public String getTEL_EXT1() {
		return TEL_EXT1;
	}
	public void setTEL_EXT1(String tEL_EXT1) {
		TEL_EXT1 = tEL_EXT1;
	}
	public String getTEL_TYP1() {
		return TEL_TYP1;
	}
	public void setTEL_TYP1(String tEL_TYP1) {
		TEL_TYP1 = tEL_TYP1;
	}
	public String getTEL_DAY1() {
		return TEL_DAY1;
	}
	public void setTEL_DAY1(String tEL_DAY1) {
		TEL_DAY1 = tEL_DAY1;
	}
	public String getTEL_NIGHT1() {
		return TEL_NIGHT1;
	}
	public void setTEL_NIGHT1(String tEL_NIGHT1) {
		TEL_NIGHT1 = tEL_NIGHT1;
	}
	public String getTEL_VOICE1() {
		return TEL_VOICE1;
	}
	public void setTEL_VOICE1(String tEL_VOICE1) {
		TEL_VOICE1 = tEL_VOICE1;
	}
	public String getTEL_FAX1() {
		return TEL_FAX1;
	}
	public void setTEL_FAX1(String tEL_FAX1) {
		TEL_FAX1 = tEL_FAX1;
	}
	public String getTEL_COD2() {
		return TEL_COD2;
	}
	public void setTEL_COD2(String tEL_COD2) {
		TEL_COD2 = tEL_COD2;
	}
	public String getTEL_NO2() {
		return TEL_NO2;
	}
	public void setTEL_NO2(String tEL_NO2) {
		TEL_NO2 = tEL_NO2;
	}
	public String getTEL_EXT2() {
		return TEL_EXT2;
	}
	public void setTEL_EXT2(String tEL_EXT2) {
		TEL_EXT2 = tEL_EXT2;
	}
	public String getTEL_TYP2() {
		return TEL_TYP2;
	}
	public void setTEL_TYP2(String tEL_TYP2) {
		TEL_TYP2 = tEL_TYP2;
	}
	public String getTEL_DAY2() {
		return TEL_DAY2;
	}
	public void setTEL_DAY2(String tEL_DAY2) {
		TEL_DAY2 = tEL_DAY2;
	}
	public String getTEL_NIGHT2() {
		return TEL_NIGHT2;
	}
	public void setTEL_NIGHT2(String tEL_NIGHT2) {
		TEL_NIGHT2 = tEL_NIGHT2;
	}
	public String getTEL_VOICE2() {
		return TEL_VOICE2;
	}
	public void setTEL_VOICE2(String tEL_VOICE2) {
		TEL_VOICE2 = tEL_VOICE2;
	}
	public String getTEL_FAX2() {
		return TEL_FAX2;
	}
	public void setTEL_FAX2(String tEL_FAX2) {
		TEL_FAX2 = tEL_FAX2;
	}
	public String getEMAIL_ADDR() {
		return EMAIL_ADDR;
	}
	public void setEMAIL_ADDR(String eMAIL_ADDR) {
		EMAIL_ADDR = eMAIL_ADDR;
	}
	public String getNO_EMAIL_FLG() {
		return NO_EMAIL_FLG;
	}
	public void setNO_EMAIL_FLG(String nO_EMAIL_FLG) {
		NO_EMAIL_FLG = nO_EMAIL_FLG;
	}
	public String getENG_NAME() {
		return ENG_NAME;
	}
	public void setENG_NAME(String eNG_NAME) {
		ENG_NAME = eNG_NAME;
	}
	public String getENG_ZIP() {
		return ENG_ZIP;
	}
	public void setENG_ZIP(String eNG_ZIP) {
		ENG_ZIP = eNG_ZIP;
	}
	public String getENG_ADDR_1() {
		return ENG_ADDR_1;
	}
	public void setENG_ADDR_1(String eNG_ADDR_1) {
		ENG_ADDR_1 = eNG_ADDR_1;
	}
	public String getENG_ADDR_2() {
		return ENG_ADDR_2;
	}
	public void setENG_ADDR_2(String eNG_ADDR_2) {
		ENG_ADDR_2 = eNG_ADDR_2;
	}
	public String getREG_CAP_VAL() {
		return REG_CAP_VAL;
	}
	public void setREG_CAP_VAL(String rEG_CAP_VAL) {
		REG_CAP_VAL = rEG_CAP_VAL;
	}
	public String getCAP_VAL_YY() {
		return CAP_VAL_YY;
	}
	public void setCAP_VAL_YY(String cAP_VAL_YY) {
		CAP_VAL_YY = cAP_VAL_YY;
	}
	public String getCAP_VAL_MM() {
		return CAP_VAL_MM;
	}
	public void setCAP_VAL_MM(String cAP_VAL_MM) {
		CAP_VAL_MM = cAP_VAL_MM;
	}
	public String getCAP_VAL() {
		return CAP_VAL;
	}
	public void setCAP_VAL(String cAP_VAL) {
		CAP_VAL = cAP_VAL;
	}
	public String getEMP_CNT() {
		return EMP_CNT;
	}
	public void setEMP_CNT(String eMP_CNT) {
		EMP_CNT = eMP_CNT;
	}
	public String getAPROV_DATE() {
		return APROV_DATE;
	}
	public void setAPROV_DATE(String aPROV_DATE) {
		APROV_DATE = aPROV_DATE;
	}
	public String getBUS_ITEM() {
		return BUS_ITEM;
	}
	public void setBUS_ITEM(String bUS_ITEM) {
		BUS_ITEM = bUS_ITEM;
	}
	public String getCONTACT_PSON() {
		return CONTACT_PSON;
	}
	public void setCONTACT_PSON(String cONTACT_PSON) {
		CONTACT_PSON = cONTACT_PSON;
	}
	public String getRESP_ID() {
		return RESP_ID;
	}
	public void setRESP_ID(String rESP_ID) {
		RESP_ID = rESP_ID;
	}
	public String getRESP_NAME() {
		return RESP_NAME;
	}
	public void setRESP_NAME(String rESP_NAME) {
		RESP_NAME = rESP_NAME;
	}
	public String getRESP_BDAY() {
		return RESP_BDAY;
	}
	public void setRESP_BDAY(String rESP_BDAY) {
		RESP_BDAY = rESP_BDAY;
	}
	public String getPRT_SLIP() {
		return PRT_SLIP;
	}
	public void setPRT_SLIP(String pRT_SLIP) {
		PRT_SLIP = pRT_SLIP;
	}
	public String getFR_CMPID() {
		return FR_CMPID;
	}
	public void setFR_CMPID(String fR_CMPID) {
		FR_CMPID = fR_CMPID;
	}
	public String getFR_INTNO() {
		return FR_INTNO;
	}
	public void setFR_INTNO(String fR_INTNO) {
		FR_INTNO = fR_INTNO;
	}
	public String getFR_REGNO() {
		return FR_REGNO;
	}
	public void setFR_REGNO(String fR_REGNO) {
		FR_REGNO = fR_REGNO;
	}
	public String getBILLS_CHECK() {
		return BILLS_CHECK;
	}
	public void setBILLS_CHECK(String bILLS_CHECK) {
		BILLS_CHECK = bILLS_CHECK;
	}
	public String getSRC_TYP() {
		return SRC_TYP;
	}
	public void setSRC_TYP(String sRC_TYP) {
		SRC_TYP = sRC_TYP;
	}
	public String getLOST_FLG() {
		return LOST_FLG;
	}
	public void setLOST_FLG(String lOST_FLG) {
		LOST_FLG = lOST_FLG;
	}
	public String getBILLS_UPD_DATE() {
		return BILLS_UPD_DATE;
	}
	public void setBILLS_UPD_DATE(String bILLS_UPD_DATE) {
		BILLS_UPD_DATE = bILLS_UPD_DATE;
	}
	public String getBILLS_STR_DATE() {
		return BILLS_STR_DATE;
	}
	public void setBILLS_STR_DATE(String bILLS_STR_DATE) {
		BILLS_STR_DATE = bILLS_STR_DATE;
	}
	public String getBILLS_END_DATE() {
		return BILLS_END_DATE;
	}
	public void setBILLS_END_DATE(String bILLS_END_DATE) {
		BILLS_END_DATE = bILLS_END_DATE;
	}
	public String getLST_TX_BRH1() {
		return LST_TX_BRH1;
	}
	public void setLST_TX_BRH1(String lST_TX_BRH1) {
		LST_TX_BRH1 = lST_TX_BRH1;
	}
	public String getLST_TX_DATE1() {
		return LST_TX_DATE1;
	}
	public void setLST_TX_DATE1(String lST_TX_DATE1) {
		LST_TX_DATE1 = lST_TX_DATE1;
	}
	public String getLST_TX_BRH2() {
		return LST_TX_BRH2;
	}
	public void setLST_TX_BRH2(String lST_TX_BRH2) {
		LST_TX_BRH2 = lST_TX_BRH2;
	}
	public String getLST_TX_DATE2() {
		return LST_TX_DATE2;
	}
	public void setLST_TX_DATE2(String lST_TX_DATE2) {
		LST_TX_DATE2 = lST_TX_DATE2;
	}
	public String getLST_TX_BRH3() {
		return LST_TX_BRH3;
	}
	public void setLST_TX_BRH3(String lST_TX_BRH3) {
		LST_TX_BRH3 = lST_TX_BRH3;
	}
	public String getLST_TX_DATE3() {
		return LST_TX_DATE3;
	}
	public void setLST_TX_DATE3(String lST_TX_DATE3) {
		LST_TX_DATE3 = lST_TX_DATE3;
	}
	public String getLST_TX_BRH4() {
		return LST_TX_BRH4;
	}
	public void setLST_TX_BRH4(String lST_TX_BRH4) {
		LST_TX_BRH4 = lST_TX_BRH4;
	}
	public String getLST_TX_DATE4() {
		return LST_TX_DATE4;
	}
	public void setLST_TX_DATE4(String lST_TX_DATE4) {
		LST_TX_DATE4 = lST_TX_DATE4;
	}
	public String getLST_TX_BRH5() {
		return LST_TX_BRH5;
	}
	public void setLST_TX_BRH5(String lST_TX_BRH5) {
		LST_TX_BRH5 = lST_TX_BRH5;
	}
	public String getLST_TX_DATE5() {
		return LST_TX_DATE5;
	}
	public void setLST_TX_DATE5(String lST_TX_DATE5) {
		LST_TX_DATE5 = lST_TX_DATE5;
	}
	public String getLST_TX_BRH6() {
		return LST_TX_BRH6;
	}
	public void setLST_TX_BRH6(String lST_TX_BRH6) {
		LST_TX_BRH6 = lST_TX_BRH6;
	}
	public String getLST_TX_DATE6() {
		return LST_TX_DATE6;
	}
	public void setLST_TX_DATE6(String lST_TX_DATE6) {
		LST_TX_DATE6 = lST_TX_DATE6;
	}
	public String getMARRAGE() {
		return MARRAGE;
	}
	public void setMARRAGE(String mARRAGE) {
		MARRAGE = mARRAGE;
	}
	public String getCHILD_NO() {
		return CHILD_NO;
	}
	public void setCHILD_NO(String cHILD_NO) {
		CHILD_NO = cHILD_NO;
	}
	public String getEDUCATION() {
		return EDUCATION;
	}
	public void setEDUCATION(String eDUCATION) {
		EDUCATION = eDUCATION;
	}
	public String getCAREER() {
		return CAREER;
	}
	public void setCAREER(String cAREER) {
		CAREER = cAREER;
	}
	public String getOLD_CUST_NO() {
		return OLD_CUST_NO;
	}
	public void setOLD_CUST_NO(String oLD_CUST_NO) {
		OLD_CUST_NO = oLD_CUST_NO;
	}
	public String getEDM_APPV() {
		return EDM_APPV;
	}
	public void setEDM_APPV(String eDM_APPV) {
		EDM_APPV = eDM_APPV;
	}
	public String getDM_APPV() {
		return DM_APPV;
	}
	public void setDM_APPV(String dM_APPV) {
		DM_APPV = dM_APPV;
	}
	public String getTM_APPV() {
		return TM_APPV;
	}
	public void setTM_APPV(String tM_APPV) {
		TM_APPV = tM_APPV;
	}
	public String getSMS_APPV() {
		return SMS_APPV;
	}
	public void setSMS_APPV(String sMS_APPV) {
		SMS_APPV = sMS_APPV;
	}
	public String getCUST_STS_01() {
		return CUST_STS_01;
	}
	public void setCUST_STS_01(String cUST_STS_01) {
		CUST_STS_01 = cUST_STS_01;
	}
	public String getTD_NOTIFY() {
		return TD_NOTIFY;
	}
	public void setTD_NOTIFY(String tD_NOTIFY) {
		TD_NOTIFY = tD_NOTIFY;
	}
	public String getPROT_NAME() {
		return PROT_NAME;
	}
	public void setPROT_NAME(String pROT_NAME) {
		PROT_NAME = pROT_NAME;
	}
	public String getRATE_TYP() {
		return RATE_TYP;
	}
	public void setRATE_TYP(String rATE_TYP) {
		RATE_TYP = rATE_TYP;
	}
	public String getMTN_BRH_RAT() {
		return MTN_BRH_RAT;
	}
	public void setMTN_BRH_RAT(String mTN_BRH_RAT) {
		MTN_BRH_RAT = mTN_BRH_RAT;
	}
	public String getMTN_DATE_RAT() {
		return MTN_DATE_RAT;
	}
	public void setMTN_DATE_RAT(String mTN_DATE_RAT) {
		MTN_DATE_RAT = mTN_DATE_RAT;
	}
	public String getRATE() {
		return RATE;
	}
	public void setRATE(String rATE) {
		RATE = rATE;
	}
	public String getRATE_YYY() {
		return RATE_YYY;
	}
	public void setRATE_YYY(String rATE_YYY) {
		RATE_YYY = rATE_YYY;
	}
	public Date getBDAY_D() {
		return BDAY_D;
	}
	public void setBDAY_D(Date bDAY_D) {
		BDAY_D = bDAY_D;
	}
	public Date getRISK_DATE_01_D() {
		return RISK_DATE_01_D;
	}
	public void setRISK_DATE_01_D(Date rISK_DATE_01_D) {
		RISK_DATE_01_D = rISK_DATE_01_D;
	}
	public Date getBILLS_UPD_DATE_D() {
		return BILLS_UPD_DATE_D;
	}
	public void setBILLS_UPD_DATE_D(Date bILLS_UPD_DATE_D) {
		BILLS_UPD_DATE_D = bILLS_UPD_DATE_D;
	}
	public Date getBILLS_STR_DATE_D() {
		return BILLS_STR_DATE_D;
	}
	public void setBILLS_STR_DATE_D(Date bILLS_STR_DATE_D) {
		BILLS_STR_DATE_D = bILLS_STR_DATE_D;
	}
	public Date getBILLS_END_DATE_D() {
		return BILLS_END_DATE_D;
	}
	public void setBILLS_END_DATE_D(Date bILLS_END_DATE_D) {
		BILLS_END_DATE_D = bILLS_END_DATE_D;
	}
	public Date getLST_TX_DATE1_D() {
		return LST_TX_DATE1_D;
	}
	public void setLST_TX_DATE1_D(Date lST_TX_DATE1_D) {
		LST_TX_DATE1_D = lST_TX_DATE1_D;
	}
	public Date getLST_TX_DATE2_D() {
		return LST_TX_DATE2_D;
	}
	public void setLST_TX_DATE2_D(Date lST_TX_DATE2_D) {
		LST_TX_DATE2_D = lST_TX_DATE2_D;
	}
	public Date getLST_TX_DATE3_D() {
		return LST_TX_DATE3_D;
	}
	public void setLST_TX_DATE3_D(Date lST_TX_DATE3_D) {
		LST_TX_DATE3_D = lST_TX_DATE3_D;
	}
	public Date getLST_TX_DATE5_D() {
		return LST_TX_DATE5_D;
	}
	public void setLST_TX_DATE5_D(Date lST_TX_DATE5_D) {
		LST_TX_DATE5_D = lST_TX_DATE5_D;
	}
	public Date getLST_TX_DATE6_D() {
		return LST_TX_DATE6_D;
	}
	public void setLST_TX_DATE6_D(Date lST_TX_DATE6_D) {
		LST_TX_DATE6_D = lST_TX_DATE6_D;
	}
	public Date getMTN_DATE_RAT_D() {
		return MTN_DATE_RAT_D;
	}
	public void setMTN_DATE_RAT_D(Date mTN_DATE_RAT_D) {
		MTN_DATE_RAT_D = mTN_DATE_RAT_D;
	}
	public Date getPD_DATE_D() {
		return PD_DATE_D;
	}
	public void setPD_DATE_D(Date pD_DATE_D) {
		PD_DATE_D = pD_DATE_D;
	}

    
	


}