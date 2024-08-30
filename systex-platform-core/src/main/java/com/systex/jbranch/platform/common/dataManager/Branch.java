package com.systex.jbranch.platform.common.dataManager;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
//import javax.servlet.http.HttpSession;

/**
 * 存放各分行資料。<br>
 *
 * @author Eric.Lin。
 */

public class Branch {
    private String brchID;
    private String name;
    private String opDate;
    private String txnFlag;
    private PlatFormVO platformVO;
    private BizlogicVO bizlogicVO;

    private Map<String, WorkStation> workStations = new ConcurrentHashMap<String, WorkStation>();

    public PlatFormVO getPlatformVO() {
        return platformVO;
    }

    public void setPlatformVO(PlatFormVO platformVO) {
        this.platformVO = platformVO;
    }

    public BizlogicVO getBizlogicVO() {
        return bizlogicVO;
    }

    public void setBizlogicVO(BizlogicVO bizlogicVO) {
        this.bizlogicVO = bizlogicVO;
    }

    /** @return 分行代號。<br> */
    public String getBrchID() {
        return brchID;
    }

    /** @return the name:分行名稱。<br> */
    public String getName() {
        return name;
    }

    /** @return the opDate:<br> */
    public String getOpDate() {
        return opDate;
    }

    /** @return the txnFlag:<br> */
    public String getTxnFlag() {
        return txnFlag;
    }

    /** @param brchID :分行代號。 */
    public void setBrchID(String brchID) {
        this.brchID = brchID;
    }

    /** @param name :分行名稱。 */
    public void setName(String name) {
        this.name = name;
    }

    /** @param opDate the opDate to set */
    public void setOpDate(String opDate) {
        this.opDate = opDate;
    }

    /** @param txnFlag the txnFlag to set<br> */
    public void setTxnFlag(String txnFlag) {
        this.txnFlag = txnFlag;
    }

    /** @return 所有已登入的所有工作站<br> */
//	public Map<String, WorkStation> getWorkStations() {
//		return this.workStations;
//	}
    public Map<String, WorkStation> getWorkStations() {
        return Collections.unmodifiableMap(this.workStations);
    }

    public boolean existWorkStation(String workStationID) {
        return this.workStations.containsKey(workStationID);
    }

    /**
     * 判斷分行裏的工作站是否存在
     *
     * @param workStationID 工作站ID
     * @return 工作站
     */
    public WorkStation getWorkStation(String workStationID) {

        //原始寫法會造成誤判
        //Ex   工作站簽退，但因為某處呼叫到，故又新增了 WorkStation
//charlotte 20081028 mark
//		if (ws == null){
//			ws= new WorkStation();
//	  		ws.setBrchID(this.brchID);
//	  		ws.setWsID(workStationID);
//	  		setWorkStation(workStationID, ws);
//		}
        return this.workStations.get(workStationID);
    }


    /**
     * 設定工作站於分行中。<br>
     *
     * @param workStationID:工作站代號。<br>
     * @param ws:workstation           instance。<br>
     */
    public void setWorkStation(String workStationID, WorkStation ws) {
        this.workStations.put(workStationID, ws);
    }


    /**
     * 刪除分行中的工作站。<br>
     *
     * @param workStationID:工作站代號<br>
     * @return true:正常刪除;false:不正常刪除，可能不存在。<br>
     */
    public boolean deleteWorkStation(String workStationID) {
        return this.workStations.remove(workStationID) != null;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("com.systex.jbranch.platform.common.dataManager.Branch");
        sb.append("{brchID='").append(brchID).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", opDate='").append(opDate).append('\'');
        sb.append(", txnFlag='").append(txnFlag).append('\'');
        sb.append(", workStations=").append(workStations);
        sb.append('}');
        return sb.toString();
    }
}

