package com.systex.jbranch.fubon.commons.seniorValidation;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.systex.jbranch.app.server.fps.iot920.IOT920;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.parse.JsonUtil;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.fubon.webservice.rs.SeniorCitizenClientRS;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("SeniorValidation")
@Scope("request")
public class SeniorValidation extends FubonWmsBizLogic {
	private Logger logger = LoggerFactory.getLogger(SeniorValidation.class);
	private DataAccessManager dam = null;
	
	private String seniorCust01ANoBuyDTL;
	private String seniorCust01FChkBuyDTL;
	private Date seniorCustValidDate; //高齡評估量表到期日	
	private Date seniorCustMatchDate; //高齡評估量表承作日
	
	public String getSeniorCust01ANoBuyDTL() {
		return seniorCust01ANoBuyDTL;
	}
	public void setSeniorCust01ANoBuyDTL(String seniorCust01ANoBuyDTL) {
		this.seniorCust01ANoBuyDTL = seniorCust01ANoBuyDTL;
	}
	public String getSeniorCust01FChkBuyDTL() {
		return seniorCust01FChkBuyDTL;
	}
	public void setSeniorCust01FChkBuyDTL(String seniorCust01FChkBuyDTL) {
		this.seniorCust01FChkBuyDTL = seniorCust01FChkBuyDTL;
	}
	public Date getSeniorCustValidDate() {
		return seniorCustValidDate;
	}
	public void setSeniorCustValidDate(Date seniorCustValidDate) {
		this.seniorCustValidDate = seniorCustValidDate;
	}
	public Date getSeniorCustMatchDate() {
		return seniorCustMatchDate;
	}
	public void setSeniorCustMatchDate(Date seniorCustMatchDate) {
		this.seniorCustMatchDate = seniorCustMatchDate;
	}
	
	
	/***
	 * 下單適配高齡評估量表檢核
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void validSeniorCustEval(Object body, IPrimitiveMap header) throws Exception {
		SeniorValidationInputVO inputVO = (SeniorValidationInputVO) body;
		SeniorValidationOutputVO outputVO = new SeniorValidationOutputVO();
		
		outputVO.setSeniorCustEvalResult(this.validSeniorCustEval(inputVO.getCustID()));
		this.sendRtnObject(outputVO);
	}
	
	/***
	 * 下單適配高齡評估量表檢核
	 * @param custId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, String>> validSeniorCustEval(String custID) throws Exception  {
		List<Map<String, String>> rtnList = new ArrayList<Map<String, String>>();
		
		custID = custID.toUpperCase();
		//65歲非高齡客戶不需檢核
		if(getCustAge(custID).compareTo(new BigDecimal(65)) < 0) {
			return rtnList;
		}
		
		//A：高齡客戶資訊觀察表是否已過期
		if(!validSeniorCustPeriod(custID, "SOT")) {
			throw new JBranchException("此客戶高齡客戶資訊觀察表已過期，需先重新填寫。");
		}
		
		//B：高齡客戶資訊觀察表是否有填答不可申購的選項(金融認知中第二或第三個選項)
		if(validSeniorCust02FNoBuy(custID, "SOT")) {
			throw new JBranchException("此客戶無足夠的金融認知，不得受理委託申購投資商品。");
		}
		
		//C：高齡客戶資訊觀察表取得能力表現是否填答需主管確認方可申購
		if(validSeniorCust01BChkBuy(custID, "SOT")) {
			SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("invalidCode", "C");
			map.put("matchDate", ft.format(this.getSeniorCustMatchDate()));
			map.put("invalidMsg", this.getSeniorCust01ANoBuyDTL());
			
			rtnList.add(map);
		}
		
		//D：高齡客戶資訊觀察表是否有填答不建議申購之選項(健康情況第二項及第四項)
		if(validSeniorCust01ANoBuy(custID, "SOT")) {
			//msg = "請引導客戶申購可利用網行銀、電話銀行，或可臨櫃授權人代理交易之商品。"; 
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("invalidCode", "D");
			map.put("invalidMsg", "請引導客戶申購可利用網行銀、電話銀行，或可臨櫃授權人代理交易之商品。");
			rtnList.add(map);
		}
		
		//E：高齡客戶資訊觀察表填寫日是否為適配當日
		if(!validSeniorCustEvalDate(custID, "SOT")) {
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("invalidCode", "E");
			rtnList.add(map);
		}
		
		//F：金融認知是否填答第一選項
		if(validSeniorCust01FChkBuy(custID, "SOT")) {
			Map<String, String> map = new HashMap<String, String>();
			SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
			
			map.put("invalidCode", "F");
			map.put("matchDate", ft.format(this.getSeniorCustMatchDate()));
			map.put("invalidMsg", this.getSeniorCust01FChkBuyDTL());
			
			rtnList.add(map);
		}
		
		return rtnList;
	}
	
	/***
	 * 保險要保人購買檢核高齡評估量表檢核(理專)
	 * 要保人或被保人或繳款人，年齡>=64.5歲(保險年齡=65歲以上)
	 * A:須檢核該客戶ID於本行的高齡評估作業異動日期是否介於要保書申請日-3個工作天～要保書申請日區間。
	 * @param custId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, String>> validSeniorCustEvalIOTRM(SeniorValidationInputVO inputVO) throws Exception  {
		List<Map<String, String>> rtnList = new ArrayList<Map<String, String>>();	
		
		//A：高齡評估作業異動日期是否介於要保書申請日-3個工作天～要保書申請日區間
	    if(!validSeniorCustInsFit(inputVO)) {
	    	Map<String, String> map = new HashMap<String, String>();
	    	map.put("invalidCode", "A");
			rtnList.add(map);
	    }
		
		return rtnList;
	}
	
	/***
	 * 保險要保人購買檢核高齡評估量表檢核(主管覆核)
	 * 要保人或被保人或繳款人，年齡>=64.5歲(保險年齡=65歲以上)
	 * B：高齡客戶資訊觀察表金融認知結果是否填答<>4沒有上述情形
	 * C：高齡客戶資訊觀察表取得能力表現是否填答<>8.無上述情形
	 * D：金融認知是否填答第一選項：無法自行透過報章雜誌或網路查詢，取得金融資訊
	 * E：高齡客戶資訊觀察表是否有填答不可申購的選項(金融認知中第二或第三個選項)
	 * @param custId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, String>> validSeniorCustEvalIOTBOSS(String custID) throws Exception  {
		List<Map<String, String>> rtnList = new ArrayList<Map<String, String>>();
		custID = custID.toUpperCase();
		
		//B：高齡客戶資訊觀察表金融認知結果是否填答<>4沒有上述情形
		if(!validSeniorCust02FAns4(custID, "IOT")) {
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("invalidCode", "B"); //金融認知結果<>4沒有上述情形
			rtnList.add(map);
		}
		
		//C：高齡客戶資訊觀察表取得能力表現是否填答<>8.無上述情形
		if(validSeniorCust01BChkBuy(custID, "IOT")) {
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("invalidCode", "C"); //能力表現填答<>8.無上述情形
			rtnList.add(map);
		}
		
		//D：金融認知是否填答第一選項：無法自行透過報章雜誌或網路查詢，取得金融資訊
		//Y：是/N：否
		if(validSeniorCust01FChkBuy(custID, "IOT")) {
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("invalidCode", "D"); //金融認知填答第一選項
			rtnList.add(map);
		}
		
		//E：高齡客戶資訊觀察表是否有填答不可申購的選項(金融認知中第二或第三個選項)
		//true 是，有填答 (不可申購) false：否，沒有填答(可申購)
		if(validSeniorCust02FNoBuy(custID, "IOT")) {
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("invalidCode", "E"); //金融認知填答第二或第三個選項
			rtnList.add(map);
		}
		
		return rtnList;
	}
	
	/***
	 * 前一次填答紀錄：保險要保人購買檢核高齡評估量表檢核(主管覆核)
	 * 要保人或被保人或繳款人，年齡>=64.5歲(保險年齡=65歲以上)
	 * CL：前一次填答紀錄：高齡客戶資訊觀察表取得能力表現是否填答<>8.無上述情形
	 * DL：前一次填答紀錄：金融認知是否填答第一選項：無法自行透過報章雜誌或網路查詢，取得金融資訊
	 * EL：前一次填答紀錄：高齡客戶資訊觀察表是否有填答不可申購的選項(金融認知中第二或第三個選項)
	 * @param custId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, String>> validSeniorCustEvalIOTLast(String custID) throws Exception  {
		List<Map<String, String>> rtnList = new ArrayList<Map<String, String>>();
		custID = custID.toUpperCase();
			
		//CL：前一次填答紀錄：高齡客戶資訊觀察表取得能力表現是否填答<>8.無上述情形
		if(validSeniorCust01BChkBuyLastAns(custID, "IOT")) {
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("invalidCode", "CL"); //能力表現填答<>8.無上述情形
			rtnList.add(map);
		}
		
		//CL：前一次填答紀錄：金融認知是否填答第一選項：無法自行透過報章雜誌或網路查詢，取得金融資訊
		//Y：是/N：否
		if(validSeniorCust01FChkBuyLastAns(custID, "IOT")) {
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("invalidCode", "DL"); //金融認知填答第一選項
			rtnList.add(map);
		}
		
		//CL：前一次填答紀錄：高齡客戶資訊觀察表是否有填答不可申購的選項(金融認知中第二或第三個選項)
		//true 是，有填答 (不可申購) false：否，沒有填答(可申購)
		if(validSeniorCust02FNoBuyLastAns(custID, "IOT")) {
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("invalidCode", "EL"); //金融認知填答第二或第三個選項
			rtnList.add(map);
		}
		
		return rtnList;
	}
	
	/***
	 * 取得高齡評估表承作日期
	 * WMS-CR-20240129-04_減少保險新契約照會專案第三階段
	 * @param inputVO
	 * @return 高齡評估表承作日期
	 * @throws Exception
	 */
	public Date validateSeniorCustEvalDateIns(String custId) throws Exception {
		//取得高齡評估表承作日期
		boolean chkEvalDate = validSeniorCustPeriod(custId, "SOT"); 
		
		return this.getSeniorCustMatchDate();
	}
	
	/***
	 * 取得高齡評估表健康情況答案選項
	 * WMS-CR-20240129-04_減少保險新契約照會專案第三階段
	 * getOldCust_01A_NoBuy
	 * @param custId
	 * @return 健康情況答案選項
	 * @throws Exception
	 */
	public List<String> getSeniorCustHealthDtlIns(String custId) throws Exception {
		//傳入參數
		GenericMap inputGmap = new GenericMap();
		inputGmap.put("CUST_ID", custId);
		inputGmap.put("SOURCE_METHOD", "IOT");
		
		//健康情況答案選項
		Map<String, Object> result = sendSeniorEvalAPI("getOldCust_01A_NoBuy", inputGmap);
		String healthyDtl = ObjectUtils.toString(result.get("HEALTHY_DTL"));
		
		//回傳Array形式
		return Arrays.asList(healthyDtl.split("\\s*;\\s*"));
	}
	
	/***
	 * 取得高齡評估表金融認知答案選項
	 * WMS-CR-20240129-04_減少保險新契約照會專案第三階段
	 * getOldCust_01A_NoBuy
	 * @param custId
	 * @return 金融認知答案選項
	 * @throws Exception
	 */
	public List<String> getSeniorCustCognitionDtlIns(String custId) throws Exception {
		//傳入參數
		GenericMap inputGmap = new GenericMap();
		inputGmap.put("CUST_ID", custId);
		inputGmap.put("SOURCE_METHOD", "IOT");
		
		//金融認知答案選項
		Map<String, Object> result = sendSeniorEvalAPI("getOldCust_02F_NoBuy", inputGmap);
		String cognitionDtl = ObjectUtils.toString(result.get("FINACIAL_COGNITION_DTL"));
		
		//回傳Array形式
		return Arrays.asList(cognitionDtl.split("\\s*;\\s*"));
	}
	
	private BigDecimal getCustAge(String custId) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT ROUND(MONTHS_BETWEEN(SYSDATE, MAST.BIRTH_DATE)/12, 4) AS AGE ");
		sb.append(" FROM TBCRM_CUST_MAST MAST ");
		sb.append(" WHERE MAST.CUST_ID = :custID ");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("custID", custId);
		List<Map<String, Object>> cList = dam.exeQuery(queryCondition);
		
		return CollectionUtils.isNotEmpty(cList) ? (BigDecimal) cList.get(0).get("AGE") : BigDecimal.ZERO;
	}
	
	/***
	 * A：高齡客戶資訊觀察表是否已過期
	 * getOldCust_Period
	 * @return 	true:有效	
	 * 			false:過期
	 * E：高齡客戶資訊觀察表有效日是否為適配當日	
	 * 			true:是當日
	 * 			false:非當日
	 * @throws Exception 
	 */
	private boolean validSeniorCustPeriod(String custID, String source) throws Exception {
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		//傳入參數
		GenericMap inputGmap = new GenericMap();
		inputGmap.put("CUST_ID", custID);
		inputGmap.put("SOURCE_METHOD", source);
		
		//取得客戶評估量表效期與是否有效 Y:有效 N:過期
		Map<String, Object> result = sendSeniorEvalAPI("getOldCust_Period", inputGmap);
		String validYN = ObjectUtils.toString(result.get("VALID_YN"));
		Date validDate = (StringUtils.isEmpty(ObjectUtils.toString(result.get("VALID_DATE")))) ? null : ft.parse((String) result.get("VALID_DATE"));
		this.setSeniorCustValidDate(validDate);//有效日
		
		//適配日 = 有效日 - 365天
		if(validDate != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(validDate);//設置起時間
			cal.add(Calendar.DATE, -365);
			this.setSeniorCustMatchDate(cal.getTime());
		}
		
		return "Y".equals(validYN) ? true : false;
	}
	
	/***
	 * B：高齡客戶資訊觀察表是否有填答不可申購的選項(金融認知中第二或第三個選項)
	 * getOldCust_02F_NoBuy
	 * @return	true：是，有填答 (不可申購)
	 * 			false：否，沒有填答(可申購)
	 * @throws Exception
	 */
	public boolean validSeniorCust02FNoBuy(String custID, String source) throws Exception {
		//傳入參數
		GenericMap inputGmap = new GenericMap();
		inputGmap.put("CUST_ID", custID);
		inputGmap.put("SOURCE_METHOD", source);
		
		//金融認知是否填答2或3項	Y：是(不可申購)/N：否(可申購)
		Map<String, Object> result = sendSeniorEvalAPI("getOldCust_02F_NoBuy", inputGmap);		
		String item23 = ObjectUtils.toString(result.get("FINACIAL_COGNITION_YN"));
				
		return "Y".equals(item23) ? true : false;
	}
	
	/***
	 * C：高齡客戶資訊觀察表取得能力表現是否填答需主管確認方可申購
	 * getOldCust_01B_ChkBuy
	 * 能力表現是否填答1~7項
	 * @return	true：是(需主管確認)
	 * 			false：否(可申購)
	 * 設定setSeniorCust01ANoBuyDTL：選項明細(各項選項串成明細，以；連接)
	 * @throws Exception
	 */
	public boolean validSeniorCust01BChkBuy(String custID, String source) throws Exception {
		//傳入參數
		GenericMap inputGmap = new GenericMap();
		inputGmap.put("CUST_ID", custID);
		inputGmap.put("SOURCE_METHOD", source);
		
		//能力表現是否填答1~7項 Y：是(需主管確認)/N：否(可申購)
		Map<String, Object> result = sendSeniorEvalAPI("getOldCust_01B_ChkBuy", inputGmap);
		String needAuth = ObjectUtils.toString(result.get("ABILITY_YN"));
		this.setSeniorCust01ANoBuyDTL(ObjectUtils.toString(result.get("ABILITY_DTL")));
		
		return "Y".equals(needAuth) ? true : false;
	}
	
	/***
	 * D：高齡客戶資訊觀察表是否有填答不建議申購之選項(健康情況第二項及第四項)
	 * getOldCust_01A_NoBuy
	 * @return	true：是(不建議申購)
	 * 			false：否(可申購)
	 * @throws Exception
	 */
	private boolean validSeniorCust01ANoBuy(String custID, String source) throws Exception {
		//傳入參數
		GenericMap inputGmap = new GenericMap();
		inputGmap.put("CUST_ID", custID);
		inputGmap.put("SOURCE_METHOD", source);
		
		//健康情況是否填答2或4項 Y：是(不建議申購)/N：否(可申購)
		Map<String, Object> result = sendSeniorEvalAPI("getOldCust_01A_NoBuy", inputGmap);
		String healthy = ObjectUtils.toString(result.get("HEALTHY_YN"));
		
		return "Y".equals(healthy) ? true : false;
	}
	
	/***
	 * E：高齡客戶資訊觀察表填寫日是否為適配當日
	 * @return 	true:是當日	
	 * 			false:非當日
	 * @throws Exception 
	 */
	private boolean validSeniorCustEvalDate(String custID, String source) throws Exception {
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		String nowDate = ft.format(new Date());
		
		if(this.getSeniorCustValidDate() == null) { //取得有效日
			this.validSeniorCustPeriod(custID, source);
		}	

		//E：高齡客戶資訊觀察表填寫日是否為適配當日 true:是當日	false:非當日
		//適配日 = 有效日 - 365天
		Calendar cal = Calendar.getInstance();
	    cal.setTime(this.getSeniorCustValidDate());//設置起時間
	    cal.add(Calendar.DATE, -365);
	    String matchDate = ft.format(cal.getTime());
	    
	    return StringUtils.equals(matchDate, nowDate) ? true : false;
	}
	
	/***
	 * F：金融認知是否填答第一選項：無法自行透過報章雜誌或網路查詢，取得金融資訊
	 * @param custID
	 * @param source
	 * @return	true：是(需主管確認)
	 * 			false：否(可申購)
	 * @throws Exception
	 */
	public boolean validSeniorCust01FChkBuy(String custID, String source) throws Exception {
		//傳入參數
		GenericMap inputGmap = new GenericMap();
		inputGmap.put("CUST_ID", custID);
		inputGmap.put("SOURCE_METHOD", source);
		
		//金融認知是否填答第一選項 Y：是(需主管確認)/N：否(可申購)
		Map<String, Object> result = sendSeniorEvalAPI("getOldCust_01F_ChkBuy", inputGmap);
		String needAuth = ObjectUtils.toString(result.get("FINACIAL_COGNITION_YN"));
		this.setSeniorCust01FChkBuyDTL(ObjectUtils.toString(result.get("FINACIAL_COGNITION_DTL")));
		
		return "Y".equals(needAuth) ? true : false;
	}
	
	/***
	 * 金融認知結果=4沒有上述情形
	 * getOldCust_02F_NoBuy
	 * @return true:沒有上述情形 false:認知有問題
	 * @throws Exception
	 */
	public boolean validSeniorCust02FAns4(String custID, String source) throws Exception {
		//傳入參數
		GenericMap inputGmap = new GenericMap();
		inputGmap.put("CUST_ID", custID);
		inputGmap.put("SOURCE_METHOD", source);
		
		//金融認知選項明細
		Map<String, Object> result = sendSeniorEvalAPI("getOldCust_02F_NoBuy", inputGmap);
		String items = ObjectUtils.toString(result.get("FINACIAL_COGNITION_DTL"));
		
		return StringUtils.isEmpty(items) ? false : (StringUtils.equals("4", items.substring(0, 1)) ? true : false);
	}
	
	/***
	 * 要保人、被保人、繳款人承作高齡評估量表是否在要保書申請日-3個工作日~要保書申請日之前
	 * 被保人、繳款人若為非本行客戶，則通過檢核點
	 * chkINS_Fit2
	 * @param inputVO
	 * @return true:通過檢核 false:未通過檢核
	 * @throws Exception
	 */
	private boolean validSeniorCustInsFit(SeniorValidationInputVO inputVO) throws Exception {
		SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
		
		//傳入參數
		GenericMap inputGmap = new GenericMap();
		inputGmap.put("CUST_ID", inputVO.getCustID().toUpperCase());
		inputGmap.put("INSURED_ID", inputVO.getInsuredID().toUpperCase());
		inputGmap.put("PAYER_ID", inputVO.getPayerID().toUpperCase());
		inputGmap.put("APPLY_DATE_S", ft.format(inputVO.getIOTApplyDate3()));
		inputGmap.put("APPLY_DATE_E", ft.format(inputVO.getIOTApplyDate()));
				
		//要保人、被保人、繳款人承作高齡評估量表是否在要保書申請日-3個工作日~要保書申請日之前
		Map<String, Object> result = sendSeniorEvalAPI("chkINS_Fit2", inputGmap);
		String custYN = ObjectUtils.toString(result.get("CUST_YN"));
		String insuredYN = ObjectUtils.toString(result.get("INSURED_YN"));
		String payerYN = ObjectUtils.toString(result.get("PAYER_YN"));
		
		return StringUtils.equals("Y", custYN) && StringUtils.equals("Y", insuredYN) && StringUtils.equals("Y", payerYN) ? true : false;
	}
	
	/***
	 * 前一次填答紀錄：金融認知是否填答第一選項：無法自行透過報章雜誌或網路查詢，取得金融資訊
	 * @param custID
	 * @param source
	 * @return	true：是(需主管確認)
	 * 			false：否(可申購) //若沒有前一次填答紀錄當作"可申購"
	 * @throws Exception
	 */
	private boolean validSeniorCust01FChkBuyLastAns(String custID, String source) throws Exception {
		//傳入參數
		GenericMap inputGmap = new GenericMap();
		inputGmap.put("CUST_ID", custID);
		inputGmap.put("SOURCE_METHOD", source);
		
		//前一次填答紀錄：金融認知是否填答第一選項 Y：是(需主管確認)/N：否(可申購)
		Map<String, Object> result = sendSeniorEvalAPI("getOldCust_01F_ChkBuy_LastAns", inputGmap);
		String needAuth = ObjectUtils.toString(result.get("FINACIAL_COGNITION_YN"));
		this.setSeniorCust01FChkBuyDTL(ObjectUtils.toString(result.get("FINACIAL_COGNITION_DTL")));
		
		return "Y".equals(needAuth) ? true : false;
	}
	
	/***
	 * 前一次填答紀錄：高齡客戶資訊觀察表是否有填答不可申購的選項(金融認知中第二或第三個選項)
	 * getOldCust_02F_NoBuy_LastAns
	 * @return	true：是，有填答 (不可申購)
	 * 			false：否，沒有填答(可申購) //若沒有前一次填答紀錄當作"可申購"
	 * @throws Exception
	 */
	public boolean validSeniorCust02FNoBuyLastAns(String custID, String source) throws Exception {
		//傳入參數
		GenericMap inputGmap = new GenericMap();
		inputGmap.put("CUST_ID", custID);
		inputGmap.put("SOURCE_METHOD", source);
		
		//前一次填答紀錄：金融認知是否填答2或3項	Y：是(不可申購)/N：否(可申購)
		Map<String, Object> result = sendSeniorEvalAPI("getOldCust_02F_NoBuy_LastAns", inputGmap);		
		String item23 = ObjectUtils.toString(result.get("FINACIAL_COGNITION_YN"));
				
		return "Y".equals(item23) ? true : false;
	}
	
	/***
	 * 前一次填答紀錄：高齡客戶資訊觀察表取得能力表現是否填答需主管確認方可申購
	 * getOldCust_01B_ChkBuy_LastAns
	 * 能力表現是否填答1~7項
	 * @return	true：是(需主管確認)
	 * 			false：否(可申購) //若沒有前一次填答紀錄當作"可申購"
	 * 設定setSeniorCust01ANoBuyDTL：選項明細(各項選項串成明細，以；連接)
	 * @throws Exception
	 */
	public boolean validSeniorCust01BChkBuyLastAns(String custID, String source) throws Exception {
		//傳入參數
		GenericMap inputGmap = new GenericMap();
		inputGmap.put("CUST_ID", custID);
		inputGmap.put("SOURCE_METHOD", source);
		
		//前一次填答紀錄：能力表現是否填答1~7項 Y：是(需主管確認)/N：否(可申購)
		Map<String, Object> result = sendSeniorEvalAPI("getOldCust_01B_ChkBuy_LastAns", inputGmap);
		String needAuth = ObjectUtils.toString(result.get("ABILITY_YN"));
		this.setSeniorCust01ANoBuyDTL(ObjectUtils.toString(result.get("ABILITY_DTL")));
		
		return "Y".equals(needAuth) ? true : false;
	}
	
	/***
	 * 呼叫高齡評估量表資料API
	 * @param apiName
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> sendSeniorEvalAPI(String apiName, GenericMap inputVO) throws Exception {
		XmlInfo xmlInfo = new XmlInfo();
		Gson gson = JsonUtil.genDefaultGson();
		//取得API URL
		String url = (String) xmlInfo.getVariable("SYS.SENIOR_CITIZEN_URL", apiName, "F3");
		
//		DataFormat dfObj = new DataFormat();
//		
//		//呼叫API Log
//		logger.info(apiName + " url:" + url);
//		logger.info(apiName + " inputVO:" + dfObj.maskJson(gson.toJson(inputVO.getParamMap()), "CUST_?[IN][DO]"));
		
		//取得客戶評估量表API回傳資料
		Map<String, Object> apiResult = new SeniorCitizenClientRS().getMap(url, inputVO);
		
//		//API回傳Log
//		logger.info(apiName + " return:" + apiResult.toString());
		
		//將每次呼叫高齡API資料寫入LOG檔
		try {
			IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
			String txnId = this.getClass().getSimpleName() + "." + new Object() {}.getClass().getEnclosingMethod().getName();
			iot920.writeWebServiceLog(txnId, url, "", gson.toJson(inputVO.getParamMap()), apiResult.toString());
		} catch(Exception e) {}
		
		//若API有回錯誤訊息
		if(StringUtils.isNotBlank(ObjectUtils.toString(apiResult.get("EMSGTXT")))) {
			 throw new JBranchException(ObjectUtils.toString(apiResult.get("EMSGTXT")));
		}
		
		return apiResult;
	}
}
