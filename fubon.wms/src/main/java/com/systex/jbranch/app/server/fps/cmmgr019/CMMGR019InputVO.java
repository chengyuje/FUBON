package com.systex.jbranch.app.server.fps.cmmgr019;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
public class CMMGR019InputVO {

    /**
     * 查詢參數
     */
    private String hostId;
    private String url;

    /**
     * 操作參數
     **/
    private boolean isLocal; // 是否為對本地操作 (否則是遠端操作) => 對於AP_SERVER來說，本地就是自己
    private String srcDir;
    private String srcFile;
    private boolean srcIsDir;
    private String desDir;
    private String desFile;
    private String seq;

    /**
     * 任務參數
     **/
    private String seqList;

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getSrcDir() {
        return srcDir;
    }

    public void setSrcDir(String srcDir) {
        this.srcDir = srcDir;
    }

    public String getSrcFile() {
        return srcFile;
    }

    public void setSrcFile(String srcFile) {
        this.srcFile = srcFile;
    }

    public String getDesDir() {
        return desDir;
    }

    public void setDesDir(String desDir) {
        this.desDir = desDir;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public boolean srcIsDir() {
        return srcIsDir;
    }

    public void setSrcIsDir(boolean srcIsDir) {
        this.srcIsDir = srcIsDir;
    }

    public String getSeqList() {
        return seqList;
    }

    public void setSeqList(String seqList) {
        this.seqList = seqList;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesFile() {
        return desFile;
    }

    public void setDesFile(String desFile) {
        this.desFile = desFile;
    }
}
