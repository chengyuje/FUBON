package com.systex.jbranch.app.server.fps.mgm110;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBMGM_ACTIVITY_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBMGM_MGMVO;
import com.systex.jbranch.app.common.fps.table.TBMGM_SIGN_FORMVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.PdfUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Carley
 * @date 2018/02/22
 * 
 */
@Component("mgm110")
@Scope("request")
public class MGM110 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	
	private static final String SIGN_STATUS_SAVE  	= "1"; 			//1. 僅鍵機未上傳
	private static final String SIGN_STATUS_SEND  	= "2"; 			//2. 已上傳待主管覆核
	private static final String SIGN_STATUS_AUTH  	= "3"; 			//3. 主管已覆核
	private static final String SIGN_STATUS_REJECT  = "4"; 			//4. 已退回待重新上傳
	
	//取得活動代碼
	public void getActSeq (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ACT_SEQ, ACT_NAME FROM TBMGM_ACTIVITY_MAIN ");
		sb.append("WHERE TO_CHAR(EFF_DATE, 'YYYY/MM/DD') <= TO_CHAR(SYSDATE, 'YYYY/MM/DD') ");
		sb.append("AND (DELETE_YN <> 'Y' OR DELETE_YN IS NULL) ");
		sb.append("AND (TEMP_YN <> 'Y' OR TEMP_YN IS NULL) ");
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	//查詢活動類型 (M：MGM、V：VIP)
	public void checkActType (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ACT_TYPE FROM TBMGM_ACTIVITY_MAIN WHERE ACT_SEQ = :act_seq ");
		
		queryCondition.setObject("act_seq", inputVO.getAct_seq());
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	//查詢活動期間
	public void getActPeriod (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT EFF_DATE, DEADLINE, ADD_MONTHS(SYSDATE, -3) AS MGM_MIN_DATE ");
		sb.append("FROM TBMGM_ACTIVITY_MAIN WHERE ACT_SEQ = :act_seq ");
		
		queryCondition.setObject("act_seq", inputVO.getAct_seq());
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	//檢核推薦人資格
	public void checkMGMCust (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT EMP.*, ORG.EMP_NAME FROM ( SELECT CUST.*, AO.EMP_ID FROM ( ");
		sb.append("SELECT CUST_NAME, BRA_NBR, AO_CODE, VIP_DEGREE FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id ) CUST ");
		sb.append("LEFT JOIN TBORG_SALES_AOCODE AO ON CUST.AO_CODE = AO.AO_CODE ) EMP ");
		sb.append("LEFT JOIN TBORG_MEMBER ORG ON EMP.EMP_ID = ORG.EMP_ID ");
		
		queryCondition.setObject("cust_id", inputVO.getMgm_cust_id());
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
//	檢核被推薦人資格
//	被推薦人2條件，符合其一即可被推薦：
//	1.	舊戶：活動年度之前一年度12月31日AUM餘額加總小於等值新台幣10萬元(金額可能變動，設為參數)者。
//	2.	新戶：活動年度全新開戶。
	public void checkBeMGMCust (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		
		String cust_id = inputVO.getBe_mgm_cust_id();
		String act_year = null;
		String vip_degree = null;
		String lday_aum_amt = null;
		String first_open_year = null;
		String cust_name = null;
		String cust_phone = null;
		Map<String,Object> map = new HashMap<String,Object>();
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		
		//查詢活動年度
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT TO_CHAR(EFF_DATE, 'yyyy') AS ACT_YEAR FROM TBMGM_ACTIVITY_MAIN WHERE ACT_SEQ = :act_seq ");		
		queryCondition.setObject("act_seq", inputVO.getAct_seq());
		queryCondition.setQueryString(sb.toString());
		List<Map<String,Object>> list = dam.exeQuery(queryCondition);
		if(list.size() > 0){
			act_year = list.get(0).get("ACT_YEAR").toString();
		}
		map.put("ACT_YEAR", act_year);
		
		if(StringUtils.isNotBlank(cust_id)){
			//查詢理財會員等級
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT VIP_DEGREE FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", cust_id);
			queryCondition.setQueryString(sb.toString());
			list = dam.exeQuery(queryCondition);
			if(list.size() > 0){
				if(list.get(0).get("VIP_DEGREE") != null){
					vip_degree = list.get(0).get("VIP_DEGREE").toString();					
				}
			}
			map.put("VIP_DEGREE", vip_degree);
			
			//查詢活動年度之前一年度12月31日AUM餘額加總
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT LDAY_AUM_AMT FROM TBCRM_CUST_AUM_MONTHLY_HIST WHERE CUST_ID = :cust_id ");
			sb.append("AND DATA_YEAR = '2024' AND DATA_MONTH = '12' ");	
			queryCondition.setObject("cust_id", cust_id);
			
			if(StringUtils.isNotBlank(act_year)){
				int act_year_int = Integer.parseInt(act_year);
				int data_year_int = act_year_int - 1;
				act_year = String.valueOf(data_year_int);
			}
			
//			queryCondition.setObject("data_year", act_year);
			queryCondition.setQueryString(sb.toString());
			list = dam.exeQuery(queryCondition);
			if(list.size() > 0 && list.get(0).get("LDAY_AUM_AMT") != null){
				lday_aum_amt = list.get(0).get("LDAY_AUM_AMT").toString();					
			}
			map.put("LDAY_AUM_AMT", lday_aum_amt);
			
			//查詢開戶日期
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT TO_CHAR(MIN(OPEN_DATE), 'yyyy') AS FIRST_OPEN_YEAR FROM TBCRM_ACCT_MAST ");
			sb.append("WHERE CUST_ID = :cust_id AND (ACCT_STATUS <> '2' OR ACCT_STATUS IS NULL) ");	
			queryCondition.setObject("cust_id", cust_id);
			queryCondition.setQueryString(sb.toString());
			list = dam.exeQuery(queryCondition);
			if(list.size() > 0 && list.get(0).get("FIRST_OPEN_YEAR") != null){
				first_open_year = list.get(0).get("FIRST_OPEN_YEAR").toString();					
			}
			map.put("FIRST_OPEN_YEAR", first_open_year);
			
			//查詢被推薦人姓名&連絡電話
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT MAST.CUST_NAME, CONT.TEL_NO, CONT.EXT_NO, CONT.MOBILE_NO FROM TBCRM_CUST_MAST MAST ");
			sb.append("LEFT JOIN TBCRM_CUST_CONTACT CONT ON MAST.CUST_ID = CONT.CUST_ID WHERE MAST.CUST_ID = :cust_id ");	
			queryCondition.setObject("cust_id", cust_id);
			queryCondition.setQueryString(sb.toString());
			list = dam.exeQuery(queryCondition);
			if(list.size() > 0){
				if(list.get(0).get("CUST_NAME") != null)
					cust_name = list.get(0).get("CUST_NAME").toString();
				
				if(list.get(0).get("TEL_NO") != null){
					if(list.get(0).get("EXT_NO") != null){
						cust_phone = list.get(0).get("TEL_NO").toString() + "#" + list.get(0).get("EXT_NO").toString();											
					}else{
						cust_phone = list.get(0).get("TEL_NO").toString();
					}
				}else{
					if(list.get(0).get("MOBILE_NO") != null)
						cust_phone = list.get(0).get("MOBILE_NO").toString();
				}
			}
			map.put("CUST_NAME", cust_name);
			map.put("CUST_PHONE", cust_phone);
			
			resultList.add(map);
		}
		
		outputVO.setResultList(resultList);
		this.sendRtnObject(outputVO);
	}
	
	//檢核該被推薦人於活動期間是否已被推薦
	public void checkMGMHis (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT BE_MGM_CUST_ID FROM TBMGM_MGM WHERE ACT_SEQ = :act_seq AND BE_MGM_CUST_ID = :cust_id ");
		
		queryCondition.setObject("act_seq", inputVO.getAct_seq());
		queryCondition.setObject("cust_id", inputVO.getBe_mgm_cust_id());
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	//檢核該推薦人於推薦當時需年滿20歲，方可符合活動參與資格。
	public void checkAge (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT :mgm_start_date - add_months(BIRTH_DATE, 240) AS AGE ");
		sb.append("From TBCRM_CUST_MAST ");
		sb.append("where CUST_ID = :cust_id ");
		
		queryCondition.setObject("mgm_start_date", inputVO.getMgm_start_date());
		queryCondition.setObject("cust_id", inputVO.getMgm_cust_id());
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	//確認及儲存
	public void confirmSave (Object body, IPrimitiveMap header) throws Exception {
		this.sendRtnObject(this.confirmSave(body));
	}
	
	public MGM110OutputVO confirmSave (Object body) throws Exception {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT TRUNC(ADD_MONTHS(SYSDATE , -3)) AS MGM_APPLY_MIN_DATE FROM DUAL ");
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> dateList = dam.exeQuery(queryCondition);
		Date mgm_apply_min_date = (Date) dateList.get(0).get("MGM_APPLY_MIN_DATE");
		Date mgm_start_date = inputVO.getMgm_start_date();
		System.out.println(mgm_apply_min_date);
		System.out.println(mgm_start_date);
		if(mgm_start_date.before(mgm_apply_min_date)){
			throw new APException("僅能補鍵三個月內推薦之案件。");
		}
		
		TBMGM_MGMVO vo = new TBMGM_MGMVO();
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sb = new StringBuffer();
		sb.append("SELECT LAST_DAY(ADD_MONTHS( :mgm_start_date , 3)) AS MGM_END_DATE FROM DUAL ");
		
		queryCondition.setObject("mgm_start_date", inputVO.getMgm_start_date());		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		Date mgm_end_date = (Date) list.get(0).get("MGM_END_DATE");
		String seq = null;
		Map<String,Object> map = new HashMap<String,Object>();
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		
		if(StringUtils.isBlank(inputVO.getSeq())) {
			//新增
			seq = this.getMGM_SEQ();
			vo.setSEQ(seq);																	//案件序號
			vo.setACT_SEQ(inputVO.getAct_seq());											//活動代碼
			vo.setMGM_CUST_ID(inputVO.getMgm_cust_id());									//推薦人ID
			vo.setMGM_SIGN_STATUS(SIGN_STATUS_SAVE);										//推薦簽署表狀態
			vo.setBE_MGM_CUST_ID(inputVO.getBe_mgm_cust_id());								//被推薦人ID
			vo.setBE_MGM_CUST_NAME(inputVO.getBe_mgm_cust_name());							//被推薦人姓名
			vo.setBE_MGM_CUST_PHONE(inputVO.getBe_mgm_cust_phone());						//被推薦人連絡電話
			vo.setBE_MGM_SIGN_STATUS(SIGN_STATUS_SAVE);										//被推薦簽署表狀態
			vo.setMGM_START_DATE(new Timestamp(inputVO.getMgm_start_date().getTime()));		//推薦日
			vo.setMGM_END_DATE(new Timestamp(mgm_end_date.getTime()));						//追蹤迄日：推薦日次三個月月底
			vo.setPOINTS_TYPE("1");															//1：贈品點數	2：達人加碼
			vo.setMEMO(inputVO.getMemo());													//備註
			dam.create(vo);
			
		}else{
			//修改
			seq = inputVO.getSeq();
			vo = (TBMGM_MGMVO) dam.findByPKey(TBMGM_MGMVO.TABLE_UID, seq);
			vo.setMGM_START_DATE(new Timestamp(inputVO.getMgm_start_date().getTime()));		//推薦日
			vo.setMGM_END_DATE(new Timestamp(mgm_end_date.getTime()));						//追蹤迄日：推薦日次三個月月底
			vo.setBE_MGM_CUST_ID(inputVO.getBe_mgm_cust_id());								//被推薦人ID
			vo.setBE_MGM_CUST_NAME(inputVO.getBe_mgm_cust_name());							//被推薦人姓名
			vo.setBE_MGM_CUST_PHONE(inputVO.getBe_mgm_cust_phone());						//被推薦人連絡電話
			vo.setMEMO(inputVO.getMemo());													//備註
			dam.update(vo);
		}
//		this.saveSignForm(seq, inputVO);
		
		map.put("SEQ", seq);
		resultList.add(map);
		outputVO.setResultList(resultList);
		return outputVO;
	}
	
	//推薦人簽署表單or被推薦人簽署表單上傳
	public void saveSignForm(String seq, MGM110InputVO inputVO) throws Exception {
		if(StringUtils.isNotBlank(seq)){
			dam = this.getDataAccessManager();
			if (!StringUtils.isBlank(inputVO.getMgm_sign_form_name()) || !StringUtils.isBlank(inputVO.getBe_mgm_sign_form_name())) {
				String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				
				TBMGM_SIGN_FORMVO signFormVO = (TBMGM_SIGN_FORMVO) dam.findByPKey(TBMGM_SIGN_FORMVO.TABLE_UID, seq);
				if(null != signFormVO){
					//修改
					if(!StringUtils.isBlank(inputVO.getMgm_sign_form_name())){
						String joinedPath = new File(tempPath, inputVO.getMgm_sign_form_name()).toString();
						Path path = Paths.get(joinedPath);
						byte[] data = Files.readAllBytes(path);
						
						signFormVO.setMGM_SIGN_FORM_NAME(inputVO.getReal_mgm_sign_form_name());
						signFormVO.setMGM_SIGN_FORM(ObjectUtil.byteArrToBlob(data));					
					}
					
					if(!StringUtils.isBlank(inputVO.getBe_mgm_sign_form_name())){
						String joinedPath = new File(tempPath, inputVO.getBe_mgm_sign_form_name()).toString();
						Path path = Paths.get(joinedPath);
						byte[] data = Files.readAllBytes(path);
						
						signFormVO.setBE_MGM_SIGN_FORM_NAME(inputVO.getReal_be_mgm_sign_form_name());
						signFormVO.setBE_MGM_SIGN_FORM(ObjectUtil.byteArrToBlob(data));					
					}
					dam.update(signFormVO);
					
				} else {
					//新增
					signFormVO = new TBMGM_SIGN_FORMVO();
					signFormVO.setSEQ(seq);
					
					if(!StringUtils.isBlank(inputVO.getMgm_sign_form_name())){
						String joinedPath = new File(tempPath, inputVO.getMgm_sign_form_name()).toString();
						Path path = Paths.get(joinedPath);
						byte[] data = Files.readAllBytes(path);
						
						signFormVO.setMGM_SIGN_FORM_NAME(inputVO.getReal_mgm_sign_form_name());
						signFormVO.setMGM_SIGN_FORM(ObjectUtil.byteArrToBlob(data));					
					}
					
					if(!StringUtils.isBlank(inputVO.getBe_mgm_sign_form_name())){
						String joinedPath = new File(tempPath, inputVO.getBe_mgm_sign_form_name()).toString();
						Path path = Paths.get(joinedPath);
						byte[] data = Files.readAllBytes(path);
						
						signFormVO.setBE_MGM_SIGN_FORM_NAME(inputVO.getReal_be_mgm_sign_form_name());
						signFormVO.setBE_MGM_SIGN_FORM(ObjectUtil.byteArrToBlob(data));					
					}
					dam.create(signFormVO);
				}
			}
		}
	}
	
	//送出及上傳
	public void send (Object body, IPrimitiveMap header) throws Exception {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		String seq = inputVO.getSeq();
		
		if(StringUtils.isNotBlank(seq)){
			this.saveSignForm(seq, inputVO);
			
			//更改狀態為送出
			if (!StringUtils.isBlank(inputVO.getMgm_sign_form_name()) || !StringUtils.isBlank(inputVO.getBe_mgm_sign_form_name())) {			
				dam = this.getDataAccessManager();
				TBMGM_MGMVO vo = (TBMGM_MGMVO) dam.findByPKey(TBMGM_MGMVO.TABLE_UID, seq);
				
				if (!StringUtils.isBlank(inputVO.getMgm_sign_form_name())){		//推薦人簽署
					vo.setMGM_SIGN_STATUS(SIGN_STATUS_SEND);					//2. 已列印送出待主管覆核
				}
				if (!StringUtils.isBlank(inputVO.getBe_mgm_sign_form_name())){	//被推薦人簽署
					vo.setBE_MGM_SIGN_STATUS(SIGN_STATUS_SEND);					//2. 已列印送出待主管覆核
				}
				dam.update(vo);
			}
		} else {
			throw new APException("系統發生錯誤請洽系統管理員。");
		}
		this.sendRtnObject(null);
	}
	
	//查詢已上傳的推薦人簽署表單&被推薦人簽署表單
	public void getSignForm (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		
		String seq = inputVO.getSeq();
		if(StringUtils.isNotBlank(seq)){
			TBMGM_SIGN_FORMVO signFormVO = (TBMGM_SIGN_FORMVO) dam.findByPKey(TBMGM_SIGN_FORMVO.TABLE_UID, seq);
			if(null != signFormVO){
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("MGM_SIGN_FORM_NAME", signFormVO.getMGM_SIGN_FORM_NAME());
				map.put("MGM_SIGN_FORM", signFormVO.getMGM_SIGN_FORM());
				map.put("BE_MGM_SIGN_FORM_NAME", signFormVO.getBE_MGM_SIGN_FORM_NAME());
				map.put("BE_MGM_SIGN_FORM", signFormVO.getBE_MGM_SIGN_FORM());
				resultList.add(map);
			}
		}
		outputVO.setResultList(resultList);
		this.sendRtnObject(outputVO);
	}
	
	//檢視已上傳的簽署表
	public void signFormView (Object body, IPrimitiveMap header) throws Exception {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		
		String seq = inputVO.getSeq();
		String formType = inputVO.getFormType();
		if(StringUtils.isNotBlank(seq) && StringUtils.isNotBlank(formType)){
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM TBMGM_SIGN_FORM WHERE SEQ = :seq ");
			queryCondition.setObject("seq", seq);
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			
			if(list.size() > 0){
				String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				String uuid = UUID.randomUUID().toString();
				
				if("R".equals(formType)){
					Blob blob = (Blob) list.get(0).get("MGM_SIGN_FORM");
					String mgm_sign_form_name = list.get(0).get("MGM_SIGN_FORM_NAME").toString();
					int index = mgm_sign_form_name.lastIndexOf(".");
					String data_name = mgm_sign_form_name.substring(index); 
					
					int blobLength = (int) blob.length();  
					byte[] blobAsBytes = blob.getBytes(1, blobLength);
					
					File targetFile = new File(filePath, uuid + data_name);
					FileOutputStream fos = new FileOutputStream(targetFile);
					fos.write(blobAsBytes);
					fos.close();
					
//			    	this.notifyClientToDownloadFile("temp//"+uuid, fileName);
					outputVO.setPdfUrl("temp/" + uuid + data_name);
					
				} else if("B".equals(formType)){
					Blob blob = (Blob) list.get(0).get("BE_MGM_SIGN_FORM");
					String be_mgm_sign_form_name = list.get(0).get("BE_MGM_SIGN_FORM_NAME").toString();
					int index = be_mgm_sign_form_name.lastIndexOf(".");
					String data_name = be_mgm_sign_form_name.substring(index); 
					
					int blobLength = (int) blob.length();  
					byte[] blobAsBytes = blob.getBytes(1, blobLength);
					
					File targetFile = new File(filePath, uuid + data_name);
					FileOutputStream fos = new FileOutputStream(targetFile);
					fos.write(blobAsBytes);
					fos.close();
					
//			    	this.notifyClientToDownloadFile("temp//"+uuid, fileName);
					outputVO.setPdfUrl("temp/" + uuid + data_name);
				}
			} else {
				throw new APException("系統發生錯誤請洽系統管理員。");
			}
		}
	    this.sendRtnObject(outputVO);
	}
	
	//檢視空白簽署表單
	public void printForm (Object body, IPrimitiveMap header) throws Exception {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		
		String act_seq = inputVO.getAct_seq();
		String formType = inputVO.getFormType();
		if(StringUtils.isNotBlank(act_seq) && StringUtils.isNotBlank(formType)){
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM TBMGM_ACTIVITY_SIGN_FORM WHERE ACT_SEQ = :act_seq ");
			queryCondition.setObject("act_seq", act_seq);
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			
			String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			String uuid = UUID.randomUUID().toString();

			if("R".equals(formType)){
				String fileName = list.get(0).get("MGM_FORM_NAME").toString();
				Blob blob = (Blob) list.get(0).get("MGM_FORM");
				int blobLength = (int) blob.length();  
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
				
				File targetFile = new File(filePath, uuid);
				FileOutputStream fos = new FileOutputStream(targetFile);
				fos.write(blobAsBytes);
				fos.close();
				
		    	this.notifyClientToDownloadFile("temp//"+uuid, fileName);
//				outputVO.setPdfUrl("temp//"+uuid);
				
			} else if("B".equals(formType)){
				String fileName = list.get(0).get("BE_MGM_FORM_NAME").toString();
				Blob blob = (Blob) list.get(0).get("BE_MGM_FORM");
				int blobLength = (int) blob.length();  
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
				
				File targetFile = new File(filePath, uuid);
				FileOutputStream fos = new FileOutputStream(targetFile);
				fos.write(blobAsBytes);
				fos.close();
				
		    	this.notifyClientToDownloadFile("temp//"+uuid, fileName);
//				outputVO.setPdfUrl("temp//"+uuid);
			}
		}
	    this.sendRtnObject(outputVO);
	}
	
	//取得MGM活動鍵機檔的案件序號(SEQ)
	private String getMGM_SEQ () throws JBranchException {		
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String date = df.format(new Date());
        
		try{
			seqNum = date + "MGM" + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBMGM_MGM")));
		}catch(Exception e){
			sn.createNewSerial("TBMGM_MGM", "0000", 1, "d", null, 1, new Long("9999"), "y", new Long("0"), null);
			seqNum = date + "MGM" + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBMGM_MGM")));
		}
		return seqNum;
	}
	
	//送出及列印
	public void sendPrint (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
//		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		
		//更改狀態為送出
		String seq = inputVO.getSeq();
		String mgm_sign = inputVO.getMgm_sign();
		String be_mgm_sign = inputVO.getBe_mgm_sign();
		
		TBMGM_MGMVO vo = new TBMGM_MGMVO();
		vo = (TBMGM_MGMVO) dam.findByPKey(TBMGM_MGMVO.TABLE_UID, seq);
		
		if (vo != null) {
			if("Y".equals(mgm_sign)){							//推薦人簽署
				vo.setMGM_SIGN_STATUS(SIGN_STATUS_SEND);		//2. 已列印送出待主管覆核
			}
			
			if("Y".equals(be_mgm_sign)){						//被推薦人簽署
				vo.setBE_MGM_SIGN_STATUS(SIGN_STATUS_SEND);		//2. 已列印送出待主管覆核
			}
			
			dam.update(vo);
		}
		
		//列印
		if("Y".equals(mgm_sign) || "Y".equals(be_mgm_sign)){
			String mgm_cust_brh = "";
			
			//查詢分行名稱
			if(StringUtils.isNotBlank(inputVO.getBranch_nbr())){
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				
				StringBuffer sb = new StringBuffer();
				sb.append("SELECT DEPT_NAME FROM TBORG_DEFN WHERE DEPT_ID = :dept_id ");
				
				queryCondition.setObject("dept_id", inputVO.getBranch_nbr());		
				queryCondition.setQueryString(sb.toString());
				
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if(list.size() > 0){
					mgm_cust_brh = list.get(0).get("DEPT_NAME").toString();
				}
			}
			if("Y".equals(mgm_sign) && "Y".equals(be_mgm_sign)){	//推薦人簽署 + 被推薦人簽署
				this.printMGM(inputVO, mgm_cust_brh, "both");
			}else{
				if("Y".equals(mgm_sign)){								//推薦人簽署
					this.printMGM(inputVO, mgm_cust_brh, "R1");
				}
				if("Y".equals(be_mgm_sign)){							//被推薦人簽署
					this.printMGM(inputVO, mgm_cust_brh, "R2");
				}				
			}
			
		}
		this.sendRtnObject(null);
	}

	private void printMGM(MGM110InputVO inputVO, String mgm_cust_brh, String reportID) throws JBranchException {
		ReportFactory factory = new ReportFactory();
		ReportGeneratorIF gen = factory.getGenerator();
		String txnCode = "MGM110";
		ReportDataIF data = new ReportData();
		
		data.addParameter("MGM_CUST_NAME", inputVO.getMgm_cust_name());
		data.addParameter("MGM_CUST_ID", inputVO.getMgm_cust_id());
		data.addParameter("MGM_CUST_BRH", mgm_cust_brh);
		data.addParameter("EMP_NAME", inputVO.getMgm_cust_name());
		data.addParameter("BE_MGM_CUST_NAME", inputVO.getBe_mgm_cust_name());
		data.addParameter("BE_MGM_CUST_ID", inputVO.getBe_mgm_cust_id());
		data.addParameter("BE_MGM_CUST_PHONE", inputVO.getBe_mgm_cust_phone());
		
		if("R1".equals(reportID) || "R2".equals(reportID)){
			ReportIF report = gen.generateReport(txnCode, reportID, data);
			String url = report.getLocation();
			if("R1".equals(reportID)){
				notifyClientToDownloadFile(url, "推薦人簽署專用表單.pdf");			
			} else if ("R2".equals(reportID)){
				notifyClientToDownloadFile(url, "被推薦人簽署專用表單.pdf");
			}			
		} else if ("both".equals(reportID)){
			ReportIF report = null;
			String url = null;
			List<String> url_list = new ArrayList<String>();
			
			report = gen.generateReport(txnCode, "R1", data);
			url = report.getLocation();
			url_list.add(url);
			
			report = gen.generateReport(txnCode, "R2", data);
			url = report.getLocation();
			url_list.add(url);
			
			String reportURL = PdfUtil.mergePDF(url_list, true);
			
			notifyClientToDownloadFile(reportURL, "推薦人及被推薦人簽署專用表單.pdf");
		}
	}
	
	private void printBeMGM() throws JBranchException {
		ReportFactory factory = new ReportFactory();
		ReportGeneratorIF gen = factory.getGenerator();
		String txnCode = "MGM110";
	}
	
	//取得MGM活動最後鍵機日
	//(由於鍵機推薦日應只能選"三個月"內日期，以活動期間推薦日設定(2018/1/1~2018/12/31)為例，2019/4/1(含)起應不得新增案件鍵機)
	public void checkMmgApplyEndDate (Object body, IPrimitiveMap header) throws Exception {
		MGM110InputVO inputVO = (MGM110InputVO) body;
//		MGM110OutputVO outputVO = new MGM110OutputVO();
//		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		
		String act_seq = inputVO.getAct_seq();
		if(StringUtils.isNotBlank(act_seq)){
			dam = this.getDataAccessManager();
			TBMGM_ACTIVITY_MAINVO actVO = (TBMGM_ACTIVITY_MAINVO) dam.findByPKey(TBMGM_ACTIVITY_MAINVO.TABLE_UID, act_seq);
			
			if(null != actVO){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				
				StringBuffer sb = new StringBuffer();
				sb.append("SELECT ADD_MONTHS( :deadline , 3) AS MGM_APPLY_END_DATE FROM DUAL ");
				
				queryCondition.setObject("deadline", actVO.getDEADLINE());		
				queryCondition.setQueryString(sb.toString());
				List<Map<String,Object>> resultList = dam.exeQuery(queryCondition);
				
				if(resultList.size() > 0){
					Date mgm_apply_end_date =  (Date) resultList.get(0).get("MGM_APPLY_END_DATE");
					String mgm_apply_end_date_s = sdf.format(mgm_apply_end_date);
					String now_date_s = sdf.format(new Date());
					
					Date mgmApplyEndDate = sdf.parse(mgm_apply_end_date_s);
					Date nowDate = sdf.parse(now_date_s);
					
					if(nowDate.after(mgmApplyEndDate)){
						throw new APException("此活動(" + act_seq + ")最後鍵機日為：" + mgm_apply_end_date_s);
					}
				}
			} else {
				throw new APException("系統發生錯誤請洽系統管理員。");
			}
		} else{
			throw new APException("ehl_01_common_022");		//欄位檢核錯誤：*為必要輸入欄位,請輸入後重試
		}
		
//		outputVO.setResultList(resultList);
		this.sendRtnObject(null);
		
	}
	
}