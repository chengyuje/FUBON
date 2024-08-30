package com.systex.jbranch.app.server.fps.crm411;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM411InputVO extends PagingInputVO{
	private String PROD_TYPE;
	private String CON_DEGREE;
	private String LEVEL_NO;
	private String ROLE_LIST;
	private BigDecimal DISCOUNT_a;
	private BigDecimal DISCOUNT_b;
	private BigDecimal DISCOUNT_c;
	private BigDecimal DISCOUNT_d;
	private String DISCOUNT_RNG_TYPE_a;
	private String DISCOUNT_RNG_TYPE_b;
	private String DISCOUNT_RNG_TYPE_c;
	private String DISCOUNT_RNG_TYPE_d;
	private List<Map<String, Object>> role_List;
	
	private List<Map<String, Object>> roleList;
	private List<Map<String, Object>> roleList_a;
	private List<Map<String, Object>> roleList_b;
	private List<Map<String, Object>> roleList_c;
	private List<Map<String, Object>> prodType;
	private List<Map<String, Object>> conList;
	
	private String setupType;
	
	public String getSetupType() {
		return setupType;
	}
	public void setSetupType(String setupType) {
		this.setupType = setupType;
	}
	public String getPROD_TYPE() {
		return PROD_TYPE;
	}
	public void setPROD_TYPE(String pROD_TYPE) {
		PROD_TYPE = pROD_TYPE;
	}
	public String getCON_DEGREE() {
		return CON_DEGREE;
	}
	public void setCON_DEGREE(String cON_DEGREE) {
		CON_DEGREE = cON_DEGREE;
	}
	public String getLEVEL_NO() {
		return LEVEL_NO;
	}
	public void setLEVEL_NO(String lEVEL_NO) {
		LEVEL_NO = lEVEL_NO;
	}
	public String getROLE_LIST() {
		return ROLE_LIST;
	}
	public void setROLE_LIST(String rOLE_LIST) {
		ROLE_LIST = rOLE_LIST;
	}
	
	public BigDecimal getDISCOUNT_a() {
		return DISCOUNT_a;
	}
	public void setDISCOUNT_a(BigDecimal dISCOUNT_a) {
		DISCOUNT_a = dISCOUNT_a;
	}
	public BigDecimal getDISCOUNT_b() {
		return DISCOUNT_b;
	}
	public void setDISCOUNT_b(BigDecimal dISCOUNT_b) {
		DISCOUNT_b = dISCOUNT_b;
	}
	public BigDecimal getDISCOUNT_c() {
		return DISCOUNT_c;
	}
	public void setDISCOUNT_c(BigDecimal dISCOUNT_c) {
		DISCOUNT_c = dISCOUNT_c;
	}
	public BigDecimal getDISCOUNT_d() {
		return DISCOUNT_d;
	}
	public void setDISCOUNT_d(BigDecimal dISCOUNT_d) {
		DISCOUNT_d = dISCOUNT_d;
	}
	public String getDISCOUNT_RNG_TYPE_a() {
		return DISCOUNT_RNG_TYPE_a;
	}
	public void setDISCOUNT_RNG_TYPE_a(String dISCOUNT_RNG_TYPE_a) {
		DISCOUNT_RNG_TYPE_a = dISCOUNT_RNG_TYPE_a;
	}
	public String getDISCOUNT_RNG_TYPE_b() {
		return DISCOUNT_RNG_TYPE_b;
	}
	public void setDISCOUNT_RNG_TYPE_b(String dISCOUNT_RNG_TYPE_b) {
		DISCOUNT_RNG_TYPE_b = dISCOUNT_RNG_TYPE_b;
	}
	public String getDISCOUNT_RNG_TYPE_c() {
		return DISCOUNT_RNG_TYPE_c;
	}
	public void setDISCOUNT_RNG_TYPE_c(String dISCOUNT_RNG_TYPE_c) {
		DISCOUNT_RNG_TYPE_c = dISCOUNT_RNG_TYPE_c;
	}
	public String getDISCOUNT_RNG_TYPE_d() {
		return DISCOUNT_RNG_TYPE_d;
	}
	public void setDISCOUNT_RNG_TYPE_d(String dISCOUNT_RNG_TYPE_d) {
		DISCOUNT_RNG_TYPE_d = dISCOUNT_RNG_TYPE_d;
	}
	public List<Map<String, Object>> getRoleList() {
		return roleList;
	}
	public void setRoleList(List<Map<String, Object>> roleList) {
		this.roleList = roleList;
	}
	public List<Map<String, Object>> getProdType() {
		return prodType;
	}
	public void setProdType(List<Map<String, Object>> prodType) {
		this.prodType = prodType;
	}
	public List<Map<String, Object>> getConList() {
		return conList;
	}
	public void setConList(List<Map<String, Object>> conList) {
		this.conList = conList;
	}
	public List<Map<String, Object>> getRole_List() {
		return role_List;
	}
	public void setRole_List(List<Map<String, Object>> role_List) {
		this.role_List = role_List;
	}
	public List<Map<String, Object>> getRoleList_a() {
		return roleList_a;
	}
	public void setRoleList_a(List<Map<String, Object>> roleList_a) {
		this.roleList_a = roleList_a;
	}
	public List<Map<String, Object>> getRoleList_b() {
		return roleList_b;
	}
	public void setRoleList_b(List<Map<String, Object>> roleList_b) {
		this.roleList_b = roleList_b;
	}
	public List<Map<String, Object>> getRoleList_c() {
		return roleList_c;
	}
	public void setRoleList_c(List<Map<String, Object>> roleList_c) {
		this.roleList_c = roleList_c;
	}
	
	
}
