package com.systex.jbranch.app.server.fps.crm511;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.util.TextUtils;
import com.systex.jbranch.app.common.fps.table.TBCRM_DKYC_ANS_SETVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_DKYC_QSTN_SETVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * crm511
 * 
 * @author moron
 * @date 2016/05/27
 * @spec null
 */
@Component("crm511")
@Scope("request")
public class CRM511 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM511.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM511InputVO inputVO = (CRM511InputVO) body;
		CRM511OutputVO return_VO = new CRM511OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.*, b.EMP_NAME FROM TBCRM_DKYC_QSTN_SET a ");
		sql.append("left join TBORG_MEMBER b on a.MODIFIER = b.EMP_ID where 1=1 ");
		// where
		if (!StringUtils.isBlank(inputVO.getQstn_content())) {
			sql.append("and a.QSTN_CONTENT like :content ");
			queryCondition.setObject("content", "%" + inputVO.getQstn_content() + "%");
		}	
		if (!StringUtils.isBlank(inputVO.getQstn_type())) {
			sql.append("and a.QSTN_TYPE = :type ");
			queryCondition.setObject("type", inputVO.getQstn_type());
		}	
		if (inputVO.getBgn_sDate() != null) {
			sql.append("and a.VALID_BGN_DATE >= :start ");
			queryCondition.setObject("start", inputVO.getBgn_sDate());
		}
		if (inputVO.getBgn_eDate() != null) {
			sql.append("and a.VALID_BGN_DATE <= :end ");
			queryCondition.setObject("end", inputVO.getBgn_eDate());
		}
		if (inputVO.getEnd_sDate() != null) {
			sql.append("and a.VALID_END_DATE >= :start2 ");
			queryCondition.setObject("start2", inputVO.getEnd_sDate());
		}
		if (inputVO.getEnd_eDate() != null) {
			sql.append("and a.VALID_END_DATE <= :end2 ");
			queryCondition.setObject("end2", inputVO.getEnd_eDate());
		}
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
	
	public void deleteKYC(Object body, IPrimitiveMap header) throws JBranchException {
		CRM511InputVO inputVO = (CRM511InputVO) body;
		CRM511OutputVO return_VO = new CRM511OutputVO();
		dam = this.getDataAccessManager();
		
		// TBCRM_DKYC_QSTN_SET
		TBCRM_DKYC_QSTN_SETVO vo = new TBCRM_DKYC_QSTN_SETVO();
		vo = (TBCRM_DKYC_QSTN_SETVO) dam.findByPKey(
				TBCRM_DKYC_QSTN_SETVO.TABLE_UID, inputVO.getQstn_id());
		if (vo != null) {
			dam.delete(vo);
			// TBCRM_DKYC_ANS_SET
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			condition.setQueryString("delete TBCRM_DKYC_ANS_SET where QSTN_ID = :id");
			condition.setObject("id", inputVO.getQstn_id());
			dam.exeUpdate(condition);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		this.sendRtnObject(null);
	}
	
	public void getSN(Object body, IPrimitiveMap header) throws JBranchException {
		CRM511OutputVO return_VO = new CRM511OutputVO();
		dam = this.getDataAccessManager();
		
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		try{
			seqNum = sn.getNextSerialNumber("CRM511");
		}
		catch(Exception e){
			sn.createNewSerial("CRM511", "00000", null, null, null, 1, new Long("99999"), "y", new Long("0"), null);
			seqNum = sn.getNextSerialNumber("CRM511");
		}
		return_VO.setQstn_id(seqNum);
		this.sendRtnObject(return_VO);
	}
	
	public void getAU(Object body, IPrimitiveMap header) throws JBranchException {
		CRM511InputVO inputVO = (CRM511InputVO) body;
		CRM511OutputVO return_VO = new CRM511OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ANS_CONTENT,ANS_VALUE,DISPLAY_ORDER from TBCRM_DKYC_ANS_SET where QSTN_ID = :id order by DISPLAY_ORDER ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("id", inputVO.getQstn_id());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void addKYC(Object body, IPrimitiveMap header) throws Exception {
		CRM511InputVO inputVO = (CRM511InputVO) body;
		dam = this.getDataAccessManager();
		
		// TBCRM_DKYC_QSTN_SET
		TBCRM_DKYC_QSTN_SETVO vo = new TBCRM_DKYC_QSTN_SETVO();
		if(inputVO.getQstn_id() != null){
			vo.setQSTN_ID(inputVO.getQstn_id());
		}
		if(inputVO.getDisplay_layer() != null){
			vo.setDISPLAY_LAYER(inputVO.getDisplay_layer());
		}
		if(inputVO.getDisplay_order() != null){
			vo.setDISPLAY_ORDER(inputVO.getDisplay_order());
		}
		if(inputVO.getQstn_content() != null){
			vo.setQSTN_CONTENT(inputVO.getQstn_content());
		}
		if(inputVO.getWord_surgery() != null){
			vo.setWORD_SURGERY(inputVO.getWord_surgery());
		}
		if(inputVO.getQstn_type() != null){
			vo.setQSTN_TYPE(inputVO.getQstn_type());
		}
		if(inputVO.getQstn_format() != null){
			vo.setQSTN_FORMAT(inputVO.getQstn_format());
		}
		if(inputVO.getOpt_yn() != null){
			vo.setOTH_OPT_YN(inputVO.getOpt_yn());
		}
		if(inputVO.getMemo_yn() != null){
			vo.setEXT_MEMO_YN(inputVO.getMemo_yn());
		}
		if(inputVO.getBgn_sDate() != null){
			vo.setVALID_BGN_DATE(new Timestamp(inputVO.getBgn_sDate().getTime()));
		}
		if(inputVO.getEnd_sDate() != null){
			vo.setVALID_END_DATE(new Timestamp(inputVO.getEnd_sDate().getTime()));
		}
		if(inputVO.getVip_degree() != null){
			vo.setVIP_DEGREE(TextUtils.join(",",inputVO.getVip_degree()));
		}
		if(inputVO.getAum_degree() != null){
			vo.setAUM_DEGREE(TextUtils.join(",",inputVO.getAum_degree()));
		}
		dam.create(vo);
		// TBCRM_DKYC_ANS_SET
		for(Map<String, String> map : inputVO.getAu_list()) {
			TBCRM_DKYC_ANS_SETVO avo = new TBCRM_DKYC_ANS_SETVO();
			String id = getAUSN();
			avo.setSEQ(new BigDecimal(id));
			avo.setQSTN_ID(inputVO.getQstn_id());
			avo.setANS_ID(id);
			avo.setDISPLAY_ORDER(map.get("DISPLAY_ORDER"));
			avo.setANS_CONTENT(map.get("ANS_CONTENT"));
			avo.setANS_VALUE(map.get("ANS_VALUE"));
			dam.create(avo);
		}
		this.sendRtnObject(null);
	}
	private String getAUSN() throws JBranchException {
		  SerialNumberUtil sn = new SerialNumberUtil();
		  String seqNum = "";
		  try{
		    seqNum = sn.getNextSerialNumber("CRM511AU");
		  }
		  catch(Exception e){
		   sn.createNewSerial("CRM511AU", "00000", null, null, null, 1, new Long("99999"), "y", new Long("0"), null);
		   seqNum = sn.getNextSerialNumber("CRM511AU");
		   }
		  return seqNum;
	}
	
	public void updateKYC(Object body, IPrimitiveMap header) throws Exception {
		CRM511InputVO inputVO = (CRM511InputVO) body;
		dam = this.getDataAccessManager();
		
		// TBCRM_DKYC_QSTN_SET
		TBCRM_DKYC_QSTN_SETVO vo = new TBCRM_DKYC_QSTN_SETVO();
		vo = (TBCRM_DKYC_QSTN_SETVO) dam.findByPKey(TBCRM_DKYC_QSTN_SETVO.TABLE_UID, inputVO.getQstn_id());
		if (vo != null) {
			vo.setDISPLAY_LAYER(inputVO.getDisplay_layer());
			vo.setDISPLAY_ORDER(inputVO.getDisplay_order());
			vo.setQSTN_CONTENT(inputVO.getQstn_content());
			vo.setWORD_SURGERY(inputVO.getWord_surgery());
			vo.setQSTN_TYPE(inputVO.getQstn_type());
			vo.setQSTN_FORMAT(inputVO.getQstn_format());
			vo.setOTH_OPT_YN(inputVO.getOpt_yn());
			vo.setEXT_MEMO_YN(inputVO.getMemo_yn());
			if(inputVO.getBgn_sDate() != null)
				vo.setVALID_BGN_DATE(new Timestamp(inputVO.getBgn_sDate().getTime()));
			else
				vo.setVALID_BGN_DATE(null);
			if(inputVO.getEnd_sDate() != null)
				vo.setVALID_END_DATE(new Timestamp(inputVO.getEnd_sDate().getTime()));
			else
				vo.setVALID_END_DATE(null);
			vo.setVIP_DEGREE(TextUtils.join(",",inputVO.getVip_degree()));
			vo.setAUM_DEGREE(TextUtils.join(",",inputVO.getAum_degree()));
			dam.update(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		
		// TBCRM_DKYC_ANS_SET
		// del first
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString("delete TBCRM_DKYC_ANS_SET where QSTN_ID = :id");
		condition.setObject("id", inputVO.getQstn_id());
		dam.exeUpdate(condition);
		// then add
		for(Map<String, String> map : inputVO.getAu_list()) {
			TBCRM_DKYC_ANS_SETVO avo = new TBCRM_DKYC_ANS_SETVO();
			String id = getAUSN();
			avo.setSEQ(new BigDecimal(id));
			avo.setQSTN_ID(inputVO.getQstn_id());
			avo.setANS_ID(id);
			avo.setDISPLAY_ORDER(map.get("DISPLAY_ORDER"));
			avo.setANS_CONTENT(map.get("ANS_CONTENT"));
			avo.setANS_VALUE(map.get("ANS_VALUE"));
			dam.create(avo);
		}
		this.sendRtnObject(null);
	}
	
	public void download(Object body, IPrimitiveMap header) throws JBranchException {
		CRM511InputVO inputVO = (CRM511InputVO) body;
		CRM511OutputVO return_VO = new CRM511OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.*, b.EMP_NAME FROM TBCRM_DKYC_QSTN_SET a ");
		sql.append("left join TBORG_MEMBER b on a.MODIFIER = b.EMP_ID where 1=1 ");
		// where
		if (!StringUtils.isBlank(inputVO.getQstn_content()))
			sql.append("and a.QSTN_CONTENT like :content ");
		if (!StringUtils.isBlank(inputVO.getQstn_type()))
			sql.append("and a.QSTN_TYPE = :type ");
		if (inputVO.getBgn_sDate() != null) {
			sql.append("and a.VALID_BGN_DATE >= :start ");
		}
		if (inputVO.getBgn_eDate() != null) {
			sql.append("and a.VALID_BGN_DATE <= :end ");
		}
		if (inputVO.getEnd_sDate() != null) {
			sql.append("and a.VALID_END_DATE >= :start2 ");
		}
		if (inputVO.getEnd_eDate() != null) {
			sql.append("and a.VALID_END_DATE <= :end2 ");
		}
		queryCondition.setQueryString(sql.toString());
		// where2
		if (!StringUtils.isBlank(inputVO.getQstn_content()))
			queryCondition.setObject("content", "%" + inputVO.getQstn_content() + "%");
		if (!StringUtils.isBlank(inputVO.getQstn_type()))
			queryCondition.setObject("type", inputVO.getQstn_type());
		if (inputVO.getBgn_sDate() != null)
			queryCondition.setObject("start", inputVO.getBgn_sDate());
		if (inputVO.getBgn_eDate() != null)
			queryCondition.setObject("end", inputVO.getBgn_eDate());
		if (inputVO.getEnd_sDate() != null)
			queryCondition.setObject("start2", inputVO.getEnd_sDate());
		if (inputVO.getEnd_eDate() != null)
			queryCondition.setObject("end2", inputVO.getEnd_eDate());
		List<Map<String, Object>> list = dam.executeQuery(queryCondition);
		if(list.size() > 0) {
			// gen csv
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
			String fileName = "深度KYC清單_"+ sdf.format(new Date()) +".csv";
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				// 8 column
				String[] records = new String[8];
				int i = 0;
				records[i] = checkIsNull(map, "QSTN_ID"); // 題目代碼
				records[++i] = checkIsNull(map, "QSTN_CONTENT"); // 題目內容
				records[++i] = checkIsNull(map, "DISPLAY_ORDER"); // 優先順序
				records[++i] = "1".equals(checkIsNull(map, "QSTN_TYPE")) ? "客戶經營-KYC" : "Advisory-KYC"; // 題目類型
				if(map.get("VALID_BGN_DATE") != null) { // 有效起始日期
					records[++i] = sdf2.format(map.get("VALID_BGN_DATE"));
				} else
					records[++i] = "";
				if(map.get("VALID_END_DATE") != null) { // 有效截止日期
					records[++i] = sdf2.format(map.get("VALID_END_DATE"));
				} else
					records[++i] = "";
				records[++i] = checkIsNull(map, "LASTUPDATE"); // 最後修改時間
				records[++i] = checkIsNull(map, "MODIFIER"); // 最後修改人
				listCSV.add(records);
			}
			// header
			String[] csvHeader = new String[8];
			int j = 0;
			csvHeader[j] = "題目代碼";
			csvHeader[++j] = "題目內容";
			csvHeader[++j] = "優先順序";
			csvHeader[++j] = "題目類型";
			csvHeader[++j] = "有效起始日期";
			csvHeader[++j] = "有效截止日期";
			csvHeader[++j] = "最後修改時間";
			csvHeader[++j] = "最後修改人";
			
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
	/**
	* 檢查Map取出欄位是否為Null
	* 
	* @param map
	* @return String
	*/
	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(String.valueOf(map.get(key)))){
			return String.valueOf(map.get(key));
		}else{
			return "";
		}
	}
	
}