package com.systex.jbranch.platform.server.mail;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;

public class FubonSendJavaMail {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	public static String MAIL = "MAIL";
	
	public void sendMail(FubonMail fubonMail,Map<String, Object> annexData) throws Exception {
		// create some properties and get the default Session
		DataAccessManager dam = PlatformContext.getBean(DataAccessManager.class);
		QueryConditionIF qc = dam.getQueryCondition(dam.QUERY_LANGUAGE_TYPE_VAR_SQL);
		qc.setQueryString("SELECT PARAM_CODE, PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SYS.MAIL' order by PARAM_ORDER");
		logger.info("Mail : 讀取SYS.MAIL設定");
		List<Map<String, String>> result = dam.exeQuery(qc);
		if (result == null || result.size() <=0 ) {
			throw new JBranchException("沒有SYS.MAIL的設定");
		}
		
		Properties objProps = new Properties();
		Properties fromMail = new Properties();
		
		String userName = null;
		String pwd = null;
		String protocol = "smtp";
		for (Map<String, String> obj : result) {
			String paramCode = obj.get("PARAM_CODE");
			if ("username".equals(paramCode)) {
				userName = obj.get("PARAM_NAME");
			} else if ("password".equals(paramCode)) {
				pwd = obj.get("PARAM_NAME");
			} else if ("protocol".equals(paramCode)) {
				protocol = obj.get("PARAM_NAME");
			} else if (paramCode.startsWith("from")) { // .matches("^[A-Z]+\\d+$")) {
				fromMail.put(obj.get("PARAM_CODE"), obj.get("PARAM_NAME"));
			} else {
				objProps.put(obj.get("PARAM_CODE"), obj.get("PARAM_NAME"));
			}
//			logger.info("MAIL.DB:{} = {}", obj.get("PARAM_CODE"), obj.get("PARAM_NAME"));
		}
		
		final String fUserName = userName;
		final String fPwd = pwd;
		Session objSession = Session.getInstance(objProps, new Authenticator() {  
            protected PasswordAuthentication getPasswordAuthentication() {  
             	return new PasswordAuthentication(fUserName, fPwd);
            }  
        });  
		objSession.setDebug(isTestMode()); // 設定是否是debug模式(如果是會輸出送Mail的相關資訊)

		try {
			// create a message
			MimeMessage mail = new MimeMessage(objSession);
			String[] fromDefault = fromMail.getProperty("from", "wmsr_bank@fubon.com,台北富邦銀行").split(",");
			String[] fromData = fromDefault;
			if (fubonMail != null) {
				if ( fubonMail.getSender() != null) {
					fromData = fromMail.getProperty(fubonMail.getSender() , fromMail.getProperty("from", "wmsr_bank@fubon.com,台北富邦銀行")).split(",");
					logger.info("DONE");
				} else {
					// 指定寄件者為理專
					if ( null != fubonMail.getFromMail()) fromData[0] = fubonMail.getFromMail();
					if ( null != fubonMail.getFromName()) fromData[1] = fubonMail.getFromName();
					logger.info("CALL BY FUBON MAIL");
				}
			}
			if (fromData == null) {
				fromData = fromDefault;
			} else if (fromData.length == 1) {
				Pattern emailPattern = Pattern
						.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
				Matcher matcher = emailPattern.matcher(fromData[0]);
				if (matcher.find()) {
					fromData = new String[]{fromData[0], fromDefault[1]};
					logger.info("mailSen:" + fromData[0] + fromDefault[1]);
				} else {
					fromData = new String[]{fromDefault[0], fromData[0]};
					logger.info("mailSen:" + fromDefault[0] + fromData[0]);
				}
			} 
			mail.setFrom(new InternetAddress(fromData[0], fromData[1]));
			
			// mail.setFrom(new InternetAddress(fubonMail.getFromMail()));

			// create 多收件者
			mail.setRecipients(Message.RecipientType.TO, getMailToArray(fubonMail.getLstMailTo()));
			mail.setRecipients(Message.RecipientType.CC, getMailToArray(fubonMail.getLstMailCc()));
			mail.setRecipients(Message.RecipientType.BCC, getMailToArray(fubonMail.getLstMailBcc()));
			mail.setSubject(fubonMail.getSubject(), fubonMail.getEncoding());

			// create and fill the first message part
			MimeBodyPart content = new MimeBodyPart();
			content.setContent(fubonMail.getContent(), fubonMail.getContentType() + "; charset=" + fubonMail.getEncoding());

			// create the Multipart and add its parts to it
			Multipart body = new MimeMultipart();
			body.addBodyPart(content);

			// create the second message part
			MimeBodyPart attach = null;

			// attach the file to the message			
			if (annexData.size() > 0) {
				Iterator<?> iterator = annexData.entrySet().iterator();
				while (iterator.hasNext()) {
					   Map.Entry mapEntry = (Map.Entry) iterator.next();
					   
						content = new MimeBodyPart();
						content.setText(fubonMail.getContent(), fubonMail.getEncoding());
						body.addBodyPart(content);
						DataSource fileds = new ByteArrayDataSource(((byte[])mapEntry.getValue()),"application/octet-stream");
						DataHandler dataHandler = new DataHandler(fileds);
						content.setDataHandler(dataHandler);
						content.setFileName(mapEntry.getKey().toString());		   
				}
			}

			// add the Multipart to the message
			mail.setContent(body);

			// set the Date: header
			mail.setSentDate(new Date());
			mail.saveChanges();
			
//			logger.info(
//					new StringBuffer()
//						.append("--------mail info--------/r/n ")
//						.append("--from : {} \r\n ")
//						.append("--to : {} \r\n ")
//						.append("--subject : {} \r\n")
//						.append("--content : {} \r\n")
//						.append("------------------------------").toString()
//								, mail.getFrom()[0] + "-" + mail.getFrom()[1]
//								, mail.getRecipients(Message.RecipientType.TO)[0]
//								, mail.getSubject()
//								, mail.getContent());
			logger.info("connecting...");	
			// send the message
			// Transport.send(mail);
			Transport transport = objSession.getTransport(protocol);
			transport.connect();
			logger.info("prepare to send...");
			transport.sendMessage(mail, mail.getAllRecipients());
			logger.info("sent...");
			transport.close();
			logger.info("finish...");
		} catch (MessagingException mex) {
			logger.info("Mail occur MessagingException : {} ", mex.getMessage());
			mex.printStackTrace();
			Exception ex = null;

			if ((ex = mex.getNextException()) != null) {
				logger.info("Mail occur MessagingException-EX : {} ", ex.getMessage());
				ex.printStackTrace();
			}
			throw mex;
//		} finally {
//			// send mail part 2
//			logger.info("MAIL.ORG: start to send");
//			FubonSendJavaMail2 mail2= new FubonSendJavaMail2();
//			mail2.sendMail(fubonMail, annexData);
//			logger.info("MAIL.ORG: sent");
		} catch (Exception e) {
			logger.info("Mail occur exception : {} ", e.getMessage());
			throw e;
		}
	}
	
	/*
	 * 轉換收件者列表
	 */
	private InternetAddress[] getMailToArray(List<Map<String, String>> mailAdress) throws Exception {

		if (mailAdress == null || mailAdress.isEmpty()) {
			return null;
		}

		InternetAddress[] arrAddress = new InternetAddress[mailAdress.size()];
		for (int index = 0; index < mailAdress.size(); index++) {

			try {
				arrAddress[index] = new InternetAddress(mailAdress.get(index).get(MAIL));
			} catch (Exception e) {
				logger.debug("getMailToArray mail not send @index[" + index + "]");
				throw new Exception(e.getMessage(), e);
			}
		}

		return arrAddress;
	}


	/*
	 * 設定是否是debug模式
	 */
	private boolean isTestMode()
	{	
		try{
			//改判斷系統參數
			Map<String , String> xmlVar = new XmlInfo().getVariable("FUBONSYS.MODE" , FormatHelper.FORMAT_3);
			return xmlVar != null && "D".equals(xmlVar.get("MODE"));
			//return "D".equals(DataManager.getSystem().getInfo().get("mode"));
		}catch(Exception e){
			return false;
		}
	}
}