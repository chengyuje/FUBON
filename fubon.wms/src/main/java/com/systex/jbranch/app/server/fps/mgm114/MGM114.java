package com.systex.jbranch.app.server.fps.mgm114;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.birt.report.model.api.util.StringUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBMGM_MGMVO;
import com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO;
import com.systex.jbranch.app.server.fps.mgm110.MGM110OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Carley
 * @date 2018/03/05
 * 
 */
@Component("mgm114")
@Scope("request")
public class MGM114 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	
	//轄下兌換紀錄查詢
	public void inquire (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT APP.ACT_SEQ, APP.CUST_ID, CUST.CUST_NAME, ");
		sb.append("CUST.BRA_NBR, DEFN.DEPT_NAME AS BRA_NAME, ");
		sb.append("CUST.AO_CODE, AO.EMP_ID, ORG.EMP_NAME, ");
		sb.append("MGM.TOTAL, APP.EXC_POINTS, (MGM.TOTAL - APP.EXC_POINTS) AS UN_EXC, ");
		sb.append("EXH.EMP_NAME AS EXH_EMP_NAME FROM ( ");
		sb.append("SELECT ACT_SEQ, CUST_ID, SUM(EXCHANGE_POINTS) AS EXC_POINTS ");
		sb.append("FROM TBMGM_APPLY_MAIN WHERE ACT_SEQ = :act_seq GROUP BY ACT_SEQ, CUST_ID ");
		sb.append(") APP LEFT JOIN ( ");
		sb.append("SELECT MGM_CUST_ID, SUM(APPR_POINTS) AS TOTAL FROM TBMGM_MGM ");
		sb.append("WHERE RELEASE_YN = 'Y' AND ACT_SEQ = :act_seq GROUP BY MGM_CUST_ID ");
		sb.append(") MGM ON APP.CUST_ID = MGM.MGM_CUST_ID ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CUST ON APP.CUST_ID = CUST.CUST_ID ");
		sb.append("LEFT JOIN TBORG_DEFN DEFN ON CUST.BRA_NBR = DEFN.DEPT_ID ");
		sb.append("LEFT JOIN TBORG_SALES_AOCODE AO ON CUST.AO_CODE = AO.AO_CODE ");
		sb.append("LEFT JOIN TBORG_MEMBER ORG ON AO.EMP_ID = ORG.EMP_ID LEFT JOIN ( ");
		sb.append("SELECT * FROM ( SELECT ACT_SEQ, CUST_ID, CREATOR, CREATETIME, ROW_NUMBER() OVER ( ");
		sb.append("PARTITION BY CUST_ID ORDER BY CREATETIME DESC) AS SORT FROM TBMGM_APPLY_MAIN ");
		sb.append("WHERE ACT_SEQ = :act_seq ) WHERE SORT = 1 ");
		sb.append(") EMP ON APP.CUST_ID = EMP.CUST_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER EXH ON EMP.CREATOR = EXH.EMP_ID ");
		sb.append("WHERE 1 = 1 ");
		
		queryCondition.setObject("act_seq", inputVO.getAct_seq());
		
		//有(推薦人)客戶ID即可編輯、補件MGM活動鍵機案件。有輸入客戶ID則略其餘(除活動代碼外)查詢條件。
		if (StringUtils.isNotBlank(inputVO.getCust_id())){
			sb.append("AND APP.CUST_ID LIKE :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
			
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
		sb.append("ORDER BY APP.CUST_ID ");
		queryCondition.setQueryString(sb.toString());
		outputVO.setResultList(dam.exeQuery(queryCondition));
		this.sendRtnObject(outputVO);
	}
	
	//查詢推薦人核點明細
	public void getPointDetail (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT CUST.CUST_NAME, BE_CUST.CUST_NAME AS BE_MGM_CUST_NAME, MGM.* FROM ( ");
		sb.append("SELECT MGM_CUST_ID, BE_MGM_CUST_ID, POINTS_TYPE, APPR_POINTS, APPR_DATE FROM TBMGM_MGM ");
		sb.append("WHERE ACT_SEQ = :act_seq AND MGM_CUST_ID = :mgm_cust_id ");
		sb.append("AND APPR_DATE IS NOT NULL AND RELEASE_YN = 'Y' ) MGM ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CUST ON MGM.MGM_CUST_ID = CUST.CUST_ID ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST BE_CUST ON MGM.BE_MGM_CUST_ID = BE_CUST.CUST_ID ");
		
		queryCondition.setObject("act_seq", inputVO.getAct_seq());
		queryCondition.setObject("mgm_cust_id", inputVO.getCust_id());
		
		queryCondition.setQueryString(sb.toString());		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	//查詢禮贈品兌換紀錄明細
	public void getExchDetail (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DETAIL.*, INFO.GIFT_NAME, INFO.GIFT_KIND, ORG.EMP_NAME FROM ( ");
		sb.append("SELECT APPLY_SEQ, APPLY_POINTS, APPLY_REWARD, APPLY_NUMBER, GIFT_SEQ, CREATOR, CREATETIME ");
		sb.append("FROM TBMGM_APPLY_DETAIL WHERE APPLY_SEQ IN(SELECT APPLY_SEQ FROM TBMGM_APPLY_MAIN ");
		sb.append("WHERE ACT_SEQ = :act_seq AND CUST_ID = :cust_id )) DETAIL ");
		sb.append("LEFT JOIN TBMGM_GIFT_INFO INFO ON DETAIL.GIFT_SEQ = INFO.GIFT_SEQ ");
		sb.append("LEFT JOIN TBORG_MEMBER ORG ON DETAIL.CREATOR = ORG.EMP_ID ");
		sb.append("ORDER BY DETAIL.CREATETIME DESC, APPLY_SEQ DESC ");
		
		queryCondition.setObject("act_seq", inputVO.getAct_seq());
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		
		queryCondition.setQueryString(sb.toString());		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
}