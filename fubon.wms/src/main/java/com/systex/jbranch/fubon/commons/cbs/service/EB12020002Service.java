package com.systex.jbranch.fubon.commons.cbs.service;

import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.dao._000400_032041DAO;
import com.systex.jbranch.fubon.commons.cbs.dao._000481_000482DAO;
import com.systex.jbranch.fubon.commons.cbs.dao._085081_085105DAO;
import com.systex.jbranch.fubon.commons.cbs.vo._000400_032041.CBS032041OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._000481_000482.CBS000482OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._085081_085105.CBS085105OutputDetailsVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb12020002.EB12020002OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb12020002.EB12020002OutputVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.*;


/**
 * 此服務將代替原本的 EB12020002 ESB 電文
 * （Note: 原本的 EB12020002 ESB 電文改走 CBS 路線）
 */
@Service
public class EB12020002Service {
    @Autowired
    private _085081_085105DAO _085081_085105dao;
    @Autowired
    private _000400_032041DAO _000400_032041dao;
    @Autowired
    private _000481_000482DAO _000481_000482dao;
    @Autowired
    private CBSService cbsService;
    @Autowired
    private MediatorService mediatorService;

    /**
     * 依照 curType 決定為台幣或外幣定存
     * 1: 台幣
     * 2: 外幣
     **/
    public List<EB12020002OutputVO> search(String custId, String curType) throws Exception {
        List result = new ArrayList();
        // 呼叫 _085081_085105 取得客戶的帳戶資料
        for (CBSUtilOutputVO acctInfo : _085081_085105dao.search(custId, cbsService.getCBSIDCode(custId))) {
            /** Detail **/
            List ebDetails = new ArrayList();
            for (CBS085105OutputDetailsVO acctDetail : acctInfo.getCbs085105OutputVO().getDetails()) {
                if(isBlank(acctDetail.getWA_X_ACCTNO())) continue;
                // 判斷是否符合定存帳號
                if (!fit(acctDetail, curType)) continue;

                // 藉由 085081_085105 在去打 000400_032041、000481_000482 數字電文已取得其他資料
                // 並將這些數字電文的資料組合成舊電文 EB12020002Output
                EB12020002OutputDetailsVO ebDetailVO = new EB12020002OutputDetailsVO();
                _085081_085105Transfer(acctDetail, ebDetailVO);
                _000400_032041Transfer(acctDetail, ebDetailVO);
                _000481_000482Transfer(acctDetail, ebDetailVO);
                ebDetails.add(ebDetailVO);
            }
            EB12020002OutputVO esbOutput = new EB12020002OutputVO();
            esbOutput.setDetails(ebDetails);

            result.add(esbOutput);
        }
        return result;
    }

    /**
     * 綜定存帳號才需上送，TODO 待麗文給予
     * 規格書轉換  _000481_000482 to EB12020002 detail
     **/
    private void _000481_000482Transfer(CBS085105OutputDetailsVO acctDetail, EB12020002OutputDetailsVO ebDetailVO) throws Exception {
    	if(!conformCTAcct(acctDetail.getWA_X_ACCTNO())) return;
        List<CBSUtilOutputVO> result = _000481_000482dao.search(acctDetail.getWA_X_ACCTNO());
        if (isEmpty(result)) return;

        CBS000482OutputVO cbs = result.get(0).getCbs000482OutputVO();
        ebDetailVO.setCRLN_UTL(isNotBlank(cbs.getSigned32()) ? substring(cbs.getSigned32(), 17) +
                substring(cbs.getSigned32(), 0, 17) : "0");
        ebDetailVO.setLOAN_BAL(isNotBlank(cbs.getSigned33()) ? substring(cbs.getSigned33(), 17) +
                substring(cbs.getSigned33(), 0, 17) : "0");
    }

    /**
     * 規格書轉換  _000400_032041 to EB12020002 detail
     * #1880_台外幣定存存款期間邏輯調整  SamTu 2024.01.29
     **/
    private void _000400_032041Transfer(CBS085105OutputDetailsVO acctDetail, EB12020002OutputDetailsVO ebDetailVO) throws Exception {
        List<CBSUtilOutputVO> result = _000400_032041dao.search(mediatorService.getCbs000400InputVO(acctDetail));
        if (result.isEmpty()) return;

        CBS032041OutputVO cbs = result.get(0).getCbs032041OutputVO();
        // 若7個0就呈現空白
        ebDetailVO.setSLIP_NO("0000000".equals(trim(cbs.getCertNo1().substring(0,7))) ? " " : cbs.getCertNo1().substring(0, 7));

        String termBasis1 = trim(cbs.getTermBasis1());
        if ("M".equals(termBasis1)) {
            ebDetailVO.setDPR_MM(substring(cbs.getTermDay1(), 3, 5));
            ebDetailVO.setDPR_DDD(null);
        } else if ("D".equals(termBasis1)) {
            ebDetailVO.setDPR_MM(null);
            ebDetailVO.setDPR_DDD(substring(cbs.getTermDay1(), 3, 5));
        } else {
            ebDetailVO.setDPR_MM(null);
            ebDetailVO.setDPR_DDD(null);
        }
        ebDetailVO.setACNO_SA(cbsService.checkAcctLength(cbs.getIntTrfAcct1()));
        ebDetailVO.setAUTO_TR_TYP(identifyAUTO_TR_TYP(trim(cbs.getTermRolloverType1()), trim(cbs.getIntPayMethod1()), trim(cbs.getCertNo1().substring(0, 7))));
        ebDetailVO.setAUTO_TR_LEFT(identifyAUTO_TR_LEFT(trim(cbs.getTermRolloverType1()), trim(cbs.getMaxRollCont()), cbs.getTotRollCont()));
        ebDetailVO.setINT_PAY_TYP(identifuINT_PAY_TYP(trim(cbs.getIntPayMethod1()), trim(cbs.getIntFreq())));
        ebDetailVO.setLST_INT_DATE(cbs.getLasFinDate1());
    }

    /**
     * 規格書轉換  _085081_085105 detail to  EB12020002 detail
     **/
    private void _085081_085105Transfer(CBS085105OutputDetailsVO acctDetail, EB12020002OutputDetailsVO ebDetailVO) {
        ebDetailVO.setACNO(cbsService.checkAcctLength(acctDetail.getWA_X_ACCTNO()));
        ebDetailVO.setCUR(acctDetail.getWA_X_CURR());

        if (length(acctDetail.getWA_X_CURR_BALANCE()) > 15)
            ebDetailVO.setOPN_DPR_AMT(substring(acctDetail.getWA_X_CURR_BALANCE(), 17) +
                    substring(acctDetail.getWA_X_CURR_BALANCE(), 0, 17));

        ebDetailVO.setINT_TYP(identifyINT_TYP(substring(acctDetail.getOthers(), 26, 27)));
        ebDetailVO.setBRA(cbsService.checkBraLength(acctDetail.getWA_X_BRANCH_NO()));
        ebDetailVO.setBK_VALUE(substring(acctDetail.getOthers(), 0, 8));
        ebDetailVO.setDUE_DTE(substring(acctDetail.getOthers(), 8, 16));
        ebDetailVO.setINT_RATE(substring(acctDetail.getOthers(), 25, 26)
                + substring(acctDetail.getOthers(), 16, 25));
    }

    /**
     * 判斷是不是存款 && 定存 && 台幣/外幣
     */
    private boolean fit(CBS085105OutputDetailsVO detail, String func) {
        if ("1".equals(func)) return mediatorService.isTimedDepositTW(detail);
        if ("2".equals(func)) return mediatorService.isTimedDepositFC(detail);
        return false;
    }

    private String identifyAUTO_TR_TYP(String TRT, String IPM, String CNO) {
        if (TRT.equals("N") && CNO.equals("0000000")) {
            return "4";
        } else if (TRT.equals("N") && !CNO.equals("0000000")) {
            return "1";
        } else if (TRT.equals("R") && IPM.equals("R")) {
            return "3";
        } else if (TRT.equals("R") && !IPM.equals("R")) {
            return "2";
        } else {
            return "X";
        }

    }

    //排除組合式商品
    public String calAUTO_TR_LEFT(String max, String min) {
        try {
            int a = Integer.parseInt(max);
            int b = Integer.parseInt(min);
            int c = a - b;
            return String.valueOf(c);
        } catch (Exception e) {
            return "XXX";
        }
    }

    private String identifyAUTO_TR_LEFT(String TRT, String MRC, String TRC) {
        if (TRT.equals("R") && MRC.equals("000")) {
            return "無限次";
        } else if (TRT.equals("R") && !MRC.equals("000")) {
            return calAUTO_TR_LEFT(MRC, TRC);
        } else {
            return "";
        }
    }

    private String identifuINT_PAY_TYP(String IPM, String IFq) {
        if (IPM.equals("I")) {
            if (IFq.equals("1A")) {
                return "1";
            } else if (IFq.equals("YA")) {
                return "2";
            } else if (IFq.equals("M")) {
                return "3";
            }
        } else if (IPM.equals("T")) {
            if (IFq.equals("1A")) {
                return "4";
            } else if (IFq.equals("YA")) {
                return "5";
            } else if (IFq.equals("M")) {
                return "6";
            }
        } else if (IPM.equals("R")) {
            if (IFq.equals("1A")) {
                return "7";
            } else if (IFq.equals("YA")) {
                return "8";
            } else if (IFq.equals("M")) {
                return "9";
            }
        } else {
            return "10";
        }
        return "XX";
    }

    /** 存款利率別 F-固定 L-機動 N-議價固定 M-逾期活期　**/
    private String identifyINT_TYP(String mark) {
        switch (defaultString(mark)) {
            case "F": return "1";
            case "L": return "2";
            case "N": return "3";
            case "M": return "4";
            default: return " ";
        }
    }
    /**
     * 符合綜定的邏輯判斷
     * @param acct
     * @return
     */
    private boolean conformCTAcct(String acct){
    	String[] identifyOldMasterAcctThreeByte = {"126","626","236","636","256","656",};
	
		acct = cbsService.checkAcctLength(acct);
	if (acct.substring(0, 1).equals("0")){
		if(Arrays.asList(identifyOldMasterAcctThreeByte).contains(acct.substring(5, 8))){
			return true;
		}		
	}else if (acct.substring(0, 1).equals("8")){
		if(acct.substring(0, 4).equals("8401")){
			return true;
		}
		if(Integer.parseInt(acct.substring(0, 5))>=82500 && Integer.parseInt(acct.substring(0, 5))<82800){
			return true;
		}
		if(Integer.parseInt(acct.substring(0, 5))>=83500 && Integer.parseInt(acct.substring(0, 5))<83800){
			return true;
		}
		if(Integer.parseInt(acct.substring(0, 5))>=83960 && Integer.parseInt(acct.substring(0, 5))<84000){
			return true;
		}
	}else {
		return false;
	}
	return false;	
    }
}

