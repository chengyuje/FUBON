package com.systex.jbranch.platform.reportserver.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ObjectAdapter extends XmlAdapter<String,Object> {
	
	@Override  
    public String marshal(Object object) throws Exception {  
        XStream xs = new XStream(new DomDriver());  
        return xs.toXML(object);  
    }  
  
    @Override  
    public Object unmarshal(String xmlData) throws Exception {  
        XStream xs = new XStream(new DomDriver());  
        return xs.fromXML(xmlData);  
    } 
}
