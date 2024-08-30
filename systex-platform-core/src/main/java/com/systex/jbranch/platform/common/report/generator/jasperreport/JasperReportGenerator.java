package com.systex.jbranch.platform.common.report.generator.jasperreport;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.generator.AbstractReportGenerator;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alex Lin
 * @version 2010/04/02 11:05:41 AM
 */
@SuppressWarnings("unchecked")
public class JasperReportGenerator extends AbstractReportGenerator {
// ------------------------------ FIELDS ------------------------------

    private static final String REPORT_EXT = "jasper";
    protected Map<String, String> charSetMap;
    protected String charSet;
    private JRExporter exporter;
	private Logger logger = LoggerFactory.getLogger(JasperReportGenerator.class);

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ReportGeneratorIF ---------------------

    public ReportIF generateReport(String txnCode, String reportID, ReportDataIF data) throws JBranchException {
        try {
            this.strTxnCode = txnCode;

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();


            ReportData reportData = (ReportData) data;

            Map parameters = new HashMap();
            parameters.putAll(reportData.getParameters());
            parameters.putAll(reportData.getRecordListAll());

            boolean merge = reportData.isMerge();

            // 取得AP Server當前執行路徑
            String fileName;
            if (reportData.getFileName() == null) {
                fileName = getSaveName(reportID);
            }
            else {
                fileName = reportData.getFileName() + "." + format.getExt();
            }

            String path;
            boolean pathflag = true;
            if (reportData.getPath() == null) {
                path = new File(serverPath, reportTemp).getAbsolutePath();
            }
            else {
                pathflag = false;
                path = reportData.getPath();
            }

            String pathAndFileName = new File(path, fileName).getAbsolutePath();

            String returnURL;
            if (pathflag) {
                returnURL = (reportTemp + fileName).substring(1);
            }
            else {
                returnURL = pathAndFileName;
            }

            // 確認目錄是否存在
            File thisFilePath = new File(path);
            FileUtils.forceMkdir(thisFilePath);

            // 取得report絕對路徑
            String readReportPath = getReportPath(txnCode, reportID);
            JRDataSource dataSource = reportData.getRecord().isEmpty() ? new JREmptyDataSource() : new JRMapCollectionDataSource(new ArrayList(reportData.getRecord()));
            JasperPrint jasperPrint = JasperFillManager.fillReport(readReportPath, parameters, dataSource);
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, pathAndFileName);
            exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, charSet);
            exporter.exportReport();

            stopWatch.stop();

            double speendingTime = stopWatch.getTime() * 0.001;// 報表產生經過時間

            // ===============設定Report相關回傳資訊===============
            String returnUrl = pathAndFileName.substring(pathAndFileName
                    .indexOf(returnURL));
            String currentTime = getCurrentTime("yyyyMMddhhmmss");

            return proccessReportIF(returnUrl, fileName, speendingTime,
                    currentTime, merge);
            // =================================================
        }
        catch (JRException e) {
            logger.error(e.getMessage(), e);
            throw new JBranchException(e);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new JBranchException(e);
        }
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    public void init() throws JBranchException {
        super.init();
        charSet = charSetMap.get(locale);
    }

    @Override
    public String getReportExt() {
        return REPORT_EXT;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setCharSetMap(Map charSetMap) {
        this.charSetMap = charSetMap;
    }

    public void setExporter(JRExporter exporter) {
        this.exporter = exporter;
    }
}
