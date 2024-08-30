package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TbsysconfigPK  implements Serializable  {

    /** identifier field */
    private String type;

    /** identifier field */
    private String name;

    private String key;

public static final String TABLE_UID = "com.systex.jbranch.app.javabranch.table.Tbsysconfig";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TbsysconfigPK(String type, String name, String key) {
		super();
		this.type = type;
		this.name = name;
		this.key = key;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return "TbsysconfigPK [type=" + type + ", name=" + name + ", key="
				+ key + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TbsysconfigPK other = (TbsysconfigPK) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}


}
