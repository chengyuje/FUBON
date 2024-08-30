package com.systex.jbranch.app.server.fps.ins410;



import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lowagie.text.DocumentException;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.PdfUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Carley
 * @date 2017/08/15
 * 
 */
@Component("ins410")
@Scope("request")
public class INS410 extends FubonWmsBizLogic {
	
	/*
	 * 查詢
	 * 
	 * 2017-11-08 add by Carley
	 * 
	 */

	public INS410OutputVO queryHisPlanMain (Object body, IPrimitiveMap header) throws JBranchException, Exception {
		INS410InputVO inputVO = (INS410InputVO) body;
		INS410OutputVO outputVO = new INS410OutputVO();
		DataAccessManager dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		if(StringUtils.isNotBlank(inputVO.getCust_id())){
			sql.append("SELECT SPP.SPP_ID, SPP.STATUS, SPP.SPP_TYPE, SPP.SPP_NAME, INFO.BRANCH_NAME, INFO.EMP_NAME, ");
			sql.append("SPP.LASTUPDATE, SPP.CREATETIME ");
			sql.append("FROM TBINS_SPP_MAIN SPP ");
			sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON SPP.CREATOR = INFO.EMP_ID ");
			sql.append("WHERE 1 = 1 ");
			
			if (StringUtils.isNotBlank(inputVO.getPlan_status())){
				sql.append("AND SPP.STATUS = :plan_status ");
				queryCondition.setObject("plan_status", inputVO.getPlan_status());
			}
			
			if (inputVO.getPlan_sDate() != null){
				sql.append("AND TO_CHAR(CREATETIME, 'yyyyMMdd') >= TO_CHAR( :plan_sDate, 'yyyyMMdd') ");
				queryCondition.setObject("plan_sDate", inputVO.getPlan_sDate());
			}
			
			if (inputVO.getPlan_eDate() != null){
				sql.append("AND TO_CHAR(CREATETIME, 'yyyyMMdd') <= TO_CHAR( :plan_eDate, 'yyyyMMdd') ");
				queryCondition.setObject("plan_eDate", inputVO.getPlan_eDate());
			}
			
			sql.append("AND SPP.CUST_ID = :cust_id ");
			sql.append("ORDER BY SPP.CREATETIME DESC ");

			queryCondition.setObject("cust_id", inputVO.getCust_id());
			
			queryCondition.setQueryString(sql.toString());
			List<Map<String,Object>> list = dam.exeQuery(queryCondition);
			
			outputVO.setResultList(list);
			
		}else{
			throw new APException("ehl_01_KYC310_004");  //請輸入客戶ID
		}

		return outputVO;
	}
	
    /**
     * 下載
     * @param body
     * @param header
     * @throws JBranchException
     * @throws ParseException
     * @throws DocumentException 
     */
    public void openFile(Object body, IPrimitiveMap header) throws JBranchException, ParseException, DocumentException {
    	INS410InputVO inputVO = (INS410InputVO) body;
		INS410OutputVO outputVO = new INS410OutputVO();
		List<String> url_list =  new ArrayList<String>();
		DataAccessManager dam = this.getDataAccessManager();
		String sppType = inputVO.getSppType();
		String sppName = inputVO.getSppName();

		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM ( ");
		sb.append("SELECT KEYNO,FILE_NAME,REPORT_FILE ");
		sb.append("FROM TBINS_REPORT ");
		sb.append("WHERE plan_id = :planId ");
		sb.append("ORDER BY LASTUPDATE DESC ) ");
		sb.append("WHERE ROWNUM = 1 ");
									
        qc.setObject("planId", inputVO.getSppID());
        qc.setQueryString(sb.toString());
        List<Map<String, Object>> result = dam.exeQuery(qc);
        
        if (!result.isEmpty()) {
            String filename = (String) result.get(0).get("FILE_NAME");
            String seqNo = String.valueOf((BigDecimal) result.get(0).get("KEYNO"));
            byte[] blobarray;
            try {
                blobarray = ObjectUtil.blobToByteArr((Blob) result.get(0).get("REPORT_FILE"));
            } catch (Exception e) {
                throw new APException("文件下載錯誤！"); // 文件下載錯誤！
            }

            String temp_path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
            String temp_path_relative = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH_RELATIVE);

            FileOutputStream download_file = null;
            try {
                download_file = new FileOutputStream(new File(temp_path, filename));
                download_file.write(blobarray);
                inputVO.setKEYNO(seqNo);
            } catch (Exception e) {
                throw new APException("文件下載錯誤！"); // 下載檔案失敗
            } finally {
                try {
                    download_file.close();
                } catch (Exception e) {
                    //IGNORE
                }
            }
            url_list.add("doc//INS//INS400_COVER.pdf");
			url_list.add(temp_path_relative + "//" + filename);
			String reportURL = PdfUtil.mergePDF(url_list, inputVO.getCust_id());
			this.notifyClientToDownloadFile(reportURL, sppType + "_" + sppName + "_"  + "保險規劃書.pdf");
//            notifyClientToDownloadFile(temp_path_relative + "//" + filename, sppType + "_" + sppName + "_"  + "保險規劃書.pdf");
        } else {
            throw new APException("無可下載的檔案!"); // 無可下載的檔案
        }
    }
	
	/**
	 * 保險特定規劃-查詢保險規劃歷史規劃
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryHisPlan (Object body, IPrimitiveMap header) throws JBranchException, Exception {
		sendRtnObject(queryHisPlanMain(body, header));
	}
	
	/**
	 * 保險特定規劃2階API-查詢保險規劃歷史規劃-特定目的
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryHistoryInsurancePurposePlan(Object body,IPrimitiveMap header) throws JBranchException, Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Map inputMap = (Map)body;
		INS410InputVO inputVO = new INS410InputVO();
		String startDate = ObjectUtils.toString(inputMap.get("planSDate"));
		String endDate = ObjectUtils.toString(inputMap.get("planEDate"));
		Date planSDate = startDate.isEmpty() ? null : sdf.parse(startDate) ;
		Date planEDate = endDate.isEmpty() ? null : sdf.parse(endDate) ;
		String status = inputMap.get("status") != null ? ObjectUtils.toString(((Double) inputMap.get("status")).intValue()) : null;
		if("0".equals(status)){
			status = null;
		}
		
		inputVO.setCust_id(ObjectUtils.toString(inputMap.get("custId")));
		inputVO.setPlan_sDate(planSDate);
		inputVO.setPlan_eDate(planEDate);
		inputVO.setPlan_status(status);
		INS410OutputVO  outVo = queryHisPlanMain(inputVO, header);
		Map<String,Object> finalMap = new  HashMap<String, Object>();
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>() ;
		for(Map<String,Object> map : (List<Map<String,Object>>) outVo.getResultList() ){
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("sppId", map.get("SPP_ID"));
			resultMap.put("date", sdf.format(map.get("CREATETIME")));
			resultMap.put("sppName", map.get("SPP_NAME"));
			resultMap.put("sppType", Character.getNumericValue((char) map.get("SPP_TYPE")));
			resultMap.put("braName", map.get("BRANCH_NAME"));
			resultMap.put("empName", map.get("EMP_NAME"));
			resultMap.put("lastUpdate", sdf.format(map.get("LASTUPDATE")));
			resultMap.put("status", Character.getNumericValue((char) map.get("STATUS")));
			result.add(resultMap);
		}
		finalMap.put("custId", ObjectUtils.toString(inputMap.get("custId")));
		finalMap.put("plan", result);
		this.sendRtnObject(finalMap);
	}
	
}