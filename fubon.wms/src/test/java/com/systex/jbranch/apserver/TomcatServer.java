package com.systex.jbranch.apserver;

import java.io.File;
import java.net.MalformedURLException;
import java.sql.SQLException;

import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TomcatServer {

	private static Logger logger = LoggerFactory.getLogger(TomcatServer.class);
	
	public static void main(String[] args) throws ServletException, LifecycleException, MalformedURLException, SQLException {
		String webappDirLocation = "src/webapp/";
		Tomcat tomcat = new Tomcat();
        tomcat.setPort(8081);
        tomcat.setBaseDir(new File("target").getAbsolutePath());
        Context context = tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
        logger.info("configuring app with basedir: " + new File(webappDirLocation).getAbsolutePath());
        
        Server.createWebServer().start();

        File configFile = new File("src/test/resources/META-INF/context.xml");
        context.setConfigFile(configFile.toURI().toURL());
        tomcat.enableNaming();
        tomcat.start();
        tomcat.getServer().await();
	}

}
