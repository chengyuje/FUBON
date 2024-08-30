package com.systex.jbranch.app.server.fps.prd172;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPRD_INS_DOCCHKVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * PRD172
 * 
 * @author Kevin Hsu
 * @date 2016/09/06
 * @spec null
 */

@Component("prd172")
@Scope("request")
public class PRD172 extends FubonWmsBizLogic {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	DataAccessManager dam_obj = null;

	public void queryData(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		PRD172InputVO inputVO = (PRD172InputVO) body;
		PRD172OutputVO outputVO = new PRD172OutputVO();
		List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" Select ");
			sb.append("        KEY_NO, REG_TYPE, DOC_NAME, OTH_TYPE, DOC_SEQ, ");
			sb.append("        DOC_TYPE, DOC_LEVEL, SIGN_INC, CREATETIME, CREATOR, ");
			sb.append("        LASTUPDATE, MODIFIER, APPROVER, APP_DATE ");
			sb.append("   From VWPRD_INS_DOCCHK ");
			sb.append("  Where REG_TYPE in ('3','4') ");

//			if (!StringUtils.isBlank(inputVO.getREG_TYPE())) {
			sb.append(" and REG_TYPE = :REG_TYPEE ");
			qc.setObject("REG_TYPEE", inputVO.getREG_TYPE());
//			}

			if (!StringUtils.isBlank(inputVO.getOTH_TYPE())) {
				sb.append(" and OTH_TYPE = :OTH_TYPEE ");
				qc.setObject("OTH_TYPEE", inputVO.getOTH_TYPE());
			}
			
			
			qc.setQueryString(sb.toString());
			// tempList = dam_obj.exeQuery(qc);

				

			ResultIF list = dam_obj.executePaging(qc,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord();

			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數

			outputVO.setINS_ANCDOCList(list);
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

	public void addData(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		Timestamp currentTM = new Timestamp(System.currentTimeMillis());

		PRD172EDITInputVO inputVO = (PRD172EDITInputVO) body;
		try {
			dam_obj = this.getDataAccessManager();
			TBPRD_INS_DOCCHKVO vo = new TBPRD_INS_DOCCHKVO();
				if(inputVO.getDOC_LEVEL() != null){
					if(!"".equals(inputVO.getDOC_LEVEL())){
						vo.setDOC_LEVEL(inputVO.getDOC_LEVEL());
					}else{
						vo.setDOC_LEVEL("2");
					}
				}else{
					vo.setDOC_LEVEL("2");
				}
				vo.setDOC_NAME(inputVO.getDOC_NAME());
				vo.setDOC_SEQ(new BigDecimal(inputVO.getDOC_SEQ().toString()));
				vo.setDOC_TYPE(inputVO.getDOC_TYPE());
				if(inputVO.getSIGN_INC() != null){
					if(!"".equals(inputVO.getSIGN_INC())){
						vo.setSIGN_INC(inputVO.getSIGN_INC());
					}else{
						vo.setSIGN_INC("N");
					}
				}else{
					vo.setSIGN_INC("N");
				}
				vo.setKEY_NO(new BigDecimal(getSEQ()));
				vo.setREG_TYPE(inputVO.getREG_TYPE());
				vo.setOTH_TYPE(inputVO.getOTH_TYPE());
//				vo.setAPP_DATE(currentTM);
//				vo.setAPPROVER("1");
				dam_obj.create(vo);
				sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

	public void updateData(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		PRD172EDITInputVO inputVO = (PRD172EDITInputVO) body;
		try {
			dam_obj = this.getDataAccessManager();

			TBPRD_INS_DOCCHKVO vo = (TBPRD_INS_DOCCHKVO) dam_obj.findByPKey(
					TBPRD_INS_DOCCHKVO.TABLE_UID,
					new BigDecimal(inputVO.getSEQ()));
			if (vo != null) {
				vo.setDOC_LEVEL(inputVO.getDOC_LEVEL());
				vo.setDOC_NAME(inputVO.getDOC_NAME());
				vo.setDOC_SEQ(new BigDecimal(inputVO.getDOC_SEQ().toString()));
				vo.setDOC_TYPE(inputVO.getDOC_TYPE());
				vo.setSIGN_INC(inputVO.getSIGN_INC());
				vo.setOTH_TYPE(inputVO.getOTH_TYPE());
				dam_obj.update(vo);
				sendRtnObject(null);
			} else {
				throw new APException("ehl_01_common_007");
			}

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

	public void deleteData(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		PRD172EDITInputVO inputVO = (PRD172EDITInputVO) body;
		try {
			dam_obj = this.getDataAccessManager();
			TBPRD_INS_DOCCHKVO vo = (TBPRD_INS_DOCCHKVO) dam_obj.findByPKey(
					TBPRD_INS_DOCCHKVO.TABLE_UID,
					new BigDecimal(inputVO.getSEQ()));
			if (vo != null) {
				dam_obj.delete(vo);
				sendRtnObject(null);
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

	/** 產生seq No */
	private String getSEQ() throws JBranchException {
		SerialNumberUtil seq = new SerialNumberUtil();
		String seqNum = "";
		try {
			seqNum = seq.getNextSerialNumber("PRD172");
		} catch (Exception e) {
			seq.createNewSerial("PRD172", "0000000000", null, null, null, 6,
					new Long("99999999"), "y", new Long("900"), null);
			seqNum = seq.getNextSerialNumber("PRD172");
		}
		return seqNum;
	}

}
