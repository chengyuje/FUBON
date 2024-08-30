package com.systex.jbranch.app.server.fps.ins960;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBINS_PARA_HEADERVO;
import com.systex.jbranch.app.common.fps.table.TBINS_REPORTVO;
import com.systex.jbranch.app.common.fps.table.TBINS_SPPEDU_DETAILVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_INS_COMPAREDPK;
import com.systex.jbranch.app.common.fps.table.TBPRD_INS_COMPAREDVO;
import com.systex.jbranch.app.server.fps.ins450.INS450InputVO;
import com.systex.jbranch.app.server.fps.ins450.INS450OutputVO;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("ins960")
@Scope("request")
public class INS960 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(INS960.class);
	
	public void init(Object body, IPrimitiveMap header) throws JBranchException, InterruptedException {
		
		INS960InputVO inputVO = (INS960InputVO) body;
		INS960OutputVO outputVO = new INS960OutputVO();
		dam = this.getDataAccessManager();
	    List<Map<String, Object>> insData = null;
	    List<Map<String, Object>> fubonIns = null;
	    insData = insDataList(dam, inputVO);
	    fubonIns = insList(dam, body);
	    outputVO.setInsDataOutputList(insData);
	    outputVO.setInsOutputList(fubonIns);
		
		this.sendRtnObject(outputVO);
	}
	
	/**
	 * INS960 保險資訊源資料
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException
	 */	
	  @SuppressWarnings("unchecked")
	  private List<Map<String, Object>> insDataList(DataAccessManager dam, INS960InputVO inputVO) throws DAOException, JBranchException {
		  
		QueryConditionIF qc = null;
		StringBuilder sb = new StringBuilder();
	    
	    qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	    
	    sb.append("SELECT KEY_NO,PRD_ID,PRD_NAME,IS_MAIN,TITLE_Y,LIST_Y,TITLE_A,LIST_A,TITLE_O,LIST_O,TITLE_K,LIST_K ");
		sb.append("FROM TBPRD_INSDATA_PROD_MAIN ");
		sb.append("WHERE COM_ID = '209' AND IFCHS = 'T' ");
		
		if (StringUtils.isNotEmpty(inputVO.getPRD_ID())) {
			sb.append(" and PRD_ID like ? ");
			qc.setString(0, inputVO.getPRD_ID() + "%");
		}
		sb.append("ORDER BY PRD_ID ");
	
	    qc.setQueryString(sb.toString());
		return dam.exeQuery(qc);
	  }
	  
	/**
	 * INS960 富邦人壽資料
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException
	 */	
	  @SuppressWarnings("unchecked")
	  private List<Map<String, Object>> insList(DataAccessManager dam, Object body) throws DAOException, JBranchException {
		QueryConditionIF qc = null;
		StringBuilder sb = new StringBuilder();
		
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sb.append("SELECT KEY_NO,PRD_ID,INSPRD_NAME,INSPRD_ANNUAL,GUARANTEE_ANNUAL,PRD_UNIT,SALE_SDATE,SALE_EDATE,MAIN_RIDER,CURR_CD ");
		sb.append("FROM TBPRD_INS A ");
		sb.append("WHERE NOT EXISTS (SELECT * FROM TBPRD_INS_COMPARED B WHERE A.KEY_NO = B.KEY_NO) ");
		sb.append("ORDER BY PRD_ID ");
		
		qc.setQueryString(sb.toString());
		return dam.exeQuery(qc);
	  }
	  
	/**
	 * INS960Mapping
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException
	 */
	public void mappingList (Object body, IPrimitiveMap<Object> header) throws JBranchException {
		INS960InputVO inputVO = (INS960InputVO) body;
		INS960OutputVO outputVO = new INS960OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT A.KEY_NO,A.INSDATA_KEYNO, ");
		sb.append("B.PRD_ID insPrdId,B.INSPRD_NAME,B.INSPRD_ANNUAL,B.GUARANTEE_ANNUAL,B.PRD_UNIT,B.CURR_CD,B.SALE_SDATE,B.SALE_EDATE,B.MAIN_RIDER, ");
		sb.append("C.PRD_ID insDataPrdId,C.PRD_NAME,C.IS_MAIN,C.TITLE_Y,C.TITLE_A,C.TITLE_O,C.TITLE_K,C.LIST_Y,C.LIST_A,C.LIST_O,C.LIST_K,C.CURR_CD insDataCurrCd ");
		sb.append("FROM TBPRD_INS_COMPARED A,TBPRD_INS B,TBPRD_INSDATA_PROD_MAIN C ");
		sb.append("WHERE A.KEY_NO = B.KEY_NO AND A.INSDATA_KEYNO = C.KEY_NO ");
		
		if (StringUtils.isNotEmpty(inputVO.getPRD_ID())) {
			sb.append(" and B.PRD_ID like ? ");
			qc.setString(0, inputVO.getPRD_ID() + "%");
		}
		sb.append("ORDER BY b.PRD_ID ");

		qc.setQueryString(sb.toString());
		outputVO.setOutputList(dam.exeQuery(qc));
		this.sendRtnObject(outputVO);
	}
	
	/**
	 * INS960 save
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException
	 */
	public void save (Object body, IPrimitiveMap<Object> header) throws JBranchException {
		INS960InputVO inputVO = (INS960InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		TBPRD_INS_COMPAREDVO vo = new TBPRD_INS_COMPAREDVO();
		vo.setKEY_NO(new BigDecimal(inputVO.getKEY_NO()));
		vo.setINSDATA_KEYNO(inputVO.getINSDATA_KEYNO());
		this.sendRtnObject(dam.create(vo));
	}
	
	/**
	 * INS960 delete
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException
	 */
	public void delete (Object body, IPrimitiveMap<Object> header) throws JBranchException {
		INS960InputVO inputVO = (INS960InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		
		for(Map<String, Object> map : inputVO.getCheckData()){
			TBPRD_INS_COMPAREDVO vo = (TBPRD_INS_COMPAREDVO) dam
					.findByPKey(TBPRD_INS_COMPAREDVO.TABLE_UID,new BigDecimal(ObjectUtils.toString(map.get("KEY_NO"))));
			dam.delete(vo);
		}
		this.sendRtnObject(null);
	}
	
}