package com.systex.jbranch.app.server.fps.ins300;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.insjlb.INSJLB;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsCompareInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPdfInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPdfOutputVO;
import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("ins300")
@Scope("request")
public class INS300 extends FubonWmsBizLogic {
	private Logger logger = LoggerFactory.getLogger(INS300.class);

	@Autowired @Qualifier("insjlb")
	private INSJLB insjlb;

	/**
	 * 取得查詢結果
	 * @param body
	 * @param header
	 * @throws Exception 
	 */
	public void queryData(Object body, IPrimitiveMap header) throws Exception {
		INS300InputVO inputVO = (INS300InputVO) body;
		INS300OutputVO outputVO = new INS300OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		//==查詢==
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT ");
		sb.append(" COMP.COM_NAME, COMP.COM_ID, ITEM.QID, MAIN.KEY_NO, MAIN.PRD_NAME, ");
		sb.append(" MAIN.PRD_ID, MAIN.IS_SALE, MAIN.IS_MAIN, MAIN.LIST_Y as ITEM_Y ");
		sb.append(" FROM TBPRD_INSDATA_PROD_MAIN MAIN ");
		sb.append(" LEFT JOIN TBPRD_INSDATA_COMPANY COMP ON MAIN.COM_ID = COMP.COM_ID ");
		sb.append(" LEFT JOIN TBPRD_INSDATA_QUERYITEM ITEM ON MAIN.KEY_NO = ITEM.KEY_NO ");
		
		if(!StringUtils.isBlank(inputVO.getPRD_NAME())){
			String[] prdNames = inputVO.getPRD_NAME().split(",");
			if(prdNames.length > 4) {
				throw new APException("請勿輸入超過四個欲模糊查詢的險種名稱 !");
			}
			
			List<String> prdConditions = new ArrayList<String>();
			for(String prdName :prdNames) {
				prdConditions.add("%" + prdName.trim() + "%");
			}
			qc.setObject("prdConditions", prdConditions);
			sb.append(" JOIN (SELECT column_value filter FROM table(sys.odcivarchar2list(:prdConditions))) ON MAIN.PRD_NAME LIKE filter ");
		}
		
		sb.append(" WHERE 1=1 AND MAIN.IFCHS <> 'F'");

		//==查詢條件==
		if(!StringUtils.isBlank(inputVO.getQID())){
			sb.append(" AND ITEM.QID = :QID ");
			qc.setObject("QID", inputVO.getQID());
		}
		if(!StringUtils.isBlank(inputVO.getCOM_NAME())){
			sb.append(" AND COMP.COM_NAME = :COM_NAME ");
			qc.setObject("COM_NAME", inputVO.getCOM_NAME());
		}
		if(CollectionUtils.isNotEmpty(inputVO.getIsSaleList())){
			sb.append(" AND MAIN.IS_SALE in (:IS_SALE) ");
			qc.setObject("IS_SALE", inputVO.getIsSaleList());
		}
		if(!StringUtils.isBlank(inputVO.getIS_MAIN())){
			sb.append(" AND MAIN.IS_MAIN = :IS_MAIN ");
			qc.setObject("IS_MAIN", inputVO.getIS_MAIN());
		}
		sb.append(" order by COMP.COM_ID,ITEM.QID,MAIN.PRD_NAME ");
		// 查詢結果
		qc.setQueryString(sb.toString());
		outputVO.setIns_outputList(dam.exeQuery(qc));// 回傳資料
		// ResultIF pageList = dam.executePaging(qc, inputVO.getCurrentPageIndex()+1, 
		// 	inputVO.getPageCount());
		// outputVO.setIns_outputList(pageList);
		// outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		// outputVO.setTotalPage(pageList.getTotalPage());// 總頁次
		// outputVO.setTotalRecord(pageList.getTotalRecord());// 總筆數

		this.sendRtnObject(outputVO);
	}

	/**
	 * 點擊檢視說明鍵
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException
	 */
	public void btnDescription(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		INS300InputVO inputVO = (INS300InputVO) body;

		try {
		  // 得到檔案
	    GetInsPdfInputVO pdfInputVO = new GetInsPdfInputVO();
	    pdfInputVO.setLstInsProd(Arrays.asList(inputVO.getPrdIDArr()));
	    pdfInputVO.setPdfType("2");
	    GetInsPdfOutputVO pdfOutputVO = insjlb.getInsPdf(pdfInputVO);
	    this.sendRtnObject(pdfOutputVO);
		} catch(Exception e) {
		  throw new APException("ehl_01_common_009"); // 查無資料
		}
		// test 
		// String[] testPdf = {"temp/123.pdf","temp/456.pdf"};
		// int testPdfIndex = 0;
		// for(Map<String, String> test : pdfOutputVO.getLstPdf()){
		//	test.put("URL1", testPdf[testPdfIndex++]);
		// }
	}

	/**
	* 點擊檢視條款鍵
	*/
	@SuppressWarnings("unchecked")
	public void btnTerms(Object body, IPrimitiveMap<Object> header) throws JBranchException, ParseException {
		INS300InputVO inputVO = (INS300InputVO) body;
		try {
  		// 得到檔案
  		GetInsPdfInputVO pdfInputVO = new GetInsPdfInputVO();
  		pdfInputVO.setLstInsProd(Arrays.asList(inputVO.getPrdIDArr()));
  		pdfInputVO.setPdfType("1");
  		GetInsPdfOutputVO pdfOutputVO = insjlb.getInsPdf(pdfInputVO);
  
  		this.sendRtnObject(pdfOutputVO);
  	} catch(Exception e) {
      throw new APException("ehl_01_common_009"); // 查無資料
    }
	}

	/**
	 * 點擊檢視完整內容鍵
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public void btnContent(Object body, IPrimitiveMap<Object> header) throws Exception {
		INS300InputVO inputVO = (INS300InputVO) body;
    try {
  		// 得到檔案
  		GetInsPdfInputVO pdfInputVO = new GetInsPdfInputVO();
  		pdfInputVO.setLstInsProd(Arrays.asList(inputVO.getPrdIDArr()));
  		pdfInputVO.setPdfType("4");
  		GetInsPdfOutputVO pdfOutputVO = insjlb.getInsPdf(pdfInputVO);
  
  		// test 
  		// String[] testPdf = {"temp/123.pdf","temp/456.pdf"};
  		// for(Map<String, String> test : pdfOutputVO.getLstPdf()){
  		// 	test.put("URL1", testPdf[0]);
  		//	test.put("URL2", testPdf[1]);
  		// }
  
  		this.sendRtnObject(pdfOutputVO);
    } catch(Exception e) {
      throw new APException("ehl_01_common_009"); // 查無資料
    }
	}

	/**
	 * 點擊列印鍵
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public void btnPrint(Object body, IPrimitiveMap<Object> header) throws Exception {
		INS300InputVO inputVO = (INS300InputVO) body;

		// 得到檔案
		GetInsPdfInputVO pdfInputVO = new GetInsPdfInputVO();
		pdfInputVO.setLstInsProd(Arrays.asList(inputVO.getPrdIDArr()));
		pdfInputVO.setPdfType("3");
		GetInsPdfOutputVO pdfOutputVO = insjlb.printPdf(this, pdfInputVO);

		//this.sendRtnObject(pdfOutputVO);
	}

	/**
	 * 點擊比較鍵
	 */
	@SuppressWarnings("unchecked")
	public void btnComp (Object body, IPrimitiveMap<Object> header) throws JBranchException, ParseException {
		INS300InputVO inputVO = (INS300InputVO) body;
		
  		// 得到檔案
  		GetInsCompareInputVO pdfInputVO = new GetInsCompareInputVO();
  		pdfInputVO.setLstInsProd(Arrays.asList(inputVO.getPrdIDArr()));
  		String url = insjlb.getInsCompareToIns145Report(pdfInputVO);
  		this.sendRtnObject(url);
	}

	/**
	 * 取得公司名稱
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException
	 */
	public void getComName(Object body, IPrimitiveMap header) throws JBranchException, ParseException{
		INS300OutputVO outputVO = new INS300OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		//==查詢==
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT COM_ID, COM_NAME FROM TBPRD_INSDATA_COMPANY ORDER BY COM_TYPE ");

		//==查詢結果==
		qc.setQueryString(sb.toString());
		outputVO.setIns_outputList(dam.exeQuery(qc));
		this.sendRtnObject(outputVO);
	}
}
