package com.systex.jbranch.app.server.fps.pms335;

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

import com.systex.jbranch.app.server.fps.pms330.PMS330InputVO;
import com.systex.jbranch.app.server.fps.pms330.PMS330OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :重覆經營客戶報表Controller <br>
 * Comments Name : PMS334.java<br>
 * Author :Kevin<br>
 * Date :2016年06月06日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */
@Component("pms335")
@Scope("request")
public class PMS335 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS335.class);
	/**
	 * 明細查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getDetail(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS335InputVO inputVO = (PMS335InputVO) body;
		PMS335OutputVO return_VO = new PMS335OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			// ==主查詢==
			sql.append(" SELECT ROWNUM,T.* ,C.VIP_DEGREE  ");
			sql.append(" FROM (SELECT * FROM TBPMS_REOP_CUST_RPT_DTL) T ,TBCRM_CUST_MAST C ");
			sql.append(" WHERE 1=1 AND T.CUST_ID = C.CUST_ID ");
			sql.append(" AND T.EMP_ID = :id AND T.YEARMON= :YEMON");

			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("id", inputVO.getEMP_ID());
			queryCondition.setObject("YEMON", inputVO.getYEARMON());
			// 查詢分頁結果result
			ResultIF list = dam.executePaging(queryCondition,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord();
			// 分頁結果
			return_VO.setResultList(list);
			return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			return_VO.setTotalPage(totalPage_i);// 總頁次
			return_VO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	/**
	 * 匯出
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 取得畫面資料csv
		PMS335OutputVO return_VO = (PMS335OutputVO) body;
		List<Map<String, Object>> list = return_VO.getList();
		try {
			if (list.size() > 0) {
				// gen csv
				String.format("%1$,09d", -3123);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "重覆經營客戶報表" + sdf.format(new Date())
						+ "_"+getUserVariable(FubonSystemVariableConsts.LOGINID)+".csv";
				List listCSV = new ArrayList();
				for (Map<String, Object> map : list) {
					// 17 column
					String[] records = new String[13];
					int i = 0;
					records[i] = checkIsNull(map, "YEARMON"); // 資料統計年月
					records[++i] = checkIsNull(map, "REGION_CENTER_ID"); // 區域中心ID
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 區域中心名稱
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID"); // 營運區ID
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區名稱
					records[++i] = checkIsNull(map, "BRANCH_NBR"); // 分行ID
					records[++i] = checkIsNull(map, "BRANCH_NAME"); // 分行名稱
					records[++i] = checkIsNull(map, "AO_CODE"); // AO Code
					records[++i] = checkIsNull(map, "EMP_ID"); // 理專員編
					records[++i] = checkIsNull(map, "EMP_NAME"); // 理專姓名
					records[++i] = checkIsNull(map, "CUST_CNT"); // 客戶數
					records[++i] = checkIsNull(map, "AUM"); // AUM"
					records[++i] = checkIsNull(map, "AVG_CONTRIB"); // 平均貢獻度

					listCSV.add(records);
				}
				// header
				String[] csvHeader = new String[13];
				int j = 0;
				csvHeader[j] = "資料統計年月";
				csvHeader[++j] = "業務處ID";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區ID";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行ID";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "AO Code";
				csvHeader[++j] = "理專員編";
				csvHeader[++j] = "理專姓名";
				csvHeader[++j] = "客戶數";
				csvHeader[++j] = "AUM";
				csvHeader[++j] = "平均貢獻度";

				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader);
				csv.addRecordList(listCSV);
				String url = csv.generateCSV();
				// download
				notifyClientToDownloadFile(url, fileName);
			} else{
				return_VO.setResultList(list);
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS335InputVO inputVO = (PMS335InputVO) body;
		PMS335OutputVO outputVO = new PMS335OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		try {
			// ==主查詢==
			sql.append("SELECT * FROM TBPMS_REOP_CUST_RPT_MAST "
					+ " WHERE 1=1  ");
			// ==主查詢條件==
			// 區域中心
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" and REGION_CENTER_ID LIKE :REGION_CENTER_IDDD");
			}
			// 營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" and BRANCH_AREA_ID LIKE :OP_AREA_IDDD");
			}
			// 分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" and BRANCH_NBR LIKE :BRANCH_NBRR");
			}
			// 員編
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sql.append(" and AO_CODE LIKE :EMP_IDEE");
			}
			// 年月
			if (!StringUtils.isBlank(inputVO.getsCreDate())) {
				sql.append(" and YEARMON LIKE :YEARMONN");
			}
			sql.append(" order by REGION_CENTER_ID,BRANCH_NBR,AO_CODE");
			
			
			condition.setQueryString(sql.toString());

			// ==主查詢條件設定==
			// 區域中心
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				condition.setObject("REGION_CENTER_IDDD",
						"%" + inputVO.getRegion_center_id() + "%");
			}
			// 營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				condition
						.setObject("OP_AREA_IDDD", "%" + inputVO.getBranch_area_id() + "%");
			}
			// 分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				condition.setObject("BRANCH_NBRR", "%" + inputVO.getBranch_nbr()
						+ "%");
			}
			// 理專
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				condition.setObject("EMP_IDEE", "%" + inputVO.getAo_code() + "%");
			}
			// 年月
			if (!StringUtils.isBlank(inputVO.getsCreDate())) {
				condition.setObject("YEARMONN", "%" + inputVO.getsCreDate() + "%");
				System.err.println("STIME     : ===  " + inputVO.getsCreDate());
			}
			// 分頁結果
			ResultIF list = dam.executePaging(condition,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			// csv查詢
			List<Map<String, Object>> csvList = dam.exeQuery(condition);
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList(list); // data
			outputVO.setCsvList(csvList);
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

	/* ==== 【查詢】建表日期 ======== */
	public void queryCDate(Object body, IPrimitiveMap header) throws JBranchException {
		PMS335InputVO inputVO = (PMS335InputVO) body;
		PMS335OutputVO outputVO = new PMS335OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT MAX(CREATETIME) AS CREATDATE FROM TBPMS_REOP_CUST_RPT_MAST WHERE 1=1 ");
		sql.append(" AND YEARMON LIKE :YEARMONN ");

		// 設定時間
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//		String CDate = sdf.format(inputVO.getsCreDate());
		condition.setObject("YEARMONN",inputVO.getReportDate());
		
		condition.setQueryString(sql.toString());
		System.out.println("==================" + inputVO.getReportDate());
		List<Map<String, Object>> list = dam.exeQuery(condition);		
		outputVO.setResultList(list);
		this.sendRtnObject(outputVO);
	}
	
}