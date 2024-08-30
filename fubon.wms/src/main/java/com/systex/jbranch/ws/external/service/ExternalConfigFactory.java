package com.systex.jbranch.ws.external.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.systex.jbranch.platform.common.errHandle.JBranchException;


public class ExternalConfigFactory {
	public static final String TXN_CODE = "TxnCode";
	public static final String BIZ_CODE = "BizCode";
	
	private Map webViewJsMapping;
	private Map webViewPathMapping;
	
	private List externalConfigList;

	public List getExternalConfigList() {
		return externalConfigList;
	}

	public void setExternalConfigList(List externalConfigList) {
		this.externalConfigList = new ArrayList();
		
		if(CollectionUtils.isNotEmpty(externalConfigList)){
			for(Object object : externalConfigList){
				if(object instanceof List){
					List exList = (List)object;
					this.externalConfigList.addAll((List)object);
					exList.clear();
				}
				else if(object instanceof ExternalConfig){
					this.externalConfigList.add(object);
				}
			}
		}
	}
	
	
	public ExternalConfig doGetExternalConfig(String mappingId)throws JBranchException{
		if(StringUtils.isBlank(mappingId)){
			throw new JBranchException("not found this service");
		}

		for(Object obj : externalConfigList){
			ExternalConfig config = (ExternalConfig)obj;
			String id = ObjectUtils.toString(config.getId());
			
			if(id.equals(mappingId)){
				return config;
			}
		}
		
		return null;
	}

	public Map getWebViewJsMapping() {
		return webViewJsMapping;
	}

	public void setWebViewJsMapping(Map webViewJsMapping) {
		this.webViewJsMapping = webViewJsMapping;
	}

	public Map getWebViewPathMapping() {
		return webViewPathMapping;
	}

	public void setWebViewPathMapping(Map webViewPathMapping) {
		this.webViewPathMapping = webViewPathMapping;
	}
}
