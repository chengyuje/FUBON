package com.systex.jbranch.app.server.fps.prd178;

import static com.systex.jbranch.fubon.commons.ExcelUtil.columnLocation;
import static com.systex.jbranch.fubon.commons.ExcelUtil.getColumnsCode;
import static org.apache.commons.lang.StringUtils.trim;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBJSB_INS_PROD_PROJECTVO;
import com.systex.jbranch.app.common.fps.table.TBJSB_INS_PROD_PROJECT_CONTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * PRD178
 * 
 * @author Sam Tu
 * @date 2022/06/17
 */

@Component("prd178")
@Scope("request")
public class PRD178 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD178.class);

	public void init(Object body, IPrimitiveMap header) throws JBranchException {
		PRD178OutputVO return_VO = new PRD178OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		// parameter參數 專案大類別
		sql.append("select CD_NM as LABEL, CD_NM as DATA from TBJSB_INS_PROD_CODE_TABLE where CD_INDEX = :CD_INDEX ");
		queryCondition.setObject("CD_INDEX", "A05");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> projectBigClassList = dam.exeQuery(queryCondition);
		return_VO.setProjectBigClassList(projectBigClassList);

		// parameter參數 專案中類別
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("select CD_NM as LABEL, CD_NM as DATA from TBJSB_INS_PROD_CODE_TABLE where CD_INDEX = :CD_INDEX ");
		queryCondition.setObject("CD_INDEX", "A06");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> projectMidClassList = dam.exeQuery(queryCondition);
		return_VO.setProjectMidClassList(projectMidClassList);

		// parameter參數 專案小類別
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("select CD_NM as LABEL, CD_NM as DATA from TBJSB_INS_PROD_CODE_TABLE where CD_INDEX = :CD_INDEX ");
		queryCondition.setObject("CD_INDEX", "A07");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> projectSmallClassList = dam.exeQuery(queryCondition);
		return_VO.setProjectSmallClassList(projectSmallClassList);

		// parameter參數 公司名稱
		// queryCondition =
		// dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		// sql = new StringBuffer();
		// sql.append("select SERIALNUM as DATA, CNAME as LABEL  from TBJSB_INS_PROD_COMPANY ");
		// queryCondition.setQueryString(sql.toString());
		// List<Map<String, Object>> companyNameList =
		// dam.exeQuery(queryCondition);
		// return_VO.setCompanyNameList(companyNameList);

		this.sendRtnObject(return_VO);
	}

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		PRD178InputVO inputVO = (PRD178InputVO) body;
		PRD178OutputVO return_VO = new PRD178OutputVO();
		dam = this.getDataAccessManager();

		List<Map<String, Object>> resultList = getMainSqlResult(inputVO);
		return_VO.setResultList(resultList);
		this.sendRtnObject(return_VO);
	}

	private List getMainSqlResult(PRD178InputVO inputVO) throws DAOException, JBranchException {
		// TODO Auto-generated method stub
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		// 主查詢
		sql.append("select ");
		sql.append("C.CNAME ");
		sql.append(", P.PROJECTID ");
		sql.append(", P.PROJECTNAME ");
		sql.append(", P.DEPARTMENTTYPE ");
		sql.append(", P.PROJECTTYPE2 ");
		sql.append(", P.PROJECTTYPE ");
		sql.append(", P.PROJECTTYPE1 ");
		sql.append(", P.PERMER ");
		sql.append(", P.VALIDFROM ");
		sql.append(", P.VALIDTHRU ");
		sql.append(", P.INSCOPRODUCTNAME ");
		sql.append(", P.INSCOPRODUCTID ");
		sql.append(", P.MEMO ");
		sql.append(", M.PRODUCTID as MPRODUCTID ");
		sql.append(", M.PRODUCTNAME as MPRODUCTNAME ");
		sql.append(", I.PRODUCTPUDTYPE ");
		sql.append(", I.PRODUCTPUD ");
		sql.append(", I.PRODUCTEDTYPE ");
		sql.append(", I.PRODUCTED ");
		sql.append(", PC.COVERAGEDUEPREMIUM ");
		sql.append(", PC.CONTCOVERAGEDUEPREMIUM ");
		sql.append("from TBJSB_INS_PROD_PROJECT P ");
		sql.append("left join TBJSB_INS_PROD_COMPANY C on C.SERIALNUM = P.INSURANCECOSERIALNUM ");
		sql.append("left join TBJSB_INS_PROD_PROJECT_CONT PC on PC.PROJECTSERIALNUM = P.PROJECTSERIALNUM ");
		sql.append("left join TBJSB_INS_PROD_MAIN M on M.PRODUCTSERIALNUM = PC.PRODUCTSERIALNUM ");
		sql.append("left join TBJSB_INS_PROD_LIFEITEM I on I.PRODUCTSERIALNUM = M.PRODUCTSERIALNUM ");
		sql.append("where 1=1 ");

		// 限制條件
		// 保險公司
		if (StringUtils.isNotBlank(inputVO.getCNAME())) {
			sql.append("and C.CNAME like '%");
			sql.append(inputVO.getCNAME());
			sql.append("%' ");
		}
		// 專案名稱
		if (StringUtils.isNotBlank(inputVO.getPROJECTNAME())) {
			sql.append("and P.PROJECTNAME like '%");
			sql.append(inputVO.getPROJECTNAME());
			sql.append("%' ");
		}
		// 專案代碼
		if (StringUtils.isNotBlank(inputVO.getPROJECTID())) {
			sql.append("and P.PROJECTID like '%");
			sql.append(inputVO.getPROJECTID());
			sql.append("%' ");
		}
		// 開始銷售日
		if (null != inputVO.getsCreDate()) {
			sql.append("and VALIDFROM >= :sDate ");
			queryCondition.setObject("sDate", inputVO.getsCreDate());
		}
		// 停止銷售日
		if (null != inputVO.getEndDate()) {
			sql.append("and VALIDTHRU <= :eDate ");
			queryCondition.setObject("eDate", inputVO.getEndDate());
		}

		// 排序
		sql.append("order by P.PROJECTSERIALNUM desc ");
		queryCondition.setQueryString(sql.toString());
		return dam.exeQuery(queryCondition);
	}

	public void downloadCSV(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		PRD178InputVO inputVO = (PRD178InputVO) body;
		PRD178OutputVO return_VO = new PRD178OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		dam = this.getDataAccessManager();

		List<Map<String, Object>> list = getMainSqlResult(inputVO);

		if (list.size() > 0) {
			// gen csv
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String fileName = "日盛專案管理_" + sdf.format(new Date()) + "_" + ws.getUser().getUserID() + ".csv";
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				// 18 column
				String[] records = new String[18];
				int i = 0;

				records[i] = checkIsNull(map, "CNAME");
				records[++i] = checkIsNull(map, "PROJECTID");
				records[++i] = checkIsNull(map, "PROJECTNAME");
				records[++i] = checkIsNull(map, "DEPARTMENTTYPE");
				records[++i] = checkIsNull(map, "PROJECTTYPE2");
				records[++i] = checkIsNull(map, "PROJECTTYPE");
				records[++i] = checkIsNull(map, "PROJECTTYPE1");
				records[++i] = checkIsNull(map, "PERMER");
				records[++i] = checkIsNull(map, "VALIDFROM");
				records[++i] = checkIsNull(map, "VALIDTHRU");
				records[++i] = checkIsNull(map, "INSCOPRODUCTNAME");
				records[++i] = checkIsNull(map, "INSCOPRODUCTID");
				records[++i] = checkIsNull(map, "MEMO");
				records[++i] = checkIsNull(map, "MPRODUCTID");
				records[++i] = checkIsNull(map, "MPRODUCTNAME");
				records[++i] = checkIsNull(map, "PRODUCTPUD") + checkIsNull(map, "PRODUCTPUDTYPE") + "/" + checkIsNull(map, "PRODUCTED") + checkIsNull(map, "PRODUCTEDTYPE");
				records[++i] = checkIsNull(map, "COVERAGEDUEPREMIUM");
				records[++i] = checkIsNull(map, "CONTCOVERAGEDUEPREMIUM");

				listCSV.add(records);
			}
			// header
			String[] csvHeader = new String[18];
			int j = 0;
			csvHeader[j] = "保險公司";
			csvHeader[++j] = "專案代碼";
			csvHeader[++j] = "專案名稱";
			csvHeader[++j] = "通路型態";
			csvHeader[++j] = "專案大分類";
			csvHeader[++j] = "專案中分類";
			csvHeader[++j] = "專案小分類";
			csvHeader[++j] = "保費固定";
			csvHeader[++j] = "開始銷售日";
			csvHeader[++j] = "停止銷售日";
			csvHeader[++j] = "保險公司產品名稱";
			csvHeader[++j] = "保險公司產品代號";
			csvHeader[++j] = "備註";
			csvHeader[++j] = "產品代號";
			csvHeader[++j] = "產品名稱";
			csvHeader[++j] = "繳費/保障方式";
			csvHeader[++j] = "單位數";
			csvHeader[++j] = "保費";

			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			// download
			notifyClientToDownloadFile(url, fileName);
		} else
			return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}

	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	/**
	 * 下載日盛專案管理範例檔
	 * 
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void downloadSimple(Object body, IPrimitiveMap header) throws Exception {
		this.notifyClientToDownloadFile("doc//PRD//PRD178_EXAMPLE.csv", "日盛專案管理範例.csv");
	}

	public void upload(Object body, IPrimitiveMap header) throws Exception {
		PRD178InputVO inputVO = (PRD178InputVO) body;
		PRD178OutputVO return_VO = new PRD178OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		dam = this.getDataAccessManager();

		List<String> PROD_MAIN_notExist = new ArrayList<String>(); // 對應TBJSB_INS_PROD_MAIN不存在
		List<String> PROD_LIFEITEM_notExist = new ArrayList<String>(); // 對應TBJSB_INS_PROD_LIFEITEM不存在
		List<String> typeError = new ArrayList<String>(); // 數值不符合資料庫欄位型態（紀錄表格位置）
		List<String> requiredError = new ArrayList<String>(); // 若為新增，缺少必要欄位（紀錄表格位置）

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if (!dataCsv.isEmpty()) {

			if (dataCsv.size() <= 1) {
				throw new APException("上傳檔案僅有 header，無包含資料");
			}

			// 整批上傳每次僅可上傳單一家保險公司資料
			checkHaveDifferentCompany(dataCsv);

			int columnSize = 15;
			String[] columnCodes = getColumnsCode(columnSize);

			for (int index = 1; index < dataCsv.size(); index++) {
				// 以 header size 建立新的 string array，避免上傳欄位空值導致 OutOfIndexBound
				String[] columns = Arrays.copyOf(dataCsv.get(index), columnSize);

				if (index == 0) {
					checkUploadHeader(columns);
					continue;
				}

				// 用PROJECT_ID確認TABLE是否已存在對應資料並進行相對應初始化
				List<Map<String, Object>> primaryKeyList = getPrimaryKeyList(trim(columns[1]));
				Boolean projectIDExist = false;
				TBJSB_INS_PROD_PROJECTVO projectVO = null;
				TBJSB_INS_PROD_PROJECT_CONTVO projectContVO = null;
				List<Map<String, Object>> PROD_MAIN = getPROD_MAIN(columns);
				if (PROD_MAIN.size() < 1) {
					PROD_MAIN_notExist.add(columns[1]);
					continue;
				}
				List<Map<String, Object>> PROD_LIFEITEM = getPROD_LIFEITEM(PROD_MAIN.get(0).get("PRODUCTSERIALNUM").toString());
				if (PROD_LIFEITEM.size() < 1) {
					PROD_LIFEITEM_notExist.add(columns[1]);
					continue;
				}
				
				if (primaryKeyList.size() == 0) {
					projectIDExist = false;
					projectVO = new TBJSB_INS_PROD_PROJECTVO();
					projectContVO = new TBJSB_INS_PROD_PROJECT_CONTVO();
				} else {			
					projectIDExist = true;
					BigDecimal PROJECTSERIALNUM = new BigDecimal(primaryKeyList.get(0).get("PROJECTSERIALNUM").toString());
					BigDecimal PROJCONTSERIALNUM = new BigDecimal(primaryKeyList.get(0).get("PROJCONTSERIALNUM").toString());
					projectVO = (TBJSB_INS_PROD_PROJECTVO) dam.findByPKey(TBJSB_INS_PROD_PROJECTVO.TABLE_UID, PROJECTSERIALNUM);
					projectContVO = (TBJSB_INS_PROD_PROJECT_CONTVO) dam.findByPKey(TBJSB_INS_PROD_PROJECT_CONTVO.TABLE_UID, PROJCONTSERIALNUM);
				}

				// 保險公司
				String INSURANCECOSERIALNUM = trim(columns[0]);
				if (StringUtils.isNotBlank(INSURANCECOSERIALNUM)) {
					try {
						projectVO.setINSURANCECOSERIALNUM(new BigDecimal(INSURANCECOSERIALNUM));
					} catch (Exception e) {
						typeError.add(columnLocation(index + 1, 0, columnCodes));
						continue;
					}

				} else {
					requiredError.add(columnLocation(index + 1, 0, columnCodes));
					continue;
				}
				// 專案代碼
				String PROJECTID = trim(columns[1]);
				if (StringUtils.isNotBlank(PROJECTID)) {
					if (PROJECTID.length() > 20) {
						typeError.add(columnLocation(index + 1, 1, columnCodes));
						continue;		
					} else {
						projectVO.setPROJECTID(PROJECTID);
					}
				} else {
					requiredError.add(columnLocation(index + 1, 1, columnCodes));
					continue;
				}
				// 專案名稱
				String PROJECTNAME = trim(columns[2]);
				if (StringUtils.isNotBlank(PROJECTNAME)) {
					if (PROJECTNAME.length() > 100) {
						typeError.add(columnLocation(index + 1, 2, columnCodes));
						continue;					
					} else {
						projectVO.setPROJECTNAME(PROJECTNAME);
					}
				} else {
					requiredError.add(columnLocation(index + 1, 2, columnCodes));
					continue;
				}
				// 通路型態
				String DEPARTMENTTYPE = trim(columns[3]);
				if (StringUtils.isNotBlank(DEPARTMENTTYPE)) {
					if (DEPARTMENTTYPE.length() > 20) {
						typeError.add(columnLocation(index + 1, 3, columnCodes));
						continue;	
					} else {
						projectVO.setDEPARTMENTTYPE(DEPARTMENTTYPE);
					}
				} else {
					requiredError.add(columnLocation(index + 1, 3, columnCodes));
					continue;
				}
				// 專案大分類
				String PROJECTTYPE2 = trim(columns[4]);
				if (StringUtils.isNotBlank(PROJECTTYPE2)) {
					if(PROJECTTYPE2.length() > 40) {
						typeError.add(columnLocation(index + 1, 4, columnCodes));
						continue;	
					} else {
						projectVO.setPROJECTTYPE2(PROJECTTYPE2);
					}
				} else {
					requiredError.add(columnLocation(index + 1, 4, columnCodes));
					continue;
				}
				// 專案中分類
				String PROJECTTYPE = trim(columns[5]);
				if (StringUtils.isNotBlank(PROJECTTYPE)) {
					if(PROJECTTYPE.length() > 40) {
						typeError.add(columnLocation(index + 1, 5, columnCodes));
						continue;	
					} else {
						projectVO.setPROJECTTYPE(PROJECTTYPE);
					}		
				} else {
					requiredError.add(columnLocation(index + 1, 5, columnCodes));
					continue;
				}

				// 專案小分類
				String PROJECTTYPE1 = trim(columns[6]);
				if (StringUtils.isNotBlank(PROJECTTYPE1)) {
					if(PROJECTTYPE1.length() > 100) {
						typeError.add(columnLocation(index + 1, 6, columnCodes));
						continue;	
					} else {
						projectVO.setPROJECTTYPE1(PROJECTTYPE1);
					}
				} else {
					requiredError.add(columnLocation(index + 1, 6, columnCodes));
					continue;
				}

				// 保費固定
				String PERMER = trim(columns[7]);
				if (StringUtils.isNotBlank(PERMER)) {
					if(PERMER.length() > 2) {
						typeError.add(columnLocation(index + 1, 7, columnCodes));
						continue;	
					} else {
						projectVO.setPERMER(PERMER);
					}	
				} else {
					requiredError.add(columnLocation(index + 1, 7, columnCodes));
					continue;
				}
				// 開始銷售日
				String VALIDFROM = trim(columns[8]);
				if (StringUtils.isNotBlank(VALIDFROM)) {
					try {
						projectVO.setVALIDFROM(new Timestamp(sdf.parse(VALIDFROM).getTime()));
					} catch (Exception e) {
						typeError.add(columnLocation(index + 1, 8, columnCodes));
						continue;
					}
				} else {
					requiredError.add(columnLocation(index + 1, 8, columnCodes));
					continue;
				}

				// 停止銷售日
				String VALIDTHRU = trim(columns[9]);
				if (StringUtils.isNotBlank(VALIDTHRU)) {
					try {
						projectVO.setVALIDTHRU(new Timestamp(sdf.parse(VALIDTHRU).getTime()));
					} catch (Exception e) {
						typeError.add(columnLocation(index + 1, 9, columnCodes));
						continue;
					}
				} else {
					requiredError.add(columnLocation(index + 1, 9, columnCodes));
					continue;
				}

				// 保險公司產品名稱
				String INSCOPRODUCTNAME = trim(columns[10]);
				if (StringUtils.isNotBlank(INSCOPRODUCTNAME)) {
					if(INSCOPRODUCTNAME.length() > 1000) {
						typeError.add(columnLocation(index + 1, 10, columnCodes));
						continue;	
					} else {
						projectVO.setINSCOPRODUCTNAME(INSCOPRODUCTNAME);
					}
				} else {
					requiredError.add(columnLocation(index + 1, 10, columnCodes));
					continue;
				}
				// 保險公司產品代號
				String INSCOPRODUCTID = trim(columns[11]);
				if (StringUtils.isNotBlank(INSCOPRODUCTID)) {
					if(INSCOPRODUCTID.length() > 1000) {
						typeError.add(columnLocation(index + 1, 11, columnCodes));
						continue;	
					} else {
						projectVO.setINSCOPRODUCTID(INSCOPRODUCTID);
					}
				} else {
					requiredError.add(columnLocation(index + 1, 11, columnCodes));
					continue;
				}
				// 備註
				String MEMO = trim(columns[12]);
				if (StringUtils.isNotBlank(MEMO)) {
					if(MEMO.length() > 4000) {
						typeError.add(columnLocation(index + 1, 12, columnCodes));
						continue;	
					} else {
						projectVO.setMEMO(MEMO);
					}
				} else {
					requiredError.add(columnLocation(index + 1, 12, columnCodes));
					continue;
				}

				// 單位數
				String COVERAGEDUEPREMIUM = trim(columns[13]);
				if (StringUtils.isNotBlank(COVERAGEDUEPREMIUM)) {
					try {
						projectContVO.setCOVERAGEDUEPREMIUM(new BigDecimal(COVERAGEDUEPREMIUM).floatValue());
					} catch (Exception e) {
						typeError.add(columnLocation(index + 1, 13, columnCodes));
						continue;
					}
				} else {
					requiredError.add(columnLocation(index + 1, 13, columnCodes));
					continue;
				}

				// 保費
				String CONTCOVERAGEDUEPREMIUM = trim(columns[14]);
				if (StringUtils.isNotBlank(CONTCOVERAGEDUEPREMIUM)) {
					try {
						projectContVO.setCONTCOVERAGEDUEPREMIUM(new BigDecimal(CONTCOVERAGEDUEPREMIUM));
					} catch (Exception e) {
						typeError.add(columnLocation(index + 1, 14, columnCodes));
						continue;
					}
				} else {
					requiredError.add(columnLocation(index + 1, 14, columnCodes));
					continue;
				}

				// DB處理
				if (projectIDExist) {
					dam.update(projectVO);
					dam.update(projectContVO);
				} else {

					BigDecimal PROJECTSERIALNU = getNextPROJECTSERIALNUM();
					projectVO.setPROJECTSERIALNUM(PROJECTSERIALNU);
					dam.create(projectVO);

					// 日盛保代_保險產品專案管理副檔
					projectContVO.setPROJCONTSERIALNUM(getNextPROJCONTSERIALNUM());
					projectContVO.setPROJECTSERIALNUM(PROJECTSERIALNU);
					projectContVO.setPRODUCTSERIALNUM(new BigDecimal(PROD_MAIN.get(0).get("PRODUCTSERIALNUM").toString()));
					dam.create(projectContVO);
				}
			}
		}
		return_VO.setPROD_MAIN_notExist(PROD_MAIN_notExist);
		return_VO.setPROD_LIFEITEM_notExist(PROD_LIFEITEM_notExist);
		return_VO.setTypeError(typeError);
		return_VO.setRequiredError(requiredError);
		this.sendRtnObject(return_VO);
	}

	private List<Map<String, Object>> getPROD_LIFEITEM(String string) throws DAOException, JBranchException {
		List<Map<String, Object>> list = null;
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select PRODUCTSERIALNUM, ITEMSERIALNUM from TBJSB_INS_PROD_LIFEITEM where PRODUCTSERIALNUM = :PRODUCTSERIALNUM ");
		queryCondition.setObject("PRODUCTSERIALNUM", string);
		queryCondition.setQueryString(sql.toString());
		return dam.exeQuery(queryCondition);
	}

	private List<Map<String, Object>> getPROD_MAIN(String[] str) throws DAOException, JBranchException {
		List<Map<String, Object>> list = null;
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select PRODUCTSERIALNUM from TBJSB_INS_PROD_MAIN where PRODUCTID = :PRODUCTID ");
		queryCondition.setObject("PRODUCTID", StringUtils.isNotBlank(str[11]) ? str[11].trim() : "");
		queryCondition.setQueryString(sql.toString());
		return dam.exeQuery(queryCondition);
	}

	private List getPrimaryKeyList(String projectID) throws JBranchException {
		// TODO Auto-generated method stub
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select P.PROJECTSERIALNUM, PC.PROJCONTSERIALNUM ");
		sql.append("from TBJSB_INS_PROD_PROJECT P ");
		sql.append("left join TBJSB_INS_PROD_PROJECT_CONT PC on PC.PROJECTSERIALNUM = P.PROJECTSERIALNUM ");
		sql.append("WHERE P.PROJECTID = :PROJECTID ");
		queryCondition.setObject("PROJECTID", projectID);
		queryCondition.setQueryString(sql.toString());

		return dam.exeQuery(queryCondition);
	}

	private BigDecimal getNextPROJCONTSERIALNUM() throws JBranchException {
		// TODO Auto-generated method stub
		List<Map<String, BigDecimal>> result = Manager.manage(this.getDataAccessManager()).append("select MAX(PROJCONTSERIALNUM) + 1 SEQ from TBJSB_INS_PROD_PROJECT_CONT ").query();
		return new BigDecimal(result.get(0).get("SEQ").toString());
	}

	private BigDecimal getNextPROJECTSERIALNUM() throws JBranchException {
		// TODO Auto-generated method stub
		List<Map<String, BigDecimal>> result = Manager.manage(this.getDataAccessManager()).append("select MAX(PROJECTSERIALNUM) + 1 SEQ from TBJSB_INS_PROD_PROJECT ").query();
		return result.get(0).get("SEQ");
	}

	private void checkHaveDifferentCompany(List<String[]> dataCsv) throws APException {
		// TODO Auto-generated method stub
		for (int i = 1; i < dataCsv.size() - 1; i++) {
			String[] now = dataCsv.get(i);
			String[] next = dataCsv.get(i + 1);
			if (!StringUtils.equals(now[0], next[0])) {
				throw new APException("整批上傳每次僅可上傳單一家保險公司資料");
			}
		}
	}

	private void checkUploadHeader(String[] str) throws APException {
		// TODO Auto-generated method stub
		try {
			if (!"保險公司".equals(str[0].trim()))
				throw new Exception(str[0]);
			else if (!"專案代碼".equals(str[1].trim()))
				throw new Exception(str[1]);
			else if (!"專案名稱".equals(str[2].trim()))
				throw new Exception(str[2]);
			else if (!"通路型態".equals(str[3].trim()))
				throw new Exception(str[3]);
			else if (!"專案大分類".equals(str[4].trim()))
				throw new Exception(str[4]);
			else if (!"專案中分類".equals(str[5].trim()))
				throw new Exception(str[5]);
			else if (!"專案小分類".equals(str[6].trim()))
				throw new Exception(str[6]);
			else if (!"保費固定".equals(str[7].trim()))
				throw new Exception(str[7]);
			else if (!"開始銷售日".equals(str[8].trim()))
				throw new Exception(str[8]);
			else if (!"停止銷售日".equals(str[9].trim()))
				throw new Exception(str[9]);
			else if (!"保險公司產品名稱".equals(str[10].trim()))
				throw new Exception(str[10]);
			else if (!"保險公司產品代號".equals(str[11].trim()))
				throw new Exception(str[11]);
			else if (!"備註".equals(str[12].trim()))
				throw new Exception(str[12]);
			else if (!"單位數".equals(str[13].trim()))
				throw new Exception(str[13]);
			else if (!"保費".equals(str[14].trim()))
				throw new Exception(str[14]);
		} catch (Exception ex) {
			throw new APException(ex.getMessage() + ":上傳檔案header欄位錯誤，請下載範例檔案");
		}
	}

	/**
	 * 取得只有數字的部分
	 */
	private String getNumber(String str) {
		String regEx = "[^0-9.-]";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matchar = pattern.matcher(str);

		return matchar.replaceAll("").trim();
	}

	/**
	 * 取得只有字串的部分
	 */
	private String getString(String str) {
		String regEx = "[0-9.,-]";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matchar = pattern.matcher(str);

		return matchar.replaceAll("").trim().equals("$") ? "TWD" : matchar.replaceAll("").trim();
	}

	public void update(Object body, IPrimitiveMap header) throws Exception {
		PRD178InputVO inputVO = (PRD178InputVO) body;
		PRD178OutputVO return_VO = new PRD178OutputVO();

		dam = this.getDataAccessManager();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		// 用PROJECT_ID來確定PK
		List<Map<String, Object>> primaryKeyList = getPrimaryKeyList(inputVO.getPROJECTID());

		try {

			BigDecimal PROJECTSERIALNUM = new BigDecimal(primaryKeyList.get(0).get("PROJECTSERIALNUM").toString());
			BigDecimal PROJCONTSERIALNUM = new BigDecimal(primaryKeyList.get(0).get("PROJCONTSERIALNUM").toString());

			if (inputVO.isUpdateTBJSB_INS_PROD_PROJECT()) {
				// 日盛保代_保險產品專案管理檔
				TBJSB_INS_PROD_PROJECTVO projectVO = (TBJSB_INS_PROD_PROJECTVO) dam.findByPKey(TBJSB_INS_PROD_PROJECTVO.TABLE_UID, PROJECTSERIALNUM);

				projectVO.setPROJECTNAME(inputVO.getPROJECTNAME());
				projectVO.setDEPARTMENTTYPE(inputVO.getDEPARTMENTTYPE());
				projectVO.setPROJECTTYPE2(inputVO.getPROJECTTYPE2());
				projectVO.setPROJECTTYPE(inputVO.getPROJECTTYPE());
				projectVO.setPROJECTTYPE1(inputVO.getPROJECTTYPE1());
				projectVO.setPERMER(inputVO.getPERMER());
				projectVO.setVALIDFROM(new Timestamp(inputVO.getVALIDFROM().getTime()));
				projectVO.setVALIDTHRU(new Timestamp(inputVO.getVALIDTHRU().getTime()));
				projectVO.setINSCOPRODUCTNAME(inputVO.getINSCOPRODUCTNAME());
				projectVO.setINSCOPRODUCTID(inputVO.getINSCOPRODUCTID());
				projectVO.setMEMO(inputVO.getMEMO());

				dam.update(projectVO);
			}
			if (inputVO.isUpdateTBJSB_INS_PROD_PROJECT_CONT()) {
				// 日盛保代_保險產品專案管理副檔
				TBJSB_INS_PROD_PROJECT_CONTVO projectContVO = (TBJSB_INS_PROD_PROJECT_CONTVO) dam.findByPKey(TBJSB_INS_PROD_PROJECT_CONTVO.TABLE_UID, PROJCONTSERIALNUM);
				projectContVO.setCOVERAGEDUEPREMIUM(new BigDecimal(inputVO.getCOVERAGEDUEPREMIUM()).floatValue());
				projectContVO.setCONTCOVERAGEDUEPREMIUM(new BigDecimal(inputVO.getCONTCOVERAGEDUEPREMIUM()));
				dam.update(projectContVO);
			}
		} catch (Exception e) {
			throw new APException("刪除失敗，請洽管理人員。 " + e.getMessage());
		}
		this.sendRtnObject(return_VO);
	}

	public void delete(Object body, IPrimitiveMap header) throws Exception {
		PRD178InputVO inputVO = (PRD178InputVO) body;
		PRD178OutputVO return_VO = new PRD178OutputVO();

		dam = this.getDataAccessManager();

		// 查主鍵
		List<Map<String, Object>> primaryKeyList = getPrimaryKeyList(inputVO.getDeleteProjectID());

		try {
			BigDecimal PROJECTSERIALNUM = new BigDecimal(primaryKeyList.get(0).get("PROJECTSERIALNUM").toString());
			BigDecimal PROJCONTSERIALNUM = new BigDecimal(primaryKeyList.get(0).get("PROJCONTSERIALNUM").toString());

			// 日盛保代_保險產品專案管理檔
			TBJSB_INS_PROD_PROJECTVO projectVO = (TBJSB_INS_PROD_PROJECTVO) dam.findByPKey(TBJSB_INS_PROD_PROJECTVO.TABLE_UID, PROJECTSERIALNUM);
			dam.delete(projectVO);

			// 日盛保代_保險產品專案管理副檔
			TBJSB_INS_PROD_PROJECT_CONTVO projectContVO = (TBJSB_INS_PROD_PROJECT_CONTVO) dam.findByPKey(TBJSB_INS_PROD_PROJECT_CONTVO.TABLE_UID, PROJCONTSERIALNUM);
			dam.delete(projectContVO);

		} catch (Exception e) {
			throw new APException("刪除失敗，請洽管理人員。 " + e.getMessage());
		}

		this.sendRtnObject(return_VO);
	}

}
