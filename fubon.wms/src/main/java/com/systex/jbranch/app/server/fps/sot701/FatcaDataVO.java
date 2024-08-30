package com.systex.jbranch.app.server.fps.sot701;

import java.util.Date;

/**
 * Created by James on 2018/2/21.
 */
public class FatcaDataVO {
    private String fatcaType;   //FatcaType
    private String IDF_N;		//目前FATCA身分
    private String IDF_S;		//辨識狀態
    private String IDF_P;		//前次辨識身分

    public String getFatcaType() {
        return fatcaType;
    }

    public void setFatcaType(String fatcaType) {
        this.fatcaType = fatcaType;
    }

    public String getIDF_N() {
		return IDF_N;
	}

	public void setIDF_N(String iDF_N) {
		IDF_N = iDF_N;
	}

	public String getIDF_S() {
		return IDF_S;
	}

	public void setIDF_S(String iDF_S) {
		IDF_S = iDF_S;
	}

	public String getIDF_P() {
		return IDF_P;
	}

	public void setIDF_P(String iDF_P) {
		IDF_P = iDF_P;
	}

	@Override
	public String toString() {
		return "FatcaDataVO [fatcaType=" + fatcaType + ", IDF_N=" + IDF_N
				+ ", IDF_S=" + IDF_S + ", IDF_P=" + IDF_P + "]";
	}
	
}
