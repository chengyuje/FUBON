package com.systex.jbranch.fubon.commons.mplus.cons;

public class MPlusAlterEmployeeCons {
	public static final String STAGING = "https://stagenterprise.mplusapp.com/MplusEIMAPI/";
//	public static final String PROD = "https://enterprise.mplusapp.com/MplusEIMAPI/";
	public static final String PROD = "https://175.99.67.70/MplusEIMAPI/";
	public static final String CHENGE_TOKEN = "#change";
	public static final String ALERT_ENPOLYEE_URL = CHENGE_TOKEN + "AlterEmployee";//員工名單新增/異動
	public static final String DELETE_ENPOLYEE_URL = CHENGE_TOKEN + "DeleteEmployee";//工名單刪除
	public static final String ALERT_GROUP_URL = CHENGE_TOKEN + "AlterGroup";//異動公司群組
	public static final String ALERT_GROUP_MEMBERS_URL = CHENGE_TOKEN + "AlterGroupMembers";//群組異動成員
	public static final String COUNT_EA_MENBER_URL = CHENGE_TOKEN + "CountEaMember";//員工總數
}
