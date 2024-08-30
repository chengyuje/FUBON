package com.systex.jbranch.app.server.fps.fps920;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBFPS_SPP_RECOMMANDVO;
import com.systex.jbranch.app.common.fps.table.TBFPS_SPP_RECOMMAND_HEADVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("fps920")
@Scope("request")
public class FPS920 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(FPS920.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		FPS920InputVO inputVO = (FPS920InputVO) body;
		FPS920OutputVO return_VO = new FPS920OutputVO();
		WorkStation ws = DataManager.getWorkStation(uuid);
		dam = this.getDataAccessManager();
		
		// 判斷主管直接根據有無覆核權限
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'FPS920' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT a.PARAM_NO, a.EFFECT_START_DATE, a.EFFECT_END_DATE, a.SUBMIT_DATE, a.STATUS, a.CREATOR, a.CREATETIME, a.LASTUPDATE, a.MODIFIER, ");
		sql.append("DECODE(a.MODIFIER, NULL, NULL, b.EMP_NAME || '-' || a.MODIFIER) as EDITOR, ");
		sql.append("DECODE(a.CREATOR, NULL, NULL, c.EMP_NAME || '-' || a.CREATOR) as ADDOR ");
		sql.append("FROM TBFPS_SPP_RECOMMAND_HEAD a ");
		sql.append("LEFT JOIN TBORG_MEMBER b on a.MODIFIER = b.EMP_ID ");
		sql.append("LEFT JOIN TBORG_MEMBER c on a.CREATOR = c.EMP_ID ");
		sql.append("WHERE a.PARAM_NO NOT IN (SELECT PARAM_NO FROM TBFPS_SPP_RECOMMAND_HEAD WHERE STATUS = 'S' AND CREATOR != :creator) ");
		queryCondition.setObject("creator", ws.getUser().getUserID());
		// where
		if (inputVO.getDate() != null) {
			sql.append("AND TO_CHAR(:date, 'yyyyMM') = TO_CHAR(a.EFFECT_START_DATE, 'yyyyMM') ");
			queryCondition.setObject("date", inputVO.getDate());
		}
		// 主管的狀態只會有”審核(覆核中)”、 ”核准(生效)”、”失效”
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0)
			sql.append("AND STATUS IN ('P', 'W', 'A', 'F') ");
		//
		sql.append("ORDER BY a.EFFECT_START_DATE DESC, a.LASTUPDATE DESC ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
	
	public void init_detail(Object body, IPrimitiveMap header) throws JBranchException {
		FPS920InputVO inputVO = (FPS920InputVO) body;
		FPS920OutputVO return_VO = new FPS920OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.PARAM_NO, a.SEQ_NO, a.AGE_START, a.AGE_END, a.LASTUPDATE, a.CHILD_YN, a.LN_HOUSE_YN, ");
		sql.append("a.FP_EDUCATION_YN, a.FP_RETIRE_YN, a.FP_BUYHOUSE_YN, a.FP_BUYCAR_YN, a.FP_MARRY_YN, a.FP_OVERSEA_EDUCATION_YN, a.FP_TRAVEL_YN, a.FP_OTHER_YN, ");
		sql.append("DECODE(a.MODIFIER, NULL, NULL, b.EMP_NAME || '-' || a.MODIFIER) as MODIFIER ");
		sql.append("FROM TBFPS_SPP_RECOMMAND a ");
		sql.append("LEFT JOIN TBORG_MEMBER b on a.MODIFIER = b.EMP_ID ");
		sql.append("WHERE a.PARAM_NO = :param_no ");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
	
	public void add(Object body, IPrimitiveMap header) throws Exception {
		FPS920InputVO inputVO = (FPS920InputVO) body;
		WorkStation ws = DataManager.getWorkStation(uuid);
		dam = this.getDataAccessManager();
		
		TBFPS_SPP_RECOMMAND_HEADVO vo = new TBFPS_SPP_RECOMMAND_HEADVO();
		vo = (TBFPS_SPP_RECOMMAND_HEADVO) dam.findByPKey(TBFPS_SPP_RECOMMAND_HEADVO.TABLE_UID, inputVO.getParam_no());
		if (vo != null) {
			vo.setSTATUS("S");
			vo.setEFFECT_START_DATE(new Timestamp(inputVO.getDate().getTime()));
			vo.setCreator(ws.getUser().getUserID());
			dam.update(vo);
		} else
			throw new APException("ehl_01_common_001");
		
		// del first
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("DELETE FROM TBFPS_SPP_RECOMMAND WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		dam.exeUpdate(queryCondition);
		// then add
		for(Map<String, Object> map : inputVO.getTotalList()) {
			String seq = getSN("DETAIL");
			TBFPS_SPP_RECOMMANDVO dvo = new TBFPS_SPP_RECOMMANDVO();
			dvo.setPARAM_NO(inputVO.getParam_no());
			dvo.setSEQ_NO(seq);
			dvo.setAGE_START(new BigDecimal(ObjectUtils.toString(map.get("AGE_START"))));
			dvo.setAGE_END(new BigDecimal(ObjectUtils.toString(map.get("AGE_END"))));
			dvo.setCHILD_YN(ObjectUtils.toString(map.get("CHILD_YN")));
			dvo.setLN_HOUSE_YN(ObjectUtils.toString(map.get("LN_HOUSE_YN")));
			dvo.setFP_EDUCATION_YN(ObjectUtils.toString(map.get("FP_EDUCATION_YN")));
			dvo.setFP_RETIRE_YN(ObjectUtils.toString(map.get("FP_RETIRE_YN")));
			dvo.setFP_BUYHOUSE_YN(ObjectUtils.toString(map.get("FP_BUYHOUSE_YN")));
			dvo.setFP_BUYCAR_YN(ObjectUtils.toString(map.get("FP_BUYCAR_YN")));
			dvo.setFP_MARRY_YN(ObjectUtils.toString(map.get("FP_MARRY_YN")));
			dvo.setFP_OVERSEA_EDUCATION_YN(ObjectUtils.toString(map.get("FP_OVERSEA_EDUCATION_YN")));
			dvo.setFP_TRAVEL_YN(ObjectUtils.toString(map.get("FP_TRAVEL_YN")));
			dvo.setFP_OTHER_YN(ObjectUtils.toString(map.get("FP_OTHER_YN")));
			dam.create(dvo);
		}
		
		this.sendRtnObject(null);
	}
	
	public void copy(Object body, IPrimitiveMap header) throws Exception {
		FPS920InputVO inputVO = (FPS920InputVO) body;
		dam = this.getDataAccessManager();
		
		// TBFPS_SPP_RECOMMAND_HEAD
		String seq;
		TBFPS_SPP_RECOMMAND_HEADVO head_vo = new TBFPS_SPP_RECOMMAND_HEADVO();
		head_vo = (TBFPS_SPP_RECOMMAND_HEADVO) dam.findByPKey(TBFPS_SPP_RECOMMAND_HEADVO.TABLE_UID, inputVO.getParam_no());
		if (head_vo != null) {
			seq = new SimpleDateFormat("yyyyMM").format(new Date()) + StringUtils.leftPad(getSN("MAIN"), 4, "0");
			TBFPS_SPP_RECOMMAND_HEADVO vo_new = new TBFPS_SPP_RECOMMAND_HEADVO();
			vo_new.setPARAM_NO(seq);
			vo_new.setEFFECT_START_DATE(head_vo.getEFFECT_START_DATE());
			vo_new.setEFFECT_END_DATE(head_vo.getEFFECT_END_DATE());
			vo_new.setSTATUS("S");
			dam.create(vo_new);
		} else
			throw new APException("ehl_01_common_001");
		
		// TBFPS_SPP_RECOMMAND
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT * FROM TBFPS_SPP_RECOMMAND WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		for(Map<String, Object> map : list) {
			String seq2 = getSN("DETAIL");
			TBFPS_SPP_RECOMMANDVO dvo = new TBFPS_SPP_RECOMMANDVO();
			dvo.setPARAM_NO(seq);
			dvo.setSEQ_NO(seq2);
			dvo.setAGE_START(new BigDecimal(ObjectUtils.toString(map.get("AGE_START"))));
			dvo.setAGE_END(new BigDecimal(ObjectUtils.toString(map.get("AGE_END"))));
			dvo.setCHILD_YN(ObjectUtils.toString(map.get("CHILD_YN")));
			dvo.setLN_HOUSE_YN(ObjectUtils.toString(map.get("LN_HOUSE_YN")));
			dvo.setFP_EDUCATION_YN(ObjectUtils.toString(map.get("FP_EDUCATION_YN")));
			dvo.setFP_RETIRE_YN(ObjectUtils.toString(map.get("FP_RETIRE_YN")));
			dvo.setFP_BUYHOUSE_YN(ObjectUtils.toString(map.get("FP_BUYHOUSE_YN")));
			dvo.setFP_BUYCAR_YN(ObjectUtils.toString(map.get("FP_BUYCAR_YN")));
			dvo.setFP_MARRY_YN(ObjectUtils.toString(map.get("FP_MARRY_YN")));
			dvo.setFP_OVERSEA_EDUCATION_YN(ObjectUtils.toString(map.get("FP_OVERSEA_EDUCATION_YN")));
			dvo.setFP_TRAVEL_YN(ObjectUtils.toString(map.get("FP_TRAVEL_YN")));
			dvo.setFP_OTHER_YN(ObjectUtils.toString(map.get("FP_OTHER_YN")));
			dam.create(dvo);
		}
		
		this.sendRtnObject(null);
	}
	
	public void delete(Object body, IPrimitiveMap header) throws Exception {
		FPS920InputVO inputVO = (FPS920InputVO) body;
		dam = this.getDataAccessManager();
		
		TBFPS_SPP_RECOMMAND_HEADVO vo = new TBFPS_SPP_RECOMMAND_HEADVO();
		vo = (TBFPS_SPP_RECOMMAND_HEADVO) dam.findByPKey(TBFPS_SPP_RECOMMAND_HEADVO.TABLE_UID, inputVO.getParam_no());
		if (vo != null)
			dam.delete(vo);
		else
			throw new APException("ehl_01_common_001");
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("DELETE FROM TBFPS_SPP_RECOMMAND WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		dam.exeUpdate(queryCondition);
		
		this.sendRtnObject(null);
	}
	
	public void upload(Object body, IPrimitiveMap header) throws Exception {
		FPS920InputVO inputVO = (FPS920InputVO) body;
		FPS920OutputVO return_VO = new FPS920OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		dam = this.getDataAccessManager();
		
		Integer success = 0;
		List<String> error = new ArrayList<String>();
		List<String> error2 = new ArrayList<String>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if(!dataCsv.isEmpty()) {
			// TBFPS_SPP_RECOMMAND_HEAD
			String seq_main = new SimpleDateFormat("yyyyMM").format(new Date()) + StringUtils.leftPad(getSN("MAIN"), 4, "0");
			TBFPS_SPP_RECOMMAND_HEADVO vo_main = new TBFPS_SPP_RECOMMAND_HEADVO();
			vo_main.setPARAM_NO(seq_main);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, 1);
			cal.set(Calendar.DATE, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			vo_main.setEFFECT_START_DATE(new Timestamp(cal.getTime().getTime()));
			vo_main.setSTATUS("S");
			dam.create(vo_main);
			//
			for(int i = 0;i < dataCsv.size();i++) {
				String[] str = dataCsv.get(i);
				if(i == 0) {
					try {
						if(!"年齡(含起日)".equals(str[0].trim()))
							throw new Exception(str[0]);
						else if(!"年齡(不含訖日)".equals(str[1].trim()))
							throw new Exception(str[1]);
						else if(!"未成年子女".equals(str[2].substring(0, 5)))
							throw new Exception(str[2]);
						else if(!"房貸".equals(str[3].trim()))
							throw new Exception(str[3]);
						else if(!"子女教育規劃".equals(str[4].trim()))
							throw new Exception(str[4]);
						else if(!"退休規劃".equals(str[5].trim()))
							throw new Exception(str[5]);
						else if(!"購屋規劃".equals(str[6].trim()))
							throw new Exception(str[6]);
						else if(!"購車規劃".equals(str[7].trim()))
							throw new Exception(str[7]);
						else if(!"結婚規劃".equals(str[8].trim()))
							throw new Exception(str[8]);
						else if(!"留遊學規劃".equals(str[9].trim()))
							throw new Exception(str[9]);
						else if(!"旅遊規劃".equals(str[10].trim()))
							throw new Exception(str[10]);
						else if(!"其他規劃".equals(str[11].trim()))
							throw new Exception(str[11]);
					} catch(Exception ex) {
						throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
					}
					continue;
				}
				// TBFPS_SPP_RECOMMAND
				String seq_de = getSN("DETAIL");
				TBFPS_SPP_RECOMMANDVO vo_detail = new TBFPS_SPP_RECOMMANDVO();
				vo_detail.setPARAM_NO(seq_main);
				vo_detail.setSEQ_NO(seq_de);
				// AGE_START
				if(StringUtils.isNotBlank(str[0])) {
					try {
						BigDecimal str0 = new BigDecimal(str[0]);
						// NUMBER(3,0)
						if(getNumOfBigDecimal(str0) > 3)
							throw new Exception("");
						vo_detail.setAGE_START(str0);
					} catch (Exception e) {
						error.add(i + "列，年齡(起)");
						continue;
					}
				} else {
					error2.add(i + "列，年齡(起)");
					continue;
				}
				// AGE_END
				if(StringUtils.isNotBlank(str[1])) {
					try {
						BigDecimal str1 = new BigDecimal(str[1]);
						// NUMBER(3,0)
						if(getNumOfBigDecimal(str1) > 3)
							throw new Exception("");
						vo_detail.setAGE_END(str1);
					} catch (Exception e) {
						error.add(i + "列，年齡(訖)");
						continue;
					}
				} else {
					error2.add(i + "列，年齡(訖)");
					continue;
				}
				//
				vo_detail.setCHILD_YN(str[2]);
				vo_detail.setLN_HOUSE_YN(str[3]);
				vo_detail.setFP_EDUCATION_YN(str[4]);
				vo_detail.setFP_RETIRE_YN(str[5]);
				vo_detail.setFP_BUYHOUSE_YN(str[6]);
				vo_detail.setFP_BUYCAR_YN(str[7]);
				vo_detail.setFP_MARRY_YN(str[8]);
				vo_detail.setFP_OVERSEA_EDUCATION_YN(str[9]);
				vo_detail.setFP_TRAVEL_YN(str[10]);
				vo_detail.setFP_OTHER_YN(str[11]);
				dam.create(vo_detail);
				
				success += 1;
			}
		}
		
		if(success == 0)
			throw new APException("ehl_01_cus130_003");
		return_VO.setErrorList(error);
		return_VO.setErrorList2(error2);
		this.sendRtnObject(return_VO);
	}
	public void downloadSimple(Object body, IPrimitiveMap header) throws Exception {
		notifyClientToDownloadFile("doc//FPS//FPS920_EXAMPLE.csv", "Life stage參數上傳範例.csv");
	}
	
	public void goReview(Object body, IPrimitiveMap header) throws Exception {
		FPS920InputVO inputVO = (FPS920InputVO) body;
		dam = this.getDataAccessManager();
		
		this.add(body, header);
		
		TBFPS_SPP_RECOMMAND_HEADVO vo = new TBFPS_SPP_RECOMMAND_HEADVO();
		vo = (TBFPS_SPP_RECOMMAND_HEADVO) dam.findByPKey(TBFPS_SPP_RECOMMAND_HEADVO.TABLE_UID, inputVO.getParam_no());
		if (vo != null) {
			vo.setSTATUS("P");
			dam.update(vo);
		} else
			throw new APException("ehl_01_common_001");
		
		this.sendRtnObject(null);
	}
	
	public void review(Object body, IPrimitiveMap header) throws Exception {
		FPS920InputVO inputVO = (FPS920InputVO) body;
		dam = this.getDataAccessManager();
		
		TBFPS_SPP_RECOMMAND_HEADVO vo = new TBFPS_SPP_RECOMMAND_HEADVO();
		vo = (TBFPS_SPP_RECOMMAND_HEADVO) dam.findByPKey(TBFPS_SPP_RECOMMAND_HEADVO.TABLE_UID, inputVO.getParam_no());
		if (vo != null) {
			vo.setSTATUS(inputVO.getStatus());
			if("W".equals(inputVO.getStatus()))
				vo.setSUBMIT_DATE(new Timestamp(System.currentTimeMillis()));
			dam.update(vo);
		} else
			throw new APException("ehl_01_common_001");
		
		this.sendRtnObject(null);
	}
	
	private String getSN(String name) throws JBranchException {
		String ans = "";
		switch(name) {
			case "MAIN":
				SerialNumberUtil sn = new SerialNumberUtil();
				try {
					ans = sn.getNextSerialNumber("TBFPS_SPP_RECOMMAND");
				}
				catch(Exception e) {
					sn.createNewSerial("TBFPS_SPP_RECOMMAND", "0000", 1, "m", new Timestamp(System.currentTimeMillis()), 1, new Long("9999"), "y", new Long("0"), null);
					ans = sn.getNextSerialNumber("TBFPS_SPP_RECOMMAND");
				}
				break;
			case "DETAIL":
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString("SELECT SQ_TBFPS_SPP_RECOMMAND.nextval AS SEQ FROM DUAL");
				List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
				ans = ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
				break;
		}
		return ans;
	}
	
	
	
}