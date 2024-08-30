package com.systex.jbranch.app.server.fps.prd140;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD140InputVO extends PagingInputVO {
	private String type;
	private String cust_id;
	private String sn_id;
	private String sn_name;
	private String sn_type;
	private String currency;
	private String maturity;
	private String rate_guaran;
	private String risk_level;
	private String pi_YN;
	private String hnwc_YN;
	private String obu_YN;
	private String prod_type;
	private List<Map<String, String>> downloadList;
	private String from;
	private String trustTS; //特金:S 金錢信託:M

	private String snCustLevel;
	private String snProject;

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getSn_id() {
		return sn_id;
	}
	public void setSn_id(String sn_id) {
		this.sn_id = sn_id;
	}
	public String getSn_name() {
		return sn_name;
	}
	public void setSn_name(String sn_name) {
		this.sn_name = sn_name;
	}
	public String getSn_type() {
		return sn_type;
	}
	public void setSn_type(String sn_type) {
		this.sn_type = sn_type;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getMaturity() {
		return maturity;
	}
	public void setMaturity(String maturity) {
		this.maturity = maturity;
	}
	public String getRate_guaran() {
		return rate_guaran;
	}
	public void setRate_guaran(String rate_guaran) {
		this.rate_guaran = rate_guaran;
	}
	public String getRisk_level() {
		return risk_level;
	}
	public void setRisk_level(String risk_level) {
		this.risk_level = risk_level;
	}
	public String getPi_YN() {
		return pi_YN;
	}
	public void setPi_YN(String pi_YN) {
		this.pi_YN = pi_YN;
	}
	public String getHnwc_YN() {
		return hnwc_YN;
	}
	public void setHnwc_YN(String hnwc_YN) {
		this.hnwc_YN = hnwc_YN;
	}
	public String getObu_YN() {
		return obu_YN;
	}
	public void setObu_YN(String obu_YN) {
		this.obu_YN = obu_YN;
	}
	public String getProd_type() {
		return prod_type;
	}
	public void setProd_type(String prod_type) {
		this.prod_type = prod_type;
	}
	public List<Map<String, String>> getDownloadList() {
		return downloadList;
	}
	public void setDownloadList(List<Map<String, String>> downloadList) {
		this.downloadList = downloadList;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTrustTS() {
		return trustTS;
	}
	public void setTrustTS(String trustTS) {
		this.trustTS = trustTS;
	}

	public String getSnCustLevel() {
		return snCustLevel;
	}

	public void setSnCustLevel(String snCustLevel) {
		this.snCustLevel = snCustLevel;
	}

	public String getSnProject() {
		return snProject;
	}

	public void setSnProject(String snProject) {
		this.snProject = snProject;
	}
}
