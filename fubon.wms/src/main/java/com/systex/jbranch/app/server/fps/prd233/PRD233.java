package com.systex.jbranch.app.server.fps.prd233;

import java.io.File;
import java.nio.file.Files;
import java.sql.Timestamp;
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

import com.systex.jbranch.app.common.fps.table.TBSYS_FILE_DETAILVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_FILE_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_PRD_LINKPK;
import com.systex.jbranch.app.common.fps.table.TBSYS_PRD_LINKVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * prd233
 * 
 * @author moron
 * @date 2016/08/24
 * @spec null
 */
@Component("prd233")
@Scope("request")
public class PRD233 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD233.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		PRD233InputVO inputVO = (PRD233InputVO) body;
		PRD233OutputVO return_VO = new PRD233OutputVO();
		dam = this.getDataAccessManager();
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// type
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		// 1 and 3
		if(StringUtils.equals("1", inputVO.getType()) || StringUtils.equals("3", inputVO.getType())) {
			sql.append("select F.PRD_ID, F.FUND_CNAME_A, M.DOC_ID, M.DOC_NAME,M.DOC_TYPE,D.DOC_URL, D.FILE_NAME, 'N' as FILE_SHARED, D.DOC_START_DATE, D.DOC_DUE_DATE, D.UPLOAD_DATE, D.DOC_FILE_TYPE, D.CREATETIME,'N' as SHARED from ");
			sql.append("TBPRD_FUND F, TBSYS_PRD_LINK P, TBSYS_FILE_DETAIL D, TBSYS_FILE_MAIN M ");
			sql.append("where F.PRD_ID = P.PRD_ID and P.DOC_ID = M.DOC_ID and P.DOC_ID = D.DOC_ID and P.PTYPE = 'MFD' ");
			if(StringUtils.equals("1", inputVO.getType())) {
				if(StringUtils.isNotBlank(inputVO.getPrd_id())) {
					sql.append("and F.PRD_ID like :id ");
					queryCondition.setObject("id", inputVO.getPrd_id() + "%");
				}
			} else {
				if(StringUtils.isNotBlank(inputVO.getCname())) {
					sql.append("and F.PRD_ID in (select A.PRD_ID from TBPRD_MAIN_FUND_MAPPING A left Join TBPRD_MAIN_FUND B on B.MAIN_PRD_ID = A.MAIN_PRD_ID ");
					sql.append("where B.CNAME_A like :name) ");
					queryCondition.setObject("name", "%" + inputVO.getCname() + "%");
				}
			}
		}
		// 2
		else {
			sql.append("select  F.PRD_ID, F.FUND_CNAME_A, M.DOC_ID, M.DOC_NAME,M.DOC_TYPE,D.DOC_URL, D.FILE_NAME, 'N' as FILE_SHARED, D.DOC_START_DATE, D.DOC_DUE_DATE, D.UPLOAD_DATE, D.DOC_FILE_TYPE, D.CREATETIME,'N' as SHARED from TBPRD_FUND F, TBPRD_FUNDINFO I, TBSYS_PRD_LINK P, TBSYS_FILE_DETAIL D, TBSYS_FILE_MAIN M ");
			sql.append("where F.PRD_ID = I.PRD_ID and I.PRD_ID = P.PRD_ID and P.DOC_ID = M.DOC_ID and P.DOC_ID = D.DOC_ID ");
			sql.append("and P.PTYPE = 'MFD' and I.MAIN_PRD = 'Y' and (TRUNC(sysdate) BETWEEN I.MAIN_PRD_SDATE and I.MAIN_PRD_EDATE) ");
			if(inputVO.getsDate() != null) {
				sql.append("and I.MAIN_PRD_SDATE <= :start ");
				queryCondition.setObject("start", inputVO.getsDate());
			}
			if(inputVO.geteDate() != null) {
				sql.append("and I.MAIN_PRD_EDATE >= :end ");
				queryCondition.setObject("end", inputVO.geteDate());
			}	
		}
		queryCondition.setQueryString(sql.toString());
		list.addAll(dam.exeQuery(queryCondition));
		
		// 加上共用輔銷文件
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("select '' as PRD_ID, '基金共用' as FUND_CNAME, M.DOC_ID, M.DOC_NAME,M.DOC_TYPE,D.DOC_URL, D.FILE_NAME, 'Y' as FILE_SHARED, D.DOC_START_DATE, D.DOC_DUE_DATE, D.UPLOAD_DATE, D.DOC_FILE_TYPE, D.CREATETIME,'Y' as SHARED from ");
		sql.append("TBSYS_FILE_DETAIL D, TBSYS_FILE_MAIN M, TBSYS_PRD_SHARED_LINK S ");
		sql.append("where M.DOC_ID = D.DOC_ID and D.DOC_ID = S.DOC_ID and S.PTYPE = 'MFD' ");
		queryCondition.setQueryString(sql.toString());
		list.addAll(dam.exeQuery(queryCondition));
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void checkID(Object body, IPrimitiveMap header) throws JBranchException {
		PRD233InputVO inputVO = (PRD233InputVO) body;
		PRD233OutputVO return_VO = new PRD233OutputVO();
		dam = this.getDataAccessManager();
		
		List<String> error = new ArrayList<String>();
		List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if(!dataCsv.isEmpty()) {
			for(int i = 0;i < dataCsv.size();i++) {
				if(i == 0) {
					continue;
				}
				String[] str = dataCsv.get(i);
				// check prd_id
				// TBPRD_FUND
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT PRD_ID,FUND_CNAME_A FROM TBPRD_FUND where PRD_ID = :id ");
				queryCondition.setObject("id", str[0]);
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if (list.size() == 0) {
					error.add(str[0]);
					continue;
				} else {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("PRD_ID", str[0]);
					map.put("FUND_CNAME_A", list.get(0).get("FUND_CNAME_A"));
					res.add(map);
				}	
			}
		}
		return_VO.setErrorList(error);
		return_VO.setResultList(res);
		this.sendRtnObject(return_VO);
	}
	
	public void upload(Object body, IPrimitiveMap header) throws Exception {
		PRD233InputVO inputVO = (PRD233InputVO) body;
		dam = this.getDataAccessManager();
		
		for(Map<String, Object> submap : inputVO.getIdList()) {
			// seq
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT SQ_TBSYS_FILE_MAIN.nextval AS SEQ FROM DUAL ");
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
			String doc_id = ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
			
			// byte data
			if("1".equals(inputVO.getType()) && StringUtils.isNotBlank(inputVO.getFileName())) {
				String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				byte[] data = Files.readAllBytes(new File(tempPath, inputVO.getFileName()).toPath());

				// TBSYS_FILE_MAIN
				TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
				fvo.setDOC_ID(doc_id);
				fvo.setDOC_NAME(inputVO.getDoc_name() == null ? "FILE" : inputVO.getDoc_name());
				fvo.setSUBSYSTEM_TYPE("PRD");
				fvo.setDOC_TYPE("02");
				dam.create(fvo);
				// TBSYS_FILE_DETAIL
				TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
				dvo.setDOC_ID(doc_id);
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
				fvo.setDOC_ID(doc_id);
				fvo.setDOC_NAME(inputVO.getDoc_name() == null ? "URL" : inputVO.getDoc_name());
				fvo.setSUBSYSTEM_TYPE("PRD");
				fvo.setDOC_TYPE("02");
				dam.create(fvo);
				TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
				dvo.setDOC_ID(doc_id);
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
			// TBSYS_PRD_LINK
			TBSYS_PRD_LINKVO vo = new TBSYS_PRD_LINKVO();
			TBSYS_PRD_LINKPK pk = new TBSYS_PRD_LINKPK();
			pk.setDOC_ID(doc_id);
			pk.setPRD_ID(ObjectUtils.toString(submap.get("PRD_ID")));
			pk.setPTYPE("MFD");
			vo.setcomp_id(pk);
			dam.create(vo);
		}
		this.sendRtnObject(null);
	}
	
	public void downloadSimple(Object body, IPrimitiveMap header) throws Exception {
		notifyClientToDownloadFile("doc//PRD//PRD233_EXAMPLE.csv", "上傳指定商品代碼範例.csv");
	    this.sendRtnObject(null);
	}
	
	
}