package com.systex.jbranch.platform.common.dataaccess.vo;

import org.apache.commons.lang.builder.ToStringBuilder;
import java.sql.Timestamp;

/**
 * 此類別為所有平台內VO的父類別，定義了所有table所共有的欄位
 *
 */
public class VOBase implements Cloneable{

	protected Long   version;
	protected String creator;
	protected Timestamp createtime;
	protected Timestamp lastupdate;
	protected String modifier;
	
	/**
	 * 取得table uid
	 * @return
	 */
	public String getTableuid(){ return null; }
	

	
	/**
	 * 取得資料版本
	 * (此欄位由hibernate控制)
	 * @return
	 */
	public Long getVersion() {
		return version;
	}
	
	/**
	 * 設定資料版本
	 * (勿直接呼叫此method，資料版本由hibernate控制)
	 * @param version
	 */
	public void setVersion(Long version) {
		this.version = version;
	}
	
	/**
	 * 取得建立者
	 * @return
	 */
	public String getCreator() {
		return creator;
	}
	
	/**
	 * 設定建立者
	 * @param creator
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	/**
	 * 取得建立時間
	 * @return
	 */
	public Timestamp getCreatetime() {
		return createtime;
	}
	
	/**
	 * 設定建立時間
	 * @param createtime
	 */
	public void setCreatetime(Timestamp createtime) {
		this.createtime = createtime;
	}
	
	/**
	 * 取得最後更新時間
	 * @return
	 */
	public Timestamp getLastupdate() {
		return lastupdate;
	}
	
	/**
	 * 設定最後更新時間
	 * @param lastupdate
	 */
	public void setLastupdate(Timestamp lastupdate) {
		this.lastupdate = lastupdate;
	}
	
	/**
	 * 取得修改者
	 * @return
	 */
	public String getModifier() {
		return modifier;
	}
	
	/**
	 * 設定修改者
	 * @param modifier
	 */
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	/**
	 * 設定預設值
	 */
	public void checkDefaultValue(){}
	
	/**
	 * 設定預設值
	 */
	public void checkDefault(){}

	 @Override
	 public Object clone() throws CloneNotSupportedException {
	  return super.clone();
	 }
}
