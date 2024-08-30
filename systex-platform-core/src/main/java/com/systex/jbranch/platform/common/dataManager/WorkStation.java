package com.systex.jbranch.platform.common.dataManager;

import com.systex.jbranch.platform.common.multiLang.MultiLangFacadeIF;

import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WorkStation implements Map {

 	@Deprecated
	public static final String WS_INFO_FPS_HTGSESSIONID="ws.info.fps.htg.sessionid";
	@Deprecated
	public static final String WS_INFO_FPS_HTGCOOKIE="ws.info.fps.htg.cookie";
	@Deprecated
	public static final String WS_INFO_FPS_HTG_ESTABLISHSESSION_TYPE="ws.info.fps.htg.establishsession.type";
	@Deprecated
	public static final String WS_INFO_FPS_HTG_ESTABLISHSESSION_DEPID="ws.info.fps.htg.establishsession.depid";

    protected String wsID;
    protected String signOnBranchID;
    protected String brchID;
    protected String status;
    protected Map<String, Section> section = new ConcurrentHashMap<String, Section>();
    protected String wsIP;
    protected String macAddress;
    protected String developMode;
    protected String localMode;
    protected String signOnWsID;
    protected User user;
    protected MultiLangFacadeIF language;
    protected EnumWorkStationType type;
    protected long touchedTime = java.lang.System.currentTimeMillis();
    private Map wsMap = new Hashtable();
    protected String applicationID;

    /**
     * 取得語系。<br>
     *
     * @return<br>
     */

    public MultiLangFacadeIF getLanguage() {
        return this.language;
    }

    /**
     * 設定語系。<br>
     *
     * @param language<br>
     */
    public void setLanguage(MultiLangFacadeIF language) {
        this.language = language;
    }

    /** @return the developMode */
    public String getDevelopMode() {
        return this.developMode;
    }

    /** @return the localMode */
    public String getLocalMode() {
        return this.localMode;
    }

    /**
     * 取得所有在此工作站的工作區。<br>
     *
     * @return <br>
     */
    public Map<String, Section> getSection() {
        return Collections.unmodifiableMap(this.section);
    }

    /**
     * 取得在此工作站的工作區。<br>
     *
     * @param sectionID:工作區代號。<br>
     * @return
     */
    public Section getSection(String sectionID) {
        return this.section.get(sectionID);
    }

    /** @return the signOnBranchID */
    public String getSignOnBranchID() {
        return this.signOnBranchID;
    }

    /** @return the signOnWsID */
    public String getSignOnWsID() {
        return this.signOnWsID;
    }

    /**
     * 取得User<br>
     *
     * @return the user<br>
     */
    public User getUser() {
        if (this.user == null) {
            this.user = new User();
        }
        return this.user;
    }

    /**
     * 取得工作站代號。<br>
     *
     * @return the wdIs
     */
    public String getWsID() {
        return this.wsID;
    }

    public long getTouchedTime() {
        return touchedTime;
    }

    public void setTouchedTime(long touchedTime) {
        this.touchedTime = touchedTime;
    }

    /**
     * 取得工作站種類。<br>
     *
     * @return the type
     */
    public EnumWorkStationType getType() {
        return this.type;
    }

    /** @param developMode the developMode to set */
    public void setDevelopMode(String developMode) {
        this.developMode = developMode;
    }

    /** @param localMode the localMode to set */
    public void setLocalMode(String localMode) {
        this.localMode = localMode;
    }
    /**
     * @param section the section to set
     */
//	public void setSection(HashMap section) {
//		this.section = section;
//	}

    /** 設定工作區於工作站中。<br> */
    public void setSection(String sectID, Section sect) {
        this.section.put(sectID, sect);
    }

    /** @param signOnBranchID the signOnBranchID to set */
    public void setSignOnBranchID(String signOnBranchID) {
        this.signOnBranchID = signOnBranchID;
    }

    /** @param signOnWsID the signOnWsID to set */
    public void setSignOnWsID(String signOnWsID) {
        this.signOnWsID = signOnWsID;
    }

    /**
     * 設定User於工作站中。<br>
     *
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * 設定工作站代號。<br>
     *
     * @param wdIs the wdIs to set
     */
    public void setWsID(String wsID) {
        this.wsID = wsID;
    }

    /**
     * 設定工作站種類。<br>
     *
     * @param type the type to set
     */
    public void setType(EnumWorkStationType type) {
        this.type = type;
    }

    /**
     * 取得工作站IP。<br>
     *
     * @return the wsIP
     */
    public String getWsIP() {
        return this.wsIP;
    }

    /**
     * 設定工作站IP。<br>
     *
     * @param wsIP the wsIP to set<br>
     */
    public void setWsIP(String wsIP) {
        this.wsIP = wsIP;
    }

    /** @return the brchID<br> */
    public String getBrchID() {
        return this.brchID;
    }

    /**
     * 設定所屬分行代號。<br>
     *
     * @param brchID the brchID to set
     */
    public void setBrchID(String brchID) {
        this.brchID = brchID;
    }

    /** @return the applicationID<br> */
    public String getApplicationID() {
        return this.applicationID;
    }

    /**
     * 設定工作站session id。<br>
     *
     * @param applicationID the applicationID to set
     */
    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }


    /** 設定多個工作區於工作站中。<br> */
    public void setSection(Hashtable<String, Section> section) {
        this.section = section;
    }

    /**
     * 刪除工作區。	<br>
     *
     * @param sectionID:工作區代號。<br>
     * @return true:正常刪除;false:不正常刪除，可能不存在。<br>
     */
    public boolean deleteSection(String sectionID) {
        return this.section.remove(sectionID) != null;
    }

    public void clear() {
        wsMap.clear();
    }

    public boolean containsKey(Object key) {
        return wsMap.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return wsMap.containsValue(value);
    }

    public Set entrySet() {
        return wsMap.entrySet();
    }

    public Object get(Object key) {
        return wsMap.get(key);
    }

    public boolean isEmpty() {
        return wsMap.isEmpty();
    }

    public Set keySet() {
        return wsMap.keySet();
    }

	public Object put(Object key, Object value) 
	{
		Object o=null;
		
		if(key!=null)
		{
			o=wsMap.remove(key);
			if(value!=null)
				wsMap.put(key, value);
		}
		return o;
	}
    public void putAll(Map t) {
        wsMap.putAll(t);
    }

    public Object remove(Object key) {
        return wsMap.remove(key);
    }

    public int size() {
        return wsMap.size();
    }

    public Collection values() {
        return wsMap.values();
    }
    
    /**
	 * @return the macAddress
	 */
	public String getMacAddress() {
		return macAddress;
	}

	/**
	 * @param macAddress the macAddress to set
	 */
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	@Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("com.systex.jbranch.platform.common.dataManager.WorkStation");
        sb.append("{wsID='").append(wsID).append('\'');
        sb.append(", signOnBranchID='").append(signOnBranchID).append('\'');
        sb.append(", brchID='").append(brchID).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", section=").append(section);
        sb.append(", wsIP='").append(wsIP).append('\'');
        sb.append(", macAddress='").append(macAddress).append('\'');
        sb.append(", developMode='").append(developMode).append('\'');
        sb.append(", localMode='").append(localMode).append('\'');
        sb.append(", signOnWsID='").append(signOnWsID).append('\'');
        sb.append(", user=").append(user);
        sb.append(", language=").append(language);
        sb.append(", type=").append(type);
        sb.append(", touchedTime=").append(touchedTime);
        sb.append(", wsMap=").append(wsMap);
        sb.append(", applicationID='").append(applicationID).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
