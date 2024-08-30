package  com.systex.jbranch.platform.common.util;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public final class JsonUtil {

  public static String toJson(Object obj) {
    
    Gson g = new GsonBuilder().serializeNulls().create();
    return g.toJson(obj);
  }
  
  public static <T> T fromJson(String json, Type collectionType) {
  
    return initGson().<T>fromJson(json, collectionType);
  }
  
  public static <T> T fromJson(String json, Class<T> clazz) {
   
    return initGson().<T>fromJson(json, clazz);
  }
  
  private static Gson initGson() {
   
    Gson g = new GsonBuilder()
    .setDateFormat("yyyy-MM-dd HH:mm:ss")
    .serializeNulls()
    .create();
    
    return g;
  }
}