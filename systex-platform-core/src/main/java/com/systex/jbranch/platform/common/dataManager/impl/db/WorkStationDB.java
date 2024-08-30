package com.systex.jbranch.platform.common.dataManager.impl.db;

import com.systex.jbranch.platform.common.dataManager.DataManagerIF;
import com.systex.jbranch.platform.common.dataManager.Section;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataManager.User;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;

import java.util.Hashtable;
import java.util.Map;

/**
 * @author Alex Lin
 * @version 2011/03/07 1:29 PM
 */
public class WorkStationDB extends WorkStation {
    private DataManagerIF dataManager;

    public WorkStationDB() throws JBranchException {
        this.dataManager = (DataManagerIF) PlatformContext.getBean("dataManager");
    }

    @Override
    public Map<String, Section> getSection() {
        return super.getSection();
    }

    @Override
    public Section getSection(String sectionID) {
        UUID uuid = new UUID();
        uuid.setWsId(this.getWsID());
        uuid.setBranchID(this.getBrchID());
        uuid.setSectionID(sectionID);
        return dataManager.getSection(uuid);
    }

    @Override
    public void setSection(String sectID, Section sect) {
        UUID uuid = new UUID();
        uuid.setWsId(this.getWsID());
        uuid.setBranchID(this.getBrchID());
        uuid.setSectionID(sectID);
        dataManager.setSection(uuid, sect);
    }

    @Override
    public void setSection(Hashtable<String, Section> section) {
        super.setSection(section);
    }

    @Override
    public boolean deleteSection(String sectionID) {
        UUID uuid = new UUID();
        uuid.setWsId(this.getWsID());
        uuid.setBranchID(this.getBrchID());
        uuid.setSectionID(sectionID);
        return dataManager.deleteSection(uuid);
    }

    public void setUserVO(User user) {
        this.user = user;
    }

    @Override
    public void setUser(User user) {
        UUID uuid = new UUID();
        uuid.setWsId(this.getWsID());
        uuid.setBranchID(this.getBrchID());
        if (user != null) {
            uuid.setTellerId(user.getUserID());
        }
        dataManager.setUser(uuid, user);
        setUserVO(user);
    }
}
