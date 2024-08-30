package com.systex.jbranch.platform.server;

import com.systex.jbranch.platform.common.dataManager.DataManagerIF;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

//可處理多檔案
//一律放暫存目錄
public class FileUpload extends HttpServlet {
// ------------------------------ FIELDS ------------------------------

    private static final String CONFIG_KEY_UPLOAD_FILEMAXSIZE = "uploadFileMaxSize";
    private static final String CONFIG_KEY_UPLOAD_FILESIZETHRESHOLD = "uploadFileSizeThresHold";

    private long fileMaxsize;
    private int fileSizehreshold;
    private String base;    //暫存目錄
	private Logger logger = LoggerFactory.getLogger(FileUpload.class);
    private DataAccessManager dam;



// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Servlet ---------------------

    public void init(ServletConfig sc) throws ServletException {
        try {
            base = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);

            DataManagerIF dataManagerIF = PlatformContext.getBean(DataManagerIF.class);
            Map defaultValue = dataManagerIF.getSystem().getDefaultValue();
            fileMaxsize = NumberUtils.toLong((String) defaultValue.get(CONFIG_KEY_UPLOAD_FILEMAXSIZE));
            fileSizehreshold = NumberUtils.toInt((String) defaultValue.get(CONFIG_KEY_UPLOAD_FILESIZETHRESHOLD));

            if (dam == null) {
                dam = (DataAccessManager) PlatformContext.getBean("dataAccessManager");
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

// -------------------------- OTHER METHODS --------------------------

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        try {
            if (isMultipart) {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                factory.setSizeThreshold(fileSizehreshold);
                factory.setRepository(new File(base));
                ServletFileUpload upload = new ServletFileUpload(factory);
                upload.setSizeMax(fileMaxsize);
                List items = upload.parseRequest(req);
                for (Object item1 : items) {
                    FileItem item = (FileItem) item1;
                    if (!item.isFormField()) {
                        String fieldName = item.getFieldName();

                        // 開啟檢核才需要檢查白名單附檔名
                        if (needCheck() && !isPass(fieldName)) {
                            throw new APException("請確認副檔名是否正確！");
                        }

                        File to = new File(base, fieldName);
                        item.write(to);
                        if (logger.isInfoEnabled()) {
                            logger.info("upload filename:" + fieldName);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServletException(e.getMessage());
        }
    }

    private boolean needCheck() throws JBranchException {
        QueryConditionIF cond = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        cond.setQueryString("select * from TBSYSPARAMETER where PARAM_TYPE = 'FILE_UPLOAD_CHECK' and PARAM_CODE = 'IS_OPEN' and PARAM_NAME = 'Y'");

        List<Map<String,String>> list = dam.exeQuery(cond);

        return !list.isEmpty();
    }

    private boolean isPass(String fileName) throws JBranchException {
        String extension = FilenameUtils.getExtension(fileName);
        String whitelist = getWhiteList();
        return whitelist.contains(extension);
    }

    private String getWhiteList() throws JBranchException {
        QueryConditionIF cond = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        cond.setQueryString("select PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = 'FILE_UPLOAD_CHECK' and PARAM_CODE = 'WHITE_LIST' ");

        List<Map<String,String>> list = dam.exeQuery(cond);
        return StringUtils.defaultString(list.get(0).get("PARAM_NAME"));
    }
}
