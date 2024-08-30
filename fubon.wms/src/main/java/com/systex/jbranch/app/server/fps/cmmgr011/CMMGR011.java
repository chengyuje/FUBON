package com.systex.jbranch.app.server.fps.cmmgr011;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBFPS_SEC_ITMFUNASSPK;
import com.systex.jbranch.app.common.fps.table.TBFPS_SEC_ITMFUNASSVO;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;
import com.systex.jbranch.platform.common.platformdao.table.TbsyssecumoduitemVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsyssecuprifunmapPK;
import com.systex.jbranch.platform.common.platformdao.table.TbsyssecuprifunmapVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsystxnVO;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 參數內容設定
 * 
 * @author 胡海龍
 * @date 2009-10-28
 * @spec 修改註記 1.0 2009/10/20 初版 余嘉雯
 */
@Component("cmmgr011")
@Scope("request")
public class CMMGR011 extends BizLogic {
	private DataAccessManager dam = null;

	private final String MAINTAIN = "maintenance";
	private final String QUERY = "query";
	private final String PRINT = "print";
	private final String EXPORT = "export";
	private final String WATER = "watermark";
	private final String SECURITY = "security";
	private final String CONFIRM = "confirm";
	private final String MOBILE = "mobile";
	private final String SCREEN = "screen";
	private final String QUERY_BY_FUNC = "QUERY_BY_FUNC";
	private final String QUERY_BY_GROUP = "QUERY_BY_GROUP";
	//private final String TXN_CODE = "CMMGR011";
	private Map<String, String> fun_map = new HashMap<String, String>();

	private Logger logger = LoggerFactory.getLogger(CMMGR011.class);
	
	/**
	 * 初始化查詢--查詢角色清單、群組列表
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void initInquire(Object body, IPrimitiveMap header)
			throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		CMMGR011OutputVO outputVO = new CMMGR011OutputVO();
		condition.setQueryString("select PRIVILEGEID as PRIID, NAME as PRINAME from TBSYSSECUPRI order by PRIID");
		List<Map<String, Object>> list = dam.exeQuery(condition);
		outputVO.setPriList(list);
		sendRtnObject(outputVO);
	}

	/**
	 * 功能代號--角色查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquireItemFun(Object body, IPrimitiveMap header)
			throws JBranchException {
		dam = this.getDataAccessManager();
		CMMGR011InputVO inputVO = (CMMGR011InputVO) body;
		CMMGR011OutputVO outputVO = new CMMGR011OutputVO();
		QueryConditionIF condition = dam.getQueryCondition();
		String sql = "select p.PRIVILEGEID, p.NAME, pfm.FUNCTIONID"
				+ " from TBSYSSECUPRIFUNMAP pfm"
				+ " left join TBSYSSECUPRI p on p.PRIVILEGEID = pfm.PRIVILEGEID"
				+ " where pfm.ITEMID = ? order by p.PRIVILEGEID ";
		condition.setString(1, inputVO.getTxnCode());
		condition.setQueryString(sql);
		List<Map<String, Object>> queryResult = null;
		queryResult = dam.exeQuery(condition);
		Iterator<Map<String, Object>> queryResultDetail = queryResult
				.iterator();
		List<CMMGR011OutputVO2> list = new ArrayList<CMMGR011OutputVO2>();
		while (queryResultDetail.hasNext()) {
			Map<String, Object> map = queryResultDetail.next();
			String priID = (String) map.get("PRIVILEGEID");
			String funID = (String) map.get("FUNCTIONID");
			CMMGR011OutputVO2 outputVO2 = null;
			outputVO2 = findVO2(list, priID);
			if (outputVO2 == null) {
				outputVO2 = new CMMGR011OutputVO2();
				outputVO2.setPriID(priID);
				outputVO2.setPriName((String) map.get("NAME"));
				list.add(outputVO2);
			}
			if (MAINTAIN.equals(funID)) {
				outputVO2.setMaintenance(true);
			} else if (QUERY.equals(funID)) {
				outputVO2.setQuery(true);
			} else if (PRINT.equals(funID)) {
				outputVO2.setPrint(true);
			} else if (EXPORT.equals(funID)) {
				outputVO2.setExports(true);
			} else if (WATER.equals(funID)) {
				outputVO2.setWatermark(true);
			} else if (SECURITY.equals(funID)) {
				outputVO2.setSecurity(true);
			} else if (CONFIRM.equals(funID)) {
				outputVO2.setConfirm(true);
			} else if (MOBILE.equals(funID)) {
				outputVO2.setMobile(true);
			} else if (SCREEN.equals(funID)) {
				outputVO2.setScreen(true);
			}
		}
		outputVO.setItemPriFuns(list);
		outputVO.setAllowFuns(getAllowFuns(inputVO.getTxnCode()));
		sendRtnObject(outputVO);
	}

	/**
	 * 查詢--功能代號
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void query(Object body, IPrimitiveMap header) throws JBranchException {
		CMMGR011InputVO inputVO = (CMMGR011InputVO) body;
		CMMGR011OutputVO outputVO = new CMMGR011OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		if (QUERY_BY_FUNC.equals(inputVO.getQueryType())) {
			sql.append("select * from TBSYSTXN where 1=1 ");
			if (!StringUtils.isBlank(inputVO.getTxnCode())) {
				String upper = inputVO.getTxnCode().toUpperCase();
				sql.append("and TXNCODE like :code ");
				queryCondition.setObject("code", "%" + upper + "%");
			}
			if (!StringUtils.isBlank(inputVO.getTxnName())) {
				sql.append("and TXNNAME like :name ");
				queryCondition.setObject("name", "%" + inputVO.getTxnName() + "%");
			}
			sql.append("order by TXNCODE ");
		}
		else if (QUERY_BY_GROUP.equals(inputVO.getQueryType())) {
			sql.append("select DISTINCT pfm.ITEMID as TXNCODE, TXNNAME, t.SYSTYPE, t.JRNTYPE, t.MODULEID,t.LASTUPDATE, t.MODIFIER ");
			sql.append("from TBSYSSECUPRIFUNMAP pfm ");
			sql.append("left join TBSYSTXN t ON t.TXNCODE = pfm.ITEMID where 1=1 ");
			if (!StringUtils.isBlank(inputVO.getPriID())) {
				sql.append("and pfm.PRIVILEGEID = :id ");
				queryCondition.setObject("id", inputVO.getPriID());
			}
			sql.append("order by pfm.ITEMID ");
		}
		queryCondition.setQueryString(sql.toString());
		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數
		outputVO.setTxnList(list);
		sendRtnObject(outputVO);
	}

	/**
	 * 功能代號擴充明細查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void detail(Object body, IPrimitiveMap header)
			throws JBranchException {
		CMMGR011InputVO inputVO = (CMMGR011InputVO) body;
		CMMGR011OutputVO outputVO = new CMMGR011OutputVO();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();

		condition
				.setQueryString("select APPLY  from TBSYSSECUMODUITEM where ITEMID = ?");
		condition.setString(1, inputVO.getTxnCode());
		list = dam.exeQuery(condition);
		if (list.size() > 0) {
			outputVO.setApply(list.get(0).get("APPLY").toString());
		}

		condition.setQueryString("select FUNCTIONID from TBFPS_SEC_ITMFUNASS where ITEMID = ?");
		condition.setString(1, inputVO.getTxnCode());
		list = dam.exeQuery(condition);
		if (list.size() > 0) {
			outputVO.setFunctionList(list);
		}
		sendRtnObject(outputVO);
	}

	/**
	 * 儲存修改
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		CMMGR011InputVO inputVO = (CMMGR011InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		condition.setQueryString("delete from TBSYSSECUPRIFUNMAP where ITEMID = ?");
		condition.setString(1, inputVO.getTxnCode());
		dam.exeUpdate(condition);
		List functionList = inputVO.getItemPriFuns();
		Iterator iter = functionList.iterator();
		String priID = "";
		String LOG_TYPE_ID="SECU_LOG";
		//String moduleid = getSysType(TXN_CODE);
		String moduleid = getSysType(inputVO.getTxnCode());
		
		while (iter.hasNext()) {
			Map map = (Map) iter.next();
			priID = (String) map.get("priID");

			TbsyssecuprifunmapVO funmapVO = new TbsyssecuprifunmapVO();
			TbsyssecuprifunmapPK funmappk = new TbsyssecuprifunmapPK();
			funmappk.setModuleid(moduleid);
			funmappk.setItemid(inputVO.getTxnCode());
			funmappk.setPrivilegeid(priID);
			
			if ((Boolean) map.get("exports")) {
				funmappk.setFunctionid(EXPORT);
				funmapVO.setComp_id(funmappk);
				dam.create(funmapVO);
				functionStr(priID,"匯出");
				logger.info("txnCode={}, export, priID={}", inputVO.getTxnCode(), priID);
			}
			if ((Boolean) map.get("maintenance")) {
				funmappk.setFunctionid(MAINTAIN);
				funmapVO.setComp_id(funmappk);
				dam.create(funmapVO);
				functionStr(priID,"維護");
				logger.info("txnCode={}, maintenance, priID={}", inputVO.getTxnCode(), priID);
			}
			if ((Boolean) map.get("print")) {
				funmappk.setFunctionid(PRINT);
				funmapVO.setComp_id(funmappk);
				dam.create(funmapVO);
				functionStr(priID,"列印");
				logger.info("txnCode={}, print, priID={}", inputVO.getTxnCode(), priID);
			}
			if ((Boolean) map.get("query")) {
				funmappk.setFunctionid(QUERY);
				funmapVO.setComp_id(funmappk);
				dam.create(funmapVO);
				functionStr(priID,"查詢");
				logger.info("txnCode={}, query, priID={}", inputVO.getTxnCode(), priID);
			}
			if ((Boolean) map.get("watermark")) {
				funmappk.setFunctionid(WATER);
				funmapVO.setComp_id(funmappk);
				dam.create(funmapVO);
				functionStr(priID,"浮水印");
				logger.info("txnCode={}, watermark, priID={}", inputVO.getTxnCode(), priID);
			}
			if ((Boolean) map.get("security")) {
				funmappk.setFunctionid(SECURITY);
				funmapVO.setComp_id(funmappk);
				dam.create(funmapVO);
				functionStr(priID,"鎖右鍵");
				logger.info("txnCode={}, security, priID={}", inputVO.getTxnCode(), priID);
			}
			if ((Boolean) map.get("confirm")) {
				funmappk.setFunctionid(CONFIRM);
				funmapVO.setComp_id(funmappk);
				dam.create(funmapVO);
				functionStr(priID,"覆核");
				logger.info("txnCode={}, confirm, priID={}", inputVO.getTxnCode(), priID);
			}
			if ((Boolean) map.get("mobile")) {
				funmappk.setFunctionid(MOBILE);
				funmapVO.setComp_id(funmappk);
				dam.create(funmapVO);
				functionStr(priID,"行動");
				logger.info("txnCode={}, mobile, priID={}", inputVO.getTxnCode(), priID);
			}
			if ((Boolean) map.get("screen")) {
				funmappk.setFunctionid(SCREEN);
				funmapVO.setComp_id(funmappk);
				dam.create(funmapVO);
				functionStr(priID,"拍螢幕");
				logger.info("txnCode={}, screen, priID={}", inputVO.getTxnCode(), priID);
			}
			
			//紀錄更改權限
			String priStr =  fun_map.get("priID").toString();
			String[] prilist = priStr.split(",");
			BigDecimal logindex = getLogIndex();
			for (String pri : prilist) {
				WorkStation ws = DataManager.getWorkStation(uuid);
				
				//TBSYSSECULOGVO屬platformdao，故使用insert語法
				DataAccessManager dam_obj = this.getDataAccessManager();
				QueryConditionIF queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sb = new StringBuffer();
				
				sb.append(" INSERT INTO TBSYSSECULOG ");
				sb.append(" (LOGINDEX, BRANCHID, WORKSTATIONID, TELLER, DATA1, DATA2, DATA3, VERSION, CREATOR, MODIFIER, CREATETIME,LASTUPDATE)");
				sb.append(" VALUES(:LOGINDEX,:BRANCHID,:WORKSTATIONID,:TELLER,:DATA1,:DATA2,:DATA3,1,:TELLER,:TELLER,SYSDATE,SYSDATE) ");
				
				queryCondition.setObject("LOGINDEX", logindex);
				queryCondition.setObject("BRANCHID", "");
				queryCondition.setObject("WORKSTATIONID",ws.getWsIP());
				queryCondition.setObject("TELLER", SysInfo.getInfoValue(SystemVariableConsts.LOGINID));
				queryCondition.setObject("DATA1", inputVO.getTxnCode());
				queryCondition.setObject("DATA2", fun_map.get(pri));
				queryCondition.setObject("DATA3", pri);
				
				queryCondition.setQueryString(sb.toString());
				
				dam_obj.exeUpdate(queryCondition);
			}
		}
		
		sendRtnObject(true);
	}

	/**
	 * 操作(新增、修改、刪除)
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void operation(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 取得畫面資料
		CMMGR011InputVO inputVO = (CMMGR011InputVO) body;

		if (inputVO.getOperType().equals("Create")) {
			String txnCode_s = inputVO.getTxnCode();
			String txnName_s = inputVO.getTxnName();
			String sysType_s = inputVO.getSysType();
			String jrnType_s = inputVO.getJrnType();
			String secApply_s = inputVO.getSecApply();
			String moduleId_s = inputVO.getModuleId();

			DataAccessManager dam = this.getDataAccessManager();
			QueryConditionIF queryConditionTXN = dam.getQueryCondition();

			// 1.查詢表格TBSYSTXN,若有資料,顯示錯誤訊息common_005,程式結束
			StringBuffer sql_sb = new StringBuffer();
			sql_sb.append("select * from TBSYSTXN A where A.TXNCODE = ? ");
			queryConditionTXN.setQueryString(sql_sb.toString());
			queryConditionTXN.setString(1, txnCode_s);
			List data_list = dam.exeQuery(queryConditionTXN);

			// 若有資料,顯示錯誤訊息common_005,程式結束
			if (data_list.size() > 0) {
				throw new APException("ehl_01_common_005");
			}

			// .查詢表格Tbsyssecumoduitem,條件ITEMID=畫面.tipTxnCode
			TbsyssecumoduitemVO tbsyssecumoduitemVO;
			try {
				// 2.查詢表格IT02,條件ITEMID=畫面.tipTxnCode,若有資料,刪除之
				tbsyssecumoduitemVO = new TbsyssecumoduitemVO();
				tbsyssecumoduitemVO = (TbsyssecumoduitemVO) dam.findByPKey(
						TbsyssecumoduitemVO.TABLE_UID, txnCode_s);
				if (tbsyssecumoduitemVO != null) {
					dam.delete(tbsyssecumoduitemVO);

				}
			} catch (NotFoundException e) {
				e.printStackTrace();
			} catch (DAOException e) {
				e.printStackTrace();
			} catch (JBranchException e) {
				e.printStackTrace();
				throw e;
			}

			QueryConditionIF queryCondition = dam.getQueryCondition();
			queryCondition
					.setQueryString("delete from TBFPS_SEC_ITMFUNASS where ITEMID= ?");
			queryCondition.setString(1, txnCode_s);
			try {
				dam.exeUpdate(queryCondition);
			} catch (Exception e) {

			}

			// 4.新增表格TBSYSTXN
			TbsystxnVO tbsystxnVO = new TbsystxnVO();
			tbsystxnVO.setTxncode(txnCode_s);
			tbsystxnVO.setTxnname(txnName_s);
			tbsystxnVO.setSystype(sysType_s);
			tbsystxnVO.setCrdb("0");
			tbsystxnVO.setJrntype(jrnType_s);
			tbsystxnVO.setPassbookflag("0");
			tbsystxnVO.setTimeout((short) 0);
			tbsystxnVO.setCommunicationtype("0");
			tbsystxnVO.setSealflag("0");
			tbsystxnVO.setSupflag("0");
			tbsystxnVO.setModuleid(moduleId_s);

			dam.create(tbsystxnVO);

			// 5.新增表格IT02

			tbsyssecumoduitemVO = new TbsyssecumoduitemVO();
			tbsyssecumoduitemVO.setItemid(txnCode_s);
			tbsyssecumoduitemVO.setApply(secApply_s);

			dam.create(tbsyssecumoduitemVO);

			// 6.新增表格IT03
			TBFPS_SEC_ITMFUNASSVO tbfps_sec_itmfunassvo = new TBFPS_SEC_ITMFUNASSVO();
			if (inputVO.getChkMaintenance()) {

				tbfps_sec_itmfunassvo = new TBFPS_SEC_ITMFUNASSVO();
				TBFPS_SEC_ITMFUNASSPK pk = new TBFPS_SEC_ITMFUNASSPK();
				pk.setFUNCTIONID("maintenance");
				pk.setITEMID(txnCode_s);
				tbfps_sec_itmfunassvo.setcomp_id(pk);
				dam.create(tbfps_sec_itmfunassvo);

			}

			if (inputVO.getChkExport()) {

				tbfps_sec_itmfunassvo = new TBFPS_SEC_ITMFUNASSVO();
				TBFPS_SEC_ITMFUNASSPK pk = new TBFPS_SEC_ITMFUNASSPK();
				pk.setFUNCTIONID("export");
				pk.setITEMID(txnCode_s);
				tbfps_sec_itmfunassvo.setcomp_id(pk);
				dam.create(tbfps_sec_itmfunassvo);

			}

			if (inputVO.getChkPrint()) {

				tbfps_sec_itmfunassvo = new TBFPS_SEC_ITMFUNASSVO();
				TBFPS_SEC_ITMFUNASSPK pk = new TBFPS_SEC_ITMFUNASSPK();
				pk.setFUNCTIONID("print");
				pk.setITEMID(txnCode_s);
				tbfps_sec_itmfunassvo.setcomp_id(pk);
				dam.create(tbfps_sec_itmfunassvo);

			}

			if (inputVO.getChkQuery()) {

				tbfps_sec_itmfunassvo = new TBFPS_SEC_ITMFUNASSVO();
				TBFPS_SEC_ITMFUNASSPK pk = new TBFPS_SEC_ITMFUNASSPK();
				pk.setFUNCTIONID("query");
				pk.setITEMID(txnCode_s);
				tbfps_sec_itmfunassvo.setcomp_id(pk);
				dam.create(tbfps_sec_itmfunassvo);

			}
			
			if (inputVO.getChkWatermark()) {

				tbfps_sec_itmfunassvo = new TBFPS_SEC_ITMFUNASSVO();
				TBFPS_SEC_ITMFUNASSPK pk = new TBFPS_SEC_ITMFUNASSPK();
				pk.setFUNCTIONID("watermark");
				pk.setITEMID(txnCode_s);
				tbfps_sec_itmfunassvo.setcomp_id(pk);
				dam.create(tbfps_sec_itmfunassvo);

			}
			
			if (inputVO.getChkSecurity()) {

				tbfps_sec_itmfunassvo = new TBFPS_SEC_ITMFUNASSVO();
				TBFPS_SEC_ITMFUNASSPK pk = new TBFPS_SEC_ITMFUNASSPK();
				pk.setFUNCTIONID("security");
				pk.setITEMID(txnCode_s);
				tbfps_sec_itmfunassvo.setcomp_id(pk);
				dam.create(tbfps_sec_itmfunassvo);

			}
			
			if (inputVO.getChkConfirm()) {

				tbfps_sec_itmfunassvo = new TBFPS_SEC_ITMFUNASSVO();
				TBFPS_SEC_ITMFUNASSPK pk = new TBFPS_SEC_ITMFUNASSPK();
				pk.setFUNCTIONID("confirm");
				pk.setITEMID(txnCode_s);
				tbfps_sec_itmfunassvo.setcomp_id(pk);
				dam.create(tbfps_sec_itmfunassvo);

			}
			
			if (inputVO.getChkMobile()) {

				tbfps_sec_itmfunassvo = new TBFPS_SEC_ITMFUNASSVO();
				TBFPS_SEC_ITMFUNASSPK pk = new TBFPS_SEC_ITMFUNASSPK();
				pk.setFUNCTIONID("mobile");
				pk.setITEMID(txnCode_s);
				tbfps_sec_itmfunassvo.setcomp_id(pk);
				dam.create(tbfps_sec_itmfunassvo);

			}
			
			if (inputVO.getChkScreen()) {

				tbfps_sec_itmfunassvo = new TBFPS_SEC_ITMFUNASSVO();
				TBFPS_SEC_ITMFUNASSPK pk = new TBFPS_SEC_ITMFUNASSPK();
				pk.setFUNCTIONID("screen");
				pk.setITEMID(txnCode_s);
				tbfps_sec_itmfunassvo.setcomp_id(pk);
				dam.create(tbfps_sec_itmfunassvo);

			}
			this.sendRtnObject(null);
		} else if (inputVO.getOperType().equals("Update")) {
			DataAccessManager dam = this.getDataAccessManager();

			// TBSYSTXN
			TbsystxnVO tbsystxnVO = (TbsystxnVO) dam.findByPKey(
					TbsystxnVO.TABLE_UID, inputVO.getTxnCode());
			if (tbsystxnVO == null) {
				// 新增
//				TBFPS_SEC_ITMFUNASSVO tbfps_sec_itmfunassvo = new TBFPS_SEC_ITMFUNASSVO();
				tbsystxnVO = new TbsystxnVO();
				tbsystxnVO.setTxncode(inputVO.getTxnCode());
				tbsystxnVO.setTxnname(inputVO.getTxnName());
				tbsystxnVO.setSystype(inputVO.getSysType());
				tbsystxnVO.setCrdb("0");
				tbsystxnVO.setJrntype(inputVO.getJrnType());
				tbsystxnVO.setPassbookflag("0");
				tbsystxnVO.setTimeout((short) 0);
				tbsystxnVO.setCommunicationtype("0");
				tbsystxnVO.setSealflag("0");
				tbsystxnVO.setSupflag("0");
				tbsystxnVO.setModuleid(inputVO.getModuleId());

				dam.create(tbsystxnVO);
			} else {
				// 修改
				tbsystxnVO.setTxnname(inputVO.getTxnName());
				tbsystxnVO.setSystype(inputVO.getSysType());
				tbsystxnVO.setJrntype(inputVO.getJrnType());
				tbsystxnVO.setModuleid(inputVO.getModuleId());

				dam.update(tbsystxnVO);
			}

			// TBSYSSECUMODUITEM
			TbsyssecumoduitemVO tbsyssecumoduitemVO = new TbsyssecumoduitemVO();
			tbsyssecumoduitemVO = (TbsyssecumoduitemVO) dam.findByPKey(
					TbsyssecumoduitemVO.TABLE_UID, inputVO.getTxnCode());
			if (tbsyssecumoduitemVO == null) {
				// 新增
				tbsyssecumoduitemVO = new TbsyssecumoduitemVO();
				tbsyssecumoduitemVO.setItemid(inputVO.getTxnCode());
				tbsyssecumoduitemVO.setApply(inputVO.getSecApply());

				dam.create(tbsyssecumoduitemVO);
			} else {
				// 修改
				tbsyssecumoduitemVO.setApply(inputVO.getSecApply());

				dam.update(tbsyssecumoduitemVO);
			}

			// TBFPS_SEC_ITMFUNASS
			String txnCode_s = inputVO.getTxnCode();
			QueryConditionIF queryCondition = dam.getQueryCondition();
			queryCondition
					.setQueryString("delete from TBFPS_SEC_ITMFUNASS where ITEMID= ?");
			queryCondition.setString(1, txnCode_s);
			try {
				// 刪除
				dam.exeUpdate(queryCondition);
			} catch (Exception e) {
			}
			TBFPS_SEC_ITMFUNASSVO tbfps_sec_itmfunassvo = new TBFPS_SEC_ITMFUNASSVO();
			if (inputVO.getChkMaintenance()) {
				tbfps_sec_itmfunassvo = new TBFPS_SEC_ITMFUNASSVO();
				TBFPS_SEC_ITMFUNASSPK pk = new TBFPS_SEC_ITMFUNASSPK();
				pk.setFUNCTIONID("maintenance");
				pk.setITEMID(txnCode_s);
				tbfps_sec_itmfunassvo.setcomp_id(pk);

				dam.create(tbfps_sec_itmfunassvo);
			} else {
				queryCondition
						.setQueryString("delete"
								+ " from TBSYSSECUPRIFUNMAP"
								+ " where ITEMID = ? and FUNCTIONID = 'maintenance'");
				queryCondition.setString(1, txnCode_s);

				dam.exeUpdate(queryCondition);
			}

			if (inputVO.getChkExport()) {
				tbfps_sec_itmfunassvo = new TBFPS_SEC_ITMFUNASSVO();
				TBFPS_SEC_ITMFUNASSPK pk = new TBFPS_SEC_ITMFUNASSPK();
				pk.setFUNCTIONID("export");
				pk.setITEMID(txnCode_s);
				tbfps_sec_itmfunassvo.setcomp_id(pk);

				dam.create(tbfps_sec_itmfunassvo);
			} else {
				queryCondition
						.setQueryString("delete"
								+ " from TBSYSSECUPRIFUNMAP"
								+ " where ITEMID = ? and FUNCTIONID = 'export'");
				queryCondition.setString(1, txnCode_s);

				dam.exeUpdate(queryCondition);
			}

			if (inputVO.getChkPrint()) {
				tbfps_sec_itmfunassvo = new TBFPS_SEC_ITMFUNASSVO();
				TBFPS_SEC_ITMFUNASSPK pk = new TBFPS_SEC_ITMFUNASSPK();
				pk.setFUNCTIONID("print");
				pk.setITEMID(txnCode_s);
				tbfps_sec_itmfunassvo.setcomp_id(pk);

				dam.create(tbfps_sec_itmfunassvo);
			} else {
				queryCondition
						.setQueryString("delete"
								+ " from TBSYSSECUPRIFUNMAP"
								+ " where ITEMID = ? and FUNCTIONID = 'print'");
				queryCondition.setString(1, txnCode_s);

				dam.exeUpdate(queryCondition);
			}

			if (inputVO.getChkQuery()) {
				tbfps_sec_itmfunassvo = new TBFPS_SEC_ITMFUNASSVO();
				TBFPS_SEC_ITMFUNASSPK pk = new TBFPS_SEC_ITMFUNASSPK();
				pk.setFUNCTIONID("query");
				pk.setITEMID(txnCode_s);
				tbfps_sec_itmfunassvo.setcomp_id(pk);

				dam.create(tbfps_sec_itmfunassvo);
			} else {
				queryCondition
						.setQueryString("delete"
								+ " from TBSYSSECUPRIFUNMAP"
								+ " where ITEMID = ? and FUNCTIONID = 'query'");
				queryCondition.setString(1, txnCode_s);

				dam.exeUpdate(queryCondition);
			}
			
			if (inputVO.getChkWatermark()) {
				tbfps_sec_itmfunassvo = new TBFPS_SEC_ITMFUNASSVO();
				TBFPS_SEC_ITMFUNASSPK pk = new TBFPS_SEC_ITMFUNASSPK();
				pk.setFUNCTIONID("watermark");
				pk.setITEMID(txnCode_s);
				tbfps_sec_itmfunassvo.setcomp_id(pk);

				dam.create(tbfps_sec_itmfunassvo);
			} else {
				queryCondition
						.setQueryString("delete"
								+ " from TBSYSSECUPRIFUNMAP"
								+ " where ITEMID = ? and FUNCTIONID = 'watermark'");
				queryCondition.setString(1, txnCode_s);

				dam.exeUpdate(queryCondition);
			}
			
			if (inputVO.getChkSecurity()) {
				tbfps_sec_itmfunassvo = new TBFPS_SEC_ITMFUNASSVO();
				TBFPS_SEC_ITMFUNASSPK pk = new TBFPS_SEC_ITMFUNASSPK();
				pk.setFUNCTIONID("security");
				pk.setITEMID(txnCode_s);
				tbfps_sec_itmfunassvo.setcomp_id(pk);

				dam.create(tbfps_sec_itmfunassvo);
			} else {
				queryCondition
						.setQueryString("delete"
								+ " from TBSYSSECUPRIFUNMAP"
								+ " where ITEMID = ? and FUNCTIONID = 'security'");
				queryCondition.setString(1, txnCode_s);

				dam.exeUpdate(queryCondition);
			}
			
			if (inputVO.getChkConfirm()) {
				tbfps_sec_itmfunassvo = new TBFPS_SEC_ITMFUNASSVO();
				TBFPS_SEC_ITMFUNASSPK pk = new TBFPS_SEC_ITMFUNASSPK();
				pk.setFUNCTIONID("confirm");
				pk.setITEMID(txnCode_s);
				tbfps_sec_itmfunassvo.setcomp_id(pk);

				dam.create(tbfps_sec_itmfunassvo);
			} else {
				queryCondition
						.setQueryString("delete"
								+ " from TBSYSSECUPRIFUNMAP"
								+ " where ITEMID = ? and FUNCTIONID = 'confirm'");
				queryCondition.setString(1, txnCode_s);

				dam.exeUpdate(queryCondition);
			}
			
			if (inputVO.getChkMobile()) {
				tbfps_sec_itmfunassvo = new TBFPS_SEC_ITMFUNASSVO();
				TBFPS_SEC_ITMFUNASSPK pk = new TBFPS_SEC_ITMFUNASSPK();
				pk.setFUNCTIONID("mobile");
				pk.setITEMID(txnCode_s);
				tbfps_sec_itmfunassvo.setcomp_id(pk);

				dam.create(tbfps_sec_itmfunassvo);
			} else {
				queryCondition
						.setQueryString("delete"
								+ " from TBSYSSECUPRIFUNMAP"
								+ " where ITEMID = ? and FUNCTIONID = 'mobile'");
				queryCondition.setString(1, txnCode_s);

				dam.exeUpdate(queryCondition);
			}
			
			if (inputVO.getChkScreen()) {
				tbfps_sec_itmfunassvo = new TBFPS_SEC_ITMFUNASSVO();
				TBFPS_SEC_ITMFUNASSPK pk = new TBFPS_SEC_ITMFUNASSPK();
				pk.setFUNCTIONID("screen");
				pk.setITEMID(txnCode_s);
				tbfps_sec_itmfunassvo.setcomp_id(pk);

				dam.create(tbfps_sec_itmfunassvo);
			} else {
				queryCondition
						.setQueryString("delete"
								+ " from TBSYSSECUPRIFUNMAP"
								+ " where ITEMID = ? and FUNCTIONID = 'screen'");
				queryCondition.setString(1, txnCode_s);

				dam.exeUpdate(queryCondition);
			}
			this.sendRtnObject(null);
		} else if (inputVO.getOperType().equals("Delete")) {
			String txnCode_s = inputVO.getTxnCode();

			DataAccessManager dam = this.getDataAccessManager();
			QueryConditionIF deleteCondition = dam.getQueryCondition();

			// 刪除 TBSYSTXN
			deleteCondition.setQueryString("delete from TBSYSTXN where TXNCODE = ? ");
			deleteCondition.setString(1, txnCode_s);
			try {
				dam.exeUpdate(deleteCondition);
			} catch (Exception e) {
			}
			// 刪除 TBSYSSECUPRIFUNMAP
			deleteCondition.setQueryString("delete from TBSYSSECUPRIFUNMAP where ITEMID = ? ");
			deleteCondition.setString(1, txnCode_s);
			try {
				dam.exeUpdate(deleteCondition);
			} catch (Exception e) {
			}
			// 刪除 TBSYSSECUMODUITEM
			deleteCondition
					.setQueryString("delete from TBSYSSECUMODUITEM where ITEMID = ? ");
			deleteCondition.setString(1, txnCode_s);
			try {
				dam.exeUpdate(deleteCondition);
			} catch (Exception e) {
			}
			// 刪除 TBFPS_SEC_ITMGRPASS
			deleteCondition.setQueryString("delete from TBFPS_SEC_ITMGRPASS where ITEMID = ? ");
			deleteCondition.setString(1, txnCode_s);
			try {
				dam.exeUpdate(deleteCondition);
			} catch (Exception e) {
			}
			// 刪除 TBFPS_SEC_ITMFUNASS
			deleteCondition
					.setQueryString("delete from TBFPS_SEC_ITMFUNASS where ITEMID = ? ");
			deleteCondition.setString(1, txnCode_s);
			try {
				dam.exeUpdate(deleteCondition);
			} catch (Exception e) {
			}
			this.sendRtnObject(null);
		}
	}

	/**
	 * 查詢特定功能代號允許執行的function
	 * 
	 * @param itemid
	 * @return
	 * @throws JBranchException
	 */
	private List<String> getAllowFuns(String itemid) throws JBranchException {
		dam = this.getDataAccessManager();
		List<String> allowListFuns = new ArrayList<String>();
		QueryConditionIF condition = dam.getQueryCondition();
		condition.setQueryString("select * from TBFPS_SEC_ITMFUNASS where ITEMID = ?");
		condition.setString(1, itemid);
		List<Map<String, Object>> queryResult = dam.exeQuery(condition);
		Iterator<Map<String, Object>> queryResultDetail = queryResult
				.iterator();
		while (queryResultDetail.hasNext()) {
			Map<String, Object> map = queryResultDetail.next();
			allowListFuns.add((String) map.get("FUNCTIONID"));
		}
		return allowListFuns;
	}

	private CMMGR011OutputVO2 findVO2(List<CMMGR011OutputVO2> list,
			String priID) {
		for (CMMGR011OutputVO2 vo2 : list) {
			if (null != priID && priID.equals(vo2.getPriID())) {
				return vo2;
			}
		}
		return null;
	}
	
	private String getSysType(String txnCode) throws DAOException, JBranchException{
		DataAccessManager dam = this.getDataAccessManager();
		TbsystxnVO txnVo = (TbsystxnVO) dam.findByPKey(TbsystxnVO.TABLE_UID, txnCode);
		if(txnVo == null){
			return null;
		}
		return txnVo.getSystype();
	}
	

	private void functionStr(String priID, String fuc_str) throws JBranchException{
		String priID_str = "";
		
		if(fun_map.containsKey(priID)){
			fun_map.put(priID,fun_map.get(priID).toString()+"、"+ fuc_str);
		}else{
			fun_map.put(priID, fuc_str);
			if("".equals(priID_str)){
				priID_str = priID;	
			}else{
				priID_str = priID_str + ","+ priID;
			}
			fun_map.put("priID",priID_str);
		}
	}
	
private BigDecimal getLogIndex() throws JBranchException{
		BigDecimal idx = BigDecimal.ZERO;
		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF cond = dam.getQueryCondition();
		StringBuilder sb = new StringBuilder("select max(LOGINDEX) as LASTINDEX from TBSYSSECULOG");
		
		cond.setQueryString(sb.toString());
		List<Map<String, BigDecimal>> result = dam.exeQuery(cond);
		
		try{
			idx = result.get(0).get("LASTINDEX");
			return idx.add(BigDecimal.ONE);
		}
		catch(Exception e){
			return idx;
		}
	}
	
}
