package com.systex.jbranch.app.server.fps.cmmgr018;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CMMGR018InputVO extends PagingOutputVO {
    private String logDirPath;
    private String logName;
    private String zipPath;

    public String getLogDirPath() {
        return logDirPath;
    }

    public void setLogDirPath(String logDirPath) {
        this.logDirPath = logDirPath;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public String getZipPath() {
        return zipPath;
    }

    public void setZipPath(String zipPath) {
        this.zipPath = zipPath;
    }
}
