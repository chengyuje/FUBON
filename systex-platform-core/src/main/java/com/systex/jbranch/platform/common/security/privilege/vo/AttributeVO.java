package com.systex.jbranch.platform.common.security.privilege.vo;

/** 
 * 平台內的屬性
 * @author Hong-jie
 * @version 1.0, 2008/4/13
 */
public class AttributeVO {

	/**
 * 屬性描述
 */
private String description;
 
/**
 * Getter of the property <tt>description</tt>
 *
 * @return Returns the description.
 * 
 */
public String getDescription()
{
	return description;
}

/**
 * Setter of the property <tt>description</tt>
 *
 * @param description The description to set.
 *
 */
public void setDescription(String description ){
	this.description = description;
}

	/**
 * 屬性種類
 * "O":組織屬性
 * "R":登入屬性
 */
private String type;
 
/**
 * Getter of the property <tt>type</tt>
 *
 * @return Returns the type.
 * 
 */
public String getType()
{
	return type;
}

/**
 * Setter of the property <tt>type</tt>
 *
 * @param type The type to set.
 *
 */
public void setType(String type ){
	this.type = type;
}

	/**
 * 屬性ID
 */
private String pattributeid;
 
/**
 * Getter of the property <tt>pattributeid</tt>
 *
 * @return Returns the pattributeid.
 * 
 */
public String getPattributeid()
{
	return pattributeid;
}

/**
 * Setter of the property <tt>pattributeid</tt>
 *
 * @param pattributeid The pattributeid to set.
 *
 */

public void setPattributeid(String pattributeid) {
	this.pattributeid = pattributeid;
}
	/**
 * 屬性名稱
 */
private String name;
 
/**
 * Getter of the property <tt>name</tt>
 *
 * @return Returns the name.
 * 
 */
public String getName()
{
	return name;
}

/**
 * Setter of the property <tt>name</tt>
 *
 * @param name The name to set.
 *
 */
public void setName(String name ){
	this.name = name;
}


}


///**
// * ??±o?????W??
// * Getter of the property <tt>name</tt>
// * @return Returns the name. 
//*/
//public String getName(){
//return name;
//	 }
///**
// * ?]?w?????W??
// * Setter of the property <tt>name</tt>
// * @param name The name to set. 
//*/
//public void setName(String name){
//this.name = name;
//	 }
///**
// * ??±o????ID
// * Getter of the property <tt>pattributeid</tt>
// * @return Returns the pattributeid. 
//*/
//public long getPattributeid(){
//return pattributeid;
//	 }
///**
// * ?]?w????ID
// * Setter of the property <tt>pattributeid</tt>
// * @param pattributeid The pattributeid to set. 
//*/
//public void setPattributeid(long pattributeid){
//this.pattributeid = pattributeid;
//	 }
///**
// * ??±o?????????G????????"O" / ?n?J????"R"
// * Getter of the property <tt>type</tt>
// * @return Returns the type. 
//*/
//public String getType(){
//return type;
//	 }
///**
// * ??±o?????????G????????"O" / ?n?J????"R"
// * Setter of the property <tt>type</tt>
// * @param type The type to set. 
//*/
//public void setType(String type){
//this.type = type;
//	 }
///**
// * Getter of the property <tt>type</tt>
// *
// * @return Returns the type.
// * 
// */
//public String getType()
//{
//	return type;
//}
///**
// * Setter of the property <tt>type</tt>
// *
// * @param type The type to set.
// *
// */
//public void setType(String type ){
//	this.type = type;
//}
///**
// * ??±o?????y?z
// * Getter of the property <tt>description</tt>
// * @return Returns the description. 
//*/
//public String getDescription(){
//return description;
//	 }
///**
// * ?]?w?????y?z
// * Setter of the property <tt>description</tt>
// * @param description The description to set. 
//*/
//public void setDescription(String description){
//this.description = description;
//	 }