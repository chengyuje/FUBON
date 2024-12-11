package com.systex.jbranch.app.server.fps.crm827;

import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvipa.NMVIPAInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvipa.NMVIPAOutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvipa.NMVIPAOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp3a.NMVP3AInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp3a.NMVP3AOutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp3a.NMVP3AOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp4a.NMVP4AInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp4a.NMVP4AOutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp4a.NMVP4AOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp5a.NMVP5AInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp5a.NMVP5AOutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp5a.NMVP5AOutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.birt.report.model.api.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrmCons.*;

/**
 * crm827
 * 
 * @author moron
 * @date 2016/05/27
 * @spec null
 */
@Component("crm827")
@Scope("request")
public class CRM827 extends EsbUtil {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM827.class);
	/* const */
    private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
    private String thisClaz = this.getClass().getSimpleName() + ".";
	
	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		CRM827InputVO inputVO = (CRM827InputVO) body;
		CRM827OutputVO return_VO = new CRM827OutputVO();
		
		String custID = inputVO.getCust_id();
        String htxtid = NMVIPA_CUST_DATA;

        //init util
        ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
        esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        txHead.setDefaultTxHead();
        esbUtilInputVO.setTxHeadVO(txHead);

        //body
        NMVIPAInputVO nmvipaInputVO = new NMVIPAInputVO();
        nmvipaInputVO.setFUNCTION("58");
//        nmvipaInputVO.setRANGE("B737");
        nmvipaInputVO.setUNIT("D");
        nmvipaInputVO.setCUSID(custID);        //客戶ID
        esbUtilInputVO.setNmvipaInputVO(nmvipaInputVO);

        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

        List<CustAssetNMVIPAVO> list = new ArrayList<>();
        CustAssetNMVIPAVO asset = new CustAssetNMVIPAVO();
        
        /**資料分類儲存用TYPE**/
        String checkTYPE = "";
        String checkTYPE_old = "";
        for(ESBUtilOutputVO esbUtilOutputVO : vos) {
        	NMVIPAOutputVO nmvipaOutputVO = esbUtilOutputVO.getNmvipaOutputVO();

        	List<NMVIPAOutputDetailsVO> details = nmvipaOutputVO.getDetails();
            details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NMVIPAOutputDetailsVO>() : details;
            
            //取得庫存資料
            for (NMVIPAOutputDetailsVO detail : details) {
                //符合資料放入庫存CustAssetBondVO
            	
            	if (StringUtil.isEqual(nmvipaOutputVO.getFUNCTION(), "DA")) {
            		checkTYPE = "A";
            		
            		asset.setDA_ARR00(detail.getARR00());
            		asset.setDA_ARR01(detail.getARR01());
            		asset.setDA_ARR02(detail.getARR02());
            		asset.setDA_ARR03(detail.getARR03());
            		asset.setDA_ARR04(detail.getARR04());
            		//日期格式
            		if(detail.getARR05().length() == 8){
            			java.util.Date DA_ARR05 = new EsbUtil().toAdYearMMdd(detail.getARR05());
            			asset.setDA_ARR05(new java.text.SimpleDateFormat("yyyy/MM/dd").format(DA_ARR05));
            		}else{
            			asset.setDA_ARR05(detail.getARR05());
            		}
            		if(detail.getARR06().length() == 8){
            			java.util.Date DA_ARR06 = new EsbUtil().toAdYearMMdd(detail.getARR06());
            			asset.setDA_ARR06(new java.text.SimpleDateFormat("yyyy/MM/dd").format(DA_ARR06));
            		}else{
            			asset.setDA_ARR06(detail.getARR06());
            		}
            		
            		asset.setDA_ARR07(detail.getARR07());
            		asset.setDA_ARR08(new EsbUtil().decimalPoint(detail.getARR08(), 2).toString());
            		asset.setDA_ARR09(new EsbUtil().decimalPoint(detail.getARR09(), 2).toString());
            		asset.setDA_ARR10(detail.getARR10());
            		asset.setDA_ARR11(detail.getARR11());
            		asset.setDA_ARR12(new EsbUtil().decimalPoint(detail.getARR12(), 4).toString());
            		asset.setDA_ARR13(detail.getARR13());
            		asset.setDA_ARR14(detail.getARR14());
            		asset.setDA_V0015(detail.getV0015());
            		asset.setDA_V0016(detail.getV0016());
            		asset.setDA_V0017(detail.getV0017());
            		asset.setDA_V0018(detail.getV0018());
            		asset.setDA_V0019(detail.getV0019());
            		
            		String v0020Str = "";
            		int v0021int = Integer.valueOf(detail.getV0021());
            		if(detail.getV0020().equals("1")) {
            			v0020Str="每月第"+ String.valueOf(v0021int) + "日";
            		} else if (detail.getV0020().equals("2")) {
            			v0020Str = String.valueOf(v0021int) + "個月扣帳一次";
            		} else if(detail.getV0020().equals("3") ) {
            			v0020Str = "到期扣帳";
            		}
            		asset.setDA_V0020(v0020Str);
            		asset.setDA_V0021(detail.getV0021());
            		
            		asset.setDA_V0022(detail.getV0022());
            		asset.setDA_V0023(detail.getV0023());
            		asset.setDA_V0024(detail.getV0024());
            		asset.setDA_V0025(detail.getV0025());
            		
            	} else if (StringUtil.isEqual(nmvipaOutputVO.getFUNCTION(), "DB")) {
            		checkTYPE = "B";
            		
            		asset.setDB_ARR00(detail.getARR00());
            		asset.setDB_ARR01(detail.getARR01());
            		asset.setDB_ARR02(new EsbUtil().decimalPoint(detail.getARR02(), 2).toString());
            		asset.setDB_ARR03(detail.getARR03());
            		asset.setDB_ARR04(detail.getARR04());
            		//日期格式
            		if(detail.getARR05().length() == 8){
        				java.util.Date DA_ARR05 = new EsbUtil().toAdYearMMdd(detail.getARR05());
        				asset.setDB_ARR05(new java.text.SimpleDateFormat("yyyy/MM/dd").format(DA_ARR05));
            		}else{
            			asset.setDB_ARR05(detail.getARR05());
            		}          		
            		if(detail.getARR06().length() == 8){
            			java.util.Date DA_ARR06 = new EsbUtil().toAdYearMMdd(detail.getARR06());
            			asset.setDB_ARR06(new java.text.SimpleDateFormat("yyyy/MM/dd").format(DA_ARR06));
            		}else{
            			asset.setDB_ARR06(detail.getARR06());
            		}
            		
            		asset.setDB_ARR07(detail.getARR07());
            		asset.setDB_ARR08(detail.getARR08());
            		asset.setDB_ARR09(detail.getARR09());
            		asset.setDB_ARR10(new EsbUtil().decimalPoint(detail.getARR10(), 2).toString());
            		asset.setDB_ARR11(detail.getARR11());
            		asset.setDB_ARR12(new EsbUtil().decimalPoint(detail.getARR12(), 2).toString());
            		asset.setDB_ARR13(detail.getARR13());
            		asset.setDB_ARR14(detail.getARR14());
            		asset.setDB_ARR15(detail.getARR15());
            		asset.setDB_ARR16(new EsbUtil().decimalPoint(detail.getARR16(), 2).toString());
            		asset.setDB_ARR17(detail.getARR17());
            		asset.setDB_ARR18(new EsbUtil().decimalPoint(detail.getARR18(), 2).toString());
            		asset.setDB_ARR19(detail.getARR19());
            		asset.setDB_ARR20(detail.getARR20());
            		asset.setDB_ARR21(detail.getARR21());
            		asset.setDB_ARR22(new EsbUtil().decimalPoint(detail.getARR22(), 2).toString());
            		asset.setDB_ARR23(detail.getARR23());
            		asset.setDB_ARR24(new EsbUtil().decimalPoint(detail.getARR24(), 2).toString());
            		asset.setDB_ARR25(detail.getARR25());
            		asset.setDB_ARR26(detail.getARR26());
            		asset.setDB_ARR27(detail.getARR27());
            		asset.setDB_ARR28(new EsbUtil().decimalPoint(detail.getARR28(), 2).toString());
            		asset.setDB_ARR29(detail.getARR29());
            		asset.setDB_ARR30(new EsbUtil().decimalPoint(detail.getARR30(), 2).toString());
            		asset.setDB_ARR31(detail.getARR31());
            		asset.setDB_ARR32(detail.getARR32());
            		asset.setDB_ARR33(detail.getARR33());
            		asset.setDB_ARR34(new EsbUtil().decimalPoint(detail.getARR34(), 2).toString());
            		asset.setDB_ARR35(detail.getARR35());
            		asset.setDB_ARR36(new EsbUtil().decimalPoint(detail.getARR36(), 2).toString());
            		asset.setDB_ARR37(detail.getARR37());
            		asset.setDB_ARR38(detail.getARR38());
            	}
            	
            	/**資料為DA DB 一組，到DB就可以加入**/
            	if (StringUtil.isEqual("B", checkTYPE)){
                	list.add(asset);
                }
            }
            
            /**TYPE為B(上一次為A)，在上面已經新增了，所以在這邊清空**/
            if (StringUtil.isEqual(checkTYPE, "B") && StringUtil.isEqual(checkTYPE_old, "A")){
            	asset = new CustAssetNMVIPAVO();
            }
            /**在迴圈的最後紀錄上一次的CHECKTYPE**/
            checkTYPE_old = checkTYPE;
        }  
        return_VO.setResultList(list);
        
        this.sendRtnObject(return_VO);

	}
	
	public void inquire2(Object body, IPrimitiveMap header) throws Exception {
		CRM827InputVO inputVO = (CRM827InputVO) body;
		CRM827OutputVO return_VO = new CRM827OutputVO();
		
		String prod_id = inputVO.getProd_id();
        String htxtid = NMVP3A_CUST_DATA;

        //init util
        ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
        esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        txHead.setDefaultTxHead();
        esbUtilInputVO.setTxHeadVO(txHead);

        //body
        NMVP3AInputVO nmvp3aInputVO = new NMVP3AInputVO();
        nmvp3aInputVO.setFUNCTION("FA");
        nmvp3aInputVO.setNMNO(prod_id);
        esbUtilInputVO.setNmvp3aInputVO(nmvp3aInputVO);

        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
        
        NMVP3AOutputVO nmvp3aOutputVO = new NMVP3AOutputVO();
        List<NMVP3AOutputDetailsVO> results = new ArrayList<NMVP3AOutputDetailsVO>();
        
        for(ESBUtilOutputVO esbUtilOutputVO : vos) {
        	nmvp3aOutputVO = esbUtilOutputVO.getNmvp3aOutputVO();
            List<NMVP3AOutputDetailsVO> details = nmvp3aOutputVO.getDetails();
            details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NMVP3AOutputDetailsVO>() : details;
            for (NMVP3AOutputDetailsVO datas : details) {
            	datas.setT0101(datas.getT0101().trim().toString());
            	datas.setT0102(datas.getT0102().trim().toString());
            	datas.setT0106(new EsbUtil().decimalPoint(datas.getT0106(), 4).toString());
            	datas.setT0107(new EsbUtil().decimalPoint(datas.getT0107(), 2).toString());
            	datas.setT0108(new EsbUtil().decimalPoint(datas.getT0108(), 2).toString());
            	datas.setT0109(new EsbUtil().decimalPoint(datas.getT0109(), 9).toString());
            	datas.setT0112(new EsbUtil().decimalPoint(datas.getT0112(), 2).toString());
            	datas.setT0114(new EsbUtil().decimalPoint(datas.getT0114(), 4).toString());
            	//日期格式
            	if(datas.getT0115().length() == 8){
            		java.util.Date T0115 = new EsbUtil().toAdYearMMdd(datas.getT0115());
            		datas.setT0115(new java.text.SimpleDateFormat("yyyy/MM/dd").format(T0115));
            	}
            	
            	results.add(datas);
            }
        }
        
        return_VO.setResultList_data(results);
		
		this.sendRtnObject(return_VO);
	}
	
	//台外幣活存
	public void inquireCDep(Object body, IPrimitiveMap header) throws Exception{
		CRM827InputVO inputVO = (CRM827InputVO)body;
		CRM827OutputVO outputVO = new CRM827OutputVO();
		String contract_no = inputVO.getContract_no();
 
		 //init util
        ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, NMVP4A);
//        ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, CPYNMVP4);
        esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        esbUtilInputVO.setTxHeadVO(txHead);
        txHead.setDefaultTxHead();
        
        //body
        NMVP4AInputVO txBodyVO = new NMVP4AInputVO();
        txBodyVO.setCONTRACT_NO(contract_no);             //契約編號
        esbUtilInputVO.setNmvp4aInputVO(txBodyVO);
        
        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

        NMVP4AOutputVO nmvp4aOutputVO = new NMVP4AOutputVO();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for(ESBUtilOutputVO esbUtilOutputVO : vos) {
        	nmvp4aOutputVO = esbUtilOutputVO.getNmvp4aOutputVO();
            List<NMVP4AOutputDetailsVO> details = nmvp4aOutputVO.getDetails();
            details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NMVP4AOutputDetailsVO>() : details;
            for (NMVP4AOutputDetailsVO datas : details) {
            	Map<String, Object> map = new HashMap<String, Object>();
				map.put("ACNO", datas.getACNO());
				map.put("CUR", datas.getCUR());
				map.put("VALUE", new EsbUtil().decimalPoint(datas.getVALUE(), 2));
				result.add(map);
            }
        }
        outputVO.setCDepList(result);
		this.sendRtnObject(outputVO);
	}
	
	//台外幣定存
	public void inquireTDep(Object body, IPrimitiveMap header) throws Exception{
		CRM827InputVO inputVO = (CRM827InputVO)body;
		CRM827OutputVO outputVO = new CRM827OutputVO();
		String contract_no = inputVO.getContract_no();
 
		 //init util
        ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, NMVP5A);
        esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        esbUtilInputVO.setTxHeadVO(txHead);
        txHead.setDefaultTxHead();
        
        //body
        NMVP5AInputVO txBodyVO = new NMVP5AInputVO();
        txBodyVO.setCONTRACT_NO(contract_no);             //契約編號
        esbUtilInputVO.setNmvp5aInputVO(txBodyVO);
        
        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

        NMVP5AOutputVO nmvp5aOutputVO = new NMVP5AOutputVO();
        List<Map<String, Object>> TDresult = new ArrayList<>();
        List<Map<String, Object>> FTDresult = new ArrayList<>();
        for(ESBUtilOutputVO esbUtilOutputVO : vos) {
        	nmvp5aOutputVO = esbUtilOutputVO.getNmvp5aOutputVO();
            List<NMVP5AOutputDetailsVO> details = nmvp5aOutputVO.getDetails();
            details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NMVP5AOutputDetailsVO>() : details;
            for (NMVP5AOutputDetailsVO datas : details) {
            	if(datas.getCUR().trim().equals("NTD")){
            		Map<String, Object> map = new HashMap<String, Object>();
    				map.put("ACNO", datas.getACNO());
    				map.put("CD_NBR", datas.getCD_NBR());
    				map.put("CUR", datas.getCUR());
    				map.put("VALUE_DATE", new EsbUtil().toAdYearMMdd(datas.getVALUE_DATE(), false));
    				map.put("DUE_DATE", new EsbUtil().toAdYearMMdd(datas.getDUE_DATE(), false));
    				map.put("INT_TYPE", datas.getINT_TYPE());
    				map.put("INT_RATE", new EsbUtil().decimalPoint(datas.getINT_RATE(), 4));
    				map.put("CD_AMT", new EsbUtil().decimalPoint(datas.getCD_AMT(), 2));
    				map.put("INT_ACNO", datas.getINT_ACNO());
    				map.put("RENEW_TYPE", datas.getRENEW_TYPE());
    				map.put("INT_DRAW_TYPE", datas.getINT_DRAW_TYPE());
    				TDresult.add(map);
            	}else{
            		Map<String, Object> map = new HashMap<String, Object>();
    				map.put("ACNO", datas.getACNO());
    				map.put("CD_NBR", datas.getCD_NBR());
    				map.put("CUR", datas.getCUR());
    				map.put("VALUE_DATE", new EsbUtil().toAdYearMMdd(datas.getVALUE_DATE(), false));
    				map.put("DUE_DATE", new EsbUtil().toAdYearMMdd(datas.getDUE_DATE(), false));
    				map.put("INT_TYPE", datas.getINT_TYPE());
    				map.put("INT_RATE", new EsbUtil().decimalPoint(datas.getINT_RATE(), 4));
    				map.put("CD_AMT", new EsbUtil().decimalPoint(datas.getCD_AMT(), 2));
    				map.put("INT_ACNO", datas.getINT_ACNO());
    				map.put("RENEW_TYPE", datas.getRENEW_TYPE());
    				map.put("INT_DRAW_TYPE", datas.getINT_DRAW_TYPE());
    				FTDresult.add(map);
            	}
            }
        }
        outputVO.setTDepList(TDresult);
        outputVO.setFTDepList(FTDresult);
		
		this.sendRtnObject(outputVO);
	}
	
	//基金
	public void inquireFund(Object body, IPrimitiveMap header) throws DAOException,JBranchException{
		CRM827InputVO inputVO = (CRM827InputVO)body;
		CRM827OutputVO outputVO = new CRM827OutputVO();
		
		dam = this.getDataAccessManager();  
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT DISTINCT F.T62002 AS PROD_ID,  ");
		sql.append("        D.FUND_CNAME AS FUND_CNAME, ");
		sql.append("        B.T03410 AS CRCY_TYPE, "); 
		sql.append("        DECODE(B.T03410, 'NTD', L.AC112, L.AC123) AS INV_AMT, ");
	    sql.append("        ROUND(((L.AC107-L.AC108)*N.NET03)*DECODE(B.T03410, D.CURRENCY_STD_ID, 1, I.BUY_RATE),2) AS REF_MKT_VAL, ");
	    sql.append("        ROUND(((L.AC107-L.AC108)*N.NET03)*DECODE(B.T03410, D.CURRENCY_STD_ID, 1, I.BUY_RATE) - DECODE(B.T03410, 'NTD', L.AC112, L.AC123), 2) AS INV_GAIN_LOSS, ");
	    sql.append("        CASE WHEN DECODE(B.T03410, 'NTD', L.AC112, L.AC123) = 0 THEN 0 ELSE ROUND((NVL(ROUND(((L.AC107-L.AC108)*N.NET03)*DECODE(B.T03410, D.CURRENCY_STD_ID, 1, I.BUY_RATE)), 0) - NVL(DECODE(B.T03410, 'NTD', L.AC112, L.AC123), 0))*100 / NVL(DECODE(B.T03410, 'NTD', L.AC112, L.AC123), 0), 2) END AS RATE_OF_RETURN, ");
	    sql.append("        CASE WHEN DECODE(B.T03410, 'NTD', L.AC112, L.AC123) = 0 THEN 0 ELSE ROUND((NVL(ROUND(((L.AC107-L.AC108)*N.NET03)*DECODE(B.T03410, D.CURRENCY_STD_ID, 1, I.BUY_RATE)), 0) - NVL(DECODE(B.T03410, 'NTD', L.AC112, L.AC123), 0) + NVL(E.CHS03, 0))*100 / NVL(DECODE(B.T03410, 'NTD', L.AC112, L.AC123), 0), 2) END AS RATE_OF_RETURN_CASH, ");
		sql.append("        B.T03404 AS CON_NUM, ");
		sql.append("        G.FUS08 AS FUND_TYPE, ");
		sql.append("        NVL(E.CHS03,0) AS CASH_INT, ");
		sql.append("        H.T34102 AS CERT_NO ");
		sql.append(" FROM TBCRM_NMS034 B ");
		sql.append(" INNER JOIN TBCRM_NMECM C ON B.T03413 = C.LSE001 AND C.LSE011 <> 'A' ");
		sql.append(" INNER JOIN TBCRM_NMS620 F ON B.T03413 = F.T62001 ");
		sql.append(" INNER JOIN TBCRM_NMS341 H ON H.T34103 = B.T03404 AND H.T34101 = B.T03413 ");
		sql.append(" LEFT JOIN TBCRM_NETFIL N ON F.T62002 = N.NET02 AND N.NET01 = (SELECT MAX(NET01) FROM TBCRM_NETFIL WHERE NET02 = F.T62002) ");
        sql.append(" LEFT JOIN TBCRM_AC1FIL_DAY L ON H.T34102 = TRIM(L.AC101) AND L.AC103 = F.T62002 ");
		sql.append(" LEFT JOIN TBCRM_NMS020_DAY A ON B.T03404 = A.TMB01 ");		
		sql.append(" LEFT JOIN TBCRM_NFCHSMP0 E ON TRIM(E.CHS01) = H.T34102 AND E.CHS02 = F.T62002 ");
		sql.append(" LEFT JOIN TBPRD_FUND D ON D.PRD_ID = F.T62002 ");
		sql.append(" LEFT JOIN TBPMS_FUSFIL G ON G.FUS01 || G.FUS02 = F.T62002 ");
		sql.append(" LEFT JOIN TBPMS_IQ053 I ON I.CUR_COD = D.CURRENCY_STD_ID ");
		sql.append(" WHERE B.T03402 = (SELECT MAX(T03402) FROM TBCRM_NMS034 WHERE TRUNC(T03402) < TRUNC(SYSDATE + 1)) ");
		sql.append("   AND I.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053 WHERE CUR_COD = D.CURRENCY_STD_ID AND MTN_DATE <= B.T03449) ");
		sql.append("   AND B.T03401 = '0'   ");
		sql.append("   AND (B.T03403 = '4' OR B.T03403 = 'FUND' OR B.T03403 = 'FTFR') ");
		sql.append("   AND DECODE(B.T03410, 'NTD', L.AC112, L.AC123) != 0 ");
		sql.append("   AND A.TMB02 = :cust_id ");
		sql.append("   AND B.T03404 = :contract_no ");
		sql.append("   ORDER BY F.T62002, H.T34102 ");
				
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setObject("contract_no", inputVO.getContract_no());
		queryCondition.setQueryString(sql.toString());	
		outputVO.setFundList(dam.exeQuery(queryCondition));
	
		this.sendRtnObject(outputVO);
	}
	
	//海外股票ETF
	public void inquireETF(Object body, IPrimitiveMap header) throws DAOException,JBranchException{
		CRM827InputVO inputVO = (CRM827InputVO)body;
		CRM827OutputVO outputVO = new CRM827OutputVO();
		dam = this.getDataAccessManager();   //ETF
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT C.T62002 AS PROD_ID,  ");
		sql.append(" 	    T03410 AS CRCY_TYPE, ");
		sql.append(" 	    T03411 AS INV_AMT, ");
		sql.append(" 	    T03412 AS REF_MKT_VAL, ");
		sql.append(" 	    CASE WHEN NVL(T03411, 0) = 0 THEN 0 ELSE ROUND((NVL(T03412,0)-NVL(T03411,0))/NVL(T03411,0)*100,2) END AS REF_RATE_RETURN, ");
		sql.append(" 		T03404 AS CON_NUM, ");
		sql.append(" 		NVL(T03419,0)+NVL(T03420,0)+NVL(T03421,0) AS STK_AMT, ");
		sql.append(" 		T03448 AS CLOSE_PRICE, ");
		sql.append(" 		NVL(D.ETF_CNAME, E.STOCK_CNAME) AS PROD_NAME ");
		sql.append(" FROM TBCRM_NMS034 B ");
		sql.append(" INNER JOIN TBCRM_NMS620 C ON B.T03413 = C.T62001 ");
		sql.append(" LEFT JOIN TBCRM_NMS020_DAY A ON B.T03404 = A.TMB01 AND A.SNAP_DATE = (SELECT MAX(SNAP_DATE) FROM TBCRM_NMS020_DAY)  ");			
		sql.append(" LEFT JOIN TBPRD_ETF D ON D.PRD_ID = C.T62002 ");
		sql.append(" LEFT JOIN TBPRD_STOCK E ON E.PRD_ID = C.T62002 ");
		sql.append(" WHERE B.T03402 = (SELECT MAX(T03402) FROM TBCRM_NMS034 WHERE TRUNC(T03402) <= TRUNC(SYSDATE)) ");
		sql.append(" AND B.T03401 = '0'  ");
		sql.append(" AND SUBSTR(T03403, 0, 1) = '1'  ");
		sql.append(" AND A.TMB02 = :cust_id ");
		sql.append(" AND B.T03404 = :contract_no ");			
		
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setObject("contract_no", inputVO.getContract_no());
		queryCondition.setQueryString(sql.toString());	
		outputVO.setETFList(dam.exeQuery(queryCondition));
	
		this.sendRtnObject(outputVO);
	}
	
	//海外債券
	public void inquireFbond(Object body, IPrimitiveMap header) throws DAOException,JBranchException{
		CRM827InputVO inputVO = (CRM827InputVO)body;
		CRM827OutputVO outputVO = new CRM827OutputVO();
		
		dam = this.getDataAccessManager();  
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT A.BDAF1 AS PROD_ID, ");
		sql.append("        D.BDA04 AS PROD_NAME, ");
		sql.append("        D.BDA28 AS CRCY_TYPE, ");
		sql.append("        NVL(A.BDAF8, 0) * NVL(D.BDA13, 0) AS DENOM, ");	//庫存面額
		sql.append("        B.T34105 AS TRUST_AMT, ");
		sql.append("        F.B7EFE AS REF_REDEEM, ");
		sql.append("        G.SUM_BDG0O AS CASH_DIV,");
		sql.append("        ROUND((NVL(A.BDAF8,0) * NVL(D.BDA13,0) * NVL(F.B7056,0))/100,2) AS PRE_INT, ");
		sql.append("        ROUND(NVL(E.BDADP,0), 2) AS PAID_SERV_FEE, ");
		sql.append("        CASE WHEN NVL(B.T34105, 0) = 0 THEN 0 ");	//((庫存面額*參考報價+累積現金配息+應收前手息-已付前手息/(信託本金)) - 1) * 100
		sql.append("				ELSE ROUND(((((NVL(A.BDAF8,0) * NVL(D.BDA13,0)) * (NVL(F.B7EFE,0)/100) + NVL(G.SUM_BDG0O,0) + ");
		sql.append("								((NVL(A.BDAF8,0) * NVL(D.BDA13,0) * NVL(F.B7056,0))/100) - NVL(E.BDADP,0))/ NVL(B.T34105,0)) - 1) * 100, 2) END AS RATE_OF_RETURN,");
		sql.append("        A.BDAFH AS CERT_NO ");
		sql.append(" FROM TBCRM_BDS160_DAY A");
		sql.append(" INNER JOIN TBCRM_NMS341 B ON B.T34102 = A.BDAFH ");
		sql.append(" INNER JOIN TBCRM_NMS020_DAY C ON B.T34103 = C.TMB01 AND C.SNAP_DATE = (SELECT MAX(SNAP_DATE) FROM TBCRM_NMS020_DAY) ");
		sql.append(" INNER JOIN TBPRD_BDS010 D ON D.BDA01 = A.BDAF1 AND D.BDA02 != 'SN' ");
		sql.append(" LEFT JOIN TBPMS_BDS140_DAY E ON E.BDAD6 = 'B' AND E.BDAD9 = A.BDAFH AND E.SNAP_DATE = (SELECT MAX(SNAP_DATE) FROM TBPMS_BDS140_DAY) ");
		sql.append(" LEFT JOIN (SELECT DISTINCT B7EF1, ");
		sql.append("                   TO_NUMBER(CONCAT(CONCAT(NVL(SUBSTR(TO_NUMBER(B7EFE),0,LENGTH(TO_NUMBER(B7EFE))-9),0),'.'),SUBSTR(B7EFE,-9,9))) AS B7EFE, ");
		sql.append("                   TO_NUMBER(CONCAT(CONCAT(NVL(SUBSTR(TO_NUMBER(B7056),0,LENGTH(TO_NUMBER(B7056))-9),0),'.'),SUBSTR(B7056,-9,9))) AS B7056 ");
		sql.append("            FROM TBPMS_BDS057_SG ");
		sql.append("            WHERE (B7EF1, B7EF3) IN (SELECT B7EF1, MAX(B7EF3)  ");
		sql.append(" 									FROM TBPMS_BDS057_SG  ");
		sql.append(" 									GROUP BY B7EF1) ");
		sql.append(" 		  ) F ON F.B7EF1 = A.BDAF1 ");
		sql.append(" LEFT JOIN (SELECT SUM(NVL(BDG0O,0)) as SUM_BDG0O, BDG01, BDG0K FROM TBPMS_BDS070 ");
		sql.append("             GROUP BY BDG01, BDG0K) G ON G.BDG01 = A.BDAF1 AND G.BDG0K = A.BDAFH ");
		sql.append(" WHERE A.SNAP_DATE = (SELECT MAX(SNAP_DATE) FROM TBCRM_BDS160_DAY) ");
		sql.append("   AND C.TMB02 = :cust_id ");
		sql.append("   AND B.T34103 = :contract_no ");
		sql.append("   AND NVL(A.BDAF8, 0) <> 0 AND NVL(D.BDA13, 0) <> 0 "); //庫存面額為0不用顯示

		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setObject("contract_no", inputVO.getContract_no());
		queryCondition.setQueryString(sql.toString());	
		outputVO.setFbondList(dam.exeQuery(queryCondition));
	
		this.sendRtnObject(outputVO);
	}
	
	//SI
	public void inquireSI(Object body, IPrimitiveMap header) throws DAOException,JBranchException{
		CRM827InputVO inputVO = (CRM827InputVO)body;
		CRM827OutputVO outputVO = new CRM827OutputVO();
		
		dam = this.getDataAccessManager();  
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT C.PRD_ID AS PROD_ID,");
		sql.append("        C.SI_CNAME AS PROD_NAME,");
		sql.append("        B.T03410 AS CRCY_TYPE,");
		sql.append("        B.T03411 AS INV_AMT,");	//庫存面額
		sql.append("        E.TRANS_DATE AS START_DATE,");
		sql.append("        E.VALUE_DATE AS DUE_DATE,");
		sql.append("        G.PLADTE AS LATEST_PAYDAY,");
		sql.append("        G.IVAMT2 AS DIV_RATE,");
		sql.append("        ROUND(NVL(F.SDAMT3,0),2) AS REDEEM_PRICE,");
		sql.append("        NVL(G.IVAMT2,0) + ROUND(NVL(F.SDAMT3,0),2)  AS INT_PRICE,");
		sql.append("        NVL(G.IVAMT2,0) + ROUND(NVL(F.SDAMT3,0),2) - 100 AS RATE_OF_RETURN,");
		sql.append("        B.T03412 AS REF_VAL ");
		sql.append(" FROM TBCRM_NMS034 B ");
		sql.append(" INNER JOIN TBCRM_NMECM H ON B.T03413 = H.LSE001 AND H.LSE011 = 'A' ");
		sql.append(" INNER JOIN TBCRM_NMS020_DAY A ON B.T03404 = A.TMB01 AND A.SNAP_DATE = (SELECT MAX(SNAP_DATE) FROM TBCRM_NMS020_DAY)");
		sql.append(" INNER JOIN TBCRM_NMS180 D ON B.T03404 = D.T18002");
		sql.append(" INNER JOIN TBPRD_SDINVMP2 I ON D.T18003 = I.REG207");
		sql.append(" LEFT JOIN TBPRD_SI C ON C.PRD_ID = I.REG201");
		sql.append(" LEFT JOIN TBPRD_SIINFO E ON E.PRD_ID = C.PRD_ID ");
		sql.append(" LEFT JOIN TBPRD_SIPRICE F ON F.SDPRD = C.PRD_ID AND F.SDDTE = (SELECT MAX(SDDTE) FROM TBPRD_SIPRICE WHERE SDPRD = C.PRD_ID) ");
		sql.append(" LEFT JOIN (SELECT PLAID, MAX(PLADTE) PLADTE, SDPRD, ");
		sql.append(" 					ROUND(SUM(CASE WHEN IVAMT2 = 0 THEN 0 ELSE (PLALAM/IVAMT2)*100 END), 7) AS IVAMT2 ");					
		sql.append("                 FROM (SELECT B.PLAID, A.SDPRD,SUM(A.IVAMT2) IVAMT2,");
		sql.append("                              B.PLADTE,B.PLALAM ");
		sql.append("                         FROM (SELECT T1.IVRDTE, T1.SDPRD, SUM(T1.IVAMT2) IVAMT2 ");			
		sql.append("                                 FROM (SELECT CASE IVSTS1 WHEN 'S3' THEN TO_CHAR(SYSDATE+1, 'YYYYMMDD') ");						
		sql.append("                                                          WHEN 'S4' THEN TO_CHAR(IVDTE1, 'YYYYMMDD') ");					
		sql.append("                                                          WHEN 'S5' THEN TO_CHAR(IVDTE4, 'YYYYMMDD') ");					
		sql.append("                                                          WHEN 'S9' THEN TO_CHAR(IVDTE4, 'YYYYMMDD') END IVRDTE, ");						
		sql.append("                                              SDPRD, IVAMT2	");
		sql.append("                                         FROM TBPMS_SDINVMP0_DAY ");
		sql.append("                                        WHERE SNAP_DATE = (SELECT MAX(SNAP_DATE)	FROM TBPMS_SDINVMP0_DAY) ");					
		sql.append("                                          AND IVSTS1 IN ('S3', 'S4', 'S5', 'S9')) T1 ");
		sql.append("                             GROUP BY T1.IVRDTE, T1.SDPRD) A, ");
		sql.append("                              (SELECT F.PLAID, F.PLAPRD,SUM(F.PLALAM) AS PLALAM, ");
		sql.append("                                      TO_CHAR(F.PLADTE,'YYYYMMDD') AS PLADTE ");
		sql.append("                                 FROM TBPRD_SIDIVIDEND F ");
		sql.append("                                WHERE IMPORT_FLAG = 'F' ");			
		sql.append("                                  AND (F.PLATYP = '1' OR F.PLATYP = '2' ");
		sql.append("                                   OR (F.PLATYP = 'F' AND EXISTS (SELECT G.IVDTE4 FROM TBPMS_SDINVMP0_DAY G WHERE F.PLAPRD = G.SDPRD AND F.PLADTE = G.IVDTE4))) ");					
		sql.append("                               GROUP BY F.PLAID, F.PLAPRD,TO_CHAR(F.PLADTE,'YYYYMMDD')) B ");
		sql.append("                        WHERE A.SDPRD = B.PLAPRD ");
		sql.append("                          AND A.IVRDTE >= B.PLADTE ");
		sql.append("                        GROUP BY B.PLAID,A.SDPRD,B.PLADTE,B.PLALAM) ");
		sql.append("             GROUP BY PLAID, SDPRD) G ON G.SDPRD = C.PRD_ID AND G.PLAID = A.TMB02 ");			
		sql.append(" WHERE B.T03402 = (SELECT MAX(T03402) FROM TBCRM_NMS034 WHERE TRUNC(T03402) <= TRUNC(SYSDATE)) ");
		sql.append("   AND B.T03401 = '0' ");			
		sql.append("   AND (B.T03403 = '4' OR B.T03403 = 'FUND' OR B.T03403 = 'FTFR') ");
		sql.append("   AND A.TMB02 = :cust_id ");
		sql.append("   AND B.T03404 = :contract_no ");
		sql.append("   AND NVL(B.T03411, 0) <> 0 "); //庫存面額為0不用顯示
	
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setObject("contract_no", inputVO.getContract_no());
		queryCondition.setQueryString(sql.toString());	
		outputVO.setSIList(dam.exeQuery(queryCondition));
	
		this.sendRtnObject(outputVO);
	}
	
	//SN
	public void inquireSN(Object body, IPrimitiveMap header) throws DAOException,JBranchException{
		CRM827InputVO inputVO = (CRM827InputVO)body;
		CRM827OutputVO outputVO = new CRM827OutputVO();
		
		dam = this.getDataAccessManager();  
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT DISTINCT A.BDAFH AS CERT_NO,");
        sql.append("        A.BDAF1 AS PROD_ID,");
        sql.append("        B.BDA04 AS PROD_NAME,");
        sql.append("        B.BDA28 AS CUR_TYPE,");
        sql.append("        NVL(A.BDAF8,0) * NVL(B.BDA13,0) AS DENOM,"); //庫存面額
        sql.append("        C.BDAS2 AS TRAN_DATE,");
        sql.append("        B.BDA08 AS ISSUE_DATE,");
        sql.append("        E.BDG04 AS LATEST_INT_DATE,");
        sql.append("        CASE WHEN (NVL(A.BDAF8,0) = 0 OR NVL(B.BDA13,0) = 0) THEN 0 ELSE ROUND((NVL(E.BDG0O,0) /(NVL(A.BDAF8,0) * NVL(B.BDA13,0)) *100),2) END AS LATEST_INT_RATE,");
        sql.append("        H.SUM_BDG0O AS ACC_CASH_DIV,");
        sql.append("        CASE WHEN (NVL(A.BDAF8,0) = 0 OR NVL(B.BDA13,0) = 0) THEN 0 ELSE ROUND((NVL(H.SUM_BDG0O,0) /(NVL(A.BDAF8,0) * NVL(B.BDA13,0)) * 100),2) END AS ACC_INT_RATE,");
        sql.append("        D.BDEB4 AS LATEST_PRICE,");
        sql.append("        CASE WHEN NVL(F.T34105, 0) - 1 = 0 THEN 0 ELSE ROUND(((NVL(A.BDAF8,0) * NVL(B.BDA13,0)) * (NVL(D.BDEB4,0)/100) + NVL(H.SUM_BDG0O,0) -(NVL(A.BDAF8,0) * NVL(B.BDA13,0)) ) / (NVL(F.T34105,0)/100), 4) END AS RATE_OF_RETURN,");
        sql.append("                F.T34105 AS TRUST_AMT");
        sql.append(" FROM TBCRM_BDS160_DAY A");
        sql.append(" INNER JOIN TBCRM_NMS341 F ON F.T34102 = A.BDAFH ");
        sql.append(" INNER JOIN TBCRM_NMS020_DAY G ON F.T34103 = G.TMB01 AND G.SNAP_DATE = (SELECT MAX(SNAP_DATE) FROM TBCRM_NMS020_DAY) ");
        sql.append(" INNER JOIN TBPRD_BDS010 B ON B.BDA01 = A.BDAF1 AND B.BDA02 = 'SN' ");
        sql.append(" LEFT JOIN TBPRD_BDS019 C ON C.BDAS1 = A.BDAF1 ");
        sql.append(" LEFT JOIN TBCRM_BDS052 D ON D.BDEB2 = A.BDAF1 ");
        sql.append(" LEFT JOIN (SELECT SNAP_DATE, BDG04, BDG0O, BDG01, BDG0K FROM TBPMS_BDS070 ");
        sql.append("                        WHERE SNAP_DATE = (SELECT MAX(SNAP_DATE) FROM TBPMS_BDS070)) E ");
        sql.append("                                ON E.BDG01 = A.BDAF1 AND E.BDG0K = A.BDAFH ");
        sql.append("                                        AND E.BDG04 = (SELECT MAX(BDG04) FROM TBPMS_BDS070 WHERE SNAP_DATE = E.SNAP_DATE AND BDG01 = E.BDG01 AND BDG0K = E.BDG0K) ");
        sql.append(" LEFT JOIN (SELECT SUM(NVL(BDG0O,0)) as SUM_BDG0O, BDG01, BDG0K FROM TBPMS_BDS070 ");
        sql.append("             GROUP BY BDG01, BDG0K) H ON H.BDG01 = A.BDAF1 AND H.BDG0K = A.BDAFH ");
        sql.append(" WHERE A.SNAP_DATE = (SELECT MAX(SNAP_DATE) FROM TBCRM_BDS160_DAY) ");
        sql.append("   AND F.SNAP_DATE = (SELECT MAX(SNAP_DATE) FROM TBCRM_NMS341) ");
        sql.append("   AND (D.SNAP_DATE, D.BDEB3) = (SELECT MAX(SNAP_DATE), MAX(BDEB3) FROM TBCRM_BDS052 WHERE BDEB2 = A.BDAF1) ");
        sql.append("   AND G.TMB02 = :cust_id ");
        sql.append("   AND F.T34103 = :contract_no ");
        sql.append("   AND NVL(A.BDAF8, 0) <> 0 AND NVL(B.BDA13,0) <> 0 "); //庫存面額為0不用顯示
		
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setObject("contract_no", inputVO.getContract_no());
		queryCondition.setQueryString(sql.toString());	
		outputVO.setSNList(dam.exeQuery(queryCondition));
	
		this.sendRtnObject(outputVO);
	}
	
	/***
	 * 取得信託關係人資料
	 * @param body
	 * @param header
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public void inquireRelatives(Object body, IPrimitiveMap header) throws DAOException,JBranchException {
		CRM827InputVO inputVO = (CRM827InputVO)body;
		CRM827OutputVO outputVO = new CRM827OutputVO();
			
		dam = this.getDataAccessManager();  
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		StringBuffer sql1 = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();
		
		sql.append(" SELECT DISTINCT A.TMF02L, ");							//指示類型
        sql.append("        A.TMF03L, ");									//約定事項
        sql.append("		B.S1YO04, ");									//信託關係人類別
//        sql.append("        B.S1YO05, ");									//信託關係人證號
        sql.append("        B.S1YO051, ");									//信託關係人姓名
        sql.append("        (E.S1YO06 || ' ' || F.PARAM_NAME) AS S1YO06 ");	//指示方式
        sql.append(" FROM TBCRM_NMS060L A ");
        sql.append(" LEFT JOIN (SELECT S1YO01, S1YO02, S1YO03, S1YO04, LISTAGG(S1YO051, '、') WITHIN GROUP (ORDER BY S1YO01, S1YO02, S1YO03, S1YO04) as S1YO051 ");
        sql.append(" 			FROM TBCRM_NMS1YO A ");
        sql.append(" 			LEFT JOIN TBCRM_NMS050 B ON B.T05001 = A.S1YO01 and B.T05002 = A.S1YO05 ");
        sql.append("   			WHERE S1YO06 IS NOT NULL ");
        sql.append("   				AND (A.S1YO04 != '3' OR B.T05027 IS NULL OR B.T05028 IS NULL ");
        sql.append("				 	 OR (A.S1YO04 = '3' AND TRUNC(B.T05027) <= TRUNC(SYSDATE) AND TRUNC(SYSDATE) <= TRUNC(B.T05028))) "); //監察人(S1YO04 = '3')需檢查是否在任職期間內
        sql.append(" 			GROUP BY S1YO01, S1YO02, S1YO03, S1YO04 ORDER BY S1YO01, S1YO02, S1YO03, S1YO04 ");
        sql.append("		   ) B ON B.S1YO01 = A.TMF01L AND B.S1YO02 = A.TMF02L AND B.S1YO03 = A.TMF03L ");
        sql.append(" LEFT JOIN TBCRM_NMS1YO E ON E.S1YO01 = B.S1YO01 AND E.S1YO02 = B.S1YO02 AND E.S1YO03 = B.S1YO03 AND E.S1YO04 = B.S1YO04 AND E.S1YO06 IS NOT NULL ");
        sql.append(" LEFT JOIN TBCRM_NMS050 D ON D.T05001 = A.TMF01L AND D.T05002 = E.S1YO05 ");
        sql.append(" LEFT JOIN TBSYSPARAMETER F ON F.PARAM_TYPE = 'CRM.MON_REL_INS_WAY' AND F.PARAM_CODE = E.S1YO06 "); //指示方式中文名稱
        sql.append(" WHERE A.SNAP_DATE = (SELECT MAX(SNAP_DATE) FROM TBCRM_NMS060L) ");
        sql.append("   AND A.TMF01L = :contract_no ");
        		
        //信託財產運用權人
        sql1.append(sql);
        sql1.append(" AND A.TMF02L = '5' ");
        sql1.append("   ORDER BY A.TMF02L, A.TMF03L ");
		queryCondition.setObject("contract_no", inputVO.getContract_no());
		queryCondition.setQueryString(sql1.toString());	
		outputVO.setResultList1(dam.exeQuery(queryCondition));
	
		//其他運用指示有權人
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql2.append(sql);
	    sql2.append(" AND A.TMF02L IN ('1', '2', '3', '4', '6') ");
	    sql2.append(" AND E.S1YO06 IS NOT NULL ");	//關係人資料有”指示方式”時才顯示 
	    sql2.append("   ORDER BY A.TMF02L, A.TMF03L ");
		queryCondition.setObject("contract_no", inputVO.getContract_no());
		queryCondition.setQueryString(sql2.toString());	
		outputVO.setResultList2(dam.exeQuery(queryCondition));
		
		//授權第三人
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append(" SELECT S1YQ04 FROM TBCRM_NMS1YQ WHERE S1YQ01 = :contract_no ");
		sql.append(" AND TRUNC(S1YQ02) <= TRUNC(SYSDATE) AND TRUNC(SYSDATE) <= TRUNC(S1YQ03) ");
		queryCondition.setObject("contract_no", inputVO.getContract_no());
		queryCondition.setQueryString(sql.toString());	
		outputVO.setResultList3(dam.exeQuery(queryCondition));
		
		//限制不可申購
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append(" SELECT S1YR04, ");		//限制不可申購_發行區域
		sql.append("		S1YR05, ");		//限制不可申購_商品類型
		sql.append("		S1YR06 ");		//限制不可申購_基金除權息
		sql.append(" FROM TBCRM_NMS1YR WHERE S1YR01 = :contract_no ");
		sql.append(" AND TRUNC(S1YR02) <= TRUNC(SYSDATE) AND TRUNC(SYSDATE) <= TRUNC(S1YR03) ");
		queryCondition.setObject("contract_no", inputVO.getContract_no());
		queryCondition.setQueryString(sql.toString());	
		outputVO.setResultList_data(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
				
}