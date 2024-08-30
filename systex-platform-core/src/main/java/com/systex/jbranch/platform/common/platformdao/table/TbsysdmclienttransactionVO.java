package com.systex.jbranch.platform.common.platformdao.table;

import com.systex.jbranch.platform.common.dataManager.BizlogicVO;
import com.systex.jbranch.platform.common.dataManager.PlatFormVO;

import java.io.Serializable;

/**
 * @author Alex Lin
 * @version 2011/03/03 12:03 PM
 */
public class TbsysdmclienttransactionVO implements Serializable {
// ------------------------------ FIELDS ------------------------------

    private long version;
    private PlatFormVO platformVO;
    private BizlogicVO bizlogicVO;
    private TbsysdmsectionVO section;
    private String sectionID;

// --------------------- GETTER / SETTER METHODS ---------------------

    public BizlogicVO getBizlogicVO() {
        return bizlogicVO;
    }

    public void setBizlogicVO(BizlogicVO bizlogicVO) {
        this.bizlogicVO = bizlogicVO;
    }

    public PlatFormVO getPlatformVO() {
        return platformVO;
    }

    public void setPlatformVO(PlatFormVO platformVO) {
        this.platformVO = platformVO;
    }

    public TbsysdmsectionVO getSection() {
        return section;
    }

    public void setSection(TbsysdmsectionVO section) {
        this.section = section;
    }

    public String getSectionID() {
        return sectionID;
    }

    public void setSectionID(String sectionID) {
        this.sectionID = sectionID;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.common.platformdao.table.TbsysdmclienttransactionVO{" +
                "version=" + version +
                ", platformVO=" + platformVO +
                ", bizlogicVO=" + bizlogicVO +
                ", section=" + section +
                ", sectionID='" + sectionID + '\'' +
                '}';
    }
}
