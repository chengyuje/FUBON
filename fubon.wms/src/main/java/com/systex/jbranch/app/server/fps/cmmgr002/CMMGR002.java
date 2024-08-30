package com.systex.jbranch.app.server.fps.cmmgr002;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TbsyssecupriVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsyssecurolpriassPK;
import com.systex.jbranch.platform.common.platformdao.table.TbsyssecurolpriassVO;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 權限群組管理 Notics:修改歷程及說明
 * 
 * @author 劉麗麗
 * @date 2009-9-8
 * @spec 修改註記 0.1 2009/7/7 初版 王鴻傑 1.1 2009/10/17 1.UI操作調整 2.新增3,項次5 余嘉雯
 */
@Component("cmmgr002")
@Scope("request")
public class CMMGR002 extends BizLogic {

	private CMMGR002InputVO cMMGR002InputVO_obj = null;
	private DataAccessManager dam_obj = null;

	/**
	 * 查詢選中群組對應的功能清單
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 取得畫面資料
		cMMGR002InputVO_obj = (CMMGR002InputVO) body;

		// 商業邏輯
		CMMGR002OutputVO returnVo = new CMMGR002OutputVO();
		returnVo = queryMember(returnVo, cMMGR002InputVO_obj.getPriID());
		sendRtnObject(returnVo);
	}

	/**
	 * 根據privilegeID,設置角色色清單的數據
	 * 
	 * @param returnVo
	 * @param string
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private CMMGR002OutputVO queryMember(CMMGR002OutputVO returnVo,
			String privilegeID) throws DAOException, JBranchException {
		// 商業邏輯
		dam_obj = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam_obj.getQueryCondition();
		queryCondition
				.setQueryString("select B.ROLEID, B.NAME from TBSYSSECUROLPRIASS A "
						+ "left join TBSYSSECUROLE B on B.ROLEID=A.ROLEID "
						+ "where A.PRIVILEGEID = ? ORDER BY A.ROLEID ");
		queryCondition.setString(1, privilegeID);
		returnVo.setResultList((dam_obj.exeQuery(queryCondition)));

		return returnVo;
	}

	/**
	 * 界面初始化時 查詢群組清單以及角色清單的數據
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void query(Object body, IPrimitiveMap header) throws JBranchException {
		CMMGR002InputVO inputVO = (CMMGR002InputVO) body;
		CMMGR002OutputVO returnVo = new CMMGR002OutputVO();
		dam_obj = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam_obj.getQueryCondition();
		// 群組
		queryCondition.setQueryString("select PRIVILEGEID, NAME, LASTUPDATE, MODIFIER from TBSYSSECUPRI");
		ResultIF list = dam_obj.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();
		returnVo.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		returnVo.setTotalPage(totalPage_i);// 總頁次
		returnVo.setTotalRecord(totalRecord_i);// 總筆數
		returnVo.setGroupList(dam_obj.exeQuery(queryCondition));

		// 角色
		queryCondition = dam_obj.getQueryCondition();
		queryCondition.setQueryString("select ROLEID, NAME from TBSYSSECUROLE where ROLEID NOT IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS)");
		returnVo.setRoleList((dam_obj.exeQuery(queryCondition)));

		sendRtnObject(returnVo);
	}

	/**
	 * 界面初始化時 查詢群組清單以及功能清單的數據
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryGroup(Object body, IPrimitiveMap header)
			throws JBranchException {
		dam_obj = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam_obj.getQueryCondition();
		queryCondition
				.setQueryString("select PRIVILEGEID, NAME, PID,LASTUPDATE, MODIFIER from TBSYSSECUPRI");
		CMMGR002OutputVO returnVo = new CMMGR002OutputVO();
		returnVo.setResultList(dam_obj.exeQuery(queryCondition));
		sendRtnObject(returnVo);
	}

	/**
	 * 新增或者修改群組清單里的資料
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void addGroupName(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 取得畫面資料
		CMMGR002InputVO inputVO = (CMMGR002InputVO) body;

		dam_obj = this.getDataAccessManager();
		// dam_obj.setAutoCommit(true);

		TbsyssecupriVO vo = (TbsyssecupriVO) dam_obj.findByPKey(
				TbsyssecupriVO.TABLE_UID, inputVO.getPriID());

		if (vo == null) {
			vo = new TbsyssecupriVO();
			vo.setPrivilegeid(inputVO.getPriID());
			vo.setName(inputVO.getPriName());
			dam_obj.create(vo);
			this.sendRtnMsg("ehl_01_common_001");
		} else {
			vo.setName(inputVO.getPriName());
			dam_obj.update(vo);
			this.sendRtnMsg("ehl_01_common_002");
		}

		sendRtnObject(null);
	}

	/**
	 * 刪除群組清單里的資料
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void deleteGroupName(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 取得畫面資料
		CMMGR002InputVO inputVO = (CMMGR002InputVO) body;

		dam_obj = this.getDataAccessManager();
		QueryConditionIF deleteCondition = dam_obj.getQueryCondition();

		deleteCondition
				.setQueryString("delete from TBSYSSECUROLPRIASS where PRIVILEGEID = ?");
		deleteCondition.setString(1, inputVO.getPriID());
		dam_obj.exeUpdate(deleteCondition);

		deleteCondition = dam_obj.getQueryCondition();
		deleteCondition
				.setQueryString("delete from TBSYSSECUPRI where PRIVILEGEID = ? ");
		deleteCondition.setString(1, inputVO.getPriID());
		dam_obj.exeUpdate(deleteCondition);

		this.sendRtnMsg("ehl_01_common_003");
		sendRtnObject(null);
	}

	/**
	 * 儲存群組對應的功能清單
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		// 取得畫面資料
		CMMGR002InputVO inputVO = (CMMGR002InputVO) body;
		dam_obj = this.getDataAccessManager();
		QueryConditionIF deleteCondition = dam_obj.getQueryCondition();

		deleteCondition = dam_obj.getQueryCondition();
		deleteCondition
				.setQueryString("delete from TBSYSSECUROLPRIASS where PRIVILEGEID = ?");
		deleteCondition.setString(1, inputVO.getPriID());
		dam_obj.exeUpdate(deleteCondition);

		List input_list = inputVO.getRoleList();
		for (int index_i = 0; index_i < input_list.size(); index_i++) {
			TbsyssecurolpriassVO vo = new TbsyssecurolpriassVO();
			Map map = (Map) input_list.get(index_i);
			TbsyssecurolpriassPK pk = new TbsyssecurolpriassPK();
			pk.setPrivilegeid(inputVO.getPriID());
			pk.setRoleid(map.get("ROLEID").toString());
			vo.setComp_id(pk);
			dam_obj.create(vo);
		}

		sendRtnObject(null);

	}

}
