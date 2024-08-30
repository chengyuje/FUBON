package com.systex.jbranch.app.server.fps.crm3502;

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
@Component("crm3502")
@Scope("request")
public class CRM3502 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM3502.class);

	
	public void calculateAOChangeNotification(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT L.CUST_ID, L.ORG_AO_CODE, L.NEW_AO_CODE, L.ORG_AO_BRH, L.NEW_AO_BRH, L.NEW_AO_NAME, M2.EMP_NAME, BR.BRA_NAME, L.APL_REASON, C.CUST_NAME, C.VIP_DEGREE, C.CON_DEGREE, CT.EMAIL, CT.COM_ADDRESS ");
		sql.append("FROM TBCRM_CUST_AOCODE_CHGLOG L LEFT JOIN TBCRM_CUST_CONTACT CT ON L.CUST_ID = CT.CUST_ID,  ");
		sql.append("TBCRM_CUST_MAST C,  ");
		sql.append("TBORG_MEMBER M2, ");
		sql.append("TBORG_MEMBER_ROLE R2,  ");
		sql.append("(SELECT DEPT_ID AS BRA_NBR, DEPT_NAME AS BRA_NAME, PARENT_DEPT_ID FROM TBORG_DEFN WHERE ORG_TYPE = '50') BR ");
		sql.append("WHERE L.CUST_ID=C.CUST_ID AND M2.EMP_ID = R2.EMP_ID AND R2.ROLE_ID IN ( SELECT ROLE_ID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID = ' 011'  ) ");  
		sql.append("AND L.NEW_AO_BRH = BR.BRA_NBR ");
		sql.append("AND TRUNC(LETGO_DATETIME) BETWEEN TRUNC(current_date, 'D')-7     AND TRUNC (current_date, 'D')-1 ");
		sql.append("ORDER BY L.LETGO_DATETIME ASC ");
		
		queryCondition.setQueryString(sql.toString());
		
		List<Map<String, String>> list = dam.exeQuery(queryCondition);
		
		//k 指要刪掉的名單
		List<Integer> k = new ArrayList<Integer>();
		for (int i = 0; i < list.size(); i++) {
			if (k.contains(i)){
				continue;
			}
			for (int j = i + 1; j < list.size(); j++) {
				//排除該筆之後仍有移轉紀錄的客戶（以最後移轉的為主）
				if (list.get(i).get("CUST_ID").equals(list.get(j).get("CUST_ID")) && list.get(i).get("NEW_AO_CODE").equals(list.get(j).get("ORG_AO_CODE"))) {
					//排除一週內從A理專移轉到B理專，又移轉回A理專的客戶
					if(list.get(i).get("ORG_AO_CODE").equals(list.get(j).get("NEW_AO_CODE"))) {
						k.add(j);
						k.add(i);
						continue;
					}else {
						k.add(i);
					}
				}
			}
			if(k.contains(i)){
				continue;
			}else{
				//do something
				String a = "EI";
				String b = "POS";
				//E、I等級客戶
				if (a.contains(list.get(i).get("CON_DEGREE"))) {
					//同分行
					if (list.get(i).get("ORG_AO_BRH").equals(list.get(i).get("NEW_AO_BRH"))) {
						//輸出⟪○○分行-同分行客戶異動通知信-○○○.docx⟫到「同分行客戶異動通知信的檔案輸出目錄」
						//尚不知道檔案來源及輸出目的地
						
					//跨分行
					}else {
						//輸出⟪○○分行-分流客戶異動通知信-○○○.docx⟫到「同分行客戶異動通知信的檔案輸出目錄」
						//尚不知道檔案來源及輸出目的地
						
					}

				//P、O、S等級客戶
				}else if (b.contains(list.get(i).get("CON_DEGREE"))) {
					//同分行
					if (list.get(i).get("ORG_AO_BRH").equals(list.get(i).get("NEW_AO_BRH"))) {
						//寄E-MAIL通知 (尚不知E-MAIL內容)
						
						
						//如無E-MAIL寄實體信
						
					//跨分行
					}else {
						//寄E-MAIL通知
						
						
						//如無E-MAIL寄實體信
						
					}
				//OTH等級客戶 (不會有)
				}else{
					return;	
				}
			}
		}

	}
}