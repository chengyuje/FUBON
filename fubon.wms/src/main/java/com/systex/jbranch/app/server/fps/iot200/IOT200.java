package com.systex.jbranch.app.server.fps.iot200;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBIOT_NOTIFY_SENDVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author Brian
 * @date 2018/05/16
 * @spec null
 */
@Component("iot200")
@Scope("request")
public class IOT200 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;

	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {
		
		initUUID();

		IOT200InputVO inputVO = (IOT200InputVO) body;
		IOT200OutputVO outputVO = new IOT200OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT IOT.*, (CASE WHEN M.STATUS IS NULL THEN IOT.STATUS ELSE M.STATUS END) AS FINAL_STATUS ");
		sb.append("FROM TBIOT_NOTIFY_SEND IOT ");
		sb.append("LEFT OUTER JOIN TBIOT_MAIN M ON M.INS_ID = IOT.INS_ID ");
		
		if (StringUtils.isNotBlank(inputVO.getAreaID())) {
			sb.append("LEFT OUTER JOIN VWORG_DEFN_INFO ORG ON ORG.BRANCH_NBR = IOT.BRANCH_NBR ");
		}
				
		sb.append("WHERE 1 = 1 ");
		
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0 &&
			!StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "uhrm")) {
			sb.append("AND EXISTS ( ");
			sb.append("  SELECT 1 ");
			sb.append("  FROM TBCRM_CUST_MAST CM ");
			sb.append("  INNER JOIN VWORG_EMP_UHRM_INFO U ON CM.AO_CODE = U.UHRM_CODE ");
			sb.append("  WHERE IOT.CUST_ID = CM.CUST_ID");
			sb.append(") ");
		}
		
		//區別(營運區)
		if (StringUtils.isNotBlank(inputVO.getAreaID())) {
			sb.append(" AND ORG.BRANCH_AREA_ID = :BRANCH_AREA_ID ");
			queryCondition.setObject("BRANCH_AREA_ID", inputVO.getAreaID());
		}
		
		//分行
		if (StringUtils.isNotBlank(inputVO.getBranchID())) {
			sb.append(" AND IOT.BRANCH_NBR = :BRANCH_NBR ");
			queryCondition.setObject("BRANCH_NBR", inputVO.getBranchID());
		}
		
		//申請日起日至迄日
		if (inputVO.getsApplyDate() != null && inputVO.geteApplyDate() != null) {
			sb.append(" AND TRUNC(IOT.APPLY_DATE) ");
			sb.append(" BETWEEN ");
			sb.append(" TRUNC(TO_DATE(:sApplyDate, 'YYYY-MM-DD')) ");
			sb.append(" AND ");
			sb.append(" TRUNC(TO_DATE(:eApplyDate, 'YYYY-MM-DD')) ");
			queryCondition.setObject("sApplyDate", new java.text.SimpleDateFormat("yyyy/MM/dd").format(inputVO.getsApplyDate()));
			queryCondition.setObject("eApplyDate", new java.text.SimpleDateFormat("yyyy/MM/dd").format(inputVO.geteApplyDate()));
		}
		
		//要保人ID
		if (StringUtils.isNotBlank(inputVO.getCustID())) {
			sb.append(" AND IOT.CUST_ID = :CUST_ID ");
			queryCondition.setObject("CUST_ID", inputVO.getCustID());
		}
		
		//案件編號
		if (StringUtils.isNotBlank(inputVO.getCaseID())) {
			sb.append(" AND IOT.CASE_ID = :CASE_ID ");
			queryCondition.setObject("CASE_ID", inputVO.getCaseID());
		}
		
		//被保人ID
		if (StringUtils.isNotBlank(inputVO.getInsuredID())) {
			sb.append(" AND IOT.INSURED_ID = :INSURED_ID ");
			queryCondition.setObject("INSURED_ID", inputVO.getInsuredID());
		}
		
		//案件狀態
		if (StringUtils.isNotBlank(inputVO.getStatus())) {
			sb.append(" AND (CASE WHEN M.STATUS IS NULL THEN IOT.STATUS ELSE M.STATUS END) = :STATUS ");
			queryCondition.setObject("STATUS", inputVO.getStatus());
		}
		
		//險種代號
		if (StringUtils.isNotBlank(inputVO.getInsPrdID())) {
			sb.append(" AND IOT.INSPRD_ID = :INSPRD_ID ");
			queryCondition.setObject("INSPRD_ID", inputVO.getInsPrdID());
		}
		
		sb.append(" ORDER BY IOT.APPLY_DATE ");
		
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		resultList = dam.exeQuery(queryCondition);

		//檢查是否與TBIOT_MAIN有相同案件編號
		List<Map<String, Object>> idList = dam.exeQuery(genDefaultQueryConditionIF().setQueryString(new StringBuffer().append(" SELECT M.CASE_ID FROM TBIOT_MAIN M ")
																													  .append(" LEFT JOIN TBIOT_NOTIFY_SEND S ON M.CASE_ID = S.CASE_ID ")
																													  .append(" WHERE M.CASE_ID = S.CASE_ID ").toString()));

		for (Map<String, Object> resultMap : resultList) {
			resultMap.put("CUST_ID", hiddenCode(ObjectUtils.toString(resultMap.get("CUST_ID"))));
			resultMap.put("PROPOSER_NAME", hiddenName(ObjectUtils.toString(resultMap.get("PROPOSER_NAME"))));
			resultMap.put("INSURED_ID", hiddenCode(ObjectUtils.toString(resultMap.get("INSURED_ID"))));
			resultMap.put("INSURED_NAME", hiddenName(ObjectUtils.toString(resultMap.get("INSURED_NAME"))));
			for (Map<String, Object> idMap : idList) {
				if (ObjectUtils.toString(resultMap.get("CASE_ID")).equals(ObjectUtils.toString(idMap.get("CASE_ID")))) {
					resultMap.put("STATUS", 4);
				}
			}
		}

		outputVO.setResultList(resultList);

		sendRtnObject(outputVO);
	}

	//上傳CSV檔案
	public void uploadFile(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		IOT200InputVO inputVO = (IOT200InputVO) body;
		IOT200OutputVO outputVO = new IOT200OutputVO();
		dam = this.getDataAccessManager();
		
		//顯示上傳成功的訊息
		String successMsg;

		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());

		if (!dataCsv.isEmpty()) {
			for (int i = 0; i < dataCsv.size(); i++) {
				if (i == 0) {
					continue;
				}

				String[] str = dataCsv.get(i);

				TBIOT_NOTIFY_SENDVO vo = null;
				vo = (TBIOT_NOTIFY_SENDVO) dam.findByPKey(TBIOT_NOTIFY_SENDVO.TABLE_UID, str[3]);
				if (null == vo) {
					vo = new TBIOT_NOTIFY_SENDVO();
					saveOrUpdate(vo, str);
					dam.create(vo);
				} else {
					saveOrUpdate(vo, str);
					dam.update(vo);
				}
			}

			successMsg = "資料匯入成功，筆數" + (dataCsv.size() - 1);
			outputVO.setSuccessMsg(successMsg);

		}
		sendRtnObject(outputVO);
	}

	//產生CSV範例檔
	public void getExample(Object body, IPrimitiveMap header) throws Exception {

		CSVUtil csv = new CSVUtil();
		//表頭組成的字串陣列
		String[] headColumnArray = new String[] { "案件來源", "建檔日期", "申請日期", "案件編號", "保險文件編號", "案件狀態", "要保人ID", "要保人姓名", "被保人ID", "被保人姓名", "幣別", "主約險種代號", "繳別", "保費", "業務員姓名", "分行代號", "傳送完成日期時間" };
		// 設定表頭
		csv.setHeader(headColumnArray);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, "要保書通報送件_範例.csv");
		sendRtnObject(null);
	}

	//新增或更新電子要保書通報送件資料
	public TBIOT_NOTIFY_SENDVO saveOrUpdate(TBIOT_NOTIFY_SENDVO vo, String[] str) throws JBranchException, ParseException {

		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat sdfDate2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");

		vo.setCASE_SOU(ObjectUtils.toString(str[0]).trim());
		vo.setKEYIN_DATE(new Timestamp(sdfDate.parse(ObjectUtils.toString(str[1]).trim()).getTime()));
		vo.setAPPLY_DATE(new Timestamp(sdfDate.parse(ObjectUtils.toString(str[2]).trim()).getTime()));
		vo.setCASE_ID(ObjectUtils.toString(str[3]).trim());
		vo.setINS_ID(ObjectUtils.toString(str[4]).trim());
		vo.setSTATUS(new BigDecimal(ObjectUtils.toString(str[5]).trim()));
		vo.setCUST_ID(ObjectUtils.toString(str[6]).trim());
		vo.setPROPOSER_NAME(ObjectUtils.toString(str[7]).trim());
		vo.setINSURED_ID(ObjectUtils.toString(str[8]).trim());
		vo.setINSURED_NAME(ObjectUtils.toString(str[9]).trim());
		vo.setCURR_CD(ObjectUtils.toString(str[10]).trim());
		vo.setINSPRD_ID(ObjectUtils.toString(str[11]).trim());
		vo.setPAY_TYPE(ObjectUtils.toString(str[12]).trim());
		vo.setREAL_PREMIUM(new BigDecimal(ObjectUtils.toString(str[13]).trim()));
		vo.setSALES_NAME(ObjectUtils.toString(str[14]).trim());
		vo.setBRANCH_NBR(ObjectUtils.toString(str[15]).trim());
		vo.setSEND_DATETIME(new Timestamp(sdfDate2.parse(ObjectUtils.toString(str[16]).trim()).getTime()));
		return vo;
	}

	/**
	 * 客戶ID隱碼用
	 * 
	 * @param custId
	 * @return
	 * @throws JBranchException
	 */
	public String hiddenCode(String custId) throws JBranchException {

		if (StringUtils.isNotBlank(custId)) {
			String cutIdFront = custId.length() > 4 ? custId.substring(0, 4) : "";
			String custIdBack = custId.length() > 8 ? custId.substring(8, custId.length()) : "";
			custId = cutIdFront + "****" + custIdBack;
		}
		return custId;
	}

	/**
	 * 客戶姓名隱碼用
	 * 
	 * @param custName
	 * @return
	 * @throws JBranchException
	 */
	public String hiddenName(String custName) throws JBranchException {
		
		String firstName = custName.substring(0, 1);
		String lastName = "";

		for (int i = 0; i < custName.length() - 1; i++) {
			lastName = lastName + "*";
		}

		custName = firstName + lastName;
		return custName;
	}
}