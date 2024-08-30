package com.systex.jbranch.app.server.fps.sot809;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_REPORTVO;
import com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO;
import com.systex.jbranch.app.server.fps.sot712.SotPdf;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;

/**
 * MENU
 * SI申購申請書報表
 * @author Lily
 * @date 2016/11/09
 * @spec null
 */
@Component("sot809")
@Scope("request")
public class SOT809 extends SotPdf {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT809.class);
	
	/**
	 * WMS-TDS-SOT810-SI贖回申請書報表  SI 贖回申請書報表不用套表了 改從 PM 專區抓
	 */
	@Override
	public List<String> printReport() throws JBranchException {
		//改為列印 PM 專區上傳的輔消文件
		String url = null;
		String txnCode = "SOT809";
		String reportID = "R1";
		ReportIF report = null;
		
		
		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator();  //產出pdf
		List<String> url_list = new ArrayList<String>();

		PRDFitInputVO inputVO = getInputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		try {
			
			StringBuffer sql = new StringBuffer();
			sql.append("select M.CUST_ID, M.CUST_NAME,M.REC_SEQ,D.*,C.CUST_RISK_ATR ");
			sql.append("from TBSOT_SI_TRADE_D D ");
			sql.append("inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
			sql.append("left outer join TBCRM_CUST_MAST C on C.CUST_ID = M.CUST_ID ");
			sql.append("where D.TRADE_SEQ = :tradeSeq ");
			queryCondition.setObject("tradeSeq", inputVO.getTradeSeq());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0 && list.get(0).get("PROD_ID") !=null) {
				if (list.get(0).get("PROD_ID") != null && StringUtils.isNotBlank(list.get(0).get("PROD_ID").toString())) {
					String prodId = list.get(0).get("PROD_ID").toString();
					StringBuffer sqlFile = new StringBuffer();
					sqlFile.append("SELECT DOC_FILE FROM TBSYS_FILE_DETAIL ");
					sqlFile.append("WHERE DOC_ID IN (SELECT DOC_ID FROM TBSYS_FILE_MAIN  ");
					sqlFile.append(" WHERE DOC_ID IN (SELECT DOC_ID FROM TBSYS_PRD_LINK  ");
					sqlFile.append("  WHERE PTYPE = 'SI'  ");
					sqlFile.append(" AND PRD_ID = :PRD_ID)  ");
					sqlFile.append("AND DOC_TYPE = '03') ");
					sqlFile.append("ORDER BY CREATETIME DESC ");
					QueryConditionIF queryFileCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					queryFileCondition.setObject("PRD_ID", prodId);
					queryFileCondition.setQueryString(sqlFile.toString());
					List<Map<String, Object>> listFile = dam.exeQuery(queryFileCondition);

					if (listFile.size() > 0 && listFile.get(0).get("DOC_FILE") != null) {
						
						String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
						String uuid = UUID.randomUUID().toString();
						String fileName =String.format("SI_%s_%s.pdf", prodId,uuid);
						Blob blob = (Blob) listFile.get(0).get("DOC_FILE");
						int blobLength = (int) blob.length();
						byte[] blobAsBytes = blob.getBytes(1, blobLength);

						File targetFile = new File(filePath, uuid);
						FileOutputStream fos = new FileOutputStream(targetFile);
						fos.write(blobAsBytes);
						fos.close();
						
						url_list.add("temp/" + uuid);
					}
				}
			}
			return url_list;
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		} 
	}

	private String getString(Object val){
		if(val != null){
			return val.toString();
		}else{
			return "";
		}
	}
	
}