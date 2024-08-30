package com.systex.jbranch.app.server.fps.cmmgr004;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

//import com.sun.xml.internal.ws.util.StringUtils;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSSCHDJOBCONDIPK;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSSCHDJOBCONDIVO;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSWORKDAYRULEVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdjobassPK;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdjobassVO;
import com.systex.jbranch.platform.common.scheduler.ScheduleManagement;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 排程設定:修改歷程及說明
 * 
 * @author 劉麗麗
 * @date 2009-9-07
 * @since Version
 * @spec 修改註記 V0.1 2009/9/1 初版 余嘉雯 2009/9/5 修改3.4 3.5紅字 余嘉雯 2009/9/9 修改3.2
 *       3.6余嘉雯
 */
@Component("cmmgr004")
@Scope("request")
public class CMMGR004 extends BizLogic {

	private CMMGR004InputVO cMMGR004InputVO_obj = null;
	private DataAccessManager dam_obj = null;

	public void getProcessList(Object body, IPrimitiveMap header)
			throws JBranchException {
		ScheduleManagement sm = new ScheduleManagement();
		dam_obj = this.getDataAccessManager();
		String[] processList = sm.getProcessList(dam_obj);
		CMMGR004ProcessorOutputVO output = new CMMGR004ProcessorOutputVO();
		output.setProcessList(processList);
		sendRtnObject(output);
	}

	/**
	 * 查詢畫面一的資料
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 取得畫面資料
		cMMGR004InputVO_obj = (CMMGR004InputVO) body;

		// 商業邏輯
		dam_obj = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam_obj.getQueryCondition();

		StringBuffer sql = new StringBuffer();
		sql.append("select SCHEDULEID, SCHEDULENAME, SCHD.DESCRIPTION ,LASTTRY,ISSCHEDULED,ISUSE, r.CALENDAR_PROVIDER_ID");
		sql.append(" from TBSYSSCHD schd");
		sql.append(" left join TBSYSWORKDAYRULE r on schd.scheduleid=r.RULE_ID");
		sql.append(" where 1=1 ");
		int i = 1;
		if (StringUtils.isNotEmpty(cMMGR004InputVO_obj.getScheduleid())) {
			sql.append(" and SCHEDULEID like ? ");
			queryCondition.setString(i++,
					"%" + cMMGR004InputVO_obj.getScheduleid() + "%");
		}
		if (StringUtils.isNotEmpty(cMMGR004InputVO_obj.getSchedulename())) {
			sql.append(" and schd.SCHEDULENAME like ? ");
			queryCondition.setString(i++,
					"%" + cMMGR004InputVO_obj.getSchedulename() + "%");
		}
		if (StringUtils.isNotEmpty(cMMGR004InputVO_obj.getDescription())) {
			sql.append(" and schd.DESCRIPTION like ? ");
			queryCondition.setString(i++,
					"%" + cMMGR004InputVO_obj.getDescription() + "%");
		}
		sql.append(" order by scheduleid");

		queryCondition.setQueryString(sql.toString());
		ResultIF list = dam_obj.executePaging(queryCondition,
				cMMGR004InputVO_obj.getCurrentPageIndex() + 1,
				cMMGR004InputVO_obj.getPageCount());

		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();

		CMMGR004OutputVO return_VO = new CMMGR004OutputVO();

		return_VO.setResultList(list);// 傳遞回Flex端的List

		if (list.size() == 0) {
			throw new APException("ehl_01_common_009");
		}

		return_VO
				.setCurrentPageIndex(cMMGR004InputVO_obj.getCurrentPageIndex());// 當前頁次

		return_VO.setTotalPage(totalPage_i);// 總頁次

		return_VO.setTotalRecord(totalRecord_i);// 總筆數

		this.sendRtnObject(return_VO);

	}

	/**
	 * 手動執行
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void execute(Object body, IPrimitiveMap header)
			throws JBranchException, Exception {
		CMMGR004QueryInputVO cMMGR004QueryInputVO = (CMMGR004QueryInputVO) body;

		dam_obj = this.getDataAccessManager();

		// 判斷傳入的scheduleid在數據庫是否已經存在 畫面二傳入的scheduleid可能不存在
		TbsysschdVO result_Vo = (TbsysschdVO) dam_obj.findByPKey(
				TbsysschdVO.TABLE_UID, cMMGR004QueryInputVO.getHlbscheduleid());

		if (result_Vo != null) {
			/*
			 * ScheduleManagement scheduleManagement = new ScheduleManagement();
			 * scheduleManagement.run(cMMGR004QueryInputVO.getHlbscheduleid());
			 */
			result_Vo.setIstriggered("Y");
			dam_obj.update(result_Vo);
		} else {
			throw new APException("ehl_01_common_017");
		}
		
		this.sendRtnObject(null);
	}

	/**
	 * 畫面二初始化時查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryJob(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 商業邏輯
		dam_obj = this.getDataAccessManager();

		CMMGR004QueryJobOutputVO returnVO = new CMMGR004QueryJobOutputVO();

		// 獲得總的job
		QueryConditionIF queryCondition = dam_obj.getQueryCondition();
		queryCondition.setQueryString(" select JOBID, JOBNAME"
				+ " from TBSYSSCHDJOB" + " order by JOBID");

		returnVO.setLstTotalJob(dam_obj.exeQuery(queryCondition));
		this.sendRtnObject(returnVO);
	}

	/**
	 * 從畫面一傳參數到畫面二，界面初始化查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void query(Object body, IPrimitiveMap header)
			throws JBranchException {

		CMMGR004QueryInputVO cMMGR004QueryInputVO = (CMMGR004QueryInputVO) body;

		// 商業邏輯
		dam_obj = this.getDataAccessManager();

		CMMGR004QueryOutputVO returnVO = new CMMGR004QueryOutputVO();

		// 獲得總的job
		QueryConditionIF queryCondition = dam_obj.getQueryCondition();
		queryCondition.setQueryString("select JOBID, JOBNAME"
				+ " from TBSYSSCHDJOB" + " order by JOBID");

		returnVO.setLstTotalJob(dam_obj.exeQuery(queryCondition));

		// 獲得對應job
		queryCondition = dam_obj.getQueryCondition();
		queryCondition.setQueryString(" select A.JOBID,B.JOBNAME,A.JOBORDER"
				+ " from TBSYSSCHDJOBASS A "
				+ " left join TBSYSSCHDJOB B on A.JOBID = B.JOBID"
				+ " where SCHEDULEID = ? " + " order by A.JOBORDER");
		queryCondition.setString(1, cMMGR004QueryInputVO.getHlbscheduleid());
		returnVO.setLstChoiceJob(dam_obj.exeQuery(queryCondition));

		// 獲得對應欄位的資料
		queryCondition = dam_obj.getQueryCondition();
		queryCondition.setQueryString("select schd.*, r.CALENDAR_PROVIDER_ID "
				+ "from TBSYSSCHD schd "
				+ "left join TBSYSWORKDAYRULE r on schd.scheduleid=r.RULE_ID "
				+ "where scheduleid = ? ");
		queryCondition.setString(1, cMMGR004QueryInputVO.getHlbscheduleid());
		ResultIF queryResult = (ResultIF) dam_obj.exeQuery(queryCondition);

		if (queryResult.size() == 0) {
			throw new APException("ehl_01_common_009");
		} else {
			returnVO.setResultList(dam_obj.exeQuery(queryCondition));
		}

		// 獲得各JOB對應的優先執行JOB代號
		queryCondition = dam_obj.getQueryCondition();
		queryCondition.setQueryString("select A.*,B.JOBNAME"
				+ " from TBSYSSCHDJOBCONDI A"
				+ " left join TBSYSSCHDJOB B on A.REQUIREDJOBID = B.JOBID"
				+ " where A.scheduleid = ? order BY A.JOBID");
		queryCondition.setString(1, cMMGR004QueryInputVO.getHlbscheduleid());
		ResultIF queryConfResult = (ResultIF) dam_obj
				.exeQuery(queryCondition);
		if (queryResult.size() != 0) {
			returnVO.setJobConList(dam_obj.exeQuery(queryCondition));
		}

		this.sendRtnObject(returnVO);
	}

	/**
	 * 對IT01的操作:新增、修改、刪除
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void operationIT01(Object body, IPrimitiveMap header)
			throws JBranchException, Exception {

		// 取得畫面資料
		CMMGR004IT01InputVO cMMGR004IT01InputVO = (CMMGR004IT01InputVO) body;

		dam_obj = this.getDataAccessManager();

		String type_s = cMMGR004IT01InputVO.getType();

		CMMGR004IT01OutputVO return_VO = new CMMGR004IT01OutputVO();

		if ("Create".equals(type_s)) {
			// IT01
			TbsysschdVO vo = new TbsysschdVO();
			TbsysschdVO queryVO = new TbsysschdVO();// 用于判斷是否已經存在相同主鍵
			// 先查找是否已經存在同樣主鍵的數據
			queryVO = (TbsysschdVO) dam_obj.findByPKey(TbsysschdVO.TABLE_UID,
					cMMGR004IT01InputVO.getScheduleid());
			if (queryVO != null) {
				throw new APException("ehl_01_common_008");
			} else {
				if (cMMGR004IT01InputVO.getExecutetime() != null) {
					vo.setExecutetime(new BigDecimal(cMMGR004IT01InputVO
							.getExecutetime()));
				}
				vo.setscheduleid(cMMGR004IT01InputVO.getScheduleid());
				vo.setschedulename(cMMGR004IT01InputVO.getSchedulename());
				vo.setdescription(cMMGR004IT01InputVO.getDescription());
				vo.setprocessor(cMMGR004IT01InputVO.getProcessor());
				vo.setcronexpression(cMMGR004IT01InputVO.getCronexpression());
				vo.setrepeat(cMMGR004IT01InputVO.getRepeat());
				vo.setrepeatinterval(cMMGR004IT01InputVO.getRepeatinterval());
				vo.setscheduleparameter(cMMGR004IT01InputVO
						.getScheduleparameter());
				vo.setisscheduled("N");
				// vo.setIsuse("Y"); //啟用
				vo.setIsuse(cMMGR004IT01InputVO.getIsuse());
				vo.setOnetime("N"); // 非 one time job
				vo.setJobstart(new BigDecimal(1));
				dam_obj.create(vo);

				if (StringUtils.isNotEmpty(cMMGR004IT01InputVO.getCalendar())) {
					TBSYSWORKDAYRULEVO extVo = new TBSYSWORKDAYRULEVO();
					extVo.setRULE_ID(cMMGR004IT01InputVO.getScheduleid());
					extVo.setCALENDAR_PROVIDER_ID(cMMGR004IT01InputVO
							.getCalendar());
					dam_obj.create(extVo);
				}
			}

			// IT03 先刪除再新增 刪除IT03
			QueryConditionIF deleteCondition = dam_obj.getQueryCondition();
			deleteCondition = dam_obj.getQueryCondition();

			deleteCondition.setQueryString("delete" + " from TBSYSSCHDJOBASS"
					+ " where SCHEDULEID = ? ");

			deleteCondition.setString(1, cMMGR004IT01InputVO.getScheduleid());

			dam_obj.exeUpdate(deleteCondition);

			// 新增IT03
			List job_list = cMMGR004IT01InputVO.getJoblist();
			BigDecimal order_bd = new BigDecimal(0);
			if (job_list != null) {
				for (int index_i = 0; index_i < job_list.size(); index_i++) {
					TbsysschdjobassVO jobVo = new TbsysschdjobassVO();
					order_bd = order_bd.add(new BigDecimal(1));
					Map map = (Map) job_list.get(index_i);
					TbsysschdjobassPK pk = new TbsysschdjobassPK();
					pk.setJobid(map.get("JOBID").toString());
					pk.setScheduleid(cMMGR004IT01InputVO.getScheduleid());
					jobVo.setcomp_id(pk);
					jobVo.setJoborder(order_bd);
					dam_obj.create(jobVo);
				}
			}

			// IT05先刪除后新增
			List<CMMGR004JobConInputVO> jobcon_list = cMMGR004IT01InputVO
					.getJobconList();
			if (jobcon_list != null && jobcon_list.size() > 0) {
				for (int index_i = 0; index_i < jobcon_list.size(); index_i++) {

					CMMGR004JobConInputVO jobconvo = jobcon_list.get(index_i);

					deleteCondition = dam_obj.getQueryCondition();

					deleteCondition.setQueryString("delete"
							+ " from TBSYSSCHDJOBCONDI"
							+ " where SCHEDULEID = ? and JOBID = ? ");
					deleteCondition.setString(1,
							cMMGR004IT01InputVO.getScheduleid());
					deleteCondition.setString(2, jobconvo.getJobid());
					dam_obj.exeUpdate(deleteCondition);

					if (jobconvo.getConlist() != null
							&& jobconvo.getConlist().size() > 0) {
						List conlist = jobconvo.getConlist();
						for (int indexCon_i = 0; indexCon_i < conlist.size(); indexCon_i++) {

							Map map = (Map) conlist.get(indexCon_i);

							TBSYSSCHDJOBCONDIVO it05vo = new TBSYSSCHDJOBCONDIVO();
							TBSYSSCHDJOBCONDIPK it05pk = new TBSYSSCHDJOBCONDIPK();
							it05pk.setJOBID(jobconvo.getJobid());
							// it05pk.setREQUIREDJOBID(conlist.get(indexCon_i).toString());
							it05pk.setREQUIREDJOBID(map.get("JOBID").toString());
							it05pk.setSCHEDULEID(cMMGR004IT01InputVO
									.getScheduleid());
							it05vo.setcomp_id(it05pk);
							dam_obj.create(it05vo);
						}
					}
				}
			}

		} else if ("Delete".equals(type_s)) {

			QueryConditionIF deleteCondition = dam_obj.getQueryCondition();

			// 刪除IT01
			deleteCondition
					.setQueryString("delete from TBSYSSCHD where SCHEDULEID = ? ");

			deleteCondition.setString(1, cMMGR004IT01InputVO.getScheduleid());

			int exsit_i = dam_obj.exeUpdate(deleteCondition);
			if (exsit_i == 0) {
				// 顯示資料不存在
				throw new APException("ehl_01_common_017");
			}

			// 刪除IT03
			deleteCondition = dam_obj.getQueryCondition();

			deleteCondition
					.setQueryString("delete from TBSYSSCHDJOBASS where SCHEDULEID = ? ");

			deleteCondition.setString(1, cMMGR004IT01InputVO.getScheduleid());

			dam_obj.exeUpdate(deleteCondition);

//			ScheduleManagement scheduleManagement = new ScheduleManagement();
//			scheduleManagement.deleteSchedule(cMMGR004IT01InputVO
//					.getScheduleid());

		} else if ("Update".equals(type_s)) {

			TbsysschdVO vo = new TbsysschdVO();
			vo = (TbsysschdVO) dam_obj.findByPKey(TbsysschdVO.TABLE_UID,
					cMMGR004IT01InputVO.getScheduleid());

			if (vo != null) {

				if (cMMGR004IT01InputVO.getExecutetime() != null) {
					vo.setExecutetime(new BigDecimal(cMMGR004IT01InputVO
							.getExecutetime()));
				}
				vo.setschedulename(cMMGR004IT01InputVO.getSchedulename());
				vo.setdescription(cMMGR004IT01InputVO.getDescription());
				vo.setprocessor(cMMGR004IT01InputVO.getProcessor());
				vo.setcronexpression(cMMGR004IT01InputVO.getCronexpression());
				vo.setrepeat(cMMGR004IT01InputVO.getRepeat());
				vo.setrepeatinterval(cMMGR004IT01InputVO.getRepeatinterval());
				vo.setscheduleparameter(cMMGR004IT01InputVO
						.getScheduleparameter());
				vo.setisscheduled(cMMGR004IT01InputVO.getIsscheduled());
				vo.setIsuse(cMMGR004IT01InputVO.getIsuse());
				vo.setJobstart(cMMGR004IT01InputVO.getStartjob());
				dam_obj.update(vo);

				TBSYSWORKDAYRULEVO ruleVo = (TBSYSWORKDAYRULEVO) dam_obj
						.findByPKey(TBSYSWORKDAYRULEVO.TABLE_UID,
								cMMGR004IT01InputVO.getScheduleid());
				if (cMMGR004IT01InputVO.getCalendar() == null
						|| "".equals(cMMGR004IT01InputVO.getCalendar().trim())) {
					if (ruleVo != null) {
						dam_obj.delete(ruleVo);
					}
				} else {

					if (ruleVo == null) {
						ruleVo = new TBSYSWORKDAYRULEVO();
						ruleVo.setRULE_ID(cMMGR004IT01InputVO.getScheduleid());
						ruleVo.setCALENDAR_PROVIDER_ID(cMMGR004IT01InputVO
								.getCalendar());
						dam_obj.create(ruleVo);
					} else {
						ruleVo.setCALENDAR_PROVIDER_ID(cMMGR004IT01InputVO
								.getCalendar());
						dam_obj.update(ruleVo);
					}
				}
			} else {
				// 顯示資料不存在
				throw new APException("ehl_01_common_017");
			}
			// 刪除IT03的資料
			QueryConditionIF deleteCondition = dam_obj.getQueryCondition();
			deleteCondition.setQueryString("delete" + " from TBSYSSCHDJOBASS"
					+ " where SCHEDULEID = ? ");
			// System.out.println("angus test cMMGR004IT01InputVO.getScheduleid()="
			// + cMMGR004IT01InputVO.getScheduleid());
			deleteCondition.setString(1, cMMGR004IT01InputVO.getScheduleid());

			dam_obj.exeUpdate(deleteCondition);

			// 新增IT03的資料
			List job_list = cMMGR004IT01InputVO.getJoblist();
			BigDecimal order_bd = new BigDecimal(0);
			if (job_list != null) {
				for (int index_i = 0; index_i < job_list.size(); index_i++) {
					TbsysschdjobassVO jobVo = new TbsysschdjobassVO();
					order_bd = order_bd.add(new BigDecimal(1));
					Map map = (Map) job_list.get(index_i);
					TbsysschdjobassPK pk = new TbsysschdjobassPK();
					pk.setJobid(map.get("JOBID").toString());
					pk.setScheduleid(cMMGR004IT01InputVO.getScheduleid());
					jobVo.setcomp_id(pk);
					jobVo.setJoborder(order_bd);
					dam_obj.create(jobVo);
				}
			}

			// IT05先刪除后新增
			List<CMMGR004JobConInputVO> jobcon_list = cMMGR004IT01InputVO
					.getJobconList();
			if (jobcon_list != null && jobcon_list.size() > 0) {
				for (int index_i = 0; index_i < jobcon_list.size(); index_i++) {

					CMMGR004JobConInputVO jobconvo = jobcon_list.get(index_i);

					deleteCondition = dam_obj.getQueryCondition();

					deleteCondition.setQueryString("delete"
							+ " from TBSYSSCHDJOBCONDI"
							+ " where SCHEDULEID = ? and JOBID = ? ");

					deleteCondition.setString(1,
							cMMGR004IT01InputVO.getScheduleid());
					deleteCondition.setString(2, jobconvo.getJobid());
					dam_obj.exeUpdate(deleteCondition);

					if (jobconvo.getConlist() != null) {

						// deleteCondition = dam_obj.getQueryCondition();
						//
						// deleteCondition
						// .setQueryString("delete"
						// + " from TBSYSSCHDJOBCONDI"
						// + " where SCHEDULEID = ? and JOBID = ? ");
						//
						// deleteCondition.setString(0, cMMGR004IT01InputVO
						// .getScheduleid());
						// deleteCondition.setString(1, jobconvo.getJobid());
						// dam_obj.executeUpdate(deleteCondition);

						if (jobconvo.getConlist() != null
								&& jobconvo.getConlist().size() > 0) {
							List conlist = jobconvo.getConlist();
							for (int indexCon_i = 0; indexCon_i < conlist
									.size(); indexCon_i++) {

								Map map = (Map) conlist.get(indexCon_i);

								TBSYSSCHDJOBCONDIVO it05vo = new TBSYSSCHDJOBCONDIVO();
								TBSYSSCHDJOBCONDIPK it05pk = new TBSYSSCHDJOBCONDIPK();
								it05pk.setJOBID(jobconvo.getJobid());
								it05pk.setREQUIREDJOBID(map.get("JOBID")
										.toString());
								it05pk.setSCHEDULEID(cMMGR004IT01InputVO
										.getScheduleid());
								it05vo.setcomp_id(it05pk);
								dam_obj.create(it05vo);
							}
						}
					}
				}
			}
			// ScheduleManagement scheduleManagement = new ScheduleManagement();
			// scheduleManagement.deleteSchedule(cMMGR004IT01InputVO.getScheduleid());
		}
		this.sendRtnObject(return_VO);
	}
}
