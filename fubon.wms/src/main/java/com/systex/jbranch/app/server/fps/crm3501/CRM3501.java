package com.systex.jbranch.app.server.fps.crm3501;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;





import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_AOCHG_PLISTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.mail.FubonMail;
import com.systex.jbranch.platform.server.mail.FubonSendJavaMail;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/08/24
 * 
 */
@Component("crm3501")
@Scope("request")
public class CRM3501 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM3501.class);

	
	public void calculateAOChange(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		
		//從「達康的客戶名單檔案輸出目錄」讀檔
		//尚未知道是甚麼 先預設有LIST > 
		
		List<Map<String,String>> datalist = new ArrayList<Map<String,String>>();
		Map<String, String> map = new HashMap<String,String>();
		map.put("CUST_ID", "A123456789");
		map.put("BRANCH_NBR", "686");
		
		Map<String, String> map2 = new HashMap<String,String>();
		map2.put("CUST_ID", "B123456789");
		map2.put("BRANCH_NBR", "752");

		datalist.add(map);
		datalist.add(map2);
		
		String custlist = "'" + datalist.get(0).get("CUST_ID") + "'";
		for (int i = 1; i < datalist.size(); i++) {
			custlist = custlist + ", '" + datalist.get(i).get("CUST_ID") + "'";
		}
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT C.CUST_ID, C.BRA_NBR AS ORG_BRA_NBR, C.AO_CODE AS ORG_AO_CODE, C.CON_DEGREE, C.VIP_DEGREE, P.SEQ AS P_SEQ ");
		sql.append("FROM TBCRM_CUST_MAST C LEFT JOIN TBCRM_TRS_AOCHG_PLIST P ON C.CUST_ID = P.CUST_ID AND P.PROCESS_STATUS= 'L0' ");
		sql.append("WHERE C.CUST_ID IN (:custlist) ");
		
		queryCondition.setObject("custlist", custlist);
		queryCondition.setQueryString(sql.toString());
		
		List<Map<String, String>> list = dam.exeQuery(queryCondition);
		
		for (int i = 0 ; i < datalist.size(); i++) {
			for (int j = 0 ; j < list.size(); j++ )
			if (datalist.get(i).get("CUST_ID").equals(list.get(j).get("CUST_ID"))) {
				list.get(j).put("NEW_BRA_NBR", datalist.get(i).get("BRANCH_NBR"));
			}
		}
		
		//檢查達康傳回分行代碼是否存在，不存在則跳下一筆
			// do something 寫到錯誤檔案
			// (參考COMMON_XML.CRM.DELCAM_CUST_LIST_ERRLOG_FILE_PATH)
		//檢查是否存在非財管(VIP_DEGREE!=A, B, V) or負貢獻度客戶(CON_DEGREE=S)，不存在則寫到錯誤檔案後，跳下一筆
		
		for (Map<String, String> data : list) {
			if (!data.get("VIP_DEGREE").equals("A") || !data.get("VIP_DEGREE").equals("B") 
				|| !data.get("VIP_DEGREE").equals("V") || data.get("CON_DEGREE").equals("S")) {
				// do something 寫到錯誤檔案
				// (參考COMMON_XML.CRM.DELCAM_CUST_LIST_ERRLOG_FILE_PATH)
				}
			//檢查現行待移轉清單裡是否有等待達康的資料
			
			TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
			vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(
					TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, data.get("P_SEQ"));
			
			vo.setNEW_AO_BRH(data.get("NEW_BRA_NBR"));
			vo.setPROCESS_STATUS("L1");
			dam.update(vo);
			
			String a = "EI";
			String b = "PO";
			//E、I等級客戶
			if (a.contains(data.get("CON_DEGREE"))) {
//				TBCRM_TRS_PRJ_MASTVO vo_mast = new TBCRM_TRS_PRJ_MASTVO();
//				vo_mast.setPRJ_ID(getSEQ);
//				vo_mast.setPRJ_NAME(getSEQ);
//				vo_mast.setPRJ_NOTE(getSEQ);
//				vo_mast.setPRJ_DATE_BGN(getSEQ);
//				vo_mast.setPRJ_DATE_END(getSEQ);
//				vo_mast.setPRJ_STATUS(getSEQ);
//				
//				TBCRM_TRS_PRJ_DTLVO vo_dtl = new TBCRM_TRS_PRJ_DTLVO();
//				vo_dtl.setPRJ_ID(getSEQ);
//				vo_dtl.setCUST_ID(CUST_ID);
//				vo_dtl.setNEW_AO_BRH(NEW_AO_BRH);
//				vo_dtl.setIMP_SUCCESS_YN(IMP_SUCCESS_YN);

			//P、O、S等級客戶
			}else if (b.contains(data.get("CON_DEGREE"))) {
//				TBCRM_TRS_AOCHG_PLISTVO vo_plist = new TBCRM_TRS_AOCHG_PLISTVO();
//				vo_plist.setSEQ();
//				vo_plist.setCUST_ID();
//				vo_plist.setORG_AO_CODE();
//				vo_plist.setORG_AO_BRH();
//				vo_plist.setNEW_AO_CODE("");
//				vo_plist.setNEW_AO_BRH();
//				vo_plist.setAPL_EMP_ID();
//				vo_plist.setAPL_EMP_ROLE();
//				vo_plist.setAPL_DATETIME();
//				vo_plist.setAPL_REASON();
//				vo_plist.setTRS_FLOW_TYPE();
//				vo_plist.setPROCESS_STATUS();
//				vo_plist.setTRS_TYPE();
//				vo_plist.setTRS_TXN_SOURCE();

			}

		}
		
		
		//每日抓客群身份又同時是空Code的客戶名單，直接丟主管待覆核清單
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql2 = new StringBuffer();
		
		sql2.append("SELECT CUST_ID, BRA_NBR, AO_CODE ");
		sql2.append("FROM TBCRM_CUST_MAST ");
		sql2.append("WHERE VIP_DEGREE in ( 'V', 'A', 'B') AND AO_CODE IS NULL ");

		queryCondition2.setQueryString(sql2.toString());
		
		List<Map<String, String>> list2 = dam.exeQuery(queryCondition2);
		
		if (list2.size() > 0 ) {
//			TBCRM_TRS_AOCHG_PLISTVO vo_plist2 = new TBCRM_TRS_AOCHG_PLISTVO();
//			vo_plist2.setSEQ();
//			vo_plist2.setCUST_ID(list);
//			vo_plist2.setORG_AO_CODE();
//			vo_plist2.setORG_AO_BRH();
//			vo_plist2.setNEW_AO_CODE();
//			vo_plist2.setNEW_AO_BRH();
//			vo_plist2.setAPL_EMP_ID();
//			vo_plist2.setAPL_EMP_ROLE();
//			vo_plist2.setAPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
//			vo_plist2.setAPL_REASON("16");
//			vo_plist2.setTRS_FLOW_TYPE("1");
//			vo_plist2.setPROCESS_STATUS("L1");
//			vo_plist2.setTRS_TYPE("1");
//			vo_plist2.setTRS_TXN_SOURCE("6");
//			dam.create(vo_plist2);
		}
		
		//取參數「手續費收入門檻」 v_ACT_PRFT
		
		
		
		
		
		
		
		
		
		
		
	}

	
}