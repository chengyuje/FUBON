package com.systex.jbranch.app.server.fps.sot701.ruler;

import org.apache.commons.lang.StringUtils;

/**
 * @author Eli
 * @date 20180822
 * 
 */
public class ProdRuler {
	 public static boolean isFUND(String type) {
		 return StringUtils.equals(type, "1");
	 }
	
	 public static boolean isES(String type) {
		 return StringUtils.equals(type, "2");
	 }
	 
	 public static boolean isSI(String type) {
		 return StringUtils.equals(type, "4") || StringUtils.equals(type, "7"); //SI & FCI
	 }
	 
	 public static boolean isINS(String type) {
		 return StringUtils.equals(type, "6");
	 }
}
