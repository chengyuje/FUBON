package com.systex.jbranch.app.server.fps.mkt110;

import java.io.File;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialClob;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.util.TextUtils;
import com.systex.jbranch.app.common.fps.table.TBMKT_BULLETIN_BOARDVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_FILE_DETAILVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_FILE_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_MKT_LINKVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author moron
 * @date 2016/04/20
 * @spec null
 */
@Component("mkt110")
@Scope("request")
public class MKT110 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(MKT110.class);
	
	public void addData(Object body, IPrimitiveMap header) throws Exception {
		MKT110InputVO inputVO = (MKT110InputVO) body;
		dam = this.getDataAccessManager();
		
		// seq
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBMKT_BULLETIN_BOARD.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		//
		TBMKT_BULLETIN_BOARDVO vo = new TBMKT_BULLETIN_BOARDVO();
		vo.setSEQ(seqNo);
		vo.setBTYPE(inputVO.getbType());
		vo.setSUBJECT(inputVO.getSubject());
		vo.setROLE(TextUtils.join(",",inputVO.getChkCode()));
		vo.setPTYPE(inputVO.getpType());
		if(inputVO.getsDate() != null)
			vo.setS_DATE(new Timestamp(inputVO.getsDate().getTime()));
		if(inputVO.geteDate() != null)
			vo.setE_DATE(new Timestamp(inputVO.geteDate().getTime()));
		vo.setORGN(inputVO.getOrgn());
		vo.setCONTACT(inputVO.getContact());
		vo.setCONTENT(new SerialClob(inputVO.getContent().toCharArray()));
		vo.setACT_TYPE("A");
		vo.setREVIEW_STATUS("W");
		dam.create(vo);
		// byte data
		for (int i = 0; i < inputVO.getFileName().size(); i++) {
			if(!StringUtils.isBlank(inputVO.getFileName().get(i).get("DOC_NAME"))) {
				String SN = getSN();
				String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				byte[] data = Files.readAllBytes(new File(tempPath, inputVO.getFileName().get(i).get("DOC_NAME")).toPath());
				TBSYS_MKT_LINKVO mvo = new TBSYS_MKT_LINKVO();
				mvo.setDOC_ID(SN);
				mvo.setSEQ(vo.getSEQ());
				dam.create(mvo);
				TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
				fvo.setDOC_ID(SN);
				fvo.setDOC_NAME(inputVO.getRealfileName().get(i).get("DOC_NAME"));
				fvo.setSUBSYSTEM_TYPE("CAM");
				fvo.setDOC_TYPE("01");
				dam.create(fvo);
				TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
				dvo.setDOC_ID(SN);
				dvo.setDOC_VERSION_STATUS("2");
				dvo.setDOC_FILE_TYPE("D");
				dvo.setDOC_FILE(ObjectUtil.byteArrToBlob(data));
				dam.create(dvo);
				vo.setATTACHMENT("Y");
			}
		}
		for (int i = 0; i < inputVO.getWeb().size(); i++) {
			if(!StringUtils.isBlank(inputVO.getWeb().get(i).get("DOC_NAME"))) {
				String SN = getSN();
				TBSYS_MKT_LINKVO mvo = new TBSYS_MKT_LINKVO();
				mvo.setDOC_ID(SN);
				mvo.setSEQ(vo.getSEQ());
				dam.create(mvo);
				TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
				fvo.setDOC_ID(SN);
				if(inputVO.getWeb().get(i).get("DOC_NAME").indexOf("http://") == -1 && inputVO.getWeb().get(i).get("DOC_NAME").indexOf("https://") == -1)
					fvo.setDOC_NAME("http://" + inputVO.getWeb().get(i).get("DOC_NAME"));
				else
					fvo.setDOC_NAME(inputVO.getWeb().get(i).get("DOC_NAME"));
				fvo.setSUBSYSTEM_TYPE("CAM");
				fvo.setDOC_TYPE("01");
				dam.create(fvo);
				TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
				dvo.setDOC_ID(SN);
				dvo.setDOC_VERSION_STATUS("2");
				dvo.setDOC_FILE_TYPE("U");
				if(inputVO.getWeb().get(i).get("DOC_NAME").indexOf("http://") == -1 && inputVO.getWeb().get(i).get("DOC_NAME").indexOf("https://") == -1)
					dvo.setDOC_URL("http://" + inputVO.getWeb().get(i).get("DOC_NAME"));
				else
					dvo.setDOC_URL(inputVO.getWeb().get(i).get("DOC_NAME"));
				dam.create(dvo);
				vo.setATTACHMENT("Y");
			}
		}
		// picture
		if(!StringUtils.isBlank(inputVO.getPictureName())) {
			String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			byte[] data = Files.readAllBytes(new File(tempPath, inputVO.getPictureName()).toPath());
			vo.setPICTURE(ObjectUtil.byteArrToBlob(data));
		}
		dam.update(vo);
		this.sendRtnObject(null);
	}
	private String getSN() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBSYS_FILE_MAIN.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		MKT110InputVO inputVO = (MKT110InputVO) body;
		MKT110OutputVO return_VO = new MKT110OutputVO();
		dam = this.getDataAccessManager();
		
		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'MKT110' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SEQ, BTYPE, DEPT_NAME, SUBJECT, ROLE, PTYPE, S_DATE, E_DATE, ORGN, CONTACT, ATTACHMENT, PICTURE, ACT_TYPE, REVIEW_STATUS, CREATOR, EMP_NAME FROM ( ");
		// 覆核人員
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0) {
			sql.append("SELECT DISTINCT mkt.SEQ, mkt.BTYPE, defn.DEPT_NAME, mkt.SUBJECT, mkt.ROLE, mkt.PTYPE, mkt.S_DATE, mkt.E_DATE, mkt.ORGN, mkt.CONTACT, mkt.ATTACHMENT, nvl2(mkt.PICTURE, '1', null) as PICTURE, mkt.ACT_TYPE, mkt.REVIEW_STATUS, mkt.CREATOR, de_info.EMP_NAME ");
			sql.append("FROM TBMKT_BULLETIN_BOARD mkt left join TBORG_DEFN defn on mkt.ORGN = defn.DEPT_ID ");
			sql.append("left join VWORG_BRANCH_EMP_DETAIL_INFO de_info on mkt.CREATOR = de_info.EMP_ID ");
			sql.append("left join TBORG_MEMBER_ROLE mem on mkt.CREATOR = mem.EMP_ID ");
			sql.append("left join TBSYSSECUROLPRIASS pri on mem.ROLE_ID = pri.ROLEID ");
			sql.append("left join (select PARAM_CODE, PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = 'ORG.PRIVILEGEID_RANK') met on pri.PRIVILEGEID = met.PARAM_CODE ");
			sql.append("WHERE (mkt.REVIEW_STATUS = 'W' and met.PARAM_NAME like :pri_id) ");
			sql.append("or (mkt.REVIEW_STATUS = 'Y' and mkt.ACT_TYPE != 'D') or (mkt.REVIEW_STATUS = 'N' and mkt.ACT_TYPE = 'A' and mkt.CREATOR = :creator) ");
			queryCondition.setObject("pri_id", "%" + inputVO.getPri_id() + "%");
		}
		else {
			sql.append("SELECT mkt.SEQ, mkt.BTYPE, defn.DEPT_NAME, mkt.SUBJECT, mkt.ROLE, mkt.PTYPE, mkt.S_DATE, mkt.E_DATE, mkt.ORGN, mkt.CONTACT, mkt.ATTACHMENT, nvl2(mkt.PICTURE, '1', null) as PICTURE, mkt.ACT_TYPE, mkt.REVIEW_STATUS, mkt.CREATOR, de_info.EMP_NAME ");
			sql.append("FROM TBMKT_BULLETIN_BOARD mkt left join TBORG_DEFN defn on mkt.ORGN = defn.DEPT_ID ");
			sql.append("left join VWORG_BRANCH_EMP_DETAIL_INFO de_info on mkt.CREATOR = de_info.EMP_ID ");
			sql.append("WHERE (mkt.REVIEW_STATUS = 'W' AND mkt.CREATOR = :creator) or (mkt.REVIEW_STATUS = 'Y' and mkt.ACT_TYPE != 'D') or (mkt.REVIEW_STATUS = 'N' and mkt.ACT_TYPE = 'A' and mkt.CREATOR = :creator) ");
		}
		queryCondition.setObject("creator", getUserVariable(FubonSystemVariableConsts.LOGINID));
		sql.append(") WHERE 1=1 ");
		if (inputVO.getSeq() != null) {
			sql.append("and SEQ = :seq ");
			queryCondition.setObject("seq", inputVO.getSeq());
		}
		if (StringUtils.isNotBlank(inputVO.getbType())) {
			sql.append("and BTYPE = :btype ");
			queryCondition.setObject("btype", inputVO.getbType());
		}
		if (StringUtils.isNotBlank(inputVO.getSubject())) {
			sql.append("and SUBJECT like :subject ");
			queryCondition.setObject("subject", "%" + inputVO.getSubject() + "%");
		}	
		if (StringUtils.isNotBlank(inputVO.getpType())) {
			sql.append("and PTYPE = :ptype ");
			queryCondition.setObject("ptype", inputVO.getpType());
		}
		if (inputVO.getsDate() != null) {
			sql.append("and S_DATE >= :start ");
			queryCondition.setObject("start", inputVO.getsDate());
		}
		if (inputVO.geteDate() != null) {
			sql.append("and E_DATE <= :end ");
			queryCondition.setObject("end", inputVO.geteDate());
		}
		sql.append("ORDER BY CASE WHEN REVIEW_STATUS = 'W' THEN 0 WHEN REVIEW_STATUS = 'N' THEN 1 WHEN REVIEW_STATUS = 'Y' THEN 2 ELSE 3 END ASC ,S_DATE DESC ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void inqEdit(Object body, IPrimitiveMap header) throws Exception {
		MKT110InputVO inputVO = (MKT110InputVO) body;
		MKT110OutputVO return_VO = new MKT110OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select a.*,b.DEPT_NAME from TBMKT_BULLETIN_BOARD a ");
		sql.append("left join TBORG_DEFN b on a.ORGN = b.DEPT_ID ");
		sql.append("where SEQ = :seq ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("seq", inputVO.getSeq());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		for(Map<String, Object> map : list) {
			if (map.get("PICTURE") != null) {
				Blob blob = (Blob) map.get("PICTURE");
				int blobLength = (int) blob.length();  
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
				map.put("PICTURE", blobAsBytes);
				blob.free();
			}
			if (map.get("CONTENT") != null) {
				Reader in = ((Clob) map.get("CONTENT")).getCharacterStream();
				StringWriter w = new StringWriter();
				IOUtils.copy(in, w);
				String clobAsString = w.toString();
				map.put("CONTENT", clobAsString);
				in.close();
				w.close();
			}
		}
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void getUpdateData(Object body, IPrimitiveMap header) throws Exception {
		MKT110InputVO inputVO = (MKT110InputVO) body;
		MKT110OutputVO return_VO = new MKT110OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select b.DOC_ID, b.DOC_NAME, UPPER(substr(b.DOC_NAME, -3)) AS PDFNAME from TBSYS_MKT_LINK a ");
		sql.append("left join TBSYS_FILE_MAIN b on a.DOC_ID = b.DOC_ID ");
		sql.append("left join TBSYS_FILE_DETAIL c on b.DOC_ID = c.DOC_ID ");
		sql.append("where a.seq = :seq and c.DOC_FILE_TYPE = 'D' ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("seq", inputVO.getSeq());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("select b.DOC_ID,b.DOC_NAME from TBSYS_MKT_LINK a ");
		sql.append("left join TBSYS_FILE_MAIN b on a.DOC_ID = b.DOC_ID ");
		sql.append("left join TBSYS_FILE_DETAIL c on b.DOC_ID = c.DOC_ID ");
		sql.append("where a.seq = :seq and c.DOC_FILE_TYPE = 'U' ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("seq", inputVO.getSeq());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		return_VO.setResultList2(list2);
		
		this.sendRtnObject(return_VO);
	}
	
	public void editData(Object body, IPrimitiveMap header) throws Exception {
		MKT110InputVO inputVO = (MKT110InputVO) body;
		dam = this.getDataAccessManager();
		
		TBMKT_BULLETIN_BOARDVO vo = new TBMKT_BULLETIN_BOARDVO();
		vo = (TBMKT_BULLETIN_BOARDVO) dam.findByPKey(TBMKT_BULLETIN_BOARDVO.TABLE_UID, inputVO.getSeq());
		if (vo != null) {
			if("W".equals(vo.getREVIEW_STATUS()))
				throw new APException("ehl_01_common_028");
			vo.setBTYPE(inputVO.getbType());
			vo.setSUBJECT(inputVO.getSubject());
			vo.setROLE(TextUtils.join(",",inputVO.getChkCode()));
			vo.setPTYPE(inputVO.getpType());
			if(inputVO.getsDate() != null)
				vo.setS_DATE(new Timestamp(inputVO.getsDate().getTime()));
			if(inputVO.geteDate() != null)
				vo.setE_DATE(new Timestamp(inputVO.geteDate().getTime()));
			vo.setORGN(inputVO.getOrgn());
			vo.setCONTACT(inputVO.getContact());
			vo.setCONTENT(new SerialClob(inputVO.getContent().toCharArray()));
			vo.setATTACHMENT(null);
			// 2017/8/7 被退回點編輯
			if("N".equals(vo.getREVIEW_STATUS()))
				vo.setACT_TYPE("A");
			else
				vo.setACT_TYPE("M");
			vo.setREVIEW_STATUS("W");
			vo.setCreator(ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINID)));
			dam.update(vo);
			
			// byte data
			for (int i = 0; i < inputVO.getFileName().size(); i++) {
				// 空的才是新上傳
				if(StringUtils.isBlank(inputVO.getFileName().get(i).get("DOC_ID"))) {
					// 檢查而已
					if(!StringUtils.isBlank(inputVO.getFileName().get(i).get("DOC_NAME"))) {
						String SN = getSN();
						String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
						byte[] data = Files.readAllBytes(new File(tempPath, inputVO.getFileName().get(i).get("DOC_NAME")).toPath());
						TBSYS_MKT_LINKVO mvo = new TBSYS_MKT_LINKVO();
						mvo.setDOC_ID(SN);
						mvo.setSEQ(vo.getSEQ());
						dam.create(mvo);
						TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
						fvo.setDOC_ID(SN);
						fvo.setDOC_NAME(inputVO.getRealfileName().get(i).get("DOC_NAME"));
						fvo.setSUBSYSTEM_TYPE("CAM");
						fvo.setDOC_TYPE("01");
						dam.create(fvo);
						TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
						dvo.setDOC_ID(SN);
						dvo.setDOC_VERSION_STATUS("2");
						dvo.setDOC_FILE_TYPE("D");
						dvo.setDOC_FILE(ObjectUtil.byteArrToBlob(data));
						dam.create(dvo);
						vo.setATTACHMENT("Y");
					}
				} else {
					vo.setATTACHMENT("Y");
				}
			}
			for (int i = 0; i < inputVO.getWeb().size(); i++) {
				// 空的才是新上傳
				if(StringUtils.isBlank(inputVO.getWeb().get(i).get("DOC_ID"))) {
					// 檢查而已
					if(!StringUtils.isBlank(inputVO.getWeb().get(i).get("DOC_NAME"))) {
						String SN = getSN();
						TBSYS_MKT_LINKVO mvo = new TBSYS_MKT_LINKVO();
						mvo.setDOC_ID(SN);
						mvo.setSEQ(vo.getSEQ());
						dam.create(mvo);
						TBSYS_FILE_MAINVO fvo = new TBSYS_FILE_MAINVO();
						fvo.setDOC_ID(SN);
						if(inputVO.getWeb().get(i).get("DOC_NAME").indexOf("http://") == -1 && inputVO.getWeb().get(i).get("DOC_NAME").indexOf("https://") == -1)
							fvo.setDOC_NAME("http://" + inputVO.getWeb().get(i).get("DOC_NAME"));
						else
							fvo.setDOC_NAME(inputVO.getWeb().get(i).get("DOC_NAME"));
						fvo.setSUBSYSTEM_TYPE("CAM");
						fvo.setDOC_TYPE("01");
						dam.create(fvo);
						TBSYS_FILE_DETAILVO dvo = new TBSYS_FILE_DETAILVO();
						dvo.setDOC_ID(SN);
						dvo.setDOC_VERSION_STATUS("2");
						dvo.setDOC_FILE_TYPE("U");
						if(inputVO.getWeb().get(i).get("DOC_NAME").indexOf("http://") == -1 && inputVO.getWeb().get(i).get("DOC_NAME").indexOf("https://") == -1)
							dvo.setDOC_URL("http://" + inputVO.getWeb().get(i).get("DOC_NAME"));
						else
							dvo.setDOC_URL(inputVO.getWeb().get(i).get("DOC_NAME"));
						dam.create(dvo);
						vo.setATTACHMENT("Y");
					}
				} else {
					vo.setATTACHMENT("Y");
				}
			}
			// picture
			if(StringUtils.isBlank(inputVO.getExipicture()))
				vo.setPICTURE(null);
			if(!StringUtils.isBlank(inputVO.getPictureName())) {
				String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				byte[] data = Files.readAllBytes(new File(tempPath, inputVO.getPictureName()).toPath());
				vo.setPICTURE(ObjectUtil.byteArrToBlob(data));
			}
			dam.update(vo);
			// 刪除舊有
			if(inputVO.getDelId().size() > 0) {
				for (String id : inputVO.getDelId()) {
					TBSYS_MKT_LINKVO mvo = new TBSYS_MKT_LINKVO();
					mvo = (TBSYS_MKT_LINKVO) dam.findByPKey(
							TBSYS_MKT_LINKVO.TABLE_UID, id);
					if (mvo != null) {
						dam.delete(mvo);
					}
				}
			}
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		this.sendRtnObject(null);
	}
	
	public void deleteData(Object body, IPrimitiveMap header) throws Exception {
		MKT110InputVO inputVO = (MKT110InputVO) body;
		dam = this.getDataAccessManager();
		
		TBMKT_BULLETIN_BOARDVO vo = new TBMKT_BULLETIN_BOARDVO();
		vo = (TBMKT_BULLETIN_BOARDVO) dam.findByPKey(TBMKT_BULLETIN_BOARDVO.TABLE_UID, inputVO.getSeq());
		if (vo != null) {
			if("W".equals(vo.getREVIEW_STATUS()))
				throw new APException("ehl_01_common_028");
			// 2017/8/7 被退回點刪除
			if("N".equals(vo.getREVIEW_STATUS())) {
				vo.setACT_TYPE("D");
				vo.setREVIEW_STATUS("Y");
			}
			else {
				vo.setACT_TYPE("D");
				vo.setREVIEW_STATUS("W");
			}
			vo.setCreator(ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINID)));
			dam.update(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		this.sendRtnObject(null);
	}
	
	public void review(Object body, IPrimitiveMap header) throws JBranchException {
		MKT110InputVO inputVO = (MKT110InputVO) body;
		dam = this.getDataAccessManager();
		
		for(Map<String, Object> rmap : inputVO.getReview_list()) {
			TBMKT_BULLETIN_BOARDVO vo = new TBMKT_BULLETIN_BOARDVO();
			vo = (TBMKT_BULLETIN_BOARDVO) dam.findByPKey(TBMKT_BULLETIN_BOARDVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(rmap.get("SEQ"))));
			if (vo != null) {
				// 2017/8/7 退回
				if("N".equals(inputVO.getStatus())) {
					// 新增 就是 A N
					if("A".equals(vo.getACT_TYPE()))
						vo.setREVIEW_STATUS(inputVO.getStatus());
					// 修改 刪除直接打回去 A Y
					else {
						vo.setACT_TYPE("A");
						vo.setREVIEW_STATUS("Y");
					}
				} else
					vo.setREVIEW_STATUS(inputVO.getStatus());
				dam.update(vo);
			} else
				throw new APException("ehl_01_common_001");
		}
		
		this.sendRtnObject(null);
	}
	
	
}