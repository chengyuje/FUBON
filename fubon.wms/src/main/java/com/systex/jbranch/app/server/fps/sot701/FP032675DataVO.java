package com.systex.jbranch.app.server.fps.sot701;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by SamTu on 2018/02/26.
 *
 * FC032675電文(客戶)資料VO
 */
public class FP032675DataVO {
    private String  custID;         //客戶ID
    private String  custName;       //客戶姓名
    private String  custRemarks;    //客戶註記 (弱勢客戶註記)
    private String  custProFlag;    //專業投資人(Y/N)
    private String	custProType;	//專業投資人類型 (1:大專投 2:小專投)
	private Date    custProDate;    //專業投資人效期
	private String  custProRemark;	//專業投資人註記    20200210 應該是標註信託同意書註記 
    private String  custTxFlag;     //同意投資商品諮詢服務註記 （ANS：就是推介同意註記，會更名為同意投資商品諮詢服務）
    private String  rejectProdFlag; //商品拒銷註記
    private String  deathFlag;      //死亡戶註記
    private String  noSale;         //商品禁銷註記
    private String  billType;       //帳單註記
    private String  obuFlag;        //OBU 客戶FLG
    private String  investFlag;     //客戶申購戶況
    private String  investType;     //特定客戶
    private String  investExp;      //是否具備結構複雜商品之投資經驗
    private String  investDue;      //申請日距今是否滿兩週
    private String  ageUnder70Flag; //年齡小於70
    private String  eduJrFlag;      //教育程度國中以上(不含國中)
    private String  healthFlag;     //未領有全民健保重大傷病證明
    private String  sickType;       //重大傷病等級
    private String  dmFlag;			//銀行DM註記
    private String  edmFlag;		//銀行EDM註記
    private String  smsFlag;		//銀行SMS註記
    private String  tmFlag;			//銀行TM註記
    private String  infoFlag;		//基本資料共同行銷註記
    private String  acc1Flag;		//帳戶資料-產物共同行銷註記
    private String  acc2Flag; 		//帳戶資料-人壽共同行銷註記
    private String  acc3Flag;		//帳戶資料-證券共同行銷註記
    private String  acc4Flag;		//帳戶資料-投信共同行銷註記
    private String  acc5Flag;		//帳戶資料-直效行銷共同行銷註記  (行銷已取消，輸出欄位固定為空白)
    private String  acc6Flag; 		//帳戶資料-期貨共同行銷註記
    private String  trustFlag;		//信託交易>=5筆
    private String  acc7Flag;		//帳戶資料-富昇人身共同行銷註記
    private String  acc8Flag;		//帳戶資料-富昇財產共同行銷註記
    private String  acc1Other;		//基本資料 (其它)註記-產物註記
    private String  acc2Other;		//基本資料 (其它)註記 人壽註記
    private String  acc3Other;		//基本資料 (其它)註記 證券註記
    private String  acc4Other;		//基本資料 (其它)註記 投信註記
    private String  acc6Other;		//基本資料 (其它)註記 期貨註記
    private String  acc7Other;		//基本資料 (其它)註記 人身註記
    private String  acc8Other;		//基本資料 (其它)註記 財產註記
    private String  bondFlag;		//結構型商品客戶交易經驗或行業經驗
    private Date	kycEndDate;		//KYC到期日
    private String  idnF;			//目前FATCA身分
    private Date 	bDay;			//生日
    private String  branchCode;		//分行代碼
    private String	degrade;		//ＫＹＣ免降等註記(Y/N)
    private String	degradeDate;	//ＫＹＣ免降等註記到期日
    private String	proCorpInv;		//專業機構投資人
    private String	proCorpInv2;	//專業機構投資人
    private String	highYieldCorp;	//高淨值法人
    private String	siProCorp;		//衍商資格專業法人
    private String	trustProCorp;	//信託專業法人
    private String	pro3000;		//專業自然人提供3000萬財力證明
    private String	pro1500;		//專業自然人提供1500萬財力證明
	
	private boolean isPiDueDateUseful; //FOR CBS測試日期修改  Y代表過期 N代表沒過期

	
	
    public boolean isPiDueDateUseful() {
		return isPiDueDateUseful;
	}

	public void setPiDueDateUseful(boolean isPiDueDateUseful) {
		this.isPiDueDateUseful = isPiDueDateUseful;
	}

	public String getCustID() {
        return custID;
    }

    public void setCustID(String custID) {
        this.custID = custID;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustRemarks() {
        return custRemarks;
    }

    public void setCustRemarks(String custRemarks) {
        this.custRemarks = custRemarks;
    }

    public String getCustProFlag() {
        return custProFlag;
    }

    public void setCustProFlag(String custProFlag) {
        this.custProFlag = custProFlag;
    }
    
    public String getCustProType() {
		return custProType;
	}

	public void setCustProType(String custProType) {
		this.custProType = custProType;
	}
	
    public Date getCustProDate() {
        return custProDate;
    }

    public void setCustProDate(Date custProDate) {
        this.custProDate = custProDate;
    }

    public String getCustTxFlag() {
        return custTxFlag;
    }

    public void setCustTxFlag(String custTxFlag) {
        this.custTxFlag = custTxFlag;
    }

    public String getRejectProdFlag() {
        return rejectProdFlag;
    }

    public void setRejectProdFlag(String rejectProdFlag) {
        this.rejectProdFlag = rejectProdFlag;
    }

    public String getDeathFlag() {
        return deathFlag;
    }

    public void setDeathFlag(String deathFlag) {
        this.deathFlag = deathFlag;
    }

    public String getNoSale() {
        return noSale;
    }

    public void setNoSale(String noSale) {
        this.noSale = noSale;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getObuFlag() {
        return obuFlag;
    }

    public void setObuFlag(String obuFlag) {
        this.obuFlag = obuFlag;
    }

    public String getInvestFlag() {
        return investFlag;
    }

    public void setInvestFlag(String investFlag) {
        this.investFlag = investFlag;
    }

    public String getInvestType() {
        return investType;
    }

    public void setInvestType(String investType) {
        this.investType = investType;
    }

    public String getInvestExp() {
        return investExp;
    }

    public void setInvestExp(String investExp) {
        this.investExp = investExp;
    }

    public String getInvestDue() {
        return investDue;
    }

    public void setInvestDue(String investDue) {
        this.investDue = investDue;
    }

    public String getAgeUnder70Flag() {
        return ageUnder70Flag;
    }

    public void setAgeUnder70Flag(String ageUnder70Flag) {
        this.ageUnder70Flag = ageUnder70Flag;
    }

    public String getEduJrFlag() {
        return eduJrFlag;
    }

    public void setEduJrFlag(String eduJrFlag) {
        this.eduJrFlag = eduJrFlag;
    }

    public String getHealthFlag() {
        return healthFlag;
    }

    public void setHealthFlag(String healthFlag) {
        this.healthFlag = healthFlag;
    }

    public String getSickType() {
        return sickType;
    }

    public void setSickType(String sickType) {
        this.sickType = sickType;
    }

    public String getCustProRemark() {
		return custProRemark;
	}

	public void setCustProRemark(String custProRemark) {
		this.custProRemark = custProRemark;
	}

	public String getDmFlag() {
		return dmFlag;
	}

	public void setDmFlag(String dmFlag) {
		this.dmFlag = dmFlag;
	}

	public String getEdmFlag() {
		return edmFlag;
	}

	public void setEdmFlag(String edmFlag) {
		this.edmFlag = edmFlag;
	}

	public String getSmsFlag() {
		return smsFlag;
	}

	public void setSmsFlag(String smsFlag) {
		this.smsFlag = smsFlag;
	}

	public String getTmFlag() {
		return tmFlag;
	}

	public void setTmFlag(String tmFlag) {
		this.tmFlag = tmFlag;
	}

	public String getInfoFlag() {
		return infoFlag;
	}

	public void setInfoFlag(String infoFlag) {
		this.infoFlag = infoFlag;
	}

	public String getAcc1Flag() {
		return acc1Flag;
	}

	public void setAcc1Flag(String acc1Flag) {
		this.acc1Flag = acc1Flag;
	}

	public String getAcc2Flag() {
		return acc2Flag;
	}

	public void setAcc2Flag(String acc2Flag) {
		this.acc2Flag = acc2Flag;
	}

	public String getAcc3Flag() {
		return acc3Flag;
	}

	public void setAcc3Flag(String acc3Flag) {
		this.acc3Flag = acc3Flag;
	}

	public String getAcc4Flag() {
		return acc4Flag;
	}

	public void setAcc4Flag(String acc4Flag) {
		this.acc4Flag = acc4Flag;
	}

	public String getAcc5Flag() {
		return acc5Flag;
	}

	public void setAcc5Flag(String acc5Flag) {
		this.acc5Flag = acc5Flag;
	}

	public String getAcc6Flag() {
		return acc6Flag;
	}

	public void setAcc6Flag(String acc6Flag) {
		this.acc6Flag = acc6Flag;
	}

	public String getTrustFlag() {
		return trustFlag;
	}

	public void setTrustFlag(String trustFlag) {
		this.trustFlag = trustFlag;
	}

	public String getAcc7Flag() {
		return acc7Flag;
	}

	public void setAcc7Flag(String acc7Flag) {
		this.acc7Flag = acc7Flag;
	}

	public String getAcc8Flag() {
		return acc8Flag;
	}

	public void setAcc8Flag(String acc8Flag) {
		this.acc8Flag = acc8Flag;
	}

	public String getAcc1Other() {
		return acc1Other;
	}

	public void setAcc1Other(String acc1Other) {
		this.acc1Other = acc1Other;
	}

	public String getAcc2Other() {
		return acc2Other;
	}

	public void setAcc2Other(String acc2Other) {
		this.acc2Other = acc2Other;
	}

	public String getAcc3Other() {
		return acc3Other;
	}

	public void setAcc3Other(String acc3Other) {
		this.acc3Other = acc3Other;
	}

	public String getAcc4Other() {
		return acc4Other;
	}

	public void setAcc4Other(String acc4Other) {
		this.acc4Other = acc4Other;
	}

	public String getAcc6Other() {
		return acc6Other;
	}

	public void setAcc6Other(String acc6Other) {
		this.acc6Other = acc6Other;
	}

	public String getAcc7Other() {
		return acc7Other;
	}

	public void setAcc7Other(String acc7Other) {
		this.acc7Other = acc7Other;
	}

	public String getAcc8Other() {
		return acc8Other;
	}

	public void setAcc8Other(String acc8Other) {
		this.acc8Other = acc8Other;
	}

	public String getBondFlag() {
		return bondFlag;
	}

	public void setBondFlag(String bondFlag) {
		this.bondFlag = bondFlag;
	}

	public Date getKycEndDate() {
		return kycEndDate;
	}

	public void setKycEndDate(Date kycEndDate) {
		this.kycEndDate = kycEndDate;
	}

	public String getIdnF() {
		return idnF;
	}

	public void setIdnF(String idnF) {
		this.idnF = idnF;
	}

	public Date getbDay() {
		return bDay;
	}

	public void setbDay(Date bDay) {
		this.bDay = bDay;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	
	public String getDegradeDate() {
		return degradeDate;
	}

	public void setDegradeDate(String degradeDate) {
		this.degradeDate = degradeDate;
	}

	public String getDegrade() {
		return degrade;
	}

	public void setDegrade(String degrade) {
		this.degrade = degrade;
	}

    public String getProCorpInv() {
		return proCorpInv;
	}

	public void setProCorpInv(String proCorpInv) {
		this.proCorpInv = proCorpInv;
	}

	public String getProCorpInv2() {
		return proCorpInv2;
	}

	public void setProCorpInv2(String proCorpInv2) {
		this.proCorpInv2 = proCorpInv2;
	}

	public String getHighYieldCorp() {
		return highYieldCorp;
	}

	public void setHighYieldCorp(String highYieldCorp) {
		this.highYieldCorp = highYieldCorp;
	}

	public String getSiProCorp() {
		return siProCorp;
	}

	public void setSiProCorp(String siProCorp) {
		this.siProCorp = siProCorp;
	}

	public String getTrustProCorp() {
		return trustProCorp;
	}

	public void setTrustProCorp(String trustProCorp) {
		this.trustProCorp = trustProCorp;
	}

	public String getPro3000() {
		return pro3000;
	}

	public void setPro3000(String pro3000) {
		this.pro3000 = pro3000;
	}

	public String getPro1500() {
		return pro1500;
	}

	public void setPro1500(String pro1500) {
		this.pro1500 = pro1500;
	}
	
	@Override
    public String toString() {
        return "FC032675DataVO{" +
                "custID='" + custID + '\'' +
                ", custName='" + custName + '\'' +
                ", custRemarks='" + custRemarks + '\'' +
                ", custProFlag='" + custProFlag + '\'' +
                ", custProType='" + custProType + '\'' +
                ", custProDate='" + custProDate + '\'' +
                ", custProRemark='" + custProRemark + '\'' +
                ", custTxFlag='" + custTxFlag + '\'' +
                ", rejectProdFlag='" + rejectProdFlag + '\'' +
                ", deathFlag='" + deathFlag + '\'' +
                ", noSale='" + noSale + '\'' +
                ", billType='" + billType + '\'' +
                ", obuFlag='" + obuFlag + '\'' +
                ", investFlag='" + investFlag + '\'' +
                ", investType='" + investType + '\'' +
                ", investExp='" + investExp + '\'' +
                ", investDue='" + investDue + '\'' +
                ", ageUnder70Flag='" + ageUnder70Flag + '\'' +
                ", eduJrFlag='" + eduJrFlag + '\'' +
                ", healthFlag='" + healthFlag + '\'' +
                ", sickType='" + sickType + '\'' +
                ", dmFlag='" + dmFlag + '\'' +
				", edmFlag='" + edmFlag + '\'' +
				", smsFlag='" + smsFlag + '\'' +
				", tmFlag='" + tmFlag + '\'' +
				", infoFlag='" + infoFlag + '\'' +
				", acc1Flag='" + acc1Flag + '\'' +
				", acc2Flag='" + acc2Flag + '\'' +
				", acc3Flag='" + acc3Flag + '\'' +
				", acc4Flag='" + acc4Flag + '\'' +
				", acc5Flag='" + acc5Flag + '\'' +
				", acc6Flag='" + acc6Flag + '\'' +
				", trustFlag='" + trustFlag + '\'' +
				", acc7Flag='" + acc7Flag + '\'' +
				", acc8Flag='" + acc8Flag + '\'' +
				", acc1Other='" + acc1Other + '\'' +
				", acc2Other='" + acc2Other + '\'' +
				", acc3Other='" + acc3Other + '\'' +
				", acc4Other='" + acc4Other + '\'' +
				", acc6Other='" + acc6Other + '\'' +
				", acc7Other='" + acc7Other + '\'' +
				", acc8Other='" + acc8Other + '\'' +
				", bondFlag='" + bondFlag + '\'' +
				", kycEndDate='" + kycEndDate + '\'' +
				", idnF='" + idnF + '\'' +
				", bDay='" + bDay + '\'' +
				", branchCode='" + branchCode + '\'' +
				", degrade='" + degrade + '\'' +
				", proCorpInv='" + proCorpInv + '\'' +
				", highYieldCorp='" + highYieldCorp + '\'' +
				", siProCorp='" + siProCorp + '\'' +
				", trustProCorp='" + trustProCorp + '\'' +
				", pro3000='" + pro3000 + '\'' +
				", pro1500='" + pro1500 + '\'' +
                '}';
    }
}
