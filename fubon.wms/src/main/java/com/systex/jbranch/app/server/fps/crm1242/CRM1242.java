package com.systex.jbranch.app.server.fps.crm1242;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.text.SimpleDateFormat;
import com.systex.jbranch.app.common.fps.table.TBCAM_CAL_SALES_TASKVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_WKPG_AS_MASTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author walalala
 * @date 2016/09/23
 * @spec null
 */
@Component("crm1242")
@Scope("request")
public class CRM1242 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM1242.class);
	
	//查詢
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM1242InputVO inputVO = (CRM1242InputVO) body;
		CRM1242OutputVO return_VO = new CRM1242OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT  ");
		sql.append(" R1.SEQ, R1.SALES_PLAN_SEQ, C.ONSITE_BRH, DI.BRANCH_NAME AS ONSITE_NAME, E1.EMP_NAME, CI.CUST_NAME AS CUST_NAME_1, A1.BRANCH_NAME,  ");
		sql.append(" R1.AS_EMP_ID, R1.AS_TYPE, R1.CUST_ID AS CUST_ID_1, R1.AO_CODE AS AO_CODE_1, A1.EMP_NAME AS AO_EMP_NAME_1,  ");    
		sql.append(" A1.BRA_NBR AS AO_BRA_NBR_1, R1.AS_DATE,R1.AS_DATETIME_BGN AS AS_DATETIME_BGN_1, R1.AS_DATETIME_END AS AS_DATETIME_END_1,  ");  
		sql.append(" '' AS AS_EMP_ID_2, '' AS CUST_ID_2, '' AS CUST_NAME_2, '' AS AO_CODE_2, '' AS AO_EMP_NAME_2,  ");    
		sql.append(" '' AS AO_BRA_NBR_2, '' AS AS_DATETIME_BGN_2, '' AS AS_DATETIME_END_2  ");    
		sql.append(" from TBCRM_WKPG_AS_MAST R1,  ");  
		sql.append(" TBCRM_WKPG_AS_CALENDAR C,  ");    
		sql.append(" VWCRM_CUST_INFO CI,  ");  
		sql.append(" TBORG_MEMBER E1,  ");    
		sql.append(" VWORG_AO_INFO A1,  ");
		sql.append(" VWORG_DEFN_INFO DI  ");
		sql.append(" WHERE 1=1  ");    
		sql.append(" AND DI.BRANCH_NBR = C.ONSITE_BRH  ");
		sql.append(" AND TO_CHAR(C.ONSITE_DATE, 'YYYY/MM/DD') = TO_CHAR(R1.AS_DATE, 'YYYY/MM/DD') "); 	
		sql.append(" AND C.AS_EMP_ID = R1.AS_EMP_ID  ");    
		sql.append(" AND (  ");    
		sql.append(" (C.ONSITE_PERIOD = '1' AND (R1.AS_DATETIME_BGN BETWEEN '08:00' AND '12:00')) OR  ");
		sql.append(" (C.ONSITE_PERIOD = '2' AND (R1.AS_DATETIME_BGN BETWEEN '12:01' AND '18:00')) OR  ");
		sql.append(" (C.ONSITE_PERIOD = '3' AND (R1.AS_DATETIME_BGN BETWEEN '08:00' AND '18:00')) )  ");	
		sql.append(" AND R1.AO_CODE = A1.AO_CODE  ");
		sql.append(" AND R1.AS_EMP_ID = E1.EMP_ID  ");
		sql.append(" AND R1.CUST_ID = CI.CUST_ID  "); 
				
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> mbrmMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);
		
		//IF 登入者身份為分行主管or 業務主管
		if (bmmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {	
			sql.append("AND R1.STATUS IN ('01', '0A1') ");
			sql.append("AND A1.BRA_NBR IN ( :bra_id ) ");
			condition.setObject("bra_id", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		//ELSE IF 登入者身份為區督導
		} else if (mbrmMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND R1.STATUS IN ('02', '0A2') ");
			sql.append("AND A1.AREA_ID IN ( :area_id ) ");
			condition.setObject("area_id", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		//ELSE IF 登入者身份為業務處主管	
		} else if (armgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND R1.STATUS IN ('03', '0A3') ");
			sql.append("AND A1.CENTER_ID IN ( :center_id ) ");
			condition.setObject("center_id", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		} else {
			throw new APException("使用者登入身分不適用");
		} 
		condition.setQueryString(sql.toString());
		
		List list = dam.exeQuery(condition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	//檢查衝突
	public void check(Object body, IPrimitiveMap header) throws JBranchException {
		CRM1242InputVO inputVO = (CRM1242InputVO) body;
		CRM1242OutputVO return_VO = new CRM1242OutputVO();
		dam = this.getDataAccessManager();
		XmlInfo xmlInfo = new XmlInfo();
		
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> mbrmMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT R1.SEQ, R1.SALES_PLAN_SEQ, C.ONSITE_BRH, DI.BRANCH_NAME AS ONSITE_NAME, E1.EMP_NAME, CI.CUST_NAME AS CUST_NAME_1, A1.BRANCH_NAME, ");
		sql.append("R1.AS_EMP_ID, R1.AS_TYPE, R1.CUST_ID AS CUST_ID_1, R1.AO_CODE AS AO_CODE_1, A1.EMP_NAME AS AO_EMP_NAME_1, ");
		sql.append("A1.BRA_NBR AS AO_BRA_NBR_1, R1.AS_DATE, R1.AS_DATETIME_BGN AS AS_DATETIME_BGN_1, R1.AS_DATETIME_END AS AS_DATETIME_END_1,  ");
		sql.append("R2.AS_EMP_ID AS AS_EMP_ID_2, R2.CUST_ID_2, R2.CUST_NAME_2, R2.AO_CODE_2, R2.AO_EMP_NAME_2, ");
		sql.append("R2.AO_BRANCH_NAME_2, R2.AS_DATETIME_BGN_2, R2.AS_DATETIME_END_2 ");
		sql.append("FROM TBCRM_WKPG_AS_MAST R1  ");
		sql.append("LEFT JOIN (SELECT M.CUST_ID AS CUST_ID_2, M.AO_CODE AS AO_CODE_2, M.AS_EMP_ID, M.AS_DATE,  ");
		sql.append("M.AS_DATETIME_BGN AS AS_DATETIME_BGN_2, M.AS_DATETIME_END AS AS_DATETIME_END_2, A2.EMP_NAME  ");
		sql.append("AS AO_EMP_NAME_2, A2.BRANCH_NAME AS AO_BRANCH_NAME_2, CI.CUST_NAME AS CUST_NAME_2   ");
		sql.append("FROM TBCRM_WKPG_AS_MAST M, VWORG_AO_INFO A2, VWCRM_CUST_INFO CI WHERE M.AO_CODE=A2.AO_CODE AND M.CUST_ID = CI.CUST_ID ");
		//IF 登入者身份為分行主管or 業務主管
		if (bmmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND M.STATUS IN ('01', '0A1') ");
			sql.append("AND A2.BRA_NBR IN ( :bra_id ) ");
		//ELSE IF 登入者身份為區督導
		} else if (mbrmMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND M.STATUS IN ('02', '0A2') ");
			sql.append("AND A2.AREA_ID IN :area_id ");
		//ELSE IF 登入者身份為業務處主管	
		} else if (armgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND M.STATUS IN ('03', '0A3') ");
			sql.append("AND A2.CENTER_ID IN :center_id ");
		}
		sql.append(") R2  ");
		sql.append("ON R1.AS_EMP_ID = R2.AS_EMP_ID AND R1.AS_DATE = R2.AS_DATE  ");
		sql.append("AND ((R1.AS_DATETIME_BGN BETWEEN R2.AS_DATETIME_BGN_2 AND R2.AS_DATETIME_END_2) OR (R2.AS_DATETIME_BGN_2 BETWEEN R1.AS_DATETIME_BGN AND R1.AS_DATETIME_END)), ");
		sql.append("TBCRM_WKPG_AS_CALENDAR C, ");
		sql.append("VWCRM_CUST_INFO CI,  ");
		sql.append("TBORG_MEMBER E1, ");
		sql.append("VWORG_AO_INFO A1, ");
		sql.append("VWORG_DEFN_INFO DI  ");
		sql.append("WHERE 1=1 ");
		sql.append("AND DI.BRANCH_NBR = C.ONSITE_BRH  ");
		sql.append("AND TO_CHAR(C.ONSITE_DATE, 'YYYY/MM/DD') = TO_CHAR(R1.AS_DATE, 'YYYY/MM/DD') "); 
		sql.append("AND C.AS_EMP_ID = R1.AS_EMP_ID ");
		sql.append("AND ( ");
		sql.append("(C.ONSITE_PERIOD = '1' AND (R1.AS_DATETIME_BGN BETWEEN '08:00' AND '12:00')) OR  ");
		sql.append("(C.ONSITE_PERIOD = '2' AND (R1.AS_DATETIME_BGN BETWEEN '12:01' AND '18:00')) OR ");
		sql.append("(C.ONSITE_PERIOD = '3' AND (R1.AS_DATETIME_BGN BETWEEN '08:00' AND '18:00')) )  ");	
		sql.append("AND R1.AO_CODE = A1.AO_CODE ");
		sql.append("AND R1.AS_EMP_ID = E1.EMP_ID ");
		sql.append("AND R1.CUST_ID = CI.CUST_ID ");	
		sql.append("AND R1.AO_CODE != R2.AO_CODE_2 ");			
		//IF 登入者身份為分行主管or 業務主管
		if (bmmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND R1.STATUS IN ('01', '0A1') ");
			sql.append("AND A1.BRA_NBR IN ( :bra_id ) ");
			condition.setObject("bra_id", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		//ELSE IF 登入者身份為區督導
		} else if (mbrmMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND R1.STATUS IN ('02', '0A2') ");
			sql.append("AND A1.AREA_ID IN (:area_id ) ");
			condition.setObject("area_id", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		//ELSE IF 登入者身份為業務處主管	
		} else if (armgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND R1.STATUS IN ('03', '0A3') ");
			sql.append("AND A1.CENTER_ID IN ( :center_id ) ");
			condition.setObject("center_id", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		}
		condition.setQueryString(sql.toString());
		
		List list = dam.exeQuery(condition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	//確定放行
	public void confirm(Object body, IPrimitiveMap header) throws Exception {
		CRM1242InputVO inputVO = (CRM1242InputVO) body;
		dam = this.getDataAccessManager();		
		TBCRM_WKPG_AS_MASTVO vo = new TBCRM_WKPG_AS_MASTVO();
				
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> mbrmMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);
		for(Map<String, Object> data : inputVO.getList()){
			/********************************************************************************************	
			--------------------------------------分行主管or業務主管 覆核---------------------------------------
			********************************************************************************************/
			if (bmmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				vo = (TBCRM_WKPG_AS_MASTVO) dam.findByPKey(
						TBCRM_WKPG_AS_MASTVO.TABLE_UID, data.get("SEQ").toString());
				
				//確定放行01 or 0A1 ==> 1A
				if (vo != null) {
					vo.setSTATUS("1A");
					vo.setAS_EMP_ID(ObjectUtils.toString(data.get("AS_EMP_ID")));
					vo.setNOT_REMIND_YN("Y");
					vo.setBRH_MGR((String)getUserVariable(FubonSystemVariableConsts.LOGINID));
					vo.setBRH_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
					dam.update(vo);
				}else {
					// 顯示資料不存在
					throw new APException("ehl_01_common_017");
				}
				/****************************************************
									更新駐點行資訊
				****************************************************/
				QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				
				sql.append("UPDATE TBCRM_WKPG_AS_CALENDAR SET ONSITE_BRH =  ");
				sql.append("(SELECT BRA_NBR FROM VWORG_AO_INFO WHERE AO_CODE = :ao_code ) ");
				sql.append("WHERE ONSITE_DATE = :as_date ");
				sql.append("AND ONSITE_PERIOD = :onsite_period ");
				
				condition.setQueryString(ObjectUtils.toString(sql));
				condition.setObject("ao_code", ObjectUtils.toString(data.get("AO_CODE_1")));
				//時間轉換
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				condition.setObject("as_date", new Timestamp(sdf.parse(ObjectUtils.toString(data.get("AS_DATE"))).getTime()));							
				
				//判斷上下午 : IF REVIEW_LIST_[#].AS_DATETIME_BGN BETWEEN 早上8:00~12:00
				if ( 800 < Integer.valueOf(ObjectUtils.toString(data.get("AS_DATETIME_BGN_1")).replace(":" , "")) &&
					Integer.valueOf(ObjectUtils.toString(data.get("AS_DATETIME_BGN_1")).replace(":" , "")) <= 1200 ) {
					condition.setObject("onsite_period", "1");
				} else if ( 1200 < Integer.valueOf(ObjectUtils.toString(data.get("AS_DATETIME_BGN_1")).replace(":" , "")) &&
						Integer.valueOf(ObjectUtils.toString(data.get("AS_DATETIME_BGN_1")).replace(":" , "")) <= 1800 ) {
					condition.setObject("onsite_period", "2");
				} else {
					condition.setObject("onsite_period", "3");
				}
				
				dam.exeUpdate(condition);
								
				//預約陪訪覆核放行後，提醒、分行經理、業務主管(同區)
				QueryConditionIF condition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql2 = new StringBuffer();
								
				sql2.append("SELECT EMP_ID FROM VWORG_AO_INFO WHERE AO_CODE = :ao_code ");
				
				condition2.setQueryString(ObjectUtils.toString(sql2));
				condition2.setObject("ao_code", ObjectUtils.toString(data.get("AO_CODE_1")));
				
				List<Map<String, String>> empList = dam.exeQuery(condition2);			
				for(Map<String, String> emp : empList) {
				QueryConditionIF queryConditions = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sqls = new StringBuffer();
				sqls.append("SELECT SQ_TBCAM_CAL_SALES_TASK.nextval AS SEQ FROM DUAL ");
				
				queryConditions.setQueryString(ObjectUtils.toString(sqls));
				
				List<Map<String, Object>> SEQLIST = dam.exeQuery(queryConditions);
				
				BigDecimal seqNo = new BigDecimal(getSEQ());
				while(checkID(seqNo)) {
					seqNo = new BigDecimal(getSEQ());
				}
				
				TBCAM_CAL_SALES_TASKVO vos = new TBCAM_CAL_SALES_TASKVO();
				vos.setSEQ_NO(seqNo);
				vos.setEMP_ID(emp.get("EMP_ID"));
				vos.setAS_EMP_ID(ObjectUtils.toString(data.get("AS_EMP_ID")));
				vos.setCUST_ID(ObjectUtils.toString(data.get("CUST_ID_1")));										
				vos.setTASK_DATE(new Timestamp(sdf.parse(ObjectUtils.toString(data.get("AS_DATE"))).getTime()));
				vos.setTASK_STIME(ObjectUtils.toString(data.get("AS_DATETIME_BGN_1")).replace(":" , ""));
				vos.setTASK_ETIME(ObjectUtils.toString(data.get("AS_DATETIME_END_1")).replace(":" , ""));
				vos.setTASK_TITLE("預約陪訪行程放行");
				
				
				//提醒內容:判斷FA/IA
				String emp_id = "";
				if ("F".equals(ObjectUtils.toString(data.get("AS_TYPE")))) {
					emp_id = " FA-" + ObjectUtils.toString(data.get("EMP_NAME"));
				}else if ("I".equals(ObjectUtils.toString(data.get("AS_TYPE")))) {
					emp_id = " IA-" + ObjectUtils.toString(data.get("EMP_NAME"));
				}else{
					emp_id = ObjectUtils.toString(data.get("EMP_NAME"));
				}
				
				vos.setTASK_MEMO("陪訪日期-" + new Timestamp(sdf.parse(ObjectUtils.toString(data.get("AS_DATE"))).getTime()) + " 陪訪時間-" +
						ObjectUtils.toString(data.get("AS_DATETIME_BGN_1")) + "~" + ObjectUtils.toString(data.get("AS_DATETIME_END_1")) +
						" 分行-" + ObjectUtils.toString(data.get("ONSITE_BRH")) + " 理專-" + ObjectUtils.toString(data.get("AO_EMP_NAME_1")) +
						" 客戶姓名-" + ObjectUtils.toString(data.get("CUST_NAME_1") + " " + emp_id));
				vos.setTASK_SOURCE("4");
				//vos.setTASK_TYPE();
				vos.setTASK_STATUS("I");
				
				dam.create(vos);
				}
				
				//衝突覆核退回理專 01 or 0A1 ==> 5C
				QueryConditionIF condition3 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql3 = new StringBuffer();
				
				sql3.append("UPDATE TBCRM_WKPG_AS_MAST SET STATUS = '5C', NOT_REMIND_YN = 'Y' WHERE ");
				sql3.append("SEQ <> :seq ");
				sql3.append("AND AS_DATE = :as_date ");
				sql3.append("AND AS_EMP_ID = :as_emp_id ");
				sql3.append("AND TO_NUMBER(REPLACE( :as_datetime_bgn ,':',''))  ");
				sql3.append("BETWEEN TO_NUMBER(REPLACE(AS_DATETIME_BGN,':','')) AND TO_NUMBER(REPLACE(AS_DATETIME_END,':','')) ");
				condition3.setQueryString(sql3.toString());
				condition3.setObject("seq", ObjectUtils.toString(data.get("SEQ")));
				condition3.setObject("as_emp_id", ObjectUtils.toString(data.get("AS_EMP_ID")));
				
				//時間轉換
				condition3.setObject("as_date", new Timestamp(sdf.parse(ObjectUtils.toString(data.get("AS_DATE"))).getTime()));			
				
				condition3.setObject("as_datetime_bgn", data.get("AS_DATETIME_BGN_1").toString());
				
				dam.exeUpdate(condition3);
				this.sendRtnObject(null);
				
			/********************************************************************************************	
			-------------------------------------------區督導 覆核-------------------------------------------
			********************************************************************************************/
			} else if (mbrmMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				vo = (TBCRM_WKPG_AS_MASTVO) dam.findByPKey(
						TBCRM_WKPG_AS_MASTVO.TABLE_UID, data.get("SEQ").toString());
								
				//確定放行02 or 0A2 ==> 2A
				if (vo != null) {					
					vo.setSTATUS("2A");
					vo.setAS_EMP_ID(ObjectUtils.toString(data.get("AS_EMP_ID")));
					vo.setNOT_REMIND_YN("Y");
					vo.setOP_MGR((String)getUserVariable(FubonSystemVariableConsts.LOGINID));
					vo.setOP_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
					dam.update(vo);
					
				}else {
					// 顯示資料不存在
					throw new APException("ehl_01_common_017");
				}
				
				/****************************************************
									更新駐點行資訊
				****************************************************/
				QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				
				sql.append("UPDATE TBCRM_WKPG_AS_CALENDAR SET ONSITE_BRH =  ");
				sql.append("(SELECT BRA_NBR FROM VWORG_AO_INFO WHERE AO_CODE = :ao_code ) ");
				sql.append("WHERE ONSITE_DATE = :as_date ");
				sql.append("AND ONSITE_PERIOD = :onsite_period ");
				
				condition.setQueryString(ObjectUtils.toString(sql));
				condition.setObject("ao_code", ObjectUtils.toString(data.get("AO_CODE_1")));
				//時間轉換
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				condition.setObject("as_date", new Timestamp(sdf.parse(ObjectUtils.toString(data.get("AS_DATE"))).getTime()));							
				
				//判斷上下午 : IF REVIEW_LIST_[#].AS_DATETIME_BGN BETWEEN 早上8:00~12:00
				if ( 800 < Integer.valueOf(ObjectUtils.toString(data.get("AS_DATETIME_BGN_1")).replace(":" , "")) &&
						Integer.valueOf(ObjectUtils.toString(data.get("AS_DATETIME_BGN_1")).replace(":" , "")) <= 1200 ) {
					condition.setObject("onsite_period", "1");
				} else if ( 1200 < Integer.valueOf(ObjectUtils.toString(data.get("AS_DATETIME_BGN_1")).replace(":" , "")) &&
						Integer.valueOf(ObjectUtils.toString(data.get("AS_DATETIME_BGN_1")).replace(":" , "")) <= 1800 ){
					condition.setObject("onsite_period", "2");
				} else {
					condition.setObject("onsite_period", "3");
				}
				
				dam.exeUpdate(condition);
				
				
				//預約陪訪覆核放行後，提醒、分行經理、業務主管(同區)
				QueryConditionIF condition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql2 = new StringBuffer();
				
				sql2.append("SELECT EMP_ID FROM VWORG_AO_INFO WHERE AO_CODE = :ao_code ");
				
				condition2.setQueryString(ObjectUtils.toString(sql2));
				condition2.setObject("ao_code", ObjectUtils.toString(data.get("AO_CODE_1")));
				
				List<Map<String, String>> empList = dam.exeQuery(condition2);			
				for(Map<String, String> emp : empList) {
					QueryConditionIF queryConditions = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer sqls = new StringBuffer();
					sqls.append("SELECT SQ_TBCAM_CAL_SALES_TASK.nextval AS SEQ FROM DUAL ");
					
					queryConditions.setQueryString(ObjectUtils.toString(sqls));
					
					List<Map<String, Object>> SEQLIST = dam.exeQuery(queryConditions);
					
					BigDecimal seqNo = new BigDecimal(getSEQ());
					while(checkID(seqNo)) {
						seqNo = new BigDecimal(getSEQ());
					}
					
					TBCAM_CAL_SALES_TASKVO vos = new TBCAM_CAL_SALES_TASKVO();
					vos.setSEQ_NO(seqNo);
					vos.setEMP_ID(emp.get("EMP_ID"));
					vos.setAS_EMP_ID(ObjectUtils.toString(data.get("AS_EMP_ID")));
					vos.setCUST_ID(ObjectUtils.toString(data.get("CUST_ID_1")));										
					vos.setTASK_DATE(new Timestamp(sdf.parse(ObjectUtils.toString(data.get("AS_DATE"))).getTime()));
					vos.setTASK_STIME(ObjectUtils.toString(data.get("AS_DATETIME_BGN_1")).replace(":" , ""));
					vos.setTASK_ETIME(ObjectUtils.toString(data.get("AS_DATETIME_END_1")).replace(":" , ""));
					vos.setTASK_TITLE("預約陪訪行程放行");
					
					
					//提醒內容:判斷FA/IA
					String emp_id = "";
					if ("F".equals(ObjectUtils.toString(data.get("AS_TYPE")))) {
						emp_id = " FA-" + ObjectUtils.toString(data.get("EMP_NAME"));
					}else if ("I".equals(ObjectUtils.toString(data.get("AS_TYPE")))) {
						emp_id = " IA-" + ObjectUtils.toString(data.get("EMP_NAME"));
					}else{
						emp_id = ObjectUtils.toString(data.get("EMP_NAME"));
					}
					
					vos.setTASK_MEMO("陪訪日期-" + new Timestamp(sdf.parse(ObjectUtils.toString(data.get("AS_DATE"))).getTime()) + " 陪訪時間-" +
							ObjectUtils.toString(data.get("AS_DATETIME_BGN_1")) + "~" + ObjectUtils.toString(data.get("AS_DATETIME_END_1")) +
							" 分行-" + ObjectUtils.toString(data.get("ONSITE_BRH")) + " 理專-" + ObjectUtils.toString(data.get("AO_EMP_NAME_1")) +
							" 客戶姓名-" + ObjectUtils.toString(data.get("CUST_NAME_1")) + " " + emp_id);
					vos.setTASK_SOURCE("4");
					vos.setTASK_STATUS("I");
					
					dam.create(vos);
				}
				
				//衝突覆核退回理專 02 or 0A2 ==> 5C
				QueryConditionIF condition3 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql3 = new StringBuffer();
					
				sql3.append("UPDATE TBCRM_WKPG_AS_MAST SET STATUS = '5C', NOT_REMIND_YN = 'Y' WHERE ");
				sql3.append("SEQ <> :seq ");
				sql3.append("AND AS_DATE = :as_date ");
				sql3.append("AND AS_EMP_ID = :as_emp_id ");
				sql3.append("AND TO_NUMBER(REPLACE( :as_datetime_bgn ,':',''))  ");
				sql3.append("BETWEEN TO_NUMBER(REPLACE(AS_DATETIME_BGN,':','')) AND TO_NUMBER(REPLACE(AS_DATETIME_END,':','')) ");
				
				
				condition3.setQueryString(ObjectUtils.toString(sql3));
				condition3.setObject("seq", ObjectUtils.toString(data.get("SEQ")));
				condition3.setObject("as_emp_id", ObjectUtils.toString(data.get("AS_EMP_ID_1")));
				
				//時間轉換
				condition3.setObject("as_date", new Timestamp(sdf.parse(ObjectUtils.toString(data.get("AS_DATE"))).getTime()));
				
				condition3.setObject("as_datetime_bgn", ObjectUtils.toString(data.get("AS_DATETIME_BGN_1")));				
				dam.exeUpdate(condition3);
				this.sendRtnObject(null);
				
			/********************************************************************************************	
			----------------------------------------業務處主管 覆核------------------------------------------
			********************************************************************************************/
			} else if (armgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				vo = (TBCRM_WKPG_AS_MASTVO) dam.findByPKey(
						TBCRM_WKPG_AS_MASTVO.TABLE_UID, data.get("SEQ").toString());
				
				//確定放行03 or 0A3 ==> 3A
				if (vo != null) {				
					vo.setSTATUS("3A");
					vo.setAS_EMP_ID(ObjectUtils.toString(data.get("AS_EMP_ID")));
					vo.setNOT_REMIND_YN("Y");
					vo.setDC_MGR((String)getUserVariable(FubonSystemVariableConsts.LOGINID));
					vo.setDC_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
					dam.update(vo);				
				}else {
					// 顯示資料不存在
					throw new APException("ehl_01_common_017");
				}
				
				/****************************************************
									更新駐點行資訊
				****************************************************/
				QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				
				sql.append("UPDATE TBCRM_WKPG_AS_CALENDAR SET ONSITE_BRH =  ");
				sql.append("(SELECT BRA_NBR FROM VWORG_AO_INFO WHERE AO_CODE = :ao_code ) ");
				sql.append("WHERE ONSITE_DATE = :as_date ");
				sql.append("AND ONSITE_PERIOD = :onsite_period ");
				
				condition.setQueryString(sql.toString());
				condition.setObject("ao_code", ObjectUtils.toString(data.get("AO_CODE_1")));				
				//時間轉換
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				condition.setObject("as_date", new Timestamp(sdf.parse(ObjectUtils.toString(data.get("AS_DATE"))).getTime()));
				
				//判斷上下午 : IF REVIEW_LIST_[#].AS_DATETIME_BGN BETWEEN 早上8:00~12:00
				
				if ( 800 < Integer.valueOf(ObjectUtils.toString(data.get("AS_DATETIME_BGN_1")).replace(":" , "")) &&
					Integer.valueOf(ObjectUtils.toString(data.get("AS_DATETIME_BGN_1")).replace(":" , "")) <= 1200 ) {
					condition.setObject("onsite_period", "1");
				} else if ( 1200 < Integer.valueOf(ObjectUtils.toString(data.get("AS_DATETIME_BGN_1")).replace(":" , "")) &&
						Integer.valueOf(ObjectUtils.toString(data.get("AS_DATETIME_BGN_1")).replace(":" , "")) <= 1800 ){
					condition.setObject("onsite_period", "2");
				} else {
					condition.setObject("onsite_period", "3");
				}
				dam.exeUpdate(condition);
				
				//預約陪訪覆核放行後，提醒、分行經理、業務主管(同區)
				QueryConditionIF condition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql2 = new StringBuffer();
							
				sql2.append("SELECT EMP_ID FROM VWORG_AO_INFO WHERE AO_CODE = :ao_code ");
						
				condition2.setQueryString(ObjectUtils.toString(sql2));
				condition2.setObject("ao_code", ObjectUtils.toString(data.get("AO_CODE_1")));
				
				List<Map<String, String>> empList = dam.exeQuery(condition2);	
				for(Map<String, String> emp : empList){
					
					BigDecimal seqNo = new BigDecimal(getSEQ());
					while(checkID(seqNo)) {
						seqNo = new BigDecimal(getSEQ());
					}
					TBCAM_CAL_SALES_TASKVO vos = new TBCAM_CAL_SALES_TASKVO();
					vos.setSEQ_NO(seqNo);
					vos.setEMP_ID(emp.get("EMP_ID"));
					vos.setAS_EMP_ID(data.get("AS_EMP_ID").toString());
					vos.setCUST_ID(data.get("CUST_ID_1").toString());
					vos.setTASK_DATE(new Timestamp(sdf.parse(ObjectUtils.toString(data.get("AS_DATE"))).getTime()));
					vos.setTASK_STIME(ObjectUtils.toString(data.get("AS_DATETIME_BGN_1")).replace(":" , ""));
					vos.setTASK_ETIME(ObjectUtils.toString(data.get("AS_DATETIME_BGN_1")).replace(":" , ""));
					vos.setTASK_TITLE("預約陪訪行程放行");
					
					//
					String emp_id = "";
					if ("F".equals(ObjectUtils.toString(data.get("EMP_NAME")))) {
						emp_id = " FA-" + ObjectUtils.toString(data.get("AS_EMP_ID"));
					}else if ("I".equals(ObjectUtils.toString(data.get("EMP_NAME")))) {
						emp_id = " IA-" + ObjectUtils.toString(data.get("AS_EMP_ID"));
					}else{
						emp_id = ObjectUtils.toString(data.get("EMP_NAME"));
					}
					
					vos.setTASK_MEMO("陪訪日期-" + new Timestamp(sdf.parse(ObjectUtils.toString(data.get("AS_DATE"))).getTime()) + " 陪訪時間-" +
							ObjectUtils.toString(data.get("AS_DATETIME_BGN_1")) + "~" + ObjectUtils.toString(data.get("AS_DATETIME_END_1")) +
							" 分行-" + ObjectUtils.toString(data.get("ONSITE_BRH")) + " 理專-" + ObjectUtils.toString(data.get("AO_EMP_NAME_1")) +
							" 客戶姓名-" + ObjectUtils.toString(data.get("CUST_NAME_1")) + " " + emp_id);
					vos.setTASK_SOURCE("4");
//					vos.setTASK_TYPE();
					vos.setTASK_STATUS("I");
					
					dam.create(vos);
				}
				/***************************************************/
				
				//檢查其他衝突的行程，需取消其他的預約行程
				QueryConditionIF condition3 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql3 = new StringBuffer();
				
				sql3.append("UPDATE TBCRM_WKPG_AS_MAST SET STATUS = '5C', NOT_REMIND_YN = 'Y' WHERE ");
				sql3.append("SEQ <> :seq ");
				sql3.append("AND AS_DATE = :as_date ");
				sql3.append("AND AS_EMP_ID = :as_emp_id ");
				sql3.append("AND TO_NUMBER(REPLACE( :as_datetime_bgn ,':',''))  ");
				sql3.append("BETWEEN TO_NUMBER(REPLACE(AS_DATETIME_BGN,':','')) AND TO_NUMBER(REPLACE(AS_DATETIME_END,':','')) ");
								
				condition3.setQueryString(sql3.toString());
				condition3.setObject("seq", ObjectUtils.toString(data.get("SEQ")));
				condition3.setObject("as_emp_id", ObjectUtils.toString(data.get("AS_EMP_ID")));
				
				//時間轉換
				condition3.setObject("as_date", new Timestamp(sdf.parse(ObjectUtils.toString(data.get("AS_DATE"))).getTime()));
				
				condition3.setObject("as_datetime_bgn", data.get("AS_DATETIME_BGN_1").toString());				
				dam.exeUpdate(condition3);
				this.sendRtnObject(null);
			//===========================================================================================	
			}
		}
	}
	
	
	//退回理專
	public void reject(Object body, IPrimitiveMap header) throws JBranchException {
		CRM1242InputVO inputVO = (CRM1242InputVO) body;
		dam = this.getDataAccessManager();
		
		TBCRM_WKPG_AS_MASTVO vo = new TBCRM_WKPG_AS_MASTVO();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> mbrmMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);
		
		for(Map<String, Object> data : inputVO.getList()){
			//分行主管or業務主管 覆核
			//===========================================================================================	
			if (bmmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				
				vo = (TBCRM_WKPG_AS_MASTVO) dam.findByPKey(
						TBCRM_WKPG_AS_MASTVO.TABLE_UID, data.get("SEQ").toString());
				
				if (vo != null) {
					
					vo.setSTATUS("1R");
					vo.setBRH_MGR((String)getUserVariable(FubonSystemVariableConsts.LOGINID));
					vo.setBRH_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
					dam.update(vo);
					this.sendRtnObject(null);
					
				}else {
					// 顯示資料不存在
					throw new APException("ehl_01_common_017");
				}
				
				
			//===========================================================================================	
			//區督導 覆核
			//===========================================================================================	
			} else if (mbrmMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				
				vo = (TBCRM_WKPG_AS_MASTVO) dam.findByPKey(
						TBCRM_WKPG_AS_MASTVO.TABLE_UID, data.get("SEQ").toString());
				
				if (vo != null) {
					
					vo.setSTATUS("2R");
					vo.setOP_MGR((String)getUserVariable(FubonSystemVariableConsts.LOGINID));
					vo.setOP_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
					dam.update(vo);
					this.sendRtnObject(null);
					
				}else {
					// 顯示資料不存在
					throw new APException("ehl_01_common_017");
				}
				
			//===========================================================================================		
			//業務處主管 覆核
			//===========================================================================================	
			} else if (armgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				
				vo = (TBCRM_WKPG_AS_MASTVO) dam.findByPKey(
						TBCRM_WKPG_AS_MASTVO.TABLE_UID, data.get("SEQ").toString());
				
				if (vo != null) {
					
					vo.setSTATUS("3R");
					vo.setDC_MGR((String)getUserVariable(FubonSystemVariableConsts.LOGINID));
					vo.setDC_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
					dam.update(vo);
					this.sendRtnObject(null);
					
				}else {
					// 顯示資料不存在
					throw new APException("ehl_01_common_017");
				}
				
			}
			
			
		}
	}
	
	
	/**產生seq No */
	private String getSEQ() throws JBranchException {
		  SerialNumberUtil seq = new SerialNumberUtil();
		  String seqNum = "";
		  try{
		    seqNum = seq.getNextSerialNumber("TBCAM_CAL_SALES_TASK");
		  }
		  catch(Exception e){
		   seq.createNewSerial("CRM121", "0000000000", null, null, null, 6, new Long("99999999"), "y", new Long("0"), null);
		   seqNum = seq.getNextSerialNumber("TBCAM_CAL_SALES_TASK");
		   }
		  return seqNum;
	}
	/**檢查seq No */
	private Boolean checkID(BigDecimal seqNo) throws JBranchException {
		Boolean ans = false;
		
		TBCAM_CAL_SALES_TASKVO vo = new TBCAM_CAL_SALES_TASKVO();
		vo = (TBCAM_CAL_SALES_TASKVO) dam.findByPKey(TBCAM_CAL_SALES_TASKVO.TABLE_UID, seqNo);
		if (vo != null)
			ans = true;
		else
			ans = false;
		return ans;
	}
	
	//輔銷陪訪判斷:駐點時間
	public void checkASOnsite(Object body, IPrimitiveMap header) throws JBranchException {
		CRM1242InputVO inputVO = (CRM1242InputVO) body;
		CRM1242OutputVO return_VO = new CRM1242OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		if(!"3".equals(inputVO.getMeetingType())){
			sql.append("SELECT A.ONSITE_BRH, FI.BRANCH_NBR, FI.EMP_ID AS AS_EMP_ID ");
			sql.append("FROM TBCRM_WKPG_AS_CALENDAR A, ");
			sql.append("TBORG_FAIA FI ");
			sql.append("WHERE TO_CHAR(A.ONSITE_DATE, 'YYYY/MM/DD') = TO_CHAR(:meetingDate, 'YYYY/MM/DD') ");
			sql.append("AND A.AS_EMP_ID = FI.EMP_ID ");
			sql.append("AND FI.BRANCH_NBR = :branch_nbr ");
			sql.append("AND A.ONSITE_BRH = FI.BRANCH_NBR ");
			
			//IF PMS109InputVO.meetingTimeS 介於早上8:00~12:00之間
			if ("1".equals(inputVO.getMeetingType())) {
				sql.append("AND ONSITE_PERIOD = 1 ");					
			} 
			//IF PMS109InputVO.meetingTimeS 介於下午12:00~18:00之間	
			else if ("2".equals(inputVO.getMeetingType())){
				sql.append("AND ONSITE_PERIOD = 2 ");			
			}			
			
			//IF PMS109InputVO.FA_FLAG=Y & PMS109InputVO.IA_FLAG=Y THEN
			if ("Y".equals(inputVO.getFA_FLAG()) &&  "Y".equals(inputVO.getIA_FLAG())) {
				sql.append("AND FI.SUPT_SALES_TEAM_ID in ('FA', 'IA') ");
			} else if ("Y".equals(inputVO.getFA_FLAG())) {
				sql.append("AND FI.SUPT_SALES_TEAM_ID = 'FA' ");
			} else if ("Y".equals(inputVO.getIA_FLAG())) {
				sql.append("AND FI.SUPT_SALES_TEAM_ID = 'IA' ");
			}
		}
		//IF PMS109InputVO.meetingTimeS 跨越中午於8:00~18:00之間	
		else {
			sql.append(" SELECT O1.ONSITE_BRH, O1.BRANCH_NBR, O1.AS_EMP_ID FROM	( ");
			sql.append(" SELECT A.ONSITE_BRH, FI.BRANCH_NBR, FI.EMP_ID AS AS_EMP_ID ");
			sql.append(" FROM TBCRM_WKPG_AS_CALENDAR A, ");
			sql.append(" TBORG_FAIA FI ");
			sql.append(" WHERE TO_CHAR(A.ONSITE_DATE, 'YYYY/MM/DD') = TO_CHAR(:meetingDate, 'YYYY/MM/DD') ");
			sql.append(" AND A.AS_EMP_ID = FI.EMP_ID ");
			sql.append(" AND FI.BRANCH_NBR = :branch_nbr ");
			sql.append(" AND A.ONSITE_BRH = FI.BRANCH_NBR ");
			sql.append(" AND ONSITE_PERIOD = 1 ");
			//IF PMS109InputVO.FA_FLAG=Y & PMS109InputVO.IA_FLAG=Y THEN
			if ("Y".equals(inputVO.getFA_FLAG()) &&  "Y".equals(inputVO.getIA_FLAG())) {
				sql.append("AND FI.SUPT_SALES_TEAM_ID in ('FA', 'IA') ");
			} else if ("Y".equals(inputVO.getFA_FLAG())) {
				sql.append("AND FI.SUPT_SALES_TEAM_ID = 'FA' ");
			} else if ("Y".equals(inputVO.getIA_FLAG())) {
				sql.append("AND FI.SUPT_SALES_TEAM_ID = 'IA' ");
			}
			sql.append(" )O1 ");
			sql.append(" LEFT JOIN ( ");
			sql.append(" SELECT A.ONSITE_BRH, FI.BRANCH_NBR, FI.EMP_ID AS AS_EMP_ID ");
			sql.append(" FROM TBCRM_WKPG_AS_CALENDAR A, ");
			sql.append(" TBORG_FAIA FI ");
			sql.append(" WHERE TO_CHAR(A.ONSITE_DATE, 'YYYY/MM/DD') = TO_CHAR(:meetingDate, 'YYYY/MM/DD') ");
			sql.append(" AND A.AS_EMP_ID = FI.EMP_ID ");
			sql.append(" AND FI.BRANCH_NBR = :branch_nbr ");
			sql.append(" AND A.ONSITE_BRH = FI.BRANCH_NBR ");
			sql.append(" AND ONSITE_PERIOD = 2 ");
			//IF PMS109InputVO.FA_FLAG=Y & PMS109InputVO.IA_FLAG=Y THEN
			if ("Y".equals(inputVO.getFA_FLAG()) &&  "Y".equals(inputVO.getIA_FLAG())) {
				sql.append("AND FI.SUPT_SALES_TEAM_ID in ('FA', 'IA') ");
			} else if ("Y".equals(inputVO.getFA_FLAG())) {
				sql.append("AND FI.SUPT_SALES_TEAM_ID = 'FA' ");
			} else if ("Y".equals(inputVO.getIA_FLAG())) {
				sql.append("AND FI.SUPT_SALES_TEAM_ID = 'IA' ");
			}
			sql.append(" )O2 ");
			sql.append(" ON O1.AS_EMP_ID = O2.AS_EMP_ID ");
			sql.append(" WHERE O2.AS_EMP_ID is not null ");
		}
		condition.setQueryString(sql.toString());
		condition.setObject("branch_nbr", inputVO.getBranch_nbr());
		condition.setObject("meetingDate", new Timestamp(inputVO.getMeetingDate().getTime()));
		
		List list = dam.exeQuery(condition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);

	}
	
	//新增輔銷駐點覆核
	public void createASDateReview(Object body, IPrimitiveMap header) throws JBranchException {
		CRM1242InputVO inputVO = (CRM1242InputVO) body;
		dam = this.getDataAccessManager();
				
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		QueryConditionIF condition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();
		
		/**sql	:	查詢理專要求之時間，該分行的駐點人員
		 * sql2	:	查詢理專要求之時間，全分行的駐點人員*/
		
		if(!"3".equals(inputVO.getMeetingType())){
			sql.append("SELECT A.ONSITE_BRH, FI.BRANCH_NBR, FI.EMP_ID AS AS_EMP_ID ");
			sql.append("FROM TBCRM_WKPG_AS_CALENDAR A, ");
			sql.append("TBORG_FAIA FI ");
			sql.append("WHERE TO_CHAR(A.ONSITE_DATE, 'YYYY/MM/DD') = TO_CHAR(:meetingDate, 'YYYY/MM/DD') ");
			sql.append("AND A.AS_EMP_ID = FI.EMP_ID ");
			sql.append("AND FI.BRANCH_NBR = :branch_nbr ");
			sql.append("AND A.ONSITE_BRH = FI.BRANCH_NBR ");
			
			//IF PMS109InputVO.meetingTimeS 介於早上8:00~12:00之間
			if ("1".equals(inputVO.getMeetingType())) {
				sql.append("AND ONSITE_PERIOD = 1 ");					
			} 
			//IF PMS109InputVO.meetingTimeS 介於下午12:00~18:00之間	
			else if ("2".equals(inputVO.getMeetingType())){
				sql.append("AND ONSITE_PERIOD = 2 ");			
			}			
			
			//IF PMS109InputVO.FA_FLAG=Y & PMS109InputVO.IA_FLAG=Y THEN
			if ("Y".equals(inputVO.getFA_FLAG()) &&  "Y".equals(inputVO.getIA_FLAG())) {
				sql.append("AND FI.SUPT_SALES_TEAM_ID in ('FA', 'IA') ");
			} else if ("Y".equals(inputVO.getFA_FLAG())) {
				sql.append("AND FI.SUPT_SALES_TEAM_ID = 'FA' ");
			} else if ("Y".equals(inputVO.getIA_FLAG())) {
				sql.append("AND FI.SUPT_SALES_TEAM_ID = 'IA' ");
			}
		}
		//IF PMS109InputVO.meetingTimeS 跨越中午於8:00~18:00之間	
		else {
			sql.append(" SELECT O1.ONSITE_BRH, O1.BRANCH_NBR, O1.AS_EMP_ID FROM	( ");
			sql.append(" SELECT A.ONSITE_BRH, FI.BRANCH_NBR, FI.EMP_ID AS AS_EMP_ID ");
			sql.append(" FROM TBCRM_WKPG_AS_CALENDAR A, ");
			sql.append(" TBORG_FAIA FI ");
			sql.append(" WHERE TO_CHAR(A.ONSITE_DATE, 'YYYY/MM/DD') = TO_CHAR(:meetingDate, 'YYYY/MM/DD') ");
			sql.append(" AND A.AS_EMP_ID = FI.EMP_ID ");
			sql.append(" AND FI.BRANCH_NBR = :branch_nbr ");
			sql.append(" AND A.ONSITE_BRH = FI.BRANCH_NBR ");
			sql.append(" AND ONSITE_PERIOD = 1 ");
			//IF PMS109InputVO.FA_FLAG=Y & PMS109InputVO.IA_FLAG=Y THEN
			if ("Y".equals(inputVO.getFA_FLAG()) &&  "Y".equals(inputVO.getIA_FLAG())) {
				sql.append("AND FI.SUPT_SALES_TEAM_ID in ('FA', 'IA') ");
			} else if ("Y".equals(inputVO.getFA_FLAG())) {
				sql.append("AND FI.SUPT_SALES_TEAM_ID = 'FA' ");
			} else if ("Y".equals(inputVO.getIA_FLAG())) {
				sql.append("AND FI.SUPT_SALES_TEAM_ID = 'IA' ");
			}
			sql.append(" )O1 ");
			sql.append(" LEFT JOIN ( ");
			sql.append(" SELECT A.ONSITE_BRH, FI.BRANCH_NBR, FI.EMP_ID AS AS_EMP_ID ");
			sql.append(" FROM TBCRM_WKPG_AS_CALENDAR A, ");
			sql.append(" TBORG_FAIA FI ");
			sql.append(" WHERE TO_CHAR(A.ONSITE_DATE, 'YYYY/MM/DD') = TO_CHAR(:meetingDate, 'YYYY/MM/DD') ");
			sql.append(" AND A.AS_EMP_ID = FI.EMP_ID ");
			sql.append(" AND FI.BRANCH_NBR = :branch_nbr ");
			sql.append(" AND A.ONSITE_BRH = FI.BRANCH_NBR ");
			sql.append(" AND ONSITE_PERIOD = 2 ");
			//IF PMS109InputVO.FA_FLAG=Y & PMS109InputVO.IA_FLAG=Y THEN
			if ("Y".equals(inputVO.getFA_FLAG()) &&  "Y".equals(inputVO.getIA_FLAG())) {
				sql.append("AND FI.SUPT_SALES_TEAM_ID in ('FA', 'IA') ");
			} else if ("Y".equals(inputVO.getFA_FLAG())) {
				sql.append("AND FI.SUPT_SALES_TEAM_ID = 'FA' ");
			} else if ("Y".equals(inputVO.getIA_FLAG())) {
				sql.append("AND FI.SUPT_SALES_TEAM_ID = 'IA' ");
			}
			sql.append(" )O2 ");
			sql.append(" ON O1.AS_EMP_ID = O2.AS_EMP_ID ");
			sql.append(" WHERE O2.AS_EMP_ID is not null ");
		}
		
		condition.setQueryString(sql.toString());
		condition.setObject("branch_nbr", inputVO.getBranch_nbr());
		condition.setObject("meetingDate", new Timestamp(inputVO.getMeetingDate().getTime()));
		
		//======================================================================================
				
		if(!"3".equals(inputVO.getMeetingType())){
			sql2.append("SELECT A.ONSITE_BRH, FI.BRANCH_NBR, FI.EMP_ID AS AS_EMP_ID ");
			sql2.append("FROM TBCRM_WKPG_AS_CALENDAR A, ");
			sql2.append("TBORG_FAIA FI ");
			sql2.append("WHERE TO_CHAR(A.ONSITE_DATE, 'YYYY/MM/DD') = TO_CHAR(:meetingDate, 'YYYY/MM/DD') ");
			sql2.append("AND A.AS_EMP_ID = FI.EMP_ID ");
			sql2.append("AND A.ONSITE_BRH = FI.BRANCH_NBR ");
			
			//IF PMS109InputVO.meetingTimeS 介於早上8:00~12:00之間
			if ("1".equals(inputVO.getMeetingType())) {
				sql2.append("AND ONSITE_PERIOD = 1 ");					
			} 
			//IF PMS109InputVO.meetingTimeS 介於下午12:00~18:00之間	
			else if ("2".equals(inputVO.getMeetingType())){
				sql2.append("AND ONSITE_PERIOD = 2 ");			
			} 
			//IF PMS109InputVO.FA_FLAG=Y & PMS109InputVO.IA_FLAG=Y THEN
			if ("Y".equals(inputVO.getFA_FLAG()) &&  "Y".equals(inputVO.getIA_FLAG())) {
				sql2.append("AND FI.SUPT_SALES_TEAM_ID in ('FA', 'IA') ");
			} else if ("Y".equals(inputVO.getFA_FLAG())) {
				sql2.append("AND FI.SUPT_SALES_TEAM_ID = 'FA' ");
			} else if ("Y".equals(inputVO.getIA_FLAG())) {
				sql2.append("AND FI.SUPT_SALES_TEAM_ID = 'IA' ");
			}
		}
		//IF PMS109InputVO.meetingTimeS 跨越中午於8:00~18:00之間	
		else {
			sql2.append(" SELECT O1.ONSITE_BRH, O1.BRANCH_NBR, O1.AS_EMP_ID FROM ( ");
			sql2.append(" SELECT A.ONSITE_BRH, FI.BRANCH_NBR, FI.EMP_ID AS AS_EMP_ID ");
			sql2.append(" FROM TBCRM_WKPG_AS_CALENDAR A, ");
			sql2.append(" TBORG_FAIA FI ");
			sql2.append(" WHERE TO_CHAR(A.ONSITE_DATE, 'YYYY/MM/DD') = TO_CHAR(:meetingDate, 'YYYY/MM/DD') ");
			sql2.append(" AND A.AS_EMP_ID = FI.EMP_ID ");
			sql2.append(" AND A.ONSITE_BRH = FI.BRANCH_NBR ");
			sql2.append(" AND ONSITE_PERIOD = 1 ");
			//IF PMS109InputVO.FA_FLAG=Y & PMS109InputVO.IA_FLAG=Y THEN
			if ("Y".equals(inputVO.getFA_FLAG()) &&  "Y".equals(inputVO.getIA_FLAG())) {
				sql2.append("AND FI.SUPT_SALES_TEAM_ID in ('FA', 'IA') ");
			} else if ("Y".equals(inputVO.getFA_FLAG())) {
				sql2.append("AND FI.SUPT_SALES_TEAM_ID = 'FA' ");
			} else if ("Y".equals(inputVO.getIA_FLAG())) {
				sql2.append("AND FI.SUPT_SALES_TEAM_ID = 'IA' ");
			}
			sql2.append(" )O1 ");
			sql2.append(" LEFT JOIN ( ");
			sql2.append(" SELECT A.ONSITE_BRH, FI.BRANCH_NBR, FI.EMP_ID AS AS_EMP_ID ");
			sql2.append(" FROM TBCRM_WKPG_AS_CALENDAR A, ");
			sql2.append(" TBORG_FAIA FI ");
			sql2.append(" WHERE TO_CHAR(A.ONSITE_DATE, 'YYYY/MM/DD') = TO_CHAR(:meetingDate, 'YYYY/MM/DD') ");
			sql2.append(" AND A.AS_EMP_ID = FI.EMP_ID ");
			sql2.append(" AND A.ONSITE_BRH = FI.BRANCH_NBR ");
			sql2.append(" AND ONSITE_PERIOD = 2 ");
			//IF PMS109InputVO.FA_FLAG=Y & PMS109InputVO.IA_FLAG=Y THEN
			if ("Y".equals(inputVO.getFA_FLAG()) &&  "Y".equals(inputVO.getIA_FLAG())) {
				sql2.append("AND FI.SUPT_SALES_TEAM_ID in ('FA', 'IA') ");
			} else if ("Y".equals(inputVO.getFA_FLAG())) {
				sql2.append("AND FI.SUPT_SALES_TEAM_ID = 'FA' ");
			} else if ("Y".equals(inputVO.getIA_FLAG())) {
				sql2.append("AND FI.SUPT_SALES_TEAM_ID = 'IA' ");
			}
			sql2.append(" )O2 ");
			sql2.append(" ON O1.AS_EMP_ID = O2.AS_EMP_ID ");
			sql2.append(" WHERE O2.AS_EMP_ID is not null ");
		}
		
		condition2.setQueryString(sql2.toString());
		condition2.setObject("meetingDate", new Timestamp(inputVO.getMeetingDate().getTime()));
		List<Map<String, String>> list = dam.exeQuery(condition);
		List<Map<String, String>> list2 = dam.exeQuery(condition2);
		List<Map<String, String>> list3 = list;
		//IF 同分行無駐點人員 THEN 約訪分行跨區
		if(list.isEmpty()){
			list3 = list2;
		}
			
		String v_STATUS = null;
		String  v_AS_EMP_ID = null;
		String v_CROSS_OP_YN = null;
		
		for (Map<String, String> data : list3) {
			if (StringUtils.isNotBlank(data.get("ONSITE_BRH")) || StringUtils.isNotBlank(data.get("BRANCH_NBR"))) {
				//同分行:分行主管覆核 
				if (ObjectUtils.equals(list3,list)){
					v_CROSS_OP_YN = "N";
					v_STATUS = "01";
					v_AS_EMP_ID = data.get("AS_EMP_ID");					
				//不同分行
				} else {
					QueryConditionIF condition_a = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer sql_a = new StringBuffer();
					
					sql_a.append("SELECT BRANCH_AREA_ID FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE BRANCH_NBR = :aa ");
					condition_a.setQueryString(sql_a.toString());
					condition_a.setObject("aa", data.get("ONSITE_BRH"));
					List<Map<String, String>> list_a = dam.exeQuery(condition_a);
					
					QueryConditionIF condition_b = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer sql_b = new StringBuffer();
					
					sql_b.append("SELECT BRANCH_AREA_ID FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE BRANCH_NBR = :bb ");
					condition_b.setQueryString(sql_b.toString());
					condition_b.setObject("bb", data.get("BRANCH_NBR"));
					List<Map<String, String>> list_b = dam.exeQuery(condition_b);
					
					//同營運區 不同分行:營運區督導覆核
					if (StringUtils.equals(list_a.get(0).get("BRANCH_AREA_ID"), list_b.get(0).get("BRANCH_AREA_ID"))) {
						v_CROSS_OP_YN = "N";
						v_STATUS = "02";
						v_AS_EMP_ID = data.get("AS_EMP_ID");
					//不同營運區 不同分行:業處處主管覆核
					} else {
						v_CROSS_OP_YN = "Y";
						v_STATUS = "03";
						v_AS_EMP_ID = data.get("AS_EMP_ID");
					}
				}
			//分行無資料(轄區輔銷人員未設定駐點行, 直接走分行主管, 正常應不會沒資料)
			} else {
				v_CROSS_OP_YN = "N";
				v_STATUS = "01";
				v_AS_EMP_ID = data.get("AS_EMP_ID");
			}
			//新增/編輯
			if(ObjectUtils.equals(inputVO.getMODE(), "add")){
				TBCRM_WKPG_AS_MASTVO vo = new TBCRM_WKPG_AS_MASTVO();

				vo.setSEQ(getTBCRM_WKPG_AS_MAST_SEQ());
				vo.setAS_EMP_ID(v_AS_EMP_ID);
				vo.setCUST_ID(inputVO.getCUST_ID());
				vo.setAO_CODE(inputVO.getAO_CODE());

				if ("Y".equals(inputVO.getFA_FLAG()) && "Y".equals(inputVO.getIA_FLAG())) {
					vo.setAS_TYPE("A");
				} else if ("Y".equals(inputVO.getFA_FLAG())) {
					vo.setAS_TYPE("F");
				} else if ("Y".equals(inputVO.getIA_FLAG())) {
					vo.setAS_TYPE("I");
				}
				vo.setSALES_PLAN_SEQ(getBigDecimal(inputVO.getSEQ()));
				vo.setAS_DATE(new Timestamp(inputVO.getMeetingDate().getTime()));
				vo.setAS_DATETIME_BGN(inputVO.getMeetingTimeS());
				vo.setAS_DATETIME_END(inputVO.getMeetingTimeE());
				vo.setVISIT_PURPOSE(inputVO.getVisit_purpose());
				
				if (StringUtils.isNotBlank(inputVO.getVisit_purpose_other())){
					vo.setVISIT_PURPOSE_OTHER(inputVO.getVisit_purpose_other());
				}
				
				vo.setKEY_ISSUE(inputVO.getKey_issue());
				vo.setCROSS_OP_YN(v_CROSS_OP_YN);
				vo.setSTATUS(v_STATUS);

				dam.create(vo);
			//編輯
			}else if(ObjectUtils.equals(inputVO.getMODE(), "upd")){
				TBCRM_WKPG_AS_MASTVO vo = new TBCRM_WKPG_AS_MASTVO();
				vo = (TBCRM_WKPG_AS_MASTVO) dam.findByPKey(TBCRM_WKPG_AS_MASTVO.TABLE_UID, inputVO.getSEQ2());
				if (vo != null) {
					vo.setAS_EMP_ID(v_AS_EMP_ID);
					vo.setCUST_ID(inputVO.getCUST_ID());
					vo.setAO_CODE(inputVO.getAO_CODE());

					if ("Y".equals(inputVO.getFA_FLAG()) && "Y".equals(inputVO.getIA_FLAG())) {
						vo.setAS_TYPE("A");
					} else if ("Y".equals(inputVO.getFA_FLAG())) {
						vo.setAS_TYPE("F");
					} else if ("Y".equals(inputVO.getIA_FLAG())) {
						vo.setAS_TYPE("I");
					}
					
					vo.setSALES_PLAN_SEQ(getBigDecimal(inputVO.getSEQ()));
					vo.setAS_DATE(new Timestamp(inputVO.getMeetingDate().getTime()));
					vo.setAS_DATETIME_BGN(inputVO.getMeetingTimeS());
					vo.setAS_DATETIME_END(inputVO.getMeetingTimeE());
					vo.setVISIT_PURPOSE(inputVO.getVisit_purpose());
					
					if (StringUtils.isNotBlank(inputVO.getVisit_purpose_other())){
						vo.setVISIT_PURPOSE_OTHER(inputVO.getVisit_purpose_other());
					}
					
					vo.setKEY_ISSUE(inputVO.getKey_issue());
					vo.setCROSS_OP_YN(v_CROSS_OP_YN);
					v_STATUS = "0A" + v_STATUS.substring(1,2);
					vo.setSTATUS(v_STATUS);

					dam.update(vo);
				}	
			}
		}
		this.sendRtnObject(null);
	}
		
	//查詢陪訪紀錄被退回的資料
	public void cancel(Object body, IPrimitiveMap header) throws JBranchException {	
		//查詢
		CRM1242InputVO inputVO = (CRM1242InputVO) body;
		CRM1242OutputVO return_VO = new CRM1242OutputVO();
		dam = this.getDataAccessManager();
		//PRI
		String PRI = "";
		if(!StringUtils.isBlank(ObjectUtils.toString(inputVO.getPriID()))){
			PRI = inputVO.getPriID();
		}
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT * FROM TBCRM_WKPG_AS_MAST ");
		sql.append(" WHERE 1 = 1 ");
		sql.append(" AND AS_EMP_ID = :LoginID ");
		sql.append(" AND NOT_REMIND_YN = 'Y' ");
		
		//輔銷
		if (ObjectUtils.equals(PRI, "014") || ObjectUtils.equals(PRI, "015") ||
			ObjectUtils.equals(PRI, "023") || ObjectUtils.equals(PRI, "024")){
			sql.append(" AND STATUS IN ( '1A', '2A', '3A', '5C')  ");
		}
		//理專
		else{
			if (inputVO.getAOlist().size() > 0){
				condition.setObject("ao_code", inputVO.getAOlist());
				sql.append(" AND AO_CODE IN ( :ao_code) ");
				sql.append(" AND STATUS IN ( '1R', '2R', '3R', '4A','4C' )  ");
			}
		}
		
		condition.setObject("LoginID", inputVO.getLoginID());
		condition.setQueryString(sql.toString());
		List list = dam.exeQuery(condition);
		return_VO.setResultList(list);
		
		//更改
		List<Map<String,Object>> updatelist = dam.exeQuery(condition);
		for(Map<String,Object> map:updatelist){
			TBCRM_WKPG_AS_MASTVO vo = new TBCRM_WKPG_AS_MASTVO();
			vo = (TBCRM_WKPG_AS_MASTVO) dam.findByPKey(TBCRM_WKPG_AS_MASTVO.TABLE_UID, map.get("SEQ").toString());
			if (vo != null) {
				vo.setNOT_REMIND_YN("N");
				dam.update(vo);
			}	
		}
		
		this.sendRtnObject(return_VO);
	}
	
	/**產生seq No */
	private String getTBCRM_WKPG_AS_MAST_SEQ() throws JBranchException {
		  SerialNumberUtil seq = new SerialNumberUtil();
		  String seqNum = "";
		  try{
		    seqNum = seq.getNextSerialNumber("TBCRM_WKPG_AS_MAST");
		  }
		  catch(Exception e){
		   seq.createNewSerial("TBCRM_WKPG_AS_MAST", "0000000000", null, null, null, 6, new Long("99999999"), "y", new Long("0"), null);
		   seqNum = seq.getNextSerialNumber("TBCRM_WKPG_AS_MAST");
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