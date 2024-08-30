package com.systex.jbranch.platform.common.report.generator.linemode;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.reflect.TypeToken;
import com.systex.jbranch.platform.common.dataManager.PlatformVOHelper;
import com.systex.jbranch.platform.common.dataManager.ServerTransaction;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.LineModeReport;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.generator.AbstractReportGenerator;
import com.systex.jbranch.platform.common.report.generator.linemode.context.Page;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.JsonUtil;
import com.systex.jbranch.platform.common.util.NullTK;

public class LineModeReportGenerator extends AbstractReportGenerator {
    private static final String REPORT_EXT = "rptdesign";
	private static final String ENCODE = "UTF-8";
	private Logger logger = LoggerFactory.getLogger(LineModeReportGenerator.class);
    private IReportEngine engine;
    private IRenderOption renderOption;
    private StoreIF store;
    private Double axisXSalt;
    private Double axisYSalt;
    private String deviceId;
    private String printPrompt;
    private String paperSize;
    private int cpi;
    private int lpi;
    private String formfeed;

	/* (non-Javadoc)
	 * @see com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF#generateReport(java.lang.String, java.lang.String, com.systex.jbranch.platform.common.report.reportdata.ReportDataIF)
	 */
	public ReportIF generateReport(String txnCode, String reportID, ReportDataIF data) throws JBranchException {
		// TODO Auto-generated method stub
		LineModeReport lineModeReport = new LineModeReport();
		IRunAndRenderTask task = null;
		try {
			long startTime = System.currentTimeMillis();
			String readReportPath = getReportPath(txnCode, reportID);
			IReportRunnable design = engine.openReportDesign(readReportPath);
			
			// 取得Report tittle並存入datamanager記錄ej用
			String tittle = (String) NullTK.checkNull(design.getProperty(IReportRunnable.TITLE), "");
			if (logger.isDebugEnabled()) {
                logger.debug("Report Name = " + tittle);
            }
			ServerTransaction st = dataManager.getServerTransaction(uuidNew);
			if(st != null){
				st.getPlatFormVO().setVar(PlatformVOHelper._EJMEMO, tittle);
			}
            task = engine.createRunAndRenderTask(design);
            
            ReportData reportData = (ReportData) data;

            Map parameters = reportData.getParameters();
            List list = reportData.getRecord();
            Map<String, List> records = reportData.getRecordListAll();
            
            if (records != null) {
                Map map = null;
                HashMap<String, List> contextMap = new HashMap<String, List>();
                for (String key : records.keySet()) {
                    List value = records.get(key);
                    contextMap.put(key, value);
                }
                task.setAppContext(contextMap);
            }


            checkParamExists(parameters, "$axisXSalt", axisXSalt);
            checkParamExists(parameters, "$axisYSalt", axisYSalt);
            checkParamExists(parameters, "$deviceId", deviceId);
            checkParamExists(parameters, "$printPrompt", printPrompt);
            checkParamExists(parameters, "$paperSize", paperSize);
            checkParamExists(parameters, "$cpi", cpi);
            checkParamExists(parameters, "$lpi", lpi);
            checkParamExists(parameters, "$formfeed", formfeed);
            String deviceId = (String) parameters.get("$deviceId");
            if(deviceId == null){
            	throw new JBranchException("尚未設定$deviceId");
            }
            
            task.setParameterValues(parameters);
            task.validateParameters();
            ByteArrayOutputStream os = new ByteArrayOutputStream(); 
            renderOption.setOutputStream(os);
            renderOption.setOutputFormat(format.getType());
            task.setRenderOption(renderOption);
            task.run();
            
            String jsonString = new String(os.toByteArray(), ENCODE);
            
            Type collectionType = new TypeToken<List<Page>>(){}.getType();
            List<Page> pages = JsonUtil.fromJson(jsonString, collectionType);
            Object o = reportData.getOthers();
            String rePrint = (String) reportData.getParameters().get("$rePrint");
            String reportName = txnCode + "_" + reportID;
        	String reportId = store.saveReportMaster(txnCode, reportName, deviceId, pages.size(), rePrint, o);           	
            for (int i = 0; i < pages.size(); i++) {
            	Page page = pages.get(i);
            	store.saveReportDetail(JsonUtil.toJson(page), reportId, i + 1);		
			}
            store.updateReportMasterToFinshed(reportId);
            long endTime = System.currentTimeMillis();
            Map<String, String> returnMap = new HashMap<String, String>();
			returnMap.put("reportId", reportId);
			returnMap.put("deviceId", deviceId);
			List<Map<String, String>> paramsList = new ArrayList<Map<String, String>>();
            paramsList.add(returnMap);
            lineModeReport.setParamsList(paramsList);
            lineModeReport.setSpeendingTime(endTime - startTime);
            lineModeReport.setReportId(reportId);
        }catch (Exception e) {
            throw new JBranchException(e.getMessage(), e);
        }finally{
        	if(task != null){
        		task.close();
        	}
        }
        return lineModeReport;
	}

	/* (non-Javadoc)
	 * @see com.systex.jbranch.platform.common.report.generator.AbstractReportGenerator#getReportExt()
	 */
	@Override
	public String getReportExt() {
		// TODO Auto-generated method stub
		return REPORT_EXT;
	}

	private void checkParamExists(Map parameters, String key, String value){
		if(parameters.get(key) == null){
			parameters.put(key, value);
		}
	}
	
	private void checkParamExists(Map parameters, String key, Double value){
		if(parameters.get(key) == null){
			parameters.put(key, value);
		}
	}
	
	private void checkParamExists(Map parameters, String key, Integer value){
		if(parameters.get(key) == null){
			parameters.put(key, value);
		}
	}
	
	@Override
	protected String getReportPath(String txnCode, String reportID)
    	throws JBranchException {
		try {
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
		            .append(reportID)
		    		.append(".")
		            .append(getReportExt());
		    return strReportFile.toString();
		}catch (Exception e) {
		    logger.error(e.getMessage(), e);
		    throw new JBranchException("pf_report_common_006");
		}
	}
	
    /**
     * @param engine
     */
    public void setEngine(IReportEngine engine) {
        this.engine = engine;
    }

    /**
     * @param renderOption
     */
    public void setRenderOption(IRenderOption renderOption) {
        this.renderOption = renderOption;
    }

	/**
	 * @return the store
	 */
	public StoreIF getStore() {
		return store;
	}

	/**
	 * @param store the store to set
	 */
	public void setStore(StoreIF store) {
		this.store = store;
	}

	/**
	 * @return the axisXSalt
	 */
	public Double getAxisXSalt() {
		return axisXSalt;
	}
	
	/**
	 * @param axisXSalt the axisXSalt to set
	 */
	public void setAxisXSalt(Double axisXSalt) {
		this.axisXSalt = axisXSalt;
	}

	/**
	 * @return the axisYSalt
	 */
	public Double getAxisYSalt() {
		return axisYSalt;
	}

	/**
	 * @param axisYSalt the axisYSalt to set
	 */
	public void setAxisYSalt(Double axisYSalt) {
		this.axisYSalt = axisYSalt;
	}

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * @return the printPrompt
	 */
	public String getPrintPrompt() {
		return printPrompt;
	}

	/**
	 * @param printPrompt the printPrompt to set
	 */
	public void setPrintPrompt(String printPrompt) {
		this.printPrompt = printPrompt;
	}

	/**
	 * @return the paperSize
	 */
	public String getPaperSize() {
		return paperSize;
	}

	/**
	 * @param paperSize the paperSize to set
	 */
	public void setPaperSize(String paperSize) {
		this.paperSize = paperSize;
	}

	/**
	 * @return the cpi
	 */
	public int getCpi() {
		return cpi;
	}

	/**
	 * @param cpi the cpi to set
	 */
	public void setCpi(int cpi) {
		this.cpi = cpi;
	}

	/**
	 * @return the lpi
	 */
	public int getLpi() {
		return lpi;
	}

	/**
	 * @param lpi the lpi to set
	 */
	public void setLpi(int lpi) {
		this.lpi = lpi;
	}

	/**
	 * @return the formfeed
	 */
	public String getFormfeed() {
		return formfeed;
	}

	/**
	 * @param formfeed the formfeed to set
	 */
	public void setFormfeed(String formfeed) {
		this.formfeed = formfeed;
	}	
	
	
}
