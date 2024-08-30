package com.systex.jbranch.app.server.fps.prd210;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPRD_INSINFOVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_INSINFO_REVIEWVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_INS_LINKING_PRDVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_INS_LINKING_PRD_REVIEWVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_RANKVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_RANK_INS_TARGETPK;
import com.systex.jbranch.app.common.fps.table.TBPRD_RANK_INS_TARGETVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("prd210")
@Scope("request")
public class PRD210 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD210.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		PRD210InputVO inputVO = (PRD210InputVO) body;
		PRD210OutputVO return_VO = new PRD210OutputVO();
		dam = this.getDataAccessManager();
		
		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PRD210' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		// 2018/1/17 SA:保險只需要這些
		// TBPRD_INS 有多個險種代號 這裡只需一個沒差DISTINCT
		sql.append("SELECT DISTINCT SEQ, PRD_ID, INSPRD_NAME, INS_TYPE, IS_RETIRED, IS_EDUCATION, IS_PURPOSE, IS_LIFE_INS, IS_ACCIDENT, IS_MEDICAL, IS_DISEASES, IS_INV, BASE_AMT_OF_PURCHASE, ACT_TYPE, REVIEW_STATUS, CREATOR FROM ( ");
		// 覆核人員
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0) {
			sql.append("SELECT rw.SEQ, rw.PRD_ID, ins.INSPRD_NAME, rw.INS_TYPE, rw.IS_RETIRED, rw.IS_EDUCATION, rw.IS_PURPOSE, rw.IS_LIFE_INS, rw.IS_ACCIDENT, rw.IS_MEDICAL, rw.IS_DISEASES, rw.IS_INV, rw.BASE_AMT_OF_PURCHASE, rw.ACT_TYPE, rw.REVIEW_STATUS, rw.CREATOR ");
			sql.append("FROM TBPRD_INSINFO_REVIEW rw left join TBPRD_INS ins on rw.PRD_ID = ins.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			if(!StringUtils.equals("HOME", inputVO.getPassParams())) {
				sql.append("UNION ");
				sql.append("SELECT null as SEQ, info.PRD_ID, ins.INSPRD_NAME, info.INS_TYPE, info.IS_RETIRED, info.IS_EDUCATION, info.IS_PURPOSE, info.IS_LIFE_INS, info.IS_ACCIDENT, info.IS_MEDICAL, info.IS_DISEASES, info.IS_INV, info.BASE_AMT_OF_PURCHASE, rw.ACT_TYPE, rw.REVIEW_STATUS, null as CREATOR ");
				sql.append("FROM TBPRD_INSINFO info left join TBPRD_INS ins on info.PRD_ID = ins.PRD_ID ");
				sql.append("left join TBPRD_INSINFO_REVIEW rw on rw.PRD_ID = info.PRD_ID and rw.REVIEW_STATUS = 'W' ");
				sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
				sql.append("AND info.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_INSINFO_REVIEW WHERE REVIEW_STATUS = 'W') ");
			}
		}
		else {
			sql.append("SELECT rw.SEQ, rw.PRD_ID, ins.INSPRD_NAME, rw.INS_TYPE, rw.IS_RETIRED, rw.IS_EDUCATION, rw.IS_PURPOSE, rw.IS_LIFE_INS, rw.IS_ACCIDENT, rw.IS_MEDICAL, rw.IS_DISEASES, rw.IS_INV, rw.BASE_AMT_OF_PURCHASE, rw.ACT_TYPE, rw.REVIEW_STATUS, null as CREATOR ");
			sql.append("FROM TBPRD_INSINFO_REVIEW rw left join TBPRD_INS ins on rw.PRD_ID = ins.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("AND rw.CREATOR = :creator ");
			sql.append("UNION ");
			sql.append("SELECT null as SEQ, info.PRD_ID, ins.INSPRD_NAME, info.INS_TYPE, info.IS_RETIRED, info.IS_EDUCATION, info.IS_PURPOSE, info.IS_LIFE_INS, info.IS_ACCIDENT, info.IS_MEDICAL, info.IS_DISEASES, info.IS_INV, info.BASE_AMT_OF_PURCHASE, rw.ACT_TYPE, rw.REVIEW_STATUS, null as CREATOR ");
			sql.append("FROM TBPRD_INSINFO info left join TBPRD_INS ins on info.PRD_ID = ins.PRD_ID ");
			sql.append("left join TBPRD_INSINFO_REVIEW rw on rw.PRD_ID = info.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND info.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_INSINFO_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
			queryCondition.setObject("creator", ws.getUser().getUserID());
		}
		sql.append(") WHERE 1=1 ");
		if(StringUtils.isNotBlank(inputVO.getPrd_id())) {
			sql.append("AND PRD_ID like :id ");
			queryCondition.setObject("id", "%" + inputVO.getPrd_id() + "%");
		}
		if(StringUtils.isNotBlank(inputVO.getIns_type())) {
			sql.append("AND INS_TYPE = :ins_type ");
			queryCondition.setObject("ins_type", inputVO.getIns_type());
		}
		sql.append("ORDER BY CASE WHEN REVIEW_STATUS = 'W' THEN 0 WHEN REVIEW_STATUS = 'Y' THEN 1 ELSE 2 END ASC,PRD_ID ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void download(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		PRD210InputVO inputVO = (PRD210InputVO) body;
		PRD210OutputVO return_VO = new PRD210OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		dam = this.getDataAccessManager();
		
		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PRD210' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT DISTINCT PRD_ID, INSPRD_NAME, INS_TYPE, IS_RETIRED, IS_EDUCATION, IS_PURPOSE, IS_LIFE_INS, IS_ACCIDENT, IS_MEDICAL, IS_DISEASES, IS_INV, BASE_AMT_OF_PURCHASE, ACT_TYPE, REVIEW_STATUS, CREATOR FROM ( ");
		// 覆核人員
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0) {
			sql.append("SELECT rw.PRD_ID, ins.INSPRD_NAME, rw.INS_TYPE, rw.IS_RETIRED, rw.IS_EDUCATION, rw.IS_PURPOSE, rw.IS_LIFE_INS, rw.IS_ACCIDENT, rw.IS_MEDICAL, rw.IS_DISEASES, rw.IS_INV, rw.BASE_AMT_OF_PURCHASE, rw.ACT_TYPE, rw.REVIEW_STATUS, rw.CREATOR ");
			sql.append("FROM TBPRD_INSINFO_REVIEW rw left join TBPRD_INS ins on rw.PRD_ID = ins.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("UNION ");
			sql.append("SELECT info.PRD_ID, ins.INSPRD_NAME, info.INS_TYPE, info.IS_RETIRED, info.IS_EDUCATION, info.IS_PURPOSE, info.IS_LIFE_INS, info.IS_ACCIDENT, info.IS_MEDICAL, info.IS_DISEASES, info.IS_INV, info.BASE_AMT_OF_PURCHASE, rw.ACT_TYPE, rw.REVIEW_STATUS, null as CREATOR ");
			sql.append("FROM TBPRD_INSINFO info left join TBPRD_INS ins on info.PRD_ID = ins.PRD_ID ");
			sql.append("left join TBPRD_INSINFO_REVIEW rw on rw.PRD_ID = info.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND info.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_INSINFO_REVIEW WHERE REVIEW_STATUS = 'W') ");	
		}
		else {
			sql.append("SELECT rw.PRD_ID, ins.INSPRD_NAME, rw.INS_TYPE, rw.IS_RETIRED, rw.IS_EDUCATION, rw.IS_PURPOSE, rw.IS_LIFE_INS, rw.IS_ACCIDENT, rw.IS_MEDICAL, rw.IS_DISEASES, rw.IS_INV, rw.BASE_AMT_OF_PURCHASE, rw.ACT_TYPE, rw.REVIEW_STATUS, null as CREATOR ");
			sql.append("FROM TBPRD_INSINFO_REVIEW rw left join TBPRD_INS ins on rw.PRD_ID = ins.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("AND rw.CREATOR = :creator ");
			sql.append("UNION ");
			sql.append("SELECT info.PRD_ID, ins.INSPRD_NAME, info.INS_TYPE, info.IS_RETIRED, info.IS_EDUCATION, info.IS_PURPOSE, info.IS_LIFE_INS, info.IS_ACCIDENT, info.IS_MEDICAL, info.IS_DISEASES, info.IS_INV, info.BASE_AMT_OF_PURCHASE, rw.ACT_TYPE, rw.REVIEW_STATUS, null as CREATOR ");
			sql.append("FROM TBPRD_INSINFO info left join TBPRD_INS ins on info.PRD_ID = ins.PRD_ID ");
			sql.append("left join TBPRD_INSINFO_REVIEW rw on rw.PRD_ID = info.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND info.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_INSINFO_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
			queryCondition.setObject("creator", ws.getUser().getUserID());
		}
		sql.append(") WHERE 1=1 ");
		if (!StringUtils.isBlank(inputVO.getPrd_id())) {
			sql.append("AND PRD_ID like :id ");
			queryCondition.setObject("id", "%" + inputVO.getPrd_id() + "%");
		}
		sql.append("ORDER BY CASE WHEN REVIEW_STATUS = 'W' THEN 0 WHEN REVIEW_STATUS = 'Y' THEN 1 ELSE 2 END ASC,PRD_ID ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(list.size() > 0) {
			// gen csv
			Map<String, String> ins_type = xmlInfo.doGetVariable("PRD.INS_TYPE", FormatHelper.FORMAT_3);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String fileName = "保險清單_"+ sdf.format(new Date()) + "_" + ws.getUser().getUserID() + ".csv";
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				// 13 column
				String[] records = new String[13];
				int i = 0;
				if ("W".equals(ObjectUtils.toString(map.get("REVIEW_STATUS"))))
					records[i] = "覆核中";
				else
					records[i] = "已覆核";
				records[++i] = checkIsNull(map, "PRD_ID");
				records[++i] = checkIsNull(map, "INSPRD_NAME");
				records[++i] = ins_type.get(checkIsNull(map, "INS_TYPE"));
				records[++i] = checkIsNull(map, "IS_RETIRED");
				records[++i] = checkIsNull(map, "IS_EDUCATION");
				records[++i] = checkIsNull(map, "IS_PURPOSE");
				records[++i] = checkIsNull(map, "IS_LIFE_INS");
				records[++i] = checkIsNull(map, "IS_ACCIDENT");
				records[++i] = checkIsNull(map, "IS_MEDICAL");
				records[++i] = checkIsNull(map, "IS_DISEASES");
				records[++i] = checkIsNull(map, "IS_INV");
				records[++i] = checkIsNull(map, "BASE_AMT_OF_PURCHASE");
				listCSV.add(records);
			}
			// header
			String[] csvHeader = new String[13];
			int j = 0;
			csvHeader[j] = "覆核狀態";
			csvHeader[++j] = "保險代碼";
			csvHeader[++j] = "保險名稱";
			csvHeader[++j] = "險種類別";
			csvHeader[++j] = "適合退休規劃";
			csvHeader[++j] = "適合子女教育規劃";
			csvHeader[++j] = "適合特定目的規劃";
			csvHeader[++j] = "適合壽險規劃";
			csvHeader[++j] = "適合意外規劃";
			csvHeader[++j] = "適合醫療規劃";
			csvHeader[++j] = "適合重大疾病規劃";
			csvHeader[++j] = "是否類全委";
			csvHeader[++j] = "最低申購金額";
						
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
		if(StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	public void checkID(Object body, IPrimitiveMap header) throws JBranchException {
		PRD210InputVO inputVO = (PRD210InputVO) body;
		PRD210OutputVO return_VO = new PRD210OutputVO();
		dam = this.getDataAccessManager();
		
		// update
		if(StringUtils.equals("Y", inputVO.getStatus())) {
			// TBPRD_INS
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT PRD_ID, INSPRD_NAME FROM TBPRD_INS where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				return_VO.setIns_name(ObjectUtils.toString(list.get(0).get("INSPRD_NAME")));
				return_VO.setCanEdit(true);
			}
			else {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_026");
			}
			
			// TBPRD_INSINFO_REVIEW
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT PRD_ID FROM TBPRD_INSINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			if (list2.size() > 0) {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_028");
			}
		}
		// sort
		else if(StringUtils.equals("S", inputVO.getStatus())) {
			// TBPRD_INS 有限制可申購
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT a.PRD_ID, a.INSPRD_NAME, a.CURR_CD FROM TBPRD_INS a ");
			sql.append("LEFT JOIN TBPRD_INSINFO b on a.PRD_ID = b.PRD_ID ");
			sql.append("where a.PRD_ID = :id and sysdate between a.SALE_SDATE and NVL(a.SALE_EDATE , sysdate) and a.INS_TYPE = :ins_type ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setObject("ins_type", inputVO.getIns_type());
			// mantis 4587 參數維護、新增商品、主推商品維護、特定/非特定畫面(含再投資)等，投資型保險皆僅包含類全委投資型保險商品
			if("2".equals(inputVO.getIns_type()))
				sql.append("AND b.IS_INV = 'Y' ");
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				return_VO.setIns_name(ObjectUtils.toString(list.get(0).get("INSPRD_NAME")));
				return_VO.setCurrency(ObjectUtils.toString(list.get(0).get("CURR_CD")));
				return_VO.setCanEdit(true);
			}
			else
				return_VO.setCanEdit(false);
		}
		// add
		else {
			// TBPRD_INS
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT PRD_ID, INSPRD_NAME FROM TBPRD_INS where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				return_VO.setIns_name(ObjectUtils.toString(list.get(0).get("INSPRD_NAME")));
				return_VO.setCanEdit(true);
			}
			else {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_026");
			}
			// TBPRD_INSINFO
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT PRD_ID FROM TBPRD_INSINFO where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			if (list2.size() > 0) {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_027");
			}
			// TBPRD_INSINFO_REVIEW
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT PRD_ID FROM TBPRD_INSINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
			if (list3.size() > 0) {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_028");
			}
		}
		
		this.sendRtnObject(return_VO);
	}
	
	public void addData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD210InputVO inputVO = (PRD210InputVO) body;
		dam = this.getDataAccessManager();
		
		// check again
		// TBPRD_INS
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID, INSPRD_NAME FROM TBPRD_INS where PRD_ID = :id ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() == 0)
			throw new APException("ehl_01_common_026");		//商品主檔無此商品 {0}
		// TBPRD_INSINFO
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_INSINFO where PRD_ID = :id ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		if (list2.size() > 0)
			throw new APException("ehl_01_common_027");		//商品資訊檔已有此商品 {0}
		// TBPRD_INSINFO_REVIEW
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_INSINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
		if (list3.size() > 0)
			throw new APException("ehl_01_common_028");		//該商品已待覆核 {0}
		
		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_INSINFO_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// add TBPRD_INSINFO_REVIEW
		TBPRD_INSINFO_REVIEWVO vo = new TBPRD_INSINFO_REVIEWVO();
		vo.setSEQ(seqNo);
		vo.setPRD_ID(inputVO.getPrd_id());
		vo.setINS_TYPE(inputVO.getIns_type());
		vo.setIS_RETIRED(inputVO.getRetired());
		vo.setIS_EDUCATION(inputVO.getEducation());
		vo.setIS_PURPOSE(inputVO.getPurpose());
		vo.setIS_LIFE_INS(inputVO.getLife());
		vo.setIS_ACCIDENT(inputVO.getAccident());
		vo.setIS_MEDICAL(inputVO.getMedical());
		vo.setIS_DISEASES(inputVO.getDiseases());
		vo.setIS_INV(inputVO.getIs_inv());
		vo.setBASE_AMT_OF_PURCHASE(inputVO.getBase_amt_of_purchase());
		vo.setACT_TYPE("A");
		vo.setREVIEW_STATUS("W");
		dam.create(vo);
		this.sendRtnObject(null);
	}
	
	public void review(Object body, IPrimitiveMap header) throws JBranchException {
		PRD210InputVO inputVO = (PRD210InputVO) body;
		dam = this.getDataAccessManager();
		
		// 2017/2/21
		for(Map<String, Object> rmap : inputVO.getReview_list()) {
			TBPRD_INSINFO_REVIEWVO rvo = new TBPRD_INSINFO_REVIEWVO();
			rvo = (TBPRD_INSINFO_REVIEWVO) dam.findByPKey(TBPRD_INSINFO_REVIEWVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(rmap.get("SEQ"))));
			if (rvo != null) {
				// confirm
				if("Y".equals(inputVO.getStatus())) {
					// 新增
					if("A".equals(rvo.getACT_TYPE())) {
						// TBPRD_INSINFO
						TBPRD_INSINFOVO vo = new TBPRD_INSINFOVO();
						vo.setPRD_ID(rvo.getPRD_ID());
						vo.setINS_TYPE(rvo.getINS_TYPE());
						vo.setIS_RETIRED(rvo.getIS_RETIRED());
						vo.setIS_EDUCATION(rvo.getIS_EDUCATION());
						vo.setIS_PURPOSE(rvo.getIS_PURPOSE());
						vo.setIS_LIFE_INS(rvo.getIS_LIFE_INS());
						vo.setIS_ACCIDENT(rvo.getIS_ACCIDENT());
						vo.setIS_MEDICAL(rvo.getIS_MEDICAL());
						vo.setIS_DISEASES(rvo.getIS_DISEASES());
						vo.setIS_INV(rvo.getIS_INV());
						vo.setBASE_AMT_OF_PURCHASE(rvo.getBASE_AMT_OF_PURCHASE());
						vo.setACT_TYPE("A");
						vo.setREVIEW_STATUS("Y");
						dam.create(vo);
					}
					// 修改
					else if("M".equals(rvo.getACT_TYPE())) {
						TBPRD_INSINFOVO vo = new TBPRD_INSINFOVO();
						vo = (TBPRD_INSINFOVO) dam.findByPKey(TBPRD_INSINFOVO.TABLE_UID, rvo.getPRD_ID());
						if (vo != null) {
							vo.setINS_TYPE(rvo.getINS_TYPE());
							vo.setIS_RETIRED(rvo.getIS_RETIRED());
							vo.setIS_EDUCATION(rvo.getIS_EDUCATION());
							vo.setIS_PURPOSE(rvo.getIS_PURPOSE());
							vo.setIS_LIFE_INS(rvo.getIS_LIFE_INS());
							vo.setIS_ACCIDENT(rvo.getIS_ACCIDENT());
							vo.setIS_MEDICAL(rvo.getIS_MEDICAL());
							vo.setIS_DISEASES(rvo.getIS_DISEASES());
							vo.setIS_INV(rvo.getIS_INV());
							vo.setBASE_AMT_OF_PURCHASE(rvo.getBASE_AMT_OF_PURCHASE());
							vo.setACT_TYPE("M");
							vo.setREVIEW_STATUS("Y");
							dam.update(vo);
						} else {
							// 顯示資料不存在
							throw new APException("ehl_01_common_001");
						}
					}
					// 刪除
					else if("D".equals(rvo.getACT_TYPE())) {
						TBPRD_INSINFOVO vo = new TBPRD_INSINFOVO();
						vo = (TBPRD_INSINFOVO) dam.findByPKey(TBPRD_INSINFOVO.TABLE_UID, rvo.getPRD_ID());
						if (vo != null) {
							dam.delete(vo);
						} else {
							// 顯示資料不存在
							throw new APException("ehl_01_common_001");
						}
					}
					// mantis 4556 tbprd_insinfo覆核後，請更新到tbprd_ins.INS_TYPE
					if("A".equals(rvo.getACT_TYPE()) || "M".equals(rvo.getACT_TYPE())) {
						QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						queryCondition.setQueryString("UPDATE TBPRD_INS SET INS_TYPE = :ins_type WHERE PRD_ID = :prd_id");
						queryCondition.setObject("ins_type", rvo.getINS_TYPE());
						queryCondition.setObject("prd_id", rvo.getPRD_ID());
						dam.exeUpdate(queryCondition);
					}
				}
				rvo.setREVIEW_STATUS(inputVO.getStatus());
				dam.update(rvo);
			} else
				throw new APException("ehl_01_common_001");
		}
		
		this.sendRtnObject(null);
	}
	
	public void editData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD210InputVO inputVO = (PRD210InputVO) body;
		dam = this.getDataAccessManager();
		
		// check again
		// TBPRD_INSINFO_REVIEW
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_INSINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			throw new APException("ehl_01_common_028");
		
		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_INSINFO_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// add TBPRD_INSINFO_REVIEW
		TBPRD_INSINFO_REVIEWVO vo = new TBPRD_INSINFO_REVIEWVO();
		vo.setSEQ(seqNo);
		vo.setPRD_ID(inputVO.getPrd_id());
		vo.setINS_TYPE(inputVO.getIns_type());
		vo.setIS_RETIRED(inputVO.getRetired());
		vo.setIS_EDUCATION(inputVO.getEducation());
		vo.setIS_PURPOSE(inputVO.getPurpose());
		vo.setIS_LIFE_INS(inputVO.getLife());
		vo.setIS_ACCIDENT(inputVO.getAccident());
		vo.setIS_MEDICAL(inputVO.getMedical());
		vo.setIS_DISEASES(inputVO.getDiseases());
		vo.setIS_INV(inputVO.getIs_inv());
		vo.setBASE_AMT_OF_PURCHASE(inputVO.getBase_amt_of_purchase());
		vo.setACT_TYPE("M");
		vo.setREVIEW_STATUS("W");
		dam.create(vo);
		this.sendRtnObject(null);
	}
	
	public void deleteData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD210InputVO inputVO = (PRD210InputVO) body;
		dam = this.getDataAccessManager();
		
		// check again
		// TBPRD_INSINFO_REVIEW
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_INSINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			throw new APException("ehl_01_common_028");
		
		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_INSINFO_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// get ori
		TBPRD_INSINFOVO vo = new TBPRD_INSINFOVO();
		vo = (TBPRD_INSINFOVO) dam.findByPKey(TBPRD_INSINFOVO.TABLE_UID, inputVO.getPrd_id());
		if (vo != null) {
			// add TBPRD_INSINFO_REVIEW
			TBPRD_INSINFO_REVIEWVO rvo = new TBPRD_INSINFO_REVIEWVO();
			rvo.setSEQ(seqNo);
			rvo.setPRD_ID(vo.getPRD_ID());
			rvo.setINS_TYPE(vo.getINS_TYPE());
			rvo.setIS_RETIRED(vo.getIS_RETIRED());
			rvo.setIS_EDUCATION(vo.getIS_EDUCATION());
			rvo.setIS_PURPOSE(vo.getIS_PURPOSE());
			rvo.setIS_LIFE_INS(vo.getIS_LIFE_INS());
			rvo.setIS_ACCIDENT(vo.getIS_ACCIDENT());
			rvo.setIS_MEDICAL(vo.getIS_MEDICAL());
			rvo.setIS_DISEASES(vo.getIS_DISEASES());
			rvo.setIS_INV(vo.getIS_INV());
			rvo.setBASE_AMT_OF_PURCHASE(vo.getBASE_AMT_OF_PURCHASE());
			rvo.setACT_TYPE("D");
			rvo.setREVIEW_STATUS("W");
			dam.create(rvo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		this.sendRtnObject(null);
	}
	
	public void upload(Object body, IPrimitiveMap header) throws JBranchException {
		PRD210InputVO inputVO = (PRD210InputVO) body;
		PRD210OutputVO return_VO = new PRD210OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		dam = this.getDataAccessManager();
		
		List<String> error = new ArrayList<String>();
		List<String> error2 = new ArrayList<String>();
		List<String> error3 = new ArrayList<String>();
		List<String> error4 = new ArrayList<String>();
		Set<String> idList = new HashSet<String>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if(!dataCsv.isEmpty()) {
			Map<String, String> ins_type = xmlInfo.doGetVariable("PRD.INS_TYPE", FormatHelper.FORMAT_3);
			Map<String, String> conYN = xmlInfo.doGetVariable("COMMON.YES_NO", FormatHelper.FORMAT_3);
			for(int i = 0;i < dataCsv.size();i++) {
				String[] str = dataCsv.get(i);
				if(i == 0) {
					try {
						if(!"商品代碼".equals(str[0].trim()))
							throw new Exception(str[0]);
						else if(!"險種類別".equals(str[1].substring(0, 4)))
							throw new Exception(str[1]);
						else if(!"適合退休規劃".equals(str[2].substring(0, 6)))
							throw new Exception(str[2]);
						else if(!"適合子女教育規劃".equals(str[3].substring(0, 8)))
							throw new Exception(str[3]);
						else if(!"適合特定目的規劃".equals(str[4].substring(0, 8)))
							throw new Exception(str[4]);
						else if(!"適合壽險規劃".equals(str[5].substring(0, 6)))
							throw new Exception(str[5]);
						else if(!"適合意外規劃".equals(str[6].substring(0, 6)))
							throw new Exception(str[6]);
						else if(!"適合醫療規劃".equals(str[7].substring(0, 6)))
							throw new Exception(str[7]);
						else if(!"適合重大疾病規劃".equals(str[8].substring(0, 8)))
							throw new Exception(str[8]);
						else if(!"是否類全委".equals(str[9].substring(0, 5)))
							throw new Exception(str[9]);
						else if(!"最低申購金額".equals(str[10].substring(0, 6)))
							throw new Exception(str[10]);
					} catch(Exception ex) {
						throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
					}
					continue;
				}
				// check prd_id 請PG判斷，第一欄若為空，就跳過去不判斷。
				if(StringUtils.isBlank(str[0]))
					continue;
				// 使用者很愛重覆上傳
				if(idList.contains(str[0].trim())) {
					error4.add(str[0]);
					continue;
				}
				idList.add(str[0].trim());
				// TBPRD_INS
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT PRD_ID FROM TBPRD_INS where PRD_ID = :id ");
				queryCondition.setObject("id", str[0].trim());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if (list.size() == 0) {
					error.add(str[0]);
					continue;
				}
				// TBPRD_INSINFO check edit
				Boolean exist = false;
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT PRD_ID FROM TBPRD_INSINFO where PRD_ID = :id ");
				queryCondition.setObject("id", str[0].trim());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				if (list2.size() > 0)
					exist = true;
				// TBPRD_INSINFO_REVIEW
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT PRD_ID FROM TBPRD_INSINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
				queryCondition.setObject("id", str[0].trim());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
				if (list3.size() > 0) {
					error2.add(str[0]);
					continue;
				}
				
				// seq
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT SQ_TBPRD_INSINFO_REVIEW.nextval AS SEQ FROM DUAL ");
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
				BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
				// add TBPRD_INSINFO_REVIEW
				TBPRD_INSINFO_REVIEWVO vo = new TBPRD_INSINFO_REVIEWVO();
				vo.setSEQ(seqNo);
				//
				if(utf_8_length(str[0]) > 16) {
					error3.add(str[0]);
					continue;
				}
				else
					vo.setPRD_ID(str[0].trim());
				//
				if(StringUtils.isNotBlank(str[1])) {
					if(StringUtils.isBlank(ins_type.get(str[1]))) {
						error3.add(str[0]+":"+str[1]);
						continue;
					}
					vo.setINS_TYPE(str[1]);
				}
				else
					error3.add(str[0]+":"+str[1]);
				//
				if(StringUtils.isNotBlank(str[2])) {
					if(StringUtils.isBlank(conYN.get(str[2]))) {
						error3.add(str[0]+":"+str[2]);
						continue;
					}
					vo.setIS_RETIRED(str[2]);
				}
				else
					vo.setIS_RETIRED(null);
				//
				if(StringUtils.isNotBlank(str[3])) {
					if(StringUtils.isBlank(conYN.get(str[3]))) {
						error3.add(str[0]+":"+str[3]);
						continue;
					}
					vo.setIS_EDUCATION(str[3]);
				}
				else
					vo.setIS_EDUCATION(null);
				//
				if(StringUtils.isNotBlank(str[4])) {
					if(StringUtils.isBlank(conYN.get(str[4]))) {
						error3.add(str[0]+":"+str[4]);
						continue;
					}
					vo.setIS_PURPOSE(str[4]);
				}
				else
					vo.setIS_PURPOSE(null);
				//
				if(StringUtils.isNotBlank(str[5])) {
					if(StringUtils.isBlank(conYN.get(str[5]))) {
						error3.add(str[0]+":"+str[5]);
						continue;
					}
					vo.setIS_LIFE_INS(str[5]);
				}
				else
					vo.setIS_LIFE_INS(null);
				//
				if(StringUtils.isNotBlank(str[6])) {
					if(StringUtils.isBlank(conYN.get(str[6]))) {
						error3.add(str[0]+":"+str[6]);
						continue;
					}
					vo.setIS_ACCIDENT(str[6]);
				}
				else
					vo.setIS_ACCIDENT(null);
				//
				if(StringUtils.isNotBlank(str[7])) {
					if(StringUtils.isBlank(conYN.get(str[7]))) {
						error3.add(str[0]+":"+str[7]);
						continue;
					}
					vo.setIS_MEDICAL(str[7]);
				}
				else
					vo.setIS_MEDICAL(null);
				//
				if(StringUtils.isNotBlank(str[8])) {
					if(StringUtils.isBlank(conYN.get(str[8]))) {
						error3.add(str[0]+":"+str[8]);
						continue;
					}
					vo.setIS_DISEASES(str[8]);
				}
				else
					vo.setIS_DISEASES(null);
				//
				if(StringUtils.isNotBlank(str[9])) {
					if(StringUtils.isBlank(conYN.get(str[9]))) {
						error3.add(str[0]+":"+str[9]);
						continue;
					}
					vo.setIS_INV(str[9]);
				}
				else
					vo.setIS_INV(null);
				//
				if(StringUtils.isNotBlank(str[10])) {
					try {
						BigDecimal str10 = new BigDecimal(str[10]);
						vo.setBASE_AMT_OF_PURCHASE(str10);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[10]);
						continue;
					}
				}
				else
					vo.setBASE_AMT_OF_PURCHASE(null);
				//
				if(!exist)
					vo.setACT_TYPE("A");
				else
					vo.setACT_TYPE("M");
				vo.setREVIEW_STATUS("W");
				dam.create(vo);
			}
		}
		return_VO.setErrorList(error);
		return_VO.setErrorList2(error2);
		return_VO.setErrorList3(error3);
		return_VO.setErrorList4(error4);
		this.sendRtnObject(return_VO);
	}
	
	public void downloadSimple(Object body, IPrimitiveMap header) throws Exception {
		this.notifyClientToDownloadFile("doc//PRD//PRD210_EXAMPLE.csv", "上傳指定保險代碼範例.csv");
	}
	
	public void downloadTargetSimple(Object body, IPrimitiveMap header) throws Exception {
		this.notifyClientToDownloadFile("doc//PRD//PRD210_EXAMPLE2.csv", "上傳投資型保險標的股債配置範例.csv");
	}
	
	public void getRank(Object body, IPrimitiveMap header) throws JBranchException {
		PRD210InputVO inputVO = (PRD210InputVO) body;
		PRD210OutputVO return_VO = new PRD210OutputVO();
		dam = this.getDataAccessManager();
		
		// INS_S
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select PRD_ID, INSPRD_NAME, PRD_RANK, PRD_RANK_DATE FROM TBPRD_INS WHERE PRD_RANK IS NOT NULL AND INS_TYPE = '1' ORDER BY PRD_RANK");
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		// select
		if(inputVO.getDate() != null) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT a.PRD_ID, b.INSPRD_NAME, a.PRD_RANK FROM TBPRD_RANK a ");
			sql.append("LEFT JOIN TBPRD_INS b on a.PRD_ID = b.PRD_ID ");
			sql.append("WHERE a.PRD_TYPE = 'INS_S' ");
			sql.append("AND a.EFFECT_DATE = :eff_date ");
			queryCondition.setObject("eff_date", inputVO.getDate());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			return_VO.setResultList2(list2);
		}
		// 最近的
		else {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("select DISTINCT EFFECT_DATE FROM TBPRD_RANK WHERE PRD_TYPE = 'INS_S' AND EFFECT_DATE > trunc(sysdate) ORDER BY EFFECT_DATE");
			List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
			if(list3.size() > 0) {
				return_VO.setLastDate((Timestamp) list3.get(0).get("EFFECT_DATE"));
				
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT a.PRD_ID, b.INSPRD_NAME, a.PRD_RANK FROM TBPRD_RANK a ");
				sql.append("LEFT JOIN TBPRD_INS b on a.PRD_ID = b.PRD_ID ");
				sql.append("WHERE a.PRD_TYPE = 'INS_S' ");
				sql.append("AND a.EFFECT_DATE = :eff_date ");
				queryCondition.setObject("eff_date", list3.get(0).get("EFFECT_DATE"));
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				return_VO.setResultList2(list2);
			}
		}
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select DISTINCT EFFECT_DATE FROM TBPRD_RANK WHERE PRD_TYPE = 'INS_S' AND EFFECT_DATE > trunc(sysdate) ORDER BY EFFECT_DATE");
		List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
		return_VO.setResultList3(list3);
		
		this.sendRtnObject(return_VO);
	}
	
	public void getRank2(Object body, IPrimitiveMap header) throws JBranchException {
		PRD210InputVO inputVO = (PRD210InputVO) body;
		PRD210OutputVO return_VO = new PRD210OutputVO();
		dam = this.getDataAccessManager();
		
		// INS_I
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select PRD_ID, INSPRD_NAME, PRD_RANK, PRD_RANK_DATE FROM TBPRD_INS WHERE PRD_RANK IS NOT NULL AND INS_TYPE = '2' ORDER BY PRD_RANK");
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		// TBPRD_RANK_INS_TARGET
		for(Map<String, Object> map : list) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("select * from TBPRD_RANK_INS_TARGET where INSPRD_ID = :ins_id and STATUS = 'A'");
			queryCondition.setObject("ins_id", map.get("PRD_ID"));
			List<Map<String, Object>> t_list = dam.exeQuery(queryCondition);
			map.put("fund_list", t_list);
		}
		return_VO.setResultList(list);
		
		// select
		if(inputVO.getDate2() != null) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT a.SEQ, a.PRD_ID, b.INSPRD_NAME, a.PRD_RANK FROM TBPRD_RANK a ");
			sql.append("LEFT JOIN TBPRD_INS b on a.PRD_ID = b.PRD_ID ");
			sql.append("WHERE a.PRD_TYPE = 'INS_I' ");
			sql.append("AND a.EFFECT_DATE = :eff_date ");
			queryCondition.setObject("eff_date", inputVO.getDate2());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			// TBPRD_RANK_INS_TARGET
			for(Map<String, Object> map2 : list2) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString("select * from TBPRD_RANK_INS_TARGET where SEQ = :seq");
				queryCondition.setObject("seq", map2.get("SEQ"));
				List<Map<String, Object>> t_list = dam.exeQuery(queryCondition);
				map2.put("fund_list", t_list);
			}
			return_VO.setResultList2(list2);
		}
		// 最近的
		else {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("select DISTINCT EFFECT_DATE FROM TBPRD_RANK WHERE PRD_TYPE = 'INS_I' ORDER BY EFFECT_DATE");
			List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
			if(list3.size() > 0) {
				return_VO.setLastDate((Timestamp) list3.get(0).get("EFFECT_DATE"));
				
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT a.SEQ, a.PRD_ID, b.INSPRD_NAME, a.PRD_RANK FROM TBPRD_RANK a ");
				sql.append("LEFT JOIN TBPRD_INS b on a.PRD_ID = b.PRD_ID ");
				sql.append("WHERE a.PRD_TYPE = 'INS_I' ");
				sql.append("AND a.EFFECT_DATE = :eff_date ");
				queryCondition.setObject("eff_date", list3.get(0).get("EFFECT_DATE"));
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				// TBPRD_RANK_INS_TARGET
				for(Map<String, Object> map2 : list2) {
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					queryCondition.setQueryString("select * from TBPRD_RANK_INS_TARGET where SEQ = :seq");
					queryCondition.setObject("seq", map2.get("SEQ"));
					List<Map<String, Object>> t_list = dam.exeQuery(queryCondition);
					map2.put("fund_list", t_list);
				}
				return_VO.setResultList2(list2);
			}
		}
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select DISTINCT EFFECT_DATE FROM TBPRD_RANK WHERE PRD_TYPE = 'INS_I' AND EFFECT_DATE > trunc(sysdate) ORDER BY EFFECT_DATE");
		List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
		return_VO.setResultList3(list3);
		
		this.sendRtnObject(return_VO);
	}
	
	public void getFund(Object body, IPrimitiveMap header) throws JBranchException {
		PRD210InputVO inputVO = (PRD210InputVO) body;
		PRD210OutputVO return_VO = new PRD210OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		queryCondition.setQueryString("select * from TBPRD_INS_LINKING where INSPRD_ID = :ins_id");
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT INS.TARGET_ID, INS.LINKED_NAME, INS.PRD_RISK, PRD.STOCK_BOND_TYPE ");
		sql.append("FROM TBPRD_INS_LINKING INS ");
		sql.append("LEFT JOIN TBPRD_INS_LINKING_PRD PRD ");
		sql.append("ON INS.TARGET_ID = PRD.TARGET_ID ");
		sql.append("WHERE INS.INSPRD_ID = :insprd_id ");
		
		if(StringUtils.isNotBlank(inputVO.getStock_bond_type())) {
			sql.append("AND PRD.STOCK_BOND_TYPE = :stock_bond_type ");			
			queryCondition.setObject("stock_bond_type", inputVO.getStock_bond_type());
		}
		
		queryCondition.setObject("insprd_id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
	
	public void saveSort(Object body, IPrimitiveMap header) throws JBranchException {
		PRD210InputVO inputVO = (PRD210InputVO) body;
		dam = this.getDataAccessManager();
		
		// del first
//		if(inputVO.getDate() != null) {
//			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//			queryCondition.setQueryString("DELETE FROM TBPRD_RANK WHERE PRD_TYPE = 'INS_S' AND EFFECT_DATE = :eff_date");
//			queryCondition.setObject("eff_date", inputVO.getDate());
//			dam.exeUpdate(queryCondition);
//		}
		if(inputVO.getDate2() != null) {
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("DELETE FROM TBPRD_RANK WHERE PRD_TYPE = 'INS_I' AND EFFECT_DATE = :eff_date");
			queryCondition.setObject("eff_date", inputVO.getDate2());
			dam.exeUpdate(queryCondition);
		}
		// then add
//		for(Map<String, Object> map : inputVO.getReview_list()) {
//			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//			queryCondition.setQueryString("SELECT SQ_TBPRD_RANK.nextval AS SEQ FROM DUAL");
//			List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
//			BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
//			
//			TBPRD_RANKVO vo = new TBPRD_RANKVO();
//			vo.setSEQ(seqNo);
//			vo.setPRD_TYPE("INS_S");
//			vo.setPRD_ID(ObjectUtils.toString(map.get("prd_id")));
//			vo.setPRD_RANK(new BigDecimal(ObjectUtils.toString(map.get("rank"))));
//			vo.setEFFECT_DATE(new Timestamp(inputVO.getDate().getTime()));
//			dam.create(vo);
//		}
		for(Map<String, Object> map : inputVO.getReview_list2()) {
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("SELECT SQ_TBPRD_RANK.nextval AS SEQ FROM DUAL");
			List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
			BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
			
			TBPRD_RANKVO vo = new TBPRD_RANKVO();
			vo.setSEQ(seqNo);
			vo.setPRD_TYPE("INS_I");
			vo.setPRD_ID(ObjectUtils.toString(map.get("prd_id")));
			vo.setPRD_RANK(new BigDecimal(ObjectUtils.toString(map.get("rank"))));
			vo.setEFFECT_DATE(new Timestamp(inputVO.getDate2().getTime()));
			dam.create(vo);
			// TBPRD_RANK_INS_TARGET
			if(map.get("fund_list") != null) {
				List<Map<String, Object>> fund_list = (List<Map<String, Object>>) map.get("fund_list");
				for(Map<String, Object> fund_map : fund_list) {
					TBPRD_RANK_INS_TARGETPK tpk = new TBPRD_RANK_INS_TARGETPK();
					TBPRD_RANK_INS_TARGETVO tvo = new TBPRD_RANK_INS_TARGETVO();
					tpk.setSEQ(seqNo);
					tpk.setKEY_NO(new BigDecimal(fund_map.get("KEY_NO").toString()));
					tvo.setcomp_id(tpk);
					tvo.setINSPRD_ID(ObjectUtils.toString(fund_map.get("INSPRD_ID")));
					tvo.setTARGET_ID(ObjectUtils.toString(fund_map.get("TARGET_ID")));
					tvo.setFUND_ID(ObjectUtils.toString(fund_map.get("FUND_ID")));
					tvo.setLIPPER_ID(ObjectUtils.toString(fund_map.get("LIPPER_ID")));
					tvo.setLINKED_NAME(ObjectUtils.toString(fund_map.get("LINKED_NAME")));
					tvo.setPRD_RISK(ObjectUtils.toString(fund_map.get("PRD_RISK")));
					tvo.setSTATUS("W");
					dam.create(tvo);
				}
			}
		}
		
		this.sendRtnObject(null);
	}
	
	private List<Map<String, Object>> getTaret (String target_id, String pass_params) throws JBranchException  {
		WorkStation ws = DataManager.getWorkStation(uuid);
		dam = this.getDataAccessManager();
		
		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PRD210' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sql.append("SELECT SEQNO, TARGET_ID, LINKED_NAME, STOCK_BOND_TYPE, ACT_TYPE, REVIEW_STATUS, CREATOR FROM ( ");
		// 覆核人員
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0) {
			sql.append("SELECT rw.SEQNO, rw.TARGET_ID, ins.LINKED_NAME, rw.STOCK_BOND_TYPE, rw.ACT_TYPE, rw.REVIEW_STATUS, rw.CREATOR ");
			sql.append("FROM TBPRD_INS_LINKING_PRD_REVIEW rw LEFT JOIN ");
			sql.append("(SELECT DISTINCT TARGET_ID, LINKED_NAME FROM TBPRD_INS_LINKING) ins ");
			sql.append("ON rw.TARGET_ID = ins.TARGET_ID WHERE rw.REVIEW_STATUS = 'W' ");
//			if(!StringUtils.equals("HOME", inputVO.getPassParams())) {
			if(!StringUtils.equals("HOME", pass_params)) {
				sql.append("UNION ");
				sql.append("SELECT null as SEQNO, info.TARGET_ID, ins.LINKED_NAME, info.STOCK_BOND_TYPE, rw.ACT_TYPE, rw.REVIEW_STATUS, null as CREATOR ");
				sql.append("FROM TBPRD_INS_LINKING_PRD info LEFT JOIN ");
				sql.append("(SELECT DISTINCT TARGET_ID, LINKED_NAME FROM TBPRD_INS_LINKING) ins ");
				sql.append("ON info.TARGET_ID = ins.TARGET_ID ");
				sql.append("LEFT JOIN TBPRD_INS_LINKING_PRD_REVIEW rw on rw.TARGET_ID = info.TARGET_ID and rw.REVIEW_STATUS = 'W' ");
				sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
				sql.append("AND info.TARGET_ID NOT IN (SELECT TARGET_ID FROM TBPRD_INS_LINKING_PRD_REVIEW WHERE REVIEW_STATUS = 'W') ");
			}
		}
		else {
			sql.append("SELECT rw.SEQNO, rw.TARGET_ID, ins.LINKED_NAME, rw.STOCK_BOND_TYPE, rw.ACT_TYPE, rw.REVIEW_STATUS, rw.CREATOR ");
			sql.append("FROM TBPRD_INS_LINKING_PRD_REVIEW rw LEFT JOIN ");
			sql.append("(SELECT DISTINCT TARGET_ID, LINKED_NAME FROM TBPRD_INS_LINKING) ins ");
			sql.append("ON rw.TARGET_ID = ins.TARGET_ID WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("AND rw.CREATOR = :creator ");
			sql.append("UNION ");
			sql.append("SELECT null as SEQNO, info.TARGET_ID, ins.LINKED_NAME, info.STOCK_BOND_TYPE, rw.ACT_TYPE, rw.REVIEW_STATUS, null as CREATOR ");
			sql.append("FROM TBPRD_INS_LINKING_PRD info LEFT JOIN ");
			sql.append("(SELECT DISTINCT TARGET_ID, LINKED_NAME FROM TBPRD_INS_LINKING) ins ");
			sql.append("ON info.TARGET_ID = ins.TARGET_ID ");
			sql.append("LEFT JOIN TBPRD_INS_LINKING_PRD_REVIEW rw on rw.TARGET_ID = info.TARGET_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND info.TARGET_ID NOT IN ");
			sql.append("(SELECT TARGET_ID FROM TBPRD_INS_LINKING_PRD_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
			
			queryCondition.setObject("creator", ws.getUser().getUserID());
		}
		sql.append(") WHERE 1=1 ");
		if(StringUtils.isNotBlank(target_id)) {
			sql.append("AND TARGET_ID like :target_id ");
			queryCondition.setObject("target_id", "%" + target_id + "%");
		}
		
		sql.append("ORDER BY CASE WHEN REVIEW_STATUS = 'W' THEN 0 WHEN REVIEW_STATUS = 'Y' THEN 1 ELSE 2 END ASC, TARGET_ID ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return list;
	}
	
	public void inquireTarget (Object body, IPrimitiveMap header) throws JBranchException {
		PRD210InputVO inputVO = (PRD210InputVO) body;
		PRD210OutputVO return_VO = new PRD210OutputVO();
		return_VO.setResultList(getTaret(inputVO.getTarget_id(), inputVO.getPassParams()));
		this.sendRtnObject(return_VO);
	}
	
	public void checkTragetID (Object body, IPrimitiveMap header) throws JBranchException {
		PRD210InputVO inputVO = (PRD210InputVO) body;
		PRD210OutputVO return_VO = new PRD210OutputVO();
		
		if(StringUtils.isNotBlank(inputVO.getTarget_id())) {
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			
			sql.append("SELECT DISTINCT TARGET_ID, LINKED_NAME FROM TBPRD_INS_LINKING WHERE TARGET_ID = :target_id ");
			sql.append("AND TARGET_ID NOT IN (SELECT TARGET_ID FROM TBPRD_INS_LINKING_PRD) ");
			sql.append("AND TARGET_ID NOT IN (SELECT DISTINCT TARGET_ID FROM TBPRD_INS_LINKING_PRD_REVIEW WHERE REVIEW_STATUS = 'W') ");
			queryCondition.setObject("target_id", inputVO.getTarget_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> resultList = dam.exeQuery(queryCondition);
			
			return_VO.setResultList(resultList);
		}
		this.sendRtnObject(return_VO);
	}
	
	private void saveReview(String target_id, String stock_bond_type, String act_type) throws JBranchException  {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TARGET_ID FROM TBPRD_INS_LINKING_PRD_REVIEW where TARGET_ID = :target_id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("target_id", target_id);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			throw new APException("ehl_01_common_028");		//該商品已待覆核 {0}
		
		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_INS_LINKING_P_REVIEW.nextval AS SEQNO FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQNO");
		
		// add TBPRD_INS_LINKING_PRD_REVIEW
		TBPRD_INS_LINKING_PRD_REVIEWVO reviewVO = new TBPRD_INS_LINKING_PRD_REVIEWVO();
		reviewVO.setSEQNO(seqNo);
		reviewVO.setTARGET_ID(target_id);
		reviewVO.setSTOCK_BOND_TYPE(stock_bond_type);
		reviewVO.setACT_TYPE(act_type);		//A-新增 M-修改 D-刪除
		reviewVO.setREVIEW_STATUS("W");		//W-待覆核
		dam.create(reviewVO);
	}
	
	public void saveTraget (Object body, IPrimitiveMap header) throws JBranchException {
		PRD210InputVO inputVO = (PRD210InputVO) body;
		PRD210OutputVO return_VO = new PRD210OutputVO();
		dam = this.getDataAccessManager();
		String target_id = inputVO.getTarget_id();
		if(StringUtils.isNotBlank(target_id)) {
			TBPRD_INS_LINKING_PRDVO vo = (TBPRD_INS_LINKING_PRDVO) dam.findByPKey(TBPRD_INS_LINKING_PRDVO.TABLE_UID, target_id);
			if(null != vo) {
				saveReview(target_id, inputVO.getStock_bond_type(), "M");	//修改
			} else {
				saveReview(target_id, inputVO.getStock_bond_type(), "A");	//新增
			}
		}
		this.sendRtnObject(null);
	}
	
	public void deleteTarget(Object body, IPrimitiveMap header) throws JBranchException {
		PRD210InputVO inputVO = (PRD210InputVO) body;
		dam = this.getDataAccessManager();
		
		String target_id = inputVO.getTarget_id();
		if(StringUtils.isNotBlank(target_id)) {
			TBPRD_INS_LINKING_PRDVO vo = (TBPRD_INS_LINKING_PRDVO) dam.findByPKey(TBPRD_INS_LINKING_PRDVO.TABLE_UID, target_id);
			if(null != vo) {
				saveReview(target_id, vo.getSTOCK_BOND_TYPE(), "D");	//刪除
			} else {
				throw new APException("ehl_01_common_017");				//資料不存在
			}
		}
		this.sendRtnObject(null);
	}
	
	public void reviewTarget (Object body, IPrimitiveMap header) throws JBranchException {
		PRD210InputVO inputVO = (PRD210InputVO) body;
		dam = this.getDataAccessManager();
		
		for(Map<String, Object> rmap : inputVO.getReview_list()) {
			TBPRD_INS_LINKING_PRD_REVIEWVO reviewVO = new TBPRD_INS_LINKING_PRD_REVIEWVO();
			reviewVO = (TBPRD_INS_LINKING_PRD_REVIEWVO) dam.findByPKey(TBPRD_INS_LINKING_PRD_REVIEWVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(rmap.get("SEQNO"))));
			if (reviewVO != null) {
				if("Y".equals(inputVO.getStatus())) {
					// 新增
					if("A".equals(reviewVO.getACT_TYPE())) {
						TBPRD_INS_LINKING_PRDVO vo = new TBPRD_INS_LINKING_PRDVO();
						vo.setTARGET_ID(reviewVO.getTARGET_ID());
						vo.setSTOCK_BOND_TYPE(reviewVO.getSTOCK_BOND_TYPE());
						vo.setACT_TYPE("A");
						vo.setREVIEW_STATUS("Y");
						dam.create(vo);
					}
					// 修改
					else if("M".equals(reviewVO.getACT_TYPE())) {
						TBPRD_INS_LINKING_PRDVO vo = new TBPRD_INS_LINKING_PRDVO();
						vo = (TBPRD_INS_LINKING_PRDVO) dam.findByPKey(TBPRD_INS_LINKING_PRDVO.TABLE_UID, reviewVO.getTARGET_ID());
						if (vo != null) {
							vo.setSTOCK_BOND_TYPE(reviewVO.getSTOCK_BOND_TYPE());
							vo.setACT_TYPE("M");
							vo.setREVIEW_STATUS("Y");
							dam.update(vo);
						} else {
							throw new APException("ehl_01_common_017");		// 資料不存在
						}
					}
					// 刪除
					else if("D".equals(reviewVO.getACT_TYPE())) {
						TBPRD_INS_LINKING_PRDVO vo = new TBPRD_INS_LINKING_PRDVO();
						vo = (TBPRD_INS_LINKING_PRDVO) dam.findByPKey(TBPRD_INS_LINKING_PRDVO.TABLE_UID, reviewVO.getTARGET_ID());
						if (vo != null) {
							dam.delete(vo);
						} else {
							throw new APException("ehl_01_common_017");		// 資料不存在
						}
					}
				}
				reviewVO.setREVIEW_STATUS(inputVO.getStatus());
				dam.update(reviewVO);
			} else
				throw new APException("ehl_01_common_017");		// 資料不存在
		}
		this.sendRtnObject(null);
	}
	
	public void uploadTarget (Object body, IPrimitiveMap header) throws JBranchException {
		PRD210InputVO inputVO = (PRD210InputVO) body;
		PRD210OutputVO return_VO = new PRD210OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		dam = this.getDataAccessManager();
		
		List<String> error = new ArrayList<String>();
		List<String> error2 = new ArrayList<String>();
		List<String> error3 = new ArrayList<String>();
		List<String> error4 = new ArrayList<String>();
		Set<String> idList = new HashSet<String>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if(!dataCsv.isEmpty()) {
			Map<String, String> ins_type = xmlInfo.doGetVariable("PRD.INS_TYPE", FormatHelper.FORMAT_3);
			Map<String, String> conYN = xmlInfo.doGetVariable("COMMON.YES_NO", FormatHelper.FORMAT_3);
			for(int i = 0;i < dataCsv.size();i++) {
				String[] str = dataCsv.get(i);
				if(i == 0) {
					try {
						if(!"標的代碼".equals(str[0].trim()))
							throw new Exception(str[0]);
						else if(!"股債類型".equals(str[1].substring(0, 4)))
							throw new Exception(str[1]);
					} catch(Exception ex) {
						throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
					}
					continue;
				}
				// check prd_id 請PG判斷，第一欄若為空，就跳過去不判斷。
				if(StringUtils.isBlank(str[0]))
					continue;
				// 使用者很愛重覆上傳
				if(idList.contains(str[0].trim())) {
					error4.add(str[0]);		//商品代碼：{0}，多筆重覆上傳，第二筆之後的已跳過。
					continue;
				}
				idList.add(str[0].trim());
				
				// TBPRD_INS_LINKING
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT DISTINCT TARGET_ID, LINKED_NAME FROM TBPRD_INS_LINKING WHERE TARGET_ID = :target_id ");
				queryCondition.setObject("target_id", str[0].trim());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if (list.size() == 0) {
					error.add(str[0]);		//商品主檔無此商品代碼：{0}，無法上傳編輯。
					continue;
				}
				
				// TBPRD_INS_LINKING_PRD check edit
				Boolean exist = false;
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT TARGET_ID FROM TBPRD_INS_LINKING_PRD WHERE TARGET_ID = :target_id ");
				queryCondition.setObject("target_id", str[0].trim());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				if (list2.size() > 0)
					exist = true;
				
				// TBPRD_INS_LINKING_PRD_REVIEW
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT TARGET_ID FROM TBPRD_INS_LINKING_PRD_REVIEW WHERE TARGET_ID = :target_id AND REVIEW_STATUS = 'W' ");
				queryCondition.setObject("target_id", str[0].trim());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
				if (list3.size() > 0) {
					error2.add(str[0]);		//此商品代碼：{0}，覆核中無法上傳編輯。
					continue;
				}
				
				// seq
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT SQ_TBPRD_INS_LINKING_P_REVIEW.nextval AS SEQNO FROM DUAL ");
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
				BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQNO");
				// add TBPRD_INS_LINKING_PRD_REVIEW
				TBPRD_INS_LINKING_PRD_REVIEWVO vo = new TBPRD_INS_LINKING_PRD_REVIEWVO();
				vo.setSEQNO(seqNo);
				vo.setTARGET_ID(str[0].trim());;
				
//				if(utf_8_length(str[0]) > 16) {
//					error3.add(str[0]);		//此商品代碼：{0}，欄位檢核錯誤。
//					continue;
//				}
//				else
//					vo.setPRD_ID(str[0].trim());
				
				if(StringUtils.isNotBlank(str[1])) {
					String stock_bond_type = str[1].trim().toUpperCase();
					if(!"S".equals(stock_bond_type) && !"B".equals(stock_bond_type)) {
						throw new APException("標的代號：" + str[0] + ":" + str[1] + "，股債類型欄位檢核錯誤。");						
					} else
						vo.setSTOCK_BOND_TYPE(stock_bond_type);
				}
				else
					throw new APException("標的代號：" + str[0] + "，股債類型欄位不得為空。");
				
				if(!exist)
					vo.setACT_TYPE("A");
				else
					vo.setACT_TYPE("M");
				
				vo.setREVIEW_STATUS("W");
				dam.create(vo);
			}
		}
		return_VO.setErrorList(error);
		return_VO.setErrorList2(error2);
		return_VO.setErrorList3(error3);
		return_VO.setErrorList4(error4);
		this.sendRtnObject(return_VO);
	}
	
	public void downloadTarget (Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		PRD210InputVO inputVO = (PRD210InputVO) body;
		PRD210OutputVO return_VO = new PRD210OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		
		List<Map<String, Object>> list = getTaret(inputVO.getTarget_id(), inputVO.getPassParams());
		if(list.size() > 0) {
			// gen csv
			Map<String, String> stock_bond_type = xmlInfo.doGetVariable("FPS.STOCK_BOND_TYPE", FormatHelper.FORMAT_3);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String fileName = "投資型保險標的股債配置_"+ sdf.format(new Date()) + "_" + ws.getUser().getUserID() + ".csv";
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				// 13 column
				String[] records = new String[4];
				int i = 0;
				if ("W".equals(ObjectUtils.toString(map.get("REVIEW_STATUS"))))
					records[i] = "覆核中";
				else
					records[i] = "已覆核";
				records[++i] = checkIsNull(map, "TARGET_ID");
				records[++i] = checkIsNull(map, "LINKED_NAME");
				records[++i] = stock_bond_type.get(checkIsNull(map, "STOCK_BOND_TYPE"));
				
				listCSV.add(records);
			}
			// header
			String[] csvHeader = new String[4];
			int j = 0;
			csvHeader[j] = "覆核狀態";
			csvHeader[++j] = "標的代碼";
			csvHeader[++j] = "標的名稱";
			csvHeader[++j] = "股債類型";
						
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
}
