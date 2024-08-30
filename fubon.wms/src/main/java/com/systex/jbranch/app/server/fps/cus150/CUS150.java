package com.systex.jbranch.app.server.fps.cus150;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("cus150")
@Scope("request")
public class CUS150 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CUS150.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CUS150InputVO inputVO = (CUS150InputVO) body;
		CUS150OutputVO return_VO = new CUS150OutputVO();
		dam = this.getDataAccessManager();
		
		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行
		Map<String, String> armgrMap   = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);	//業務處
		Map<String, String> mbrmgrMap  = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);	//營運區
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * from ( ");
		sql.append("SELECT a.IVG_PLAN_NAME,a.IVG_PLAN_DESC,a.IVG_TYPE,a.IVG_PLAN_TYPE,a.IVG_START_DATE,a.IVG_END_DATE,a.MODIFIER,a.IVG_PLAN_SEQ, ");
		// TOTAL_COUNT
		sql.append("(SELECT COUNT (con1.IVG_PLAN_SEQ) FROM TBCAM_IVG_PLAN_CONTENT con1 ");
		sql.append("WHERE con1.IVG_PLAN_SEQ = a.IVG_PLAN_SEQ ");
		if (headmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append(" AND (con1.REGION_CENTER_ID IN (:rcIdList) OR con1.REGION_CENTER_ID IS NULL) ");
			sql.append(" AND (con1.BRANCH_AREA_ID IN (:opIdList) OR con1.BRANCH_AREA_ID IS NULL) ");
			sql.append(" AND (con1.BRANCH_NBR IN (:brNbrList) OR con1.BRANCH_NBR IS NULL) ");
		}
		else if (armgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append(" AND (con1.REGION_CENTER_ID IN (:rcIdList)) ");
			sql.append(" AND (con1.BRANCH_AREA_ID IN (:opIdList) OR con1.BRANCH_AREA_ID IS NULL) ");
			sql.append(" AND (con1.BRANCH_NBR IN (:brNbrList) OR con1.BRANCH_NBR IS NULL) ");
		}
		else if (mbrmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append(" AND (con1.REGION_CENTER_ID IN (:rcIdList)) ");
			sql.append(" AND (con1.BRANCH_AREA_ID IN (:opIdList)) ");
			sql.append(" AND (con1.BRANCH_NBR IN (:brNbrList) OR con1.BRANCH_NBR IS NULL) ");
		}
		else {
			sql.append(" AND (con1.REGION_CENTER_ID IN (:rcIdList)) ");
			sql.append(" AND (con1.BRANCH_AREA_ID IN (:opIdList)) ");
			sql.append(" AND (con1.BRANCH_NBR IN (:brNbrList)) ");
		}
		sql.append(") AS TOTAL_COUNT, ");
		// NO_RETURN
		sql.append("(SELECT COUNT (con2.IVG_PLAN_SEQ) FROM TBCAM_IVG_PLAN_CONTENT con2 ");
		sql.append("WHERE con2.IVG_PLAN_SEQ = a.IVG_PLAN_SEQ AND RES_FLAG = 'N' ");
		if (headmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append(" AND (con2.REGION_CENTER_ID IN (:rcIdList) OR con2.REGION_CENTER_ID IS NULL) ");
			sql.append(" AND (con2.BRANCH_AREA_ID IN (:opIdList) OR con2.BRANCH_AREA_ID IS NULL) ");
			sql.append(" AND (con2.BRANCH_NBR IN (:brNbrList) OR con2.BRANCH_NBR IS NULL) ");
		}
		else if (armgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append(" AND (con2.REGION_CENTER_ID IN (:rcIdList)) ");
			sql.append(" AND (con2.BRANCH_AREA_ID IN (:opIdList) OR con2.BRANCH_AREA_ID IS NULL) ");
			sql.append(" AND (con2.BRANCH_NBR IN (:brNbrList) OR con2.BRANCH_NBR IS NULL) ");
		}
		else if (mbrmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append(" AND (con2.REGION_CENTER_ID IN (:rcIdList)) ");
			sql.append(" AND (con2.BRANCH_AREA_ID IN (:opIdList)) ");
			sql.append(" AND (con2.BRANCH_NBR IN (:brNbrList) OR con2.BRANCH_NBR IS NULL) ");
		}
		else {
			sql.append(" AND (con2.REGION_CENTER_ID IN (:rcIdList)) ");
			sql.append(" AND (con2.BRANCH_AREA_ID IN (:opIdList)) ");
			sql.append(" AND (con2.BRANCH_NBR IN (:brNbrList)) ");
		}
		queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		sql.append(") AS NO_RETURN, ");
		//
		sql.append("a.LASTUPDATE,b.DOC_ID,b.DOC_NAME,c.EMP_NAME AS MEMP_NAME FROM TBCAM_IVG_PLAN_MAIN a ");
		sql.append("LEFT JOIN TBSYS_FILE_MAIN b ON a.DOC_ID = b.DOC_ID ");
		sql.append("LEFT JOIN TBORG_MEMBER c ON a.MODIFIER = c.EMP_ID WHERE 1=1 ");
		// WHERE
		if (!StringUtils.isBlank(inputVO.getIvgPlanName())) {
			sql.append("AND a.IVG_PLAN_NAME LIKE :plan_name ");
			queryCondition.setObject("plan_name", "%" + inputVO.getIvgPlanName() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getIvgType())) {
			sql.append("AND a.IVG_TYPE = :type ");
			queryCondition.setObject("type", inputVO.getIvgType());
		}
		// 計畫狀態 mantis always 有效
		sql.append("AND a.IVG_PLAN_TYPE = '1' and TRUNC(sysdate) between TRUNC(a.IVG_START_DATE) and TRUNC(a.IVG_END_DATE) ");
//		if (!StringUtils.isBlank(inputVO.getIvgPlanType())) {
//			sql.append("AND a.IVG_PLAN_TYPE = :plan_type ");
//			queryCondition.setObject("plan_type", inputVO.getIvgPlanType());
//		}
		if (inputVO.getIvgStartDate() != null) {
			sql.append("AND a.IVG_START_DATE >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getIvgStartDate());
		}
		if (inputVO.getIvgEndDate() != null) {
			sql.append("AND a.IVG_START_DATE < TRUNC(:end)+1 ");
			queryCondition.setObject("end", inputVO.getIvgEndDate());
		}
		//
		sql.append(") temp where temp.TOTAL_COUNT <> 0 ");
		queryCondition.setQueryString(sql.toString());
		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();
		return_VO.setResultList(list);
		return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		return_VO.setTotalPage(totalPage_i);// 總頁次
		return_VO.setTotalRecord(totalRecord_i);// 總筆數
		this.sendRtnObject(return_VO);
	}
	
	public void getDetail(Object body, IPrimitiveMap header) throws JBranchException {
		CUS150InputVO inputVO = (CUS150InputVO) body;
		CUS150OutputVO return_VO = new CUS150OutputVO();
		dam = this.getDataAccessManager();
		
		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行
		Map<String, String> armgrMap   = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);	//業務處
		Map<String, String> mbrmgrMap  = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);	//營運區
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("WITH TEMP AS (select * from vworg_dept_br) ");
		sql.append("select DISTINCT a.REGION_CENTER_ID, (select DEPT_NAME from TEMP where DEPT_ID = a.REGION_CENTER_ID) as REGION_CENTER_NAME, a.BRANCH_AREA_ID, (select DEPT_NAME from TEMP where DEPT_ID = a.BRANCH_AREA_ID) as BRANCH_AREA_NAME, a.BRANCH_NBR, (select DEPT_NAME from TEMP where DEPT_ID = a.BRANCH_NBR) as BRANCH_NAME, a.EMP_ID, b.EMP_NAME,r.ROLE_NAME,a.RES_FLAG,a.LASTUPDATE,a.CREATETIME,a.IVG_RESULT_SEQ,c.FIELD_TYPE,c.FIELD_RANK,c.FIELD_LABEL,c.FIELD_VALUE ");
		sql.append("from ( select con1.* from TBCAM_IVG_PLAN_CONTENT con1 where con1.IVG_PLAN_SEQ = :seq ");
		if (headmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append(" AND (con1.REGION_CENTER_ID IN (:rcIdList) OR con1.REGION_CENTER_ID IS NULL) ");
			sql.append(" AND (con1.BRANCH_AREA_ID IN (:opIdList) OR con1.BRANCH_AREA_ID IS NULL) ");
			sql.append(" AND (con1.BRANCH_NBR IN (:brNbrList) OR con1.BRANCH_NBR IS NULL) ");
		}
		else if (armgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append(" AND (con1.REGION_CENTER_ID IN (:rcIdList)) ");
			sql.append(" AND (con1.BRANCH_AREA_ID IN (:opIdList) OR con1.BRANCH_AREA_ID IS NULL) ");
			sql.append(" AND (con1.BRANCH_NBR IN (:brNbrList) OR con1.BRANCH_NBR IS NULL) ");
		}
		else if (mbrmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append(" AND (con1.REGION_CENTER_ID IN (:rcIdList)) ");
			sql.append(" AND (con1.BRANCH_AREA_ID IN (:opIdList)) ");
			sql.append(" AND (con1.BRANCH_NBR IN (:brNbrList) OR con1.BRANCH_NBR IS NULL) ");
		}
		else {
			sql.append(" AND (con1.REGION_CENTER_ID IN (:rcIdList)) ");
			sql.append(" AND (con1.BRANCH_AREA_ID IN (:opIdList)) ");
			sql.append(" AND (con1.BRANCH_NBR IN (:brNbrList)) ");
		}
		sql.append(") a ");
		sql.append("left join TBORG_MEMBER b on a.EMP_ID = b.EMP_ID ");
		sql.append("left join TBORG_ROLE r on a.ROLE_ID = r.ROLE_ID ");
		sql.append("left join TBCAM_IVG_PLAN_FIELD c on a.IVG_RESULT_SEQ = c.IVG_RESULT_SEQ ");
		sql.append("order by a.IVG_RESULT_SEQ,c.FIELD_RANK ");
		queryCondition.setObject("seq", inputVO.getSeq());
		queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.executeQuery(queryCondition);
		// resize
		List<Map<String, Object>> ans = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : list) {
			Map<String, Object> obj = new HashMap<String, Object>();
			boolean exist = false;
			for (int k=0;k < ans.size();k++) {
				if(ObjectUtils.toString(ans.get(k).get("IVG_RESULT_SEQ")).equals(ObjectUtils.toString(map.get("IVG_RESULT_SEQ")))) {
					exist = true;
					obj = ans.get(k); 
					break;
				}
			}
			if (!exist) {
				obj.put("REGION_CENTER_ID", map.get("REGION_CENTER_ID"));
				obj.put("REGION_CENTER_NAME", map.get("REGION_CENTER_NAME"));
				obj.put("BRANCH_AREA_ID", map.get("BRANCH_AREA_ID"));
				obj.put("BRANCH_AREA_NAME", map.get("BRANCH_AREA_NAME"));
				obj.put("BRANCH_NBR", map.get("BRANCH_NBR"));
				obj.put("BRANCH_NAME", map.get("BRANCH_NAME"));
				obj.put("EMP_ID", map.get("EMP_ID"));
				obj.put("EMP_NAME", map.get("EMP_NAME"));
				obj.put("ROLE_NAME", map.get("ROLE_NAME"));
				obj.put("RES_FLAG", map.get("RES_FLAG"));
				obj.put("CREATETIME", map.get("CREATETIME"));
				obj.put("LASTUPDATE", map.get("LASTUPDATE"));
				obj.put("IVG_RESULT_SEQ", map.get("IVG_RESULT_SEQ"));
				List<Map<String, Object>> subItem = new ArrayList<Map<String, Object>>();
				Map<String, Object> obj2 = new HashMap<String, Object>();
				obj2.put("FIELD_TYPE", map.get("FIELD_TYPE"));
				obj2.put("FIELD_LABEL", map.get("FIELD_LABEL"));
				obj2.put("FIELD_VALUE", map.get("FIELD_VALUE"));
				subItem.add(obj2);
				obj.put("SUBITEM", subItem);
				ans.add(obj);
			}
			else {
				List<Map<String, Object>> subItem = (List<Map<String, Object>>) obj.get("SUBITEM");
				Map<String, Object> obj2 = new HashMap<String, Object>();
				obj2.put("FIELD_TYPE", map.get("FIELD_TYPE"));
				obj2.put("FIELD_LABEL", map.get("FIELD_LABEL"));
				obj2.put("FIELD_VALUE", map.get("FIELD_VALUE"));
				subItem.add(obj2);
				obj.put("SUBITEM", subItem);
			}
		}
		return_VO.setResultList(ans);
		//
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("select DISTINCT b.FIELD_LABEL,b.FIELD_RANK ");
		sql.append("from ( select * from TBCAM_IVG_PLAN_CONTENT where IVG_PLAN_SEQ = :seq ) a ");
		sql.append("left join TBCAM_IVG_PLAN_FIELD b on a.IVG_RESULT_SEQ = b.IVG_RESULT_SEQ ");
		sql.append("order by b.FIELD_RANK ");
		queryCondition.setObject("seq", inputVO.getSeq());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		return_VO.setResultList2(list2);
		
		this.sendRtnObject(return_VO);
	}
	
	public void download(Object body, IPrimitiveMap header) throws Exception {
		try {
			CUS150InputVO inputVO = (CUS150InputVO) body;
			dam = this.getDataAccessManager();
			
			String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			String fileName = inputVO.getFileName();
			String uuid = UUID.randomUUID().toString();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT DOC_FILE FROM TBSYS_FILE_DETAIL where DOC_ID = :id ");
			queryCondition.setObject("id", inputVO.getFileID());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			Blob blob = (Blob) list.get(0).get("DOC_FILE");
			int blobLength = (int) blob.length();  
			byte[] blobAsBytes = blob.getBytes(1, blobLength);
			
			File targetFile = new File(filePath, uuid);
			FileOutputStream fos = new FileOutputStream(targetFile);
		    fos.write(blobAsBytes);
		    fos.close();
		    notifyClientToDownloadFile("temp//"+uuid, fileName);
		    this.sendRtnObject(null);
		} catch(Exception e){
			logger.debug(e.getMessage(),e);
		}
	}
	
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		CUS150InputVO inputVO = (CUS150InputVO) body;
		XmlInfo xmlInfo = new XmlInfo();
		
		//Csv
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
		String fileName = "回報結果_"+ sdf.format(new Date()) +".csv";
		List listCSV = new ArrayList();
		for (Map<String, Object> map : inputVO.getListBase()) {
			//MAX 30
			String[] records = new String[30];
			int i = 0;
			records[i] = xmlInfo.getVariable("COMMON.YES_NO", checkIsNull(map, "RES_FLAG"), FormatHelper.FORMAT_3); // 是否已回報
			records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 區域中心
			records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區
			records[++i] = StringUtils.isNotBlank(ObjectUtils.toString(map.get("BRANCH_NBR"))) ? map.get("BRANCH_NBR") + "-" + map.get("BRANCH_NAME") : ""; // 分行
			records[++i] = checkIsNull(map, "EMP_ID") + "-" + checkIsNull(map, "EMP_NAME"); // 回報人員
			records[++i] = checkIsNull(map, "ROLE_NAME"); // 角色
			records[++i] = "=\"" + checkIsNull(map, "CREATETIME") + "\""; // 最後回報日期
			records[++i] = "=\"" + checkIsNull(map, "LASTUPDATE") + "\""; // 最後修改日期
			//動態
			List<Map<String, Object>> subItem = (List<Map<String, Object>>) map.get("SUBITEM");
			for (Map<String, Object> map2 : subItem) {
				if("4".equals(map2.get("FIELD_TYPE")) && map2.get("FIELD_VALUE") != null)
					records[++i] = "=\"" + sdf2.format(new Date((long)Float.parseFloat(map2.get("FIELD_VALUE").toString()))) + "\"";
				else
					records[++i] = checkIsNull(map2, "FIELD_VALUE");
			}
			listCSV.add(records);
		}
		//header
		String[] csvHeader = new String[30];
		int j = 0;
		csvHeader[j] = "是否已回報";
		csvHeader[++j] = "區域中心";
		csvHeader[++j] = "營運區";
		csvHeader[++j] = "分行";
		csvHeader[++j] = "回報人員";
		csvHeader[++j] = "角色";
		csvHeader[++j] = "最後回報日期";
		csvHeader[++j] = "最後修改日期";
		// 動態
		for (Map<String, Object> map : inputVO.getCustList()) {
			csvHeader[++j] = ObjectUtils.toString(map.get("FIELD_LABEL"));
		}
		
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		//Download
		notifyClientToDownloadFile(url, fileName);
		this.sendRtnObject(null);
	}
	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	
}