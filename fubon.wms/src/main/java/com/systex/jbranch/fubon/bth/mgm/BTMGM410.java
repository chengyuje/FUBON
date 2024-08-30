package com.systex.jbranch.fubon.bth.mgm;

import java.io.FileWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBMGM_APPLY_DETAILPK;
import com.systex.jbranch.app.common.fps.table.TBMGM_APPLY_DETAILVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
@Component("btmgm410")
@Scope("prototype")
public class BTMGM410 extends BizLogic {
	private DataAccessManager dam = null;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	//產檔(刷卡金名單)
	public void genCreditCardFile(Object body, IPrimitiveMap<?> header) throws Exception {
		//撈取要寫入檔案的資料
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT GIFT.GIFT_KIND, MAI.CUST_ID, ");
		sql.append("DET.APPLY_NUMBER * GIFT.GIFT_COSTS AS BONUS, ");
		sql.append("DET.* FROM TBMGM_APPLY_DETAIL DET ");
		sql.append("LEFT JOIN TBMGM_GIFT_INFO GIFT ON DET.GIFT_SEQ = GIFT.GIFT_SEQ ");
		sql.append("LEFT JOIN TBMGM_APPLY_MAIN MAI ON DET.APPLY_SEQ = MAI.APPLY_SEQ ");
		sql.append("WHERE DET.DELIVERY_STATUS = '1' AND GIFT.GIFT_KIND = '1' ");
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list  = dam.exeQuery(queryCondition);
		
		List<String> fwList = new ArrayList<String>();
		String[] txtHeader = { "CUST_ID" ,"BONUS"};
		
		for (Map<String, Object> map : list) {
			StringBuffer temp = new StringBuffer();
			int i = 1;
			for(String key : txtHeader){
				if("BONUS".equals(key)){
					if(map.get("BONUS") != null){
						String bonusNoZero = map.get("BONUS").toString();
						int bonusLength = bonusNoZero.length();
						int addZero = 9 - bonusLength;
						String bonus = "";
						for(int add = 0; add < addZero; add++){
							bonus += "0";
						}
						bonus += bonusNoZero;
						temp.append(bonus);
					}
				} else {
					temp.append(ObjectUtils.toString(map.get(key)));					
				}
				
				if(i != txtHeader.length){
					temp.append(",");
					i++;
				}
			}
			fwList.add(temp.toString());
		}
		
		//產生檔案
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports\\";
		String filename = "WMMGM_" + sdf.format(new Date());
		FileWriter fw = new FileWriter(tempPath + filename + ".TXT");
		
		if (fwList.size() > 0) {
			for(String str : fwList) {
				fw.write(str+"\r\n");
			}
		}
		fw.close();	
		
		//更新贈品出貨狀態為：2.廠商已收檔作業中
		for (Map<String, Object> map : list) {
			TBMGM_APPLY_DETAILPK pk = new TBMGM_APPLY_DETAILPK();
			pk.setAPPLY_SEQ(map.get("APPLY_SEQ").toString());
			pk.setGIFT_SEQ(map.get("GIFT_SEQ").toString());
			
			TBMGM_APPLY_DETAILVO vo =  (TBMGM_APPLY_DETAILVO) getDataAccessManager().findByPKey(TBMGM_APPLY_DETAILVO.TABLE_UID, pk);
			
			if(null != vo){
				//贈品出貨狀態：　1.分行已兌換待總行執行　2.廠商作業中　3.已出貨/已入帳
				vo.setDELIVERY_STATUS("2");
				//贈品下單日期
				vo.setORDER_DATE(new Timestamp(System.currentTimeMillis()));
				dam.update(vo);
			}
		}
	}
	
	//產檔(旺紅包名單)
	public void genRedEnvelopeFile(Object body, IPrimitiveMap<?> header) throws Exception {
		//撈取要寫入檔案的資料
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT GIFT.GIFT_KIND, MAI.CUST_ID, '' AS MEMO, ");
		sql.append("DET.APPLY_NUMBER * GIFT.GIFT_COSTS AS BONUS, ");
		sql.append("DET.* FROM TBMGM_APPLY_DETAIL DET ");
		sql.append("LEFT JOIN TBMGM_GIFT_INFO GIFT ON DET.GIFT_SEQ = GIFT.GIFT_SEQ ");
		sql.append("LEFT JOIN TBMGM_APPLY_MAIN MAI ON DET.APPLY_SEQ = MAI.APPLY_SEQ ");
		sql.append("WHERE DET.DELIVERY_STATUS = '1' AND GIFT.GIFT_KIND = '2' ");
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list  = dam.exeQuery(queryCondition);
		
		List<String> fwList = new ArrayList<String>();
		String[] txtHeader = { "CUST_ID", "BONUS", "MEMO"};
		
		for (Map<String, Object> map : list) {
			StringBuffer temp = new StringBuffer();
			int i = 1;
			for(String key : txtHeader){
				if("MEMO".equals(key)){
					temp.append("");
				} else {
					temp.append(ObjectUtils.toString(map.get(key)));							
				}
				
				if(i != txtHeader.length){
					temp.append(",");
					i++;
				}
			}
			fwList.add(temp.toString());
		}
		
		//產生檔案
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports\\";
		String filename = "MGMACTFORMPLUSLEAD_" + sdf.format(new Date());
		FileWriter fw = new FileWriter(tempPath + filename + ".TXT");
		
		if (fwList.size() > 0) {
			for(String str : fwList) {
				fw.write(str+"\r\n");
			}
		}
		fw.close();	
		
		//更新贈品出貨狀態為：2.廠商已收檔作業中
		for (Map<String, Object> map : list) {
			TBMGM_APPLY_DETAILPK pk = new TBMGM_APPLY_DETAILPK();
			pk.setAPPLY_SEQ(map.get("APPLY_SEQ").toString());
			pk.setGIFT_SEQ(map.get("GIFT_SEQ").toString());
			
			TBMGM_APPLY_DETAILVO vo =  (TBMGM_APPLY_DETAILVO) getDataAccessManager().findByPKey(TBMGM_APPLY_DETAILVO.TABLE_UID, pk);
			
			if(null != vo){
				//贈品出貨狀態：　1.分行已兌換待總行執行　2.廠商作業中　3.已出貨/已入帳
				vo.setDELIVERY_STATUS("2");
				//贈品下單日期
				vo.setORDER_DATE(new Timestamp(System.currentTimeMillis()));
				dam.update(vo);
			}
		}
	}
}