package com.systex.jbranch.app.server.fps.pms347;

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

import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :固定收益型商品提前解約損失日報表<br>
 * Comments Name : PMS347java<br>
 * Author : Kevin<br>
 * Date :2016/06/29 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */

@Component("pms347")
@Scope("request")
public class PMS347 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS347.class);

	/**
	 * 匯出確認NULL
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			if(map.get(key)==null)
			return "";
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	/**
	 * 匯出csv
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 取得畫面資料
		PMS347OutputVO return_VO = (PMS347OutputVO) body;
		List<Map<String, Object>> list = return_VO.getList();
		try {
			if (list.size() > 0) {
				// gen csv
				String.format("%1$,09d", -3123);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "固定收益型商品提前解約損失日報表" + sdf.format(new Date())+ "-"
						+ getUserVariable(FubonSystemVariableConsts.LOGINID)
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
				csv.setHeader(csvHeader);
				csv.addRecordList(listCSV);
				String url = csv.generateCSV();
				// download
				notifyClientToDownloadFile(url, fileName);
			} else
				return_VO.setResultList(list);
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/**
	 * 主查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 輸入vo
		PMS347InputVO inputVO = (PMS347InputVO) body;
		// 輸出vo
		PMS347OutputVO outputVO = new PMS347OutputVO();
		dam = this.getDataAccessManager();
		try {
			// ==主查詢==
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE",FormatHelper.FORMAT_2); // 理專
			Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2); // OP
			Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員

			// 取得查詢資料可視範圍
			PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
			PMS000InputVO pms000InputVO = new PMS000InputVO();
			pms000InputVO.setReportDate(sdf.format(inputVO.getsCreDate()));
			PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
			
			StringBuffer sql = new StringBuffer(
					"SELECT ROWNUM AS NUM,T.* FROM "
							+ "(select *  from TBPMS_DAILY_FIPETL WHERE 1=1   ");
			// ==主查詢條件==
			// 區域中心
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				//sql.append(" and REGION_CENTER_ID LIKE :REGION_CENTER_IDDD");
				sql.append("  and BRANCH_NBR IN ( ");
				sql.append("    SELECT BRANCH_NBR ");
				sql.append("    FROM VWORG_DEFN_BRH ");
				sql.append("    WHERE DEPT_ID = :REGION_CENTER_IDDD ");
				sql.append("  ) ");
				condition.setObject("REGION_CENTER_IDDD", "%" + inputVO.getRegion_center_id() + "%");
			}else {
				// 登入非總行人員強制加區域中心
				if (!headmgrMap.containsKey(roleID)) {
					//sql.append("and REGION_CENTER_ID IN (:REGION_CENTER_IDDD) ");
					sql.append("  and BRANCH_NBR IN ( ");
					sql.append("    SELECT BRANCH_NBR ");
					sql.append("    FROM VWORG_DEFN_BRH ");
					sql.append("    WHERE DEPT_ID IN (:REGION_CENTER_IDDD) ");
					sql.append("  ) ");
					condition.setObject("REGION_CENTER_IDDD",pms000outputVO.getV_regionList());
				}
			}
			// 營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				//sql.append(" and BRANCH_AREA_ID LIKE :OP_AREA_IDDD ");
				sql.append("  and BRANCH_NBR IN ( ");
				sql.append("    SELECT BRANCH_NBR ");
				sql.append("    FROM VWORG_DEFN_BRH ");
				sql.append("    WHERE DEPT_ID LIKE :OP_AREA_IDDD ");
				sql.append("  ) ");
				condition .setObject("OP_AREA_IDDD", "%" + inputVO.getBranch_area_id() + "%");
			}else {
				// 登入非總行人員強制加營運區
				if (!headmgrMap.containsKey(roleID)) {
					//sql.append(" and BRANCH_AREA_ID IN (:OP_AREA_IDDD) ");
					sql.append("  and BRANCH_NBR IN ( ");
					sql.append("    SELECT BRANCH_NBR ");
					sql.append("    FROM VWORG_DEFN_BRH ");
					sql.append("    WHERE DEPT_ID IN (:OP_AREA_IDDD) ");
					sql.append("  ) ");
					condition.setObject("OP_AREA_IDDD",pms000outputVO.getV_areaList());
				}
			}
			// 分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" and BRANCH_NBR LIKE :BRANCH_NBRR");
				condition.setObject("BRANCH_NBRR", "%" + inputVO.getBranch_nbr() + "%");
			}else {
				// 登入非總行人員強制加分行
				if (!headmgrMap.containsKey(roleID)) {
					sql.append("and BRANCH_NBR IN (:BRANCH_NBRR) ");
					condition.setObject("BRANCH_NBRR",pms000outputVO.getV_branchList());
				}
			}
			// 起日
			if (inputVO.getsCreDate() != null) {
				sql.append(" and DATA_DATE LIKE :YEARMONN");
			}
			// 員編
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sql.append(" and AO_CODE LIKE :EMP_IDEE");
				condition .setObject("EMP_IDEE", "%" + inputVO.getAo_code() + "%");
			}else {
				// 登入為銷售人員強制加AO_CODE
				if (fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
					sql.append(" and AO_CODE IN (:EMP_IDEE) ");
					condition.setObject("EMP_IDEE", pms000outputVO.getV_aoList());
				}
			}
			// sql排序
			sql.append("   ORDER BY REGION_CENTER_ID , BRANCH_AREA_ID , BRANCH_NBR , AO_CODE , CUST_ID ) T");

			condition.setQueryString(sql.toString());
			// ==主查詢條件設定==
			// 時間
			// 進行轉換
			String dateString = sdf.format(inputVO.getsCreDate());
			// 設定時間
			condition.setObject("YEARMONN","%" + dateString + "%" );
			// 分頁查詢結果
			ResultIF list = dam.executePaging(condition,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			// csv結果
			List<Map<String, Object>> csvList = dam.exeQuery(condition);
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList(list); // data
			outputVO.setCsvList(csvList); // 匯出csv
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

}