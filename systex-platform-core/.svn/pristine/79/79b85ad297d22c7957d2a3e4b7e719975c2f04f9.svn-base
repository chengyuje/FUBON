package com.systex.jbranch.platform.common.platformdao.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @author Hibernate CodeGenerator */
public class TbsyssecukeystorVO extends VOBase {

    /** identifier field */
    private String keyid;

    /** persistent field */
    private String keytype;

    /** persistent field */
    private String keyvalue1;

    /** nullable persistent field */
    private String keyvalue2;
    
    /** persistent field */
    private String algorithm;

    /** nullable persistent field */
    private String description;

    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsyssecukeystor";
    public String getTableuid(){
    	return TABLE_UID;
    }
  
    /** full constructor */
    public TbsyssecukeystorVO(String keyid, String keytype, String keyvalue1, String keyvalue2,String algorithm, String description, String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this.keyid = keyid;
        this.keytype = keytype;
        this.keyvalue1 = keyvalue1;
        this.keyvalue2 = keyvalue2;
        this.algorithm = algorithm;
        this.description = description;
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

    /** default constructor */
    public TbsyssecukeystorVO() {
    }

    /** minimal constructor */
    public TbsyssecukeystorVO(String keyid, String keytype, String keyvalue1, String algorithm) {
        this.keyid = keyid;
        this.keytype = keytype;
        this.keyvalue1 = keyvalue1;
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getKeyid() {
        return this.keyid;
    }

    public void setKeyid(String keyid) {
        this.keyid = keyid;
    }

    public String getKeytype() {
        return this.keytype;
    }

    public void setKeytype(String keytype) {
        this.keytype = keytype;
    }

    public String getKeyvalue1() {
        return this.keyvalue1;
    }

    public void setKeyvalue1(String keyvalue1) {
        this.keyvalue1 = keyvalue1;
    }

    public String getKeyvalue2() {
        return this.keyvalue2;
    }

    public void setKeyvalue2(String keyvalue2) {
        this.keyvalue2 = keyvalue2;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


  
    public String toString() {
        return new ToStringBuilder(this)
            .append("keyid", getKeyid())
            .toString();
    }

}
