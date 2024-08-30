package com.systex.jbranch.platform.common.security.privilege.vo;

/** 
 * 權限與Runtime(登入)屬性的對應關係類別
 * @author Hong-jie
 * @version 1.0 2008/4/14
 */
public class PrivilegeAttributeAssociationVO {

	private String flag;
	/**
 * 權限ID
 */
private String privilegeid;
 
/**
 * 取得權限ID
 * Getter of the property <tt>privilegeid</tt>
 * @return Returns the privilegeid. 
*/

public String getPrivilegeid() {
	return privilegeid;
}
/**
 * 設定權限ID
 * Setter of the property <tt>privilegeid</tt>
 * @param privilegeid The privilegeid to set. 
*/
public void setPrivilegeid(String privilegeid) {
	this.privilegeid = privilegeid;
}

	/**
 * 可接受的屬性值("ALL"為不限制)
 */
private String value;
 
/**
 * 取得可接受的屬性值
 * Getter of the property <tt>value</tt>
 * @return Returns the value. 
*/
public String getValue(){
return value;
	 }

/**
 * 設定可接受的屬性值("ALL"為不受任何限制，全都接受)
 * Setter of the property <tt>value</tt>
 * @param value The value to set. 
*/
public void setValue(String value){
this.value = value;
	 }

	/**
 * 屬性ID
 */
private String attributeid;


 
/**
 * 取得屬性ID
 * Getter of the property <tt>attributeid</tt>
 * @return Returns the attributeid. 
*/
	public String getAttributeid() {
		return attributeid;
	}
/**
 * 設定屬性ID
 * Setter of the property <tt>attributeid</tt>
 * @param attributeid The attributeid to set. 
*/
	public void setAttributeid(String attributeid) {
		this.attributeid = attributeid;
	}
public String getFlag() {
	return flag;
}
public void setFlag(String flag) {
	this.flag = flag;
}
}

///**
// * Setter of the property <tt>attributeid</tt>
// *
// * @param attributeid The attributeid to set.
// *
// */
//public void setAttributeid(String attributeid ){
//	this.attributeid = attributeid;
//}
///**
// * Getter of the property <tt>attributeid</tt>
// *
// * @return Returns the attributeid.
// * 
// */
//public String getAttributeid()
//{
//	return attributeid;
//}
///**
// * Setter of the property <tt>attributeid</tt>
// *
// * @param attributeid The attributeid to set.
// *
// */
//public void setAttributeid(String attributeid ){
//	this.attributeid = attributeid;
//}
///**
// * Getter of the property <tt>attributeid</tt>
// *
// * @return Returns the attributeid.
// * 
// */
//public String getAttributeid()
//{
//	return attributeid;
//}
///**
// * Getter of the property <tt>value</tt>
// *
// * @return Returns the value.
// * 
// */
//public String getValue()
//{
//	return value;
//}
///**
// * Setter of the property <tt>value</tt>
// *
// * @param value The value to set.
// *
// */
//public void setValue(String value ){
//	this.value = value;
//}
///**
// * Setter of the property <tt>privilegeid</tt>
// *
// * @param privilegeid The privilegeid to set.
// *
// */
//public void setPrivilegeid(String privilegeid ){
//	this.privilegeid = privilegeid;
//}
///**
// * Getter of the property <tt>privilegeid</tt>
// *
// * @return Returns the privilegeid.
// * 
// */
//public String getPrivilegeid()
//{
//	return privilegeid;
//}