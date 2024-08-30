package com.systex.jbranch.app.server.fps.crm341;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM341OutputVO extends PagingOutputVO {
	private List resultList;
	private List ao_list;
	private String resultList2;
	private boolean isCMDTCust; //是否為十保客戶ID監控
	private String CMDT2022CUST1_YN; //是否為：2022必輪調名單：RM輪調後，帶走30%核心客戶，一年內不得再帶走該RM轄下原分行70%客戶
	private String CMDT2022CUST2_YN; //是否為：2022必輪調名單：輪調後帶至新分行之核心客戶，依客戶意願欲轉回原分行
	private String CMDT2022CUST3_YN; //是否為：2022換手名單6個月內要移回原理專的客戶
	private String CMDT2023CUST1_YN; //是否為：2023必輪調名單：必輪調RM名單上傳後，名單中RM不可做移入申請
	private String CMDT2023CUST2_YN; //是否為：2023必輪調名單：必輪調RM名單上傳後，名單中客戶不可做移入申請
	private String CMDT2023CUST3_YN; //是否為：2023必輪調名單：非區域分行，RM輪調後，帶走30%核心客戶，一年內不得再帶走該RM轄下原分行70%客戶
	private String CMDT2023CUST4_YN; //是否為：2023必輪調名單：區域分行非核心客戶移回原理專
	private String CMDT2023CUST5_YN; //是否為：2023必輪調名單：非區域分行，輪調後帶至新分行之核心客戶，依客戶意願欲轉回原分行
	
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public String getResultList2() {
		return resultList2;
	}
	public void setResultList2(String resultList2) {
		this.resultList2 = resultList2;
	}
	public List getAo_list() {
		return ao_list;
	}
	public void setAo_list(List ao_list) {
		this.ao_list = ao_list;
	}
	public boolean isCMDTCust() {
		return isCMDTCust;
	}
	public void setCMDTCust(boolean isCMDTCust) {
		this.isCMDTCust = isCMDTCust;
	}
	public String getCMDT2022CUST1_YN() {
		return CMDT2022CUST1_YN;
	}
	public void setCMDT2022CUST1_YN(String cMDT2022CUST1_YN) {
		CMDT2022CUST1_YN = cMDT2022CUST1_YN;
	}
	public String getCMDT2022CUST2_YN() {
		return CMDT2022CUST2_YN;
	}
	public void setCMDT2022CUST2_YN(String cMDT2022CUST2_YN) {
		CMDT2022CUST2_YN = cMDT2022CUST2_YN;
	}
	public String getCMDT2022CUST3_YN() {
		return CMDT2022CUST3_YN;
	}
	public void setCMDT2022CUST3_YN(String cMDT2022CUST3_YN) {
		CMDT2022CUST3_YN = cMDT2022CUST3_YN;
	}
	public String getCMDT2023CUST1_YN() {
		return CMDT2023CUST1_YN;
	}
	public void setCMDT2023CUST1_YN(String cMDT2023CUST1_YN) {
		CMDT2023CUST1_YN = cMDT2023CUST1_YN;
	}
	public String getCMDT2023CUST2_YN() {
		return CMDT2023CUST2_YN;
	}
	public void setCMDT2023CUST2_YN(String cMDT2023CUST2_YN) {
		CMDT2023CUST2_YN = cMDT2023CUST2_YN;
	}
	public String getCMDT2023CUST3_YN() {
		return CMDT2023CUST3_YN;
	}
	public void setCMDT2023CUST3_YN(String cMDT2023CUST3_YN) {
		CMDT2023CUST3_YN = cMDT2023CUST3_YN;
	}
	public String getCMDT2023CUST4_YN() {
		return CMDT2023CUST4_YN;
	}
	public void setCMDT2023CUST4_YN(String cMDT2023CUST4_YN) {
		CMDT2023CUST4_YN = cMDT2023CUST4_YN;
	}
	public String getCMDT2023CUST5_YN() {
		return CMDT2023CUST5_YN;
	}
	public void setCMDT2023CUST5_YN(String cMDT2023CUST5_YN) {
		CMDT2023CUST5_YN = cMDT2023CUST5_YN;
	}
}
