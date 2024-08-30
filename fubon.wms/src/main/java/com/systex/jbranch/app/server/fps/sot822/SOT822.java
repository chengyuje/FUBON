package com.systex.jbranch.app.server.fps.sot822;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
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
 * 海外債風險預告書
 */
@Component("sot822")
@Scope("request")
public class SOT822 extends SotPdf {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT822.class);
	
	/**
	 * PM上傳海外債風險預告書
	 * @throws SQLException 
	 */
	@Override
	public List<String> printReport() throws Exception {
		PRDFitInputVO inputVO = getInputVO();
		List<String> url_list = new ArrayList<String>();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		StringBuffer sql = new StringBuffer();
		if (inputVO.getCaseCode() == 1) { //Case1 下單
			sql.append("select R.RISK_FILE ");
			sql.append(" FROM TBSOT_BN_TRADE_D D ");
			sql.append(" INNER JOIN TBPRD_BOND_RISK_FILE R ON R.PRD_ID = D.PROD_ID ");
			sql.append(" WHERE D.TRADE_SEQ = :tradeSeq");
			queryCondition.setObject("tradeSeq", inputVO.getTradeSeq());
			queryCondition.setQueryString(sql.toString());
			list = dam.exeQuery(queryCondition);
		} else { //Case1 適配
			sql.append("select RISK_FILE ");
			sql.append("from TBPRD_BOND_RISK_FILE ");
			sql.append("where PRD_ID = :prdId ");
			queryCondition.setObject("prdId", inputVO.getPrdId());
			queryCondition.setQueryString(sql.toString());
			list = dam.exeQuery(queryCondition);
		}
		
		if (CollectionUtils.isNotEmpty(list)) {
			String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			String uuid = UUID.randomUUID().toString();
			Blob blob = (Blob) list.get(0).get("RISK_FILE");
			byte[] blobAsBytes = blob.getBytes(1, (int) blob.length());

			File targetFile = new File(filePath, uuid);
			FileOutputStream fos = new FileOutputStream(targetFile);
			fos.write(blobAsBytes);
			fos.close();
			
			url_list.add("temp/" + uuid);
		}
			
		return url_list;
	}
}