package com.systex.jbranch.platform.common.util;

import java.lang.reflect.Type;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Service
public class JsonService {

	  private Gson g;
	  
	  @PostConstruct
	  public void initGson() {
	   
	    g = new GsonBuilder()
	    .setDateFormat("yyyy-MM-dd HH:mm:ss")
	    .serializeNulls()
	    .create();
	  }
	  
	  public String toJson(Object obj) {
		    
	    return g.toJson(obj);
	  }
	  
	  public <T> T fromJson(String json, Type collectionType) {
	  
	    return g.<T>fromJson(json, collectionType);
	  }
	  
	  public <T> T fromJson(String json, Class<T> clazz) {
	   
	    return g.<T>fromJson(json, clazz);
	  }
}
