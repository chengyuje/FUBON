package com.systex.jbranch.app.server.fps.pms350;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.db2.jcc.t4.sb;
import com.opensymphony.util.TextUtils;
import com.systex.jbranch.app.common.fps.table.TBPMS_DYNAMIC_RPT_MASTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
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
 * 動態報表 2016/08/02 Frank 2017/01/30 Kevin 2021/04/27 Ocean 0000612:
 * WMS-CR-20210422-01_銀證督導主要角色功能增修_動態報表 modify by ocean
 *
 */

@Component("pms350")
@Scope("request")
public class PMS350 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS350.class);

	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {

		PMS350InputVO inputVO = (PMS350InputVO) body;
		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ROWNUM, T.* ");
		sql.append("FROM ( ");
		sql.append("  SELECT DRM.SEQ, ");
		sql.append("         DRM.RPT_NAME, ");
		sql.append("         DRM.RPT_EXPLAIN, ");
		sql.append("         DRM.ROLES, ");
		sql.append("         DRM.VALID_FLAG, ");
		sql.append("         DRM.MARQUEE_FLAG, ");
		sql.append("         DRM.MARQUEE_TXT, ");
		sql.append("         DRM.BEGIN_DATE, ");
		sql.append("         DRM.END_DATE, ");
		sql.append("         DRM.UPLOAD_ROLES, ");
		sql.append("         DRM.USER_ROLES, ");
		sql.append("         TO_CHAR(DRM.BEGIN_DATE, 'YYYY/MM/DD') as BEGIN_DATE_F, ");
		sql.append("         TO_CHAR(DRM.END_DATE, 'YYYY/MM/DD') as END_DATE_F, ");
		sql.append("         DRM.FILENAME, ");
		sql.append("         MEM.EMP_NAME, ");
		sql.append("         DRM.RPT_DEPT, ");
		sql.append("         DEF3.DEPT_NAME AS RPT_DEPT_NAME, ");
		sql.append("         DRM.RPT_TYPE, ");
		sql.append("         DRM.RPT_DEPT_1, ");
		sql.append("         DEF1.DEPT_NAME AS RPT_DEPT_1_NAME, ");
		sql.append("         DRM.RPT_DEPT_2, ");
		sql.append("         DEF2.DEPT_NAME AS RPT_DEPT_2_NAME, ");
		sql.append("         TO_CHAR(DRM.LASTUPDATE, 'YYYY/MM/DD') as LASTUPDATE, ");
		sql.append("         DRM.RPT_UPDATER, ");
		sql.append("         DRM.RPT_LASTUPDATE, ");
		sql.append("         MEM2.EMP_NAME AS UPDATER_NAME, ");
		sql.append("         DRM.RPT_SETUP_UPDATER, ");
		sql.append("         DRM.RPT_SETUP_LASTUPDATE, ");
		sql.append("         MEM3.EMP_NAME AS SETUP_EMP_NAME, ");
		sql.append("         DRM.EXPORT_YN ");
		sql.append("  FROM TBPMS_DYNAMIC_RPT_MAST DRM ");
		sql.append("  LEFT JOIN TBORG_MEMBER MEM ON MEM.EMP_ID = DRM.MODIFIER ");
		sql.append("  LEFT JOIN TBORG_MEMBER MEM2 ON MEM2.EMP_ID = DRM.RPT_UPDATER ");
		sql.append("  LEFT JOIN TBORG_MEMBER MEM3 ON MEM3.EMP_ID = DRM.RPT_SETUP_UPDATER ");
		sql.append("  LEFT JOIN TBORG_DEFN DEF1 ON DEF1.DEPT_ID = DRM.RPT_DEPT_1 ");
		sql.append("  LEFT JOIN TBORG_DEFN DEF2 ON DEF2.DEPT_ID = DRM.RPT_DEPT_2 ");
		sql.append("  LEFT JOIN TBORG_DEFN DEF3 ON DEF3.DEPT_ID = DRM.RPT_DEPT ");

		if (inputVO.getUSER_TYPE().equals("true")) {
			sql.append("  LEFT JOIN  TBSYSSECUROLPRIASS P ON P.ROLEID = :UPLOADROLE ");
		}

		sql.append("  WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(inputVO.getRptName())) {
			sql.append(" and DRM.RPT_NAME Like :RRPT_NAME ");
			condition.setObject("RRPT_NAME", "%" + inputVO.getRptName() + "%");
		}

		if (StringUtils.isNotBlank(inputVO.getRPT_DEPT())) {
			sql.append(" and (DRM.RPT_DEPT = :RRPT_DEPT or DRM.RPT_DEPT_1 = :RRPT_DEPT or DRM.RPT_DEPT_2 = :RRPT_DEPT) ");
			condition.setObject("RRPT_DEPT", inputVO.getRPT_DEPT());
		}

		if (StringUtils.isNotBlank(inputVO.getRPT_TYPE())) {
			sql.append(" and DRM.RPT_TYPE =:RRPT_TYPE ");
			condition.setObject("RRPT_TYPE", inputVO.getRPT_TYPE());
		}

		if (StringUtils.isNotBlank(inputVO.getReport_description())) {
			sql.append(" and DRM.RPT_EXPLAIN Like:RRPT_EXPLAIN ");
			condition.setObject("RRPT_EXPLAIN", "%" + inputVO.getReport_description() + "%");

		}

		// 判斷登入角色是否有權查看報表
		if (inputVO.getUSER_TYPE().equals("true")) {
			if (StringUtils.isNotBlank((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				condition.setObject("UPLOADROLE", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
				sql.append("  AND INSTR(DRM.UPLOAD_ROLES, P.PRIVILEGEID) > 0 ");
			} else {
				condition.setObject("UPLOADROLE", "");
			}
		}

		sql.append("  order by DRM.BEGIN_DATE desc, DRM.LASTUPDATE desc ");
		sql.append(") t ");

		condition.setQueryString(sql.toString().replaceAll("\\s+", " "));

		ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();
		outputVO.setResultList(list);
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數

		this.sendRtnObject(outputVO);
	}

	/** =======報表檢視扣除超過公告期間以及公告期間無效======== **/
	public void queryData_Check(Object body, IPrimitiveMap header) throws JBranchException {

		PMS350InputVO inputVO = (PMS350InputVO) body;
		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ROWNUM, T.* ");
		sql.append("FROM ( ");
		sql.append("  SELECT DRM.SEQ, ");
		sql.append("         DRM.RPT_NAME, ");
		sql.append("         DRM.RPT_EXPLAIN, ");
		sql.append("         DRM.VALID_FLAG, ");
		sql.append("         DRM.MARQUEE_TXT, ");
		sql.append("         DRM.MARQUEE_FLAG, ");
		sql.append("         DRM.BEGIN_DATE, ");
		sql.append("         DRM.END_DATE, ");
		sql.append("         DRM.UPLOAD_ROLES, ");
		sql.append("         DRM.USER_ROLES, ");
		sql.append("         DRM.ROLES, ");
		sql.append("         TO_CHAR(DRM.BEGIN_DATE, 'YYYY/MM/DD') as BEGIN_DATE_F, ");
		sql.append("         TO_CHAR(DRM.END_DATE, 'YYYY/MM/DD') as END_DATE_F, ");
		sql.append("         DRM.FILENAME, ");
		sql.append("         MEM.EMP_NAME, ");
		sql.append("         DRM.RPT_DEPT, ");
		sql.append("         DEF3.DEPT_NAME AS RPT_DEPT_NAME, ");
		sql.append("         DRM.RPT_TYPE,");
		sql.append("         DRM.RPT_DEPT_1, ");
		sql.append("         DEF1.DEPT_NAME AS RPT_DEPT_1_NAME, ");
		sql.append("         DRM.RPT_DEPT_2, ");
		sql.append("         DEF2.DEPT_NAME AS RPT_DEPT_2_NAME, ");
		sql.append("         TO_CHAR(DRM.LASTUPDATE, 'YYYY/MM/DD') as LASTUPDATE, ");
		sql.append("         DRM.CREATOR, ");
		sql.append("         DRM.MODIFIER, ");
		sql.append("         DRM.RPT_UPDATER, ");
		sql.append("         DRM.RPT_LASTUPDATE, ");
		sql.append("         MEM2.EMP_NAME AS UPDATER_NAME, ");
		sql.append("         DRM.RPT_SETUP_UPDATER, ");
		sql.append("         DRM.RPT_SETUP_LASTUPDATE, ");
		sql.append("         MEM3.EMP_NAME AS SETUP_EMP_NAME, ");
		sql.append("         DRM.EXPORT_YN ");
		sql.append("  FROM TBPMS_DYNAMIC_RPT_MAST DRM ");
		sql.append("  LEFT JOIN  TBORG_MEMBER MEM ON MEM.EMP_ID = DRM.MODIFIER ");
		sql.append("  LEFT JOIN  TBORG_MEMBER MEM2 ON MEM2.EMP_ID = DRM.RPT_UPDATER ");
		sql.append("  LEFT JOIN  TBORG_MEMBER MEM3 ON MEM3.EMP_ID = DRM.RPT_SETUP_UPDATER ");
		sql.append("  LEFT JOIN  TBORG_DEFN DEF1 ON DEF1.DEPT_ID = DRM.RPT_DEPT_1 ");
		sql.append("  LEFT JOIN  TBORG_DEFN DEF2 ON DEF2.DEPT_ID = DRM.RPT_DEPT_2 ");
		sql.append("  LEFT JOIN  TBORG_DEFN DEF3 ON DEF3.DEPT_ID = DRM.RPT_DEPT ");
		sql.append("  LEFT JOIN  TBSYSSECUROLPRIASS P ON P.ROLEID = :USERROLE ");

		sql.append("  WHERE 1 = 1 ");

		// 2018/11/12 VALID_FLAG = 'Y' or Creator
		sql.append("  AND (DRM.VALID_FLAG = 'Y' or DRM.RPT_UPDATER = :creator) ");
		sql.append("  AND TO_CHAR(DRM.END_DATE,'YYYY/MM/DD') >= TO_CHAR(sysdate,'YYYY/MM/DD') ");
		condition.setObject("creator", getUserVariable(FubonSystemVariableConsts.LOGINID));

		if (StringUtils.isNotBlank(inputVO.getRptName())) {
			sql.append(" and DRM.RPT_NAME =:RRPT_NAME ");
			condition.setObject("RRPT_NAME", inputVO.getRptName());
		}

		if (StringUtils.isNotBlank(inputVO.getRPT_DEPT())) {
			sql.append(" and (DRM.RPT_DEPT = :RRPT_DEPT or DRM.RPT_DEPT_1=:RRPT_DEPT or DRM.RPT_DEPT_2 = :RRPT_DEPT) ");
			condition.setObject("RRPT_DEPT", inputVO.getRPT_DEPT());
		}

		if (StringUtils.isNotBlank(inputVO.getRPT_TYPE())) {
			sql.append(" and DRM.RPT_TYPE = :RRPT_TYPE ");
			condition.setObject("RRPT_TYPE", inputVO.getRPT_TYPE());
		}

		if (StringUtils.isNotBlank(inputVO.getReport_description())) {
			sql.append(" and DRM.RPT_EXPLAIN Like :RRPT_EXPLAIN ");
			condition.setObject("RRPT_EXPLAIN", "%" + inputVO.getReport_description() + "%");
		}

		//判斷登入角色是否有權查看報表
		if (StringUtils.isNotBlank((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			condition.setObject("USERROLE", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
			sql.append("  AND (INSTR(DRM.USER_ROLES, P.PRIVILEGEID) > 0 or DRM.RPT_UPDATER = :creator) ");
		} else {
			condition.setObject("USERROLE", "");
		}

		sql.append("  order by DRM.BEGIN_DATE desc, DRM.LASTUPDATE desc ");
		sql.append(") t ");

		condition.setQueryString(sql.toString());

		ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();
		outputVO.setResultList2(list);
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數

		this.sendRtnObject(outputVO);
	}

	/** ======= 人員代碼 ======== */
	public void personnel(Object body, IPrimitiveMap header) throws JBranchException {

		PMS350InputVO inputVO = (PMS350InputVO) body;
		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sql = new StringBuilder();

		try {
			if (StringUtils.isNotBlank(inputVO.getUSER_TYPE())) {
				if (inputVO.getUSER_TYPE().equals("user")) {
					sql.append("SELECT PRIVILEGEID, NAME ");
					sql.append("FROM TBSYSSECUPRI ");

					if (StringUtils.isNotBlank(inputVO.getRolesName())) {
						sql.append(" WHERE NAME LIKE :name ");
						condition.setObject("name", "%" + inputVO.getRolesName() + "%");
					}
				} else {
					sql.append("SELECT PRIVILEGEID, NAME ");
					sql.append("FROM TBSYSSECUPRI ");
					sql.append("WHERE PRIVILEGEID > '015' ");
					sql.append("AND PRIVILEGEID NOT IN ('UHRM002', 'UHRM012', 'JRM', '023', '024', '032', '038', '039', '040', '053', '054', '055', '056', '057') ");

					if (StringUtils.isNotBlank(inputVO.getRolesName())) {
						sql.append(" WHERE NAME LIKE :name ");
						condition.setObject("name", "%" + inputVO.getRolesName() + "%");
					}
				}
				sql.append("ORDER BY NAME, PRIVILEGEID ");
			} else {
				sql.append("SELECT PRIVILEGEID, NAME ");
				sql.append("FROM TBSYSSECUPRI ");

				if (StringUtils.isNotBlank(inputVO.getRolesName())) {
					sql.append("WHERE NAME LIKE :name ");
					condition.setObject("name", "%" + inputVO.getRolesName() + "%");
				}

				sql.append("ORDER BY PRIVILEGEID ");
			}
			condition.setQueryString(sql.toString());
			outputVO.setResultList(dam.exeQuery(condition));
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

		this.sendRtnObject(outputVO);
	}

	/** ======= 管理科權限 ======== */
	public void Authority(Object body, IPrimitiveMap header) throws Exception {

		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sql = new StringBuilder();

		try {
			sql.append("SELECT EMP_ID FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE ROLE_NAME = '分行績效管理科經辦' ");
			condition.setQueryString(sql.toString());
			outputVO.setResultList(dam.exeQuery(condition));
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

		this.sendRtnObject(outputVO);
	}

	/** ======= 個金分行業務管理部權限(DEPT_ID = 175D) ======== **/
	public void AuthorityOf175B(Object body, IPrimitiveMap header) throws Exception {

		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sql = new StringBuilder();

		try {
			sql.append("SELECT EMP_ID FROM tbpms_employee_rec_n WHERE dept_id like '175D' ");
			condition.setQueryString(sql.toString());
			outputVO.setResultList(dam.exeQuery(condition));
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

		this.sendRtnObject(outputVO);
	}

	/** 檢視所屬資料權限 **/
	public void queryAuthority(Object body, IPrimitiveMap header) throws JBranchException {

		PMS350DetailInputVO inputVO = (PMS350DetailInputVO) body;
		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();

		ArrayList<String> sql_list = new ArrayList<String>();

		sql.append("SELECT ROLES ");
		sql.append("FROM TBPMS_DYNAMIC_RPT_MAST ");
		sql.append("WHERE SEQ = ? ");
		sql_list.add(inputVO.getSeq().toString());

		condition.setQueryString(sql.toString());
		for (int sql_i = 0; sql_i < sql_list.size(); sql_i++) {
			condition.setString(sql_i + 1, sql_list.get(sql_i));
		}

		outputVO.setTotalList(dam.exeQuery(condition));

		sendRtnObject(outputVO);
	}

	/** ======= 新增資料 ======== */
	public void addRPT(Object body, IPrimitiveMap header) throws Exception {

		PMS350InputVO inputVO = (PMS350InputVO) body;
		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();

		inputVO.setSeq(new BigDecimal(getSN()));

		TBPMS_DYNAMIC_RPT_MASTVO vo = new TBPMS_DYNAMIC_RPT_MASTVO();
		vo.setSEQ(inputVO.getSeq());
		vo.setBEGIN_DATE(Timestamp.valueOf(new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(inputVO.getsCreDate())));
		vo.setEND_DATE(Timestamp.valueOf(new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(inputVO.geteCreDate())));
		vo.setVALID_FLAG(inputVO.getValid());
		vo.setRPT_NAME(inputVO.getRptName());
		vo.setRPT_EXPLAIN(inputVO.getRptExplain());
		vo.setMARQUEE_FLAG(inputVO.getMarqueeFlag());
		vo.setMARQUEE_TXT(inputVO.getMarqueeTxt());
		vo.setFILENAME(inputVO.getRealFileName());

		if (StringUtils.isNotBlank(inputVO.getWtfflag())) {
			vo.setUPLOAD_ROLES(TextUtils.join("、", inputVO.getWtfupload()));
			vo.setUSER_ROLES(TextUtils.join("、", inputVO.getWtfuser()));
			vo.setROLES(TextUtils.join("、", inputVO.getRoles())); //檢視所屬資料權限
		} else {
			vo.setUPLOAD_ROLES(TextUtils.join("、", inputVO.getUPLOAD_ROLES()));
			vo.setUSER_ROLES(TextUtils.join("、", inputVO.getRoles()));
			vo.setROLES(TextUtils.join("、", inputVO.getUSER_ROLES())); //檢視所屬資料權限
		}

		vo.setRPT_DEPT_1(inputVO.getRPT_DEPT_1());
		vo.setRPT_DEPT_2(inputVO.getRPT_DEPT_2());
		vo.setRPT_DEPT(inputVO.getRPT_DEPT()); // 報表提供單位
		vo.setRPT_TYPE(inputVO.getRPT_TYPE()); // 報表類型

		if (StringUtils.isNotBlank(inputVO.getUploadFlag())) {
			if (inputVO.getUploadFlag().equals("Y")) {
				insertCSVFile(inputVO, outputVO);
				vo.setFILENAME(inputVO.getRealFileName());
			}
		}

		if (StringUtils.equals("Y", inputVO.getUpdateUpdater())) { //由上傳/更新報表進入
			vo.setRPT_UPDATER((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			vo.setRPT_LASTUPDATE(new Timestamp(new Date().getTime()));
		} else { //由新增報表設定進入
			vo.setRPT_SETUP_UPDATER((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			vo.setRPT_SETUP_LASTUPDATE(new Timestamp(new Date().getTime()));
		}

		if (inputVO.getEXPORT_YN()) {
			vo.setEXPORT_YN("Y");
		}

		dam.create(vo);
		outputVO.setReportId(inputVO.getSeq().toString());

		this.sendRtnObject(outputVO);
	}

	/** ======= 修改資料 ======== */
	public void updateRPT(Object body, IPrimitiveMap header) throws Exception {

		PMS350InputVO inputVO = (PMS350InputVO) body;
		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sql = new StringBuilder();

		TBPMS_DYNAMIC_RPT_MASTVO vo = (TBPMS_DYNAMIC_RPT_MASTVO) dam.findByPKey(TBPMS_DYNAMIC_RPT_MASTVO.TABLE_UID, inputVO.getSeq());
		if (inputVO.getUploadFlag().equals("Y")) {
			if (null != vo) {
				vo.setBEGIN_DATE(Timestamp.valueOf(new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(inputVO.getsCreDate())));
				vo.setEND_DATE(Timestamp.valueOf(new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(inputVO.geteCreDate())));
				vo.setVALID_FLAG(inputVO.getValid());
				vo.setRPT_NAME(inputVO.getRptName());
				vo.setRPT_EXPLAIN(inputVO.getRptExplain());
				vo.setMARQUEE_FLAG(inputVO.getMarqueeFlag());
				vo.setMARQUEE_TXT(inputVO.getMarqueeTxt());
				vo.setRPT_DEPT(inputVO.getRPT_DEPT());
				vo.setRPT_TYPE(inputVO.getRPT_TYPE());

				if (!"".equals(inputVO.getFileName())) {
					insertCSVFile(inputVO, outputVO);
					vo.setFILENAME(inputVO.getRealFileName());
				}

				if (StringUtils.equals("Y", inputVO.getUpdateUpdater())) {
					vo.setRPT_UPDATER((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					vo.setRPT_LASTUPDATE(new Timestamp(new Date().getTime()));
				}

				if (inputVO.getEXPORT_YN()) {
					vo.setEXPORT_YN("Y");
				} else {
					vo.setEXPORT_YN(null);
				}

				dam.update(vo);
			} else {
				// 顯示資料不存在
				throw new APException("ehl_01_commON_017");
			}
		} else {
			String exportYN = vo.getEXPORT_YN() == null ? "N" : vo.getEXPORT_YN();
			String export_YN = inputVO.getEXPORT_YN() ? "Y" : "N";

			String updata_type = ""; //判斷為何種修改
			if (!vo.getRPT_NAME().equals(inputVO.getRptName())) // 改名稱
				updata_type = "1";
			else if (!vo.getRPT_TYPE().equals(inputVO.getRPT_TYPE()))// 改類型
				updata_type = "2";
			else if (!exportYN.equals(export_YN))
				updata_type = "4"; // 改可否匯出 
			else if (!TextUtils.join("、", inputVO.getWtfupload()).equals(vo.getUPLOAD_ROLES()) || !TextUtils.join("、", inputVO.getWtfuser()).equals(vo.getUSER_ROLES()) || !TextUtils.join("、", inputVO.getRoles()).equals(vo.getROLES()))
				updata_type = "3";

			if (updata_type != "") {
				sql.append(" SELECT * FROM TBPMS_DYNAMIC_RPT_MAST WHERE 1 = 1 ");
				switch (updata_type) {
				case "1":
				case "3":
					sql.append(" AND RPT_NAME = :rptname ");
					sql.append(" AND RPT_TYPE = :rpttype ");
					condition.setObject("rptname", vo.getRPT_NAME());
					condition.setObject("rpttype", vo.getRPT_TYPE());

					break;
				case "2":
					sql.append(" AND RPT_TYPE = :rpttype ");
					condition.setObject("rpttype", vo.getRPT_TYPE());

					break;
				case "4":
					sql.append(" AND RPT_NAME = :rptname ");
					sql.append(" AND RPT_TYPE = :rpttype ");
					condition.setObject("rptname", vo.getRPT_NAME());
					condition.setObject("rpttype", vo.getRPT_TYPE());

					if (vo.getEXPORT_YN() == null) {
						sql.append(" AND EXPORT_YN is NULL ");
					} else {
						sql.append(" AND EXPORT_YN = :exportYN ");
						condition.setObject("exportYN", vo.getEXPORT_YN());
					}

					break;
				}

				condition.setQueryString(sql.toString());

				List<Map<String, Object>> list = dam.exeQuery(condition);
				for (Map<String, Object> updataList : list) {
					vo = (TBPMS_DYNAMIC_RPT_MASTVO) dam.findByPKey(TBPMS_DYNAMIC_RPT_MASTVO.TABLE_UID, (BigDecimal) updataList.get("SEQ"));
					if (null != vo) {
						switch (updata_type) {
						case "1":
							vo.setRPT_NAME(inputVO.getRptName());

							break;
						case "2":
							vo.setRPT_TYPE(inputVO.getRPT_TYPE());

							break;
						case "3":
							vo.setUPLOAD_ROLES(TextUtils.join("、", inputVO.getWtfupload()));
							vo.setUSER_ROLES(TextUtils.join("、", inputVO.getWtfuser()));
							vo.setROLES(TextUtils.join("、", inputVO.getRoles()));

							break;
						case "4":
							if (inputVO.getEXPORT_YN()) {
								vo.setEXPORT_YN("Y");
							} else {
								vo.setEXPORT_YN(null);
							}

							break;
						}

						if (StringUtils.equals("Y", inputVO.getUpdateUpdater())) {
							vo.setRPT_UPDATER((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
							vo.setRPT_LASTUPDATE(new Timestamp(new Date().getTime()));
						}

						dam.update(vo);
					} else {
						// 顯示資料不存在
						throw new APException("ehl_01_commON_017");
					}
				}
			} else {
				vo = (TBPMS_DYNAMIC_RPT_MASTVO) dam.findByPKey(TBPMS_DYNAMIC_RPT_MASTVO.TABLE_UID, inputVO.getSeq());
				if (null != vo) {
					vo.setRPT_NAME(inputVO.getRptName());
					vo.setRPT_TYPE(inputVO.getRPT_TYPE());
					vo.setUPLOAD_ROLES(TextUtils.join("、", inputVO.getWtfupload()));
					vo.setUSER_ROLES(TextUtils.join("、", inputVO.getWtfuser()));
					vo.setROLES(TextUtils.join("、", inputVO.getRoles()));

					if (StringUtils.equals("Y", inputVO.getUpdateUpdater())) {
						vo.setRPT_UPDATER((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
						vo.setRPT_LASTUPDATE(new Timestamp(new Date().getTime()));
					}

					if (inputVO.getEXPORT_YN()) {
						vo.setEXPORT_YN("Y");
					} else {
						vo.setEXPORT_YN(null);
					}

					dam.update(vo);
				} else {
					// 顯示資料不存在
					throw new APException("ehl_01_commON_017");
				}
			}
		}

		outputVO.setReportId(inputVO.getSeq().toString());
		outputVO.setUploadFlag(inputVO.getUploadFlag());

		this.sendRtnObject(outputVO);
	}

	/** ====== 檢查主檔相同名稱資料筆數 ======= **/
	public void dataCount(Object body, IPrimitiveMap header) throws JBranchException {

		PMS350InputVO inputVO = (PMS350InputVO) body;
		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT count(*) as dataNum ");
		sql.append("FROM TBPMS_DYNAMIC_RPT_MAST ");
		sql.append("WHERE RPT_NAME = :RPT_NAMEE ");

		if (StringUtils.isNotBlank(inputVO.getRptName())) {
			condition.setObject("RPT_NAMEE", inputVO.getRptName());
		}

		condition.setQueryString(sql.toString());

		outputVO.setCountList(dam.exeQuery(condition));

		this.sendRtnObject(outputVO);
	}

	/** ====== 刪除主檔資料 ======= */
	public void delData(Object body, IPrimitiveMap header) throws JBranchException {

		PMS350InputVO inputVO = (PMS350InputVO) body;
		dam = this.getDataAccessManager();

		TBPMS_DYNAMIC_RPT_MASTVO vo = (TBPMS_DYNAMIC_RPT_MASTVO) dam.findByPKey(TBPMS_DYNAMIC_RPT_MASTVO.TABLE_UID, inputVO.getSeq());
		if (null != vo) {
			dam.delete(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_commON_017");
		}

		delHeadData(inputVO, header);
		delDtlData(inputVO, header);
		delRecData(inputVO, header);

		this.sendRtnObject(null);
	}

	/** ====== 刪除表頭資料 ======= */
	public void delHeadData(PMS350InputVO inputVO, IPrimitiveMap header) throws JBranchException {

		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		condition.setQueryString("delete TBPMS_DYNAMIC_RPT_HEADER WHERE SEQ = :seq");
		condition.setObject("seq", inputVO.getSeq());

		dam.exeUpdate(condition);
	}

	/** ====== 刪除明細-固定欄位資料 ======= */
	public void delDtlData(PMS350InputVO inputVO, IPrimitiveMap header) throws JBranchException {

		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		condition.setQueryString("delete TBPMS_DYNAMIC_RPT_DTL WHERE SEQ = :seq");
		condition.setObject("seq", inputVO.getSeq());

		dam.exeUpdate(condition);
	}

	/** ====== 刪除明細-動態欄位資料 ======= */
	public void delRecData(PMS350InputVO inputVO, IPrimitiveMap header) throws JBranchException {

		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		condition.setQueryString("delete TBPMS_DYNAMIC_RPT_DTL_REC WHERE SEQ = :seq");
		condition.setObject("seq", inputVO.getSeq());

		dam.exeUpdate(condition);
	}

	/** 產生seq No */
	private String getSN() throws JBranchException {

		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";

		try {
			seqNum = sn.getNextSerialNumber("PMS350");
		} catch (Exception e) {
			sn.createNewSerial("PMS350", "0000000000", null, null, null, 6, new Long("99999999"), "y", new Long("0"), null);
			seqNum = sn.getNextSerialNumber("PMS350");
		}

		return seqNum;
	}

	/** 新增CSV檔 表頭、固定欄位、變動欄位資料 **/
	private void insertCSVFile(PMS350InputVO inputVO, PMS350OutputVO outputVO) throws Exception {

		dam = this.getDataAccessManager();
		StringBuffer sqlHead = new StringBuffer();
		StringBuffer sqlValue = new StringBuffer();
		StringBuffer sql = new StringBuffer();
		List<String> error = new ArrayList<String>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());

		if (!dataCsv.isEmpty()) {
			QueryConditionIF dcON = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

			//報表ID
			BigDecimal ReportId = inputVO.getSeq();

			StringBuffer sb = new StringBuffer();
			sb.append(" DELETE TBPMS_DYNAMIC_RPT_TEMP WHERE FILED_51 = :SEQ ");
			dcON.setObject("SEQ", ReportId);
			dcON.setQueryString(sb.toString());
			dam.exeUpdate(dcON);

			//報表資料長度
			int report_len = dataCsv.get(0).length;

			//動態拼接SQL
			sqlHead.append("INSERT INTO TBPMS_DYNAMIC_RPT_TEMP(RNUM");
			for (int i = 1; i <= report_len; i++) {
				String colId = "FILED_" + String.format("%02d", i);
				sqlHead.append(",");
				sqlHead.append(colId);
				sqlValue.append(",:");
				sqlValue.append(colId);
			}

			sqlHead.append(",FILED_50,FILED_51) VALUES (:RNUM");
			sqlValue.append(",TO_CHAR(SYSDATE,'YYYYMMDD'),");
			sqlValue.append(ReportId);
			sqlValue.append(")");

			sql = sqlHead.append(sqlValue);

			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

			for (int i = 0; i < dataCsv.size(); i++) {
				String[] str = dataCsv.get(i);
				// 2018/11/9 str[2] add 0
				if (StringUtils.isNotBlank(str[2]))
					str[2] = StringUtils.leftPad(str[2].trim(), 6, "0");
				//
				if (str.length == report_len) {
					for (int j = 0; j < str.length; j++) {
						String colId = "FILED_" + String.format("%02d", j + 1);
						qc.setObject(colId, str[j]);
					}
				} else {
					error.add("第" + i + "行：與表頭格式不同");
				}

				qc.setObject("RNUM", i);
				qc.setQueryString(sql.toString());

				dam.exeUpdate(qc);
			}

			outputVO.setErrorList(error);
		}
	}

	/** 調用存儲過程 **/
	@SuppressWarnings({ "unused", "rawtypes" })
	public void callStored(Object body, IPrimitiveMap header) throws APException {

		List<String> error = new ArrayList<String>();
		dam = this.getDataAccessManager();
		PMS350InputVO inputVO = (PMS350InputVO) body;
		PMS350OutputVO outputVO = new PMS350OutputVO();

		try {
			String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());

			//報表ID
			BigDecimal ReportId = inputVO.getSeq();

			//報表資料長度
			int report_len = dataCsv.get(0).length;

			QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS352.SP_TBPMS_DYNAMIC_RPT(? ,? ,? ) ");
			qc1.setString(1, ReportId.toString());
			qc1.setString(2, String.valueOf(report_len));
			qc1.registerOutParameter(3, Types.VARCHAR);

			qc1.setQueryString(sb.toString());

			Map<Integer, Object> resultMap = dam.executeCallable(qc1);
			String str = (String) resultMap.get(3);
			String[] strs = null;
			if (str != null) {
				strs = str.split(";");
				if (strs != null && strs.length > 5) {
					str = strs[0] + ";" + strs[1] + ";" + strs[2] + ";" + strs[3] + ";" + strs[4] + "...等";
				}
			}
			error.add(str);
		} catch (Exception e) {
			error.add(e.getMessage());
		}

		outputVO.setErrorList(error);

		this.sendRtnObject(outputVO);
	}

	/**** PMS350_DETAIL ****/
	public void queryRPTCol(Object body, IPrimitiveMap header) throws JBranchException {

		PMS350DetailInputVO inputVO = (PMS350DetailInputVO) body;
		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();

		ArrayList<String> sql_list = new ArrayList<String>();
		sql.append("SELECT COL_NAME, DRM.RPT_EXPLAIN ");
		sql.append("FROM TBPMS_DYNAMIC_RPT_HEADER DRH ");
		sql.append("LEFT JOIN  TBPMS_DYNAMIC_RPT_MAST DRM ON DRH.SEQ = DRM.SEQ ");
		sql.append("WHERE 1 = 1 ");
		sql.append("and DRH.SEQ = ? ");
		sql.append("and COL_SEQ > 4 ");
		sql.append("order by COL_SEQ ");
		sql_list.add(inputVO.getSeq().toString());

		condition.setQueryString(sql.toString());

		for (int sql_i = 0; sql_i < sql_list.size(); sql_i++) {
			condition.setString(sql_i + 1, sql_list.get(sql_i));
		}

		outputVO.setTotalList(dam.exeQuery(condition));

		sendRtnObject(outputVO);
	}

	public void queryRPTData(Object body, IPrimitiveMap header) throws JBranchException {

		XmlInfo xmlInfo = new XmlInfo();
		boolean isHANDMGR = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isUHRMMGR = xmlInfo.doGetVariable("FUBONSYS.UHRMMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isARMGR = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isOPMGR = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isUHRMBMMGR = xmlInfo.doGetVariable("FUBONSYS.UHRMBMMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));

		PMS350DetailInputVO inputVO = (PMS350DetailInputVO) body;
		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("WITH BASE_ORG AS ( ");
		sql.append("  SELECT ID.DEPT_ID, ID.DEPT_NAME, ID.ORG_TYPE, ");
		sql.append("	     CASE WHEN EXISTS (SELECT 1 FROM VWORG_DEPT_BR T WHERE T.DEPT_ID = ID.DEPT_ID_30) THEN ID.DEPT_ID_30 ELSE NULL END AS REGION_CENTER_ID, ");
		sql.append("	     CASE WHEN EXISTS (SELECT 1 FROM VWORG_DEPT_BR T WHERE T.DEPT_ID = ID.DEPT_ID_40) THEN ID.DEPT_ID_40 ELSE NULL END AS BRANCH_AREA_ID, ");
		sql.append("	     CASE WHEN EXISTS (SELECT 1 FROM VWORG_DEPT_BR T WHERE T.DEPT_ID = ID.DEPT_ID_50) THEN ID.DEPT_ID_50 ELSE NULL END AS BRANCH_NBR, ");
		sql.append("	     ID.DEPT_ID_00, ");
		sql.append("	     ID.DEPT_ID_05, ");
		sql.append("	     ID.DEPT_ID_10, ");
		sql.append("	     ID.DEPT_ID_20, ");
		sql.append("	     ID.DEPT_ID_30, ");
		sql.append("	     ID.DEPT_ID_40, ");
		sql.append("	     ID.DEPT_ID_50 ");
		sql.append("  FROM ( ");
		sql.append("	SELECT * ");
		sql.append("	FROM ( ");
		sql.append("	  SELECT A.DEPT_ID, A.ORG_TYPE, A.DEPT_NAME, ORG.ORG_TYPE AS ORG_TYPE_CLS, ORG.DEPT_ID AS LEV_DEPT_ID ");
		sql.append("	  FROM TBORG_DEFN A ");
		sql.append("	  LEFT JOIN ( ");
		sql.append("	    SELECT DISTINCT ORG_TYPE, CONNECT_BY_ROOT(DEPT_ID) AS CHILD_DEPT_ID, DEPT_ID, DEPT_NAME ");
		sql.append("	    FROM TBORG_DEFN ");
		sql.append("	    START WITH DEPT_ID IS NOT NULL ");
		sql.append("	    CONNECT BY PRIOR PARENT_DEPT_ID = DEPT_ID ");
		sql.append("	  ) ORG ON A.DEPT_ID = ORG.CHILD_DEPT_ID ");
		sql.append("	) ");
		sql.append("	PIVOT (MAX(LEV_DEPT_ID) FOR ORG_TYPE_CLS IN ('00' AS DEPT_ID_00, '05' AS DEPT_ID_05, '10' AS DEPT_ID_10, '20' AS DEPT_ID_20, '30' AS DEPT_ID_30, '40' AS DEPT_ID_40, '50' AS DEPT_ID_50)) ");
		sql.append("  ) ID ");
		sql.append(") ");

		sql.append("SELECT TT.* ");
		sql.append("FROM ( ");
		sql.append("  SELECT T.SEQ, T.ROW_SEQ, ");
		sql.append("         D.DATA_YEARMON, ");
		sql.append("         D.REGION_CENTER_ID, ");
		sql.append("         D.REGION_CENTER_NAME, ");
		sql.append("         D.BRANCH_AREA_ID, ");
		sql.append("         D.BRANCH_AREA_NAME, ");
		sql.append("         D.BRANCH_NBR, ");
		sql.append("         D.BRANCH_NAME, ");
		sql.append("         D.EMP_ID, ");
		sql.append("         M.EMP_NAME, ");
		sql.append("         D.AO_CODE, ");
		sql.append("         CASE WHEN ORG_50.DEPT_ID IS NOT NULL THEN ORG_50.REGION_CENTER_ID ");
		sql.append("              WHEN ORG_40.DEPT_ID IS NOT NULL THEN ORG_40.REGION_CENTER_ID ");
		sql.append("              WHEN ORG_30.DEPT_ID IS NOT NULL THEN ORG_30.REGION_CENTER_ID ");
		sql.append("         ELSE D.REGION_CENTER_ID END AS REAL_RC, ");
		sql.append("         CASE WHEN ORG_50.DEPT_ID IS NOT NULL THEN ORG_50.BRANCH_AREA_ID ");
		sql.append("              WHEN ORG_40.DEPT_ID IS NOT NULL THEN ORG_40.BRANCH_AREA_ID ");
		sql.append("              WHEN ORG_30.DEPT_ID IS NOT NULL THEN ORG_30.BRANCH_AREA_ID ");
		sql.append("         ELSE D.BRANCH_AREA_ID END AS REAL_OP, ");
		sql.append("         CASE WHEN ORG_50.DEPT_ID IS NOT NULL THEN ORG_50.BRANCH_NBR ");
		sql.append("              WHEN ORG_40.DEPT_ID IS NOT NULL THEN ORG_40.BRANCH_NBR ");
		sql.append("              WHEN ORG_30.DEPT_ID IS NOT NULL THEN ORG_30.BRANCH_NBR ");
		sql.append("         ELSE D.BRANCH_NBR END AS REAL_BR, ");
		sql.append("         T.COL ");
		sql.append("  FROM ( ");
		sql.append("    SELECT DISTINCT SEQ, ROW_SEQ, ");
		sql.append("           LISTAGG(NVL(S.CONTENT, ' '), ';') WITHIN GROUP(ORDER BY S.COL_SEQ) OVER (PARTITION BY ROW_SEQ) AS COL ");
		sql.append("    FROM TBPMS_DYNAMIC_RPT_DTL_REC S ");
		sql.append("    WHERE S.SEQ = :seq ");
		sql.append("  ) T ");
		sql.append("  INNER JOIN TBPMS_DYNAMIC_RPT_DTL D ON D.SEQ = T.SEQ AND D.ROW_SEQ = T.ROW_SEQ ");
		sql.append("  LEFT JOIN TBORG_MEMBER M ON D.EMP_ID = M.EMP_ID  ");
		sql.append("  LEFT JOIN TBORG_DEFN DEFN ON DEFN.DEPT_ID = M.DEPT_ID  ");
		sql.append("  LEFT JOIN BASE_ORG ORG_50 ON DEFN.ORG_TYPE = '50' AND DEFN.DEPT_ID = ORG_50.DEPT_ID  ");
		sql.append("  LEFT JOIN BASE_ORG ORG_40 ON DEFN.ORG_TYPE = '40' AND DEFN.DEPT_ID = ORG_40.DEPT_ID  ");
		sql.append("  LEFT JOIN BASE_ORG ORG_30 ON DEFN.ORG_TYPE = '30' AND DEFN.DEPT_ID = ORG_30.DEPT_ID  ");
		sql.append("  WHERE 1 = 1 ");

		//此報表是否只查所屬的資料
		if ("0".equals(inputVO.getIsSelf())) {
			if (StringUtils.isNotEmpty(inputVO.getBranch_nbr())) { // 分行
				sql.append("  AND ( ");

				// 分行
				sql.append("    D.BRANCH_NBR = :branch ");

				// 營運區合計
				sql.append("    OR ( ");
				sql.append("   	      D.BRANCH_NBR IS NULL ");
				sql.append("      AND EXISTS (SELECT 1 FROM VWORG_DEFN_INFO I WHERE I.BRANCH_AREA_ID = D.BRANCH_AREA_ID AND I.BRANCH_NBR = :branch)");
				sql.append("   	) ");

				// 業務處合計	
				sql.append("    OR ( ");
				sql.append("   	      D.BRANCH_NAME LIKE '%處%合計%' ");
				sql.append("   	  AND D.BRANCH_AREA_ID IS NULL ");
				sql.append("   	  AND D.BRANCH_NBR IS NULL ");
				sql.append("   	  AND REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(D.BRANCH_NAME, '合計', ''), '*', ''), '分行業務', ''), '處', ''), ' ', '') = (SELECT DISTINCT REPLACE(REPLACE(REGION_CENTER_NAME, '分行業務', ''), '處', '') FROM VWORG_DEFN_INFO I WHERE I.BRANCH_NBR = :branch)");
				sql.append("   	) ");

				// 全行合計
				sql.append("   	OR (D.BRANCH_NAME NOT LIKE '%處%合計%' AND D.REGION_CENTER_ID IS NULL AND D.BRANCH_AREA_ID IS NULL AND D.BRANCH_NBR IS NULL) ");

				sql.append("  ) ");

				condition.setObject("branch", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotEmpty(inputVO.getBranch_area_id())) {
				sql.append("  AND ( ");

				// 分行
				sql.append("    EXISTS (SELECT 1 FROM VWORG_DEFN_INFO I WHERE D.BRANCH_NBR = I.BRANCH_NBR AND I.BRANCH_AREA_ID = :area_id) ");

				// 營運區合計
				sql.append("    OR ( ");
				sql.append("   	  D.BRANCH_NBR IS NULL ");
				sql.append("      AND EXISTS (SELECT 1 FROM VWORG_DEFN_INFO I WHERE I.BRANCH_AREA_ID = D.BRANCH_AREA_ID AND I.BRANCH_AREA_ID = :area_id)");
				sql.append("   	) ");

				// 業務處合計	
				sql.append("    OR ( ");
				sql.append("   	  D.BRANCH_NAME LIKE '%處%合計%' ");
				sql.append("   	  AND D.BRANCH_AREA_ID IS NULL ");
				sql.append("   	  AND D.BRANCH_NBR IS NULL ");
				sql.append("   	  AND REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(D.BRANCH_NAME, '合計', ''), '*', ''), '分行業務', ''), '處', ''), ' ', '') = (SELECT DISTINCT REPLACE(REPLACE(REGION_CENTER_NAME, '分行業務', ''), '處', '') FROM VWORG_DEFN_INFO I WHERE I.BRANCH_AREA_ID = :area_id)");
				sql.append("   	) ");

				// 全行合計
				sql.append("   	OR (D.BRANCH_NAME NOT LIKE '%處%合計%' AND D.REGION_CENTER_ID IS NULL AND D.BRANCH_AREA_ID IS NULL AND D.BRANCH_NBR IS NULL) ");

				if (isUHRMMGR || isOPMGR || isUHRMBMMGR) {
					sql.append("    OR M.DEPT_ID = :loginOP ");
					condition.setObject("loginOP", (String) getUserVariable(FubonSystemVariableConsts.LOGIN_AREA));
				} else if (isARMGR || isHANDMGR) {
					sql.append("    OR ( ");
					sql.append("   	  (CASE WHEN (SELECT COUNT(1) FROM TBORG_UHRM_BRH UB WHERE UB.EMP_ID = D.EMP_ID) > 0 THEN 'Y' ELSE 'N' END) = 'Y'  ");
					sql.append("   	  AND (CASE WHEN ORG_50.DEPT_ID IS NOT NULL THEN ORG_50.BRANCH_AREA_ID ");
					sql.append("                WHEN ORG_40.DEPT_ID IS NOT NULL THEN ORG_40.BRANCH_AREA_ID ");
					sql.append("                WHEN ORG_30.DEPT_ID IS NOT NULL THEN ORG_30.BRANCH_AREA_ID ");
					sql.append("           ELSE D.BRANCH_AREA_ID END) = :area_id ");
					sql.append("    ) ");
				}

				sql.append("  ) ");

				condition.setObject("area_id", inputVO.getBranch_area_id());
			} else if (StringUtils.isNotEmpty(inputVO.getRegion_center_id())) {
				sql.append("  AND ( ");

				// 分行
				sql.append("    EXISTS (SELECT 1 FROM VWORG_DEFN_INFO I WHERE D.BRANCH_NBR = I.BRANCH_NBR AND I.REGION_CENTER_ID = :center_id) ");

				// 營運區合計
				sql.append("    OR ( ");
				sql.append("   	  D.BRANCH_NBR IS NULL ");
				sql.append("      AND EXISTS (SELECT 1 FROM VWORG_DEFN_INFO I WHERE I.BRANCH_AREA_ID = D.BRANCH_AREA_ID AND I.REGION_CENTER_ID = :center_id)");
				sql.append("   	) ");

				// 業務處合計	
				sql.append("    OR (");
				sql.append("   	    D.BRANCH_NAME LIKE '%處%合計%' ");
				sql.append("   	    AND D.BRANCH_AREA_ID IS NULL AND D.BRANCH_NBR IS NULL ");
				sql.append("   	    AND REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(D.BRANCH_NAME, '合計', ''), '*', ''), '分行業務', ''), '處', ''), ' ', '') = (SELECT DISTINCT REPLACE(REPLACE(REGION_CENTER_NAME, '分行業務', ''), '處', '') FROM VWORG_DEFN_INFO I WHERE I.REGION_CENTER_ID = :center_id)");
				sql.append("   	) ");

				// 全行合計
				sql.append("   	OR (D.BRANCH_NAME NOT LIKE '%處%合計%' AND D.REGION_CENTER_ID IS NULL AND D.BRANCH_AREA_ID IS NULL AND D.BRANCH_NBR IS NULL) ");

				if (isARMGR) {
					sql.append("    OR ( ");
					sql.append("   	  (CASE WHEN (SELECT COUNT(1) FROM TBORG_UHRM_BRH UB WHERE UB.EMP_ID = D.EMP_ID) > 0 THEN 'Y' ELSE 'N' END) = 'Y'  ");
					sql.append("   	  AND (CASE WHEN ORG_50.DEPT_ID IS NOT NULL THEN ORG_50.REGION_CENTER_ID ");
					sql.append("                WHEN ORG_40.DEPT_ID IS NOT NULL THEN ORG_40.REGION_CENTER_ID ");
					sql.append("                WHEN ORG_30.DEPT_ID IS NOT NULL THEN ORG_30.REGION_CENTER_ID ");
					sql.append("           ELSE D.REGION_CENTER_ID END) = :loginRC ");
					sql.append("    ) ");
					condition.setObject("loginRC", (String) getUserVariable(FubonSystemVariableConsts.LOGIN_REGION));
				}

				sql.append("  ) ");

				condition.setObject("center_id", inputVO.getRegion_center_id());
			}

			// 2018/11/9
			if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
				sql.append("  AND D.EMP_ID = :emp_id ");
				condition.setObject("emp_id", inputVO.getEmp_id());
			}
		}

		//員工編號是否只查所屬的資料
		if ("1".equals(inputVO.getIsSelf()) && !"".equals(inputVO.getEmp_id())) {
			QueryConditionIF querycondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql1 = new StringBuffer();

			sql1.append("SELECT PRIVILEGEID ");
			sql1.append("FROM TBSYSSECUROLPRIASS ");
			sql1.append("WHERE ROLEID = :roleID ");

			querycondition.setQueryString(sql1.toString());

			querycondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));

			List<Map<String, Object>> pri = dam.exeQuery(querycondition);

			switch ((String) pri.get(0).get("PRIVILEGEID")) {
			case "004": // 若為消金PS則不判斷AO_CODE
			case "JRM":
				sql.append("AND D.EMP_ID = :emp_id ");
				condition.setObject("emp_id", inputVO.getEmp_id());
				break;
			default: // 改為判斷員編或者AO_CODE, 因為會有繼承別人而來的AO_CODE
				sql.append("AND (D.AO_CODE IN (:ao_code) OR D.EMP_ID = :emp_id) ");
				condition.setObject("ao_code", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
				condition.setObject("emp_id", inputVO.getEmp_id());
				break;
			}
		}

		// 2021/04/27 0000612: WMS-CR-20210422-01_銀證督導主要角色功能增修_動態報表
		if (StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "bsmgr")) {
			sql.append("AND ( ");
			sql.append("     D.EMP_ID IS NOT NULL AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO U WHERE D.EMP_ID = U.EMP_ID) ");
			sql.append("  OR D.AO_CODE IS NOT NULL AND EXISTS (SELECT 1 FROM VWORG_EMP_BS_INFO U WHERE D.AO_CODE = U.BS_CODE) ");
			sql.append(") ");
		}

		sql.append("  ORDER BY T.ROW_SEQ ");
		sql.append(") TT ");

		sql.append("WHERE 1 = 1 ");

		if (isUHRMMGR || isOPMGR || isUHRMBMMGR) {
			sql.append("AND (TT.REAL_OP = :loginOP OR TT.REAL_OP IS NULL) ");
			condition.setObject("loginOP", (String) getUserVariable(FubonSystemVariableConsts.LOGIN_AREA));
		} else if (isARMGR) {
			sql.append("AND (TT.REAL_RC = :loginRC OR TT.REAL_RC IS NULL) ");
			condition.setObject("loginRC", (String) getUserVariable(FubonSystemVariableConsts.LOGIN_REGION));
		}

		//此報表是否只查所屬的資料
		if ("1".equals(inputVO.getIsSelf()) && "".equals(inputVO.getEmp_id())) {
			sql.append("AND TT.EMP_ID = :emp_id ");
			if (StringUtils.isNotBlank((String) getUserVariable(FubonSystemVariableConsts.LOGINID))) {
				condition.setObject("emp_id", getUserVariable(FubonSystemVariableConsts.LOGINID));
			} else {
				condition.setObject("emp_id", "");
			}
		}

		condition.setObject("seq", inputVO.getSeq());
		condition.setQueryString(sql.toString());

		ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		List<Map<String, Object>> csvList = dam.exeQuery(condition);

		outputVO.setResultList(list);
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(list.getTotalPage());// 總頁次
		outputVO.setTotalRecord(list.getTotalRecord());// 總筆數
		outputVO.setTotalList(csvList);
		outputVO.setCsvList(csvList);

		sendRtnObject(outputVO);
	}

	public void queryDTLREC(Object body, IPrimitiveMap header) throws JBranchException {

		PMS350DetailInputVO inputVO = (PMS350DetailInputVO) body;
		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();

		ArrayList<String> sql_list = new ArrayList<String>();
		sql.append("SELECT ROW_SEQ, COL_SEQ, CONTENT, CREATETIME ");
		sql.append("FROM TBPMS_DYNAMIC_RPT_DTL_REC ");
		sql.append("WHERE 1 = 1 ");
		sql.append("and SEQ = ? ");
		sql.append("order by COL_SEQ ");
		sql_list.add(inputVO.getSeq().toString());

		condition.setQueryString(sql.toString());

		for (int sql_i = 0; sql_i < sql_list.size(); sql_i++) {
			condition.setString(sql_i + 1, sql_list.get(sql_i));
		}

		outputVO.setTotalList(dam.exeQuery(condition));

		sendRtnObject(outputVO);
	}

	/** 下載範例檔 **/
	public void downloadSample(Object body, IPrimitiveMap header) throws Exception {

		notifyClientToDownloadFile("doc//PMS//PMS350_EXAMPLE.csv", "動態報表上傳範例.csv");

		this.sendRtnObject(null);
	}

	/** 匯出 **/
	public void export(Object body, IPrimitiveMap header) throws JBranchException {

		PMS350OutputVO return_VO = (PMS350OutputVO) body;
		List<Map<String, Object>> list = return_VO.getList();
		List<Map<String, Object>> headerList = return_VO.getTotalList();

		CSVUtil csv = new CSVUtil();
		String[] recordsKey = { "ITEM", "DATA_YEARMON", "REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NBR", "BRANCH_NAME", "EMP_ID", "EMP_NAME", "AO_CODE" };
		String[] headersKey = { "項次", "資料年月", "業務處", "營運區", "分行代號", "分行名稱", "員工編號", "員工姓名", "AO_CODE" };
		try {
			if (list.size() > 0) {
				String.format("%1$,09d", -3123);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "動態報表" + sdf.format(new Date()) + "-" + getUserVariable(FubonSystemVariableConsts.LOGINID) + ".csv";
				List listCSV = new ArrayList();

				// TITLE
				String[] csvHeader = new String[headerList.size() + headersKey.length];
				for (int i = 0; i < (headerList.size() + headersKey.length); i++) {
					if (i < headersKey.length) {
						csvHeader[i] = headersKey[i];
					} else {
						String[] sp2 = checkIsNullAndSplit(headerList.get(i - headersKey.length), "COL_NAME");
						csvHeader[i] = sp2[0]; //動態表頭
					}
				}
				csv.setHeader(new String[] { return_VO.getReportId() });
				listCSV.add(0, csvHeader);

				// CONTENT
				for (int i = 0; i < list.size(); i++) {
					String[] sp = checkIsNullAndSplit(list.get(i), "COL"); //資料切割
					String[] records = new String[sp.length + recordsKey.length];

					for (int j = 0; j < (sp.length + recordsKey.length); j++) {
						if (j < recordsKey.length) {
							switch (recordsKey[j]) {
							case "ITEM": // 項次
								records[j] = String.valueOf(i + 1);
								break;
							case "DATA_YEARMON": //資料年月 
								records[j] = "=\"" + checkIsNull(list.get(i), recordsKey[j]) + "\"";
								break;
							default:
								records[j] = checkIsNull(list.get(i), recordsKey[j]);
								break;
							}
						} else {
							records[j] = "=\"" + sp[j - recordsKey.length] + "\""; //動態資料
						}
					}

					listCSV.add(records);
				}

				csv.addRecordList(listCSV);
				String url = csv.generateCSV();

				// download
				notifyClientToDownloadFile(url, fileName);
			} else {
				return_VO.setResultList(list);
				this.sendRtnObject(return_VO);
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	private String checkIsNull(Map map, String key) {

		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			if (map.get(key) == null) {
				return "";
			}
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	private String[] checkIsNullAndSplit(Map map, String key) {

		String[] x = String.valueOf(map.get(key)).split(";");
		int len = x.length;
		for (int i = 0; i < len; ++i) {
			if (x[i].equals("null")) {
				x[i] = "";
			}
		}

		return x;
	}

	/** 報表名稱名單 **/
	public void QUERY_NAME(Object body, IPrimitiveMap header) throws JBranchException {

		PMS350InputVO inputVO = (PMS350InputVO) body;
		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT RPT_NAME FROM TBPMS_DYNAMIC_RPT_MAST WHERE RPT_NAME IS NOT NULL ");

		condition.setQueryString(sql.toString());

		outputVO.setNamelist(dam.exeQuery(condition));

		this.sendRtnObject(outputVO);
	}

	/** 設定報表-取報表名稱 **/
	public void QUERY_NAME2(Object body, IPrimitiveMap header) throws JBranchException {

		PMS350InputVO inputVO = (PMS350InputVO) body;
		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT RPT_NAME AS RPT_NAME_D FROM TBPMS_DYNAMIC_RPT_MAST DRM ");
		sql.append("LEFT JOIN  TBORG_DEFN DEF ON DEF.DEPT_ID = CASE WHEN DRM.RPT_DEPT IS NOT NULL THEN DRM.RPT_DEPT ");
		sql.append("                                               WHEN DRM.RPT_DEPT_2 IS NOT NULL THEN DRM.RPT_DEPT_2 ");
		sql.append("                                          ELSE DRM.RPT_DEPT_1 END ");
		sql.append("WHERE RPT_NAME IS NOT NULL ");
		//			sql.append(" AND DRM.VALID_FLAG = 'Y' ");
		//			sql.append(" AND TO_CHAR(DRM.END_DATE,'YYYY/MM/DD') >= TO_CHAR(sysdate,'YYYY/MM/DD') ");

		if (StringUtils.isNotBlank(inputVO.getRPT_TYPE())) {
			sql.append(" AND RPT_TYPE = :rpt_type ");
			condition.setObject("rpt_type", inputVO.getRPT_TYPE());
		}

		condition.setQueryString(sql.toString());

		outputVO.setNamelist(dam.exeQuery(condition));

		this.sendRtnObject(outputVO);
	}

	/** 報表名稱名單FOR上傳更新報表 **/
	public void QUERY_NAME_UPLOAD(Object body, IPrimitiveMap header) throws JBranchException {

		PMS350InputVO inputVO = (PMS350InputVO) body;
		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT RPT_NAME AS RPT_NAME_D FROM TBPMS_DYNAMIC_RPT_MAST DRM ");
		sql.append("LEFT JOIN  TBSYSSECUROLPRIASS P ON P.ROLEID = :UPLOADROLE ");
		sql.append("LEFT JOIN  TBORG_DEFN DEF ON DEF.DEPT_ID = CASE WHEN DRM.RPT_DEPT IS NOT NULL THEN DRM.RPT_DEPT ");
		sql.append("                                               WHEN DRM.RPT_DEPT_2 IS NOT NULL THEN DRM.RPT_DEPT_2 ");
		sql.append("                                          ELSE DRM.RPT_DEPT_1 END ");
		sql.append("WHERE RPT_NAME IS NOT NULL ");

		if (StringUtils.isNotBlank(inputVO.getRPT_TYPE())) {
			sql.append(" AND RPT_TYPE = :rpt_type ");
			condition.setObject("rpt_type", inputVO.getRPT_TYPE());
		}

		if (StringUtils.isNotBlank((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			condition.setObject("UPLOADROLE", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
			sql.append("AND INSTR(DRM.UPLOAD_ROLES, P.PRIVILEGEID) > 0 ");
		} else {
			condition.setObject("UPLOADROLE", "");
		}

		condition.setQueryString(sql.toString());

		outputVO.setNamelist(dam.exeQuery(condition));

		this.sendRtnObject(outputVO);
	}

	/** 報表名稱名單FOR上傳檢視報表 **/
	public void QUERY_NAME_VIEW(Object body, IPrimitiveMap header) throws JBranchException {

		PMS350InputVO inputVO = (PMS350InputVO) body;
		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT DRM.RPT_NAME AS RPT_NAME_D ");
		sql.append("FROM ( ");
		sql.append("  SELECT DISTINCT RPT_NAME, RPT_TYPE, RPT_DEPT, RPT_DEPT_1, RPT_DEPT_2, END_DATE, VALID_FLAG, REGEXP_SUBSTR(USER_ROLES_LIST, '[^,]+', 1, TEMP_T.LEV) AS USER_ROLES ");
		sql.append("  FROM ( ");
		sql.append("    SELECT DISTINCT RPT_NAME, RPT_TYPE, RPT_DEPT, RPT_DEPT_1, RPT_DEPT_2, END_DATE, VALID_FLAG, REPLACE(DRM.USER_ROLES, '、', ',') AS USER_ROLES_LIST ");
		sql.append("    FROM TBPMS_DYNAMIC_RPT_MAST DRM ");
		sql.append("  ) PAR ");
		sql.append("  OUTER APPLY ( ");
		sql.append("    SELECT LEVEL AS LEV ");
		sql.append("    FROM DUAL ");
		sql.append("    CONNECT BY LEVEL <= REGEXP_COUNT(PAR.USER_ROLES_LIST, ',') + 1 ");
		sql.append("  ) TEMP_T ");
		sql.append(") DRM ");
		sql.append("LEFT JOIN TBSYSSECUROLPRIASS P ON P.ROLEID = :USERROLES ");
		sql.append("LEFT JOIN TBORG_DEFN DEF ON DEF.DEPT_ID = CASE WHEN DRM.RPT_DEPT IS NOT NULL THEN DRM.RPT_DEPT ");
		sql.append("                                                WHEN DRM.RPT_DEPT_2 IS NOT NULL THEN DRM.RPT_DEPT_2 ");
		sql.append("                                           ELSE DRM.RPT_DEPT_1 END ");
		sql.append("WHERE RPT_NAME IS NOT NULL ");
		sql.append("AND DRM.VALID_FLAG = 'Y' ");
		sql.append("AND TO_CHAR(DRM.END_DATE, 'YYYY/MM/DD') >= TO_CHAR(sysdate, 'YYYY/MM/DD') ");

		if (StringUtils.isNotBlank(inputVO.getRPT_TYPE())) {
			sql.append("AND RPT_TYPE = :rpt_type ");
			condition.setObject("rpt_type", inputVO.getRPT_TYPE());
		}

		if (StringUtils.isNotBlank((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			condition.setObject("USERROLES", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
			sql.append("AND DRM.USER_ROLES = P.PRIVILEGEID ");
		} else {
			condition.setObject("USERROLES", "");
		}

		sql.append("ORDER BY RPT_NAME DESC ");

		condition.setQueryString(sql.toString());

		outputVO.setNamelist(dam.exeQuery(condition));

		this.sendRtnObject(outputVO);
	}

	/** 報表類型名單 - 設定報表 **/
	public void QUERY_TYPE(Object body, IPrimitiveMap header) throws JBranchException {

		PMS350InputVO inputVO = (PMS350InputVO) body;
		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT RPT_TYPE ");
		sql.append("FROM TBPMS_DYNAMIC_RPT_MAST DRM ");
		sql.append("LEFT JOIN  TBORG_DEFN DEF ON DEF.DEPT_ID = CASE WHEN DRM.RPT_DEPT IS NOT NULL THEN DRM.RPT_DEPT ");
		sql.append("                                               WHEN DRM.RPT_DEPT_2 IS NOT NULL THEN DRM.RPT_DEPT_2 ");
		sql.append("                                          ELSE DRM.RPT_DEPT_1 END ");
		sql.append("WHERE RPT_TYPE IS NOT NULL ");

		//			sql.append(" AND DRM.VALID_FLAG = 'Y' ");
		//			sql.append(" AND TO_CHAR(DRM.END_DATE,'YYYY/MM/DD') >= TO_CHAR(sysdate,'YYYY/MM/DD') ");

		//若是報表設定進來, 則用OR處理, 反之會按照RPT_DEPT_2及RPT_DEPT來查詢
		if ("1".equals(inputVO.getALL_DEPT())) {
			sql.append("AND (RPT_DEPT =:rpt_dept or RPT_DEPT_1=:rpt_dept  or RPT_DEPT_2=:rpt_dept) ");
			condition.setObject("rpt_dept", inputVO.getRPT_DEPT());
		} else {
			if (StringUtils.isNotBlank(inputVO.getRPT_DEPT_2())) {
				//sql.append(" AND (RPT_DEPT =:rpt_dept or RPT_DEPT_1=:rpt_dept  or RPT_DEPT_2=:rpt_dept) ");
				sql.append("AND RPT_DEPT_2 = :rpt_dept_2 ");
				condition.setObject("rpt_dept_2", inputVO.getRPT_DEPT_2());
			}

			if (StringUtils.isNotBlank(inputVO.getRPT_DEPT())) {
				//sql.append(" AND (RPT_DEPT =:rpt_dept or RPT_DEPT_1=:rpt_dept  or RPT_DEPT_2=:rpt_dept) ");
				sql.append("AND RPT_DEPT = :rpt_dept ");
				condition.setObject("rpt_dept", inputVO.getRPT_DEPT());
			}
		}

		condition.setQueryString(sql.toString());

		outputVO.setTypelist(dam.exeQuery(condition));

		this.sendRtnObject(outputVO);
	}

	/** 報表類型名單FOR上傳更新報表 **/
	public void QUERY_TYPE_UPLOAD(Object body, IPrimitiveMap header) throws JBranchException {

		PMS350InputVO inputVO = (PMS350InputVO) body;
		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT RPT_TYPE ");
		sql.append("FROM TBPMS_DYNAMIC_RPT_MAST DRM ");
		sql.append("LEFT JOIN  TBSYSSECUROLPRIASS P ON P.ROLEID = :UPLOADROLE ");
		sql.append("LEFT JOIN  TBORG_DEFN DEF ON DEF.DEPT_ID = CASE WHEN DRM.RPT_DEPT IS NOT NULL THEN DRM.RPT_DEPT ");
		sql.append("                                               WHEN DRM.RPT_DEPT_2 IS NOT NULL THEN DRM.RPT_DEPT_2 ");
		sql.append("                                          ELSE DRM.RPT_DEPT_1 END ");
		sql.append("WHERE RPT_TYPE IS NOT NULL ");

		if (StringUtils.isNotBlank(inputVO.getRPT_DEPT())) {
			sql.append(" AND (RPT_DEPT = :rpt_dept or RPT_DEPT_1 = :rpt_dept or RPT_DEPT_2 = :rpt_dept) ");
			condition.setObject("rpt_dept", inputVO.getRPT_DEPT());
		}

		if (StringUtils.isNotBlank((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			condition.setObject("UPLOADROLE", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
			sql.append("AND INSTR(DRM.UPLOAD_ROLES, P.PRIVILEGEID)>0 ");
		} else {
			condition.setObject("UPLOADROLE", "");
		}

		condition.setQueryString(sql.toString());

		outputVO.setTypelist(dam.exeQuery(condition));

		this.sendRtnObject(outputVO);
	}

	/** 報表類型名單 - 設定報表 **/
	public void QUERY_TYPE_VIEW(Object body, IPrimitiveMap header) throws JBranchException {

		PMS350InputVO inputVO = (PMS350InputVO) body;
		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT RPT_TYPE ");
		sql.append("FROM TBPMS_DYNAMIC_RPT_MAST DRM ");
		sql.append("LEFT JOIN TBORG_DEFN DEF ON DEF.DEPT_ID = CASE WHEN DRM.RPT_DEPT IS NOT NULL THEN DRM.RPT_DEPT WHEN DRM.RPT_DEPT_2 IS NOT NULL THEN DRM.RPT_DEPT_2 ELSE DRM.RPT_DEPT_1 END ");
		sql.append("WHERE RPT_TYPE IS NOT NULL ");
		sql.append("AND DRM.VALID_FLAG = 'Y' ");
		sql.append("AND TO_CHAR(DRM.END_DATE, 'YYYY/MM/DD') >= TO_CHAR(SYSDATE, 'YYYY/MM/DD') ");

		//若是報表設定進來, 則用OR處理, 反之會按照RPT_DEPT_2及RPT_DEPT來查詢
		if ("".equals(inputVO.getRPT_TYPE())) {
			sql.append("AND (RPT_DEPT = :rpt_dept OR RPT_DEPT_1 = :rpt_dept OR RPT_DEPT_2 = :rpt_dept) ");
			condition.setObject("rpt_dept", inputVO.getRPT_DEPT());
		} else {
			if (StringUtils.isNotBlank(inputVO.getRPT_DEPT_2())) {
				sql.append("AND RPT_DEPT_2 = :rpt_dept_2 ");
				condition.setObject("rpt_dept_2", inputVO.getRPT_DEPT_2());
			}
			if (StringUtils.isNotBlank(inputVO.getRPT_DEPT())) {
				sql.append("AND RPT_DEPT = :rpt_dept ");
				condition.setObject("rpt_dept", inputVO.getRPT_DEPT());
			}
		}

		if (StringUtils.isNotBlank((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND EXISTS ( ");
			sql.append("  SELECT BASE.RPT_TYPE, BASE.USER_ROLES ");
			sql.append("  FROM ( ");
			sql.append("    SELECT DISTINCT RPT_TYPE, REGEXP_SUBSTR(USER_ROLES_LIST, '[^,]+', 1, TEMP_T.LEV) AS USER_ROLES ");
			sql.append("    FROM ( ");
			sql.append("      SELECT DISTINCT RPT_TYPE, REPLACE(DRM.USER_ROLES, '、', ',') AS USER_ROLES_LIST ");
			sql.append("      FROM TBPMS_DYNAMIC_RPT_MAST DRM ");
			sql.append("    ) PAR ");
			sql.append("    OUTER APPLY ( ");
			sql.append("      SELECT LEVEL AS LEV ");
			sql.append("      FROM DUAL ");
			sql.append("      CONNECT BY LEVEL <= REGEXP_COUNT(PAR.USER_ROLES_LIST, ',') + 1 ");
			sql.append("    ) TEMP_T ");
			sql.append("  ) BASE ");
			sql.append("  WHERE EXISTS (SELECT 1 FROM TBSYSSECUROLPRIASS P WHERE BASE.USER_ROLES = P.PRIVILEGEID AND P.ROLEID = :USERROLE) ");
			sql.append("  AND BASE.RPT_TYPE = DRM.RPT_TYPE ");
			sql.append(") ");

			condition.setObject("USERROLE", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		}

		condition.setQueryString(sql.toString());

		outputVO.setTypelist(dam.exeQuery(condition));

		this.sendRtnObject(outputVO);
	}

	/** 報表提供單位名稱名單 **/
	public void QUERY_DEPTNAME(Object body, IPrimitiveMap header) throws JBranchException {

		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT DEF.DEPT_NAME, DEF.DEPT_ID ");
		sql.append("FROM TBPMS_DYNAMIC_RPT_MAST DRM ");
		sql.append("LEFT JOIN  TBORG_DEFN DEF ON DEF.DEPT_ID = CASE WHEN DRM.RPT_DEPT IS NOT NULL THEN DRM.RPT_DEPT ");
		sql.append("                                               WHEN DRM.RPT_DEPT_2 IS NOT NULL THEN DRM.RPT_DEPT_2 ");
		sql.append("                                          ELSE DRM.RPT_DEPT_1 END ");
		sql.append("WHERE DEF.DEPT_NAME IS NOT NULL ");

		condition.setQueryString(sql.toString());

		outputVO.setDeptNamelist(dam.exeQuery(condition));

		this.sendRtnObject(outputVO);
	}

	/** 報表提供單位名稱名單 **/
	public void QUERY_DEPTNAME2(Object body, IPrimitiveMap header) throws JBranchException {

		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT DEF.DEPT_NAME AS DEPT_NAME_D, DEF.DEPT_ID ");
		sql.append("FROM TBPMS_DYNAMIC_RPT_MAST DRM ");
		sql.append("LEFT JOIN  TBSYSSECUROLPRIASS P ON P.ROLEID = :USERROLES ");
		sql.append("LEFT JOIN  TBORG_DEFN DEF ON DEF.DEPT_ID = CASE WHEN DRM.RPT_DEPT IS NOT NULL THEN DRM.RPT_DEPT ");
		sql.append("                                               WHEN DRM.RPT_DEPT_2 IS NOT NULL THEN DRM.RPT_DEPT_2 ");
		sql.append("                                          ELSE DRM.RPT_DEPT_1 END ");
		sql.append("WHERE DEF.DEPT_NAME IS NOT NULL ");
		sql.append("AND DRM.VALID_FLAG = 'Y' ");
		sql.append("AND TO_CHAR(DRM.END_DATE, 'YYYY/MM/DD') >= TO_CHAR(sysdate, 'YYYY/MM/DD') ");

		if (StringUtils.isNotBlank((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			condition.setObject("USERROLES", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
			sql.append("AND INSTR(DRM.USER_ROLES, P.PRIVILEGEID) > 0 ");
		} else {
			condition.setObject("USERROLES", "");
		}

		condition.setQueryString(sql.toString());

		outputVO.setDeptNamelist(dam.exeQuery(condition));

		this.sendRtnObject(outputVO);
	}

	/** 報表提供單位名稱名單FOR上傳更新報表 **/
	public void QUERY_DEPTNAME_UPLOAD(Object body, IPrimitiveMap header) throws JBranchException {

		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT DEF.DEPT_NAME, DEF.DEPT_ID ");
		sql.append("FROM TBPMS_DYNAMIC_RPT_MAST DRM ");
		sql.append("LEFT JOIN  TBSYSSECUROLPRIASS P ON P.ROLEID = :UPLOADROLE ");
		sql.append("LEFT JOIN  TBORG_DEFN DEF ON DEF.DEPT_ID = CASE WHEN DRM.RPT_DEPT IS NOT NULL THEN DRM.RPT_DEPT ");
		sql.append("                                               WHEN DRM.RPT_DEPT_2 IS NOT NULL THEN DRM.RPT_DEPT_2 ");
		sql.append("                                          ELSE DRM.RPT_DEPT_1 END ");
		sql.append("WHERE DEF.DEPT_NAME IS NOT NULL ");

		if (StringUtils.isNotBlank((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			condition.setObject("UPLOADROLE", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
			sql.append("AND INSTR(DRM.UPLOAD_ROLES, P.PRIVILEGEID) > 0 ");
		} else {
			condition.setObject("UPLOADROLE", "");
		}

		condition.setQueryString(sql.toString());

		outputVO.setDeptNamelist(dam.exeQuery(condition));

		this.sendRtnObject(outputVO);
	}

	//報表提供單位名單
	public void QUERY_DEPT(Object body, IPrimitiveMap header) throws JBranchException {

		PMS350InputVO inputVO = (PMS350InputVO) body;
		PMS350OutputVO outputVO = new PMS350OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		//第一階
		if (ObjectUtils.equals("10", inputVO.getORG_TYPE())) {
			sql.append("SELECT DEPT_ID, DEPT_NAME, ORG_TYPE, PARENT_DEPT_ID ");
			sql.append("FROM TBORG_DEFN ");
			sql.append("WHERE 1 = 1 ");
			sql.append("AND DEPT_NAME LIKE '%個金%' ");
			sql.append("AND ORG_TYPE = '10' ");
			sql.append("OR DEPT_NAME LIKE '%信用卡處%' ");
			sql.append("ORDER BY ORG_TYPE, PARENT_DEPT_ID, DEPT_ID ");
		}

		//第二階
		if (ObjectUtils.equals("20", inputVO.getORG_TYPE())) {
			sql.append("SELECT DEPT_ID, DEPT_NAME, ORG_TYPE, PARENT_DEPT_ID ");
			sql.append("FROM TBORG_DEFN ");
			sql.append("WHERE  PARENT_DEPT_ID = :dpet_id ");
			sql.append("ORDER BY PARENT_DEPT_ID, org_type, DEPT_ID ");
			condition.setObject("dpet_id", inputVO.getDEPT_NAME());
		}

		//第三階
		if (ObjectUtils.equals("30", inputVO.getORG_TYPE())) {
			sql.append("SELECT DEPT_ID, DEPT_NAME, ORG_TYPE, PARENT_DEPT_ID ");
			sql.append("FROM TBORG_DEFN ");
			sql.append("WHERE  PARENT_DEPT_ID = :dpet_id ");
			sql.append("ORDER BY PARENT_DEPT_ID, org_type, DEPT_ID ");
			condition.setObject("dpet_id", inputVO.getDEPT_NAME());
		}

		condition.setQueryString(sql.toString());

		outputVO.setDeptlist(dam.exeQuery(condition));

		this.sendRtnObject(outputVO);
	}
}