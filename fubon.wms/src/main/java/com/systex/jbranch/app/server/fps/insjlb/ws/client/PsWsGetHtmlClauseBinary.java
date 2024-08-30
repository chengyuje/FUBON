package com.systex.jbranch.app.server.fps.insjlb.ws.client;

import java.lang.reflect.Field;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.CoverAgePrem;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.CoverageDataSet;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.CoverageTable;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.Dt;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.Expression;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.NewDataSet;


@Component("PsWsGetHtmlClauseBinary")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@SuppressWarnings({"unchecked" , "rawtypes" , "unused"})
public class PsWsGetHtmlClauseBinary extends PolicySourceWsClient{
	public Dt caller(Object getCoverage01Map) throws Exception{
		return caller(PolicySourceConf.GET_HTML_CLAUSE_BINARY , getCoverage01Map);
	}
	
	public void genRequestXml(Element element , Object obj) throws Exception{
		element.addElement("strHtml_Clause").setText(ObjectUtils.toString(obj));
	}
	
	public Dt afterSend(PolicySoapVo soapVo) throws Exception{
		return (Dt)parseNodeToObjInField(soapVo , new Dt() , "dt" , new String[]{
			"Binary_Data" , "ViceFile" , "Version" 
		});
	}
	
	public void elementToField(Element subElement , PolicySoapVo soapVo , Object object , String nodeName) throws Exception{
		Dt dt = (Dt)object;
		
		if(nodeName.equals("Binary_Data")){
			dt.setBinaryData(reBase64ByteArray(subElement.getText()));
		}
		else if(nodeName.equals("ViceFile")){
			dt.setViceFile(subElement.getTextTrim());
		}
		else if(nodeName.equals("Version")){
			dt.setVersion(reBigDecimal(subElement.getTextTrim()).intValue());
		}
	}
}
