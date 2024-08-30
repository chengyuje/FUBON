package com.systex.jbranch.fubon.bth;

import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 將 TBCRM_CUST_COMPLAIN_REPORT 的 REPORT 匯出
 * 移轉到「數位客訴平台系統」
 */
@Component("complainRptTrans")
@Scope("prototype")
public class BtcrmComplainReportTransfer extends BizLogic {
    private Logger logger = LoggerFactory.getLogger(BtcrmComplainReportTransfer.class);

    public void transfer(Object body, IPrimitiveMap<?> header) throws Exception {
        String defaultZipName = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "-complain-report.zip";
        String defaultLogName = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "-complain-report-bad.log";

        List<String> error = extractedZip(defaultZipName);
        log(error, defaultLogName);
        ftp((Map<String, Map>) body);
    }

    private static void ftp(Map<String, Map> body) throws Exception {
        Map<String, String> inputMap = body.get(SchedulerHelper.JOB_PARAMETER_KEY);
        if (StringUtils.isNotBlank(inputMap.get("ftpPutCode")))
            new BthFtpJobUtil().ftpPutFile(inputMap.get("ftpPutCode"));
    }

    private void log(List<String> error, String logName) throws IOException {
        File logFile = new File(AccessContext.tempPath, logName);
        try (BufferedWriter br = Files.newBufferedWriter(logFile.toPath(), StandardCharsets.UTF_8)) {
            for (String row : error) {
                br.write(row);
                br.newLine();
            }
        }
    }

    private List<String> extractedZip(String zipName) throws JBranchException, IOException {
        File zip = new File(AccessContext.tempPath, zipName);
        Files.deleteIfExists(zip.toPath());

        List<String> error = new LinkedList<>();

        try (ZipFile zipFile = new ZipFile(zip)) {
            for (Map<String, Object> each : getComplainReportData()) {
                Blob blob = (Blob) each.get("REPORT");
                String id = each.get("COMPLAIN_LIST_ID").toString();
                String name = StringUtils.defaultString((String) each.get("REPORT_NAME"));
                String targetName = id + "#" + name;

                try {
                    ZipParameters params = new ZipParameters();
                    params.setCompressionMethod(CompressionMethod.DEFLATE);
                    params.setCompressionLevel(CompressionLevel.ULTRA);
                    params.setFileNameInZip(targetName);

                    zipFile.addStream(blob.getBinaryStream(), params);

                } catch (Exception e) {
                    error.add("Report " + id + "：" + e);
                } finally {
                    try {
                        blob.free();
                    } catch (SQLException e) {
                        logger.error("Report " + id + " free：" + e);
                    }
                }
            }
        }
        return error;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getComplainReportData() throws JBranchException {
        return (List<Map<String, Object>>) Manager.manage(this.getDataAccessManager())
                .append("select COMPLAIN_LIST_ID, REPORT_NAME, REPORT from TBCRM_CUST_COMPLAIN_REPORT ")
                .append("where REPORT is not null ")
                .query();
    }
}
