package com.systex.jbranch.fubon.commons.cbs.service;

import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.vo._000400_032041.CBS000400InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._085081_085105.CBS085105OutputDetailsVO;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang.StringUtils.defaultString;

/**
 * 負責各個數字電文之間的邏輯轉換
 */
@Service
public class MediatorService {
    @Autowired
    private CBSService cbsService;

    /**
     * 是否為定存
     **/
    public boolean isTimedDeposit(CBS085105OutputDetailsVO cbsDtl) {
        return "DEP".equals(cbsDtl.getWA_X_SYS()) && "T".equals(cbsDtl.getWA_X_PRODUCT_IND());
    }

    /**
     * 是否為台幣定存
     **/
    public boolean isTimedDepositTW(CBS085105OutputDetailsVO cbsDtl) {
        return isTimedDeposit(cbsDtl) && "TWD".equals(cbsDtl.getWA_X_CURR());
    }

    /**
     * 是否為外幣定存
     **/
    public boolean isTimedDepositFC(CBS085105OutputDetailsVO cbsDtl) {
        return isTimedDeposit(cbsDtl) &&
                !"TWD".equals(cbsDtl.getWA_X_CURR()) &&
                !"XXX".equals(cbsDtl.getWA_X_CURR()) &&
                exceptAcct(cbsDtl.getWA_X_ACCTNO());
    }

    /**
     * 是否為台幣支存
     **/
    public boolean isCheckTW(String curr, String product) {
    	CBS085105OutputDetailsVO checkVO = new CBS085105OutputDetailsVO();
		if(StringUtils.isNotBlank(curr)){
			checkVO.setWA_X_CURR(curr);
		}
		if(StringUtils.isNotBlank(product)){
			checkVO.setWA_X_PRODUCT_IND(product);
		}
        return isCheckTW(checkVO);
    }
    public boolean isCheckTW(CBS085105OutputDetailsVO cbsDtl) {
        return "Q".equals(cbsDtl.getWA_X_PRODUCT_IND()) &&
                "TWD".equals(cbsDtl.getWA_X_CURR());
    }

    /**
     * 是否為台幣活存
     **/
    public boolean isCurrentDepositTW(CBS085105OutputDetailsVO cbsDtl) {
        return isCurrentDeposit(cbsDtl) &&
                "TWD".equals(cbsDtl.getWA_X_CURR());
    }

    /**
     * 是否為活存
     */
    public boolean isCurrentDeposit(String sys, String product) {
    	
    	CBS085105OutputDetailsVO checkVO = new CBS085105OutputDetailsVO();
		if(StringUtils.isNotBlank(sys)){
			checkVO.setWA_X_SYS(sys);
		}
		if(StringUtils.isNotBlank(product)){
			checkVO.setWA_X_PRODUCT_IND(product);
		}
        return isCurrentDeposit(checkVO);
    }
    public boolean isCurrentDeposit(CBS085105OutputDetailsVO cbsDtl) {
        return "DEP".equals(cbsDtl.getWA_X_SYS()) &&
                "S".equals(cbsDtl.getWA_X_PRODUCT_IND());
    }

    /**
     * 是否為外幣活存
     **/
    public boolean isCurrentDepositFC(CBS085105OutputDetailsVO acctDetail) {
        return acctDetail.getWA_X_SYS().equals("DEP") &
                acctDetail.getWA_X_PRODUCT_IND().equals("S") &&
                !acctDetail.getWA_X_CURR().equals("TWD") &&
                !acctDetail.getWA_X_CURR().equals("XXX");
    }

    public boolean exceptAcct(String acct) {
        acct = cbsService.checkAcctLength(acct);
        if (acct.substring(0, 1).equals("0")) {
            if (acct.substring(5, 8).equals("677")) {
                return false;
            }

        } else if (acct.substring(0, 1).equals("8")) {
            if (acct.substring(0, 4).equals("8345") || acct.substring(0, 4).equals("8395")) {
            	return false;
            }
        } else {
            return true;
        }
        return true;
    }

    /** 根據 085105 Detail 的資料取得 CBS000400InputVO**/
    public CBS000400InputVO getCbs000400InputVO(CBS085105OutputDetailsVO acctDetail) {
        CBS000400InputVO inputVO = new CBS000400InputVO();
        inputVO.setAccntNumber1(acctDetail.getWA_X_ACCTNO());

        String type = defaultString(acctDetail.getWA_X_PRODUCT_IND());
        // 台外幣定存 T => WA_X_SUB_INFO。
        inputVO.setSubAccountInfo(type.equals("T") ? acctDetail.getWA_X_SUB_INFO() :
                // 台外幣活存 S | 支存 Q => WA_X_CURR。
                type.matches("S|Q") ? acctDetail.getWA_X_CURR() : "");
        return inputVO;
    }

    /** 是否為貸款 **/
    public boolean isLoan(CBS085105OutputDetailsVO acctDetail) {
        return "LON".equals(acctDetail.getWA_X_SYS());
    }
}
