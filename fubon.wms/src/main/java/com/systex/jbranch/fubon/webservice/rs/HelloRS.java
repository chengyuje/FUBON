package com.systex.jbranch.fubon.webservice.rs;
 
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

@Path("/hello")
public class HelloRS {
 
	@GET
	@Produces(MediaType.TEXT_PLAIN)
    public String sayHelloWorld() {
        return "Hello world";
    }
	
	@GET
    @Path("/{name}")
    public String sayHello(@PathParam("name") String name) {
        return "Hello, " + name;
    }
 
    @POST
	@Path("/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response crunchifyREST(InputStream inData) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(inData));
			String line = null;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			System.out.println("Error Parsing: - ");
		}
		System.out.println("Data Received: " + sb.toString());
 
		// return HTTP response 200 in case of success
		return Response.status(200).entity(sb.toString()).build();
	}
    
    @GET
	@Path("/testHttpClient")
	public String testHttpClient() {
    	String str = "";
		try {
			ClassLoader classLoader = HelloRS.class.getClassLoader();
			URL resource = classLoader.getResource("org/apache/http/conn/ssl/AllowAllHostnameVerifier.class");
			str += resource.toString() + "\n\n";
			
			HttpClient httpClient = HttpClientBuilder.create().build();
		} catch (Throwable e) {
			str += e.toString() + e.getMessage() + "\n\n";
			StackTraceElement[] stackArr = e.getStackTrace();
			for (StackTraceElement se : stackArr) {
				str += se.toString() + "\n";
			}
		}
		return str;
	}
 
}