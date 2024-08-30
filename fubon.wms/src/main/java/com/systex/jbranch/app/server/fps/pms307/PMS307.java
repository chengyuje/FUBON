package com.systex.jbranch.app.server.fps.pms307;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :保險核實報表查詢 Controller <br>
 * Comments Name : PMS307.java<br>
 * Author :Kevin<br>
 * Date :2016年08月04日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
@Component("pms307")
@Scope("request")

public class PMS307 extends FubonWmsBizLogic {
	public DataAccessManager dam = null;

	/**
	 * 主查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException {
		//輸入VO
		PMS307InputVO inputVO = (PMS307InputVO) body;
		//輸出VO
		PMS307OutputVO outputVO = new PMS307OutputVO();
		
		
		String roleType = "";
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);         //理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);     //OP
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);    //個金主管
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);   //營運督導
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);   //區域中心主管
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
			
		//取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getReportDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);

		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		// ===取得理專員額表
		StringBuffer sb = new StringBuffer();
		try {
			//==主查詢== TBPMS_INS_VERIFY_PRT
			sb.append("select rownum as num,t.* from ");
			
			if("1".equals(inputVO.getSeture1()))
			sb.append("(select * from TBPMS_INS_VERIFY_DAY  ");
			else if("2".equals(inputVO.getSeture1()))
			sb.append("(select * from TBPMS_INS_VERIFY_MON  ");
			else if("3".equals(inputVO.getSeture1()))
			sb.append("(select * from TBPMS_INS_TRACE_DAY  ");
			
			sb.append("order by "
					+ "REGION_CENTER_ID,"
					+ "BRANCH_AREA_ID,"
					+ "BRANCH_NBR) t  "
					+ "where  BRANCH_NBR >= '200' AND BRANCH_NBR <= '900' AND LENGTH(BRANCH_NBR) = 3  "
					+ "AND TO_NUMBER(BRANCH_NBR) <> 806 AND TO_NUMBER(BRANCH_NBR) <> 810  ");
			//==主查詢條件==
			//區域中心
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sb.append(" and REGION_CENTER_ID LIKE :REGION_CENTER_IDDD");
				queryCondition.setObject("REGION_CENTER_IDDD","%" + inputVO.getRegion_center_id() + "%");
			}else{
				//登入非總行人員強制加區域中心
				if(!headmgrMap.containsKey(roleID)) {
					sb.append("and REGION_CENTER_ID IN (:region_center_id) ");
					queryCondition.setObject("region_center_id", pms000outputVO.getV_regionList());
				}
			}
			//營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sb.append(" and BRANCH_AREA_ID LIKE :OP_AREA_IDDD");
				queryCondition.setObject("OP_AREA_IDDD", "%" + inputVO.getBranch_area_id() + "%");
			}else{
				//登入非總行人員強制加營運區
				if(!headmgrMap.containsKey(roleID)) {
					sb.append("  and BRANCH_AREA_ID IN (:branch_area_id) ");
					queryCondition.setObject("branch_area_id", pms000outputVO.getV_areaList());
				}
			}
			//分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sb.append(" and BRANCH_NBR LIKE :BRANCH_NBRR");
				queryCondition.setObject("BRANCH_NBRR","%" + inputVO.getBranch_nbr() + "%");
			}else{
				//登入非總行人員強制加分行
				if(!headmgrMap.containsKey(roleID)) {		
					sb.append("  and BRANCH_NBR IN (:branch_nbr) ");
					queryCondition.setObject("branch_nbr", pms000outputVO.getV_branchList());
				}
			}
			
			
			
			if (inputVO.getsCreDate() != null) {
				
			   if("1".equals(inputVO.getSeture1())||"3".equals(inputVO.getSeture1())){
				   sb.append(" and DATA_DATE = :RPT_DATEE");
				   SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				   // 進行轉換
				   String dateString = sdf.format(inputVO.getsCreDate());
				   queryCondition.setObject("RPT_DATEE", dateString);
			   } else if("2".equals(inputVO.getSeture1())){  
				   sb.append(" and YEARMON = :RPT_DATEE");
				   queryCondition.setObject("RPT_DATEE", inputVO.getReportDate());	   
			   }
			}

			queryCondition.setQueryString(sb.toString());
			
			//分頁查詢結果 		**分頁，暫不需要
//			ResultIF list = dam.executePaging(queryCondition,inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			//int totalPage_i = list.getTotalPage();
			//int totalRecord_i = list.getTotalRecord();
			// csv 共用查詢S
			List<Map<String, Object>> csvList = dam.exeQuery(queryCondition); // 匯出全部用
			
			outputVO.setCsvList(csvList);  //csv
			outputVO.setResultList(list); //畫面

			
			//**分頁，暫不需要
//			if (list.size() > 0) {
//				outputVO.setTotalPage(list.getTotalPage());
//				outputVO.setResultList(list);
//				outputVO.setTotalRecord(list.getTotalRecord());
//				outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
//				sendRtnObject(outputVO);
//			} else {
//				throw new APException("ehl_01_common_009");
//			}
			
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

	/**
	 * 匯出
	 * 
	 * @return
	 * @throws JBranchException
	 */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException, FileNotFoundException, IOException {
		PMS307OutputVO return_VO = (PMS307OutputVO) body;

		List<Map<String, Object>> list = return_VO.getList();
		try {
			if (list.size() > 0) {
				// gen csv
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "保險核實報表查詢" + sdf.format(new Date())+ "-"
						+ getUserVariable(FubonSystemVariableConsts.LOGINID)
						+ ".csv";
				List listCSV = new ArrayList();
				
				if("1".equals(return_VO.getChecked()))
				{ 
					for (Map<String, Object> map : list) {
				
					// 41 column
					String[] records = new String[24];
					int i = 0;
					records[i] = checkIsNull(map, "REGION_CENTER_NAME"); // 營運中心ID
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區
					records[++i] = checkIsNull(map, "BRANCH_NBR"); // 分行代號
					records[++i] = checkIsNull(map, "BRANCH_NAME"); // 營業單位
					records[++i] = checkIsNull(map, "GROUP_ID"); // 組別
					
					records[++i] = checkIsNull(map, "BASE_TX_CNT"); // 保險戰報實績-件數
					records[++i] = checkIsNull(map, "BASE_ANNU_ACT_FEE"); // 保險戰報實績-預估收益
					
					records[++i] = checkIsNull(map, "DAY_CLOSE_CNT"); // 已核實預估佣收-件數
					records[++i] = checkIsNull(map, "DAY_CLOSE_COMIS_Y"); // 已核實預估佣收-核實佣收
					records[++i] = checkIsNull(map, "DAY_CLOSE_PETG"); // 已核實預估佣收-比例
					
					records[++i] = checkIsNull(map, "B_CNT"); // 核保通過,待結案-件數
					records[++i] = checkIsNull(map, "B_ANNU_ACT_FEE"); // 核保通過,待結案-預估收益
					records[++i] = checkIsNull(map, "B_PETG"); // 核保通過,待結案-比例
					
					records[++i] = checkIsNull(map, "DE_CNT"); // 照會中-件數
					records[++i] = checkIsNull(map, "DE_ANNU_ACT_FEE"); // 照會中-預估收益
					records[++i] = checkIsNull(map, "DE_PETG"); // 照會中-比例
					
					records[++i] = checkIsNull(map, "C_CNT"); // 核保中-件數
					records[++i] = checkIsNull(map, "C_ANNU_ACT_FEE"); // 核保中-預估收益
					records[++i] = checkIsNull(map, "C_PETG"); // 核保中-比例
					
					records[++i] = checkIsNull(map, "FGHI_CNT"); // 契撤/取消/拒保/延期-件數
					records[++i] = checkIsNull(map, "FGHI_ANNU_ACT_FEE"); // 契撤/取消/拒保/延期-預估收益
					records[++i] = checkIsNull(map, "FGHI_PETG"); // 契撤/取消/拒保/延期-比例
					
					records[++i] = checkIsNull(map, "J_CNT"); // 其他-件數
					records[++i] = checkIsNull(map, "J_ANNU_ACT_FEE"); // 其他-預估收益
					
				

					listCSV.add(records);
				}
				// header
				String[] csvHeader = new String[24];
				int j = 0;
				csvHeader[j] = "業務處ID";
				csvHeader[++j] = "營運區";
				csvHeader[++j] = "分行代號";
				csvHeader[++j] = "營業單位";
				csvHeader[++j] = "組別";
				
				csvHeader[++j] = "保險戰報實績-件數";
				csvHeader[++j] = "保險戰報實績-預估收益";
				csvHeader[++j] = "已核實預估佣收-件數";
				csvHeader[++j] = "已核實預估佣收-核實佣收";
				csvHeader[++j] = "已核實預估佣收-比例";
				csvHeader[++j] = "核保通過,待結案-件數";
				csvHeader[++j] = "核保通過,待結案-預估收益";
				csvHeader[++j] = "核保通過,待結案-比例";
				csvHeader[++j] = "照會中-件數";
				csvHeader[++j] = "照會中-件數";
				csvHeader[++j] = "照會中-比例";
				csvHeader[++j] = "核保中-件數";
				csvHeader[++j] = "核保中-預估收益";
				csvHeader[++j] = "核保中-比例";
				csvHeader[++j] = "契撤/取消/拒保/延期-件數";
				csvHeader[++j] = "契撤/取消/拒保/延期-預估收益";
				csvHeader[++j] = "契撤/取消/拒保/延期-比例";
				csvHeader[++j] = "其他-件數";
				csvHeader[++j] = "其他-預估收益";
				
			

				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader); // 設定標頭
				csv.addRecordList(listCSV); // 設定內容
				String url = csv.generateCSV();
				// download
				notifyClientToDownloadFile(url, fileName);
				 }
				else if("2".equals(return_VO.getChecked()))
				 {
					for (Map<String, Object> map : list) {
						
						// 41 column
						String[] records = new String[25];
						int i = 0;
						records[i] = checkIsNull(map, "REGION_CENTER_NAME"); // 營運中心ID
						records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區
						records[++i] = checkIsNull(map, "BRANCH_NBR"); // 分行代號
						records[++i] = checkIsNull(map, "BRANCH_NAME"); // 營業單位
						records[++i] = checkIsNull(map, "GROUP_ID"); // 組別
						
						records[++i] = checkIsNull(map, "BASE_TX_CNT"); // 保險戰報實績-件數
						records[++i] = checkIsNull(map, "BASE_ANNU_ACT_FEE"); // 保險戰報實績-預估收益
						
						records[++i] = checkIsNull(map, "DAY_CLOSE_CNT"); // 已核實預估佣收-件數
						records[++i] = checkIsNull(map, "DAY_CLOSE_COMIS_Y"); // 已核實預估佣收-核實佣收
						records[++i] = checkIsNull(map, "DAY_CLOSE_PETG"); // 已核實預估佣收-比例
						
						records[++i] = checkIsNull(map, "B_CNT"); // 核保通過,待結案-件數
						records[++i] = checkIsNull(map, "B_ANNU_ACT_FEE"); // 核保通過,待結案-預估收益
						records[++i] = checkIsNull(map, "B_PETG"); // 核保通過,待結案-比例
						
						records[++i] = checkIsNull(map, "DE_CNT"); // 照會中-件數
						records[++i] = checkIsNull(map, "DE_ANNU_ACT_FEE"); // 照會中-預估收益
						records[++i] = checkIsNull(map, "DE_PETG"); // 照會中-比例
						
						records[++i] = checkIsNull(map, "C_CNT"); // 核保中-件數
						records[++i] = checkIsNull(map, "C_ANNU_ACT_FEE"); // 核保中-預估收益
						records[++i] = checkIsNull(map, "C_PETG"); // 核保中-比例
						
						records[++i] = checkIsNull(map, "FGHI_CNT"); // 契撤/取消/拒保/延期-件數
						records[++i] = checkIsNull(map, "FGHI_ANNU_ACT_FEE"); // 契撤/取消/拒保/延期-預估收益
						records[++i] = checkIsNull(map, "FGHI_PETG"); // 契撤/取消/拒保/延期-比例
						
						records[++i] = checkIsNull(map, "J_CNT"); // 其他-件數
						records[++i] = checkIsNull(map, "J_ANNU_ACT_FEE"); // 其他-預估收益
						records[++i] = checkIsNull(map, "J_PETG"); // 其他-比例
					
						
						listCSV.add(records);
					}
					// header
					String[] csvHeader = new String[25];
					int j = 0;
					csvHeader[j] = "業務處ID";
					csvHeader[++j] = "營運區";
					csvHeader[++j] = "分行代號";
					csvHeader[++j] = "營業單位";
					csvHeader[++j] = "組別";
					
					csvHeader[++j] = "保險戰報實績-件數";
					csvHeader[++j] = "保險戰報實績-預估收益";
					csvHeader[++j] = "已核實預估佣收-件數";
					csvHeader[++j] = "已核實預估佣收-核實佣收";
					csvHeader[++j] = "已核實預估佣收-比例";
					csvHeader[++j] = "核保通過,待結案-件數";
					csvHeader[++j] = "核保通過,待結案-預估收益";
					csvHeader[++j] = "核保通過,待結案-比例";
					csvHeader[++j] = "照會中-件數";
					csvHeader[++j] = "照會中-件數";
					csvHeader[++j] = "照會中-比例";
					csvHeader[++j] = "核保中-件數";
					csvHeader[++j] = "核保中-預估收益";
					csvHeader[++j] = "核保中-比例";
					csvHeader[++j] = "契撤/取消/拒保/延期-件數";
					csvHeader[++j] = "契撤/取消/拒保/延期-預估收益";
					csvHeader[++j] = "契撤/取消/拒保/延期-比例";
					csvHeader[++j] = "其他-件數";
					csvHeader[++j] = "其他-預估收益";
					csvHeader[++j] = "其他-比例";
				
					
					CSVUtil csv = new CSVUtil();
					csv.setHeader(csvHeader); // 設定標頭
					csv.addRecordList(listCSV); // 設定內容
					String url = csv.generateCSV();
					// download
					notifyClientToDownloadFile(url, fileName);
				  }
				else if("3".equals(return_VO.getChecked()))
				{
					for (Map<String, Object> map : list) {
						
						// 41 column
						String[] records = new String[23];
						int i = 0;
						records[i] = checkIsNull(map, "REGION_CENTER_NAME"); // 營運中心ID
						records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區
						records[++i] = checkIsNull(map, "BRANCH_NBR"); // 分行代號
						records[++i] = checkIsNull(map, "BRANCH_NAME"); // 營業單位
						records[++i] = checkIsNull(map, "GROUP_ID"); // 組別
						
						records[++i] = checkIsNull(map, "A_CNT"); // 已發單已結案-件數
						records[++i] = checkIsNull(map, "A_ANNU_ACT_FEE"); // 已發單已結案-核實佣收
						records[++i] = checkIsNull(map, "A_PETG"); // 已發單已結案-比例
						
						records[++i] = checkIsNull(map, "B_CNT"); // 核保通過,待結案-件數
						records[++i] = checkIsNull(map, "B_ANNU_ACT_FEE"); // 核保通過,待結案-預估收益
						records[++i] = checkIsNull(map, "B_PETG"); // 核保通過,待結案-比例
						
						records[++i] = checkIsNull(map, "DE_CNT"); // 照會中-件數
						records[++i] = checkIsNull(map, "DE_ANNU_ACT_FEE"); // 照會中-預估收益
						records[++i] = checkIsNull(map, "DE_PETG"); // 照會中-比例
						
						records[++i] = checkIsNull(map, "C_CNT"); // 核保中-件數
						records[++i] = checkIsNull(map, "C_ANNU_ACT_FEE"); // 核保中-預估收益
						records[++i] = checkIsNull(map, "C_PETG"); // 核保中-比例
						
						records[++i] = checkIsNull(map, "FGHI_CNT"); // 契撤/取消/拒保/延期-件數
						records[++i] = checkIsNull(map, "FGHI_ANNU_ACT_FEE"); // 契撤/取消/拒保/延期-預估收益
						records[++i] = checkIsNull(map, "FGHI_PETG"); // 契撤/取消/拒保/延期-比例
						
						records[++i] = checkIsNull(map, "J_CNT"); // 其他-件數
						records[++i] = checkIsNull(map, "J_ANNU_ACT_FEE"); // 其他-預估收益
						records[++i] = checkIsNull(map, "J_PETG"); // 其他-比例
					

						listCSV.add(records);
					}
					// header
					String[] csvHeader = new String[23];
					int j = 0;
					csvHeader[j] = "業務處ID";
					csvHeader[++j] = "營運區";
					csvHeader[++j] = "分行代號";
					csvHeader[++j] = "營業單位";
					csvHeader[++j] = "組別";
					csvHeader[++j] = "已發單已結案-件數";
					csvHeader[++j] = "已發單已結案-核實佣收";
					csvHeader[++j] = "已發單已結案-比例";
					csvHeader[++j] = "核保通過,待結案-件數";
					csvHeader[++j] = "核保通過,待結案-預估收益";
					csvHeader[++j] = "核保通過,待結案-比例";
					csvHeader[++j] = "照會中-件數";
					csvHeader[++j] = "照會中-件數";
					csvHeader[++j] = "照會中-比例";
					csvHeader[++j] = "核保中-件數";
					csvHeader[++j] = "核保中-預估收益";
					csvHeader[++j] = "核保中-比例";
					csvHeader[++j] = "契撤/取消/拒保/延期-件數";
					csvHeader[++j] = "契撤/取消/拒保/延期-預估收益";
					csvHeader[++j] = "契撤/取消/拒保/延期-比例";
					csvHeader[++j] = "其他-件數";
					csvHeader[++j] = "其他-預估收益";
					csvHeader[++j] = "其他-比例";

					CSVUtil csv = new CSVUtil();
					csv.setHeader(csvHeader); // 設定標頭
					csv.addRecordList(listCSV); // 設定內容
					String url = csv.generateCSV();
					// download
					notifyClientToDownloadFile(url, fileName);	
				}
				
				
			    } else

				this.sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

	/**
	 * 檢查字串
	 * 
	 * @return
	 * @throws JBranchException
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	/**
	 * 檢查數字
	 * 
	 * @return
	 * @throws JBranchException
	 */
	private String checkIsNu(Map map, String key) {

		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "0";
		}
	}

	/**
	 * 取得temp資料夾絕對路徑
	 * 
	 * @return
	 * @throws JBranchException
	 */
	private String getTempPath() throws JBranchException {
		String serverPath = (String) getCommonVariable(SystemVariableConsts.SERVER_PATH);
		String seperator = System.getProperties().getProperty("file.separator");
		if (!serverPath.endsWith(seperator)) {
			serverPath += seperator;
		}
		return serverPath + "temp";
	}
}
