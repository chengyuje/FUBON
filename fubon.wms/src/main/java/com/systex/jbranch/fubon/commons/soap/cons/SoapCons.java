package com.systex.jbranch.fubon.commons.soap.cons;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.CharEncoding;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.dom4j.Element;


public class SoapCons{
	//soap 1.1 xml schema
	public static final String SOAP_1_1_SCHEMA = "http://schemas.xmlsoap.org/soap/envelope/";
	//soap 1.1 content type
	public static final String SOAP_1_1_CONTENT_TYPE = "text/xml; ";
	//soap 1.1 soap
	public static final String SOAP_1_1 = "soap";
	//soap 1.2 xml schema
	public static final String SOAP_1_2_SCHEMA = "http://www.w3.org/2003/05/soap-envelope";
	//soap 1.2 soap
	public static final String SOAP_1_2 = "soap12";
	//soap 1.2 content type
	public static final String SOAP_1_2_CONTENT_TYPE = "application/soap+xml; ";
	//soap root name
	public static final String SOAP_ROOT = ":Envelope";
	//soap body name
	public static final String SOAP_BODY = ":Body";
	//xml schema xsi
	public static final String SOAP_XSI_SCHEMA = "http://www.w3.org/2001/XMLSchema-instance";
	//xml schema xsd
	public static final String SOAP_XSD_SCHEMA = "http://www.w3.org/2001/XMLSchema/";
}