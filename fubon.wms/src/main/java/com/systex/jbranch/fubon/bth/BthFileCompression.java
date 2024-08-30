package com.systex.jbranch.fubon.bth;

import com.systex.jbranch.fubon.bth.ftp.BthFtpUtil;
import com.systex.jbranch.fubon.commons.FileCompression;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * Created by SebastianWu on 2016/12/29.
 */
@Repository
@Scope("prototype")
public class BthFileCompression extends BizLogic {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private DataAccessManager dam = null;
	private FileCompression fileCompression = null;

    public void execute(Object body, IPrimitiveMap<?> header) throws Exception {
        //取得傳入參數
        Map<String, Object> inputMap = (Map<String, Object>) body;
        Map<String, Object> jobParameter = (Map<String, Object>) inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);

        //排程使用參數
        String source = (String) jobParameter.get("source"); //解壓縮檔案位置(含檔名)
        String target = (String) jobParameter.get("target"); //解壓縮位置

		if (StringUtils.isBlank(source) && StringUtils.isBlank(target)) {
			throw new JBranchException("參數: source, target 為必入欄位");
		}
        
        BthFtpUtil ftpUtil = new BthFtpUtil();
        source = ftpUtil.getRealFileName(source);
        target = ftpUtil.getRealFileName(target);

        try {
            fileCompression = (FileCompression) PlatformContext.getBean("fileCompression");
            fileCompression.setInputFilePath(source);
            fileCompression.setOutputFileFolder(target);

            fileCompression.decompress();
        } catch (Exception e){
            throw new Exception(e);
        }
    }
}
