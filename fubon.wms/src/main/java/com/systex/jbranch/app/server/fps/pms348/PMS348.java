package com.systex.jbranch.app.server.fps.pms348;

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

import com.systex.jbranch.app.common.fps.table.TBPMS_DAILY_FIPETLPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_DAILY_FIPETLVO;
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
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :固定收益型商品提前解約損失月報表<br>
 * Comments Name : PMS348java<br>
 * Author : Kevin<br>
 * Date :2016/06/30 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */
@Component("pms348")
@Scope("request")
public class PMS348 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS348.class);
	
	/**
	 * 主查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException {

		PMS348InputVO inputVO = (PMS348InputVO) body;
		PMS348OutputVO outputVO = new PMS348OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			//==主查詢SQL==
			sql.append(" SELECT ROWNUM AS NUM,T.* FROM  ");
			sql.append(" (select TBPMS_DAILY_FIPETL.* ,CUST_ID AS NEW_CUST_ID  ");
			sql.append(" from TBPMS_DAILY_FIPETL WHERE 1=1  ");

			//==查詢SQL條件==
			//區域中心
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" and REGION_CENTER_ID LIKE :REGION_CENTER_IDDD");
			}
			//營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" and BRANCH_AREA_ID LIKE :OP_AREA_IDDD");
			}
			//分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" and BRANCH_NBR LIKE :BRANCH_NBRR");
			}
			//時間
			if (inputVO.getsCreDate() != null) {
				sql.append(" and  substr(DATA_DATE,0,6) LIKE :YEARMONN");
			   
			}
			//員編
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sql.append(" and AO_CODE LIKE :EMP_IDEE");
			}
			//排序SQL
			sql.append("   ORDER BY REGION_CENTER_NAME,BRANCH_AREA_NAME,BRANCH_NBR,AO_CODE,CUST_ID ) T");

			condition.setQueryString(sql.toString());
			//==查詢SQL條件設定==
			//區域中心
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				condition.setObject("REGION_CENTER_IDDD",
						"%" + inputVO.getRegion_center_id() + "%");
			}
			//營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				condition.setObject("OP_AREA_IDDD", "%" + inputVO.getBranch_area_id()+ "%");
			}
			//分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				condition.setObject("BRANCH_NBRR", "%" + inputVO.getBranch_nbr()
						+ "%");
			}
			//時間
			
				//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
				// 進行轉換
				//String dateString = sdf.format(inputVO.getsCreDate());
				// 設定時間
				condition.setObject("YEARMONN","%" +inputVO.getsCreDate() + "%");
			
			//員編
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				condition.setObject("EMP_IDEE", "%" + inputVO.getAo_code() + "%");
			}
			//==分頁查詢結果==
			ResultIF list = dam.executePaging(condition,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount()); // 分頁用
			List<Map<String, Object>> list1 = dam.exeQuery(condition); // 匯出全部用
			outputVO.setCsvList(list1); //CSV用LIST
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList(list); // data主查詢
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
		PMS348InputVO inputVO = (PMS348InputVO) body;
		PMS348OutputVO outputVO = new PMS348OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT MAX(CREATETIME) AS CREATDATE FROM TBPMS_DAILY_FIPETL WHERE 1=1 ");
		sql.append(" AND DATA_DATE LIKE :YEARMONN ");
		// 設定時間
		condition.setObject("YEARMONN","%" +inputVO.getsCreDate() + "%");
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(condition);		
		outputVO.setResultList(list);
		this.sendRtnObject(outputVO);
	}
	
	
	
	/* ==== 【儲存】更新資料 ======== */
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		PMS348InputVO inputVO = (PMS348InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		try {
			for (Map<String, Object> map : inputVO.getList()) {
				if(map.get("NOTE")==null)
					map.put("NOTE", "");
				for (Map<String, Object> map2 : inputVO.getList2()) {
					if(map2.get("NOTE")==null)
						map2.put("NOTE", "");
					if (map.get("DATA_DATE").equals(map2.get("DATA_DATE"))
							&& map.get("TXN_NO").equals(map2.get("TXN_NO"))
							&& (!map.get("NOTE").equals(map2.get("NOTE")))) {

						StringBuffer Sb = new StringBuffer(map.get("DATA_DATE")
								.toString());
						Sb.insert(4, "-");
						Sb.insert(7, "-");
						Sb.append("  00:00:00");
						TBPMS_DAILY_FIPETLPK PK = new TBPMS_DAILY_FIPETLPK();
						PK.setTXN_NO(map.get("TXN_NO").toString());
						PK.setDATA_DATE(map.get("DATA_DATE").toString());

						TBPMS_DAILY_FIPETLVO PARAMVO = (TBPMS_DAILY_FIPETLVO) dam
								.findByPKey(TBPMS_DAILY_FIPETLVO.TABLE_UID, PK);
						PARAMVO.setNOTE(map.get("NOTE").toString());
						//修改者
						PARAMVO.setModifier((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
						//修改時間
						PARAMVO.setLastupdate(stamp);
						dam.update(PARAMVO);
					}

				}
			}
			sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}


	/* === 產出Excel==== */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 取得畫面資料
		PMS348OutputVO return_VO = (PMS348OutputVO) body;

		List<Map<String, Object>> list = return_VO.getList();
		try {
			if (list.size() > 0) {
				// gen csv
				String.format("%1$,09d", -3123);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "固定收益型商品提前解約損失報月表" + sdf.format(new Date())
						+ ".csv";
				List listCSV = new ArrayList();
				for (Map<String, Object> map : list) {
					// 21 column
					String[] records = new String[22];
					int i = 0;
					records[i] = checkIsNull(map, "DATA_DATE"); // 資料統計日期
					records[++i] = checkIsNull(map, "REGION_CENTER_ID"); // 區域中心ID
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 區域中心名稱
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID"); // 營運區ID
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區名稱
					records[++i] = checkIsNull(map, "BRANCH_NBR"); // 分行ID
					records[++i] = checkIsNull(map, "BRANCH_NAME"); // 分行名稱
					records[++i] = checkIsNull(map, "AO_CODE"); // AO CODE
					records[++i] = checkIsNull(map, "EMP_ID"); // 理專員編
					records[++i] = checkIsNull(map, "EMP_NAME"); // 理專姓名
					records[++i] = checkIsNull(map, "CUST_ID"); // 客戶ID
					records[++i] = checkIsNull(map, "CUST_NAME"); // 客戶姓名
					records[++i] = "=\"" +checkIsNull(map, "PRD_ID")+ "\""; // 商品代號
					records[++i] = checkIsNull(map, "PRD_NAME"); // 商品名稱
					records[++i] = "=\"" +checkIsNull(map, "TXN_NO")+ "\""; // 交易編號
					records[++i] = checkIsNull(map, "S_TXN_AMT"); // 實際贖回價格(台幣)
					records[++i] = checkIsNull(map, "INTEREST"); // 累計配息(台幣)
					records[++i] = checkIsNull(map, "S_TXN_FEE"); // 贖回手續費(台幣)
					records[++i] = checkIsNull(map, "B_TXN_AMT"); // 原始申購本金(台幣)
					records[++i] = checkIsNull(map, "B_TXN_FEE"); // 申購手續費(台幣)
					records[++i] = checkIsNull(map, "S_TXN_LOSS"); // 提前解約損失(台幣)
					records[++i] = checkIsNull(map, "NOTE"); // 處理備註
			
					listCSV.add(records);
				}
				// header
				String[] csvHeader = new String[22];
				int j = 0;
				csvHeader[j] = "資料統計日期";
				csvHeader[++j] = "業務處ID";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區ID";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行ID";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "AO CODE";
				csvHeader[++j] = "理專員編";
				csvHeader[++j] = "理專姓名";
				csvHeader[++j] = "客戶ID";
				csvHeader[++j] = "客戶姓名";
				csvHeader[++j] = "商品代號";
				csvHeader[++j] = "商品名稱";
				csvHeader[++j] = "交易編號";
				csvHeader[++j] = "實際贖回價格(台幣)";
				csvHeader[++j] = "累計配息(台幣)";
				csvHeader[++j] = "贖回手續費(台幣)";
				csvHeader[++j] = "原始申購本金(台幣)";
				csvHeader[++j] = "申購手續費(台幣)";
				csvHeader[++j] = "提前解約損失(台幣)";
				csvHeader[++j] = "處理備註";
			

				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader);	//表頭
				csv.addRecordList(listCSV);	//內容
				String url = csv.generateCSV();
				// download
				notifyClientToDownloadFile(url, fileName);
			} else
				return_VO.setResultList(list);
			this.sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
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
}