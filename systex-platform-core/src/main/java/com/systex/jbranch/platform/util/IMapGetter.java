package com.systex.jbranch.platform.util;

import java.util.Date;
import java.util.List;
import org.w3c.dom.Document;

public interface IMapGetter<K> {

	Object getObj(K key);
	
    boolean getBool(K key);
    
    byte getByte(K key);
    
    char getChar(K key);

    double getDbl(K key);
    
    float getFloat(K key);

    int getInt(K key);

    long getLong(K key);
    
    short getShort(K key);
    
    String getStr(K key);
    
    Document getXml(K key);
    
    Date getDate(K key);
    
    IPrimitiveMap getMap(K key);
    
    List getList(K key);
}
