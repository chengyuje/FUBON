package com.systex.jbranch.app.server.fps.mgm116;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO;
import com.systex.jbranch.app.server.fps.mgm110.MGM110OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Carley
 * @date 2018/03/12
 * 
 */
@Component("mgm116")
@Scope("request")
public class MGM116 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	
	//贈品出貨紀錄查詢
	public void inquire (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT CUST.CUST_NAME, CUST.AO_CODE AS CUST_AO_CODE, APP.*, ORG.BRANCH_NBR, ");
		sb.append("ORG.BRANCH_NAME, ORG.AO_CODE, ORG.EMP_NAME, GIFT.GIFT_NAME, GIFT.GIFT_KIND ");
		sb.append("FROM ( SELECT MAI.ACT_SEQ, MAI.CUST_ID, DET.* FROM TBMGM_APPLY_DETAIL DET ");
		sb.append("LEFT JOIN TBMGM_APPLY_MAIN MAI ON DET.APPLY_SEQ = MAI.APPLY_SEQ ");
		sb.append("WHERE MAI.ACT_SEQ = :act_seq ) APP ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CUST ON APP.CUST_ID = CUST.CUST_ID ");
		sb.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO ORG ON APP.CREATOR = ORG.EMP_ID ");
		sb.append("LEFT JOIN TBMGM_GIFT_INFO GIFT ON APP.GIFT_SEQ = GIFT.GIFT_SEQ ");
		sb.append("WHERE 1 = 1 ");
		
		queryCondition.setObject("act_seq", inputVO.getAct_seq());
		
		//有(推薦人)客戶ID即可編輯、補件MGM活動鍵機案件。有輸入客戶ID則略其餘(除活動代碼外)查詢條件。
		if (StringUtils.isNotBlank(inputVO.getCust_id())){
			sb.append("AND APP.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
			
		} else {
			if ("ao".equals(inputVO.getRole())) {
				if (StringUtils.isNotBlank(inputVO.getAo_code())){
					sb.append("AND CUST.AO_CODE = :ao_code ");
					queryCondition.setObject("ao_code", inputVO.getAo_code());
					
				} else {
					//無選取AO Code的理專(AO Code有兩個以上的理專)
					String[] ao_list = inputVO.getAo_list().split(",");
					sb.append("AND CUST.AO_CODE IN ( :ao_list ) ");
					queryCondition.setObject("ao_list", ao_list);					
				}
			}else{
				//有選取分行
				if(!StringUtils.isBlank(inputVO.getBranch_nbr())){
					sb.append("AND CUST.BRA_NBR = :bra_nbr ");	        	
		        	queryCondition.setObject("bra_nbr", inputVO.getBranch_nbr());
				}else{
					//未選取分行，但有選取業務處  || 營運區
					if (!StringUtils.isBlank(inputVO.getRegion_center_id()) || !StringUtils.isBlank(inputVO.getBranch_area_id())) {
		    			        		        	
						sb.append("AND CUST.BRA_NBR IN (:branch_list) ");
						
						List<String> branch_list = new ArrayList<String>();
			        	for(int i = 0; i < inputVO.getBranch_list().size(); i++){
			        		branch_list.add(inputVO.getBranch_list().get(i).get("DATA"));
			        	}
			        	queryCondition.setObject("branch_list", branch_list);
					}
				}
			}
		}
		queryCondition.setQueryString(sb.toString());
		outputVO.setResultList(dam.exeQuery(queryCondition));
		this.sendRtnObject(outputVO);
	}
	
	//匯出客戶簽收單
	public void getReceipt (Object body, IPrimitiveMap header) throws JBranchException, SQLException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT * FROM TBMGM_ACTIVITY_RECEIPT WHERE ACT_SEQ = :act_seq ");
		
		queryCondition.setObject("act_seq", inputVO.getAct_seq());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		String errorMag = null;
		
		try {
			if (list.size() > 0 && list.get(0).get("RECEIPT") != null) {
				String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				String uuid = UUID.randomUUID().toString();
				String fileName = String.format("%s.pdf", uuid);
				Blob blob = (Blob) list.get(0).get("RECEIPT");
				int blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);

				File targetFile = new File(filePath, fileName);
				FileOutputStream fos = new FileOutputStream(targetFile);
				fos.write(blobAsBytes);
				fos.close();
//				notifyClientToDownloadFile("temp//" + uuid, fileName);
				notifyClientViewDoc("temp//" + fileName, "pdf");
			} else {
				errorMag ="查無此資料請洽系統管理員";
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		
		if (StringUtils.isNotBlank(errorMag)) {
			throw new APException(errorMag);
		}
		this.sendRtnObject(null);
	}
}