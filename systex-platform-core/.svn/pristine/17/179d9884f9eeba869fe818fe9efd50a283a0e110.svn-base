package com.systex.jbranch.platform.common.report.generator;

import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;
import com.systex.jbranch.platform.common.dataManager.*;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.FlexReport;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFormat;
import com.systex.jbranch.platform.common.report.reportdata.ConfigAdapterIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.DateUtil;
import com.systex.jbranch.platform.common.util.ThreadDataPool;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @author Alex Lin
 * @version 2010/11/09 2:59 PM
 */
public abstract class AbstractReportGenerator implements ReportGeneratorIF {
// ------------------------------ FIELDS ------------------------------

    protected ReportFormat format;
    protected UUID uuidNew;
    protected String strTxnCode;
    protected String serverPath;
    protected ConfigAdapterIF config;
    protected String strRoot;
    protected String strTransaction;
    protected String reportTemp;
    protected String locale;

    protected String defaultReportId;
    protected String outputReportDir = "Report";
    protected String encryptReport;
    protected DataManagerIF dataManager;
	private Logger logger = LoggerFactory.getLogger(AbstractReportGenerator.class);
    private static AtomicLong atomicLong = new AtomicLong();

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ReportGeneratorIF ---------------------

    public ReportIF generateReport(String reportID, ReportDataIF data) throws JBranchException {
        return generateReport(strTxnCode, reportID, data);
    }

    public ReportIF generateReport(String reportID) throws JBranchException {
        ReportDataIF data = new ReportData();
        return generateReport(reportID, data);
    }

    public ReportIF generateReport(String txnCode, String reportID) throws JBranchException {
        return generateReport(txnCode, reportID, new ReportData());
    }

    public ReportIF generateReport() throws JBranchException {
        return generateReport(defaultReportId);
    }

    public ReportIF generateReport(ReportDataIF data) throws JBranchException {
        return generateReport(defaultReportId, data);
    }

    public ReportIF mergeFiles(ReportIF[] files) throws JBranchException {
        FlexReport report;
        String pdfName;
        for (ReportIF file : files) {
            report = (FlexReport) file;
            if (!report.isMerge()) {
                throw new JBranchException("pf_report_common_008", Arrays.asList(report.getReportName()));
            }
        }

        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            String mergeFileName = getSaveName("merge");
            String pathAndFileName = serverPath + reportTemp
                    + mergeFileName;
            String returnURL = new File(reportTemp, mergeFileName).getAbsolutePath().substring(1);
            String encryptFileName = strRoot + encryptReport
                    + mergeFileName;
            PdfCopyFields mergePDF = new PdfCopyFields(
                    new FileOutputStream(pathAndFileName));
            // =========merge PDF start=========
            for (ReportIF file : files) {
                report = (FlexReport) file;
                pdfName = report.getReportName();
                mergePDF.addDocument(new PdfReader(serverPath + reportTemp
                        + pdfName));
            }
            mergePDF.close();

            // 刪除已合併過的PDF
            for (ReportIF file : files) {
                report = (FlexReport) file;
                pdfName = report.getReportName();

                File delFile = new File(serverPath + reportTemp + pdfName);
                delFile.delete();
            }

            // =========merge PDF end=========
            // 加密備份檔案
            // encryptReport(pathAndFileName,encryptFileName);

            stopWatch.stop();
            double speendingTime = stopWatch.getTime() * 0.001;// 報表產生經過時間
            // (
            // sec
            // )

            // ===============設定Report相關回傳資訊===============
            String returnUrl = pathAndFileName.substring(pathAndFileName
                    .indexOf(returnURL));

            String currentTime = getCurrentTime("yyyyMMddhhmmss");

            return proccessReportIF(returnUrl, mergeFileName,
                    speendingTime, currentTime, true);
            // =================================================
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new JBranchException("pf_report_common_004");
        }
    }

// -------------------------- OTHER METHODS --------------------------

    public void init() throws JBranchException {
        uuidNew = (UUID) ThreadDataPool.getData(ThreadDataPool.KEY_UUID);

        if (uuidNew != null) {
            strTxnCode = dataManager.getTransactionDefinition(uuidNew).getTxnCode();
        }
        else {
            uuidNew = new UUID();
            Branch branchId = new Branch();
            branchId.setBrchID("0000");
            WorkStation ws = new WorkStation();
            ws.setWsID("127.0.0.1" + "00000");
            Section section = new Section();
            section.setSectionID("0000");
            ClientTransaction ct = new ClientTransaction();
            branchId.setWorkStation("127.0.0.1" + "00000", ws);
            ws.setSection("0000", section);
            section.setClientTransaction(ct);
        }
        strRoot = config.getRoot();
        strTransaction = config.getTransaction();
        serverPath = config.getServerHome();
        reportTemp = config.getReportTemp();
        locale = (String) ObjectUtils.defaultIfNull(uuidNew.getLocale(), (String) dataManager.getSystem().getDefaultValue().get("language"));
        encryptReport = config.getEncryptReport();
		// 確認目錄是否存在
		File thisFilePath = new File(serverPath + reportTemp);
		if (!thisFilePath.exists()) {
			thisFilePath.mkdirs();
		}
    }

    protected String getReportPath(String txnCode, String reportID)
            throws JBranchException {
        try {
//        	logger.debug("txnCode="+txnCode);
//        	logger.debug("reportID="+reportID);
//        	logger.debug("dataManager.getTransactionDefinition(txnCode)="+dataManager.getTransactionDefinition(txnCode));
            StringBuilder strReportFile = new StringBuilder();
            strReportFile
                    .append(strRoot)
                    .append("/")
                    .append(strTransaction)
                    .append(dataManager.getTransactionDefinition(txnCode).getSysType().trim())
                    .append("/")
                    .append(txnCode)
                    .append("/")
                    .append(outputReportDir)
                    .append("/")
                    .append(txnCode)
                    .append("_")
                    .append(reportID);
            if(locale != null && !"".equals(locale)){
            	strReportFile.append("_")
                .append(locale);
            }
                    
            strReportFile.append(".")
                    .append(getReportExt());
            return strReportFile.toString();
        } catch(JBranchException e){
        	throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new JBranchException("pf_report_common_006");
        }
    }

    public abstract String getReportExt();

    /**
     * 產生PDF檔名
     *
     * @param reportID 報表id
     * @return PDF檔名
     * @since 2009/05/07
     */
    protected String getSaveName(String reportID) {
        StringBuilder saveName = new StringBuilder();
        // naming rule:交易代號_報表代號_uuid_時間(到微秒).xxx
        String time = getCurrentTime("yyyyMMddhhmmssSSS");
        long seq = atomicLong.getAndIncrement();
        if (reportID.equalsIgnoreCase("merge")
                && format.equals(ReportFormat.PDF)) {
            saveName.append(reportID).append("_").append(uuidNew.toString())
                    .append("_").append(time).append("_").append(seq).append(".").append(format.getExt());
        }else {
            saveName.append(strTxnCode).append("_").append(
                    uuidNew.toString()).append("_").append(time).append("_").append(seq).append(".")
                    .append(format.getExt());
        }
        return saveName.toString();
    }

    /**
     * 取得目前時間
     *
     * @param format 自訂時間格式
     * @return time 目前時間
     * @since 2009/05/07
     */
    protected String getCurrentTime(String format) {
        
    	return DateUtil.format(new Date(), format);
    }

    /**
     * 儲存ReportIF資訊
     *
     * @param returnURL     存放PDF的URL
     * @param fileName      PDF檔名
     * @param spendingTime  處理時間
     * @param EstablishTime
     * @param merge         是否合併
     * @param returnURL     存放PDF的URL
     * @return ReportIF 產出PDF的相關資訊
     */
    protected ReportIF proccessReportIF(String returnURL, String fileName,
                                        double spendingTime, String EstablishTime, boolean merge) {
        FlexReport report = new FlexReport();
        report.setLocation(returnURL);
        report.setReportName(fileName);
        report.setSpeendingTime(spendingTime);
        report.setEstablishTime(EstablishTime);
        report.setMerge(merge);

        return report;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setConfig(ConfigAdapterIF config) {
        this.config = config;
    }

    public void setDataManager(DataManagerIF dataManager) {
        this.dataManager = dataManager;
    }

    public void setDefaultReportId(String defaultReportId) {
        this.defaultReportId = defaultReportId;
    }

    public void setFormat(ReportFormat format) {
        this.format = format;
    }

    public void setOutputReportDir(String outputReportDir) {
        this.outputReportDir = outputReportDir;
    }
}
