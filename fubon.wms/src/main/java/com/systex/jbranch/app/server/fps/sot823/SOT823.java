package com.systex.jbranch.app.server.fps.sot823;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
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
 * 海外債客戶年齡與金融商品年限申購聲明書
 */
@Component("sot823")
@Scope("request")
public class SOT823 extends SotPdf {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT823.class);
	
	/**
	 * 客戶年齡與金融商品年限申購聲明書
	 * @throws SQLException 
	 */
	@Override
	public List<String> printReport() throws Exception {
		String url = null;
        String txnCode = "SOT823";
        String reportID = "R1";
        ReportIF report = null;
        List<String> url_list = new ArrayList<String>();

        ReportFactory factory = new ReportFactory();
        ReportDataIF data = new ReportData();
        ReportGeneratorIF gen = factory.getGenerator();  //產出pdf


        PRDFitInputVO inputVO = getInputVO();
        dam = this.getDataAccessManager();
        QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        
        StringBuffer sql = new StringBuffer();
        if (inputVO.getCaseCode() == 1) { //Case1 下單
	        sql.append("select D.PROD_NAME, ROUND(MONTHS_BETWEEN(SYSDATE, C.BIRTH_DATE)/12, 2) AS AGE,  ")
	                .append(" ROUND((P.DATE_OF_MATURITY - SYSDATE) / 365, 2) as DATE_OF_MATURITY, ") //剩餘年期=(到期日-today())/360，四捨五入取到小數第二位
	                .append(" (NVL(ROUND(MONTHS_BETWEEN(SYSDATE, C.BIRTH_DATE)/12, 2), 0) + ROUND((P.DATE_OF_MATURITY - SYSDATE) / 365, 2)) AS SCORE ")
	                .append(" from TBSOT_BN_TRADE_D D ")
	                .append(" left join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ")
	                .append(" left join TBCRM_CUST_MAST C on C.CUST_ID = M.CUST_ID ")
	                .append(" left join TBPRD_BOND P on P.PRD_ID = D.PROD_ID ")
	                .append("where D.TRADE_SEQ = :tradeSeq order by BATCH_SEQ ");
	        queryCondition.setObject("tradeSeq", inputVO.getTradeSeq());
	        queryCondition.setQueryString(sql.toString());
        } else { //Case1 適配
        	sql.append("select P.BOND_CNAME AS PROD_NAME, ROUND(MONTHS_BETWEEN(SYSDATE, C.BIRTH_DATE)/12, 2) AS AGE,  ")
            .append(" ROUND((P.DATE_OF_MATURITY - SYSDATE) / 365, 2) as DATE_OF_MATURITY, ") //剩餘年期=(到期日-today())/360，四捨五入取到小數第二位
            .append(" (NVL(ROUND(MONTHS_BETWEEN(SYSDATE, C.BIRTH_DATE)/12, 2), 0) + ROUND((P.DATE_OF_MATURITY - SYSDATE) / 365, 2)) AS SCORE ")
            .append(" from TBCRM_CUST_MAST C, TBPRD_BOND P ")
            .append("where C.CUST_ID = :custId AND P.PRD_ID = :prdId ");
        	queryCondition.setObject("custId", inputVO.getCustId());
        	queryCondition.setObject("prdId", inputVO.getPrdId());
        	queryCondition.setQueryString(sql.toString());
        }
        List<Map<String, Object>> list = checkList(dam.exeQuery(queryCondition));
        
        if (list.size() > 0) {
            Map<String, Object> tradeData = list.get(0);
            data.addParameter("PROD_NAME", ObjectUtils.toString(tradeData.get("PROD_NAME")));
            data.addParameter("AGE", ObjectUtils.toString(tradeData.get("AGE")));
            data.addParameter("DATE_OF_MATURITY", ObjectUtils.toString(tradeData.get("DATE_OF_MATURITY")));
            data.addParameter("SCORE", ObjectUtils.toString(tradeData.get("SCORE")));
            
            report = gen.generateReport(txnCode, reportID, data);
            url = report.getLocation();
            url_list.add(url);
        }
        
        return url_list;
	}
}