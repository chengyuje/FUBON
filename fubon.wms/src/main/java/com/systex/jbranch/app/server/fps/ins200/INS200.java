package com.systex.jbranch.app.server.fps.ins200;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.ibm.icu.text.SimpleDateFormat;
import com.systex.jbranch.app.server.fps.ins.parse.WSMappingParserUtils;
import com.systex.jbranch.app.server.fps.ins210.INS210;
import com.systex.jbranch.app.server.fps.ins810.FubonIns810;
import com.systex.jbranch.app.server.fps.ins810.INS810InputVO;
import com.systex.jbranch.app.server.fps.ins810.PolicySuggestInputVO;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.PdfUtil;
import com.systex.jbranch.fubon.commons.ValidUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("ins200")
@Scope("request")
public class INS200 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(INS200.class);
	
	@Autowired @Qualifier("ins810")
	private FubonIns810 ins810;
	
	public FubonIns810 getIns810() {
		return ins810;
	}

	public void setIns810(FubonIns810 ins810) {
		this.ins810 = ins810;
	}
	
	@Autowired @Qualifier("ins210")
	private INS210 ins210;
	
	public INS210 getIns210() {
		return ins210;
	}

	public void setIns210(INS210 ins210) {
		this.ins210 = ins210;
	}
	
	// ============================== WEB 調用 ==============================
	/**
	 * 查詢客戶基本資料
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void query(Object body,IPrimitiveMap header) throws Exception {
		INS200InputVO inputVO = (INS200InputVO) body;
		INS200OutputVO outputVO = new INS200OutputVO();
		dam = this.getDataAccessManager();
		
		ArrayList<String> loginAO = (ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
		String LoginBraNbr = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
		
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT CUST.CUST_ID,");
		sb.append("       CUST.CUST_NAME,");
		sb.append("       CUST.GENDER,");
		sb.append("       CUST.BIRTH_DATE,");
		sb.append("       PLAN.PLAN_KEYNO,");
		sb.append("       PLAN.PLAN_DATE,");
		sb.append("       ORG.EMP_NAME,");
		sb.append("       RPT.FILE_NAME,");
		sb.append("       RPT.REPORT_FILE ");
		sb.append("  FROM (SELECT '1' SRC, CUST_ID, GENDER, CUST_NAME, BIRTH_DATE FROM TBCRM_CUST_MAST");
		sb.append("        WHERE CUST_ID = :custid ");
		sb.append("        AND ((AO_CODE IS NULL AND BRA_NBR = :loginBraNbr) OR (AO_CODE IS NOT NULL AND AO_CODE in (:loginAO))) ");
		sb.append("       UNION ");
		sb.append("        SELECT '2' SRC, CUST_ID, GENDER, CUST_NAME, BIRTH_DATE ");
		sb.append("        FROM TBINS_CUST_MAST WHERE CUST_ID = :custid ");
		sb.append("        ORDER BY SRC FETCH FIRST 1 ROWS ONLY) CUST ");
		sb.append("   LEFT JOIN TBINS_PLAN_MAIN PLAN ON PLAN.CUST_ID = CUST.CUST_ID ");
		sb.append("   LEFT JOIN TBINS_REPORT RPT ON RPT.PLAN_ID = PLAN.PLAN_KEYNO ");
		sb.append("   LEFT JOIN TBORG_MEMBER ORG ON ORG.EMP_ID = PLAN.CREATOR ");
		sb.append("ORDER BY PLAN.PLAN_DATE DESC ");
		qc.setObject("custid", inputVO.getCUST_ID());
		qc.setObject("loginBraNbr", LoginBraNbr);
		qc.setObject("loginAO", loginAO);
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> queryData = dam.exeQuery(qc);
		if (queryData.size() > 0) {
			// 計算保險年齡，並將保險年齡加入查詢資料
			queryData.get(0).put("age", policyAge(new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(queryData.get(0).get("BIRTH_DATE")))));
			
			// Blob
			if (queryData.get(0).get("REPORT_FILE") != null) {
				Blob blob = (Blob) queryData.get(0).get("REPORT_FILE");
				byte[] blobAsBytes = blob.getBytes(1, (int) blob.length());
				queryData.get(0).put("REPORT_FILE", blobAsBytes);
				blob.free();
			}
		}
		
		outputVO.setResultList(queryData);
		this.sendRtnObject(outputVO);
	}
	
	public void takeLastRecord(Object body,IPrimitiveMap header) throws Exception {
		INS200InputVO inputVO = (INS200InputVO) body;
		List<String> url_list =  new ArrayList<String>();
		dam = this.getDataAccessManager();
		
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("select RPT.FILE_NAME, RPT.REPORT_FILE from TBINS_PLAN_MAIN PLAN ");
		sb.append("LEFT JOIN TBINS_REPORT RPT ON RPT.PLAN_ID = PLAN.PLAN_KEYNO ");
		sb.append("WHERE PLAN.CUST_ID = :custid ");
		sb.append("ORDER BY PLAN.PLAN_DATE DESC ");
		qc.setObject("custid", inputVO.getCUST_ID());
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> queryData = dam.exeQuery(qc);
		
		if(queryData.size() > 0 && queryData.get(0).get("REPORT_FILE") != null){
			String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			String uuid = UUID.randomUUID().toString();
			String fileName = queryData.get(0).get("FILE_NAME") + ".pdf";
			Blob blob = (Blob) queryData.get(0).get("REPORT_FILE");
			int blobLength = (int) blob.length();
			byte[] blobAsBytes = blob.getBytes(1, blobLength);

			File targetFile = new File(filePath, uuid);
			FileOutputStream fos = new FileOutputStream(targetFile);
			fos.write(blobAsBytes);
			fos.close();
			url_list.add("doc//INS//INS200_COVER.pdf");
			url_list.add("temp//" + uuid);
			
			// 合併所有PDF＆加密碼
			String reportURL = PdfUtil.mergePDF(url_list, inputVO.getCUST_ID());
//			String reportURL = PdfUtil.mergePDF(url_list, false);
			this.notifyClientToDownloadFile(reportURL, fileName);
		 }
		this.sendRtnObject(null);
	}
	
	/**
	 * 取得各頁籤 相關參數
	 * @param body
	 * @param header
	 * @throws DAOException
	 * @throws JBranchException
	 * 
	 * OUTPUT
	 * PARA_NO, CAL_DESC, VAILDPARAMETER, 疾病列表 DiseaseList
	 */
	public void vaildParameter (Object body,IPrimitiveMap header) throws DAOException, JBranchException {
		INS200InputVO inputVO = (INS200InputVO) body;
		INS200OutputVO outputVO = new INS200OutputVO();
		
		dam = this.getDataAccessManager();
		// GET PARA_NO
		List<Map<String, Object>>  list = getIns810().getPlanNo(new PolicySuggestInputVO(inputVO.getPARA_TYPE(), null, null,null), dam);
		BigDecimal paraNo = new BigDecimal(getIns810().getSingleMapValue(list, "PARA_NO"));
		outputVO.setPara_no(paraNo);
		outputVO.setCal_desc(getIns810().getSingleMapValue(list, "CAL_DESC"));
		outputVO.setVaildparameter(vaildParameter(inputVO.getPARA_TYPE(), dam));
		
		if("3".equals(inputVO.getPARA_TYPE())){
			getHospitalList(ObjectUtils.toString(paraNo));
		}
		
		if ("4".equals(inputVO.getPARA_TYPE())) {
			
			// type3_para_no 為醫療險 與 重大疾病頁籤時需要進行使用
			// 用途:算出缺口 CALL CODE-INS210 calculaGap >> calculaDemandAnalysis >> getHospitalizationDays
			List<Map<String, Object>> type3_list = getIns810().getPlanNo(new PolicySuggestInputVO("3", null, null,null), dam);
			outputVO.setType3_para_no(new BigDecimal(type3_list.get(0).get("PARA_NO").toString()));
			outputVO.setDiseaseList(getDiseaseList(paraNo));
		}
		
		this.sendRtnObject(outputVO);
	}
	
	/**
	 * 取得照護相關下拉資料
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getLtcare(Object body, IPrimitiveMap header) throws JBranchException {
		INS200OutputVO return_VO = new INS200OutputVO();
		dam = this.getDataAccessManager();
		
		// PARA_NO
		List<Map<String, Object>> paraNoList= getIns810().getPlanNo(new PolicySuggestInputVO("4", null, null,null), dam);
		String paraNo = getIns810().getSingleMapValue(paraNoList, "PARA_NO");
		return_VO.setLtcareList(getLtcareList(paraNo));
		this.sendRtnObject(return_VO);
	}
	
	/**
	 * 取得照護相關下拉資料清單
	 * @param paraNo
	 * @param header
	 * @throws JBranchException
	 */
	private List<Map<String, Object>> getLtcareList(String paraNo) throws DAOException, JBranchException {
		// INS.CARE_WAY
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> careWayXMLMap = xmlInfo.doGetVariable("INS.CARE_WAY", FormatHelper.FORMAT_3); // 1-居家式 2-社區式 3-機構式 
		
		// 組合成要得
		List<Map<String, Object>> ltcareList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> allCareWayList = getAllCareWayList(paraNo, null, null);
		for(Entry<String, String> entry : careWayXMLMap.entrySet()) {
			Map<String, Object> careWayMap = new HashMap<String, Object>();
			careWayMap.put("DATA", entry.getKey());
			careWayMap.put("LABEL", entry.getValue());
			careWayMap.put("ltcareList", findByCareWayIndex(entry.getKey(), allCareWayList));
			ltcareList.add(careWayMap);
		}
		return ltcareList;
	} 
	
	// ============================== WS 調用 ==============================
	/**
	 * 保險特定規劃2階API-查詢保險規劃歷史規劃
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryHistoryInsurancePlan(Object body,IPrimitiveMap header) throws JBranchException, Exception {
		Map inputMap = (Map)body;
		Map<String,Object> finalmap = new HashMap<String, Object>();
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		for(Map<String,Object> map : (List<Map<String, Object>>)queryHisPlanMain(inputMap)){
			Map<String,Object> mapOutput = new HashMap<String, Object>();
			mapOutput.put("planKeyNo", map.get("PLAN_KEYNO"));
			String date = sdf.format(map.get("PLAN_DATE"));
			mapOutput.put("date", date);
			mapOutput.put("sppName", map.get("MAIN_NAME"));
			mapOutput.put("braName", map.get("BRANCH_NAME"));
			mapOutput.put("empName", map.get("EMP_NAME"));
			result.add(mapOutput);
		}
		finalmap.put("custId", ObjectUtils.toString(inputMap.get("custId")));
		finalmap.put("plan", result);
		this.sendRtnObject(finalmap);
	}
	
	/**
	 * 調用取得 保險規劃 歷史紀錄
	 * @param resoureMap
	 * @return
	 * @throws JBranchException
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryHisPlanMain (Map<String, Object> resoureMap) throws JBranchException, Exception {
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		StringBuffer sql = new StringBuffer();
		List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
		
		// 資料準備
		String custId = ObjectUtils.toString(resoureMap.get("custId"));
		String startDate = ObjectUtils.toString(resoureMap.get("startDate"));
		String endDate = ObjectUtils.toString(resoureMap.get("endDate"));
		Date planSDate = startDate.isEmpty() ? null : sdf.parse(startDate) ;
		Date planEDate = endDate.isEmpty() ? null : sdf.parse(endDate) ;
		
		if(!custId.isEmpty()){
			sql.append("SELECT plan.CUST_ID,plan.PLAN_KEYNO,plan.PLAN_DATE, ");
			sql.append("'保險規劃-'||TO_CHAR(B.CREATETIME, 'yyyymmdd') MAIN_NAME,INFO.BRANCH_NAME, INFO.EMP_NAME ");
			sql.append("FROM TBINS_PLAN_MAIN plan ");
			sql.append("JOIN TBINS_PLAN_DTL B ON plan.PLAN_KEYNO = B.PLAN_KEYNO ");
			sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON plan.CREATOR = INFO.EMP_ID ");
			sql.append("WHERE 1 = 1 ");
			
			if (planSDate != null){
				sql.append("AND TO_CHAR(PLAN_DATE, 'yyyyMMdd') >= TO_CHAR( :planSDate, 'yyyyMMdd') ");
				qc.setObject("planSDate", planSDate);
			}
			if (planEDate != null){
				sql.append("AND TO_CHAR(PLAN_DATE, 'yyyyMMdd') <= TO_CHAR( :planEDate, 'yyyyMMdd') ");
				qc.setObject("planEDate", planEDate);
			}
			sql.append("AND plan.CUST_ID = :custId ");
			sql.append("ORDER BY plan.PLAN_DATE DESC ");
			qc.setObject("custId", custId);
			qc.setQueryString(sql.toString());
			list = dam.exeQuery(qc);
		}

		return list;
	}
	
	/**
	 * WS 調用取得 getInsuranceRefData 保險規劃參數
	 * @param body mp
	 * @param header
	 * @throws JBranchException
	 */
	public void getInsuranceRefData(Object body, IPrimitiveMap header) throws JBranchException {
		GenericMap inputGMap = new GenericMap((Map) body);
		System.out.println(new Gson().toJson(inputGMap.getParamMap()));
		 
		Integer[] paraTypeArr = new Integer[]{1, 2, 3, 4};
		Map<String, Object> insuranceRefDataMap = new HashMap<String, Object>();
		List<Map<String, Object>> recommandList = new ArrayList<Map<String, Object>>();
		
		String loginBraNbr = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
		ArrayList<String> loginAO = (ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);

		String custId = inputGMap.getStr("custId", null);
		String insuranceAge = ObjectUtils.toString(inputGMap.getBigDecimal("insuranceAge").intValue());
		
		for(Integer paraType : paraTypeArr) {
			getInsuranceRefDataMap(paraType, custId, insuranceAge, loginBraNbr, loginAO, insuranceRefDataMap, recommandList);
		}
		insuranceRefDataMap.put("planKeyNo", "PLAN" + getIns810().doGetSeqLdap("TBINS_SEQ" , "5" , "0"));
		insuranceRefDataMap.put("recommand", recommandList);
		
		System.out.println(new Gson().toJson(insuranceRefDataMap));
		this.sendRtnObject(insuranceRefDataMap);
	}
	
	
	// ============================== 共用 ==============================
	/**
	 * 檢查參數是否都正確
	 * @param paraType
	 * @param dam
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private boolean vaildParameter(String paraType, DataAccessManager dam) throws DAOException, JBranchException{
		//檢核參數設定
		Integer countHeader = Integer.valueOf(String.valueOf(countHeader(paraType, dam).get(0).get("COUNT")));
		
		//檢核推薦商品
		Integer countSuggest = Integer.valueOf(String.valueOf(countSug(paraType, dam).get(0).get("COUNT")));
		return countHeader > 0 && countSuggest > 0;
	}
	
	/**
	 * 檢核參數設定 檢查是否有設定過參數
	 * @param paraType
	 * @param dam
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> countHeader(String paraType, DataAccessManager dam) throws DAOException, JBranchException{
	    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	    StringBuffer sb = new StringBuffer();  
			
		//檢核參數設定
		sb.append("SELECT COUNT(*) AS COUNT FROM VWINS_PARA_HEADER ");
		sb.append("WHERE PARA_TYPE = :para_type ");
		qc.setObject("para_type", paraType);

	    qc.setQueryString(sb.toString());
	    return dam.exeQuery(qc);
	}
	
	/**
	 * 檢核推薦商品 檢查是否有推薦商品
	 * @param paraType
	 * @param dam
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> countSug(String paraType, DataAccessManager dam) throws DAOException, JBranchException{
	    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	    StringBuffer sb = new StringBuffer();  
			
		//檢核推薦商品
		sb.append("SELECT COUNT(*) AS COUNT FROM VWPRD_INS_SUGGEST ");
		sb.append("WHERE STATUS='A' ");
		sb.append("AND TRUNC(EFFECT_DATE) <= TRUNC(SYSDATE) ");
		sb.append("AND NVL(TRUNC(EXPIRY_DATE), SYSDATE) >= TRUNC(SYSDATE) ");
		sb.append("AND PARA_TYPE = :para_type ");
		qc.setObject("para_type", paraType);
		
	    qc.setQueryString(sb.toString());
	    return dam.exeQuery(qc);
	}
	
	/**
	 * 家族遺傳或最擔心疾病列表
	 * @param paraNo
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private List<Map<String, Object>> getDiseaseList (BigDecimal paraNo) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);;
		StringBuffer sb = new StringBuffer();;
		
		sb.append("SELECT D_KEYNO as DATA, DIS_NAME as LABEL, 'N' as choose, TYPE_CANCER, TYPE_MAJOR, TYPE_LT, DIS_DESC ");
		sb.append("FROM TBINS_PARA_DISEASE WHERE PARA_NO = :para_no ORDER BY SEQ_NUM ");
		qc.setObject("para_no", paraNo);
		qc.setQueryString(sb.toString());
		return dam.exeQuery(qc);
	}
	
	/**
	 * 取得照顧方針 全部 (先不過濾 模式 1-居家式 2-社區式 3-機構式 )
	 * @param paraNo
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public List<Map<String, Object>> getAllCareWayList(String paraNo, String careWay, String careStyle) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.LT_KEYNO AS DATA, A.CARE_WAY, A.CARE_STYLE, A.MONTH_AMT, B.PARAM_NAME AS LABEL ");
		sql.append("FROM TBINS_PARA_LTCARE A, ");
		sql.append("(SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE='INS.CARE_STYLE') B ");
		sql.append("WHERE A.CARE_STYLE = B.PARAM_CODE ");
		sql.append("AND A.PARA_NO = :para_no ");
		queryCondition.setObject("para_no", paraNo);
		
		if(careWay != null) {
			sql.append("AND A.CARE_WAY = :careWay ");
			queryCondition.setObject("careWay", careWay);
		}
		
		if(careStyle != null) {
			sql.append("AND A.CARE_STYLE = :careStyle ");
			queryCondition.setObject("careStyle", careStyle);
		}
		
		
		queryCondition.setQueryString(sql.toString());
		return dam.exeQuery(queryCondition);
	}
	
	/**
	 * 根據照護模式 1-居家式 2-社區式 3-機構式 
	 * 取得實際照護方式 List
	 * @param keyIndex
	 * @param allCareWayList
	 * @return
	 */
	private List<Map<String, Object>> findByCareWayIndex(String keyIndex, List<Map<String, Object>> allCareWayList) {
		List<Map<String, Object>> findResultList = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> careWayMap : allCareWayList) {
			if(ObjectUtils.toString(careWayMap.get("CARE_WAY")).equals(keyIndex)) {
				findResultList.add(careWayMap);
			}
		}
		return findResultList;
	}
	
	/**
	 * 檢核身分證字號
	 * @param body
	 * @return outputVO-Vaildcustid
	 * @throws Exception
	 * */
	public void vaildCUSTID(Object body,IPrimitiveMap header) throws JBranchException {
		INS200InputVO inputVO = (INS200InputVO) body;
		INS200OutputVO outputVO = new INS200OutputVO();
		outputVO.setVaildcustid(ValidUtil.isValidIDorRCNumber(inputVO.getCUST_ID()));
		sendRtnObject(outputVO);
	}
	
	/**
	 * 計算保險年齡
	 * @param body
	 * @return outputVO-Age
	 * @throws Exception
	 * */
	public void countAge(Object body,IPrimitiveMap header) throws JBranchException {
		INS200InputVO inputVO = (INS200InputVO) body;
		INS200OutputVO outputVO = new INS200OutputVO();
		outputVO.setAge(policyAge(new Date(inputVO.getBIRTHDAY())));
		sendRtnObject(outputVO);
	}
	
	/**
	 * 統一取得保險年齡
	 * @param birthday
	 * @return
	 * @throws APException
	 */
	private int policyAge(Date birthday) throws APException{
		INS810InputVO ins810inputVO = new INS810InputVO();
		ins810inputVO.setBirthday(birthday);
		return getIns810().getAge(ins810inputVO).getAge();
	}
	
	//======================== WS 資料整理 ===========================	
	/**
	 * 組合 OUTPUT 相關資料
	 * @param paraType 1:壽險 / 2:意外險 / 3:醫療險 / 4.重大疾病
	 * @param insuranceRefDataMap 組合後最後要 Return 的 output 
	 * @param recommandList 商品推薦
	 * @throws JBranchException 
	 * @throws DAOException 
	 */
	private void getInsuranceRefDataMap(Integer paraType, String custId, String insuranceAge, String loginBraNbr, ArrayList<String> loginAO,
			Map<String, Object> insuranceRefDataMap, List<Map<String, Object>> recommandList) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		// GET PARA_NO
		List<Map<String, Object>>  list = getIns810().getPlanNo(new PolicySuggestInputVO(paraType.toString(), null, null,null), dam);
		BigDecimal paraNo = new BigDecimal(getIns810().getSingleMapValue(list, "PARA_NO"));
		String paraNoStr = ObjectUtils.toString(paraNo);
		
		// 檢核參數
		boolean hasParam = vaildParameter(ObjectUtils.toString(paraType), dam);
		
		List<Map<String, Object>> planList = null;
		List<Map<String, Object>> suggestList = null;
		if(hasParam) {
			planList = getIns210().getPlanList(ObjectUtils.toString(paraType), custId, loginBraNbr, loginAO, "1");
			suggestList = getIns210().getSuggestProd(paraNoStr, null, insuranceAge);
		}
		
		switch(paraType) {
			case 1:{ // 壽險
				Map<String, Object> lifeMap = new HashMap<String, Object>();
				lifeMap.put("paraNo", paraNoStr);
				lifeMap.put("current", WSMappingParserUtils.getCurrentPolicyList(planList, paraType));
				insuranceRefDataMap.put("life", lifeMap = hasParam && CollectionUtils.isNotEmpty(suggestList) ? lifeMap : null);
				break;
			}
			case 2:{ // 意外險
				Map<String, Object> accidentMap = new HashMap<String, Object>();
				accidentMap.put("paraNo", paraNoStr);
				accidentMap.put("current", WSMappingParserUtils.getCurrentPolicyList(planList, paraType));
				insuranceRefDataMap.put("accident", accidentMap = hasParam && CollectionUtils.isNotEmpty(suggestList) ? accidentMap : null);
				break;
			}
			case 3:{ // 醫療險
				Map<String, Object> medicalMap = new HashMap<String, Object>();
				medicalMap.put("paraNo", paraNoStr);
				medicalMap.put("isHS", false);
				medicalMap.put("hospital", getHospitalList(paraNoStr));
				medicalMap.put("current", WSMappingParserUtils.getCurrentPolicyList(planList, paraType));
				insuranceRefDataMap.put("medical", medicalMap = hasParam && CollectionUtils.isNotEmpty(suggestList) ? medicalMap : null);
				break;
			}
			case 4:{ // 重大疾病
				Map<String, Object> dreadDiseaseMap = new HashMap<String, Object>();
				dreadDiseaseMap.put("paraNo", paraNoStr);
				dreadDiseaseMap.put("disease", getDiseaseList(paraNoStr));
				dreadDiseaseMap.put("homeCare", getHomeCareList(paraNoStr));
				dreadDiseaseMap.put("current", WSMappingParserUtils.getCurrentPolicyList(planList, paraType));
				insuranceRefDataMap.put("dreadDisease", dreadDiseaseMap = hasParam && CollectionUtils.isNotEmpty(suggestList) ? dreadDiseaseMap : null);
				break;
			}
			default : {
				break;
			}
		}
		// 商品推薦
		recommandList.addAll(getRecommandList(suggestList, paraType));
	}
	
	/**
	 * 取得醫院相關訊息清單
	 * @paraNo para no
	 * @return
	 * @throws JBranchException 
	 * @throws DAOException 
	 */
	private List<Map<String, Object>> getHospitalList(String paraNo) throws DAOException, JBranchException {
		dam = getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		qc.setObject("paraNo", paraNo);
		qc.setQueryString(getHospitalListSql());
		List<Map<String, Object>> allHospitalList = dam.exeQuery(qc);
		
		List<Map<String, Object>> hospitalList = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> allHospitalMap :allHospitalList) {
			Map<String, Object> tempH = CollectionSearchUtils.findMapInColleciton(hospitalList, "hospitalType", allHospitalMap.get("HOSPITAL_TYPE"));
			Map<String, Object> hospitalMap = MapUtils.isNotEmpty(tempH) ? tempH : new HashMap<String, Object>();
			
			// 醫院是新的 TYPE 病房類型一定是新的
			// 醫院是存在 TYPE 病房可能是新的也可能是存在的
			
			if(hospitalMap.size() == 0) {
				hospitalMap.put("hospitalType", allHospitalMap.get("HOSPITAL_TYPE"));
				hospitalMap.put("hospitalName", allHospitalMap.get("HOSPITAL_NAME"));
				List<Map<String, Object>> wardList = new ArrayList<Map<String, Object>>();
				Map<String, Object> newWardMap = new HashMap<String, Object>();
				newWardMap.put("wardType", allHospitalMap.get("WARD_TYPE"));
				newWardMap.put("wardName", allHospitalMap.get("WARD_NAME"));
				newWardMap.put("dayAmt", new BigDecimal(ObjectUtils.toString(allHospitalMap.get("DAY_AMT"))));
				wardList.add(newWardMap);
				hospitalMap.put("ward", wardList);
				hospitalList.add(hospitalMap);
			} else {
				Object tempWObj = hospitalMap.get("ward");
				List<Map<String, Object>> tempWList = tempWObj != null ? (List<Map<String, Object>>) tempWObj : new ArrayList<Map<String, Object>>();
				Map<String, Object> tempW = CollectionSearchUtils.findMapInColleciton(hospitalList, "wardType", allHospitalMap.get("WARD_TYPE"));
				Map<String, Object> wardMap = MapUtils.isNotEmpty(tempW) ? tempW : new HashMap<String, Object>();
				
				if(tempWList.size() == 0 || wardMap.size() == 0) {
					wardMap.put("wardType", allHospitalMap.get("WARD_TYPE"));
					wardMap.put("wardName", allHospitalMap.get("WARD_NAME"));
					wardMap.put("dayAmt", new BigDecimal(ObjectUtils.toString(allHospitalMap.get("DAY_AMT"))));
					tempWList.add(wardMap);
				} 
			}
			
		}
		return hospitalList;
	}
	
	/**
	 * 取得醫院相關資訊 SQL
	 * @return
	 */
	private String getHospitalListSql() {
		StringBuffer sql = new StringBuffer();
		sql.append(" select H_KEYNO, PARA_NO, DAY_AMT, HOSPITAL_TYPE, TP1.PARAM_NAME HOSPITAL_NAME, WARD_TYPE, TP2.PARAM_NAME WARD_NAME"); 
		sql.append(" from TBINS_PARA_HOSPITAL TH ");
		sql.append(" left join TBSYSPARAMETER TP1 on TH.HOSPITAL_TYPE = TP1.PARAM_CODE ");
		sql.append(" left join TBSYSPARAMETER TP2 on TH.WARD_TYPE = TP2.PARAM_CODE ");
		sql.append(" where 1=1  ");
		sql.append(" and PARA_NO = :paraNo ");
		sql.append(" and TP1.PARAM_TYPE ='INS.HOSPITAL_TYPE' "); 
		sql.append(" and TP2.PARAM_TYPE ='INS.WARD_TYPE' ");
		sql.append(" order by HOSPITAL_TYPE, WARD_TYPE asc ");
		return sql.toString();
	}

	/**
	 * 重大疾病項目清單
	 * @param fromData
	 * @return
	 * @throws JBranchException 
	 * @throws DAOException 
	 */
	private List<Map<String, Object>> getDiseaseList(String paraNo) throws DAOException, JBranchException {
		List<Map<String, Object>> diseaseList = getDiseaseList(new BigDecimal(paraNo));
		List<Map<String, Object>> newDiseaseList = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> diseaseMap :diseaseList) {
			Map<String, Object> newDiseaseMap = new HashMap<String, Object>();
			newDiseaseMap.put("diseaseId", ObjectUtils.toString(diseaseMap.get("DATA")));
			newDiseaseMap.put("diseaseName", diseaseMap.get("LABEL"));
			List<Integer> diseaseTypeList = new ArrayList<Integer>();
			if("Y".equals(ObjectUtils.toString(diseaseMap.get("TYPE_CANCER")))) diseaseTypeList.add(1); // 是癌症阿
			if("Y".equals(ObjectUtils.toString(diseaseMap.get("TYPE_MAJOR")))) 	diseaseTypeList.add(2); // 是重大疾病阿
			if("Y".equals(ObjectUtils.toString(diseaseMap.get("TYPE_LT")))) 	diseaseTypeList.add(3); // 是長期看護阿
			newDiseaseMap.put("diseaseType", diseaseTypeList);
			newDiseaseMap.put("wording", diseaseMap.get("DIS_DESC"));
			newDiseaseList.add(newDiseaseMap);
		}
		return newDiseaseList;
	}

	/**
	 * 居家看護項目清單
	 * @param fromData
	 * @return
	 * @throws JBranchException 
	 * @throws DAOException 
	 */
	private List<Map<String, Object>> getHomeCareList(String paraNo) throws DAOException, JBranchException {
		// {[{LABEL=護理之家, DATA=358220, MONTH_AMT=5000, CARE_WAY=3, CARE_STYLE=7}], LABEL=機構式, DATA=3}
		List<Map<String, Object>> homeCareList = getLtcareList(paraNo);
		List<Map<String, Object>> newhomeCareList = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> homeCareMap :homeCareList) {
			Map<String, Object> newhomeCareMap = new HashMap<String, Object>();
			newhomeCareMap.put("careLocationId", homeCareMap.get("DATA"));
			newhomeCareMap.put("careLocationName", homeCareMap.get("LABEL"));
			
			List<Map<String, Object>> careTypeList = (List<Map<String, Object>>)homeCareMap.get("ltcareList");
			List<Map<String, Object>> newCareTypeList = new ArrayList<Map<String, Object>>();
			
			for(Map<String, Object> careType : careTypeList) {
				Map<String, Object> newCareTypeMap = new HashMap<String, Object>();
				newCareTypeMap.put("careStyle", careType.get("CARE_STYLE"));
				newCareTypeMap.put("careStyleName", careType.get("LABEL"));
				newCareTypeMap.put("monthAmt", careType.get("MONTH_AMT"));
				newCareTypeList.add(newCareTypeMap);
			}
			
			newhomeCareMap.put("careType", newCareTypeList);
			newhomeCareList.add(newhomeCareMap);
		}
		return newhomeCareList;
	}

	/**
	 * 取得商品推薦單一品項
	 * @return
	 * @throws JBranchException 
	 * @throws DAOException 
	 */
	private List<Map<String, Object>> getRecommandList(List<Map<String, Object>> suggestList, int type) throws DAOException, JBranchException{
		List<Map<String, Object>> newSuggestList = new ArrayList<Map<String, Object>>();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> unitMap = xmlInfo.doGetVariable("INS.UNIT", FormatHelper.FORMAT_3);
		Map<String, String> suggestTypeMap = xmlInfo.doGetVariable("INS.PARA_NO4_SUGGEST_TYPE", FormatHelper.FORMAT_3);
		
		for(Map<String, Object> suggestMap: suggestList) {
			Map<String, Object> newSuggestMap = new HashMap<String, Object>();
			List<String> paymentList = new ArrayList<String>();

			newSuggestMap.put("type", type);
			newSuggestMap.put("prdId", suggestMap.get("PRD_ID"));
			newSuggestMap.put("insprdName", suggestMap.get("INSPRD_NAME"));
			newSuggestMap.put("unit", unitMap.get(suggestMap.get("PRD_UNIT")));
			newSuggestMap.put("currCd", suggestMap.get("CURR_CD"));
			
			Object prodInfoObj = suggestMap.get("insprd_annualList");
			List<Map<String, Object>> prodInfoList = prodInfoObj != null ? (List<Map<String, Object>>)prodInfoObj : new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> newProdInfoList = new ArrayList<Map<String, Object>>();
			for(Map<String, Object> prodInfoMap :prodInfoList) {
				Map<String, Object> newProdInfoMap = new HashMap<String, Object>();
				newProdInfoMap.put("paymentyearSel", prodInfoMap.get("DATA"));
				newProdInfoMap.put("prdKeyNo", prodInfoMap.get("KEY_NO"));
				newProdInfoList.add(newProdInfoMap);
				paymentList.add(ObjectUtils.toString(prodInfoMap.get("DATA")));
			}
			newSuggestMap.put("paymentyearSel", paymentList);
			newSuggestMap.put("productInfo", newProdInfoList);
			
			if(type == 4) {
				String suggestType = suggestTypeMap.get(ObjectUtils.toString(suggestMap.get("SUGGEST_TYPE")));
//				1 癌症
//				2 重大疾病
//				3 長期看護
				Integer key = suggestType != null ? ("癌症".equals(suggestType) ? 1 : ("重大疾病".equals(suggestType) ? 2 : ("長期看護".equals(suggestType) ? 3 : 0))) : null;
				newSuggestMap.put("suggestType", key);
			}
			
			newSuggestList.add(newSuggestMap);
		}
		
		return newSuggestList;
	}
	
	/**
	 * 共用取得各 頁籤檔案
	 * 
	 * @param planId
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public Blob getPolicySuggestFile(BigDecimal planId) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	    StringBuffer sb = new StringBuffer();  
			
		//檢核參數設定
		sb.append("SELECT REPORT_FILE FROM TBINS_REPORT ");
		sb.append("WHERE PLAN_ID = :planId ");
		qc.setObject("planId", planId.toString());

	    qc.setQueryString(sb.toString());
	    List<Map<String, Object>> list = dam.exeQuery(qc);
	    
	    
	    if(CollectionUtils.isNotEmpty(list)) {
	    	return (Blob)list.get(0).get("REPORT_FILE");
	    }
	    
	    return null;
	}
}