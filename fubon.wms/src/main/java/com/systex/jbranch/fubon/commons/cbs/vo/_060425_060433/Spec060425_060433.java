package com.systex.jbranch.fubon.commons.cbs.vo._060425_060433;

import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CbsSpec;

/**
 * 060425_060433 規格
 */
public class Spec060425_060433 extends CbsSpec {
    @Override
    public void process() throws Exception {
        if ("0188".equals(getErrorCode())) {
            CBSUtilOutputVO output = new CBSUtilOutputVO();
            CBS060433OutputVO cbsVO = new CBS060433OutputVO();
            cbsVO.setIsuse(false);
            output.setCbs060433OutputVO(cbsVO);
            setCustomTxData(output);
            setHasCustomErrorProcess(true);
        }
    }
}
