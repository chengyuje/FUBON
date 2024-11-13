package com.systex.jbranch.app.server.fps.crm230;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.birt.report.model.api.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_GROUPPK;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_GROUPVO;
import com.systex.jbranch.app.server.fps.crm231.CRM231InputVO;
import com.systex.jbranch.app.server.fps.crm2310.CRM2310InputVO;
import com.systex.jbranch.app.server.fps.crm2311.CRM2311InputVO;
import com.systex.jbranch.app.server.fps.crm232.CRM232InputVO;
import com.systex.jbranch.app.server.fps.crm233.CRM233InputVO;
import com.systex.jbranch.app.server.fps.crm234.CRM234InputVO;
import com.systex.jbranch.app.server.fps.crm235.CRM235InputVO;
import com.systex.jbranch.app.server.fps.crm236.CRM236InputVO;
import com.systex.jbranch.app.server.fps.crm237.CRM237InputVO;
import com.systex.jbranch.app.server.fps.crm238.CRM238InputVO;
import com.systex.jbranch.app.server.fps.crm239.CRM239InputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.CAM996;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author moron, walalala
 * @date 2016/11/02
 * @spec null
 */
@Component("crm230")
@Scope("request")
public class CRM230 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM230.class);
	private static final String OUT_SIDE_WHERE = "#outSideWhere";
	private static final String OUT_SIDE_TABLE = "#outSideTable";
	private static final String OUT_SIDE_COLUMN = "#outSideColumn";
	private static final String OUT_SIDE_COLUMN_END = "#outSideColumnEnd";
	private static final String IN_COLUMN = "#inColumn";
	private static final String IN_TABLE = "#inTable";
	private static final String IN_WHERE = "#inWhere";

	private enum SqlGen {
		IN_COLUMN, IN_TABLE, IN_WHERE, OUT_SIDE_COLUMN, OUT_SIDE_TABLE, OUT_SIDE_WHERE,
	}

	public void getAOCode(Object body, IPrimitiveMap header) throws JBranchException {
		CRM230OutputVO return_VO = new CRM230OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT AO_CODE, EMP_NAME FROM VWORG_AO_INFO WHERE BRA_NBR ");
		sql.append("IN ( :branchlist ) ");

		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("branchlist", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));

		// result
		List<Map<String, Object>> list = dam.exeQueryWithoutSort(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
//	public void getRM(Object body, IPrimitiveMap header) throws JBranchException {
//		CRM230OutputVO return_VO = new CRM230OutputVO();
//		dam = this.getDataAccessManager();
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuilder sql = new StringBuilder();
//		
//		sql.append(" SELECT EMP_ID || '_' || EMP_NAME AS LABEL, EMP_ID AS DATA, BRANCH_NBR ");
//		sql.append(" FROM ( ");
//		sql.append(" SELECT DISTINCT EMP_ID, EMP_NAME, BRANCH_NBR FROM VWORG_EMP_INFO ");
//		sql.append(" WHERE AO_CODE IS NOT NULL ");
//		sql.append(" AND BRANCH_NBR IS NOT NULL ");
//		
//		if (!"ALL".equals(getCommonVariable(FubonSystemVariableConsts.LOGINROLE).toString()) && 
//			((List<String>) getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST)).size() > 0) {  
//			// 判斷非總行人員再加條件
//			sql.append("AND BRANCH_NBR IN (:brNbrList) ");
//			queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
//		}
//		
//		sql.append(" ) ORDER BY EMP_ID ");
//		queryCondition.setQueryString(sql.toString());
//		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
//		return_VO.setResultList(list);
//		this.sendRtnObject(return_VO);
//	}

	public CRM230OutputVO inquire_common(Object body, String type) throws JBranchException {
		
		CRM230OutputVO return_VO = new CRM230OutputVO();
		CRM230_ALLInputVO inputVO_all = (CRM230_ALLInputVO) body;
		QueryConditionIF queryCondition = genDefaultQueryConditionIF();
		dam = getDataAccessManager();

		//query str
		String qryCol = "SELECT A.* ";
		String qryCnt = "SELECT count(1) as COUNT ";
		String sql = "";
		String qryContentStr = genCustFilterSql(type, queryCondition, inputVO_all).toString();
		Integer _QRY_MAX_RESULTS = (Integer) SysInfo.getInfoValue(FubonSystemVariableConsts.QRY_MAX_RESULTS);
		// query total row

		/**
		 * 2017/01/19 by Oliver 因目前前端的客戶篩選畫面並沒有show最終查詢總筆數，暫時先用限制最大筆數控制SELECT
		 * COUNT花費時間過長的問題 如未來仍需要最終查詢筆數，可嘗試用 reorg or restate的方式試試看
		 */
		sql = qryCnt + qryContentStr.replaceFirst("^.*" + OUT_SIDE_COLUMN_END, "") + " AND ROWNUM  BETWEEN 1 AND " + _QRY_MAX_RESULTS;
		queryCondition.setQueryString(sql);

		List<Map<String, BigDecimal>> tolist = exeQueryWithoutSortForQcf(queryCondition);
		return_VO.setTotalCntRecord(tolist.get(0).get("COUNT"));

		sql = qryCol + qryContentStr.replaceFirst(OUT_SIDE_COLUMN_END, "");
		queryCondition.setQueryString("SELECT * FROM(" + sql + ")");

		queryCondition.setMaxResults(_QRY_MAX_RESULTS);
		List list = exeQueryForQcf(queryCondition);
		return_VO.setResultList(list);

		return return_VO;
	}

	public Map<SqlGen, StringBuilder> createSqlModelMap() {
		Map<SqlGen, StringBuilder> sqlGenMap = new Hashtable<SqlGen, StringBuilder>();
		sqlGenMap.put(SqlGen.IN_COLUMN, new StringBuilder());
		sqlGenMap.put(SqlGen.IN_TABLE, new StringBuilder());
		sqlGenMap.put(SqlGen.IN_WHERE, new StringBuilder("WHERE 1 = 1 "));
		sqlGenMap.put(SqlGen.OUT_SIDE_COLUMN, new StringBuilder());
		sqlGenMap.put(SqlGen.OUT_SIDE_TABLE, new StringBuilder());
		sqlGenMap.put(SqlGen.OUT_SIDE_WHERE, new StringBuilder("WHERE 1 = 1 "));
		return sqlGenMap;
	}

	public void getGroupList(Object body, IPrimitiveMap header) throws JBranchException {
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);	//理專
		Map<String, String> fchMap = xmlInfo.doGetVariable("FUBONSYS.FCH_ROLE", FormatHelper.FORMAT_2);	//FCH理專
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		
		CRM230InputVO inputVO = (CRM230InputVO) body;
		CRM230OutputVO return_VO = new CRM230OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT A.GROUP_ID, A.GROUP_NAME ");
		sql.append("FROM TBCRM_CUST_AO_DEF_GROUP A ");
		sql.append("WHERE 1 = 1 ");

		String[] ao_list = inputVO.getAo_code().split(","); //一個理專可能有多個AO_CODE
		
		if (fcMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
			fchMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND A.AO_CODE IN (:ao_list) ");
			sql.append("AND EXISTS (SELECT T.AO_CODE FROM TBORG_SALES_AOCODE T WHERE T.TYPE <= 4 AND T.AO_CODE = A.AO_CODE)");
			queryCondition.setObject("ao_list", ao_list);
		} else if (uhrmMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND A.AO_CODE IN (:ao_list) ");
			sql.append("AND EXISTS (SELECT T.AO_CODE FROM TBORG_SALES_AOCODE T WHERE T.TYPE = 5 AND T.AO_CODE = A.AO_CODE)");
			queryCondition.setObject("ao_list", ao_list);
		} else if (uhrmMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND AO_CODE IN (SELECT AO_CODE FROM TBORG_SALES_AOCODE WHERE TYPE = '5')  ");
		} else {
			sql.append("AND AO_CODE IN (SELECT AO_CODE FROM VWORG_AO_INFO WHERE BRA_NBR IN ( :branchlist )) ");
			queryCondition.setObject("branchlist", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}

		queryCondition.setQueryString(sql.toString());
		
		return_VO.setResultList(dam.exeQueryWithoutSort(queryCondition));
		
		this.sendRtnObject(return_VO);
	}

	// === 查詢登入者的PRIVILEGEID
	private String getPrivilegeID() throws JBranchException {

		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT PRIVILEGEID ");
		sb.append("FROM TBSYSSECUROLPRIASS ");
		sb.append("WHERE ROLEID = :loginRoleID ");

		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("loginRoleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));

		List<Map<String, Object>> tempList = dam.exeQueryWithoutSort(queryCondition);
		if (tempList.size() > 0) {
			return (String) tempList.get(0).get("PRIVILEGEID");
		}
		return null;
	}

	//查詢GROUP
	public void group_check(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM230InputVO inputVO = (CRM230InputVO) body;
		CRM230OutputVO return_VO = new CRM230OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		///=============================================================//
		logger.info("CRM230_debug_time!!!!");
		List<Map<String, String>> groupList = inputVO.getGrouplist();
		for (int i = 0; i < groupList.size(); i++) {
			Map<String, String> map = groupList.get(i);
			for (String key : map.keySet()) {
				logger.info(key + " : " + map.get(key));
			}
		}
		//=============================================================//
		
		String priID = getPrivilegeID();
		
		sb.append(" SELECT * FROM TBCRM_CUST_AO_DEF_GROUP ");
		sb.append(" WHERE GROUP_ID = :group_id ");

		if (priID.startsWith("UHRM")) {
			for (Map<String, String> data : inputVO.getGrouplist()) {
				logger.info("CRM230_進入Map<String, String> data : inputVO.getGrouplist()迴圈");
				sb.append(" AND AO_CODE = (SELECT AO_CODE FROM TBORG_SALES_AOCODE WHERE TYPE = '5' AND EMP_ID = :uEmpID) ");
				queryCondition.setObject("uEmpID", data.get("UEMP_ID"));
			}
		} else {
			for (Map<String, String> data : inputVO.getGrouplist()) {
				logger.info("CRM230_進入Map<String, String> data : inputVO.getGrouplist()迴圈");
				sb.append(" AND AO_CODE = :ao_code ");
				queryCondition.setObject("ao_code", data.get("AO_CODE"));
			}
		}


		queryCondition.setObject("group_id", inputVO.getGroup());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQueryWithoutSort(queryCondition);
		logger.info("CRM230_執行完dam");
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}

	//加入GROUP
	public void group_join(Object body, IPrimitiveMap header) throws JBranchException {
		CRM230InputVO inputVO = (CRM230InputVO) body;
		dam = this.getDataAccessManager();

		for (Map<String, String> data : inputVO.getGrouplist()) {
			TBCRM_CUST_GROUPVO vo = new TBCRM_CUST_GROUPVO();
			TBCRM_CUST_GROUPPK pk = new TBCRM_CUST_GROUPPK();
			pk.setCUST_ID(data.get("CUST_ID"));
			pk.setGROUP_ID(inputVO.getGroup());
			vo = (TBCRM_CUST_GROUPVO) dam.findByPKey(TBCRM_CUST_GROUPVO.TABLE_UID, pk);
			if (vo == null) {
				pk.setCUST_ID(data.get("CUST_ID"));
				pk.setGROUP_ID(inputVO.getGroup());
				TBCRM_CUST_GROUPVO vos = new TBCRM_CUST_GROUPVO();
				vos.setGROUP_NAME(inputVO.getGroup_name());
				vos.setcomp_id(pk);
				dam.create(vos);
			} else {
				dam.update(vo);
			}
		}
		this.sendRtnObject(null);
	}

	//新增名單
	public void add_campaign(Object body, IPrimitiveMap header) throws JBranchException {
		CRM230InputVO inputVO = (CRM230InputVO) body;
		dam = this.getDataAccessManager();
		//		CAM996 cam996 = (CAM996) PlatformContext.getBean("cam996");
		CAM996 cam996 = new CAM996();
		Date date = new Date();
		String campaign_ao_code = null;

		if (StringUtils.isBlank(inputVO.getCampaign_ao_code())) {
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			String emp_id = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);

			StringBuilder sb = new StringBuilder();
			sb.append("SELECT AO_CODE FROM TBORG_SALES_AOCODE WHERE EMP_ID = :emp_id AND TYPE = '1' ");

			queryCondition.setObject("emp_id", emp_id);
			queryCondition.setQueryString(sb.toString());

			List<Map<String, Object>> aoCode = dam.exeQueryWithoutSort(queryCondition);

			campaign_ao_code = aoCode.get(0).get("AO_CODE").toString();
		} else {
			campaign_ao_code = inputVO.getCampaign_ao_code();
		}

		//===查詢新指派理專為FC(002)還是FCH(003)
		//		dam = this.getDataAccessManager();
		QueryConditionIF query = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuilder sb1 = new StringBuilder();
		sb1.append("SELECT AO.AO_CODE ,PRI.PRIVILEGEID FROM TBORG_SALES_AOCODE AO ");
		sb1.append("LEFT JOIN TBORG_MEMBER_ROLE ROLE ON AO.EMP_ID = ROLE.EMP_ID ");
		sb1.append("LEFT JOIN TBSYSSECUROLPRIASS PRI ON ROLE.ROLE_ID = PRI.ROLEID ");
		sb1.append("WHERE AO.AO_CODE = :campaign_ao_code ");

		query.setQueryString(sb1.toString());
		//		query.setObject("campaign_ao_code", inputVO.getCampaign_ao_code());
		query.setObject("campaign_ao_code", campaign_ao_code);

		List<Map<String, Object>> temp = dam.exeQueryWithoutSort(query);
		
		String privilegeid = "";
		if (null != temp && temp.size() > 0 && null != temp.get(0).get("PRIVILEGEID")) {
			privilegeid = temp.get(0).get("PRIVILEGEID").toString();			
		}

		//===查詢新指派理專為FC(002)還是FCH(003)--END

		BigDecimal campaign_Seq = cam996.getCampaignSEQ(dam);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		//		String campaignID = sdf.format(date) + "TODO" + inputVO.getCampaign_ao_code();
		String campaignID = sdf.format(date) + "TODO" + campaign_ao_code;
		String campaignName = inputVO.getCampaign_name();
		String campaignDesc = inputVO.getCampaign_desc();
		sdf = new SimpleDateFormat("HHmmss");
		String stepID = sdf.format(date);
		String leadSourceId = "04";
		Date startDate = new Timestamp(Calendar.getInstance().getTime().getTime());

		/*
		 * WMS-CR-20240524-02_RM名單管理機制調整：
		 * 1. 名單類別調整為「參考資訊」(04)。
		 * 2. 名單到期日為T(建立日) + 60日
		 * **/
		Date today = new Date();
//	    System.out.println("Today：" + today);
	    Calendar c = Calendar.getInstance();
	    c.setTime(today);
	    c.add(Calendar.DATE, 60);
	    Date endDate = c.getTime();
//		Date endDate = new Timestamp(inputVO.getCampaign_date().getTime());
		String leadType = "04";
//		String leadType = "03";
		
		String leadPara1 = "N";
		String leadPara2 = "N";
		String examID = null;
		String salesPitch = null;
		String firstChannel = "FCALL";
		if ("003".equals(privilegeid)) {
			firstChannel = "FCH";
		}
		String secondChannel = null;
		String impStatus = "IN";
		String checkStatus = "00";
		String giftCampaignID = null;
		BigDecimal leTotalCnt = new BigDecimal(inputVO.getCampaign_custlist().size());

		cam996.saveCampaign(dam, campaign_Seq, campaignID, campaignName, campaignDesc, stepID, leadSourceId, startDate, endDate, leadType, leadPara1, leadPara2, 
							examID, salesPitch, firstChannel, secondChannel, impStatus, checkStatus, giftCampaignID, leTotalCnt, null, null);

		for (Map<String, String> data : inputVO.getCampaign_custlist()) {
			BigDecimal seqNo = campaign_Seq;
			String custID = data.get("CUST_ID");
			String custName = data.get("CUST_NAME");
			String branchID = null;
			if (StringUtil.isEqual(inputVO.getSource(), "crm210")) {
				if (data.get("CUST_02") != null) {
					branchID = data.get("CUST_02");
				} else { //如果客戶沒有歸屬行則定義branchID為指派新理專的歸屬行
					QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

					StringBuilder sb = new StringBuilder();
					sb.append("SELECT INFO.BRANCH_NBR FROM TBORG_SALES_AOCODE SALE ");
					sb.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON SALE.EMP_ID = INFO.EMP_ID ");
					sb.append("WHERE INFO.BRANCH_NBR IS NOT NULL AND SALE.AO_CODE = :ao_code ");

					queryCondition.setQueryString(sb.toString());

					if (StringUtils.isBlank(inputVO.getCampaign_ao_code())) {
						queryCondition.setObject("ao_code", data.get("AO_CODE"));
					} else {
						queryCondition.setObject("ao_code", inputVO.getCampaign_ao_code());
					}

					List<Map<String, Object>> tempList = dam.exeQueryWithoutSort(queryCondition);
					branchID = tempList.get(0).get("BRANCH_NBR").toString();
				}
			} else {
				branchID = data.get("BRA_NBR");
			}
//			String aoCode = data.get("AO_CODE");
			String aoCode = inputVO.getCampaign_ao_code() != null && inputVO.getCampaign_ao_code().equals("OWN") ? null : inputVO.getCampaign_ao_code();
			Date lead_startDate = new Timestamp(Calendar.getInstance().getTime().getTime());
			Date lead_endDate = endDate;
			String lead_leadType = "03";

			cam996.saveLeads(dam, seqNo, custID, custName, branchID, aoCode, lead_startDate, lead_endDate, lead_leadType);
		}
		this.sendRtnObject(null);
	}

	//薪轉公司查詢
	public void query_company(Object body, IPrimitiveMap header) throws JBranchException {

		CRM230InputVO inputVO = (CRM230InputVO) body;
		CRM230OutputVO return_VO = new CRM230OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append(" SELECT DISTINCT SAL_COMPANY_NAME as SAL_COMPANY ");
		sql.append(" FROM TBCRM_SALRMT ");
		sql.append(" WHERE 1=1  ");

		if (StringUtils.equals(inputVO.getCompanyTYPE(), "1")) {
			sql.append(" AND SAL_COMPANY_NAME LIKE :keywrod ");
			condition.setObject("keywrod", "%" + inputVO.getKeyWord() + "%");
		}

		sql.append(" ORDER BY SAL_COMPANY_NAME ASC ");
		condition.setQueryString(sql.toString());
		ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();

		return_VO.setTotalList(dam.exeQuery(condition));
		return_VO.setResultList(list);
		return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		return_VO.setTotalPage(totalPage_i);// 總頁次
		return_VO.setTotalRecord(totalRecord_i);// 總筆數
		return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
		this.sendRtnObject(return_VO);
	}

	public StringBuilder genCustFilterSql(String type, QueryConditionIF queryCondition, CRM230_ALLInputVO inputVO_all) throws JBranchException {
		
		initUUID();

		Map<SqlGen, StringBuilder> sqlGenMap = createSqlModelMap();

		StringBuilder sqlModel = new StringBuilder();
		sqlModel.append(OUT_SIDE_COLUMN).append(" ");
		sqlModel.append(OUT_SIDE_COLUMN_END);
		sqlModel.append("FROM( ");
		sqlModel.append("	SELECT  ");
		sqlModel.append("		A.CUST_ID, ");
		sqlModel.append("     	A.CUST_NAME, ");
		sqlModel.append("     	A.AUM_AMT, ");
		sqlModel.append("     	A.CON_DEGREE, ");
		sqlModel.append("     	A.VIP_DEGREE, ");
		sqlModel.append("    	A.AO_CODE, ");
		sqlModel.append("       CUP.EMP_ID AS UEMP_ID, ");
		sqlModel.append("    	CUP.EMP_NAME || CASE WHEN CUP.CODE_TYPE = '1' THEN '(計績)' WHEN CUP.CODE_TYPE = '3' THEN '(維護)' ELSE '(副)' END AS UEMP_NAME, ");
		sqlModel.append("     	A.GENDER, ");
		sqlModel.append("     	A.BIRTH_DATE, ");
		sqlModel.append("    	A.BRA_NBR, ");
		sqlModel.append("     	A.FAMILY_DEGREE, ");
		sqlModel.append("     	NVL(B.CO_ACCT_YN,'N') AS CO_ACCT_YN, ");
		sqlModel.append("     	B.COMPLAIN_YN, ");
		sqlModel.append("       B.W8BEN_YN AS CUST_W8BEN, "); 	// W-8BEN(W-8BEN-E)
		sqlModel.append("     	B.REC_YN ");
		sqlModel.append(IN_COLUMN);
		sqlModel.append("	FROM TBCRM_CUST_MAST A ");
		
		if (StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "uhrm")) {
			sqlModel.append("   INNER JOIN VWORG_EMP_UHRM_INFO CUP ON A.AO_CODE = CUP.UHRM_CODE ");
		} else if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0) {
			sqlModel.append("   INNER JOIN VWORG_EMP_UHRM_INFO CUP ON A.AO_CODE = CUP.UHRM_CODE AND CUP.DEPT_ID = :loginArea ");
			queryCondition.setObject("loginArea", getUserVariable(FubonSystemVariableConsts.LOGIN_AREA));
		} else {
			sqlModel.append("   LEFT JOIN VWORG_EMP_UHRM_INFO CUP ON A.AO_CODE = CUP.UHRM_CODE");
		}
		
		sqlModel.append("   INNER JOIN TBCRM_CUST_NOTE B ON A.CUST_ID = B.CUST_ID ");
		sqlModel.append(IN_TABLE).append(" ");
		sqlModel.append(IN_WHERE);
		sqlModel.append(") A ");
		sqlModel.append(OUT_SIDE_TABLE).append(" ");
		sqlModel.append(OUT_SIDE_WHERE);

		//呼叫對應的方法產生SQL
		try {
			//客戶主檔、註記檔查詢條件組合
			genCustSqlContent(sqlGenMap, inputVO_all, queryCondition);

			String methodName = "gen" + type;
			Class[] argumentsType = new Class[] { Map.class, CRM230_ALLInputVO.class, QueryConditionIF.class };
			this.getClass().getMethod(methodName, argumentsType).invoke(this, sqlGenMap, inputVO_all, queryCondition);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (sqlGenMap.get(SqlGen.OUT_SIDE_COLUMN).length() > 0)
			sqlGenMap.get(SqlGen.OUT_SIDE_COLUMN).insert(0, " , ");

		if (sqlGenMap.get(SqlGen.IN_COLUMN).length() > 0)
			sqlGenMap.get(SqlGen.IN_COLUMN).insert(0, " , ");

		replace(sqlModel, OUT_SIDE_COLUMN, sqlGenMap.get(SqlGen.OUT_SIDE_COLUMN));
		replace(sqlModel, OUT_SIDE_TABLE, sqlGenMap.get(SqlGen.OUT_SIDE_TABLE));
		replace(sqlModel, OUT_SIDE_WHERE, sqlGenMap.get(SqlGen.OUT_SIDE_WHERE));
		replace(sqlModel, IN_COLUMN, sqlGenMap.get(SqlGen.IN_COLUMN));
		replace(sqlModel, IN_TABLE, sqlGenMap.get(SqlGen.IN_TABLE));
		replace(sqlModel, IN_WHERE, sqlGenMap.get(SqlGen.IN_WHERE));

		return sqlModel;
	}

	public StringBuilder replace(StringBuilder strBuilder, String str, StringBuilder reStr) {
		return strBuilder.replace(strBuilder.indexOf(str), strBuilder.indexOf(str) + str.length(), reStr.toString());
	}

	public void genCustSqlContent(Map<SqlGen, StringBuilder> sqlGenMap, CRM230_ALLInputVO inputVO_all, QueryConditionIF queryCondition) throws JBranchException {
		
		initUUID();
		
		StringBuilder inJoinTable = sqlGenMap.get(SqlGen.IN_TABLE);
		StringBuilder inWhere = sqlGenMap.get(SqlGen.IN_WHERE);
		CRM230InputVO inputVO = inputVO_all.getCrm230inputVO();

		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap      = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);		//理專
		Map<String, String> psopMap    = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);	//作業人員
		Map<String, String> uhrmmgrMap = xmlInfo.doGetVariable("FUBONSYS.UHRMMGR_ROLE", FormatHelper.FORMAT_2);	//UHRM科/處主管
		Map<String, String> uhrmMap    = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2);	//UHRM
		Map<String, String> paoMap     = xmlInfo.doGetVariable("FUBONSYS.PAO_ROLE", FormatHelper.FORMAT_2);		//作業人員
		
		// uemp id
		if (StringUtils.isNotBlank(inputVO.getuEmpID())) {
			// mantis 6705 : WMS-CR-20190920-02_高端報表需求維護Code調整申請_P1
			String[] uEmpDtl = inputVO.getuEmpID().split(",");
    		if(uEmpDtl.length >= 2){ //有輸入
    			inWhere.append("AND CUP.EMP_ID = :uEmpID ");
    			inWhere.append("AND CUP.CODE_TYPE = :uEmpAoType ");
        		queryCondition.setObject("uEmpID", uEmpDtl[0]);
        		queryCondition.setObject("uEmpAoType", uEmpDtl[1]);
    		} else {
    			inWhere.append("AND CUP.EMP_ID = :uEmpID ");
        		queryCondition.setObject("uEmpID", getCommonVariable(FubonSystemVariableConsts.LOGINID));
    		}
		}
		
		//歸屬行 - 有選
		if (StringUtils.isNotBlank(inputVO.getBelong_bra_nbr())) {
			inWhere.append("AND A.BRA_NBR = :branch_list ");
			queryCondition.setObject("branch_list", inputVO.getBelong_bra_nbr());
		} else { //歸屬行 - 未選
			if (uhrmMap.containsKey(inputVO_all.getLoginRole()) || uhrmmgrMap.containsKey(inputVO_all.getLoginRole()) || 
				(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0 &&
				 !StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "uhrm")) ) {
			} else {
				inWhere.append("AND A.BRA_NBR IN (:branch_list) ");
				queryCondition.setObject("branch_list", inputVO_all.getAvailBranchList());
			}
		}

		//AO Code
		if (StringUtils.isNotBlank(inputVO.getAo_code()) && !StringUtils.equals("0", inputVO.getAo_code())) { //有輸入
			// 20210629 add by Ocean => #0662: WMS-CR-20210624-01_配合DiamondTeam專案調整系統模組功能_組織+客管 change svn:log
			if (StringUtils.equals(inputVO.getAo_code(), "Diamond Team")) {
				inWhere.append("AND EXISTS ( ");
				inWhere.append("  SELECT AO.AO_CODE ");
				inWhere.append("  FROM TBORG_DIAMOND_TEAM DT_A ");
				inWhere.append("  INNER JOIN TBORG_SALES_AOCODE AO ON DT_A.EMP_ID = AO.EMP_ID ");
				inWhere.append("  WHERE EXISTS (SELECT 1 FROM TBORG_DIAMOND_TEAM DT_B WHERE DT_A.BRANCH_NBR = DT_B.BRANCH_NBR AND DT_A.TEAM_TYPE = DT_B.TEAM_TYPE AND EMP_ID = :empID) ");
				inWhere.append("  AND A.AO_CODE = AO.AO_CODE ");
				inWhere.append(") ");
				queryCondition.setObject("empID", getUserVariable(FubonSystemVariableConsts.LOGINID));
			} else {
				inWhere.append("AND A.AO_CODE = :ao_code ");
				queryCondition.setObject("ao_code", inputVO.getAo_code());
			}
		} else if (StringUtils.equals("0", inputVO.getAo_code())) { //查詢空Code
			inWhere.append("AND A.AO_CODE IS NULL "); //先暫時這樣寫，待提升效能後再修正
		} else if (StringUtils.isBlank(inputVO.getAo_code())) { //請選擇
			if (fcMap.containsKey(inputVO_all.getLoginRole()) || 
				psopMap.containsKey(inputVO_all.getLoginRole()) ||
				paoMap.containsKey(inputVO_all.getLoginRole())) {//fc or psop or pao
				inWhere.append("AND A.AO_CODE in (SELECT AO_CODE FROM VWORG_AO_INFO WHERE EMP_ID = :loginID) ");
				queryCondition.setObject("loginID", inputVO_all.getLoginEmpID());
			} else if (uhrmMap.containsKey(inputVO_all.getLoginRole())) {
				inWhere.append("AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO T WHERE T.UHRM_CODE = A.AO_CODE AND T.EMP_ID = :loginID) ");
				queryCondition.setObject("loginID", inputVO_all.getLoginEmpID());
			} 
		}

		//國籍
		if (!StringUtils.isBlank(inputVO.getCountry_nbr())) {
			inWhere.append("AND A.COUNTRY_NBR = :country_nbr ");
			queryCondition.setObject("country_nbr", inputVO.getCountry_nbr());
		}

		//性別
		if (!StringUtils.isBlank(inputVO.getGender())) {
			inWhere.append("AND A.GENDER = :gender ");
			queryCondition.setObject("gender", inputVO.getGender());
		}

		//婚姻狀況
		if (!StringUtils.isBlank(inputVO.getMarriage())) {
			if (inputVO.getMarriage().matches("1|2")) {
				inWhere.append("AND A.MARRIAGE_STAT = :marriage ");
				queryCondition.setObject("marriage", inputVO.getMarriage());
			} else {
				inWhere.append("AND A.MARRIAGE_STAT not in('1' , '2') ");
			}
		}

		//學歷
		if (!StringUtils.isBlank(inputVO.getEducation())) {
			inWhere.append("AND A.EDUCATION_STAT = :education ");
			queryCondition.setObject("education", inputVO.getEducation());
		}

		//子女數
		if (!StringUtils.isBlank(inputVO.getChild())) {
			if ("Y".equals(inputVO.getChild())) {
				inWhere.append("AND (A.CHILD_NUM IS NOT NULL AND A.CHILD_NUM <> '0') ");
			}
			if ("N".equals(inputVO.getChild())) {
				inWhere.append("AND (A.CHILD_NUM IS NULL OR A.CHILD_NUM = '0') ");
			}
		}

		//理財會員等級(理財會員等級空白即是一般#0002559)
		if (!StringUtils.isBlank(inputVO.getVip_degree())) {
			if ("M".equals(inputVO.getVip_degree())) { //一般
				inWhere.append("AND (NVL(A.VIP_DEGREE, 'M') = 'M' OR A.VIP_DEGREE = ' ') ");
			} else {
				inWhere.append("AND A.VIP_DEGREE = :vip_degree ");
				queryCondition.setObject("vip_degree", inputVO.getVip_degree());
			}
		}

		//客戶風險屬性：
		if (!StringUtils.isBlank(inputVO.getCust_risk_atr())) {
			inWhere.append("AND A.CUST_RISK_ATR = :cust_risk_atr ");
			queryCondition.setObject("cust_risk_atr", inputVO.getCust_risk_atr());
		}

		//薪轉公司
		if (!StringUtils.isBlank(inputVO.getSal_company())) {
			inWhere.append("AND INSTR(A.SAL_COMPANY, :sal_company)>0 ");
			queryCondition.setObject("sal_company", inputVO.getSal_company().trim());
		}

		//法金板塊
		if (!StringUtils.isBlank(inputVO.getCo_acct())) {
			//法金戶 CO_ACCT_YN 空值和N都屬於 無法金戶
			inWhere.append("AND NVL(B.CO_ACCT_YN, 'N') = :co_acct ");
			queryCondition.setObject("co_acct", inputVO.getCo_acct());
		}

		//客訴戶
		if (!StringUtils.isBlank(inputVO.getComplain())) {
			inWhere.append("AND NVL(B.COMPLAIN_YN, 'N') = :complain ");
			queryCondition.setObject("complain", inputVO.getComplain());
		}

		//使用網路銀行
		if (!StringUtils.isBlank(inputVO.getEbank())) {
			inWhere.append("AND NVL(B.EBANK_YN, 'N') = :ebank ");
			queryCondition.setObject("ebank", inputVO.getEbank());
		}

		//使用語音
		if (!StringUtils.isBlank(inputVO.getVoice())) {
			inWhere.append("AND NVL(B.VOICE_YN, 'N') = :voice ");
			queryCondition.setObject("voice", inputVO.getVoice());
		}

		//使用行動銀行
		if (!StringUtils.isBlank(inputVO.getApp())) {
			inWhere.append("AND NVL(B.APP_YN, 'N') = :app ");
			queryCondition.setObject("app", inputVO.getApp());
		}

		//專業投資人
		if (!StringUtils.isBlank(inputVO.getInvestor()) && "Y".equals(inputVO.getInvestor())) {
			inWhere.append("AND B.PROF_INVESTOR_YN = :investor ");
			queryCondition.setObject("investor", inputVO.getInvestor());
		}

		//可推介
		if (!StringUtils.isBlank(inputVO.getRec()) && "Y".equals(inputVO.getRec())) {
			inWhere.append("AND B.REC_YN = :rec ");
			queryCondition.setObject("rec", inputVO.getRec());
		}

		//出生年起日
		if (inputVO.getBirth_sDate() != null) {
			inWhere.append("AND A.BIRTH_DATE >= :start ");
			queryCondition.setObject("start", new Timestamp(inputVO.getBirth_sDate().getTime()));
		}

		//出生年迄日
		if (inputVO.getBirth_eDate() != null) {
			inWhere.append("AND A.BIRTH_DATE <= :end ");
			queryCondition.setObject("end", new Timestamp(inputVO.getBirth_eDate().getTime()));
		}
		
		//客戶別(02：自然人、03：法人)
		if (!StringUtils.isBlank(inputVO.getCust_type())) {
			if ("02".equals(inputVO.getCust_type())) {
				inWhere.append("AND LENGTH(A.CUST_ID) = 10 ");				
			} else {
				inWhere.append("AND LENGTH(A.CUST_ID) <> 10 ");
			}
		}
		
		//薪轉戶
		if (!StringUtils.isBlank(inputVO.getSal_acc_yn())) {
			inWhere.append("AND NVL(B.SAL_ACC_YN, 'N') = :sal_acc_yn ");
			queryCondition.setObject("sal_acc_yn", inputVO.getSal_acc_yn());
		}
		
		// W-8BEN(W-8BEN-E)
		if (!StringUtils.isBlank(inputVO.getCust_w8ben())) {
			switch (inputVO.getCust_w8ben()) {
				case "N":
					inWhere.append("AND (B.W8BEN_YN IS NULL OR B.W8BEN_YN = 'N') ");
					break;
				case "Y":
					inWhere.append("AND B.W8BEN_YN = 'Y' ");
					break;
			}
		}
		
		//CASE 1:持有信用卡  (選Y)
		if (StringUtils.isNotBlank(inputVO.getHold_card_flg()) && StringUtils.equals("Y", inputVO.getHold_card_flg())) {
			inJoinTable.append("INNER JOIN MVCRM_CCARD_INFO_NOW CI ON A.CUST_ID = CI.CUST_ID ");
			if (StringUtils.equals("0", inputVO.getMs_type())) { //正卡
				inJoinTable.append(" AND CI.M_CARD_CNT > 0 AND CI.S_CARD_CNT = 0 ");
			} else if (StringUtils.equals("1", inputVO.getMs_type())) { //附卡
				inJoinTable.append(" AND CI.M_CARD_CNT = 0 AND CI.S_CARD_CNT > 0 ");
			} else if (StringUtils.equals("A", inputVO.getMs_type())) { //正卡+附卡
				inJoinTable.append(" AND CI.M_CARD_CNT > 0 AND CI.S_CARD_CNT > 0 ");
			} else { //沒選(任意卡皆可)
				inJoinTable.append(" AND (CI.M_CARD_CNT > 0 OR CI.S_CARD_CNT > 0) ");
			}
		}

		//CASE 2:持有信用卡  (選N)
		if (StringUtils.isNotBlank(inputVO.getHold_card_flg()) && StringUtils.equals("N", inputVO.getHold_card_flg())) {
			inJoinTable.append(" INNER JOIN ( ");
			inJoinTable.append(" 	SELECT DISTINCT ");
			inJoinTable.append(" 		Q.CUST_ID, ");
			inJoinTable.append(" 		NVL(CI.M_CARD_CNT, 0) AS M_CARD_CNT, ");
			inJoinTable.append("		NVL(CI.S_CARD_CNT, 0) AS S_CARD_CNT ");
			inJoinTable.append(" 	FROM TBCRM_CUST_MAST Q ");
			inJoinTable.append(" 		LEFT OUTER JOIN (");
			inJoinTable.append(" 			SELECT ");
			inJoinTable.append(" 				CUST_ID, ");
			inJoinTable.append(" 				SUM(CASE WHEN NVL(MS_TYPE,'X')='0' THEN 1 ELSE 0 END) AS M_CARD_CNT, ");
			inJoinTable.append(" 				SUM(CASE WHEN NVL(MS_TYPE,'X')='1' THEN 1 ELSE 0 END) AS S_CARD_CNT  ");
			inJoinTable.append(" 			FROM TBCRM_AST_LIB_CREDITCARD ");
			inJoinTable.append(" 			WHERE 1=1 ");
			inJoinTable.append(" 				AND TRUNC(ISSUE_DATE) <= TRUNC(SYSDATE) ");
			inJoinTable.append("				AND (OPP_DATE IS NULL OR TRUNC(OPP_DATE) > TRUNC(SYSDATE)) ");
			inJoinTable.append(" 			GROUP BY CUST_ID ");
			inJoinTable.append("		)CI ON Q.CUST_ID = CI.CUST_ID ");
			inJoinTable.append(" 	)CII ON A.CUST_ID = CII.CUST_ID ");
			if (StringUtils.equals("0", inputVO.getOpp_ms_type())) { //正卡
				inJoinTable.append("AND CII.M_CARD_CNT = 0 ");
			} else if (StringUtils.equals("1", inputVO.getOpp_ms_type())) { //附卡
				inJoinTable.append("AND CII.S_CARD_CNT = 0 ");
			} else if (StringUtils.equals("A", inputVO.getOpp_ms_type())) { //正卡+附卡
				inJoinTable.append("AND CII.M_CARD_CNT = 0 AND CII.S_CARD_CNT = 0 ");
			} else { //沒選(任意卡皆可)
				inJoinTable.append("AND CII.M_CARD_CNT = 0 AND CII.S_CARD_CNT = 0 ");
			}
		}
	}

	/** 所有商品 **/
	public void genCRM231(Map<SqlGen, StringBuilder> sqlGenMap, CRM230_ALLInputVO inputVO_all, QueryConditionIF queryCondition) {
		StringBuilder inJoinTable = sqlGenMap.get(SqlGen.IN_TABLE);
		StringBuilder outSideColumn = sqlGenMap.get(SqlGen.OUT_SIDE_COLUMN);
		StringBuilder outSideJoin = sqlGenMap.get(SqlGen.OUT_SIDE_TABLE);
		StringBuilder outSideWhere = sqlGenMap.get(SqlGen.OUT_SIDE_WHERE);

		CRM231InputVO inputVO_CRM231 = inputVO_all.getCrm231inputVO();
		//    	String holdCardFlg = ;//是否持有有效信用卡

		//有選商品類別
		if (inputVO_CRM231.getProd_flag()) {
			outSideColumn.append(" V.AST_TYPE, "); //商品類別
			outSideColumn.append(" V.PROD_ID, "); //商品代號
			outSideColumn.append(" V.PROD_NAME, "); //商品名稱
			outSideColumn.append(" V.CUR_ID, "); //投資幣別
			outSideColumn.append(" V.STOP_LOSS_PT, "); //客戶停損點
			outSideColumn.append(" V.INV_AMT, "); //原幣投資金額
			outSideColumn.append(" V.NOW_AMT, "); //原幣參考現值			
			outSideColumn.append(" V.NOW_AMT * BR.BUY_RATE NOW_AMT_TWD, "); //台幣參考現值
			outSideColumn.append(" V.REF_PL, "); //原幣損益金額
			outSideColumn.append(" V.REF_PL * BR.BUY_RATE REF_PL_TWD, "); //台幣損益金額
			outSideColumn.append(" BR.BUY_RATE EXCH_RATE, "); //參考匯率			
			outSideColumn.append(" V.RTN_RATE_WD, "); //報酬率
			outSideColumn.append(" V.TAKE_PRFT_PT, "); //客戶停利點
			outSideColumn.append(" V.TOTAL_UNIT "); //SI.SN.海外債庫存面額或基金.ETF.海外股累計單位數
			//outSideColumn.append(" V.EXCH_RATE, ");//參考匯率
			//outSideColumn.append(" V.REF_PL_TWD, ");//台幣損益金額
			//outSideColumn.append(" V.NOW_AMT_TWD, ");//台幣參考現值

			outSideJoin.append(" LEFT JOIN MVCRM_AST_ALLPRD_DETAIL V ");
			outSideJoin.append(" ON A.CUST_ID = V.CUST_ID ");
			//串IQ053匯率(前日匯率相關的資訊)
			outSideJoin.append(" LEFT JOIN ( ");
			outSideJoin.append(" 	SELECT CUR_COD , BUY_RATE FROM( ");
			outSideJoin.append(" 		SELECT CUR_COD , BUY_RATE FROM TBPMS_IQ053 WHERE (CUR_COD , MTN_DATE) IN( SELECT CUR_COD , MAX(MTN_DATE) FROM TBPMS_IQ053 GROUP BY CUR_COD ) ");
			outSideJoin.append(" 	)");
			outSideJoin.append(" ) BR ON BR.CUR_COD = V.CUR_ID ");

			//商品類別
			if (!StringUtils.isBlank(inputVO_CRM231.getProd_type())) {
				//當商品類為保險時
				if ("14".equals(inputVO_CRM231.getProd_type())) {
					outSideWhere.append(" AND (V.AST_TYPE = '20' "); //富邦銀 & 日盛銀
					
					outSideWhere.append(" OR (V.AST_TYPE = '14' AND (A.CUST_ID , V.PROD_ID) IN(");
					outSideWhere.append(" 	select APPL_ID, PRD_ID from TBCRM_NPOLM WHERE IS_CONSENT = 'Y' AND POLICY_STATUS NOT IN('06','11','12','13','14','15','17','20','21','22','23','24','25','26','27','28','29','30','31','32')) ");
					outSideWhere.append(" )) ");
				} else {
					outSideWhere.append(" AND V.AST_TYPE  = :prod_type ");
					queryCondition.setObject("prod_type", inputVO_CRM231.getProd_type());

				}
					
			}

			//報酬率(%)(起)
			if (!StringUtils.isBlank(inputVO_CRM231.getRtn_rate_wd_bgn())) {
				outSideWhere.append(" AND V.RTN_RATE_WD >= :rtn_rate_wd_bgn ");
				queryCondition.setObject("rtn_rate_wd_bgn", new BigDecimal(inputVO_CRM231.getRtn_rate_wd_bgn()));
			}

			//報酬率(%)(迄)
			if (!StringUtils.isBlank(inputVO_CRM231.getRtn_rate_wd_end())) {
				outSideWhere.append(" AND V.RTN_RATE_WD <= :rtn_rate_wd_end ");
				queryCondition.setObject("rtn_rate_wd_end", new BigDecimal(inputVO_CRM231.getRtn_rate_wd_end()));
			}

			//#0003472 : 若無法以報酬率篩選海外債和DCI，查詢結果請不要帶出來
			if (!StringUtils.isBlank(inputVO_CRM231.getRtn_rate_wd_bgn()) || !StringUtils.isBlank(inputVO_CRM231.getRtn_rate_wd_end())) {
				outSideWhere.append(" AND V.AST_TYPE != '14' ");
				outSideWhere.append(" AND V.PROD_ID NOT IN(SELECT PRD_ID FROM TBPRD_BOND)  ");
			}

			//商品代碼
			if (!StringUtils.isBlank(inputVO_CRM231.getProd_id())) {
				outSideWhere.append(" AND V.PROD_ID LIKE :prod_id ");
				queryCondition.setObject("prod_id", "%" + inputVO_CRM231.getProd_id().toUpperCase() + "%");
			}

			//台幣參考現值(起)
			if (!StringUtils.isBlank(inputVO_CRM231.getNow_amt_twd_bgn())) {
				outSideWhere.append(" AND (V.NOW_AMT * BR.BUY_RATE) >= :now_amt_twd_bgn ");
				queryCondition.setObject("now_amt_twd_bgn", inputVO_CRM231.getNow_amt_twd_bgn());
			}

			//台幣參考現值(迄)
			if (!StringUtils.isBlank(inputVO_CRM231.getNow_amt_twd_end())) {
				outSideWhere.append(" AND (V.NOW_AMT * BR.BUY_RATE) <= :now_amt_twd_end ");
				queryCondition.setObject("now_amt_twd_end", inputVO_CRM231.getNow_amt_twd_end());
			}

			//商品名稱
			if (!StringUtils.isBlank(inputVO_CRM231.getProd_name())) {
				outSideWhere.append(" AND V.PROD_NAME LIKE :prod_name ");
				queryCondition.setObject("prod_name", "%" + inputVO_CRM231.getProd_name() + "%");
			}

			//投資幣別
			if (!StringUtils.isBlank(inputVO_CRM231.getCur_id())) {
				outSideWhere.append(" AND V.CUR_ID = :cur_id ");
				queryCondition.setObject("cur_id", inputVO_CRM231.getCur_id());
			}

			//客戶停利點(%)(起)
			if (!StringUtils.isBlank(inputVO_CRM231.getTake_prft_pt_bgn())) {
				outSideWhere.append(" AND V.TAKE_PRFT_PT >= :take_prft_pt_bgn ");
				queryCondition.setObject("take_prft_pt_bgn", new BigDecimal(inputVO_CRM231.getTake_prft_pt_bgn()));
			}

			//客戶停利點(%)(迄)
			if (!StringUtils.isBlank(inputVO_CRM231.getTake_prft_pt_end())) {
				outSideWhere.append(" AND V.TAKE_PRFT_PT <= :take_prft_pt_end ");
				queryCondition.setObject("take_prft_pt_end", new BigDecimal(inputVO_CRM231.getTake_prft_pt_end()));
			}

			//客戶停損點(%)(起)
			if (!StringUtils.isBlank(inputVO_CRM231.getStop_loss_pt_bgn())) {
				outSideWhere.append(" AND V.STOP_LOSS_PT >= :stop_loss_pt_bgn ");
				queryCondition.setObject("stop_loss_pt_bgn", new BigDecimal(inputVO_CRM231.getStop_loss_pt_bgn()));
			}

			//客戶停損點(%)(迄)
			if (!StringUtils.isBlank(inputVO_CRM231.getStop_loss_pt_end())) {
				outSideWhere.append(" AND V.STOP_LOSS_PT <= :stop_loss_pt_end ");
				queryCondition.setObject("stop_loss_pt_end", new BigDecimal(inputVO_CRM231.getStop_loss_pt_end()));
			}
		}
	}

	/** 基金 **/
	public void genCRM232(Map<SqlGen, StringBuilder> sqlGenMap, CRM230_ALLInputVO inputVO_all, QueryConditionIF queryCondition) {
		StringBuilder outSideColumn = sqlGenMap.get(SqlGen.OUT_SIDE_COLUMN);
		StringBuilder outSideJoinTable = sqlGenMap.get(SqlGen.OUT_SIDE_TABLE);
		StringBuilder outSideWhere = sqlGenMap.get(SqlGen.OUT_SIDE_WHERE);
		CRM232InputVO inputVO_CRM232 = inputVO_all.getCrm232inputVO();

		outSideColumn.append(" M.INV_CRCY_TYPE, ");//投資幣別
		outSideColumn.append(" M.INV_AMT_ORGD, ");//原始投資本金
		outSideColumn.append(" M.VALU_CRCY_TYPE, ");//計價幣別
		outSideColumn.append(" M.TOTAL_ACUM_INV_UNIT, ");
		outSideColumn.append(" M.REF_TOTAL_PL_ORGD, "); //原幣損益金額
		outSideColumn.append(" M.REF_TOTAL_PL_ORGD * BR.BUY_RATE REF_TOTAL_PL_TWD, ");//台幣損益金額
		outSideColumn.append(" M.REF_TOTAL_REAL_VALUE_ORGD, ");//原幣參考現值
		outSideColumn.append(" M.REF_TOTAL_REAL_VALUE_ORGD * BR.BUY_RATE REF_TOTAL_REAL_VALUE_TWD , ");//台幣參考現值
		outSideColumn.append(" BR.BUY_RATE REF_BUYIN_EXCH_RATE , ");//參考匯率
		outSideColumn.append(" M.RTN_RATE_WD, ");//含息報酬率
		outSideColumn.append(" M.TAKE_PRFT_PT, ");//客戶停利點
		outSideColumn.append(" M.STOP_LOSS_PT, ");//客戶停損點
		outSideColumn.append(" M.REF_ABS_RET_RATE_TWD, ");//報酬率
		outSideColumn.append(" F.FUND_CNAME, ");
		outSideColumn.append(" F.FUND_CNAME_A, ");//信託產品名稱
		outSideColumn.append(" F.PRD_ID, ");//產品代號
		outSideColumn.append(" F.DIVIDEND_FREQUENCY, ");//配息頻率
		outSideColumn.append(" F.GLOBAL_ID  ");//類別投資市場別

		//outSideColumn.append(" M.REF_BUYIN_EXCH_RATE, ");//參考匯率
		//outSideColumn.append(" M.REF_TOTAL_REAL_VALUE_TWD, ");
		//outSideColumn.append(" M.REF_TOTAL_PL_TWD, ");

		outSideJoinTable.append(" INNER JOIN VWCRM_AST_INV_FUND M ON A.CUST_ID = M.CUST_ID ");
		outSideJoinTable.append(" LEFT JOIN TBPRD_FUND F ON M.FUND_CODE = F.PRD_ID ");
		//2017-10-19 by Jacky 基金客戶查詢優化
//    	outSideJoinTable.append(" LEFT JOIN TBPMS_AC2FIL_DAY AC ON M.CUST_ID = TRIM(AC.AC203) AND TRIM(M.CERT_NBR) = TRIM(AC.AC201) ");

        //串IQ053匯率(前日匯率相關的資訊)
        outSideJoinTable.append(" LEFT JOIN ( SELECT CUR_COD, BUY_RATE FROM TBPMS_IQ053 WHERE MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ) BR ");
        outSideJoinTable.append(" ON BR.CUR_COD = M.VALU_CRCY_TYPE ");

		//類別、投資市場別
		if (!StringUtils.isBlank(inputVO_CRM232.getTier1())) {
			outSideWhere.append(" AND F.GLOBAL_ID IN ( ");
			outSideWhere.append(" 	SELECT GLOBAL_ID FROM TBPRD_INVEST_AREA ");
			outSideWhere.append(" 	WHERE 1 = 1 ");
			outSideWhere.append(" 	AND MKT_TIER1 = :tier1 ");

			queryCondition.setObject("tier1", inputVO_CRM232.getTier1());

			if (!StringUtils.isBlank(inputVO_CRM232.getTier2())) {
				outSideWhere.append(" AND MKT_TIER2 = :tier2 ");
				queryCondition.setObject("tier2", inputVO_CRM232.getTier2());
			}

			if (!StringUtils.isBlank(inputVO_CRM232.getTier3())) {
				outSideWhere.append(" AND MKT_TIER3 = :tier3 ");
				queryCondition.setObject("tier3", inputVO_CRM232.getTier3());
			}

			outSideWhere.append(" ) ");
		}

		//投資方法
		if (!StringUtils.isBlank(inputVO_CRM232.getInv_way_nbr())) {
			outSideWhere.append(" AND ( ");
			outSideWhere.append(" 	CASE  ");
			outSideWhere.append(" 		WHEN M.INV_WAY_NBR = '2' AND M.PRJ_NOTE = 'FN99' AND TO_CHAR(SIGN_DATE, 'yyyyMMdd') >= '20230901' THEN '8' ");
			outSideWhere.append(" 		WHEN M.INV_WAY_NBR = '3' AND M.PRJ_NOTE = 'FN99' AND TO_CHAR(SIGN_DATE, 'yyyyMMdd') >= '20230901' THEN '9' ");
			outSideWhere.append(" 		WHEN M.INV_WAY_NBR = '2' AND M.PRJ_NOTE = 'FN99' AND TO_CHAR(SIGN_DATE, 'yyyyMMdd') < '20230901'THEN '4' ");
			outSideWhere.append(" 		WHEN M.INV_WAY_NBR = '3' AND M.PRJ_NOTE = 'FN99' AND TO_CHAR(SIGN_DATE, 'yyyyMMdd') < '20230901'THEN '5' ");
			outSideWhere.append(" 		WHEN M.INV_WAY_NBR = '1' THEN '1' ");
			outSideWhere.append(" 		WHEN M.INV_WAY_NBR = '2' THEN '2' ");
			outSideWhere.append(" 		WHEN M.INV_WAY_NBR = '3' THEN '3' ");
			outSideWhere.append(" 	END ");
			outSideWhere.append(" ) = :inv_way_nbr ");
			queryCondition.setObject("inv_way_nbr", inputVO_CRM232.getInv_way_nbr());
		}

		//扣款狀態
		if (!StringUtils.isBlank(inputVO_CRM232.getDeduct_yn())) {
			outSideWhere.append(" AND ( ");
			outSideWhere.append(" 	(M.DEDUCT_YN = :deduct_yn ) OR ");
			outSideWhere.append(" 	EXISTS(SELECT 'X' FROM TBPMS_AC2FIL_DAY AC WHERE  M.CUST_ID = TRIM(AC.AC203) AND TRIM(M.CERT_NBR) = TRIM(AC.AC201) AND AC.AC230 = :deduct_yn ) ");
			outSideWhere.append(" ) ");
			queryCondition.setObject("deduct_yn", inputVO_CRM232.getDeduct_yn());
		}

		//報酬率-最小值
		if (!StringUtils.isBlank(inputVO_CRM232.getRef_abs_ret_rate_twd_min())) {
			outSideWhere.append(" AND M.REF_ABS_RET_RATE_TWD >= :ref_abs_ret_rate_twd_min ");
			queryCondition.setObject("ref_abs_ret_rate_twd_min", getBigDecimal(inputVO_CRM232.getRef_abs_ret_rate_twd_min()));
		}

		//報酬率-最大值
		if (!StringUtils.isBlank(inputVO_CRM232.getRef_abs_ret_rate_twd_max())) {
			outSideWhere.append(" AND M.REF_ABS_RET_RATE_TWD <= :ref_abs_ret_rate_twd_max ");
			queryCondition.setObject("ref_abs_ret_rate_twd_max", getBigDecimal(inputVO_CRM232.getRef_abs_ret_rate_twd_max()));
		}

		//含息報酬率-最小值
		if (!StringUtils.isBlank(inputVO_CRM232.getRtn_rate_wd_min())) {
			outSideWhere.append(" AND M.RTN_RATE_WD >= :rtn_rate_wd_min ");
			queryCondition.setObject("rtn_rate_wd_min", getBigDecimal(inputVO_CRM232.getRtn_rate_wd_min()));
		}

		//含息報酬率-最大值
		if (!StringUtils.isBlank(inputVO_CRM232.getRtn_rate_wd_max())) {
			outSideWhere.append(" AND M.RTN_RATE_WD <= :rtn_rate_wd_max ");
			queryCondition.setObject("rtn_rate_wd_max", getBigDecimal(inputVO_CRM232.getRtn_rate_wd_max()));
		}

//		//基金名稱
		if (!StringUtils.isBlank(inputVO_CRM232.getFundcname_a())) {
			outSideWhere.append(" AND F.FUND_CNAME_A LIKE :fundcname_a ");
			queryCondition.setObject("fundcname_a", "%" + inputVO_CRM232.getFundcname_a() + "%");
		}

		//客戶停損點-最小值
		if (!StringUtils.isBlank(inputVO_CRM232.getStop_loss_pt_min())) {
			outSideWhere.append(" AND M.STOP_LOSS_PT >= :stop_loss_pt_min ");
			queryCondition.setObject("stop_loss_pt_min", getBigDecimal(inputVO_CRM232.getStop_loss_pt_min()));
		}

		//客戶停損點-最大值
		if (!StringUtils.isBlank(inputVO_CRM232.getStop_loss_pt_max())) {
			outSideWhere.append(" AND M.STOP_LOSS_PT <= :stop_loss_pt_max ");
			queryCondition.setObject("stop_loss_pt_max", getBigDecimal(inputVO_CRM232.getStop_loss_pt_max()));
		}

//		//基金代碼
		if (!StringUtils.isBlank(inputVO_CRM232.getFund_code())) {
			//outSideWhere.append(" AND M.FUND_CODE LIKE '" + inputVO_CRM232.getFund_code() + "%'");
			outSideWhere.append(" AND SUBSTR(M.FUND_CODE, 1, length(:fund_code)) = :fund_code ");
			queryCondition.setObject("fund_code", inputVO_CRM232.getFund_code());
		}

		//客戶停利點-最小值
		if (!StringUtils.isBlank(inputVO_CRM232.getTake_prft_pt_min())) {
			outSideWhere.append(" AND M.TAKE_PRFT_PT >= :take_prft_pt_min ");
			queryCondition.setObject("take_prft_pt_min", getBigDecimal(inputVO_CRM232.getTake_prft_pt_min()));
		}

		//客戶停利點-最大值
		if (!StringUtils.isBlank(inputVO_CRM232.getTake_prft_pt_max())) {
			outSideWhere.append(" AND M.TAKE_PRFT_PT <= :take_prft_pt_max ");
			queryCondition.setObject("take_prft_pt_max", getBigDecimal(inputVO_CRM232.getTake_prft_pt_max()));
		}

		//計價幣別
		if (!StringUtils.isBlank(inputVO_CRM232.getValu_crcy_type())) {
			outSideWhere.append(" AND M.VALU_CRCY_TYPE = :valu_crcy_type ");
			queryCondition.setObject("valu_crcy_type", inputVO_CRM232.getValu_crcy_type());
		}

		//配息頻率
		if (!StringUtils.isBlank(inputVO_CRM232.getDividend_frequency())) {
			outSideWhere.append(" AND F.DIVIDEND_FREQUENCY = :dividend_frequency ");
			queryCondition.setObject("dividend_frequency", inputVO_CRM232.getDividend_frequency());
		}

		//配息頻率
		if (!StringUtils.isBlank(inputVO_CRM232.getDividend_frequency())) {
			outSideWhere.append(" AND NVL(F.DIVIDEND_FREQUENCY, ' ') = :dividend_frequency ");
			queryCondition.setObject("dividend_frequency", inputVO_CRM232.getDividend_frequency());
		}

		//信託類別
		if (!StringUtils.isBlank(inputVO_CRM232.getInv_crcy_type())) {
			if ("1".equals(inputVO_CRM232.getInv_crcy_type())) {
				outSideWhere.append(" AND M.INV_CRCY_TYPE = 'TWD' ");
			}
			if ("2".equals(inputVO_CRM232.getInv_crcy_type())) {
				outSideWhere.append(" AND M.INV_CRCY_TYPE <> 'TWD' ");
			}
		}

		//商品風險等級
		if (!StringUtils.isBlank(inputVO_CRM232.getRiskcate_id())) {
			outSideWhere.append(" AND F.RISKCATE_ID = :riskcate_id ");
			queryCondition.setObject("riskcate_id", inputVO_CRM232.getRiskcate_id());
		}

		//申購區間-起
		if (inputVO_CRM232.getSign_date_bgn() != null) {
			outSideWhere.append(" AND M.SIGN_DATE >= :sign_date_bgn ");
			queryCondition.setObject("sign_date_bgn", new Timestamp(inputVO_CRM232.getSign_date_bgn().getTime()));
		}

		//申購區間-迄
		if (inputVO_CRM232.getSign_date_end() != null) {
			outSideWhere.append(" AND M.SIGN_DATE <= :sign_date_end ");
			queryCondition.setObject("sign_date_end", new Timestamp(inputVO_CRM232.getSign_date_end().getTime()));
		}
	}

	/** 海外ETF **/
	public void genCRM233(Map<SqlGen, StringBuilder> sqlGenMap, CRM230_ALLInputVO inputVO_all, QueryConditionIF queryCondition) {
		StringBuilder inColumn = sqlGenMap.get(SqlGen.IN_COLUMN);
		StringBuilder inJoinTable = sqlGenMap.get(SqlGen.IN_TABLE);
		StringBuilder inWhere = sqlGenMap.get(SqlGen.IN_WHERE);
		CRM233InputVO inputVO_CRM233 = inputVO_all.getCrm233inputVO();

		inColumn.append(" M.ORG_STK_PRICE, ");
		inColumn.append(" M.PROD_NAME, ");
		inColumn.append(" M.CRCY_TYPE, ");//投資幣別、計價幣別
		inColumn.append(" M.ORG_STK_PRICE * M.STK_AMT INV_AMT, ");//原始投資本金
		inColumn.append(" P.CURRENCY_STD_ID, ");//商品幣別
		inColumn.append(" (M.STK_AMT + M.ACUM_STK_AMT) as ACUM_STK_AMT, ");//總累積單位數
		inColumn.append(" M.REF_STK_PRICE * (STK_AMT + ACUM_STK_AMT) NOW_AMT, ");//元幣參考現值
		inColumn.append(" M.REF_STK_PRICE * (STK_AMT + ACUM_STK_AMT) * BR.BUY_RATE NOW_AMT_TWD, ");//台幣參考現值
		//  參考股價 * (股數 +  累計配股數) - 原始股價 * 股數
		inColumn.append(" M.REF_STK_PRICE * (STK_AMT + ACUM_STK_AMT) - M.ORG_STK_PRICE * M.STK_AMT REF_PL, ");//外幣損益金額
		// 台幣參考金額 - 原始股價 * 股數 * 參考匯率
		//inColumn.append(" M.REF_AMT_TWD - M.ORG_STK_PRICE * M.STK_AMT * BR.BUY_RATE REF_PL_TWD, ");//台幣損益金額
		inColumn.append(" (M.REF_STK_PRICE * (STK_AMT + ACUM_STK_AMT) - M.ORG_STK_PRICE * M.STK_AMT) * BR.BUY_RATE REF_PL_TWD, ");//台幣損益金額
		inColumn.append(" BR.BUY_RATE REF_EXCH_RATE, ");//參考匯率
		inColumn.append(" M.REF_ABS_RET_RATE_TWD, ");
		inColumn.append(" M.TAKE_PRFT_PT, ");
		inColumn.append(" M.STOP_LOSS_PT ");

		inJoinTable.append(" INNER JOIN TBCRM_AST_INV_FORD_ETFNSTOCK M ON A.CUST_ID = M.CUST_ID ");
		inJoinTable.append(" INNER JOIN TBPRD_ETF P ON M.PROD_ID = P.PRD_ID ");//要用INNER，不然可能會查到海外ETF庫存

		//串IQ053匯率(前日匯率相關的資訊)
		inJoinTable.append(" LEFT JOIN ( ");
		inJoinTable.append(" 	SELECT CUR_COD , BUY_RATE FROM( ");
		inJoinTable.append(" 		SELECT CUR_COD , BUY_RATE FROM TBPMS_IQ053 WHERE (CUR_COD , MTN_DATE) IN( SELECT CUR_COD , MAX(MTN_DATE) FROM TBPMS_IQ053 GROUP BY CUR_COD ) ");
		inJoinTable.append(" 	)");
		inJoinTable.append(" ) BR ON BR.CUR_COD = M.CRCY_TYPE ");

		//成交日期(起)
		if (inputVO_CRM233.getTxn_date_bgn() != null) {
			inWhere.append(" AND TRUNC(M.TXN_DATE) >= :txn_date_bgn ");
			queryCondition.setObject("txn_date_bgn", new Timestamp(inputVO_CRM233.getTxn_date_bgn().getTime()));
		}

		//成交日期(迄)
		if (inputVO_CRM233.getTxn_date_end() != null) {
			inWhere.append(" AND TRUNC(M.TXN_DATE) <= :txn_date_end ");
			queryCondition.setObject("txn_date_end", new Timestamp(inputVO_CRM233.getTxn_date_end().getTime()));
		}

		//商品名稱
		if (!StringUtils.isBlank(inputVO_CRM233.getProd_name())) {
			inWhere.append(" AND M.PROD_NAME LIKE :prod_name ");
			queryCondition.setObject("prod_name", "%" + inputVO_CRM233.getProd_name() + "%");
		}

		//交易所
		if (!StringUtils.isBlank(inputVO_CRM233.getStock_code())) {
			inWhere.append(" AND P.STOCK_CODE = :stock_code ");
			queryCondition.setObject("stock_code", inputVO_CRM233.getStock_code());
		}

		//商品代碼
		if (!StringUtils.isBlank(inputVO_CRM233.getProd_id())) {
			inWhere.append(" AND M.PROD_ID LIKE :prod_id ");
			queryCondition.setObject("prod_id", "%" + inputVO_CRM233.getProd_id() + "%");
		}

		//投資標的類型-->商品屬性
		if (!StringUtils.isBlank(inputVO_CRM233.getStock_attribute())) {
			inWhere.append(" AND P.STOCK_ATTRIBUTE = :stock_attribute ");
			queryCondition.setObject("stock_attribute", inputVO_CRM233.getStock_attribute());
		}

		//商品幣別
		if (!StringUtils.isBlank(inputVO_CRM233.getCurrency_std_id())) {
			inWhere.append(" AND P.CURRENCY_STD_ID = :currency_std_id ");
			queryCondition.setObject("currency_std_id", inputVO_CRM233.getCurrency_std_id());
		}

		//投資區域
		if (!StringUtils.isBlank(inputVO_CRM233.getStock_area())) {
			inWhere.append(" AND P.STOCK_AREA = :stock_area ");
			queryCondition.setObject("stock_area", inputVO_CRM233.getStock_area());
		}

		//商品風險等級
		if (!StringUtils.isBlank(inputVO_CRM233.getRiskcate_id())) {
			inWhere.append(" AND P.RISKCATE_ID = :riskcate_id ");
			queryCondition.setObject("riskcate_id", inputVO_CRM233.getRiskcate_id());
		}

		//商品類型
		if (!StringUtils.isBlank(inputVO_CRM233.getPrd_type())) {
			inWhere.append(" AND P.PRD_TYPE = :prd_type ");
			queryCondition.setObject("prd_type", inputVO_CRM233.getPrd_type());
		}

		//報酬率(起)
		if (!StringUtils.isBlank(inputVO_CRM233.getRef_abs_ret_rate_twd_bgn())) {
			inWhere.append(" AND M.REF_ABS_RET_RATE_TWD >=  :ref_abs_ret_rate_twd_bgn ");
			queryCondition.setObject("ref_abs_ret_rate_twd_bgn", new BigDecimal(inputVO_CRM233.getRef_abs_ret_rate_twd_bgn()));
		}

		//報酬率(迄)
		if (!StringUtils.isBlank(inputVO_CRM233.getRef_abs_ret_rate_twd_end())) {
			inWhere.append(" AND M.REF_ABS_RET_RATE_TWD <=  :ref_abs_ret_rate_twd_end ");
			queryCondition.setObject("ref_abs_ret_rate_twd_end", new BigDecimal(inputVO_CRM233.getRef_abs_ret_rate_twd_end()));
		}

		//專業投資人商品
		if (!StringUtils.isBlank(inputVO_CRM233.getPi_buy())) {
			inWhere.append(" AND NVL(P.PI_BUY, 'N') = :pi_buy ");
			queryCondition.setObject("pi_buy", inputVO_CRM233.getPi_buy());
		}

		//客戶停損點(%)(起)
		if (!StringUtils.isBlank(inputVO_CRM233.getTake_prft_pt_bgn())) {
			inWhere.append(" AND M.STOP_LOSS_PT >= :take_prft_pt_bgn ");
			queryCondition.setObject("take_prft_pt_bgn", new BigDecimal(inputVO_CRM233.getTake_prft_pt_bgn()));
		}

		//客戶停損點(%)(迄)
		if (!StringUtils.isBlank(inputVO_CRM233.getTake_prft_pt_end())) {
			inWhere.append(" AND M.STOP_LOSS_PT <= :take_prft_pt_end ");
			queryCondition.setObject("take_prft_pt_end", new BigDecimal(inputVO_CRM233.getTake_prft_pt_end()));
		}

		//信託類別
		if (!StringUtils.isBlank(inputVO_CRM233.getCom27())) {
			inWhere.append(" AND M.TRST_TYPE_FORD = :trst_type_ford ");
			queryCondition.setObject("trst_type_ford", inputVO_CRM233.getCom27());
		}

		//客戶停利點(%)(起)
		if (!StringUtils.isBlank(inputVO_CRM233.getTake_prft_pt_bgn())) {
			inWhere.append(" AND M.TAKE_PRFT_PT >= :stop_loss_pt_bgn ");
			queryCondition.setObject("stop_loss_pt_bgn", new BigDecimal(inputVO_CRM233.getTake_prft_pt_bgn()));
		}

		//客戶停利點(%)(迄)
		if (!StringUtils.isBlank(inputVO_CRM233.getStop_loss_pt_end())) {
			inWhere.append(" AND M.TAKE_PRFT_PT <= :stop_loss_pt_end ");
			queryCondition.setObject("stop_loss_pt_end", new BigDecimal(inputVO_CRM233.getStop_loss_pt_end()));
		}
	}

	/** 海外股票 **/
	public void genCRM234(Map<SqlGen, StringBuilder> sqlGenMap, CRM230_ALLInputVO inputVO_all, QueryConditionIF queryCondition) {
		StringBuilder outSideColumn = sqlGenMap.get(SqlGen.OUT_SIDE_COLUMN);
		StringBuilder outSideJoinTable = sqlGenMap.get(SqlGen.OUT_SIDE_TABLE);
		StringBuilder outSideWhere = sqlGenMap.get(SqlGen.OUT_SIDE_WHERE);
		CRM234InputVO inputVO_CRM234 = inputVO_all.getCrm234inputVO();

		outSideColumn.append(" M.ORG_STK_PRICE,  ");
		outSideColumn.append(" M.PROD_NAME,  ");
		outSideColumn.append(" M.CRCY_TYPE,  ");
		outSideColumn.append(" M.ORG_STK_PRICE * M.STK_AMT INV_AMT,  ");
		outSideColumn.append(" P.CURRENCY_STD_ID,  ");
		outSideColumn.append(" M.ACUM_STK_AMT,  ");
		outSideColumn.append(" M.REF_STK_PRICE * (STK_AMT + ACUM_STK_AMT) NOW_AMT,  ");//原幣參考現值
		outSideColumn.append(" M.REF_STK_PRICE * (STK_AMT + ACUM_STK_AMT) * BR.BUY_RATE NOW_AMT_TWD,  ");//台幣參考現值
		outSideColumn.append(" M.REF_STK_PRICE * (STK_AMT + ACUM_STK_AMT) - M.ORG_STK_PRICE * M.STK_AMT REF_PL,  ");//外幣損益金額
		// 台幣參考金額 -  原始股價  * 股數  *參考匯率
		//outSideColumn.append(" M.REF_AMT_TWD - M.ORG_STK_PRICE * M.STK_AMT * REF_EXCH_RATE REF_PL_TWD,  ");
		outSideColumn.append(" (M.REF_STK_PRICE * (STK_AMT + ACUM_STK_AMT) - M.ORG_STK_PRICE * M.STK_AMT) * BR.BUY_RATE REF_PL_TWD,  ");//台幣損益金額
		outSideColumn.append(" BR.BUY_RATE REF_EXCH_RATE,  ");
		outSideColumn.append(" M.REF_ABS_RET_RATE_TWD,  ");
		outSideColumn.append(" M.TAKE_PRFT_PT,  ");
		outSideColumn.append(" M.STOP_LOSS_PT  ");

		//客戶資產-海外ETFn股票日庫存檔
		outSideJoinTable.append(" INNER JOIN TBCRM_AST_INV_FORD_ETFNSTOCK M ON A.CUST_ID = M.CUST_ID ");
		outSideJoinTable.append(" INNER JOIN TBPRD_STOCK P ON M.PROD_ID = P.PRD_ID ");//要用INNER，不然可能會查到海外股票庫存

		//串IQ053匯率(前日匯率相關的資訊)
		outSideJoinTable.append(" LEFT JOIN ( ");
		outSideJoinTable.append(" 	SELECT CUR_COD , BUY_RATE FROM( ");
		outSideJoinTable.append(" 		SELECT CUR_COD , BUY_RATE FROM TBPMS_IQ053 WHERE (CUR_COD , MTN_DATE) IN( SELECT CUR_COD , MAX(MTN_DATE) FROM TBPMS_IQ053 GROUP BY CUR_COD ) ");
		outSideJoinTable.append(" 	)");
		outSideJoinTable.append(" ) BR ON BR.CUR_COD = M.CRCY_TYPE ");

		//成交日期(起)
		if (inputVO_CRM234.getTxn_date_bgn() != null) {
			outSideWhere.append(" AND TRUNC(M.TXN_DATE) >= :txn_date_bgn ");
			queryCondition.setObject("txn_date_bgn", new Timestamp(inputVO_CRM234.getTxn_date_bgn().getTime()));
		}

		//成交日期(迄)
		if (inputVO_CRM234.getTxn_date_end() != null) {
			outSideWhere.append(" AND TRUNC(M.TXN_DATE) <= :txn_date_end ");
			queryCondition.setObject("txn_date_end", new Timestamp(inputVO_CRM234.getTxn_date_end().getTime()));
		}

		//商品名稱
		if (!StringUtils.isBlank(inputVO_CRM234.getProd_name())) {
			outSideWhere.append(" AND M.PROD_NAME LIKE :prod_name ");
			queryCondition.setObject("prod_name", "%" + inputVO_CRM234.getProd_name() + "%");
		}

		//交易所
		if (!StringUtils.isBlank(inputVO_CRM234.getStock_code())) {
			outSideWhere.append(" AND P.STOCK_CODE = :stock_code ");
			queryCondition.setObject("stock_code", inputVO_CRM234.getStock_code());
		}

		//商品代碼
		if (!StringUtils.isBlank(inputVO_CRM234.getProd_id())) {
			outSideWhere.append(" AND M.PROD_ID LIKE :prod_id ");
			queryCondition.setObject("prod_id", "%" + inputVO_CRM234.getProd_id() + "%");
		}

		//投資標的類型
		if (!StringUtils.isBlank(inputVO_CRM234.getPrd_type())) {
			outSideWhere.append(" AND P.PRD_TYPE = :prd_type ");
			queryCondition.setObject("prd_type", inputVO_CRM234.getPrd_type());
		}

		//商品幣別
		if (!StringUtils.isBlank(inputVO_CRM234.getCurrency_std_id())) {
			outSideWhere.append(" AND P.CURRENCY_STD_ID = :currency_std_id ");
			queryCondition.setObject("currency_std_id", inputVO_CRM234.getCurrency_std_id());
		}

		//投資區域
		if (!StringUtils.isBlank(inputVO_CRM234.getStock_area())) {
			outSideWhere.append(" AND P.STOCK_AREA = :stock_area ");
			queryCondition.setObject("stock_area", inputVO_CRM234.getStock_area());
		}

		//商品風險等級
		if (!StringUtils.isBlank(inputVO_CRM234.getRiskcate_id())) {
			outSideWhere.append(" AND P.RISKCATE_ID = :riskcate_id ");
			queryCondition.setObject("riskcate_id", inputVO_CRM234.getRiskcate_id());
		}

		//商品類型 - 海外股票沒有使用到商品類別
		//    	if (!StringUtils.isBlank(inputVO_CRM234.getStock_attribute())) {
		//    		outSideWhere.append(" AND P.STOCK_ATTRIBUTE = :stock_attribute ");
		//    		queryCondition.setObject("stock_attribute", inputVO_CRM234.getStock_attribute());
		//    	}

		//報酬率(起)
		if (!StringUtils.isBlank(inputVO_CRM234.getRef_abs_ret_rate_twd_bgn())) {
			outSideWhere.append(" AND M.REF_ABS_RET_RATE_TWD >= :ref_abs_ret_rate_twd_bgn ");
			queryCondition.setObject("ref_abs_ret_rate_twd_bgn", new BigDecimal(inputVO_CRM234.getRef_abs_ret_rate_twd_bgn()));
		}

		//報酬率(迄)
		if (!StringUtils.isBlank(inputVO_CRM234.getRef_abs_ret_rate_twd_end())) {
			outSideWhere.append(" AND M.REF_ABS_RET_RATE_TWD <= :ref_abs_ret_rate_twd_end ");
			queryCondition.setObject("ref_abs_ret_rate_twd_end", new BigDecimal(inputVO_CRM234.getRef_abs_ret_rate_twd_end()));
		}

		//專業投資人商品
		if (!StringUtils.isBlank(inputVO_CRM234.getPi_buy())) {
			outSideWhere.append(" AND NVL(P.PI_BUY, 'N') = :pi_buy ");
			queryCondition.setObject("pi_buy", inputVO_CRM234.getPi_buy());
		}

		//客戶停損點(%)(起)
		if (!StringUtils.isBlank(inputVO_CRM234.getStop_loss_pt_bgn())) {
			outSideWhere.append(" AND M.STOP_LOSS_PT >= :stop_loss_pt_bgn ");
			queryCondition.setObject("stop_loss_pt_bgn", new BigDecimal(inputVO_CRM234.getStop_loss_pt_bgn()));
		}

		//客戶停損點(%)(迄)
		if (!StringUtils.isBlank(inputVO_CRM234.getStop_loss_pt_end())) {
			outSideWhere.append(" AND M.STOP_LOSS_PT <= :stop_loss_pt_end ");
			queryCondition.setObject("stop_loss_pt_end", new BigDecimal(inputVO_CRM234.getStop_loss_pt_end()));
		}

		//信託類別
		if (!StringUtils.isBlank(inputVO_CRM234.getCom27())) {
			if ("2".equals(inputVO_CRM234.getCom27())) {
				outSideWhere.append(" AND M.TRST_TYPE_FORD = 'Y' ");
			} else {
				outSideWhere.append(" AND M.TRST_TYPE_FORD = 'N' ");
			}
		}

		//客戶停利點(%)(起)
		if (!StringUtils.isBlank(inputVO_CRM234.getTake_prft_pt_bgn())) {
			outSideWhere.append(" AND M.TAKE_PRFT_PT >= :take_prft_pt_bgn ");
			queryCondition.setObject("take_prft_pt_bgn", new BigDecimal(inputVO_CRM234.getTake_prft_pt_bgn()));
		}

		//客戶停利點(%)(迄)
		if (!StringUtils.isBlank(inputVO_CRM234.getTake_prft_pt_end())) {
			outSideWhere.append(" AND M.TAKE_PRFT_PT <= :take_prft_pt_end ");
			queryCondition.setObject("take_prft_pt_end", new BigDecimal(inputVO_CRM234.getTake_prft_pt_end()));
		}
	}

	/** 海外債 **/
	public void genCRM235(Map<SqlGen, StringBuilder> sqlGenMap, CRM230_ALLInputVO inputVO_all, QueryConditionIF queryCondition) {
		StringBuilder outSideColumn = sqlGenMap.get(SqlGen.OUT_SIDE_COLUMN);
		StringBuilder outSideJoinTable = sqlGenMap.get(SqlGen.OUT_SIDE_TABLE);
		StringBuilder outSideWhere = sqlGenMap.get(SqlGen.OUT_SIDE_WHERE);
		CRM235InputVO inputVO_CRM235 = inputVO_all.getCrm235inputVO();

		outSideColumn.append(" P.BOND_CATE_ID, ");
		outSideColumn.append(" TO_CHAR(DATE_OF_MATURITY, 'YYYY') YEAR_OF_MATURITY, ");
		outSideColumn.append(" M.CERT_NBR, ");
		outSideColumn.append(" M.BOND_NBR, ");
		outSideColumn.append(" NVL(P.BOND_CNAME_A, M.PROD_NAME) PROD_NAME, ");
		outSideColumn.append(" M.VALU_CRCY_TYPE, ");
		outSideColumn.append(" M.DENO_AMT, ");
		outSideColumn.append(" M.INV_AMT_TWD, ");
		outSideColumn.append(" M.REF_PRICE, ");
		outSideColumn.append(" P.RISKCATE_ID, ");
		outSideColumn.append(" PI.INSTITION_OF_FLOTATION, ");
		outSideColumn.append(" V.RTN_RATE_WD ");

		//客戶資產-海外債日庫存檔
		outSideJoinTable.append(" INNER JOIN TBCRM_AST_INV_FBOND M ON A.CUST_ID = M.CUST_ID ");
		//海外債商品主檔
		outSideJoinTable.append(" INNER JOIN TBPRD_BOND P ON M.BOND_NBR = P.PRD_ID ");
		//海外債商品資訊檔
		outSideJoinTable.append(" LEFT JOIN TBPRD_BONDINFO PI ON M.BOND_NBR = PI.PRD_ID ");
		//for海外債投資報酬率
		outSideJoinTable.append(" LEFT JOIN MVCRM_AST_ALLPRD_DETAIL V ON V.AST_TYPE = '10' ");
		outSideJoinTable.append(" AND M.CUST_ID = V.CUST_ID ");
		outSideJoinTable.append(" AND M.BOND_NBR = V.PROD_ID ");
		outSideJoinTable.append(" AND M.CERT_NBR = V.CERT_NBR ");

		//商品幣別
		if (!StringUtils.isBlank(inputVO_CRM235.getCurrency_std_id())) {
			outSideWhere.append(" AND M.VALU_CRCY_TYPE = :currency_std_id ");
			queryCondition.setObject("currency_std_id", inputVO_CRM235.getCurrency_std_id());
		}

		//商品代號
		if (!StringUtils.isBlank(inputVO_CRM235.getBond_nbr())) {
			outSideWhere.append(" AND M.BOND_NBR LIKE :bond_nbr ");
			queryCondition.setObject("bond_nbr", "%" + inputVO_CRM235.getBond_nbr() + "%");
		}

		//到期年度
		if (inputVO_CRM235.getYear_of_maturity() != null) {
			outSideWhere.append(" AND TO_CHAR(DATE_OF_MATURITY, 'YYYY') = TO_CHAR( :year_of_maturity , 'YYYY') ");
			queryCondition.setObject("year_of_maturity", inputVO_CRM235.getYear_of_maturity());
		}

		//商品名稱
		if (!StringUtils.isBlank(inputVO_CRM235.getProd_name())) {
			outSideWhere.append(" AND NVL(P.BOND_CNAME_A, M.PROD_NAME) LIKE '%" + inputVO_CRM235.getProd_name() + "%' ");
		}

		//商品風險等級
		if (!StringUtils.isBlank(inputVO_CRM235.getRiskcate_id())) {
			outSideWhere.append(" AND P.RISKCATE_ID = :riskcate_id ");
			queryCondition.setObject("riskcate_id", inputVO_CRM235.getRiskcate_id());
		}

		//發行機構
		if (!StringUtils.isBlank(inputVO_CRM235.getInstition_of_flotation())) {
			outSideWhere.append(" AND PI.INSTITION_OF_FLOTATION LIKE '%" + inputVO_CRM235.getInstition_of_flotation() + "%' ");
		}

		//債券類型
		if (!StringUtils.isBlank(inputVO_CRM235.getBond_cate_id())) {
			outSideWhere.append(" AND P.BOND_CATE_ID = :bond_cate_id ");
			queryCondition.setObject("bond_cate_id", inputVO_CRM235.getBond_cate_id());
		}
		
		// 含息報酬率(%)(起)
		if (!StringUtils.isBlank(inputVO_CRM235.getRtn_rate_wd_min())) {
			outSideWhere.append(" AND V.RTN_RATE_WD >= :rtn_rate_wd_min ");
			queryCondition.setObject("rtn_rate_wd_min", new BigDecimal(inputVO_CRM235.getRtn_rate_wd_min()));
		}

		// 含息報酬率(%)(迄)
		if (!StringUtils.isBlank(inputVO_CRM235.getRtn_rate_wd_max())) {
			outSideWhere.append(" AND V.RTN_RATE_WD <= :rtn_rate_wd_max ");
			queryCondition.setObject("rtn_rate_wd_max", new BigDecimal(inputVO_CRM235.getRtn_rate_wd_max()));
		}
	}

	/** 組合式商品SI **/
	public void genCRM236(Map<SqlGen, StringBuilder> sqlGenMap, CRM230_ALLInputVO inputVO_all, QueryConditionIF queryCondition) {
		StringBuilder inColumn = sqlGenMap.get(SqlGen.IN_COLUMN);
		StringBuilder inJoinTable = sqlGenMap.get(SqlGen.IN_TABLE);
		StringBuilder inWhere = sqlGenMap.get(SqlGen.IN_WHERE);
		StringBuilder outSideColumn = sqlGenMap.get(SqlGen.OUT_SIDE_COLUMN);
		StringBuilder outSideJoin = sqlGenMap.get(SqlGen.OUT_SIDE_TABLE);
		CRM236InputVO inputVO_CRM236 = inputVO_all.getCrm236inputVO();

		//全商品庫存檢視檔
		outSideColumn.append(" D.NOW_AMT , ");
		outSideColumn.append(" D.T_DIV ");

		outSideJoin.append(" LEFT JOIN MVCRM_AST_ALLPRD_DETAIL D ");
		outSideJoin.append(" ON  AST_TYPE = '15' ");
		outSideJoin.append(" AND A.PROD_ID = D.PROD_ID ");
		outSideJoin.append(" AND A.ACC_NBR = D.CERT_NBR ");
		outSideJoin.append(" AND A.CUST_ID = D.CUST_ID ");
		outSideJoin.append(" AND A.VALUE_DATE = D.DATA_DATE ");
		outSideJoin.append(" AND A.PROD_NAME_SOURCE = D.PROD_NAME ");
		outSideJoin.append(" AND A.CRCY_TYPE = D.CUR_ID ");

		//NOW_AMT 參考現值金額(原幣) 是市值，INV_AMT 原始投資金額(原幣)是面額?(不確定)
		outSideJoin.append(" AND A.PRCH_AMT_ORGD = D.INV_AMT ");
		outSideJoin.append(" AND A.PRCH_AMT_TWD = D.INV_AMT_TWD ");
		//    	outSideJoin.append(" AND A.REF_EXCH_RATE = D.EXCH_RATE ");
		//    	outSideJoin.append(" AND A.CUST_ID = D.CUST_ID ");

		inColumn.append(" M.PROD_ID,  ");
		inColumn.append(" M.ACC_NBR,  ");
		//    	inColumn.append(" M.VALUE_DATE, ");
		inColumn.append(" M.PROD_NAME PROD_NAME_SOURCE, ");
		inColumn.append(" M.CRCY_TYPE, ");
		inColumn.append(" M.PRCH_AMT_ORGD, ");
		inColumn.append(" M.PRCH_AMT_TWD, ");
		inColumn.append(" M.REF_EXCH_RATE, ");
		inColumn.append(" P0.INBOX_NO, ");
		inColumn.append(" NVL(P.SI_CNAME, M.PROD_NAME) PROD_NAME,  ");
		inColumn.append(" PI.INV_SDATE, ");
		inColumn.append(" PP.SDAMT3,  ");
		inColumn.append(" PI.TRANS_DATE,  "); //交易日
		inColumn.append(" PI.VALUE_DATE  "); //起息日

		//客戶資產-外幣組合商品(SI)日庫存檔
		inJoinTable.append(" INNER JOIN TBCRM_AST_INV_SI M ON A.CUST_ID = M.CUST_ID ");
		//SI商品主檔
		inJoinTable.append(" LEFT JOIN TBPRD_SI P ON M.PROD_ID = P.PRD_ID ");
		//SI商品資訊檔
		inJoinTable.append(" LEFT JOIN TBPRD_SIINFO PI ON M.PROD_ID = PI.PRD_ID ");
		//SI參考報價檔 - 取該產品的最新的小額申購
		inJoinTable.append(" LEFT JOIN ( ");
		inJoinTable.append(" 	SELECT SDPRD, SDDTE, SDAMT3 FROM TBPRD_SIPRICE "); //產品編碼、報價日期、小額申購
		inJoinTable.append(" 	WHERE (SDPRD, SDDTE) IN ( ");
		inJoinTable.append(" 		SELECT SDPRD, MAX(SDDTE) FROM TBPRD_SIPRICE GROUP BY SDPRD ");//各產品最新報價日期
		inJoinTable.append(" 	) ");
		inJoinTable.append(" ) PP ON M.PROD_ID = PP.SDPRD ");//相同的產品編碼JOIN起來
		//SI交易明細檔
		inJoinTable.append(" LEFT JOIN TBPMS_WMG_SI_TXN P0 ");
		inJoinTable.append(" ON  M.PROD_ID = P0.PROD_CD ");
		inJoinTable.append(" AND M.CUST_ID = P0.CUST_ID ");
		inJoinTable.append(" AND M.ACC_NBR = P0.ACCT_NO_REG ");
		inJoinTable.append(" AND M.CD_NBR = P0.SAV_NO ");

		//商品幣別
		if (!StringUtils.isBlank(inputVO_CRM236.getCrcy_type())) {
			inWhere.append(" AND M.CRCY_TYPE = :crcy_type ");
			queryCondition.setObject("crcy_type", inputVO_CRM236.getCrcy_type());
		}

		//保本率：	0 = 無保本約定型商品、1 = 保本率 80%(不含 )以下、2 = 保本率 80%(含) 以上、3 = 100% 保本型商品
		if (!StringUtils.isBlank(inputVO_CRM236.getRate_guaranteepay())) {
			inWhere.append(" AND P.RATE_GUARANTEEPAY = :rate ");
			queryCondition.setObject("rate", inputVO_CRM236.getRate_guaranteepay());
		}

		//商品代號
		if (!StringUtils.isBlank(inputVO_CRM236.getProd_id())) {
			inWhere.append(" AND M.PROD_ID LIKE :prod_id ");
			queryCondition.setObject("prod_id", "%" + inputVO_CRM236.getProd_id() + "%");
		}

		//到期年度
		if (inputVO_CRM236.getYear_of_maturity() != null) {
			inWhere.append(" AND TO_CHAR(P.DATE_OF_MATURITY, 'YYYY') = TO_CHAR( :year_of_maturity , 'YYYY') ");
			queryCondition.setObject("year_of_maturity", inputVO_CRM236.getYear_of_maturity());
		}

		//商品名稱
		if (!StringUtils.isBlank(inputVO_CRM236.getProd_name())) {
			inWhere.append(" AND M.PROD_NAME LIKE '%" + inputVO_CRM236.getProd_name() + "%' ");
		}

		//商品風險等級
		if (!StringUtils.isBlank(inputVO_CRM236.getRiskcate_id())) {
			inWhere.append(" AND P.RISKCATE_ID = :riskcate_id ");
			queryCondition.setObject("riskcate_id", inputVO_CRM236.getRiskcate_id());
		}

		//連結標的類型
		if (!StringUtils.isBlank(inputVO_CRM236.getInv_target_type())) {
			inWhere.append(" AND P.INV_TARGET_TYPE = :inv_target_type ");
			queryCondition.setObject("inv_target_type", inputVO_CRM236.getInv_target_type());
		}

		//報價範圍(up)
		if (!StringUtils.isBlank(inputVO_CRM236.getSdamt3_bgn())) {
			inWhere.append(" AND PP.SDAMT3 >= :sdamt3_bgn ");
			queryCondition.setObject("sdamt3_bgn", new BigDecimal(inputVO_CRM236.getSdamt3_bgn()));
		}

		//報價範圍(to)
		if (!StringUtils.isBlank(inputVO_CRM236.getSdamt3_end())) {
			inWhere.append(" AND PP.SDAMT3 <= :sdamt3_end ");
			queryCondition.setObject("sdamt3_end", new BigDecimal(inputVO_CRM236.getSdamt3_end()));
		}

		//幣轉
		if (!StringUtils.isBlank(inputVO_CRM236.getCurrency_exchange())) {
			inWhere.append(" AND PI.CURRENCY_EXCHANGE = :currency_exchange ");
			queryCondition.setObject("currency_exchange", inputVO_CRM236.getCurrency_exchange());
		}
	}

	/** 境外結構性商品(SN) **/
	public void genCRM237(Map<SqlGen, StringBuilder> sqlGenMap, CRM230_ALLInputVO inputVO_all, QueryConditionIF queryCondition) {
		StringBuilder inColumn = sqlGenMap.get(SqlGen.IN_COLUMN);
		StringBuilder inJoinTable = sqlGenMap.get(SqlGen.IN_TABLE);
		StringBuilder inWhere = sqlGenMap.get(SqlGen.IN_WHERE);
		StringBuilder outSideColumn = sqlGenMap.get(SqlGen.OUT_SIDE_COLUMN);
		StringBuilder outSideJoin = sqlGenMap.get(SqlGen.OUT_SIDE_TABLE);
		CRM237InputVO inputVO_CRM237 = inputVO_all.getCrm237inputVO();

		//全商品庫存檢視檔
		outSideColumn.append(" D.T_DIV ");
		//    	
		//    	outSideJoin.append(" LEFT JOIN MVCRM_AST_ALLPRD_DETAIL D ");
		//    	outSideJoin.append(" ON  D.AST_TYPE = '10' ");
		//    	outSideJoin.append(" AND A.BOND_NBR = D.PROD_ID ");
		//    	outSideJoin.append(" AND A.CERT_NBR = D.CERT_NBR ");
		//    	outSideJoin.append(" AND A.CUST_ID = D.CUST_ID ");

		outSideJoin.append(" LEFT JOIN VWCRM_SN_TDIV D ON A.BOND_NBR = D.PRD_ID ");

		inColumn.append(" M.CERT_NBR, ");
		inColumn.append(" M.BOND_NBR, ");
		inColumn.append(" M.PROD_NAME, ");
		inColumn.append(" M.INV_CRCY_TYPE, ");
		inColumn.append(" M.REF_AMT_ORGD, ");
		inColumn.append(" PI.TRANS_DATE DEL_DATE, "); //交易日要吃商品檔資訊 2017-7-19 by Jacky 
		inColumn.append(" M.DENO_AMT, ");
		inColumn.append(" PI.DATE_OF_FLOTATION, ");
		inColumn.append(" PP.SELL_PRICE ");

		//客戶資產-海外債日庫存檔
		inJoinTable.append(" INNER JOIN TBCRM_AST_INV_FBOND M ON A.CUST_ID = M.CUST_ID ");
		//SN商品主檔
		inJoinTable.append(" INNER JOIN TBPRD_SN P ON M.BOND_NBR = P.PRD_ID ");
		//SN商品資訊檔
		inJoinTable.append(" LEFT JOIN TBPRD_SNINFO PI ON M.BOND_NBR = PI.PRD_ID ");
		//海外債參考報價檔
		inJoinTable.append(" LEFT JOIN ( ");
		inJoinTable.append(" 	SELECT * FROM TBPRD_BONDPRICE WHERE (PRD_ID,BARGAIN_DATE) IN( ");
		inJoinTable.append(" 		SELECT PRD_ID,MAX(BARGAIN_DATE) FROM TBPRD_BONDPRICE GROUP BY PRD_ID ");
		inJoinTable.append(" 	) ");
		inJoinTable.append(" ) PP ON M.BOND_NBR = PP.PRD_ID ");

		//商品幣別
		if (!StringUtils.isBlank(inputVO_CRM237.getInv_crcy_type())) {
			inWhere.append(" AND M.INV_CRCY_TYPE = :inv_crcy_type ");
			queryCondition.setObject("inv_crcy_type", inputVO_CRM237.getInv_crcy_type());
		}

		//保本率
		if ("0".equals(inputVO_CRM237.getRate_guaranteepay())) {
			inWhere.append(" AND (P.RATE_GUARANTEEPAY = 0 OR P.RATE_GUARANTEEPAY IS NULL) ");
		}

		if ("1".equals(inputVO_CRM237.getRate_guaranteepay())) {
			inWhere.append(" AND P.RATE_GUARANTEEPAY < 80 ");
		}

		if ("2".equals(inputVO_CRM237.getRate_guaranteepay())) {
			inWhere.append(" AND P.RATE_GUARANTEEPAY >= 80 ");
		}

		if ("3".equals(inputVO_CRM237.getRate_guaranteepay())) {
			inWhere.append(" AND P.RATE_GUARANTEEPAY >= 100 ");
		}

		//商品代號
		if (!StringUtils.isBlank(inputVO_CRM237.getBond_nbr())) {
			inWhere.append(" AND M.BOND_NBR LIKE :bond_nbr ");
			queryCondition.setObject("bond_nbr", "%" + inputVO_CRM237.getBond_nbr() + "%");
		}

		//到期年度
		if (inputVO_CRM237.getYear_of_maturity() != null) {
			inWhere.append(" AND TO_CHAR(P.DATE_OF_MATURITY, 'YYYY') = TO_CHAR( :year_of_maturity , 'YYYY') ");
			queryCondition.setObject("year_of_maturity", inputVO_CRM237.getYear_of_maturity());
		}

		//商品名稱
		if (!StringUtils.isBlank(inputVO_CRM237.getProd_name())) {
			inWhere.append(" AND M.PROD_NAME LIKE :prod_name ");
			queryCondition.setObject("prod_name", "%" + inputVO_CRM237.getProd_name() + "%");
		}

		//商品風險等級
		if (!StringUtils.isBlank(inputVO_CRM237.getRiskcate_id())) {
			inWhere.append(" AND P.RISKCATE_ID = :riskcate_id ");
			queryCondition.setObject("riskcate_id", inputVO_CRM237.getRiskcate_id());
		}

		//連結標的類型
		if (!StringUtils.isBlank(inputVO_CRM237.getInvestment_targets())) {
			inWhere.append(" AND PI.INVESTMENT_TARGETS LIKE :investment_targets ");
			queryCondition.setObject("investment_targets", inputVO_CRM237.getInvestment_targets());
		}

		//報價範圍(起)
		if (!StringUtils.isBlank(inputVO_CRM237.getSell_price_bgn())) {
			inWhere.append(" AND PP.SELL_PRICE >= :sell_price_bgn ");
			queryCondition.setObject("sell_price_bgn", inputVO_CRM237.getSell_price_bgn());
		}

		//報價範圍(訖)
		if (!StringUtils.isBlank(inputVO_CRM237.getSell_price_end())) {
			inWhere.append(" AND PP.SELL_PRICE <= :sell_price_end ");
			queryCondition.setObject("sell_price_end", inputVO_CRM237.getSell_price_end());
		}

		//幣轉
		if (!StringUtils.isBlank(inputVO_CRM237.getCurrency_exchange())) {
			inWhere.append(" AND PI.CURRENCY_EXCHANGE = :currency_exchange ");
			queryCondition.setObject("currency_exchange", inputVO_CRM237.getCurrency_exchange());
		}

		//發行機構
		if (!StringUtils.isBlank(inputVO_CRM237.getInstition_of_flotation())) {
			inWhere.append(" AND PI.INSTITION_OF_FLOTATION LIKE :instition_of_flotation ");
			queryCondition.setObject("instition_of_flotation", "%" + inputVO_CRM237.getInstition_of_flotation() + "%");
		}
	}

	/** DCI 需同步檢查 CRM681.java(資產總覽) & CRM826.java(投資分布) **/
	public void genCRM238(Map<SqlGen, StringBuilder> sqlGenMap, CRM230_ALLInputVO inputVO_all, QueryConditionIF queryCondition) {
		StringBuilder inColumn = sqlGenMap.get(SqlGen.IN_COLUMN);
		StringBuilder inJoinTable = sqlGenMap.get(SqlGen.IN_TABLE);
		StringBuilder inWhere = sqlGenMap.get(SqlGen.IN_WHERE);
		CRM238InputVO inputVO_CRM238 = inputVO_all.getCrm238inputVO();

		inColumn.append(" MO.PROD_ID,  ");
		inColumn.append(" CASE  ");
		inColumn.append(" 	WHEN MO.GRNT_TYPE = '0' THEN '外匯雙享利'  ");
		inColumn.append(" 	ELSE '保值型外匯雙享利'  ");
		inColumn.append(" END AS PROD_NAME,  ");
		inColumn.append(" MO.TXN_AMT,  ");
		inColumn.append(" CASE WHEN MO.CRCY_CHGE_STATUS IS NULL THEN NVL(MO.TXN_AMT,0) ELSE 0 END as TXN_AMT_USE_IN_SUM,  ");//幣別合計用
		inColumn.append(" CASE WHEN MO.CRCY_CHGE_STATUS in ('0','1','2') THEN NVL(MO.DEL_AMT,0) ELSE 0 END as DEL_AMT_USE_IN_SUM,  ");//幣別合計用
		inColumn.append(" MO.VALU_CRCY_TYPE,  ");
		inColumn.append(" MO.MPNG_CRCY_TYPE,  ");
		inColumn.append(" MO.TXN_DATE,  ");
		inColumn.append(" MO.EXPIRY_DATE,  ");
		inColumn.append(" MO.DUE_DATE,  ");
		inColumn.append(" MO.YIELD_RATE,  ");
		inColumn.append(" MO.REF_PROFIT,  ");
		inColumn.append(" MO.CRCY_CHGE_STATUS,  ");
		inColumn.append(" CASE  ");
		inColumn.append(" 	WHEN NVL(MO.CRCY_CHGE_STATUS,'0') IN ('0','2') THEN MO.VALU_CRCY_TYPE ");
		inColumn.append(" 	ELSE MO.MPNG_CRCY_TYPE  ");
		inColumn.append(" END as CRCY,  ");
		inColumn.append(" MO.DEL_AMT  ");

		inJoinTable.append(" INNER JOIN TBCRM_AST_INV_DCI_OTH MO ON A.CUST_ID = MO.CUST_ID ");
		inJoinTable.append(" LEFT JOIN TBCRM_AST_INV_DCI_DIVID_NOW NV ");
		inJoinTable.append(" ON MO.PROD_ID = NV.DCI_CODE AND MO.CUST_ID = NV.CUST_ID AND MO.ACC_NBR = NV.CERT_NBR AND TO_NUMBER(MO.CD_NBR) = NV.DEPOSIT_NBR ");

		//inWhere.append(" AND (MO.DUE_DATE IS NULL OR MO.DUE_DATE >= TRUNC(SYSDATE)) ");

		//商品幣別
		if (!StringUtils.isBlank(inputVO_CRM238.getValu_crcy_type())) {
			inWhere.append(" AND MO.VALU_CRCY_TYPE = :valu_crcy_type ");
			queryCondition.setObject("valu_crcy_type", inputVO_CRM238.getValu_crcy_type());
		}

		//相對幣別
		if (!StringUtils.isBlank(inputVO_CRM238.getMpng_crcy_type())) {
			inWhere.append(" AND MO.MPNG_CRCY_TYPE = :mpng_crcy_type ");
			queryCondition.setObject("mpng_crcy_type", inputVO_CRM238.getMpng_crcy_type());
		}

		//商品代號
		if (!StringUtils.isBlank(inputVO_CRM238.getProd_id())) {
			inWhere.append(" AND MO.PROD_ID LIKE :prod_id ");
			queryCondition.setObject("prod_id", "%" + inputVO_CRM238.getProd_id() + "%");
		}

		//保值/不保值
		if ("Y".equals(inputVO_CRM238.getHedging_yn())) {
			inWhere.append(" AND NVL(MO.GRNT_TYPE, '0') IN ('1', '2', '3') ");
		} else if ("N".equals(inputVO_CRM238.getHedging_yn())) {
			inWhere.append(" AND NVL(MO.GRNT_TYPE, '0') = '0' ");
		}

		//是否轉換
		if ("Y".equals(inputVO_CRM238.getTransfer_yn())) {
			inWhere.append(" AND NVL(MO.CRCY_CHGE_STATUS, '0') in ( '1', '2') ");
		} else if ("N".equals(inputVO_CRM238.getTransfer_yn())) {
			inWhere.append(" AND MO.CRCY_CHGE_STATUS = '0' ");//選「否」，不要SHOW是否幣轉為NULL的資料
		}

		//比價日(起)
		if (inputVO_CRM238.getExpiry_date_bgn() != null) {
			inWhere.append(" AND TRUNC(MO.EXPIRY_DATE) >= :expiry_date_bgn ");
			queryCondition.setObject("expiry_date_bgn", new Timestamp(inputVO_CRM238.getExpiry_date_bgn().getTime()));
		}

		//比價日(迄)
		if (inputVO_CRM238.getExpiry_date_end() != null) {
			inWhere.append(" AND TRUNC(MO.EXPIRY_DATE) <= :expiry_date_end ");
			queryCondition.setObject("expiry_date_end", new Timestamp(inputVO_CRM238.getExpiry_date_end().getTime()));
		}

		//到期日(起)
		if (inputVO_CRM238.getDue_date_bgn() != null) {
			inWhere.append(" AND TRUNC(MO.DUE_DATE) >= :due_date_bgn ");
			queryCondition.setObject("due_date_bgn", new Timestamp(inputVO_CRM238.getDue_date_bgn().getTime()));
		}

		//到期日(迄)
		if (inputVO_CRM238.getDue_date_end() != null) {
			inWhere.append(" AND TRUNC(MO.DUE_DATE) <= :due_date_end ");
			queryCondition.setObject("due_date_end", new Timestamp(inputVO_CRM238.getDue_date_end().getTime()));
		}

		//商品風險等級
		if ("P1".equals(inputVO_CRM238.getRisk_level())) {
			inWhere.append(" AND 1 <> 1 "); //尚無P1資料
		} else if ("P2".equals(inputVO_CRM238.getRisk_level())) {
			inWhere.append(" AND NVL(MO.GRNT_TYPE, '0') = '3' AND MO.VALU_CRCY_TYPE <> 'ZAR' ");//保值商品+非南非幣
		} else if ("P3".equals(inputVO_CRM238.getRisk_level())) {
			inWhere.append(" AND NVL(MO.GRNT_TYPE, '0') = '3' AND MO.VALU_CRCY_TYPE = 'ZAR' "); //保值商品+南非幣
		} else if ("P4".equals(inputVO_CRM238.getRisk_level())) {
			inWhere.append(" AND NVL(MO.GRNT_TYPE, '0') = '0' "); //不保值商品
		}
	}

	/** 保險商品_富邦進件 **/
	public void genCRM239(Map<SqlGen, StringBuilder> sqlGenMap, CRM230_ALLInputVO inputVO_all, QueryConditionIF queryCondition) {
		StringBuilder inColumn = sqlGenMap.get(SqlGen.IN_COLUMN);
		StringBuilder inJoinTable = sqlGenMap.get(SqlGen.IN_TABLE);
		StringBuilder inWhere = sqlGenMap.get(SqlGen.IN_WHERE);
		CRM239InputVO inputVO_CRM239 = inputVO_all.getCrm239inputVO();

		inColumn.append(" M.APPL_NAME, ");
		inColumn.append(" M.POLICY_NBR, ");
		inColumn.append(" M.POLICY_SEQ, ");
		inColumn.append(" M.ID_DUP, ");
		inColumn.append(" M.CONTRACT_STATUS, ");
		inColumn.append(" M.INS_TYPE, ");
		inColumn.append(" M.INS_COMP_NAME, ");
		inColumn.append(" M.CRCY_TYPE, ");
		inColumn.append(" M.POLICY_FULL_NAME AS POLICY_SIMP_NAME, ");
		inColumn.append(" M.ACUM_INS_AMT_ORGD, ");
		inColumn.append(" M.ACUM_PAID_POLICY_FEE, ");
		inColumn.append(" M.TERMI_AMT, ");
		inColumn.append(" M.POLICY_ACTIVE_DATE, ");
		inColumn.append(" M.EMP_NAME AS SERVICE_EMP_NAME, ");
		inColumn.append(" M.SP_POLICY_NOTE ");

		inJoinTable.append(" LEFT JOIN TBCRM_AST_INS_MAST M ON A.CUST_ID = M.CUST_ID ");
		inJoinTable.append(" LEFT JOIN TBPRD_INSINFO I ON M.INS_TYPE = I.PRD_ID ");
		//29190306, steven, 因標的代碼會有多筆，造成畫面上累計已繳保費加總錯誤,調整不用Join改至where條件。
		//inJoinTable.append(" LEFT JOIN TBCRM_AST_INS_TARGET T ON M.ID_DUP = T.ID_DUP ");
		//inJoinTable.append(" AND M.POLICY_NBR = T.POLICY_NO AND M.POLICY_SEQ = T.POLICY_SEQ ");
		inJoinTable.append(" JOIN TBCRM_NPOLM TN ON M.POLICY_NBR = TN.POLICY_NO ");
		inJoinTable.append(" AND M.POLICY_SEQ = TN.POLICY_SEQ AND M.ID_DUP = TN.ID_DUP ");

		inWhere.append(" AND TN.IS_CONSENT = 'Y' ");
		inWhere.append(" AND TN.POLICY_STATUS NOT IN ( ");
		inWhere.append(" '06','11','12','13','14','15','17','20','21','22','23','24','25','26','27','28','29','30','31','32') ");

		//保險代碼
		if (!StringUtils.isBlank(inputVO_CRM239.getPrd_id())) {
			inWhere.append(" AND M.PRD_ID LIKE :prod_id ");
			queryCondition.setObject("prod_id", "%" + inputVO_CRM239.getPrd_id().toUpperCase() + "%");
		}

		//幣別
		if (StringUtils.isNotBlank(inputVO_CRM239.getCrcy_type())) {
			//客戶篩選BY 保險商品，幣別選擇為台幣，
			//保單契約狀態=01有效,02催告,04繳清,05展期,06主被保人死亡,07給付殘扶金,10停效,16自動墊繳才需要呈現，非上述狀態均不需呈現
			if ("TWD".equals(inputVO_CRM239.getCrcy_type())) {
				inWhere.append(" AND M.CONTRACT_STATUS in ('01', '02', '04', '05', '06', '07', '10', '16') ");
			}

			inWhere.append(" AND M.CRCY_TYPE = :crcy_type ");
			queryCondition.setObject("crcy_type", inputVO_CRM239.getCrcy_type());
		}

		//保險名稱
		if (!StringUtils.isBlank(inputVO_CRM239.getProd_name())) {
			inWhere.append(" AND POLICY_SIMP_NAME LIKE '%" + inputVO_CRM239.getProd_name() + "%' ");
		}

		//檔期
		if (!StringUtils.isBlank(inputVO_CRM239.getTerm_cnt())) {
			inWhere.append(" AND M.TERM_CNT = :term_cnt ");
			queryCondition.setObject("term_cnt", inputVO_CRM239.getTerm_cnt());
		}

		//投資型連結標的代碼
		if (!StringUtils.isBlank(inputVO_CRM239.getInv_target_no())) {
			//		//29190306, steven, 因標的代碼會有多筆，造成畫面上累計已繳保費加總錯誤,調整不用Join改查詢語法。
			//inWhere.append(" AND T.INV_TARGET_NO LIKE '%" + inputVO_CRM239.getInv_target_no().toUpperCase() + "%' ");
			inWhere.append(" AND M.POLICY_NBR IN ( select POLICY_NO FROM TBCRM_AST_INS_TARGET where INV_TARGET_NO like '%" + inputVO_CRM239.getInv_target_no().toUpperCase() + "%' ) ");
		}

		//生效日-起
		if (inputVO_CRM239.getPolicy_active_date_s() != null) {
			inWhere.append(" AND TRUNC(M.POLICY_ACTIVE_DATE) >= :policy_active_date_s ");
			queryCondition.setObject("policy_active_date_s", new Timestamp(inputVO_CRM239.getPolicy_active_date_s().getTime()));
		}

		//生效日-迄
		if (inputVO_CRM239.getPolicy_active_date_e() != null) {
			inWhere.append(" AND TRUNC(M.POLICY_ACTIVE_DATE) <= :policy_active_date_e ");
			queryCondition.setObject("policy_active_date_e", new Timestamp(inputVO_CRM239.getPolicy_active_date_e().getTime()));
		}
		
		// 險種類別
		if (!StringUtils.isBlank(inputVO_CRM239.getIns_type())) {
			inWhere.append(" AND I.INS_TYPE = :ins_type ");
			queryCondition.setObject("ins_type", inputVO_CRM239.getIns_type());
		}
	}
	
	/** 保險商品_日盛銀進件 **/
	public void genCRM239_JSB(Map<SqlGen, StringBuilder> sqlGenMap, CRM230_ALLInputVO inputVO_all, QueryConditionIF queryCondition) {
		StringBuilder inColumn = sqlGenMap.get(SqlGen.IN_COLUMN);
		StringBuilder inJoinTable = sqlGenMap.get(SqlGen.IN_TABLE);
		StringBuilder inWhere = sqlGenMap.get(SqlGen.IN_WHERE);
		CRM239InputVO inputVO_CRM239 = inputVO_all.getCrm239inputVO();

		inColumn.append(" M.APPL_NAME, ");
		inColumn.append(" M.POLICY_NBR, ");
		inColumn.append(" M.POLICY_SEQ, ");
		inColumn.append(" M.ID_DUP, ");
		inColumn.append(" M.CONTRACT_TEXT AS CONTRACT_STATUS, ");
		inColumn.append(" M.INS_TYPE, ");
		inColumn.append(" M.INS_COMP_NAME, ");
		inColumn.append(" M.CRCY_TYPE, ");
		inColumn.append(" M.POLICY_FULL_NAME AS POLICY_SIMP_NAME, ");
		inColumn.append(" M.ACUM_INS_AMT_ORGD, ");
		inColumn.append(" M.ACUM_PAID_POLICY_FEE, ");
		inColumn.append(" M.TERMI_AMT, ");
		inColumn.append(" M.POLICY_ACTIVE_DATE, ");
		inColumn.append(" M.EMP_NAME AS SERVICE_EMP_NAME, ");
		inColumn.append(" M.SP_POLICY_NOTE ");

		inJoinTable.append(" LEFT JOIN TBJSB_AST_INS_MAST M ON A.CUST_ID = M.CUST_ID ");

		//BILL_REMRK空白才為有效保單
		inWhere.append(" AND A.CUST_ID IN (SELECT CUST_ID FROM TBJSB_AST_INS_MAST WHERE TX_SRC IN (SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'IOT.JSB_TX_SRC') AND BILL_REMRK IS NULL) ");
		
		//保險代碼
		if (!StringUtils.isBlank(inputVO_CRM239.getPrd_id())) {
			inWhere.append(" AND M.PRD_ID LIKE :prod_id ");
			queryCondition.setObject("prod_id", "%" + inputVO_CRM239.getPrd_id().toUpperCase() + "%");
		}

		//幣別
		if (StringUtils.isNotBlank(inputVO_CRM239.getCrcy_type())) {
			inWhere.append(" AND M.CRCY_TYPE = :crcy_type ");
			queryCondition.setObject("crcy_type", inputVO_CRM239.getCrcy_type());
		}

		//保險名稱
		if (!StringUtils.isBlank(inputVO_CRM239.getProd_name())) {
			inWhere.append(" AND M.POLICY_SIMP_NAME LIKE '%" + inputVO_CRM239.getProd_name() + "%' ");
		}

		//檔期
		if (!StringUtils.isBlank(inputVO_CRM239.getTerm_cnt())) {
			inWhere.append(" AND M.TERM_CNT = :term_cnt ");
			queryCondition.setObject("term_cnt", inputVO_CRM239.getTerm_cnt());
		}

		//投資型連結標的代碼
		//目前沒有日盛保險庫存標的檔
//		if (!StringUtils.isBlank(inputVO_CRM239.getInv_target_no())) {
//			//		//29190306, steven, 因標的代碼會有多筆，造成畫面上累計已繳保費加總錯誤,調整不用Join改查詢語法。
//			//inWhere.append(" AND T.INV_TARGET_NO LIKE '%" + inputVO_CRM239.getInv_target_no().toUpperCase() + "%' ");
//			inWhere.append(" AND M.POLICY_NBR IN ( select POLICY_NO FROM TBCRM_AST_INS_TARGET where INV_TARGET_NO like '%" + inputVO_CRM239.getInv_target_no().toUpperCase() + "%' ) ");
//		}

		//生效日-起
		if (inputVO_CRM239.getPolicy_active_date_s() != null) {
			inWhere.append(" AND TRUNC(M.POLICY_ACTIVE_DATE) >= :policy_active_date_s ");
			queryCondition.setObject("policy_active_date_s", new Timestamp(inputVO_CRM239.getPolicy_active_date_s().getTime()));
		}

		//生效日-迄
		if (inputVO_CRM239.getPolicy_active_date_e() != null) {
			inWhere.append(" AND TRUNC(M.POLICY_ACTIVE_DATE) <= :policy_active_date_e ");
			queryCondition.setObject("policy_active_date_e", new Timestamp(inputVO_CRM239.getPolicy_active_date_e().getTime()));
		}
		
		//保險公司
		if (!StringUtils.isBlank(inputVO_CRM239.getIns_companyNum())) {
			inWhere.append(" AND TRUNC(M.COM_ID) = :comId ");
			queryCondition.setObject("comId", inputVO_CRM239.getIns_companyNum());
		}
		
	}

	/** 定存 **/
	public void genCRM2310(Map<SqlGen, StringBuilder> sqlGenMap, CRM230_ALLInputVO inputVO_all, QueryConditionIF queryCondition) {
		StringBuilder inColumn = sqlGenMap.get(SqlGen.IN_COLUMN);
		StringBuilder inJoinTable = sqlGenMap.get(SqlGen.IN_TABLE);
		StringBuilder inWhere = sqlGenMap.get(SqlGen.IN_WHERE);

		StringBuilder outSideColumn = sqlGenMap.get(SqlGen.OUT_SIDE_COLUMN);
		StringBuilder outSideJoinTable = sqlGenMap.get(SqlGen.OUT_SIDE_TABLE);
		StringBuilder outSideWhere = sqlGenMap.get(SqlGen.OUT_SIDE_WHERE);
		CRM2310InputVO inputVO_CRM2310 = inputVO_all.getCrm2310inputVO();

		inColumn.append(" C.CRCY_TYPE, C.VALUE_DATE, C.CD_AMT, C.DUE_DATE, C.DTL_TYPE, C.PROD_TYPE, ");
		inColumn.append(" BR.BUY_RATE REF_EXCH_RATE, C.AUTO_RLOR_YN, C.INT_TYPE, C.INT_RATE, C.ACC_NBR ");

		inJoinTable.append(" LEFT JOIN ( ");
		inJoinTable.append(" 	SELECT CUST_ID, 'TWD' AS CRCY_TYPE, VALUE_DATE, CD_AMT, DUE_DATE, '庫存' AS DTL_TYPE, ");
		inJoinTable.append(" 	'台幣定存' AS PROD_TYPE, BRA_NBR, 1.0 AS REF_EXCH_RATE, AUTO_RLOR_YN, INT_RATE_TYPE AS INT_TYPE, ");
		inJoinTable.append(" 	INT_RATE, ACC_NBR FROM TBCRM_AST_DEP_CD ");
		inJoinTable.append(" 	UNION ALL ");
		inJoinTable.append(" 	SELECT CUST_ID, CRCY_TYPE, VALUE_DATE, CD_AMT_ORGD AS CD_AMT, DUE_DATE, ");
		inJoinTable.append(" 	'庫存' AS DTL_TYPE, '外幣定存' AS PROD_TYPE, BRA_NBR, REF_EXCH_RATE, AUTO_RLOR_YN, ");
		inJoinTable.append("	INT_TYPE, INT_RATE, ACC_NBR FROM TBCRM_AST_DEP_FCD ");
		inJoinTable.append(" ) C ON A.CUST_ID = C.CUST_ID ");

		inJoinTable.append(" LEFT JOIN ( ");
		inJoinTable.append(" 	SELECT CUR_COD , BUY_RATE FROM( ");
		inJoinTable.append(" 		SELECT CUR_COD , BUY_RATE FROM TBPMS_IQ053 WHERE (CUR_COD , MTN_DATE) IN( SELECT CUR_COD , MAX(MTN_DATE) FROM TBPMS_IQ053 GROUP BY CUR_COD ) ");
		inJoinTable.append(" 	)");
		inJoinTable.append(" ) BR ON BR.CUR_COD = C.CRCY_TYPE ");

		/**
		 * 
		 inWhere.append(" AND TN.IS_CONSENT = 'Y' ");
		 * inWhere.append(" AND TN.POLICY_STATUS NOT IN( "); inWhere.append(
		 * " 	'06','11','12','13','14','15','17','20','21','22','23','24','25','26','27','28','29','30','31','32' "
		 * ); inWhere.append(" ) ");
		 */
		//商品類別
		if (!StringUtils.isBlank(inputVO_CRM2310.getCrcy_type())) {
			inWhere.append(" and C.CRCY_TYPE = :crcy_type ");
			queryCondition.setObject("crcy_type", inputVO_CRM2310.getCrcy_type());
		}

		//存單金額(起)
		if (!StringUtils.isBlank(inputVO_CRM2310.getCd_samt())) {
			inWhere.append(" and C.CD_AMT >= :cd_samt ");
			queryCondition.setObject("cd_samt", new BigDecimal(inputVO_CRM2310.getCd_samt()));
		}

		//存單金額(迄)
		if (!StringUtils.isBlank(inputVO_CRM2310.getCd_eamt())) {
			inWhere.append(" and C.CD_AMT <= :cd_eamt ");
			queryCondition.setObject("cd_eamt", new BigDecimal(inputVO_CRM2310.getCd_eamt()));
		}

		//起息日(起)
		if (inputVO_CRM2310.getValue_sDate() != null) {
			inWhere.append(" and TRUNC(C.VALUE_DATE) >= :astart ");
			queryCondition.setObject("astart", new Timestamp(inputVO_CRM2310.getValue_sDate().getTime()));
		}

		//起息日(迄)
		if (inputVO_CRM2310.getValue_eDate() != null) {
			inWhere.append(" and TRUNC(C.VALUE_DATE) <= :aend ");
			queryCondition.setObject("aend", new Timestamp(inputVO_CRM2310.getValue_eDate().getTime()));
		}

		//到期日(起)
		if (inputVO_CRM2310.getDue_sDate() != null) {
			inWhere.append(" and TRUNC(C.DUE_DATE) >= :bstart ");
			queryCondition.setObject("bstart", new Timestamp(inputVO_CRM2310.getDue_sDate().getTime()));
		}

		//到期日(迄)
		if (inputVO_CRM2310.getDue_eDate() != null) {
			inWhere.append(" and TRUNC(C.DUE_DATE) <= :bend ");
			queryCondition.setObject("bend", new Timestamp(inputVO_CRM2310.getDue_eDate().getTime()));
		}
	}
	
	/** 消金商品 **/
	public void genCRM2311(Map<SqlGen, StringBuilder> sqlGenMap, CRM230_ALLInputVO inputVO_all, QueryConditionIF queryCondition) {
		StringBuilder inColumn = sqlGenMap.get(SqlGen.IN_COLUMN);
		StringBuilder inJoinTable = sqlGenMap.get(SqlGen.IN_TABLE);
		StringBuilder inWhere = sqlGenMap.get(SqlGen.IN_WHERE);

//		StringBuilder outSideColumn = sqlGenMap.get(SqlGen.OUT_SIDE_COLUMN);
//		StringBuilder outSideJoinTable = sqlGenMap.get(SqlGen.OUT_SIDE_TABLE);
//		StringBuilder outSideWhere = sqlGenMap.get(SqlGen.OUT_SIDE_WHERE);
		
		CRM2311InputVO inputVO_CRM2311 = inputVO_all.getCrm2311inputVO();

		inColumn.append(" C.DEBIT_TYPE, C.LOAN_ACCT, C.CURRENCY_ID, C.ORI_AMT, C.LOAN_BAL, C.INT_RATE, ");
		inColumn.append(" C.DUE_DATE, C.CD_ACCT, C.CD_SLIP_NO, C.RETENTION_RATE, C.WITHHOLDING_ACCT, C.SEMESTER, C.LIB_REMARK ");

		inJoinTable.append(" INNER JOIN TBCRM_AST_LIB_DTL C ON A.CUST_ID = C.CUST_ID ");

		// 負債類型 1:房貸 2:信貸 3:留貸 4:學貸 (5:綜定質借 --不需要) 6:存單質借 7:信託質借
		if (!StringUtils.isBlank(inputVO_CRM2311.getDebit_type())) {
			inWhere.append(" and C.DEBIT_TYPE = :debit_type ");
			queryCondition.setObject("debit_type", inputVO_CRM2311.getDebit_type());				
		}

		// 幣別
		if (!StringUtils.isBlank(inputVO_CRM2311.getCurrency_id())) {
			inWhere.append(" and C.CURRENCY_ID = :currency_id ");
			queryCondition.setObject("currency_id", inputVO_CRM2311.getCurrency_id());
		}

		// 原貸金額/訂約金額
		if (!StringUtils.isBlank(inputVO_CRM2311.getOri_amt_min())) {
			inWhere.append(" and C.ORI_AMT >= :ori_amt_min ");
			queryCondition.setObject("ori_amt_min", new BigDecimal(inputVO_CRM2311.getOri_amt_min()));
		}
		
		// 原貸金額/訂約金額
		if (!StringUtils.isBlank(inputVO_CRM2311.getOri_amt_max())) {
			inWhere.append(" and C.ORI_AMT <= :ori_amt_max ");
			queryCondition.setObject("ori_amt_max", new BigDecimal(inputVO_CRM2311.getOri_amt_max()));
		}

		// 貸款餘額/已透支金額
		if (!StringUtils.isBlank(inputVO_CRM2311.getLoan_bal_min())) {
			inWhere.append(" and C.LOAN_BAL >= :loan_bal_min ");
			queryCondition.setObject("loan_bal_min", new BigDecimal(inputVO_CRM2311.getLoan_bal_min()));
		}

		// 貸款餘額/已透支金額
		if (!StringUtils.isBlank(inputVO_CRM2311.getLoan_bal_max())) {
			inWhere.append(" and C.LOAN_BAL <= :loan_bal_max ");
			queryCondition.setObject("loan_bal_max", new BigDecimal(inputVO_CRM2311.getLoan_bal_max()));
		}

		// 適用利率
		if (!StringUtils.isBlank(inputVO_CRM2311.getInt_rate_min())) {
			inWhere.append(" and C.INT_RATE >= :int_rate_min ");
			queryCondition.setObject("int_rate_min", new BigDecimal(inputVO_CRM2311.getInt_rate_min()));
		}

		// 適用利率
		if (!StringUtils.isBlank(inputVO_CRM2311.getInt_rate_max())) {
			inWhere.append(" and C.INT_RATE <= :int_rate_max ");
			queryCondition.setObject("int_rate_max", new BigDecimal(inputVO_CRM2311.getInt_rate_max()));
		}

		//到期日(起)
		if (inputVO_CRM2311.getDue_sDate() != null) {
			inWhere.append(" and TRUNC(C.DUE_DATE) >= :due_sDate ");
			queryCondition.setObject("due_sDate", new Timestamp(inputVO_CRM2311.getDue_sDate().getTime()));
		}

		//到期日(迄)
		if (inputVO_CRM2311.getDue_eDate() != null) {
			inWhere.append(" and TRUNC(C.DUE_DATE) <= :due_eDate ");
			queryCondition.setObject("due_eDate", new Timestamp(inputVO_CRM2311.getDue_eDate().getTime()));
		}
		
		// 擔保維持率
		if (!StringUtils.isBlank(inputVO_CRM2311.getRetention_rate_min())) {
			inWhere.append(" and C.RETENTION_RATE >= :retention_rate_min ");
			queryCondition.setObject("retention_rate_min", new BigDecimal(inputVO_CRM2311.getRetention_rate_min()));
		}

		// 擔保維持率
		if (!StringUtils.isBlank(inputVO_CRM2311.getRetention_rate_max())) {
			inWhere.append(" and C.RETENTION_RATE <= :retention_rate_max ");
			queryCondition.setObject("retention_rate_max", new BigDecimal(inputVO_CRM2311.getRetention_rate_max()));
		}
	}

	/*
	 * 轉Decimal
	 * 
	 * 2016-10-11 add by walala
	 * 2016-12-01 review code by ocean
	 * 
	 */
	public BigDecimal getBigDecimal(Object value) {

		BigDecimal ret = null;
		if (value != null) {
			if (value instanceof BigDecimal) {
				ret = (BigDecimal) value;
			} else if (value instanceof String) {
				ret = new BigDecimal((String) value);
			} else if (value instanceof BigInteger) {
				ret = new BigDecimal((BigInteger) value);
			} else if (value instanceof Number) {
				ret = new BigDecimal(((Number) value).doubleValue());
			} else {
				throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass() + " into a BigDecimal.");
			}
		}

		return ret;
	}
}