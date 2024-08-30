package com.systex.jbranch.app.server.fps.crm82A;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrmCons.NMP8YB;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrmCons.NMI002;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmi002.CustAssetPotInfo;
import com.systex.jbranch.fubon.commons.esb.vo.nmi002.NMI002InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmi002.NMI002OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmi002.NMI002OutputVODetails;
import com.systex.jbranch.fubon.commons.esb.vo.nmp8yb.CustAssetNMP8YB;
import com.systex.jbranch.fubon.commons.esb.vo.nmp8yb.NMP8YBInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmp8yb.NMP8YBOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmp8yb.NMP8YBOutputVODetails;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;


@Component("crm82a")
@Scope("request")
public class CRM82A extends EsbUtil {
    /**
     * 取得客戶金市海外債資料
     * @param body CRM82AInputVO
     * @param header
     * @throws Exception
     */
	public void getGoldBondAsset(Object body, IPrimitiveMap header) throws Exception {
		CRM82AInputVO inputVO = (CRM82AInputVO) body;
		CRM82AOutputVO outputVO = new CRM82AOutputVO();
		BigDecimal totalInvestment = BigDecimal.ZERO;		//總投資金額(原幣)
		BigDecimal totalInvestmentTwd = BigDecimal.ZERO;	//總投資金額(台幣)
		BigDecimal totalMarketValueTwd = BigDecimal.ZERO;	//參考總市值(台幣)
//		BigDecimal totalInvPL = BigDecimal.ZERO;			//總投資損益
		BigDecimal totalPnlAmt = BigDecimal.ZERO;			//總投資損益(含息)
		
		DataAccessManager dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		
		sb.append(" SELECT A.*, NVL(IS_RS, 'N') AS IS_RS_DIS ");
//		sb.append(" (NVL(A.M_TP_NOMINAL, 0) * NVL(B.SEL_RATE, 0)) AS M_TP_NOMINAL_TWD ");	//庫存面額(台幣)
		//投資損益 = (參考贖回報價% Ｘ 庫存面額 ＋ 累計現金配息＋ 應收前手息 － 已付前手息) － 信託本金
//		sb.append(" ((NVL(A.M_TP_NOMINAL, 0) * NVL(A.QUTOATION, 0)) + NVL(A.ACCUMULATED_INTEREST, 0) + NVL(A.ACCRUED_AMT_RCV, 0) - NVL(A.ACCRUED_AMT_PAY, 0) - NVL(A.INVEST_COST_AMT, 0)) AS INV_PL ");	//投資損益
		sb.append(" FROM TBCRM_AST_INV_VPBND_1002 A ");
		sb.append(" LEFT OUTER JOIN TBPMS_IQ053 B ON B.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) AND B.CUR_COD = A.M_TP_NOMCUR ");
		sb.append(" WHERE A.M_TP_CNTRP = :custId AND M_TRN_GRP = 'BOND' ");	
		qc.setObject("custId", inputVO.getCust_id());
		qc.setQueryString(sb.toString());
		resultList = dam_obj.exeQuery(qc);
        
		if(CollectionUtils.isNotEmpty(resultList)) {
			for(Map<String, Object> item : resultList) {
				String invVal = ObjectUtils.toString(item.get("INVEST_COST_AMT"));
				String invValTwd = ObjectUtils.toString(item.get("INVEST_COST_AMT_TWD"));
				String marketValTwd = ObjectUtils.toString(item.get("MARKET_VALUE_AMT_TWD"));
//				String invPL = ObjectUtils.toString(item.get("INV_PL"));
				String pnlAmt = ObjectUtils.toString(item.get("PNL_AMT"));	//含息損益				
				
				totalInvestment = totalInvestment.add(StringUtils.isNotBlank(invVal) ? new BigDecimal(invVal) : BigDecimal.ZERO);
				totalInvestmentTwd = totalInvestmentTwd.add(StringUtils.isNotBlank(invValTwd) ? new BigDecimal(invValTwd) : BigDecimal.ZERO);
				totalMarketValueTwd = totalMarketValueTwd.add(StringUtils.isNotBlank(marketValTwd) ? new BigDecimal(marketValTwd) : BigDecimal.ZERO);
//				totalInvPL = totalInvPL.add(StringUtils.isNotBlank(invPL) ? new BigDecimal(invPL) : BigDecimal.ZERO);
				totalPnlAmt = totalPnlAmt.add(StringUtils.isNotBlank(pnlAmt) ? new BigDecimal(pnlAmt) : BigDecimal.ZERO);
			}
		}
		
		outputVO.setTotalInvestmentTwd(totalInvestmentTwd);
		outputVO.setTotalMarketValueTwd(totalMarketValueTwd);
		outputVO.setTotalROI(totalInvestment == BigDecimal.ZERO ? BigDecimal.ZERO : totalPnlAmt.multiply(new BigDecimal(100)).divide(totalInvestment, 4));	//總含息報酬率
		outputVO.setResultList(resultList);
		
		this.sendRtnObject(outputVO);
	}
	
}