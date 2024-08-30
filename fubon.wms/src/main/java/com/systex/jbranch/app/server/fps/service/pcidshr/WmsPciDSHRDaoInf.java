package com.systex.jbranch.app.server.fps.service.pcidshr;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;

public interface WmsPciDSHRDaoInf {
	public Map<String , Object> validatePciData(GenericMap paramGmap) throws Exception;
}
