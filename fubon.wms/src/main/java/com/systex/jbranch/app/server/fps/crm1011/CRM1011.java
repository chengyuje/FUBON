package com.systex.jbranch.app.server.fps.crm1011;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_EMP_SCROLLING_TEXTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author moron
 * @date 2016/04/20
 * @spec null
 */
@Component("crm1011")
@Scope("request")
public class CRM1011 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM1011.class);
	
	public void queryCRM1011List(Object body, IPrimitiveMap header) throws JBranchException {
		CRM1011InputVO inputVO = (CRM1011InputVO) body;
		CRM1011OutputVO return_VO = new CRM1011OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select a.*,b.EMP_NAME from TBCRM_EMP_SCROLLING_TEXT a ");
		sql.append("left join VWORG_BRANCH_EMP_DETAIL_INFO b on a.MODIFIER = b.EMP_ID ");
		sql.append("where 1=1 ");
		if (!StringUtils.isBlank(inputVO.getTitle())) {
			sql.append("and a.TITLE like :title ");
			queryCondition.setObject("title", "%" + inputVO.getTitle() + "%");
		}	
		if (!StringUtils.isBlank(inputVO.getCreator())) {
			sql.append("and b.EMP_NAME like :creator ");
			queryCondition.setObject("creator", "%" + inputVO.getCreator() + "%");
		}	
		if (inputVO.getsDate() != null) {
			sql.append("and a.START_DATE >= :start ");
			queryCondition.setObject("start", inputVO.getsDate());
		}	
		if (inputVO.geteDate() != null) {
			sql.append("and a.END_DATE <= :end ");
			queryCondition.setObject("end", inputVO.geteDate());
		}	
		queryCondition.setQueryString(sql.toString());
		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();
		return_VO.setResultList(list);
		return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		return_VO.setTotalPage(totalPage_i);// 總頁次
		return_VO.setTotalRecord(totalRecord_i);// 總筆數
		this.sendRtnObject(return_VO);
	}
	
	/**
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void clickDeleteCRM1011List(Object body, IPrimitiveMap header) throws JBranchException {
		CRM1011InputVO inputVO = (CRM1011InputVO) body;
		dam = this.getDataAccessManager();

		TBCRM_EMP_SCROLLING_TEXTVO vo = new TBCRM_EMP_SCROLLING_TEXTVO();
		vo = (TBCRM_EMP_SCROLLING_TEXTVO) dam.findByPKey(
				TBCRM_EMP_SCROLLING_TEXTVO.TABLE_UID, inputVO.getId());
		if (vo != null) {
			dam.delete(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		this.sendRtnObject(null);
	}
	
	/**
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void clickInsertCRM1011List(Object body, IPrimitiveMap header) throws JBranchException {
		CRM1011InputVO inputVO = (CRM1011InputVO) body;
		dam = this.getDataAccessManager();

		// seq
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBCRM_EMP_SCROLLING_TEXT.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		
		TBCRM_EMP_SCROLLING_TEXTVO vo = new TBCRM_EMP_SCROLLING_TEXTVO();
		vo.setSEQ(seqNo);
		vo.setTITLE(inputVO.getTitle());
		vo.setCONTENT(inputVO.getContent());
		vo.setSTART_DATE(new Timestamp(inputVO.getsDate().getTime()));
		vo.setEND_DATE(new Timestamp(inputVO.geteDate().getTime()));
		vo.setMSG_LEVEL(inputVO.getMsg_level());
		vo.setNEW_MSG_YN("Y");
		vo.setDISPLAY(inputVO.getDisplay());
		dam.create(vo);
		this.sendRtnObject(null);
	}
	
	/**
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void clickModifyCRM1011List(Object body, IPrimitiveMap header) throws JBranchException {
		CRM1011InputVO inputVO = (CRM1011InputVO) body;
		dam = this.getDataAccessManager();

		TBCRM_EMP_SCROLLING_TEXTVO vo = new TBCRM_EMP_SCROLLING_TEXTVO();
		vo = (TBCRM_EMP_SCROLLING_TEXTVO) dam.findByPKey(
				TBCRM_EMP_SCROLLING_TEXTVO.TABLE_UID, inputVO.getId());
		if (vo != null) {
			vo.setTITLE(inputVO.getTitle());
			vo.setCONTENT(inputVO.getContent());
			vo.setSTART_DATE(new Timestamp(inputVO.getsDate().getTime()));
			vo.setEND_DATE(new Timestamp(inputVO.geteDate().getTime()));
			vo.setMSG_LEVEL(inputVO.getMsg_level());
			vo.setNEW_MSG_YN("Y");
			vo.setDISPLAY(inputVO.getDisplay());
			dam.update(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		this.sendRtnObject(null);
	}
	
	
}