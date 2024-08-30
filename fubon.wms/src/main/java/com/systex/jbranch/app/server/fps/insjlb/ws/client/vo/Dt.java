package com.systex.jbranch.app.server.fps.insjlb.ws.client.vo;
public class Dt {
	private byte[] BinaryData;
	private java.lang.String ViceFile;
	private int Version;
	public void setBinaryData (byte[] BinaryData){
		this.BinaryData = BinaryData;
	}
	public byte[] getBinaryData(){
		return this.BinaryData;
	}
	public void setViceFile (java.lang.String ViceFile){
		this.ViceFile = ViceFile;
	}
	public java.lang.String getViceFile(){
		return this.ViceFile;
	}
	public void setVersion (int Version){
		this.Version = Version;
	}
	public int getVersion(){
		return this.Version;
	}
}