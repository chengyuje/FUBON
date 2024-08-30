package com.systex.jbranch.app.server.fps.ins120;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;


/**
 * ins120
 * 
 * @author James
 * @date 2017/09/11
 * @spec null
 */
@Component("ins120")
@Scope("request")
public class INS120 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	
	public void getcustName (Object body, IPrimitiveMap header) throws JBranchException {
		INS120InputVO inputVO = (INS120InputVO) body;
		INS120OutputVO outputVO = new INS120OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuilder sb = null;
		try {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append(" SELECT CUST_NAME ");
			sb.append(" FROM TBCRM_CUST_MAST ");
			sb.append(" WHERE CUST_ID = :custId ");
			queryCondition.setObject("custId", inputVO.getCustId());
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> resultList = dam.exeQuery(queryCondition);			
			
			if(resultList == null || resultList.size() == 0) {
				sb = new StringBuilder();
				sb.append(" SELECT CUST_NAME ");
				sb.append(" FROM TBINS_CUST_MAST ");
				sb.append(" WHERE CUST_ID = :custId ");
				queryCondition.setObject("custId", inputVO.getCustId());
				queryCondition.setQueryString(sb.toString());
				resultList = dam.exeQuery(queryCondition);
			}
			outputVO.setResultList(resultList);
			
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}	
	}
	
	public void queryData (Object body, IPrimitiveMap header) throws JBranchException {

		INS120InputVO inputVO = (INS120InputVO) body;
		INS120OutputVO outputVO = new INS120OutputVO();
//		String branchId = (String) getUserVariable(FubonSystemVariableConsts.LOGINBRH);
		dam = this.getDataAccessManager();
		try {			
			List<Map<String, Object>> resultList = queryMain(dam, inputVO);			
			outputVO.setResultList(resultList);

			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
	}
	
	public List<Map<String, Object>> queryMain(DataAccessManager dam , INS120InputVO inputVO) throws DAOException, JBranchException{
		QueryConditionIF queryCondition = null;
		StringBuilder sb = null;
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		sb.append(" SELECT HIS.AGREE_KEYNO, HIS.CUST_ID, MAST.CUST_NAME, INSMAST.CUST_NAME as INS_CUS_NAME, REPORT.CREATETIME, VWE.EMP_ID ||'-'|| VWE.EMP_NAME AS CREATOR ");
		sb.append(" FROM TBINS_EXAM_AGREE_HIS HIS ");
		sb.append(" LEFT JOIN TBCRM_CUST_MAST MAST ON MAST.CUST_ID = HIS.CUST_ID  ");
		sb.append(" LEFT JOIN TBINS_REPORT REPORT ON REPORT.PLAN_ID = HIS.AGREE_KEYNO ");
		sb.append(" LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO VWE ON VWE.EMP_ID = HIS.CREATOR ");
		sb.append(" LEFT JOIN TBINS_CUST_MAST INSMAST ON INSMAST.CUST_ID = HIS.CUST_ID  ");
//		sb.append(" WHERE HIS.AO_CODE in (:aoCode) ");
//		queryCondition.setObject("aoCode", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
		sb.append(" WHERE 1=1 ");
		
		if (StringUtils.isNotBlank(inputVO.getAoCode())) {
			sb.append(" AND HIS.AO_CODE = :aoCode ");
			queryCondition.setObject("aoCode", inputVO.getAoCode());
			logger.info("aoCode:" + inputVO.getAoCode());
			
			sb.append(" AND VWE.BRANCH_NBR = :branchId ");
			queryCondition.setObject("branchId", inputVO.getBranchId());
			logger.info("branchId:" + inputVO.getBranchId());
			
		} else {
			ArrayList<String> braList = (ArrayList<String>) getUserVariable(SystemVariableConsts.AVAILBRANCHLIST);
			ArrayList<String> aoList = (ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
			// 是理專登入 [只能看到自己而已]
			if(CollectionUtils.isNotEmpty(braList) && braList.size() == 1 && CollectionUtils.isNotEmpty(aoList)) {
				sb.append(" AND HIS.AO_CODE in (:aoCodeList) ");
				queryCondition.setObject("aoCodeList", aoList);
				logger.info("aoCodeList:" + ObjectUtils.toString(aoList));
			} 
			
			// 不是理專登入[有選擇分行看有選擇的全部AO，沒選擇看全部]
			else {
				if(StringUtils.isNotBlank(inputVO.getBranchId())) {
					sb.append(" AND VWE.BRANCH_NBR = :branchId ");
					queryCondition.setObject("branchId", inputVO.getBranchId());
					logger.info("branchId:" + inputVO.getBranchId());
				} else {
					sb.append(" AND VWE.BRANCH_NBR in (:branchIdList) ");
					queryCondition.setObject("branchIdList", braList);
					logger.info("branchIdList:" + ObjectUtils.toString(braList));
				}
			}
		}
		
		if (StringUtils.isNotBlank(inputVO.getCustId())) {
			sb.append(" AND HIS.CUST_ID = :custId ");
			queryCondition.setObject("custId", inputVO.getCustId());
			logger.info("custId:" + inputVO.getCustId());
		}
		if (null !=inputVO.getAgreeSdate() && null !=inputVO.getAgreeEdate()) {
			sb.append(" AND TRUNC(REPORT.CREATETIME) BETWEEN TRUNC(:agreeSdate) AND TRUNC(:agreeEdate) ");
			queryCondition.setObject("agreeSdate", inputVO.getAgreeSdate());
			queryCondition.setObject("agreeEdate", inputVO.getAgreeEdate());
			logger.info("agreeSdate:" + inputVO.getAgreeSdate() + ", agreeEdate:" + inputVO.getAgreeEdate());
		}
		if (null !=inputVO.getAgreeSdate() && null ==inputVO.getAgreeEdate()) {
			sb.append(" AND TRUNC(REPORT.CREATETIME) >= TRUNC(:agreeSdate) ");
			queryCondition.setObject("agreeSdate", inputVO.getAgreeSdate());
			logger.info("agreeSdate:" + inputVO.getAgreeSdate());
		}
		if (null ==inputVO.getAgreeSdate() && null !=inputVO.getAgreeEdate()) {
			sb.append(" AND TRUNC(REPORT.CREATETIME) <= TRUNC(:agreeEdate) ");
			queryCondition.setObject("agreeEdate", inputVO.getAgreeEdate());
			logger.info("agreeEdate:" + inputVO.getAgreeEdate());
		}
		sb.append(" ORDER BY REPORT.CREATETIME DESC ");
		queryCondition.setQueryString(sb.toString());
		
		logger.info("INS120-SQL：" + sb.toString());
		
		List<Map<String, Object>> resultList = dam.exeQuery(queryCondition);			
//		outputVO.setResultList(resultList);
		return resultList;
		
	
	}
	
	public void print (Object body, IPrimitiveMap header) throws JBranchException {
		
		INS120InputVO inputVO = (INS120InputVO) body;
		INS120OutputVO outputVO = new INS120OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuilder sb = null;
		try {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append(" SELECT HIS.CUST_ID,REPORT.REPORT_FILE ");
			sb.append(" FROM TBINS_EXAM_AGREE_HIS HIS ");
			sb.append(" LEFT JOIN TBINS_REPORT REPORT ON REPORT.PLAN_ID = HIS.AGREE_KEYNO ");
			sb.append(" WHERE HIS.AGREE_KEYNO = :keyNo ");
			queryCondition.setObject("keyNo", inputVO.getAGREE_KEYNO());
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if(CollectionUtils.isNotEmpty(list)){
				String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				String uuid = UUID.randomUUID().toString();
				String fileName =String.format("保單健診同意書_%s.pdf", uuid);
				Blob blob = (Blob) list.get(0).get("REPORT_FILE");
				int blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);

				File targetFile = new File(filePath, uuid);
				FileOutputStream fos = new FileOutputStream(targetFile);
				fos.write(blobAsBytes);
				fos.close();					
				notifyClientToDownloadFile("temp//" + uuid, fileName);
			}
						
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
	}

	public void exportCSV (Object body, IPrimitiveMap<Object> header) throws JBranchException{
		SimpleDateFormat sdf = null;
		INS120InputVO inputVO = (INS120InputVO) body;
		INS120OutputVO outputVO = new INS120OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuilder sb = null;
		
		List<Map<String, Object>> resultList = queryMain(dam, inputVO);	
		
		if (resultList.size() > 0) {
			String fileName = new SimpleDateFormat("YYYYMMdd").format(new Date()) + "保單健診同意書歷史資料.csv";
			List listCSV = new ArrayList();

			String[] csvHeader = {"被保人ID", "被保人姓名", "上傳日期", "建立人"};
			String[] csvContent = {"CUST_ID", "CUST_NAME", "CREATETIME", "CREATOR"};

			for (Map<String, Object> map : resultList) {
				String[] records = new String[csvContent.length];
				for (int i = 0; i < csvContent.length; i++) {
					records[i] =  checkIsNull(map, csvContent[i]);
				}
				listCSV.add(records);
			}

			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader); // 設定標頭
			csv.addRecordList(listCSV); // 設定內容
			String url = csv.generateCSV();
			// download
			notifyClientToDownloadFile(url, fileName);
		}
	}
	
	private String checkIsNull(Map<String, Object> map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			String str =  "";
			if (StringUtils.equals("null", String.valueOf(map.get(key)))) {
				str = "";
			} else {
				str = String.valueOf(map.get(key));
			}
			return str;
		} else {
			return "";
		}
	}
		
}