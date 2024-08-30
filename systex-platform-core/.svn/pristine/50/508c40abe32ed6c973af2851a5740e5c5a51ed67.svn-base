package com.systex.jbranch.platform.common.scheduler.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.systex.jbranch.platform.common.security.impl.JBranchCryptology;

public class EncryptDriverManagerDataSource extends DriverManagerDataSource{

	
	
	@Override
	protected Connection getConnectionFromDriverManager(String url,
			Properties props) throws SQLException {
		String encodePassword = props.getProperty("password");
		props.setProperty("password", decodePassword(encodePassword));
		return super.getConnectionFromDriverManager(url, props);
	}
	
	protected Connection getConnectionFromDriver(Properties props) throws SQLException {
		String url = getUrl();
		if (logger.isInfoEnabled()) {
			logger.info("Creating new JDBC DriverManager Connection to [" + url + "]");
		}
		return getConnectionFromDriverManager(url, props);
	}
	
	private String decodePassword(String encodePassword){
		return JBranchCryptology.decodePassword(encodePassword);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String pwd = JBranchCryptology.encodePassword("db2inst1");
		System.out.println(pwd);
		System.out.println(JBranchCryptology.decodePassword(pwd));
	}

}
