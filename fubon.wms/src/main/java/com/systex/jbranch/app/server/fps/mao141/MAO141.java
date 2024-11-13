package com.systex.jbranch.app.server.fps.mao141;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBMAO_DEVICE_GPSVO;
import com.systex.jbranch.app.common.fps.table.TBMAO_DEV_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBMAO_DEV_MGMT_LOGVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/07/21
 * 
 *       2016/11/15 modify by ocean
 */
@Component("mao141")
@Scope("request")
public class MAO141 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmmgrMap = xmlInfo.doGetVariable("FUBONSYS.UHRMMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> uhrmBMmgrMap = xmlInfo.doGetVariable("FUBONSYS.UHRMBMMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2);
		
		MAO141InputVO inputVO = (MAO141InputVO) body;
		MAO141OutputVO outputVO = new MAO141OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("WITH BASE AS ( ");
		sql.append("  SELECT A.FLAG, ");
		sql.append("	     A.SEQ, ");
		sql.append("	     A.DEV_NBR, ");
		sql.append("	     A.DC_NBR, ");
		sql.append("	     A.OP_NBR, ");
		sql.append("	     A.BRA_NBR, ");
		sql.append("	     B.LATITUDE AS POS_LONG, ");
		sql.append("	     B.LONGITUDE AS POS_LATI, ");
		sql.append("	     A.DEV_STATUS, ");
		sql.append("	     B.VERSION, ");
		sql.append("	     A.CREATOR, B.CREATETIME, B.MODIFIER, B.LASTUPDATE, ");
		sql.append("	     A.DEV_SITE_TYPE, ");
		sql.append("	     A.DEV_TAKE_EMP ");
		sql.append("  FROM ( ");
		sql.append("    SELECT SEQ, ");
		sql.append("	       DC_NBR, ");
		sql.append("	       OP_NBR, ");
		sql.append("	       BRA_NBR, ");
		sql.append("	       DEV_NBR, ");
		sql.append("	       DEV_STATUS, ");
		sql.append("	       CREATOR, ");
		sql.append("	       MAX(LASTUPDATE) AS LASTUPDATE, ");
		sql.append("	       DEV_SITE_TYPE, ");
		sql.append("	       DEV_TAKE_EMP, ");
		sql.append("	       FLAG ");
		sql.append("    FROM TBMAO_DEV_MAST ");
		sql.append("    WHERE 1 = 1 ");
		
		if (!StringUtils.isBlank(inputVO.getDev_status())) {
			sql.append("AND DEV_STATUS = :dev_status ");
			queryCondition.setObject("dev_status", inputVO.getDev_status());
		}
		
		if (!StringUtils.isBlank(inputVO.getDev_site_type())) {
			sql.append("AND DEV_SITE_TYPE = :dev_site_type ");
			queryCondition.setObject("dev_site_type", inputVO.getDev_site_type());
		}
		
		sql.append("    GROUP BY SEQ, DC_NBR, OP_NBR, BRA_NBR, DEV_NBR, DEV_NBR, DEV_STATUS, CREATOR, DEV_SITE_TYPE, DEV_TAKE_EMP, FLAG ");
		sql.append("  ) A ");
		sql.append("  LEFT JOIN ( ");
		sql.append("    SELECT DEVICE_ID, ");
		sql.append("	       LATITUDE, ");
		sql.append("	       LONGITUDE, ");
		sql.append("	       MODILE_FLAG, ");
		sql.append("	       VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
		sql.append("    FROM TBMAO_DEVICE_GPS ");
		sql.append("    WHERE (DEVICE_ID, SEQNO) IN ( ");
		sql.append("      SELECT DEVICE_ID, MAX(SEQNO) AS SEQNO ");
		sql.append("      FROM TBMAO_DEVICE_GPS ");
		sql.append("      WHERE 1 = 1 ");
		sql.append("      AND DEVICE_ID IS NOT NULL ");
		sql.append("      GROUP BY DEVICE_ID ");
		sql.append("    ) ");
		sql.append("  ) B ON A.DEV_NBR = B.DEVICE_ID ");
		sql.append(") ");

		sql.append("SELECT M.SEQ, ");
		sql.append("	   M.DC_NBR, ");
		sql.append("	   DEFN.REGION_CENTER_NAME, ");
		sql.append("	   M.OP_NBR, ");
		sql.append("	   DEFN.BRANCH_AREA_NAME, ");
		sql.append("	   M.BRA_NBR, ");
		sql.append("	   DEFN.BRANCH_NAME, ");
		sql.append("	   E.AO_CODE, ");
		sql.append("	   E.EMP_NAME, ");
		sql.append("	   E.EMP_ID, ");
		sql.append("	   M.CREATOR, ");
		sql.append("	   M.DEV_NBR, ");
		sql.append("	   M.POS_LONG, ");
		sql.append("	   M.POS_LATI, ");
		sql.append("	   M.LASTUPDATE, ");
		sql.append("	   M.DEV_STATUS, ");
		sql.append("	   M.DEV_SITE_TYPE, ");
		sql.append("	   M.DEV_TAKE_EMP, ");
		sql.append("	   F.EMP_NAME AS KEEPER_NAME, ");
		sql.append("	   M.FLAG ");
		sql.append("FROM BASE M ");
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT DEV_NBR, ");
		sql.append("		 DEV_STATUS, ");
		sql.append("		 APL_EMP_ID, ");
		sql.append("		 RPAD(TO_CHAR(USE_DATE, 'yyyyMMdd') || USE_PERIOD_S_TIME || '00', 14, '0') AS START_DATETIME, ");
		sql.append("		 CASE WHEN USE_PERIOD_E_TIME < USE_PERIOD_S_TIME THEN RPAD(TO_CHAR(USE_DATE + 1, 'yyyyMMdd')  || USE_PERIOD_E_TIME || '00', 14, '0') ELSE RPAD(TO_CHAR(USE_DATE, 'yyyyMMdd') || USE_PERIOD_E_TIME || '00', 14, '0') END AS END_DATETIME ");
		sql.append("  FROM TBMAO_DEV_APL_PLIST ");
		sql.append(") P ON M.DEV_NBR = P.DEV_NBR ");
		sql.append("AND TO_CHAR(CURRENT_TIMESTAMP, 'yyyyMMddHH24MISS') BETWEEN P.START_DATETIME AND P.END_DATETIME ");
		sql.append("AND P.DEV_STATUS IN ('D06', 'E07', 'C05') ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO E ON P.APL_EMP_ID = E.EMP_ID ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO F ON M.DEV_TAKE_EMP = F.EMP_ID ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO DEFN ON M.BRA_NBR = DEFN.BRANCH_NBR ");
		sql.append("WHERE 1 = 1 ");

		if (uhrmmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
			uhrmBMmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
			uhrmMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			if (StringUtils.isNotBlank(inputVO.getBra_areaID()) && !"null".equals(inputVO.getBra_areaID())) {
				sql.append("AND M.OP_NBR = :branchAreaID "); //營運區代碼
				queryCondition.setObject("branchAreaID", inputVO.getBra_areaID());
			} else if (StringUtils.isNotBlank(inputVO.getRegionCenterId()) && !"null".equals(inputVO.getBra_areaID())) {
				sql.append("AND M.DC_NBR = :regionCenterID "); //區域代碼
				queryCondition.setObject("regionCenterID", inputVO.getRegionCenterId());
			}
		} else {
			if (StringUtils.isNotBlank(inputVO.getBranchNbr()) && !"null".equals(inputVO.getBranchNbr())) {
				sql.append("AND M.BRA_NBR = :branchID "); //分行代碼
				queryCondition.setObject("branchID", inputVO.getBranchNbr());
			} else if (StringUtils.isNotBlank(inputVO.getBra_areaID()) && !"null".equals(inputVO.getBra_areaID())) {
				sql.append("AND M.OP_NBR = :branchAreaID "); //營運區代碼
				queryCondition.setObject("branchAreaID", inputVO.getBra_areaID());
			} else if (StringUtils.isNotBlank(inputVO.getRegionCenterId()) && !"null".equals(inputVO.getBra_areaID())) {
				sql.append("AND M.DC_NBR = :regionCenterID "); //區域代碼
				queryCondition.setObject("regionCenterID", inputVO.getRegionCenterId());
			} else {
				sql.append("AND M.BRA_NBR IN (:branchIDList) ");
				queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
		}

		if (!StringUtils.isBlank(inputVO.getDev_nbr())) {
			sql.append("and M.DEV_NBR = :dev_nbr ");
			queryCondition.setObject("dev_nbr", inputVO.getDev_nbr());
		}

		queryCondition.setQueryString(sql.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}

	public void add(Object body, IPrimitiveMap header) throws JBranchException {

		MAO141InputVO inputVO = (MAO141InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COUNT(SEQ) AS COUNTS FROM TBMAO_DEV_MAST WHERE DEV_NBR = :devNbr ");
		queryCondition.setObject("devNbr", inputVO.getDev_nbr());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		if (((BigDecimal) list.get(0).get("COUNTS")).compareTo(BigDecimal.ZERO) <= 0) {

			TBMAO_DEV_MASTVO vo = new TBMAO_DEV_MASTVO();
			vo.setSEQ(getSEQ());
			vo.setDEV_NBR(inputVO.getDev_nbr());
			vo.setDC_NBR(inputVO.getRegionCenterId());
			vo.setOP_NBR(inputVO.getBra_areaID());
			vo.setBRA_NBR(inputVO.getBranchNbr());
			vo.setDEV_STATUS("N");
			vo.setDEV_SITE_TYPE(inputVO.getDev_site_type());
			vo.setFLAG(inputVO.getFlag());

			dam.create(vo);

			TBMAO_DEV_MGMT_LOGVO vos = new TBMAO_DEV_MGMT_LOGVO();
			vos.setSEQ(getSEQ_LOG());
			vos.setDEV_NBR(inputVO.getDev_nbr());
			vos.setDC_NBR(inputVO.getRegionCenterId());
			vos.setOP_NBR(inputVO.getBra_areaID());
			vos.setBRA_NBR(inputVO.getBranchNbr());
			vos.setDEV_STATUS("N");
			vos.setDEV_SITE_TYPE(inputVO.getDev_site_type());
			vos.setFLAG(inputVO.getFlag());
			
			dam.create(vos);
		} else {
			throw new APException("已存在相同設備編號");
		}

		this.sendRtnObject(null);
	}

	public void delete(Object body, IPrimitiveMap header) throws JBranchException {
		
		MAO141InputVO inputVO = (MAO141InputVO) body;
		dam = this.getDataAccessManager();

		for (Map<String, Object> map : inputVO.getChkSEQ()) {
			TBMAO_DEV_MASTVO vo = new TBMAO_DEV_MASTVO();
			vo = (TBMAO_DEV_MASTVO) dam.findByPKey(TBMAO_DEV_MASTVO.TABLE_UID, map.get("SEQ").toString());
			
			if (vo != null) {
				vo.setDEV_STATUS("D");
				dam.update(vo);
			} else {
				throw new APException("ehl_01_common_008");
			}

			String SEQ_LOG = getSEQ_LOG();

			TBMAO_DEV_MGMT_LOGVO vos = new TBMAO_DEV_MGMT_LOGVO();
			vos.setSEQ(SEQ_LOG);
			vos.setDEV_NBR(map.get("DEV_NBR").toString());
			vos.setDC_NBR(map.get("DC_NBR").toString());
			vos.setOP_NBR(map.get("OP_NBR").toString());
			vos.setBRA_NBR(map.get("BRA_NBR").toString());
			vos.setDEV_STATUS("D");
			vos.setFLAG(map.get("FLAG").toString());
			
			dam.create(vos);
		}
		
		this.sendRtnObject(null);
	}

	public void lost(Object body, IPrimitiveMap header) throws JBranchException {
		
		MAO141InputVO inputVO = (MAO141InputVO) body;
		dam = this.getDataAccessManager();

		for (Map<String, Object> map : inputVO.getChkSEQ()) {
			TBMAO_DEV_MASTVO vo = new TBMAO_DEV_MASTVO();
			vo = (TBMAO_DEV_MASTVO) dam.findByPKey(TBMAO_DEV_MASTVO.TABLE_UID, map.get("SEQ").toString());
			
			if (vo != null) {
				vo.setDEV_STATUS("L");
				dam.update(vo);
			} else {
				throw new APException("ehl_01_common_008");
			}

			String SEQ_LOG = getSEQ_LOG();

			TBMAO_DEV_MGMT_LOGVO vos = new TBMAO_DEV_MGMT_LOGVO();
			vos.setSEQ(SEQ_LOG);
			vos.setDEV_NBR(map.get("DEV_NBR").toString());
			vos.setDC_NBR(map.get("DC_NBR").toString());
			vos.setOP_NBR(map.get("OP_NBR").toString());
			vos.setBRA_NBR(map.get("BRA_NBR").toString());
			vos.setDEV_STATUS("L");
			vos.setFLAG(map.get("FLAG").toString());
			dam.create(vos);
		}
		
		this.sendRtnObject(null);
	}

	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		
		MAO141InputVO inputVO = (MAO141InputVO) body;
		dam = this.getDataAccessManager();

		for (Map<String, Object> map : inputVO.getChkSEQ()) {
			TBMAO_DEV_MASTVO vo = new TBMAO_DEV_MASTVO();
			vo = (TBMAO_DEV_MASTVO) dam.findByPKey(TBMAO_DEV_MASTVO.TABLE_UID, map.get("SEQ").toString());
			if (vo != null) {
				vo.setDEV_TAKE_EMP(ObjectUtils.toString(map.get("DEV_TAKE_EMP")));
				vo.setDEV_SITE_TYPE(ObjectUtils.toString(map.get("DEV_SITE_TYPE")));
				dam.update(vo);

			} else {
				throw new APException("ehl_01_common_008");
			}

			TBMAO_DEV_MGMT_LOGVO vos = new TBMAO_DEV_MGMT_LOGVO();
			vos.setSEQ(getSEQ_LOG());
			vos.setDEV_NBR(map.get("DEV_NBR").toString());
			vos.setDC_NBR(map.get("DC_NBR").toString());
			vos.setOP_NBR(map.get("OP_NBR").toString());
			vos.setBRA_NBR(map.get("BRA_NBR").toString());
			vos.setDEV_STATUS(ObjectUtils.toString(map.get("DEV_STATUS")));
			vos.setDEV_SITE_TYPE(ObjectUtils.toString(map.get("DEV_SITE_TYPE")));
			vos.setDEV_TAKE_EMP(ObjectUtils.toString(map.get("DEV_TAKE_EMP")));
			vos.setFLAG(map.get("FLAG").toString());
			
			dam.create(vos);
		}
		
		this.sendRtnObject(null);
	}

	public void showName(Object body, IPrimitiveMap header) throws JBranchException {
		
		MAO141InputVO inputVO = (MAO141InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT EMP_NAME ");
		sb.append("FROM VWORG_BRANCH_EMP_DETAIL_INFO ");
		sb.append("WHERE EMP_ID = :empId ");
		
		queryCondition.setObject("empId", inputVO.getDev_take_emp());
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> empNameList = dam.exeQuery(queryCondition);
		
		this.sendRtnObject(ObjectUtils.toString(empNameList.size() != 0 ? empNameList.get(0).get("EMP_NAME") : null));
	}

	public void export(Object body, IPrimitiveMap header) throws JBranchException {

		MAO141InputVO inputVO = (MAO141InputVO) body;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "設備管理_" + sdf.format(new Date()) + ".csv";
		List listCSV = new ArrayList();
		Map<String, String> gmDevStatusMap = new XmlInfo().doGetVariable("MAO.DEV_STATUS", FormatHelper.FORMAT_3);
		Map<String, String> gmDevSiteTypeMap = new XmlInfo().doGetVariable("MAO.DEV_SITE_TYPE", FormatHelper.FORMAT_3);
		String[] csvHeader = { "業務處", "營運區", "分行別", "使用人", "員編", "建立人", "設備編號", "裝置保管人", "使用地點", "座標", "資料更新日期", "狀態" };
		String[] csvMain = { "DC_NBR", "OP_NBR", "BRA_NBR", "AO_CODE", "EMP_ID", "CREATOR", "DEV_NBR", "DEV_TAKE_EMP", "DEV_SITE_TYPE", "POS", "LASTUPDATE", "DEV_STATUS" };

		for (Map<String, Object> map : inputVO.getChkSEQ()) {
			String[] records = new String[csvMain.length];
			for (int i = 0; i < csvMain.length; i++) {
				switch (csvMain[i]) {
					case "AO_CODE":
						records[i] = checkIsNull(map, csvMain[i]) + (StringUtils.isNotBlank(checkIsNull(map, "EMP_NAME")) ? "-" : "") + checkIsNull(map, "EMP_NAME");
						break;
					case "POS":
						records[i] = checkIsNull(map, "POS_LONG") + checkIsNull(map, "POS_LATI");
						break;
					case "DEV_STATUS":
						records[i] = gmDevStatusMap.get(checkIsNull(map, csvMain[i]));
						break;
					case "DEV_SITE_TYPE":
						records[i] = gmDevSiteTypeMap.get(checkIsNull(map, csvMain[i]));
						break;
					default:
						records[i] = checkIsNull(map, csvMain[i]);
						break;
				}
			}

			listCSV.add(records);
		}

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		
		String url = csv.generateCSV();

		notifyClientToDownloadFile(url, fileName);

		this.sendRtnObject(null);
	}

	public void checkAuthForSave(Object body, IPrimitiveMap header) throws JBranchException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT COUNT(1) AS CNT ");
		sb.append("FROM TBORG_MEMBER_ROLE ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND EMP_ID = :empId ");
		sb.append("AND ROLE_ID IN ( ");
		sb.append("  SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID in('045','046','061') ");
		sb.append(") ");
		
		queryCondition.setObject("empId", (String) getCommonVariable(SystemVariableConsts.LOGINID));
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		this.sendRtnObject((BigDecimal) list.get(0).get("CNT"));
	}

	public Boolean insertDeviceGPS(Map<String, String> map) throws JBranchException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		// get seqNO
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		String qrySeq = "SELECT SQ_TBMAO_DEVICE_GPS.nextval AS SEQNO FROM DUAL";
		
		condition.setQueryString(qrySeq);
		
		List<Map<String, Object>> list = dam.exeQuery(condition);
		
		BigDecimal seqNo = (BigDecimal) list.get(0).get("SEQNO");

		TBMAO_DEVICE_GPSVO vo = new TBMAO_DEVICE_GPSVO();
		vo.setSEQNO(seqNo);
		vo.setDEVICE_ID(map.get("deviceID"));
		vo.setLATITUDE(map.get("latitude"));
		vo.setLONGITUDE(map.get("longitude"));
		vo.setMODILE_FLAG(map.get("mobileFlag"));
		dam.create(vo);

		// 2017/8/25
		Set<String> mobileList = new HashSet<String>();
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString("SELECT P.USE_DATE, P.USE_PERIOD, P.DEV_NBR, P.DEV_STATUS FROM TBMAO_DEV_APL_PLIST P WHERE 1 = 1 AND TRUNC(P.USE_DATE) = TRUNC(SYSDATE) AND P.DEV_STATUS = 'A01'");
		
		List<Map<String, Object>> do_list = dam.exeQuery(condition);
		
		for (Map<String, Object> romap : do_list) {
			mobileList.add(ObjectUtils.toString(romap.get("DEV_NBR")));
		}
		
		Boolean ans;
		if (mobileList.contains(ObjectUtils.toString(map.get("deviceID"))))
			ans = true;
		else
			ans = false;

		return ans;
	}

	private String checkIsNull(Map map, String key) {
		
		if (null != map.get(key) && StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	private String getSEQ() throws JBranchException {
		
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		
		try {
			seqNum = sn.getNextSerialNumber("MAO141");
		} catch (Exception e) {
			sn.createNewSerial("MAO141", "00000", null, null, null, 1, new Long("99999"), "y", new Long("0"), null);
			seqNum = sn.getNextSerialNumber("MAO141");
		}
		
		return seqNum;
	}

	private String getSEQ_LOG() throws JBranchException {
		
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		
		try {
			seqNum = sn.getNextSerialNumber("MAO141_LOG");
		} catch (Exception e) {
			sn.createNewSerial("MAO141_LOG", "00000", null, null, null, 1, new Long("99999"), "y", new Long("0"), null);
			seqNum = sn.getNextSerialNumber("MAO141_LOG");
		}
		
		return seqNum;
	}
}