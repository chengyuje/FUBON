package com.systex.jbranch.app.server.fps.ins970;

import com.systex.jbranch.fubon.bth.ftp.SystexFtpUtil;
import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSREMOTEHOSTVO;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * M+視訊錄影檔案瀏覽下載功能
 */
@Component("ins970")
@Scope("request")
public class INS970 extends FubonWmsBizLogic {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 使用 Thread Pool 去管理執行 FTP 檔案動作
     **/
    private static ThreadPoolTaskExecutor executor;

    /**
     * 任務狀態管理庫
     **/
    private static Map<String, Map<String, Object>> taskMap;

    /**
     * 檔案刪除管理庫
     */
    private static Map<String, Map<String, Object>> deleteMap;

    static {
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.initialize();

        taskMap = new ConcurrentHashMap<>();
        deleteMap = new ConcurrentHashMap<>();

        // 加入管理執行緒，負責管理任務
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        // 每分鐘檢查一次
                        Thread.sleep(60 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        System.out.println("檔案刪除管理庫:" + deleteMap);
                        if (deleteMap.isEmpty()) continue;

                        for (Map.Entry<String, Map<String, Object>> entry : deleteMap.entrySet()) {
                            Map<String, Object> map = entry.getValue();
                            try {
                                System.out.println("準備刪除檔案: " + map);
                                long endTime = (Long) map.get(MISSION_END_TIME);
                                long currentTime = System.currentTimeMillis();
                                long defaultTimeout = 1 * 60 * 1000; // 預設一分鐘刪除
                                if ((currentTime - endTime) > defaultTimeout) {
                                    Path file = Paths.get(AccessContext.tempPath, map.get(MISSION_FILE).toString());
                                    Files.deleteIfExists(file);
                                    deleteMap.remove(entry.getKey());
                                    System.out.println("刪除檔案成功 SEQ：" + entry.getKey() + ", map: " + map);
                                }
                            } catch (Exception e) {
                                // 發生錯誤時（如使用者正在下載中），將繼續保留在檔案刪除庫裡，待下次再次嘗試刪除
                                System.out.println("刪除檔案時發生錯誤： " + e.getMessage() + ", map: " + map);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("檔案刪除管理庫發生問題：" + e.getMessage() + "data: " + deleteMap);
                    }
                }
            }
        });
    }

    private static final String ZIP = ".zip";

    /**
     * 任務參數
     **/
    private static final String MISSION_START_TIME = "START";
    private static final String MISSION_END_TIME = "END";
    private static final String MISSION_SEQ = "SEQ";
    private static final String MISSION_STATUS = "STATUS";
    private static final String MISSION_MSG = "MSG";
    private static final String MISSION_FILE = "FILE";
    private static final String MISSION_TYPE = "TYPE";
    public static final String MISSION_FILE_SIZE = "SIZE";
    private static final String MISSION_END = "E";
    private static final String MISSION_DOWNLOAD_SUCCESS = "DS";
    private static final String MISSION_DOWNLOAD_FAIL = "DF";

    /**
     * 取得批次主機下 M+ 視訊錄影檔案詳細資訊列表
     * 以列在頁面上供選擇下載檔案
     **/
    public void listFiles(Object body, IPrimitiveMap header) throws JBranchException {
        INS970InputVO inputVO = (INS970InputVO) body;

        Map<String, String> params = getParams();
        logger.info("取得檔案詳細資訊列表 HOST: {}, Path: {} ",
                params != null ? params.get("HOST") : "本機", inputVO.getUrl());

        final SystexFtpUtil ftp = params != null ? getFtp(params.get("HOST")) : null;

        INS970OutputVO outputVO = new INS970OutputVO();

        if (ftp == null) { // 沒有設定 Ftp Server 資訊就指向本機
            // 本機根目錄為系統分隔符
            if (StringUtils.isBlank(inputVO.getUrl()))
                inputVO.setUrl(File.separator);

            outputVO.setFiles(listLocalFiles(inputVO.getUrl()));
        } else {
            // FTP Server 根目錄為空白
            if (File.separator.equals(inputVO.getUrl())) {
                inputVO.setUrl("");
            }

            outputVO.setFiles(listFtpFiles(inputVO.getUrl(), ftp));
        }
        outputVO.setUrl(inputVO.getUrl());

        this.sendRtnObject(outputVO);
    }

    private List<Map<String, Object>> listFtpFiles(String url, SystexFtpUtil ftp) throws JBranchException {
        List<Map<String, Object>> fileInfoList = new ArrayList<>();

        for (FTPFile each : ftp.listFiles(url)) {
            try {
                fileInfoList.add(
                        setFileInfoMap(url,
                                each.getName(),
                                Paths.get(url, each.getName()).toString(),
                                each.getSize(),
                                each.isDirectory(),
                                each.getTimestamp().getTimeInMillis()));
            } catch (Exception e) {
                // Apache FTP api 操作 listFiles，可能會將已刪除檔案（軌跡）一起抓進來，會造成 Null Pointer Exception。如果發生則避開。
                logger.info(e.getMessage() + ", file: " + each);
            }
        }
        return fileInfoList;
    }

    private List<Map<String, Object>> listLocalFiles(String url) throws JBranchException {
        List<Map<String, Object>> fileInfoList = new ArrayList<>();
        File[] files = new File(url).listFiles();
        if (null == files) throw new JBranchException("找不到此路徑！");
        for (File each : files)
            fileInfoList.add(
                    setFileInfoMap(url,
                            each.getName(),
                            each.getPath(),
                            each.isDirectory() ? 0 : each.length(),
                            each.isDirectory(),
                            each.lastModified()));
        return fileInfoList;
    }

    /**
     * 將檔案資訊包裝進 Map
     **/
    private Map<String, Object> setFileInfoMap(String parentPath, String name, String path, long size, boolean isDir, long lastModified) {
        Map<String, Object> eachInfo = new HashMap<>();
        eachInfo.put("PARENT_PATH", parentPath);
        eachInfo.put("FILE_NAME", name);
        eachInfo.put("FILE_PATH", path);
        eachInfo.put("FILE_SIZE", size);
        eachInfo.put("IS_DIR", isDir);
        eachInfo.put("LAST_MODIFIED", formatFileDate(lastModified));
        return eachInfo;
    }

    /**
     * 格式化檔案時間
     * [檔案修改時間: LAST_MODIFIED] 格式
     **/
    private String formatFileDate(long time) {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                .format(new Date(time));
    }

    /**
     * 將使用者選擇的檔案編入任務
     * 並依照隊列依序下載
     */
    public void download(Object body, IPrimitiveMap header) throws JBranchException {
        final INS970InputVO inputVO = (INS970InputVO) body;
        final Map<String, String> params = getParams();
        final SystexFtpUtil ftp = params != null ? getFtp(params.get("HOST")) : null;
        logger.info("將編入下載任務 SEQ: {}, HOST: {}, Path: {}, File: {} ",
                inputVO.getSeq(), params != null ? params.get("HOST") : "本機", inputVO.getFileParent(), inputVO.getFileName());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> missionMap = new HashMap<>();
                missionMap.put(MISSION_START_TIME, System.currentTimeMillis());

                Path sourceFile = Paths.get(inputVO.getFileParent(), inputVO.getFileName()); // 來源檔

                logger.info("開始執行下載任務 SEQ: {}, FULL_PATH: {}", inputVO.getSeq(), sourceFile);
                List<Path> temps = new ArrayList<>(); // 暫存檔將在最後被刪除
                try {
                    // 下載檔案到 TEMP 目錄
                    Path rawFile = Paths.get(AccessContext.tempPath, UUID.randomUUID().toString()); // 原始暫存檔
                    if (ftp == null) { // 沒有設定 Ftp Server 資訊就指向本機
                        try (InputStream inputStream = Files.newInputStream(sourceFile);
                             OutputStream outputStream = Files.newOutputStream(rawFile)) {

                            IOUtils.copy(inputStream, outputStream);
                            outputStream.flush();
                        }
                    } else {
                        ftp.download(inputVO.getFileParent(), inputVO.getFileName(),
                                rawFile.getParent().toString(), rawFile.getFileName().toString());
                    }
                    temps.add(rawFile);
                    logger.info("執行下載任務 SEQ: {}, FULL_PATH: {}, RAW: {}", inputVO.getSeq(), sourceFile, rawFile);

                    boolean isZip = inputVO.getFileName().endsWith(ZIP);
                    // 如果是 Zip 檔則進行解壓縮並建立一個臨時目錄來放置檔案
                    Path unZipFolder = null; // 解壓縮暫存目錄
                    if (isZip) {
                        unZipFolder = Files.createDirectories(Paths.get(AccessContext.tempPath, UUID.randomUUID().toString()));
                        temps.add(unZipFolder);
                        upZip(rawFile, unZipFolder, params.get("AES_KEY"));
                        logger.info("執行下載任務 SEQ: {}, FULL_PATH: {}, RAW: {}, UNZIP_FOLDER: {}", inputVO.getSeq(), sourceFile, rawFile, unZipFolder);
                    }

                    // 再進行一次壓縮
                    Path zipFile = Paths.get(AccessContext.tempPath, UUID.randomUUID().toString());
                    zip(isZip ? unZipFolder : rawFile, zipFile, inputVO.getFileName());
                    logger.info("執行下載任務 SEQ: {}, FULL_PATH: {}, RAW: {}, UNZIP_FOLDER: {}, ZIP: {}", inputVO.getSeq(), sourceFile, rawFile, unZipFolder, zipFile);

                    missionMap.put(MISSION_FILE, zipFile.getFileName().toString());
                    missionMap.put(MISSION_MSG, "下載成功");
                } catch (Exception e) {
                    missionMap.put(MISSION_MSG, e.getMessage());
                    logger.info("檔案 {} 發生錯誤：{}", sourceFile, ExceptionUtils.getFullStackTrace(e));
                } finally {
                    logger.info("下載任務完成 SEQ: {} = {}", inputVO.getSeq(), missionMap);

                    missionMap.put(MISSION_TYPE, missionMap.get(MISSION_FILE) != null ? MISSION_DOWNLOAD_SUCCESS : MISSION_DOWNLOAD_FAIL);
                    missionMap.put(MISSION_SEQ, inputVO.getSeq());
                    missionMap.put(MISSION_FILE_SIZE, inputVO.getFileSize());
                    missionMap.put(MISSION_STATUS, MISSION_END);
                    missionMap.put(MISSION_END_TIME, System.currentTimeMillis());
                    taskMap.put(inputVO.getSeq(), missionMap);

                    logger.info("刪除暫存檔 SEQ: {}, {}", inputVO.getSeq(), temps);
                    for (Path each : temps)
                        deleteByPath(each);
                }
            }
        });
        this.sendRtnObject(null);
    }

    public Map<String, String> getParams() throws JBranchException {
        List<Map<String, String>> data = exeQueryForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
                .append("select PARAM_CODE, PARAM_NAME from TBSYSPARAMETER ")
                .append("where PARAM_TYPE = 'INS.MPLUS_WEBM' ")
                .toString()));
        if (data.isEmpty()) return null;

        Map<String, String> params = new HashMap();
        for (Map<String, String> map : data) {
            params.put(map.get("PARAM_CODE"), map.get("PARAM_NAME"));
        }
        return params;
    }

    private SystexFtpUtil getFtp(String hostName) throws JBranchException {
        Map<String, Object> hostInfo = queryHostInfo(hostName);
        return hostInfo != null ? new SystexFtpUtil(createHostVO(hostInfo)) : null;
    }

    /**
     * 查詢 FTP 連線主機資訊
     **/
    private Map<String, Object> queryHostInfo(String host) throws JBranchException {
        List<Map<String, Object>> data = exeQueryForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
                .append("select HOSTID, IP, PORT, USERNAME, PASSWORD, PROTOCOL ")
                .append("from TBSYSREMOTEHOST where HOSTID = :hostId ")
                .toString())
                .setObject("hostId", host));
        if (data.isEmpty()) return null;
        return data.get(0);
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
     * 刪除指定 Path
     **/
    private void deleteByPath(Path filePath) {
        try {
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
        } catch (IOException e) {
            logger.info(ExceptionUtils.getFullStackTrace(e));
        }
    }

    /**
     * 壓縮
     */
    private void zip(Path source, Path target, String fileName) throws ZipException {
        ZipFile zipFile = new ZipFile(target.toString());

        ZipParameters params = new ZipParameters();
        params.setCompressionMethod(CompressionMethod.DEFLATE);
        params.setCompressionLevel(CompressionLevel.ULTRA);
        if (Files.isDirectory(source)) {
            for (File eachFile : source.toFile().listFiles()) {
                zipFile.addFile(eachFile, params);
            }
        } else {
            params.setFileNameInZip(fileName); // 將加入的 UUID 檔名變成原始檔名
            zipFile.addFile(source.toFile(), params);
        }
    }

    /**
     * 使用 AES KEY 密碼來進行解壓縮
     **/
    private void upZip(Path source, Path target, String password) throws ZipException {
        ZipFile zipFile = new ZipFile(source.toString(), password.toCharArray());
        zipFile.extractAll(target.toString());
    }

    /**
     * 取得系統 File.seperator
     **/
    public void getFileSeperator(Object body, IPrimitiveMap header) {
        this.sendRtnObject(File.separator);
    }

    /**
     * 查看下載任務佇列是否已有完成的下載，若有，則讓通知瀏覽器下載檔案
     **/
    public void peek(Object body, IPrimitiveMap header) {
        INS970InputVO inputVO = (INS970InputVO) body;
        List<Map<String, Object>> missionResult = new ArrayList<>();

        logger.info("待下載序號：{}", inputVO.getSeqList());
        if (CollectionUtils.isNotEmpty(inputVO.getSeqList())) {
            for (String seq : inputVO.getSeqList()) {
                Map<String, Object> eachMap = taskMap.get(seq);
                if (eachMap == null) continue;

                logger.info("下載任務：{}", eachMap);
                missionResult.add(eachMap);

                if (MISSION_END.equals(eachMap.get(MISSION_STATUS))) {
                    if (MISSION_DOWNLOAD_SUCCESS.equals(eachMap.get(MISSION_TYPE))) {
                        notifyDownload(eachMap.get(MISSION_FILE).toString(), seq);
                        // 將檔案加入刪除排程
                        deleteMap.put(seq, eachMap);
                    }
                    taskMap.remove(seq);
                }
            }
        }

        INS970OutputVO outputVO = new INS970OutputVO();
        outputVO.setMissionResult(missionResult);

        this.sendRtnObject(outputVO);
    }

    /**
     * 透過瀏覽器下載檔案
     **/
    private void notifyDownload(String uuidName, String seq) {
        notifyClientToDownloadFile("temp//" + uuidName, seq + ZIP);
    }
}
