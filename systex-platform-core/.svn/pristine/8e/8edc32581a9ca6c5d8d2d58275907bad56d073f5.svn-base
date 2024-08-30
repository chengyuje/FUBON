package com.systex.jbranch.platform.common.report.generator.birt;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.model.api.CellHandle;
import org.eclipse.birt.report.model.api.ElementFactory;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.ImageHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.StructureFactory;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.eclipse.birt.report.model.api.elements.structures.EmbeddedImage;
import org.eclipse.birt.report.model.elements.interfaces.IReportItemModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataManager.PlatformVOHelper;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.generator.AbstractReportGenerator;
import com.systex.jbranch.platform.common.report.generator.DataSetCalc;
import com.systex.jbranch.platform.common.report.generator.ReportGuard;
import com.systex.jbranch.platform.common.report.generator.jfreechart.Chart;
import com.systex.jbranch.platform.common.report.generator.jfreechart.ChartModel;
import com.systex.jbranch.platform.common.report.generator.jfreechart.Pie;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.ImageUtil;
import com.systex.jbranch.platform.common.util.StringUtil;

public class BirtReportGenerator extends AbstractReportGenerator {
// ------------------------------ FIELDS ------------------------------

    private static final String REPORT_EXT = "rptdesign";
    private IReportRunnable design;
    private IRenderOption renderOption;

    private Map<String, ChartModel> chartMap = new HashMap<String, ChartModel>();
    private IReportEngine engine;
	private Logger logger = LoggerFactory.getLogger(BirtReportGenerator.class);

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ReportGeneratorIF ---------------------

    /**
     * 組報表產生PDF
     *
     * @param txnCode    交易代碼
     * @param filename   報表檔名
     * @param parameters 參數
     * @param data       多筆報表資料
     * @return ReportIF 產出PDF的相關資訊
     * @throws JBranchException
     * @author Richard
     * @since 2009/06/02
     */
    public ReportIF generateReport(String txnCode, String reportID,
                                   ReportDataIF data) throws JBranchException {
        boolean merge;
        try {
            long speendingTimeStart = System.currentTimeMillis();// 計算報表產生經過時間起始

            // 20091026
            strTxnCode = txnCode;

            ReportData reportData = (ReportData) data;

            Map parameters = reportData.getParameters();
            List list = reportData.getRecord();
            Map records = reportData.getRecordListAll();
            Object otherParams = reportData.getOthers();

            merge = reportData.isMerge();

            // 取得AP Server當前執行路徑
            String fileName = null;
            if (reportData.getFileName() == null) {
                fileName = getSaveName(reportID);
            }
            else {
                fileName = reportData.getFileName() + "." + format.getExt();
            }

            String path = null;
            boolean pathflag = true;
            if (reportData.getPath() == null) {
                path = new File(serverPath, reportTemp).getAbsolutePath();
            }
            else {
                pathflag = false;
                path = reportData.getPath();
            }

            String pathAndFileName = new File(path, fileName).getAbsolutePath();

            String returnURL = null;
            if (pathflag) {
                returnURL = reportTemp.substring(1) + fileName;
            }
            else {
                returnURL = pathAndFileName;
            }

            // 確認目錄是否存在
            File thisFilePath = new File(path);
            FileUtils.forceMkdir(thisFilePath);

            // 取得report絕對路徑
            String readReportPath = getReportPath(txnCode, reportID);

            pdfGenerator(txnCode, readReportPath, parameters, records, pathAndFileName, path, otherParams);


//            String encryptFileName = strRoot + encryptReport + fileName;


            // 將產生完的報表實際寫入PDF
//            FileUtils.writeByteArrayToFile(new File(pathAndFileName), bos.toByteArray());
            long speendingTimeEnd = System.currentTimeMillis();// 計算報表產生經過時間始束
            double speendingTime = (speendingTimeEnd - speendingTimeStart) * 0.001;// 報表產生經過時間
            // (
            // sec
            // )

            // if(merge == false ){
            // //加密備份檔案
            // encryptReport(pathAndFileName,encryptFileName);
            //
            // }

            // ===============設定Report相關回傳資訊===============
            String currentTime = getCurrentTime("yyyyMMddhhmmss");
            return proccessReportIF(returnURL, fileName, speendingTime,
                    currentTime, merge);
            // =================================================
        }
        catch (Exception e) {
            throw new JBranchException(e.getMessage(), e);
        }
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * 該方法已棄用
     * 改用BirtOtherParams類別中setChartModel(ChartModel cm)方法
     */
    @Deprecated
    public void setChartModel(String key, ChartModel chartModel) {
        chartMap.put(key, chartModel);
    }

    @Override
    public String getReportExt() {
        return REPORT_EXT;
    }

    private void encryptReport(String pathAndFileName, String encryptFileName) {
        ReportGuard encryptReport = new ReportGuard();
        encryptReport.encryptReport(pathAndFileName, encryptFileName);
    }

    private void pdfGenerator(String txnCode, String reportPath, Map parameters, Map<String, List> records, String output, String path, Object otherParams)
            throws JBranchException {
        try {
            design = engine.openReportDesign(reportPath);

            BirtOtherParams birtOtherParams = null;
            if(otherParams != null && otherParams instanceof BirtOtherParams){
            	birtOtherParams = (BirtOtherParams) otherParams;
            	birtOtherParams.process(design);
            }
            
            // 取得Report tittle並存入datamanager記錄ej用
            if (logger.isDebugEnabled()) {
                logger.debug("Report Name = " + design.getProperty(IReportRunnable.TITLE));
            }

            String tittle;
            if (design.getProperty(IReportRunnable.TITLE) == null) {
                tittle = "";
            }
            else {
                tittle = (String) design.getProperty(IReportRunnable.TITLE);
            }
            try {
                dataManager.getServerTransaction(uuidNew).getPlatFormVO()
                        .setVar(PlatformVOHelper._EJMEMO, tittle);
            }
            catch (Exception e) {
            }


            List<String> chartTempPath = new ArrayList<String>();
            
            if (this.getChartModel(txnCode) != null || birtOtherParams != null) {
                ChartModel cm = getChartModel(txnCode);
                if(birtOtherParams != null){
                	cm = birtOtherParams.getChartModel();
                }
                if (cm.getCharts() == null) {
                    pieGenerator(txnCode, records, chartTempPath, path);
                }
                else {
                    //新作法，包含Pie、Bar、Line、Bubble
                    generatorChart(cm, chartTempPath, path);
                }
            }

            // Create task to run and render the report
            IRunAndRenderTask task = engine.createRunAndRenderTask(design);

            if (records != null) {
                Map map = null;
                HashMap<String, List> contextMap = new HashMap<String, List>();
                for (String key : records.keySet()) {
                    List value = records.get(key);
                    contextMap.put(key, value);
                }
                task.setAppContext(contextMap);
            }

            replaceSpace(parameters);
            task.setParameterValues(parameters);

            task.validateParameters();

            renderOption.setOutputFileName(output);
            renderOption.setOutputFormat(format.getType());
            task.setRenderOption(renderOption);

            task.run();

            clearTempChart(chartTempPath);

            task.close();
        }
        catch (Exception e) {
            throw new JBranchException(e.getMessage(), e);
        }
        catch (Error er) {
            logger.error(er.getMessage(), er);
            throw new JBranchException(StringUtil.getStackTraceAsString(er));
        }
    }

    private void pieGenerator(String txnCode, Map<String, List> records, List<String> piePath, String path) {
        ChartModel cm = this.getChartModel(txnCode);
        if (cm == null) {
            return;
        }
        int[] piesId = cm.getPiesId();
        String[] sliceSizes = cm.getSliceSizes();
        String[] cates = cm.getCates();
        String[] titles = cm.getTitles();

        ReportDesignHandle report = (ReportDesignHandle) design.getDesignHandle();
        ElementFactory df = report.getElementFactory();

        for (int i = 0; i < piesId.length; i++) {
            if (records != null) {
                try {
                    ExtendedItemHandle pie1 = (ExtendedItemHandle) report.getElementByID(piesId[i]);

                    String width = pie1.getWidth().getStringValue();
                    String height = pie1.getHeight().getStringValue();
                    String dataSetName = (String) pie1.getProperty(IReportItemModel.DATA_SET_PROP);

                    List<Map<String, Object>> list = records.get(dataSetName);
                    if (list == null) {
                        logger.warn("dataset=" + dataSetName + " is null");
                        continue;
                    }
                    String pieFilename = createPie(list, cates[i], sliceSizes[i], titles[i], inchToPixels(width), inchToPixels(height), path);

                    if (pieFilename != null) {
                        CellHandle call = (CellHandle) pie1.getContainer();
                        pie1.drop();

                        ImageHandle image = df.newImage(null);
                        image.setFile(pieFilename);
                        image.setWidth(width);
                        image.setHeight(height);
                        call.getContent().add(image);
                        piePath.add(pieFilename);
                    }
                }
                catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public ChartModel getChartModel(String key) {
        return chartMap.get(key);
    }

    private String createPie(List<Map<String, Object>> list, String category, String sliceSize, String title, int width, int height, String path) throws IOException {
        DataSetCalc dataSet = new DataSetCalc();

        for (Map<String, Object> row : list) {
            Object key = row.get(category);
            Object value = row.get(sliceSize);
            dataSet.put(key, value);
        }
        Pie pie = new Pie();
        return pie.create(dataSet, title, width, height, path);
    }

    private void generatorChart(ChartModel cm, List<String> chartPath, String path) {
        int[] chartsId = cm.getChartsId();
        Chart[] charts = cm.getCharts();
        String[] titles = cm.getTitles();
        ReportDesignHandle report = (ReportDesignHandle) design.getDesignHandle();
        ElementFactory df = report.getElementFactory();

        for (int i = 0; i < chartsId.length; i++) {
            try {
                ExtendedItemHandle chart = (ExtendedItemHandle) report.getElementByID(chartsId[i]);

                String width = chart.getWidth().getStringValue();
                String height = chart.getHeight().getStringValue();

                String chartFilename = charts[i].create(titles[i], inchToPixels(width), inchToPixels(height), path);

                if (chartFilename != null) {
                    CellHandle call = (CellHandle) chart.getContainer();
                    chart.drop();
                    logger.debug("chartFilename="+chartFilename);
                    ImageHandle image = df.newImage(null);
                    image.setFile(chartFilename);
                    image.setWidth(width);
                    image.setHeight(height);
                    call.getContent().add(image);
                    chartPath.add(chartFilename);
                }
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private int inchToPixels(String in) {
        String pure = in.replace("in", "");
        int pixels = 300;
        try {
            pixels = (int) (Double.parseDouble(pure) * 120.0);
        }
        catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
        }
        return pixels;
    }

    private void replaceSpace(Map<String, Object> parameters) {
        try {
            if (parameters == null) {
                return;
            }
            for (String key : parameters.keySet()) {
                Object value = parameters.get(key);
                if (value instanceof String) {
                    String strValue = (String) value;
                    parameters.put(key, strValue.replace("  ", "　"));
                }
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void clearTempChart(List<String> chartPath) {
        if (chartPath != null) {
            for (String aChartPath : chartPath) {
                File f = new File(aChartPath);
                if (f.exists()) {
                    f.delete();
                }
            }
        }
    }
    
// --------------------- GETTER / SETTER METHODS ---------------------

    public void setEngine(IReportEngine engine) {
        this.engine = engine;
    }

    public void setRenderOption(IRenderOption renderOption) {
        this.renderOption = renderOption;
    }
}
