package com.systex.jbranch.app.server.fps.crm3101;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LEADS_IMPVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LE_IMP_TEMPVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_AOCODE_CHGLOGVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_NOTEVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_AOCHG_PLISTVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_DELCAM_ULIST_BRHVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_DELCAM_ULIST_DTLVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_DELCAM_ULIST_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_PRJ_DTLVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_PRJ_MASTVO;
import com.systex.jbranch.app.server.fps.crm341.CRM341;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("crm3101")
@Scope("request")
public class CRM3101  extends FubonWmsBizLogic {
	private DataAccessManager dam = null;	
	private Logger logger = LoggerFactory.getLogger(CRM3101.class);
	private List<Map<String, Object>> scsslist = null ; 
	
	public void getExample(Object body, IPrimitiveMap header) throws Exception {
		notifyClientToDownloadFile("doc//CRM//CRM3101_EXAMPLE.csv", "專案調整名單.csv");
	    this.sendRtnObject(null);
	}
	
	/**下拉式選單:所有專案**/
	public void getAllPRJ(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3101InputVO inputVO = (CRM3101InputVO) body;
		CRM3101OutputVO return_VO = new CRM3101OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRJ_ID, PRJ_NAME, PRJ_STATUS FROM TBCRM_TRS_PRJ_MAST ");
		sql.append(" WHERE PRJ_TYPE IS NULL ");  //非輪調換手名單
		if(StringUtils.isNotBlank(inputVO.getComboflag())) {
			sql.append(" AND TRUNC(sysdate) BETWEEN TRUNC(PRJ_DATE_BGN) and TRUNC(PRJ_DATE_END) ");
			
			// 20191004 ADD MARK BY OCEAN ->> mantis:6691 專案名單匯入下拉選單(新增匯入)取消二年內建立的限制 by 君榮
//			// 限制下拉選單可看專案為兩年內的專案 20170619 add
//			sql.append("AND TRUNC(CREATETIME) BETWEEN ADD_MONTHS(TRUNC(SYSDATE),-24) AND TRUNC(SYSDATE) ");
		}
		
		sql.append("ORDER BY PRJ_ID DESC ");
		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		return_VO.setAllPRJ(list);
		sendRtnObject(return_VO);
	}
	
	public void getListName(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3101InputVO inputVO = (CRM3101InputVO) body;
		CRM3101OutputVO return_VO = new CRM3101OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT * FROM TBCRM_TRS_DELCAM_ULIST_MAST");
		List list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		sendRtnObject(return_VO);
	}
	
	/**查詢 **/
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3101InputVO inputVO = (CRM3101InputVO) body;
		CRM3101OutputVO return_VO = new CRM3101OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT M.PRJ_ID, M.PRJ_NAME, M.PRJ_NOTE, M.PRJ_DATE_BGN, M.PRJ_DATE_END, M.DESC_01, M.DESC_02, M.DESC_03, M.DESC_04, M.DESC_05, M.DESC_06, M.DESC_07, M.DESC_08, M.DESC_09, M.DESC_10, M.DESC_11, M.DESC_12, M.DESC_13, M.DESC_14, M.DESC_15, E.IMP_EMP_ID, D.EMP_NAME, E.IMP_FILE_NAME, M.PRJ_STATUS, ");
		sql.append("COUNT(E.SEQ) as TOTAL_CNT, ");
		sql.append("SUM( CASE WHEN E.IMP_SUCCESS_YN = 'Y' THEN 1 ELSE 0 END ) as IMP_S_CNT, ");
		sql.append("MIN(E.IMP_DATETIME) AS IMP_DATETIME, ");
		//20170620 添加執行人與執行時間(移轉、強制ao時會記錄MODIFIER、LASTUPDATE，選擇TRS_SUCCESS_YN有數值且使用MAX變成單一MODIFIER、MODIFIER_NAME、LASTUPDATE)
		sql.append("MAX(DECODE(E.TRS_SUCCESS_YN,'Y',E.MODIFIER,'N',E.MODIFIER)) AS MODIFIER, ");
		sql.append("MAX(DECODE(E.TRS_SUCCESS_YN,'Y',D1.EMP_NAME,'N',D1.EMP_NAME)) AS MODIFIER_NAME, ");
		sql.append("MAX(DECODE(E.TRS_SUCCESS_YN,'Y',E.LASTUPDATE,'N',E.LASTUPDATE)) AS MODIFIER_TIME ");
		
		sql.append("FROM TBCRM_TRS_PRJ_MAST M ");
		sql.append("LEFT JOIN TBCRM_TRS_PRJ_DTL E ON M.PRJ_ID = E.PRJ_ID ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO D ON E.IMP_EMP_ID=D.EMP_ID ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO D1 ON E.MODIFIER = D1.EMP_ID ");
		sql.append("WHERE PRJ_TYPE IS NULL ");  //非輪調換手名單
		// where
		if(StringUtils.isNotBlank(inputVO.getPRJ_ID())){
			sql.append("AND M.PRJ_ID = :prj_id ");
			queryCondition.setObject("prj_id", inputVO.getPRJ_ID());
		}
		if(StringUtils.isNotBlank(inputVO.getPRJ_STATUS())){
			sql.append("AND M.PRJ_STATUS = :status ");
			queryCondition.setObject("status", inputVO.getPRJ_STATUS());
		}
		if(StringUtils.isNotBlank(inputVO.getIMP_FILE_NAME())){
			sql.append("AND E.IMP_FILE_NAME = :imp_file_name ");
			queryCondition.setObject("imp_file_name", inputVO.getIMP_FILE_NAME());
		}
		if(inputVO.getSdate()!=null){
			sql.append("AND E.IMP_DATETIME >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getSdate());
		}
		if(inputVO.getEdate()!=null){
			sql.append("AND E.IMP_DATETIME < TRUNC(:end)+1 ");
			queryCondition.setObject("end", inputVO.getEdate());
		}
		sql.append("GROUP BY M.PRJ_ID, M.PRJ_NAME, M.PRJ_NOTE, M.PRJ_DATE_BGN, M.PRJ_DATE_END, M.DESC_01, M.DESC_02, M.DESC_03, M.DESC_04, M.DESC_05, M.DESC_06, M.DESC_07, M.DESC_08, M.DESC_09, M.DESC_10, M.DESC_11, M.DESC_12, M.DESC_13, M.DESC_14, M.DESC_15, M.PRJ_NAME, E.IMP_EMP_ID, D.EMP_NAME, E.IMP_FILE_NAME, TRUNC(E.IMP_DATETIME), M.PRJ_STATUS ");
		//依照專案建立日排序 20170619 add
		sql.append("ORDER BY TRUNC(E.IMP_DATETIME) DESC, M.PRJ_ID ");
		
		queryCondition.setQueryString(sql.toString());
		return_VO.setResultList(dam.exeQuery(queryCondition));
		this.sendRtnObject(return_VO);
	}
	
	public void inquire2(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3101InputVO inputVO = (CRM3101InputVO) body;
		CRM3101OutputVO return_VO = new CRM3101OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT M.SEQ, M.LIST_NAME, M.LIST_FILE_NAME, M.BRH_FILE_NAME, M.LIST_STATUS, SUM(1) as TOTAL_CNT, SUM(CASE WHEN NVL(D.DELCAM_BACK_YN,'N')='Y' THEN 1 ELSE 0 END) as BACK_CNT, M.CREATOR, ME.EMP_NAME, M.CREATETIME ");
		sql.append("FROM TBCRM_TRS_DELCAM_ULIST_MAST M, TBCRM_TRS_DELCAM_ULIST_DTL D, TBORG_MEMBER ME ");
		sql.append("WHERE 1=1 AND M.SEQ = D.LIST_SEQ AND M.CREATOR = ME.EMP_ID ");
		// where
		if(StringUtils.isNotBlank(inputVO.getList_name())){
			sql.append("AND M.SEQ = :seq ");
			queryCondition.setObject("seq", inputVO.getList_name());
		}
		if(inputVO.getSdate()!=null){
			sql.append("AND M.CREATETIME >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getSdate());
		}
		if(inputVO.getEdate()!=null){
			sql.append("AND M.CREATETIME < TRUNC(:end)+1 ");
			queryCondition.setObject("end", inputVO.getEdate());
		}
		sql.append("GROUP BY M.SEQ, M.LIST_NAME, M.LIST_FILE_NAME, M.BRH_FILE_NAME, M.LIST_STATUS, M.CREATOR, ME.EMP_NAME, M.CREATETIME ");
		//依照專案建立日排序 20170619 add
		sql.append("ORDER BY M.SEQ DESC");
		
		queryCondition.setQueryString(sql.toString());
		return_VO.setResultList(dam.exeQuery(queryCondition));
		this.sendRtnObject(return_VO);
	}
	
	public void delete(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3101InputVO inputVO = (CRM3101InputVO) body;
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("DELETE FROM TBCRM_TRS_DELCAM_ULIST_MAST WHERE SEQ = :seq");
		queryCondition.setObject("seq", inputVO.getSeq());
		dam.exeUpdate(queryCondition);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("DELETE FROM TBCRM_TRS_DELCAM_ULIST_DTL WHERE LIST_SEQ = :seq");
		queryCondition.setObject("seq", inputVO.getSeq());
		dam.exeUpdate(queryCondition);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("DELETE FROM TBCRM_TRS_DELCAM_ULIST_BRH WHERE LIST_SEQ = :seq");
		queryCondition.setObject("seq", inputVO.getSeq());
		dam.exeUpdate(queryCondition);
		
		this.sendRtnObject(null);
	}
	
	public void downloadMast(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3101InputVO inputVO = (CRM3101InputVO) body;
		CRM3101OutputVO return_VO = new CRM3101OutputVO();
		dam = this.getDataAccessManager();
		
		// double check
		String list_name = "";
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT LIST_STATUS, LIST_NAME FROM TBCRM_TRS_DELCAM_ULIST_MAST WHERE SEQ = :seq");
		queryCondition.setObject("seq", inputVO.getSeq());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(list.size() > 0) {
			String status = ObjectUtils.toString(list.get(0).get("LIST_STATUS"));
			if("1".equals(status) || "2".equals(status))
				list_name = ObjectUtils.toString(list.get(0).get("LIST_NAME"));
			else
				throw new APException("達康尚未回檔完成");
		} else
			throw new APException("達康尚未回檔完成");
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ULISTNO, BRH_COD, CONTACT_ADDRESS, BRH_OTH, POINTER_1, POINTER_2, ADDR_CITY, ADDR_AREA, KM_BRH_STR, KM_BRH_OTH, BRH_LST, KM_BRH_LST, BRH_SEC, KM_BRH_SEC ");
		sql.append("FROM TBCRM_TRS_DELCAM_ULIST_DTL ");
		sql.append("WHERE LIST_SEQ = :seq ");
		queryCondition.setObject("seq", inputVO.getSeq());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		if(list2.size() > 0) {
			// gen csv
			String fileName = "名單主檔." + list_name + ".csv";
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list2) {
				String[] records = new String[14];
				int i = 0;
				records[i] = checkIsNull(map, "ULISTNO");
				records[++i] = checkIsNull(map, "BRH_COD");
				records[++i] = checkIsNull(map, "CONTACT_ADDRESS");
				records[++i] = checkIsNull(map, "BRH_OTH");
				records[++i] = checkIsNull(map, "POINTER_1");
				records[++i] = checkIsNull(map, "POINTER_2");
				records[++i] = checkIsNull(map, "ADDR_CITY");
				records[++i] = checkIsNull(map, "ADDR_AREA");
				records[++i] = checkIsNull(map, "KM_BRH_STR");
				records[++i] = checkIsNull(map, "KM_BRH_OTH");
				records[++i] = checkIsNull(map, "BRH_LST");
				records[++i] = checkIsNull(map, "KM_BRH_LST");
				records[++i] = checkIsNull(map, "BRH_SEC");
				records[++i] = checkIsNull(map, "KM_BRH_SEC");
				listCSV.add(records);
			}
			// header
			String[] csvHeader = new String[14];
			int j = 0;
			csvHeader[j] = "序號";
			csvHeader[++j] = "分行別";
			csvHeader[++j] = "地址";
			csvHeader[++j] = "其他分行別";
			csvHeader[++j] = "指標1";
			csvHeader[++j] = "指標2";
			csvHeader[++j] = "縣市";
			csvHeader[++j] = "區域";
			csvHeader[++j] = "距離分行別距離";
			csvHeader[++j] = "距離其他分行別距離";
			csvHeader[++j] = "最近分行";
			csvHeader[++j] = "最近分行距離";
			csvHeader[++j] = "次近分行";
			csvHeader[++j] = "次近分行距離";
			
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
	
	public void saveUpload(Object body, IPrimitiveMap header) throws Exception {
		CRM3101InputVO inputVO = (CRM3101InputVO) body;
		dam = this.getDataAccessManager();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		// 新增一筆名單主檔TBCRM_TRS_DELCAM_ULIST_MAST
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT TBCRM_TRS_DELCAM_ULIST_MAS_SEQ.nextval AS SEQ FROM DUAL");
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// add TBCRM_TRS_DELCAM_ULIST_MAST
		TBCRM_TRS_DELCAM_ULIST_MASTVO mvo = new TBCRM_TRS_DELCAM_ULIST_MASTVO();
		mvo.setSEQ(seqNo);
		mvo.setLIST_NAME(inputVO.getList_name());
		mvo.setLIST_FILE_NAME(inputVO.getRealfileName());
		mvo.setBRH_FILE_NAME(inputVO.getRealfileName2());
		mvo.setLIST_STATUS("U");
		dam.create(mvo);
		
		// read file
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if(!dataCsv.isEmpty()) {
			for(int i = 0;i < dataCsv.size();i++) {
				if(i == 0) {
					continue;
				}
				String[] str = dataCsv.get(i);
				
				// seq
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString("SELECT TBCRM_TRS_DELCAM_ULIST_DTL_SEQ.nextval AS SEQ FROM DUAL");
				List<Map<String, Object>> SEQLIST2 = dam.exeQuery(queryCondition);
				BigDecimal seqNo2 = (BigDecimal) SEQLIST2.get(0).get("SEQ");
				// add TBCRM_TRS_DELCAM_ULIST_DTL
				try{
					TBCRM_TRS_DELCAM_ULIST_DTLVO dvo = new TBCRM_TRS_DELCAM_ULIST_DTLVO();
					dvo.setSEQ(seqNo2);
					dvo.setLIST_SEQ(seqNo);
					dvo.setSEQ_NO(sdf.format(new Date()) + getDaKanSeq());
					dvo.setULISTNO(str[0]);
					dvo.setBRH_COD(str[1]);
					dvo.setCONTACT_ADDRESS(str[2]);
					dvo.setBRH_OTH(str[3]);
					dvo.setPOINTER_1(str[4]);
					dvo.setPOINTER_2(str[5]);
					dvo.setSNAP_DATE(str[6]);
					dam.create(dvo);
				} catch (Exception e) {
					throw new APException("名單檔第" + i + "筆資料有問題，上傳失敗！");
				}
			}
		}
		// read file
		List<String[]> dataCsv2 = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName2());
		if(!dataCsv2.isEmpty()) {
			for(int i = 0;i < dataCsv2.size();i++) {
				if(i == 0) {
					continue;
				}
				String[] str = dataCsv2.get(i);
				
				// seq
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString("SELECT TBCRM_TRS_DELCAM_ULIST_BRH_SEQ.nextval AS SEQ FROM DUAL");
				List<Map<String, Object>> SEQLIST2 = dam.exeQuery(queryCondition);
				BigDecimal seqNo2 = (BigDecimal) SEQLIST2.get(0).get("SEQ");
				// add TBCRM_TRS_DELCAM_ULIST_BRH
				try{
					TBCRM_TRS_DELCAM_ULIST_BRHVO bvo = new TBCRM_TRS_DELCAM_ULIST_BRHVO();
					bvo.setSEQ(seqNo2);
					bvo.setLIST_SEQ(seqNo);
					bvo.setBRH_NBR(str[0]);
					bvo.setBRH_NAME(str[1]);
					bvo.setBRH_ADDR(str[2]);
					bvo.setBRH_TEL(str[3]);
					dam.create(bvo);
				} catch (Exception e) {
					throw new APException("分行檔第" + i + "筆資料有問題，上傳失敗！");
				}
			}
		}
		this.sendRtnObject(null);
	}
	
	public void downloadListFile(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3101InputVO inputVO = (CRM3101InputVO) body;
		CRM3101OutputVO return_VO = new CRM3101OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT ULISTNO, BRH_COD, CONTACT_ADDRESS, BRH_OTH, POINTER_1, POINTER_2, SNAP_DATE FROM TBCRM_TRS_DELCAM_ULIST_DTL WHERE LIST_SEQ = :seq");
		queryCondition.setObject("seq", inputVO.getSeq());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(list.size() > 0) {
			// gen csv
			String fileName = "名單主檔." + inputVO.getList_name() + ".csv";
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				String[] records = new String[7];
				int i = 0;
				records[i] = checkIsNull(map, "ULISTNO");
				records[++i] = checkIsNull(map, "BRH_COD");
				records[++i] = checkIsNull(map, "CONTACT_ADDRESS");
				records[++i] = checkIsNull(map, "BRH_OTH");
				records[++i] = checkIsNull(map, "POINTER_1");
				records[++i] = checkIsNull(map, "POINTER_2");
				records[++i] = checkIsNull(map, "SNAP_DATE");
				listCSV.add(records);
			}
			// header
			String[] csvHeader = new String[7];
			int j = 0;
			csvHeader[j] = "序號";
			csvHeader[++j] = "分行別";
			csvHeader[++j] = "地址";
			csvHeader[++j] = "其他分行別";
			csvHeader[++j] = "指標1";
			csvHeader[++j] = "指標2";
			csvHeader[++j] = "檔名完整日期";
			
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
	
	public void downloadBrhFile(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3101InputVO inputVO = (CRM3101InputVO) body;
		CRM3101OutputVO return_VO = new CRM3101OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT BRH_NBR, BRH_NAME, BRH_ADDR, BRH_TEL FROM TBCRM_TRS_DELCAM_ULIST_BRH WHERE LIST_SEQ = :seq");
		queryCondition.setObject("seq", inputVO.getSeq());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(list.size() > 0) {
			// gen csv
			String fileName = "名單主檔." + inputVO.getList_name() + ".csv";
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				String[] records = new String[4];
				int i = 0;
				records[i] = checkIsNull(map, "BRH_NBR");
				records[++i] = checkIsNull(map, "BRH_NAME");
				records[++i] = checkIsNull(map, "BRH_ADDR");
				records[++i] = checkIsNull(map, "BRH_TEL");
				listCSV.add(records);
			}
			// header
			String[] csvHeader = new String[4];
			int j = 0;
			csvHeader[j] = "分行代碼";
			csvHeader[++j] = "分行名稱";
			csvHeader[++j] = "分行地址";
			csvHeader[++j] = "分行電話";
			
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
	
	//既有專案
	public void getPRJMast(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3101InputVO inputVO = (CRM3101InputVO) body;
		CRM3101OutputVO return_VO = new CRM3101OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM TBCRM_TRS_PRJ_MAST WHERE PRJ_ID = :PRJ ");
		sql.append(" AND PRJ_TYPE IS NULL ");  //非輪調換手名單
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("PRJ", inputVO.getPRJ_ID());
		List list = dam.exeQuery(queryCondition);
		return_VO.setPRJList(list);
		sendRtnObject(return_VO);
	}
	
	//新增專案名稱或是修改 old code
	public void edit(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3101InputVO inputVO = (CRM3101InputVO) body;
		dam = this.getDataAccessManager();
		
		// 修改
		if(StringUtils.isNotBlank(inputVO.getPRJ_ID())) {
			TBCRM_TRS_PRJ_MASTVO vo = new TBCRM_TRS_PRJ_MASTVO();
			vo = (TBCRM_TRS_PRJ_MASTVO) dam.findByPKey(TBCRM_TRS_PRJ_MASTVO.TABLE_UID, inputVO.getPRJ_ID());
			if (vo != null) {
				vo.setPRJ_NAME(inputVO.getPRJ_NAME());
				vo.setPRJ_DATE_BGN(new Timestamp(inputVO.getSdate().getTime()));
				vo.setPRJ_DATE_END(new Timestamp(inputVO.getEdate().getTime()));
				vo.setPRJ_NOTE(inputVO.getPRJ_NOTE());
				vo.setDESC_01(StringUtils.trim(inputVO.getDESC_01()));
				vo.setDESC_02(StringUtils.trim(inputVO.getDESC_02()));
				vo.setDESC_03(StringUtils.trim(inputVO.getDESC_03()));
				vo.setDESC_04(StringUtils.trim(inputVO.getDESC_04()));
				vo.setDESC_05(StringUtils.trim(inputVO.getDESC_05()));
				vo.setDESC_06(StringUtils.trim(inputVO.getDESC_06()));
				vo.setDESC_07(StringUtils.trim(inputVO.getDESC_07()));
				vo.setDESC_08(StringUtils.trim(inputVO.getDESC_08()));
				vo.setDESC_09(StringUtils.trim(inputVO.getDESC_09()));
				vo.setDESC_10(StringUtils.trim(inputVO.getDESC_10()));
				vo.setDESC_11(StringUtils.trim(inputVO.getDESC_11()));
				vo.setDESC_12(StringUtils.trim(inputVO.getDESC_12()));
				vo.setDESC_13(StringUtils.trim(inputVO.getDESC_13()));
				vo.setDESC_14(StringUtils.trim(inputVO.getDESC_14()));
				vo.setDESC_15(StringUtils.trim(inputVO.getDESC_15()));
				dam.update(vo);
			} else
				throw new APException("ehl_01_common_005");
		}
		else {
			BigDecimal seqno = new BigDecimal(getSEQ()); //產生序號
			
			TBCRM_TRS_PRJ_MASTVO vo = new TBCRM_TRS_PRJ_MASTVO();
			vo.setPRJ_ID(seqno.toString());
			vo.setPRJ_NAME(inputVO.getPRJ_NAME());
			vo.setPRJ_DATE_BGN(new Timestamp(inputVO.getSdate().getTime()));
			vo.setPRJ_DATE_END(new Timestamp(inputVO.getEdate().getTime()));
			vo.setPRJ_NOTE(inputVO.getPRJ_NOTE());
			vo.setDESC_01(StringUtils.trim(inputVO.getDESC_01()));
			vo.setDESC_02(StringUtils.trim(inputVO.getDESC_02()));
			vo.setDESC_03(StringUtils.trim(inputVO.getDESC_03()));
			vo.setDESC_04(StringUtils.trim(inputVO.getDESC_04()));
			vo.setDESC_05(StringUtils.trim(inputVO.getDESC_05()));
			vo.setDESC_06(StringUtils.trim(inputVO.getDESC_06()));
			vo.setDESC_07(StringUtils.trim(inputVO.getDESC_07()));
			vo.setDESC_08(StringUtils.trim(inputVO.getDESC_08()));
			vo.setDESC_09(StringUtils.trim(inputVO.getDESC_09()));
			vo.setDESC_10(StringUtils.trim(inputVO.getDESC_10()));
			vo.setDESC_11(StringUtils.trim(inputVO.getDESC_11()));
			vo.setDESC_12(StringUtils.trim(inputVO.getDESC_12()));
			vo.setDESC_13(StringUtils.trim(inputVO.getDESC_13()));
			vo.setDESC_14(StringUtils.trim(inputVO.getDESC_14()));
			vo.setDESC_15(StringUtils.trim(inputVO.getDESC_15()));
			dam.create(vo);
		}
		this.sendRtnObject(null);
	}
	
	//加入移轉清單 old code
	public void doFunction(Object body, IPrimitiveMap header) throws Exception {
		CRM3101InputVO inputVO = (CRM3101InputVO) body;
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		//1-加入移轉清單 、2-強制AO 移轉、3-查詢移轉失敗、4-回收作業
		if("1".equals(inputVO.getActtype()) || "2".equals(inputVO.getActtype())) {
			sql.append(" SELECT DTL.SEQ, DTL.PRJ_ID, DTL.CUST_ID, M.CUST_NAME, DTL.NEW_AO_CODE, DTL.NEW_AO_BRH, INFO.EMP_ID AS NEW_EMP_ID, INFO.EMP_NAME AS NEW_EMP_NAME, INFO_1.EMP_NAME AS OLD_EMP_NAME, ");
	  		sql.append(" M.AO_CODE AS V_AO_CODE, M.BRA_NBR AS V_BRA_NBR,INFO_1.EMP_ID AS V_EMP_ID, MAST.CREATOR, DTL.IMP_DATETIME, RO.ROLE_ID, PRI.PRIVILEGEID ");
			sql.append(" FROM TBCRM_TRS_PRJ_DTL DTL ");
			sql.append(" LEFT JOIN TBCRM_TRS_PRJ_MAST MAST ON MAST.PRJ_ID = DTL.PRJ_ID "); 
			sql.append(" LEFT JOIN TBCRM_CUST_MAST M ON DTL.CUST_ID = M.CUST_ID ");
			//2017-12-4 by　Jacky MANTIS單號:0004025 VWORG_BRANCH_EMP_DETAIL_INFO只有主CODE沒有副code及維護code, 改用VWORG_AO_INFO
			sql.append(" LEFT JOIN VWORG_AO_INFO INFO ON DTL.NEW_AO_CODE = INFO.AO_CODE ");
			sql.append(" LEFT JOIN VWORG_AO_INFO INFO_1 ON M.AO_CODE = INFO_1.AO_CODE ");			
			sql.append(" LEFT JOIN (SELECT MR.EMP_ID, RO.ROLE_ID, RO.ROLE_NAME, RO.JOB_TITLE_NAME FROM TBORG_MEMBER_ROLE MR, TBORG_ROLE RO ");
			sql.append(" WHERE MR.ROLE_ID = RO.ROLE_ID AND MR.IS_PRIMARY_ROLE = 'Y' AND RO.REVIEW_STATUS = 'Y' AND RO.IS_AO = 'Y') RO ON INFO_1.EMP_ID = RO.EMP_ID ");
			sql.append(" LEFT JOIN TBSYSSECUROLPRIASS PRI ON RO.ROLE_ID = PRI.ROLEID ");
			sql.append(" WHERE DTL.PRJ_ID = :prj_id AND DTL.IMP_FILE_NAME = :file_name AND DTL.IMP_SUCCESS_YN='Y' ");
			queryCondition.setObject("prj_id", inputVO.getPRJ_ID());
			queryCondition.setObject("file_name", inputVO.getIMP_FILE_NAME());
			queryCondition.setQueryString(sql.toString());
			scsslist = dam.exeQuery(queryCondition);
			if("1".equals(inputVO.getActtype())) {
				getPlist(inputVO);
			}else if("2".equals(inputVO.getActtype())) {	
				getAOPlist(inputVO, dam);
			}
		} else if("3".equals(inputVO.getActtype())) {
			getFailLst(inputVO);
			
		} else if("4".equals(inputVO.getActtype())) {
			getBackHW(inputVO);
		} else if("5".equals(inputVO.getActtype())) {
			delPRJ(inputVO);
		} else if("6".equals(inputVO.getActtype())) {
			getNoFailLst(inputVO);
		} else if("7".equals(inputVO.getActtype())) {
			delOwnPRJ(inputVO);
		}
	}
		
	//加入移轉
	public void getPlist(CRM3101InputVO body) throws JBranchException {
		CRM3101OutputVO return_VO = new CRM3101OutputVO();
		dam = this.getDataAccessManager();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = new HashMap(xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2));	//理專
		Map<String, String> fchMap = new HashMap(xmlInfo.doGetVariable("FUBONSYS.FCH_ROLE", FormatHelper.FORMAT_2));	//FCH理專
		
		String error = "N";
		for(int i = 0 ; i < scsslist.size(); i++) {
			// old code from crm331
			String result = "Y";
			try {
				TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
				String seqno = getAOCHGSEQ(); //產生序號
				while(checkID(seqno, "PLIST")) {
					seqno = getAOCHGSEQ();
				}
				vo.setSEQ(new BigDecimal(seqno));
				vo.setCUST_ID(ObjectUtils.toString(scsslist.get(i).get("CUST_ID")));
				vo.setORG_AO_CODE(ObjectUtils.toString(scsslist.get(i).get("V_AO_CODE")));
				vo.setORG_AO_BRH(ObjectUtils.toString(scsslist.get(i).get("V_BRA_NBR")));
				vo.setNEW_AO_CODE(ObjectUtils.toString(scsslist.get(i).get("NEW_AO_CODE")));
				vo.setNEW_AO_BRH(ObjectUtils.toString(scsslist.get(i).get("NEW_AO_BRH")));
				vo.setAPL_EMP_ID((String)getUserVariable(FubonSystemVariableConsts.LOGINID));
				vo.setAPL_EMP_ROLE((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE));
				vo.setAPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
				vo.setAPL_REASON("98");
				// 判斷流程類型
				String TRS_FLOW_TYPE = "";
				if (StringUtils.isBlank(ObjectUtils.toString(scsslist.get(i).get("V_AO_CODE"))))
					TRS_FLOW_TYPE = "1";
				else if(StringUtils.equals(ObjectUtils.toString(scsslist.get(i).get("V_BRA_NBR")), ObjectUtils.toString(scsslist.get(i).get("NEW_AO_BRH"))))
					TRS_FLOW_TYPE = "2";
				else if(fchMap.containsKey(ObjectUtils.toString(scsslist.get(i).get("ROLE_ID"))) && !StringUtils.equals(ObjectUtils.toString(scsslist.get(i).get("V_BRA_NBR")), ObjectUtils.toString(scsslist.get(i).get("NEW_AO_BRH")))) {
					TRS_FLOW_TYPE = "3";
				}
				else if(fcMap.containsKey(ObjectUtils.toString(scsslist.get(i).get("ROLE_ID"))) && !StringUtils.equals(ObjectUtils.toString(scsslist.get(i).get("V_BRA_NBR")), ObjectUtils.toString(scsslist.get(i).get("NEW_AO_BRH")))) {
					TRS_FLOW_TYPE = "4";
				}
				//
				vo.setTRS_FLOW_TYPE(TRS_FLOW_TYPE);
				vo.setPROCESS_STATUS("L1");
				vo.setTRS_TYPE("4");
				vo.setTRS_TXN_SOURCE("4");
				vo.setPRJ_ID(ObjectUtils.toString(scsslist.get(i).get("PRJ_ID")));
				dam.create(vo);
			} catch (Exception e) {
				result = "N";
				error = "Y";
			}

			// 應該一定有SEQ
			TBCRM_TRS_PRJ_DTLVO vo = new TBCRM_TRS_PRJ_DTLVO();
			vo = (TBCRM_TRS_PRJ_DTLVO) dam.findByPKey(TBCRM_TRS_PRJ_DTLVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(scsslist.get(i).get("SEQ"))));
			vo.setTRS_SUCCESS_YN(result);
			dam.update(vo);
		}
		// TBCRM_TRS_PRJ_MAST 
		TBCRM_TRS_PRJ_MASTVO vo = new TBCRM_TRS_PRJ_MASTVO();
		vo = (TBCRM_TRS_PRJ_MASTVO) dam.findByPKey(TBCRM_TRS_PRJ_MASTVO.TABLE_UID, body.getPRJ_ID());
		if(vo!=null){
			vo.setPRJ_STATUS("06");
			dam.update(vo);
		}
		
		return_VO.setError(error);
		this.sendRtnObject(return_VO);
	}
	
	
	//強制ao移轉
	public void getAOPlist(CRM3101InputVO body, DataAccessManager dam) throws Exception {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		CRM3101OutputVO return_VO = new CRM3101OutputVO();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = new HashMap(xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2)); //理專
		Map<String, String> fchMap = new HashMap(xmlInfo.doGetVariable("FUBONSYS.FCH_ROLE", FormatHelper.FORMAT_2)); //FCH理專

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		String error = "N";
		Set<String> ao_List = new HashSet<String>();
		for (int i = 0; i < scsslist.size(); i++) {
			// 最終結果
			String result = "Y";
			String imp_status = null;
			// 判斷流程類型
			String TRS_FLOW_TYPE = "";
			if (StringUtils.isBlank(ObjectUtils.toString(scsslist.get(i).get("V_AO_CODE"))))
				TRS_FLOW_TYPE = "1";
			else if (StringUtils.equals(ObjectUtils.toString(scsslist.get(i).get("V_BRA_NBR")), ObjectUtils.toString(scsslist.get(i).get("NEW_AO_BRH"))))
				TRS_FLOW_TYPE = "2";
			else if (fchMap.containsKey(ObjectUtils.toString(scsslist.get(i).get("ROLE_ID"))) && !StringUtils.equals(ObjectUtils.toString(scsslist.get(i).get("V_BRA_NBR")), ObjectUtils.toString(scsslist.get(i).get("NEW_AO_BRH")))) {
				TRS_FLOW_TYPE = "3";
			} else if (fcMap.containsKey(ObjectUtils.toString(scsslist.get(i).get("ROLE_ID"))) && !StringUtils.equals(ObjectUtils.toString(scsslist.get(i).get("V_BRA_NBR")), ObjectUtils.toString(scsslist.get(i).get("NEW_AO_BRH")))) {
				TRS_FLOW_TYPE = "4";
			}
			
			//------------------------2017/7/20 add 檢查七個 其他都舊的------------------------
			String CUST_SCSS = ObjectUtils.toString(scsslist.get(i).get("CUST_ID"));
			CUST_SCSS = StringUtils.isBlank(CUST_SCSS) ? CUST_SCSS : CUST_SCSS.trim();
			Boolean Check = false;
			
			// 客戶ID不存在
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("SELECT CUST_ID FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id");
			queryCondition.setObject("cust_id", CUST_SCSS);
			List<Map<String, Object>> custExistList = dam.exeQuery(queryCondition);
			
			if (custExistList.size() > 0) {
				// 拔CODE不得跨分行
				if (StringUtils.isBlank(ObjectUtils.toString(scsslist.get(i).get("NEW_AO_CODE")))) {
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					queryCondition.setQueryString("SELECT BRA_NBR FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id AND BRA_NBR = :bra_nbr");
					queryCondition.setObject("cust_id", CUST_SCSS);
					queryCondition.setObject("bra_nbr", scsslist.get(i).get("NEW_AO_BRH"));
					List<Map<String, Object>> noCodeNoNewBraList = dam.exeQuery(queryCondition);
					
					if (noCodeNoNewBraList.size() == 0) {
						Check = true;
						imp_status = "10";
					}
				} else {
					// AO CODE不存在
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					queryCondition.setQueryString("SELECT AO_CODE FROM TBORG_SALES_AOCODE WHERE AO_CODE = :ao_code");
					queryCondition.setObject("ao_code", scsslist.get(i).get("NEW_AO_CODE"));
					List<Map<String, Object>> codeExistList = dam.exeQuery(queryCondition);
					
					if (codeExistList.size() == 0) {
						Check = true;
						imp_status = "02";
					}
				}
				
				// 前面都OK
				if (!Check) {
					// 分行 CODE不存在
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					queryCondition.setQueryString("SELECT DEPT_ID FROM TBORG_MEMBER WHERE DEPT_ID = :dept_id ");
					queryCondition.setObject("dept_id", scsslist.get(i).get("NEW_AO_BRH"));
					List<Map<String, Object>> deptExistList = dam.exeQuery(queryCondition);
					
					if (deptExistList.size() > 0) {
						// 新舊AO CODE相同
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						queryCondition.setQueryString("SELECT CUST_ID FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id AND AO_CODE = :ao_code ");
						queryCondition.setObject("cust_id", CUST_SCSS);
						queryCondition.setObject("ao_code", scsslist.get(i).get("NEW_AO_CODE"));
						List<Map<String, Object>> oldNewIsSameList = dam.exeQuery(queryCondition);
						
						if (oldNewIsSameList.size() > 0) {
							Check = true;
							imp_status = "04";
						} else {
							// 分行CODE與理專目前所屬分行CODE不符合
							// 2017/7/24 拔CODE AO_CODE null
							if (scsslist.get(i).get("NEW_AO_CODE") != null) {
								queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
								queryCondition.setQueryString("SELECT BRA_NBR FROM VWORG_AO_INFO WHERE AO_CODE = :ao_code AND BRA_NBR = :bra_nbr ");
								queryCondition.setObject("ao_code", scsslist.get(i).get("NEW_AO_CODE"));
								queryCondition.setObject("bra_nbr", scsslist.get(i).get("NEW_AO_BRH"));
								
								List<Map<String, Object>> codeBraNoMatchList = dam.exeQuery(queryCondition);
								if (codeBraNoMatchList.size() == 0) {
									Check = true;
									imp_status = "09";
								}
							}
							// 前面都OK
//							if(!Check) {
//								// 關係戶(主戶ID:xxxxxx)衝突
//								if(cust_list.contains(CUST_SCSS)) {
//									Check = true;
//									imp_status = "07";
//								}
//							}

							// WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」 ADD BY OCEAN
							queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							queryCondition.setQueryString("SELECT CUST_ID FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id AND UEMP_ID IS NOT NULL ");
							queryCondition.setObject("cust_id", CUST_SCSS);
							List<Map<String, Object>> uhrmList = dam.exeQuery(queryCondition);
							
							if (uhrmList.size() > 0) {
								Check = true;
								imp_status = "88";
							}

							//十保專區檢查-不可移回原理專
							//20200325 by Jacky 增加理專十保檢查
							CRM341 crm341 = (CRM341) PlatformContext.getBean("crm341");
							if (crm341.check10CMDTCust(CUST_SCSS, (String) scsslist.get(i).get("NEW_AO_CODE")) || crm341.check10CMDTCust2(CUST_SCSS, (String) scsslist.get(i).get("NEW_AO_CODE"))) {
								Check = true;
								imp_status = "20";
							}
							String check2023CMDTCust3 = crm341.check2023CMDTCust3(CUST_SCSS, (String) scsslist.get(i).get("NEW_AO_CODE"));
							if(StringUtils.equals("Y", check2023CMDTCust3)) {
								Check = true;
								imp_status = "21";
							}
							//高端客戶於總行專案匯入後做強制移轉時須作”六個月內不得回原理專之管控”
							String check20242CMDTCust = check20242CMDTCust(CUST_SCSS, (String) scsslist.get(i).get("NEW_AO_CODE"));
							if(StringUtils.equals("Y", check20242CMDTCust)) {
								Check = true;
								imp_status = "22";
							}
							
							//是否在ONCODE客戶排除列表中
							queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							queryCondition.setObject("cust_id",CUST_SCSS);
							queryCondition.setQueryString("SELECT 1 FROM TBCRM_TRS_CUST_EXCLUDE WHERE CUST_ID = :cust_id AND NVL(DEL_YN, 'N') = 'N' ");
							List<Map<String, Object>> onCodeList = dam.exeQuery(queryCondition);
							if(CollectionUtils.isNotEmpty(onCodeList)) {
								Check = true;
								imp_status = "30";
							}
						}
					} else {
						Check = true;
						imp_status = "03";
					}
				}
			} else {
				Check = true;
				imp_status = "01";
			}
			
			if (Check) {
				result = "N";
				error = "Y";
			}
			//----------------------------------end----------------------------------
			
			else {
				ao_List.add(ObjectUtils.toString(scsslist.get(i).get("V_AO_CODE")));
				ao_List.add(ObjectUtils.toString(scsslist.get(i).get("NEW_AO_CODE")));
				try {
					// 2017/9/18 add TBCRM_TRS_AOCHG_PLIST
					TBCRM_TRS_AOCHG_PLISTVO plist_vo = new TBCRM_TRS_AOCHG_PLISTVO();
					String p_seqno = getAOCHGSEQ(); //產生序號
					while (checkID(p_seqno, "PLIST")) {
						p_seqno = getAOCHGSEQ();
					}
					plist_vo.setSEQ(new BigDecimal(p_seqno));
					plist_vo.setCUST_ID(CUST_SCSS);
					plist_vo.setORG_AO_CODE(ObjectUtils.toString(scsslist.get(i).get("V_AO_CODE")));
					plist_vo.setORG_AO_BRH(ObjectUtils.toString(scsslist.get(i).get("V_BRA_NBR")));
					plist_vo.setNEW_AO_CODE(ObjectUtils.toString(scsslist.get(i).get("NEW_AO_CODE")));
					plist_vo.setNEW_AO_BRH(ObjectUtils.toString(scsslist.get(i).get("NEW_AO_BRH")));
					plist_vo.setHQ_MGR((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					plist_vo.setHQ_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
					plist_vo.setHQ_MGR_RPL_STATUS("Y");
					plist_vo.setACT_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
					plist_vo.setAPL_REASON("98");
					plist_vo.setTRS_FLOW_TYPE(TRS_FLOW_TYPE);
					plist_vo.setPROCESS_STATUS("S");
					plist_vo.setTRS_TYPE("4");
					plist_vo.setTRS_TXN_SOURCE("4");
					plist_vo.setPRJ_ID(ObjectUtils.toString(scsslist.get(i).get("PRJ_ID")));
					dam.create(plist_vo);
					//======執行生效後動作======
					
					// Step1.更新客戶主檔，並回傳名單檔案更新主機資料
					TBCRM_CUST_MASTVO vo_mast = new TBCRM_CUST_MASTVO();
					vo_mast = (TBCRM_CUST_MASTVO) dam.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, CUST_SCSS);
					vo_mast.setAO_CODE(ObjectUtils.toString(scsslist.get(i).get("NEW_AO_CODE")));
					vo_mast.setBRA_NBR(ObjectUtils.toString(scsslist.get(i).get("NEW_AO_BRH")));
					vo_mast.setAO_LASTUPDATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
					dam.update(vo_mast);

					// Step2.新增一筆到移轉紀錄TBCRM_CUST_AOCHG_LOG
					TBCRM_CUST_AOCODE_CHGLOGVO vo = new TBCRM_CUST_AOCODE_CHGLOGVO();
					String seqno = getCHGLOG_SEQ(); //產生序號
					while (checkID(seqno, "CHGLOG")) {
						seqno = getCHGLOG_SEQ();
					}
					vo.setSEQ(seqno);
					vo.setCUST_ID(CUST_SCSS);
					vo.setORG_AO_CODE(ObjectUtils.toString(scsslist.get(i).get("V_AO_CODE")));
					vo.setORG_AO_BRH(ObjectUtils.toString(scsslist.get(i).get("V_BRA_NBR")));
					vo.setORG_AO_NAME(ObjectUtils.toString(scsslist.get(i).get("OLD_EMP_NAME")));
					vo.setNEW_AO_CODE(ObjectUtils.toString(scsslist.get(i).get("NEW_AO_CODE")));
					vo.setNEW_AO_BRH(ObjectUtils.toString(scsslist.get(i).get("NEW_AO_BRH")));
					vo.setNEW_AO_NAME(ObjectUtils.toString(scsslist.get(i).get("NEW_EMP_NAME")));
					vo.setREG_AOCODE_EMP_ID((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					vo.setREG_AOCODE_SUB_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
					vo.setLETGO_EMP_ID((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					vo.setLETGO_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
					vo.setAPL_REASON("99");
					vo.setTRS_TXN_SOURCE("4");
					vo.setTRS_PRJ_NBR(ObjectUtils.toString(scsslist.get(i).get("PRJ_ID")));
					vo.setIMP_FILE_NAME(body.getIMP_FILE_NAME());
					dam.create(vo);

					// Step3-1.寫資料到名單匯入主檔TBCAM_SFA_LEADS_IMP
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuffer();
					sb.append("SELECT SEQNO ");
					sb.append("FROM TBCAM_SFA_LEADS_IMP ");
					sb.append("WHERE CAMPAIGN_ID = 'TRS' || TO_CHAR(current_date, 'YYYYMMDD') || 'FLW' || :trs_flow_type ");
					queryCondition.setObject("trs_flow_type", TRS_FLOW_TYPE);
					queryCondition.setQueryString(sb.toString());
					List<Map<String, Object>> imp_list = dam.exeQuery(queryCondition);
					
					if (imp_list.size() > 0) {
						//Step3-2.寫資料到名單匯入暫存檔(客戶明細)
						//Step3-2-1.get SQ_TBCAM_SFA_LE_IMP_TEMP
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb = new StringBuffer();
						sb.append("SELECT SQ_TBCAM_SFA_LE_IMP_TEMP.nextval AS SEQNO FROM DUAL");
						queryCondition.setQueryString(sb.toString());
						List<Map<String, Object>> leadsImptempSEQ = dam.exeQuery(queryCondition);
						
						BigDecimal seqNo_imptemp = (BigDecimal) leadsImptempSEQ.get(0).get("SEQNO");

						//Step3-2-2.get FRQ_DAY
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb = new StringBuffer();
						sb.append("SELECT FRQ_DAY ");
						sb.append("FROM VWCRM_CUST_REVIEWDATE_MAP ");
						sb.append("WHERE CON_DEGREE = (SELECT NVL(CON_DEGREE, 'S') FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id) ");
						sb.append("AND VIP_DEGREE = (SELECT NVL(VIP_DEGREE, 'M') FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id) ");
						queryCondition.setObject("cust_id", CUST_SCSS);
						queryCondition.setQueryString(sb.toString());
						List<Map<String, Object>> list_srq = dam.exeQuery(queryCondition);
						
						//Step3-2-3.update
						TBCAM_SFA_LE_IMP_TEMPVO vo_imp_temp = new TBCAM_SFA_LE_IMP_TEMPVO();
						vo_imp_temp.setSEQNO(seqNo_imptemp);
						vo_imp_temp.setIMP_SEQNO((BigDecimal) imp_list.get(0).get("SEQNO"));
						vo_imp_temp.setLEAD_ID("SYS" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + addZeroForNum(seqNo_imptemp.toString(), 8));
						vo_imp_temp.setCUST_ID(CUST_SCSS);
						vo_imp_temp.setCUST_NAME(ObjectUtils.toString(scsslist.get(i).get("CUST_NAME")));
						vo_imp_temp.setBRANCH_ID(ObjectUtils.toString(scsslist.get(i).get("NEW_AO_BRH")));
						vo_imp_temp.setEMP_ID(ObjectUtils.toString(scsslist.get(i).get("NEW_EMP_ID")));
						vo_imp_temp.setAO_CODE(ObjectUtils.toString(scsslist.get(i).get("NEW_AO_CODE")));
						vo_imp_temp.setSTART_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
						
						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.DAY_OF_WEEK, new BigDecimal(ObjectUtils.toString(list_srq.get(0).get("FRQ_DAY"))).intValue());
						vo_imp_temp.setEND_DATE(new Timestamp(cal.getTime().getTime()));
						
						if("3".equals(TRS_FLOW_TYPE)) {
							vo_imp_temp.setLEAD_TYPE("03");
						} else {
							vo_imp_temp.setLEAD_TYPE("04");
						}
						
						dam.create(vo_imp_temp);
					} else {
						//Step3-1.寫資料到名單匯入主檔TBCAM_SFA_LEADS_IMP
						//Step3-1-1.get SQ_TBCAM_SFA_LEADS_IMP
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb = new StringBuffer();
						sb.append("SELECT SQ_TBCAM_SFA_LEADS_IMP.nextval AS SEQNO FROM DUAL");
						queryCondition.setQueryString(sb.toString());
						List<Map<String, Object>> leadsImpSEQ = dam.exeQuery(queryCondition);
						
						BigDecimal seqNo_imp = (BigDecimal) leadsImpSEQ.get(0).get("SEQNO");

						//Step3-1-2.update
						TBCAM_SFA_LEADS_IMPVO vo_imp = new TBCAM_SFA_LEADS_IMPVO();
						vo_imp.setSEQNO(seqNo_imp);
						vo_imp.setCAMPAIGN_ID("TRS" + sdf.format(new Date()) + "FLW" + TRS_FLOW_TYPE);
						
						switch (TRS_FLOW_TYPE) {
							case "1":
								vo_imp.setCAMPAIGN_NAME("新Pool客戶聯繫名單 - 空Code");
								break;
							case "2":
								vo_imp.setCAMPAIGN_NAME("新Pool客戶聯繫名單 - 有Code 同分行");
								break;
							case "3":
								vo_imp.setCAMPAIGN_NAME("新Pool客戶聯繫名單 - 有Code FCH跨分行");
								break;
							case "4":
								vo_imp.setCAMPAIGN_NAME("新Pool客戶聯繫名單 - 有Code跨分行");
								break;
						}

						vo_imp.setCAMPAIGN_DESC("提醒理專聯繫並告知客戶已更換新理專");
						vo_imp.setSTEP_ID("0001");
						vo_imp.setLEAD_SOURCE_ID("04");
						
						Calendar c = Calendar.getInstance();
						vo_imp.setSTART_DATE(new Timestamp(c.getTime().getTime()));
						
						c.add(Calendar.DAY_OF_WEEK, 35);
						vo_imp.setEND_DATE(new Timestamp(c.getTime().getTime()));
						
						if("3".equals(TRS_FLOW_TYPE)) {
							vo_imp.setLEAD_TYPE("03");
						} else {
							vo_imp.setLEAD_TYPE("04");
						}
						
						vo_imp.setLEAD_PARA1("N");
						vo_imp.setLEAD_PARA2("N");
						vo_imp.setIMP_STATUS("IN");
						
						if ("002".equals(scsslist.get(i).get("PRIVILEGEID")))
							vo_imp.setFIRST_CHANNEL("FCALL");
						else if ("003".equals(scsslist.get(i).get("PRIVILEGEID")))
							vo_imp.setFIRST_CHANNEL("FCH");
						
						dam.create(vo_imp);
						
						//Step3-2.寫資料到名單匯入暫存檔(客戶明細) 
						//Step3-2-1.get SQ_TBCAM_SFA_LE_IMP_TEMP
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb = new StringBuffer();
						sb.append("SELECT SQ_TBCAM_SFA_LE_IMP_TEMP.nextval AS SEQNO FROM DUAL");
						queryCondition.setQueryString(sb.toString());
						List<Map<String, Object>> leadsImptempSEQ = dam.exeQuery(queryCondition);
						
						BigDecimal seqNo_imptemp = (BigDecimal) leadsImptempSEQ.get(0).get("SEQNO");
						
						//Step3-2-2.get FRQ_DAY
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb = new StringBuffer();
						sb.append("SELECT FRQ_DAY ");
						sb.append("FROM VWCRM_CUST_REVIEWDATE_MAP ");
						sb.append("WHERE CON_DEGREE = (SELECT NVL(CON_DEGREE, 'S') FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id) ");
						sb.append("AND VIP_DEGREE = (SELECT NVL(VIP_DEGREE, 'M') FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id) ");
						queryCondition.setObject("cust_id", CUST_SCSS);
						queryCondition.setQueryString(sb.toString());
						List<Map<String, Object>> list_srq = dam.exeQuery(queryCondition);
						
						//Step3-2-3.update
						TBCAM_SFA_LE_IMP_TEMPVO vo_imp_temp = new TBCAM_SFA_LE_IMP_TEMPVO();
						vo_imp_temp.setSEQNO(seqNo_imptemp);
						vo_imp_temp.setIMP_SEQNO(seqNo_imp);
						vo_imp_temp.setLEAD_ID("SYS" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + addZeroForNum(seqNo_imptemp.toString(), 8));
						vo_imp_temp.setCUST_ID(CUST_SCSS);
						vo_imp_temp.setCUST_NAME(ObjectUtils.toString(scsslist.get(i).get("CUST_NAME")));
						vo_imp_temp.setBRANCH_ID(ObjectUtils.toString(scsslist.get(i).get("NEW_AO_BRH")));
						vo_imp_temp.setEMP_ID(ObjectUtils.toString(scsslist.get(i).get("NEW_EMP_ID")));
						vo_imp_temp.setAO_CODE(ObjectUtils.toString(scsslist.get(i).get("NEW_AO_CODE")));
						vo_imp_temp.setSTART_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));

						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.DAY_OF_WEEK, new BigDecimal(ObjectUtils.toString(list_srq.get(0).get("FRQ_DAY"))).intValue());
						vo_imp_temp.setEND_DATE(new Timestamp(cal.getTime().getTime()));
						if("3".equals(TRS_FLOW_TYPE)) {
							vo_imp_temp.setLEAD_TYPE("03");
						} else {
							vo_imp_temp.setLEAD_TYPE("04");
						}
						
						dam.create(vo_imp_temp);
					}
					
					//Step3-3.當客戶改AO Code或歸屬行時，若理專沒執行該名單，則要將所有名單換AO Code
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuffer();
					sb.append("UPDATE TBCAM_SFA_LEADS ");
					sb.append("SET AO_CODE = :new_ao_code, ");
					sb.append("    EMP_ID = :new_ao_emp_id, ");
					sb.append("    BRANCH_ID = :new_ao_brh, ");
					sb.append("    MODIFIER = 'CRM3101.getAOPlist', ");
					sb.append("    LASTUPDATE = SYSDATE ");
					sb.append("WHERE CUST_ID = :cust_id ");
					sb.append("AND LEAD_STATUS < '03' ");
					sb.append("AND fn_is_fc(EMP_ID) = 1 "); //#1357
					queryCondition.setQueryString(sb.toString());
					queryCondition.setObject("new_ao_code", scsslist.get(i).get("NEW_AO_CODE"));
					queryCondition.setObject("new_ao_emp_id", scsslist.get(i).get("NEW_EMP_ID"));
					queryCondition.setObject("new_ao_brh", scsslist.get(i).get("NEW_AO_BRH"));
					queryCondition.setObject("cust_id", scsslist.get(i).get("CUST_ID"));
					
					dam.exeUpdate(queryCondition);

					//Step4.結束同一客戶在移轉流程重要性較低的移轉單(專案移轉類別:4重要性:2) ADD 2017/6/30
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuffer();
					sb.append("UPDATE TBCRM_TRS_AOCHG_PLIST ");
					sb.append("SET HQ_MGR = :emp_id, ");
					sb.append("    HQ_MGR_RPL_DATETIME = sysdate, ");
					sb.append("    HQ_MGR_RPL_STATUS = 'N', ");
					sb.append("    PROCESS_STATUS = 'F' ");
					sb.append("WHERE 1 = 1 ");
					sb.append("AND CUST_ID = :cust_id ");
					sb.append("AND PROCESS_STATUS in ('L1', 'L2', 'L3', 'L4', 'L5', 'BS') ");
					sb.append("AND TRS_TYPE in (");
					sb.append("  SELECT PARAM_CODE ");
					sb.append("  FROM TBSYSPARAMETER ");
					sb.append("  WHERE PARAM_TYPE = 'CRM.TRS_TYPE_PRIORITY' ");
					sb.append("  AND PARAM_ORDER >= (SELECT PARAM_ORDER FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CRM.TRS_TYPE_PRIORITY' AND PARAM_CODE = '4')");
					sb.append(") ");
					queryCondition.setQueryString(sb.toString());
					queryCondition.setObject("emp_id", getUserVariable(FubonSystemVariableConsts.LOGINID));
					queryCondition.setObject("cust_id", CUST_SCSS);
					
					dam.exeUpdate(queryCondition);

				} catch (Exception e) {
					result = "N";
					error = "Y";
					imp_status = "99";
				}
			}
			
			// 應該一定有SEQ
			TBCRM_TRS_PRJ_DTLVO vo = new TBCRM_TRS_PRJ_DTLVO();
			vo = (TBCRM_TRS_PRJ_DTLVO) dam.findByPKey(TBCRM_TRS_PRJ_DTLVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(scsslist.get(i).get("SEQ"))));
			vo.setTRS_SUCCESS_YN(result);
			vo.setIMP_STATUS(imp_status);
			dam.update(vo);
		}
		
		// TBCRM_TRS_PRJ_MAST 
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("UPDATE TBCRM_TRS_PRJ_MAST ");
		sb.append("SET PRJ_STATUS = '07' ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND PRJ_ID = :prjID ");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("prjID", body.getPRJ_ID());
		
		dam.exeUpdate(queryCondition);
		
		// mark by ocean : 20220331 發生exception，但無法確認因何發生，故更改寫法
//		TBCRM_TRS_PRJ_MASTVO vo = new TBCRM_TRS_PRJ_MASTVO();
//		vo = (TBCRM_TRS_PRJ_MASTVO) dam.findByPKey(TBCRM_TRS_PRJ_MASTVO.TABLE_UID, body.getPRJ_ID());
//		if (null != vo) {
//			vo.setPRJ_STATUS("07");
//			dam.update(vo);
//		}

		// jacky
//		CRM331 crm331 = (CRM331) PlatformContext.getBean("crm331");
//		crm331.count_by_ao_code_change(ao_List);

		return_VO.setError(error);
		
		this.sendRtnObject(return_VO);
	}

	
	//查看移轉失敗名單
	public void getFailLst(CRM3101InputVO body) throws JBranchException {
		CRM3101OutputVO return_VO = new CRM3101OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DTL.CUST_ID, DTL.NEW_AO_CODE, DTL.NEW_AO_BRH, DTL.IMP_STATUS, INFO1.EMP_NAME, INFO2.BRANCH_NAME ");
		sql.append("FROM TBCRM_TRS_PRJ_DTL DTL ");
		//2017-12-4 by　Jacky MANTIS單號:0004025 VWORG_BRANCH_EMP_DETAIL_INFO只有主CODE沒有副code及維護code, 改用VWORG_AO_INFO
		sql.append("LEFT JOIN VWORG_AO_INFO INFO1 ON DTL.NEW_AO_CODE = INFO1.AO_CODE ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO INFO2 ON DTL.NEW_AO_BRH = INFO2.BRANCH_NBR ");
		sql.append("WHERE DTL.TRS_SUCCESS_YN = 'N' AND DTL.PRJ_ID = :PRJ AND DTL.IMP_FILE_NAME = :IMP_FILE_NAME ");
		sql.append("ORDER BY DTL.PRJ_ID ");
		queryCondition.setObject("PRJ", body.getPRJ_ID());
		queryCondition.setObject("IMP_FILE_NAME", body.getIMP_FILE_NAME());
		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		return_VO.setAOFailLst(list);
		sendRtnObject(return_VO);
	}
	
	//回收作業
	private void getBackHW(CRM3101InputVO body) throws JBranchException {
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		Timestamp currentTM = new Timestamp(System.currentTimeMillis());
		sql.append(" SELECT CUST_ID, ORG_AO_CODE, ORG_AO_BRH FROM TBCRM_CUST_AOCODE_CHGLOG WHERE TRS_PRJ_NBR = :PRJ AND IMP_FILE_NAME = :IMP_FILE_NAME ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("PRJ", body.getPRJ_ID());
		queryCondition.setObject("IMP_FILE_NAME", body.getIMP_FILE_NAME());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(list.size() > 0) {
			for(int i = 0 ; i<list.size(); i++) {
				TBCRM_CUST_MASTVO vo1 =new TBCRM_CUST_MASTVO();
				vo1 = (TBCRM_CUST_MASTVO) dam.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, ObjectUtils.toString(list.get(i).get("CUST_ID")));
				if(vo1 != null) {
					vo1.setAO_CODE(ObjectUtils.toString(list.get(i).get("ORG_AO_CODE")));
					vo1.setBRA_NBR(ObjectUtils.toString(list.get(i).get("ORG_AO_BRH")));
					dam.update(vo1);
				}
			}
			// 將LOG刪除
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			condition.setQueryString("delete TBCRM_CUST_AOCODE_CHGLOG where TRS_PRJ_NBR = :PRJ AND IMP_FILE_NAME = :IMP_FILE_NAME");
			condition.setObject("PRJ", body.getPRJ_ID());
			condition.setObject("IMP_FILE_NAME", body.getIMP_FILE_NAME());
			dam.exeUpdate(condition);
			//
			TBCRM_TRS_PRJ_MASTVO vo2 =new TBCRM_TRS_PRJ_MASTVO();
			vo2 = (TBCRM_TRS_PRJ_MASTVO) dam.findByPKey(TBCRM_TRS_PRJ_MASTVO.TABLE_UID, body.getPRJ_ID());
			if(vo2!=null){
				vo2.setPRJ_STATUS("05");
				dam.update(vo2);
			}
		}
		this.sendRtnObject(null);
	}
	
	//刪除專案
	private void delPRJ(CRM3101InputVO body) throws JBranchException {
		CRM3101OutputVO return_VO = new CRM3101OutputVO();
		dam = this.getDataAccessManager();

		// 2017/9/28 刪除整個專案: 刪除TBCRM_TRS_PRJ_MAST & TBCRM_TRS_PRJ_DTL
		try{
			//將要刪除的資料搬移到HIST
			//TBCRM_TRS_AOCHG_PLIST_HIST
//			QueryConditionIF insertCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//			insertCondition.setQueryString("INSERT INTO TBCRM_TRS_AOCHG_PLIST_HIST SELECT * FROM TBCRM_TRS_AOCHG_PLIST WHERE PRJ_ID = :prj_id ");
//			insertCondition.setObject("prj_id",  body.getPRJ_ID());
//			dam.exeUpdate(insertCondition);
			
			//TBCRM_TRS_PRJ_MAST_HIST
			QueryConditionIF insertCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			insertCondition.setQueryString("INSERT INTO TBCRM_TRS_PRJ_MAST_HIST SELECT * FROM TBCRM_TRS_PRJ_MAST WHERE PRJ_ID = :prj_id ");
			insertCondition.setObject("prj_id",  body.getPRJ_ID());
			dam.exeUpdate(insertCondition);
			
			//TBCRM_TRS_PRJ_DTL_HIST
			insertCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			insertCondition.setQueryString("INSERT INTO TBCRM_TRS_PRJ_DTL_HIST SELECT * FROM TBCRM_TRS_PRJ_DTL WHERE PRJ_ID = :prj_id ");
			insertCondition.setObject("prj_id",  body.getPRJ_ID());
			dam.exeUpdate(insertCondition);
			
			//TBCRM_TRS_PRJ_MAST
			TBCRM_TRS_PRJ_MASTVO vo = new TBCRM_TRS_PRJ_MASTVO();
			vo = (TBCRM_TRS_PRJ_MASTVO) dam.findByPKey(TBCRM_TRS_PRJ_MASTVO.TABLE_UID, body.getPRJ_ID());
			if(vo!=null) {
				dam.delete(vo);
			}
			
			// TBCRM_TRS_PRJ_DTL
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			condition.setQueryString("delete TBCRM_TRS_PRJ_DTL where PRJ_ID = :prj_id");
			condition.setObject("prj_id", body.getPRJ_ID());
			dam.exeUpdate(condition);
			
			//TBCRM_TRS_AOCHG_PLIST
//			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//			condition.setQueryString("delete TBCRM_TRS_AOCHG_PLIST where PRJ_ID = :prj_id");
//			condition.setObject("prj_id", body.getPRJ_ID());
//			dam.exeUpdate(condition);
			
			return_VO.setError("N");
		} catch (Exception e) {
			return_VO.setError("Y");
		}
		
		this.sendRtnObject(return_VO);
	}
	
	private void delOwnPRJ(CRM3101InputVO body) throws JBranchException {
		CRM3101OutputVO return_VO = new CRM3101OutputVO();
		dam = this.getDataAccessManager();
		
		// 2017/9/28 刪除此筆匯入: 刪除TBCRM_TRS_PRJ_DTL
		try{
			// TBCRM_TRS_PRJ_DTL_HIST
			QueryConditionIF insertCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			insertCondition.setQueryString("INSERT INTO TBCRM_TRS_PRJ_DTL_HIST SELECT * FROM TBCRM_TRS_PRJ_DTL WHERE PRJ_ID = :prj_id AND IMP_FILE_NAME = :file_name ");
			insertCondition.setObject("prj_id",  body.getPRJ_ID());
			insertCondition.setObject("file_name", body.getIMP_FILE_NAME());
			dam.exeUpdate(insertCondition);
			
			// TBCRM_TRS_PRJ_DTL
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			condition.setQueryString("delete TBCRM_TRS_PRJ_DTL where PRJ_ID = :prj_id and IMP_FILE_NAME = :file_name ");
			condition.setObject("prj_id", body.getPRJ_ID());
			condition.setObject("file_name", body.getIMP_FILE_NAME());
			dam.exeUpdate(condition);
			
			return_VO.setError("N");
		} catch (Exception e) {
			return_VO.setError("Y");
		}
		
		this.sendRtnObject(return_VO);
	}
	
	//查看成功名單
	public void getNoFailLst(CRM3101InputVO body) throws JBranchException {
		CRM3101OutputVO return_VO = new CRM3101OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DTL.CUST_ID, DTL.NEW_AO_CODE, DTL.NEW_AO_BRH, INFO1.EMP_NAME, INFO2.BRANCH_NAME ");
		sql.append("FROM TBCRM_TRS_PRJ_DTL DTL ");
		//2017-12-4 by　Jacky MANTIS單號:0004025 VWORG_BRANCH_EMP_DETAIL_INFO只有主CODE沒有副code及維護code, 改用VWORG_AO_INFO
		sql.append("LEFT JOIN VWORG_AO_INFO INFO1 ON DTL.NEW_AO_CODE = INFO1.AO_CODE ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO INFO2 ON DTL.NEW_AO_BRH = INFO2.BRANCH_NBR ");
		sql.append("WHERE DTL.TRS_SUCCESS_YN = 'Y' AND DTL.PRJ_ID = :PRJ AND DTL.IMP_FILE_NAME = :IMP_FILE_NAME ");
		sql.append("ORDER BY DTL.PRJ_ID ");
		queryCondition.setObject("PRJ", body.getPRJ_ID());
		queryCondition.setObject("IMP_FILE_NAME", body.getIMP_FILE_NAME());
		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		return_VO.setAOScssLst(list);
		sendRtnObject(return_VO);
	}
	
	//失敗數
	public void getFailCount(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3101InputVO inputVO = (CRM3101InputVO) body;
		CRM3101OutputVO return_VO = new CRM3101OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DTL.CUST_ID, DTL.NEW_AO_CODE, DTL.NEW_AO_BRH, DTL.IMP_STATUS, INFO1.EMP_NAME, INFO2.BRANCH_NAME ");
		sql.append("FROM TBCRM_TRS_PRJ_DTL DTL ");
		//2017-12-4 by　Jacky MANTIS單號:0004025 VWORG_BRANCH_EMP_DETAIL_INFO只有主CODE沒有副code及維護code, 改用VWORG_AO_INFO
		sql.append("LEFT JOIN VWORG_AO_INFO INFO1 ON DTL.NEW_AO_CODE = INFO1.AO_CODE ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO INFO2 ON DTL.NEW_AO_BRH = INFO2.BRANCH_NBR ");
		sql.append("WHERE DTL.IMP_SUCCESS_YN = 'N' AND DTL.PRJ_ID = :PRJ AND DTL.IMP_FILE_NAME = :IMP_FILE_NAME ");
		queryCondition.setObject("PRJ", inputVO.getPRJ_ID());
		queryCondition.setObject("IMP_FILE_NAME", inputVO.getIMP_FILE_NAME());
		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		return_VO.setFailList(list);
		sendRtnObject(return_VO);
	}

	//上傳名單匯入
	public void upload(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3101InputVO inputVO = (CRM3101InputVO) body;
		CRM3101OutputVO return_VO = new CRM3101OutputVO();
		dam = this.getDataAccessManager();
		String RoleID = getUserVariable(FubonSystemVariableConsts.LOGINROLE).toString();
		
		// 2017/7/10 檢查上傳名單檔案不能重複
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select SEQ from TBCRM_TRS_PRJ_DTL where PRJ_ID = :prj_id and IMP_FILE_NAME = :file_name");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("prj_id", inputVO.getPRJ_ID());
		queryCondition.setObject("file_name", inputVO.getRealfileName());
		List<Map<String, Object>> exist_list = dam.exeQuery(queryCondition);
		if(exist_list.size() > 0)
			throw new APException("上傳名單檔案不能重複");
		
		// 紀錄專案主檔狀態為處理中
		dam.setAutoCommit(true);
		TBCRM_TRS_PRJ_MASTVO vo = new TBCRM_TRS_PRJ_MASTVO();
		vo = (TBCRM_TRS_PRJ_MASTVO) dam.findByPKey(TBCRM_TRS_PRJ_MASTVO.TABLE_UID, inputVO.getPRJ_ID());
		if (vo != null) {
			vo.setPRJ_NOTE(inputVO.getPRJ_NOTE());
			vo.setPRJ_STATUS("01");
			dam.update(vo);
		} else
			throw new APException("ehl_01_common_001");
		dam.setAutoCommit(false);
		
		// fail count
		int fail_count = 0;
		// 客戶ID重複2筆以上 use
		Set<String> idList = new HashSet<String>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if(!dataCsv.isEmpty()) {
			for(int i = 0;i < dataCsv.size();i++) {
				if(i == 0)
					continue;
				String[] str = dataCsv.get(i);
				if(str.length < 18)
					throw new APException("檔案格式錯誤，請依據下載範例");
				if(StringUtils.isBlank(str[0]))
					continue;
				
				String imp_status = null;
				// 2017/7/20 3264 以下僅檢查1、2.B、3、客戶ID重複2筆以上
				// 1:客戶ID不存在
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString("SELECT CUST_ID FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id");
				queryCondition.setObject("cust_id", str[0].trim());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if (list.size() == 0)
					imp_status = "01";
				else {
					//客戶為禁銷戶
					if(StringUtils.isNotBlank(str[0])) {
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						queryCondition.setQueryString("SELECT CUST_ID FROM TBCRM_CUST_NOTE WHERE CUST_ID = :cust_id AND COMM_NS_YN = 'Y' AND :role_id NOT IN (Select roleid from TBSYSSECUROLPRIASS where privilegeid IN ('071', '041'))");
						queryCondition.setObject("cust_id", str[0].trim());
						queryCondition.setObject("role_id", RoleID);
						List<Map<String, Object>> list1 = dam.exeQuery(queryCondition);
						if (list1 != null && !list1.isEmpty()){
							throw new APException(str[0].trim()+"為NS戶，無法申請客戶移入，請洽客戶關懷中心。");
						}
					}
					// AO CODE可放空值, 非空值才判斷是否存在
					if(StringUtils.isNotBlank(str[2])) {
						// 2.B:AO CODE不存在
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						queryCondition.setQueryString("SELECT AO_CODE FROM TBORG_SALES_AOCODE WHERE AO_CODE = :ao_code ");
						queryCondition.setObject("ao_code", str[2]);
						List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
						if (list2.size() == 0){
							imp_status = "02";
						}
					}
					// 前面掛了後面都不用做
					if(StringUtils.isBlank(imp_status)) {
						// 3:分行 CODE不存在
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sql = new StringBuffer();
						sql.append("SELECT DEPT_ID FROM TBORG_MEMBER WHERE DEPT_ID = :dept_id ");
						queryCondition.setObject("dept_id", str[1]);
						queryCondition.setQueryString(sql.toString());
						List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
						if (list3.size() == 0)
							imp_status = "03";
						else {
							// 客戶ID重複2筆以上
							if(idList.contains(str[0].trim()))
								imp_status = "05";
							idList.add(str[0].trim());
						}
					}
				}
				
				// add
				// seq
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT SQ_TBCRM_TRS_PRJ_DTL.nextval AS SEQ FROM DUAL ");
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
				BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
				// ORG_AO_CODE, ORG_AO_BRH
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT AO_CODE, BRA_NBR FROM TBCRM_CUST_MAST WHERE CUST_ID = :id ");
				queryCondition.setObject("id", str[0].trim());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> cust_list = dam.exeQuery(queryCondition);
				String ao_code = "";String bra_nbr = "";
				if(cust_list.size() != 0) {
					ao_code = ObjectUtils.toString(cust_list.get(0).get("AO_CODE"));
					bra_nbr = ObjectUtils.toString(cust_list.get(0).get("BRA_NBR"));
				}
				
				TBCRM_TRS_PRJ_DTLVO dtlvo = new TBCRM_TRS_PRJ_DTLVO();
				dtlvo.setSEQ(seqNo);
				dtlvo.setPRJ_ID(inputVO.getPRJ_ID());
				dtlvo.setCUST_ID(str[0].trim());
				dtlvo.setORG_AO_BRH(bra_nbr);
				dtlvo.setNEW_AO_BRH(str[1]);
				dtlvo.setORG_AO_CODE(ao_code);
				dtlvo.setNEW_AO_CODE(str[2]);
				dtlvo.setDATA_01(str[3]);
				dtlvo.setDATA_02(str[4]);
				dtlvo.setDATA_03(str[5]);
				dtlvo.setDATA_04(str[6]);
				dtlvo.setDATA_05(str[7]);
				dtlvo.setDATA_06(str[8]);
				dtlvo.setDATA_07(str[9]);
				dtlvo.setDATA_08(str[10]);
				dtlvo.setDATA_09(str[11]);
				dtlvo.setDATA_10(str[12]);
				dtlvo.setDATA_11(str[13]);
				dtlvo.setDATA_12(str[14]);
				dtlvo.setDATA_13(str[15]);
				dtlvo.setDATA_14(str[16]);
				dtlvo.setDATA_15(str[17]);
				if(StringUtils.isBlank(imp_status))
					dtlvo.setIMP_SUCCESS_YN("Y");
				else {
					dtlvo.setIMP_SUCCESS_YN("N");
					dtlvo.setIMP_STATUS(imp_status);
					fail_count++;
				}
				dtlvo.setIMP_EMP_ID(ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINID)));
				dtlvo.setIMP_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
				dtlvo.setIMP_FILE_NAME(inputVO.getRealfileName());
				dam.create(dtlvo);
			}
		}
		
		if(fail_count > 0)
			vo.setPRJ_STATUS("03");
		else
			vo.setPRJ_STATUS("02");
		dam.update(vo);
		
		this.sendRtnObject(null);
	}
	
	//下載表單
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3101InputVO inputVO = (CRM3101InputVO) body;
		dam = this.getDataAccessManager();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> prjSatusMap = xmlInfo.doGetVariable("CRM.TRS_PRJ_STATUS", FormatHelper.FORMAT_3);
		
		if(inputVO.getList().size() > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String fileName = "專案調整名單匯出統計表"+sdf.format(new Date())+".csv"; 
			List listCSV =  new ArrayList();
			for(Map<String, Object> map : inputVO.getList()) {
				String[] records = new String[9];
				int i = 0;
				records[i] = checkIsNull(map, "IMP_EMP_ID");  
				records[++i] = checkIsNull(map, "IMP_DATETIME");  
				records[++i] = checkIsNull(map, "PRJ_NAME");  
				records[++i] = checkIsNull(map, "IMP_FILE_NAME");  
				records[++i] = prjSatusMap.get(checkIsNull(map, "PRJ_STATUS"));  
				records[++i] = checkIsNull(map, "TOTAL_CNT");  
				records[++i] = checkIsNull(map, "IMP_S_CNT");
				records[++i] = String.valueOf(Double.valueOf(checkIsNull(map, "TOTAL_CNT")) - Double.valueOf(checkIsNull(map, "IMP_S_CNT")));
				listCSV.add(records);
			}
			//header
			String [] csvHeader = new String[9];
			int j = 0;
			csvHeader[j]   = "匯入人員";
			csvHeader[++j] = "匯入時間";
			csvHeader[++j] = "專案名稱";
			csvHeader[++j] = "檔案名稱";
			csvHeader[++j] = "匯入狀態";
			csvHeader[++j] = "應匯入數";
			csvHeader[++j] = "實際匯入數";
			csvHeader[++j] = "失敗數";
			

			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			notifyClientToDownloadFile(url, fileName); //download
		} else {
			
			this.sendRtnObject(null);
		}
	}
	
	
	/**產生seq No */
	private String getSEQ() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TBCRM_TRS_PRJ_MAST_SEQ.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	// TBCRM_TRS_AOCHG_PLIST old code
	private String getAOCHGSEQ() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TBCRM_TRS_AOCHG_PLIST_SEQ.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	private String getCHGLOG_SEQ() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TBCRM_CUST_AOCODE_CHGLOG_SEQ.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	private Boolean checkID(String pk, String type) throws JBranchException {
		Boolean ans = false;
		if(StringUtils.equals("PLIST", type)) {
			BigDecimal check = new BigDecimal(pk);
			TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
			vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, check);
			if (vo != null)
				ans = true;
		} else {
			TBCRM_CUST_AOCODE_CHGLOGVO vo = new TBCRM_CUST_AOCODE_CHGLOGVO();
			vo = (TBCRM_CUST_AOCODE_CHGLOGVO) dam.findByPKey(TBCRM_CUST_AOCODE_CHGLOGVO.TABLE_UID, pk);
			if (vo != null)
				ans = true;
		}
		return ans;
	}
	
	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf(map.get(key));
		}else{
			return "";
		}
	}
	public String addZeroForNum(String str, int strLength) {
	    int strLen = str.length();
	    if (strLen < strLength) {
	        while (strLen < strLength) {
	            StringBuffer sb = new StringBuffer();
	            sb.append("0").append(str);// 左補0
	            // sb.append(str).append("0");//右補0
	            str = sb.toString();
	            strLen = str.length();
	        }
	    }
	    return str;
	}
	
	private String getDaKanSeq() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBCRM_TRS_DAKAN.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
	//專案匯入-失敗數與移轉失敗名單
	public void exportFail(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3101InputVO inputVO = (CRM3101InputVO) body;
		dam = this.getDataAccessManager();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> prjSatusMap = xmlInfo.doGetVariable("CRM.TRS_PRJ_DTL_IMP_STATUS", FormatHelper.FORMAT_3);
		
		if(inputVO.getList().size() > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String fileName = "專案調整失敗名單"+sdf.format(new Date())+".csv"; 
			List listCSV =  new ArrayList();
			for(Map<String, Object> map : inputVO.getList()) {
				String[] records = new String[4];
				int i = 0;
				records[i] = checkIsNull(map, "CUST_ID");  
				records[++i] = checkIsNull(map, "NEW_AO_CODE") + '-' + checkIsNull(map, "EMP_NAME");  
				records[++i] = checkIsNull(map, "NEW_AO_BRH") + '-' + checkIsNull(map, "BRANCH_NAME");  
				if ("3".equals(inputVO.getActtype())) {   //移轉失敗名單
					records[++i] = prjSatusMap.get(checkIsNull(map, "IMP_STATUS"));
				} else { //失敗數
					records[++i] = prjSatusMap.get(checkIsNull(map, "IMP_STATUS"));
				}
				listCSV.add(records);
			}
			//header
			String [] csvHeader = new String[4];
			int j = 0;
			csvHeader[j]   = "客戶ID";
			csvHeader[++j] = "新理專Code";
			csvHeader[++j] = "新分行Code";
			csvHeader[++j] = "失敗原因";

			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			notifyClientToDownloadFile(url, fileName); //download
		} else {
			this.sendRtnObject(null);
		}
	}
	
	//專案匯入 - 移轉成功名單
	public void exportScss(Object body, IPrimitiveMap header) throws JBranchException {
		CRM3101InputVO inputVO = (CRM3101InputVO) body;
		dam = this.getDataAccessManager();
		
		if(inputVO.getList().size() > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String fileName = "專案調整成功名單"+sdf.format(new Date())+".csv"; 
			List listCSV =  new ArrayList();
			for(Map<String, Object> map : inputVO.getList()) {
				String[] records = new String[3];
				int i = 0;
				records[i] = checkIsNull(map, "CUST_ID");  
				records[++i] = checkIsNull(map, "NEW_AO_CODE") + '-' + checkIsNull(map, "EMP_NAME");  
				records[++i] = checkIsNull(map, "NEW_AO_BRH") + '-' + checkIsNull(map, "BRANCH_NAME");  
				
				listCSV.add(records);
			}
			//header
			String [] csvHeader = new String[4];
			int j = 0;
			csvHeader[j]   = "客戶ID";
			csvHeader[++j] = "新理專Code";
			csvHeader[++j] = "新分行Code";

			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			notifyClientToDownloadFile(url, fileName); //download
		} else {
			this.sendRtnObject(null);
		}
	}
	
	/***
	 * 加強管控名單：
	 * 高端客戶於總行專案匯入後做強制移轉時須作”六個月內不得回原理專之管控”
	 * @param custID
	 * @param newAoCode
	 * @return "Y"：加強管控名單6個月內要移回原理專的高端客戶
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private String check20242CMDTCust(String custID,String newAoCode) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		//加強管控名單：是否為6個月內要移回原理專的高端客戶
		sql.append("SELECT A.CUST_ID ");
		sql.append(" FROM TBPMS_ROTATION_MAIN A ");
		sql.append(" INNER JOIN TBCRM_TRS_PRJ_MAST B ON B.PRJ_ID = A.PRJ_ID ");
		sql.append(" WHERE B.PRJ_TYPE = '2' AND B.PRJ_SUB_TYPE = '2' "); //加強管控專案高端類型
		sql.append("   AND A.CUST_ID = :custID ");
		sql.append("   AND A.AO_CODE IN (SELECT AO_CODE FROM VWORG_AO_INFO WHERE EMP_ID = (SELECT EMP_ID FROM VWORG_AO_INFO WHERE AO_CODE = :newAoCode)) "); 
		sql.append("   AND TRUNC(SYSDATE) BETWEEN TRUNC(B.PRJ_EXE_DATE) AND TRUNC(ADD_MONTHS(B.PRJ_EXE_DATE, 6)) "); //專案執行日~專案執行日+6個月之間
		
		queryCondition.setObject("custID", custID);
		queryCondition.setObject("newAoCode", newAoCode);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		return CollectionUtils.isNotEmpty(list) ? "Y" : "N"; //Y:加強管控名單6個月內要移回原理專的高端客戶
	}
}
