package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCAM_SFA_LEADS_IMPVO extends VOBase {

    /** identifier field */
    private BigDecimal SEQNO;

    /** nullable persistent field */
    private String CAMPAIGN_ID;

    /** nullable persistent field */
    private String CAMPAIGN_NAME;

    /** nullable persistent field */
    private String CAMPAIGN_DESC;

    /** nullable persistent field */
    private String STEP_ID;

    /** nullable persistent field */
    private String STEP_NAME;

    /** nullable persistent field */
    private String LEAD_SOURCE_ID;

    /** nullable persistent field */
    private Timestamp START_DATE;

    /** nullable persistent field */
    private Timestamp END_DATE;

    /** nullable persistent field */
    private String LEAD_USE_DP;

    /** nullable persistent field */
    private String LEAD_TYPE;

    /** nullable persistent field */
    private String LEAD_PARA1;

    /** nullable persistent field */
    private String LEAD_PARA2;

    /** nullable persistent field */
    private BigDecimal LEAD_DAYUSE;

    /** nullable persistent field */
    private BigDecimal FILE_SEQ;

    /** nullable persistent field */
    private String EXAM_ID;

    /** nullable persistent field */
    private String SALES_PITCH;

    /** nullable persistent field */
    private String FIRST_CHANNEL;

    /** nullable persistent field */
    private String SECOND_CHANNEL;

    /** nullable persistent field */
    private String IMP_STATUS;

    /** nullable persistent field */
    private String CHECK_STATUS;

    /** nullable persistent field */
    private BigDecimal LE_TOTAL_CNT;

    /** nullable persistent field */
    private BigDecimal IM_TOTAL_CNT;

    /** nullable persistent field */
    private BigDecimal IM_AO_CNT;

    /** nullable persistent field */
    private BigDecimal IM_OTHER_CNT;

    /** nullable persistent field */
    private BigDecimal ER_CNT;

    /** nullable persistent field */
    private BigDecimal RV_LE_CNT;

    /** nullable persistent field */
    private String RV_REASON;

    /** nullable persistent field */
    private Timestamp START_DT;

    /** nullable persistent field */
    private Timestamp END_DT;

    /** nullable persistent field */
    private String GIFT_CAMPAIGN_ID;

    /** nullable persistent field */
    private String RE_STATUS;

    /** nullable persistent field */
    private String RE_LOG;

    /** nullable persistent field */
    private BigDecimal HANDLE_CNT;

    /** nullable persistent field */
    private String LEAD_RESPONSE_CODE;

    /** nullable persistent field */
    private String REL_EMP_ID;

    /** nullable persistent field */
    private Timestamp REL_DATETIME;
    
    /** nullable persistent field */
    private String CAMP_PURPOSE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LEADS_IMP";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCAM_SFA_LEADS_IMPVO(BigDecimal SEQNO, String CAMPAIGN_ID, String CAMPAIGN_NAME, String CAMPAIGN_DESC, String STEP_ID, String STEP_NAME, String LEAD_SOURCE_ID, 
    							 Timestamp START_DATE, Timestamp END_DATE, String LEAD_USE_DP, String LEAD_TYPE, String LEAD_PARA1, String LEAD_PARA2, BigDecimal LEAD_DAYUSE, 
    							 BigDecimal FILE_SEQ, String EXAM_ID, String SALES_PITCH, String FIRST_CHANNEL, String SECOND_CHANNEL, String IMP_STATUS, String CHECK_STATUS, 
    							 BigDecimal LE_TOTAL_CNT, BigDecimal IM_TOTAL_CNT, BigDecimal IM_AO_CNT, BigDecimal IM_OTHER_CNT, BigDecimal ER_CNT, BigDecimal RV_LE_CNT, 
    							 String RV_REASON, Timestamp START_DT, Timestamp END_DT, String GIFT_CAMPAIGN_ID, String RE_STATUS, String RE_LOG, BigDecimal HANDLE_CNT, 
    							 String LEAD_RESPONSE_CODE, String REL_EMP_ID, Timestamp REL_DATETIME, String CAMP_PURPOSE, 
    							 Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.SEQNO = SEQNO;
        this.CAMPAIGN_ID = CAMPAIGN_ID;
        this.CAMPAIGN_NAME = CAMPAIGN_NAME;
        this.CAMPAIGN_DESC = CAMPAIGN_DESC;
        this.STEP_ID = STEP_ID;
        this.STEP_NAME = STEP_NAME;
        this.LEAD_SOURCE_ID = LEAD_SOURCE_ID;
        this.START_DATE = START_DATE;
        this.END_DATE = END_DATE;
        this.LEAD_USE_DP = LEAD_USE_DP;
        this.LEAD_TYPE = LEAD_TYPE;
        this.LEAD_PARA1 = LEAD_PARA1;
        this.LEAD_PARA2 = LEAD_PARA2;
        this.LEAD_DAYUSE = LEAD_DAYUSE;
        this.FILE_SEQ = FILE_SEQ;
        this.EXAM_ID = EXAM_ID;
        this.SALES_PITCH = SALES_PITCH;
        this.FIRST_CHANNEL = FIRST_CHANNEL;
        this.SECOND_CHANNEL = SECOND_CHANNEL;
        this.IMP_STATUS = IMP_STATUS;
        this.CHECK_STATUS = CHECK_STATUS;
        this.LE_TOTAL_CNT = LE_TOTAL_CNT;
        this.IM_TOTAL_CNT = IM_TOTAL_CNT;
        this.IM_AO_CNT = IM_AO_CNT;
        this.IM_OTHER_CNT = IM_OTHER_CNT;
        this.ER_CNT = ER_CNT;
        this.RV_LE_CNT = RV_LE_CNT;
        this.RV_REASON = RV_REASON;
        this.START_DT = START_DT;
        this.END_DT = END_DT;
        this.GIFT_CAMPAIGN_ID = GIFT_CAMPAIGN_ID;
        this.RE_STATUS = RE_STATUS;
        this.RE_LOG = RE_LOG;
        this.HANDLE_CNT = HANDLE_CNT;
        this.LEAD_RESPONSE_CODE = LEAD_RESPONSE_CODE;
        this.REL_EMP_ID = REL_EMP_ID;
        this.REL_DATETIME = REL_DATETIME;
        this.CAMP_PURPOSE = CAMP_PURPOSE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCAM_SFA_LEADS_IMPVO() {
    }

    /** minimal constructor */
    public TBCAM_SFA_LEADS_IMPVO(BigDecimal SEQNO) {
        this.SEQNO = SEQNO;
    }

    public BigDecimal getSEQNO() {
        return this.SEQNO;
    }

    public void setSEQNO(BigDecimal SEQNO) {
        this.SEQNO = SEQNO;
    }

    public String getCAMPAIGN_ID() {
        return this.CAMPAIGN_ID;
    }

    public void setCAMPAIGN_ID(String CAMPAIGN_ID) {
        this.CAMPAIGN_ID = CAMPAIGN_ID;
    }

    public String getCAMPAIGN_NAME() {
        return this.CAMPAIGN_NAME;
    }

    public void setCAMPAIGN_NAME(String CAMPAIGN_NAME) {
        this.CAMPAIGN_NAME = CAMPAIGN_NAME;
    }

    public String getCAMPAIGN_DESC() {
        return this.CAMPAIGN_DESC;
    }

    public void setCAMPAIGN_DESC(String CAMPAIGN_DESC) {
        this.CAMPAIGN_DESC = CAMPAIGN_DESC;
    }

    public String getSTEP_ID() {
        return this.STEP_ID;
    }

    public void setSTEP_ID(String STEP_ID) {
        this.STEP_ID = STEP_ID;
    }

    public String getSTEP_NAME() {
        return this.STEP_NAME;
    }

    public void setSTEP_NAME(String STEP_NAME) {
        this.STEP_NAME = STEP_NAME;
    }

    public String getLEAD_SOURCE_ID() {
        return this.LEAD_SOURCE_ID;
    }

    public void setLEAD_SOURCE_ID(String LEAD_SOURCE_ID) {
        this.LEAD_SOURCE_ID = LEAD_SOURCE_ID;
    }

    public Timestamp getSTART_DATE() {
        return this.START_DATE;
    }

    public void setSTART_DATE(Timestamp START_DATE) {
        this.START_DATE = START_DATE;
    }

    public Timestamp getEND_DATE() {
        return this.END_DATE;
    }

    public void setEND_DATE(Timestamp END_DATE) {
        this.END_DATE = END_DATE;
    }

    public String getLEAD_USE_DP() {
        return this.LEAD_USE_DP;
    }

    public void setLEAD_USE_DP(String LEAD_USE_DP) {
        this.LEAD_USE_DP = LEAD_USE_DP;
    }

    public String getLEAD_TYPE() {
        return this.LEAD_TYPE;
    }

    public void setLEAD_TYPE(String LEAD_TYPE) {
        this.LEAD_TYPE = LEAD_TYPE;
    }

    public String getLEAD_PARA1() {
        return this.LEAD_PARA1;
    }

    public void setLEAD_PARA1(String LEAD_PARA1) {
        this.LEAD_PARA1 = LEAD_PARA1;
    }

    public String getLEAD_PARA2() {
        return this.LEAD_PARA2;
    }

    public void setLEAD_PARA2(String LEAD_PARA2) {
        this.LEAD_PARA2 = LEAD_PARA2;
    }

    public BigDecimal getLEAD_DAYUSE() {
        return this.LEAD_DAYUSE;
    }

    public void setLEAD_DAYUSE(BigDecimal LEAD_DAYUSE) {
        this.LEAD_DAYUSE = LEAD_DAYUSE;
    }

    public BigDecimal getFILE_SEQ() {
        return this.FILE_SEQ;
    }

    public void setFILE_SEQ(BigDecimal FILE_SEQ) {
        this.FILE_SEQ = FILE_SEQ;
    }

    public String getEXAM_ID() {
        return this.EXAM_ID;
    }

    public void setEXAM_ID(String EXAM_ID) {
        this.EXAM_ID = EXAM_ID;
    }

    public String getSALES_PITCH() {
        return this.SALES_PITCH;
    }

    public void setSALES_PITCH(String SALES_PITCH) {
        this.SALES_PITCH = SALES_PITCH;
    }

    public String getFIRST_CHANNEL() {
        return this.FIRST_CHANNEL;
    }

    public void setFIRST_CHANNEL(String FIRST_CHANNEL) {
        this.FIRST_CHANNEL = FIRST_CHANNEL;
    }

    public String getSECOND_CHANNEL() {
        return this.SECOND_CHANNEL;
    }

    public void setSECOND_CHANNEL(String SECOND_CHANNEL) {
        this.SECOND_CHANNEL = SECOND_CHANNEL;
    }

    public String getIMP_STATUS() {
        return this.IMP_STATUS;
    }

    public void setIMP_STATUS(String IMP_STATUS) {
        this.IMP_STATUS = IMP_STATUS;
    }

    public String getCHECK_STATUS() {
        return this.CHECK_STATUS;
    }

    public void setCHECK_STATUS(String CHECK_STATUS) {
        this.CHECK_STATUS = CHECK_STATUS;
    }

    public BigDecimal getLE_TOTAL_CNT() {
        return this.LE_TOTAL_CNT;
    }

    public void setLE_TOTAL_CNT(BigDecimal LE_TOTAL_CNT) {
        this.LE_TOTAL_CNT = LE_TOTAL_CNT;
    }

    public BigDecimal getIM_TOTAL_CNT() {
        return this.IM_TOTAL_CNT;
    }

    public void setIM_TOTAL_CNT(BigDecimal IM_TOTAL_CNT) {
        this.IM_TOTAL_CNT = IM_TOTAL_CNT;
    }

    public BigDecimal getIM_AO_CNT() {
        return this.IM_AO_CNT;
    }

    public void setIM_AO_CNT(BigDecimal IM_AO_CNT) {
        this.IM_AO_CNT = IM_AO_CNT;
    }

    public BigDecimal getIM_OTHER_CNT() {
        return this.IM_OTHER_CNT;
    }

    public void setIM_OTHER_CNT(BigDecimal IM_OTHER_CNT) {
        this.IM_OTHER_CNT = IM_OTHER_CNT;
    }

    public BigDecimal getER_CNT() {
        return this.ER_CNT;
    }

    public void setER_CNT(BigDecimal ER_CNT) {
        this.ER_CNT = ER_CNT;
    }

    public BigDecimal getRV_LE_CNT() {
        return this.RV_LE_CNT;
    }

    public void setRV_LE_CNT(BigDecimal RV_LE_CNT) {
        this.RV_LE_CNT = RV_LE_CNT;
    }

    public String getRV_REASON() {
        return this.RV_REASON;
    }

    public void setRV_REASON(String RV_REASON) {
        this.RV_REASON = RV_REASON;
    }

    public Timestamp getSTART_DT() {
        return this.START_DT;
    }

    public void setSTART_DT(Timestamp START_DT) {
        this.START_DT = START_DT;
    }

    public Timestamp getEND_DT() {
        return this.END_DT;
    }

    public void setEND_DT(Timestamp END_DT) {
        this.END_DT = END_DT;
    }

    public String getGIFT_CAMPAIGN_ID() {
        return this.GIFT_CAMPAIGN_ID;
    }

    public void setGIFT_CAMPAIGN_ID(String GIFT_CAMPAIGN_ID) {
        this.GIFT_CAMPAIGN_ID = GIFT_CAMPAIGN_ID;
    }

    public String getRE_STATUS() {
        return this.RE_STATUS;
    }

    public void setRE_STATUS(String RE_STATUS) {
        this.RE_STATUS = RE_STATUS;
    }

    public String getRE_LOG() {
        return this.RE_LOG;
    }

    public void setRE_LOG(String RE_LOG) {
        this.RE_LOG = RE_LOG;
    }

    public BigDecimal getHANDLE_CNT() {
        return this.HANDLE_CNT;
    }

    public void setHANDLE_CNT(BigDecimal HANDLE_CNT) {
        this.HANDLE_CNT = HANDLE_CNT;
    }

    public String getLEAD_RESPONSE_CODE() {
        return this.LEAD_RESPONSE_CODE;
    }

    public void setLEAD_RESPONSE_CODE(String LEAD_RESPONSE_CODE) {
        this.LEAD_RESPONSE_CODE = LEAD_RESPONSE_CODE;
    }

    public String getREL_EMP_ID() {
        return this.REL_EMP_ID;
    }

    public void setREL_EMP_ID(String REL_EMP_ID) {
        this.REL_EMP_ID = REL_EMP_ID;
    }

    public Timestamp getREL_DATETIME() {
        return this.REL_DATETIME;
    }

    public void setREL_DATETIME(Timestamp REL_DATETIME) {
        this.REL_DATETIME = REL_DATETIME;
    }

    public String getCAMP_PURPOSE() {
		return CAMP_PURPOSE;
	}

	public void setCAMP_PURPOSE(String cAMP_PURPOSE) {
		CAMP_PURPOSE = cAMP_PURPOSE;
	}

	public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQNO", getSEQNO())
            .toString();
    }

}
