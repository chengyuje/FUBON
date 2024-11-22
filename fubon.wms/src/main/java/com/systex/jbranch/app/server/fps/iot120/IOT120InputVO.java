package com.systex.jbranch.app.server.fps.iot120;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT120InputVO extends PagingInputVO{

	private String in_column;
	private String in_INSID;
	private String WRITE_REASON;        //使用紙本要保書原因
	private String WRITE_REASON_OTH;    //使用紙本要保書原因為其他
	private String RECRUIT_ID;          //招攬人員員編
	private String INSURED;
	private String CUST_ID;             //要保人ID
	private String AO_CODE;             //AO CODE
	private String AO_ID;
	private String REPRESET_ID;         //法定代理人ID
	private String INS_ID;              //保險文件編號
	private Date   APPLY_DATE;            //要保書填寫申請日
	private String PRODUCT_TYPE;
	private String AB_TRANSSEQ;         //非常態交易錄音序號
	private String INSURED_ID;          //被保人ID
	private String REF_CON_ID;          //轉介編號
	private String INSPRD_ID;           //主約險種代號
	private String PAY_TYPE;	        //繳別
	private String PRD_RISK;            //風險等級-銀行
	private String REG_TYPE;            //要保書類型
	private String IS_EMP;              //員工件
	private String INSURED_NAME;        //被保人姓名
	private String PROPOSER_NAME;       //要保人姓名
	private Date   PROPOSER_BIRTH;        //要保人生日
	private String CNCT_INSPRD_KEYNO;
	private String BASE_PREMIUM;         //基本保費(原幣)
	private String REAL_PREMIUM;	     //實收保費
	private String MOP2;                 //分期繳別
	private String FIRST_PAY_WAY;        //首期保費繳費方式
	private String QC_ADD;               //檢查要保書地址與分行留存地址相同否
	private String TERMINATED_INC;       //檢核進件文件有包含解約
	private String QC_ERASER;            //檢查有使用可擦拭文具
	private String QC_ANC_DOC;           //檢核要保文件有檢付保險通報單
	private String QC_STAMP;             //檢查驗印結果是否符合規定
	private String PROPOSER_TRANSSEQ;    //要保人驗印錄音序號
	private String INSURED_TRANSSEQ;     //被保人驗印錄音序號
	private String OPR_STATUS;                 //修改狀態
	private String RLT_BT_PROREP;              //與要保人關係
	private String STATUS;                     //狀態
	private String INS_KEYNO;
	private String INSURED_CM_FLAG;            //被保人戶況檢核
	private String PROPOSER_CM_FLAG;           //要保人戶況檢核
	private String REPRESET_CM_FLAG;           //法代戶況檢核
	private String CURR_CD;		               //幣別
	private String UNDER_YN;	               //弱勢戶
	private String PRO_YN;		               //專業投資人
	private String AB_EXCH_RATE;	           //非常態交易匯率
	private String EMP_NAME;                   //招攬人員姓名
	private String CASE_ID;                    //案件編號
	private boolean editINSPRD_ID;
	private Date MATCH_DATE;                   //基金適配清單列印日
	private Date KEYIN_DATE;                   //風險值適配日
	private List<Map<String, Object>> inList;
	private List<Map<String, Object>> outList;
	private List<Map<String, Object>> INVESTList;
	private List<Map<String, Object>> INS_RIDER_DTLList; //與主約被保人關係
	private List<String> DELETE_INS_RIDER_DTLList;       //刪除_與主約被保人關係
	private List<Map<String, Object>> webservicePdfData;
	private String empId;
	private String PREMATCH_SEQ;	//適合度檢核編號
	private Date GUILD_RPT_DATE;	//公會通報日
	private String LOAN_PRD_YN;		//房貸壽險商品(Y/N)
	private String NOT_PASS_REASON;	//不通過之說明
	private String PREMIUM_TRANSSEQ;//要保人高齡/保費來源錄音序號
	private String I_PREMIUM_TRANSSEQ;//被保人高齡/保費來源錄音序號
	private String P_PREMIUM_TRANSSEQ;//繳款人高齡/保費來源錄音序號
	private String QC_IMMI;			//檢查有檢附立即投入批註書(Y/N)
	private String LOAN_SOURCE_YN;	//保費來源是否為貸款(Y/N)
	private String SALE_SENIOR_TRANSSEQ;	//高齡銷售過程錄音序號
	private String QC_APEC;
	private String QC_LOAN_CHK;
	private String QC_SIGNATURE;
	private String C_SENIOR_PVAL;
	private String CUST_RISK;
	private String FB_COM_YN;
	private BigDecimal COMPANY_NUM;
	private String C_SALE_SENIOR_YN;
	private String CANCEL_CONTRACT_YN;
	private String PREMIUM_TRANSSEQ_NEED_YN;
	private String I_PREMIUM_TRANSSEQ_NEED_YN;
	private String P_PREMIUM_TRANSSEQ_NEED_YN;
	private String fromIOT110;
	private String BUSINESS_REL;

	public String getAO_ID() {
		return AO_ID;
	}
	public void setAO_ID(String aO_ID) {
		AO_ID = aO_ID;
	}

	public List<Map<String, Object>> getWebservicePdfData() {
		return webservicePdfData;
	}
	public void setWebservicePdfData(List<Map<String, Object>> webservicePdfData) {
		this.webservicePdfData = webservicePdfData;
	}

	public String getCASE_ID() {
		return CASE_ID;
	}
	public void setCASE_ID(String cASE_ID) {
		CASE_ID = cASE_ID;
	}
	public Date getKEYIN_DATE() {
		return KEYIN_DATE;
	}
	public void setKEYIN_DATE(Date kEYIN_DATE) {
		KEYIN_DATE = kEYIN_DATE;
	}
	public Date getMATCH_DATE() {
		return MATCH_DATE;
	}
	public void setMATCH_DATE(Date mATCH_DATE) {
		MATCH_DATE = mATCH_DATE;
	}
	public String getEMP_NAME() {
		return EMP_NAME;
	}
	public void setEMP_NAME(String eMP_NAME) {
		EMP_NAME = eMP_NAME;
	}
	public boolean isEditINSPRD_ID() {
		return editINSPRD_ID;
	}
	public void setEditINSPRD_ID(boolean editINSPRD_ID) {
		this.editINSPRD_ID = editINSPRD_ID;
	}
	public String getAB_EXCH_RATE() {
		return AB_EXCH_RATE;
	}
	public void setAB_EXCH_RATE(String aB_EXCH_RATE) {
		AB_EXCH_RATE = aB_EXCH_RATE;
	}
	public String getUNDER_YN() {
		return UNDER_YN;
	}
	public String getPRO_YN() {
		return PRO_YN;
	}
	public void setUNDER_YN(String uNDER_YN) {
		UNDER_YN = uNDER_YN;
	}
	public void setPRO_YN(String pRO_YN) {
		PRO_YN = pRO_YN;
	}
	public String getCURR_CD() {
		return CURR_CD;
	}
	public void setCURR_CD(String cURR_CD) {
		CURR_CD = cURR_CD;
	}
	public List<String> getDELETE_INS_RIDER_DTLList() {
		return DELETE_INS_RIDER_DTLList;
	}
	public void setDELETE_INS_RIDER_DTLList(List<String> dELETE_INS_RIDER_DTLList) {
		DELETE_INS_RIDER_DTLList = dELETE_INS_RIDER_DTLList;
	}
	public List<Map<String, Object>> getINVESTList() {
		return INVESTList;
	}
	public List<Map<String, Object>> getINS_RIDER_DTLList() {
		return INS_RIDER_DTLList;
	}
	public void setINVESTList(List<Map<String, Object>> iNVESTList) {
		INVESTList = iNVESTList;
	}
	public void setINS_RIDER_DTLList(List<Map<String, Object>> iNS_RIDER_DTLList) {
		INS_RIDER_DTLList = iNS_RIDER_DTLList;
	}
	public String getINSURED_CM_FLAG() {
		return INSURED_CM_FLAG;
	}
	public String getPROPOSER_CM_FLAG() {
		return PROPOSER_CM_FLAG;
	}
	public String getREPRESET_CM_FLAG() {
		return REPRESET_CM_FLAG;
	}
	public void setINSURED_CM_FLAG(String iNSURED_CM_FLAG) {
		INSURED_CM_FLAG = iNSURED_CM_FLAG;
	}
	public void setPROPOSER_CM_FLAG(String pROPOSER_CM_FLAG) {
		PROPOSER_CM_FLAG = pROPOSER_CM_FLAG;
	}
	public void setREPRESET_CM_FLAG(String rEPRESET_CM_FLAG) {
		REPRESET_CM_FLAG = rEPRESET_CM_FLAG;
	}
	public String getAO_CODE() {
		return AO_CODE;
	}
	public void setAO_CODE(String aO_CODE) {
		AO_CODE = aO_CODE;
	}
	public String getBASE_PREMIUM() {
		return BASE_PREMIUM;
	}
	public void setBASE_PREMIUM(String bASE_PREMIUM) {
		BASE_PREMIUM = bASE_PREMIUM;
	}
	public String getWRITE_REASON() {
		return WRITE_REASON;
	}
	public void setWRITE_REASON(String wRITE_REASON) {
		WRITE_REASON = wRITE_REASON;
	}
	public String getWRITE_REASON_OTH() {
		return WRITE_REASON_OTH;
	}
	public void setWRITE_REASON_OTH(String wRITE_REASON_OTH) {
		WRITE_REASON_OTH = wRITE_REASON_OTH;
	}
	public String getINS_KEYNO() {
		return INS_KEYNO;
	}
	public void setINS_KEYNO(String iNS_KEYNO) {
		INS_KEYNO = iNS_KEYNO;
	}
	public String getRLT_BT_PROREP() {
		return RLT_BT_PROREP;
	}
	public void setRLT_BT_PROREP(String rLT_BT_PROREP) {
		RLT_BT_PROREP = rLT_BT_PROREP;
	}
	public String getOPR_STATUS() {
		return OPR_STATUS;
	}
	public void setOPR_STATUS(String oPR_STATUS) {
		OPR_STATUS = oPR_STATUS;
	}
	public List<Map<String, Object>> getInList() {
		return inList;
	}
	public void setInList(List<Map<String, Object>> inList) {
		this.inList = inList;
	}
	public List<Map<String, Object>> getOutList() {
		return outList;
	}
	public void setOutList(List<Map<String, Object>> outList) {
		this.outList = outList;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getREG_TYPE() {
		return REG_TYPE;
	}
	public void setREG_TYPE(String rEG_TYPE) {
		REG_TYPE = rEG_TYPE;
	}
	public String getIS_EMP() {
		return IS_EMP;
	}
	public void setIS_EMP(String iS_EMP) {
		IS_EMP = iS_EMP;
	}
	public String getINSURED_NAME() {
		return INSURED_NAME;
	}
	public void setINSURED_NAME(String iNSURED_NAME) {
		INSURED_NAME = iNSURED_NAME;
	}
	public String getPROPOSER_NAME() {
		return PROPOSER_NAME;
	}
	public void setPROPOSER_NAME(String pROPOSER_NAME) {
		PROPOSER_NAME = pROPOSER_NAME;
	}
	public Date getPROPOSER_BIRTH() {
		return PROPOSER_BIRTH;
	}
	public void setPROPOSER_BIRTH(Date pROPOSER_BIRTH) {
		PROPOSER_BIRTH = pROPOSER_BIRTH;
	}
	public String getCNCT_INSPRD_KEYNO() {
		return CNCT_INSPRD_KEYNO;
	}
	public void setCNCT_INSPRD_KEYNO(String cNCT_INSPRD_KEYNO) {
		CNCT_INSPRD_KEYNO = cNCT_INSPRD_KEYNO;
	}
	public String getREAL_PREMIUM() {
		return REAL_PREMIUM;
	}
	public void setREAL_PREMIUM(String rEAL_PREMIUM) {
		REAL_PREMIUM = rEAL_PREMIUM;
	}
	public String getMOP2() {
		return MOP2;
	}
	public void setMOP2(String mOP2) {
		MOP2 = mOP2;
	}
	public String getFIRST_PAY_WAY() {
		return FIRST_PAY_WAY;
	}
	public void setFIRST_PAY_WAY(String fIRST_PAY_WAY) {
		FIRST_PAY_WAY = fIRST_PAY_WAY;
	}
	public String getQC_ADD() {
		return QC_ADD;
	}
	public void setQC_ADD(String qC_ADD) {
		QC_ADD = qC_ADD;
	}
	public String getTERMINATED_INC() {
		return TERMINATED_INC;
	}
	public void setTERMINATED_INC(String tERMINATED_INC) {
		TERMINATED_INC = tERMINATED_INC;
	}
	public String getQC_ERASER() {
		return QC_ERASER;
	}
	public void setQC_ERASER(String qC_ERASER) {
		QC_ERASER = qC_ERASER;
	}
	public String getQC_ANC_DOC() {
		return QC_ANC_DOC;
	}
	public void setQC_ANC_DOC(String qC_ANC_DOC) {
		QC_ANC_DOC = qC_ANC_DOC;
	}
	public String getQC_STAMP() {
		return QC_STAMP;
	}
	public void setQC_STAMP(String qC_STAMP) {
		QC_STAMP = qC_STAMP;
	}
	public String getPROPOSER_TRANSSEQ() {
		return PROPOSER_TRANSSEQ;
	}
	public void setPROPOSER_TRANSSEQ(String pROPOSER_TRANSSEQ) {
		PROPOSER_TRANSSEQ = pROPOSER_TRANSSEQ;
	}
	public String getINSURED_TRANSSEQ() {
		return INSURED_TRANSSEQ;
	}
	public void setINSURED_TRANSSEQ(String iNSURED_TRANSSEQ) {
		INSURED_TRANSSEQ = iNSURED_TRANSSEQ;
	}
	public String getPRD_RISK() {
		return PRD_RISK;
	}
	public void setPRD_RISK(String pRD_RISK) {
		PRD_RISK = pRD_RISK;
	}
	public String getINSPRD_ID() {
		return INSPRD_ID;
	}
	public void setINSPRD_ID(String iNSPRD_ID) {
		INSPRD_ID = iNSPRD_ID;
	}
	public String getPAY_TYPE() {
		return PAY_TYPE;
	}
	public void setPAY_TYPE(String pAY_TYPE) {
		PAY_TYPE = pAY_TYPE;
	}
	public String getREF_CON_ID() {
		return REF_CON_ID;
	}
	public void setREF_CON_ID(String rEF_CON_ID) {
		REF_CON_ID = rEF_CON_ID;
	}
	public String getINSURED_ID() {
		return INSURED_ID;
	}
	public void setINSURED_ID(String iNSURED_ID) {
		INSURED_ID = iNSURED_ID;
	}
	public String getIn_INSID() {
		return in_INSID;
	}
	public void setIn_INSID(String in_INSID) {
		this.in_INSID = in_INSID;
	}
	public String getAB_TRANSSEQ() {
		return AB_TRANSSEQ;
	}
	public void setAB_TRANSSEQ(String aB_TRANSSEQ) {
		AB_TRANSSEQ = aB_TRANSSEQ;
	}
	public String getPRODUCT_TYPE() {
		return PRODUCT_TYPE;
	}
	public void setPRODUCT_TYPE(String pRODUCT_TYPE) {
		PRODUCT_TYPE = pRODUCT_TYPE;
	}
	public Date getAPPLY_DATE() {
		return APPLY_DATE;
	}
	public void setAPPLY_DATE(Date aPPLY_DATE) {
		APPLY_DATE = aPPLY_DATE;
	}
	public String getINS_ID() {
		return INS_ID;
	}
	public void setINS_ID(String iNS_ID) {
		INS_ID = iNS_ID;
	}
	public String getIn_column() {
		return in_column;
	}
	public void setIn_column(String in_column) {
		this.in_column = in_column;
	}
	public String getRECRUIT_ID() {
		return RECRUIT_ID;
	}
	public void setRECRUIT_ID(String rECRUIT_ID) {
		RECRUIT_ID = rECRUIT_ID;
	}
	public String getINSURED() {
		return INSURED;
	}
	public void setINSURED(String iNSURED) {
		INSURED = iNSURED;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public String getREPRESET_ID() {
		return REPRESET_ID;
	}
	public void setREPRESET_ID(String rEPRESET_ID) {
		REPRESET_ID = rEPRESET_ID;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public String getPREMATCH_SEQ() {
		return PREMATCH_SEQ;
	}
	public void setPREMATCH_SEQ(String pREMATCH_SEQ) {
		PREMATCH_SEQ = pREMATCH_SEQ;
	}
	public Date getGUILD_RPT_DATE() {
		return GUILD_RPT_DATE;
	}
	public void setGUILD_RPT_DATE(Date gUILD_RPT_DATE) {
		GUILD_RPT_DATE = gUILD_RPT_DATE;
	}
	public String getLOAN_PRD_YN() {
		return LOAN_PRD_YN;
	}
	public void setLOAN_PRD_YN(String lOAN_PRD_YN) {
		LOAN_PRD_YN = lOAN_PRD_YN;
	}
	public String getNOT_PASS_REASON() {
		return NOT_PASS_REASON;
	}
	public void setNOT_PASS_REASON(String nOT_PASS_REASON) {
		NOT_PASS_REASON = nOT_PASS_REASON;
	}
	public String getPREMIUM_TRANSSEQ() {
		return PREMIUM_TRANSSEQ;
	}
	public void setPREMIUM_TRANSSEQ(String pREMIUM_TRANSSEQ) {
		PREMIUM_TRANSSEQ = pREMIUM_TRANSSEQ;
	}
	public String getQC_IMMI() {
		return QC_IMMI;
	}
	public void setQC_IMMI(String qC_IMMI) {
		QC_IMMI = qC_IMMI;
	}
	public String getLOAN_SOURCE_YN() {
		return LOAN_SOURCE_YN;
	}
	public void setLOAN_SOURCE_YN(String lOAN_SOURCE_YN) {
		LOAN_SOURCE_YN = lOAN_SOURCE_YN;
	}
	public String getSALE_SENIOR_TRANSSEQ() {
		return SALE_SENIOR_TRANSSEQ;
	}
	public void setSALE_SENIOR_TRANSSEQ(String sALE_SENIOR_TRANSSEQ) {
		SALE_SENIOR_TRANSSEQ = sALE_SENIOR_TRANSSEQ;
	}
	public String getQC_APEC() {
		return QC_APEC;
	}
	public void setQC_APEC(String qC_APEC) {
		QC_APEC = qC_APEC;
	}
	public String getQC_LOAN_CHK() {
		return QC_LOAN_CHK;
	}
	public void setQC_LOAN_CHK(String qC_LOAN_CHK) {
		QC_LOAN_CHK = qC_LOAN_CHK;
	}
	public String getQC_SIGNATURE() {
		return QC_SIGNATURE;
	}
	public void setQC_SIGNATURE(String qC_SIGNATURE) {
		QC_SIGNATURE = qC_SIGNATURE;
	}
	public String getI_PREMIUM_TRANSSEQ() {
		return I_PREMIUM_TRANSSEQ;
	}
	public void setI_PREMIUM_TRANSSEQ(String i_PREMIUM_TRANSSEQ) {
		I_PREMIUM_TRANSSEQ = i_PREMIUM_TRANSSEQ;
	}
	public String getP_PREMIUM_TRANSSEQ() {
		return P_PREMIUM_TRANSSEQ;
	}
	public void setP_PREMIUM_TRANSSEQ(String p_PREMIUM_TRANSSEQ) {
		P_PREMIUM_TRANSSEQ = p_PREMIUM_TRANSSEQ;
	}
	public String getFromIOT110() {
		return fromIOT110;
	}
	public void setFromIOT110(String fromIOT110) {
		this.fromIOT110 = fromIOT110;
	}
	public String getC_SENIOR_PVAL() {
		return C_SENIOR_PVAL;
	}
	public void setC_SENIOR_PVAL(String c_SENIOR_PVAL) {
		C_SENIOR_PVAL = c_SENIOR_PVAL;
	}
	public String getCUST_RISK() {
		return CUST_RISK;
	}
	public void setCUST_RISK(String cUST_RISK) {
		CUST_RISK = cUST_RISK;
	}
	public String getFB_COM_YN() {
		return FB_COM_YN;
	}
	public void setFB_COM_YN(String fB_COM_YN) {
		FB_COM_YN = fB_COM_YN;
	}
	public BigDecimal getCOMPANY_NUM() {
		return COMPANY_NUM;
	}
	public void setCOMPANY_NUM(BigDecimal cOMPANY_NUM) {
		COMPANY_NUM = cOMPANY_NUM;
	}
	public String getC_SALE_SENIOR_YN() {
		return C_SALE_SENIOR_YN;
	}
	public void setC_SALE_SENIOR_YN(String c_SALE_SENIOR_YN) {
		C_SALE_SENIOR_YN = c_SALE_SENIOR_YN;
	}
	public String getCANCEL_CONTRACT_YN() {
		return CANCEL_CONTRACT_YN;
	}
	public void setCANCEL_CONTRACT_YN(String cANCEL_CONTRACT_YN) {
		CANCEL_CONTRACT_YN = cANCEL_CONTRACT_YN;
	}
	public String getPREMIUM_TRANSSEQ_NEED_YN() {
		return PREMIUM_TRANSSEQ_NEED_YN;
	}
	public void setPREMIUM_TRANSSEQ_NEED_YN(String pREMIUM_TRANSSEQ_NEED_YN) {
		PREMIUM_TRANSSEQ_NEED_YN = pREMIUM_TRANSSEQ_NEED_YN;
	}
	public String getI_PREMIUM_TRANSSEQ_NEED_YN() {
		return I_PREMIUM_TRANSSEQ_NEED_YN;
	}
	public void setI_PREMIUM_TRANSSEQ_NEED_YN(String i_PREMIUM_TRANSSEQ_NEED_YN) {
		I_PREMIUM_TRANSSEQ_NEED_YN = i_PREMIUM_TRANSSEQ_NEED_YN;
	}
	public String getP_PREMIUM_TRANSSEQ_NEED_YN() {
		return P_PREMIUM_TRANSSEQ_NEED_YN;
	}
	public void setP_PREMIUM_TRANSSEQ_NEED_YN(String p_PREMIUM_TRANSSEQ_NEED_YN) {
		P_PREMIUM_TRANSSEQ_NEED_YN = p_PREMIUM_TRANSSEQ_NEED_YN;
	}
	public String getBUSINESS_REL() {
		return BUSINESS_REL;
	}
	public void setBUSINESS_REL(String bUSINESS_REL) {
		BUSINESS_REL = bUSINESS_REL;
	}

}
