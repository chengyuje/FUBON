package com.systex.jbranch.app.server.fps.mao241;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("mao241")
@Scope("request")
public class MAO241 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;

	public void inquire (Object body, IPrimitiveMap header) throws JBranchException {

		MAO241InputVO inputVO = (MAO241InputVO) body;
		MAO241OutputVO outputVO = new MAO241OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("WITH BASE AS ( ");
		sql.append("  SELECT A.FLAG, ");
		sql.append("         A.SEQ, ");
		sql.append("         A.DEV_NBR, ");
		sql.append("         B.LATITUDE AS POS_LONG, ");
		sql.append("         B.LONGITUDE AS POS_LATI, ");
		sql.append("         A.DEV_STATUS, ");
		sql.append("         B.VERSION, ");
		sql.append("         A.CREATOR, ");
		sql.append("         B.CREATETIME, ");
		sql.append("         B.MODIFIER, ");
		sql.append("         B.LASTUPDATE, ");
		sql.append("         A.DEV_SITE_TYPE, ");
		sql.append("         A.DEV_TAKE_EMP ");
		sql.append("  FROM ( ");
		sql.append("    SELECT SEQ, ");
		sql.append("           DEV_NBR, ");
		sql.append("           DEV_STATUS, ");
		sql.append("           CREATOR, ");
		sql.append("           MAX(LASTUPDATE) AS LASTUPDATE, ");
		sql.append("           DEV_SITE_TYPE, ");
		sql.append("           DEV_TAKE_EMP, ");
		sql.append("           FLAG ");
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
		sql.append("    GROUP BY SEQ, DEV_NBR, DEV_NBR, DEV_STATUS, CREATOR, DEV_SITE_TYPE, DEV_TAKE_EMP, FLAG ");
		sql.append("  ) A ");
		sql.append("  LEFT JOIN ( ");
		sql.append("    SELECT DEVICE_ID, ");
		sql.append("           LATITUDE, ");
		sql.append("           LONGITUDE, ");
		sql.append("           MODILE_FLAG, ");
		sql.append("           VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
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
		sql.append("       P.APL_EMP_ID, ");
		sql.append("       E.EMP_NAME, ");
		sql.append("       E.EMP_ID, ");
		sql.append("       M.CREATOR, ");
		sql.append("       M.DEV_NBR, ");
		sql.append("       M.POS_LONG, ");
		sql.append("       M.POS_LATI, ");
		sql.append("       M.LASTUPDATE, ");
		sql.append("       M.DEV_STATUS, ");
		sql.append("       M.DEV_SITE_TYPE, ");
		sql.append("       M.DEV_TAKE_EMP, ");
		sql.append("       F.EMP_NAME AS KEEPER_NAME ");
		sql.append("FROM BASE M ");
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT DEV_NBR, DEV_STATUS, APL_EMP_ID,  ");
		sql.append("		 RPAD(TO_CHAR(USE_DATE, 'yyyyMMdd')  || USE_PERIOD_S_TIME || '00', 14, '0') AS START_DATETIME, ");
		sql.append("		 CASE WHEN USE_PERIOD_E_TIME < USE_PERIOD_S_TIME THEN RPAD(TO_CHAR(USE_DATE + 1, 'yyyyMMdd')  || USE_PERIOD_E_TIME || '00', 14, '0') ");
		sql.append("		 ELSE RPAD(TO_CHAR(USE_DATE, 'yyyyMMdd')  || USE_PERIOD_E_TIME || '00', 14, '0') END AS END_DATETIME ");
		sql.append("  FROM TBMAO_DEV_APL_PLIST ");
		sql.append(") P ON M.DEV_NBR = P.DEV_NBR ");
		sql.append("AND TO_CHAR(CURRENT_TIMESTAMP, 'yyyyMMddHH24MISS') BETWEEN P.START_DATETIME AND P.END_DATETIME ");
		sql.append("AND P.DEV_STATUS IN ('D06', 'E07', 'C05')  ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO E ON P.APL_EMP_ID = E.EMP_ID ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO F ON M.DEV_TAKE_EMP = F.EMP_ID ");
		sql.append("WHERE 1 = 1   ");
		sql.append("AND M.FLAG = 'U' ");

		if (!StringUtils.isBlank(inputVO.getDev_nbr())) {
			sql.append("and M.DEV_NBR = :dev_nbr ");
			queryCondition.setObject("dev_nbr", inputVO.getDev_nbr());
		}

		queryCondition.setQueryString(sql.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);

	}

	public void add (Object body, IPrimitiveMap header) throws JBranchException {
		
		MAO241InputVO inputVO = (MAO241InputVO) body;
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
			vo.setDEV_STATUS("N");
			vo.setDEV_SITE_TYPE(inputVO.getDev_site_type());
			vo.setFLAG("U");
			
			dam.create(vo);
			
			TBMAO_DEV_MGMT_LOGVO vos = new TBMAO_DEV_MGMT_LOGVO();
			vos.setSEQ(getSEQ_LOG());
			vos.setDEV_NBR(inputVO.getDev_nbr());
			vos.setDEV_STATUS("N");
			vos.setDEV_SITE_TYPE(inputVO.getDev_site_type());
			vos.setFLAG("U");
			dam.create(vos);
		} else {
			throw new APException("已存在相同設備編號");
		}
		
		this.sendRtnObject(null);
	}

	public void delete (Object body, IPrimitiveMap header) throws JBranchException {
		
		MAO241InputVO inputVO = (MAO241InputVO) body;
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
			vos.setDEV_STATUS("D");
			vos.setFLAG("U");
			dam.create(vos);
		}
		this.sendRtnObject(null);
	}

	public void lost (Object body, IPrimitiveMap header) throws JBranchException {
		
		MAO241InputVO inputVO = (MAO241InputVO) body;
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
			vos.setDEV_STATUS("L");
			vos.setFLAG("U");
			dam.create(vos);
		}
		this.sendRtnObject(null);
	}

	public void save (Object body, IPrimitiveMap header) throws JBranchException {
		
		MAO241InputVO inputVO = (MAO241InputVO) body;
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

			String SEQ_LOG = getSEQ_LOG();

			TBMAO_DEV_MGMT_LOGVO vos = new TBMAO_DEV_MGMT_LOGVO();
			vos.setSEQ(SEQ_LOG);
			vos.setDEV_NBR(map.get("DEV_NBR").toString());
			vos.setDEV_STATUS(ObjectUtils.toString(map.get("DEV_STATUS")));
			vos.setDEV_SITE_TYPE(ObjectUtils.toString(map.get("DEV_SITE_TYPE")));
			vos.setDEV_TAKE_EMP(ObjectUtils.toString(map.get("DEV_TAKE_EMP")));
			vos.setFLAG("U");
			dam.create(vos);

		}
		this.sendRtnObject(null);
	}

	public void showName (Object body, IPrimitiveMap header) throws JBranchException {
		
		MAO241InputVO inputVO = (MAO241InputVO) body;
		dam = this.getDataAccessManager();

		//取得理專姓名
		List<Map<String, Object>> empNameList = exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder().append(" SELECT EMP_NAME FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE EMP_ID = :empId ").toString()).setObject("empId", inputVO.getDev_take_emp()));

		this.sendRtnObject(ObjectUtils.toString(empNameList.size() != 0 ? empNameList.get(0).get("EMP_NAME") : null));
	}

	public void export (Object body, IPrimitiveMap header) throws JBranchException {

		MAO241InputVO inputVO = (MAO241InputVO) body;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "設備管理_" + sdf.format(new Date()) + ".csv";
		List listCSV = new ArrayList();
		Map<String, String> gmDevStatusMap = new XmlInfo().doGetVariable("MAO.DEV_STATUS", FormatHelper.FORMAT_3);
		Map<String, String> gmDevSiteTypeMap = new XmlInfo().doGetVariable("MAO.DEV_SITE_TYPE", FormatHelper.FORMAT_3);
		String[] csvHeader = {"使用人", "員編", "建立人", "設備編號", "裝置保管人", "使用地點", "座標", "資料更新日期", "狀態" };
		String[] csvMain = {"AO_CODE", "EMP_ID", "CREATOR", "DEV_NBR", "DEV_TAKE_EMP", "DEV_SITE_TYPE", "POS", "LASTUPDATE", "DEV_STATUS" };

		for (Map<String, Object> map : inputVO.getChkSEQ()) {
			String[] records = new String[csvMain.length];
			for (int i = 0; i < csvMain.length; i++) {
				if (StringUtils.equals("AO_CODE", csvMain[i])) {
					records[i] = checkIsNull(map, "AO_CODE") + (StringUtils.isNotBlank(checkIsNull(map, "EMP_NAME")) ? "-" : "") + checkIsNull(map, "EMP_NAME");
				} else if (StringUtils.equals("POS", csvMain[i])) {
					records[i] = checkIsNull(map, "POS_LONG") + checkIsNull(map, "POS_LATI");
				} else if (StringUtils.equals("DEV_STATUS", csvMain[i])) {
					records[i] = gmDevStatusMap.get(checkIsNull(map, "DEV_STATUS"));
				} else if (StringUtils.equals("DEV_SITE_TYPE", csvMain[i])) {
					records[i] = gmDevSiteTypeMap.get(checkIsNull(map, "DEV_SITE_TYPE"));
				} else {
					records[i] = checkIsNull(map, csvMain[i]);
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

	public void checkAuthForSave(Object body, IPrimitiveMap header) throws JBranchException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT COUNT(1) AS CNT ");
		sb.append("FROM TBORG_MEMBER_ROLE ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND EMP_ID = :empId ");
		sb.append("AND ROLE_ID IN ( ");
		sb.append("  SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID IN ('045', '046', '061')");
		sb.append(")");
		
		queryCondition.setObject("empId", (String) getCommonVariable(SystemVariableConsts.LOGINID));
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		this.sendRtnObject((BigDecimal)list.get(0).get("CNT"));

	}
}