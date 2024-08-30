package com.systex.jbranch.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.DynamicSystemParameter;
import com.systex.jbranch.platform.common.util.JsonService;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.ThreadDataPool;
import com.systex.jbranch.platform.server.conversation.message.Tia;
import com.systex.jbranch.platform.server.conversation.message.Toa;
import com.systex.jbranch.platform.server.pipeline.flex.CommonConstantHelper;
import com.systex.jbranch.platform.server.pipeline.flex.PipelineUtil;
import com.systex.jbranch.platform.server.pipeline.flex.RemotingService;
import com.systex.jbranch.servlet.serializer.DateSerializer;
import com.systex.jbranch.servlet.serializer.SqlDateSerializer;
import com.systex.jbranch.servlet.serializer.TimestampSerializer;
import com.systex.jbranch.servlet.vo.Header;

/**
 *將tita中json格式轉由Tia中所相對應的欄位值，並將Toa陣列轉為json後下傳。
 *
 *@author Angus
 *@date Jul 9, 2014
 *
 */
/*
 *  Maintain History:
 *
 *         Author       Date         PIS/SR NO.       Comment
 *         ==========   ===========  ===========   ==========================
 *         Angus        Jul 9, 2014                     Initial version
 * 
 */
@WebServlet("/EsoafDispatcher")
public class EsoafDispatcher extends HttpServlet {
	
	private static final String ENCODING = "UTF-8";

	private static final String INPUT_VO_CLASS = "InputVOClass";

	private Logger logger = LoggerFactory.getLogger(EsoafDispatcher.class);
	
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private RemotingService remotingService;
	
	@Autowired
	private JsonService jsonService;
	
	private Validator validator;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				config.getServletContext());
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EsoafDispatcher() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding(ENCODING);
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter writer = response.getWriter();
		String errorMessageJson = getErrorMessageJson("不支援http get");
		writer.write(errorMessageJson);
		writer.close();
	}

	private String getErrorMessageJson(String errorMessage) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("{\"header\":{\"EndBracket\":false,\"OutputType\":\"Message\"}, \"body\":{\"showType\":\"Show\",\"messageType\":\"Error\",\"msgCode\":\"Z999\",\"msgData\":" + jsonService.toJson(errorMessage) + "}},");
		sb.append("{\"header\":{\"EndBracket\":false,\"TXN_DATA\":{},\"GW_HOST_NAME\":\"\",\"OutputType\":\"EndBracket\",\"AP_HOST_NAME\":\"\"}, \"body\":{\"result\":\"error\"}}");
		sb.append("]");
		return sb.toString();
	}
	
	private String getErrorMessageJson(String[] errorMessages) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("{\"header\":{\"EndBracket\":false,\"OutputType\":\"Message\"}, \"body\":{\"showType\":\"Show\",\"messageType\":\"Error\",\"msgCode\":\"Z999\",\"msgData\":" + jsonService.toJson(errorMessages) + "}},");
		sb.append("{\"header\":{\"EndBracket\":false,\"TXN_DATA\":{},\"GW_HOST_NAME\":\"\",\"OutputType\":\"EndBracket\",\"AP_HOST_NAME\":\"\"}, \"body\":{\"result\":\"error\"}}");
		sb.append("]");
		return sb.toString();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		dispatcher(request, response);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void dispatcher(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
//		logger.info("test");
		ThreadDataPool.setData(PipelineUtil.HTTP_SERVLET_REQUEST, request);
		ThreadDataPool.setData(PipelineUtil.HTTP_SERVLET_RESPONSE, response);
		
		String ip= PipelineUtil.getRemoteAddr();
		MDC.put(CommonConstantHelper.IP, ip);

		response.setCharacterEncoding(ENCODING);
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		String[] allowIPs = this.allowOriginal();
		if (allowIPs != null) {
			for (String allowIP : allowIPs) {
				if (ip.startsWith(allowIP)) {
// System.out.println("Remote IP:" + ip);
					response.setHeader("Access-Control-Allow-Origin", ip + ":" + request.getRemotePort());
					break;
				}
				if (request.getRemoteHost().startsWith(allowIP)) {
// System.out.println("Remote Host:" + request.getRemoteHost());
					response.setHeader("Access-Control-Allow-Origin", request.getRemoteHost() + ":" + request.getRemotePort());
					break;
				}
			}
		}
//		response.setHeader("Access-Control-Allow-Origin", "*");

		InputStream in = request.getInputStream();
		StringBuffer titaStringResult = new StringBuffer();
//		byte[] buffer = new byte[512];
//		int len = in.read(buffer);
//		while (len != -1) {
//			titaStringResult.append(new String(buffer, 0, len, ENCODING));
//			len = in.read(buffer);
//		}
		String line = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, ENCODING));
        while ((line = reader.readLine()) != null) {
        	titaStringResult.append(line).append("\n");
        }
        in.close();
        reader.close();
		logger.debug("tita={}", titaStringResult.toString());
		String tota = "[]";
		try {
			JsonParser parser = new JsonParser(); 
			JsonElement e = parser.parse(titaStringResult.toString());
			JsonElement headerJsonObject = e.getAsJsonObject().getAsJsonObject("header");
			if (headerJsonObject == null) {
				throw new JsonSyntaxException("缺少header");
			}
			
			Gson gson = initGon();
			
			Header header = gson.fromJson(headerJsonObject.toString(), Header.class);
			
			Map<String, Object> headerMap = valueObjectToMap(header);
			Object body = null;
			String inputVOClassName = (String) headerMap.get(INPUT_VO_CLASS);
			
			if (inputVOClassName == null) {
				throw new JBranchException("未輸入InputVO");
			}
			Class<?> inputVOClass = Class.forName(inputVOClassName);
			JsonElement bodyJsonObject = e.getAsJsonObject().get("body");
			body = gson.fromJson(bodyJsonObject,
					(bodyJsonObject instanceof JsonArray) ? ArrayList.class
							: inputVOClass);

			 Set<ConstraintViolation<Object>> violations = validator.validate(body);
			 StringBuffer errorList = new StringBuffer();
			
	        for (ConstraintViolation violation : violations){
	            String propertyPath = violation.getPropertyPath().toString();
	            String message = violation.getMessage();
	            
	            errorList.append(message + ",");
	            logger.error(inputVOClassName + " Invalid "+ propertyPath + "(" + message + ")");
	        }
		    if (errorList.length() > 0) {
		    	throw new ValidationException(errorList.toString().substring(0, errorList.toString().length() - 1));
			}
			
			Tia origiTia = new Tia();
			origiTia.setHeaders(headerMap);
			origiTia.setBody(body);
			
			List<Toa> flexToaList = remotingService.invoke(origiTia);
			
			int toaLen = flexToaList.size();
			StringBuffer sb = new StringBuffer();
        	sb.append("\r\n[");
        	for(int i = 0; i < toaLen; i++){
        		String headerJson = jsonService.toJson(((com.systex.jbranch.platform.server.conversation.message.Toa) flexToaList.get(i)).getHeaders());
        		
        		String bodyJson = jsonService.toJson(((com.systex.jbranch.platform.server.conversation.message.Toa) flexToaList.get(i)).getBody());
        		//sb.append("\r\n{\"header\":"+headerJson+", \"body\":"+bodyJson+"}");
        		sb.append("\r\n{\"header\":").append(headerJson).append(", \"body\":").append(bodyJson).append("}");
        		
        		if(i < toaLen - 1){
        			sb.append(",");
        		}
        	}
        	sb.append("\r\n]");
			tota = sb.toString();
		} catch (ClassNotFoundException e) {
			String errorMessage = "找不到InputVO:" + e.getMessage();
			logger.error(errorMessage, e);
			tota = getErrorMessageJson(errorMessage);
		}catch (ValidationException e) {
			String[] errorMessages = e.getMessage().split(",");
			logger.error(e.getMessage(), e);
			tota = getErrorMessageJson(errorMessages);
		} catch (InstantiationException e) {
			logger.error(e.getMessage(), e);
			tota = getErrorMessageJson(e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			tota = getErrorMessageJson(e.getMessage());
		} catch (JBranchException e) {
			logger.error(e.getMessage(), e);
			tota = getErrorMessageJson(e.getMessage());
		} catch(JsonSyntaxException e){
			String errorMessage = "Json格式錯誤:"+e.getMessage();
			logger.error(errorMessage, e);
			tota = getErrorMessageJson(errorMessage);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			tota = getErrorMessageJson(e.getMessage());
		} finally{
			logger.debug("tota={}", tota);
			ServletOutputStream os = response.getOutputStream();
			os.write(tota.getBytes(ENCODING));
			os.flush();
			os.close();
		}
	}
	
	private Gson initGon() {
		return new GsonBuilder()
		.registerTypeAdapter(java.util.Date.class, new DateSerializer())
		.registerTypeAdapter(java.sql.Date.class, new SqlDateSerializer())
		.registerTypeAdapter(java.sql.Timestamp.class, new TimestampSerializer())
		.serializeNulls()
		.create();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Object> valueObjectToMap(Object vo) throws SecurityException, 
		IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Map map = new HashMap();
		
		Class clazz = vo.getClass();
		
		Field[] fields = clazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			Class<?> type = field.getType();
			if (type == Boolean.class) {
				map.put(field.getName(), field.getBoolean(vo));
			}else{
				map.put(field.getName(), field.get(vo));
			}
		}
		return map;
	}

	// 是否可信任來源
	private String[] allowOriginal() {
		Connection cn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			DynamicSystemParameter dsp = (DynamicSystemParameter) PlatformContext.getBean("jndiNameFactory");
			String jndi = (String) dsp.getValue();

			Context initContext = new InitialContext();	
			DataSource ds = (DataSource) initContext.lookup(jndi);
			cn = ds.getConnection();
			st = cn.createStatement();
			rs = st.executeQuery("SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE='FUBON_TRUST_IP' and PARAM_CODE='IP_PREFIX'");
			if (rs.next()) {
				return rs.getString(1) != null ? rs.getString(1).split(";") : null;
			}
			return null;
		} catch (Exception e) {
			logger.warn("read DB error", e);
			return null;
		} finally {
			if (rs != null) safeClose(rs);
			if (st != null) safeClose(st);
			if (cn != null) safeClose(cn);
		}
	}

	private void safeClose(ResultSet closable) {
		if (closable != null) {
			try {
				closable.close();
			} catch (Exception e) {
				logger.warn("close error", e);
			}
		}
	}

	private void safeClose(Statement closable) {
		if (closable != null) {
			try {
				closable.close();
			} catch (Exception e) {
				logger.warn("close error", e);
			}
		}
	}

	private void safeClose(Connection closable) {
		if (closable != null) {
			try {
				closable.close();
			} catch (Exception e) {
				logger.warn("close error", e);
			}
		}
	}		
}
