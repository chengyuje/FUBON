package com.systex.jbranch.apserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.SocketFactory;

public class EmailSender {

	public static void main(String[] args) {

		Properties fromMail = new Properties();

		fromMail.setProperty("mail.smtp.host", "smtp.gmail.com");
		fromMail.setProperty("mail.smtp.port", "587");
		fromMail.setProperty("mail.smtp.auth", "true");
		fromMail.setProperty("mail.smtp.starttls.enable", "true");
//		fromMail.setProperty("mail.smtp.socketFactory.port" , "587");
//		fromMail.setProperty("mail.smtp.connectiontimeout", "30000"); // 连接超时
//		fromMail.setProperty("mail.smtp.timeout", "30000");           // 读超时
//		fromMail.setProperty("mail.smtp.writetimeout", "30000");      // 写超时

		final String fUserName = "jeff.cheng@frog-jump.com";
		final String fPwd = "xnfncornysmilsnm";
		
		// 强制 JavaMail 使用指定网络接口（如 Wi-Fi）
        try {
            NetworkInterface wifiInterface = NetworkInterface.getByName("net9"); // Windows 通常是 "Wi-Fi"，macOS 可能是 "en0"
            InetAddress wifiAddress = wifiInterface.getInetAddresses().nextElement();
            fromMail.setProperty("mail.smtp.localaddress", wifiAddress.getHostAddress());
//            fromMail.setProperty("mail.smtp.localaddress", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

		Session objSession = Session.getInstance(fromMail, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fUserName, fPwd);
			}
		});

		try {
			MimeMessage mail = new MimeMessage(objSession);
			mail.setFrom(new InternetAddress(fUserName));
			mail.setRecipients(Message.RecipientType.TO, InternetAddress.parse("jyunda.huang@fubon.com"));

			mail.setSubject("測試郵件");
			mail.setText("這是測試郵件的內容");
//			Transport transport = objSession.getTransport("587");
			Transport.send(mail);

			System.out.println("郵件已成功發送至 jyunda.huang@fubon.com！");

		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

class CustomSocketFactory extends SocketFactory {
	private final String preferredIp;

	public CustomSocketFactory(String preferredIp) {
		this.preferredIp = preferredIp;
	}

	@Override
	public Socket createSocket(String arg0, int arg1) throws IOException, UnknownHostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Socket createSocket(InetAddress arg0, int arg1) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3)
			throws IOException, UnknownHostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2, int arg3) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
