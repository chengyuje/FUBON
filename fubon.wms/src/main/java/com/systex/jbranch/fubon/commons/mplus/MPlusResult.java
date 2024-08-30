package com.systex.jbranch.fubon.commons.mplus;


public class MPlusResult{	
	private String result;
	private String text;
	private String groupId;
	private String cause;
	private AlterParam[] successList;
	private AlterParam[] failList;
	private Integer count;
	
	

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public AlterParam[] getSuccessList() {
		return successList;
	}

	public void setSuccessList(AlterParam[] successList) {
		this.successList = successList;
	}

	public AlterParam[] getFailList() {
		return failList;
	}

	public void setFailList(AlterParam[] failList) {
		this.failList = failList;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}



	public class AlterParam{
		private String alterParam;
		private String cause;
		
		public String getAlterParam() {
			return alterParam;
		}
		public void setAlterParam(String alterParam) {
			this.alterParam = alterParam;
		}
		public String getCause() {
			return cause;
		}
		public void setCause(String cause) {
			this.cause = cause;
		}
	}
}
