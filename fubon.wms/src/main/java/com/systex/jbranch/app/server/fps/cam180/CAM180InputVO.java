package com.systex.jbranch.app.server.fps.cam180;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CAM180InputVO extends PagingInputVO {
	
	private String bCode;
	private String pCode;
	private String id;
	private String name;
	private String from;
	private String modifier;
	private String force;
	private Date sDate;
	private Date eDate;
	private Date sEDate;
	private Date eEDate;
	private List<Map<String, Object>> query_list;
	private String tabType;

	public String getTabType() {
		return tabType;
	}

	public void setTabType(String tabType) {
		this.tabType = tabType;
	}

	public String getbCode() {
		return bCode;
	}

	public void setbCode(String bCode) {
		this.bCode = bCode;
	}

	public String getpCode() {
		return pCode;
	}

	public void setpCode(String pCode) {
		this.pCode = pCode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getForce() {
		return force;
	}

	public void setForce(String force) {
		this.force = force;
	}

	public Date getsDate() {
		return sDate;
	}

	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}

	public Date geteDate() {
		return eDate;
	}

	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}

	public Date getsEDate() {
		return sEDate;
	}

	public void setsEDate(Date sEDate) {
		this.sEDate = sEDate;
	}

	public Date geteEDate() {
		return eEDate;
	}

	public void seteEDate(Date eEDate) {
		this.eEDate = eEDate;
	}

	public List<Map<String, Object>> getQuery_list() {
		return query_list;
	}

	public void setQuery_list(List<Map<String, Object>> query_list) {
		this.query_list = query_list;
	}
}