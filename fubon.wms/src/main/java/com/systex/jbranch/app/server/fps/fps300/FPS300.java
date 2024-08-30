package com.systex.jbranch.app.server.fps.fps300;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.sot701.CustKYCDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.esb.vo.fp032151.FP032151OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("fps300")
@Scope("request")
public class FPS300 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	
	public void queryCustMast(Object body, IPrimitiveMap header) throws Exception {
		FPS300InputVO inputVo = (FPS300InputVO)body;
		FPS300OutputVO outputVo = new FPS300OutputVO();
		List<Map<String, Object>> custResulList = new ArrayList<Map<String,Object>>();
		Map<String,Object> custResul = new HashMap<String, Object>();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT m.CUST_ID, m.CUST_NAME, m.SAL_COMPANY, m.ANNUAL_INCOME_AMT, ");
		sb.append(" n.SIGN_AGMT_YN, n.COMM_RS_YN, n.COMM_NS_YN ");
		sb.append(" FROM TBCRM_CUST_MAST m ");
		sb.append(" LEFT JOIN TBCRM_CUST_NOTE n on n.CUST_ID = m.CUST_ID ");
		sb.append(" WHERE m.CUST_ID = :custId ");			
		queryCondition.setObject("custId", inputVo.getCustId());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		custResul.put("CUST_ID", list.get(0).get("CUST_ID"));//客戶ID
		custResul.put("CUST_NAME", list.get(0).get("CUST_NAME"));//客戶姓名
		custResul.put("SAL_COMPANY", list.get(0).get("SAL_COMPANY") == null ? "N" :"Y");//薪資戶
		custResul.put("ANNUAL_INCOME_AMT", list.get(0).get("ANNUAL_INCOME_AMT"));//年收入
		custResul.put("SIGN_AGMT_YN", list.get(0).get("SIGN_AGMT_YN"));//已簽署推介同意書
		custResul.put("COMM_RS_YN", list.get(0).get("COMM_RS_YN"));//
		custResul.put("COMM_NS_YN", list.get(0).get("COMM_NS_YN"));//
		
		SOT701InputVO inputVO_701 = new SOT701InputVO();
		inputVO_701.setCustID(inputVo.getCustId());
		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		FP032675DataVO FP032675Data = sot701.getFP032675Data(inputVO_701);		
		FP032151OutputVO FP032151Data = sot701.getFP032151Data(inputVO_701, header);
		CustKYCDataVO CustKYCData = sot701.getCustKycData(inputVO_701);
		
		//FC032675DataVO.java及SOT710.java 增加BDAY_D 在1.5階新增 到時要注意
		custResul.put("BDAY_D", FP032151Data.getBDAY_D());//年齡
		custResul.put("EDUCATION", FP032151Data.getEDUCATION());//學歷
		custResul.put("KYC_LEVEL", CustKYCData.getKycLevel());//KYC等級
		custResul.put("KYC_DUE_DATE", CustKYCData.getKycDueDate());//KYC(效期)
		custResul.put("OBU_FLAG", FP032675Data.getObuFlag());//OBU註記
		custResul.put("CUST_PRO_FLAG", FP032675Data.getCustProFlag());//專業投資人
		custResul.put("CUST_PRO_DATE", FP032675Data.getCustProDate());//專業投資人(效期)
		custResulList.add(custResul);
		outputVo.setCustResulList(custResulList);
		
		sendRtnObject(outputVo);
	}
	
	public void getStep(Object body, IPrimitiveMap header) throws Exception {
		FPS300InputVO inputVo = (FPS300InputVO)body;
		FPS300OutputVO outputVo = new FPS300OutputVO();
		String step = "1";
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		//績效追蹤
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT CUST_ID ");
		sb.append(" FROM TBFPS_SPP_PRD_RETURN_HEAD ");
		sb.append(" WHERE CUST_ID = :custId ");
		queryCondition.setObject("custId", inputVo.getCustId());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			step = "2";
		} else {
			//歷史規劃
			sb = new StringBuilder();
			sb.append(" SELECT CUST_ID ");
			sb.append(" FROM TBFPS_PORTFOLIO_PLAN_SPP_HEAD ");
			sb.append(" WHERE CUST_ID = :custId ");
			queryCondition.setObject("custId", inputVo.getCustId());
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			if (list2.size() > 0) {
				step = "3";
			}
		}	
		outputVo.setStep(step);
		sendRtnObject(outputVo);
	}

  // 取得當前的前一個營業日 (沒時間抽共用 同 FPS200)
  public String getPrevBussinessDay(DataAccessManager dam) throws DAOException, JBranchException {
	  QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	  StringBuffer sql = new StringBuffer();
      sql.append("SELECT PABTH_UTIL.FC_getBusiDay(sysdate, 'TWD', -1)AS PREV_BUSI_DAY FROM DUAL");
	  queryCondition.setQueryString(sql.toString());
	  List<Map<String, Object>> busDayList = dam.exeQuery(queryCondition);
	  Date busDay = CollectionUtils.isNotEmpty(busDayList) ? new GenericMap(busDayList.get(0)).getDate("PREV_BUSI_DAY") : new Date(new Date().getTime() - 2);;
	  return new SimpleDateFormat("yyyy/MM/dd").format(busDay);
  }
}
