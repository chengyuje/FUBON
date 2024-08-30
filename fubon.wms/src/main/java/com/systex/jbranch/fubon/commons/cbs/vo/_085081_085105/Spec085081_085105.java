package com.systex.jbranch.fubon.commons.cbs.vo._085081_085105;

import com.systex.jbranch.fubon.commons.cbs.vo.basic.CbsSpec;

import static org.apache.commons.lang.StringUtils.*;

/**
 * 085081_085105 規格
 */
public class Spec085081_085105 extends CbsSpec {
    @Override
    public void process() throws Exception {
        // 0188 為需要忽略的代碼，但是其他數字電文的 0188 可能不能忽略
        if (isNotBlank(getErrorCode())) {
            setHasCustomErrorProcess("0188".equals(getErrorCode()));
        } else {
            // 特定的邏輯需要再次上送
            String key = getTxData().getCbs085105OutputVO().getHLD_KEY();
            boolean needAgain = !(length(key) >= 15 && "999999999999999".equals(substring(key, 0, 15)));
            setMultiple(needAgain);
            request.getCbs085081Input().setDefaultString2("S1" + getTxData().getCbs085105OutputVO().getHLD_KEY());
        }
    }
}
