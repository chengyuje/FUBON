package com.systex.jbranch.fubon.commons.cbs.vo._067164_067165;

import com.systex.jbranch.fubon.commons.cbs.vo.basic.CbsSpec;
import com.systex.jbranch.platform.common.errHandle.APException;

/**
 * 067164_067165 規格
 */
public class Spec067164_067165 extends CbsSpec {
    @Override
    public void process() throws Exception {
        if ("7001".equals(getErrorCode()))
            throw new APException("共同行銷註記查無資料");
    }
}
