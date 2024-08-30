package com.systex.jbranch.platform.common.dataManager;
/**
 * @code-review
 *
 * @author Administrator
 *
 */
public class User {
	protected String userID;
	protected String userPwd;
	protected String userName;
	protected String userAuth;
	protected String status;
	protected String section;
	protected String errLevel;
	protected String level;
    private String proxiedUserID;
    private String proxiedUserAuth;
	private PlatFormVO platFormVO = new PlatFormVO();
    private BizlogicVO bizlogicVO = new BizlogicVO();
    protected String userCifID;
    protected String userCode;
    protected String userAcctNo;
	private String currentUserId;

    public PlatFormVO getPlatFormVO() {
        return platFormVO;
    }

    public void setPlatFormVO(PlatFormVO platFormVO) {
        this.platFormVO = platFormVO;
    }

    public BizlogicVO getBizlogicVO() {
        return bizlogicVO;
    }

    public void setBizlogicVO(BizlogicVO bizlogicVO) {
        this.bizlogicVO = bizlogicVO;
    }

    public String getUserAuth() {
		return userAuth;
	}
	public void setUserAuth(String userAuth) {
		this.userAuth = userAuth;
	}
	/**
	 * 取得錯誤層級。<br>
	 * @return<br>
	 */
	public String getErrLevel() {
		return errLevel;
	}
	/**
	 * 設定錯誤層級。<br>
	 * @param errLevel<br>
	 */
	public void setErrLevel(String errLevel) {
		this.errLevel = errLevel;
	}
	/**
	 *
	 * @return the section
	 */
	public String getSection() {
		return section;
	}
	/**
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 取得使用者代號。<br>
	 * @return the userID<br>
	 */
	public String getUserID() {
		return userID;
	}
	/**
	 * 取得使用者名稱。<br>
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
   /**
	* @param section the section to set
    */
	public void setSection(String section) {
		this.section = section;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 設定使用者代號<br>
	 * @param userID the userID to set
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}
	/**
	 * 設定使用者名稱。<br>
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * 取得登入層級。<br>
	 * @return<br>
	 */
	public String getLevel() {
		return level;
	}
	/**
	 * 設定登入層級。<br>
	 * @param level<br>
	 */
	public void setLevel(String level) {
		this.level = level;
	}
	/**
	 * 取得登入密碼。<br>
	 * @return<br>
	 */
	public String getUserPwd() {
		return userPwd;
	}
	/**
	 * 設定登入密碼。<br>
	 * @param userPwd<br>
	 */
	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

    public String getProxiedUserAuth() {
        return proxiedUserAuth;
    }

    public void setProxiedUserAuth(String proxiedUserAuth) {
        this.proxiedUserAuth = proxiedUserAuth;
    }

    public String getProxiedUserID() {
        return proxiedUserID;
    }

    public void setProxiedUserID(String proxiedUserID) {
        this.proxiedUserID = proxiedUserID;
    }

    public boolean isAgent() {
        return proxiedUserID != null;
    }

    public String getActualUserAuth() {
        return isAgent() ? getProxiedUserAuth() : getUserAuth();
    }

    public String getActualUserID() {
        return isAgent() ? getProxiedUserID() : getUserID();
    }

	public String getUserCifID() {
		return userCifID;
	}

	public void setUserCifID(String userCifID) {
		this.userCifID = userCifID;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUserAcctNo() {
		return userAcctNo;
	}

	public void setUserAcctNo(String userAcctNo) {
		this.userAcctNo = userAcctNo;
	}

	public String getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(String currentUserId) {
		this.currentUserId = currentUserId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [userID=");
		builder.append(userID);
		builder.append(", userPwd=");
		builder.append(userPwd);
		builder.append(", userName=");
		builder.append(userName);
		builder.append(", userAuth=");
		builder.append(userAuth);
		builder.append(", status=");
		builder.append(status);
		builder.append(", section=");
		builder.append(section);
		builder.append(", errLevel=");
		builder.append(errLevel);
		builder.append(", level=");
		builder.append(level);
		builder.append(", proxiedUserID=");
		builder.append(proxiedUserID);
		builder.append(", proxiedUserAuth=");
		builder.append(proxiedUserAuth);
		builder.append(", userCifID=");
		builder.append(userCifID);
		builder.append(", userCode=");
		builder.append(userCode);
		builder.append(", userAcctNo=");
		builder.append(userAcctNo);
		builder.append(", currentUserId=");
		builder.append(currentUserId);
		builder.append("]");
		return builder.toString();
	}
}
