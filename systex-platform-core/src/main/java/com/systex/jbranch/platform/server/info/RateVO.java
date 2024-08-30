package com.systex.jbranch.platform.server.info;

public class RateVO {

	private String depCur;
	private String depCurFull;
	private String linkedCur;
	private double refSpotRate;
	private double refOTMRate;
	private int otmbp;
	
	public String getDepCur() {
		return depCur;
	}
	public void setDepCur(String depCur) {
		this.depCur = depCur;
	}
	public String getDepCurFull() {
		return depCurFull;
	}
	public void setDepCurFull(String depCurFull) {
		this.depCurFull = depCurFull;
	}
	public String getLinkedCur() {
		return linkedCur;
	}
	public void setLinkedCur(String linkedCur) {
		this.linkedCur = linkedCur;
	}
	public double getRefSpotRate() {
		return refSpotRate;
	}
	public void setRefSpotRate(double refSpotRate) {
		this.refSpotRate = refSpotRate;
	}
	public double getRefOTMRate() {
		return refOTMRate;
	}
	public void setRefOTMRate(double refOTMRate) {
		this.refOTMRate = refOTMRate;
	}
	public int getOtmbp() {
		return otmbp;
	}
	public void setOtmbp(int otmbp) {
		this.otmbp = otmbp;
	}

}
