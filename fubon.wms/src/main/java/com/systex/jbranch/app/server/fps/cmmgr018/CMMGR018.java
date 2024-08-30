package com.systex.jbranch.app.server.fps.cmmgr018;

import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;
// import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
// import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * Log 查詢
 *
 * @author eli
 * @date 20181015
 * @date 20181130 不同的主機所查詢的LOG路徑不同
 * @date 20181204 依照建立時間降冪排序Log檔案，下載完成後刪除temp folder下的壓縮檔
 */
@Component("cmmgr018")
@Scope("request")
public class CMMGR018 extends BizLogic {

    private final String CURRENT_LOG_NAME = "SystemOut.log";
    private final String ZIP = ".zip";
    private CMMGR018InputVO inputVO;
    @Autowired
    private CMMGR018OutputVO outputVO;

    /**
     * 設置 InputVO
     **/
    private void setInputVO(Object body) {
        this.inputVO = (CMMGR018InputVO) body;
    }

    /**
     * 查詢指定路徑下的LOG檔案列表
     *
     * @param body
     * @param header
     */
    public void inquireLogs(Object body, IPrimitiveMap header) throws Exception {
        setInputVO(body);

        File logDir = new File(getLogDirPath());
        if (logDir.exists() && logDir.isDirectory()) {
            List resultList = new ArrayList();
            HashMap eachMap = new HashMap();

            for (File eachFile : sortFileByCreation(logDir.listFiles())) {
                if (eachFile.isDirectory()) continue;
                if (isCurrentLog(eachFile.getName())) continue;

                eachMap.put("PARENT_PATH", eachFile.getParent());
                eachMap.put("LOG_NAME", eachFile.getName());
                resultList.add(eachMap.clone());
                eachMap.clear();
            }
            outputVO.setResultList(resultList);
        } else {
            throw new APException("請確認欲查詢的Log資料夾路徑是否存在 !");
        }
        this.sendRtnObject(outputVO);
    }

    /**
     * 使用檔案建立時間來排序
     **/
    private File[] sortFileByCreation(File[] files) {
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                try {
                    return -compareCreationTime(o1.toPath(), o2.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        return files;
    }

    /**
     * 比較檔案建立時間
     **/
    private int compareCreationTime(Path p1, Path p2) throws IOException {
        return Files.readAttributes(p1, BasicFileAttributes.class)
                .creationTime()
                .compareTo(
                        Files.readAttributes(p2, BasicFileAttributes.class).creationTime());
    }

    /**
     * 取得該主機下指定的LOG目錄
     **/
    private String getLogDirPath() throws UnknownHostException, JBranchException {
        Map param = new HashMap();
        param.put("hostName", InetAddress.getLocalHost().getHostName());
        List<Map<String, String>> result = exeQueryForMap(
                "select PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = 'LOG_PATH' and PARAM_CODE = :hostName", param);
        if (CollectionUtils.isNotEmpty(result))
            return result.get(0).get("PARAM_NAME");
        else
            throw new APException("不被允許的主機名稱，請洽系統管理員!");
    }

    /**
     * 查詢時，排除正在寫的Log檔案
     **/
    private boolean isCurrentLog(String eachName) {
        return eachName.equals(CURRENT_LOG_NAME);
    }


    /**
     * 下載指定的Log檔案，此Log檔案須經過壓縮
     *
     * @param body
     * @param header
     * @throws ZipException
     */
    public void downloadLog(Object body, IPrimitiveMap header) throws ZipException {
        setInputVO(body);
        String logZipName = UUID.randomUUID().toString();
        zipped(AccessContext.tempPath + logZipName);
        notifyClientToDownloadFile("temp//" + logZipName, inputVO.getLogName() + ZIP);
        this.sendRtnObject(logZipName);
    }

    /**
     * 壓縮Log檔案
     **/
    private void zipped(String logZipName) throws ZipException {
        ZipFile zipFile = new ZipFile(logZipName);
        ZipParameters params = new ZipParameters();
        params.setCompressionMethod(CompressionMethod.DEFLATE); //  Zip4jConstants.COMP_DEFLATE);
        params.setCompressionLevel(CompressionLevel.ULTRA); //  Zip4jConstants.DEFLATE_LEVEL_ULTRA);
        zipFile.addFile(Paths.get(inputVO.getLogDirPath()).resolve(Paths.get(inputVO.getLogName())).toFile(), params);
    }

    /**
     * 下載壓縮檔後，刪除在temp資料夾裡的壓縮檔
     **/
    public void deleteZip(Object body, IPrimitiveMap header) throws IOException, InterruptedException {
        setInputVO(body);
        Thread.sleep(100); // 暫停100ms，等待下載確定執行再刪除壓縮檔
        Files.deleteIfExists(Paths.get(AccessContext.tempPath + inputVO.getZipPath()));
    }
}
