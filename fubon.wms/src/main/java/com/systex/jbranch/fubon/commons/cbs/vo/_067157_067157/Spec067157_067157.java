package com.systex.jbranch.fubon.commons.cbs.vo._067157_067157;

import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CbsSpec;

/**
 * 067157_067157 規格
 */
public class Spec067157_067157 extends CbsSpec {
    @Override
    public void process() throws Exception {
        if ("0188".equals(getErrorCode())) {
            CBSUtilOutputVO outputVO = new CBSUtilOutputVO();
            CBS067157OutputVO cbsVO = new CBS067157OutputVO();
            cbsVO.setOption1("C");
            outputVO.setCbs067157OutputVO(cbsVO);
            setCustomTxData(outputVO);
            setHasCustomErrorProcess(true);
        }
    }
}
