package com.systex.jbranch.app.server.fps.iot360;

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

import com.systex.jbranch.app.server.fps.iot360.IOT360InputVO;
import com.systex.jbranch.app.server.fps.iot360.IOT360OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.security.privilege.vo.ItemDTO;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author Kevin Hsu
 * @date 2016/06/29
 * @spec null
 */
@Component("iot360")
@Scope("request")
public class IOT360 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(IOT360.class);

	/**
	 * 匯出確認NULL
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	//險種
	private String checkIsNull(Map map, String key) throws JBranchException {
		XmlInfo xmlInfo = new XmlInfo();
		if (map.get(key)!=null) {
			if("STATUS".equals(key)){
				String status = (String) xmlInfo.getVariable("IOT.MAIN_STATUS", String.valueOf(map.get(key)).split("\\.")[0], "F3");
				return status;
			}else if("PPT_TYPE".equals(key)){
				return (String) xmlInfo.getVariable("IOT.PPT_TYPE", String.valueOf(map.get(key)), "F3");
			}else if("outList".equals(key)){
				List<Map<String, Object>> outList = (List<Map<String, Object>>) map.get(key);
				String out = new String();
				for(int i=0;i<outList.size();i++){
					if(i<outList.size()-1){
						out += (outList.get(i).get("DOC_NAME").toString())+"、";
					}else{
						out += (outList.get(i).get("DOC_NAME").toString());
					}
				}
				return out.toString();
			}else{
				return String.valueOf(map.get(key));
			}
		} else {
			return "";
		}
	}

	//==匯出功能==//
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 取得畫面資料
		IOT360OutputVO return_VO = (IOT360OutputVO) body;

		List<Map<String, Object>> list = return_VO.getList();
		try {
			if (list.size() > 0) {
				// gen csv
				String.format("%1$,09d", -3123);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "產險新契約送件_" + sdf.format(new Date())
						+ ".csv";
				List listCSV = new ArrayList();
				for (Map<String, Object> map : list) {
					// 21 column
					String[] records = new String[25];
					int i = 0;
					records[i]   = checkIsNull(map, "STATUS");         // 狀態
					records[++i] = "=\""+checkIsNull(map, "INS_ID")+"\"";         // 保險文件編號
					records[++i] = checkIsNull(map, "BRANCH_NBR");     // 分行代號
					records[++i] = checkIsNull(map, "KEYIN_DATE");     // 鍵機日
					records[++i] = checkIsNull(map, "CUST_ID");        // 要保險人ID
					records[++i] = checkIsNull(map, "PROPOSER_NAME");  // 要保險人姓名
					records[++i] = checkIsNull(map, "INSURED_ID");     // 被保險人ID
					records[++i] = checkIsNull(map, "INSURED_NAME");   // 被保險人姓名
					records[++i] = checkIsNull(map, "UNOPEN_ACCT");    // 未開戶註記
					records[++i] = checkIsNull(map, "APPLY_DATE");     // 文件申請日期
					records[++i] = checkIsNull(map, "RECRUIT_ID");     // 招攬人員編
					records[++i] = checkIsNull(map, "PPT_TYPE");       // 險種
					records[++i] = checkIsNull(map, "outList");           // 保險相關文件
					records[++i] = checkIsNull(map, "REAL_PREMIUM");   // 總保費
					records[++i] = checkIsNull(map, "OP_BATCH_NO");    // 送件批號
					records[++i] = checkIsNull(map, "CREATOR");        // 登錄人
					records[++i] = checkIsNull(map, "CREATETIME");     // 登錄時間
					records[++i] = checkIsNull(map, "BEF_SIGN_OPRID"); // 行政助理1
					records[++i] = checkIsNull(map, "BEF_SIGN_DATE");  // 行政助理1簽收時間
					records[++i] = checkIsNull(map, "SIGN_OPRID");     // 簽署人
					records[++i] = checkIsNull(map, "SIGN_DATE");      // 簽署紀錄
					records[++i] = checkIsNull(map, "AFT_SIGN_OPRID"); // 行政助理2
					records[++i] = checkIsNull(map, "AFT_SIGN_DATE");  // 行政助理2簽收時間

					listCSV.add(records);
				}
				// header
				String[] csvHeader = new String[25];
				int j = 0;
				csvHeader[j] = "狀態";
				csvHeader[++j] = "保險文件編號";
				csvHeader[++j] = "分行代號";
				csvHeader[++j] = "鍵機日";
				csvHeader[++j] = "要保險人ID";
				csvHeader[++j] = "要保險人姓名";
				csvHeader[++j] = "被保險人ID";
				csvHeader[++j] = "被保險人姓名";
				csvHeader[++j] = "未開戶註記";
				csvHeader[++j] = "文件申請日期";
				csvHeader[++j] = "招攬人員編";
				csvHeader[++j] = "險種";
				csvHeader[++j] = "保險相關文件";
				csvHeader[++j] = "總保費";
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
    //==查詢功能==
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException {

		IOT360InputVO inputVO = (IOT360InputVO) body;
		IOT360OutputVO outputVO = new IOT360OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {

			sql.append("SELECT ROWNUM AS NUM,T.* FROM (select * from VWIOT_PPT_MAIN WHERE 1=1 AND REG_TYPE = '1'   ");

			// where  條件
			//INS_ID
			if (!StringUtils.isBlank(inputVO.getINS_ID())) {
				sql.append(" and INS_ID LIKE :INS_IDD  ");
			}
			//CUST_ID  客戶
			if (!StringUtils.isBlank(inputVO.getCUST_ID())) {
				sql.append(" and CUST_ID LIKE :CUST_IDD  ");
			}
			//INSURED_ID
			if (!StringUtils.isBlank(inputVO.getINSURED_ID())) {
				sql.append(" and INSURED_ID LIKE :INSURED_IDD");
			}
			//PPT_TYPE  險種
			if (!StringUtils.isBlank(inputVO.getPPT_TYPE())) {
				sql.append(" and PPT_TYPE LIKE :PPT_TYPEE  ");
			}
			//分行
			if (!StringUtils.isBlank(inputVO.getBRANCH_NBR())) {
				sql.append(" and BRANCH_NBR LIKE :BRANCH_NBRR  ");
			}
			//鍵機日 起
			if (inputVO.getKEYIN_DATE_FROM() != null) {
				sql.append("   and trunc(KEYIN_DATE) >= :KEYIN_DATE_FROMM ");
			}
			//鍵機日  末
			if (inputVO.getKEYIN_DATE_TO() != null) {
				sql.append("   and trunc(KEYIN_DATE) <= :KEYIN_DATE_TOO ");
			}
			//文件申請日
			if (inputVO.getAPPLY_DATE() != null) {
				sql.append("   and trunc(APPLY_DATE) = :APPLY_DATEE ");                                                      
			}

			sql.append("   ORDER BY POLICY_NO,BRANCH_NBR ) T");

			condition.setQueryString(sql.toString());

			if (!StringUtils.isBlank(inputVO.getBRANCH_NBR())) {
				condition.setObject("BRANCH_NBRR",
						"%" + inputVO.getBRANCH_NBR() + "%");
			}
			//文件申請日
			if (inputVO.getAPPLY_DATE() != null) {
				condition.setObject("APPLY_DATEE", new Timestamp(inputVO
						.getAPPLY_DATE().getTime()));
			}
			//鍵機日 起
			if (inputVO.getKEYIN_DATE_FROM() != null) {
				condition.setObject("KEYIN_DATE_FROMM", new Timestamp(inputVO
						.getKEYIN_DATE_FROM().getTime()));
			}
			//鍵機日 末
			if (inputVO.getKEYIN_DATE_TO() != null) {
				condition.setObject("KEYIN_DATE_TOO", new Timestamp(inputVO
						.getKEYIN_DATE_TO().getTime()));
			}	
			//CUST_ID  客戶
			if (!StringUtils.isBlank(inputVO.getCUST_ID())) {
				condition.setObject("CUST_IDD", "%"+inputVO.getCUST_ID().toString().toUpperCase()+"%");
			}
			//INSURED_ID 保險代碼
			if (!StringUtils.isBlank(inputVO.getINSURED_ID())) {
				condition.setObject("INSURED_IDD", "%"+inputVO.getINSURED_ID().toString().toUpperCase()+"%");
			}
			if (!StringUtils.isBlank(inputVO.getINS_ID())) {
				condition.setObject("INS_IDD", "%"+inputVO.getINS_ID().toString()+"%");
			}
			//PPT_TYPE  險種
			if (!StringUtils.isBlank(inputVO.getPPT_TYPE())) {		
				condition.setObject("PPT_TYPEE", "%"+inputVO.getPPT_TYPE().toString()+"%");
			}
			
			List<Map<String, Object>> list1 = dam.exeQuery(condition);
			
			outputVO.setResultList(list1); // data
			
			if(list1.size()>0){
				for(Map<String, Object> map:list1){
					List<Map<String, Object>> outList = new ArrayList<Map<String,Object>>();
					condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sql = new StringBuffer();
					sql.append(" Select decode(a.DOC_SEQ,a.DOC_SEQ,"
							+ "(select b.PARAM_NAME from TBSYSPARAMETER b WHERE b.PARAM_TYPE = 'IOT.PPT_DOC_SEQ' and b.PARAM_CODE = a.DOC_SEQ)) as DOC_NAME From TBIOT_PPT_DOCCHK a Where a.ins_keyno = :INS_KEYNO Order by doc_seq ");
					condition.setObject("INS_KEYNO", map.get("INS_KEYNO").toString());
					condition.setQueryString(sql.toString());
					outList = dam.exeQuery(condition);
					if(outList.size()>0){
						map.put("outList", outList);
					}
					
				}
			}
	
			outputVO.setCsvList(list1);//匯出csv
			
			this.sendRtnObject(outputVO);

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

}