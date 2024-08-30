package com.systex.jbranch.fubon.commons.cbs.vo._060503_060503;

import static org.apache.commons.lang.StringUtils.length;
import static org.apache.commons.lang.StringUtils.substring;

import org.springframework.util.StringUtils;

import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CbsSpec;

/**
 * 060503_060503 規格
 */
public class Spec060503_060503 extends CbsSpec {

    @Override
    public void process() throws Exception {
        if ("0282".equals(getErrorCode()) || "0188".equals(getErrorCode())) {
            CBSUtilOutputVO output = new CBSUtilOutputVO();
            CBS060503OutputVO cbsVO = new CBS060503OutputVO();
            cbsVO.setIsuse(false);
            output.setCbs060503OutputVO(cbsVO);
            setCustomTxData(output);
            setHasCustomErrorProcess(true);
        } else{
        	// 特定的邏輯需要再次上送
        	Boolean isNeedAgaing = "0".equals(txData.getTxHeadVO().getFLAG2()) || "4".equals(txData.getTxHeadVO().getFLAG2());  	
            setMultiple(isNeedAgaing);
        }
    }

}
