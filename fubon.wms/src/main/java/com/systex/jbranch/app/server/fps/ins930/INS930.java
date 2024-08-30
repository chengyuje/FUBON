package com.systex.jbranch.app.server.fps.ins930;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBINS_PARA_HEADERVO;
import com.systex.jbranch.app.common.fps.table.TBINS_PARA_HOSPITALPK;
import com.systex.jbranch.app.common.fps.table.TBINS_PARA_HOSPITALVO;
import com.systex.jbranch.app.common.fps.table.TBINS_REPORTVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_INS_SUGGESTPK;
import com.systex.jbranch.app.common.fps.table.TBPRD_INS_SUGGESTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("ins930")
@Scope("request")
public class INS930 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(INS930.class);
	
	public void initial(Object body, IPrimitiveMap header) throws JBranchException {
		INS930OutputVO return_VO = new INS930OutputVO();
		dam = this.getDataAccessManager();
		
		// TBINS_PARA_HEADER
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT PARA_NO, CAL_DESC, STATUS FROM (SELECT PARA_NO, CAL_DESC, STATUS FROM TBINS_PARA_HEADER WHERE STATUS IN ('A', 'P') AND PARA_TYPE = '3' ORDER BY SUBMIT_DATE DESC) WHERE ROWNUM = 1");
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(list.size() == 0)
			throw new APException("無設定初始資料，請聯絡IT人員");
		BigDecimal PARA_NO = new BigDecimal(list.get(0).get("PARA_NO").toString());
		return_VO.setStatus(list.get(0).get("STATUS").toString());
		return_VO.setCal_desc(ObjectUtils.toString(list.get(0).get("CAL_DESC")));
		
		// TBINS_PARA_HOSPITAL
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT * FROM TBINS_PARA_HOSPITAL WHERE PARA_NO = :para_no ORDER BY HOSPITAL_TYPE, WARD_TYPE");
		queryCondition.setObject("para_no", PARA_NO);
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		return_VO.setHospitalList(list2);
		
		// TBPRD_INS_SUGGEST
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT * FROM VWPRD_INS_SUGGEST SUGGEST ");
		sql.append(" WHERE 1 = 1  ");
		sql.append(" AND SUGGEST.PARA_NO = :para_no ");
		sql.append(" ORDER BY SUGGEST.PRD_ID, SUGGEST.INSPRD_ANNUAL "); // 排序
		
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("para_no", PARA_NO);
		List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
		return_VO.setSuggestList(list3);
		
		// TBINS_REPORT
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT KEYNO, FILE_NAME FROM TBINS_REPORT WHERE PLAN_ID = TO_CHAR(:para_no)");
		queryCondition.setObject("para_no", PARA_NO);
		List<Map<String, Object>> list4 = dam.exeQuery(queryCondition);
		return_VO.setReportList(list4);
		
		// 2018/1/19 主管覆核要更新舊的非新的
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'INS930' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0)
			return_VO.setPara_no(PARA_NO);
		else if("P".equals(list.get(0).get("STATUS")))
			return_VO.setPara_no(PARA_NO);
		else {
			BigDecimal NEW_PARA_NO = new BigDecimal(getSN("HEADER"));
			return_VO.setPara_no(NEW_PARA_NO);
		}
		
		this.sendRtnObject(return_VO);
	}
	
	public void downloadDoc(Object body, IPrimitiveMap header) throws Exception {
		INS930InputVO inputVO = (INS930InputVO) body;
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT FILE_NAME, REPORT_FILE FROM TBINS_REPORT WHERE KEYNO = :key_no");
		queryCondition.setObject("key_no", inputVO.getPara_no());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String fileName = (String) list.get(0).get("FILE_NAME");
		String uuid = UUID.randomUUID().toString();
		Blob blob = (Blob) list.get(0).get("REPORT_FILE");
		int blobLength = (int) blob.length();  
		byte[] blobAsBytes = blob.getBytes(1, blobLength);
		
		File targetFile = new File(filePath, uuid);
		FileOutputStream fos = new FileOutputStream(targetFile);
	    fos.write(blobAsBytes);
	    fos.close();
	    this.notifyClientToDownloadFile("temp//"+uuid, fileName);
	}
	
	public void goReview(Object body, IPrimitiveMap header) throws Exception {
		INS930InputVO inputVO = (INS930InputVO) body;
		dam = this.getDataAccessManager();
		
		// 如果找的到, 代表上面查詢是覆核 return_VO.setPara_no(PARA_NO);
		TBINS_PARA_HEADERVO head_vo = (TBINS_PARA_HEADERVO) dam.findByPKey(TBINS_PARA_HEADERVO.TABLE_UID, inputVO.getPara_no());
		if (head_vo != null) {
			head_vo.setSUBMIT_DATE(new Timestamp(System.currentTimeMillis()));
			head_vo.setCAL_DESC(inputVO.getCal_desc());
			dam.update(head_vo);
		}
		else {
			TBINS_PARA_HEADERVO new_head_vo = new TBINS_PARA_HEADERVO();
			new_head_vo.setPARA_NO(inputVO.getPara_no());
			new_head_vo.setPARA_TYPE("3");
			new_head_vo.setSUBMIT_DATE(new Timestamp(System.currentTimeMillis()));
			new_head_vo.setCAL_DESC(inputVO.getCal_desc());
			new_head_vo.setSTATUS("P");
			dam.create(new_head_vo);
		}
		
		// TBINS_PARA_HOSPITAL
		QueryConditionIF del_con = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		del_con.setQueryString("DELETE FROM TBINS_PARA_HOSPITAL WHERE PARA_NO = :para_no");
		del_con.setObject("para_no", inputVO.getPara_no());
		dam.exeUpdate(del_con);
		for(Map<String, Object> map : inputVO.getHospitalList()) {
			BigDecimal NEW_PARA_NO = new BigDecimal(getSN("HEADER"));
			TBINS_PARA_HOSPITALPK hos_pk = new TBINS_PARA_HOSPITALPK();
			hos_pk.setH_KEYNO(NEW_PARA_NO);
			hos_pk.setPARA_NO(inputVO.getPara_no());
			TBINS_PARA_HOSPITALVO hos_vo = new TBINS_PARA_HOSPITALVO();
			hos_vo.setcomp_id(hos_pk);
			hos_vo.setHOSPITAL_TYPE(ObjectUtils.toString(map.get("HOSPITAL_TYPE")));
			hos_vo.setWARD_TYPE(ObjectUtils.toString(map.get("WARD_TYPE")));
			if(StringUtils.isBlank(ObjectUtils.toString(map.get("DAY_AMT"))))
				hos_vo.setDAY_AMT(new BigDecimal(0));
			else
				hos_vo.setDAY_AMT(new BigDecimal(ObjectUtils.toString(map.get("DAY_AMT"))));
			dam.create(hos_vo);
		}
		// TBPRD_INS_SUGGEST
		del_con = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		del_con.setQueryString("DELETE FROM TBPRD_INS_SUGGEST WHERE PARA_NO = :para_no");
		del_con.setObject("para_no", inputVO.getPara_no());
		dam.exeUpdate(del_con);
		for(Map<String, Object> map : inputVO.getSuggestList()) {
			TBPRD_INS_SUGGESTPK sug_pk = new TBPRD_INS_SUGGESTPK();
			sug_pk.setKEY_NO(new BigDecimal(map.get("KEY_NO").toString()));
			sug_pk.setPARA_NO(inputVO.getPara_no());
			sug_pk.setSUGGEST_TYPE(map.get("SUGGEST_TYPE").toString());
			TBPRD_INS_SUGGESTVO sug_vo = new TBPRD_INS_SUGGESTVO();
			sug_vo.setcomp_id(sug_pk);
			sug_vo.setINSDATA_KEYNO(ObjectUtils.toString(map.get("INSDATA_KEYNO")));
			sug_vo.setPOLICY_AMT_DISTANCE(new BigDecimal(map.get("POLICY_AMT_DISTANCE").toString()));
			sug_vo.setCVRG_RATIO(new BigDecimal(map.get("CVRG_RATIO").toString()));
			sug_vo.setPOLICY_AMT_MIN(new BigDecimal(map.get("POLICY_AMT_MIN").toString()));
			sug_vo.setPOLICY_AMT_MAX(new BigDecimal(map.get("POLICY_AMT_MAX").toString()));
			
			dam.create(sug_vo);
		}
		// TBINS_REPORT
		// 非覆核修改舊的, 搬移舊上傳的檔案
		if(inputVO.getFile_seq() != null && head_vo == null) {
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("SELECT * FROM TBINS_REPORT WHERE KEYNO = :key_no");
			queryCondition.setObject("key_no", inputVO.getFile_seq());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			for(Map<String, Object> map : list) {
				TBINS_REPORTVO rep_vo = new TBINS_REPORTVO();
				rep_vo.setKEYNO(new BigDecimal(getSN("REPORT")));
				rep_vo.setPLAN_ID(inputVO.getPara_no().toString());
				if(map.get("FILE_NAME") != null)
					rep_vo.setFILE_NAME(ObjectUtils.toString(map.get("FILE_NAME")));
				if(map.get("REPORT_FILE") != null)
					rep_vo.setREPORT_FILE((Blob) map.get("REPORT_FILE"));
				dam.create(rep_vo);
			}
		} else {
			if(StringUtils.isNotBlank(inputVO.getFileName())) {
				del_con = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				del_con.setQueryString("DELETE FROM TBINS_REPORT WHERE PLAN_ID = TO_CHAR(:para_no)");
				del_con.setObject("para_no", inputVO.getPara_no());
				dam.exeUpdate(del_con);
				
				String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				byte[] data = Files.readAllBytes(new File(tempPath, inputVO.getFileName()).toPath());
				BigDecimal key_no = new BigDecimal(getSN("REPORT"));
				TBINS_REPORTVO rep_vo = new TBINS_REPORTVO();
				rep_vo.setKEYNO(key_no);
				rep_vo.setPLAN_ID(inputVO.getPara_no().toString());
				rep_vo.setFILE_NAME(inputVO.getFileRealName());
				rep_vo.setREPORT_FILE(ObjectUtil.byteArrToBlob(data));
				dam.create(rep_vo);
			}
		}
		
		this.sendRtnObject(null);
	}
	
	public void review(Object body, IPrimitiveMap header) throws JBranchException {
		INS930InputVO inputVO = (INS930InputVO) body;
		dam = this.getDataAccessManager();
		
		TBINS_PARA_HEADERVO vo = (TBINS_PARA_HEADERVO) dam.findByPKey(TBINS_PARA_HEADERVO.TABLE_UID, inputVO.getPara_no());
		if (vo != null) {
			vo.setSTATUS(inputVO.getStatus());
			if("A".equals(inputVO.getStatus())) {
				vo.setEFFECT_DATE(new Timestamp(System.currentTimeMillis()));
				
				// old code 更新前一筆核准資料的"有效截止日"
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString("SELECT MAX(PARA_NO) AS PARA_NO FROM TBINS_PARA_HEADER WHERE PARA_NO < :para_no AND STATUS = 'A' AND PARA_TYPE = '3'");
				queryCondition.setObject("para_no", inputVO.getPara_no());
				List<Map<String, Object>> end_para_no_list = dam.exeQuery(queryCondition);
				if(end_para_no_list.size() > 0 && end_para_no_list.get(0).get("PARA_NO") != null) {
					BigDecimal end_para_no = (BigDecimal) end_para_no_list.get(0).get("PARA_NO");
					TBINS_PARA_HEADERVO endHeaderVO = (TBINS_PARA_HEADERVO) dam.findByPKey(TBINS_PARA_HEADERVO.TABLE_UID, end_para_no);
					if(endHeaderVO != null) {
						endHeaderVO.setEXPIRY_DATE(new Timestamp(System.currentTimeMillis()));
						dam.update(endHeaderVO);
					}
				}
				//
			}
			dam.update(vo);
		} else
			throw new APException("ehl_01_common_001");
		
		this.sendRtnObject(null);
	}
	
	private String getSN(String name) throws JBranchException {
		String ans = "";
		switch(name) {
			case "HEADER":
				QueryConditionIF qc_header = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				qc_header.setQueryString("SELECT TBPRD_INS_SEQ.nextval AS SEQ FROM DUAL");
				List<Map<String, Object>> head_list = dam.exeQuery(qc_header);
				ans = ObjectUtils.toString(head_list.get(0).get("SEQ"));
				break;
			case "REPORT":
				QueryConditionIF qc_report = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				qc_report.setQueryString("SELECT TBINS_REPORT_SEQ.nextval AS SEQ FROM DUAL");
				List<Map<String, Object>> report_list = dam.exeQuery(qc_report);
				ans = ObjectUtils.toString(report_list.get(0).get("SEQ"));
				break;
		}
		return ans;
	}
	

}