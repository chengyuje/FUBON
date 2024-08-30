package com.systex.jbranch.app.server.fps.mgm115;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.birt.report.model.api.util.StringUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBMGM_APPLY_DETAILPK;
import com.systex.jbranch.app.common.fps.table.TBMGM_APPLY_DETAILVO;
import com.systex.jbranch.app.common.fps.table.TBMGM_APPLY_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBMGM_GIFT_INFOVO;
import com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO;
import com.systex.jbranch.app.server.fps.mgm110.MGM110OutputVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot701.basicVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Carley
 * @date 2018/03/06
 * 
 */
@Component("mgm115")
@Scope("request")
public class MGM115 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	
	//轄下未兌換客戶查詢
	public void inquire (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT * FROM ( ");
		sb.append("SELECT * FROM ( SELECT MGM.CUST_ID, CUST.CUST_NAME, ");
		sb.append("CUST.BRA_NBR, DEFN.DEPT_NAME AS BRA_NAME, ");
		sb.append("CUST.AO_CODE, AO.EMP_ID, ORG.EMP_NAME, ");
		sb.append("MGM.TOTAL, NVL( APP.EXCH_TOTAL, 0) AS EXCH_TOTAL, ");
		sb.append("MGM.TOTAL - NVL(APP.EXCH_TOTAL, 0) AS UN_EXCH FROM ( ");
		sb.append("SELECT MGM_CUST_ID AS CUST_ID, SUM(APPR_POINTS) AS TOTAL FROM TBMGM_MGM ");
		sb.append("WHERE ACT_SEQ = :act_seq AND  RELEASE_YN = 'Y' GROUP BY MGM_CUST_ID ");
		sb.append(") MGM LEFT JOIN ( ");
		sb.append("SELECT CUST_ID, SUM(EXCHANGE_POINTS) AS EXCH_TOTAL FROM TBMGM_APPLY_MAIN ");
		sb.append("WHERE ACT_SEQ = :act_seq GROUP BY CUST_ID ");
		sb.append(") APP ON MGM.CUST_ID = APP.CUST_ID ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CUST ON MGM.CUST_ID = CUST.CUST_ID ");
		sb.append("LEFT JOIN TBORG_DEFN DEFN ON CUST.BRA_NBR = DEFN.DEPT_ID ");
		sb.append("LEFT JOIN TBORG_SALES_AOCODE AO ON CUST.AO_CODE = AO.AO_CODE ");
		sb.append("LEFT JOIN TBORG_MEMBER ORG ON AO.EMP_ID = ORG.EMP_ID ");
		sb.append(") WHERE TOTAL - EXCH_TOTAL > 0 ) WHERE 1 = 1 ");
		
		queryCondition.setObject("act_seq", inputVO.getAct_seq());
		
		//有(推薦人)客戶ID即可編輯、補件MGM活動鍵機案件。有輸入客戶ID則略其餘(除活動代碼外)查詢條件。
		if (StringUtils.isNotBlank(inputVO.getCust_id())){
			sb.append("AND CUST_ID LIKE :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
			
		} else {
			//無選取AO Code的理專(AO Code有兩個以上的理專)
			if (StringUtil.isEqual(inputVO.getRole(), "ao")) {
				String[] ao_list = inputVO.getAo_list().split(",");
				sb.append("AND AO_CODE IN ( :ao_list ) ");
	        	queryCondition.setObject("ao_list", ao_list);
			}else{
				//有選取分行
				if(!StringUtils.isBlank(inputVO.getBranch_nbr())){
					sb.append("AND BRA_NBR = :bra_nbr ");	        	
		        	queryCondition.setObject("bra_nbr", inputVO.getBranch_nbr());
				}else{
					//未選取分行，但有選取業務處  || 營運區
					if (!StringUtils.isBlank(inputVO.getRegion_center_id()) || !StringUtils.isBlank(inputVO.getBranch_area_id())) {
		    			        		        	
						sb.append("AND BRA_NBR IN (:branch_list) ");
						
						List<String> branch_list = new ArrayList<String>();
			        	for(int i = 0; i < inputVO.getBranch_list().size(); i++){
			        		branch_list.add(inputVO.getBranch_list().get(i).get("DATA"));
			        	}
			        	queryCondition.setObject("branch_list", branch_list);
					}
				}
			}
		}
		sb.append("ORDER BY CUST_ID ");
		queryCondition.setQueryString(sb.toString());
		outputVO.setResultList(dam.exeQuery(queryCondition));
		this.sendRtnObject(outputVO);
	}
	
	//查詢兌換截止日期
	public void getDeadline (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT EXC_DEADLINE FROM TBMGM_ACTIVITY_MAIN WHERE ACT_SEQ = :act_seq ");
		
		queryCondition.setObject("act_seq", inputVO.getAct_seq());
		
		queryCondition.setQueryString(sb.toString());		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	//查詢活動名稱&活動適用贈品
	public void getActInfo (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		if (StringUtils.isNotBlank(inputVO.getAct_seq())) {
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT ACT_SEQ, ACT_TYPE, ACT_NAME FROM TBMGM_ACTIVITY_MAIN WHERE ACT_SEQ = :act_seq ");
			
			queryCondition.setObject("act_seq", inputVO.getAct_seq());
			queryCondition.setQueryString(sb.toString());	
			
			List<Map<String,Object>> resultList = dam.exeQuery(queryCondition);
			outputVO.setResultList(resultList);
			
			if(resultList.size() > 0){
				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				
				if("M".equals(resultList.get(0).get("ACT_TYPE"))){
					sb.append("SELECT MGM.GIFT_SEQ, MGM.GIFT_POINTS AS GIFT_EXC_UNI, ");
					sb.append("GIFT.GIFT_NAME, GIFT.GIFT_KIND, ");
					sb.append("GIFT.GIFT_AMOUNT - NVL(GIFT.GIFT_TAKEN ,0) AS GIFT_REMAINING FROM ( ");
					sb.append("SELECT * FROM TBMGM_ACTIVITY_GIFT WHERE ACT_SEQ = :act_seq ) MGM ");
					sb.append("LEFT JOIN TBMGM_GIFT_INFO GIFT ON MGM.GIFT_SEQ = GIFT.GIFT_SEQ ");
					
					queryCondition.setObject("act_seq", inputVO.getAct_seq());
					queryCondition.setQueryString(sb.toString());	
					outputVO.setGiftList(dam.exeQuery(queryCondition));
					
				} else if ("V".equals(resultList.get(0).get("ACT_TYPE"))) {
					sb.append("SELECT GIFT01, GIFT02, GIFT03, GIFT04, GIFT05, GIFT06, GIFT07, GIFT08, GIFT09, GIFT10 ");
					sb.append("FROM TBMGM_VIP WHERE ACT_SEQ = :act_seq AND CUST_ID = :cust_id ");
					
					queryCondition.setObject("act_seq", inputVO.getAct_seq());
					queryCondition.setObject("cust_id", inputVO.getCust_id());
					queryCondition.setQueryString(sb.toString());	
					List<Map<String,Object>> giftList = dam.exeQuery(queryCondition);
					
					List<Map<String,Object>> giftResultList = new ArrayList<>();
					if(giftList.size() > 0){
						Map<String,Object> giftMap = giftList.get(0);
						Set<String> giftSet = giftMap.keySet();
						for(String gift : giftSet){
							if(giftMap.get(gift) != null){
								
								sb = new StringBuffer();
								queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
								
								sb.append("SELECT VIP.*, GIFT.GIFT_EXC_UNI FROM ( ");
								sb.append("SELECT GIFT_SEQ, GIFT_NAME, GIFT_KIND ");
								sb.append("FROM TBMGM_GIFT_INFO WHERE GIFT_SEQ = :gift_seq ) VIP ");
								sb.append("LEFT JOIN ( SELECT GIFT_SEQ, GIFT_REWARD AS GIFT_EXC_UNI FROM TBMGM_ACTIVITY_GIFT ");
								sb.append("WHERE ACT_SEQ = :act_seq ) GIFT ON VIP.GIFT_SEQ = GIFT.GIFT_SEQ ");
								
								queryCondition.setObject("gift_seq", giftMap.get(gift));
								queryCondition.setObject("act_seq", inputVO.getAct_seq());
								queryCondition.setQueryString(sb.toString());
								
								List<Map<String,Object>> list = dam.exeQuery(queryCondition);
								
								if(list.size() > 0){
									Map<String,Object> map = list.get(0);
									giftResultList.add(map);									
								}
							}
						}
					}
					outputVO.setGiftList(giftResultList);
				}
			}
		}
		this.sendRtnObject(outputVO);
	}
	
	//查詢客戶基本資訊
	public void getCustInfo (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT EMP.EMP_NAME, CUST.* FROM ( SELECT AO.EMP_ID, CRM.* FROM ( ");
			sb.append("SELECT BRA_NBR, AO_CODE, CUST_NAME FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id ) CRM ");
			sb.append("LEFT JOIN TBORG_SALES_AOCODE AO ON CRM.AO_CODE = AO.AO_CODE ) CUST ");
			sb.append("LEFT JOIN TBORG_MEMBER EMP ON CUST.EMP_ID = EMP.EMP_ID ");
			
			queryCondition.setObject("cust_id", inputVO.getCust_id());
			queryCondition.setQueryString(sb.toString());	
			
			List<Map<String,Object>> resultList = dam.exeQuery(queryCondition);
			outputVO.setResultList(resultList);
		}
		
		this.sendRtnObject(outputVO);
	}
	
	//查詢收件人資訊
	public void getReceiverInfo (Object body, IPrimitiveMap header) throws Exception {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		
		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			List<Map<String,Object>> resultList = new ArrayList<>();
			Map<String,Object> map = new HashMap<>();
			
			//查：分行電話、分行地址、分行名稱、客戶姓名、客戶所屬行主管姓名
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT CUST.*, EMP.EMP_NAME AS SPV_EMP_NAME, BRH.TEL_NO_MAIN, ");
			sb.append("BRH.ZIP_COD, BRH.CHIN_ADDR, BRH.CHIN_FL_NAME FROM ( ");
			sb.append("SELECT CUST_NAME, BRA_NBR FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id ) CUST ");
			sb.append("LEFT JOIN ( SELECT PRI.PRIVILEGEID, INFO.EMP_NAME, INFO.BRANCH_NBR ");
			sb.append("FROM VWORG_BRANCH_EMP_DETAIL_INFO INFO ");
			sb.append("LEFT JOIN TBSYSSECUROLPRIASS PRI ON INFO.ROLE_ID = PRI.ROLEID ");
			sb.append("WHERE PRI.PRIVILEGEID IN ('010', '011') ) EMP ON CUST.BRA_NBR = EMP.BRANCH_NBR ");
			sb.append("LEFT JOIN TBORG_BRH_CONTACT BRH ON CUST.BRA_NBR = BRH_COD ");
			
			queryCondition.setObject("cust_id", inputVO.getCust_id());
			queryCondition.setQueryString(sb.toString());	
			
			List<Map<String,Object>> list = dam.exeQuery(queryCondition);
			map = list.get(0);
			
			//查：客戶聯絡電話、客戶地址 (即時打電文)
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
			SOT701InputVO inputVO_701 = new SOT701InputVO();
			inputVO_701.setCustID(inputVO.getCust_id());
			
			basicVO vo = new basicVO();
			vo = sot701.getCustBasicData(inputVO_701, header);
			map.put("DAY_COD", vo.getDAY_COD());		//日間電話
			map.put("TEL_NO", vo.getTEL_NO());			//手機
			map.put("CUST_ADDR", vo.getCUST_ADDR());	//地址
			
			resultList.add(map);
			outputVO.setResultList(resultList);
		}
		
		this.sendRtnObject(outputVO);
	}
	
	//禮贈品兌換
	public void apply (Object body, IPrimitiveMap header) throws Exception {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		dam = this.getDataAccessManager();
		
		TBMGM_APPLY_MAINVO mainVO = new TBMGM_APPLY_MAINVO();
		String applySeq = this.getAPPLY_SEQ();
		
		mainVO.setAPPLY_SEQ(applySeq);
		mainVO.setACT_SEQ(inputVO.getAct_seq());
		mainVO.setCUST_ID(inputVO.getCust_id());
		mainVO.setREC_NAME(inputVO.getRec_name());
		mainVO.setREC_TEL_NO(inputVO.getRec_tel_no());
		mainVO.setREC_MOBILE_NO(inputVO.getRec_mobile_no());
		mainVO.setADDRESS(inputVO.getAddress());
		if("M".equals(inputVO.getAct_type())){
			mainVO.setEXCHANGE_POINTS(new BigDecimal(inputVO.getExchange()));
		} else if ("V".equals(inputVO.getAct_type())){
			mainVO.setEXCHANGE_REWARD(new BigDecimal(inputVO.getExchange()));			
		}
		
		dam.create(mainVO);
		
		if(inputVO.getApply_gift_list().size() > 0){
//			TBMGM_APPLY_DETAILVO detailVO = new TBMGM_APPLY_DETAILVO();
//			TBMGM_APPLY_DETAILPK detailPK = new TBMGM_APPLY_DETAILPK();
			
			for(Map<String,String> map : inputVO.getApply_gift_list()){
				TBMGM_APPLY_DETAILVO detailVO = new TBMGM_APPLY_DETAILVO();
				TBMGM_APPLY_DETAILPK detailPK = new TBMGM_APPLY_DETAILPK();
				
				dam = this.getDataAccessManager();
				String giftSeq = map.get("GIFT_SEQ");
				TBMGM_GIFT_INFOVO giftVO = (TBMGM_GIFT_INFOVO) getDataAccessManager().findByPKey(TBMGM_GIFT_INFOVO.TABLE_UID, giftSeq);
				
				int gift_exc_uni = Integer.parseInt(map.get("GIFT_EXC_UNI"));
				int apply_number = Integer.parseInt(map.get("APPLY_NUMBER"));
				int apply_total = gift_exc_uni * apply_number;
				
				// PK
				detailPK.setAPPLY_SEQ(applySeq);
				detailPK.setGIFT_SEQ(giftSeq);
				// VO
				detailVO.setcomp_id(detailPK);
				detailVO.setAPPLY_NUMBER(new BigDecimal(apply_number));
				
				if("M".equals(inputVO.getAct_type())){
					detailVO.setAPPLY_POINTS(new BigDecimal(apply_total));
				} else if ("V".equals(inputVO.getAct_type())){
					detailVO.setAPPLY_REWARD(new BigDecimal(apply_total));			
				}
				
				detailVO.setDELIVERY_STATUS("1");	// 1.分行申請待結算
				
				dam.create(detailVO);
				
				if(giftVO != null){
					if(null == giftVO.getGIFT_TAKEN()){
						giftVO.setGIFT_TAKEN(new BigDecimal(apply_number));
						
					} else {
						BigDecimal gift_taken = giftVO.getGIFT_TAKEN().add(new BigDecimal(apply_number));
						giftVO.setGIFT_TAKEN(gift_taken);						
					}
					
					dam.update(giftVO);
				}
			}
		}
		
		this.sendRtnObject(null);
	}
	
	//取禮贈品申請的申請書序號(APPLY_SEQ)
	private String getAPPLY_SEQ () throws JBranchException {		
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String date = df.format(new Date());
        
		try{
			seqNum = date + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBMGM_MGM")));
		}catch(Exception e){
			sn.createNewSerial("TBMGM_APPLY_MAIN", "00000", 1, "d", null, 1, new Long("99999"), "y", new Long("0"), null);
			seqNum = date + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBMGM_APPLY_MAIN")));
		}
		
		return seqNum;
	}
	
}