package com.systex.jbranch.fubon.commons.esb.xml.parse;

import com.systex.jbranch.fubon.commons.stringFormat.ConversionType;
import com.systex.jbranch.fubon.commons.stringFormat.MappingConfig;
import com.systex.jbranch.fubon.commons.stringFormat.ScpecialConversionTypeInf;
import com.systex.jbranch.fubon.commons.stringFormat.XmlParseFormat;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EsbXmlParseUtil {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	enum ParamType{
		INSTANCE, GENERIC_TYPE, FIELD
	}

	public final Map<String , Field> MESSAGE_MAP = new Hashtable<String , Field> ();

	@SuppressWarnings("rawtypes")
	public EsbXmlParseUtil(Class cls){
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
			if(isSpecialElement(collectionXmlElement)) continue;

			subTagParamMap = new Hashtable<ParamType , Object>();
			targetObj = List.class.isAssignableFrom(field.getType()) ? new ArrayList() :
						Set.class.isAssignableFrom(field.getType()) ? new HashSet() :
						field.getType().newInstance();

			//非集合
			if(targetObj instanceof Collection)
				subTagParamMap.put(ParamType.GENERIC_TYPE, ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]);////集合中指定泛型型態

			subTagParamMap.put(ParamType.INSTANCE, targetObj);
			subTagParamMap.put(ParamType.FIELD, field);
			detailMap.put(collectionXmlElement.name(), subTagParamMap);
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

		//Field field = newClass.getDeclaredField(element.getName());
		Field field = findMappingField(element.getName() , newClass);
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

	public Field findMappingField(String name , Class cls) throws Exception {
		Exception exception = null;
		try{
			try {
				return cls.getDeclaredField(name);
			} catch (NoSuchFieldException noSuchField) {
				/** 如果 XML tag-name 有 hyphen，轉換為 JavaBean 可命名的 underscore **/
				return cls.getDeclaredField(StringUtils.replace(name, "-", "_"));
			}
		}
		catch(Exception ex){
			exception = ex;

			for(Field field : cls.getDeclaredFields()){
				MappingConfig mappingConfig = field.getAnnotation(MappingConfig.class);

				if(mappingConfig != null){
					String mappingVal = ObjectUtils.toString(mappingConfig.value());

					if(name.equals(mappingVal)){
						return field;
					}
				}
			}
		}

		throw exception;
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
			if(isSpecialElement(collectionXmlElement)) continue;

			subTagParamMap = new Hashtable<ParamType , Object>();
			targetObj = List.class.isAssignableFrom(field.getType()) ? new ArrayList() :
						Set.class.isAssignableFrom(field.getType()) ? new HashSet() :
						field.getType().newInstance();

			//非集合
			if(targetObj instanceof Collection)
				subTagParamMap.put(ParamType.GENERIC_TYPE, ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]);////集合中指定泛型型態

			subTagParamMap.put(ParamType.INSTANCE, targetObj);
			subTagParamMap.put(ParamType.FIELD, field);
			detailMap.put(collectionXmlElement.name() , subTagParamMap);
		}

		return detailMap;
	}

	/** 是否為特殊處理的電文欄位 **/
	private boolean isSpecialElement(XmlElement collectionXmlElement) {
		return collectionXmlElement == null ||
				"##default".equals(collectionXmlElement.name()) ||
				// for CBS 電文的欄位，該欄位 Tag 上送電文名稱有 '-'（如: <xx-xx>），必須使用 @XmlElement(name="xx-xx") 處理
				StringUtils.contains(collectionXmlElement.name(), "-");
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
