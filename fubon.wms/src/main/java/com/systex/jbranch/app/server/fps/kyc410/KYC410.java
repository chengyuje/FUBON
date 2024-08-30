package com.systex.jbranch.app.server.fps.kyc410;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSYS_FILE_DETAILVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_FILE_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_QST_ANSWERPK;
import com.systex.jbranch.app.common.fps.table.TBSYS_QST_ANSWERVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_QST_QUESTIONVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * KYC410
 * 
 * @author Sam
 * @date 2018/05/18
 * @spec null
 * 金融(含債卷)專案知識評估 結構型專案知識評估 題庫
 *
 */
@Component("kyc410")
@Scope("request")
public class KYC410 extends FubonWmsBizLogic {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	DataAccessManager dam = null;
	
	public void queryData(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		KYC410InputVO inputVO = (KYC410InputVO) body;
		KYC410OutputVO outputVO = new KYC410OutputVO();
		try{
			List<Map<String,Object>> Main_list = new ArrayList<Map<String,Object>>(); 
			
			dam = new DataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);			
			StringBuilder sb = new StringBuilder();
			
			sb.append(" SELECT QUESTION_VERSION, QUESTION_DESC,QUESTION_TYPE, ");
			sb.append(" 	ANS_OTHER_FLAG,ANS_MEMO_FLAG,STATUS,CREATOR,to_char(CREATETIME,'YYYY/MM/DD') as CREATETIME,	");
			sb.append("		CORR_ANS	");
			sb.append("	FROM TBSYS_QST_QUESTION ");
			sb.append("	WHERE MODULE_CATEGORY = 'PRO' ");
			if(!"".equals(inputVO.getQUESTION_DESC())){
				sb.append(" AND QUESTION_DESC LIKE :question");
				qc.setObject("question", "%" + inputVO.getQUESTION_DESC() + "%");
			}
			if(inputVO.getsDate() != null && inputVO.geteDate() == null){
				sb.append(" AND TRUNC(CREATETIME) >= :sDate ");
				qc.setObject("sDate", inputVO.getsDate());
			}
			if(inputVO.geteDate() != null && inputVO.getsDate() == null){
				sb.append(" AND TRUNC(CREATETIME) <= :eDate ");
				qc.setObject("eDate", inputVO.geteDate());
			}
			if(inputVO.getsDate() != null && inputVO.geteDate()!= null){
				sb.append(" AND TRUNC(CREATETIME) >= :sDate and TRUNC(CREATETIME) <= :eDate ");
				qc.setObject("sDate", inputVO.getsDate());
				qc.setObject("eDate", inputVO.geteDate());
			}
			sb.append(" ORDER BY CREATETIME DESC, QUESTION_VERSION DESC ");
			qc.setQueryString(sb.toString());
			Main_list = dam.exeQuery(qc);
			for(Map<String, Object> map : Main_list){
				sb = new StringBuilder();
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				
				sb.append(" SELECT QUESTION_VERSION, ANSWER_DESC,ANSWER_SEQ FROM TBSYS_QST_ANSWER WHERE QUESTION_VERSION = :question_version ");
				
				qc.setObject("question_version", map.get("QUESTION_VERSION"));
				qc.setQueryString(sb.toString());
				
				map.put("Ans", dam.exeQuery(qc));
				
				List<Map<String,Object>> picture_list = new ArrayList<Map<String,Object>>(); 
				sb = new StringBuilder();
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				
				sb.append(" SELECT a.PICTURE, b.DOC_ID, b.DOC_NAME ");
				sb.append(" FROM TBSYS_QST_QUESTION a, TBSYS_FILE_MAIN b ");
				sb.append(" WHERE a.PICTURE = b.DOC_ID and QUESTION_VERSION = :question_version ");
				
				qc.setObject("question_version", map.get("QUESTION_VERSION"));
				qc.setQueryString(sb.toString());
				picture_list = dam.exeQuery(qc);
				
				if(picture_list.size()>0){
					for(Map<String, Object> picture:picture_list){
						map.put("DOC_ID", picture.get("DOC_ID"));
						map.put("DOC_NAME", picture.get("DOC_NAME"));
						map.put("PICTURE", "Y");
					}
				}else{
					map.put("PICTURE", "N");
				}
			}
			outputVO.setQstQustionLst(Main_list);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		sendRtnObject(outputVO);
	}
	
	
	public void delete(Object body, IPrimitiveMap<?> header) throws JBranchException {
		KYC410InputVO inputVO = (KYC410InputVO) body;
		dam = new DataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		sb.append(" DELETE FROM TBSYS_QST_ANSWER WHERE QUESTION_VERSION = :question_version");
		
		qc.setObject("question_version", inputVO.getQUESTION_VERSION());
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		sb.append(" DELETE FROM TBSYS_QST_QUESTION WHERE QUESTION_VERSION = :question_version");
		qc.setObject("question_version", inputVO.getQUESTION_VERSION());
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		sendRtnObject(null);
	}

	
	public void saveData(Object body, IPrimitiveMap header) throws Exception {
		KYC410InputVO inputVO = (KYC410InputVO) body;
		SerialNumberUtil sn = new SerialNumberUtil();
		String SN = getFileSN(sn);
		String QUESTION_VERSION = getSeq();
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		try {
			dam = new DataAccessManager();
			TBSYS_QST_QUESTIONVO tqq_vo = new TBSYS_QST_QUESTIONVO();
			tqq_vo.setQUESTION_VERSION(QUESTION_VERSION);
			tqq_vo.setQUESTION_DESC(inputVO.getQUESTION_DESC());
			tqq_vo.setQUESTION_TYPE(inputVO.getQUESTION_TYPE());
			tqq_vo.setANS_OTHER_FLAG(inputVO.getANS_OTHER_FLAG());
			tqq_vo.setANS_MEMO_FLAG(inputVO.getANS_MEMO_FLAG());
			tqq_vo.setMODULE_CATEGORY("PRO");
			tqq_vo.setSTATUS("N");
			tqq_vo.setCORR_ANS(inputVO.getCORR_ANS());
			if(!StringUtils.isBlank(inputVO.getTempName())){
				tqq_vo.setPICTURE(SN);
			}
			dam.create(tqq_vo);		
			
			if(inputVO.getANSWER_DESC().size() > 0){
				for(Map<String, Object> map:inputVO.getANSWER_DESC()){
					if(map.get("ANS_DESC") != null){
						Object answer_seq = map.get("Display_order");
						dam = new DataAccessManager();
						
						TBSYS_QST_ANSWERPK tqa_pk = new TBSYS_QST_ANSWERPK();
						tqa_pk.setANSWER_SEQ(new BigDecimal(answer_seq.toString()));
						tqa_pk.setQUESTION_VERSION(QUESTION_VERSION);
						
						TBSYS_QST_ANSWERVO tqa_vo = new TBSYS_QST_ANSWERVO();
						tqa_vo.setcomp_id(tqa_pk);
						tqa_vo.setANSWER_DESC(map.get("ANS_DESC").toString());
						dam.create(tqa_vo);

					}
				}
			}
			sendRtnObject(null);
		} catch (Exception e) {
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	
	public void updateData(Object body, IPrimitiveMap header) throws Exception {
		KYC410InputVO inputVO = (KYC410InputVO) body;
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		String errorMsg = "";
		SerialNumberUtil sn = new SerialNumberUtil();
		String SN = getFileSN(sn);
		try {
			dam = new DataAccessManager();
			TBSYS_QST_QUESTIONVO tqq_vo = new TBSYS_QST_QUESTIONVO();
			tqq_vo = (TBSYS_QST_QUESTIONVO) dam.findByPKey(TBSYS_QST_QUESTIONVO.TABLE_UID, inputVO.getQUESTION_VERSION());
			tqq_vo.setCORR_ANS(inputVO.getCORR_ANS());
			if(tqq_vo != null){
				tqq_vo.setQUESTION_DESC(inputVO.getQUESTION_DESC());
				tqq_vo.setQUESTION_TYPE(inputVO.getQUESTION_TYPE());
				dam.update(tqq_vo);
			}else{
				errorMsg = "ehl_01_common_009";
				throw new APException(errorMsg);
			}
			
			//刪除選取到的row資料
			if(inputVO.getDEL_ANSWER_DESC().size() > 0){
				for(Map<String,Object> delList : inputVO.getDEL_ANSWER_DESC()){
					dam = new DataAccessManager();
					
					TBSYS_QST_ANSWERPK tqa_pk = new TBSYS_QST_ANSWERPK();
					tqa_pk.setANSWER_SEQ(new BigDecimal(delList.get("ANSWER_SEQ").toString()));
					tqa_pk.setQUESTION_VERSION(inputVO.getQUESTION_VERSION());
					
					TBSYS_QST_ANSWERVO tqa_vo = new TBSYS_QST_ANSWERVO();
					tqa_vo = (TBSYS_QST_ANSWERVO) dam.findByPKey(TBSYS_QST_ANSWERVO.TABLE_UID, tqa_pk);
					if(tqa_vo != null){
						dam.delete(tqa_vo);
					}else{
						errorMsg = "ehl_01_common_009";
						throw new APException(errorMsg);
					}
				}
				
				for(Map<String, Object> ans_desc : inputVO.getANSWER_DESC()){
					if(ans_desc.get("ANS_DESC") == null){
						dam = new DataAccessManager();
						
						TBSYS_QST_ANSWERPK tqa_pk = new TBSYS_QST_ANSWERPK();
						tqa_pk.setANSWER_SEQ(new BigDecimal(ans_desc.get("ANSWER_SEQ").toString()));
						tqa_pk.setQUESTION_VERSION(inputVO.getQUESTION_VERSION());
						
						TBSYS_QST_ANSWERVO tqa_vo = new TBSYS_QST_ANSWERVO();
						tqa_vo = (TBSYS_QST_ANSWERVO) dam.findByPKey(TBSYS_QST_ANSWERVO.TABLE_UID, tqa_pk);
						
						if(tqa_vo != null){
							dam.delete(tqa_vo);
						}else{
							errorMsg = "ehl_01_common_009";
							throw new APException(errorMsg);
						}
					}
				}
				
				for(Map<String, Object> ans_desc : inputVO.getANSWER_DESC()){
					TBSYS_QST_ANSWERPK tqa_pk = new TBSYS_QST_ANSWERPK();
					TBSYS_QST_ANSWERVO tqa_vo = new TBSYS_QST_ANSWERVO();
					
					Object answer_seq = ans_desc.get("Display_order");
					dam = new DataAccessManager();
					
					tqa_pk = new TBSYS_QST_ANSWERPK();
					tqa_pk.setANSWER_SEQ(new BigDecimal(answer_seq.toString()));
					tqa_pk.setQUESTION_VERSION(inputVO.getQUESTION_VERSION());
					
					tqa_vo = new TBSYS_QST_ANSWERVO();
					tqa_vo.setcomp_id(tqa_pk);
					
					if(ans_desc.get("ANS_DESC") != null){
						tqa_vo.setANSWER_DESC(ans_desc.get("ANS_DESC").toString());
					}else{
						tqa_vo.setANSWER_DESC(ans_desc.get("ANSWER_DESC").toString());
					}
					dam.create(tqa_vo);
				}
			}else{
				for(Map<String, Object> ans_desc : inputVO.getANSWER_DESC()){
					if(ans_desc.get("ANS_DESC") != null){
						Object answer_seq = ans_desc.get("Display_order");
						dam = new DataAccessManager();
						
						TBSYS_QST_ANSWERPK tqa_pk = new TBSYS_QST_ANSWERPK();
						tqa_pk.setANSWER_SEQ(new BigDecimal(answer_seq.toString()));
						tqa_pk.setQUESTION_VERSION(inputVO.getQUESTION_VERSION());
						
						TBSYS_QST_ANSWERVO tqa_vo = new TBSYS_QST_ANSWERVO();
						tqa_vo.setcomp_id(tqa_pk);
						tqa_vo.setANSWER_DESC(ans_desc.get("ANS_DESC").toString());
						dam.create(tqa_vo);
						
						tqq_vo = new TBSYS_QST_QUESTIONVO();
						tqq_vo = (TBSYS_QST_QUESTIONVO) dam.findByPKey(TBSYS_QST_QUESTIONVO.TABLE_UID, inputVO.getQUESTION_VERSION());
						if(tqq_vo != null){
							tqq_vo.setCORR_ANS(inputVO.getCORR_ANS());
							tqq_vo.setQUESTION_TYPE(inputVO.getQUESTION_TYPE());
						}
					}
					else if(ans_desc.get("ANSWER_DESC") != null && ans_desc.get("CORR_ANS") != null){
						dam = new DataAccessManager();
						tqq_vo = new TBSYS_QST_QUESTIONVO();
						tqq_vo = (TBSYS_QST_QUESTIONVO) dam.findByPKey(TBSYS_QST_QUESTIONVO.TABLE_UID, inputVO.getQUESTION_VERSION());
						if(tqq_vo != null){
							tqq_vo.setCORR_ANS(inputVO.getCORR_ANS());
							tqq_vo.setQUESTION_TYPE(inputVO.getQUESTION_TYPE());
						}					
					}
				}
				dam.update(tqq_vo);
			}
			sendRtnObject(null);
		} catch (Exception e) {
			if(errorMsg.length()>0){
				throw new APException(errorMsg);
			}else{
				throw new APException("系統發生錯誤請洽系統管理員");
			}
		}
	}
	
	private String getSeq() throws JBranchException{
		SerialNumberUtil sn = new SerialNumberUtil();
		java.util.Date date = new java.util.Date();
		SimpleDateFormat simple = new SimpleDateFormat("yyyyMMdd");
		String date_seqnum = simple.format(date);
		String seqNum = "";
		try {
			seqNum = date_seqnum + (sn.getNextSerialNumber("PRO410")).substring(1, 5);
		} catch (Exception e) {
			sn.createNewSerial("PRO410", "00000", null, null, null, 1, new Long("99999") , "y", new Long("0"), null);
			seqNum = date_seqnum + (sn.getNextSerialNumber("PRO410")).substring(1, 5);
		}
		return seqNum;
	}
	
	private String getAnSeq() throws JBranchException{
		SerialNumberUtil sn = new SerialNumberUtil();
		String answer_seq = "";
		try {
			answer_seq = (sn.getNextSerialNumber("PRO410")).substring(1, 5);
		} catch (Exception e) {
			sn.createNewSerial("PRO410", "00000", null, null, null, 1, new Long("99999") , "y", new Long("0"), null);
			answer_seq =(sn.getNextSerialNumber("PRO410")).substring(1, 5);
		}
		return answer_seq;
	}
	
	private String getSeqNum (SerialNumberUtil sn,String TXN_ID,String format, Timestamp timeStamp, Integer minNum, 
		 	  Long maxNum, String status, Long nowNum) throws JBranchException {
		String seqNum = "";
		try{
			seqNum = sn.getNextSerialNumber(TXN_ID);
		} catch(Exception e){
			sn.createNewSerial(TXN_ID, format, 1, "d", timeStamp, minNum, maxNum, status, nowNum, null);
			seqNum = sn.getNextSerialNumber(TXN_ID);
		}
		
		return seqNum;
	}
	
	private String getFileSN(SerialNumberUtil sn) throws JBranchException {
		
		return getSeqNum(sn, "File", "00000000000000", null, 1, new Long("99999999999999"), "y", new Long("0"));
	}
}
