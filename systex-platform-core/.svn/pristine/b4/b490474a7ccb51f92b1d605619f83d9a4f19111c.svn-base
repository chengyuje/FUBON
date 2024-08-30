package com.systex.jbranch.platform.common.dataManager;

import java.io.Serializable;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

/**
 * 作為與PlatForm資料流通的暫存區。<br>
 * @author Char.Yu
 *
 */

public class PlatFormVO implements Serializable {
	private Map<String, Serializable> vars =new Hashtable<String, Serializable>();

	public Object getVar(String key){
		return vars.get(key);
	}
	public void setVar(String key,Object value)
	{
		synchronized(this.vars)
		{
			if(key!=null)
			{
				this.vars.remove(key);

				if(value!=null)
					this.vars.put(key, (Serializable) value);
			}
		}
	}

	/**
	 * 不開放使用,避免開發人員操作整個HashMap
	 * @author Char.Yu
	 *
	 */
//	protected Hashtable<String, Object> getVars() {
//		return vars;
//	}
	public Map<String, Serializable> getVars() {
		return Collections.unmodifiableMap(vars);
	}
//	protected void setVars(Hashtable<String, Object> vars) {
//		synchronized(this.vars) {
//			this.vars = vars;
//		}
//	}
}
