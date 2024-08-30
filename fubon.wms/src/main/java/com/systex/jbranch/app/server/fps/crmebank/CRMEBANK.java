package com.systex.jbranch.app.server.fps.crmebank;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPRD_INS_MAINVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERPK;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERVO;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("crmebank")
@Scope("request")
public class CRMEBANK extends FubonWmsBizLogic{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	DataAccessManager dam_obj = null;
	
	public void query(Object body, IPrimitiveMap header) throws JBranchException {
		CRMEBANKOutputVO outputVO = new CRMEBANKOutputVO();
		try {
			dam_obj = this.getDataAccessManager();
			QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" select * from TBSYSPARAMETER where PARAM_TYPE='SYS.EBANK_GREET' ");
			qc.setQueryString(sb.toString());
			outputVO.setWelcomeList(dam_obj.exeQuery(qc));
			
			sendRtnObject(outputVO);
			
		} catch (JBranchException e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");		
		}
	}
	
	public void insert(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		CRMEBANKInputVO inputVO = (CRMEBANKInputVO) body;
		CRMEBANKOutputVO returnVO = new CRMEBANKOutputVO();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF condition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		Timestamp time = new  Timestamp(sdf.parse(sdf.format(date)).getTime());
		String errorMsg = "";
		
		TBSYSPARAMETERPK pk = new TBSYSPARAMETERPK();
		pk.setPARAM_TYPE("SYS.EBANK_GREET");
		pk.setPARAM_CODE(inputVO.getPARAM_CODE());
		TBSYSPARAMETERVO vo = (TBSYSPARAMETERVO) dam_obj.findByPKey(TBSYSPARAMETERVO.TABLE_UID, pk);
		
		if(vo == null){
			StringBuilder sql = new StringBuilder();
			sql.append(" INSERT INTO TBSYSPARAMETER(PARAM_TYPE, PARAM_CODE, PARAM_ORDER, PARAM_NAME, PARAM_NAME_EDIT, PARAM_DESC, PARAM_STATUS, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE)");
			sql.append(" VALUES( :PARAM_TYPE , :PARAM_CODE , :PARAM_ORDER , :PARAM_NAME , :PARAM_NAME_EDIT , :PARAM_DESC , :PARAM_STATUS , :VERSION , :CREATETIME , :CREATOR , :MODIFIER , :LASTUPDATE ) ");
			condition.setObject("PARAM_TYPE" , "SYS.EBANK_GREET");
			condition.setObject("PARAM_CODE" , inputVO.getPARAM_CODE());
			condition.setObject("PARAM_ORDER", "0" );
			condition.setObject("PARAM_NAME" , inputVO.getPARAM_NAME());
			condition.setObject("PARAM_NAME_EDIT", inputVO.getPARAM_NAME());
			condition.setObject("PARAM_DESC" , inputVO.getPARAM_DESC());
			condition.setObject("PARAM_STATUS","0" );
			condition.setObject("VERSION", "0");
			condition.setObject("CREATETIME", time);
			condition.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			condition.setObject("LASTUPDATE", time);
			condition.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			condition.setQueryString(sql.toString());
			
			dam_obj.exeUpdate(condition);
			this.sendRtnObject(returnVO);
		}else{
			throw new APException("類型重複，請重新輸入");
		}
		
	}

	public void updata(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		dam_obj = this.getDataAccessManager();
		CRMEBANKInputVO inputVO = (CRMEBANKInputVO) body;
		CRMEBANKOutputVO returnVO = new CRMEBANKOutputVO();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		Timestamp time = new  Timestamp(sdf.parse(sdf.format(date)).getTime());
		
		TBSYSPARAMETERPK pk = new TBSYSPARAMETERPK();
		pk.setPARAM_TYPE("SYS.EBANK_GREET");
		pk.setPARAM_CODE(inputVO.getPARAM_CODE());
		TBSYSPARAMETERVO vo = (TBSYSPARAMETERVO) dam_obj.findByPKey(TBSYSPARAMETERVO.TABLE_UID, pk);
		
		vo.setPARAM_DESC(inputVO.getPARAM_DESC()); // 備註
		vo.setPARAM_NAME(inputVO.getPARAM_NAME()); // 歡迎詞
		vo.setPARAM_NAME_EDIT(inputVO.getPARAM_NAME()); // 歡迎詞
		vo.setLastupdate(time); // 修改時間
		vo.setModifier( (String) getUserVariable(FubonSystemVariableConsts.LOGINID) ); // 修改人
		
		dam_obj.update(vo);
		sendRtnObject(null);
	}

	public void delete(Object body, IPrimitiveMap header) throws JBranchException {
		dam_obj = this.getDataAccessManager();
		CRMEBANKInputVO inputVO = (CRMEBANKInputVO) body;
		CRMEBANKOutputVO returnVO = new CRMEBANKOutputVO();
		
		TBSYSPARAMETERPK pk = new TBSYSPARAMETERPK();
		pk.setPARAM_TYPE("SYS.EBANK_GREET");
		pk.setPARAM_CODE(inputVO.getPARAM_CODE());
		TBSYSPARAMETERVO vo = (TBSYSPARAMETERVO) dam_obj.findByPKey(TBSYSPARAMETERVO.TABLE_UID, pk);
		
		dam_obj.delete(vo);
		sendRtnObject(null);
	}
}
