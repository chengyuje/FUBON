package com.systex.jbranch.app.server.fps.pms413;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPMS_BRANCH_IPVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :分行電腦IP查詢與維護<br>
 * Comments Name : PMS413.java<br>
 * Author : Frank<br>
 * Date :2016/05/17 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月26日<br>
 */

@Component("pms413")
@Scope("request")
public class PMS413 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS413.class);

	 /*是否為總行人員*/
	public void querymember(Object body, IPrimitiveMap header)throws JBranchException{
		PMS413OutputVO outputVO = new PMS413OutputVO();
		try {
			String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
			Boolean x=true; 
			if(!headmgrMap.containsKey(roleID))
			   x=false;
			outputVO.setX(x);  
			sendRtnObject(outputVO);
		} catch (Exception e) {
			throw new APException("ehl_01_common_009");
		}
	
	}
	
	
	
	
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {
		 
		PMS413InputVO inputVO = (PMS413InputVO) body;
		PMS413OutputVO outputVO = new PMS413OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		ArrayList<String> sql_list = new ArrayList<String>();
		
		StringBuffer sql = new StringBuffer();
		sql.append("select BIP.SEQ, BIP.COM_TYPE, BIP.REGION_CENTER_NAME, ");
		sql.append("       BIP.REGION_CENTER_ID, BIP.OP_AREA_ID, ");
		sql.append("       BIP.OP_AREA_NAME, BIP.BRANCH_NBR, BIP.BRANCH_NAME, ");
		sql.append("       BIP.EMP_ID, BIP.EMP_NAME, BIP.IPADDRESS, ");
		sql.append("       BIP.CREATOR, MEMC.EMP_NAME AS EMP_NAME_C, ");
		sql.append("       to_char(BIP.CREATETIME, 'YYYY/MM/DD HH24:MI:SS') as CREATETIME, ");
		sql.append("       BIP.MODIFIER, MEMM.EMP_NAME AS EMP_NAME_M, ");
		sql.append("       to_char(BIP.LASTUPDATE, 'YYYY/MM/DD HH24:MI:SS') as LASTUPDATE ");
		sql.append("from TBPMS_BRANCH_IP BIP ");
		sql.append("left join TBORG_MEMBER MEMC on MEMC.EMP_ID = BIP.CREATOR ");
		sql.append("left join TBORG_MEMBER MEMM on MEMM.EMP_ID = BIP.MODIFIER ");
		sql.append("where 1=1 ");
		// 電腦類型
		if (StringUtils.isNotBlank(inputVO.getComType())) {
			sql.append("and BIP.COM_TYPE = ? ");
			sql_list.add(inputVO.getComType());
		}
		// IPAddress
		if (StringUtils.isNotBlank(inputVO.getIp())) {
			sql.append("and BIP.IPADDRESS = ? ");
			sql_list.add(inputVO.getIp());
		}
		// by Willis 20171018 此條件因為發現組織換區有異動(例如:東寧分行在正式環境10/1從西台南區換至東台南區)，跟之前組織對應會有問題，改為對應目前最新組織分行別
		// 分行
		if (StringUtils.isNotBlank(inputVO.getBr_id())) {
			sql.append("and BIP.BRANCH_NBR = ? ");
			sql_list.add(inputVO.getBr_id());
		// 營運區	
		}else if (StringUtils.isNotBlank(inputVO.getOp_id())){
			sql.append("and BIP.BRANCH_NBR in ( ");
			sql.append("select BRANCH_NBR from VWORG_DEFN_BRH where DEPT_ID = ? )");
			sql_list.add(inputVO.getOp_id());
	    // 區域中心	
		}else if (StringUtils.isNotBlank(inputVO.getRc_id())){
			sql.append("and BIP.BRANCH_NBR in ( ");
			sql.append("select BRANCH_NBR from VWORG_DEFN_BRH where DEPT_ID = ? )");
			sql_list.add(inputVO.getRc_id());	
		}
		
		// 營運區
//		else if (StringUtils.isNotBlank(inputVO.getOp_id())) {
//			sql.append("and BIP.OP_AREA_ID = ? ");
//			sql_list.add(inputVO.getOp_id());
//		}
//		// 區域中心
//		else if (StringUtils.isNotBlank(inputVO.getRc_id())) {
//			sql.append("and BIP.REGION_CENTER_ID = ? ");
//			sql_list.add(inputVO.getRc_id());
//		}

		// 員工
		if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
			if (StringUtils.equals(inputVO.getComType(), "2")) {
				sql.append("and BIP.EMP_ID = ? ");
				sql_list.add(inputVO.getEmp_id());
			}
		}

		sql.append("order by BIP.REGION_CENTER_ID,BIP.OP_AREA_ID,BIP.BRANCH_NBR,BIP.IPADDRESS ");
		condition.setQueryString(sql.toString());
		for (int sql_i = 0; sql_i < sql_list.size(); sql_i++) {
			condition.setString(sql_i + 1, sql_list.get(sql_i));
		}
		outputVO.setTotalList(dam.exeQuery(condition));
		ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		if (list.size() > 0) {
			int totalPage = list.getTotalPage();
			outputVO.setTotalPage(totalPage);
			outputVO.setResultList(list);
			outputVO.setTotalRecord(list.getTotalRecord());

			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}

	/** ======= 新增資料 ======== */
	public void addIP(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS413InputVO inputVO = (PMS413InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		Timestamp currentTM = new Timestamp(System.currentTimeMillis());

		StringBuffer sqlc = new StringBuffer();
		sqlc.append("SELECT * FROM TBPMS_BRANCH_IP ");
		sqlc.append("WHERE IPADDRESS = '" + inputVO.getIp() + "'");
		condition.setQueryString(sqlc.toString());
		int size = dam.exeQuery(condition).size();

		if (size > 0) {
			throw new APException("該IP Address 已存在不可重複！");
		} else {
			BigDecimal sequ = new BigDecimal(getSN());
			inputVO.setSeq(sequ);
			TBPMS_BRANCH_IPVO vo = new TBPMS_BRANCH_IPVO();
			vo.setSEQ(inputVO.getSeq());
			vo.setCOM_TYPE(inputVO.getComType());
			vo.setREGION_CENTER_ID(inputVO.getRc_id());
			vo.setREGION_CENTER_NAME(inputVO.getRc_name());
			vo.setOP_AREA_ID(inputVO.getOp_id());
			vo.setOP_AREA_NAME(inputVO.getOp_name());
			vo.setBRANCH_NBR(inputVO.getBr_id());
			vo.setBRANCH_NAME(inputVO.getBr_name());
			vo.setIPADDRESS(inputVO.getIp());
			vo.setCreatetime(currentTM);
			vo.setCreator((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			vo.setModifier((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			vo.setLastupdate(currentTM);
//			if (inputVO.getComType().equals("2")) {
				vo.setEMP_ID(inputVO.getEmp_id());
				vo.setEMP_NAME(inputVO.getEmp_name());

			dam.create(vo);
			this.sendRtnObject(null);
//		  }
		}
	}

	/** ======= 修改資料 ======== */
	public void updateIP(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS413InputVO inputVO = (PMS413InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		Integer size = 0;
		if (!StringUtils.equals(inputVO.getIp(), inputVO.getTempIP())) {
			StringBuffer sqlc = new StringBuffer();
			sqlc.append("SELECT SEQ, COM_TYPE, REGION_CENTER_ID, REGION_CENTER_NAME, OP_AREA_ID, OP_AREA_NAME, BRANCH_NBR, BRANCH_NAME, EMP_ID, EMP_NAME, IPADDRESS ");
			sqlc.append("FROM TBPMS_BRANCH_IP ");
			sqlc.append("WHERE IPADDRESS = :ip ");
			condition.setObject("ip", inputVO.getIp());
			condition.setQueryString(sqlc.toString());
			
			size = dam.exeQuery(condition).size();
		}
		
		if (size > 0) {
			throw new APException("該IP Address 已存在不可重複！");
		} else {
			TBPMS_BRANCH_IPVO vo = (TBPMS_BRANCH_IPVO) dam.findByPKey(TBPMS_BRANCH_IPVO.TABLE_UID, inputVO.getSeq());
			
			if (vo != null) {
				vo.setREGION_CENTER_ID(inputVO.getRc_id());
				vo.setREGION_CENTER_NAME(inputVO.getRc_name());
				vo.setOP_AREA_ID(inputVO.getOp_id());
				vo.setOP_AREA_NAME(inputVO.getOp_name());
				vo.setBRANCH_NBR(inputVO.getBr_id());
				vo.setBRANCH_NAME(inputVO.getBr_name());
				vo.setIPADDRESS(inputVO.getIp());

				if (inputVO.getComType().equals("2")) {
					vo.setEMP_ID(inputVO.getEmp_id());
					vo.setEMP_NAME(inputVO.getEmp_name());
				}
				dam.update(vo);
			} else {
				throw new APException("ehl_01_common_001"); // 顯示資料不存在
			}
		}
		
		this.sendRtnObject(null);
	}

	/** ====== 刪除資料 ======= */
	public void delData(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS413InputVO inputVO = (PMS413InputVO) body;
		dam = this.getDataAccessManager();

		TBPMS_BRANCH_IPVO vo = (TBPMS_BRANCH_IPVO) dam.findByPKey(
				TBPMS_BRANCH_IPVO.TABLE_UID, inputVO.getSeq());
		if (vo != null) {
			dam.delete(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		this.sendRtnObject(null);
	}

	/** === 產出CSV==== */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS413OutputVO outputVO = (PMS413OutputVO) body;

		List<Map<String, Object>> list = outputVO.getTotalList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "分行電腦IP查詢與維護_" + sdf.format(new Date()) + ".csv";
		List listCSV = new ArrayList();
		for (Map<String, Object> map : list) {
			String[] records = new String[9];
			int i = 0;
			records[i] = checkIsNull(map, "REGION_CENTER_NAME"); // 區部中心
			records[++i] = checkIsNull(map, "OP_AREA_NAME"); // 營運區
			records[++i] = checkIsNull(map, "BRANCH_NBR") + "-"
					+ checkIsNull(map, "BRANCH_NAME"); // 分行
			records[++i] = checkIsNull(map, "EMP_ID") + "-"
					+ checkIsNull(map, "EMP_NAME"); // 員工
			records[++i] = checkIsNull(map, "IPADDRESS"); // IP ADDRESS
			records[++i] = checkIsNull(map, "CREATOR") + "-"
					+ checkIsNull(map, "EMP_NAME_C"); // 建立人
			records[++i] = checkIsNull(map, "CREATETIME"); // 建立日期
			records[++i] = checkIsNull(map, "MODIFIER") + "-"
					+ checkIsNull(map, "EMP_NAME_M"); // 最後修改人
			records[++i] = checkIsNull(map, "LASTUPDATE"); // 最後修改時間

			listCSV.add(records);
		}
		// header
		String[] csvHeader = new String[9];
		int j = 0;
		csvHeader[j] = "業務處";
		csvHeader[++j] = "營運區";
		csvHeader[++j] = "分行";
		csvHeader[++j] = "員工";
		csvHeader[++j] = "IP Address";
		csvHeader[++j] = "建立人";
		csvHeader[++j] = "建立日期";
		csvHeader[++j] = "最後修改人";
		csvHeader[++j] = "最後修改時間";

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);
		this.sendRtnObject(null);
	}

	/**
	 * 檢查Map取出欄位是否為Null
	 * 
	 * @param map
	 * @return String
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	/** 產生seq No */
	private String getSN() throws JBranchException {
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		try {
			seqNum = sn.getNextSerialNumber("PMS413");
		} catch (Exception e) {
			sn.createNewSerial("PMS413", "0000000000", null, null, null, 6,
					new Long("99999999"), "y", new Long("0"), null);
			seqNum = sn.getNextSerialNumber("PMS413");
		}
		return seqNum;
	}

	/** 取員工姓名 **/
	public void getEmpName(Object body, IPrimitiveMap header) throws JBranchException {
		PMS413InputVO inputVO = (PMS413InputVO) body;
		PMS413OutputVO outputVO = new PMS413OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT EMP_NAME ");
		sql.append("FROM TBORG_MEMBER ");
		sql.append("WHERE EMP_ID = :empID ");
		condition.setObject("empID", inputVO.getEmp_id());
		condition.setQueryString(sql.toString());
		
		outputVO.setEmpName(dam.exeQuery(condition));
		
		sendRtnObject(outputVO);
	}

}