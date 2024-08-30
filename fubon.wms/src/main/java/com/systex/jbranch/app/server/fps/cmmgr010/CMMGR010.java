package com.systex.jbranch.app.server.fps.cmmgr010;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSREMOTEHOSTVO;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * FTP連線Server設定:修改歷程及說明
 * 
 * @author 劉麗麗
 * @date 2009-9-07
 * @since Version
 * @spec 修改註記 V0.1 2009/10/27 初版 余嘉雯
 */
@Component("cmmgr010")
@Scope("request")
public class CMMGR010 extends FubonWmsBizLogic {

	private DataAccessManager dam_obj = null;

	/**
	 * 新增、修改、刪除
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void operation(Object body, IPrimitiveMap header)
			throws JBranchException {

		// 取得畫面資料
		CMMGR010InputVO cMMGR010InputVO = (CMMGR010InputVO) body;

		dam_obj = this.getDataAccessManager();

		String type_s = cMMGR010InputVO.getType();

		CMMGR010OutputVO return_VO = new CMMGR010OutputVO();

		if ("Creat".equals(type_s)) {
			return_VO.setResultType("Creat");
			TBSYSREMOTEHOSTVO vo = new TBSYSREMOTEHOSTVO();

			TBSYSREMOTEHOSTVO queryVO = new TBSYSREMOTEHOSTVO();
			// 先查找是否已經存在同樣主鍵的數據
			queryVO = (TBSYSREMOTEHOSTVO) dam_obj.findByPKey(
					TBSYSREMOTEHOSTVO.TABLE_UID, cMMGR010InputVO.getHostid());
			if (queryVO != null) {
				throw new APException("ehl_01_common_016");
			} else {
				vo.setHOSTID(cMMGR010InputVO.getHostid());
				vo.setIP(cMMGR010InputVO.getIp());
				vo.setPASSWORD(this.encrypt(cMMGR010InputVO.getPassword()));
				// vo.setPASSWORD(cMMGR010InputVO.getPassword());
				vo.setUSERNAME(cMMGR010InputVO.getUsername());
				vo.setPORT(cMMGR010InputVO.getPort());
				dam_obj.create(vo);
			}
			return_VO.setResultType("Creat");
		} else if ("Delete".equals(type_s)) {
			return_VO.setResultType("Delete");

			QueryConditionIF deleteCondition = dam_obj
					.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

			deleteCondition.setQueryString("delete from TBSYSREMOTEHOST where HOSTID = :hostId");
			deleteCondition.setObject("hostId", cMMGR010InputVO.getHostid());

			int exsit_i = dam_obj.exeUpdate(deleteCondition);
			if (exsit_i == 0) {
				// 顯示資料不存在
				throw new APException("ehl_01_common_009");
			}
			return_VO.setResultType("Delete");
		} else if ("Update".equals(type_s)) {
			return_VO.setResultType("Update");
			TBSYSREMOTEHOSTVO vo = new TBSYSREMOTEHOSTVO();
			vo = (TBSYSREMOTEHOSTVO) dam_obj.findByPKey(
					TBSYSREMOTEHOSTVO.TABLE_UID, cMMGR010InputVO.getHostid());

			if (vo != null) {

				vo.setIP(cMMGR010InputVO.getIp());
				vo.setPASSWORD(this.encrypt(cMMGR010InputVO.getPassword()));
				vo.setUSERNAME(cMMGR010InputVO.getUsername());
				vo.setPORT(cMMGR010InputVO.getPort());

				dam_obj.update(vo);
			} else {
				// 顯示資料不存在
				throw new APException("ehl_01_common_009");
			}

			return_VO.setResultType("Update");

		}
		
		this.sendRtnObject(return_VO);
	}

	/**
	 * 查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException {

		// 取得畫面資料
		CMMGR010InputVO cMMGR010InputVO = (CMMGR010InputVO) body;

		CMMGR010OutputVO return_VO = new CMMGR010OutputVO();

		dam_obj = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam_obj.getQueryCondition();

		ArrayList<String> sql_list = new ArrayList<String>();
		String sql_s = "select * from TBSYSREMOTEHOST where 1=1";

		if (!"".equals(cMMGR010InputVO.getHostid())) {
			sql_s = sql_s + " and HOSTID like ? ";
			sql_list.add("%" + cMMGR010InputVO.getHostid() + "%");
		}
		if (!"".equals(cMMGR010InputVO.getIp())) {
			sql_s = sql_s + " and IP like ? ";
			sql_list.add("%" + cMMGR010InputVO.getIp() + "%");
		}
		if (!"".equals(cMMGR010InputVO.getPort())
				&& cMMGR010InputVO.getPort() != null) {
			sql_s = sql_s + " and PORT like ? ";
			sql_list.add("%" + cMMGR010InputVO.getPort() + "%");
		}
		if (!"".equals(cMMGR010InputVO.getUsername())) {
			sql_s = sql_s + " and USERNAME like ? ";
			sql_list.add("%" + cMMGR010InputVO.getUsername() + "%");
		}
		sql_s = sql_s + " order BY HOSTID ";

		queryCondition.setQueryString(sql_s);
		for (int sql_i = 0; sql_i < sql_list.size(); sql_i++) {
			queryCondition.setString(sql_i + 1, sql_list.get(sql_i));
		}

		ResultIF list = dam_obj.executePaging(queryCondition,
				cMMGR010InputVO.getCurrentPageIndex() + 1,
				cMMGR010InputVO.getPageCount());

		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();

		List<Map<String, Object>> resulst_list = new ArrayList<Map<String, Object>>();
		
		if (list.size() != 0) {
			for (int index_i = 0; index_i < list.size(); index_i++) {
				Map<String, Object> map = (Map<String, Object>) list.get(index_i);
				Map<String, Object> returnMap = new HashMap<String, Object>();
				String hostid = (String) map.get("HOSTID");
				returnMap.put("HOSTID", map.get("HOSTID"));
				returnMap.put("IP", map.get("IP"));
				returnMap.put("PORT", map.get("PORT"));
				returnMap.put("USERNAME", map.get("USERNAME"));
				if (map.get("PASSWORD") != null) {
					returnMap.put("PASSWORD", this.decrypt(map.get("PASSWORD").toString()));
				}
				returnMap.put("LASTUPDATE", map.get("LASTUPDATE"));
				returnMap.put("MODIFIER", map.get("MODIFIER"));
				resulst_list.add(returnMap);
			}
		}

		return_VO.setResultList(resulst_list);// 傳遞回Flex端的List
		return_VO.setCurrentPageIndex(cMMGR010InputVO.getCurrentPageIndex());// 當前頁次
		return_VO.setTotalPage(totalPage_i);// 總頁次
		return_VO.setTotalRecord(totalRecord_i);// 總筆數

		this.sendRtnObject(return_VO);
	}

}
