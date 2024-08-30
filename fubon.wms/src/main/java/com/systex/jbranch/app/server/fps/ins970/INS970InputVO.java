package com.systex.jbranch.app.server.fps.ins970;

import java.util.List;

public class INS970InputVO {
    /**
     * 使用者下載序號
     **/
    private String seq;
    /**
     * 目前路徑
     **/
    private String url;
    /**
     * 使用者下載的檔案是否為目錄
     **/
    private String fileIsDir;
    /**
     * 使用者下載檔名目錄
     **/
    private String fileParent;
    /**
     * 使用者下載檔名
     **/
    private String fileName;
    /**
     * 使用者下載檔案大小
     */
    private long fileSize;
    /**
     * 使用者等待下載的任務序號
     **/
    private List<String> seqList;

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

    public String getFileParent() {
        return fileParent;
    }

    public void setFileParent(String fileParent) {
        this.fileParent = fileParent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileIsDir() {
        return fileIsDir;
    }

    public void setFileIsDir(String fileIsDir) {
        this.fileIsDir = fileIsDir;
    }

    public List<String> getSeqList() {
        return seqList;
    }

    public void setSeqList(List<String> seqList) {
        this.seqList = seqList;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
