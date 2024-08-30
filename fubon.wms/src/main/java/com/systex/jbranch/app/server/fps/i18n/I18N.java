package com.systex.jbranch.app.server.fps.i18n;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSI18NPK;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSI18NVO;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * I18N
 * 
 * @author moron
 * @date 2015/12/25
 * @spec null
 */
@Component("i18n")
@Scope("request")
public class I18N extends BizLogic {
	private Logger logger = LoggerFactory.getLogger(I18N.class);
	private DataAccessManager dam_obj = null;

	/**
	 * 初始化查詢--查詢角色清單、群組列表
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void initInquire(Object body, IPrimitiveMap header)
			throws JBranchException {
		I18NOutputVO outputVO = new I18NOutputVO();
		dam_obj = this.getDataAccessManager();

		QueryConditionIF condition = dam_obj.getQueryCondition();
		condition.setQueryString("select distinct LOCALE from TBSYSI18N");
		List<Map<String, Object>> locallist = dam_obj.executeQuery(condition);
		outputVO.setLocalList(locallist);
		// condition.setQueryString("select distinct TYPE from TBSYSI18N");
		// List<Map<String, Object>> typelist = dam_obj.executeQuery(condition);
		// outputVO.setTypeList(typelist);

		sendRtnObject(outputVO);
	}

	/**
	 * 對i18n的操作:查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException {
		I18NInputVO I18NInputVO = (I18NInputVO) body;
		I18NOutputVO return_VO = new I18NOutputVO();
		dam_obj = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		String sql_s = "SELECT LOCALE,CODE,TEXT,TYPE,CREATOR,CREATETIME,MODIFIER,LASTUPDATE from TBSYSI18N where 1=1 ";
		if (!StringUtils.isBlank(I18NInputVO.getLocale()))
			sql_s += "and LOCALE = :locale ";
		if (!StringUtils.isBlank(I18NInputVO.getCode()))
			sql_s += "and CODE = :code ";
		if (!StringUtils.isBlank(I18NInputVO.getText()))
			sql_s += "and TEXT like :text ";
		if (!StringUtils.isBlank(I18NInputVO.getType()))
			sql_s += "and TYPE = :type ";
		queryCondition.setQueryString(sql_s);
		if (!StringUtils.isBlank(I18NInputVO.getLocale()))
			queryCondition.setObject("locale", I18NInputVO.getLocale());
		if (!StringUtils.isBlank(I18NInputVO.getCode()))
			queryCondition.setObject("code", I18NInputVO.getCode());
		if (!StringUtils.isBlank(I18NInputVO.getText()))
			queryCondition.setObject("text", "%" + I18NInputVO.getText() + "%");
		if (!StringUtils.isBlank(I18NInputVO.getType()))
			queryCondition.setObject("type", I18NInputVO.getType());

		ResultIF list = dam_obj.executePaging(queryCondition, I18NInputVO
				.getCurrentPageIndex() + 1, I18NInputVO.getPageCount());
		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();

		return_VO.setResultList(list);// 傳遞回Flex端的List
		if (list.size() == 0) {
			throw new APException("ehl_01_common_001");
		}
		return_VO.setCurrentPageIndex(I18NInputVO.getCurrentPageIndex());// 當前頁次
		return_VO.setTotalPage(totalPage_i);// 總頁次
		return_VO.setTotalRecord(totalRecord_i);// 總筆數
		this.sendRtnObject(return_VO);
	}

	/**
	 * 對i18n的操作:查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquireAll(Object body, IPrimitiveMap header)
			throws JBranchException {
		I18NInputVO inputVO = (I18NInputVO) body;
		I18NOutputVO return_VO = new I18NOutputVO();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF condition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		String sql_s = "select CODE,TEXT from TBSYSI18N where 1=1 ";
		if (!StringUtils.isBlank(inputVO.getLocale()))
			sql_s += "and LOCALE = :locale ";
		condition.setQueryString(sql_s);
		if (!StringUtils.isBlank(inputVO.getLocale()))
			condition.setObject("locale", inputVO.getLocale());
		List<Map<String, Object>> locallist = dam_obj.executeQuery(condition);
		return_VO.setResultList(locallist);// 傳遞回Flex端的List
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
		I18NInputVO inputVO = (I18NInputVO) body;
		dam_obj = this.getDataAccessManager();

		TBSYSI18NVO vo = new TBSYSI18NVO();
		TBSYSI18NPK pk = new TBSYSI18NPK();
		pk.setLOCALE(inputVO.getLocale());
		pk.setCODE(inputVO.getCode());
		TBSYSI18NVO queryVO = new TBSYSI18NVO();// 用于判斷是否已經存在相同主鍵
		// 先查找是否已經存在同樣主鍵的數據
		queryVO = (TBSYSI18NVO) dam_obj.findByPKey(
				TBSYSI18NVO.TABLE_UID, pk);
		if (queryVO != null) {
			throw new APException("ehl_01_common_005");
		} else {
			vo.setcomp_id(pk);
			vo.setTEXT(inputVO.getText());
			vo.setTYPE(inputVO.getType());
			dam_obj.create(vo);
		}
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
		I18NInputVO inputVO = (I18NInputVO) body;
		dam_obj = this.getDataAccessManager();

		TBSYSI18NVO vo = new TBSYSI18NVO();
		TBSYSI18NPK pk = new TBSYSI18NPK();
		pk.setLOCALE(inputVO.getLocale());
		pk.setCODE(inputVO.getOriCode());
		vo = (TBSYSI18NVO) dam_obj.findByPKey(
				TBSYSI18NVO.TABLE_UID, pk);
		if (vo != null) {
			if (!inputVO.getOriCode().equals(inputVO.getCode())) {
				TBSYSI18NVO queryVO = new TBSYSI18NVO();// 用于判斷是否已經存在相同主鍵
				TBSYSI18NPK querypk = new TBSYSI18NPK();
				querypk.setLOCALE(inputVO.getLocale());
				querypk.setCODE(inputVO.getCode());
				// 先查找是否已經存在同樣主鍵的數據
				queryVO = (TBSYSI18NVO) dam_obj.findByPKey(
						TBSYSI18NVO.TABLE_UID, querypk);
				if (queryVO != null) {
					throw new APException("ehl_01_common_005");
				} else {
					// del first
					dam_obj.delete(vo);
					// then create
					vo.setcomp_id(querypk);
					vo.setTEXT(inputVO.getText());
					vo.setTYPE(inputVO.getType());
					dam_obj.create(vo);
				}
			}
			vo.setTEXT(inputVO.getText());
			vo.setTYPE(inputVO.getType());
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
		I18NInputVO inputVO = (I18NInputVO) body;
		dam_obj = this.getDataAccessManager();

		TBSYSI18NVO vo = new TBSYSI18NVO();
		TBSYSI18NPK pk = new TBSYSI18NPK();
		pk.setLOCALE(inputVO.getLocale());
		pk.setCODE(inputVO.getCode());
		vo = (TBSYSI18NVO) dam_obj.findByPKey(
				TBSYSI18NVO.TABLE_UID, pk);
		if (vo != null) {
			dam_obj.delete(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		this.sendRtnObject(null);
	}

}