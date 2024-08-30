package com.systex.jbranch.fubon.bth.ftp;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSREMOTEHOSTVO;
import com.systex.jbranch.platform.common.security.impl.JBranchCryptology;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jemmy Tsai on 2017/08/10
 *
 * FTP實作FtpUtilInf內容，不使用FtpUtil命名原因是公有套件也有相同Class
 * 
 * @modifier Eli 
 * @date 20171225
 * @spec 使平台支援任何檔名的傳輸服務 & Refactoring
 * 
 */
public class SystexFtpUtil implements FtpUtilInf {
	private TBSYSREMOTEHOSTVO hostVO;
	private Logger logger = LoggerFactory.getLogger(SystexFtpUtil.class);
	private StringBuffer auditMsg = new StringBuffer();

	public SystexFtpUtil(TBSYSREMOTEHOSTVO hostVO) throws JBranchException {
		if (hostVO == null) {
			throw new JBranchException("未設定連線資訊");
		}
		this.hostVO = hostVO;
	}

	// 連線FTP Server
	private void connect(FTPClient ftpClient) throws Exception {
		logger.info("IP {} ", this.hostVO.getIP());
		logger.info("PORT {} ", this.hostVO.getPORT());
		BigDecimal port = null;
		ftpClient.connect(this.hostVO.getIP(), this.hostVO.getPORT().intValue());
		auditMsg.append(ftpClient.getReplyString());
		ftpClient.enterLocalPassiveMode();
		auditMsg.append(ftpClient.getReplyString());
		ftpClient.setControlEncoding(encoding);
		auditMsg.append(ftpClient.getReplyString());
		String password = JBranchCryptology.decodePassword(this.hostVO.getPASSWORD());
		boolean state = ftpClient.login(this.hostVO.getUSERNAME(), password);
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE); 
		auditMsg.append(ftpClient.getReplyString());
		if (!state) {
			throw new JBranchException("登入帳號[" + this.hostVO.getUSERNAME() + "]失敗, " + ftpClient.getReplyString());
		}
		logger.info("FTP> {} login {} success!", this.hostVO.getUSERNAME(), this.hostVO.getIP());
	}

	// 切換Server的目錄。
	private void chdir(FTPClient ftpClient, String directory, boolean mkdirFlag) throws IOException, JBranchException {
		logger.info("FTP> chdir {}", directory);
		if (StringUtils.isNotBlank(directory)) {
			if (directory.indexOf("/") > -1) {
				if (directory.startsWith("/")) {
					boolean state = ftpClient.changeWorkingDirectory("/");
					logger.info("FTP> chdir / stats={}", state);
					auditMsg.append(ftpClient.getReplyString());

					if (!state) {
						throw new JBranchException("FTP切換目錄[/]失敗, " + ftpClient.getReplyString());
					}

					directory = directory.substring(1);
				}
				if (directory.endsWith("/")) {
					directory = directory.substring(0, directory.length() - 1);
				}
				String[] dirs = directory.split("/");

				for (int i = 0; i < dirs.length; i++) {
					if (dirs[i].trim().equals("")) {
						continue;
					}

					boolean state = ftpClient.changeWorkingDirectory(dirs[i]);
					auditMsg.append(ftpClient.getReplyString());
					logger.info("FTP> chdir {} stats={}", dirs[i], state);

					if (!state && mkdirFlag) { // 失敗的話,先嘗試建立這個資料夾,再做一次變更路徑
						state = ftpClient.makeDirectory(dirs[i]);
						String reply = ftpClient.getReplyString();
						auditMsg.append(reply);
						logger.info("FTP> mkdir {} stats={}", dirs[i], state);
						if (!state) {
							throw new JBranchException("FTP建立目錄失敗[" + dirs[i] + "], " + reply);
						}
						state = ftpClient.changeWorkingDirectory(dirs[i]);
						reply = ftpClient.getReplyString();
						auditMsg.append(reply);
						logger.info("FTP> re-chdir {} stats={}", dirs[i], state);

						if (!state) {
							throw new JBranchException("FTP切換目錄失敗[" + dirs[i] + "], " + reply);
						}
					}
				}
			} else {
				boolean state = ftpClient.changeWorkingDirectory(directory);
				String reply = ftpClient.getReplyString();
				logger.info("FTP> do chdir {} state={}", directory, state);
				auditMsg.append(reply);
				if (!state) {
					throw new JBranchException("切換FTP目錄失敗[" + directory + "], " + reply);
				}
			}
		}
	}

	/**
	 * 查詢FTP Server指定目錄下的檔案
	 *
	 * @param remoteFilePath
	 * @return
	 * @throws Exception
	 */
	public List<String> listNames(String remoteFilePath) throws JBranchException {
		return this.listNames(remoteFilePath, null);
	}

	/**
	 * 查詢FTP Server指定目錄下的檔案
	 *
	 * @param srcdirectory
	 *            遠端路徑
	 * @param pattern
	 *            比對表達式，可為null表示不比對
	 * @return List<String> 檔名清單
	 * @return
	 * @throws JBranchException
	 */
	public List<String> listNames(String srcdirectory, Pattern pattern) throws JBranchException {
		FTPClient ftpClient = new FTPClient();
		List<String> result = new ArrayList<String>();
		try {
			this.connect(ftpClient);
			this.chdir(ftpClient, srcdirectory, false);

			String[] filenames = ftpClient.listNames();
			if (null == filenames || filenames.length == 0) {
				return result;
			}
			int size = filenames.length;
			String reply = ftpClient.getReplyString();
			auditMsg.append(reply);
			if (pattern == null)
				for (int i = 0, n = size; i < n; i++)
					result.add(filenames[i]);
			else {
				for (int i = 0, n = size; i < n; i++) {
					Matcher matcher = pattern.matcher(filenames[i]);
					if (matcher.find()) {
						result.add(filenames[i]);
					}
				}
			}
			return result;
		} catch (JBranchException e1) {
			logger.warn("檔案不存在:{}", srcdirectory);
			throw e1;
		} catch (Exception e) {
			logger.warn("listNames fail", e);
			throw new JBranchException("連線失敗[" + this.hostVO.getHOSTID() + "] " + e.getMessage());
		} finally {
			if (isConnected(ftpClient)) diconnect(ftpClient);
		}
	}

	/**
	 * 上傳檔案至FTP Server指定目錄下
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
	 * @throws IOException 
	 */
	public void upload(String srcdirectory, String srcfilename, String desdirectory, String desfilename)
			throws JBranchException, IOException {
		File srcFile = new File(CleanPath.cleanString(srcdirectory), CleanPath.cleanString(srcfilename));
		if (!srcFile.exists()) throw new JBranchException("來源檔案[" + srcdirectory + "/" + srcfilename + "]不存在!");
		
		FTPClient ftpClient = new FTPClient();
		InputStream inStream = null;
		
		try {
			this.connect(ftpClient);
			this.chdir(ftpClient, desdirectory, true);
			
			inStream = FileUtils.openInputStream(srcFile);

			if (srcFile.length() > 50 * 1024 * 1024) {
				ftpClient.setBufferSize(32768);
				ftpClient.setDefaultTimeout(5 * 60 * 1000);
				logger.info("Increase buffer & timeout to upload {} size = {}", srcFile.getName(), srcFile.length());
			}
			boolean state = ftpClient.storeFile(this.transformEncode(desfilename), inStream);
			String reply = ftpClient.getReplyString();
			auditMsg.append(reply);
			inStream.close();

			if (!state) {
				throw new JBranchException(
						"FTP上傳檔案[" + desfilename + "]失敗, 目錄[" + ftpClient.printWorkingDirectory() + "], " + reply);
			}

		} catch (JBranchException e1) {
			logger.warn("upload err", e1);
			throw e1;
		} catch (Exception e) {
			logger.warn("upload fail", e);
			throw new JBranchException("連線失敗[" + this.hostVO.getHOSTID() + "] " + e.getMessage());
		} finally {
			if (isConnected(ftpClient)) diconnect(ftpClient);
			IOUtils.closeQuietly(inStream);
		}
	}

	public void CopyStream(InputStream is, OutputStream os) throws Exception {
		final int buffer_size = 1024;
		byte[] bytes = new byte[buffer_size];
		for (;;) {
			int count = is.read(bytes, 0, buffer_size);
			if (count == -1)
				break;
			os.write(bytes, 0, count);
		}
	}

	/**
	 * 從FTP Server指定目錄下載檔案到本機端指定路徑檔名
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
		File desPath = new File(desdirectory);
		if (!desPath.exists()) throw new JBranchException("目的路徑[" + desdirectory + "]不存在!");

		FTPClient ftpClient = new FTPClient();
		OutputStream outStream = null;
		try {
			this.connect(ftpClient);
			ftpClient.changeWorkingDirectory(srcdirectory);
			outStream = new BufferedOutputStream(new FileOutputStream(
					new File(CleanPath.cleanString(desdirectory), CleanPath.cleanString(desfilename))));
			boolean state = ftpClient.retrieveFile(this.transformEncode(srcfilename), outStream);
			String reply = ftpClient.getReplyString();
			auditMsg.append(reply);
			outStream.close();
			if (!state) {
				throw new JBranchException(
						"FTP取得檔案[" + srcfilename + "]失敗, 目錄[ " + ftpClient.printWorkingDirectory() + "]," + reply);
			}
		} catch (JBranchException e1) {
			logger.info("download err", e1);
			throw e1;
		} catch (Exception e) {
			logger.warn("download fail", e);
			throw new JBranchException("連線失敗[" + this.hostVO.getHOSTID() + "] " + e.getMessage());
		} finally {
			if (isConnected(ftpClient)) diconnect(ftpClient);
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
	public List<String> readFile(String srcdirectory, String srcfilename) throws JBranchException {
		List<String> contents = new ArrayList<String>();
		FTPClient ftpClient = new FTPClient();
		InputStream putStream = null;
		BufferedReader reader = null;
		
		try {
			this.connect(ftpClient);
			this.chdir(ftpClient, srcdirectory, false);
			
			putStream = ftpClient.retrieveFileStream(this.transformEncode(srcfilename));
			reader = new BufferedReader(new InputStreamReader(putStream, encoding));

			String line;
			while ((line = reader.readLine()) != null) {
				contents.add(line);
			}
			reader.close();
			putStream.close();
		} catch (JBranchException e1) {
			logger.warn("download err", e1);
			throw e1;
		} catch (Exception e) {
			logger.warn("download fail", e);
			throw new JBranchException("連線失敗[" + this.hostVO.getHOSTID() + "] " + e.getMessage());
		} finally {
			if (isConnected(ftpClient)) diconnect(ftpClient);
			IOUtils.closeQuietly(putStream);
			IOUtils.closeQuietly(reader);
		}
		return contents;
	}

	/**斷開FTP*/
	private void diconnect(FTPClient ftpClient) {
		try {
			ftpClient.disconnect();
		} catch (Exception e) {
			// Nothing
		}
	}

	/**FTP是否連線*/
	private boolean isConnected(FTPClient ftpClient) {
		return ftpClient.isConnected();
	}

	/**
	 * 刪除FTP Server指定目錄下的檔案
	 *
	 * @param remoteFilePath
	 * @return
	 * @throws Exception
	 */
	public void delete(String remoteFilePath) throws JBranchException {
		FTPClient ftpClient = new FTPClient();
		try {
			this.connect(ftpClient);
			boolean state = ftpClient.deleteFile(this.transformEncode(remoteFilePath));
			String reply = ftpClient.getReplyString();
			auditMsg.append(reply);

			if (!state) {
				throw new JBranchException("FTP刪除檔案失敗[" + remoteFilePath + "], " + reply);
			}
		} catch (JBranchException e1) {
			logger.warn("delete err", e1);
			throw e1;
		} catch (Exception e) {
			logger.warn("download fail", e);
			throw new JBranchException("連線失敗[" + this.hostVO.getHOSTID() + "] " + e.getMessage());
		} finally {
			if (isConnected(ftpClient)) diconnect(ftpClient);
		}
	}

	@Override
	public long exist(String srcdirectory, String srcfilename) throws JBranchException {
		FTPClient ftpClient = new FTPClient();
		try {
			this.connect(ftpClient);
			
			// mantis 5363: 處理SFTP批次失敗問題
			// 根據 FTPClient API 文件取得指定路徑下所有 file
			this.chdir(ftpClient, srcdirectory, false);
			FTPFile[] ftpFiles = ftpClient.listFiles();
			
			// 取得檔名符合的，並取得檔案大小
			long size = -1;
			for(FTPFile ftpFile : ftpFiles) {
				String ftpFileName = ftpFile.getName();
				if(ftpFileName.equals(this.transformEncode(srcfilename))) {
					size = ftpFile.getSize();
					break;
				}
			}
			
			return size;
		} catch (JBranchException e1) {
			logger.warn("delete err", e1);
			throw e1;
		} catch (Exception e) {
			logger.warn("download fail", e);
			throw new JBranchException("連線失敗[" + this.hostVO.getHOSTID() + "] " + e.getMessage());
		} finally {
			if (isConnected(ftpClient)) diconnect(ftpClient);
		}
	}

	/**將傳輸檔名編碼轉換成平台預設的編碼*/
	private String transformEncode(String beforeStr) throws UnsupportedEncodingException {
		return new String(beforeStr.getBytes(encoding), ftpEncoding);
	}

	/** 顯示檔案列表 **/
	public FTPFile[] listFiles(String remoteFilePath) throws JBranchException {
		FTPClient ftpClient = new FTPClient();
		try {
			this.connect(ftpClient);
			this.chdir(ftpClient, transformEncode(remoteFilePath), false);
			return ftpClient.listFiles();
		} catch (JBranchException e1) {
			logger.warn("目錄不存在:{}", remoteFilePath);
			throw e1;
		} catch (Exception e) {
			logger.warn("listFiles fail", e);
			throw new JBranchException("連線失敗[" + this.hostVO.getHOSTID() + "] " + e.getMessage());
		} finally {
			if (isConnected(ftpClient)) diconnect(ftpClient);
		}
	}

	/**
	 * 刪除 FTP Server 檔案或資料夾
	 */
	public void remove(String remotePath, boolean remotePathIsDir) throws Exception {
        FTPClient ftpClient = new FTPClient();
        try {
            connect(ftpClient);
            remove(ftpClient, remotePath, remotePathIsDir);
        } finally {
            if (isConnected(ftpClient)) diconnect(ftpClient);
        }
	}

	/** 刪除 FTP 目標核心邏輯 **/
	private void remove (FTPClient ftpClient, String remotePath, boolean remotePathIsDir) throws Exception {
	    if (remotePathIsDir) {
            for (FTPFile file: listFiles(remotePath))
                remove(ftpClient, resolve(remotePath, file.getName()), file.isDirectory());
            if(!ftpClient.removeDirectory(transformEncode(remotePath)))
                throw new JBranchException("FTP刪除檔案失敗[" + remotePath + "], " + ftpClient.getReplyString());
        } else {
            if (!ftpClient.deleteFile(transformEncode(remotePath)))
                throw new JBranchException("FTP刪除檔案失敗[" + remotePath + "], " + ftpClient.getReplyString());
        }
    }

    /** 結合兩路徑 **/
    private String resolve(String aPath, String bPath) {
        return Paths.get(aPath).resolve(bPath).toString();
    }

    /** 將檔案或資料夾上傳到 FTP  **/
    public void transfer(String srcDir, String srcFile, String desDir) throws Exception {
        FTPClient ftpClient = new FTPClient();
        try {
            connect(ftpClient);
            transfer(ftpClient, srcDir, srcFile, desDir);
        } finally {
            if (isConnected(ftpClient)) diconnect(ftpClient);
        }
    }

    /** 將檔案或資料夾上傳到 FTP 核心邏輯 **/
    private void transfer(FTPClient ftpClient, String srcDir, String srcFile, String desDir) throws IOException, JBranchException {
        ftpClient.changeWorkingDirectory(transformEncode(desDir));

        Path srcPath  = Paths.get(srcDir).resolve(srcFile);
        if (Files.isDirectory(srcPath)) {
            if (!ftpClient.makeDirectory(transformEncode(srcFile)))
                throw new JBranchException("上傳資料夾到 FTP 失敗[" + srcPath.toString() + "], " + ftpClient.getReplyString());

            for (File file: srcPath.toFile().listFiles())
                transfer(ftpClient, srcPath.toString(), file.getName(), resolve(desDir, srcFile));

        } else {
            try (InputStream stream = Files.newInputStream(srcPath)) {
                if(!ftpClient.storeFile(transformEncode(srcFile), stream))
                    throw new JBranchException("上傳檔案到 FTP 失敗[" + srcPath.toString() + "], " + ftpClient.getReplyString());
            }
        }
    }

	/**
	 * 下載 FTP Server 檔案或資料夾
	 */
	public void download(String remoteDir, String remoteFile, boolean remotePathIsDir, String desPath) throws Exception {
		FTPClient ftpClient = new FTPClient();
		try {
			connect(ftpClient);
			download(ftpClient, remoteDir, remoteFile, remotePathIsDir, desPath);
		} finally {
			if (isConnected(ftpClient)) diconnect(ftpClient);
		}
	}

	/** 下載 FTP Server 檔案或資料夾核心邏輯 **/
	private void download (FTPClient ftpClient, String remoteDir, String remoteFile, boolean remotePathIsDir, String desPath) throws Exception {
		ftpClient.changeWorkingDirectory(transformEncode(remoteDir));

		if (remotePathIsDir) {
			String desSubDir = resolve(desPath, remoteFile);
            Files.createDirectory(Paths.get(desSubDir));

			String remoteSubDir = resolve(remoteDir, remoteFile);
			for (FTPFile file: listFiles(remoteSubDir))
				download(ftpClient, remoteSubDir, file.getName(), file.isDirectory(), desSubDir);
		}
		else {
            try (OutputStream stream = Files.newOutputStream(Paths.get(resolve(desPath, remoteFile)))) {
                if (!ftpClient.retrieveFile(transformEncode(remoteFile), stream))
                    throw new JBranchException("FTP 下載檔案失敗[" + resolve(remoteDir, remoteFile) + "], " + ftpClient.getReplyString());
            }
		}
	}

	/**Very Poor Unit Test，ENV:UT*/
	public static void main(String[] args) throws Exception {
		/**Arrange*/
		TBSYSREMOTEHOSTVO vo = new TBSYSREMOTEHOSTVO();
		vo.setIP("10.204.2.216");
		vo.setPORT(new BigDecimal(21));
		vo.setUSERNAME("user");
		vo.setPASSWORD("78dd503c4eeb5556");
		SystexFtpUtil myftpUtil = new SystexFtpUtil(vo);
		
		/**Act*/
		/**測試 readFile 檔名在 ISO-8859-1 規範(內or外)的兩種情形 */
		
//		try { //內
//			myftpUtil.readFile("test1", "A_GetInsProduct1_20180507.zip");
//			show("readFitFile");
//		} catch (Exception e) {
//			showErr("readFitFile"); 
//		}

//		
//		try { //外
//			myftpUtil.readFile("test1", "企金VIP客戶名單.csv");	
//			show("readNonFitFile");
//		} catch (Exception e) {
//			showErr("readNonFitFile");
//		}
//		
//		/**測試 upload 檔名在 ISO-8859-1 規範(內or外)的兩種情形 */
//		
//		try { //內
//			myftpUtil.upload("D:\\", "Eli.csv", "test", "EliTest.csv");	
//			show("uploadFitFile");
//		} catch (Exception e) {
//			showErr("uploadFitFile");
//		}
//		
//		try { //外
//			myftpUtil.upload("D:\\", "Eli中文.csv", "test", "Eli中文.csv");	
//			show("uploadNonFitFile");
//		} catch (Exception e) {
//			showErr("uploadNonFitFile");
//		}
//
//		/**測試 download 檔名在 ISO-8859-1 規範(內or外)的兩種情形 */
//		try { //內
//			myftpUtil.download("test1/A0000583/INS", "A_GetInsProduct1_20180507.zip", "C:\\Users\\1800020\\Desktop\\", "A_GetInsProduct1.zip");	
//			show("downloadFitFile");
//		} catch (Exception e) {
//			showErr("downloadFitFile");
//		}
//		
//		try { //外
//			myftpUtil.download("test", "上上上.csv", "D:\\", "下下下.csv");	
//			show("downloadNonFitFile");
//		} catch (Exception e) {
//			showErr("downloadNonFitFile");
//		}
		

		/**FTP指定目錄下的檔案測試*/
//		List list = myftpUtil.listNames("test");
//		System.out.println(list.size()); 
//		
//		
//		/**測試 chectExist 檔名在 ISO-8859-1 規範(內or外)的兩種情形 */
//		try { //內
//			myftpUtil.exist("test1/A0000583/INS", "A_GetInsProduct1_20180507.zip");	
//			show("existFitFile");
//		} catch (Exception e) {
//			showErr("existFitFile");
//		}
//		
//		try { //外
//			myftpUtil.exist("test", "上上上.csv");	
//			show("existNonFitFile");
//		} catch (Exception e) {
//			showErr("existNonFitFile");
//		}
		
		/**測試 delete 檔名在 ISO-8859-1 規範(內or外)的兩種情形 */
//		try { //內
//			myftpUtil.delete("test\\1234.csv");	
//			show("deleteFitFile");
//		} catch (Exception e) {
//			showErr("deleteFitFile");
//		}
//		
//		try { //外
//			myftpUtil.delete("test\\上上上.csv");	
//			show("deleteNonFitFile");
//		} catch (Exception e) {
//			showErr("deleteNonFitFile");
//		}
		
		
//		try {
////    		logger.info("download from {} / {} to {} / {}", this.srcdirectory, srcfile, this.desdirectory, desfile);
//			myftpUtil.download("test1/A0000583/INS", "A_GetInsProduct1_20180507.zip", "C:\\Users\\1800020\\Desktop\\", "A_GetInsProduct1.zip");
//    		
//			long srcSize = myftpUtil.exist("test1/A0000583/INS", "A_GetInsProduct1_20180507.zip");
//			File desFile = new File("C:\\Users\\1800020\\Desktop\\", "A_GetInsProduct1.zip");
//			long desSize = desFile.length();
//			if (desFile.exists() ) {
//				if (srcSize == desSize || (srcSize == -1 && desSize > 0)) {	// 來源檔為-1表示被刪，也允許刪除。
//					if (false) { // "Y".equals(this.tbsysftpvo.getSRCDELETE())) {
//	    				String remotePath = "test1/A0000583/INS" + "A_GetInsProduct1_20180507.zip";
//	        			if (!"test1/A0000583/INS".endsWith(File.separator))
//	        				remotePath = "test1/A0000583/INS" + File.separator + "A_GetInsProduct1_20180507.zip";
//	        			myftpUtil.delete(remotePath);
//					}
//				} else {
//					throw new JBranchException("[A_GetInsProduct1_20180507.zip] size no match! local= " + desSize + ", remote=" + srcSize);
//				}
//			} else {
//	    		throw new JBranchException("test1/A0000583/INS" + " / A_GetInsProduct1_20180507.zip no exists!");
//			}
//    		
//    	} catch (Exception e) {
//    		throw e;
//    	}
		
	}

	/**UT使用*/
	private static void show(String method) {
		System.out.printf("%s OK%n", method);
	}
	
	/**UT使用*/
	private static void showErr(String method) {
		System.out.printf("%s Fail%n", method);
	}
}
