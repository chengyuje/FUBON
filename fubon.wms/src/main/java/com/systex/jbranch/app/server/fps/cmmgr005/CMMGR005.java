package com.systex.jbranch.app.server.fps.cmmgr005;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 批次排程監控
 * 
 * @author 胡海龍
 * @date 2009-9-4
 * @spec 修改註記 
 * 			V0.1	2009/8/31	初版
 * 
 * @author William
 * @date 2016-06-07
 */
@Component("cmmgr005")
@Scope("request")
@SuppressWarnings({"rawtypes"})
public class CMMGR005 extends FubonWmsBizLogic {

	/**
	 * 
	 * 按畫面輸入區輸入的條件查詢資料庫，如果畫面傳入的值為空，這不作為查詢條件
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap<?> header) throws JBranchException {
		CMMGR005InputVO inputVO = (CMMGR005InputVO)body;
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			DataAccessManager dam = this.getDataAccessManager();
			QueryConditionIF condition = dam.getQueryCondition();
			StringBuffer sb = new StringBuffer();
			int index = 1;
			
			sb.append(" select * from TBSYSSCHDADMASTER where ");
			sb.append(" JOBID not in (select PARAM_CODE from TBSYSPARAMETER where PARAM_TYPE = 'CMMGR005.IGNORE_BTH') ");

			if(StringUtils.isNotBlank(inputVO.getAuditid())){
				sb.append(" and AUDITID = ? ");
				condition.setString(index++, inputVO.getAuditid().trim());
			}

			if(StringUtils.isNotBlank(inputVO.getScheduleId())){
				sb.append(" and SCHEDULEID like ? ");
				condition.setString(index++, "%"+inputVO.getScheduleId().trim()+"%");
			}

			if(StringUtils.isNotBlank(inputVO.getJobId())){
				sb.append(" and JOBID like ? ");
				condition.setString(index++, "%"+inputVO.getJobId().trim()+"%");
			}

			if(StringUtils.isNotBlank(inputVO.getType())){
				sb.append(" and TYPE = ? ");
				condition.setString(index++, inputVO.getType().trim());
			}

			if(StringUtils.isNotBlank(inputVO.getStatus())){
				sb.append(" and STATUS = ? ");
				condition.setString(index++, inputVO.getStatus().trim());
			}

			if(StringUtils.isNotBlank(inputVO.getResult())){
				sb.append(" and RESULT = ? ");
				condition.setString(index++, inputVO.getResult().trim());
			}

			if(inputVO.getStartDate()!=null){
				sb.append(" and TO_CHAR(STARTTIME,'yyyy/MM/dd') >= ? ");
				condition.setString(index++, sdf.format(inputVO.getStartDate()));
			}

			if(inputVO.getEndDate()!=null){
				sb.append(" and TO_CHAR(STARTTIME,'yyyy/MM/dd') <= ? ");
				condition.setString(index++, sdf.format(inputVO.getEndDate()));
			}
			sb.append(" order by STARTTIME desc ");
			
			condition.setQueryString(sb.toString());

			ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex()+1, inputVO.getPageCount());
			CMMGR005OutputVO outputVO=new CMMGR005OutputVO();

			int totalPage=list.getTotalPage();
			outputVO.setTotalPage(totalPage);
			outputVO.setQueryList(list);
			outputVO.setTotalRecord(list.getTotalRecord());
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			
			sendRtnObject(outputVO);
			
		} catch (Exception e) {
			logger.debug(e.getMessage(),e); // print Exception to Log
			throw new APException(null); 	// isError:true
		}
		

	}
	/**
	 * 根據AuditId查詢表TBSYSSCHDADDETAIL 得到message 傳到前端
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void messageInquire(Object body, IPrimitiveMap<?> header) throws JBranchException {
		CMMGR005InputVO inputVO=(CMMGR005InputVO)body;
		
		CMMGR005OutputVO outputVO=new CMMGR005OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		try{
			QueryConditionIF condition = dam.getQueryCondition();
			StringBuffer sb = new StringBuffer();
			
			sb.append(" select MESSAGE,CREATETIME ");
			sb.append(" from TBSYSSCHDADDETAIL ");
			sb.append(" where AUDITID = ? and SCHEDULEID = ?");
			sb.append(" order by DETAILID ");
			
			condition.setBigDecimal(1, new BigDecimal(inputVO.getHlbAuditId().trim()));
			condition.setString(2, inputVO.getScheduleId().trim());
			condition.setQueryString(sb.toString());
			
			ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex()+1, inputVO.getPageCount());
			List<Map<String, Object>> message_list=new ArrayList<Map<String, Object>>();
			
			if(list.size()!=0){
				
				for(Map<String, Object> map : (List<Map<String, Object>>) list){
					String message_s=map.get("MESSAGE").toString();
//					message_s = message_s.replaceAll("\n", "");
					
					Map<String, Object> messageMap=new HashMap<String, Object>();
					messageMap.put("MESSAGE", message_s);
					messageMap.put("CREATETIME", map.get("CREATETIME"));
					
					message_list.add(messageMap);
				}
				outputVO.setMessageList(message_list);
				outputVO.setTotalPage(list.getTotalPage());
				outputVO.setQueryList(list);
				outputVO.setTotalRecord(list.getTotalRecord());

				outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			}
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.debug(e.getMessage(),e); // print Exception to Log
			throw new APException(null); 	// isError:true
		}
	}

	/*
	 * 計算日期
	 */
	private Date calcuDate(Date date, int amnt) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + amnt);
		
		return calendar.getTime();
	}
	
	/**
	 * 排程執行時間
	 * @param body
	 * @param header
	 * @throws APException
	 */
	public void inquireExecuteTime(Object body, IPrimitiveMap<?> header) throws APException {
		CMMGR005OutputVO outputVO = new CMMGR005OutputVO();
		CMMGR005BatchInfo info = new CMMGR005BatchInfo();
		
		try {
			info.setSrcList(getInquireExecuteTimeData());
		} catch (Exception e) {
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		outputVO.setAp1List(info.getAp1List());
		outputVO.setAp2List(info.getAp2List());
//		outputVO.setErrList(info.getErrList()); //for the feature if want to do
		this.sendRtnObject(outputVO);
	}
	
	/**取得排程相關資訊*/
	private List getInquireExecuteTimeData() throws Exception {
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("select SCHEDULEID,CRONEXPRESSION, ")
		  //使測試區與正式區的主機統一使用 TPEBNKVIPBAT01T 做邏輯判斷
	      .append("case when SUBSTR( PROCESSOR, 0, VSIZE(PROCESSOR)-1) = 'TPEBNKVIPBAT01' ")
	      .append("then 'TPEBNKVIPBAT01T' else PROCESSOR end PROCESSOR ")
		  .append("from TBSYSSCHD ")
		  .append("where ISUSE='Y' and ISSCHEDULED='Y' ");
		condition.setQueryString(sb.toString());
		return dam.exeQuery(condition);
	}
	
}
