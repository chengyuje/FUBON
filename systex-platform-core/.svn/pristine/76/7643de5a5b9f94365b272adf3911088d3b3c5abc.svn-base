package com.systex.jbranch.platform.reportserver.service;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.jws.WebService;

import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSREPORTVO;
import com.systex.jbranch.platform.common.report.FlexReport;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.reportserver.ReportOperator;
import com.systex.jbranch.platform.reportserver.service.vo.ExchangeRequestVO;
import com.systex.jbranch.platform.reportserver.service.vo.ExchangeResponseVO;

@WebService(endpointInterface = "com.systex.jbranch.platform.reportserver.service.ReportService")
public class ReportServiceImpl implements ReportService {

	private Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);
	private static final String THREAD_POOL_EXECUTOR = "threadPoolExecutor";

	public ExchangeResponseVO generate(ExchangeRequestVO rqVO) throws JBranchException {
		DataAccessManager dam = new DataAccessManager();

		String remoteAddr = "test";
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        TBSYSREPORTVO reportVo = new TBSYSREPORTVO();
        reportVo.setREQUEST_HOST(remoteAddr);
        reportVo.setSTATUS(ReportOperator.RUNNING);
        reportVo.setCreator(remoteAddr);
        reportVo.setCreatetime(ts);
        reportVo.setModifier(remoteAddr);
        reportVo.setLastupdate(ts);
        dam.create(reportVo);
        
//        String extendName = "pdf";
//		if("birtPDFGenerator".equals(beanId)){
//			extendName = "pdf";
//		}else if("wordGenerator".equals(beanId)){
//			extendName = "doc";
//		}
        

        ReportOperator operator = new ReportOperator(rqVO);
        operator.setDamVo(dam, reportVo);

        ReportIF reportIF = null;
        ExchangeResponseVO rsVO = new ExchangeResponseVO();
        rsVO.setGenId(reportVo.getGEN_ID());
        if(rqVO.isAsync()){
        	ThreadPoolExecutor executor = (ThreadPoolExecutor) PlatformContext.getBean(THREAD_POOL_EXECUTOR);
        	executor.execute(operator);
        	return rsVO;
        }else{
        	operator.run();
        	reportIF = operator.getReport();
        }

        rsVO.setErrorMsg(operator.getErrorMsg());
        if(reportIF != null){
        	FlexReport flexReport=(FlexReport) reportIF;
    		ObjectUtil.copyProperties(rsVO, flexReport);
    		if(operator.getErrorMsg().equals("")){
            	rsVO.setStatus(ReportOperator.SUCCEEDED);
            }else{
            	rsVO.setStatus(ReportOperator.FAILED);
            }
    		
    		String location = flexReport.getLocation();
    		fillDataHandler(rsVO, location);
    	}
		return rsVO;
	}

	public ExchangeResponseVO queryReport(long genId) throws JBranchException {
		DataAccessManager dam = new DataAccessManager();
		TBSYSREPORTVO reportVO = (TBSYSREPORTVO) dam.findByPKey(TBSYSREPORTVO.TABLE_UID, genId);
		
		ExchangeResponseVO rsVO = new ExchangeResponseVO();
		rsVO.setGenId(genId);
		if(reportVO != null){
			String location = reportVO.getTEMP_PATH();
			rsVO.setLocation(location);
			rsVO.setSpeendingTime(reportVO.getGEN_TIME().doubleValue());
			rsVO.setEstablishTime(DateFormatUtils.format(new Date(reportVO.getCreatetime().getTime()), "yyyyMMddhhmmssSSS"));
			rsVO.setStatus(reportVO.getSTATUS());
			fillDataHandler(rsVO, location);
		}

		return rsVO;
	}
	
	private void fillDataHandler(ExchangeResponseVO rsVO, String location) {
		if(location != null){
			File file = new File(DataManager.getRealPath(), location);
			if(file.exists()){
				rsVO.setReportData(new DataHandler(new FileDataSource(file)));
			}
		}
	}
}
