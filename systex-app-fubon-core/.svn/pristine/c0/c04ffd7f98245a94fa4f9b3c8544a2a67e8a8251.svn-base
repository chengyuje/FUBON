package com.systex.jbranch.platform.server.mail;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.util.PlatformContext;

public class FubonSendJavaMail2  {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	public static String MAIL = "MAIL";
	
	public void sendMail(FubonMail fubonMail,Map<String, Object> annexData) throws Exception {
		// create some properties and get the default Session		
		JavaMailSenderImpl sender = (JavaMailSenderImpl) PlatformContext.getBean("mailSender");
		Properties objProps = sender.getJavaMailProperties();
		
		final String userName = sender.getUsername();
		final String pwd = sender.getPassword();
		
		Session objSession = Session.getInstance(objProps, new Authenticator() {  
            protected PasswordAuthentication getPasswordAuthentication() {  
                return new PasswordAuthentication(userName, pwd);  
                 
            }  
        });  
		objSession.setDebug(isTestMode()); // 設定是否是debug模式(如果是會輸出送Mail的相關資訊)

		try {
			// create a message
			MimeMessage mail = new MimeMessage(objSession);
			mail.setFrom(new InternetAddress(fubonMail.getFromMail()));

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
			
			// send the message
			Transport.send(mail);
		} catch (MessagingException mex) {
			mex.printStackTrace();
			Exception ex = null;

			if ((ex = mex.getNextException()) != null) {
				ex.printStackTrace();
			}
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
			return "D".equals(DataManager.getSystem().getInfo().get("mode"));
		}catch(Exception e){
			return false;
		}
	}
}