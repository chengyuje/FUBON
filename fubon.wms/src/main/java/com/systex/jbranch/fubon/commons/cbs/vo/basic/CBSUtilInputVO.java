package com.systex.jbranch.fubon.commons.cbs.vo.basic;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.systex.jbranch.fubon.commons.cbs.vo._000400_032041.CBS000400InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._000454_032081.CBS000454InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._000454_032081.Spec000454_032081;
import com.systex.jbranch.fubon.commons.cbs.vo._000481_000482.CBS000481InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._002023_002024.CBS002023InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._002023_002024.CBS002024InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._010400_032105.CBS010400InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._017050_017000.CBS017050InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._017079_017079.CBS017079InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._017079_017079.Spec017079_017079;
import com.systex.jbranch.fubon.commons.cbs.vo._017904_017904.CBS017904InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._060425_060433.CBS060425InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._060425_060433.Spec060425_060433;
import com.systex.jbranch.fubon.commons.cbs.vo._060440_060441.CBS060440InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._060503_060503.CBS060503InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._060503_060503.Spec060503_060503;
import com.systex.jbranch.fubon.commons.cbs.vo._062141_062144.CBS062141InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._062167_062167.CBS062167InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._062171_062171.CBS062171InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._062410_062411.CBS062410InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._062410_062411.Spec062410_062411;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067000OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067050InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067101OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067115OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067157_067157.CBS067157InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067157_067157.CBS067157OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067157_067157.Spec067157_067157;
import com.systex.jbranch.fubon.commons.cbs.vo._067164_067165.CBS067164InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067164_067165.Spec067164_067165;
import com.systex.jbranch.fubon.commons.cbs.vo._067439_067442.CBS067439InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._085081_085105.CBS085081InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._085081_085105.Spec085081_085105;

@XmlRootElement(name = "Tx")
@XmlAccessorType(XmlAccessType.NONE)
public class CBSUtilInputVO {
    /**
     * 交易模組代號
     **/
    private String module;
    /**
     * 電文交易的特殊規格
     **/
    private CbsSpec spec = null;
    /**
     * 回傳電文 Id
     */
    private String pickUpId;

    @XmlElement(name = "TxHead")
    private TxHeadVO txHeadVO;

    @XmlElement(name = "TxBody")
    private CBS085081InputVO cbs085081Input;

    @XmlElement(name = "TxBody")
    private CBS000400InputVO cbs000400Input;

    @XmlElement(name = "TxBody")
    private CBS000481InputVO cbs000481InputVO;

    @XmlElement(name = "TxBody")
    private CBS000454InputVO cbs000454InputVO;

    @XmlElement(name = "TxBody")
    private CBS067050InputVO cbs067050InputVO;

    @XmlElement(name = "TxBody")
    private CBS067115OutputVO cbs067115UpdateVO;

    @XmlElement(name = "TxBody")
    private CBS060425InputVO cbs060425InputVO;

    @XmlElement(name = "TxBody")
    private CBS067164InputVO cbs067164InputVO;

    @XmlElement(name = "TxBody")
    private CBS062410InputVO cbs062410InputVO;

    @XmlElement(name = "TxBody")
    private CBS067157InputVO cbs067157InputVO;

    @XmlElement(name = "TxBody")
    private CBS067000OutputVO cbs067000updateVO;

    @XmlElement(name = "TxBody")
    private CBS067157OutputVO cbs067157updateVO;

    @XmlElement(name = "TxBody")
    private CBS067101OutputVO cbs067101updateVO;

    @XmlElement(name = "TxBody")
    private CBS010400InputVO cbs010400InputVO;

    @XmlElement(name = "TxBody")
    private CBS017050InputVO cbs017050InputVO;
    
    @XmlElement(name = "TxBody")
    private CBS062167InputVO cbs062167InputVO;
    
    @XmlElement(name = "TxBody")
    private CBS062141InputVO cbs062141InputVO;
    
    @XmlElement(name = "TxBody")
    private CBS002023InputVO cbs002023InputVO;

    @XmlElement(name = "TxBody")
    private CBS002024InputVO cbs002024InputVO;
    
    @XmlElement(name = "TxBody")
    private CBS060440InputVO cbs060440InputVO;
    
    @XmlElement(name = "TxBody")
    private CBS017079InputVO cbs017079InputVO;
    
    @XmlElement(name = "TxBody")
    private CBS017904InputVO cbs017904InputVO;
    
    @XmlElement(name = "TxBody")
    private CBS060503InputVO cbs060503InputVO;
    
    @XmlElement(name = "TxBody")
    private CBS062171InputVO cbs062171InputVO;
    
    @XmlElement(name = "TxBody")
    private CBS067439InputVO cbs067439InputVO;

    public String getPickUpId() {
        return pickUpId;
    }

    public CBSUtilInputVO setPickUpId(String pickUpId) {
        this.pickUpId = pickUpId;
        return this;
    }

    public String getModule() {
        return module;
    }

    public CBSUtilInputVO setModule(String module) {
        this.module = module;
        return this;
    }

    public TxHeadVO getTxHeadVO() {
        return txHeadVO;
    }

    public CBSUtilInputVO setTxHeadVO(TxHeadVO txHeadVO) {
        this.txHeadVO = txHeadVO;
        return this;
    }

    public CbsSpec getSpec() {
        return spec;
    }

    public void setSpec(CbsSpec spec) {
        this.spec = spec;
    }

    public CBS085081InputVO getCbs085081Input() {
        return cbs085081Input;
    }

    public CBSUtilInputVO setCbs085081Input(CBS085081InputVO cbs085081Input) {
        this.cbs085081Input = cbs085081Input;
        setSpec(new Spec085081_085105());
        return this;
    }

    public CBS000400InputVO getCbs000400Input() {
        return cbs000400Input;
    }

    public CBSUtilInputVO setCbs000400Input(CBS000400InputVO cbs000400Input) {
        this.cbs000400Input = cbs000400Input;
        return this;
    }

    public CBS000481InputVO getCbs000481InputVO() {
        return cbs000481InputVO;
    }

    public CBSUtilInputVO setCbs000481InputVO(CBS000481InputVO cbs000481InputVO) {
        this.cbs000481InputVO = cbs000481InputVO;
        return this;
    }

    public CBS000454InputVO getCbs000454InputVO() {
        return cbs000454InputVO;
    }

    public CBSUtilInputVO setCbs000454InputVO(CBS000454InputVO cbs000454InputVO) {
        this.cbs000454InputVO = cbs000454InputVO;
        setSpec(new Spec000454_032081());
        return this;
    }

    public CBS067050InputVO getCbs067050InputVO() {
        return cbs067050InputVO;
    }

    public CBSUtilInputVO setCbs067050InputVO(CBS067050InputVO cbs067050InputVO) {
        this.cbs067050InputVO = cbs067050InputVO;
        return this;
    }

    public CBS067115OutputVO getCbs067115UpdateVO() {
        return cbs067115UpdateVO;
    }

    public CBSUtilInputVO setCbs067115UpdateVO(CBS067115OutputVO cbs067115UpdateVO) {
        this.cbs067115UpdateVO = cbs067115UpdateVO;
        return this;
    }

    public CBS060425InputVO getCbs060425InputVO() {
        return cbs060425InputVO;
    }

    public CBSUtilInputVO setCbs060425InputVO(CBS060425InputVO cbs060425InputVO) {
        this.cbs060425InputVO = cbs060425InputVO;
        setSpec(new Spec060425_060433());
        return this;
    }

    public CBS067164InputVO getCbs067164InputVO() {
        return cbs067164InputVO;
    }

    public CBSUtilInputVO setCbs067164InputVO(CBS067164InputVO cbs067164InputVO) {
        this.cbs067164InputVO = cbs067164InputVO;
        setSpec(new Spec067164_067165());
        return this;
    }

    public CBS062410InputVO getCbs062410InputVO() {
        return cbs062410InputVO;
    }

    public CBSUtilInputVO setCbs062410InputVO(CBS062410InputVO cbs062410InputVO) {
        this.cbs062410InputVO = cbs062410InputVO;
        setSpec(new Spec062410_062411());
        return this;
    }

    public CBS067157InputVO getCbs067157InputVO() {
        return cbs067157InputVO;
    }

    public CBSUtilInputVO setCbs067157InputVO(CBS067157InputVO cbs067157InputVO) {
        this.cbs067157InputVO = cbs067157InputVO;
        setSpec(new Spec067157_067157());
        return this;
    }

    public CBS067000OutputVO getCbs067000updateVO() {
        return cbs067000updateVO;
    }

    public CBSUtilInputVO setCbs067000updateVO(CBS067000OutputVO cbs067000updateVO) {
        this.cbs067000updateVO = cbs067000updateVO;
        return this;
    }

    public CBS067157OutputVO getCbs067157updateVO() {
        return cbs067157updateVO;
    }

    public CBSUtilInputVO setCbs067157updateVO(CBS067157OutputVO cbs067157updateVO) {
        this.cbs067157updateVO = cbs067157updateVO;
        return this;
    }

    public CBS067101OutputVO getCbs067101updateVO() {
        return cbs067101updateVO;
    }

    public CBSUtilInputVO setCbs067101updateVO(CBS067101OutputVO cbs067101updateVO) {
        this.cbs067101updateVO = cbs067101updateVO;
        return this;
    }

    public CBS010400InputVO getCbs010400InputVO() {
        return cbs010400InputVO;
    }

    public CBSUtilInputVO setCbs010400InputVO(CBS010400InputVO cbs010400InputVO) {
        this.cbs010400InputVO = cbs010400InputVO;
        return this;
    }

    public CBS017050InputVO getCbs017050InputVO() {
        return cbs017050InputVO;
    }

    public CBSUtilInputVO setCbs017050InputVO(CBS017050InputVO cbs017050InputVO) {
        this.cbs017050InputVO = cbs017050InputVO;
        return this;
    }

	public CBS062167InputVO getCbs062167InputVO() {
		return cbs062167InputVO;
	}

	public CBSUtilInputVO setCbs062167InputVO(CBS062167InputVO cbs062167InputVO) {
		this.cbs062167InputVO = cbs062167InputVO;
		return this;
	}

	public CBS062141InputVO getCbs062141InputVO() {
		return cbs062141InputVO;
	}

	public CBSUtilInputVO setCbs062141InputVO(CBS062141InputVO cbs062141InputVO) {
		this.cbs062141InputVO = cbs062141InputVO;
		return this;
	}

	public CBS002023InputVO getCbs002023InputVO() {
		return cbs002023InputVO;
	}

	public CBSUtilInputVO setCbs002023InputVO(CBS002023InputVO cbs002023InputVO) {
		this.cbs002023InputVO = cbs002023InputVO;
		return this;
	}

	public CBS060440InputVO getCbs060440InputVO() {
		return cbs060440InputVO;
	}

	public CBSUtilInputVO setCbs060440InputVO(CBS060440InputVO cbs060440InputVO) {
		this.cbs060440InputVO = cbs060440InputVO;
		return this;
	}

	public CBS017079InputVO getCbs017079InputVO() {
		return cbs017079InputVO;
	}

	public CBSUtilInputVO setCbs017079InputVO(CBS017079InputVO cbs017079InputVO) {
		this.cbs017079InputVO = cbs017079InputVO;
		setSpec(new Spec017079_017079());
		return this;
	}

	public CBS002024InputVO getCbs002024InputVO() {
		return cbs002024InputVO;
	}

	public CBSUtilInputVO setCbs002024InputVO(CBS002024InputVO cbs002024InputVO) {
		this.cbs002024InputVO = cbs002024InputVO;
		return this;
	}

	public CBS017904InputVO getCbs017904InputVO() {
		return cbs017904InputVO;
	}

	public CBSUtilInputVO setCbs017904InputVO(CBS017904InputVO cbs017904InputVO) {
		this.cbs017904InputVO = cbs017904InputVO;
		return this;
	}

	public CBS060503InputVO getCbs060503InputVO() {	
		return cbs060503InputVO;
	}

	public CBSUtilInputVO setCbs060503InputVO(CBS060503InputVO cbs060503InputVO) {
		this.cbs060503InputVO = cbs060503InputVO;
		setSpec(new Spec060503_060503());
		return this;
	}

	public CBS062171InputVO getCbs062171InputVO() {
		return cbs062171InputVO;
	}

	public CBSUtilInputVO setCbs062171InputVO(CBS062171InputVO cbs062171InputVO) {
		this.cbs062171InputVO = cbs062171InputVO;
		return this;
	}

	public CBS067439InputVO getCbs067439InputVO() {
		return cbs067439InputVO;
	}

	public CBSUtilInputVO setCbs067439InputVO(CBS067439InputVO cbs067439InputVO) {
		this.cbs067439InputVO = cbs067439InputVO;
		return this;
	}
}
