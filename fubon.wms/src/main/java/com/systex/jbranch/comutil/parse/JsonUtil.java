package com.systex.jbranch.comutil.parse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.systex.jbranch.servlet.serializer.DateSerializer;
import com.systex.jbranch.servlet.serializer.SqlDateSerializer;
import com.systex.jbranch.servlet.serializer.TimestampSerializer;

public class JsonUtil {	
	public static Gson genDefaultGson(){
		return new GsonBuilder()
			.registerTypeAdapter(java.util.Date.class, new DateSerializer())
			.registerTypeAdapter(java.sql.Date.class, new SqlDateSerializer())
			.registerTypeAdapter(java.sql.Timestamp.class, new TimestampSerializer())
			.serializeNulls()
			.disableHtmlEscaping().create();
	}
}
