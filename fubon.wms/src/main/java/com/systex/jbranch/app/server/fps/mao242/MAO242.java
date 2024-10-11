package com.systex.jbranch.app.server.fps.mao242;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBMAO_DEV_APL_PLISTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("mao242")
@Scope("request")
public class MAO242 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {

		MAO242InputVO inputVO = (MAO242InputVO) body;
		MAO242OutputVO outputVO = new MAO242OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DM.BRA_NBR AS BRANCH_NBR, ");
		sql.append("       E.AO_CODE, ");
		sql.append("       E.EMP_NAME, ");
		sql.append("       P.USE_DATE, ");
		sql.append("       P.DEV_NBR, ");
		sql.append("       P.SEQ, ");
		sql.append("       P.USE_PERIOD_S_TIME, ");
		sql.append("       P.USE_PERIOD_E_TIME, ");
		sql.append("       SUBSTR(P.USE_PERIOD_S_TIME, 1, 2) || ':00' AS START_TIME, ");
		sql.append("       CASE WHEN TO_NUMBER(SUBSTR(P.USE_PERIOD_E_TIME, 1, 2)) < 9 THEN '次日' ELSE '' END || SUBSTR(P.USE_PERIOD_E_TIME, 1, 2) || ':00' AS END_TIME, ");
		sql.append("       P.DEV_STATUS, ");
		sql.append("       CASE WHEN (SELECT COUNT(1) FROM TBMAO_DEV_APL_PLIST WHERE DEV_STATUS IN ('D06', 'E07') AND DEV_NBR = P.DEV_NBR) > 0 THEN 'N' ELSE 'Y' END AS GIVE_YN, ");
		sql.append("       P.DEV_TAKE_EMP_ID, ");
		sql.append("       P.DEV_TAKE_DATETIME, ");
		sql.append("       P.DEV_RETURN_EMP_ID, ");
		sql.append("       P.DEV_RETURN_DATETIME ");
		sql.append("FROM TBMAO_DEV_APL_PLIST P ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO E ON P.APL_EMP_ID = E.EMP_ID ");
		sql.append("LEFT JOIN TBMAO_DEV_MAST DM ON P.DEV_NBR = DM.DEV_NBR ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND DM.FLAG = 'U' ");
		sql.append("AND P.DEV_STATUS IN ('C05', 'D06', 'E07') ");

		if (StringUtils.isNotBlank(inputVO.getBra_areaID()) && !"null".equals(inputVO.getBra_areaID())) {
			sql.append("AND DM.OP_NBR = :branchAreaID "); //營運區代碼
			queryCondition.setObject("branchAreaID", inputVO.getBra_areaID());
		} else if (StringUtils.isNotBlank(inputVO.getRegionCenterId()) && !"null".equals(inputVO.getBra_areaID())) {
			sql.append("AND DM.DC_NBR = :regionCenterID "); //區域代碼
			queryCondition.setObject("regionCenterID", inputVO.getRegionCenterId());
		}

		if (inputVO.getUse_date_bgn() != null) {
			sql.append("AND TRUNC(P.USE_DATE) >= TRUNC(:start) ");
			queryCondition.setObject("start", new Timestamp(inputVO.getUse_date_bgn().getTime()));
		}

		if (inputVO.getUse_date_end() != null) {
			sql.append("AND TRUNC(P.USE_DATE) <= TRUNC(:end) ");
			queryCondition.setObject("end", new Timestamp(inputVO.getUse_date_end().getTime()));
		}

		if (StringUtils.isNotBlank(inputVO.getDev_status())) {
			sql.append("AND P.DEV_STATUS = :dev_status ");
			queryCondition.setObject("dev_status", inputVO.getDev_status());
		}

		sql.append("ORDER BY USE_PERIOD_S_TIME DESC, P.USE_DATE DESC ");

		queryCondition.setQueryString(sql.toString());

		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();

		outputVO.setResultList(list);
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數

		this.sendRtnObject(outputVO);
	}

	public void reply(Object body, IPrimitiveMap header) throws JBranchException {

		WorkStation ws = DataManager.getWorkStation(uuid);
		MAO242InputVO inputVO = (MAO242InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		TBMAO_DEV_APL_PLISTVO vo = new TBMAO_DEV_APL_PLISTVO();
		vo = (TBMAO_DEV_APL_PLISTVO) dam.findByPKey(TBMAO_DEV_APL_PLISTVO.TABLE_UID, inputVO.getSeq());
		if (null != vo) {
			if (inputVO.getReply_type().equals("T")) {
				vo.setDEV_STATUS("D06");
				vo.setDEV_TAKE_EMP_ID(ws.getUser().getUserID());
				vo.setDEV_TAKE_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
				dam.update(vo);
			} else if (inputVO.getReply_type().equals("B")) {
				vo.setDEV_STATUS("A02");
				vo.setDEV_RETURN_EMP_ID(ws.getUser().getUserID());
				vo.setDEV_RETURN_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
				dam.update(vo);

				// 刪除內部通知
				StringBuffer sb = new StringBuffer();
				sb.append("DELETE FROM TBCRM_WKPG_MD_MAST WHERE EMP_ID = :empID AND DISPLAY_NO = '199'");
				queryCondition.setQueryString(sb.toString());
				queryCondition.setObject("empID", vo.getAPL_EMP_ID());
				dam.exeUpdate(queryCondition);
			} else {
				vo.setDEV_STATUS("X");
				dam.update(vo);
			}
		} else {
			throw new APException("ehl_01_common_008");
		}

		this.sendRtnObject(null);
	}
}