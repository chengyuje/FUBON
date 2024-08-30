package com.systex.jbranch.app.server.fps.marquee;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSFA_FPG_SCRLTXTVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MARQUEE
 * 
 * @author moron
 * @date 2015/12/25
 * @spec null
 */
@Component("marquee")
@Scope("request")
public class MARQUEE extends BizLogic{
	private DataAccessManager dam_obj = null;
	
	/**
	 * 對marquee的操作:查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException {
		MARQUEEInputVO MARQUEEInputVO = (MARQUEEInputVO) body;
		MARQUEEOutputVO return_VO = new MARQUEEOutputVO();
		dam_obj = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		String sql_s = "SELECT SCRL_ID,SCRL_TITLE,SCRL_DESC,SCRL_START_DT,SCRL_END_DT,MSG_TYPE,MSG_NEW,SCRL_STOP,CREATOR,CREATETIME,MODIFIER,LASTUPDATE from TBSFA_FPG_SCRLTXT where 1=1 ";
		if (!StringUtils.isBlank(MARQUEEInputVO.getScrl_title()))
			sql_s += "and SCRL_TITLE like :title ";
		if (MARQUEEInputVO.getScrl_start_dt() != null) {
			if (MARQUEEInputVO.getScrl_end_dt() != null)
				sql_s += "and SCRL_START_DT between :start and :end ";
			else
				sql_s += "and SCRL_START_DT >= :start ";
		}
		if (MARQUEEInputVO.getScrl_end_dt() != null) {
			if (MARQUEEInputVO.getScrl_start_dt() != null)
				sql_s += "and SCRL_END_DT between :start and :end ";
			else
				sql_s += "and SCRL_END_DT <= :end ";
		}
		if (!StringUtils.isBlank(MARQUEEInputVO.getCreator()))
			sql_s += "and CREATOR = :creator ";
		sql_s += "order by SCRL_START_DT, SCRL_END_DT ";
		queryCondition.setQueryString(sql_s);
		if (!StringUtils.isBlank(MARQUEEInputVO.getScrl_title()))
			queryCondition.setObject("title", "%" + MARQUEEInputVO.getScrl_title() + "%");
		if (MARQUEEInputVO.getScrl_start_dt() != null)
			queryCondition.setObject("start", MARQUEEInputVO.getScrl_start_dt());
		if (MARQUEEInputVO.getScrl_end_dt() != null) {
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(MARQUEEInputVO.getScrl_end_dt());
			calendar.add(Calendar.DATE, 1);
			calendar.add(Calendar.MILLISECOND, -1);
			queryCondition.setObject("end", calendar.getTime());
		}
		if (!StringUtils.isBlank(MARQUEEInputVO.getCreator()))
			queryCondition.setObject("creator", MARQUEEInputVO.getCreator());

		ResultIF list = dam_obj.executePaging(queryCondition, MARQUEEInputVO
				.getCurrentPageIndex() + 1, MARQUEEInputVO.getPageCount());
		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();

		return_VO.setResultList(list);// 傳遞回Flex端的List
		if (list.size() == 0) {
			throw new APException("ehl_01_common_001");
		}
		return_VO.setCurrentPageIndex(MARQUEEInputVO.getCurrentPageIndex());// 當前頁次
		return_VO.setTotalPage(totalPage_i);// 總頁次
		return_VO.setTotalRecord(totalRecord_i);// 總筆數
		this.sendRtnObject(return_VO);
	}
	
	/**
	 * 對marquee的操作:查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquireToday(Object body, IPrimitiveMap header)
			throws JBranchException {
		MARQUEEOutputVO return_VO = new MARQUEEOutputVO();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF condition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		String sql_s = " SELECT TITLE, CONTENT, MSG_LEVEL FROM TBCRM_EMP_SCROLLING_TEXT WHERE (SELECT SYSDATE FROM DUAL) BETWEEN START_DATE and END_DATE + 1 and DISPLAY = 'Y' ORDER BY SEQ ";
		condition.setQueryString(sql_s);
		List<Map<String, Object>> locallist = dam_obj.executeQuery(condition);
		return_VO.setResultList(locallist);
		this.sendRtnObject(return_VO);
	}
	
	/**
	 * 
	 * 根據客戶所填的參數信息，新增一筆資料
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void insert(Object body, IPrimitiveMap header)
			throws JBranchException {
		MARQUEEInputVO inputVO = (MARQUEEInputVO) body;
		dam_obj = this.getDataAccessManager();

		TBSFA_FPG_SCRLTXTVO vo = new TBSFA_FPG_SCRLTXTVO();
		vo.setSCRL_TITLE(inputVO.getScrl_title());
		vo.setSCRL_DESC(inputVO.getScrl_desc());
		vo.setSCRL_START_DT(new Timestamp(inputVO.getScrl_start_dt().getTime()));
		vo.setSCRL_END_DT(new Timestamp(inputVO.getScrl_end_dt().getTime()));
		vo.setMSG_TYPE(inputVO.getMsg_type());
		vo.setMSG_NEW(inputVO.getMsg_new());
		vo.setSCRL_STOP(inputVO.getScrl_stop());
		dam_obj.create(vo);
		
		this.sendRtnObject(null);
	}

	/**
	 * 
	 * 修改用戶所選的參數資料
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void update(Object body, IPrimitiveMap header)
			throws JBranchException {
		MARQUEEInputVO inputVO = (MARQUEEInputVO) body;
		dam_obj = this.getDataAccessManager();

		TBSFA_FPG_SCRLTXTVO vo = new TBSFA_FPG_SCRLTXTVO();
		vo = (TBSFA_FPG_SCRLTXTVO) dam_obj.findByPKey(
				TBSFA_FPG_SCRLTXTVO.TABLE_UID, inputVO.getScrl_id());
		if (vo != null) {
			vo.setSCRL_TITLE(inputVO.getScrl_title());
			vo.setSCRL_DESC(inputVO.getScrl_desc());
			vo.setSCRL_START_DT(new Timestamp(inputVO.getScrl_start_dt().getTime()));
			vo.setSCRL_END_DT(new Timestamp(inputVO.getScrl_end_dt().getTime()));
			vo.setMSG_TYPE(inputVO.getMsg_type());
			vo.setMSG_NEW(inputVO.getMsg_new());
			vo.setSCRL_STOP(inputVO.getScrl_stop());
			dam_obj.update(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		this.sendRtnObject(null);
	}

	/**
	 * 
	 * 刪除客戶所選定的參數資料
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void delete(Object body, IPrimitiveMap header)
			throws JBranchException {
		MARQUEEInputVO inputVO = (MARQUEEInputVO) body;
		dam_obj = this.getDataAccessManager();

		TBSFA_FPG_SCRLTXTVO vo = new TBSFA_FPG_SCRLTXTVO();
		vo = (TBSFA_FPG_SCRLTXTVO) dam_obj.findByPKey(
				TBSFA_FPG_SCRLTXTVO.TABLE_UID, inputVO.getScrl_id());
		if (vo != null) {
			dam_obj.delete(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		this.sendRtnObject(null);
	}
	
}
