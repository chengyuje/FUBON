package com.systex.jbranch.fubon.commons.cbs.vo.basic;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.systex.jbranch.fubon.commons.cbs.vo._000400_032041.CBS032041OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._000454_032081.CBS032081OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._000481_000482.CBS000482OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._002023_002024.CBS002024OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._010400_032105.CBS032105OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._017050_017000.CBS017000OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._017050_017000.CBS017200OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._017079_017079.CBS017079OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._017904_017904.CBS017904OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._060425_060433.CBS060433OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._060440_060441.CBS060441OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._060503_060503.CBS060503OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._062141_062144.CBS062144OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._062167_062167.CBS062167OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._062171_062171.CBS062171OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._062410_062411.CBS062411OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067000OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067101OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067102OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067108OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067112OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067115OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067501OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067157_067157.CBS067157OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067164_067165.CBS067165OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._085081_085105.CBS085105OutputVO;

@XmlRootElement(name = "Tx")
@XmlAccessorType(XmlAccessType.NONE)
public class CBSUtilOutputVO {
	
	/**
	 * 017079_017079折返邏輯會用到
	 */
	private String hretrn;
	
    @XmlElement(name = "TxHead")
    private TxHeadVO txHeadVO;

    //add by SamTu 2018.07.30  for cbs type
    @XmlElement(name = "CBS085105")
    private CBS085105OutputVO cbs085105OutputVO;

    @XmlElement(name = "CBS032041")
    private CBS032041OutputVO cbs032041OutputVO;

    @XmlElement(name = "CBS000482")
    private CBS000482OutputVO cbs000482OutputVO;

    @XmlElement(name = "CBS032081")
    private CBS032081OutputVO cbs032081OutputVO;

    @XmlElement(name = "CBS067115")
    private CBS067115OutputVO cbs067115OutputVO;

    @XmlElement(name = "CBS067000")
    private CBS067000OutputVO cbs067000OutputVO;

    @XmlElement(name = "CBS067101")
    private CBS067101OutputVO cbs067101OutputVO;

    @XmlElement(name = "CBS067102")
    private CBS067102OutputVO cbs067102OutputVO;

    @XmlElement(name = "CBS067112")
    private CBS067112OutputVO cbs067112OutputVO;

    @XmlElement(name = "CBS060433")
    private CBS060433OutputVO cbs060433OutputVO;

    @XmlElement(name = "CBS067165")
    private CBS067165OutputVO cbs067165OutputVO;

    @XmlElement(name = "CBS062411")
    private CBS062411OutputVO cbs062411OutputVO;

    @XmlElement(name = "CBS067108")
    private CBS067108OutputVO cbs067108OutputVO;

    @XmlElement(name = "CBS067501")
    private CBS067501OutputVO cbs067501OutputVO;

    @XmlElement(name = "CBS067157")
    private CBS067157OutputVO cbs067157OutputVO;

    @XmlElement(name = "CBS032105")
    private CBS032105OutputVO cbs032105OutputVO;

    @XmlElement(name = "CBS017000")
    private CBS017000OutputVO cbs017000OutputVO;
    
    @XmlElement(name = "CBS062167")
    private CBS062167OutputVO cbs062167OutputVO;
    
    @XmlElement(name = "CBS062144")
    private CBS062144OutputVO cbs062144OutputVO;
    
    @XmlElement(name = "CBS002024")
    private CBS002024OutputVO cbs002024OutputVO;
    
    @XmlElement(name = "CBS017200")
    private CBS017200OutputVO cbs017200OutputVO;
    
    @XmlElement(name = "CBS060441")
    private CBS060441OutputVO cbs060441OutputVO;
    
    @XmlElement(name = "CBS017079")
    private CBS017079OutputVO cbs017079OutputVO;
    
    @XmlElement(name = "CBS017904")
    private CBS017904OutputVO cbs017904OutputVO;

    //    @XmlElement(name = "CBS062411")
//    private CBS062411OutputVO cbs062411OutputVO;
    
    @XmlElement(name = "CBS060503")
    private CBS060503OutputVO cbs060503OutputVO;
    
    @XmlElement(name = "CBS062171")
    private CBS062171OutputVO cbs062171OutputVO;
    
	public String getHretrn() {
		return hretrn;
	}

	public void setHretrn(String hretrn) {
		this.hretrn = hretrn;
	}

	public TxHeadVO getTxHeadVO() {
        return txHeadVO;
    }

    public void setTxHeadVO(TxHeadVO txHeadVO) {
        this.txHeadVO = txHeadVO;
    }

    public CBS085105OutputVO getCbs085105OutputVO() {
        return cbs085105OutputVO;
    }

    public void setCbs085105OutputVO(CBS085105OutputVO cbs085105OutputVO) {
        this.cbs085105OutputVO = cbs085105OutputVO;
    }

    public CBS032041OutputVO getCbs032041OutputVO() {
        return cbs032041OutputVO;
    }

    public void setCbs032041OutputVO(CBS032041OutputVO cbs032041OutputVO) {
        this.cbs032041OutputVO = cbs032041OutputVO;
    }

    public CBS000482OutputVO getCbs000482OutputVO() {
        return cbs000482OutputVO;
    }

    public void setCbs000482OutputVO(CBS000482OutputVO cbs000482OutputVO) {
        this.cbs000482OutputVO = cbs000482OutputVO;
    }

    public CBS032081OutputVO getCbs032081OutputVO() {
        return cbs032081OutputVO;
    }

    public void setCbs032081OutputVO(CBS032081OutputVO cbs032081OutputVO) {
        this.cbs032081OutputVO = cbs032081OutputVO;
    }

    public CBS067115OutputVO getCbs067115OutputVO() {
        return cbs067115OutputVO;
    }

    public void setCbs067115OutputVO(CBS067115OutputVO cbs067115OutputVO) {
        this.cbs067115OutputVO = cbs067115OutputVO;
    }

    public CBS067000OutputVO getCbs067000OutputVO() {
        return cbs067000OutputVO;
    }

    public void setCbs067000OutputVO(CBS067000OutputVO cbs067000OutputVO) {
        this.cbs067000OutputVO = cbs067000OutputVO;
    }

    public CBS067101OutputVO getCbs067101OutputVO() {
        return cbs067101OutputVO;
    }

    public void setCbs067101OutputVO(CBS067101OutputVO cbs067101OutputVO) {
        this.cbs067101OutputVO = cbs067101OutputVO;
    }

    public CBS067102OutputVO getCbs067102OutputVO() {
        return cbs067102OutputVO;
    }

    public void setCbs067102OutputVO(CBS067102OutputVO cbs067102OutputVO) {
        this.cbs067102OutputVO = cbs067102OutputVO;
    }

    public CBS067112OutputVO getCbs067112OutputVO() {
        return cbs067112OutputVO;
    }

    public void setCbs067112OutputVO(CBS067112OutputVO cbs067112OutputVO) {
        this.cbs067112OutputVO = cbs067112OutputVO;
    }

    public CBS060433OutputVO getCbs060433OutputVO() {
        return cbs060433OutputVO;
    }

    public void setCbs060433OutputVO(CBS060433OutputVO cbs060433OutputVO) {
        this.cbs060433OutputVO = cbs060433OutputVO;
    }

    public CBS067165OutputVO getCbs067165OutputVO() {
        return cbs067165OutputVO;
    }

    public void setCbs067165OutputVO(CBS067165OutputVO cbs067165OutputVO) {
        this.cbs067165OutputVO = cbs067165OutputVO;
    }

    public CBS062411OutputVO getCbs062411OutputVO() {
        return cbs062411OutputVO;
    }

    public void setCbs062411OutputVO(CBS062411OutputVO cbs062411OutputVO) {
        this.cbs062411OutputVO = cbs062411OutputVO;
    }

	public CBS067108OutputVO getCbs067108OutputVO() {
		return cbs067108OutputVO;
	}

	public void setCbs067108OutputVO(CBS067108OutputVO cbs067108OutputVO) {
		this.cbs067108OutputVO = cbs067108OutputVO;
	}


    public CBS067501OutputVO getCbs067501OutputVO() {
        return cbs067501OutputVO;
    }

    public void setCbs067501OutputVO(CBS067501OutputVO cbs067501OutputVO) {
        this.cbs067501OutputVO = cbs067501OutputVO;
    }

    public CBS067157OutputVO getCbs067157OutputVO() {
        return cbs067157OutputVO;
    }

    public void setCbs067157OutputVO(CBS067157OutputVO cbs067157OutputVO) {
        this.cbs067157OutputVO = cbs067157OutputVO;
    }

    public CBS032105OutputVO getCbs032105OutputVO() {
        return cbs032105OutputVO;
    }

    public void setCbs032105OutputVO(CBS032105OutputVO cbs032105OutputVO) {
        this.cbs032105OutputVO = cbs032105OutputVO;
    }

    public CBS017000OutputVO getCbs017000OutputVO() {
        return cbs017000OutputVO;
    }

    public void setCbs017000OutputVO(CBS017000OutputVO cbs017000OutputVO) {
        this.cbs017000OutputVO = cbs017000OutputVO;
    }

	public CBS062167OutputVO getCbs062167OutputVO() {
		return cbs062167OutputVO;
	}

	public void setCbs062167OutputVO(CBS062167OutputVO cbs062167OutputVO) {
		this.cbs062167OutputVO = cbs062167OutputVO;
	}

	public CBS062144OutputVO getCbs062144OutputVO() {
		return cbs062144OutputVO;
	}

	public void setCbs062144OutputVO(CBS062144OutputVO cbs062144OutputVO) {
		this.cbs062144OutputVO = cbs062144OutputVO;
	}

	public CBS002024OutputVO getCbs002024OutputVO() {
		return cbs002024OutputVO;
	}

	public void setCbs002024OutputVO(CBS002024OutputVO cbs002024OutputVO) {
		this.cbs002024OutputVO = cbs002024OutputVO;
	}

	public CBS017200OutputVO getCbs017200OutputVO() {
		return cbs017200OutputVO;
	}

	public void setCbs017200OutputVO(CBS017200OutputVO cbs017200OutputVO) {
		this.cbs017200OutputVO = cbs017200OutputVO;
	}

	public CBS060441OutputVO getCbs060441OutputVO() {
		return cbs060441OutputVO;
	}

	public void setCbs060441OutputVO(CBS060441OutputVO cbs060441OutputVO) {
		this.cbs060441OutputVO = cbs060441OutputVO;
	}

	public CBS017079OutputVO getCbs017079OutputVO() {
		return cbs017079OutputVO;
	}

	public void setCbs017079OutputVO(CBS017079OutputVO cbs017079OutputVO) {
		this.cbs017079OutputVO = cbs017079OutputVO;
	}

	public CBS017904OutputVO getCbs017904OutputVO() {
		return cbs017904OutputVO;
	}

	public void setCbs017904OutputVO(CBS017904OutputVO cbs017904OutputVO) {
		this.cbs017904OutputVO = cbs017904OutputVO;
	}

	public CBS060503OutputVO getCbs060503OutputVO() {
		return cbs060503OutputVO;
	}

	public void setCbs060503OutputVO(CBS060503OutputVO cbs060503OutputVO) {
		this.cbs060503OutputVO = cbs060503OutputVO;
	}

	public CBS062171OutputVO getCbs062171OutputVO() {
		return cbs062171OutputVO;
	}

	public void setCbs062171OutputVO(CBS062171OutputVO cbs062171OutputVO) {
		this.cbs062171OutputVO = cbs062171OutputVO;
	}
	
}
