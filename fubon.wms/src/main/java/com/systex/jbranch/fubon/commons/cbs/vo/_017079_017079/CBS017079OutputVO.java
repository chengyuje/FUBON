package com.systex.jbranch.fubon.commons.cbs.vo._017079_017079;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS017079OutputVO {

	@XmlElement(name = "TxRepeat")
	private List<CBS017079OutputDetailsVO> details;

	private String accntNumber1;
	private String term1;
	private String term2;
	private String opt;
	private String paccntNumber1;
	private String pterm1;
	private String pterm2;
	private String popt;
	private String endflag;
	private String prepayamount;
	private String EndTxn;
	private String cycleno1;
	private String baddebtind1;
	private String OffsetsFiller1;
	private String YrTerm;
	
	

	public List<CBS017079OutputDetailsVO> getDetails() {
		return details;
	}

	public void setDetails(List<CBS017079OutputDetailsVO> details) {
		this.details = details;
	}

	public String getAccntNumber1() {
		return accntNumber1;
	}

	public void setAccntNumber1(String accntNumber1) {
		this.accntNumber1 = accntNumber1;
	}

	public String getTerm1() {
		return term1;
	}

	public void setTerm1(String term1) {
		this.term1 = term1;
	}

	public String getTerm2() {
		return term2;
	}

	public void setTerm2(String term2) {
		this.term2 = term2;
	}

	public String getOpt() {
		return opt;
	}

	public void setOpt(String opt) {
		this.opt = opt;
	}

	public String getPaccntNumber1() {
		return paccntNumber1;
	}

	public void setPaccntNumber1(String paccntNumber1) {
		this.paccntNumber1 = paccntNumber1;
	}

	public String getPterm1() {
		return pterm1;
	}

	public void setPterm1(String pterm1) {
		this.pterm1 = pterm1;
	}

	public String getPterm2() {
		return pterm2;
	}

	public void setPterm2(String pterm2) {
		this.pterm2 = pterm2;
	}

	public String getPopt() {
		return popt;
	}

	public void setPopt(String popt) {
		this.popt = popt;
	}

	public String getEndflag() {
		return endflag;
	}

	public void setEndflag(String endflag) {
		this.endflag = endflag;
	}

	public String getPrepayamount() {
		return prepayamount;
	}

	public void setPrepayamount(String prepayamount) {
		this.prepayamount = prepayamount;
	}

	public String getEndTxn() {
		return EndTxn;
	}

	public void setEndTxn(String endTxn) {
		EndTxn = endTxn;
	}

	public String getCycleno1() {
		return cycleno1;
	}

	public void setCycleno1(String cycleno1) {
		this.cycleno1 = cycleno1;
	}

	public String getBaddebtind1() {
		return baddebtind1;
	}

	public void setBaddebtind1(String baddebtind1) {
		this.baddebtind1 = baddebtind1;
	}

	public String getOffsetsFiller1() {
		return OffsetsFiller1;
	}

	public void setOffsetsFiller1(String offsetsFiller1) {
		OffsetsFiller1 = offsetsFiller1;
	}

	public String getYrTerm() {
		return YrTerm;
	}

	public void setYrTerm(String yrTerm) {
		YrTerm = yrTerm;
	}

}
