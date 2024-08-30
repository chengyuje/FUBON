package com.systex.jbranch.app.server.fps.ins400;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBINS_SPPEDU_DETAILPK;
import com.systex.jbranch.app.common.fps.table.TBINS_SPPEDU_DETAILVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Carley
 * @date 2017/08/15
 * 
 */
@Component("ins400")
@Scope("request")
public class INS400 extends FubonWmsBizLogic {
	DataAccessManager dam = null;
	
	/*
	 * 查詢
	 * 
	 * 2017-08-15 add by Carley
	 * 
	 */
	public void inquire (Object body, IPrimitiveMap header) throws JBranchException, Exception {
		INS400InputVO inputVO = (INS400InputVO) body;
		INS400OutputVO outputVO = new INS400OutputVO();
		DataAccessManager dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		if(StringUtils.isNotBlank(inputVO.getCust_id())){
			outputVO.setResultList(inquire(inputVO.getCust_id(),dam, null));
			
		}else{
			throw new APException("ehl_01_common_022");  //欄位檢核錯誤：*為必要輸入欄位，請輸入後重試
		}

		sendRtnObject(outputVO);
	}
	
	public List<Map<String,Object>> inquire(String custID, DataAccessManager dam,ArrayList<String> aoCode) throws DAOException, JBranchException {
		INS400OutputVO outputVO = new INS400OutputVO();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		//客戶註記：禁銷戶只要顯示"NS"(COMM_NS_YN)、拒銷戶只要顯示"RS"(COMM_RS_YN)、特定客戶(SP_CUST_YN)
		sql.append("SELECT MAST.CUST_NAME, MAST.AGE, MAST.EDUCATION_STAT, MAST.CUST_RISK_ATR, MAST.GENDER, MAST.BIRTH_DATE, ");
		sql.append("MAST.KYC_DUE_DATE, MAST.ANNUAL_INCOME_AMT, NVL(NOTE.SP_CUST_YN, 'N') SP_CUST_YN, ");
		sql.append("NVL(NOTE.COMM_RS_YN, 'N') COMM_RS_YN, NVL(NOTE.COMM_NS_YN, 'N') COMM_NS_YN, ");
		sql.append("NVL(NOTE.OBU_YN, 'N') OBU_YN, NVL(NOTE.PROF_INVESTOR_YN, 'N') PROF_INVESTOR_YN, ");
		sql.append("NVL(NOTE.SAL_ACC_YN, 'N') SAL_ACC_YN FROM TBCRM_CUST_MAST MAST  ");
		sql.append("LEFT JOIN TBCRM_CUST_NOTE NOTE ON MAST.CUST_ID = NOTE.CUST_ID ");
		sql.append("WHERE MAST.CUST_ID = :cust_id ");
		sql.append("AND MAST.AO_CODE IN ( :loginAoCode ) ");
		
		qc.setObject("cust_id", custID);
		qc.setObject("loginAoCode", aoCode == null ? getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST) : aoCode);
		
		qc.setQueryString(sql.toString());
		List<Map<String,Object>> list = dam.exeQuery(qc);
		
//		outputVO.setResultList(list);
		return list;
	}
	
	/**
	 * INS431_CHILDREN 計算教育費用所需資金
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException
	 */	
	public void createChild(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		INS400InputVO inputVO = (INS400InputVO)body;
		dam = this.getDataAccessManager();
		List<Map<String,Object>> list = inputVO.getList();
		for (Map<String,Object> map: list) {
			if (("create").equals(map.get("action"))) {
				create(map,inputVO.getCust_id());
			} else if (("update").equals(map.get("action"))) {
				update(map,inputVO.getCust_id());
			}
		}
		this.sendRtnObject(null);
	}
		
	//新增child
	public String create(Map<String,Object> map, String cust_id) throws JBranchException {
		dam = this.getDataAccessManager();
		TBINS_SPPEDU_DETAILPK pk = new TBINS_SPPEDU_DETAILPK();
		pk.setCUST_ID(cust_id);
		pk.setCHILD_NAME((String)map.get("CHILD_NAME"));
		TBINS_SPPEDU_DETAILVO vo = new TBINS_SPPEDU_DETAILVO();
		vo.setcomp_id(pk);
		vo.setCHILD_BIRTHDATE(new Timestamp(((Double)map.get("CHILD_BIRTH")).longValue()));
		vo.setEND_EDU_AGE(new BigDecimal((Double)map.get("EDU_END")));
		vo.setEDU_AMT(new BigDecimal((Double)map.get("EDU_COST")));
		dam.create(vo);
		return cust_id;
	}
	
	//ws用新增child
	public String createChildDataForMobile(Map<String,Object> map, String cust_id) throws JBranchException, ParseException {
		dam = this.getDataAccessManager();
		TBINS_SPPEDU_DETAILPK pk = new TBINS_SPPEDU_DETAILPK();
		pk.setCUST_ID(cust_id);
		pk.setCHILD_NAME((String)map.get("childName"));
		TBINS_SPPEDU_DETAILVO vo = new TBINS_SPPEDU_DETAILVO();
		vo.setcomp_id(pk);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date parseDate = sdf.parse((String)map.get("childBirthdate"));
		vo.setCHILD_BIRTHDATE(new Timestamp(parseDate.getTime()));
		vo.setEND_EDU_AGE(new BigDecimal((Double)map.get("endEduAge")));
		vo.setEDU_AMT(new BigDecimal((Double)map.get("eduAmt")));
		dam.create(vo);
		return cust_id;
	}
	
	// 更新 child
	public void update(Map<String,Object> map, String cust_id)throws JBranchException, ParseException {
		dam = this.getDataAccessManager();
		TBINS_SPPEDU_DETAILPK pk = new TBINS_SPPEDU_DETAILPK();
		pk.setCUST_ID(cust_id);
		pk.setCHILD_NAME((String)map.get("CHILD_NAME"));
		TBINS_SPPEDU_DETAILVO vo = 
				(TBINS_SPPEDU_DETAILVO) dam.findByPKey(TBINS_SPPEDU_DETAILVO.TABLE_UID, pk);
		vo.setCHILD_BIRTHDATE(new Timestamp(((Double)map.get("CHILD_BIRTH")).longValue()));
		vo.setEND_EDU_AGE(new BigDecimal((Double)map.get("EDU_END")));
		if(org.springframework.util.StringUtils.isEmpty(map.get("EDU_COST"))) {
			vo.setEDU_AMT(new BigDecimal(0));			
		} else {
			vo.setEDU_AMT(new BigDecimal(String.valueOf(map.get("EDU_COST"))));
		}
		
		dam.update(vo);
	}
	
	// ws用更新 child
	public void updateChildDataForMobile(Map<String,Object> map, String cust_id)throws JBranchException, ParseException {
		dam = this.getDataAccessManager();
		TBINS_SPPEDU_DETAILPK pk = new TBINS_SPPEDU_DETAILPK();
		pk.setCUST_ID(cust_id);
		pk.setCHILD_NAME((String)map.get("childName"));
		TBINS_SPPEDU_DETAILVO vo = 
				(TBINS_SPPEDU_DETAILVO) dam.findByPKey(TBINS_SPPEDU_DETAILVO.TABLE_UID, pk);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date parseDate = sdf.parse((String)map.get("childBirthdate"));
		vo.setCHILD_BIRTHDATE(new Timestamp(parseDate.getTime()));
		vo.setEND_EDU_AGE(new BigDecimal((Double)map.get("endEduAge")));
		if(org.springframework.util.StringUtils.isEmpty(map.get("eduAmt"))) {
			vo.setEDU_AMT(new BigDecimal(0));			
		} else {
			vo.setEDU_AMT(new BigDecimal(String.valueOf(map.get("eduAmt"))));
		}
		
		dam.update(vo);
	}
	
	/**
	 * INS431_CHILDREN 計算教育費用所需資金查詢
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException
	 */
	public void inquireChild(Object body, IPrimitiveMap<Object> headers) throws JBranchException {
		INS400InputVO inputVO = (INS400InputVO)body;
		INS400OutputVO outputVO = new INS400OutputVO();
		//取得子女教育所需基金細項的list
		List<Map<String, Object>> outputList = new ArrayList<Map<String,Object>>();
		outputList = inquireChildCommon(inputVO.getCust_id());
		outputVO.setOutputList(outputList);
		sendRtnObject(outputVO);
	  }
	
	public List<Map<String, Object>> inquireChildCommon(String custId) throws JBranchException {
		//取得子女教育所需基金細項的list
		List<Map<String, Object>> outputList = new ArrayList<Map<String,Object>>();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append(" SELECT CHILD_NAME,to_char(CHILD_BIRTHDATE,'YYYY/MM/DD') as CHILD_BIRTH,END_EDU_AGE as EDU_END,EDU_AMT as EDU_COST,round((trunc(LASTUPDATE - CHILD_BIRTHDATE))/365,0) CHILD_AGE,TO_CHAR(LASTUPDATE,'YYYY/MM/DD')LASTUPDATE ");
		sb.append(" FROM TBINS_SPPEDU_DETAIL WHERE CUST_ID = :custID");
		qc.setObject("custID", custId);
		
		qc.setQueryString(sb.toString());
		outputList = dam.exeQuery(qc);
		return outputList;
	}
	
	//ws用查詢子女教育所需資金細項
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> inquireChildDataForMobile(String custId) throws JBranchException, ParseException {
		//取得子女教育所需基金細項的list
		List<Map<String, Object>> outputList = inquireChildCommon(custId);		
		//存放修改過名稱的map
		List<Map<String, Object>> outputModList = new ArrayList<Map<String,Object>>();		
		// 日期轉換
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); 
		// 轉回Date用
		SimpleDateFormat sdfToDate = new SimpleDateFormat("yyyy/MM/dd"); 
		
		for(Map<String, Object> map : outputList){
			//存放修改過名稱的資料
			Map dataMap = new HashMap();
			//childName : 子女姓名
			dataMap.put("childName", map.get("CHILD_NAME"));
			//childBirthdate : 子女出生日期
			dataMap.put("childBirthdate", map.get("CHILD_BIRTH") != null 
					? sdf.format(sdfToDate.parse(map.get("CHILD_BIRTH").toString())) : "");
			//lastupdate : 起算日
			dataMap.put("lastupdate", map.get("LASTUPDATE") !=null 
					? sdf.format(sdfToDate.parse(map.get("LASTUPDATE").toString())) : "");
			//endEduAge : 學齡結束年紀
			dataMap.put("endEduAge", map.get("EDU_END"));
			//eduAmt : 教育需求金額
			dataMap.put("eduAmt", map.get("EDU_COST"));
			//存放修改過名稱的map
			outputModList.add(dataMap);
		}
		
		return outputModList;
	}
	/**
	 * INS431_CHILDREN 計算教育費用所需資金
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException
	 */
	public void deleteChild(Object body,IPrimitiveMap<Object>headers)throws JBranchException{
		INS400InputVO inputVO = (INS400InputVO)body;
		dam = this.getDataAccessManager();
		TBINS_SPPEDU_DETAILPK pk = new TBINS_SPPEDU_DETAILPK();
		pk.setCUST_ID(inputVO.getCust_id());
		pk.setCHILD_NAME(inputVO.getChild_Name());
		TBINS_SPPEDU_DETAILVO vo = (TBINS_SPPEDU_DETAILVO) dam
				.findByPKey(TBINS_SPPEDU_DETAILVO.TABLE_UID, pk);
		dam.delete(vo);

		this.sendRtnObject(null);
	}
	
	//ws用刪除子女教育所需基金細項
	public void deleteChildDataForMobile(String custId,Map<String,Object> map)throws JBranchException{
		dam = this.getDataAccessManager();
		TBINS_SPPEDU_DETAILPK pk = new TBINS_SPPEDU_DETAILPK();
		pk.setCUST_ID(custId);
		pk.setCHILD_NAME(map.get("childName").toString());
		TBINS_SPPEDU_DETAILVO vo = (TBINS_SPPEDU_DETAILVO) dam
				.findByPKey(TBINS_SPPEDU_DETAILVO.TABLE_UID, pk);
		dam.delete(vo);
	}
	
	//ws儲存子女教育所需基金細項
	public void saveChildDataForMobile (String custId,List<Map<String, Object>> etuDetail) throws JBranchException, Exception {
		/**子女教育所需基金細項**/
		//來自DB的資料
		List<Map<String, Object>> eduExplist = inquireChildDataForMobile(custId);
		
		//新增資料庫所沒有的資料
		for1:
		for(Map<String, Object> mapFromData : etuDetail){
			
			for(Map<String, Object> mapFromDB : eduExplist){
								
				if( mapFromData.get("childName").equals(mapFromDB.get("childName"))){
					
					if(!new BigDecimal((Double)mapFromData.get("eduAmt")).equals(mapFromDB.get("eduAmt"))){
						updateChildDataForMobile(mapFromData, custId);
					}
					
					continue for1;
				} 				
			}
			
			createChildDataForMobile(mapFromData, custId);
		}
		
		//刪除資料庫的資料
		for2:
		for(Map<String, Object> mapFromDB : eduExplist){
			
			for(Map<String, Object> mapFromData : etuDetail){
				
				if(mapFromDB.get("childName").equals(mapFromData.get("childName"))){
					continue for2;
				}
			}
			
			deleteChildDataForMobile(custId,mapFromDB);
		}
				
	}
	
}