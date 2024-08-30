package com.systex.jbranch.ws.external.service.domain.insurance_status;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

import java.util.List;

public interface InsuranceStatusService {
    List<InsuranceStatusOutputVO> query(InsuranceStatusInputVO input) throws JBranchException;
}
