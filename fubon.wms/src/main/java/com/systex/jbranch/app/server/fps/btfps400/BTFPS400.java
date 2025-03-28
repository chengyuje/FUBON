package com.systex.jbranch.app.server.fps.btfps400;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.cmjlb014.ResultObj;
import com.systex.jbranch.app.server.fps.fpsjlb.FPSJLB;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("btfps400")
@Scope("prototype")
public class BTFPS400 extends BizLogic {
	
	@Autowired@Qualifier("fpsjlb")
	private FPSJLB fpsjlb;
    private QueryConditionIF condition;
    private DataAccessManager dam = null;
    /**
     * Batch Enterance
     *
     * @param body
     * @param header
     * @throws Exception
     */
    public void execute(Object body, IPrimitiveMap<?> header) throws Exception {
    	dam = this.getDataAccessManager();
    	condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

        //抓取對應的參數 
		String vr_param_no = BTFPS400_1(dam);
		Double vr_risk_atr_1 = BTFPS400_2(dam, vr_param_no, "1");
        Double vr_risk_atr_2 = BTFPS400_2(dam, vr_param_no, "2");
        Double vr_risk_atr_3 = BTFPS400_2(dam, vr_param_no, "3");

       	System.out.println("vr_risk_atr_1:"+vr_risk_atr_1);
    	System.out.println("vr_risk_atr_2:"+vr_risk_atr_2);
    	System.out.println("vr_risk_atr_3:"+vr_risk_atr_3);

        //抓取理財規劃已成立未失效的名單，且對應該客戶有理顧
		List<Map<String, Object>> list = BTFPS400_3(dam);

		//組裝資料 
        Map<String, Map<String, Object>> m = BTFPS400_4(list);

        //依每個c.plan_id, 將c.prd_id (產品代碼)、 c. PORTFOLIO_RATIO (權重)、MFD (基金)傳入到 FPSJLB理財規劃核心元件 
		for(String plan_id : m.keySet()){
        	Map<String, Object> map = m.get(plan_id);
        	Double vr_return_vol_rate = (double) 0;
        	try{
	        	ResultObj result = fpsjlb.getPortfolioVolatility((List<Map<String, Object>>) map.get("FBSJLB"));
	        	vr_return_vol_rate = result.getResultArray()[0][0];
        	}catch(Exception e){
        		vr_return_vol_rate = (double) 0;
        	}
        	
        	
        	String risk_attr = map.get("CUST_RISK_ATR").toString();
        	String cust_id = map.get("CUST_ID").toString();

        	System.out.println("plan_id:"+plan_id);
        	System.out.println("fbsjlb:"+map.get("FBSJLB").toString());
        	System.out.println("vr_return_vol_rate:"+vr_return_vol_rate);
        	System.out.println("risk_attr:"+risk_attr);
//        	System.out.println("cust_id:"+cust_id);

        	if((risk_attr.equals("1") && vr_return_vol_rate > vr_risk_atr_1) ||
        	   (risk_attr.equals("2") && vr_return_vol_rate > vr_risk_atr_2) ||
        	   (risk_attr.equals("3") && vr_return_vol_rate > vr_risk_atr_3)){
        			System.out.println("************BTFPS_5*************");
        			System.out.println("plan_id:"+plan_id);
//                	System.out.println("cust_id:"+cust_id);
                	System.out.println("************BTFPS_5*************");
        			BTFPS_5(dam, cust_id, plan_id, vr_return_vol_rate);
        	}
        	System.out.println("********************************");
        }

		//針對特定理財規劃以外的基股商品
		//抓取參數
		Double vr_fund_aum = BTFPS400_6(dam);

		list = BTFPS400_7(dam, vr_fund_aum);
		
		//組裝資料
		m = btfps400_8(dam, list, vr_fund_aum);

		for(String cust_id : m.keySet()){
        	Map<String, Object> map = m.get(cust_id);
        	try{
        		ResultObj temp = fpsjlb.getPortfolioVolatility((List<Map<String, Object>>) map.get("FBSJLB"));
            	Double vr_return_vol_rate = temp.getResultArray()[0][0];
            	String risk_attr = map.get("RISK_ATTR").toString();

            	if((risk_attr.equals("1") && vr_return_vol_rate > vr_risk_atr_1) ||
            	   (risk_attr.equals("2") && vr_return_vol_rate > vr_risk_atr_2) ||
            	   (risk_attr.equals("3") && vr_return_vol_rate > vr_risk_atr_3)){
            			BTFPS_5(dam, cust_id, "FUND_AUM", vr_return_vol_rate);
            	}
        	}catch(Exception e){
        		Double vr_return_vol_rate = (double) 0;
        		String risk_attr = map.get("RISK_ATTR").toString();
              	if((risk_attr.equals("1") && 0 > vr_risk_atr_1) ||
             	   (risk_attr.equals("2") && 0 > vr_risk_atr_2) ||
             	   (risk_attr.equals("3") && 0 > vr_risk_atr_3)){
             			BTFPS_5(dam, cust_id, "FUND_AUM", vr_return_vol_rate);
             	}
        	}
        }

        BTFPS400_9(dam);


		list = BTFPS400_10(dam);

        for(Map map : list){
        	String cust_id = map.get("CUST_ID").toString();
        	String plan_id = map.get("PLAN_ID").toString();

        	int count1 = BTFPS400_11(dam, cust_id, plan_id, -6);
        	int count2 = BTFPS400_11(dam, cust_id, plan_id, -5);
        	int count3 = BTFPS400_11(dam, cust_id, plan_id, -4);
        	int count4 = BTFPS400_11(dam, cust_id, plan_id, -3);
        	int count5 = BTFPS400_11(dam, cust_id, plan_id, -2);
        	int count6 = BTFPS400_11(dam, cust_id, plan_id, -1);

        	sb = new StringBuffer();
            String sql;
        	if(count1 > 0 && count2 > 0 && count3 > 0 && count4 > 0 && count5 > 0 && count6 > 0) {
        		sb = new StringBuffer();
        		sb.append("UPDATE TBFPS_CUST_VOL_LIST ");
        		sb.append("SET notify_flag = 'Y', ");
        		sb.append("    modifier = 'BFPS400' ");
        		sb.append("WHERE TRUNC(sou_date)  = TRUNC(sysdate) ");
        		sb.append("AND cust_id     = '"+cust_id+"' ");
        		sb.append("AND plan_id     = '"+plan_id+"' ");
                sql = sb.toString();
        		condition.setQueryString(sql);
                dam.exeUpdate(condition);
        	}
        }

        list = BTFPS400_12(dam);

        sb = new StringBuffer();
		String sql;
		sb = new StringBuffer();
		sb.append("select TBCAM_SFA_CAMP_TEMP_SEQ.nextval from dual");
		sql = sb.toString();
        condition.setQueryString(sql);
        List<Map<String, Object>> list2 = dam.exeQuery(condition);
        String TBCAM_SFA_CAMP_TEMP_SEQ = list2.get(0).get("NEXTVAL").toString();
        BTFPS400_13(dam, TBCAM_SFA_CAMP_TEMP_SEQ);

    	for(Map map : list){
        	sb = new StringBuffer();
    		sb.append("select SFA.TBCAM_SFA_LEADS_TEMP_SEQ.nextval from dual");
    		sql = sb.toString();
            condition.setQueryString(sql);
            list2 = dam.exeQuery(condition);
            String TBCAM_SFA_LEADS_TEMP_SEQ = list2.get(0).get("NEXTVAL").toString();

        	BTFPS400_14(dam, map, TBCAM_SFA_CAMP_TEMP_SEQ,
					TBCAM_SFA_LEADS_TEMP_SEQ);

        	BTFPS400_15(dam, map, TBCAM_SFA_LEADS_TEMP_SEQ);
    	}
    	
    	System.out.println("complete!");
    }

	private void BTFPS400_15(DataAccessManager dam, Map map, String TBCAM_SFA_LEADS_TEMP_SEQ)
			throws DAOException, JBranchException {
		StringBuffer sb;
		String sql;
		sb = new StringBuffer();
		sb.append("insert into TBCAM_SFA_LEADS_VAR_SYS ( ");
		sb.append("    SFA_LEAD_ID, ");
		sb.append("    cust_id, ");
		sb.append("    creator, ");
		sb.append("    modifier, ");
		sb.append("    createtime, ");
		sb.append("    lastupdate, ");
		sb.append("    seq_no, ");
		sb.append("    field_name, ");
		sb.append("    field_value ");
		sb.append(") ");
		sb.append("values( ");
		sb.append("    'SYS'||LPAD("+TBCAM_SFA_LEADS_TEMP_SEQ+", 22, 0), ");
		sb.append("    '"+map.get("CUST_ID")+"', ");
		sb.append("    'BFPS400', ");
		sb.append("    'BFPS400', ");
		sb.append("    sysdate, ");
		sb.append("    sysdate, ");
		sb.append("    1, ");
		sb.append("    '客戶名稱', ");
		sb.append("    '"+map.get("CUST_NAME")+"' ");
		sb.append(") ");
		sql = sb.toString();
		condition.setQueryString(sql);
		dam.exeUpdate(condition);
	}

	private void BTFPS400_14(DataAccessManager dam, Map map,
			String TBCAM_SFA_CAMP_TEMP_SEQ, String TBCAM_SFA_LEADS_TEMP_SEQ) throws DAOException, JBranchException {
		StringBuffer sb;
		String sql;
		sb = new StringBuffer();
		sb.append("insert into TBCAM_SFA_LEADS_TEMP ( ");
		sb.append("    seqno,  ");
		sb.append("    imp_seqno, ");
		sb.append("    SFA_LEAD_ID, ");
		sb.append("    cust_id, ");
		sb.append("    BRANCH_ID, ");
		sb.append("    emp_id, ");
		sb.append("    start_date, ");
		sb.append("    end_Date, ");
		sb.append("    creator, ");
		sb.append("    modifier, ");
		sb.append("    createtime, ");
		sb.append("    lastupdate ");
		sb.append(")values( ");
		sb.append("    '"+TBCAM_SFA_LEADS_TEMP_SEQ+"', ");
		sb.append("    '"+TBCAM_SFA_CAMP_TEMP_SEQ+"', ");
		sb.append("    'SYS'||LPAD("+TBCAM_SFA_LEADS_TEMP_SEQ+", 22, 0) , ");
		sb.append("    '"+map.get("CUST_ID").toString()+"', ");
		sb.append("    '"+map.get("TERRITORY_ID").toString()+"', ");
		sb.append("    '"+map.get("AO_ID").toString()+"', ");
		sb.append("    sysdate, ");
		sb.append("    sysdate + 7, ");
		sb.append("    'BFPS400', ");
		sb.append("    'BFPS400', ");
		sb.append("    sysdate, ");
		sb.append("    sysdate ");
		sb.append(") ");
		sql = sb.toString();
		condition.setQueryString(sql);
		dam.exeUpdate(condition);
	}

	private void BTFPS400_13(DataAccessManager dam,
			String TBCAM_SFA_CAMP_TEMP_SEQ) throws DAOException,
			JBranchException {
		StringBuffer sb;
		String sql;
		sb = new StringBuffer();
		sb.append("insert into TBCAM_SFA_CAMP_TEMP ( ");
		sb.append("    			    seqno, ");
		sb.append("    			    CAMPAIGN_ID, ");
		sb.append("    			    STEP_ID, ");
		sb.append("    			    LEAD_SOURCE_ID, ");
		sb.append("    			    CAMP_TYPE, ");
		sb.append("    			    start_date, ");
		sb.append("    			    end_Date, ");
		sb.append("    			    LEAD_RESPONSE_CODE, ");
		sb.append("    			    LEAD_TYPE, ");
		sb.append("    			    CHANNEL, ");
		sb.append("    			    CUST_REPLY_FLAG, ");
		sb.append("    			    MARKETING_FLAG, ");
		sb.append("    			    CAM_DISP_ROLE, ");
		sb.append("    			    AUTO_DISPATCH_FLAG, ");
		sb.append("    			    CAM_STS, ");
		sb.append("    			    CAM_BTH_STS, ");
		sb.append("    			    REMOVE_FLAG, ");
		sb.append("    			    TERRITORY_ID, ");
		sb.append("    			    creator, ");
		sb.append("    			    modifier, ");
		sb.append("    			    createtime, ");
		sb.append("    			    lastupdate, ");
		sb.append("    			    CAMPAIGN_NAME, ");
		sb.append("    			    SALES_PITCH ");
		sb.append("    			)values( ");
		sb.append("    			    '"+TBCAM_SFA_CAMP_TEMP_SEQ+"', ");
		sb.append("    			    'EVENT_REGULAR_VIEW', ");
		sb.append("    			    to_char(sysdate,'YYYYMMDDHH24mi'), ");
		sb.append("    			    '01', ");
		sb.append("    			    '02', ");
		sb.append("    			    sysdate, ");
		sb.append("    			    sysdate + 7, ");
		sb.append("    			    '0000000000', ");
		sb.append("    			    '2', ");
		sb.append("    			    'B01', ");
		sb.append("    			    'N', ");
		sb.append("    			    'N', ");
		sb.append("    			    'B51', ");
		sb.append("    			    'Y', ");
		sb.append("    			    '0', ");
		sb.append("    			    'IN', ");
		sb.append("    			    'N', ");
		sb.append("    			    'H072050000', ");
		sb.append("    			    'BFPS400', ");
		sb.append("    			    'BFPS400', ");
		sb.append("    			    sysdate, ");
		sb.append("    			    sysdate, ");
		sb.append("    			    '波動率超標警示名單通知', ");
		sb.append("    			    '波動率超標警示名單通知' ");
		sb.append("    			) ");
		sql = sb.toString();
		condition.setQueryString(sql);
		dam.exeUpdate(condition);
	}

	private List BTFPS400_12(DataAccessManager dam) throws DAOException,
			JBranchException {
		List<Map<String, Object>> list;
		StringBuffer sb;
		String sql;
		sb = new StringBuffer();
        sb.append("SELECT DISTINCT TRUNC(a.sou_date) sou_date, ");
        sb.append("  a.cust_id, ");
        sb.append("  a.notify_flag, ");
        sb.append("  b.ao_id, ");
        sb.append("  b.cust_name, ");
        sb.append("  c.emp_id, ");
        sb.append("  SUBSTR(c.territory_id, 2, 3) as territory_id ");
        sb.append("FROM TBFPS_CUST_VOL_LIST a ");
        sb.append("JOIN TBSFA_DAT_CUST b ");
        sb.append("ON a.cust_id = b.cust_id ");
        sb.append("JOIN TBSFA_ORG_MEMBER c ");
        sb.append("ON b.ao_id        = c.emp_id ");
        sb.append("WHERE TRUNC(a.sou_date)  = TRUNC(sysdate) ");
        sb.append("AND a.notify_flag = 'Y' ");
        sb.append("GROUP BY TRUNC(a.sou_date), ");
        sb.append("  a.cust_id, ");
        sb.append("  a.notify_flag, ");
        sb.append("  b.ao_id, ");
        sb.append("  b.cust_name, ");
        sb.append("  c.emp_id, ");
        sb.append("  SUBSTR(c.territory_id, 2, 3)  ");
        sql = sb.toString();
        condition.setQueryString(sql);
    	return dam.exeQuery(condition);
	}

    private int BTFPS400_11(DataAccessManager dam, String cust_id, String plan_id, int offset) throws DAOException,
	JBranchException  {
    	StringBuffer sb = new StringBuffer();
    	sb.append("SELECT COUNT(1) AS COUNT ");
    	sb.append("FROM TBFPS_CUST_VOL_LIST t1 ");
    	sb.append("WHERE TRUNC(t1.sou_date) = add_months (TRUNC(sysdate), "+offset+") ");
    	sb.append("AND t1.cust_id    = '"+cust_id+"' ");
    	sb.append("AND t1.plan_id    = '"+plan_id+"'  ");
    	String sql = sb.toString();
    	condition.setQueryString(sql);
    	List<Map<String, Object>> count1_list = dam.exeQuery(condition);
    	return Integer.parseInt(count1_list.get(0).get("COUNT").toString());
    }

	private List BTFPS400_10(DataAccessManager dam) throws DAOException,
			JBranchException {
		StringBuffer sb = new StringBuffer();
        sb.append("SELECT plan_id, cust_id FROM TBFPS_CUST_VOL_LIST a WHERE TRUNC(a.sou_date) = TRUNC(sysdate) ");
        String sql = sb.toString();
        
        condition.setQueryString(sql);
		return dam.exeQuery(condition);
	}

	private void BTFPS400_9(DataAccessManager dam) throws DAOException,
			JBranchException {
		StringBuffer sb = new StringBuffer();
        sb.append("UPDATE TBFPS_CUST_VOL_LIST ");
        sb.append("SET notify_flag = 'N', ");
        sb.append("    modifier = 'BFPS400' ");
        sb.append("WHERE TRUNC(sou_date)  = TRUNC(sysdate) ");
        sb.append("AND cust_id    IN ");
        sb.append("  (SELECT cust_id ");
        sb.append("  FROM TBFPG_CAL_SALES_TASK ");
        sb.append("  WHERE task_date       >= add_months (sysdate, -2) ");
        sb.append("  AND TASK_STATUS       <> 'X' ");
        sb.append("  AND FINANCIAL_PLAN_FLAG= 'Y' ");
        sb.append("  )  ");
        String sql = sb.toString();
		condition.setQueryString(sql);
        dam.exeUpdate(condition);
	}

	private Map<String, Map<String, Object>> btfps400_8(DataAccessManager dam,
			List<Map<String, Object>> list, Double vr_fund_aum)  throws DAOException, JBranchException {
		Map<String, Map<String, Object>> outMap = new HashMap();
		for(Map map : list){
        	String cust_id = map.get("CUST_ID").toString();

        	StringBuffer sb = new StringBuffer();
        	sb.append("SELECT t2.cust_id , ");
        	sb.append("  t2.prd_id, ");
        	sb.append("  t2.AMT_NOW_FOR, ");
        	sb.append("  (t2.AMT_NOW_FOR / "+map.get("TOTAL_FUND_AUM").toString()+") AS prd_ratio, ");
        	sb.append("  t3.CUST_risk_atr ");
        	sb.append("FROM TBSFA_DAT_AST_FUND t2 ");
        	sb.append("JOIN TBCRM_CUST_MAST t3 ");
        	sb.append("ON t2.cust_id        = t3.cust_id ");
        	sb.append("WHERE t2.prd_id NOT IN ");
        	sb.append("  (SELECT c2.fnd_code ");
        	sb.append("  FROM TBFPS_PORTFOLIO_PLAN_SPP_HEAD a2 ");
        	sb.append("  JOIN TBFPS_SPP_PRD_RETURN_HEAD b2 ");
        	sb.append("  ON a2.plan_id = b2.plan_id ");
        	sb.append("  JOIN TBFPS_SPP_PRD_RETURN c2 ");
        	sb.append("  ON b2.plan_id = c2.plan_id ");
        	sb.append("  JOIN TBCRM_CUST_MAST d2 ");
        	sb.append("  ON a2.cust_id         = d2.cust_id ");
        	sb.append("  WHERE (a2.valid_flag <> 'N' ");
        	sb.append("  OR a2.valid_flag     IS NULL) ");
        	sb.append("  AND a2.TRACE_FLAG     = 'Y' ");
        	sb.append("  AND ( b2.status NOT  IN ('D', 'C') ");
        	sb.append("  OR b2.status         IS NULL) ");
        	sb.append("  AND a2.cust_id        = t2.cust_id ");
        	sb.append("  ) ");
        	sb.append("AND '"+cust_id+"' = t2.cust_id  ");
        	String sql = sb.toString();
            condition.setQueryString(sql);
            List<Map<String, Object>>  list2 = dam.exeQuery(condition);

            for(Map map2 : list2){
            	if(!outMap.containsKey(cust_id)){
            		outMap.put(cust_id, new HashMap());
            	}

            	Map<String, Object> custMap = outMap.get(cust_id);
            	custMap.put("RISK_ATTR", map2.get("CUST_RISK_ATR"));

            	if(!custMap.containsKey("FBSJLB")){
            		custMap.put("FBSJLB", new ArrayList<Map<String, Object>>());
            	}

            	List<Map<String, Object>>fbsjlblist = (List<Map<String, Object>>) custMap.get("FBSJLB");	
            	HashMap fbsjlbMap = new HashMap();
            	fbsjlbMap.put("PRD_ID", map2.get("PRD_ID").toString().substring(0, 4));
            	fbsjlbMap.put("PRD_TYPE", "MFD");
            	fbsjlbMap.put("WEIGHT", Double.parseDouble(map2.get("PRD_RATIO").toString()));
            	fbsjlblist.add(fbsjlbMap);
            }
        }
		return outMap;
	}

	private List<Map<String, Object>> BTFPS400_7(DataAccessManager dam, Double vr_fund_aum) throws DAOException,
			JBranchException {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT t1.cust_id , ");
		sb.append("  SUM (t1.AMT_NOW_FOR) AS total_fund_aum ");
		sb.append("FROM TBSFA_DAT_AST_FUND t1 ");
		sb.append("WHERE t1.prd_id NOT IN ");
		sb.append("  (SELECT c1.fnd_code ");
		sb.append("  FROM TBFPS_PORTFOLIO_PLAN_SPP_HEAD a1 ");
		sb.append("  JOIN TBFPS_SPP_PRD_RETURN_HEAD b1 ");
		sb.append("  ON a1.plan_id = b1.plan_id ");
		sb.append("  JOIN TBFPS_SPP_PRD_RETURN c1 ");
		sb.append("  ON b1.plan_id = c1.plan_id ");
		sb.append("  JOIN TBCRM_CUST_MAST d1 ");
		sb.append("  ON a1.cust_id = d1.cust_id ");
		sb.append("  LEFT JOIN TBORG_SALES_AOCODE e1 ");
		sb.append("  ON d1.ao_code           = e1.AO_CODE ");
		sb.append("  WHERE (a1.valid_flag <> 'N' ");
		sb.append("  OR a1.valid_flag      IS NULL) ");
		sb.append("  AND a1.TRACE_FLAG     = 'Y' ");
		sb.append("  AND ( b1.status NOT  IN ('D', 'C') ");
		sb.append("  OR b1.status         IS NULL) ");
		sb.append("  AND ( d1.ao_CODE       <> '' ");
		sb.append("  OR d1.ao_CODE          IS NOT NULL) ");
		sb.append("  AND a1.cust_id        = t1.cust_id ");
		sb.append("  ) ");
		sb.append("GROUP BY t1.cust_id  ");
		sb.append("HAVING SUM(t1.AMT_NOW_FOR) >= "+vr_fund_aum+" ");
		String sql = sb.toString();
        condition.setQueryString(sql);
        return dam.exeQuery(condition);
	}

	private Double BTFPS400_6(DataAccessManager dam) throws DAOException,
			JBranchException {
		List<Map<String, Object>> list;
		StringBuffer sb;
		String sql;
        sb = new StringBuffer();
        sb.append("SELECT NVL(a.fund_aum, 0) * 10000 as fund_aum ");
        sb.append("FROM TBFPS_OTHER_PARA a ");
        sb.append("WHERE a.param_no = ");
        sb.append("  (SELECT param_no ");
        sb.append("  FROM TBFPS_OTHER_PARA_HEAD ");
        sb.append("  WHERE status          = 'A' ");
        sb.append("  AND effect_start_date = ");
        sb.append("    (SELECT MAX (effect_start_date) ");
        sb.append("    FROM TBFPS_OTHER_PARA_HEAD ");
        sb.append("    WHERE status           = 'A' ");
        sb.append("    AND effect_start_date <= sysdate ");
        sb.append("    ) ");
        sb.append("  )  ");
        sql = sb.toString();
        condition.setQueryString(sql);
        list = dam.exeQuery(condition);
        return Double.parseDouble(list.get(0).get("FUND_AUM").toString());
	}

	private void BTFPS_5(DataAccessManager dam, String cust_id, String plan_id, Double vr_return_vol_rate) throws Exception, DAOException,
			JBranchException {
		StringBuffer sb = new StringBuffer();
		sb.append("insert into TBFPS_CUST_VOL_LIST ( ");
		sb.append("    cust_id, ");
		sb.append("    plan_id, ");
		sb.append("    notify_flag, ");
		sb.append("    sou_date, ");
		sb.append("    createtime, ");
		sb.append("    lastupdate, ");
		sb.append("    creator, ");
		sb.append("    modifier, ");
		sb.append("    volatility ");
		sb.append(") values ( ");
		sb.append("    '"+cust_id+"', ");
		sb.append("    '"+plan_id+"', ");
		sb.append("    'Y', ");
		sb.append("    sysdate, ");
		sb.append("    sysdate, ");
		sb.append("    sysdate, ");
		sb.append("    'BFPS400', ");
		sb.append("    'BFPS400', ");
		sb.append("    "+vr_return_vol_rate+" ");
		sb.append(") ");
		String sql = sb.toString();
		condition.setQueryString(sql);
        dam.exeUpdate(condition);
	}

	private Map<String, Map<String, Object>> BTFPS400_4(List<Map<String, Object>> list) {
		Map<String, Map<String, Object>> outMap = new HashMap<String, Map<String, Object>>();

        for(Map map : list){
        	String plan_id = map.get("PLAN_ID").toString();
        	if(!outMap.containsKey(plan_id)){
        		outMap.put(plan_id, new HashMap<String, Object>());
        	}
        	Map<String, Object> planMap = outMap.get(plan_id);        	
        	planMap.put("PLAN_ID", plan_id);
        	planMap.put("CUST_ID", map.get("CUST_ID").toString());
        	planMap.put("RISK_ATTR", map.get("RISK_ATTR").toString());

        	if(!planMap.containsKey("FBSJLB")){
        		planMap.put("FBSJLB", new ArrayList<Map<String, Object>>());
        	}
        	List<Map<String, Object>>fbsjlbList = (List<Map<String, Object>>) planMap.get("FBSJLB");
        	Map<String, Object> fbsjlbMap = new HashMap<String, Object>();
    		fbsjlbMap.put("PRD_ID", map.get("PRD_ID").toString());
        	fbsjlbMap.put("PRD_TYPE", "MFD");
        	fbsjlbMap.put("WEIGHT", Double.parseDouble(map.get("PORTFOLIO_RATIO").toString()) / 100);
        	fbsjlbList.add(fbsjlbMap);
        }
        return outMap;
	}

	private List<Map<String, Object>> BTFPS400_3(DataAccessManager dam)
			throws DAOException, JBranchException {
		StringBuffer sb;
		String sql;
		List<Map<String, Object>> list;
		sb = new StringBuffer();
        sb.append("SELECT c.plan_id, c.prd_id, SUM(NVL(c.portfolio_ratio, 0)) as portfolio_ratio, d.CUST_risk_attr, a.cust_id ");
        sb.append("FROM TBFPS_PORTFOLIO_PLAN_SPP_HEAD a ");
        sb.append("JOIN TBFPS_SPP_PRD_RETURN_HEAD b ");
        sb.append("ON a.plan_id = b.plan_id ");
        sb.append("JOIN TBFPS_SPP_PRD_RETURN c ");
        sb.append("ON b.plan_id = c.plan_id ");
        sb.append("JOIN TBCRM_CUST_MAST d ");
        sb.append("ON a.cust_id = d.cust_id ");
        sb.append("LEFT JOIN TBORG_SALES_AOCODE e ");
        sb.append("ON d.AO_CODE = e.AO_CODE ");
        sb.append("WHERE (a.valid_flag <> 'N' ");
        sb.append("OR a.valid_flag     IS NULL) ");
        sb.append("AND a.TRACE_FLAG     = 'Y' ");
        sb.append("AND ( b.status NOT  IN ('D', 'C') ");
        sb.append("OR b.status         IS NULL) ");
        sb.append("AND ( d.ao_CODE       <> '' ");
        sb.append("OR d.ao_CODE          IS NOT NULL)  ");
        sb.append("GROUP BY c.plan_id, ");
        sb.append("  c.prd_id, ");
        sb.append("  d.CUST_RISK_ATR, ");
        sb.append("  a.cust_id  ");
        sql = sb.toString();
        
        condition.setQueryString(sql);
        list = dam.exeQuery(condition);
		return list;
	}

	/**
	 * 波動類型 = '2' , 
	 * @param dam
	 * @param vr_param_no
	 * @param cust_risk_atr
	 * @return 波動度
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private Double BTFPS400_2(DataAccessManager dam, String vr_param_no,
			String cust_risk_atr) throws DAOException, JBranchException {
		StringBuffer sb;
		String sql;
		List<Map<String, Object>> list;
		sb = new StringBuffer();
        sb.append("SELECT VOLATILITY ");
        sb.append("FROM TBFPS_CUSTRISK_VOLATILITY ");
        sb.append("WHERE param_no    = '"+vr_param_no+"' ");
        sb.append("AND setting_type  = 1 ");
        sb.append("AND vol_type      = 2 ");
        sb.append("AND cust_risk_atr = "+cust_risk_atr+" ");
        sql = sb.toString();

        condition.setQueryString(sql);
        list = dam.exeQuery(condition);
        Double vr_risk_atr_1 = Double.parseDouble(list.get(0).get("VOLATILITY").toString());
        return vr_risk_atr_1;
	}

	private String BTFPS400_1(DataAccessManager dam) throws DAOException,
			JBranchException {
		StringBuffer sb = new StringBuffer();
        sb.append("SELECT param_no ");
        sb.append("FROM TBFPS_CUSTRISK_VOLATILITY_head ");
        sb.append("WHERE status          = 'A' ");
        sb.append("AND effect_start_date = ");
        sb.append("  (SELECT MAX(effect_start_date) ");
        sb.append("  FROM TBFPS_CUSTRISK_VOLATILITY_head ");
        sb.append("  WHERE status           = 'A' ");
        sb.append("  AND effect_start_date <=sysdate ");
        sb.append("  ) ");
        String sql = sb.toString();

        condition.setQueryString(sql);
        List<Map<String, Object>> list = dam.exeQuery(condition);
        String vr_param_no = list.get(0).get("PARAM_NO").toString();
		return vr_param_no;
	}
}
