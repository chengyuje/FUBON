package com.systex.jbranch.app.server.fps.crm122;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCAM_CAL_SALES_TASKVO;
import com.systex.jbranch.app.common.fps.table.TBPMS_SALES_PLANPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_SALES_PLANVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;



/**
 * @author Stella
 * @date 2016/09/20
 * Mark: 2017/01/18 業務排程寫在CRM121 Stella
 */
@Component("crm122")
@Scope("request")



public class CRM122 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM122.class);
	private List<Map<String,Object>> myAOList =null;
	
	
	
	public void getMyAOs (Object body, IPrimitiveMap header) throws JBranchException {

		CRM122OutputVO return_vo = new CRM122OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT AO_CODE, EMP_ID, EMP_NAME ");
		sql.append(" FROM VWORG_BRANCH_EMP_DETAIL_INFO ");
		sql.append(" WHERE BRANCH_NBR IN ( :br_id ) AND AO_CODE IS NOT NULL ");
		sql.append(" ORDER BY AO_CODE ");
		
		queryCondition.setQueryString(ObjectUtils.toString(sql));
		queryCondition.setObject("br_id", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		myAOList = dam.exeQuery(queryCondition);
		
		return_vo.setMyAOsList(myAOList);
		this.sendRtnObject(return_vo);
		
	}
	
	
	
	
	
	public void getTodo (Object body, IPrimitiveMap header) throws JBranchException {

		CRM122InputVO inputVO = (CRM122InputVO) body;
		CRM122OutputVO return_vo = new CRM122OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
		StringBuffer sql = new StringBuffer();
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> ARMGR = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);    //區域主管
		Map<String, String> MBRMGR = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);  //營運督導
		Map<String, String> BMMGR = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);    //分行個金	
		

//		// A/M/C
		sql.append(" WITH BASE AS ( ");
		sql.append(" SELECT  CUST_ID, CUST_NAME, TITLE, STATUS, DATETIME, STIME, ETIME	,	0 SEQ_NO	,	''	TASK_MEMO	,	''	TASK_SOURCE	,	''	TASK_TYPE	");
		sql.append(" FROM	(	");
//		// --A日期
		sql.append(" SELECT CUST_ID, CUST_NAME, EST_PRD AS TITLE, (CASE WHEN  TRUNC(ACTION_DATE) = TRUNC(ACTION_DATE) THEN 'A'  END) AS STATUS, TRUNC(ACTION_DATE) AS DATETIME, '0800' AS STIME, '0900'AS ETIME	,	");
		sql.append("	0 SEQ_NO	,	''	TASK_MEMO	,	''	TASK_SOURCE	,	''	TASK_TYPE	");
		sql.append(" FROM TBPMS_SALES_PLAN ");
		if(ObjectUtils.equals(inputVO.getEMP_ID(), "") || StringUtils.isEmpty(inputVO.getEMP_ID())){
			sql.append(" WHERE EMP_ID IN (:EMP) ");
			queryCondition.setObject("EMP", inputVO.getAOsList()); 
		}else{
			sql.append(" WHERE EMP_ID = :EMP ");
			queryCondition.setObject("EMP", inputVO.getEMP_ID()); 
		}
		sql.append(" UNION ALL ");
//		// --M日期
		sql.append(" SELECT  CUST_ID, CUST_NAME, EST_PRD AS TITLE, (CASE WHEN  TRUNC(MEETING_DATE_S) BETWEEN TRUNC(MEETING_DATE_S)  AND TRUNC(MEETING_DATE_E)  THEN 'M' END) AS STATUS, TRUNC(MEETING_DATE_S) AS DATETIME, TO_CHAR(MEETING_DATE_S,'hh24mm') AS STIME , TO_CHAR(MEETING_DATE_E,'hh24mm') AS ETIME	,	");
		sql.append("	0 SEQ_NO	,	''	TASK_MEMO	,	''	TASK_SOURCE	,	''	TASK_TYPE	");
		sql.append(" FROM TBPMS_SALES_PLAN  ");
		if(ObjectUtils.equals(inputVO.getEMP_ID(), "") || StringUtils.isEmpty(inputVO.getEMP_ID())){
			sql.append(" WHERE EMP_ID IN (:EMP) ");
			queryCondition.setObject("EMP", inputVO.getAOsList()); 
		}else{
			sql.append(" WHERE EMP_ID = :EMP ");
			queryCondition.setObject("EMP", inputVO.getEMP_ID()); 
		}
		sql.append(" UNION ALL  ");
//		// --C日期
		sql.append(" SELECT  CUST_ID, CUST_NAME, EST_PRD AS TITLE, (CASE WHEN TRUNC(CLOSE_DATE) = TRUNC(CLOSE_DATE) THEN 'C' END) AS STATUS, TRUNC(CLOSE_DATE) AS DATETIME, '0800' AS STIME, '0900'AS ETIME	,	");
		sql.append("	0 SEQ_NO	,	''	TASK_MEMO	,	''	TASK_SOURCE	,	''	TASK_TYPE	");
		sql.append(" FROM TBPMS_SALES_PLAN ");
		sql.append(" WHERE  EMP_ID IN (:EMP) ) ) ");
		/**待辦事項已處理狀態C與銷售計劃C衝突，行事曆日期上的點不需要特別區分待辦事項是否處理，因此全部以I作為待辦事項**/
		sql.append(" SELECT TASK.SEQ_NO, TASK.CUST_ID, CUST.CUST_NAME, TASK.TASK_TITLE AS TITLE, 'I' AS STATUS, TRUNC(TASK.TASK_DATE) AS DATETIME, ");
		sql.append(" TASK.TASK_STIME AS STIME, TASK.TASK_ETIME AS ETIME, TASK.TASK_MEMO, TASK.TASK_SOURCE, TASK.TASK_TYPE  ");
		sql.append(" FROM TBCAM_CAL_SALES_TASK TASK ");
		sql.append(" LEFT JOIN TBCRM_CUST_MAST CUST ON CUST.CUST_ID = TASK.CUST_ID  ");
		
		if(ObjectUtils.equals(inputVO.getEMP_ID(), "") || StringUtils.isEmpty(inputVO.getEMP_ID())){
			sql.append(" WHERE TASK.EMP_ID IN (:EMP) ");
			queryCondition.setObject("EMP", inputVO.getAOsList()); 
		}else{
			sql.append(" WHERE TASK.EMP_ID = :EMP ");
			queryCondition.setObject("EMP", inputVO.getEMP_ID()); 
		}
		/**===========================2017/02/20===========================**
		 * 需求 : 輔銷駐點不在待辦事項出現，但陪訪放行必須在待辦事項
		 * 問題 : 輔銷駐點跟陪訪放行都是自建4，無法用TASK_SOURCE做判斷
		 * 方法 : 輔銷駐點變更覆核的TITLE是由程式生成而非使用者自定義，因此直接判斷TITLE
		 * **/
		sql.append(" AND TASK.TASK_TITLE != '輔銷人員變更駐點行' ");
		/**================================================================**/
				
		sql.append(" UNION ALL ");
		sql.append(" SELECT  0 SEQ_NO	,	CUST_ID, CUST_NAME, TITLE, STATUS, DATETIME, STIME, ETIME	,	''	TASK_MEMO, ''	TASK_SOURCE, ''	TASK_TYPE	");
		sql.append(" FROM BASE ");
		
		queryCondition.setQueryString(sql.toString());
		
		List<Map<String,Object>> list = dam.exeQuery(queryCondition);
		
		return_vo.setResultList(list);
		this.sendRtnObject(return_vo);			
		
	}
	
	//待辦事項
		public void getTodoDtl (Object body, IPrimitiveMap header) throws JBranchException {

			CRM122InputVO inputVO = (CRM122InputVO) body;
			CRM122OutputVO return_vo = new CRM122OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			StringBuffer sql = new StringBuffer();
			
			sql.append(" SELECT TASK.SEQ_NO AS SEQ_NO, TASK.CUST_ID, CUST.CUST_NAME, TASK.TASK_TITLE AS TITLE, TASK.TASK_STATUS AS STATUS, TRUNC(TASK.TASK_DATE) AS DATETIME, TASK.TASK_STIME AS STIME, TASK.TASK_ETIME AS ETIME, ");
			sql.append(" TASK.TASK_MEMO AS TASK_MEMO, TASK.TASK_SOURCE AS TASK_SOURCE, TASK.TASK_TYPE AS TASK_TYPE, TASK.EMP_ID, INFO.EMP_NAME, INFO.AO_CODE ");
			sql.append(" FROM TBCAM_CAL_SALES_TASK TASK ");
			sql.append(" LEFT JOIN (SELECT EMP_ID, EMP_NAME,AO_CODE FROM  VWORG_BRANCH_EMP_DETAIL_INFO ) INFO ON INFO.EMP_ID = TASK.EMP_ID ");
			sql.append(" LEFT JOIN TBCRM_CUST_MAST CUST ON CUST.CUST_ID = TASK.CUST_ID ");
			if(ObjectUtils.equals(inputVO.getEMP_ID(), "") || StringUtils.isEmpty(inputVO.getEMP_ID())){
				sql.append(" WHERE TASK.EMP_ID IN ( :emplist ) ");
				queryCondition.setObject("emplist", inputVO.getAOsList()); 
			}else{
				sql.append(" WHERE TASK.EMP_ID = :emplist  ");
				queryCondition.setObject("emplist", inputVO.getEMP_ID()); 
			}
			/**===========================2017/02/20===========================**
			 * 需求 : 輔銷駐點不在待辦事項出現，但陪訪放行必須在待辦事項
			 * 問題 : 輔銷駐點跟陪訪放行都是自建4，無法用TASK_SOURCE做判斷
			 * 方法 : 輔銷駐點變更覆核的TITLE是由程式生成而非使用者自定義，因此直接判斷TITLE
			 * **/
			sql.append(" AND TASK.TASK_TITLE != '輔銷人員變更駐點行' ");
			/**================================================================**/
			sql.append(" AND TO_CHAR(TASK.TASK_DATE,'YYYY/MM/DD') = TO_CHAR(:date_o ,'YYYY/MM/DD')");
			
			queryCondition.setQueryString(ObjectUtils.toString(sql));
			queryCondition.setObject("date_o",new Timestamp(inputVO.getDate().getTime()));
			List<Map<String,Object>> list = dam.exeQuery(queryCondition);
			System.out.println(" ============== " + inputVO.getEMP_ID());
			System.out.println(" ============== " + new Timestamp(inputVO.getDate().getTime()));
			return_vo.setMyTodoLst(list);
			this.sendRtnObject(return_vo);		
		}
	
//		//amc
		public void getAMC (Object body, IPrimitiveMap header) throws JBranchException {

			CRM122InputVO inputVO = (CRM122InputVO) body;
			CRM122OutputVO return_vo = new CRM122OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			StringBuffer sql = new StringBuffer();
			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> ARMGR = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);    //區域主管
			Map<String, String> MBRMGR = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);  //營運督導
			Map<String, String> BMMGR = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);    //分行個金	
			
			sql.append(" SELECT B.SEQ, B.CUST_ID, B.CUST_NAME, B.STATUS, B.EST_PRD, B.EST_AMT, B.EST_EARNINGS, B.MEETING_DATE_S, B.MEETING_DATE_E, B.ACTION_DATE, B.CLOSE_DATE, B.EMP_ID, B.EMP_NAME, B.AO_CODE ");
			sql.append(" FROM( ");
//			//A
			sql.append(" SELECT SEQ, EMP_ID, EMP_NAME, AO_CODE, CUST_ID, CUST_NAME, MEETING_DATE_S, MEETING_DATE_E,(CASE WHEN TRUNC(ACTION_DATE) = TRUNC(:date_o ) THEN 'A' ");
			sql.append(" END) AS STATUS, ");
			sql.append(" EST_PRD, EST_AMT, EST_EARNINGS, ACTION_DATE, CLOSE_DATE, null AS FAIA_STATUS ");
			sql.append(" FROM TBPMS_SALES_PLAN ");
			if(ObjectUtils.equals(inputVO.getEMP_ID(), "") || StringUtils.isEmpty(inputVO.getEMP_ID())){
				sql.append(" WHERE EMP_ID IN (:emplist) ");
				queryCondition.setObject("emplist", inputVO.getAOsList()); 
			}else{
				sql.append(" WHERE EMP_ID = :emplist ");
				queryCondition.setObject("emplist", inputVO.getEMP_ID()); 
			}
			sql.append(" AND TO_CHAR(ACTION_DATE,'YYYY/MM/DD') = TO_CHAR(:date_o ,'YYYY/MM/DD') ");
			sql.append(" UNION ALL  ");
//			//M
			sql.append(" SELECT P.SEQ, P.EMP_ID, P.EMP_NAME, P.AO_CODE, P.CUST_ID, P.CUST_NAME, P.MEETING_DATE_S, P.MEETING_DATE_E, ");
			sql.append(" (CASE WHEN TRUNC(:date_o ) BETWEEN TRUNC(P.MEETING_DATE_S) ");
			sql.append(" AND TRUNC(P.MEETING_DATE_E)  THEN 'M' END) AS STATUS, ");
			sql.append(" P.EST_PRD, EST_AMT, P.EST_EARNINGS, P.ACTION_DATE, P.CLOSE_DATE, M.STATUS AS FAIA_STATUS ");
			sql.append(" FROM TBPMS_SALES_PLAN P ");
			sql.append(" LEFT JOIN TBCRM_WKPG_AS_MAST M ON  P.SEQ = M.SALES_PLAN_SEQ AND P.CUST_ID = M.CUST_ID ");
			if(ObjectUtils.equals(inputVO.getEMP_ID(), "") || StringUtils.isEmpty(inputVO.getEMP_ID())){
				sql.append(" WHERE P.EMP_ID IN (:emplist) ");
				queryCondition.setObject("emplist", inputVO.getAOsList()); 
			}else{
				sql.append(" WHERE P.EMP_ID = :emplist ");
				queryCondition.setObject("emplist", inputVO.getEMP_ID()); 
			}
			sql.append(" AND TO_CHAR(:date_o ,'YYYY/MM/DD') BETWEEN TO_CHAR(P.MEETING_DATE_S,'YYYY/MM/DD')  AND TO_CHAR(P.MEETING_DATE_E,'YYYY/MM/DD') ");
			sql.append(" UNION ALL  ");
//			//C
			sql.append(" SELECT SEQ, EMP_ID, EMP_NAME, AO_CODE, CUST_ID, CUST_NAME, MEETING_DATE_S, MEETING_DATE_E,(CASE ");
			sql.append(" WHEN TO_CHAR(CLOSE_DATE,'YYYY/MM/DD') = TO_CHAR(:date_o ,'YYYY/MM/DD') THEN 'C' END) AS STATUS, ");
			sql.append(" EST_PRD, EST_AMT, EST_EARNINGS, ACTION_DATE, CLOSE_DATE, null AS FAIA_STATUS ");
			sql.append(" FROM TBPMS_SALES_PLAN ");
			if(ObjectUtils.equals(inputVO.getEMP_ID(), "") || StringUtils.isEmpty(inputVO.getEMP_ID())){
				sql.append(" WHERE EMP_ID IN (:emplist) ");
				queryCondition.setObject("emplist", inputVO.getAOsList()); 
			}else{
				sql.append(" WHERE EMP_ID = :emplist ");
				queryCondition.setObject("emplist", inputVO.getEMP_ID()); 
			}
			sql.append(" AND TO_CHAR(CLOSE_DATE,'YYYY/MM/DD') = TO_CHAR(:date_o ,'YYYY/MM/DD')  ");
			sql.append(" )B ");
			
			sql.append("ORDER BY CASE WHEN B.STATUS = 'A' THEN 0 WHEN B.STATUS = 'M' THEN 1 WHEN B.STATUS = 'C' THEN 2 END ASC  ");
			
			queryCondition.setQueryString(ObjectUtils.toString(sql));
			queryCondition.setObject("date_o",new Timestamp(inputVO.getDate().getTime()));
			List<Map<String,Object>> list = dam.exeQuery(queryCondition);
			
			return_vo.setMyAUMLst(list);
			this.sendRtnObject(return_vo);
		}
		

		public void delTack (Object body, IPrimitiveMap header) throws JBranchException {
		
			CRM122InputVO inputVO = (CRM122InputVO) body;
			dam = this.getDataAccessManager();

			if(inputVO.getChkCodedata().size()>0){
				for (int i =0; i<inputVO.getChkCodedata().size(); i++){
					TBCAM_CAL_SALES_TASKVO vo = new TBCAM_CAL_SALES_TASKVO();
					BigDecimal seqNo = new BigDecimal(inputVO.getChkCodedata().get(i).get("SEQ_NO").toString());
					vo = (TBCAM_CAL_SALES_TASKVO) dam.findByPKey(TBCAM_CAL_SALES_TASKVO.TABLE_UID,seqNo);
					if(vo!=null){
						dam.delete(vo);
					}
				}
			}
			if(inputVO.getChkCode_1data().size()>0){
				for(int j=0; j<inputVO.getChkCode_1data().size(); j++){
					for(int k=0; k<inputVO.getChkCode_1data().size(); k++){
						TBPMS_SALES_PLANPK PK = new TBPMS_SALES_PLANPK();
						TBPMS_SALES_PLANVO vo1 = new TBPMS_SALES_PLANVO();
						BigDecimal seqNo = new BigDecimal(inputVO.getChkCode_1data().get(j).get("SEQ").toString());
						PK.setCUST_ID(inputVO.getChkCode_1data().get(k).get("CUST_ID").toString());
						PK.setSEQ(seqNo);
						vo1 = (TBPMS_SALES_PLANVO) dam.findByPKey(TBPMS_SALES_PLANVO.TABLE_UID, PK);
						if(vo1!=null){
							dam.delete(vo1);
						}
					}
				}
			}
			this.sendRtnObject(null);
		}

}
