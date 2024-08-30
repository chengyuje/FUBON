package com.systex.jbranch.app.server.fps.ins970;

import java.util.List;
import java.util.Map;

public class INS970OutputVO {
    /**
     * 檔案詳細資訊列表
     **/
    private List<Map<String, Object>> files;
    /**
     * 上一層路徑
     **/
    private String prevUrl;
    /**
     * 校正 Url
     **/
    private String url;
    /**
     * 使用者等待下載的任務結果
     **/
    private List<Map<String, Object>> missionResult;

    public List<Map<String, Object>> getFiles() {
        return files;
    }

    public void setFiles(List<Map<String, Object>> files) {
        this.files = files;
    }

    public String getPrevUrl() {
        return prevUrl;
    }

    public void setPrevUrl(String prevUrl) {
        this.prevUrl = prevUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Map<String, Object>> getMissionResult() {
        return missionResult;
    }

    public void setMissionResult(List<Map<String, Object>> missionResult) {
        this.missionResult = missionResult;
    }
}
