package com.systex.jbranch.fubon.webservice.rs;

import com.systex.jbranch.app.common.fps.table.TBKYC_LOG;
import com.systex.jbranch.app.server.fps.kycoperation.KycException;
import com.systex.jbranch.app.server.fps.kycoperation.KycOperationDao;
import com.systex.jbranch.app.server.fps.kycoperation.KycOperationJava;
import com.systex.jbranch.app.server.fps.kycoperation.KycOperationJavaInputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import java.util.LinkedList;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.serial.SerialClob;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Path("/Kyc")
@SuppressWarnings({"rawtypes" , "unchecked"})
public class KycOperation {
	public static final Logger logger = LoggerFactory.getLogger(KycOperation.class);
	public static final String USER_ID = "999999"; // 預設網銀 999999


    private KycOperationDao kycDao;
    private KycOperationJava kycJava;

	@GET
	@Path("/KycOperation")
	@Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
	public String testForm(
	    @QueryParam("branch") String branch,
		@QueryParam("userID") String userID,
		@QueryParam("custID") String custID,
		@QueryParam("examID") String examID,
		@QueryParam("generalQuestion") String generalQuestion,
		@QueryParam("generalAns") String generalAns,
		@QueryParam("confirm") String confirm,
		@QueryParam("method") String method,
		@QueryParam("IP") String ip) throws Exception {
		// 配置必要組件
		configure();

//		String branch = "999";
//		String userID = "999999";
//		String custID = "A120104233";
//		String examID = "KYC202004153164";
//		String generalQuestion = "201912190006^201912190007^201912180099^201912190008^201912190009^201912190010^201912190011^201912190012^201912190013^201912190014^201912190015^201912190016";
//		String generalAns = "51.94.55.94.4953.45.5053.45.5153.45.5253.45.5353.45.5453.94.51.94.49.94.52.94.49.94.53.94.53.94.52.94.53.94.53";
//		String confirm = "0";
//		String ip = "";
//		String method = "recordRisk";
		
		TBKYC_LOG tbkycLog = new TBKYC_LOG();
		tbkycLog.setBranch(ObjectUtils.toString(branch));
		tbkycLog.setUserid(StringUtils.defaultIfEmpty(userID, USER_ID));
		tbkycLog.setCustId(ObjectUtils.toString(custID));
		tbkycLog.setExamid(ObjectUtils.toString(examID));
		tbkycLog.setQuestion(ObjectUtils.toString(generalQuestion));
		tbkycLog.setAnswerTwo(ObjectUtils.toString(generalAns));
		tbkycLog.setConfirm(ObjectUtils.toString(confirm));
		tbkycLog.setMethod(ObjectUtils.toString(method));
		tbkycLog.setIpAddress(ObjectUtils.toString(ip));

		tbkycLog = writeLog(tbkycLog);

        String result = execute(tbkycLog);

		updateWriteLog(tbkycLog);

		return result;
    }

	private void configure() throws JBranchException {
		kycDao 	= getBean("KycOperationDao");
		kycJava = getBean("kycoperation");
	}

	private String execute(TBKYC_LOG tbkycLog) throws Exception {
		String resultContent;
		try {
			checkRequiredParameters(new GenericMap()
					.put("method", tbkycLog.getMethod())
					.getParamMap());

			switch (tbkycLog.getMethod()) {
				case "getKycCounts":
					resultContent = getKycCounts(tbkycLog);
					break;
				case "chkCoolingPeriod":
					resultContent = chkCoolingPeriod(tbkycLog);
					break;
				case "recordRisk":
					resultContent = recordRisk(tbkycLog);
					break;
				default:
					throw new APException("無指定服務可執行：" + tbkycLog.getMethod());
			}

			//將狀態寫為「F」（Finish：成功）
			tbkycLog.setStatus("F");

		} catch(Exception ex) {
			// 將狀態寫為「E」（Error：失敗）
			tbkycLog.setStatus("E");

			logger.error("Error [" + tbkycLog.getCustId() + "]" + StringUtil.getStackTraceAsString(ex));
			ex.printStackTrace();

			resultContent = errorXmlContent(ex);
		}
		tbkycLog.setStatusUpdate(new Timestamp(new Date().getTime()));

		logger.info("回傳網行銀KycOperation XML：" + resultContent);

		//將回傳值寫入LOG
		tbkycLog.setReturnXml(new SerialClob(resultContent.toCharArray()));

		return resultContent;
	}

	/** 取得客戶投資風險屬性（提供試算功能） **/
	private String recordRisk(TBKYC_LOG tbkycLog) throws Exception {
		checkRequiredParameters(new GenericMap()
				.put("branch", tbkycLog.getBranch())
				.put("userID", tbkycLog.getUserid())
				.put("custID", tbkycLog.getCustId())
				.put("examID", tbkycLog.getExamid())
				.put("generalQuestion", tbkycLog.getQuestion())
				.put("generalAns", tbkycLog.getAnswerTwo())
				.getParamMap()
		);

		checkCountsQandA(tbkycLog);

		KycOperationJavaInputVO kycin = new KycOperationJavaInputVO();
		kycin.setBranch(tbkycLog.getBranch());
		kycin.setUserID(tbkycLog.getUserid());
		kycin.setCustID(tbkycLog.getCustId());
		kycin.setExamID(tbkycLog.getExamid());
		kycin.setIP(tbkycLog.getIpAddress());
		kycin.setGeneralQuestion(tbkycLog.getQuestion());
		kycin.setGeneralAns(tbkycLog.getAnswerTwo());

		Map<String, Object> kyc_ans;
		//執行 kyc 問卷
		if(StringUtils.equals(tbkycLog.getConfirm(), "0"))	// 試算，不存 DB & 不上送電文
			kyc_ans = kycJava.getKycValueCal(kycin , tbkycLog);
		else {
			if (kycDao.recordRiskIsDuplicatedOperation(tbkycLog.getCustId()))
				throwRecordRiskIsPendingException(tbkycLog.getCustId());

			if (kycDao.isOverLimitationToRiskRecord(tbkycLog.getCustId()))
				throwOverLimitationToRiskRecordException();

			kyc_ans = kycJava.getKycValue(kycin , tbkycLog);
		}

		return successXmlContent(kyc_ans);
	}

	/** 題目與答案個數應一致 **/
	private void checkCountsQandA(TBKYC_LOG tbkycLog) throws KycException, JBranchException {
		if (tbkycLog.getQuestion().split(";").length ==
			tbkycLog.getAnswerTwo().split(";").length) return;
		throwsParamsLackingException(Arrays.asList(new String[]{"generalQuestion", "generalAns"}));
	}

	/** 檢查客戶是否在冷靜期中，若回傳空值表示客戶不在冷靜期中 **/
	private String chkCoolingPeriod(TBKYC_LOG tbkycLog) throws JBranchException, KycException {
		checkRequiredParameters(new GenericMap()
				.put("custID", tbkycLog.getCustId())
				.getParamMap()
		);
		List<Map<String, Object>> cList = kycDao.chkCoolingPeriod(tbkycLog.getCustId());
		return chkCoolingPeriodXml(CollectionUtils.isNotEmpty(cList) ? ObjectUtils.toString(cList.get(0).get("EFFECTIVE_DATE")) : "");
	}

	/** 取得本日已做網行銀KYC次數與包含承作日之最近 7 個日曆日內 kyc 次數，並回傳該客戶 recordRisk 目前的執行狀態 **/
	private String getKycCounts(TBKYC_LOG tbkycLog) throws JBranchException, KycException {
		checkRequiredParameters(new GenericMap()
				.put("custID", tbkycLog.getCustId())
				.getParamMap()
		);

		return getKycCountsXml(
				kycDao.getKycCounts(tbkycLog.getCustId()),
				kycDao.getKycWeekCounts(tbkycLog.getCustId()),
				kycDao.recordRiskIsPending(tbkycLog.getCustId()),
				kycDao.inReviewStatus(tbkycLog.getCustId()));
	}

	private void checkRequiredParameters(Map<String, String> map) throws KycException, JBranchException {
		List paramNames = new LinkedList();
		for (Map.Entry<String, String> param: map.entrySet()) {
			if (StringUtils.isBlank(param.getValue())) {
				paramNames.add(param.getKey());
			}
		}

		if (paramNames.isEmpty()) return;
		throwsParamsLackingException(paramNames);
	}

	private void throwsParamsLackingException(List paramsNames) throws KycException, JBranchException {
		Map<String, String> map = kycDao.getKycExceptionMap("KYCNG16");
		throw new KycException(map.get("PARAM_CODE"),
				map.get("PARAM_NAME") + StringUtils.join(paramsNames, ","));
	}

	private void throwRecordRiskIsPendingException(String custId) throws KycException, JBranchException {
		Map<String, String> map = kycDao.getKycExceptionMap("KYCNG19");
		throw new KycException(map.get("PARAM_CODE"), custId + " " + map.get("PARAM_NAME"));
	}

	private void throwOverLimitationToRiskRecordException() throws KycException, JBranchException {
		Map<String, String> map = kycDao.getKycExceptionMap("KYCNG20");
		throw new KycException(map.get("PARAM_CODE"), map.get("PARAM_NAME"));
	}

	public TBKYC_LOG writeLog(TBKYC_LOG tbkycLog) throws JBranchException {
		StringBuffer stringMsg = new StringBuffer();

		try{
			logger.info(stringMsg
			   .append("KycOperation=> ")
			   .append("custId:").append(tbkycLog.getCustId()).append(" ,")
			   .append("branch:").append(tbkycLog.getBranch()).append(" ,")
			   .append("userID:").append(tbkycLog.getUserid()).append(" ,")
			   .append("examID:").append(tbkycLog.getExamid()).append(" ,")
			   .append("generalQuestion").append(tbkycLog.getQuestion()).append(" ,")
			   .append("generalAns").append(tbkycLog.getAnswerTwo()).append(" ,")
			   .append("IP").append(tbkycLog.getIpAddress()).append(" ,")
			   .append("confirm:").append(tbkycLog.getConfirm()).append(" ,")
			   .append("method:").append(tbkycLog.getMethod())
			   .toString());

			//題目
			tbkycLog.setQuestion(tbkycLog.getQuestion().replaceAll("\\^", ";"));
			//答案
			tbkycLog.setAnswerTwo(KycOperationJava.uncodeAnswer(tbkycLog.getAnswerTwo())
						.replaceAll("\\^" , ";")
						.replaceAll("-", ",")
						.replaceAll("\\[|\\]|\\s" , ""));
			//完整傳入內容
			tbkycLog.setInputData(new SerialClob(stringMsg.toString().toCharArray()));
			// 將狀態寫為「P」（Pending：作業中）
			tbkycLog.setStatus("P");
			tbkycLog.setStatusUpdate(new Timestamp(new Date().getTime()));
		}
		catch(Exception ex){
			logger.error("寫入log失敗：" + StringUtil.getStackTraceAsString(ex));
		}

		return kycDao.insertKycLog(tbkycLog);
	}

	public void updateWriteLog(TBKYC_LOG tbkycLog){
		try{
			kycDao.updateKycLog(tbkycLog);
		}
		catch(Exception ex){
			logger.error("寫入log失敗：" + StringUtil.getStackTraceAsString(ex));
		}
	}

    @SuppressWarnings({"unchecked" })
	public <T>T getBean(String key) throws JBranchException{
    	return (T)PlatformContext.getBean(key);
    }

    public String successXmlContent(Map<String, Object> kycMap){
    	String cName = ObjectUtils.toString(kycMap.get("CNAME"));
		String cData = ObjectUtils.toString(kycMap.get("CDATA"));
		String cDataUp = ObjectUtils.toString(kycMap.get("CDATA_UP"));
		String coolingRiskId = ObjectUtils.toString(kycMap.get("CoolingRiskID"));
		String coolingRiskName = ObjectUtils.toString(kycMap.get("CoolingRiskName"));
		String coolingEffDate = ObjectUtils.toString(kycMap.get("CoolingEffDate"));
		String expiry_date = ObjectUtils.toString(kycMap.get("EXPIRY_DATE"));

    	StringBuffer sb = new StringBuffer();
    	if(StringUtils.isNotBlank(cDataUp)) {	//試算
    		sb.append("<item>\n")
			  .append("<key xsi:type=\"soapenc:string\">popup</key>\n")
			  .append("<value xsi:type=\"soapenc:string\">" + cDataUp + "</value>\n")
			  .append("</item>\n");
    	} else {	//非試算，須加上冷靜期資料
    		sb.append("<item>\n")
			  .append("<key xsi:type=\"soapenc:string\">CoolingRiskName</key>\n")
			  .append("<value xsi:type=\"soapenc:string\">" + coolingRiskName + "</value>\n")
			  .append("</item>\n")
			  .append("<item>\n")
			  .append("<key xsi:type=\"soapenc:string\">CoolingRiskID</key>\n")
			  .append("<value xsi:type=\"soapenc:string\">" + coolingRiskId + "</value>\n")
			  .append("</item>\n")
			  .append("<item>\n")
			  .append("<key xsi:type=\"soapenc:string\">CoolingPeriod</key>\n")
			  .append("<value xsi:type=\"soapenc:string\">" + coolingEffDate + "</value>\n")
			  .append("</item>\n");
    	}

    	StringBuffer rtnSb = new StringBuffer();
    	rtnSb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
		.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n")
		.append("<soapenv:Body>\n")
		.append("<recordRiskResponse soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n")
		.append("<recordRiskReturn href=\"#id0\"/>\n")
		.append("</recordRiskResponse>\n")
		.append("<multiRef id=\"id0\" soapenc:root=\"0\" soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xsi:type=\"ns1:Map\"\n")
		.append("xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:ns1=\"http://xml.apache.org/xml-soap\">\n")
		.append("<item>\n")
		.append("<key xsi:type=\"soapenc:string\">riskName</key>\n")
		.append("<value xsi:type=\"soapenc:string\">" + cName + "</value>\n")
		.append("</item>\n")
		.append("<item>\n")
		.append("<key xsi:type=\"soapenc:string\">state</key>\n")
		.append("<value xsi:type=\"soapenc:string\">checked</value>\n")
		.append("</item>\n")
		.append("<item>\n")
		.append("<key xsi:type=\"soapenc:string\">riskID</key>\n")
		.append("<value xsi:type=\"soapenc:string\">" + cData + "</value>\n")
		.append("</item>\n")
    	.append("<item>\n")
    	.append("<key xsi:type=\"soapenc:string\">expiryDate</key>\n")
    	.append("<value xsi:type=\"soapenc:string\">" + expiry_date + "</value>\n")
    	.append("</item>\n");

    	if(StringUtils.isNotBlank(sb.toString()))
    		rtnSb.append(sb);

    	rtnSb.append("</multiRef>\n")
			 .append("</soapenv:Body>\n")
			 .append("</soapenv:Envelope>\n");

    	return rtnSb.toString();
    }

    public String errorXmlContent(Exception excepion) {
		KycException ex = transferToKycException(excepion);
		StringBuilder sb = new StringBuilder();
		sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n")
				.append("<soapenv:Body>\n")
				.append("<soapenv:Fault>\n")
				.append("<faultcode>" + ex.getCode() + "</faultcode>\n")
				.append("<faultstring>" + ex.getDesc() + "</faultstring>\n")
				.append("<detail>\n")
				.append("<ns1:hostname xmlns:ns1=\"http://xml.apache.org/axis/\">TPEBNKFPQ5</ns1:hostname>\n")
				.append("</detail>\n")
				.append("</soapenv:Fault>\n")
				.append("</soapenv:Body>\n")
				.append("</soapenv:Envelope>\n");
    	return sb.toString();
    }

	private KycException transferToKycException(Exception excepion) {
		if (excepion instanceof KycException)
			return (KycException) excepion;
		else {
			return new KycException("soapenv:Server.userException", excepion.getMessage());
		}
	}

	public String getKycCountsXml(BigDecimal kycCounts, BigDecimal kycWeekCounts, String isPending, String inReviewStatus){
    	return new StringBuffer()
			.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n")
			.append("<soapenv:Body>\n")
			.append("<getKycCountsResponse soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n")
			.append("<kycCounts>" + kycCounts +"</kycCounts>\n")
			.append("<kycWeekCounts>" + kycWeekCounts +"</kycWeekCounts>\n")
			.append("<isPending>" + isPending +"</isPending>\n")
			.append("<isInReviewStatus>" + inReviewStatus +"</isInReviewStatus>\n")
			.append("</getKycCountsResponse>\n")
			.append("</soapenv:Body>\n")
			.append("</soapenv:Envelope>\n").toString();
    }

    public String chkCoolingPeriodXml(String effDate){
    	return new StringBuffer()
			.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n")
			.append("<soapenv:Body>\n")
			.append("<chkCoolingPeriodResponse soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n")
			.append("<coolingPeriod>" + effDate +"</coolingPeriod>\n")
			.append("</chkCoolingPeriodResponse>\n")
			.append("</soapenv:Body>\n")
			.append("</soapenv:Envelope>\n").toString();
    }
}