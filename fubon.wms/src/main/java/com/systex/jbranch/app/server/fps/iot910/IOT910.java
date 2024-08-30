package com.systex.jbranch.app.server.fps.iot910;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * IOT130
 * 
 * @author Jimmy
 * @date 2016/09/20
 * @spec null
 */
@Component("iot910")
@Scope("request")
public class IOT910 extends FubonWmsBizLogic{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	DataAccessManager dam_obj;
			
	public void queryINSPRD(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		IOT910InputVO inputVO = (IOT910InputVO) body;
		IOT910OutputVO outputVO = new IOT910OutputVO();
		
		if(StringUtils.equals("Y", inputVO.getFB_COM_YN())) {
			//富壽保險商品
			outputVO = getINSPRD_FB(inputVO);
		} else {
			//非富壽保險商品
			outputVO = getINSPRD_NFB(inputVO);
		}
		
		sendRtnObject(outputVO);
	}
	
	public IOT910OutputVO getINSPRD_FB(IOT910InputVO inputVO) throws DAOException, JBranchException {
		IOT910OutputVO outputVO = new IOT910OutputVO();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		sb.append(" SELECT A.*, B.CNAME AS INS_COM_NAME ");
		sb.append(" FROM TBPRD_INS_MAIN A ");
		sb.append(" LEFT JOIN TBJSB_INS_PROD_COMPANY B ON B.SERIALNUM = A.COMPANY_NUM ");
		sb.append(" WHERE TRUNC(A.EFFECT_DATE)<=TRUNC(SYSDATE) ");
		sb.append("  AND TRUNC(NVL(A.EXPIRY_DATE, SYSDATE)) >= TRUNC(SYSDATE) ");
		sb.append("  AND A.MAIN_RIDER = :rider ");
		sb.append("  AND A.COMPANY_NUM = '82' "); //富壽
		
		qc.setObject("rider", inputVO.isINS_RIDER_DLT() ? "R" : "M");
		if(StringUtils.isNotBlank(inputVO.getINSPRD_ID())){
			sb.append(" and A.INSPRD_ID like :insprd_id ");
			qc.setObject("insprd_id", "%"+inputVO.getINSPRD_ID()+"%");
		}
		
		logger.info("#[IOT910 SQL] : " + sb.toString());
		logger.info("#[IOT910 rider] : " + inputVO.isINS_RIDER_DLT() == null ? "null" : (inputVO.isINS_RIDER_DLT() ? "R" : "M"));
		logger.info("#[IOT910 insprd_id] : " + "%"+inputVO.getINSPRD_ID()+"%");
		
		qc.setQueryString(sb.toString());
		outputVO.setINSPRDList(dam_obj.exeQuery(qc));
		//參考匯率
		for(Map<String, Object> map:outputVO.getINSPRDList()){
			if(map.get("CURR_CD").toString().equals("TWD")){
				map.put("EXCH_RATE", 1);
			}else{
				List<Map<String, Object>> IQ053List = new ArrayList<Map<String,Object>>();
				qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append("SELECT * FROM TBPMS_IQ053 ");
				sb.append("WHERE CUR_COD = :CURR_CD ");
				sb.append("AND MTN_DATE = (SELECT MAX(MTN_DATE) ");
				sb.append("FROM TBPMS_IQ053 ");
				sb.append("WHERE CUR_COD = :CURR_CD ");
				sb.append("AND (TRUNC(MTN_DATE) - TRUNC(MTN_DATE, 'MM')) <= 24 ");
				sb.append("AND TRUNC(MTN_DATE) BETWEEN TRUNC(CASE WHEN TO_CHAR(SYSDATE, 'DD') < 25 THEN TRUNC(TO_DATE(ADD_MONTHS(SYSDATE, -1)), 'MM') ");
				sb.append("ELSE TRUNC(SYSDATE, 'MM') ");
				sb.append("END) ");
				sb.append("AND TRUNC(CASE WHEN TO_CHAR(SYSDATE, 'DD') < 25 THEN LAST_DAY(ADD_MONTHS(SYSDATE, -1)) ");
				sb.append("ELSE LAST_DAY(SYSDATE) + 1 ");
				sb.append("END)) ");
				qc.setObject("CURR_CD", map.get("CURR_CD").toString());
				qc.setQueryString(sb.toString());
				IQ053List = dam_obj.exeQuery(qc);
				if(IQ053List.size()>0){
					BigDecimal BUY_RATE = new BigDecimal(IQ053List.get(0).get("BUY_RATE").toString());
					BigDecimal SEL_RATE = new BigDecimal(IQ053List.get(0).get("SEL_RATE").toString());
					map.put("EXCH_RATE", (BUY_RATE.add(SEL_RATE)).divide(new BigDecimal(2), 2, BigDecimal.ROUND_DOWN));
				} else {
					map.put("EXCH_RATE", null);
				}
			}			
		}
		
		return outputVO;
	}
	
	private IOT910OutputVO getINSPRD_NFB(IOT910InputVO inputVO) throws DAOException, JBranchException {
		IOT910OutputVO outputVO = new IOT910OutputVO();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		sb.append("SELECT COM.CNAME AS INS_COM_NAME, ");
        sb.append("       PRD_M.PRODUCTSERIALNUM AS INSPRD_KEYNO, PRD_M.PRODUCTID AS INSPRD_ID, PRD_M.PRODUCTNAME AS INSPRD_NAME, PRD_M.PRODUCTSHORTNAME AS INSPRD_NAME_SHORT, ");
        sb.append("       PRD_M.CONTRACTID AS MAIN_RIDER, PRD_M.PRODUCTON_DT, PRD_M.PRODUCTVALIDFROM, PRD_M.PRODUCTVALIDTHRU, ");
        sb.append("       PRD_M.PRODUCTTYPE1, PRD_M.PREMIUMTABLE, PRD_M.CURRENCY1, CODE.CD_DESC AS CURR_CD, 'N' AS NEED_MATCH, ");
        sb.append("       CASE WHEN TRIM(PRD_M.PRODUCTTYPE1) <> '投資型' THEN '1' ELSE '2' END AS INSPRD_TYPE, ");
        sb.append("       ITEM.ITEMSERIALNUM, ITEM.PRODUCTPUDTYPE, ITEM.PRODUCTPUD, ITEM.PRODUCTEDTYPE, ITEM.PRODUCTED, PRD_M.INSURANCECOSERIALNUM AS COMPANY_NUM ");
        sb.append("FROM TBJSB_INS_PROD_MAIN PRD_M ");
        sb.append("LEFT JOIN TBJSB_INS_PROD_COMPANY COM ON PRD_M.INSURANCECOSERIALNUM = COM.SERIALNUM ");
        sb.append("LEFT JOIN TBJSB_INS_PROD_LIFEITEM ITEM ON PRD_M.PRODUCTSERIALNUM = ITEM.PRODUCTSERIALNUM ");
        sb.append("LEFT JOIN TBJSB_INS_PROD_CODE_TABLE CODE ON CODE.CD_INDEX = 'A08' AND CODE.CD_NM = PRD_M.CURRENCY1 ");
        sb.append("WHERE 1 = 1 ");
        
        //以商品ID查詢
        if(StringUtils.isNotBlank(inputVO.getINSPRD_ID())) {
			sb.append(" and PRD_M.PRODUCTID like :insprd_id ");
			qc.setObject("insprd_id", "%"+inputVO.getINSPRD_ID()+"%");
		}
        //以保險公司查詢
        if(StringUtils.isNotBlank(inputVO.getCOMPANY_NUM())) {
			sb.append(" and PRD_M.INSURANCECOSERIALNUM  = :companyNum ");
			qc.setObject("companyNum", inputVO.getCOMPANY_NUM());
		}
        
        qc.setQueryString(sb.toString());
		outputVO.setINSPRDList(dam_obj.exeQuery(qc));
		
		return outputVO;
	}
}
