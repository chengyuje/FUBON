package com.systex.jbranch.app.server.fps.ins110;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.ibm.icu.text.SimpleDateFormat;
import com.lowagie.text.DocumentException;
import com.systex.jbranch.app.server.fps.ins.parse.WSMappingParserUtils;
import com.systex.jbranch.app.server.fps.ins100.INS100;
import com.systex.jbranch.app.server.fps.ins810.INS810;
import com.systex.jbranch.app.server.fps.ins810.INS810InputVO;
import com.systex.jbranch.app.server.fps.ins810.InOutBuyDataProcess;
import com.systex.jbranch.app.server.fps.ins810.IntegrationOutputVO;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.PdfUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;


/**
 * ins110
 * 
 * @author Jimmy
 * @date 2017/11/06
 * @spec null
 */


@Component("ins110")
@Scope("request")
public class INS110 extends FubonWmsBizLogic{
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(INS110.class);
	
	@Autowired @Qualifier("ins100")
	private INS100 ins100;
	
	public INS100 getIns100() {
		return ins100;
	}

	public void setIns100(INS100 ins100) {
		this.ins100 = ins100;
	}

	@Autowired @Qualifier("ins810")
	private INS810 ins810;
	
	public INS810 getIns810() {
		return ins810;
	}

	public void setIns810(INS810 ins810) {
		this.ins810 = ins810;
	}

	//======================== WEB 調用 ========================
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException{
		
		INS110InputVO inputVO = (INS110InputVO) body;
		INS110OutputVO outputVO = new INS110OutputVO();
		Map outputMap = new HashMap(); 
		
		String custId = inputVO.getINSURED_ID();
		String loginBraNbr = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
		ArrayList<String> loginAO = (ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
		outputVO.setDataList(getInsuranceDataList(custId, loginBraNbr, loginAO));
		
		sendRtnObject(outputVO);
				
	}
	
	//前次健診紀錄
	public void PrintLastTime(Object body, IPrimitiveMap header) throws DocumentException{
		List<String> url_list =  new ArrayList<String>();
		List<Map<String, Object>> blobList = new ArrayList<Map<String,Object>>();
		INS110InputVO inputVO = (INS110InputVO) body;
		
		try {
			
			blobList = PrintLastTimeCommon(inputVO.getINSURED_ID());
			
			if(blobList.size() > 0){
				String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				Blob blob = (Blob) blobList.get(0).get("REPORT_FILE");
				int blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
				
				Date now = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDD");
				String dateString = sdf.format(now);
				
				String fileName = dateString+"前次健診紀錄.pdf";
				String uuid = UUID.randomUUID().toString();
				
				File targetFile = new File(filePath,uuid+".pdf");
				FileOutputStream fos = new FileOutputStream(targetFile);
				fos.write(blobAsBytes);
				fos.close();
				
//				url_list.add("doc//INS//INS000_COVER.pdf");
//				url_list.add("temp//" + uuid + ".pdf");
				
				// 合併所有PDF＆加密碼
//				String reportURL = PdfUtil.mergePDF(url_list, inputVO.getINSURED_ID());
//				String reportURL = PdfUtil.mergePDF(url_list, false);
//				this.notifyClientToDownloadFile(reportURL, fileName);
				
				// 先前上傳時已是合併＆加密檔案，故讀出即可。
				this.notifyClientToDownloadFile("temp//" + uuid + ".pdf", fileName);
				
			}
			INS110OutputVO outputVO = new INS110OutputVO();
			outputVO.setDataList(blobList);
			sendRtnObject(outputVO);
			
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JBranchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//刪除保單(主約+附約 or 單獨刪除附約)
	public void deleteOutBuyDtl(Object body, IPrimitiveMap header){
		INS110InputVO inputVO = (INS110InputVO) body;
		dam = getDataAccessManager();
		QueryConditionIF qc = null;
		StringBuffer sb = new StringBuffer();
		
		try {
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			switch (inputVO.getIS_MAIN()) {
			case "Y"://主約
				sb.append(" DELETE TBINS_OUTBUY_MAST WHERE INSSEQ = :insseq ");
				qc.setObject("insseq", inputVO.getINSSEQ());
				break;
			case "N"://附約
				sb.append(" DELETE TBINS_OUTBUY_MAST WHERE KEYNO = :keyno ");
				qc.setObject("keyno", inputVO.getKEYNO());
				break;
			default:
				break;
			}
			
			qc.setQueryString(sb.toString());
			dam.exeUpdate(qc);
			sendRtnObject(null);
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JBranchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//======================== WS 調用 ========================
	/**
     * 提供給 APP 使用的 API <br>
     * <code> <b> 2.5.15 </b> 取得前次健診報告書</code>
     * @param inputVO : custId : 客戶Id
     * @return Object : 
     * @throws JBranchException : <br>
     *                <p>
     *                             <b>客製訊息如下 :</b>
     *                             <li>查無資料: </li>
     *                </p>
	 * @throws SQLException 
     */
	public void getLastInsuranceReport(Object body, IPrimitiveMap header) throws JBranchException, SQLException{
		
		List<Map<String, Object>> blobList = new ArrayList<Map<String,Object>>();

		GenericMap inputGmap = new GenericMap((Map) body);			
		//custId : 客戶Id
		String custId = inputGmap.getNotNullStr("custId");
		//用來存放前次健診報告書資料
		Map outputMap = new HashMap(); 
				
			blobList = PrintLastTimeCommon(custId);
			
			if(CollectionUtils.isEmpty(blobList)){
				 throw new APException("無前次健診紀錄");
			}
			
			//將存放PDF的blob轉成byte[]
			String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			Map<String, Object> blobMap = blobList.get(0);
			Blob blob = (Blob) blobMap.get("REPORT_FILE");
			int blobLength = (int) blob.length();
			byte[] blobAsBytes = blob.getBytes(1, blobLength);
							
			//fileCode : PDF投/組編號(與理規共用)
			outputMap.put("fileCode", blobMap.get("PLAN_ID"));
			//file : PDF檔案的byte[]，使用Base64加密
			outputMap.put("file", DatatypeConverter.printBase64Binary(blobAsBytes));
			
			sendRtnObject(outputMap);		

	}
	
   /**
    * 提供給 APP 使用的 API <br>
    * <code> <b> 2.5.2 </b> 刪除保單(主約+附約 or 單獨刪除附約)</code>
    * @param inputVO : insSeq : 保單號碼, policySerial : 行外保單序號(附約)
    * @return Object : null
    * @throws JBranchException : <br>
    *                <p>
    *                             <b>客製訊息如下 :</b>
    *                             <li>查無資料: </li>
    *                </p>
    */
	public void deletePolicy(Object body, IPrimitiveMap header){
		GenericMap inputGmap = new GenericMap((Map) body);		
		//取得保單自訂序號 : insSeq
		String insSeq = inputGmap.getNotNullStr("insSeq");
		//取得行外保單序號(附約) : policySerial
		String policySerial = inputGmap.getNotNullStr("policySerial");
		
		dam = getDataAccessManager();
		QueryConditionIF qc = null;
		StringBuffer sb = new StringBuffer();
		
		try {
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			//如果行外保單序號不存在，主約連同附約一起刪除
			if(StringUtils.isEmpty(policySerial)){
				sb.append(" DELETE TBINS_OUTBUY_MAST WHERE INSSEQ = :insseq ");
				qc.setObject("insseq", insSeq);
			}
			//如果行外保單序號存在，只刪除附約
			else{
				sb.append(" DELETE TBINS_OUTBUY_MAST WHERE KEYNO = :keyno ");
				qc.setObject("keyno", policySerial);
			}
			
			qc.setQueryString(sb.toString());
			dam.exeUpdate(qc);
			sendRtnObject(null);
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JBranchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    /**
    * 提供給 APP 使用的 API <br>
    * <code> <b> 2.5.1 </b> 依客戶取得其所有保單資料(本行+行外)</code>
    * @param inputVO : custId : 客戶ID
    * @return Object : 
    * @throws JBranchException : <br>
    *                <p>
    *                             <b>客製訊息如下 :</b>
    *                             <li>查無資料: </li>
    *                </p>
    */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getAllPolicy(Object body, IPrimitiveMap header) throws JBranchException, ParseException{
		GenericMap map = new GenericMap((Map) body);
		System.out.println(new Gson().toJson(map.getParamMap()));
		// 調用其他資訊源或服務準備參數
		String custId = map.getNotNullStr("custId");
		String loginBraNbr = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
		ArrayList<String> loginAO = (ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
		List<Map<String, Object>> custInfos = getIns100().getCustSearchResult(custId, loginBraNbr, loginAO);
		Map<String, Object> custMap = CollectionSearchUtils.findMapInColleciton(custInfos, "CUST_ID", custId);
		
		// 計算有無簽署保單健檢同意書 大於七天為過期
		Date now = new Date();
		boolean agreement = false;
		Object reportDateObj = custMap.get("REPORT_DATE");
		if(reportDateObj != null) {
			Date reportDate = (Date) reportDateObj;
			agreement = (now.getTime()-reportDate.getTime())/(24*60*60*1000) > 7 ? false : true;
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("custId", custMap.get("CUST_ID"));
		resultMap.put("custName", custMap.get("CUST_NAME") == null ? "" : custMap.get("CUST_NAME"));
		resultMap.put("agreement_i", agreement);
		
		List<Map<String, Object>> fromList = new ArrayList<Map<String, Object>>(); 	// 來源資料未整理
		List<Map<String, Object>> toList = new ArrayList<Map<String, Object>>();	// 回傳資料 根據 ws 要求整理過的
		
		// 調用資訊源取行內外保單
		fromList = getInsuranceDataList(custId, loginBraNbr, loginAO);
		
		// 組合行內外保單成 ws 結構
		BigDecimal sumInsYearFee = WSMappingParserUtils.wsAllPolicyMapping(fromList, toList);
		
		resultMap.put("insyearfee_y", sumInsYearFee);
		resultMap.put("policy", toList);
		System.out.println(new Gson().toJson(resultMap));
		this.sendRtnObject(resultMap);
	}
	
	//======================== 共用 調用 & 處理========================
	//前次健診紀錄共用
	public List<Map<String, Object>> PrintLastTimeCommon(String custId){
		
		List<Map<String, Object>> blobList = new ArrayList<Map<String,Object>>();
		
		dam = getDataAccessManager();
		QueryConditionIF qc = null;
		StringBuffer sb = new StringBuffer();
		
		try {
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append(" select b.REPORT_FILE ")
			.append(" from TBINS_EXAM_MAIN a,TBINS_REPORT b ")
			.append(" where a.EXAM_KEYNO = b.PLAN_ID ")
			.append(" and a.CUST_ID = :insured_id ")
			.append(" order by a.PRINT_DATE desc fetch first 1 rows only ");
			
			qc.setObject("insured_id", custId);
			qc.setQueryString(sb.toString());
			blobList = dam.exeQuery(qc);
						
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JBranchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return blobList; 
	}
	
	/**
	 * 台幣續期保費換算
	 * @param dataMap 單一筆資料來源
	 * @param currConvMap 轉換表
	 */
	private void currConv(Map<String, Object> dataMap, Map<String, BigDecimal> currConvMap) {
		//原幣幣別
		String currCd = ObjectUtils.toString(dataMap.get("CURR_CD"));
		//原幣
		String insyearfee = ObjectUtils.toString(dataMap.get("INSYEARFEE"));
		//原幣參考匯率
		BigDecimal refExcRate = BigDecimal.valueOf(1.0);
		
		//取得參考匯率
		if(StringUtils.isNotBlank(currCd) && MapUtils.isNotEmpty(currConvMap) && currConvMap.get(currCd) != null){
			refExcRate = currConvMap.get(currCd);
		}

		//原幣轉台幣
		if(StringUtils.isNotBlank(insyearfee)) {
			BigDecimal insYearFreeValue = new BigDecimal(insyearfee);
			dataMap.put("LOCAL_INSYEARFEE", insYearFreeValue.multiply(refExcRate).setScale(0, BigDecimal.ROUND_HALF_UP));
		}
	}
	
	/**
	 * 年度所繳保費計算
	 * @param data  單一筆資料來源
	 * @param sdf 日期格式
	 * @throws ParseException 
	 * @throws APException 
	 */
	private void totalInsYearFee(Map<String, Object> data, SimpleDateFormat sdf) throws APException, ParseException {
		BigDecimal totalInsyearfee= new BigDecimal(0);	// 年度所繳保費計算用
		if (!StringUtils.isEmpty(ObjectUtils.toString(data.get("PAY_TYPE")))) {
			String payType = String.valueOf(data.get("PAY_TYPE"));
			String localInsyearfee = ObjectUtils.toString(data.get("LOCAL_INSYEARFEE"));
			totalInsyearfee = new BigDecimal(StringUtils.isEmpty(localInsyearfee) ? "0" :  localInsyearfee);
			
			// 邏輯 mantis UAT 1.5 4543 
			String paymentYearStr = ObjectUtils.toString(data.get("PAYMENTYEAR_SEL"));
			Integer paymentYearInt = 0;
			
			// 名稱0開頭，取0之後的數字
			if(paymentYearStr.startsWith("0")) {
				paymentYearInt = Integer.parseInt(paymentYearStr.substring(1));
			} else {
				// 字串裡面有非數字的
				if(!paymentYearStr.matches("^[0-9]*$")) {
					paymentYearStr = paymentYearStr.replaceAll("@", "");
					INS810InputVO inputVO = new INS810InputVO();
					inputVO.setBirthday(sdf.parse(ObjectUtils.toString(data.get("INSURED_BIRTHDAY"))));
					int age = getIns810().getAge(inputVO).getAge();
					int canAge = Integer.parseInt(paymentYearStr.trim());
					paymentYearInt = canAge >= age ? 9999 : -9999; // 取得 LIST_Y 比當前年齡小要算否則不算
				} 
				// 字串裡面都是數字的
				else {
					paymentYearInt = !paymentYearStr.isEmpty() ?Integer.parseInt(paymentYearStr) : -9999;
				}
			}
			
			Integer effect = Integer.parseInt((String.valueOf(data.get("EFFECTED_DATE"))).substring(0, 4));
			Integer now = Integer.parseInt(sdf.format(new Date().getTime()).substring(0, 4));

			// 躉繳-0  // 彈性繳-99 以及 今年度之前的單(含今年) 的都 * 0 所以是 0 
			if((effect + paymentYearInt -1) <= now || StringUtils.equals("0", payType) || StringUtils.equals("99", payType)) {
				data.put("INSYEARFEE_YEAR", 0);
			} 
			// 年繳-1 // 半年繳-2 // 季繳-4 // 月繳-12
			else {
				data.put("INSYEARFEE_YEAR", totalInsyearfee.multiply(new BigDecimal(String.valueOf(data.get("PAY_TYPE")))));
			}
		}
	}
		
	/**
	 * 取得行內外保單統一調用
	 * @param custId
	 * @param loginBraNbr
	 * @param loginAO
	 * @return 行內外保單
	 * @throws JBranchException
	 * @throws ParseException 
	 */
	public List<Map<String, Object>> getInsuranceDataList(String custId, String loginBraNbr, ArrayList<String> loginAO) throws JBranchException, ParseException {
		List<Map<String, Object>> insuranceDataList = new ArrayList<Map<String,Object>>();
		INS810InputVO ins810inputVO = new INS810InputVO();
		IntegrationOutputVO integrationOupputVO = new IntegrationOutputVO();

		ins810inputVO.setCUST_ID(custId);		
		ins810inputVO.setLoginBranch(loginBraNbr);
		ins810inputVO.setLoginAOCode(loginAO);
		
		integrationOupputVO = getIns810().queryPolicyContent(ins810inputVO , InOutBuyDataProcess.PAYMENTYEAR_SEL_NAME);
		
		insuranceDataList = integrationOupputVO.getIntegrationList();
		
		// 幣別換算 & 年度所繳保費計算
		Map<String , BigDecimal> refExcRateMap = getIns810().queryRefExcRate(); // 參考匯率All
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 日期轉換
		for(Map<String, Object> fromMap : insuranceDataList) {
			// 針對附約沒有繳費年期，取其主約的繳費年期當基準做計算
			// 基本上只有行外保單會跑進來 (行內保單一律都有繳費年期，附約通通都有)
			if("N".equals(ObjectUtils.toString(fromMap.get("IS_MAIN"))) && ObjectUtils.toString(fromMap.get("PAYMENTYEAR_SEL")).isEmpty()) {
				Map<String, Object> findObj = new HashMap<String, Object>();
				findObj.put("IS_MAIN", new Character('Y'));
				findObj.put("INSSEQ", ObjectUtils.toString(fromMap.get("INSSEQ")));
				Map<String, Object> mainMap = CollectionSearchUtils.findMapByKey(insuranceDataList, findObj, "IS_MAIN", "INSSEQ");
				if(mainMap != null) {
					fromMap.put("PAYMENTYEAR_SEL", mainMap.get("PAYMENTYEAR_SEL"));
				}
			}
			Map<String, Object> toMap = new HashMap<String, Object>();
			currConv(fromMap, refExcRateMap); // 匯率換算 原幣轉台幣
			totalInsYearFee(fromMap, sdf); // 保費 Total 計算
		}
		return insuranceDataList;
	}
	
	/**
	 * 提供理財規劃使用計算 未來一年應繳保費
	 * @param custId 客戶 ID
	 * @param insType 險種類型 {1=儲蓄型, 2=投資型, 3=保障型}
	 * @return sumInsYearFee
	 * @throws JBranchException
	 * @throws ParseException
	 */
	public BigDecimal getTotalInsyearfee(String custId, String loginBraNbr, ArrayList<String> loginAO, String insType) throws JBranchException, ParseException{
		List<Map<String, Object>> fromList = new ArrayList<Map<String, Object>>(); 	// 來源資料未整理
		BigDecimal sumInsYearFee = BigDecimal.ZERO;
		
		fromList = getInsuranceDataList(custId, loginBraNbr, loginAO);
		
		for(Map<String, Object> fromMap : fromList) {
			if(ObjectUtils.toString(fromMap.get("INS_TYPE")).equals(insType)) {
				sumInsYearFee = sumInsYearFee.add(new GenericMap(fromMap).getBigDecimal("INSYEARFEE_YEAR"));
			}
		}
		return sumInsYearFee;
	}
}
