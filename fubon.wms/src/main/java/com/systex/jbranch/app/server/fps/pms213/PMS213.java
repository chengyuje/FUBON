package com.systex.jbranch.app.server.fps.pms213;

import java.io.File;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.6.0 <br>
 * Description : <br>
 * Comments Name : PMS213.java<br>
 * Author :WKK<br>
 * Date :2016年12月5日 <br>
 * Version : 1.01 <br>
 * Editor : WKK<br>
 * Editor Date : 2016年12月5日<br>
 */
@Component("pms213")
@Scope("request")
public class PMS213 extends FubonWmsBizLogic
{
	public DataAccessManager dam = null;

	private Logger logger = LoggerFactory.getLogger(PMS213.class);
	
	private static Map<String, String> typeMap ;
	private static Map<String, String> descMap ;
	
	public void queryData (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		PMS213QueryInputVO inputVO = (PMS213QueryInputVO) body;
		PMS213QueryOutputVO outputVO = new PMS213QueryOutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		int total=0;
		this.queryData(condition, inputVO);
		
		ResultIF list = dam.executePaging(condition,inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
	
	
		if (list.size() > 0) {
			outputVO.setTotalPage(list.getTotalPage());
			outputVO.setOutputLargeAgrList(list);
			outputVO.setTotalRecord(list.getTotalRecord());
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			outputVO.setErrorMessage("");
			total=list.getTotalRecord();
			if(outputVO.getTotalRecord()>200000)
			{
				outputVO.setOutputLargeAgrList( new ArrayList());
				outputVO.setErrorMessage("查詢筆數過多, 請增加查詢條件");
				sendRtnObject(outputVO);
			}
			else
			{
				sendRtnObject(outputVO);
			}
		}
		else 
		{
				throw new APException("ehl_01_common_009");
		}
		
	}

	public void queryData (QueryConditionIF qc, PMS213QueryInputVO inputVO) throws JBranchException, ParseException {
		
		StringBuffer sb = new StringBuffer();
		sb.append("	  SELECT ROWNUM,ALLS.*                                       ");
		sb.append("	  FROM                                                       ");
		sb.append("	  (                                                          ");
		sb.append("	  SELECT PRDADJ.SEQ_NO,PRDADJ.BRH_COD,PRDADJ.YEARMON,        ");
		sb.append("	    PRDADJ.TRANDATE,PRDADJ.CUSTID,PRDADJ.EMP_ID,             ");
		sb.append("	  	PRDADJ.CUSTNAME,PRDADJ.AOCODE,PRDADJ.TRANAOCODE,         ");
		sb.append("	  	PRDADJ.PRODCODE,PRDADJ.PRODTYPE,PRDADJ.PRODNAME,         ");
		sb.append("	    PRDADJ.TYPE_1,PRDADJ.TYPE_2,PRDADJ.TYPE_3,PRDADJ.TYPE_4, ");
		sb.append("	  	PRDADJ.PRODBEN,PRDADJ.TXN_FEE,PRDADJ.PRODREALBEN,        ");
		sb.append("	  	PRDADJ.PRODCNRBEN,PRDADJ.ADJ,PRDADJ.ADJ_TYPE,            ");
		sb.append("	  	PRDADJ.ADJ_DESC,PRDADJ.CNR_PROFIT,PRDADJ.CNR_RATE,       ");
		sb.append("	    REC.BRANCH_NAME,PRDADJ.ADJ_MEMO                          ");
		sb.append("	  FROM TBPMS_PROD_BEN_ADJ PRDADJ                             ");
		sb.append("	  LEFT JOIN TBPMS_ORG_REC_N REC                              ");
		sb.append("	  ON PRDADJ.BRH_COD=REC.BRANCH_NBR                           ");
		sb.append("	  AND TO_DATE(PRDADJ.YEARMON || '01','YYYYMMDD')             ");
		sb.append("	  BETWEEN REC.START_TIME                                     ");
		sb.append("	  AND REC.END_TIME                                           ");
		sb.append("	  WHERE  PRDADJ.YEARMON = :YEARMON                           ");


		/* 根據用戶輸入的條件查詢 */
		if(null != inputVO.getCustId() && !("".equals(inputVO.getCustId())))
		{
			sb.append("		AND  PRDADJ.CUSTID = :CUSTID               ");
			qc.setObject("CUSTID", inputVO.getCustId());
		}
		if(null != inputVO.getAOCODE() && !("".equals(inputVO.getAOCODE())))
		{
			sb.append("    AND PRDADJ.AOCODE = :AOCODE ");
			qc.setObject("AOCODE", inputVO.getAOCODE());
		}
		if(null != inputVO.getAdjType() && !("".equals(inputVO.getAdjType())))
		{
			sb.append("    AND PRDADJ.ADJ_TYPE = :ADJ_TYPE ");
			qc.setObject("ADJ_TYPE", inputVO.getAdjType());
		}
		if(null != inputVO.getProdCode() && !("".equals(inputVO.getProdCode())))
		{
			sb.append("    AND PRDADJ.PRODCODE = :PRODCODE ");
			qc.setObject("PRODCODE", inputVO.getProdCode());
		}
		if(null != inputVO.getAdjDesc() && !("".equals(inputVO.getAdjDesc())))
		{
			sb.append("    AND PRDADJ.ADJ_DESC = :ADJ_DESC ");
			qc.setObject("ADJ_DESC", inputVO.getAdjDesc());
		}
		// 員編 2017/06/13 新增員邊查詢功能
		if (StringUtils.isNotBlank(inputVO.getEmp_id())) 
		{		
			sb.append(" and PRDADJ.EMP_ID = :EMP_ID ");
			qc.setObject("EMP_ID", inputVO.getEmp_id());				
		}
		//  2017/06/13 新增分行查詢功能
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) 
		{		
			sb.append(" and PRDADJ.BRH_COD = :BRH_COD ");
			qc.setObject("BRH_COD", inputVO.getBranch_nbr());
		}
		//PRODTYPE 產品類別
		if (StringUtils.isNotBlank(inputVO.getPRODTYPE())) 
		{		
			sb.append(" and PRDADJ.PRODTYPE = :PRODTYPE ");
			qc.setObject("PRODTYPE", inputVO.getPRODTYPE());
		}
		
		sb.append("		ORDER BY PRDADJ.YEARMON, PRDADJ.PRODTYPE, PRDADJ.CUSTID, PRDADJ.TRANDATE, PRDADJ.ADJ_TYPE, PRDADJ.SEQ_NO");
		sb.append(" ) ALLS ");			
		//比數限制暫時無用
//		sb.append(" WHERE ROWNUM <=:QRY_MAX_LIMIT ");
		//薪資兩萬筆限制
//		qc.setObject("QRY_MAX_LIMIT", "20000");
		qc.setObject("YEARMON", inputVO.getYearMon());
		qc.setQueryString(sb.toString());
	}

//	/**
//	 * 查詢
//	 * 
//	 * @param body
//	 * @param header
//	 * @throws JBranchException
//	 */
//	public void queryData(Object body, IPrimitiveMap header) throws JBranchException
//	{
//		PMS213QueryInputVO inputVO = (PMS213QueryInputVO) body;
//		PMS213QueryOutputVO outputVO = new PMS213QueryOutputVO();
//		int totalRecord_i=0;
//		try
//		{
//			// 筆數限制 2017/06/13 新增功能
//			XmlInfo xmlInfo = new XmlInfo();
//			Map<String, String> qry_max_limit_xml = xmlInfo.doGetVariable("SYS.MAX_QRY_ROWS", FormatHelper.FORMAT_2);
//			
//			dam = this.getDataAccessManager();
//			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//			StringBuffer sb = new StringBuffer();
//			sb.append("	  SELECT ROWNUM,ALLS.*                                       ");
//			sb.append("	  FROM                                                       ");
//			sb.append("	  (                                                          ");
//			sb.append("	  SELECT PRDADJ.SEQ_NO,PRDADJ.BRH_COD,PRDADJ.YEARMON,        ");
//			sb.append("	    PRDADJ.TRANDATE,PRDADJ.CUSTID,                           ");
//			sb.append("	  	PRDADJ.CUSTNAME,PRDADJ.AOCODE,PRDADJ.TRANAOCODE,         ");
//			sb.append("	  	PRDADJ.PRODCODE,PRDADJ.PRODTYPE,PRDADJ.PRODNAME,         ");
//			sb.append("	    PRDADJ.TYPE_1,PRDADJ.TYPE_2,PRDADJ.TYPE_3,PRDADJ.TYPE_4, ");
//			sb.append("	  	PRDADJ.PRODBEN,PRDADJ.PRODREALBEN,PRDADJ.PRODCNRBEN,     ");
//			sb.append("	  	PRDADJ.ADJ,PRDADJ.ADJ_TYPE,PRDADJ.ADJ_DESC,              ");
//			sb.append("	    REC.BRANCH_NAME                                          ");
//			sb.append("	  FROM TBPMS_PROD_BEN_ADJ PRDADJ                             ");
//			sb.append("	  LEFT JOIN TBPMS_ORG_REC_N REC                              ");
//			sb.append("	  ON PRDADJ.BRH_COD=REC.BRANCH_NBR                           ");
//			sb.append("	  AND TO_DATE(PRDADJ.YEARMON || '01','YYYYMMDD')             ");
//			sb.append("	  BETWEEN REC.START_TIME                                     ");
//			sb.append("	  AND REC.END_TIME                                           ");
//			sb.append("	  WHERE  PRDADJ.YEARMON = :YEARMON                           ");
//
//
//			/* 根據用戶輸入的條件查詢 */
//			if(null != inputVO.getCustId() && !("".equals(inputVO.getCustId())))
//			{
//				sb.append("		AND  PRDADJ.CUSTID = :CUSTID               ");
//				qc.setObject("CUSTID", inputVO.getCustId());
//			}
//			if(null != inputVO.getAOCODE() && !("".equals(inputVO.getAOCODE())))
//			{
//				sb.append("    AND PRDADJ.AOCODE = :AOCODE");
//				qc.setObject("AOCODE", inputVO.getAOCODE());
//			}
//			if(null != inputVO.getAdjType() && !("".equals(inputVO.getAdjType())))
//			{
//				sb.append("    AND PRDADJ.ADJ_TYPE = :ADJ_TYPE");
//				qc.setObject("ADJ_TYPE", inputVO.getAdjType());
//			}
//			if(null != inputVO.getProdCode() && !("".equals(inputVO.getProdCode())))
//			{
//				sb.append("    AND PRDADJ.PRODCODE = :PRODCODE");
//				qc.setObject("PRODCODE", inputVO.getProdCode());
//			}
//			if(null != inputVO.getAdjDesc() && !("".equals(inputVO.getAdjDesc())))
//			{
//				sb.append("    AND PRDADJ.ADJ_DESC = :ADJ_DESC");
//				qc.setObject("ADJ_DESC", inputVO.getAdjDesc());
//			}
//			// 員編 2017/06/13 新增員邊查詢功能
//			if (StringUtils.isNotBlank(inputVO.getEmp_id())) 
//			{		
//				sb.append("and PRDADJ.EMP_ID = :EMP_ID ");
//				qc.setObject("EMP_ID", inputVO.getEmp_id());				
//			}
//			//  2017/06/13 新增分行查詢功能
//			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) 
//			{		
//				sb.append("and PRDADJ.BRH_COD = :BRH_COD ");
//				qc.setObject("BRH_COD", inputVO.getBranch_nbr());
//			}
//			
//			sb.append("		ORDER BY PRDADJ.YEARMON, PRDADJ.PRODTYPE, PRDADJ.SEQ_NO, PRDADJ.TRANDATE, PRDADJ.CUSTID, PRDADJ.ADJ_TYPE  ");
//			sb.append(" ) ALLS ");			
//			//比數限制暫時無用
////			sb.append(" WHERE ROWNUM <=:QRY_MAX_LIMIT ");
//			//薪資兩萬筆限制
////			qc.setObject("QRY_MAX_LIMIT", "20000");
//			qc.setObject("YEARMON", inputVO.getYearMon());
//			qc.setQueryString(sb.toString());
//
//			ResultIF list = dam.executePaging(qc, inputVO
//					.getCurrentPageIndex() + 1, inputVO.getPageCount());
//			int totalPage_i = list.getTotalPage();
//			totalRecord_i = list.getTotalRecord();
//			//List<Map<String, Object>> result = dam.exeQuery(qc);
//			outputVO.setOutputLargeAgrList(list);
//			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());  // 當前頁次
//			outputVO.setTotalPage(totalPage_i);                           // 總頁次
//			outputVO.setTotalRecord(totalRecord_i);                       // 總筆數
//			
//			if(totalRecord_i > 20000)
//			{
//				throw new APException("查詢筆數過多, 請增加查詢條件");
//			}
//			else
//			{
//				sendRtnObject(outputVO);
//			}
//			
//		}
//		catch (Exception e)
//		{
//			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
//			if(totalRecord_i < 20000)
//				throw new APException("系統發生錯誤請洽系統管理員");
//			else
//				throw new APException("查詢筆數過多, 請增加查詢條件");
//		}
//	}

	/**
	 * 由原始資料新增的數據作插入操作
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void insertData(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS213UpdateInputVO inputVO = (PMS213UpdateInputVO) body;
		PMS213QueryOutputVO outputVO = new PMS213QueryOutputVO();
		try
		{
			dam = this.getDataAccessManager();

			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			// 將數據填入臨時表
			sb.append("     INSERT INTO TBPMS_PROD_BEN_ADJ                         ");
			sb.append("     	(SEQ_NO,BRH_COD,YEARMON,TRANDATE,CUSTID,CUSTNAME,  ");
			sb.append("     	AOCODE,TRANAOCODE,PRODCODE,PRODTYPE,PRODNAME,      ");
			sb.append("     	TYPE_1,TYPE_2,TYPE_3,TYPE_4,     				   ");
			sb.append("     	PRODBEN,TXN_FEE,PRODREALBEN,PRODCNRBEN,            ");
			sb.append("     	ADJ,ADJ_TYPE,ADJ_DESC,                             ");
			sb.append("     	VERSION,CREATETIME,CREATOR,MODIFIER,LASTUPDATE     ");
			sb.append("     	)                                                  ");
			sb.append("     VALUES                                                 ");
			sb.append("     	(                                                  ");
			sb.append("     	PABTH_BTPMS715.FN_GET_TZKHCPSY_NEXT_SEQ( :SEQ_NO ),");
			sb.append("     	:BRH_COD,                                          ");
			sb.append("     	:YEARMON,                                          ");
			sb.append("     	:TRANDATE,                                         ");
			sb.append("     	:CUSTID,                                           ");
			sb.append("     	:CUSTNAME,                                         ");
			sb.append("     	:AOCODE,                                           ");
			sb.append("     	:TRANAOCODE,                                       ");
			sb.append("     	:PRODCODE,                                         ");
			sb.append("     	:PRODTYPE,                                         ");
			sb.append("     	:PRODNAME,                                         ");
			sb.append("     	:TYPE_1,                                       	   ");
			sb.append("     	:TYPE_2,                                           ");
			sb.append("     	:TYPE_3,                                      	   ");
			sb.append("     	:TYPE_4,                                       	   ");
			sb.append("     	:PRODBEN,                                          ");
			sb.append("     	:TXN_FEE,                                          ");
			sb.append("     	:PRODREALBEN,                                      ");
			sb.append("     	:PRODCNRBEN,                                       ");
			sb.append("     	:ADJ,                                              ");
			sb.append("     	:ADJ_TYPE,                                         ");
			sb.append("     	:ADJ_DESC,                                         ");
			sb.append("     	0,                                                 ");
			sb.append("     	sysdate,                                           ");
			sb.append("     	:userId,                                           ");
			sb.append("     	:userId,                                           ");
			sb.append("     	sysdate                                            ");
			sb.append("     	)                                                  ");

			// 插入操作
			qc.setObject("SEQ_NO", inputVO.getInputList().get(0).get("SEQ_NO"));
			qc.setObject("BRH_COD", inputVO.getInputList().get(0).get("BRH_COD"));
			qc.setObject("YEARMON", inputVO.getInputList().get(0).get("YEARMON"));
			qc.setObject("TRANDATE", inputVO.getInputList().get(0).get("TRANDATE"));
			qc.setObject("CUSTID", inputVO.getInputList().get(0).get("CUSTID"));
			qc.setObject("CUSTNAME", inputVO.getInputList().get(0).get("CUSTNAME"));
			qc.setObject("AOCODE", inputVO.getInputList().get(0).get("AOCODE"));
			qc.setObject("TRANAOCODE", inputVO.getInputList().get(0).get("TRANAOCODE"));
			qc.setObject("PRODCODE", inputVO.getInputList().get(0).get("PRODCODE"));
			qc.setObject("PRODTYPE", inputVO.getInputList().get(0).get("PRODTYPE"));
			qc.setObject("PRODNAME", inputVO.getInputList().get(0).get("PRODNAME"));
			qc.setObject("TYPE_1", inputVO.getInputList().get(0).get("TYPE_1"));
			qc.setObject("TYPE_2", inputVO.getInputList().get(0).get("TYPE_2"));
			qc.setObject("TYPE_3", inputVO.getInputList().get(0).get("TYPE_3"));
			qc.setObject("TYPE_4", inputVO.getInputList().get(0).get("TYPE_4"));
			qc.setObject("PRODBEN", inputVO.getInputList().get(0).get("PRODBEN"));
			qc.setObject("TXN_FEE", inputVO.getInputList().get(0).get("TXN_FEE"));
			qc.setObject("PRODREALBEN", inputVO.getInputList().get(0).get("PRODREALBEN"));
			qc.setObject("PRODCNRBEN", inputVO.getInputList().get(0).get("PRODCNRBEN"));
			qc.setObject("ADJ", inputVO.getInputList().get(0).get("ADJ"));
			qc.setObject("ADJ_TYPE", inputVO.getInputList().get(0).get("ADJ_TYPE"));
			qc.setObject("ADJ_DESC", inputVO.getInputList().get(0).get("ADJ_DESC"));
			qc.setObject("userId", inputVO.getUserId());

			qc.setQueryString(sb.toString());
			int result = dam.exeUpdate(qc);
			sendRtnObject(result);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}

	/**
	 * 由人工調整修改的數據作更新操作
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void updateData(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS213UpdateInputVO inputVO = (PMS213UpdateInputVO) body;
		try
		{
			dam = this.getDataAccessManager();
			
			//更新，將ADJ設置為0
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("     UPDATE TBPMS_PROD_BEN_ADJ  M          ");
			sb.append("     SET M.ADJ = ''            		 	  ");
			sb.append("     	, M.LASTUPDATE = sysdate          ");
			sb.append("     	, M.VERSION =  M.VERSION + 1      ");
			sb.append("     	, M.MODIFIER = :userId            ");
			sb.append("     WHERE M.SEQ_NO = :SEQ_NO              ");

			qc.setObject("SEQ_NO", inputVO.getInputList().get(0).get("SEQ_NO"));
			/*qc.setObject("YEARMON", inputVO.getInputList().get(0).get("YEARMON"));
			qc.setObject("AOCODE", inputVO.getInputList().get(0).get("AOCODE"));
			qc.setObject("PRODBEN", inputVO.getInputList().get(0).get("PRODBEN"));
			qc.setObject("PRODREALBEN", inputVO.getInputList().get(0).get("PRODREALBEN"));
			qc.setObject("ADJ_DESC", inputVO.getInputList().get(0).get("ADJ_DESC"));
			qc.setObject("ADJ", inputVO.getInputList().get(0).get("ADJ"));
			qc.setObject("PRODCNRBEN", inputVO.getInputList().get(0).get("PRODCNRBEN"));*/
			qc.setObject("userId", inputVO.getUserId());
			qc.setQueryString(sb.toString());
			int result = dam.exeUpdate(qc);
			inputVO.getInputList().get(0).put("ADJ", "V");
			insertData(body, header);
			//sendRtnObject(result);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}

	/**
	 * 刪除數據
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void delData(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS213UpdateInputVO inputVO = (PMS213UpdateInputVO) body;
		try
		{
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("  DELETE FROM TBPMS_PROD_BEN_ADJ    ");
			sb.append("  WHERE SEQ_NO = :SEQ_NO    ");

			qc.setObject("SEQ_NO", inputVO.getSeqNo());
			qc.setQueryString(sb.toString());
			int result = dam.exeUpdate(qc);
			sendRtnObject(result);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/**
	 * 上傳功能，上傳為人工調整軌跡
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void addBenAdj(Object body, IPrimitiveMap header) throws JBranchException
	{
		int flag = 0;
		PMS213UpdateInputVO inputVO = (PMS213UpdateInputVO) body;
		try
		{
			dam = this.getDataAccessManager();
			List<String> list = new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			// 有表頭.xls文檔
			// 清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			String dsql = " TRUNCATE TABLE TBPMS_PROD_BEN_ADJ_U ";
			dcon.setQueryString(dsql.toString());
			dam.exeUpdate(dcon);
			
			//清空異常數據表
			String delT = " DELETE FROM TBPMS_PROD_BEN_ADJ_T WHERE YEARMON = :YEARMON";
			QueryConditionIF dcon1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			dcon1.setObject("YEARMON", inputVO.getYearMon());
			dcon1.setQueryString(delT);
			dam.exeUpdate(dcon1);
			String lab = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			for (int a = 0; a < sheet.length; a++)
			{
				for (int i = 1; i < sheet[a].getRows(); i++)
				{
					for (int j = 0; j < sheet[a].getColumns(); j++)
					{
						if(j<14){
							lab = sheet[a].getCell(j, i).getContents();
							if(j<14 && (null==lab || "".equals(lab)) ){
								flag++;
								throw new APException("資料上傳失敗,錯誤發生在第" + flag + "筆,傳入空值");
							}
							list.add(lab);
						}
					}

					// excel表格記行數
					flag++;

					// 判斷當前上傳數據月份是否一致
					/*
					 * if(!list.get(0).equals(inputVO.getYearMon())){ throw new
					 * APException("上傳數據選擇月份不一致"); }
					 */

					// 判斷當前上傳數據欄位個數是否一致
					if(list.size() != 14)
					{
						throw new APException("資料上傳失敗,錯誤發生在第" + flag + "筆,上傳數據欄位個數不一致");
					}

					String trandate = (String) list.get(1).trim();
					String custId = (String) list.get(2);
					for (int i1 = custId.length(); i1 < 20; i1++)
						custId = '0' + custId;
					String prodCode = (String) list.get(6);
					for (int i1 = prodCode.length(); i1 < 10; i1++)
						prodCode = '0' + prodCode;
					String timeNow = sdf.format(new Date()) + i;
					String end = "30"+i;
					String SEQ_NO = trandate + custId + prodCode + "000000" + timeNow + end;
					// SQL指令
					StringBuffer sb = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("   INSERT INTO TBPMS_PROD_BEN_ADJ_U (SEQ_NO,	         ");
					sb.append("  		YEARMON,            				         ");
					sb.append("  		TRANDATE,           					     ");
					sb.append("  		CUSTID,            					         ");
					sb.append("  		CUSTNAME,            			             ");
					sb.append("  		AOCODE,            					         ");
					sb.append("  		TRANAOCODE,            					     ");
					sb.append("  		PRODCODE,            					     ");
					sb.append("  		PRODTYPE,            				         ");
					sb.append("  		PRODNAME,            					     ");
					sb.append("  		PRODBEN,            					     ");
					sb.append("  		TXN_FEE,            					     ");
					sb.append("  		PRODREALBEN,            					 ");
					sb.append("  		PRODCNRBEN,            					     ");
					sb.append("  		ADJ,            					         ");
					sb.append("  		ADJ_TYPE,            					     ");
					sb.append("  		ADJ_DESC,            					     ");
					sb.append("  		RNUM,            					         ");
					sb.append("  		VERSION,            						 ");
					sb.append("  		CREATETIME,             					 ");
					sb.append("  		CREATOR,             						 ");
					sb.append("  		MODIFIER,         						     ");
					sb.append("  		LASTUPDATE )             					 ");
					sb.append("  	VALUES(:SEQ_NO,            				         ");
					sb.append("  		:YEARMON,             				         ");
					sb.append("  		:TRANDATE,             				         ");
					sb.append("  		:CUSTID,             				         ");
					sb.append("  		:CUSTNAME,             			             ");
					sb.append("  		:AOCODE,             					     ");
					sb.append("  		:TRANAOCODE,             					 ");
					sb.append("  		:PRODCODE,             					     ");
					sb.append("  		:PRODTYPE,             				         ");
					sb.append("  		:PRODNAME,             					     ");
					sb.append("  		:PRODBEN,             					     ");
					sb.append("  		:TXN_FEE,             					     ");
					sb.append("  		:PRODREALBEN,             					 ");
					sb.append("  		:PRODCNRBEN,             					 ");
					sb.append("  		:ADJ,             					         ");
					sb.append("  		:ADJ_TYPE,             					     ");
					sb.append("  		:ADJ_DESC,             					     ");
					sb.append("  		:RNUM,             					         ");
					sb.append("  		:VERSION,           					     ");
					sb.append("  		SYSDATE,           				             ");
					sb.append("  		:CREATOR,            					     ");
					sb.append("  		:MODIFIER,         					         ");
					sb.append("  		SYSDATE)          				             ");
					qc.setObject("SEQ_NO", "");
					qc.setObject("YEARMON", list.get(0).trim());
					qc.setObject("TRANDATE", trandate.trim());
					qc.setObject("CUSTID", list.get(2).trim());
					qc.setObject("CUSTNAME", list.get(3).trim());
					qc.setObject("AOCODE", list.get(4).trim());
					qc.setObject("TRANAOCODE", list.get(5).trim());
					qc.setObject("PRODCODE", list.get(6).trim());
					qc.setObject("PRODTYPE", list.get(7).trim());
					qc.setObject("PRODNAME", list.get(8).trim());
					qc.setObject("PRODBEN", list.get(9).trim());
					qc.setObject("TXN_FEE", list.get(10).trim());
					qc.setObject("PRODREALBEN", list.get(11).trim());
					qc.setObject("PRODCNRBEN", list.get(12).trim());
					qc.setObject("ADJ", "V");
					qc.setObject("ADJ_TYPE", "3");
					qc.setObject("ADJ_DESC", list.get(13).trim());
					qc.setObject("RNUM", flag);
					qc.setObject("VERSION", "0");
					qc.setObject("CREATOR", inputVO.getUserId());
					qc.setObject("MODIFIER", inputVO.getUserId());
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
					list.clear();
				}
			}

			sendRtnObject(null);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}

	/**
	 * 調用存儲過程
	 * 
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings(
	{ "unused", "rawtypes" })
	public void callStored(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS213UpdateInputVO inputVO = (PMS213UpdateInputVO) body;
			PMS213QueryOutputVO outputVO = new PMS213QueryOutputVO();
			// 執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS715.SP_TBPMS_PROD_BEN_ADJ(? ,? ) ");
			qc.setString(1, inputVO.getYearMon());
			qc.registerOutParameter(2, Types.VARCHAR);
			qc.setQueryString(sb.toString());
			Map<Integer, Object> resultMap = dam.executeCallable(qc);
			String str = (String) resultMap.get(2);
			String[] strs = null;
			if(str != null)
			{
				strs = str.split("；");
				if(strs != null && strs.length > 5)
				{
					str = strs[0] + "；" + strs[1] + "；" + strs[2] + "；" + strs[3] + "；" + strs[4] + "...等";
				}
			}
			outputVO.setErrorMessage(str);
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}

	}
	/**
	 * 匯出EXCLE
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void export(Object body, IPrimitiveMap header) throws JBranchException 
	{
		PMS213QueryInputVO inputVO = (PMS213QueryInputVO) body;
		PMS213QueryOutputVO outputVO = new PMS213QueryOutputVO();
		//查詢匯出數據
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("  	SELECT	ADJ_T.YEARMON    ,                               ");
		sb.append("  			ADJ_T.TRANDATE   ,                     			 ");
		sb.append("  			ADJ_T.CUSTID     ,                     			 ");
		sb.append("  			ADJ_T.CUSTNAME   ,                     			 ");
		sb.append("  			ADJ_T.AOCODE     ,                     			 ");
		sb.append("  			ADJ_T.TRANAOCODE ,                     			 ");
		sb.append("  			ADJ_T.PRODCODE   ,                     			 ");
		sb.append("  			ADJ_T.PRODTYPE   ,                     			 ");
		sb.append("  			ADJ_T.PRODNAME   ,                     			 ");
		sb.append("  			ADJ_T.PRODBEN    ,                     			 ");
		sb.append("  			ADJ_T.TXN_FEE    ,                     			 ");
		sb.append("  			ADJ_T.PRODREALBEN,                     			 ");
		sb.append("  			ADJ_T.PRODCNRBEN ,                     			 ");
		sb.append("  			ADJ_T.ADJ_DESC    ,                    			 ");
		sb.append("  			ADJ_T.ADJ_MEMO    ,                    			 ");
		sb.append("             ORG.BRANCH_NAME   ,                    			 ");
		sb.append("             ORG.BRANCH_NBR    ,                    			 ");
		sb.append("             REC.EMP_ID                             			 ");
		sb.append("    FROM TBPMS_PROD_BEN_ADJ_T ADJ_T                 			 ");
		sb.append("    INNER JOIN TBPMS_SALES_AOCODE_M REC             			 ");
		sb.append("         ON ADJ_T.AOCODE=REC.AO_CODE                			 ");
		sb.append("         AND LAST_DAY(TO_DATE(ADJ_T.YEARMON,'YYYYMM'))        ");
		sb.append("             BETWEEN REC.START_TIME AND REC.END_TIME          ");
		sb.append("         AND ADJ_T.YEARMON = REC.YEARMON                  	 ");
		sb.append("    LEFT JOIN TBPMS_ORG_REC_N ORG                             ");
		sb.append("         ON ORG.DEPT_ID = REC.DEPT_ID                         ");
		sb.append("         AND LAST_DAY(TO_DATE(ADJ_T.YEARMON,'YYYYMM'))        ");
		sb.append("             BETWEEN REC.START_TIME AND REC.END_TIME          ");
		sb.append("    WHERE ADJ_T.YEARMON = :YEARMON                            ");
		qc.setObject("YEARMON",(String)inputVO.getYearMon() );
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(qc);
		//List<Map<String, Object>> list = return_VO2.getCsvList();
		if(list.size() > 0)
		{			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = "調整客戶產品收益據表_" + sdf.format(new Date()) + ".csv"; 
			List listCSV =  new ArrayList();
			for(Map<String, Object> map : list){
				String[] records = new String[18];
				int i = 0;
				records[i] = checkIsNull(map, "YEARMON");                     //計績年月
				records[++i] = checkIsNull(map, "BRANCH_NBR");                //分行
				records[++i] = checkIsNull(map, "BRANCH_NAME");               //分行名稱
				records[++i] = checkIsNull(map, "EMP_ID"); 					  //員編
				records[++i] = checkIsNull(map, "CUSTID");                 	  //客戶ID				
				records[++i] = checkIsNull(map, "TRANDATE");                  //交易日期
				records[++i] = checkIsNull(map, "CUSTNAME");                  //客戶姓名
				records[++i] = checkIsNull(map, "AOCODE");                 	  //AOCODE
				records[++i] = checkIsNull(map, "TRANAOCODE");                //交易理專
				records[++i] = checkIsNull(map, "PRODCODE");                  //產品代號
				records[++i] = checkIsNull(map, "PRODTYPE");         		  //產品類別
				records[++i] = checkIsNull(map, "PRODNAME");            	  //商品名稱
				records[++i] = checkIsNull(map, "PRODBEN");            		  //產品銷量/調整數
				records[++i] = checkIsNull(map, "TXN_FEE");            		  //手續費
				records[++i] = checkIsNull(map, "PRODREALBEN");          	  //產品實際收益
				records[++i] = checkIsNull(map, "PRODCNRBEN");                //產品CNR收益
				records[++i] = descFormat(map, "ADJ_DESC");                  //人工調整說明
				records[++i] = descFormat(map, "ADJ_DEMO");                  //調整備註
				listCSV.add(records);
			}
			//header
			String [] csvHeader = new String[18];
			int j = 0;
			csvHeader[j] = "計績年月";
			csvHeader[++j] = "分行代碼";
			csvHeader[++j] = "分行名稱";
			csvHeader[++j] = "員編";
			csvHeader[++j] = "客戶ID";
			csvHeader[++j] = "交易日期";
			csvHeader[++j] = "客戶姓名";
			csvHeader[++j] = "AOCODE";
			csvHeader[++j] = "交易理專";
			csvHeader[++j] = "產品代號";
			csvHeader[++j] = "產品類別";
			csvHeader[++j] = "商品名稱";
			csvHeader[++j] = "產品銷量/調整數";
			csvHeader[++j] = "手續費/調整數";
			csvHeader[++j] = "產品實際收益/調整數";
			csvHeader[++j] = "產品CNR收益/調整數";
			csvHeader[++j] = "人工調整說明";
			csvHeader[++j] = "調整備註";
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			notifyClientToDownloadFile(url, fileName);
			this.sendRtnObject(null);
		} else 
		{
			outputVO.setOutputLargeAgrList(list);
			this.sendRtnObject(outputVO);
	    }
	}
	
	
	/**
	 * 匯出主檔EXCLE
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	
	/* === 產出Excel==== */
	public void exportMast (Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		PMS213QueryInputVO inputVO = (PMS213QueryInputVO) body;
		PMS213QueryOutputVO outputVO = new PMS213QueryOutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		this.queryData(condition, inputVO);

		List<Map<String, Object>> list = dam.exeQuery(condition);
		if(list.size()>200000)
		{	outputVO.setOutputLargeAgrList( new ArrayList());
			outputVO.setErrorMessage("匯出筆數過多, 請增加查詢條件");
			sendRtnObject(outputVO);
//		
		}
		else
		{	

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String fileName = "調整客戶產品收益報表_" + sdf.format(new Date()) + "-" + getUserVariable(FubonSystemVariableConsts.LOGINID) + ".csv";
			List listCSV = new ArrayList();
			int a = 1;
			for (Map<String, Object> map : list) {
				String[] records = new String[32];
				int i = 0;
				records[i]=checkIsNullAndTrans(map,"SEQ_NO");            //"序列號"
				records[++i]=checkIsNull(map,"YEARMON");           //"計績年月"
				records[++i]=checkIsNull(map,"TRANDATE");          //"交易日期"
				records[++i]=checkIsNullAndTrans(map,"CUSTID");            //"客戶ID"
				records[++i]=checkIsNull(map,"CUSTNAME");          //"客戶姓名"
				records[++i]=checkIsNullAndTrans(map,"AOCODE");            //"AOCODE"
				records[++i]=checkIsNull(map,"BRH_COD");           //"分行"
				records[++i]=checkIsNull(map,"BRANCH_NAME");           //"分行"
				records[++i]=checkIsNullAndTrans(map,"EMP_ID");            //"交易理專ID"
				records[++i]=checkIsNull(map,"TRANAOCODE");        //"交易理專"
				records[++i]=checkIsNull(map,"PRODCODE");          //"產品代號"
				records[++i]=checkIsNull(map,"PRODTYPE");          //"產品類別"
				records[++i]=checkIsNull(map,"TYPE_1");            //"類型1"
				records[++i]=checkIsNull(map,"TYPE_2");            //"類型2"
				records[++i]=checkIsNull(map,"TYPE_3");            //"類型3"
				records[++i]=checkIsNull(map,"TYPE_4");            //"類型4"
				records[++i]=checkIsNull(map,"PRODNAME");          //"商品名稱"
				records[++i]=checkIsNull(map,"PRODBEN");           //"產品銷量/調整數"
				records[++i]=checkIsNull(map,"TXN_FEE");           //"產品銷量/調整數"
				records[++i]=checkIsNull(map,"PRODREALBEN");       //"產品實際收益"
				records[++i]=checkIsNull(map,"PRODCNRBEN");        //"產品CNR收益"
				records[++i]=checkIsNull(map,"ADJ");               //"調整"
				records[++i]=typeFormat(map,"ADJ_TYPE");          //"軌跡類型0：原始數據1：人工調整2：系統調整"
				records[++i]=descFormat(map,"ADJ_DESC");          //"調整類別"
				records[++i]=checkIsNull(map,"ADJ_MEMO");          //"調整備註"
				records[++i]=checkIsNull(map,"CNR_PROFIT");        //"CNR收益"
				records[++i]=checkIsNull(map,"CNR_RATE");          //"CNR收益率"
			
				listCSV.add(records);
			}
			
			// header
			String[] csvHeader = new String[32];
			int j = 0;
			csvHeader[j]="序列號";
			csvHeader[++j]="計績年月";
			csvHeader[++j]="交易日期";
			csvHeader[++j]="客戶ID";
			csvHeader[++j]="客戶姓名";
			csvHeader[++j]="AOCODE";
			csvHeader[++j]="分行代碼";
			csvHeader[++j]="分行名稱";
			csvHeader[++j]="交易理專ID";
			csvHeader[++j]="交易理專";
			csvHeader[++j]="產品代號";
			csvHeader[++j]="產品類別";
			csvHeader[++j]="類型1";
			csvHeader[++j]="類型2";
			csvHeader[++j]="類型3";
			csvHeader[++j]="類型4";
			csvHeader[++j]="商品名稱";
			csvHeader[++j]="產品銷量/調整數";
			csvHeader[++j]="手續費/調整數";
			csvHeader[++j]="產品實際收益/調整數";
			csvHeader[++j]="產品CNR收益/調整數";
			csvHeader[++j]="調整";
			csvHeader[++j]="軌跡類型0原始數據1：人工調整2：系統調整";
			csvHeader[++j]="調整類別";
			csvHeader[++j]="調整備註";
			csvHeader[++j]="CNR收益";
			csvHeader[++j]="CNR收益率";
	
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			notifyClientToDownloadFile(url, fileName);
			this.sendRtnObject(null);
		}
	}
	/**
	* 檢查Map取出欄位是否為Null
	* 
	* @param map
	* @return String
	*/
	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf(map.get(key));
		}else{
			return "";
		}
	}

	/**
	* 日期格式轉換
	* 
	* @param map
	* @return String
	*/

	private String dateFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			SimpleDateFormat sdfd = new SimpleDateFormat("yyyyMMdd HH:mm");
			return sdfd.format(map.get(key));
		} else
			return "";
	}
	
	/**
	* 檢查Map取出欄位是否為Null
	* @param map
	* @return String
	*/
	private String checkIsNullAndTrans(Map map, String key) 
	{
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf("=\""+map.get(key)+"\"");
		}else{
			return "";
		}
	}
	
	
	/**
	* 軌跡類型下拉框格式轉換
	*  ADJ_TYPE 
	* @param map
	* @return String
	 * @throws JBranchException 
	*/
	
	private String typeFormat(Map map, String key) throws JBranchException {		
		if(typeMap==null){
			XmlInfo xmlInfo = new XmlInfo();
			typeMap = xmlInfo.doGetVariable("PMS.CNR_ADJ_TYPE", FormatHelper.FORMAT_3);     //軌跡類型	
		}			
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			String type = ""; 
			if(map.get(key)!=null)
				type=typeMap.get(map.get(key)+"");		
			return type;
		} else
			return "";
	}	
	/**
	* 軌跡類型下拉框格式轉換
	*  ADJ_DESC 
	* @param map
	* @return String
	 * @throws JBranchException 
	*/
	private String descFormat(Map map, String key) throws JBranchException {		
		if(descMap==null){
			XmlInfo xmlInfo = new XmlInfo();
			descMap = xmlInfo.doGetVariable("PMS.CNR_SYS_ADJ_DESC", FormatHelper.FORMAT_3);     //軌跡類型	
		}	
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			String type = ""; 
			if(map.get(key)!=null)
				type=descMap.get(map.get(key)+"");		
			return type;
		} else
			return "";
	}	
	

}
