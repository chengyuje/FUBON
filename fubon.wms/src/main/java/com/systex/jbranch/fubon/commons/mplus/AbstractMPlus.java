package com.systex.jbranch.fubon.commons.mplus;

  
import static com.systex.jbranch.fubon.commons.mplus.cons.MPlusAlterEmployeeCons.CHENGE_TOKEN;
import static com.systex.jbranch.fubon.commons.mplus.cons.MPlusAlterEmployeeCons.PROD;
import static com.systex.jbranch.fubon.commons.mplus.cons.MPlusCons.AES_ENCRYPT;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.gson.Gson;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.mplus.multipartEmpty.MultipartEntity;
import com.systex.jbranch.fubon.commons.mplus.multipartEmpty.SecretKeyType;
import com.systex.jbranch.fubon.commons.mplus.multipartEmpty.annotation.MultipartEmptyMappingInf;
import com.systex.jbranch.fubon.commons.soap.HttpClientSoapUtils;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;

/**
 * Created by SebastianWu on 2016/10/20.
 */
public abstract class AbstractMPlus extends FubonWmsBizLogic {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired@Qualifier("mplusutil")
	private MPlusUtil mplusUtil;
	
    DataAccessManager dam = null;

    
    public MPlusResult send2MPlusForPostNameValuePair(String url , Object inputVO) throws Exception{    	
    	HttpClient client = new HttpClient();
    	List<NameValuePair> nameValPairs = null;
    	PostMethod post = new PostMethod(url);
    	
        try {
        	if(CollectionUtils.isNotEmpty(nameValPairs = genNameValPair(inputVO)))
        		post.setRequestBody(nameValPairs.toArray(new NameValuePair[]{}));
        	
        	if(url.matches("^https.*")){
        		Protocol myhttps = new Protocol("https", new SSLProtocolSocketFactory(), 443);   
        		Protocol.registerProtocol("https", myhttps);
        	}
        	
        	post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            // 返回狀態值.
            if (client.executeMethod(post) != HttpStatus.SC_OK) {
            	throw new Exception("Method failed: " + post.getStatusLine());
            }
            
       		return new Gson().fromJson(new String(post.getResponseBody()) , MPlusResult.class);       		
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return null;
    }
    
    /**
     * 傳入ｕurl及vo發送請求給url所對應的對象
     *
     * @param inputVO
     * @return String - response的結果
     * @throws Exception
     */
    public MPlusResult send2MPlusForPost(String url , Object inputVO) throws Exception{    	
    	CloseableHttpClient httpClient = null;
    	MultipartEntityBuilder muti = conversionMultipartEntity(inputVO);
    	muti.setCharset(MIME.UTF8_CHARSET);
    	muti.setContentType(ContentType.create("text/plain" , MIME.UTF8_CHARSET));
    	
    	HttpEntity entity = muti.build();
    	StringWriter writer = new StringWriter();
    	IOUtils.copy(entity.getContent() , writer, "utf-8");
    	
        try {
        	//判斷是否是走SSL
            httpClient = url.matches("^https.*") ? HttpClientSoapUtils.createSSLInsecureClient() : HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(entity);
            HttpResponse resp = httpClient.execute(httpPost);
            mPlusLog(httpPost);
            
            entity = resp.getEntity();
            String result = EntityUtils.toString(entity,"UTF-8");
            logger.debug(result);
       		//return new Gson().fromJson(result , MPlusResult.class);   
       		return new Gson().fromJson(result , MPlusResult.class);       		
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
        	if(httpClient != null)
        		httpClient.close();
        }
        return null;
    }
    
    public MPlusResult send2MPlusForGet(String url , Object inputVO) throws Exception{    	
    	url = url.replaceFirst(CHENGE_TOKEN , PROD) + genGetMethodParam(inputVO).replaceFirst("^&", "?");
    	logger.debug(url);
    	CloseableHttpClient httpClient = null;

        try {
            httpClient = url.matches("^https.*") ? HttpClientSoapUtils.createSSLInsecureClient() : HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(url);
            HttpResponse resp = httpClient.execute(get);
            HttpEntity entity = resp.getEntity();
            String result = EntityUtils.toString(entity,"UTF-8");
            logger.debug(result);
       		return new Gson().fromJson(result , MPlusResult.class);       		
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
        	if(httpClient != null)
        		httpClient.close();
        }
        return null;
    }

    /**
     * 依傳入inputVO傳入欄位去設定MultipartEntityBuilder
     *
     * @param inputVO
     * @return
     * @throws Exception
     */
    protected MultipartEntityBuilder conversionMultipartEntity(Object inputVO) throws Exception {
    	return conversionMultipartEntity(MultipartEntityBuilder.create() , inputVO.getClass() , inputVO);
    }
    
    protected List<NameValuePair> genNameValPair(Object inputVO) throws Exception {
    	return genNameValPair(new ArrayList<NameValuePair>() , inputVO.getClass() , inputVO);
    }

    
    /**vo to form**/
    @SuppressWarnings("rawtypes")
	protected List<NameValuePair> genNameValPair(List<NameValuePair> list , Class typeCls , Object inputVO) throws Exception {
    	//先處理super class的field
    	if(typeCls.getSuperclass() != null)
    		list.addAll(genNameValPair(list , typeCls.getSuperclass() , inputVO));
    	
    	//逐一將vo的屬性取出處理
        for(Field field : typeCls.getDeclaredFields()){
            String fieldName = field.getName();
            String fieldGetMethodName = fieldName.replaceFirst("^.", "get" + fieldName.substring(0 , 1).toUpperCase());//get方法名稱 
            Object fieldInstance = inputVO.getClass().getMethod(fieldGetMethodName).invoke(inputVO);//取該field對應的get方法得回傳值
            MultipartEntity multipartEntity = null;
            
            //沒值跳過
        	if(fieldInstance == null)
        		continue;
        	//不加密處理：判斷field沒有MultipartEntity這個注釋，或是這個注釋為EMPTY，不需進行加密處理
        	else if((multipartEntity = field.getAnnotation(MultipartEntity.class)) == null || SecretKeyType.EMPTY.equals(multipartEntity.secretKeyType()))
        		list.add(new NameValuePair(fieldName , fieldInstance.toString()));
    		else 
    			list.add(genNameValuePairEncrypt(inputVO , field , fieldInstance , multipartEntity));
        }
        return list;
    }

    
    /**vo to form**/
    @SuppressWarnings("rawtypes")
	protected MultipartEntityBuilder conversionMultipartEntity(MultipartEntityBuilder mebuilder , Class typeCls , Object inputVO) throws Exception {
    	//先處理super class的field
    	if(typeCls.getSuperclass() != null)
    		mebuilder = conversionMultipartEntity(mebuilder , typeCls.getSuperclass() , inputVO);
    	
    	//逐一將vo的屬性取出處理
        for(Field field : typeCls.getDeclaredFields()){
            String fieldName = field.getName();
            String fieldGetMethodName = fieldName.replaceFirst("^.", "get" + fieldName.substring(0 , 1).toUpperCase());//get方法名稱 
            Object fieldInstance = inputVO.getClass().getMethod(fieldGetMethodName).invoke(inputVO);//取該field對應的get方法得回傳值
            MultipartEntity multipartEntity = null;
            
            //沒值跳過
        	if(fieldInstance == null)
        		continue;
        	//不加密處理：判斷field沒有MultipartEntity這個注釋，或是這個注釋為EMPTY，不需進行加密處理
        	else if((multipartEntity = field.getAnnotation(MultipartEntity.class)) == null || SecretKeyType.EMPTY.equals(multipartEntity.secretKeyType()))
    			addTextBody(mebuilder ,inputVO , field , fieldInstance);
    		else
    			addTextBodyEncrypt(mebuilder , inputVO , field , fieldInstance , multipartEntity) ;
        }
        return mebuilder;
    }
    
    @SuppressWarnings("unchecked")
	public Method getMethod(Class cls , String methodName){
    	try {
			return cls.getMethod(methodName);
		} catch (NoSuchMethodException e) {
			return getMethod(cls.getSuperclass() , methodName);
		}
    }
    
    @SuppressWarnings("unchecked")
	public Method getMethod(Class cls , String methodName , Class[] params){
    	try {
			return cls.getMethod(methodName, params);
		} catch (NoSuchMethodException e) {
			return getMethod(cls.getSuperclass() , methodName , params);
		}
    }
    
    
    protected String genGetMethodParam(Object inputVO) throws Exception {
    	return genGetMethodParam(inputVO.getClass() , inputVO);
    }
    
    protected String genGetMethodParam(Class typeCls , Object inputVO) throws Exception {
    	String urlParam = "";
    	String valParam = null;
    	//先處理super class的field
    	if(typeCls.getSuperclass() != null)
    		urlParam += genGetMethodParam(typeCls.getSuperclass() , inputVO);
        
        for(Field field : typeCls.getDeclaredFields()){
            String fieldName = field.getName();
            String fieldGetMethodName = fieldName.replaceFirst("^.", "get" + fieldName.substring(0 , 1).toUpperCase());//get方法名稱 
            Object fieldInstance = inputVO.getClass().getMethod(fieldGetMethodName).invoke(inputVO);//取該field對應的get方法得回傳值
            
            valParam = addUrlParamForVo(inputVO , field , fieldInstance);
          //有值就丟進表單
            if(StringUtils.isNotBlank(valParam))
            	urlParam += "&" + valParam;
        }
        return urlParam;
    }
    
    /**不加密處理**/
    @SuppressWarnings("rawtypes")
	public void addTextBody(MultipartEntityBuilder mebuilder , Object inputVO , Field field , Object fieldInstance){
    	Class valClass = fieldInstance.getClass();
    	String valStr = fieldInstance.toString();
    	
		if(String.class.equals(valClass))
			mebuilder.addTextBody(field.getName() , valStr , ContentType.TEXT_PLAIN.withCharset(CharEncoding.UTF_8));
		//JSONObject物件格式
		else if(JSONObject.class.equals(valClass)){
            if(StringUtils.equals(field.getName(), "text"))
            	mebuilder.addTextBody(field.getName(), valStr , ContentType.TEXT_PLAIN.withCharset(CharEncoding.UTF_8));
            else
            	mebuilder.addTextBody(field.getName(), valStr, ContentType.TEXT_PLAIN.withCharset(CharEncoding.UTF_8));
        }
    }
    
    /**加密處理**/
	@SuppressWarnings("rawtypes")
	public NameValuePair genNameValuePairEncrypt(Object inputVO , Field field , Object fieldInstance , MultipartEntity multipartEntity) throws Exception, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, ClassNotFoundException{
    	MultipartEmptyMappingInf multipartEmptyMapping = null;
    	Class memCls = null;
    	Matcher matcher = null;
    	
		//加密處理：取出field塞入textBody方式的實作類別
		memCls = multipartEntity.voToMultipartEntity();
		
		//判斷類別名稱是否為內部類別的格式，若是以內部類別的方式建立物件實體
		if((matcher = Pattern.compile("(\\w+\\.)*\\w+\\$\\w+").matcher(memCls.getName())).find())
			multipartEmptyMapping = (MultipartEmptyMappingInf)memCls.getConstructors()[0].newInstance(Class.forName(matcher.group().replaceFirst("\\$.*", "")).newInstance());
		else//一般類別
			multipartEmptyMapping = (MultipartEmptyMappingInf)memCls.newInstance();
		
    	return multipartEmptyMapping.genNameValuePair(inputVO, field, fieldInstance, doGetSecretKey(inputVO , multipartEntity));
    }
	
	@SuppressWarnings("rawtypes")
	public void addTextBodyEncrypt(MultipartEntityBuilder mebuilder , Object inputVO , Field field , Object fieldInstance , MultipartEntity multipartEntity) throws Exception, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, ClassNotFoundException{
    	MultipartEmptyMappingInf multipartEmptyMapping = null;
    	Class memCls = null;
    	Matcher matcher = null;
    	
		//加密處理：取出field塞入textBody方式的實作類別
		memCls = multipartEntity.voToMultipartEntity();
		
		//判斷類別名稱是否為內部類別的格式，若是以內部類別的方式建立物件實體
		if((matcher = Pattern.compile("(\\w+\\.)*\\w+\\$\\w+").matcher(memCls.getName())).find())
			multipartEmptyMapping = (MultipartEmptyMappingInf)memCls.getConstructors()[0].newInstance(Class.forName(matcher.group().replaceFirst("\\$.*", "")).newInstance());
		else//一般類別
			multipartEmptyMapping = (MultipartEmptyMappingInf)memCls.newInstance();
		
    	multipartEmptyMapping.addTextBody(mebuilder, inputVO, field, fieldInstance, doGetSecretKey(inputVO , multipartEntity));
    }
    
    
 	public String addUrlParamForVo(Object inputVO , Field field , Object fieldInstance)throws Exception{
     	MultipartEntity multipartEntity = null;
     	if(fieldInstance == null)
     		return null;
     	
     	//不加密處理：判斷field沒有MultipartEntity這個注釋，或是這個注釋為EMPTY，不需進行加密處理
     	else if((multipartEntity = field.getAnnotation(MultipartEntity.class)) == null || SecretKeyType.EMPTY.equals(multipartEntity.secretKeyType()))
 			return field.getName() + "=" + fieldInstance.toString();
     	
		return field.getName() + "=" + encryptAES(fieldInstance.toString() , URLEncoder.encode(doGetSecretKey(inputVO , multipartEntity) , "utf-8"));
     }
    
    /**
     * 取MultipartEntity內所設定的SecretKey來源
     *
     * @param inputVO
     * @param multipartEntity
     * 
     * @return String
     * @throws Exception
     */
    public String doGetSecretKey(Object inputVO , MultipartEntity multipartEntity) throws Exception {
    	//vo中某一個屬性
		if(SecretKeyType.FIELD.equals(multipartEntity.secretKeyType())){
			Field sKeyField = inputVO.getClass().getDeclaredField(multipartEntity.secretKeyValue());
			sKeyField.setAccessible(true);
			return (String)sKeyField.get(inputVO); 
		}
		//vo中某一個方法的回傳值
		else if(SecretKeyType.METHOD.equals(multipartEntity.secretKeyType()))
			return (String) inputVO.getClass().getMethod(multipartEntity.secretKeyValue() , new Class[]{}).invoke(inputVO);
		//直接在annotation中指定SecretKey
		else if(SecretKeyType.VALUE.equals(multipartEntity.secretKeyType()))
			return multipartEntity.secretKeyValue(); 
		
		return null;
    }
    
    /**
     * AES Base64加密
     *
     * @param value
     * @param secretKey
     * 
     * @return String
     * @throws Exception
     */
    public static String encryptAES(String value , String secretKey) throws Exception {
        return new String(Base64.encodeBase64(genCipher(Cipher.ENCRYPT_MODE , AES_ENCRYPT , secretKey).doFinal(value.getBytes(CharEncoding.UTF_8))));
    }
    
//    public static void main(String...args) throws Exception{
//    	System.out.println(encryptAES("AE9wW9e60b46q1PDKLvy" , "2b51f2ae0b6d61ea"));
//    }
    
    /**
     * AES Base64解密
     *
     * @param value
     * @param secretKey
     * 
     * @return String
     * @throws Exception
     */
    public static String decryptAES(String value , String secretKey) throws Exception {
        return new String(genCipher(Cipher.DECRYPT_MODE , AES_ENCRYPT , secretKey).doFinal(Base64.decodeBase64(value)) , "UTF-8");
    }
    
    /**
     * 初始化Cipher
     *
     * @param mode
     * @param encryptType
     * @param secretKey
     * 
     * @return Cipher
     * @throws Exception
     */
    public static Cipher genCipher(int mode , String encryptType , String secretKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException{
        Cipher cipher = Cipher.getInstance(encryptType);
        cipher.init(mode , genAesSecretKey(secretKey , encryptType));
        return cipher;
    }
    
    
    /**
     * 建立SecretKeySpec
     *
     * @param secretKey
     * @param algorithm
     * 
     * @return SecretKeySpec
     * @throws Exception
     */
    public static SecretKeySpec genAesSecretKey(String secretKey , String algorithm) throws UnsupportedEncodingException{
    	return new SecretKeySpec(Arrays.copyOf(secretKey.getBytes(CharEncoding.UTF_8), 16), algorithm);
    }
    
    /**
     * 將response回來的結果印出
     *
     * @param httpPost
     * 
     * @return 
     * @throws IOException
     */
    private void mPlusLog(HttpPost httpPost) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream((int)httpPost.getEntity().getContentLength());
        httpPost.getEntity().writeTo(out);
        String param = out.toString();

        // logger.info("MPlus Http Req:\n" + param);

        out.close();
    }

	public MPlusUtil getMplusUtil() {
		return mplusUtil;
	}

	public void setMplusUtil(MPlusUtil mplusUtil) {
		this.mplusUtil = mplusUtil;
	}
    
    

}
