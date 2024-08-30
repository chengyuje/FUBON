package com.systex.jbranch.fubon.commons.esb.xml.parse;

import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlElement;

import org.apache.jasper.tagplugins.jstl.core.Set;

import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nkne01.NKNE01OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nkne01.NKNE01OutputVODetailsVO;


public class EsbXmlParseTest {
	public static void main1(String...agrs){
		System.out.println(new EsbUtil().decimalPoint("12345", 0));
	}
	
	public static void main(String...args) throws Exception{
		String fcOutboundXML = "<Tx><TxHead><HMSGID>P</HMSGID><HERRID>0000</HERRID><HSYDAY>1070223</HSYDAY><HSYTIME>114019</HSYTIME><HWSID>WMSR</HWSID><HSTANO>0230302</HSTANO><HDTLEN>0086</HDTLEN><HREQQ1/><HREPQ1>EA2PSEQ1</HREPQ1><HDRVQ1>NRWEBHQ</HDRVQ1><HPVDQ1>EA2PSEQ1</HPVDQ1><HPVDQ2/><HSYCVD>1070206</HSYCVD><HTLID>2005001</HTLID><HTXTID>NKNE01</HTXTID><HFMTID>0001</HFMTID><HRETRN>E</HRETRN><HSLGNF>1</HSLGNF><HSPSCK>Y8</HSPSCK><HRTNCD/><HSBTRF/><HFILL/></TxHead><TxBody><SPRefId>00001</SPRefId><AcctId16/><Name/><SEX/><Occur>01</Occur><TxRepeat><W-8BEN>Y</W-8BEN><RAM/><SignStartDate>20180222</SignStartDate><SignEndDate>20211231</SignEndDate><FstTrade>Y</FstTrade><DayFstTrade>Y</DayFstTrade></TxRepeat></TxBody></Tx>";
		ESBUtilOutputVO vo = new ESBUtilOutputVO();
		EsbXmlParseUtil xmlUtil = new EsbXmlParseUtil(vo.getClass());
	
		for(String key : new String[]{"TxHead" , "TxBody" , "NKNE01"}){
			Field field = xmlUtil.MESSAGE_MAP.get(key);
			field.setAccessible(true);
			field.set(vo , xmlUtil.xmlToObject(fcOutboundXML.replaceAll("TxBody", "NKNE01") ,key));
		}
		
//		System.out.println(vo.getNfbrn5OutputVO().getChangeItem());
		//NFBRN5OutputVO
		//showField(vo);
		//showField(vo.getFc032675OutputVO());
		//System.out.println(vo.getFc032675OutputVO().getBDAY());
		
		NKNE01OutputVO fc032675OutputVO = vo.getNkne01OutputVO();
		
		NKNE01OutputVODetailsVO detail = fc032675OutputVO.getDetails();
		System.out.println(detail.getW_8BEN());
		
		
		//System.out.println(vo.getNr098NOutputVO().getDetails().get(0).getProductNo().toString());
		
	}
	
	public static void main2(String...aegs) throws Exception{
		Class cls = ESBUtilOutputVO.class;
		TreeSet<String> set = new TreeSet<String>();
		for(Field field : cls.getDeclaredFields()){
			if(field.getAnnotation(XmlElement.class) != null){
				//System.out.println(field.getType().getName());
				
				for(Field sField : field.getType().getDeclaredFields()){
					if(sField.getAnnotation(XmlElement.class) != null){
						
						if("TxRepeat".equals(sField.getAnnotation(XmlElement.class).name())){
							Object targetObj = List.class.isAssignableFrom(sField.getType()) ? new ArrayList() : 
							Set.class.isAssignableFrom(sField.getType()) ? new HashSet() : 
								sField.getType().newInstance();
					
							if(targetObj instanceof Collection){
								Class scls = (Class)((ParameterizedType)sField.getGenericType()).getActualTypeArguments()[0];
								
								for(Field s2Field : scls.getDeclaredFields()){
									if(s2Field.getAnnotation(XmlElement.class) != null){
										set.add(scls.getName() + " - " + s2Field.getName() + " - " + s2Field.getType().getName());
									}
								}
								
							}
							else{
								for(Field s2Field : targetObj.getClass().getDeclaredFields()){
									if(s2Field.getAnnotation(XmlElement.class) != null){
										set.add(targetObj.getClass().getName() + " - " + s2Field.getName() + " - " + s2Field.getType().getName());
									}
								}
							}
							continue;
						}
						//set.add(sField.getType());
						
						set.add(field.getType().getName() + " - " + sField.getName() + " - " + sField.getType().getName());
					}
				}
			}
		}
		
		FileWriter fr = new FileWriter("D://checkFile.txt");
		for(String scls : set){
			String[] strs = scls.split("\\s-\\s");
			String topStr = strs[0].replaceAll("\\w+\\.|\\.java", "");
			String centerStr = strs[1];
			
			int length = 30 - topStr.length();
			for(int i = 0 ; i < length; i++)
				topStr = topStr  +" ";
			
			length = 30 - centerStr.length();
			for(int i = 0 ; i < length; i++)
				centerStr = centerStr + " ";
			
			System.out.println(topStr + "\t\t" + centerStr + "\t\t" + strs[2] + "\r\n");
			fr.write(topStr + "\t\t" + centerStr + "\t\t" + strs[2] + "\r\n");
			fr.flush();
		}
		fr.close();
	}
	
	public static void showField(Object obj){
		showField(obj , obj.getClass());
	}
	
	public static void showField(Object obj , Class cls){
		if(cls.getSuperclass() != null){
			showField(obj , cls.getSuperclass());
		}
		try{
			if(obj == null)
				return;
			
			System.out.println("##################" + cls.getName());

			System.out.println("#show object field start");
			if(obj != null){
				System.out.println("[" + obj.getClass().getName() + "");
		    	for(Field field : cls.getDeclaredFields()){
		    		String getMethodName = "get" + field.getName().replaceFirst("^.", field.getName().substring(0 , 1).toUpperCase());
		    		try{
		    			Method method = cls.getMethod(getMethodName, new Class[]{});
		    			Object result = method.invoke(obj, new Object[]{});
		    			System.out.println(field.getName() + ":" + (result == null ? "null" : result.toString()));
		    		}
		    		catch(Exception ex){
		    			
		    		}
		    	}
			}
			System.out.println("show object field end#");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
