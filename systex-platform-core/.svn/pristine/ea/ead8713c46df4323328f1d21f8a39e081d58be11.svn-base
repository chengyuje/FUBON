package com.systex.jbranch.platform.reportserver.adapter;

import java.util.HashMap;  
import javax.xml.bind.annotation.adapters.XmlAdapter;  
import com.thoughtworks.xstream.XStream;  
import com.thoughtworks.xstream.io.xml.DomDriver;  

public class MapAdapter extends XmlAdapter<String,HashMap> {  
  
    @Override  
    public String marshal(HashMap map) throws Exception {  
        XStream xs = new XStream(new DomDriver());  
        return xs.toXML(map);  
    }  
  
    @Override  
    public HashMap unmarshal(String xmlData) throws Exception {  
        XStream xs = new XStream(new DomDriver());  
        return (HashMap) xs.fromXML(xmlData);  
    }  
  
}  