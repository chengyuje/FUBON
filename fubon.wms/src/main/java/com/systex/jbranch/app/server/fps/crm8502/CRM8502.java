package com.systex.jbranch.app.server.fps.crm8502;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrmCons.SC120100;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.CUST_ASSET_NF_DBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.CUST_ASSET_NF_OBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_ASSETS;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Blob;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_ASSET_DOCVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_MASTVO;
import com.systex.jbranch.app.server.fps.crm821.CRM821;
import com.systex.jbranch.app.server.fps.crm841.CRM841;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot707.CustAssetBondVO;
import com.systex.jbranch.app.server.fps.sot707.SOT707;
import com.systex.jbranch.app.server.fps.sot707.SOT707InputVO;
import com.systex.jbranch.app.server.fps.sot707.SOT707OutputVO;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.service.EB12020002Service;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.afbrn9.AFBRN9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb12020002.EB12020002OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb12020002.EB12020002OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn9.CustAssetFundVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn9.NFBRN9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn9.NFBRN9OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn9.NFBRN9OutputVODetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfvipa.NFVIPAInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfvipa.NFVIPAOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfvipa.NFVIPAOutputVODetails;
import com.systex.jbranch.fubon.commons.esb.vo.sc120100.SC120100DetailOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sc120100.SC120100InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sc120100.SC120100OutputVO;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.formatter.FormatUtils;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * crm8502
 *
 * @author James
 * @date 2017/10/19
 * @spec null
 */
@Component("crm8502")
@Scope("request")
public class CRM8502 extends EsbUtil {
	@Autowired
	private CBSService cbsservice;
	@Autowired
	private EB12020002Service eb12020002Service;
	@Autowired
	private CRM821 crm821;

	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM8502.class);
    private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
    private String thisClaz = this.getClass().getSimpleName() + ".";

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM8502InputVO inputVO = (CRM8502InputVO) body;
		CRM8502OutputVO outputVO = new CRM8502OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuilder sb = null;
		try {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();

			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行
			String role = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);

			// List1
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append(" SELECT A.*, B.CUST_NAME, PH.EMP_BRA_NBR AS AUTH_BRA_NBR ");
			sb.append(" FROM TBCRM_CUST_ASSET_DOC A ");
			sb.append(" LEFT JOIN TBCRM_CUST_MAST B ON A.CUST_ID = B.CUST_ID ");
			sb.append(" LEFT JOIN TBORG_SALES_AOCODE C ON B.AO_CODE = C.AO_CODE ");
			sb.append(" LEFT JOIN ( ");
			sb.append("	 SELECT EMP_ID, SUBSTR(LISTAGG(EMP_BRA_NBR, ',') WITHIN GROUP(ORDER BY CREATETIME DESC), 0, 3) AS EMP_BRA_NBR ");
			sb.append("  FROM TBCRM_CUST_ASSET_PRINT_HIS GROUP BY EMP_ID ");
			sb.append(") PH ");
			sb.append(" ON PH.EMP_ID = A.AUTH_ID ");
			sb.append(" WHERE A.CUST_ID = B.CUST_ID AND IS_FIRST = 'N' ");

			if (StringUtils.isNotBlank(inputVO.getCustID())) {
				sb.append(" AND a.CUST_ID = :custId ");
				queryCondition.setObject("custId", inputVO.getCustID());
			}

			if (StringUtils.isNotBlank(inputVO.getBranchNbr()) && !"ALL".equals(inputVO.getBranchNbr())) {
				sb.append(" AND (PRINT_BRA_NBR = :branchNbr OR PH.EMP_BRA_NBR = :branchNbr) ");
				queryCondition.setObject("branchNbr", inputVO.getBranchNbr());
			}

			if (null != inputVO.getApplySdate() && null != inputVO.getApplyEdate()) {
				sb.append(" AND TRUNC(A.APPLY_DATE) BETWEEN TRUNC(:applySdate) AND TRUNC(:applyEdate) ");
				queryCondition.setObject("applySdate", inputVO.getApplySdate());
				queryCondition.setObject("applyEdate", inputVO.getApplyEdate());
			}
			if (null != inputVO.getApplySdate() && null == inputVO.getApplyEdate()) {
				sb.append(" AND TRUNC(A.APPLY_DATE) >= TRUNC(:applySdate) ");
				queryCondition.setObject("applySdate", inputVO.getApplySdate());
			}
			if (null == inputVO.getApplySdate() && null != inputVO.getApplyEdate()) {
				sb.append(" AND TRUNC(A.APPLY_DATE) <= TRUNC(:applyEdate) ");
				queryCondition.setObject("applyEdate", inputVO.getApplyEdate());
			}
			if (!"ALL".equals(inputVO.getPrintStatus())) {
				sb.append(" AND A.PRINT_STATUS = :printStatus ");
				queryCondition.setObject("printStatus", inputVO.getPrintStatus());
			}
			sb.append(" AND A.APPLY_TYPE = 'P' ");
//			sb.append(" AND PRINT_STATUS = 'N' ");
			sb.append(" ORDER by A.APPLY_DATE DESC , A.SEQ DESC");
			queryCondition.setQueryString(sb.toString());
			outputVO.setCustAssetDocList1(dam.exeQuery(queryCondition));

			// List3
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append(" SELECT m.CUST_NAME, ph.SEQ, ph.PRINT_DATE, ph.APPLY_DATE, ph.EMP_BRA_NBR AS AUTH_BRA_NBR, ph.EMP_ID AS AUTH_ID, ph.EMP_NAME AS AUTH_NAME, ");
			sb.append(" d.PRINT_BRA_NBR, d.CUST_ID, d.EMP_NAME, d.AO_CODE, '是' PRINT_STATUS,ph.PRINT_COUNT");
			sb.append(" FROM TBCRM_CUST_ASSET_PRINT_HIS ph ");
			sb.append(" LEFT JOIN TBCRM_CUST_ASSET_DOC d on (ph.SEQ = d.SEQ) ");
			sb.append(" LEFT JOIN TBCRM_CUST_MAST m ON D.CUST_ID = M.CUST_ID ");
			sb.append(" LEFT JOIN TBORG_SALES_AOCODE C ON m.AO_CODE = C.AO_CODE ");
			sb.append(" WHERE m.CUST_ID = d.CUST_ID ");

			if (StringUtils.isNotBlank(inputVO.getCustID())) {
				sb.append(" AND d.CUST_ID = :custId AND ph.EMP_BRA_NBR = :branchNbr ");
				queryCondition.setObject("branchNbr", getUserVariable(FubonSystemVariableConsts.LOGINBRH));
				queryCondition.setObject("custId", inputVO.getCustID());
			}

			if (StringUtils.isNotBlank(inputVO.getBranchNbr()) && !"ALL".equals(inputVO.getBranchNbr())) {
				sb.append(" AND ph.EMP_BRA_NBR = :branchNbr ");
				queryCondition.setObject("branchNbr", inputVO.getBranchNbr());
			}

			if (null != inputVO.getApplySdate() && null != inputVO.getApplyEdate()) {
				sb.append(" AND TRUNC(ph.PRINT_DATE) BETWEEN TRUNC(:printSdate) AND TRUNC(:printEdate) ");
				queryCondition.setObject("printSdate", inputVO.getApplySdate());// 修改為列印日期，但vo暫不改
				queryCondition.setObject("printEdate", inputVO.getApplyEdate());
			}
			if (null != inputVO.getApplySdate() && null == inputVO.getApplyEdate()) {
				sb.append(" AND TRUNC(ph.PRINT_DATE) >= TRUNC(:printSdate) ");
				queryCondition.setObject("printSdate", inputVO.getApplySdate());
			}
			if (null == inputVO.getApplySdate() && null != inputVO.getApplyEdate()) {
				sb.append(" AND TRUNC(ph.PRINT_DATE) <= TRUNC(:printEdate) ");
				queryCondition.setObject("printEdate", inputVO.getApplyEdate());
			}
			sb.append(" ORDER by PRINT_DATE DESC, APPLY_DATE DESC ");
			queryCondition.setQueryString(sb.toString());
			outputVO.setCustAssetPrintHisList(dam.exeQuery(queryCondition));

			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 預覽列印  十保需求新增
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void printReview (Object body, IPrimitiveMap header) throws JBranchException {
		CRM8502InputVO inputVO = (CRM8502InputVO) body;
		CRM8502OutputVO outputVO = new CRM8502OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = null;

		try {
			List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
			Map<String , Object> data = this.getTot(inputVO.getCustID());
			data.put("RECORD_SEQ" ,this.maskString(inputVO.getCustID())+"-"+new SimpleDateFormat("yyyyMMdd").format(new Date())+inputVO.getSeq());//記錄編號CUST_ID-YYYYMMDD+6碼流水號(SEQ)
			result.add(data);
			outputVO.setResult(result);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		this.sendRtnObject(outputVO);
	}
	
	private List<Map<String,Object>> getNanoAsset(String custID) throws JBranchException {
		List<Map<String,Object>> nanoList = new ArrayList<>();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		// 88
		sb.append(" SELECT A.TMB01 AS POT_ID, K.TMF02K AS PLAN_NAME, B.INV_AMT AS INCREASE_AMT_BAS, ");
		sb.append(" ROUND(B.INV_AMT * NVL(IQ.BUY_RATE,1)) AS INCREASE_AMT_TWD, ");
		sb.append(" NVL(C.REF_AMT_SAV, 0) + NVL(D.REF_AMT_INV, 0) AS MARKET_VAL_BAS, ");
		sb.append(" ROUND((NVL(C.REF_AMT_SAV, 0) + NVL(D.REF_AMT_INV, 0)) * NVL(IQ.BUY_RATE, 1)) AS MARKET_VAL_TWD, ");
		sb.append(" NVL(C.REF_AMT_SAV, 0) + NVL(D.REF_AMT_INV, 0) - NVL(B.INV_AMT, 0) AS PROFIT_BAS, ");
		sb.append(" (NVL(C.REF_AMT_SAV, 0) + NVL(D.REF_AMT_INV, 0) - NVL(B.INV_AMT, 0)) * NVL(IQ.BUY_RATE, 1) AS PROFIT_TWD, ");
		sb.append(" CASE WHEN NVL(B.INV_AMT, 0) = 0 THEN 0 ELSE ROUND(((NVL(C.REF_AMT_SAV, 0) + NVL(D.REF_AMT_INV, 0) - NVL(B.INV_AMT, 0)) / NVL(B.INV_AMT, 0)) * 100  , 2) END AS PROFIT_RATE_BAS, ");
		sb.append(" NVL(Y.DIV_AMT,0) AS DIV_AMT, ");
		sb.append(" CASE WHEN NVL(B.INV_AMT, 0) = 0 THEN 0 ELSE ROUND(((NVL(C.REF_AMT_SAV, 0) + NVL(D.REF_AMT_INV, 0) - NVL(B.INV_AMT, 0) + NVL(Y.DIV_AMT,0)) / NVL(B.INV_AMT, 0)) * 100, 2) END AS PROFIT_RATE_DIV, ");
		sb.append(" E.TMF25 ");
		sb.append(" FROM TBCRM_NMS060_DAY E ");
		sb.append(" LEFT JOIN TBCRM_NMS060K K ON E.TMF01 = K.TMF01K ");
		sb.append(" LEFT JOIN (SELECT T54002, SUM(NVL(CASE WHEN T54005 = '3' THEN T54009 * 1 WHEN T54005 = '4' THEN T54009 * -1 ELSE 0 END, 0)) AS INV_AMT ");
		sb.append(" FROM TBCRM_NMS540 GROUP BY T54002) B ON E.TMF01 = B.T54002 ");
		sb.append(" INNER JOIN (SELECT DISTINCT TMB01, TMB02 FROM TBCRM_NMS020_DAY) A ON A.TMB01 = E.TMF01 ");
		sb.append(" LEFT OUTER JOIN (SELECT T06301, SUM(ROUND(NVL(T06303, 0), 2) + ROUND(NVL(T06319, 0), 2) - ROUND(NVL(T06337, 0), 2)) AS REF_AMT_SAV ");
		sb.append(" FROM TBCRM_NMS063 WHERE T06302 = (SELECT MAX(T06302) FROM TBCRM_NMS063) GROUP BY T06301) C ON C.T06301 = A.TMB01 ");
		sb.append(" LEFT OUTER JOIN (SELECT T03404, SUM(ROUND(NVL(T03419, 0) * NVL(T03448, 0), 2)) AS REF_AMT_INV ");
		sb.append(" FROM TBCRM_NMS034 WHERE T03402 = (SELECT MAX(T03402) FROM TBCRM_NMS034) GROUP BY T03404) D ON D.T03404 = A.TMB01 ");
		sb.append(" LEFT OUTER JOIN (SELECT DISTINCT T8YF01,FIRST_VALUE(T8YF04) OVER(PARTITION BY T8YF01 ORDER BY T8YF02 DESC) AS DIV_AMT FROM TBCRM_NMS8YF) Y ON E.TMF01 = Y.T8YF01 ");
		sb.append(" CROSS JOIN (SELECT * FROM TBPMS_IQ053 WHERE MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) AND CUR_COD = 'USD') IQ ");
		sb.append(" WHERE E.TMF25 = '88' AND A.TMB02 = :custID AND E.TMF10 IS NULL ");
		
		qc.setObject("custID", custID);
		qc.setQueryString(sb.toString());
		List<Map<String,Object>> list88 = dam.exeQuery(qc);
		
		// 89
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		sb.append(" SELECT A.TMB01 AS POT_ID, K.TMF02K AS PLAN_NAME, B.INV_AMT AS INCREASE_AMT_BAS, ");
		sb.append(" ROUND(B.INV_AMT) AS INCREASE_AMT_TWD, ");
		sb.append(" NVL(C.REF_AMT_SAV, 0) + NVL(D.REF_AMT_INV, 0) AS MARKET_VAL_BAS, ");
		sb.append(" NVL(C.REF_AMT_SAV, 0) + NVL(D.REF_AMT_INV, 0) AS MARKET_VAL_TWD, ");
		sb.append(" NVL(C.REF_AMT_SAV, 0) + NVL(D.REF_AMT_INV, 0) - NVL(B.INV_AMT, 0) AS PROFIT_BAS, ");
		sb.append(" ROUND(NVL(C.REF_AMT_SAV, 0) + NVL(D.REF_AMT_INV, 0) - NVL(B.INV_AMT, 0)) AS PROFIT_TWD, ");
		sb.append(" CASE WHEN NVL(B.INV_AMT, 0) = 0 THEN 0 ELSE ROUND(((NVL(C.REF_AMT_SAV, 0) + NVL(D.REF_AMT_INV, 0) - NVL(B.INV_AMT, 0)) / NVL(B.INV_AMT, 0)) * 100  , 2) END AS PROFIT_RATE_BAS, ");
		sb.append(" NVL(Y.DIV_AMT,0) AS DIV_AMT, ");
		sb.append(" CASE WHEN NVL(B.INV_AMT, 0) = 0 THEN 0 ELSE ROUND(((NVL(C.REF_AMT_SAV, 0) + NVL(D.REF_AMT_INV, 0) - NVL(B.INV_AMT, 0) + NVL(Y.DIV_AMT,0)) / NVL(B.INV_AMT, 0)) * 100, 2) END AS PROFIT_RATE_DIV, ");
		sb.append(" E.TMF25 ");
		sb.append(" FROM TBCRM_NMS060_DAY E ");
		sb.append(" LEFT JOIN TBCRM_NMS060K K ON E.TMF01 = K.TMF01K ");
		sb.append(" LEFT JOIN (SELECT T54002, SUM(NVL(CASE WHEN T54005 = '3' THEN T54009 * 1 WHEN T54005 = '4' THEN T54009 * -1 ELSE 0 END, 0)) AS INV_AMT ");
		sb.append(" FROM TBCRM_NMS540 GROUP BY T54002) B ON E.TMF01 = B.T54002 ");
		sb.append(" INNER JOIN (SELECT DISTINCT TMB01, TMB02 FROM TBCRM_NMS020_DAY) A ON A.TMB01 = E.TMF01 ");
		sb.append(" LEFT OUTER JOIN (SELECT T06301, SUM(ROUND(NVL(T06303, 0), 0) + ROUND(NVL(T06319, 0), 0) - ROUND(NVL(T06337, 0), 0)) AS REF_AMT_SAV ");
		sb.append(" FROM TBCRM_NMS063 WHERE T06302 = (SELECT MAX(T06302) FROM TBCRM_NMS063) GROUP BY T06301) C ON C.T06301 = A.TMB01 ");
		sb.append(" LEFT OUTER JOIN (SELECT T03404, SUM(ROUND(NVL(T03419, 0) * NVL(T03448, 0), 0)) AS REF_AMT_INV ");
		sb.append(" FROM TBCRM_NMS034 WHERE T03402 = (SELECT MAX(T03402) FROM TBCRM_NMS034) GROUP BY T03404) D ON D.T03404 = A.TMB01 ");
		sb.append(" LEFT OUTER JOIN (SELECT DISTINCT T8YF01,FIRST_VALUE(T8YF04) OVER(PARTITION BY T8YF01 ORDER BY T8YF02 DESC) AS DIV_AMT FROM TBCRM_NMS8YF) Y ON E.TMF01 = Y.T8YF01 ");
		sb.append(" CROSS JOIN (SELECT * FROM TBPMS_IQ053 WHERE MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) AND CUR_COD = 'USD') IQ ");
		sb.append(" WHERE E.TMF25 = '89' AND A.TMB02 = :custID AND E.TMF10 IS NULL ");
		
		qc.setObject("custID", custID);
		qc.setQueryString(sb.toString());
		List<Map<String,Object>> list89 = dam.exeQuery(qc);
		
		if (list88.size() > 0) {
			nanoList.addAll(list88);						
		}
		if (list89.size() > 0) {
			nanoList.addAll(list89);						
		}
		return nanoList;
	}

	public void printReport (Object body, IPrimitiveMap header) throws JBranchException {
		CRM8502InputVO inputVO = (CRM8502InputVO) body;
		CRM8502OutputVO outputVO = new CRM8502OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = null;

		String url = null;
		String txnCode = "CRM8502";
		String reportID = "R1";
		ReportIF report = null;
		BigDecimal seq = inputVO.getSeq();
		try {

			//取日期
			System.out.println("Before update TBCRM_CUST_ASSET_DOC");
			Timestamp ts = new Timestamp(new Date().getTime());
			//更新TBCRM_CUST_ASSET_DOC
			TBCRM_CUST_ASSET_DOCVO vo = new TBCRM_CUST_ASSET_DOCVO();
			vo = (TBCRM_CUST_ASSET_DOCVO) dam.findByPKey(TBCRM_CUST_ASSET_DOCVO.TABLE_UID, seq);
			vo.setAUTH_ID((String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
			vo.setAUTH_NAME((String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINNAME));
			vo.setPRINT_STATUS("Y");
			vo.setPRINT_DATE(ts);
			System.out.println("SEQ:"+vo.getSEQ());
			dam.update(vo);
			System.out.println("Update TBCRM_CUST_ASSET_DOC,SEQ:"+vo.getSEQ()+" complete.");

			System.out.println("Get TBCRM_CUST_ASSET_DOCVO AGAIN");
			TBCRM_CUST_ASSET_DOCVO docVo = (TBCRM_CUST_ASSET_DOCVO) dam.findByPKey(TBCRM_CUST_ASSET_DOCVO.TABLE_UID, seq);

			//進行PDF套印
			ReportFactory factory = new ReportFactory();
			ReportDataIF data = new ReportData();
			ReportGeneratorIF gen = factory.getGenerator(); // 產出pdf
			txnCode = "CRM8502";
			reportID = "R1";
			factory = new ReportFactory();
			data = new ReportData();
			gen = factory.getGenerator(); // 產出pdf
			data = packAssetDetails(inputVO , docVo);//依列印需求列出要印的資產、圖示
//			data.addParameter("custName", "test");

			report = gen.generateReport(txnCode, reportID, data);
			url = report.getLocation();
			notifyClientViewDoc(url, "pdf");


			//寫入列印記錄(TBCRM_CUST_ASSET_PRINT_HIS)
			BigDecimal printCount = this.getPrintCount(seq);
//			TBCRM_CUST_ASSET_PRINT_HISPK dPK = new TBCRM_CUST_ASSET_PRINT_HISPK();
//			dPK.setSEQ(seq);
//			dPK.setPRINT_DATE(ts);

//			TBCRM_CUST_ASSET_PRINT_HISVO addDataVo = new TBCRM_CUST_ASSET_PRINT_HISVO();
			sb = new StringBuilder();
			sb.append("	INSERT INTO TBCRM_CUST_ASSET_PRINT_HIS	");
			sb.append("(SEQ , PRINT_DATE , APPLY_DATE , EMP_ID , PRINT_COUNT, EMP_BRA_NBR,	");
			sb.append("	EMP_NAME  , CREATOR, VERSION , CREATETIME)	");
			sb.append("VALUES	");
			sb.append("	(:seq , :printDate , :applyDate , :empId , :printCount , :empBraNbr , :empName ,	");
			sb.append("  :creator , :version , :createTime )	");
//			addDataVo.setcomp_id(dPK);
//			addDataVo.setEMP_ID((String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
//			addDataVo.setEMP_NAME((String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINNAME));
//			addDataVo.setAPPLY_DATE(vo.getAPPLY_DATE());
//			addDataVo.setPRINT_COUNT(this.getPrintCount(seq));
//			dam.create(addDataVo);
			queryCondition.setObject("seq", seq);
			queryCondition.setObject("printDate", ts);
			queryCondition.setObject("applyDate", vo.getAPPLY_DATE());
			queryCondition.setObject("empId", (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
			queryCondition.setObject("printCount", printCount);
			queryCondition.setObject("empBraNbr", getUserVariable(FubonSystemVariableConsts.LOGINBRH));
			queryCondition.setObject("empName", (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINNAME));
			queryCondition.setObject("creator", (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
			queryCondition.setObject("version", 0);
			queryCondition.setObject("createTime", ts);
			queryCondition.setQueryString(sb.toString());
			dam.exeUpdate(queryCondition);

			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	//資況表資產內容
	private ReportDataIF packAssetDetails(CRM8502InputVO inputVO , TBCRM_CUST_ASSET_DOCVO docVo) throws Exception{
		WorkStation ws = DataManager.getWorkStation(uuid);
		BigDecimal seq = inputVO.getSeq();
		String custId = inputVO.getCustID();

		ReportDataIF result = new ReportData();
		dam = this.getDataAccessManager();
		StringBuilder sb = new StringBuilder();
		try {
			//判斷列印內容
			String printAll = docVo.getPRINT_ALL();//列印所有資產列表
			String printAllChart = docVo.getPRINT_ALL_CHART();//列印所有圖示
			result.addParameter("printAll","Y".equals(printAll) ? printAll : "N");//全選
			result.addParameter("printSav","Y".equals(docVo.getPRINT_SAV()) ? docVo.getPRINT_SAV() : "N");//存款
			result.addParameter("printInv","Y".equals(docVo.getPRINT_INV()) ? docVo.getPRINT_INV() : "N");//投資
			result.addParameter("printIns","Y".equals(docVo.getPRINT_INS()) ? docVo.getPRINT_INS() : "N");//保險
			result.addParameter("printLoan","Y".equals(docVo.getPRINT_LOAN()) ? docVo.getPRINT_LOAN() : "N");//融資
			result.addParameter("printAllChart","Y".equals(printAllChart) ? printAllChart : "N");//圖示全選
			result.addParameter("printAUM","Y".equals(docVo.getPRINT_AUM()) ? docVo.getPRINT_AUM() : "N");//資產(AUM)趨勢圖
			result.addParameter("printIIL","Y".equals(docVo.getPRINT_IIL()) ? docVo.getPRINT_IIL() : "N");//存投保分佈圖
			result.addParameter("printCUR","Y".equals(docVo.getPRINT_CUR()) ? docVo.getPRINT_CUR() : "N");//總資產計價幣別圖
			result.addParameter("printType","Y".equals(docVo.getPRINT_TYPE()) ? docVo.getPRINT_TYPE() : "N");//投資資產類別圖
			result.addParameter("printFundMkt","Y".equals(docVo.getPRINT_FUND_MKT()) ? docVo.getPRINT_FUND_MKT() : "N");//基金投資區域圖
			result.addParameter("printPortfolio","Y".equals(docVo.getPRINT_PORTFOLIO()) ? docVo.getPRINT_PORTFOLIO() : "N");//投資組合分佈圖
			result.addParameter("fundSortType",docVo.getFUND_SORT_TYPE());//基金排序方式

			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

			Map<String,Object> dataMap = new HashMap<String,Object>();
			dataMap = this.getTot(custId);
			result.addParameter("custName", (String)dataMap.get("CUST_NAME"));//客戶姓名
			result.addParameter("custId", this.maskString(custId));//客戶身分證/統一編號

			result.addParameter("kyc", (String)dataMap.get("KYC_VALUE"));//客戶KYC值
			result.addParameter("kycCreateDate",(String)dataMap.get("KYC_CREATE_DATE"));//KYC測驗日期
			result.addParameter("kycDueDate", (String)dataMap.get("KYC_DUE_DATE"));//KYC到期日
			result.addParameter("reportSeq", this.maskString(
					custId)+"-"+new SimpleDateFormat("yyyyMMdd").format(new Date())+seq);//記錄編號CUST_ID-YYYYMMDD+6碼流水號(SEQ)
			result.addParameter("printEmpId", ws.getUser().getUserID());
			result.addParameter("printDate", FormatUtils.T1Format(new Date()).substring(0,9));


			BigDecimal depositTwd = (BigDecimal)dataMap.get("DEPOSIT_TWD");
			BigDecimal depositF = (BigDecimal)dataMap.get("DEPOSIT_FOR");
			BigDecimal invFund = (BigDecimal)dataMap.get("INV_FUND");
			BigDecimal invSi = (BigDecimal)dataMap.get("INV_SI");
			BigDecimal invSn = (BigDecimal)dataMap.get("INV_SN");
			BigDecimal invFbond = (BigDecimal)dataMap.get("INV_FBOND");
			BigDecimal invEtf = (BigDecimal)dataMap.get("INV_ETF");
			BigDecimal invGold = (BigDecimal)dataMap.get("INV_GOLD");
			BigDecimal invNano = (BigDecimal)dataMap.get("INV_NANO");
			BigDecimal invVpbnd = (BigDecimal)dataMap.get("INV_VPBND");
			BigDecimal insTot = (BigDecimal)dataMap.get("TOT_INS");
			BigDecimal subFstock = (BigDecimal)dataMap.get("SUB_FSTOCK");
			BigDecimal subFbond = (BigDecimal)dataMap.get("SUB_FBOND");
			BigDecimal subSn = (BigDecimal)dataMap.get("SUB_SN");
			BigDecimal subDsn = (BigDecimal)dataMap.get("SUB_DSN");
			BigDecimal subTot = (BigDecimal)dataMap.get("TOT_SUB");
			

			//存款
			result.addParameter("depositTwd", depositTwd);
			result.addParameter("depositF"	, depositF);
			//投資
			result.addParameter("invFund"	, invFund);
			result.addParameter("invSi"		, invSi);
			result.addParameter("invSn"		, invSn);
			result.addParameter("invFbond"	, invFbond);
			result.addParameter("invEtf"	, invEtf);
			result.addParameter("invGold"	, invGold);
			result.addParameter("invVpbnd"	, invVpbnd);
			result.addParameter("invNano"	, invNano);
			
			//保險
			result.addParameter("insTotAll", insTot);//保險(累計淨值保費：富邦 + 日盛)
			//證券複委託
			result.addParameter("subFstock" , subFstock);
			result.addParameter("subFbond", subFbond);
			result.addParameter("subSn", subSn);
			result.addParameter("subDsn", subDsn);
			result.addParameter("subTot", subTot);
//			//資產總計
//			result.addParameter("assetTot", depositTot.add(invTot).add(insTot));
			//貸款

			//資產列表
			//存款
			Map<String , Object> details = new HashMap<String, Object>();
			if (printAll.equals("Y") || docVo.getPRINT_SAV().equals("Y")) {
//				台幣存款
				try {
					sb = new StringBuilder();
					sb.append(" SELECT * FROM (");
					sb.append(" SELECT '活期存款' AS TYPE , B.CHIN_ALS , '2' AS RANK ,	");
					sb.append("        ACC_NBR ,");
					sb.append("        'TWD' AS CUR_TYPE , '-' AS CD_NBR ,	");
					sb.append("			A.CURT_AMT	 CURT_AMT ,	");
					sb.append("			'-' as ACC_OPEN_DATE , '-' AS DUE_DATE , ");
					sb.append("		   	'-' AS INT_RATE_TYPE ,null AS INT_RATE ,	");
					sb.append("			CASE	WHEN	A.AVAL_AMT<0	THEN	'已動用透支金額'||to_char((A.AVAL_AMT*-1), 'FM999,999,999,999,999') ");
					sb.append("						END	AS	PLG_NOTE	,	''	SLIP_NO	,	A.AVAL_AMT	");
					sb.append("	  FROM TBCRM_AST_DEP_SAV A LEFT JOIN TBORG_BRH_CONTACT B ");
					sb.append("		ON A.BRA_NBR = B.BRH_COD");
					sb.append("  where A.CUST_ID = :custId	");
					sb.append("  UNION ALL	");
					sb.append(" SELECT '支票存款' AS TYPE , B.CHIN_ALS , '1' AS RANK ,	");
					sb.append("       ACC_NBR ,");
					sb.append("        'TWD' AS CUR_TYPE , '-' AS CD_NBR ,	");
					sb.append("			A.CURT_AMT	CURT_AMT ,	");
					sb.append("			'-' as ACC_OPEN_DATE , '-' AS DUE_DATE , ");
					sb.append("		   	'-' AS INT_RATE_TYPE , null AS INT_RATE , ");
					sb.append("			CASE	WHEN	A.AVAL_AMT<0	THEN	'已動用透支金額'||to_char((A.AVAL_AMT*-100), 'FM999,999,999,999,999') ");//PEOPSOFT資料料需*100
					sb.append("						END	AS	PLG_NOTE	,	''	SLIP_NO	,	A.AVAL_AMT	");
					sb.append("	  FROM TBCRM_AST_DEP_CHK A LEFT JOIN TBORG_BRH_CONTACT B ");
					sb.append("		ON A.BRA_NBR = B.BRH_COD");
					sb.append("  where A.CUST_ID = :custId	");
					sb.append("  UNION ALL	");
					sb.append(" SELECT '定期存款' AS TYPE , B.CHIN_ALS , '3' AS RANK ,	");
					sb.append("        ACC_NBR ,");
					sb.append("        'TWD' AS CUR_TYPE , CD_NBR , CD_AMT CURT_AMT,	");
					sb.append("			to_char(VALUE_DATE,'yyyy-MM-dd') AS ACC_OPEN_DATE , ");
					sb.append("        to_char(DUE_DATE,'yyyy-MM-dd') as DUE_DATE , ");
					sb.append("		   C.PARAM_NAME AS INT_RATE_TYPE , to_char(A.INT_RATE/100) AS INT_RATE ,	");
					sb.append("	       CASE WHEN DUE_AGMT='1' THEN '不轉期'	");
					sb.append("             WHEN DUE_AGMT='2' THEN '本金轉期'	");
					sb.append("             WHEN DUE_AGMT='3' THEN '本息轉期'	");
					sb.append("             WHEN DUE_AGMT='4' THEN '綜定到期轉綜活'	");
					sb.append("		   END	AS PLG_NOTE, SLIP_NO	,	0	AVAL_AMT	");
					sb.append("	  FROM TBCRM_AST_DEP_CD A LEFT JOIN TBORG_BRH_CONTACT B ");
					sb.append("		ON A.BRA_NBR = B.BRH_COD");
					sb.append("   LEFT JOIN TBSYSPARAMETER C ON A.INT_RATE_TYPE = C.PARAM_CODE AND C.PARAM_TYPE='CRM.INT_TYPE'	");
					sb.append("  where A.CUST_ID = :custId	");
					sb.append("  ) ORDER BY RANK,ACC_NBR,ACC_OPEN_DATE ");
					List list = this.getQueryExecute(sb.toString() , custId);
					result.addRecordList("DEPOSIT_TWD", list);
				} catch (Exception e) {
					logger.error("Deposit Twd:"+e.getMessage());
				}
//				外幣存款
				try {
					sb = new StringBuilder();
					sb.append(" SELECT * FROM ( ");
					sb.append(" select '1' AS RANK,'活期存款' AS TYPE, SUBSTR(a.ACC_NBR,1,LENGTH(a.ACC_NBR)-4)||'**'||SUBSTR(a.ACC_NBR,LENGTH(a.ACC_NBR)-1,LENGTH(a.ACC_NBR)) as ACC_NBR ,");
					sb.append("		   C.CHIN_ALS , '-' AS CD_NBR , a.CRCY_TYPE , 	");
					sb.append("			a. CURT_AMT_ORGD 	CURT_AMT_ORGD , '-' AS VALUE_DATE , ");
					sb.append("		   '-' AS DUE_DATE ,	");
					sb.append("		   '-' AS INT_TYPE , null AS INT_RATE,	");
					sb.append("			CASE	WHEN	NVL(a. CURT_AMT_ORGD,0)	<	0	THEN	'已動用透支金額'||to_char((a.CURT_AMT_ORGD*-1), 'FM999,999,999,999,999')||A.CRCY_TYPE	");
					sb.append("						END	PLG_NOTE	,	a. CURT_AMT_ORGD	AVAL_AMT	,	");
					sb.append("			CURT_AMT_TWD	, '' SLIP_NO,ACC_NBR ACCT, '' INT_ROLL_IN_ACC_NBR");
					sb.append("	  from TBCRM_AST_DEP_FSAV a left join tbpms_IQ053 b ");
					sb.append("		on a.CRCY_TYPE = b.CUR_COD and 					");
					sb.append("		   b.MTN_DATE = 			");
					sb.append("	       (											");
					sb.append("	        SELECT MAX(MTN_DATE) 	");
					sb.append("	        FROM TBPMS_IQ053 						");
					sb.append("	        WHERE CUR_COD = A.CRCY_TYPE				");
					sb.append("	       ) 										");
					sb.append("			LEFT JOIN TBORG_BRH_CONTACT C 			");
					sb.append("		ON A.BRA_NBR = C.BRH_COD					");
					sb.append("	where A.CUST_ID = :custId	");
					sb.append("	UNION ALL	");
					sb.append("	select '2' AS RANK,'定期存款' AS TYPE , SUBSTR(A.ACC_NBR,1,LENGTH(A.ACC_NBR)-4)||'**'||SUBSTR(A.ACC_NBR,LENGTH(A.ACC_NBR)-1,LENGTH(A.ACC_NBR)) as ACC_NBR ,");
					sb.append("	       C.CHIN_ALS, CD_NBR , A.CRCY_TYPE ,	");
					sb.append("			A.CD_AMT_ORGD	CURT_AMT_ORGD,	");
					sb.append("			to_char(A.VALUE_DATE,'yyyy-MM-dd') as VALUE_DATE ,");
					sb.append("	       to_char(A.DUE_DATE,'yyyy-MM-dd') as DUE_DATE ,	");
					sb.append("	       D.PARAM_NAME AS INT_TYPE ,	");
					sb.append("	       TO_CHAR(A.INT_RATE/100) AS INT_RATE , 				");
					sb.append("	       CASE WHEN DUE_AGMT='1' THEN '不轉期'	");
					sb.append("             WHEN DUE_AGMT='2' THEN '本金轉期'	");
					sb.append("             WHEN DUE_AGMT='3' THEN '本息轉期'	");
					sb.append("             WHEN DUE_AGMT='4' THEN '綜定到期轉綜活'	");
					sb.append("            END	AS PLG_NOTE	,	");
					sb.append("			0	AVAL_AMT	,	");
					sb.append("		   CD_AMT_TWD	,	SLIP_NO,ACC_NBR ACCT,INT_ROLL_IN_ACC_NBR");
					sb.append("	  from TBCRM_AST_DEP_FCD a left join TBPMS_IQ053 b 	");
					sb.append("	    on a.CRCY_TYPE = b.CUR_COD and 					");
					sb.append("	       b.MTN_DATE = 			");
					sb.append("	       (											");
					sb.append("	        SELECT MAX(MTN_DATE)	");
					sb.append("	        FROM TBPMS_IQ053 				");
					sb.append("	        WHERE CUR_COD = A.CRCY_TYPE		");
					sb.append("	       ) 								");
					sb.append("			LEFT JOIN TBORG_BRH_CONTACT C 	");
					sb.append("		ON A.BRA_NBR = C.BRH_COD		");
					sb.append("   LEFT JOIN TBSYSPARAMETER D ON A.INT_TYPE = D.PARAM_CODE AND D.PARAM_TYPE='CRM.INT_TYPE'	");
					sb.append("	where A.CUST_ID = :custId	");
					sb.append(" ) ORDER BY RANK,ACC_NBR,VALUE_DATE ");
					List<Map<String, Object>> list = this.getQueryExecute(sb.toString() , custId);
					result.addRecordList("DEPOSIT_F", list);
				} catch (Exception e) {
					logger.error("Deposit F errof:"+e.getMessage());
				}
			}
			//投資
			if (printAll.equals("Y") || docVo.getPRINT_INV().equals("Y")) {

				result.addParameter("printInv", "Y");
				Map<String, Object> dataFundMap = this.getInvFund(custId,docVo.getFUND_SORT_TYPE());
				result.addRecordList("INV_MUTUAL_FUND_SECURITY", (List<Map<String,Object>>) dataFundMap.get("list"));
				result.addParameter("invFund"	,(BigDecimal)dataFundMap.get("tot"));

				String obuFlag = (String)dataFundMap.get("obuFlag");

				//判斷DBU OBU
				SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
				SOT701InputVO sot701_inputVO = new SOT701InputVO();
				SOT707 sot707 = (SOT707) PlatformContext.getBean("sot707");
				SOT707InputVO sot707_inputVO = new SOT707InputVO();

				try {
					//境外結構型商品-特定金錢信託
					sb = new StringBuilder();
					sb.append("  select PROD_NAME , B.ISIN_CODE,	");
					sb.append("         to_char(DEL_DATE , 'yyyy-MM-dd') as DEL_DATE ,	");
					sb.append("         to_char(PRICE_DATE , 'yyyy-MM-dd') as PRICE_DATE ,	");
					sb.append("         INV_CRCY_TYPE , REF_AMT_ORGD ,	DENO_AMT ,	REF_AMT_TWD,");
					sb.append("         REF_RDMP_EXCH_RATE , REF_PRICE/100 REF_PRICE ,	");
					sb.append("         (CASE WHEN D.BDASD >='100' THEN '到期保本'	");
					sb.append("               ELSE '非保本' END) AS BDASD,	");
					sb.append("         CERT_NBR , BOND_NBR , to_char(E.BDA23 , 'yyyy-MM-dd') as BDA23 , INV_AMT_TWD , INV_AMT_ORGD,	");
					sb.append("         nvl(BDG08,0) BDG08 , BUY_RATE ,RISKCATE_ID,	");
					sb.append("        (DENO_AMT*REF_PRICE/100  + NVL(BDG08,0))/INV_AMT_ORGD-1 as PROFIT_RATE,	");//含息報酬率
					sb.append("         nvl(INSTITION_OF_FLOTATION,INSTITION_OF_AVOUCH) as INSTITION,	");
					sb.append("         CASE WHEN INSTITION_OF_FLOTATION IS NULL ");
					sb.append("				 THEN CREDIT_RATING_SP||'/'||CREDIT_RATING_MODDY||'/'||CREDIT_RATING_FITCH	");
					sb.append("				 ELSE AVOUCH_CREDIT_RATING_SP||'/'||AVOUCH_CREDIT_RATING_MODDY||'/'||AVOUCH_CREDIT_RATING_FITCH ");
					sb.append("			END AS EVA  ");
					sb.append("    from TBCRM_AST_INV_FBOND A                                                   ");
					sb.append("    INNER JOIN TBPRD_SNINFO B ON A.BOND_NBR = B.PRD_ID                           ");
					sb.append("    LEFT JOIN TBPRD_BDS019 D ON A.BOND_NBR = D.BDAS1	");
					sb.append("    LEFT JOIN TBPRD_BDS010 E ON A.BOND_NBR = E.BDA01	");
					sb.append("    LEFT JOIN (SELECT BDG06,BDG01,SUM(BDG08) AS BDG08,BDG0K	");
					if (obuFlag.equals("Y"))
		                sb.append("                 FROM TBPMS_AJS070	");
					else
						sb.append("                 FROM TBPMS_BDS070	");
	                sb.append("                WHERE BDG06 = :custId	AND SNAP_DATE = (");
	                sb.append("									 SELECT MAX(SNAP_DATE)");
					if (obuFlag.equals("Y"))
		                sb.append("    					             FROM TBPMS_AJS070	");
					else
						sb.append("                 				 FROM TBPMS_BDS070	");
	                sb.append("                    				 WHERE BDG06 = :custId");
	                sb.append("									 )");
	                sb.append("    			   GROUP BY BDG06, BDG01,BDG0K) F	");
	                sb.append(" 			 ON A.BOND_NBR = F.BDG01 AND A.CUST_ID = F.BDG06 AND A.CERT_NBR = F.BDG0K");
					sb.append("    LEFT JOIN TBPMS_IQ053 G  ON A.VALU_CRCY_TYPE = G.CUR_COD AND                 ");
					sb.append("        G.MTN_DATE =	");
					sb.append("        (	");
					sb.append("         SELECT MAX(MTN_DATE)	");
					sb.append("         FROM TBPMS_IQ053	");
					sb.append("         WHERE CUR_COD = A.VALU_CRCY_TYPE	");
					sb.append("        )	");
					sb.append("    LEFT JOIN VWPRD_MASTER H ON A.BOND_NBR = H.PRD_ID  AND H.PTYPE='SN'	");
					sb.append("  where CUST_ID = :custId");
				 	List<Map<String,Object>> list = this.getQueryExecute(sb.toString(), custId);
					result.addRecordList("INV_SN", list);
				} catch (Exception e) {
					logger.error("SN error:"+e.getMessage());
				}
				try {
					//海外債-特定金錢信託
					sb = new StringBuilder();
					sb.append("  select PROD_NAME , C.ISIN_CODE,                    							");
					sb.append("         to_char(DEL_DATE , 'yyyy-MM-dd') as DEL_DATE , 							");
					sb.append("         to_char(PRICE_DATE , 'yyyy-MM-dd') as PRICE_DATE , 						");
					sb.append("         REF_AMT_TWD , INV_CRCY_TYPE,	 ");
					sb.append("         REF_RDMP_EXCH_RATE , REF_PRICE/100 AS REF_PRICE,	");
					sb.append("         (CASE WHEN D.BDASD >='100' THEN '到期保本'                                ");
					sb.append("               ELSE '' END) AS BDASD,                                            ");
					sb.append("         CERT_NBR , BOND_NBR , to_char(E.BDA23,'yyyy-MM-dd') BDA23, INV_AMT_ORGD ,DENO_AMT,	");
					sb.append("         nvl(BDG08,0) BDG08 , BUY_RATE ,RISKCATE_ID,	");
					sb.append("         (DENO_AMT*REF_PRICE/100  + NVL(BDG08,0))/INV_AMT_ORGD-1 as PROFIT_RATE, ");//含息報酬率
					sb.append("         nvl(INSTITION_OF_FLOTATION,INSTITION_OF_AVOUCH) as INSTITION,	");
					sb.append("         CASE WHEN INSTITION_OF_FLOTATION IS NULL ");
					sb.append("				 THEN CREDIT_RATING_SP||'/'||CREDIT_RATING_MODDY||'/'||CREDIT_RATING_FITCH	");
					sb.append("				 ELSE BOND_CREDIT_RATING_SP||'/'||BOND_CREDIT_RATING_MODDY||'/'||BOND_CREDIT_RATING_FITCH ");
					sb.append("			END AS EVA  ,");
					sb.append("        0 FRONTFEE1 , 0 FRONTFEE2");
					sb.append("    From TBCRM_AST_INV_FBOND A                                                   ");
					sb.append("    INNER JOIN TBPRD_BONDINFO C ON A.BOND_NBR = C.PRD_ID                         ");
					sb.append("    LEFT JOIN TBPRD_BDS019 D ON A.BOND_NBR = D.BDAS1                             ");
					sb.append("    LEFT JOIN TBPRD_BDS010 E ON A.BOND_NBR = E.BDA01                             ");
					sb.append("    LEFT JOIN (SELECT BDG06,BDG01,SUM(NVL(BDG08,0)) AS BDG08,BDG0K	");
					if (obuFlag.equals("Y"))
		                sb.append("                 FROM TBPMS_AJS070	");
					else
						sb.append("                 FROM TBPMS_BDS070	");
	                sb.append("                WHERE BDG06 = :custId	AND SNAP_DATE = (");
	                sb.append("									 SELECT MAX(SNAP_DATE)");
					if (obuFlag.equals("Y"))
		                sb.append("       					          FROM TBPMS_AJS070	");
					else
						sb.append("                 				  FROM TBPMS_BDS070	");
	                sb.append("                    				 WHERE BDG06 = :custId");
	                sb.append("									 )");
	                sb.append("    			   GROUP BY BDG06, BDG01,BDG0K) F	");
	                sb.append(" 			 ON A.BOND_NBR = F.BDG01 AND A.CUST_ID = F.BDG06 AND A.CERT_NBR = F.BDG0K");
					sb.append("    LEFT JOIN TBPMS_IQ053 G  ON A.VALU_CRCY_TYPE = G.CUR_COD AND                 ");
					sb.append("               G.MTN_DATE = 								");
					sb.append("               (																	");
					sb.append("                SELECT MAX(MTN_DATE) 						");
					sb.append("                  FROM TBPMS_IQ053 												");
					sb.append("                 WHERE CUR_COD = A.VALU_CRCY_TYPE								");
					sb.append("                ) 																");
					sb.append("    LEFT JOIN VWPRD_MASTER H ON A.BOND_NBR = H.PRD_ID AND H.PTYPE='BND'	");
					sb.append("  where CUST_ID = :custId");
				 	List<Map<String,Object>> list = this.getQueryExecute(sb.toString(), custId);
					result.addRecordList("INV_FBOND", list);
					try {
						//先判斷客戶是否為OBU
						if (!list.isEmpty()) {
							//#1060 幫sot707_inputVO加參數,避免沒發送到電文
							sot707_inputVO.setCustId(custId);
							sot707_inputVO.setProdType("3");
							sot707_inputVO.setIsOBU(obuFlag);
							SOT707OutputVO sot707VO = sot707.getCustAssetBondData(sot707_inputVO);
							//Fetch every fbond under this customer
							for (Map<String,Object> map:list) {
								//prevent text return null value of front fee
								map.put("FRONTFEE1", new BigDecimal(0));	//已付前手息
								map.put("FRONTFEE2", new BigDecimal(0));	//應收前手息
								for(CustAssetBondVO cabVO : sot707VO.getCustAssetBondList()){
									Map<String,Object> tempMap = new HashMap<String, Object>();
									String textTrustNo = cabVO.getTrustNo().trim();
									String textBondNo = cabVO.getBondNo().trim();
									String tableCertNbr = (String)map.get("CERT_NBR");
									String tableBondCode = (String)map.get("BOND_NBR");
									if (tableCertNbr.equals(textTrustNo) && tableBondCode.equals(textBondNo)) {
										System.out.println("TRUST NO MATCHED,BOND NO MATCHED");
										map.put("FRONTFEE1", cabVO.getFrontfee1());	//已付前手息
										map.put("FRONTFEE2", cabVO.getFrontfee2());	//應收前手息
									}
								}
							}

							result.addRecordList("INV_FBOND", list);
						}
					} catch ( Exception e ) {
						logger.error("Text error:"+e.getMessage());
					}
				} catch (Exception e) {
					logger.error("FBOND error:"+e.getMessage());
				}

				try {
					//海外債-金市
					sb = new StringBuilder();
					sb.append("	SELECT M_SE_CODE , M_ISSUER_NM, M_TP_NOMCUR, M_TP_NOMINAL, ");//ISIN CODE, 發行人, 幣別, 面額
					sb.append("        INVEST_COST_AMT, QUTOATION, MARKET_VALUE_AMT, MARKET_VALUE_AMT_TWD ");//投資成本(原幣), 參考報價, 參考市值, 參考市值(台幣)
				    sb.append("   FROM TBCRM_AST_INV_VPBND_1002 ");
					sb.append("  where M_TP_CNTRP = :custId AND M_TRN_GRP = 'BOND' ");
				 	List<Map<String,Object>> list = this.getQueryExecute(sb.toString(), custId);
					result.addRecordList("INV_VPBND", list);
				} catch (Exception e) {
					logger.error("VPBND error:"+e.getMessage());
				}

				try {
					//海外股票-特定金錢信託
					sb = new StringBuilder();
					sb.append("	SELECT PROD_ID , c.PASSBOOK as PROD_NAME , STK_AMT ,	");//股票代號、股票名稱、庫存數量(股)(A)
					sb.append("        to_char(NET_VALUE_DATE, 'yyyy-MM-dd') as NET_VALUE_DATE ,	");//、報價日期
				    sb.append("        REF_STK_PRICE,");//參考收盤價(B)
				    sb.append("        REF_EXCH_RATE , PL_BALANCE ,	");//參考匯率(C)、累計除權
				    sb.append("        B.RISKCATE_ID , ACUM_INT_AMT,");//風險屬性、累計除息
				    sb.append("        (STK_AMT * REF_STK_PRICE) AS VALUE,	");//參考市值
				    sb.append("		   REF_ABS_RET_RATE_ORGD/100 REF_ABS_RET_RATE_ORGD, ORG_STK_PRICE,	REF_AMT_TWD , ");//報酬率、平均買進價格、台幣參考金額
				    sb.append("        CRCY_TYPE, ");//幣別
				    sb.append("	       SUBSTR(TRST_ACC,1,LENGTH(TRST_ACC)-4)||'**'||SUBSTR(TRST_ACC,LENGTH(TRST_ACC)-1,LENGTH(TRST_ACC)) as TRST_ACC");//信託帳號
				    sb.append("   FROM TBCRM_AST_INV_FORD_ETFNSTOCK A                    ");
				    sb.append("   LEFT JOIN VWPRD_MASTER B ON A.PROD_ID = B.PRD_ID AND B.PTYPE='ETF'	");
				    sb.append("   LEFT JOIN TBPRD_ETF C ON A.PROD_ID = C.PRD_ID AND A.STK_EXCHANGE_ID = C.STOCK_CODE ");
					sb.append("  where CUST_ID = :custId ");
				 	List<Map<String,Object>> list = this.getQueryExecute(sb.toString(), custId);
					result.addRecordList("INV_FSTOCK", list);
				} catch (Exception e) {
					logger.error("FSTOCK error:"+e.getMessage());
				}

				try {

					result.addParameter("printInv", "Y");
					Map<String, Object> dataSIMap = this.getInvSI(custId);
					result.addRecordList("INV_SI", (List<Map<String,Object>>) dataSIMap.get("si_list"));
					result.addRecordList("INV_DCI", (List<Map<String,Object>>) dataSIMap.get("dci_list"));
					result.addParameter("invSi"		, (BigDecimal)dataSIMap.get("tot"));

					sb = new StringBuilder();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append(" SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER ");
					sb.append("  WHERE PARAM_TYPE = 'CRM.DCI_CRCY_CHGE_STATUS' ");
					queryCondition.setQueryString(sb.toString());
					result.addParameter("DCI_CRCY_CHGE_STATUS", dam.executeQuery(queryCondition));
				} catch (Exception e) {
					logger.error("SI error:"+e.getMessage());
				}

				try {
				//黃金存褶
				sb = new StringBuilder();
				sb.append(" select SUBSTR(ACC_NBR,1,LENGTH(ACC_NBR)-4)||'**'||SUBSTR(ACC_NBR,LENGTH(ACC_NBR)-1,LENGTH(ACC_NBR)) as ACC_NBR , (CASE WHEN STOCK_UNIT_G > 0 THEN STOCK_UNIT_G ");//帳號
				sb.append("                        WHEN STOCK_UNIT_O > 0 THEN STOCK_UNIT_O ELSE 0 END) as STOCK_AMT , ");//庫存數量(A)
				sb.append("        (CASE WHEN REF_PRICE_TWD > 0 THEN REF_PRICE_TWD ELSE REF_PRICE_USD END) as REF_PRICE , ");//牌價(B)
				sb.append("        (CASE WHEN REF_PRICE_TWD > 0 THEN REF_REAL_VALUE_TWD ELSE REF_REAL_VALUE_USD END) as REF_VALUE, ");//參考現值(A*B)
				sb.append("        (CASE WHEN REF_PRICE_TWD > 0 THEN AVG_BUYIN_COST_TWD ELSE AVG_BUYIN_COST_USD END) as AVG_COST , ");//平均參考買入成本(D)
				sb.append("        (CASE WHEN REF_PRICE_TWD > 0 THEN ( ");
				sb.append("																					  CASE WHEN AVG_BUYIN_COST_TWD<=0 THEN 0	");
				sb.append("																								  ELSE (REF_PRICE_TWD - AVG_BUYIN_COST_TWD)/AVG_BUYIN_COST_TWD	END) 	");
				sb.append("                                                                                  ELSE (	");
				sb.append("                                                                                  CASE WHEN AVG_BUYIN_COST_USD<=0 THEN 0	");
				sb.append("																					  ELSE (REF_PRICE_USD - AVG_BUYIN_COST_USD)/AVG_BUYIN_COST_USD END) END) as REF_PROFIT ,	");//參考損益(B-D)/D
				sb.append("		   CASE WHEN STATUS_TWD = '0' THEN 'TWD'	");
				sb.append("				WHEN STATUS_USD = '0' THEN 'USD' END AS CUR_TYPE	");
				sb.append("        from TBCRM_AST_INV_GOLD ");
				sb.append("  where CUST_ID = :custId AND (STOCK_UNIT_G >0 OR STOCK_UNIT_O>0)	");
			 	List<Map<String,Object>> list = this.getQueryExecute(sb.toString(), custId);
				result.addRecordList("INV_GOLD", list);
				} catch (Exception e) {
					logger.error("GOLD error:"+e.getMessage());
				}
				
				// 奈米投
				try {
					List<Map<String,Object>> nanoList = this.getNanoAsset(custId);
					result.addRecordList("INV_NANO", nanoList);
					
				} catch (Exception e) {
					logger.error("NANO error:"+e.getMessage());
				}
			}
			//保險
			if (printAll.equals("Y") || docVo.getPRINT_INS().equals("Y")) {
				result.addParameter("printIns", "Y");
				try {
					// 進件來源：台北富邦銀行
					Map<String, Object> dataInsMap = this.getIns(custId);

					result.addParameter("insTot", (BigDecimal)dataInsMap.get("tot"));// 富邦保險(折台累計淨值保費)
					result.addRecordList("INS", (List<Map<String,Object>>) dataInsMap.get("list"));
				} catch (Exception e) {
					logger.error("INS error:"+e.getMessage(),e);
				}

				try {
					// 進件來源：日盛保代
					Map<String, Object> dataInsMap = this.getInsJSB(custId);

					result.addParameter("insTotJSB", dataInsMap.get("totJSB")); // 日盛保代(折台累計淨值保費)
					result.addRecordList("INS_JSB", (List<Map<String,Object>>) dataInsMap.get("list"));
				} catch (Exception e) {
					logger.error("JSB_INS error:"+e.getMessage(),e);
				}
			}
			
			//證券複委託
			if(subTot != null && subTot.compareTo(BigDecimal.ZERO) != 0){
				
				//海外股票-證券複委託
				if(subFstock != null  && subFstock.compareTo(BigDecimal.ZERO) != 0){
					try{
						sb = new StringBuilder();
						sb.append(" SELECT ACCT_NBR, PROD_ID, PROD_NAME, AVG_COST_PRICE, QTY, UNIT_PRICE, ");
					    sb.append("PRICE_DATE, AUM_FC, BENEFIT_AMT1, BENEFIT_RATE1, ");
					    sb.append(" ROUND(AUM_FC * NVL(BUY_RATE, 1), 2) AS AUM_TW, CURRENCY ");
						sb.append(" FROM ( ");
					    sb.append(" SELECT ACCT_NBR, PROD_ID, PROD_NAME, AVG_COST_PRICE, SUM(NVL(QTY, 0)) AS QTY, ");
					    sb.append(" UNIT_PRICE, PRICE_DATE, SUM(NVL(AUM_FC, 0)) AS AUM_FC, ");
					    sb.append(" SUM(NVL(BENEFIT_AMT1, 0)) AS BENEFIT_AMT1, AVG(BENEFIT_RATE1) AS BENEFIT_RATE1, ");
					    sb.append(" TBCRM_AST_INV_SEC_STOCK.CURRENCY, TBPMS_IQ053.BUY_RATE ");
					    sb.append(" FROM TBCRM_AST_INV_SEC_STOCK ");
					    sb.append(" LEFT JOIN ( SELECT CUR_COD, BUY_RATE, MTN_DATE, ");
					    sb.append(" ROW_NUMBER() OVER (PARTITION BY CUR_COD ORDER BY MTN_DATE DESC) AS RN ");
					    sb.append(" FROM TBPMS_IQ053 ");
					    sb.append(" ) TBPMS_IQ053 ");
					    sb.append(" ON TBCRM_AST_INV_SEC_STOCK.CURRENCY = TBPMS_IQ053.CUR_COD ");
					    sb.append(" AND TBPMS_IQ053.RN = 1 ");
					    sb.append(" WHERE CUST_ID = :custId ");
					    sb.append(" AND DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBCRM_AST_INV_SEC_STOCK) ");
					    sb.append(" GROUP BY ACCT_NBR, PROD_ID, PROD_NAME, AVG_COST_PRICE, UNIT_PRICE, ");
					    sb.append(" PRICE_DATE, TBCRM_AST_INV_SEC_STOCK.CURRENCY, TBPMS_IQ053.BUY_RATE ");
						sb.append(" ) T "); 
					
						List<Map<String,Object>> list = this.getQueryExecute(sb.toString(), custId);
						result.addRecordList("SUB_FSTOCK", list);
					}catch(Exception e){
						logger.error("Fstock error:"+e.getMessage(),e);
					}
				}
				
				//海外債券-證券複委託
				if(subFbond != null  && subFbond.compareTo(BigDecimal.ZERO) != 0){
					try{
						sb = new StringBuilder();
						sb.append(" SELECT ACCT_NBR, PROD_ID, PROD_NAME, ISSUE_BROKER_NAME, ");
					    sb.append(" RANK_LEVEL_MOODYS, RANK_LEVEL_SP, RANK_LEVEL_FITCH, ");
					    sb.append(" MATURITY_DATE, INVEST_AMT_FC, QTY, UNIT_PRICE, ");
					    sb.append(" PRICE_DATE, AUM_FC, BENEFIT_AMT2, ");
					    sb.append(" DIVIDENT_AMT_FC, BENEFIT_RATE2, CURRENCY, ROUND(AUM_FC * NVL(BUY_RATE, 1), 2) AS AUM_TW ");
						sb.append(" FROM ( ");
					    sb.append(" SELECT ACCT_NBR, PROD_ID, PROD_NAME, ISSUE_BROKER_NAME, ");
					    sb.append(" RANK_LEVEL_MOODYS, RANK_LEVEL_SP, RANK_LEVEL_FITCH, ");
					    sb.append(" MATURITY_DATE, SUM(NVL(INVEST_AMT_FC, 0)) AS INVEST_AMT_FC, ");
					    sb.append(" SUM(NVL(QTY, 0)) AS QTY, UNIT_PRICE, ");
					    sb.append(" PRICE_DATE, SUM(NVL(AUM_FC, 0)) AS AUM_FC, ");
					    sb.append(" SUM(NVL(BENEFIT_AMT2, 0)) AS BENEFIT_AMT2, ");
					    sb.append(" DIVIDENT_AMT_FC, AVG(BENEFIT_RATE2) AS BENEFIT_RATE2, ");
					    sb.append(" TBCRM_AST_INV_SEC_BOND.CURRENCY, TBPMS_IQ053.BUY_RATE ");
					    sb.append(" FROM TBCRM_AST_INV_SEC_BOND ");
					    sb.append(" LEFT JOIN ( SELECT CUR_COD, BUY_RATE, MTN_DATE, ");
					    sb.append(" ROW_NUMBER() OVER (PARTITION BY CUR_COD ORDER BY MTN_DATE DESC) AS RN ");
					    sb.append(" FROM TBPMS_IQ053 ");
					    sb.append(" ) TBPMS_IQ053 ");
					    sb.append(" ON TBCRM_AST_INV_SEC_BOND.CURRENCY = TBPMS_IQ053.CUR_COD ");
					    sb.append(" WHERE CUST_ID = :custId ");
					    sb.append(" AND TBPMS_IQ053.RN = 1 ");
					    sb.append(" AND DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBCRM_AST_INV_SEC_BOND) ");
					    sb.append(" GROUP BY ACCT_NBR, PROD_ID, PROD_NAME, ISSUE_BROKER_NAME, ");
					    sb.append(" RANK_LEVEL_MOODYS, RANK_LEVEL_SP, RANK_LEVEL_FITCH, ");
					    sb.append(" MATURITY_DATE, UNIT_PRICE, PRICE_DATE, ");
					    sb.append(" DIVIDENT_AMT_FC, TBCRM_AST_INV_SEC_BOND.CURRENCY, TBPMS_IQ053.BUY_RATE ");
						sb.append(" ) T ");
						
						List<Map<String,Object>> list = this.getQueryExecute(sb.toString(), custId);
						result.addRecordList("SUB_FBOND", list);
					}catch(Exception e){
						logger.error("Fbond error:"+e.getMessage(),e);
					}
				}
				//境外結構型商品-證券複委託
				if(subSn != null  && subSn.compareTo(BigDecimal.ZERO) != 0){
					try{
						sb = new StringBuilder();
						sb.append(" SELECT ACCT_NBR, PROD_ID, PROD_NAME, APPLY_START_DATE, ");
					    sb.append(" MATURITY_DATE, INVEST_AMT_FC, DIVIDENT_AMT_FC, UNIT_PRICE, ");
					    sb.append(" PRICE_DATE, AUM_FC, BENEFIT_AMT2, BENEFIT_RATE2, CURRENCY, ");
					    sb.append(" ROUND(AUM_FC * NVL(BUY_RATE, 1), 2) AS AUM_TW ");
						sb.append("	FROM ( ");
					    sb.append(" SELECT CRMSN.ACCT_NBR, CRMSN.PROD_ID, CRMSN.PROD_NAME, TFBSN.APPLY_START_DATE,");
					    sb.append(" CRMSN.MATURITY_DATE, SUM(NVL(CRMSN.INVEST_AMT_FC, 0)) AS INVEST_AMT_FC,");
					    sb.append(" SUM(NVL(CRMSN.DIVIDENT_AMT_FC, 0)) AS DIVIDENT_AMT_FC, CRMSN.UNIT_PRICE, ");
					    sb.append(" CRMSN.PRICE_DATE, SUM(NVL(CRMSN.AUM_FC, 0)) AS AUM_FC, ");
					    sb.append(" SUM(NVL(CRMSN.BENEFIT_AMT2, 0)) AS BENEFIT_AMT2, ");
					    sb.append(" AVG(CRMSN.BENEFIT_RATE2) AS BENEFIT_RATE2, ");
					    sb.append(" CRMSN.CURRENCY, TBPMS_IQ053.BUY_RATE ");
					    sb.append(" FROM TBCRM_AST_INV_SEC_SN CRMSN ");
					    sb.append(" LEFT JOIN TBPRD_TFB_FOREIGN_SN TFBSN ");
					    sb.append(" ON CRMSN.PROD_ID = TFBSN.STOCK_CODE ");
					    sb.append(" LEFT JOIN ( SELECT CUR_COD, BUY_RATE, MTN_DATE, ");
					    sb.append(" ROW_NUMBER() OVER (PARTITION BY CUR_COD ORDER BY MTN_DATE DESC) AS RN ");
					    sb.append(" FROM TBPMS_IQ053 ");
					    sb.append(" ) TBPMS_IQ053 ");
					    sb.append(" ON CRMSN.CURRENCY = TBPMS_IQ053.CUR_COD ");
					    sb.append(" AND TBPMS_IQ053.RN = 1 ");
					    sb.append(" WHERE CRMSN.CUST_ID = :custId ");
					    sb.append(" AND CRMSN.DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBCRM_AST_INV_SEC_SN) ");
					    sb.append(" GROUP BY CRMSN.ACCT_NBR, CRMSN.PROD_ID, CRMSN.PROD_NAME, TFBSN.APPLY_START_DATE, ");
					    sb.append(" CRMSN.MATURITY_DATE, CRMSN.UNIT_PRICE, CRMSN.PRICE_DATE, CRMSN.CURRENCY, TBPMS_IQ053.BUY_RATE ");
						sb.append(" ) T ");
						
						List<Map<String,Object>> list = this.getQueryExecute(sb.toString(), custId);
						result.addRecordList("SUB_SN", list);
					}catch(Exception e){
						logger.error("FSn error:"+e.getMessage(),e);
					}
				}
				//境內結構型商品-證券複委託
				if(subDsn != null  && subDsn.compareTo(BigDecimal.ZERO) != 0){
					try{
						sb = new StringBuilder();
						sb.append(" SELECT ACCT_NBR, STOCK_CODE, STOCK_NAME, TXN_DATE, ");
						sb.append(" SETTLE_DATE,SUM(NVL(INVEST_COST, 0)) AS INVEST_COST, ");
						sb.append(" SUM(NVL(DIVIDEND_AMT, 0)) AS DIVIDEND_AMT, REFER_PRICE, ");
						sb.append(" SNAP_DATE, NVL(SUM(AUM_TW * NVL(BUY_RATE, 1)), 0) AS AUM_TW, CURRENCY ");
						sb.append(" FROM TBCRM_AST_INV_SEC_DSN	");
						sb.append(" LEFT JOIN ( SELECT CUR_COD, BUY_RATE, MTN_DATE, ");
					    sb.append(" ROW_NUMBER() OVER (PARTITION BY CUR_COD ORDER BY MTN_DATE DESC) AS RN ");
					    sb.append(" FROM TBPMS_IQ053 ");
					    sb.append(" ) TBPMS_IQ053 ");
					    sb.append(" ON TBCRM_AST_INV_SEC_DSN.CURRENCY = TBPMS_IQ053.CUR_COD ");
						sb.append(" WHERE CUSTOMER_ID = :custId AND INS_DATE = (SELECT MAX(INS_DATE) FROM TBCRM_AST_INV_SEC_DSN) ");
						sb.append(" AND TBPMS_IQ053.RN = 1 ");
						sb.append(" GROUP BY ACCT_NBR, STOCK_CODE, ");
						sb.append(" STOCK_NAME, TXN_DATE, SETTLE_DATE, REFER_PRICE, ");
						sb.append(" SNAP_DATE, CURRENCY ");
						
						List<Map<String,Object>> list = this.getQueryExecute(sb.toString(), custId);
						result.addRecordList("SUB_DSN", list);
					}catch(Exception e){
						logger.error("FDSn error:"+e.getMessage(),e);
					}
				}
			}
			//資產圖示
			//AUM趨勢圖
			if (printAllChart.equals("Y") || docVo.getPRINT_AUM().equals("Y")) {
				sb = new StringBuilder();
				sb.append("	SELECT * FROM (");
				sb.append(" SELECT DATA_YEAR||DATA_MONTH as YEAR_MONTH, AVG_AUM_AMT 				");
				sb.append(" FROM TBCRM_CUST_AUM_MONTHLY_HIST 									");
				sb.append(" WHERE CUST_ID = :custId 											");
//				sb.append(" AND ROWNUM <= 12 													");
				sb.append(" ORDER BY DATA_YEAR DESC, DATA_MONTH DESC 							");
				sb.append("	) WHERE ROWNUM <=12");

                List<Map<String,Object>> list = this.getQueryExecute(sb.toString(), custId);
				result.addRecordList("CHART_AUM_LIST", list);
			}
			
			//存投保分佈圖
			if (printAllChart.equals("Y") || docVo.getPRINT_IIL().equals("Y")) {
				List list = new ArrayList();
				BigDecimal totalAsset = depositF.add(depositTwd)
						 						.add(invFund).add(invSi).add(invSn).add(invFbond).add(invEtf).add(invGold).add(invNano).add(invVpbnd)
						 						.add(insTot);
				BigDecimal inv = invFund.add(invFbond).add(invSi).add(invEtf).add(invSn).add(invGold).add(invNano).add(invVpbnd);
				BigDecimal invRatio = totalAsset.longValue()<=0?new BigDecimal(0):inv.divide(totalAsset,4,RoundingMode.HALF_UP).multiply(new BigDecimal(100));
				BigDecimal insRatio = totalAsset.longValue()<=0?new BigDecimal(0):insTot.divide(totalAsset,4,RoundingMode.HALF_UP).multiply(new BigDecimal(100));
				Map map = new HashMap();
				map = new HashMap();
				map.put("AST_TYPE","投資");
				map.put("AMT", invRatio);
				map.put("TOT", totalAsset);
				list.add(0,map);
				map = new HashMap();
				map.put("AST_TYPE","保險");
				map.put("AMT", insRatio);
				map.put("TOT", totalAsset);
				list.add(1,map);
				map = new HashMap();
				map.put("AST_TYPE","存款");
				map.put("AMT", new BigDecimal(100).subtract(invRatio).subtract(insRatio));
				map.put("TOT", totalAsset);
				list.add(2,map);
				result.addRecordList("CHART_DEP_INV_INS_RATIO",list);
			}

			//總資產計價幣別圖
			if (printAllChart.equals("Y") || docVo.getPRINT_CUR().equals("Y")) {
				sb = new StringBuilder();
				sb.append(" SELECT SUM(INV_AMT_TWD) AMT , CUR_ID ");
				sb.append("   FROM MVCRM_AST_ALLPRD_DETAIL                ");
				sb.append("  WHERE CUST_ID = :custId AND INV_AMT_TWD > 0	");
				sb.append("  GROUP BY CUST_ID , CUR_ID                    ");
				sb.append("  ORDER BY CUST_ID,CUR_ID                      ");
                List<Map<String,Object>> list = this.getQueryExecute(sb.toString(), custId);
				result.addRecordList("CHART_ASSET_CURR_RATIO", list);
			}

			//投資資產類別圖
			if (printAllChart.equals("Y") || docVo.getPRINT_TYPE().equals("Y")) {
				sb = new StringBuilder();
				sb.append(" SELECT SUM(INV_AMT_TWD) AMT ,						 		");
			    sb.append("        (case when AST_TYPE='06' THEN 'RP複委託' 		 		");
			    sb.append("              WHEN AST_TYPE='07' THEN '債券型基金'		 		");
			    sb.append("              WHEN AST_TYPE='08' THEN '國內基金'		 		");
			    sb.append("              WHEN AST_TYPE='09' THEN '海外基金'		 		");
			    sb.append("              WHEN AST_TYPE='10' THEN '海外商品'		 		");
			    sb.append("              WHEN AST_TYPE='12' THEN '海外股票' 		 		");
			    sb.append("              WHEN AST_TYPE='13' THEN '指定單獨信託'     		");
			    sb.append("              WHEN AST_TYPE='15' THEN '組合式商品(SI)'   		");
			    sb.append("              WHEN AST_TYPE='16' THEN '組合式商品(DCI)'  		");
			    sb.append("              WHEN AST_TYPE='17' THEN '黃金存摺'     	 		");
			    sb.append("              WHEN AST_TYPE='19' THEN '外國債券-自營平台'		"); //海外債-金市
			    sb.append("              WHEN AST_TYPE='08' THEN '債券型基金' END ) 		");
			    sb.append("        AS AST_TYPE                                   		");
				sb.append(" FROM MVCRM_AST_ALLPRD_DETAIL                         	   	");
				sb.append(" WHERE AST_TYPE NOT IN ('01','02','03','04','05','14', '18') AND 	");
				sb.append("		INV_AMT_TWD > 0 AND CUST_ID = :custId	");
				sb.append(" GROUP BY AST_TYPE,PROD_NAME                          		");
				sb.append(" ORDER BY AST_TYPE,PROD_NAME                          		");
                List<Map<String,Object>> list = this.getQueryExecute(sb.toString(), custId);
				result.addRecordList("CHART_INV_TYPE", list);
			}

			//基金投資區域圖
			if (printAllChart.equals("Y") || docVo.getPRINT_FUND_MKT().equals("Y")) {
				sb = new StringBuilder();
				sb.append(" SELECT SUM(INV_AMT_TWD) AS AMT , E.PARAM_NAME             				");
				sb.append("   FROM MVCRM_AST_ALLPRD_DETAIL A										");
//				sb.append("   LEFT JOIN TBCRM_AST_INV_DTL B ON A.CUST_ID = B.CUST_ID AND 			");
//				sb.append("                                    A.CERT_NBR = B.CERT_NBR  			");
				sb.append("   LEFT JOIN TBPRD_FUND C        ON A.PROD_ID = C.PRD_ID					");
				sb.append("   LEFT JOIN TBPRD_INVEST_AREA D ON C.GLOBAL_ID = D.GLOBAL_ID			");
				sb.append("   LEFT JOIN TBSYSPARAMETER E    ON E.PARAM_TYPE = 'PRD.MKT_TIER2' AND 	");
				sb.append("                               D.MKT_TIER2 = E.PARAM_CODE				");
				sb.append("  WHERE AST_TYPE IN ('07','08','09') and a.CUST_ID = :custId				");
				sb.append("  GROUP BY A.CUST_ID ,A.PROD_ID,A.CERT_NBR,D.GLOBAL_ID,					");
				sb.append("          A.AST_TYPE,E.PARAM_NAME					");
				sb.append("  ORDER BY D.GLOBAL_ID													");
                List<Map<String,Object>> list = this.getQueryExecute(sb.toString(), custId);
				result.addRecordList("CHART_FUND_REG", list);
			}

			//投資組合分佈圖
			if (printAllChart.equals("Y") || docVo.getPRINT_PORTFOLIO().equals("Y")) {
				sb = new StringBuilder();
				sb.append(" SELECT SUM(INV_AMT_TWD) AMT , PROD_NAME              		");
				sb.append(" FROM MVCRM_AST_ALLPRD_DETAIL                         		");
				sb.append(" WHERE AST_TYPE NOT IN ('01','02','03','04','05','14','18') AND INV_AMT_TWD>0 AND ");
				sb.append("       CUST_ID = :custId                              		");
				sb.append(" GROUP BY AST_TYPE,PROD_NAME                          		");
				sb.append(" ORDER BY AST_TYPE,PROD_NAME                          		");
                List<Map<String,Object>> list = this.getQueryExecute(sb.toString(), custId);
				result.addRecordList("CHART_INV_COMBINATION", list);
			}
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
		} catch (JBranchException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
		};
		return result;
	}

	private Map<String,Object> getTot(String custID) throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = null;
		Map<String , Object> data = new HashMap<String,Object>();

		try {
			List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();

			//客戶姓名
			//客戶主檔,kyc資料
			TBCRM_CUST_MASTVO custMastVo =
					(TBCRM_CUST_MASTVO) dam.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, custID);
			//基本資料
			if(custMastVo != null){
				data.put("CUST_NAME", custMastVo.getCUST_NAME());//客戶姓名
			}
			sb = new StringBuilder();
			sb.append("SELECT CUST_RISK_AFR , CREATE_DATE,EXPIRY_DATE	");
			sb.append("	FROM TBKYC_INVESTOREXAM_M	");
			sb.append("	WHERE CUST_ID = :custId	");
			queryCondition.setObject("custId", custID);
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if(!list.isEmpty()){
				data.put("KYC_VALUE", list.get(0).get("CUST_RISK_AFR"));
				data.put("KYC_CREATE_DATE", FormatUtils.T1Format((Date)list.get(0).get("CREATE_DATE")).substring(0,9));
				data.put("KYC_DUE_DATE", FormatUtils.T1Format((Date)list.get(0).get("EXPIRY_DATE")).substring(0,9));
			}

			BigDecimal depositTwd = (BigDecimal) new BigDecimal(0);
			BigDecimal depositFor = (BigDecimal) new BigDecimal(0);
			BigDecimal totDep = (BigDecimal) new BigDecimal(0);
			BigDecimal invFund = (BigDecimal) new BigDecimal(0);
			BigDecimal invSi = (BigDecimal) new BigDecimal(0);
			BigDecimal invSn = (BigDecimal) new BigDecimal(0);
			BigDecimal invFbond = (BigDecimal) new BigDecimal(0);
			BigDecimal invEtf = (BigDecimal) new BigDecimal(0);
			BigDecimal invGold = (BigDecimal) new BigDecimal(0);
			BigDecimal invNano = (BigDecimal) new BigDecimal(0);
			BigDecimal invVpbnd = (BigDecimal) new BigDecimal(0);
			BigDecimal totInv = (BigDecimal) new BigDecimal(0);
			BigDecimal insTot = (BigDecimal) new BigDecimal(0);
			BigDecimal subFstock = (BigDecimal) new BigDecimal(0);
			BigDecimal subFbond = (BigDecimal) new BigDecimal(0);
			BigDecimal subSn = (BigDecimal) new BigDecimal(0);
			BigDecimal subDsn = (BigDecimal) new BigDecimal(0);
			BigDecimal subTot = (BigDecimal) new BigDecimal(0);

			sb = new StringBuilder();
			sb.append("SELECT CUST_ID,SUM(CASE WHEN AST_TYPE IN ('01','02','03') THEN NOW_AMT_TWD ELSE 0 END) DEPOSIT_TWD, ");
			sb.append("SUM(CASE WHEN AST_TYPE IN ('04','05') THEN NOW_AMT_TWD ELSE 0 END) DEPOSIT_FOR, ");
			sb.append("SUM(CASE WHEN AST_TYPE IN ('12') THEN NOW_AMT_TWD ELSE 0 END) INV_ETF, ");
			sb.append("SUM(CASE WHEN AST_TYPE IN ('17') THEN NOW_AMT_TWD ELSE 0 END) INV_GOLD, ");
			sb.append("SUM(CASE WHEN AST_TYPE IN ('19') THEN NOW_AMT_TWD ELSE 0 END) INV_VPBND ");
			sb.append("FROM MVCRM_AST_ALLPRD_DETAIL WHERE  CUST_ID = :custId ");
			sb.append("GROUP BY CUST_ID");
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setObject("custId", custID);
			queryCondition.setQueryString(sb.toString());
			list = dam.exeQuery(queryCondition);
			if(!list.isEmpty()){
				depositTwd = (BigDecimal)list.get(0).get("DEPOSIT_TWD");  //台幣存款
				depositFor = (BigDecimal)list.get(0).get("DEPOSIT_FOR");  //外幣存款存款
				invEtf = (BigDecimal)list.get(0).get("INV_ETF");  //海外ETF/海外股存款
				invGold = (BigDecimal)list.get(0).get("INV_GOLD");  //黃金存摺
				invVpbnd = (BigDecimal)list.get(0).get("INV_VPBND");  //海外債-金市
			}

			sb = new StringBuilder();
			sb.append(" SELECT CUST_ID,SUM(CASE WHEN B.PRD_ID IS NOT NULL THEN REF_AMT_TWD ELSE 0 END) INV_FBOND ");
			sb.append(" ,SUM(CASE WHEN C.PRD_ID IS NOT NULL THEN REF_AMT_TWD ELSE 0 END) INV_SN ");
			sb.append(" FROM TBCRM_AST_INV_FBOND M LEFT JOIN TBPRD_BOND B ON M.BOND_NBR = B.PRD_ID ");
			sb.append(" LEFT JOIN TBPRD_SN C ON M.BOND_NBR = C.PRD_ID ");
			sb.append(" WHERE CUST_ID = :custId ");
			sb.append(" GROUP BY M.CUST_ID ");
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setObject("custId", custID);
			queryCondition.setQueryString(sb.toString());
			list = dam.exeQuery(queryCondition);
			if(!list.isEmpty()){
				invFbond = (BigDecimal)list.get(0).get("INV_FBOND"); //海外債
				invSn = (BigDecimal)list.get(0).get("INV_SN");  //SN
			}
			
			sb = new StringBuilder();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append(" SELECT NVL(SUM(NVL(AUM_FC, 0) * NVL(BUY_RATE, 1)), 0) AS AUM ");
			sb.append(" FROM  TBCRM_AST_INV_SEC_STOCK ");
			sb.append(" LEFT JOIN (SELECT CUR_COD, BUY_RATE, MTN_DATE,  ");
			sb.append(" ROW_NUMBER() OVER (PARTITION BY CUR_COD ORDER BY MTN_DATE DESC) AS RN ");
			sb.append(" FROM TBPMS_IQ053 ) TBPMS_IQ053 ");
			sb.append(" ON TBCRM_AST_INV_SEC_STOCK.CURRENCY = TBPMS_IQ053.CUR_COD ");
			sb.append(" WHERE CUST_ID =:custId ");
			sb.append(" AND TBPMS_IQ053.RN = 1 ");
			sb.append(" AND DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBCRM_AST_INV_SEC_STOCK) ");
		
			queryCondition.setObject("custId", custID);
			queryCondition.setQueryString(sb.toString());
			list = dam.exeQuery(queryCondition);
			if(!list.isEmpty()){
				subFstock = (BigDecimal)list.get(0).get("AUM"); //海外股票-證券複委託
			}
			
			sb = new StringBuilder();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append(" SELECT NVL(SUM(NVL(AUM_FC, 0) * NVL(BUY_RATE, 1)), 0) AS AUM ");
			sb.append(" FROM  TBCRM_AST_INV_SEC_BOND ");
			sb.append(" LEFT JOIN (SELECT CUR_COD, BUY_RATE, MTN_DATE,  ");
			sb.append(" ROW_NUMBER() OVER (PARTITION BY CUR_COD ORDER BY MTN_DATE DESC) AS RN ");
			sb.append(" FROM TBPMS_IQ053 ) TBPMS_IQ053 ");
			sb.append(" ON TBCRM_AST_INV_SEC_BOND.CURRENCY = TBPMS_IQ053.CUR_COD ");
			sb.append(" WHERE CUST_ID =:custId ");
			sb.append(" AND TBPMS_IQ053.RN = 1 ");
			sb.append(" AND DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBCRM_AST_INV_SEC_BOND) ");
			queryCondition.setObject("custId", custID);
			queryCondition.setQueryString(sb.toString());
			list = dam.exeQuery(queryCondition);
			if(!list.isEmpty()){
				subFbond = (BigDecimal)list.get(0).get("AUM"); //海外債券-證券複委託
			}
			
			sb = new StringBuilder();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append(" SELECT NVL(SUM(NVL(AUM_FC, 0) * NVL(BUY_RATE, 1)), 0) AS AUM ");
			sb.append(" FROM  TBCRM_AST_INV_SEC_SN ");
			sb.append(" LEFT JOIN (SELECT CUR_COD, BUY_RATE, MTN_DATE,  ");
			sb.append(" ROW_NUMBER() OVER (PARTITION BY CUR_COD ORDER BY MTN_DATE DESC) AS RN ");
			sb.append(" FROM TBPMS_IQ053 ) TBPMS_IQ053 ");
			sb.append(" ON TBCRM_AST_INV_SEC_SN.CURRENCY = TBPMS_IQ053.CUR_COD ");
			sb.append(" WHERE CUST_ID =:custId ");
			sb.append(" AND DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBCRM_AST_INV_SEC_SN) ");
			queryCondition.setObject("custId", custID);
			queryCondition.setQueryString(sb.toString());
			list = dam.exeQuery(queryCondition);
			if(!list.isEmpty()){
				subSn = (BigDecimal)list.get(0).get("AUM"); //境外結構型商品-證券複委託
			}
			
			sb = new StringBuilder();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append(" SELECT NVL(SUM(NVL(AUM_TW, 0) * NVL(BUY_RATE, 1)), 0) AS AUM ");
			sb.append(" FROM  TBCRM_AST_INV_SEC_DSN ");
			sb.append(" LEFT JOIN (SELECT CUR_COD, BUY_RATE, MTN_DATE,  ");
			sb.append(" ROW_NUMBER() OVER (PARTITION BY CUR_COD ORDER BY MTN_DATE DESC) AS RN ");
			sb.append(" FROM TBPMS_IQ053 ) TBPMS_IQ053 ");
			sb.append(" ON TBCRM_AST_INV_SEC_DSN.CURRENCY = TBPMS_IQ053.CUR_COD ");
			sb.append(" WHERE CUSTOMER_ID =:custId ");
			sb.append(" AND INS_DATE = (SELECT MAX(INS_DATE) FROM TBCRM_AST_INV_SEC_DSN) ");
			sb.append(" GROUP BY CUSTOMER_ID ");
			queryCondition.setObject("custId", custID);
			queryCondition.setQueryString(sb.toString());
			list = dam.exeQuery(queryCondition);
			if(!list.isEmpty()){
				subDsn = (BigDecimal)list.get(0).get("AUM"); //境內結構型商品-證券複委託
			}
			
			data.put("DEPOSIT_TWD", depositTwd);  					// 台幣存款
			data.put("DEPOSIT_FOR", depositFor); 					// 外幣存款
			totDep = depositTwd.add(depositFor);
			data.put("TOT_DEP", totDep); 							// 總存款
			data.put("INV_ETF", invEtf); 							// 海外ETF/海外股
			data.put("INV_GOLD", invGold); 							// 黃金存摺
			invNano = (BigDecimal) this.getNano(custID).get("NANO");
			data.put("INV_NANO", invNano); // 奈米投
			data.put("INV_VPBND", invVpbnd); 						// 海外債-金市
			data.put("INV_FBOND", invFbond);  						// 海外債
			data.put("INV_SN", invSn); 								// SN
			data.put("SUB_FSTOCK", subFstock);						//海外股票-證券複委託
			data.put("SUB_FBOND", subFbond);						//海外債券-證券複委託
			data.put("SUB_SN", subSn);								//境外結構型商品-證券複委託
			data.put("SUB_DSN", subDsn);							//境內結構型商品-證券複委託
			//取得基金資料
			Map<String,Object> invMap = this.getInvFund(custID,"byCERT_NBR");
			invFund = (BigDecimal)invMap.get("tot");
			data.put("INV_FUND", invFund); //基金
			//取得SI資料
			Map<String,Object> siMap = this.getInvSI(custID);
			invSi = (BigDecimal)siMap.get("tot");
			data.put("INV_SI", invSi); //組合式商品
			totInv = invFund.add(invEtf).add(invFbond).add(invSn).add(invSi).add(invGold).add(invVpbnd).add(invNano);
			data.put("TOT_INV", totInv); //總投資金額
			//取得保險資料（進件來源：台北富邦銀行）
			Map<String,Object> insMap = this.getIns(custID);
			//取得保險資料（進件來源：日盛保代）
			Map<String, Object> insMapJSB = this.getInsJSB(custID);
			insTot = (BigDecimal)insMap.get("tot");
			BigDecimal insTotJSB = (BigDecimal) insMapJSB.get("totJSB");
			data.put("TOT_INS", insTot.add(insTotJSB)); //保險商品（富邦 + 日盛）
			
			//證券複委託總金額
			subTot = subTot.add(subFstock).add(subFbond).add(subSn).add(subDsn); 
			data.put("TOT_SUB", subTot); //證券複委託加總
			data.put("TOT_ASSET", totDep.add(totInv).add(insTot).add(insTotJSB).add(subTot)); //總資產

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return data;
	}
	
	// 奈米投_參考市值(台幣)加總
	private Map<String,Object> getNano(String custID) throws JBranchException {
		Map<String, Object> data = new HashMap<String,Object>();
		BigDecimal nano = new BigDecimal(0);
		List<Map<String, Object>> list = this.getNanoAsset(custID);
		
		for (Map<String, Object> map : list) {
			nano = nano.add(new BigDecimal(map.get("MARKET_VAL_TWD").toString()));
		}
		data.put("NANO", nano);
		return data;
	}

	private Map<String,Object> getInvFund(String custID, String fundSortType) throws JBranchException {
		List<Map<String,Object>> result;
		BigDecimal totFund = new BigDecimal(0);
		Map<String,Object> dataMap = new HashMap<String,Object>();
		StringBuilder sb;
		//共同基金-特定金錢信託
		try {

			//判斷DBU OBU

			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
			SOT701InputVO sot701_inputVO = new SOT701InputVO();
			SOT707 sot707 = (SOT707) PlatformContext.getBean("sot707");
			SOT707InputVO sot707_inputVO = new SOT707InputVO();

			String obuFlag = "";
			try {

				sot701_inputVO.setCustID(custID);
				FP032675DataVO dataOBU = sot701.getFP032675Data(sot701_inputVO);
				obuFlag = dataOBU.getObuFlag();
				//前手息用電文 njbrvb1 贖回庫存查詢
				sot707_inputVO.setCustId(custID);
				sot707_inputVO.setProdType("2");
				sot707_inputVO.setIsOBU(obuFlag == "Y" ? "Y" : "N");

				dataMap.put("obuFlag", obuFlag);
			} catch (Exception e) {
				logger.error("DBO,OBU justified failed "+e.getMessage());
			}
			List<CustAssetFundVO> AssetFunList = new ArrayList<CustAssetFundVO>();
			//基金電文
			try {
				AssetFunList = getNFVIPA(custID , obuFlag);
			} catch (Exception e ) {
				logger.error("getCustAssetNFD failed,",e);
			}

			sb = new StringBuilder();

			sb.append("SELECT B.FUND_CNAME_S ,	");
			sb.append("	TOTAL_ACUM_INV_UNIT ,	");
			sb.append("	to_char(SIGN_DATE , 'yyyy-MM-dd') as SIGN_DATE ,	");
			sb.append("	(CASE WHEN INV_CRCY_TYPE = 'TWD' THEN INV_AMT_TWD ELSE INV_AMT_ORGD END) INV_AMT_ORGD ,");
			sb.append("	CERT_NBR ,	");
			sb.append("	RISKCATE_ID ,	");
			sb.append("	to_char(NET_VALUE_DATE , 'yyyy-MM-dd') as NET_VALUE_DATE ,	");
			sb.append("	REF_RDMP_NET_VALUE ,	");
			sb.append("	nvl(AUM_CASH_DIVID,0) as AUM_CASH_DIVID ,	");
			sb.append("	PARAM_NAME as INV_TYPE, REF_BUYIN_EXCH_RATE, INV_AMT_TWD ,	");
			sb.append(" (CASE WHEN A.INV_CRCY_TYPE = 'TWD' THEN ROUND(REF_TOTAL_REAL_VALUE_TWD, 2)  ");
            sb.append("  ELSE (ROUND(TOTAL_ACUM_INV_UNIT * REF_RDMP_NET_VALUE, 2)) END )as VALUE, ");
			sb.append("	REF_ABS_RET_RATE_TWD/100 as PROFIT_RATE,	");
			sb.append("	REF_ABS_RET_RATE_ORGD , REF_TOTAL_REAL_VALUE_TWD , A.VALU_CRCY_TYPE , A.INV_CRCY_TYPE,	");
			sb.append("	CASE WHEN D.AC2M2 = A.FUND_CODE THEN '動態鎖利_母' ");
			sb.append("		 WHEN (D.AC2M5 = A.FUND_CODE OR D.AC2M7 = A.FUND_CODE OR D.AC2M9 = A.FUND_CODE OR D.AC2M11 = A.FUND_CODE OR D.AC2M13 = A.FUND_CODE) THEN '動態鎖利_子' ");
			sb.append("		 ELSE '一般' END AS REMARK ");
			sb.append("	FROM TBCRM_AST_INV_FUND_OVS A	");
			sb.append("	LEFT JOIN TBPRD_FUND B ON A.FUND_CODE = B.PRD_ID ");
			sb.append("	LEFT JOIN TBSYSPARAMETER C ON C.PARAM_TYPE='CRM.CRM232_INV_WAY_NBR' AND C.PARAM_CODE=A.INV_WAY_NBR	");
			sb.append("	LEFT JOIN TBCRM_AC2MARK D ON A.CERT_NBR = D.AC201M ");
			sb.append("	where CUST_ID = :custId	");
			sb.append("UNION	ALL	");
			sb.append("SELECT B.FUND_CNAME_S ,	");
			sb.append("	TOTAL_ACUM_INV_UNIT ,	");
			sb.append("	to_char(SIGN_DATE , 'yyyy-MM-dd') as SIGN_DATE ,	");
			sb.append("	INV_AMT_ORGD , CERT_NBR ,	");
			sb.append("	RISKCATE_ID ,	");
			sb.append("	to_char(NET_VALUE_DATE , 'yyyy-MM-dd') as NET_VALUE_DATE ,	");
			sb.append("	REF_RDMP_NET_VALUE , nvl(AUM_CASH_DIVID,0) as AUM_CASH_DIVID ,	");
			sb.append("	PARAM_NAME as INV_TYPE, EXCH_RATE AS  REF_BUYIN_EXCH_RATE, INV_AMT_TWD ,	");
			sb.append("	( CASE WHEN INV_CRCY_TYPE = 'TWD' THEN REF_TOTAL_REAL_VALUE	");
			sb.append("	 ELSE REF_TOTAL_REAL_VALUE/EXCH_RATE END) as VALUE,	");
			sb.append(" REF_ABS_RET_RATE_TWD/100 AS PROFIT_RATE,	");
			sb.append("	REF_ABS_RET_RATE_ORGD , REF_TOTAL_REAL_VALUE_TWD , VALU_CRCY_TYPE,INV_CRCY_TYPE, ");
			sb.append("	CASE WHEN D.AC2M2 = A.FUND_CODE THEN '動態鎖利_母' ");
			sb.append("		 WHEN (D.AC2M5 = A.FUND_CODE OR D.AC2M7 = A.FUND_CODE OR D.AC2M9 = A.FUND_CODE OR D.AC2M11 = A.FUND_CODE OR D.AC2M13 = A.FUND_CODE) THEN '動態鎖利_子' ");
			sb.append("		 ELSE '一般' END AS REMARK ");
			sb.append("	FROM TBCRM_AST_INV_FUND_DOM A	");
			sb.append("	LEFT JOIN TBPRD_FUND B ON A.FUND_CODE = B.PRD_ID	");
			sb.append("	LEFT JOIN TBSYSPARAMETER C ON C.PARAM_TYPE='CRM.CRM232_INV_WAY_NBR' AND	C.PARAM_CODE=A.INV_WAY_NBR	");
			sb.append("	LEFT JOIN TBCRM_AC2MARK D ON A.CERT_NBR = D.AC201M ");
			sb.append("	where CUST_ID = :custId	");
			sb.append("UNION	ALL	");
			sb.append("SELECT B.FUND_CNAME_S ,	");
			sb.append("	TOTAL_ACUM_INV_UNIT ,	");
			sb.append("	to_char(SIGN_DATE , 'yyyy-MM-dd') as SIGN_DATE ,	");
			sb.append("	INV_AMT_ORGD , CERT_NBR ,	");
			sb.append("	RISKCATE_ID ,	");
			sb.append("	to_char(NET_VALUE_DATE , 'yyyy-MM-dd') as NET_VALUE_DATE ,	");
			sb.append("	REF_RDMP_NET_VALUE , nvl(AUM_CASH_DIVID,0) as AUM_CASH_DIVID ,	");
			sb.append("	PARAM_NAME as INV_TYPE, 0 REF_BUYIN_EXCH_RATE,INV_AMT_ORGD INV_AMT_TWD ,	");
			sb.append("	REF_TOTAL_REAL_VALUE as VALUE,	");
			sb.append("	REF_ANN_RET_RATE/100 AS PROFIT_RATE,	");
			sb.append("	REF_ANN_RET_RATE REF_ABS_RET_RATE_ORGD , REF_TOTAL_REAL_VALUE REF_TOTAL_REAL_VALUE_TWD , 'TWD' VALU_CRCY_TYPE,'TWD' INV_CRCY_TYPE, ");
			sb.append("	CASE WHEN D.AC2M2 = A.FUND_CODE THEN '動態鎖利_母' ");
			sb.append("		 WHEN (D.AC2M5 = A.FUND_CODE OR D.AC2M7 = A.FUND_CODE OR D.AC2M9 = A.FUND_CODE OR D.AC2M11 = A.FUND_CODE OR D.AC2M13 = A.FUND_CODE) THEN '動態鎖利_子' ");
			sb.append("		 ELSE '一般' END AS REMARK ");
			sb.append("	FROM TBCRM_AST_INV_FUND_BOND A	");
			sb.append("	LEFT JOIN TBPRD_FUND B ON A.FUND_CODE = B.PRD_ID	");
			sb.append("	LEFT JOIN TBSYSPARAMETER C ON C.PARAM_TYPE='CRM.CRM232_INV_WAY_NBR' AND	C.PARAM_CODE=A.INV_WAY_NBR	");
			sb.append("	LEFT JOIN TBCRM_AC2MARK D ON A.CERT_NBR = D.AC201M ");
			sb.append("	where CUST_ID = :custId	");
			if(StringUtils.equals(fundSortType, "byCERT_NBR")) {
				sb.append("	ORDER BY CERT_NBR ASC ");
			} else if(StringUtils.equals(fundSortType, "byFUND_CNAME_S")) {
				sb.append("	ORDER BY FUND_CNAME_S , CERT_NBR ");
			} else {
				sb.append("	ORDER BY CERT_NBR ASC ");
			}

			List<Map<String,Object>> list = this.getQueryExecute(sb.toString(), custID);
			dataMap.put("list",list);

			for (Map map:list) {
				totFund = totFund.add(this.getBigDecimal( map.get("REF_TOTAL_REAL_VALUE_TWD")));
			}
			for (Map<String,Object> map : list) {
				map.put("AccAllocateRew",  new BigDecimal(0));
				String certNbr = map.get("CERT_NBR")==null?"":(String) map.get("CERT_NBR");
				for (CustAssetFundVO fundVo:AssetFunList) {
					//現有庫存調整後累積配息
					if (certNbr.trim().equals(fundVo.getEviNum()==null?"":fundVo.getEviNum().trim())) {
						System.out.println("Cert nbr matched, insert accAllocateRew");
						map.put("AccAllocateRew",  fundVo.getAccAllocateRew()==null?new BigDecimal(0):fundVo.getAccAllocateRew());
						map.put("AUM_CASH_DIVID", fundVo.getIncrease()==null?new BigDecimal(0):fundVo.getIncrease());
					}
				}
			}
			dataMap.put("tot", totFund);

		} catch (Exception e) {
			logger.error("Fund error:"+e.getLocalizedMessage(),e);
		}

		return dataMap;
	}

	private Map<String,Object> getInvSI(String custID) throws JBranchException {
		List<Map<String,Object>> result;
		BigDecimal invSi = new BigDecimal(0);
		Map<String,Object> dataMap = new HashMap<String,Object>();
		StringBuilder sb;
		try {
			//組合式商品
			sb = new StringBuilder();
			sb.append(" With BaseDivid As ( ");
			sb.append("		   SELECT DISTINCT CUST_ID, SI_CODE, TXN_DATE, CERT_NBR, DEPOSIT_NBR, VALU_CRCY_TYPE, TXN_DIVID_ORGD ");
			sb.append("		    FROM TBCRM_AST_INV_SI_DIVID ");
			sb.append("		    WHERE CUST_ID = :custId) ");
			sb.append(" SELECT A.PROD_NAME, A.PROD_ID, A.CRCY_TYPE,	");
			sb.append("        A.PRCH_AMT_ORGD,	A.PRCH_AMT_TWD ,");
			sb.append("		   TO_CHAR(A.VALUE_DATE,'yyyy-MM-dd') AS VALUE_DATE,");
			sb.append("        TO_CHAR(A.DUE_DATE , 'yyyy-MM-dd') AS DUE_DATE,	");
			sb.append("        TO_CHAR(B.SDDTE,'yyyy-MM-dd') AS SDDTE, NVL(B.SDAMT3,0) SDAMT3,	");
			sb.append("        NVL(C.DIVID_ORGD,0) DIVID_ORGD,	");
			sb.append("	       CASE WHEN D.RATE_GUARANTEEPAY='3' THEN '到期保本'	");
			sb.append("	            ELSE '非保本' END	");
			sb.append("	            AS RATE_GUARANTEEPAY,	");
			sb.append("			(CASE WHEN B.SDAMT3 IS NULL THEN 	0");
			sb.append("        ELSE (A.PRCH_AMT_ORGD * B.SDAMT3+ NVL(C.DIVID_ORGD,0))/A.PRCH_AMT_ORGD-1 END) PROFIT_RATE,");
			sb.append("			(B.SDAMT3*A.PRCH_AMT_TWD) AS NOW_TWD");
			sb.append("   FROM TBCRM_AST_INV_SI A 	");
			sb.append("   LEFT JOIN ( SELECT AA.SDPRD,AA.SDAMT3/100 AS SDAMT3,AA.SDDTE	");
			sb.append("               FROM TBPRD_SIPRICE AA			");
			sb.append("               LEFT JOIN	( SELECT MAX(SDDTE) SDDTE,SDPRD	");
			sb.append("                           FROM TBPRD_SIPRICE			");
			sb.append("                           GROUP BY SDPRD ) BB ON AA.SDPRD = BB.SDPRD AND AA.SDDTE = BB.SDDTE	");
			sb.append("               WHERE AA.SDDTE = BB.SDDTE) B ON A.PROD_ID = B.SDPRD	");
			sb.append("   LEFT JOIN ( SELECT CUST_ID, SI_CODE, DEPOSIT_NBR, SUM(TXN_DIVID_ORGD) AS DIVID_ORGD	");
			sb.append("               FROM BaseDivid	");
			sb.append("               GROUP BY CUST_ID, SI_CODE, DEPOSIT_NBR ) C ON A.CUST_ID = C.CUST_ID AND A.PROD_ID = C.SI_CODE AND	A.CD_NBR = C.DEPOSIT_NBR	");
			sb.append("	  LEFT JOIN TBPRD_SI D ON A.PROD_ID = D.PRD_ID");
			sb.append("   WHERE A.CUST_ID = :custId");
		 	List<Map<String,Object>> list = this.getQueryExecute(sb.toString(), custID);
		 	dataMap.put("si_list", list);

			//總折台市值
			invSi = invSi.add(this.sumBigDecimal(list, "NOW_TWD"));
		} catch (Exception e) {
			logger.error("SI error:"+e.getMessage());
		}

		try {
			//外匯雙享利非保本外幣組合式商品	(dci)
			sb = new StringBuilder();
			sb.append(" SELECT A.PROD_ID,A.PRCH_AMT_ORGD,A.VALUE_DATE,	");
			sb.append("        B.EXPIRY_DATE,B.EXPIRY_DATE_SPOT_RATE,	");
			sb.append("        B.STRIKE2,B.STRIKE,B.DUE_DATE,			");
			sb.append("        B.YIELD_RATE,B.VALU_CRCY_TYPE||'/'||B.MPNG_CRCY_TYPE AS CRCY_TYPE,	");
			sb.append("        PARAM_NAME||'('|| CASE WHEN B.GRNT_TYPE='1' THEN '70%' ");
			sb.append("                     WHEN B.GRNT_TYPE='2' THEN '80%' ");
			sb.append("				  	    WHEN B.GRNT_TYPE='3' THEN '90%' END ");
			sb.append("                               ||')' as GRNT_TYPE,D.PARAM_NAME AS CRCY_CHGE_STATUS,");
			sb.append("        C.DIVID_AMT	,	PRCH_AMT_TWD	");
			sb.append(" FROM TBCRM_AST_INV_DCI A 	");
			sb.append(" LEFT JOIN TBCRM_AST_INV_DCI_OTH B	");
			sb.append(" ON A.CUST_ID = B.CUST_ID AND A.PROD_ID = B.PROD_ID	");
			sb.append(" AND B.ACC_NBR = A.ACC_NBR	");
			sb.append(" LEFT JOIN (SELECT CUST_ID,DCI_CODE,CERT_NBR AS ACC_NBR,	");
			sb.append("                   SUM(TXN_DIVID_ORGD) AS DIVID_AMT	");
			sb.append("              FROM TBCRM_AST_INV_DCI_DIVID	");
			sb.append("  			WHERE CUST_ID = :custId");
			sb.append("             GROUP BY CUST_ID,DCI_CODE,CERT_NBR) C	");
			sb.append(" ON A.CUST_ID = C.CUST_ID AND A.PROD_ID = C.DCI_CODE	");
			sb.append(" AND A.ACC_NBR = C.ACC_NBR");
			sb.append(" LEFT JOIN TBSYSPARAMETER D ON B.CRCY_CHGE_STATUS = D.PARAM_CODE AND D.PARAM_TYPE = 'CRM.DCI_CRCY_CHGE_STATUS' ");
			sb.append("  where A.CUST_ID = :custId");
		 	List<Map<String,Object>> list = this.getQueryExecute(sb.toString(), custID);
		 	dataMap.put("dci_list", list);
			invSi = invSi.add(this.sumBigDecimal(list, "PRCH_AMT_TWD"));

		} catch (Exception e) {
			logger.error("DCI error:"+e.getMessage());
		}
		dataMap.put("tot", invSi);
		return dataMap;
	}

	private Map<String,Object> getInsJSB(String custID) throws JBranchException {
		Map<String,Object> dataMap = new HashMap<>();
		BigDecimal invInsJSB = new BigDecimal(0);
		try {
			String sb = "SELECT COALESCE(PRD.PRODUCTCATEGORY, '') AS INS_TYPE_STR, " +
						"       M.POLICY_SIMP_NAME, " +
						"       SUBSTR(M.INS_NAME, 1, LENGTH(M.INS_NAME) - 2) || '*' || SUBSTR(M.INS_NAME, LENGTH(M.INS_NAME)) AS INS_NAME, " +
						"       to_char(M.POLICY_ACTIVE_DATE, 'YYYY-MM-DD') AS POLICY_ACTIVE_DATE, " +
						"		M.CRCY_TYPE, " +
						"       M.POLICY_ASSURE_AMT || M.UNIT AS POLICY_ASSURE_AMT, " +
						"		M.ACUM_INS_AMT_ORGD, " +
						"		M.COM_NAME, " +
						"       M.POLICY_NBR AS POLICY_NBR_STR, " +
						"		IQ.BUY_RATE * M.ACUM_INS_AMT_ORGD AS ACUM_INS_AMT_TW " +
						"		FROM TBJSB_AST_INS_MAST M " +
						"		LEFT JOIN TBJSB_INS_PROD_MAIN PRD ON PRD.INSURANCECOSERIALNUM = M.COM_ID AND PRD.PRODUCTID = M.INS_TYPE " +
						"		LEFT JOIN TBPMS_IQ053 IQ ON M.CRCY_TYPE = IQ.CUR_COD AND IQ.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) " +
						"		WHERE M.CUST_ID = :custId " +
						"		AND M.TX_SRC IN (SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'IOT.JSB_TX_SRC') " +
						"		AND M.BILL_REMRK IS NULL " + //BILL_REMRK空白才為有效保單
						"		ORDER BY POLICY_NBR_STR ";
			List<Map<String, Object>> list = this.getQueryExecute(sb, custID);
			for (Map<String, Object> map: list) {
				BigDecimal netIns = getBigDecimal(map.get("ACUM_INS_AMT_TW"));
				invInsJSB = invInsJSB.add(netIns);
			}
			dataMap.put("list", list);
		} catch (Exception e) {
			logger.error("JSB_INS error:"+e.getMessage(),e);
		}
		dataMap.put("totJSB", invInsJSB);
		return dataMap;
	}

	private Map<String,Object> getIns(String custID) throws JBranchException {
		List<Map<String,Object>> result;
		BigDecimal invIns = new BigDecimal(0);
		Map<String,Object> dataMap = new HashMap<String,Object>();
		StringBuilder sb;
		try {
			sb = new StringBuilder();
			sb.append(" 	SELECT	");
			sb.append("		CASE WHEN IS_NUMBER(SUBSTR(D.POLICY_NBR,1,2))=0 AND TRIM(D.ID_DUP) IS NULL	");
			sb.append("     			THEN D.POLICY_NBR||'-'||D.POLICY_SEQ	");
			sb.append("					WHEN IS_NUMBER(SUBSTR(D.POLICY_NBR,1,2))=0 AND TRIM(D.ID_DUP) IS NOT NULL	");
			sb.append("     			THEN D.POLICY_NBR||'-'||D.POLICY_SEQ||'-'||D.ID_DUP	");
			sb.append("					ELSE D.POLICY_NBR END POLICY_NO,	");//保單號碼
		    sb.append("    B.PROD_NAME_S , to_char(b.INS_DUE_DATE,'yyyy-MM-dd') AS TERMINATE_DATE,	");//險種簡稱,保單解約日
		    sb.append("    SUBSTR(C.INS_NAME,1,LENGTH(C.INS_NAME)-2)||'*'||SUBSTR(C.INS_NAME,LENGTH(C.INS_NAME)) as INSURED_NAME ,  ");//被保險人
		    sb.append("    to_char(B.EFF_DATE , 'yyyy-MM-dd') as EFF_DATE , ");//保險始期、保單狀態
		    sb.append("    A.POLICY_CUR , B.EX_INS_AMOUNT||B.EX_UNIT as EX_INS_AMOUNT,	");//幣別、保額(單位)
		    sb.append("    nvl(ACCU_CUR_PREM,0) as ACCU_CUR_PREM , CASE WHEN C.INS_TYPE='1' THEN '儲蓄型'	");
		    sb.append("                         WHEN C.INS_TYPE='2' THEN '投資型'	");
		    sb.append("                         WHEN C.INS_TYPE='3' THEN '保障型' END AS INS_TYPE,	");
		    sb.append("   nvl(DRAW_AMT_CUR,0) as DRAW_AMT_CUR , nvl(ACC_VALUE,0) as ACC_VALUE,	");
		    sb.append("		(CASE WHEN SG.ITEM_REMRK = 'U' OR SG.ITEM_REMRK='S' OR SG.ITEM_REMRK = 'I' OR SG.ITEM_REMRK='F'	");
		    sb.append("					THEN SG.PAID_PREM	");
		    sb.append("					ELSE SG.PREM*SG.PAY_TIME END");
		    sb.append("		) NET_INS_FEE,");//累積淨繳保費
		    sb.append("		(CASE WHEN SG.ITEM_REMRK = 'U' OR SG.ITEM_REMRK='S' OR SG.ITEM_REMRK = 'I' OR SG.ITEM_REMRK='F'	");
		    sb.append("					THEN SG.PAID_PREM*E.BUY_RATE	");//折台累積淨繳保費
		    sb.append("					ELSE SG.PREM*SG.PAY_TIME*E.BUY_RATE END");
		    sb.append("		) NET_INS_FEE_TW,");
		    sb.append("   CASE WHEN A.POLICY_CUR = 'TWD'	");
		    sb.append("   THEN (ACCU_CUR_PREM)	");
		    sb.append("   ELSE ");
		    sb.append("   (ACCU_CUR_PREM * E.BUY_RATE) END LOCAL_CUR_PREM , ");
		    sb.append("   CASE WHEN A.POLICY_CUR = 'TWD'	");
		    sb.append("   THEN	DRAW_AMT_CUR");
		    sb.append("   ELSE (DRAW_AMT_CUR * E.BUY_RATE) END DRAW_AMT_LOCAL	");
		    sb.append("   FROM TBCRM_NPOLM A	");
		    sb.append("   LEFT JOIN TBCRM_NPOLD B ON A.POLICY_NO = B.POLICY_NO	");
			sb.append("                          AND A.POLICY_SEQ = B.POLICY_SEQ AND A.PRD_ID = B.INS_ITEM	");
			sb.append("   LEFT JOIN VWCRM_AST_INS_MAST C ON A.POLICY_NO = C.POLICY_NBR AND	");
			sb.append("                                     A.APPL_ID = C.CUST_ID AND	");
			sb.append("                                     A.POLICY_SEQ = C.POLICY_SEQ	 AND A.PRD_ID = C.INSPRD_ID	");
			sb.append("   LEFT JOIN TBCRM_AST_INS_MAST D ON A.POLICY_NO = D.POLICY_NBR AND		");
			sb.append("                                A.APPL_ID = D.CUST_ID AND	");
			sb.append("                                A.POLICY_SEQ = D.POLICY_SEQ	 AND A.PRD_ID = D.INS_TYPE	");
			sb.append("	   LEFT JOIN TBPMS_IQ053 E	");
			sb.append("		on a.POLICY_CUR = E.CUR_COD and		");
			sb.append("		   E.MTN_DATE  =(	");
			sb.append("	        SELECT MAX(MTN_DATE)	");
			sb.append("	        FROM TBPMS_IQ053	");
			sb.append("	        WHERE CUR_COD = A.POLICY_CUR	");
			sb.append("	       ) 										");
			sb.append("		LEFT JOIN (select * from TBCRM_FBRNY8A0 where SNAP_DATE	= (	SELECT MAX(SNAP_DATE) FROM TBCRM_FBRNY8A0))  SG	");
			sb.append("		ON A.POLICY_NO = SG.POLICY_NO AND	");
			sb.append("				A.POLICY_SEQ = SG.POLICY_SEQ AND 	");
			sb.append("				A.APPL_ID = SG.APPL_ID	");
		    sb.append("   WHERE a.APPL_ID = :custId AND D.CONTRACT_STATUS NOT IN ('06','11','12','13','14','15','17','20','21','22','23','24','25','26','27','28','29','30','31','32') AND D.CONSENT_YN='Y' ");
            List<Map<String, Object>> list = this.getQueryExecute(sb.toString(), custID);

			for (Map map:list) {
				BigDecimal	netIns = getBigDecimal(map.get("NET_INS_FEE_TW"));
				invIns = invIns.add(netIns);
			}
			dataMap.put("list", list);
		} catch (Exception e) {
			logger.error("INS error:"+e.getMessage(),e);
		}

		dataMap.put("tot", invIns);
		return dataMap;
	}

	/** 查詢客戶台外幣定存資料 **/
	private List<EB12020002OutputDetailsVO> getEndAgr(String custId) throws Exception {
		List results = new ArrayList();
		for(EB12020002OutputVO vo : eb12020002Service.search(custId, "2")) {
			if (isEmpty(vo.getDetails())) continue;

			for (EB12020002OutputDetailsVO data : vo.getDetails()) {
				/** 帳號為空不處理 **/
				if (isBlank(data.getACNO())) continue;

				data.setOPN_DPR_AMT(cbsservice.amountFormat(data.getOPN_DPR_AMT()));
				results.add(data);
			}
		}
		return results;
	}

	//Copy from CRM821
	private List<CustAssetFundVO> getCustAssetNFData(String cust_id, String isOBU) throws Exception {
		List<CustAssetFundVO> total = new ArrayList<CustAssetFundVO>();

        //init util
        ESBUtilInputVO esbUtilInputVO;
        if (StringUtils.equals("Y", isOBU)) {
        	esbUtilInputVO = getTxInstance(ESB_TYPE, CUST_ASSET_NF_OBU);
        } else {
        	esbUtilInputVO = getTxInstance(ESB_TYPE, CUST_ASSET_NF_DBU);
        }
        esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        txHead.setDefaultTxHead();
        txHead.setHFMTID("0001");
        esbUtilInputVO.setTxHeadVO(txHead);

        //body
		if (StringUtils.equals("Y", isOBU)) {
			AFBRN9InputVO txBodyVO = new AFBRN9InputVO();
			esbUtilInputVO.setAfbrn9InputVO(txBodyVO);
	        txBodyVO.setCustId(cust_id);
		} else {
			NFBRN9InputVO txBodyVO = new NFBRN9InputVO();
	        esbUtilInputVO.setNfbrn9InputVO(txBodyVO);
	        txBodyVO.setCustId(cust_id);
		}

        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

        for(ESBUtilOutputVO esbUtilOutputVO : vos){
            TxHeadVO headVO = esbUtilOutputVO.getTxHeadVO();

            String hfmtid = StringUtils.isBlank(headVO.getHFMTID()) ? "" : headVO.getHFMTID().trim();
            /**
             * RN9的下行電文會有5個Format的格式
             * Header.‧HFMTID =0001：單筆
             * Header.‧HFMTID =0002：定期定額
             * Header.‧HFMTID =0003：定期不定額
             * Header.‧HFMTID =0004：定存轉基金
             * Header.‧HFMTID =0005：基金套餐
             */
            if("0001".equals(hfmtid)) {
            	NFBRN9OutputVO outputVO;
            	if (StringUtils.equals("Y", isOBU)) {
            		outputVO = esbUtilOutputVO.getAfbrn9OutputVO();
            	} else {
            		outputVO = esbUtilOutputVO.getNfbrn9OutputVO();
            	}

                for(NFBRN9OutputVODetailsVO devo : outputVO.getDetails()) {
                	//2017-01-18 by Jacky 判斷單位數 > 0 才回傳前端
                	if(checkCustAssetFundVO(devo)){
                		CustAssetFundVO retVO = new CustAssetFundVO();
                        retVO.setAssetType(hfmtid); //信託種類
                        retVO.setSPRefId(devo.getSPRefId());
                        retVO.setAcctId16(devo.getAcctId16());
                        retVO.setOccur(devo.getOccur());
                        retVO.setAcctId02(devo.getAcctId02());
                        retVO.setEviNum(devo.getEviNum());
                        retVO.setFundNO(devo.getFundNO());
                        retVO.setFundName(devo.getFundName());
                        retVO.setCurFund(devo.getCurFund());
                        retVO.setCurCode(devo.getCurCode());
                        retVO.setCurAmt(decimalPoint(devo.getCurAmt(), 2));
                        retVO.setCurAmtNT(decimalPoint(devo.getCurAmtNT(), 2));
                        retVO.setCurBal(decimalPoint(devo.getCurBal(), 2));
                        retVO.setCurBalNT(decimalPoint(devo.getCurBalNT(), 2));
                        retVO.setProfitAndLoss(decimalPoint(devo.getProfitAndLoss(), 2));
                        retVO.setIncrease(decimalPoint(devo.getIncrease(), 2));
                        retVO.setSignDigit(devo.getSignDigit());
                        retVO.setReturn(decimalPoint(devo.getReturn(), 2));
                        retVO.setRewRateDigit(devo.getRewRateDigit());
                        retVO.setAccAllocateRewRate(decimalPoint(devo.getAccAllocateRewRate(), 2));
                        retVO.setCurUntNum(decimalPoint(devo.getCurUntNum(), 4));
                        retVO.setReferenceExchangeRate(decimalPoint(devo.getReferenceExchangeRate(), 4));
                        retVO.setNetValueDate(devo.getNetValueDate());
                        retVO.setNetValue(decimalPoint(devo.getNetValue(), 4));
                        retVO.setStoplossSign(devo.getStoplossSign());
                        retVO.setStoploss(decimalPoint(devo.getStoploss(), 2));
                        retVO.setSatisfiedSign(devo.getSatisfiedSign());
                        retVO.setSatisfied(decimalPoint(devo.getSatisfied(), 2));
                        retVO.setStrdate(devo.getStrdate());
                        retVO.setFundType(devo.getFundType());
                        retVO.setApproveFlag(devo.getApproveFlag());
                        retVO.setProjectCode(devo.getProjectCode());
                        retVO.setGroupCode(devo.getGroupCode());
                        retVO.setPayAcctId(devo.getPayAcctId());
                        retVO.setAccAllocateRew(decimalPoint(devo.getAccAllocateRew(), 2));

						if (retVO.getAcctId02() != null)
							retVO.setAcctId02(StringUtils.trim(retVO.getAcctId02()));
						if (retVO.getPayAcctId() != null)
							retVO.setPayAcctId(StringUtils.trim(retVO.getPayAcctId()));
						if (retVO.getPayAccountNo() != null)
							retVO.setPayAccountNo(StringUtils.trim(retVO.getPayAccountNo()));

						retVO.setDynamic(devo.getDynamic());
						retVO.setComboReturnDate(devo.getComboReturnDate());
						retVO.setComboReturnSign(devo.getComboReturnSign());
						retVO.setComboReturn(decimalPoint(devo.getComboReturn(), 2));
						retVO.setSatelliteBuyDate1(devo.getSatelliteBuyDate1());
						retVO.setSatelliteBuyDate2(devo.getSatelliteBuyDate2());
						retVO.setSatelliteBuyDate3(devo.getSatelliteBuyDate3());
						retVO.setSatelliteBuyDate4(devo.getSatelliteBuyDate4());
						retVO.setSatelliteBuyDate5(devo.getSatelliteBuyDate5());
						retVO.setSatelliteBuyDate6(devo.getSatelliteBuyDate6());
						retVO.setBenefitReturnRate1(decimalPoint(devo.getBenefitReturnRate1(), 2));
						retVO.setBenefitReturnRate2(decimalPoint(devo.getBenefitReturnRate2(), 2));
						retVO.setBenefitReturnRate3(decimalPoint(devo.getBenefitReturnRate3(), 2));

                        total.add(retVO);
                	}
                }
            }
            else if("0002".equals(hfmtid)) {   //定期定額
                NFBRN9OutputVO outputVO;
                if (StringUtils.equals("Y", isOBU)) {
            		outputVO = esbUtilOutputVO.getAfbrn9OutputVO();
            	} else {
            		outputVO = esbUtilOutputVO.getNfbrn9OutputVO();
            	}

                for(NFBRN9OutputVODetailsVO devo : outputVO.getDetails()) {
                	//2017-01-18 by Jacky 判斷單位數 > 0 才回傳前端
//                	if(checkCustAssetFundVO(devo)){
                	if(true){	//測試用 TODO
                		CustAssetFundVO retVO = new CustAssetFundVO();
                        retVO.setAssetType(hfmtid);
                        retVO.setSPRefId(devo.getSPRefId());
                        retVO.setOccur(devo.getOccur());
                        retVO.setAcctId02(devo.getAcctId02());
                        retVO.setEviNum(devo.getEviNum());
                        retVO.setFundNO(devo.getFundNO());
                        retVO.setFundName(devo.getFundName());
                        retVO.setCurFund(devo.getCurFund());
                        retVO.setCurCode(devo.getCurCode());
                        retVO.setCurAmt(decimalPoint(devo.getCurAmt(), 2));
                        retVO.setCurAmtNT(decimalPoint(devo.getCurAmtNT(), 2));
                        retVO.setCurBal(decimalPoint(devo.getCurBal(), 2));
                        retVO.setCurBalNT(decimalPoint(devo.getCurBalNT(), 2));
                        retVO.setProfitAndLoss(decimalPoint(devo.getProfitAndLoss(), 2));
                        retVO.setIncrease(decimalPoint(devo.getIncrease(), 2));
                        retVO.setSignDigit(devo.getSignDigit());
                        retVO.setReturn(decimalPoint(devo.getReturn(), 2));
                        retVO.setRewRateDigit(devo.getRewRateDigit());
                        retVO.setAccAllocateRewRate(decimalPoint(devo.getAccAllocateRewRate(), 2));
                        retVO.setCurUntNum(decimalPoint(devo.getCurUntNum(), 4));
                        retVO.setReferenceExchangeRate(decimalPoint(devo.getReferenceExchangeRate(), 4));
                        retVO.setNetValueDate(devo.getNetValueDate());
                        retVO.setNetValue(decimalPoint(devo.getNetValue(), 4));
                        retVO.setTransferAmt(decimalPoint(devo.getTransferAmt(), 0));
                        retVO.setTransferDate01(devo.getTransferDate01());
                        retVO.setTransferDate02(devo.getTransferDate02());
                        retVO.setTransferDate03(devo.getTransferDate03());
                        retVO.setTransferDate04(devo.getTransferDate04());
                        retVO.setTransferDate05(devo.getTransferDate05());
                        retVO.setTransferDate06(devo.getTransferDate06());
                        retVO.setTransferCount(devo.getTransferCount());
                        retVO.setPayCount(devo.getPayCount());
                        retVO.setStatus(devo.getStatus());
                        retVO.setStoplossSign(devo.getStoplossSign());
                        retVO.setStoploss(decimalPoint(devo.getStoploss(), 2));
                        retVO.setSatisfiedSign(devo.getSatisfiedSign());
                        retVO.setSatisfied(decimalPoint(devo.getSatisfied(), 2));
                        retVO.setStrdate(devo.getStrdate());
                        retVO.setFundType(devo.getFundType());
                        retVO.setApproveFlag(devo.getApproveFlag());
                        retVO.setProjectCode(devo.getProjectCode());
                        retVO.setGroupCode(devo.getGroupCode());
                        retVO.setPayAcctId(devo.getPayAcctId());
                        retVO.setPayAccountNo(devo.getPayAccountNo());
                        retVO.setTxType(devo.getTxType());
                        retVO.setFrgnPurchaseFlag(devo.getFrgnPurchaseFlag());
                        retVO.setSame(devo.getSame());
                        retVO.setLongDiscount(decimalPoint(devo.getLongDiscount(), 2));
                        retVO.setAccAllocateRew(decimalPoint(devo.getAccAllocateRew(), 2));
                        if (retVO.getAcctId02() != null)
							retVO.setAcctId02(StringUtils.trim(retVO.getAcctId02()));
						if (retVO.getPayAcctId() != null)
							retVO.setPayAcctId(StringUtils.trim(retVO.getPayAcctId()));
						if (retVO.getPayAccountNo() != null)
							retVO.setPayAccountNo(StringUtils.trim(retVO.getPayAccountNo()));
                        total.add(retVO);
                	}
                }
            }
            else if("0003".equals(hfmtid)) {//定期不定額
                NFBRN9OutputVO outputVO;
                if (StringUtils.equals("Y", isOBU)) {
            		outputVO = esbUtilOutputVO.getAfbrn9OutputVO();
            	} else {
            		outputVO = esbUtilOutputVO.getNfbrn9OutputVO();
            	}

                for(NFBRN9OutputVODetailsVO devo : outputVO.getDetails()) {
                	//2017-01-18 by Jacky 判斷單位數 > 0 才回傳前端
//                	if(checkCustAssetFundVO(devo)){
                	if(true){	//測試用 TODO
                		CustAssetFundVO retVO = new CustAssetFundVO();
                        retVO.setAssetType(hfmtid);
                        retVO.setSPRefId(devo.getSPRefId());
                        retVO.setOccur(devo.getOccur());
                        retVO.setAcctId02(devo.getAcctId02());
                        retVO.setEviNum(devo.getEviNum());
                        retVO.setFundNO(devo.getFundNO());
                        retVO.setFundName(devo.getFundName());
                        retVO.setCurFund(devo.getCurFund());
                        retVO.setCurCode(devo.getCurCode());
                        retVO.setCurAmt(decimalPoint(devo.getCurAmt(), 2));
                        retVO.setCurAmtNT(decimalPoint(devo.getCurAmtNT(), 2));
                        retVO.setCurBal(decimalPoint(devo.getCurBal(), 2));
                        retVO.setCurBalNT(decimalPoint(devo.getCurBalNT(), 2));
                        retVO.setProfitAndLoss(decimalPoint(devo.getProfitAndLoss(), 2));
                        retVO.setIncrease(decimalPoint(devo.getIncrease(), 2));
                        retVO.setSignDigit(devo.getSignDigit());
                        retVO.setReturn(decimalPoint(devo.getReturn(), 2));
                        retVO.setRewRateDigit(devo.getRewRateDigit());
                        retVO.setAccAllocateRewRate(decimalPoint(devo.getAccAllocateRewRate(), 2));
                        retVO.setCurUntNum(decimalPoint(devo.getCurUntNum(), 4));
                        retVO.setReferenceExchangeRate(decimalPoint(devo.getReferenceExchangeRate(), 4));
                        retVO.setNetValueDate(devo.getNetValueDate());
                        retVO.setNetValue(decimalPoint(devo.getNetValue(), 4));
                        retVO.setTransferAmt_H(decimalPoint(devo.getTransferAmt_H(), 0));
                        retVO.setTransferAmt_M(decimalPoint(devo.getTransferAmt_M(), 0));
                        retVO.setTransferAmt_L(decimalPoint(devo.getTransferAmt_L(), 0));
                        retVO.setTransferDate01(devo.getTransferDate01());
                        retVO.setTransferDate02(devo.getTransferDate02());
                        retVO.setTransferDate03(devo.getTransferDate03());
                        retVO.setTransferDate04(devo.getTransferDate04());
                        retVO.setTransferDate05(devo.getTransferDate05());
                        retVO.setTransferDate06(devo.getTransferDate06());
                        retVO.setTransferCount(devo.getTransferCount());
                        retVO.setPayCount(devo.getPayCount());
                        retVO.setStatus(devo.getStatus());
                        retVO.setStoplossSign(devo.getStoplossSign());
                        retVO.setStoploss(decimalPoint(devo.getStoploss(), 2));
                        retVO.setSatisfiedSign(devo.getSatisfiedSign());
                        retVO.setSatisfied(decimalPoint(devo.getSatisfied(), 2));
                        retVO.setStrdate(devo.getStrdate());
                        retVO.setFundType(devo.getFundType());
                        retVO.setApproveFlag(devo.getApproveFlag());
                        retVO.setProjectCode(devo.getProjectCode());
                        retVO.setGroupCode(devo.getGroupCode());
                        retVO.setPayAcctId(devo.getPayAcctId());
                        retVO.setPayAccountNo(devo.getPayAccountNo());
                        retVO.setTxType(devo.getTxType());
                        retVO.setFrgnPurchaseFlag(devo.getFrgnPurchaseFlag());
                        retVO.setSame(devo.getSame());
                        retVO.setAccAllocateRew(decimalPoint(devo.getAccAllocateRew(), 2));
                        if (retVO.getAcctId02() != null)
							retVO.setAcctId02(StringUtils.trim(retVO.getAcctId02()));
						if (retVO.getPayAcctId() != null)
							retVO.setPayAcctId(StringUtils.trim(retVO.getPayAcctId()));
						if (retVO.getPayAccountNo() != null)
							retVO.setPayAccountNo(StringUtils.trim(retVO.getPayAccountNo()));
                        total.add(retVO);
                	}
                }
            }
            else if("0004".equals(hfmtid)) {
                NFBRN9OutputVO outputVO;
                if (StringUtils.equals("Y", isOBU)) {
            		outputVO = esbUtilOutputVO.getAfbrn9OutputVO();
            	} else {
            		outputVO = esbUtilOutputVO.getNfbrn9OutputVO();
            	}

                for(NFBRN9OutputVODetailsVO devo : outputVO.getDetails()) {
                	//2017-01-18 by Jacky 判斷單位數 > 0 才回傳前端
//                	if(checkCustAssetFundVO(devo)){
                	if(true){	//測試用 TODO
                		CustAssetFundVO retVO = new CustAssetFundVO();
                        retVO.setAssetType(hfmtid);
                        retVO.setSPRefId(devo.getSPRefId());
                        retVO.setOccur(devo.getOccur());
                        retVO.setAcctId02(devo.getAcctId02());
                        retVO.setEviNum(devo.getEviNum());
                        retVO.setFundNO(devo.getFundNO());
                        retVO.setFundName(devo.getFundName());
                        retVO.setCurFund(devo.getCurFund());
                        retVO.setCurCode(devo.getCurCode());
                        retVO.setCurAmt(decimalPoint(devo.getCurAmt(), 2));
                        retVO.setCurAmtNT(decimalPoint(devo.getCurAmtNT(), 2));
                        retVO.setCurBal(decimalPoint(devo.getCurBal(), 2));
                        retVO.setCurBalNT(decimalPoint(devo.getCurBalNT(), 2));
                        retVO.setProfitAndLoss(decimalPoint(devo.getProfitAndLoss(), 2));
                        retVO.setIncrease(decimalPoint(devo.getIncrease(), 2));
                        retVO.setSignDigit(devo.getSignDigit());
                        retVO.setReturn(decimalPoint(devo.getReturn(), 2));
                        retVO.setRewRateDigit(devo.getRewRateDigit());
                        retVO.setAccAllocateRewRate(decimalPoint(devo.getAccAllocateRewRate(), 2));
                        retVO.setCurUntNum(decimalPoint(devo.getCurUntNum(), 4));
                        retVO.setReferenceExchangeRate(decimalPoint(devo.getReferenceExchangeRate(), 4));
                        retVO.setNetValueDate(devo.getNetValueDate());
                        retVO.setNetValue(decimalPoint(devo.getNetValue(), 4));
                        retVO.setTransferAmt(decimalPoint(devo.getTransferAmt(), 0));
                        retVO.setTransferDate01(devo.getTransferDate01());
                        retVO.setTransferDate02(devo.getTransferDate02());
                        retVO.setTransferDate03(devo.getTransferDate03());
                        retVO.setTransferDate04(devo.getTransferDate04());
                        retVO.setTransferDate05(devo.getTransferDate05());
                        retVO.setTransferDate06(devo.getTransferDate06());
                        retVO.setTransferCount(devo.getTransferCount());
                        retVO.setPayCount(devo.getPayCount());
                        retVO.setStatus(devo.getStatus());
                        retVO.setStoplossSign(devo.getStoplossSign());
                        retVO.setStoploss(decimalPoint(devo.getStoploss(), 2));
                        retVO.setSatisfiedSign(devo.getSatisfiedSign());
                        retVO.setSatisfied(decimalPoint(devo.getSatisfied(), 2));
                        retVO.setStrdate(devo.getStrdate());
                        retVO.setFundType(devo.getFundType());
                        retVO.setApproveFlag(devo.getApproveFlag());
                        retVO.setGroupCode(devo.getGroupCode());
                        retVO.setPayAccountNo(devo.getPayAccountNo());
                        retVO.setAcctId(devo.getAcctId());
                        retVO.setTimeDepositPrjCd(devo.getTimeDepositPrjCd());
                        retVO.setTotal_Cnt(devo.getTotal_Cnt());
                        retVO.setPay_Cnt(devo.getPay_Cnt());
                        retVO.setEnd_Flg(devo.getEnd_Flg());
                        retVO.setSame(devo.getSame());
                        retVO.setAccAllocateRew(decimalPoint(devo.getAccAllocateRew(), 2));
                        if (retVO.getAcctId02() != null)
							retVO.setAcctId02(StringUtils.trim(retVO.getAcctId02()));
						if (retVO.getPayAcctId() != null)
							retVO.setPayAcctId(StringUtils.trim(retVO.getPayAcctId()));
						if (retVO.getPayAccountNo() != null)
							retVO.setPayAccountNo(StringUtils.trim(retVO.getPayAccountNo()));
                        total.add(retVO);
                	}
                }
            }
            else if("0005".equals(hfmtid)) {
                NFBRN9OutputVO outputVO;
                if (StringUtils.equals("Y", isOBU)) {
            		outputVO = esbUtilOutputVO.getAfbrn9OutputVO();
            	} else {
            		outputVO = esbUtilOutputVO.getNfbrn9OutputVO();
            	}

                for (NFBRN9OutputVODetailsVO devo : outputVO.getDetails()) {
                	//2017-01-18 by Jacky 判斷單位數 > 0 才回傳前端
                	if(true){	//測試用 TODO
//                	if(checkCustAssetFundVO(devo)){
                		CustAssetFundVO retVO = new CustAssetFundVO();
                        retVO.setAssetType(hfmtid);
                        retVO.setSPRefId(devo.getSPRefId());
                        retVO.setAcctId16(devo.getAcctId16());
                        retVO.setOccur(devo.getOccur());
                        retVO.setAcctId02(devo.getAcctId02());
                        retVO.setEviNum(devo.getEviNum());
                        retVO.setFundNO(devo.getFundNO());
                        retVO.setFundName(devo.getFundName());
                        retVO.setCurFund(devo.getCurFund());
                        retVO.setCurCode(devo.getCurCode());
                        retVO.setCurAmt(decimalPoint(devo.getCurAmt(), 2));
                        retVO.setCurAmtNT(decimalPoint(devo.getCurAmtNT(), 2));
                        retVO.setCurBal(decimalPoint(devo.getCurBal(), 2));
                        retVO.setCurBalNT(decimalPoint(devo.getCurBalNT(), 2));
                        retVO.setProfitAndLoss(decimalPoint(devo.getProfitAndLoss(), 2));
                        retVO.setIncrease(decimalPoint(devo.getIncrease(), 2));
                        retVO.setSignDigit(devo.getSignDigit());
                        retVO.setReturn(decimalPoint(devo.getReturn(), 2));
                        retVO.setRewRateDigit(devo.getRewRateDigit());
                        retVO.setAccAllocateRewRate(decimalPoint(devo.getAccAllocateRewRate(), 2));
                        retVO.setCurUntNum(decimalPoint(devo.getCurUntNum(), 4));
                        retVO.setReferenceExchangeRate(decimalPoint(devo.getReferenceExchangeRate(), 4));
                        retVO.setNetValueDate(devo.getNetValueDate());
                        retVO.setNetValue(decimalPoint(devo.getNetValue(), 4));
                        retVO.setTransferAmt(decimalPoint(devo.getTransferAmt(), 0));
                        retVO.setTransferDate01(devo.getTransferDate01());
                        retVO.setTransferDate02(devo.getTransferDate02());
                        retVO.setTransferDate03(devo.getTransferDate03());
                        retVO.setTransferDate04(devo.getTransferDate04());
                        retVO.setTransferDate05(devo.getTransferDate05());
                        retVO.setTransferDate06(devo.getTransferDate06());
                        retVO.setTransferCount(devo.getTransferCount());
                        retVO.setPayCount(devo.getPayCount());
                        retVO.setStatus(devo.getStatus());
                        retVO.setStoplossSign(devo.getStoplossSign());
                        retVO.setStoploss(decimalPoint(devo.getStoploss(), 2));
                        retVO.setSatisfiedSign(devo.getSatisfiedSign());
                        retVO.setSatisfied(decimalPoint(devo.getSatisfied(), 2));
                        retVO.setStrdate(devo.getStrdate());
                        retVO.setFundType(devo.getFundType());
                        retVO.setApproveFlag(devo.getApproveFlag());
                        retVO.setProjectCode(devo.getProjectCode());
                        retVO.setGroupCode(devo.getGroupCode());
                        retVO.setPayAcctId(devo.getPayAcctId());
                        retVO.setPayAccountNo(devo.getPayAccountNo());
                        retVO.setTxType(devo.getTxType());
                        retVO.setSame(devo.getSame());
                        retVO.setFundPackageNo(devo.getFundPackageNo());
                        retVO.setFundPackage(devo.getFundPackage());
                        retVO.setEnd_Flg(devo.getEnd_Flg());
                        retVO.setAccAllocateRew(decimalPoint(devo.getAccAllocateRew(), 2));
                        if (retVO.getAcctId02() != null)
							retVO.setAcctId02(StringUtils.trim(retVO.getAcctId02()));
						if (retVO.getPayAcctId() != null)
							retVO.setPayAcctId(StringUtils.trim(retVO.getPayAcctId()));
						if (retVO.getPayAccountNo() != null)
							retVO.setPayAccountNo(StringUtils.trim(retVO.getPayAccountNo()));
                        total.add(retVO);
                	}
                }
            }
        }

		  Collections.sort(total, new Comparator<CustAssetFundVO>() {
		      public int compare( CustAssetFundVO o1,  CustAssetFundVO o2) {
		    	  if(StringUtils.isEmpty(o1.getEviNum())){
		    		  return -1;
		    	  }
		    	  if(StringUtils.isEmpty(o2.getEviNum())){
		    		  return 1;
		    	  }
			       return (o1.getEviNum()).compareTo(o2.getEviNum());
		      }
	     });

		return total;
	}
    /*
     * 2021.09.14 改NF/AF電文發送邏輯 by SamTu
     */
	public List<CustAssetFundVO> getNFVIPA(String cust_id , String isOBU) throws Exception {
		List<CustAssetFundVO> D1List = sendNFVIPA("D1", cust_id, isOBU);
		List<CustAssetFundVO> D2List = sendNFVIPA("D2", cust_id, isOBU);
//		List<CustAssetFundVO> AssetFunList = getCustAssetNFData(cust_id, isOBU);

		List<CustAssetFundVO> AssetFunList = new ArrayList();
		crm821.getCustAssetNFData(cust_id, AssetFunList, "NF");
		if(StringUtils.equals("Y", isOBU)){
			crm821.getCustAssetNFData(cust_id, AssetFunList, "AF");
		}

		if(D2List.size() > 0){
			for(CustAssetFundVO vo : D2List){
				D1List.add(vo);
			}
		}
		if(AssetFunList.size() > 0){
			for(CustAssetFundVO vo : AssetFunList){
				D1List.add(vo);
			}
		}
		return D1List;
	}
	private List<CustAssetFundVO> sendNFVIPA(String fun, String cust_id, String isOBU) throws Exception {
//		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<CustAssetFundVO> total = new ArrayList<CustAssetFundVO>();

		try{
			//init util
			ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, ETF_ASSETS);
			esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

			//head
			TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
			esbUtilInputVO.setTxHeadVO(txHead);
			txHead.setDefaultTxHead();

			//body
			NFVIPAInputVO txBodyVO = new NFVIPAInputVO();
			esbUtilInputVO.setNfvipaInputVO(txBodyVO);
			txBodyVO.setFUNCTION(fun);
			txBodyVO.setCUSID(cust_id);
			txBodyVO.setUNIT("Y".equals(isOBU) ? "O" : "D");

			//發送電文
			List<ESBUtilOutputVO> esbUtilOutputVO = send(esbUtilInputVO);

			for(ESBUtilOutputVO vo : esbUtilOutputVO){
				NFVIPAOutputVO nfvipaOutputVO = vo.getNfvipaOutputVO();
				List<NFVIPAOutputVODetails> details = nfvipaOutputVO.getDetails();
				details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NFVIPAOutputVODetails>() : details;

				for(NFVIPAOutputVODetails detail : details){
					CustAssetFundVO retVO = new CustAssetFundVO();

					retVO.setFundName(detail.getARR18());
					retVO.setCurCode("TWD");
					retVO.setCurAmt(decimalPoint(detail.getARR10(), 0));
					retVO.setCurBal(decimalPoint(detail.getARR11(), 2));
					retVO.setSignDigit(detail.getARR12());
					retVO.setProfitAndLoss(decimalPoint(detail.getARR17(), 4));
					retVO.setReturn(decimalPoint(detail.getARR13(), 4));
					retVO.setFundType(detail.getARR01());
					retVO.setFundNO(detail.getARR04());
					retVO.setAssetType("0006");
					retVO.setNetValueDate(detail.getARR09());
					retVO.setStrdate(detail.getARR06());
					retVO.setCurUntNum(decimalPoint(detail.getARR07(), 4));

					total.add(retVO);
				}
			}
		}catch(Exception e){
			logger.debug("發送電文失敗：客戶ID = "+ cust_id);
			logger.debug("ESB error:NFVIPA="+StringUtil.getStackTraceAsString(e));
		}

		return total;
	}
    /**
     * 檢查可用庫存(憑證,判斷單位數 > 0 )
     * @param devo
     * @return
     */
	private boolean checkCustAssetFundVO(NFBRN9OutputVODetailsVO devo){
		if (decimalPoint(devo.getCurUntNum(), 4).compareTo(BigDecimal.ZERO) == 1) {
			return true;
		}
    	return false;
    }

	/** SEQ產生 **/
	public BigDecimal getPrintCount(BigDecimal seq) throws JBranchException{
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT (max(PRINT_COUNT)+1) SEQ FROM TBCRM_CUST_ASSET_PRINT_HIS ");
		sql.append("WHERE SEQ = :seq");
		queryCondition.setObject("seq", seq);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNoB =(BigDecimal) (SEQLIST.get(0).get("SEQ")==null?new BigDecimal(0):SEQLIST.get(0).get("SEQ"));
		return seqNoB;
	}

	private BigDecimal sumBigDecimal(List<Map<String,Object>> list  , String fieldName) {
		BigDecimal result = new BigDecimal(0);
		for (Map field:list) {
			result = result.add(getBigDecimal(field.get(fieldName)));
		}

		return result;
	}

	/**
	 * 倒數第4個字開始遮照2個字
	 * @param input
	 * @return
	 */

	private String maskString(String input) {
		int inputLen = input.length();
		String result = input.substring(0,inputLen-4) + "**" + input.substring(inputLen-2 , inputLen);
		return result;
	}
	private List<Map<String,Object>> getQueryExecute(String sql , String custId) throws DAOException, JBranchException {
		List result = new ArrayList();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		if(sql.isEmpty())
			return result;
		if (custId != null) {
			queryCondition.setObject("custId", custId);
		}
		queryCondition.setQueryString(sb.toString());
		result = dam.exeQuery(queryCondition);
		return result;
	}
	public String sumAllNumber(String... input) {
		String result;
		Double x = 0d;
		for (String number:input) {
			x += Double.valueOf(number);
		}
		result = new DecimalFormat("#,###.###").format(x);
//		result = String.format("%.3f", dec);
		return result;
	}
	public void getFile (Object body, IPrimitiveMap header) throws JBranchException {
		CRM8502InputVO inputVO = (CRM8502InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM TBCRM_CUST_ASSET_PRINT_FILE WHERE SEQ = :seq");
		queryCondition.setObject("seq", inputVO.getSeq());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		String errorMag = null;

		try {
			if (list.size() > 0 && list.get(0).get("FILE_DOC") != null) {
				String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				String uuid = UUID.randomUUID().toString();
				String fileName = String.format("%s.pdf", uuid);
				Blob blob = (Blob) list.get(0).get("FILE_DOC");
				int blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);

				File targetFile = new File(filePath, fileName);
				FileOutputStream fos = new FileOutputStream(targetFile);
				fos.write(blobAsBytes);
				fos.close();
				notifyClientToDownloadFile("temp//" + uuid+".pdf", fileName);
			} else {
				errorMag ="查無此資料請洽系統管理員";
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

		if (StringUtils.isNotBlank(errorMag)) {
			throw new APException(errorMag);
		}
		this.sendRtnObject(null);

	}

	/*  === 產出EXCEL==== */
	public void exPort(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		CRM8502OutputVO outputVO = (CRM8502OutputVO) body;
		String fileName = "客戶資產現況表申請記錄";
		List<Map<String, Object>> list = outputVO.getCustAssetDocList2();
		List<Map<String, Object>> list2 = outputVO.getCustAssetPrintHisList();
		Map<String, String> file = new HashMap<String, String>();
        Map<String, String> order = new LinkedHashMap<String, String>();
        Map<String, String> order2 = new LinkedHashMap<String, String>();
        String url = null;
//        order.put("APPLY_DATE", "申請日期");
//        order.put("PRINT_BRA_NBR", "分行別");
//        order.put("CUST_ID", "客戶ID");
//        order.put("CUST_NAME", "客戶姓名");
//        order.put("EMP_NAME", "申請專員");
//        order.put("AO_CODE", "專員AOCODE");
//        order.put("APPLY_NO", "記錄編號");
//        order.put("APPLY_TYPE", "申請狀態");

//        order2.put("PRINT_DATE", "列印日期");
//        order2.put("APPLY_DATE", "申請日期");
//        order2.put("PRINT_BRA_NBR", "分行別");
//        order2.put("CUST_ID", "客戶ID");
//        order2.put("CUST_NAME", "客戶姓名");
//        order2.put("EMP_NAME", "申請專員");
//        order2.put("AO_CODE", "專員AOCODE");
//        order2.put("APPLY_NO", "記錄編號");
//        order2.put("AUTH_BRA_NBR", "列印人員所屬行");
//        order2.put("AUTH_ID", "列印人員員編");
//        order2.put("AUTH_NAME", "列印人員姓名");
//        order2.put("PRINT_STATUS", "列印狀態");
//
//
//        file = this.exportxlsx(fileName, list, order, list2, order2);
//		this.sendRtnObject("downloadFile", file);
        
        url = exportPDF(list2);
        notifyClientToDownloadFile(url, "客戶資產現況表申請記錄.pdf");

	}
	
	private String exportPDF(List<Map<String, Object>> list2) throws JBranchException {
		String url = null;
		String txnCode = "CRM8502";
		String reportID = "R3";
		ReportIF report = null;
		
		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator(); 
		
		for(Map map : list2) {
			map.put("APPLY_NO",dealAPPLY_NO(map.get("APPLY_NO").toString())); 
		}
		
		data.addRecordList("ResultList", list2);
		report = gen.generateReport(txnCode, reportID, data);
		url = report.getLocation();
		
		return url;
		
	}

	//#1957 對記錄編號的資料加空白以便birt換行，目前控制每行11位
	private String dealAPPLY_NO(String string) {
		String newStr = "";
		int count = string.length() / 11;
		for (int i = 0; i < count+1; i++) {
			if(i == count) {
				newStr = newStr + string.substring((i * 11));
			} else {
				newStr = newStr + string.substring(i * 11,(i+1) * 11) + " ";
			}
			
		}
		return newStr;
	}

	private List<Map<String,Object>> packAgentFund(List<CustAssetFundVO> fundVos) {
		List<Map<String , Object>> result = new ArrayList<Map<String , Object>>();
		for (CustAssetFundVO fundVo:fundVos) {
//			if ((fundVo.getFundNO()==null || fundVo.getFundNO().equals("")) && fundVo.getAssetType().equals("0006")) {
			if (fundVo.getAssetType().equals("0006")) {
				Map<String , Object> map = new HashMap<String , Object> ();

				map.put("FUND_CNAME_S", fundVo.getFundName());	//中文名稱FUND_CNAME_S

				map.put("CERT_NBR", fundVo.getEviNum());	//憑證號碼CERT_NBR
				//投資方式INV_TYPE 0001：單筆 0002：定期定額 0003：定期不定額 0004：定存轉基金 0005：基金套餐
//				String assetType = fundVo.getAssetType();
//				switch (assetType) {
//				case "0001":
//					assetType = "單筆";
//					break;
//				case "0002":
//					assetType = "定期定額";
//					break;
//				case "0003":
//					assetType = "定期不定額";
//					break;
//				case "0004":
//					assetType = "定存轉基金";
//					break;
//				case "0005":
//					assetType = "基金套餐";
//					break;
//				}
				map.put("INV_WAY_NBR", "國內代理基金");
				String signDate = fundVo.getStrdate().substring(0, 3) + fundVo.getStrdate().substring(3, 5) + fundVo.getStrdate().substring(5, 7);
				map.put("SIGN_DATE", signDate);	//投資起始日SIGN_DATE
				map.put("INV_AMT_ORGD", fundVo.getCurAmt());	//原始投資本金INV_AMT_ORGD

				map.put("INV_AMT_TWD", fundVo.getCurAmtNT());	//原始投資折台INV_AMT_TWD

				map.put("TOTAL_ACUM_INV_UNIT", fundVo.getCurUntNum());	//累積單位數TOTAL_ACUM_INV_UNIT

				map.put("VALU_CRY_TYPE", fundVo.getCurCode());	//幣別VALU_CRCY_TYPE

				map.put("REF_RDMP_NET_VALUE", fundVo.getNetValue());	//單位淨值REF_RDMP_NET_VALUE
				String netValueDate = fundVo.getNetValueDate();
				map.put("NET_VALUE_DATE", netValueDate.substring(0, 4)+"/"+netValueDate.substring(4,6)+"/"+netValueDate.substring(6,8));	//淨值參考日NET_VALUE_DATE

				map.put("REF_TOTAL_REAL_VALUE", fundVo.getCurBal());	//折台市值REF_TOTAL_REAL_VALUE

				map.put("", fundVo.getCurBal());	//市值REF_TOTAL_REAL_VALUE

				map.put("REF_ABS_RET_RATE_ORGD", fundVo.getReturn().divide(new BigDecimal(100)));	//報酬率REF_ABS_RET_RATE_ORGD
				result.add(map);
			}
		}
		return result;
	}

	//匯出EXCEL BY 排序 AND 欄位名稱設定
	public Map<String, String> exportxlsx (String name, List<Map<String, Object>> list, Map<String, String> order, List<Map<String, Object>> list2, Map<String, String> order2) throws JBranchException, IOException{

		XSSFWorkbook workbook = new XSSFWorkbook();
		//頁簽1
		XSSFSheet sheet = workbook.createSheet("客戶資產現況表申請記錄");
		//頁簽2
//		XSSFSheet sheet2 = workbook.createSheet("列印記錄");
		CellStyle cellStyleFont = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontName("Serif");
		cellStyleFont.setFont(font);

		CellStyle cellStyleDate = workbook.createCellStyle();
		CreationHelper createHelper = workbook.getCreationHelper();
		cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd h:mm:ss"));

//		//頁簽1
//		Row row_head = sheet.createRow(0);
//		Cell cell_head = row_head.createCell(0);
//		cell_head.setCellStyle(cellStyleFont);
//		cell_head.setCellValue("資況表約定書簽署記錄");
//		row_head = sheet.createRow(1);
//		int cell_head_num = 0;
//
		/*T:已取消
			P:已申請
			A:已申請*/
		XmlInfo	xmlInfo	=	new	XmlInfo();
		Map<String, String> applyPringStatus = xmlInfo.doGetVariable("CRM.APPLY_PRINT_STATUS",FormatHelper.FORMAT_3);
//
//		//頁簽1
//		for (Entry<String, String> strs : order.entrySet()) {
//			String str = strs.getValue();
//			cell_head = row_head.createCell(cell_head_num++);
//			cell_head.setCellStyle(cellStyleFont);
//			cell_head.setCellValue(str);
//		}
//		//頁簽1
//		List<String> orderkey = new ArrayList<String>();
//		for ( String key : order.keySet() ) {
//			orderkey.add(key);
//		}
//		//頁簽1
//		int rownum = sheet.getLastRowNum();
//		//頁簽1
//		for (Map<String, Object> objs : list) {
//			Row row = sheet.createRow(++rownum);
//
//			for (Entry<String, Object> en : objs.entrySet()) {
//				Object obj  = en.getValue();
//
//				//申請狀態
//				if (en.getKey().equals("APPLY_TYPE"))
//					obj = applyPringStatus.get(en.getValue());
//
//				int idx = orderkey.indexOf(en.getKey());
//
//				if (idx >= 0) {
//					Cell cell = row.createCell(idx);
//					cell.setCellStyle(cellStyleFont);
//
//					//sql String
//					if (obj instanceof String) {
//
//						cell.setCellValue((String)obj);
//					//sql Date
//					} else if(obj instanceof Date) {
//						cell.setCellStyle(cellStyleDate);
//						cell.setCellValue((Date)obj);
//					}
//					//sql Number
//					else if(obj instanceof Number) {
//						cell.setCellValue((getBigDecimal(obj)).doubleValue());
//					}
//					//sql Double
//					else if(obj instanceof Double) {
//						cell.setCellValue((Double)obj);
//					}
//					//sql Boolean
//					else if(obj instanceof Boolean) {
//						cell.setCellValue((Boolean)obj);
//					}
//					else if(obj instanceof Blob) {
//						cell.setCellValue("檔案");
//					}
//					//sql Null
//					else if(obj == null) {
//						cell.setCellValue("(null)");
//					}
//					//sql undefined
//					else {
//						cell.setCellValue("ERROR");
//					}
//				}
//			}
//		}

		//頁簽2
		Row row_head2 = sheet.createRow(0);
		Cell cell_head = row_head2.createCell(0);
		cell_head.setCellStyle(cellStyleFont);
		cell_head.setCellValue("資況表列印記錄");
		int cell_head_num2 = 0;
		row_head2 = sheet.createRow(1);
		//頁簽2
		for (Entry<String, String> strs : order2.entrySet()) {
			String str = strs.getValue();
			cell_head = row_head2.createCell(cell_head_num2++);
			cell_head.setCellStyle(cellStyleFont);
			cell_head.setCellValue(str);
		}
		//頁簽2
		List<String> orderkey2 = new ArrayList<String>();
		for ( String key : order2.keySet() ) {
			orderkey2.add(key);
		}
		//頁簽2
		int rownum2 = sheet.getLastRowNum();
		//頁簽2
		for (Map<String, Object> objs : list2) {
			Row row = sheet.createRow(++rownum2);

			for (Entry<String, Object> en : objs.entrySet()) {
				Object obj  = en.getValue();
				int idx = orderkey2.indexOf(en.getKey());

				if (idx >= 0) {
					Cell cell = row.createCell(idx);
					cell.setCellStyle(cellStyleFont);

					//sql String
					if (obj instanceof String) {
						cell.setCellValue((String)obj);
					//sql Date
					} else if(obj instanceof Date) {
						cell.setCellStyle(cellStyleDate);
						cell.setCellValue((Date)obj);
					}
					//sql Number
					else if(obj instanceof Number) {
						cell.setCellValue((getBigDecimal(obj)).doubleValue());
					}
					//sql Double
					else if(obj instanceof Double) {
						cell.setCellValue((Double)obj);
					}
					//sql Boolean
					else if(obj instanceof Boolean) {
						cell.setCellValue((Boolean)obj);
					}
					else if(obj instanceof Blob) {
						cell.setCellValue("檔案");
					}
					//sql Null
					else if(obj == null) {
						cell.setCellValue("(null)");
					}
					//sql undefined
					else {
						cell.setCellValue("ERROR");
					}
				}
			}
		}
//

		//主管 經辦簽名欄
		Row row_head3 = sheet.createRow(rownum2+=2);
		cell_head = row_head3.createCell(0);
		cell_head.setCellStyle(cellStyleFont);
		cell_head.setCellValue("主管");
		cell_head = row_head3.createCell(4);
		cell_head.setCellStyle(cellStyleFont);
		cell_head.setCellValue("經辦");

//		//autoSizeColumn 頁簽1
//		for (int i = 0; i < row_head.getPhysicalNumberOfCells(); i++) {
//			sheet.autoSizeColumn(i);
//		}
		//autoSizeColumn 頁簽2
		for (int i = 0; i < row_head2.getPhysicalNumberOfCells(); i++) {
			sheet.autoSizeColumn(i);
		}

		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String uuid = UUID.randomUUID().toString();
		String fileName = name + ".xlsx";
		String filePath = Path + uuid;

		workbook.write(new FileOutputStream(filePath));

		Map<String, String> params = new HashMap<String, String>();
        params.put("fileUrl", DataManager.getSystem().getPath().get("temp").toString() + uuid);
        params.put("defaultFileName", fileName);

		workbook.close();
		return (params);
	}

	public BigDecimal getBigDecimal(Object val){
		if(val == null){
			return new BigDecimal(0);
		}else{
			return new BigDecimal(val.toString());
		}
	}

	private List<Map<String,Object>> getLoan2004128(String cust_id , List<Map<String,Object>> loanList) throws Exception {
		CRM841 crm841 = (CRM841) PlatformContext.getBean("crm841");
		String SEQNO = "";
		SEQNO = crm841.getSeq_Num();

		String IDNO = cust_id;
		int IDLen = 0;
		IDLen = cust_id.toString().length();
		//SC120100上行電文需帶入11碼客戶ID，故客戶ID不足10則在後面補上不足數目的空格。
		IDNO = ObjectUtils.toString(cust_id);
		if(IDLen < 11){
			for(int i = 0; i < 10 - IDLen; i++){
				IDNO += " ";
			}
		}

		String TYPE = "2"; 		//1.存款2.放款
		String ITEM_NO = "2";	//2.明細

		//傳送電文用
		String DATA1= SEQNO+"&"+IDNO+" "+"&"+TYPE+"&"+ITEM_NO;

        //init util
        ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, SC120100);
        esbUtilInputVO.setModule("CRM8502.getLoan2004128");

        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        esbUtilInputVO.setTxHeadVO(txHead);
        txHead.setDefaultTxHead();
        txHead.setHTLID("2004128");
        txHead.setHFMTID("0001");

        //body
        SC120100InputVO txBodyVO = new SC120100InputVO();
        esbUtilInputVO.setSc120100InputVO(txBodyVO);
        txBodyVO.setFUNC_COD("54");
        txBodyVO.setDATA1(DATA1);

        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
        System.out.println("測試成功!");
        SC120100OutputVO sc120100OutputVO = new SC120100OutputVO();
        List<Map<String,Object>> results = new ArrayList<>();

        for(ESBUtilOutputVO esbUtilOutputVO : vos) {
        	sc120100OutputVO = esbUtilOutputVO.getSc120100OutputVO();
            List<SC120100DetailOutputVO> details = sc120100OutputVO.getDetails();
            details = (CollectionUtils.isEmpty(details)) ? new ArrayList<SC120100DetailOutputVO>() : details;

            for (SC120100DetailOutputVO data : details) {
            	String acno = data.getACNO().trim();//貸款帳號
            	String acnoSa = data.getACNO_SA().trim();//扣款帳號
            	String startDte = data.getPENL_STR_DATE().trim();//起迄期間
            	String endDte = data.getDUE_DATE().trim();
            	String strRATE = new EsbUtil().decimalPoint(data.getRATE(), 4).toString();//貸款利率
//            	String oriLoanBal = new EsbUtil().decimalPoint(data.getORI_LOAN_BAL().trim(), 2).toString();//貸款額度
            	String actBal = new EsbUtil().decimalPoint(data.getACT_BAL().trim(),2).toString();//貸款餘額
            	String actBalNt = new EsbUtil().decimalPoint(data.getACT_BAL_NT().trim(),2).toString();//貸款餘額(折台)
            	String curCod = data.getCUR_COD().trim();
            	String type = data.getTYPE().trim();
            	String loanType = data.getLOAN_TYP().trim();
            	String docNo = data.getDOC_NO()==null?"":data.getDOC_NO();
            	String[] refnoAndDibso = docNo.split(" +");

        		Map<String,Object> map = new HashMap();
        		map.put("LNAC_ACCT1" , acno);
        		map.put("LNAC_ACCT2" , acnoSa);
        		map.put("LNAC_IDATE", startDte);
        		map.put("LNAC_EDATE", endDte);
        		map.put("LNAC_FRATE", strRATE);
//        		map.put("LNAC_PAMT", oriLoanBal);
        		map.put("LNAC_BAL", actBal);
        		map.put("LNAC_BAL_NT", actBalNt);
        		map.put("LNAC_CUR1", curCod);
        		map.put("LNAC_CUR2", curCod);
        		map.put("LOAN_TYPE", loanType);
        		map.put("TYPE",type);
            	for (Map odsMap:loanList) {
            		String acct1 = odsMap.get("LNAC_ACCT1")==null?"":(String) odsMap.get("LNAC_ACCT1");
            		acct1 = acct1.split("-")[0];
            		String acct2 = odsMap.get("LNAC_ACCT2")==null?"":(String)odsMap.get("LNAC_ACCT2");
            		String refno = odsMap.get("LNAC_REFNO")==null?"":(String) odsMap.get("LNAC_REFNO");
            		String dibso = odsMap.get("LNAC_DIBSO")==null?"":(String) odsMap.get("LNAC_DIBSO");
            		String odsEndDte = odsMap.get("LNAC_EDATE")==null?"":(String) odsMap.get("LNAC_EDATE");
            		String loanName = odsMap.get("LNAC_NAME")==null?"":(String) odsMap.get("LNAC_NAME");
            		if (acct2 != null && acnoSa != null) {
	            		if(acct1.equals(acno) &&
	            				acct2.equals(acnoSa) &&
	            				refno.equals(refnoAndDibso[0]) &&
	            				dibso.equals(refnoAndDibso[1]) &&
	            				odsEndDte.equals(endDte)) {
	            			map.put("LNAC_PAMT", this.getBigDecimal(odsMap.get("LNAC_PAMT")));
	            		}
	            		if (acct1.split("-")[0].equals(acno) &&
	            				acct2.equals(acnoSa) && loanName.equals("綜存質借"))
	            			map.put("LNAC_PAMT", this.getBigDecimal(odsMap.get("LNAC_PAMT")));
            		}
            	}
				//分期型房貸
				if("61".equals(type) && !StringUtils.isEmpty(data.getACNO()) &&  !"XXX".equals(curCod)){
					map.put("LNAC_NAME", "分期型房貸");//LNAC_NAME;
				}
				//循環型startj
				//循環型信貸(額度式)
				if("69".equals(type)  && ("01".equals(loanType)) ){
					map.put("LNAC_NAME", "循環型信貸(額度式)");//LNAC_NAME;
				}
				//循環型房貸(回復式)
				else if("69".equals(type) && "02".equals(loanType)) {
					map.put("LNAC_NAME", "循環型房貸(回復式)");//LNAC_NAME;
				}
				//綜存質借(台幣)
				if("67".equals(type) && "TWD".equals(curCod)){
					if (new BigDecimal(actBal).compareTo(BigDecimal.ZERO) > 0)
						map.put("LNAC_NAME", "綜存質借(台幣)");//LNAC_NAME;
				}
				//綜存質借(非台幣　一本萬利外幣)
				if("67".equals(type) && !"TWD".equals(curCod)
						&& (("17".equals( (String.valueOf((Integer.parseInt(acno)))).substring(3, 4) )) || "18".equals( (String.valueOf((Integer.parseInt(acno)))).substring(3, 4) ))
						){
					if (new BigDecimal(actBal).compareTo(BigDecimal.ZERO) > 0)
						map.put("LNAC_NAME", "綜存質借(非台幣　一本萬利外幣)");//LNAC_NAME;
				}
				//綜存質借(非台幣　一般外幣外幣)
				if("67".equals(type) && !"TWD".equals(curCod) && "168".equals( (String.valueOf((Integer.parseInt(acno)))).substring(3, 5) ) ){
					if (new BigDecimal(actBal).compareTo(BigDecimal.ZERO) > 0)
						map.put("LNAC_NAME", "綜存質借(非台幣　一般外幣外幣)");//LNAC_NAME;
				}
				//循環型end
            	//學貸
            	if (!StringUtils.isEmpty(acno) && !"XXX".equals(curCod)
                    && "63".equals(type) && "01".equals(loanType)) {
            		map.put("LNAC_NAME" , "就學貸款");
            	}
            	//信用貸款
				if (!StringUtils.isEmpty(acno)
						&& ( "62".equals(type) ) && !"XXX".equals(curCod)) {
            		map.put("LNAC_NAME" , "信用貸款");
				}
				//留學貸款
            	if (!StringUtils.isEmpty(acno) && !"XXX".equals(curCod)
                        && "63".equals(type) && "02".equals(loanType)) {
            		map.put("LNAC_NAME" , "留學貸款");
            	}
            	//存單質借
            	if (!StringUtils.isEmpty(acno) && !"XXX".equals(curCod)
                        && "70".equals(type)) {
					if (new BigDecimal(actBal).compareTo(new BigDecimal(0)) > 0)
						map.put("LNAC_NAME" , "存單質借");
            	}
				//企業貸款
				if("65".equals(data.getTYPE())) {
//					data.setACT_BAL_NT(new EsbUtil().decimalPoint(data.getACT_BAL_NT(), 2).toString());
            		map.put("LNAC_NAME" , "企業貸款");
				}


            	if (!StringUtils.isEmpty(acno) && !"XXX".equals(curCod)	&&	map.get("LNAC_NAME")!=null && new BigDecimal(actBal).compareTo(BigDecimal.ZERO) > 0)
            		results.add(map);
            }
        }
        return results;
	}

	private boolean isAuthOrReview(String role) {
		String[] authNReviewMap = {"A156","A157"};//覆核人員&有權簽章
		for (String value:authNReviewMap) {
			if (role.equals(value))
				return true;
		}
		return false;
	}
	private class PrintOption {
		private String printAll;//列印所有資產
		private String printSav;//列印存款
		private String printInv;//列印投資
		private String printIns;//列印保險
		private String printLoan;//列印融資

		private String printAllChart;//列印所有圖示
		private String printAum;//列印AUM趨勢圖
		private String printIil;//列印存投保分佈圖
		private String printCur;//列印總資產計價幣別圖
		private String printType;//列印資產類別圖
		private String printFundMkt;//列印基金投資區域圖
		private String printPortfolio;//列印投資組合分佈圖
		public String getPrintAll() {
			return printAll;
		}
		public void setPrintAll(String printAll) {
			this.printAll = printAll;
		}
		public String getPrintSav() {
			return printSav;
		}
		public void setPrintSav(String printSav) {
			this.printSav = printSav;
		}
		public String getPrintInv() {
			return printInv;
		}
		public void setPrintInv(String printInv) {
			this.printInv = printInv;
		}
		public String getPrintIns() {
			return printIns;
		}
		public void setPrintIns(String printIns) {
			this.printIns = printIns;
		}
		public String getPrintLoan() {
			return printLoan;
		}
		public void setPrintLoan(String printLoan) {
			this.printLoan = printLoan;
		}
		public String getPrintAllChart() {
			return printAllChart;
		}
		public void setPrintAllChart(String printAllChart) {
			this.printAllChart = printAllChart;
		}
		public String getPrintAum() {
			return printAum;
		}
		public void setPrintAum(String printAum) {
			this.printAum = printAum;
		}
		public String getPrintIil() {
			return printIil;
		}
		public void setPrintIil(String printIil) {
			this.printIil = printIil;
		}
		public String getPrintCur() {
			return printCur;
		}
		public void setPrintCur(String printCur) {
			this.printCur = printCur;
		}
		public String getPrintType() {
			return printType;
		}
		public void setPrintType(String printType) {
			this.printType = printType;
		}
		public String getPrintFundMkt() {
			return printFundMkt;
		}
		public void setPrintFundMkt(String printFundMkt) {
			this.printFundMkt = printFundMkt;
		}
		public String getPrintPortfolio() {
			return printPortfolio;
		}
		public void setPrintPortfolio(String printPortfolio) {
			this.printPortfolio = printPortfolio;
		}

	}
}
