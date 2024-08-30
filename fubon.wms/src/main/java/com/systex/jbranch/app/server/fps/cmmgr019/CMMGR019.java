package com.systex.jbranch.app.server.fps.cmmgr019;

import com.systex.jbranch.fubon.bth.ftp.FtpUtilInf;
import com.systex.jbranch.fubon.bth.ftp.Sftp2Util;
import com.systex.jbranch.fubon.bth.ftp.SftpUtil;
import com.systex.jbranch.fubon.bth.ftp.SystexFtpUtil;
import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSREMOTEHOSTVO;
import com.systex.jbranch.platform.util.IPrimitiveMap;
// import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
// import net.lingala.zip4j.util.Zip4jConstants;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * FTP's UI Version
 *
 * @author Eli
 * @date 2019/03/05 初版
 * @date 2019/03/14 提供方法取得 File.seperator 供前端使用
 * @date 2019/03/20 1、刪除檢索檔案功能。
 *                  2、改變使用者角度，本地端變為電腦使用者。（原本地端是 AP_SERVER）
 *                  3、依改變使用者角度需求，更改刪除動作、上傳動作、下載動作等面向邏輯
 */
@Component("cmmgr019")
@Scope("request")
public class CMMGR019 extends FubonWmsBizLogic {
    public static final String ZIP = ".zip";

    /**
     * 使用 Thread Pool 去管理執行 FTP 檔案動作
     **/
    private static ThreadPoolTaskExecutor executor;

    /**
     * 任務狀態管理庫
     **/
    private static HashMap<String, HashMap<String, String>> queueMsgMap;

    /**
     * 待刪除檔案清單
     **/
    private static List<String> waitDelFile = new ArrayList();


    /**
     * 任務參數
     **/
    private static final String MISSION_SEQ = "SEQ";
    private static final String MISSION_STATUS = "STATUS";
    private static final String MISSION_MSG = "MSG";
    private static final String MISSION_FILE = "FILE";
    private static final String MISSION_TYPE = "TYPE";
    private static final String MISSION_END = "E";
    private static final String MISSION_DOWNLOAD_SUCCESS = "DS";
    private static final String MISSION_DOWNLOAD_FAIL = "DF";
    private static final String MISSION_DELETE = "DD";
    public static final String MISSION_UPDATE = "U";

    static {
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.initialize();

        queueMsgMap = new HashMap();
    }

    @Autowired
    private CMMGR019OutputVO outputVO;

    private CMMGR019InputVO inputVO;

    /**
     * [檔案修改時間: LAST_MODIFIED] 格式
     **/
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    /**
     * Ftp
     **/
    private FtpUtilInf ftp;

    /**
     * 設置 InputVO
     **/
    private void setInputVO(Object body) {
        inputVO = (CMMGR019InputVO) body;
    }

    /**
     * 列出 AP_SERVER 檔案列表
     **/
    public void listLocalFiles(Object body, IPrimitiveMap header) throws JBranchException {
        setInputVO(body);

        List fileInfoList = new ArrayList();
        File[] files = new File(inputVO.getUrl()).listFiles();
        if (null == files) throw new JBranchException("找不到此路徑！");
        for (File each : files)
            fileInfoList.add(setFileInfoMap(inputVO.getUrl(), each.getName(), each.getPath(), each.isDirectory() ? 0 : each.length(), each.isDirectory(), each.lastModified()));

        outputVO.setFileInfoList(fileInfoList);
        this.sendRtnObject(outputVO);
    }

    /**
     * 將檔案資訊包裝進 Map
     **/
    private Map setFileInfoMap(String parentPath, String name, String path, long size, boolean isDir, long lastModified) {
        Map eachInfo = new HashMap();
        eachInfo.put("PARENT_PATH", parentPath);
        eachInfo.put("FILE_NAME", name);
        eachInfo.put("FILE_PATH", path);
        eachInfo.put("FILE_SIZE", size);
        eachInfo.put("IS_DIR", isDir);
        eachInfo.put("LAST_MODIFIED", formatFileDate(lastModified));
        return eachInfo;
    }

    /**
     * 列出遠端檔案列表
     **/
    public void listRemoteFiles(Object body, IPrimitiveMap header) throws Exception {
        setInputVO(body);
        connectFtp();

        List fileInfoList = new ArrayList();
        if (isShaServer())
            for (String fileName : ftp.listNames(inputVO.getUrl())) {
                try {
                    fileInfoList.add(setFileInfoMap(inputVO.getUrl(), fileName, getFtpFilePath(fileName), 0, !fileName.contains("."), 0));
                } catch (Exception e) {
                    logger.info(e.getMessage()); // 如果檔案有任何錯誤，則略過
                }
            }
        else
            for (FTPFile each : ((SystexFtpUtil) ftp).listFiles(inputVO.getUrl())) {
                try {
                    fileInfoList.add(setFileInfoMap(inputVO.getUrl(), each.getName(), getFtpFilePath(each.getName()), each.getSize(), each.isDirectory(), each.getTimestamp().getTimeInMillis()));
                } catch (Exception e) {
                    // Apache FTP api 操作 listFiles，可能會將已刪除檔案（軌跡）一起抓進來，會造成 Null Pointer Exception。如果發生則避開。
                    logger.info(e.getMessage() + ", file: " + each);
                }
            }
        outputVO.setFileInfoList(fileInfoList);
        this.sendRtnObject(outputVO);
    }

    /**
     * 是否為SHA FTP SERVER
     **/
    private boolean isShaServer() {
        return !(ftp instanceof SystexFtpUtil);
    }

    /**
     * 取得 Ftp 檔案路徑
     **/
    private String getFtpFilePath(String fileName) {
        return Paths.get(inputVO.getUrl()).resolve(Paths.get(fileName)).toFile().getPath();
    }

    /**
     * 連線到指定主機
     **/
    private void connectFtp() throws JBranchException {
        Map<String, Object> hostInfo = queryHostInfo();
        switch (hostInfo.get("PROTOCOL").toString()) {
            case "SFTP":
                ftp = new SftpUtil(createHostVO(hostInfo));
                break;
            case "SFTP2":
                ftp = new Sftp2Util(createHostVO(hostInfo));
                break;
            default:
                ftp = new SystexFtpUtil(createHostVO(hostInfo));
        }
    }

    /**
     * 查詢 FTP 連線主機資訊
     **/
    private Map queryHostInfo() throws JBranchException {
        return (Map) exeQueryForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
                .append("select HOSTID, IP, PORT, USERNAME, PASSWORD, PROTOCOL ")
                .append("from TBSYSREMOTEHOST where HOSTID = :hostId ")
                .toString())
                .setObject("hostId", inputVO.getHostId()))
                .get(0);
    }

    /**
     * 查詢 FTP 連線主機清單
     **/
    public void listHost(Object body, IPrimitiveMap header) throws JBranchException {
        outputVO.setHostList(exeQueryForQcf(genDefaultQueryConditionIF()
                .setQueryString("select HOSTID from TBSYSREMOTEHOST ")));
        this.sendRtnObject(outputVO);
    }

    /**
     * 將 Map 結構轉變為 TableVO 結構
     **/
    private TBSYSREMOTEHOSTVO createHostVO(Map<String, Object> result) {
        TBSYSREMOTEHOSTVO hostVO = new TBSYSREMOTEHOSTVO();
        hostVO.setHOSTID(result.get("HOSTID").toString());
        hostVO.setIP(result.get("IP").toString());
        hostVO.setPORT(new BigDecimal(result.get("PORT").toString()));
        hostVO.setUSERNAME(result.get("USERNAME").toString());
        hostVO.setPASSWORD(result.get("PASSWORD").toString());
        return hostVO;
    }

    /**
     * 格式化檔案時間
     **/
    private String formatFileDate(long time) {
        return sdf.format(new Date(time));
    }

    /**
     * 上傳本地檔案 (檔案將上傳至 AP_SERVER temp 資料夾內，再將檔案移動到指定目錄)
     **/
    public void uploadLocalFile(Object body, IPrimitiveMap header) {
        setInputVO(body);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                HashMap missionMap = new HashMap();
                try {
                    Path source = Paths.get(AccessContext.tempPath).resolve(inputVO.getSrcFile());
                    if (inputVO.isLocal()) {
                        move(source, Paths.get(inputVO.getDesDir()).resolve(inputVO.getDesFile()));
                    } else {
                        connectFtp();
                        if (isShaServer()) throw new JBranchException("sha 主機目前不支援上傳服務！");

                        Path sourceTmp = Paths.get(AccessContext.tempPath).resolve(inputVO.getDesFile());
                        move(source, sourceTmp);

                        inputVO.setSrcDir(AccessContext.tempPath);
                        ((SystexFtpUtil) ftp).transfer(inputVO.getSrcDir(), inputVO.getDesFile(), inputVO.getDesDir());

                        if (Files.exists(sourceTmp))
                            Files.delete(sourceTmp);
                    }

                    missionMap.put(MISSION_MSG, "上傳成功");
                } catch (Exception e) {
                    missionMap.put(MISSION_MSG, e.toString());
                } finally {
                    setBasicInfo(missionMap, MISSION_UPDATE, inputVO.getSeq());
                }
            }
        });
        this.sendRtnObject(outputVO);
    }

    private void move(Path source, Path target) throws IOException {
        if (Files.exists(target))
            Files.delete(target);

        Files.move(source, target);
    }

    /**
     * 設置任務基本資訊
     **/
    private void setBasicInfo(HashMap missionMap, String type, String seq) {
        missionMap.put(MISSION_TYPE, type);
        missionMap.put(MISSION_SEQ, seq);
        missionMap.put(MISSION_STATUS, MISSION_END);
        queueMsgMap.put(seq, missionMap);
    }

    /**
     * 刪除檔案
     **/
    public void deleteFile(Object body, IPrimitiveMap header) {
        setInputVO(body);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                HashMap missionMap = new HashMap();
                try {
                    if (inputVO.isLocal()) deleteLocalFile(getSrcSourcePath());
                    else deleteRemoteFile();

                    missionMap.put(MISSION_MSG, "刪除成功");
                } catch (Exception e) {
                    missionMap.put(MISSION_MSG, e.toString());
                } finally {
                    setBasicInfo(missionMap, MISSION_DELETE, inputVO.getSeq());
                }
            }
        });
        this.sendRtnObject(outputVO);
    }

    /**
     * 透過 executor 執行下載遠端檔案操作，executor 執行期間將紀錄相關資訊於 queueMsgMap 中
     **/
    public void downloadRemoteFile(Object body, IPrimitiveMap header) {
        setInputVO(body);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                HashMap missionMap = new HashMap();
                try {
                    File srcFile;
                    if (inputVO.isLocal()) {
                        srcFile = getSrcSourcePath().toFile();
                    } else {
                        connectFtp();
                        if (isShaServer()) throw new JBranchException("sha 主機目前不支援下載服務！");
                        inputVO.setDesDir(AccessContext.tempPath);
                        ((SystexFtpUtil) ftp).download(inputVO.getSrcDir(), inputVO.getSrcFile(), inputVO.srcIsDir(), inputVO.getDesDir());
                        srcFile = new File(AccessContext.tempPath + inputVO.getSrcFile());
                    }

                    missionMap.put(MISSION_FILE, zip(srcFile, inputVO.srcIsDir()));
                    missionMap.put(MISSION_MSG, "下載成功");
                } catch (Exception e) {
                    missionMap.put(MISSION_MSG, e.toString());
                } finally {
                    setBasicInfo(missionMap, missionMap.get(MISSION_FILE) != null ? MISSION_DOWNLOAD_SUCCESS : MISSION_DOWNLOAD_FAIL, inputVO.getSeq());

                    // 遠端會先下載到 AP_SERVER TEMP 資料夾，壓縮完畢後即可刪除原始資料
                    if (!inputVO.isLocal()) {
                        try {
                            deleteLocalFile(Paths.get(AccessContext.tempPath).resolve(inputVO.getSrcFile()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        this.sendRtnObject(outputVO);
    }

    /**
     * 壓縮 srcFile
     **/
    private String zip(File srcFile, boolean isDir) throws ZipException {
        String uuidName = UUID.randomUUID().toString();
        ZipFile zipFile = new ZipFile(AccessContext.tempPath + uuidName);
        ZipParameters params = new ZipParameters();
        params.setCompressionMethod(CompressionMethod.DEFLATE); //  Zip4jConstants.COMP_DEFLATE);
        params.setCompressionLevel(CompressionLevel.ULTRA); //  Zip4jConstants.DEFLATE_LEVEL_ULTRA);

        if (isDir) zipFile.addFolder(srcFile, params);
        else zipFile.addFile(srcFile, params);
        return uuidName;
    }

    /**
     * 透過瀏覽器下載檔案
     **/
    private void notifyDownload(String uuidName, String seq) {
        notifyClientToDownloadFile("temp//" + uuidName, seq + ZIP);
    }


    /**
     * 刪除遠端檔案
     **/
    private void deleteRemoteFile() throws Exception {
        connectFtp();
        if (isShaServer()) throw new JBranchException("sha 主機目前不支援刪除服務！");
        ((SystexFtpUtil) ftp).remove(Paths.get(inputVO.getSrcDir()).resolve(Paths.get(inputVO.getSrcFile())).toString(), inputVO.srcIsDir());
    }

    /**
     * 刪除本地檔案
     **/
    private void deleteLocalFile(Path filePath) throws IOException {
        Files.walkFileTree(filePath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                if (e != null) throw e;

                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * 取得本地檔案完整路徑
     **/
    private Path getSrcSourcePath() {
        return Paths.get(inputVO.getSrcDir()).resolve(Paths.get(inputVO.getSrcFile()));
    }

    /**
     * 查看檔案佇列資訊，並回傳至前端
     * 下載動作 => 取得下載好的檔案名稱，排進瀏覽器下載任務佇列。該檔案將於下次方法被呼叫時，透過 waitDelFileExecute 方法刪除
     **/
    public void lookFtpInfo(Object body, IPrimitiveMap header) {
        setInputVO(body);
        waitDelFileExecute();

        List missionResult = new ArrayList();
        for (String seq : inputVO.getSeqList().split(";")) {
            HashMap eachMap = queueMsgMap.get(seq);
            if (eachMap == null) continue;
            missionResult.add(eachMap);

            if (queueMsgMap.get(seq).get(MISSION_TYPE).equals(MISSION_DOWNLOAD_SUCCESS)) {
                notifyDownload(queueMsgMap.get(seq).get(MISSION_FILE), seq);
                // 下載完後，將壓縮檔加入待刪除名單，待下次進入此方法時刪除
                waitDelFile.add(queueMsgMap.get(seq).get(MISSION_FILE));
            }

            if (queueMsgMap.get(seq).get(MISSION_STATUS).equals(MISSION_END))
                queueMsgMap.remove(seq);
        }
        outputVO.setMissionResult(missionResult);

        this.sendRtnObject(outputVO);
    }

    /**
     * 刪除待刪除清單裡的所有檔案
     **/
    private void waitDelFileExecute() {
        List<String> delFiles = new ArrayList();
        // 刪除檔案
        for (String fileName : waitDelFile) {
            try {
                Files.deleteIfExists(Paths.get(AccessContext.tempPath).resolve(fileName));
                delFiles.add(fileName);
            } catch (IOException e) {
            }
        }
        // 從名單中移除已刪除檔案
        for (String fileName : delFiles)
            waitDelFile.remove(fileName);
    }

    /**
     * 取得系統 File.seperator
     **/
    public void getFileSeperator(Object body, IPrimitiveMap header) {
        this.sendRtnObject(File.separator);
    }
}
