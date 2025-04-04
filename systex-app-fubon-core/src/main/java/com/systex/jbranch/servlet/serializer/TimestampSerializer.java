package com.systex.jbranch.servlet.serializer;

import java.lang.reflect.Type;
import java.sql.Timestamp;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TimestampSerializer implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {

	@Override
	public Timestamp deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		
		return new Timestamp(json.getAsLong());
	}

	@Override
	public JsonElement serialize(Timestamp src,
			Type typeOfSrc,
			JsonSerializationContext context) {

		return new JsonPrimitive(src.getTime());
	}

}
