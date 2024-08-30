package com.systex.jbranch.commons.soap;

import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.commons.soap.cons.SoapCons;


public enum HttpClientSoapFactory{
	SOAP_1_1(SoapCons.SOAP_1_1 , SoapCons.SOAP_1_1_SCHEMA , SoapCons.SOAP_1_1_CONTENT_TYPE) , 
	SOAP_1_2(SoapCons.SOAP_1_2 , SoapCons.SOAP_1_2_SCHEMA , SoapCons.SOAP_1_2_CONTENT_TYPE) ;

	private static Logger logger = LoggerFactory.getLogger(HttpClientSoapFactory.class);
	private String contentType;
	private String soapXmlSchema;
	private String rootName;
	private String soapBody;
	private String soap;

	
	HttpClientSoapFactory(String soap , String soapXmlSchema , String contentType){
		this.soap = soap;
		this.contentType = contentType;
		this.soapXmlSchema = soapXmlSchema;
		this.rootName = soap + SoapCons.SOAP_ROOT;
		this.soapBody = soap + SoapCons.SOAP_BODY;
	}
	
	/**產生SOAP REQUEST DATA**/
	public SoapVo requestBuilder(SoapVo vo , Element subElement){
		logger.info("create soap request data Start");
		Document document = DocumentHelper.createDocument();	
		
		Element elmenet = document.addElement(this.getRootName())
			.addNamespace(this.getSoap() , this.getSoapXmlSchema());
		
		if(MapUtils.isNotEmpty(vo.getNameSpace())){
			for(Entry<String , String> nameSpace : vo.getNameSpace().entrySet()){
				elmenet.addNamespace(nameSpace.getKey() , nameSpace.getValue());
			}
		}
			
		Element rootElement = elmenet.addElement(this.getSoapBody());
		rootElement.add(subElement);
		
		if(StringUtils.isBlank(vo.getHeaders().get("Content-Type")) ){
			vo.getHeaders().put("Content-Type", this.getContentType() + vo.getRequestCharSet());
		}
		
		vo.setSoapRequestData(document.asXML());
		logger.info("soap request data :\r\n" + vo.getSoapRequestData());
		logger.info("create soap request data End");
		return vo;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getSoapXmlSchema() {
		return soapXmlSchema;
	}

	public void setSoapXmlSchema(String soapXmlSchema) {
		this.soapXmlSchema = soapXmlSchema;
	}

	public String getRootName() {
		return rootName;
	}

	public void setRootName(String rootName) {
		this.rootName = rootName;
	}

	public String getSoapBody() {
		return soapBody;
	}

	public void setSoapBody(String soapBody) {
		this.soapBody = soapBody;
	}

	public String getSoap() {
		return soap;
	}

	public void setSoap(String soap) {
		this.soap = soap;
	}
}
