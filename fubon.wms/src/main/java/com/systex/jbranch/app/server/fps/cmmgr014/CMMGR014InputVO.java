package com.systex.jbranch.app.server.fps.cmmgr014;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;



public class CMMGR014InputVO extends PagingInputVO{
	
	public CMMGR014InputVO()
	{
	}
	
	public String tipFtpSettingId;
	public String tipSrcDirectory;
	public String tipSrcFileName;
	public String tipCheckFile;
	public String tipDesDirectory;
	public String tipDesFileName;
	public Integer tipRepeat;
	public Integer tipRepeatInterval;
	public String cmbHostId;

	public String operType;
	public String srcDelete;
	
	public String encryptKey;
	public String operKeyType;
	
	
	

	public String getTipFtpSettingId() {
		return tipFtpSettingId;
	}
	public void setTipFtpSettingId(String tipFtpSettingId) {
		this.tipFtpSettingId = tipFtpSettingId;
	}
	public String getTipSrcDirectory() {
		return tipSrcDirectory;
	}
	public void setTipSrcDirectory(String tipSrcDirectory) {
		this.tipSrcDirectory = tipSrcDirectory;
	}
	public String getTipSrcFileName() {
		return tipSrcFileName;
	}
	public void setTipSrcFileName(String tipSrcFileName) {
		this.tipSrcFileName = tipSrcFileName;
	}
	public String getTipCheckFile() {
		return tipCheckFile;
	}
	public void setTipCheckFile(String tipCheckFile) {
		this.tipCheckFile = tipCheckFile;
	}
	public String getTipDesDirectory() {
		return tipDesDirectory;
	}
	public void setTipDesDirectory(String tipDesDirectory) {
		this.tipDesDirectory = tipDesDirectory;
	}
	public String getTipDesFileName() {
		return tipDesFileName;
	}
	public void setTipDesFileName(String tipDesFileName) {
		this.tipDesFileName = tipDesFileName;
	}
	public Integer getTipRepeat() {
		return tipRepeat;
	}
	public void setTipRepeat(Integer tipRepeat) {
		this.tipRepeat = tipRepeat;
	}
	public Integer getTipRepeatInterval() {
		return tipRepeatInterval;
	}
	public void setTipRepeatInterval(Integer tipRepeatInterval) {
		this.tipRepeatInterval = tipRepeatInterval;
	}
	public String getCmbHostId() {
		return cmbHostId;
	}
	public void setCmbHostId(String cmbHostId) {
		this.cmbHostId = cmbHostId;
	}
	public String getOperType() {
		return operType;
	}
	public void setOperType(String operType) {
		this.operType = operType;
	}
	public String getSrcDelete() {
		return srcDelete;
	}
	public void setSrcDelete(String srcDelete) {
		this.srcDelete = srcDelete;
	}
	public String getEncryptKey() {
		return encryptKey;
	}
	public void setEncryptKey(String encryptKey) {
		this.encryptKey = encryptKey;
	}
	public String getOperKeyType() {
		return operKeyType;
	}
	public void setOperKeyType(String operKeyType) {
		this.operKeyType = operKeyType;
	}
	
}
