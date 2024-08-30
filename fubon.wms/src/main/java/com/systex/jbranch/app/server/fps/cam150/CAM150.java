package com.systex.jbranch.app.server.fps.cam150;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LEADS_IMPVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.CAM997;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Ocean
 * @date 2016/05/19
 * 
 */
@Component("cam150")
@Scope("request")
public class CAM150 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CAM150.class);
	SimpleDateFormat SDFYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM150InputVO inputVO = (CAM150InputVO) body;
		CAM150OutputVO outputVO = new CAM150OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT imp.SEQNO, imp.CAMPAIGN_ID, imp.CAMPAIGN_NAME, imp.CAMPAIGN_DESC, imp.CREATOR, ");
		sb.append("imp.START_DATE, imp.END_DATE, imp.FIRST_CHANNEL, imp.SECOND_CHANNEL, imp.LE_TOTAL_CNT, imp.CHECK_STATUS, main.DOC_NAME, det.FILE_NAME, imp.IMP_STATUS ");
		sb.append("FROM TBCAM_SFA_LEADS_IMP imp ");
		sb.append("left join TBSYS_FILE_MAIN main on imp.FILE_SEQ = main.DOC_ID ");
		sb.append("left join TBSYS_FILE_DETAIL det on main.DOC_ID = det.DOC_ID ");
		sb.append("WHERE 1=1 ");
		sb.append("AND imp.CHECK_STATUS <> '00' ");
		
		if (!StringUtils.isBlank(inputVO.getCampID())) {
			sb.append("AND imp.CAMPAIGN_ID like :campID "); // 行銷活動代碼
			queryCondition.setObject("campID", "%" + inputVO.getCampID() + "%");
		}	
		
		if (!StringUtils.isBlank(inputVO.getCampName())) {
			sb.append("AND imp.CAMPAIGN_NAME like :campName "); // 行銷活動名稱
			queryCondition.setObject("campName", "%" + inputVO.getCampName() + "%");
		}	
		
		if (null != inputVO.getsDate() && null != inputVO.getsDate2()) {// 活動起始日-起迄 
			sb.append("AND TO_CHAR(imp.START_DATE, 'yyyyMMdd') >= TO_CHAR(:sDate, 'yyyyMMdd') AND TO_CHAR(imp.START_DATE, 'yyyyMMdd') <= TO_CHAR(:sDate2, 'yyyyMMdd') ");
			queryCondition.setObject("sDate", inputVO.getsDate());
			queryCondition.setObject("sDate2", inputVO.getsDate2());
		}
		
		if ((null != inputVO.getsDate() && null == inputVO.getsDate2()) || 
			(null == inputVO.getsDate() && null != inputVO.getsDate2())) {// 活動起始日 
			sb.append("AND TO_CHAR(imp.START_DATE, 'yyyyMMdd') >= TO_CHAR(:sDate, 'yyyyMMdd') ");
			queryCondition.setObject("sDate", (null != inputVO.getsDate() && null == inputVO.getsDate2()) ? inputVO.getsDate() : inputVO.getsDate2());
		}
	
		if (null != inputVO.geteDate() && null != inputVO.geteDate2()) {// 活動終止日-起迄
			sb.append("AND TO_CHAR(imp.END_DATE, 'yyyyMMdd') >= TO_CHAR(:eDate, 'yyyyMMdd') AND TO_CHAR(imp.END_DATE, 'yyyyMMdd') <= TO_CHAR(:eDate2, 'yyyyMMdd') ");
			queryCondition.setObject("eDate", inputVO.geteDate());
			queryCondition.setObject("eDate2", inputVO.geteDate2());
		}
		
		if ((null != inputVO.geteDate() && null == inputVO.geteDate2()) || 
			(null == inputVO.geteDate() && null != inputVO.geteDate2())) {// 活動終止日 
			sb.append("AND TO_CHAR(imp.END_DATE, 'yyyyMMdd') >= TO_CHAR(:eDate, 'yyyyMMdd') ");
			queryCondition.setObject("eDate", (null != inputVO.geteDate() && null == inputVO.geteDate2()) ? inputVO.geteDate() : inputVO.geteDate2());
		}
		
		if (!StringUtils.isBlank(inputVO.getChannel())) {//使用部隊
			sb.append("AND imp.FIRST_CHANNEL like :channel "); 
			queryCondition.setObject("channel", inputVO.getChannel());
		}
		
		if (!StringUtils.isBlank(inputVO.getSource_id())) {//名單來源
			sb.append("AND imp.LEAD_SOURCE_ID = :sourceID ");
			queryCondition.setObject("sourceID", inputVO.getSource_id());
		}
		
		if (!StringUtils.isBlank(inputVO.getCheckStatus())) { // 狀態
			sb.append("AND imp.CHECK_STATUS = :checkStatus ");
			queryCondition.setObject("checkStatus", inputVO.getCheckStatus());
		}
		
		if (!StringUtils.isBlank(inputVO.getDocName())) {
			sb.append("AND main.DOC_NAME like :docName "); // 文件名稱
			queryCondition.setObject("docName", "%" + inputVO.getDocName() + "%");
		}	
		
		queryCondition.setQueryString(sb.toString());
		
		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage(); // 分頁用
		int totalRecord_i = list.getTotalRecord(); // 分頁用
		outputVO.setResultList(list); // data
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數
		
		this.sendRtnObject(outputVO);
	}
	
	public void updCheckStatus(Object body, IPrimitiveMap header) throws JBranchException {
		CAM150InputVO inputVO = (CAM150InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		TBCAM_SFA_LEADS_IMPVO vo = new TBCAM_SFA_LEADS_IMPVO();
		vo = (TBCAM_SFA_LEADS_IMPVO) dam.findByPKey(TBCAM_SFA_LEADS_IMPVO.TABLE_UID, inputVO.getSeqNo());

		if (null != vo) {
			vo.setCHECK_STATUS(inputVO.getCheckStatus());
			// 2018/5/18 mantis 4932
			if ("02".equals(inputVO.getCheckStatus()) || "03".equals(inputVO.getCheckStatus())) {
				vo.setREL_EMP_ID((String)getUserVariable(FubonSystemVariableConsts.LOGINID));
				vo.setREL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
			}
			dam.update(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		
		this.sendRtnObject(null);
	}
	
	public void getSpreadsheets(Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM150InputVO inputVO = (CAM150InputVO) body;
		CAM150OutputVO outputVO = new CAM150OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		TBCAM_SFA_LEADS_IMPVO vo = new TBCAM_SFA_LEADS_IMPVO();
		vo = (TBCAM_SFA_LEADS_IMPVO) dam.findByPKey(TBCAM_SFA_LEADS_IMPVO.TABLE_UID, inputVO.getSeqNo());
		
		if (null != vo) {
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT CUST_ID, BRANCH_ID, AO_CODE, START_DATE, END_DATE ");
			sb.append("FROM TBCAM_SFA_LE_IMP_TEMP ");
			sb.append("WHERE IMP_SEQNO = :seqNo ");
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("seqNo", inputVO.getSeqNo());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			
			CAM997 cam997 = new CAM997();
			Map<String, Integer> assignDtl = cam997.getAssignDtl(dam, list, SDFYYYYMMDD.format(vo.getSTART_DATE()), SDFYYYYMMDD.format(vo.getEND_DATE()));
			
			outputVO.setTotalNum(assignDtl.get("totalNum"));
			outputVO.setSuccessNum(assignDtl.get("successNum"));
			outputVO.setFailureNum(assignDtl.get("failureNum"));
		}
		
		this.sendRtnObject(outputVO);
	}
	
}