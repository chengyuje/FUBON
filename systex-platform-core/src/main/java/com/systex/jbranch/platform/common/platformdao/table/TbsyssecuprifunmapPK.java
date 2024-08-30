package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;

public class TbsyssecuprifunmapPK implements Serializable{
	
    /** persistent field */
    private String moduleid;

    /** persistent field */
    private String itemid;

    /** persistent field */
    private String functionid;

    /** persistent field */
    private String privilegeid;

	public String getModuleid() {
		return moduleid;
	}

	public void setModuleid(String moduleid) {
		this.moduleid = moduleid;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getFunctionid() {
		return functionid;
	}

	public void setFunctionid(String functionid) {
		this.functionid = functionid;
	}

	public String getPrivilegeid() {
		return privilegeid;
	}

	public void setPrivilegeid(String privilegeid) {
		this.privilegeid = privilegeid;
	}

}
