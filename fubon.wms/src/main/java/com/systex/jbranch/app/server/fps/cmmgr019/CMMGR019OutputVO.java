package com.systex.jbranch.app.server.fps.cmmgr019;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("request")
public class CMMGR019OutputVO {
    private List fileInfoList;
    private List hostList;
    private List contentList;
    private List missionResult;

    public List getFileInfoList() {
        return fileInfoList;
    }

    public void setFileInfoList(List fileInfoList) {
        this.fileInfoList = fileInfoList;
    }

    public List getHostList() {
        return hostList;
    }

    public void setHostList(List hostList) {
        this.hostList = hostList;
    }

    public List getContentList() {
        return contentList;
    }

    public void setContentList(List contentList) {
        this.contentList = contentList;
    }

    public List getMissionResult() {
        return missionResult;
    }

    public void setMissionResult(List missionResult) {
        this.missionResult = missionResult;
    }
}
