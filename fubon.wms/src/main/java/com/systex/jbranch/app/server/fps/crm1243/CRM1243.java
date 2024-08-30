package com.systex.jbranch.app.server.fps.crm1243;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCAM_CAL_SALES_TASKVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_WKPG_AS_CALENDARVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_WKPG_AS_MASTVO;
import com.systex.jbranch.app.server.fps.crm1242.CRM1242InputVO;
import com.systex.jbranch.app.server.fps.crm1242.CRM1242OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/09/22
 * 
 */
@Component("crm1243")
@Scope("request")
public class CRM1243 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM1243.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM1243InputVO inputVO = (CRM1243InputVO) body;
		CRM1243OutputVO return_VO = new CRM1243OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		//補上原駐點分行名稱:V.BRANCH_NAME html直接將原駐點跟新駐點欄位串成 BAR-NAME
		sql.append(" SELECT ");
		sql.append(" R.SEQ, R.AS_EMP_ID, E.EMP_NAME, E.DEPT_ID, ");
		sql.append(" R.ONSITE_BRH, V.BRANCH_NAME as ONSITE_BRH_NAME, ");
		sql.append(" R.NEW_ONSITE_BRH, T.DEPT_NAME, ");
		sql.append(" R.CHG_REASON, R.CHG_REASON_OTH, R.ONSITE_DATE, ");
		sql.append(" R.ONSITE_PERIOD, R.NEW_ONSITE_DATE, ");
		sql.append(" R.NEW_ONSITE_PERIOD, R.STATUS ");
		
		sql.append(" FROM TBCRM_WKPG_AS_CALENDAR R, ");
		sql.append(" TBORG_MEMBER E, ");
		sql.append(" TBORG_DEFN T, ");
		sql.append(" VWORG_DEFN_INFO V, ");
		sql.append(" TBORG_FAIA F ");
		
		sql.append(" WHERE R.AS_EMP_ID = E.EMP_ID ");
		sql.append(" AND R.NEW_ONSITE_BRH = T.DEPT_ID ");
		sql.append(" AND R.ONSITE_BRH = V.BRANCH_NBR ");
		sql.append(" AND R.STATUS = '2' ");
		sql.append(" AND F.BRANCH_NBR = V.BRANCH_NBR ");
		sql.append(" AND F.EMP_ID = E.EMP_ID ");
		sql.append(" AND F.BRANCH_NBR IN (:br_id) ");
		
		condition.setQueryString(sql.toString());
		condition.setObject("br_id", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		
		List list = dam.exeQuery(condition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void confirm(Object body, IPrimitiveMap header) throws Exception {
		CRM1243InputVO inputVO = (CRM1243InputVO) body;
		dam = this.getDataAccessManager();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		TBCRM_WKPG_AS_CALENDARVO vo = new TBCRM_WKPG_AS_CALENDARVO();
		
		for(Map<String, Object> data : inputVO.getList()){
			
			vo = (TBCRM_WKPG_AS_CALENDARVO) dam.findByPKey(
					TBCRM_WKPG_AS_CALENDARVO.TABLE_UID, data.get("SEQ").toString());
			if (vo != null) {
				vo.setSTATUS("3");
				vo.setNOT_REMIND_YN("Y");
				vo.setONSITE_BRH(data.get("NEW_ONSITE_BRH").toString());
				if(data.get("NEW_ONSITE_DATE") !=null)
					vo.setONSITE_DATE(new Timestamp(sdf.parse(ObjectUtils.toString(data.get("NEW_ONSITE_DATE"))).getTime()));
				vo.setONSITE_PERIOD(data.get("NEW_ONSITE_PERIOD").toString());
				
				vo.setRPL_BRH_MGR((String)getUserVariable(FubonSystemVariableConsts.LOGINID));
				vo.setRPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
				dam.update(vo);
				
				//通知理專、分行業務主管、個金主管、區督導
				QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				
				sql.append("SELECT EMP_ID FROM VWORG_AO_INFO  ");
				sql.append("WHERE BRA_NBR = :bra_nbr ");
				sql.append("UNION ");
				sql.append("SELECT E.EMP_ID ");
				sql.append("FROM TBORG_MEMBER  E,  ");
				sql.append("TBORG_DEFN D,  ");
				sql.append("TBORG_MEMBER_ROLE R  ");
				sql.append("WHERE E.EMP_ID = R.EMP_ID  ");
				sql.append("AND E.DEPT_ID = D.DEPT_ID  ");
				sql.append("AND D.ORG_TYPE = '50'  ");
				sql.append("AND R.ROLE_ID IN (select roleid from TBSYSSECUROLPRIASS where privilegeid = '009')  ");
				sql.append("AND E.DEPT_ID = :bra_nbr ");
				sql.append("UNION ");
				sql.append("SELECT E.EMP_ID ");
				sql.append("FROM TBORG_MEMBER E, ");
				sql.append("TBORG_DEFN D, ");
				sql.append("TBORG_MEMBER_ROLE R ");
				sql.append("WHERE D.ORG_TYPE = '50' ");
				sql.append("AND E.DEPT_ID = D.DEPT_ID ");
				sql.append("AND E.EMP_ID = R.EMP_ID ");
				sql.append("AND R.ROLE_ID IN (select roleid from TBSYSSECUROLPRIASS where privilegeid = '011') ");
				sql.append("AND E.DEPT_ID = :bra_nbr ");
				sql.append("UNION ");
				sql.append("SELECT E.EMP_ID ");
				sql.append("FROM TBORG_MEMBER  E,  ");
				sql.append("VWORG_DEFN_INFO D,  ");
				sql.append("TBORG_MEMBER_ROLE R  ");
				sql.append("WHERE E.EMP_ID = R.EMP_ID ");
				sql.append("AND R.ROLE_ID IN  (select roleid from TBSYSSECUROLPRIASS where privilegeid = '012') ");
				sql.append("AND D.BRANCH_AREA_ID = E.DEPT_ID ");
				sql.append("AND D.BRANCH_NBR = :bra_nbr ");
		
				condition.setQueryString(sql.toString());
				condition.setObject("bra_nbr", data.get("NEW_ONSITE_BRH"));
				
				List<Map<String, String>> empList = dam.exeQuery(condition);
				
				for(Map<String, String> emp : empList){
					QueryConditionIF queryConditions = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer sqls = new StringBuffer();
					sqls.append("SELECT SQ_TBCAM_CAL_SALES_TASK.nextval AS SEQ FROM DUAL ");
					queryConditions.setQueryString(sqls.toString());
					List<Map<String, Object>> SEQLIST = dam.exeQuery(queryConditions);
					BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
					
					TBCAM_CAL_SALES_TASKVO vos = new TBCAM_CAL_SALES_TASKVO();
					vos.setSEQ_NO(seqNo);
					vos.setEMP_ID(emp.get("EMP_ID"));
					vos.setAS_EMP_ID(data.get("AS_EMP_ID").toString());
					
					if(data.get("NEW_ONSITE_DATE") !=null)
						vo.setONSITE_DATE(new Timestamp(sdf.parse(ObjectUtils.toString(data.get("NEW_ONSITE_DATE"))).getTime()));
					
					if ("1".equals(data.get("NEW_ONSITE_PERIOD").toString())) {
						vos.setTASK_STIME("0800");
						vos.setTASK_ETIME("1200");
						
					} else {
						vos.setTASK_STIME("1300");
						vos.setTASK_ETIME("1700");
					}
					vos.setTASK_TITLE("輔銷人員變更駐點行");
					vos.setTASK_MEMO("輔銷人員-" + data.get("AS_EMP_ID").toString() + "_" + data.get("EMP_NAME").toString()
							+ "已變更駐點行至轄下分行" + data.get("NEW_ONSITE_BRH").toString() + "-" + data.get("DEPT_NAME").toString());
					vos.setTASK_SOURCE("4");
					vos.setTASK_DATE(vo.getRPL_DATETIME());
//					vos.setTASK_TYPE(null);
					vos.setTASK_STATUS("I");
					dam.create(vos);
				
				}
				
				
			}else {
				// 顯示資料不存在
				throw new APException("ehl_01_common_017");
			}
			
		}
		
		this.sendRtnObject(null);
	}
	
	public void reject(Object body, IPrimitiveMap header) throws JBranchException {
		CRM1243InputVO inputVO = (CRM1243InputVO) body;
		dam = this.getDataAccessManager();
		
		TBCRM_WKPG_AS_CALENDARVO vo = new TBCRM_WKPG_AS_CALENDARVO();
		
		for(Map<String, Object> data : inputVO.getList()){
			
			vo = (TBCRM_WKPG_AS_CALENDARVO) dam.findByPKey(
					TBCRM_WKPG_AS_CALENDARVO.TABLE_UID, data.get("SEQ").toString());
			if (vo != null) {
				
				vo.setSTATUS("4");
				vo.setNOT_REMIND_YN("Y");
				vo.setRPL_BRH_MGR((String)getUserVariable(FubonSystemVariableConsts.LOGINID));
				vo.setRPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
				dam.update(vo);
			}else {
				// 顯示資料不存在
				throw new APException("ehl_01_common_017");
			}
			
		}
		
		this.sendRtnObject(null);
	}
	
	//輔銷駐點異動提醒
	public void change(Object body, IPrimitiveMap header) throws JBranchException {	
		//查詢
		CRM1243InputVO inputVO = (CRM1243InputVO) body;
		CRM1243OutputVO return_VO = new CRM1243OutputVO();
		dam = this.getDataAccessManager();
		
		//覆核通過
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT * FROM TBCRM_WKPG_AS_CALENDAR ");
		sql.append(" WHERE 1 = 1 ");
		sql.append(" AND AS_EMP_ID = :LoginID ");
		sql.append(" AND NOT_REMIND_YN = 'Y' ");
		sql.append(" AND STATUS = '3'  ");

		condition.setObject("LoginID", inputVO.getLoginID());
		condition.setQueryString(sql.toString());
		List list = dam.exeQuery(condition);
		return_VO.setResultList(list);
		
		//覆核退回
		QueryConditionIF condition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql2 = new StringBuffer();
		sql2.append(" SELECT * FROM TBCRM_WKPG_AS_CALENDAR ");
		sql2.append(" WHERE 1 = 1 ");
		sql2.append(" AND AS_EMP_ID = :LoginID ");
		sql2.append(" AND NOT_REMIND_YN = 'Y' ");
		sql2.append(" AND STATUS = '4'  ");

		condition2.setObject("LoginID", inputVO.getLoginID());
		condition2.setQueryString(sql2.toString());
		List list2 = dam.exeQuery(condition2);
		return_VO.setResultList2(list2);
		
		//更改
		QueryConditionIF condition3 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql3 = new StringBuffer();
		sql3.append(" SELECT * FROM TBCRM_WKPG_AS_CALENDAR ");
		sql3.append(" WHERE 1 = 1 ");
		sql3.append(" AND AS_EMP_ID = :LoginID ");
		sql3.append(" AND NOT_REMIND_YN = 'Y' ");
		sql3.append(" AND STATUS IN ('3','4')  ");
		condition3.setObject("LoginID", inputVO.getLoginID());
		condition3.setQueryString(sql3.toString());
		
		List<Map<String,Object>> updatelist = dam.exeQuery(condition3);
		for(Map<String,Object> map:updatelist){
			TBCRM_WKPG_AS_CALENDARVO vo = new TBCRM_WKPG_AS_CALENDARVO();
			vo = (TBCRM_WKPG_AS_CALENDARVO) dam.findByPKey(TBCRM_WKPG_AS_CALENDARVO.TABLE_UID, map.get("SEQ").toString());
			if (vo != null) {
				vo.setNOT_REMIND_YN("N");
				dam.update(vo);
			}	
		}
		
		this.sendRtnObject(return_VO);
	}
	
	/**產生seq No */
	private String getSEQ() throws JBranchException {
		  SerialNumberUtil seq = new SerialNumberUtil();
		  String seqNum = "";
		  try{
		    seqNum = seq.getNextSerialNumber("CRM121");
		  }
		  catch(Exception e){
		   seq.createNewSerial("CRM121", "0000000000", null, null, null, 6, new Long("99999999"), "y", new Long("0"), null);
		   seqNum = seq.getNextSerialNumber("CRM121");
		   }
		  return seqNum;
	}
	
}