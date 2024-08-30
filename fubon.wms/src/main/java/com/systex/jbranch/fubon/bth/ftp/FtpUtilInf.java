package com.systex.jbranch.fubon.bth.ftp;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
/**
 * Created by Jemmy Tsai on 2017/08/10
 *
 * FTP介面，為FTP、SFTP及FTPS共同繼承
 */
public interface FtpUtilInf {
	// 顯示檔案清單
	public List<String> listNames(String remoteFilePath) throws JBranchException;
	// 顯示檔案清單，以Regex Pattern過濾
	public List<String> listNames(String srcdirectory, Pattern pattern) throws JBranchException;
	// 上傳
	public void upload(String srcdirectory, String srcfilename, String desdirectory, String desfilename)
			throws JBranchException, IOException;
	// 下載
	public void download(String srcdirectory, String srcfilename, String desdirectory, String desfilename)
			throws JBranchException;
	// 刪除
	public void delete(String remoteFilePath) throws JBranchException;
	// 讀檔
	public List<String> readFile(String srcdirectory, String srcfilename)
			throws JBranchException;
	// 存在與否, -1：不存在；正值：File Size
	public long exist(String srcdirectory, String srcfilename)
			throws JBranchException;
	// 讀檔編碼
	public static final String encoding = "UTF-8";
	// FTP平台預設編碼
	public static final String ftpEncoding = "ISO-8859-1";
}
