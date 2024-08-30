package com.systex.jbranch.app.server.fps.ins112;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;


/**
 * INS112
 * 
 * @author Jimmy
 * @date 2017/08/15
 * @spec null
 */

@Component("ins112")
@Scope("request")
public class INS112 extends FubonWmsBizLogic{
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(INS112.class);
	
	public void queryData(Object body, IPrimitiveMap header){
		INS112InputVO inputVO = (INS112InputVO) body;
		INS112OutputVO outputVO = new INS112OutputVO();
		dam = getDataAccessManager();
		StringBuilder sb = new StringBuilder();
		try {
			
			// 增加取得 TITLE_Y,A,O,K
			// TITLE_Y – 繳費年期的動態欄位名稱
			// TITLE_A – 保障年期的動態欄位名稱
			// TITLE_O – 投保對象的動態欄位名稱
			// TITLE_K – 種類的動態欄位名稱
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append(" SELECT COMP.COM_NAME, MAIN.IS_SALE, ITEM.QID, MAIN.PRD_ID, MAIN.PRD_NAME, MAIN.KEY_NO ");
			sb.append(" , MAIN.TITLE_Y, MAIN.TITLE_A, MAIN.TITLE_O, MAIN.TITLE_K ");
			sb.append(" FROM TBPRD_INSDATA_PROD_MAIN MAIN ");
			sb.append(" LEFT JOIN TBPRD_INSDATA_QUERYITEM ITEM ");
			sb.append(" ON ITEM.KEY_NO = MAIN.KEY_NO ");
			sb.append(" AND ITEM.COM_ID = MAIN.COM_ID ");
			sb.append(" LEFT JOIN TBPRD_INSDATA_COMPANY COMP ");
			sb.append(" ON COMP.COM_ID = MAIN.COM_ID ");
			sb.append(" WHERE MAIN.COM_ID = :com_id and MAIN.IFCHS <> 'F' ");
			qc.setObject("com_id", inputVO.getCOM_ID());//保險公司代碼
			
			sb.append(" AND MAIN.IS_MAIN = :ismain ");
			qc.setObject("ismain", inputVO.getIS_MAIN_TYPE());
			
			
			// 附約幣別
			if("N".equals(inputVO.getIS_MAIN_TYPE())) {
				sb.append(" AND  instr(nvl(MAIN.LIST_E, :currCd), :currCd) > 0 ");
				qc.setObject("currCd", inputVO.getCURR_CD());
			}
			
			if(StringUtils.isNotBlank(inputVO.getIS_SALE())){
				sb.append(" AND MAIN.IS_SALE = :is_sale ");	//商品狀態
				qc.setObject("is_sale", inputVO.getIS_SALE());
			}
			
			if(StringUtils.isNotBlank(inputVO.getQID())){
				sb.append(" AND ITEM.QID = :qid ");	//險種別
				qc.setObject("qid", inputVO.getQID());
			}
			
			if(StringUtils.isNotBlank(inputVO.getPRD_ID())){
				sb.append(" AND MAIN.PRD_ID LIKE :prd_id ");
				qc.setObject("prd_id", "%"+inputVO.getPRD_ID()+"%");
			}
			
			if(StringUtils.isNotBlank(inputVO.getPRD_NAME())){
				sb.append(" AND MAIN.PRD_NAME LIKE :prd_name ");
				qc.setObject("prd_name", "%"+inputVO.getPRD_NAME()+"%");
			}
			
			//使用商品代碼排序
			sb.append(" order by MAIN.PRD_ID ");
			qc.setQueryString(sb.toString());
			outputVO.setQueryList(dam.exeQuery(qc));
			
			this.sendRtnObject(outputVO);
			
		} catch (JBranchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
}
