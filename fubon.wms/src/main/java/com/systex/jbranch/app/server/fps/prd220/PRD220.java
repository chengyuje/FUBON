package com.systex.jbranch.app.server.fps.prd220;

import java.io.File;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSYS_FILE_DETAILVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_FILE_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_PRD_LINKPK;
import com.systex.jbranch.app.common.fps.table.TBSYS_PRD_LINKVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_PRD_SHARED_LINKPK;
import com.systex.jbranch.app.common.fps.table.TBSYS_PRD_SHARED_LINKVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * prd220
 * 
 * @author moron
 * @date 2016/08/24
 * @spec null
 */
@Component("prd220")
@Scope("request")
public class PRD220 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD220.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		PRD220InputVO inputVO = (PRD220InputVO) body;
		PRD220OutputVO return_VO = new PRD220OutputVO();
		dam = this.getDataAccessManager();
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// TBSYS_PRD_LINK
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select a.PRD_ID,b.DOC_ID,b.DOC_NAME,b.DOC_TYPE,c.DOC_FILE_TYPE,c.DOC_URL,c.DOC_START_DATE,c.DOC_DUE_DATE,c.FILE_NAME,c.CREATETIME,'N' as SHARED from ");
		sql.append("(select DOC_ID,PRD_ID from TBSYS_PRD_LINK where PTYPE = :ptype ");
		queryCondition.setObject("ptype", inputVO.getPtype());
		if (!StringUtils.isBlank(inputVO.getPrd_id())) {
			sql.append("and PRD_ID LIKE :prd_id ");
			queryCondition.setObject("prd_id", inputVO.getPrd_id()+"%");
		}
		sql.append(") a ");
		sql.append("inner join (select DOC_ID,DOC_NAME,DOC_TYPE from TBSYS_FILE_MAIN where SUBSYSTEM_TYPE = 'PRD' and DOC_TYPE = '02' ");
		sql.append(") b on a.DOC_ID = b.DOC_ID ");
//		sql.append("inner join (select DOC_ID,DOC_FILE_TYPE,DOC_URL,DOC_START_DATE,DOC_DUE_DATE,FILE_NAME,CREATETIME from TBSYS_FILE_DETAIL where DOC_VERSION_STATUS = '2' and TRUNC(sysdate) BETWEEN DOC_START_DATE and DOC_DUE_DATE) c on b.DOC_ID = c.DOC_ID ");
		sql.append("inner join (select DOC_ID,DOC_FILE_TYPE,DOC_URL,DOC_START_DATE,DOC_DUE_DATE,FILE_NAME,CREATETIME from TBSYS_FILE_DETAIL where DOC_VERSION_STATUS = '2' ) c on b.DOC_ID = c.DOC_ID ");
		queryCondition.setQueryString(sql.toString());
		list.addAll(dam.exeQuery(queryCondition));
		
		// TBSYS_PRD_SHARED_LINK
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("select b.DOC_ID,b.DOC_NAME,b.DOC_TYPE,c.DOC_FILE_TYPE,c.DOC_URL,c.DOC_START_DATE,c.DOC_DUE_DATE,c.FILE_NAME,c.CREATETIME,'Y' as SHARED from ");
		sql.append("(select DOC_ID from TBSYS_PRD_SHARED_LINK where PTYPE = :ptype) a ");
		queryCondition.setObject("ptype", inputVO.getPtype());
		sql.append("inner join (select DOC_ID,DOC_NAME,DOC_TYPE from TBSYS_FILE_MAIN where SUBSYSTEM_TYPE = 'PRD' and DOC_TYPE = '02' ");
		sql.append(") b on a.DOC_ID = b.DOC_ID ");
		sql.append("inner join (select DOC_ID,DOC_FILE_TYPE,DOC_URL,DOC_START_DATE,DOC_DUE_DATE,FILE_NAME,CREATETIME from TBSYS_FILE_DETAIL where DOC_VERSION_STATUS = '2' and TRUNC(sysdate) BETWEEN DOC_START_DATE and DOC_DUE_DATE) c on b.DOC_ID = c.DOC_ID ");
		queryCondition.setQueryString(sql.toString());
		list.addAll(dam.exeQuery(queryCondition));
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void checkID(Object body, IPrimitiveMap header) throws JBranchException {
		PRD220InputVO inputVO = (PRD220InputVO) body;
		PRD220OutputVO return_VO = new PRD220OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		// by ptype
		switch(inputVO.getPtype()) {
			case "MFD":
				sql.append("SELECT PRD_ID,FUND_CNAME_A FROM TBPRD_FUND where PRD_ID = :id ");
				break;
			case "ETF":
				sql.append("SELECT PRD_ID,ETF_CNAME FROM TBPRD_ETF where PRD_ID = :id ");
				break;
			case "STOCK":
				sql.append("SELECT PRD_ID,STOCK_CNAME FROM TBPRD_STOCK where PRD_ID = :id ");
				break;
			case "BND":
				sql.append("SELECT PRD_ID,BOND_CNAME_A FROM TBPRD_BOND where PRD_ID = :id ");
				break;
			case "SI":
				sql.append("SELECT PRD_ID,SI_CNAME FROM TBPRD_SI where PRD_ID = :id ");
				break;
			case "SN":
				sql.append("SELECT PRD_ID,SN_CNAME_A FROM TBPRD_SN where PRD_ID = :id ");
				break;
			case "INS":
				sql.append("SELECT PRD_ID,INSPRD_NAME FROM TBPRD_INS where PRD_ID = :id ");
				break;
		}
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			String name = "";
			switch(inputVO.getPtype()) {
				case "MFD":
					name = ObjectUtils.toString(list.get(0).get("FUND_CNAME_A"));
					break;
				case "ETF":
					name = ObjectUtils.toString(list.get(0).get("ETF_CNAME"));
					break;
				case "STOCK":
					name = ObjectUtils.toString(list.get(0).get("STOCK_CNAME"));
					break;
				case "BND":
					name = ObjectUtils.toString(list.get(0).get("BOND_CNAME_A"));
					break;
				case "SI":
					name = ObjectUtils.toString(list.get(0).get("SI_CNAME"));
					break;
				case "SN":
					name = ObjectUtils.toString(list.get(0).get("SN_CNAME_A"));
					break;
				case "INS":
					name = ObjectUtils.toString(list.get(0).get("INSPRD_NAME"));
					break;
			}
			return_VO.setName(name);
			return_VO.setCanEdit(true);
		}
		else
			return_VO.setCanEdit(false);
		this.sendRtnObject(return_VO);
	}
	
	public void getDOC_ID(Object body, IPrimitiveMap header) throws JBranchException {
		PRD220OutputVO return_VO = new PRD220OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBSYS_FILE_MAIN.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		return_VO.setDoc_id(ObjectUtils.toString(SEQLIST.get(0).get("SEQ")));
		
		this.sendRtnObject(return_VO);
	}
	
	public void saveData(Object body, IPrimitiveMap header) throws Exception {
		PRD220InputVO inputVO = (PRD220InputVO) body;
		dam = this.getDataAccessManager();
		
		if("N".equals(inputVO.getShared())) {
			// check again
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			switch(inputVO.getPtype()) {
				case "MFD":
					sql.append("SELECT PRD_ID FROM TBPRD_FUND where PRD_ID = :id ");
					break;
				case "ETF":
					sql.append("SELECT PRD_ID FROM TBPRD_ETF where PRD_ID = :id ");
					break;
				case "STOCK":
					sql.append("SELECT PRD_ID,STOCK_CNAME FROM TBPRD_STOCK where PRD_ID = :id ");
					break;
				case "BND":
					sql.append("SELECT PRD_ID FROM TBPRD_BOND where PRD_ID = :id ");
					break;
				case "SI":
					sql.append("SELECT PRD_ID FROM TBPRD_SI where PRD_ID = :id ");
					break;
				case "SN":
					sql.append("SELECT PRD_ID FROM TBPRD_SN where PRD_ID = :id ");
					break;
				case "INS":
					sql.append("SELECT PRD_ID FROM TBPRD_INS where PRD_ID = :id ");
					break;
			}
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() == 0)
				throw new APException("ehl_01_common_026");
		}
		
		// byte data
		if("1".equals(inputVO.getType()) && StringUtils.isNotBlank(inputVO.getFileName())) {
			String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			byte[] data = Files.readAllBytes(new File(tempPath, inputVO.getFileName()).toPath());
			
			// TBSYS_FILE_MAIN
			TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
			fvo.setDOC_ID(inputVO.getDoc_id());
			fvo.setDOC_NAME(inputVO.getDoc_name() == null ? "FILE" : inputVO.getDoc_name());
			fvo.setSUBSYSTEM_TYPE("PRD");
			fvo.setDOC_TYPE(inputVO.getDoc_type());
			dam.create(fvo);
			TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
			dvo.setDOC_ID(inputVO.getDoc_id());
			if(inputVO.getDoc_sDate() != null)
				dvo.setDOC_START_DATE(new Timestamp(inputVO.getDoc_sDate().getTime()));
			if(inputVO.getDoc_eDate() != null)
				dvo.setDOC_DUE_DATE(new Timestamp(inputVO.getDoc_eDate().getTime()));
			dvo.setDOC_VERSION_STATUS("2");
			dvo.setFILE_NAME(inputVO.getRealfileName());
			dvo.setDOC_FILE_TYPE("D");
			dvo.setDOC_FILE(ObjectUtil.byteArrToBlob(data));
			dam.create(dvo);
		}
		if("2".equals(inputVO.getType()) && StringUtils.isNotBlank(inputVO.getWeb())) {
			// TBSYS_FILE_MAIN
			TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
			fvo.setDOC_ID(inputVO.getDoc_id());
			fvo.setDOC_NAME(inputVO.getDoc_name() == null ? "URL" : inputVO.getDoc_name());
			fvo.setSUBSYSTEM_TYPE("PRD");
			fvo.setDOC_TYPE(inputVO.getDoc_type());
			dam.create(fvo);
			TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
			dvo.setDOC_ID(inputVO.getDoc_id());
			if(inputVO.getDoc_sDate() != null)
				dvo.setDOC_START_DATE(new Timestamp(inputVO.getDoc_sDate().getTime()));
			if(inputVO.getDoc_eDate() != null)
				dvo.setDOC_DUE_DATE(new Timestamp(inputVO.getDoc_eDate().getTime()));
			dvo.setDOC_VERSION_STATUS("2");
			dvo.setDOC_FILE_TYPE("U");
			if(inputVO.getWeb().indexOf("http://") == -1 && inputVO.getWeb().indexOf("https://") == -1)
				dvo.setDOC_URL("http://"+inputVO.getWeb());
			else
				dvo.setDOC_URL(inputVO.getWeb());
			dam.create(dvo);
		}
		
		// TBSYS_PRD_SHARED_LINK
		if("Y".equals(inputVO.getShared())) {
			TBSYS_PRD_SHARED_LINKVO vo = new TBSYS_PRD_SHARED_LINKVO();
			TBSYS_PRD_SHARED_LINKPK pk = new TBSYS_PRD_SHARED_LINKPK();
			pk.setDOC_ID(inputVO.getDoc_id());
			pk.setPTYPE(inputVO.getPtype());
			vo.setcomp_id(pk);
			dam.create(vo);
		}
		// TBSYS_PRD_LINK
		else {
			TBSYS_PRD_LINKVO vo = new TBSYS_PRD_LINKVO();
			TBSYS_PRD_LINKPK pk = new TBSYS_PRD_LINKPK();
			pk.setDOC_ID(inputVO.getDoc_id());
			pk.setPRD_ID(inputVO.getPrd_id());
			pk.setPTYPE(inputVO.getPtype());
			vo.setcomp_id(pk);
			dam.create(vo);
		}
		this.sendRtnObject(null);
	}
	
	public void editData(Object body, IPrimitiveMap header) throws Exception {
		PRD220InputVO inputVO = (PRD220InputVO) body;
		dam = this.getDataAccessManager();
		
		// byte data
		// update TBSYS_FILE_MAIN
		TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
		fvo = (TBSYS_FILE_MAINVO) dam.findByPKey(TBSYS_FILE_MAINVO.TABLE_UID, inputVO.getDoc_id());
		if (fvo != null) {
			fvo.setDOC_NAME(inputVO.getDoc_name());
			dam.update(fvo);
		} else
			throw new APException("ehl_01_common_001");
		// update TBSYS_FILE_MAIN
		TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
		dvo = (TBSYS_FILE_DETAILVO) dam.findByPKey(TBSYS_FILE_DETAILVO.TABLE_UID, inputVO.getDoc_id());
		if (dvo != null) {
			if(inputVO.getDoc_sDate() != null)
				dvo.setDOC_START_DATE(new Timestamp(inputVO.getDoc_sDate().getTime()));
			if(inputVO.getDoc_eDate() != null)
				dvo.setDOC_DUE_DATE(new Timestamp(inputVO.getDoc_eDate().getTime()));
			dam.update(dvo);
		} else
			throw new APException("ehl_01_common_001");
		// have real but don't have name => ori file
		if("1".equals(inputVO.getType()) && StringUtils.isNotBlank(inputVO.getRealfileName()) && StringUtils.isBlank(inputVO.getFileName())) {
			
		}
		// upload
		else if("1".equals(inputVO.getType()) && StringUtils.isNotBlank(inputVO.getFileName())) {
			String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			byte[] data = Files.readAllBytes(new File(tempPath, inputVO.getFileName()).toPath());
			
			// TBSYS_FILE_DETAILVO
			dvo.setFILE_NAME(inputVO.getRealfileName());
			dvo.setDOC_FILE_TYPE("D");
			dvo.setDOC_FILE(ObjectUtil.byteArrToBlob(data));
			dvo.setDOC_URL(null);
			dam.update(dvo);
		}
		if("2".equals(inputVO.getType()) && StringUtils.isNotBlank(inputVO.getWeb())) {
			// TBSYS_FILE_DETAILVO
			dvo.setFILE_NAME(null);
			dvo.setDOC_FILE_TYPE("U");
			dvo.setDOC_FILE(null);
			dvo.setDOC_URL(inputVO.getWeb());
			dam.update(dvo);
		}
		this.sendRtnObject(null);
	}
	
	public void deleteData(Object body, IPrimitiveMap header) throws Exception {
		PRD220InputVO inputVO = (PRD220InputVO) body;
		dam = this.getDataAccessManager();
		
		// TBSYS_PRD_SHARED_LINK
		if("Y".equals(inputVO.getShared())) {
			TBSYS_PRD_SHARED_LINKVO vo = new TBSYS_PRD_SHARED_LINKVO();
			TBSYS_PRD_SHARED_LINKPK pk = new TBSYS_PRD_SHARED_LINKPK();
			pk.setDOC_ID(inputVO.getDoc_id());
			pk.setPTYPE(inputVO.getPtype());
			vo = (TBSYS_PRD_SHARED_LINKVO) dam.findByPKey(TBSYS_PRD_SHARED_LINKVO.TABLE_UID, pk);
			if (vo != null)
				dam.delete(vo);
			else
				throw new APException("ehl_01_common_001");
		}
		// TBSYS_PRD_LINK
		else {
			TBSYS_PRD_LINKVO vo = new TBSYS_PRD_LINKVO();
			TBSYS_PRD_LINKPK pk = new TBSYS_PRD_LINKPK();
			pk.setDOC_ID(inputVO.getDoc_id());
			pk.setPTYPE(inputVO.getPtype());
			pk.setPRD_ID(inputVO.getPrd_id());
			vo = (TBSYS_PRD_LINKVO) dam.findByPKey(TBSYS_PRD_LINKVO.TABLE_UID, pk);
			if (vo != null)
				dam.delete(vo);
			else
				throw new APException("ehl_01_common_001");
		}
		
		// TBSYS_FILE_MAIN
		TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
		fvo = (TBSYS_FILE_MAINVO) dam.findByPKey(TBSYS_FILE_MAINVO.TABLE_UID, inputVO.getDoc_id());
		if (fvo != null)
			dam.delete(fvo);
		else
			throw new APException("ehl_01_common_001");
		// TBSYS_FILE_DETAIL
		TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
		dvo = (TBSYS_FILE_DETAILVO) dam.findByPKey(TBSYS_FILE_DETAILVO.TABLE_UID, inputVO.getDoc_id());
		if (dvo != null)
			dam.delete(dvo);
		else
			throw new APException("ehl_01_common_001");
		
		this.sendRtnObject(null);
	}
	
	
	
}