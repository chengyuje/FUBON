package com.systex.jbranch.app.server.fps.btfps100;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

//import com.systex.jbranch.app.server.fps.insjlb.INSJLB;
//import com.systex.jbranch.app.server.fps.insjlb.InsjlbBusinessInf;
//import com.systex.jbranch.app.server.fps.insjlb.InsjlbParamInf;
//import com.systex.jbranch.app.server.fps.insjlb.vo.GetOdItemListInputVO;
//import com.systex.jbranch.app.server.fps.insjlb.vo.GetOldItemListOutputVO;
//import com.systex.jbranch.app.server.fps.insjlb.FubonInsjlb;
//import com.systex.jbranch.app.server.fps.insjlb.vo.GetOdItemListInputVO;
//import com.systex.jbranch.app.server.fps.insjlb.vo.GetOldItemListOutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Repository("btfps100")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BTFPS100 extends BizLogic {
	private DataAccessManager dam = null;
//	private InsjlbBusinessInf insjlb;

    /**
     * Batch Enterance
     *
     * @param body
     * @param header
     * @throws Exception
     */
    public void execute(Object body, IPrimitiveMap<?> header) throws Exception {
    	dam = this.getDataAccessManager();
    	QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
        String sql = "";

        //1.抓取對應的參數設定:
        //PLAN_AMT_1 "理專首頁《待理財規劃客戶門檻》 存款-預定保費支出-貸款 (萬)"
        //PLAN_AMT_2 "客戶首頁《資產負債總覽》區塊 存款-預定保費支出-貸款 (萬)"
        sb.append(" SELECT plan_amt_1 * 10000 as plan_amt ");
        sb.append(" FROM TBFPS_OTHER_PARA ");
        sb.append(" WHERE param_no = ");
        sb.append("   (SELECT param_no ");
        sb.append("   FROM TBFPS_OTHER_PARA_HEAD ");
        sb.append("   WHERE status = 'A' ");
        sb.append("   AND effect_start_date = ");
        sb.append("     (SELECT MAX (effect_start_date) ");
        sb.append("     FROM TBFPS_OTHER_PARA_HEAD ");
        sb.append("     WHERE status = 'A' ");
        sb.append("     AND effect_start_date <= sysdate ");
        sb.append("   ) ");
        sb.append(" ) ");
        sql = sb.toString();

        condition.setQueryString(sql);
        List<Map<String, Object>> list = dam.exeQuery(condition);
        BigDecimal vr_plan_amt = getBigDecimal(list.size()>0 ? list.get(0).get("PLAN_AMT_1") : 0);        

        //2.(1)先尋找客戶歸戶日資產存款類 >= vr_plan_amt
        /**
         * 資料來源TBCRM_AST_ALLPRD_DETAIL_HIST
         * STEP1 取符合條件的客戶ID,存款(不含支存)總額 , 存款(不含支存)總額大於參數設定值
         */
        condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" SELECT a.CUST_ID,B.AO_CODE,B.CUST_NAME,SUM(NOW_AMT_TWD) AS DEPOSIT_AMT_NO_CHECK");
		sb.append(" FROM TBCRM_AST_ALLPRD_DETAIL_HIST a left join TBCRM_CUST_MAST b  ");
		sb.append(" ON A.CUST_ID=B.CUST_ID ");
//		sb.append(" WHERE AST_TYPE IN ('01','02','04','05') AND ROWNUM <=50 ");測試用
		sb.append(" WHERE AST_TYPE IN ('01','02','04','05') ");
		sb.append(" GROUP BY A.CUST_ID,B.CUST_NAME,B.AO_CODE ");
		sb.append(" HAVING SUM (NOW_AMT_TWD) >= "+vr_plan_amt.toString()+" ");
        sql = sb.toString();
        condition.setQueryString(sql);
        list = dam.exeQuery(condition);

        //(2)再將上述所列出的客戶，對應計算其 
        /**
         * 前一日資產餘額(台幣)
         * 前一日帳上存款金額(台幣)
         * 前一日帳上存款金額(台幣)(不含支存)
         * 前一日帳上儲蓄型保險金額(台幣)預期貸款支出
         * 前一日帳上固定收益商品金額(台幣)
         * 前一日帳上基股商品金額(台幣)
         */
        Map<String, Map> map3 = new HashMap<String, Map>();//For insert into TBFPS_CUST_LIST
        for(Map map : list) {
        	BigDecimal vr_deposit_amt = getBigDecimal(map.get("DEPOSIT_AMT"));
        	BigDecimal vr_deposit_amt_no_check = getBigDecimal(map.get("DEPOSIT_AMT_NO_CHECK"));
//        	BigDecimal vr_aum = getBigDecimal(map.get("EXP_TOTAL"));

            String vr_cust_id = ObjectUtils.toString(map.get("CUST_ID"));
            String vr_ao_code = ObjectUtils.toString(map.get("AO_CODE"));
//            String vr_branch = ObjectUtils.toString(map.get("BRA_NBR"));

            if(!map3.containsKey(vr_cust_id)){
        		map3.put(vr_cust_id, new HashMap());
        	}
            
            //存款餘額(含支存)
            condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    		sb = new StringBuffer();
            sb.append(" SELECT SUM(NOW_AMT_TWD) as  DEPOSIT_AMT");
            sb.append(" FROM TBCRM_AST_ALLPRD_DETAIL_HIST ");
            sb.append(" WHERE CUST_ID ='"+vr_cust_id+"' and AST_TYPE in ('01','02','03','04','05')");
    		sb.append(" GROUP BY CUST_ID");
            sql = sb.toString();
            condition.setQueryString(sql);
            List<Map<String, Object>> sqlList = dam.exeQuery(condition);
            BigDecimal depositAmt = getBigDecimal(sqlList.size()>0?sqlList.get(0).get("DEPOSIT_AMT"):0);

            //全資產餘額
            condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    		sb = new StringBuffer();
            sb.append(" SELECT SUM(NOW_AMT_TWD) as  ASSET_AMT");
            sb.append(" FROM TBCRM_AST_ALLPRD_DETAIL_HIST ");
            sb.append(" WHERE CUST_ID ='"+vr_cust_id+"' ");
    		sb.append(" GROUP BY CUST_ID");
            sql = sb.toString();
            condition.setQueryString(sql);
            sqlList = dam.exeQuery(condition);
            BigDecimal assetsAmt = getBigDecimal(sqlList.size()>0?sqlList.get(0).get("ASSET_AMT"):0);
            
            //固定收益商品:海外債/SI/SN
            condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    		sb = new StringBuffer();
            sb.append(" SELECT SUM(NOW_AMT_TWD) as  INV_AMT");
            sb.append(" FROM TBCRM_AST_ALLPRD_DETAIL_HIST ");
            sb.append(" WHERE CUST_ID ='"+vr_cust_id+"' AND AST_TYPE IN ('10','15','16')");
    		sb.append(" GROUP BY CUST_ID");
            sql = sb.toString();
            condition.setQueryString(sql);
            sqlList = dam.exeQuery(condition);
            BigDecimal fixprofitInv = getBigDecimal(sqlList.size()>0?sqlList.get(0).get("INV_AMT"):0);
            
            //基股商品:基金/ETF/投資型保險
            condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    		sb = new StringBuffer();
            sb.append(" SELECT SUM(NOW_AMT_TWD) as INV_AMT");
            sb.append(" FROM TBCRM_AST_ALLPRD_DETAIL_HIST ");
            sb.append(" WHERE CUST_ID ='"+vr_cust_id+"' AND AST_TYPE IN ('07','08','09','12')");
    		sb.append(" GROUP BY CUST_ID");
            sql = sb.toString();
            condition.setQueryString(sql);
            sqlList = dam.exeQuery(condition);
            BigDecimal stockBaseInv = getBigDecimal(sqlList.size()>0?sqlList.get(0).get("INV_AMT"):0);
            
            //貸款類，需待取得資料來源
//            condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//    		sb = new StringBuffer();
//            sb.append("SELECT vr_loan, ");
//            sb.append("  CASE ");
//            sb.append("    WHEN CAPTL_PURPOSE IN ");
//            sb.append("      (SELECT param_code ");
//            sb.append("      FROM TBSYSPARAMETER ");
//            sb.append("      WHERE param_type = 'CRM.LOAN_CAPTL_PURPOSE' ");
//            sb.append("      AND param_desc   = 'STUDY' ");
//            sb.append("      ) ");
//            sb.append("    THEN vr_loan ");
//            sb.append("    ELSE 0 ");
//            sb.append("  END AS vr_loan_study, ");
//            sb.append("  CASE ");
//            sb.append("    WHEN CAPTL_PURPOSE IN ");
//            sb.append("      (SELECT param_code ");
//            sb.append("      FROM TBSYSPARAMETER ");
//            sb.append("      WHERE param_type = 'CRM.LOAN_CAPTL_PURPOSE' ");
//            sb.append("      AND param_desc   = 'HOUSE' ");
//            sb.append("      ) ");
//            sb.append("    THEN vr_loan ");
//            sb.append("    ELSE 0 ");
//            sb.append("  END AS vr_loan_house, ");
//            sb.append("  CASE ");
//            sb.append("    WHEN CAPTL_PURPOSE IN ");
//            sb.append("      (SELECT param_code ");
//            sb.append("      FROM TBSYSPARAMETER ");
//            sb.append("      WHERE param_type = 'CRM.LOAN_CAPTL_PURPOSE' ");
//            sb.append("      AND param_desc   = 'CREDIT' ");
//            sb.append("      ) ");
//            sb.append("    THEN vr_loan ");
//            sb.append("    ELSE 0 ");
//            sb.append("  END AS vr_loan_credit ");
//            sb.append("FROM ");
//            sb.append("  (SELECT ");
//            sb.append("    CASE ");
//            sb.append("      WHEN due_date >= add_months(sysdate, 12) ");
//            sb.append("      THEN EACH_RTRN_AMT * 12 ");
//            sb.append("      WHEN due_date < add_months(sysdate, 12) ");
//            sb.append("      THEN EACH_RTRN_AMT * TRUNC ( months_between (TBSFA_DAT_AST_LOAN.due_date , sysdate )) ");
//            sb.append("    END AS vr_loan, ");
//            sb.append("    CAPTL_PURPOSE ");
//            sb.append("  FROM TBSFA_DAT_AST_LOAN ");
//            sb.append("  WHERE cust_id      = '"+vr_cust_id+"' ");
//            sb.append("  AND due_date      >= sysdate ");
//            sb.append("  AND CAPTL_PURPOSE IN ");
//            sb.append("    (SELECT param_code ");
//            sb.append("    FROM TBSYSPARAMETER ");
//            sb.append("    WHERE param_type = 'CRM.LOAN_CAPTL_PURPOSE' ");
//            sb.append("    AND param_desc  IN ('STUDY', 'HOUSE', 'CREDIT') ");
//            sb.append("    ) ");
//            sb.append("  AND RTRN_INTVL = '01' ");
//            sb.append("  ) ");
//            sql = sb.toString();
//            condition.setQueryString(sql);
//            List<Map<String, Object>> list2 = dam.exeQuery(condition);

            
            BigDecimal vr_loan_study = new BigDecimal(0.0);
        	BigDecimal vr_loan_house = new BigDecimal(0.0);
        	BigDecimal vr_loan_credit = new BigDecimal(0.0);
        	BigDecimal vr_loan = new BigDecimal(0.0);
        	
            //貸款資料查不到金額維持0
//            if(!list2.isEmpty()){
//                //把同筆cust_id的資料加總起來
//                for(Map map2 : list2){
//                	Map map4 = map3.get(vr_cust_id);
//                	vr_loan_study = map4.containsKey("VR_LOAN_STUDY") ? getBigDecimal(map4.get("VR_LOAN_STUDY")) : new BigDecimal(0.0);
//                	vr_loan_house = map4.containsKey("VR_LOAN_HOUSE") ? getBigDecimal(map4.get("VR_LOAN_HOUSE")) : new BigDecimal(0.0);
//                	vr_loan_credit = map4.containsKey("VR_LOAN_CREDIT") ? getBigDecimal(map4.get("VR_LOAN_CREDIT")) : new BigDecimal(0.0);
//                	vr_loan = map4.containsKey("VR_LOAN") ? getBigDecimal(map4.get("VR_LOAN")) : new BigDecimal(0.0);
//
//                    vr_loan = vr_loan.add(getBigDecimal(map2.get("VR_LOAN")));
//                    vr_loan_study = vr_loan_study.add(getBigDecimal(map2.get("VR_LOAN_STUDY")));
//                    vr_loan_house = vr_loan_house.add(getBigDecimal(map2.get("VR_LOAN_HOUSE")));
//                    vr_loan_credit = vr_loan_credit.add(getBigDecimal(map2.get("VR_LOAN_CREDIT")));
//
//                    map4.put("VR_LOAN_STUDY", vr_loan_study);
//                    map4.put("VR_LOAN_HOUSE", vr_loan_house);
//                    map4.put("VR_LOAN_CREDIT", vr_loan_credit);
//                    map4.put("VR_LOAN", vr_loan);                    
//                }
//            }
            //依庫存檔搜尋保單保費:VWCRM_AST_INS_MAST.INSPRD_ID(險種代碼) join TBPRD_INS.PRD_ID
            condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	  		sb = new StringBuffer();
	  		sb.append(" select sum(policy_fee) as policyFee,b.ins_type ");
	  		sb.append(" from VWCRM_AST_INS_MAST a join TBPRD_INS b");
	  		sb.append(" on a.insprd_id = b.prd_id ");
	  		sb.append(" where a.cust_id = '"+vr_cust_id+"'");
	  		sb.append(" group by a.cust_id,b.ins_type ");
            sql = sb.toString();
            condition.setQueryString(sql);
            sqlList = dam.exeQuery(condition);
//            BigDecimal policyFee = getBigDecimal(sqlList.get(0).get("policyFee"));
            BigDecimal ins_1_amt = new BigDecimal(0);
            BigDecimal vr_ins_all_amt_1 = new BigDecimal(0);
            BigDecimal vr_ins_all_amt_2 = new BigDecimal(0);
            
            for (Map insMap:sqlList) {
            	String insType = (String)insMap.get("ins_type");
            	switch(insType) {
            	case "1"://儲蓄型
            		ins_1_amt = getBigDecimal(insMap.get("policyFee"));
            	case "2"://投資型
            		vr_ins_all_amt_1 = getBigDecimal(insMap.get("policyFee"));
            	case "3"://保障型
            		vr_ins_all_amt_2 = getBigDecimal(insMap.get("policyFee"));
            	}
            }

            //查有效保單
//            BigDecimal vr_ins_amt = new BigDecimal(0.0);
//            BigDecimal vr_ins_all_amt_1 = new BigDecimal(0.0);
//            BigDecimal vr_ins_all_amt_2 = new BigDecimal(0.0);
//            condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//    		sb = new StringBuffer();
//            sb.append("SELECT fiac_prod_kind ");
//            sb.append("FROM TBSFA_DAT_AST_INS b ");
//            sb.append("JOIN TBPRD_INS a ");
//            sb.append("ON a.prd_id = b.prd_id ");
//            sb.append("LEFT JOIN TBPRD_INS_COMPARED c ");
//            sb.append("ON c.prd_id = a.prd_id ");
//            sb.append("LEFT JOIN TBPRD_INSDATA_PROD_MAIN d ");
//            sb.append("ON c.key_no              = d.key_no ");
//            sb.append("WHERE a. FIAC_PROD_KIND IN ('1', '2') ");
//            sb.append("AND b.cust_id            = '"+vr_cust_id+"' ");
//            sb.append("AND b.pay_sts           IN ('01', '02', '03', '04', '05', '06', '07', '08')  ");
//            sql = sb.toString();
//            condition.setQueryString(sql);
//            list2 = dam.exeQuery(condition);

            //「有效保單」有:透過保險元件查詢金額，沒有:金額=0
//            if(!list2.isEmpty()){
//        		接INSJLB
//                FubonInsjlb insjlb = (FubonInsjlb) PlatformContext.getBean("insjlb");
//        		GetOdItemListInputVO getOdItemListInputVO = new GetOdItemListInputVO();
//        		getOdItemListInputVO.setCustId(vr_cust_id);
//        		List<String> loginAOCode = new ArrayList<String>();
//        		loginAOCode.add(vr_ao_code);
//        		getOdItemListInputVO.setLoginAOCode(loginAOCode);
//        		getOdItemListInputVO.setLoginBranch(vr_branch);
//        		getOdItemListInputVO.setPlanTypes("P,H,D,W");
//        		
//        		//!!Error creating bean with name 'ins810':
//        		GetOldItemListOutputVO oldItem = insjlb.getOldItemList(getOdItemListInputVO);
//        		List<Map<String, Object>> oldItemList = oldItem.getOldItemLlist();
//                //保險元件查詢不到資料==>金額維持0
//        		if(oldItemList != null){
//            		if(oldItemList.size() > 0){
//                        vr_ins_amt = new BigDecimal(oldItemList.get(0).get("PREMIUM").toString());
//                    }
//        		}
//
//                String fiac_prod_kind = list2.get(0).get("FIAC_PROD_KIND").toString();
//                if(fiac_prod_kind.equals("1")){
//                    vr_ins_all_amt_2 = vr_ins_all_amt_2.add(vr_ins_amt); 
//                }else if(fiac_prod_kind.equals("2")){
//                	vr_ins_all_amt_1 = vr_ins_all_amt_1.add(vr_ins_amt);
//                }
//            }
//            
//            map3.get(vr_cust_id).put("VR_INS_ALL_AMT_1", vr_ins_all_amt_1);
//            map3.get(vr_cust_id).put("VR_INS_ALL_AMT_2", vr_ins_all_amt_2);
//            map3.get(vr_cust_id).put("VR_AUM", assetsAmt);
            map3.get(vr_cust_id).put("VR_DEPOSIT_AMT", depositAmt);
            map3.get(vr_cust_id).put("VR_DEPOSIT_AMT_NO_CHECK", vr_deposit_amt_no_check);
            map3.get(vr_cust_id).put("fixprofitInv", fixprofitInv);
            map3.get(vr_cust_id).put("stockBaseInv", stockBaseInv.add(ins_1_amt));//基股商品 - 基金/ETF/投資型保險
            map3.get(vr_cust_id).put("vr_ins_all_amt_1", vr_ins_all_amt_1);
            map3.get(vr_cust_id).put("vr_ins_all_amt_2", vr_ins_all_amt_2);
            map3.get(vr_cust_id).put("ins_1_amt", ins_1_amt);
            
//
        }
        
        sb = new StringBuffer();
        sb.append("delete TBFPS_CUST_LIST");
        sql = sb.toString();
        condition.setQueryString(sql);
        dam.exeUpdate(condition);

        for(String cust_id : map3.keySet()){
        	Map map4 = map3.get(cust_id);
        	BigDecimal vr_loan_study = getBigDecimal(map4.get("VR_LOAN_STUDY"));
        	BigDecimal vr_loan_house = getBigDecimal(map4.get("VR_LOAN_HOUSE"));
        	BigDecimal vr_loan_credit = getBigDecimal(map4.get("VR_LOAN_CREDIT"));
        	BigDecimal vr_loan = getBigDecimal(map4.get("VR_LOAN"));
        	BigDecimal vr_ins_amt = getBigDecimal(map4.get("VR_INS_AMT"));
//        	BigDecimal vr_ins_all_amt_1 = getBigDecimal(map4.get("VR_INS_ALL_AMT_1"));
//        	BigDecimal vr_ins_all_amt_2 = getBigDecimal(map4.get("VR_INS_ALL_AMT_2"));
        	BigDecimal vr_deposit_amt_no_check = getBigDecimal(map4.get("VR_DEPOSIT_AMT_NO_CHECK"));
        	BigDecimal vr_aum = getBigDecimal(map4.get("VR_AUM"));
        	BigDecimal vr_deposit_amt = getBigDecimal(map4.get("VR_DEPOSIT_AMT"));
        	BigDecimal fixprofitInv = getBigDecimal(map4.get("fixprofitInv"));
        	BigDecimal stockBaseInv = getBigDecimal(map4.get("stockBaseInv"));
        	BigDecimal ins_1_amt = getBigDecimal(map4.get("ins_1_amt"));
        	BigDecimal vr_ins_all_amt_1 = getBigDecimal(map4.get("vr_ins_all_amt_1"));
        	BigDecimal vr_ins_all_amt_2 = getBigDecimal(map4.get("vr_ins_all_amt_2"));
        	

            if(vr_deposit_amt_no_check.subtract(vr_ins_all_amt_1).subtract(vr_ins_all_amt_2).subtract(vr_loan_study).subtract(vr_loan_house).subtract(vr_loan_credit).compareTo(vr_plan_amt) >= 0) {
                condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        		sb = new StringBuffer();
            	sb.append("insert into TBFPS_CUST_LIST( ");
            	sb.append("	    cust_id, ");
            	sb.append("	    aum, ");
            	sb.append("	    deposit_amt, ");
            	sb.append("	    deposit_amt_no_ck, ");
            	sb.append("	    INS_1_AMT, ");
            	sb.append("	    FIXED_INCOME_AMT, ");
            	sb.append("	    FUND_AMT, ");
            	sb.append("	    ins_year_amt_1, ");
            	sb.append("	    ins_year_amt_2, ");
            	sb.append("	    ln_year_amt_1, ");
            	sb.append("	    ln_year_amt_2, ");
            	sb.append("	    ln_year_amt_3, ");
            	sb.append("	    creator, ");
            	sb.append("	    modifier, ");
            	sb.append("	    createtime, ");
            	sb.append("	    lastupdate ");
            	sb.append("	)values( ");
            	sb.append("	    '"+cust_id+"', ");
            	sb.append("	    '"+vr_aum+"', ");
            	sb.append("	    '"+vr_deposit_amt+"', ");
            	sb.append("	    '"+vr_deposit_amt_no_check+"', ");
            	sb.append("	    '"+ins_1_amt+"', ");
            	sb.append("	    '"+fixprofitInv+"', ");
            	sb.append("	    '"+stockBaseInv+"', ");
            	sb.append("	    '"+vr_ins_all_amt_1+"', ");
            	sb.append("	    '"+vr_ins_all_amt_2+"', ");
            	sb.append("	    '"+vr_loan_house+"', ");
            	sb.append("	    '"+vr_loan_credit+"', ");
            	sb.append("	    '"+vr_loan_study+"', ");
            	sb.append("	    'BTFPS100', ");
            	sb.append("	    'BTFPS100', ");
            	sb.append("	    sysdate, ");
            	sb.append("	    sysdate ");
            	sb.append("	) ");
            	sql = sb.toString();
            	condition.setQueryString(sql);
                dam.exeUpdate(condition);
            }
        }
    }

	/**
	 * 判斷空字串或null 欄位轉化為0
	 * 
	 * @param data_obj
	 * @return
	 */
	public static BigDecimal getBigDecimal(Object data_obj) {
		if (data_obj == "" || data_obj == null) {
			return BigDecimal.ZERO;
		} else {
			return new BigDecimal(ObjectUtils.toString(data_obj));
		}
	}
}
