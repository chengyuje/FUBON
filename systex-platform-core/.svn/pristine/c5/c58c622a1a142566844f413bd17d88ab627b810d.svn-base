package com.systex.jbranch.platform.common.report.generator.msoffice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.lang.time.DateFormatUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.office.formula.DataProcessFormula;
import com.systex.jbranch.office.formula.FuncationFormula;
import com.systex.jbranch.office.word.WordReader;
import com.systex.jbranch.office.word.bookmark.BookmarkContent;
import com.systex.jbranch.office.word.wml.WmlParseReader;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.FlexReport;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.generator.AbstractReportGenerator;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.ParamsUtil;
import com.systex.jbranch.platform.common.util.PathUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;

public class MsWordWmlReportGenerator extends AbstractReportGenerator {
	// private Map<String, List> dataSets = new HashMap<String, List>();

	private Logger logger = LoggerFactory.getLogger(MsWordWmlReportGenerator.class);
	private static final String REPORT_EXT = "xml";
	private String savePath = null;
	private String templatePath;
	private ReportDataIF reportData;
	private boolean isPreviewMode = false;

	private ReportIF generateToWord() throws JBranchException {
		long startTime = System.currentTimeMillis();
		if (savePath == null) {
			savePath = PathUtil.getSavePath(strTxnCode);
		}

		File f = new File(templatePath);
		if (!f.exists()) {
			throw new JBranchException("template[" + templatePath + "]不存在");
		}

		WordReader reader = null;
		try {
			reader = new WmlParseReader(templatePath);
			String dId = f.getName();
			if(dId.indexOf(".") != -1){
				dId = dId.split("\\.")[0];
			}
			if(dId.indexOf("_") != -1){
				dId = dId.split("_")[0];
			}
			fillDocument(reader, dId);
			reader.save(savePath);
		} catch (Exception e) {
			throw new JBranchException(e.getMessage(), e);
		}finally{
			if(reader != null){
				try {
					reader.close();
				} catch (Exception e) {
					throw new JBranchException(e.getMessage(), e);
				}
			}
		}
		long endTime = System.currentTimeMillis();
		long speendingTime = endTime - startTime;
		String establishTime = DateFormatUtils.format(Calendar.getInstance(),
				"yyyyMMddhhmmss");
		File file = new File(savePath);
		String fileName = file.getName();

		ReportData reportData = (ReportData) this.reportData;
		String path = null;
		boolean pathflag = true;
		logger.debug("reportData.getPath()="+reportData.getPath());
		if (reportData.getPath() == null) {
			path = new File(serverPath, reportTemp).getAbsolutePath();
		} else {
			pathflag = false;
			path = reportData.getPath();
		}
		logger.debug("path="+path+",fileName="+fileName);
		String pathAndFileName = new File(path, fileName).getAbsolutePath();

		String returnURL = null;
		if (pathflag) {
			returnURL = reportTemp.substring(1) + fileName;
		} else {
			returnURL = pathAndFileName;
		}
		FlexReport report = new FlexReport();
		report.setLocation(returnURL);
		report.setReportName(fileName);
		report.setSpeendingTime(speendingTime * 0.001);
		report.setEstablishTime(establishTime);
		report.setMerge(false);
		return report;
	}

	private void fillDocument(WordReader reader, String dId) throws Exception {
		Map<String, BookmarkContent> ranges = reader.getBookmarkRange();
		DataAccessManager dam = new DataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition();
		String sql = "select bk.NAME, bk.DATATYPE, bk.LIMIT_LENGTH, bk.DATASOURCE, f.FID, f.BEAN_ID, ass.ARGUMENTS from TBSYSOVERPRINTBOOKMARK bk "
				+ "left join TBSYSOVERPRINTBFASS ass on ass.DID=bk.DID and ass.BNAME=bk.NAME "
				+ "left join TBSYSOVERPRINTFORMULA f on f.FID=ass.FID "
				+ "where bk.DID=? order by bk.DID, bk.NAME, ass.ORDER_NUM";

		qc.setQueryString(sql);
		qc.setString(1, dId);

		List<Map> list = dam.exeQuery(qc);
		if(list == null || list.size() == 0){
			return;	//查無屬性時，忽略
//			throw new JBranchException("查無文件Bookmark屬性[" + dId + "]");
		}
		Map params = reportData.getParameters();
		Map contextMap = reportData.getRecordListAll();
		Object otherParams = reportData.getOthers();
		if(otherParams != null && otherParams instanceof MsWordWmlOtherParams){
			MsWordWmlOtherParams wmlOtherParams = (MsWordWmlOtherParams) otherParams;
			isPreviewMode = wmlOtherParams.isPreviewMode();
		}

		JXPathContext paramsContext = JXPathContext.newContext(params);
		JXPathContext recordListContext = JXPathContext.newContext(contextMap);
		fillWaterMarker(reader.getDocument(), paramsContext, recordListContext);
		
		BookmarkData tempData = new BookmarkData();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> row = list.get(i);
			String bkName = (String) row.get("NAME");

			BigDecimal bkLimitLength = (BigDecimal) row.get("LIMIT_LENGTH");
			String name = (String) row.get("NAME");
			String bkDataType = (String) row.get("DATATYPE");
			String bkDataSource = (String) row.get("DATASOURCE");
			BigDecimal bkFId = (BigDecimal) row.get("FID");
			String bkBeanId = (String) row.get("BEAN_ID");
			String bkArgs = (String) row.get("ARGUMENTS");
				
			if(bkDataType != null && SysVariable.CUST.equals(bkDataType) == false){
				bkDataSource = bkDataType + "/" + bkDataSource;
			}
			if(bkDataSource == null || "".equals(bkDataSource)){
				throw new JBranchException("bookmark[" + name + "]未定義資料來源");
			}

			if((SysVariable.SYS + "/" + SysVariable.PAGE_CODE).equalsIgnoreCase(bkDataSource)){

				BookmarkDetail bd = new BookmarkDetail("", false);
				bd.setPageCode(true);
				tempData.setBookmarkInfo(bkName, bd);
				continue;
			}
			Object data = null;
			if(isPreviewMode){
				data = StringUtil.leftPad("", bkLimitLength.intValue(), "0");
			}else{
				try {
					if (bkDataSource.indexOf("/") == -1) {
						data = paramsContext.getValue(bkDataSource);
					} else {
						data = recordListContext.getValue(bkDataSource);
					}
					if(data == null){
						data = "";
//						throw new JBranchException("資料來源[" + bkDataSource + "]不存在");
					}
				} catch (JXPathException e) {
					data = "";	//由於資料查詢可能會無資料，造成map內無jxpath指定的參數而丟出例外，故不存在時以空字串顯示(Jeff說的 )20110323
//					throw new JBranchException("無法取得resource[" + bkDataSource + "]:" + e.getMessage());
				}
			}

//			BookmarkContent bc = ranges.get(bkName);
//			if(bc == null){
//				throw new IllegalArgumentException("在文件[" + dId + "]中，無此bookmark name[" + bkName + "]");
//			}

			BookmarkDetail bd = tempData.getDetail(bkName);
			if(bd == null){
				bd = new BookmarkDetail(data, true);
				tempData.setBookmarkInfo(bkName, bd);
			}else{
				data = bd.getData();
			}
			
			if (bkBeanId != null) {
				try {
					Object before = bd.getData();
					DataProcessFormula dpf = (DataProcessFormula) PlatformContext
							.getBean(bkBeanId);
					data = dpf.process(data, bkArgs);
					if(data == null){
						throw new JBranchException("Formulat bean[" + bkBeanId + "]回傳值不可為null");
					}
					
					logger.debug("FID[" + bkFId + "], DID[" + dId + "], BNAME[" + bkName + "], before data[ " + before + "], after data[" + data + "], arguments[" + bkArgs + "]");
					if(dpf instanceof FuncationFormula){
						bd.setData(data);
						bd.setText(false);
						continue;
					}
				} catch (Throwable e) {
					logger.warn("套用公式失敗:FID[" + bkFId + "], DID[" + dId + "], BNAME[" + bkName + "], data[" + data + "], class[" + data.getClass() + "], arguments[" + bkArgs + "]", e);
				}
			}
			logger.debug("FID[" + bkFId + "], DID[" + dId + "], BNAME[" + bkName + "], data[" + data + "]");
			bd.setData(data);
		}
		
		Iterator<Entry<String, BookmarkDetail>> it = tempData.iterator().iterator();
		while(it.hasNext()){
			Entry<String, BookmarkDetail> entry = it.next();
			BookmarkContent bc = ranges.get(entry.getKey());
			BookmarkDetail bd = entry.getValue();

			if(bc != null){
				if(bd.isText()){
					bc.setText(bd.getData().toString());
				}else if(bd.isPageCode()){
					bc.setPageCode((Integer) recordListContext.getValue(SysVariable.SYS + "/" + SysVariable.START_PAGE), (Integer) recordListContext.getValue(SysVariable.SYS + "/" + SysVariable.TOTAL_PAGES));
				}else{
					bc.setFuncationVarable(bd.getData().toString());
				}
			}
		}

	}

	private void fillWaterMarker(Document document,
			JXPathContext paramsContext, JXPathContext recordListContext) {
		List<Attribute> list = document.selectNodes("/w:wordDocument/w:body/w:sectPr/w:hdr/w:p/w:r/w:pict/v:shape/v:textpath/@string");
		Object data;
		String newValue;

		for (Attribute attr : list) {
			String value = attr.getText();

			String[] paramsName = ParamsUtil.getParamsName(value);
			for (String paramName : paramsName) {
				if((SysVariable.SYS + "/" + SysVariable.PAGE_CODE).equalsIgnoreCase(paramName)){
					continue;
				}
				try {
					if (paramName.indexOf("/") == -1) {
						data = paramsContext.getValue(paramName);
					} else {
						data = recordListContext.getValue(paramName);
					}
					
				} catch (JXPathException e) {
					logger.warn("RecordData中無法取得paramName[" + paramName + "]");
					data = null;
				}
				if(data != null){
					newValue = data.toString();
					value = ParamsUtil.replace(value, paramName, newValue);
				}
			}
			attr.setText(value);
		}

	}

//	public DatasourceContext getDatasetContext(String datasource) {
//
//		Pattern pattern = Pattern.compile("([^\\]]+)\\[(\\d)\\]\\.(.+)$");
//		Matcher matcher = pattern.matcher(datasource);
//		matcher.find();
//		if (matcher.groupCount() < 3) {
//			throw new IllegalArgumentException("不合法的Datasource格式[" + datasource
//					+ "]");
//		}
//		DatasourceContext dc = new DatasourceContext();
//		dc.setDataset(matcher.group(1));
//		dc.setIndex(Integer.parseInt(matcher.group(2)));
//		dc.setColumn(matcher.group(3));
//
//		return dc;
//	}

	public ReportIF generateReport(String txnCode, String reportID,
			ReportDataIF data) throws JBranchException {

		OutputStream fis = null;
		ByteArrayOutputStream bos = null;
		try {
			// 20091026
			strTxnCode = txnCode;

			ReportData reportData = (ReportData) data;

			// 取得AP Server當前執行路徑

			String fileName = null;
			if (reportData.getFileName() == null) {
				fileName = getSaveName(reportID);
			} else {
				fileName = reportData.getFileName() + "." + format.getExt();
			}
			
			String pathAndFileName = serverPath == null ? null : serverPath + reportTemp + fileName;
			this.setSavePath(pathAndFileName);
			this.setReportData(data);
			
			MsWordWmlOtherParams wmlOtherParams = null;
			Object otehrParams = reportData.getOthers();
			if(otehrParams != null && otehrParams instanceof MsWordWmlOtherParams == false){
				throw new JBranchException("MsWordWmlReportGenerator中OtherParams參數需為MsWordWmlOtherParams實體");
			}
			if(otehrParams != null){
				wmlOtherParams = (MsWordWmlOtherParams) otehrParams;
				templatePath = wmlOtherParams.getTemplatePath();
			}
			
			if(templatePath == null){
				this.setTemplatePath(getReportPath(txnCode, reportID));
			}
			return generateToWord();
			// =================================================

		} catch (Exception e) {
			throw new JBranchException(e.getMessage(), e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {

				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {

				}
			}
		}
	}

	public void setReportData(ReportDataIF reportData) {
		this.reportData = reportData;
	}

	public void setSavePath(String pathAndFileName) {
		this.savePath = pathAndFileName;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	@Override
	public String getReportExt() {
		return REPORT_EXT;
	}
	
	public static void main(String[] args) throws Exception {
		WmlParseReader reader = new WmlParseReader("D:/doc/定期定額申購申請書_newnew.xml");
		Document doc = reader.getDocument();
		///w:wordDocument/w:body/w:sectPr/w:ftr/w:p/w:r/w:pict/v:shape/v:textpath/@string
		///w:wordDocument/w:body/w:sectPr/w:hdr/w:p/w:r/w:pict/v:shape/v:textpath/@string
		List<Attribute> list = doc.selectNodes("/w:wordDocument/w:body/w:sectPr/w:hdr/w:p/w:r/w:pict/v:shape/v:textpath/@string");
		for (Attribute attr : list) {
			attr.setText("abc1");
		}
		list = doc.selectNodes("w:wordDocument/w:body/w:sectPr/w:ftr/w:p/w:r/w:pict/v:shape/v:textpath/@string");
		for (Attribute attr : list) {
			attr.setText("abc2");
		}
		reader.save("D:/doc/定期定額申購申請書_newnew2.xml");
		System.out.println("finshed");
	}
}
