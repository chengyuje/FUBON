package com.systex.jbranch.app.server.fps.pms204;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPMS_MNGR_EVAL_SETPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_MNGR_EVAL_SETVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information :  <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :主管評估頻率維護<br>
 * Comments Name : PMS204.java<br>
 * Author : Kevin Hsu<br>
 * Date :2016年05月24日 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月12日<br>
 */

@Component("pms204")
@Scope("request")
public class PMS204 extends FubonWmsBizLogic {
	public DataAccessManager dam = null;
	Timestamp currentTM = new Timestamp(System.currentTimeMillis());
	private Logger logger = LoggerFactory.getLogger(PMS204.class);

	/**
	 * 查詢 TBPMS_MNGR_EVAL_SET (主表)
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS204InputVO inputVO = (PMS204InputVO) body; // 預設BODY
		PMS204OutputVO outputVO = new PMS204OutputVO(); // 輸出OUTPUTVO
		
		dam = this.getDataAccessManager(); // 啟動搜索
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append("    select c.*,                                                                                           ");
			sql.append("    ( c.MODIFIER || '-' || (case c.EMP_NAME when c.EMP_NAME then c.EMP_NAME else 'ID查無此人' end) ) as ID ");
			sql.append("    from (select a.*,b.EMP_NAME,b.EMP_ID from TBPMS_MNGR_EVAL_SET　a                                      ");
			sql.append("    left join VWORG_BRANCH_EMP_DETAIL_INFO b                                                              ");
			sql.append("    on a.MODIFIER=b.emp_id  ) c   where 1=1                                                                              ");
			if (!StringUtils.isBlank(inputVO.getPLAN_YEARMON())) {
				sql.append(" AND c.YEARMON LIKE :YEMON ");
				condition.setObject("YEMON", inputVO.getPLAN_YEARMON());
			}
			sql.append("    ORDER BY JOB_TITLE_ID                                                    ");
			
			condition.setQueryString(sql.toString()); // 檔案函數放入SQL字串
			
			ResultIF list = dam.executePaging(condition, // 檔案函數放到DAM產生結果LIST
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList(list); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/**
	 * UPDATE TBPMS_MNGR_EVAL_SET
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void ddlModify(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS204InputVO inputVO = (PMS204InputVO) body;
		
		dam = this.getDataAccessManager();

		try {
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sql=new StringBuilder();
			sql.append("select rownum as num, t.* from ( select * from TBPMS_MNGR_EVAL_SET ");
			sql.append(") t where 1=1 ");
				
			  
			if(StringUtils.isNotBlank(inputVO.getOLD_PLAN_YEARMON()))
			{
				sql.append("and YEARMON LIKE:YEARMONN ");
				queryCondition.setObject("YEARMONN", "%"+inputVO.getOLD_PLAN_YEARMON()+"%");
			}
			if(StringUtils.isNotBlank(inputVO.getOLD_JOB_TITLE_ID()))
			{
				sql.append("and JOB_TITLE_ID LIKE:JOB_TITLE_IDD ");
				queryCondition.setObject("JOB_TITLE_IDD", "%"+inputVO.getOLD_JOB_TITLE_ID()+"%");
				
			}
			if(StringUtils.isNotBlank(inputVO.getOLD_MTD_ACH_RATE_EO()))
			{
				sql.append("and MTD_ACH_RATE_E LIKE:MTD_ACH_RATE_EE ");
				queryCondition.setObject("MTD_ACH_RATE_EE", "%"+inputVO.getOLD_MTD_ACH_RATE_EO()+"%");
				
			}
			if(StringUtils.isNotBlank(inputVO.getOLD_MTD_ACH_RATE_SO()))
			{
				sql.append("and MTD_ACH_RATE_S LIKE:MTD_ACH_RATE_SS ");
				queryCondition.setObject("MTD_ACH_RATE_SS", "%"+inputVO.getOLD_MTD_ACH_RATE_SO()+"%");
				
			}
			queryCondition.setQueryString(sql.toString());
			List<Map<String,Object>> list=dam.exeQuery(queryCondition);
			Map<String,Object> map=list.get(0);
			
			if(map!=null)
			{
				QueryConditionIF queryCondition2 = dam.getQueryCondition();
				StringBuilder sql2=new StringBuilder();
			 sql2.append("DELETE FROM TBPMS_MNGR_EVAL_SET WHERE ");
			 sql2.append("YEARMON="); 
			 sql2.append(map.get("YEARMON").toString());	
			 sql2.append(" and JOB_TITLE_ID='");
			sql2.append(map.get("JOB_TITLE_ID").toString());
			sql2.append("' and MTD_ACH_RATE_E=");
			 sql2.append(map.get("MTD_ACH_RATE_E").toString());
			sql2.append(" and MTD_ACH_RATE_S=");
			sql2.append(map.get("MTD_ACH_RATE_S").toString());
			queryCondition2.setQueryString(sql2.toString());
			 dam.exeUpdate(queryCondition2);
			
			 TBPMS_MNGR_EVAL_SETPK PK = new TBPMS_MNGR_EVAL_SETPK();
			 PK.setYEARMON(inputVO.getPLAN_YEARMON());
			 PK.setJOB_TITLE_ID(inputVO.getJOB_TITLE_ID());
			 BigDecimal s=new BigDecimal(inputVO.getMTD_ACH_RATE_SO());
			 BigDecimal e=new BigDecimal(inputVO.getMTD_ACH_RATE_EO());
			 
			 PK.setMTD_ACH_RATE_S(s);
			 PK.setMTD_ACH_RATE_E(e);
			 Timestamp currentTM = new Timestamp(System.currentTimeMillis());
			 TBPMS_MNGR_EVAL_SETVO VO=new TBPMS_MNGR_EVAL_SETVO();
			    VO.setMAINTAIN_DATE(currentTM);
				VO.setCOACHING_FREQ(inputVO.getCOACHING_STATE());
				VO.setCOACHING_POINT_A(inputVO.getAPOINT());
				VO.setCOACHING_POINT_B(inputVO.getBPOINT());
				VO.setCOACHING_POINT_C(inputVO.getCPOINT());
				VO.setCOACHING_POINT_D(inputVO.getDPOINT());
			    VO.setcomp_id(PK);
			    dam.create(VO);
			    this.sendRtnObject(null);
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
}

	/**
	 * 新增 TBPMS_MNGR_EVAL_SET
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void addModify(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS204InputVO inputVO = (PMS204InputVO) body;
		dam = this.getDataAccessManager();
		
		TBPMS_MNGR_EVAL_SETPK PK = new TBPMS_MNGR_EVAL_SETPK();
		PK.setJOB_TITLE_ID(inputVO.getJOB_TITLE_ID());
		BigDecimal bigs = new BigDecimal(inputVO.getMTD_ACH_RATE_S());
		BigDecimal bige = new BigDecimal(inputVO.getMTD_ACH_RATE_E());
		PK.setMTD_ACH_RATE_S(bigs);
		PK.setMTD_ACH_RATE_E(bige);
		Timestamp currentTM = new Timestamp(System.currentTimeMillis());
		TBPMS_MNGR_EVAL_SETVO VO = (TBPMS_MNGR_EVAL_SETVO) dam.findByPKey(
				TBPMS_MNGR_EVAL_SETVO.TABLE_UID, PK);
		if (VO == null) {
			PK.setYEARMON(inputVO.getPLAN_YEARMON());
			PK.setJOB_TITLE_ID(inputVO.getJOB_TITLE_ID());
			PK.setMTD_ACH_RATE_S(bigs);
			PK.setMTD_ACH_RATE_E(bige);
			/*
				TBPMS_MNGR_EVAL_SETVO VOs = new TBPMS_MNGR_EVAL_SETVO();
				VOs.setMAINTAIN_DATE(currentTM);
				VOs.setCOACHING_FREQ(inputVO.getCOACHING_STATE());
				VOs.setCOACHING_POINT_A(inputVO.getAPOINT());
				VOs.setCOACHING_POINT_B(inputVO.getBPOINT());
				VOs.setCOACHING_POINT_C(inputVO.getCPOINT());
				VOs.setCOACHING_POINT_D(inputVO.getDPOINT());
				VOs.setcomp_id(PK);
				dam.create(VOs);
			 */
			VO = new TBPMS_MNGR_EVAL_SETVO();
			VO.setMAINTAIN_DATE(currentTM);
			VO.setCOACHING_FREQ(inputVO.getCOACHING_STATE());
			VO.setCOACHING_POINT_A(inputVO.getAPOINT());
			VO.setCOACHING_POINT_B(inputVO.getBPOINT());
			VO.setCOACHING_POINT_C(inputVO.getCPOINT());
			VO.setCOACHING_POINT_D(inputVO.getDPOINT());
			VO.setcomp_id(PK);
			dam.create(VO);
			this.sendRtnObject(null);
		} else {
			throw new APException("資料已存在！");
		}
		/*
		try {
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		*/
	}

	// 以下是刪除
	public void delRes(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS204InputVO inputVO = (PMS204InputVO) body;
		dam = this.getDataAccessManager();
		try {
			TBPMS_MNGR_EVAL_SETPK PK = new TBPMS_MNGR_EVAL_SETPK();
			TBPMS_MNGR_EVAL_SETVO VO = new TBPMS_MNGR_EVAL_SETVO();
			PK.setYEARMON(inputVO.getPLAN_YEARMON());
			PK.setJOB_TITLE_ID(inputVO.getJOB_TITLE_ID());
			BigDecimal bigs = new BigDecimal(inputVO.getMTD_ACH_RATE_S());
			BigDecimal bige = new BigDecimal(inputVO.getMTD_ACH_RATE_E());
			PK.setMTD_ACH_RATE_S(bigs);
			PK.setMTD_ACH_RATE_E(bige);
			VO = (TBPMS_MNGR_EVAL_SETVO) dam.findByPKey(
					TBPMS_MNGR_EVAL_SETVO.TABLE_UID, PK);

			if (VO != null) {
				dam.delete(VO);
			} else {
				// 顯示資料不存在
				throw new APException("ehl_01_common_005");
			}
			this.sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

}
