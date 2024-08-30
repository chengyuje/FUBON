package com.systex.jbranch.fubon.commons.esb.vo.njbrvc3;

import static org.apache.commons.lang.StringUtils.isBlank;

import com.systex.jbranch.fubon.commons.esb.vo.EsbSpec;

public class SpecNJBRVC3 extends EsbSpec {

	@Override
	public void process() throws Exception {
        if (isBlank(getErrorVO().getEMSGID())) {
            setMultiple("C".equals(getHRETRN()));

            // 如果要繼續上送其 InputVO 需要設置頁數
            if (isMultiple && null != txData) {
            	NJBRVC3InputVO njbrvcc3nputVO = request.getNjbrvc3InputVO();
            }
        }
		
	}

}
