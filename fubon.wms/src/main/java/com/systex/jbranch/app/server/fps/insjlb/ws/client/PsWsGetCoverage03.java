package com.systex.jbranch.app.server.fps.insjlb.ws.client;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.dom4j.Element;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.AllPolicyOBJ;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.Expression;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.LogTable;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.NewDataSet;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.NewReportExpression;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.PayYear;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.Policy;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.PolicyColumn;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.PolicyDtlColumn;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.PremDetail;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.PremPerMonth;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.RelationColumn;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.TmpExpression;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.WholeLife;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.WholeLifeDtl;
import com.systex.jbranch.comutil.collection.GenericMap;

@Component("PsWsGetCoverage03")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@SuppressWarnings({"unchecked" , "rawtypes" , "unused"})
public class PsWsGetCoverage03 extends PolicySourceWsClient{	
	public NewDataSet caller(Object policys) throws Exception{
		return caller(PolicySourceConf.GET_COVERAGE03, policys);
	}
	
	public void genRequestXml(Element element , Object obj) throws Exception{
		GenericMap getCoverage03Gmap = new GenericMap((Map)obj);
		List<Policy> policys = getCoverage03Gmap.get("policys");
		List<RelationColumn> relations = getCoverage03Gmap.get("relations");

		Element policyDtlEl = element.addElement("PolicyDtl");
		Element policyEl = element.addElement("Policy");
		Element relationEl = element.addElement("Relation");
		
		if(CollectionUtils.isEmpty(policys)){
			throw new Exception("Policy IS Empty");
		}
		
		//一個poilice會有一組PolicyDtlColumns跟1個PolicyColumn
		for(Policy policy : policys){
			Element arrayOfPolicyDtlColumn = policyDtlEl.addElement("ArrayOfPolicyDtlColumn");
			
			if(CollectionUtils.isNotEmpty(policy.getPolicyDtlColumns())){
				for(PolicyDtlColumn policyDtlColumn : policy.getPolicyDtlColumns()){
					Element policyDtlColumnEl = arrayOfPolicyDtlColumn.addElement("PolicyDtlColumn");
					
					for(Field field : policyDtlColumn.getClass().getDeclaredFields()){
						field.setAccessible(true);
						String elName = field.getName().equals("CustSSNIO") ? "CustSSN_IO" : field.getName();
						policyDtlColumnEl.addElement(elName).setText(ObjectUtils.toString(field.get(policyDtlColumn)));
					}
				}
			}
			
			if(policy.getPolicyColumn() != null){
				PolicyColumn policyColumn = policy.getPolicyColumn();
				Element PolicyColumnEl = policyEl.addElement("PolicyColumn");
				
				for(Field field : policyColumn.getClass().getDeclaredFields()){
					field.setAccessible(true);
					PolicyColumnEl.addElement(field.getName()).setText(ObjectUtils.toString(field.get(policyColumn)));
				}
			}
		}
		
		if(CollectionUtils.isEmpty(relations)){
			RelationColumn relationColumn = new RelationColumn();
			relations = relations == null ? new ArrayList() : relations;
			relations.add(relationColumn);
		}
		
		for(RelationColumn relation : relations){
			Element relationColumnEl = relationEl.addElement("RelationColumn");
			
			for(Field field : relation.getClass().getDeclaredFields()){
				field.setAccessible(true);
				relationColumnEl.addElement(field.getName()).setText(ObjectUtils.toString(field.get(relation)));
			}
		}
		
		
	}
	
	public NewDataSet afterSend(PolicySoapVo soapVo) throws Exception{
		return (NewDataSet)parseNodeToObjInSubElement(soapVo , new NewDataSet() , "NewDataSet" , new String[]{
			"WholeLife" 	, "PremPerMonth" 	, "PremDetail" 		, "NewReportExpression" , "AllPolicyOBJ" , 
			"tmpExpression" , "Expression" 		, "WholeLifeDtl" 	, "LogTable" 			, "PayYear"
		});
	}
	
	public void elementToField(Element subElement , PolicySoapVo soapVo , Object object , String nodeName) throws Exception{
		String className = "tmpExpression".equals(subElement.getName()) ? "TmpExpression" :subElement.getName();
		Class cls = Class.forName("com.systex.jbranch.app.server.fps.insjlb.ws.client.vo." + className);
		Object obj = cls.newInstance();
		NewDataSet newDataSet = (NewDataSet)object;
		
		//將物件塞到對應的陣列，並將xml內容塞到物件之中
		for(Field field : newDataSet.getClass().getDeclaredFields()){
			if(nodeName.equals("WholeLife")){
				newDataSet.setWholeLifeArray((WholeLife[]) ArrayUtils.add(newDataSet.getWholeLifeArray() , obj));
			}
			else if(nodeName.equals("PremPerMonth")){
				newDataSet.setPremPerMonthArray((PremPerMonth[]) ArrayUtils.add(newDataSet.getPremPerMonthArray() , obj));
			}
			else if(nodeName.equals("PremDetail")){
				newDataSet.setPremDetailArray((PremDetail[]) ArrayUtils.add(newDataSet.getPremDetailArray() , obj));
			}
			else if(nodeName.equals("NewReportExpression")){
				newDataSet.setNewReportExpressionArray((NewReportExpression[]) ArrayUtils.add(newDataSet.getNewReportExpressionArray() , obj));
			}
			else if(nodeName.equals("AllPolicyOBJ")){
				newDataSet.setAllPolicyOBJArray((AllPolicyOBJ[]) ArrayUtils.add(newDataSet.getAllPolicyOBJArray() , obj));
			}
			else if(nodeName.equals("tmpExpression")){
				newDataSet.setTmpExpressionArray((TmpExpression[]) ArrayUtils.add(newDataSet.getTmpExpressionArray() , obj));
			}
			else if(nodeName.equals("Expression")){
				newDataSet.setExpressionArray((Expression[]) ArrayUtils.add(newDataSet.getExpressionArray() , obj));
			}
			else if(nodeName.equals("WholeLifeDtl")){
				newDataSet.setWholeLifeDtlArray((WholeLifeDtl[]) ArrayUtils.add(newDataSet.getWholeLifeDtlArray() , obj));
			}
			else if(nodeName.equals("LogTable")){
				newDataSet.setLogTableArray((LogTable[]) ArrayUtils.add(newDataSet.getLogTableArray() , obj));
			}
			else if(nodeName.equals("PayYear")){
				newDataSet.setPayYearArray((PayYear[]) ArrayUtils.add(newDataSet.getPayYearArray() , obj));
			}
			
			doSetField(subElement , obj);
			break;
		}
	}
}
