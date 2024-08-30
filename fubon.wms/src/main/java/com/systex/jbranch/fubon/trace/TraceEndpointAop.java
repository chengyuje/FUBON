package com.systex.jbranch.fubon.trace;

import com.systex.jbranch.app.common.fps.table.TBSYS_ENDPOINT_TRACEVO;
import com.systex.jbranch.platform.common.dataManager.UUID;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TraceEndpointAop {
    private Logger log = LoggerFactory.getLogger(TraceEndpointAop.class);

    @Autowired
    private TraceEndpointService traceEndpointService;

    public Object invoke(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Map basicInfo = (HashMap) args[0];
        Map targetInfo = (HashMap) args[1];

        UUID uuid = (UUID) basicInfo.get("UUID");
        if ("SCHEDULER".equals(uuid.getWsId())) // 排程先避開
            return joinPoint.proceed();
        else {
            TBSYS_ENDPOINT_TRACEVO tracevo = new TBSYS_ENDPOINT_TRACEVO();
            tracevo.setS_TIME(new Timestamp(System.currentTimeMillis()));
            try {
                return joinPoint.proceed();
            } finally {
                tracevo.setTELLERID(uuid.getTellerId());
                tracevo.setWSID(uuid.getWsId());
                tracevo.setBEAN(ObjectUtils.toString(targetInfo.get("bean.name"), "UNDEFINED"));
                tracevo.setMETHOD(ObjectUtils.toString(targetInfo.get("method"), "UNDEFINED"));
                tracevo.setE_TIME(new Timestamp(System.currentTimeMillis()));
                traceEndpointService.save(tracevo);
            }
        }
    }
}
