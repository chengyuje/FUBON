package com.systex.jbranch.fubon.commons.esb.xml.parse;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.stringFormat.ConversionType;
import com.systex.jbranch.fubon.commons.stringFormat.ScpecialConversionTypeInf;
import com.systex.jbranch.fubon.commons.stringFormat.XmlParseFormat;


public class XmlParseUtil {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	enum ParamType{
		INSTANCE, GENERIC_TYPE, FIELD
	}
	
	public final Map<String , Field> MESSAGE_MAP = new Hashtable<String , Field> ();
	
	@SuppressWarnings("rawtypes")
	public XmlParseUtil(Class cls){
		for(Field field : cls.getDeclaredFields()){
			XmlElement xmlElement = field.getAnnotation(XmlElement.class);
			if(xmlElement != null){
				MESSAGE_MAP.put(xmlElement.name() , field);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T>T xmlToObject(String xmlContent , String xmlElementName) throws Exception{
		logger.info("xml parse ：\r\n" + xmlElementName);
		Element element = (Element)DocumentHelper.parseText(xmlContent).getRootElement().selectSingleNode(xmlElementName);
		T obj = (T)MESSAGE_MAP.get(xmlElementName).getType().newInstance();
		return (T) (element == null ? null : xmlToObject(element , obj , obj.getClass()));
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T>T xmlToObject(Element nextElement , Object newObject , Class newClass) throws Exception{
		if(newClass.getSuperclass() != null)
			newObject = xmlToObject(nextElement , newObject , newClass.getSuperclass());
		
		Map<String , Map<ParamType , Object>> detailMap = initSpecialTagField(newClass);
		
		//判斷轉換物件中是否有TxRepeat結構若有給予空list
		for(Field field : newClass.getDeclaredFields()){
			XmlElement collectionXmlElement = field.getAnnotation(XmlElement.class);
			Object targetObj = null;
			Map<ParamType , Object> subTagParamMap = null;
			
			//判斷是否預設xml tag 與 class field的對應關係若是不用特殊處理
			if(collectionXmlElement == null || "##default".equals(collectionXmlElement.name()))
				continue;
			
			subTagParamMap = new Hashtable<ParamType , Object>();
			targetObj = List.class.isAssignableFrom(field.getType()) ? new ArrayList() : 
						Set.class.isAssignableFrom(field.getType()) ? new HashSet() : 
						field.getType().newInstance();
						
			//集合
			if(targetObj instanceof Collection)
				subTagParamMap.put(ParamType.GENERIC_TYPE, ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]);//集合中指定泛型型態
			
			subTagParamMap.put(ParamType.INSTANCE, targetObj);
			subTagParamMap.put(ParamType.FIELD, field);
			detailMap.put(collectionXmlElement.name() , subTagParamMap);
		}
		
		//將xml的內容塞到物件中
		for(Element subElement : (List<Element>)nextElement.elements()){
			ConversionType format = null;
			XmlParseFormat xmlParseformat = null;
			
			try{
				//details
				Map<ParamType , Object> subTagParamMap = detailMap.get(subElement.getName());
				
				//子tag處理，直接跳下一層
				if(subTagParamMap != null){
					Object targetObj = null;
					Field field = (Field)subTagParamMap.get(ParamType.FIELD);
						
					//collection type
					if(Collection.class.isAssignableFrom(subTagParamMap.get(ParamType.INSTANCE).getClass())){
						targetObj = subTagParamMap.get(ParamType.INSTANCE);
						Class genType = (Class)subTagParamMap.get(ParamType.GENERIC_TYPE);
						Object genObj = genType.newInstance();
						((Collection)targetObj).add(xmlToObject(subElement , genObj , genType));//集合中指定泛型型態
					}
					//other object field
					else{
						targetObj = xmlToObject(subElement , field.getType().newInstance() , field.getType());
					}
					field.setAccessible(true);
					field.set(newObject , targetObj);
					continue;
				}
			
				inputValToField(newObject , subElement , newClass);
			}catch(NoSuchFieldException ex){}
		}
		
		return (T)newObject;
	}
	
	//塞值給Field
	@SuppressWarnings({ "rawtypes"})
	private void inputValToField(Object newObject , Element element , Class newClass) throws Exception{
		//tag處理
//		Class newClass = newObject.getClass();
		Field field = newClass.getDeclaredField(element.getName());
		Class fieldType = field.getType();
		field.setAccessible(true);
		XmlParseFormat xmlParseformat = field.getAnnotation(XmlParseFormat.class);
		Class specialConversion = null;
		Object converaionTypeValue = null;
		
		//無用XmlParseFormat，進行預設解析
		if(xmlParseformat == null)
			converaionTypeValue = ConversionType.DEFAULT.conversionType(element.getText(), fieldType);
		//有設定XmlParseFormat但未指定，進行預設解析
		else if(XmlParseFormat.DEFAULT_CTYPE.equals(xmlParseformat.value()))
			converaionTypeValue = xmlParseformat.defaultConversion().conversionType(element.getText(), fieldType);
		//進行數值解析
		else if(XmlParseFormat.NUMBER_CTYPE.equals(xmlParseformat.value()))
			converaionTypeValue = xmlParseformat.numberConversion().conversionType(element.getText(), xmlParseformat.point());
		//進行日期解析
		else if(XmlParseFormat.DATE_CTYPE.equals(xmlParseformat.value())){
			//有指定格式
			if(StringUtils.isNotBlank(xmlParseformat.dateFormat()))
				converaionTypeValue = xmlParseformat.dateConversion().conversionType(element.getText(), xmlParseformat.dateFormat());
			//沒有指定格是走預設方式來轉
			else
				converaionTypeValue = xmlParseformat.dateConversion().conversionType(element.getText());
		}
		//設定特殊格式，該類別為specialConversion的實作類別，且不可為抽象類別
		else if(XmlParseFormat.SPECIAL_CTYPE.equals(xmlParseformat.value()) && (specialConversion = xmlParseformat.specialConversion()) != null && ScpecialConversionTypeInf.class.isAssignableFrom(specialConversion) && !Modifier.isAbstract(specialConversion.getModifiers())){
			ScpecialConversionTypeInf scpecialConversionType = null;
			Matcher matcher = Pattern.compile("(\\w+\\.)*\\w+\\$\\w+").matcher(specialConversion.getName());
			//內部類別
			if(matcher.find())
				scpecialConversionType = (ScpecialConversionTypeInf)specialConversion.getConstructors()[0].newInstance(Class.forName(matcher.group().replaceFirst("\\$.*", "")).newInstance());
			//一般類別
			else
				scpecialConversionType = (ScpecialConversionTypeInf)specialConversion.newInstance();
			converaionTypeValue = scpecialConversionType.conversionType(element.getText());
		}
		field.set(newObject , converaionTypeValue);
	}
	
	@SuppressWarnings("rawtypes")
	public Map<String , Map<ParamType , Object>> initSpecialTagField(Class newClass) throws Exception{
		return initSpecialTagField(newClass , new Hashtable<String , Map<ParamType , Object>> ());
	}
	
	@SuppressWarnings("rawtypes")
	public Map<String , Map<ParamType , Object>> initSpecialTagField(Class newClass , Map<String , Map<ParamType , Object>> detailMap) throws Exception{
		//判斷轉換物件中是否有TxRepeat結構若有給予空list
		for(Field field : newClass.getDeclaredFields()){
			XmlElement collectionXmlElement = field.getAnnotation(XmlElement.class);
			Object targetObj = null;
			Map<ParamType , Object> subTagParamMap = null;
			
			//判斷是否預設xml tag 與 class field的對應關係若是不用特殊處理
			if(collectionXmlElement == null || "##default".equals(collectionXmlElement.name()))
				continue;
			
			subTagParamMap = new Hashtable<ParamType , Object>();
			targetObj = List.class.isAssignableFrom(field.getType()) ? new ArrayList() : 
						Set.class.isAssignableFrom(field.getType()) ? new HashSet() : 
						field.getType().newInstance();
						
			//非集合
			if(targetObj instanceof Collection)
				subTagParamMap.put(ParamType.GENERIC_TYPE, ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]);//集合中指定泛型型態
			
			subTagParamMap.put(ParamType.INSTANCE, targetObj);
			subTagParamMap.put(ParamType.FIELD, field);
			detailMap.put(collectionXmlElement.name() , subTagParamMap);
		}
		
		return detailMap;
	}
	
	
	public static void main(String...aegs) throws NoSuchMethodException, SecurityException{
		checkCanNewInstance(ScpecialConversionTypeInf.class , new ScpecialConversionTypeInf(){
			@Override
			public <T> T conversionType(String value) throws Exception {
				return null;
			}
		});
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean checkCanNewInstance(Class superCls , Object obj) throws NoSuchMethodException, SecurityException{
		return checkCanNewInstance(superCls , obj.getClass());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean checkCanNewInstance(Class superCls , Class cls) throws NoSuchMethodException, SecurityException{
		return superCls != null && cls != null && superCls.isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers());
	}
}
