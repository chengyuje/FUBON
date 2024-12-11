package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCAM_SFA_CAMPAIGNVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMPAIGNPK comp_id;

    /** nullable persistent field */
    private String CAMPAIGN_NAME;

    /** nullable persistent field */
    private String CAMPAIGN_DESC;

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
    private Timestamp START_DT;

    /** nullable persistent field */
    private Timestamp END_DT;

    /** nullable persistent field */
    private String GIFT_CAMPAIGN_ID;

    /** nullable persistent field */
    private String REMOVE_FLAG;

    /** nullable persistent field */
    private String LEAD_RESPONSE_CODE;
    
    /** nullable persistent field */
    private String CAMP_PURPOSE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMPAIGN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCAM_SFA_CAMPAIGNVO(com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMPAIGNPK comp_id, String CAMPAIGN_NAME, String CAMPAIGN_DESC, String STEP_NAME, String LEAD_SOURCE_ID, 
    							Timestamp START_DATE, Timestamp END_DATE, String LEAD_USE_DP, String LEAD_TYPE, String LEAD_PARA1, String LEAD_PARA2, BigDecimal LEAD_DAYUSE, 
    							BigDecimal FILE_SEQ, String EXAM_ID, String SALES_PITCH, String FIRST_CHANNEL, String SECOND_CHANNEL, Timestamp START_DT, Timestamp END_DT, 
    							String GIFT_CAMPAIGN_ID, String REMOVE_FLAG, String LEAD_RESPONSE_CODE, String CAMP_PURPOSE, 
    							Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.CAMPAIGN_NAME = CAMPAIGN_NAME;
        this.CAMPAIGN_DESC = CAMPAIGN_DESC;
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
        this.START_DT = START_DT;
        this.END_DT = END_DT;
        this.GIFT_CAMPAIGN_ID = GIFT_CAMPAIGN_ID;
        this.REMOVE_FLAG = REMOVE_FLAG;
        this.LEAD_RESPONSE_CODE = LEAD_RESPONSE_CODE;
        this.CAMP_PURPOSE = CAMP_PURPOSE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCAM_SFA_CAMPAIGNVO() {
    }

    /** minimal constructor */
    public TBCAM_SFA_CAMPAIGNVO(com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMPAIGNPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMPAIGNPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMPAIGNPK comp_id) {
        this.comp_id = comp_id;
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

    public String getREMOVE_FLAG() {
        return this.REMOVE_FLAG;
    }

    public void setREMOVE_FLAG(String REMOVE_FLAG) {
        this.REMOVE_FLAG = REMOVE_FLAG;
    }

    public String getLEAD_RESPONSE_CODE() {
        return this.LEAD_RESPONSE_CODE;
    }

    public void setLEAD_RESPONSE_CODE(String LEAD_RESPONSE_CODE) {
        this.LEAD_RESPONSE_CODE = LEAD_RESPONSE_CODE;
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
            .append("comp_id", getcomp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBCAM_SFA_CAMPAIGNVO) ) return false;
        TBCAM_SFA_CAMPAIGNVO castOther = (TBCAM_SFA_CAMPAIGNVO) other;
        return new EqualsBuilder()
            .append(this.getcomp_id(), castOther.getcomp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getcomp_id())
            .toHashCode();
    }

}
