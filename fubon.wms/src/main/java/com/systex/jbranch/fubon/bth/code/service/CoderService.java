package com.systex.jbranch.fubon.bth.code.service;

import com.systex.jbranch.fubon.bth.code.coder.Coder;
import com.systex.jbranch.fubon.bth.code.config.FileCoderConfig;
import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.fubon.bth.job.business_logic.RptResolver;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * 利用傳入的 ftpCode 參數撈取加密 Key、欲加密檔案的路徑
 * 並且由自己的子類別負責提供進行加解密的實作
 */
public abstract class CoderService extends FubonWmsBizLogic {
    /** 提供 Coder 介面的實作 **/
    public abstract Coder provideCoder(String key, Charset charset) throws Exception;

    public void execute(String ftpCode) throws Exception {
        codeFile(getKey(ftpCode), getSourcePath(ftpCode));
    }

    /**
     * 以提供的加密 key 將目標加密
     **/
    private void codeFile(String key, Path source) throws Exception {
        FileCoderConfig cfg = new FileCoderConfig();
        cfg.setCodeType(Coder.ENCODE);
        cfg.setSourcePath(source);

        Charset sourceCharset = getSourceCharset(source);
        cfg.setCharset(sourceCharset);
        FileCoder.code(provideCoder(key, sourceCharset), cfg);
    }

    /**
     * 取得目標編碼
     **/
    private Charset getSourceCharset(Path source) throws IOException {
        return RptResolver.getFileCharset(source);
    }

    /**
     * 取得欲加密的來源路徑
     **/
    private Path getSourcePath(String ftpCode) throws Exception {
        List<Map<String, String>> data = Manager.manage(this.getDataAccessManager())
                .append("select SRCDIRECTORY, SRCFILENAME from TBSYSFTP ")
                .append("where FTPSETTINGID = :ftpCode ")
                .put("ftpCode", ftpCode)
                .query();

        if (data.isEmpty())
            throw new APException("無相關 FTP 設定");

        BthFtpJobUtil util = new BthFtpJobUtil();
        return Paths.get(
                util.replaceRootPath(
                        data.get(0).get("SRCDIRECTORY") +
                                File.separator +
                                util.getRealFileName(data.get(0).get("SRCFILENAME"))));
    }

    /**
     * 取得加密 Key
     **/
    private String getKey(String ftpCode) throws JBranchException {
        List<Map<String, String>> data = Manager.manage(this.getDataAccessManager())
                .append("select KEY from TBSYSFTPKEY ")
                .append("where FTPSETTINGID = :ftpCode ")
                .put("ftpCode", ftpCode)
                .query();

        String key;
        if (data.isEmpty() || StringUtils.isBlank(key = data.get(0).get("KEY")))
            throw new APException("沒有相關加密 Key 設定！");
        return key;
    }
}
