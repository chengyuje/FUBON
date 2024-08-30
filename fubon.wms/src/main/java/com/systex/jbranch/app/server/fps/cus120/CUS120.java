package com.systex.jbranch.app.server.fps.cus120;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSYS_EMAIL_ATTACHMENTVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_EMAIL_ATTACHMENT_REVIEWVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_EMAIL_CONTENTVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_EMAIL_CONTENT_REVIEWVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_FILE_DETAILVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_FILE_MAINVO;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("cus120")
@Scope("request")
public class CUS120 extends BizLogic{
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CUS120.class);

	public void queryEmailContent(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		CUS120ContentOutputVO return_VO = new CUS120ContentOutputVO();
		dam = this.getDataAccessManager();
		
		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'CUS120' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT SEQ, CONTENT, CATEGORY_BY_CONTENT, CATEGORY_BY_PRODUCT, ACT_TYPE, REVIEW_STATUS, CREATETIME FROM ( ");
		// 覆核人員
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0) {
			sql.append("SELECT rw.SEQ,rw.CONTENT,rw.CATEGORY_BY_CONTENT,rw.CATEGORY_BY_PRODUCT,rw.ACT_TYPE,rw.REVIEW_STATUS, rw.CREATETIME ");
			sql.append("FROM TBSYS_EMAIL_CONTENT_REVIEW rw WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("UNION ");
			sql.append("SELECT con.SEQ, con.CONTENT, con.CATEGORY_BY_CONTENT, con.CATEGORY_BY_PRODUCT, rw.ACT_TYPE,rw.REVIEW_STATUS, con.CREATETIME ");
			sql.append("FROM TBSYS_EMAIL_CONTENT con ");
			sql.append("left join TBSYS_EMAIL_CONTENT_REVIEW rw on rw.SEQ_M = con.SEQ and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE con.SEQ NOT IN (SELECT NVL(SEQ_M, -1) FROM TBSYS_EMAIL_CONTENT_REVIEW WHERE REVIEW_STATUS = 'W') ");
		}
		else {
			sql.append("SELECT rw.SEQ,rw.CONTENT,rw.CATEGORY_BY_CONTENT,rw.CATEGORY_BY_PRODUCT,rw.ACT_TYPE,rw.REVIEW_STATUS, rw.CREATETIME ");
			sql.append("FROM TBSYS_EMAIL_CONTENT_REVIEW rw WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("AND rw.CREATOR = :creator ");
			sql.append("UNION ");
			sql.append("SELECT con.SEQ, con.CONTENT, con.CATEGORY_BY_CONTENT, con.CATEGORY_BY_PRODUCT, rw.ACT_TYPE,rw.REVIEW_STATUS, con.CREATETIME ");
			sql.append("FROM TBSYS_EMAIL_CONTENT con ");
			sql.append("left join TBSYS_EMAIL_CONTENT_REVIEW rw on rw.SEQ_M = con.SEQ and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE con.SEQ NOT IN (SELECT NVL(SEQ_M, -1) FROM TBSYS_EMAIL_CONTENT_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
			queryCondition.setObject("creator", ws.getUser().getUserID());
		}
		sql.append(") WHERE 1=1 ");
		sql.append("ORDER BY CASE WHEN REVIEW_STATUS = 'W' THEN 0 WHEN REVIEW_STATUS = 'Y' THEN 1 ELSE 2 END ASC,SEQ ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void delEmailContentData(Object body, IPrimitiveMap header) throws JBranchException {
		CUS120ContentInputVO inputVO = (CUS120ContentInputVO) body ;
		dam = this.getDataAccessManager();
		
		// check again
		// TBSYS_EMAIL_CONTENT_REVIEW
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SEQ_M FROM TBSYS_EMAIL_CONTENT_REVIEW where SEQ_M = :seq and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("seq", inputVO.getSeq());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			throw new APException("ehl_01_common_028");
		
		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBSYS_EMAIL_CONTENT_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// get ori
		TBSYS_EMAIL_CONTENTVO vo = new TBSYS_EMAIL_CONTENTVO();
		vo = (TBSYS_EMAIL_CONTENTVO) dam.findByPKey(TBSYS_EMAIL_CONTENTVO.TABLE_UID, new BigDecimal(inputVO.getSeq()));
		if (vo != null) {
			// add TBSYS_EMAIL_CONTENT_REVIEW
			TBSYS_EMAIL_CONTENT_REVIEWVO rvo = new TBSYS_EMAIL_CONTENT_REVIEWVO();
			rvo.setSEQ(seqNo);
			rvo.setSEQ_M(new BigDecimal(inputVO.getSeq()));
			rvo.setCONTENT(vo.getCONTENT());
			rvo.setCATEGORY_BY_CONTENT(vo.getCATEGORY_BY_CONTENT());
			rvo.setCATEGORY_BY_PRODUCT(vo.getCATEGORY_BY_PRODUCT());
			rvo.setACT_TYPE("D");
			rvo.setREVIEW_STATUS("W");
			dam.create(rvo);
		} else
			throw new APException("ehl_01_common_001");
		
		this.sendRtnObject(null);
	}
	
	// 2017/4/18
	public void addMessageReview(Object body, IPrimitiveMap header) throws JBranchException {
		CUS120ContentInputVO inputVO = (CUS120ContentInputVO) body;
		dam = this.getDataAccessManager();
		
		// seq
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBSYS_EMAIL_CONTENT_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// add TBSYS_EMAIL_CONTENT_REVIEW
		TBSYS_EMAIL_CONTENT_REVIEWVO vo = new TBSYS_EMAIL_CONTENT_REVIEWVO();
		vo.setSEQ(seqNo);
		vo.setCONTENT(inputVO.getMessage());
		vo.setCATEGORY_BY_CONTENT(inputVO.getContent());
		vo.setCATEGORY_BY_PRODUCT(inputVO.getProduct());
		vo.setACT_TYPE("A");
		vo.setREVIEW_STATUS("W");
		dam.create(vo);
		this.sendRtnObject(null);
	}
	
	public void ReviewMessage(Object body, IPrimitiveMap header) throws JBranchException {
		CUS120ContentInputVO inputVO = (CUS120ContentInputVO) body ;
		dam = this.getDataAccessManager();
		
		// 2017/4/18 add 覆核
		for(Map<String, Object> rmap : inputVO.getReview_list()) {
			TBSYS_EMAIL_CONTENT_REVIEWVO rvo = new TBSYS_EMAIL_CONTENT_REVIEWVO();
			rvo = (TBSYS_EMAIL_CONTENT_REVIEWVO) dam.findByPKey(TBSYS_EMAIL_CONTENT_REVIEWVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(rmap.get("SEQ"))));
			if (rvo != null) {
				// confirm
				if("Y".equals(inputVO.getStatus())) {
					// 新增
					if("A".equals(rvo.getACT_TYPE())) {
						BigDecimal seqNo = getSeqNum();
						TBSYS_EMAIL_CONTENTVO vo = new TBSYS_EMAIL_CONTENTVO();
						vo.setSEQ(seqNo);
						vo.setCONTENT(rvo.getCONTENT());
						vo.setCATEGORY_BY_CONTENT(rvo.getCATEGORY_BY_CONTENT());
						vo.setCATEGORY_BY_PRODUCT(rvo.getCATEGORY_BY_PRODUCT());
						dam.create(vo);
					}
					// 修改
					else if("M".equals(rvo.getACT_TYPE())) {
						// no edit
					}
					// 刪除
					else if("D".equals(rvo.getACT_TYPE())) {
						TBSYS_EMAIL_CONTENTVO vo = new TBSYS_EMAIL_CONTENTVO();
						vo = (TBSYS_EMAIL_CONTENTVO) dam.findByPKey(TBSYS_EMAIL_CONTENTVO.TABLE_UID, rvo.getSEQ_M());
						if (vo != null)
							dam.delete(vo);
						else
							throw new APException("ehl_01_common_001");
					}
				}
				rvo.setREVIEW_STATUS(inputVO.getStatus());
				dam.update(rvo);
			} else
				throw new APException("ehl_01_common_001");
		}
		
		this.sendRtnObject(null);
	}
	
	public void queryEmailAttachment(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		CUS120AttachmentOutputVO return_VO = new CUS120AttachmentOutputVO();
		dam = this.getDataAccessManager();
		
		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'CUS120' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT SEQ, DOC_ID, DOCNAME, FILENAME, CATEGORY_BY_CONTENT, CATEGORY_BY_PRODUCT, ACT_TYPE, REVIEW_STATUS, CREATETIME FROM ( ");
		// 覆核人員
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0) {
			sql.append("SELECT rw.SEQ,rw.DOC_ID,rw.DOCNAME,rw.FILENAME,rw.CATEGORY_BY_CONTENT,rw.CATEGORY_BY_PRODUCT,rw.ACT_TYPE,rw.REVIEW_STATUS, rw.CREATETIME ");
			sql.append("FROM TBSYS_EMAIL_ATTACHMENT_REVIEW rw WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("UNION ");
			sql.append("SELECT null as SEQ, att.DOC_ID, att.DOCNAME, att.FILENAME, att.CATEGORY_BY_CONTENT, att.CATEGORY_BY_PRODUCT, rw.ACT_TYPE,rw.REVIEW_STATUS, att.CREATETIME ");
			sql.append("FROM TBSYS_EMAIL_ATTACHMENT att ");
			sql.append("left join TBSYS_EMAIL_ATTACHMENT_REVIEW rw on rw.DOC_ID = att.DOC_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE att.DOC_ID NOT IN (SELECT DOC_ID FROM TBSYS_EMAIL_ATTACHMENT_REVIEW WHERE REVIEW_STATUS = 'W') ");
		}
		else {
			sql.append("SELECT rw.SEQ,rw.DOC_ID,rw.DOCNAME,rw.FILENAME,rw.CATEGORY_BY_CONTENT,rw.CATEGORY_BY_PRODUCT,rw.ACT_TYPE,rw.REVIEW_STATUS, rw.CREATETIME ");
			sql.append("FROM TBSYS_EMAIL_ATTACHMENT_REVIEW rw WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("AND rw.CREATOR = :creator ");
			sql.append("UNION ");
			sql.append("SELECT null as SEQ, att.DOC_ID, att.DOCNAME, att.FILENAME, att.CATEGORY_BY_CONTENT, att.CATEGORY_BY_PRODUCT, rw.ACT_TYPE,rw.REVIEW_STATUS, att.CREATETIME ");
			sql.append("FROM TBSYS_EMAIL_ATTACHMENT att ");
			sql.append("left join TBSYS_EMAIL_ATTACHMENT_REVIEW rw on rw.DOC_ID = att.DOC_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE att.DOC_ID NOT IN (SELECT DOC_ID FROM TBSYS_EMAIL_ATTACHMENT_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
			queryCondition.setObject("creator", ws.getUser().getUserID());
		}
		sql.append(") WHERE 1=1 ");
		sql.append("ORDER BY CASE WHEN REVIEW_STATUS = 'W' THEN 0 WHEN REVIEW_STATUS = 'Y' THEN 1 ELSE 2 END ASC, CREATETIME DESC ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void delEmailAttachmentData(Object body, IPrimitiveMap header) throws JBranchException {
		CUS120AttachmentInputVO inputVO = (CUS120AttachmentInputVO) body;
		dam = this.getDataAccessManager();
		
		// check again
		// TBSYS_EMAIL_ATTACHMENT_REVIEW
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DOC_ID FROM TBSYS_EMAIL_ATTACHMENT_REVIEW where DOC_ID = :doc_id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("doc_id", inputVO.getDocno());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			throw new APException("ehl_01_common_028");
		
		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBSYS_EMAIL_ATTACHMENT_RW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// get ori
		TBSYS_EMAIL_ATTACHMENTVO vo = new TBSYS_EMAIL_ATTACHMENTVO();
		vo = (TBSYS_EMAIL_ATTACHMENTVO) dam.findByPKey(TBSYS_EMAIL_ATTACHMENTVO.TABLE_UID, inputVO.getDocno());
		if (vo != null) {
			// add TBSYS_EMAIL_ATTACHMENT_REVIEW
			TBSYS_EMAIL_ATTACHMENT_REVIEWVO rvo = new TBSYS_EMAIL_ATTACHMENT_REVIEWVO();
			rvo.setSEQ(seqNo);
			rvo.setDOC_ID(vo.getDOC_ID());
			rvo.setDOCNAME(vo.getDOCNAME());
			rvo.setFILENAME(vo.getFILENAME());
			rvo.setCATEGORY_BY_CONTENT(vo.getCATEGORY_BY_CONTENT());
			rvo.setCATEGORY_BY_PRODUCT(vo.getCATEGORY_BY_PRODUCT());
			rvo.setACT_TYPE("D");
			rvo.setREVIEW_STATUS("W");
			dam.create(rvo);
		} else
			throw new APException("ehl_01_common_001");
		
		this.sendRtnObject(null);
	}
	
	public void addDataReview(Object body, IPrimitiveMap header) throws Exception {
		CUS120AttachmentInputVO inputVO = (CUS120AttachmentInputVO) body ;
		dam = this.getDataAccessManager();
		
		if(StringUtils.isNotBlank(inputVO.getDataName())) {
			byte[] data = Files.readAllBytes(Paths.get(new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getDataName()).toString()));
			// seq
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT SQ_TBSYS_FILE_MAIN.nextval AS SEQ FROM DUAL ");
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> DOC_LIST = dam.exeQuery(queryCondition);
			String SN = ObjectUtils.toString(DOC_LIST.get(0).get("SEQ"));
			
			// seq
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT SQ_TBSYS_EMAIL_ATTACHMENT_RW.nextval AS SEQ FROM DUAL ");
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
			BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
			// TBSYS_EMAIL_ATTACHMENT_REVIEW
			TBSYS_EMAIL_ATTACHMENT_REVIEWVO rvo = new TBSYS_EMAIL_ATTACHMENT_REVIEWVO();
			rvo.setSEQ(seqNo);
			rvo.setDOC_ID(SN);
			rvo.setDOCNAME(inputVO.getFileName());
			rvo.setFILENAME(inputVO.getRealDataName());
			rvo.setCATEGORY_BY_CONTENT(inputVO.getContent());
			rvo.setCATEGORY_BY_PRODUCT(inputVO.getProduct());
			rvo.setACT_TYPE("A");
			rvo.setREVIEW_STATUS("W");
			dam.create(rvo);
			// TBSYS_FILE_MAIN
			TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
			fvo.setDOC_ID(SN);
			fvo.setDOC_NAME(inputVO.getRealDataName());
			fvo.setSUBSYSTEM_TYPE("EMAIL");
			fvo.setDOC_TYPE("05");
			dam.create(fvo);
			// TBSYS_FILE_DETAIL
			TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
			dvo.setDOC_ID(SN);
			dvo.setDOC_VERSION_STATUS("2");
			dvo.setDOC_FILE_TYPE("D");
			dvo.setDOC_FILE(ObjectUtil.byteArrToBlob(data));
			dam.create(dvo);
		}
		this.sendRtnObject(null);
	}
	
	public void ReviewData(Object body, IPrimitiveMap header) throws Exception {
		CUS120AttachmentInputVO inputVO = (CUS120AttachmentInputVO) body ;
		dam = this.getDataAccessManager();
		
		// 2017/4/18 add 覆核
		for(Map<String, Object> rmap : inputVO.getReview_list()) {
			TBSYS_EMAIL_ATTACHMENT_REVIEWVO rvo = new TBSYS_EMAIL_ATTACHMENT_REVIEWVO();
			rvo = (TBSYS_EMAIL_ATTACHMENT_REVIEWVO) dam.findByPKey(TBSYS_EMAIL_ATTACHMENT_REVIEWVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(rmap.get("SEQ"))));
			if (rvo != null) {
				// confirm
				if("Y".equals(inputVO.getStatus())) {
					// 新增
					if("A".equals(rvo.getACT_TYPE())) {
						// 只需要新增 TBSYS_EMAIL_ATTACHMENT
						TBSYS_EMAIL_ATTACHMENTVO vo = new TBSYS_EMAIL_ATTACHMENTVO();	
						vo.setDOC_ID(rvo.getDOC_ID());		
						vo.setDOCNAME(rvo.getDOCNAME());
						vo.setFILENAME(rvo.getFILENAME());
						vo.setCATEGORY_BY_CONTENT(rvo.getCATEGORY_BY_CONTENT());
						vo.setCATEGORY_BY_PRODUCT(rvo.getCATEGORY_BY_PRODUCT());
						dam.create(vo);
					}
					// 修改
					else if("M".equals(rvo.getACT_TYPE())) {
						// no edit
					}
					// 刪除
					else if("D".equals(rvo.getACT_TYPE())) {
						// TBSYS_EMAIL_ATTACHMENT 
						TBSYS_EMAIL_ATTACHMENTVO vo = new TBSYS_EMAIL_ATTACHMENTVO();
						vo = (TBSYS_EMAIL_ATTACHMENTVO) dam.findByPKey(TBSYS_EMAIL_ATTACHMENTVO.TABLE_UID, rvo.getDOC_ID());
						if (vo != null) {
							dam.delete(vo);
						} else
							throw new APException("ehl_01_common_001");
						// TBSYS_FILE_MAIN
						TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
						fvo = (TBSYS_FILE_MAINVO) dam.findByPKey(TBSYS_FILE_MAINVO.TABLE_UID, rvo.getDOC_ID());
						if (fvo != null)
							dam.delete(fvo);
						// TBSYS_FILE_DETAIL
						TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
						dvo = (TBSYS_FILE_DETAILVO) dam.findByPKey(TBSYS_FILE_DETAILVO.TABLE_UID, rvo.getDOC_ID());
						if (dvo != null)
							dam.delete(dvo);
					}
				}
				// 否認要刪除垃圾
				else {
					// TBSYS_FILE_MAIN
					TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
					fvo = (TBSYS_FILE_MAINVO) dam.findByPKey(TBSYS_FILE_MAINVO.TABLE_UID, rvo.getDOC_ID());
					if (fvo != null)
						dam.delete(fvo);
					// TBSYS_FILE_DETAIL
					TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
					dvo = (TBSYS_FILE_DETAILVO) dam.findByPKey(TBSYS_FILE_DETAILVO.TABLE_UID, rvo.getDOC_ID());
					if (dvo != null)
						dam.delete(dvo);
				}
				rvo.setREVIEW_STATUS(inputVO.getStatus());
				dam.update(rvo);
			} else
				throw new APException("ehl_01_common_001");
		}
		
		this.sendRtnObject(null);
	}
	
	private BigDecimal getSeqNum() throws JBranchException {
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString("select TBSYS_EMAIL_CONTENT_SEQ.NEXTVAL from dual");
		List<Map<String, Object>> result = dam.exeQuery(condition);
		return (BigDecimal) result.get(0).get("NEXTVAL");
	}
	
}