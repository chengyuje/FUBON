package com.systex.jbranch.app.server.fps.iot370;

import java.sql.Timestamp;
import java.text.DecimalFormat;
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

import com.systex.jbranch.app.server.fps.iot370.IOT370InputVO;
import com.systex.jbranch.app.server.fps.iot370.IOT370OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author Kevin Hsu
 * @date 2016/10/11
 * @spec null
 */
@Component("iot370")
@Scope("request")
public class IOT370 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(IOT370.class);

	/**
	 * 匯出確認NULL
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf(map.get(key));
		}else{
			return "";
		}
	}
	//處理貨幣格式
	private String currencyFormat(Map map, String key){		
		DecimalFormat df = new DecimalFormat("#,###.##");
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return df.format(map.get(key));										
		}else
			return df.format(0);		
	}

	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 取得畫面資料
		IOT370OutputVO return_VO = (IOT370OutputVO) body;

		List<Map<String, Object>> list = return_VO.getList();
		try {
			if (list.size() > 0) {
				// gen csv
				String.format("%1$,09d", -3123);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "產險其他送件報表_" + sdf.format(new Date())
						+ ".csv";
				List listCSV = new ArrayList();
				for (Map<String, Object> map : list) {
					// 21 column
					String[] records = new String[25];
					int i = 0;
					records[i]   = checkIsNull(map, "STATUS"); //狀態
					records[++i] = "=\""+checkIsNull(map, "INS_ID")+"\""; //保險文件編號
					records[++i] = "=\""+checkIsNull(map, "POLICY_NO")+"\""; //保單號碼
					records[++i] = checkIsNull(map, "BRANCH_NBR"); //分行代號
					records[++i] = checkIsNull(map, "KEYIN_DATE"); //鍵機日
					records[++i] = checkIsNull(map, "CUST_ID"); //要保險人ID
					records[++i] = checkIsNull(map, "PROPOSER_NAME"); //要保險人姓名
					records[++i] = checkIsNull(map, "UNOPEN_ACCT"); //未開戶註記
					records[++i] = checkIsNull(map, "RECRUIT_ID"); //批改申請人
					records[++i] = checkIsNull(map, "APPLY_DATE"); //批改申請日
					records[++i] = checkIsNull(map, "PPT_TYPE"); //險種
					records[++i] = checkIsNull(map, "OP_BATCH_NO"); //送件批號
					records[++i] = checkIsNull(map, "CREATOR"); //登錄人
					records[++i] = checkIsNull(map, "CREATETIME"); //登錄時間
					records[++i] = checkIsNull(map, "BEF_SIGN_OPRID"); //行政助理1
					records[++i] = checkIsNull(map, "BEF_SIGN_DATE"); //行政助理1簽收時間
					records[++i] = checkIsNull(map, "SIGN_OPRID"); //簽署人
					records[++i] = checkIsNull(map, "SIGN_DATE"); //簽署紀錄
					records[++i] = checkIsNull(map, "AFT_SIGN_OPRID"); //行政助理2
					records[++i] = checkIsNull(map, "AFT_SIGN_DATE"); //行政助理2簽收時間

					listCSV.add(records);
				}
				// header
				String[] csvHeader = new String[25];
				int j = 0;
				csvHeader[j]   = "狀態"; 
				csvHeader[++j] = "保險文件編號"; 
				csvHeader[++j] = "保單號碼"; 
				csvHeader[++j] = "分行代號"; 
				csvHeader[++j] = "鍵機日"; 
				csvHeader[++j] = "要保險人ID"; 
				csvHeader[++j] = "要保險人姓名"; 
				csvHeader[++j] = "未開戶註記"; 
				csvHeader[++j] = "批改申請人"; 
				csvHeader[++j] = "批改申請日"; 
				csvHeader[++j] = "險種"; 
				csvHeader[++j] = "送件批號"; 
				csvHeader[++j] = "登錄人"; 
				csvHeader[++j] = "登錄時間"; 
				csvHeader[++j] = "行政助理1"; 
				csvHeader[++j] = "行政助理1簽收時間"; 
				csvHeader[++j] = "簽署人"; 
				csvHeader[++j] = "簽署紀錄"; 
				csvHeader[++j] = "行政助理2"; 
				csvHeader[++j] = "行政助理2簽收時間"; 

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

	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException {

		IOT370InputVO inputVO = (IOT370InputVO) body;
		IOT370OutputVO outputVO = new IOT370OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {

			ArrayList<String> sql_list = new ArrayList<String>();
			sql.append("SELECT ROWNUM AS NUM,T.* FROM (select * from VWIOT_PPT_MAIN WHERE 1=1 AND REG_TYPE = '2'   ");

			// where

			// INS_ID
			if (!StringUtils.isBlank(inputVO.getINS_ID())) {
				sql.append(" and INS_ID LIKE :INS_IDD  ");
			}

			// CUST_ID
			if (!StringUtils.isBlank(inputVO.getCUST_ID())) {
				sql.append(" and CUST_ID LIKE :CUST_IDD  ");
			}

			// INSURED_ID
			if (!StringUtils.isBlank(inputVO.getINSURED_ID())) {
				sql.append(" and INSURED_ID LIKE :INSURED_IDD");
			}

			// PPT_TYPE
			if (!StringUtils.isBlank(inputVO.getPPT_TYPE())) {
				sql.append(" and PPT_TYPE LIKE :PPT_TYPEE  ");
			}

			if (!StringUtils.isBlank(inputVO.getBRANCH_NBR())) {
				sql.append(" and BRANCH_NBR LIKE :BRANCH_NBRR  ");
			}

			if (!StringUtils.isBlank(inputVO.getPOLICY_NO())) {
				sql.append(" and POLICY_NO LIKE :POLICY_NOO  ");
			}
			
			if(inputVO.getKEYIN_DATE_FROM() != null || inputVO.getKEYIN_DATE_TO() != null){
				sql.append(" And trunc(KEYIN_DATE) between :keyin_date_f and :keyin_date_t ");
				if(inputVO.getKEYIN_DATE_TO() == null){
					condition.setObject("keyin_date_f", inputVO.getKEYIN_DATE_FROM());
					condition.setObject("keyin_date_t", inputVO.getKEYIN_DATE_FROM());
				}
				if(inputVO.getKEYIN_DATE_FROM() == null){
					condition.setObject("keyin_date_f", inputVO.getKEYIN_DATE_TO());
					condition.setObject("keyin_date_t", inputVO.getKEYIN_DATE_TO());
				}
				if(inputVO.getKEYIN_DATE_FROM() != null && inputVO.getKEYIN_DATE_TO() != null){
					condition.setObject("keyin_date_f", inputVO.getKEYIN_DATE_FROM());
					condition.setObject("keyin_date_t", inputVO.getKEYIN_DATE_TO());
				}
			}

			if(inputVO.getAPPLY_DATE_FROM() != null || inputVO.getAPPLY_DATE_TO() != null){
				sql.append(" And trunc(APPLY_DATE) between :apply_date_f and :apply_date_t ");
				if(inputVO.getAPPLY_DATE_TO() == null){
					condition.setObject("apply_date_f", inputVO.getAPPLY_DATE_FROM());
					condition.setObject("apply_date_t", inputVO.getAPPLY_DATE_FROM());
				}
				if(inputVO.getAPPLY_DATE_FROM() == null){
					condition.setObject("apply_date_f", inputVO.getAPPLY_DATE_TO());
					condition.setObject("apply_date_t", inputVO.getAPPLY_DATE_TO());
				}
				if(inputVO.getAPPLY_DATE_FROM() != null && inputVO.getAPPLY_DATE_TO() != null){
					condition.setObject("apply_date_f", inputVO.getAPPLY_DATE_FROM());
					condition.setObject("apply_date_t", inputVO.getAPPLY_DATE_TO());
				}
			}
			
			sql.append("   ORDER BY POLICY_NO,BRANCH_NBR ) T");

			condition.setQueryString(sql.toString());

			if (!StringUtils.isBlank(inputVO.getBRANCH_NBR())) {
				condition.setObject("BRANCH_NBRR",
						"%" + inputVO.getBRANCH_NBR() + "%");
			}

			if (!StringUtils.isBlank(inputVO.getPOLICY_NO())) {
				condition.setObject("POLICY_NOO","%" +inputVO.getPOLICY_NO().toString()+"%");
			}


			// CUST_ID
			if (!StringUtils.isBlank(inputVO.getCUST_ID())) {
				condition.setObject("CUST_IDD", "%"
						+ inputVO.getCUST_ID().toString() + "%");
			}
			// INSURED_ID
			if (!StringUtils.isBlank(inputVO.getINSURED_ID())) {
				condition.setObject("INSURED_IDD", "%"
						+ inputVO.getINSURED_ID().toString() + "%");
			}
			if (!StringUtils.isBlank(inputVO.getINS_ID())) {
				condition.setObject("INS_IDD", "%"
						+ inputVO.getINS_ID().toString() + "%");

			}
			// PPT_TYPE
			if (!StringUtils.isBlank(inputVO.getPPT_TYPE())) {
				condition.setObject("PPT_TYPEE", "%"
						+ inputVO.getPPT_TYPE().toString() + "%");
			}
			
			List<Map<String, Object>> list1 = dam.exeQuery(condition);
		
			outputVO.setResultList(list1); // data
			outputVO.setCsvList(list1);
			
			this.sendRtnObject(outputVO);

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

}