package com.systex.jbranch.fubon.bth.mgm;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBMGM_MGMVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Created by Carley on 2018/05/30
 */
@Component("btmgm112")
@Scope("prototype")
public class BTMGM112 extends BizLogic {
private DataAccessManager dam = null;
	
	//計算投保銷量
	public void getInvInsVol(Object body, IPrimitiveMap<?> header) throws Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		//查詢計算名單(追蹤期間投銷量內容都顯示銷量，當鍵機後即每月計算銷量。鍵機後尚未跑月批次時，預設銷量為0。)
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SEQ, ACT_SEQ, BE_MGM_CUST_ID, TO_CHAR(MGM_START_DATE, 'yyyyMMdd') AS MGM_START_DATE, ");
		sql.append("TO_CHAR(MGM_END_DATE, 'yyyyMMdd') AS MGM_END_DATE FROM TBMGM_MGM ");
//		sql.append("WHERE TO_CHAR(MGM_END_DATE, 'yyyyMMdd') <= TO_CHAR(SYSDATE, 'yyyyMMdd') ");
//		sql.append("AND ALL_REVIEW_DATE IS NOT NULL AND MGM_APPR_STATUS IS NULL ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND APPR_DATE IS NULL AND POINTS_TYPE = '1' ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> beMgmCustList = dam.exeQuery(queryCondition);
		
		for(Map<String, Object> map : beMgmCustList){
			/***********************計算投保銷量***********************/
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT CUST_ID, ROUND(SUM(AMT), 0) AS TOTAL_AMT FROM ( ");
			//保險
			sql.append("SELECT APPL_ID AS CUST_ID, SUM(ACCU_PREM_NTD) AS AMT ");
//			sql.append("FROM DM_WMG.WMG_FBRPY8A0_DETAIL@ODSTOWMS ");
			sql.append("FROM TBCAM_WMG_FBRPY8A0_DETAIL_SG ");
			sql.append("WHERE EFF_DATE BETWEEN TO_DATE( :mgm_start_date ,'YYYYMMDD') ");
			sql.append("AND TO_DATE( :mgm_end_date ,'YYYYMMDD') AND APPL_ID = :cust_id ");
			sql.append("GROUP BY APPL_ID UNION ALL ");
			//基金
			sql.append("SELECT CUST_ID, SUM(TXN_AMT_NTD) AS AMT FROM ( ");
			sql.append("SELECT F.CUST_ID, F.TXN_DT, F.TXN_AMT_NTD, ");
			sql.append("CASE WHEN F.INVS_TYP = '2' ");
			sql.append("AND TO_CHAR(U.FST_PCAS_TXN_DT, 'YYYYMM') = TO_CHAR(F.TXN_DT, 'YYYYMM') THEN 1 ");
			sql.append("WHEN F.INVS_TYP='1' THEN 1 ELSE 0 END FUND_FLG, ");
			sql.append("RANK() OVER(PARTITION BY TO_CHAR(F.TXN_DT, 'YYYY'), F.CERF_NO ORDER BY F.TXN_DT ) TXN_RANK_SEQ ");
//			sql.append("FROM DM_WMG.WMG_FUND_TXN@ODSTOWMS F ");
			sql.append("FROM TBCAM_WMG_FUND_TXN_SG F ");
//			sql.append("LEFT JOIN DM_T_VIEW.WMG_FUND_ACCT@ODSTOWMS U ON U.CERF_NO = F.CERF_NO ");
			sql.append("LEFT JOIN TBCAM_WMG_FUND_ACCT_SG U ON U.CERF_NO = F.CERF_NO ");
			sql.append("WHERE F.TXN_DT BETWEEN TO_DATE( :mgm_start_date , 'YYYYMMDD') ");
			sql.append("AND TO_DATE( :mgm_end_date , 'YYYYMMDD') ");
			sql.append("AND F.TXN_TYP = '10' AND F.CUST_ID = :cust_id ) ");
			sql.append("WHERE FUND_FLG = '1' AND TXN_RANK_SEQ = '1' ");
			
//			sql.append("AND F.YYYYMM in ('201801') ");		//for 測試用，之後拿掉
			
			sql.append("GROUP BY CUST_ID UNION ALL ");
			//其他交易(組合式商品,債券,指單)
			sql.append("SELECT CUST_ID, SUM(TXN_AMT) AS AMT ");
//			sql.append("FROM DM_WMG.WMG_CUST_TXN_D@ODSTOWMS ");
			sql.append("FROM TBCAM_WMG_CUST_TXN_D_SG ");
			sql.append("WHERE SOURCE_DESC IN ('指單', '債券', '組合式商品') ");
			sql.append("AND PROD_TYPE_1 = '投資' ");
			sql.append("AND TXN_DT BETWEEN TO_DATE( :mgm_start_date ,'YYYYMMDD') ");
			sql.append("AND TO_DATE( :mgm_end_date , 'YYYYMMDD') AND( ");
			sql.append("(PROD_TYPE_2 = 'DCI_小額' AND TXN_TYPE = '1') OR ");
			sql.append("(PROD_TYPE_2 = 'DCI_大額' AND TXN_TYPE = 'S3') OR ");
			sql.append("(SOURCE_DESC = '指單' AND TXN_TYPE IN ('B', '買')) OR ");
			sql.append("(PROD_TYPE_2 = 'SI' AND PROD_TYPE_3 IS NOT NULL ) OR ");
			sql.append("(SOURCE_DESC = '債券' AND TXN_TYPE = 'B')) ");
			sql.append("AND CUST_ID = :cust_id ");
			
//			sql.append("AND SNAP_YYYYMM in ('201704') ");		//for 測試用，之後拿掉
			
			sql.append("GROUP BY CUST_ID UNION ALL ");
			
			//個人金錢信託
			sql.append("SELECT CUST_ID, SUM(CNTR_AMT_NT) AS AMT ");
//			sql.append("FROM DM_WMG.WMG_PMNY_TRST_ACCT_DAY@ODSTOWMS WHERE SNAP_DATE = ( ");
			sql.append("FROM TBCAM_WMG_TRST_ACCT_DAY_SG WHERE SNAP_DATE = ( ");
//			sql.append("SELECT MAX(SNAP_DATE) FROM DM_WMG.WMG_PMNY_TRST_ACCT_DAY@ODSTOWMS ) ");
			sql.append("SELECT MAX(SNAP_DATE) FROM TBCAM_WMG_TRST_ACCT_DAY_SG ) ");
			sql.append("AND CNTR_ST_DT BETWEEN TO_DATE( :mgm_start_date ,'YYYYMMDD') ");
			sql.append("AND TO_DATE( :mgm_end_date , 'YYYYMMDD')  ");
			sql.append("AND CUST_ID = :cust_id GROUP BY CUST_ID UNION ALL ");
			//債票券附買回交易
			sql.append("SELECT CUST_NO AS CUST_ID, SUM(TRAN_AMT) AS AMT ");
//			sql.append("FROM DM_WMG.WMG_RPRS_ACCT_D@ODSTOWMS WHERE SNAP_DATE = ( ");
			sql.append("FROM TBCAM_WMG_RPRS_ACCT_D_SG WHERE SNAP_DATE = ( ");
//			sql.append("SELECT MAX(SNAP_DATE) FROM DM_WMG.WMG_RPRS_ACCT_D@ODSTOWMS ) ");
			sql.append("SELECT MAX(SNAP_DATE) FROM TBCAM_WMG_RPRS_ACCT_D_SG ) ");
			sql.append("AND DEAL_DATE BETWEEN TO_DATE( :mgm_start_date ,'YYYYMMDD') ");
			sql.append("AND TO_DATE( :mgm_end_date , 'YYYYMMDD') ");
			sql.append("AND TRAN_DESC = '附買回' AND DATA_SRC_CD IN ('票券', '債券') ");
			sql.append("AND CUST_NO = :cust_id GROUP BY CUST_NO UNION ALL ");
			//海外股票
			sql.append("SELECT CUST_ID, SUM(TXN_AMT) AS AMT ");
//			sql.append("FROM DM_WMG.WMG_OVERA_STOCK_TXN_REP@ODSTOWMS ");
			sql.append("FROM TBCAM_WMG_STOCK_TXN_REP_SG ");
			sql.append("WHERE TXN_DATE BETWEEN TO_DATE( :mgm_start_date ,'YYYYMMDD') ");
			sql.append("AND TO_DATE( :mgm_end_date , 'YYYYMMDD') AND TXN_TYP = 'B' ");
			sql.append("AND CUST_ID = :cust_id GROUP BY CUST_ID ) ");
			
//			sql.append("AND TO_CHAR(SNAP_DATE, 'YYYYMMDD') in ('20170913') ");		//for 測試用，之後拿掉
			
			sql.append("GROUP BY CUST_ID ");
			
			queryCondition.setObject("cust_id", map.get("BE_MGM_CUST_ID").toString());
			queryCondition.setObject("mgm_start_date", map.get("MGM_START_DATE").toString());
			queryCondition.setObject("mgm_end_date", map.get("MGM_END_DATE").toString());
			
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> invInsVolList = dam.exeQuery(queryCondition);
			
			BigDecimal invInsVol = new BigDecimal("0");
			if(invInsVolList.size() > 0){
				invInsVol = new BigDecimal(invInsVolList.get(0).get("TOTAL_AMT").toString());
			}
			
			TBMGM_MGMVO mgmVO = (TBMGM_MGMVO) dam.findByPKey(TBMGM_MGMVO.TABLE_UID, map.get("SEQ").toString());
			if(null != mgmVO) {
				mgmVO.setINS_SELL_VOL(invInsVol);
				dam.update(mgmVO);				
			}
		}
	}
	
	//執行核點作業
	public void givePoints (Object body, IPrimitiveMap<?> header) throws Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		//查詢核點名單(除投保銷量於追蹤迄日之前已達最高核點金額外，系統核點名單會抓追蹤迄日月份為前兩個月以前之鍵機案件。)
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM TBMGM_MGM WHERE INS_SELL_VOL IS NOT NULL ");
		sql.append("AND APPR_DATE IS NULL AND POINTS_TYPE = '1' ");
		sql.append("AND ALL_REVIEW_DATE IS NOT NULL ");
		sql.append("AND (TO_CHAR(MGM_END_DATE, 'yyyyMMdd') <= ( ");
		sql.append("SELECT TO_CHAR(LAST_DAY(ADD_MONTHS( SYSDATE , -2)), 'yyyyMMdd') FROM DUAL) ");
		sql.append("OR INS_SELL_VOL >= (SELECT MAX(INS_SELL_VOL_MAX) FROM TBMGM_XML )) ");
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> givePointsList = dam.exeQuery(queryCondition);
		
		if(givePointsList.size() > 0) {
			//查詢核點之最低/最高投保銷量&最高核點點數
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT MIN(INS_SELL_VOL_MIN) AS VOL_MIN, ");
			sql.append("MAX(INS_SELL_VOL_MIN) AS VOL_MAX, ");
			sql.append("MAX(POINTS) AS MAX_POINTS FROM TBMGM_XML ");
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> volMinList = dam.exeQuery(queryCondition);
			
			BigDecimal volMin = new BigDecimal("0");
			BigDecimal volMax = new BigDecimal("0");
			BigDecimal maxPoints = new BigDecimal("0");
			
			if(volMinList.size() > 0){
				if(volMinList.get(0).get("VOL_MIN") != null)
					volMin = new BigDecimal(volMinList.get(0).get("VOL_MIN").toString());
				
				if(volMinList.get(0).get("VOL_MAX") != null)
					volMax = new BigDecimal(volMinList.get(0).get("VOL_MAX").toString());
				
				if(volMinList.get(0).get("MAX_POINTS") != null)
					maxPoints = new BigDecimal(volMinList.get(0).get("MAX_POINTS").toString());
			}
			
			for(Map<String, Object> map : givePointsList) {
				TBMGM_MGMVO mgmVO = (TBMGM_MGMVO) dam.findByPKey(TBMGM_MGMVO.TABLE_UID, map.get("SEQ").toString());
				
				BigDecimal invInsVol = new BigDecimal("0");
				if(map.get("INS_SELL_VOL") != null) {
					invInsVol = new BigDecimal(map.get("INS_SELL_VOL").toString());
				}
				
				if(null != mgmVO && volMin != new BigDecimal("0") && 
						volMax != new BigDecimal("0") && maxPoints != new BigDecimal("0")){
					
					//已達門檻：
					//1. 已過推薦迄日，且迄今累計投保銷量已達核點之最低投保銷量。
					//2. 未達推薦日，但投保銷量已大於最高核點之投保銷量
					if(invInsVol.compareTo(volMin) == 0 || invInsVol.compareTo(volMin) == 1){
						mgmVO.setMGM_APPR_STATUS("Y");			//已達門檻
						
						if(invInsVol.compareTo(volMax) == 0 || invInsVol.compareTo(volMax) == 1){
							mgmVO.setAPPR_POINTS(maxPoints);	//核點點數
						} else {
							//查詢核點點數
							queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							sql = new StringBuffer();
							sql.append("SELECT POINTS FROM TBMGM_XML WHERE 1 = 1 ");
							sql.append("AND INS_SELL_VOL_MIN <= :invInsVol ");
							sql.append("AND INS_SELL_VOL_MAX > :invInsVol ");
							queryCondition.setObject("invInsVol", invInsVol);
							queryCondition.setQueryString(sql.toString());
							List<Map<String, Object>> pointsList = dam.exeQuery(queryCondition);
							
							BigDecimal points = new BigDecimal("0");
							if(pointsList.size() > 0){
								if(pointsList.get(0).get("POINTS") != null)
									points = new BigDecimal(pointsList.get(0).get("POINTS").toString());
							}
							if(points != new BigDecimal("0")){
								mgmVO.setAPPR_POINTS(points);	//核點點數
							}
						}
					} else {
						mgmVO.setMGM_APPR_STATUS("N");	//未達門檻：已過推薦迄日，且迄今累計投保銷量未達核點之最低投保銷量。
					}
					mgmVO.setRELEASE_YN("N");			//是否已放行：N.未放行 (已/未達門檻之案件皆需放行)
					mgmVO.setAPPR_DATE(new Timestamp(System.currentTimeMillis()));	//核點日期
					dam.update(mgmVO);
				}
			}
		}
	}
	
	//執行達人加碼作業(推薦人有五筆以上<可累加>且得點6點以上之案件"點數入帳(總行放行)"後，"次月" 再行給予加碼點數。)
	public void proBonus(Object body, IPrimitiveMap<?> header) throws Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		//查詢達人名單
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM ( ");
		sql.append("SELECT MGM.*, PRO.PRO_BONUS FROM ( ");
		sql.append("SELECT ACT_SEQ, MGM_CUST_ID, COUNT(*) AS COUNT FROM TBMGM_MGM ");
		sql.append("WHERE APPR_POINTS >= 6 AND POINTS_TYPE = '1' AND RELEASE_YN = 'Y' ");
		sql.append("GROUP BY ACT_SEQ, MGM_CUST_ID ) MGM ");
		sql.append("LEFT JOIN ( ");
		sql.append("SELECT ACT_SEQ, MGM_CUST_ID, SUM(APPR_POINTS) AS PRO_BONUS ");
		sql.append("FROM TBMGM_MGM WHERE POINTS_TYPE = '2' ");
		sql.append("GROUP BY ACT_SEQ, MGM_CUST_ID ) PRO ");
		sql.append("ON MGM.ACT_SEQ = PRO.ACT_SEQ ");
		sql.append("AND MGM.MGM_CUST_ID = PRO.MGM_CUST_ID ");
		sql.append(") WHERE COUNT >= 5 ");
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> proCustList = dam.exeQuery(queryCondition);
		
		for(Map<String, Object> proMap : proCustList){
			Double count = new Double(proMap.get("COUNT").toString());

			Double proBonus = 0.0;
			if(proMap.get("PRO_BONUS") != null){
				proBonus = new Double(proMap.get("PRO_BONUS").toString());
			}
			
			Double newBonus = Math.floor(count / 5) * 10.0;
			
			if(!proBonus.equals(newBonus)){
				if(null == proMap.get("PRO_BONUS")){
					TBMGM_MGMVO vo = new TBMGM_MGMVO();
					vo.setSEQ(this.getMGM_SEQ());
					vo.setACT_SEQ(proMap.get("ACT_SEQ").toString());
					vo.setMGM_CUST_ID(proMap.get("MGM_CUST_ID").toString());
					vo.setPOINTS_TYPE("2");		//2：達人加碼
					vo.setAPPR_POINTS(new BigDecimal(newBonus));
					vo.setAPPR_DATE(new Timestamp(System.currentTimeMillis()));
					vo.setRELEASE_YN("N");
					
					dam.create(vo);
					
				} else {
					TBMGM_MGMVO vo = new TBMGM_MGMVO();
					vo.setSEQ(this.getMGM_SEQ());
					vo.setACT_SEQ(proMap.get("ACT_SEQ").toString());
					vo.setMGM_CUST_ID(proMap.get("MGM_CUST_ID").toString());
					vo.setPOINTS_TYPE("2");		//2：達人加碼
					vo.setAPPR_POINTS(new BigDecimal(newBonus - proBonus));
					vo.setAPPR_DATE(new Timestamp(System.currentTimeMillis()));
					vo.setRELEASE_YN("N");
					
					dam.create(vo);
				}
			}
		}
	}
	
	//取得MGM活動達人加碼的案件序號(SEQ)
	private String getMGM_SEQ () throws JBranchException {		
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String date = df.format(new Date());
        
		try{
			seqNum = date + "PRO" + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBMGM_MGM")));
		}catch(Exception e){
			sn.createNewSerial("TBMGM_MGM", "0000", 1, "d", null, 1, new Long("9999"), "y", new Long("0"), null);
			seqNum = date + "PRO" + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBMGM_MGM")));
		}
		return seqNum;
	}
        
}
