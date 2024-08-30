package com.systex.jbranch.app.server.fps.pms228;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BRPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BRVO;
import com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BR_NAMEPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BR_NAMEVO;
import com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_SETPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_SETVO;
import com.systex.jbranch.app.common.fps.table.TBPMS_PS_PRD_TAR_MPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_PS_PRD_TAR_MVO;
import com.systex.jbranch.app.server.fps.pms345.PMS345OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :<br>
 * Comments Name : PMS228.java<br>
 * Author : KevinHsu<br>
 * Date :2017年03月17日 <br>
 * Version : 1.0 <br>
 * Editor : <br>
 * Editor Date : 2017年01月12日<br>
 */

@Component("pms228")
@Scope("request")
public class PMS228 extends BizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS228.class);
	private Timestamp stamp = new Timestamp(System.currentTimeMillis());
	/** ===== 查詢資料 ===== **/
	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS228InputVO inputVO = (PMS228InputVO) body;
		PMS228OutputVO outputVO = new PMS228OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		ArrayList<String> sql_list = new ArrayList<String>();
		QueryConditionIF condition = dam.getQueryCondition();
		
//		StringBuffer sql = new StringBuffer("select ROWNUM, t.* from ( ");
		StringBuffer sql = new StringBuffer();
		// ==== 查詢 「匯入報表資料」====
		sql.append("   SELECT                        ");
		sql.append("   DATA_YEARMON                  ");
		sql.append("   ,REGION_CENTER_ID             ");
		sql.append("   ,REGION_CENTER_NAME           ");
		sql.append("   ,BRANCH_AREA_ID               ");
		sql.append("   ,BRANCH_AREA_NAME             ");
		sql.append("   ,BRANCH_NBR                   ");
		sql.append("   ,BRANCH_NAME                  ");
		sql.append("   ,EMP_ID                       ");
		sql.append("   ,AO_CODE                      ");
		sql.append("   ,EMP_NAME                     ");
		sql.append("   ,JOB_TITLE_ID                 ");
		sql.append("   ,FIRST_DATE                   ");
		sql.append("   ,JOB_TITLE                    ");
		sql.append("   ,HB_AMT                       ");
		sql.append("   ,NHB_AMT                      ");
		sql.append("   ,S_HB_AMT                     ");
		sql.append("   ,C_HB_AMT                     ");
		sql.append("   ,NC_AMT                       ");
		sql.append("   ,GC_AMT                       ");
		sql.append("   ,CC_AMT                       ");
		sql.append("   ,MAINTAIN_DATE                ");
		sql.append("   ,MAINTAIN_NAME                ");
		sql.append("   ,CREATETIME                   ");
		sql.append("   FROM TBPMS_PS_PRD_TAR_M       ");
		sql.append("   WHERE 1=1                     ");

		// 資料統計月份
		if (inputVO.getReportDate() != null
				&& !inputVO.getReportDate().equals("")) {
		sql.append("and DATA_YEARMON = ? ");
			sql_list.add(inputVO.getReportDate());
//			condition.setObject("DATA_YEARMON", inputVO.getReportDate());
		}

		for (int sql_i = 0; sql_i < sql_list.size(); sql_i++) {
			condition.setString(sql_i + 1, sql_list.get(sql_i));
		}
		condition.setQueryString(sql.toString());
	
		outputVO.setTotalList(dam.exeQuery(condition));
		
		ResultIF list = dam.executePaging(condition,
				inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			
		if (list.size() > 0) {
			int totalPage = list.getTotalPage();
			outputVO.setTotalPage(totalPage);
			outputVO.setResultList(list);
			outputVO.setTotalRecord(list.getTotalRecord());

			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}

	

	/** ==== 整批匯入 ==== **/
	public void insertCSVFile(Object body, IPrimitiveMap header)
			throws Exception {
		dam = this.getDataAccessManager();
		PMS228InputVO inputVO = (PMS228InputVO) body;
		PMS228OutputVO OutputVO = new PMS228OutputVO();
		//TBPMS_PS_PRD_TAR_M
		try {
			Path path = Paths.get(new File((String) SysInfo
					.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO
					.getFileName()).toString());
			List<String> lines = FileUtils.readLines(new File(path.toString()),
					"big5");
			// ==第二開始表內容==
			for (int i = 0; i < lines.size(); i++) {
				String[] str = lines.get(i).split(",");
				if (i != 0) {
					for (int j = 0; j < str.length; j++) {
						if(!str[0].toString().equals("") && !str[1].toString().equals("")){
							
							List<Map<String, String>> list=getORGInfo(str[0].toString(),str[1].toString());
							if(list.size()>0){
								TBPMS_PS_PRD_TAR_MPK PK=new TBPMS_PS_PRD_TAR_MPK();
								PK.setBRANCH_AREA_ID(list.get(0).get("BRANCH_AREA_ID"));
								PK.setBRANCH_NBR(list.get(0).get("BRANCH_NBR"));
								PK.setDATA_YEARMON(str[0].toString());
								PK.setEMP_ID(str[1].toString());
								PK.setREGION_CENTER_ID(list.get(0).get("REGION_CENTER_ID"));
								//查詢TBPMS_PS_PRD_TAR_M有無資料
								TBPMS_PS_PRD_TAR_MVO vo = (TBPMS_PS_PRD_TAR_MVO) dam.findByPKey(
										TBPMS_PS_PRD_TAR_MVO.TABLE_UID,PK);
								if(vo==null){
									//ao_code
									vo=new TBPMS_PS_PRD_TAR_MVO();
									vo.setAO_CODE(list.get(0).get("AO_CODE")!=""?list.get(0).get("AO_CODE"):"");
//									vo.setAO_CODE(list.get(0).get("AO_CODE").toString());
									//營運區
									vo.setBRANCH_AREA_NAME(list.get(0).get("BRANCH_AREA_ID")!=""?list.get(0).get("BRANCH_AREA_ID"):"");
									//分行名稱
									vo.setBRANCH_NAME(list.get(0).get("BRANCH_NAME")!=""?list.get(0).get("BRANCH_NAME"):"");
									StringBuffer sql = new StringBuffer(list.get(0).get("ONBOARD_DATE"));
//									sql.insert(4, "-");
//									sql.insert(7, "-");
									sql.append(" 00:00:00");
									//到職日時間
									SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
									
									Timestamp ts=Timestamp.valueOf(sql.toString());
									vo.setFIRST_DATE(ts);
									//區域中心
									vo.setREGION_CENTER_NAME(list.get(0).get("REGION_CENTER_NAME")!=""?list.get(0).get("REGION_CENTER_NAME"):"");
									//職級
									vo.setJOB_TITLE_ID(list.get(0).get("JOB_TITLE_ID"));
									//職稱
									vo.setJOB_TITLE(list.get(0).get("JOB_TITLE"));
									//員工姓名
									vo.setEMP_NAME(list.get(0).get("EMP_NAME"));
									//Pk
									vo.setcomp_id(PK);
									//維護日期
									vo.setMAINTAIN_DATE(stamp);
									//維護人員
//									購屋         HB_AMT
									vo.setHB_AMT(new BigDecimal(str[2].toString()).multiply(new BigDecimal("1000000")));
//									房貸-非購屋  NHB_AMT
									vo.setNHB_AMT(new BigDecimal(str[3].toString()).multiply(new BigDecimal("1000000")));
//									分期型       S_HB_AMT
									vo.setS_HB_AMT(new BigDecimal(str[4].toString()).multiply(new BigDecimal("1000000")));
//									循環型       C_HB_AMT
									vo.setC_HB_AMT(new BigDecimal(str[5].toString()).multiply(new BigDecimal("1000000")));
//									信貸-一般    NC_AMT
									vo.setNC_AMT(new BigDecimal(str[6].toString()).multiply(new BigDecimal("1000000")));
//									信貸-職團    GC_AMT
									vo.setGC_AMT(new BigDecimal(str[7].toString()).multiply(new BigDecimal("1000000")));
//									信貸-卡友    CC_AMT
									vo.setCC_AMT(new BigDecimal(str[8].toString()).multiply(new BigDecimal("1000000")));
									vo.setMAINTAIN_NAME((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
									
									dam.create(vo);
								}else{
									//ao_code
									vo.setAO_CODE(list.get(0).get("AO_CODE")!=""?list.get(0).get("AO_CODE"):"");
									//營運區
									vo.setBRANCH_AREA_NAME(list.get(0).get("BRANCH_AREA_ID")!=""?list.get(0).get("BRANCH_AREA_ID"):"");
									//分行名稱
									vo.setBRANCH_NAME(list.get(0).get("BRANCH_NAME")!=""?list.get(0).get("BRANCH_NAME"):"");
									StringBuffer sql = new StringBuffer(list.get(0).get("ONBOARD_DATE").toString());
									
									sql.append(" 00:00:00");
									//到職日時間
								
									Timestamp ts=Timestamp.valueOf(sql.toString());
									vo.setFIRST_DATE(ts);
									//區域中心
									vo.setREGION_CENTER_NAME(list.get(0).get("REGION_CENTER_NAME")!=""?list.get(0).get("REGION_CENTER_NAME"):"");
									//職級
									vo.setJOB_TITLE_ID(list.get(0).get("JOB_TITLE_ID"));
									//職稱
									vo.setJOB_TITLE(list.get(0).get("JOB_TITLE_NAME"));
									//員工姓名
									vo.setEMP_NAME(list.get(0).get("EMP_NAME"));
									//維護日期
									vo.setMAINTAIN_DATE(stamp);
									//維護人員
									vo.setMAINTAIN_NAME((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
//									購屋         HB_AMT
									vo.setHB_AMT(new BigDecimal(str[2].toString()).multiply(new BigDecimal("1000000")));
//									房貸-非購屋  NHB_AMT
									vo.setNHB_AMT(new BigDecimal(str[3].toString()).multiply(new BigDecimal("1000000")));
//									分期型       S_HB_AMT
									vo.setS_HB_AMT(new BigDecimal(str[4].toString()).multiply(new BigDecimal("1000000")));
//									循環型       C_HB_AMT
									vo.setC_HB_AMT(new BigDecimal(str[5].toString()).multiply(new BigDecimal("1000000")));
//									信貸-一般    NC_AMT
									vo.setNC_AMT(new BigDecimal(str[6].toString()).multiply(new BigDecimal("1000000")));
//									信貸-職團    GC_AMT
									vo.setGC_AMT(new BigDecimal(str[7].toString()).multiply(new BigDecimal("1000000")));
//									信貸-卡友    CC_AMT
									vo.setCC_AMT(new BigDecimal(str[8].toString()).multiply(new BigDecimal("1000000")));
									dam.update(vo);
								}
							}else{
								//傳訊息到前端
//								this.sendRtnObject(null);
							}
						}
					}
				}
			}
			this.sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("CSV必須上傳,或格式有誤請重新上傳CSV!!!");
		}
	}

	/** 取區域中心、營運區、分行資訊 **/
	public List<Map<String, String>> getORGInfo(String yyyymm, String EMP_ID)
			throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();
		sql.append("   SELECT REC.region_center_id,                                          ");
		sql.append("          REC.region_center_name,                                        ");
		sql.append("          REC.branch_area_id,                                            ");
		sql.append("          REC.branch_area_name,                                          ");
		sql.append("          REC.branch_nbr,                                                ");
		sql.append("          REC.branch_name,TO_CHAR(ORG.ONBOARD_DATE,'YYYY-MM-DD') as ONBOARD_DATE,                                               ");
		sql.append("          ORG.emp_id,                                                    ");
		sql.append("          ORG.emp_name,                                                  ");
		sql.append("          AO.ao_code,                                                    ");
		sql.append("          NREC.ao_job_rank as JOB_TITLE_ID,                                              ");
		sql.append("          NREC.job_title_name                                            ");
		sql.append("   FROM   tborg_member ORG                                               ");
		sql.append("          LEFT JOIN tbpms_org_rec_n REC                                  ");
		sql.append("                 ON REC.branch_nbr = ORG.dept_id                         ");
		sql.append("                    AND Last_day(To_date(:yyyymm                        ");
		sql.append("                                         || '01', 'yyyymmDD')) BETWEEN   ");
		sql.append("                        REC.start_time AND REC.end_time                  ");
		sql.append("          LEFT JOIN tbpms_sales_aocode_rec AO                            ");
		sql.append("                 ON AO.emp_id = ORG.emp_id                               ");
		sql.append("                    AND type = '1'                                       ");
		sql.append("                    AND Last_day(To_date(:yyyymm                        ");
		sql.append("                                         || '01', 'yyyymmDD')) BETWEEN   ");
		sql.append("                        AO.start_time AND AO.end_time                    ");
		sql.append("          LEFT JOIN tbpms_employee_rec_n NREC                            ");
		sql.append("                 ON NREC.emp_id = ORG.emp_id                             ");
		sql.append("                    AND Last_day(To_date(:yyyymm                        ");
		sql.append("                                         || '01', 'yyyymmDD')) BETWEEN   ");
		sql.append("                        AO.start_time AND AO.end_time                    ");
		sql.append("   WHERE                                                                 ");
		sql.append("   ORG.emp_id = :EMP_ID                                                 ");
		sql.append("   AND REC.BRANCH_NAME IS NOT NULL                                       ");
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("yyyymm",yyyymm);
		condition.setObject("EMP_ID",EMP_ID);
		condition.setQueryString(sql.toString());
		return dam.exeQuery(condition);
	}

	


	/** ==== 產出Excel ==== **/
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 取得畫面資料
		PMS228OutputVO return_VO = (PMS228OutputVO) body;

		List<Map<String, Object>> list = return_VO.getTotalList();
		try {
			if (list.size() > 0) {
				// gen csv
				String.format("%1$,09d", -3123);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "消金PS生產力目標維護" + sdf.format(new Date()) + "_"
						+ getUserVariable(FubonSystemVariableConsts.LOGINID)
						+ ".csv";
				List listCSV = new ArrayList();
				for (Map<String, Object> map : list) {
					// 21 column
					String[] records = new String[22];
					int i = 0;
					records[i] = checkIsNull(map, "DATA_YEARMON");                 //資料統計月份
					records[++i] = checkIsNull(map, "REGION_CENTER_ID");             //區域中心ID
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME");           //區域中心名稱
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID");               //營運區ID
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");             //營運區名稱
					records[++i] = checkIsNull(map, "BRANCH_NBR");                   //分行代碼
					records[++i] = checkIsNull(map, "BRANCH_NAME");                  //分行名稱
					records[++i] = checkIsNull(map, "EMP_ID");                       //理專員編
					records[++i] = checkIsNull(map, "AO_CODE");                      //AO Code
					records[++i] = checkIsNull(map, "EMP_NAME");                     //理專姓名
					records[++i] = checkIsNull(map, "JOB_TITLE_ID");                 //職級
					records[++i] = checkIsNull(map, "FIRST_DATE");                   //到職日
					records[++i] = checkIsNull(map, "JOB_TITLE");                    //職稱
					records[++i] = checkIsNull(map, "HB_AMT");                       //房屋-購屋
					records[++i] = checkIsNull(map, "NHB_AMT");                      //房屋-非購屋
					records[++i] = checkIsNull(map, "S_HB_AMT");                     //房屋-分期型
					records[++i] = checkIsNull(map, "C_HB_AMT");                     //房屋-循環型
					records[++i] = checkIsNull(map, "NC_AMT");                       //信貸-一般
					records[++i] = checkIsNull(map, "GC_AMT");                       //信貸-職團
					records[++i] = checkIsNull(map, "CC_AMT");                       //信貸-卡友
					records[++i] = checkIsNull(map, "MAINTAIN_DATE");                //維護日期
					records[++i] = checkIsNull(map, "MAINTAIN_NAME");                //維護人員


					listCSV.add(records);
				}
				// header
				String[] csvHeader = new String[22];
				int j = 0;
				csvHeader[j] = "資料統計月份";
				csvHeader[++j] = "業務處ID";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區ID";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行代碼";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "理專員編";
				csvHeader[++j] = "AO Code";
				csvHeader[++j] = "理專姓名";
				csvHeader[++j] = "職級";
				csvHeader[++j] = "到職日";
				csvHeader[++j] = "職稱";
				csvHeader[++j] = "房屋-購屋(單位:百萬元)";
				csvHeader[++j] = "房屋-非購屋(單位:百萬元)";
				csvHeader[++j] = "房屋-分期型(單位:百萬元)";
				csvHeader[++j] = "房屋-循環型(單位:百萬元)";
				csvHeader[++j] = "信貸-一般(單位:百萬元)";
				csvHeader[++j] = "信貸-職團(單位:百萬元)";
				csvHeader[++j] = "信貸-卡友(單位:百萬元)";
				csvHeader[++j] = "維護日期";
				csvHeader[++j] = "維護人員";


				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader);
				csv.addRecordList(listCSV);
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

	// 處理貨幣/數字格式
	private String currencyFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			DecimalFormat df = new DecimalFormat("#,##0.00");
			return df.format(map.get(key));
		} else
			return "0.00";
	}

	/** 下載範例檔 **/
	public void downloadSample(Object body, IPrimitiveMap header)
			throws Exception {
		PMS228InputVO inputVO = (PMS228InputVO) body;
		notifyClientToDownloadFile("doc//PMS//PMS228_EXAMPLE.csv",
					"上傳範例_消金PS生產力目標維護.csv");
		this.sendRtnObject(null);
	}
}