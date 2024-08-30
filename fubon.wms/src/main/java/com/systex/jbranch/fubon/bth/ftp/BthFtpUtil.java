package com.systex.jbranch.fubon.bth.ftp;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

/**
 * Created by SebastianWu on 2016/06/20
 * modified by Jemmy Tsai on 2017/08/10
 *
 * Based on BthFtpUtil.java
 * 2017/08/10：改為過水用的程式，導向BthFtpJobUtil。
 */
public class BthFtpUtil {

    // ------------------------------ FIELDS ------------------------------
    private String hostId;
// --------------------------- CONSTRUCTORS ---------------------------

    public BthFtpUtil() {
    }

	public BthFtpUtil(String hostId) throws Exception {
    	this();
        this.hostId = hostId;
    }

    /**
     * 使用Regex查詢FTP Server指定目錄下的檔案
     *
     * @param srcdirectory
     * @param pattern
     * @return
     * @throws Exception
     */
    public List<String> listNames(String srcdirectory, Pattern pattern) throws Exception {
    	BthFtpJobUtil jobUtil = new BthFtpJobUtil();
    	return jobUtil.listNames(this.hostId, srcdirectory, pattern);
    }

    /**
     * 取得正確的檔名
     * 日期格式須以大括弧{}包覆之
     *
     * 目前可使用的六種日期格式:
     * {yyyy-MM-dd-HH} | {yy-MM-dd-HH}
     * {yyyy_MM_dd_HH} | {yy_MM_dd_HH}
     * {yyyyMMddHH}   | {yyMMddHH}
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public String getRealFileName(String fileName) throws Exception {
    	BthFtpJobUtil jobUtil = new BthFtpJobUtil();
    	return jobUtil.getRealFileName(fileName);
    }

    public void disConnection() {
    	
    }

    public void connect() throws Exception {
    	
    }
    
    public void setRetryCount(int cnt) {
    	
    }
    
    /**
     * 讀取 FTP Server 上指定檔案內容
     *
     * @version 2016-07-29
     * @param ftpDirectory
     * @param ftpFilename
     * @return
     */
    public List<String> readFtpFile(String ftpDirectory, String ftpFilename) throws Exception {
        if(StringUtils.isBlank(ftpDirectory) || StringUtils.isBlank(ftpFilename)){
            throw new JBranchException("需輸入FTP路徑與檔名");
        }

        BthFtpJobUtil jobUtil = new BthFtpJobUtil();
        return jobUtil.readFile(this.hostId, ftpDirectory, ftpFilename);
    }
}