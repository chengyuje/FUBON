package com.systex.jbranch.fubon.webservice.ws.FBMAPPService;

import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.commons.soap.HttpClientSoapFactory;
import com.systex.jbranch.commons.soap.HttpClientSoapUtils;
import com.systex.jbranch.commons.soap.SoapVo;
import com.systex.jbranch.commons.soap.cons.SoapCons;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;

public class GetMappCaseSoapUtils {
	private static Logger logger = LoggerFactory.getLogger(GetMappCaseSoapUtils.class);
	
	public static Map<String , Object> getCaseToMap(String dataDispVal) throws Exception{
		return HttpClientSoapUtils.parseToMap(getCase(dataDispVal));
	}
	
	@SuppressWarnings("unchecked")
	public static String getCase(String dataDispVal) throws Exception{
		try{
			Hashtable<String , String> caseConfig = new XmlInfo().getVariable("IOT.GETMAPPCASE" , FormatHelper.FORMAT_3);
			SoapVo vo = new SoapVo();
			vo.setUrl(caseConfig.get("URL"));
			vo.getHeaders().put("SOAPAction" , caseConfig.get("SOAP_ACT"));				
			vo.setRequestCharSet(StandardCharsets.UTF_8);
			vo.setReponseCharSet(StandardCharsets.UTF_8);
			
			Hashtable<String , String> soapNameSpace = new Hashtable<String , String>();
			soapNameSpace.put("xsd", SoapCons.SOAP_XSD_SCHEMA);
			soapNameSpace.put("xsi", SoapCons.SOAP_XSI_SCHEMA);
			vo.setNameSpace(soapNameSpace);
			
			Element getCase = DocumentHelper.createElement("GetCase");
			getCase.addNamespace("" , caseConfig.get("NAME_SPACE"));
				
			Element dataDisp = getCase.addElement("DataDisp");
			dataDisp.setText(dataDispVal);
			
			HttpClientSoapUtils.sendSoap(HttpClientSoapFactory.SOAP_1_1.requestBuilder(vo , getCase));
			return StringUtils.isNotBlank(vo.getSoapResponseData()) ? HttpClientSoapUtils.searchSingleElement(vo , "GetCaseResult").getTextTrim() : null;
		}catch(Exception ex){
			logger.error(String.format("GetMappCaseSoapUtils.getCase exception：%s", StringUtil.getStackTraceAsString(ex)));
			throw ex;
		}
	}
}
