package com.systex.jbranch.app.server.fps.insjlb.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.systex.jbranch.app.server.fps.insjlb.vo.CalFamilyGapInputVO;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public class FubonGetInsValiateUtils {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(FubonGetInsValiateUtils.class);
	
	public static void validate(CalFamilyGapInputVO inputVo) throws JBranchException{
		if(inputVo == null || StringUtils.isBlank(inputVo.getInsCustID())){
			throw new JBranchException("in CalFamilyGapInputVO insCustID is empty");
			
		}
	}
}
