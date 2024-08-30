package com.systex.jbranch.app.server.fps.pms207;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms110.PMS110InputVO;
import com.systex.jbranch.app.server.fps.pms201.PMS201OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;


/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :預估收益<br>
 * Comments Name : PMS207.java<br>
 * Author : Frank<br>
 * Date :2016年05月28日 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月12日<br>
 */
@Component("pms207")
@Scope("request")
public class PMS207 extends BizLogic {
//	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS207.class);

	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS207InputVO inputVO = (PMS207InputVO) body;
		PMS207OutputVO outputVO = new PMS207OutputVO();
//		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = this.getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		try {
			/** 查詢 最後統計日 **/

			StringBuffer sql_d = new StringBuffer("select ");
			sql_d.append("substr(DATA_DATE, 1,4)||'/'||substr(DATA_DATE, 5,2)||'/'||substr(DATA_DATE, 7,2) as DATA_DATE ");
			sql_d.append("from TBPMS_AO_DIFF_DAY ");
			condition.setQueryString(sql_d.toString());

			outputVO.setDateList(this.getDataAccessManager().exeQuery(condition));
			/** END **/

			/** 查詢 預估報表主檔 **/
			StringBuffer sql_m = new StringBuffer();
			sql_m.append(" select substr(YEARMON, 1, 4) || '/' || substr(YEARMON, 5, 2) as YEARMON, ");
			sql_m.append(" AO_CODE, ");
			sql_m.append(" MAS.EMP_ID, ");
			sql_m.append(" EMP_NAME, ");
			sql_m.append(" AO_JOB_RANK, ");
			sql_m.append(" ACT_AMT_NTD, ");
			sql_m.append(" ACT_ROI_NTD, ");
			sql_m.append(" TARGET_AMT, ");
			sql_m.append(" EST_RATE, ");
			sql_m.append(" ACT_RATE, ");
			sql_m.append(" REC.JOB_RANK, ");
			sql_m.append(" PRO.PRODUCT_GOALS ");
			sql_m.append(" from TBPMS_AO_DIFF_MAST MAS ");
			sql_m.append(" LEFT JOIN ( ");
			sql_m.append(" SELECT EMP_ID, ");
			sql_m.append(" JOB_RANK ");
			sql_m.append(" FROM TBPMS_EMPLOYEE_REC_N) REC ");
			sql_m.append(" ON MAS.EMP_ID = REC.EMP_ID ");
			sql_m.append(" LEFT JOIN ( ");
			sql_m.append(" SELECT JOB_TITLE_ID, ");
			sql_m.append(" PRODUCT_GOALS, ");
			sql_m.append(" SUBSTR(OL_TITLE,2,1) AS OL_TITLE ");
			sql_m.append(" FROM TBPMS_PRO_FUN_TR_PROD_SET ");
			sql_m.append(" WHERE DATA_YEARMON = :yearmonth) PRO ");
			sql_m.append(" ON REC.JOB_RANK = PRO.OL_TITLE ");
			sql_m.append(" AND PRO.JOB_TITLE_ID = MAS.AO_JOB_RANK ");
			sql_m.append(" where 1=1 ");
			
			// 計劃月份
			sql_m.append("and YEARMON = :yearmonth ");
			condition.setObject("yearmonth", inputVO.getDataMonth());
			
			// 理專
			sql_m.append("and AO_CODE = :ao_code ");
			condition.setObject("ao_code", inputVO.getAo_code());
			
			condition.setQueryString(sql_m.toString());
			
			outputVO.setMastList(this.getDataAccessManager().exeQuery(condition));
			/** END **/

			/** 查詢 週統計 **/
			condition = this.getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql_w = new StringBuffer();
			sql_w.append("select YEARMON, AO_CODE, EMP_ID, ");
			sql_w.append("EST_AMT, ACT_AMT, EST_ROI, ACT_ROI, ");
		 	sql_w.append("('第' || substr(MTH_WEEK, -1, 1) || '週') as WORK_DT, ");
			sql_w.append("MTH_WEEK as WKDT ");
			sql_w.append("from TBPMS_AO_DIFF_DTL_WEEK ");
			
			sql_w.append("where 1=1 ");
			//週統計塞選
			sql_w.append(" AND ('第' || substr(MTH_WEEK, -1, 1) || '週') <>'第A週'  ");
			// 計劃月份
			sql_w.append("and YEARMON = :yearmonth ");
			condition.setObject("yearmonth", inputVO.getDataMonth());
			
			// 理專
			sql_w.append("and AO_CODE = :ao_code ");
			condition.setObject("ao_code", inputVO.getAo_code());

			condition.setQueryString(sql_w.toString());
			
			outputVO.setDiffList_w(this.getDataAccessManager().exeQuery(condition));
			/** END **/
			
			/** 查詢 日統計 **/
			condition = this.getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql_d = new StringBuffer();
			sql_d.append("select YEARMON, AO_CODE, EMP_ID, ");
			sql_d.append("EST_AMT, ACT_AMT, EST_ROI, ACT_ROI, ");
		
			sql_d.append("substr(DATA_DATE, 5, 2) || '/' || substr(DATA_DATE, -2, 2) as WORK_DT, ");
			sql_d.append("DATA_DATE as WKDT ");
			sql_d.append("from TBPMS_AO_DIFF_DTL_DAY ");
			
			sql_d.append("where 1=1 ");
			//日統計 塞選
			sql_d.append(" AND  substr(DATA_DATE, -2, 2) <>'AA'  ");
			// 計劃月份
			sql_d.append("and YEARMON = :yearmonth ");
			condition.setObject("yearmonth", inputVO.getDataMonth());
			
			// 理專
			sql_d.append("and AO_CODE = :ao_code ");
			condition.setObject("ao_code", inputVO.getAo_code());

			condition.setQueryString(sql_d.toString());
			
			outputVO.setDiffList_d(this.getDataAccessManager().exeQuery(condition));
			/** END **/

			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**
	 * 查詢目標資訊
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryTargetSale(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		String date = sdf.format(new Date().getTime());
		
		PMS207InputVO inputVO =  (PMS207InputVO)body;
		PMS207OutputVO outputVO = new PMS207OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = this.getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		ArrayList<String> sql_list = new ArrayList<String>();
		
		StringBuffer sql = new StringBuffer();

		sql.append("   SELECT    dm.ao_code,                               ");
		sql.append("             dm.target_amt,                            ");
		sql.append("             dm.est_rate,                              ");
		sql.append("             dm.act_rate,                              ");
		sql.append("             dr.week_start_date,                       ");
		sql.append("             dr.week_end_date                          ");
		sql.append("   FROM      tbpms_date_rec dr                         ");
		sql.append("   left join tbpms_ao_diff_mast dm                     ");
		sql.append("   ON        dm.yearmon = substr(:weekStartDate, 0, 6) ");
		sql.append("   WHERE     dr.data_date = :weekStartDate             ");
		sql.append("   AND     dm.AO_CODE = :AO_CODE             		   ");
		
		condition.setObject("weekStartDate", inputVO.getDataMonth()+"01");
		condition.setObject("AO_CODE", inputVO.getAo_code());
		
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = this.getDataAccessManager().exeQuery(condition);
		outputVO.setEmpDtl(SEQLIST);
		sendRtnObject(outputVO);
	}
	

	public void queryDetail(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS207InputVO inputVO = (PMS207InputVO) body;
		PMS207OutputVO outputVO = new PMS207OutputVO();
		QueryConditionIF condition = this.getDataAccessManager().getQueryCondition();
		try {

			/** 查詢 客戶經營計劃表 **/
			ArrayList<String> sql_list_c = new ArrayList<String>();
			StringBuffer sql_c = new StringBuffer();
			sql_c.append("select to_char(a.A_DATE, 'YYYY/MM/DD') as A_DATE, ");
			sql_c.append("to_char(a.M_DATE, 'YYYY/MM/DD') as M_DATE,  ");
			sql_c.append("a.CUST_NAME as CUST_NAME_E, ");
			sql_c.append("a.PRD_TYPE as PRD_TYPE_E, a.AMT as AMT_E, a.ROI as ROI_E, ");
			sql_c.append("to_char(b.C_DATE, 'YYYY/MM/DD') as C_DATE,  ");
			sql_c.append("b.CUST_NAME as CUST_NAME_A, b.PRD_TYPE as PRD_TYPE_A, ");
			sql_c.append("b.AMT as AMT_A, b.ROI as ROI_A from ");
			if (inputVO.getSttType().equals("week")) {
				sql_c.append("(select * from TBPMS_AO_DIFF_WEEK_CUST ");
				sql_c.append("where TYPE='1' and YEARMON = ? ");
				sql_c.append("and EMP_ID = ? and MTH_WEEK = ? ) a ");
				sql_c.append("full outer join ");
				sql_c.append("(select * from TBPMS_AO_DIFF_WEEK_CUST ");
				sql_c.append("where TYPE='2' and YEARMON = ? ");
				sql_c.append("and EMP_ID = ? and MTH_WEEK = ? ) b ");
				sql_c.append("on a.MTH_WEEK = b.MTH_WEEK ");
			} else {
				sql_c.append("(select * from TBPMS_AO_DIFF_DAY_CUST ");
				sql_c.append("where TYPE='1' and YEARMON = ? ");
				sql_c.append("and EMP_ID = ? and DATA_DATE = ? ) a ");
				sql_c.append("full outer join ");
				sql_c.append("(select * from TBPMS_AO_DIFF_DAY_CUST ");
				sql_c.append("where TYPE='2' and YEARMON = ? ");
				sql_c.append("and EMP_ID = ? and DATA_DATE = ? ) b ");
				sql_c.append("on a.DATA_DATE = b.DATA_DATE ");
			}
			sql_list_c.add(inputVO.getDataMonth());
			sql_list_c.add(inputVO.getEmp_id());
			sql_list_c.add(inputVO.getWork_dt());
			sql_list_c.add(inputVO.getDataMonth());
			sql_list_c.add(inputVO.getEmp_id());
			sql_list_c.add(inputVO.getWork_dt());

			sql_c.append("and a.CUST_ID = b.CUST_ID ");
			sql_c.append("and a.YEARMON = b.YEARMON ");
			sql_c.append("and a.EMP_ID = b.EMP_ID ");
			sql_c.append("order by a.A_DATE ");

			condition.setQueryString(sql_c.toString());
			for (int sql_i = 0; sql_i < sql_list_c.size(); sql_i++) {
				condition.setString(sql_i + 1, sql_list_c.get(sql_i));
			}
			outputVO.setDfCustList(this.getDataAccessManager().exeQuery(condition));
			/** END **/

			/** 查詢 產品銷售差異 **/
			QueryConditionIF condition2 = this.getDataAccessManager().getQueryCondition();
			ArrayList<String> sql_list_p = new ArrayList<String>();
			StringBuffer sql_p = new StringBuffer("select ");
			sql_p.append("PRD_CATE,EST_AMT, EST_ROI, ACT_AMT, ACT_ROI, GAP_AMT, GAP_ROI ");
			if (inputVO.getSttType().equals("week")) {
				sql_p.append("from TBPMS_AO_DIFF_WEEK_PRD ");
			} else {
				sql_p.append("from TBPMS_AO_DIFF_DAY_PRD ");
			}
			sql_p.append("where 1=1 ");
			// 計劃月份
			sql_p.append("and YEARMON = ? ");
			sql_list_p.add(inputVO.getDataMonth());
			// AO CODE
			sql_p.append("and AO_CODE = ? ");
			sql_list_p.add(inputVO.getAo_code());
			// 理專
			sql_p.append("and EMP_ID = ? ");
			sql_list_p.add(inputVO.getEmp_id());
			// 月週-工作日期
			if (inputVO.getSttType().equals("week")) {
				sql_p.append("and MTH_WEEK = ? ");
			} else {
				sql_p.append("and DATA_DATE = ? ");
			}
			sql_list_p.add(inputVO.getWork_dt());
			sql_p.append("and PRD_CATE not in ('08', '09', '10', '11', '13', '14', '15') ");
			sql_p.append("order by PRD_CATE ");
			condition2.setQueryString(sql_p.toString());
			for (int sql_i = 0; sql_i < sql_list_p.size(); sql_i++) {
				condition2.setString(sql_i + 1, sql_list_p.get(sql_i));
			}
			outputVO.setDfPrdList1(this.getDataAccessManager().exeQuery(condition2));
			// ---------*****---------------------//

			QueryConditionIF condition3 = this.getDataAccessManager().getQueryCondition();
			ArrayList<String> sql_list_p1 = new ArrayList<String>();
			StringBuffer sql_p1 = new StringBuffer("select ");
			sql_p1.append("PRD_CATE,EST_AMT, EST_ROI, ACT_AMT, ACT_ROI, GAP_AMT, GAP_ROI ");
			if (inputVO.getSttType().equals("week")) {
				sql_p1.append("from TBPMS_AO_DIFF_WEEK_PRD ");
			} else {
				sql_p1.append("from TBPMS_AO_DIFF_DAY_PRD ");
			}
			sql_p1.append("where 1=1 ");
			// 計劃月份
			sql_p1.append("and YEARMON = ? ");
			sql_list_p1.add(inputVO.getDataMonth());
			// AO CODE
			sql_p1.append("and AO_CODE = ? ");
			sql_list_p1.add(inputVO.getAo_code());
			// 理專
			sql_p1.append("and EMP_ID = ? ");
			sql_list_p1.add(inputVO.getEmp_id());
			// 月週-工作日期
			if (inputVO.getSttType().equals("week")) {
				sql_p1.append("and MTH_WEEK = ? ");
			} else {
				sql_p1.append("and DATA_DATE = ? ");
			}
			sql_list_p1.add(inputVO.getWork_dt());
			sql_p1.append("and PRD_CATE in ('08', '09', '10', '13', '15') ");
			sql_p1.append("order by PRD_CATE ");
			condition3.setQueryString(sql_p1.toString());
			for (int sql_i = 0; sql_i < sql_list_p1.size(); sql_i++) {
				condition3.setString(sql_i + 1, sql_list_p1.get(sql_i));
			}
			outputVO.setDfPrdList2(this.getDataAccessManager().exeQuery(condition3));

			// ---------*****---------------------//
			QueryConditionIF condition4 = this.getDataAccessManager().getQueryCondition();
			ArrayList<String> sql_list_p2 = new ArrayList<String>();
			StringBuffer sql_p2 = new StringBuffer("select ");
			sql_p2.append("PRD_CATE,EST_AMT, EST_ROI, ACT_AMT, ACT_ROI, GAP_AMT, GAP_ROI ");
			if (inputVO.getSttType().equals("week")) {
				sql_p2.append("from TBPMS_AO_DIFF_WEEK_PRD ");
			} else {
				sql_p2.append("from TBPMS_AO_DIFF_DAY_PRD ");
			}
			sql_p2.append("where 1=1 ");
			// 計劃月份
			sql_p2.append("and YEARMON = ? ");
			sql_list_p2.add(inputVO.getDataMonth());
			// AO CODE
			sql_p2.append("and AO_CODE = ? ");
			sql_list_p2.add(inputVO.getAo_code());
			// 理專
			sql_p2.append("and EMP_ID = ? ");
			sql_list_p2.add(inputVO.getEmp_id());
			// 月週-工作日期
			if (inputVO.getSttType().equals("week")) {
				sql_p2.append("and MTH_WEEK = ? ");
			} else {
				sql_p2.append("and DATA_DATE = ? ");
			}
			sql_list_p2.add(inputVO.getWork_dt());
			sql_p2.append("and PRD_CATE in ('11', '14') ");
			sql_p2.append("order by PRD_CATE ");
			condition4.setQueryString(sql_p2.toString());
			for (int sql_i = 0; sql_i < sql_list_p2.size(); sql_i++) {
				condition4.setString(sql_i + 1, sql_list_p2.get(sql_i));
			}
			outputVO.setDfPrdList3(this.getDataAccessManager().exeQuery(condition4));

			/** END **/
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	
	/**
	 * 取得EST_AMT
	 * *
	 * @throws JBranchException 
	 * @throws DAOException 
	 */
	private List<Map<String, Object>> getEstAmt(String yearMon) throws DAOException, JBranchException{
//		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = this.getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		sql.append(" select SUM(EST_AMT) as predictPcnt FROM TBPMS_SALES_PLAN ");
		sql.append(" WHERE BRANCH_NBR= :brh  AND BRANCH_AREA_ID= :area  AND EMP_ID= :emp_id and PLAN_YEARMON = :yearMon");
		String aa  = getMetaDate().get("brh");
		String bb=  getMetaDate().get("area");
		String cc = getMetaDate().get("loginID");
		condition.setObject("emp_id", getMetaDate().get("loginID"));
		condition.setObject("yearMon", yearMon);
		condition.setObject("brh", getMetaDate().get("brh"));
		condition.setObject("area", getMetaDate().get("area"));
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = this.getDataAccessManager().exeQuery(condition);
		return SEQLIST;
	}
	
	/**
	 * 登入ID : loginID<br>
	 * 登入姓名 :　loginName<br>
	 * 登入分行 : brh<br>
	 * 登入營運區 : area
	 * */
	private Map<String,String> getMetaDate() throws JBranchException{
		 String brh =(String) getUserVariable(FubonSystemVariableConsts.LOGINBRH);
		 String area =(String) getUserVariable(FubonSystemVariableConsts.LOGIN_AREA);
		 ArrayList<String> aoCode = (ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
		 String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		 String loginName =(String) getUserVariable(FubonSystemVariableConsts.LOGINNAME);
		 Map<String,String> val = new HashMap<String, String>();
		 val.put("brh", brh);
		 val.put("area", area);
		 val.put("loginID", loginID);
		 val.put("aoCode :", (aoCode.size()!=0)?aoCode.get(0).toString():"");
		 val.put("loginName :", loginName==null?"":loginName );
		 return val;
		
	}

}