package com.systex.jbranch.app.server.fps.insjlb.ws.client;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.AllPolicyOBJ;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.CoverAgePrem;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.CoverageDataSet;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.CoverageTable;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.Expression;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.LogTable;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.NewDataSet;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.NewReportExpression;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.PayYear;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.PremDetail;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.PremPerMonth;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.TmpExpression;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.WholeLife;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.WholeLifeDtl;


@Component("PsWsGetCoverage01")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@SuppressWarnings({"unchecked" , "rawtypes" , "unused"})
public class PsWsGetCoverage01 extends PolicySourceWsClient{
	public CoverageDataSet caller(Object getCoverage01Map) throws Exception{
		return caller(PolicySourceConf.GET_COVERAGE01, getCoverage01Map);
	}
	
	public void genRequestXml(Element element , Object obj) throws Exception{
		Map<String , Object> getCoverage01Map = (Map<String , Object>)obj;
		for(Entry<String, Object> entry : getCoverage01Map.entrySet()){
			element.addElement(entry.getKey()).setText(ObjectUtils.toString(entry.getValue()));
		}
	}
	
	public CoverageDataSet afterSend(PolicySoapVo soapVo) throws Exception{
		return (CoverageDataSet)parseNodeToObjInField(soapVo , new CoverageDataSet() , "CoverageDataSet" , new String[]{
			"CoverageTable" , "CoverAgePrem" , "Expression"
		});
	}
	
	public void elementToField(Element subElement , PolicySoapVo soapVo , Object object , String nodeName) throws Exception{
		CoverageDataSet coverageDataSet = (CoverageDataSet)object;
		
		Class cls = Class.forName("com.systex.jbranch.app.server.fps.insjlb.ws.client.vo." + subElement.getName());
		Object obj = cls.newInstance();
		
		if(nodeName.equals("CoverageTable")){
			coverageDataSet.setCoverageTableArray((CoverageTable[]) 
				ArrayUtils.add(coverageDataSet.getCoverageTableArray() , obj));
		}
		else if(nodeName.equals("CoverAgePrem")){
			coverageDataSet.setCoverAgePremArray((CoverAgePrem[]) 
				ArrayUtils.add(coverageDataSet.getCoverAgePremArray() , obj));
		}
		else if(nodeName.equals("Expression")){
			coverageDataSet.setExpressionArray((Expression[])
				ArrayUtils.add(coverageDataSet.getExpressionArray() , obj));
		}
		
		//將對應到的tag下的子tag相對內容塞到obj裡面的所有field中
		doSetField(subElement , obj);
	}
}
