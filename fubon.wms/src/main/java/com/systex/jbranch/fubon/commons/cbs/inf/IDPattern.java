package com.systex.jbranch.fubon.commons.cbs.inf;

import java.util.regex.Pattern;

/**
 * ID Pattern Interface
 * @author Eli
 * @date 20180223
 *
 */
public interface IDPattern {
	/**pattern of implement*/
	Pattern getPattern();
	/**whether match pattern*/
	boolean match(String id);
	/**implement's code*/
	String getCode();
}