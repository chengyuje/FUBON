package com.systex.jbranch.app.server.fps.pms331;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPMS_HO_MGMTPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_HO_MGMTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :Controller <br>
 * Comments Name : PMS331.java<br>
 * Author :Frank<br>
 * Date :2016年07月07日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */

@Component("pms331")
@Scope("request")
public class PMS331 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS331.class);

	/*** 查詢資料 ***/
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS331InputVO inputVO = (PMS331InputVO) body;
		PMS331OutputVO outputVO = new PMS331OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		StringBuffer sql = new StringBuffer("SELECT ROWNUM, T.* FROM ( ");
		sql.append("SELECT YEARMON, REGION_CENTER_NAME, BRANCH_NBR, ");
		sql.append("BRANCH_NAME, AO_CODE, CUST_ID, CUST_NAME, ");
		sql.append("VIP_DEGREE, AUM_DEGREE, AUM, INV_AUM, ");
		sql.append("INS_AUM, TO_CHAR(CHECK_DAY, 'YYYY/MM/DD') as CHECK_DAY, ");
		sql.append("FEE, CNT, P_AO_CODE, A_AO_CODE, T_FLAG ");
		sql.append("FROM TBPMS_HO_MGMT ");
		sql.append("WHERE 1=1 ");
		// 報表月份
		if (!StringUtils.isBlank(inputVO.getDataMonth())) {
			sql.append("and YEARMON = :yrmn ");
		}
		// 區域中心
		if (!StringUtils.isBlank(inputVO.getRc_id())) {
			sql.append("and REGION_CENTER_ID = :rcid ");
		}
		// 營運區
		if (!StringUtils.isBlank(inputVO.getOp_id())) {
			sql.append("and BRANCH_AREA_ID = :opid ");
		}
		// 分行
		if (!StringUtils.isBlank(inputVO.getBr_id())) {
			sql.append("and BRANCH_NBR = :brid ");
		}
		// 理專AO CODE
		if (!StringUtils.isBlank(inputVO.getAoEmp())) {
			sql.append("and AO_CODE = :aocode ");
		}
		// 分派狀況：已分派
		if (inputVO.getAssignType().equals("2"))
			sql.append("and A_AO_CODE is not null ");
		// 分派狀況：未分派
		if (inputVO.getAssignType().equals("3"))
			sql.append("and A_AO_CODE is null ");
		sql.append("order by REGION_CENTER_ID, BRANCH_NBR, AO_CODE) T ");
		condition.setQueryString(sql.toString());

		if (!StringUtils.isBlank(inputVO.getDataMonth())) {
			condition.setObject("yrmn", inputVO.getDataMonth());
		}
		if (!StringUtils.isBlank(inputVO.getRc_id())) {
			condition.setObject("rcid", inputVO.getRc_id());
		}
		if (!StringUtils.isBlank(inputVO.getOp_id())) {
			condition.setObject("opid", inputVO.getOp_id());
		}
		if (!StringUtils.isBlank(inputVO.getBr_id())) {
			condition.setObject("brid", inputVO.getBr_id());
		}
		if (!StringUtils.isBlank(inputVO.getAoEmp())) {
			condition.setObject("aocode", inputVO.getAoEmp());
		}

		ResultIF list = dam.executePaging(condition,
				inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

		if (list.size() > 0) {
			int totalPage = list.getTotalPage();
			outputVO.setTotalPage(totalPage);
			outputVO.setResultList(list);
			outputVO.setTotalRecord(list.getTotalRecord());
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}

	/* ==== 【儲存】更新資料 ======== */
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		PMS331InputVO inputVO = (PMS331InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		String aAoCode, aAoCode2 = "";

		for (Map<String, Object> map : inputVO.getList()) {
			for (Map<String, Object> map2 : inputVO.getList2()) {
				aAoCode = map.get("A_AO_CODE") == null ? "" : map.get(
						"A_AO_CODE").toString();
				aAoCode2 = map2.get("A_AO_CODE") == null ? "" : map2.get(
						"A_AO_CODE").toString();
				if (map.get("YEARMON").equals(map2.get("YEARMON"))
						&& map.get("CUST_ID").equals(map2.get("CUST_ID"))
						&& (!aAoCode.equals(aAoCode2))) {
					TBPMS_HO_MGMTPK pk = new TBPMS_HO_MGMTPK();
					pk.setYEARMON(map.get("YEARMON").toString());
					pk.setCUST_ID(map.get("CUST_ID").toString());

					TBPMS_HO_MGMTVO paramVO = (TBPMS_HO_MGMTVO) dam.findByPKey(
							TBPMS_HO_MGMTVO.TABLE_UID, pk);
					paramVO.setA_AO_CODE(map.get("A_AO_CODE").toString());
					paramVO.setModifier("test");
					paramVO.setLastupdate(stamp);

					dam.update(paramVO);
				}
			}
		}
		sendRtnObject(null);
	}

	/* ==== 【確認】移轉資料 ======== */
	public void confirm(Object body, IPrimitiveMap header)
			throws JBranchException {
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		PMS331InputVO inputVO = (PMS331InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();

		for (Map<String, Object> map : inputVO.getList()) {
			TBPMS_HO_MGMTPK pk = new TBPMS_HO_MGMTPK();
			pk.setYEARMON(map.get("YEARMON").toString());
			pk.setCUST_ID(map.get("CUST_ID").toString());

			TBPMS_HO_MGMTVO paramVO = (TBPMS_HO_MGMTVO) dam.findByPKey(
					TBPMS_HO_MGMTVO.TABLE_UID, pk);
			paramVO.setT_FLAG("Y");
			paramVO.setModifier("test");
			paramVO.setLastupdate(stamp);

			dam.update(paramVO);
		}
		sendRtnObject(null);
	}

}