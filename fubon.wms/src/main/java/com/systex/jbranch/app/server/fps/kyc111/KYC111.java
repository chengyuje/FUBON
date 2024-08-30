package com.systex.jbranch.app.server.fps.kyc111;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_DKYC_DATAVO;
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
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;


/**
 * kyc111
 * 
 * @author Jimmy
 * @date 2016/07/20
 * @spec null
 */
@Component("kyc111")
@Scope("request")
public class KYC111 extends FubonWmsBizLogic{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String getSeq() throws JBranchException{
		SerialNumberUtil sn = new SerialNumberUtil();
		java.util.Date date = new java.util.Date();
		SimpleDateFormat simple = new SimpleDateFormat("yyyyMMdd");
		String date_seqnum = simple.format(date);
		String seqNum = "";
		try {
			seqNum = date_seqnum+(sn.getNextSerialNumber("KYC111")).substring(1, 5);
		} catch (Exception e) {
			sn.createNewSerial("KYC111", "00000", null, null, null, 1,new Long("99999") , "y", new Long("0"), null);
			seqNum = date_seqnum+(sn.getNextSerialNumber("KYC111")).substring(1, 5);
		}
		return seqNum;
	}
	
	private String getAnSeq() throws JBranchException{
		SerialNumberUtil sn = new SerialNumberUtil();
		String answer_seq = "";
		try {
			answer_seq = (sn.getNextSerialNumber("KYC111")).substring(1, 5);
		} catch (Exception e) {
			sn.createNewSerial("KYC111", "00000", null, null, null, 1,new Long("99999") , "y", new Long("0"), null);
			answer_seq =(sn.getNextSerialNumber("KYC111")).substring(1, 5);
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
	
	public void saveData(Object body, IPrimitiveMap header) throws Exception {
		KYC111InputVO inputVO = (KYC111InputVO) body;
		SerialNumberUtil sn = new SerialNumberUtil();
		String SN = getFileSN(sn);
		String QUESTION_VERSION = getSeq();
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
//		try {
			TBSYS_QST_QUESTIONVO tqq_vo = new TBSYS_QST_QUESTIONVO();
			tqq_vo.setQUESTION_VERSION(QUESTION_VERSION);
			tqq_vo.setQUESTION_DESC(inputVO.getQUESTION_DESC());
			tqq_vo.setQUESTION_DESC_ENG(inputVO.getQUESTION_DESC_ENG());
			tqq_vo.setQUESTION_TYPE(inputVO.getQUESTION_TYPE());
			tqq_vo.setANS_OTHER_FLAG(inputVO.getANS_OTHER_FLAG());
			tqq_vo.setANS_MEMO_FLAG(inputVO.getANS_MEMO_FLAG());
			tqq_vo.setMODULE_CATEGORY("KYC");
			tqq_vo.setSTATUS("N");
			if(!StringUtils.isBlank(inputVO.getTempName())){
				tqq_vo.setPICTURE(SN);
			}
			getDataAccessManager().create(tqq_vo);

			if(!StringUtils.isBlank(inputVO.getTempName())){
				Path path = Paths.get(new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH),inputVO.getTempName()).toString());
				byte[] data = Files.readAllBytes(path);
				TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
				fvo.setDOC_ID(SN);
				fvo.setDOC_NAME(inputVO.getRealTempName());
				fvo.setSUBSYSTEM_TYPE("KYC");
				fvo.setDOC_TYPE("01");
				getDataAccessManager().create(fvo);
				
				TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
				dvo.setDOC_ID(SN);
				dvo.setDOC_VERSION_STATUS("2");
				dvo.setDOC_FILE_TYPE("D");
				dvo.setDOC_FILE(ObjectUtil.byteArrToBlob(data));
				getDataAccessManager().create(dvo);
			}
			
			
			if(inputVO.getANSWER_DESC().size()>0){
				for(Map<String, Object> map:inputVO.getANSWER_DESC()){
					if(map.get("ANS_DESC") != null){
						Object answer_seq = map.get("Display_order");
						
						TBSYS_QST_ANSWERPK tqa_pk = new TBSYS_QST_ANSWERPK();
						tqa_pk.setANSWER_SEQ(new BigDecimal(answer_seq.toString()));
						tqa_pk.setQUESTION_VERSION(QUESTION_VERSION);
						TBSYS_QST_ANSWERVO tqa_vo = new TBSYS_QST_ANSWERVO();
						tqa_vo.setcomp_id(tqa_pk);
						tqa_vo.setANSWER_DESC(ObjectUtils.toString(map.get("ANS_DESC")));
						tqa_vo.setANSWER_DESC_ENG(ObjectUtils.toString(map.get("ANS_DESC_ENG")));
						getDataAccessManager().create(tqa_vo);

					}
				}
			}
			sendRtnObject(null);
//		} catch (Exception e) {
//			throw new APException("系統發生錯誤請洽系統管理員");
//		}
	}
	
	
	public void updateData(Object body, IPrimitiveMap header) throws Exception {
		KYC111InputVO inputVO = (KYC111InputVO) body;
		String errorMsg = "";
		SerialNumberUtil sn = new SerialNumberUtil();
		String SN = getFileSN(sn);
		try {
			TBSYS_QST_QUESTIONVO tqq_vo = new TBSYS_QST_QUESTIONVO();
			tqq_vo = (TBSYS_QST_QUESTIONVO) getDataAccessManager().findByPKey(TBSYS_QST_QUESTIONVO.TABLE_UID, inputVO.getQUESTION_VERSION());
			if(tqq_vo != null){
				tqq_vo.setQUESTION_DESC(inputVO.getQUESTION_DESC());
				tqq_vo.setQUESTION_DESC_ENG(inputVO.getQUESTION_DESC_ENG());
				tqq_vo.setQUESTION_TYPE(inputVO.getQUESTION_TYPE());
				tqq_vo.setANS_OTHER_FLAG(inputVO.getANS_OTHER_FLAG());
				tqq_vo.setANS_MEMO_FLAG(inputVO.getANS_MEMO_FLAG());
				getDataAccessManager().update(tqq_vo);
			}else{
				errorMsg = "ehl_01_common_009";
				throw new APException(errorMsg);
			}


			if(inputVO.getRealTempName() != null){
				tqq_vo = new TBSYS_QST_QUESTIONVO();
				tqq_vo = (TBSYS_QST_QUESTIONVO) getDataAccessManager().findByPKey(TBSYS_QST_QUESTIONVO.TABLE_UID, inputVO.getQUESTION_VERSION());
				if(tqq_vo != null){
					tqq_vo.setPICTURE(SN);
					getDataAccessManager().update(tqq_vo);
				}else{
					errorMsg = "ehl_01_common_009";
					throw new APException(errorMsg);
				}

				if(inputVO.getDOC_ID() != null){
					TBSYS_FILE_DETAILVO tfd_vo = new TBSYS_FILE_DETAILVO();
					tfd_vo = (TBSYS_FILE_DETAILVO) getDataAccessManager().findByPKey(TBSYS_FILE_DETAILVO.TABLE_UID, inputVO.getDOC_ID());
					if(tqq_vo != null){
						getDataAccessManager().delete(tfd_vo);
					}else{
						errorMsg = "ehl_01_common_009";
						throw new APException(errorMsg);
					}
				}
				if(!StringUtils.isBlank(inputVO.getTempName())){
					Path path = Paths.get(new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH),inputVO.getTempName()).toString());
					byte[] data = Files.readAllBytes(path);
					TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
					fvo.setDOC_ID(SN);
					fvo.setDOC_NAME(inputVO.getRealTempName());
					fvo.setSUBSYSTEM_TYPE("KYC");
					fvo.setDOC_TYPE("01");
					getDataAccessManager().create(fvo);
					
					TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
					dvo.setDOC_ID(SN);
					dvo.setDOC_VERSION_STATUS("2");
					dvo.setDOC_FILE_TYPE("D");
					dvo.setDOC_FILE(ObjectUtil.byteArrToBlob(data));
					getDataAccessManager().create(dvo);
				}
			}

			if(inputVO.getDEL_ANSWER_DESC().size()>0){
				for(Object answer_seq:inputVO.getDEL_ANSWER_DESC()){
					TBSYS_QST_ANSWERPK tqa_pk = new TBSYS_QST_ANSWERPK();
					tqa_pk.setANSWER_SEQ(new BigDecimal(answer_seq.toString()));
					tqa_pk.setQUESTION_VERSION(inputVO.getQUESTION_VERSION());
					TBSYS_QST_ANSWERVO tqa_vo = new TBSYS_QST_ANSWERVO();
					tqa_vo = (TBSYS_QST_ANSWERVO) getDataAccessManager().findByPKey(TBSYS_QST_ANSWERVO.TABLE_UID, tqa_pk);
					if(tqa_vo != null){
						getDataAccessManager().delete(tqa_vo);
					}else{
						errorMsg = "ehl_01_common_009";
						throw new APException(errorMsg);
					}
				}
				for(Map<String, Object> ans_desc: inputVO.getANSWER_DESC()){
					if(ans_desc.get("ANS_DESC")==null){
						TBSYS_QST_ANSWERPK tqa_pk = new TBSYS_QST_ANSWERPK();
						tqa_pk.setANSWER_SEQ(new BigDecimal(ans_desc.get("ANSWER_SEQ").toString()));
						tqa_pk.setQUESTION_VERSION(inputVO.getQUESTION_VERSION());
						TBSYS_QST_ANSWERVO tqa_vo = new TBSYS_QST_ANSWERVO();
						tqa_vo = (TBSYS_QST_ANSWERVO) getDataAccessManager().findByPKey(TBSYS_QST_ANSWERVO.TABLE_UID, tqa_pk);
						if(tqa_vo != null){
							getDataAccessManager().delete(tqa_vo);
						}else{
							errorMsg = "ehl_01_common_009";
							throw new APException(errorMsg);
						}
					}
				}
				for(Map<String, Object> ans_desc: inputVO.getANSWER_DESC()){
					TBSYS_QST_ANSWERPK tqa_pk = new TBSYS_QST_ANSWERPK();
					TBSYS_QST_ANSWERVO tqa_vo = new TBSYS_QST_ANSWERVO();
					Object answer_seq = ans_desc.get("Display_order");
					tqa_pk = new TBSYS_QST_ANSWERPK();
					tqa_pk.setANSWER_SEQ(new BigDecimal(answer_seq.toString()));
					tqa_pk.setQUESTION_VERSION(inputVO.getQUESTION_VERSION());
					tqa_vo = new TBSYS_QST_ANSWERVO();
					tqa_vo.setcomp_id(tqa_pk);
					if(ans_desc.get("ANS_DESC") != null){
						tqa_vo.setANSWER_DESC(ObjectUtils.toString(ans_desc.get("ANS_DESC")));
					}else{
						tqa_vo.setANSWER_DESC(ObjectUtils.toString(ans_desc.get("ANSWER_DESC")));
					}
					if(ans_desc.get("ANS_DESC_ENG") != null){
						tqa_vo.setANSWER_DESC_ENG(ObjectUtils.toString(ans_desc.get("ANS_DESC_ENG")));
					}else{
						tqa_vo.setANSWER_DESC_ENG(ObjectUtils.toString(ans_desc.get("ANSWER_DESC_ENG")));
					}
					getDataAccessManager().create(tqa_vo);
				}
			}else{
				for(Map<String, Object> ans_desc: inputVO.getANSWER_DESC()){
					if(ans_desc.get("ANS_DESC")!=null){
						Object answer_seq = ans_desc.get("Display_order");
						TBSYS_QST_ANSWERPK tqa_pk = new TBSYS_QST_ANSWERPK();
						tqa_pk.setANSWER_SEQ(new BigDecimal(answer_seq.toString()));
						tqa_pk.setQUESTION_VERSION(inputVO.getQUESTION_VERSION());
						TBSYS_QST_ANSWERVO tqa_vo = new TBSYS_QST_ANSWERVO();
						tqa_vo.setcomp_id(tqa_pk);
						tqa_vo.setANSWER_DESC(ObjectUtils.toString(ans_desc.get("ANS_DESC")));
						tqa_vo.setANSWER_DESC_ENG(ObjectUtils.toString(ans_desc.get("ANS_DESC_ENG")));
						getDataAccessManager().create(tqa_vo);
					}
				}
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
	

}
