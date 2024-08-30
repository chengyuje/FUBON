package com.systex.jbranch.app.server.fps.pms103;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @Description : 銷售計劃明細
 * @Author      : 20160617 KevinHsu
 * @Editor      : 20170112 KevinHsu
 */

@Component("pms103")
@Scope("request")
public class PMS103 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;

	public void getYmList(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS103OutputVO outputVO = new PMS103OutputVO();
		
		outputVO.setYmList(PMS000.getSalesPlanYMlist());
		
		this.sendRtnObject(outputVO);
	}

	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {

		PMS103InputVO inputVO = (PMS103InputVO) body;
		PMS103OutputVO outputVO = new PMS103OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員

		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		
		sb.append("SELECT SEQ, ");
		sb.append("       PLAN_YEARMON, BRANCH_NBR, AO_CODE, EMP_ID, EMP_NAME, CUST_ID, CUST_NAME, SRC_TYPE, ");
		sb.append("       EST_PRD, EST_AMT, EST_EARNINGS_RATE, EST_EARNINGS, ACTION_DATE, MEETING_DATE, CLOSE_DATE, ");
		sb.append("       TO_CHAR(CLOSE_DATE, 'YYYYMM') AS C_YYYYMM, ");
		sb.append("       VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
		sb.append("FROM TBPMS_SALES_PLANS ");
		sb.append("WHERE 1 = 1	");
		
		/** 判斷是否為銷售人員 **/
		if (fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {	// 增加可視範圍為LoginID可查詢資料
			sb.append("AND EMP_ID = :loginID ");
			queryCondition.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		} else if (!headmgrMap.containsKey(roleID)) {					// 非總行人員可視範圍為AvalibleBranch可查詢資料
			sb.append("AND BRANCH_NBR IN (:avalibleBranch) ");
			queryCondition.setObject("avalibleBranch", (List<Map<String, String>>) getCommonVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}

		// 預計成交月份
		if (!StringUtils.isBlank(inputVO.getsTime())) {
			sb.append("AND TO_CHAR(CLOSE_DATE, 'YYYYMM') = :ym ");
			queryCondition.setObject("ym", inputVO.getsTime());
		}
		
		// 分行條件
		if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
			sb.append("AND BRANCH_NBR = :BRANCH_NBR ");
			queryCondition.setObject("BRANCH_NBR", inputVO.getBranch_nbr());
		}
		
		// 理專條件
		if (!StringUtils.isBlank(inputVO.getAo_code())) {
			sb.append("AND AO_CODE = :AO_CODE ");
			queryCondition.setObject("AO_CODE", inputVO.getAo_code());
		}
		
		// 客戶ID
		if (!StringUtils.isBlank(inputVO.getCUST_ID())) {
			sb.append("AND CUST_ID LIKE :CUST_ID ");
			queryCondition.setObject("CUST_ID", inputVO.getCUST_ID() + "%");
		}
		
		sb.append("ORDER BY SRC_TYPE, SEQ ");
		
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		outputVO.setResultList(list);
		outputVO.setResultTotal(list);
		outputVO.setCurrentYM(sdf.format(cal.getTime()));
		
		/** 銷售目標 **/
		sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("SELECT NVL(SUM(TAR_AMOUNT), 0) AS SALETARGET ");
		sb.append("FROM TBPMS_AO_PRD_TAR_M ");
		sb.append("WHERE 1 = 1 ");
		
		/** 判斷是否為銷售人員 **/
		if (fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {	//增加可視範圍為LoginID可查詢資料
			sb.append("AND EMP_ID = :loginID ");
			queryCondition.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		} else if (!headmgrMap.containsKey(roleID)) {					//非總行人員可視範圍為AvalibleBranch可查詢資料
			sb.append("AND BRANCH_NBR in (:avalibleBranch) ");
			queryCondition.setObject("avalibleBranch", (List<Map<String, String>>) getCommonVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}

		if (!StringUtils.isBlank(inputVO.getsTime())) {
			sb.append("AND DATA_YEARMON = :STARTTIME ");
			queryCondition.setObject("STARTTIME", inputVO.getsTime());
		} else {
			// 預設查詢當月資料
			sb.append("AND DATA_YEARMON = TO_CHAR(sysdate, 'YYYYMM') ");
		}

		// 分行條件
		if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
			sb.append("AND BRANCH_NBR = :BRANCH_NBR ");
			queryCondition.setObject("BRANCH_NBR", inputVO.getBranch_nbr());
		}
		
		// 理專條件
		if (!StringUtils.isBlank(inputVO.getAo_code())) {
			sb.append("AND AO_CODE = :AO_CODE ");
			queryCondition.setObject("AO_CODE", inputVO.getAo_code());
		}

		queryCondition.setQueryString(sb.toString());
		
		outputVO.setTarList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}

	/** 刪除 **/
	public void delRes(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS103InputVO inputVO = (PMS103InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("DELETE FROM TBPMS_SALES_PLANS WHERE SEQ = :SEQ ");
		
		queryCondition.setObject("SEQ", inputVO.getSEQ());
		
		queryCondition.setQueryString(sb.toString());
		
		dam.exeUpdate(queryCondition);

		this.sendRtnObject(null);
	}

}
