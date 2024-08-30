package com.systex.jbranch.app.server.fps.ins260;

import com.opensymphony.util.TextUtils;
import com.systex.jbranch.app.common.fps.table.*;
import com.systex.jbranch.app.server.fps.ins810.INS810;
import com.systex.jbranch.app.server.fps.ins810.INS810InputVO;
import com.systex.jbranch.app.server.fps.ins810.INS810OutputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.PdfUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.sql.rowset.serial.SerialException;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component("ins260")
@Scope("request")
public class INS260 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(INS260.class);
	
	//======================== WEB 調用 ========================	
	/**
	 * 取得保險規劃狀態
	 * @param body
	 * @param header
	 * @throws JBranchException,Exception
	 */
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		sendRtnObject(inquireMain(body,header));
	}
	
	/**
	 * 列印報表
	 * @param body
	 * @param header
	 * @throws JBranchException,Exception
	 */
	public void printReport(Object body, IPrimitiveMap header) throws Exception {
		INS260InputVO inputVO = (INS260InputVO) body;
		List<String> url_list =  new ArrayList<String>();
		//取得產生報表的資料
		ReportDataIF data = getReportDataIF(inputVO);
		String fileName = new SimpleDateFormat("YYYYMMdd").format(new Date()) + "保險規劃書.pdf";
		//取得報表
		ReportIF report = getReport(data);
		//取得報表的byte[]
		byte[] rep_data = getByteArr(report);
		//儲存報表
		save(inputVO.getPlanKeyno(), fileName, rep_data);
		url_list.add("doc//INS//INS200_COVER.pdf");
		url_list.add(report.getLocation());
		
		// 合併所有PDF＆加密碼
		String reportURL = PdfUtil.mergePDF(url_list, inputVO.getCustId());
//		String reportURL = PdfUtil.mergePDF(url_list, false);
		this.notifyClientToDownloadFile(reportURL, fileName);
//		this.notifyClientToDownloadFile(report.getLocation(), fileName);
	}
	
	//======================== WEB 處理========================
	public INS260OutputVO inquireMain(Object body, IPrimitiveMap header) throws JBranchException, Exception{
		INS260OutputVO outputVO = new INS260OutputVO();
		INS260InputVO inputVO = (INS260InputVO) body;
		
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT PLAN_TYPE, STATUS FROM TBINS_PLAN_DTL WHERE PLAN_KEYNO = :plan_keyno");
		queryCondition.setObject("plan_keyno", inputVO.getPlanKeyno());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setResultList(list);
		
		return outputVO;
	}
	
	//======================== WS 調用 ========================
    /**
     * 提供給 APP 使用的 API <br>
     * <code> <b> 2.7.9 </b> 產生保險規劃書</code>
     * @param inputVO : custId, planKeyNo, type, isNew
     * @return Object : 
     * @throws JBranchException : <br>
     *                <p>
     *                             <b>客製訊息如下 :</b>
     *                             <li>查無資料: </li>
     *                </p>
     * @throws IOException 
     * @throws SQLException 
     * @throws SerialException 
     */
	public void generateInsurancePlan(Object body, IPrimitiveMap header) throws JBranchException, IOException, SerialException, SQLException {
		dam = this.getDataAccessManager();
		
		INS260InputVO inputVO = new INS260InputVO();
		//取得報表的byte[]
		byte[] rep_data = null;
		//用來存放產生出來的報告書
		Map outputMap = new HashMap();
		//取得傳進來的資料
		GenericMap inputGmap = new GenericMap((Map) body);
		//取得客戶Id
		String custId = inputGmap.getNotNullStr("custId");
		//取得保險規劃序號
		String planKeyNo = inputGmap.getNotNullStr("planKeyNo");
		//取得規劃種類
		List<Double> type = inputGmap.get("type");
		//是否重新產生規畫書
		boolean isNew = inputGmap.get("isNew");
		//將規劃種類轉為字串
		List<String> chkType = new ArrayList<String>();
		//裝blob的list
		List<Map<String, Object>> blobList = new ArrayList<Map<String,Object>>();
		
		//數值+1以符合web版查詢條件
		for(Double num_double : type){
			int num_int = num_double.intValue() + 1;
			String num_int_toString = String.valueOf(num_int);
			chkType.add(num_int_toString);
		}
		
		//判斷是否來自家庭財務安全試算
		boolean from_ins_132 = false;
		
		//判斷是否來自家庭財務安全試算，如果是，取其中相對應項目的保障金額
		for(String typeNum : chkType){
						
			switch(typeNum){
			case "1" : {								
				List<Map<String,Object>> itemsList = getItemsFromIns132(planKeyNo,typeNum,"1");
				
				from_ins_132 = "2".equals(ObjectUtils.toString(itemsList.get(0).get("PORTAL_FLAG")));
				
				if(from_ins_132){
					inputVO.setINS200_FamilyGap(ObjectUtils.toString(itemsList.get(0).get("SHD_PROTECT1")));
				}
				break;
			}
			case "2" : {
				List<Map<String,Object>> itemsList = getItemsFromIns132(planKeyNo,typeNum,"1");
				
				from_ins_132 = "2".equals(ObjectUtils.toString(itemsList.get(0).get("PORTAL_FLAG")));
				
				if(from_ins_132){
					inputVO.setINS200_Accident(ObjectUtils.toString(itemsList.get(0).get("SHD_PROTECT1")));
				}
				break;
			}
			case "3" : {
				List<Map<String,Object>> itemsList = getItemsFromIns132(planKeyNo,typeNum,"2");
				
				from_ins_132 = "2".equals(ObjectUtils.toString(itemsList.get(0).get("PORTAL_FLAG")));
				
				if(from_ins_132){
					inputVO.setINS200_Health(ObjectUtils.toString(itemsList.get(0).get("SHD_PROTECT2")));
				}
				break;				
			}
			}
			
		}
		
		//將傳入的參數放進inputVO
		inputVO.setCustId(custId);
		inputVO.setPlanKeyno(planKeyNo);
		inputVO.setChkType(chkType);
		inputVO.setIns200_from_ins132(from_ins_132);
		
		//預防取舊規畫書卻為空的狀況
		getBlobList(planKeyNo, blobList);
		if(false == isNew && CollectionUtils.isEmpty(blobList)){
			isNew = true;
		}
		
		if(isNew){
			//取得產生報表的資料
			ReportDataIF data = getReportDataIF(inputVO);
			String fileName = new SimpleDateFormat("YYYYMMdd").format(new Date()) + "保險規劃書.pdf";
			//取得報表
			ReportIF report = getReport(data);			
			rep_data = getByteArr(report);
			//刪除既存報表
			delete(planKeyNo);
			//儲存報表
			save(inputVO.getPlanKeyno(), fileName, rep_data);
		}else{
			getBlobList(planKeyNo, blobList);
			
			rep_data = ObjectUtil.blobToByteArr((Blob) blobList.get(0).get("REPORT_FILE"));		
		}
		
		//fileCode : 保險規劃序號
		outputMap.put("fileCode", planKeyNo);
		//file : PDF檔案的byte[]，使用Base64加密
		outputMap.put("file", DatatypeConverter.printBase64Binary(rep_data));
		
		sendRtnObject(outputMap);
	}
		
	/**
	 * 保險特定規劃2階API-取得保險規劃狀態
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getInsurancePlanStatus(Object body,IPrimitiveMap header) throws JBranchException,Exception {
		Map inputMap = (Map)body;
		dam = this.getDataAccessManager();
		INS260InputVO inputVO = new INS260InputVO();
		inputVO.setCustId(ObjectUtils.toString(inputMap.get("custId")));
		inputVO.setPlanKeyno(ObjectUtils.toString(inputMap.get("planKeyNo")));
		INS260OutputVO outputVO = inquireMain(inputVO,header);
		Map<String, Object> result = new HashMap<String, Object>();
		// 初始話預先放0
		result.put("life", 0);
		result.put("accident", 0);
		result.put("medical", 0);
		result.put("dreadDisease", 0);
		for(Map<String ,Object> map : (List<Map<String,Object>>)outputVO.getResultList()){
			result.put("life", map.get("PLAN_TYPE").toString().equals("1") ? Integer.valueOf(map.get("STATUS").toString()) : result.get("life"));
			result.put("accident", map.get("PLAN_TYPE").toString().equals("2") ? Integer.valueOf(map.get("STATUS").toString()) : result.get("accident"));
			result.put("medical", map.get("PLAN_TYPE").toString().equals("3") ? Integer.valueOf(map.get("STATUS").toString()) : result.get("medical"));
			result.put("dreadDisease", map.get("PLAN_TYPE").toString().equals("4") ? Integer.valueOf(map.get("STATUS").toString()) : result.get("dreadDisease"));
			
		}
		this.sendRtnObject(result);
	}
	
	/**
	 * 刪除報表
	 * @param String : planKeyno
	 * @param byte[] : rep_data
	 * @throws JBranchException
	 * @throws SQLException 
	 * @throws SerialException 
	 */
	public void delete(String planKeyno) throws JBranchException, SerialException, SQLException{
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append(" delete from TBINS_REPORT ");
		sb.append(" where PLAN_ID = :planKeyno ");
		qc.setObject("planKeyno", planKeyno);
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
	}
	
	/**
	 * 取得blobList
	 * @param String : planKeyNo
	 * @param List<Map<String, Object>> : blobList
	 * @throws JBranchException
	 */
	public void getBlobList(String planKeyNo, List<Map<String, Object>> blobList) throws JBranchException{
		QueryConditionIF queryCondition = null;
		StringBuilder sb = new StringBuilder();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append(" SELECT REPORT_FILE FROM TBINS_REPORT ");
		sb.append(" WHERE PLAN_ID = :planKeyNo");
		queryCondition.setObject("planKeyNo", planKeyNo);
		queryCondition.setQueryString(sb.toString());
		blobList = dam.exeQuery(queryCondition);	
	}
	
	/**
	 * 取得來自家庭財務安全試算的各項保障金額跟判斷是否來自家庭安全財務試算的辨別項
	 * @param String : planKeyno
	 * @param String : chkType
	 * @param String : value
	 * @throws JBranchException
	 * @throws DAOException 
	 * @return itemsList
	 */
	public List<Map<String,Object>> getItemsFromIns132(String planKeyNo,String chkType,String value) throws DAOException, JBranchException{
		
		List<Map<String,Object>> itemsList = exeQueryForQcf(genDefaultQueryConditionIF()
				.setQueryString(new StringBuilder()
				.append(" SELECT SHD_PROTECT" + value + ", PORTAL_FLAG FROM TBINS_PLAN_DTL ")
				.append(" WHERE PLAN_KEYNO = :planKeyNo ")
				.append(" AND PLAN_D_KEYNO = ( ")
				.append("     SELECT PLAN_D_KEYNO FROM TBINS_PLAN_DTL ")
				.append("     WHERE PLAN_KEYNO = :planKeyNo ")
				.append("     AND PLAN_TYPE = :chkType ")
				.append(" ) ").toString())
				.setObject("planKeyNo", planKeyNo)
		        .setObject("chkType", chkType));
				
		return itemsList;
		
	}
	
	//======================== 共用 調用 & 處理========================
	/**
	 * 儲存報表
	 * @param String : planKeyno
	 * @param String : fileName
	 * @param byte[] : rep_data
	 * @throws JBranchException
	 * @throws SQLException 
	 * @throws SerialException 
	 */
	public void save(String planKeyno, String fileName, byte[] rep_data) throws JBranchException, SerialException, SQLException{
		dam = this.getDataAccessManager();
		// TBINS_REPORT
		TBINS_REPORTVO rep_vo = new TBINS_REPORTVO();
		rep_vo.setKEYNO(new BigDecimal(getSN("REPORT")));
		rep_vo.setPLAN_ID(planKeyno);
		rep_vo.setFILE_NAME(fileName);
		rep_vo.setREPORT_FILE(ObjectUtil.byteArrToBlob(rep_data));
		dam.create(rep_vo);		
	}
	
	/**
	 * 取得報表的byte[]
	 * @param ReportDataIF : data
	 * @return byte[] : rep_data
	 * @throws JBranchException,IOException
	 */
	public byte[] getByteArr(ReportIF report) throws JBranchException, IOException {
		String serverPath = (String) SysInfo.getInfoValue(SystemVariableConsts.SERVER_PATH);
		byte[] rep_data = Files.readAllBytes(new File(serverPath, report.getLocation()).toPath());
		
		return rep_data;		
	}
	
	/**
	 * 取得報表
	 * @param ReportDataIF : data
	 * @return ReportIF : report
	 * @throws JBranchException
	 */
	public ReportIF getReport(ReportDataIF data) throws JBranchException{
		ReportGeneratorIF gen = new ReportFactory().getGenerator();
		ReportIF report = gen.generateReport("INS260", "R1", data);
		
		return report;		
	}
	
	/**
	 * 取得產生報表的資料
	 * @param INS260InputVO : inputVO
	 * @return ReportDataIF : data
	 * @throws JBranchException
	 */
	public ReportDataIF getReportDataIF(INS260InputVO inputVO) throws JBranchException {
		dam = this.getDataAccessManager();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		NumberFormat nf = NumberFormat.getInstance();
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> com_yn = new HashMap(xmlInfo.doGetVariable("COMMON.YES_NO", FormatHelper.FORMAT_3));
		Map<String, String> risk_type = new HashMap(xmlInfo.doGetVariable("KYC.RISK_TYPE", FormatHelper.FORMAT_3));
		Map<String, String> prof_grade = new HashMap(xmlInfo.doGetVariable("INS.PROF_GRADE", FormatHelper.FORMAT_3));
		Map<String, String> ins_unit = new HashMap(xmlInfo.doGetVariable("INS.UNIT", FormatHelper.FORMAT_3));
		Map<String, String> hospital_type = new HashMap(xmlInfo.doGetVariable("INS.HOSPITAL_TYPE", FormatHelper.FORMAT_3));
		Map<String, String> ward_type = new HashMap(xmlInfo.doGetVariable("INS.WARD_TYPE", FormatHelper.FORMAT_3));
		Map<String, String> care_way = new HashMap(xmlInfo.doGetVariable("INS.CARE_WAY", FormatHelper.FORMAT_3));
		Map<String, String> care_style = new HashMap(xmlInfo.doGetVariable("INS.CARE_STYLE", FormatHelper.FORMAT_3));
		Map<String, String> para_no4_suggest = new HashMap(xmlInfo.doGetVariable("INS.PARA_NO4_SUGGEST_TYPE", FormatHelper.FORMAT_3));
		
		ReportDataIF data = new ReportData();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		// 查看勾選哪些
		data.addParameter("PLAN_TYPE_1", inputVO.getChkType().contains("1") ? true : false);
		data.addParameter("PLAN_TYPE_2", inputVO.getChkType().contains("2") ? true : false);
		data.addParameter("PLAN_TYPE_3", inputVO.getChkType().contains("3") ? true : false);
		data.addParameter("PLAN_TYPE_4", inputVO.getChkType().contains("4") ? true : false);
		// ins132
		data.addParameter("ins200_from_ins132", inputVO.getIns200_from_ins132());
		data.addParameter("INS200_FamilyGap", inputVO.getINS200_FamilyGap() != null ? nf.format(new BigDecimal(inputVO.getINS200_FamilyGap())) : "");
		data.addParameter("INS200_Accident", inputVO.getINS200_Accident() != null ? nf.format(new BigDecimal(inputVO.getINS200_Accident())) : "");
		data.addParameter("INS200_Health", inputVO.getINS200_Health() != null ? nf.format(new BigDecimal(inputVO.getINS200_Health())) : "");
		// 表頭
		TBINS_PLAN_MAINVO tpmVo = (TBINS_PLAN_MAINVO)dam.findByPKey(TBINS_PLAN_MAINVO.TABLE_UID, inputVO.getPlanKeyno());
		TBCRM_CUST_MASTVO tcmVO = (TBCRM_CUST_MASTVO) dam.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, tpmVo.getCUST_ID());
		TBINS_CUST_MASTVO insVO = (TBINS_CUST_MASTVO) dam.findByPKey(TBINS_CUST_MASTVO.TABLE_UID, tpmVo.getCUST_ID());
		if(tcmVO == null && insVO == null)
			throw new APException("客戶主檔和保險客戶(被保險人)主檔皆無此客戶");
		//計算保險年齡
		INS810 ins810 = (INS810) PlatformContext.getBean("ins810");
		INS810InputVO ins810inputVO = new INS810InputVO();
		ins810inputVO.setBirthday(tcmVO != null ? tcmVO.getBIRTH_DATE() : insVO.getBIRTH_DATE());
		INS810OutputVO ins810outputVO = ins810.getAge(ins810inputVO);
		data.addParameter("CUST_AGE", ObjectUtils.toString(ins810outputVO.getAge()));
		//
		if(tcmVO != null) {
			data.addParameter("CUST_NAME", tcmVO.getCUST_NAME());
			data.addParameter("CUST_BIRTHDAY", tcmVO.getBIRTH_DATE() != null ? sdf.format(tcmVO.getBIRTH_DATE()) : "");
			data.addParameter("CUST_RISK", tcmVO.getCUST_RISK_ATR() != null ? risk_type.get(tcmVO.getCUST_RISK_ATR()) : "");
		}
		else {
			data.addParameter("CUST_NAME", insVO.getCUST_NAME());
			data.addParameter("CUST_BIRTHDAY", insVO.getBIRTH_DATE() != null ? sdf.format(insVO.getBIRTH_DATE()) : "");
		}
		// 保障需求分析
		data.addParameter("LIFE_EXPENSE", tpmVo.getLIFE_EXPENSE() != null ? nf.format(tpmVo.getLIFE_EXPENSE()) : "");
		data.addParameter("LIFE_EXPENSE_YEARS", ObjectUtils.toString(tpmVo.getLIFE_EXPENSE_YEARS()));
		data.addParameter("LIFE_EXPENSE_AMT", tpmVo.getLIFE_EXPENSE_AMT() != null ? nf.format(tpmVo.getLIFE_EXPENSE_AMT()) : "");
		data.addParameter("LOAN_AMT", tpmVo.getLOAN_AMT() != null ? nf.format(tpmVo.getLOAN_AMT()) : "");
		data.addParameter("EDU_FFE", tpmVo.getEDU_FFE() != null ? nf.format(tpmVo.getEDU_FFE()) : "");
		data.addParameter("PREPARE_AMT", tpmVo.getPREPARE_AMT() != null ? nf.format(tpmVo.getPREPARE_AMT()) : "");
		
		data.addParameter("NURSE_FEE", tpmVo.getNURSE_FEE() != null ? nf.format(tpmVo.getNURSE_FEE()) : "");
		data.addParameter("INS_PREPARE_YEARS", ObjectUtils.toString(tpmVo.getINS_PREPARE_YEARS()));
		data.addParameter("PRO_LEVEL", prof_grade.get(tpmVo.getPRO_LEVEL()));
		
		data.addParameter("TTL_FLAG", com_yn.get(tpmVo.getTTL_FLAG()));
		if(tpmVo.getSICKROOM_FEE() != null) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("SELECT HOSPITAL_TYPE, WARD_TYPE FROM TBINS_PARA_HOSPITAL WHERE H_KEYNO = :h_keyno ");
			queryCondition.setObject("h_keyno", tpmVo.getSICKROOM_FEE());
			List<Map<String, Object>> hos_list = dam.exeQuery(queryCondition);
			if(hos_list.size() > 0) {
				data.addParameter("HOSPITAL_TYPE", hospital_type.get(ObjectUtils.toString(hos_list.get(0).get("HOSPITAL_TYPE"))));
				data.addParameter("WARD_TYPE", ward_type.get(ObjectUtils.toString(hos_list.get(0).get("WARD_TYPE"))));
			}
		}
		
		if(StringUtils.isNotBlank(tpmVo.getDISEASE())) {
			String[] diseaseArray = StringUtils.split(tpmVo.getDISEASE(), ",");
			Set<String> diseaseName = new HashSet<String>();
			for(String str: diseaseArray) {
				TBINS_PARA_DISEASEVO disvo = (TBINS_PARA_DISEASEVO) dam.findByPKey(TBINS_PARA_DISEASEVO.TABLE_UID, new BigDecimal(str));
				diseaseName.add(disvo.getDIS_NAME());
			}
			data.addParameter("DISEASE", TextUtils.join(",", diseaseName));
		}
		data.addParameter("MAJOR_DISEASES_PAY", tpmVo.getMAJOR_DISEASES_PAY() != null ? nf.format(tpmVo.getMAJOR_DISEASES_PAY()) : "");
		if(tpmVo.getNURSE_FEE_PAY() != null) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("SELECT CARE_WAY, CARE_STYLE FROM TBINS_PARA_LTCARE WHERE LT_KEYNO = :lt_keyno ");
			queryCondition.setObject("lt_keyno", tpmVo.getNURSE_FEE_PAY());
			List<Map<String, Object>> lt_list = dam.exeQuery(queryCondition);
			if(lt_list.size() > 0)
				data.addParameter("NURSE_FEE_PAY", care_way.get(ObjectUtils.toString(lt_list.get(0).get("CARE_WAY"))) + ":" + care_style.get(ObjectUtils.toString(lt_list.get(0).get("CARE_STYLE"))));
		}
		
		// 現有保單一覽表
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT B.PLAN_TYPE, C.COM_NAME, A.POLICY_NBR, A.PRD_NAME, A.CURR_CD, A.POLICY_ASSURE_AMT || A.UNIT AS POLICY_ASSURE_AMT, A.POLICY_RETURN_AMT1, A.POLICY_RETURN_AMT2, A.POLICY_RETURN_AMT3, A.POLICY_RETURN_AMT4 FROM TBINS_PLAN_AST_INSLIST A ");
		sql.append("LEFT JOIN TBINS_PLAN_DTL B ON A.PLAN_D_KEYNO = B.PLAN_D_KEYNO ");
		sql.append("LEFT JOIN TBPRD_INSDATA_COMPANY C ON A.COM_ID = C.COM_ID ");
		sql.append("WHERE B.PLAN_KEYNO = :plan_keyno ");
		sql.append("AND B.PLAN_TYPE IN (:plan_type) ");
		sql.append("ORDER BY B.PLAN_TYPE ");
		queryCondition.setObject("plan_keyno", inputVO.getPlanKeyno());
		queryCondition.setObject("plan_type", inputVO.getChkType());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		List<Map<String, String>> ans_list1 = new ArrayList<Map<String,String>>();
		for(Map<String, Object> map : list) {
			Map<String, String> list_map = new HashMap<String, String>();
			// by sa
			if("1".equals(map.get("PLAN_TYPE").toString())) {
				list_map.put("COM_NAME", ObjectUtils.toString(map.get("COM_NAME")));
				list_map.put("POLICY_NBR", ObjectUtils.toString(map.get("POLICY_NBR")));
				list_map.put("PRD_NAME", ObjectUtils.toString(map.get("PRD_NAME")));
				list_map.put("PLAN_TYPE", "壽險");
				list_map.put("POLICY_ASSURE_AMT", ObjectUtils.toString(map.get("POLICY_ASSURE_AMT")));
				list_map.put("POLICY_RETURN_AMT", map.get("POLICY_RETURN_AMT1") != null ? nf.format(map.get("POLICY_RETURN_AMT1")) : "");
			}
			else if("2".equals(map.get("PLAN_TYPE").toString())) {
				list_map.put("COM_NAME", ObjectUtils.toString(map.get("COM_NAME")));
				list_map.put("POLICY_NBR", ObjectUtils.toString(map.get("POLICY_NBR")));
				list_map.put("PRD_NAME", ObjectUtils.toString(map.get("PRD_NAME")));
				list_map.put("PLAN_TYPE", "意外險");
				list_map.put("POLICY_ASSURE_AMT", ObjectUtils.toString(map.get("POLICY_ASSURE_AMT")));
				list_map.put("POLICY_RETURN_AMT", map.get("POLICY_RETURN_AMT1") != null ? nf.format(map.get("POLICY_RETURN_AMT1")) : "");
			}
			else if("3".equals(map.get("PLAN_TYPE").toString())) {
				list_map.put("COM_NAME", ObjectUtils.toString(map.get("COM_NAME")));
				list_map.put("POLICY_NBR", ObjectUtils.toString(map.get("POLICY_NBR")));
				list_map.put("PRD_NAME", ObjectUtils.toString(map.get("PRD_NAME")));
				list_map.put("PLAN_TYPE", "醫療險");
				list_map.put("POLICY_ASSURE_AMT", "住院日額:" + map.get("POLICY_ASSURE_AMT"));
				list_map.put("POLICY_RETURN_AMT", map.get("POLICY_RETURN_AMT3") != null ? nf.format(map.get("POLICY_RETURN_AMT3")) : "");
			}
			else if("4".equals(map.get("PLAN_TYPE").toString())) {
				list_map.put("COM_NAME", ObjectUtils.toString(map.get("COM_NAME")));
				list_map.put("POLICY_NBR", ObjectUtils.toString(map.get("POLICY_NBR")));
				list_map.put("PRD_NAME", ObjectUtils.toString(map.get("PRD_NAME")));
				list_map.put("PLAN_TYPE", "重大疾病\n(癌症、長照)");
				list_map.put("POLICY_ASSURE_AMT", ObjectUtils.toString(map.get("POLICY_ASSURE_AMT")));
				// 每月(AMT3)跟住院(AMT2)比取有值的，若都沒值取一次給付(AMT1)
				if((map.get("POLICY_RETURN_AMT2") == null || new BigDecimal(ObjectUtils.toString(map.get("POLICY_RETURN_AMT2"))).compareTo(BigDecimal.ZERO) == 0) 
						&& (map.get("POLICY_RETURN_AMT3") == null || new BigDecimal(ObjectUtils.toString(map.get("POLICY_RETURN_AMT3"))).compareTo(BigDecimal.ZERO) == 0)) {
					list_map.put("POLICY_RETURN_AMT", "一次給付:" + (map.get("POLICY_RETURN_AMT1") != null ? nf.format(map.get("POLICY_RETURN_AMT1")) : ""));
				} else {
					if(map.get("POLICY_RETURN_AMT3") != null && new BigDecimal(ObjectUtils.toString(map.get("POLICY_RETURN_AMT3"))).compareTo(BigDecimal.ZERO)>0) {
						list_map.put("POLICY_RETURN_AMT", "每月給付:" + (map.get("POLICY_RETURN_AMT3") != null ? nf.format(map.get("POLICY_RETURN_AMT3")) : ""));
					}
					else if(map.get("POLICY_RETURN_AMT2") != null && new BigDecimal(ObjectUtils.toString(map.get("POLICY_RETURN_AMT2"))).compareTo(BigDecimal.ZERO)>0) {
						list_map.put("POLICY_RETURN_AMT", "住院日額:" + (map.get("POLICY_RETURN_AMT2") != null ? nf.format(map.get("POLICY_RETURN_AMT2")) : ""));
					}
				}
				
				
			}
			ans_list1.add(list_map);
		}
		data.addRecordList("first_list", ans_list1);
		
		// 保單規劃缺口
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT PLAN_TYPE, SHD_PROTECT1, SHD_PROTECT2, SHD_PROTECT3, NOW_PROTECT1, NOW_PROTECT2, NOW_PROTECT3, PROTECT_GAP1, PROTECT_GAP2, PROTECT_GAP3 ");
		sql.append("FROM TBINS_PLAN_DTL WHERE PLAN_KEYNO = :plan_keyno ");
		sql.append("AND PLAN_TYPE IN (:plan_type) ");
		queryCondition.setObject("plan_keyno", inputVO.getPlanKeyno());
		queryCondition.setObject("plan_type", inputVO.getChkType());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		List<Map<String, String>> ans_list2 = new ArrayList<Map<String,String>>();
		for(Map<String, Object> map : list2) {
			Map<String, String> list2_map = new HashMap<String, String>();
			// by sa
			if("1".equals(map.get("PLAN_TYPE").toString())) {
				list2_map.put("PLAN_TYPE", "壽險規劃");
				list2_map.put("SHD_PROTECT1", nf.format(map.get("SHD_PROTECT1")));
				list2_map.put("NOW_PROTECT1", nf.format(map.get("NOW_PROTECT1")));
				list2_map.put("PROTECT_GAP1", nf.format(map.get("PROTECT_GAP1")));
				// null show 0
				list2_map.put("SHD_PROTECT2", "0");
				list2_map.put("NOW_PROTECT2", "0");
				list2_map.put("PROTECT_GAP2", "0");
			}
			else if("2".equals(map.get("PLAN_TYPE").toString())) {
				list2_map.put("PLAN_TYPE", "意外規劃");
				list2_map.put("SHD_PROTECT1", nf.format(map.get("SHD_PROTECT1")));
				list2_map.put("NOW_PROTECT1", nf.format(map.get("NOW_PROTECT1")));
				list2_map.put("PROTECT_GAP1", nf.format(map.get("PROTECT_GAP1")));
				// null show 0
				list2_map.put("SHD_PROTECT2", "0");
				list2_map.put("NOW_PROTECT2", "0");
				list2_map.put("PROTECT_GAP2", "0");
			}
			else if("3".equals(map.get("PLAN_TYPE").toString())) {
				list2_map.put("PLAN_TYPE", "醫療規劃");
				list2_map.put("SHD_PROTECT2", nf.format(map.get("SHD_PROTECT2")));
				list2_map.put("NOW_PROTECT2", nf.format(map.get("NOW_PROTECT2")));
				list2_map.put("PROTECT_GAP2", nf.format(map.get("PROTECT_GAP2")));
				// null show 0
				list2_map.put("SHD_PROTECT1", "0");
				list2_map.put("NOW_PROTECT1", "0");
				list2_map.put("PROTECT_GAP1", "0");
			}
			else if("4".equals(map.get("PLAN_TYPE").toString())) {
				if(map.get("SHD_PROTECT1") != null) {
					list2_map.put("PLAN_TYPE", "重疾規劃");
					list2_map.put("SHD_PROTECT1", nf.format(map.get("SHD_PROTECT1")));
					list2_map.put("NOW_PROTECT1", nf.format(map.get("NOW_PROTECT1")));
					list2_map.put("PROTECT_GAP1", nf.format(map.get("PROTECT_GAP1")));
					// null show 0
					list2_map.put("SHD_PROTECT2", "0");
					list2_map.put("NOW_PROTECT2", "0");
					list2_map.put("PROTECT_GAP2", "0");
				}
				else if(map.get("SHD_PROTECT2") != null) {
					list2_map.put("PLAN_TYPE", "癌症規劃");
					list2_map.put("SHD_PROTECT2", nf.format(map.get("SHD_PROTECT2")));
					list2_map.put("NOW_PROTECT2", nf.format(map.get("NOW_PROTECT2")));
					list2_map.put("PROTECT_GAP2", nf.format(map.get("PROTECT_GAP2")));
					// null show 0
					list2_map.put("SHD_PROTECT1", "0");
					list2_map.put("NOW_PROTECT1", "0");
					list2_map.put("PROTECT_GAP1", "0");
				}
				else if(map.get("SHD_PROTECT3") != null) {
					list2_map.put("PLAN_TYPE", "長看規劃");
					list2_map.put("SHD_PROTECT2", nf.format(map.get("SHD_PROTECT3")));
					list2_map.put("NOW_PROTECT2", nf.format(map.get("NOW_PROTECT3")));
					list2_map.put("PROTECT_GAP2", nf.format(map.get("PROTECT_GAP3")));
					// null show 0
					list2_map.put("SHD_PROTECT1", "0");
					list2_map.put("NOW_PROTECT1", "0");
					list2_map.put("PROTECT_GAP1", "0");
				}
			}
			ans_list2.add(list2_map);
		}
		data.addRecordList("second_list", ans_list2);
		// 建議補足保障缺口之本行保險商品
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("select a.PLAN_TYPE, b.*, c.INSPRD_NAME from TBINS_PLAN_DTL a ");
		sql.append("left join TBINS_PLAN_SUG_INSLIST b on a.PLAN_D_KEYNO = b.PLAN_D_KEYNO ");
		sql.append("left join TBPRD_INS c on b.INSPRD_KEYNO = c.KEY_NO ");
		sql.append("where a.PLAN_KEYNO = :plan_keyno ");
		queryCondition.setObject("plan_keyno", inputVO.getPlanKeyno());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
		List<Map<String, String>> ans_list3_1 = new ArrayList<Map<String,String>>();
		List<Map<String, String>> ans_list3_2 = new ArrayList<Map<String,String>>();
		List<Map<String, String>> ans_list3_3 = new ArrayList<Map<String,String>>();
		List<Map<String, String>> ans_list3_4 = new ArrayList<Map<String,String>>();
		for(Map<String, Object> map : list3) {
			Map<String, String> list3_map = new HashMap<String, String>();
			list3_map.put("INSPRD_ID", ObjectUtils.toString(map.get("INSPRD_ID")));
			list3_map.put("INSPRD_NAME", ObjectUtils.toString(map.get("INSPRD_NAME")));
			list3_map.put("PAY_YEARS", ObjectUtils.toString(map.get("PAY_YEARS")));
			list3_map.put("UNIT", ins_unit.get(ObjectUtils.toString(map.get("UNIT"))));
			list3_map.put("CURR_CD", ObjectUtils.toString(map.get("CURR_CD")));
			if("TWD".equals(map.get("CURR_CD"))) {
				list3_map.put("POLICY_ASSURE_AMT", map.get("POLICY_ASSURE_AMT_TWD") != null ? nf.format(map.get("POLICY_ASSURE_AMT_TWD")) : "0");
				list3_map.put("POLICY_FEE", map.get("POLICY_FEE_TWD") != null ? nf.format(map.get("POLICY_FEE_TWD")) : "0");
			} else {
				list3_map.put("POLICY_ASSURE_AMT", map.get("POLICY_ASSURE_AMT") != null ? nf.format(map.get("POLICY_ASSURE_AMT")) : "0");
				list3_map.put("POLICY_FEE", map.get("POLICY_FEE") != null ? nf.format(map.get("POLICY_FEE")) : "0");
			}
			if("1".equals(map.get("PLAN_TYPE").toString()))
				ans_list3_1.add(list3_map);
			else if("2".equals(map.get("PLAN_TYPE").toString()))
				ans_list3_2.add(list3_map);
			else if("3".equals(map.get("PLAN_TYPE").toString()))
				ans_list3_3.add(list3_map);
			else if("4".equals(map.get("PLAN_TYPE").toString())) {
				list3_map.put("SUGGEST_TYPE", para_no4_suggest.get(ObjectUtils.toString(map.get("SUGGEST_TYPE"))));
				ans_list3_4.add(list3_map);
			}
		}
		data.addRecordList("third_list_type1", ans_list3_1);
		data.addRecordList("third_list_type2", ans_list3_2);
		data.addRecordList("third_list_type3", ans_list3_3);
		data.addRecordList("third_list_type4", ans_list3_4);
		
		return data;		
	}
	
	private String getSN(String name) throws JBranchException {
		String ans = "";
		switch(name) {
			case "REPORT":
				QueryConditionIF qc_report = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				qc_report.setQueryString("SELECT TBINS_REPORT_SEQ.nextval AS SEQ FROM DUAL");
				List<Map<String, Object>> report_list = dam.exeQuery(qc_report);
				ans = ObjectUtils.toString(report_list.get(0).get("SEQ"));
				break;
		}
		return ans;
	}
		
}