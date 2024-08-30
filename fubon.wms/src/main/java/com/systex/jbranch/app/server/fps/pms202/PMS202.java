package com.systex.jbranch.app.server.fps.pms202;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPMS_MNGR_EVAL_SCHEDULEPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_MNGR_EVAL_SCHEDULEVO;
import com.systex.jbranch.app.server.fps.pms110.PMS110InputVO;
import com.systex.jbranch.app.server.fps.pms110.PMS110OutputVO;
import com.systex.jbranch.app.server.fps.pms202.PMS202InputVO;
import com.systex.jbranch.app.server.fps.pms202.PMS202OutputVO;
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
 * Description :主管評估排程管理<br>
 * Comments Name : PMS202.java<br>
 * Author : Kevin Hsu<br>
 * Date :2016年05月24日 <br>
 * Version : 1.0 <br>
 * Editor : KevinHsu<br>
 * Editor Date : 2017年01月12日<br>
 */

@Component("pms202")
@Scope("request")
public class PMS202 extends FubonWmsBizLogic {
	public DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS202.class);

	/**
	 * 
	 * 下拉選單初始化
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void weekly(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS202OutputVO return_VO = new PMS202OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("select to_char(to_date(WEEK_START_DATE,'YYYYMMDD'),'YYYY/MM/DD') || ' 第' || substr(MTH_WEEK,4,1) || '週' as DATA_DATE,WEEK_START_DATE from (select distinct WEEK_START_DATE, MTH_WEEK from TBPMS_DATE_REC where DATA_DATE <= TO_CHAR(SYSDATE, 'YYYYMMDD')  and DATA_DATE >= TO_CHAR(add_months((SYSDATE-1),-12), 'YYYYMMDD')) T order by T.WEEK_START_DATE desc");
			queryCondition.setQueryString(sql.toString());
			// result
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			return_VO.setResultList(list);
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
		
	/**
	 * 理專下拉是選單
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void aoCode(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS202InputVO inputVO = (PMS202InputVO) body;
		PMS202OutputVO return_VO = new PMS202OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" select  a.AO_CODE||'-'||a.EMP_NAME as NAME,a.AO_CODE,a.BRANCH_NBR,a.EMP_NAME as EMP_NAME, ");
			sql.append(" a.EMP_ID as EMP_ID,a.BRANCH_NBR as BRANCH_NBRS,a.AO_JOB_RANK as AO_JOB_RANK ");
			sql.append(" from VWORG_BRANCH_EMP_DETAIL_INFO a  WHERE 1=1 ");
			sql.append(" and a.AO_CODE =:ao_code ");
			sql.append(" order by EMP_ID");
			queryCondition.setObject("ao_code", inputVO.getAo_code());
			queryCondition.setQueryString(sql.toString());
			// result
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			return_VO.setAolist(list);
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**
	 * 圖用理專下拉是選單
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void aoCodeView(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS202OutputVO return_VO = new PMS202OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {	
				sql.append(" SELECT mao.ao_code                        ");
				sql.append("        || '-'                              ");
				sql.append("        || mao.emp_name AS NAME,           ");
				sql.append("        mao.ao_code,                       ");
				sql.append("        mao.branch_nbr,                    ");
				sql.append("        mao.emp_name    AS EMP_NAME,       ");
				sql.append("        mao.emp_id      AS EMP_ID,         ");
				sql.append("        mao.branch_nbr  AS BRANCH_NBRS,    ");
				sql.append("        mao.ao_job_rank AS AO_JOB_RANK     ");
				sql.append(" FROM   vworg_branch_emp_detail_info mao   ");
				sql.append(" WHERE  1 = 1                               ");
				sql.append("        AND mao.ao_code  IS NOT NULL       ");
				sql.append("        AND mao.BRANCH_NBR  IS NOT NULL    ");
				sql.append(" ORDER  BY emp_id                           ");
			
			queryCondition.setQueryString(sql.toString());
			// result
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			return_VO.setAolist(list);
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**
	 * 
	 * 主查詢
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException {
		//輸入vo
		PMS202InputVO inputVO = (PMS202InputVO) body;
		//輸入vo
		PMS202OutputVO outputVO = new PMS202OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			//==主查詢==
			sql.append(" SELECT A.AO_CODE,A.EMP_ID,A.DATE_S, ");
			sql.append(" A.COACH_DATE,A.REGION_CENTER_ID,A.REGION_CENTER_NAME,A.BRANCH_AREA_ID ");
			sql.append(" ,A.BRANCH_AREA_NAME,A.BRANCH_ID,A.BRANCH_NAME,A.EMP_NAME,A.JOB_TITLE,A.COACHING_FREQ,A.COACHING_POINT_A,A.COACHING_POINT_B,A.CREATETIME,B.MODIFIER,A.LASTUPDATE ");
			sql.append(" ,A.COACHING_POINT_C,A.COACHING_POINT_D,A.DIR_REV,B.C_DATE,A.FIN_TYPE FROM TBPMS_MNGR_EVAL_SCHEDULE A  LEFT JOIN TBPMS_PRDCTVT_MAST B ON B.WEEK_START_DATE=A.DATE_S AND A.AO_CODE=B.AO_CODE WHERE 1=1  ");
			//==主查詢條件==
			//日期
			if (!StringUtils.isBlank(inputVO.getsCreDate())){
				sql.append(" and DATE_S >= :DATA_YEARMON "); // DATE_S起始
				sql.append(" and DATE_S <= :DATA_YEARMON "); 
			}
			if (!StringUtils.isBlank(inputVO.getType()))
				sql.append(" and FIN_TYPE  like :FIN_TYPEQ "); // 狀態
			if (!StringUtils.isBlank(inputVO.getBranch_nbr()))
				sql.append(" and BRANCH_ID  like :BRANCH_IDEE "); // 分行
			if (!StringUtils.isBlank(inputVO.getBranch_area_id()))
				sql.append(" and BRANCH_AREA_ID  like :OP_AREA_IDEEE "); // 營運區
			if (!StringUtils.isBlank(inputVO.getAo_code()))
				sql.append(" and A.AO_CODE  like :AO_CODEEE "); // 員工
			if(!StringUtils.isBlank(inputVO.getAojob()))
			    sql.append("and A.JOB_TITLE like :JOB");
			
			//排序
			sql.append(" ORDER BY BRANCH_ID , COACH_DATE, BRANCH_AREA_ID,  AO_CODE  ");
			condition.setQueryString(sql.toString());
			//==主查詢條件設定==
			//日期
			if (!StringUtils.isBlank(inputVO.getsCreDate()))
				condition.setObject("DATA_YEARMON",inputVO.getsCreDate());
			//狀態
			if (!StringUtils.isBlank(inputVO.getType()))
				condition.setObject("FIN_TYPEQ", "%" + inputVO.getType() + "%");
			//分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr()))
				condition.setObject("BRANCH_IDEE", "%" + inputVO.getBranch_nbr()
						+ "%");
			//營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id()))
				condition.setObject("OP_AREA_IDEEE", "%" + inputVO.getBranch_area_id()
						+ "%");
			//員編
			if (!StringUtils.isBlank(inputVO.getAo_code()))
				condition
						.setObject("AO_CODEEE", "%" + inputVO.getAo_code() + "%");
			//職級
			if(!StringUtils.isBlank(inputVO.getAojob()))
			   condition.setObject("JOB", "%"+inputVO.getAojob()+"%");
			
			
			//分頁結果
			ResultIF list = dam.executePaging(condition,
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
	 * update TBPMS_MNGR_EVAL
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void ddlModify(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS202InputVO inputVO = (PMS202InputVO) body;
		try {
			TBPMS_MNGR_EVAL_SCHEDULEPK PK = new TBPMS_MNGR_EVAL_SCHEDULEPK();
			TBPMS_MNGR_EVAL_SCHEDULEVO VO = new TBPMS_MNGR_EVAL_SCHEDULEVO();
			DataAccessManager dam = this.getDataAccessManager();
			PK.setAO_CODE(inputVO.getAo_code());
			PK.setDATE_S(inputVO.geteTime());
			VO = (TBPMS_MNGR_EVAL_SCHEDULEVO) dam.findByPKey(
					TBPMS_MNGR_EVAL_SCHEDULEVO.TABLE_UID, PK);
			if (VO != null) {
				Date today = new Date();
				VO.setCOACH_DATE(new Timestamp(inputVO.getDatec().getTime()));
				VO.setLastupdate(new Timestamp(today.getTime()));
				dam.update(VO);
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

	public void JOB_change(Object body, IPrimitiveMap header)throws JBranchException, ParseException{
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		PMS202InputVO inputVO =  (PMS202InputVO)body;
		PMS202OutputVO outputVO = new PMS202OutputVO();
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT * ");
		sql.append(" FROM  VWORG_BRANCH_EMP_DETAIL_INFO ");
		sql.append(" WHERE AO_JOB_RANK IS NOT NULL ");
		if(StringUtils.isNotBlank(inputVO.getAojob())){
			if(!StringUtils.equals(inputVO.getAojob(), "")){
				sql.append(" AND AO_JOB_RANK = :job ");
				 condition.setObject("job",inputVO.getAojob());
			}
		}
		condition.setQueryString(sql.toString());
		outputVO.setResultList(dam.exeQuery(condition));
		sendRtnObject(outputVO);
		
	}
}
