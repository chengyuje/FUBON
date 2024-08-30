package com.systex.jbranch.app.server.fps.sot701;

import java.util.Date;

/**
 * Created by SebastianWu on 2016/9/9.
 */
public class W8BenDataVO {
    private String w8BenEffYN;  //W8ben簽署是否有效 Y：有效 N：逾期
    private String fatcaType;   //FatcaType
    private Date effDate;       //W8ben有效日期
    private String IDF_N;		//目前FATCA身分
    private String IDF_S;		//辨識狀態
    private String IDF_P;		//前次辨識身分
    
    //20191120 從原本主階加入
    private Date w8benStartDate; //W8ben簽署起日
    private Date w8benEndDate;   //W8ben簽署迄日

    
    public Date getW8benStartDate() {
		return w8benStartDate;
	}

	public void setW8benStartDate(Date w8benStartDate) {
		this.w8benStartDate = w8benStartDate;
	}

	public Date getW8benEndDate() {
		return w8benEndDate;
	}

	public void setW8benEndDate(Date w8benEndDate) {
		this.w8benEndDate = w8benEndDate;
	}

	public String getW8BenEffYN() {
        return w8BenEffYN;
    }

    public void setW8BenEffYN(String w8BenEffYN) {
        this.w8BenEffYN = w8BenEffYN;
    }

    public String getFatcaType() {
        return fatcaType;
    }

    public void setFatcaType(String fatcaType) {
        this.fatcaType = fatcaType;
    }

    public Date getEffDate() {
        return effDate;
    }

    public void setEffDate(Date effDate) {
        this.effDate = effDate;
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
        return "W8BenDataVO{" +
                "w8BenEffYN='" + w8BenEffYN + '\'' +
                ", fatcaType='" + fatcaType + '\'' +
                ", effDate=" + effDate + '\'' +
                ", IDF_N=" + IDF_N + '\'' +
                ", IDF_S=" + IDF_S + '\'' +
                ", IDF_P=" + IDF_P + 
                '}';
    }
}
