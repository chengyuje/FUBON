package com.systex.jbranch.app.server.fps.prd181;

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

import com.systex.jbranch.app.common.fps.table.TBPRD_FX_DISCOUNT_PRODVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_INS_PPT_MAINVO;
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
 * PRD181
 * 
 * @author Kevin Hsu
 * @date 2016/09/20
 * @spec null
 */

@Component("prd181")
@Scope("request")
public class PRD181 extends FubonWmsBizLogic {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	DataAccessManager dam = null;

	public void queryData(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		PRD181OutputVO outputVO = new PRD181OutputVO();
		PRD181InputVO inputVO = (PRD181InputVO) body;

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" select * from TBPRD_INS_PPT_MAIN where 1=1 ");
	
			queryCondition.setQueryString(sb.toString());
			ResultIF list = dam.executePaging(queryCondition,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord();

			outputVO.setDicountList(list);
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	public void addData(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		PRD181InputVO inputVO = (PRD181InputVO) body;
		try {
			List<Map<String, Object>> nextvalList = new ArrayList<Map<String, Object>>();
			
			dam = this.getDataAccessManager();
			TBPRD_INS_PPT_MAINVO vo=new TBPRD_INS_PPT_MAINVO();
			vo.setCERT_03(inputVO.getCERT_03());
			vo.setCERT_05(inputVO.getCERT_05());
			vo.setINSPRD_ID(inputVO.getINSPRD_ID());
			vo.setINSPRD_KEYNO(new BigDecimal(getTBPRD_INS_PPT_MAIN_SEQ()));
			vo.setINSPRD_NAME(inputVO.getINSPRD_NAME());
			dam.create(vo);
			sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("ehl_01_prd181_001");
		}
	}

	public void deleteData(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		PRD181InputVO inputVO = (PRD181InputVO) body;
		try {
			dam = this.getDataAccessManager();
			TBPRD_INS_PPT_MAINVO vo = new TBPRD_INS_PPT_MAINVO();
			BigDecimal keyno = new BigDecimal(inputVO.getINSPRD_KEYNO());
			vo = (TBPRD_INS_PPT_MAINVO) dam.findByPKey(
					TBPRD_INS_PPT_MAINVO.TABLE_UID, keyno);
			if (vo != null) {
				dam.delete(vo);
				sendRtnObject(null);
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	
	/** 產生seq No */
	private String getTBPRD_INS_PPT_MAIN_SEQ() throws JBranchException {
		SerialNumberUtil seq = new SerialNumberUtil();
		String seqNum = "";
		try {
			seqNum = seq.getNextSerialNumber("TBPRD_INS_PPT_MAIN");
		} catch (Exception e) {
			seq.createNewSerial("TBPRD_INS_PPT_MAIN", "0000000000", null, null,
					null, 6, new Long("99999999"), "y", new Long("900"), null);
			seqNum = seq.getNextSerialNumber("TBPRD_INS_PPT_MAIN_SEQ");
		}
		return seqNum;
	}
	

}
