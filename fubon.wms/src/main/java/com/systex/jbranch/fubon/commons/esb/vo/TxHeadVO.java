package com.systex.jbranch.fubon.commons.esb.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/8/19.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class TxHeadVO {
    //電文主機帳號, 電文流水號, 電文編號
    String id, seq, htxtid;

    @XmlElement
    private String HFMTID;

    /** 電文主機帳號 **/
    @XmlElement
    private String HWSID;

    /** 電文流水號(不可重複) **/
    @XmlElement
    private String HSTANO;

    /** 使用者代號，特定交易要上送固定的代號 **/
    @XmlElement
    private String HTLID;

    /** 電文編號 **/
    @XmlElement
    private String HTXTID;

    /** 是否還有下頁資料 C:還有資料  E:已無資料 **/
    @XmlElement
    private String HRETRN;

    @XmlElement
    private String PAGEFLG;


    @XmlElement
    private String HDRVQ1;
    
    @XmlElement
    private String HERRID;
    
    /**

	/**
     * automatically sets some default column in header
     * as follows:
     * HWSID    電文主機帳號
     * HSTANO   電文流水號 (不可重複)
     * HTXTID   電文編號
     *
     * @throws Exception
     */
    public void setDefaultTxHead() throws Exception {
        this.setHWSID(id);
        this.setHSTANO(seq);
        this.setHTXTID(htxtid);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getHtxtid() {
        return htxtid;
    }

    public void setHtxtid(String htxtid) {
        this.htxtid = htxtid;
    }

    public String getHFMTID() {
        return HFMTID;
    }

    public TxHeadVO setHFMTID(String HFMTID) {
        this.HFMTID = HFMTID;
        return this;
    }

    public String getHWSID() {
        return HWSID;
    }

    public void setHWSID(String HWSID) {
        this.HWSID = HWSID;
    }

    public String getHSTANO() {
        return HSTANO;
    }

    public void setHSTANO(String HSTANO) {
        this.HSTANO = HSTANO;
    }

    public String getHTLID() {
        return HTLID;
    }

    public TxHeadVO setHTLID(String HTLID) {
        this.HTLID = HTLID;
        return this;
    }

    public String getHTXTID() {
        return HTXTID;
    }

    public void setHTXTID(String HTXTID) {
        this.HTXTID = HTXTID;
    }

    public String getHRETRN() {
        return HRETRN;
    }

    public void setHRETRN(String HRETRN) {
        this.HRETRN = HRETRN;
    }

    public String getPAGEFLG() {
        return PAGEFLG;
    }

    public void setPAGEFLG(String PAGEFLG) {
        this.PAGEFLG = PAGEFLG;
    }

	public String getHDRVQ1() {
		return HDRVQ1;
	}

	public void setHDRVQ1(String hDRVQ1) {
		HDRVQ1 = hDRVQ1;
	}

	public String getHERRID() {
		return HERRID;
	}

	public void setHERRID(String hERRID) {
		HERRID = hERRID;
	}
    
    
}
