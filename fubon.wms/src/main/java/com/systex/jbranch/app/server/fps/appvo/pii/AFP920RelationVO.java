package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.math.BigDecimal;

public class AFP920RelationVO implements Serializable {
	public AFP920RelationVO(){
		super();
	}
	
	private String updateStatus; 	//異動狀態
	private String layer1;		//第一層
	private String layer2;		//第二層
	private String layer3;		//第三層
	private String layer4;		//第四層
	private String defTarget;	//投資策略
	private BigDecimal defReturn;	//預估報酬率
	
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
	public String getDefTarget() {
		return defTarget;
	}
	public void setDefTarget(String defTarget) {
		this.defTarget = defTarget;
	}
	public BigDecimal getDefReturn() {
		return defReturn;
	}
	public void setDefReturn(BigDecimal defReturn) {
		this.defReturn = defReturn;
	}
}
