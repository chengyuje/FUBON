package com.systex.jbranch.platform.common.platformdao.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TbsysdximpmasterVO extends VOBase {

    /** identifier field */
    private Long importmasterid;

    /** nullable persistent field */
    private String filepath;

    /** nullable persistent field */
    private String tablename;

    /** nullable persistent field */
    private BigDecimal commitcount;

    /** nullable persistent field */
    private String action;

    /** nullable persistent field */
    private BigDecimal filetype;

    /** nullable persistent field */
    private String encoding;

    private String methodtype;
    
    private String columnmap;
    
    private String timeformat;
    
    private String datefromat;
    
    private String timestampformat;
    
    private String ftpsettingid;

public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsysdximpmaster";

public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TbsysdximpmasterVO(String filepath, String tablename, BigDecimal commitcount, String action, BigDecimal filetype, String encoding, String creator, Timestamp createtime, String modifier, Timestamp lastupdate, Long version
    		, String methodtype, String columnmap, String timeformat, String datefromat, String timestampformat, String ftpsettingid) {
        this.filepath = filepath;
        this.tablename = tablename;
        this.commitcount = commitcount;
        this.action = action;
        this.filetype = filetype;
        this.encoding = encoding;
        this.creator = creator;
        this.createtime = createtime;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
        this.methodtype = methodtype;
        this.columnmap = columnmap;
        this.timeformat = timeformat;
        this.datefromat = datefromat;
        this.timestampformat = timestampformat;
        this.ftpsettingid = ftpsettingid;
    }

    /** default constructor */
    public TbsysdximpmasterVO() {
    }

    public Long getimportmasterid() {
        return this.importmasterid;
    }

    public void setimportmasterid(Long importmasterid) {
        this.importmasterid = importmasterid;
    }

    public String getfilepath() {
        return this.filepath;
    }

    public void setfilepath(String filepath) {
        this.filepath = filepath;
    }

    public String gettablename() {
        return this.tablename;
    }

    public void settablename(String tablename) {
        this.tablename = tablename;
    }

    public BigDecimal getcommitcount() {
        return this.commitcount;
    }

    public void setcommitcount(BigDecimal commitcount) {
        this.commitcount = commitcount;
    }

    public String getaction() {
        return this.action;
    }

    public void setaction(String action) {
        this.action = action;
    }

    public BigDecimal getfiletype() {
        return this.filetype;
    }

    public void setfiletype(BigDecimal filetype) {
        this.filetype = filetype;
    }

    public String getencoding() {
        return this.encoding;
    }

    public void setencoding(String encoding) {
        this.encoding = encoding;
    }

    public void checkDefaultValue() {
    }
    


    public String getmethodtype() {
    	return methodtype;
    }

    public void setmethodtype(String methodtype) {
    	this.methodtype = methodtype;
    }

    public String getcolumnmap() {
    	return columnmap;
    }

    public void setcolumnmap(String columnmap) {
    	this.columnmap = columnmap;
    }

    public String gettimeformat() {
    	return timeformat;
    }

    public void settimeformat(String timeformat) {
    	this.timeformat = timeformat;
    }

    public String getdatefromat() {
    	return datefromat;
    }

    public void setdatefromat(String datefromat) {
    	this.datefromat = datefromat;
    }

    public String gettimestampformat() {
    	return timestampformat;
    }

    public void settimestampformat(String timestampformat) {
    	this.timestampformat = timestampformat;
    }

    public String getftpsettingid() {
    	return ftpsettingid;
    }

    public void setftpsettingid(String ftpsettingid) {
    	this.ftpsettingid = ftpsettingid;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("importmasterid", getimportmasterid())
            .toString();
    }

}
