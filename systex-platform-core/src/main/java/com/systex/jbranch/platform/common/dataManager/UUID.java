package com.systex.jbranch.platform.common.dataManager;

/**
 * 作為存取DataManager各物件是依據，主要的組成是分行代號，工作站代號，工作區代號。<br>
 * 由Conversation模組產生此物件。<br>
 *
 * @author Eric.Lin<br>
 */

public class UUID {
    private String branchID = "";
    private String wsId = "";
    private String sectionID = "";
    private String tellerId = "";
    private String locale;

    /**
     * 取得分行代號。<br>
     *
     * @return<br>
     */
    public String getBranchID() {
        return branchID;
    }

    /**
     * 設定分行代號。<br>
     *
     * @param branchID<br>
     */
    public void setBranchID(String branchID) {
        this.branchID = branchID;
    }

    /**
     * 取得工作區代號。<br>
     *
     * @return<br>
     */
    public String getSectionID() {
        return sectionID;
    }

    /**
     * 設定工作區代號。<br>
     *
     * @param sectionID<br>
     */
    public void setSectionID(String sectionID) {
        this.sectionID = sectionID;
    }

    /**
     * 取得工作站代號。<br>
     *
     * @return<br>
     */
    public String getWsId() {
        return wsId;
    }

    /**
     * 設定工作站代號。<br>
     *
     * @param wsId<br>
     */
    public void setWsId(String wsId) {
        this.wsId = wsId;
    }

    /**
     * 取得櫃員代號
     *
     * @return 櫃員代號
     */
    public String getTellerID() {
        return tellerId;
    }

    /**
     * 設定櫃員代號
     *
     * @param tellerID 櫃員代號
     */
    public void setTellerID(String tellerID) {
        this.tellerId = tellerID;
    }

    /**
     * 取得語系
     * @return 語系
     */
    public String getLocale() {
        return locale;
    }

    /**
     * 設定語系
     * @param locale 語系
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTellerId() {
        return tellerId;
    }

    public void setTellerId(String tellerId) {
        this.tellerId = tellerId;
    }

    @Override
    public String toString() {
        return getBranchID() + "-" +
                getWsId() + "-" +
                getTellerID() + "-" +
                getSectionID();
    }
}
