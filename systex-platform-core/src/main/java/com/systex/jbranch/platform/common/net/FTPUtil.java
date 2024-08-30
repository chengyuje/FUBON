package com.systex.jbranch.platform.common.net;

import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataexchange.FileUtil;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSREMOTEHOSTVO;
import com.systex.jbranch.platform.common.scheduler.AuditLogUtil;
import com.systex.jbranch.platform.common.security.util.CryptoUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.common.workday.utils.WorkDayUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

public class FTPUtil {
// ------------------------------ FIELDS ------------------------------

    public static String LOG_TYPE = "ftp";

    final private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    private int retryCount = 3;    //重試次數
    private int retryInterval = 1;    //單位分
    private FTPClient ftpClient;
    private String ip;
    private int port = 21;
    private String username;
    private String password;
    private AuditLogUtil audit;
    private String currentDirectory;
    private WorkDayUtils workDayUtils;
	private Logger logger = LoggerFactory.getLogger(FTPUtil.class);

// --------------------------- CONSTRUCTORS ---------------------------

    public FTPUtil() {

    }

    public FTPUtil(String hostId) throws Exception {
        setHostId(hostId);
        connect();
    }

    public void setHostId(String hostId) throws DAOException, JBranchException, Exception {
        DataAccessManager dam_obj = new DataAccessManager();
        TBSYSREMOTEHOSTVO remotehost = (TBSYSREMOTEHOSTVO) dam_obj.findByPKey(TBSYSREMOTEHOSTVO.TABLE_UID, hostId);
        if (remotehost == null) {
            throw new JBranchException("無法取得遠端主機資訊，[" + hostId + "]不存在");
        }
        String passWrod = new String(CryptoUtil.getInstance().
                symmetricDecrypt(StringUtil.fromHex(remotehost.getPASSWORD())));
        this.ip = remotehost.getIP();
        this.port = remotehost.getPORT().intValue();
        this.username = remotehost.getUSERNAME();
        this.password = passWrod;
    }

    public FTPUtil(String ip, int port, String username, String password) throws Exception {
        this(ip, port, username, password, null);
    }

    public FTPUtil(String ip, int port, String username, String password, AuditLogUtil audit) throws Exception {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
        this.audit = audit;
        connect();
    }

// -------------------------- OTHER METHODS --------------------------

    public String ftpGetFile(String srcdirectory, String srcfilename,
                             String checkfile, String desdirectory, String desfilename, String repeat, String repeatinterval) throws Exception {
        desdirectory = replaceRootPath(desdirectory);

        //取得正確的檔案名稱，檔名規則有固定檔名或是以日期為規則的檔名兩種
        srcfilename = getRealFileName(srcfilename);

        //切換目錄
        if (srcdirectory != null && srcdirectory.trim().length() > 0) {
            this.changeWorkingDirectory(srcdirectory);
        }

        //確認遠端檢查檔是否已經準備好)
        if (checkfile != null && !"".equals(checkfile.trim()) && repeat != null && repeatinterval != null) {
            waitForCheckSumFile(checkfile, repeat, repeatinterval);
        }
        String[] files = this.listNames();
        if (haveFile(files, srcfilename)) {
            //抓取檔案
            String file = getRemoteFile(desdirectory, desfilename, srcfilename, ftpClient);
            if (checkfile != null && !"".equals(checkfile.trim())) {
                this.deleteFile(checkfile);
            }

            return file;
        }
        else {
            throw new JBranchException("pf_ftp_common_004");
        }
    }

    private void waitForCheckSumFile(String checkfile, String repeat, String repeatinterval) throws Exception {
        String[] files = this.listNames();
        int repeatCount = 0;
        while (!isReady(checkfile, files)) {
            if (repeat != null && Integer.parseInt(repeat) > 0) {
                repeatCount++;
                if (repeatCount > Integer.parseInt(repeat)) {
                    throw new JBranchException("pf_ftp_common_005");
                }
            }
            if (repeatinterval != null && Integer.parseInt(repeatinterval) > 0) {
                String currentDir = ftpClient.printWorkingDirectory();
                disConnection();
                if (logger.isInfoEnabled()) {
                    logger.info("等待檢核檔[" + checkfile + "] 第[" + repeatCount + "/" + repeat + "]次重試, 等待[" + repeatinterval + "]分鐘。");
                }
                Thread.sleep(Integer.parseInt(repeatinterval) * 60 * 1000);
                connect();
                ftpClient.changeWorkingDirectory(currentDir);
            }
            else {
                throw new JBranchException("pf_ftp_common_003");
            }
            files = this.listNames();
        }
    }

    /**
     * 檢查有沒有checksum檔案，來判斷檔案是否傳完了
     *
     * @param checkFile
     * @param files
     * @return
     */
    private boolean isReady(String checkFile, String[] files) {
        //先判斷有沒有設定check file
        if (checkFile == null || checkFile.trim().length() == 0) {
            return true;
        }

        for (int i = 0; i < files.length; i++) {
            if (files[i].equals(checkFile)) {
                return true;
            }
        }
        return false;
    }

    private String getRemoteFile(String outputdirectory, String outputfilename, String filename, FTPClient ftpClient) throws Exception {
        if (outputfilename == null || outputfilename.trim().length() == 0) {
            outputfilename = filename;
        }
        if (outputdirectory != null && outputdirectory.trim().length() > 0) {
            File file = new File(outputdirectory);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        String gotFile = outputdirectory + System.getProperty("file.separator") + outputfilename;
        OutputStream outStream = new BufferedOutputStream(new FileOutputStream(gotFile));
        try {
            this.retrieveFile(filename, outStream);
            outStream.flush();
        }
        finally {
            if (outStream != null) {
                outStream.close();
            }
        }

        FileUtil.openReadCommand(gotFile);
        return gotFile;
    }

    private void retrieveFile(String filename, OutputStream outStream) throws Exception {
        this.doAction(Type.RETRIEVE_FILE, filename, outStream);
    }

    public void deleteFile(String fileName) throws Exception {
        this.doAction(Type.DELETE_FILE, fileName);
    }

    public String ftpPutFile(String srcdirectory, String srcfilename,
                             String checkfile, String desdirectory, String desfilename) throws Exception {
        srcdirectory = replaceRootPath(srcdirectory);
        //取得正確的檔案名稱，檔名規則有固定檔名或是以日期為規則的檔名兩種
        srcfilename = getRealFileName(srcfilename);

        //切換目錄
        if (desdirectory != null && desdirectory.trim().length() > 0) {
            this.changeAndCreateWorkingDirectory(desdirectory);
        }
        String[] files = null;
        File file = new File(srcdirectory);
        if (file.exists()) {
            files = file.list();
        }
        else {
            throw new JBranchException("pf_ftp_common_003");
        }
        if (haveFile(files, srcfilename)) {
            //放檔案
            return putRemoteFile(srcdirectory, srcfilename, desdirectory, desfilename);
        }
        else {
            throw new JBranchException("pf_ftp_common_004");
        }
    }

    public String replaceRootPath(String desdirectory) throws JBranchException {
        char fileSeparator = System.getProperty("file.separator").charAt(0);

        if (desdirectory.trim().startsWith("%JBRANCH_ROOT%")) {
            desdirectory = desdirectory.replaceFirst("%JBRANCH_ROOT%", "");
            String rootPath = DataManager.getRoot();
            if (rootPath.endsWith("\\") || rootPath.endsWith("/")) {
                rootPath = rootPath.substring(0, rootPath.length() - 1);
            }
            desdirectory = rootPath + desdirectory;
        }
        else if (desdirectory.trim().startsWith("%APP_ROOT%")) {
            desdirectory = desdirectory.replaceFirst("%APP_ROOT%", "");
            String appPath = null;
            try {
                appPath = DataManager.getRealPath();
                if (appPath.endsWith("\\") || appPath.endsWith("/")) {
                    appPath = appPath.substring(0, appPath.length() - 1);
                }
            }
            catch (Exception e) {
                throw new JBranchException("pf_ftp_common_006");
            }
            desdirectory = appPath + desdirectory;
        }
        desdirectory.replace('\\', fileSeparator);
        return desdirectory;
    }

    private void changeAndCreateWorkingDirectory(String directory) throws Exception {
        int index = directory.indexOf("/");
        if (index > -1) {
            if (directory.startsWith("/")) {
                boolean state = ftpClient.changeWorkingDirectory("/");
                if (!state) {
                    throw new JBranchException("FTP切換目錄[/]失敗, return=" + state);
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

                if (!state) { // 失敗的話,先嘗試建立這個資料夾,再做一次變更路徑
                    this.makeDirectory(dirs[i]);
                    this.changeWorkingDirectory(dirs[i]);
                }
                currentDirectory = ftpClient.printWorkingDirectory();
            }
        }
        else {
            this.doChangeWorkingDirectory(directory);
            currentDirectory = ftpClient.printWorkingDirectory();
        }
    }

    public void makeDirectory(String dir) throws Exception {
        this.doAction(Type.MARK_DIRECTORY, dir);
    }

    private String putRemoteFile(String inputdirectory, String inputfilename, String outputdirectory, String outputfilename) throws Exception {
        if (outputfilename == null || outputfilename.trim().length() == 0) {
            outputfilename = inputfilename;
        }
        if (outputdirectory != null && outputdirectory.trim().length() > 0) {
            File file = new File(outputdirectory);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        String gotFile = inputdirectory + System.getProperty("file.separator") + inputfilename;
        InputStream inStream = new BufferedInputStream(new FileInputStream(gotFile));

        try {
            this.storeFile(outputfilename, inStream);
        }
        finally {
            if (inStream != null) {
                inStream.close();
            }
        }
        return outputfilename;
    }

    private void storeFile(String outputFileName, InputStream inStream) throws Exception {
        this.doAction(Type.STORE_FILE, outputFileName, inStream);
    }

    public boolean remoteHaveFile(String path, String fileName) throws Exception {
        String oldDir = ftpClient.printWorkingDirectory();

        this.changeWorkingDirectory(path);
        String[] files = this.listNames();
        this.changeWorkingDirectory(oldDir);
        return haveFile(files, fileName);
    }

    public String[] listNames() throws Exception {
        return (String[]) this.doAction(Type.LIST_NAMES, null);
    }

    /**
     * 判斷遠端是否有指定的檔案
     *
     * @param remotefiles
     * @param filename
     * @return
     */
    private boolean haveFile(String[] remotefiles, String filename) {
        for (int i = 0; i < remotefiles.length; i++) {
            if (remotefiles[i].equals(filename)) {
                return true;
            }
        }
        return false;
    }

    public void setAuditLog(AuditLogUtil audit) {
        this.audit = audit;
    }

    //@Test
    public void testCase() {

    }

    private Object doAction(Type type, Object... arg) throws Exception {
        int i = 0;
        while (true) {
            try {
                switch (type) {
                    case CONNECT:
                        doConnect();
                        break;
                    case LIST_NAMES:
                        return doListNames();
                    case DELETE_FILE:
                        doDeleteFile((String) arg[0]);
                        break;
                    case CHANGE_WORKING_DIRECTORY:
                        doChangeWorkingDirectory((String) arg[0]);
                        break;
                    case MARK_DIRECTORY:
                        doMakeDirectory((String) arg[0]);
                        break;
                    case RETRIEVE_FILE:
                        doRetrieveFile((String) arg[0], (OutputStream) arg[1]);
                        break;
                    case STORE_FILE:
                        doStoreFile((String) arg[0], (InputStream) arg[1]);
                        break;
                }
                break;
            }
            catch (IOException e) {
                if (i >= retryCount) {
                    logger.error(e.getMessage(), e);
                    throw e;
                }
                logger.warn("FTP連線中斷[" + e.getMessage() + "], 第[" + (i + 1) + "/" + retryCount + "]次重試, 等待[" + retryInterval + "]分鐘。");
            }
            catch (JBranchException e) {
                if (i >= retryCount) {
                    logger.error(e.getMessage(), e);
                    throw e;
                }
                logger.warn(e.getMessage() + ", 第[" + (i + 1) + "/" + retryCount + "]次重試, 等待[" + retryInterval + "]分鐘。");
            }

            try {
                Thread.sleep(retryInterval * 60000L);
            }
            catch (InterruptedException e) {
                //ignore
            }
            if (type == Type.CONNECT) {
                disConnection();
            }
            else {
                reConnect();
            }
            i++;
        }
        return null;
    }

    private void doConnect() throws Exception {
        ftpClient = new FTPClient();
        ftpClient.connect(this.ip, this.port);
        boolean state = ftpClient.login(this.username, this.password);
        if (!state) {
            throw new JBranchException("FTP登入帳號[" + this.username + "]失敗, retrun=" + state);
        }
    }

    private String[] doListNames() throws Exception {
        String[] list = ftpClient.listNames();
        if (list == null) {
            throw new JBranchException("無法取得檔案清單[" + ftpClient.printWorkingDirectory() + "]");
        }
        return list;
    }

    private void doDeleteFile(String fileName) throws Exception {
        boolean state = ftpClient.deleteFile(getRealFileName(fileName));
        if (!state) {
            throw new JBranchException("FTP刪除檔案失敗[" + getRealFileName(fileName) + "], return=" + state);
        }
    }

    /**
     * 取得正確的檔名
     * (請參考WorkDayUtils.formatDateString說明)
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public String getRealFileName(String fileName) throws Exception {
        return workDayUtils.formatDateString(fileName);
    }

    private void doChangeWorkingDirectory(String directory) throws Exception {
        boolean state = ftpClient.changeWorkingDirectory(directory);
        if (!state) {
            throw new JBranchException("FTP切換目錄失敗[" + directory + "], return=" + state);
        }
        currentDirectory = ftpClient.printWorkingDirectory();
    }

    private void doMakeDirectory(String dir) throws Exception {
        boolean state = ftpClient.makeDirectory(dir);
        if (!state) {
            throw new JBranchException("FTP建立目錄失敗[" + dir + "], return=" + state);
        }
    }

    private void doRetrieveFile(String filename, OutputStream outStream) throws Exception {
        boolean state = ftpClient.retrieveFile(filename, outStream);
        if (!state) {
            throw new JBranchException("FTP取得檔案[" + filename + "]失敗, 目錄[ " + ftpClient.printWorkingDirectory() + "]");
        }
    }

    private void doStoreFile(String filename, InputStream inStream) throws Exception {
        boolean state = ftpClient.storeFile(filename, inStream);
        if (!state) {
            throw new JBranchException("FTP上傳檔案[" + filename + "]失敗, 目錄[" + ftpClient.printWorkingDirectory() + "]");
        }
    }

    public void reConnect() throws Exception {
        disConnection();
        connect();
        this.changeWorkingDirectory(currentDirectory);
    }

    public void disConnection() {
        if (ftpClient != null) {
            try {
                ftpClient.disconnect();
            }
            catch (IOException e) {
            }
        }
    }

    public void connect() throws Exception {
        this.doAction(Type.CONNECT, null);
    }

    public void changeWorkingDirectory(String directory) throws Exception {
        this.doAction(Type.CHANGE_WORKING_DIRECTORY, directory);
    }

// -------------------------- ENUMERATIONS --------------------------

    private enum Type {
        CONNECT, LIST_NAMES, DELETE_FILE, CHANGE_WORKING_DIRECTORY, MARK_DIRECTORY, RETRIEVE_FILE, STORE_FILE
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public void setRetryInterval(int retryInterval) {
        this.retryInterval = retryInterval;
    }

    public void setWorkDayUtils(WorkDayUtils workDayUtils) {
        this.workDayUtils = workDayUtils;
    }

    /*
     private String decryptReport(String password){
         String tmpStr=null;
         byte[] fileArray=null;
         LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "bess password="+password);
         try {
             fileArray = StringUtil.fromHex(password);
         } catch (DecoderException e1) {
             String msg = StringUtil.getStackTraceAsString(e);
              LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_ERROR, msg);
         }

         try {
             fileArray = CryptoUtil.getInstance().symmetricDecrypt(fileArray);
             tmpStr=new String(fileArray);
         } catch (JBranchException e) {
             String msg = StringUtil.getStackTraceAsString(e);
              LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_ERROR, msg);
         }

         LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "bess 280 passwrod="+tmpStr);
         return tmpStr;
     }
     */
}