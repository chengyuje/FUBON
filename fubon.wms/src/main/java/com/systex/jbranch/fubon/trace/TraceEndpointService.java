package com.systex.jbranch.fubon.trace;

import com.systex.jbranch.app.common.fps.table.TBSYS_ENDPOINT_TRACEVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TraceEndpointService extends FubonWmsBizLogic {
    private Logger log = LoggerFactory.getLogger(TraceEndpointService.class);

    @Async
    public void save(TBSYS_ENDPOINT_TRACEVO tracevo) {
        try {
            getDataAccessManager().create(tracevo);
        } catch (JBranchException e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
        }
    }
}
