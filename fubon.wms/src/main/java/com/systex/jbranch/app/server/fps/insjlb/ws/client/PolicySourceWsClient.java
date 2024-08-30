package com.systex.jbranch.app.server.fps.insjlb.ws.client;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.systex.jbranch.commons.soap.HttpClientSoapFactory;
import com.systex.jbranch.commons.soap.HttpClientSoapUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.io.JoinDifferentSysBizLogic;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;

@SuppressWarnings({ "unchecked", "rawtypes" })
abstract public class PolicySourceWsClient extends JoinDifferentSysBizLogic implements PolicySourceWsClienInf{
	@Autowired @Qualifier("policySourceConf")
	private GenericMap policySourceConf;
	
	private Logger logger = LoggerFactory.getLogger(PolicySourceWsClient.class);

	/**產生向資訊源呼叫的soap12 xml*/
	abstract public void genRequestXml(Element element , Object obj) throws Exception;
	
	/**接回response後要做的事情*/
	abstract public <T>T afterSend(PolicySoapVo soapVo) throws Exception;
	
	/**解析節點內容給物件屬性的方法*/
	abstract public <T> void elementToField(Element subElement , PolicySoapVo soapVo , T t , String nodeName) throws Exception;
	
	/**呼叫保險資訊源*/
	public <T>T caller(PolicySourceConf conf , Object object) throws Exception{
		return afterSend(send(conf , beforeSend(conf , object)));
	}
	
	/**發送前的準備工作*/
	public String beforeSend(PolicySourceConf conf , Object obj) throws Exception{
		Element element = DocumentHelper.createElement(conf.getName());
		element.addNamespace("" , "http://tempuri.org/");
		genRequestXml(element , obj);
		return element.asXML();
	}
	
	/**發送請求向資訊源索取資料*/
	public PolicySoapVo send(PolicySourceConf conf , String xml) throws Exception{
		PolicySoapVo soapVo = new PolicySoapVo();
		soapVo.setSoapRequestData(xml.replaceAll("xmlns=\"\"", ""));
		soapVo.setPolicySourceConf(conf);
		
		logger.info("policy source request xml：\r\n" + soapVo.getSoapRequestData());
		sendPolicyWs(soapVo);
		logger.info("policy source response xml：\r\n" + soapVo.getSoapResponseData());
		
		defultCheckIsError(soapVo.getSoapResponseData());
		checkIsError(soapVo.getSoapResponseData());

		return soapVo;
	}
	
	/**找出要塞資料的節點，並呼叫轉換物件屬性方法(掃每一個Field就呼叫轉換物件屬性方法)*/
	public Object parseNodeToObjInField(PolicySoapVo soapVo , Object obj , String rootNodeName , String [] nodeNames) throws Exception{
		List<Element> list = nextByName(DocumentHelper.parseText(soapVo.getSoapResponseData()).getRootElement() , rootNodeName);
		
		for(Element el : list){
			for(String nodeName : nodeNames){				
				try{
					List<Element> nodeList = el.selectNodes(nodeName);
					
					for(Element subElement : nodeList){
						for(Field field : obj.getClass().getDeclaredFields()){
							elementToField(subElement , soapVo , obj ,  nodeName);
							break;
						}
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
		
		return obj;
	}
	
	/**找出要塞資料的節點，並呼叫轉換物件屬性方法(到子節點就呼叫轉換物件屬性方法)*/
	public Object parseNodeToObjInSubElement(PolicySoapVo soapVo , Object obj , String rootNodeName , String [] nodeNames) throws Exception{
		List<Element> list = nextByName(DocumentHelper.parseText(soapVo.getSoapResponseData()).getRootElement() , rootNodeName);
		
		for(Element el : list){
			for(String nodeName : nodeNames){				
				try{
					List<Element> nodeList = el.selectNodes(nodeName);
					
					for(Element subElement : nodeList){
						elementToField(subElement , soapVo , obj ,  nodeName);
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
		
		return obj;
	}
	
	public Element selectSingleByNameTree(Element element , String...names){
		for(String name : names){
			element = selectSingleByName(element , name);
			if(element == null)
				return element;
		}
		
		return element;
	}
	
	public Element selectSingleByName(Element element , String name){
		Element result = null;
		
		if(name.equals(element.getName())){
			return element;
		}
		
		for(Element el : (List<Element>)element.elements()){
			if(name.equals(el.getName())){
				return el;
			}
			
			if((result = selectSingleByName(el , name)) != null){
				return result;
			}
		}
		
		return result;
	}
	
	/**取得符合name的element*/
	public List nextByName(Element element , String name){
		List list = new ArrayList();
		if(ObjectUtils.toString(element.getName()).matches(name)){
			list.add(element);
		}
		
		for(Element el : (List<Element>)element.elements()){
			list.addAll(nextByName(el , name));
		}
		
		return list;
	}
	
	/**將下層內容轉到物件的field*/
	public void doSetField(Element element , Object obj) throws IllegalArgumentException, IllegalAccessException{
		for(Element subEl : (List<Element>)element.elements()){
			for(Field field : obj.getClass().getDeclaredFields()){
				field.setAccessible(true);
				if(subEl.getName().toUpperCase().equals(field.getName().toUpperCase())){
					if(field.getType().equals(BigDecimal.class)){
						field.set(obj, reBigDecimal(subEl.getTextTrim()));
					}
					else if(field.getType().equals(short.class) || field.getType().equals(Short.class)){
						field.set(obj, reBigDecimal(subEl.getTextTrim()).shortValue());
					}
					else if(field.getType().equals(int.class) || field.getType().equals(Integer.class)){
						field.set(obj, reBigDecimal(subEl.getTextTrim()).intValue());
					}
					else if(field.getType().equals(long.class) || field.getType().equals(Long.class)){
						field.set(obj, reBigDecimal(subEl.getTextTrim()).longValue());
					}
					else if(field.getType().equals(float.class) || field.getType().equals(Float.class)){
						field.set(obj, reBigDecimal(subEl.getTextTrim()).floatValue());
					}
					else if(field.getType().equals(double.class) || field.getType().equals(Double.class)){
						field.set(obj, reBigDecimal(subEl.getTextTrim()).doubleValue());
					}
					else if(field.getType().equals(char.class) || field.getType().equals(Charset.class)){
						field.set(obj, ObjectUtils.toString(subEl.getTextTrim()).charAt(0));
					}
					else if(field.getType().equals(byte.class) || field.getType().equals(Byte.class)){
						field.set(obj, Byte.valueOf(subEl.getTextTrim()));
					}
					else if(field.getType().equals(String.class)){
						field.set(obj, subEl.getTextTrim());
					}
					else if(field.getType().equals(Calendar.class)){
						if(StringUtils.isNotBlank(subEl.getText())){
							Calendar cal = Calendar.getInstance();
							String calStr = "";
							Matcher matcher = Pattern.compile("\\d+").matcher(subEl.getTextTrim());
							
							while(matcher.find()){
								calStr += matcher.group();
							}
							
							if(calStr.length() >= 9){
								try {
									cal.setTime(new SimpleDateFormat("yyyyMMdd").parse(calStr.substring(0 , 8)));
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								field.set(obj , cal);
							}
						}
					}
				}
			}
		}
	}
	
	public static BigDecimal reBigDecimal(String str){
		if(StringUtils.isBlank(str)){
			return BigDecimal.ZERO;
		}
		return new BigDecimal(str);
	}
	
	//二進位base64串流字串轉byte[]
	public static byte[] reBase64ByteArray(String base64Str){
		return DatatypeConverter.parseBase64Binary(ObjectUtils.toString(base64Str));
	}
	
	/**send request to WS
	 * @throws Exception */
	public void sendPolicyWs(PolicySoapVo soapVo) throws Exception{
		String host = policySourceConf.getStr("host");
		String wsUrl = policySourceConf.getStr("wsUrl");
		String post = policySourceConf.getStr("post");
		Map nameSpaces = policySourceConf.get("policySourceNameSpace");
		
		//若host、url、post只要缺其中一個就讀XML設定
		if(StringUtils.isBlank(host) || StringUtils.isBlank(wsUrl) || StringUtils.isBlank(post)){
			XmlInfo xmlinfo = new XmlInfo();
			GenericMap xmlVar = new GenericMap(new XmlInfo().getVariable("SYS.POL_CONF" , FormatHelper.FORMAT_3));
			
			if(MapUtils.isNotEmpty(xmlVar.getParamMap())){
				wsUrl = xmlVar.getNotNullStr("URL");
				post = xmlVar.getNotNullStr("POST");
				host = xmlVar.getNotNullStr("HOST");
			}
		}
		
		soapVo.setUrl(wsUrl);
		soapVo.setRequestCharSet(StandardCharsets.UTF_8);
		soapVo.setReponseCharSet(StandardCharsets.UTF_8);
		
		Element rootElement = DocumentHelper.parseText(soapVo.getSoapRequestData()).getRootElement();
		soapVo.setNameSpace(nameSpaces);

		
		soapVo.setHeaders(new GenericMap()
			.put("SOAPAction", "http://tempuri.org/" + soapVo.getPolicySourceConf().getName())
			.put("POST", post)
			.put("Host", host)
			.getParamMap()
		);
		logger.info("PolicySourceWsClient 263 do HttpClientSoapUtils.sendSoap start ");  // sen add check error
		HttpClientSoapUtils.sendSoap(HttpClientSoapFactory.SOAP_1_2.requestBuilder(soapVo , rootElement));
		logger.info("PolicySourceWsClient 263 do HttpClientSoapUtils.sendSoap end ");  // sen add check error
		
	}
	
	/**錯誤訊息-資訊源程式錯誤訊息*/
	public void defultCheckIsError(String response) throws Exception{
		Document doc = DocumentHelper.parseText(response);
		Element errElement = selectSingleByNameTree(doc.getRootElement(), "Fault" , "Text");
		if(errElement != null)
			throw new Exception(errElement.getTextTrim().replaceAll("Exception:", "Exception:\n"));
	}
	
	/**錯誤訊息-資訊源回應的錯誤訊息*/
	public void checkIsError(String response) throws Exception{
		Document doc = DocumentHelper.parseText(response);
		Element exceptionEl = selectSingleByName(doc.getRootElement() , "Exception");
		StringBuffer errorMsg = new StringBuffer("call PolicySoucreWs is error : ");
		
		if(exceptionEl != null){
			Node node = null;
			for(String nodeName : new String[]{"ExceptionCode" , "ExceptionTxt" , "ExceptionType"}){
				if((node = exceptionEl.selectSingleNode(nodeName)) != null){
					errorMsg.append(node.getText());
				}
			}
			throw new Exception(errorMsg.toString());
		}
	}
	
	/**查看節點基本結構用的測試方法*/
	public void nextShow(Element element , String line){
		for(Element el : (List<Element>)element.elements()){
			System.out.println(line + el.getName() + "=" + el.getTextTrim());
			nextShow(el , line + "\t");
		}
	}
	public GenericMap getPolicySourceConf() {
		return policySourceConf;
	}
	
	public void setPolicySourceConf(GenericMap policySourceConf) {
		this.policySourceConf = policySourceConf;
	}
}
