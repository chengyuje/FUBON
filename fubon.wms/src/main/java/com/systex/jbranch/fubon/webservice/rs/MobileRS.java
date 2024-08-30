package com.systex.jbranch.fubon.webservice.rs;
 
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.stream.JsonReader;
import com.systex.jbranch.app.server.fps.mao141.MAO141;
import com.systex.jbranch.platform.common.util.PlatformContext;

@Path("/mobile")
public class MobileRS {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
 
    @POST
	@Path("/info")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveInfo(InputStream in) {
    	
    	String result = "success";
		Map<String, String> map = null;
		
		Boolean ans;
		try {
			map = parseJson(in);
			MAO141 mao141 = (MAO141)PlatformContext.getBean("mao141");
			ans = mao141.insertDeviceGPS(map);
			if(ans) {
				return Response.status(200).entity(result).build();
			} else {
				URI uri = new URI("../../../Login.html");
				return Response.temporaryRedirect(uri).build();
			}
		} catch (Exception e) {
			result = e.getMessage();
			logger.error(ExceptionUtils.getStackTrace(e));
			return Response.status(200).entity(result).build();
		}
	}
    
    private Map<String, String> parseJson(InputStream in) throws IOException {
		Map<String, String> map = new HashMap<String, String>();
		
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		reader.beginObject();
		
		while (reader.hasNext()) {
			map.put(reader.nextName(), reader.nextString());
		}
		
		reader.endObject();
		reader.close();
		
		return map;
	}
 
}