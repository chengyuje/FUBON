package com.systex.jbranch.platform.common.dataManager.impl.db;

import com.systex.jbranch.platform.common.dataManager.Branch;
import com.systex.jbranch.platform.common.dataManager.DataManagerIF;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;

import java.util.Map;

/**
 * @author Alex Lin
 * @version 2011/03/07 1:31 PM
 */
public class BranchDB extends Branch {
    private DataManagerIF dataManager;

    BranchDB(String branchId) throws JBranchException {
        this.dataManager = (DataManagerIF) PlatformContext.getBean("dataManager");
        this.setBizlogicVO(new BranchBizlogicVODB(branchId));
        this.setPlatformVO(new BranchPlatformVODB(branchId));
    }

    @Override
    public Map<String, WorkStation> getWorkStations() {
        return super.getWorkStations();
    }

    @Override
    public boolean existWorkStation(String workStationID) {
        return super.existWorkStation(workStationID);
    }

    @Override
    public WorkStation getWorkStation(String workStationID) {
        return dataManager.getWorkStation(this.getBrchID(), workStationID);
    }

    @Override
    public void setWorkStation(String workStationID, WorkStation ws) {
        dataManager.setWorkStation(this.getBrchID(), workStationID, ws);
    }

    @Override
    public boolean deleteWorkStation(String workStationID) {
        UUID uuid = new UUID();
        uuid.setBranchID(this.getBrchID());
        uuid.setWsId(workStationID);
        return dataManager.deleteWorkStation(uuid);
    }
}
