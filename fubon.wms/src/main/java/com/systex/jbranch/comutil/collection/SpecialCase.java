package com.systex.jbranch.comutil.collection;

import java.lang.reflect.Method;
import java.util.Map;

abstract public class SpecialCase {
	abstract public boolean isSpaceial(Method method)throws Exception;
	abstract public void mapToObject(Object object , Map map)throws Exception;
}
