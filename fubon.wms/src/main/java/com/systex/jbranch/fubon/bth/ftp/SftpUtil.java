package com.systex.jbranch.fubon.bth.ftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSch;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSREMOTEHOSTVO;
import com.systex.jbranch.platform.common.security.impl.JBranchCryptology;

/**
 * Created by Jemmy Tsai on 2017/08/10
 *
 * SFTP實作FtpUtilInf內容
 */
public class SftpUtil implements FtpUtilInf {
	private TBSYSREMOTEHOSTVO hostVO;
	private Logger logger = LoggerFactory.getLogger(SftpUtil.class);

	public SftpUtil(TBSYSREMOTEHOSTVO hostVO) {
		this.hostVO = hostVO;
		JSch.setConfig("kex", "diffie-hellman-group1-sha1");
	}

	private String createConnectionString(String remoteFilePath) {
		String password = JBranchCryptology.decodePassword(this.hostVO.getPASSWORD());
		return "sftp://" + this.hostVO.getUSERNAME() + ":" + password + "@" + this.hostVO.getIP() + ":"
				+ this.hostVO.getPORT() + "/" + remoteFilePath;
	}

	private FileSystemOptions createDefaultOptions() throws FileSystemException {
		// Create SFTP options
		FileSystemOptions opts = new FileSystemOptions();

		// SSH Key checking
		SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");

		// Root directory set to user home
		SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);

		// Timeout is count by Milliseconds
		SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000);

		return opts;
	}

	/**
	 * 查詢SFTP Server指定目錄下的檔案
	 *
	 * @param remoteFilePath
	 * @return
	 * @throws Exception
	 */
	public List<String> listNames(String remoteFilePath) throws JBranchException {
		StandardFileSystemManager manager = new StandardFileSystemManager();
		List<String> result = new ArrayList<String>();
		try {
			manager.init();

			FileObject remoteFile = manager.resolveFile(createConnectionString(CleanPath.cleanString(remoteFilePath)),
					createDefaultOptions());
			if (!remoteFile.exists()) {
				throw new JBranchException("指定路徑不存在[" + remoteFilePath + "]");
			}

			int size = remoteFile.getChildren().length;
			if (size == 0) {
				return result;
			}
			for (int i = 0, n = size; i < n; i++)
				result.add(remoteFile.getChildren()[i].getName().getBaseName());
			return result;
		} catch (JBranchException e1) {
			logger.warn("檔案不存在:{}", remoteFilePath);
			throw e1;
		} catch (Exception e) {
			logger.warn("listNames fail", e);
			throw new JBranchException("無法取得檔案清單[" + remoteFilePath + "]");
		} finally {
			manager.close();
		}
	}

	/**
	 * 查詢SFTP Server指定目錄下的檔案
	 *
	 * @param remoteFilePath
	 *            遠端路徑
	 * @param pattern
	 *            比對表達式，可為null表示不比對
	 * @return List<String> 檔名清單
	 * @throws JBranchException
	 */
	public List<String> listNames(String remoteFilePath, Pattern pattern) throws JBranchException {
		StandardFileSystemManager manager = new StandardFileSystemManager();
		List<String> result = new ArrayList<String>();
		try {
			manager.init();

			FileObject remoteFile = manager.resolveFile(createConnectionString(CleanPath.cleanString(remoteFilePath)),
					createDefaultOptions());
			if (!remoteFile.exists()) {
				throw new JBranchException("指定路徑不存在[" + remoteFilePath + "]");
			}

			int size = remoteFile.getChildren().length;
			if (size == 0) {
				return result;
			}
			if (pattern == null)
				for (int i = 0, n = size; i < n; i++)
					result.add(remoteFile.getChildren()[i].getName().getBaseName());
			else {
				for (int i = 0, n = size; i < n; i++) {
					String filename = remoteFile.getChildren()[i].getName().getBaseName();
					Matcher matcher = pattern.matcher(filename);
					if (matcher.find()) {
						result.add(filename);
					}
				}
			}
			return result;
		} catch (JBranchException e1) {
			logger.warn("檔案不存在:{}", remoteFilePath);
			throw e1;
		} catch (Exception e) {
			logger.warn("listNames fail", e);
			throw new JBranchException("無法取得檔案清單[" + remoteFilePath + "]");
		} finally {
			manager.close();
		}
	}


	/**
	 * 上傳檔案至SFTP Server指定目錄下
	 *
	 * @param srcdirectory
	 *            來源路徑
	 * @param srcfilename
	 *            來源檔名
	 * @param desdirectory
	 *            目的路徑
	 * @param desfilename
	 *            目的檔名
	 * @return
	 * @throws JBranchException
	 */
	public void upload(String srcdirectory, String srcfilename, String desdirectory, String desfilename)
			throws JBranchException {

		File f = new File(CleanPath.cleanString(srcdirectory), CleanPath.cleanString(srcfilename));
		if (!f.exists())
			throw new JBranchException("來源檔案[" + srcdirectory + "/" + srcfilename + "]不存在!");

		StandardFileSystemManager manager = new StandardFileSystemManager();

		try {
			manager.init();

			// Create local file object
			FileObject localFile = manager.resolveFile(f.getAbsolutePath());
			String remoteFilePath = desdirectory + desfilename;
			if (!desdirectory.endsWith(File.separator))
				remoteFilePath = desdirectory + File.separator + desfilename;
			// Create remote file object
			FileObject remoteFile = manager.resolveFile(createConnectionString(remoteFilePath), createDefaultOptions());
			// Copy local file to sftp server
			remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);

		} catch (Exception e) {
			logger.warn("upload error", e);
			throw new JBranchException("SFTP上傳檔案[" + desfilename + "]失敗, 目錄[" + desdirectory + "]:" + e.getMessage());
		} finally {
			manager.close();
		}
	}

	/**
	 * 從SFTP Server指定目錄下載檔案到本機端指定路徑檔名
	 *
	 * @param srcdirectory
	 *            來源路徑
	 * @param srcfilename
	 *            來源檔名
	 * @param desdirectory
	 *            目的路徑
	 * @param desfilename
	 *            目的檔名
	 * @return
	 * @throws JBranchException
	 */
	public void download(String srcdirectory, String srcfilename, String desdirectory, String desfilename)
			throws JBranchException {
		StandardFileSystemManager manager = new StandardFileSystemManager();

		try {
			manager.init();

			String downloadFilePath = desdirectory + desfilename;
			if (!desdirectory.endsWith(File.separator))
				downloadFilePath = desdirectory + File.separator + desfilename;
			// Create local file object
			FileObject localFile = manager.resolveFile(downloadFilePath);

			String remoteFilePath = srcdirectory + srcfilename;
			if (!srcdirectory.endsWith(File.separator))
				remoteFilePath = srcdirectory + File.separator + srcfilename;
			// Create remote file object
			FileObject remoteFile = manager.resolveFile(createConnectionString(remoteFilePath), createDefaultOptions());

			// Copy local file to sftp server
			localFile.copyFrom(remoteFile, Selectors.SELECT_SELF);
			//boolean state = remoteFile.delete();
			//logger.info("delete {} {}", remoteFilePath, state ? "SUCC" : "FAIL");
		} catch (Exception e) {
			logger.warn("download error", e);
			throw new JBranchException("SFTP下載檔案[" + srcfilename + "]失敗, 來源[" + srcdirectory + "]:" + e.getMessage());
		} finally {
			manager.close();
		}
	}

	/**
	 * 從SFTP Server指定目錄下載檔案到本機端指定路徑檔名
	 *
	 * @param srcdirectory
	 *            來源路徑
	 * @param srcfilename
	 *            來源檔名
	 * @return
	 * @throws JBranchException
	 */
	public List<String> readFile(String srcdirectory, String srcfilename)
			throws JBranchException {
		List<String> contents = new ArrayList<String>();
		StandardFileSystemManager manager = new StandardFileSystemManager();
		InputStream putStream = null;
		BufferedReader reader = null;
		try {
			manager.init();

			String filePath = srcdirectory + srcfilename;
			if (!srcdirectory.endsWith(File.separator))
				filePath = srcdirectory + File.separator + srcfilename;
			// Create remote file object
			FileObject remoteFile = manager.resolveFile(createConnectionString(filePath), createDefaultOptions());

			// Copy local file to sftp server
			FileContent content = remoteFile.getContent();
			putStream = content.getInputStream();
			reader = new BufferedReader(new InputStreamReader(putStream, encoding));
			String line;
            while ((line = reader.readLine()) != null) {
            	System.out.println(line);
                contents.add(line);
            }
            reader.close();
            putStream.close();
			System.out.println("File read success");
		} catch (Exception e) {
			logger.warn("download error", e);
			throw new JBranchException("SFTP讀取檔案[" + srcfilename + "]失敗, 來源[" + srcdirectory + "]:" + e.getMessage());
		} finally {
			manager.close();
			IOUtils.closeQuietly(putStream);
			IOUtils.closeQuietly(reader);
		}
		return contents;
	}
	/**
	 * 刪除SFTP Server指定目錄下的檔案
	 *
	 * @param remoteFilePath
	 * @return
	 * @throws Exception
	 */	
	public void delete(String remoteFilePath) throws JBranchException {
	    StandardFileSystemManager manager = new StandardFileSystemManager();
	 
	    try {
	        manager.init();
	 
	        // Create remote object
	        FileObject remoteFile = manager.resolveFile(
	                createConnectionString(remoteFilePath), createDefaultOptions());
	 
	        if (remoteFile.exists()) {
	            remoteFile.delete();
	            System.out.println("Delete remote file success");
	        } else {
	        	throw new JBranchException("無此檔案[" + remoteFilePath + "]可刪除");
	        }
	    } catch (Exception e) {
	    	logger.warn("delete error", e);
			throw new JBranchException("FTP刪除檔案[" + remoteFilePath + "]失敗:" + e.getMessage());
	    } finally {
	        manager.close();
	    }
	}
	
	@Override
	public long exist(String srcdirectory, String srcfilename) throws JBranchException {
		StandardFileSystemManager manager = new StandardFileSystemManager();
        String remoteFilePath = srcdirectory + srcfilename;
		if (!srcdirectory.endsWith(File.separator))
			remoteFilePath = srcdirectory + File.separator + srcfilename;
		try {
	        manager.init();
	 
	        // Create remote object
	        FileObject remoteFile = manager.resolveFile(
	                createConnectionString(remoteFilePath), createDefaultOptions());
	        if (remoteFile.exists()) {
	        	return remoteFile.getContent().getSize();
	        }
	        return -1;
	    } catch (Exception e) {
	    	logger.warn("exist error", e);
			throw new JBranchException("FTP刪除檔案[" + remoteFilePath + "]失敗:" + e.getMessage());
	    } finally {
	        manager.close();
	    }
	}

	public static void main(String[] args) throws Exception {
		TBSYSREMOTEHOSTVO vo = new TBSYSREMOTEHOSTVO();
		vo.setIP("10.204.2.217");
		vo.setPORT(new BigDecimal(22));
		vo.setUSERNAME("sftptest");
		vo.setPASSWORD("b1fc2b4f796ae5a2");
		SftpUtil myftpUtil = new SftpUtil(vo);
		try {
			List<String> list = myftpUtil.listNames("Downloads");
			for (String str : list) {
				System.out.println(str);
			}
//			myftpUtil.delete("Downloads/4321.txt");
//			myftpUtil.upload("/Users/jemmy/Downloads", "1234.java", "Downloads", "jemmy.java");
//			myftpUtil.download("Downloads", "jemmy.java", "/Users/jemmy/Downloads", "5687.java");
			List<String> lines = myftpUtil.readFile("Downloads", "GetCase.java");
			for (String line : lines) System.out.println(line);
		} finally {
			// System.out.println(myftpUtil.getMsg());
		}
	}
}
