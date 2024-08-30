package com.systex.jbranch.app.server.fps.prd175;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPRD_INS_ANCDOCVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_INS_COMMISSIONVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_INS_DOCCHKVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_INS_LINKINGVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_INS_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_INS_PARAMETERVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_INS_SPECIAL_CNDVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * PRD175
 * 
 * @author Kevin Hsu
 * @date 2016/10/05
 * @spec null
 */

@Component("prd175")
@Scope("request")
public class PRD175 extends FubonWmsBizLogic {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	DataAccessManager dam_obj = null;

	/** ==主查詢== **/
	public void queryData(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		PRD175InputVO inputVO = (PRD175InputVO) body;
		PRD175OutputVO outputVO = new PRD175OutputVO();
		List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("   SELECT                           ");
			sb.append("   A.CUST_ID,                       ");
			sb.append("   A.TRAGET_ID,                       ");
			sb.append("   A.REG_DATE,                      ");			
			sb.append("   A.MODIFIER,                      ");
			sb.append("   A.LASTUPDATE,                    ");
			sb.append("   C.BRANCH_NBR,                    ");
			sb.append("   C.EMP_NAME,                      ");
			sb.append("   C.EMP_ID                         ");
			sb.append("   FROM                             ");
			sb.append("   TBPRD_FUND_TRAINING A,           ");
			sb.append("   VWORG_BRANCH_EMP_DETAIL_INFO C   ");
			sb.append("   WHERE A.CUST_ID = C.CUST_ID(+)   ");
			sb.append("   AND c.EMP_NAME　IS NOT NULL       ");
			sb.append("   AND c.EMP_ID　IS NOT NULL         ");

			// BRANCH_NBR
			if (!StringUtils.isBlank(inputVO.getBRANCH_NBR())) {
				sb.append(" and BRANCH_NBR LIKE :BRANCH_NBRR ");
			}

			if (!StringUtils.isBlank(inputVO.getEMP_ID())) {
				sb.append(" and EMP_ID LIKE :EMP_IDD ");
			}

			if (!StringUtils.isBlank(inputVO.getEMP_NAME())) {
				sb.append(" and EMP_NAME LIKE :EMP_NAMEE ");
			}

			if (inputVO.getREG_DATE() != null) {
				sb.append(" and TO_CHAR(REG_DATE,'YYYYMMDD') = TO_CHAR(:REG_DATEE,'YYYYMMDD') ");
			}

			qc.setQueryString(sb.toString());
			// tempList = dam_obj.exeQuery(qc);

			if (!StringUtils.isBlank(inputVO.getBRANCH_NBR())) {
				qc.setObject("BRANCH_NBRR", "%" + inputVO.getBRANCH_NBR() + "%");
			}

			if (!StringUtils.isBlank(inputVO.getEMP_ID())) {
				qc.setObject("EMP_IDD", "%" + inputVO.getEMP_ID() + "%");
			}

			if (!StringUtils.isBlank(inputVO.getEMP_NAME())) {
				qc.setObject("EMP_NAMEE", "%" + inputVO.getEMP_NAME() + "%");
			}

			if (inputVO.getREG_DATE() != null) {

				qc.setObject("REG_DATEE",  inputVO.getREG_DATE());
			}
			ResultIF list = dam_obj.executePaging(qc,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord();

			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數

			outputVO.setINS_ANCDOCList(list);
			sendRtnObject(outputVO);

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("發生錯誤");
		}

	}

}
