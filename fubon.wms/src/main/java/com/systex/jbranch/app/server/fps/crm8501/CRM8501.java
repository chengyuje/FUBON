package com.systex.jbranch.app.server.fps.crm8501;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_ASSET_DOCVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_ASSET_PRINT_FILEVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_NOTEVO;
import com.systex.jbranch.app.server.fps.crm211.CRM211OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * crm8501
 * 
 * @author James
 * @date 2017/10/12
 * @spec null
 */
@Component("crm8501")
@Scope("request")
public class CRM8501 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM8501.class);

	//將首次列印實體PDF寫入 TBCRM_CUST_ASSET_PRINT_FILE
	private void updatePrintFile(String url, BigDecimal seq) throws Exception {
		
		String serverPath = (String) SysInfo.getInfoValue(SystemVariableConsts.SERVER_PATH);
		byte[] reportData = Files.readAllBytes(new File(serverPath, url).toPath());
		TBCRM_CUST_ASSET_PRINT_FILEVO rvo = (TBCRM_CUST_ASSET_PRINT_FILEVO) dam.findByPKey(TBCRM_CUST_ASSET_PRINT_FILEVO.TABLE_UID, seq);
		//只記首次
		if (rvo == null) {
			rvo = new TBCRM_CUST_ASSET_PRINT_FILEVO();
			rvo.setSEQ(seq);
			rvo.setFILE_DOC(ObjectUtil.byteArrToBlob(reportData));
			dam.create(rvo);
		} else {
			rvo.setFILE_DOC(ObjectUtil.byteArrToBlob(reportData));
			dam.update(rvo);
		}
	}

	/**
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void applyPrint(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM8501InputVO inputVO = (CRM8501InputVO) body;
		CRM8501OutputVO outputVO = this.applyPrint(inputVO);
		
		this.sendRtnObject(outputVO);
	}

	public CRM8501OutputVO applyPrint(CRM8501InputVO inputVO) throws JBranchException {
		
		CRM8501OutputVO outputVO = new CRM8501OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = null;
		
		String docSeq = "";

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> mbrmMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行

		String role = (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINROLE);
		String loginBrh = (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINBRH);
		String loginID = (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID);
		
		try {
			//1.新申請: 檢查TBCRM_CUST_NOTE.ASSET_PRINT_FLAG 如已申請則提示該客戶已申請
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append("SELECT A.CUST_ID, ASSET_PRINT_FLAG ");
			sb.append("FROM TBCRM_CUST_NOTE A ");
			sb.append("LEFT JOIN TBCRM_CUST_MAST B ON A.CUST_ID = B.CUST_ID ");
			sb.append("LEFT JOIN TBORG_SALES_AOCODE C ON B.AO_CODE = C.AO_CODE ");
			sb.append("WHERE A.CUST_ID = :custId ");
			
			queryCondition.setObject("custId", inputVO.getCustID());
			
			if (!headmgrMap.containsKey(role)) {
				//IF 登入者身份為分行主管or 業務主管
				if (bmmgrMap.containsKey(role)) {
					sb.append("AND b.BRA_NBR = :BRANCH_NBR ");
					queryCondition.setObject("BRANCH_NBR", loginBrh);
				}
				//IF 登入者身份為區督導
				else if (mbrmMap.containsKey(role)) {
					sb.append("AND b.BRA_NBR = :BRANCH_NBR ");
					queryCondition.setObject("BRANCH_NBR", loginBrh);
				} else if (armgrMap.containsKey(role)) {
					sb.append("AND b.BRA_NBR = :BRANCH_NBR ");
					queryCondition.setObject("BRANCH_NBR", loginBrh);
				} else {//if (fcRoleMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))){
					sb.append("AND ( ");
					sb.append("     C.EMP_ID = :empId ");
					
					// 20210629 add by Ocean => #0662: WMS-CR-20210624-01_配合DiamondTeam專案調整系統模組功能_組織+客管
					sb.append("  OR EXISTS ( ");
					sb.append("    SELECT AO.AO_CODE, DT_A.EMP_ID, DT_A.BRANCH_NBR ");
					sb.append("    FROM TBORG_DIAMOND_TEAM DT_A ");
					sb.append("    INNER JOIN TBORG_SALES_AOCODE AO ON DT_A.EMP_ID = AO.EMP_ID ");
					sb.append("    WHERE EXISTS (SELECT 1 FROM TBORG_DIAMOND_TEAM DT_B WHERE DT_A.BRANCH_NBR = DT_B.BRANCH_NBR AND DT_A.TEAM_TYPE = DT_B.TEAM_TYPE AND EMP_ID = :empId) ");
					sb.append("    AND C.EMP_ID = DT_A.EMP_ID ");
					sb.append("  ) ");
					
					sb.append(") ");
					queryCondition.setObject("empId", loginID);
				}
			}

			queryCondition.setQueryString(sb.toString());
			
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			
			outputVO.setCustNoteList(list);

			if (list.size() > 0 || StringUtils.equals(inputVO.getFromTxn(), "CRM341")) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				BigDecimal seq = this.getSEQ();
				//3.更新申請狀態檢查TBCRM_CUST_NOTE.ASSET_PRINT_FLAG = 已申請
				TBCRM_CUST_NOTEVO updateDataVo = new TBCRM_CUST_NOTEVO();
				updateDataVo = (TBCRM_CUST_NOTEVO) dam.findByPKey(TBCRM_CUST_NOTEVO.TABLE_UID, inputVO.getCustID());
				updateDataVo.setASSET_PRINT_FLAG("A");
				dam.update(updateDataVo);

				//4.寫檔TBCRM_CUST_ASSET_DOC
				Timestamp ts = new Timestamp(new Date().getTime());
				sb = new StringBuilder();
				sb.append("INSERT INTO TBCRM_CUST_ASSET_DOC	(");
				sb.append("  SEQ, ");
				sb.append("  CUST_ID, ");
				sb.append("  APPLY_DATE, ");
				sb.append("  APPLY_TYPE, ");
				sb.append("  PROCESS_STATUS, ");
				sb.append("  PRINT_ALL, ");
				sb.append("  PRINT_SAV, ");
				sb.append("  PRINT_INV,	");
				sb.append("  PRINT_INS, ");
				sb.append("  PRINT_LOAN, ");
				sb.append("  PRINT_AUM, ");
				sb.append("  PRINT_IIL,	");
				sb.append("  PRINT_CUR, ");
				sb.append("  PRINT_TYPE, ");
				sb.append("  PRINT_FUND_MKT, ");
				sb.append("  PRINT_PORTFOLIO, ");
				sb.append("  PRINT_BRA_NBR, ");
				sb.append("  EMP_ID, ");
				sb.append("  EMP_NAME, ");
				sb.append("  AO_CODE, ");
				sb.append("  PRINT_STATUS, ");
				sb.append("  VERSION, ");
				sb.append("  CREATETIME, ");
				sb.append("  CREATOR, ");
				sb.append("  MODIFIER,	");
				sb.append("  LASTUPDATE, ");
				sb.append("  PRINT_ALL_CHART, ");
				sb.append("  IS_FIRST, ");
				sb.append("  FUND_SORT_TYPE ");
				sb.append(")	");
			    sb.append("VALUES ( ");
			    sb.append("  :seq, ");
			    sb.append("  :custId, ");
			    sb.append("  :applyDate, ");
			    sb.append("  :applyType, ");
			    sb.append("  :processStatus, ");
			    sb.append("  :printAll, ");
			    sb.append("  :printSav, ");
			    sb.append("  :printInv, ");
			    sb.append("  :printIns, ");
			    sb.append("  :printLoan, ");
			    sb.append("  :printAum, ");
			    sb.append("  :printIil, ");
			    sb.append("  :printCur, ");
			    sb.append("  :printType, ");
			    sb.append("  :printFundMkt, ");
			    sb.append("  :printPortfolio, ");
			    sb.append("  :printBraNbr, ");
			    sb.append("  :empId, ");
			    sb.append("  :empName, ");
			    sb.append("  :aoCode, ");
			    sb.append("  :printStatus,  ");
			    sb.append("  :version, ");
			    sb.append("  :createTime, ");
			    sb.append("  :creator, ");
			    sb.append("  :modifier, ");
			    sb.append("  :lastUpdate, ");
			    sb.append("  :printAllChart, ");
			    sb.append("  :isFirst, ");
			    sb.append("  :fundSortType ");
			    sb.append(") ");
			    
				queryCondition.setObject("seq", seq);
				queryCondition.setObject("custId", inputVO.getCustID());
				queryCondition.setObject("applyDate", ts);
				queryCondition.setObject("applyType", "P");
				queryCondition.setObject("processStatus", "S");
				queryCondition.setObject("printAll", "Y".equals(inputVO.getPrintAll()) ? inputVO.getPrintAll() : "N");
				queryCondition.setObject("printSav", "Y".equals(inputVO.getPrintSav()) ? inputVO.getPrintSav() : "N");
				queryCondition.setObject("printInv", "Y".equals(inputVO.getPrintInv()) ? inputVO.getPrintInv() : "N");
				queryCondition.setObject("printIns", "Y".equals(inputVO.getPrintIns()) ? inputVO.getPrintIns() : "N");
				queryCondition.setObject("printLoan", "Y".equals(inputVO.getPrintLoan()) ? inputVO.getPrintLoan() : "N");
				queryCondition.setObject("printAum", "Y".equals(inputVO.getPrintAUM()) ? inputVO.getPrintAUM() : "N");
				queryCondition.setObject("printIil", "Y".equals(inputVO.getPrintIIL()) ? inputVO.getPrintIIL() : "N");
				queryCondition.setObject("printCur", "Y".equals(inputVO.getPrintCUR()) ? inputVO.getPrintCUR() : "N");
				queryCondition.setObject("printType", "Y".equals(inputVO.getPrintType()) ? inputVO.getPrintType() : "N");
				queryCondition.setObject("printFundMkt", "Y".equals(inputVO.getPrintFundMkt()) ? inputVO.getPrintFundMkt() : "N");
				queryCondition.setObject("printPortfolio", "Y".equals(inputVO.getPrintPortfolio()) ? inputVO.getPrintPortfolio() : "N");
				queryCondition.setObject("printBraNbr", inputVO.getBranchNbr());
				queryCondition.setObject("empId", (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
				queryCondition.setObject("empName", (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINNAME));
				queryCondition.setObject("aoCode", inputVO.getAoCode());
				queryCondition.setObject("printStatus", "N");
				queryCondition.setObject("version", 0);
				queryCondition.setObject("createTime", ts);
				queryCondition.setObject("creator", (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
				queryCondition.setObject("modifier", (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
				queryCondition.setObject("lastUpdate", ts);
				queryCondition.setObject("printAllChart", "Y".equals(inputVO.getPrintAllChart()) ? inputVO.getPrintAllChart() : "N");
				queryCondition.setObject("isFirst", "N");
				queryCondition.setObject("fundSortType", inputVO.getFundSortType());
				
				queryCondition.setQueryString(sb.toString());
				
				dam.exeUpdate(queryCondition);

				if (inputVO.isPrint()) {//是否套印表單, 以下2種情況會套印CRM8501申請及CRM341同行移入之十保客戶
					TBCRM_CUST_ASSET_DOCVO docVo = (TBCRM_CUST_ASSET_DOCVO) dam.findByPKey(TBCRM_CUST_ASSET_DOCVO.TABLE_UID, seq);
					//5.進行套表
					String url = null;
					String txnCode = "CRM8501";
					String reportID = "R3";
					ReportIF report = null;
					List<String> url_list = new ArrayList<String>();

					ReportFactory factory = new ReportFactory();
					ReportDataIF data = new ReportData();
					ReportGeneratorIF gen = factory.getGenerator(); // 產出pdf

					String getMonth = "";
					String getDate = "";
					getMonth = (ts.getMonth() + 1) + "";
					getDate = ts.getDate() + "";
					if (getMonth.length() < 2) {
						getMonth = "0" + getMonth;
					}
					if (getDate.length() < 2) {
						getDate = "0" + getDate;
					}
					data.addParameter("seq", seq.toString());
					data.addParameter("applyDate", "申請日期： 中華民國  " + (ts.getYear() - 11) + "  年  " + getMonth + "  月  " + getDate + "  日");
					data.addParameter("printAll", "Y".equals(docVo.getPRINT_ALL()) ? docVo.getPRINT_ALL() : "N");//全選
					data.addParameter("printSav", "Y".equals(docVo.getPRINT_SAV()) ? docVo.getPRINT_SAV() : "N");//存款
					data.addParameter("printInv", "Y".equals(docVo.getPRINT_INV()) ? docVo.getPRINT_INV() : "N");//投資
					data.addParameter("printIns", "Y".equals(docVo.getPRINT_INS()) ? docVo.getPRINT_INS() : "N");//保險
					data.addParameter("printAllChart", "Y".equals(docVo.getPRINT_ALL_CHART()) ? docVo.getPRINT_ALL_CHART() : "N");//圖示全選
					data.addParameter("printAUM", "Y".equals(docVo.getPRINT_AUM()) ? docVo.getPRINT_AUM() : "N");//資產(AUM)趨勢圖
					data.addParameter("printIIL", "Y".equals(docVo.getPRINT_IIL()) ? docVo.getPRINT_IIL() : "N");//存投保分佈圖
					data.addParameter("printCUR", "Y".equals(docVo.getPRINT_CUR()) ? docVo.getPRINT_CUR() : "N");//總資產計價幣別圖
					data.addParameter("printType", "Y".equals(docVo.getPRINT_TYPE()) ? docVo.getPRINT_TYPE() : "N");//資產類別圖
					data.addParameter("printFundMkt", "Y".equals(docVo.getPRINT_FUND_MKT()) ? docVo.getPRINT_FUND_MKT() : "N");//基金投資區域圖
					data.addParameter("printPortfolio", "Y".equals(docVo.getPRINT_PORTFOLIO()) ? docVo.getPRINT_PORTFOLIO() : "N");//投資組合分佈圖
					data.addParameter("CUST_ID", docVo.getCUST_ID());//客戶ID

					report = gen.generateReport(txnCode, reportID, data);
					url = report.getLocation();
					//儲存約定書實體
					updatePrintFile(url, seq);

					if (StringUtils.isBlank(inputVO.getFileName())) {//從CRM8501申請
						inputVO.setFileName("客戶資況表申請書_");
					}

					if (StringUtils.equals(inputVO.getFromTxn(), "CRM341")) {
						//從CRM341進來的要由它去通知下載
						outputVO.setUrl(url);
						outputVO.setFileNmae(inputVO.getFileName() + seq.toString() + ".pdf");
					} else {
						notifyClientToDownloadFile(url, inputVO.getFileName() + seq.toString() + ".pdf");
					}
				}
				
				docSeq = seq.toString();
				outputVO.setDosSeq(docSeq);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return outputVO;
	}

	/** SEQ產生 **/
	public BigDecimal getSEQ() throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT TBCRM_CUST_AST_DOC_SEQ.nextval AS SEQ FROM DUAL ");
		
		queryCondition.setQueryString(sql.toString());
		
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		
		return seqNo;
	}
	
	public void initQuery(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		CRM8501OutputVO return_VO = new CRM8501OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT PARAM_NAME ");
		sql.append("FROM TBSYSPARAMETER ");
		sql.append("WHERE PARAM_TYPE = 'CRM8501.INITIALIZE.TABLECHECK' ");
		sql.append("ORDER BY PARAM_ORDER ");
		
		condition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(condition);
			
		if(!StringUtils.equals((String) list.get(1).get("PARAM_NAME"), new SimpleDateFormat("yyyyMMdd").format(new Date()))){
			return_VO.setErrorMsg((String) list.get(0).get("PARAM_NAME"));
			return_VO.setShowMsg(true);
		} 
		
		sendRtnObject(return_VO);
	}

}