package com.systex.jbranch.app.server.fps.mgm112;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.birt.report.model.api.util.StringUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO;
import com.systex.jbranch.app.server.fps.mgm110.MGM110OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Carley
 * @date 2018/03/01
 * 
 */
@Component("mgm112")
@Scope("request")
public class MGM112 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	
	//查詢轄下鍵機案件總覽
	public void inquire (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		
		if (StringUtils.isNotBlank(inputVO.getAct_seq())){
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT AO.EMP_ID, BAO.EMP_ID AS BE_EMP_ID, ");
			sb.append("ORG.EMP_NAME, CUST.BRA_NBR, CUST.AO_CODE, CUST.CUST_NAME, MGM.*, ");
			sb.append("(CASE WHEN MGM.MGM_SIGN_STATUS != '3' OR MGM.BE_MGM_SIGN_STATUS != '3' THEN 1111 ");
			sb.append("WHEN MGM.MGM_SIGN_STATUS = '3' AND MGM.BE_MGM_SIGN_STATUS = '3' AND ");
			sb.append("TO_CHAR(SYSDATE, 'YYYY/MM/DD') < TO_CHAR(MGM.MGM_END_DATE, 'YYYY/MM/DD') THEN 2222 ");
			sb.append("WHEN MGM.MGM_SIGN_STATUS = '3' AND MGM.BE_MGM_SIGN_STATUS = '3' AND ");
			sb.append("TO_CHAR(SYSDATE, 'YYYY/MM/DD') >= TO_CHAR(MGM.MGM_END_DATE, 'YYYY/MM/DD') ");
			sb.append("AND MGM.MGM_APPR_STATUS = 'Y' THEN ( CASE WHEN MGM.RELEASE_YN <> 'Y' OR ");
			sb.append("MGM.RELEASE_YN IS NULL THEN 5555 WHEN MGM.RELEASE_YN = 'Y' THEN MGM.APPR_POINTS END ) ");
			sb.append("WHEN MGM.MGM_SIGN_STATUS = '3' AND MGM.BE_MGM_SIGN_STATUS = '3' AND ");
			sb.append("TO_CHAR(SYSDATE, 'YYYY/MM/DD') >= TO_CHAR(MGM.MGM_END_DATE, 'YYYY/MM/DD') ");
			sb.append("AND MGM.MGM_APPR_STATUS = 'N' THEN 4444 ELSE 2222 END) AS APPR_STATUS, ");
//			sb.append("(CASE WHEN MGM.MGM_SIGN_STATUS = '3' AND MGM.BE_MGM_SIGN_STATUS = '3' ");
//			sb.append("THEN MGM.INS_SELL_VOL ELSE NULL END) AS INS FROM ( ");
			sb.append("NVL(MGM.INS_SELL_VOL, 0) AS INS FROM ( ");
			sb.append("SELECT * FROM TBMGM_MGM MGM WHERE MGM.ACT_SEQ = :act_seq AND MGM.POINTS_TYPE = '1' ) MGM ");
			sb.append("LEFT JOIN TBCRM_CUST_MAST CUST ON MGM.MGM_CUST_ID = CUST.CUST_ID ");
			sb.append("LEFT JOIN TBORG_SALES_AOCODE AO ON CUST.AO_CODE = AO.AO_CODE ");
			sb.append("LEFT JOIN TBCRM_CUST_MAST BCUST ON MGM.BE_MGM_CUST_ID = BCUST.CUST_ID ");
			sb.append("LEFT JOIN TBORG_SALES_AOCODE BAO ON BCUST.AO_CODE = BAO.AO_CODE ");
			sb.append("LEFT JOIN TBORG_MEMBER ORG ON MGM.CREATOR = ORG.EMP_ID ");
			sb.append("WHERE 1 = 1 AND (DELETE_YN IS NULL OR DELETE_YN <> 'Y') ");
			
			queryCondition.setObject("act_seq", inputVO.getAct_seq());
			
			//有(推薦人/被推薦人)客戶ID即可編輯、補件MGM活動鍵機案件。有輸入(推薦人/被推薦人)客戶ID則忽略其餘(除活動代碼外)查詢條件。
			if (StringUtils.isNotBlank(inputVO.getCust_id()) || StringUtils.isNotBlank(inputVO.getBemgm_cust_id())){
				if(StringUtils.isNotBlank(inputVO.getCust_id())) {
					sb.append("AND MGM.MGM_CUST_ID = :cust_id ");
					queryCondition.setObject("cust_id", inputVO.getCust_id());					
				}
				if(StringUtils.isNotBlank(inputVO.getBemgm_cust_id())) {
					sb.append("AND MGM.BE_MGM_CUST_ID = :be_mgm_cust_id ");
					queryCondition.setObject("be_mgm_cust_id", inputVO.getBemgm_cust_id());					
				}
			} else {
				//AO Code  (getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST))
				if(!StringUtils.isBlank(inputVO.getAo_code())){
					sb.append("AND CUST.AO_CODE = :ao_code ");	        	
		        	queryCondition.setObject("ao_code", inputVO.getAo_code());
				} else {
					//無選取AO Code的理專(AO Code有兩個以上的理專)
					if (StringUtil.isEqual(inputVO.getRole(), "ao")) {
						String[] ao_list = inputVO.getAo_list().split(",");
						sb.append("AND CUST.AO_CODE IN ( :ao_list ) ");
			        	queryCondition.setObject("ao_list", ao_list);
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
			}
			queryCondition.setQueryString(sb.toString());		
			outputVO.setResultList(dam.exeQuery(queryCondition));
			
		} else {
			throw new APException("ehl_01_common_022");		//欄位檢核錯誤：*為必要輸入欄位,請輸入後重試
		}
		
		this.sendRtnObject(outputVO);
	}
}