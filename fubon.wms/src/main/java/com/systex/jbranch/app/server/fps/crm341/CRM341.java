package com.systex.jbranch.app.server.fps.crm341;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_10CMDT_CHGAO_FILEVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_AOCHG_PLISTVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_APL_SEQ_DTLVO;
import com.systex.jbranch.app.server.fps.crm8501.CRM8501;
import com.systex.jbranch.app.server.fps.crm8501.CRM8501InputVO;
import com.systex.jbranch.app.server.fps.crm8501.CRM8501OutputVO;
import com.systex.jbranch.app.server.fps.ins810.INS810;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.PdfUtil;
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
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/06/16
 * 
 */
@Component("crm341")
@Scope("request")
public class CRM341 extends FubonWmsBizLogic {
	private String newAoBrh = "";
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM341.class);

	public void initial(Object body, IPrimitiveMap header) throws JBranchException {
		CRM341InputVO inputVO = (CRM341InputVO) body;
		CRM341OutputVO outputVO = new CRM341OutputVO();
		dam = this.getDataAccessManager();

		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2); //業務處
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2); //營運區

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.EMP_ID, A.EMP_NAME, A.AO_CODE, B.TYPE ");
		sql.append("FROM VWORG_AO_INFO A ");
		sql.append("LEFT JOIN TBORG_SALES_AOCODE B on A.AO_CODE = B.AO_CODE and A.EMP_ID = B.EMP_ID ");
		sql.append("WHERE A.AO_CODE IS NOT NULL ");
		if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND (A.CENTER_ID IN (:rcIdList) OR A.CENTER_ID IS NULL) ");
			sql.append("AND (A.AREA_ID IN (:opIdList) OR A.AREA_ID IS NULL) ");
			sql.append("AND (A.BRA_NBR IN (:brNbrList) OR A.BRA_NBR IS NULL) ");
		} else if (armgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND (A.CENTER_ID IN (:rcIdList)) ");
			sql.append("AND (A.AREA_ID IN (:opIdList) OR A.AREA_ID IS NULL) ");
			sql.append("AND (A.BRA_NBR IN (:brNbrList) OR A.BRA_NBR IS NULL) ");
		} else if (mbrmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND (A.CENTER_ID IN (:rcIdList)) ");
			sql.append("AND (A.AREA_ID IN (:opIdList)) ");
			sql.append("AND (A.BRA_NBR IN (:brNbrList) OR A.BRA_NBR IS NULL) ");
		} else {
			sql.append("AND (A.CENTER_ID IN (:rcIdList)) ");
			sql.append("AND (A.AREA_ID IN (:opIdList)) ");
			sql.append("AND (A.BRA_NBR IN (:brNbrList)) ");
		}
		queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));

		List<String> curr_ao_list = (ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
		if (curr_ao_list.size() > 0) {
			sql.append("AND A.AO_CODE in (:curr_ao_code) ");
			queryCondition.setObject("curr_ao_code", curr_ao_list);
		}
		sql.append(" ORDER BY A.AO_CODE ");
		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		outputVO.setAo_list(list);

		this.sendRtnObject(outputVO);
	}

	//查詢
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM341InputVO inputVO = (CRM341InputVO) body;
		CRM341OutputVO outputVO = new CRM341OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.CUST_ID, A.CUST_NAME, ");
		sql.append("       D.REGION_CENTER_NAME as CENTER_NAME, D.BRANCH_AREA_NAME as AREA_NAME, A.BRA_NBR, D.BRANCH_NAME, ");
		sql.append("       B.EMP_NAME, B.AO_CODE, RO.ROLE_ID, RO.ROLE_NAME, RO.JOB_TITLE_NAME, A.AUM_AMT, A.CON_DEGREE, A.VIP_DEGREE, N.CO_ACCT_YN, A.UEMP_ID, ");
		//2018-12-12 by Jacky WMS-CR-20181025-03 增加禁銷戶檢查
		sql.append("       COMM_NS_YN, B.TYPE ");
		sql.append("FROM TBCRM_CUST_MAST A ");
		sql.append("LEFT JOIN TBCRM_CUST_NOTE N ON A.CUST_ID = N.CUST_ID ");
		sql.append("LEFT JOIN VWORG_AO_INFO B ON A.AO_CODE = B.AO_CODE ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO D ON A.BRA_NBR = D.BRANCH_NBR ");
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT MR.EMP_ID, RO.ROLE_ID, RO.ROLE_NAME, RO.JOB_TITLE_NAME ");
		sql.append("  FROM TBORG_MEMBER_ROLE MR, TBORG_ROLE RO ");
		sql.append("  WHERE MR.ROLE_ID = RO.ROLE_ID AND MR.IS_PRIMARY_ROLE = 'Y' AND RO.REVIEW_STATUS = 'Y' AND RO.IS_AO = 'Y'");
		sql.append(") RO ON B.EMP_ID = RO.EMP_ID ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND A.CUST_ID = :cust_id ");
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setResultList(list);

		// 加工 原 AO_CODE 如果撈出來沒資料，但客戶主檔有資料 (客戶名稱顯示離職)　
		// sen issue 4131 20180111 離職理專空 code 認領
		Map<String, Object> custInfoMap = this.getCustInfo(inputVO);
		if (list != null && list.size() > 0 && custInfoMap != null) {
			String oldAoCode = (String) (list.get(0)).get("AO_CODE");
			if ((oldAoCode == null || "".equals(oldAoCode.trim())) && (custInfoMap.get("AO_CODE") != null && !"".equals(custInfoMap.get("AO_CODE")))) {
				list.get(0).put("EMP_NAME", "已離職");
			}

			if (list.get(0).get("COMM_NS_YN") != null && StringUtils.equals(list.get(0).get("COMM_NS_YN").toString(), "Y")) {
				throw new APException("為NS戶，無法申請客戶移入，請洽分行內控品管科");
			}
		}

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT NEW_AO_CODE ");
		sql.append("FROM TBCRM_TRS_AOCHG_PLIST ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND CUST_ID = :cust_id ");
		sql.append("AND PROCESS_STATUS in ('L0', 'L1', 'L2', 'L3', 'L4', 'L5') ");
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		if (list2.size() > 0)
			outputVO.setResultList2(joinListByCom(list2, ","));
		
		//WMS-CR-20200214-01_新增理專輪調暨客戶換手經營資料產出及後續控管  Jacky added
		outputVO.setCMDTCust(this.check10CMDTCust(inputVO.getCust_id(), inputVO.getNew_ao_code()));
		//2022必輪調名單：RM輪調後，帶走30%核心客戶，一年內不得再帶走該RM轄下原分行70%客戶
		outputVO.setCMDT2022CUST1_YN(this.check2022CMDTCust1(inputVO.getCust_id(), inputVO.getNew_ao_code()));
		//2022必輪調名單：輪調後帶至新分行之核心客戶，依客戶意願欲轉回原分行
		outputVO.setCMDT2022CUST2_YN(this.check2022CMDTCust2(inputVO.getCust_id(), inputVO.getNewVOList().get("NEW_BRA_NBR")));
		//2022換手名單：是否為2022換手名單6個月內要移回原理專的客戶
		outputVO.setCMDT2022CUST3_YN(this.check2022CMDTCust3(inputVO.getCust_id(), inputVO.getNew_ao_code()));
		//2023必輪調名單：必輪調RM名單上傳後，名單中RM不可做移入申請
		outputVO.setCMDT2023CUST1_YN(this.check2023CMDTCust1());
		//2023必輪調名單：必輪調RM名單上傳後，名單中客戶不可做移入申請
		outputVO.setCMDT2023CUST2_YN(this.check2023CMDTCust2(inputVO.getCust_id()));
		//2023必輪調名單：RM輪調後，帶走30%核心客戶，一年內不得再帶走該RM轄下原分行70%客戶
		outputVO.setCMDT2023CUST3_YN(this.check2023CMDTCust3(inputVO.getCust_id(), inputVO.getNew_ao_code()));
		//2023必輪調名單：區域分行一年內若因客戶意願欲轉回原RM服務，經由一階主管同意，再經由處主管同意後生效，客戶即可轉回原RM
		outputVO.setCMDT2023CUST4_YN(this.check2023CMDTCust4(inputVO.getCust_id(), inputVO.getNew_ao_code()));
		//2023必輪調名單：非區域分行，輪調後帶至新分行之核心客戶，依客戶意願欲轉回原分行
		outputVO.setCMDT2023CUST5_YN(this.check2023CMDTCust5(inputVO.getCust_id(), inputVO.getNewVOList().get("NEW_BRA_NBR")));
		
		this.sendRtnObject(outputVO);
	}

	public void changeNewAO(Object body, IPrimitiveMap header) throws JBranchException {
		newAoBrh = "";
		CRM341InputVO inputVO = (CRM341InputVO) body;
		CRM341OutputVO outputVO = new CRM341OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT CENTER_ID as NEW_CENTER_ID, ");
		sql.append("       CENTER_NAME as NEW_CENTER_NAME, ");
		sql.append("       AREA_ID as NEW_AREA_ID, ");
		sql.append("       AREA_NAME as NEW_AREA_NAME, ");
		sql.append("       BRA_NBR as NEW_BRA_NBR, ");
		sql.append("       BRANCH_NAME as NEW_BRANCH_NAME, ");
		sql.append("       EMP_NAME as NEW_EMP_NAME, ");
		sql.append("       AO_CODE as NEW_AO_CODE, ");
		sql.append("       TYPE AS NEW_AO_TYPE, ");
		sql.append("       RO.JOB_TITLE_NAME as NEW_JOB_TITLE_NAME, ");
		sql.append("       RO.ROLE_ID as NEW_ROLE_ID, ");
		sql.append("       RO.ROLE_NAME as NEW_ROLE_NAME ");
		sql.append("FROM VWORG_AO_INFO ");
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT MR.EMP_ID, RO.ROLE_ID, RO.ROLE_NAME, RO.JOB_TITLE_NAME ");
		sql.append("  FROM TBORG_MEMBER_ROLE MR, TBORG_ROLE RO ");
		sql.append("  WHERE MR.ROLE_ID = RO.ROLE_ID ");
//		sql.append("  AND MR.IS_PRIMARY_ROLE = 'Y' "); // 20210205 modify by ocean : mark by 祐傑，銀證(兼FC)申請移轉時，主管無法覆核 => MR.IS_PRIMARY_ROLE = 'Y'
		sql.append("  AND RO.REVIEW_STATUS = 'Y' ");
		sql.append("  AND RO.IS_AO = 'Y'");
		sql.append(") RO ON VWORG_AO_INFO.EMP_ID = RO.EMP_ID ");
		sql.append("WHERE AO_CODE = :ao_code ");
		queryCondition.setObject("ao_code", inputVO.getNew_ao_code());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setResultList(list);
		newAoBrh = (String) list.get(0).get("NEW_BRA_NBR");
		this.sendRtnObject(outputVO);
	}

	//下載
	public void download(Object body, IPrimitiveMap header) throws Exception {
		CRM341InputVO inputVO = (CRM341InputVO) body;
		String rtn = download(inputVO);
		this.sendRtnObject(null);
	}
	
	//下載	
	public String download(CRM341InputVO inputVO) throws Exception {
		//新增序號
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String SN = sdf.format(new Date()) + getAPLSeq();
		while (checkID(SN)) {
			SN = sdf.format(new Date()) + getAPLSeq();
		}
		
		//檢查是否為十保客戶
        if (!inputVO.isCMDTCust() && !inputVO.isIs2022CMDTCust3() && !inputVO.isIs2023CMDTCust4()){
        	String con_degree = inputVO.getOldVOList().get("CON_DEGREE");
        	String reportID = "";
        	if ("E".equals(con_degree) || "I".equals(con_degree)) {
    			reportID = "R1";
    		} else {
    			reportID = "R2";
    		}
        	String url = this.printReport(inputVO, reportID, "", SN); //原來流程
        	notifyClientToDownloadFile(url, "客戶同意書_" + SN + ".pdf");
        } else {
        	//十保客戶流程
        	//2022換手客戶
        	//2023必輪調：區域分行非核心客戶一年內移回原理專
        	List<String> rptURL = this.downloadCMDTCust(inputVO, SN); 
        	String url = PdfUtil.mergePDF(rptURL, false);
        	if(StringUtils.equals("Y", inputVO.getFromCRM3103YN())) {
        		return url;
        	} else {
        		notifyClientToDownloadFile(url, "資況表申請書暨自主聲明書.pdf");
        	}
        }
        
        return "";
	}
	
	/**
	 * 下載客戶指定理專聲明聲(不在十保客戶名單裡)-原流程(R1,R2), R3-服務人員異動暨客戶資產表申請書
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @author Jacky Wu
	 */
	private String printReport(CRM341InputVO inputVO, String reportID, String docSeq, String SN) throws JBranchException {
		dam = this.getDataAccessManager();

		String url = null;
		String txnCode = "CRM341";
		ReportIF report = null;

		//避免尚未按下查詢就下載客戶同意書，以查詢過的客戶ID為主
		inputVO.setCust_id(inputVO.getOldVOList().get("CUST_ID"));

		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();//取得傳輸資料給report模組的instance
		ReportGeneratorIF gen = factory.getGenerator(); //取得產生PDF檔的instance
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);

//		try {
			// 創建存放明細的List	
			Map<String, String> old_ao = inputVO.getOldVOList();
			Map<String, String> new_ao = inputVO.getNewVOList();

			//身分證拆解
			String B1 = "", B2 = "", B3 = "", B4 = "", B5 = "", B6 = "", B7 = "", B8 = "", B9 = "", BA = "";
			B1 = inputVO.getCust_id().length() >= 1 ? inputVO.getCust_id().substring(0, 1) : "";
			B2 = inputVO.getCust_id().length() >= 2 ? inputVO.getCust_id().substring(1, 2) : "";
			B3 = inputVO.getCust_id().length() >= 3 ? inputVO.getCust_id().substring(2, 3) : "";
			B4 = inputVO.getCust_id().length() >= 4 ? inputVO.getCust_id().substring(3, 4) : "";
			B5 = inputVO.getCust_id().length() >= 5 ? inputVO.getCust_id().substring(4, 5) : "";
			B6 = inputVO.getCust_id().length() >= 6 ? inputVO.getCust_id().substring(5, 6) : "";
			B7 = inputVO.getCust_id().length() >= 7 ? inputVO.getCust_id().substring(6, 7) : "";
			B8 = inputVO.getCust_id().length() >= 8 ? inputVO.getCust_id().substring(7, 8) : "";
			B9 = inputVO.getCust_id().length() >= 9 ? inputVO.getCust_id().substring(8, 9) : "";
			BA = inputVO.getCust_id().length() >= 10 ? inputVO.getCust_id().substring(9, 10) : "";

			//分行拆解
			String D1 = "", D2 = "", D3 = "";//新分行
			String H1 = "", H2 = "", H3 = "";//舊分行

			H1 = old_ao.get("BRA_NBR").length() >= 1 ? old_ao.get("BRA_NBR").substring(0, 1) : "";
			H2 = old_ao.get("BRA_NBR").length() >= 2 ? old_ao.get("BRA_NBR").substring(1, 2) : "";
			H3 = old_ao.get("BRA_NBR").length() >= 3 ? old_ao.get("BRA_NBR").substring(2, 3) : "";
			D1 = new_ao.get("NEW_BRA_NBR").length() >= 1 ? new_ao.get("NEW_BRA_NBR").substring(0, 1) : "";
			D2 = new_ao.get("NEW_BRA_NBR").length() >= 2 ? new_ao.get("NEW_BRA_NBR").substring(1, 2) : "";
			D3 = new_ao.get("NEW_BRA_NBR").length() >= 3 ? new_ao.get("NEW_BRA_NBR").substring(2, 3) : "";

			//將值帶入報表欄位中		
			data.addParameter("CUST_ID", inputVO.getCust_id());

			data.addParameter("$B1", B1);
			data.addParameter("$B2", B2);
			data.addParameter("$B3", B3);
			data.addParameter("$B4", B4);
			data.addParameter("$B5", B5);
			data.addParameter("$B6", B6);
			data.addParameter("$B7", B7);
			data.addParameter("$B8", B8);
			data.addParameter("$B9", B9);
			data.addParameter("$BA", BA);

			data.addParameter("$D1", D1);
			data.addParameter("$D2", D2);
			data.addParameter("$D3", D3);
			data.addParameter("$H1", H1);
			data.addParameter("$H2", H2);
			data.addParameter("$H3", H3);

			data.addParameter("CUST_NAME", old_ao.get("CUST_NAME"));
			data.addParameter("OLD_AO_CODE", old_ao.get("AO_CODE"));
			data.addParameter("NEW_AO_CODE", new_ao.get("NEW_AO_CODE"));
			data.addParameter("OLD_BRA_NAME", old_ao.get("BRANCH_NAME"));
			data.addParameter("NEW_BRA_NAME", new_ao.get("NEW_BRANCH_NAME"));
			data.addParameter("OLD_EMP_NAME", old_ao.get("EMP_NAME"));
			data.addParameter("NEW_EMP_NAME", new_ao.get("NEW_EMP_NAME"));
			
			String RptSN = "", RptSN2 = "";
			RptSN = "異動申請書編號：" + SN; //#0002122 : 左上方異動單編號，建議前面加上「異動申請書編號：」
					
			data.addParameter("SN", RptSN);
			if(StringUtils.equals(reportID, "R3") || StringUtils.equals(reportID, "R5")){//R3orR5報表
				RptSN2 = "資產現況表申請書編號：" + docSeq; //資產表申請書編號
				data.addParameter("SN2", RptSN2);
			}
			
			// TBCRM_TRS_APL_SEQ_DTL
			TBCRM_TRS_APL_SEQ_DTLVO vo = new TBCRM_TRS_APL_SEQ_DTLVO();
			vo.setAPL_SEQ(SN);
			vo.setCUST_ID(inputVO.getCust_id());
			Calendar calendar = Calendar.getInstance();
			java.sql.Timestamp date = new java.sql.Timestamp(calendar.getTime().getTime());
			vo.setAPL_DATE(date);
			vo.setNEW_AO_CODE(new_ao.get("NEW_AO_CODE"));
			dam.create(vo);
			report = gen.generateReport(txnCode, reportID, data);
			url = report.getLocation();

			return url;		
			
//		} catch (Exception e) {
//			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
//			throw new APException("系統發生錯誤請洽系統管理員");
//		} 
	}
	
	/**
	 * 下載客戶指定理專聲明聲(在十保客戶名單裡)-新流程 2020/3/20新增
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @author Jacky Wu
	 */
	private List<String> downloadCMDTCust(CRM341InputVO inputVO, String SN) throws JBranchException {
		//1. 檢查是同分行移入或跨分行移入
		//1.1 同分行移入產生 a.客戶資況表申請書 b.客戶指定理專自主聲明書
		//1.2 跨分行移入產生 a.客戶移入兼資況表申請書 b.客戶指定理專自主聲明書
		
		List<String> mergeUrl = new ArrayList();
		
		//處理資況表申請書
		CRM8501 crm8501 = (CRM8501) PlatformContext.getBean("crm8501");
		CRM8501InputVO crm8501InputVO = new CRM8501InputVO();
		crm8501InputVO.setCustID(inputVO.getCust_id());
		if(StringUtils.isNotBlank(inputVO.getNewVOList().get("PRINT_BRA_NBR"))) {
			//2023必輪調的新分行顯示為空(尚未輪調)，但列印分行需設為原分行(否則分行主管查不到此筆資料)
			crm8501InputVO.setBranchNbr(inputVO.getNewVOList().get("PRINT_BRA_NBR"));
		} else {
			crm8501InputVO.setBranchNbr(inputVO.getNewVOList().get("NEW_BRA_NBR"));
		}
		crm8501InputVO.setAoCode(inputVO.getNewVOList().get("NEW_AO_CODE"));
		crm8501InputVO.setPrintAll("Y"); //明細全選
		crm8501InputVO.setPrintSav("Y");
		crm8501InputVO.setPrintInv("Y");
		crm8501InputVO.setPrintIns("Y");
		crm8501InputVO.setPrintLoan("N");
		crm8501InputVO.setPrintAllChart("N"); //圖形
		crm8501InputVO.setPrintAUM("N");
		crm8501InputVO.setPrintIIL("N");
		crm8501InputVO.setPrintCUR("N");
		crm8501InputVO.setPrintType("N");
		crm8501InputVO.setPrintFundMkt("N");
		crm8501InputVO.setPrintPortfolio("N");
		crm8501InputVO.setFromTxn("CRM341");
		crm8501InputVO.setFundSortType("byCERT_NBR");
		
		String docSeq = "";
		String docUrl = "";
		if(StringUtils.equals(inputVO.getNewVOList().get("NEW_BRA_NBR"), inputVO.getOldVOList().get("BRA_NBR"))){ //同分行移入
			crm8501InputVO.setPrint(true);
			CRM8501OutputVO crm8501outputVO = crm8501.applyPrint(crm8501InputVO);
			docSeq = crm8501outputVO.getDosSeq();
			docUrl = crm8501outputVO.getUrl();			
		}else{ //跨分行移入
			crm8501InputVO.setPrint(false);
			CRM8501OutputVO crm8501outputVO = crm8501.applyPrint(crm8501InputVO);
			docSeq = crm8501outputVO.getDosSeq();	
			
			//套印客戶主要往來/服務人員異動暨客戶資況表申請書
			if(StringUtils.equals("Y", inputVO.getFromCRM3103YN())) {
				docUrl = this.printReport(inputVO, "R5", docSeq, SN); //2023必輪調
			} else {
				docUrl = this.printReport(inputVO, "R3", docSeq, SN);
			}
		}
		mergeUrl.add(docUrl);
		
		String url = null;
		String txnCode = "CRM341";
		String reportID;
		ReportIF report = null;
		try {
			  reportID = "R4"; //客戶指定理專自主聲明書
			
			  ReportFactory factory = new ReportFactory();
			  ReportDataIF data = new ReportData();//取得傳輸資料給report模組的instance
			  ReportGeneratorIF gen = factory.getGenerator(); //取得產生PDF檔的instance
			  String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			  Map<String, String> old_ao = inputVO.getOldVOList();
			  Map<String, String> new_ao = inputVO.getNewVOList();
			  //將值帶入報表欄位中		
			  data.addParameter("CUST_NAME", old_ao.get("CUST_NAME"));
			  data.addParameter("BRANCH_NAME", new_ao.get("NEW_BRANCH_NAME"));
			  data.addParameter("AO_NAME", new_ao.get("NEW_EMP_NAME"));
			  data.addParameter("SN", docSeq);
			  report = gen.generateReport(txnCode, reportID, data);
			  url = report.getLocation();
			  
			  mergeUrl.add(url);
			  
//			  String fileName = "客戶指定理專自主聲明書_"+docSeq+".pdf";
//			  notifyClientToDownloadFile(url, fileName);
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		
		return mergeUrl;
	}
	
	public void upload(Object body, IPrimitiveMap header) throws JBranchException {
		CRM341InputVO inputVO = (CRM341InputVO) body;
		CRM341OutputVO outputVO = new CRM341OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT APL_SEQ, TRUNC(SYSDATE) - TRUNC(APL_DATE) as VALID_DAY, APL_DATE, CREATOR, NEW_AO_CODE, CUST_ID ");
		sql.append("FROM TBCRM_TRS_APL_SEQ_DTL WHERE APL_SEQ = :seq ");
		queryCondition.setObject("seq", inputVO.getAgmt_seq());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setResultList(list);
		this.sendRtnObject(outputVO);
	}

	//檢查
	public void checkadd(Object body, IPrimitiveMap header) throws JBranchException {
		CRM341InputVO inputVO = (CRM341InputVO) body;
		CRM341OutputVO outputVO = new CRM341OutputVO();
		dam = this.getDataAccessManager();

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = new HashMap(xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2)); //理專
		Map<String, String> fchMap = new HashMap(xmlInfo.doGetVariable("FUBONSYS.FCH_ROLE", FormatHelper.FORMAT_2)); //FCH理專

		Boolean Check1 = false;
		Boolean Check2 = false;
		Boolean Check3 = false;
		Boolean Check4 = false;
		Boolean Check5 = false;
		Boolean Check6 = false;
		Boolean Check7 = false;
		Boolean Check8 = false;

		Map<String, String> oldVOList = inputVO.getOldVOList();
		Map<String, String> newVOList = inputVO.getNewVOList();
		// 檢核是否超過客戶數上限  NEW_ROLE_NAME in (FC1, FC2, FC3, FC4, FC5)
		if (fcMap.containsKey(newVOList.get("NEW_ROLE_ID"))) {
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT COUNT(1) CUR_CNT FROM TBCRM_CUST_MAST WHERE AO_CODE = :ao_code ");
			queryCondition.setObject("ao_code", inputVO.getNew_ao_code());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			BigDecimal CUR_CNT = new BigDecimal(ObjectUtils.toString(list.get(0).get("CUR_CNT")));

			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT S.TTL_CUST_NO_LIMIT_UP FROM TBCRM_TRS_CUST_MGMT_SET S WHERE S.AO_JOB_RANK = :role_id ");
			queryCondition.setObject("role_id", newVOList.get("NEW_ROLE_ID"));
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			BigDecimal TTL_CUST_NO_LIMIT_UP = null;
			if (list2.size() > 0)
				TTL_CUST_NO_LIMIT_UP = new BigDecimal(ObjectUtils.toString(list2.get(0).get("TTL_CUST_NO_LIMIT_UP")));

			if (list2.size() == 0) {
				Check1 = true;
			} else if (CUR_CNT.add(new BigDecimal(1)).compareTo(TTL_CUST_NO_LIMIT_UP) > 0) {
				Check2 = true;
			}
		}

		// 如果該客戶有原理專且不同分行　sen issue 4131 20180111 離職理專空 code 認領
		Map<String, Object> custInfoMap = this.getCustInfo(inputVO);

		// mantis 4131 新舊同分行判別修正
		newAoBrh = inputVO.getNewVOList().get("NEW_BRA_NBR");
		if (custInfoMap != null) {
			if (!org.springframework.util.StringUtils.isEmpty(custInfoMap.get("AO_CODE")) && !newAoBrh.equals(custInfoMap.get("BRA_NBR"))) {
				if (inputVO.getFileName() == null) { // 必須要上傳申請書
					Check7 = true;
				}
			}
		}

		// 檢核該客戶是否在待覆核流程
		// 2017/12/18 jacky 2個檢查合為一個 SQL
		// 2020-8-17 十保客戶名單也需要排除讓客戶可以申請 TRS_TYPE = 'A', 預約生效中BS也要可以申請
		// 2023-11-14 調離職AOCode客戶名單也要可以申請
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(1) FROM TBCRM_TRS_AOCHG_PLIST WHERE 1=1 ");
		sql.append("AND CUST_ID = :cust_id ");
		sql.append("AND PROCESS_STATUS in ('L0', 'L1', 'L2', 'L3', 'L4', 'L5', 'BS') ");
		sql.append("AND TRS_TYPE NOT IN ('7', '8', '9', 'A', 'G') ");
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			BigDecimal COUNT = new BigDecimal(ObjectUtils.toString(list.get(0).get("COUNT(1)")));
			if (COUNT.compareTo(new BigDecimal(0)) > 0)
				Check3 = true;
		}
		
		// 重複申請
		if (!Check3) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT COUNT(1) FROM TBCRM_TRS_AOCHG_PLIST WHERE 1=1 ");
			sql.append("AND NEW_AO_CODE = :ao_code ");
			sql.append("AND CUST_ID = :cust_id ");
			sql.append("AND PROCESS_STATUS in ('L0', 'L1', 'L2', 'L3', 'L4', 'L5') ");
			queryCondition.setObject("ao_code", newVOList.get("NEW_AO_CODE"));
			queryCondition.setObject("cust_id", inputVO.getCust_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
			if (list3.size() > 0) {
				BigDecimal COUNT = new BigDecimal(ObjectUtils.toString(list3.get(0).get("COUNT(1)")));
				if (COUNT.compareTo(new BigDecimal(0)) > 0)
					Check3 = true;
			}
		}
		// 檢核禁移條件
		// FC移到FCH
		if (fcMap.containsKey(oldVOList.get("ROLE_ID")) && fchMap.containsKey(newVOList.get("NEW_ROLE_ID"))) {
			Check4 = true;
		}
		// 計績FCH移到維護FCH
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT A1.TYPE as OLD_TYPE, A2.TYPE as NEW_TYPE FROM TBORG_SALES_AOCODE A1, TBORG_SALES_AOCODE A2 ");
		sql.append("WHERE A1.AO_CODE = :ao_code ");
		sql.append("AND A2.AO_CODE = :ao_code2 ");
		queryCondition.setObject("ao_code", oldVOList.get("AO_CODE"));
		queryCondition.setObject("ao_code2", newVOList.get("NEW_AO_CODE"));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		if (list2.size() > 0) {
			String OLD_TYPE = ObjectUtils.toString(list2.get(0).get("OLD_TYPE"));
			String NEW_TYPE = ObjectUtils.toString(list2.get(0).get("NEW_TYPE"));
			if ("1".equals(OLD_TYPE) && fchMap.containsKey(oldVOList.get("ROLE_ID")) && "3".equals(NEW_TYPE) && fchMap.containsKey(newVOList.get("NEW_ROLE_ID"))) {
				Check5 = true;
			}
		}
		// 公司戶與公司負責人Code不同
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT M1.CUST_ID, M1.AO_CODE AS RPRS_AO_CODE, TRIM(M1.RPRS_ID) as RPRS_ID, M2.AO_CODE FROM TBCRM_CUST_MAST M1 ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST M2 ON TRIM(M1.RPRS_ID) = M2.CUST_ID WHERE 1=1 ");
		sql.append("AND LENGTH(M1.CUST_ID) < 10 AND M1.CUST_ID = :cust_id ");
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
		if (list3.size() != 0) {
			// 1.有公司, 2.原公司空CODE才檢核, 3.公司負責人非空CODE, 4.新CODE非空
			if (list3.get(0).get("RPRS_ID") != null && list3.get(0).get("RPRS_AO_CODE") == null && list3.get(0).get("AO_CODE") != null && newVOList.get("NEW_AO_CODE") != null) {
				if (!newVOList.get("NEW_AO_CODE").equals(ObjectUtils.toString(list3.get(0).get("AO_CODE"))))
					Check6 = true;
			}
		}

		//是否在ONCODE客戶排除列表中
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT 1 FROM TBCRM_TRS_CUST_EXCLUDE WHERE CUST_ID = :cust_id AND NVL(DEL_YN, 'N') = 'N' ");
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setQueryString(sql.toString());
		list3 = dam.exeQuery(queryCondition);
		if(CollectionUtils.isNotEmpty(list3)) {
			Check8 = true;
		}
		
		if (Check1)
			outputVO.setResultList2("ERR1");
		else if (Check3)
			outputVO.setResultList2("ERR3");
		else if (Check7) // 如果該客戶有原理專且不同分行 sen issue 4131 20180111  離職理專空 code 認領
			outputVO.setResultList2("ERR7");
		else if (Check8)
			outputVO.setResultList2("ERR8"); //在ONCODE客戶排除列表中
		else if (Check4)
			outputVO.setResultList2("ERR4");
		else if (Check5)
			outputVO.setResultList2("ERR5");
		else if (Check6)
			outputVO.setResultList2("ERR6");
		else if (Check2)
			outputVO.setResultList2("ERR2");
		else
			outputVO.setResultList2("GOOD");
		
		this.sendRtnObject(outputVO);
	}

	//上傳
	public void addconfirm(Object body, IPrimitiveMap header) throws Exception {
		CRM341InputVO inputVO = (CRM341InputVO) body;
		dam = this.getDataAccessManager();

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = new HashMap(xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2)); //理專
		Map<String, String> fchMap = new HashMap(xmlInfo.doGetVariable("FUBONSYS.FCH_ROLE", FormatHelper.FORMAT_2)); //FCH理專
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2); //個金主管
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2); //營運督導
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2); //區域中心主管
		// TRS_FLOW_TYPE, TRS_TYPE
		Map<String, String> oldVOList = inputVO.getOldVOList();
		Map<String, String> newVOList = inputVO.getNewVOList();
		String TRS_FLOW_TYPE = "";
		String TRS_TYPE = "";

		// 跨分行移轉 + 空CODE客戶 + 移轉至維護CODE : add by ocean CR:20190812085945-0001
		if (!StringUtils.equals(oldVOList.get("BRA_NBR"), newVOList.get("NEW_BRA_NBR")) && oldVOList.get("AO_CODE") == null && StringUtils.equals(newVOList.get("NEW_AO_TYPE"), "3")) {
			TRS_FLOW_TYPE = "4"; 	//4.跨分行: 分行主管->總行覆核生效
			TRS_TYPE = "3";			//3.跨分行移入申請
		}
		// 無code移轉
		else if (oldVOList.get("AO_CODE") == null) {
			TRS_FLOW_TYPE = "1"; 	//1.空Code: 分行主管
			TRS_TYPE = "1";			//1.空Code認養
		}
		// 同分行移轉
		else if (StringUtils.equals(oldVOList.get("BRA_NBR"), newVOList.get("NEW_BRA_NBR"))) {
			TRS_FLOW_TYPE = "2"; 	//2.同分行: 分行主管
			if(inputVO.isIs2023CMDTCust4()) {
				TRS_TYPE = "E";		//2023必輪調區域分行未帶走拔CODE後移回原理專
			} else {
				TRS_TYPE = "2";			//2.同分行移入申請
			}
		}
		// FCH→FCH 且 跨分行移轉
		else if (fchMap.containsKey(oldVOList.get("ROLE_ID")) && fchMap.containsKey(newVOList.get("NEW_ROLE_ID")) && !StringUtils.equals(oldVOList.get("BRA_NBR"), newVOList.get("NEW_BRA_NBR"))) {
			TRS_FLOW_TYPE = "3";	//3.FCH跨分行: 分行主管->原分行主管->總行覆核生效
			TRS_TYPE = "3"; 		//3.跨分行移入申請
		}
		// XX→FC 且 跨分行移轉
		else if (fcMap.containsKey(newVOList.get("NEW_ROLE_ID")) && !StringUtils.equals(oldVOList.get("BRA_NBR"), newVOList.get("NEW_BRA_NBR"))) {
			TRS_FLOW_TYPE = "4"; 	//4.跨分行: 分行主管->總行覆核生效
			TRS_TYPE = "3";			//3.跨分行移入申請
		}
		//
		BigDecimal SN = new BigDecimal(getSN("TBCRM_TRS_AOCHG_PLIST_SEQ"));
		// 理專
		if (fcMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || fchMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			// 2017/12/18 jacky
			this.cancelOther(inputVO.getCust_id());

			TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
			vo.setSEQ(SN);
			vo.setCUST_ID(inputVO.getCust_id());
			vo.setORG_AO_CODE(oldVOList.get("AO_CODE"));
			vo.setORG_AO_BRH(oldVOList.get("BRA_NBR"));
			vo.setNEW_AO_CODE(newVOList.get("NEW_AO_CODE"));
			vo.setNEW_AO_BRH(newVOList.get("NEW_BRA_NBR"));
			vo.setAPL_EMP_ID(ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINID)));
			vo.setAPL_EMP_ROLE(ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINROLE)));
			vo.setAPL_DATETIME(new Timestamp(System.currentTimeMillis()));
			vo.setAPL_REASON(inputVO.getApl_reason());
			vo.setAPL_OTH_REASON(inputVO.getApl_oth_reason());
			vo.setAGMT_SEQ(inputVO.getAgmt_seq());
			// file
			if (StringUtils.isNotBlank(inputVO.getFileName())) {
				String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
				Path path = Paths.get(joinedPath);
				byte[] data = Files.readAllBytes(path);
				vo.setAGMT_FILE(ObjectUtil.byteArrToBlob(data));
				vo.setAGMT_FILE_TYPE(FilenameUtils.getExtension(inputVO.getFileName()));
			}
			vo.setTRS_FLOW_TYPE(TRS_FLOW_TYPE);
			vo.setTRS_TYPE(TRS_TYPE);
			vo.setPROCESS_STATUS("L1");
			vo.setTRS_TXN_SOURCE("2");
			dam.create(vo);
			
			if(inputVO.isCMDTCust() || inputVO.isIs2022CMDTCust3() || inputVO.isIs2023CMDTCust4()){
				TBCRM_10CMDT_CHGAO_FILEVO fileVO = new TBCRM_10CMDT_CHGAO_FILEVO();
				fileVO.setSEQ(new BigDecimal(this.getSN("TBCRM_10CMDT_CHGAO_FILE_SEQ")));
				fileVO.setPLIST_SEQ(SN);
				String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName2()).toString();
				Path path = Paths.get(joinedPath);
				byte[] data = Files.readAllBytes(path);
				fileVO.setCMDT_FILE(ObjectUtil.byteArrToBlob(data));
				fileVO.setCMDT_FILE_TYPE(FilenameUtils.getExtension(inputVO.getFileName2()));
				dam.create(fileVO);
			}			
		}
		// 主管
		else if (bmmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			// 2017/12/18 jacky
			this.cancelOther(inputVO.getCust_id());

			TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
			vo.setSEQ(SN);
			vo.setCUST_ID(inputVO.getCust_id());
			vo.setORG_AO_CODE(oldVOList.get("AO_CODE"));
			vo.setORG_AO_BRH(oldVOList.get("BRA_NBR"));
			vo.setNEW_AO_CODE(newVOList.get("NEW_AO_CODE"));
			vo.setNEW_AO_BRH(newVOList.get("NEW_BRA_NBR"));
			vo.setAPL_EMP_ID(ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINID)));
			vo.setAPL_EMP_ROLE(ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINROLE)));
			vo.setAPL_DATETIME(new Timestamp(System.currentTimeMillis()));
			vo.setAPL_REASON(inputVO.getApl_reason());
			vo.setAPL_OTH_REASON(inputVO.getApl_oth_reason());
			vo.setAGMT_SEQ(inputVO.getAgmt_seq());
			// file
			if (StringUtils.isNotBlank(inputVO.getFileName())) {
				String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
				Path path = Paths.get(joinedPath);
				byte[] data = Files.readAllBytes(path);
				vo.setAGMT_FILE(ObjectUtil.byteArrToBlob(data));
				vo.setAGMT_FILE_TYPE(FilenameUtils.getExtension(inputVO.getFileName()));
			}
			vo.setTRS_FLOW_TYPE(TRS_FLOW_TYPE);
			vo.setTRS_TYPE(TRS_TYPE);
			vo.setPROCESS_STATUS("L1");
			vo.setTRS_TXN_SOURCE("2");
			dam.create(vo);
		}
		// 區督導
		else if (mbrmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			// 2017/12/18 jacky
			this.cancelOther(inputVO.getCust_id());

			TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
			vo.setSEQ(SN);
			vo.setCUST_ID(inputVO.getCust_id());
			vo.setORG_AO_CODE(oldVOList.get("AO_CODE"));
			vo.setORG_AO_BRH(oldVOList.get("BRA_NBR"));
			vo.setNEW_AO_CODE(newVOList.get("NEW_AO_CODE"));
			vo.setNEW_AO_BRH(newVOList.get("NEW_BRA_NBR"));
			vo.setAPL_EMP_ID(ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINID)));
			vo.setAPL_EMP_ROLE(ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINROLE)));
			vo.setAPL_DATETIME(new Timestamp(System.currentTimeMillis()));
			vo.setAPL_REASON(inputVO.getApl_reason());
			vo.setAPL_OTH_REASON(inputVO.getApl_oth_reason());
			vo.setAGMT_SEQ(inputVO.getAgmt_seq());
			// file
			if (StringUtils.isNotBlank(inputVO.getFileName())) {
				String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
				Path path = Paths.get(joinedPath);
				byte[] data = Files.readAllBytes(path);
				vo.setAGMT_FILE(ObjectUtil.byteArrToBlob(data));
				vo.setAGMT_FILE_TYPE(FilenameUtils.getExtension(inputVO.getFileName()));
			}
			vo.setTRS_FLOW_TYPE(TRS_FLOW_TYPE);
			vo.setTRS_TYPE(TRS_TYPE);
			if ("1".equals(TRS_FLOW_TYPE) || "2".equals(TRS_FLOW_TYPE))
				vo.setPROCESS_STATUS("L2");
			else
				vo.setPROCESS_STATUS("L1");
			vo.setTRS_TXN_SOURCE("2");
			dam.create(vo);
		}
		// 處主管
		else if (armgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			// 2017/12/18 jacky
			this.cancelOther(inputVO.getCust_id());

			TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
			vo.setSEQ(SN);
			vo.setCUST_ID(inputVO.getCust_id());
			vo.setORG_AO_CODE(oldVOList.get("AO_CODE"));
			vo.setORG_AO_BRH(oldVOList.get("BRA_NBR"));
			vo.setNEW_AO_CODE(newVOList.get("NEW_AO_CODE"));
			vo.setNEW_AO_BRH(newVOList.get("NEW_BRA_NBR"));
			vo.setAPL_EMP_ID(ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINID)));
			vo.setAPL_EMP_ROLE(ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINROLE)));
			vo.setAPL_DATETIME(new Timestamp(System.currentTimeMillis()));
			vo.setAPL_REASON(inputVO.getApl_reason());
			vo.setAPL_OTH_REASON(inputVO.getApl_oth_reason());
			vo.setAGMT_SEQ(inputVO.getAgmt_seq());
			// file
			if (StringUtils.isNotBlank(inputVO.getFileName())) {
				String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
				Path path = Paths.get(joinedPath);
				byte[] data = Files.readAllBytes(path);
				vo.setAGMT_FILE(ObjectUtil.byteArrToBlob(data));
				vo.setAGMT_FILE_TYPE(FilenameUtils.getExtension(inputVO.getFileName()));
			}
			vo.setTRS_FLOW_TYPE(TRS_FLOW_TYPE);
			vo.setTRS_TYPE(TRS_TYPE);
			if ("1".equals(TRS_FLOW_TYPE) || "2".equals(TRS_FLOW_TYPE))
				vo.setPROCESS_STATUS("L3");
			else
				vo.setPROCESS_STATUS("L1");
			vo.setTRS_TXN_SOURCE("2");
			dam.create(vo);
		} else
			throw new APException("使用者登入身分不適用");

		this.sendRtnObject(null);
	}

	private void cancelOther(String cust_id) throws JBranchException {
		// 2017/12/18 jacky 作廢
		QueryConditionIF queryCondition_close = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql_close = new StringBuffer();
		sql_close.append("UPDATE TBCRM_TRS_AOCHG_PLIST SET MODIFIER = :emp_id, LASTUPDATE = sysdate, PROCESS_STATUS = 'F' ");
		sql_close.append("WHERE CUST_ID = :cust_id ");
		sql_close.append("AND PROCESS_STATUS in ('L1', 'L2', 'L3', 'L4', 'L5') ");
		sql_close.append("AND TRS_TYPE in ('7', '8', '9') ");
		queryCondition_close.setQueryString(sql_close.toString());
		queryCondition_close.setObject("emp_id", getUserVariable(FubonSystemVariableConsts.LOGINID));
		queryCondition_close.setObject("cust_id", cust_id);
		dam.exeUpdate(queryCondition_close);
	}

	private String getSN(String sn_name) throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT "+sn_name+".nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);

		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}

	private String getAPLSeq() throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TBCRM_TRS_APL_SEQ_DTL_SEQ.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);

		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}

	private Boolean checkID(String pk) throws JBranchException {
		Boolean ans = false;
		TBCRM_TRS_APL_SEQ_DTLVO vo = new TBCRM_TRS_APL_SEQ_DTLVO();
		vo = (TBCRM_TRS_APL_SEQ_DTLVO) dam.findByPKey(TBCRM_TRS_APL_SEQ_DTLVO.TABLE_UID, pk);
		if (vo != null)
			ans = true;
		else
			ans = false;
		return ans;
	}

	private String getString(Object val) {
		if (val == null) {
			return "";
		} else {
			return val.toString();
		}
	}

	/**
	 * 查詢客戶資料 sen issue 4131 20180111 離職理專空 code 認領
	 */
	private Map<String, Object> getCustInfo(CRM341InputVO inputVO) throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT CUST_ID, OPEN_BRA_NBR, BRA_NBR, AO_CODE FROM TBCRM_CUST_MAST WHERE 1=1 ");
		sql.append("AND CUST_ID = :cust_id ");
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * 移入申請檢查是否為十保監控理專客戶名單
	 * @author Jacky Wu
	 */
	public void check10CMDTCust(Object body, IPrimitiveMap header) throws JBranchException {
		CRM341InputVO inputVO = (CRM341InputVO) body;
		CRM341OutputVO outputVO = new CRM341OutputVO();
		
		//2021 十保客戶移轉類別
		outputVO.setCMDTCust(this.check10CMDTCust(inputVO.getCust_id(), inputVO.getNew_ao_code()));
		
		this.sendRtnObject(outputVO);
	}
	
	public boolean check10CMDTCust(String custID,String newAoCode) throws JBranchException {
		boolean isCMDTCust = false;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
//		sql.append("SELECT CUST_ID FROM TBCRM_10CMDT_CUST_CHGAO_LIST WHERE CUST_ID = :custID ");
//		sql.append("AND OLD_EMP_ID = (SELECT EMP_ID FROM VWORG_AO_INFO WHERE AO_CODE = :newAoCode) "); //找出移入者為新理專
//		sql.append("AND CHG_STATUS IN ('AS','AO') AND EFF_DATE >= ADD_MONTHS(TRUNC(SYSDATE),-6)");//找出6個月內已被指派
		
		sql.append("SELECT CUST_ID FROM TBCRM_TRS_AOCHG_PLIST WHERE CUST_ID = :custID ");
		sql.append("AND ORG_AO_CODE IN (SELECT AO_CODE FROM VWORG_AO_INFO WHERE EMP_ID = (SELECT EMP_ID FROM VWORG_AO_INFO WHERE AO_CODE = :newAoCode)) "); //找出移入者為新理專
		sql.append("AND TRS_TYPE = 'A' "); //2021 十保客戶移轉類別
		sql.append("AND PROCESS_STATUS IN ('BS','S') AND ACT_DATE >= ADD_MONTHS(TRUNC(SYSDATE),-6) ");//找出6個月內已被指派
		
		queryCondition.setObject("custID", custID);
		queryCondition.setObject("newAoCode", newAoCode);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		if (!list.isEmpty()) {
			isCMDTCust = true; //不可移入此客戶ID
		}
		
		return isCMDTCust;
	}

	/***
	 * 2022必輪調名單：
	 * RM輪調後，帶走30%核心客戶，一年內不得再帶走該RM轄下原分行70%客戶
	 * @param custID
	 * @param newAoCode
	 * @return "Y"：非核心客戶不可移回原理專
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private String check2022CMDTCust1(String custID,String newAoCode) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		//2022必輪調名單：RM輪調後，帶走30%核心客戶，一年內不得再帶走該RM轄下原分行70%客戶
		sql.append("SELECT A.CUST_ID ");
		sql.append(" FROM TBCRM_TRS_AOCHG_PLIST A ");
		sql.append(" INNER JOIN TBSYSPARAMETER P ON P.PARAM_TYPE = 'CRM.2022CMDT_PRJ_NAME' AND P.PARAM_CODE = '2' "); //非核心客戶
		sql.append(" INNER JOIN TBCRM_TRS_PRJ_MAST B ON B.PRJ_ID = A.PRJ_ID AND PRJ_NAME = P.PARAM_NAME ");
		sql.append(" WHERE A.CUST_ID = :custID ");
		sql.append("   AND A.ORG_AO_CODE IN (SELECT AO_CODE FROM VWORG_AO_INFO WHERE EMP_ID = (SELECT EMP_ID FROM VWORG_AO_INFO WHERE AO_CODE = :newAoCode)) "); 
		sql.append("   AND A.PROCESS_STATUS IN ('BS','S') AND TRUNC(A.ACT_DATE) >= ADD_MONTHS(TRUNC(SYSDATE), -12) ");
		
		queryCondition.setObject("custID", custID);
		queryCondition.setObject("newAoCode", newAoCode);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		return CollectionUtils.isNotEmpty(list) ? "Y" : "N"; //Y:非核心客戶不可移回原理專
	}
	
	/***
	 * 2022必輪調名單：
	 * 輪調後帶至新分行之核心客戶，依客戶意願欲轉回原分行(限RM移入功能)，經由一階主管同意，再經由處主管同意後生效，客戶即可轉回原分行
	 * (控一年)
	 * @param custID
	 * @param newBranch
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private String check2022CMDTCust2(String custID,String newBranch) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		//2022必輪調名單：輪調後帶至新分行之核心客戶，依客戶意願欲轉回原分行
		sql.append("SELECT A.CUST_ID ");
		sql.append(" FROM TBCRM_TRS_AOCHG_PLIST A ");
		sql.append(" INNER JOIN TBSYSPARAMETER P ON P.PARAM_TYPE = 'CRM.2022CMDT_PRJ_NAME' AND P.PARAM_CODE = '1' "); //核心客戶
		sql.append(" INNER JOIN TBCRM_TRS_PRJ_MAST B ON B.PRJ_ID = A.PRJ_ID AND B.PRJ_NAME = P.PARAM_NAME ");
		sql.append(" WHERE A.CUST_ID = :custID ");
		sql.append("   AND A.ORG_AO_BRH <> A.NEW_AO_BRH "); 
		sql.append("   AND A.ORG_AO_BRH = :newBranch "); 
		sql.append("   AND A.PROCESS_STATUS IN ('BS','S') AND TRUNC(A.ACT_DATE) >= ADD_MONTHS(TRUNC(SYSDATE), -12) ");
		
		queryCondition.setObject("custID", custID);
		queryCondition.setObject("newBranch", newBranch);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		return CollectionUtils.isNotEmpty(list) ? "Y" : "N"; //Y:輪調後帶至新分行之核心客戶
	}
	
	/***
	 * 2022換手名單：
	 * 是否為6個月內要移回原理專的客戶
	 * 若要移回原理專，需請客戶簽署「客戶資產現況表申請書」及「客戶指定個金客戶經理自主聲明書」
	 * @param custID
	 * @param newAoCode
	 * @return "Y"：換手名單6個月內要移回原理專的客戶
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public String check2022CMDTCust3(String custID,String newAoCode) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		//2022換手名單：是否為6個月內要移回原理專的客戶
		sql.append("SELECT A.CUST_ID ");
		sql.append(" FROM TBPMS_ROTATION_MAIN A ");
		sql.append(" INNER JOIN TBCRM_TRS_AOCHG_PLIST P ON P.CUST_ID = A.CUST_ID AND P.TRS_TYPE = 'C' AND P.PROCESS_STATUS = 'S' "); //主管放行後6個月內不可移回原理專
		sql.append(" INNER JOIN TBPMS_ROTATION_5YCUST Y ON Y.PRJ_ID = A.PRJ_ID AND Y.CUST_ID = A.CUST_ID "); 
		sql.append(" WHERE A.CUST_ID = :custID ");
		sql.append("   AND Y.EMP_ID = (SELECT EMP_ID FROM VWORG_AO_INFO WHERE AO_CODE = :newAoCode) "); //原理專員編
		sql.append("   AND TRUNC(SYSDATE) <= TRUNC(ADD_MONTHS(P.ACT_DATE, 6)) ");
		
		queryCondition.setObject("custID", custID);
		queryCondition.setObject("newAoCode", newAoCode);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		return CollectionUtils.isNotEmpty(list) ? "Y" : "N"; //Y:換手名單6個月內要移回原理專的客戶
	}
	
	//2023必輪調名單：必輪調RM名單上傳後，名單中RM不可做移入申請
	private String check2023CMDTCust1() throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT 1 ");
		sql.append(" FROM TBCRM_TRS_PRJ_ROTATION_M A ");
		sql.append(" INNER JOIN TBCRM_TRS_PRJ_MAST B ON B.PRJ_ID = A.PRJ_ID "); 
		sql.append(" WHERE A.EMP_ID = :empId AND A.IMP_SUCCESS_YN = 'Y' AND B.PRJ_TYPE = '1' "); //必輪調有匯入成功的理專
		sql.append("   AND TRUNC(SYSDATE) < TRUNC(B.PRJ_EXE_DATE) "); //名單尚未執行
		
		queryCondition.setObject("empId", ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINID)));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		return CollectionUtils.isNotEmpty(list) ? "Y" : "N"; //Y:必輪調名單中RM，不可做移入申請
	}
	
	//2023必輪調名單：必輪調RM名單上傳後，名單中客戶不可做移入申請
	public String check2023CMDTCust2(String custId) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT 1 ");
		sql.append(" FROM TBCRM_TRS_PRJ_ROTATION_D A ");
		sql.append(" INNER JOIN TBCRM_TRS_PRJ_MAST B ON B.PRJ_ID = A.PRJ_ID "); 
		sql.append(" WHERE A.CUST_ID = :custId AND B.PRJ_TYPE = '1' ");	 //必輪調客戶
		sql.append("   AND TRUNC(SYSDATE) < TRUNC(B.PRJ_EXE_DATE) "); //名單尚未執行
		
		queryCondition.setObject("custId", custId);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		return CollectionUtils.isNotEmpty(list) ? "Y" : "N"; //Y:必輪調名單中客戶，不可做移入申請
	}
	
	/***
	 * 2023必輪調名單(非區域分行)：
	 * RM輪調後，帶走30%核心客戶，一年內不得再帶走該RM轄下原分行70%客戶
	 * @param custID
	 * @param newAoCode
	 * @return "Y"：非核心客戶不可移回原理專
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public String check2023CMDTCust3(String custID, String newAoCode) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		//2023必輪調名單：RM輪調後，帶走30%核心客戶，一年內不得再帶走該RM轄下原分行70%客戶
		sql.append("SELECT 1 ");
		sql.append(" FROM TBCRM_TRS_PRJ_ROTATION_D A ");
		sql.append(" INNER JOIN TBCRM_TRS_PRJ_ROTATION_M B ON B.PRJ_ID = A.PRJ_ID AND B.BRANCH_NBR = A.BRANCH_NBR AND B.EMP_ID = A.EMP_ID "); 
		sql.append(" INNER JOIN TBCRM_TRS_PRJ_MAST C ON C.PRJ_ID = A.PRJ_ID "); 
		sql.append(" WHERE C.PRJ_TYPE = '1' "); //必輪調專案
		sql.append("   AND B.REGION_BRANCH_YN = 'N' "); //非區域分行
		sql.append("   AND A.CUST_ID = :custID AND A.EMP_ID = (SELECT EMP_ID FROM VWORG_AO_INFO WHERE AO_CODE = :newAoCode) "); 
//		sql.append("   AND NVL(A.GO_CUST_YN, 'N') = 'N' "); //沒有帶走的客戶
		sql.append("   AND A.STATUS = '7' "); //名單狀態為'7'：輪調已執行_未帶走客戶
		sql.append("   AND TRUNC(C.PRJ_EXE_DATE) >= ADD_MONTHS(TRUNC(SYSDATE), -12) "); //輪調日一年內
		
		queryCondition.setObject("custID", custID);
		queryCondition.setObject("newAoCode", newAoCode);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		return CollectionUtils.isNotEmpty(list) ? "Y" : "N"; //Y:非核心客戶不可移回原理專
	}	
	
	/***
	 * 2023必輪調名單(區域分行)：
	 * RM輪調後，未帶走70%客戶，一年內若因客戶意願欲轉回原RM服務，經由一階主管同意，再經由處主管同意後生效，客戶即可轉回原RM
	 * @param custID
	 * @param newAoCode
	 * @return "Y"：區域分行非核心客戶移回原理專
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public String check2023CMDTCust4(String custID, String newAoCode) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		//2023必輪調名單：區域分行一年內若因客戶意願欲轉回原RM服務
		sql.append("SELECT 1 ");
		sql.append(" FROM TBCRM_TRS_PRJ_ROTATION_D A ");
		sql.append(" INNER JOIN TBCRM_TRS_PRJ_ROTATION_M B ON B.PRJ_ID = A.PRJ_ID AND B.BRANCH_NBR = A.BRANCH_NBR AND B.EMP_ID = A.EMP_ID "); 
		sql.append(" INNER JOIN TBCRM_TRS_PRJ_MAST C ON C.PRJ_ID = A.PRJ_ID "); 
		sql.append(" WHERE C.PRJ_TYPE = '1' "); //必輪調專案
		sql.append("   AND B.REGION_BRANCH_YN = 'Y' "); //區域分行
		sql.append("   AND A.CUST_ID = :custID AND A.EMP_ID = (SELECT EMP_ID FROM VWORG_AO_INFO WHERE AO_CODE = :newAoCode) "); 
//		sql.append("   AND NVL(A.GO_CUST_YN, 'N') = 'N' "); //沒有帶走的客戶
		sql.append("   AND A.STATUS = '7' "); //名單狀態為'7'：輪調已執行_未帶走客戶
		sql.append("   AND TRUNC(C.PRJ_EXE_DATE) >= ADD_MONTHS(TRUNC(SYSDATE), -12) "); //輪調日一年內
		
		queryCondition.setObject("custID", custID);
		queryCondition.setObject("newAoCode", newAoCode);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		return CollectionUtils.isNotEmpty(list) ? "Y" : "N"; //Y:區域分行非核心客戶移回原理專
	}
	
	/***
	 * 2023必輪調名單：
	 * 非區域分行，輪調後帶至新分行之核心客戶，依客戶意願欲轉回原分行(限RM移入功能)，經由一階主管同意，再經由處主管同意後生效，客戶即可轉回原分行
	 * (控一年)
	 * @param custID
	 * @param newBranch
	 * @return Y:輪調後帶至新分行之核心客戶
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public String check2023CMDTCust5(String custID, String newBranch) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		//2023必輪調名單：輪調後帶至新分行之核心客戶，依客戶意願欲轉回原分行
		sql.append("SELECT 1 ");
		sql.append(" FROM TBCRM_TRS_PRJ_ROTATION_D A ");
		sql.append(" INNER JOIN TBCRM_TRS_PRJ_ROTATION_M B ON B.PRJ_ID = A.PRJ_ID AND B.BRANCH_NBR = A.BRANCH_NBR AND B.EMP_ID = A.EMP_ID "); 
		sql.append(" INNER JOIN TBCRM_TRS_PRJ_MAST C ON C.PRJ_ID = A.PRJ_ID "); 
		sql.append(" WHERE C.PRJ_TYPE = '1' "); //必輪調專案
		sql.append("   AND A.CUST_ID = :custID "); 
		sql.append("   AND B.REGION_BRANCH_YN = 'N' AND A.BRANCH_NBR = :newBranch "); //非區域分行移回原分行
		sql.append("   AND NVL(A.GO_CUST_YN, 'N') = 'Y' "); //帶走的客戶
		sql.append("   AND A.STATUS = '6' "); //名單狀態為'6'：輪調已執行_帶走的客戶
		sql.append("   AND TRUNC(C.PRJ_EXE_DATE) >= ADD_MONTHS(TRUNC(SYSDATE), -12) "); //輪調日一年內
		
		queryCondition.setObject("custID", custID);
		queryCondition.setObject("newBranch", newBranch);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		return CollectionUtils.isNotEmpty(list) ? "Y" : "N"; //Y:輪調後帶至新分行之核心客戶
	}
	
	public boolean check10CMDTCust2(String custID,String newAoCode) throws JBranchException {
		boolean isCMDTCust = false;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
//		sql.append("SELECT CUST_ID FROM TBCRM_10CMDT_CUST_CHGAO_LIST WHERE CUST_ID = :custID ");
//		sql.append("AND OLD_EMP_ID = (SELECT EMP_ID FROM VWORG_AO_INFO WHERE AO_CODE = :newAoCode) "); //找出移入者為新理專
//		sql.append("AND CHG_STATUS IN ('AS','AO') AND EFF_DATE >= ADD_MONTHS(TRUNC(SYSDATE),-6)");//找出6個月內已被指派
		
		sql.append("SELECT CUST_ID FROM TBCRM_TRS_AOCHG_PLIST WHERE CUST_ID = :custID ");
//		sql.append("AND ORG_AO_CODE IN (SELECT AO_CODE FROM VWORG_AO_INFO WHERE EMP_ID = (SELECT EMP_ID FROM VWORG_AO_INFO WHERE AO_CODE = :newAoCode)) "); //十保名單分派中就不要鎖原AOCode或新AOCode.
		sql.append("AND TRS_TYPE = 'A' "); //十保客戶移轉類別
		sql.append("AND PROCESS_STATUS NOT IN ('BS','S') ");//找出6個月內已被指派
		
		queryCondition.setObject("custID", custID);
//		queryCondition.setObject("newAoCode", newAoCode);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		if (!list.isEmpty()) {
			isCMDTCust = true; //不可移入此客戶ID
		}
		
		return isCMDTCust;
	}
	
	public boolean check10CMDTCustAgree(BigDecimal plistSeq)  throws JBranchException {
		boolean isCustAgree = false;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT CUST_ID FROM TBCRM_TRS_AOCHG_PLIST WHERE SEQ = :seq AND AGMT_FILE_TYPE is not null");
		queryCondition.setObject("seq", plistSeq);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		if (!list.isEmpty()) { //第一份同意書
			isCustAgree = true; //可移入此客戶ID
		}
		
		if(isCustAgree){
			sql = new StringBuffer();
			sql.append("SELECT SEQ FROM TBCRM_10CMDT_CHGAO_FILE WHERE PLIST_SEQ = :seq AND CMDT_FILE_TYPE is not null");
			queryCondition.setObject("seq", plistSeq);
			queryCondition.setQueryString(sql.toString());
			list = dam.exeQuery(queryCondition);
			if (list.isEmpty()) { //第二份同意書沒上傳
				isCustAgree = false; //不可移入此客戶ID
			}
		}
		return isCustAgree;
	}
}