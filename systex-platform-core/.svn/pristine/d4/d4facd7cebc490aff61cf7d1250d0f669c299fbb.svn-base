package com.systex.jbranch.platform.common.dataManager;
/**
 * 提供相對於Client端各工作區存取資料的區域。<BR>
 * 
 * @author Eric.Lin
 *
 */


public class Section {
   protected String sectionID;
   protected String luNo;
   protected String txnCode;
   protected String txnName;
   protected java.io.PrintWriter out=null;
   protected ServerTransaction serverTransaction=null;   
   protected ClientTransaction clientTransaction=null;
   protected PlatFormVO platFormVO = new PlatFormVO();
   protected BizlogicVO bizlogicVO = new BizlogicVO();
   /**
    * @return the luNo
	*/
	   
   public String getLuNo() {
	   return this.luNo;
   }
   /**
    * @return the sectionID
    */
   public String getSectionID() {
	   return this.sectionID;
   }
   /**
    * @return the txnCode
    */
   public String getTxnCode() {
	   return this.txnCode;
   }
   /**
    * @return the txnName
    */
   public String getTxnName() {
	   return this.txnName;
   }
   /**
    * @param luNo the luNo to set
    */
   public void setLuNo(String luNo) {
	   this.luNo = luNo;
   }
   /**
    * 設定工作區代號。<br>
    * @param sectionID the sectionID to set
    */
   public void setSectionID(String sectionID) {
	   this.sectionID = sectionID;
   }
   /**
    * @param txnCode the txnCode to set
    */
   public void setTxnCode(String txnCode) {
	   this.txnCode = txnCode;
   }
   /**
    * @param txnName the txnName to set
    */
   public void setTxnName(String txnName) {
	   this.txnName = txnName;
   }
   /**
    * 取得ServerTransaction<br>
    * @return the serverTransaction<br>
    */
   public ServerTransaction getServerTransaction() {
	   if(serverTransaction==null){
		   serverTransaction=new ServerTransaction(); 
	   }	
	   return serverTransaction;	
   }
   /**
    * 設定ServerTransaction。<br>
    * @param serverTransaction the serverTransaction to set
    */
   public void setServerTransaction(ServerTransaction serverTransaction) 
   {   
   		this.serverTransaction = serverTransaction;
   }
   /**
    * 取得輸出物件。<br>
    * @return
    */
   public java.io.PrintWriter getOut() {
   		return this.out;
   }
   /**
    * 設定輸出物件。<br>
    * @param out
    */
   public void setOut(java.io.PrintWriter out) 
   {
   		this.out = out;
   }
   /**
    * 刪除工作站中的ServerTransaction。<br>
    *  @return true:正常刪除;false:不正常刪除。<br>
    */
   public boolean deleteServerTransaction()
   {
   		if(this.serverTransaction==null){
   			return false;
   		} else {
   			this.serverTransaction =null;
   			return true;
   		}
   }
   /**
    * 取得ClientTransaction<br>
    * @return
    */
   public ClientTransaction getClientTransaction() 
   {
   		if (clientTransaction==null){
   			clientTransaction=new ClientTransaction();
   		}
   		return clientTransaction;
   }
   /**
    * 設定clientTransaction。<br>
    * @param clientTransaction<br>
    */
   public void setClientTransaction(ClientTransaction clientTransaction) 
   {
   		this.clientTransaction = clientTransaction;
   }
   /**
    * 刪除ClientTransaction<br>
    *  @return true:正常刪除;false:不正常刪除。<br>
    */
   public boolean deleteClientTransaction()
   {
   		if(this.clientTransaction==null){
   			return false;
   		}else{
   			this.clientTransaction =null;
   			return true;
   		}
   }
	/**
	 * @return the platFormVO
	 */
	public PlatFormVO getPlatFormVO() {
		return platFormVO;
	}
	/**
	 * @param platFormVO the platFormVO to set
	 */
	public void setPlatFormVO(PlatFormVO platFormVO) {
		this.platFormVO = platFormVO;
	}
	/**
	 * @return the bizlogicVO
	 */
	public BizlogicVO getBizlogicVO() {
		return bizlogicVO;
	}
	/**
	 * @param bizlogicVO the bizlogicVO to set
	 */
	public void setBizlogicVO(BizlogicVO bizlogicVO) {
		this.bizlogicVO = bizlogicVO;
	}
	   
	   
}