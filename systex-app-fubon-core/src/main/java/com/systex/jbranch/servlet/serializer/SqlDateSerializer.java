package com.systex.jbranch.servlet.serializer;

import java.lang.reflect.Type;
import java.sql.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class SqlDateSerializer implements JsonSerializer<Date>, JsonDeserializer<Date> {

	@Override
	public Date deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		
		return new Date(json.getAsLong());
	}

	@Override
	public JsonElement serialize(Date src,
			Type typeOfSrc,
			JsonSerializationContext context) {

		return new JsonPrimitive(src.getTime());
	}

}
