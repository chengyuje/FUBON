package com.systex.jbranch.app.server.fps.cus110;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.util.TextUtils;
import com.systex.jbranch.app.common.fps.table.TBSYS_EMAIL_LOGVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.mail.FubonMail;
import com.systex.jbranch.platform.server.mail.FubonSendJavaMail;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * cus130
 * 
 * @author Joe
 * @date 2016/07/15
 * @spec null
 */
@Component("cus110")
@Scope("request")
public class CUS110 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CUS110.class);
	
	public void getConfirmData(Object body, IPrimitiveMap header) throws JBranchException {
		CUS110InputVO inputVO = (CUS110InputVO) body;
		CUS110OutputVO return_VO = new CUS110OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT * FROM TBSYS_EMAIL_LOG WHERE SEQ = :seq");
		queryCondition.setObject("seq", inputVO.getSeq());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		// queryCustData
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.CUST_ID||'  '||b.CUST_NAME AS CUST_DATA ,a.EMAIL ");
		sql.append("FROM TBCRM_CUST_CONTACT a, TBCRM_CUST_MAST b ");
		sql.append("WHERE a.CUST_ID IN (:cust_id) AND a.CUST_ID = b.CUST_ID ");
		queryCondition.setQueryString(sql.toString());
		String[] id = ObjectUtils.toString(list.get(0).get("RECIPIENT")).split(",");
		queryCondition.setObject("cust_id", id);
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		// getUploadFile
		if(list.get(0).get("ATTACHMENT") != null) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("SELECT DOC_ID, FILENAME FROM TBSYS_EMAIL_ATTACHMENT WHERE DOC_ID IN (:doc_id)");
			String[] id2 = ObjectUtils.toString(list.get(0).get("ATTACHMENT")).split(",");
			queryCondition.setObject("doc_id", id2);
			List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
			
			list.get(0).put("getUploadFile", list3);
		}
		list.get(0).put("queryCustData", list2);
		
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void queryCustData(Object body, IPrimitiveMap header) throws JBranchException {
		CUS110InputVO inputVO = (CUS110InputVO) body;
		CUS110OutputVO return_VO = new CUS110OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.CUST_ID||'  '||b.CUST_NAME AS CUST_DATA ,a.EMAIL ");
		sql.append("FROM TBCRM_CUST_CONTACT a, TBCRM_CUST_MAST b ");
		sql.append("WHERE a.CUST_ID IN (:cust_id) AND a.CUST_ID = b.CUST_ID ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("cust_id", inputVO.getCustID());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	//取得訊息下拉式選單內容
	public void queryContentList(Object body, IPrimitiveMap header) throws JBranchException {
		CUS110OutputVO return_VO = new CUS110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SEQ, CONTENT FROM TBSYS_EMAIL_CONTENT");
		queryCondition.setQueryString(sql.toString());
		// result
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	//取得附加檔案
	public void queryAddData(Object body, IPrimitiveMap header) throws JBranchException {
		CUS110OutputVO return_VO = new CUS110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.DOC_ID, a.FILENAME, a.CATEGORY_BY_CONTENT, a.CATEGORY_BY_PRODUCT, a.CREATETIME, b.DOC_FILE ");
		sql.append("FROM TBSYS_EMAIL_ATTACHMENT a, TBSYS_FILE_DETAIL b ");
		sql.append("WHERE a.DOC_ID = b.DOC_ID ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void ConfirmMessage(Object body, IPrimitiveMap header) throws Exception {
		CUS110InputVO inputVO = (CUS110InputVO) body;
		CUS110OutputVO return_VO = new CUS110OutputVO();
		FubonSendJavaMail sendMail = new FubonSendJavaMail();
		dam = this.getDataAccessManager();
		
		TBSYS_EMAIL_LOGVO vo = new TBSYS_EMAIL_LOGVO();
		vo = (TBSYS_EMAIL_LOGVO) dam.findByPKey(TBSYS_EMAIL_LOGVO.TABLE_UID, inputVO.getSeq());
		if (vo != null) {
			vo.setREVIEW_STATUS(inputVO.getStatus());
			dam.update(vo);
		}
		else
			throw new APException("ehl_01_common_001");
		
		// send mail copy old code
		if("Y".equals(inputVO.getStatus())) {
			FubonMail mail = new FubonMail();
			
			Map<String, Object> annexData = new HashMap<String, Object>();
			
			//設定收件者
			List<Map<String, String>> lstMailTo = new ArrayList<Map<String,String>>();
			String[] email = inputVO.getEmail().split(",");
			logger.info("CUS110 mail:" + email);
			
			Map<String, String> mailMap = new HashMap<String, String>();
			for(int i = 0 ; i < email.length ; i++) {	
				mailMap.put(FubonSendJavaMail.MAIL, email[i]);
				lstMailTo.add(mailMap);
			}
			
			//寄件方式-密件
			mail.setLstMailBcc(lstMailTo);
			
			//設定信件主旨
			if("1".equals(inputVO.getSubject()))
				mail.setSubject("來自台北富邦銀行  " + inputVO.getConfirmNAME() + "  的訊息");
			else if("2".equals(inputVO.getSubject()))
				mail.setSubject(inputVO.getSubjectTxt());
			
			//設定信件內容
			if("-1".equals(inputVO.getContentList()))
				mail.setContent(inputVO.getCenterTextarea());
			else {
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString("SELECT CONTENT FROM TBSYS_EMAIL_CONTENT WHERE SEQ = :seq");
				queryCondition.setObject("seq", inputVO.getContentList());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if(list.size() > 0)
					mail.setContent(ObjectUtils.toString(list.get(0).get("CONTENT")));
			}
			
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//			MimeBodyPart filePart = new MimeBodyPart();		
			List<Map<String, Object>> annexIdMap = inputVO.getAnnexID();
			List<String> dataName = new ArrayList<String>();
			List<byte[]> data = new ArrayList<byte[]>();
			
			//Email格式檢查
			String message = "信件已寄出";
			String mailAddressError = "N";
			
//			int emailLength = 0;
//			
//			for (int i = 0; i < email.length; i++) {
//				emailLength = i;
//			}
				
			for (int i = 0; i < email.length; i++) {
				if (isEmail(email[i]) == false) {
					message = "信箱Email格式錯誤: " + "錯誤信箱 - " + email[i] +"，所有郵件寄件失敗。";
					mailAddressError = "Y";
				} 
			}
			
			return_VO.setMessage(message);
			
			if(!mailAddressError.equals("Y")) {
				//夾帶附件-取得寄送附件
				if (annexIdMap.size() > 0) {
					StringBuilder getAnnexId = new StringBuilder();
					for(Map<String, Object> map : annexIdMap) {
						getAnnexId.append(" '" + map.get("ID").toString() + "',");
					}
					String sql = "SELECT a.DOC_NAME,b.DOC_FILE "
							     + "FROM TBSYS_FILE_MAIN a,TBSYS_FILE_DETAIL b "
							     + "WHERE b.DOC_ID IN (" 
							     + getAnnexId.deleteCharAt( getAnnexId.length() -1 ).toString() + ") "
							     + "AND a.DOC_ID = b.DOC_ID ";
					StringBuffer sb = new StringBuffer();
					sb.append(sql);
					queryCondition.setQueryString(sb.toString());
					//Blob 轉  byte[]
					List<Map<String, Object>> annexDataList = dam.executeQuery(queryCondition);
					for (int i = 0; i < annexDataList.size(); i++) {
						Blob file = (Blob)annexDataList.get(i).get("DOC_FILE");
						int fileLength = (int)file.length();
						byte[] onversion = file.getBytes(1, fileLength);
						file.free();
						dataName.add((String) annexDataList.get(i).get("DOC_NAME"));
						data.add(onversion);
					}
					
					for (int i = 0; i < dataName.size(); i++) {
						annexData.put(dataName.get(i), data.get(i));
					}
					//寄出信件-有附件
					sendMail.sendMail(mail,annexData);
				} else {
					//寄出信件-無附件
					sendMail.sendMail(mail,annexData);				
				}
			}
		}
		
		this.sendRtnObject(return_VO);
	}
	
	public void sendMail(Object body, IPrimitiveMap header) throws Exception {
		CUS110InputVO inputVO = (CUS110InputVO) body;
		CUS110OutputVO return_VO = new CUS110OutputVO();
		FubonSendJavaMail sendMail = new FubonSendJavaMail();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT SQ_TBSYS_EMAIL_LOG.nextval AS SEQ FROM DUAL");
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// 2017/4/28 add all need insert TBSYS_EMAIL_LOG
		TBSYS_EMAIL_LOGVO vo = new TBSYS_EMAIL_LOGVO();
		vo.setSEQ(seqNo);
		vo.setEMP_ID(ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINID)));
		vo.setBRA_NBR(ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINBRH)));
		vo.setRECIPIENT(TextUtils.join(",", inputVO.getCustID()));
		// 2017/5/25
		if("1".equals(inputVO.getSubject())) {
			vo.setSUBJECT(new BigDecimal(inputVO.getSubject()));
			vo.setSUBJECT_CUS("來自台北富邦銀行  " + SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINNAME) + "  的訊息");
		}
		else if("2".equals(inputVO.getSubject())) {
			vo.setSUBJECT(new BigDecimal(inputVO.getSubject()));
			vo.setSUBJECT_CUS(inputVO.getSubjectTxt());
		}
		// ao no give subject all 2 
		else {
			vo.setSUBJECT(new BigDecimal(2));
			vo.setSUBJECT_CUS(inputVO.getSubjectTxt());
		}
		if("-1".equals(inputVO.getContentList())) {
			vo.setCONTENT(new BigDecimal(inputVO.getContentList()));
			vo.setCONTENT_CUS(inputVO.getCenterTextarea());
		}
		else {
			if(StringUtils.isNotBlank(inputVO.getContentList())) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString("SELECT CONTENT FROM TBSYS_EMAIL_CONTENT WHERE SEQ = :seq");
				queryCondition.setObject("seq", inputVO.getContentList());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if(list.size() > 0)
					vo.setSUBJECT_CUS(ObjectUtils.toString(list.get(0).get("CONTENT")));
				vo.setCONTENT(new BigDecimal(inputVO.getContentList()));
			}
			// ao no give subject
			else {
				vo.setCONTENT(new BigDecimal(-1));
				vo.setCONTENT_CUS(inputVO.getCenterTextarea());
			}
		}
		if(inputVO.getAnnexID() != null)
			vo.setATTACHMENT(joinListByCom(inputVO.getAnnexID(), ","));
		if("2".equals(inputVO.getSubject()) || "-1".equals(inputVO.getContentList()))
			vo.setREVIEW_STATUS("W");
		else
			vo.setREVIEW_STATUS("Y");
		dam.create(vo);
		
		// old code 沒動
		//SMTP Server 設定於 fubon.wms\src\webapp\WEB-INF\bean\mail.bean.xml
		FubonMail mail = new FubonMail();
		
		Map<String, Object> annexData = new HashMap<String, Object>();
		//內部信件-無寄件畫面與附件
		if ("AO".equals(inputVO.getRecipientType())) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("SELECT EMP_EMAIL_ADDRESS AS EMAIL FROM TBORG_MEMBER WHERE EMP_ID IN (:emp_id)");
			queryCondition.setObject("emp_id", inputVO.getCustID());
			List<Map<String, Object>> mailList = dam.exeQuery(queryCondition);
			
			String emails = joinListByCom(mailList, ",");
			String[] email = emails.split(",");
			
			//Email格式檢查
			String message = "信件已寄出";
			String mailAddressError = "N";
							
			for (int i = 0; i < email.length; i++) {
				if (isEmail(email[i]) == false) {
					message = "信箱Email格式錯誤: " + "錯誤信箱 - " + email[i] +"，所有郵件寄件失敗。";
					mailAddressError = "Y";
				} 
			}

			return_VO.setMessage(message);
			
			if(!mailAddressError.equals("Y")) {
				
				List<Map<String, String>> lstMailTo = new ArrayList<Map<String,String>>();
				
				for(int i = 0 ; i < email.length ; i++) {	
					Map<String, String> mailMap = new HashMap<String, String>();
					mailMap.put(FubonSendJavaMail.MAIL, email[i]);
					lstMailTo.add(mailMap);
				}
				
				//寄件方式-一般
	     		mail.setLstMailTo(lstMailTo);	
				//設定信件主旨
				mail.setSubject(inputVO.getSubjectTxt());
				//設定信件內容
				mail.setContent(inputVO.getCenterTextarea());
				//寄出信件-無附件
				sendMail.sendMail(mail,annexData);
			} 
		}
		
		// review y send
		if("2".equals(inputVO.getSubject()) || "-1".equals(inputVO.getContentList())) {
			
		}
		else {
			// old code
			// 外部信件
			if("CUST".equals(inputVO.getRecipientType())) {
				//設定收件者
				List<Map<String, String>> lstMailTo = new ArrayList<Map<String,String>>();
				String[] email = inputVO.getEmail().split(",");
				
				Map<String, String> mailMap = new HashMap<String, String>();
				for(int i = 0 ; i < email.length ; i++) {	
					mailMap.put(FubonSendJavaMail.MAIL, email[i]);
					lstMailTo.add(mailMap);
				}
				
				//寄件方式-密件
				mail.setLstMailBcc(lstMailTo);
				
				//設定信件主旨
				if("1".equals(inputVO.getSubject()))
					mail.setSubject("來自台北富邦銀行  " + SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINNAME) + "  的訊息");
				else if("2".equals(inputVO.getSubject()))
					mail.setSubject(inputVO.getSubjectTxt());
				
				//設定信件內容
				if("-1".equals(inputVO.getContentList()))
					mail.setContent(inputVO.getCenterTextarea());
				else {
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					queryCondition.setQueryString("SELECT CONTENT FROM TBSYS_EMAIL_CONTENT WHERE SEQ = :seq");
					queryCondition.setObject("seq", inputVO.getContentList());
					List<Map<String, Object>> list = dam.exeQuery(queryCondition);
					if(list.size() > 0)
						mail.setContent(ObjectUtils.toString(list.get(0).get("CONTENT")));
				}
				
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//				MimeBodyPart filePart = new MimeBodyPart();		
				List<Map<String, Object>> annexIdMap = inputVO.getAnnexID();
				List<String> dataName = new ArrayList<String>();
				List<byte[]> data = new ArrayList<byte[]>();
				
				//Email格式檢查
				String message = "信件已寄出";
				String mailAddressError = "N";
				
//				int emailLength = 0;
//				
//				for (int i = 0; i < email.length; i++) {
//					emailLength = i;
//				}
					
				for (int i = 0; i < email.length; i++) {
					if (isEmail(email[i]) == false) {
						message = "信箱Email格式錯誤: " + "錯誤信箱 - " + email[i] +"，所有郵件寄件失敗。";
						mailAddressError = "Y";
					} 
				}
				
				return_VO.setMessage(message);
				
				if(!mailAddressError.equals("Y")) {
					//夾帶附件-取得寄送附件
					if (annexIdMap.size() > 0) {
						StringBuilder getAnnexId = new StringBuilder();
						for(Map<String, Object> map : annexIdMap) {
							getAnnexId.append(" '" + map.get("ID").toString() + "',");
						}
						String sql = "SELECT a.DOC_NAME,b.DOC_FILE "
								     + "FROM TBSYS_FILE_MAIN a,TBSYS_FILE_DETAIL b "
								     + "WHERE b.DOC_ID IN (" 
								     + getAnnexId.deleteCharAt( getAnnexId.length() -1 ).toString() + ") "
								     + "AND a.DOC_ID = b.DOC_ID ";
						StringBuffer sb = new StringBuffer();
						sb.append(sql);
						queryCondition.setQueryString(sb.toString());
						//Blob 轉  byte[]
						List<Map<String, Object>> annexDataList = dam.executeQuery(queryCondition);
						for (int i = 0; i < annexDataList.size(); i++) {
							Blob file = (Blob)annexDataList.get(i).get("DOC_FILE");
							int fileLength = (int)file.length();
							byte[] onversion = file.getBytes(1, fileLength);
							file.free();
							dataName.add((String) annexDataList.get(i).get("DOC_NAME"));
							data.add(onversion);
						}
						
						for (int i = 0; i < dataName.size(); i++) {
							annexData.put(dataName.get(i), data.get(i));
						}
						//寄出信件-有附件
						sendMail.sendMail(mail,annexData);
					} else {
						//寄出信件-無附件
						sendMail.sendMail(mail,annexData);				
					}
				}
			}
		}
		
		this.sendRtnObject(return_VO);
	}
	
	//信箱Email格式檢查
	public static boolean isEmail(String email) {
		Pattern emailPattern = Pattern
				.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher matcher = emailPattern.matcher(email);
		if (matcher.find()) {
			return true;
		}
		return false;
	}
	
}