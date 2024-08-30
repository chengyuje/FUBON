package com.systex.jbranch.fubon.commons.cbs.vo._017079_017079;

import com.systex.jbranch.fubon.commons.cbs.vo.basic.CbsSpec;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * 017079_017079 規格
 */
public class Spec017079_017079 extends CbsSpec {
    @Override
    public void process() throws Exception {
        if (isBlank(getErrorCode())) {
            setMultiple("C".equals(getTxDownObject().getServiceHeader().getHRETRN()));
        }
    }
}
