package com.systex.jbranch.platform.server.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.systex.jbranch.platform.common.dataManager.DataManager;

public class DTDEntityResolver implements EntityResolver {	
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
	       	if (systemId == null) {
	            return null;
	        }

	        try {	        	
	            URL url = new URL(systemId);
	            String file = url.getFile();
	            //
	            //從 JBoss 的 Log 檢視時，發現底下程式碼會執行二次
	            //另一次的發動點是在 WorkflowDescriptor.Java 的 validateDTD() 裏
	            //為保持程式的完整性，故不更改
	            //
	            //LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "===============");
	            //LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "Url =" + url.toString());
	            //LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "File1=" + file);
	            //LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "===============");
	            //2008.10.07 將 dtd 驗証放至 $JBranch_Root\Config 下
	            if ((file != null) && (file.indexOf('/') > -1)) {
	            	file = file.substring(file.lastIndexOf('/') + 1);
	            }
	            String dtdFile = SystemCfg.getSysFolder(EnumSysPath.config) + file;
	            //LogUtil.writeLog(LogHelper.TYPE_ID_PF, LogUtil.LEVEL_DEBUG, "File2=" + dtdFile);
	            
				InputStream is = new FileInputStream(dtdFile);
	            if (is != null) {
	                return new InputSource(is);
	            }
	        }
	        //modified by mbussetti - 15 nov 2004
	        //if the systemId isn't an URL, it is searched in the usual classpath
	         catch (MalformedURLException e) {
	            InputStream is = getClass().getResourceAsStream("/META-INF/" + systemId);

	            if (is == null) {
	                is = getClass().getResourceAsStream('/' + systemId);
	            }

	            if (is != null) {
	                return new InputSource(is);
	            }
	        }

		return null;
	}
}

//用 SAXBuilder 的解析方法
//透過
//	SAXBuilder builder = new SAXBuilder();
//	builder.setValidation(false);
//	builder.setEntityResolver(new SystexEntityResolver(   dtdFile ))  驗証
/*
public class SystexEntityResolver implements EntityResolver 
{ 
	private String m_dtdFile;
	public SystexEntityResolver(final String dtdFile) {
		this.m_dtdFile = dtdFile;
	}
	public InputSource resolveEntity(String publicId,
		String systemId) throws SAXException, IOException {
		return new InputSource(this.m_dtdFile);
	}
}
*/
