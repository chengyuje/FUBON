package com.systex.jbranch.app.server.fps.crm435;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO;
import com.systex.jbranch.app.server.fps.sot712.SotPdfContext;
import com.systex.jbranch.app.server.fps.sot804.SOT804;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.PdfConfigVO;
import com.systex.jbranch.fubon.commons.PdfUtil;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.dao._062171_062171DAO;
import com.systex.jbranch.fubon.commons.cbs.dao._067157_067157DAO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 議價授權簽核紀錄庫
 * 
 * @author Eli
 * @date 2017/09/15
 * @spec
 */

@Component("crm435")
@Scope("request")
public class CRM435 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM435.class);

	@Autowired
	private CRM435OutputVO returnVO;

	@Autowired
	private _062171_062171DAO _062171_062171Dao;

	@Autowired
	private _067157_067157DAO _067157_067157Dao;

	@Autowired
	private CBSService cbsService;

	@Autowired
	private SOT804 sot804;

	private List<String> printRptIdList = new ArrayList<String>();

	public void inquire(Object body, IPrimitiveMap<?> header) throws Exception {

//		dam = this.getDataAccessManager();
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuffer sb = new StringBuffer();
//		sb.append("SELECT 1 ");
//		sb.append("FROM TBCRM_CUST_MAST ");
//		sb.append("WHERE CUST_ID = :custID ");
//		sb.append("AND ROUND(MONTHS_BETWEEN(SYSDATE, BIRTH_DATE) / 12, 2) >= 64.5 ");
//		
//		queryCondition.setQueryString(sb.toString());
//		queryCondition.setObject("custID", "T220494883");
//		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
//		this.print(body, header);
		try {
			this.test(null);
		} catch (Exception e) {
			System.out.println(e.getMessage());
//			e.printStackTrace();
		}

		this.sendRtnObject(returnVO);
	}

	public void print(Object body, IPrimitiveMap<?> header) throws Exception {
		PRDFitInputVO inputVO = new PRDFitInputVO();
		inputVO.setCaseCode(1);
		inputVO.setCustId("T220494883");
		inputVO.setPrdType("1");
		inputVO.setTradeType(1);
		dam = this.getDataAccessManager();

		List<String> list = new ArrayList<>();
		list.addAll(getPdfULst(inputVO, "sot814"));

		String reportURL = PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), true, list));

		notifyClientViewDoc(reportURL, "pdf");

	}

	private List<String> getPdfULst(PRDFitInputVO inputVO, String reportType) throws JBranchException, Exception {
		// 將有列印的表單ID放入List中
		if (!printRptIdList.contains(reportType.toUpperCase())) {
			printRptIdList.add(reportType.toUpperCase());
		}

		return new SotPdfContext(inputVO, reportType).getSotPdfULst();
	}

	private String test(String str) throws Exception {
		String[] array = str.split("");
		return null;
	}

}
