package com.systex.jbranch.commons.soap;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class HttpClientSoapUtils {
	private static Logger logger = LoggerFactory.getLogger(HttpClientSoapUtils.class);
	
	public static SoapVo sendSoap(SoapVo vo) throws Exception {
		CloseableHttpClient httpclient = vo.getUrl().matches("^https.*") ? createSSLInsecureClient() : HttpClientBuilder.create().build();
		HttpPost httpPost = genHttpPost(vo);
		CloseableHttpResponse response = null;
		int time = 900000;
		RequestConfig config = RequestConfig.custom().setSocketTimeout(time).setConnectTimeout(time).setConnectionRequestTimeout(time).build();
          
        try{
        	httpPost.setConfig(config);
        	logger.info("send ws start"); // sen add check error
	        response = httpclient.execute(httpPost);
	        logger.info("send ws end"); // sen add check error
	        
	        if(response != null && response.getEntity() != null){
	        	vo.setSoapResponseData(EntityUtils.toString(response.getEntity(), vo.getSoapResponseData()));
	        	logger.info("response soap data : \r\n" + vo.getSoapResponseData()); // sen add check error
	        }
        }
        catch(Exception ex){
        	logger.error(ExceptionUtils.getStackTrace(ex));
        	throw ex;
        }
        finally{
            try {
            	httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	
        }
        
        return vo;
	}
	
	public static HttpPost genHttpPost(SoapVo vo){
//		logger.info("create httpPost start");
		HttpPost httpPost = new HttpPost(vo.getUrl());
		httpPost.setEntity(new StringEntity(vo.getSoapRequestData().replace("xmlns=\"\"", "") , vo.getRequestCharSet()));
		
//		logger.info(vo.getSoapRequestData().replace("xmlns=\"\"", ""));
		
		for(String key : vo.getHeaders().keySet()){
			httpPost.addHeader(key, vo.getHeaders().get(key));
		}
		
//		logger.info("create httpPost End");
		return httpPost; 
	}
	
	public static Element searchSingleElement(SoapVo vo , String elemntName) throws DocumentException{
		return searchSingleElement(vo.getSoapResponseData() , vo.getReponseCharSet().name() , elemntName);
	}
	
	public static Element searchSingleElement(String xml , String xmlEncode , String elemntName) throws DocumentException{
		return (Element)loadXmlStr(xml , xmlEncode).getRootElement().selectSingleNode("//*[local-name()='" + elemntName + "']");
	}
	
	public static List searchElements(SoapVo vo , String elemntName) throws DocumentException{
		return searchElements(vo.getSoapResponseData() , vo.getReponseCharSet().name() , elemntName);
	}
	
	public static List searchElements(String xml , String xmlEncode , String elemntName) throws DocumentException{
		return loadXmlStr(xml , xmlEncode).getRootElement().selectNodes("//*[local-name()='" + elemntName + "']");
	}
	
	public static Document loadXmlStr(String xml , String xmlEncode) throws DocumentException{
		Document document = DocumentHelper.parseText(xml);
		document.setXMLEncoding(xmlEncode);
		return document;
	}
	
//	public static void main(String...args) throws DocumentException{
//		SAXReader r = new SAXReader();
//		
//		Map<String , Object>  tb = parseToMap(searchSingleElement(r.read(new File("d://soapResult.xml")).asXML(), "utf-8", "GetCaseResult"));
//		
//		for(Object key : tb.keySet()){
//			System.out.println(key + ":" + tb.get(key));
//		}
//		
//	}
	
	public static Map<String , Object> parseToMap(String jsonStr){
        return parseToType(jsonStr, new TypeToken<Map<String , Object>>(){}.getType());
	}
	
	public static Map<String , Object> parseToMap(Element jsonElement){
		return parseToMap(jsonElement.getTextTrim());
	}
	
	public static Map<String , Object> parseToMap(Document jsonDocument){
		return parseToMap(jsonDocument.getText());
	}
	
	public static Map<String , Object> parseToType(String jsonStr , Type type){
		return new Gson().fromJson(jsonStr, type);
	}

			
	public static void printHttpWsdl(String url) throws Exception{
        int timeOut = 1000*50;
		HttpGet post = new HttpGet(url);
		post.setConfig(RequestConfig.custom().setConnectionRequestTimeout(timeOut).setConnectTimeout(timeOut).build());
		post.addHeader("Content-Type", ContentType.TEXT_HTML.toString());
		CloseableHttpClient closeHc = HttpClientBuilder.create().build();
		HttpResponse res = closeHc.execute(post);
//		System.out.println("wsdl");
//		System.out.println(EntityUtils.toString(res.getEntity()));
		closeHc.close();
	}

	
	public static CloseableHttpClient createSSLInsecureClient() {
		try {
			TrustStrategy trustStrategy = new TrustStrategy() {
				public boolean isTrusted(X509Certificate[] chain, String authType) {
					return true;
				}
			};
			
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null , trustStrategy).build();
			
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslContext , 
				new String[] { "TLSv1", "TLSv1.1", "TLSv1.2" }, 
				null,
	            SSLConnectionSocketFactory.getDefaultHostnameVerifier()
	        );
			
			//SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return  HttpClients.createDefault();
	}
}
