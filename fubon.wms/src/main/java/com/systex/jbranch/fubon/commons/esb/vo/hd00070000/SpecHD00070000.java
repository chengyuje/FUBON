package com.systex.jbranch.fubon.commons.esb.vo.hd00070000;

import com.systex.jbranch.fubon.commons.esb.vo.EsbSpec;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * HD00070000 規格
 */
public class SpecHD00070000 extends EsbSpec {
    @Override
    public void process() throws Exception {
        if (isBlank(getErrorVO().getEMSGID())) {
            setMultiple("C".equals(getHRETRN()));

            // 如果要繼續上送其 InputVO 需要設置頁數
            if (isMultiple && null != txData) {
                HD00070000InputVO hd00070000InputVO = request.getHd00070000InputVO();
                String page = String.valueOf((Integer.parseInt(hd00070000InputVO.getPAGE_NUM()) + 1));
                hd00070000InputVO.setPAGE_NUM(page);
            }
        }
    }
}
