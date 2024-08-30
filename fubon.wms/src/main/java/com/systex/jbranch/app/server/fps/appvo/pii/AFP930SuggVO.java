package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;

public class AFP930SuggVO implements Serializable{
	public AFP930SuggVO(){
		super();
	}
	
	private String updateStatus; 	//異動狀態
	private String layer1;		//第一層
	private String layer2;		//第二層
	private String layer3;		//第三層
	private String layer4;		//第四層
	private String investSugg;  	//投資建議
	
	public String getUpdateStatus() {
		return updateStatus;
	}
	public void setUpdateStatus(String updateStatus) {
		this.updateStatus = updateStatus;
	}
	public String getLayer1() {
		return layer1;
	}
	public void setLayer1(String layer1) {
		this.layer1 = layer1;
	}
	public String getLayer2() {
		return layer2;
	}
	public void setLayer2(String layer2) {
		this.layer2 = layer2;
	}
	public String getLayer3() {
		return layer3;
	}
	public void setLayer3(String layer3) {
		this.layer3 = layer3;
	}
	public String getLayer4() {
		return layer4;
	}
	public void setLayer4(String layer4) {
		this.layer4 = layer4;
	}
	public String getInvestSugg() {
		return investSugg;
	}
	public void setInvestSugg(String investSugg) {
		this.investSugg = investSugg;
	}
}
