package com.systex.jbranch.app.server.fps.cmmgr001;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSSECUROLEVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsyssecupriVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsyssecuprifunmapPK;
import com.systex.jbranch.platform.common.platformdao.table.TbsyssecuprifunmapVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsyssecurolpriassPK;
import com.systex.jbranch.platform.common.platformdao.table.TbsyssecurolpriassVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsystxnVO;
import com.systex.jbranch.platform.common.security.privilege.PrivilegeManagement;
import com.systex.jbranch.platform.common.security.privilege.vo.ItemDTO;
import com.systex.jbranch.platform.common.security.privilege.vo.ModuleDTO;
import com.systex.jbranch.platform.common.security.privilege.vo.RoleFunctionDTO;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 參數內容設定
 * 
 * @author 胡海龍
 * @date 2009-9-9
 * @spec 修改註記 0.1 2009/7/7 初版 王鴻傑
 */
@Component("cmmgr001")
@Scope("request")
public class CMMGR001 extends BizLogic {
	private DataAccessManager dam = null;
	private final String MAINTAIN = "maintenance";
	private final String QUERY = "query";
	private final String PRINT = "print";
	private final String EXPORT = "export";
	private final String WATER = "watermark";
	private final String SECURITY = "security";
	private final String CONFIRM = "confirm";
	private final String QUERY_BY_FUNC="QUERY_BY_FUNC";
	private final String QUERY_BY_GROUP="QUERY_BY_GROUP";
	//private final String TXN_CODE = "CMMGR001";

	private Logger logger = LoggerFactory.getLogger(CMMGR001.class);
	
	/**
	 * 畫面初始化時查詢角色列表
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void initInquire(Object body, IPrimitiveMap header)
			throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		condition.setQueryString("SELECT * FROM TBSYSSECUROLE");
		List<Map<String, Object>> list = dam.executeQuery(condition);
		CMMGR001OutputVO outputVO = new CMMGR001OutputVO();
		outputVO.setRoleList(list);
		
		condition
		.setQueryString("SELECT GROUP_ID,GROUPNAME FROM TBFPS_SEC_ITMGRP");
		List<Map<String, Object>> listGroup = dam.executeQuery(condition);

		outputVO.setGroupList(listGroup);

		sendRtnObject(outputVO);

	}

	/**
	 * 畫面初始化時查詢角色列表群組列表
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	/*
	public void inquireGroupList(Object body, IPrimitiveMap header)
			throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		condition
				.setQueryString("SELECT GROUP_ID,GROUPNAME FROM TBFPS_SEC_ITMGRP");
		List<Map<String, Object>> list = dam.executeQuery(condition);
		CMMGR001OutputVO outputVO = new CMMGR001OutputVO();
		outputVO.setGroupList(list);
		sendRtnObject(outputVO);
	}
*/
	/**
	 * 根據畫面傳過來的roleID,roleName創建角色
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	/*
	public void createRole(Object body, IPrimitiveMap header)
			throws JBranchException {
		CMMGR001InputVO inputVO = (CMMGR001InputVO) body;
		dam = this.getDataAccessManager();
		TBSYSSECUROLEVO roleVO = new TBSYSSECUROLEVO();
		roleVO.setROLEID(inputVO.getRoleID());
		roleVO.setNAME(inputVO.getRoleName());
		roleVO.setEXTEND1(inputVO.getApprovalLevel());
		roleVO.setEXTEND2(inputVO.getMainPage());
		dam.create(roleVO);
		TbsyssecupriVO priVO = new TbsyssecupriVO();
		priVO.setPrivilegeid(inputVO.getRoleID());
		priVO.setName(inputVO.getRoleName());
		dam.create(priVO);
		TbsyssecurolpriassVO priassVO = new TbsyssecurolpriassVO();
		TbsyssecurolpriassPK priassPK = new TbsyssecurolpriassPK();
		priassPK.setPrivilegeid(inputVO.getRoleID());
		priassPK.setRoleid(inputVO.getRoleID());
		priassVO.setComp_id(priassPK);
		dam.create(priassVO);
		CMMGR001OutputVO outputVO = new CMMGR001OutputVO();
		outputVO.setRoleid(inputVO.getRoleID());
		outputVO.setRolename(inputVO.getRoleName());
		sendRtnObject(outputVO);
	}
*/
	// create check
	public void createRole(Object body, IPrimitiveMap header)
			throws JBranchException {
		CMMGR001InputVO inputVO = (CMMGR001InputVO) body;
		dam = this.getDataAccessManager();
		// TBSYSSECUROLEVO
		TBSYSSECUROLEVO roleVO = new TBSYSSECUROLEVO();
		TBSYSSECUROLEVO queryroleVO = new TBSYSSECUROLEVO();
		// 先查找是否已經存在同樣主鍵的數據
		queryroleVO = (TBSYSSECUROLEVO) dam.findByPKey(
				TBSYSSECUROLEVO.TABLE_UID, inputVO.getRoleID());
		if (queryroleVO != null) {
			throw new APException("ehl_01_common_005");
		} else {
			roleVO.setROLEID(inputVO.getRoleID());
			roleVO.setNAME(inputVO.getRoleName());
			roleVO.setEXTEND1(inputVO.getApprovalLevel());
			roleVO.setEXTEND2(inputVO.getMainPage());
			roleVO.setEXTEND3(inputVO.getExtend3());
			dam.create(roleVO);
		}
		
		// TbsyssecupriVO
		TbsyssecupriVO priVO = new TbsyssecupriVO();
		TbsyssecupriVO querypriVO = new TbsyssecupriVO();
		// 先查找是否已經存在同樣主鍵的數據
		querypriVO = (TbsyssecupriVO) dam.findByPKey(
				TbsyssecupriVO.TABLE_UID, inputVO.getRoleID());
		if (querypriVO != null) {
			throw new APException("ehl_01_common_005");
		} else {
			priVO.setPrivilegeid(inputVO.getRoleID());
			priVO.setName(inputVO.getRoleName());
			dam.create(priVO);
		}
		
		// TbsyssecurolpriassVO
		TbsyssecurolpriassVO priassVO = new TbsyssecurolpriassVO();
		TbsyssecurolpriassPK priassPK = new TbsyssecurolpriassPK();
		priassPK.setPrivilegeid(inputVO.getRoleID());
		priassPK.setRoleid(inputVO.getRoleID());
		priassVO.setComp_id(priassPK);
		dam.create(priassVO);
		
		this.sendRtnObject(null);
	}
	
	public void inquireRole(Object body, IPrimitiveMap header)
			throws JBranchException {
		CMMGR001InputVO inputVO = (CMMGR001InputVO) body;
		dam = this.getDataAccessManager();
		TBSYSSECUROLEVO roleVO = (TBSYSSECUROLEVO) dam.findByPKey(
				TBSYSSECUROLEVO.TABLE_UID, inputVO.getRoleID());
		CMMGR001OutputVO outputVO = new CMMGR001OutputVO();
		outputVO.setRoleid(roleVO.getROLEID());
		outputVO.setRolename(roleVO.getNAME());
		outputVO.setApprovalLevel(roleVO.getEXTEND1());
		outputVO.setMainPage(roleVO.getEXTEND2());
		sendRtnObject(outputVO);

	}

	public void updateRole(Object body, IPrimitiveMap header)
			throws JBranchException {
		CMMGR001InputVO inputVO = (CMMGR001InputVO) body;
		dam = this.getDataAccessManager();
		TBSYSSECUROLEVO roleVO = (TBSYSSECUROLEVO) dam.findByPKey(
				TBSYSSECUROLEVO.TABLE_UID, inputVO.getRoleID());
		roleVO.setNAME(inputVO.getRoleName());
		roleVO.setEXTEND1(inputVO.getApprovalLevel());
		roleVO.setEXTEND2(inputVO.getMainPage());
		roleVO.setEXTEND3(inputVO.getExtend3());
		dam.update(roleVO);
		sendRtnObject(null);

	}

	/**
	 * 根據畫面傳過來的roleID，刪除相應的角色
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void deleteRole(Object body, IPrimitiveMap header)
			throws JBranchException {
		CMMGR001InputVO inputVO = (CMMGR001InputVO) body;
		String roleID = inputVO.getRoleID();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		condition.setQueryString("DELETE FROM TBSYSSECUROLPRIASS "
				+ "WHERE ROLEID='" + roleID + "'");
		dam.exeUpdate(condition);
		condition.setQueryString("DELETE FROM TBSYSSECUROLE "
				+ "WHERE ROLEID='" + roleID + "'");
		dam.exeUpdate(condition);
		condition.setQueryString("DELETE FROM TBSYSSECUPRI "
				+ "WHERE PRIVILEGEID='" + roleID + "'");
		dam.exeUpdate(condition);
		condition.setQueryString("DELETE FROM TBSYSSECUPRIFUNMAP "
				+ "WHERE PRIVILEGEID='" + roleID + "'");
		dam.exeUpdate(condition);

		sendRtnObject(null);

	}

	/**
	 * 查詢相應的角色的權限列表
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryRoleFun(Object body, IPrimitiveMap header)
			throws JBranchException {
		
		CMMGR001InputVO inputVO = (CMMGR001InputVO) body;
		CMMGR001OutputVO outputVO = new CMMGR001OutputVO();
		dam = this.getDataAccessManager();
		String roleID = inputVO.getRoleID();
		PrivilegeManagement pm = (PrivilegeManagement) PlatformContext.getBean("privilegeManagement");
		RoleFunctionDTO dto = pm.getFunctionMapByRole(roleID);
		//String moduleid = getSysType(TXN_CODE);
		Map<String, ModuleDTO> moduleMap = dto.getModuleMap();
		
		//需search所有的module,除非日後UI有指定子系統,再依子系統取權限
		//ModuleDTO moduleDTO = moduleMap.get(moduleid);	
		List<CMMGR001OutputVO2> functionList = new ArrayList<CMMGR001OutputVO2>();
		Map<String, ItemDTO> itemMap =null;
		Iterator<String> itr =null;
		ArrayList<String> itemList=null;
		String itemID="";
		ItemDTO itemDTO=null;
		Set<String> functionSet=null;
		CMMGR001OutputVO2 outputVO2=null;
		Iterator<String> funItr=null;
		String s="";
		TbsystxnVO txnVO=null;
		int itemLength_i=0;
		List<Map<String, Object>> queryResult = null;
		Iterator<Map<String, Object>> queryResultDetail = null;
		QueryConditionIF condition=null;
		String sqlQuery_s="";
		int item_i = 0;
		Map<String, Object> map =null;
		String temp = "";
		for (Map.Entry<String, ModuleDTO> moduleDTO : moduleMap.entrySet()) {
			if (moduleDTO==null) continue;
	
			itemMap = moduleDTO.getValue().getItemMap();
			itr = itemMap.keySet().iterator();
		
			//String itemList = "";
			itemList = new ArrayList<String>();
			while (itr.hasNext()) {
				itemID = itr.next();
				//itemList+="'"+itemID+"'"+",";
				itemList.add(itemID);
				itemDTO = itemMap.get(itemID);
				functionSet = itemDTO.getFunctionSet();
				outputVO2 = new CMMGR001OutputVO2();

				funItr = functionSet.iterator();
				while (funItr.hasNext()) {
					s = funItr.next();
					if (MAINTAIN.equals(s)) {
						outputVO2.setMaintenance(true);
					} else if (QUERY.equals(s)) {
						outputVO2.setQuery(true);
					} else if (PRINT.equals(s)) {
						outputVO2.setPrint(true);
					} else if (EXPORT.equals(s)) {
						outputVO2.setExports(true);
					} else if (WATER.equals(s)) {
						outputVO2.setWatermark(true);
					} else if (SECURITY.equals(s)) {
						outputVO2.setSecurity(true);
					} else if (CONFIRM.equals(s)) {
						outputVO2.setConfirm(true);
					}
				}
				outputVO2.setRoleid(roleID);
				outputVO2.setItemid(itemID);

				txnVO = (TbsystxnVO) dam.findByPKey(
						TbsystxnVO.TABLE_UID, itemID);
				if (txnVO != null) {
					outputVO2.setItemname(txnVO.getTxnname());
				}

				functionList.add(outputVO2);

			}
			//List<String> allowList = new ArrayList<String>();
			itemLength_i = itemList.size();
			if(itemLength_i > 0){
				queryResult = null;
				queryResultDetail = null;
				condition = dam.getQueryCondition();
				//itemList="("+itemList.substring(0, itemList.length()-1)+")";
				//condition.setQueryString("select * from TBFPS_SEC_ITMFUNASS AS IT05 where IT05.ITEMID in"+itemList + " order by IT05.ITEMID");
				sqlQuery_s = "select * from TBFPS_SEC_ITMFUNASS AS IT05 where IT05.ITEMID in (";
				item_i = 0;
				for (item_i = 0; item_i < itemLength_i; item_i ++) {
					sqlQuery_s += "?,";
				}
				sqlQuery_s = sqlQuery_s.substring(0, sqlQuery_s.length() - 1) + ") order by IT05.ITEMID";
				condition.setQueryString(sqlQuery_s);
				for (item_i = 0; item_i < itemLength_i; item_i ++) {
					condition.setString(item_i + 1, itemList.get(item_i).toString());
				}
				queryResult = dam.executeQuery(condition);
				queryResultDetail = queryResult.iterator();
				
				while (queryResultDetail.hasNext()) {
					map = queryResultDetail.next();
					temp = (String)map.get("ITEMID");
					outputVO2=findVO2(functionList, temp);
					if (temp != null&&outputVO2!=null) {
						s=(String)map.get("FUNCTIONID");
						if (MAINTAIN.equals(s)) {
							outputVO2.setAllowMaintenance(true);
						} else if (QUERY.equals(s)) {
							outputVO2.setAllowQuery(true);
						} else if (PRINT.equals(s)) {
							outputVO2.setAllowPrint(true);
						} else if (EXPORT.equals(s)) {
							outputVO2.setAllowExport(true);
						} else if (WATER.equals(s)) {
							outputVO2.setAllowWatermark(true);
						} else if (SECURITY.equals(s)) {
							outputVO2.setAllowSecurity(true);
						} else if (CONFIRM.equals(s)) {
							outputVO2.setAllowConfirm(true);
						}
						
					}
				}
			}
		}
		outputVO.setFunctionList(functionList);
		sendRtnObject(outputVO);

	}
	private CMMGR001OutputVO2 findVO2(List<CMMGR001OutputVO2> list,String itemid){
		for(CMMGR001OutputVO2 vo2:list){
			if(itemid.equals(vo2.getItemid())){
				return vo2;
			}
		}
		return null;
	}
	/**
	 * 根據用戶點選的群組，加入相應的權限
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	/*
	public void addGroupFun(Object body, IPrimitiveMap header)
			throws JBranchException {
		CMMGR001InputVO inputVO = (CMMGR001InputVO) body;
		CMMGR001OutputVO outputVO = new CMMGR001OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		List<Map<String, Object>> queryResult = null;
		Iterator<Map<String, Object>> queryResultDetail = null;
		condition.setQueryString("SELECT  A.ITEMID, B.TXNNAME AS ITEMNAME "
				+ "FROM TBFPS_SEC_ITMGRPASS A "
				+ "LEFT JOIN TBSYSTXN B ON A.ITEMID= B.TXNCODE "
				+ "WHERE A.GROUP_ID =" + inputVO.getGroupID() );
		queryResult = dam.executeQuery(condition);
		queryResultDetail = queryResult.iterator();
		List<CMMGR001OutputVO2> functionList = new ArrayList<CMMGR001OutputVO2>();
		while (queryResultDetail.hasNext()) {
			CMMGR001OutputVO2 outputVO2 = new CMMGR001OutputVO2();
			Map<String, Object> map = queryResultDetail.next();
			Object temp = map.get("ITEMID");
			if (temp != null) {
				outputVO2.setItemid(temp.toString());
			}
			temp = map.get("ITEMNAME");
			if (temp != null) {
				outputVO2.setItemname(temp.toString());
			}
			functionList.add(outputVO2);
		}
		outputVO.setFunctionList(functionList);
		sendRtnObject(outputVO);

	}
	*/
	public void query(Object body, IPrimitiveMap header)
								throws JBranchException {
		CMMGR001InputVO inputVO = (CMMGR001InputVO) body;
		CMMGR001OutputVO outputVO = new CMMGR001OutputVO();
		dam = this.getDataAccessManager();
		/*
		if(QUERY_BY_FUNC.equals(inputVO.getQueryType())){
			sql="select * from TBSYSTXN AS IT02 where 1=1";
			if(!"".equals(inputVO.getRoleID())){
				sql=sql+" and IT02.TXNCODE like '%"+inputVO.getRoleID()+"%'";
			}
			if(!"".equals(inputVO.getRoleName())){
				sql=sql+" and IT02.TXNNAME  like '%"+inputVO.getRoleName()+"%'";
			}
			sql=sql+" ORDER BY IT02.TXNCODE";
		}
		else if(QUERY_BY_GROUP.equals(inputVO.getQueryType())){
			sql = "SELECT distinct IT04.ITEMID AS TXNCODE, IT02.TXNNAME "
			+ "FROM TBFPS_SEC_ITMGRPASS AS IT04 "
			+ "LEFT JOIN TBSYSTXN AS IT02 ON IT04.ITEMID= IT02.TXNCODE WHERE 1 = 1";
			if (inputVO.getGroupID() != null){
				sql += " and IT04.GROUP_ID = '" + inputVO.getGroupID() + "' ORDER BY IT04.ITEMID";
			}else{
				sql += " ORDER BY IT04.ITEMID";
			}
		}*/
		QueryConditionIF condition = dam.getQueryCondition();		
		List<Map<String, Object>> queryResult = null;

		ArrayList<String> sql_list = new ArrayList<String>();
		String sql = "";
		if(QUERY_BY_FUNC.equals(inputVO.getQueryType())){
			sql = "select * from TBSYSTXN AS IT02 where 1=1";
			if(!"".equals(inputVO.getTxnCode())){
				sql += " and IT02.TXNCODE like ? ";
				sql_list.add("%" + inputVO.getTxnCode() + "%");
			}
			if(!"".equals(inputVO.getTxnName())){
				sql += " and IT02.TXNNAME  like ? ";
				sql_list.add("%" + inputVO.getTxnName() + "%");
			}
			sql += " ORDER BY IT02.TXNCODE";
		}
		else if(QUERY_BY_GROUP.equals(inputVO.getQueryType())){
			sql_list = new ArrayList<String>();
			sql = "select distinct IT04.ITEMID AS TXNCODE, IT02.TXNNAME "
				+ " from TBFPS_SEC_ITMGRPASS AS IT04 "
				+ " left join TBSYSTXN AS IT02 on IT04.ITEMID= IT02.TXNCODE where 1 = 1";
			if (!StringUtils.isBlank(inputVO.getGroupID())){
				sql += " and IT04.GROUP_ID = ? ORDER BY IT04.ITEMID";
				sql_list.add(inputVO.getGroupID());
			}else{
				sql += " order by IT04.ITEMID";
			}
		}
		condition.setQueryString(sql);
		for (int sql_i = 0; sql_i < sql_list.size(); sql_i ++) {
			condition.setString(sql_i + 1, sql_list.get(sql_i));
		}
		queryResult = dam.exeQuery(condition);
		Iterator<Map<String, Object>> queryResultDetail = queryResult.iterator();
		List<CMMGR001OutputVO2> functionList = new ArrayList<CMMGR001OutputVO2>();
		//String itemList="";
		ArrayList<String> itemList = new ArrayList<String>();
		while (queryResultDetail.hasNext()) {
			CMMGR001OutputVO2 outputVO2 = new CMMGR001OutputVO2();
			Map<String, Object> map = queryResultDetail.next();
			Object temp = map.get("TXNCODE");
			if (temp != null) {
				outputVO2.setItemid(temp.toString());
				//itemList+="'"+temp.toString()+"'"+",";
				itemList.add(temp.toString());
			}
			temp = map.get("TXNNAME");
			if (temp != null) {
				outputVO2.setItemname(temp.toString());
			}
			functionList.add(outputVO2);
		}
		List<String> allowList=new ArrayList<String>();
		int itemLength_i = itemList.size();
		if(itemLength_i > 0){
			QueryConditionIF itemcondition = dam.getQueryCondition();		
			String sqlQuery_s = "select * from TBFPS_SEC_ITMFUNASS AS IT05 where IT05.ITEMID in(";
			int item_i = 0;
			for (item_i = 0; item_i < itemLength_i; item_i ++) {
				sqlQuery_s += "?,";
			}
			sqlQuery_s = sqlQuery_s.substring(0, sqlQuery_s.length() - 1) + ") order by IT05.ITEMID";
			itemcondition.setQueryString(sqlQuery_s);
			for (item_i = 0; item_i < itemLength_i; item_i ++) {
				itemcondition.setString(item_i + 1, itemList.get(item_i).toString());
			}
			queryResult = dam.exeQuery(itemcondition);
			queryResultDetail = queryResult.iterator();
			
			while (queryResultDetail.hasNext()) {
				Map<String, Object> map = queryResultDetail.next();
				String temp = (String)map.get("ITEMID");
				CMMGR001OutputVO2 outputVO2=findVO2(functionList, temp);
				if (temp != null&&outputVO2!=null) {
					                       
					String s=(String)map.get("FUNCTIONID");
					if (MAINTAIN.equals(s)) {
						outputVO2.setAllowMaintenance(true);
					} else if (QUERY.equals(s)) {
						outputVO2.setAllowQuery(true);
					} else if (PRINT.equals(s)) {
						outputVO2.setAllowPrint(true);
					} else if (EXPORT.equals(s)) {
						outputVO2.setAllowExport(true);
					} else if (WATER.equals(s)) {
						outputVO2.setAllowWatermark(true);
					} else if (SECURITY.equals(s)) {
						outputVO2.setAllowSecurity(true);
					} else if (CONFIRM.equals(s)) {
						outputVO2.setAllowConfirm(true);
					}
					
				}
			}
		}

		outputVO.setFunctionList(functionList);
		sendRtnObject(outputVO);
		
	}
//	private  getAllowList(){
//		
//	}
	/**
	 * 保存用戶的修改
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		CMMGR001InputVO inputVO = (CMMGR001InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		condition.setQueryString("delete"
								+ " from TBSYSSECUPRIFUNMAP "
								+ " where PRIVILEGEID = ? ");
		condition.setString(0, inputVO.getRoleID());
		dam.exeUpdate(condition);
		List functionList = inputVO.getFunctionList();
		Iterator iter = functionList.iterator();
		String itemid = "";
		String LOG_TYPE_ID="SECU_LOG";
		//String moduleid = getSysType(TXN_CODE);
		String moduleid ="";
		while (iter.hasNext()) {
			Map map = (Map) iter.next();
			itemid = map.get("itemid").toString();
			moduleid = getSysType(itemid);
			if ((Boolean) map.get("exports")) {
				TbsyssecuprifunmapVO funmapVO = new TbsyssecuprifunmapVO();
				TbsyssecuprifunmapPK funmappk = new TbsyssecuprifunmapPK();
				funmappk.setModuleid(moduleid);
				funmappk.setItemid(map.get("itemid").toString());
				funmappk.setPrivilegeid(inputVO.getRoleID());
				funmappk.setFunctionid(EXPORT);
				funmapVO.setComp_id(funmappk);
				dam.create(funmapVO);
				logger.info("itemid={}, export, roleId={}", itemid, inputVO.getRoleID());
			}
			if ((Boolean) map.get("maintenance")) {
				TbsyssecuprifunmapVO funmapVO = new TbsyssecuprifunmapVO();
				TbsyssecuprifunmapPK funmappk = new TbsyssecuprifunmapPK();
				funmappk.setModuleid(moduleid);
				funmappk.setItemid(map.get("itemid").toString());
				funmappk.setPrivilegeid(inputVO.getRoleID());
				funmappk.setFunctionid(MAINTAIN);
				funmapVO.setComp_id(funmappk);
				dam.create(funmapVO);
				logger.info("itemid={}, maintence, roleId={}", itemid, inputVO.getRoleID());
			}
			if ((Boolean) map.get("print")) {
				TbsyssecuprifunmapVO funmapVO = new TbsyssecuprifunmapVO();
				TbsyssecuprifunmapPK funmappk = new TbsyssecuprifunmapPK();
				funmappk.setModuleid(moduleid);
				funmappk.setItemid(map.get("itemid").toString());
				funmappk.setPrivilegeid(inputVO.getRoleID());
				funmappk.setFunctionid(PRINT);
				funmapVO.setComp_id(funmappk);
				dam.create(funmapVO);
				logger.info("itemid={}, print, roleId={}", itemid, inputVO.getRoleID());
			}
			if ((Boolean) map.get("query")) {
				TbsyssecuprifunmapVO funmapVO = new TbsyssecuprifunmapVO();
				TbsyssecuprifunmapPK funmappk = new TbsyssecuprifunmapPK();
				funmappk.setModuleid(moduleid);
				funmappk.setItemid(map.get("itemid").toString());
				funmappk.setPrivilegeid(inputVO.getRoleID());
				funmappk.setFunctionid(QUERY);
				funmapVO.setComp_id(funmappk);
				dam.create(funmapVO);
				logger.info("itemid={}, query, roleId={}", itemid, inputVO.getRoleID());
			}
			if ((Boolean) map.get("watermark")) {
				TbsyssecuprifunmapVO funmapVO = new TbsyssecuprifunmapVO();
				TbsyssecuprifunmapPK funmappk = new TbsyssecuprifunmapPK();
				funmappk.setModuleid(moduleid);
				funmappk.setItemid(map.get("itemid").toString());
				funmappk.setPrivilegeid(inputVO.getRoleID());
				funmappk.setFunctionid(WATER);
				funmapVO.setComp_id(funmappk);
				dam.create(funmapVO);
				logger.info("itemid={}, watermark, roleId={}", itemid, inputVO.getRoleID());
			}
			if ((Boolean) map.get("security")) {
				TbsyssecuprifunmapVO funmapVO = new TbsyssecuprifunmapVO();
				TbsyssecuprifunmapPK funmappk = new TbsyssecuprifunmapPK();
				funmappk.setModuleid(moduleid);
				funmappk.setItemid(map.get("itemid").toString());
				funmappk.setPrivilegeid(inputVO.getRoleID());
				funmappk.setFunctionid(SECURITY);
				funmapVO.setComp_id(funmappk);
				dam.create(funmapVO);
				logger.info("itemid={}, security, roleId={}", itemid, inputVO.getRoleID());
			}
			if ((Boolean) map.get("confirm")) {
				TbsyssecuprifunmapVO funmapVO = new TbsyssecuprifunmapVO();
				TbsyssecuprifunmapPK funmappk = new TbsyssecuprifunmapPK();
				funmappk.setModuleid(moduleid);
				funmappk.setItemid(map.get("itemid").toString());
				funmappk.setPrivilegeid(inputVO.getRoleID());
				funmappk.setFunctionid(CONFIRM);
				funmapVO.setComp_id(funmappk);
				dam.create(funmapVO);
				logger.info("itemid={}, confirm, roleId={}", itemid, inputVO.getRoleID());
			}
		}
	}

	/**
	 * 刪除用戶點選的項目
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void deleteItemFun(Object body, IPrimitiveMap header)
			throws JBranchException {
		CMMGR001InputVO inputVO = (CMMGR001InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		List functionList = inputVO.getFunctionList();
		Iterator iter = functionList.iterator();
		while (iter.hasNext()) {
			Map map = (Map) iter.next();

			condition.setQueryString("delete from TBSYSSECUPRIFUNMAP "
									+ " where PRIVILEGEID = ? "
									+ " and ITEMID = ? ");
			condition.setString(0,  map.get("roleid").toString());
			condition.setString(1,  map.get("itemid").toString());
			dam.exeUpdate(condition);
		}
	}

	private String getSysType(String txnCode) throws DAOException, JBranchException{
		DataAccessManager dam = this.getDataAccessManager();
		TbsystxnVO txnVo = (TbsystxnVO) dam.findByPKey(TbsystxnVO.TABLE_UID, txnCode);
		if(txnVo == null){
			return null;
		}
		return txnVo.getSystype();
	}
}
