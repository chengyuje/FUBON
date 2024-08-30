package com.systex.jbranch.app.server.fps.fps210;

import com.systex.jbranch.app.server.fps.fps200.FPS200InputVO;

import java.math.BigDecimal;

public class FPS210InputVO extends FPS200InputVO{
  public FPS210InputVO(){}
  
  private boolean isDirty;
  private String hasIns;
//  private String custID;
  // total
//  private BigDecimal planAmt;
  // amt A
  private BigDecimal deposit;
  // prod A1 A2 A3 A4
  private BigDecimal annuityProd;
  private BigDecimal fixedProd;
  private BigDecimal fundProd;
  // amt B
  private BigDecimal sowAmt;
  
  // cash C
  private BigDecimal cash;
  private BigDecimal essCash;
  private BigDecimal emeCsh;
  private BigDecimal houseLoan;
  private BigDecimal creditLoan;
  private BigDecimal stdLoan;
  private BigDecimal payForHouse;
  private BigDecimal payForCar;
  private BigDecimal study;
  private BigDecimal travel;
  private BigDecimal other;
  
  // inv D1 D2
  private BigDecimal insSavAmt;
  private BigDecimal insPolicyAmt;
  
  private BigDecimal mfdProd;
  private BigDecimal etfProd;
  private BigDecimal insProd;
  private BigDecimal bondProd;
  private BigDecimal snProd;
  private BigDecimal siProd;
  private BigDecimal nanoProd; //2020-1-30 by Jacky 新增奈米投
  
  private BigDecimal cashPrepare;
  private BigDecimal loanExpenses;
  private BigDecimal otherExpenses;
  
  private BigDecimal portfolio2Ratio;
  private BigDecimal portfolio3Ratio;
  private BigDecimal portfolio2Amt;
  private BigDecimal portfolio3Amt;
  
//	public String getCustID() {
//	  return custID;
//	}
//	
//	public void setCustID(String custID) {
//	  this.custID = custID;
//	}

//  public BigDecimal getPlanAmt() {
//    return planAmt;
//  }
//
//  public void setPlanAmt(BigDecimal planAmt) {
//    this.planAmt = planAmt;
//  }
  
  public String getHasIns() {
    return hasIns;
  }

  public BigDecimal getNanoProd() {
	return nanoProd;
}

public void setNanoProd(BigDecimal nanoProd) {
	this.nanoProd = nanoProd;
}

public void setHasIns(String hasIns) {
    this.hasIns = hasIns;
  }

  public BigDecimal getDeposit() {
    return deposit;
  }

  public void setDeposit(BigDecimal deposit) {
    this.deposit = deposit;
  }

  public BigDecimal getAnnuityProd() {
    return annuityProd;
  }

  public void setAnnuityProd(BigDecimal annuityProd) {
    this.annuityProd = annuityProd;
  }

  public BigDecimal getFixedProd() {
    return fixedProd;
  }

  public void setFixedProd(BigDecimal fixedProd) {
    this.fixedProd = fixedProd;
  }

  public BigDecimal getFundProd() {
    return fundProd;
  }

  public void setFundProd(BigDecimal fundProd) {
    this.fundProd = fundProd;
  }

  public BigDecimal getSowAmt() {
    return sowAmt;
  }

  public void setSowAmt(BigDecimal sowAmt) {
    this.sowAmt = sowAmt;
  }

  public BigDecimal getCash() {
    return cash;
  }

  public void setCash(BigDecimal cash) {
    this.cash = cash;
  }

  public BigDecimal getEssCash() {
    return essCash;
  }

  public void setEssCash(BigDecimal essCash) {
    this.essCash = essCash;
  }

  public BigDecimal getEmeCsh() {
    return emeCsh;
  }

  public void setEmeCsh(BigDecimal emeCsh) {
    this.emeCsh = emeCsh;
  }

  public BigDecimal getHouseLoan() {
    return houseLoan;
  }

  public void setHouseLoan(BigDecimal houseLoan) {
    this.houseLoan = houseLoan;
  }

  public BigDecimal getCreditLoan() {
    return creditLoan;
  }

  public void setCreditLoan(BigDecimal creditLoan) {
    this.creditLoan = creditLoan;
  }

  public BigDecimal getStdLoan() {
    return stdLoan;
  }

  public void setStdLoan(BigDecimal stdLoan) {
    this.stdLoan = stdLoan;
  }

  public BigDecimal getPayForHouse() {
    return payForHouse;
  }

  public void setPayForHouse(BigDecimal payForHouse) {
    this.payForHouse = payForHouse;
  }

  public BigDecimal getPayForCar() {
    return payForCar;
  }

  public void setPayForCar(BigDecimal payForCar) {
    this.payForCar = payForCar;
  }

  public BigDecimal getStudy() {
    return study;
  }

  public void setStudy(BigDecimal study) {
    this.study = study;
  }

  public BigDecimal getTravel() {
    return travel;
  }

  public void setTravel(BigDecimal travel) {
    this.travel = travel;
  }

  public BigDecimal getOther() {
    return other;
  }

  public void setOther(BigDecimal other) {
    this.other = other;
  }

  public BigDecimal getInsSavAmt() {
    return insSavAmt;
  }

  public void setInsSavAmt(BigDecimal insSavAmt) {
    this.insSavAmt = insSavAmt;
  }

  public BigDecimal getInsPolicyAmt() {
    return insPolicyAmt;
  }

  public void setInsPolicyAmt(BigDecimal insPolicyAmt) {
    this.insPolicyAmt = insPolicyAmt;
  }

  public boolean isDirty() {
    return isDirty;
  }

  public void setDirty(boolean isDirty) {
    this.isDirty = isDirty;
  }

	public BigDecimal getMfdProd() {
		return mfdProd;
	}
	
	public void setMfdProd(BigDecimal mfdProd) {
		this.mfdProd = mfdProd;
	}
	
	public BigDecimal getEtfProd() {
		return etfProd;
	}
	
	public void setEtfProd(BigDecimal etfProd) {
		this.etfProd = etfProd;
	}
	
	public BigDecimal getInsProd() {
		return insProd;
	}
	
	public void setInsProd(BigDecimal insProd) {
		this.insProd = insProd;
	}
	
	public BigDecimal getBondProd() {
		return bondProd;
	}
	
	public void setBondProd(BigDecimal bondProd) {
		this.bondProd = bondProd;
	}
	
	public BigDecimal getSnProd() {
		return snProd;
	}
	
	public void setSnProd(BigDecimal snProd) {
		this.snProd = snProd;
	}
	
	public BigDecimal getSiProd() {
		return siProd;
	}
	
	public void setSiProd(BigDecimal siProd) {
		this.siProd = siProd;
	}

	public BigDecimal getCashPrepare() {
		return cashPrepare;
	}

	public void setCashPrepare(BigDecimal cashPrepare) {
		this.cashPrepare = cashPrepare;
	}

	public BigDecimal getLoanExpenses() {
		return loanExpenses;
	}

	public void setLoanExpenses(BigDecimal loanExpenses) {
		this.loanExpenses = loanExpenses;
	}

	public BigDecimal getOtherExpenses() {
		return otherExpenses;
	}

	public void setOtherExpenses(BigDecimal otherExpenses) {
		this.otherExpenses = otherExpenses;
	}

	public BigDecimal getPortfolio2Ratio() {
		return portfolio2Ratio;
	}

	public void setPortfolio2Ratio(BigDecimal portfolio2Ratio) {
		this.portfolio2Ratio = portfolio2Ratio;
	}

	public BigDecimal getPortfolio3Ratio() {
		return portfolio3Ratio;
	}

	public void setPortfolio3Ratio(BigDecimal portfolio3Ratio) {
		this.portfolio3Ratio = portfolio3Ratio;
	}

	public BigDecimal getPortfolio2Amt() {
		return portfolio2Amt;
	}

	public void setPortfolio2Amt(BigDecimal portfolio2Amt) {
		this.portfolio2Amt = portfolio2Amt;
	}

	public BigDecimal getPortfolio3Amt() {
		return portfolio3Amt;
	}

	public void setPortfolio3Amt(BigDecimal portfolio3Amt) {
		this.portfolio3Amt = portfolio3Amt;
	}
	
	
}