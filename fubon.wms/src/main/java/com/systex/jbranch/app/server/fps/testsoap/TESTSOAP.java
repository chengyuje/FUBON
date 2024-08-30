package com.systex.jbranch.app.server.fps.testsoap;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * REF110 新增轉介資料
 * 
 * @author Ocean
 * @date 20160622
 * @spec 
 */
@Component("testsoap")
@Scope("request")
public class TESTSOAP extends FubonWmsBizLogic {
	private Logger logger = LoggerFactory.getLogger(TESTSOAP.class);
		
	public void query(Object body, IPrimitiveMap header) throws JBranchException {
		TESTSOAPInputVO inputVO = (TESTSOAPInputVO)body;
		TESTSOAPOutputVO outputVO = new TESTSOAPOutputVO();
		logger.info(inputVO.getSoapRequestData());
		
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
		HttpPost httpPost = new HttpPost(inputVO.getUrl());
		httpPost.setHeader("Content-Type", inputVO.getContentType());
        httpPost.setHeader("SOAPAction", inputVO.getSoapAction());
        httpPost.setEntity(new StringEntity(inputVO.getSoapRequestData(), Charset.forName("UTF-8"))); 
        
        try{
	        CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
	        if(response != null){
	        	HttpEntity httpEntity = response.getEntity();
		        
		        if (httpEntity != null) {  
		        	outputVO.setResultSoapData(EntityUtils.toString(httpEntity, "UTF-8"));  
		            // logger.info("response:" + outputVO.getResultSoapData());
		        }	
	        }
        }catch(Exception ex){
        	ex.printStackTrace();
        	outputVO.setResultSoapData(String.format("GetMappCaseSoapUtils.getCase exception：%s", StringUtil.getStackTraceAsString(ex)));
        }
        finally{
        	// 释放资源  
            try {
				closeableHttpClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
        }
        
        this.sendRtnObject(outputVO);
	}	
}