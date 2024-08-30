package com.systex.jbranch.app.server.fps.pms205;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.util.Calendar;
import com.systex.jbranch.app.common.fps.table.TBPMS_TRACK_PRO_SETPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_TRACK_PRO_SETVO;
import com.systex.jbranch.app.server.fps.pms305.PMS305OutputVO;
import com.systex.jbranch.app.server.fps.pms305.PMS305detailInputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :追蹤產品設定<br>
 * Comments Name : PMS205.java<br>
 * Author : Kevin Hsu<br>
 * Date :2016年05月24日 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月12日<br>
 */
@Component("pms205")
@Scope("request")
public class PMS205 extends FubonWmsBizLogic {
	public DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS205.class);

	/**
	 * 主查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS205InputVO inputVO = (PMS205InputVO) body;
		PMS205OutputVO outputVO = new PMS205OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("select * from " + "TBPMS_TRACK_PRO_SET" + "　where 1=1 ");
			// ==主查詢條件==
			// 年月
			if (inputVO.geteTime()!=null)
				sql.append("and EDATE like :DATA_YEARMON "); // 年月
			// 排序
			sql.append("ORDER BY PRD_ID,SDATE         ");
			condition.setQueryString(sql.toString());
			// ==主查詢條件設定==
			// 年月
			if (inputVO.geteTime()!=null)
				{
				  SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd"); 
				condition.setObject("DATA_YEARMON", "%" + sdf.format(inputVO.geteTime())
						+ "%");
//				System.out.println("aaaaaaa "+sdf.format(inputVO.geteTime()));
				}
				// 分頁結果
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
	
	//基金跟保險查詢
	public void queryINS(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS205detailInputVO inputVO=(PMS205detailInputVO)body;
	    DataAccessManager dam=this.getDataAccessManager();
	    QueryConditionIF condition=dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	    StringBuffer sql=new StringBuffer();
	    try {
	    	
	    	if(inputVO.isCheckgi().equals("1")){
	    		sql.append("SELECT ROWNUM AS NUM,T.* FROM(");
				sql.append("SELECT distinct  PRD_ID,FUND_ENAME, FUND_CNAME FROM TBPRD_FUND WHERE 1=1");
				
				if(StringUtils.isNotBlank(inputVO.getINS_ID()))
				{
					sql.append(" and PRD_ID LIKE :INS_IDD");
				}
				sql.append(" ORDER BY  PRD_ID)T");
				condition.setQueryString(sql.toString());
				if(StringUtils.isNotBlank(inputVO.getINS_ID()))
				{
					condition.setObject("INS_IDD","%"+inputVO.getINS_ID() +"%");
				}
	    	
	    	}
	    	
	    	if(inputVO.isCheckgi().equals("2")){
				sql.append("SELECT ROWNUM AS NUM,T.* FROM(");
				sql.append("SELECT distinct  INSPRD_ID,INSPRD_NAME FROM TBPRD_INS_MAIN WHERE 1=1");
				
				if(StringUtils.isNotBlank(inputVO.getINS_ID()))
				{
					sql.append(" and INSPRD_ID LIKE :INS_IDD");
				}
				sql.append(" ORDER BY  INSPRD_ID)T");
				condition.setQueryString(sql.toString());
				if(StringUtils.isNotBlank(inputVO.getINS_ID()))
				{
					condition.setObject("INS_IDD","%"+inputVO.getINS_ID() +"%");
				}
	    	}
			
			
			List<Map<String,Object>> list=dam.exeQuery(condition);
			PMS305OutputVO outputVO = new PMS305OutputVO();
			outputVO.setResultList(list);
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	    
	
	  }


	/**
	 * 新增TBPMS_TRACK_PRO_SET
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void ddlModify(Object body, IPrimitiveMap header)
			throws JBranchException {
		String ym = "";
		PMS205InputVO inputVO2 = (PMS205InputVO) body;
		Date date = new Date();
		try {
			DataAccessManager dam = this.getDataAccessManager();
			Calendar calendar = Calendar.getInstance();
			TBPMS_TRACK_PRO_SETVO paramVO = new TBPMS_TRACK_PRO_SETVO();
			TBPMS_TRACK_PRO_SETPK PK = new TBPMS_TRACK_PRO_SETPK();
			// 時間設定字串
			if (calendar.get(Calendar.MONTH) <= 9) {
				ym = calendar.get(Calendar.YEAR) + "0"
						+ calendar.get(Calendar.MONTH);
			}
			if (calendar.get(Calendar.MONTH) > 9) {
				ym = calendar.get(Calendar.YEAR) + ""
						+ calendar.get(Calendar.MONTH);
			}
			SimpleDateFormat sdf=new SimpleDateFormat("YYYYMM");
			PK.setDATA_YEARMON(inputVO2.geteTimes().substring(6));
		    
			PK.setMAIN_COM_NBR(inputVO2.getMAIN_COM_NBR());
			PK.setPRD_ID(inputVO2.getCheckgi());
			paramVO = (TBPMS_TRACK_PRO_SETVO) dam.findByPKey(
					TBPMS_TRACK_PRO_SETVO.TABLE_UID, PK);
			// 如果找到那筆資料
			if (paramVO == null) {
				paramVO = new TBPMS_TRACK_PRO_SETVO();
				paramVO.setcomp_id(PK);
				paramVO.setMAIN_COM_NBR_NAME(getPrdName(inputVO2
						.getMAIN_COM_NBR()));
//				paramVO.setPRD_NAME(inputVO2.getCheckgi());
				paramVO.setEDATE(inputVO2.geteTimes());
				paramVO.setSDATE(inputVO2.getsTimes());
				paramVO.setREL_COM_NBR(inputVO2.getREL_COM_NBR());
				// 新增一筆
				dam.create(paramVO);
			} else {
				// 顯示資料不存在S
				throw new APException("ehl_01_common_008");
			}

			sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));

			throw new APException("資料已存在!!!");
		}

	}

	// 以下是刪除
	public void delRes(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS205InputVO inputVO = (PMS205InputVO) body;
		dam = this.getDataAccessManager();
		try {
			TBPMS_TRACK_PRO_SETPK PK = new TBPMS_TRACK_PRO_SETPK();
			TBPMS_TRACK_PRO_SETVO VO = new TBPMS_TRACK_PRO_SETVO();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMM");
			PK.setDATA_YEARMON(inputVO.geteTime_DEL());
			PK.setMAIN_COM_NBR(inputVO.getMAIN_COM_NBR_DEL());
			PK.setPRD_ID(inputVO.getEs());
			VO = (TBPMS_TRACK_PRO_SETVO) dam.findByPKey(
					TBPMS_TRACK_PRO_SETVO.TABLE_UID, PK);
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

	// 取商品名稱
	private String getPrdName(String prdid) throws JBranchException {
		String prdName = "";
		String[] str = null ;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();
		if(prdid.length()>0){
			str = prdid.split(",");
		}
		sql.append(" SELECT LISTAGG(PNAME, ',') WITHIN GROUP (ORDER BY PRD_ID) AS PRD_LIST ");
		sql.append(" FROM VWPRD_MASTER WHERE 1=1 ");
		//輸入值只有一個的時候
		sql.append(" AND( PRD_ID = '" + str[0] + "'");
		//輸入值多筆的時候
		if(str.length>1){
			for(int i = 1 ; i < str.length ; i++){
				sql.append(" OR PRD_ID = '" + str[i] + "' ");
			}
		}
		sql.append(" ) ");
		condition.setQueryString(sql.toString());
		List<Map<String, String>> map = dam.exeQuery(condition);
		if (map.size() > 0){
			prdName = map.get(0).get("PRD_LIST");
		}
		return prdName;
	}

}
