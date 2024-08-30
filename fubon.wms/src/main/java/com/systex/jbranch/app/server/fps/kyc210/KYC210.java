package com.systex.jbranch.app.server.fps.kyc210;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * KYC210
 * 
 * @author Jimmy
 * @date 2016/07/21
 * @spec null
 */
@Component("kyc210")
@Scope("request")
public class KYC210 extends FubonWmsBizLogic{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void queryData(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		KYC210InputVO inputVO = (KYC210InputVO) body;
		KYC210OutputVO outputVO = new KYC210OutputVO();
		try {
			QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" Select distinct b.EXAM_NAME,b.EXAM_VERSION,b. STATUS,to_char(b.ACTIVE_DATE,'YYYY/MM/DD') as ACTIVE_DATE,b.MODIFIER,to_char(b.LASTUPDATE,'YYYY/MM/DD') as LASTUPDATE ");
			sb.append(" from TBSYS_QUESTIONNAIRE b ");
			sb.append(" where quest_type in ('02','03') ");
			if(!"".equals(inputVO.getEXAM_NAME())){
				sb.append(" and b.exam_name like :exam_name ");
				qc.setObject("exam_name", "%"+inputVO.getEXAM_NAME()+"%");
			}
			if(!"".equals(inputVO.getEXAM_VERSION())){
				sb.append(" And b.exam_version = :exam_version ");
				qc.setObject("exam_version", inputVO.getEXAM_VERSION());
			}
			if(inputVO.getLASTUPDATE() != null){
				sb.append(" And trunc(b.lastupdate) = :lastupdate ");
				qc.setObject("lastupdate", inputVO.getLASTUPDATE());
			}
			if(!"".equals(inputVO.getQUEST_TYPE())){
				sb.append(" And b.QUEST_TYPE = :quest_type ");
				qc.setObject("quest_type", inputVO.getQUEST_TYPE());
			}
			if(!"".equals(inputVO.getSTATUS())){
				sb.append(" And b.status = :status ");
				qc.setObject("status", inputVO.getSTATUS());
			}
			sb.append(" order by LASTUPDATE DESC ");
			qc.setQueryString(sb.toString());
			ResultIF list = getDataAccessManager().executePaging(qc, inputVO.getCurrentPageIndex()+1, inputVO.getPageCount());
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord();
			outputVO.setQuestionList(list);
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			outputVO.setTotalPage(totalPage_i);
			outputVO.setTotalRecord(totalRecord_i);
//			outputVO.setQuestionList(getDataAccessManager().exeQuery(qc));
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	//這裡的刪除是指將狀態改成刪除事實上保留資料
	public void deleteData(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		KYC210InputVO inputVO = (KYC210InputVO) body;
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		try {
			QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" update TBSYS_QUESTIONNAIRE set STATUS = '05',MODIFIER = :modifier,LASTUPDATE = sysdate where EXAM_VERSION = :exam_version ");
			qc.setObject("exam_version", inputVO.getDelete_Data());
			qc.setObject("modifier", loginID);
			qc.setQueryString(sb.toString());
			getDataAccessManager().exeUpdate(qc);
			sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

}
