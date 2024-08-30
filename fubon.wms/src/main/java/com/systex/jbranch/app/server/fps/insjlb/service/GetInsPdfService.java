package com.systex.jbranch.app.server.fps.insjlb.service;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPdfInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPdfOutputVO;
import com.systex.jbranch.common.io.util.PdfInputOutputUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

/**取得保險百科條款與說明檔(url)*/
@Service("GetInsPdfService")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@SuppressWarnings("unchecked")
public class GetInsPdfService extends GetInsService implements GetInsPdfServiceInf{
	private Logger logger = LoggerFactory.getLogger(GetInsPdfService.class);	
	
	@Autowired 
	private PdfInputOutputUtils pdfUtils;

	@SuppressWarnings({ "rawtypes" })
	public GetInsPdfOutputVO getInsPdf(GetInsPdfInputVO inputVo) throws JBranchException{
		GetInsPdfOutputVO outputVo = new GetInsPdfOutputVO();
		List<? extends Map> lstPdfList = null;
		String pdfType = inputVo.getPdfType();//檔案種類
		
		//驗證傳參
		GetInsValiateUtils.validate(inputVo);
		
		//讀取資訊源產品檔
		if(CollectionUtils.isEmpty(lstPdfList = getInsjlbDao().queryInfSourceProdData(inputVo.getLstInsProd()))){
			throw new JBranchException("data not found");
		}
		
		try {
			String methodName = CASE_METHOD_NAME + pdfType;
			Class[] argumentTypes = new Class[]{GenericMap.class};
			
			//若有多筆就會呼叫多次，並將每一筆的URL1或URL2設定好
			for(Map lstPdfMap : lstPdfList){
				//依照type呼叫case1到case4其中一個方法
				this.getClass().getMethod(methodName , argumentTypes).invoke(this , new GenericMap(lstPdfMap)); 
			}
		}
		catch(Exception ex){
			logger.error(ExceptionUtils.getStackTrace(ex));
			throw new JBranchException(ex);
		}
		
		outputVo.setLstPdf(lstPdfList);
		return outputVo;
	}
	
	
	/**條款*/
	public void getInsPdfCase1(GenericMap lstPdfGmap) throws JBranchException{
		getInsPdfCase(lstPdfGmap , lstPdfGmap.getNotNullStr(CLAUSE_FILENAME) , "1" , "(條款)");
	}
	
	/**說明*/
	public void getInsPdfCase2(GenericMap lstPdfGmap) throws JBranchException{
		getInsPdfCase(lstPdfGmap , lstPdfGmap.getNotNullStr(CONTENT_FILENAME) , "1" , "(說明)");
	}
	
	/**列印用(同時含條款&說明)*/
	public void getInsPdfCase3(GenericMap lstPdfGmap) throws JBranchException, FileNotFoundException{
 		String contentFileName = lstPdfGmap.getNotNullStr(CONTENT_FILENAME);
		String clausefileName = lstPdfGmap.getNotNullStr(CLAUSE_FILENAME);
		List<Map<String, Object>> lstdsList = null;
		byte[] binaryData = null;
		byte[] binaryData2 = null;
	    
	    List<String> pdfFiles = new ArrayList<String>();
	    String contentFileUrl = null;
	    String clausefileUrl = null;
		String url = null;
		
		if(StringUtils.isBlank(contentFileName) && StringUtils.isBlank(clausefileName)){
			return;
		}
		
		if(StringUtils.isNotBlank(contentFileName)){
			lstdsList = getCmjlb210().getHtmlClauseBinary(contentFileName).getLstDS();
			
			if((binaryData = (byte[])lstdsList.get(0).get(BINARY_DATA)) != null){
				contentFileUrl = pdfUtils.doWritePdfFile(binaryData , CONTENT_FILENAME);
			}
		}
		
		if(StringUtils.isNotBlank(clausefileName)){
			lstdsList = getCmjlb210().getHtmlClauseBinary(clausefileName).getLstDS();
			
			if((binaryData2 = (byte[])lstdsList.get(0).get(BINARY_DATA)) != null){
				clausefileUrl = pdfUtils.doWritePdfFile(binaryData2 , CLAUSE_FILENAME);
			}
		}
		
		if(contentFileUrl  == null || clausefileUrl == null){
			url = contentFileUrl == null && clausefileUrl != null ? clausefileUrl : clausefileUrl == null && contentFileUrl != null ? contentFileUrl : null;
		    lstPdfGmap.put(URL1 , url);
		    lstPdfGmap.put("pdfName1" , lstPdfGmap.getNotNullStr(PRD_NAME));
		    return;
	    }
	    
	    for(String path : new String[]{contentFileUrl , clausefileUrl}){
	    	if(StringUtils.isNotBlank(path)){
	    		pdfFiles.add(path);
	    	}
        }

		lstPdfGmap.put(URL1 , pdfUtils.doWriteMergePdfFile(pdfFiles , true));
		lstPdfGmap.put("pdfName1" , lstPdfGmap.getNotNullStr(PRD_NAME));
	}
	
	/**條款+說明*/
	public void getInsPdfCase4(GenericMap lstPdfGmap) throws JBranchException{
		getInsPdfCase(lstPdfGmap , lstPdfGmap.getNotNullStr(CONTENT_FILENAME) , "1" , "(說明)");
		getInsPdfCase(lstPdfGmap , lstPdfGmap.getNotNullStr(CLAUSE_FILENAME) , "2" , "(條款)");
	}
	
	public void getInsPdfCase(GenericMap lstPdfGmap , String fileName , String numStr , String wording) throws JBranchException{
		byte[] binaryData = null;
		List<Map<String, Object>> lstdsList = null;
		
		if( StringUtils.isBlank(fileName) ||
			CollectionUtils.isEmpty(lstdsList = getCmjlb210().getHtmlClauseBinary(fileName).getLstDS()) ||
			(binaryData = (byte[])lstdsList.get(0).get(BINARY_DATA)) == null ){
			return;
		}
		
		lstPdfGmap.put("URL" + numStr , pdfUtils.doWritePdfFile(binaryData));
		lstPdfGmap.put("pdfName" + numStr , lstPdfGmap.getNotNullStr(PRD_NAME) + wording);
	}
}

