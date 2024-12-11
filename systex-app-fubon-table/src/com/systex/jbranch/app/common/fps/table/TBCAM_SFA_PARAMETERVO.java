package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBCAM_SFA_PARAMETERVO extends VOBase {

    /** identifier field */
    private String SFA_PARA_ID;

    /** nullable persistent field */
    private String CAMPAIGN_ID;

    /** nullable persistent field */
    private String CAMPAIGN_NAME;

    /** nullable persistent field */
    private String CAMPAIGN_DESC;

    /** nullable persistent field */
    private String LEAD_SOURCE_ID;

    /** nullable persistent field */
    private String LEAD_RESPONSE_CODE;

    /** nullable persistent field */
    private String LEAD_TYPE;

    /** nullable persistent field */
    private String LEAD_PARA1;

    /** nullable persistent field */
    private String LEAD_PARA2;

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
    private String CAMP_PURPOSE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBCAM_SFA_PARAMETER";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBCAM_SFA_PARAMETERVO(String SFA_PARA_ID, String CAMPAIGN_ID, String CAMPAIGN_NAME, String CAMPAIGN_DESC, String LEAD_SOURCE_ID, String LEAD_RESPONSE_CODE, String LEAD_TYPE, String LEAD_PARA1, String LEAD_PARA2, String EXAM_ID, String SALES_PITCH, String FIRST_CHANNEL, String SECOND_CHANNEL, Timestamp START_DT, Timestamp END_DT, String GIFT_CAMPAIGN_ID, String CAMP_PURPOSE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.SFA_PARA_ID = SFA_PARA_ID;
        this.CAMPAIGN_ID = CAMPAIGN_ID;
        this.CAMPAIGN_NAME = CAMPAIGN_NAME;
        this.CAMPAIGN_DESC = CAMPAIGN_DESC;
        this.LEAD_SOURCE_ID = LEAD_SOURCE_ID;
        this.LEAD_RESPONSE_CODE = LEAD_RESPONSE_CODE;
        this.LEAD_TYPE = LEAD_TYPE;
        this.LEAD_PARA1 = LEAD_PARA1;
        this.LEAD_PARA2 = LEAD_PARA2;
        this.EXAM_ID = EXAM_ID;
        this.SALES_PITCH = SALES_PITCH;
        this.FIRST_CHANNEL = FIRST_CHANNEL;
        this.SECOND_CHANNEL = SECOND_CHANNEL;
        this.START_DT = START_DT;
        this.END_DT = END_DT;
        this.GIFT_CAMPAIGN_ID = GIFT_CAMPAIGN_ID;
        this.CAMP_PURPOSE = CAMP_PURPOSE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBCAM_SFA_PARAMETERVO() {
    }

    /** minimal constructor */
    public TBCAM_SFA_PARAMETERVO(String SFA_PARA_ID) {
        this.SFA_PARA_ID = SFA_PARA_ID;
    }

    public String getSFA_PARA_ID() {
        return this.SFA_PARA_ID;
    }

    public void setSFA_PARA_ID(String SFA_PARA_ID) {
        this.SFA_PARA_ID = SFA_PARA_ID;
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

    public String getLEAD_SOURCE_ID() {
        return this.LEAD_SOURCE_ID;
    }

    public void setLEAD_SOURCE_ID(String LEAD_SOURCE_ID) {
        this.LEAD_SOURCE_ID = LEAD_SOURCE_ID;
    }

    public String getLEAD_RESPONSE_CODE() {
        return this.LEAD_RESPONSE_CODE;
    }

    public void setLEAD_RESPONSE_CODE(String LEAD_RESPONSE_CODE) {
        this.LEAD_RESPONSE_CODE = LEAD_RESPONSE_CODE;
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
            .append("SFA_PARA_ID", getSFA_PARA_ID())
            .toString();
    }

}
