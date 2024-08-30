package com.systex.jbranch.app.server.fps.prd173;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPRD_FX_DISCOUNT_PRODVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;




/**
 * PRD173
 * 
 * @author Jimmy
 * @date 2016/08/31
 * @spec null
 */


@Component("prd173")
@Scope("request")
public class PRD173 extends FubonWmsBizLogic{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	DataAccessManager dam_obj = null;
	
	public void queryData(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		PRD173OutputVO outputVO = new PRD173OutputVO();
		List<Map<String, Object>> tempList = new ArrayList<Map<String,Object>>();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" select FXD_KEYNO,PROD_NAME,PROD_PERIOD, ");
			sb.append(" to_char(EFFECT_DATE,'YYYY/MM/DD') as EFFECT_DATE, ");
			sb.append(" to_char(EXPIRY_DATE,'YYYY/MM/DD') as EXPIRY_DATE,APPROVER, ");
			sb.append(" to_char(APP_DATE,'YYYY/MM/DD') as APP_DATE, ");
			sb.append(" to_char(CREATETIME,'YYYY/MM/DD') as CREATETIME,CREATOR, ");
			sb.append(" to_char(LASTUPDATE,'YYYY/MM/DD') as LASTUPDATE,MODIFIER ");
			sb.append(" FROM TBPRD_FX_DISCOUNT_PROD ");
			qc.setQueryString(sb.toString());
			tempList = dam_obj.exeQuery(qc);
			for(int a = 0; a<tempList.size(); a++){
				String effect_date = tempList.get(a).get("EFFECT_DATE").toString();
				String expiry_date = tempList.get(a).get("EXPIRY_DATE").toString();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				Date dt2 = sdf.parse(effect_date);
				Date dt3 = sdf.parse(expiry_date);
				long tr_effect_date=dt2.getTime();
				long tr_expiry_date = dt3.getTime();
				tempList.get(a).put("EFFECT_DATE_CHANGE", tr_effect_date);
				tempList.get(a).put("EXPIRY_DATE_CHANGE", tr_expiry_date);
			}
			outputVO.setFXD_DicountList(tempList);
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	
	public void addData(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		PRD173EDITInputVO inputVO = (PRD173EDITInputVO) body;
		try {
			List<Map<String, Object>> nextvalList = new ArrayList<Map<String,Object>>();
			dam_obj = this.getDataAccessManager();
			QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" select TBPRD_INS_SEQ.nextval from dual ");
			qc.setQueryString(sb.toString());
			nextvalList = dam_obj.exeQuery(qc);
			String fxd_keyno = nextvalList.get(0).get("NEXTVAL").toString();
			BigDecimal fxd_keyno_change = new BigDecimal(fxd_keyno);
			dam_obj = this.getDataAccessManager();
			TBPRD_FX_DISCOUNT_PRODVO fxvo = new TBPRD_FX_DISCOUNT_PRODVO();
			fxvo.setFXD_KEYNO(fxd_keyno_change);
			fxvo.setPROD_NAME(inputVO.getPROD_NAME());
			if(inputVO.getPROD_PERIOD() != null){
				fxvo.setPROD_PERIOD(inputVO.getPROD_PERIOD());
			}
			Timestamp effect_date = new Timestamp(inputVO.getEFFECT_DATE().getTime());
			fxvo.setEFFECT_DATE(effect_date);
			Timestamp expiry_date = new Timestamp(inputVO.getEXPIRY_DATE().getTime());
			fxvo.setEXPIRY_DATE(expiry_date);
			dam_obj.create(fxvo);
			sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	public void updateData(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		PRD173EDITInputVO inputVO = (PRD173EDITInputVO) body;
		try {
			dam_obj = this.getDataAccessManager();
			TBPRD_FX_DISCOUNT_PRODVO fxvo = new TBPRD_FX_DISCOUNT_PRODVO();
			BigDecimal fxd_keyno_change = new BigDecimal(inputVO.getFXD_KEYNO());
			fxvo = (TBPRD_FX_DISCOUNT_PRODVO) dam_obj.findByPKey(TBPRD_FX_DISCOUNT_PRODVO.TABLE_UID, fxd_keyno_change);
			fxvo.setPROD_NAME(inputVO.getPROD_NAME());
			if(inputVO.getPROD_PERIOD() != null){
				fxvo.setPROD_PERIOD(inputVO.getPROD_PERIOD());
			}
			Timestamp effect_date = new Timestamp(inputVO.getEFFECT_DATE().getTime());
			fxvo.setEFFECT_DATE(effect_date);
			Timestamp expiry_date = new Timestamp(inputVO.getEXPIRY_DATE().getTime());
			fxvo.setEXPIRY_DATE(expiry_date);
			dam_obj.update(fxvo);
			sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	public void deleteData(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		PRD173InputVO inputVO = (PRD173InputVO) body;
		try {
			dam_obj = this.getDataAccessManager();
			TBPRD_FX_DISCOUNT_PRODVO fxvo = new TBPRD_FX_DISCOUNT_PRODVO();
			BigDecimal fxd_keyno_change = new BigDecimal(inputVO.getFXD_KEYNO());
			fxvo = (TBPRD_FX_DISCOUNT_PRODVO) dam_obj.findByPKey(TBPRD_FX_DISCOUNT_PRODVO.TABLE_UID, fxd_keyno_change);
			if(fxvo != null){
				dam_obj.delete(fxvo);
				sendRtnObject(null);
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}


}
