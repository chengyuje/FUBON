package com.systex.jbranch.app.server.fps.fps941;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBFPS_OTHER_PARA_MANUALVO;
import com.systex.jbranch.app.common.fps.table.TBFPS_OTHER_PARA_WARNINGVO;
import com.systex.jbranch.app.common.fps.table.TBFPS_RPT_PARA_HEADVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("fps941")
@Scope("request")
public class FPS941 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(FPS941.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		FPS941InputVO inputVO = (FPS941InputVO) body;
		FPS941OutputVO return_VO = new FPS941OutputVO();
		WorkStation ws = DataManager.getWorkStation(uuid);
		dam = this.getDataAccessManager();
		
		// 判斷主管直接根據有無覆核權限
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'FPS941' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT a.PARAM_NO, a.EFFECT_START_DATE, a.EFFECT_END_DATE, a.SUBMIT_DATE, a.STATUS, a.CREATOR, a.CREATETIME, a.LASTUPDATE, a.MODIFIER, ");
		sql.append("DECODE(a.MODIFIER, NULL, NULL, b.EMP_NAME || '-' || a.MODIFIER) as EDITOR, ");
		sql.append("DECODE(a.CREATOR, NULL, NULL, c.EMP_NAME || '-' || a.CREATOR) as ADDOR ");
		sql.append("FROM TBFPS_RPT_PARA_HEAD a ");
		sql.append("LEFT JOIN TBORG_MEMBER b on a.MODIFIER = b.EMP_ID ");
		sql.append("LEFT JOIN TBORG_MEMBER c on a.CREATOR = c.EMP_ID ");
		sql.append("WHERE a.PARAM_NO NOT IN (SELECT PARAM_NO FROM TBFPS_RPT_PARA_HEAD WHERE STATUS = 'S' AND CREATOR != :creator) ");
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
	
	public void init_detail(Object body, IPrimitiveMap header) throws Exception {
		FPS941InputVO inputVO = (FPS941InputVO) body;
		FPS941OutputVO return_VO = new FPS941OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT RPT_PIC, RPT_PIC_NAME from TBFPS_RPT_PARA_HEAD WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		for(Map<String, Object> map : list) {
			if (map.get("RPT_PIC") != null) {
				Blob blob = (Blob) map.get("RPT_PIC");
				int blobLength = (int) blob.length();  
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
				map.put("RPT_PIC", blobAsBytes);
				blob.free();
			}
		}
		return_VO.setResultList(list);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT * from TBFPS_OTHER_PARA_MANUAL WHERE PARAM_NO = :param_no and DESC_TYPE = 'F' ORDER BY FPS_TYPE, RANK");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		return_VO.setResultList2(list2);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT * from TBFPS_OTHER_PARA_MANUAL WHERE PARAM_NO = :param_no and DESC_TYPE = 'M' ORDER BY FPS_TYPE, RANK");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
		return_VO.setResultList3(list3);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT * from TBFPS_OTHER_PARA_WARNING WHERE PARAM_NO = :param_no ORDER BY DECODE(PRD_TYPE, 'FUND', 1, 'ETF', 2, 'BND', 3, 'SN', 4, 'SI', 5, 'INSI1', 6, 'INSI2', 7, 'INS-S', 8, 9), RANK");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		List<Map<String, Object>> list4 = dam.exeQuery(queryCondition);
		return_VO.setResultList4(list4);
		
		this.sendRtnObject(return_VO);
	}
	
	public void copy(Object body, IPrimitiveMap header) throws Exception {
		FPS941InputVO inputVO = (FPS941InputVO) body;
		dam = this.getDataAccessManager();
		
		// TBFPS_RPT_PARA_HEAD
		String seq;
		TBFPS_RPT_PARA_HEADVO head_vo = (TBFPS_RPT_PARA_HEADVO) dam.findByPKey(TBFPS_RPT_PARA_HEADVO.TABLE_UID, inputVO.getParam_no());
		if (head_vo != null) {
			seq = new SimpleDateFormat("yyyyMM").format(new Date()) + StringUtils.leftPad(getSN("MAIN"), 4, "0");
			TBFPS_RPT_PARA_HEADVO vo_new = new TBFPS_RPT_PARA_HEADVO();
			vo_new.setPARAM_NO(seq);
			vo_new.setEFFECT_START_DATE(head_vo.getEFFECT_START_DATE());
			vo_new.setEFFECT_END_DATE(head_vo.getEFFECT_END_DATE());
			vo_new.setRPT_PIC_NAME(head_vo.getRPT_PIC_NAME());
			vo_new.setRPT_PIC(head_vo.getRPT_PIC());
			vo_new.setSTATUS("S");
			dam.create(vo_new);
		} else
			throw new APException("ehl_01_common_001");
		
		// TBFPS_OTHER_PARA_MANUAL
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT * FROM TBFPS_OTHER_PARA_MANUAL WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		for(Map<String, Object> map : list) {
			String seq2 = getSN("MANUAL");
			TBFPS_OTHER_PARA_MANUALVO manu_vo = new TBFPS_OTHER_PARA_MANUALVO();
			manu_vo.setSEQ(new BigDecimal(seq2));
			manu_vo.setPARAM_NO(seq);
			manu_vo.setFPS_TYPE(ObjectUtils.toString(map.get("FPS_TYPE")));
			manu_vo.setDESC_TYPE(ObjectUtils.toString(map.get("DESC_TYPE")));
			manu_vo.setFONT(ObjectUtils.toString(map.get("FONT")));
			if(map.get("RANK") != null)
				manu_vo.setRANK(new BigDecimal(ObjectUtils.toString(map.get("RANK"))));
			manu_vo.setCONTENT(ObjectUtils.toString(map.get("CONTENT")));
			dam.create(manu_vo);
		}
		
		// TBFPS_OTHER_PARA_WARNING
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT * FROM TBFPS_OTHER_PARA_WARNING WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		for(Map<String, Object> map : list2) {
			String seq2 = getSN("WARNING");
			TBFPS_OTHER_PARA_WARNINGVO manu_vo = new TBFPS_OTHER_PARA_WARNINGVO();
			manu_vo.setSEQ(new BigDecimal(seq2));
			manu_vo.setPARAM_NO(seq);
			manu_vo.setPRD_TYPE(ObjectUtils.toString(map.get("PRD_TYPE")));
			manu_vo.setFONT(ObjectUtils.toString(map.get("FONT")));
			manu_vo.setRANK(new BigDecimal(ObjectUtils.toString(map.get("RANK"))));
			manu_vo.setWARNING(ObjectUtils.toString(map.get("WARNING")));
			dam.create(manu_vo);
		}
		
		this.sendRtnObject(null);
	}
	
	public void create(Object body, IPrimitiveMap header) throws Exception {
		FPS941InputVO inputVO = (FPS941InputVO) body;
		dam = this.getDataAccessManager();
		
		// TBFPS_RPT_PARA_HEAD
		String seq_main = new SimpleDateFormat("yyyyMM").format(new Date()) + StringUtils.leftPad(getSN("MAIN"), 4, "0");
		TBFPS_RPT_PARA_HEADVO vo_main = new TBFPS_RPT_PARA_HEADVO();
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
		
		this.sendRtnObject(null);
	}
	
	public void add(Object body, IPrimitiveMap header) throws Exception {
		FPS941InputVO inputVO = (FPS941InputVO) body;
		WorkStation ws = DataManager.getWorkStation(uuid);
		dam = this.getDataAccessManager();
		
		// TBFPS_RPT_PARA_HEAD
		TBFPS_RPT_PARA_HEADVO head_vo = (TBFPS_RPT_PARA_HEADVO) dam.findByPKey(TBFPS_RPT_PARA_HEADVO.TABLE_UID, inputVO.getParam_no());
		if (head_vo != null) {
			head_vo.setSTATUS("S");
			head_vo.setEFFECT_START_DATE(new Timestamp(inputVO.getDate().getTime()));
			head_vo.setCreator(ws.getUser().getUserID());
			// picture
			if(StringUtils.isBlank(inputVO.getExipicture())) {
				head_vo.setRPT_PIC(null);
				head_vo.setRPT_PIC_NAME(null);
			}	
			else if(StringUtils.isNotBlank(inputVO.getPictureName())) {
				String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				byte[] data = Files.readAllBytes(scalePNG(tempPath, inputVO.getPictureName()).toPath());
				head_vo.setRPT_PIC(ObjectUtil.byteArrToBlob(data));
				head_vo.setRPT_PIC_NAME(inputVO.getRealpictureName());
			}
			dam.update(head_vo);
		} else
			throw new APException("ehl_01_common_001");
		
		// del first
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("DELETE FROM TBFPS_OTHER_PARA_MANUAL WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		dam.exeUpdate(queryCondition);
		// TBFPS_OTHER_PARA_MANUAL
		for(Map<String, Object> map : inputVO.getManualfList()) {
			String seq2 = getSN("MANUAL");
			TBFPS_OTHER_PARA_MANUALVO manu_vo = new TBFPS_OTHER_PARA_MANUALVO();
			manu_vo.setSEQ(new BigDecimal(seq2));
			manu_vo.setPARAM_NO(inputVO.getParam_no());
			manu_vo.setFPS_TYPE(ObjectUtils.toString(map.get("FPS_TYPE")));
			manu_vo.setDESC_TYPE(ObjectUtils.toString(map.get("DESC_TYPE")));
			manu_vo.setCONTENT(ObjectUtils.toString(map.get("CONTENT")));
			dam.create(manu_vo);
		}
		for(Map<String, Object> map : inputVO.getManualmList()) {
			String seq2 = getSN("MANUAL");
			TBFPS_OTHER_PARA_MANUALVO manu_vo = new TBFPS_OTHER_PARA_MANUALVO();
			manu_vo.setSEQ(new BigDecimal(seq2));
			manu_vo.setPARAM_NO(inputVO.getParam_no());
			manu_vo.setFPS_TYPE(ObjectUtils.toString(map.get("FPS_TYPE")));
			manu_vo.setDESC_TYPE(ObjectUtils.toString(map.get("DESC_TYPE")));
			manu_vo.setFONT(ObjectUtils.toString(map.get("FONT")) + ObjectUtils.toString(map.get("FONT_COLOR")));
			manu_vo.setRANK(new BigDecimal(ObjectUtils.toString(map.get("RANK"))));
			manu_vo.setCONTENT(ObjectUtils.toString(map.get("CONTENT")));
			dam.create(manu_vo);
		}
		// del first
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("DELETE FROM TBFPS_OTHER_PARA_WARNING WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		dam.exeUpdate(queryCondition);
		// TBFPS_OTHER_PARA_WARNING
		for(Map<String, Object> map : inputVO.getWarningList()) {
			String seq2 = getSN("WARNING");
			TBFPS_OTHER_PARA_WARNINGVO manu_vo = new TBFPS_OTHER_PARA_WARNINGVO();
			manu_vo.setSEQ(new BigDecimal(seq2));
			manu_vo.setPARAM_NO(inputVO.getParam_no());
			manu_vo.setPRD_TYPE(ObjectUtils.toString(map.get("PRD_TYPE")));
			manu_vo.setFONT(ObjectUtils.toString(map.get("FONT")) + ObjectUtils.toString(map.get("FONT_COLOR")));
			manu_vo.setRANK(new BigDecimal(ObjectUtils.toString(map.get("RANK"))));
			manu_vo.setWARNING(ObjectUtils.toString(map.get("WARNING")));
			dam.create(manu_vo);
		}
		
		this.sendRtnObject(null);
	}
	
	private File scalePNG(String tempPath, String pictureName) throws IOException {
		File srcFile = new File(tempPath, pictureName);
		int newWidth = 0;
		int newHeight = 0;

		int oriWidth = 0;
		int oriHeight = 0;

		double scale = 0;
		try {
			BufferedImage srcImage = ImageIO.read(srcFile);
			oriWidth = srcImage.getWidth();
			oriHeight = srcImage.getHeight();
			logger.info("*FPS941  scalePNG oriWidth+oriHeight:  " + oriWidth + "   " + oriHeight);
		} catch (IOException e) {
			logger.info("*FPS941  scalePNG 失敗 回傳srcFile");
			return srcFile;
		}
		if (oriWidth < 842) {
			return srcFile;
		}
		scale = oriWidth / 840.0;
		newWidth = (int) (oriWidth / scale);
		newHeight = (int) (oriHeight / scale);
		// System.out.println("scale:"+scale);
		// System.out.println("newWidth:"+newWidth);
		// System.out.println("newHeight:"+newHeight);
        try {
        	Image src = javax.imageio.ImageIO.read(srcFile);
        	logger.info("**FPS941  scalePNG newWidth+newHeight:  " + newWidth + "   " + newHeight);
    		BufferedImage tag = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
    		tag.getGraphics().drawImage(src.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);

    		ImageIO.write(tag, "png", new File(tempPath, pictureName));
    		logger.info("**FPS941  scalePNG  結束");
        	
        } catch (IOException e) {
        	logger.info("**FPS941  scalePNG 失敗 回傳srcFile");
			return srcFile;
		}
		
		return new File(tempPath, pictureName);
	}

	public void delete(Object body, IPrimitiveMap header) throws Exception {
		FPS941InputVO inputVO = (FPS941InputVO) body;
		dam = this.getDataAccessManager();
		
		TBFPS_RPT_PARA_HEADVO vo = (TBFPS_RPT_PARA_HEADVO) dam.findByPKey(TBFPS_RPT_PARA_HEADVO.TABLE_UID, inputVO.getParam_no());
		if (vo != null)
			dam.delete(vo);
		else
			throw new APException("ehl_01_common_001");
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("DELETE FROM TBFPS_OTHER_PARA_MANUAL WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		dam.exeUpdate(queryCondition);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("DELETE FROM TBFPS_OTHER_PARA_WARNING WHERE PARAM_NO = :param_no");
		queryCondition.setObject("param_no", inputVO.getParam_no());
		dam.exeUpdate(queryCondition);
		
		this.sendRtnObject(null);
	}
	
	public void goReview(Object body, IPrimitiveMap header) throws Exception {
		FPS941InputVO inputVO = (FPS941InputVO) body;
		dam = this.getDataAccessManager();
		
		this.add(body, header);
		
		TBFPS_RPT_PARA_HEADVO vo = (TBFPS_RPT_PARA_HEADVO) dam.findByPKey(TBFPS_RPT_PARA_HEADVO.TABLE_UID, inputVO.getParam_no());
		if (vo != null) {
			vo.setSTATUS("P");
			dam.update(vo);
		} else
			throw new APException("ehl_01_common_001");
		
		this.sendRtnObject(null);
	}
	
	public void review(Object body, IPrimitiveMap header) throws Exception {
		FPS941InputVO inputVO = (FPS941InputVO) body;
		dam = this.getDataAccessManager();
		
		TBFPS_RPT_PARA_HEADVO vo = (TBFPS_RPT_PARA_HEADVO) dam.findByPKey(TBFPS_RPT_PARA_HEADVO.TABLE_UID, inputVO.getParam_no());
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
					ans = sn.getNextSerialNumber("TBFPS_RPT_PARA_HEAD");
				}
				catch(Exception e) {
					sn.createNewSerial("TBFPS_RPT_PARA_HEAD", "0000", 1, "m", new Timestamp(System.currentTimeMillis()), 1, new Long("9999"), "y", new Long("0"), null);
					ans = sn.getNextSerialNumber("TBFPS_RPT_PARA_HEAD");
				}
				break;
			case "MANUAL":
				QueryConditionIF qc_man = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				qc_man.setQueryString("SELECT SQ_TBFPS_OTHER_PARA_MANUAL.nextval AS SEQ FROM DUAL");
				List<Map<String, Object>> man_list = dam.exeQuery(qc_man);
				ans = ObjectUtils.toString(man_list.get(0).get("SEQ"));
				break;
			case "WARNING":
				QueryConditionIF qc_warn = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				qc_warn.setQueryString("SELECT SQ_TBFPS_OTHER_PARA_WARNING.nextval AS SEQ FROM DUAL");
				List<Map<String, Object>> warn_list = dam.exeQuery(qc_warn);
				ans = ObjectUtils.toString(warn_list.get(0).get("SEQ"));
				break;
		}
		return ans;
	}
	
	
	
	
}