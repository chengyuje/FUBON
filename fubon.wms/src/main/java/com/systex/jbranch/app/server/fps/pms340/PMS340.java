package com.systex.jbranch.app.server.fps.pms340;

import java.io.File;
import java.sql.Types;
import java.text.NumberFormat;
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

import com.systex.jbranch.app.server.fps.pms217.PMS217InputVO;
import com.systex.jbranch.app.server.fps.pms217.PMS217OutputVO;
import com.systex.jbranch.app.server.fps.pms227.PMS227InputVO;
import com.systex.jbranch.app.server.fps.pms227.PMS227OutputVO;
import com.systex.jbranch.app.server.fps.pms340.PMS340;
import com.systex.jbranch.app.server.fps.pms340.PMS340InputVO;
import com.systex.jbranch.app.server.fps.pms340.PMS340OutputVO;
import com.systex.jbranch.app.server.fps.pms716.PMS716InputVO;
import com.systex.jbranch.app.server.fps.pms717.PMS717InputVO;
import com.systex.jbranch.app.server.fps.pms717.PMS717OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : <br>
 * Comments Name : PMS340.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月11日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月11日<br>
 */
@Component("pms340")
@Scope("request")
public class PMS340 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private PMS340InputVO inputVO = null;
	private Logger logger = LoggerFactory.getLogger(PMS340.class);
	private String roleflag = "0";

	/**
	 * 查詢檔案
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS340InputVO inputVO = (PMS340InputVO) body;
		PMS340OutputVO outputVO = new PMS340OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sql.append("  SELECT * FROM(                                              ");
			// sql.append("  SELECT YEARMON,                                             ");
			// sql.append("  	     REGION_CENTER_ID,                                    ");
			// sql.append("  	     REGION_CENTER_NAME,                                  ");
			// sql.append("  	     EMP_ID,                                              ");
			// sql.append("  	     EMP_NAME,                                            ");
			// sql.append("  	     ID_CAT,                                              ");
			// sql.append("  	     POLICY_NO,                                           ");
			// sql.append("  	     EFFECT_MON,                                          ");
			// sql.append("  	     INS_FEE,                                             ");
			// sql.append("  	     PER_BONUS_RATE,                                      ");
			// sql.append("  	     BONUS,                                               ");
			// sql.append("  	     SHOULD_BONUS,                                        ");
			// sql.append("  	     ACT_BONUS,                                           ");
			// sql.append("  	     DEFER_BONUS,                                         ");
			// sql.append("  	     VERSION,                                             ");
			// sql.append("  	     CREATETIME,                                          ");
			// sql.append("  	     CREATOR,                                             ");
			// sql.append("  	     MODIFIER,                                            ");
			// sql.append("  	     LASTUPDATE,                                          ");
			// sql.append("  	     REMARK                                               ");
			// sql.append("  FROM TBPMS_LIFE_INS_BONUS                                   ");
			// sql.append("  WHERE 1=1                                                   ");

			sql.append("  SELECT                                                          ");
			sql.append("     BOU.YEARMON,                                                 ");
			if (StringUtils.isNotBlank(inputVO.getIdCat()) && "1".equals(inputVO.getIdCat())) {
				sql.append("     ORG.REGION_CENTER_ID,                                        ");
				sql.append("     ORG.REGION_CENTER_NAME,                                      ");
			}
			sql.append("     BOU.EMP_ID,                                                  ");
			sql.append("     BOU.EMP_NAME,                                                ");
			sql.append("     BOU.ID_CAT,                                                  ");
			sql.append("     BOU.POLICY_NO,                                               ");
			sql.append("     BOU.EFFECT_MON,                                              ");
			sql.append("     BOU.INS_FEE,                                                 ");
			sql.append("     BOU.PER_BONUS_RATE,                                          ");
			sql.append("     BOU.BONUS,                                                   ");
			sql.append("     BOU.SHOULD_BONUS,                                            ");
			sql.append("     BOU.ACT_BONUS,                                               ");
			sql.append("     BOU.DEFER_BONUS,                                             ");
			sql.append("  	 BOU.VERSION,                                             ");
			sql.append("  	 BOU.CREATETIME,                                          ");
			sql.append("  	 BOU.CREATOR,                                             ");
			sql.append("  	 BOU.MODIFIER,                                            ");
			sql.append("  	 BOU.LASTUPDATE,                                          ");
			sql.append("     BOU.REMARK,                                                  ");
			sql.append("     BOU.EFFECT_YEAR,                                             ");
//			sql.append("     ORG.BRANCH_AREA_ID,                                          ");
//			sql.append("     ORG.BRANCH_AREA_NAME,                                        ");
//			sql.append("     ORG.BRANCH_NBR,                                              ");
//			sql.append("     ORG.BRANCH_NAME,                                             ");
			sql.append("     BOU.REF_EMP_ID,                                              ");
			sql.append("     BOU.REF_EMP_NAME,                                            ");
			sql.append("     NVL(ORGR.REGION_CENTER_ID,'-') AS REF_REGION_CENTER_ID,      ");
			sql.append("     NVL(ORGR.REGION_CENTER_NAME,'-') AS REF_REGION_CENTER_NAME   ");
			sql.append("  FROM TBPMS_LIFE_INS_BONUS BOU                                   ");
			if (StringUtils.isNotBlank(inputVO.getIdCat()) && "1".equals(inputVO.getIdCat())) {
				sql.append("  INNER JOIN TBPMS_EMPLOYEE_REC_N REC                             ");
				sql.append("    ON REC.EMP_ID=BOU.EMP_ID                                      ");
				sql.append("    AND LAST_DAY(TO_DATE(:TM ,'YYYYMM')) BETWEEN REC.START_TIME AND REC.END_TIME ");
				sql.append("  INNER JOIN TBPMS_ORG_REC_N ORG                                  ");
				sql.append("    ON REC.DEPT_ID=ORG.DEPT_ID                                 ");
				sql.append("    AND LAST_DAY(TO_DATE(:TM ,'YYYYMM')) BETWEEN ORG.START_TIME AND ORG.END_TIME ");
			}
			sql.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N RECR                             ");
			sql.append("    ON RECR.EMP_ID=BOU.REF_EMP_ID                                 ");
			sql.append("    AND LAST_DAY(TO_DATE(:TM ,'YYYYMM')) BETWEEN RECR.START_TIME AND RECR.END_TIME ");
			sql.append("  LEFT JOIN TBPMS_ORG_REC_N ORGR                                  ");
			sql.append("    ON ORGR.DEPT_ID=RECR.DEPT_ID                               ");
			sql.append("    AND LAST_DAY(TO_DATE(:TM ,'YYYYMM')) BETWEEN ORGR.START_TIME AND ORGR.END_TIME ");
			sql.append("  WHERE 1=1                                                   ");
			if (StringUtils.isNotBlank(inputVO.getsTime())) {
				sql.append(" AND BOU.YEARMON = :TM 					                  ");
				qc.setObject("TM", inputVO.getsTime());
			}
			if (StringUtils.isNotBlank(inputVO.getIdCat())) {
				sql.append(" AND BOU.ID_CAT = :ID_CAT 						          ");
				qc.setObject("ID_CAT", Integer.parseInt(inputVO.getIdCat()));
			}
			// 新增條件
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql.append(" AND  ORG.REGION_CENTER_ID = :REGION_CENTER_ID 						              ");
				qc.setObject("REGION_CENTER_ID", inputVO.getRegion_center_id());
			}
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sql.append(" AND  ORG.BRANCH_AREA_ID = :BRANCH_AREA_ID 						              ");
				qc.setObject("BRANCH_AREA_ID", inputVO.getBranch_area_id());
			}
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sql.append(" AND  ORG.BRANCH_NBR = :BRANCH_NBR			 ");
				qc.setObject("BRANCH_NBR", inputVO.getBranch_nbr());
			}
			if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
				sql.append(" AND  BOU.EMP_ID = :EMP_ID			 ");
				qc.setObject("EMP_ID", inputVO.getEmp_id());
			}

			sql.append("  UNION ALL                                                   ");
			sql.append("  SELECT '' AS YEARMON,                                       ");
			if (StringUtils.isNotBlank(inputVO.getIdCat()) && "1".equals(inputVO.getIdCat())) {
				sql.append("  	     '' AS REGION_CENTER_ID,                              ");
				sql.append("  	     '' AS REGION_CENTER_NAME,                            ");
			}
			sql.append("  	     '' AS EMP_ID,                                        ");
			sql.append("  	     '' AS EMP_NAME,                                      ");
			sql.append("  	     '-' AS ID_CAT,                                       ");
			sql.append("  	     '' AS POLICY_NO,                                     ");
			sql.append("  	     '' AS EFFECT_MON,                                    ");
			sql.append("  	     SUM (BOU.INS_FEE),                                       ");
			sql.append("  	     NULL,                                                ");
			sql.append("  	     SUM (BOU.BONUS),                                         ");
			sql.append("  	     SUM (BOU.SHOULD_BONUS),                                  ");
			sql.append("  	     SUM (BOU.ACT_BONUS),                                     ");
			sql.append("  	     SUM (BOU.DEFER_BONUS),                                   ");
			sql.append("  	     NULL,                                                ");
			sql.append("  	     NULL,                                                ");
			sql.append("  	     NULL,                                                ");
			sql.append("  	     NULL,                                                ");
			sql.append("  	     NULL,                                                ");
			sql.append("  	     '' AS REMARK,                                        ");
			// 新增8個NULL 對應上表
			sql.append("  	     NULL,                                                ");
			sql.append("  	     NULL,                                                ");
//			sql.append("  	     NULL,                                                ");
//			sql.append("  	     NULL,                                                ");
//			sql.append("  	     NULL,                                                ");
//			sql.append("  	     NULL,                                                ");
			sql.append("  	     NULL,                                                ");
			sql.append("  	     NULL,                                                ");
			sql.append("  	     NULL                                                 ");			
			// 修改為了串前端 營運區等條件
			sql.append("  FROM TBPMS_LIFE_INS_BONUS BOU              ");
			if (StringUtils.isNotBlank(inputVO.getIdCat()) && "1".equals(inputVO.getIdCat())) {
				sql.append("  INNER JOIN TBPMS_EMPLOYEE_REC_N REC                             ");
				sql.append("    ON REC.EMP_ID=BOU.EMP_ID                                      ");
				sql.append("    AND LAST_DAY(TO_DATE(:TM ,'YYYYMM')) BETWEEN REC.START_TIME AND REC.END_TIME ");
				sql.append("  INNER JOIN TBPMS_ORG_REC_N ORG                                  ");
				sql.append("    ON REC.DEPT_ID=ORG.DEPT_ID                                 ");
				sql.append("    AND LAST_DAY(TO_DATE(:TM ,'YYYYMM')) BETWEEN ORG.START_TIME AND ORG.END_TIME ");
			}
			sql.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N RECR                             ");
			sql.append("    ON RECR.EMP_ID=BOU.REF_EMP_ID                                 ");
			sql.append("    AND LAST_DAY(TO_DATE(:TM ,'YYYYMM')) BETWEEN RECR.START_TIME AND RECR.END_TIME ");
			sql.append("  LEFT JOIN TBPMS_ORG_REC_N ORGR                                  ");
			sql.append("    ON ORGR.DEPT_ID=RECR.DEPT_ID                               ");
			sql.append("    AND LAST_DAY(TO_DATE(:TM ,'YYYYMM')) BETWEEN ORGR.START_TIME AND ORGR.END_TIME ");
			sql.append("  WHERE POLICY_NO='-'                        ");
			if (StringUtils.isNotBlank(inputVO.getsTime())) {
				sql.append(" AND BOU.YEARMON = :TM 					          ");
				qc.setObject("TM", inputVO.getsTime());
			}
			if (StringUtils.isNotBlank(inputVO.getIdCat())) {
				sql.append(" AND BOU.ID_CAT = :ID_CAT 				          ");
				qc.setObject("ID_CAT", Integer.parseInt(inputVO.getIdCat()));
			}
			// 新增條件
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql.append(" AND  ORG.REGION_CENTER_ID = :REGION_CENTER_ID 		           ");
				qc.setObject("REGION_CENTER_ID", inputVO.getRegion_center_id());
			}
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sql.append(" AND  ORG.BRANCH_AREA_ID = :BRANCH_AREA_ID 			           ");
				qc.setObject("BRANCH_AREA_ID", inputVO.getBranch_area_id());
			}
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sql.append(" AND  ORG.BRANCH_NBR = :BRANCH_NBR			 ");
				qc.setObject("BRANCH_NBR", inputVO.getBranch_nbr());
			}
			if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
				sql.append(" AND  BOU.EMP_ID = :EMP_ID			 ");
				qc.setObject("EMP_ID", inputVO.getEmp_id());
			}

			if (StringUtils.isNotBlank(inputVO.getIdCat()) && "3".equals(inputVO.getIdCat())) {
				sql.append(" )  order by REF_REGION_CENTER_ID,DECODE(REF_EMP_ID,'-','999999',REF_EMP_ID),DECODE(POLICY_NO,'-','999999999999',POLICY_NO)  ");
			}else{
				sql.append(" )  order by REGION_CENTER_ID,DECODE(EMP_ID,'-','999999',EMP_ID),DECODE(POLICY_NO,'-','999999999999',POLICY_NO)  ");
			}
			qc.setQueryString(sql.toString());
			// ResultIF list = dam.executePaging(queryCondition, inputVO
			// .getCurrentPageIndex() + 1, inputVO.getPageCount());
			// List<Map<String, Object>> list1 = dam.exeQuery(queryCondition);
			// int totalPage_i = list.getTotalPage();
			// int totalRecord_i = list.getTotalRecord();
			// outputVO.setResultList(list);
			// outputVO.setCsvList(list1);
			// outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());//
			// 當前頁次
			// outputVO.setTotalPage(totalPage_i);// 總頁次
			// outputVO.setTotalRecord(totalRecord_i);// 總筆數
			// this.sendRtnObject(outputVO);

			List<Map<String, Object>> result = dam.exeQuery(qc);
			outputVO.setResultList(result);
			outputVO.setCsvList(result);
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/**
	 * 匯出EXCLE
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS340OutputVO return_VO2 = (PMS340OutputVO) body;
		List<Map<String, Object>> list = return_VO2.getCsvList();
		int idCat = return_VO2.getIdCat();
		String format = (idCat == 1) ? "招攬" : "轉介";
		roleflag = return_VO2.getFlag().toString();
		// 總行
		int pagecount = 13;
		if (roleflag.equals("1")) {
			pagecount = 13;
		} else {
			// 非總行權限
			pagecount = 12;
		}
		if (list.size() > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			List listCSV = new ArrayList();
			String[] csvHeader = null;
			String fileName = "房貸壽險獎勵金明細表_" + sdf.format(new Date()) + ".csv";
			for (int j = 0; j < list.size(); j++) {

				String[] records = new String[pagecount];
				int i = 0;
				if ("-".equals(list.get(j).get("ID_CAT"))) {
					if (return_VO2.getIdCat() == 1) {
						records[i] = "PS:保費/個人獎金小計";
					} else if (return_VO2.getIdCat() == 2) {
						records[i] = "業務主管:保費/個人獎金小計";
					} else if (return_VO2.getIdCat() == 3) {
						records[i] = "轉介人:保費/個人獎金小計";
					}
				} else {
					if (return_VO2.getIdCat() == 1) {
						records[i] = "PS";
					} else if (return_VO2.getIdCat() == 2) {
						records[i] = "業務主管";
					} else if (return_VO2.getIdCat() == 3) {
						records[i] = "轉介人";
					}

				}
				records[++i] = checkIsNullAndTrans(list.get(j), "POLICY_NO");
				if (return_VO2.getIdCat() == 1) {
					records[++i] = checkIsNull(list.get(j), "EMP_NAME");
				} else if (return_VO2.getIdCat() == 3) {
					records[++i] = checkIsNull(list.get(j), "REF_EMP_NAME");
				}
				if (return_VO2.getIdCat() == 1) {
					records[++i] = checkIsNullAndTrans(list.get(j), "EMP_ID");
				} else if (return_VO2.getIdCat() == 3) {
					records[++i] = checkIsNullAndTrans(list.get(j),
							"REF_EMP_ID");
				}
				if (return_VO2.getIdCat() == 1) {
					records[++i] = checkIsNull(list.get(j),
							"REGION_CENTER_NAME");
				} else if (return_VO2.getIdCat() == 3) {
					records[++i] = checkIsNull(list.get(j),
							"REF_REGION_CENTER_NAME");
				}
				records[++i] = checkIsNull(list.get(j), "EFFECT_MON");
				records[++i] = checkIsNull(list.get(j), "INS_FEE");
				records[++i] = checkIsNull(list.get(j), "PER_BONUS_RATE");
				records[++i] = checkIsNull(list.get(j), "BONUS");
				records[++i] = checkIsNull(list.get(j), "SHOULD_BONUS");
				records[++i] = checkIsNull(list.get(j), "ACT_BONUS");
				records[++i] = checkIsNull(list.get(j), "DEFER_BONUS");
				if (return_VO2.getIdCat() == 3) {
					records[++i] = checkIsNull(list.get(j), "REMARK");
				}
				listCSV.add(records);
			}
			// header
			csvHeader = new String[pagecount];
			int j = 0;
			csvHeader[j] = "身份類別";
			csvHeader[++j] = "保單編號";
			csvHeader[++j] = format + "人員姓名";
			csvHeader[++j] = format + "人員員編";
			csvHeader[++j] = format + "人員區中心";
			csvHeader[++j] = "生效月份";
			csvHeader[++j] = "保費";
			csvHeader[++j] = "個人獎金率";
			csvHeader[++j] = "獎金";
			csvHeader[++j] = "應發獎金";
			csvHeader[++j] = "實發獎金(T+3)";
			csvHeader[++j] = "遞延獎金(T+6)";
			if (return_VO2.getIdCat() == 3) {
				csvHeader[++j] = "備註";
			}
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			notifyClientToDownloadFile(url, fileName); // download
		} else {
			return_VO2.setResultList(list);
			this.sendRtnObject(return_VO2);
		}
	}

	/**
	 * 匯出轉介資料
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void exportRef(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS340OutputVO outputVO = (PMS340OutputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> list;
		try {
			sql.append("select YEARMON,CUST_ID,REF_EMP_ID,REF_EMP_NAME,REF_REGION_NAME from TBPMS_LIFE_INS_BONUS_REF ");
			sql.append("where YEARMON=:ym ");
			qc.setObject("ym", outputVO.getsTime());
			qc.setQueryString(sql.toString());
			
			list = dam.exeQuery(qc);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		
		if (list.size() > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			List listCSV = new ArrayList();
			String[] csvHeader = null;
			String fileName = "轉介資料明細表_" + sdf.format(new Date()) + ".csv";
			for (int j = 0; j < list.size(); j++) {

				int i = 0;
				String[] records = new String[5];
				
				records[i] =   checkIsNull(list.get(j), "YEARMON");           	//資料年月				
				records[++i] = checkIsNullAndTrans(list.get(j), "CUST_ID");     //客戶ID
				records[++i] = checkIsNullAndTrans(list.get(j), "REF_EMP_ID");  //轉介人ID
				records[++i] = checkIsNull(list.get(j), "REF_EMP_NAME");        //轉介人姓名
				records[++i] = checkIsNull(list.get(j), "REF_REGION_NAME");     //轉介人區中心
				
				listCSV.add(records);
			}
			// header
			csvHeader = new String[5];
			int j = 0;
			csvHeader[j] = "資料年月";
			csvHeader[++j] = "客戶ID";
			csvHeader[++j] = "轉介人ID";
			csvHeader[++j] = "轉介人姓名";
			csvHeader[++j] = "轉介人區中心";
			
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			notifyClientToDownloadFile(url, fileName); // download
		} else {
			outputVO.setResultList(list);
			this.sendRtnObject(outputVO);
		}
	}
	
	/**
	 * 角色獲取
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	public void getRole(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS340OutputVO return_VO = new PMS340OutputVO();
		PMS340InputVO inputVO = (PMS340InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		String roleflag = "0";
		try {
			sql.append("  SELECT COUNT(1) AS CNT														");
			sql.append("  FROM TBORG_MEMBER_ROLE                                                        ");
			if (StringUtils
					.isNotBlank((String) getUserVariable(FubonSystemVariableConsts.LOGINID))) {
				sql.append(" WHERE EMP_ID = :user 				                                        ");
				queryCondition
						.setObject(
								"user",
								(String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			}
			sql.append("        AND ROLE_ID IN(SELECT PARAM_CODE                                        ");
			sql.append("  				     FROM TBSYSPARAMETER                                        ");
			sql.append("  				     WHERE PARAM_TYPE = 'FUBONSYS.HEADMGR_ROLE')                ");
			queryCondition.setQueryString(sql.toString());
			// result
			List<Map<String, Object>> roleList = dam
					.executeQuery(queryCondition);
			return_VO.setRole(roleList);
			roleflag = roleList.get(0).get("CNT").toString();
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

	/**
	 * 檢查Map取出欄位是否為Null
	 * 
	 * @param map
	 * @return String
	 */
	private String checkIsNullAndTrans(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return String.valueOf("=\"" + map.get(key) + "\"");
		} else {
			return "";
		}
	}

	/**
	 * 檢查Map取出欄位是否為Null
	 * 
	 * @param map
	 * @return String
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	/**
	 * 處理貨幣格式
	 * 
	 * @param map
	 * @param key
	 * @return
	 */
	private String currencyFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			return nf.format(map.get(key));
		} else
			return "$0.00";
	}

	/**
	 * 達成率格式
	 * 
	 * @param map
	 * @param key
	 * @return
	 */
	private String pcntFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return (int) (Float.parseFloat(map.get(key) + "") + 0.5) + "%";
		} else
			return "";
	}

	/**
	 * 從excel表格中新增數據 上傳
	 * 
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public void importData(Object body, IPrimitiveMap header) throws APException
	{
		int flag = 1;  //第幾筆錯誤
		try {
			
			PMS340InputVO inputVO = (PMS340InputVO) body;
			PMS340OutputVO outputVO = new PMS340OutputVO();
	
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			String lab = null;
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			//有表頭.xls文檔
			//清空臨時表
			dam = this.getDataAccessManager();
			String cellYear=sheet[0].getCell(0, 1).getContents().toString();
			if(sheet[0].getCell(0, 1).getContents().toString().equals(inputVO.getsTime())){
				QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
				String dsql = " truncate table TBPMS_LIFE_INSBONUS_REF_U ";
//				dcon.setObject("YEARMON", inputVO.getsTime());
				dcon.setQueryString(dsql.toString());
				dam.exeUpdate(dcon);
				for(int a=0;a<sheet.length;a++){
//					if(sheet[a].getRows()<5){
//						throw new APException("上傳檔案,第"+a+"筆,上傳欄位數不足五筆");
//					}
					for(int i=1;i<sheet[a].getRows();i++){
						for(int j=0;j<5;j++){
							lab = sheet[a].getCell(j, i).getContents();	
//							if(lab.equals(""))
//								if(j==0 || j==1 || j==3 )
//									throw new APException("第"+(j+1)+"欄位,不能是空值");
							list.add(lab);
						}
						
							//excel表格記行數
							flag++;
							//判斷當前上傳數據欄位個數是否一致
							
							StringBuffer sb = new StringBuffer();
							dam = this.getDataAccessManager();
//							YEARMON		        資料年月
//							CUST_ID		        客戶ID
//							REF_EMP_ID		    轉介人ID
//							REF_EMP_NAME	    轉介人姓名
//							REF_REGION_NAME		轉介人區中心
							QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							sb.append("     INSERT INTO WMSUSER.TBPMS_LIFE_INSBONUS_REF_U   ");
							sb.append("     	(                                 ");
							sb.append("     	YEARMON,                           ");
							sb.append("     	CUST_ID,                           ");
							sb.append("     	REF_EMP_ID,                          ");
							sb.append("     	REF_EMP_NAME,                            ");
							sb.append("     	REF_REGION_NAME,                          ");						
							sb.append("     	RNUM                          ");
							sb.append("     	)                                 ");
							sb.append("     VALUES                                ");
							sb.append("     	(                                 ");
							sb.append("     	:YEARMON,                          ");
							sb.append("     	:CUST_ID,                          ");
							sb.append("     	:REF_EMP_ID,                         ");
							sb.append("     	:REF_EMP_NAME,                           ");
							sb.append("     	:REF_REGION_NAME,                         ");	
							sb.append("     	:RNUM                         ");	
							sb.append("     	)                                 ");
							qc.setObject("YEARMON", inputVO.getsTime());
							qc.setObject("CUST_ID", list.get(1));
							qc.setObject("REF_EMP_ID", list.get(2));
							qc.setObject("REF_EMP_NAME", list.get(3));
							qc.setObject("REF_REGION_NAME", list.get(4));
							qc.setObject("RNUM",flag                                           );
//							qc.setObject("userId", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
							qc.setQueryString(sb.toString());
							dam.exeUpdate(qc);
							list.clear();
						}
					
					}
				
				//資料上傳成功			
				this.sendRtnObject(outputVO);
					
				}else{					
					throw new APException("年月有誤");
			}
			
		} catch (Exception e) {
			logger.error("發生錯誤:%s",StringUtil.getStackTraceAsString(e));
			if(e.getMessage().length()==1)
				throw new APException("資料上傳失敗,欄位數不足5欄位,上傳欄位數"+e.getMessage()+"欄");
			else
				throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
		
	}
	
	/**
	 * 調用存儲過程
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	public void callStored(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS340InputVO inputVO = (PMS340InputVO) body;
			PMS340OutputVO outputVO = new PMS340OutputVO();
			//執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS711.SP_TBPMS_REF_UPLOAD(? , ?) ");
			qc.setString(1, inputVO.getsTime());			
			qc.registerOutParameter(2, Types.VARCHAR);
			qc.setQueryString(sb.toString());
			Map<Integer, Object> resultMap = dam.executeCallable(qc);
			String str = (String) resultMap.get(2);
			String[] strs = null;
			if(str!=null){
				strs = str.split("；");
				if(strs!=null&&strs.length>5){
					str = strs[0]+"；"+strs[1]+"；"+strs[2]+"；"+strs[3]+"；"+strs[4]+"...等";
				}
			}
			outputVO.setErrorMessage(str);
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	
	}
	
	/** 計算獎勵金 **/
	public void callCalcProc(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS340OutputVO outputVO = (PMS340OutputVO) body;
			//執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS711.SP_TBPMS_LIFE_INS_BONUS(?) ");
			qc.setString(1, outputVO.getsTime());		
			qc.setQueryString(sb.toString());
			Map<Integer, Object> resultMap = dam.executeCallable(qc);
			String str = (String) resultMap.get(2);	
			if(str==null || "".equals(str)){
				outputVO.setErrorMessage("");
			}else{
				outputVO.setErrorMessage(str);
			}
			sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	
	}
	
	/** 下載範例檔 **/
	public void downloadSample(Object body, IPrimitiveMap header) throws Exception {
		PMS340InputVO inputVO = (PMS340InputVO) body;	
		notifyClientToDownloadFile("doc"+File.separator+"PMS"+File.separator+"PMS340_EXAMPLE.xls", "轉介資料_上傳範例.xls");
		
	    this.sendRtnObject(null);
	}
}