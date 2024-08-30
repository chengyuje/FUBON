package com.systex.jbranch.fubon.commons.cbs.vo._062410_062411;

import com.systex.jbranch.fubon.commons.cbs.vo.basic.CbsSpec;

/**
 * 062410_062411 規格
 */
public class Spec062410_062411 extends CbsSpec {

	 @Override
	    public void process() throws Exception {
	        if ("0188".equals(getErrorCode())) {
	            setHasCustomErrorProcess(true);
	        }
	    }

}
