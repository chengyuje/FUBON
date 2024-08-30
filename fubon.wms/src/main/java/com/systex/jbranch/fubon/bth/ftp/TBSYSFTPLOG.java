package com.systex.jbranch.fubon.bth.ftp;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class TBSYSFTPLOG {
	private BigDecimal SEQ;
	private String FTPSETTINGID; 
	private String SRCDIRECTORY; 
	private String SRCFILENAME; 
	private String CHECKFILE; 
	private String DESDIRECTORY; 
	private String DESFILENAME; 
	private String HOSTID; 
	private String IP; 
	private int PORT; 
	private String USERNAME; 
	private String PROCESSOR; 
	private String OPERATOR;
	private String RESULT;
	private String CREATOR; 
	private Timestamp CREATETIME;
	private Timestamp FINISHTIME;
	public BigDecimal getSEQ() {
		return SEQ;
	}
	public void setSEQ(BigDecimal sEQ) {
		SEQ = sEQ;
	}
	public String getFTPSETTINGID() {
		return FTPSETTINGID;
	}
	public void setFTPSETTINGID(String fTPSETTINGID) {
		FTPSETTINGID = fTPSETTINGID;
	}
	public String getSRCDIRECTORY() {
		return SRCDIRECTORY;
	}
	public void setSRCDIRECTORY(String sRCDIRECTORY) {
		SRCDIRECTORY = sRCDIRECTORY;
	}
	public String getSRCFILENAME() {
		return SRCFILENAME;
	}
	public void setSRCFILENAME(String sRCFILENAME) {
		SRCFILENAME = sRCFILENAME;
	}
	public String getCHECKFILE() {
		return CHECKFILE;
	}
	public void setCHECKFILE(String cHECKFILE) {
		CHECKFILE = cHECKFILE;
	}
	public String getDESDIRECTORY() {
		return DESDIRECTORY;
	}
	public void setDESDIRECTORY(String dESDIRECTORY) {
		DESDIRECTORY = dESDIRECTORY;
	}
	public String getDESFILENAME() {
		return DESFILENAME;
	}
	public void setDESFILENAME(String dESFILENAME) {
		DESFILENAME = dESFILENAME;
	}
	public String getHOSTID() {
		return HOSTID;
	}
	public void setHOSTID(String hOSTID) {
		HOSTID = hOSTID;
	}
	public String getIP() {
		return IP;
	}
	public void setIP(String iP) {
		IP = iP;
	}
	public int getPORT() {
		return PORT;
	}
	public void setPORT(int pORT) {
		PORT = pORT;
	}
	public String getUSERNAME() {
		return USERNAME;
	}
	public void setUSERNAME(String uSERNAME) {
		USERNAME = uSERNAME;
	}
	public String getPROCESSOR() {
		return PROCESSOR;
	}
	public void setPROCESSOR(String pROCESSOR) {
		PROCESSOR = pROCESSOR;
	}
	public String getOPERATOR() {
		return OPERATOR;
	}
	public void setOPERATOR(String oPERATOR) {
		OPERATOR = oPERATOR;
	}
	public String getRESULT() {
		return RESULT;
	}
	public void setRESULT(String rESULT) {
		RESULT = rESULT;
	}
	public String getCREATOR() {
		return CREATOR;
	}
	public void setCREATOR(String cREATOR) {
		CREATOR = cREATOR;
	}
	public Timestamp getCREATETIME() {
		return CREATETIME;
	}
	public void setCREATETIME(Timestamp cREATETIME) {
		CREATETIME = cREATETIME;
	}
	public Timestamp getFINISHTIME() {
		return FINISHTIME;
	}
	public void setFINISHTIME(Timestamp fINISHTIME) {
		FINISHTIME = fINISHTIME;
	} 
}
