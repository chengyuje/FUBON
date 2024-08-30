package com.systex.jbranch.app.server.fps.pms346;

import java.sql.Clob;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.fubon.bth.job.business_logic.RptCommander;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :非新契約照會日報<br>
 * Comments Name : PMS346java<br>
 * Author : Kevin<br>
 * Date :2016/06/29 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */

@Component("pms346")
@Scope("request")
public class PMS346 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS346.class);

	/**
	 * 匯出確認NULL
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			if (key == "POLICY_NO") {
				return String.valueOf(map.get(key)) + "-"
						+ String.valueOf(map.get("SEQ")) + "-"
						+ String.valueOf(map.get("ID_DUP"));
			}
			return (String.valueOf(map.get(key)) == "null") ? "0" : String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	/**
	 * 匯出
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException 
	 */
	public void export(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS346InputVO inputVO = (PMS346InputVO) body;
		PMS346OutputVO outputVO = new PMS346OutputVO();
		
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);         	//理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);     	//OP
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);    	//個金主管
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);   //營運督導
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);   	//區域中心主管
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
			
		//取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getReportDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ROWNUM AS NUM, M.* FROM ");
		sql.append("(SELECT NVL(TO_CHAR(TO_DATE(N.REPLY_DT, 'YYYYMMDD'), 'YYYY/MM/DD'), ' ') AS REPLY_DT, ");
		sql.append("TO_CHAR(A.NOTE_DATE, 'YYYY/MM/DD') AS NOTE_DATE_S, A.*,  ");
		sql.append("(A.POLICY_NO ||'-'|| TO_CHAR(A.SEQ) || CASE WHEN A.ID_DUP <> ' ' THEN '-' || A.ID_DUP END ) AS POLICY_NO_2, ");
		sql.append("A.CREATETIME AS CDATE, T.PARAM_NAME ");
		sql.append("FROM TBPMS_DAILY_CONTRACT A ");
		sql.append("LEFT JOIN (SELECT * FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PMS.NOTE_TYPE' ) T ");
		sql.append("ON T.PARAM_CODE = A.NOTE_TYPE ");
		sql.append("LEFT JOIN TBPMS_NUNAD N ON A.N_SEQ = N.N_SEQ ");
		sql.append("WHERE 1 = 1 ");
		
		// ==匯出查詢條件==
		// 區域中心
		if (StringUtils.isNotBlank(inputVO.getRegion())) {
			//sql.append(" AND A.REGION_CENTER_ID = :region_center_id ");
			sql.append("  and A.BRANCH_NBR IN ( ");
			sql.append("    SELECT BRANCH_NBR ");
			sql.append("    FROM VWORG_DEFN_BRH ");
			sql.append("    WHERE DEPT_ID = :region_center_id ");
			sql.append("  ) ");
			condition.setObject("region_center_id", inputVO.getRegion());
		} else {
			// 登入非總行人員強制加區域中心
			if (!headmgrMap.containsKey(roleID)) {
				//sql.append(" AND A.REGION_CENTER_ID IN (:region_center_id) ");
				sql.append("  and A.BRANCH_NBR IN ( ");
				sql.append("    SELECT BRANCH_NBR ");
				sql.append("    FROM VWORG_DEFN_BRH ");
				sql.append("    WHERE DEPT_ID IN (:region_center_id) ");
				sql.append("  ) ");
				condition.setObject("region_center_id", pms000outputVO.getV_regionList());
			}
		}
		// 營運區
		if (StringUtils.isNotBlank(inputVO.getOp())) {
			//sql.append(" AND A.BRANCH_AREA_ID = :branch_area_id ");
			sql.append("  and A.BRANCH_NBR IN ( ");
			sql.append("    SELECT BRANCH_NBR ");
			sql.append("    FROM VWORG_DEFN_BRH ");
			sql.append("    WHERE DEPT_ID = :branch_area_id ");
			sql.append("  ) ");
			condition.setObject("branch_area_id", inputVO.getOp());
		} else {
			// 登入非總行人員強制加營運區
			if (!headmgrMap.containsKey(roleID)) {
				//sql.append(" AND A.BRANCH_AREA_ID IN (:branch_area_id) ");
				sql.append("  and A.BRANCH_NBR IN ( ");
				sql.append("    SELECT BRANCH_NBR ");
				sql.append("    FROM VWORG_DEFN_BRH ");
				sql.append("    WHERE DEPT_ID IN (:branch_area_id) ");
				sql.append("  ) ");
				condition.setObject("branch_area_id", pms000outputVO.getV_areaList());
			}
		}
		// 分行
		if (StringUtils.isNotBlank(inputVO.getBranch())) {
			sql.append(" AND A.BRANCH_NBR = :branch_nbr ");
			condition.setObject("branch_nbr", inputVO.getBranch());
		} else {
			// 登入非總行人員強制加分行
			if (!headmgrMap.containsKey(roleID)) {
				sql.append(" AND A.BRANCH_NBR IN (:branch_nbr) ");
				condition.setObject("branch_nbr", pms000outputVO.getV_branchList());
			}
		}
		// 員編
		if (StringUtils.isNotBlank(inputVO.getAo_code())) {
			sql.append(" AND A.AO_CODE LIKE :ao_code ");
			condition.setObject("ao_code", inputVO.getAo_code());
		} else {
			// 登入為銷售人員強制加AO_CODE
			if (fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
				sql.append(" AND A.AO_CODE IN (:ao_code) ");
				condition.setObject("ao_code", pms000outputVO.getV_aoList());
			}
		}
		/*** 以下是序號-保單序號-ID ***/
		if (!StringUtils.isBlank(inputVO.getPOLICY_NO())) {
			sql.append(" AND POLICY_NO LIKE :POLICY_NOO");
			condition.setObject("POLICY_NOO", "%" + inputVO.getPOLICY_NO() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getNum())) {
			sql.append(" AND SEQ LIKE :SEQQ");
			condition.setObject("SEQQ", "%" + inputVO.getNum() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getID_DUP())) {
			sql.append(" AND ID_DUP LIKE :ID_DUPP");
			condition.setObject("ID_DUPP", "%" + inputVO.getID_DUP() + "%");
		}
		/***** END *******/
		
		if (!StringUtils.isBlank(inputVO.getClas())) {
			sql.append(" AND NOTE_TYPE LIKE :NOTE_TYPEE");
			condition .setObject("NOTE_TYPEE", "%" + inputVO.getClas() + "%");
		}
		// 確認type
		if (!StringUtils.isBlank(inputVO.getType())) {
			if (inputVO.getType().matches("^02"))
				sql.append(" AND STATUS LIKE :STATUSS");
			else
				sql.append(" AND STATUS NOT LIKE :STATUSS");
			condition.setObject("STATUSS", "%" + "03" + "%");
		}

		if (!StringUtils.isBlank(inputVO.getId())) {
			sql.append(" AND PROPOSER_ID LIKE :PROPOSER_IDD");
			condition.setObject("PROPOSER_IDD", "%" + inputVO.getId() + "%");
		}
		if (inputVO.getsCreDate() != null) {
			sql.append(" AND NOTE_DATE >= :crestart ");
			condition.setObject("crestart", new Timestamp(inputVO.getsCreDate().getTime()));
		}

		if (inputVO.geteCreDate() != null) {
			sql.append(" AND NOTE_DATE <= :creend ");
			condition.setObject("creend", new Timestamp(inputVO.geteCreDate().getTime()));
		}

		if (inputVO.getsCreDate2() != null) {
			sql.append(" AND REPLY_DATE >= :crestart2 ");
			condition.setObject("crestart2", new Timestamp(inputVO.getsCreDate2().getTime()));
		}

		if (inputVO.geteCreDate2() != null) {
			sql.append(" AND REPLY_DATE <= :creend2 ");
			condition.setObject("creend2", new Timestamp(inputVO.geteCreDate2().getTime()));
		}
		// sql 排序
		sql.append(" ORDER BY A.REGION_CENTER_ID, A.BRANCH_AREA_ID, A.BRANCH_NBR, A.POLICY_NO, A.NOTE_DATE, A.REPLY_DATE, A.CASE_NO, A.NOTE_CODE ) M ");
		condition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(condition);
		try {
			if (list.size() > 0) {
				// gen csv
				String.format("%1$,09d", -3123);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "新契約照會日報" + sdf.format(new Date()) + "-"
						+ getUserVariable(FubonSystemVariableConsts.LOGINID)
						+ "_員工編號.csv";
				List listCSV = new ArrayList();
				for (Map<String, Object> map : list) {
					// 22 column
					String[] records = new String[22];
					int i = 0;
					records[i]   = checkIsNull(map, "NOTE_DATE_S"); 		// 照會日期
					records[++i] = checkIsNull(map, "REGION_CENTER_ID"); 	// 區域中心ID
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); 	// 區域中心名稱
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID"); 		// 營運區ID
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); 	// 營運區名稱
					records[++i] = checkIsNull(map, "BRANCH_NBR"); 			// 分行代碼
					records[++i] = checkIsNull(map, "BRANCH_NAME"); 		// 分行名稱
					records[++i] = checkIsNull(map, "AO_CODE"); 			// AO Code
					records[++i] = checkIsNull(map, "EMP_ID"); 				// 理專員編
					records[++i] = checkIsNull(map, "EMP_NAME"); 			// 理專姓名
					records[++i] = checkIsNull(map, "ATTRACT_ID"); 			// 招攬人ID
					records[++i] = checkIsNull(map, "POLICY_NO"); 			// 保單號碼
					records[++i] = checkIsNull(map, "PRD_NAME"); 			// 商品名稱
					records[++i] = checkIsNull(map, "PROPOSER_ID"); 		// 要保人ID
					records[++i] = checkIsNull(map, "PROPOSER_NAME"); 		// 要保人姓名
					records[++i] = checkIsNull(map, "PARAM_NAME"); 			// 照會類別
					records[++i] = checkIsNull(map, "REPLY_DATE"); 			// 照會回覆截止日
					records[++i] = checkIsNull(map, "NOTE_URL"); 			// 照會內容
					records[++i] = checkIsNull(map, "SUMMARY"); 			// 訪談摘要
					records[++i] = checkIsNull(map, "STATUS"); 				// 處理狀態
					records[++i] = checkIsNull(map, "NOTE_CODE"); 			// 照會碼
					records[++i] = checkIsNull(map, "REPLY_DT"); 			// 照會回覆日

					listCSV.add(records);
				}
				// header
				String[] csvHeader = new String[22];
				int j = 0;
				csvHeader[j]   = "照會日期";
				csvHeader[++j] = "業務處ID";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區ID";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行代碼";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "AO Code";
				csvHeader[++j] = "理專員編";
				csvHeader[++j] = "理專姓名";
				csvHeader[++j] = "招攬人ID";
				csvHeader[++j] = "保單號碼";
				csvHeader[++j] = "商品名稱";
				csvHeader[++j] = "要保人ID";
				csvHeader[++j] = "要保人姓名";
				csvHeader[++j] = "照會類別";
				csvHeader[++j] = "照會回覆截止日";
				csvHeader[++j] = "照會內容";
				csvHeader[++j] = "訪談摘要";
				csvHeader[++j] = "處理狀態";
				csvHeader[++j] = "照會碼";
				csvHeader[++j] = "照會回覆日";
				
				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader);
				csv.addRecordList(listCSV);
				String url = csv.generateCSV();
				// download
				notifyClientToDownloadFile(url, fileName);
			} else 
				outputVO.setResultList(list);
			
			this.sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/**
	 * 主查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException,ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		PMS346InputVO inputVO = (PMS346InputVO) body;
		PMS346OutputVO outputVO = new PMS346OutputVO();
		dam = this.getDataAccessManager();
		Date eDate = inputVO.geteCreDate() != null ? inputVO.geteCreDate() : new Date();
		
		String roleType = "";
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);         //理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);     //OP
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);    //個金主管
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);   //營運督導
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);   //區域中心主管
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
			
		//取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getReportDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			// 主查詢SQL
			sql.append("SELECT ROWNUM AS NUM,T.* FROM ( ");
			sql.append("	SELECT row_number() over (partition by A.POLICY_NO ,A.SEQ ,A.ID_DUP ,A.NOTE_DATE ,A.CASE_NO ,A.NOTE_TYPE ORDER BY A.NOTE_CODE, A.N_SEQ) RN ");
			sql.append("	,A.NOTE_DATE ");
			sql.append("	,A.C_TYPE ");
			sql.append("	,A.REGION_CENTER_ID ");
			sql.append("	,A.REGION_CENTER_NAME ");
			sql.append("	,A.BRANCH_AREA_ID ");
			sql.append("	,A.BRANCH_AREA_NAME ");
			sql.append("	,A.BRANCH_NBR ");
			sql.append("	,A.BRANCH_NAME ");
			sql.append("	,A.AO_CODE ");
			sql.append("	,A.EMP_ID ");
			sql.append("	,A.EMP_NAME ");
			sql.append("	,A.ATTRACT_ID ");
			sql.append("	,A.POLICY_NO ");
			sql.append("	,A.SEQ ");
			sql.append("	,A.ID_DUP ");
			sql.append("	,A.PRD_NAME ");
			sql.append("	,A.PROPOSER_ID ");
			sql.append("	,A.PROPOSER_NAME ");
			sql.append("	,A.NOTE_TYPE ");
			sql.append("	,A.REPLY_DATE ");
			sql.append("	,A.NOTE_URL ");
			sql.append("	,B.CONTENT ");
			sql.append("	,B.STATUS ");
			sql.append("	,A.VISIT_SEQ ");
			sql.append("	,A.VERSION ");
			sql.append("	,A.CREATOR ");
			sql.append("	,A.MODIFIER ");
			sql.append("	,A.LASTUPDATE ");
			sql.append("	,A.CASE_NO ");
			sql.append("	,A.NOTE_NAME ");
			sql.append("	,A.NOTE_NBR ");
			sql.append("	,(A.POLICY_NO ||'-' || TO_CHAR(A.SEQ) || CASE WHEN A.ID_DUP <> ' ' ");
			sql.append("	THEN '-' || A.ID_DUP END ) AS POLICY_NO_2, ");
			sql.append("	A.CREATETIME AS CDATE, ");
			sql.append("	T.PARAM_NAME ");
			sql.append("	FROM TBPMS_DAILY_CONTRACT A ");
			sql.append("	INNER JOIN ");
			sql.append("		( ");
			sql.append("		SELECT POLICY_NO ");
			sql.append("			,SEQ ");
			sql.append("			,ID_DUP ");
			sql.append("			,NOTE_DATE ");
			sql.append("			,CASE_NO ");
			sql.append("			,NOTE_TYPE ");
			sql.append("			,MIN(STATUS) AS STATUS ");
//			sql.append("			,LISTAGG(CAST(SUMMARY AS VARCHAR(30 CHAR)), ' ') WITHIN GROUP (ORDER BY NOTE_CODE, N_SEQ) AS CONTENT ");
			sql.append("			,XMLAGG(xmlparse(content SUMMARY || ' ') ORDER BY NOTE_CODE, N_SEQ).getclobval() as CONTENT ");
			sql.append("		FROM TBPMS_DAILY_CONTRACT ");
			sql.append("		WHERE NOTE_DATE >= TO_DATE('").append(sdf.format(inputVO.getsCreDate())).append("','YYYYMMDD') ");
			sql.append("		AND NOTE_DATE <= TO_DATE('").append(sdf.format(eDate)).append("','YYYYMMDD') ");
			sql.append("		GROUP BY POLICY_NO ");
			sql.append("			,SEQ ");
			sql.append("			,ID_DUP ");
			sql.append("			,NOTE_DATE ");
			sql.append("            ,CASE_NO ");
			sql.append("            ,NOTE_TYPE ) B ");
			sql.append("		ON B.POLICY_NO = A.POLICY_NO ");
			sql.append("		AND B.SEQ = A.SEQ ");
			sql.append("		AND B.ID_DUP = A.ID_DUP ");
			sql.append("	    AND B.NOTE_DATE = A.NOTE_DATE ");
			sql.append("		AND B.CASE_NO = A.CASE_NO ");
			sql.append("		AND B.NOTE_TYPE = A.NOTE_TYPE ");
			sql.append("	LEFT JOIN TBSYSPARAMETER T ");
			sql.append("	ON T.PARAM_CODE=A.NOTE_TYPE ");
			sql.append("	AND PARAM_TYPE='PMS.NOTE_TYPE' ");
			sql.append("	WHERE A.NOTE_DATE >= TO_DATE('").append(sdf.format(inputVO.getsCreDate())).append("','YYYYMMDD')  "); // START DATE
			sql.append("	AND A.NOTE_DATE <= TO_DATE('").append(sdf.format(eDate)).append("','YYYYMMDD') ) T "); // END DATE
			sql.append("WHERE 1=1 ");
			sql.append("AND T.RN = 1 ");
			
			// 區域中心
			if (StringUtils.isNotBlank(inputVO.getRegion())) {
				//sql.append(" AND T.REGION_CENTER_ID = :REGION_CENTER_IDD ");
				sql.append("  and T.BRANCH_NBR IN ( ");
				sql.append("    SELECT BRANCH_NBR ");
				sql.append("    FROM VWORG_DEFN_BRH ");
				sql.append("    WHERE DEPT_ID = :REGION_CENTER_IDD ");
				sql.append("  ) ");
				condition.setObject("REGION_CENTER_IDD",inputVO.getRegion());
			} else {
				// 登入非總行人員強制加區域中心
				if (!headmgrMap.containsKey(roleID)) {
					//sql.append("and T.REGION_CENTER_ID IN (:REGION_CENTER_IDD) ");
					sql.append("  and T.BRANCH_NBR IN ( ");
					sql.append("    SELECT BRANCH_NBR ");
					sql.append("    FROM VWORG_DEFN_BRH ");
					sql.append("    WHERE DEPT_ID IN (:REGION_CENTER_IDD) ");
					sql.append("  ) ");
					condition.setObject("REGION_CENTER_IDD",pms000outputVO.getV_regionList());
				}
			}
			// 營運區
			if (StringUtils.isNotBlank(inputVO.getOp())) {
				//sql.append(" AND T.BRANCH_AREA_ID = :OP_AREA_IDDD ");
				sql.append("  and T.BRANCH_NBR IN ( ");
				sql.append("    SELECT BRANCH_NBR ");
				sql.append("    FROM VWORG_DEFN_BRH ");
				sql.append("    WHERE DEPT_ID = :OP_AREA_IDDD ");
				sql.append("  ) ");
				condition.setObject("OP_AREA_IDDD", inputVO.getOp());
			} else {
				// 登入非總行人員強制加營運區
				if (!headmgrMap.containsKey(roleID)) {
					//sql.append("and T.BRANCH_AREA_ID IN (:OP_AREA_IDDD) ");
					sql.append("  and T.BRANCH_NBR IN ( ");
					sql.append("    SELECT BRANCH_NBR ");
					sql.append("    FROM VWORG_DEFN_BRH ");
					sql.append("    WHERE DEPT_ID IN (:OP_AREA_IDDD) ");
					sql.append("  ) ");
					condition.setObject("OP_AREA_IDDD",pms000outputVO.getV_areaList());
				}
			}
			// 分行
			if (StringUtils.isNotBlank(inputVO.getBranch())) {
				sql.append(" AND T.BRANCH_NBR = :BRANCH_NBRR ");
				condition.setObject("BRANCH_NBRR", inputVO.getBranch());
			} else {
				// 登入非總行人員強制加分行
				if (!headmgrMap.containsKey(roleID)) {
					sql.append("and T.BRANCH_NBR IN (:BRANCH_NBRR) ");
					condition.setObject("BRANCH_NBRR",pms000outputVO.getV_branchList());
				}
			}
			// 員編
			if (StringUtils.isNotBlank(inputVO.getAo_code())) {
				sql.append(" and T.AO_CODE LIKE :AO_CODEE ");
				condition.setObject("AO_CODEE", inputVO.getAo_code());
			} else {
				// 登入為銷售人員強制加AO_CODE
				if (fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
					sql.append(" and T.AO_CODE IN (:ao_code) ");
					condition.setObject("ao_code", pms000outputVO.getV_aoList());
				}
			}
			/*** 以下是序號-保單序號-ID ***/
			if (!StringUtils.isBlank(inputVO.getPOLICY_NO())) {
				sql.append(" and T.POLICY_NO LIKE :POLICY_NOO");
				condition.setObject("POLICY_NOO", "%" + inputVO.getPOLICY_NO() + "%");
			}
			if (!StringUtils.isBlank(inputVO.getNum())) {
				sql.append(" and T.SEQ LIKE :SEQQ");
				condition.setObject("SEQQ", "%" + inputVO.getNum() + "%");
			}
			if (!StringUtils.isBlank(inputVO.getID_DUP())) {
				sql.append(" and T.ID_DUP LIKE :ID_DUPP");
				condition.setObject("ID_DUPP", "%" + inputVO.getID_DUP() + "%");
			}
			/***** END *******/
			
			if (!StringUtils.isBlank(inputVO.getClas())) {
				sql.append(" and T.NOTE_TYPE LIKE :NOTE_TYPEE");
				condition .setObject("NOTE_TYPEE", "%" + inputVO.getClas() + "%");
			}
			// 確認type
			if (!StringUtils.isBlank(inputVO.getType())) {
				if (inputVO.getType().matches("^02"))
					sql.append(" and T.STATUS LIKE :STATUSS");
				else
					sql.append(" and T.STATUS NOT LIKE :STATUSS");
				condition.setObject("STATUSS", "%" + "03" + "%");
			}
			
			if (!StringUtils.isBlank(inputVO.getId())) {
				sql.append(" and T.PROPOSER_ID LIKE :PROPOSER_IDD");
				condition.setObject("PROPOSER_IDD", "%" + inputVO.getId() + "%");
			}
			
			if (inputVO.getsCreDate2() != null) {
				sql.append("   and T.REPLY_DATE >= :crestart2 ");
				condition.setObject("crestart2", new Timestamp(inputVO.getsCreDate2().getTime()));
			}
			
			if (inputVO.geteCreDate2() != null) {
				sql.append("    and T.REPLY_DATE <= :creend2 ");
				condition.setObject("creend2", new Timestamp(inputVO.geteCreDate2().getTime()));
			}
			
			condition.setQueryString(sql.toString());
			
			ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList(list); // 分頁用
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			
			List<Map<String, Object>> List = dam.exeQuery(condition); // 全部筆數
			outputVO.setList(List); 
			//處理clob轉string
			for(Map map: List) {
				RptCommander rptCommander = (RptCommander) PlatformContext.getBean("rptCommander");
		        String str = rptCommander.getSqlFromClob((Clob) map.get("CONTENT"));
		        map.remove("CONTENT");
		        map.put("CONTENT", str);
			}
//			
//			// ==匯出用SQL==
//			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//			sql = new StringBuffer();
//			
//			sql.append("SELECT ROWNUM AS NUM,T.* FROM ");
//			sql.append("( select a.*,(a.POLICY_NO ||'-'|| TO_CHAR(a.SEQ) || CASE WHEN a.ID_DUP <> ' ' THEN '-' || a.ID_DUP END ) AS POLICY_NO_2,a.CREATETIME AS CDATE, ");
//			sql.append("t.PARAM_name from TBPMS_DAILY_CONTRACT a ");
//			sql.append("left join ");
//			sql.append("(select * from TBSYSPARAMETER where PARAM_TYPE='PMS.NOTE_TYPE' ) t ");
//			sql.append("on t.PARAM_CODE=a.NOTE_TYPE WHERE 1=1 ");
//			// ==匯出查詢條件==
//			// 區域中心
//			if (StringUtils.isNotBlank(inputVO.getRegion())) {
//				sql.append(" AND REGION_CENTER_ID = :REGION_CENTER_IDD ");
//				condition.setObject("REGION_CENTER_IDD",inputVO.getRegion());
//			} else {
//				// 登入非總行人員強制加區域中心
//				if (!headmgrMap.containsKey(roleID)) {
//					sql.append("and REGION_CENTER_ID IN (:REGION_CENTER_IDD) ");
//					condition.setObject("REGION_CENTER_IDD",pms000outputVO.getV_regionList());
//				}
//			}
//			// 營運區
//			if (StringUtils.isNotBlank(inputVO.getOp())) {
//				sql.append(" AND BRANCH_AREA_ID = :OP_AREA_IDDD ");
//				condition.setObject("OP_AREA_IDDD", inputVO.getOp());
//			} else {
//				// 登入非總行人員強制加營運區
//				if (!headmgrMap.containsKey(roleID)) {
//					sql.append("and BRANCH_AREA_ID IN (:OP_AREA_IDDD) ");
//					condition.setObject("OP_AREA_IDDD",pms000outputVO.getV_areaList());
//				}
//			}
//			// 分行
//			if (StringUtils.isNotBlank(inputVO.getBranch())) {
//				sql.append(" AND BRANCH_NBR = :BRANCH_NBRR ");
//				condition.setObject("BRANCH_NBRR", inputVO.getBranch());
//			} else {
//				// 登入非總行人員強制加分行
//				if (!headmgrMap.containsKey(roleID)) {
//					sql.append("and BRANCH_NBR IN (:BRANCH_NBRR) ");
//					condition.setObject("BRANCH_NBRR",pms000outputVO.getV_branchList());
//				}
//			}
//			// 員編
//			if (StringUtils.isNotBlank(inputVO.getAo_code())) {
//				sql.append(" and AO_CODE LIKE :AO_CODEE ");
//				condition.setObject("AO_CODEE", inputVO.getAo_code());
//			} else {
//				// 登入為銷售人員強制加AO_CODE
//				if (fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
//					sql.append(" and AO_CODE IN (:ao_code) ");
//					condition.setObject("ao_code", pms000outputVO.getV_aoList());
//				}
//			}
//			/*** 以下是序號-保單序號-ID ***/
//			if (!StringUtils.isBlank(inputVO.getPOLICY_NO())) {
//				sql.append(" and POLICY_NO LIKE :POLICY_NOO");
//				condition.setObject("POLICY_NOO", "%" + inputVO.getPOLICY_NO() + "%");
//			}
//			if (!StringUtils.isBlank(inputVO.getNum())) {
//				sql.append(" and SEQ LIKE :SEQQ");
//				condition.setObject("SEQQ", "%" + inputVO.getNum() + "%");
//			}
//			if (!StringUtils.isBlank(inputVO.getID_DUP())) {
//				sql.append(" and ID_DUP LIKE :ID_DUPP");
//				condition.setObject("ID_DUPP", "%" + inputVO.getID_DUP() + "%");
//			}
//			/***** END *******/
//			
//			if (!StringUtils.isBlank(inputVO.getClas())) {
//				sql.append(" and NOTE_TYPE LIKE :NOTE_TYPEE");
//				condition .setObject("NOTE_TYPEE", "%" + inputVO.getClas() + "%");
//			}
//			// 確認type
//			if (!StringUtils.isBlank(inputVO.getType())) {
//				if (inputVO.getType().matches("^02"))
//					sql.append(" and STATUS LIKE :STATUSS");
//				else
//					sql.append(" and STATUS NOT LIKE :STATUSS");
//				condition.setObject("STATUSS", "%" + "03" + "%");
//			}
//
//			if (!StringUtils.isBlank(inputVO.getId())) {
//				sql.append(" and PROPOSER_ID LIKE :PROPOSER_IDD");
//				condition.setObject("PROPOSER_IDD", "%" + inputVO.getId() + "%");
//			}
//			if (inputVO.getsCreDate() != null) {
//				sql.append("   and NOTE_DATE >= :crestart ");
//				condition.setObject("crestart", new Timestamp(inputVO.getsCreDate().getTime()));
//			}
//
//			if (inputVO.geteCreDate() != null) {
//				sql.append("   and NOTE_DATE <= :creend ");
//				condition.setObject("creend", new Timestamp(inputVO.geteCreDate().getTime()));
//			}
//
//			if (inputVO.getsCreDate2() != null) {
//				sql.append("   and REPLY_DATE >= :crestart2 ");
//				condition.setObject("crestart2", new Timestamp(inputVO.getsCreDate2().getTime()));
//			}
//
//			if (inputVO.geteCreDate2() != null) {
//				sql.append("    and REPLY_DATE <= :creend2 ");
//				condition.setObject("creend2", new Timestamp(inputVO.geteCreDate2().getTime()));
//			}
//			// sql 排序
//			sql.append(" ORDER BY REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR,POLICY_NO,NOTE_DATE,POLICY_NO,REPLY_DATE,CASE_NO,NOTE_CODE ) T WHERE ROWNUM<=2000");
//			condition.setQueryString(sql.toString());
//			// csv全部查詢結果
//			List<Map<String, Object>> csvList = dam.exeQuery(condition);
//			outputVO.setCsvList(csvList);; // dataF
			
			this.sendRtnObject(outputVO);

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	//加密URL
	public void encoding(Object body, IPrimitiveMap header)throws JBranchException,ParseException{
		PMS346InputVO inputVO = (PMS346InputVO) body;
		PMS346OutputVO outputVO = new PMS346OutputVO();
		
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String str = sdf.format(d);
		String Timer = str.substring(0,14);
		Hashtable<String, String> skeyHash = new Hashtable<String, String>();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		String today = sdf1.format(d);
		
		try{
			//取得加密key值及IV
			XmlInfo xmlInfo = new XmlInfo();
			skeyHash = xmlInfo.doGetVariable("FUBONLIFE_ENCRYPT", "F3");	
			String sKey = skeyHash.get("AES256_SKEY1") + today + skeyHash.get("AES256_SKEY2");	//KEY: 209 + 27935073 + 03750168 + 系統日(8碼)yyyymmdd + TPFB1(5碼)。Ex: 209279350730375016820201124TPFB1
			String sIV = skeyHash.get("AES256_IV1") + today + skeyHash.get("AES256_IV2");		//IV: 209 + 系統日(8碼)yyyymmdd + TPFB1 (5碼)。Ex:20920201124TPFB1
			
			//加密url
			if(!StringUtils.isBlank(inputVO.getNOTE_URL())){
				String caseNo =inputVO.getCASE_NO();
				String url = inputVO.getNOTE_URL();
				
				if(StringUtils.isNotBlank(url) && StringUtils.isNotBlank(caseNo)){
					String parameter = "timer="+Timer+"&partner_code=209&authorkey="+caseNo;
//					parameter = "timer=20201210130800&partner_code=209&authorkey=INB2005181809058265";	//測試資料
//					String encodedUrl = url+"&PA="+ AesEncryptDecryptUtils.encryptAesCbcPkcs5PaddingAscii(sKey, parameter, sIV, 256);
					String encodedUrl = url+"&PA="+ AESEncrypt(parameter, sKey, sIV);
					outputVO.setEncodedUrl(encodedUrl);
				}
			}
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**
	 * 以北富銀提供方式加密
	 * @param sSrc
	 * @param SKey
	 * @param Siv
	 * @return
	 * @throws Exception
	 */
	private String AESEncrypt(String sSrc,String SKey ,String Siv) throws Exception {
	    byte[] raw = SKey.getBytes("UTF-8");
	    byte[] keyBytes = new byte[32];
	    int len = raw.length;
	    if (len > keyBytes.length)
	        len = keyBytes.length;
	    System.arraycopy(raw, 0, keyBytes, 0, len);
	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/補碼方式"
	    IvParameterSpec ivps = new IvParameterSpec(Siv.getBytes("UTF-8"));//使用CBC模式，需要一個向量iv，可增加加密算法的強度
	    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivps);

	    return toHexString(cipher.doFinal(sSrc.getBytes("UTF-8"))).toUpperCase();
	}
	
	/**
	*  將byte [] 再轉換為16進制，對比 C#  ret.AppendFormat("{0:X2}", b);	
	* @param b
	* @return
	*/
	private static String toHexString(byte b[]) {   
	      StringBuffer hexString = new StringBuffer();   
	      for (int i = 0; i < b.length; i++) {   
	          String plainText = Integer.toHexString(0xff & b[i]);   
	          if (plainText.length() < 2)   
	              plainText = "0" + plainText;   
	          hexString.append(plainText);   
	      }   
	         
	      return hexString.toString();   
	  }   

}