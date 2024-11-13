package com.systex.jbranch.app.server.fps.cam120;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_PARAMETERVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author 1500617
 * @date 27/4/2016
 * 
 */
@Component("cam120")
@Scope("request")
public class CAM120 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CAM120.class);
	
	/**
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException {
		CAM120InputVO inputVO = (CAM120InputVO) body;
		CAM120OutputVO outputVO = new CAM120OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
//		sql.append("select * from TBCAM_SFA_PARAMETER where 1=1 ");
		sql.append("SELECT SFA_PARA_ID, CAMPAIGN_ID, CAMPAIGN_NAME, CAMPAIGN_DESC, CAMP_PURPOSE, ");
		sql.append("       LEAD_SOURCE_ID, LEAD_RESPONSE_CODE, LEAD_TYPE, LEAD_PARA1, LEAD_PARA2, EXAM_ID, SALES_PITCH, FIRST_CHANNEL, SECOND_CHANNEL, START_DT, END_DT, GIFT_CAMPAIGN_ID, ");
		sql.append("       CREATETIME, CREATOR, MODIFIER, LASTUPDATE "); //, ");
//		sql.append("       CASE WHEN CREATOR = :loginID THEN 'Y' WHEN (SELECT REPLACE(ROL.SYS_ROLE, '_ROLE', '') AS LOGIN_ROLE FROM TBORG_ROLE ROL WHERE ROLE_ID = :loginRole) = 'HEADMGR' THEN 'Y' ELSE 'N' END AS MODIFY_YN ");
		sql.append("FROM TBCAM_SFA_PARAMETER ");
		sql.append("WHERE 1 = 1 ");
		// 行銷活動代碼
		if (!StringUtils.isBlank(inputVO.getCampId())) {
			sql.append("and CAMPAIGN_ID like :campId "); 
			condition.setObject("campId", "%" + inputVO.getCampId() + "%");
		}
		// 行銷活動名稱
		if (!StringUtils.isBlank(inputVO.getCampName())) {
			sql.append("and CAMPAIGN_NAME like :campName "); 
			condition.setObject("campName", "%" + inputVO.getCampName() + "%");
		}
		sql.append("order by SFA_PARA_ID ");
		condition.setQueryString(sql.toString());
		
		ResultIF list = dam.executePaging(condition, inputVO
				.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage(); // 分頁用
		int totalRecord_i = list.getTotalRecord(); // 分頁用
		outputVO.setResultList(list); // data
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數
		this.sendRtnObject(outputVO);
	}
	
	/**
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void ddlModify(Object body, IPrimitiveMap header) throws JBranchException {
		CAM120InputVO inputVO = (CAM120InputVO) body;
		dam = this.getDataAccessManager();

		TBCAM_SFA_PARAMETERVO vo = new TBCAM_SFA_PARAMETERVO();
		vo = (TBCAM_SFA_PARAMETERVO) dam.findByPKey(
				TBCAM_SFA_PARAMETERVO.TABLE_UID, inputVO.getId());
		if (vo != null) {
			dam.delete(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		this.sendRtnObject(null);
	}

}
