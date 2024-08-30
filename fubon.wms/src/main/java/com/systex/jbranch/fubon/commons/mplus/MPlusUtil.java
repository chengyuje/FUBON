package com.systex.jbranch.fubon.commons.mplus;

import static com.systex.jbranch.fubon.commons.mplus.HttpClientUtil.TLSv1_2;
import static com.systex.jbranch.fubon.commons.mplus.cons.MPlusCons.AES_ENCRYPT;
import static com.systex.jbranch.fubon.commons.mplus.cons.MPlusCons.M_PLUS_URI;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;

/**
 * Created by SebastianWu on 2016/10/20.
 */
@Component("mplusutil")
@Scope("prototype")
public class MPlusUtil extends FubonWmsBizLogic {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    DataAccessManager dam = null;

    public static void main(String...args) throws JBranchException{
    	MPlusInputVO vo = new MPlusInputVO();
    	new MPlusUtil().send2MPlus(vo);
    }

    public void send2MPlus(MPlusInputVO inputVO) throws JBranchException {
    	String result = null;
        try {
            //check
            checkField(inputVO);

            //set account & password
        	Map<String , String> config = getMPlusAccPwd();
        	inputVO.setAccount(config.get("ACT"));//帳號
        	inputVO.setPassword(config.get("password"));//密碼
        	inputVO.setSkey(config.get("SecretKey"));
            inputVO.setSendtime(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));

            logger.info("M+ setMultipartEntity前");
            MultipartEntityBuilder multi = setMultipartEntity(inputVO);
            logger.info("M+ setMultipartEntity後");

            logger.info("M+ HttpClientBuilder.create().build()前");
            HttpClient httpClient = HttpClientUtil.getTrustAnyHttpClient(TLSv1_2);
            logger.info("M+ HttpClientBuilder.create().build()後");
            HttpPost httpPost = new HttpPost(M_PLUS_URI);
            logger.info("M+ httpPost.setEntity(multi.build())前");
            httpPost.setEntity(multi.build());
            logger.info("M+ httpPost.setEntity(multi.build())後");

            //send
            logger.info("M+ httpClient.execute(httpPost)前");
            HttpResponse resp = httpClient.execute(httpPost);
            logger.info("M+ httpClient.execute(httpPost)後");
            logger.info("M+ mPlusLog(httpPost)前");
            mPlusLog(httpPost);
            logger.info("M+ mPlusLog(httpPost)後");

            result = EntityUtils.toString(resp.getEntity(),"UTF-8");

            if("00".equals(result.substring(11, 13))){
            	logger.info("M+ 發送成功：" + result);
            }else{
            	logger.info("M+ 發送失敗：" + result);
//            	throw new APException("M+ 發送失敗：" + result);
            }
        } catch (Exception e) {
        	logger.error("M+ 拋出的Exception：" + StringUtil.getStackTraceAsString(e));
        	e.printStackTrace();

//        	if(StringUtils.isBlank(result)){
//        		throw new APException("M+ 發送失敗：" + StringUtil.getStackTraceAsString(e));
//        	}else{
//        		throw new APException("M+ 發送失敗：" + result);
//        	}

        }
    }

    /**
     * check requested field
     *
     * @param inputVO
     */
    private void checkField(MPlusInputVO inputVO) throws JBranchException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String type = inputVO.getTargetType();
        String msgType = inputVO.getMsgType();
        JSONObject text = inputVO.getText();
        MPlusInputVO.BinaryFile image = inputVO.getImage();
        String infopushCount = inputVO.getInfopushCount();
        String templateType = inputVO.getTemplateType();
        JSONObject headline = inputVO.getHeadline();
        MPlusInputVO.BinaryFile iconFile1 = inputVO.getIconFile1();
        MPlusInputVO.BinaryFile iconFile2 = inputVO.getIconFile2();
        MPlusInputVO.BinaryFile iconFile3 = inputVO.getIconFile3();
        JSONObject infoUrl = inputVO.getInfoUrl();
        MPlusInputVO.BinaryFile target = inputVO.getTarget();
        JSONObject department = inputVO.getDepartment();
        JSONObject group = inputVO.getGroup();
        String operatorId = inputVO.getOperatorId();

        if(StringUtils.isBlank(type) || StringUtils.isBlank(msgType)){
            logger.error("[M+]必入欄位未輸入");
        }

        Boolean isErr = Boolean.FALSE;
        //msgType=T
        if(StringUtils.equals("T", msgType)){
            //文字訊息內容為必入
            isErr = (text != null && StringUtils.isNotBlank(text.toString())) ? Boolean.FALSE : Boolean.TRUE;

            if(isErr){
                logger.error("[M+]文字訊息內容為必入");
            }

            //須為文字檔
            if(target.getFileCxt()!=null){
                logger.error("[M+]名單檔案為必入欄位");
            }
        }
        //msgType=P
        else if(StringUtils.equals("P", msgType)){
            //圖片訊息內容為必入
            isErr = (image!=null && image.getFileCxt().length > 0) ? Boolean.FALSE : Boolean.TRUE;

            if(isErr){
                logger.error("[M+]圖片訊息內容為必入");
            }
        }
        //msgType=I
        else if(StringUtils.equals("I", msgType)){
            //InfoPush內容個數為必入
            isErr = (infopushCount!=null
            //手機上訊息呈現的版型為必入
            && templateType != null && templateType.matches("1|2")
            //標題為必入
            && headline != null && StringUtils.isNotBlank(headline.toString())
            //第一組info-push圖檔為必入
            && iconFile1 != null && iconFile1.getFileObj() != null) ? Boolean.FALSE : Boolean.TRUE;
//          && iconFile1 != null && iconFile1.getFileCxt().length > 0) ? Boolean.FALSE : Boolean.TRUE;

            //通過初步檢核後,再做傳送多張圖片時的欄位檢核
            if(!isErr){
                //裝入各title內容之list
                List titles = new ArrayList();
                //iconFile與title比對之list,從第二張圖片開始檢核
                List iconFiles = getIconFileField(inputVO);

                JSONArray jsArr = headline.getJSONArray("title");

                int idx = 0;
                while(idx < jsArr.length()){
                    String title = jsArr.getString(idx);

                    //title有值
                    if(StringUtils.isNotBlank(title)) {
                        titles.add(title);
                    }
                    idx++;
                }

                isErr = (titles.size() == iconFiles.size()) ? Boolean.FALSE : Boolean.TRUE;
            }

            if(isErr){
                logger.error("[M+]InfoPush使用參數有誤");
            }
        }
    }

    /**
     * add iconFile field to list
     *
     * @param inputVO
     * @return
     */
    private List getIconFileField(MPlusInputVO inputVO) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Field[] fields = inputVO.getClass().getDeclaredFields();
        List list = new ArrayList();

        for(Field field : fields){
            String fieldName = field.getName();

            if(fieldName.matches("iconFile[0-9]")){
                String methodName = "get"+firstCharUpperCase(fieldName);

                Method method = inputVO.getClass().getMethod(methodName, new Class[]{});
                Object rtnObj = method.invoke(inputVO);

                if(rtnObj != null){
                    list.add(rtnObj);
                }
            }
        }
        return list;
    }

    /**
     * get password from db
     *
     * @return
     */
//    private String getMPlusPassword() throws JBranchException {
//        dam = getDataAccessManager();
//
//        String sql = new StringBuffer(
//            "select PARAM_NAME " +"" +
//            "  from TBSYSPARAMETER " +
//            " where PARAM_CODE = :PARAM_CODE " +
//            "   and PARAM_TYPE = :PARAM_TYPE "
//        ).toString();
//
//        QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//        condition.setObject("PARAM_CODE", "password");
//        condition.setObject("PARAM_TYPE", "MPLUS.API_PASSWORD");
//        condition.setQueryString(sql);
//
//        List<Map> list = dam.exeQuery(condition);
//
//        String password = null;
//        for(Map map : list){
//            password = (String) map.get("PARAM_NAME");
//        }
//
//        return password;
//    }

	protected Map<String , String> getMPlusAccPwd() throws JBranchException {
    	return new XmlInfo().doGetVariable("MPLUS.API_PASSWORD", FormatHelper.FORMAT_3);
    }

    /**
     * 依傳入MPlusInputVO傳入欄位去設定MultipartEntityBuilder
     *
     * @param inputVO
     * @return
     * @throws Exception
     */
    private MultipartEntityBuilder setMultipartEntity(MPlusInputVO inputVO) throws Exception {
        Field[] fields = inputVO.getClass().getDeclaredFields();
        MultipartEntityBuilder meb = MultipartEntityBuilder.create();

        for(Field field : fields){
            String fieldName = field.getName();
            String methodName = "get"+firstCharUpperCase(fieldName);

            Method method = inputVO.getClass().getMethod(methodName, new Class[]{});
            Object rtnObj = method.invoke(inputVO);

            Type type = method.getGenericReturnType();

            //skip field if it is null
            if(rtnObj!=null) {
                if (type.equals(String.class)) {
                    meb.addTextBody(fieldName, String.valueOf(rtnObj));
                } else if (type.equals(MPlusInputVO.BinaryFile.class)) {
                    MPlusInputVO.BinaryFile binaryFileObj = (MPlusInputVO.BinaryFile) rtnObj;
                    if ( binaryFileObj.getFileCxt() != null)
                    	meb.addBinaryBody(fieldName, binaryFileObj.getFileCxt(), ContentType.APPLICATION_OCTET_STREAM, binaryFileObj.getFileName());
                    else
                    	meb.addBinaryBody(fieldName, binaryFileObj.getFileObj(), ContentType.APPLICATION_OCTET_STREAM, binaryFileObj.getFileName());
                } else if(type.equals(JSONObject.class)){
                    //encrypt field
                    if(StringUtils.equals(fieldName, "text")){
                        meb.addTextBody(fieldName, encryptAES(rtnObj.toString() , inputVO.getSkey()));
                    }else {
                        meb.addTextBody(fieldName, rtnObj.toString(), ContentType.TEXT_PLAIN.withCharset(CharEncoding.UTF_8));
                    }
                }
            }
        }
        return meb;
    }

    /**
     * trans first case to upper case
     *
     * @param fieldName
     * @return
     */
    private String firstCharUpperCase(String fieldName) {
        char[] charArr = fieldName.toString().trim().toCharArray();
        charArr[0] = Character.toUpperCase(charArr[0]);

        String name = "";
        for(Character c : charArr){
            name += c;
        }

        return name;
    }

    /**
     * AES encrypt
     * @param password
     * @return
     * @throws Exception
     */
    public String encryptAES(String password , String skey) throws Exception {
        Cipher cipher = null;

        cipher = Cipher.getInstance(AES_ENCRYPT);
        SecretKeySpec key = new SecretKeySpec(Arrays.copyOf(skey.getBytes(CharEncoding.UTF_8), 16), AES_ENCRYPT);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] passwordBytes = password.getBytes(CharEncoding.UTF_8);

        return new String(Base64.encodeBase64(cipher.doFinal(passwordBytes)));
    }

    private void mPlusLog(HttpPost httpPost) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream((int)httpPost.getEntity().getContentLength());
        httpPost.getEntity().writeTo(out);
        String param = out.toString();

        logger.info("MPlus Http Req:\n"+param);

        out.close();
    }
}
