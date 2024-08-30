package com.systex.jbranch.app.server.fps.sot712;

import java.util.List;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;

/*
 * SOT Report Context
 * author Eli
 * date 2017/08/07
 * 
 */
public class SotPdfContext extends FubonWmsBizLogic{
	private SotPdf pdf = null;
	
	/**
	 * Constructor
	 * @param inputVO : PRDFitInputVO 下單適配Input
	 * @param reportType : 報表種類
	 * @throws JBranchException
	 */
	public SotPdfContext (PRDFitInputVO inputVO, String reportType) throws JBranchException{
		try{
			pdf = (SotPdf) PlatformContext.getBean(reportType);
			pdf.setInputVO(inputVO);
		} catch (JBranchException e){
			throw new APException("SOT報表初始化錯誤!");
		}
	}

	/**
	 * call each report method to get Pdf Url (store to list)
	 * @return List : report urlList
	 * @throws Exception 
	 * @see {@link SotPdf#printReport()}
	 */
	public List<String> getSotPdfULst() throws Exception{
		return pdf.printReport();
	}
}
