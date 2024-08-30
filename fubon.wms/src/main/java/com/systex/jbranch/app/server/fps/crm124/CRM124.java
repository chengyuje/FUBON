package com.systex.jbranch.app.server.fps.crm124;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.birt.report.model.api.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCAM_CAL_SALES_TASKVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_WKPG_AS_CALENDARVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_WKPG_AS_MASTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author moron,  walalala
 * @date 2016/04/20, 2016/09/12
 * @spec null
 */
@Component("crm124")
@Scope("request")
public class CRM124 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM124.class);
	String FaiaType = "";
	String RoleID = "";
	public void initial(Object body, IPrimitiveMap header) throws JBranchException {
		CRM124InputVO inputVO = (CRM124InputVO) body;
		CRM124OutputVO return_VO = new CRM124OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		// get 前後和今月
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(inputVO.getDate());
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		calendar.add(Calendar.MONTH, -1);
		Date before = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.add(Calendar.MONTH, 2);
		Date next = calendar.getTime();
//===================================================================================================================	
		RoleID = getUserVariable(FubonSystemVariableConsts.LOGINROLE).toString();

		/**判斷角色權限:
		 * 		FaiaType 1 = 輔銷科長
		 * 		FaiaType 2 = 輔銷人員
		 * 		FaiaType 3 = 其他人員
		 */
		if (inputVO.getFaia_type() == null){
			if ("FA9".equals(RoleID) || "IA9".equals(RoleID)){
				FaiaType = "1";
			}else if("FA".equals(RoleID) || "IA".equals(RoleID)){
				FaiaType = "2";
			}else{
				FaiaType = "3";
			}
		}else{
			FaiaType = inputVO.getFaia_type();
		}
		
		//Type 1 科長查詢所有輔銷人員
		if(FaiaType.equals("1")){
			sql.append(" SELECT '1' AS TYPE , SEQ, AS_EMP_ID, ONSITE_DATE , (SELECT EMP_NAME ");
			sql.append(" FROM TBORG_MEMBER WHERE AS_EMP_ID = EMP_ID)|| '-' ||(SELECT DEPT_NAME FROM TBORG_DEFN ");
			sql.append(" WHERE ONSITE_BRH = DEPT_ID)|| (CASE WHEN T.STATUS = '2' THEN '(修改待覆核)' ELSE '' END) ");
			sql.append(" AS TITLE, null AS STATUS, T.ONSITE_PERIOD AS START_TIME, T.ONSITE_PERIOD AS END_TIME, ");
			sql.append(" T.ONSITE_BRH AS ONSITE_BRH, R.ROLE_ID, R.EMP_ID ");
			sql.append(" FROM TBCRM_WKPG_AS_CALENDAR T,TBORG_MEMBER_ROLE R ");
			sql.append(" WHERE 1=1 ");
			sql.append(" AND EMP_ID = R.EMP_ID ");
			//身分FA
			if ("FA9".equals(RoleID)){
				sql.append("AND R.ROLE_ID = 'FA'");		
			//身分IA
			}else if("IA9".equals(RoleID)){
				sql.append("AND R.ROLE_ID = 'IA' ");
			}
			sql.append(" AND T.STATUS = '2' ");
			sql.append(" AND TO_CHAR(ONSITE_DATE,'YYYY/MM') BETWEEN TO_CHAR(:start,'YYYY/MM') AND TO_CHAR(:end,'YYYY/MM') ");
			sql.append(" UNION ALL ");
			sql.append(" SELECT '2' AS TYPE, TO_CHAR(SEQ_NO) AS SEQ, AS_EMP_ID, TASK_DATE, TASK_TITLE AS TITLE, '' AS STATUS, ");
			sql.append(" TASK_STIME AS START_TIME, TASK_ETIME AS END_TIME, ");
			sql.append(" null AS ONSITE_BRH, ");
			//身分FA
			if ("FA9".equals(RoleID)){
				sql.append(" 'FA' AS ROLE_ID, ");		
			//身分IA
			}else if("IA9".equals(RoleID)){
				sql.append(" 'IA' AS ROLE_ID, ");
			}else{
				sql.append(" null AS ROLE_ID, ");
			}
			sql.append(" EMP_ID  ");
			sql.append(" FROM TBCAM_CAL_SALES_TASK ");
			sql.append(" WHERE EMP_ID = :emp_id ");
			sql.append(" AND TASK_SOURCE !='4' "); //TASK_SOURCE 提醒類別為4的是變更駐點分行提醒，FA本身不需接收到
			sql.append(" AND TO_CHAR(TASK_DATE,'YYYY/MM') BETWEEN TO_CHAR(:start,'YYYY/MM') AND TO_CHAR(:end,'YYYY/MM') ");
			sql.append(" UNION ALL ");
			sql.append(" SELECT '3' AS TYPE, M.SEQ, M.AS_EMP_ID, M.AS_DATE, (M.AS_EMP_ID || '-' || T.EMP_NAME || '陪同 ' || A.EMP_NAME || ' 拜訪' || C.CUST_NAME) AS TITLE, M.STATUS AS STATUS, ");
			sql.append(" REPLACE(M.AS_DATETIME_BGN,':','') AS START_TIME, REPLACE(M.AS_DATETIME_END,':','') AS END_TIME,  ");
			sql.append(" null AS ONSITE_BRH, R.ROLE_ID, R.EMP_ID ");
			sql.append(" FROM TBCRM_WKPG_AS_MAST M, VWORG_AO_INFO A , VWCRM_CUST_INFO C, TBORG_MEMBER_ROLE R, TBORG_MEMBER T ");
			sql.append(" WHERE 1=1 ");
			sql.append(" AND M.AS_EMP_ID = R.EMP_ID ");
			sql.append(" AND M.AS_EMP_ID = T.EMP_ID ");
			sql.append(" AND A.AO_CODE = M.AO_CODE  ");
			sql.append(" AND C.CUST_ID = M.CUST_ID  ");
			sql.append(" AND M.STATUS in ('1A', '2A', '3A', '4A') ");
			sql.append(" AND M.COMPLETE_YN = 'Y' ");
			sql.append(" AND TO_CHAR(AS_DATE,'YYYY/MM') BETWEEN TO_CHAR(:start,'YYYY/MM') AND TO_CHAR(:end,'YYYY/MM') ");
			//身分FA
			if ("FA9".equals(RoleID)){
				sql.append("AND R.ROLE_ID = 'FA'");		
			//身分IA
			}else if("IA9".equals(RoleID)){
				sql.append("AND R.ROLE_ID = 'IA' ");
			}
			condition.setObject("emp_id", getUserVariable(FubonSystemVariableConsts.LOGINID).toString());
		//Type 2 (1)科長查詢特定輔銷人員  (2)輔銷人員行事曆
		}else if(FaiaType.equals("2")){
			condition.setObject("emp_id", inputVO.getEmp_id());
			sql.append(" SELECT '1' AS TYPE , SEQ, AS_EMP_ID, ONSITE_DATE , (SELECT EMP_NAME ");
			sql.append(" FROM TBORG_MEMBER WHERE AS_EMP_ID = EMP_ID)|| '-' ||(SELECT DEPT_NAME FROM TBORG_DEFN ");
			sql.append(" WHERE ONSITE_BRH = DEPT_ID) || (CASE WHEN STATUS = '2' THEN '(修改待覆核)' ELSE '' END) ");
			sql.append(" AS TITLE, STATUS, ONSITE_PERIOD AS START_TIME, ONSITE_PERIOD AS END_TIME,  ");
			sql.append(" ONSITE_BRH ");
			sql.append(" FROM TBCRM_WKPG_AS_CALENDAR ");
			sql.append(" WHERE AS_EMP_ID = :emp_id ");
			sql.append(" AND TO_CHAR(ONSITE_DATE,'YYYY/MM') BETWEEN TO_CHAR(:start,'YYYY/MM') AND TO_CHAR(:end,'YYYY/MM') ");
			//(1)輔銷科長只顯示變更駐點行與陪訪紀錄
			if ("FA9".equals(RoleID) || "IA9".equals(RoleID)){
				sql.append(" AND STATUS = '2' ");
				sql.append(" UNION ALL ");
				sql.append(" SELECT '3' AS TYPE, M.SEQ, M.AS_EMP_ID, M.AS_DATE, ('陪同 ' || A.EMP_NAME || ' 拜訪' || C.CUST_NAME) AS TITLE, M.STATUS AS STATUS, ");
				sql.append(" REPLACE(M.AS_DATETIME_BGN,':','') AS START_TIME, REPLACE(M.AS_DATETIME_END,':','') AS END_TIME,  ");
				sql.append(" null AS ONSITE_BRH  ");
				sql.append(" FROM TBCRM_WKPG_AS_MAST M, VWORG_AO_INFO A , VWCRM_CUST_INFO C ");
				sql.append(" WHERE M.AS_EMP_ID = :emp_id  ");
				sql.append(" AND A.AO_CODE = M.AO_CODE  ");
				sql.append(" AND C.CUST_ID = M.CUST_ID  ");
				sql.append(" AND M.STATUS in ('1A', '2A', '3A', '4A') ");
				sql.append(" AND M.COMPLETE_YN = 'Y' ");
				sql.append(" AND TO_CHAR(AS_DATE,'YYYY/MM') BETWEEN TO_CHAR(:start,'YYYY/MM') AND TO_CHAR(:end,'YYYY/MM') ");
			//(2)輔銷人員全部顯示
			}else{			
				sql.append(" UNION ALL ");
				sql.append(" SELECT '2' AS TYPE, TO_CHAR(SEQ_NO) AS SEQ, AS_EMP_ID, TASK_DATE, TASK_TITLE AS TITLE, '' AS STATUS, ");
				sql.append(" TASK_STIME AS START_TIME, TASK_ETIME AS END_TIME, ");
				sql.append(" null AS ONSITE_BRH  ");
				sql.append(" FROM TBCAM_CAL_SALES_TASK ");
				
				/**輔銷陪訪會議記錄 待辦事項行事曆與輔銷駐點行事曆合而為一:AS_EMP_ID代表提醒 EMP_ID代表待辦事項**/
//				sql.append(" WHERE AS_EMP_ID = :emp_id ");
				sql.append(" WHERE (AS_EMP_ID = :emp_id OR EMP_ID = :emp_id) ");
				
				sql.append(" AND TASK_SOURCE !='4' "); //TASK_SOURCE 提醒類別為4的是變更駐點分行提醒，FA本身不需接收到
				sql.append(" AND TO_CHAR(TASK_DATE,'YYYY/MM') BETWEEN TO_CHAR(:start,'YYYY/MM') AND TO_CHAR(:end,'YYYY/MM') ");
				sql.append(" UNION ALL ");
				sql.append(" SELECT '3' AS TYPE, M.SEQ, M.AS_EMP_ID, M.AS_DATE, ('陪同 ' || A.EMP_NAME || ' 拜訪' || C.CUST_NAME) AS TITLE, M.STATUS AS STATUS, ");
				sql.append(" REPLACE(M.AS_DATETIME_BGN,':','') AS START_TIME, REPLACE(M.AS_DATETIME_END,':','') AS END_TIME,  ");
				sql.append(" null AS ONSITE_BRH  ");
				sql.append(" FROM TBCRM_WKPG_AS_MAST M, VWORG_AO_INFO A , VWCRM_CUST_INFO C ");
				sql.append(" WHERE M.AS_EMP_ID = :emp_id  ");
				sql.append(" AND A.AO_CODE = M.AO_CODE  ");
				sql.append(" AND C.CUST_ID = M.CUST_ID  ");
				sql.append(" AND M.STATUS in ('1A', '2A', '3A', '4A','4C') ");
				sql.append(" AND TO_CHAR(AS_DATE,'YYYY/MM') BETWEEN TO_CHAR(:start,'YYYY/MM') AND TO_CHAR(:end,'YYYY/MM') ");
			}
		//理專人員
		}else{
			sql.append(" SELECT C.SEQ, C.STATUS, C.AS_EMP_ID, I.EMP_NAME, C.ONSITE_DATE AS DATETIME, ");
			sql.append(" CASE C.ONSITE_PERIOD WHEN '1' THEN '0900' WHEN '2' THEN '1300' END AS STIME, ");
			sql.append(" CASE C.ONSITE_PERIOD WHEN '1' THEN '1200' WHEN '2' THEN '1700' END AS ETIME ");
			sql.append(" FROM TBCRM_WKPG_AS_CALENDAR C ");
			sql.append(" LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO I ON I.EMP_ID = C.AS_EMP_ID ");
			sql.append(" WHERE 1 = 1 ");
			sql.append(" and TO_CHAR(C.ONSITE_DATE,'YYYY/MM') BETWEEN TO_CHAR(:start,'YYYY/MM') AND TO_CHAR(:end,'YYYY/MM') ");
			sql.append(" and C.ONSITE_BRH IN (:bra_nbr) ");
			
			condition.setObject("bra_nbr", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		inputVO.setFaia_type(FaiaType);
		condition.setQueryString(sql.toString());				
		condition.setObject("start", before);
		condition.setObject("end", next);
		List<Map<String, Object>> list = dam.exeQuery(condition);
		
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
//=====================================================================================================================				
	public void emp_inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM124OutputVO return_VO = new CRM124OutputVO();
		dam = this.getDataAccessManager();
		RoleID = getUserVariable(FubonSystemVariableConsts.LOGINROLE).toString();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TMR.EMP_ID , TM.EMP_NAME , TMR.ROLE_ID ");
		sql.append("FROM TBORG_MEMBER_ROLE TMR ,TBORG_MEMBER TM ");
		sql.append("WHERE TMR.EMP_ID = TM.EMP_ID ");
		if ("FA9".equals(RoleID)){
			sql.append("AND TMR.ROLE_ID='FA' ");
		}
		else if ("IA9".equals(RoleID)){
			sql.append("AND TMR.ROLE_ID='IA' ");
		}		
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(condition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);		
	}
	
	//理專:檢視輔銷駐點
	public void ao_code_Query(Object body, IPrimitiveMap header) throws JBranchException {
		CRM124InputVO inputVO = (CRM124InputVO) body;
		CRM124OutputVO return_VO = new CRM124OutputVO();
		dam = this.getDataAccessManager();
		RoleID = getUserVariable(FubonSystemVariableConsts.LOGINROLE).toString();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT C.SEQ, C.STATUS, C.AS_EMP_ID, I.EMP_NAME, C.ONSITE_DATE AS DATETIME, I.ROLE_NAME, ");
		sql.append(" CASE C.ONSITE_PERIOD WHEN '1' THEN '0900' WHEN '2' THEN '1300' END AS STIME, ");
		sql.append(" CASE C.ONSITE_PERIOD WHEN '1' THEN '1200' WHEN '2' THEN '1700' END AS ETIME, ");
		sql.append(" C.ONSITE_BRH, C.NEW_ONSITE_BRH, V.BRANCH_NAME AS NEW_BRA, TO_CHAR(C.NEW_ONSITE_DATE,'YYYY/MM/DD') AS NEW_ONSITE_DATE, ");
		sql.append(" CASE C.NEW_ONSITE_PERIOD WHEN '1' THEN '上午' WHEN '2' THEN '下午' END AS NEW_ONSITE_PERIOD ");
		sql.append(" FROM TBCRM_WKPG_AS_CALENDAR C ");
		sql.append(" LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO I ON I.EMP_ID = C.AS_EMP_ID ");
		sql.append(" LEFT JOIN VWORG_DEFN_INFO V ON C.NEW_ONSITE_BRH = V.BRANCH_NBR ");
		sql.append(" WHERE 1 = 1 ");
		sql.append(" and TO_CHAR(C.ONSITE_DATE,'YYYY/MM/DD') = TO_CHAR(:date_o ,'YYYY/MM/DD') ");
		sql.append(" and C.ONSITE_BRH IN (:bra_nbr) ");
		
		condition.setObject("date_o", new Timestamp(inputVO.getDate().getTime()));
		condition.setObject("bra_nbr", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(condition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);		
	}
	
	
	//輔銷駐點:初始
	public void initial_onsite(Object body, IPrimitiveMap header) throws JBranchException {
		CRM124InputVO inputVO = (CRM124InputVO) body;
		CRM124OutputVO return_VO = new CRM124OutputVO();
		dam = this.getDataAccessManager();
		RoleID = getUserVariable(FubonSystemVariableConsts.LOGINROLE).toString();
						
		/**判斷角色權限:
		 * 		FaiaType 1 = 輔銷科長
		 * 		FaiaType 2 = 輔銷人員
		 */
		if (inputVO.getFaia_type() == null){
			if ("FA9".equals(RoleID) || "IA9".equals(RoleID)){
				FaiaType = "1";
			}else if("FA".equals(RoleID) || "IA".equals(RoleID)){
				FaiaType = "2";
			}
		}else{
			FaiaType = inputVO.getFaia_type();
		}
		
		//===================================ResultList===================================
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT a.*, b.EMP_NAME, c.ROLE_ID ");
		sql.append(" FROM TBCRM_WKPG_AS_CALENDAR a ");
		sql.append(" left join TBORG_MEMBER b on a.AS_EMP_ID = b.EMP_ID ");
		sql.append(" left join TBORG_MEMBER_ROLE c on a.AS_EMP_ID = c.EMP_ID ");
		sql.append(" WHERE 1 = 1 ");
		/**輔銷行事曆會議記錄 編輯駐點時給予SEQ再次查詢**/
		if(!StringUtils.isEmpty(inputVO.getSeq())){
			sql.append(" and a.SEQ = :seq ");
			condition.setObject("seq", inputVO.getSeq());
		}else{		
			// get 前後和今月
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(inputVO.getDate());
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
			Date before = calendar.getTime();
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date next = calendar.getTime();			
			/**輔銷行事曆會議記錄 輔銷科長覆核變更駐點分行**/
			if(FaiaType.equals("1")){
				if ("FA9".equals(RoleID)){
					sql.append("AND c.ROLE_ID = 'FA' ");
					sql.append("AND a.STATUS = '2' ");
				}
				else if ("IA9".equals(RoleID)){
					sql.append("AND c.ROLE_ID = 'IA' ");
					sql.append("AND a.STATUS = '2' ");
				}
			}else{
				sql.append(" and a.AS_EMP_ID = :emp_id ");
				condition.setObject("emp_id", inputVO.getEmp_id());
			}
			sql.append(" and a.ONSITE_DATE between :start and :end ");
			condition.setObject("start", before);
			condition.setObject("end", next);
		}	
		
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(condition);
		return_VO.setResultList(list);
		
		//===================================BraList===================================
		QueryConditionIF condition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql2 = new StringBuffer();
		
		sql2.append(" SELECT BRANCH_NBR, BRANCH_NAME FROM VWORG_DEFN_INFO ");	
		
		condition2.setQueryString(sql2.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(condition2);
		return_VO.setBraList(list2);
		
		this.sendRtnObject(return_VO);
	}
	
	//輔銷駐點:新增
	public void add_onsite(Object body, IPrimitiveMap header) throws JBranchException {
		CRM124InputVO inputVO = (CRM124InputVO) body;
		dam = this.getDataAccessManager();
		
		//上午
		for(Map<String,Object> data : inputVO.getDateList()){
			TBCRM_WKPG_AS_CALENDARVO vo = new TBCRM_WKPG_AS_CALENDARVO();
			if (StringUtils.isBlank(data.get("seq_a").toString())) {
				if (!StringUtils.isBlank(data.get("bnr_id_a").toString())) {
					vo.setSEQ(getSN());
					vo.setAS_EMP_ID(inputVO.getEmp_id());
					vo.setONSITE_BRH(data.get("bnr_id_a").toString());
					
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(inputVO.getDate());
					calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
					calendar.set(Calendar.DAY_OF_MONTH, ((Double)(data.get("date"))).intValue());
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
					
					vo.setONSITE_DATE(new Timestamp(calendar.getTime().getTime()));
					vo.setONSITE_PERIOD(data.get("period_a").toString());
					vo.setSTATUS("1");
					dam.create(vo);
				}				
			} 
		}
		
		//下午
		for(Map<String,Object> data : inputVO.getDateList()){
			TBCRM_WKPG_AS_CALENDARVO vo = new TBCRM_WKPG_AS_CALENDARVO();
			if (StringUtils.isBlank(data.get("seq_b").toString())) {
				if (!StringUtils.isBlank(data.get("bnr_id_b").toString())) {
					vo.setSEQ(getSN());
					vo.setAS_EMP_ID(inputVO.getEmp_id());
					vo.setONSITE_BRH(data.get("bnr_id_b").toString());
					
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(inputVO.getDate());
					calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
					calendar.set(Calendar.DAY_OF_MONTH, ((Double)(data.get("date"))).intValue());				
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
					
					vo.setONSITE_DATE(new Timestamp(calendar.getTime().getTime()));
					vo.setONSITE_PERIOD(data.get("period_b").toString());
					vo.setSTATUS("1");
					dam.create(vo);
				}			
			} 
		}
		this.sendRtnObject(null);
		
	}
	//輔銷駐點:編輯
	public void edit_onsite(Object body, IPrimitiveMap header) throws JBranchException {
		CRM124InputVO inputVO = (CRM124InputVO) body;
		dam = this.getDataAccessManager();
		
		TBCRM_WKPG_AS_CALENDARVO vo = new TBCRM_WKPG_AS_CALENDARVO();
		vo = (TBCRM_WKPG_AS_CALENDARVO) dam.findByPKey(
				TBCRM_WKPG_AS_CALENDARVO.TABLE_UID, inputVO.getSeq());
		if (vo != null) {
//			vo.setONSITE_BRH(inputVO.getOnsite_brh());
			vo.setNEW_ONSITE_BRH(inputVO.getNew_onsite_brh());
//			vo.setONSITE_DATE(new Timestamp(inputVO.getOn_date().getTime()));
			vo.setNEW_ONSITE_DATE(new Timestamp(inputVO.getNew_on_date().getTime()));
			if(StringUtils.equals(inputVO.getNew_onsite_period(), "1")) {
				vo.setNEW_ONSITE_PERIOD("1");
			} else {
				vo.setNEW_ONSITE_PERIOD("2");
			}
			vo.setCHG_REASON(inputVO.getChg_reason());
			if (!StringUtils.isBlank(inputVO.getChg_reason_oth()))
				vo.setCHG_REASON_OTH(inputVO.getChg_reason_oth());
			vo.setSTATUS("2");
			dam.update(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_017");
		}
		this.sendRtnObject(null);
	}
	
	public void aoinquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM124InputVO inputVO = (CRM124InputVO) body;
		CRM124OutputVO return_VO = new CRM124OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT EMP_ID, AO_CODE, EMP_NAME FROM VWORG_AO_INFO WHERE 1 = 1 ");
		sql.append("AND BRA_NBR IN ( :emp_id ) ");
		sql.append("AND AO_CODE IS NOT NULL ORDER BY AO_CODE ");
		
		condition.setQueryString(sql.toString());
		condition.setObject("emp_id", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		
		List list = dam.exeQuery(condition);
		return_VO.setAoList(list);
		this.sendRtnObject(return_VO);
		
	}

	public void inquire_salesplan(Object body, IPrimitiveMap header) throws JBranchException {
		CRM124InputVO inputVO = (CRM124InputVO) body;
		CRM124OutputVO return_VO = new CRM124OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT P.SEQ, P.MEETING_DATE_S, P.MEETING_DATE_E, ");
		sql.append(" P.PLAN_YEARMON || '-' || T.PARAM_NAME AS TITLE  ");
		sql.append(" FROM TBPMS_SALES_PLAN P ");
		sql.append(" LEFT JOIN TBCRM_WKPG_AS_MAST M ON M.SALES_PLAN_SEQ = P.SEQ ");
		sql.append(" LEFT JOIN TBSYSPARAMETER T ON P.EST_PRD = T.PARAM_CODE ");
		sql.append(" WHERE 1 = 1 ");
		sql.append(" AND M.SEQ IS NULL ");
		sql.append(" AND T.PARAM_TYPE = 'PMS.SALE_PLAN_PRD_TYPE' ");
		sql.append(" AND P.AO_CODE = :ao_code ");
		sql.append(" AND P.CUST_ID = :cust_id ");
		
		condition.setQueryString(sql.toString());
		condition.setObject("ao_code", inputVO.getTask_ao_code());
		condition.setObject("cust_id", inputVO.getTask_cust_id());
		
		List list = dam.exeQuery(condition);
		return_VO.setSalesplanList(list);
		this.sendRtnObject(return_VO);
		
	}
	
	//輔銷提醒:新增
	public void add_remind(Object body, IPrimitiveMap header) throws JBranchException {
		CRM124InputVO inputVO = (CRM124InputVO) body;
		dam = this.getDataAccessManager();
		
		// seq
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBCAM_CAL_SALES_TASK.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		
		TBCAM_CAL_SALES_TASKVO vo = new TBCAM_CAL_SALES_TASKVO();
		vo.setSEQ_NO(seqNo);
		vo.setEMP_ID(inputVO.getTask_emp_id());
		vo.setAS_EMP_ID((String)getUserVariable(FubonSystemVariableConsts.LOGINID));
		if (StringUtil.isEqual(inputVO.getTask_source(), "A2") || StringUtil.isEqual(inputVO.getTask_source(), "A5")) {
			vo.setCUST_ID(inputVO.getTask_cust_id());
		}
		vo.setTASK_DATE(new Timestamp (inputVO.getTask_date().getTime()));
		vo.setTASK_STIME(inputVO.getTask_stime());
		vo.setTASK_ETIME(inputVO.getTask_etime());
		vo.setTASK_TITLE(inputVO.getTask_title());
		vo.setTASK_MEMO(inputVO.getTask_memo());
		vo.setTASK_SOURCE(inputVO.getTask_source());
//		vo.setTASK_TYPE(null);
		vo.setTASK_STATUS("I");
		dam.create(vo);
				
		//輔銷自建陪訪
		if (StringUtil.isEqual(inputVO.getTask_source(), "A2")) {
			TBCRM_WKPG_AS_MASTVO vos = new TBCRM_WKPG_AS_MASTVO();
			vos.setSEQ(getWKPG_SN());
			vos.setSALES_TASK_SEQ(seqNo);
			vos.setCUST_ID(inputVO.getTask_cust_id());
			vos.setAO_CODE(inputVO.getTask_ao_code());
			vos.setNOT_REMIND_YN("Y");
			vos.setAS_EMP_ID((String)getUserVariable(FubonSystemVariableConsts.LOGINID));
			if(inputVO.getSalesplan_seq() != null && !"".equals(inputVO.getSalesplan_seq())){
				vos.setSALES_PLAN_SEQ(getBigDecimal(inputVO.getSalesplan_seq()));
			}
			//判斷FA or IA
			if (StringUtil.isEqual((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE), "FA")){
				vos.setAS_TYPE("F");
			} else if (StringUtil.isEqual((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE), "IA")){
				vos.setAS_TYPE("I");
			}
			
			if (!StringUtil.isBlank(inputVO.getSalesplan_seq())) {
				vos.setSALES_PLAN_SEQ(getBigDecimal(inputVO.getSalesplan_seq()));
			}
			vos.setAS_DATE(new Timestamp (inputVO.getTask_date().getTime()));
			vos.setAS_DATETIME_BGN(inputVO.getTask_stime().substring(0, 2) + ':' + inputVO.getTask_stime().substring(2, inputVO.getTask_stime().length()));
			vos.setAS_DATETIME_END(inputVO.getTask_etime().substring(0, 2) + ':' + inputVO.getTask_etime().substring(2, inputVO.getTask_etime().length()));
			
			if (!StringUtil.isBlank(inputVO.getVisit_purpose())) {
				vos.setVISIT_PURPOSE(inputVO.getVisit_purpose());
			} else {
				vos.setVISIT_PURPOSE("A2");
			}
			
			if (!StringUtil.isBlank(inputVO.getVisit_purpose()))
				vos.setVISIT_PURPOSE_OTHER(inputVO.getVisit_purpose());
			
			if (!StringUtil.isBlank(inputVO.getVisit_purpose()))
				vos.setKEY_ISSUE(inputVO.getKey_issue());
			
			vos.setSTATUS("4A");
			dam.create(vos);
		}
		
		this.sendRtnObject(null);
	}
	
	//輔銷提醒:編輯
	public void edit_remind_inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM124InputVO inputVO = (CRM124InputVO) body;
		CRM124OutputVO return_VO = new CRM124OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		//輔銷行事曆會議記錄 更改
		if(!StringUtils.isBlank(inputVO.getSeq())){
			sql.append("SELECT * FROM TBCAM_CAL_SALES_TASK WHERE SEQ_NO = :seq ");
			condition.setObject("seq", inputVO.getSeq());
		}else{
			sql.append("SELECT * FROM TBCAM_CAL_SALES_TASK WHERE TO_CHAR(TASK_DATE,'YYYY/MM/DD') = TO_CHAR(:task_date,'YYYY/MM/DD') AND AS_EMP_ID = :emp_id ");
			condition.setObject("task_date", inputVO.getTask_date());
			condition.setObject("emp_id", inputVO.getEmp_id());
		}
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(condition);
		
		return_VO.setEdit_remind_List(list);
		this.sendRtnObject(return_VO);
	}
	
	
	public void edit_remind(Object body, IPrimitiveMap header) throws JBranchException {
		CRM124InputVO inputVO = (CRM124InputVO) body;
		dam = this.getDataAccessManager();
		
		TBCAM_CAL_SALES_TASKVO vo = new TBCAM_CAL_SALES_TASKVO();
		
		vo = (TBCAM_CAL_SALES_TASKVO) dam.findByPKey(
				TBCAM_CAL_SALES_TASKVO.TABLE_UID, getBigDecimal(inputVO.getSeq()));
		
		if (vo != null) {
			
			if (StringUtil.isEqual(inputVO.getTask_source(), "A2")) {

				vo.setTASK_DATE(new Timestamp(inputVO.getTask_date().getTime()));
				vo.setTASK_STIME(inputVO.getTask_stime());
				vo.setTASK_ETIME(inputVO.getTask_etime());
				dam.update(vo);
								
				QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT SEQ FROM TBCRM_WKPG_AS_MAST WHERE SALES_TASK_SEQ = :seq ");
				condition.setQueryString(sql.toString());
				condition.setObject("seq", inputVO.getSeq());
				
				List<Map<String, Object>> list = dam.exeQuery(condition);
				
				if (list.size() > 0 ) {
					
					TBCRM_WKPG_AS_MASTVO vos = new TBCRM_WKPG_AS_MASTVO();
					
					vos = (TBCRM_WKPG_AS_MASTVO) dam.findByPKey(
							TBCRM_WKPG_AS_MASTVO.TABLE_UID, list.get(0).get("SEQ").toString());
					
					if (vos != null) {
						
						if (!StringUtil.isBlank(inputVO.getSalesplan_seq())) {
							vos.setSALES_PLAN_SEQ(getBigDecimal(inputVO.getSalesplan_seq()));
						}
						
						vos.setAS_DATE(new Timestamp(inputVO.getTask_date().getTime()));
						vos.setAS_DATETIME_BGN(inputVO.getTask_stime().substring(0, 2) + ':' + inputVO.getTask_stime().substring(2, inputVO.getTask_stime().length()));
						vos.setAS_DATETIME_END(inputVO.getTask_etime().substring(0, 2) + ':' + inputVO.getTask_etime().substring(2, inputVO.getTask_etime().length()));
						
						if (!StringUtil.isBlank(inputVO.getVisit_purpose()))
							vos.setVISIT_PURPOSE(inputVO.getVisit_purpose());
						
						if (!StringUtil.isBlank(inputVO.getVisit_purpose()))
							vos.setVISIT_PURPOSE_OTHER(inputVO.getVisit_purpose());
						
						if (!StringUtil.isBlank(inputVO.getVisit_purpose()))
							vos.setKEY_ISSUE(inputVO.getKey_issue());
						
						dam.update(vos);
						
					}else {
						// 顯示資料不存在
						throw new APException("ehl_01_common_017");
					}
				} else {
					// 顯示資料不存在
					throw new APException("ehl_01_common_017");
				}

			} else {

				vo.setEMP_ID(inputVO.getTask_emp_id());
				vo.setTASK_DATE(new Timestamp (inputVO.getTask_date().getTime()));
				vo.setTASK_STIME(inputVO.getTask_stime());
				vo.setTASK_ETIME(inputVO.getTask_etime());
				vo.setTASK_TITLE(inputVO.getTask_title());
				vo.setTASK_MEMO(inputVO.getTask_memo());
				dam.update(vo);
			}

		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_017");
		}

		this.sendRtnObject(null);
	}
	
	//輔銷提醒:刪除
	public void delete_remind(Object body, IPrimitiveMap header) throws JBranchException {
		CRM124InputVO inputVO = (CRM124InputVO) body;
		dam = this.getDataAccessManager();
		
		TBCAM_CAL_SALES_TASKVO vo = new TBCAM_CAL_SALES_TASKVO();
		vo = (TBCAM_CAL_SALES_TASKVO) dam.findByPKey(
				TBCAM_CAL_SALES_TASKVO.TABLE_UID, getBigDecimal(inputVO.getSeq()));
		
		if (vo != null) {
			dam.delete(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_017");
		}
		
		if (StringUtil.isEqual(inputVO.getTask_source(), "A2")) {
			
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT SEQ FROM TBCRM_WKPG_AS_MAST WHERE SALES_TASK_SEQ = :seq ");
			condition.setQueryString(sql.toString());
			condition.setObject("seq", inputVO.getSeq());
			
			List<Map<String, Object>> list = dam.exeQuery(condition);
			
			
			TBCRM_WKPG_AS_MASTVO vos = new TBCRM_WKPG_AS_MASTVO();
			
			vos = (TBCRM_WKPG_AS_MASTVO) dam.findByPKey(
					TBCRM_WKPG_AS_MASTVO.TABLE_UID, list.get(0).get("SEQ").toString());
			
			//狀態修改 : 1A、2A、3A、4A ==> 4C
			if (vos != null) {				
				vos.setSTATUS("4C");
				vos.setNOT_REMIND_YN("Y");
				vos.setBRH_MGR((String)getUserVariable(FubonSystemVariableConsts.LOGINID));
				vos.setBRH_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
				dam.update(vos);
				this.sendRtnObject(null);
				
			}else {
				// 顯示資料不存在
				throw new APException("ehl_01_common_017");
			}			
		}
		
		this.sendRtnObject(null);
	}
	
	//輔銷陪訪:刪除
	public void delete_visit(Object body, IPrimitiveMap header) throws JBranchException {
		CRM124InputVO inputVO = (CRM124InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SEQ FROM TBCRM_WKPG_AS_MAST WHERE SALES_TASK_SEQ = :seq ");
		condition.setQueryString(sql.toString());
		condition.setObject("seq", inputVO.getSeq());
		
		List<Map<String, Object>> list = dam.exeQuery(condition);				
		TBCRM_WKPG_AS_MASTVO vos = new TBCRM_WKPG_AS_MASTVO();
		
		vos = (TBCRM_WKPG_AS_MASTVO) dam.findByPKey(
				TBCRM_WKPG_AS_MASTVO.TABLE_UID, inputVO.getSeq().toString());
		
		//狀態修改 : 1A、2A、3A、4A ==> 4C
		if (vos != null) {				
			vos.setSTATUS("4C");
			vos.setNOT_REMIND_YN("Y");
			vos.setBRH_MGR((String)getUserVariable(FubonSystemVariableConsts.LOGINID));
			vos.setBRH_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
			dam.update(vos);
			this.sendRtnObject(null);
			
		}else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_017");
		}
	}
	
	
	public void holiday(Object body, IPrimitiveMap header) throws JBranchException {
		CRM124InputVO inputVO = (CRM124InputVO) body;
		CRM124OutputVO return_VO = new CRM124OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT(HOL_DATE) FROM TBBTH_HOLIDAY WHERE VERSION = '0' ");
		sql.append("AND to_char(HOL_DATE, 'YYYY') = :year AND to_char(HOL_DATE, 'MM') = :month  ");
		condition.setQueryString(sql.toString());
		condition.setObject("year", inputVO.getYear());
		condition.setObject("month", inputVO.getMonth());
		
		List<Map<String, Object>> list = dam.exeQuery(condition);
		return_VO.setHolidayList(list);
		this.sendRtnObject(return_VO);
	}
		
	//SEQ序號產生:TBCRM_WKPG_AS_CALENDAR
	private String getSN() throws JBranchException {
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		try{
			seqNum = sn.getNextSerialNumber("TBCRM_WKPG_AS_CALENDAR");
		}
		catch(Exception e){
			sn.createNewSerial("TBCRM_WKPG_AS_CALENDAR", "00000", null, null, null, 1, new Long("99999"), "y", new Long("0"), null);
			seqNum = sn.getNextSerialNumber("TBCRM_WKPG_AS_CALENDAR");
		}
		return seqNum;
	}
	
	//SEQ序號產生:TBCRM_WKPG_AS_MAST
	private String getWKPG_SN() throws JBranchException {
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		try{
			seqNum = sn.getNextSerialNumber("TBCRM_WKPG_AS_MAST");
		}
		catch(Exception e){
			sn.createNewSerial("TBCRM_WKPG_AS_MAST", "00000", null, null, null, 1, new Long("99999"), "y", new Long("0"), null);
			seqNum = sn.getNextSerialNumber("TBCRM_WKPG_AS_MAST");
		}
		return seqNum;
	}
	
	//轉Decimal
	public BigDecimal getBigDecimal( Object value ) {
        BigDecimal ret = null;
        if( value != null ) {
            if( value instanceof BigDecimal ) {
                ret = (BigDecimal) value;
            } else if( value instanceof String ) {
                ret = new BigDecimal( (String) value );
            } else if( value instanceof BigInteger ) {
                ret = new BigDecimal( (BigInteger) value );
            } else if( value instanceof Number ) {
                ret = new BigDecimal( ((Number)value).doubleValue() );
            } else {
                throw new ClassCastException("Not possible to coerce ["+value+"] from class "+value.getClass()+" into a BigDecimal.");
            }
        }
        return ret;
    }
	
	
}