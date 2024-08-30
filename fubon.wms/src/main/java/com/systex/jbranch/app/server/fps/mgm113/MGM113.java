package com.systex.jbranch.app.server.fps.mgm113;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Carley
 * @date 2018/03/02
 * 
 */
@Component("mgm113")
@Scope("request")
public class MGM113 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	
	private static final String SIGN_STATUS_SAVE  	= "1"; 			//1. 僅鍵機未上傳
	private static final String SIGN_STATUS_SEND  	= "2"; 			//2. 已上傳待主管覆核
	private static final String SIGN_STATUS_AUTH  	= "3"; 			//3. 主管已覆核
	private static final String SIGN_STATUS_REJECT  = "4"; 			//4. 已退回待重新上傳
	
	//查詢待覆核案件一覽(推薦/被推薦簽署表狀態若皆為"已覆核"，就不會出現在待覆核案件一覽。)
	public void inquire (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ORG.EMP_NAME, CUST.BRA_NBR, CUST.AO_CODE, ");
		sb.append("CUST.CUST_NAME AS MGM_CUST_NAME, BE.CUST_NAME AS BE_CUST_NAME, MGM.* ");
		sb.append("FROM ( SELECT * FROM TBMGM_MGM MGM WHERE MGM.ACT_SEQ = :act_seq ) MGM ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CUST ON MGM.MGM_CUST_ID = CUST.CUST_ID ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST BE ON MGM.BE_MGM_CUST_ID = BE.CUST_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER ORG ON MGM.CREATOR = ORG.EMP_ID ");
		sb.append("WHERE 1 = 1 AND (MGM_SIGN_STATUS <> '3' OR BE_MGM_SIGN_STATUS <> '3') ");
		sb.append("AND (DELETE_YN IS NULL OR DELETE_YN <> 'Y') ");
		queryCondition.setObject("act_seq", inputVO.getAct_seq());
		
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
		
		//有填寫推薦人客戶ID
		if (StringUtils.isNotBlank(inputVO.getCust_id())){
			sb.append("AND MGM.MGM_CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
		}
		
		//有填寫被推薦人客戶ID
		if (StringUtils.isNotBlank(inputVO.getBemgm_cust_id())){
			sb.append("AND MGM.BE_MGM_CUST_ID = :bemgm_cust_id ");
			queryCondition.setObject("bemgm_cust_id", inputVO.getBemgm_cust_id());
		}
		
//		//有填寫客戶姓名
//		if (StringUtils.isNotBlank(inputVO.getCust_name())){
//			sb.append("AND CUST.CUST_NAME = :cust_name ");
//			queryCondition.setObject("cust_name", inputVO.getCust_name());
//		}
		
		queryCondition.setQueryString(sb.toString());
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	//覆核&退回
	public void action (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		dam = this.getDataAccessManager();
		
		String seq = inputVO.getSeq();
		String flag = inputVO.getMgm_flag();		// 1.推薦簽署表狀態	2.被推薦簽署表狀態
		String act = inputVO.getAction_type();		// 1.覆核   2.退回
		
		TBMGM_MGMVO vo = new TBMGM_MGMVO();
		vo = (TBMGM_MGMVO) dam.findByPKey(TBMGM_MGMVO.TABLE_UID, seq);
		
		if("1".equals(flag)){									//推薦人簽署
			if("1".equals(act)){
				Timestamp now = new Timestamp(new Date().getTime());
				//覆核
				vo.setMGM_SIGN_STATUS(SIGN_STATUS_AUTH);		//3. 主管已覆核
				vo.setMGM_SIGN_REVIEW_DATE(now);
				
				if(SIGN_STATUS_AUTH.equals(vo.getBE_MGM_SIGN_STATUS())){
					vo.setALL_REVIEW_DATE(now);
				}
			}else if("2".equals(act)){
				//退回
				vo.setMGM_SIGN_STATUS(SIGN_STATUS_REJECT);		//4. 已退回待重新上傳
			}
		} else if ("2".equals(flag)) {							//被推薦人簽署
			if("1".equals(act)){
				Timestamp now = new Timestamp(new Date().getTime());
				//覆核
				vo.setBE_MGM_SIGN_STATUS(SIGN_STATUS_AUTH);		//3. 主管已覆核
				vo.setBE_MGM_SIGN_REVIEW_DATE(now);
				
				if(SIGN_STATUS_AUTH.equals(vo.getMGM_SIGN_STATUS())){
					vo.setALL_REVIEW_DATE(now);
				}
			}else if("2".equals(act)){
				//退回
				vo.setBE_MGM_SIGN_STATUS(SIGN_STATUS_REJECT);	//4. 已退回待重新上傳
			}
		}
		dam.update(vo);
		
		this.sendRtnObject(null);
	}
	
	//刪除功能(需提示是否刪除。簽署表皆已覆核之案件不可刪除。資料庫不刪除，更新TBMGM_MGM.RELEASE_YN為'Y')
	public void delete (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		String seq = inputVO.getSeq();
		if (StringUtils.isNotBlank(seq)){
			dam = this.getDataAccessManager();
			TBMGM_MGMVO vo = (TBMGM_MGMVO) dam.findByPKey(TBMGM_MGMVO.TABLE_UID, seq);
			if(null != vo){
				vo.setDELETE_YN("Y");
				dam.update(vo);
			}
		}
		this.sendRtnObject(null);
	}
	
	//計算待覆核案件數
	public void reviewCount (Object body, IPrimitiveMap header) throws JBranchException {
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SUM(COUNT) COUNT FROM ( ");
		sb.append("SELECT CRM.BRA_NBR, COUNT(*) COUNT FROM TBMGM_MGM MGM ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CRM ON MGM.MGM_CUST_ID = CRM.CUST_ID ");
		sb.append("WHERE MGM.MGM_SIGN_STATUS = '2' OR MGM.BE_MGM_SIGN_STATUS = '2' ");
		sb.append("GROUP BY CRM.BRA_NBR ) COU ");
		sb.append("WHERE BRA_NBR IN ( :branchList ) ");
		
		queryCondition.setObject("branchList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		queryCondition.setQueryString(sb.toString());
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
		
	}
}